package com.payneteasy.strilog.sender;


import com.payneteasy.srvlog.api.impl.SrvlogClientImpl;
import com.payneteasy.strilog.sender.config.ConfigLoader;
import com.payneteasy.strilog.sender.config.TSenderConfig;
import com.payneteasy.strilog.sender.config.TSenderDir;
import com.payneteasy.strilog.sender.offset.impl.OffsetStoreImpl;
import com.payneteasy.strilog.sender.task.batch.BatchItemSizeCalculatorImpl;
import com.payneteasy.strilog.sender.task.batch.BatchSender;
import com.payneteasy.strilog.sender.task.batch.BatchSenderClientImpl;
import com.payneteasy.strilog.sender.task.converter.LineToSaveLogEventConverter;
import com.payneteasy.strilog.sender.task.dir.DirSenderTask;
import com.payneteasy.strilog.sender.task.file.FileSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class SenderApplication {

    public static void main(String[] args) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        IStartupConfig config = getStartupParameters(IStartupConfig.class);

        TSenderConfig senderConfig = new ConfigLoader().loadConfig(config.senderConfigFile());

        //noinspection resource
        ExecutorService executor = Executors.newFixedThreadPool(senderConfig.getDirs().size());

        BatchItemSizeCalculatorImpl batchItemSizeCalculator = new BatchItemSizeCalculatorImpl();

        BatchSenderClientImpl batchSenderClient = new BatchSenderClientImpl(
                new SrvlogClientImpl(
                        config.srvlogSaveUrl()
                        , config.srvlogToken())
        );

        Logger log = LoggerFactory.getLogger( SenderApplication.class );

        for (TSenderDir dirConfig : senderConfig.getDirs()) {
            log.info("Registering dir {} ...", dirConfig);

            File dir = new File(dirConfig.getPath());
            executor.execute(new DirSenderTask(
                    dirConfig
                    , new FileSenderImpl(
                        new OffsetStoreImpl(dir)
                        , new BatchSender<>(
                            new LineToSaveLogEventConverter(dirConfig.getProgram(), dirConfig.getFacility())
                            , batchSenderClient
                            , batchItemSizeCalculator
                            , config.maxBatchItems()
                            , config.maxBatchSize()
                            , config.batchErrorSleep()
                        )
                    )
                    , config.dirSleep()
                    , config.dirDetectOldFiles()
            ));
        }


        executor.shutdown();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown ...");
            executor.shutdownNow();
        }));
    }
}
