package com.cms.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.config.SysLog;
import com.cms.config.SysLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
public class LogController {

    private final SysLogMapper sysLogMapper;

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<SysLog>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        if (action != null && !action.isEmpty()) {
            wrapper.eq(SysLog::getAction, action);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysLog::getUsername, keyword)
                    .or().like(SysLog::getOperation, keyword));
        }
        wrapper.orderByDesc(SysLog::getCreateTime);
        Page<SysLog> result = sysLogMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }
}
