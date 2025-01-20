package concert.domain.concerthall.domain;

import concert.domain.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "concert_hall")
@NoArgsConstructor
public class ConcertHall extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name")
  private String name;

  @Column(name = "address")
  private String address;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "home_page")
  private String homePage;

  @Builder
  public ConcertHall(String name, String address, String phoneNumber, String homePage) {
    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.homePage = homePage;
  }

  public static ConcertHall of(String name, String address, String phoneNumber, String homePage) {
    return ConcertHall.builder()
                      .name(name)
                      .address(address)
                      .phoneNumber(phoneNumber)
                      .homePage(homePage)
                      .build();
  }
}
