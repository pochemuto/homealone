package com.pochemuto.homealone.bot;

import java.util.concurrent.ConcurrentHashMap;

import com.pochemuto.homealone.spring.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import org.telegram.telegrambots.meta.generics.Webhook;
import org.telegram.telegrambots.meta.generics.WebhookBot;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BotController implements Webhook {

    private final ApplicationProperties applicationProperties;

    private final ConcurrentHashMap<String, WebhookBot> callbacks = new ConcurrentHashMap<>();


    @PostMapping("/bot/update/{botPath}")
    public BotApiMethod<?> updateReceived(@PathVariable("botPath") String botPath, @RequestBody Update update) {
        if (callbacks.containsKey(botPath)) {
            try {
                BotApiMethod<?> response = callbacks.get(botPath).onWebhookUpdateReceived(update);
                if (response != null) {
                    response.validate();
                }
                return response;
            } catch (TelegramApiValidationException e) {
                log.error(e.getLocalizedMessage(), e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage(), e);
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/bot/ping")
    public String ping() {
        return "pong, version " + applicationProperties.getVersion();
    }

    @GetMapping("/bot/update/{botPath}")
    public String testReceived(@PathVariable("botPath") String botPath) {
        if (callbacks.containsKey(botPath)) {
            return "Hi there " + botPath + "!";
        } else {
            return "Callback not found for " + botPath;
        }
    }

    @Override
    public void startServer() {
        // do nothing
    }

    @Override
    public void registerWebhook(WebhookBot callback) {
        registerCallback(callback);
    }

    private void registerCallback(WebhookBot callback) {
        if (!callbacks.containsKey(callback.getBotPath())) {
            callbacks.put(callback.getBotPath(), callback);
            log.info("Callback for {} registered", callback.getBotUsername());
        }
    }

    @Override
    public void setInternalUrl(String internalUrl) {
        // do nothing
    }

    @Override
    public void setKeyStore(String keyStore, String keyStorePassword) {
        throw new UnsupportedOperationException("not implemented");
    }
}
