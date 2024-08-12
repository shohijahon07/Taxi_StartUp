package org.example.backend;

import org.example.backend.Telegram_bot.TaxiProjectBot;
import org.example.backend.repository.Route_DriverRepo;
import org.example.backend.repository.UserRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext; // Import ApplicationContext
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) throws TelegramApiException {
        ApplicationContext context = SpringApplication.run(BackendApplication.class, args);
        UserRepo userRepo = context.getBean(UserRepo.class);
        Route_DriverRepo routeDriverRepo = context.getBean(Route_DriverRepo.class);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        TaxiProjectBot taxiProjectBot = new TaxiProjectBot(userRepo,routeDriverRepo);
        telegramBotsApi.registerBot(taxiProjectBot);
    }
}
