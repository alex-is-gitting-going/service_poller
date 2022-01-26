package com.kry.utility.servicepoller.poller;

import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.requests.MonitorNewServiceRequest;
import com.kry.utility.servicepoller.poller.requests.StopMonitoringServiceRequest;
import com.kry.utility.servicepoller.poller.requests.UpdateMonitoredServiceRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/monitored-services")
@AllArgsConstructor
public class ServiceMonitoringController {

    ServiceMonitoringService serviceMonitoringService;

    @PostMapping
    public RegisteredService monitorNewService(@RequestBody MonitorNewServiceRequest request) {
        return serviceMonitoringService.registerNewService(request.getName(), request.getURL(), request.getUsername());
    }

    @PatchMapping
    public RegisteredService updateMonitoredService(@RequestBody UpdateMonitoredServiceRequest request) {
        return serviceMonitoringService.updateMonitoredService(request.getId(), request.getName(), request.getUrl());
    }

    @DeleteMapping
    public void stopMonitoringService(@RequestBody StopMonitoringServiceRequest request) {
        serviceMonitoringService.stopMonitoringService(request.getId());
    }

    @GetMapping(path = "/all")
    public List<RegisteredService> getAllMonitoredServices() {
        return serviceMonitoringService.getAllMonitoredServices();
    }

    @GetMapping
    public List<RegisteredService> getMonitoredServicesForUser(@RequestParam String username) {
        return serviceMonitoringService.getMonitoredServicesForUser(username);
    }

}
