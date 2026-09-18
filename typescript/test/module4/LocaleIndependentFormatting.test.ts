import { afterEach, describe, expect, it, vi } from 'vitest';
import { EquipmentType } from '../../src/module4/model/EquipmentType.js';
import { RentalRequest } from '../../src/module4/model/RentalRequest.js';
import { RentalQuoteService as Stage0 } from '../../src/module4/stage0/RentalQuoteService.js';
import { RentalQuoteService as Stage1 } from '../../src/module4/stage1/RentalQuoteService.js';
import { RentalQuoteService as Stage2 } from '../../src/module4/stage2/RentalQuoteService.js';
import { RentalQuoteService as Stage3 } from '../../src/module4/stage3/RentalQuoteService.js';

// JS nie ma zmiennego domyślnego locale (Locale.setDefault). Zamiast tego podmieniamy
// API zależne od locale tak, by domyślnie używały ar-EG, i sprawdzamy, że kod ich nie woła.
describe('LocaleIndependentFormattingTest', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('quoteFormattingDoesNotDependOnDefaultFormatLocale', () => {
    const arabicLocale = 'ar-EG';
    const originalNumberToLocale = Number.prototype.toLocaleString;
    const originalUpperToLocale = String.prototype.toLocaleUpperCase;
    const numberToLocale = vi
      .spyOn(Number.prototype, 'toLocaleString')
      .mockImplementation(function (this: number, locales?: Intl.LocalesArgument, options?: Intl.NumberFormatOptions) {
        return originalNumberToLocale.call(this, locales ?? arabicLocale, options);
      });
    const upperToLocale = vi
      .spyOn(String.prototype, 'toLocaleUpperCase')
      .mockImplementation(function (this: string, locales?: Intl.LocalesArgument) {
        return originalUpperToLocale.call(this, locales ?? arabicLocale);
      });

    // Pod ar-EG formatowanie zależne od locale dałoby cyfry arabsko-indyjskie.
    expect((960).toLocaleString(undefined, { minimumFractionDigits: 2 })).not.toBe('960.00');
    numberToLocale.mockClear();
    upperToLocale.mockClear();

    const request = new RentalRequest('Acme', EquipmentType.GENERATOR, 8, true, true);

    const approvedQuote = new Stage0().createQuote(request);
    const stage1 = new Stage1();
    const stage2 = new Stage2();
    const stage3 = new Stage3();

    expect(approvedQuote).toContain('Days: 8\n');
    expect(approvedQuote).toContain('Total: 1172.19\n');
    expect.soft(stage1.createQuote(request)).toBe(approvedQuote);
    expect.soft(stage2.createQuote(request)).toBe(approvedQuote);
    expect.soft(stage3.createQuote(request)).toBe(approvedQuote);
    expect(numberToLocale).not.toHaveBeenCalled();
    expect(upperToLocale).not.toHaveBeenCalled();
  });
});
