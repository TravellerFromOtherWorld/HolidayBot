package holidayBot;

import java.io.IOException;

public class MessageFromBot {
    private String message;
    private boolean exit;
    private boolean authentication = false;
    private boolean newHoliday = false;
    private int errors = 0;

    public void setMessage(String text) {
        message = text;
    }

    public void setNewHoliday(boolean holiday) {
        newHoliday = holiday;
    }

    public boolean getNewHoliday() {
        return newHoliday;
    }

    public void setErrors(int ex) {
        errors = ex;
    }

    public int getErrors() {
        return errors;
    }

    public void addMessage(String newText) {
        if (message == null)
            setMessage(newText);
        else
            message = message + '\n' + newText;
    }

    public void setAuthentication(boolean auth) {
        authentication = auth;
    }

    public void setExit(boolean e) {
        exit = e;
    }

    public String getMessage() {
        return message;
    }

    public boolean getAuthentication() {
        return authentication;
    }

    public boolean toExit() {
        return exit;
    }

    public boolean isEmpty() {
        return message == null;
    }

    public void cleanMessage() {
        message = null;
        exit = false;
        newHoliday = false;
        authentication = false;
    }

    public void copy(MessageFromBot newMessage) {
        message = newMessage.getMessage();
        authentication = newMessage.getAuthentication();
        exit = newMessage.toExit();
        newHoliday = newMessage.getNewHoliday();
        errors = newMessage.getErrors();
    }
}
