package pl.training.workshop.m6.s13_extractcomposite.step2;

/** Krok 2: maraton = suma dzieci + 15 minut przerwy między pozycjami. */
public final class Marathon extends CompositeProgramItem {
    public Marathon(String name) {
        super(name);
    }

    @Override
    public int minutes() {
        int count = children().size();
        return count == 0 ? 0 : childrenMinutes() + 15 * (count - 1);
    }

    @Override
    protected String label() {
        return "Maraton";
    }
}
