package util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level implements Serializable {

    private static final long serialVersionUID = -8176160795706313070L;

    private final String name;

    private final int value;

    public static final Level OFF = new Level("OFF",1);
    public static final Level INFO = new Level("INFO",1);
    public static final Level DEBUG = new Level("DEBUG",1);
    public static final Level ALL = new Level("ALL",1);

    public Level(String name, int value) {
        this.name = name;
        this.value = value;
        KnownLevel.add(this);
    }

    public Level findByLevel(String name) {
        if(name == null) throw new NullPointerException();

        KnownLevel knownLevel = KnownLevel.findByName(name);

        if(knownLevel != null) {
            return knownLevel.mirroredLevel;
        }

        int value = Integer.parseInt(name);
        knownLevel = KnownLevel.findByValue(value);
        if(knownLevel == null) {
            Level objectLevel = new Level(name,value);
            knownLevel = KnownLevel.findByValue(value);
        }

        return knownLevel.mirroredLevel;
    }

    public int intValue() {
        return value;
    }

    public Level parse(String name) {
        if(name == null) throw new NullPointerException();

        KnownLevel knownLevel = KnownLevel.findByName(name);

        if(knownLevel != null)
            return knownLevel.levelObject;

        int value = Integer.parseInt(name);
        //TODO:
        return knownLevel.levelObject;
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
            if(level instanceof Level) {
                mirroredLevel = level;
            }
            mirroredLevel = new Level(level.name,level.value);
        }

        static synchronized void add(Level l) {

            KnownLevel knownLevel = new KnownLevel(l);

            List<KnownLevel> knownLevelsByName = nameToLevels.get(l.name);
            if(knownLevelsByName == null) {
                List<KnownLevel> newKnownLevelsByName = new ArrayList<KnownLevel>();
                newKnownLevelsByName.add(knownLevel);
                nameToLevels.put(l.name,newKnownLevelsByName);
            }
            knownLevelsByName.add(knownLevel);

            List<KnownLevel> knownLevelsByValue = intToLevels.get(l.value);
            if(knownLevelsByValue == null) {
                List<KnownLevel> newLevelsByValue = new ArrayList<KnownLevel>();
                newLevelsByValue.add(knownLevel);
                intToLevels.put(l.value, newLevelsByValue);
            }
            knownLevelsByValue.add(knownLevel);

        }

        static synchronized KnownLevel findByName(String name) {
            List<KnownLevel> knownLevels = nameToLevels.get(name);
            if(knownLevels != null) return knownLevels.get(0);
            return null;
        }

        static synchronized KnownLevel findByValue(int value) {
            List<KnownLevel> knownLevels = intToLevels.get(value);
            if(knownLevels != null) return knownLevels.get(0);
            return null;
        }

        static synchronized KnownLevel match(Level l) {

            List<KnownLevel> knownLevels = nameToLevels.get(l);

            for(KnownLevel knownLevel : knownLevels) {
                Class<?> type = knownLevel.levelObject.getClass();

                if(knownLevel.levelObject.value == l.value) {
                    if(type == l.getClass())
                        return knownLevel;
                }

            }

            return null;
        }

    }
}
