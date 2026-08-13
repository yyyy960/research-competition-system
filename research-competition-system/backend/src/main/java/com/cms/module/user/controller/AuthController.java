package com.cms.module.user.controller;

import com.cms.common.Result;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysRoleMapper;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.security.JwtTokenProvider;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        log.info("Login attempt: username={}", username);

        SysUser user = userMapper.selectByUsername(username);
        if (user == null) {
            log.warn("Login failed: user '{}' not found", username);
            return Result.error(401, "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            log.warn("Login failed: user '{}' is disabled", username);
            return Result.error(401, "用户名或密码错误");
        }
        log.info("User found: id={}, storedPwd={}", user.getId(), user.getPassword());
        boolean pwdMatch = passwordEncoder.matches(password, user.getPassword());
        log.info("Password match result: {}", pwdMatch);
        if (!pwdMatch) {
            log.warn("Login failed: wrong password for user '{}'", username);
            return Result.error(401, "用户名或密码错误");
        }

        String roleName = roleMapper.selectById(user.getRoleId()).getRoleName();
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roleName);

        Map<String, Object> userInfo = Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "realName", user.getRealName() != null ? user.getRealName() : "",
            "role", roleName,
            "roleId", user.getRoleId(),
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "email", user.getEmail() != null ? user.getEmail() : ""
        );

        return Result.ok(Map.of("token", token, "userInfo", userInfo));
    }

    @GetMapping("/current-user")
    public Result<Map<String, Object>> currentUser() {
        if (!SecurityUtils.isAuthenticated()) {
            return Result.error(401, "未登录");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        String roleName = roleMapper.selectById(user.getRoleId()).getRoleName();

        Map<String, Object> userInfo = Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "realName", user.getRealName() != null ? user.getRealName() : "",
            "role", roleName,
            "roleId", user.getRoleId(),
            "phone", user.getPhone() != null ? user.getPhone() : "",
            "email", user.getEmail() != null ? user.getEmail() : ""
        );

        return Result.ok(userInfo);
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> data) {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);

        if (!passwordEncoder.matches(data.get("oldPassword"), user.getPassword())) {
            return Result.error("原密码错误");
        }

        user.setPassword(passwordEncoder.encode(data.get("newPassword")));
        userMapper.updateById(user);
        return Result.ok();
    }

}
