package pl.spc.scientist;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Random;
import java.util.function.Supplier;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

public final class Scientist {
    private static final String USE_TIMER_BASE_NAME = "use-times";
    private static final String TRY_TIMER_BASE_NAME = "try-times";
    private static final String FAILURES_HISTOGRAM_BASE_NAME = "failures";
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Random random = new Random();

    private Scientist() {}

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
