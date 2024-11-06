package com.auriga_tt.repository;

import com.auriga_tt.model.Player;
import com.auriga_tt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByUser(User user);
}