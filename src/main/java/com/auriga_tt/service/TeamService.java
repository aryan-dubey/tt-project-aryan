package com.auriga_tt.service;

import com.auriga_tt.dto.PlayerDTO;
import com.auriga_tt.dto.TeamDTO;
import com.auriga_tt.model.Team;
import com.auriga_tt.model.Tournament;
import com.auriga_tt.model.Player;
import com.auriga_tt.exceptions.ResourceNotFoundException;
import com.auriga_tt.repository.TeamRepository;
import com.auriga_tt.repository.TournamentRepository;
import com.auriga_tt.repository.PlayerRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private UserService userService;

    public TeamDTO getTeam(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
        return convertToDTO(team);
    }

    public Page<TeamDTO> getTeamsByTournament(Long tournamentId, Pageable pageable) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));
        Page<Team> teams = teamRepository.findByTournament(tournament, pageable);
        return teams.map(this::convertToDTO);
    }

    @Transactional
    public TeamDTO createTeam(Long tournamentId, TeamDTO teamDTO) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));

        validateTeam(teamDTO);
        Team team = convertToEntity(teamDTO);
        team.setTournament(tournament);
        team.setCreatedAt(LocalDateTime.now());

        Team savedTeam = teamRepository.save(team);
        return convertToDTO(savedTeam);
    }

    @Transactional
    public TeamDTO updateTeam(Long tournamentId, Long teamId, TeamDTO teamDTO) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (!team.getTournament().getTournamentId().equals(tournamentId)) {
            throw new ValidationException("Team does not belong to the specified tournament");
        }

        validateTeam(teamDTO);
        updateTeamFields(team, teamDTO);
        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Transactional
    public void deleteTeam(Long tournamentId, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (!team.getTournament().getTournamentId().equals(tournamentId)) {
            throw new ValidationException("Team does not belong to the specified tournament");
        }

        teamRepository.delete(team);
    }

    private Team convertToEntity(TeamDTO dto) {
        Team team = new Team();
        team.setTeamName(dto.getTeamName());

        // Use the IDs directly from DTO
        if (dto.getPlayer1Id() != null) {
            Player player1 = playerRepository.findById(dto.getPlayer1Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Player 1 not found"));
            team.setPlayer1(player1);
        }

        if (dto.getPlayer2Id() != null) {
            Player player2 = playerRepository.findById(dto.getPlayer2Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Player 2 not found"));
            team.setPlayer2(player2);
        }

        return team;
    }


    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setTeamId(team.getTeamId());
        dto.setTeamName(team.getTeamName());
        dto.setTournamentId(team.getTournament().getTournamentId());

        if (team.getPlayer1() != null) {
            dto.setPlayer1Id(team.getPlayer1().getPlayerId());
            dto.setPlayer1(convertToPlayerDTO(team.getPlayer1()));
        }

        if (team.getPlayer2() != null) {
            dto.setPlayer2Id(team.getPlayer2().getPlayerId());
            dto.setPlayer2(convertToPlayerDTO(team.getPlayer2()));
        }

        dto.setCreatedAt(team.getCreatedAt());
        return dto;
    }

    private PlayerDTO convertToPlayerDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setPlayerId(player.getPlayerId());
        dto.setUser(userService.convertToDTO(player.getUser()));
        dto.setSkillLevel(player.getSkillLevel());
        dto.setCreatedAt(player.getCreatedAt());
        return dto;
    }

    private void updateTeamFields(Team team, TeamDTO dto) {
        team.setTeamName(dto.getTeamName());
        Player player1 = playerRepository.findById(dto.getPlayer1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 1 not found"));
        team.setPlayer1(player1);

        Player player2 = playerRepository.findById(dto.getPlayer2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 2 not found"));
        team.setPlayer2(player2);
    }

    private void validateTeam(TeamDTO teamDTO) {
        if (teamDTO.getTeamName() == null || teamDTO.getTeamName().trim().isEmpty()) {
            throw new ValidationException("Team name cannot be empty");
        }
        if (teamDTO.getPlayer1Id() == null || teamDTO.getPlayer2Id() == null) {
            throw new ValidationException("Both players must be specified");
        }
        if (teamDTO.getPlayer1Id().equals(teamDTO.getPlayer2Id())) {
            throw new ValidationException("A team cannot have the same player twice");
        }
    }
}
