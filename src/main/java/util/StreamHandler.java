package util;

import java.io.*;

public class StreamHandler extends Handler {

    private OutputStream outputStream;
    private Writer writer;
    private boolean header;

    private void configure() {
//        LogManager
        String className = getClass().getName();
        setLogLevel(Level.INFO);
        setFilter(null);
        setFormatter(new SimpleFormatter());
    }

    public StreamHandler() {
        configure();
    }

    public StreamHandler(OutputStream outputStream, Formatter formatter) {
        configure();
        setFormatter(formatter);
        setOutputStream(outputStream);
    }

    private synchronized void setOutputStream(OutputStream outputStream) {
        if (outputStream == null) throw new NullPointerException();

        flushAndClose();
        this.outputStream = outputStream;
        header = false;

        String encoding = getEncoding();
        if(encoding == null) {
            writer = new OutputStreamWriter(outputStream);
        }else{
            try {
                writer = new OutputStreamWriter(outputStream, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    public void setEncoding(String encoding) {
        super.setEncoding(encoding);
        if(outputStream == null)
        if(writer != null) {
            flush();

        }
    }

    /**
     * phần header luôn được dùng để viết trước!!!
     * @param record
     */

    @Override
    public void publish(LogRecord record) {

    }


    @Override
    public synchronized void flush() {
        if (writer != null) {
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void flushAndClose() {
        if (writer != null) {
            try {
                if (!header) {
                    writer.write(getFormatter().getHead(this));
                    header = true;
                }

                writer.write(getFormatter().getTail(this));
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void close() {

    }
}
z