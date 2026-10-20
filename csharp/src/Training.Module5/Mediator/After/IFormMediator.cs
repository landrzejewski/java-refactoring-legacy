namespace Training.Module5.Mediator.After;

/// <summary>
/// Jedyna zależność komponentów: zgłaszają mediatorowi, że coś się w nich zmieniło.
/// </summary>
public interface IFormMediator
{
    void VersionChanged();

    void ApproverChosen();

    void PublishClicked();
}
