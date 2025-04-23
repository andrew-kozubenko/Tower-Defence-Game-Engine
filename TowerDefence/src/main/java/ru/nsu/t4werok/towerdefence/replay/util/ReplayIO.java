package ru.nsu.t4werok.towerdefence.replay.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nsu.t4werok.towerdefence.replay.model.ReplayDataModel;

import java.io.File;

public class ReplayIO {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static void save(ReplayDataModel model, String path) throws Exception {
        MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(path), model);
    }
    public static ReplayDataModel load(String path) throws Exception {
        return MAPPER.readValue(new File(path), ReplayDataModel.class);
    }
}