using Training.Workshop.Shared;

namespace Training.Workshop.M6.S07Decorator;

/// <summary>
/// Stabilny kontrakt sceny: zamówienie biletu z dodatkami. VIP +10.00, okulary +3.00 dla 3D
/// (chyba że klient ma własne), ubezpieczenie biletu +4.00 (wartość przykładowa).
/// </summary>
public sealed record TicketOrder(
    string Title,
    string Format,
    Money Base,
    bool Vip,
    bool OwnGlasses,
    bool Insurance);
