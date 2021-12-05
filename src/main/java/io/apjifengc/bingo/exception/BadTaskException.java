package io.apjifengc.bingo.exception;

import io.apjifengc.bingo.api.game.task.BingoTask;

public class BadTaskException extends Exception {

    public BadTaskException() {
        super("");
    }

    public BadTaskException(Throwable e) {
        super(e);
    }

    public BadTaskException(String msg) {
        super(msg);
    }

    public BadTaskException(String msg, Throwable e) {
        super(msg, e);
    }

    public BadTaskException(BingoTask task) {
        super(String.format("Task '%s' didn't work properly.", task));
    }

    public BadTaskException(BingoTask task, Throwable e) {
        super(String.format("Task '%s' didn't work properly.", task), e);
    }

    public BadTaskException(BingoTask task, String msg) {
        super(String.format("Task '%s' didn't work properly - %s", task, msg));
    }

    public BadTaskException(BingoTask task, String msg, Throwable e) {
        super(String.format("Task '%s' didn't work properly - %s", task, msg), e);
    }

}
