package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.*;
import com.tennisfantasy.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Service for managing the fantasy draft.
 * 
 * Supports SNAKE draft format where pick order reverses each round.
 * Example: 4 teams, 3 rounds
 * Round 1: 1, 2, 3, 4
 * Round 2: 4, 3, 2, 1
 * Round 3: 1, 2, 3, 4
 */
@Service
public class DraftService {

    private static final Logger logger = LoggerFactory.getLogger(DraftService.class);

    private final LeagueRepository leagueRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final PlayerRepository playerRepository;
    private final RosterRepository rosterRepository;

    // In-memory draft state (for simplicity - in production, use Redis or DB)
    private final Map<Long, DraftState> activeDrafts = new HashMap<>();

    public DraftService(LeagueRepository leagueRepository,
            LeagueMemberRepository leagueMemberRepository,
            PlayerRepository playerRepository,
            RosterRepository rosterRepository) {
        this.leagueRepository = leagueRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.playerRepository = playerRepository;
        this.rosterRepository = rosterRepository;
    }

    /**
     * Start a draft for a league.
     */
    @Transactional
    public DraftState startDraft(Long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));

        // Validate
        if (!league.canStartDraft()) {
            throw new RuntimeException("Cannot start draft - need at least 2 teams");
        }

        if (!"NOT_STARTED".equals(league.getDraftStatus())) {
            throw new RuntimeException("Draft has already started or completed");
        }

        // Get members
        List<LeagueMember> members = leagueMemberRepository.findByLeagueId(leagueId);

        // Randomize draft order
        Collections.shuffle(members);
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setDraftPosition(i + 1);
            leagueMemberRepository.save(members.get(i));
        }

        // Get available players
        List<Player> availablePlayers = getAvailablePlayers(league);

        // Create draft state
        DraftState state = new DraftState();
        state.setLeagueId(leagueId);
        state.setTotalRounds(league.getRosterSize());
        state.setTotalTeams(members.size());
        state.setCurrentRound(1);
        state.setCurrentPick(1);
        state.setCurrentPickIndex(0);
        state.setDraftOrder(members.stream().map(LeagueMember::getId).toList());
        state.setAvailablePlayerIds(availablePlayers.stream().map(Player::getId).toList());
        state.setStatus("IN_PROGRESS");

        // Update league status
        league.setDraftStatus("IN_PROGRESS");
        leagueRepository.save(league);

        activeDrafts.put(leagueId, state);

        logger.info("Started draft for league {} with {} teams, {} rounds",
                league.getName(), members.size(), league.getRosterSize());

        return state;
    }

    /**
     * Make a draft pick.
     */
    @Transactional
    public DraftState makePick(Long leagueId, Long leagueMemberId, Long playerId) {
        DraftState state = activeDrafts.get(leagueId);
        if (state == null) {
            throw new RuntimeException("Draft not in progress for this league");
        }

        // Validate it's this member's turn
        Long currentPicker = state.getCurrentPickerId();
        if (!currentPicker.equals(leagueMemberId)) {
            throw new RuntimeException("Not your turn to pick");
        }

        // Validate player is available
        if (!state.getAvailablePlayerIds().contains(playerId)) {
            throw new RuntimeException("Player is not available");
        }

        // Get entities
        LeagueMember member = leagueMemberRepository.findById(leagueMemberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Create roster entry
        Roster roster = new Roster(member, player, state.getCurrentRound(), state.getCurrentPick());
        roster.setSlotNumber(state.getCurrentRound());
        rosterRepository.save(roster);

        // Remove player from available
        state.removePlayer(playerId);

        // Record pick
        state.addPick(new DraftPick(leagueMemberId, playerId, state.getCurrentRound(), state.getCurrentPick()));

        logger.info("Pick #{}: {} drafted {} ({})",
                state.getCurrentPick(), member.getTeamName(), player.getFullName(), player.getTour());

        // Advance to next pick
        advanceDraft(state);

        return state;
    }

    /**
     * Advance to the next pick using snake draft order.
     */
    private void advanceDraft(DraftState state) {
        int totalTeams = state.getTotalTeams();
        int currentRound = state.getCurrentRound();
        int pickIndexInRound = state.getCurrentPickIndex() % totalTeams;

        // Check if round is complete
        if (pickIndexInRound == totalTeams - 1) {
            // Move to next round
            state.setCurrentRound(currentRound + 1);

            // Check if draft is complete
            if (state.getCurrentRound() > state.getTotalRounds()) {
                completeDraft(state);
                return;
            }
        }

        // Move to next pick
        state.incrementPick();
    }

    /**
     * Complete the draft.
     */
    @Transactional
    private void completeDraft(DraftState state) {
        state.setStatus("COMPLETED");

        League league = leagueRepository.findById(state.getLeagueId())
                .orElseThrow(() -> new RuntimeException("League not found"));
        league.setDraftStatus("COMPLETED");
        leagueRepository.save(league);

        activeDrafts.remove(state.getLeagueId());

        logger.info("Draft completed for league {}", league.getName());
    }

    /**
     * Get current draft state.
     */
    public DraftState getDraftState(Long leagueId) {
        return activeDrafts.get(leagueId);
    }

    /**
     * Get available players for drafting.
     */
    public List<Player> getAvailablePlayers(League league) {
        // Get already drafted player IDs
        List<Long> draftedIds = rosterRepository.findDraftedPlayerIdsByLeagueId(league.getId());

        // Get all active players based on tour type
        List<Player> allPlayers;
        if ("ATP".equals(league.getTourType())) {
            allPlayers = playerRepository.findAtpPlayers();
        } else if ("WTA".equals(league.getTourType())) {
            allPlayers = playerRepository.findWtaPlayers();
        } else {
            allPlayers = playerRepository.findByIsActiveTrue();
        }

        // Filter out drafted players
        return allPlayers.stream()
                .filter(p -> !draftedIds.contains(p.getId()))
                .toList();
    }

    /**
     * Get available players for a league (by ID).
     */
    public List<Player> getAvailablePlayers(Long leagueId) {
        League league = leagueRepository.findById(leagueId)
                .orElseThrow(() -> new RuntimeException("League not found"));
        return getAvailablePlayers(league);
    }

    // ===== Inner classes for draft state =====

    /**
     * Represents the current state of a draft.
     */
    public static class DraftState {
        private Long leagueId;
        private int totalRounds;
        private int totalTeams;
        private int currentRound;
        private int currentPick;
        private int currentPickIndex;
        private List<Long> draftOrder;
        private List<Long> availablePlayerIds;
        private List<DraftPick> picks = new ArrayList<>();
        private String status;

        // Snake draft: odd rounds go forward, even rounds go backward
        public Long getCurrentPickerId() {
            int posInRound = currentPickIndex % totalTeams;
            if (currentRound % 2 == 0) {
                // Even round - reverse order
                posInRound = totalTeams - 1 - posInRound;
            }
            return draftOrder.get(posInRound);
        }

        public void incrementPick() {
            currentPick++;
            currentPickIndex++;
        }

        public void removePlayer(Long playerId) {
            availablePlayerIds = new ArrayList<>(availablePlayerIds);
            availablePlayerIds.remove(playerId);
        }

        public void addPick(DraftPick pick) {
            picks.add(pick);
        }

        // Getters and setters
        public Long getLeagueId() {
            return leagueId;
        }

        public void setLeagueId(Long leagueId) {
            this.leagueId = leagueId;
        }

        public int getTotalRounds() {
            return totalRounds;
        }

        public void setTotalRounds(int totalRounds) {
            this.totalRounds = totalRounds;
        }

        public int getTotalTeams() {
            return totalTeams;
        }

        public void setTotalTeams(int totalTeams) {
            this.totalTeams = totalTeams;
        }

        public int getCurrentRound() {
            return currentRound;
        }

        public void setCurrentRound(int currentRound) {
            this.currentRound = currentRound;
        }

        public int getCurrentPick() {
            return currentPick;
        }

        public void setCurrentPick(int currentPick) {
            this.currentPick = currentPick;
        }

        public int getCurrentPickIndex() {
            return currentPickIndex;
        }

        public void setCurrentPickIndex(int currentPickIndex) {
            this.currentPickIndex = currentPickIndex;
        }

        public List<Long> getDraftOrder() {
            return draftOrder;
        }

        public void setDraftOrder(List<Long> draftOrder) {
            this.draftOrder = draftOrder;
        }

        public List<Long> getAvailablePlayerIds() {
            return availablePlayerIds;
        }

        public void setAvailablePlayerIds(List<Long> availablePlayerIds) {
            this.availablePlayerIds = new ArrayList<>(availablePlayerIds);
        }

        public List<DraftPick> getPicks() {
            return picks;
        }

        public void setPicks(List<DraftPick> picks) {
            this.picks = picks;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * Represents a single draft pick.
     */
    public static class DraftPick {
        private Long leagueMemberId;
        private Long playerId;
        private int round;
        private int overall;

        public DraftPick(Long leagueMemberId, Long playerId, int round, int overall) {
            this.leagueMemberId = leagueMemberId;
            this.playerId = playerId;
            this.round = round;
            this.overall = overall;
        }

        public Long getLeagueMemberId() {
            return leagueMemberId;
        }

        public Long getPlayerId() {
            return playerId;
        }

        public int getRound() {
            return round;
        }

        public int getOverall() {
            return overall;
        }
    }
}
