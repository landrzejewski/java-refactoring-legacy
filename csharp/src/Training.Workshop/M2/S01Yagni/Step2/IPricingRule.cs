namespace Training.Workshop.M2.S01Yagni.Step2;

/// <summary>Krok 2: kontrakt na typowanych danych - bez rzutowań z IDictionary&lt;string, object&gt;.</summary>
public interface IPricingRule
{
    bool AppliesTo(TicketQuote quote);

    decimal Apply(decimal price);
}
