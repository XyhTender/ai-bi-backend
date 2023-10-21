package com.yu.bi.model.dto.chart;

import lombok.Data;

/**
 * @ClassName BceResponse
 * @DATE 2023/10/11 17:39
 */
@Data
public class BceResponse {
    private String error_code = "";
    private String error_msg = "";
    private String id = "";
    private String result = "";
    private String object = "";
    private boolean need_clear_history = false;
    @Data
    class prompt_tokens {
        private int completion_tokens;
        private int total_tokens;
    }
//      "id": "as-1tbbc0x5af",
//              "object": "chat.completion",
//              "created": 1680166551,
//              "result": "2023年4月2日上海气温13~21℃，多云转阴，东风3-4级，空气质量良，空气质量指数55。\n\n\n\n近7日天气信息：\n\n2023-03-29：阴转小雨，11~17℃，东北风<3级，空气质量良。\n\n2023-03-30：小雨转阴，10~14℃，东风3-4级，空气质量良。\n\n2023-03-31：多云，12~18℃，东风3-4级，空气质量优。\n\n2023-04-01：多云转晴，11~20℃，东南风3-4级，空气质量良。\n\n2023-04-02：多云转阴，13~21℃，东风3-4级，空气质量良。\n\n2023-04-03：阴转中雨，15~18℃，东南风4-5级，空气质量良。\n\n2023-04-04：中雨转小雨，10~17℃，南风5-6级，空气质量优。\n\n2023-04-05：阴，9~14℃，西北风3-4级，空气质量优。",
//              "need_clear_history": false,
//              "usage": {
//        "prompt_tokens": 470,
//                "completion_tokens": 198,
//                "total_tokens": 668
//    }
}
