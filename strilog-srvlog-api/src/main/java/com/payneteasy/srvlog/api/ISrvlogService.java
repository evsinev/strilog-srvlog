package com.payneteasy.srvlog.api;

import com.payneteasy.srvlog.api.exception.SrvlogUnknownException;
import com.payneteasy.srvlog.api.messages.SaveLogsRequest;
import com.payneteasy.srvlog.api.messages.SaveLogsResponse;

public interface ISrvlogService {

    SaveLogsResponse saveLogs(SaveLogsRequest aLogs) throws SrvlogUnknownException;

}
