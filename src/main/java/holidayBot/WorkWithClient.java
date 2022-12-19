package holidayBot;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkWithClient {
    private MessageFromBot answer = new MessageFromBot();
    private WorkWithFiles fileWorker = new WorkWithFiles("Users.txt");
    final int fileError = 1;

    //пользователь пытается войти в систему с логином и паролем.
    public MessageFromBot tryEnter(UserData userStatus) {
        Storage user = findUser(userStatus.getUserNickname(), userStatus.getUserPassword());
        if (user != null) {
            answer.setMessage("Привет! Давно не виделись)");
            userStatus.saveDayLastAuth(user.getDate());
            userStatus.setStatus(true);
            answer.setAuthentication(false);
            try {
                fileWorker.rewriteAllFile(user);
            } catch (IOException ex) {
                answer.setMessage("Произошла ошибка");
                answer.setErrors(fileError);
                return answer;
            }

        } else if (answer.getErrors() == 0) {
            answer.setMessage("""
                    Ты не достоин!
                    Приплыли... Что дальше?""");
            userStatus.saveUserNickname("");
            userStatus.saveUserPassword("");
        }
        return answer;
    }

    //пользователь пытается зарегистрироваться в системе, вводя логин и пароль
    public MessageFromBot tryRegister(UserData userStatus) {
        if (findUser(userStatus.getUserNickname(), userStatus.getUserPassword()) != null) {
            if (answer.getErrors() == 0) {
                answer.setMessage("Придумай другой логин и пароль");
            }
            return answer;
        }
        addNewUser(userStatus.getUserNickname(), userStatus.getUserPassword());
        if (answer.getErrors() == 0) {
            answer.setMessage("Регистрация прошла упешно!");
            userStatus.saveDayLastAuth(LocalDate.now());
            answer.setAuthentication(false);
            userStatus.setStatus(true);
        }
        return answer;
    }

    //бот ищет пользователя в базе данных
    private Storage findUser(String login, String pass) {
        List<Storage> userData = new ArrayList<Storage>();
        try {
            userData = fileWorker.getDataFromFile();
        } catch (IOException e) {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
            return null;
        }
        if (userData.isEmpty())
            return null;
        for (Storage element : userData) {
            if (Objects.equals(login, element.getNickname()) && Objects.equals(pass, element.getPassword())) {
                return element;
            }
        }
        return null;
    }

    private void addNewUser(String login, String pass) {
        LocalDate date = LocalDate.now();
        String userData = login + ':' + pass + ':' + date + "\n";
        try {
            fileWorker.writeDataToTheFile(userData);
        } catch (IOException e) {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
    }
}
