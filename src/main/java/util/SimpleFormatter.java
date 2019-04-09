package util;

public class SimpleFormatter extends Formatter{

    @Override
    public String format(LogRecord logRecord) {
        return logRecord.getSourceClassName() + " " + logRecord.getSourceMethodName() + " "  + logRecord.getMessage();
    }
}
