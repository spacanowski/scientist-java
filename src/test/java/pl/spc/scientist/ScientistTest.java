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
