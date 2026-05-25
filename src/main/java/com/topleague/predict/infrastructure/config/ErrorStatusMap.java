package com.topleague.predict.infrastructure.config;

import com.topleague.predict.domain.exception.GroupErrorCode;
import com.topleague.predict.domain.exception.GroupMemberErrorCode;
import com.topleague.predict.domain.exception.PredictionErrorCode;
import com.topleague.predict.domain.exception.TopPredictErrorCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ErrorStatusMap {

    @Bean
    public Map<TopPredictErrorCode, HttpStatus> httpStatusMap() {
        Map<TopPredictErrorCode, HttpStatus> map = new HashMap<>();

        // Group exceptions
        map.put(GroupErrorCode.INVALID_GROUP_DATA, HttpStatus.BAD_REQUEST);
        map.put(GroupErrorCode.GROUP_EXISTS, HttpStatus.CONFLICT);
        map.put(GroupErrorCode.ERROR_SAVING_GROUP, HttpStatus.INTERNAL_SERVER_ERROR);
        map.put(GroupErrorCode.GROUP_NOT_FOUND, HttpStatus.NOT_FOUND);
        map.put(GroupErrorCode.COULD_NOT_GENERATE_INVITE_CODE, HttpStatus.INTERNAL_SERVER_ERROR);

        // Group member exceptions
        map.put(GroupMemberErrorCode.INVALID_MEMBER_DATA, HttpStatus.BAD_REQUEST);
        map.put(GroupMemberErrorCode.MEMBER_EXISTS, HttpStatus.CONFLICT);
        map.put(GroupMemberErrorCode.ERROR_SAVING_MEMBER, HttpStatus.INTERNAL_SERVER_ERROR);
        map.put(GroupMemberErrorCode.MEMBER_NOT_FOUND, HttpStatus.FORBIDDEN); // 403 Forbidden for leaderboard access

        // Prediction exceptions
        map.put(PredictionErrorCode.INVALID_PREDICTION_DATA, HttpStatus.BAD_REQUEST);
        map.put(PredictionErrorCode.PREDICTION_EXISTS, HttpStatus.CONFLICT);
        map.put(PredictionErrorCode.PREDICTION_NOT_FOUND, HttpStatus.NOT_FOUND);
        map.put(PredictionErrorCode.MISMATCHED_GAME_IDS, HttpStatus.BAD_REQUEST);
        map.put(PredictionErrorCode.PREDICTION_LOCKED, HttpStatus.FORBIDDEN);

        return map;
    }
}
