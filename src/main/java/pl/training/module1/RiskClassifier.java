package pl.training.module1;

import java.math.BigDecimal;
import java.util.List;

public final class RiskClassifier {
    private RiskClassifier() {
    }

    public static int riskLevel(OrderSummary order) {
        int score = 0;

        if (order.total().compareTo(new BigDecimal("1000.00")) > 0) {
            score++;
        }

        if (order.international()) {
            score++;
        }

        for (Item item : order.items()) {
            if (item.fragile()) {
                score++;
            }
        }

        return score;
    }
}

record OrderSummary(
        BigDecimal total,
        boolean international,
        List<Item> items) {
}

record Item(boolean fragile) {
}
