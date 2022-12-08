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

    public MessageFromBot work() {
        switch (command) {
            case HELP:
                return help();
            case ENTER:
            case REGISTER:
                return clientAuthentication();
            case ADDHOLIDAY:
                return newHoliday();
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
                Чтобы зарегестрироваться, напиши: "/register"
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
    public MessageFromBot clientAuthentication(String login, String password) {
        if (!Objects.equals(login, "") && !Objects.equals(password, "")) {

            if (client.getAuthenticationStatus()) {
                message.setMessage("""
                        Ты уже используешь свою учётную запись.
                        Чтобы зарегестрирвать другой аккаунт, выйди из системы.
                        Что будем делать дальше?""");
                message.setAuthentication(false);
                return message;
            }
            if (command == ENTER) {
                message = client.tryEnter(login, password);
            }
            if (command == REGISTER) {
                message = client.tryRegister(login, password);
            }
            if (message.getErrors() != 0) {
                message.setAuthentication(false);
                return message;
            }
            if (client.getAuthenticationStatus()) {
                message.addMessage(holiday.remindHoliday(client.getNickname(), client.getPassword(), client.getDayLastAuth()).getMessage());
                message.addMessage("Что ты хочешь сделать дальше?");
            }
        } else
            message.setMessage("Заполните, пожалуйста, оба поля!");
        exit();
        return message;
    }

    private MessageFromBot newHoliday() {
        if (client.getAuthenticationStatus()) {
            message.setMessage("Хорошо, давай начнём.");
            message.setNewHoliday(true);
        } else {
            message.setMessage("Чтобы добавить праздник, войди в аккаунт.");
        }
        return message;
    }

    public MessageFromBot newHoliday(String date, String name) {
        if (!(Objects.equals(date, "")) && !(Objects.equals(name, ""))) {
            message = holiday.addNewHoliday(date, name, client.getNickname(), client.getPassword());
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
            client.exit();
            return message;
        }
        message.setExit(false);
        return message;
    }
}
