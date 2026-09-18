namespace Training.Patterns.Creational.Prototype;

public sealed class Window : ICloneable
{
    public int X { get; }

    public int Y { get; }

    public int Width { get; }

    public int Height { get; }

    // Java: Cloneable + super.clone() (shallow field copy) -> MemberwiseClone
    public Window Clone() => (Window)MemberwiseClone();

    object ICloneable.Clone() => Clone();

    public Window(int x, int y, int width, int height)
    {
        X = x;
        Y = y;
        Width = width;
        Height = height;
    }

    public override bool Equals(object? o)
    {
        if (ReferenceEquals(o, this)) return true;
        if (o is not Window other) return false;
        if (X != other.X) return false;
        if (Y != other.Y) return false;
        if (Width != other.Width) return false;
        if (Height != other.Height) return false;
        return true;
    }

    public override int GetHashCode()
    {
        const int PRIME = 59;
        var result = 1;
        result = result * PRIME + X;
        result = result * PRIME + Y;
        result = result * PRIME + Width;
        result = result * PRIME + Height;
        return result;
    }

    public override string ToString() => "Window(x=" + X + ", y=" + Y + ", width=" + Width + ", height=" + Height + ")";
}
