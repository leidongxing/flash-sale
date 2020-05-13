import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;

/**
 * @author LeiDongxing
 * created on 2020/5/13
 */
public class RedissonTest {
    @Test
    public void test(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redisson = Redisson.create(config);
        RedissonReactiveClient redissonReactive = Redisson.createReactive(config);
        RedissonRxClient redissonRx = Redisson.createRx(config);
    }
}
