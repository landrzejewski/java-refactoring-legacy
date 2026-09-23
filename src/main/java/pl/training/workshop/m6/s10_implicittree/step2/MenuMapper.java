package pl.training.workshop.m6.s10_implicittree.step2;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: mapper starego formatu (zagnieżdżone listy) na Composite. Zachowuje komunikaty
 * błędów starego kodu - to też jest obserwowalne zachowanie.
 */
public final class MenuMapper {
    private MenuMapper() {
    }

    public static Combo fromNested(List<?> combo) {
        if (combo.isEmpty() || !(combo.getFirst() instanceof String name)) {
            throw new IllegalArgumentException("combo needs a name");
        }
        List<MenuItem> items = new ArrayList<>();
        for (Object element : combo.subList(1, combo.size())) {
            items.add(switch (element) {
                case String product -> product(product);
                case List<?> nested -> fromNested(nested);
                default -> throw new IllegalArgumentException("unsupported element: " + element);
            });
        }
        return new Combo(name, items);
    }

    private static Product product(String text) {
        if (!text.contains("=")) {
            throw new IllegalArgumentException("product needs a price: " + text);
        }
        int separator = text.indexOf('=');
        return new Product(text.substring(0, separator), Money.of(text.substring(separator + 1)));
    }
}
