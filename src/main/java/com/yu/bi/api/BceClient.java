package com.yu.bi.api;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.squareup.okhttp.*;
import com.yu.bi.common.ErrorCode;
import com.yu.bi.exception.BusinessException;
import com.yu.bi.model.dto.chart.BceAccessTokenResponse;
import com.yu.bi.model.dto.chart.BceResponse;
import com.yu.bi.model.dto.chart.BiResponse;
import com.yu.bi.service.ChartService;
import com.yu.bi.service.impl.ChartServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName BceClient
 * @Description 获取AccessToken（有效期为30天，生产环境注意刷新）
 * @DATE 2023/10/11 11:54
 */
@Configuration
@Data
@Slf4j
public class BceClient {
    private String apiKey = "调用你的百度千帆模型apikey";
    private String secretKey = "调用你的百度千帆模型secretKey";
    private String accessToken = "调用你的百度千帆模型accessToken";
    private ChartService chartService = new ChartServiceImpl();

    static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    public String getAccessToken() {
        HTTP_CLIENT.setReadTimeout(50, TimeUnit.SECONDS);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        StringBuilder url = new StringBuilder();
        url.append("https://aip.baidubce.com/oauth/2.0/token?client_id=");
        //url.append(appProperties.getBceProvider().getApiKey());
        url.append(apiKey);
        url.append("&client_secret=");
        //url.append(appProperties.getBceProvider().getSecretKey());
        url.append(secretKey);
        url.append("&grant_type=client_credentials");
        Request request = new Request.Builder()
                .url(url.toString())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();

            BceAccessTokenResponse bceAccessTokenResponse = JSONUtil.toBean(response.body().string(), BceAccessTokenResponse.class);
            if (bceAccessTokenResponse == null || StringUtils.isBlank(bceAccessTokenResponse.getAccess_token())) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取AI服务失败");
            }
            return bceAccessTokenResponse.getAccess_token();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AI 对话
     * @param message
     * @throws IOException
     */
    public String onChat(String message) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, message);
        StringBuilder url = new StringBuilder();
        url.append("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/eb-instant?access_token=");
        url.append(accessToken);
        Request request = new Request.Builder()
                .url(url.toString())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            BceResponse bceResponse = JSONUtil.toBean(response.body().string(), BceResponse.class);
            // 如果过期就重新刷accessToken
            if (bceResponse.getError_code() != null && bceResponse.getError_code().equals("110") || bceResponse.getError_code().equals("119")) {
                accessToken = getAccessToken();
            } else if (bceResponse.getError_code() != null && bceResponse.getError_code().equals("336002") || bceResponse.getError_code().equals("336003")) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入的JSON有误");
            }

            return bceResponse.getResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildChatMessage(String goal, String data, String chartType) {
        JSONObject json = new JSONObject();
        // 添加"messages"字段，并设置其值为一个JSON数组
        JSONArray messages = new JSONArray();
        json.put("messages", messages);
        // 创建一个JSONObject，表示第一条消息
        JSONObject message1 = new JSONObject();
        // 设置"role"字段为"user"
        message1.put("role", "user");
        // 设置"content"字段为所需的内容字符串
        // String content = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容:\n分析需求:\n{用户数量趋势}\n原始数据:\n{日期,用户数量\\n2023.10.10,10\\n2023.10.11,10\\n2023.10.12,10\\n2023.10.13,50\\n2023.10.14,60\\n2023.10.15,10\\n2023.10.16,5\\n2023.10.17,1010\\n2023.10.18,955\\n2023.10.19,536\\n}\n请根据这两部分内容，帮我按照以下格式生成内容(此外不要输出任何多余的开头、结尾、注释)\n{合理的使用前端 Echarts V5 的option 配置对象js代码将数据绘制成散点图}\n{图表的描述及明确的数据分析结论，不少于500字}";
        // String content = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容:\n分析需求:\n{" + goal + "}\n原始数据:\n{" + data + "}\n请根据这两部分内容，帮我按照以下格式生成内容(此外不要输出任何多余的开头、结尾、注释)\n：【【【\n{将数据绘制成"+ chartType +"的前端 Echarts V5 的option 配置对象js代码}【【【\n{看图说话及明确的数据分析结论，不少于500字}";
        // String content = "你是一个数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容:\n分析需求:\n{" + goal + "}\n原始数据:\n{" + data + "}\n请根据这两部分内容，帮我按照以下格式生成内容(此外不要输出任何多余的开头、结尾、注释)：\n{将数据绘制成"+ chartType +"的前端 Echarts V5 的option 配置对象json代码}\n{看图说话及明确的数据分析结论，不少于500字}";
        // String content = "接下来我会按照以下固定格式给你提供内容:\n分析需求:\n{" + goal + "}\n原始数据:\n{" + data + "}\n请根据这两部分内容，帮我按照以下格式生成内容(此外不要输出任何多余的开头、结尾、注释)：\n{将数据绘制成"+ chartType +"的前端 Echarts V5 的option 配置对象json代码(用markdown语法展示代码，我不想自己配置任何参数，所以你不要写注释，或者让我自己配置什么)}\n{看图说话及明确的数据分析结论，不少于500字}";
        String content = "接下来我会按照以下固定格式给你提供内容:\n分析需求:\n{" + goal + "}\n原始数据:\n{" + data + "}\n请根据这两部分内容，帮我生成(此外不要输出任何多余的开头、结尾、注释)：\n```将数据绘制成"+ chartType +"的前端 Echarts V5 的option 配置对象json代码```\n{图表的描述及明确的数据分析结论}";
        message1.put("content", content);
        // 将message对象添加到messages数组中
        messages.add(message1);

        // 添加系统人设，
        json.put("system", "你是一个数据分析师和前端开发专家");
        // 输出JSON字符串
        System.out.println(json.toStringPretty());
        return json.toString();
    }

    /**
     * 拆分接口返回的结果 BiResponse
     * @return
     */
    public BiResponse getChatResult(String res) throws Exception {
        String[] splits = res.split("```");
        BiResponse biResponse = new BiResponse();
        // biResponse.setGenChart("option = " + splits[1].substring(splits[1].indexOf("{")));
        // 验证代码是否正确，不正确的话统一抛出异常
        // 可能会在代码段之前生成一段汉字
        int codeIndex = 0;
        int descriptionIndex = 1;
        if (splits.length == 3) {
            codeIndex++;
            descriptionIndex++;
        }
        String chartCode = splits[codeIndex].substring(splits[codeIndex].indexOf("{"));
        // 如果还配置了"option"字段
        if (chartCode.contains("option"))
            chartCode = chartCode.substring(chartCode.indexOf(": ") + 1);
        // TODO: 过滤掉不属于ECharts的字段
        Set<String> set = new HashSet<>();

        // JSONUtil.parseArray(chartCode);
        JSONUtil.parseObj(chartCode);
        biResponse.setGenChart(chartCode);
        if (splits[descriptionIndex].contains("}"))
            biResponse.setGenResult(splits[descriptionIndex].substring(splits[descriptionIndex].indexOf("}") + 3));
        else biResponse.setGenResult(splits[descriptionIndex]);
        return biResponse;
    }
}
