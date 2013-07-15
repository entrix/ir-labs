import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: AVVolkov
 * Date: 15.07.13
 * Time: 12:49
 * To change this template use File | Settings | File Templates.
 */
public class HashCodeTest {

    @Test
    public void testHashCode() {
        String s1 = "abc";
        String s2 = "abc";

        Assert.assertEquals("hash codes are equal", s1.hashCode(), s2.hashCode());
    }
}
