package com.cms.module.competition.service;

import com.cms.common.PageResult;
import com.cms.module.competition.dto.CompetitionDTO;
import com.cms.module.competition.dto.CompetitionQueryDTO;
import com.cms.module.competition.vo.CompetitionVO;

public interface CompetitionService {

    PageResult<CompetitionVO> page(CompetitionQueryDTO query);

    CompetitionVO getDetail(Long id);

    CompetitionVO create(CompetitionDTO dto);

    CompetitionVO update(Long id, CompetitionDTO dto);

    void delete(Long id);
    void withdraw(Long id);
}
