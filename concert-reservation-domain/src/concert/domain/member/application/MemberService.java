package member.application;

import common.CustomException;
import common.ErrorCode;
import common.Loggable;
import member.domain.Member;
import member.domain.MemberRepository;
import member.domain.vo.MemberVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberVO createMember(String name) {
        Member member = Member.of(name);
        Member savedMember = memberRepository.save(member);

        return MemberVO.of(savedMember.getUuid(), savedMember.getName());
    }

    public Member getMemberByUuid(String uuid) {
        return memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, Loggable.NEVER));
    }

    public Member getMemberByUuidWithLock(String uuid) {
        return memberRepository.findByUuidWithLock(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND, Loggable.NEVER));
    }

    @Transactional
    public long getMemberBalance(String uuid) {
        return getMemberByUuidWithLock(uuid).getBalance();
    }

    public void decreaseBalance(String uuid, long price) {
        Member member = getMemberByUuidWithLock(uuid);
        member.updateBalance(member.getBalance()-price);
    }

    public List<MemberVO> getMembers() {
        List<Member> members = memberRepository.findRecentMembers(10000);

        return members.stream()
                .map(member -> new MemberVO(member.getUuid(), member.getName()))
                .toList();
    }
}
