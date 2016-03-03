import Coin
import comunicationStrings
import org.junit.Test

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 28.02.2016.
 */
class comunicationStringsTest extends GroovyTestCase {
    @Test
    void testSENDMANY() {
        HashMap<String, Coin> test = new HashMap<String, Coin>();
        test.put("addr1", new Coin("1.01"));
        test.put("addr5", new Coin("5.01"));
        System.out.println(test);
        System.out.println(comunicationStrings.class.SENDMANY("default", test));
    }
}
