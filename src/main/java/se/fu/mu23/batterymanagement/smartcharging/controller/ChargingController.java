package se.fu.mu23.batterymanagement.smartcharging.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ModeRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.response.UpdateResponse;
import se.fu.mu23.batterymanagement.smartcharging.service.ChargingService;

@Controller
@Slf4j
public class ChargingController {

    private final ChargingService chargingService;

    public ChargingController(ChargingService chargingService) {
        this.chargingService = chargingService;
    }


    @GetMapping()
    public String viewHomePage(Model model) {
        model.addAttribute("rates", chargingService.getPriceRate());
        model.addAttribute("energyConsumption", chargingService.getHouseholdsEnergyConsumption());
        model.addAttribute("recommendationsByPrice", chargingService.getRecommendedChargingHourByPrice());
        model.addAttribute("recommendationsByLoad", chargingService.getRecommendedChargingHourByLoad());
        model.addAttribute("chargerStatus", chargingService.getChargerStatus());
        model.addAttribute("chargingMode", chargingService.getMode());
        model.addAttribute("info", chargingService.getInfo());
        return "index";
    }

    @GetMapping("/battery/info")
    public String getBatteryInfo(Model model) {
        log.debug("Battery info request received");
        model.addAttribute("info", chargingService.getInfo());
        return "index::battery-info";
    }

    @GetMapping("/battery/update")
    public ResponseEntity<UpdateResponse> getInfoUpdate() {
        return ResponseEntity.ok(chargingService.getUpdate());
    }

    @GetMapping("/mode/{value}")
    public String setMode(@PathVariable String value) {
        log.debug("Mode: {} request received", value);
        chargingService.setMode(ModeRequest.builder().mode(value).build());
        return "redirect:/";
    }

    @GetMapping("/charge/{chargingStatus}/{mode}")
    public String setCharging(@PathVariable boolean chargingStatus, @PathVariable String mode) {
        log.info("chargingStatus: {}, mode: {} request received", chargingStatus ? "on" : "off", mode);
        chargingService.setMode(ModeRequest.builder().mode(mode).build());
        if (chargingStatus) {
            chargingService.startCharging();
        } else chargingService.stopCharging();
        return "redirect:/";
    }

    @GetMapping("/plugin")
    public String plugin() {
        log.debug("Plugin request received");
        chargingService.plugin();
        return "redirect:/";
    }

    @GetMapping("/unplug")
    public String unplug() {
        log.debug("Unplug request received");
        chargingService.unplug();
        return "redirect:/";
    }

    @GetMapping("/discharge/{discharging}")
    public String setDischarging(@PathVariable String discharging) {
        log.debug("Discharge (reset) request received");
        chargingService.setDischarging(DischargingRequest.builder().discharging(discharging).build());
        return "redirect:/";
    }
}
