package pl.training.workshop.m6.s13_extractcomposite.step2;

/** Krok 2: blok krótkich metraży = suma dzieci, bez przerw. */
public final class ShortsBlock extends CompositeProgramItem {
    public ShortsBlock(String name) {
        super(name);
    }

    @Override
    public int minutes() {
        return childrenMinutes();
    }

    @Override
    protected String label() {
        return "Blok";
    }
}
