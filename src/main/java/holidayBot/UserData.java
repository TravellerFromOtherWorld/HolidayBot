package holidayBot;

public class UserData {
    private boolean setNickname = false;
    private boolean setPassword = false;
    private boolean getDate = false;
    private boolean getName = false;
    private String nickname = "";
    private String date = "";

    public void setNickname(boolean data) {
        setNickname = data;
    }

    public boolean getNickname() {
        return setNickname;
    }

    public void setPassword(boolean data) {
        setPassword = data;
    }

    public boolean getPassword() {
        return setPassword;
    }

    public void setDate(boolean data) {
        getDate = data;
    }

    public boolean getDate() {
        return getDate;
    }

    public void setName(boolean data) {
        getName = data;
    }

    public boolean getName() {
        return getName;
    }

    public void saveUserNickname(String data) {
        nickname = data;
    }

    public String getUserNickname() {
        return nickname;
    }

    public void saveUserDate(String data) {
        date = data;
    }

    public String getUserDate() {
        return date;
    }
}
