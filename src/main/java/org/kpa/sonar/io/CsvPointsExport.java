package org.kpa.sonar.io;

import org.kpa.sonar.Point;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvPointsExport implements AutoCloseable {
    private final CsvMapWriter csvMapWriter;
    public static final String LAT = "широта(град.)";
    public static final String LON = "долгота(град.)";
    public static final String DEPTH = "глубина(м.)";
    public static final String TEMP = "температура(К)";
    public static final String NAME = "название";
    public static final String NUM = "номер";
    private static final String[] header = new String[]{LAT, LON, DEPTH, TEMP, NAME, NUM};

    public CsvPointsExport(String fileName) throws IOException {
        csvMapWriter = new CsvMapWriter(new FileWriter(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
        csvMapWriter.writeHeader(header);
    }

    public void print(Point point, String name, int num) {
        try {
            Map<String, Object> valMap = new HashMap<>();
            valMap.put(LON, point.getLongitude());
            valMap.put(LAT, point.getLattitude());
            valMap.put(DEPTH, point.getDepth());
            valMap.put(TEMP, point.getTemp());
            valMap.put(NAME, name);
            valMap.put(NUM, num);
            csvMapWriter.write(valMap, header);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (csvMapWriter != null) {
            csvMapWriter.close();
        }
    }
}
