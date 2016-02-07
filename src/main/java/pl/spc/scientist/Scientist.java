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
import java.util.function.Supplier;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

/**
 * Main Scientist that is most commonly used. Registers time metrics of execution for
 * use and test methods and histogram of fail rate for test method. Fail of test method
 * is considered when result of use and test methods are different or when test method
 * throws exception. Both methods are run in random order. All exceptions all test method
 * will be caught and not propagated. Result of use method will always be returned.
 * Metrics are registered using {@link io.dropwizard.metrics}. Time metrics are registered
 * using {@link Timer} and failure rates using {@link Histogram}
 */
public final class Scientist {
    private static final String USE_TIMER_BASE_NAME = "use-times";
    private static final String TRY_TIMER_BASE_NAME = "try-times";
    private static final String FAILURES_HISTOGRAM_BASE_NAME = "failures";
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Random random = new Random();

    private Scientist() {}

    /**
     * Run scientist and register metrics of run. Time metrics for use method are
     * named use-times.{@value name} for test method try-times.{@value name} and
     * failure rates failures.{@value name}
     *
     * @param name of scientist
     * @param use method that is considered as method currently in use
     * @param test method that is tested as replacement of use method
     * @return result of use method
     */
    public static <T>T run(String name, Supplier<T> use, Supplier<T> test) {
        Histogram failures = metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name));
        Timer useTimer = metrics.timer(name(USE_TIMER_BASE_NAME, name));
        Timer testTimer = metrics.timer(name(TRY_TIMER_BASE_NAME, name));

        final T useResult;
        final T testResult;

        if (random.nextBoolean()) {
            useResult = executeUseRun(use, useTimer);
            testResult = executeTryRun(test, testTimer);
        } else {
            testResult = executeTryRun(test, testTimer);
            useResult = executeUseRun(use, useTimer);
        }

        failures.update(isSameResult(useResult, testResult) ? 0 : 1);

        return useResult;
    }

    /**
     * Returns {@link MetricRegistry} used by Scientist. Used for metrics reporter.
     *
     * @return {@link MetricRegistry} for scientist
     */
    public static MetricRegistry getMetrics() {
        return metrics;
    }

    private static <T>T executeUseRun(Supplier<T> function, Timer timer) {
        Context time = timer.time();

        try {
            return function.get();
        } finally {
            time.stop();
        }
    }

    private static <T>T executeTryRun(Supplier<T> function, Timer timer) {
        try {
            return executeUseRun(function, timer);
        } catch (Exception e) {
            return null;
        }
    }

    private static <T>boolean isSameResult(T useResult, T testResult) {
        return (useResult == null && testResult == null) ||
                    (useResult != null && testResult != null && useResult.equals(testResult));
    }
}
