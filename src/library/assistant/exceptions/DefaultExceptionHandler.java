package library.assistant.exceptions;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(DefaultExceptionHandler.class.getName());

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        LOGGER.log(Level.ERROR, "Exception occurred ()", throwable);
    }
}
