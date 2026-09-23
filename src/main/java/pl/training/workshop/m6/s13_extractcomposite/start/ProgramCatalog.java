package pl.training.workshop.m6.s13_extractcomposite.start;

/** Start: klient budujący programy wydarzeń. */
public final class ProgramCatalog {
    public ProgramItem find(String code) {
        return switch (code) {
            case "marathon" -> {
                Marathon marathon = new Marathon("Diuna");
                marathon.add(new Film("Diuna", 155));
                marathon.add(new Film("Diuna: Czesc druga", 166));
                yield marathon;
            }
            case "shorts" -> shorts();
            case "night" -> {
                Marathon night = new Marathon("Noc kina");
                night.add(shorts());
                night.add(new Film("Amator", 120));
                yield night;
            }
            case "empty" -> new Marathon("Pusty");
            default -> throw new IllegalArgumentException("unknown program: " + code);
        };
    }

    private ShortsBlock shorts() {
        ShortsBlock block = new ShortsBlock("Krotkie metraze");
        block.add(new Film("Kot", 12));
        block.add(new Film("Pies", 9));
        block.add(new Film("Ryba", 15));
        return block;
    }
}
