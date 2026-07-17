package com.danny.chatroom.repository;

import com.danny.chatroom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    boolean existsByChatRoomIdAndUserId(Long roomId, Long userId);

    ChatRoomMember findByChatRoomIdAndUserId(Long roomId, Long userId);

    List<ChatRoomMember> findByChatRoomId(Long roomId);

    void deleteByChatRoomIdAndUserId(Long roomId, Long userId);

    @Query("""
                SELECT COUNT(m.id)
                FROM ChatRoomMember rm
                JOIN rm.chatRoom.messages m
                WHERE rm.user.id = :userId
                AND rm.chatRoom.id = :roomId
                AND m.createdAt > rm.lastReadAt
                AND m.sender.id <> :userId
                AND m.deletedAt IS NULL
            """)
    Long countUnreadMessages(@Param("userId") Long userId, @Param("roomId") Long roomId);

    @Query("""
                SELECT m FROM ChatRoomMember m
                JOIN FETCH m.chatRoom r
                WHERE m.user.id = :userId
                ORDER BY r.lastMessageTime DESC
            """)
    List<ChatRoomMember> findMyRoomsByUserId(@Param("userId") Long userId);

    @Query("""
                SELECT rm.chatRoom.id, COUNT(m.id)
                FROM ChatRoomMember rm
                JOIN rm.chatRoom.messages m
                WHERE rm.user.id = :userId
                AND rm.chatRoom.id IN :roomIds
                AND m.createdAt > rm.lastReadAt
                AND m.sender.id <> :userId
                AND m.deletedAt IS NULL
                GROUP BY rm.chatRoom.id
            """)
    List<Object[]> countUnreadMessagesBatch(@Param("userId") Long userId, @Param("roomIds") Collection<Long> roomIds);
}