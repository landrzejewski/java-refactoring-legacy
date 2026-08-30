package pl.training.module4.stage1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalQuoteService {
    private static final int LONG_RENTAL_DAYS = 7;
    private static final BigDecimal INSURANCE_DAILY_RATE =
            new BigDecimal("8.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("25.00");
    private static final BigDecimal VAT_RATE = new BigDecimal("0.23");
    private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");

    private final Map<EquipmentType, BigDecimal> dailyRates = Map.of(
            EquipmentType.DRILL, new BigDecimal("39.99"),
            EquipmentType.GENERATOR, new BigDecimal("120.00"));
    private final BigDecimal longRentalDiscountRate =
            new BigDecimal("0.10");

    public String createQuote(RentalRequest request) {
        BigDecimal dailyRate = dailyRates.get(request.equipmentType());
        BigDecimal rentalDays = BigDecimal.valueOf(request.days());
        BigDecimal baseRentalCost = money(dailyRate.multiply(rentalDays));
        BigDecimal discount = calculateDiscount(request, baseRentalCost);
        BigDecimal insuranceCost = calculateInsuranceCost(request);
        BigDecimal deliveryCost = calculateDeliveryCost(request);
        BigDecimal netAmount = money(baseRentalCost
                .subtract(discount)
                .add(insuranceCost)
                .add(deliveryCost));
        BigDecimal vat = money(netAmount.multiply(VAT_RATE));
        BigDecimal total = money(netAmount.add(vat));

        return buildDocument(
                request,
                baseRentalCost,
                discount,
                insuranceCost,
                deliveryCost,
                netAmount,
                vat,
                total);
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

    private static String buildDocument(
            RentalRequest request,
            BigDecimal baseRentalCost,
            BigDecimal discount,
            BigDecimal insuranceCost,
            BigDecimal deliveryCost,
            BigDecimal netAmount,
            BigDecimal vat,
            BigDecimal total) {
        return String.format(
                Locale.ROOT,
                """
                RENTAL QUOTE
                Customer: %s
                Equipment: %s
                Days: %d
                Base: %s
                Discount: %s
                Insurance: %s
                Delivery: %s
                Net: %s
                VAT: %s
                Total: %s
                """,
                request.customerName().strip().toUpperCase(Locale.ROOT),
                request.equipmentType(),
                request.days(),
                baseRentalCost.toPlainString(),
                discount.toPlainString(),
                insuranceCost.toPlainString(),
                deliveryCost.toPlainString(),
                netAmount.toPlainString(),
                vat.toPlainString(),
                total.toPlainString());
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
