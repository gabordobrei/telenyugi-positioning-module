
package hu.bme.emt.telenyugi.server.msg;

public class StatusMessage {

    private final Status status;
    private String message;

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

        public StatusMessage createMessage(String message) {
            return new StatusMessage(this, message);
        }

        public StatusMessage createMessage() {
            return createMessage(null);
        }
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("StatusMessage [status=");
        builder.append(status);
        builder.append(", message=");
        builder.append(message);
        builder.append("]");
        return builder.toString();
    }

}
