package com.cms.module.review.service;

import com.cms.common.PageResult;
import com.cms.module.review.dto.ReviewDTO;

import java.util.Map;

public interface ReviewService {
    PageResult<Map<String, Object>> getTodoList(String achievementType, int page, int size, String userRole, String status);
    void approve(ReviewDTO dto);
    void reject(ReviewDTO dto);
}
