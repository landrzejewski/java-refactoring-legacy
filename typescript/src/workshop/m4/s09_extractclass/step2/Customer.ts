/**
 * Krok 2: Move Method `contact` i `formattedPhone` do Customer.
 * Klient sam wie, jak się go przedstawia - dane i zachowanie mają jednego właściciela.
 */
export class Customer {
  constructor(readonly name: string, readonly email: string, readonly phone: string) {}

  contactLine(): string {
    return this.name + ' <' + this.email.trim().toLowerCase() + '>, tel. ' + this.formattedPhone();
  }

  private formattedPhone(): string {
    const digits = this.phone.replace(/\D/g, '');
    const local = digits.substring(digits.length - 9);
    return local.substring(0, 3) + '-' + local.substring(3, 6) + '-' + local.substring(6);
  }
}
