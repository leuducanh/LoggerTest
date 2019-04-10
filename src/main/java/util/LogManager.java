package util;


import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Properties;

public class LogManager {

    // The global LogManager object
    private static  LogManager manager;

    private final static Handler[] emptyHandlers = {};
    private Properties props = new Properties();

    private final static Level defailtLevel = Level.INFO;

    // b?ng cho c�c logger ?� bi?t
    private Hashtable<String, WeakReference<Logger>> nameToLogger = new Hashtable<>();

    // ?� kh?i t?o global root handler ch?a?
    private boolean isInitializedGlobalHandlers = false;

    // c�y logger
    //    private Logger root = new ;
    private Logger rootLogger;

    private boolean initializedGlobalHandlers = true;

    // bi?n x�c ??nh l� jvm s?p ???c t?t v� qu� tr�nh exit hook s?p x?y ra
    private boolean deathImminent =false;

    static {
        if(manager == null) {
            manager = new LogManager();
        }


        manager.rootLogger = manager

    }

    /**
     * d�ng ?? load global handlers,
     * kh?i t?o lazily, khi global handlers l�n ??u ???c d�ng
     */
    private synchronized void setInitializedGlobalHandlers() {
        if(initializedGlobalHandlers) {
            return;
        }

        initializedGlobalHandlers = true;

        if(deathImminent) {
            // VM ?ang ???c shutdown v� exit hook ?ang ?c g?i ko
            // c?n vi?c cung c?p b? nh? cho global handlers
            return;
        }


    }
}
