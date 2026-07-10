package com.cms.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.PageResult;
import com.cms.module.user.dto.UserDTO;
import com.cms.module.user.entity.SysRole;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysRoleMapper;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.module.user.service.UserService;
import com.cms.module.user.vo.UserVO;
import com.cms.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<UserVO> page(int page, int size, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getRealName, keyword)
            );
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> p = new Page<>(page, size);
        Page<SysUser> result = userMapper.selectPage(p, wrapper);

        List<UserVO> records = result.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setRoleId(user.getRoleId());
            vo.setStatus(user.getStatus());
            vo.setCreateTime(user.getCreateTime());

            SysRole role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                vo.setRoleName(role.getRoleName());
            }

            return vo;
        }).collect(Collectors.toList());

        return PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), records);
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setRoleId(user.getRoleId());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());

        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role != null) {
            vo.setRoleName(role.getRoleName());
        }

        return vo;
    }

    @Override
    @Transactional
    public void create(UserDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, dto.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRoleId(dto.getRoleId());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void update(Long id, UserDTO dto) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // Check username uniqueness if changed
        if (!user.getUsername().equals(dto.getUsername())) {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, dto.getUsername());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new RuntimeException("用户名已存在");
            }
        }

        user.setUsername(dto.getUsername());
        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRoleId(dto.getRoleId());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : user.getStatus());

        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == 1L) {
            throw new RuntimeException("不能删除管理员账号");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (id.equals(currentUserId)) {
            throw new RuntimeException("不能删除自己");
        }

        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        userMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateRole(Long id, Long roleId) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setRoleId(roleId);
        userMapper.updateById(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (id == 1L) {
            throw new RuntimeException("不能修改管理员状态");
        }

        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    // ── Batch Operations ──

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择至少一个用户");
        }
        List<Long> distinctIds = ids.stream().distinct().collect(Collectors.toList());

        if (distinctIds.contains(1L)) {
            throw new BusinessException(400, "不能删除管理员账号");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (distinctIds.contains(currentUserId)) {
            throw new BusinessException(400, "不能删除自己的账号");
        }

        List<SysUser> existing = userMapper.selectBatchIds(distinctIds);
        if (existing.size() != distinctIds.size()) {
            Set<Long> existingIds = existing.stream().map(SysUser::getId).collect(Collectors.toSet());
            List<Long> missing = distinctIds.stream().filter(id -> !existingIds.contains(id)).collect(Collectors.toList());
            throw new BusinessException(400, "以下用户不存在: " + missing);
        }

        userMapper.deleteBatchIds(distinctIds);
    }

    @Override
    @Transactional
    public void batchUpdateRole(List<Long> ids, Long roleId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择至少一个用户");
        }
        List<Long> distinctIds = ids.stream().distinct().collect(Collectors.toList());

        if (distinctIds.contains(1L)) {
            throw new BusinessException(400, "不能修改管理员角色");
        }

        List<SysUser> existing = userMapper.selectBatchIds(distinctIds);
        if (existing.size() != distinctIds.size()) {
            Set<Long> existingIds = existing.stream().map(SysUser::getId).collect(Collectors.toSet());
            List<Long> missing = distinctIds.stream().filter(id -> !existingIds.contains(id)).collect(Collectors.toList());
            throw new BusinessException(400, "以下用户不存在: " + missing);
        }

        for (SysUser user : existing) {
            user.setRoleId(roleId);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请选择至少一个用户");
        }
        List<Long> distinctIds = ids.stream().distinct().collect(Collectors.toList());

        if (distinctIds.contains(1L)) {
            throw new BusinessException(400, "不能修改管理员状态");
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (status == 0 && distinctIds.contains(currentUserId)) {
            throw new BusinessException(400, "不能禁用自己的账号");
        }

        List<SysUser> existing = userMapper.selectBatchIds(distinctIds);
        if (existing.size() != distinctIds.size()) {
            Set<Long> existingIds = existing.stream().map(SysUser::getId).collect(Collectors.toSet());
            List<Long> missing = distinctIds.stream().filter(id -> !existingIds.contains(id)).collect(Collectors.toList());
            throw new BusinessException(400, "以下用户不存在: " + missing);
        }

        for (SysUser user : existing) {
            user.setStatus(status);
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional
    public void batchCreate(List<UserDTO> users) {
        if (users == null || users.isEmpty()) {
            throw new BusinessException(400, "用户列表不能为空");
        }
        for (int i = 0; i < users.size(); i++) {
            UserDTO dto = users.get(i);
            int row = i + 1;
            if (!StringUtils.hasText(dto.getUsername())) {
                throw new BusinessException(400, "第" + row + "行：用户名不能为空");
            }
            if (!StringUtils.hasText(dto.getPassword())) {
                throw new BusinessException(400, "第" + row + "行：密码不能为空");
            }
            if (dto.getRoleId() == null) {
                throw new BusinessException(400, "第" + row + "行：角色不能为空");
            }
            // Check username uniqueness
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, dto.getUsername());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "第" + row + "行：用户名[" + dto.getUsername() + "]已存在");
            }
            // Check for duplicate usernames within this batch
            for (int j = 0; j < i; j++) {
                if (users.get(j).getUsername().equals(dto.getUsername())) {
                    throw new BusinessException(400, "第" + row + "行：用户名[" + dto.getUsername() + "]与第" + (j + 1) + "行重复");
                }
            }
        }

        for (UserDTO dto : users) {
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRealName(dto.getRealName());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setRoleId(dto.getRoleId());
            user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            userMapper.insert(user);
        }
    }

    @Override
    @Transactional
    public int importFromExcel(List<UserDTO> users) {
        if (users == null || users.isEmpty()) {
            throw new BusinessException(400, "Excel中没有有效的用户数据");
        }

        // Validate each row
        for (int i = 0; i < users.size(); i++) {
            UserDTO dto = users.get(i);
            int row = i + 2; // Excel row number (header is row 1)
            if (!StringUtils.hasText(dto.getUsername())) {
                throw new BusinessException(400, "第" + row + "行：用户名不能为空");
            }
            if (!StringUtils.hasText(dto.getPassword())) {
                throw new BusinessException(400, "第" + row + "行：密码不能为空");
            }
            if (dto.getRoleId() == null) {
                throw new BusinessException(400, "第" + row + "行：角色不能为空或角色名称无法识别");
            }
            // Check username uniqueness against database
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, dto.getUsername());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "第" + row + "行：用户名[" + dto.getUsername() + "]已存在");
            }
            // Check for duplicate usernames within this import batch
            for (int j = 0; j < i; j++) {
                if (users.get(j).getUsername().equals(dto.getUsername())) {
                    throw new BusinessException(400, "第" + row + "行：用户名[" + dto.getUsername() + "]与第" + (j + 2) + "行重复");
                }
            }
        }

        int successCount = 0;
        for (UserDTO dto : users) {
            SysUser user = new SysUser();
            user.setUsername(dto.getUsername());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRealName(dto.getRealName());
            user.setPhone(dto.getPhone());
            user.setEmail(dto.getEmail());
            user.setRoleId(dto.getRoleId());
            user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
            userMapper.insert(user);
            successCount++;
        }
        return successCount;
    }
}
