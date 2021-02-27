package com.pochemuto.homealone.bot;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import com.google.common.collect.MapDifference.ValueDifference;
import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.IkeaListener;
import com.pochemuto.homealone.ikea.Item;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot implements IkeaListener {

    @Autowired
    private BotProperties properties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IkeaChecker ikeaChecker;

    @Autowired
    private MeterRegistry registry;

    @PostConstruct
    public void postConstruct() {
        log.info("Registering as bot {} with token {}",
                getBotUsername(),
                Objects.requireNonNullElse(getBotToken(), "null").substring(0, 4)
        );
    }

    //region Settings
    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }
    //endregion

    @Override
    public void onUpdateReceived(Update update) {
        registry.timer("bot.update").record(() -> {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var text = update.getMessage().getText().toLowerCase().strip();
                switch (text) {
                    case "/ikea", "/икея" -> ikea(update);
                    case "ping" -> ping(update);
                }
            }
        });
    }

    private void ping(Update update) {
        log.info("Sending pong");
        sendMessage(update.getMessage().getChatId(), "pong");
        log.info("Pong!");
    }

    private void ikea(Update update) {
        long chatId = update.getMessage().getChatId();
        try {
            Integer requestingMessageId = sendMessage(chatId, "Смотрим...");
            if (requestingMessageId == null) {
                return;
            }
            meetUser(update);
            var items = ikeaChecker.getActual();
            sendApiMethod(EditMessageText.builder()
                    .messageId(requestingMessageId)
                    .chatId(String.valueOf(chatId))
                    .text(formatItems(items))
                    .parseMode("markdown")
                    .build()
            );
        } catch (IOException | TelegramApiException e) {
            handleError(chatId, e);
        }
    }

    private void meetUser(Update update) {
        var userId = update.getMessage().getFrom().getId();
        var chatId = update.getMessage().getChat().getId();
        var userName = update.getMessage().getFrom().getUserName();
        var user = User.builder()
                .id(new User.Key(userId.longValue(), chatId))
                .login(userName)
                .build();

        userRepository.save(user);
    }

    private static Comparator<Item> itemsComparator() {
        return comparing(Item::getPrice).reversed().thenComparing(Item::getName);
    }

    private static String formatItems(List<Item> items) {
        var sb = new StringBuilder();
        items.stream()
                .sorted(itemsComparator())
                .forEach(item -> {
                    sb.append("• ");
                    formatItem(sb, item);
                    sb.append("\n");
                });
        return sb.toString();
    }

    private static void formatItem(StringBuilder sb, Item item) {
        sb.append(item.getName()).append(": `").append(item.getPrice()).append("`");
    }

    private String formatChanged(List<ValueDifference<Item>> changed) {
        StringBuilder sb = new StringBuilder();
        changed.stream()
                .sorted(Comparator.comparing(ValueDifference::leftValue, itemsComparator()))
                .forEach(c -> {
                    sb.append("• ");
                    formatItem(sb, c.leftValue());
                    sb.append(" ➝ ");
                    formatItem(sb, c.rightValue());
                    sb.append("\n");
                });
        return sb.toString();
    }

    private void handleError(long chatId, Throwable e) {
        log.error("Error", e);
        sendMessage(chatId, "*Произошла ошибка:* " + e);
    }

    @Nullable
    private Integer sendMessage(long chatId, String text) {
        try {
            Message response = sendApiMethod(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .parseMode("markdown")
                    .build()
            );
            return response.getMessageId();
        } catch (TelegramApiException telegramApiException) {
            log.error("Cannot send reply", telegramApiException);
        }
        return null;
    }

    @Override
    public void onItemsChanged(List<Item> added, List<Item> removed, List<ValueDifference<Item>> changed) {
        var sb = new StringBuilder();
        if (!added.isEmpty()) {
            sb.append("*Новые товары:*\n").append(formatItems(added));
        }
        if (!removed.isEmpty()) {
            sb.append("*Исчезнувшие товары:*\n").append(formatItems(removed));
        }
        if (!changed.isEmpty()) {
            sb.append("*Изменившиеся товары:*\n").append(formatChanged(changed));
        }
        var users = userRepository.findAll();
        var text = sb.toString();
        log.info("Sending message to {} users: {}", users.size(), users);
        for (User user : users) {
            try {
                sendApiMethod(SendMessage.builder()
                        .chatId(String.valueOf(user.getId().getChatId()))
                        .parseMode("markdown")
                        .text(text)
                        .build()
                );
            } catch (TelegramApiException e) {
                handleError(user.getId().getChatId(), e);
            }
        }
    }
}
