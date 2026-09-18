namespace Training.Patterns.Structural.Decorator;

public abstract class ReaderDecorator : IReader
{
    protected readonly IReader reader;

    public virtual string GetText() => reader.GetText();

    protected ReaderDecorator(IReader reader)
    {
        this.reader = reader;
    }
}
