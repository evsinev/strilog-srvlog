package com.payneteasy.strilog.sender.offset.impl;

import com.payneteasy.strilog.sender.offset.CommitOffset;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Builder(toBuilder = true)
public class CommitOffsetConfig {
    Map<String, CommitOffset> offsets;
}
