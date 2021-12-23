package com.tlyy.sale.service.order;

import cn.hutool.json.JSONUtil;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.entity.ItemOrder;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.mapper.ItemOrderMapper;
import com.tlyy.sale.service.cache.RedisQueue;
import com.tlyy.sale.util.SnowflakeByHandle;
import com.tlyy.sale.vo.CreateOrderV2VO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.tlyy.sale.vo.CreateOrderV2VO.*;


/**
 * @author LeiDongxing
 * created on 2021/12/12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV2Service {
    private final ItemOrderMapper itemOrderMapper;
    private final static SnowflakeByHandle idWorker = new SnowflakeByHandle(0, 0);
    private final static long ALL_PROCESS_ALLOW_MILLISECOND = 200L;


    private static final ThreadPoolExecutor redisThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(2000));

    private final String SUB_ITEM_STOCK_LUA_SCRIPT = "local key=KEYS[1];local num = tonumber(ARGV[1]);local stock = tonumber(redis.call('get',key));" +
            "if (stock<=0) then return false " +
            "elseif (num > stock) then return false " +
            "else redis.call('decrby', KEYS[1], num) return true end";

    private static final ThreadPoolExecutor dbThreadPool = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(1024));


    public Long createOrderV1(CreateOrderV2VO vo) throws CommonException {
        Long itemId = vo.getItemId();
        Long amount = vo.getAmount();
        Long userId = vo.getUid();
        long startTime = System.currentTimeMillis();

        //1.校验下单状态,TODO 内存校验下单的商品是否存在
        Item item = new Item();
        item.setPrice(new BigDecimal("5600"));

        //2.请求进入redis扣减队列
        RedisQueue.offer(vo);

        //3.订单处理
        ItemOrder order = new ItemOrder();
        order.setUserId(userId);
        order.setItemId(itemId);
        order.setAmount(amount);
        order.setItemPrice(item.getPrice());
        order.setOrderPrice(order.getItemPrice().multiply(new BigDecimal(amount)));
        order.setGmtCreate(new Date());
        order.setGmtModified(new Date());
        order.setDeleted(false);

        int waitTimes = 0;
        while (System.currentTimeMillis() - startTime < ALL_PROCESS_ALLOW_MILLISECOND) {
            switch (vo.getRedisProcessStatus()) {
                case PROCESS_FAIL:
                    log.info("处理失败，等待处理次数waitTime:{},vo:{}", waitTimes, JSONUtil.toJsonStr(vo));
                    throw new CommonException(CommonResponseCode.ERROR, "库存不足");
                case PROCESS_SUCCESS:
                    //4.生成交易流水号,订单号
                    order.setId(idWorker.nextId());
                    int orderResult = itemOrderMapper.insert(order);
                    if (orderResult <= 0) {
                        throw new CommonException(CommonResponseCode.ERROR, "生成订单失败");
                    }

                    //TODO 异步处理 根据订单 加上商品的销量  减去库存

                    //4.返回前端
                    return order.getId();
                case UN_PROCESS:
                case PROCESS_PENDING:
                    try {
                        waitTimes++;
                        TimeUnit.MICROSECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        throw new CommonException(CommonResponseCode.ERROR, "等待redis扣减异常");
                    }
            }
        }
        log.info("处理超时，等待处理次数waitTime:{},vo:{}", waitTimes, JSONUtil.toJsonStr(vo));
        throw new CommonException(CommonResponseCode.ERROR, "处理超时");
    }
}
