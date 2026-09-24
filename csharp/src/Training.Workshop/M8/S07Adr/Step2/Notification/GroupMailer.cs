namespace Training.Workshop.M8.S07Adr.Step2.Notification;

/// <summary>Krok 2 (bez zmian): powiadomienia - wołane tylko przez warstwę aplikacji.</summary>
public sealed class GroupMailer
{
    private readonly List<string> _sent = [];

    public void GroupDiscountGranted(string organizer, int tickets)
    {
        _sent.Add(organizer + ": rabat grupowy dla " + tickets + " biletow");
    }

    public IReadOnlyList<string> Sent()
    {
        return _sent.ToList();
    }
}
