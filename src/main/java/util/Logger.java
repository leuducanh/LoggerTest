package util;

import java.util.ArrayList;

public class Logger {


    private static final Handler[] emptyHandlers = new Handler[0];
    private LogManager manager;
    private String name;
    private ArrayList<Handler> handlers;


    /**
     * t�m v� t?o logger cho named subsystem n?u logger ?� ?c t?o v?i t�n
     *
     *
     */
    public static synchronized Logger getLogger(String name) {
        LogManager manager = LogManager.get
    }
}
