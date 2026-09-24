using Training.Workshop.Shared;

namespace Training.Workshop.M7.S02MethodObject;

/// <summary>Stabilny kontrakt sceny: wycena zamówienia.</summary>
public sealed record Quote(Money Tickets, Money Fees, Money Total, int LoyaltyPoints);
