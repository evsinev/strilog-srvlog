package com.payneteasy.strilog.sender.task.batch;

import com.payneteasy.srvlog.api.model.SaveLogEvent;

public class BatchItemSizeCalculatorImpl implements IBatchItemSizeCalculator<SaveLogEvent> {

    @Override
    public long sizeOfItem(SaveLogEvent aItem) {
        return    aItem.getMessage().length()
                + aItem.getProgram().length()
                + 10 // facility;
                + 10 // level
                + 10 // timestamp
        ;
    }
}
