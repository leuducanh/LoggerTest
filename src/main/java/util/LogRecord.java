package util;

import java.io.Serializable;

public class LogRecord implements Serializable {

    private static long globalSequenceNumber;
    private static int nextThreadId = 10;
    private static ThreadLocal<Integer> threadIds = new ThreadLocal<Integer>();

    private Level level;

    private long sequenceNumber;

    private String sourceClassName;

    private String sourceMethodName;

    private String message;

    private int threadId;

    // event time since 1970
    private long millis;

    private Throwable throwable;

    // name of source logger
    private String loggerName;

    private transient boolean needToInferCaller;

    private transient Object[] parameters[];

    public LogRecord(Level level, String msg) {
        //make sure not null
        level.getClass();
        this.level = level;
        message = msg;

        //Assign a thread id and a unique sequence num
        synchronized (LogRecord.class) {
            sequenceNumber = globalSequenceNumber++;
            Integer id = threadIds.get();
            if(id == null) {
                id = new Integer(nextThreadId++);
            }
        }
    }
}
