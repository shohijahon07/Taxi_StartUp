package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaxiProjectBot extends TelegramLongPollingBot {

    @Override
    public String getBotToken() {
        return "6995954341:AAFa0pZzNkGS2NJ0VDuMDO0K7Jlqwgs7-jE";
    }

    @Override
    public String getBotUsername() {
        return "jonkatoychoq_bot";
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            SendMessage sendMessage = new SendMessage();

            if (message.hasText()) {
                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("Iltimos tilni tanlang!   " +
                            "Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());

                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            SendMessage sendMessage = new SendMessage();

            if ("uz".equals(data)) {
                sendMessage.setText("Iltimos kontaktni yuboring!");
                sendMessage.setReplyMarkup(genContactButtons("uz"));

            } else if ("ru".equals(data)) {
                sendMessage.setText("Пожалуйста пришлите контакт!");
                sendMessage.setReplyMarkup(genContactButtons("ru"));
            } else {
                sendMessage.setText("Unknown language code");
            }

            sendMessage.setChatId(chatId);
            execute(sendMessage);
        }
    }

    private ReplyKeyboardMarkup genContactButtons(String language) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        rows.add(row);

        KeyboardButton button = new KeyboardButton();
        String buttonText;

        if ("uz".equals(language)) {
            buttonText = "Kontaktni baham ko'ring";
        } else if ("ru".equals(language)) {
            buttonText = "Поделиться контактом";
        } else {
            buttonText = "Share contact";
        }

        button.setText(buttonText);
        button.setRequestContact(true);
        row.add(button);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup selectLanguageButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        rows.add(row);

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("uz \uD83C\uDDFA\uD83C\uDDFF");
        button1.setCallbackData("uz");
        row.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("ru \uD83C\uDDF7\uD83C\uDDFA");
        button2.setCallbackData("ru");
        row.add(button2);

        return new InlineKeyboardMarkup(rows);
    }
}
