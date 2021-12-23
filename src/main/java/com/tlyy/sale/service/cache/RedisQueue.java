package com.tlyy.sale.service.cache;

import cn.hutool.json.JSONUtil;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.vo.CreateOrderV2VO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author LeiDongxing
 * created on 2021/12/13
 */
@Slf4j
public class RedisQueue {
    private static final LinkedBlockingQueue<CreateOrderV2VO> redisReqQueue = new LinkedBlockingQueue<>(10);

    public static void offer(CreateOrderV2VO vo) {
        boolean result = redisReqQueue.offer(vo);
        if (!result) {
            log.info("入队redis扣减队列失败,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        vo.setEnterQueueTime(System.currentTimeMillis());
        log.info("入队redis扣减队列成功,vo:{}", JSONUtil.toJsonStr(vo));
    }

    public static CreateOrderV2VO take() {
        try {
            CreateOrderV2VO vo = redisReqQueue.take();
            log.info("出队redis扣减队列成功,vo:{}", JSONUtil.toJsonStr(vo));
            return vo;
        } catch (InterruptedException e) {
            log.info("出队redis扣减队列失败,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
    }
}
