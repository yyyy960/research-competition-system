package com.cms.module.review.service.impl;

import com.cms.module.review.entity.ModifyLog;
import com.cms.module.review.mapper.ModifyLogMapper;
import com.cms.module.review.service.ModifyLogService;
import com.cms.security.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModifyLogServiceImpl implements ModifyLogService {

    private final ModifyLogMapper modifyLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordCreate(String type, Long id, Object afterData) {
        ModifyLog log = new ModifyLog();
        log.setAchievementType(type);
        log.setAchievementId(id);
        log.setModifyType("create");
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setAfterData(toJson(afterData));
        modifyLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordUpdate(String type, Long id, Object beforeData, Object afterData) {
        ModifyLog log = new ModifyLog();
        log.setAchievementType(type);
        log.setAchievementId(id);
        log.setModifyType("update");
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setBeforeData(toJson(beforeData));
        log.setAfterData(toJson(afterData));
        modifyLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordDelete(String type, Long id, Object beforeData) {
        ModifyLog log = new ModifyLog();
        log.setAchievementType(type);
        log.setAchievementId(id);
        log.setModifyType("delete");
        log.setOperatorId(SecurityUtils.getCurrentUserId());
        log.setOperatorName(SecurityUtils.getCurrentUsername());
        log.setBeforeData(toJson(beforeData));
        modifyLogMapper.insert(log);
    }

    private String toJson(Object obj) {
        try {
            return obj != null ? objectMapper.writeValueAsString(obj) : null;
        } catch (Exception e) {
            return "{\"error\": \"serialization failed\"}";
        }
    }
}
