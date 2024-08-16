package org.example.backend;

import org.example.backend.Telegram_bot.TaxiProjectBot;
import org.example.backend.repository.FromCityRepo;
import org.example.backend.repository.RouteDriverRepo;
import org.example.backend.repository.ToCityRepo;
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
        RouteDriverRepo routeDriverRepo = context.getBean(RouteDriverRepo.class);
        FromCityRepo fromCityRepo = context.getBean(FromCityRepo.class);
        ToCityRepo toCityRepo = context.getBean(ToCityRepo.class);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        TaxiProjectBot taxiProjectBot = new TaxiProjectBot(userRepo,routeDriverRepo,fromCityRepo,toCityRepo);
        telegramBotsApi.registerBot(taxiProjectBot);
    }
}
