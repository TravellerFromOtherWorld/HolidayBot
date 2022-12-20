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
                UserStatus.put(user, new UserData());
                detectCommand(msg, user, update.getMessage().isCommand(), UserStatus.get(user));
            } else {
                logic.setCommand(UserStatus.get(user).getCommand());
                detectCommand(msg, user, update.getMessage().isCommand(), UserStatus.get(user));
            }

        } else if (update.hasCallbackQuery()) {
            var msg = update.getCallbackQuery().getData();
            var user = update.getCallbackQuery().getMessage().getChatId();
            if (!UserStatus.containsKey(user)) {
                UserStatus.put(user, new UserData());
                detectCommand(msg, user, true, UserStatus.get(user));
            } else {
                logic.setCommand(UserStatus.get(user).getCommand());
                detectCommand(msg, user, true, UserStatus.get(user));
            }
        }
    }

    private void detectCommand(String msg, Long user, boolean command, UserData userStat) {
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
                userStat.setSaveAnswer(logic.work(userStat));

                if (!userStat.getSaveAnswer().getAuthentication()) {
                    if (userStat.getSaveAnswer().getNewHoliday()) {
                        addHoliday(user, userStat.getSaveAnswer().getMessage(), userStat);
                        saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
                        return;
                    }
                    if (userStat.getSaveAnswer().toExit()) {
                        sendText(user, userStat.getSaveAnswer().getMessage(), false);
                        userStat.getSaveAnswer().cleanMessage();
                        logic.clean();
                        UserStatus.remove(user);
                    } else {
                        sendText(user, userStat.getSaveAnswer().getMessage(), true);
                        saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
                    }
                } else {
                    authentication(user, msg, userStat);
                }
            }
        } else if (userStat.getSaveAnswer().getAuthentication()) {
            authentication(user, msg, userStat);
        } else if (userStat.getSaveAnswer().getNewHoliday()) {
            addHoliday(user, msg, userStat);
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

    private void authentication(Long user, String text, UserData userStat) {

        if (!userStat.getNickname()) {
            sendText(user, "Введите логин:", false);
            userStat.setNickname(true);
            saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
            UserStatus.put(user, userStat);
            return;
        }
        if (!userStat.getPassword()) {
            sendText(user, "Введите пароль:", false);
            userStat.setPassword(true);
            userStat.saveUserNickname(text);
            saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
            UserStatus.put(user, userStat);
            return;
        } else {
            userStat.saveUserPassword(text);
        }
        userStat.setSaveAnswer(logic.clientAuthentication(userStat));
        userStat.setPassword(false);
        userStat.setNickname(false);
        saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
        UserStatus.put(user, userStat);
        sendText(user, userStat.getSaveAnswer().getMessage(), true);
    }

    private void addHoliday(Long user, String text, UserData userStat) {
        if (!userStat.getDate()) {
            sendText(user, text + "\nВведи дату праздника в виде YYYY-MM-DD:", false);
            userStat.setDate(true);
            saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
            UserStatus.put(user, userStat);
            return;
        }

        if (!userStat.getName()) {
            userStat.saveUserDate(text);
            sendText(user, "Введите название праздника:", false);
            userStat.setName(true);
            saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
            UserStatus.put(user, userStat);
            return;
        } else {
            userStat.saveNameOfHoliday(text);
        }

        userStat.setSaveAnswer(logic.newHoliday(userStat));
        userStat.setName(false);
        userStat.setDate(false);
        userStat.saveUserDate("");
        saveState(userStat.getSaveAnswer(), logic.getCommand(), userStat);
        UserStatus.put(user, userStat);
        sendText(user, userStat.getSaveAnswer().getMessage(), true);
    }

    private void saveState(MessageFromBot ans, int task, UserData userStat) {
        userStat.setSaveAnswer(ans);
        userStat.setCommand(task);
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
