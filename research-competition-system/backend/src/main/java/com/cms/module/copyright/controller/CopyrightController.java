package com.cms.module.copyright.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.copyright.dto.CopyrightDTO;
import com.cms.module.copyright.dto.CopyrightQueryDTO;
import com.cms.module.copyright.service.CopyrightService;
import com.cms.module.copyright.vo.CopyrightVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/copyright")
@RequiredArgsConstructor
public class CopyrightController {

    private final CopyrightService copyrightService;

    @GetMapping("/page")
    public Result<PageResult<CopyrightVO>> page(CopyrightQueryDTO queryDTO) {
        return Result.ok(copyrightService.page(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<CopyrightVO> getDetail(@PathVariable Long id) {
        return Result.ok(copyrightService.getDetail(id));
    }

    @PostMapping
    public Result<Long> create(@Valid @RequestBody CopyrightDTO copyrightDTO) {
        return Result.ok(copyrightService.create(copyrightDTO));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CopyrightDTO copyrightDTO) {
        copyrightService.update(id, copyrightDTO);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        copyrightService.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        copyrightService.withdraw(id);
        return Result.ok();
    }
}
