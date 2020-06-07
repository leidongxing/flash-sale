package com.tlyy.sale.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.tlyy.sale.entity.Item;
import com.tlyy.sale.exception.CommonException;
import com.tlyy.sale.exception.CommonResponseCode;
import com.tlyy.sale.mapper.ItemMapper;
import com.tlyy.sale.mapper.ItemOrderMapper;
import com.tlyy.sale.mapper.ItemStockMapper;
import com.tlyy.sale.util.SnowflakeByHandle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.tlyy.sale.constant.Constants.SALT;

/**
 * @author LeiDongxing
 * create on 2020/6/7 22:08
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderV2Service {
    private final ItemMapper itemMapper;
    private final ItemStockMapper itemStockMapper;
    private final ItemOrderMapper itemOrderMapper;
    private final OrderV1Service orderV1Service;
    private final static SnowflakeByHandle idWorker = new SnowflakeByHandle(0, 0);


    public String createVerifyKey(Long userId, Long itemId) {
        log.info("验证是否在抢购时间内");
        log.info("验证用户是否为合法用户");
        Item item = itemMapper.selectById(itemId);
        if (item == null) {
            throw new CommonException(CommonResponseCode.ERROR, "商品信息不存在");
        }
        String verify = SALT + itemId + userId;
        String verifyKey = DigestUtil.md5Hex(verify);
        return verifyKey;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long userId, Long itemId, Long amount, String key) throws CommonException {
        //1.校验验证码有效性
        if (key == null) {
            throw new CommonException(CommonResponseCode.ERROR, "验证码无效");
        }
        return orderV1Service.createOrder(userId, itemId, amount);
    }
}
