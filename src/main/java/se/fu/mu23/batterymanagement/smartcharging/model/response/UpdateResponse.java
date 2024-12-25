package se.fu.mu23.batterymanagement.smartcharging.model.response;

import lombok.Builder;

@Builder
public record UpdateResponse(BatteryInfoResponse info, String chargingMode, boolean chargerStatus,
                             boolean chargingStatus) {
}
