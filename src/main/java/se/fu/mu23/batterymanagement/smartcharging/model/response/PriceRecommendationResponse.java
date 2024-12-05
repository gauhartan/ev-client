package se.fu.mu23.batterymanagement.smartcharging.model.response;

import lombok.Builder;

@Builder
public record PriceRecommendationResponse(int hour, double price) {
}
