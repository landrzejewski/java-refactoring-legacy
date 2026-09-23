package pl.training.workshop.m6.s11_safecomposite.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 1: zarządzanie dziećmi tylko w węźle; describe() rozszerza opis o dzieci. */
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

    public void add(MenuComponent child) {
        children.add(child);
    }

    public List<MenuComponent> children() {
        return List.copyOf(children);
    }

    @Override
    public String describe() {
        if (children.isEmpty()) {
            return super.describe();
        }
        return super.describe() + " " + children.stream().map(MenuComponent::describe).toList();
    }
}
