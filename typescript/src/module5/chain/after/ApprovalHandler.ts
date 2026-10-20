import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { ChangeRequest } from '../ChangeRequest.js';
import { Approver, Decision } from '../Decision.js';

/**
 * Ogniwo łańcucha zobowiązań. Każdy handler sam decyduje: obsługuje żądanie
 * (i kończy przetwarzanie) albo przekazuje je dalej przez passOn.
 * Żądanie obsługuje dokładnie jeden handler; gdy nikt go nie obsłuży, koniec łańcucha
 * odrzuca je.
 *
 * Różnica względem Decoratora: dekorator zawsze deleguje do opakowanego obiektu i
 * dokłada do wyniku swoje zachowanie (działają wszystkie warstwy). W łańcuchu zobowiązań
 * handler może przerwać przetwarzanie - zwykle działa tylko jedno ogniwo.
 */
export abstract class ApprovalHandler {
  private next: ApprovalHandler | undefined;

  /**
   * Jawne składanie łańcucha; zwraca kolejne ogniwo, aby można było pisać
   * first.then(second).then(third). W Javie metoda jest `final`.
   * Uwaga TS: metoda `then` czyni obiekt "thenable" - handlera nie zwracamy z funkcji async.
   */
  then(next: ApprovalHandler): ApprovalHandler {
    this.next = requireNonNull(next, 'next');
    return next;
  }

  abstract handle(request: ChangeRequest): Decision;

  // W Javie `protected final`.
  protected passOn(request: ChangeRequest): Decision {
    if (this.next === undefined) {
      return Decision.of(Approver.REJECTED, 'no approver for ' + request.id);
    }
    return this.next.handle(request);
  }
}
