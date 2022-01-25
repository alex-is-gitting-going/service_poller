package com.kry.utility.servicepoller.poller.requests;

import lombok.Data;

@Data
public class MonitorNewServiceRequest {
    private String username;
    private String name;
    private String URL;
}
