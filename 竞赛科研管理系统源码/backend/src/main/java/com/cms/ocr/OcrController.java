package com.cms.ocr;

import com.cms.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("available", false);
        s.put("message", "当前环境不支持自动OCR识别，请使用证书图片参考功能手动填写表单");
        return Result.ok(s);
    }
}
