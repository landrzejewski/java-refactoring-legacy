namespace Training.Workshop.M7.S02MethodObject;

/// <summary>Stabilny kontrakt sceny: zamówienie (także grupowe) na jeden seans.</summary>
/// <param name="Format">2D, 3D albo IMAX</param>
/// <param name="Start">godzina seansu</param>
/// <param name="TicketTypes">NORMAL, STUDENT, SENIOR albo CHILD - jeden wpis na bilet</param>
/// <param name="VipSeats">ile z tych biletów to miejsca VIP</param>
/// <param name="OwnGlasses">własne okulary 3D</param>
/// <param name="Online">zamówienie przez internet</param>
public sealed record GroupOrder(string Format, TimeOnly Start, IReadOnlyList<string> TicketTypes,
    int VipSeats, bool OwnGlasses, bool Online);
