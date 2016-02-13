/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

package update;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 07.02.2016.
 */
public class UpdatingTest {

    @Test
    public void testSetProgress() throws Exception {
        Updating gui = new Updating();
        gui.setProgress(0.5f);
        while (gui.isValid()){}
        assertTrue(true);
    }
}