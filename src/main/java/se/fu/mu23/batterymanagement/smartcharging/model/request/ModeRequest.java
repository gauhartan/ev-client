package se.fu.mu23.batterymanagement.smartcharging.model.request;

import lombok.Builder;

@Builder
public record ModeRequest(String mode) {
}