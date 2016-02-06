package pl.spc.scientist;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Arrays;
import java.util.Random;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;

import pl.spc.scientist.function.CheckedSupplier;

public final class ScientistWithExceptions {
    private static final String USE_TIMER_BASE_NAME = "ex-use-times";
    private static final String TRY_TIMER_BASE_NAME = "ex-try-times";
    private static final String FAILURES_HISTOGRAM_BASE_NAME = "ex-failures";
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Random random = new Random();

    private ScientistWithExceptions() {}

    public static <T>T run(String name, CheckedSupplier<T> use, CheckedSupplier<T> test) throws Exception {
        Histogram failures = metrics.histogram(name(FAILURES_HISTOGRAM_BASE_NAME, name));
        Timer useTimer = metrics.timer(name(USE_TIMER_BASE_NAME, name));
        Timer testTimer = metrics.timer(name(TRY_TIMER_BASE_NAME, name));

        final RunResult<T> useResult;
        final RunResult<T> testResult;

        if (random.nextBoolean()) {
            useResult = executeRun(use, useTimer);
            testResult = executeRun(test, testTimer);
        } else {
            testResult = executeRun(test, testTimer);
            useResult = executeRun(use, useTimer);
        }

        failures.update(isSameResult(useResult.runResult, testResult.runResult, useResult.runException, testResult.runException) ? 0 : 1);

        if (useResult.runException != null) {
            throw useResult.runException;
        }

        return useResult.runResult;
    }

    private static <T>RunResult<T> executeRun(CheckedSupplier<T> function, Timer timer) {
        RunResult<T> result = new RunResult<>();
        Context time = timer.time();

        try {
            result.runResult = function.get();
        } catch (Exception e) {
            result.runException = e;
        } finally {
            time.stop();
        }

        return result;
    }

    private static <T>boolean isSameResult(T useResult, T testResult, Exception useException, Exception testException) {
        return ((useResult == null && testResult == null) ||
                    (useResult != null && testResult != null && useResult.equals(testResult))) &&
                isSameException(useException, testException);
    }

    private static boolean isSameException(Exception useException, Exception testException) {
        return (useException == null && testException == null) ||
                (useException != null && testException != null && useException.getClass().isInstance(testException) &&
                    Arrays.deepEquals(useException.getStackTrace(), testException.getStackTrace()) &&
                isSameString(useException.getMessage(), testException.getMessage()));
    }

    private static boolean isSameString(String use, String test) {
        return (use == null && test == null) || (use != null && test != null && use.equals(test));
    }

    private static class RunResult<T> {
        private T runResult = null;
        private Exception runException = null;
    }
}
