package com.kry.utility.servicepoller;

import com.kry.utility.servicepoller.poller.ScheduledMonitoringTask;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredService;
import com.kry.utility.servicepoller.poller.registeredservice.RegisteredServiceRepository;
import com.kry.utility.servicepoller.poller.registeredservice.ServiceStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class ScheduledMonitoringTaskTest {


    @InjectMocks
    private ScheduledMonitoringTask scheduledMonitoringTask;

    @Mock
    HttpURLConnection mockConnection;

    @Mock
    RegisteredService registeredService;

    @ParameterizedTest
    @MethodSource("provideServiceStatusParameters")
    public void should_return_fail_when_request_fail(HttpStatus status, ServiceStatus expectedResult) throws  IOException {
        doReturn(status.value()).when(mockConnection).getResponseCode();
        ServiceStatus returnedStatus = scheduledMonitoringTask.getServiceStatus(registeredService, mockConnection);
        Assertions.assertEquals(returnedStatus, expectedResult);
    }

    private static Stream<Arguments> provideServiceStatusParameters() {
        return Stream.of(
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, ServiceStatus.FAIL),
                Arguments.of(HttpStatus.ACCEPTED, ServiceStatus.OK),
                Arguments.of(HttpStatus.CREATED, ServiceStatus.OK)
                // .. more
        );
    }


    // .. more
}
