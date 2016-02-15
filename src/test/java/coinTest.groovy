import Coin
import org.junit.Test

/*
 * Copyright (c) 2016.
 * Decred JWallet by Jörg Bayer is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License.
 * Permissions beyond the scope of this license may be available at https://www.sg-o.de/.
 */

/**
 * Decred Util: Created by Joerg Bayer (admin@sg-o.de) on 15.02.2016.
 */
class coinTest extends GroovyTestCase {
    private Coin testCo;

    @Test
    void testMakeCoin0() throws Exception {
        testCo = new Coin("10");
        assertEquals(testCo.getAmount(), 10);
    }

    @Test
    void testMakeCoin1() throws Exception {
        testCo = new Coin("-10");
        assertEquals(testCo.getAmount(), -10);
    }

    @Test
    void testMakeCoin3() throws Exception {
        testCo = new Coin(10d);
        assertEquals(testCo.getAmount(), 10);
    }

    @Test
    void testMakeCoin4() throws Exception {
        testCo = new Coin(-10d);
        assertEquals(testCo.getAmount(), -10);
    }

    @Test
    void testMakeCoin5() throws Exception {
        testCo = new Coin("10.0001");
        assertEquals(testCo.getAmount(), 10.0001);
    }

    @Test
    void testMakeCoin6() throws Exception {
        testCo = new Coin("10,0001");
        assertEquals(testCo.getAmount(), 10.0001);
    }

    @Test
    void testMakeCoin7() throws Exception {
        testCo = new Coin("-10.0001");
        assertEquals(testCo.getAmount(), -10.0001);
    }

    @Test
    void testMakeCoin8() throws Exception {
        testCo = new Coin("-10,0001");
        assertEquals(testCo.getAmount(), -10.0001);
    }

    @Test
    void testMakeCoin9() throws Exception {
        testCo = new Coin("10.000000001");
        assertEquals(testCo.getAmount(), 10);
    }

    @Test
    void testMakeCoin10() throws Exception {
        testCo = new Coin("-10.000000001");
        assertEquals(testCo.getAmount(), -10);
    }

    @Test
    void testSetAmount0() throws Exception {
        testCo = new Coin(10d);
        testCo.setAmount(20);
        assertEquals(testCo.getAmount(), 20);
    }

    @Test
    void testSetAmount1() throws Exception {
        testCo = new Coin(10d);
        testCo.setAmount(-20);
        assertEquals(testCo.getAmount(), -20)
    }

    @Test
    void testSetAmount3() throws Exception {
        testCo = new Coin(10d);
        testCo.setAmount("20");
        assertEquals(testCo.getAmount(), 20);
    }

    @Test
    void testSetAmount4() throws Exception {
        testCo = new Coin(10d);
        testCo.setAmount("-20");
        assertEquals(testCo.getAmount(), -20);
    }

    @Test
    void testToString0() throws Exception {
        testCo = new Coin(10d);
        assertEquals(testCo.toString(), "10,00000000");
    }

    @Test
    void testToString1() throws Exception {
        testCo = new Coin(-10d);
        assertEquals(testCo.toString(), "-10,00000000");
    }

    @Test
    void testToString2() throws Exception {
        testCo = new Coin(10.01d);
        assertEquals(testCo.toString(), "10,01000000");
    }

    @Test
    void testToString3() throws Exception {
        testCo = new Coin(-10.01d);
        assertEquals(testCo.toString(), "-10,01000000");
    }

    @Test
    void testToString4() throws Exception {
        testCo = new Coin(10.000000001d);
        assertEquals(testCo.toString(), "10,00000000");
    }

    @Test
    void testToString5() throws Exception {
        testCo = new Coin(-10.000000001d);
        assertEquals(testCo.toString(), "-10,00000000");
    }

    @Test
    void testToString6() throws Exception {
        testCo = new Coin("20999999.99800912");
        assertEquals(testCo.toString(), "20999999,99800912");
    }

    @Test
    void testToString7() throws Exception {
        testCo = new Coin("-20999999.99800912");
        assertEquals(testCo.toString(), "-20999999,99800912");
    }

    @Test
    void testToString8() {
        boolean success = false;
        try {
            testCo = new Coin("20999999.99800913");
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testToString9() {
        boolean success = false;
        try {
            testCo = new Coin("-20999999.99800913");
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testAdd0() {
        testCo = new Coin(10);
        testCo.add(new Coin(10));
        assertEquals(testCo.getAmount(), 20d);
    }

    @Test
    void testAdd1() {
        testCo = new Coin(10);
        testCo.add(new Coin(-10));
        assertEquals(testCo.getAmount(), 0d);
    }

    @Test
    void testAdd2() {
        testCo = new Coin(-10);
        testCo.add(new Coin(10));
        assertEquals(testCo.getAmount(), 0d);
    }

    @Test
    void testAdd3() {
        testCo = new Coin(-10);
        testCo.add(new Coin(-10));
        assertEquals(testCo.getAmount(), -20d);
    }

    @Test
    void testAdd4() {
        testCo = new Coin(0);
        testCo.add(new Coin(10));
        assertEquals(testCo.getAmount(), 10d);
    }

    @Test
    void testAdd5() {
        testCo = new Coin(0);
        testCo.add(new Coin(-10));
        assertEquals(testCo.getAmount(), -10d);
    }

    @Test
    void testAdd6() {
        boolean success = false;
        try {
            testCo = new Coin("20999999");
            testCo.add(new Coin(1));
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testAdd7() {
        boolean success = false;
        try {
            testCo = new Coin("-20999999");
            testCo.add(new Coin(-1));
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testSub0() {
        testCo = new Coin(10);
        testCo.sub(new Coin(10));
        assertEquals(testCo.getAmount(), 0d);
    }

    @Test
    void testSub1() {
        testCo = new Coin(10);
        testCo.sub(new Coin(-10));
        assertEquals(testCo.getAmount(), 20d);
    }

    @Test
    void testSub2() {
        testCo = new Coin(-10);
        testCo.sub(new Coin(10));
        assertEquals(testCo.getAmount(), -20d);
    }

    @Test
    void testSub3() {
        testCo = new Coin(-10);
        testCo.sub(new Coin(-10));
        assertEquals(testCo.getAmount(), 0d);
    }

    @Test
    void testSub4() {
        testCo = new Coin(0);
        testCo.sub(new Coin(-10));
        assertEquals(testCo.getAmount(), 10d);
    }

    @Test
    void testSub5() {
        testCo = new Coin(0);
        testCo.sub(new Coin(-10));
        assertEquals(testCo.getAmount(), 10d);
    }

    @Test
    void testSub6() {
        boolean success = false;
        try {
            testCo = new Coin("20999999");
            testCo.sub(new Coin(-1));
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testSub7() {
        boolean success = false;
        try {
            testCo = new Coin("-20999999");
            testCo.sub(new Coin(1));
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testMul0() {
        testCo = new Coin(10);
        testCo.mul(2);
        assertEquals(testCo.getAmount(), 20);
    }

    @Test
    void testMul1() {
        testCo = new Coin(-10);
        testCo.mul(2);
        assertEquals(testCo.getAmount(), -20);
    }

    @Test
    void testMul2() {
        testCo = new Coin(10);
        testCo.mul(5);
        assertEquals(testCo.getAmount(), 50);
    }

    @Test
    void testMul3() {
        testCo = new Coin(-10);
        testCo.mul(5);
        assertEquals(testCo.getAmount(), -50);
    }

    @Test
    void testMul4() {
        testCo = new Coin(10);
        testCo.mul(-5);
        assertEquals(testCo.getAmount(), -50);
    }

    @Test
    void testMul5() {
        testCo = new Coin(-10);
        testCo.mul(-5);
        assertEquals(testCo.getAmount(), 50);
    }

    void testMul6() {
        boolean success = false;
        try {
            testCo = new Coin("20999999");
            testCo.mul(2);
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testMul7() {
        boolean success = false;
        try {
            testCo = new Coin("-20999999");
            testCo.mul(2);
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    void testMul8() {
        boolean success = false;
        try {
            testCo = new Coin("20999999");
            testCo.mul(2000000);
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    void testMul9() {
        boolean success = false;
        try {
            testCo = new Coin("-20999999");
            testCo.mul(2000000);
        } catch (Exception e) {
            success = true;
        }
        assertTrue(success);
    }
}
