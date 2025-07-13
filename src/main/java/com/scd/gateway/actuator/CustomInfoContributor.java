package com.scd.gateway.actuator;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomInfoContributor implements InfoContributor {
    private ServerInfo serverInfo;

    public CustomInfoContributor(ServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    @Override
    public void contribute(Info.Builder builder) {
        serverInfo.getApp().forEach(builder::withDetail);
    }
}
