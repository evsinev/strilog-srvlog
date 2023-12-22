package com.payneteasy.strilog.sender.config;

import com.payneteasy.yaml2json.YamlParser;

import java.io.File;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ConfigLoader {

    private final YamlParser parser = new YamlParser();

    public TSenderConfig loadConfig(File aFile) {
        return validateConfig(
                parser.parseFile(aFile, TSenderConfig.class)
        );
    }

    private static TSenderConfig validateConfig(TSenderConfig config) {
        List<TSenderDir> dirs = requireNonNull(config.getDirs(), "No dirs");
        if (dirs.isEmpty()) {
            throw new IllegalStateException("Dirs is empty");
        }

        for (TSenderDir dir : dirs) {
            requireNonNull(dir.getPath(), "Path is empty");
            requireNonNull(dir.getPath(), "program is empty for path " + dir.getPath());
            if (dir.getFacility() <= 0) {
                throw new IllegalStateException("Facility is <= 0 for path " + dir.getPath());
            }

        }
        return config;
    }
}
