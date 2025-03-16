package org.example.bidflow.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.bidflow.data.Role;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.winner.entity.Winner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "USER_TABLE")
public class User {

    @Id
    @Column(name = "USER_UUID", nullable = false, length = 50)
    private String userUUID;

    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "NICKNAME", nullable = false, unique = true)
    private String nickname;

    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "profileImage" ,columnDefinition = "TEXT")
    private String profileImage;

    @Builder.Default
    @Column(name = "CREATED_DATE")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Builder.Default
    @Column(name = "MODIFIED_AT")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Winner> winners = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();

public void setNickname(String nickname) {
    this.nickname = nickname;
}

public void setProfileImage(String profileImage) {
    this.profileImage = profileImage;
}
public void setEmail(String email) {
    this.email = email;
}

}
