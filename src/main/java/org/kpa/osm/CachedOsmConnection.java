package org.kpa.osm;

import de.westnordost.osmapi.ApiRequestWriter;
import de.westnordost.osmapi.ApiResponseReader;
import de.westnordost.osmapi.OsmConnection;
import oauth.signpost.OAuthConsumer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class CachedOsmConnection extends OsmConnection {
    private static final Logger logger = LoggerFactory.getLogger(CachedOsmConnection.class);
    private String cacheDirectory;

    public String getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(String cacheDirectory) {
        this.cacheDirectory = cacheDirectory;
    }

    public CachedOsmConnection(String apiUrl, String userAgent, OAuthConsumer oauth, Integer timeout) {
        super(apiUrl, userAgent, oauth, timeout);
    }

    public CachedOsmConnection(String apiUrl, String userAgent, OAuthConsumer oauth) {
        super(apiUrl, userAgent, oauth);
    }

    public CachedOsmConnection(String apiUrl, String userAgent) {
        super(apiUrl, userAgent);
    }

    private String callToName(String call) {
        return cacheDirectory + "/" + FilenameUtils.normalize(call)
                .replace("/", "")
                .replace("\\", "")
                .replace(":", "");
    }

    private InputStream getFromCache(String call) {
        try {
            File file = new File(callToName(call));
            if (!file.exists()) {
                return null;
            }
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T makeRequest(String call, String method, boolean authenticate, ApiRequestWriter writer, ApiResponseReader<T> reader) {
        InputStream is = getFromCache(call);
        if (is == null) {
            return super.makeRequest(call, method, authenticate, writer, new ApiResponseReader<T>() {
                @Override
                public T parse(InputStream in) throws Exception {
                    logger.info("Cache miss. Requesting from OSM api via internet: {}", call);
                    IOUtils.copy(in, new FileOutputStream(callToName(call)));
                    return reader.parse(getFromCache(call));
                }
            });
        }
        try {
            return reader.parse(getFromCache(call));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
