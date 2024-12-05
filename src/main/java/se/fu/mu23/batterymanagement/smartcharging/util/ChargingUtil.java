package se.fu.mu23.batterymanagement.smartcharging.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.BatteryInfoResponse;
import se.fu.mu23.batterymanagement.smartcharging.model.response.NumberArrayResponse;
import se.fu.mu23.batterymanagement.smartcharging.model.response.PriceRecommendationResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ChargingUtil {

    final static String uriBase = "http://127.0.0.1:5000";

    public static BatteryInfoResponse getBatteryInfo() {
        RestClient restClient = RestClient.create();

        BatteryInfoResponse result = restClient.get()
                .uri(uriBase + "/info")
                .retrieve()
                .body(BatteryInfoResponse.class);

        return result;
    }

    public static NumberArrayResponse getPriceRate() {
        RestClient restClient = RestClient.create();

        NumberArrayResponse result = restClient.get()
                .uri(uriBase + "/priceperhour")
                .retrieve()
                .body(NumberArrayResponse.class);

        return result;
    }

    public static NumberArrayResponse getHouseholdsEnergyConsumption() {
        RestClient restClient = RestClient.create();

        NumberArrayResponse result = restClient.get()
                .uri(uriBase + "/baseload")
                .retrieve()
                .body(NumberArrayResponse.class);

        return result;
    }

    public static ChargingRequest setCharging(ChargingRequest request) {
        RestClient restClient = RestClient.create();

        ChargingRequest result = restClient.post()
                .uri(uriBase + "/charge")
                .body(request)
                .retrieve()
                .body(ChargingRequest.class);

        return result;
    }

    public static DischargingRequest setDischarging(DischargingRequest request) {
        RestClient restClient = RestClient.create();

        DischargingRequest result = restClient.post()
                .uri(uriBase + "/discharge")
                .body(request)
                .retrieve()
                .body(DischargingRequest.class);

        return result;
    }

    public static List<PriceRecommendationResponse> getRecommendedChargingDayHours() {

        var rates = getPriceRate().values();

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

        log.info("Cheapest price: " + cheapestPrice);
        log.info("mostExpensivePrice price: " + mostExpensivePrice);
        log.info("Cheapest price is at: " + cheapestHour);
        log.info("mostExpensiveHour price is at: " + mostExpensiveHour);

        List<PriceRecommendationResponse> result = new ArrayList<>();
        double chargingPriceThreshold = calculateChargingPriceThreshold(cheapestPrice, mostExpensivePrice);
        log.info("chargingPriceThreshold: " + chargingPriceThreshold);
        for (int j = 0; j < rates.size(); j++) {
            double priceAtHour = rates.get(j);
            if (priceAtHour <= chargingPriceThreshold) {
                System.out.print(priceAtHour + ", ");
                result.add(PriceRecommendationResponse.builder().hour(j).price(priceAtHour).build());
            }

        }
        return result;
    }

    @SneakyThrows
    public static void smartCharging() {
        int minSoc = 20, maxSoc = 80, maxTotalEnergyConsumptionKWh = 11;
        double maxBatteryChargeKwh = 46.3 * maxSoc / 100;
        boolean isChargingDone = false;

        Map<Integer, Double> chargingHours = new HashMap<>();
        var recommendedPriceHours = getRecommendedChargingDayHours();
        for (PriceRecommendationResponse recommendedPriceHour : recommendedPriceHours) {
            chargingHours.put(recommendedPriceHour.hour(), recommendedPriceHour.price());
        }

        while (!isChargingDone) {

            var info = getBatteryInfo();

            boolean isChargingNow = info.evBatteryChargeStartStop();
            int hour = info.simTimeHour();

            if (info.batteryCapacityKWh() >= maxBatteryChargeKwh) {
                if (isChargingNow) stopCharging(hour);
                log.info("Charging done. {}", info);
                isChargingDone = true;
            }
            if (info.baseCurrentLoad() >= maxTotalEnergyConsumptionKWh && isChargingNow) {
                stopCharging(hour);
                log.info("Paused due to exceeded max allowed energy consumption");
                Thread.sleep(1000);
                continue;
            }

            if (chargingHours.get(hour) != null) {
                if (!isChargingNow) startCharging(hour);
            }

            log.info("Current charging status= {}, hour={}, baseCurrentLoad={}, batteryCapacityKWh={}", isChargingNow ? "YES" : "NO", hour, info.baseCurrentLoad(), info.batteryCapacityKWh());
            Thread.sleep(1000);
        }

    }

    private static double calculateChargingPriceThreshold(double cheapestPrice, double mostExpensivePrice) {
        return cheapestPrice + (mostExpensivePrice - cheapestPrice) / 2;
    }

    private static void startCharging(int hour) {
        log.info("Starting charging at hour={} !!!!!!!!!!!!", hour);
        setCharging(ChargingRequest.builder().charging("on").build());
    }

    private static void stopCharging(int hour) {
        log.info("Stopping charging at hour={}", hour);
        setCharging(ChargingRequest.builder().charging("off").build());
    }

}
