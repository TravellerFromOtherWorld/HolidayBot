package holidayBot;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkWithClient {
    private MessageFromBot answer = new MessageFromBot();
    private String nickname;
    private String password;
    private boolean authenticationStatus = false;
    private LocalDate dayLastAuth;
    private WorkWithFiles fileWorker = new WorkWithFiles();
    final int fileError = 1;

    public boolean getAuthenticationStatus() {
        return authenticationStatus;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
    public LocalDate getDayLastAuth(){
        return dayLastAuth;
    }

    //пользователь пытается войти в систему с логином и паролем.
    public MessageFromBot tryEnter(String login, String pass) {
        Storage user = findUser(login, pass);
        if (user != null) {
            answer.setMessage("Привет! Давно не виделись)");
            nickname = login;
            password = pass;
            dayLastAuth = user.getDate();
            authenticationStatus = true;
            answer.setAuthentication(false);
            try{
                fileWorker.rewriteAllFile(user, "Users.txt");
            } catch (IOException ex){
                answer.setMessage("Произошла ошибка");
                answer.setErrors(fileError);
                return answer;
            }

        } else if (answer.getErrors() == 0) {
            answer.setMessage("""
                    Ты не достоин!
                    Приплыли... Что дальше?""");
        }
        return answer;
    }

    //пользователь пытается зарегистрироваться в системе, вводя логин и пароль
    public MessageFromBot tryRegister(String login, String pass) {
        if (findUser(login, pass) != null) {
            if (answer.getErrors() == 0) {
                answer.setMessage("Придумай другой логин и пароль");
            }
            return answer;
        }
        addNewUser(login, pass);
        if (answer.getErrors() == 0) {
            answer.setMessage("Регистрация прошла упешно!");
            nickname = login;
            password = pass;
            dayLastAuth = LocalDate.now();
            answer.setAuthentication(false);
            authenticationStatus = true;
        }
        return answer;
    }

    //бот ищет пользователя в базе данных
    private Storage findUser(String login, String pass) {
        List<Storage> userData = new ArrayList<Storage>();
        try {
            userData = fileWorker.getDataFromFile("Users.txt");
        } catch (IOException e) {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
            return null;
        }
        if (userData.isEmpty())
            return null;
        for (Storage element : userData) {
            if (Objects.equals(login, element.getNickname()) && Objects.equals(pass, element.getPassword())){
                return element;
            }
        }
        return null;
    }

    private void addNewUser(String login, String pass) {
        LocalDate date = LocalDate.now();
        String userData = login + ':' + pass + ':' + date;
        try {
            fileWorker.writeDataToTheFile(userData, "Users.txt");
        }
        catch (IOException e) {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
    }
}
