package com.cms.statistics;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.common.Result;
import com.cms.module.competition.mapper.CompetitionMapper;
import com.cms.module.copyright.mapper.CopyrightMapper;
import com.cms.module.innovation.mapper.InnovationProjectMapper;
import com.cms.module.paper.mapper.PaperMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final CompetitionMapper competitionMapper;
    private final InnovationProjectMapper innovationMapper;
    private final CopyrightMapper copyrightMapper;
    private final PaperMapper paperMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Map<String, Object> result = new LinkedHashMap<>();

        // Competition counts
        List<Map<String, Object>> compStats = queryStatusCounts(competitionMapper, "competition_achievement");
        long totalCompetitions = sumCounts(compStats);
        result.put("totalCompetitions", totalCompetitions);
        result.put("competitionStatusBreakdown", compStats);

        // Competition by category (A/B/C)
        result.put("competitionByCategory", queryGroupedStats(
                competitionMapper, "competition_achievement", "competition_category", null));

        // Competition by award level (国家级/省级/市级/校级/院级)
        result.put("competitionByLevel", queryGroupedStats(
                competitionMapper, "competition_achievement", "award_level", null));

        // Competition by award grade (一等奖/二等奖/三等奖)
        result.put("competitionByGrade", queryGroupedStats(
                competitionMapper, "competition_achievement", "award_grade", null));

        // Innovation counts
        List<Map<String, Object>> innovStats = queryStatusCounts(innovationMapper, "innovation_project");
        result.put("totalInnovations", sumCounts(innovStats));
        result.put("innovationStatusBreakdown", innovStats);

        // Innovation by level
        result.put("innovationByLevel", queryGroupedStats(
                innovationMapper, "innovation_project", "project_level", null));

        // Copyright counts
        List<Map<String, Object>> copyrightStats = queryStatusCounts(copyrightMapper, "software_copyright");
        result.put("totalCopyrights", sumCounts(copyrightStats));

        // Paper counts
        List<Map<String, Object>> paperStats = queryStatusCounts(paperMapper, "academic_paper");
        result.put("totalPapers", sumCounts(paperStats));

        // Paper by journal level
        result.put("paperByLevel", queryGroupedStats(
                paperMapper, "academic_paper", "journal_level", null));

        long grandTotal = totalCompetitions + sumCounts(innovStats) + sumCounts(copyrightStats) + sumCounts(paperStats);
        result.put("grandTotal", grandTotal);

        // ── Review status cross-type counts ──
        result.put("archivedCount", countByStatusAll("archived"));
        result.put("underReviewCount", countByStatusAll("under_review"));
        result.put("pendingCount", countByStatusAll("pending_review"));
        result.put("returnedCount", countByStatusAll("returned"));

        // ── Monthly trend data ──
        result.put("monthlyCompetition", queryMonthlyCompetition());
        result.put("monthlyPaper", queryMonthlyPaper());
        result.put("monthlyCopyright", queryMonthlyCopyright());
        result.put("monthlyInnovation", queryMonthlyInnovation());

        return Result.ok(result);
    }

    /** Count records with a given status across all 4 achievement tables */
    private long countByStatusAll(String status) {
        long count = 0;
        count += competitionMapper.selectCount(new QueryWrapper<com.cms.module.competition.entity.Competition>().eq("status", status));
        count += innovationMapper.selectCount(new QueryWrapper<com.cms.module.innovation.entity.InnovationProject>().eq("status", status));
        count += copyrightMapper.selectCount(new QueryWrapper<com.cms.module.copyright.entity.SoftwareCopyright>().eq("status", status));
        count += paperMapper.selectCount(new QueryWrapper<com.cms.module.paper.entity.AcademicPaper>().eq("status", status));
        return count;
    }

    private List<Integer> queryMonthlyCompetition() {
        int[] m = new int[12];
        competitionMapper.selectMaps(new QueryWrapper<com.cms.module.competition.entity.Competition>()
                .select("MONTH(award_time) as mo, COUNT(*) as ct")
                .isNotNull("award_time").groupBy("mo"))
                .forEach(row -> incMonth(m, row));
        return toList(m);
    }
    private List<Integer> queryMonthlyPaper() {
        int[] m = new int[12];
        paperMapper.selectMaps(new QueryWrapper<com.cms.module.paper.entity.AcademicPaper>()
                .select("MONTH(create_time) as mo, COUNT(*) as ct")
                .isNotNull("create_time").groupBy("mo"))
                .forEach(row -> incMonth(m, row));
        return toList(m);
    }
    private List<Integer> queryMonthlyCopyright() {
        int[] m = new int[12];
        copyrightMapper.selectMaps(new QueryWrapper<com.cms.module.copyright.entity.SoftwareCopyright>()
                .select("MONTH(create_time) as mo, COUNT(*) as ct")
                .isNotNull("create_time").groupBy("mo"))
                .forEach(row -> incMonth(m, row));
        return toList(m);
    }
    private List<Integer> queryMonthlyInnovation() {
        int[] m = new int[12];
        innovationMapper.selectMaps(new QueryWrapper<com.cms.module.innovation.entity.InnovationProject>()
                .select("MONTH(create_time) as mo, COUNT(*) as ct")
                .isNotNull("create_time").groupBy("mo"))
                .forEach(row -> incMonth(m, row));
        return toList(m);
    }
    private void incMonth(int[] months, Map<String, Object> row) {
        Object mo = row.get("mo"), ct = row.get("ct");
        if (mo != null && ct != null) {
            int idx = ((Number) mo).intValue() - 1;
            if (idx >= 0 && idx < 12) months[idx] = ((Number) ct).intValue();
        }
    }
    private List<Integer> toList(int[] arr) {
        List<Integer> r = new ArrayList<>();
        for (int v : arr) r.add(v);
        return r;
    }

    @GetMapping("/competition")
    public Result<Map<String, Object>> competitionStats(@RequestParam(required = false) Integer year) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Group by category
        result.put("categoryStats", queryGroupedStats(
                competitionMapper, "competition_achievement", "competition_category", year));

        // Group by award level
        result.put("awardLevelStats", queryGroupedStats(
                competitionMapper, "competition_achievement", "award_level", year));

        // Group by award grade
        result.put("awardGradeStats", queryGroupedStats(
                competitionMapper, "competition_achievement", "award_grade", year));

        return Result.ok(result);
    }

    @GetMapping("/innovation")
    public Result<Map<String, Object>> innovationStats(@RequestParam(required = false) Integer year) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Group by project level
        result.put("levelStats", queryGroupedStats(
                innovationMapper, "innovation_project", "project_level", year));

        // Group by project type
        result.put("typeStats", queryGroupedStats(
                innovationMapper, "innovation_project", "project_type", year));

        return Result.ok(result);
    }

    @GetMapping("/copyright")
    public Result<Map<String, Object>> copyrightStats(@RequestParam(required = false) Integer year) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Count by year
        result.put("yearStats", queryYearlyCounts(copyrightMapper, "software_copyright", year));

        return Result.ok(result);
    }

    @GetMapping("/paper")
    public Result<Map<String, Object>> paperStats(@RequestParam(required = false) Integer year) {
        Map<String, Object> result = new LinkedHashMap<>();

        // Group by journal level
        result.put("journalLevelStats", queryGroupedStats(
                paperMapper, "academic_paper", "journal_level", year));

        return Result.ok(result);
    }

    // ---- Helper methods ----

    /**
     * Query status counts grouped by status field.
     */
    private <T> List<Map<String, Object>> queryStatusCounts(BaseMapper<T> mapper, String tableName) {
        List<Map<String, Object>> raw = mapper.selectMaps(new QueryWrapper<T>()
                .select("status, COUNT(*) as value")
                .groupBy("status"));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row.get("status"));
            item.put("value", row.get("value"));
            result.add(item);
        }
        return result;
    }

    /**
     * Query counts grouped by a specific column, with optional year filter.
     */
    private <T> List<Map<String, Object>> queryGroupedStats(BaseMapper<T> mapper, String tableName,
                                                             String groupColumn, Integer year) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        if (year != null) {
            qw.apply("YEAR(create_time) = {0}", year);
        }
        qw.select(groupColumn + " as label, COUNT(*) as value");
        qw.groupBy(groupColumn);
        qw.isNotNull(groupColumn);

        List<Map<String, Object>> raw = mapper.selectMaps(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row.get("label"));
            item.put("value", row.get("value"));
            result.add(item);
        }
        return result;
    }

    /**
     * Query yearly counts, with optional year filter (returns single year if specified).
     */
    private <T> List<Map<String, Object>> queryYearlyCounts(BaseMapper<T> mapper, String tableName,
                                                             Integer year) {
        QueryWrapper<T> qw = new QueryWrapper<>();
        if (year != null) {
            qw.apply("YEAR(create_time) = {0}", year);
        }
        qw.select("YEAR(create_time) as label, COUNT(*) as value");
        qw.groupBy("label");
        qw.orderByAsc("label");

        List<Map<String, Object>> raw = mapper.selectMaps(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : raw) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", String.valueOf(row.get("label")));
            item.put("value", row.get("value"));
            result.add(item);
        }
        return result;
    }

    private long sumCounts(List<Map<String, Object>> statusList) {
        return statusList.stream()
                .mapToLong(m -> ((Number) m.get("value")).longValue())
                .sum();
    }
}
