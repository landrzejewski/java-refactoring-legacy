import type { RefundDecision } from '../RefundDecision.js';
import type { RefundRequest } from '../RefundRequest.js';

/**
 * Krok 3: Chain of Responsibility - ogniwo zawsze zwraca decyzję: własną (i zatrzymuje łańcuch)
 * albo następnika (przekazuje dalej). Następnika dostaje w konstruktorze.
 *
 * Kształt jak w Decorator (obiekt opakowuje obiekt tego samego typu), ale intencja inna:
 * dekorator ZAWSZE woła wnętrze i dokłada coś do jego wyniku - każda warstwa ma udział
 * w odpowiedzi. Ogniwo łańcucha woła następnika tylko wtedy, gdy samo nie obsłuży żądania,
 * a odpowiedź pochodzi od dokładnie jednego ogniwa. Pytanie kontrolne: "czy wynik składa się
 * z wkładu wszystkich (Decorator), czy rozstrzyga go pierwszy właściwy (Chain)?".
 */
export interface RefundHandler {
  handle(request: RefundRequest): RefundDecision;
}
