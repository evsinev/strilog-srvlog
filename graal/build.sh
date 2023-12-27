#export NATIVE_IMAGE_USER_HOME=META-INF/native-image

native-image \
  --verbose \
  --allow-incomplete-classpath \
  --no-fallback  \
  -H:ConfigurationFileDirectories=META-INF/native-image \
  -H:IncludeResources=logback.xml \
  --enable-url-protocols=http,https \
  --initialize-at-build-time=org.slf4j.LoggerFactory,ch.qos.logback \
  -march=x86-64-v1 \
  -jar ../strilog-srvlog-sender/target/strilog-srvlog-sender.jar

