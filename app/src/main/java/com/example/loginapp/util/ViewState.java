package com.example.loginapp.util;

public interface ViewState<T> {
    class Loading<T> implements ViewState<T> {
    }

    class Success<T> implements ViewState<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    class Failure<T> implements ViewState<T> {
        private final Exception exception;

        public Failure(Exception exception) {
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }
}
