package com.payneteasy.strilog.sender.task.converter;

import com.google.gson.Gson;
import com.payneteasy.srvlog.api.model.SaveLogEvent;
import com.payneteasy.strilog.sender.event.LogEvent;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

public class LineToSaveLogEventConverter implements ILineToItemConverter<SaveLogEvent> {

    private static final int INFO_LEVEL = 6;

    private final Gson gson = new Gson();

    private final String program;
    private final int    facility;

    public LineToSaveLogEventConverter(String program, int facility) {
        this.program  = program;
        this.facility = facility;
    }

    @Override
    public SaveLogEvent convertToItem(String aLine) {
        LogEvent event = gson.fromJson(aLine, LogEvent.class);
        return mapEvent(event);
    }

    private SaveLogEvent mapEvent(LogEvent aEvent) {
        return SaveLogEvent.builder()
                .time(aEvent.getEpoch())
                .severity(levelToSeverity(aEvent.getLevel()))
                .facility(facility)
                .program(program)
                .message(toMessage(aEvent))
                .build();
    }

    private String toMessage(LogEvent aEvent) {
        StringBuilder sb = new StringBuilder();

        sb.append(aEvent.getThread());
        sb.append(' ');
        sb.append(aEvent.getLevel());
        sb.append(' ');
        sb.append(aEvent.getClazz());
        sb.append(' ');

        if (aEvent.getArgs() != null && !aEvent.getArgs().isEmpty()) {
            sb.append(arrayFormat(aEvent.getTemplate(), aEvent.getArgs().toArray(new String[0])).getMessage());
        } else {
            sb.append(aEvent.getTemplate());
        }

        if (aEvent.getExceptionMessage() != null) {
            sb.append(' ');
            sb.append(aEvent.getExceptionMessage());
        }

        if (aEvent.getStacktrace() != null) {
            sb.append('\n');
            sb.append(aEvent.getStacktrace());
        }

        return sb.toString();
    }

    private int levelToSeverity(String level) {
        if (level == null) {
            return INFO_LEVEL;
        }

        return switch (level) {
            case "ERROR" -> 3;
            case "WARN"  -> 4;
            case "DEBUG", "TRACE" -> 7;
            default      -> INFO_LEVEL;
        };
    }

}
