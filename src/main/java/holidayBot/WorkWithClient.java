package holidayBot;

import java.io.*;

public class WorkWithClient {
    private MessageFromBot answer = new MessageFromBot();
    private String nickname;
    private String password;
    private boolean authenticationStatus = false;
    final int fileError = 1;

    public boolean getAuthenticationStatus()
    {
        return authenticationStatus;
    }

    public String getPassword()
    {
        return password;
    }

    public String getNickname()
    {
        return nickname;
    }
    public MessageFromBot tryEnter(String login, String pass)
    {
        if (findUser(login, pass))
        {
            answer.setMessage("Привет! Давно не виделись)");
            nickname = login;
            password = pass;
            authenticationStatus = true;
            answer.setAuthentication(false);
        }
        else if (answer.getErrors() == 0) {
            answer.setMessage("Ты не достоин!");
        }
        return answer;
    }

    public MessageFromBot tryRegister(String login, String pass)
    {
        if (findUser(login, pass))
        {
            if (answer.getErrors() == 0)
            {
                answer.setMessage("Придумай другой логин и пароль");
            }
            return answer;
        }
        addNewUser(login, pass);
        if (answer.getErrors() == 0) {
            answer.setMessage("Регистрация прошла упешно!");
            answer.setAuthentication(false);
            authenticationStatus = true;
        }
        return answer;
    }

    private boolean findUser(String login, String pass)
    {
        try (BufferedReader fileReader = new BufferedReader(new FileReader("Users.txt")))
        {
            String user = fileReader.readLine();
            while (user != null)
            {
                String[] userData = user.split(" ");
                if (login.equals(userData[0]) && pass.equals(userData[1]))
                {
                    return true;
                }
                user = fileReader.readLine();
            }
        }
        catch (IOException e)
        {
            answer.setMessage("Произошла ошибка");
            answer.setErrors(fileError);
        }
        return false;
    }

    private void addNewUser(String login, String pass)
    {
        try (FileWriter writer = new FileWriter("Users.txt", true)) {
            String userData = login + ' ' + pass;
            writer.write(userData);
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
