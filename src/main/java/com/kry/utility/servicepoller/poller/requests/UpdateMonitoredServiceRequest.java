package com.kry.utility.servicepoller.poller.requests;

import lombok.Data;

@Data
public class UpdateMonitoredServiceRequest {
    Long id;
    String name;
    String url;
}
