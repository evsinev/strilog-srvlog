package com.payneteasy.srvlog.api.impl;


import com.payneteasy.srvlog.api.messages.SaveLogsRequest;
import com.payneteasy.srvlog.api.messages.SaveLogsResponse;
import com.payneteasy.srvlog.api.model.SaveLogEvent;
import com.payneteasy.startup.parameters.AStartupParameter;
import com.payneteasy.startup.parameters.StartupParametersFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class SrvlogClientImplTest {

    private static final Logger LOG = LoggerFactory.getLogger( SrvlogClientImplTest.class );

    interface IConfig {
        @AStartupParameter(name = "SRVLOG_TOKEN", value = "test-token-197dc68c-34e5-11eb-9fbd-7bb165d4566c", maskVariable = true)
        String srvlogToken();

        @AStartupParameter(name = "SRVLOG_SAVE_URL", value = "")
        String srvlogSaveUrl();
    }

    @Test
    public void test() {
        IConfig config = StartupParametersFactory.getStartupParameters(IConfig.class);
        if(config.srvlogSaveUrl() == null || config.srvlogSaveUrl().isEmpty()) {
            LOG.warn("SRVLOG_SAVE_URL is empty. Skipping test.");
            return;
        }
        SrvlogClientImpl client = new SrvlogClientImpl(
                config.srvlogSaveUrl()
                , config.srvlogToken()
        );

        SaveLogsResponse saveLogsResponse = client.saveLogs(SaveLogsRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .messages(List.of(
                        SaveLogEvent.builder()
                                .time(System.currentTimeMillis())
                                .program("test-1")
                                .facility(1)
                                .severity(6)
                                .message("Test message " + System.currentTimeMillis())
                                .build()
                ))
                .build());

        System.out.println("saveLogsResponse = " + saveLogsResponse);
    }
}