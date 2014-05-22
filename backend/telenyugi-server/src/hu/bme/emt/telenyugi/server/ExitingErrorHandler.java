package hu.bme.emt.telenyugi.server;

public class ExitingErrorHandler implements ErrorHandler{

    @Override
    public boolean handleError(Exception exception) {
        System.out.println("Error: " + exception.getMessage());
        System.exit(-1);
        return true;
    }

}
