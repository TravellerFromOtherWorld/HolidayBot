package holidayBot;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkWithHoliday {
    final int fileError = 1;

    MessageFromBot answer = new MessageFromBot();

    public boolean correctDate(String date)
    {
        if (date.matches("^(?:(?:31(\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$"))
            return true;
        return false;
    }

    public MessageFromBot addNewHoliday(String date, String name,String nickname, String password) //метод добавления нового праздника конкретного пользователя
    {
        if (correctDate(date))
        {
            addHoliday(date, name,nickname, password);
            if (answer.getErrors() == 0)
                answer.setMessage("Праздник добавлен!");
        }
        else
        {
            answer.setMessage("Дата введена некорректно!");
        }
        answer.setNewHoliday(false);
        return answer;
    }

    public MessageFromBot remindHoliday(String nickname, String password) //метод считывает файлы Holidays.txt и MarkedHolidays.txt и сравнивает их содержимое. если обнаруживает невыведенный праздник, он напоминает о нём.
    {
        List<String> markHoliday = new ArrayList<String>();
        try (BufferedReader holidayReader = new BufferedReader(new FileReader("Holidays.txt"));
             BufferedReader mHolidayReader = new BufferedReader(new FileReader("MarkedHolidays.txt")))
        {
            String holiday = holidayReader.readLine();
            while (holiday != null)
            {
                String[] holidayData = holiday.split(":");
                if (nickname.equals(holidayData[2]) && password.equals(holidayData[3]))
                {
                    LocalDate date = LocalDate.now();
                    String[] dateParts = holidayData[0].split("\\.");
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    int year = Integer.parseInt(dateParts[2]);
                    LocalDate holidayDate = LocalDate.of(year, month, day);

                    if (holidayDate.isEqual(date))
                    {
                        String markedHoliday = mHolidayReader.readLine();
                        while (markedHoliday != null)
                        {
                            if (markedHoliday.equals(holiday))
                                break;
                            markedHoliday = mHolidayReader.readLine();
                        }
                        if (markedHoliday == null)
                        {
                            answer.addMessage("Не забудь, сегодня " + holidayData[1]);
                            markHoliday.add(holiday);
                        }
                    }
                }
                holiday = holidayReader.readLine();
            }
        }
        catch (IOException e)
        {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
        try(FileWriter writer = new FileWriter("MarkedHolidays.txt", true))
        {
            for(String holiday : markHoliday)
            {
                writer.write(holiday);
                writer.append('\n');
            }
            writer.flush();
        }
        catch (IOException e)
        {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
        if (answer.isEmpty())
            answer.setMessage("Ты ничего не пропустил)");
        return answer;
    }

    private void addHoliday(String date, String name,String nickname, String password) //приватный метод для добавления праздника
    {
        try (FileWriter writer = new FileWriter("Holidays.txt", true)) {
            String holiday = date + ':' + name + ':' + nickname + ':' + password;
            writer.write(holiday);
            writer.append('\n');
            writer.flush();
        }
        catch (IOException e)
        {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
    }
}
