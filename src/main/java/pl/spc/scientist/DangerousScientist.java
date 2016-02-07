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

import java.util.function.BooleanSupplier;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import pl.spc.scientist.function.SimpleFunction;

public class DangerousScientist {
    private static final String USE_TIMER_BASE_NAME = "simsin-use-times";
    private static final String TRY_TIMER_BASE_NAME = "simsin-try-times";
    private static final String FAILURES_HISTOGRAM_BASE_NAME = "simsin-failures";
    private static final MetricRegistry metrics = new MetricRegistry();

    private DangerousScientist() {}

    public static void run(String name, SimpleFunction use, SimpleFunction test, SimpleFunction rollback, BooleanSupplier runCompare) {
        Timer useTimer = metrics.timer(name(USE_TIMER_BASE_NAME, name));
        Timer testTimer = metrics.timer(name(TRY_TIMER_BASE_NAME, name));

        executeTryRun(test, testTimer, name, rollback);
        executeUseRun(use, useTimer);

        if (runCompare.getAsBoolean()) {
            metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name)).update(0);
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

    private static void executeTryRun(SimpleFunction function, Timer timer, String name, SimpleFunction rollback) {
        try {
            executeUseRun(function, timer);
        } catch (Exception e) {
            metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name)).update(1);
        } finally {
            rollback.execute();
        }
    }
}
