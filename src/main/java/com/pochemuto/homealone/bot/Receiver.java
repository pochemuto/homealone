package com.pochemuto.homealone.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Receiver {

    void onUpdateReceived(Update update);

}
