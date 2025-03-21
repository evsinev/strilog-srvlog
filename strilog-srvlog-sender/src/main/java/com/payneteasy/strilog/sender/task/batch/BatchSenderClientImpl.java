package com.payneteasy.strilog.sender.task.batch;

import com.payneteasy.srvlog.api.ISrvlogService;
import com.payneteasy.srvlog.api.exception.SrvlogUnknownException;
import com.payneteasy.srvlog.api.messages.SaveLogsRequest;
import com.payneteasy.srvlog.api.messages.SaveLogsResponse;
import com.payneteasy.srvlog.api.model.SaveLogEvent;
import com.payneteasy.srvlog.api.model.SaveLogsStatus;

import java.util.List;
import java.util.UUID;

public class BatchSenderClientImpl implements IBatchSenderClient<SaveLogEvent> {

    private final ISrvlogService srvlogClient;

    public BatchSenderClientImpl(ISrvlogService srvlogClient) {
        this.srvlogClient = srvlogClient;
    }

    @Override
    public void sendItems(List<SaveLogEvent> aItems) throws SrvlogUnknownException {
        String           requestId = UUID.randomUUID().toString();
        SaveLogsResponse response;
        try {
            response = srvlogClient.saveLogs(SaveLogsRequest.builder()
                    .requestId(requestId)
                    .messages(aItems)
                    .build());
        } catch (SrvlogUnknownException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("Cannot send batch for request id " + requestId, e);
        }

        if (response.getStatus() != SaveLogsStatus.SUCCESS) {
            throw new IllegalStateException("Cannot save logs " + response);
        }

    }
}
