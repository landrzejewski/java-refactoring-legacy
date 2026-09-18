using System.Collections.Frozen;
using Training.Module4.Model;

namespace Training.Module4.Encapsulation;

public sealed class EquipmentCatalog
{
    private readonly Dictionary<EquipmentType, decimal> _dailyRates = [];

    public EquipmentCatalog(
        string name,
        IReadOnlyDictionary<EquipmentType, decimal> dailyRates)
    {
        Name = ValidName(name);
        ArgumentNullException.ThrowIfNull(dailyRates);

        foreach (var (type, rate) in dailyRates)
        {
            ChangeDailyRate(type, rate);
        }
    }

    public string Name { get; private set; }

    public void RenameTo(string newName)
    {
        Name = ValidName(newName);
    }

    public decimal DailyRateFor(EquipmentType type)
    {
        if (!_dailyRates.TryGetValue(type, out var rate))
        {
            throw new ArgumentException($"Missing daily rate for {type}", nameof(type));
        }
        return rate;
    }

    public void ChangeDailyRate(EquipmentType type, decimal newRate)
    {
        var normalizedRate = Math.Round(newRate, 2, MidpointRounding.AwayFromZero);
        if (normalizedRate <= 0m)
        {
            throw new ArgumentException("Daily rate must be positive", nameof(newRate));
        }
        _dailyRates[type] = normalizedRate;
    }

    public IReadOnlyDictionary<EquipmentType, decimal> DailyRates()
    {
        return _dailyRates.ToFrozenDictionary();
    }

    private static string ValidName(string value)
    {
        ArgumentNullException.ThrowIfNull(value, "name");
        if (string.IsNullOrWhiteSpace(value))
        {
            throw new ArgumentException("Catalog name must not be blank", "name");
        }
        return value;
    }
}
