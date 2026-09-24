using Training.Workshop.M8.S10QualityGate.Sample.Clean;

namespace Training.Workshop.Tests.M8.S10QualityGate.Sample.Broken;

/// <summary>
/// Fikstura dla bramki: "test", który nie przechodzi. Celowo NIE używa xUnit (własny atrybut
/// Test), więc dotnet test i IDE go nie uruchamiają - uruchamia go tylko QualityGate z kroku 4.
/// </summary>
internal sealed class PriceTableTest
{
    [Test]
    private void BasePriceOfImax()
    {
        Check(new PriceTable().BasePrice("IMAX") == 40);
    }

    [Test]
    private void VipSurchargeStartsAtRowNine()
    {
        Check(new PriceTable().VipSurcharge(9) == 10);
    }

    private static void Check(bool condition)
    {
        if (!condition)
        {
            throw new AssertionException("oczekiwanie niespełnione");
        }
    }

    [AttributeUsage(AttributeTargets.Method)]
    private sealed class TestAttribute : Attribute
    {
    }

    private sealed class AssertionException(string message) : Exception(message);
}
