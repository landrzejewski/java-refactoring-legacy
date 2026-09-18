namespace Training.Patterns.Structural.Decorator;

public class LowerCaseReaderDecorator : ReaderDecorator
{
    public LowerCaseReaderDecorator(IReader reader) : base(reader)
    {
    }

    public override string GetText() => base.GetText().ToLowerInvariant();
}
