package com.cms.module.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    @NotNull(message = "角色不能为空")
    private Long roleId;

    private Integer status = 1;
}
