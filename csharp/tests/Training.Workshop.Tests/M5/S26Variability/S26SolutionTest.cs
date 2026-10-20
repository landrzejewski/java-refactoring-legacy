using System.Reflection;
using System.Runtime.CompilerServices;
using Training.Workshop.M5.S26Variability;
using Training.Workshop.M5.S26Variability.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S26Variability;

/// <summary>Trzy mechanizmy istnieją i każdy obsługuje jedną oś zmienności - osie się nie mieszają.</summary>
public sealed class S26SolutionTest
{
    private static readonly IReadOnlyList<Type> Commands =
        [typeof(BookCommand), typeof(PayCommand), typeof(UseCommand), typeof(CancelCommand)];

    [Fact]
    public void TicketPriceIsAStatelessStrategyThatKnowsOnlyMoney()
    {
        // delegat - odpowiednik @FunctionalInterface z Javy
        Assert.True(typeof(TicketPricing).IsSubclassOf(typeof(Delegate)));
        var price = typeof(TicketPricing).GetMethod("Invoke")!;
        Assert.Equal(typeof(Money), price.ReturnType);
        Assert.Equal([typeof(Money)], price.GetParameters().Select(parameter => parameter.ParameterType));
        Assert.Same(TicketPricings.ForType("S"), TicketPricings.ForType("S"));
        Assert.Equal(Money.Of("30.00"), TicketPricings.ForType("S").Invoke(Money.Of("40.00")));
    }

    [Fact]
    public void EveryOperatorOperationIsACommandWithUndo()
    {
        foreach (var command in Commands)
        {
            Assert.True(typeof(IDeskCommand).IsAssignableFrom(command), command.Name);
        }
        Assert.NotNull(typeof(IDeskCommand).GetMethod("Undo", [typeof(BookingLedger)]));
        Assert.NotNull(typeof(IDeskCommand).GetMethod("JournalEntry", Type.EmptyTypes));
    }

    [Fact]
    public void LifecycleDecidesWhatCancelMeans()
    {
        var unpaid = new Booking(Money.Of("40.00"));
        Assert.Equal(Money.Zero, unpaid.Cancel());

        var paid = new Booking(Money.Of("40.00"));
        Assert.Equal(Money.Of("40.00"), paid.Pay());
        Assert.Equal(Money.Of("40.00"), paid.Cancel()); // w PAID anulowanie oddaje pieniądze
        Assert.Equal(Status.Cancelled, paid.Status);
        var error = Assert.Throws<InvalidOperationException>(paid.Use);
        Assert.Equal("cannot use in CANCELLED", error.Message);
    }

    /// <summary>Java: prywatny enum ze stałymi o własnych metodach; C#: prywatna klasa abstrakcyjna stanu.</summary>
    [Fact]
    public void StateLivesInsideBookingAsPrivateType()
    {
        var nested = typeof(Booking).GetNestedTypes(BindingFlags.NonPublic);
        Assert.True(nested.Any(type => type.IsNestedPrivate && type.IsAbstract
                && type.GetProperty("Status")?.PropertyType == typeof(Status)),
            "stany jako prywatny typ rezerwacji");
    }

    [Fact]
    public void AxesAreNotMixed()
    {
        Assert.False(FieldTypes(typeof(Booking)).Contains(typeof(TicketPricing)), "stan nie zna cennika");
        Assert.False(FieldTypes(typeof(Booking)).Contains(typeof(string)), "stan nie zna nazw operacji");
        foreach (var command in Commands)
        {
            var types = FieldTypes(command);
            Assert.False(types.Contains(typeof(TicketPricing)), command.Name + " nie trzyma strategii");
            Assert.False(types.Contains(typeof(Booking)), command.Name + " nie sięga do stanu rezerwacji");
        }
        var desk = FieldTypes(typeof(BoxOfficeDesk));
        Assert.False(desk.Contains(typeof(Money)) || desk.Contains(typeof(Status)) || desk.Contains(typeof(Booking)),
            "pulpit zna tylko polecenia i odbiorcę: " + string.Join(", ", desk.Select(type => type.Name)));
    }

    /// <summary>Typy pól zadeklarowanych w klasie (bez pól generowanych przez kompilator; Nullable rozpakowany).</summary>
    private static HashSet<Type> FieldTypes(Type type)
    {
        return type.GetFields(BindingFlags.Instance | BindingFlags.Static | BindingFlags.Public
                | BindingFlags.NonPublic | BindingFlags.DeclaredOnly)
            .Where(field => !field.IsDefined(typeof(CompilerGeneratedAttribute)))
            .Select(field => Nullable.GetUnderlyingType(field.FieldType) ?? field.FieldType)
            .ToHashSet();
    }
}
