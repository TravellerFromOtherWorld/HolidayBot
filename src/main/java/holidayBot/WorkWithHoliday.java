package holidayBot;

import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class WorkWithHoliday {
    private final int fileError = 1;

    private MessageFromBot answer = new MessageFromBot();
    private WorkWithFiles fileWorker = new WorkWithFiles();

    public boolean correctDate(String date) {
        if (date.matches("^[0-9]{4}-(((0[13578]|(10|12))-(0[1-9]|[1-2][0-9]|3[0-1]))|(02-(0[1-9]|[1-2][0-9]))|((0[469]|11)-(0[1-9]|[1-2][0-9]|30)))$"))
            return true;
        return false;
    }

    //метод добавления нового праздника конкретного пользователя
    public MessageFromBot addNewHoliday(String date, String name, String nickname, String password) {
        if (correctDate(date)) {
            addHoliday(date, name, nickname, password);
            if (answer.getErrors() == 0)
                answer.setMessage("Праздник добавлен!");
        } else {
            answer.setMessage("Дата введена некорректно!");
        }
        answer.setNewHoliday(false);
        return answer;
    }

    public MessageFromBot remindHoliday(String nickname, String password, LocalDate dateOfLastAuth) {
        try {
            List<Storage> holidayStorage = fileWorker.getDataFromFile("Holidays.txt");
            if (!(holidayStorage.isEmpty()))
            {
                for (Storage element : holidayStorage){
                    if (Objects.equals(nickname, element.getNickname()) && Objects.equals(password, element.getPassword())){
                        if (element.getDate().isAfter(dateOfLastAuth)  && element.getDate().isBefore(LocalDate.now())){
                            answer.addMessage("Не забудь " + element.getDate().getDayOfMonth() + " " + element.getDate().getMonth() + " - " + element.getNameHoliday() + " :)");
                            continue;
                        }
                        if (element.getDate().isEqual(LocalDate.now()))
                            answer.addMessage("Не забудь " + element.getDate().getDayOfMonth() + " " + element.getDate().getMonth() + " - " + element.getNameHoliday() + " :)");
                    }
                }
            }
        } catch (IOException e){
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
        if (answer.isEmpty())
            answer.setMessage("Ты ничего не пропустил :)");
        return answer;
    }

    //приватный метод для добавления праздника
    private void addHoliday(String date, String name, String nickname, String password) {
        try {
            String holiday = nickname + ':' + password + ':' + date + ':' + name;
            fileWorker.writeDataToTheFile(holiday, "Holidays.txt");
        } catch (IOException e) {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
    }
}
