/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */


import org.junit.Test

import javax.swing.*

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 04.02.2016.
 */
class CreateWalletGUITest extends GroovyTestCase {
    @Test
    public void testForm() throws Exception {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new CreateWalletGUI();
        assertTrue(true);
    }
}
