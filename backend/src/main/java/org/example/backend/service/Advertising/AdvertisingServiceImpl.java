package org.example.backend.service.Advertising;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.AdvertsiningDTO;
import org.example.backend.entity.User;
import org.example.backend.repository.UserRepo;
import org.example.backend.service.Advertising.AdvertisingService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisingServiceImpl implements AdvertisingService {

    private final UserRepo userRepo;
    private final RestTemplate restTemplate;
    private final String apiToken = "6964154747:AAH-HTw_4kM2NIjogEacAQWGJAKRToOzmsc";
    @Override
    public void saveAdvertising(AdvertsiningDTO advertsiningDTO) {

        List<Long> usersChatIds = getAllChatIds();  // Chat ID larni olamiz

        // Har bir foydalanuvchiga reklama jo'natish
        for (Long chatId : usersChatIds) {
            sendMessage(chatId, advertsiningDTO);
        }
    }

    private List<Long> getAllChatIds() {
        return userRepo.findAllByChatIdIsNotNull()
                .stream()
                .map(user -> ((User) user).getChatId())  // User turiga cast qilish
                .collect(Collectors.toList());
    }

    private void sendMessage(Long chatId, AdvertsiningDTO advertsiningDTO) {
        String text = advertsiningDTO.getText();
        String img =advertsiningDTO.getImg();
        String buttonName = advertsiningDTO.getButtonName();
        String link = advertsiningDTO.getLink();

        try {
            // Xabar yuborish
            String sendMessageUrl = "https://api.telegram.org/bot" + apiToken + "/sendPhoto";
            URI uri = UriComponentsBuilder.fromHttpUrl(sendMessageUrl)
                    .queryParam("chat_id", chatId)
                    .queryParam("photo", img)
                    .queryParam("caption", text)
                    .build().toUri();

            restTemplate.getForEntity(uri, String.class);  // Xabarni yuboramiz

            // Agar tugma nomi va link bo'lsa, tugmalarni qo'shamiz
            if (buttonName != null && link != null) {
                String[] buttonNames = buttonName.split(",");
                String[] links = link.split(",");

                for (int i = 0; i < buttonNames.length; i++) {
                    String buttonText = buttonNames[i];
                    String buttonLink = links[i];

                    // Inline button yaratish va yuborish
                    sendInlineKeyboard(chatId, buttonText, buttonLink);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  // Hato yuz berishi mumkin
        }
    }

    // Inline tugma yuborish
    private void sendInlineKeyboard(Long chatId, String buttonText, String buttonLink) {
        String sendInlineKeyboardUrl = "https://api.telegram.org/bot" + apiToken + "/sendMessage";

        try {
            String messageText = "Tugma: " + buttonText;
            String inlineKeyboard = "[[{\"text\": \"" + buttonText + "\", \"url\": \"" + buttonLink + "\"}]]"; // JSON formatida

            URI uri = UriComponentsBuilder.fromHttpUrl(sendInlineKeyboardUrl)
                    .queryParam("chat_id", chatId)
                    .queryParam("text", messageText)
                    .queryParam("reply_markup", inlineKeyboard)  // Inline tugmalarni qo'shamiz
                    .build().toUri();

            restTemplate.getForEntity(uri, String.class);  // Inline tugma yuboramiz
        } catch (Exception e) {
            e.printStackTrace();  // Hato yuz berishi mumkin
        }
    }
}
