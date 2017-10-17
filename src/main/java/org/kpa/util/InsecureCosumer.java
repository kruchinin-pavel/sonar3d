package org.kpa.util;

public interface InsecureCosumer<T> {
    void accept(T t) throws Exception;
}

