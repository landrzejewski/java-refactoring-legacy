using Training.Module4.Encapsulation;
using Training.Module4.Model;

namespace Training.Module4.Tests.Encapsulation;

public sealed class EquipmentCatalogTest
{
    [Fact]
    public void LegacyCatalogSharesItsMutableMapWithTheCaller()
    {
        var source = Rates();
        var catalog = new LegacyEquipmentCatalog("Summer rental", source);

        source[EquipmentType.Drill] = 1.00m;

        Assert.Equal(1.00m, catalog.DailyRates[EquipmentType.Drill]);
    }

    [Fact]
    public void AccessorBasedCatalogPreservesAliasesDuringControlledMigration()
    {
        var source = Rates();
        var catalog = new AccessorBasedEquipmentCatalog("Summer rental", source);

        source[EquipmentType.Drill] = 1.00m;
        catalog.Name = " ";

        Assert.Same(source, catalog.DailyRates);
        Assert.Equal(1.00m, catalog.DailyRates[EquipmentType.Drill]);
        Assert.Equal(" ", catalog.Name);
    }

    [Fact]
    public void EncapsulatedCatalogOwnsRatesAndReturnsUnmodifiableSnapshots()
    {
        var source = Rates();
        var catalog = new EquipmentCatalog("Summer rental", source);
        var snapshot = catalog.DailyRates();

        source[EquipmentType.Drill] = 1.00m;
        Assert.Equal(39.99m, catalog.DailyRateFor(EquipmentType.Drill));

        catalog.ChangeDailyRate(EquipmentType.Drill, 42.00m);

        Assert.Equal(39.99m, snapshot[EquipmentType.Drill]);
        Assert.Equal(42.00m, catalog.DailyRateFor(EquipmentType.Drill));
        var mutableView = Assert.IsAssignableFrom<IDictionary<EquipmentType, decimal>>(snapshot);
        Assert.Throws<NotSupportedException>(
            () => mutableView.Add(EquipmentType.Generator, 120.00m));
    }

    [Fact]
    public void ChangesNameOnlyThroughValidatedOperation()
    {
        var catalog = new EquipmentCatalog("Summer rental", Rates());

        catalog.RenameTo("Winter rental");

        Assert.Equal("Winter rental", catalog.Name);
        Assert.Throws<ArgumentException>(() => catalog.RenameTo(" "));
    }

    [Fact]
    public void NormalizesRateBeforeCheckingItsInvariant()
    {
        var catalog = new EquipmentCatalog("Summer rental", Rates());

        catalog.ChangeDailyRate(EquipmentType.Drill, 42.005m);

        Assert.Equal(42.01m, catalog.DailyRateFor(EquipmentType.Drill));
        Assert.Throws<ArgumentException>(
            () => catalog.ChangeDailyRate(EquipmentType.Drill, 0.004m));
    }

    private static Dictionary<EquipmentType, decimal> Rates()
    {
        return new Dictionary<EquipmentType, decimal>
        {
            [EquipmentType.Drill] = 39.99m
        };
    }
}
