package com.cms.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.user.entity.Announcement;
import com.cms.module.user.mapper.AnnouncementMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/announcement")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementMapper announcementMapper;

    @GetMapping("/page")
    public Result<PageResult<Announcement>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getIsTop).orderByDesc(Announcement::getCreateTime);
        Page<Announcement> result = announcementMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<Announcement> detail(@PathVariable Long id) {
        return Result.ok(announcementMapper.selectById(id));
    }

    @GetMapping("/latest")
    public Result<Announcement> latest() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Announcement::getIsTop).orderByDesc(Announcement::getCreateTime).last("LIMIT 1");
        return Result.ok(announcementMapper.selectOne(wrapper));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> create(@RequestBody Announcement announcement) {
        announcementMapper.insert(announcement);
        return Result.ok();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        announcementMapper.updateById(announcement);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.ok();
    }
}
