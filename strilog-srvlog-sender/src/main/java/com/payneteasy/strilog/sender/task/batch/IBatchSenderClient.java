package com.payneteasy.strilog.sender.task.batch;

import java.util.List;

public interface IBatchSenderClient<I> {

    void sendItems(List<I> aItems);

}
