package concert.domain.member.services;

import concert.domain.member.entities.MemberEntity;
import concert.domain.member.entities.dao.MemberRepository;
import concert.domain.member.entities.vo.MemberVO;
import concert.domain.member.exceptions.MemberException;
import concert.domain.member.exceptions.MemberExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

  private final MemberRepository memberRepository;

  public MemberService(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Transactional
  public MemberVO createMember(String name) {
    MemberEntity member = MemberEntity.of(name);
    MemberEntity savedMember = memberRepository.save(member);

    return MemberVO.of(savedMember.getUuid(), savedMember.getName());
  }

  public MemberEntity getMemberByUuid(String uuid) {
    return memberRepository.findByUuid(uuid)
                           .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
  }

  public MemberEntity getMemberByUuidWithLock(String uuid) {
    return memberRepository.findByUuidWithLock(uuid)
                           .orElseThrow(() -> new MemberException(MemberExceptionType.MEMBER_NOT_FOUND));
  }

  @Transactional
  public long getMemberBalance(String uuid) {
    return getMemberByUuidWithLock(uuid).getBalance();
  }

  public List<MemberVO> getMembers() {
    List<MemberEntity> members = memberRepository.findRecentMembers(10000);

    return members.stream()
                  .map(member -> new MemberVO(member.getUuid(), member.getName()))
                  .toList();
  }
}
