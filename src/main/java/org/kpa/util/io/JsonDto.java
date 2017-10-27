package org.kpa.util.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;


public class JsonDto {
    private static final Logger logger = LoggerFactory.getLogger(JsonDto.class);
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> T read(String fileName, Class<T> clazz) throws IOException {
        try (FileReader reader = new FileReader(fileName)) {
            return read(reader, clazz);
        }
    }

    public static <T> T read(Reader reader, Class<T> clazz) {
        try {
            return mapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void write(String fileName, Object msg) throws IOException {
        String temp = FileUtils.tempInSameDir(fileName);
        try (FileWriter writer = new FileWriter(temp)) {
            write(writer, msg);
        }
        FileUtils.replaceFile(new File(fileName), new File(temp), false);
    }

    public static void write(Writer writer, Object msg) {
        try {
            mapper.writeValue(writer, msg);
        } catch (Exception e) {
            throw new RuntimeException("Error processing message " + msg + ": " + e.getMessage(), e);
        }
    }

    public static String write(Object msg) {
        StringWriter writer = new StringWriter();
        write(writer, msg);
        return writer.getBuffer().toString();
    }

    public static <T extends JsonDto> T loadFromString(String data) {
        try (StringReader reader = new StringReader(data)) {
            T read = read(reader, (Class<T>) JsonDto.class);
            return read;
        }
    }

    public static <T extends JsonDto> T load(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            T read = read(reader, (Class<T>) JsonDto.class);
            return read;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void store(String fileName, Object obj, boolean _throw) {
        try {
            Files.write(Paths.get(fileName), Collections.singletonList(write(obj)), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error storing file: " + e.getMessage(), e);
        }
    }

}
