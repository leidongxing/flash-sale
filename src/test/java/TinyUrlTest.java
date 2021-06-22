import com.tlyy.sale.util.NumericConvertUtils;
import org.junit.Test;

/**
 * @author LeiDongxing
 * created on 2021/6/21
 */
public class TinyUrlTest {
    @Test
    public void test() {
        for (long i = 0; i < 1000000000; i++) {
            String s = NumericConvertUtils.toRandomNumberSystem62(i);
            long n = NumericConvertUtils.toRandomDecimalNumber62(s);
            if (n != i) {
                System.out.println("s: " + s + " " + NumericConvertUtils.toRandomDecimalNumber62(s));
            }
        }
    }
}
