using Training.Workshop.Shared;

namespace Training.Workshop.M5.S29BoundaryObject.Step3;

/// <summary>
/// Krok 3: Adapter implementujący port domeny. Łączy klienta terminala z mapperem;
/// poza tą klasą nikt nie widzi groszy ani tekstu "OK;AUTH;4500".
/// </summary>
public sealed class LegacyTerminalAdapter : IPaymentTerminal
{
    private readonly LegacyTerminalClient _client;
    private readonly TerminalResponseMapper _mapper = new();

    public LegacyTerminalAdapter(LegacyTerminalClient client)
    {
        ArgumentNullException.ThrowIfNull(client);
        _client = client;
    }

    public PaymentConfirmation Authorize(string bookingId, Money amount)
    {
        return _mapper.ToConfirmation(_client.Authorize(bookingId, _mapper.ToGrosze(amount)));
    }
}
