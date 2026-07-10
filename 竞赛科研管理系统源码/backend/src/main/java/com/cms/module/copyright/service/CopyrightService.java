package com.cms.module.copyright.service;

import com.cms.common.PageResult;
import com.cms.module.copyright.dto.CopyrightDTO;
import com.cms.module.copyright.dto.CopyrightQueryDTO;
import com.cms.module.copyright.vo.CopyrightVO;

public interface CopyrightService {
    PageResult<CopyrightVO> page(CopyrightQueryDTO queryDTO);
    CopyrightVO getDetail(Long id);
    Long create(CopyrightDTO copyrightDTO);
    void update(Long id, CopyrightDTO copyrightDTO);
    void delete(Long id);
    void withdraw(Long id);
}
