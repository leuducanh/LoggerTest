package util;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LogRecord implements Serializable {

//    private static long globalSequenceNumber;
//    private static int nextThreadId = 10;

    private static final AtomicLong globalSequenceNumber = new AtomicLong();
    /**
     * dùng chính id của thread gọi nó để làm threadid , nếu số lượng id vượt quá MIN_SEQUE đặt ra
     * thì lúc đó dùng biến atomicinteger để tính tiếp, lí do chia 2 bởi vì long khi ép về int thì có thể bị overrflow khi long vượt quá
     * max của int, nhưng tại sao lại ko xét là thread.currentThread.id < Integer.MAX thì gán luôn cho threadid mà lại  phải MAX_VALUE/2
     * vì cái id của current thread là long nó có thể nhảy lên lớn hơn Integer.MAX bất cứ lúc nào mà nếu để MIN_SEQUEN = Integer.MAX luôn thì
     * lúc nó nhảy vượt hơn ta sẽ không biết gán số int nào cho var threadid của LogRecord cả vì thế phải chia đôi và dùng nửa trên dành cho các
     * LogRecord có số threadid nhảy vọt lên.
     */
    private static final int MIN_SEQUE = Integer.MAX_VALUE / 2;
    private static final AtomicInteger nextThreadId = new AtomicInteger(MIN_SEQUE);
    private static ThreadLocal<Integer> threadIds = new ThreadLocal<Integer>();

    private Level level;

    //  Sequence number sẽ là số tăng trong một vm
    private long sequenceNumber;

    private String sourceClassName;

    private String sourceMethodName;

    private String message;

    // thread id gọi cái logging call ấy.
    private int threadId;

    // event time since 1970
    private long millis;

    private Throwable throwable;

    // name of source logger
    private String loggerName;

    private transient boolean needToInferCaller;

    private transient Object[] parameters;

    public LogRecord(Level level, String msg) {
        //make sure not null
        level.getClass();
        this.level = level;
        message = msg;

        //Assign a thread id and a unique sequence num
//        synchronized (LogRecord.class) {
//            sequenceNumber = globalSequenceNumber++;
//            Integer id = threadIds.get();
//            if (id == null) {
//                id = new Integer(nextThreadId++);
//                threadIds.set(id);
//            }
//            threadId = id.intValue();
//        }

        sequenceNumber = globalSequenceNumber.getAndIncrement();
        threadId = defaultIdCalculate();

        millis = System.currentTimeMillis();
        needToInferCaller = true;
    }

    private int defaultIdCalculate() {
        long idCurrentThread = Thread.currentThread().getId();
        if (idCurrentThread < MIN_SEQUE) {
            threadId = (int) idCurrentThread;
        } else {
            Integer id = threadIds.get();
            if (id == null) {
                id = nextThreadId.getAndIncrement();
                threadIds.set(id);
            }

            threadId = id;
        }

        return 0;
    }

    public String getLoggerName() {
        return this.loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException();
        }

        this.level = level;
    }

    // đc gắn số unique cho mỗi new record tăng dần.
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    //sourceclass name có thể được truyền vào hoặc đc suy ra trên call của stack frame nếu case sau thì phải check lại
    public String getSourceClassName() {
        if (needToInferCaller) inferCaller();
        return sourceClassName;
    }

    public String getSourceMethodName() {
        if (needToInferCaller) inferCaller();
        return sourceMethodName;
    }

    public void setParameters(Object[][] parameters) {
        this.parameters = parameters;
    }

//    // suy luận caller class và method name.
//    private void inferCaller() {
//        needToInferCaller = false;
//        // muốn biết class nào là class đầu tiên gọi đến thằng logger thì thử ném ra một throwable và suy luận từ stacktrace sinh ra
//        StackTraceElement[] stack = (new Throwable()).getStackTrace();
//        int ix = 0;
//        while (ix < stack.length && !stack[ix].getClassName().equals("util.Logger")) {
//            ix++;
//        }
//
//        while (ix < stack.length) {
//            StackTraceElement element = stack[ix];
//            String claName = element.getClassName();
//            if (claName.equals("util.Logger")) {
//                setSourceClassName(claName);
//                setSourceMethodName(element.getMethodName());
//                return;
//            }
//
//            ix++;
//        }
//    }

    /**
     * bỏ cách trên để dùng cách khác bằng native code hơn nhưng vẫn chậm hơn do nó ném ra exception thật.
     * tìm class gọi logger bằng cách check stack trace đến logger và tìm cái đầu tiên phía trước logger.
     */
    private void inferCaller() {
        JavaLangAccess access = SharedSecrets.getJavaLangAccess();
        Throwable throwable = new Throwable();
        int depth = access.getStackTraceDepth(throwable);

        String logClassName = "util.Logger";
        boolean isDetecedLoggerClassName = false;

        for (int i = 0; i < depth; i++) {
            // gọi geetStackTraceElement trực tiếp ngăn VM mất công khởi tạo toàn bộ stack frame.

            StackTraceElement stackTraceElement = access.getStackTraceElement(throwable, i);
            String name = stackTraceElement.getClassName();

            if (isDetecedLoggerClassName) {
                if (!name.equals(logClassName)) {

                    /**
                     *  ở đây trong bản gốc là còn phải bỏ qua sun.util.logging.PlatformLogger do
                     *  thằng logging trước đấy đc dùng cả trong jre nên để modul hóa và tránh dependency nhiều do mgười dùng ko hề log bằng loggger nhưng
                     *  jre lại cứ tạo ra nên phải có thằng bắt trước log là platformlogger để thằng này có thể bỏ qua việc setmethod và class cho các logger mà jre tạo ra.
                     */
                    if(name.equals("java.lang.reflect.")) {
                        setSourceClassName(name);
                        setSourceMethodName(stackTraceElement.getMethodName());
                        return;
                    }
                }
            } else {
                if (name.equals(logClassName)) {
                    isDetecedLoggerClassName = true;
                }
            }
        }

    }

    private static final long serialVersionUID = 5372048053134512534L;

    public void writeObject(ObjectOutputStream objectOutputStream) throws IOException {

        objectOutputStream.defaultWriteObject();

        // viết version number
        objectOutputStream.writeByte(1);
        objectOutputStream.writeByte(0);

        if (parameters == null) {
            objectOutputStream.writeByte(-1);
            return;
        }

        objectOutputStream.writeInt(parameters.length);

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i] == null) {
                objectOutputStream.writeObject(null);
            } else {
                objectOutputStream.writeObject(parameters[i].toString());
            }
        }
    }

    public void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {

        inputStream.defaultReadObject();

        // đọc version number
        byte major = inputStream.readByte();
        byte minor = inputStream.readByte();

        if (major != 1) {
            throw new IOException("LogRecord: bad version " + major + "." + minor);
        }

        int len = inputStream.readInt();
        if (len == -1) {
            parameters = null;
        } else {
            parameters = new Object[len];

            for (int i = 0; i < parameters.length; i++) {
                parameters[i] = inputStream.readObject();
            }
        }

        needToInferCaller = false;
    }

    private void setSourceMethodName(String methodName) {
        this.sourceMethodName = methodName;
        needToInferCaller = false;
    }

    private void setSourceClassName(String claName) {
        this.sourceClassName = claName;
        needToInferCaller = false;
    }

    // set "raw" tthông điệp
    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public long getMillis() {
        return millis;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

}
