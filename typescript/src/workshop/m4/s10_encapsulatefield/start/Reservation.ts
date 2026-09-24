/**
 * Start: status rezerwacji to publiczne pole. Reguły przejść (NEW -> PAID -> USED, ...)
 * pilnuje - czasem - kod klientów. Każdy może też po prostu przypisać dowolny tekst.
 */
export class Reservation {
  status = 'NEW';
}
