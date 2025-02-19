package concert.domain.waitingqueue.entities;

import lombok.Data;

@Data
public class WaitingDTO {
  private String timestamp;
  private String uuid;
  private String token;
  private long waitingRank;

  WaitingDTO(String timestamp, String uuid) {
    this.timestamp = timestamp;
    this.uuid = uuid;
  }

  WaitingDTO(String timestamp, String uuid, String token) {
    this.timestamp = timestamp;
    this.uuid = uuid;
    this.token = token;
  }

  WaitingDTO(String timestamp, String uuid, String token, long waitingRank) {
    this.timestamp = timestamp;
    this.uuid = uuid;
    this.token = token;
    this.waitingRank = waitingRank;
  }

  public static WaitingDTO of(String uuid) {
    String timestamp = Long.toString(System.currentTimeMillis());
    return new WaitingDTO(timestamp, uuid);
  }

  public boolean isUuidEquals(String token) {
    String uuid = token.split(":")[1];
    return this.uuid.equals(uuid);
  }

  public String getToken() {
    if (null == this.token) {
      this.token = new StringBuilder(timestamp).append(":").append(uuid).toString();
    }
    return token;
  }


  public static WaitingDTO parse(String entry) {
    String[] split = entry.split(":");
    String timestamp = split[0];
    String uuid = split[1];

    return new WaitingDTO(timestamp, uuid, entry);
  }

  public static WaitingDTO parse(String entry, long waitingRank) {
    String[] split = entry.split(":");
    String timestamp = split[0];
    String uuid = split[1];

    return new WaitingDTO(timestamp, uuid, entry, waitingRank);
  }
}
