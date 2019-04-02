package util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level implements Serializable {

    private static final long serialVersionUID = -8176160795706313070L;

    private final String name;

    private final int value;

    public Level(String name, int value) {
        this.name = name;
        this.value = value;
    }


    //for serializable
    private Object readResolve() {


        Level level = new Level(this.name, this.value);
        return level;
    }

    @Override
    public boolean equals(Object ox) {
        try {
            Level lx = (Level) ox;
            return (lx.value == this.value);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    static final class KnownLevel {

        private static Map<String, List<KnownLevel>> nameToLevels = new HashMap<String, List<KnownLevel>>();
        private static Map<Integer, List<KnownLevel>> intToLevels = new HashMap<Integer, List<KnownLevel>>();
        // for sub-class
        private Level levelObject;
        // for only level class
        private Level mirroredLevel;
        public KnownLevel(Level level) {
            levelObject = level;
        }
    }
}
