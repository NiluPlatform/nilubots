package tech.nilu.bots.util;

import java.util.concurrent.Callable;

/**
 * Created by mmariameda on 3/29/17.
 */
public class ExceptionHelper {
    public static RuntimeException runtime(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }

        return new RuntimeException(e);
    }

    // this is a new one, n/a in public libs
    // Callable just suits as a functional interface in JDK throwing Exception
    public static <V> V propagate(Callable<V> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw runtime(e);
        }
    }

    public static void handleException(Runnable callable) {
        try {
            callable.run();
        } catch (Exception e) {
            throw runtime(e);
        }
    }
}
