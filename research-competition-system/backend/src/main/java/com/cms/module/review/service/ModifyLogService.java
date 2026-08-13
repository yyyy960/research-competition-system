package com.cms.module.review.service;

public interface ModifyLogService {
    void recordCreate(String type, Long id, Object afterData);
    void recordUpdate(String type, Long id, Object beforeData, Object afterData);
    void recordDelete(String type, Long id, Object beforeData);
}
