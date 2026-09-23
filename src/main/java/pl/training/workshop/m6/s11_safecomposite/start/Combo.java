package pl.training.workshop.m6.s11_safecomposite.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/** Start: zestaw nadpisuje add() i children(). */
public final class Combo extends MenuComponent {
    private final String name;
    private final List<MenuComponent> children = new ArrayList<>();

    public Combo(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return name;
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
    public void add(MenuComponent child) {
        children.add(child);
    }

    @Override
    public List<MenuComponent> children() {
        return List.copyOf(children);
    }
}
