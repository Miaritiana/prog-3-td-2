import app.foot.utils.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DateUtilsTest {
    @Test
    public void test_one_equals_one() {
        Assertions.assertEquals(1,1);
    }
    @Test
    public void second_test_one_equals_one() {
        Assertions.assertEquals(1, DateUtils.oneEqualsOne(1));
    }
}