import org.junit.Test

/**
 * DecredJWallet: Created by Joerg Bayer(admin@sg-o.de) on 16.02.2016.
 */
class DecredTest extends GroovyTestCase {
    Decred prog;

    @Test
    void testReadDecred() {
        prog = new Decred("test", "test", false, true);
        System.out.println(prog.checkVersion());
        prog.startDecred();
        prog.startWallet();
        while (prog.allRunning()) {
            String read = prog.readWallet();
            if (read != null) {
                System.out.println(read);
            }
        }
        prog.terminate();
    }
}
