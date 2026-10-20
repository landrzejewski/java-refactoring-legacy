namespace Training.Module4.Structural.Decorator;

public class LowerCaseReaderDecorator : ReaderDecorator
{
    public LowerCaseReaderDecorator(IReader reader) : base(reader)
    {
    }

    public override string GetText() => base.GetText().ToLowerInvariant();
}
