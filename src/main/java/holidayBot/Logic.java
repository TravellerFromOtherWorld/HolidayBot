package holidayBot;

import java.util.Random;

public class Logic {
    private int command = 0;
    private String[] byePhrase = new String[3];
    MessageFromBot message = new MessageFromBot();
    private WorkWithClient client = new WorkWithClient();
    private WorkWithHoliday holiday = new WorkWithHoliday();
    final int HELP = 1;
    final int EXIT = 2;
    final int ENTER = 3;
    final int REGISTER = 4;
    final int ADDHOLIDAY = 5;

    Logic()
    {
        byePhrase[0] = "Да прибудет с тобой сила!";
        byePhrase[1] = "I'll be back!";
        byePhrase[2] = "Мы разойдёмся как в море корабли!";
    }

    public boolean cantDetect(String request)//функция определяет, что именно хочет сделать пользователь
    {

        if (request.equals("help") || request.equals("Help")) {
            command = HELP;
            return false;
        }
        if (request.equals("вход") || request.equals("Вход")){
            command = ENTER;
            return false;
        }
        if (request.equals("Регистрация") || request.equals("регистрация")){
            command = REGISTER;
            return false;
        }
        if (request.equals("Пока") || request.equals("пока")) {
            command = EXIT;
            return false;
        }
        if (request.equals("Добавить праздник") || request.equals("добавить праздник")) {
            command = ADDHOLIDAY;
            return false;
        }

        return true;
    }

    public MessageFromBot work()
    {
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

    private MessageFromBot help()
    {
        String answer = """
        Приветствую тебя! Я бот-помощник-с-праздниками!
        Я буду напоминать тебе о праздниках, могу сообщить какой праздник сегодня.
        Также ты сможешь добавить свои праздники, например, дни рождения.
        Чтобы ещё раз запросить информацию, напиши: "help"
        Чтобы закончить со мной общение, напиши: "Пока"
        Чтобы войти в систему, напиши: "Вход"
        Чтобы зарегестрироваться, напиши: "Регистрация"
        Чтобы добавить праздник, напиши: "Добавить праздник"
        А теперь, чего ты хочешь?""";
        message.setMessage(answer);
        exit();
        return message;
    }

    private MessageFromBot clientAuthentication()
    {
        message.setAuthentication(true);
        exit();
        return message;
    }

    public MessageFromBot clientAuthentication(String login, String password)//функция проводит либо регистрацию, либо вход и возврашает сообщение
    {                                                                        //типа MessageFromBot, сообщая результат
        if (login != "" && password != "")
        {

            if (client.getAuthenticationStatus())
            {
                message.setMessage("""
                Ты уже используешь свою учётную запись.
                Чтобы зарегестрирвать другой аккаунт, выйди из системы.
                Что будем делать дальше?""");
                message.setAuthentication(false);
                return message;
            }
            if (command == ENTER)
            {
                message = client.tryEnter(login, password);
            }
            if (command == REGISTER)
            {
                message = client.tryRegister(login, password);
            }
            if (message.getErrors() != 0)
            {
                message.setAuthentication(false);
                return message;
            }
            if (client.getAuthenticationStatus()) {
                message.addMessage(holiday.remindHoliday(client.getNickname(), client.getPassword()).getMessage());
                message.addMessage("Что ты хочешь сделать дальше?");
            }
        }
        else
            message.setMessage("Заполните, пожалуйста, оба поля!");
        exit();
        return message;
    }

    private MessageFromBot newHoliday()
    {
        if (client.getAuthenticationStatus())
        {
            message.setMessage("Хорошо, давай начнём.");
            message.setNewHoliday(true);
        }
        else
        {
           message.setMessage("Чтобы добавить праздник, войди в аккаунт.");
        }
        return message;
    }

    public MessageFromBot newHoliday(String date, String name)
    {
        if (date != "" && name != "")
        {
            message = holiday.addNewHoliday(date, name,client.getNickname(), client.getPassword());
            if (message.getErrors() == 0)
            {
                message.addMessage("Что ты хочешь сделать дальше?");
            }
        }
        else
            message.setMessage("Заполните, пожалуйста, оба поля!");
        exit();
        return message;
    }

    private MessageFromBot exit()
    {
        if (command == EXIT) {
            Random r = new Random();
            String phrase = (byePhrase[r.nextInt(3)]);
            message.setMessage(phrase);
            message.setExit(true);
            return message;
        }
        message.setExit(false);
        return message;
    }
}
