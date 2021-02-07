package com.pochemuto.homealone.bot;

import java.io.IOException;
import java.util.List;

import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.IkeaListener;
import com.pochemuto.homealone.ikea.Item;
import com.pochemuto.homealone.spring.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements IkeaListener {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IkeaChecker ikeaChecker;

    //region Settings
    @Override
    public String getBotUsername() {
        return properties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getBot().getToken();
    }
    //endregion

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var text = update.getMessage().getText();
            switch (text) {
                case "/ikea", "/икея" -> ikea(update);
            }
        }
    }

    private void ikea(Update update) {
        try {
            meetUser(update);
            var items = ikeaChecker.getActual();
            sendMessage(update.getMessage().getChatId(), formatItems(items));
        } catch (IOException e) {
            handleError(update.getMessage().getChatId(), e);
        }
    }

    private void meetUser(Update update) {
        var userId = update.getMessage().getFrom().getId();
        var chatId = update.getMessage().getChat().getId();
        var userName = update.getMessage().getFrom().getUserName();
        var user = User.builder()
                .id(userId.longValue())
                .chatId(chatId)
                .login(userName)
                .build();

        userRepository.save(user);
    }

    private String formatItems(List<Item> items) {
        var sb = new StringBuilder();
        items.stream()
                .sorted(comparing(Item::getPrice).reversed().thenComparing(Item::getName))
                .forEach(item ->
                        sb.append("• ").append(item.getName()).append(": `").append(item.getPrice()).append("`\n")
                );
        return sb.toString();
    }

    private void handleError(long chatId, Throwable e) {
        log.error("Error", e);
        sendMessage(chatId, "*Произошла ошибка:* " + e);
    }

    private void sendMessage(long chatId, String text) {
        try {
            sendApiMethod(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .parseMode("markdown")
                    .build()
            );
        } catch (TelegramApiException telegramApiException) {
            log.error("Cannot send reply", telegramApiException);
        }
    }

    @Override
    public void onItemsChanged(List<Item> added, List<Item> removed) {
        var sb = new StringBuilder();
        if (!added.isEmpty()) {
            sb.append("*Новые товары:*\n").append(formatItems(added));
        }
        if (!removed.isEmpty()) {
            sb.append("*Исчезнувшие товары:*\n").append(formatItems(removed));
        }
        var users = userRepository.findAll();
        var text = sb.toString();
        log.info("Sending message to {} users: {}", users.size(), users);
        for (User user : users) {
            try {
                sendApiMethod(SendMessage.builder()
                        .chatId(String.valueOf(user.getChatId()))
                        .parseMode("markdown")
                        .text(text)
                        .build()
                );
            } catch (TelegramApiException e) {
                handleError(user.getChatId(), e);
            }
        }
    }
}
