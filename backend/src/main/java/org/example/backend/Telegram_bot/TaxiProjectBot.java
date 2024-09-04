package org.example.backend.Telegram_bot;

import lombok.SneakyThrows;
import org.example.backend.entity.*;
import org.example.backend.repository.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.io.File;
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
    private final CommentRepo commentRepo;
    private final RoleRepo roleRepo;
    private String language;
    private String id;

    public TaxiProjectBot(UserRepo userRepo, RouteDriverRepo routeDriverRepo1, FromCityRepo fromCityRepo, ToCityRepo toCityRepo, CommentRepo commentRepo, RoleRepo roleRepo) {
        this.userRepo = userRepo;
        this.routeDriverRepo = routeDriverRepo1;
        this.fromCityRepo = fromCityRepo;
        this.toCityRepo = toCityRepo;
        this.commentRepo=commentRepo;
        this.roleRepo=roleRepo;

    }

    @Override
    public String getBotToken() {
        return "7170837425:AAGYpViG20xIwtYVNacL7jW47pjxoWFWJc0";
    }

    @Override
    public String getBotUsername() {
        return "shift_proejct_taxi_bot";
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
    private String[] driver_chatId_comment=new String[2];
    private String phoneNumber="";
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

                if (foundUser.getStatus().equals(Status.START) && message.getText().equalsIgnoreCase("/start") && foundUser.getIsDriver().equals(false)) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                } else if (foundUser.getStatus().equals(Status.SET_CITY_FROM_SAVE)) {
                    foundUser.setStatus(Status.GET_PASSENGER_PATH);
                    userRepo.save(foundUser);
                    driver_data_path[0] = message.getText();
                    if(language.equals("uz")){
                        sendMessage.setText("Qayerga bormoqchisiz  \uD83D\uDE97");
                    }else if (language.equals("ru")){
                        sendMessage.setText("Куда вы хотите пойти?   \uD83D\uDE97");
                    }
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
                        String basePath = "C:/Users/user/Desktop/Taxi_project/backend/files/";
                        String fullPath = basePath + carImgFileName.trim();
                        File file = new File(fullPath);

                        if (file.exists()) {
                            sendPhoto.setChatId(chatId);  // Set chatId for the photo message here
                            sendPhoto.setPhoto(new InputFile(file));
                            execute(sendPhoto);  // Send the photo

                            // After sending the photo, prepare the follow-up message
                            Long chatId1 = routeDriver.getUser().getChatId();
                            if(language.equals("uz")){
                                sendMessage.setText(
                                        "📱 Telefon raqami: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                                "📅 Sana: " + routeDriver.getDay() + "\n" +
                                                "⏰ Soati: " + routeDriver.getHour() + "\n" +
                                                "🔢 Bo'sh jo'y soni: " + routeDriver.getCountSide() + " ta\n" +
                                                "💵 Narxi: " + routeDriver.getPrice() + " So'm");
                            }else if(language.equals("ru")){
                                sendMessage.setText(
                                        "📱 Номер телефона: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                                "📅 Дата: " + routeDriver.getDay() + "\n" +
                                                "⏰ Час: " + routeDriver.getHour() + "\n" +
                                                "🔢 Количество мест: " + routeDriver.getCountSide() + " ta\n" +
                                                "💵 Цена: " + routeDriver.getPrice() + " So'm");
                            }


                            if (chatId1 != null && chatId1 > 0) {
                                sendMessage.setReplyMarkup(Passsenger(chatId1));
                            } else {
                                sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true)); // Remove any existing keyboard
                            }

                            sendMessage.setChatId(chatId);  // Ensure chatId is set for the text message
                            execute(sendMessage);  // Send the text message with details

                        } else {
                            sendMessage.setChatId(chatId);
                            if(language.equals("uz")){
                                sendMessage.setText("Rasmni yuk bo'lmadi: noto'g'ri URL yoki fayl manzili");

                            }else if(language.equals("ru")){
                                sendMessage.setText("Не удалось загрузить изображение: неверный URL-адрес или местоположение файла.");
                            }
                            execute(sendMessage);
                            return;
                        }
                    } else {
                        foundUser.setStatus(Status.BACK);
                        userRepo.save(foundUser);
                        sendMessage.setChatId(chatId);
                        if(language.equals("uz")){
                            sendMessage.setText("🚫 Bunday yo'lga hozircha haydovchi yo'q");

                        }else if(language.equals("ru")){
sendMessage.setText("🚫 На этой дороге пока нет водителя");
                        }
                        sendMessage.setReplyMarkup(NotPath());
                        execute(sendMessage);
                    }
                }


                else if (foundUser.getStatus().equals(Status.COMMENT_CREATE)) {
                    String chatIdStr = driver_chatId_comment[1];

                    // Check if chatIdStr is a valid number
                    if (isNumeric(chatIdStr)) {
                        Optional<User> byChatId1 = userRepo.findByChatId(Long.valueOf(chatIdStr));
                        User user1 = byChatId1.orElse(null);

                        if (user1 != null) {
                            UUID driver_id = user1.getId();
                            Optional<User> byChatId = userRepo.findByChatId(chatId);
                            User user = byChatId.orElse(null);

                            if (user != null) {
                                String idPassenger = user.getFullName();
                                String name = message.getText();
                                Comment comment = new Comment(name, idPassenger, new User(driver_id));
                                commentRepo.save(comment);
                                if(language.equals("uz")){
                                    sendMessage.setText("✅ Sizning izohingiz qo'shildi"); // "✅" for confirmation

                                }else if(language.equals("ru")){
                                    sendMessage.setText("✅ Ваш комментарий добавлен"); // "✅" for confirmation

                                }

                                execute(sendMessage);
                            } else {
                                if(language.equals("uz")){
                                    sendMessage.setText("🔍 Foydalanuvchi topilmadi."); // "🔍" for searching or not found

                                }else if(language.equals("ru")){
                                    sendMessage.setText("🔍 Пользователь не найден."); // "🔍" for searching or not found

                                }

                                execute(sendMessage);
                            }
                        } else {
                            if(language.equals("uz")){
                                sendMessage.setText("🚫 Haydovchi topilmadi."); // "🚫" for no entry or not available

                            }else if(language.equals("ru")){
                                sendMessage.setText("🚫 Драйвер не найден."); // "🚫" for no entry or not available

                            }

                            execute(sendMessage);
                        }
                    } else {
                        if(language.equals("uz")){
                            sendMessage.setText("Noto'g'ri formatdagi chat ID: " + chatIdStr);

                        }else if(language.equals("ru")){
                            sendMessage.setText("Идентификатор чата в неправильном формате: " + chatIdStr);

                        }
                        execute(sendMessage);
                    }
                }

                else if (foundUser.getStatus().equals(Status.BACK)) {
                    if(language.equals("uz")){
                        sendMessage.setText("📍 Qayerdan"); // "📍" for location or starting point
                    }else if(language.equals("ru")){
                        sendMessage.setText("📍 Откуда"); // "📍" for location or starting point

                    }

                    sendMessage.setReplyMarkup(fromCitysButtonsReply());

                    foundUser.setStatus(Status.SET_CITY_FROM_SAVE);
                    userRepo.save(foundUser);
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);
                }
                Optional<User> byChatId = userRepo.findByChatId(chatId);
                List<Route_Driver> all = routeDriverRepo.findAll();


                if (foundUser.getStatus().equals(Status.START) && message.getText().equalsIgnoreCase("/start") && foundUser.getIsDriver().equals(true)) {
                    sendMessage.setText("Iltimos tilni tanlang! Пожалуйста, выберите язык!");
                    sendMessage.setReplyMarkup(selectLanguageButtons());
                    sendMessage.setChatId(chatId);
                    execute(sendMessage);


                }
                else if (foundUser.getStatus().equals(Status.SET_DIRECTIONS)) {
                    List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                    for (Route_Driver routeDriver : routeDriverRepo.findAll()) {
                        if (userIds.contains(routeDriver.getUser().getId())) {
                            if(language.equals("ru")){
                                sendMessage.setText(
                                        routeDriver.getFromCity() + " ➡️ " + routeDriver.getToCity() + "\n" +
                                                "🪑 Количество вакансий: " + routeDriver.getCountSide() + " \n" +
                                                "💲 Цена: " + routeDriver.getPrice() + " so'm \n" +
                                                "📅 Дата: " + routeDriver.getDay() + "\n" +
                                                "⏰ час: " + routeDriver.getHour()
                                );
                            }else if(language.equals("uz")){
                                sendMessage.setText(
                                        routeDriver.getFromCity() + " ➡️ " + routeDriver.getToCity() + "\n" +
                                                "🪑 Bo'sh ish o'rinlari soni : " + routeDriver.getCountSide() + " \n" +
                                                "💲 Narxi: " + routeDriver.getPrice() + " so'm \n" +
                                                "📅 Sana: " + routeDriver.getDay() + "\n" +
                                                "⏰ soat: " + routeDriver.getHour()
                                );
                            }


                            sendMessage.setReplyMarkup(directionData(routeDriver.getId()));

                            // Execute the message and capture the result
                            Message sentMessage = execute(sendMessage);

                            // Store the message ID from the response
                            band_delete_data[1] = String.valueOf(sentMessage.getMessageId());
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
                            if(language.equals("uz")){
                                sendMessage.setText("✅ Jo'ylar soni muvaffaqiyatli qo'shildi"); // "✅" for success

                            }else if(language.equals("ru")){
                                sendMessage.setText("✅ Количество успешно добавленных мест"); // "✅" for success
                            }
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    if(language.equals("uz")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                                        "💲 Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                                        "📅 Sana: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Soat: " + routeDriver2.getHour()
                                        );
                                    }else if(language.equals("ru")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Количество вакансий: " + routeDriver2.getCountSide() + " \n" +
                                                            "💲 Цена: " + routeDriver2.getPrice() + " сум \n" +
                                                        "📅 Дата: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Час: " + routeDriver2.getHour()
                                        );
                                    }

                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                        } catch (NumberFormatException e) {
                            if(language.equals("uz")){
                                sendMessage.setText("⚠️ Kechirasiz, noto'g'ri qiymat kiritildi. Faqat sonlarni kiriting."); // "⚠️" for warning
                            }else if(language.equals("ru")){
                                sendMessage.setText("⚠️ Извините, было введено неверное значение. Просто введите цифры."); // "⚠️" for warning
                            }

                        }

                        execute(sendMessage);
                    } else {
                        if(language.equals("uz")){
                            sendMessage.setText("❌ Xatolik."); // "❌" for error

                        }else if(language.equals("ru")){
                            sendMessage.setText("❌ Ошибка."); // "❌" for error

                        }

                        execute(sendMessage);
                    }

                } else if (foundUser.getStatus().equals(Status.NEW_DAY)) {
                    try {
                        System.out.println("kirdi");

                        LocalDate inputDate = validateAndParseDate(message.getText());

                        Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));
                        if (byId.isPresent()) {
                            Route_Driver routeDriver = byId.get();
                            routeDriver.setDay(inputDate);
                            routeDriverRepo.save(routeDriver);
                            if(language.equals("uz")){
                                sendMessage.setText("📅 Sana muvaffaqiyatli yangilandi"); // "📅" for date

                            }else if(language.equals("ru")){
                                sendMessage.setText("📅 Дата успешно обновлена"); // "📅" for date

                            }

                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    if(language.equals("uz")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                                        "💲 Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                                        "📅 Sana: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Soat: " + routeDriver2.getHour()
                                        );
                                    }else if(language.equals("ru")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Количество вакансий: " + routeDriver2.getCountSide() + " \n" +
                                                        "💲 Цена: " + routeDriver2.getPrice() + " сум \n" +
                                                        "📅 Дата: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Час: " + routeDriver2.getHour()
                                        );
                                    }


                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                            execute(sendMessage);sendMessage.setText("Xatolik mavjud.");
                            execute(sendMessage);
                        }
                    } catch (DateTimeParseException e) {
                        sendMessage.setText("⚠️ Noto'g'ri format. Iltimos, sanani 'kun-oy' formatida kiriting (kun 1-31 gacha, oy 1-12 gacha) va bugungi kundan boshlab yana 2 kun kirita olasiz.");
                        execute(sendMessage);
                    } catch (Exception e) {
                        sendMessage.setText("❌ Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");
                        execute(sendMessage);
                    }
                } else if (foundUser.getStatus().equals(Status.NEW_TIME)) {
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
if(language.equals("uz")){
    sendMessage.setText("📲 Soat muvaffaqiyatli yangilandi");
}else if(language.equals("ru")){
    sendMessage.setText("📲 Часы успешно обновлены");

}
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver2 : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver2.getUser().getId())) {
                                    if(language.equals("uz")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Bo'sh-jo'ylar soni: " + routeDriver2.getCountSide() + " \n" +
                                                        "💲 Narxi: " + routeDriver2.getPrice() + " so'm \n" +
                                                        "📅 Sana: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Soat: " + routeDriver2.getHour()
                                        );
                                    }else if(language.equals("ru")){
                                        sendMessage.setText(
                                                routeDriver2.getFromCity() + " ➡️ " + routeDriver2.getToCity() + "\n" +
                                                        "🪑 Количество вакансий: " + routeDriver2.getCountSide() + " \n" +
                                                        "💲 Цена: " + routeDriver2.getPrice() + " so'm \n" +
                                                        "📅 Дата: " + routeDriver2.getDay() + "\n" +
                                                        "⏰ Час: " + routeDriver2.getHour()
                                        );
                                    }

                                    sendMessage.setReplyMarkup(directionData(routeDriver2.getId()));
                                    execute(sendMessage);
                                }
                            }
                            execute(sendMessage);
                        } else {
                            if(language.equals("uz")){
                                sendMessage.setText("❌ Xatolik mavjud.");

                            }else if(language.equals("ru")){
                                sendMessage.setText("❌ Есть ошибка.");

                            }
                            execute(sendMessage);
                        }
                    } catch (IllegalArgumentException e) {
                        sendMessage.setText(e.getMessage());
                        execute(sendMessage);
                    }
                } else if (foundUser.getStatus().equals(Status.SET_GO_MONEY)) {
                    try {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        Integer.parseInt(message.getText());
                        driver_data[3] = message.getText();
                        if(language.equals("uz")){
                            sendMessage.setText("🔄 Iltimos, sanani va oyni kiriting (masalan, '04-20' kuni uchun):");

                        }else if(language.equals("ru")){
                            sendMessage.setText("🔄 Пожалуйста, введите дату и месяц (например, «04-20»):");

                        }

                        execute(sendMessage);
                    } catch (NumberFormatException e) {
                        foundUser.setStatus(Status.SET_GO_MONEY);
                        userRepo.save(foundUser);
                        if(language.equals("uz")){
                            sendMessage.setText("⚠️ Noto'g'ri format. Iltimos, faqat son kiritishingiz kerak.");

                        }else if(language.equals("ru")){
                            sendMessage.setText("⚠️ Неверный формат. Пожалуйста, введите только число.");

                        }

                        execute(sendMessage);
                    }

                } else if (foundUser.getStatus().equals(Status.SET_DAY_MONTH)) {
                    try {
                        foundUser.setStatus(Status.SET_TIME);
                        userRepo.save(foundUser);

                        LocalDate inputDate = validateAndParseDate(message.getText());
                        driver_data[4] = inputDate.toString();
                        if(language.equals("uz")){
                            sendMessage.setText("🕒 Soatni kiriting (masalan, 01:50)");

                        }else if(language.equals("ru")){
                            sendMessage.setText("🕒 Введите время (например, 01:50)");

                        }

                        execute(sendMessage);
                    } catch (DateTimeParseException e) {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        if(language.equals("uz")){
                            sendMessage.setText("⚠️ Noto'g'ri format. Iltimos, sanani 'kun-oy' formatida kiriting (kun 1-31 gacha, oy 1-12 gacha) va bugungi kundan boshlab yana 2 kun kirita olasiz.");

                        }else if(language.equals("ru")){
                            sendMessage.setText("⚠️ Неверный формат. Введите дату в формате «день-месяц» (день от 1 до 31, месяц от 1 до 12), и вы сможете ввести еще 2 дня, начиная с сегодняшнего дня.");

                        }

                        execute(sendMessage);
                    } catch (Exception e) {
                        foundUser.setStatus(Status.SET_DAY_MONTH);
                        userRepo.save(foundUser);
                        if(language.equals("uz")){
                            sendMessage.setText("❗ Xatolik yuz berdi. Iltimos, qayta urinib ko'ring.");

                        }else if(language.equals("ru")){
                            sendMessage.setText("❗ Произошла ошибка. Пожалуйста, попробуйте еще раз.");

                        }

                        execute(sendMessage);
                    }
                } else if (foundUser.getStatus().equals(Status.SET_TIME)) {
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
                            if(language.equals("uz")){
                                sendMessage.setText("✅ Muvaffaqiyatli qo'shildi");

                            }else if(language.equals("ru")){
                                sendMessage.setText("✅ Добавлено успешно");

                            }

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
                // Save the message ID of the contact message to delete it later
                int contactMessageId = message.getMessageId();
phoneNumber=message.getContact().getPhoneNumber();
                // Get user contact details
                String phoneNumber = message.getContact().getPhoneNumber();
                String firstName = message.getContact().getFirstName();
                String lastName = message.getContact().getLastName();
                String fullName;

                if (lastName == null || lastName.isEmpty()) {
                    fullName = firstName;
                } else {
                    fullName = firstName + " " + lastName;  // Corrected from 'familiya' to 'lastName'
                }

                foundUser.setStatus(Status.DIRECTIONS);
                userRepo.save(foundUser);

                // Send welcome message and role selection buttons
                if ("uz".equals(language)) {
                    sendMessage.setText("Assalom eleykum botimizga xush kelibsiz! \uD83D\uDC4B\n" +
                            "Pastdagi knopkalardan birini tanlang. ⬇️ Siz haydovchimi \uD83D\uDE95 yoki yo'lovchi \uDDF3?");
                } else if ("ru".equals(language)) {
                    sendMessage.setText("Здравствуйте и добро пожаловать в наш бот! \\uD83D\\uDC4B\\n\" +\n" +
                            " «Выберите одну из кнопок ниже. ⬇\uFE0F Вы водитель \\uD83D\\uDE95 или пассажир \\uDDF3?");
                }
                sendMessage.setReplyMarkup(selectInlineRoleButtons(chatId));
                execute(sendMessage);

                // Delete the previous contact button message after obtaining contact
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1])); // Ensure this is the correct message ID
                execute(deleteMessage);

                // Delete the contact message after processing
                DeleteMessage deleteContactMessage = new DeleteMessage();
                deleteContactMessage.setChatId(chatId);
                deleteContactMessage.setMessageId(contactMessageId); // Use the captured contact message ID
                execute(deleteContactMessage);
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
                sendMessage.setText("☎️ Kontakkingizni yuboring ");
                sendMessage.setReplyMarkup(genContactButtons(language));  // Corrected to use the 'language' variable

                // Send the message with the contact button and store the message ID for later deletion
                Message executedMessage = execute(sendMessage);
                band_delete_data[1] = String.valueOf(executedMessage.getMessageId());
            } else if (data.equals("ru") && !user.getIsDriver()) {
                language = "ru";
                sendMessage.setText("☎\uFE0FОтправьте свой контакт");
                sendMessage.setReplyMarkup(genContactButtons(language));
                execute(sendMessage);
            }else if(data.equals("uz")&user.getIsDriver()){
                language="uz";
                System.out.println(language);
                                    user.setStatus(Status.SET_FROM);
                    userRepo.save(user);
                        sendMessage.setText("🗺️ Yo'nalishingizni kiriting \n 📍 Qayerdan?"); // "🗺️" for route or map and "📍" for location
                    sendMessage.setReplyMarkup(fromCitysButtons());
                    execute(sendMessage);
            }else if(data.equals("ru")&user.getIsDriver()){
                language="ru";
                System.out.println(language);


                user.setStatus(Status.SET_FROM);
                    userRepo.save(user);
                        sendMessage.setText("🗺️ Введите пункт назначения\n" +
                                " \uD83D\uDCCDОткуда?");
                    sendMessage.setReplyMarkup(fromCitysButtons());
                    execute(sendMessage);
            }
            else if (data.equals("Passengers")) {
                user.setStatus(Status.SET_CITY_FROM_SAVE);
                user.setFullName(fullName);
                user.setPhoneNumber(phoneNumber);
                List<Role> roles = new ArrayList<>();
                Role driverRole = roleRepo.findByName("ROLE_DRIVER");
                if (driverRole == null) {
                    driverRole = new Role("ROLE_DRIVER");
                    roleRepo.save(driverRole);
                }
                roles.add(driverRole);
                user.setRoles(roles);
                userRepo.save(user);

                userRepo.save(user);
                if(language.equals("uz")){
                    sendMessage.setText("Qayerdan \uD83C\uDF0D ");
                } else if (language.equals("ru")) {
                    sendMessage.setText("Откуда \uD83C\uDF0D ");
                }
                sendMessage.setReplyMarkup(fromCitysButtonsReply());
                sendMessage.setChatId(chatId);
                execute(sendMessage);
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1]));
                execute(deleteMessage);
            }


            boolean cityFound = false;

            if (user.getStatus().equals(Status.SET_FROM)) {
                for (FromCity city : allFromCities) {
                    if (city.getName().equals(data)) {
                        cityFound = true;
                        driver_data[0] = data;
                        user.setStatus(Status.SET_TO);
                        if(language.equals("uz")){
                            sendMessage.setText("Yo'nalishingizni kiriting \n Qayerga?");

                        }else if(language.equals("ru")){
                            sendMessage.setText("Введите пункт назначения\n куда?");

                        }
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
                if(language.equals("uz")){
                    sendMessage.setText("Jo'ylar soni yangi qiymatini kiriting:");
                }else if(language.equals("ru")){
                    sendMessage.setText("Введите новое значение количества слотов:");

                }
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
                if(language.equals("uz")){
                    sendMessage.setText("Narxni yangi qiymatini kiriting:");
                }else if(language.equals("ru")){
                    sendMessage.setText("Введите новое значение цены:");

                }
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
                if(language.equals("uz")){
                    sendMessage.setText("Sanani yangi qiymatini kiriting:");

                }else if(language.equals("ru")){
                    sendMessage.setText("Введите новое значение даты:");

                }
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
                if(language.equals("uz")){
                    sendMessage.setText("Soatni  yangi qiymatini kiriting:");
                }else if(language.equals("ru")){
                    sendMessage.setText("Введите новое значение часа:");

                }
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
                    if(language.equals("uz")){
                        sendMessage.setText("Yo'nalishlar topilmadi.");

                    }else if(language.equals("ru")){
                        sendMessage.setText("Маршруты не найдены.");
                    }
                    user.setStatus(Status.BACK);
                    userRepo.save(user);
                    execute(sendMessage);
                    return;
                }

                if (count >= routeDrivers.size()) {
                    sendMessage.setChatId(chatId);
                    if(language.equals("uz")){
                        sendMessage.setText("Bu yo'nalishga tegishlilar tugadi.");

                    }else if(language.equals("ru")){
                        sendMessage.setText("Относящиеся к этому направлению закончились.");

                    }
                    count=0;
                    user.setStatus(Status.BACK);
                    userRepo.save(user);
                    execute(sendMessage);
                } else {

                    Route_Driver routeDriver = routeDrivers.get(count);
                    count++;

                    String carImgFileName = routeDriver.getUser().getCarImg();
                    String basePath = "C:/Users/user/Desktop/Taxi_project/backend/files/";
                    String fullPath = basePath + carImgFileName.trim();
                    File file = new File(fullPath);

                    if (file.exists()) {
                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setChatId(chatId);
                        sendPhoto.setPhoto(new InputFile(file));
                        execute(sendPhoto);
                    } else {
                        sendMessage.setChatId(chatId);
                        if(language.equals("uz")){
                            sendMessage.setText("Rasmni yuklashda xatolik yuz berdi: noto'g'ri URL yoki fayl manzili.");

                        }else if(language.equals("ru")){
                            sendMessage.setText("При загрузке изображения произошла ошибка: неверный URL-адрес или местоположение файла.");
                        }
                        execute(sendMessage);
                        return;
                    }


                    Long chatId1 = routeDriver.getUser().getChatId();
                    if(language.equals("uz")){
                        sendMessage.setText(
                                "Telefon raqami: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                        "Sana: " + routeDriver.getDay() + "\n" +
                                        "Bo'sh jo'y soni: " + routeDriver.getCountSide() + " ta\n" +
                                        "Narxi: " + routeDriver.getPrice() + " So'm");
                    }else if(language.equals("ru")){
                        sendMessage.setText(
                                "Номер телефона: " + routeDriver.getUser().getPhoneNumber() + " \n" +
                                        "Дата: " + routeDriver.getDay() + "\n" +
                                        "Пустое число радости: " + routeDriver.getCountSide() + " ta\n" +
                                        "Цена: " + routeDriver.getPrice() + " So'm");
                    }

                    sendMessage.setReplyMarkup(Passsenger(chatId1));
                    execute(sendMessage);
                }
            }
            else if(data.startsWith("band")){
                Optional<User> allByChatId = userRepo.findByChatId(chatId);
                User user1 = allByChatId.get();
                dataParts = data.split(":");

                idPassenger= user1.getId();
                sendMessage.setText("Haydovchi tez orada siz bilan aloqaga chiqadi");
                sendMessage.setChatId(chatId);
                if(dataParts.length > 1){
                    if(language.equals("uz")){
                        sendMessage.setText(
                                "👤 Siz " + user1.getFullName() + " yo'lovchini qabul qilasizmi? \n" +
                                        "📞 Telefon raqami: " + user1.getPhoneNumber()
                        );
                    }else if(language.equals("ru")){
                        sendMessage.setText (
                                "👤 ты" + user1.getFullName() + "Вы принимаете пассажира? \n" +
                                        "📞 Номер телефона:" + user1.getPhoneNumber()
                        );
                    }


                    sendMessage.setReplyMarkup(sendBusy());
                    sendMessage.setChatId(dataParts[1]);
                    Message sentMessage = execute(sendMessage);
                    band_delete_data[1]= String.valueOf(sentMessage.getMessageId());
                }
            }

            else if (data.startsWith("comment")) {
                driver_chatId_comment=data.split(":");

                Optional<User> byChatId = userRepo.findByChatId(chatId);
                User user2 = byChatId.get();
                List<UUID> passengerId = Collections.singletonList(user2.getId());
                System.out.println(passengerId);

                if (driver_chatId_comment.length > 1) {
                    Optional<User> driverIdData = userRepo.findByChatId(Long.valueOf(driver_chatId_comment[1]));
                    if (driverIdData.isPresent()) {
                        Route_Driver byUser = routeDriverRepo.findByUser(Optional.of(driverIdData.get()));

                        if (byUser != null && byUser.getPassenger() != null && byUser.getPassenger().equals(passengerId)) {
                          if(language.equals("uz")){
                              sendMessage.setText("✍\uFE0F Haydovchiga fikr qoldiring");
                          }else if(language.equals("ru")){
                              sendMessage.setText("✍\uFE0F Оставьте идею водителю  ");

                          }
                            user.setStatus(Status.COMMENT_CREATE);
                            userRepo.save(user);
                            execute(sendMessage);
                        } else {
                            if(language.equals("uz")){
                                sendMessage.setText("\uD83D\uDEAB Siz ni oldin haydovchi ruxsat berish kerak");

                            }else if(language.equals("ru")){
                                sendMessage.setText("\uD83D\uDEAB Вам нужно разрешить вам допустить");

                            }
                            execute(sendMessage);
                        }
                    } else {
                        if(language.equals("uz")){
                            sendMessage.setText("\uD83D\uDE97  Haydovchi topilmadi.");
                        }else if(language.equals("ru")){
                            sendMessage.setText("\uD83D\uDE97  Вам нужно разрешить вам допустить");

                        }
                        execute(sendMessage);
                    }
                } else {
                    if(language.equals("uz")){
                        sendMessage.setText(" ❌ Xato: noto'g'ri format.");
                    }else if(language.equals("ru")){
                        sendMessage.setText(" ❌ Ошибка: неверный формат.");
                    }
                    execute(sendMessage);
                }
            }


            else if(data.equals("ha")) {
                Optional<User> userOptional1 = userRepo.findByChatId(chatId);

                if (userOptional1.isPresent()) {
                    User user1 = userOptional1.get();
                    Route_Driver byUser = routeDriverRepo.findByUser(Optional.of(user1));
                    if(byUser.getCountSide()>0){
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
                            List<UUID> userIds = userRepo.findAllUserIdsByChatId(chatId);
                            for (Route_Driver routeDriver : routeDriverRepo.findAll()) {
                                if (userIds.contains(routeDriver.getUser().getId())) {
                                    if(language.equals("uz")){
                                        sendMessage.setText(
                                                routeDriver.getFromCity() + " 🚖 " + routeDriver.getToCity() + "\n" +
                                                        "🛋️ Bo'sh-jo'ylar soni: " + routeDriver.getCountSide() + "\n" +
                                                        "💰 Narxi: " + routeDriver.getPrice() + " so'm\n" +
                                                        "📅  Sana" + routeDriver.getDay() + " ⏰ " + routeDriver.getHour()
                                        );
                                    }else if(language.equals("ru")){
                                        sendMessage.setText(
                                                routeDriver.getFromCity() + " 🚖 " + routeDriver.getToCity() + "\n" +
                                                        "🛋️ Количество вакансий: " + routeDriver.getCountSide() + "\n" +
                                                        "💰 Цена: " + routeDriver.getPrice() + " so'm\n" +
                                                        "📅  Дата" + routeDriver.getDay() + " ⏰ " + routeDriver.getHour()
                                        );
                                    }


                                    sendMessage.setChatId(chatId);
                                    sendMessage.setReplyMarkup(directionData(routeDriver.getId()));
                                    execute(sendMessage);
                                }
                            }
                        }
                    }else {
                        if(language.equals("uz")){
                            sendMessage.setText(
                                    "🚫 Sizda jo'ylar soni tugadi. " +
                                            "📅 Siz yo'lishga borgandan so'ng qayta yo'naltirish qo'shish uchun /start bosing."
                            );
                        }else if(language.equals("ru")){
                            sendMessage.setText(
                                    "🚫У вас закончились места. " +
                                            "📅 Как только вы доберетесь до пути, нажмите /start, чтобы добавить перенаправление."
                            );
                        }

                        user.setStatus(Status.START);
                        routeDriverRepo.deleteById(byUser.getId());
                        execute(sendMessage);
                    }
                }
            }

            else if (data.equals("yo'q")) {
                Optional<User> byChatId = userRepo.findByChatId(chatId);
                if (byChatId.isPresent()) {
                    Route_Driver byUser = routeDriverRepo.findByUser(Optional.of(byChatId.get()));

                    if (byUser != null) { // Ensure byUser is not null
                        if (byUser.getPassenger() != null && byUser.getPassenger().contains(idPassenger)) {
                            byUser.getPassenger().remove(idPassenger);
                            Integer countSide = byUser.getCountSide();
                            byUser.setCountSide(countSide+1);
                            routeDriverRepo.save(byUser);

                            sendMessage.setChatId(chatId);
                            if(language.equals("uz")){
                                sendMessage.setText("✅ Siz yo'lovchini o'chirdiz");
                            }else if(language.equals("ru")){
                                sendMessage.setText("✅ Вы удалили пассажира");

                            }

                            execute(sendMessage);

                            DeleteMessage deleteMessage = new DeleteMessage();
                            deleteMessage.setChatId(chatId);
                            deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1]));
                            band_delete_data[1] = "";
                            execute(deleteMessage);
                        } else {
                            sendMessage.setChatId(chatId);
                            if(language.equals("uz")){
                                sendMessage.setText("✅ Siz yo'lovchini o'chirdiz");
                            }else if(language.equals("ru")){
                                sendMessage.setText("✅ Вы удалили пассажира");

                            }
                            execute(sendMessage);

                            // Only delete message if it was previously set
                            if (!band_delete_data[1].isEmpty()) {
                                DeleteMessage deleteMessage = new DeleteMessage();
                                deleteMessage.setChatId(chatId);
                                deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1]));
                                band_delete_data[1] = "";
                                execute(deleteMessage);
                            }
                        }
                    } else {
                        sendMessage.setChatId(chatId);
                        if(language.equals("uz")){
                            sendMessage.setText("🚗 Haydovchi topilmadi.");

                        }else if(language.equals("ru")){
                            sendMessage.setText("🚗 Драйвер не найден.");

                        }
                        execute(sendMessage);
                    }
                } else {
                    sendMessage.setChatId(chatId);
                    if(language.equals("uz")){
                        sendMessage.setText("👤 Foydalanuvchi topilmadi.");

                    }else if(language.equals("ru")){
                        sendMessage.setText("👤 Foydalanuvchi topilmadi.");

                    }
                    execute(sendMessage);
                }
            }


            if (user.getStatus().equals(Status.SET_TO)) {
                for (ToCity city : allToCities) {
                    if (city.getName().equals(data)) {
                        cityFound = true;
                        user.setStatus(Status.SET_COUNT_SIDE);
                        driver_data[1] = data;
                        if (language.equals("uz")) {
                            sendMessage.setText("🔢 Nechta joy bor?");

                        }else if(language.equals("ru")){
                            sendMessage.setText("🔢 Есть несколько мест?");

                        }
                        sendMessage.setReplyMarkup(countseatButtons(user.isCount()));
                        userRepo.save(user);
                        execute(sendMessage);
                        return;
                    }
                }
            }
            else if (data.startsWith("del")) {
                String[] dataParts = data.split(":");
                id = String.valueOf(UUID.fromString(dataParts[1]));
                Optional<Route_Driver> byId = routeDriverRepo.findById(UUID.fromString(id));

                if(byId.isPresent()) {
                    Route_Driver routeDriver = byId.get();
                    UUID id1 = routeDriver.getId();
                    routeDriverRepo.deleteById(id1);
                    if(language.equals("uz")){
                        sendMessage.setText("✅ Ma'lumot muvaffaqiyatli o'chirildi. " +
                                "Siz yo'nalish qo'shishingiz uchun /start buyrug'ini bering.");
                    }else if(language.equals("ru")){
                        sendMessage.setText("✅Данные успешно удалены. \" +\n" +
                                " «Чтобы добавить маршрут, введите команду /start.");
                    }

                    user.setStatus(Status.START);
                    userRepo.save(user);
                    execute(sendMessage);

                    sendMessage.setChatId(chatId.toString());
                    sendMessage.setReplyMarkup(new ForceReplyKeyboard());
                    DeleteMessage deleteMessage = new DeleteMessage();

                    // Make sure to parse and use the correct message ID
                    if (band_delete_data[1] != null) {
                        System.out.println("keldi " + band_delete_data[1]);
                        deleteMessage.setMessageId(Integer.valueOf(band_delete_data[1]));
                        deleteMessage.setChatId(chatId.toString());
                        execute(deleteMessage);
                    } else {
                        System.out.println("No message ID to delete.");
                    }
                } else {
                    System.out.println("Route_Driver not found with ID: " + id);
                }

            }

            if (user.getStatus().equals(Status.SET_COUNT_SIDE)) {
                if (user.isCount()) {
                    switch (data) {
                        case "1 ta":
                        case "2 ta":
                        case "3 ta":
                        case "4 ta":
                        case "5 ta":
                        case "6 ta":
                        case "1 место":
                        case "2 места":
                        case "3 места":
                        case "4 места":
                        case "5 мест":
                        case "6 мест":
                            driver_data[2] = data;
                            user.setStatus(Status.SET_GO_MONEY);
                            if (language.equals("uz")) {
                                sendMessage.setText("💵 Necha pulga olib ketasiz?");
                            } else if (language.equals("ru")) {
                                sendMessage.setText("💵 Сколько вы возьмете?");
                            }
                            userRepo.save(user);
                            execute(sendMessage);
                            return;
                        default:
                            break;
                    }
                } else {
                    switch (data) {
                        case "1 ta":
                        case "2 ta":
                        case "3 ta":
                        case "4 ta":
                        case "5 ta":
                        case "6 ta":
                        case "7 ta":
                        case "8 ta":
                        case "9 ta":
                        case "10 ta":
                        case "11 ta":
                        case "12 ta":
                        case "1 место":
                        case "2 места":
                        case "3 места":
                        case "4 места":
                        case "5 мест":
                        case "6 мест":
                        case "7 мест":
                        case "8 мест":
                        case "9 мест":
                        case "10 мест":
                        case "11 мест":
                        case "12 мест":
                            driver_data[2] = data;
                            user.setStatus(Status.SET_GO_MONEY);
                            if (language.equals("uz")) {
                                sendMessage.setText("💵 Necha pulga olib ketasiz?");
                            } else if (language.equals("ru")) {
                                sendMessage.setText("💵 Сколько вы возьмете?");
                            }
                            userRepo.save(user);
                            execute(sendMessage);
                            return;
                        default:
                            break;
                    }
                }
            }


        }
    }
    private ReplyKeyboardMarkup NotPath(){
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        if(language.equals("uz")){
            button1.setText("🔙 Orqaga qaytish");

        }else if(language.equals("ru")){
            button1.setText("🔙 Возвращаться");

        }

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
        if(language.equals("uz")){
            button1.setText("✅ Ha:");

        }else if(language.equals("ru")){
        button1.setText(" ✅ Да:");

        }
        button1.setCallbackData("ha");
        firstRow.add(button1);
        rows.add(firstRow);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if(language.equals("uz")){
            button2.setText("❌ Yo'q");

        }else if(language.equals("ru")){
            button2.setText("❌ Нет");

        }
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
        if(language.equals("uz")){
            button1.setText("📌 Band qilish"); // "📌" for 'Book'

        }else if(language.equals("ru")){
            button1.setText("📌Бронирование"); // "📌" for 'Book'

        }
        button1.setCallbackData("band:" + id);
        firstRow.add(button1);
        rows.add(firstRow);

        List<InlineKeyboardButton> firstRow2 = new ArrayList<>();
        InlineKeyboardButton buttonp = new InlineKeyboardButton();
        if(language.equals("uz")){
            buttonp.setText("👤 Haydovchini lichkasiga o'tish"); // "👤" for 'Go to driver's chat'

        }else if(language.equals("ru")){
            buttonp.setText("👤 Перейти на водительское удостоверение"); // "👤" for 'Go to driver's chat'

        }
        buttonp.setUrl("tg://user?id=" + id);
        firstRow2.add(buttonp);
        rows.add(firstRow2);

        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if(language.equals("uz")){
            button2.setText("➡️ Keyingisi"); // "➡️" for 'Next'

        }else if(language.equals("ru")){
            button2.setText("➡️ Следующий"); // "➡️" for 'Next'

        }
        button2.setCallbackData("next:" + id);
        secondRow.add(button2);
        rows.add(secondRow);

        List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        if(language.equals("uz")){
            button3.setText("💬 Izohlar"); // "💬" for 'Comments'

        }else if(language.equals("ru")){
            button3.setText("💬 Примечания"); // "💬" for 'Comments'
        }
        button3.setCallbackData("comment:" + id);
        secondRow3.add(button3);
        rows.add(secondRow3);

//    List<InlineKeyboardButton> secondRow3 = new ArrayList<>();
//    InlineKeyboardButton button3 = new InlineKeyboardButton();
//    button3.setText("📅 Sana o'zgartirish "); // "📅" for 'Change Date'
//    button3.setCallbackData("day:" + id);
//    secondRow3.add(button3);
//    rows.add(secondRow3);

//    List<InlineKeyboardButton> secondRow4 = new ArrayList<>();
//    InlineKeyboardButton button4 = new InlineKeyboardButton();
//    button4.setText("⏰ Soatni o'zgartirish "); // "⏰" for 'Change Time'
//    button4.setCallbackData("time:" + id);
//    secondRow4.add(button4);
//    rows.add(secondRow4);

//    List<InlineKeyboardButton> secondRow5 = new ArrayList<>();
//    InlineKeyboardButton button5 = new InlineKeyboardButton();
//    button5.setText("🗑️ O'chirish"); // "🗑️" for 'Delete'
//    button5.setCallbackData("del:" + id);
//    secondRow5.add(button5);
//    rows.add(secondRow5);

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
            if(language.equals("uz")){

                throw new IllegalArgumentException("⏱️ Vaqt formati noto‘g‘ri. Soat: Minut formatida bo'lishi kerak."); // Time format is incorrect

            }else if(language.equals("ru")){
                throw new IllegalArgumentException("⏱️ Формат времени неправильный. Час: должен быть в минутном формате."); // Time format is incorrect

            }
        }

        if (driverDataDay == null || driverDataDay.isEmpty()) {
            if(language.equals("uz")){
                throw new IllegalArgumentException("🗓️ Sana ma'lumotlari mavjud emas."); // Date information is missing

            }else if(language.equals("ru")){
                throw new IllegalArgumentException("🗓️ Информация о дате отсутствует."); // Date information is missing

            }

        }

        String[] timeParts = timeText.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        if (hour > 23 || minute > 59) {
            if(language.equals("uz")){
                throw new IllegalArgumentException("🕒 Soat 23 dan kichik va daqiqa 59 dan kichik bo'lishi kerak."); // Hour should be less than 23 and minutes less than 59

            } else if ( language.equals("ru")) {
                throw new IllegalArgumentException("🕒 Час должен быть меньше 23, а минуты должны быть меньше 59.."); // Hour should be less than 23 and minutes less than 59


            }
        }

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        LocalDate day = null;
        try {
            day = LocalDate.parse(driverDataDay);
        } catch (DateTimeParseException e) {
            if(language.equals("uz")){
                throw new IllegalArgumentException("📅 Sana noto‘g‘ri formatda kiritilgan."); // Date is in the wrong format

            }else if(language.equals("ru")){
                throw new IllegalArgumentException("📅 Дата введена в неправильном формате."); // Date is in the wrong format

            }

        }

        if (day.equals(today)) {
            LocalTime inputTime = LocalTime.of(hour, minute);
            if (!inputTime.isAfter(now)) {
                if(language.equals("uz")){
                    throw new IllegalArgumentException("📅 Bugungi sanani kiritganingizda, vaqt hozirgi vaqtdan keyingi bo'lishi kerak.");

                }else if(language.equals("ru")){
                    throw new IllegalArgumentException("📅 Когда вы вводите сегодняшнюю дату, время должно быть после текущего времени.");

                }
            }
        }
    }

    public LocalDate validateAndParseDate(String dateInput) throws DateTimeParseException {
        // Check if the date input is null or empty
        if (dateInput == null || dateInput.isEmpty()) {
            if (language.equals("uz")) {
                throw new DateTimeParseException("🗓️ Sana kiritilmadi yoki noto'g'ri formatda kiritildi", dateInput, 0); // Date not provided or entered in the wrong format.
            } else if (language.equals("ru")) {
                throw new DateTimeParseException("🗓️ Дата не предоставлена или введена в неправильном формате", dateInput, 0); // Date not provided or entered in the wrong format.
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM");
        String[] parts = dateInput.split("-");

        // Check if the date format is correct and within the range
        if (parts.length == 2 && parts[0].length() == 2 && parts[1].length() == 2) {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            if (day >= 1 && day <= 31 && month >= 1 && month <= 12) {
                int currentYear = LocalDate.now().getYear();
                LocalDate inputDate = LocalDate.of(currentYear, month, day);
                LocalDate today = LocalDate.now();

                if (inputDate.isBefore(today) || inputDate.isAfter(today.plusDays(2))) {
                    if (language.equals("uz")) {
                        throw new DateTimeParseException("📅 Sana oraliqdan tashqarida", dateInput, 0); // Date is out of range.
                    } else if (language.equals("ru")) {
                        throw new DateTimeParseException("📅 Дата вне допустимого диапазона", dateInput, 0); // Date is out of range.
                    }
                }

                return inputDate;
            } else {
                if (language.equals("uz")) {
                    throw new DateTimeParseException("📅 Noto'g'ri oy yoki kun", dateInput, 0); // Incorrect month or day.
                } else if (language.equals("ru")) {
                    throw new DateTimeParseException("📅 Неправильный месяц или день", dateInput, 0); // Incorrect month or day.
                }
            }
        } else {
            if (language.equals("uz")) {
                throw new DateTimeParseException("📅 Noto'g'ri format", dateInput, 0); // Wrong format.
            } else if (language.equals("ru")) {
                throw new DateTimeParseException("📅 Неправильный формат", dateInput, 0); // Wrong format.
            }
        }

        // Default return, should never reach here because all branches throw an exception if they do not pass
        return null;
    }

    private InlineKeyboardMarkup directionData(UUID id) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        // First row: Button for changing seat count
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        if (language.equals("uz")) {
            button1.setText("🔢 Jo'y sonni o'zgartirish:"); // Uzbek: "Change seat count"
        } else if (language.equals("ru")) {
            button1.setText("🔢 Изменить количество мест:"); // Russian: "Change seat count"
        }
        button1.setCallbackData("place:" + id);
        firstRow.add(button1);
        rows.add(firstRow);

        // Second row: Button for changing price
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if (language.equals("uz")) {
            button2.setText("💵 Narxni o'zgartirish"); // Uzbek: "Change price"
        } else if (language.equals("ru")) {
            button2.setText("💵 Изменить цену"); // Russian: "Change price"
        }
        button2.setCallbackData("money:" + id);
        secondRow.add(button2);
        rows.add(secondRow);

        // Third row: Button for changing date
        List<InlineKeyboardButton> thirdRow = new ArrayList<>();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        if (language.equals("uz")) {
            button3.setText("📅 Sana o'zgartirish"); // Uzbek: "Change date"
        } else if (language.equals("ru")) {
            button3.setText("📅 Изменить дату"); // Russian: "Change date"
        }
        button3.setCallbackData("day:" + id);
        thirdRow.add(button3);
        rows.add(thirdRow);

        // Fourth row: Button for changing time
        List<InlineKeyboardButton> fourthRow = new ArrayList<>();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        if (language.equals("uz")) {
            button4.setText("⏰ Soatni o'zgartirish"); // Uzbek: "Change time"
        } else if (language.equals("ru")) {
            button4.setText("⏰ Изменить время"); // Russian: "Change time"
        }
        button4.setCallbackData("time:" + id);
        fourthRow.add(button4);
        rows.add(fourthRow);

        // Fifth row: Button for deleting
        List<InlineKeyboardButton> fifthRow = new ArrayList<>();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        if (language.equals("uz")) {
            button5.setText("🗑️ O'chirish"); // Uzbek: "Delete"
        } else if (language.equals("ru")) {
            button5.setText("🗑️ Удалить"); // Russian: "Delete"
        }
        button5.setCallbackData("del:" + id);
        fifthRow.add(button5);
        rows.add(fifthRow);

        return new InlineKeyboardMarkup(rows);
    }

    private ReplyKeyboardMarkup directions() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        if (language.equals("uz")) {
            button1.setText("🗺️ Yo'nalishlarim"); // Uzbek: "My Routes"
        } else if (language.equals("ru")) {
            button1.setText("🗺️ Мои маршруты"); // Russian: "My Routes"
        }
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
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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


    private InlineKeyboardMarkup countseatButtons(boolean count) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        InlineKeyboardButton button3 = new InlineKeyboardButton();
        InlineKeyboardButton button4 = new InlineKeyboardButton();
        InlineKeyboardButton button5 = new InlineKeyboardButton();
        InlineKeyboardButton button6 = new InlineKeyboardButton();
        InlineKeyboardButton button7 = new InlineKeyboardButton();
        InlineKeyboardButton button8 = new InlineKeyboardButton();
        InlineKeyboardButton button9 = new InlineKeyboardButton();
        InlineKeyboardButton button10 = new InlineKeyboardButton();
        InlineKeyboardButton button11 = new InlineKeyboardButton();
        InlineKeyboardButton button12 = new InlineKeyboardButton();

        if (language.equals("uz")) {

                button1.setText("1 ta");
                button2.setText("2 ta");
                button3.setText("3 ta");
                button4.setText("4 ta");
                button5.setText("5 ta");
                button6.setText("6 ta");
                button1.setCallbackData("1 ta");
                button2.setCallbackData("2 ta");
                button3.setCallbackData("3 ta");
                button4.setCallbackData("4 ta");
                button5.setCallbackData("5 ta");
                button6.setCallbackData("6 ta");
           if (!count) {
                button7.setText("1 ta");
                button7.setText("2 ta");
                button7.setText("3 ta");
                button7.setText("4 ta");
                button7.setText("5 ta");
                button7.setText("6 ta");
                button7.setText("7 ta");
                button8.setText("8 ta");
                button9.setText("9 ta");
                button10.setText("10 ta");
                button11.setText("11 ta");
                button12.setText("12 ta");
                button7.setCallbackData("1 ta");
                button7.setCallbackData("2 ta");
                button7.setCallbackData("3 ta");
                button7.setCallbackData("4 ta");
                button7.setCallbackData("5 ta");
                button7.setCallbackData("6 ta");
                button7.setCallbackData("7 ta");
                button8.setCallbackData("8 ta");
                button9.setCallbackData("9 ta");
                button10.setCallbackData("10 ta");
                button11.setCallbackData("11 ta");
                button12.setCallbackData("12 ta");
            }
        } else if (language.equals("ru")) {
                button1.setText("1 место");
                button2.setText("2 места");
                button3.setText("3 места");
                button4.setText("4 места");
                button5.setText("5 мест");
                button6.setText("6 мест");
                button1.setCallbackData("1 место");
                button2.setCallbackData("2 места");
                button3.setCallbackData("3 места");
                button4.setCallbackData("4 места");
                button5.setCallbackData("5 мест");
                button6.setCallbackData("6 мест");

             if (!count) {
                button7.setText("1 мест");
                button7.setText("2 мест");
                button7.setText("3 мест");
                button7.setText("4 мест");
                button7.setText("5 мест");
                button7.setText("6 мест");
                button7.setText("7 мест");
                button8.setText("8 мест");
                button9.setText("9 мест");
                button10.setText("10 мест");
                button11.setText("11 мест");
                button12.setText("12 мест");
                button7.setCallbackData("1 мест");
                button7.setCallbackData("2 мест");
                button7.setCallbackData("3 мест");
                button7.setCallbackData("4 мест");
                button7.setCallbackData("5 мест");
                button7.setCallbackData("6 мест");
                button7.setCallbackData("7 мест");
                button8.setCallbackData("8 мест");
                button9.setCallbackData("9 мест");
                button10.setCallbackData("10 мест");
                button11.setCallbackData("11 мест");
                button12.setCallbackData("12 мест");
            }
        }

        row1.add(button1);
        row1.add(button2);
        row2.add(button3);
        row2.add(button4);
        row3.add(button5);
        row3.add(button6);

        rows.add(row1);
        rows.add(row2);
        rows.add(row3);

        // Adding extra rows if count is false
        if (!count) {
            List<InlineKeyboardButton> row4 = new ArrayList<>();
            List<InlineKeyboardButton> row5 = new ArrayList<>();

            row4.add(button7);
            row4.add(button8);
            row5.add(button9);
            row5.add(button10);

            rows.add(row4);
            rows.add(row5);

            if (language.equals("uz")) {
                List<InlineKeyboardButton> row6 = new ArrayList<>();
                row6.add(button11);
                row6.add(button12);
                rows.add(row6);
            } else if (language.equals("ru")) {
                List<InlineKeyboardButton> row6 = new ArrayList<>();
                row6.add(button11);
                row6.add(button12);
                rows.add(row6);
            }
        }

        return new InlineKeyboardMarkup(rows);
    }

    private ReplyKeyboardMarkup selectRoleButtons() {
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton();
        if ("uz".equals(language)) {
            button1.setText("Haydovchi  \uD83D\uDE95");
        }else{
            button1.setText("Драйверы  ");
        }
        row1.add(button1);

        KeyboardRow row2 = new KeyboardRow();
        KeyboardButton button2 = new KeyboardButton();
        if("uz".equals(language)){
            button2.setText("Yo'lovchi \uD83E\uDDF3");
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
            button1.setText("🚖 Haydovchilar");
            button1.setUrl("http://192.168.0.81:5174/register?chatId=" + chatId);
        } else {
            button1.setText("🚖 Драйверы");
            button1.setUrl("http://192.168.0.81:51744/register?chatId=" + chatId);
        }
        button1.setCallbackData("Drivers");
        row1.add(button1);

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        InlineKeyboardButton button2 = new InlineKeyboardButton();
        if ("uz".equals(language)) {
            button2.setText("🧳 Yo'lovchilar"); // "🧳" Luggage icon for Passengers
        } else {
            button2.setText("🧳 Пассажиры"); // "🧳" Luggage icon for Passengers
        }
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
            buttonText = "☎\uFE0F Kontakni yuborish ";
        } else if ("ru".equals(language)) {
            buttonText = "☎\uFE0F Отправить контакт \uD83D\uDCDE";
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

