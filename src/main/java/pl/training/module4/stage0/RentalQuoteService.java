package pl.training.module4.stage0;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalQuoteService {
    private final Map<EquipmentType, BigDecimal> rates = Map.of(
            EquipmentType.DRILL, new BigDecimal("39.99"),
            EquipmentType.GENERATOR, new BigDecimal("120.00"));
    private final BigDecimal disc = new BigDecimal("0.10");

    public String createQuote(RentalRequest r) {
        BigDecimal a = money(rates.get(r.equipmentType())
                .multiply(BigDecimal.valueOf(r.days())));
        BigDecimal d = r.days() >= 7
                ? money(a.multiply(disc))
                : money(BigDecimal.ZERO);
        BigDecimal i = r.insurance()
                ? money(new BigDecimal("8.00")
                        .multiply(BigDecimal.valueOf(r.days())))
                : money(BigDecimal.ZERO);
        BigDecimal f = r.delivery()
                ? new BigDecimal("25.00")
                : money(BigDecimal.ZERO);
        BigDecimal n = money(a.subtract(d).add(i).add(f));
        BigDecimal v = money(n.multiply(new BigDecimal("0.23")));
        BigDecimal t = money(n.add(v));

        String q = "RENTAL QUOTE\n"
                + "Customer: "
                + r.customerName().strip().toUpperCase(Locale.ROOT) + "\n"
                + "Equipment: " + r.equipmentType() + "\n"
                + "Days: " + r.days() + "\n"
                + "Base: " + a.toPlainString() + "\n"
                + "Discount: " + d.toPlainString() + "\n"
                + "Insurance: " + i.toPlainString() + "\n"
                + "Delivery: " + f.toPlainString() + "\n"
                + "Net: " + n.toPlainString() + "\n"
                + "VAT: " + v.toPlainString() + "\n"
                + "Total: " + t.toPlainString() + "\n";
        return q;
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
