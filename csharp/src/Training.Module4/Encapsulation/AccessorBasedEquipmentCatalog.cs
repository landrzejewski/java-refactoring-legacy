using Training.Module4.Model;

namespace Training.Module4.Encapsulation;

public sealed class AccessorBasedEquipmentCatalog
{
    public AccessorBasedEquipmentCatalog(
        string name,
        IDictionary<EquipmentType, decimal> dailyRates)
    {
        Name = name;
        DailyRates = dailyRates;
    }

    public string Name { get; set; }

    public IDictionary<EquipmentType, decimal> DailyRates { get; }
}
