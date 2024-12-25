package se.fu.mu23.batterymanagement.smartcharging.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.BatteryInfoResponse;
import se.fu.mu23.batterymanagement.smartcharging.model.response.NumberArrayResponse;
import se.fu.mu23.batterymanagement.smartcharging.service.ChargingService;

@RestController
@RequestMapping("api")
@Slf4j
public class ChargingControllerRest {

    private final ChargingService chargingService;

    public ChargingControllerRest(ChargingService chargingService) {
        this.chargingService = chargingService;
    }


    @GetMapping("/battery/info")
    public ResponseEntity<BatteryInfoResponse> getBatteryInfo() {
        log.debug("Battery info request received");
        var info = chargingService.getInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/price/rate")
    public ResponseEntity<NumberArrayResponse> getPriceRate() {
        log.debug("Price rates request received");
        var priceRate = chargingService.getPriceRate();
        return ResponseEntity.ok(priceRate);
    }

    @GetMapping("/households/energy/consumption")
    public ResponseEntity<NumberArrayResponse> getHouseholdsEnergyConsumption() {
        log.debug("households energy consumption request received");
        var energyConsumption = chargingService.getHouseholdsEnergyConsumption();
        return ResponseEntity.ok(energyConsumption);
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargingRequest> setCharging(@RequestBody ChargingRequest request) {
        log.debug("Charge on/off request received");
        var charging = chargingService.setCharging(request);
        return ResponseEntity.ok(charging);
    }

    @PostMapping("/discharge")
    public ResponseEntity<DischargingRequest> setDischarging(@RequestBody DischargingRequest request) {
        log.debug("Discharge (reset) request received");
        var discharging = chargingService.setDischarging(request);
        return ResponseEntity.ok(discharging);
    }

    @GetMapping("/charge/recommendation/hours")
    public ResponseEntity getRecommendedChargingDayHours() {
        log.debug("Recommended charging hours request received");
        var recommendedChargingDayHours = chargingService.getRecommendedChargingHourByPrice();
        return ResponseEntity.ok(recommendedChargingDayHours);
    }
}
