/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 02.02.2016.
 */
public class settingsTest {
    @Test
    public void testEncrypt1() throws Exception {
        settings testset = new settings();
        String encrypted = testset.encrypt("test", "key" );
        assertEquals("9X//4Dm53Rkbs/aYse4MXA==",encrypted);
    }
    @Test
    public void testEncrypt2() throws Exception {
        settings testset = new settings();
        String encrypted = testset.encrypt("\"othertestwithlongtextandäöü@#$%!&%%and space  otherhorriblestuff\\t\\n\"", "key" );
        assertEquals("cuSxS61Lo3w43BD76kvi++BHVA7ypOZ0VDpYdfkuetRMKsEGIUaRQ8WRzwBT98jffI9i5r8jBZ5/3iB6564xDX5lDpjic26Wdr1ibF0AgZI=",encrypted);
    }

    @Test
    public void testDecrypt1() throws Exception {
        settings testset = new settings();
        String decrypted = testset.decrypt("9X//4Dm53Rkbs/aYse4MXA==", "key");
        assertEquals("test", decrypted);
        decrypted = testset.decrypt("9X//4Dm53Rkbs/aYse4MXA==", "falsekey");
        assertNotEquals("test", decrypted);
    }

    @Test
    public void testDecrypt2() throws Exception {
        settings testset = new settings();
        String decrypted = testset.decrypt("9X//4Dm53Rkbs/aYse4MXA==", "falsekey");
        assertNotEquals("test", decrypted);
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String test = UUID.randomUUID().toString();
        String testkey = UUID.randomUUID().toString();
        settings testset = new settings();
        String encrypted = testset.encrypt(test, testkey );
        String decrypted = testset.decrypt(encrypted, testkey);
        assertEquals(test,decrypted);
    }
}