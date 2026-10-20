using Training.Workshop.Shared;

namespace Training.Workshop.M2.S02Adr.Step2.Pricing;

/// <summary>Krok 2: kwota w Money (R2).</summary>
public sealed record Quote(Money Total, bool GroupDiscount);
