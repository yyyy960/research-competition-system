package com.cms.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private long total;
    private long pageSize;
    private long current;
    private List<T> records;

    public static <T> PageResult<T> of(long total, long current, long pageSize, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.total = total;
        result.current = current;
        result.pageSize = pageSize;
        result.records = records;
        return result;
    }
}
