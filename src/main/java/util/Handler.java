package util;

/**
 * lấy log mess from logger và export chúng.
 */
public abstract class Handler {

    private static final int offValue = Level.OFF.intValue();
//    private LogManager;
    private Filter filter;
    private Formatter formatter;

    private Level logLevel = Level.ALL;
    private ErrorManager errorManager = new ErrorManager();
    private String encoding = "";

    protected Handler() {

    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * loggin request được tạo ra trong logger và tạo logrecord đưa đến hàm này
     * @param record
     */
    public abstract void publish(LogRecord record);

    public abstract void flush();

    // giải phóng tài nguyên và flush tất cả dữ liệu đang bị bufferd.
    public abstract void close();

    public void setFormatter(Formatter formatter) {
        formatter.getClass();
        this.formatter = formatter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        filter.getClass();
        this.filter = filter;
    }

    public ErrorManager getErrorManager() {
        return errorManager;
    }

    public void setErrorManager(ErrorManager errorManager) {
        this.errorManager = errorManager;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * nếu handler thực sự log một logrecord
     * @param logRecord
     * @return
     */
    public boolean isLoggable(LogRecord logRecord) {
        int levelVal = getLogLevel().intValue();
        if(logRecord.getLevel().intValue() < levelVal || levelVal == offValue) {
            return false;
        }

        Filter filter = getFilter();
        if(filter == null) {
            return true;
        }
        return filter.isLoggable(logRecord);
    }
}
