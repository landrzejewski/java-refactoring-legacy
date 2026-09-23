package pl.training.workshop.m6.s10_implicittree.step3;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 3: węzeł - zestaw, którego cena to suma elementów. Niemutowalny. */
public record Combo(String name, List<MenuItem> items) implements MenuItem {
    public Combo {
        items = List.copyOf(items);
    }

    @Override
    public Money price() {
        Money total = Money.ZERO;
        for (MenuItem item : items) {
            total = total.plus(item.price());
        }
        return total;
    }

    @Override
    public void render(int depth, StringBuilder text) {
        text.append("  ".repeat(depth)).append(name).append(' ').append(price()).append('\n');
        items.forEach(item -> item.render(depth + 1, text));
    }
}
