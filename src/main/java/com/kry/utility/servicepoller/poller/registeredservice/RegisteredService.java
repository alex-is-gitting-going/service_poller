package com.kry.utility.servicepoller.poller.registeredservice;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(indexes = {
        @Index(name = "iLastPolledDate", columnList = "lastPolledDate", unique = false)
})
public class RegisteredService {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @Column(length = 2000)
    private String url;
    @Enumerated(EnumType.ORDINAL)
    private ServiceStatus status;
    private LocalDateTime createDate;
    private LocalDateTime lastPolledDate;
    private String username;

    public RegisteredService(String name, String url, String username) {
        this.name = name;
        this.url = url;
        this.username = username;
        this.status = ServiceStatus.WAITING;
        this.createDate = LocalDateTime.now();
    }
}
