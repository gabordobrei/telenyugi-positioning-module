package hu.bme.emt.telenyugi.server;

public interface ErrorHandler {

    boolean handleError(Exception exception);
}
