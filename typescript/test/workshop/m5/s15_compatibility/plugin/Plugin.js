// Wtyczka partnera zbudowana przeciw API 1.x (step1) - gotowy JavaScript, którego nikt już nie
// przekompiluje (odpowiednik skompilowanego .class z Javy). Klasy API dostaje z zewnątrz, jak po
// podmianie pakietu biblioteki na serwerze. Nie jest sprawdzana przez kompilator TS (to plik .js).
export function run(api) {
  const ticket = new api.StudentTicket('Amator', api.Money.of('25.00'));
  return `${ticket.price()}/${new api.BoxOfficeApi().quoteStudent(ticket)}`;
}
