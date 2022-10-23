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
        while (true) {
            Scanner input = new Scanner(System.in);
            String request = input.nextLine(); //здесь запрос от пользователя

            if ( result.cantDetect(request) ) { //здесь распознаётся запрос
                System.out.println("Ай карамба! Ты ошибся, попробуй ещё раз!");
                continue;
            }

            answer = result.work();
            System.out.println(answer.getMessage());
            if ( answer.toExit() ) { //здесь решается, надо ли прекратить общение
                break;
            }
        }
    }
}