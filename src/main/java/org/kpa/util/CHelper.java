package org.kpa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.function.Consumer;

public class CHelper {
    private static final Logger logger = LoggerFactory.getLogger(CHelper.class);

    public static String fillUserAndHost(String fmt) {
        String hostName;
        try {
            java.net.InetAddress localMachine = null;
            localMachine = java.net.InetAddress.getLocalHost();
            hostName = localMachine.getHostName();
        } catch (UnknownHostException e) {
            hostName = "unknown";
        }
        return String.format(fmt, System.getProperty("user.name"), hostName);
    }

    public static String localHostAndUserName() {
        String s = fillUserAndHost("user=%s on host=%s") + ", debug enabled = " + logger.isDebugEnabled();
        logger.debug("Debug control message");
        return s;
    }

    public static void startInWrap(String[] args, InsecureCosumer<String[]> run) {
        try {
            logger.info("Starting with {}", CHelper.localHostAndUserName());
            KeyboardEnterActor.stopCurrentThreadOnEnter();
            long ct = System.currentTimeMillis();
            run.accept(args);
            logger.info("Completed running for {} seconds.", (System.currentTimeMillis() - ct) / 1000);
        } catch (Throwable e) {
            logger.error("Error happened. Exit(1)", e);
            System.exit(1);
        }
    }

    public static <T> void forEachSecure(Collection<T> lst, Consumer<? super T> action) {
        lst.forEach(t -> {
            try {
                action.accept(t);
            } catch (Exception e) {
                logger.error("Error in {}. Ignoring.", e);
            }
        });
    }

}

