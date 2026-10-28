# Moduł 2. Prewencja - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Dziesięć małych scen w domenie kina CineLegacy pokazuje tematy z części II prezentacji („Zanim wprowadzisz wzorzec”) jako serie bezpiecznych zmian: bramki kompilatora i jakości jako najszybsze pętle informacji zwrotnej, odraczanie decyzji (YAGNI) i ich zapis (ADR z regułą sprawdzaną przez test), projekt, który hamuje degradację kodu (zasada pojedynczej odpowiedzialności, zasada odwrócenia zależności, granice czystej architektury, automatyczna kontrola granicy) oraz codzienną dbałość o kod (zasada harcerza, seria zmian gotowych do przeglądu). Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny ilustrują sekcję 2 slajdów ("Zanim wprowadzisz wzorzec"): każdy temat ma własną, kilkuminutową scenę. Sceny są pokazami prowadzącego do wyboru - nie trzeba pokazywać wszystkich. Numery scen odpowiadają katalogom w kodzie i w tej kolejności sceny są opisane poniżej. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Proponowana kolejność pokazu” na końcu.

Port TypeScript: kod scen leży w `typescript/src/workshop/m2/sNN_.../{start,step1,...}`, testy vitest w `typescript/test/workshop/m2/sNN_.../`. Kwoty to `Decimal` z biblioteki decimal.js (odpowiednik `BigDecimal`) albo `Money` z `typescript/src/workshop/shared/Money.ts`. `Decimal` nie pamięta skali, więc tam, gdzie Java drukuje kwotę przez `toString()`, port drukuje `toFixed(2)`. Rekordy Javy to klasy z polami `readonly` w konstruktorze, a statyczne metody klas-punktów wejścia to funkcje modułu. Największe adaptacje dotyczą scen narzędziowych: bramka kompilatora (s03) i bramka jakości (s04) korzystają z API kompilatora TypeScript (pakiet `typescript-api`), a bramka jakości także z zestawów przypadków testowych ładowanych jako moduły. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m2              # sceny i kroki modułu 2
scripts/warsztat.sh --lang ts test m2/s01          # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh --lang ts test m2              # wszystkie sceny modułu (150 testów)
scripts/warsztat.sh --lang ts diff m2/s01 0 1      # co zmienia krok 1 względem start (0 = start)
scripts/warsztat.sh --lang ts diff m2/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m2/s09 3        # przeskok: start = snapshot kroku 3
scripts/warsztat.sh --lang ts next m2/s09          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m2/s09         # przywrócenie start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku - to w praktyce pętla informacji zwrotnej ze slajdu 2.1.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `--lang ts next m2/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `next` (albo `jump` do konkretnego snapshotu) i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: funkcja `confirmReservation()` w `Main.ts` (s08), `reservationController()` w `CinemaApplication.ts` (s09), `describe()` w `CinemaApp.ts` (s10). Zmiany konstruktorów i sygnatur robimy ręcznie, a kompilator (`npm run typecheck`) wskaże każde wywołanie do zmiany.
- Część testów to nie tylko równoważność (`SNNEquivalenceTest.test.ts`), ale też dowody strategii (`SNNSolutionTest`, `S08DependencyDirectionTest`, `S09UseCaseTest`, `S10ArchitectureTest`): wynik bramki, lista naruszeń ADR, kierunek zależności. Testy, które **dokumentują stan startowy** (`startViolatesBothRules` w s02, `startFailsTheGateWithFourKindsOfWarnings` w s03, `startPassesEverythingBecauseItChecksNothing` w s04, `startDomainHasNoViolationsBeyondTheFrozenOnes` w s10), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `start` - to znak, że start jest już naprawiony. Pułapki, które znikają po naprawie, testy dokumentują na kopii w kroku, który jeszcze je ma (np. s05 krok 1 - antyprzykład nadużycia Boy Scout).
- Sceny s02, s03, s04, s08 i s10 czytają pliki źródłowe sceny. Ścieżki są liczone względem modułu (`import.meta.url`, pomocnik testów `workshopDir(...)` z `typescript/test/workshop/support/paths.ts`), więc testy działają niezależnie od katalogu, z którego je uruchamiasz.
- Sceny s03 i s04 celowo zawierają kod, którego nie przepuszcza ostrzejsza bramka (`start` w s03 i `sample/dirty` w s04) - to materiał dla bramek, nie niedoróbka. W s03 przelot w `switch` jest w buildzie projektu wyciszony `// @ts-expect-error`, a próbki s04 są wyłączone z typechecku projektu (`exclude` w `typescript/tsconfig.json`).
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów. Tam, gdzie krok opiera się na kontroli kompilatora (literówka w nazwie reguły w s01, usunięty plik, zmieniona sygnatura), uruchom też `npm run typecheck` w katalogu `typescript`.
- Refaktoryzacje z katalogu (Extract Method, Extract Constant, Inline, Move, Rename, Change Signature, Safe Delete, Extract Interface, Extract Class) wykonujemy jako zwykłe, małe edycje kodu. Przed usunięciem symbolu znajdź wszystkie jego użycia, a po zmianie sygnatury uruchom kompilator - wskaże każde miejsce do poprawy.
- Testy uruchamiasz skryptem (`scripts/warsztat.sh --lang ts test m2/sNN`).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji | 3 | `m2/s01_yagni` | ~12 min |
| s02 | 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj | 2 | `m2/s02_adr` | ~10 min |
| s03 | 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 3 | `m2/s03_compilergate` | ~10 min |
| s04 | 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 4 | `m2/s04_qualitygate` | ~12 min |
| s05 | 2.9. Zasada harcerza i małe porcje zmian | 2 | `m2/s05_boyscout` | ~8 min |
| s06 | 2.9. Zasada harcerza i małe porcje zmian | 3 | `m2/s06_reviewableseries` | ~10 min |
| s07 | 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany | 3 | `m2/s07_srp` | ~12 min |
| s08 | 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji | 3 | `m2/s08_dip` | ~12 min |
| s09 | 2.8. Reguła zależności, porty i korzeń kompozycji | 4 | `m2/s09_cleanarchitecture` | ~18 min |
| s10 | 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie) | 2 | `m2/s10_boundarycheck` | ~12 min |

Katalogi są względne wobec `typescript/src/workshop/`, testy leżą w odpowiednim katalogu pod `typescript/test/workshop/`.

## Scena s01. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji
**Katalog:** `typescript/src/workshop/m2/s01_yagni` · **Test:** `scripts/warsztat.sh --lang ts test m2/s01` (`S01EquivalenceTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `Map` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy mapę typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba. To praktyczna wersja ostatniego odpowiedzialnego momentu: decyzję o mechanizmie rozszerzeń odkładamy, dopóki nie znamy jego kształtu, a usunięcie spekulacji jest drzwiami dwukierunkowymi.

**Efekt:** Z pięciu typów zostaje jedna klasa z dwiema nazwanymi regułami, a literówka w nazwie reguły nie skompiluje się. Gdy przyjdzie trzecia reguła z innym właścicielem, abstrakcję trzeba będzie wprowadzić ponownie.

**Różnica względem Javy:** kontekst `Map<String, Object>` to `ReadonlyMap<string, unknown>` z rzutowaniami `as LocalTime` / `as number` - te same "rzutowania z mapy", które znikają w kroku 2. Dwa konstruktory `TicketPricer` z Javy to jeden konstruktor z parametrami domyślnymi, a `Supplier<PricingRule>` to funkcja `() => PricingRule`.

### Co widzimy

`TicketPricer` to spekulatywny silnik: rejestr pluginów, konfiguracja napisem `'morning,vip'`, priorytety i kontekst `Map<string, unknown>`. Obsługuje dokładnie dwie reguły (poranek -5.00, VIP +10.00), które nie zależą od kolejności.

```typescript
const context = new Map<string, unknown>();
context.set('start', quote.start);
context.set('row', quote.row);
...
for (const rule of this.registry.resolve(this.activeRules)) {
  if (rule.appliesTo(context)) {
    price = rule.apply(context, price);
  }
}
```

Na żywo: zmień w konstruktorze wartość domyślną `'morning,vip'` na `'morning,vlp'` i uruchom test - błąd konfiguracji wychodzi dopiero w runtime (`IllegalArgumentError: brak reguly: vlp`). Cofnij zmianę.

### Krok 1: Inline Class - rejestr pluginów znika

**Zmiany:**

1. W `TicketPricer` usuń konstruktor z parametrami `registry` i `activeRules`; w ich miejsce dodaj pole `rules` z niezmienną listą `Object.freeze([new MorningRule(), new VipRule()])` (Inline Class dla rejestru). W importach miejsce `RuleRegistry` zajmują `MorningRule`, `VipRule` i `import type { PricingRule }`.
2. W `price` pętla iteruje po `this.rules` zamiast po `this.registry.resolve(this.activeRules)`.
3. Usuń plik `RuleRegistry.ts` - rejestr pluginów i konfiguracja napisem znikają.
4. Usuń metodę `priority()` z interfejsu `PricingRule` oraz jej implementacje w `MorningRule` (`return 10`) i `VipRule` (`return 20`) - była potrzebna tylko do sortowania w rejestrze; przed usunięciem upewnij się, że nie ma innych użyć (Safe Delete). Pozostała treść obu reguł nie zmienia się.

**Przed (`TicketPricer.ts`):**

```typescript
import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import { RuleRegistry } from './RuleRegistry.js';

export class TicketPricer {
  constructor(
    private readonly registry: RuleRegistry = RuleRegistry.withDefaults(),
    private readonly activeRules: string = 'morning,vip',
  ) {}

  price(quote: TicketQuote): Decimal {
    const context = new Map<string, unknown>();
    context.set('format', quote.format);
    context.set('start', quote.start);
    context.set('row', quote.row);
    context.set('vipFromRow', quote.vipFromRow);
    let price = this.basePrice(quote.format);
    for (const rule of this.registry.resolve(this.activeRules)) {
      if (rule.appliesTo(context)) {
        price = rule.apply(context, price);
      }
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
```

**Po (`TicketPricer.ts`; metoda `basePrice` bez zmian):**

```typescript
import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import { MorningRule } from './MorningRule.js';
import type { PricingRule } from './PricingRule.js';
import { VipRule } from './VipRule.js';

export class TicketPricer {
  private readonly rules: readonly PricingRule[] = Object.freeze([new MorningRule(), new VipRule()]);

  price(quote: TicketQuote): Decimal {
    const context = new Map<string, unknown>();
    context.set('format', quote.format);
    context.set('start', quote.start);
    context.set('row', quote.row);
    context.set('vipFromRow', quote.vipFromRow);
    let price = this.basePrice(quote.format);
    for (const rule of this.rules) {
      if (rule.appliesTo(context)) {
        price = rule.apply(context, price);
      }
    }
    return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
```

**Po (`PricingRule.ts`):**

```typescript
import type { Decimal } from 'decimal.js';

export interface PricingRule {
  appliesTo(context: ReadonlyMap<string, unknown>): boolean;

  apply(context: ReadonlyMap<string, unknown>, price: Decimal): Decimal;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s01` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s01 0 1`

### Krok 2: Change Signature - typowane dane zamiast mapy

**Zmiany:**

1. W interfejsie `PricingRule` zmień sygnaturę `appliesTo`: parametr `context: ReadonlyMap<string, unknown>` zastąp `quote: TicketQuote`; z `apply` usuń parametr kontekstu - zostaje `apply(price: Decimal)` (Change Signature, ręcznie). Interfejs dostaje `import type { TicketQuote } from '../TicketQuote.js'`.
2. Popraw obie implementacje, które wskaże kompilator: w `MorningRule` warunek to `quote.start.hour < 12`, w `VipRule` - `quote.row >= quote.vipFromRow`; rzutowania `as LocalTime` i `as number` znikają. Obie reguły importują `TicketQuote` (`import type`), a `MorningRule` przestaje importować `LocalTime`.
3. W `TicketPricer.price` usuń budowanie mapy `context` i przekazuj regułom `quote` oraz `price`.

**Przed (`PricingRule.ts`):**

```typescript
import type { Decimal } from 'decimal.js';

export interface PricingRule {
  appliesTo(context: ReadonlyMap<string, unknown>): boolean;

  apply(context: ReadonlyMap<string, unknown>, price: Decimal): Decimal;
}
```

**Przed (`VipRule.ts`, metody; w `MorningRule` analogicznie):**

```typescript
appliesTo(context: ReadonlyMap<string, unknown>): boolean {
  return (context.get('row') as number) >= (context.get('vipFromRow') as number);
}

apply(_context: ReadonlyMap<string, unknown>, price: Decimal): Decimal {
  return price.plus(new Decimal('10.00'));
}
```

**Po (`PricingRule.ts`):**

```typescript
import type { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';

export interface PricingRule {
  appliesTo(quote: TicketQuote): boolean;

  apply(price: Decimal): Decimal;
}
```

**Po (`MorningRule.ts`):**

```typescript
import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import type { PricingRule } from './PricingRule.js';

export class MorningRule implements PricingRule {
  appliesTo(quote: TicketQuote): boolean {
    return quote.start.hour < 12;
  }

  apply(price: Decimal): Decimal {
    return price.minus(new Decimal('5.00'));
  }
}
```

**Po (`VipRule.ts`):**

```typescript
import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';
import type { PricingRule } from './PricingRule.js';

export class VipRule implements PricingRule {
  appliesTo(quote: TicketQuote): boolean {
    return quote.row >= quote.vipFromRow;
  }

  apply(price: Decimal): Decimal {
    return price.plus(new Decimal('10.00'));
  }
}
```

**Po (`TicketPricer.ts`, metoda `price`; reszta pliku bez zmian):**

```typescript
price(quote: TicketQuote): Decimal {
  let price = this.basePrice(quote.format);
  for (const rule of this.rules) {
    if (rule.appliesTo(quote)) {
      price = rule.apply(price);
    }
  }
  return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s01 1 2`

### Krok 3: Inline Class dla reguł, usunięcie interfejsu

**Zmiany:**

1. Do `TicketPricer` dodaj prywatne metody `isMorning(quote)` i `isVip(quote)` z ciałami `appliesTo` obu reguł (Inline Class dla `MorningRule` i `VipRule`); w `price` pętlę po regułach zastąp dwoma `if`, a `return` zwraca `price` bez końcowego `toDecimalPlaces`.
2. Kwoty zniżki i dopłaty wydziel do pól `private static readonly MORNING_DISCOUNT = new Decimal('5.00')` i `VIP_SURCHARGE = new Decimal('10.00')` (Extract Constant).
3. Usuń pliki `MorningRule.ts`, `VipRule.ts` i `PricingRule.ts`; pole `rules` w `TicketPricer` i importy tych trzech modułów znikają razem z nimi.

**Przed (`TicketPricer.ts`, fragment):**

```typescript
private readonly rules: readonly PricingRule[] = Object.freeze([new MorningRule(), new VipRule()]);

price(quote: TicketQuote): Decimal {
  let price = this.basePrice(quote.format);
  for (const rule of this.rules) {
    if (rule.appliesTo(quote)) {
      price = rule.apply(price);
    }
  }
  return price.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
}
```

**Po (`TicketPricer.ts`; metoda `basePrice` bez zmian):**

```typescript
import { Decimal } from 'decimal.js';

import type { TicketQuote } from '../TicketQuote.js';

export class TicketPricer {
  private static readonly MORNING_DISCOUNT = new Decimal('5.00');
  private static readonly VIP_SURCHARGE = new Decimal('10.00');

  price(quote: TicketQuote): Decimal {
    let price = this.basePrice(quote.format);
    if (this.isMorning(quote)) {
      price = price.minus(TicketPricer.MORNING_DISCOUNT);
    }
    if (this.isVip(quote)) {
      price = price.plus(TicketPricer.VIP_SURCHARGE);
    }
    return price;
  }

  private isMorning(quote: TicketQuote): boolean {
    return quote.start.hour < 12;
  }

  private isVip(quote: TicketQuote): boolean {
    return quote.row >= quote.vipFromRow;
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** YAGNI nie zabrania testów, nazwanych metod ani szwu testowego - cena liczona jest z danych wejściowych, bez zegara i stanu statycznego, więc każdą regułę łatwo sprawdzić. Gdy pojawi się trzecia reguła z innym właścicielem, wydzielimy abstrakcję wtedy.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s01 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą pięć pytań ze slajdu 2.4: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu typów została jedna klasa.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s02. ADR i wykonywalny model decyzji

**Temat ze slajdów:** 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj
**Katalog:** `typescript/src/workshop/m2/s02_adr` · **Test:** `scripts/warsztat.sh --lang ts test m2/s02` (`S02EquivalenceTest.test.ts`, `S02SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** ADR-0007 mówi, że cennik nie zależy od `notification` i nie trzyma kwot w `number`, a cennik w `start` sam wysyła mail o rabacie grupowym i liczy w `number`. Cennik zaczyna zwracać `Quote` z informacją o rabacie, mail wysyła `BookingService`, a kwoty przechodzą na `Money`.

**Zasada:** ADR zapisuje decyzję o trwałym wpływie: kontekst, opcje, decyzję i konsekwencje - to dokument historyczny, którego się nie edytuje, tylko zastępuje nowym. Regułę z ADR warto uczynić wykonywalną, żeby build pilnował jej na co dzień. Nie pisze się ADR dla każdego Rename.

**Efekt:** `ArchitectureRules` zwraca pustą listę naruszeń, a maile i odpowiedzi są takie same jak przed zmianą. Reguła tekstowa ma swoje granice (dynamiczny `import()`, re-eksport z innego katalogu albo kwota w `number` pod inną nazwą ją ominą) i ADR opisuje to w konsekwencjach.

**Różnica względem Javy:** reguły działają na tekście plików `.ts`. R1 zakazuje linii `import ... '.../notification/...'` w katalogu `pricing`. R2 w Javie szuka słowa `double`; w TypeScripcie `number` jest też typem liczby sztuk, która w kroku 2 zostaje, więc reguła zakazuje adnotacji `number` przy nazwie kwoty (`total`, `sum`, `price`, `amount`, `unitPrice`). ADR mówi o "katalogu" zamiast "pakietu" i o pakiecie npm (workspace) zamiast modułu Maven. Enum reguł z polami (`ArchitectureRules.Rule`) to klasa `Rule` ze statycznymi instancjami i metodą `values()`. Naruszenie R1 w starcie wskazuje `TicketPricing.ts:1` (w Javie `:3`, bo import stoi pod linią `package`).

### Co widzimy

Obok kodu sceny leży `ADR-0007-cennik-jako-czysty-modul.md` (status: Zaakceptowana) z dwiema regułami: **R1** katalog `pricing` nie zależy od katalogu `notification`, **R2** `pricing` nie używa `number` do kwot. `ArchitectureRules` to wykonywalny model tych reguł. W `start` cennik sam wysyła mail o rabacie grupowym i liczy w `number`:

```typescript
import type { GroupMailer } from '../notification/GroupMailer.js';
...
total(organizer: string, tickets: number, unitPrice: number): number {
  ...
  this.mailer.groupDiscountGranted(organizer, tickets);
```

### Krok 1: spełnienie R1 - wynik zamiast efektu

**Zmiany:**

1. Nowy plik `pricing/Quote.ts`: klasa `Quote` z polami `readonly total: number` i `readonly groupDiscount: boolean`.
2. W `TicketPricing` zmień sygnaturę `total(organizer, tickets, unitPrice): number` na `total(tickets, unitPrice): Quote` - parametr `organizer` znika, a informacja o rabacie wraca w polu `groupDiscount` (Change Signature, ręcznie; wywołania wskaże `npm run typecheck`).
3. Usuń z `TicketPricing` konstruktor z polem `mailer` i import `GroupMailer` - cennik przestaje zależeć od katalogu `notification`.
4. W `BookingService.book` twórz `TicketPricing` bez argumentu, a wywołanie `mailer.groupDiscountGranted(organizer, tickets)` wykonuj po wycenie, gdy `quote.groupDiscount` jest prawdziwe. (`GroupMailer` zostaje bez zmian; w snapshotach zmienia się tylko jego komentarz wariantu.)

**Przed (`pricing/TicketPricing.ts`):**

```typescript
import type { GroupMailer } from '../notification/GroupMailer.js';

export class TicketPricing {
  constructor(private readonly mailer: GroupMailer) {}

  total(organizer: string, tickets: number, unitPrice: number): number {
    let sum = unitPrice * tickets;
    if (tickets >= 10) {
      sum = sum - sum * 0.10;
      this.mailer.groupDiscountGranted(organizer, tickets);
    }
    return Math.round(sum * 100) / 100.0;
  }
}
```

**Przed (`BookingService.ts`, pola i metoda `book`):**

```typescript
export class BookingService {
  private readonly mailer = new GroupMailer();
  private readonly pricing = new TicketPricing(this.mailer);

  book(organizer: string, tickets: number, format: string): string {
    const unitPrice = BookingService.unitPrice(format);
    const total = this.pricing.total(organizer, tickets, unitPrice);
    return 'DO ZAPLATY ' + total.toFixed(2);
  }
```

**Po (`pricing/Quote.ts`, nowy plik):**

```typescript
/** Wynik cennika niesie informację o rabacie - zamiast efektu ubocznego mamy daną. */
export class Quote {
  constructor(readonly total: number, readonly groupDiscount: boolean) {}
}
```

**Po (`pricing/TicketPricing.ts`):**

```typescript
import { Quote } from './Quote.js';

export class TicketPricing {
  total(tickets: number, unitPrice: number): Quote {
    let sum = unitPrice * tickets;
    const groupDiscount = tickets >= 10;
    if (groupDiscount) {
      sum = sum - sum * 0.10;
    }
    return new Quote(Math.round(sum * 100) / 100.0, groupDiscount);
  }
}
```

**Po (`BookingService.ts`, pola i metoda `book`; importy i reszta pliku bez zmian):**

```typescript
export class BookingService {
  private readonly mailer = new GroupMailer();
  private readonly pricing = new TicketPricing();

  book(organizer: string, tickets: number, format: string): string {
    const unitPrice = BookingService.unitPrice(format);
    const quote = this.pricing.total(tickets, unitPrice);
    if (quote.groupDiscount) {
      this.mailer.groupDiscountGranted(organizer, tickets);
    }
    return 'DO ZAPLATY ' + quote.total.toFixed(2);
  }
```

**Uruchom:** test zielony; `step1RemovesDependencyOnNotification` - zostało tylko naruszenie `ADR-0007/R2`.
**Co powiedzieć:** naruszenie w teście prowadzi wprost do identyfikatora decyzji i jej uzasadnienia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s02 0 1`

### Krok 2: spełnienie R2 - Money

**Zmiany:**

1. Zmień typ pola `Quote.total` i parametru `unitPrice` w `TicketPricing.total` z `number` na `Money` (Type Migration, ręcznie); przez kolejne miejsca prowadzi `npm run typecheck`. Oba pliki `pricing` dostają `import type { Money } from '../../../../shared/Money.js'`.
2. Obliczenia przechodzą na `Money`: `unitPrice.times(tickets)` i `sum.minus(sum.percent(...))`; próg i procent rabatu wydziel do stałych `GROUP_SIZE = 10` i `GROUP_DISCOUNT_PERCENT = 10`.
3. W `BookingService` metoda `unitPrice(format)` zwraca `Money` (`Money.of('40.00')`, `Money.of('32.00')`, `Money.of('25.00')`), a odpowiedź formatuje `quote.total.toString()` zamiast `toFixed(2)`; dochodzi `import { Money } from '../../../shared/Money.js'`. (`GroupMailer` nadal bez zmian.)

**Przed (`pricing/TicketPricing.ts`, metoda `total`):**

```typescript
total(tickets: number, unitPrice: number): Quote {
  let sum = unitPrice * tickets;
  const groupDiscount = tickets >= 10;
  if (groupDiscount) {
    sum = sum - sum * 0.10;
  }
  return new Quote(Math.round(sum * 100) / 100.0, groupDiscount);
}
```

**Przed (`BookingService.ts`, metoda `unitPrice`):**

```typescript
private static unitPrice(format: string): number {
  switch (format) {
    case 'IMAX': return 40.00;
    case '3D': return 32.00;
    default: return 25.00;
  }
}
```

**Po (`pricing/Quote.ts`):**

```typescript
import type { Money } from '../../../../shared/Money.js';

export class Quote {
  constructor(readonly total: Money, readonly groupDiscount: boolean) {}
}
```

**Po (`pricing/TicketPricing.ts`):**

```typescript
import type { Money } from '../../../../shared/Money.js';
import { Quote } from './Quote.js';

export class TicketPricing {
  private static readonly GROUP_SIZE = 10;
  private static readonly GROUP_DISCOUNT_PERCENT = 10;

  total(tickets: number, unitPrice: Money): Quote {
    let sum = unitPrice.times(tickets);
    const groupDiscount = tickets >= TicketPricing.GROUP_SIZE;
    if (groupDiscount) {
      sum = sum.minus(sum.percent(TicketPricing.GROUP_DISCOUNT_PERCENT));
    }
    return new Quote(sum, groupDiscount);
  }
}
```

**Po (`BookingService.ts`, zmienione miejsca):**

```typescript
import { Money } from '../../../shared/Money.js';
...
    return 'DO ZAPLATY ' + quote.total.toString();
...
  private static unitPrice(format: string): Money {
    switch (format) {
      case 'IMAX': return Money.of('40.00');
      case '3D': return Money.of('32.00');
      default: return Money.of('25.00');
    }
  }
```

**Uruchom:** test zielony; `step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s02 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `adrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: dynamiczny `import()`, re-eksport z innego katalogu albo kwota w `number` pod inną nazwą ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s03. Bramka kompilatora - opcje strict i ostrzeżenia jako błędy

**Temat ze slajdów:** 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Katalog:** `typescript/src/workshop/m2/s03_compilergate` · **Test:** `scripts/warsztat.sh --lang ts test m2/s03` (`S03EquivalenceTest.test.ts`, `S03SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Kod `start` nie przechodzi bramki `CompilerGate` (opcje ostrzejsze niż build projektu, każde ostrzeżenie to błąd): surowy `new Map()` i niesprawdzone asercje `as` w `SeatMap`, przestarzałe przeciążenie `PriceTable.basePrice(format: number)` i celowy przelot w `switch`. Usuwamy ostrzeżenia po jednej kategorii na krok: argumenty typu, nowe przeciążenie, osobna funkcja z `return` w każdym `case`.

**Zasada:** Bramka kompilatora traktuje ostrzeżenia jak błędy i zwraca strukturalną diagnostykę, więc test może wymagać zera ostrzeżeń albo braku nowych. W dużym legacy nie włącza się jej nagle: najpierw stan bazowy, potem "brak nowych naruszeń" i stopniowa redukcja, a tłumienie tylko wąskie i uzasadnione.

**Efekt:** Bramka przechodzi z zerem ostrzeżeń, a raport działa tak samo, z intencją "IMAX ma też Dolby" zapisaną wprost w `switch`. Przestarzałe przeciążenie zostaje w `PriceTable`, dopóki ktoś może je wołać spoza repozytorium.

**Różnica względem Javy:** w miejsce `javax.tools` z `--release 25 -Xlint:all -Werror` bramka używa API kompilatora TypeScript z pakietu `typescript-api` (alias npm na TypeScript 5.9 z klasycznym API kompilatora; sam projekt buduje pakiet `typescript` w wersji 7): `ts.createProgram` w pamięci z `strict` i dodatkowo `noUnusedLocals`, `noUnusedParameters`, `noImplicitReturns`, `noPropertyAccessFromIndexSignature`, `noFallthroughCasesInSwitch`, `allowUnreachableCode: false`. TypeScript sam nie zgłasza surowych typów, niesprawdzonych rzutowań ani użycia przestarzałego API, więc te trzy kategorie bramka wykrywa własnym przejściem po AST z `TypeChecker` - nazwy kategorii zostały takie jak w Javie: `rawtypes` (`new Map()` bez argumentów typu, uzupełnionych jako `any`), `unchecked` (asercja `as` na wartości typu `any`), `deprecation` (wywołanie sygnatury z tagiem `@deprecated`; w TypeScripcie przestarzałe może być pojedyncze przeciążenie, jak `basePrice(int)` w Javie), `fallthrough` (TS7029). Build projektu też ma `noFallthroughCasesInSwitch`, więc w `start`, `step1` i `step2` przelot jest wyciszony `// @ts-expect-error` - a bramka świadomie nie honoruje tych dyrektyw.

### Co widzimy

`CompilerGate.check(dir)` kompiluje wszystkie pliki `.ts` wariantu przez `typescript-api` i zwraca `Result` z werdyktem i strukturalną diagnostyką (`Warning`: kategoria, plik, linia). Host kompilatora podmienia dyrektywy `@ts-expect-error`/`@ts-ignore` przed kompilacją, więc wyciszenie w kodzie nie przepuszcza ostrzeżenia. Kod `start` nie przechodzi: `SeatMap` na surowym `Map` z asercjami `as` (rawtypes, unchecked), `OccupancyReport` woła przestarzałe przeciążenie `PriceTable.basePrice(format: number)` (deprecation) i celowo przelatuje z IMAX do 3D w `switch` (fallthrough).

```typescript
private readonly seatsByRow = new Map();
...
switch (format) {
  // @ts-expect-error TS7029 - celowy przelot: IMAX ma też dźwięk Dolby
  case 3:
    features = features + 'duzy ekran, ';
  case 2:
    features = features + 'dzwiek Dolby';
    break;
```

### Krok 1: argumenty typu zamiast surowego Map

**Zmiany:**

1. W `SeatMap` dopisz argumenty typu na polu: `new Map<number, string[]>()` zamiast surowego `new Map()`.
2. W `take` zastąp sprawdzanie `undefined` wyrażeniem `this.seatsByRow.get(row) ?? []` i usuń asercję `as string[] | undefined`.
3. W `takenPerRow` zbuduj wynik z posortowanych wpisów mapy bez asercji `as number` i `as string[]` - kompilator zna już typy. (`OccupancyReport` i `PriceTable` bez zmian; w snapshotach zmieniają się tylko ich komentarze wariantów.)

**Przed (`SeatMap.ts`):**

```typescript
export class SeatMap {
  private readonly seatsByRow = new Map();

  take(seat: string): void {
    const row = Number.parseInt(seat.substring(1), 10);
    let seats = this.seatsByRow.get(row) as string[] | undefined;
    if (seats === undefined) {
      seats = [];
      this.seatsByRow.set(row, seats);
    }
    seats.push(seat);
  }

  takenPerRow(): Map<number, number> {
    const result = new Map<number, number>();
    for (const row of [...this.seatsByRow.keys()].sort((a, b) => a - b)) {
      result.set(row as number, (this.seatsByRow.get(row) as string[]).length);
    }
    return result;
  }
}
```

**Po (`SeatMap.ts`):**

```typescript
export class SeatMap {
  private readonly seatsByRow = new Map<number, string[]>();

  take(seat: string): void {
    const row = Number.parseInt(seat.substring(1), 10);
    const seats = this.seatsByRow.get(row) ?? [];
    this.seatsByRow.set(row, seats);
    seats.push(seat);
  }

  takenPerRow(): Map<number, number> {
    return new Map([...this.seatsByRow]
      .sort(([a], [b]) => a - b)
      .map(([row, seats]) => [row, seats.length]));
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s03` - zostają kategorie `deprecation`, `fallthrough`.
**Co powiedzieć:** jedna kategoria ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s03 0 1`

### Krok 2: nowe API zamiast przestarzałego

**Zmiany:**

1. W `OccupancyReport.describe` zamień w `return` argument wywołania `PriceTable.basePrice(format)` na istniejącą już zmienną `name` - wywołanie trafia do przeciążenia `basePrice(format: string)`, a przestarzałe przeciążenie z tagiem `@deprecated` przestaje być używane.
2. Samo przestarzałe przeciążenie `basePrice(format: number)` zostaje w `PriceTable` - usuniemy je dopiero, gdy nikt go nie woła. (To jedyna zmiana kodu w tym kroku; `SeatMap` i `PriceTable` zmieniają w snapshotach tylko komentarze wariantów.)

**Przed:**

```typescript
const name = format === 3 ? 'IMAX' : format === 2 ? '3D' : '2D';
return name + ' [' + features + '], cena ' + PriceTable.basePrice(format)
  + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
```

**Po:**

```typescript
return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
  + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.ts:13` (TypeScript wskazuje `case`, z którego następuje przelot).
**Co powiedzieć:** przestarzałe przeciążenie usuniemy dopiero, gdy nikt go nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s03 1 2`

### Krok 3: funkcja z return w każdym case zamiast przelotu

**Zmiany:**

1. Wydziel cały `switch` z `describe` do prywatnej statycznej metody `features(format: number): string` (Extract Method); TypeScript nie ma wyrażenia `switch`, więc to zwykła metoda.
2. W każdym `case` zamień przypisania do `features` na `return`; przelot zapisany wprost: `case 3: return 'duzy ekran, dzwiek Dolby'`, `case 2: return 'dzwiek Dolby'`, `default: return 'standard'`.
3. Usuń dyrektywę `// @ts-expect-error` - znika razem z przelotem; w `describe` zostaje `const features = OccupancyReport.features(format);`. (Metoda `show` i pozostałe pliki bez zmian.)

**Przed (`OccupancyReport.ts`, metoda `describe`):**

```typescript
describe(map: SeatMap, format: number): string {
  let features = '';
  switch (format) {
    // @ts-expect-error TS7029 - celowy przelot: IMAX ma też dźwięk Dolby
    case 3:
      features = features + 'duzy ekran, ';
    case 2:
      features = features + 'dzwiek Dolby';
      break;
    default:
      features = 'standard';
  }
  const name = format === 3 ? 'IMAX' : format === 2 ? '3D' : '2D';
  return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
    + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
}
```

**Po (`OccupancyReport.ts`, metody `describe` i `features`):**

```typescript
describe(map: SeatMap, format: number): string {
  const features = OccupancyReport.features(format);
  const name = format === 3 ? 'IMAX' : format === 2 ? '3D' : '2D';
  return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
    + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
}

private static features(format: number): string {
  switch (format) {
    case 3: return 'duzy ekran, dzwiek Dolby';
    case 2: return 'dzwiek Dolby';
    default: return 'standard';
  }
}
```

**Uruchom:** test zielony; `step3PassesTheGate` - zero ostrzeżeń, bramka przechodzi.
**Co powiedzieć:** intencja "IMAX ma też Dolby" jest teraz w kodzie, a nie w kolejności `case`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s03 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: w Javie z `-Werror` javac przerywa po fazie z pierwszym ostrzeżeniem i pełną listę trzeba zbierać drugim przebiegiem. TypeScript zbiera diagnostyki składniowe i semantyczne w jednym przebiegu, ale część kategorii (surowe typy, niesprawdzone rzutowania, przestarzałe API) nie jest diagnostyką kompilatora - bramka dokłada je własnym przejściem po AST z `TypeChecker` (`getResolvedSignature(...).getJsDocTags()` dla `@deprecated`).

### Pułapki

- Nagłe włączenie ostrzejszych opcji jako błędów w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `// @ts-ignore` albo `// @ts-nocheck` na całym pliku zamiast wąskiego `// @ts-expect-error` z uzasadnieniem (i bramki, która wyciszeń nie honoruje).
- Wersja TypeScriptu bramki (`typescript-api`, 5.9) nie jest wersją, którą buduje projekt - różne wersje potrafią inaczej raportować te same diagnostyki, więc obie trzeba przypiąć w `package-lock.json`.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s04. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Katalog:** `typescript/src/workshop/m2/s04_qualitygate` · **Test:** `scripts/warsztat.sh --lang ts test m2/s04` (`S04EquivalenceTest.test.ts`, `S04SolutionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `QualityGate` w `start` to lista kontrolna w komentarzu TSDoc i metoda, która zawsze zwraca pustą listę, więc przepuszcza brudną próbkę. Zamieniamy listę na cztery wykonywalne sprawdzenia: skan TODO i `console.`, diagnostyki kompilatora, przybliżenie pokrycia i uruchomienie testów.

**Zasada:** Minimalna bramka jakości łączy kilka niezależnych sygnałów, bo każde narzędzie dowodzi czegoś innego. Wynik ma być deterministyczny i wskazywać miejsce, a bramka potrzebuje właściciela i nie może dawać fałszywych alarmów - inaczej zespół ją wyłączy.

**Efekt:** Bramka zgłasza problemy `sample/dirty`, zatrzymuje nieprzechodzący test z `sample/broken` i przepuszcza `sample/clean`. Sprawdzenie pokrycia to tylko przybliżenie ("czy jakiś test w ogóle woła metodę") i nie zastępuje pomiaru `vitest --coverage`.

**Różnica względem Javy:** próbki `sample/clean`, `sample/dirty` i ich testy (`test/workshop/m2/s04_qualitygate/sample/{clean,dirty,broken}`) są wyłączone z typechecku projektu (`exclude` w `typescript/tsconfig.json`), bo brudna próbka celowo nie przechodzi `strict`: parametry bez typu to niejawne `any`, odpowiednik surowego typu. `GateInput.testClass` (nazwa klasy dla `Class.forName`) to `testSuite: TestSuite` - załadowany zestaw przypadków `{ name, tests }`; testy próbek to pliki `PriceTableTest.ts` z asercjami `node:assert/strict` (nie `*.test.ts`, więc vitest ich nie uruchamia), a `S04Fixtures.ts` ładuje je dynamicznym `import()`. `System.out`/`System.err` to `console.`, kompilator to `typescript-api` z opcjami projektu i `noUnused*`, `noImplicitReturns`, a wynik ma postać `kompilator <plik>:<linia> TS<kod>` zamiast klucza javac. Numery linii są inne niż w Javie.

### Co widzimy

`start/QualityGate.ts` to lista kontrolna w komentarzu TSDoc i metoda `evaluate`, która zawsze zwraca pustą listę - przepuszcza próbkę `sample/dirty` (TODO, `console.log`, parametry bez typu, metody bez testu). Bramka działa na `GateInput`: katalog źródeł domeny, plik testu, kluczowa klasa i zestaw testów.

```typescript
evaluate(_input: GateInput): string[] {
  return [];
}
```

### Krok 1: skan źródeł - TODO i console

**Zmiany:**

1. W `QualityGate` dodaj prywatną statyczną metodę `scanSources(input, findings)`: dla każdego pliku `.ts` z katalogu `input.sources` i każdej linii z `TODO`/`FIXME` dopisuje wynik `'TODO <plik>:<linia>'`, a dla linii z `console.` - `'console <plik>:<linia>'`.
2. Dodaj pomocnicze metody `sourceFiles(dir)` (posortowana lista plików `.ts` katalogu) i `read(file)` (linie pliku).
3. `evaluate` przestaje zwracać pustą listę - zbiera wyniki `scanSources` do listy `findings`. Plik dostaje importy `readdirSync`, `readFileSync` z `node:fs` i `path` z `node:path`; metoda `passes` i lista kontrolna w komentarzu zostają (komentarz można skrócić do opisu kroku).

**Przed:**

```typescript
evaluate(_input: GateInput): string[] {
  return [];
}
```

**Po (`QualityGate.ts`; metoda `passes` bez zmian):**

```typescript
import { readdirSync, readFileSync } from 'node:fs';
import path from 'node:path';

import type { GateInput } from '../GateInput.js';

export class QualityGate {
  /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.scanSources(input, findings);
    return findings;
  }

  passes(input: GateInput): boolean {
    return this.evaluate(input).length === 0;
  }

  /** TODO/FIXME i wydruki na konsolę w źródłach domeny. */
  private static scanSources(input: GateInput, findings: string[]): void {
    for (const file of QualityGate.sourceFiles(input.sources)) {
      QualityGate.read(file).forEach((line, i) => {
        const where = path.basename(file) + ':' + (i + 1);
        if (line.includes('TODO') || line.includes('FIXME')) {
          findings.push('TODO ' + where);
        }
        if (line.includes('console.')) {
          findings.push('console ' + where);
        }
      });
    }
  }

  private static sourceFiles(dir: string): string[] {
    return readdirSync(dir)
      .filter((name) => name.endsWith('.ts'))
      .sort()
      .map((name) => path.join(dir, name));
  }

  private static read(file: string): string[] {
    return readFileSync(file, 'utf8').split(/\r?\n/);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s04` - `TODO PriceTable.ts:10`, `console PriceTable.ts:20`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 0 1`

### Krok 2: diagnostyki kompilatora

**Zmiany:**

1. Dodaj stałą `COMPILER_OPTIONS` (opcje projektu plus `strict`, `noUnusedLocals`, `noUnusedParameters`, `noImplicitReturns`, `noEmit`) i metodę `compileSources(input, findings)`, która buduje program `ts.createProgram` z pakietu `typescript-api` na plikach domeny.
2. Każdą diagnostykę składniową i semantyczną zamień na wynik `'kompilator <plik>:<linia> TS<kod>'`.
3. W `evaluate` wywołaj `compileSources` przed `scanSources`. Na górze pliku dochodzi `import ts from 'typescript-api'`; pozostałe metody bez zmian.

**Przed:**

```typescript
evaluate(input: GateInput): string[] {
  const findings: string[] = [];
  QualityGate.scanSources(input, findings);
  return findings;
}
```

**Po (`QualityGate.ts`, nowe i zmienione fragmenty):**

```typescript
import ts from 'typescript-api';
...
  private static readonly COMPILER_OPTIONS: ts.CompilerOptions = {
    target: ts.ScriptTarget.ES2023,
    module: ts.ModuleKind.NodeNext,
    moduleResolution: ts.ModuleResolutionKind.NodeNext,
    lib: ['lib.es2023.d.ts', 'lib.dom.d.ts'],
    types: [],
    strict: true,
    noUncheckedIndexedAccess: true,
    exactOptionalPropertyTypes: true,
    noImplicitOverride: true,
    noFallthroughCasesInSwitch: true,
    noUnusedLocals: true,
    noUnusedParameters: true,
    noImplicitReturns: true,
    skipLibCheck: true,
    noEmit: true,
  };

  /** Lista wyników bramki; pusta lista = bramka przepuszcza zmianę. */
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.compileSources(input, findings);
    QualityGate.scanSources(input, findings);
    return findings;
  }

  /** Kompilacja źródeł domeny (typescript-api, strict i opcje "lint"); każda diagnostyka to wynik bramki. */
  private static compileSources(input: GateInput, findings: string[]): void {
    const program = ts.createProgram(QualityGate.sourceFiles(input.sources), QualityGate.COMPILER_OPTIONS);
    for (const sourceFile of program.getRootFileNames().map((name) => program.getSourceFile(name))) {
      if (sourceFile === undefined) {
        continue;
      }
      for (const d of [...program.getSyntacticDiagnostics(sourceFile), ...program.getSemanticDiagnostics(sourceFile)]) {
        const line = sourceFile.getLineAndCharacterOfPosition(d.start ?? 0).line + 1;
        findings.push('kompilator ' + path.basename(sourceFile.fileName) + ':' + line + ' TS' + d.code);
      }
    }
  }
```

**Uruchom:** test zielony; dochodzą dwa wyniki `kompilator PriceTable.ts:28 TS7006` (parametry `what` i `kind` bez typu - niejawne `any`).
**Co powiedzieć:** to ta sama idea co w s03, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**Zmiany:**

1. Dodaj stałą `PUBLIC_METHOD` - wyrażenie regularne wyszukujące na poziomie wcięcia składowej metody bez `private`/`protected` (konstruktor pominięty) - i metodę `checkCoverage(input, findings)`, która czyta plik `input.keyClass + '.ts'` oraz plik testu `input.testSource`.
2. Każda publiczna metoda, której wywołania (`.` + nazwa + `(`) nie ma w tekście testu, daje wynik `'pokrycie <klasa>.<metoda> bez testu'`.
3. W `evaluate` dołóż `checkCoverage` po `scanSources`; pozostałe metody bez zmian.

**Przed:**

```typescript
evaluate(input: GateInput): string[] {
  const findings: string[] = [];
  QualityGate.compileSources(input, findings);
  QualityGate.scanSources(input, findings);
  return findings;
}
```

**Po (`QualityGate.ts`, nowe i zmienione fragmenty):**

```typescript
  // Metoda publiczna na poziomie klasy: bez private/protected, poza konstruktorem.
  private static readonly PUBLIC_METHOD = /^ {2}(?:public\s+)?(?:static\s+)?(?!constructor\b)([A-Za-z_]\w*)\s*\(/gm;
...
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.compileSources(input, findings);
    QualityGate.scanSources(input, findings);
    QualityGate.checkCoverage(input, findings);
    return findings;
  }

  /** Przybliżenie pokrycia: publiczna metoda kluczowej klasy musi być wywołana w jej teście. */
  private static checkCoverage(input: GateInput, findings: string[]): void {
    const source = readFileSync(path.join(input.sources, input.keyClass + '.ts'), 'utf8');
    const test = readFileSync(input.testSource, 'utf8');
    for (const method of source.matchAll(QualityGate.PUBLIC_METHOD)) {
      if (!test.includes('.' + method[1]! + '(')) {
        findings.push('pokrycie ' + input.keyClass + '.' + method[1]! + ' bez testu');
      }
    }
  }
```

**Uruchom:** test zielony; `vipSurcharge` i `lookupCount` bez testu.
**Co powiedzieć:** to nie zastępuje pomiaru pokrycia (`vitest run --coverage`), ale łapie metodę, której żaden test nawet nie woła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 2 3`

### Krok 4: testy zielone

**Zmiany:**

1. Dodaj metodę `runTests(input, findings)`: dla każdego przypadku z `input.testSuite.tests` (nazwy posortowane) wywołuje funkcję przypadku, a złapany wyjątek zamienia na wynik `'test <zestaw>.<nazwa> nie przechodzi: <nazwa błędu>'` - bez zależności od vitest.
2. W `evaluate` wywołaj `runTests` jako pierwsze z czterech sprawdzeń; pozostałe metody bez zmian.

**Przed:**

```typescript
evaluate(input: GateInput): string[] {
  const findings: string[] = [];
  QualityGate.compileSources(input, findings);
  QualityGate.scanSources(input, findings);
  QualityGate.checkCoverage(input, findings);
  return findings;
}
```

**Po (`QualityGate.ts`, nowe i zmienione fragmenty):**

```typescript
  evaluate(input: GateInput): string[] {
    const findings: string[] = [];
    QualityGate.runTests(input, findings);
    QualityGate.compileSources(input, findings);
    QualityGate.scanSources(input, findings);
    QualityGate.checkCoverage(input, findings);
    return findings;
  }

  /** Uruchamia przypadki zestawu testów (bez zależności od vitest) i zgłasza porażki. */
  private static runTests(input: GateInput, findings: string[]): void {
    const suite = input.testSuite;
    for (const name of Object.keys(suite.tests).sort()) {
      try {
        suite.tests[name]!();
      } catch (failure) {
        findings.push('test ' + suite.name + '.' + name + ' nie przechodzi: ' + (failure as Error).name);
      }
    }
  }
```

**Uruchom:** test zielony; `step4AddsGreenTestsAndCatchesAFailingOne` - fikstura `sample/broken` (celowo nie plik `*.test.ts`, żeby nie psuć przebiegu vitest) daje wynik `test PriceTableTest.vipSurchargeStartsAtRowNine nie przechodzi: AssertionError`.
**Co powiedzieć:** lista kontrolna jest w całości wykonywalna i ma ten sam wynik u każdego.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S04EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Katalog:** `typescript/src/workshop/m2/s05_boyscout` · **Test:** `scripts/warsztat.sh --lang ts test m2/s05` (`S05EquivalenceTest.test.ts`, `S05SolutionTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPrinter.print` i tak otwieramy dla nowej linii "Sala", a kod ma nazwy `s`, `d`, `x` i ręczne sklejanie listy miejsc. Najpierw pokazujemy antyprzykład "skoro już tu jestem", który po cichu zmienia wydruk, a potem robimy małą poprawę tylko w tej metodzie.

**Zasada:** Boy Scout Rule mówi: dotykany kod zostaw w nieco lepszym stanie - to heurystyka, nie licencja na przebudowę. Poprawa jest mała, lokalna, dotyczy kodu bieżącej zmiany i nie zmienia zachowania; zmiana reguły biznesowej pod etykietą sprzątania to nadużycie.

**Efekt:** Metoda ma czytelne nazwy, `join`, tablicę linii i `phoneLine`, a test równoważności potwierdza identyczny wydruk. Sortowanie miejsc czy małe litery w e-mailu mogą być dobrymi pomysłami, ale trafiają do osobnego commita jako świadoma zmiana zachowania.

**Różnica względem Javy:** javowy `StringBuilder` zamiast konkatenacji ma w TypeScripcie idiomatyczny odpowiednik: tablicę linii złączoną `join` (w kroku 2 literał tablicy z szablonami tekstowymi). Antyprzykład w kroku 1 jest taki sam jak w Javie: sortowanie miejsc, `toLowerCase()` e-maila i pominięta linia "Tel".

### Co widzimy

`start/TicketPrinter.ts` - metodę `print` trzeba i tak otworzyć, bo w sprincie dochodzi linia "Sala". Kod ma nazwy `s`, `d`, `x`, konkatenację w pętli i ręczne sklejanie listy miejsc. Kusi, żeby "posprzątać wszystko".

```typescript
let x = '';
for (let i = 0; i < t.seats.length; i++) {
  if (i > 0) {
    x = x + ', ';
  }
  x = x + t.seats[i]!;
}
```

### Krok 1: nadużycie - "skoro już tu jestem"

**Zmiany:**

1. Antyprzykładu nie piszemy na żywo - wstaw gotowy snapshot: `scripts/warsztat.sh --lang ts jump m2/s05 1`.
2. W `print` obok dobrych ruchów (nazwa `ticket`, tablica linii, `join`) przemycono trzy zmiany zachowania: miejsca są sortowane (`[...ticket.seats].sort()`), e-mail przechodzi na małe litery (`toLowerCase()`), a linia "Tel" znika, gdy klient nie podał telefonu.
3. Dodatkowo kwota jest drukowana przez `Money` zamiast `toFixed(2)` - stąd importy `Decimal` z `decimal.js` i `Money` z `../../../shared/Money.js`.

**Przed (`TicketPrinter.ts`):**

```typescript
import type { Ticket } from '../Ticket.js';

export class TicketPrinter {
  print(t: Ticket): string {
    let s = '';
    s = s + 'Film: ' + t.title + '\n';
    const d = t.start.toLocalDate().toString() + ' ' + t.start.toLocalTime().toString();
    s = s + 'Seans: ' + d + '\n';
    let x = '';
    for (let i = 0; i < t.seats.length; i++) {
      if (i > 0) {
        x = x + ', ';
      }
      x = x + t.seats[i]!;
    }
    s = s + 'Miejsca: ' + x + '\n';
    s = s + 'Klient: ' + t.email.trim() + '\n';
    if (t.phone != null) {
      s = s + 'Tel: ' + t.phone + '\n';
    } else {
      s = s + 'Tel: -\n';
    }
    s = s + 'Do zaplaty: ' + t.total.toFixed(2) + '\n';
    return s;
  }
}
```

**Po (`TicketPrinter.ts`):**

```typescript
import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { Ticket } from '../Ticket.js';

export class TicketPrinter {
  print(ticket: Ticket): string {
    const lines: string[] = [];
    lines.push(`Film: ${ticket.title}`);
    lines.push(`Seans: ${ticket.start.toLocalDate().toString()} ${ticket.start.toLocalTime().toString()}`);
    const sortedSeats = [...ticket.seats].sort();
    lines.push(`Miejsca: ${sortedSeats.join(', ')}`);
    lines.push(`Klient: ${ticket.email.trim().toLowerCase()}`);
    if (ticket.phone != null) {
      lines.push(`Tel: ${ticket.phone}`);
    }
    lines.push(`Do zaplaty: ${new Money(new Decimal(ticket.total)).toString()}`);
    return lines.map((line) => line + '\n').join('');
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest` → `abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh --lang ts reset m2/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s05 0 1`

### Krok 2: poprawna, mała poprawa

**Zmiany:**

1. Pracujemy znów na `start` (krok 1 został cofnięty). Zmień nazwę parametru `t` na `ticket` (Rename); zmienne `s`, `d` i `x` znikają razem z konkatenacją.
2. Pętlę sklejającą miejsca zastąp wyrażeniem `ticket.seats.join(', ')`.
3. Zbuduj wydruk z literału tablicy linii złączonego `.map((line) => line + '\n').join('')` zamiast doklejania do zmiennej.
4. Wydziel linię telefonu do prywatnej statycznej metody `phoneLine(ticket)` (Extract Method) - zachowanie `Tel: -` bez telefonu zostaje bez zmian. Import `Ticket` zostaje jedynym importem pliku.

**Przed (`TicketPrinter.ts`):** kod `start` - pełna metoda `print` jest w bloku Przed kroku 1.

**Po (`TicketPrinter.ts`):**

```typescript
import type { Ticket } from '../Ticket.js';

export class TicketPrinter {
  print(ticket: Ticket): string {
    return [
      `Film: ${ticket.title}`,
      `Seans: ${ticket.start.toLocalDate().toString()} ${ticket.start.toLocalTime().toString()}`,
      `Miejsca: ${ticket.seats.join(', ')}`,
      `Klient: ${ticket.email.trim()}`,
      TicketPrinter.phoneLine(ticket),
      `Do zaplaty: ${ticket.total.toFixed(2)}`,
    ].map((line) => line + '\n').join('');
  }

  private static phoneLine(ticket: Ticket): string {
    return 'Tel: ' + (ticket.phone != null ? ticket.phone : '-');
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tylko dotykana metoda, bez zmiany kontraktu, diff do przejrzenia w minutę. Nowa linia "Sala" to osobny commit (patrz s06).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Katalog:** `typescript/src/workshop/m2/s06_reviewableseries` · **Test:** `scripts/warsztat.sh --lang ts test m2/s06` (`S06EquivalenceTest.test.ts`, `S06SolutionTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `PriceList.price` ma dostać "Tani wtorek" (NORMAL -20% we wtorek), a zniżki są wplecione w jedną metodę i nie widzą daty. Zamiast jednego commita "porządki + tani wtorek" robimy serię: Extract Method, Change Signature, a dopiero potem nowa reguła.

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build po integracji. Refaktoryzację oddziela się od zmiany funkcjonalnej, formatowanie od logiki, a diff automatyczny od ręcznego - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Efekt:** Dwa pierwsze commity dowodzi test równoważności, a trzeci świadomie zmienia zachowanie: test różnicowy pokazuje, że zmieniło się dokładnie 12 przypadków, wszystkie NORMAL we wtorek. Kosztem jest więcej commitów, ale recenzent sprawdza w każdym tylko jedną rzecz.

**Różnica względem Javy:** start liczy na `Decimal` z biblioteki `decimal.js` (odpowiednik `BigDecimal`), a wydzielone metody to `private static` z instrukcją `switch` i `return` w każdym `case` (TypeScript nie ma wyrażenia `switch`).

### Co widzimy

`start/PriceList.ts` - metoda `price` ma dostać promocję "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek. Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu, a nowa reguła potrzebuje daty. Pokusa: jeden commit "porządki + tani wtorek".

```typescript
let d: Decimal;
if (q.type === 'STUDENT') {
  d = p.times(new Decimal('0.25'));
} else if ...
```

### Krok 1: commit 1 - refaktoryzacja (Extract Method)

**Zmiany:**

1. Wydziel wybór ceny bazowej do prywatnej statycznej metody `basePrice(format: string): Money` z `switch` i `return` w każdym `case` (Extract Method).
2. Wydziel wybór zniżki do `discountPercent(type: string): number` zwracającej procent (Extract Method).
3. Przejdź z `Decimal` na `Money`: `Money.of('40.00')` itd., zniżka to `base.minus(base.percent(...))`, a rabat poranny i dopłata VIP to `Money.of('5.00')` i `Money.of('10.00')`. Parametr `q` zmienia nazwę na `query` (Rename); import `Decimal` z `decimal.js` znika.

**Przed (`PriceList.ts`):**

```typescript
import { Decimal } from 'decimal.js';

import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

export class PriceList {
  price(q: TicketQuery): Money {
    let p: Decimal;
    if (q.format === 'IMAX') {
      p = new Decimal('40.00');
    } else if (q.format === '3D') {
      p = new Decimal('32.00');
    } else {
      p = new Decimal('25.00');
    }
    let d: Decimal;
    if (q.type === 'STUDENT') {
      d = p.times(new Decimal('0.25'));
    } else if (q.type === 'SENIOR') {
      d = p.times(new Decimal('0.30'));
    } else if (q.type === 'CHILD') {
      d = p.times(new Decimal('0.40'));
    } else {
      d = new Decimal(0);
    }
    p = p.minus(d);
    if (q.start.hour < 12) {
      p = p.minus(new Decimal('5.00'));
    }
    if (q.row >= 10) {
      p = p.plus(new Decimal('10.00'));
    }
    return new Money(p);
  }
}
```

**Po (`PriceList.ts`):**

```typescript
import { Money } from '../../../shared/Money.js';
import type { TicketQuery } from '../TicketQuery.js';

export class PriceList {
  price(query: TicketQuery): Money {
    const base = PriceList.basePrice(query.format);
    let price = base.minus(base.percent(PriceList.discountPercent(query.type)));
    if (query.start.hour < 12) {
      price = price.minus(Money.of('5.00'));
    }
    if (query.row >= 10) {
      price = price.plus(Money.of('10.00'));
    }
    return price;
  }

  private static basePrice(format: string): Money {
    switch (format) {
      case 'IMAX': return Money.of('40.00');
      case '3D': return Money.of('32.00');
      default: return Money.of('25.00');
    }
  }

  private static discountPercent(type: string): number {
    switch (type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return 0;
    }
  }
}
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**Zmiany:**

1. Zmień sygnaturę `discountPercent`: zamiast `type: string` metoda przyjmuje całe `query: TicketQuery` (Change Signature, ręcznie), bo następna reguła potrzebuje daty.
2. W ciele metody `switch (query.type)`, w `price` wywołanie `discountPercent(query)`; pominięte wywołania pokaże `npm run typecheck`.

**Przed:**

```typescript
private static discountPercent(type: string): number {
  switch (type) {
    case 'STUDENT': return 25;
    case 'SENIOR': return 30;
    case 'CHILD': return 40;
    default: return 0;
  }
}
```

**Po (`PriceList.ts`, zmienione fragmenty):**

```typescript
    let price = base.minus(base.percent(PriceList.discountPercent(query)));
...
  private static discountPercent(query: TicketQuery): number {
    switch (query.type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return 0;
    }
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** "make the change easy, then make the easy change". Ten commit nadal nie zmienia zachowania.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**Zmiany:**

1. Dodaj stałą `private static readonly CHEAP_TUESDAY_PERCENT = 20` i prywatną statyczną metodę `isCheapTuesday(query)` sprawdzającą `query.start.dayOfWeek === 'TUESDAY'`.
2. W gałęzi `default` metody `discountPercent` zwróć `PriceList.isCheapTuesday(query) ? PriceList.CHEAP_TUESDAY_PERCENT : 0` zamiast `0`. Metody `price` i `basePrice` bez zmian.

**Przed:**

```typescript
private static discountPercent(query: TicketQuery): number {
  switch (query.type) {
    case 'STUDENT': return 25;
    case 'SENIOR': return 30;
    case 'CHILD': return 40;
    default: return 0;
  }
}
```

**Po (`PriceList.ts`, nowe i zmienione fragmenty):**

```typescript
export class PriceList {
  private static readonly CHEAP_TUESDAY_PERCENT = 20;
...
  private static discountPercent(query: TicketQuery): number {
    switch (query.type) {
      case 'STUDENT': return 25;
      case 'SENIOR': return 30;
      case 'CHILD': return 40;
      default: return PriceList.isCheapTuesday(query) ? PriceList.CHEAP_TUESDAY_PERCENT : 0;
    }
  }

  private static isCheapTuesday(query: TicketQuery): boolean {
    return query.start.dayOfWeek === 'TUESDAY';
  }
}
```

**Uruchom:** test zielony; `S06SolutionTest` → `cheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `behaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s06 2 3`

### Rozwiązanie i uzasadnienie

Trzy zestawy zmian, każdy z jedną intencją i zielonym buildem. Refaktoryzacje dowodzi test równoważności, zmianę zachowania - test nowej reguły i test różnicowy ograniczający jej zasięg.

### Pułapki

- Łączenie refaktoryzacji ze zmianą zachowania: recenzent nie odróżni ruchu mechanicznego od nowej reguły.
- Łączenie formatowania z logiką, diffu automatycznego z ręcznym.
- Commit przygotowawczy, który "przy okazji" poprawia regułę.

### Pytanie do sali

Jak opisalibyście commit 3 według pięciu pytań (dlaczego, co i czego nie, kontrakt, dowody, wdrożenie i wycofanie)?

## Scena s07. SRP - raport dla dwóch aktorów

**Temat ze slajdów:** 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany
**Katalog:** `typescript/src/workshop/m2/s07_srp` · **Test:** `scripts/warsztat.sh --lang ts test m2/s07` (`S07EquivalenceTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `DailyReport` składa raport dla księgowości i marketingu, a wspólny helper `revenue` sprawia, że zmiana dla jednego aktora cicho zmienia kwoty drugiego. Wydzielamy sekcje, rozdzielamy helper i tworzymy klasę na każdego aktora.

**Zasada:** SRP mówi, że moduł powinien odpowiadać przed jednym aktorem, czyli mieć jeden powód zmiany. To nie jest "klasa robi jedną rzecz" ani jedna metoda na klasę - klasa może mieć kilka metod, jeśli wszystkie zmieniają się dla tego samego aktora.

**Efekt:** Zmiana definicji hitu dotyka tylko `MarketingSection`, a zmiana stawki VAT tylko `AccountingSection`. Koszt: dwie identyczne dziś formuły, powtórzone świadomie, bo to kod, a nie wspólna wiedza.

**Różnica względem Javy:** `StringBuilder` to konkatenacja `out += ...`, a `TreeMap.merge` to zwykła `Map` plus sortowanie wpisów po tytule przed wyborem hitu (kolejność alfabetyczna rozstrzyga remis tak samo jak w Javie).

### Co widzimy

`DailyReport.render` składa raport dla księgowości (przychód brutto i netto wg VAT 8% i 23%) i dla marketingu (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera `revenue`. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera po cichu zmieni też "Razem brutto" dla księgowości.

```typescript
private revenue(sale: Sale): Decimal {
  return sale.ticketRevenue.plus(sale.barRevenue);
}
...
total = total.plus(this.revenue(sale));                                                          // księgowość
byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.revenue(sale)));  // marketing
```

### Krok 1: Extract Method według aktora

**Zmiany:**

1. Wydziel blok pod komentarzem `// księgowość` do prywatnej metody `accountingSection(sales): string` z własną zmienną `out` (Extract Method).
2. To samo dla bloku pod `// marketing` - metoda `marketingSection(sales): string`.
3. `render` zwraca sklejenie wyników obu metod; komentarze `// księgowość` i `// marketing` znikają - zastępują je nazwy metod. Helpery `revenue` i `net` zostają bez zmian.

**Przed (`DailyReport.ts`, metoda `render`):**

```typescript
render(sales: readonly Sale[]): string {
  let out = '';
  // księgowość
  let tickets = new Decimal('0.00');
  let bar = new Decimal('0.00');
  for (const sale of sales) {
    tickets = tickets.plus(sale.ticketRevenue);
    bar = bar.plus(sale.barRevenue);
  }
  out += 'KSIEGOWOSC\n';
  out += `Bilety brutto ${tickets.toFixed(2)}, netto ${this.net(tickets, 8).toFixed(2)}\n`;
  out += `Bar brutto ${bar.toFixed(2)}, netto ${this.net(bar, 23).toFixed(2)}\n`;
  let total = new Decimal('0.00');
  for (const sale of sales) {
    total = total.plus(this.revenue(sale));
  }
  out += `Razem brutto ${total.toFixed(2)}\n`;
  // marketing
  out += 'MARKETING\n';
  const byTitle = new Map<string, Decimal>();
  let sold = 0;
  for (const sale of sales) {
    byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.revenue(sale)));
    sold += sale.tickets;
  }
  let hit = 'brak';
  let best = new Decimal(0);
  // tytuły w kolejności alfabetycznej - przy remisie wygrywa pierwszy
  for (const [title, value] of [...byTitle.entries()].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))) {
    if (value.comparedTo(best) > 0) {
      hit = `${title} (${value.toFixed(2)})`;
      best = value;
    }
  }
  out += `Hit dnia: ${hit}\n`;
  out += `Sprzedanych biletow: ${sold}\n`;
  return out;
}
```

**Po (`DailyReport.ts`, metody `render`, `accountingSection` i `marketingSection`; `revenue` i `net` bez zmian):**

```typescript
render(sales: readonly Sale[]): string {
  return this.accountingSection(sales) + this.marketingSection(sales);
}

private accountingSection(sales: readonly Sale[]): string {
  let out = '';
  let tickets = new Decimal('0.00');
  let bar = new Decimal('0.00');
  for (const sale of sales) {
    tickets = tickets.plus(sale.ticketRevenue);
    bar = bar.plus(sale.barRevenue);
  }
  out += 'KSIEGOWOSC\n';
  out += `Bilety brutto ${tickets.toFixed(2)}, netto ${this.net(tickets, 8).toFixed(2)}\n`;
  out += `Bar brutto ${bar.toFixed(2)}, netto ${this.net(bar, 23).toFixed(2)}\n`;
  let total = new Decimal('0.00');
  for (const sale of sales) {
    total = total.plus(this.revenue(sale));
  }
  out += `Razem brutto ${total.toFixed(2)}\n`;
  return out;
}

private marketingSection(sales: readonly Sale[]): string {
  let out = '';
  out += 'MARKETING\n';
  const byTitle = new Map<string, Decimal>();
  let sold = 0;
  for (const sale of sales) {
    byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.revenue(sale)));
    sold += sale.tickets;
  }
  let hit = 'brak';
  let best = new Decimal(0);
  // tytuły w kolejności alfabetycznej - przy remisie wygrywa pierwszy
  for (const [title, value] of [...byTitle.entries()].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))) {
    if (value.comparedTo(best) > 0) {
      hit = `${title} (${value.toFixed(2)})`;
      best = value;
    }
  }
  out += `Hit dnia: ${hit}\n`;
  out += `Sprzedanych biletow: ${sold}\n`;
  return out;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `revenue` nadal wiąże oba światy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**Zmiany:**

1. Skopiuj metodę `revenue` i nazwij kopię `popularity`; ciało na razie identyczne (`sale.ticketRevenue.plus(sale.barRevenue)`).
2. W `marketingSection` zamień wywołanie `this.revenue(sale)` na `this.popularity(sale)`; w `accountingSection` zostaje `revenue`.
3. Nad każdą z metod dodaj komentarz dokumentacyjny (`/** ... */`) z właścicielem: księgowość dla `revenue`, marketing dla `popularity`.

**Przed:**

```typescript
byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.revenue(sale)));
...
private revenue(sale: Sale): Decimal {
  return sale.ticketRevenue.plus(sale.barRevenue);
}
```

**Po (`DailyReport.ts`, zmienione fragmenty; reszta pliku bez zmian):**

```typescript
    byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.popularity(sale)));
...
  /** Księgowość: przychód brutto seansu. */
  private revenue(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  /** Marketing: miara popularności filmu (dziś: bilety + bar). */
  private popularity(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z DRY (slajd 2.6: dwie identyczne dziś formuły to nie duplikacja wiedzy) w kontekście SRP.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**Zmiany:**

1. Nowy plik `AccountingSection.ts`: klasa `AccountingSection` z metodami `render` (dawna `accountingSection`), `revenue` i `net`, przeniesionymi z `DailyReport` (Extract Class, ręcznie).
2. Nowy plik `MarketingSection.ts`: klasa `MarketingSection` z metodami `render` (dawna `marketingSection`) i `popularity`.
3. W `DailyReport` zostają dwa pola (`accounting = new AccountingSection()`, `marketing = new MarketingSection()`) i delegujące `render`; metody sekcji, `revenue`, `popularity` i `net` znikają z `DailyReport` razem z importem `Decimal`.

**Przed (`DailyReport.ts`, szkic; pełne ciała metod pokazują bloki kroków 1 i 2):**

```typescript
export class DailyReport {
  render(sales: readonly Sale[]): string {
    return this.accountingSection(sales) + this.marketingSection(sales);
  }

  private accountingSection(sales: readonly Sale[]): string { ... }

  private marketingSection(sales: readonly Sale[]): string { ... }

  /** Księgowość: przychód brutto seansu. */
  private revenue(sale: Sale): Decimal { ... }

  /** Marketing: miara popularności filmu (dziś: bilety + bar). */
  private popularity(sale: Sale): Decimal { ... }

  private net(gross: Decimal, vatPercent: number): Decimal { ... }
}
```

**Po (`AccountingSection.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/** Aktor: księgowość. Zmienia się, gdy zmieniają się stawki VAT lub wymogi sprawozdań. */
export class AccountingSection {
  render(sales: readonly Sale[]): string {
    let out = '';
    let tickets = new Decimal('0.00');
    let bar = new Decimal('0.00');
    for (const sale of sales) {
      tickets = tickets.plus(sale.ticketRevenue);
      bar = bar.plus(sale.barRevenue);
    }
    out += 'KSIEGOWOSC\n';
    out += `Bilety brutto ${tickets.toFixed(2)}, netto ${this.net(tickets, 8).toFixed(2)}\n`;
    out += `Bar brutto ${bar.toFixed(2)}, netto ${this.net(bar, 23).toFixed(2)}\n`;
    let total = new Decimal('0.00');
    for (const sale of sales) {
      total = total.plus(this.revenue(sale));
    }
    out += `Razem brutto ${total.toFixed(2)}\n`;
    return out;
  }

  private revenue(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }

  private net(gross: Decimal, vatPercent: number): Decimal {
    return gross.times(100)
      .dividedBy(100 + vatPercent).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
  }
}
```

**Po (`MarketingSection.ts`, nowy plik):**

```typescript
import { Decimal } from 'decimal.js';

import type { Sale } from '../Sale.js';

/** Aktor: marketing. Zmienia się, gdy marketing zmienia definicję "hitu". */
export class MarketingSection {
  render(sales: readonly Sale[]): string {
    let out = '';
    out += 'MARKETING\n';
    const byTitle = new Map<string, Decimal>();
    let sold = 0;
    for (const sale of sales) {
      byTitle.set(sale.title, (byTitle.get(sale.title) ?? new Decimal(0)).plus(this.popularity(sale)));
      sold += sale.tickets;
    }
    let hit = 'brak';
    let best = new Decimal(0);
    // tytuły w kolejności alfabetycznej - przy remisie wygrywa pierwszy
    for (const [title, value] of [...byTitle.entries()].sort(([a], [b]) => (a < b ? -1 : a > b ? 1 : 0))) {
      if (value.comparedTo(best) > 0) {
        hit = `${title} (${value.toFixed(2)})`;
        best = value;
      }
    }
    out += `Hit dnia: ${hit}\n`;
    out += `Sprzedanych biletow: ${sold}\n`;
    return out;
  }

  private popularity(sale: Sale): Decimal {
    return sale.ticketRevenue.plus(sale.barRevenue);
  }
}
```

**Po (`DailyReport.ts`):**

```typescript
import type { Sale } from '../Sale.js';
import { AccountingSection } from './AccountingSection.js';
import { MarketingSection } from './MarketingSection.js';

export class DailyReport {
  private readonly accounting = new AccountingSection();
  private readonly marketing = new MarketingSection();

  render(sales: readonly Sale[]): string {
    return this.accounting.render(sales) + this.marketing.render(sales);
  }
}
```

**Uruchom:** test zielony - dokument identyczny co do znaku.
**Co powiedzieć:** `DailyReport` koordynuje, ale nie zna polityk. Zmiana definicji hitu dotyka tylko `MarketingSection`, zmiana stawki VAT tylko `AccountingSection`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości. Obie sekcje są eksportowane (w Javie byłyby package-private) - TypeScript nie ma widoczności pakietowej, więc granicę "tylko dla `DailyReport`" wyznacza tu konwencja, a w większym projekcie reguła importów (jak w s10).

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w module `reportUtils.ts`) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji
**Katalog:** `typescript/src/workshop/m2/s08_dip` · **Test:** `scripts/warsztat.sh --lang ts test m2/s08` (`S08EquivalenceTest.test.ts`, `S08DependencyDirectionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Przypadek użycia `ConfirmReservation` sam tworzy klienta SMTP i zna protokół, więc import i wywołania biegną `app -> infra`. Wstrzykujemy zależność, nazywamy potrzebę polityki i wprowadzamy port `CustomerNotifier` z adapterem w `infra`.

**Zasada:** DIP mówi, że zależności źródłowe mają wskazywać od szczegółów ku polityce i abstrakcjom, a port nazywa potrzebę klienta, nie kształt technologii. DIP to nie dependency injection: wstrzyknięcie konkretnej klasy przez konstruktor wciąż wiąże politykę ze szczegółem.

**Efekt:** Sterowanie nadal płynie do `infra`, ale import odwrócił się na `infra -> app`, a politykę da się przetestować literałem obiektu. Wystarczyła ręczna funkcja `confirmReservation()` w `Main.ts` jako composition root, bez kontenera DI.

**Różnica względem Javy:** `S08DependencyDirectionTest` zamiast refleksji po typach pól czyta importy pliku `app/ConfirmReservation.ts` i sprawdza, czy któryś zaczyna się od `../infra/` (liczy się też `import type`). `isAssignableFrom` zastępuje przypisanie adaptera do typu portu (sprawdza kompilator) oraz sprawdzenie, że plik adaptera importuje `../app/CustomerNotifier.js` - to kierunek zależności źródłowej `infra -> app`. Fake portu to literał obiektu zamiast lambdy.

### Co widzimy

Przypadek użycia `app/ConfirmReservation` sam tworzy klienta `infra/SmtpMailSender`, składa nagłówki MIME i interpretuje kody SMTP. Import i przepływ sterowania biegną w tę samą stronę: `app -> infra`. `Main.ts` to composition root wariantu - test woła tylko funkcję `confirmReservation()`.

```typescript
private readonly mail = new SmtpMailSender('smtp.kino.pl', 25);
...
const mime = `To: ${reservation.email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
const reply = this.mail.send(reservation.email, mime);
if (!reply.startsWith('250')) {
  throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
}
```

### Krok 1: Introduce Parameter - dependency injection

**Zmiany:**

1. W `ConfirmReservation` zamień pole `private readonly mail = new SmtpMailSender('smtp.kino.pl', 25)` na właściwość parametru konstruktora: `constructor(private readonly mail: SmtpMailSender) {}` (Introduce Parameter, ręcznie).
2. Tworzenie `new SmtpMailSender('smtp.kino.pl', 25)` przenieś do `Main.ts` - funkcja `confirmReservation()` składa teraz `new ConfirmReservation(new SmtpMailSender('smtp.kino.pl', 25))`; wywołanie do poprawy wskaże kompilator.
3. Import `SmtpMailSender` w `ConfirmReservation` staje się `import type`. Metoda `confirm` zostaje bez zmian.

**Przed (`app/ConfirmReservation.ts`, zmienione fragmenty):**

```typescript
import { SmtpMailSender } from '../infra/SmtpMailSender.js';
...
export class ConfirmReservation {
  private readonly mail = new SmtpMailSender('smtp.kino.pl', 25);
```

**Przed (`Main.ts`):**

```typescript
import { ConfirmReservation } from './app/ConfirmReservation.js';

export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation();
}
```

**Po (`app/ConfirmReservation.ts`, zmienione fragmenty):**

```typescript
import type { SmtpMailSender } from '../infra/SmtpMailSender.js';
...
export class ConfirmReservation {
  constructor(private readonly mail: SmtpMailSender) {}
```

**Po (`Main.ts`):**

```typescript
import { ConfirmReservation } from './app/ConfirmReservation.js';
import { SmtpMailSender } from './infra/SmtpMailSender.js';

/** Composition root wariantu: jedyne miejsce, które składa graf obiektów. */
export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation(new SmtpMailSender('smtp.kino.pl', 25));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s08` - 18 testów zielonych.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `import type { SmtpMailSender } from '../infra/SmtpMailSender.js'` nadal stoi w polityce. Test `step1AndStep2PolicyDependsOnInfrastructure` to potwierdza. `import type` znika po kompilacji, ale zależność źródłowa zostaje: zmiana API `SmtpMailSender` psuje politykę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s08 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**Zmiany:**

1. Wydziel składanie MIME, wywołanie `send` i sprawdzenie kodu odpowiedzi do prywatnej metody `notifyCustomer` (Extract Method).
2. Parametry metody nazwij `email` i `message` (Rename); `confirm` kończy się wywołaniem `this.notifyCustomer(reservation.email, message)` i zwróceniem potwierdzenia. Importy i konstruktor bez zmian; zmienia się tylko `ConfirmReservation`.

**Przed (`app/ConfirmReservation.ts`, koniec metody `confirm`):**

```typescript
const mime = `To: ${reservation.email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
const reply = this.mail.send(reservation.email, mime);
if (!reply.startsWith('250')) {
  throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
}
return `potwierdzono: ${reservation.email}`;
```

**Po (`app/ConfirmReservation.ts`, metody `confirm` i `notifyCustomer`):**

```typescript
confirm(reservation: Reservation): string {
  if (reservation.seats < 1) {
    throw new IllegalArgumentError('rezerwacja bez miejsc');
  }
  const message = `Rezerwacja: ${reservation.title}, ${reservation.start.toString()}`
    + `, miejsc: ${reservation.seats}. Zaplac w ciagu 15 minut.`;
  this.notifyCustomer(reservation.email, message);
  return `potwierdzono: ${reservation.email}`;
}

private notifyCustomer(email: string, message: string): void {
  const mime = `To: ${email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
  const reply = this.mail.send(email, mime);
  if (!reply.startsWith('250')) {
    throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie wydzielamy interfejsu z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s08 1 2`

### Krok 3: Port po stronie polityki, adapter w infra

**Zmiany:**

1. Nowy plik `app/CustomerNotifier.ts`: interfejs `CustomerNotifier` z metodą `notifyCustomer(email: string, message: string): void` (Extract Interface z potrzeby polityki, ręcznie).
2. Nowy plik `infra/SmtpCustomerNotifier.ts`: klasa `SmtpCustomerNotifier implements CustomerNotifier` z klientem `SmtpMailSender` w konstruktorze; przenieś do niej metodę `notifyCustomer` z `ConfirmReservation` (Move Method, ręcznie) razem z tłumaczeniem kodu SMTP na `IllegalStateError`.
3. W `ConfirmReservation` parametr konstruktora zmienia typ na `notifier: CustomerNotifier`, a `confirm` woła `this.notifier.notifyCustomer(...)`; import z `infra` znika z polityki, a z importu błędów znika `IllegalStateError` (rzuca go już tylko adapter).
4. W `Main.ts` złóż graf: `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender('smtp.kino.pl', 25)))`; dochodzi import `SmtpCustomerNotifier` z `./infra/SmtpCustomerNotifier.js`.

**Przed:**

```typescript
export class ConfirmReservation {
  constructor(private readonly mail: SmtpMailSender) {}
  ...
  private notifyCustomer(email: string, message: string): void {
    const mime = `To: ${email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
    const reply = this.mail.send(email, mime);
    if (!reply.startsWith('250')) {
      throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
    }
  }
}
```

**Po (`app/CustomerNotifier.ts`, nowy plik):**

```typescript
/**
 * Port wyjściowy należy do polityki i nazywa JEJ potrzebę ("powiadom klienta"),
 * a nie możliwości technologii (brak MIME, hosta, kodów SMTP).
 */
export interface CustomerNotifier {
  notifyCustomer(email: string, message: string): void;
}
```

**Po (`infra/SmtpCustomerNotifier.ts`, nowy plik):**

```typescript
import { IllegalStateError } from '../../../../../shared/errors.js';
import type { CustomerNotifier } from '../app/CustomerNotifier.js';
import type { SmtpMailSender } from './SmtpMailSender.js';

/** Adapter: implementuje port polityki (import infra -> app) i tłumaczy go na protokół. */
export class SmtpCustomerNotifier implements CustomerNotifier {
  constructor(private readonly mail: SmtpMailSender) {}

  notifyCustomer(email: string, message: string): void {
    const mime = `To: ${email}\r\nSubject: Rezerwacja\r\n\r\n${message}`;
    const reply = this.mail.send(email, mime);
    if (!reply.startsWith('250')) {
      throw new IllegalStateError(`SMTP odrzucil: ${reply}`);
    }
  }
}
```

**Po (`app/ConfirmReservation.ts`):**

```typescript
import { IllegalArgumentError } from '../../../../../shared/errors.js';
import type { Reservation } from '../../Reservation.js';
import type { CustomerNotifier } from './CustomerNotifier.js';

export class ConfirmReservation {
  constructor(private readonly notifier: CustomerNotifier) {}

  confirm(reservation: Reservation): string {
    if (reservation.seats < 1) {
      throw new IllegalArgumentError('rezerwacja bez miejsc');
    }
    const message = `Rezerwacja: ${reservation.title}, ${reservation.start.toString()}`
      + `, miejsc: ${reservation.seats}. Zaplac w ciagu 15 minut.`;
    this.notifier.notifyCustomer(reservation.email, message);
    return `potwierdzono: ${reservation.email}`;
  }
}
```

**Po (`Main.ts`):**

```typescript
import { ConfirmReservation } from './app/ConfirmReservation.js';
import { SmtpCustomerNotifier } from './infra/SmtpCustomerNotifier.js';
import { SmtpMailSender } from './infra/SmtpMailSender.js';

/** Composition root wariantu: jedyne miejsce, które zna adapter i składa graf obiektów. */
export function confirmReservation(): ConfirmReservation {
  return new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender('smtp.kino.pl', 25)));
}
```

**Uruchom:** test zielony; `stepsSendTheSameMimeMessage` potwierdza identyczny MIME, `step3PolicyIsTestableWithAHandWrittenFake` testuje politykę literałem obiektu `{ notifyCustomer: (email, message) => ... }`.
**Co powiedzieć:** sterowanie nadal płynie `app -> infra` (confirm woła notifier), ale zależność źródłowa odwróciła się: `infra -> app`. To jest DIP.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s08 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`IllegalStateError`), więc polityka nie zna protokołu. Kontener DI nie był potrzebny - wystarczyła ręczna funkcja w `Main.ts`.

### Pułapki

- "Mamy kontener DI (InversifyJS, NestJS), więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Błąd lub typ technologii (np. błąd sterownika bazy) w sygnaturze portu.

### Pytanie do sali

Narysujcie strzałki importów i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s09. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji
**Katalog:** `typescript/src/workshop/m2/s09_cleanarchitecture` · **Test:** `scripts/warsztat.sh --lang ts test m2/s09` (`S09EquivalenceTest.test.ts`, `S09UseCaseTest.test.ts`)
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, katalogi `app` i `adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste obiekty danych, a composition root składa graf bez reguł biznesowych. Liczy się kierunek importów, a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to osiem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

**Różnica względem Javy:** kwoty są typu `Money` (a nie `Decimal`), bo trafiają do wiersza bazy i komunikatu przez `toString()` - `Money` drukuje zawsze dwa miejsca po przecinku, jak `BigDecimal` w Javie. Wiersz `Object[]` to `readonly unknown[]`, rekordy to klasy z polami `readonly`, a `CinemaApplication` to moduł z funkcją `reservationController(db, outbox)`. Łapanie `IllegalArgumentException`/`IllegalStateException` to `instanceof IllegalArgumentError`/`IllegalStateError` z ponownym rzuceniem pozostałych błędów. Porty w `S09UseCaseTest` implementują literały obiektów zamiast lambd.

### Co widzimy

`ReservationController` obsługuje żądanie "HTTP" (`ReadonlyMap<string, string>`) i robi wszystko: parsuje parametry, liczy cenę (format + VIP), zapisuje wiersz `unknown[]` w `RowStore`, publikuje komunikat w `Outbox`, buduje odpowiedź. `RowStore` i `Outbox` to stabilny "świat zewnętrzny" sceny. Test wchodzi przez `reservationController(db, outbox)` z `CinemaApplication.ts` i porównuje odpowiedź, zapisane wiersze i komunikaty.

```typescript
let id: string;
try {
  id = this.db.insert([email, format, rows.length, total]);
} catch (error) {
  if (error instanceof IllegalStateError) {
    return `503 ${error.message}`;
  }
  throw error;
}
this.outbox.publish('reservation-created', `${id};${email};${total.toString()}`);
return `201 ${id} ${total.toString()}`;
```

### Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja

**Zmiany:**

1. Nowy plik `BookSeatsCommand.ts`: klasa danych z polami `readonly email`, `readonly format` i `readonly rows` (konstruktor zamraża kopię listy); nowy plik `Booking.ts`: klasa danych z polami `readonly id` i `readonly total: Money` - odpowiedniki rekordów.
2. Nowy plik `BookSeats.ts`: klasa `BookSeats` z `db` i `outbox` w konstruktorze oraz metodą `execute(command): Booking` - przenieś do niej z kontrolera wycenę, zapis i publikację (Extract Class, ręcznie); wycenę wydziel w niej jako prywatną metodę `price(command)`, a walidacja "brak miejsc" rzuca `IllegalArgumentError`.
3. `ReservationController` tylko tłumaczy: parsuje parametry, tworzy `BookSeatsCommand`, woła `execute` i mapuje wyjątki (`IllegalArgumentError` na `400`, `IllegalStateError` na `503`); pole `bookSeats` tworzy na razie w konstruktorze.

**Przed (`ReservationController.ts`):**

```typescript
import { IllegalStateError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';

export class ReservationController {
  constructor(private readonly db: RowStore, private readonly outbox: Outbox) {}

  handle(params: ReadonlyMap<string, string>): string {
    const email = params.get('email');
    if (email === undefined || email.trim() === '') {
      return '400 brak email';
    }
    const format = params.get('format') ?? '2D';
    const rows = (params.get('rows') ?? '').split(',')
      .filter((s) => s.trim() !== '').map((s) => Number.parseInt(s, 10));
    if (rows.length === 0) {
      return '400 brak miejsc';
    }
    let base: Money;
    switch (format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    let total = Money.of('0.00');
    for (const row of rows) {
      total = total.plus(base);
      if (row >= 10) {
        total = total.plus(Money.of('10.00'));
      }
    }
    let id: string;
    try {
      id = this.db.insert([email, format, rows.length, total]);
    } catch (error) {
      if (error instanceof IllegalStateError) {
        return `503 ${error.message}`;
      }
      throw error;
    }
    this.outbox.publish('reservation-created', `${id};${email};${total.toString()}`);
    return `201 ${id} ${total.toString()}`;
  }
}
```

**Po (`BookSeatsCommand.ts`, nowy plik):**

```typescript
/** Dane wejściowe przypadku użycia - prosty rekord, bez Map z HTTP. */
export class BookSeatsCommand {
  readonly rows: readonly number[];

  constructor(readonly email: string, readonly format: string, rows: readonly number[]) {
    this.rows = Object.freeze([...rows]);
  }
}
```

**Po (`Booking.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Wynik przypadku użycia. */
export class Booking {
  constructor(readonly id: string, readonly total: Money) {}
}
```

**Po (`BookSeats.ts`, nowy plik):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import type { BookSeatsCommand } from './BookSeatsCommand.js';
import { Booking } from './Booking.js';

/** Przypadek użycia jako jawna orkiestracja jednego celu: wyceń, zapisz, powiadom. */
export class BookSeats {
  constructor(private readonly db: RowStore, private readonly outbox: Outbox) {}

  execute(command: BookSeatsCommand): Booking {
    if (command.rows.length === 0) {
      throw new IllegalArgumentError('brak miejsc');
    }
    const total = this.price(command);
    const id = this.db.insert([command.email, command.format, command.rows.length, total]);
    this.outbox.publish('reservation-created', `${id};${command.email};${total.toString()}`);
    return new Booking(id, total);
  }

  private price(command: BookSeatsCommand): Money {
    let base: Money;
    switch (command.format) {
      case 'IMAX': base = Money.of('40.00'); break;
      case '3D': base = Money.of('32.00'); break;
      default: base = Money.of('25.00');
    }
    let total = Money.of('0.00');
    for (const row of command.rows) {
      total = total.plus(base);
      if (row >= 10) {
        total = total.plus(Money.of('10.00'));
      }
    }
    return total;
  }
}
```

**Po (`ReservationController.ts`):**

```typescript
import { IllegalArgumentError, IllegalStateError } from '../../../../shared/errors.js';
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import { BookSeats } from './BookSeats.js';
import { BookSeatsCommand } from './BookSeatsCommand.js';

/** Kontroler tylko tłumaczy - parametry na BookSeatsCommand, wynik i wyjątki na kody odpowiedzi. */
export class ReservationController {
  private readonly bookSeats: BookSeats;

  constructor(db: RowStore, outbox: Outbox) {
    this.bookSeats = new BookSeats(db, outbox);
  }

  handle(params: ReadonlyMap<string, string>): string {
    const email = params.get('email');
    if (email === undefined || email.trim() === '') {
      return '400 brak email';
    }
    const rows = (params.get('rows') ?? '').split(',')
      .filter((s) => s.trim() !== '').map((s) => Number.parseInt(s, 10));
    try {
      const booking = this.bookSeats.execute(
        new BookSeatsCommand(email, params.get('format') ?? '2D', rows));
      return `201 ${booking.id} ${booking.total.toString()}`;
    } catch (error) {
      if (error instanceof IllegalArgumentError) {
        return `400 ${error.message}`;
      }
      if (error instanceof IllegalStateError) {
        return `503 ${error.message}`;
      }
      throw error;
    }
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s09` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste obiekty danych, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**Zmiany:**

1. Nowy plik `NewReservation.ts`: klasa danych (`email`, `format`, `seats`, `total`) przekraczająca granicę do portu zapisu.
2. Nowe pliki portów: `ReservationStore.ts` z metodą `save(reservation: NewReservation): string` i `BookingNotifier.ts` z metodą `reservationCreated(id, reservation)`.
3. Nowy plik `RowStoreReservationStore.ts`: adapter mapujący `NewReservation` na kolumny `db.insert([...])`; nowy plik `OutboxBookingNotifier.ts`: adapter znający temat `reservation-created` i format komunikatu.
4. `BookSeats` przyjmuje w konstruktorze porty `store` i `notifier` zamiast `db` i `outbox`; `execute` tworzy `NewReservation`, woła `store.save`, a potem `notifier.reservationCreated`.
5. Kontroler składa na razie graf w konstruktorze: `new BookSeats(new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox))` - dochodzą importy obu adapterów.

**Przed (`BookSeats.ts`, metoda `execute`):**

```typescript
execute(command: BookSeatsCommand): Booking {
  if (command.rows.length === 0) {
    throw new IllegalArgumentError('brak miejsc');
  }
  const total = this.price(command);
  const id = this.db.insert([command.email, command.format, command.rows.length, total]);
  this.outbox.publish('reservation-created', `${id};${command.email};${total.toString()}`);
  return new Booking(id, total);
}
```

**Po (`NewReservation.ts`, nowy plik):**

```typescript
import type { Money } from '../../../shared/Money.js';

/** Dane przekraczające granicę do portu zapisu - rekord, nie unknown[]. */
export class NewReservation {
  constructor(
    readonly email: string,
    readonly format: string,
    readonly seats: number,
    readonly total: Money,
  ) {}
}
```

**Po (`ReservationStore.ts`, nowy plik):**

```typescript
import type { NewReservation } from './NewReservation.js';

/**
 * Port wyjściowy zdefiniowany przez potrzebę przypadku użycia.
 * Zwraca identyfikator; gdy nie da się zapisać - `IllegalStateError`.
 */
export interface ReservationStore {
  save(reservation: NewReservation): string;
}
```

**Po (`BookingNotifier.ts`, nowy plik):**

```typescript
import type { NewReservation } from './NewReservation.js';

/** Port wyjściowy - "daj znać, że rezerwacja powstała". */
export interface BookingNotifier {
  reservationCreated(id: string, reservation: NewReservation): void;
}
```

**Po (`RowStoreReservationStore.ts`, nowy plik):**

```typescript
import type { RowStore } from '../RowStore.js';
import type { NewReservation } from './NewReservation.js';
import type { ReservationStore } from './ReservationStore.js';

/** Adapter wyjściowy - mapuje rekord na kolumny tabeli. */
export class RowStoreReservationStore implements ReservationStore {
  constructor(private readonly db: RowStore) {}

  save(reservation: NewReservation): string {
    return this.db.insert([
      reservation.email, reservation.format, reservation.seats, reservation.total]);
  }
}
```

**Po (`OutboxBookingNotifier.ts`, nowy plik):**

```typescript
import type { Outbox } from '../Outbox.js';
import type { BookingNotifier } from './BookingNotifier.js';
import type { NewReservation } from './NewReservation.js';

/** Adapter wyjściowy - zna temat i format komunikatu. */
export class OutboxBookingNotifier implements BookingNotifier {
  constructor(private readonly outbox: Outbox) {}

  reservationCreated(id: string, reservation: NewReservation): void {
    this.outbox.publish('reservation-created', `${id};${reservation.email};${reservation.total.toString()}`);
  }
}
```

**Po (`BookSeats.ts`, importy, konstruktor i `execute`; metoda `price` bez zmian):**

```typescript
import { IllegalArgumentError } from '../../../../shared/errors.js';
import { Money } from '../../../shared/Money.js';
import type { BookingNotifier } from './BookingNotifier.js';
import type { BookSeatsCommand } from './BookSeatsCommand.js';
import { Booking } from './Booking.js';
import { NewReservation } from './NewReservation.js';
import type { ReservationStore } from './ReservationStore.js';

export class BookSeats {
  constructor(private readonly store: ReservationStore, private readonly notifier: BookingNotifier) {}

  execute(command: BookSeatsCommand): Booking {
    if (command.rows.length === 0) {
      throw new IllegalArgumentError('brak miejsc');
    }
    const reservation = new NewReservation(
      command.email, command.format, command.rows.length, this.price(command));
    const id = this.store.save(reservation);
    this.notifier.reservationCreated(id, reservation);
    return new Booking(id, reservation.total);
  }
```

**Po (`ReservationController.ts`, zmienione fragmenty; `handle` bez zmian):**

```typescript
import { OutboxBookingNotifier } from './OutboxBookingNotifier.js';
import { RowStoreReservationStore } from './RowStoreReservationStore.js';
...
  constructor(db: RowStore, outbox: Outbox) {
    this.bookSeats = new BookSeats(
      new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
  }
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 1 2`

### Krok 3: Move - granica widoczna w katalogach

**Zmiany:**

1. Utwórz katalog `app` i przenieś do niego pliki `BookSeats.ts`, `BookSeatsCommand.ts`, `Booking.ts`, `NewReservation.ts`, `ReservationStore.ts` i `BookingNotifier.ts` (Move).
2. Utwórz katalog `adapter` i przenieś do niego `ReservationController.ts`, `RowStoreReservationStore.ts` i `OutboxBookingNotifier.ts`.
3. Popraw ścieżki importów w przenoszonych plikach (adaptery importują porty z `../app/...`, świat zewnętrzny z `../../...`) oraz w `CinemaApplication.ts` (kontroler z `./adapter/ReservationController.js`). Pliki w `app` importujące `Money` i `errors` dostają jeden poziom `../` więcej (np. `../../../../shared/Money.js`); treść klas i funkcji nigdzie się nie zmienia.

**Przed (`ReservationController.ts`, importy lokalne):**

```typescript
import { BookSeats } from './BookSeats.js';
import { BookSeatsCommand } from './BookSeatsCommand.js';
import { OutboxBookingNotifier } from './OutboxBookingNotifier.js';
import { RowStoreReservationStore } from './RowStoreReservationStore.js';
```

**Po (układ katalogów):**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Po (`adapter/ReservationController.ts`, importy):**

```typescript
import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { Outbox } from '../../Outbox.js';
import type { RowStore } from '../../RowStore.js';
import { BookSeats } from '../app/BookSeats.js';
import { BookSeatsCommand } from '../app/BookSeatsCommand.js';
import { OutboxBookingNotifier } from './OutboxBookingNotifier.js';
import { RowStoreReservationStore } from './RowStoreReservationStore.js';
```

**Po (`adapter/RowStoreReservationStore.ts`, importy):**

```typescript
import type { RowStore } from '../../RowStore.js';
import type { NewReservation } from '../app/NewReservation.js';
import type { ReservationStore } from '../app/ReservationStore.js';
```

**Po (`adapter/OutboxBookingNotifier.ts`, importy):**

```typescript
import type { Outbox } from '../../Outbox.js';
import type { BookingNotifier } from '../app/BookingNotifier.js';
import type { NewReservation } from '../app/NewReservation.js';
```

**Po (`app/BookSeats.ts`, importy):**

```typescript
import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Money } from '../../../../shared/Money.js';
import type { BookingNotifier } from './BookingNotifier.js';
import type { BookSeatsCommand } from './BookSeatsCommand.js';
import { Booking } from './Booking.js';
import { NewReservation } from './NewReservation.js';
import type { ReservationStore } from './ReservationStore.js';
```

**Po (`CinemaApplication.ts`, zmieniony import):**

```typescript
import { ReservationController } from './adapter/ReservationController.js';
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy katalogów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 2 3`

### Krok 4: Composition root

**Zmiany:**

1. W `ReservationController` zamień tworzenie `new BookSeats(...)` w konstruktorze na właściwość parametru `constructor(private readonly bookSeats: BookSeats) {}` (Introduce Parameter, ręcznie); parametry `db` i `outbox` znikają z konstruktora kontrolera.
2. Składanie grafu przenieś do funkcji `reservationController(db, outbox)` w `CinemaApplication.ts`: tworzy oba adaptery, `BookSeats` i przekazuje go kontrolerowi; wywołanie do poprawy wskaże kompilator. Importy wędrują razem z kodem: kontroler przestaje importować `Outbox`, `RowStore` i oba adaptery (`BookSeats` staje się `import type`), a `CinemaApplication.ts` importuje oba adaptery i `BookSeats`.

**Przed:**

```typescript
export class ReservationController {
  private readonly bookSeats: BookSeats;

  constructor(db: RowStore, outbox: Outbox) {
    this.bookSeats = new BookSeats(
      new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
  }
```

**Po (`adapter/ReservationController.ts`, importy i konstruktor; metoda `handle` bez zmian):**

```typescript
import { IllegalArgumentError, IllegalStateError } from '../../../../../shared/errors.js';
import type { BookSeats } from '../app/BookSeats.js';
import { BookSeatsCommand } from '../app/BookSeatsCommand.js';

export class ReservationController {
  constructor(private readonly bookSeats: BookSeats) {}
```

**Po (`CinemaApplication.ts`):**

```typescript
import type { Outbox } from '../Outbox.js';
import type { RowStore } from '../RowStore.js';
import { OutboxBookingNotifier } from './adapter/OutboxBookingNotifier.js';
import { ReservationController } from './adapter/ReservationController.js';
import { RowStoreReservationStore } from './adapter/RowStoreReservationStore.js';
import { BookSeats } from './app/BookSeats.js';

/** Composition root: jedyne miejsce, które zna wszystkie konkrety i składa graf. */
export function reservationController(db: RowStore, outbox: Outbox): ReservationController {
  const bookSeats = new BookSeats(
    new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
  return new ReservationController(bookSeats);
}
```

**Uruchom:** test zielony; pokaż `S09UseCaseTest`: przypadek użycia z literałami obiektów zamiast adapterów, w tym `doesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako proste klasy danych. Koszt: osiem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 2.4, pytanie o koszt abstrakcji).

### Pułapki

- Kopiowanie szablonu czterech folderów bez sprawdzenia kierunku importów.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `unknown[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez frameworka webowego (Express, NestJS) i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s10. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie)
**Katalog:** `typescript/src/workshop/m2/s10_boundarycheck` · **Test:** `scripts/warsztat.sh --lang ts test m2/s10` (`S10ArchitectureTest.test.ts`, `S10EquivalenceTest.test.ts`, `S10ToolsTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy i importuje adapter oraz typ sterownika bazy (`sql/Timestamp`). Wydzielamy maper, przenosimy go do adaptera, a narzędzia `BoundaryRule` i `CohesionProbe` mierzą efekt po każdym kroku.

**Zasada:** Spójny moduł zmienia się z jednego powodu, a reguła zależności mówi, że domena nie importuje technologii - to dwa niezależne wymiary. Regułę architektury warto sprawdzać automatycznie przy każdym buildzie, a metryki takie jak LCOM4 traktować jako sygnał do rozmowy, nie wyrocznię.

**Efekt:** Domena jest wolna od `sql` i typów adaptera, a każda klasa ma LCOM4 równe 1. Skan importów jest słabą bramką - dynamiczne `import()` przejdzie, więc silniejsze są dependency-cruiser, eslint z regułami granic, osobne pakiety workspace lub project references.

**Różnica względem Javy (adaptacja):** zakazana technologia `java.sql.Timestamp` to w porcie stabilny plik sceny `sql/Timestamp.ts` ("typ sterownika bazy", ten sam format `2026-10-02 20:00:00.0`). `BoundaryRule` skanuje pliki `.ts` i zgłasza ścieżki importów (`import`/`export ... from`, także `import type`) zawierające zakazany fragment; reguła sceny to `new BoundaryRule('/sql/', '/adapter/')`, a naruszenia mają format `ScreeningRowMapper.ts: ../../sql/Timestamp.js`. `CohesionProbe` rozpoznaje pola klasy i właściwości parametrów konstruktora (`private readonly x`). Odpowiednikiem Javowego "importu statycznego, który łatwo przeoczyć" jest `import type` - stąd test narzędzia `boundaryRuleReportsForbiddenImportsIncludingTypeOnly`. Zamiast ArchUnit, JPMS i modułów Maven omawiamy dependency-cruiser, eslint (`no-restricted-imports`, eslint-plugin-boundaries), pakiety workspace i project references.

### Co widzimy

Dwa narzędzia bez bibliotek w katalogu sceny: `BoundaryRule` (skanuje pliki `.ts` w katalogu i zgłasza zakazane importy) oraz `CohesionProbe` (LCOM4: liczba grup metod połączonych wspólnym polem lub wywołaniem). Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy - importuje adapter i `sql`.

```typescript
import { Timestamp } from '../../sql/Timestamp.js';
import { ScreeningRow } from '../adapter/ScreeningRow.js';
import { Screening } from './Screening.js';

export class ScreeningService {
  constructor(
    private readonly basePrice: Decimal,
    private readonly morningDiscount: Decimal,
    private readonly table: string,
  ) {}
```

Uruchom test z logami (`scripts/warsztat.sh --lang ts test m2/s10` - skrypt uruchamia vitest z `--reporter=verbose`, więc widać `console.log` także z zielonych testów) i pokaż wyjście: `s10 start - naruszenia granicy: [ScreeningService.ts: ../../sql/Timestamp.js, ScreeningService.ts: ../adapter/ScreeningRow.js]` i `s10 start - LCOM4 ScreeningService: Result[lcom4=2, groups=[fromRow, toRow, isMorning, price]]`. Test startu działa jak "zamrożone naruszenia" (baseline znanych naruszeń w dependency-cruiser, FreezingArchRule w ArchUnit): znane są tolerowane, każde nowe jest czerwone.

### Krok 1: Extract Class - spójność

**Zmiany:**

1. Nowy plik `domain/ScreeningRowMapper.ts`: klasa `ScreeningRowMapper` z parametrem konstruktora `table` oraz metodami `toRow` i `fromRow` przeniesionymi z `ScreeningService` (Extract Class, ręcznie); importy `Timestamp`, `ScreeningRow` i `IllegalArgumentError` idą razem z kodem.
2. W `ScreeningService` zostają pola `basePrice` i `morningDiscount` oraz metody `price` i `isMorning`; parametr `table`, metody `toRow`/`fromRow` i importy technologii znikają.
3. Popraw composition root `CinemaApp.ts`: tworzy `new ScreeningService(new Decimal('25.00'), new Decimal('5.00'))` i `new ScreeningRowMapper('screenings')`, a wiersz i odczyt idą przez maper; dochodzi import mapera z `./domain/ScreeningRowMapper.js`.

**Przed (`domain/ScreeningService.ts`; metody `price` i `isMorning` bez zmian):**

```typescript
export class ScreeningService {
  constructor(
    private readonly basePrice: Decimal,
    private readonly morningDiscount: Decimal,
    private readonly table: string,
  ) {}
  ...
  toRow(screening: Screening): ScreeningRow {
    return new ScreeningRow(this.table, screening.title, Timestamp.valueOf(screening.start));
  }

  fromRow(row: ScreeningRow): Screening {
    if (row.table !== this.table) {
      throw new IllegalArgumentError(`obca tabela: ${row.table}`);
    }
    return new Screening(row.title, row.start.toLocalDateTime());
  }
}
```

**Przed (`CinemaApp.ts`, funkcja `describe`):**

```typescript
export function describe(title: string, start: LocalDateTime): string {
  const service = new ScreeningService(
    new Decimal('25.00'), new Decimal('5.00'), 'screenings');
  const screening = new Screening(title, start);
  const row: ScreeningRow = service.toRow(screening);
  return `${service.price(screening).toFixed(2)} | ${row.toString()} | ${service.fromRow(row).equals(screening)}`;
}
```

**Po (`domain/ScreeningRowMapper.ts`, nowy plik - wciąż w domain):**

```typescript
import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { ScreeningRow } from '../adapter/ScreeningRow.js';
import { Screening } from './Screening.js';

/** Mapowanie na wiersz jako osobna, spójna klasa (LCOM4 = 1); granica wciąż naruszona. */
export class ScreeningRowMapper {
  constructor(private readonly table: string) {}

  toRow(screening: Screening): ScreeningRow {
    return new ScreeningRow(this.table, screening.title, Timestamp.valueOf(screening.start));
  }

  fromRow(row: ScreeningRow): Screening {
    if (row.table !== this.table) {
      throw new IllegalArgumentError(`obca tabela: ${row.table}`);
    }
    return new Screening(row.title, row.start.toLocalDateTime());
  }
}
```

**Po (`domain/ScreeningService.ts`):**

```typescript
import type { Decimal } from 'decimal.js';

import type { Screening } from './Screening.js';

/** Po Extract Class zostaje sama polityka cenowa (LCOM4 = 1) i żadnego importu technologii. */
export class ScreeningService {
  constructor(private readonly basePrice: Decimal, private readonly morningDiscount: Decimal) {}

  price(screening: Screening): Decimal {
    return this.isMorning(screening) ? this.basePrice.minus(this.morningDiscount) : this.basePrice;
  }

  private isMorning(screening: Screening): boolean {
    return screening.start.hour < 12;
  }
}
```

**Po (`CinemaApp.ts`, funkcja `describe`):**

```typescript
import { ScreeningRowMapper } from './domain/ScreeningRowMapper.js';
...
export function describe(title: string, start: LocalDateTime): string {
  const service = new ScreeningService(new Decimal('25.00'), new Decimal('5.00'));
  const mapper = new ScreeningRowMapper('screenings');
  const screening = new Screening(title, start);
  const row: ScreeningRow = mapper.toRow(screening);
  return `${service.price(screening).toFixed(2)} | ${row.toString()} | ${mapper.fromRow(row).equals(screening)}`;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s10` - 13 testów zielonych; LCOM4 obu klas = 1, ale `step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s10 0 1`

### Krok 2: Move do adaptera

**Zmiany:**

1. Przenieś plik `ScreeningRowMapper.ts` z katalogu `domain` do `adapter` (Move Class) - plik `domain/ScreeningRowMapper.ts` znika.
2. Popraw ścieżki importów w maperze (`Screening` z `../domain/Screening.js`, `ScreeningRow` z `./ScreeningRow.js`; `errors` i `Timestamp` bez zmian, bo `adapter` leży na tej samej głębokości co `domain`) i w `CinemaApp.ts` (maper z `./adapter/ScreeningRowMapper.js`). Treść klasy `ScreeningRowMapper` nie zmienia się.

**Przed (`domain/ScreeningRowMapper.ts`, importy):**

```typescript
import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { ScreeningRow } from '../adapter/ScreeningRow.js';
import { Screening } from './Screening.js';
```

**Po (`adapter/ScreeningRowMapper.ts`, importy):**

```typescript
import { IllegalArgumentError } from '../../../../../shared/errors.js';
import { Timestamp } from '../../sql/Timestamp.js';
import { Screening } from '../domain/Screening.js';
import { ScreeningRow } from './ScreeningRow.js';
```

**Po (`CinemaApp.ts`, zmieniony import):**

```typescript
import { ScreeningRowMapper } from './adapter/ScreeningRowMapper.js';
```

**Uruchom:** test zielony; `step2DomainIsFreeOfTechnology` - lista naruszeń pusta.
**Co powiedzieć:** zależność biegnie teraz `adapter -> domain`. Reguła jest sprawdzana automatycznie przy każdym buildzie, a nie tylko na code review.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s10 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `sql` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S10ToolsTest` sprawdza same narzędzia na stałych próbkach kodu.

### Pułapki

- `BoundaryRule` widzi tylko instrukcje `import`/`export ... from` - dynamiczne `import()` czy `require` przejdą. Silniejsze bramki: dependency-cruiser, eslint (`no-restricted-imports`, eslint-plugin-boundaries), osobne pakiety workspace z jawnymi zależnościami w `package.json`, project references w `tsconfig`, reguły w CI. Porównajcie ich siłę.
- LCOM4 to heurystyka na źródle (pola klasy i właściwości parametrów konstruktora, składowe wcięte o 2 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po.

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Proponowana kolejność pokazu

**Ścieżka krótka (~55 min):** s03 (bramka kompilatora, 10) → s01 (YAGNI, 12) → s08 (odwrócenie zależności, 12) → s05 (zasada harcerza, 8) → s06 (seria zmian, 10). Pokazuje pełny łuk prewencji: najszybsza pętla, odłożona decyzja, kierunek zależności, mała poprawa i zestaw zmian gotowy do przeglądu. Jeśli zostanie czas, z s09 pokaż tylko `scripts/warsztat.sh --lang ts diff m2/s09 3 4` i `S09UseCaseTest.test.ts` (5 min).

**Ścieżka pełna (~115 min, z przerwą):**

1. Szybka informacja zwrotna: s03, s04 (~22 min) - na koniec pytanie, które sprawdzenia uruchamiać lokalnie, a które w CI.
2. Decyzje: s01, s02 (~22 min) - pięć pytań ze slajdu 2.4 na przykładzie s01, potem decyzja trwała zapisana w ADR i pilnowana testem.
3. Projekt: s07, s08, s09, s10 (~54 min) - od jednego aktora zmiany, przez kierunek zależności, do granicy sprawdzanej automatycznie.
4. Codzienna dbałość o kod: s05, s06 (~18 min) jako podsumowanie - mała poprawa w dotykanym kodzie i seria zmian z osobnymi dowodami.

Sceny s09 i s10 dobrze łączyć: po s09 zapytaj, jak upewnić się, że `app` nigdy nie zaimportuje `adapter`, i przejdź do s10. Podobnie s05 i s06: po s05 zapytaj, jak uczciwie wprowadzić posortowane miejsca, i pokaż, jak s06 oddziela refaktoryzację od zmiany zachowania.
