using System.Diagnostics;
using System.Reflection;
using Microsoft.CodeAnalysis.CSharp;
using Microsoft.CodeAnalysis.CSharp.Syntax;
using Training.Workshop.Shared;
using Training.Workshop.Tests.M5.Tooling;

namespace Training.Workshop.Tests.M5.S13Sealed;

/// <summary>
/// Otwarta hierarchia kontra zamknięta: cichy błąd, kontrola wyczerpania (Roslyn zamiast kompilatora)
/// i wyjątek z ramienia <c>_</c> w starym switch.
/// </summary>
public sealed class S13SolutionTest
{
    /// <summary>Wariant dopisany "z zewnątrz" - w osobnym assembly, kompilowanym w pamięci.</summary>
    private const string OutsideChildTicket = """
        using Training.Workshop.Shared;

        namespace Outside;

        public sealed record ChildTicket(Money BasePrice) : Training.Workshop.M5.S13Sealed.{0}.Ticket(BasePrice);
        """;

    /// <summary>
    /// Pułapka otwartej hierarchii: typ dopisany w innym assembly (kompilowany w pamięci, żeby test
    /// kompilował się także po zamknięciu start na żywo) dostaje po cichu 0% zniżki.
    /// Po kroku 1 wykonanym na start ten test zrobi się czerwony - obce assembly nie skompiluje wariantu.
    /// </summary>
    [Fact]
    public void StartSilentlyGivesUnknownTicketNoDiscount()
    {
        var outside = InMemoryAssembly.Compile("Outside", [OutsideChildTicket.Replace("{0}", "Start")]);
        Assert.True(outside.Success, "hierarchia start jest już zamknięta: " + outside.ErrorIds);
        var childType = InMemoryAssembly.Load(outside).GetType("Outside.ChildTicket")!;
        var child = (Training.Workshop.M5.S13Sealed.Start.Ticket)Activator.CreateInstance(childType, Money.Of("25.00"))!;
        // pułapka: bilet dziecięcy (40%) policzony jak normalny
        Assert.Equal(Money.Of("25.00"), new Training.Workshop.M5.S13Sealed.Start.PriceCalculator().Price(child));
    }

    [Fact]
    public void SealedHierarchyListsAllVariants()
    {
        var ticket = typeof(Training.Workshop.M5.S13Sealed.Step1.Ticket);
        var constructor = ticket.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic, [typeof(Money)])!;
        Assert.True(constructor.IsFamilyAndAssembly, "konstruktor private protected zamyka hierarchię w assembly");
        Assert.Equal(
            [typeof(Training.Workshop.M5.S13Sealed.Step1.SeniorTicket), typeof(Training.Workshop.M5.S13Sealed.Step1.StandardTicket), typeof(Training.Workshop.M5.S13Sealed.Step1.StudentTicket)],
            Variants(ticket));
        Assert.All(Variants(ticket), variant => Assert.True(variant.IsSealed));
        var outside = InMemoryAssembly.Compile("Outside", [OutsideChildTicket.Replace("{0}", "Step1")]);
        Assert.False(outside.Success, "obce assembly nie może dopisać wariantu");
        Assert.Contains("CS0122", outside.ErrorIds);
    }

    [Fact]
    public void SolutionHandlesChildTicket()
    {
        Assert.Equal(Money.Of("15.00"),
            new Training.Workshop.M5.S13Sealed.Step3.PriceCalculator().Price(new Training.Workshop.M5.S13Sealed.Step3.ChildTicket(Money.Of("25.00"))));
    }

    /// <summary>
    /// Kontrola wyczerpania w C# nie należy do kompilatora (bez ramienia <c>_</c> jest tylko CS8509
    /// "switch nie obsługuje wszystkich wartości" - zawsze, nawet dla kompletu wariantów). Robi to ten test:
    /// Roslyn czyta switch w PriceCalculator i porównuje obsłużone typy z wariantami hierarchii.
    /// </summary>
    [Fact]
    public void ExhaustivenessCheckFlagsUnhandledVariant()
    {
        Assert.Empty(Unhandled("Step2", typeof(Training.Workshop.M5.S13Sealed.Step2.Ticket)));
        Assert.Empty(Unhandled("Step3", typeof(Training.Workshop.M5.S13Sealed.Step3.Ticket)));
        // kalkulator ze step2 z hierarchią ze step3: "switch nie obsługuje wariantu ChildTicket"
        Assert.Equal(["ChildTicket"], Unhandled("Step2", typeof(Training.Workshop.M5.S13Sealed.Step3.Ticket)));
    }

    /// <summary>
    /// Stary switch z nowym wariantem: PriceCalculator ze step2 (3 warianty) zbudowany razem z hierarchią
    /// ze step3 (4 warianty). Kompiluje się bez błędu (C# nie sprawdza wyczerpania), a ramię <c>_</c>
    /// rzuca UnreachableException - odpowiednik Javowego MatchException.
    /// </summary>
    [Fact]
    public void OldSwitchThrowsUnreachableExceptionForNewVariant()
    {
        var api = InMemoryAssembly.Compile("Api",
            [.. SceneSources.Rewritten("S13Sealed", "Step3", "Api", "PriceCalculator.cs"),
             .. SceneSources.Rewritten("S13Sealed", "Step2", "Api").Where(source => source.Contains("class PriceCalculator"))]);
        Assert.True(api.Success, api.ErrorIds);
        var assembly = InMemoryAssembly.Load(api);
        var calculator = Activator.CreateInstance(assembly.GetType("Api.PriceCalculator")!)!;
        var child = Activator.CreateInstance(assembly.GetType("Api.ChildTicket")!, Money.Of("25.00"))!;
        var call = calculator.GetType().GetMethod("DiscountPercent")!;
        var failure = Assert.Throws<TargetInvocationException>(() => call.Invoke(calculator, [child]));
        Assert.IsType<UnreachableException>(failure.InnerException);
    }

    private static IReadOnlyList<Type> Variants(Type ticket)
    {
        return ticket.Assembly.GetTypes().Where(t => t.BaseType == ticket).OrderBy(t => t.Name, StringComparer.Ordinal).ToList();
    }

    /// <summary>Warianty hierarchii, których nie wymienia żadne ramię switch w PriceCalculator wariantu sceny.</summary>
    private static IReadOnlyList<string> Unhandled(string variant, Type ticket)
    {
        var source = File.ReadAllText(Path.Combine(SceneSources.Variant("S13Sealed", variant), "PriceCalculator.cs"));
        var handled = CSharpSyntaxTree.ParseText(source).GetRoot()
            .DescendantNodes().OfType<SwitchExpressionArmSyntax>()
            .Where(arm => arm.Pattern is not DiscardPatternSyntax)
            .Select(arm => arm.Pattern.DescendantNodesAndSelf().OfType<IdentifierNameSyntax>().First().Identifier.Text)
            .ToHashSet();
        return Variants(ticket).Select(t => t.Name).Where(name => !handled.Contains(name)).ToList();
    }
}
