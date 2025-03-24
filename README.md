## Overview

The app sends log messages from strilog-encoder to srvlog

* strilog-encoder https://github.com/evsinev/strilog-encoder
* srvlog https://github.com/payneteasy/srvlog

### How it works

* we write logs to a json file from our strilog-encoder plus file roller every minute a new file
* as soon as two files or more appear in the directory, we take the one that was previous
* convert from one message format to a message for srvlog
* we send several messages in batche
* since we don’t care about the delay here, we have 1 minute of messages in our queue
* if there no more than 1 file for more than 3 minutes, then we send one last file
* the log file is then deleted after successful sending
  
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
        <fileNamePattern>/var/log/app-1-json/json-%d{yyyy-MM-dd-HH-mm}.json</fileNamePattern>
        <maxHistory>30</maxHistory>
        <totalSizeCap>3GB</totalSizeCap>
    </rollingPolicy>

</appender>
```
             

## json api example

endpoint: http://localhost:28080/save-logs

Headers:

```
Content-Type: application/json; charset=utf-8.
Token: token-1d2ad41e-c44f-4e8d-89f8-2d988433ece6
```

```json
{
  "requestId": "0e088113-f578-42e3-8529-25a80b174f3a",
  "messages": [
    {
      "time": 1742813520982,
      "program": "program-1",
      "facility": 1,
      "severity": 4,
      "message": "message 1"
    },
    {
      "time": 1742813562657,
      "program": "program-2",
      "facility": 1,
      "severity": 4,
      "message": "message 2"
    }
  ]
}
```