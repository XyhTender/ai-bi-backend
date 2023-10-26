package com.yu.bi.manager;

import com.yu.bi.common.ErrorCode;
import com.yu.bi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务（提供通用能力）
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    //限流操作  key-->区分不同的限流器
    public void doRateLimit(String key){
        // 每秒限制最多访问两次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);

        //每当一个操作来了后，请求一个令牌
        boolean tryAcquire = rateLimiter.tryAcquire(1);
        if (!tryAcquire) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
