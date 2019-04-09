package util;

public abstract class Formatter {

    protected Formatter() {

    }

    public abstract String format(LogRecord logRecord);

    // có thể đc override bởi subclass
    public String getHead(Handler h) {
        return "";
    }

    public String getTail(Handler h) {
        return "";
    }
}
