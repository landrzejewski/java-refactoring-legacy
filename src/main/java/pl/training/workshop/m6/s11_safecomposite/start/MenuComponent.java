package pl.training.workshop.m6.s11_safecomposite.start;

import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Start: Transparent Composite - add() i children() we wspólnym typie. Klient może wywołać
 * add() na liściu; kompilator milczy, błąd wychodzi dopiero w runtime.
 */
public abstract class MenuComponent {
    public abstract String name();

    public abstract Money price();

    public void add(MenuComponent child) {
        throw new UnsupportedOperationException("cannot add to " + name());
    }

    public List<MenuComponent> children() {
        return List.of();
    }

    public String describe() {
        String text = name() + " " + price();
        if (children().isEmpty()) {
            return text;
        }
        return text + " " + children().stream().map(MenuComponent::describe).toList();
    }
}
