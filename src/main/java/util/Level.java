package util;

import java.io.Serializable;

public class Level implements Serializable {

    private static final long serialVersionUID = -8176160795706313070L;

    private final String name;

    private final int value;

    public Level(String name, int value) {
        this.name = name;
        this.value = value;
    }



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



}
