package com.cms.module.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Excel导入用户数据DTO
 */
@Data
public class UserExcelDTO {

    @ExcelProperty(value = "用户名", index = 0)
    private String username;

    @ExcelProperty(value = "密码", index = 1)
    private String password;

    @ExcelProperty(value = "姓名", index = 2)
    private String realName;

    @ExcelProperty(value = "手机号", index = 3)
    private String phone;

    @ExcelProperty(value = "邮箱", index = 4)
    private String email;

    @ExcelProperty(value = "角色", index = 5)
    private String roleName;
}
