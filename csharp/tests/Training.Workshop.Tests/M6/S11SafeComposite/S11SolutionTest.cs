using System.Reflection;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M6.S11SafeComposite;

/// <summary>Transparent kontra Safe: gdzie wychodzi błąd Add() na liściu.</summary>
public sealed class S11SolutionTest
{
    private const string Scene = "Training.Workshop.M6.S11SafeComposite.";

    /// <summary>
    /// Dokumentuje pułapkę Start; po "warsztat.sh jump" (Start = krok 1+) test kończy się
    /// bez sprawdzeń (xUnit 2 nie ma odpowiednika assumeTrue).
    /// </summary>
    [Fact]
    public void TransparentCompositeFailsAtRuntime()
    {
        var component = TypeOf(Scene + "Start.MenuComponent");
        var product = TypeOf(Scene + "Start.Product");
        var add = product.GetMethod("Add", [component]);
        if (add == null)
        {
            return; // Start nie jest już Transparent Composite
        }
        var nachos = Activator.CreateInstance(product, "Nachos", "14.00");
        var sauce = Activator.CreateInstance(product, "Sos", "3.00");
        var error = Assert.Throws<TargetInvocationException>(() => add.Invoke(nachos, [sauce]));
        Assert.Equal(typeof(NotSupportedException), error.InnerException!.GetType());
        Assert.Equal("cannot add to Nachos", error.InnerException.Message);
    }

    [Fact]
    public void SafeCompositeHasNoAddOnLeafOrCommonType()
    {
        var component = TypeOf(Scene + "Step1.MenuComponent");
        Assert.Null(TypeOf(Scene + "Step1.Product").GetMethod("Add", [component]));
        Assert.Null(component.GetProperty("Children"));
    }

    [Fact]
    public void ImmutableCompositeHasNoAddAtAll()
    {
        var combo = Training.Workshop.M6.S11SafeComposite.Step2.Combo.Of("Zestaw");
        var children = (ICollection<Training.Workshop.M6.S11SafeComposite.Step2.MenuComponent>)combo.Children;
        Assert.Throws<NotSupportedException>(() => children.Add(null!));
    }

    private static Type TypeOf(string name) =>
        typeof(Money).Assembly.GetType(name) ?? throw new TypeLoadException(name);
}
