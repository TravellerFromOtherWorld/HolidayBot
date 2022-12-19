package holidayBot;

import java.util.Objects;
import java.util.Random;

public class Logic {
    private int command = 0;
    private String[] byePhrase = new String[3];
    private MessageFromBot message = new MessageFromBot();
    private WorkWithClient client = new WorkWithClient();
    private WorkWithHoliday holiday = new WorkWithHoliday();
    final int HELP = 1;
    final int EXIT = 2;
    final int ENTER = 3;
    final int REGISTER = 4;
    final int ADDHOLIDAY = 5;

    Logic() {
        byePhrase[0] = "Да прибудет с тобой сила!";
        byePhrase[1] = "I'll be back!";
        byePhrase[2] = "Мы разойдёмся как в море корабли!";
    }

    //функция определяет, что именно хочет сделать пользователь
    public boolean cantDetect(String request) {

        if (request.equals("/help")) {
            command = HELP;
            return false;
        }
        if (request.equals("/entry")) {
            command = ENTER;
            return false;
        }
        if (request.equals("/register")) {
            command = REGISTER;
            return false;
        }
        if (request.equals("/bye")) {
            command = EXIT;
            return false;
        }
        if (request.equals("/addHoliday")) {
            command = ADDHOLIDAY;
            return false;
        }

        return true;
    }

    public MessageFromBot work(UserData user) {
        switch (command) {
            case HELP:
                return help();
            case ENTER:
            case REGISTER:
                return clientAuthentication();
            case ADDHOLIDAY:
                return newHolidayIn(user);
            default:
                return exit();
        }
    }

    private MessageFromBot help() {
        String answer = """
                Приветствую тебя! Я бот-помощник-с-праздниками!
                Я буду напоминать тебе о праздниках, могу сообщить какой праздник сегодня.
                Также ты сможешь добавить свои праздники, например, дни рождения.
                Чтобы ещё раз запросить информацию, напиши: "/help"
                Чтобы закончить со мной общение, напиши: "/bye"
                Чтобы войти в систему, напиши: "/entry"
                Чтобы зарегистрироваться, напиши: "/register"
                Чтобы добавить праздник, напиши: "/addHoliday"
                А теперь, чего ты хочешь?""";
        message.setMessage(answer);
        exit();
        return message;
    }

    private MessageFromBot clientAuthentication() {
        message.setAuthentication(true);
        exit();
        return message;
    }

    //функция проводит либо регистрацию, либо вход и возврашает сообщение типа MessageFromBot, сообщая результат
    public MessageFromBot clientAuthentication(UserData user) {
        if (!Objects.equals(user.getUserNickname(), "") && !Objects.equals(user.getUserPassword(), "")) {

            if (user.getStatus()) {
                message.setMessage("""
                        Ты уже используешь свою учётную запись.
                        Чтобы зарегистрировать другой аккаунт, выйди из системы.
                        Что будем делать дальше?""");
                message.setAuthentication(false);
                return message;
            }
            if (command == ENTER) {
                message = client.tryEnter(user);
            }
            if (command == REGISTER) {
                message = client.tryRegister(user);
            }
            if (message.getErrors() != 0) {
                message.setAuthentication(false);
                return message;
            }
            if (user.getStatus()) {
                message.addMessage(holiday.remindHoliday(user.getUserNickname(), user.getUserPassword(), user.getDayLastAuth()).getMessage());
                message.addMessage("Что ты хочешь сделать дальше?");
            }
        } else
            message.setMessage("Заполните, пожалуйста, оба поля!");
        exit();
        return message;
    }

    private MessageFromBot newHolidayIn(UserData user) {
        if (user.getStatus()) {
            message.setMessage("Хорошо, давай начнём.");
            message.setNewHoliday(true);
        } else {
            message.setMessage("Чтобы добавить праздник, войди в аккаунт.");
        }
        return message;
    }

    public MessageFromBot newHoliday(UserData user) {
        if (!(Objects.equals(user.getUserDate(), "")) && !(Objects.equals(user.getNameOfHoliday(), ""))) {
            message = holiday.addNewHoliday(user.getUserDate(), user.getNameOfHoliday(), user.getUserNickname(), user.getUserPassword());
            if (message.getErrors() == 0) {
                message.addMessage("Что ты хочешь сделать дальше?");
            }
        } else
            message.setMessage("Заполните, пожалуйста, оба поля!");
        exit();
        return message;
    }

    private MessageFromBot exit() {
        if (command == EXIT) {
            Random r = new Random();
            String phrase = (byePhrase[r.nextInt(3)]);
            message.cleanMessage();
            message.setMessage(phrase);
            message.setExit(true);
            return message;
        }
        message.setExit(false);
        return message;
    }

    public void clean() {
        holiday.exit();
        message.cleanMessage();
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int task){
        command = task;
    }
}
