package util;

import sun.rmi.runtime.Log;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Hashtable;

public class LogManager {

    // global log manager
    private static LogManager manager;

    private Hashtable<String, WeakReference<Logger>> nameToWeakReference = new Hashtable<>();
    // tree logger đã biết
    private LogNode root = new LogNode(null);


    private Level levelForAllInitial = Level.ALL;

    static {

    }


    public static LogManager getLogManager() {
        if (manager != null) {
            manager = new LogManager();
        }

        return manager;
    }

    private void readConfiguration() {

    }

    public void readConfiguration(InputStream ins) {

    }

    public synchronized boolean addLogger(Logger logger) {
        final String name = logger.getName();

        if (name == null) {
            throw new NullPointerException();
        }

        WeakReference<Logger> ref = nameToWeakReference.get(name);

        if (ref != null) {
            if (ref.get() == null) {

                // đã bị gc-ed nên xóa nó đi
                nameToWeakReference.remove(name);
            } else {
                // đã tồn tại.
                return false;
            }
        }

        // tạo logger mới với weak refer.
        nameToWeakReference.put(name, new WeakReference<>(logger));
        if (levelForAllInitial != null) {
            doSetLevel(logger, levelForAllInitial);
        }

        processParentHandlers(logger, name);

        // tìm node và parent của node
        LogNode logNode = findNode(name);
        logNode.loggerRef = new WeakReference<>(logger);
        LogNode parentNode = logNode.parent;

        Logger parentLoggerStillAlive = null;
        while (parentNode != null) {
            WeakReference<Logger> parentWR = parentNode.loggerRef;
            if (parentNode.loggerRef != null) {
                parentLoggerStillAlive = parentWR.get();
                if (parentLoggerStillAlive !=null ) {
                    break;
                }
            }

            parentNode = parentNode.parent;
        }

        if (parentLoggerStillAlive != null) {
            logger.setParent(parentLoggerStillAlive);
        }

        logNode.walkAndSetParent(logger);
        return true;
    }

    // tìm node trên cây.
    private LogNode findNode(String name) {
        if (name == null || name.isEmpty()) {
            return root;
        }

        LogNode pos = root;
        int end = 0;
        String current;
        while (name.length() > 0) {
            end = name.indexOf(".");

            if (end > 0) {
                current = name.substring(0, end);
                name = name.substring(end + 1);
            } else {
                current = name;
            }

            if (pos.children == null) {
                pos.children = new HashMap<>();
            }
            LogNode node = pos.children.get(current);
            if (node == null) {
                node = new LogNode(pos);
                pos.children.put(current, node);
            }

            pos = node;
        }

        return pos;
    }

    private void processParentHandlers(Logger logger, String name) {
        int start = 1;
        while (true) {
            int end = name.indexOf(".", start);
            if (end < 0) {
                return;
            }
            String parentName = name.substring(0, end);
            start = end + 1;
            demandLogger(parentName);
        }
    }

    /**
     * tìm hoặc khởi tạo logger , nếu tìm sẽ return còn ko tạo mới với global namespace.
     *
     * @param name
     * @return
     */
    synchronized Logger demandLogger(String name) {
        Logger result = getLogger(name);

        if (result == null) {
            Logger logger = new Logger(name);
            addLogger(logger);
            result = getLogger(name);
        }

        return result;
    }

    public synchronized Logger getLogger(String name) {
        WeakReference<Logger> ref = nameToWeakReference.get(name);
        if (ref == null) {
            return null;
        }

        Logger logger = ref.get();
        if (logger == null) {
            // đã bị GC-ed
            nameToWeakReference.remove(name);
        }
        return logger;
    }

    private void doSetLevel(Logger logger, Level level) {
        logger.setLevel(level);
    }

    private static void doSetParent(Logger logger, Logger parent) {
        logger.setParent(parent);
    }

    // nested class
    private static class LogNode {
        HashMap<String, LogNode> children;
        WeakReference<Logger> loggerRef;
        LogNode parent;

        public LogNode(LogNode parent) {
            this.parent = parent;
        }

        void walkAndSetParent(Logger parent) {
            if (children == null) {
                return;
            }

            children.entrySet().stream().forEach(entry -> {
                LogNode childNode = entry.getValue();
                WeakReference<Logger> childWR = childNode.loggerRef;
                if (childWR != null) {
                    Logger child = childWR.get();
                    if(child != null) {
                        child.setParent(parent);
                    }
                }

                childNode.walkAndSetParent(parent);
            });
        }

    }
}
