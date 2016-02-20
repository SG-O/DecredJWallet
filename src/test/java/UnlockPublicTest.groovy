import org.junit.Test

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 20.02.2016.
 */
class UnlockPublicTest extends GroovyTestCase {
    Decred prog;

    @Test
    void testUnlockForm() {
        prog = new Decred("test", "test", false, false);
        new UnlockPublic(prog);
    }
}
