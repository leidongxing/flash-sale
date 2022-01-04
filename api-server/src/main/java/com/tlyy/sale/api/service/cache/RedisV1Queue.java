package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.tlyy.sale.api.vo.CreateOrderV1VO;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author LeiDongxing
 * created on 2021/12/13
 */
@Slf4j
public class RedisV1Queue {
    private static final LinkedBlockingQueue<CreateOrderV1VO> redisV1ReqQueue = new LinkedBlockingQueue<>(10240);

    public static void offerV1(CreateOrderV1VO vo) {
        boolean result = redisV1ReqQueue.offer(vo);
        if (!result) {
            log.error("入队redis扣减队列失败v1,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        vo.setEnterQueueTime(System.currentTimeMillis());
        log.debug("入队redis扣减队列成功v1,vo:{}", JSONUtil.toJsonStr(vo));
    }

    public static CreateOrderV1VO takeV1() {
        try {
            CreateOrderV1VO vo = redisV1ReqQueue.take();
            vo.setLeaveQueueTime(System.currentTimeMillis());
            log.debug("出队redis扣减队列成功v1,vo:{}", JSONUtil.toJsonStr(vo));
            return vo;
        } catch (InterruptedException e) {
            log.error("出队redis扣减队列失败v1,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
    }
}
