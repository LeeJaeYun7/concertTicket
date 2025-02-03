package concert.domain.order.entities;

import concert.domain.shared.entities.BaseTimeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "outbox")
public class OutboxEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sender")
  @Schema(description = "발신자, domain name을 의미한다.")
  private String sender;

  @Column(name = "recipient")
  @Schema(description = "수신자, 메시지를 발송할 Kafka topic을 의미한다.")
  private String recipient;

  @Column(name = "subject")
  @Schema(description = "제목, Event Type을 의미한다.")
  private String subject;

  @Column(name = "message")
  @Schema(description = "메시지, 보낸 Event 내용을 의미한다.")
  private String message;

  @Column(name = "sent")
  @Schema(description = "메시지 발송 여부를 의미한다.")
  private boolean sent;

  @Builder
  public OutboxEntity(String sender, String recipient, String subject, String message, boolean sent) {
    this.sender = sender;
    this.recipient = recipient;
    this.subject = subject;
    this.message = message;
    this.sent = sent;
    this.setCreatedAt(LocalDateTime.now());
    this.setUpdatedAt(LocalDateTime.now());
  }

  public static OutboxEntity of(String sender, String recipient, String subject, String message, boolean sent) {
    return OutboxEntity.builder()
                        .sender(sender)
                        .recipient(recipient)
                        .subject(subject)
                        .message(message)
                        .sent(sent)
                        .build();
  }

  public void updateSent(boolean sent) {
    this.sent = sent;
  }
}
