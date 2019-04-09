package util;

public interface Filter {
    public boolean isLoggable(LogRecord record);
}
