package com.kry.utility.servicepoller;

import com.kry.utility.servicepoller.poller.ServiceMonitoringService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class ServiceMonitoringServiceTest {

    @Mock
    private RegisteredServiceRepository mockRegisteredServiceRepository;

    @InjectMocks
    private ServiceMonitoringService serviceMonitoringService;

    @Captor
    ArgumentCaptor<RegisteredService> registeredServiceCaptor;

    @Test
    public void should_throw_bad_username_exception() {
        ResponseStatusException noUsernameException = assertThrows(ResponseStatusException.class, () -> {
            serviceMonitoringService.registerNewService("name", "https://www.kry.se/en/about/", null);
        });

        assertEquals("No username provided", noUsernameException.getReason());
        assertEquals(noUsernameException.getStatus(), HttpStatus.BAD_REQUEST);

        ResponseStatusException emptyUsernameException = assertThrows(ResponseStatusException.class, () -> {
            serviceMonitoringService.registerNewService("name", "https://www.kry.se/en/about/", "  ");
        });

        assertEquals("No username provided", emptyUsernameException.getReason());
        assertEquals(emptyUsernameException.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @ParameterizedTest
    @ValueSource(strings = { "www.kry.se/en/about/", "ht://www.kry.se/en/about/", "" })
    public void should_throw_invalid_url_exception(String url) {
        ResponseStatusException badURLException = assertThrows(ResponseStatusException.class, () -> {
            serviceMonitoringService.validateURL(url);
        });

        assertEquals("Provided URL is not a valid URL", badURLException.getReason());
        assertEquals(badURLException.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_throw_url_too_long_exception() {
        String longURL = "https://www.kry.se/en/about/" + "a".repeat(2048);
        ResponseStatusException badURLException = assertThrows(ResponseStatusException.class, () -> {
            serviceMonitoringService.validateURL(longURL);
        });

        assertEquals("Provided URL is too long (max len 2048)", badURLException.getReason());
        assertEquals(badURLException.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void should_save_then_return_new_registered_service() {
        String serviceName = "Kry About";
        String serviceURL = "https://www.kry.se/en/about/";
        String username = "Alex";
        String normalisedUsername = serviceMonitoringService.normaliseUsername(username);
        RegisteredService returnedService = serviceMonitoringService.registerNewService(serviceName, serviceURL, username);

        verify(mockRegisteredServiceRepository, times(1)).save(registeredServiceCaptor.capture());
        RegisteredService savedService = registeredServiceCaptor.getAllValues().get(0);

        assertEquals(savedService.getName(), serviceName);
        assertEquals(savedService.getUrl(), serviceURL);
        assertEquals(savedService.getUsername(), serviceMonitoringService.normaliseUsername(username));

        assertEquals(returnedService.getName(), serviceName);
        assertEquals(returnedService.getUrl(), serviceURL);
        assertEquals(returnedService.getUsername(), normalisedUsername);
    }

    // ... more
}

