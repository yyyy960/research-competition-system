package com.cms.module.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cms.common.BusinessException;
import com.cms.common.Result;
import com.cms.module.competition.entity.Competition;
import com.cms.module.competition.mapper.CompetitionMapper;
import com.cms.module.copyright.entity.SoftwareCopyright;
import com.cms.module.copyright.mapper.CopyrightMapper;
import com.cms.module.innovation.entity.InnovationProject;
import com.cms.module.innovation.mapper.InnovationProjectMapper;
import com.cms.module.paper.entity.AcademicPaper;
import com.cms.module.paper.mapper.PaperMapper;
import com.cms.module.user.entity.SysUser;
import com.cms.module.user.mapper.SysUserMapper;
import com.cms.security.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/personal")
@RequiredArgsConstructor
public class PersonalCenterController {

    private final CompetitionMapper competitionMapper;
    private final InnovationProjectMapper innovationMapper;
    private final CopyrightMapper copyrightMapper;
    private final PaperMapper paperMapper;
    private final SysUserMapper userMapper;

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        Long userId = SecurityUtils.getCurrentUserId();

        // Competition stats
        List<Competition> competitions = competitionMapper.selectList(
                new LambdaQueryWrapper<Competition>().eq(Competition::getSubmitUserId, userId));
        Map<String, Object> competitionStats = buildTypeStats(competitions, "competition");

        // Innovation stats
        List<InnovationProject> innovations = innovationMapper.selectList(
                new LambdaQueryWrapper<InnovationProject>().eq(InnovationProject::getSubmitUserId, userId));
        Map<String, Object> innovationStats = buildTypeStats(innovations, "innovation");

        // Copyright stats
        List<SoftwareCopyright> copyrights = copyrightMapper.selectList(
                new LambdaQueryWrapper<SoftwareCopyright>().eq(SoftwareCopyright::getSubmitUserId, userId));
        Map<String, Object> copyrightStats = buildTypeStats(copyrights, "copyright");

        // Paper stats
        List<AcademicPaper> papers = paperMapper.selectList(
                new LambdaQueryWrapper<AcademicPaper>().eq(AcademicPaper::getSubmitUserId, userId));
        Map<String, Object> paperStats = buildTypeStats(papers, "paper");

        long grandTotal = competitions.size() + innovations.size() + copyrights.size() + papers.size();

        Map<String, Object> result = new LinkedHashMap<>();

        // ── Flat totals (same shape as system /statistics/overview) ──
        result.put("totalCompetitions", competitions.size());
        result.put("totalInnovations", innovations.size());
        result.put("totalCopyrights", copyrights.size());
        result.put("totalPapers", papers.size());
        result.put("grandTotal", grandTotal);

        // ── Review status counts (cross-type aggregate) ──
        result.put("archivedCount", countByStatus(competitions, innovations, copyrights, papers, "archived"));
        result.put("underReviewCount", countByStatus(competitions, innovations, copyrights, papers, "under_review"));
        result.put("pendingCount", countByStatus(competitions, innovations, copyrights, papers, "pending_review"));
        result.put("returnedCount", countByStatus(competitions, innovations, copyrights, papers, "returned"));

        // ── Competition charts ──
        result.put("competitionByCategory", buildCategoryDistribution(competitions));
        result.put("competitionByGrade", buildGradeDistribution(competitions));
        result.put("competitionByLevel", buildLevelDistribution(competitions));

        // ── Monthly trends ──
        result.put("monthlyCompetition", buildMonthlyTrend(competitions, "awardTime"));
        result.put("monthlyPaper", buildMonthlyTrend(papers, "createTime"));
        result.put("monthlyCopyright", buildMonthlyTrend(copyrights, "createTime"));

        // ── Nested detail (for PersonalAchievement page compatibility) ──
        result.put("competitions", competitionStats);
        result.put("innovations", innovationStats);
        result.put("copyrights", copyrightStats);
        result.put("papers", paperStats);
        result.put("achievementDistribution", List.of(
                Map.of("name", "学科竞赛", "value", competitions.size()),
                Map.of("name", "大创项目", "value", innovations.size()),
                Map.of("name", "软件著作权", "value", copyrights.size()),
                Map.of("name", "学术论文", "value", papers.size())
        ));

        return Result.ok(result);
    }

    /** Count records with a given status across all 4 achievement types */
    private long countByStatus(List<Competition> competitions,
                               List<InnovationProject> innovations,
                               List<SoftwareCopyright> copyrights,
                               List<AcademicPaper> papers,
                               String status) {
        long count = 0;
        count += competitions.stream().filter(c -> status.equals(c.getStatus())).count();
        count += innovations.stream().filter(p -> status.equals(p.getStatus())).count();
        count += copyrights.stream().filter(c -> status.equals(c.getStatus())).count();
        count += papers.stream().filter(p -> status.equals(p.getStatus())).count();
        return count;
    }

    /** Build A/B/C distribution for competitions */
    private List<Map<String, Object>> buildCategoryDistribution(List<Competition> competitions) {
        Map<String, Long> grouped = competitions.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCompetitionCategory() != null ? c.getCompetitionCategory() : "未知",
                        Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    /** Build first/second/third grade distribution for competitions */
    private List<Map<String, Object>> buildGradeDistribution(List<Competition> competitions) {
        Map<String, String> gradeLabels = Map.of(
                "first", "一等奖", "second", "二等奖", "third", "三等奖"
        );
        Map<String, Long> grouped = competitions.stream()
                .collect(Collectors.groupingBy(
                        c -> {
                            String grade = c.getAwardGrade();
                            return grade != null ? gradeLabels.getOrDefault(grade, grade) : "未知";
                        },
                        Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    /** Build national/provincial/municipal/school/college distribution */
    private List<Map<String, Object>> buildLevelDistribution(List<Competition> competitions) {
        Map<String, String> levelLabels = Map.of(
                "national", "国家级", "provincial", "省级",
                "municipal", "市级", "school", "校级", "college", "院级"
        );
        Map<String, Long> grouped = competitions.stream()
                .collect(Collectors.groupingBy(
                        c -> {
                            String level = c.getAwardLevel();
                            return level != null ? levelLabels.getOrDefault(level, level) : "未知";
                        },
                        Collectors.counting()));
        return grouped.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("name", e.getKey());
                    m.put("value", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());
    }

    /** Build month-by-month count for monthly trend chart. Uses the given dateField. */
    private List<Integer> buildMonthlyTrend(List<?> records, String dateField) {
        int[] months = new int[12]; // index 0 = Jan
        for (Object record : records) {
            Integer month = null;
            if (record instanceof Competition c) {
                if ("awardTime".equals(dateField) && c.getAwardTime() != null) {
                    month = c.getAwardTime().getMonthValue();
                } else if (c.getCreateTime() != null) {
                    month = c.getCreateTime().getMonthValue();
                }
            } else if (record instanceof InnovationProject p && p.getCreateTime() != null) {
                month = p.getCreateTime().getMonthValue();
            } else if (record instanceof SoftwareCopyright c && c.getCreateTime() != null) {
                month = c.getCreateTime().getMonthValue();
            } else if (record instanceof AcademicPaper p && p.getCreateTime() != null) {
                month = p.getCreateTime().getMonthValue();
            }
            if (month != null && month >= 1 && month <= 12) {
                months[month - 1]++;
            }
        }
        List<Integer> result = new ArrayList<>();
        for (int m : months) result.add(m);
        return result;
    }

    @GetMapping("/achievements")
    public Result<Map<String, Object>> achievements(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtils.getCurrentUserId();

        List<Map<String, Object>> allRecords = new ArrayList<>();

        // Query competitions
        if (type == null || "competition".equals(type)) {
            LambdaQueryWrapper<Competition> qw = new LambdaQueryWrapper<>();
            qw.eq(Competition::getSubmitUserId, userId);
            if (status != null) {
                qw.eq(Competition::getStatus, status);
            }
            if (year != null) {
                qw.apply("YEAR(award_time) = {0}", year);
            }
            qw.orderByDesc(Competition::getCreateTime);
            List<Competition> list = competitionMapper.selectList(qw);
            for (Competition c : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("type", "competition");
                map.put("title", c.getCompetitionName());
                map.put("workName", c.getWorkName());
                map.put("category", c.getCompetitionCategory());
                map.put("awardLevel", c.getAwardLevel());
                map.put("awardGrade", c.getAwardGrade());
                map.put("advisor", c.getAdvisor());
                map.put("participants", c.getParticipants());
                map.put("status", c.getStatus());
                map.put("isPinned", c.getIsPinned());
                map.put("createTime", c.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query innovations
        if (type == null || "innovation".equals(type)) {
            LambdaQueryWrapper<InnovationProject> qw = new LambdaQueryWrapper<>();
            qw.eq(InnovationProject::getSubmitUserId, userId);
            if (status != null) {
                qw.eq(InnovationProject::getStatus, status);
            }
            if (year != null) {
                qw.apply("YEAR(start_time) = {0}", year);
            }
            qw.orderByDesc(InnovationProject::getCreateTime);
            List<InnovationProject> list = innovationMapper.selectList(qw);
            for (InnovationProject p : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", p.getId());
                map.put("type", "innovation");
                map.put("title", p.getProjectName());
                map.put("projectLevel", p.getProjectLevel());
                map.put("projectType", p.getProjectType());
                map.put("advisor", p.getAdvisor());
                map.put("members", p.getMembers());
                map.put("status", p.getStatus());
                map.put("isPinned", p.getIsPinned());
                map.put("createTime", p.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query copyrights
        if (type == null || "copyright".equals(type)) {
            LambdaQueryWrapper<SoftwareCopyright> qw = new LambdaQueryWrapper<>();
            qw.eq(SoftwareCopyright::getSubmitUserId, userId);
            if (status != null) {
                qw.eq(SoftwareCopyright::getStatus, status);
            }
            if (year != null) {
                qw.apply("YEAR(create_time) = {0}", year);
            }
            qw.orderByDesc(SoftwareCopyright::getCreateTime);
            List<SoftwareCopyright> list = copyrightMapper.selectList(qw);
            for (SoftwareCopyright c : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", c.getId());
                map.put("type", "copyright");
                map.put("title", c.getSoftwareName());
                map.put("registrationNumber", c.getRegistrationNumber());
                map.put("copyrightOwner", c.getCopyrightOwner());
                map.put("status", c.getStatus());
                map.put("isPinned", c.getIsPinned());
                map.put("createTime", c.getCreateTime());
                allRecords.add(map);
            }
        }

        // Query papers
        if (type == null || "paper".equals(type)) {
            LambdaQueryWrapper<AcademicPaper> qw = new LambdaQueryWrapper<>();
            qw.eq(AcademicPaper::getSubmitUserId, userId);
            if (status != null) {
                qw.eq(AcademicPaper::getStatus, status);
            }
            if (year != null) {
                qw.apply("YEAR(create_time) = {0}", year);
            }
            qw.orderByDesc(AcademicPaper::getCreateTime);
            List<AcademicPaper> list = paperMapper.selectList(qw);
            for (AcademicPaper p : list) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", p.getId());
                map.put("type", "paper");
                map.put("title", p.getTitle());
                map.put("journalName", p.getJournalName());
                map.put("journalLevel", p.getJournalLevel());
                map.put("authors", p.getAuthors());
                map.put("status", p.getStatus());
                map.put("isPinned", p.getIsPinned());
                map.put("createTime", p.getCreateTime());
                allRecords.add(map);
            }
        }

        // Sort by createTime descending, with pinned items first
        allRecords.sort((a, b) -> {
            Integer pinnedA = (Integer) a.getOrDefault("isPinned", 0);
            Integer pinnedB = (Integer) b.getOrDefault("isPinned", 0);
            if (!pinnedA.equals(pinnedB)) {
                return pinnedB - pinnedA;
            }
            LocalDateTime ta = (LocalDateTime) a.get("createTime");
            LocalDateTime tb = (LocalDateTime) b.get("createTime");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

        // Paginate
        int total = allRecords.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);

        List<Map<String, Object>> records;
        if (start >= total) {
            records = Collections.emptyList();
        } else {
            records = allRecords.subList(start, end);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        result.put("records", records);

        return Result.ok(result);
    }

    @GetMapping("/export")
    public void export(HttpServletResponse response) throws Exception {
        Long userId = SecurityUtils.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        String userName = user != null && user.getRealName() != null ? user.getRealName() : user.getUsername();

        List<Map<String, Object>> allRecords = new ArrayList<>();

        // Collect all achievements
        LambdaQueryWrapper<Competition> compQw = new LambdaQueryWrapper<>();
        compQw.eq(Competition::getSubmitUserId, userId);
        for (Competition c : competitionMapper.selectList(compQw)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", "学科竞赛"); m.put("name", c.getCompetitionName());
            m.put("level", c.getAwardLevel()); m.put("grade", c.getAwardGrade());
            m.put("time", c.getAwardTime()); m.put("status", statusLabel(c.getStatus()));
            allRecords.add(m);
        }

        LambdaQueryWrapper<InnovationProject> innovQw = new LambdaQueryWrapper<>();
        innovQw.eq(InnovationProject::getSubmitUserId, userId);
        for (InnovationProject p : innovationMapper.selectList(innovQw)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", "大创项目"); m.put("name", p.getProjectName());
            m.put("level", p.getProjectLevel()); m.put("grade", p.getProjectType());
            m.put("time", p.getStartTime()); m.put("status", statusLabel(p.getStatus()));
            allRecords.add(m);
        }

        LambdaQueryWrapper<SoftwareCopyright> copyQw = new LambdaQueryWrapper<>();
        copyQw.eq(SoftwareCopyright::getSubmitUserId, userId);
        for (SoftwareCopyright s : copyrightMapper.selectList(copyQw)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", "软件著作权"); m.put("name", s.getSoftwareName());
            m.put("level", s.getRegistrationNumber()); m.put("grade", "");
            m.put("time", s.getRegistrationDate()); m.put("status", statusLabel(s.getStatus()));
            allRecords.add(m);
        }

        LambdaQueryWrapper<AcademicPaper> paperQw = new LambdaQueryWrapper<>();
        paperQw.eq(AcademicPaper::getSubmitUserId, userId);
        for (AcademicPaper p : paperMapper.selectList(paperQw)) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type", "学术论文"); m.put("name", p.getTitle());
            m.put("level", p.getJournalLevel()); m.put("grade", p.getJournalName());
            m.put("time", p.getSubmissionDate()); m.put("status", statusLabel(p.getStatus()));
            allRecords.add(m);
        }

        // Write CSV directly (compatible with Excel)
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" +
                URLEncoder.encode(userName + "_科研成果报告.csv", "UTF-8").replace("+", "%20"));
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        writer.write("﻿"); // BOM for Excel UTF-8
        writer.println("序号,成果类型,成果名称,级别/编号,等级/期刊,时间,状态");
        for (int i = 0; i < allRecords.size(); i++) {
            Map<String, Object> m = allRecords.get(i);
            writer.print(i + 1); writer.print(",");
            writer.print(csv(str(m.get("type")))); writer.print(",");
            writer.print(csv(str(m.get("name")))); writer.print(",");
            writer.print(csv(str(m.get("level")))); writer.print(",");
            writer.print(csv(str(m.get("grade")))); writer.print(",");
            writer.print(csv(str(m.get("time")))); writer.print(",");
            writer.print(csv(str(m.get("status")))); writer.println();
        }
        writer.flush();
    }

    private String str(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    private String csv(String val) {
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    @PutMapping("/pin/{type}/{id}")
    public Result<Void> togglePin(@PathVariable String type, @PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();

        switch (type) {
            case "competition" -> {
                Competition competition = competitionMapper.selectById(id);
                if (competition == null) {
                    throw new BusinessException("竞赛成果不存在");
                }
                if (!competition.getSubmitUserId().equals(userId)) {
                    throw new BusinessException("无权限操作此成果");
                }
                competition.setIsPinned(competition.getIsPinned() != null && competition.getIsPinned() == 1 ? 0 : 1);
                competitionMapper.updateById(competition);
            }
            case "innovation" -> {
                InnovationProject project = innovationMapper.selectById(id);
                if (project == null) {
                    throw new BusinessException("创新项目不存在");
                }
                if (!project.getSubmitUserId().equals(userId)) {
                    throw new BusinessException("无权限操作此成果");
                }
                project.setIsPinned(project.getIsPinned() != null && project.getIsPinned() == 1 ? 0 : 1);
                innovationMapper.updateById(project);
            }
            case "copyright" -> {
                SoftwareCopyright copyright = copyrightMapper.selectById(id);
                if (copyright == null) {
                    throw new BusinessException("软件著作权不存在");
                }
                if (!copyright.getSubmitUserId().equals(userId)) {
                    throw new BusinessException("无权限操作此成果");
                }
                copyright.setIsPinned(copyright.getIsPinned() != null && copyright.getIsPinned() == 1 ? 0 : 1);
                copyrightMapper.updateById(copyright);
            }
            case "paper" -> {
                AcademicPaper paper = paperMapper.selectById(id);
                if (paper == null) {
                    throw new BusinessException("论文成果不存在");
                }
                if (!paper.getSubmitUserId().equals(userId)) {
                    throw new BusinessException("无权限操作此成果");
                }
                paper.setIsPinned(paper.getIsPinned() != null && paper.getIsPinned() == 1 ? 0 : 1);
                paperMapper.updateById(paper);
            }
            default -> throw new BusinessException("未知的成果类型: " + type);
        }

        return Result.ok();
    }

    // --- Helper methods ---

    private String statusLabel(String status) {
        return switch (status) {
            case "pending_review" -> "待审核";
            case "under_review" -> "审核中";
            case "returned" -> "已退回";
            case "archived" -> "已归档";
            default -> status;
        };
    }

    private Map<String, Object> buildTypeStats(List<?> records, String type) {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", records.size());

        Map<String, Long> byStatus = records.stream()
                .collect(Collectors.groupingBy(r -> {
                    if (r instanceof Competition c) return c.getStatus();
                    if (r instanceof InnovationProject p) return p.getStatus();
                    if (r instanceof SoftwareCopyright c) return c.getStatus();
                    if (r instanceof AcademicPaper p) return p.getStatus();
                    return "unknown";
                }, Collectors.counting()));

        List<Map<String, Object>> statusList = byStatus.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("status", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        stats.put("byStatus", statusList);
        return stats;
    }
}
