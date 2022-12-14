package holidayBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class TelegramBot extends TelegramLongPollingBot {
    private Logic logic = new Logic();
    private MessageFromBot answer = new MessageFromBot();
    private UserData client = new UserData();

    public HashMap<Long, List<Boolean>> UserStatus = new HashMap<>();

    public List<Boolean> Status = new ArrayList<Boolean>();


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

        if (update.hasMessage()) {
            var msg = update.getMessage().getText();
            var user = update.getMessage().getChatId();
            detectCommand(msg, user, update.getMessage().isCommand());

        } else if (update.hasCallbackQuery()) {
            var msg = update.getCallbackQuery().getData();
            var user = update.getCallbackQuery().getMessage().getChatId();
            detectCommand(msg, user, true);
        }
    }

    private void detectCommand(String msg, Long user, boolean command) {
        if (command) {
            if (UserStatus.containsKey(user)) {
                UserStatus.replace(user, Status);
            } else {
                Status.add(client.getNickname());
                Status.add(client.getPassword());
                Status.add(client.getDate());
                Status.add(client.getName());
                UserStatus.putIfAbsent(user, Status);
            }
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
                        return;
                    }
                    if (answer.toExit()) {
                        sendText(user, answer.getMessage(), false);
                        answer.cleanMessage();
                        logic.clean();
                    } else {
                        sendText(user, answer.getMessage(), true);
                    }
                } else {
                    Authentication(user, msg);
                }
            }
        } else if (answer.getAuthentication()) {
            Authentication(user, msg);
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

    private void Authentication(Long user, String text) {

        if (java.util.Objects.equals(Status.get(0), false)) {
            sendText(user, "Введите логин:", false);
            Status.set(0,true);
            UserStatus.replace(user, Status);
            return;
        }
        String password = "";
        if (java.util.Objects.equals(Status.get(1), false)) {
            client.saveUserNickname(text);
            sendText(user, "Введите пароль:", false);
            Status.set(1, true);
            UserStatus.replace(user, Status);
            return;
        } else {
            password = text;
        }
        answer = logic.clientAuthentication(client.getUserNickname(), password);
        Status.set(1, false);
        Status.set(0, false);
        UserStatus.replace(user, Status);
        client.saveUserNickname("");
        sendText(user, answer.getMessage(), true);
    }

    private void addHoliday(Long user, String text) {
        if (java.util.Objects.equals(Status.get(2), false)) {
            sendText(user, text + "\nВведи дату праздника в виде YYYY-MM-DD:", false);
            Status.set(2, true);
            UserStatus.replace(user, Status);
            return;
        }

        String name = "";
        if (java.util.Objects.equals(Status.get(3), false)) {
            client.saveUserDate(text);
            sendText(user, "Введите название праздника:", false);
            Status.set(3, true);
            UserStatus.replace(user, Status);
            return;
        } else {
            name = text;
        }

        answer = logic.newHoliday(client.getUserDate(), name);
        Status.set(3, false);
        Status.set(2, true);
        UserStatus.replace(user, Status);
        client.saveUserDate("");
        sendText(user, answer.getMessage(), true);
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
