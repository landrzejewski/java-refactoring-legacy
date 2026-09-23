package pl.training.workshop.m6.s11_safecomposite.step2;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 2: węzeł z dziećmi podanymi przy tworzeniu - add() nie istnieje nigdzie. */
public record Combo(String name, List<MenuComponent> children) implements MenuComponent {
    public Combo {
        children = List.copyOf(children);
    }

    public static Combo of(String name, MenuComponent... children) {
        return new Combo(name, List.of(children));
    }

    @Override
    public Money price() {
        Money total = Money.ZERO;
        for (MenuComponent child : children) {
            total = total.plus(child.price());
        }
        return total;
    }

    @Override
    public String describe() {
        String text = name + " " + price();
        if (children.isEmpty()) {
            return text;
        }
        return text + " " + children.stream().map(MenuComponent::describe).toList();
    }
}
