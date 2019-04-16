package util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Logger {

    private static final Handler[] emptyHandler = new Handler[0];
    private static final int offValue = Level.OFF.intValue();
    private LogManager manager;

    private String name;
    private ArrayList<Handler> handlers;

    private boolean useParentHandlers = true;
    private Filter filter;

    // lock cho tree quan hệ parent - child.
    private static Object treeLock = new Object();

    //giữ weak refer từ parent đến con nhưng giữu strong từ con đến parent.
    private Logger parent;
    private ArrayList<WeakReference<Logger>> kids;
    private Level levelObject;
    private volatile int levelValue;

    protected Logger(String name) {
        this.name = name;
        manager = LogManager.getLogManager();
        levelValue = Level.INFO.intValue();
    }

    public static synchronized Logger getLogger(String name) {
        LogManager manager = LogManager.getLogManager();
    }

    public String getName() {
        return name;
    }

    public void setLevel(Level newLevel) {
        synchronized (treeLock) {
            levelObject = newLevel;
            updateEffectiveLevel();
        }
    }

    public void setParent(Logger newParent) {
        synchronized (treeLock) {
            this.parent = parent;
            updateEffectiveLevel();
        }
    }

    // tính toán lại level tốt nhất cho node này và update các con của nó
    private void updateEffectiveLevel() {
        int newLevelValue;
        if(levelObject != null) {
            newLevelValue = levelObject.intValue();
        } else {
            if(parent != null) {
                newLevelValue = parent.levelValue;
            } else {
                // dang khởi tạo
                newLevelValue = Level.INFO.intValue();
            }
        }

        // effective value chưa đổi thì đã xong.x
        if(levelValue == newLevelValue){
            return;
        }

        levelValue = newLevelValue;

        // đệ quy update trên kids.
        if(kids != null){
            kids.forEach(kid->{
                Logger kidLogger = kid.get();
                if(kidLogger != null) {
                    kidLogger.updateEffectiveLevel();
                }
            });
        }
    }

}
