package holidayBot;

import java.util.Random;

public class Logic {
    private int command = 0;
    private String[] byePhrase = new String[3];
    MessageFromBot message = new MessageFromBot();
    final int HELP = 1;
    final int EXIT = 2;

    Logic() {
        byePhrase[0] = "Да прибудет с тобой сила!";
        byePhrase[1] = "I'll be back!";
        byePhrase[2] = "Мы разойдёмся как в море корабли!";
    }

    public boolean cantDetect(String request) {

        if (request.equals("help") || request.equals("Help")) {
            command = HELP;
            return false;
        }
        if (request.equals("Пока") || request.equals("пока")) {
            command = EXIT;
            return false;
        }

        return true;
    }

    public MessageFromBot work() {
        switch (command) {
            case HELP:
                return help();
            default:
                return exit();
        }
    }

    private MessageFromBot help() {
        String answer = """
        Приветствую тебя! Я бот-помощник-с-праздниками!
        Я буду напоминать тебе о праздниках, могу сообщить какой праздник сегодня.
        Также ты сможешь добавить свои праздники, например, дни рождения.
        Чтобы ещё раз запросить информацию, напиши: help
        Чтобы закончить со мной общение, напиши: Пока
        А теперь, чего ты хочешь?""";
        message.setMessage(answer);
        exit();
        return message;
    }

    private MessageFromBot exit() {
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
