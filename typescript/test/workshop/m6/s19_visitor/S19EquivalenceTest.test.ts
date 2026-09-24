import { describe } from 'vitest';

import * as startPrinter from '../../../../src/workshop/m6/s19_visitor/start/ReceiptPrinter.js';
import * as startOrders from '../../../../src/workshop/m6/s19_visitor/start/SampleOrders.js';
import * as step1Printer from '../../../../src/workshop/m6/s19_visitor/step1/ReceiptPrinter.js';
import * as step1Orders from '../../../../src/workshop/m6/s19_visitor/step1/SampleOrders.js';
import * as step2Printer from '../../../../src/workshop/m6/s19_visitor/step2/ReceiptPrinter.js';
import * as step2Orders from '../../../../src/workshop/m6/s19_visitor/step2/SampleOrders.js';
import * as step3Printer from '../../../../src/workshop/m6/s19_visitor/step3/ReceiptPrinter.js';
import * as step3Orders from '../../../../src/workshop/m6/s19_visitor/step3/SampleOrders.js';
import { Scene } from '../../support/scene.js';

/** Paragon (linie, suma, VAT) identyczny: instanceof, Visitor i switch po zamkniętej unii. */
describe('S19EquivalenceTest', () => {
  describe('everyStepPrintsTheSameReceipt', () => {
    Scene.variants<string, string>()
      .variant('start', (code) => new startPrinter.ReceiptPrinter().print(new startOrders.SampleOrders().find(code)))
      .variant('step1', (code) => new step1Printer.ReceiptPrinter().print(new step1Orders.SampleOrders().find(code)))
      .variant('step2', (code) => new step2Printer.ReceiptPrinter().print(new step2Orders.SampleOrders().find(code)))
      .variant('step3', (code) => new step3Printer.ReceiptPrinter().print(new step3Orders.SampleOrders().find(code)))
      .expect('wieczór: bilet, bar, voucher', 'evening', `Bilet Diuna IMAX 40.00
Popcorn L 18.00
Cola 9.00
Voucher KINO20 -20.00
Razem: 47.00
VAT: 8.01
`)
      .expect('voucher prawie pokrywa bilet', 'voucher', `Bilet Amator 2D 25.00
Voucher KINO20 -20.00
Razem: 5.00
VAT: 1.85
`)
      .expect('puste zamówienie', 'empty', 'Razem: 0.00\nVAT: 0.00\n')
      .tests();
  });
});
