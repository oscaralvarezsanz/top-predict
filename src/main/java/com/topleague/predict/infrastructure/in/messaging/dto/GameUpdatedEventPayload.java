package com.topleague.predict.infrastructure.in.messaging.dto;

public record GameUpdatedEventPayload(
        Integer gameId,
        Integer homeScore,
        Integer awayScore) {
}