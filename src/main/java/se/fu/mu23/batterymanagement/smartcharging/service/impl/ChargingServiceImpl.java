package se.fu.mu23.batterymanagement.smartcharging.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ModeRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.*;
import se.fu.mu23.batterymanagement.smartcharging.service.ChargingService;
import se.fu.mu23.batterymanagement.smartcharging.util.ChargingUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ChargingServiceImpl implements ChargingService {

    private final int maxSoc = 77, maxTotalEnergyConsumptionKWh = 11;
    private final double maxBatteryChargeKwh = 46.3 * maxSoc / 100;

    private boolean chargingStatus = true;
    private boolean chargerStatus = true;
    private String mode = "price";
    private BatteryInfoResponse info;
    private NumberArrayResponse priceRates;
    private NumberArrayResponse loadRates;
    private List<PriceRecommendationResponse> recommendedChargingHourByPrice;
    private List<LoadRecommendationResponse> recommendedChargingHourByLoad;

    @Override
    public BatteryInfoResponse getInfo() {
        return ChargingUtil.getBatteryInfo();
    }

    @Override
    public UpdateResponse getUpdate() {
        info = ChargingUtil.getBatteryInfo();
        var result = UpdateResponse.builder();
        result.info(info);
        result.chargingMode(mode);
        result.chargerStatus(chargerStatus);
        result.chargingStatus(chargingStatus);

        if (chargerStatus) isSmartCharging();
        return result.build();
    }

    @Override
    public NumberArrayResponse getPriceRate() {
        priceRates = ChargingUtil.getPriceRate();
        return priceRates;
    }

    @Override
    public NumberArrayResponse getHouseholdsEnergyConsumption() {
        loadRates = ChargingUtil.getHouseholdsEnergyConsumption();
        return loadRates;
    }

    @Override
    public List<PriceRecommendationResponse> getRecommendedChargingHourByPrice() {
        var rates = priceRates.values();

        // order price
        double cheapestPrice = rates.get(0);
        double mostExpensivePrice = rates.get(0);
        int cheapestHour = 0, mostExpensiveHour = 0;

        for (int i = 1; i < rates.size(); i++) {
            double priceAtHour = rates.get(i);
            if (cheapestPrice > priceAtHour) {
                cheapestPrice = priceAtHour;
                cheapestHour = i;
            } else if (mostExpensivePrice < priceAtHour) {
                mostExpensivePrice = priceAtHour;
                mostExpensiveHour = i;
            }
        }

        log.debug("Cheapest price: " + cheapestPrice);
        log.debug("mostExpensivePrice price: " + mostExpensivePrice);
        log.debug("Cheapest price is at: " + cheapestHour);
        log.debug("mostExpensiveHour price is at: " + mostExpensiveHour);

        List<PriceRecommendationResponse> result = new ArrayList<>();
        double chargingPriceThreshold = ChargingUtil.calculateChargingPriceThreshold(cheapestPrice, mostExpensivePrice);
        log.debug("chargingPriceThreshold: " + chargingPriceThreshold);
        for (int j = 0; j < rates.size(); j++) {
            double priceAtHour = rates.get(j);
            if (priceAtHour <= chargingPriceThreshold) {
//                System.out.print(priceAtHour + ", ");
                result.add(PriceRecommendationResponse.builder().hour(j).price(priceAtHour).build());
            }

        }
        recommendedChargingHourByPrice = result;
        return result;
    }

    @Override
    public List<LoadRecommendationResponse> getRecommendedChargingHourByLoad() {
        var rates = loadRates.values();

        // order load
        double leastLoad = rates.get(0);
        double mostLoad = rates.get(0);
        int leastLoadHour = 0, mostLoadHour = 0;

        for (int i = 1; i < rates.size(); i++) {
            double loadAtHour = rates.get(i);
            if (leastLoad > loadAtHour) {
                leastLoad = loadAtHour;
                leastLoadHour = i;
            } else if (mostLoad < loadAtHour) {
                mostLoad = loadAtHour;
                mostLoadHour = i;
            }
        }

        log.debug("least load: " + leastLoad);
        log.debug("most load: " + mostLoad);
        log.debug("least load hour is at: " + leastLoadHour);
        log.debug("most load hour is at: " + mostLoadHour);

        List<LoadRecommendationResponse> result = new ArrayList<>();
        double chargingLoadThreshold = ChargingUtil.calculateChargingLoadThreshold(leastLoad, mostLoad);
        log.debug("chargingLoadThreshold: " + chargingLoadThreshold);
        for (int j = 0; j < rates.size(); j++) {
            double loadAtHour = rates.get(j);
            if (loadAtHour <= chargingLoadThreshold) {
//                System.out.print(loadAtHour + ", ");
                result.add(LoadRecommendationResponse.builder().hour(j).load(loadAtHour).build());
            }

        }
        recommendedChargingHourByLoad = result;
        return result;
    }

    @Override
    public ModeRequest setMode(ModeRequest modeRequest) {
        mode = modeRequest.mode();
        return modeRequest;
    }

    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public boolean plugin() {
        chargerStatus = true;
        startCharging();
        return chargerStatus;
    }

    @Override
    public boolean unplug() {
        chargerStatus = false;
        stopCharging();
        return chargerStatus;
    }

    @Override
    public boolean getChargerStatus() {
        return chargerStatus;
    }

    @Override
    public ChargingRequest setCharging(ChargingRequest request) {
        boolean result = "on".equals(request.charging()) ? startCharging() : stopCharging();
        return ChargingRequest.builder().charging(result ? "on" : "off").build();
    }

    @Override
    public boolean startCharging() {
        chargingStatus = true;
        return chargingStatus;
    }

    @Override
    public boolean stopCharging() {
        log.debug("Stopping charging at hour={}", info.simTimeHour());
        chargingStatus = false;
        ChargingUtil.setChargingStatus(ChargingRequest.builder().charging("off").build());
        return chargingStatus;
    }

    @Override
    public boolean getChargingStatus() {
        return chargingStatus;
    }

    @Override
    public DischargingRequest setDischarging(DischargingRequest request) {
        if ("on".equals(request.discharging())) {
            chargerStatus = true;
            chargingStatus = true;
        }
        return ChargingUtil.setDischarging(request);
    }

    @SneakyThrows
    private boolean isSmartCharging() {

        if (!chargingStatus) return false;

        Map<Integer, Double> chargingHours = new HashMap<>();

        log.info("Max: {}, h:{}, battery={}, status={}", maxBatteryChargeKwh, info.simTimeHour(), info.batteryCapacityKWh(), info.evBatteryChargeStartStop());

        if ("price".equals(getMode())) {
            for (PriceRecommendationResponse recommendedPriceHour : recommendedChargingHourByPrice) {
                chargingHours.put(recommendedPriceHour.hour(), recommendedPriceHour.price());
            }
        } else if ("load".equals(getMode())) {
            for (LoadRecommendationResponse recommendedLoadHour : recommendedChargingHourByLoad) {
                chargingHours.put(recommendedLoadHour.hour(), recommendedLoadHour.load());
            }
        }

        if (info.batteryCapacityKWh() > maxBatteryChargeKwh) {
            stopCharging();
            log.info("Charging done. hour: {}, battery={}, status={}", info.simTimeHour(), info.batteryCapacityKWh(), info.evBatteryChargeStartStop());
            return chargingStatus;
        }
        if (info.baseCurrentLoad() >= maxTotalEnergyConsumptionKWh) {
            stopCharging();
            log.info("Paused due to exceeded max allowed energy consumption");
            Thread.sleep(1000);
            return chargingStatus;
        }

        if (chargingHours.get(info.simTimeHour()) != null) {
            chargingStatus = true;
            ChargingUtil.setChargingStatus(ChargingRequest.builder().charging("on").build());
            log.debug("Starting charging at hour={} !!!!!!!!!!!!", info.simTimeHour());
        }

        log.debug("Current charging status= YES, hour={}, baseCurrentLoad={}, batteryCapacityKWh={}", info.simTimeHour(), info.baseCurrentLoad(), info.batteryCapacityKWh());
        Thread.sleep(1000);
        return chargingStatus;
    }
}
