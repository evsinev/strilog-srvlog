package com.payneteasy.srvlog.api.messages;

import com.payneteasy.srvlog.api.model.SaveLogEvent;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class SaveLogsRequest {
    String             requestId;
    List<SaveLogEvent> messages;
}
