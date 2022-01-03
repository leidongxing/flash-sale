package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.tlyy.sale.api.vo.CreateOrderV2VO;
import com.tlyy.sale.api.vo.CreateOrderV3VO;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.vo.CreateOrderV2VO;
import com.tlyy.sale.api.vo.CreateOrderV3VO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author LeiDongxing
 * created on 2021/12/13
 */
@Slf4j
public class RedisQueue {
    private static final LinkedBlockingQueue<CreateOrderV2VO> redisV2ReqQueue = new LinkedBlockingQueue<>(10240);
    private static final LinkedBlockingQueue<CreateOrderV3VO> redisV3ReqQueue = new LinkedBlockingQueue<>(10240);


    public static void offerV2(CreateOrderV2VO vo) {
        boolean result = redisV2ReqQueue.offer(vo);
        if (!result) {
            log.error("入队redis扣减队列失败v2,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        vo.setEnterQueueTime(System.currentTimeMillis());
        log.debug("入队redis扣减队列成功v2,vo:{}", JSONUtil.toJsonStr(vo));
    }

    public static void offerV3(CreateOrderV3VO vo) {
        boolean result = redisV3ReqQueue.offer(vo);
        if (!result) {
            log.error("入队redis扣减队列失败v3,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        try {
            vo.setEnterQueueTime(System.currentTimeMillis());
            vo.getLock().lock();
            vo.getCondition().await();
            log.debug("入队redis扣减队列成功v3,vo:{}", JSONUtil.toJsonStr(vo));
        } catch (Exception e) {
            log.error("入队redis加锁失败v3,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis加锁失败");
        } finally {
            vo.getLock().unlock();
        }
    }

    public static CreateOrderV2VO takeV2() {
        try {
            CreateOrderV2VO vo = redisV2ReqQueue.take();
            vo.setLeaveQueueTime(System.currentTimeMillis());
            log.debug("出队redis扣减队列成功v2,vo:{}", JSONUtil.toJsonStr(vo));
            return vo;
        } catch (InterruptedException e) {
            log.error("出队redis扣减队列失败v2,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
    }

    public static CreateOrderV3VO takeV3() {
        CreateOrderV3VO vo;
        try {
            vo = redisV3ReqQueue.take();
            vo.setLeaveQueueTime(System.currentTimeMillis());
            log.debug("出队redis扣减队列成功v3,vo:{}", JSONUtil.toJsonStr(vo));
        } catch (InterruptedException e) {
            log.error("出队redis扣减队列失败v3,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
        return vo;
    }


}
