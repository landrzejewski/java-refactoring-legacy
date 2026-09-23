package pl.training.workshop.m6.s10_implicittree.step2;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 2: price przeniesione na Composite - jedna operacja naraz; render jeszcze stary. */
public final class BarMenu {
    public Money price(List<?> combo) {
        return MenuMapper.fromNested(combo).price();
    }

    public String render(List<?> combo) {
        StringBuilder text = new StringBuilder();
        render(combo, 0, text);
        return text.toString();
    }

    private void render(List<?> combo, int depth, StringBuilder text) {
        text.append("  ".repeat(depth)).append(combo.get(0))
                .append(' ').append(price(combo)).append('\n');
        for (Object element : combo.subList(1, combo.size())) {
            if (element instanceof String product) {
                text.append("  ".repeat(depth + 1)).append(product, 0, product.indexOf('='))
                        .append(' ').append(productPrice(product)).append('\n');
            } else if (element instanceof List<?> nested) {
                render(nested, depth + 1, text);
            } else {
                throw new IllegalArgumentException("unsupported element: " + element);
            }
        }
    }

    private static Money productPrice(String product) {
        if (!product.contains("=")) {
            throw new IllegalArgumentException("product needs a price: " + product);
        }
        return Money.of(product.substring(product.indexOf('=') + 1));
    }
}
