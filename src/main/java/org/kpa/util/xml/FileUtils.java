package org.kpa.util.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileUtils {
    public static InputStream classOrPath(String url) throws FileNotFoundException {
        InputStream resourceAsStream = Class.class.getResourceAsStream(url);
        return resourceAsStream != null ? resourceAsStream : new FileInputStream(url);
    }
}
