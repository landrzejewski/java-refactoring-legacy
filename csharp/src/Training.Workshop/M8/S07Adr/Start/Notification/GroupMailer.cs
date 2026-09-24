namespace Training.Workshop.M8.S07Adr.Start.Notification;

/// <summary>Start: powiadomienia o rabacie grupowym (poza cennikiem - ale cennik je woła).</summary>
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
