package com.payneteasy.strilog.sender;

import com.payneteasy.startup.parameters.AStartupParameter;

import java.io.File;
import java.time.Duration;

public interface IStartupConfig {

    @AStartupParameter(name = "SRVLOG_TOKEN", value = "test-token-197dc68c-34e5-11eb-9fbd-7bb165d4566c", maskVariable = true)
    String srvlogToken();

    @AStartupParameter(name = "SRVLOG_SAVE_URL", value = "http://localhost:28080/save-logs")
    String srvlogSaveUrl();

    @AStartupParameter(name = "CONFIG_FILE", value = "./config.yaml")
    File senderConfigFile();

    @AStartupParameter(name = "DIR_SLEEP_BETWEEN_LIST_FILES", value = "PT1S")
    Duration dirSleep();

    @AStartupParameter(name = "DIR_DETECT_OLD_FILES", value = "PT3M")
    Duration dirDetectOldFiles();

    @AStartupParameter(name = "BATCH_MAX_BYTES", value = "1000000")
    int maxBatchSize();

    @AStartupParameter(name = "BATCH_MAX_ITEMS", value = "1000")
    int maxBatchItems();

    @AStartupParameter(name = "BATCH_ERROR_SLEEP", value = "PT1S")
    Duration batchErrorSleep();

}
