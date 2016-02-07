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
    public static void main(String[] args) throws InterruptedException {
        final CsvReporter reporter = CsvReporter.forRegistry(Scientist.getMetrics())
                .formatFor(Locale.US)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(new File("."));
        reporter.start(250, TimeUnit.MILLISECONDS);

        Run run = new Run();
        String test = "smth";

        for (int i = 0; i < 10000; i++) {
            Scientist.run("test", () -> run.use(test), () -> run.test(test));
        }
    }

    private String use(String arg) {
        String result = arg;

        for (int i = 0; i < 1000; i++) {
            result += arg;
        }

        return result;
    }

    private String test(String arg) {
        StringBuilder builder = new StringBuilder(arg);
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            if (random.nextInt(1000) != 1) {
                builder.append(arg);
            }
        }

        return builder.toString();
    }
}
