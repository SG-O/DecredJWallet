

/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

import org.junit.Test

/**
 * DecredUtil: Created by Joerg Bayer(admin@sg-o.de) on 09.02.2016.
 */
class getFundsTest extends GroovyTestCase {
    static Process dcrd, wallet;
    @Test
    void testDialogInit() {
        new getFunds();
    }
}
