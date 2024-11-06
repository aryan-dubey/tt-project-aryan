package com.auriga_tt.controller;

import com.auriga_tt.dto.*;
import com.auriga_tt.model.Tournament;
import com.auriga_tt.service.PlayerService;
import com.auriga_tt.service.TeamService;
import com.auriga_tt.service.TournamentService;
import com.auriga_tt.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tournament")
@PreAuthorize("isAuthenticated()")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @GetMapping("/create")
    public String showCreateTournamentForm(Model model) {
        model.addAttribute("tournamentDTO", new TournamentDTO());
        return "tournament/create";
    }

    @PostMapping("/create")
    public String createTournament(@Valid @ModelAttribute TournamentDTO tournamentDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/create";
        }
        TournamentDTO createdTournament = tournamentService.createTournament(tournamentDTO);
        return "redirect:/tournament/" + createdTournament.getTournamentId();
    }

    @GetMapping("/{id}")
    public String getTournament(@PathVariable Long id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournament", tournament);
        return "tournament/details";
    }

    @GetMapping
    public String getAllTournaments(
            @PageableDefault(sort = "startDate") Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Tournament.TournamentType type,
            Model model) {
        Page<TournamentDTO> tournaments = tournamentService.getAllTournaments(pageable, name, status, type);
        model.addAttribute("tournaments", tournaments);
        return "tournament/list";
    }

    @GetMapping("/{id}/edit")
    public String showEditTournamentForm(@PathVariable Long id, Model model) {
        TournamentDTO tournament = tournamentService.getTournamentById(id);
        model.addAttribute("tournamentDTO", tournament);
        return "tournament/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTournament(@PathVariable Long id, @Valid @ModelAttribute TournamentDTO tournamentDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/edit";
        }
        tournamentService.updateTournament(id, tournamentDTO);
        return "redirect:/tournament/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTournament(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return "redirect:/tournament";
    }

    @GetMapping("/{id}/participants")
    public String getTournamentParticipants(
            @PathVariable Long id,
            Pageable pageable,
            @RequestParam(required = false) String status,
            Model model) {
        Page<TournamentParticipantDTO> participants = tournamentService.getTournamentParticipants(id, pageable, status);
        model.addAttribute("participants", participants);
        model.addAttribute("tournamentId", id);
        return "tournament/participants";
    }

    @GetMapping("/{id}/participants/add")
    public String showAddParticipantForm(@PathVariable Long id, Model model) {
        model.addAttribute("tournamentId", id);
        model.addAttribute("participantDTO", new TournamentParticipantDTO());
        model.addAttribute("players", playerService.getAllPlayers(Pageable.unpaged()).getContent());
        model.addAttribute("teams", teamService.getTeamsByTournament(id, Pageable.unpaged()).getContent());
        return "tournament/add-participant";
    }

    @PostMapping("/{id}/participants/add")
    public String addParticipant(
            @PathVariable Long id,
            @Valid @ModelAttribute TournamentParticipantDTO participantDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/add-participant";
        }
        tournamentService.addParticipant(id, participantDTO);
        return "redirect:/tournament/" + id + "/participants";
    }

    @GetMapping("/{tournamentId}/participants/{participantId}/edit")
    public String showEditParticipantForm(
            @PathVariable Long tournamentId,
            @PathVariable Long participantId,
            Model model) {
        TournamentParticipantDTO participant = tournamentService.getParticipant(tournamentId, participantId);
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("participantDTO", participant);
        return "tournament/edit-participant";
    }

    @PostMapping("/{tournamentId}/participants/{participantId}/edit")
    public String updateParticipant(
            @PathVariable Long tournamentId,
            @PathVariable Long participantId,
            @Valid @ModelAttribute TournamentParticipantDTO participantDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/edit-participant";
        }
        tournamentService.updateParticipant(tournamentId, participantId, participantDTO);
        return "redirect:/tournament/" + tournamentId + "/participants";
    }

    @PostMapping("/{tournamentId}/participants/{participantId}/delete")
    public String removeParticipant(
            @PathVariable Long tournamentId,
            @PathVariable Long participantId) {
        tournamentService.removeParticipant(tournamentId, participantId);
        return "redirect:/tournament/" + tournamentId + "/participants";
    }

    @GetMapping("/{tournamentId}/teams")
    public String getTournamentTeams(
            @PathVariable Long tournamentId,
            @PageableDefault(sort = "teamName") Pageable pageable,
            Model model) {
        Page<TeamDTO> teams = teamService.getTeamsByTournament(tournamentId, pageable);
        model.addAttribute("teams", teams);
        model.addAttribute("tournamentId", tournamentId);
        return "tournament/teams";
    }

    @GetMapping("/{tournamentId}/teams/create")
    public String showCreateTeamForm(@PathVariable Long tournamentId, @PageableDefault(size = 100) Pageable pageable, Model model) {
        TeamDTO teamDTO = new TeamDTO();
        TournamentDTO tournamentDTO = new TournamentDTO();
        tournamentDTO.setTournamentId(tournamentId);
        Page<PlayerDTO> players = playerService.getAllPlayers(pageable);
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("teamDTO", teamDTO);
        model.addAttribute("players", players); // Add players to model
        return "tournament/create-team";
    }

    @PostMapping("/{tournamentId}/teams/create")
    public String createTeam(
            @PathVariable Long tournamentId,
            @Valid @ModelAttribute TeamDTO teamDTO,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("players", playerService.getAllPlayers(Pageable.unpaged()));
            return "tournament/create-team";
        }
        teamService.createTeam(tournamentId, teamDTO);
        return "redirect:/tournament/" + tournamentId + "/teams";
    }

    @GetMapping("/{tournamentId}/teams/{teamId}/edit")
    public String showEditTeamForm(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId,
            Model model) {
        TeamDTO team = teamService.getTeam(teamId);
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("teamDTO", team);
        model.addAttribute("players", playerService.getAllPlayers(Pageable.unpaged()));
        return "tournament/edit-team";
    }

    @PostMapping("/{tournamentId}/teams/{teamId}/edit")
    public String updateTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId,
            @Valid @ModelAttribute TeamDTO teamDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/edit-team";
        }
        teamService.updateTeam(tournamentId, teamId, teamDTO);
        return "redirect:/tournament/" + tournamentId + "/teams";
    }

    @PostMapping("/{tournamentId}/teams/{teamId}/delete")
    public String deleteTeam(
            @PathVariable Long tournamentId,
            @PathVariable Long teamId) {
        teamService.deleteTeam(tournamentId, teamId);
        return "redirect:/tournament/" + tournamentId + "/teams";
    }

    @GetMapping("/players")
    public String getAllPlayers(
            @PageableDefault(sort = "user.username") Pageable pageable,
            Model model) {
        Page<PlayerDTO> players = playerService.getAllPlayers(pageable);
        model.addAttribute("players", players);
        return "tournament/players";
    }

    @GetMapping("/players/create")
    public String showCreatePlayerForm(Model model) {
        model.addAttribute("playerDTO", new PlayerDTO());
        List<UserDTO> availableUsers = userService.getAllUsers();
        model.addAttribute("availableUsers", availableUsers);
        return "tournament/create-player";
    }


    @PostMapping("/players/create")
    public String createPlayer(@Valid @ModelAttribute PlayerDTO playerDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("availableUsers", userService.getAllUsers());
            return "tournament/create-player";
        }
        playerService.createPlayer(playerDTO);
        return "redirect:/tournament/players";
    }

    @GetMapping("/players/{playerId}/edit")
    public String showEditPlayerForm(@PathVariable Long playerId, Model model) {
        PlayerDTO player = playerService.getPlayer(playerId);
        List<UserDTO> availableUsers = userService.getAllUsers();
        model.addAttribute("playerDTO", player);
        model.addAttribute("availableUsers", availableUsers);
        //model.addAttribute("availableUsers", userService.getAllUsers());
        return "tournament/edit-player";
    }

    @PostMapping("/players/{playerId}/edit")
    public String updatePlayer(
            @PathVariable Long playerId,
            @Valid @ModelAttribute PlayerDTO playerDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            return "tournament/edit-player";
        }
        playerService.updatePlayer(playerId, playerDTO);
        return "redirect:/tournament/players";
    }

    @PostMapping("/players/{playerId}/delete")
    public String deletePlayer(@PathVariable Long playerId) {
        playerService.deletePlayer(playerId);
        return "redirect:/tournament/players";
    }
}