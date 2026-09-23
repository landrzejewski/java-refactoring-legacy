package pl.training.workshop.m6.s11_safecomposite.step2;

/** Krok 2: drzewo zapisane deklaratywnie - kształt kodu to kształt zestawu. */
public final class ComboCatalog {
    public MenuComponent find(String code) {
        return switch (code) {
            case "family" -> Combo.of("Zestaw Rodzinny",
                    new Product("Popcorn XL", "24.00"),
                    Combo.of("Napoje",
                            new Product("Cola", "9.00"),
                            new Product("Cola", "9.00"),
                            new Product("Woda", "7.00")));
            case "duo" -> Combo.of("Zestaw Duo",
                    new Product("Popcorn L", "18.00"),
                    new Product("Cola", "9.00"),
                    new Product("Cola", "9.00"));
            case "nachos" -> new Product("Nachos", "14.00");
            default -> throw new IllegalArgumentException("unknown combo: " + code);
        };
    }
}
