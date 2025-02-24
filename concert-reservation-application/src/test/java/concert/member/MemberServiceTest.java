package concert.member;

import concert.domain.member.entities.MemberEntity;
import concert.domain.member.services.MemberService;
import concert.domain.member.entities.dao.MemberRepository;
import concert.domain.member.entities.vo.MemberVO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

  @Mock
  private MemberRepository memberRepository;

  @InjectMocks
  private MemberService sut;

  @Nested
  @DisplayName("멤버를 생성할 때")
  class 멤버를_생성할때 {

    @Test
    @DisplayName("name이 전달될 때, 멤버가 생성된다")
    void name이_전달될때_멤버가_생성된다() {
      MemberEntity member = MemberEntity.of("Tom Cruise");

      given(memberRepository.save(any(MemberEntity.class))).willReturn(member);

      MemberVO memberVO = sut.createMember("Tom Cruise");

      assertEquals(memberVO.getUuid(), member.getUuid());
      verify(memberRepository, times(1)).save(any(MemberEntity.class));
    }
  }

  @Nested
  @DisplayName("멤버를 조회할 때")
  class 멤버를_조회할때 {

    @Test
    @DisplayName("uuid가 전달될 때, 멤버가 조회된다")
    void uuid가_전달될때_멤버가_조회된다() {
      String name = "Tom Cruise";
      MemberEntity member = MemberEntity.of(name);
      String uuid = member.getUuid();

      given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(member));

      MemberEntity foundMember = sut.getMemberByUuid(uuid);

      assertEquals(foundMember.getUuid(), member.getUuid());
    }

    @Test
    @DisplayName("uuid가 전달될 때, 멤버가 비관적 락을 통해 조회된다")
    void uuid가_전달될때_멤버가_비관적_락을_통해_조회된다() {
      String name = "Tom Cruise";
      MemberEntity member = MemberEntity.of(name);
      String uuid = member.getUuid();

      given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

      MemberEntity foundMember = sut.getMemberByUuidWithLock(uuid);

      assertEquals(foundMember.getUuid(), member.getUuid());
    }
  }

  @Nested
  @DisplayName("멤버의 잔액을 조회할 때")
  class 멤버의_잔액을_조회할때 {

    @Test
    @Disabled
    @DisplayName("uuid가 전달될 때, 멤버의 잔액이 조회된다")
    void uuid가_전달될때_멤버의_잔액이_조회된다() {
      String name = "Tom Cruise";
      MemberEntity member = MemberEntity.of(name);
      String uuid = member.getUuid();

      given(memberRepository.findByUuidWithLock(uuid)).willReturn(Optional.of(member));

      long balance = sut.getMemberBalance(uuid);
      assertEquals(100, balance);
    }
  }
}