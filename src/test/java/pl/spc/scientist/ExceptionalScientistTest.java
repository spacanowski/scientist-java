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

public class ExceptionalScientistTest {
    @Test
    public void testSameResults() throws Exception {
        String expected = "smth";

        String result = ExceptionalScientist.run("test", () -> expected, () -> expected);

        Assert.assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameExceptions() throws Exception {
        ExceptionalScientist.run("test", () -> {throw new IllegalArgumentException();}, () -> {throw new IllegalArgumentException();});
    }

    @Test
    public void testDifferentResults() throws Exception {
        String expected = "smth";

        String result = ExceptionalScientist.run("test", () -> expected, () -> "different");

        Assert.assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentExceptions() throws Exception {
        ExceptionalScientist.run("test", () -> {throw new IllegalArgumentException();}, () -> {throw new IllegalStateException();});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionInUseFunction() throws Exception {
        ExceptionalScientist.run("test", () -> {throw new IllegalArgumentException();}, () -> "smth");
    }

    @Test
    public void testExceptionInTryFunction() throws Exception {
        String expected = "smth";

        String result = ExceptionalScientist.run("test", () -> expected, this::exc);

        Assert.assertEquals(expected, result);
    }

    private String exc() throws Exception {
        throw new IllegalArgumentException();
    }
}
