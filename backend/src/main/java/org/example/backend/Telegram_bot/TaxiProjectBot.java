package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.example.backend.entity.*;
import org.example.backend.repository.FromCityRepo;
import org.example.backend.repository.RouteDriverRepo;
import org.example.backend.repository.ToCityRepo;
import org.example.backend.repository.UserRepo;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class TaxiProjectBot extends TelegramLongPollingBot {

    private final UserRepo userRepo;
    private final RouteDriverRepo routeDriverRepo;
    private final FromCityRepo fromCityRepo;
    private final ToCityRepo toCityRepo;


    private String language;
    private String id;


    public TaxiProjectBot(UserRepo userRepo, RouteDriverRepo routeDriverRepo1, FromCityRepo fromCityRepo, ToCityRepo toCityRepo) {
        this.userRepo = userRepo;
        this.routeDriverRepo = routeDriverRepo1;
        this.fromCityRepo = fromCityRepo;
        this.toCityRepo = toCityRepo;
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
    private  String[] status = new String[6];
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            System.out.println(Arrays.toString(new String[]{status[0]}));
            User foundUser = userRepo.findByChatId(chatId).orElse(null);
            if (foundUser == null) {
                foundUser = new User();
                foundUser.setChatId(chatId);
            } else {
                foundUser.setChatId(chatId);
            }

            userRepo.save(foundUser);


            System.out.println(foundUser);
            if (message.hasText()) {
                System.out.println("array: " + Arrays.toString(driver_data));
                User user1 = new User();
                if (foundUser.getStatus().equals(Status.START)&&message.getText().equalsIgnoreCase("/start")&&foundUser.getIsDriver().equals(false)) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
                if (foundUser.getStatus().equals(Status.START)&&message.getText().equalsIgnoreCase("/start")&&foundUser.getIsDriver().equals(true)){
                   foundUser.setStatus(Status.SET_FROM);
                   userRepo.save(foundUser);
                    sendMessage.setText("Yo'nalishingizni kiriting \n Qayerdan?");
                    sendMessage.setReplyMarkup(fromCitysButtons());
                    execute(sendMessage);
                }
                else if (foundUser.getStatus().equals(Status.SET_DIRECTIONS)) {
                    List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                    for (Route_Driver routeDriver : routeDriverRepo.findAll()) {
                        if (userIds.contains(routeDriver.getUser().getId())) {
                            sendMessage.setText(routeDriver.getFromCity() + "-" + routeDriver.getToCity() + "\n" +
                                    "Bo'sh-jo'ylar soni: " + routeDriver.getCountSide() + " \n" +
                                    "Narxi: " + routeDriver.getPrice() + " so'm \n" +
                                    routeDriver.getDay() + " " + routeDriver.getHour()
                            );
                            sendMessage.setReplyMarkup(directionData(routeDriver.getId()));
                            execute(sendMessage);
                        }
                    }

                }
                else if (foundUser.getStatus().equals(Status.NEW_NUMBER)) {
                    System.out.println("kirdi");
                    String countS = message.getText();
                    Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));

                    if (byId.isPresent()) {
                        Route_Driver routeDriver = byId.get();
                        routeDriver.setCountSide(Integer.parseInt(countS));

                        routeDriverRepo.save(routeDriver);

                        sendMessage.setText("Jo'ylar soni muvafaqatli qo'shildi");
                        execute(sendMessage);
                    } else {
                        sendMessage.setText("Kechirasiz, ushbu marshrut topilmadi.");
                        execute(sendMessage);
                    }
                }else if(foundUser.getStatus().equals(Status.NEW_PRICE)){
                    System.out.println("kirdi");
                    String price = message.getText();
                    Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));

                    if (byId.isPresent()) {
                        Route_Driver routeDriver = byId.get();
                        routeDriver.setPrice(Integer.parseInt(price));

                        routeDriverRepo.save(routeDriver);

                        sendMessage.setText("Narx  muvafaqatli yangilandi");
                        execute(sendMessage);
                    } else {
                        sendMessage.setText("Kechirasiz, ushbu marshrut topilmadi.");
                        execute(sendMessage);
                    }
                }else if(foundUser.getStatus().equals(Status.NEW_DAY)){
                    System.out.println("kirdi");
                    String day = message.getText();
                    Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));

                    if (byId.isPresent()) {
                        Route_Driver routeDriver = byId.get();
                        routeDriver.setDay(LocalDate.parse(day));





                        routeDriverRepo.save(routeDriver);

                        sendMessage.setText("Sana muvafaqatli yangilandi");
                        execute(sendMessage);
                    } else {
                        sendMessage.setText("Xatolik mavjud.");
                        execute(sendMessage);
                    }
                }

                else if (foundUser.getStatus().equals(Status.SET_GO_MONEY)) {
                    try {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        Integer.parseInt(message.getText());
                        driver_data[3] = message.getText();
                        sendMessage.setText("Iltimos, sanani va oyni kiriting (masalan, '04-20' kuni uchun):");
                        execute(sendMessage);
                    } catch (NumberFormatException e) {
                        sendMessage.setText("Noto'g'ri format. Iltimos, faqat son kiritishingiz kerak.");
                        execute(sendMessage);
                    }

                }
                else if (foundUser.getStatus().equals(Status.SET_DAY_MONTH)) {
                    try {
                        foundUser.setStatus(Status.SET_TIME);
                        userRepo.save(foundUser);

                        String input = message.getText();

                        // Check if the input text is null or empty before processing it
                        if (input == null || input.isEmpty()) {
                            sendMessage.setText("Sana kiritilmadi yoki noto'g'ri formatda kiritildi. Iltimos, sanani 'kun-oy' formatida kiriting (masalan, '04-29').");
                            execute(sendMessage);
                            return;
                        }

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
                        String[] parts = input.split("-");

                        if (parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 2) {
                            int day = Integer.parseInt(parts[0]);
                            int month = Integer.parseInt(parts[1]);

                            if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                                int currentYear = LocalDate.now().getYear();
                                LocalDate inputDate = LocalDate.of(currentYear, month, day);
                                LocalDate today = LocalDate.now();

                                if (inputDate.isBefore(today.minusDays(1)) || inputDate.isAfter(today.plusDays(2))) {
                                    throw new DateTimeParseException("Sana oraliqdan tashqarida", input, 0);
                                }

                                driver_data[4] = inputDate.toString();
                                sendMessage.setText("Soatni kiriting (masalan, 01:50)");
                                execute(sendMessage);
                            } else {
                                throw new DateTimeParseException("Noto'g'ri oy yoki kun", input, 0);
                            }
                        } else {
                            throw new DateTimeParseException("Noto'g'ri format", input, 0);
                        }
                    } catch (DateTimeParseException e) {
                        sendMessage.setText("Noto'g'ri format. Iltimos, sanani 'kun-oy' formatida kiriting (kun 1-31 gacha, oy 1-12 gacha) va bugungi kundan boshlab yana 2 kun kirita olasiz.");
                        execute(sendMessage);
                    } catch (Exception e) {
                        sendMessage.setText("Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");
                        execute(sendMessage);
                    }
                }

                else if (foundUser.getStatus().equals(Status.SET_TIME)) {
                    List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                    driver_data[5] = message.getText();
                    if (!userIds.isEmpty()) {
                        foundUser.setStatus(Status.SET_DIRECTIONS);
                        userRepo.save(foundUser);
                        User user = userRepo.findById(userIds.get(0)).orElseThrow(() -> new IllegalStateException("User not found"));

                        String fromCity = driver_data[0];
                        String toCity = driver_data[1];

                        String countSideStr = driver_data[2].replaceAll("[^0-9]", "");
                        Integer countSide = Integer.valueOf(countSideStr);

                        String priceStr = driver_data[3].replaceAll("[^0-9]", "");
                        Integer price = Integer.valueOf(priceStr);

                        LocalDate day = LocalDate.parse(driver_data[4]);
                        String hour = driver_data[5];
                        System.out.println(hour);
                        Route_Driver routeDriver = new Route_Driver(fromCity, toCity, countSide, price, day, hour, user);
                        routeDriverRepo.save(routeDriver);

                        System.out.println(fromCity + " " + toCity + " " + countSide + " " + price + " " + day + " " + hour);
                        sendMessage.setText("Muvafafaqiyatli qo'shildi");
                        sendMessage.setReplyMarkup(directions());
                        execute(sendMessage);
                        driver_data = new String[6];
                    } else {
                        sendMessage.setText("User not found for the given chat ID");
                        execute(sendMessage);
                    }
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
                sendMessage.setReplyMarkup(selectInlineRoleButtons());
                execute(sendMessage);
            }
        }
        else if (update.hasCallbackQuery()) {
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
            System.out.println(data);
            List<FromCity> allFromCities = fromCityRepo.findAll();
            List<ToCity> allToCities = toCityRepo.findAll();

            if (data.equals("uz") && !user.getIsDriver()) {
                language = "uz";
                sendMessage.setText("Iltimos telefon raqamingizni yuboring:");
                sendMessage.setReplyMarkup(genContactButtons(language));
                execute(sendMessage);
                return;
            } else if (data.equals("ru") && !user.getIsDriver()) {
                language = "ru";
                sendMessage.setText("Пожалуйста, отправьте ваш номер телефона!");
                sendMessage.setReplyMarkup(genContactButtons(language));
                execute(sendMessage);
                return;
            }

            boolean cityFound = false;

            if (user.getStatus().equals(Status.SET_FROM)) {
                for (FromCity city : allFromCities) {
                    if (city.getName().equals(data)) {
                        cityFound = true;
                        driver_data[0] = data;
                        user.setStatus(Status.SET_TO);
                        sendMessage.setText("Yo'nalishingizni kiriting \n Qayerga?");
                        sendMessage.setReplyMarkup(toCitysButtons());
                        userRepo.save(user);
                        execute(sendMessage);
                        return;
                    }
                }
            }
            if (data.startsWith("place")) {
                String[] dataParts = data.split(":");
                System.out.println("place kirdi");
                sendMessage.setText("Jo'ylar soni yangi qiymatini kiriting:");
                status[0]="place";
                if (dataParts.length > 1) {
                    id = String.valueOf(UUID.fromString(dataParts[1]));
                    System.out.println(id);
                    user.setStatus(Status.NEW_NUMBER);
                    userRepo.save(user);
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                    execute(sendMessage);
                }
            }else if(data.startsWith("money")){
                String[] dataParts = data.split(":");
                System.out.println("money kirdi");
                sendMessage.setText("Narxni yangi qiymatini kiriting:");
                status[0]="money";
                if (dataParts.length > 1) {
                    id = String.valueOf(UUID.fromString(dataParts[1]));
                    System.out.println(id);
                    user.setStatus(Status.NEW_PRICE);
                    userRepo.save(user);
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                    execute(sendMessage);
                }
            }else if(data.startsWith("day")){
                String[] dataParts = data.split(":");
                System.out.println("day kirdi");
                sendMessage.setText("Sanani yangi qiymatini kiriting:");
                status[0]="day";
                if (dataParts.length > 1) {
                    id = String.valueOf(UUID.fromString(dataParts[1]));
                    System.out.println(id);
                    user.setStatus(Status.NEW_DAY);
                    userRepo.save(user);
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                    execute(sendMessage);
                }
            }
            if (user.getStatus().equals(Status.SET_TO)) {
                for (ToCity city : allToCities) {
                    if (city.getName().equals(data)) {
                        cityFound = true;
                        user.setStatus(Status.SET_COUNT_SIDE);
                        driver_data[1] = data;
                        sendMessage.setText("Nechta joy bor?");
                        sendMessage.setReplyMarkup(countseatButtons());
                        userRepo.save(user);
                        execute(sendMessage);
                        return;
                    }
                }
            }

            if (user.getStatus().equals(Status.SET_COUNT_SIDE)) {
                switch (data) {
                    case "1 ta":
                    case "2 ta":
                    case "3 ta":
                    case "4 ta":
                    case "5 ta":
                    case "6 ta":
                        driver_data[2] = data;
                        user.setStatus(Status.SET_GO_MONEY);
                        sendMessage.setText("Necha pulga olib ketasiz?");
                        userRepo.save(user);
                        execute(sendMessage);
                        return;
                    default:
                        break;
                }
            }

        }
    }
    private InlineKeyboardMarkup directionData(UUID id) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Jo'y sonni o'zgartirsh:");
        button1.setCallbackData("place:" + id);
        firstRow.add(button1);
        rows.add(firstRow);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Narxni o'zgartirsh");
        button2.setCallbackData("money:" + id);
        secondRow.add(button2);
        rows.add(secondRow);

        List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Sana o'zgartish ");
        button3.setCallbackData("day:" + id);
        secondRow3.add(button3);
        rows.add(secondRow3);
        return new InlineKeyboardMarkup(rows);
    }

    private ReplyKeyboardMarkup directions(){
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        button1.setText("Yo'nalishlarim");
        row1.add(button1);


        rows.add(row1);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup fromCitysButtons() {
        List<FromCity> fromCities = fromCityRepo.findAll();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (FromCity fromCity : fromCities) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(fromCity.getName());
            button.setCallbackData(fromCity.getName());

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rows.add(currentRow);
                currentRow = new ArrayList<>();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return new InlineKeyboardMarkup(rows);
    }
    private InlineKeyboardMarkup toCitysButtons() {
        List<ToCity> all = toCityRepo.findAll();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> currentRow = new ArrayList<>();

        for (ToCity toCity : all) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(toCity.getName());
            button.setCallbackData(toCity.getName());

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

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

    private InlineKeyboardMarkup selectInlineRoleButtons() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        if ("uz".equals(language)) {
            button1.setText("Haydovchilar");
        } else {
            button1.setText("Драйверы");
        }
        button1.setUrl("https://16.170.202.181:8080/");
        row1.add(button1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if ("uz".equals(language)) {
            button2.setText("Yo'lovchilar");
        } else {
            button2.setText("Пассажиры");
        }
        button2.setCallbackData("PASSENGER"); // Callback data ni o'rnatish
        row2.add(button2);

        rows.add(row1);
        rows.add(row2);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rows);

        return inlineKeyboardMarkup;
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

