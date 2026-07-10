package com.cms.module.user.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.user.dto.UserDTO;
import com.cms.module.user.dto.UserExcelDTO;
import com.cms.module.user.entity.SysRole;
import com.cms.module.user.mapper.SysRoleMapper;
import com.cms.module.user.service.UserService;
import com.cms.module.user.vo.UserVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SysRoleMapper roleMapper;

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<UserVO>> page(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           @RequestParam(required = false) String keyword) {
        return Result.ok(userService.page(page, size, keyword));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        userService.create(dto);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        userService.update(id, dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Long> data) {
        userService.updateRole(id, data.get("roleId"));
        return Result.ok();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> data) {
        userService.updateStatus(id, data.get("status"));
        return Result.ok();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SysRole>> getAllRoles() {
        return Result.ok(roleMapper.selectList(null));
    }

    // ── Batch Operations ──

    @PostMapping("/batch-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        userService.batchDelete(ids);
        return Result.ok();
    }

    @PostMapping("/batch-create")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchCreate(@RequestBody List<UserDTO> users) {
        userService.batchCreate(users);
        return Result.ok();
    }

    @PutMapping("/batch-role")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchUpdateRole(@RequestBody Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Object> rawIds = (List<Object>) data.get("ids");
        List<Long> ids = rawIds.stream().map(item -> ((Number) item).longValue()).collect(Collectors.toList());
        Long roleId = ((Number) data.get("roleId")).longValue();
        userService.batchUpdateRole(ids, roleId);
        return Result.ok();
    }

    @PutMapping("/batch-status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchUpdateStatus(@RequestBody Map<String, Object> data) {
        @SuppressWarnings("unchecked")
        List<Object> rawIds = (List<Object>) data.get("ids");
        List<Long> ids = rawIds.stream().map(item -> ((Number) item).longValue()).collect(Collectors.toList());
        Integer status = ((Number) data.get("status")).intValue();
        userService.batchUpdateStatus(ids, status);
        return Result.ok();
    }

    /**
     * Excel导入用户 — 上传并解析Excel文件，返回预览数据
     */
    @PostMapping("/import-excel")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> importExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error(400, "请选择文件");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error(400, "仅支持 .xlsx 或 .xls 格式的Excel文件");
        }

        // 1. Parse Excel to UserExcelDTO list using try-with-resources
        List<UserExcelDTO> excelList = new ArrayList<>();
        try (ExcelReader excelReader = EasyExcel.read(file.getInputStream(), UserExcelDTO.class,
                new AnalysisEventListener<UserExcelDTO>() {
                    @Override
                    public void invoke(UserExcelDTO data, AnalysisContext context) {
                        excelList.add(data);
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        // Done
                    }

                    @Override
                    public void onException(Exception exception, AnalysisContext context) throws Exception {
                        log.error("Excel第{}行解析异常: {}", context.readRowHolder().getRowIndex(), exception.getMessage());
                        throw exception;
                    }
                }).build()) {
            ReadSheet sheet = EasyExcel.readSheet(0).build();
            excelReader.read(sheet);
        } catch (Exception e) {
            log.error("Excel解析失败", e);
            return Result.error(400, "Excel文件解析失败：" + e.getMessage());
        }

        if (excelList.isEmpty()) {
            return Result.error(400, "Excel文件中没有数据，请至少填写一行用户信息");
        }

        // 2. Build role name -> role id mapping
        List<SysRole> roles = roleMapper.selectList(null);
        Map<String, Long> roleNameMap = new HashMap<>();
        for (SysRole role : roles) {
            roleNameMap.put(role.getRoleName().trim(), role.getId());
        }

        // 3. Convert to UserDTO list with validation errors collected
        List<UserDTO> validUsers = new ArrayList<>();
        List<Map<String, Object>> errorRows = new ArrayList<>();

        for (int i = 0; i < excelList.size(); i++) {
            UserExcelDTO row = excelList.get(i);
            int excelRow = i + 2; // header is row 1
            List<String> errors = new ArrayList<>();

            if (row.getUsername() == null || row.getUsername().trim().isEmpty()) {
                errors.add("用户名不能为空");
            }
            if (row.getPassword() == null || row.getPassword().trim().isEmpty()) {
                errors.add("密码不能为空");
            }
            if (row.getRoleName() == null || row.getRoleName().trim().isEmpty()) {
                errors.add("角色不能为空");
            }

            String roleName = row.getRoleName() != null ? row.getRoleName().trim() : "";
            Long roleId = roleNameMap.get(roleName);
            if (!roleName.isEmpty() && roleId == null) {
                errors.add("角色名称[" + roleName + "]无法识别，可选角色：" + roleNameMap.keySet());
            }

            if (!errors.isEmpty()) {
                Map<String, Object> err = new LinkedHashMap<>();
                err.put("row", excelRow);
                err.put("username", row.getUsername());
                err.put("realName", row.getRealName());
                err.put("roleName", row.getRoleName());
                err.put("errors", errors);
                errorRows.add(err);
            } else {
                UserDTO dto = new UserDTO();
                dto.setUsername(row.getUsername().trim());
                dto.setPassword(row.getPassword().trim());
                dto.setRealName(row.getRealName() != null ? row.getRealName().trim() : null);
                dto.setPhone(row.getPhone() != null ? row.getPhone().trim() : null);
                dto.setEmail(row.getEmail() != null ? row.getEmail().trim() : null);
                dto.setRoleId(roleId);
                dto.setStatus(1);
                validUsers.add(dto);
            }
        }

        // 4. Return preview data
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", excelList.size());
        result.put("validCount", validUsers.size());
        result.put("errorCount", errorRows.size());
        result.put("validUsers", validUsers);
        result.put("errorRows", errorRows);
        result.put("filename", filename);

        return Result.ok(result);
    }

    /**
     * 确认导入 — 将预览校验通过的用户批量写入数据库
     */
    @PostMapping("/import-confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> importConfirm(@RequestBody List<UserDTO> users) {
        int count = userService.importFromExcel(users);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("importedCount", count);
        return Result.ok(result);
    }

    /**
     * 下载Excel导入模板
     */
    @GetMapping("/import-template")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String filename = URLEncoder.encode("用户导入模板.xlsx", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        // Prepare example data
        List<UserExcelDTO> exampleData = new ArrayList<>();
        UserExcelDTO example = new UserExcelDTO();
        example.setUsername("zhangsan");
        example.setPassword("123456");
        example.setRealName("张三");
        example.setPhone("13800138000");
        example.setEmail("zhangsan@example.com");
        example.setRoleName("学生/教师");
        exampleData.add(example);

        // Write template with try-with-resources
        try (ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), UserExcelDTO.class)
                .excelType(ExcelTypeEnum.XLSX)
                .build()) {
            WriteSheet sheet = EasyExcel.writerSheet("用户导入").build();
            excelWriter.write(exampleData, sheet);
        }
    }
}
