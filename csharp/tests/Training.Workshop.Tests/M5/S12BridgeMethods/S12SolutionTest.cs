using System.Reflection;
using Training.Workshop.M5.S12BridgeMethods;
using Training.Workshop.Shared;

namespace Training.Workshop.Tests.M5.S12BridgeMethods;

/// <summary>Metody "mostu" dla roli generycznej: widoczne dla refleksji i dla wywołań przez nieogólny kontrakt.</summary>
public sealed class S12SolutionTest
{
    [Fact]
    public void GenericInterfaceAddsBridgeMethod()
    {
        var type = typeof(Training.Workshop.M5.S12BridgeMethods.Step1.StudentRule);
        var apply = ApplyMethods(type);
        Assert.Equal(2, apply.Count);
        var bridges = type.GetInterfaceMap(typeof(Training.Workshop.M5.S12BridgeMethods.Step1.IPriceRule)).TargetMethods;
        var bridge = Assert.Single(apply, bridges.Contains);
        // nieogólny kontrakt: IPriceRule.Apply(ITicket)
        Assert.Equal(typeof(ITicket), bridge.GetParameters()[0].ParameterType);
    }

    [Fact]
    public void NaiveReflectionWouldRegisterBaseType()
    {
        var seenByNaiveScan = ApplyMethods(typeof(Training.Workshop.M5.S12BridgeMethods.Step1.StudentRule))
            .Select(m => m.GetParameters()[0].ParameterType.Name)
            .Order(StringComparer.Ordinal)
            .ToList();
        Assert.Equal(["ITicket", "StudentTicket"], seenByNaiveScan);
    }

    [Fact]
    public void CallThroughBridgeFailsWithInvalidCastException()
    {
        Training.Workshop.M5.S12BridgeMethods.Step1.IPriceRule raw = new Training.Workshop.M5.S12BridgeMethods.Step1.StudentRule();
        Assert.Throws<InvalidCastException>(() => raw.Apply(new StandardTicket(Money.Of("25.00"))));
    }

    [Fact]
    public void SolutionReportsMissingRuleExplicitly()
    {
        var registry = new Training.Workshop.M5.S12BridgeMethods.Step2.RuleRegistry(new Training.Workshop.M5.S12BridgeMethods.Step2.StandardRule());
        Assert.Throws<InvalidOperationException>(() => registry.Price(new StudentTicket(Money.Of("25.00"), "S-1")));
    }

    [Fact]
    public void SolutionRulesDeclareNoBridge()
    {
        Assert.Single(ApplyMethods(typeof(Training.Workshop.M5.S12BridgeMethods.Step2.StudentRule)));
    }

    private static List<MethodInfo> ApplyMethods(Type type)
    {
        const BindingFlags declared = BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly;
        return type.GetMethods(declared).Where(m => m.Name == "Apply").ToList();
    }
}
