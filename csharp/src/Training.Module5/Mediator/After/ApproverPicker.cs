namespace Training.Module5.Mediator.After;

public sealed class ApproverPicker
{
    private readonly IFormMediator _mediator;
    private string _selection = "";
    private bool _enabled;

    internal ApproverPicker(IFormMediator mediator)
    {
        ArgumentNullException.ThrowIfNull(mediator);
        _mediator = mediator;
    }

    public void Choose(string approver)
    {
        if (!_enabled)
        {
            return;
        }
        _selection = approver;
        _mediator.ApproverChosen();
    }

    public string Selection => _selection;

    public bool Enabled => _enabled;

    internal bool HasSelection() => !string.IsNullOrWhiteSpace(_selection);

    internal void SetEnabled(bool enabled) => _enabled = enabled;

    internal void Clear() => _selection = "";
}
