package com.cms.module.user.service;

import com.cms.common.PageResult;
import com.cms.module.user.dto.UserDTO;
import com.cms.module.user.vo.UserVO;

import java.util.List;

public interface UserService {

    PageResult<UserVO> page(int page, int size, String keyword);

    UserVO getById(Long id);

    void create(UserDTO dto);

    void update(Long id, UserDTO dto);

    void delete(Long id);

    void updateRole(Long id, Long roleId);

    void updateStatus(Long id, Integer status);

    void batchDelete(List<Long> ids);

    void batchUpdateRole(List<Long> ids, Long roleId);

    void batchUpdateStatus(List<Long> ids, Integer status);

    void batchCreate(List<UserDTO> users);

    /**
     * 从Excel导入用户
     * @param users 解析后的用户列表
     * @return 导入成功的数量
     */
    int importFromExcel(List<UserDTO> users);
}
