package com.payneteasy.srvlog.api.messages;

import com.payneteasy.srvlog.api.model.SaveLogsStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class SaveLogsResponse {
    String         requestId;
    SaveLogsStatus status;
    String         errorMessage;
    String         errorId;
}
