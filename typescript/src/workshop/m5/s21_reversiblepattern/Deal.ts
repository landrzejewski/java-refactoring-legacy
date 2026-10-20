/**
 * Stabilny kontrakt sceny - umowa z dystrybutorem filmu.
 *
 * @param model PERCENT (procent od przychodu z biletów) albo FESTIVAL (stawka z systemu festiwalu)
 */
export class Deal {
  constructor(readonly title: string, readonly model: string) {}
}
