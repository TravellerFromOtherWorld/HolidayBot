package holidayBot;

import java.time.LocalDate;

public class Storage {
    private LocalDate date;
    private String nickname;
    private String password;
    private String nameHoliday;

    public Storage(String nick, String pass, LocalDate day, String holiday) {
        this.date = day;
        this.nickname = nick;
        this.password = pass;
        this.nameHoliday = holiday;
    }

    public LocalDate getDate(){
        return date;
    }

    public String getNickname(){
        return nickname;
    }

    public String getPassword(){
        return password;
    }

    public String getNameHoliday(){
        return nameHoliday;
    }
}
