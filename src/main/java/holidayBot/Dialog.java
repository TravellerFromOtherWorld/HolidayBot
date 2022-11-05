package holidayBot;

import java.util.Scanner;

public class Dialog {
    public static void main(String[] args) {
        System.out.println("""
                Привет, мешок с костями!
                Что ты хочешь?
                Если тебе нужно больше информации, напиши help)""");

        Logic result = new Logic();
        MessageFromBot answer = new MessageFromBot(); //здесь в итоге ответ от бота
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                String request = input.nextLine(); //здесь запрос от пользователя

                if (result.cantDetect(request)) { //здесь распознаётся запрос
                    System.out.println("Ай карамба! Ты ошибся, попробуй ещё раз!");
                    continue;
                }

                answer = result.work();

                if (answer.getAuthentication() == false) {
                    System.out.println(answer.getMessage());
                }

                while (answer.getAuthentication()) {
                    System.out.println("Введите логин:");
                    String nickname = input.nextLine();
                    System.out.println("Введите пароль:");
                    String password = input.nextLine();
                    answer = result.clientAuthentication(nickname, password);
                    System.out.println(answer.getMessage());
                }

                if (answer.getNewHoliday())
                {
                    System.out.println("Введи дату праздника в виде 01.01.2000:");
                    String date = input.nextLine();
                    System.out.println("Введите название праздника:");
                    String name = input.nextLine();
                    answer = result.newHoliday(date, name);
                    System.out.println(answer.getMessage());
                }

                if (answer.toExit()) { //здесь решается, надо ли прекратить общение
                    break;
                }
            }
        }
    }
}