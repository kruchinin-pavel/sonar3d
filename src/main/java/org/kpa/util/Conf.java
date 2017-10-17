package org.kpa.util;

import org.apache.commons.lang3.StringUtils;

public class Conf {
    public static String getProperty(String propertyName, String propDescription, String defaultValue) {
        return getProperty(propertyName, propDescription, defaultValue, defaultValue == null);
    }

    public static String getSilent(String propertyName, String defaultVal) {
        return getProperty(propertyName, propertyName, defaultVal);
    }

    public static String getSilent(String propertyName) {
        return getProperty(propertyName, "", null, false);
    }

    public static String getProperty(String propertyName, String propDescription) {
        return getProperty(propertyName, propDescription, null);
    }

    public static String getProperty(String propertyName, String propDescription, String defaultValue, boolean throwOnAbsent) {
        String value = System.getProperty(propertyName);
        if (StringUtils.isEmpty(value)) {
            value = System.getenv(propertyName);
        }
        if (StringUtils.isEmpty(value)) {
            if (throwOnAbsent) {
                throw new IllegalArgumentException("Property " + propertyName + " is not set. Need to set it for: " + propDescription);
            } else {
                return defaultValue;
            }
        }
        return value;
    }


}
