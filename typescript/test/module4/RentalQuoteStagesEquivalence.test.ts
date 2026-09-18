import { describe, expect, it } from 'vitest';
import { EquipmentType } from '../../src/module4/model/EquipmentType.js';
import { RentalRequest } from '../../src/module4/model/RentalRequest.js';
import { RentalQuoteService as Stage0 } from '../../src/module4/stage0/RentalQuoteService.js';
import { RentalQuoteService as Stage1 } from '../../src/module4/stage1/RentalQuoteService.js';
import { RentalQuoteService as Stage2 } from '../../src/module4/stage2/RentalQuoteService.js';
import { RentalQuoteService as Stage3 } from '../../src/module4/stage3/RentalQuoteService.js';

type Scenario = readonly [scenario: string, request: RentalRequest, expectedQuote: string];

const approvedQuotes: readonly Scenario[] = [
  [
    'complete generator quote',
    new RentalRequest(' Acme ', EquipmentType.GENERATOR, 8, true, true),
    `RENTAL QUOTE
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
`,
  ],
  [
    'insurance without delivery',
    new RentalRequest('Acme', EquipmentType.DRILL, 2, true, false),
    `RENTAL QUOTE
Customer: ACME
Equipment: DRILL
Days: 2
Base: 79.98
Discount: 0.00
Insurance: 16.00
Delivery: 0.00
Net: 95.98
VAT: 22.08
Total: 118.06
`,
  ],
  [
    'delivery without insurance',
    new RentalRequest('Acme', EquipmentType.DRILL, 2, false, true),
    `RENTAL QUOTE
Customer: ACME
Equipment: DRILL
Days: 2
Base: 79.98
Discount: 0.00
Insurance: 0.00
Delivery: 25.00
Net: 104.98
VAT: 24.15
Total: 129.13
`,
  ],
  [
    'day before discount threshold',
    new RentalRequest('Acme', EquipmentType.GENERATOR, 6, false, false),
    `RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 6
Base: 720.00
Discount: 0.00
Insurance: 0.00
Delivery: 0.00
Net: 720.00
VAT: 165.60
Total: 885.60
`,
  ],
  [
    'discount threshold',
    new RentalRequest('Acme', EquipmentType.GENERATOR, 7, false, false),
    `RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 7
Base: 840.00
Discount: 84.00
Insurance: 0.00
Delivery: 0.00
Net: 756.00
VAT: 173.88
Total: 929.88
`,
  ],
  [
    'discount rounding',
    new RentalRequest('Acme', EquipmentType.DRILL, 15, false, false),
    `RENTAL QUOTE
Customer: ACME
Equipment: DRILL
Days: 15
Base: 599.85
Discount: 59.99
Insurance: 0.00
Delivery: 0.00
Net: 539.86
VAT: 124.17
Total: 664.03
`,
  ],
  [
    'vat rounding',
    new RentalRequest('Acme', EquipmentType.DRILL, 1, false, false),
    `RENTAL QUOTE
Customer: ACME
Equipment: DRILL
Days: 1
Base: 39.99
Discount: 0.00
Insurance: 0.00
Delivery: 0.00
Net: 39.99
VAT: 9.20
Total: 49.19
`,
  ],
];

describe('RentalQuoteStagesEquivalenceTest', () => {
  it.each(approvedQuotes)('everyStageProducesApprovedQuote: %s', (_scenario, request, expectedQuote) => {
    const stage0 = new Stage0();
    const stage1 = new Stage1();
    const stage2 = new Stage2();
    const stage3 = new Stage3();

    expect.soft(stage0.createQuote(request)).toBe(expectedQuote);
    expect.soft(stage1.createQuote(request)).toBe(expectedQuote);
    expect.soft(stage2.createQuote(request)).toBe(expectedQuote);
    expect.soft(stage3.createQuote(request)).toBe(expectedQuote);
  });
});
