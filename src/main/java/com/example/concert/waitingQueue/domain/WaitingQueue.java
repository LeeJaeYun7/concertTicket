package com.example.concert.waitingQueue.domain;

import com.example.concert.concert.domain.Concert;
import com.example.concert.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "waiting_queue")
@NoArgsConstructor
public class WaitingQueue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    private UUID uuid;

    private String token;

    @Column(name = "waiting_number")
    private long waitingNumber;

    @Enumerated(EnumType.STRING)
    private WaitingQueueStatus status;

    @Builder
    public WaitingQueue(Concert concert, UUID uuid, String token, long waitingNumber, WaitingQueueStatus status, LocalDateTime createdAt, LocalDateTime updatedAt){
        this.concert = concert;
        this.uuid = uuid;
        this.token = token;
        this.waitingNumber = waitingNumber;
        this.status = status;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    public static WaitingQueue of(Concert concert, UUID uuid, String token, long waitingNumber){

        LocalDateTime now = LocalDateTime.now();
        WaitingQueueStatus status = WaitingQueueStatus.WAITING;

        return WaitingQueue.builder()
                           .concert(concert)
                           .uuid(uuid)
                           .token(token)
                           .waitingNumber(waitingNumber)
                           .status(status)
                           .createdAt(now)
                           .updatedAt(now)
                           .build();
    }

    public void activateToken(LocalDateTime dateTime){
        this.status = WaitingQueueStatus.ACTIVE;
        this.setUpdatedAt(dateTime);
    }
    public void updateWaitingNumber(){
        this.waitingNumber--;
    }

    public void updateWaitingQueueStatus(WaitingQueueStatus status){
        this.status = status;
    }
}
