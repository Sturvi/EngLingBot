package com.example.englingbot.model;

import com.example.englingbot.model.enums.UserRoleEnum;
import com.example.englingbot.model.enums.UserStateEnum;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "telegram_chat_id", name = "telegram_chat_id_unique")
})
@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class AppUser extends AbstractEntity {

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "user_status")
    private boolean userStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state", columnDefinition = "varchar(255) default 'MAIN'")
    private UserStateEnum userState;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", columnDefinition = "varchar(255) default 'USER'")
    private UserRoleEnum role;
}
