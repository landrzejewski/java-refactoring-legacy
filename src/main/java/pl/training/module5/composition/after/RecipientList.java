package pl.training.module5.composition.after;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class RecipientList implements Iterable<String> {
    private final List<String> recipients = new ArrayList<>();

    public boolean add(String recipient) {
        return recipients.add(recipient);
    }

    public boolean remove(Object recipient) {
        return recipients.remove(recipient);
    }

    public boolean contains(Object recipient) {
        return recipients.contains(recipient);
    }

    public int size() {
        return recipients.size();
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(recipients).iterator();
    }

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recipients));
    }
}
