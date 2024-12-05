package se.fu.mu23.batterymanagement.smartcharging.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BatteryInfoResponse(@JsonProperty("sim_time_hour") int simTimeHour,
                                  @JsonProperty("sim_time_min") int simTimeMin,
                                  @JsonProperty("base_current_load") double baseCurrentLoad,
                                  @JsonProperty("battery_capacity_kWh") double batteryCapacityKWh,
                                  @JsonProperty("ev_battery_charge_start_stop") boolean evBatteryChargeStartStop) {
}
