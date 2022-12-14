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

        if (update.hasMessage()) {
            var msg = update.getMessage().getText();
            var user = update.getMessage().getChatId();
            if (!UserStatus.containsKey(user)) {
                UserStatus.put(user, client);

            }
            detectCommand(msg, user, update.getMessage().isCommand());

        } else if (update.hasCallbackQuery()) {
            var msg = update.getCallbackQuery().getData();
            var user = update.getCallbackQuery().getMessage().getChatId();
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
        var info = UserStatus.getOrDefault(user, client);
        if (!info.getNickname()) {
            sendText(user, "Введите логин:", false);
            info.setNickname(true);
            UserStatus.put(user, info);
            return;
        }
        String password = "";
        if (!info.getPassword()) {
            info.saveUserNickname(text);
            sendText(user, "Введите пароль:", false);
            info.setPassword(true);
            UserStatus.put(user, info);
            return;
        } else {
            password = text;
        }
        answer = logic.clientAuthentication(info.getUserNickname(), password);
        info.setPassword(false);
        info.setNickname(false);
        info.saveUserNickname("");
        sendText(user, answer.getMessage(), true);
        UserStatus.put(user, info);
    }

    private void addHoliday(Long user, String text) {
        var info = UserStatus.getOrDefault(user, client);
        if (!info.getDate()) {
            sendText(user, text + "\nВведи дату праздника в виде YYYY-MM-DD:", false);
            info.setDate(true);
            UserStatus.put(user, info);
            return;
        }

        String name = "";
        if (!info.getName()) {
            info.saveUserDate(text);
            sendText(user, "Введите название праздника:", false);
            info.setName(true);
            UserStatus.put(user, info);
            return;
        } else {
            name = text;
        }

        answer = logic.newHoliday(info.getUserDate(), name);
        info.setName(false);
        info.setDate(false);
        info.saveUserDate("");
        sendText(user, answer.getMessage(), true);
        UserStatus.put(user, info);
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
