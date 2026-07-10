package com.cms.module.innovation.service;

import com.cms.common.PageResult;
import com.cms.module.innovation.dto.InnovationDTO;
import com.cms.module.innovation.dto.InnovationQueryDTO;
import com.cms.module.innovation.vo.InnovationVO;

public interface InnovationService {
    PageResult<InnovationVO> page(InnovationQueryDTO query);

    InnovationVO getDetail(Long id);

    InnovationVO create(InnovationDTO dto);

    InnovationVO update(Long id, InnovationDTO dto);

    void delete(Long id);
    void withdraw(Long id);
}
