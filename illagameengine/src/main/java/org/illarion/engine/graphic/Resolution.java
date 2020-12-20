package org.illarion.engine.graphic;

public class Resolution {
    /**
     * The screen height of this resolution.
     */
    public final int height;

    /**
     * The screen width of that resolution.
     */
    public final int width;

    public Resolution(int height, int width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolution that = (Resolution) o;

        if (height != that.height) return false;
        return width == that.width;
    }

    @Override
    public int hashCode() {
        int result = height;
        result = 31 * result + width;
        return result;
    }
}
