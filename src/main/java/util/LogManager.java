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

    // b?ng cho các logger ?ã bi?t
    private Hashtable<String, WeakReference<Logger>> nameToLogger = new Hashtable<>();

    // ?ã kh?i t?o global root handler ch?a?
    private boolean isInitializedGlobalHandlers = false;

    // cây logger
    //    private Logger root = new ;
    private Logger rootLogger;

    private boolean initializedGlobalHandlers = true;

    // bi?n xác ??nh là jvm s?p ???c t?t và quá trình exit hook s?p x?y ra
    private boolean deathImminent =false;

    static {
        if(manager == null) {
            manager = new LogManager();
        }


        manager.rootLogger = manager

    }

    /**
     * dùng ?? load global handlers,
     * kh?i t?o lazily, khi global handlers làn ??u ???c dùng
     */
    private synchronized void setInitializedGlobalHandlers() {
        if(initializedGlobalHandlers) {
            return;
        }

        initializedGlobalHandlers = true;

        if(deathImminent) {
            // VM ?ang ???c shutdown và exit hook ?ang ?c g?i ko
            // c?n vi?c cung c?p b? nh? cho global handlers
            return;
        }


    }
}
