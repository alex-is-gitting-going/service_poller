package com.kry.utility.servicepoller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kry.utility.servicepoller.poller.ServiceMonitoringController;
import com.kry.utility.servicepoller.poller.ServiceMonitoringService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.registeredservice.ServiceStatus;
import com.kry.utility.servicepoller.poller.requests.MonitorNewServiceRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ServiceMonitoringController.class)
class ServiceMonitoringControllerTest {

    @MockBean
    ServiceMonitoringService mockServiceMonitoringService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @Test
    public void should_return_list_of_services() throws Exception {
        List<RegisteredService> dummyServiceList = new ArrayList<>();
        dummyServiceList.add(new RegisteredService(1l,
                "Kry Homepage",
                "https://www.kry.se/en/about/",
                ServiceStatus.OK,
                LocalDateTime.now(),
                LocalDateTime.now(),
                "alex"));

        when(mockServiceMonitoringService.getAllMonitoredServices()).thenReturn(dummyServiceList);

        String response = mockMvc.perform(get("/api/v1/monitoredservices/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(response, mapper.writeValueAsString(dummyServiceList));
    }

    @Test
    public void should_return_empty_list_of_services() throws Exception {
        List<RegisteredService> dummyServiceList = new ArrayList<>();

        when(mockServiceMonitoringService.getAllMonitoredServices()).thenReturn(dummyServiceList);

        String response = mockMvc.perform(get("/api/v1/monitoredservices/all"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(response, mapper.writeValueAsString(dummyServiceList));
    }

    @Test
    public void should_return_newly_registered_service() throws Exception {
        MonitorNewServiceRequest request = new MonitorNewServiceRequest();
        request.setUsername("alex");
        request.setName("Kry homepage");
        request.setURL("https://www.kry.se/en/about/");

        RegisteredService service = new RegisteredService(request.getName(), request.getURL(), request.getUsername());

        when(mockServiceMonitoringService.registerNewService(request.getName(), request.getURL(), request.getUsername())).thenReturn(service);

        mockMvc.perform(
                post("/api/v1/monitoredservices")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.url").value(request.getURL()))
                .andExpect(jsonPath("$.username").value(request.getUsername()));
    }

    // etc...
}
