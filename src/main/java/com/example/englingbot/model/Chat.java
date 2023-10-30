package com.example.englingbot.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true, of = {"appUser", "isActive"})
@Entity
@Table(name = "chats")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Chat extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUser appUser;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Message> messages;
}
