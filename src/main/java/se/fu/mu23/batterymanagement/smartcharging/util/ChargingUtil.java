package se.fu.mu23.batterymanagement.smartcharging.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.BatteryInfoResponse;
import se.fu.mu23.batterymanagement.smartcharging.model.response.NumberArrayResponse;

@Slf4j
@Data
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

    public static ChargingRequest setChargingStatus(ChargingRequest request) {
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

    public static double calculateChargingPriceThreshold(double cheapestPrice, double mostExpensivePrice) {
        return cheapestPrice + (mostExpensivePrice - cheapestPrice) / 2;
    }

    public static double calculateChargingLoadThreshold(double leastLoad, double mostLoad) {
        return (leastLoad + (mostLoad - leastLoad) / 2) / 2;
    }

}
