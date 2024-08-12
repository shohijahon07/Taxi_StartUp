package org.example.backend.repository;

import org.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByFullName(String fullName);


    List<User> findAllByChatId(Long chatId);

    Optional<User> findByChatId(Long chatId);
    @Query("SELECT u.id FROM users u WHERE u.chatId = :chatId")
    List<Long> findAllUserIdsByChatId(@Param("chatId") Long chatId);

}
