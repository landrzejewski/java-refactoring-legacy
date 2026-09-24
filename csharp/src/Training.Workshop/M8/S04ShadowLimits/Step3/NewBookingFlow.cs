namespace Training.Workshop.M8.S04ShadowLimits.Step3;

/// <summary>
/// Krok 3: nowa ścieżka = plan (czysty) + wykonanie planu na porcie efektów.
/// Tylko ta klasa wykonuje efekty i używa jej wyłącznie ścieżka autorytatywna.
/// </summary>
public sealed class NewBookingFlow
{
    private readonly BookingPlanner _planner = new();
    private readonly IEffects _effects;

    public NewBookingFlow(IEffects effects)
    {
        _effects = effects;
    }

    public string Book(BookingRequest request)
    {
        BookingPlan plan = _planner.Plan(request);
        foreach (BookingPlan.IEffect effect in plan.Effects)
        {
            effect.ApplyTo(_effects);
        }
        return plan.Result;
    }
}
