namespace Training.Module5.Mediator.After;

public sealed class VersionField
{
    private readonly IFormMediator _mediator;
    private string _text = "";
    private bool _editable = true;

    internal VersionField(IFormMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    public void Type(string value)
    {
        if (!_editable)
        {
            return;
        }
        _text = value;
        _mediator.VersionChanged();
    }

    public string Text => _text;

    public bool Editable => _editable;

    internal void Lock() => _editable = false;
}
