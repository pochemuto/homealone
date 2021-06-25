package com.pochemuto.homealone.bot;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.MapDifference.ValueDifference;
import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.IkeaListener;
import com.pochemuto.homealone.ikea.Item;
import com.pochemuto.homealone.marafon.MarafonLocalScraper;
import com.pochemuto.homealone.spring.ApplicationProperties;
import com.pochemuto.homealone.strida.Bike;
import com.pochemuto.homealone.strida.StridaChecker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.util.Comparator.comparing;

@Slf4j
@Component
public class TelegramBot implements Receiver, IkeaListener {

    private static final Pattern CYRILLIC = Pattern.compile("[а-яА-Я]");

    @Autowired
    private AbsSender messageSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IkeaChecker ikeaChecker;

    @Autowired
    private MarafonLocalScraper marafonLocalScraper;

    @Autowired
    private StridaChecker strida;

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private ApplicationProperties properties;


    @Override
    public void onUpdateReceived(Update update) {
        registry.timer("bot.update").record(() -> {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var text = update.getMessage().getText().toLowerCase().strip();
                switch (text) {
                    case "/ikea", "/икея" -> ikea(update);
                    case "/ping", "ping" -> pong(update);
                    case "/version" -> version(update);
                    case "/marafon" -> marafon(update);
                    case "/strida" -> strida(update);
                }
            }
        });
    }

    private void strida(Update update) {
        try {
            var actual = strida.getActual();
            var sb = new StringBuilder("Велики:");
            for (Bike bike : actual) {
                sb.append("\n🚲 [").append(bike.title()).append("]")
                        .append("(https://strida.ru/catalog/?id=").append(bike.id()).append(")")
                        .append(", ")
                        .append(bike.description())
                        .append(" `").append(bike.price()).append(" руб`")
                        .append(" • ").append(bike.availability());
            }

            messageSender.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .parseMode("markdown")
                    .disableWebPagePreview(true)
                    .text(sb.toString())
                    .build()
            );

        } catch (IOException | TelegramApiException e) {
            handleError(update.getMessage().getChatId(), e);
        }
    }

    private void version(Update update) {
        var version = properties.getVersion();
        if (version.startsWith("$")) {
            version = "<undefined>";
        }
        sendMessage(update.getMessage().getChatId(), version);
    }

    private void marafon(Update update) {
        long chatId = update.getMessage().getChatId();
        Integer requestingMessageId = sendMessage(chatId, "Смотрим...");
        if (requestingMessageId == null) {
            return;
        }
        meetUser(update);

        try {
            marafonLocalScraper.getData();
        } catch (IOException e) {
            throw new RuntimeException("cannot write screenshots", e);
        }

        File breakfast = new File("./Screenshots/Завтрак.png");
        File brunch = new File("./Screenshots/Перекус 1.png");
        File lunch = new File("./Screenshots/Обед.png");
        File dinner = new File("./Screenshots/Ужин.png");

        sendPhoto(chatId, breakfast);
        sendPhoto(chatId, brunch);
        sendPhoto(chatId, lunch);
        sendPhoto(chatId, dinner);
    }

    private void pong(Update update) {
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
            messageSender.execute(EditMessageText.builder()
                    .messageId(requestingMessageId)
                    .chatId(String.valueOf(chatId))
                    .disableWebPagePreview(true)
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
                    sb.append(item.isReduced() ? "⬇️" : "✅").append(" ");
                    formatItem(sb, item);
                    sb.append("\n");
                });
        sb.append("[Все посудомойки](https://www.ikea.com/ru/ru/cat/posudomoechnye-mashiny-20825/)");
        return sb.toString();
    }

    private static void formatItem(StringBuilder sb, Item item) {
        String cyrillicName = Stream.of(item.getName().split("\\s"))
                .filter(CYRILLIC.asPredicate())
                .collect(Collectors.joining(" "));

        sb.append("[").append(cyrillicName)
                .append("](https://www.ikea.com/ru/ru/p/link-").append(item.getId()).append(")")
                .append(": `").append(item.getPrice()).append("`");
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
            Message response = messageSender.execute(SendMessage.builder()
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

    private BotApiObject sendPhoto(long chatID, File photo) {
        try {
            InputFile targetPhoto = new InputFile(photo);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(String.valueOf(chatID));
            sendPhoto.setPhoto(targetPhoto);
            return messageSender.execute(sendPhoto);
        } catch (TelegramApiException telegramApiException) {
            log.error("Cannot send photo", telegramApiException);
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
                messageSender.execute(SendMessage.builder()
                        .chatId(String.valueOf(user.getId().getChatId()))
                        .parseMode("markdown")
                        .disableWebPagePreview(true)
                        .text(text)
                        .build()
                );
            } catch (TelegramApiException e) {
                handleError(user.getId().getChatId(), e);
            }
        }
    }
}
