package holidayBot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TelegramBot extends TelegramLongPollingBot {
    private Logic logic = new Logic();
    private MessageFromBot answer = new MessageFromBot();
    private boolean setNickname = false;
    private boolean setPassword = false;
    private boolean getDate = false;
    private boolean getName = false;
    private String nickname = "";
    private String date = "";

    @Override
    public String getBotUsername() {
        return "HolidayBot";
    }//environment vars

    @Override
    public String getBotToken() {
        return "5868427343:AAEvBAjFe0VoMkCb5JMwMsIiVCWEIANQn9o";
    }//environment vars

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

        if (!setNickname) {
            sendText(user, "Введите логин:", false);
            setNickname = true;
            return;
        }
        String password = "";
        if (!setPassword) {
            nickname = text;
            sendText(user, "Введите пароль:", false);
            setPassword = true;
            return;
        } else {
            password = text;
        }
        answer = logic.clientAuthentication(nickname, password);
        setPassword = false;
        setNickname = false;
        nickname = "";
        sendText(user, answer.getMessage(), true);
    }

    private void addHoliday(Long user, String text) {
        if (!getDate) {
            sendText(user, text + "\nВведи дату праздника в виде YYYY-MM-DD:", false);
            getDate = true;
            return;
        }

        String name = "";
        if (!getName) {
            date = text;
            sendText(user, "Введите название праздника:", false);
            getName = true;
            return;
        } else {
            name = text;
        }

        answer = logic.newHoliday(date, name);
        getName = false;
        getDate = false;
        date = "";
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
