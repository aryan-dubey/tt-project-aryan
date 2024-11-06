package com.auriga_tt.service;

import com.auriga_tt.dto.*;
import com.auriga_tt.model.*;
import com.auriga_tt.exceptions.ResourceNotFoundException;
import com.auriga_tt.repository.*;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    public PlayerDTO getPlayer(Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));
        return convertToDTO(player);
    }

    public Page<PlayerDTO> getAllPlayers(Pageable pageable) {
        Page<Player> players = playerRepository.findAll(pageable);
        return players.map(this::convertToDTO);
    }

    @Transactional
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        validatePlayer(playerDTO);
        Player player = convertToEntity(playerDTO);
        player.setCreatedAt(LocalDateTime.now());
        Player savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    @Transactional
    public PlayerDTO updatePlayer(Long playerId, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with id: " + playerId));

        validatePlayer(playerDTO);
        updatePlayerFields(player, playerDTO);
        Player updatedPlayer = playerRepository.save(player);
        return convertToDTO(updatedPlayer);
    }

    @Transactional
    public void deletePlayer(Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new ResourceNotFoundException("Player not found with id: " + playerId);
        }
        playerRepository.deleteById(playerId);
    }

    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setPlayerId(player.getPlayerId());
        dto.setUserId(player.getUser().getUserId());
        dto.setUser(userService.convertToDTO(player.getUser()));
        dto.setSkillLevel(player.getSkillLevel());
        dto.setCreatedAt(player.getCreatedAt());
        return dto;
    }

    private Player convertToEntity(PlayerDTO dto) {
        Player player = new Player();
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));
        player.setUser(user);
        player.setSkillLevel(dto.getSkillLevel());
        return player;
    }

    private void updatePlayerFields(Player player, PlayerDTO dto) {
        player.setSkillLevel(dto.getSkillLevel());
    }

    private void validatePlayer(PlayerDTO playerDTO) {
        if (playerDTO.getUserId() == null) {
            throw new ValidationException("User must be specified for the player");
        }
        if (playerDTO.getSkillLevel() == null || playerDTO.getSkillLevel() < 1 || playerDTO.getSkillLevel() > 10) {
            throw new ValidationException("Skill level must be between 1 and 10");
        }
    }
}