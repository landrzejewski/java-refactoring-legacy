using Training.Workshop.Shared;

namespace Training.Workshop.M8.S07Adr.Step2.Pricing;

/// <summary>Krok 2: kwota w Money (R2).</summary>
public sealed record Quote(Money Total, bool GroupDiscount);
