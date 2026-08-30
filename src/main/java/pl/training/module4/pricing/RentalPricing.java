package pl.training.module4.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalPricing {
    private static final int LONG_RENTAL_DAYS = 7;
    private static final BigDecimal INSURANCE_DAILY_RATE =
            new BigDecimal("8.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("25.00");
    private static final BigDecimal VAT_RATE = new BigDecimal("0.23");
    private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");

    private final Map<EquipmentType, BigDecimal> dailyRates;
    private final BigDecimal longRentalDiscountRate;

    public RentalPricing(
            Map<EquipmentType, BigDecimal> dailyRates,
            BigDecimal longRentalDiscountRate) {
        Objects.requireNonNull(dailyRates, "dailyRates");
        Objects.requireNonNull(
                longRentalDiscountRate,
                "longRentalDiscountRate");

        if (longRentalDiscountRate.signum() < 0
                || longRentalDiscountRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Discount rate must be between zero and one");
        }

        EnumMap<EquipmentType, BigDecimal> rates =
                new EnumMap<>(EquipmentType.class);
        for (EquipmentType type : EquipmentType.values()) {
            BigDecimal rate = dailyRates.get(type);
            if (rate == null) {
                throw new IllegalArgumentException(
                        "Missing daily rate for " + type);
            }
            BigDecimal normalizedRate = money(rate);
            if (normalizedRate.signum() <= 0) {
                throw new IllegalArgumentException(
                        "Daily rate must be positive for " + type);
            }
            rates.put(type, normalizedRate);
        }

        this.dailyRates = Map.copyOf(rates);
        this.longRentalDiscountRate = longRentalDiscountRate;
    }

    public static RentalPricing standard() {
        return new RentalPricing(
                Map.of(
                        EquipmentType.DRILL, new BigDecimal("39.99"),
                        EquipmentType.GENERATOR, new BigDecimal("120.00")),
                new BigDecimal("0.10"));
    }

    public PriceBreakdown calculate(RentalRequest request) {
        Objects.requireNonNull(request, "request");

        BigDecimal baseRentalCost = calculateBaseRentalCost(request);
        BigDecimal discount = calculateDiscount(request, baseRentalCost);
        BigDecimal insuranceCost = calculateInsuranceCost(request);
        BigDecimal deliveryCost = calculateDeliveryCost(request);
        BigDecimal netAmount = money(baseRentalCost
                .subtract(discount)
                .add(insuranceCost)
                .add(deliveryCost));
        BigDecimal vat = money(netAmount.multiply(VAT_RATE));
        BigDecimal total = money(netAmount.add(vat));

        return new PriceBreakdown(
                baseRentalCost,
                discount,
                insuranceCost,
                deliveryCost,
                netAmount,
                vat,
                total);
    }

    private BigDecimal calculateBaseRentalCost(RentalRequest request) {
        BigDecimal dailyRate = dailyRates.get(request.equipmentType());
        return money(dailyRate.multiply(BigDecimal.valueOf(request.days())));
    }

    private BigDecimal calculateDiscount(
            RentalRequest request,
            BigDecimal baseRentalCost) {
        if (!qualifiesForLongRentalDiscount(request)) {
            return ZERO_MONEY;
        }
        return money(baseRentalCost.multiply(longRentalDiscountRate));
    }

    private static boolean qualifiesForLongRentalDiscount(
            RentalRequest request) {
        return request.days() >= LONG_RENTAL_DAYS;
    }

    private static BigDecimal calculateInsuranceCost(RentalRequest request) {
        if (!request.insurance()) {
            return ZERO_MONEY;
        }
        return money(INSURANCE_DAILY_RATE.multiply(
                BigDecimal.valueOf(request.days())));
    }

    private static BigDecimal calculateDeliveryCost(RentalRequest request) {
        return request.delivery() ? DELIVERY_FEE : ZERO_MONEY;
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
