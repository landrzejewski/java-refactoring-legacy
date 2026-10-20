namespace Training.Module5.Mediator.After;

public sealed class PublishButton
{
    private readonly IFormMediator _mediator;
    private bool _enabled;

    internal PublishButton(IFormMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    public void Click()
    {
        if (!_enabled)
        {
            return;
        }
        _mediator.PublishClicked();
    }

    public bool Enabled => _enabled;

    internal void SetEnabled(bool enabled) => _enabled = enabled;
}
