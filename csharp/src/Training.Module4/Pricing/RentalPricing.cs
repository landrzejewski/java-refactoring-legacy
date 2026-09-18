using System.Collections.Frozen;
using Training.Module4.Model;

namespace Training.Module4.Pricing;

public sealed class RentalPricing
{
    private const int LongRentalDays = 7;
    private const decimal InsuranceDailyRate = 8.00m;
    private const decimal DeliveryFee = 25.00m;
    private const decimal VatRate = 0.23m;
    private const decimal ZeroMoney = 0.00m;

    private readonly FrozenDictionary<EquipmentType, decimal> _dailyRates;
    private readonly decimal _longRentalDiscountRate;

    public RentalPricing(
        IReadOnlyDictionary<EquipmentType, decimal> dailyRates,
        decimal longRentalDiscountRate)
    {
        ArgumentNullException.ThrowIfNull(dailyRates);

        if (longRentalDiscountRate < 0m || longRentalDiscountRate > 1m)
        {
            throw new ArgumentException(
                "Discount rate must be between zero and one",
                nameof(longRentalDiscountRate));
        }

        var rates = new Dictionary<EquipmentType, decimal>();
        foreach (var type in Enum.GetValues<EquipmentType>())
        {
            if (!dailyRates.TryGetValue(type, out var rate))
            {
                throw new ArgumentException(
                    $"Missing daily rate for {type}",
                    nameof(dailyRates));
            }
            var normalizedRate = Money(rate);
            if (normalizedRate <= 0m)
            {
                throw new ArgumentException(
                    $"Daily rate must be positive for {type}",
                    nameof(dailyRates));
            }
            rates[type] = normalizedRate;
        }

        _dailyRates = rates.ToFrozenDictionary();
        _longRentalDiscountRate = longRentalDiscountRate;
    }

    public static RentalPricing Standard()
    {
        return new RentalPricing(
            new Dictionary<EquipmentType, decimal>
            {
                [EquipmentType.Drill] = 39.99m,
                [EquipmentType.Generator] = 120.00m
            },
            0.10m);
    }

    public PriceBreakdown Calculate(RentalRequest request)
    {
        ArgumentNullException.ThrowIfNull(request);

        var baseRentalCost = CalculateBaseRentalCost(request);
        var discount = CalculateDiscount(request, baseRentalCost);
        var insuranceCost = CalculateInsuranceCost(request);
        var deliveryCost = CalculateDeliveryCost(request);
        var netAmount = Money(baseRentalCost - discount + insuranceCost + deliveryCost);
        var vat = Money(netAmount * VatRate);
        var total = Money(netAmount + vat);

        return new PriceBreakdown(
            baseRentalCost,
            discount,
            insuranceCost,
            deliveryCost,
            netAmount,
            vat,
            total);
    }

    private decimal CalculateBaseRentalCost(RentalRequest request)
    {
        var dailyRate = _dailyRates[request.EquipmentType];
        return Money(dailyRate * request.Days);
    }

    private decimal CalculateDiscount(RentalRequest request, decimal baseRentalCost)
    {
        if (!QualifiesForLongRentalDiscount(request))
        {
            return ZeroMoney;
        }
        return Money(baseRentalCost * _longRentalDiscountRate);
    }

    private static bool QualifiesForLongRentalDiscount(RentalRequest request)
    {
        return request.Days >= LongRentalDays;
    }

    private static decimal CalculateInsuranceCost(RentalRequest request)
    {
        if (!request.Insurance)
        {
            return ZeroMoney;
        }
        return Money(InsuranceDailyRate * request.Days);
    }

    private static decimal CalculateDeliveryCost(RentalRequest request)
    {
        return request.Delivery ? DeliveryFee : ZeroMoney;
    }

    private static decimal Money(decimal amount)
    {
        return Math.Round(amount, 2, MidpointRounding.AwayFromZero);
    }
}
