package se.fu.mu23.batterymanagement.smartcharging.model.response;

import lombok.Builder;

@Builder
public record LoadRecommendationResponse(int hour, double load) {
}
