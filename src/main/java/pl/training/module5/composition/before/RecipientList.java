package pl.training.module5.composition.before;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecipientList extends ArrayList<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(this));
    }
}
