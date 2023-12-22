## Overview

Sends logs from json to srvlog.                     

  
## Env variables

```shell
BATCH_ERROR_SLEEP            = PT1S
BATCH_MAX_BYTES              = 1000000
BATCH_MAX_ITEMS              = 1000
CONFIG_FILE                  = ./config.yaml
DIR_DETECT_OLD_FILES         = PT3M
DIR_SLEEP_BETWEEN_LIST_FILES = PT1S
SRVLOG_SAVE_URL              = http://localhost:28080/save-logs
SRVLOG_TOKEN                 = *******
```
## Config example

./config.yaml

```yaml
dirs:
  - path: ./target/logs
    facility: 8
    program: hello

  - path: ./target/logs2
    facility: 8
    program: hello
```
    
## How to configure logback.xml for your app

```xml
<appender name="JSON" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <encoder class="com.payneteasy.strilog.encoder.json.JsonEncoder" />

    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>/var/log/app-1-json/json-%d{yyyy-MM-dd-HH}.log</fileNamePattern>
        <maxHistory>30</maxHistory>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>

</appender>
```
