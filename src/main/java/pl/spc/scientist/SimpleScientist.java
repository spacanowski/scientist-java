/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <spc> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   spacanowski
 * ----------------------------------------------------------------------------
 */
package pl.spc.scientist;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Random;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import pl.spc.scientist.function.SimpleFunction;

public class SimpleScientist {
    private static final String USE_TIMER_BASE_NAME = "simsin-use-times";
    private static final String TRY_TIMER_BASE_NAME = "simsin-try-times";
    private static final String FAILURES_HISTOGRAM_BASE_NAME = "simsin-failures";
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Random random = new Random();

    private SimpleScientist() {}

    public static void run(String name, SimpleFunction use, SimpleFunction test) {
        Timer useTimer = metrics.timer(name(USE_TIMER_BASE_NAME, name));
        Timer testTimer = metrics.timer(name(TRY_TIMER_BASE_NAME, name));

        if (random.nextBoolean()) {
            executeUseRun(use, useTimer);
            executeTryRun(test, testTimer, name);
        } else {
            executeTryRun(test, testTimer, name);
            executeUseRun(use, useTimer);
        }
    }

    public static MetricRegistry getMetrics() {
        return metrics;
    }

    private static void executeUseRun(SimpleFunction function, Timer timer) {
        Context time = timer.time();

        try {
            function.execute();
        } finally {
            time.stop();
        }
    }

    private static void executeTryRun(SimpleFunction function, Timer timer, String name) {
        try {
            executeUseRun(function, timer);

            metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name)).update(0);
        } catch (Exception e) {
            metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name)).update(1);
        }
    }
}
