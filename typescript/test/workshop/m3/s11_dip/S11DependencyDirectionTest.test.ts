import { readFileSync } from 'node:fs';

import { describe, expect, it } from 'vitest';

import { LocalDateTime } from '../../../../src/workshop/shared/time.js';
import { Reservation } from '../../../../src/workshop/m3/s11_dip/Reservation.js';
import * as step3App from '../../../../src/workshop/m3/s11_dip/step3/app/ConfirmReservation.js';
import type { CustomerNotifier } from '../../../../src/workshop/m3/s11_dip/step3/app/CustomerNotifier.js';
import * as step3Notifier from '../../../../src/workshop/m3/s11_dip/step3/infra/SmtpCustomerNotifier.js';
import * as step3Infra from '../../../../src/workshop/m3/s11_dip/step3/infra/SmtpMailSender.js';
import { workshopDir } from '../../support/paths.js';

/** Ścieżki modułów importowanych przez plik sceny (np. '../infra/SmtpMailSender.js'). */
function importsOf(step: string, file: string): string[] {
  const source = readFileSync(workshopDir('m3', 's11_dip', step, file), 'utf8');
  return [...source.matchAll(/^import .* from '([^']+)';$/gm)].map((m) => m[1] ?? '');
}

function dependsOnInfra(step: string): boolean {
  return importsOf(step, 'app/ConfirmReservation.ts').some((path) => path.startsWith('../infra/'));
}

/**
 * DIP to kierunek zależności, nie sposób jej dostarczenia. Sprawdzamy importy pliku
 * przypadku użycia: DI w kroku 1 nie zmienia kierunku, port w kroku 3 - tak.
 * (Bez start - ten jest edytowany na żywo; krok 1 to start z wstrzykniętą zależnością.)
 */
describe('S11DependencyDirectionTest', () => {
  it('step1AndStep2PolicyDependsOnInfrastructure', () => {
    expect(dependsOnInfra('step1'), 'wstrzykniecie konkretnej klasy to DI, ale nie DIP').toBe(true);
    expect(dependsOnInfra('step2')).toBe(true);
  });

  it('step3PolicyDependsOnlyOnItsOwnPort', () => {
    expect(dependsOnInfra('step3')).toBe(false);
    // Adapter z infra implementuje port z app - sprawdza to kompilator...
    const port: CustomerNotifier = new step3Notifier.SmtpCustomerNotifier(new step3Infra.SmtpMailSender('h', 25));
    expect(port).toBeDefined();
    // ...a import w pliku adaptera pokazuje kierunek zależności źródłowej.
    expect(importsOf('step3', 'infra/SmtpCustomerNotifier.ts'),
      'adapter z infra implementuje port z app: zaleznosc zrodlowa infra -> app')
      .toContain('../app/CustomerNotifier.js');
  });

  it('step3PolicyIsTestableWithAHandWrittenFake', () => {
    const sent: string[] = [];
    const useCase = new step3App.ConfirmReservation({
      notifyCustomer: (email, message) => {
        sent.push(`${email}: ${message}`);
      },
    });

    useCase.confirm(new Reservation('anna@kino.pl', 'Diuna', LocalDateTime.of(2026, 10, 2, 20, 0), 2));

    expect(sent).toEqual(['anna@kino.pl: Rezerwacja: Diuna, 2026-10-02T20:00, miejsc: 2. Zaplac w ciagu 15 minut.']);
  });
});
