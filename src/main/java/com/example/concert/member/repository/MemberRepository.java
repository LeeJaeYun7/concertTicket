package com.example.concert.member.repository;

import com.example.concert.member.domain.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUuid(String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m from Member m WHERE m.uuid = :uuid")
    Optional<Member> findByUuidWithLock(@Param("uuid") String uuid);
    Optional<Member> findByName(String name);
}
