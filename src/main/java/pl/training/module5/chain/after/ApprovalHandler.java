package pl.training.module5.chain.after;

import java.util.Objects;

import pl.training.module5.chain.ChangeRequest;
import pl.training.module5.chain.Decision;
import pl.training.module5.chain.Decision.Approver;

/**
 * Ogniwo łańcucha zobowiązań. Każdy handler sam decyduje: obsługuje żądanie
 * (i kończy przetwarzanie) albo przekazuje je dalej przez {@link #passOn}.
 * Żądanie obsługuje dokładnie jeden handler; gdy nikt go nie obsłuży, koniec łańcucha
 * odrzuca je.
 * <p>
 * Różnica względem Decoratora: dekorator zawsze deleguje do opakowanego obiektu i
 * dokłada do wyniku swoje zachowanie (działają wszystkie warstwy). W łańcuchu zobowiązań
 * handler może przerwać przetwarzanie - zwykle działa tylko jedno ogniwo.
 */
public abstract class ApprovalHandler {
    private ApprovalHandler next;

    /**
     * Jawne składanie łańcucha; zwraca kolejne ogniwo, aby można było pisać
     * {@code first.then(second).then(third)}.
     */
    public final ApprovalHandler then(ApprovalHandler next) {
        this.next = Objects.requireNonNull(next, "next");
        return next;
    }

    public abstract Decision handle(ChangeRequest request);

    protected final Decision passOn(ChangeRequest request) {
        if (next == null) {
            return Decision.of(Approver.REJECTED, "no approver for " + request.id());
        }
        return next.handle(request);
    }
}
