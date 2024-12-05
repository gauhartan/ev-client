package se.fu.mu23.batterymanagement.smartcharging.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.BatteryInfoResponse;
import se.fu.mu23.batterymanagement.smartcharging.model.response.NumberArrayResponse;
import se.fu.mu23.batterymanagement.smartcharging.util.ChargingUtil;

@RestController
@RequestMapping("api")
@Slf4j
public class ChargingControllerRest {


    @GetMapping("/battery/info")
    public ResponseEntity<BatteryInfoResponse> getBatteryInfo() {
        log.info("Battery info request received");
        var info = ChargingUtil.getBatteryInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/price/rate")
    public ResponseEntity<NumberArrayResponse> getPriceRate() {
        log.info("Price rates request received");
        var priceRate = ChargingUtil.getPriceRate();
        return ResponseEntity.ok(priceRate);
    }

    @GetMapping("/households/energy/consumption")
    public ResponseEntity<NumberArrayResponse> getHouseholdsEnergyConsumption() {
        log.info("households energy consumption request received");
        var energyConsumption = ChargingUtil.getHouseholdsEnergyConsumption();
        return ResponseEntity.ok(energyConsumption);
    }

    @PostMapping("/charge")
    public ResponseEntity<ChargingRequest> setCharging(@RequestBody ChargingRequest request) {
        log.info("Charge on/off request received");
        var charging = ChargingUtil.setCharging(request);
        return ResponseEntity.ok(charging);
    }

    @PostMapping("/discharge")
    public ResponseEntity<DischargingRequest> setDischarging(@RequestBody DischargingRequest request) {
        log.info("Discharge (reset) request received");
        var discharging = ChargingUtil.setDischarging(request);
        return ResponseEntity.ok(discharging);
    }

    @GetMapping("/charge/recommendation/hours")
    public ResponseEntity getRecommendedChargingDayHours() {
        log.info("Recommended charging hours request received");
        var recommendedChargingDayHours = ChargingUtil.getRecommendedChargingDayHours();
        return ResponseEntity.ok(recommendedChargingDayHours);
    }

    @PostMapping("/smartcharging")
    public void smartCharging() {
        log.info("Smart charging request received");
        ChargingUtil.smartCharging();
    }
}
