package se.fu.mu23.batterymanagement.smartcharging.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import se.fu.mu23.batterymanagement.smartcharging.model.request.ChargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.model.request.DischargingRequest;
import se.fu.mu23.batterymanagement.smartcharging.util.ChargingUtil;

@Controller
@Slf4j
public class ChargingController {


    @GetMapping()
    public String viewHomePage(Model model) {
        model.addAttribute("info", ChargingUtil.getBatteryInfo());
        model.addAttribute("rates", ChargingUtil.getPriceRate());
        model.addAttribute("energyconsumption", ChargingUtil.getHouseholdsEnergyConsumption());
        model.addAttribute("recommendations", ChargingUtil.getRecommendedChargingDayHours());
        setCharging("on");
        return "index";
    }

    @GetMapping("/battery/info")
    public String getBatteryInfo(Model model) {
        log.info("Battery info request received");
        model.addAttribute("info", ChargingUtil.getBatteryInfo());
        return "index::battery-info";
    }

    @GetMapping("/charge/{charging}")
    public String setCharging(@PathVariable String charging) {
        log.info("Charge {} request received", charging);
        if ("on".equals(charging)) {
            smartCharging();
        } else ChargingUtil.setCharging(ChargingRequest.builder().charging(charging).build());
        return "redirect:/";
    }

    @GetMapping("/discharge/{discharging}")
    public String setDischarging(@PathVariable String discharging) {
        log.info("Discharge (reset) request received");
        ChargingUtil.setDischarging(DischargingRequest.builder().discharging(discharging).build());
        return "redirect:/";
    }

    @PostMapping("/smartcharging")
    public void smartCharging() {
        log.info("Smart charging request received");
        ChargingUtil.smartCharging();
    }
}
