package com.pochemuto.homealone.bot;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @Id
    private Key id;

    private String login;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {
        Long id;

        Long chatId;
    }
}
