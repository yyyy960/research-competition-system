package com.cms.module.review.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cms.module.review.entity.ReviewRecord;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface ReviewRecordMapper extends BaseMapper<ReviewRecord> {
    @Select("SELECT * FROM review_record WHERE achievement_type = #{type} AND achievement_id = #{achievementId} ORDER BY create_time DESC")
    List<ReviewRecord> selectByAchievement(String type, Long achievementId);
}
