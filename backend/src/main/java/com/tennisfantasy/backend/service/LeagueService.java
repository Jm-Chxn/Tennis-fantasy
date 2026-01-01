package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.League;
import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.model.User;
import com.tennisfantasy.backend.repository.LeagueMemberRepository;
import com.tennisfantasy.backend.repository.LeagueRepository;
import com.tennisfantasy.backend.repository.RosterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for League operations.
 * 
 * Handles league creation, joining, and management.
 */
@Service
public class LeagueService {

    private final LeagueRepository leagueRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final RosterRepository rosterRepository;

    public LeagueService(LeagueRepository leagueRepository, LeagueMemberRepository leagueMemberRepository, RosterRepository rosterRepository) {
        this.leagueRepository = leagueRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.rosterRepository = rosterRepository;
    }

    /**
     * Get all leagues.
     */
    public List<League> getAllLeagues() {
        return leagueRepository.findAll();
    }

    /**
     * Get league by ID.
     */
    public Optional<League> getLeagueById(Long id) {
        return leagueRepository.findById(id);
    }

    /**
     * Get league by join code.
     */
    public Optional<League> getLeagueByJoinCode(String joinCode) {
        return leagueRepository.findByJoinCode(joinCode);
    }

    /**
     * Create a new league.
     * The creating user becomes the owner and first member.
     */
    @Transactional
    public League createLeague(League league, User owner, String teamName) {
        // Set owner
        league.setOwner(owner);
        league.setCurrentTeams(1);

        // Save league
        League savedLeague = leagueRepository.save(league);

        // Add owner as first member (commissioner)
        LeagueMember member = new LeagueMember(savedLeague, owner, teamName);
        member.setIsCommissioner(true);
        member.setDraftPosition(1);
        leagueMemberRepository.save(member);

        return savedLeague;
    }

    /**
     * Join an existing league using join code.
     */
    @Transactional
    public LeagueMember joinLeague(String joinCode, User user, String teamName) {
        // Find league
        League league = leagueRepository.findByJoinCode(joinCode)
                .orElseThrow(() -> new RuntimeException("League not found with code: " + joinCode));

        // Check if league has room
        if (!league.hasRoom()) {
            throw new RuntimeException("League is full");
        }

        // Check if user is already a member
        if (leagueMemberRepository.existsByLeagueIdAndUserId(league.getId(), user.getId())) {
            throw new RuntimeException("User is already a member of this league");
        }

        // Check if draft has started
        if (!"NOT_STARTED".equals(league.getDraftStatus())) {
            throw new RuntimeException("Cannot join - draft has already started");
        }

        // Create membership
        LeagueMember member = new LeagueMember(league, user, teamName);
        member.setDraftPosition(league.getCurrentTeams() + 1);

        // Update league team count
        league.setCurrentTeams(league.getCurrentTeams() + 1);
        leagueRepository.save(league);

        return leagueMemberRepository.save(member);
    }

    /**
     * Leave a league.
     * If commissioner leaves and there are other members, ownership transfers automatically.
     * If commissioner is the only member, the league is deleted.
     */
    @Transactional
    public void leaveLeague(Long leagueId, Long userId) {
        LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        League league = member.getLeague();

        // Cannot leave if draft has started
        if (!"NOT_STARTED".equals(league.getDraftStatus())) {
            throw new RuntimeException("Cannot leave - draft has already started");
        }

        // If commissioner is leaving, handle ownership transfer or deletion
        if (member.getIsCommissioner()) {
            List<LeagueMember> allMembers = leagueMemberRepository.findByLeagueId(leagueId);
            
            if (allMembers.size() <= 1) {
                // Commissioner is the only member - delete rosters, member, then league
                rosterRepository.deleteAll(rosterRepository.findByLeagueMemberId(member.getId()));
                leagueMemberRepository.delete(member);
                leagueRepository.delete(league);
                return;
            } else {
                // Transfer ownership to another member
                LeagueMember newCommissioner = allMembers.stream()
                        .filter(m -> !m.getId().equals(member.getId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No other members to transfer ownership to"));
                
                newCommissioner.setIsCommissioner(true);
                league.setOwner(newCommissioner.getUser());
                leagueMemberRepository.save(newCommissioner);
                leagueRepository.save(league);
            }
        }

        // Delete member's rosters first (to avoid foreign key constraint)
        rosterRepository.deleteAll(rosterRepository.findByLeagueMemberId(member.getId()));

        // Remove member
        leagueMemberRepository.delete(member);

        // Update team count
        league.setCurrentTeams(league.getCurrentTeams() - 1);
        leagueRepository.save(league);
    }

    /**
     * Get leagues owned by a user.
     */
    public List<League> getLeaguesByOwner(Long ownerId) {
        return leagueRepository.findByOwnerId(ownerId);
    }

    /**
     * Get leagues a user is a member of.
     */
    public List<LeagueMember> getUserMemberships(Long userId) {
        return leagueMemberRepository.findByUserId(userId);
    }

    /**
     * Get all members of a league.
     */
    public List<LeagueMember> getLeagueMembers(Long leagueId) {
        return leagueMemberRepository.findByLeagueId(leagueId);
    }

    /**
     * Get league standings.
     */
    public List<LeagueMember> getLeagueStandings(Long leagueId) {
        return leagueMemberRepository.findLeagueStandings(leagueId);
    }

    /**
     * Update league settings.
     */
    public League updateLeague(Long id, League leagueDetails) {
        League league = leagueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("League not found"));

        league.setName(leagueDetails.getName());
        league.setDescription(leagueDetails.getDescription());
        league.setMaxTeams(leagueDetails.getMaxTeams());
        league.setRosterSize(leagueDetails.getRosterSize());
        league.setStarterSize(leagueDetails.getStarterSize());
        league.setDraftType(leagueDetails.getDraftType());
        league.setScoringType(leagueDetails.getScoringType());
        league.setTourType(leagueDetails.getTourType());
        league.setIsPublic(leagueDetails.getIsPublic());

        return leagueRepository.save(league);
    }

    /**
     * Delete a league.
     */
    @Transactional
    public void deleteLeague(Long id) {
        leagueRepository.deleteById(id);
    }

    /**
     * Get public leagues that can be joined (active and have room).
     */
    public List<League> getPublicLeagues() {
        return leagueRepository.findJoinablePublicLeagues();
    }

    /**
     * Search leagues by name.
     */
    public List<League> searchLeagues(String name) {
        return leagueRepository.searchByName(name);
    }
}
