package pl.training.workshop.m6.s11_safecomposite.step1;

/** Krok 1: tam, gdzie klient dodaje dzieci, zmienna ma typ Combo - kompilator pilnuje reszty. */
public final class ComboCatalog {
    public MenuComponent find(String code) {
        return switch (code) {
            case "family" -> family();
            case "duo" -> duo();
            case "nachos" -> new Product("Nachos", "14.00");
            default -> throw new IllegalArgumentException("unknown combo: " + code);
        };
    }

    private MenuComponent family() {
        Combo combo = new Combo("Zestaw Rodzinny");
        combo.add(new Product("Popcorn XL", "24.00"));
        Combo drinks = new Combo("Napoje");
        drinks.add(new Product("Cola", "9.00"));
        drinks.add(new Product("Cola", "9.00"));
        drinks.add(new Product("Woda", "7.00"));
        combo.add(drinks);
        return combo;
    }

    private MenuComponent duo() {
        Combo combo = new Combo("Zestaw Duo");
        combo.add(new Product("Popcorn L", "18.00"));
        combo.add(new Product("Cola", "9.00"));
        combo.add(new Product("Cola", "9.00"));
        return combo;
    }
}
