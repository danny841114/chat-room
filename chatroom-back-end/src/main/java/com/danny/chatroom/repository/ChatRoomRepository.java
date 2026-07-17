package com.danny.chatroom.repository;

import com.danny.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT r FROM ChatRoom r
        WHERE r.type = 'PRIVATE'
          AND r.id IN (
              SELECT m1.chatRoom.id FROM ChatRoomMember m1 WHERE m1.user.id = :userA
          )
          AND r.id IN (
              SELECT m2.chatRoom.id FROM ChatRoomMember m2 WHERE m2.user.id = :userB
          )
    """)
    Optional<ChatRoom> findPrivateRoomBetweenUsers(@Param("userA") Long userA, @Param("userB") Long userB);
}