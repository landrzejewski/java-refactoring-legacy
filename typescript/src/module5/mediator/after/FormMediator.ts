/**
 * Jedyna zależność komponentów: zgłaszają mediatorowi, że coś się w nich zmieniło.
 */
export interface FormMediator {
  versionChanged(): void;

  approverChosen(): void;

  publishClicked(): void;
}
