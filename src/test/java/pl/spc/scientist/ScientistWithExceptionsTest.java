package pl.spc.scientist;

import org.junit.Assert;
import org.junit.Test;

public class ScientistWithExceptionsTest {
    @Test
    public void testSameResults() throws Exception {
        String expected = "smth";

        String result = ScientistWithExceptions.run("test", () -> expected, () -> expected);

        Assert.assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSameExceptions() throws Exception {
        ScientistWithExceptions.run("test", () -> {throw new IllegalArgumentException();}, () -> {throw new IllegalArgumentException();});
    }

    @Test
    public void testDifferentResults() throws Exception {
        String expected = "smth";

        String result = ScientistWithExceptions.run("test", () -> expected, () -> "different");

        Assert.assertEquals(expected, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDifferentExceptions() throws Exception {
        ScientistWithExceptions.run("test", () -> {throw new IllegalArgumentException();}, () -> {throw new IllegalStateException();});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionInUseFunction() throws Exception {
        ScientistWithExceptions.run("test", () -> {throw new IllegalArgumentException();}, () -> "smth");
    }

    @Test
    public void testExceptionInTryFunction() throws Exception {
        String expected = "smth";

        String result = ScientistWithExceptions.run("test", () -> expected, this::exc);

        Assert.assertEquals(expected, result);
    }

    private String exc() throws Exception {
        throw new IllegalArgumentException();
    }
}
