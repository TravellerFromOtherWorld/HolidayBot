package holidayBot;

import java.util.Random;

public class Logic {
    private int command = 0;
    private String[] byePhrase = new String[3];

    Logic() {
        byePhrase[0] = "Да прибудет с тобой сила!";
        byePhrase[1] = "I'll be back!";
        byePhrase[2] = "Мы разойдёмся как в море корабли!";
    }

    public boolean cantDetect(String request) {

        if (request.equals("help") || request.equals("Help")) {
            command = 1;
            return false;
        }
        if (request.equals("Пока") || request.equals("пока")) {
            command = 2;
            return false;
        }

        return true;
    }

    public boolean work() {
        switch (command) {
            case 1:
                return help();
            default:
                return exit();
        }
    }

    private boolean help() {
        System.out.println("Приветствую тебя! Я бот-помощник-с-праздниками! +" +
        " Я буду напоминать тебе о праздниках, могу сообщить какой праздник сегодня." +
        "\n" +
        "Также ты сможешь добавить свои праздники, например, дни рождения." +
        "\n" +
        "Чтобы ещё раз запросить информацию, напиши: help" +
        "\n" +
        "Чтобы закончить со мной общение, напиши: Пока" +
        "\n" +
        "А теперь, чего ты хочешь?");
        return exit();
    }

    private boolean exit() {
        if (command == 2) {
            Random r = new Random();
            System.out.println(byePhrase[r.nextInt(3)]);
            return true;
        }
        return false;
    }
}
