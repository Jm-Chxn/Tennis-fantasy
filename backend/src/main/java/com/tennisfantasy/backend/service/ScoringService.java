package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.*;
import com.tennisfantasy.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for calculating fantasy points.
 * 
 * Point values:
 * - Match Win: 10 points (15 for Grand Slam)
 * - Set Win: 2 points
 * - Upset Bonus: 5 points (beating higher-ranked player)
 * - Final Reach: 10 points
 * - Tournament Win: 25 points (50 for Grand Slam)
 */
@Service
public class ScoringService {

    private static final Logger logger = LoggerFactory.getLogger(ScoringService.class);

    // Point values (easy to understand and modify)
    public static final int POINTS_MATCH_WIN = 10;
    public static final int POINTS_MATCH_WIN_GRAND_SLAM = 15;
    public static final int POINTS_SET_WIN = 2;
    public static final int POINTS_UPSET_BONUS = 5;
    public static final int POINTS_FINAL_REACH = 10;
    public static final int POINTS_TOURNAMENT_WIN = 25;
    public static final int POINTS_TOURNAMENT_WIN_GRAND_SLAM = 50;

    private final MatchRepository matchRepository;
    private final RosterRepository rosterRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final FantasyPointsRepository fantasyPointsRepository;
    private final PlayerRepository playerRepository;

    public ScoringService(MatchRepository matchRepository,
            RosterRepository rosterRepository,
            LeagueMemberRepository leagueMemberRepository,
            FantasyPointsRepository fantasyPointsRepository,
            PlayerRepository playerRepository) {
        this.matchRepository = matchRepository;
        this.rosterRepository = rosterRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.fantasyPointsRepository = fantasyPointsRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Process a completed match and award points.
     * 
     * This should be called when match data is synced from SportsRadar.
     */
    @Transactional
    public void processMatch(Match match) {
        if (!match.isCompleted() || match.getWinner() == null) {
            logger.warn("Cannot process incomplete match: {}", match.getId());
            return;
        }

        Player winner = match.getWinner();
        Player loser = match.getPlayer1().getId().equals(winner.getId())
                ? match.getPlayer2()
                : match.getPlayer1();

        logger.info("Processing match: {} vs {} - Winner: {}",
                match.getPlayer1().getFullName(),
                match.getPlayer2().getFullName(),
                winner.getFullName());

        // Find all roster entries for the winner across all leagues
        List<Roster> winnerRosters = findRostersForPlayer(winner.getId());

        for (Roster roster : winnerRosters) {
            // Only score starters
            if (!roster.getIsStarter()) {
                continue;
            }

            LeagueMember member = roster.getLeagueMember();
            int totalPoints = 0;

            // 1. Match Win Points
            int matchWinPoints = match.getIsGrandSlam() ? POINTS_MATCH_WIN_GRAND_SLAM : POINTS_MATCH_WIN;
            totalPoints += matchWinPoints;
            awardPoints(member, winner, match, matchWinPoints, "MATCH_WIN",
                    winner.getFullName() + " won match");

            // 2. Set Win Points
            Integer setsWon = match.getPlayer1().getId().equals(winner.getId())
                    ? match.getPlayer1Sets()
                    : match.getPlayer2Sets();
            if (setsWon != null && setsWon > 0) {
                int setPoints = setsWon * POINTS_SET_WIN;
                totalPoints += setPoints;
                awardPoints(member, winner, match, setPoints, "SET_WIN",
                        winner.getFullName() + " won " + setsWon + " sets");
            }

            // 3. Upset Bonus (winner has lower ranking = higher number)
            if (winner.getRanking() != null && loser.getRanking() != null
                    && winner.getRanking() > loser.getRanking()) {
                totalPoints += POINTS_UPSET_BONUS;
                awardPoints(member, winner, match, POINTS_UPSET_BONUS, "UPSET_BONUS",
                        winner.getFullName() + " upset #" + loser.getRanking() + " " + loser.getFullName());
            }

            // 4. Final Reach / Tournament Win
            if ("Final".equalsIgnoreCase(match.getRoundName())) {
                int tourneyWinPoints = match.getIsGrandSlam()
                        ? POINTS_TOURNAMENT_WIN_GRAND_SLAM
                        : POINTS_TOURNAMENT_WIN;
                totalPoints += tourneyWinPoints;
                awardPoints(member, winner, match, tourneyWinPoints, "TOURNAMENT_WIN",
                        winner.getFullName() + " won " + match.getTournamentName());
            } else if ("Semi-Final".equalsIgnoreCase(match.getRoundName())
                    || "Semifinal".equalsIgnoreCase(match.getRoundName())) {
                // Reaching the final
                totalPoints += POINTS_FINAL_REACH;
                awardPoints(member, winner, match, POINTS_FINAL_REACH, "FINAL_REACH",
                        winner.getFullName() + " reached final at " + match.getTournamentName());
            }

            // Update roster and member totals
            roster.addPoints(totalPoints);
            rosterRepository.save(roster);

            member.addPoints(totalPoints);
            leagueMemberRepository.save(member);

            logger.info("Awarded {} points to {} for {} winning",
                    totalPoints, member.getTeamName(), winner.getFullName());
        }
    }

    /**
     * Award points and log to fantasy_points table.
     */
    private void awardPoints(LeagueMember member, Player player, Match match,
            int points, String reason, String description) {
        FantasyPoints fp = new FantasyPoints(member, player, match, points, reason);
        fp.setDescription(description);
        fp.setTournamentName(match.getTournamentName());
        fantasyPointsRepository.save(fp);
    }

    /**
     * Find all roster entries for a player across all leagues.
     */
    private List<Roster> findRostersForPlayer(Long playerId) {
        // This is a simplified query - in production you'd want a proper repository
        // method
        return rosterRepository.findAll().stream()
                .filter(r -> r.getPlayer().getId().equals(playerId))
                .toList();
    }

    /**
     * Process all completed matches from today.
     * Called by the scheduled sync service.
     */
    @Transactional
    public void processRecentMatches() {
        List<Match> completedMatches = matchRepository.findCompletedMatches();

        for (Match match : completedMatches) {
            // Check if we've already scored this match
            List<FantasyPoints> existingPoints = fantasyPointsRepository.findByMatchId(match.getId());
            if (existingPoints.isEmpty()) {
                processMatch(match);
            }
        }
    }

    /**
     * Get points breakdown for a team.
     */
    public List<FantasyPoints> getPointsBreakdown(Long leagueMemberId) {
        return fantasyPointsRepository.findByLeagueMemberIdOrderByAwardedAtDesc(leagueMemberId);
    }

    /**
     * Get total points for a team.
     */
    public int getTotalPoints(Long leagueMemberId) {
        Integer total = fantasyPointsRepository.sumPointsByLeagueMemberId(leagueMemberId);
        return total != null ? total : 0;
    }
}
