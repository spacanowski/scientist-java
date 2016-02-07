/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <spc> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   spacanowski
 * ----------------------------------------------------------------------------
 */
package pl.spc.scientist.example;

import java.io.File;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.CsvReporter;

import pl.spc.scientist.Scientist;

/**
 * Example usage of {@link Scientist}
 * After run you can load test.html from src/test/resources as load generated csv files.
 * Example shows chart of mean times and mean failures rate.
 */
public class Run {
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        final CsvReporter reporter = CsvReporter.forRegistry(Scientist.getMetrics())
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File("."));
        reporter.start(250, TimeUnit.MILLISECONDS);

        Run run = new Run();
        String test = "smth";

        for (int i = 0; i < 1000; i++) {
            final int iterations = random.nextInt(10000);

            Scientist.run("test", () -> run.use(test, iterations), () -> run.test(test, iterations));
        }
    }

    private String use(String arg, int iterations) {
        String result = arg;

        for (int i = 0; i < iterations; i++) {
            result += arg;
        }

        return result;
    }

    private String test(String arg, int iterations) {
        StringBuilder builder = new StringBuilder(arg);

        for (int i = 0; i < iterations; i++) {
            if (random.nextInt(iterations) != 1) {//if for random failing
                builder.append(arg);
            }
        }

        return builder.toString();
    }
}
