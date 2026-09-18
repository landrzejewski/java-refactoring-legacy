import { describe, expect, it } from 'vitest';
import { EquipmentType } from '../../../src/module4/model/EquipmentType.js';
import { RentalRequest } from '../../../src/module4/model/RentalRequest.js';
import { RentalQuoteService } from '../../../src/module4/stage0/RentalQuoteService.js';

describe('RentalQuoteServiceCharacterizationTest', () => {
  const service = new RentalQuoteService();

  it('documentsCompleteGeneratorQuote', () => {
    const request = new RentalRequest(' Acme ', EquipmentType.GENERATOR, 8, true, true);

    const quote = service.createQuote(request);

    expect(quote).toBe(`RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 8
Base: 960.00
Discount: 96.00
Insurance: 64.00
Delivery: 25.00
Net: 953.00
VAT: 219.19
Total: 1172.19
`);
  });

  it('documentsDiscountBoundary', () => {
    const sixDays = service.createQuote(
      new RentalRequest('Acme', EquipmentType.GENERATOR, 6, false, false));
    const sevenDays = service.createQuote(
      new RentalRequest('Acme', EquipmentType.GENERATOR, 7, false, false));

    expect(sixDays).toContain('Discount: 0.00\n');
    expect(sixDays).toContain('Total: 885.60\n');
    expect(sevenDays).toContain('Discount: 84.00\n');
    expect(sevenDays).toContain('Total: 929.88\n');
  });

  it('documentsVatRounding', () => {
    const quote = service.createQuote(
      new RentalRequest('Acme', EquipmentType.DRILL, 1, false, false));

    expect(quote).toContain('VAT: 9.20\n');
    expect(quote).toContain('Total: 49.19\n');
  });

  it('distinguishesInsuranceFromDelivery', () => {
    const insuranceOnly = service.createQuote(
      new RentalRequest('Acme', EquipmentType.DRILL, 2, true, false));
    const deliveryOnly = service.createQuote(
      new RentalRequest('Acme', EquipmentType.DRILL, 2, false, true));

    expect.soft(insuranceOnly).toContain('Insurance: 16.00\n');
    expect.soft(insuranceOnly).toContain('Delivery: 0.00\n');
    expect.soft(insuranceOnly).toContain('Total: 118.06\n');
    expect.soft(deliveryOnly).toContain('Insurance: 0.00\n');
    expect.soft(deliveryOnly).toContain('Delivery: 25.00\n');
    expect.soft(deliveryOnly).toContain('Total: 129.13\n');
  });

  it('documentsDiscountRounding', () => {
    const quote = service.createQuote(
      new RentalRequest('Acme', EquipmentType.DRILL, 15, false, false));

    expect(quote).toContain('Discount: 59.99\n');
    expect(quote).toContain('Net: 539.86\n');
    expect(quote).toContain('Total: 664.03\n');
  });
});
