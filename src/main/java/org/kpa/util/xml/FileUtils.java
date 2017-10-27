package org.kpa.util.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLClassLoader;

public class FileUtils {
    public static InputStream classOrPath(String url) throws FileNotFoundException {
        InputStream resourceAsStream = URLClassLoader.getSystemResourceAsStream(url);
        return resourceAsStream != null ? resourceAsStream : new FileInputStream(url);
    }
}
