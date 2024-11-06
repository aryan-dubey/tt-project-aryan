package com.auriga_tt.service;

import com.auriga_tt.dto.*;
import com.auriga_tt.model.*;
import com.auriga_tt.exceptions.ResourceNotFoundException;
import com.auriga_tt.repository.*;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TournamentService {

    private static final Logger logger = LoggerFactory.getLogger(TournamentService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TournamentParticipantRepository participantRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public TournamentDTO createTournament(TournamentDTO tournamentDTO) {
        validateTournament(tournamentDTO);
        Tournament tournament = convertToEntity(tournamentDTO);
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setCreatedBy(getCurrentUser());
        tournament.setStatus(Tournament.TournamentStatus.UPCOMING);
        Tournament savedTournament = tournamentRepository.save(tournament);
        logger.info("Tournament created: {}", savedTournament.getTournamentId());
        return convertToDTO(savedTournament);
    }

    public TournamentDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + id));
        return convertToDTO(tournament);
    }

    public Page<TournamentDTO> getAllTournaments(Pageable pageable, String name, String status, Tournament.TournamentType type) {
        Specification<Tournament> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), Tournament.TournamentStatus.valueOf(status)));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tournamentType"), type));
        }

        Page<Tournament> tournaments = tournamentRepository.findAll(spec, pageable);
        return tournaments.map(this::convertToDTO);
    }

    @Transactional
    public TournamentDTO updateTournament(Long id, TournamentDTO tournamentDTO) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + id));

        validateTournament(tournamentDTO);
        updateTournamentFields(tournament, tournamentDTO);
        Tournament updatedTournament = tournamentRepository.save(tournament);
        logger.info("Tournament updated: {}", updatedTournament.getTournamentId());
        return convertToDTO(updatedTournament);
    }

    @Transactional
    public void deleteTournament(Long id) {
        if (!tournamentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tournament not found with id: " + id);
        }
        tournamentRepository.deleteById(id);
        logger.info("Tournament deleted: {}", id);
    }

    public TournamentParticipantDTO getParticipant(Long tournamentId, Long participantId) {
        TournamentParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        if (!participant.getTournament().getTournamentId().equals(tournamentId)) {
            throw new ValidationException("Participant does not belong to the specified tournament");
        }

        return convertToParticipantDTO(participant);
    }

    public Page<TournamentParticipantDTO> getTournamentParticipants(Long tournamentId, Pageable pageable, String status) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));

        Specification<TournamentParticipant> spec = (root, query, cb) -> cb.equal(root.get("tournament"), tournament);

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), TournamentParticipant.ParticipantStatus.valueOf(status)));
        }

        Page<TournamentParticipant> participants = participantRepository.findAll(spec, pageable);
        return participants.map(this::convertToParticipantDTO);
    }

    @Transactional
    public TournamentParticipantDTO addParticipant(Long tournamentId, TournamentParticipantDTO participantDTO) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));

        TournamentParticipant participant = new TournamentParticipant();
        participant.setTournament(tournament);
        participant.setStatus(TournamentParticipant.ParticipantStatus.REGISTERED);
        participant.setCreatedAt(LocalDateTime.now());

        if (participantDTO.getPlayerId() != null) {
            Player player = playerRepository.findById(participantDTO.getPlayerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found"));
            participant.setPlayer(player);
        } else if (participantDTO.getTeamId() != null) {
            Team team = teamRepository.findById(participantDTO.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
            participant.setTeam(team);
        } else {
            throw new ValidationException("Either player or team must be specified");
        }

        TournamentParticipant savedParticipant = participantRepository.save(participant);
        return convertToParticipantDTO(savedParticipant);
    }

    @Transactional
    public TournamentParticipantDTO updateParticipant(Long tournamentId, Long participantId, TournamentParticipantDTO participantDTO) {
        TournamentParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        if (!participant.getTournament().getTournamentId().equals(tournamentId)) {
            throw new ValidationException("Participant does not belong to the specified tournament");
        }

        participant.setStatus(participantDTO.getStatus());
        TournamentParticipant updatedParticipant = participantRepository.save(participant);
        logger.info("Participant updated in tournament {}: {}", tournamentId, updatedParticipant.getParticipantId());
        return convertToParticipantDTO(updatedParticipant);
    }

    @Transactional
    public void removeParticipant(Long tournamentId, Long participantId) {
        TournamentParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));

        if (!participant.getTournament().getTournamentId().equals(tournamentId)) {
            throw new ValidationException("Participant does not belong to the specified tournament");
        }

        participantRepository.delete(participant);
        logger.info("Participant removed from tournament {}: {}", tournamentId, participantId);
    }

    private Tournament convertToEntity(TournamentDTO dto) {
        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setTournamentType(dto.getTournamentType());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setStatus(dto.getStatus());
        tournament.setRules(dto.getRules());
        return tournament;
    }

    private TournamentDTO convertToDTO(Tournament tournament) {
        TournamentDTO dto = new TournamentDTO();
        dto.setTournamentId(tournament.getTournamentId());
        dto.setName(tournament.getName());
        dto.setTournamentType(tournament.getTournamentType());
        dto.setStartDate(tournament.getStartDate());
        dto.setEndDate(tournament.getEndDate());
        dto.setMaxParticipants(tournament.getMaxParticipants());
        dto.setStatus(tournament.getStatus());
        dto.setRules(tournament.getRules());
        dto.setCreatedBy(new UserDTO(tournament.getCreatedBy().getUserId(), tournament.getCreatedBy().getUsername()));
        dto.setCreatedAt(tournament.getCreatedAt());
        return dto;
    }

    private TournamentParticipantDTO convertToParticipantDTO(TournamentParticipant participant) {
        TournamentParticipantDTO dto = new TournamentParticipantDTO();
        dto.setParticipantId(participant.getParticipantId());
        dto.setTournament(convertToDTO(participant.getTournament()));
        dto.setPlayer(participant.getPlayer() != null ? convertToPlayerDTO(participant.getPlayer()) : null);
        dto.setTeam(participant.getTeam() != null ? convertToTeamDTO(participant.getTeam()) : null);
        dto.setStatus(participant.getStatus());
        dto.setCreatedAt(participant.getCreatedAt());
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

    private TeamDTO convertToTeamDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setTeamId(team.getTeamId());
        dto.setPlayer1(convertToPlayerDTO(team.getPlayer1()));
        dto.setPlayer2(convertToPlayerDTO(team.getPlayer2()));
        dto.setTeamName(team.getTeamName());
        dto.setCreatedAt(team.getCreatedAt());
        return dto;
    }

    private void updateTournamentFields(Tournament tournament, TournamentDTO dto) {
        tournament.setName(dto.getName());
        tournament.setTournamentType(dto.getTournamentType());
        tournament.setStartDate(dto.getStartDate());
        tournament.setEndDate(dto.getEndDate());
        tournament.setMaxParticipants(dto.getMaxParticipants());
        tournament.setStatus(dto.getStatus());
        tournament.setRules(dto.getRules());
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private void validateTournament(TournamentDTO tournamentDTO) {
        if (tournamentDTO.getStartDate().isAfter(tournamentDTO.getEndDate())) {
            throw new ValidationException("Start date must be before end date");
        }

        if (tournamentDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }

        if (tournamentDTO.getMaxParticipants() <= 1) {
            throw new ValidationException("Maximum participants must be greater than 1");
        }

        if (tournamentDTO.getName() == null || tournamentDTO.getName().trim().isEmpty()) {
            throw new ValidationException("Tournament name cannot be empty");
        }

        if (tournamentDTO.getTournamentType() == null) {
            throw new ValidationException("Tournament type must be specified");
        }

        if (tournamentDTO.getRules() == null || tournamentDTO.getRules().trim().isEmpty()) {
            throw new ValidationException("Tournament rules cannot be empty");
        }
    }

}
