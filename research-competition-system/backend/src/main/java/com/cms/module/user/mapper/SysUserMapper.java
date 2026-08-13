package com.cms.module.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.module.user.entity.SysUser;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser selectByUsername(String username);

    @Select("SELECT u.* FROM sys_user u INNER JOIN sys_role r ON u.role_id = r.id WHERE r.role_name = #{roleName}")
    List<SysUser> selectByRoleName(String roleName);
}
