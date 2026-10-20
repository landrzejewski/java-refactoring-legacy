namespace Training.Module4.Behavioral.Mediator;

/// <summary>
/// Mediator: jedyny obiekt, który zna wszystkich uczestników. Uczestnicy
/// komunikują się wyłącznie przez niego, więc nie mają referencji do siebie nawzajem.
/// </summary>
public interface IChatMediator
{
    void Join(Participant participant);

    void Broadcast(Participant sender, string text);
}
