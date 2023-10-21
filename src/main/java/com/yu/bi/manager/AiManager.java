package com.yu.bi.manager;

import com.yu.bi.api.BceClient;
import com.yu.bi.common.ErrorCode;
import com.yu.bi.exception.BusinessException;
import com.yu.bi.model.dto.chart.BiResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName AiManager
 * @Description 用于对接第三方接口
 * @DATE
 */
@Slf4j
@Service
public class AiManager {
    // 暂时只接入一个——千帆大模型，暂时只使用其中的一个服务
    @Resource
    private BceClient bceClient;

    /**
     * AI对话
     */
    public String doChat(String message) {
        String responseData =  bceClient.onChat(message);
        if (responseData == null || StringUtils.isBlank(responseData)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 响应错误");
        }
        return responseData;
    }

    public String buildChatMessage(String goal, String data, String chartType) {
        return bceClient.buildChatMessage(goal, data, chartType);
    }

    /**
     * 拆分接口返回的结果 BiResponse
     * @return
     */
    public BiResponse getChatResult(String res) throws Exception{
        return bceClient.getChatResult(res);
    }
}
