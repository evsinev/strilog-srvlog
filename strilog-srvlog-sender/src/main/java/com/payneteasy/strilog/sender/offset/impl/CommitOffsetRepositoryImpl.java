package com.payneteasy.strilog.sender.offset.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.strilog.sender.offset.CommitOffset;
import com.payneteasy.strilog.sender.offset.ICommitOffsetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.util.Optional.*;

public class CommitOffsetRepositoryImpl implements ICommitOffsetRepository {

    private static final Logger             LOG          = LoggerFactory.getLogger(CommitOffsetRepositoryImpl.class);
    private static final CommitOffsetConfig EMPTY_CONFIG = CommitOffsetConfig.builder().offsets(new HashMap<>()).build();

    private final Gson          gson          = new GsonBuilder().setPrettyPrinting().create();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock          readLock      = readWriteLock.readLock();
    private final Lock          writeLock     = readWriteLock.writeLock();

    private final File configFile;
    private final Path configPath;

    public CommitOffsetRepositoryImpl(File file) {
        this.configFile = file;
        configPath      = file.toPath();
    }

    @Override
    public void saveCommitOffset(CommitOffset aCommitOffset) {
        writeLock.lock();
        try {
            CommitOffsetConfig config = fillOffsetsIfNeeded(loadConfig().orElse(EMPTY_CONFIG));
            config.getOffsets().put(aCommitOffset.getPath(), aCommitOffset);
            writeConfig(config);
        } finally {
            writeLock.unlock();
        }
    }

    private void writeConfig(CommitOffsetConfig aConfig) {
        try(Writer out = Files.newBufferedWriter(configPath, UTF_8)) {
            gson.toJson(aConfig, out);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot write config to file " + configFile.getAbsolutePath(), e);
        }
    }

    private CommitOffsetConfig fillOffsetsIfNeeded(CommitOffsetConfig aConfig) {
        return aConfig.getOffsets() != null
                ? aConfig
                : aConfig.toBuilder()
                    .offsets(new HashMap<>())
                    .build();
    }

    @Override
    public Optional<CommitOffset> getCommitOffset(String aPath) {
        readLock.lock();
        try {

            Optional<CommitOffsetConfig> configOption = loadConfig();

            if(configOption.isEmpty()) {
                return empty();
            }

            CommitOffsetConfig config = configOption.get();

            if (config.getOffsets() == null) {
                return empty();
            }

            return ofNullable(config.getOffsets().get(aPath));

        } finally {
            readLock.unlock();
        }

    }

    private Optional<CommitOffsetConfig> loadConfig() {
        if (!configFile.exists()) {
            return empty();
        }

        try (Reader in = newBufferedReader(configPath, UTF_8)) {
            return of(gson.fromJson(in, CommitOffsetConfig.class));
        } catch (Exception e) {
            LOG.error("Error while reading file {}", configFile.getAbsolutePath(), e);
            return empty();
        }

    }
}
