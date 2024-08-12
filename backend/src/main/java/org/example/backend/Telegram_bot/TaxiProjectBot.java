package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.example.backend.entity.Route_Driver;
import org.example.backend.entity.Status;
import org.example.backend.entity.User;
import org.example.backend.repository.Route_DriverRepo;
import org.example.backend.repository.UserRepo;
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

import java.sql.Time;
import java.time.LocalDate;
import java.util.*;

@Component
public class TaxiProjectBot extends TelegramLongPollingBot {

    private final UserRepo userRepo;
    private final Route_DriverRepo routeDriverRepo;
    private String language;

    public TaxiProjectBot(UserRepo userRepo, Route_DriverRepo routeDriverRepo) {
        this.userRepo = userRepo;
        this.routeDriverRepo = routeDriverRepo;
    }

    @Override
    public String getBotToken() {
        return "6995954341:AAFa0pZzNkGS2NJ0VDuMDO0K7Jlqwgs7-jE";
    }

    @Override
    public String getBotUsername() {
        return "jonkatoychoq_bot";
    }

    private String[] driver_data = new String[6];

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);



            if (message.hasText()) {
                System.out.println("array: " + Arrays.toString(driver_data));
//                System.out.println("User: " + user);

                if (message.getText().equalsIgnoreCase("/start")) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    execute(sendMessage);
                } else if (message.getText().equals("Haydovchilar") || message.getText().equals("Драйверы")) {
                    sendMessage.setText("Yo'nalishingizni kiriting \n Qayerdan?");
                    sendMessage.setReplyMarkup(fromCitysButtons());
                    execute(sendMessage);
                } else if (message.getText().equals("Yo'lovchilar") || message.getText().equals("Пассажиры")) {
                    sendMessage.setText("Please enter your destination");
                    execute(sendMessage);
                } else if (driver_data[3]==null) {
                    driver_data[3] = message.getText();

                    sendMessage.setText("Iltimos, sanani va oyni kiriting (masalan, '23 iyul' kuni uchun):");
                    execute(sendMessage);
                } else if (driver_data[4]==null) {
                    driver_data[4] = message.getText();

                    sendMessage.setText("Soatni kiriting:");
                    List<Long> userIds = userRepo.findAllUserIdsByChatId(chatId);
                    System.out.println("User IDs: " + userIds);

                    execute(sendMessage);
                } else if (driver_data[5] == null) {
                    driver_data[5] = message.getText();
                    sendMessage.setText("Muvafaqatli qo'shildi");

//                    String fromCity = driver_data[0];
//                    String toCity = driver_data[1];

//                    // Extract numeric value from seat count string
//                    String countSideStr = driver_data[2].split(" ")[0];  // Split by space and take the first part
//                    Integer countSide = Integer.valueOf(countSideStr);
//
//                    Integer price = Integer.valueOf(driver_data[3]);
//                    LocalDate localDate = LocalDate.parse(driver_data[4]);
//                    Time time = Time.valueOf(driver_data[5]);




                    // Create a new Route_Driver instance and save it (uncomment the code if needed)
                    // Route_Driver routeDriver = new Route_Driver(fromCity, toCity, countSide, price, localDate, time, new User());
                    // routeDriverRepo.save(routeDriver);

                    execute(sendMessage);
                    driver_data = new String[4];
                }

            } else if (message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                User admin = selectUser(chatId, phoneNumber);
                if ("uz".equals(language)) {
                    sendMessage.setText("Assalom eleykum botimizga xush kelibsiz! " +
                            "Pastdagi knopkalardan birini tanlang. Siz haydovchimi yoki yo'lovchi?");
                } else if ("ru".equals(language)) {
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

            Optional<User> userOptional = userRepo.findByChatId(chatId);

            User user = userOptional.orElseGet(() -> {
                User newUser = new User();
                newUser.setChatId(chatId);
                return userRepo.save(newUser);
            });

            switch (data) {
                case "uz":
                    language = "uz";
                    sendMessage.setText("Iltimos telefon raqamingizni yuboring:");
                    sendMessage.setReplyMarkup(genContactButtons(language));
                    break;
                case "ru":
                    language = "ru";
                    sendMessage.setText("Пожалуйста, отправьте ваш номер телефона!");
                    sendMessage.setReplyMarkup(genContactButtons(language));
                    break;
                case "Buxoro":
                case "Toshkent":
                case "Samarqand":
                case "Farg'ona":
                case "Andijon":
                case "Namangan":
                case "Navoiy":
                case "Xorazm":
                case "Qashqadaryo":
                case "Surxondaryo":
                case "Jizzax":
                case "Sirdaryo":
                    if (driver_data[0] == null) {
                        driver_data[0] = data;
                        sendMessage.setText("Yo'nalishingizni kiriting \n Qayerga?");
                        sendMessage.setReplyMarkup(fromCitysButtons());
                    } else {
                        driver_data[1] = data;
                           sendMessage.setText("Nechta joy bor?");
                        sendMessage.setReplyMarkup(countseatButtons());
                    }
                    break;
                case "1 ta":
                case "2 ta":
                case "3 ta":
                case "4 ta":
                case "5 ta":
                case "6 ta":
                    driver_data[2] = data;
                    sendMessage.setText("Necha pulga olib ketasiz?");
                    userRepo.save(user);
                    break;
                default:
                    sendMessage.setText("Invalid selection");
                    break;
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


    private InlineKeyboardMarkup countseatButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("1 ta");
        button1.setCallbackData("1 ta");
        row1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("2 ta");
        button2.setCallbackData("2 ta");
        row1.add(button2);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("3 ta");
        button3.setCallbackData("3 ta");
        row2.add(button3);

        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("4 ta");
        button4.setCallbackData("4 ta");
        row2.add(button4);

        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("5 ta");
        button5.setCallbackData("5 ta");
        row3.add(button5);

        InlineKeyboardButton button6 = new InlineKeyboardButton();
        button6.setText("6 ta");
        button6.setCallbackData("6 ta");
        row3.add(button6);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        return new InlineKeyboardMarkup(rows);
    }

    private ReplyKeyboardMarkup selectRoleButtons() {
        List<KeyboardRow> rows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        if ("uz".equals(language)) {
            button1.setText("Haydovchilar");

        }else{
            button1.setText("Драйверы");
        }
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton();
        if("uz".equals(language)){
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
            buttonText = "Telefon raqamni jo'natish \uD83D\uDCDE";
        } else if ("ru".equals(language)) {
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


    private User selectUser(Long chatId, String phoneNumber) {
        List<User> admins = userRepo.findAllByChatId(chatId);

        if (!admins.isEmpty()) {
            return admins.get(0);
        }

        User newAdmin = new User(chatId, Status.SELECT_PATH, phoneNumber);
        return userRepo.save(newAdmin);
    }

}
