namespace Training.Workshop.M6.S01Strategy.Step3;

/// <summary>
/// Krok 3: wybór strategii przeniesiony do korzenia kompozycji (konfiguracja kina).
/// Strategie są bezstanowe, więc współdzielimy jedne instancje.
/// </summary>
public static class DiscountPrograms
{
    private static readonly IDiscountPolicy Standard = new StandardDiscount();
    private static readonly IDiscountPolicy StudentWeek = new StudentWeekDiscount(Standard);
    private static readonly IDiscountPolicy Premiere = new PremiereDiscount();

    public static IDiscountPolicy ForName(string? program)
    {
        if (program == null)
        {
            throw new ArgumentException("program must not be null");
        }
        return program switch
        {
            "STANDARD" => Standard,
            "STUDENT_WEEK" => StudentWeek,
            "PREMIERE" => Premiere,
            _ => throw new ArgumentException("unknown program: " + program),
        };
    }
}
