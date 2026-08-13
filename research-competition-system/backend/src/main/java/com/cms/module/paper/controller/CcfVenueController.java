package com.cms.module.paper.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.PageResult;
import com.cms.common.Result;
import com.cms.module.paper.entity.CcfVenue;
import com.cms.module.paper.mapper.CcfVenueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ccf")
@RequiredArgsConstructor
public class CcfVenueController {

    private final CcfVenueMapper ccfVenueMapper;

    @GetMapping("/page")
    public Result<PageResult<CcfVenue>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String venueType,
            @RequestParam(required = false) String area,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<CcfVenue> wrapper = new LambdaQueryWrapper<>();
        if (venueType != null && !venueType.isEmpty()) {
            wrapper.eq(CcfVenue::getVenueType, venueType);
        }
        if (area != null && !area.isEmpty()) {
            wrapper.eq(CcfVenue::getArea, area);
        }
        if (level != null && !level.isEmpty()) {
            wrapper.eq(CcfVenue::getLevel, level);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(CcfVenue::getFullName, keyword)
                    .or().like(CcfVenue::getAbbreviation, keyword));
        }
        wrapper.orderByAsc(CcfVenue::getVenueType, CcfVenue::getArea, CcfVenue::getLevel);

        Page<CcfVenue> result = ccfVenueMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.ok(PageResult.of(result.getTotal(), result.getCurrent(), result.getSize(), result.getRecords()));
    }

    @GetMapping("/areas")
    public Result<List<String>> getAreas() {
        List<String> areas = ccfVenueMapper.selectList(null).stream()
                .map(CcfVenue::getArea)
                .distinct()
                .sorted()
                .toList();
        return Result.ok(areas);
    }

    @GetMapping("/levels")
    public Result<List<String>> getLevels() {
        return Result.ok(List.of("A", "B", "C"));
    }

    @GetMapping("/match")
    public Result<CcfVenue> matchCcf(@RequestParam String name) {
        LambdaQueryWrapper<CcfVenue> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(CcfVenue::getFullName, name)
               .or()
               .eq(CcfVenue::getAbbreviation, name.toUpperCase())
               .orderByAsc(CcfVenue::getLevel)
               .last("LIMIT 1");
        CcfVenue venue = ccfVenueMapper.selectOne(wrapper);
        if (venue != null) {
            return Result.ok(venue);
        }
        return Result.ok(null);
    }
}
