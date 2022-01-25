package com.kry.utility.servicepoller.poller;

import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredServiceRepository;
import com.kry.utility.servicepoller.poller.registeredservice.ServiceStatus;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.valueOf;

@Component
@AllArgsConstructor
public class ScheduledPollingTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledPollingTask.class);

    private RegisteredServiceRepository registeredServiceRepository;

    @Scheduled(fixedDelay = 250)
    public void pollRegisteredServices() {
        log.debug("Periodically polling all registered services now.");
        registeredServiceRepository.findFirst100ByOrderByLastPolledDateAsc().parallelStream().forEach((registeredService -> pollURL(registeredService)));
    }

    private void pollURL(RegisteredService service) {
        ServiceStatus currentStatus;
        try {
            HttpURLConnection connection = getHttpURLConnection(service.getUrl());
            currentStatus = getServiceStatus(service, connection);
        } catch (IOException e) {
            System.out.println(String.format("Failed to poll registered service id %s with url %s because of an exception.", service.getId(), service.getUrl()));
            log.debug(String.format("Failed to poll registered service id %s with url %s because of an exception.", service.getId(), service.getUrl()),e);
            currentStatus = ServiceStatus.FAIL;
        }

        // update instead of save, to avoid re-inserting a service that was deleted while the polling was executed.
        registeredServiceRepository.updateServiceStatus(service.getId(), currentStatus);
    }

    private ServiceStatus getServiceStatus(RegisteredService service, HttpURLConnection connection) throws IOException {
        HttpStatus responseStatus = valueOf(connection.getResponseCode());
        connection.disconnect();
        if(responseStatus.series() == HttpStatus.Series.SUCCESSFUL || // obvious success
                responseStatus.series() == HttpStatus.Series.INFORMATIONAL || // received and understood
                responseStatus.series() == HttpStatus.Series.REDIRECTION || // redirect means we got to the endpoint we were told to get to - sounds like a success
                responseStatus == TOO_MANY_REQUESTS) { // not serving us anymore - probably up though
            return ServiceStatus.OK;
        } else {
            System.out.println(String.format("Failed to poll url %s, with response status %s",service.getUrl(),responseStatus.toString()));
            log.debug(String.format("Failed to poll url %s, with response status %s",service.getUrl(),responseStatus.toString()));
            return ServiceStatus.FAIL;
        }
    }

    private HttpURLConnection getHttpURLConnection(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36");
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        return connection;
    }
}
