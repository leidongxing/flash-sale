package com.tlyy.sale.api.service.cache;

import cn.hutool.json.JSONUtil;
import com.tlyy.sale.api.exception.CommonException;
import com.tlyy.sale.api.exception.CommonResponseCode;
import com.tlyy.sale.api.vo.CreateOrderMultiVO;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author LeiDongxing
 * created on 2022/1/4
 */
@Slf4j
public class RedisMultiQueue {
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue0 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue1 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue2 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue3 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue4 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue5 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue6 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue7 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue8 = new LinkedBlockingQueue<>(1024);
    private static final LinkedBlockingQueue<CreateOrderMultiVO> redisV3ReqQueue9 = new LinkedBlockingQueue<>(1024);
    @SuppressWarnings("unchecked")
    private static final LinkedBlockingQueue<CreateOrderMultiVO>[] queueArray = new LinkedBlockingQueue[]{redisV3ReqQueue0, redisV3ReqQueue1, redisV3ReqQueue2, redisV3ReqQueue3, redisV3ReqQueue4,
            redisV3ReqQueue5, redisV3ReqQueue6, redisV3ReqQueue7, redisV3ReqQueue8, redisV3ReqQueue9};

    public static void offerV1(CreateOrderMultiVO vo) {
        boolean result = queueArray[(int) (vo.getCreateTime() % 10)].offer(vo);
        if (!result) {
            log.error("入队redis扣减队列失败v1,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        vo.setEnterQueueTime(System.currentTimeMillis());
        log.debug("入队redis扣减队列成功v1,vo:{}", JSONUtil.toJsonStr(vo));
    }

    public static CreateOrderMultiVO takeV1(int i) {
        try {
            CreateOrderMultiVO vo = queueArray[i].take();
            vo.setLeaveQueueTime(System.currentTimeMillis());
            log.debug("出队redis扣减队列成功v1,vo:{}", JSONUtil.toJsonStr(vo));
            return vo;
        } catch (InterruptedException e) {
            log.error("出队redis扣减队列失败v1,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
    }

    public static void offerV2(CreateOrderMultiVO vo) {
        boolean result = queueArray[(int) (vo.getCreateTime() % 10)].offer(vo);
        if (!result) {
            log.error("入队redis扣减队列失败v1,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis扣减队列请求失败");
        }
        try {
            vo.setEnterQueueTime(System.currentTimeMillis());
            vo.getLock().lock();
            vo.getCondition().await();
            log.debug("入队redis扣减队列成功v2,vo:{}", JSONUtil.toJsonStr(vo));
        } catch (Exception e) {
            log.error("入队redis加锁失败v2,vo:{}", JSONUtil.toJsonStr(vo));
            throw new CommonException(CommonResponseCode.ERROR, "入队redis加锁失败");
        } finally {
            vo.getLock().unlock();
        }
    }

    public static CreateOrderMultiVO takeV2(int i) {
        CreateOrderMultiVO vo;
        try {
            vo = queueArray[i].take();
            vo.setLeaveQueueTime(System.currentTimeMillis());
            log.debug("出队redis扣减队列成功v2,vo:{}", JSONUtil.toJsonStr(vo));
        } catch (InterruptedException e) {
            log.error("出队redis扣减队列失败v2,vo:{}", e.getMessage());
            throw new CommonException(CommonResponseCode.ERROR, "出队redis扣减队列失败");
        }
        return vo;
    }
}
