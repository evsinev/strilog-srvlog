package com.payneteasy.strilog.sender.task.batch;

public interface IBatchItemSizeCalculator<I> {

    long sizeOfItem(I aItem);

}
