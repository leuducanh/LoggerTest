package util;

import java.util.ArrayList;

public class Logger {


    private static final Handler[] emptyHandlers = new Handler[0];
    private LogManager manager;
    private String name;
    private ArrayList<Handler> handlers;


    /**
     * tìm và t?o logger cho named subsystem n?u logger ?ã ?c t?o v?i tên
     *
     *
     */
    public static synchronized Logger getLogger(String name) {
        LogManager manager = LogManager.get
    }
}
