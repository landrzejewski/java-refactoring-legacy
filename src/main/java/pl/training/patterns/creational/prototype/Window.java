package pl.training.patterns.creational.prototype;

public final class Window implements Cloneable {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    @Override
    public Window clone() throws CloneNotSupportedException {
        return (Window) super.clone();
    }

    public Window(final int x, final int y, final int width, final int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Window)) return false;
        final Window other = (Window) o;
        if (this.getX() != other.getX()) return false;
        if (this.getY() != other.getY()) return false;
        if (this.getWidth() != other.getWidth()) return false;
        if (this.getHeight() != other.getHeight()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getX();
        result = result * PRIME + this.getY();
        result = result * PRIME + this.getWidth();
        result = result * PRIME + this.getHeight();
        return result;
    }

    @Override
    public String toString() {
        return "Window(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
    }
}
