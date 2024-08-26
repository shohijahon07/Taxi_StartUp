package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.example.backend.entity.*;
import org.example.backend.repository.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.time.LocalDate;
import java.time.LocalTime;
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

    public TaxiProjectBot(UserRepo userRepo,RouteDriverRepo routeDriverRepo1, FromCityRepo fromCityRepo, ToCityRepo toCityRepo) {
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
    private String[] driver_data_path = new String[3];
    private String[] band_delete_data = new String[2];
    private  String[] status = new String[6];
    private String name="";
    private  String fullName="";
    private String[] dataParts;
    private Integer count=1;
    private UUID idPassenger;
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
                for (FromCity fromCity : fromCityRepo.findAll()) {
                    name = fromCity.getName();
                }

                if (foundUser.getStatus().equals(Status.START)&&message.getText().equalsIgnoreCase("/start")&&foundUser.getIsDriver().equals(false)) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }else if (foundUser.getStatus().equals(Status.SET_CITY_FROM_SAVE)) {
                    foundUser.setStatus(Status.GET_PASSENGER_PATH);
                    userRepo.save(foundUser);
                    driver_data_path[0] = message.getText();
                    sendMessage.setText("Qayerga bormoqchisiz?");
                    sendMessage.setReplyMarkup(toCitysButtonsReply());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
                else if (foundUser.getStatus().equals(Status.GET_PASSENGER_PATH)) {
                    System.out.println(driver_data_path[0]);
                    System.out.println(driver_data_path[1]);
                    driver_data_path[1] = message.getText();
                    String fromCity = driver_data_path[0];
                    String toCity = driver_data_path[1];

                    List<Route_Driver> routeDrivers = routeDriverRepo.findAllByFromCityAndToCity(fromCity, toCity);
                    System.out.println(routeDrivers);

                    if (!routeDrivers.isEmpty()) {
                        Route_Driver routeDriver = routeDrivers.get(0);
                        String carImgFileName = routeDriver.getUser().getCarImg();
                        System.out.println("Car image file name: " + carImgFileName);

                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(chatId);
                        String basePath = "C:/Users/user/Desktop/Taxi_project/backend/files/";
                        String fullPath = basePath + carImgFileName.trim();
                        java.io.File file = new java.io.File(fullPath);
                        System.out.println("Fayl yo'li: " + fullPath);
                        System.out.println("Fayl mavjudmi: " + file.exists());
                        System.out.println("Fayl mavjud bo'lmagan joyni tekshirish: " + file.getAbsolutePath());

                        if (file.exists()) {
                            sendPhoto.setPhoto(new InputFile(file));
                            execute(sendPhoto);
                        } else {
                            sendMessage.setChatId(chatId);
                            sendMessage.setText("Rasmni yuk bo'lmadi: noto'g'ri URL yoki fayl manzili");
                            execute(sendMessage);
                            return;
                        }

                        sendMessage.setChatId(chatId);
                        Long chatId1 = routeDriver.getUser().getChatId();
                        sendMessage.setText(
                                "Telefon raqami: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                        "Sana: " + routeDriver.getDay() + "\n" +
                                        "Bo'sh jo'y soni: " + routeDriver.getCountSide() + " ta\n" +
                                        "Narxi: " + routeDriver.getPrice() + " So'm");
                        sendMessage.setReplyMarkup(Passsenger(chatId1));
                        execute(sendMessage);
                    } else {
                        foundUser.setStatus(Status.BACK);
                        userRepo.save(foundUser);
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Bunday yo'lga hozircha haydovchi yo'q");
                        sendMessage.setReplyMarkup(NotPath());
                        execute(sendMessage);
                    }
                }
                else if(foundUser.getStatus().equals(Status.BACK)&&message.getText().equals("Orqaga qaytish")){
                    sendMessage.setText("Qayerdan");
                    sendMessage.setReplyMarkup(fromCitysButtonsReply());

                    foundUser.setStatus(Status.SET_CITY_FROM_SAVE);
                    userRepo.save(foundUser);
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

                        try {
                            int count = Integer.parseInt(countS);
                            routeDriver.setCountSide(count);

                            routeDriverRepo.save(routeDriver);

                            sendMessage.setText("Jo'ylar soni muvaffaqiyatli qo'shildi");
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    sendMessage.setText(routeDriver2.getFromCity() + "-" + routeDriver2.getToCity() + "\n" +
                                            "Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                            "Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                            routeDriver2.getDay() + " " + routeDriver2.getHour()
                                    );
                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                        } catch (NumberFormatException e) {
                            sendMessage.setText("Kechirasiz, noto'g'ri qiymat kiritildi. Faqat sonlarni kiriting.");
                        }

                        execute(sendMessage);
                    } else {
                        sendMessage.setText(" Xatolik .");
                        execute(sendMessage);
                    }

                }

                else if(foundUser.getStatus().equals(Status.NEW_DAY)){
                    try {
                        System.out.println("kirdi");

                        LocalDate inputDate = validateAndParseDate(message.getText());

                        Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));
                        if (byId.isPresent()) {
                            Route_Driver routeDriver = byId.get();
                            routeDriver.setDay(inputDate);
                            routeDriverRepo.save(routeDriver);

                            sendMessage.setText("Sana muvafaqatli yangilandi");
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    sendMessage.setText(routeDriver2.getFromCity() + "-" + routeDriver2.getToCity() + "\n" +
                                            "Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                            "Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                            routeDriver2.getDay() + " " + routeDriver2.getHour()
                                    );
                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                            execute(sendMessage);
                        } else {
                            sendMessage.setText("Xatolik mavjud.");
                            execute(sendMessage);
                        }
                    } catch (DateTimeParseException e) {
                        sendMessage.setText("Noto'g'ri format. Iltimos, sanani 'kun-oy' formatida kiriting (kun 1-31 gacha, oy 1-12 gacha) va bugungi kundan boshlab yana 2 kun kirita olasiz.");
                        execute(sendMessage);
                    } catch (Exception e) {
                        sendMessage.setText("Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");
                        execute(sendMessage);
                    }
                }
                else if (foundUser.getStatus().equals(Status.NEW_TIME)) {
                    System.out.println("kirdi");
                    System.out.println(id);
                    String time = message.getText().trim();
                    Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));
                    try {
                        Route_Driver routeDriver1 = byId.get();
                        LocalDate day = routeDriver1.getDay();
                        validateTime(time, String.valueOf(day));
                        if (byId.isPresent()) {
                            Route_Driver routeDriver = byId.get();
                            routeDriver.setHour(time);
                            routeDriverRepo.save(routeDriver);

                            sendMessage.setText("Soat muvafaqatli yangilandi");
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    sendMessage.setText(routeDriver2.getFromCity() + "-" + routeDriver2.getToCity() + "\n" +
                                            "Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                            "Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                            routeDriver2.getDay() + " " + routeDriver2.getHour()
                                    );
                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                            execute(sendMessage);
                        } else {
                            sendMessage.setText("Xatolik mavjud.");
                            execute(sendMessage);
                        }
                    } catch (IllegalArgumentException e) {
                        sendMessage.setText(e.getMessage());
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
                        foundUser.setStatus(Status.SET_GO_MONEY);
                        userRepo.save(foundUser);
                        sendMessage.setText("Noto'g'ri format. Iltimos, faqat son kiritishingiz kerak.");
                        execute(sendMessage);
                    }

                }
                else if (foundUser.getStatus().equals(Status.SET_DAY_MONTH)) {
                    try {
                        foundUser.setStatus(Status.SET_TIME);
                        userRepo.save(foundUser);

                        LocalDate inputDate = validateAndParseDate(message.getText());
                        driver_data[4] = inputDate.toString();
                        sendMessage.setText("Soatni kiriting (masalan, 01:50)");
                        execute(sendMessage);
                    } catch (DateTimeParseException e) {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        sendMessage.setText("Noto'g'ri format. Iltimos, sanani 'kun-oy' formatida kiriting (kun 1-31 gacha, oy 1-12 gacha) va bugungi kundan boshlab yana 2 kun kirita olasiz.");
                        execute(sendMessage);
                    } catch (Exception e) {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        sendMessage.setText("Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");
                        execute(sendMessage);
                    }
                }
                else if (foundUser.getStatus().equals(Status.SET_TIME)) {
                    try {
                        List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                        String timeText = message.getText().trim();

                        validateTime(timeText, driver_data[4]);

                        driver_data[5] = timeText;

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

                            Route_Driver routeDriver = new Route_Driver(fromCity, toCity, countSide, price, day, timeText, user);
                            routeDriverRepo.save(routeDriver);

                            sendMessage.setText("Muvafafaqiyatli qo'shildi");
                            sendMessage.setReplyMarkup(directions());
                            execute(sendMessage);
                            driver_data = new String[6];
                        } else {
                            sendMessage.setText("User not found for the given chat ID");
                            execute(sendMessage);
                        }
                    } catch (IllegalArgumentException e) {
                        sendMessage.setText(e.getMessage());
                        execute(sendMessage);
                    }

                }
            }
            else if (message.hasContact()) {
                String phoneNumber = message.getContact().getPhoneNumber();
                String firstName = message.getContact().getFirstName();
                String lastName = message.getContact().getLastName();

                if (lastName == null || lastName.isEmpty()) {
                    fullName = firstName;
                } else {
                    fullName = firstName + " " + lastName;
                }
                foundUser.setStatus(Status.DIRECTIONS);
                userRepo.save(foundUser);
                User admin = selectUser(chatId, phoneNumber);
                if ("uz".equals(language)) {
                    sendMessage.setText("Assalom eleykum botimizga xush kelibsiz! " +
                            "Pastdagi knopkalardan birini tanlang. Siz haydovchimi yoki yo'lovchi?");
                } else if ("ru".equals(language)) {
                    sendMessage.setText("Добро пожаловать в наш бот! Выберите одну из кнопок ниже. Вы водитель или пассажир?");
                }
                sendMessage.setReplyMarkup(selectInlineRoleButtons( chatId));
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
            } else if (data.equals("ru") && !user.getIsDriver()) {
                language = "ru";
                sendMessage.setText("Пожалуйста, отправьте ваш номер телефона!");
                sendMessage.setReplyMarkup(genContactButtons(language));
                execute(sendMessage);
            }else if (data.equals("Passengers")) {
                user.setStatus(Status.SET_CITY_FROM_SAVE);
                user.setFullName(fullName);
                userRepo.save(user);
                sendMessage.setText("Qayerdan");
                sendMessage.setReplyMarkup(fromCitysButtonsReply());
                sendMessage.setChatId(chatId);
                execute(sendMessage);
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
            }
            else if(data.startsWith("money")){
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
            }
            else if(data.startsWith("day")){
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
            else if(data.startsWith("time")){
                String[] dataParts = data.split(":");
                System.out.println("time kirdi");
                sendMessage.setText("Soatni  yangi qiymatini kiriting:");
                if (dataParts.length > 1) {
                    id = String.valueOf(UUID.fromString(dataParts[1]));
                    System.out.println(id);
                    user.setStatus(Status.NEW_TIME);
                    userRepo.save(user);
                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                    execute(sendMessage);
                }
            }
            else if (data.startsWith("next")) {
                String fromCity = driver_data_path[0];
                String toCity = driver_data_path[1];
                List<Route_Driver> routeDrivers = routeDriverRepo.findAllByFromCityAndToCity(fromCity, toCity);
                System.out.println(routeDrivers);

                if (routeDrivers.isEmpty()) {
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Yo'nalishlar topilmadi.");
                    execute(sendMessage);
                    return;
                }

                if (count >= routeDrivers.size()) {
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Bu yo'nalishga tegishlilar tugadi.");
                    count=0;
                    execute(sendMessage);
                } else {

                    Route_Driver routeDriver = routeDrivers.get(count);
                    count++;

                    String carImgFileName = routeDriver.getUser().getCarImg();
                    String basePath = "C:/Users/user/Desktop/Taxi_project/backend/files/";
                    String fullPath = basePath + carImgFileName.trim();
                    java.io.File file = new java.io.File(fullPath);

                    if (file.exists()) {
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(chatId);
                        sendPhoto.setPhoto(new InputFile(file));
                        execute(sendPhoto);
                    } else {
                        sendMessage.setChatId(chatId);
                        sendMessage.setText("Rasmni yuklashda xatolik yuz berdi: noto'g'ri URL yoki fayl manzili.");
                        execute(sendMessage);
                        return;
                    }

                    sendMessage.setChatId(chatId);
                    Long chatId1 = routeDriver.getUser().getChatId();
                    sendMessage.setText(
                            "Telefon raqami: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                    "Sana: " + routeDriver.getDay() + "\n" +
                                    "Bo'sh jo'y soni: " + routeDriver.getCountSide() + " ta\n" +
                                    "Narxi: " + routeDriver.getPrice() + " So'm");
                    sendMessage.setReplyMarkup(Passsenger(chatId1));
                    execute(sendMessage);
                }
            }
            else if(data.startsWith("band")){
                Optional<User> allByChatId = userRepo.findByChatId(chatId);
                User user1 = allByChatId.get();
                idPassenger= user1.getId();
                dataParts = data.split(":");
                if(dataParts.length > 1){
                    sendMessage.setText("Siz " + user1.getFullName() + " yo'lovchini qabul qilasizmi \n" + "Telefon raqami: " + user1.getPhoneNumber());
                    sendMessage.setReplyMarkup(sendBusy());
                    sendMessage.setChatId(dataParts[1]);
                    Message sentMessage = execute(sendMessage);
                    band_delete_data[1]= String.valueOf(sentMessage.getMessageId());
                }
            }

            else if(data.startsWith("comment")){
                String[] driverId = data.split(":");

                // Foydalanuvchini ID orqali topish
                Optional<User> byChatId = userRepo.findByChatId(chatId);
                User user2 = byChatId.get();
                UUID passengerId = user2.getId();

                if (driverId.length > 1) { // driverId[1] mavjudligiga ishonch hosil qilish
                    Optional<User> driverIdData = userRepo.findByChatId(Long.valueOf(driverId[1])); // driverId[0] o'rniga driverId[1]
                    if (driverIdData.isPresent()) {
                        Route_Driver byUser = routeDriverRepo.findByUser(Optional.of(driverIdData.get()));

                        if (byUser.getPassenger().equals(passengerId)) {
                            sendMessage.setText("salonm");
                            execute(sendMessage);
                        } else {
                            sendMessage.setText("siz ni oldin haydovchi ruxsat berish kerak");
                            execute(sendMessage);
                        }
                    } else {
                        sendMessage.setText("Haydovchi topilmadi.");
                        execute(sendMessage);
                    }
                } else {
                    sendMessage.setText("Xato: noto'g'ri format.");
                    execute(sendMessage);
                }
            }

            else if(data.equals("ha")) {
                Optional<User> userOptional1 = userRepo.findByChatId(chatId);

                if (userOptional1.isPresent()) {
                    User user1 = userOptional1.get();
                    Route_Driver byUser = routeDriverRepo.findByUser(Optional.of(user1));

                    if (byUser != null) {
                        List<UUID> currentPassengers = byUser.getPassenger();

                        if (currentPassengers == null) {
                            currentPassengers = new ArrayList<>();
                        }

                        currentPassengers.add(idPassenger);

                        byUser.setPassenger(currentPassengers);
                        Integer countSide = byUser.getCountSide();
                        byUser.setCountSide(countSide - 1);
                        routeDriverRepo.save(byUser);
                        idPassenger=null;
                        List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                        for (Route_Driver routeDriver : routeDriverRepo.findAll()) {
                            if (userIds.contains(routeDriver.getUser().getId())) {
                                sendMessage.setText(routeDriver.getFromCity() + "-" + routeDriver.getToCity() + "\n" +
                                        "Bo'sh-jo'ylar soni: " + routeDriver.getCountSide() + " \n" +
                                        "Narxi: " + routeDriver.getPrice() + " so'm \n" +
                                        routeDriver.getDay() + " " + routeDriver.getHour()
                                );
                                sendMessage.setChatId(chatId);
                                sendMessage.setReplyMarkup(directionData(routeDriver.getId()));
                                execute(sendMessage);
                            }
                        }
                    }
                }
            }

            else if (data.equals("yo'q")) {
                sendMessage.setChatId(chatId);
                sendMessage.setText("Siz yo'lovchini o'chirdiz");
                execute(sendMessage);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                System.out.println(band_delete_data[1]);
                deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1]));
                band_delete_data[1]="";
                execute(deleteMessage);
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
            else if(data.startsWith("del")){
                String[] dataParts = data.split(":");

                id = String.valueOf(UUID.fromString(dataParts[1]));
                Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));
                Route_Driver routeDriver = byId.get();
                UUID id1 = routeDriver.getId();
                routeDriverRepo.deleteById(id1);
                sendMessage.setText("Ma'lumot muvaqafaqatli o'chirildi");
                sendMessage.setChatId(chatId.toString());
                sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                execute(sendMessage);
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
    private ReplyKeyboardMarkup NotPath(){
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        button1.setText("Orqaga qaytish");
        row1.add(button1);


        rows.add(row1);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }
    private InlineKeyboardMarkup sendBusy() {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Ha:");
        button1.setCallbackData("ha");
        firstRow.add(button1);
        rows.add(firstRow);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Yo'q");
        button2.setCallbackData("yo'q");
        secondRow.add(button2);
        rows.add(secondRow);

//        List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
//        InlineKeyboardButton button3 = new InlineKeyboardButton();
//        button3.setText("Sana o'zgartirish ");
//        button3.setCallbackData("day:" + id);
//        secondRow3.add(button3);
//        rows.add(secondRow3);

//        List<InlineKeyboardButton> secondRow4 = new ArrayList<>();
//        InlineKeyboardButton button4 = new InlineKeyboardButton();
//        button4.setText("Soatni o'zgartirish ");
//        button4.setCallbackData("time:" + id);
//        secondRow4.add(button4);
//        rows.add(secondRow4);

//        List<InlineKeyboardButton> secondRow5 = new ArrayList<>();
//        InlineKeyboardButton button5 = new InlineKeyboardButton();
//        button5.setText("O'chirish");
//        button5.setCallbackData("del:" + id);
//        secondRow5.add(button5);
//        rows.add(secondRow5);
        return new InlineKeyboardMarkup(rows);
    }

    private InlineKeyboardMarkup Passsenger(Long id) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Band qilish");
        button1.setCallbackData("band:"+id);
        firstRow.add(button1);
        rows.add(firstRow);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Keyingisi");
        button2.setCallbackData("next:" + id);
        secondRow.add(button2);
        rows.add(secondRow);

        List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("Izohlar");
        button3.setCallbackData("comment:" + id);
        secondRow3.add(button3);
        rows.add(secondRow3);

//        List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
//        InlineKeyboardButton button3 = new InlineKeyboardButton();
//        button3.setText("Sana o'zgartirish ");
//        button3.setCallbackData("day:" + id);
//        secondRow3.add(button3);
//        rows.add(secondRow3);

//        List<InlineKeyboardButton> secondRow4 = new ArrayList<>();
//        InlineKeyboardButton button4 = new InlineKeyboardButton();
//        button4.setText("Soatni o'zgartirish ");
//        button4.setCallbackData("time:" + id);
//        secondRow4.add(button4);
//        rows.add(secondRow4);

//        List<InlineKeyboardButton> secondRow5 = new ArrayList<>();
//        InlineKeyboardButton button5 = new InlineKeyboardButton();
//        button5.setText("O'chirish");
//        button5.setCallbackData("del:" + id);
//        secondRow5.add(button5);
//        rows.add(secondRow5);
        return new InlineKeyboardMarkup(rows);
    }

    private ReplyKeyboardMarkup toCitysButtonsReply() {
        List<ToCity> all = toCityRepo.findAll();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow currentRow = new KeyboardRow();

        for (ToCity toCity : all) {
            KeyboardButton button = new KeyboardButton();
            button.setText(toCity.getName());

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rows.add(new KeyboardRow(currentRow));
                currentRow.clear();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true); // Optional: Resizes the keyboard to fit the screen
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Optional: Hides the keyboard after use

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup fromCitysButtonsReply() {
        List<FromCity> fromCities = fromCityRepo.findAll();
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow currentRow = new KeyboardRow();

        for (FromCity fromCity : fromCities) {
            KeyboardButton button = new KeyboardButton();
            button.setText(fromCity.getName());

            currentRow.add(button);

            if (currentRow.size() == 2) {
                rows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(rows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        return replyKeyboardMarkup;
    }

    private void validateTime(String timeText, String driverDataDay) {
        if (!timeText.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Vaqt formati noto‘g‘ri. Soat: Minut formatida bo'lishi kerak.");
        }

        if (driverDataDay == null || driverDataDay.isEmpty()) {
            throw new IllegalArgumentException("Sana ma'lumotlari mavjud emas.");
        }

        String[] timeParts = timeText.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        if (hour > 23 || minute > 59) {
            throw new IllegalArgumentException("Soat 23 dan kichik va daqiqa 59 dan kichik bo'lishi kerak.");
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LocalDate day;
        try {
            day = LocalDate.parse(driverDataDay);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Sana noto‘g‘ri formatda kiritilgan.");
        }

        if (day.equals(today)) {
            LocalTime inputTime = LocalTime.of(hour, minute);
            if (!inputTime.isAfter(now)) {
                throw new IllegalArgumentException("Bugungi sanani kiritganingizda, vaqt hozirgi vaqtdan keyingi bo'lishi kerak.");
            }
        }
    }

    public LocalDate validateAndParseDate(String dateInput) throws DateTimeParseException {
        if (dateInput == null || dateInput.isEmpty()) {
            throw new DateTimeParseException("Sana kiritilmadi yoki noto'g'ri formatda kiritildi", dateInput, 0);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
        String[] parts = dateInput.split("-");

        if (parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 2) {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                int currentYear = LocalDate.now().getYear();
                LocalDate inputDate = LocalDate.of(currentYear, month, day);
                LocalDate today = LocalDate.now();

                if (inputDate.isBefore(today) || inputDate.isAfter(today.plusDays(2))) {
                    throw new DateTimeParseException("Sana oraliqdan tashqarida", dateInput, 0);
                }

                return inputDate;
            } else {
                throw new DateTimeParseException("Noto'g'ri oy yoki kun", dateInput, 0);
            }
        } else {
            throw new DateTimeParseException("Noto'g'ri format", dateInput, 0);
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
        button3.setText("Sana o'zgartirish ");
        button3.setCallbackData("day:" + id);
        secondRow3.add(button3);
        rows.add(secondRow3);

        List<InlineKeyboardButton> secondRow4 = new ArrayList<>();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        button4.setText("Soatni o'zgartirish ");
        button4.setCallbackData("time:" + id);
        secondRow4.add(button4);
        rows.add(secondRow4);

        List<InlineKeyboardButton> secondRow5 = new ArrayList<>();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        button5.setText("O'chirish");
        button5.setCallbackData("del:" + id);
        secondRow5.add(button5);
        rows.add(secondRow5);
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

    private InlineKeyboardMarkup selectInlineRoleButtons(Long chatId) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        if ("uz".equals(language)) {
            button1.setText("Haydovchilar");
            button1.setUrl("http://192.168.0.81:5174/register?chatId=" + chatId);
        } else {
            button1.setText("Драйверы");
        }
        button1.setCallbackData("Drivers");
        row1.add(button1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if ("uz".equals(language)) {
            button2.setText("Yo'lovchilar");
        } else {
            button2.setText("Пассажиры");
        }
        // Add callback data
        button2.setCallbackData("Passengers");
        row2.add(button2);

        // Add rows to the keyboard
        rows.add(row1);
        rows.add(row2);

        // Create InlineKeyboardMarkup object
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

        for (User user : admins) {
            if (user.getPhoneNumber() != null && user.getPhoneNumber().equals(phoneNumber)) {
                return user;
            }
        }

        if (!admins.isEmpty()) {
            User user = admins.get(0);
            if (user.getPhoneNumber() == null) {
                user.setPhoneNumber(phoneNumber);
                userRepo.save(user);
            }
            return user;
        }

        return null;
    }




}

