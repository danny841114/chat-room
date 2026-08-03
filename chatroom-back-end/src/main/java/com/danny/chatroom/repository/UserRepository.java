package com.danny.chatroom.repository;

import com.danny.chatroom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAccount(String account);

    List<User> findAllByIdNot(Long userId);

    @Query("""
                SELECT u FROM User u
                WHERE NOT EXISTS (
                    SELECT m FROM ChatRoomMember m
                    WHERE m.user = u
                    AND m.chatRoom.id = :roomId
                )
            """)
    List<User> findUsersNotInChatRoom(Long roomId);
}