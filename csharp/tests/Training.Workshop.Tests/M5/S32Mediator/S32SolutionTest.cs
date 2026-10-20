using System.Reflection;
using Training.Workshop.M5.S32Mediator.Step3;

namespace Training.Workshop.Tests.M5.S32Mediator;

/// <summary>Krok 3: komponenty znają tylko mediatora i dają się testować z atrapą mediatora.</summary>
public sealed class S32SolutionTest
{
    private static readonly IReadOnlyList<Type> Components =
        [typeof(SeatMap), typeof(Basket), typeof(PriceSummary), typeof(PayButton)];

    [Fact]
    public void EveryComponentKnowsOnlyTheMediator()
    {
        foreach (var component in Components)
        {
            var collaborators = component
                .GetFields(BindingFlags.Instance | BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.DeclaredOnly)
                .Select(field => field.FieldType)
                .Where(type => type.Namespace?.StartsWith("Training.Workshop.M5.S32Mediator", StringComparison.Ordinal) == true)
                .ToList();
            Assert.True(collaborators.SequenceEqual([typeof(ISelectionMediator)]),
                component.Name + ": " + string.Join(", ", collaborators.Select(type => type.Name)));
        }
    }

    [Fact]
    public void ComponentReportsWhatHappenedWithoutKnowingWhoReacts()
    {
        var mediator = new RecordingMediator();
        var map = new SeatMap(mediator);
        map.Click("A1");
        map.Click("B2");
        map.Click("A1");
        new PriceSummary(mediator).ApplyVoucher("KINO10");
        new PayButton(mediator).Press();
        // sprzedane B2 nie generuje zdarzenia; nieaktywny przycisk nie prosi o płatność
        Assert.Equal(["seatToggled A1 true", "seatToggled A1 false", "voucherApplied"], mediator.Events);
    }

    private sealed class RecordingMediator : ISelectionMediator
    {
        public List<string> Events { get; } = [];

        public void SeatToggled(string seat, bool selected)
        {
            Events.Add("seatToggled " + seat + " " + (selected ? "true" : "false"));
        }

        public void SeatRemoved(string seat)
        {
            Events.Add("seatRemoved " + seat);
        }

        public void BasketCleared()
        {
            Events.Add("basketCleared");
        }

        public void VoucherApplied()
        {
            Events.Add("voucherApplied");
        }

        public string PayRequested()
        {
            Events.Add("payRequested");
            return "ok";
        }
    }
}
