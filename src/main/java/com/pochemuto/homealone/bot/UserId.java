package com.pochemuto.homealone.bot;

import java.io.Serializable;

import javax.persistence.Id;

import lombok.Data;

@Data
public class UserId implements Serializable {
    @Id
    Long id;

    @Id
    Long chatId;
}
