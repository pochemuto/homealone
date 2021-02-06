package com.pochemuto.homealone.bot;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(UserId.class)
public class User {
    @Id
    Long id;

    @Id
    Long chatId;

    String login;
}
