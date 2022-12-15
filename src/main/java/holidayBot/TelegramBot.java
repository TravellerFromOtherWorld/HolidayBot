package holidayBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TelegramBot extends TelegramLongPollingBot {
    private Logic logic = new Logic();
    private MessageFromBot answer = new MessageFromBot();
    private UserData client;
    public HashMap<Long, UserData> UserStatus = new HashMap<>();

    @Override
    public String getBotUsername() {
        return Objects.toString(System.getenv("NAME"));
    }

    @Override
    public String getBotToken() {
        return System.getenv("TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        logic.clean();

        if (update.hasMessage()) {
            var msg = update.getMessage().getText();
            var user = update.getMessage().getChatId();
            if (!UserStatus.containsKey(user)) {
                client = new UserData();
                answer = client.getSaveAnswer();
                UserStatus.put(user, client);
            } else {
                client = UserStatus.get(user);
                answer = client.getSaveAnswer();
                logic.rebuildClient(client.getUserNickname(), client.getUserPassword(), client.getStatus(), answer);
            }
            detectCommand(msg, user, update.getMessage().isCommand());

        } else if (update.hasCallbackQuery()) {
            var msg = update.getCallbackQuery().getData();
            var user = update.getCallbackQuery().getMessage().getChatId();
            if (!UserStatus.containsKey(user)) {
                client = new UserData();
                answer = client.getSaveAnswer();
                UserStatus.put(user, client);
            } else {
                client = UserStatus.get(user);
                answer = client.getSaveAnswer();
                logic.rebuildClient(client.getUserNickname(), client.getUserPassword(), client.getStatus(), answer);

            }
            detectCommand(msg, user, true);
        }
    }

    private void detectCommand(String msg, Long user, boolean command) {
        if (command) {
            if (Objects.equals(msg, "/start")) {
                String text = """
                        Привет, мешок с костями!
                        Что ты хочешь?
                        Если тебе нужно больше информации, напиши help)""";
                sendText(user, text, true);
                return;
            }

            if (logic.cantDetect(msg)) { //здесь распознаётся запрос
                sendText(user, "Ай карамба! Ты ошибся, попробуй ещё раз!", false);
            } else {
                answer = logic.work();

                if (!answer.getAuthentication()) {
                    if (answer.getNewHoliday()) {
                        addHoliday(user, answer.getMessage());
                        saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
                        return;
                    }
                    if (answer.toExit()) {
                        sendText(user, answer.getMessage(), false);
                        answer.cleanMessage();
                        logic.clean();
                        saveState("", "", false, answer);
                    } else {
                        sendText(user, answer.getMessage(), true);
                        saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
                    }
                } else {
                    authentication(user, msg);
                }
            }
        } else if (answer.getAuthentication()) {
            authentication(user, msg);
        } else if (answer.getNewHoliday()) {
            addHoliday(user, msg);
        } else {
            sendText(user, "Ай карамба! Ты ошибся, попробуй ещё раз!", false);
        }
    }


    public void sendText(Long user, String message, boolean sendKeyBoard) {
        if (sendKeyBoard) {
            sendInlineKeyBoardMessage(user, message);
        } else {
            SendMessage sm = SendMessage.builder()
                    .chatId(user.toString())
                    .text(message).build();
            try {
                execute(sm);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void authentication(Long user, String text) {

        if (!client.getNickname()) {
            sendText(user, "Введите логин:", false);
            client.setNickname(true);
            saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
            UserStatus.put(user, client);
            return;
        }
        if (!client.getPassword()) {
            sendText(user, "Введите пароль:", false);
            client.setPassword(true);
            client.saveUserNickname(text);
            saveState(text, logic.getPassword(), logic.getState(), answer);
            UserStatus.put(user, client);
            return;
        } else {
            client.saveUserPassword(text);
        }
        answer = logic.clientAuthentication(client.getUserNickname(), client.getUserPassword());
        client.setPassword(false);
        client.setNickname(false);
        saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
        UserStatus.put(user, client);
        sendText(user, answer.getMessage(), true);
    }

    private void addHoliday(Long user, String text) {
        if (!client.getDate()) {
            sendText(user, text + "\nВведи дату праздника в виде YYYY-MM-DD:", false);
            client.setDate(true);
            saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
            UserStatus.put(user, client);
            return;
        }

        String name = "";
        if (!client.getName()) {
            client.saveUserDate(text);
            sendText(user, "Введите название праздника:", false);
            client.setName(true);
            saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
            UserStatus.put(user, client);
            return;
        } else {
            name = text;
        }

        answer = logic.newHoliday(client.getUserDate(), name);
        client.setName(false);
        client.setDate(false);
        client.saveUserDate("");
        saveState(logic.getNickname(), logic.getPassword(), logic.getState(), answer);
        UserStatus.put(user, client);
        sendText(user, answer.getMessage(), true);
    }

    private void saveState(String nick, String pass, boolean state, MessageFromBot ans) {
        client.setSaveAnswer(ans);
        client.saveUserPassword(pass);
        client.saveUserNickname(nick);
        client.setStatus(state);
    }

    public void sendInlineKeyBoardMessage(Long user, String message) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton5 = new InlineKeyboardButton();
        inlineKeyboardButton1.setText("help");
        inlineKeyboardButton1.setCallbackData("/help");
        inlineKeyboardButton2.setText("Вход");
        inlineKeyboardButton2.setCallbackData("/entry");
        inlineKeyboardButton3.setText("Регистрация");
        inlineKeyboardButton3.setCallbackData("/register");
        inlineKeyboardButton4.setText("Новый праздник");
        inlineKeyboardButton4.setCallbackData("/addHoliday");
        inlineKeyboardButton5.setText("Пока");
        inlineKeyboardButton5.setCallbackData("/bye");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        keyboardButtonsRow1.add(inlineKeyboardButton2);
        keyboardButtonsRow2.add(inlineKeyboardButton3);
        keyboardButtonsRow2.add(inlineKeyboardButton4);
        keyboardButtonsRow3.add(inlineKeyboardButton5);
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);
        inlineKeyboardMarkup.setKeyboard(rowList);
        SendMessage sm = SendMessage.builder()
                .chatId(user.toString())
                .text(message)
                .replyMarkup(inlineKeyboardMarkup).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
