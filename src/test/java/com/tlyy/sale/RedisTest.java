package com.tlyy.sale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @author LeiDongxing
 * created on 2021/12/18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisTest {
    /**
     * 扣减库存lua脚本
     */
//    private static final String SUB_ITEM_STOCK_LUA_SCRIPT =
//            "local key=KEYS[1]; local subNum=tonumber(ARGV[1]); local surplusStock=tonumber(redis.call('get',key));" +
//                    "if(surplusStock<=0) then return 0;" +
//                    "elseif (subNum>surplusStock) then return 1;" +
//                    "else redis.call('incrby',KEYS[1],-subNum) return 2 end;";

    private final String SUB_ITEM_STOCK_LUA_SCRIPT = "local key=KEYS[1];local num = tonumber(ARGV[1]);local stock = tonumber(redis.call('get',key));" +
            "if (stock<=0) then return false " +
            "elseif (num > stock) then return false " +
            "else redis.call('decrby', KEYS[1], num) return true end";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisLua() {
        //构建redisScript对象,构造方法参数1 执行的lua脚本   参数2 结果返回类型
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<>(SUB_ITEM_STOCK_LUA_SCRIPT, Boolean.class);
//        //参数1 redisScript对象  参数2 keys,可以是多个,取决于你lua里的业务, 参数3 args 需要给lua传入的参数 也是多个
        Boolean result = (Boolean) redisTemplate.execute(defaultRedisScript, Arrays.asList("stock:itemid:1"), "10");
        System.out.println(result);
    }
}
