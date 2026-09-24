using Training.Workshop.M5.S06ExtractInterface.Step3;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S06ExtractInterface;

/// <summary>Rola klienta zamiast wielu przeciążeń; domyślna metoda interfejsu rozszerza kontrakt bez łamania implementacji.</summary>
public sealed class S06SolutionTest
{
    /// <summary>Nowa implementacja spoza pierwotnej hierarchii: okulary 3D (+3.00, VAT 23%).</summary>
    private sealed record Glasses3D : IPriceable
    {
        public Money Price => Money.Of("3.00");

        public int VatPercent => 23;
    }

    [Fact]
    public void BeforeMigrationCartHasOneAddPerConcreteType()
    {
        Assert.Equal(2, typeof(Training.Workshop.M5.S06ExtractInterface.Step1.Cart).GetMethods().Count(m => m.Name == "Add"));
    }

    [Fact]
    public void SolutionCartDependsOnlyOnTheRole()
    {
        Assert.Equal(typeof(void), typeof(Cart).GetMethod("Add", [typeof(IPriceable)])!.ReturnType);
        var vatAmount = typeof(IPriceable).GetMethod("VatAmount")!;
        Assert.False(vatAmount.IsAbstract, "domyślna metoda interfejsu ma ciało");
    }

    [Fact]
    public void NewImplementationGetsDefaultMethodForFree()
    {
        // w C# domyślna metoda interfejsu jest widoczna tylko przez typ interfejsu
        IPriceable glasses = new Glasses3D();
        Assert.Equal(Money.Of("0.56"), glasses.VatAmount());
        var cart = new Cart();
        cart.Add(new Ticket("Kraina Lodu", "B4", Money.Of("32.00")));
        cart.Add(new Glasses3D());
        Assert.Equal("Razem: 35.00, VAT: 2.93", cart.Summary());
    }
}
