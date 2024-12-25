package se.fu.mu23.batterymanagement.smartcharging.service;

import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ModeRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.*;

import java.util.List;

public interface ChargingService {
    BatteryInfoResponse getInfo();

    UpdateResponse getUpdate();

    NumberArrayResponse getPriceRate();

    NumberArrayResponse getHouseholdsEnergyConsumption();

    List<PriceRecommendationResponse> getRecommendedChargingHourByPrice();

    List<LoadRecommendationResponse> getRecommendedChargingHourByLoad();

    ModeRequest setMode(ModeRequest modeRequest);

    String getMode();

    boolean plugin();

    boolean unplug();

    boolean getChargerStatus();

    ChargingRequest setCharging(ChargingRequest request);

    boolean startCharging();

    boolean stopCharging();

    boolean getChargingStatus();

    DischargingRequest setDischarging(DischargingRequest request);
}
