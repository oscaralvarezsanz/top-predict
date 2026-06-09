package com.topleague.predict.application.service.prediction;

import com.topleague.predict.application.port.in.prediction.GetGroupMatchdayPredictionsUseCase;
import com.topleague.predict.application.port.out.game.GameGetByLeagueAndMatchdayRepository;
import com.topleague.predict.application.port.out.group.GroupGetByIdRepository;
import com.topleague.predict.application.port.out.groupmember.GroupMemberGetByGroupIdRepository;
import com.topleague.predict.application.port.out.prediction.PredictionGetByGroupAndGamesRepository;
import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupException;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.GroupMemberException;
import com.topleague.predict.domain.model.Game;
import com.topleague.predict.domain.model.Group;
import com.topleague.predict.domain.model.GroupMember;
import com.topleague.predict.domain.model.GroupMemberPrediction;
import com.topleague.predict.domain.model.Prediction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupMatchdayPredictionsGetService implements GetGroupMatchdayPredictionsUseCase {

    private final GroupGetByIdRepository groupGetByIdRepository;
    private final GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository;
    private final GameGetByLeagueAndMatchdayRepository gameGetByLeagueAndMatchdayRepository;
    private final PredictionGetByGroupAndGamesRepository predictionGetByGroupAndGamesRepository;

    public GroupMatchdayPredictionsGetService(
            GroupGetByIdRepository groupGetByIdRepository,
            GroupMemberGetByGroupIdRepository groupMemberGetByGroupIdRepository,
            GameGetByLeagueAndMatchdayRepository gameGetByLeagueAndMatchdayRepository,
            PredictionGetByGroupAndGamesRepository predictionGetByGroupAndGamesRepository) {
        this.groupGetByIdRepository = groupGetByIdRepository;
        this.groupMemberGetByGroupIdRepository = groupMemberGetByGroupIdRepository;
        this.gameGetByLeagueAndMatchdayRepository = gameGetByLeagueAndMatchdayRepository;
        this.predictionGetByGroupAndGamesRepository = predictionGetByGroupAndGamesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberPrediction> getGroupMatchdayPredictions(Integer groupId, Integer matchday, Integer userId) {
        List<GroupMember> members = groupMemberGetByGroupIdRepository.getGroupMembersByGroupId(groupId);
        
        if (members.stream().noneMatch(member -> member.getUserId().equals(userId))) {
            throw new GroupMemberException(GroupMemberErrorCode.MEMBER_NOT_FOUND);
        }

        Group group = groupGetByIdRepository.getGroupById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        List<Game> games = gameGetByLeagueAndMatchdayRepository
                .getGamesByLeagueAndMatchday(group.getLeagueId(), matchday);
        
        if (games.isEmpty()) {
            return List.of();
        }

        List<Integer> gameIds = games.stream().map(Game::getId).toList();
        List<Prediction> predictions = predictionGetByGroupAndGamesRepository
                .getPredictionsByGroupAndGames(groupId, gameIds);

        List<GroupMemberPrediction> result = new ArrayList<>();
        for (Game game : games) {
            boolean isResolved = game.getHomeScore() != null && game.getAwayScore() != null;
            for (GroupMember member : members) {
                Optional<Prediction> predictionOpt = predictions.stream()
                        .filter(p -> p.getGameId().equals(game.getId()) && p.getUserId().equals(member.getUserId()))
                        .findFirst();

                boolean hideScores = !isResolved && !member.getUserId().equals(userId);

                result.add(buildMemberPrediction(game, member, predictionOpt, hideScores));
            }
        }

        return result;
    }

    private GroupMemberPrediction buildMemberPrediction(Game game, GroupMember member, Optional<Prediction> predictionOpt,
            boolean hideScores) {
        return GroupMemberPrediction.builder()
                .gameId(game.getId())
                .userId(member.getUserId())
                .alias(member.getAlias())
                .predictedHomeScore(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPredictedHomeScore() : null)
                .predictedAwayScore(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPredictedAwayScore() : null)
                .pointsEarned(!hideScores && predictionOpt.isPresent() ? predictionOpt.get().getPointsEarned() : null)
                .build();
    }
}
