package com.cms.module.paper.service;

import com.cms.common.PageResult;
import com.cms.module.paper.dto.PaperDTO;
import com.cms.module.paper.dto.PaperQueryDTO;
import com.cms.module.paper.vo.PaperVO;

public interface PaperService {
    PageResult<PaperVO> page(PaperQueryDTO queryDTO);
    PaperVO getDetail(Long id);
    Long create(PaperDTO paperDTO);
    void update(Long id, PaperDTO paperDTO);
    void delete(Long id);
    void withdraw(Long id);
}
