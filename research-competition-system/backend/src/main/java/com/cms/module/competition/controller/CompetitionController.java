package com.cms.module.competition.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.competition.dto.CompetitionDTO;
import com.cms.module.competition.dto.CompetitionQueryDTO;
import com.cms.module.competition.service.CompetitionService;
import com.cms.module.competition.vo.CompetitionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/competition")
@RequiredArgsConstructor
public class CompetitionController {

    private final CompetitionService competitionService;

    @GetMapping("/page")
    public Result<PageResult<CompetitionVO>> page(CompetitionQueryDTO query) {
        PageResult<CompetitionVO> pageResult = competitionService.page(query);
        return Result.ok(pageResult);
    }

    @GetMapping("/{id}")
    public Result<CompetitionVO> detail(@PathVariable Long id) {
        CompetitionVO vo = competitionService.getDetail(id);
        return Result.ok(vo);
    }

    @PostMapping
    public Result<CompetitionVO> create(@Valid @RequestBody CompetitionDTO dto) {
        CompetitionVO vo = competitionService.create(dto);
        return Result.ok(vo);
    }

    @PutMapping("/{id}")
    public Result<CompetitionVO> update(@PathVariable Long id, @Valid @RequestBody CompetitionDTO dto) {
        CompetitionVO vo = competitionService.update(id, dto);
        return Result.ok(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        competitionService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        competitionService.withdraw(id);
        return Result.ok();
    }
}
