package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.example.backend.Repasitory.AdminRepo;
import org.example.backend.entity.Admin;
import org.example.backend.entity.Status;
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
import java.util.Optional;

@Component
public class TaxiProjectBot extends TelegramLongPollingBot {

    private final AdminRepo adminRepo;

    public TaxiProjectBot(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public String getBotToken() {
        return "6995954341:AAFa0pZzNkGS2NJ0VDuMDO0K7Jlqwgs7-jE";
    }

    @Override
    public String getBotUsername() {
        return "jonkatoychoq_bot";
    }
    String[] language = new String[1];

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            Admin admin1 = new Admin();
            if (message.hasText()) {
                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    execute(sendMessage);
                } else if (admin1.getStatus().equals(Status.DRIVER_PAGE)) {
                    sendMessage.setText("Qayerdan");
                    sendMessage.setReplyMarkup(fromCitysButtons());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);

                }
            } else if (message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                Admin admin = selectUser(chatId, phoneNumber);
            if(language.equals("uz")){
            sendMessage.setText("Assalom eleykum botimizga xush kelibsiz! " +
            "Pastdagi knopkalardan birini tanlang. Siz haydovchimi yoki yo'lovchi?");
            }else{
             sendMessage.setText("Добро пожаловать в наш бот! Выберите одну из кнопок ниже. Вы водитель или пассажир?");
            }
                sendMessage.setReplyMarkup(selectRoleButtons());
                execute(sendMessage);
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);

            if ("uz".equals(data)) {
                sendMessage.setText("Iltimos telefon raqamingizni yuboring:");
                sendMessage.setReplyMarkup(genContactButtons("uz"));
            } else if ("ru".equals(data)) {
                sendMessage.setText("Пожалуйста, отправьте ваш номер телефона!");
                sendMessage.setReplyMarkup(genContactButtons("ru"));
            }
            execute(sendMessage);
        }
    }
    private InlineKeyboardMarkup fromCitysButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Buxoro");
        button1.setCallbackData("Buxoro");
        row1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Toshkent");
        button2.setCallbackData("Toshkent");
        row1.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Samarqand");
        button3.setCallbackData("Samarqand");
        row2.add(button3);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Farg'ona");
        button4.setCallbackData("Farg'ona");
        row2.add(button4);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("Andijon");
        button5.setCallbackData("Andijon");
        row3.add(button5);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("Namangan");
        button6.setCallbackData("Namangan");
        row3.add(button6);

        InlineKeyboardButton button7 = new InlineKeyboardButton();
        button7.setText("Navoiy");
        button7.setCallbackData("Navoiy");
        row4.add(button7);

        InlineKeyboardButton button8 = new InlineKeyboardButton();
        button8.setText("Xorazm");
        button8.setCallbackData("Xorazm");
        row4.add(button8);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);

        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();

        InlineKeyboardButton button9 = new InlineKeyboardButton();
        button9.setText("Qashqadaryo");
        button9.setCallbackData("Qashqadaryo");
        row5.add(button9);

        InlineKeyboardButton button10 = new InlineKeyboardButton();
        button10.setText("Surxondaryo");
        button10.setCallbackData("Surxondaryo");
        row5.add(button10);

        InlineKeyboardButton button11 = new InlineKeyboardButton();
        button11.setText("Jizzax");
        button11.setCallbackData("Jizzax");
        row6.add(button11);

        InlineKeyboardButton button12 = new InlineKeyboardButton();
        button12.setText("Sirdaryo");
        button12.setCallbackData("Sirdaryo");
        row6.add(button12);

        rows.add(row5);
        rows.add(row6);

        return new InlineKeyboardMarkup(rows);
    }


    private ReplyKeyboardMarkup selectRoleButtons() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        if(language.equals("uz")){
            button1.setText("Haydovchilar");
        }else{
            button1.setText("Драйверы");
        }
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton();
        if(language.equals("uz")){
            button2.setText("Yo'lovchilar");
        }else{
            button2.setText("Пассажиры");


        }
        row2.add(button2);

        rows.add(row1);
        rows.add(row2);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup genContactButtons(String language) {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        rows.add(row);

        KeyboardButton button = new KeyboardButton();
        String buttonText;

        if ("uz".equals(language)) {
            language="uz";
            buttonText = "Telefon raqamni jo'natish \uD83D\uDCDE";
        } else if ("ru".equals(language)) {
            language="ru";

            buttonText = "Отправить номер телефона  \uD83D\uDCDE";
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


    private Admin selectUser(Long chatId, String phoneNumber) {
        List<Admin> admins = adminRepo.findAllByChatId(chatId);

        if (!admins.isEmpty()) {
            return admins.get(0);
        }

        Admin newAdmin = new Admin(chatId, Status.SELECT_PATH, phoneNumber);
        System.out.println(newAdmin);
        return adminRepo.save(newAdmin);
    }
}