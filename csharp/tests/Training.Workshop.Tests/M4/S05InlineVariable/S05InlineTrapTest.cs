using System.Globalization;
using Training.Workshop.M4.S05InlineVariable;

namespace Training.Workshop.Tests.M4.S05InlineVariable;

/// <summary>
/// Dokumentuje pułapkę: tak wyglądałby Start po naiwnym Inline Variable dla number, issuedAt i price.
/// Kod się kompiluje, IDE nie protestuje - zmienia się liczba i moment ewaluacji oraz przeciążenie.
/// </summary>
public sealed class S05InlineTrapTest
{
    private readonly Ticket _ticket = new NaivelyInlinedIssuer(new TickingClock(S05EquivalenceTest.T0)).Issue("D1", 3);

    [Fact]
    public void InliningASideEffectConsumesTwoNumbers()
    {
        Assert.Equal("D1-1", _ticket.Code);
        // etykieta ma inny numer niż kod
        Assert.Equal("Bilet D1-2, cena 0.40, oplata 2.00", _ticket.Label);
    }

    [Fact]
    public void InliningAClockReadGivesTwoDifferentInstants()
    {
        // rezerwacja trzyma sekundę za długo
        Assert.Equal(TimeSpan.FromMinutes(15) + TimeSpan.FromSeconds(1), _ticket.HoldUntil - _ticket.IssuedAt);
    }

    [Fact]
    public void InliningADoubleVariablePicksTheIntOverload()
    {
        var label = _ticket.Label;
        // 40 zł potraktowane jak 40 groszy
        Assert.Equal("0.40", label[(label.IndexOf("cena ", StringComparison.Ordinal) + 5)..label.IndexOf(", oplata", StringComparison.Ordinal)]);
    }

    /// <summary>Kopia Start po trzech naiwnych Inline Variable.</summary>
    private sealed class NaivelyInlinedIssuer(TimeProvider clock)
    {
        private int _lastNumber;

        public Ticket Issue(string screeningCode, int format)
        {
            return new Ticket(screeningCode + "-" + NextNumber(),
                "Bilet " + screeningCode + "-" + NextNumber() + ", cena " + Money(BasePrice(format))
                    + ", oplata " + Money(200),
                clock.GetUtcNow(), clock.GetUtcNow() + TimeSpan.FromMinutes(15));
        }

        private int NextNumber()
        {
            _lastNumber++;
            return _lastNumber;
        }

        private static int BasePrice(int format)
        {
            return format == 3 ? 40 : format == 2 ? 32 : 25;
        }

        private static string Money(double zloty)
        {
            return zloty.ToString("F2", CultureInfo.InvariantCulture);
        }

        private static string Money(int grosze)
        {
            return string.Create(CultureInfo.InvariantCulture, $"{grosze / 100}.{grosze % 100:D2}");
        }
    }
}
