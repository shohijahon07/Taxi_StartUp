package org.example.backend;

import org.example.backend.Repasitory.AdminRepo;
import org.example.backend.Telegram_bot.TaxiProjectBot;
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
        AdminRepo adminRepo = context.getBean(AdminRepo.class);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        TaxiProjectBot taxiProjectBot = new TaxiProjectBot(adminRepo);
        telegramBotsApi.registerBot(taxiProjectBot);
    }
}
