package com.cms.module.paper.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.paper.dto.PaperDTO;
import com.cms.module.paper.dto.PaperQueryDTO;
import com.cms.module.paper.service.PaperService;
import com.cms.module.paper.vo.PaperVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paper")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    @GetMapping("/page")
    public Result<PageResult<PaperVO>> page(PaperQueryDTO queryDTO) {
        return Result.ok(paperService.page(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<PaperVO> getDetail(@PathVariable Long id) {
        return Result.ok(paperService.getDetail(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody PaperDTO paperDTO) {
        return Result.ok(paperService.create(paperDTO));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PaperDTO paperDTO) {
        paperService.update(id, paperDTO);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paperService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        paperService.withdraw(id);
        return Result.ok();
    }
}
