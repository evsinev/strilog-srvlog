package com.payneteasy.strilog.sender.task.batch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.srvlog.api.exception.SrvlogUnknownException;
import com.payneteasy.strilog.sender.task.converter.ILineToItemConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BatchSender<I> {

    private static final Logger LOG = LoggerFactory.getLogger(BatchSender.class);

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    private final ILineToItemConverter<I>     converter;
    private final int                         maxItemsCount;
    private final int                         maxBytes;
    private final List<I>                     list;
    private final IBatchSenderClient<I>       batchSenderClient;
    private final IBatchItemSizeCalculator<I> batchItemSizeCalculator;
    private final Duration                    errorSleepDuration;
    private final File                        failDir;

    private long currentBytes;

    public BatchSender(
            ILineToItemConverter<I> aConverter
            , IBatchSenderClient<I> aClient
            , IBatchItemSizeCalculator<I> aCalculator
            , int aMaxItemsCount
            , int aMaxBytes
            , Duration aErrorSleepDuration
            , File aFailDir
    ) {
        converter               = aConverter;
        maxItemsCount           = aMaxItemsCount;
        list                    = new ArrayList<>(aMaxItemsCount);
        batchSenderClient       = aClient;
        batchItemSizeCalculator = aCalculator;
        maxBytes                = aMaxBytes;
        errorSleepDuration      = aErrorSleepDuration;
        currentBytes            = 0;
        failDir                 = aFailDir;
    }

    public void addLine(String aLine) {
        I item = converter.convertToItem(aLine);
        currentBytes += batchItemSizeCalculator.sizeOfItem(item);
        list.add(item);
    }

    public boolean shouldSend() {
        return currentBytes >= maxBytes || list.size() >= maxItemsCount;
    }

    public void sendBatch() {
        LOG.debug("Sending batch of {} items with ~{} bytes ...", list.size(), currentBytes);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                try {
                    batchSenderClient.sendItems(list);
                } catch (SrvlogUnknownException e) {
                    LOG.error("Cannot send batch. Will send items one by one", e);
                    resendItemsOneByOne();
                }
                clearItems();
                return;
            } catch (Exception e) {
                LOG.error("Cannot send batch of {} items with ~{} bytes. Sleeping {}", list.size(), currentBytes, errorSleepDuration, e);
                try {
                    //noinspection BusyWait
                    Thread.sleep(errorSleepDuration.toMillis());
                } catch (InterruptedException ex) {
                    LOG.warn("Batch sleep interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void resendItemsOneByOne() {
        for (I item : list) {
            try {
                batchSenderClient.sendItems(List.of(item));
            } catch (Exception e) {
                saveFailedItem(item);
            }
        }
    }

    private void saveFailedItem(I item) {
        if (failDir.exists()) {
            if (failDir.mkdirs()) {
                LOG.error("Cannot create dir {} to save {}", failDir.getAbsolutePath(), GSON.toJson(item));
                return;
            }
        }

        File file = new File(failDir, System.currentTimeMillis() + ".json");
        try (FileWriter out = new FileWriter(file)) {
            GSON.toJson(item, out);
            LOG.error("Saved failed item to {}", file.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Cannot save item to {} : {}", file.getAbsolutePath(), GSON.toJson(item));
        }

    }

    private void clearItems() {
        list.clear();
        currentBytes = 0;
    }
}
