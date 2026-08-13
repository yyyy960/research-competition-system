package com.cms.module.innovation.controller;

import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.innovation.dto.InnovationDTO;
import com.cms.module.innovation.dto.InnovationQueryDTO;
import com.cms.module.innovation.service.InnovationService;
import com.cms.module.innovation.vo.InnovationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/innovation")
@RequiredArgsConstructor
@Validated
public class InnovationController {

    private final InnovationService service;

    @GetMapping("/page")
    public Result<PageResult<InnovationVO>> page(@Valid InnovationQueryDTO query) {
        PageResult<InnovationVO> result = service.page(query);
        return Result.ok(result);
    }

    @GetMapping("/{id}")
    public Result<InnovationVO> detail(@PathVariable Long id) {
        InnovationVO vo = service.getDetail(id);
        return Result.ok(vo);
    }

    @PostMapping
    public Result<InnovationVO> create(@Valid @RequestBody InnovationDTO dto) {
        InnovationVO vo = service.create(dto);
        return Result.ok(vo);
    }

    @PutMapping("/{id}")
    public Result<InnovationVO> update(@PathVariable Long id, @Valid @RequestBody InnovationDTO dto) {
        InnovationVO vo = service.update(id, dto);
        return Result.ok(vo);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    @PutMapping("/{id}/withdraw")
    public Result<Void> withdraw(@PathVariable Long id) {
        service.withdraw(id);
        return Result.ok();
    }
}
