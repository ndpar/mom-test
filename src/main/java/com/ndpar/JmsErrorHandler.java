package com.ndpar;

import org.springframework.util.ErrorHandler;

public class JmsErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        t.printStackTrace();
    }
}
