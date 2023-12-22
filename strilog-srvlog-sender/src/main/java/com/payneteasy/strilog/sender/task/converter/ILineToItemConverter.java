package com.payneteasy.strilog.sender.task.converter;

public interface ILineToItemConverter<I> {

    I convertToItem(String aLine);

}
