package com.payneteasy.strilog.sender.offset;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder
public class CommitOffset {
    String path;
    String filename;
    long   lineNumber;
}
