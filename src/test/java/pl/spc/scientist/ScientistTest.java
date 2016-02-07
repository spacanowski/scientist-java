/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <spc> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   spacanowski
 * ----------------------------------------------------------------------------
 */
package pl.spc.scientist;

import org.junit.Assert;
import org.junit.Test;

public class ScientistTest {
    @Test
    public void testSameResults() {
        String expected = "smth";

        String result = Scientist.run("test", () -> expected, () -> expected);

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testDifferentResults() {
        String expected = "smth";

        String result = Scientist.run("test", () -> expected, () -> "different");

        Assert.assertEquals(expected, result);
    }

    @Test
    public void testExceptionInTryFunction() {
        String expected = "smth";

        String result = Scientist.run("test", () -> expected, () -> {throw new IllegalArgumentException();});

        Assert.assertEquals(expected, result);
    }
}
