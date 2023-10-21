package com.yu.bi.model.dto.chart;

import lombok.Data;

/**
 * @ClassName BceAccessTokenResponse
 * @Description 请求AccessToken时的返回体
 * @DATE 2023/10/11 17:45
 */
@Data
public class BceAccessTokenResponse {
    private String refresh_token;
    private String expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;
}
