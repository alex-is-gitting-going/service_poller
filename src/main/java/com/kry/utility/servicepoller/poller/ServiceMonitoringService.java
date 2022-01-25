package com.kry.utility.servicepoller.poller;

import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredServiceRepository;
import com.kry.utility.servicepoller.poller.registeredservice.ServiceStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@AllArgsConstructor
public class ServiceMonitoringService {

    private static final Logger log = LoggerFactory.getLogger(ServiceMonitoringService.class);

    RegisteredServiceRepository registeredServiceRepository;


    public void registerNewService(String serviceName, String url, String username) {
        log.info(String.format("User %s is attempting to register new service with name: %s, and url '%s '", username, serviceName, url));
        String normalisedUsername = normaliseUsername(username);
        validateURL(url);
        RegisteredService service = new RegisteredService(serviceName, url, normalisedUsername);
        registeredServiceRepository.save(service);
        log.info(String.format("Service registered: %s", service));
    }

    public void updateMonitoredService(Long id, String newName, String newUrl) {
        log.info(String.format("Attempting to update service with ID %s with new name '%s' and new URL '%s'", id, newName, newUrl));
        validateURL(newUrl);
        Optional<RegisteredService> existingRegisteredService = registeredServiceRepository.findById(id);

        if(existingRegisteredService.isEmpty()) {
            log.info(String.format("No service found with ID %s, rejecting request.", id));
            throw new ResponseStatusException(BAD_REQUEST, "Service with provided ID not found");
        }
        RegisteredService service = existingRegisteredService.get();
        registeredServiceRepository.updateServiceSettings(service.getId(), newName, newUrl, ServiceStatus.WAITING);
        log.info(String.format("Service with ID %s updated with new name '%s' and new URL '%s", id, newName, newUrl));
    }

    public void stopMonitoringService(Long id) {
        try {
            registeredServiceRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Failed to delete registered service with ID %s, as it does not exist.");
        }
    }

    public List<RegisteredService> getAllMonitoredServices() {
        return registeredServiceRepository.findAll();
    }


    public List<RegisteredService> getMonitoredServicesForUser(String username) {
        String normalisedUsername = normaliseUsername(username);
        return registeredServiceRepository.findByUsername(normalisedUsername);
    }

    private String normaliseUsername(String username) {
        String normalisedUsername;

        if(username == null) {
            normalisedUsername = "";
        } else {
            normalisedUsername = username.trim().toUpperCase();
        }

        if(username.isEmpty()){
            log.info(String.format("Rejecting request as no username was provided: %s", username));
            throw new ResponseStatusException(BAD_REQUEST, "No username provided");
        }

        if(normalisedUsername.length() > 255) {
            log.info(String.format("Rejecting request as username was too long: %s", username));
            throw new ResponseStatusException(BAD_REQUEST, "Username too long (max 255 chars)");
        }

        return normalisedUsername;
    }

    private void validateURL(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            log.info(String.format("Rejecting request as the URL was invalid: %s", url));
            throw new ResponseStatusException(BAD_REQUEST, "Provided URL is not a valid URL");
        }

        if(url.length() > 2048) {
            log.info(String.format("Rejecting request as the URL was too long: %s", url));
            throw new ResponseStatusException(BAD_REQUEST, "Provided URL is too long (max len 255)");
        }
    }

}
