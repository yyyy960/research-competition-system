package com.cms.module.check;

import com.cms.common.Result;
import com.cms.module.competition.entity.Competition;
import com.cms.module.competition.mapper.CompetitionMapper;
import com.cms.module.innovation.entity.InnovationProject;
import com.cms.module.innovation.mapper.InnovationProjectMapper;
import com.cms.module.copyright.entity.SoftwareCopyright;
import com.cms.module.copyright.mapper.CopyrightMapper;
import com.cms.module.paper.entity.AcademicPaper;
import com.cms.module.paper.mapper.PaperMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/check")
@RequiredArgsConstructor
public class DuplicateCheckController {
    private final CompetitionMapper competitionMapper;
    private final InnovationProjectMapper innovationMapper;
    private final CopyrightMapper copyrightMapper;
    private final PaperMapper paperMapper;

    @PostMapping("/duplicate")
    public Result<Map<String, Object>> checkDuplicate(@RequestBody Map<String, Object> body) {
        String type = (String) body.get("type");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        List<Map<String, Object>> duplicates = new ArrayList<>();

        switch (type) {
            case "competition" -> {
                String name = (String) data.get("competitionName");
                String workName = (String) data.get("workName");
                String advisor = (String) data.get("advisor");
                LambdaQueryWrapper<Competition> qw = new LambdaQueryWrapper<>();
                qw.and(w -> w.eq(Competition::getCompetitionName, name)
                              .or()
                              .eq(Competition::getWorkName, name));
                if (workName != null && !workName.isEmpty()) {
                    qw.or().eq(Competition::getWorkName, workName);
                }
                qw.eq(Competition::getAdvisor, advisor);
                List<Competition> list = competitionMapper.selectList(qw);
                for (Competition c : list) {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("id", c.getId());
                    d.put("name", c.getCompetitionName());
                    d.put("status", c.getStatus());
                    duplicates.add(d);
                }
            }
            case "innovation" -> {
                String name = (String) data.get("projectName");
                String advisor = (String) data.get("advisor");
                LambdaQueryWrapper<InnovationProject> qw = new LambdaQueryWrapper<>();
                qw.eq(InnovationProject::getProjectName, name);
                qw.eq(InnovationProject::getAdvisor, advisor);
                List<InnovationProject> list = innovationMapper.selectList(qw);
                for (InnovationProject p : list) {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("id", p.getId());
                    d.put("name", p.getProjectName());
                    d.put("status", p.getStatus());
                    duplicates.add(d);
                }
            }
            case "copyright" -> {
                String regNum = (String) data.get("registrationNumber");
                LambdaQueryWrapper<SoftwareCopyright> qw = new LambdaQueryWrapper<>();
                qw.eq(SoftwareCopyright::getRegistrationNumber, regNum);
                List<SoftwareCopyright> list = copyrightMapper.selectList(qw);
                for (SoftwareCopyright c : list) {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("id", c.getId());
                    d.put("name", c.getSoftwareName());
                    d.put("status", c.getStatus());
                    duplicates.add(d);
                }
            }
            case "paper" -> {
                String title = (String) data.get("title");
                String journal = (String) data.get("journalName");
                LambdaQueryWrapper<AcademicPaper> qw = new LambdaQueryWrapper<>();
                qw.eq(AcademicPaper::getTitle, title);
                if (journal != null && !journal.isEmpty()) {
                    qw.eq(AcademicPaper::getJournalName, journal);
                }
                List<AcademicPaper> list = paperMapper.selectList(qw);
                for (AcademicPaper p : list) {
                    Map<String, Object> d = new LinkedHashMap<>();
                    d.put("id", p.getId());
                    d.put("name", p.getTitle());
                    d.put("status", p.getStatus());
                    duplicates.add(d);
                }
            }
            default -> {
                // unsupported type
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("hasDuplicate", !duplicates.isEmpty());
        result.put("duplicates", duplicates);
        return Result.ok(result);
    }

    @PostMapping("/validate")
    public Result<Map<String, Object>> validate(@RequestBody Map<String, Object> body) {
        String type = (String) body.get("type");
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        switch (type) {
            case "competition" -> {
                if (data.get("competitionName") == null || ((String) data.get("competitionName")).isEmpty()) {
                    errors.add("竞赛名称为必填项");
                }
                if (data.get("competitionCategory") == null) {
                    errors.add("竞赛类别为必填项");
                }
                if (data.get("awardLevel") == null) {
                    errors.add("获奖级别为必填项");
                }
                if (data.get("awardGrade") == null) {
                    errors.add("获奖等级为必填项");
                }
            }
            case "copyright" -> {
                String regNum = (String) data.get("registrationNumber");
                if (regNum == null || regNum.isEmpty()) {
                    errors.add("登记号为必填项");
                } else if (!regNum.matches("^\\d{4}SR\\d{7,8}$")) {
                    errors.add("登记号格式不正确，应为格式: 2024SR0123456");
                }
                if (data.get("softwareName") == null || ((String) data.get("softwareName")).isEmpty()) {
                    errors.add("软件名称为必填项");
                }
            }
            case "paper" -> {
                if (data.get("title") == null || ((String) data.get("title")).isEmpty()) {
                    errors.add("论文标题为必填项");
                }
                if (data.get("journalLevel") == null || ((String) data.get("journalLevel")).isEmpty()) {
                    errors.add("期刊级别为必填项");
                }
            }
            case "innovation" -> {
                if (data.get("projectName") == null || ((String) data.get("projectName")).isEmpty()) {
                    errors.add("项目名称为必填项");
                }
                if (data.get("projectLevel") == null) {
                    errors.add("立项级别为必填项");
                }
            }
            default -> {
                // unsupported type
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("valid", errors.isEmpty());
        result.put("errors", errors);
        result.put("warnings", warnings);
        return Result.ok(result);
    }
}
