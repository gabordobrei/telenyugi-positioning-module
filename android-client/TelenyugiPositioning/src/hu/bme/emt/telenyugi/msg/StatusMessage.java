
package hu.bme.emt.telenyugi.msg;

public class StatusMessage {

    private Status status;
    private String message;

    public StatusMessage() {
    }

    public StatusMessage(final Status status) {
        this(status, null);
    }

    public StatusMessage(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public static enum Status {
        OK(200), SERVER_ERROR(500), CLIENT_ERROR(400);

        public int statusCode;

        private Status(int status) {
            this.statusCode = status;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isOk() {
        return status != null && status.equals(Status.OK);
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
