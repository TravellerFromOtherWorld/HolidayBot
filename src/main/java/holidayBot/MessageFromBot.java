package holidayBot;

public class MessageFromBot {
    private String message;
    private boolean exit;

    public void setMessage(String text) {
        message = text;
    }

    public void setExit(boolean e) {
        exit = e;
    }

    public String getMessage() {
        return message;
    }

    public boolean toExit() {
        return exit;
    }
}
