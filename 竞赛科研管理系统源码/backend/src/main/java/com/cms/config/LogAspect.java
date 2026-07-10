package com.cms.config;

import com.cms.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysLogMapper sysLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Around("execution(* com.cms.module..controller.*.*(..)) || execution(* com.cms.statistics.*.*(..)) || execution(* com.cms.ocr.*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String status = "OK";
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = "FAIL";
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            try { saveLog(joinPoint, status, duration); } catch (Exception ignored) {}
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, String status, long duration) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            String methodName = method.getName();
            String className = signature.getDeclaringType().getSimpleName();

            // Determine action type
            String action = determineAction(methodName, className);
            if (action == null) return; // Skip methods that don't need logging

            // Get username
            String username = "unknown";
            String realName = "";
            try { username = SecurityUtils.getCurrentUsername(); } catch (Exception ignored) {}
            try {
                Long uid = SecurityUtils.getCurrentUserId();
                // We don't have direct access to userMapper here, so skip realName
            } catch (Exception ignored) {}

            // Get IP
            String ip = "";
            try {
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpServletRequest request = attrs.getRequest();
                    ip = request.getHeader("X-Forwarded-For");
                    if (ip == null || ip.isEmpty()) ip = request.getRemoteAddr();
                }
            } catch (Exception ignored) {}

            // Get params (limited size)
            String params = "";
            try {
                Object[] args = joinPoint.getArgs();
                if (args != null && args.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Object arg : args) {
                        if (arg instanceof jakarta.servlet.http.HttpServletResponse) continue;
                        if (arg instanceof org.springframework.web.multipart.MultipartFile) {
                            sb.append("[FILE]");
                        } else {
                            try {
                                String json = objectMapper.writeValueAsString(arg);
                                if (json.length() > 200) json = json.substring(0, 200) + "...";
                                sb.append(json);
                            } catch (Exception e) {
                                sb.append(arg.getClass().getSimpleName());
                            }
                        }
                        sb.append("; ");
                    }
                    params = sb.toString();
                    if (params.length() > 500) params = params.substring(0, 500);
                }
            } catch (Exception ignored) {}

            // Build operation description
            String module = getModule(className);
            String operation = action + " - " + module;
            if (methodName.contains("page")) operation = "查询" + module + "列表";
            else if (methodName.contains("detail") || methodName.startsWith("get")) operation = "查看" + module + "详情";

            SysLog sysLog = new SysLog();
            sysLog.setUsername(username);
            sysLog.setRealName(realName);
            sysLog.setOperation(operation);
            sysLog.setModule(module);
            sysLog.setAction(action);
            sysLog.setParams(params);
            sysLog.setIp(ip);
            sysLog.setStatus(status);
            sysLogMapper.insert(sysLog);
        } catch (Exception e) {
            log.warn("Failed to save operation log: {}", e.getMessage());
        }
    }

    private String determineAction(String methodName, String className) {
        if (methodName.equals("login")) return "LOGIN";
        if (methodName.equals("logout")) return "LOGOUT";
        if (methodName.startsWith("create") || methodName.startsWith("add")) return "SUBMIT";
        if (methodName.startsWith("update") || methodName.startsWith("edit")) return "UPDATE";
        if (methodName.startsWith("delete") || methodName.startsWith("remove")) return "DELETE";
        if (methodName.startsWith("approve") || methodName.startsWith("reject")) return "REVIEW";
        if (methodName.startsWith("page") || methodName.startsWith("list") || methodName.startsWith("get") || methodName.startsWith("detail")) return "QUERY";
        if (methodName.startsWith("upload")) return "UPLOAD";
        if (methodName.startsWith("download") || methodName.startsWith("export")) return "EXPORT";
        if (methodName.equals("withdraw")) return "WITHDRAW";
        return null; // skip unclassified
    }

    private String getModule(String className) {
        if (className.contains("Competition")) return "学科竞赛";
        if (className.contains("Innovation")) return "大创项目";
        if (className.contains("Copyright")) return "软件著作权";
        if (className.contains("Paper")) return "学术论文";
        if (className.contains("Review")) return "审核管理";
        if (className.contains("File")) return "文件管理";
        if (className.contains("User")) return "用户管理";
        if (className.contains("Auth")) return "登录认证";
        if (className.contains("Notification")) return "消息通知";
        if (className.contains("Announcement")) return "系统公告";
        if (className.contains("Personal")) return "个人成果";
        if (className.contains("Statistics")) return "数据统计";
        if (className.contains("Duplicate")) return "查重校验";
        if (className.contains("Ccf")) return "CCF目录";
        if (className.contains("Ocr")) return "OCR识别";
        return className;
    }
}
