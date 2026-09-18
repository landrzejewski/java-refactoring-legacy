using Training.Module4.Model;

namespace Training.Module4.Encapsulation;

public sealed class LegacyEquipmentCatalog
{
    public string Name;
    public readonly IDictionary<EquipmentType, decimal> DailyRates;

    public LegacyEquipmentCatalog(
        string name,
        IDictionary<EquipmentType, decimal> dailyRates)
    {
        Name = name;
        DailyRates = dailyRates;
    }
}
