# Moduł 2. Prewencja - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Dziesięć małych scen w domenie kina CineLegacy pokazuje tematy z części II prezentacji („Zanim wprowadzisz wzorzec”) jako serie bezpiecznych zmian: bramki kompilatora i jakości jako najszybsze pętle informacji zwrotnej, odraczanie decyzji (YAGNI) i ich zapis (ADR z regułą sprawdzaną przez test), projekt, który hamuje degradację kodu (zasada pojedynczej odpowiedzialności, zasada odwrócenia zależności, granice czystej architektury, automatyczna kontrola granicy) oraz codzienną dbałość o kod (zasada harcerza, seria zmian gotowych do przeglądu). Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny ilustrują sekcję 2 slajdów ("Zanim wprowadzisz wzorzec"): każdy temat ma własną, kilkuminutową scenę. Sceny są pokazami prowadzącego do wyboru - nie trzeba pokazywać wszystkich. Numery scen odpowiadają katalogom w kodzie i w tej kolejności sceny są opisane poniżej. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Proponowana kolejność pokazu” na końcu.

Port TypeScript: kod scen leży w `typescript/src/workshop/m2/sNN_.../{start,step1,...}`, testy vitest w `typescript/test/workshop/m2/sNN_.../`, a pokaz prowadzimy w VS Code. Kwoty to `Decimal` z biblioteki decimal.js (odpowiednik `BigDecimal`) albo `Money` z `typescript/src/workshop/shared/Money.ts`. `Decimal` nie pamięta skali, więc tam, gdzie Java drukuje kwotę przez `toString()`, port drukuje `toFixed(2)`. Rekordy Javy to klasy z polami `readonly` w konstruktorze, a statyczne metody klas-punktów wejścia to funkcje modułu. Największe adaptacje dotyczą scen narzędziowych: bramka kompilatora (s03) i bramka jakości (s04) korzystają z API kompilatora TypeScript (pakiet `typescript-api`), a bramka jakości także z zestawów przypadków testowych ładowanych jako moduły. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

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
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: funkcja `confirmReservation()` w `Main.ts` (s08), `reservationController()` w `CinemaApplication.ts` (s09), `describe()` w `CinemaApp.ts` (s10). VS Code nie ma refaktoryzacji Introduce Parameter ani Change Signature - te miejsca poprawiasz ręcznie, a kompilator (czerwone podkreślenia, `npm run typecheck`) wskaże każde wywołanie do zmiany.
- Część testów to nie tylko równoważność (`SNNEquivalenceTest.test.ts`), ale też dowody strategii (`SNNSolutionTest`, `S08DependencyDirectionTest`, `S09UseCaseTest`, `S10ArchitectureTest`): wynik bramki, lista naruszeń ADR, kierunek zależności. Testy, które **dokumentują stan startowy** (`startViolatesBothRules` w s02, `startFailsTheGateWithFourKindsOfWarnings` w s03, `startPassesEverythingBecauseItChecksNothing` w s04, `startDomainHasNoViolationsBeyondTheFrozenOnes` w s10), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `start` - to znak, że start jest już naprawiony. Pułapki, które znikają po naprawie, testy dokumentują na kopii w kroku, który jeszcze je ma (np. s05 krok 1 - antyprzykład nadużycia Boy Scout).
- Sceny s02, s03, s04, s08 i s10 czytają pliki źródłowe sceny. Ścieżki są liczone względem modułu (`import.meta.url`, pomocnik testów `workshopDir(...)` z `typescript/test/workshop/support/paths.ts`), więc testy działają niezależnie od katalogu, z którego je uruchamiasz.
- Sceny s03 i s04 celowo zawierają kod, którego nie przepuszcza ostrzejsza bramka (`start` w s03 i `sample/dirty` w s04) - to materiał dla bramek, nie niedoróbka. W s03 przelot w `switch` jest w buildzie projektu wyciszony `// @ts-expect-error`, a próbki s04 są wyłączone z typechecku projektu (`exclude` w `typescript/tsconfig.json`).
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów. Tam, gdzie krok opiera się na kontroli kompilatora (literówka w nazwie reguły w s01, usunięty plik, zmieniona sygnatura), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- Refaktoryzacje VS Code: menu Refactor... (⌃⇧R) daje m.in. Extract to method in class, Extract to function, Extract to constant, Inline variable, Move to a new file / Move to file. Rename Symbol to F2, Quick Fix ⌘., Go to Definition F12, Find All References ⇧⌥F12. Change Signature, Safe Delete, Extract Interface, Extract Class ani Type Migration nie ma - te ruchy robimy ręcznie: przed usunięciem symbolu sprawdź Find All References, a po zmianie - kompilator.
- Testy uruchamiasz skryptem (`scripts/warsztat.sh --lang ts test m2/sNN`) albo z rozszerzenia Vitest w VS Code (ikona przy `describe`/`it`).

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

**W IDE:** w `TicketPricer` zastąp parametry konstruktora `registry` i `activeRules` polem z listą `Object.freeze([new MorningRule(), new VipRule()])` (ręcznie, konstruktor usuń). Usuń `RuleRegistry.ts` i metodę `priority()` z interfejsu i obu reguł (była potrzebna tylko do sortowania) - przed usunięciem sprawdź ⇧⌥F12.
**Po:**

```typescript
private readonly rules: readonly PricingRule[] = Object.freeze([new MorningRule(), new VipRule()]);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s01` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s01 0 1`

### Krok 2: Change Signature - typowane dane zamiast mapy

**W IDE:** ręcznie (VS Code nie ma Change Signature): w `PricingRule.appliesTo` zmień typ parametru z `ReadonlyMap<string, unknown>` na `TicketQuote`, z `apply` usuń parametr kontekstu. Kompilator wskaże obie implementacje i wywołania - popraw je (rzutowania znikają) i usuń budowanie mapy w `TicketPricer`.
**Po:**

```typescript
appliesTo(quote: TicketQuote): boolean {
  return quote.row >= quote.vipFromRow;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s01 1 2`

### Krok 3: Inline Class dla reguł, usunięcie interfejsu

**W IDE:** ręcznie: dwa `if` w `price` z metodami `isMorning` i `isVip` (ciała z `appliesTo` obu reguł, ⌃⇧R > Extract to method in class). Usuń `MorningRule.ts`, `VipRule.ts` i `PricingRule.ts`. Stałe przez ⌃⇧R > Extract to constant (pola `private static readonly`).
**Po:**

```typescript
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

**W IDE:** nowa klasa `Quote` (`readonly total: number`, `readonly groupDiscount: boolean`) w `pricing`; ręcznie zmieniamy sygnaturę `total` - bez `organizer`, zwraca `Quote` (Change Signature w VS Code nie ma, wywołania wskaże `npm run typecheck`). Wywołanie `mailer` przenieś do `BookingService` (warstwa aplikacji). Usuń import `notification` z cennika.
**Po:**

```typescript
const quote = this.pricing.total(tickets, unitPrice);
if (quote.groupDiscount) {
  this.mailer.groupDiscountGranted(organizer, tickets);
}
```

**Uruchom:** test zielony; `step1RemovesDependencyOnNotification` - zostało tylko naruszenie `ADR-0007/R2`.
**Co powiedzieć:** naruszenie w teście prowadzi wprost do identyfikatora decyzji i jej uzasadnienia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s02 0 1`

### Krok 2: spełnienie R2 - Money

**W IDE:** VS Code nie ma Type Migration: zmieniamy ręcznie typ `number` → `Money` w `Quote.total` i parametrze `unitPrice` w `TicketPricing`, a `npm run typecheck` prowadzi przez kolejne miejsca. Nazwane stałe dla progu i procentu grupy; `BookingService` podaje cenę jednostkową jako `Money.of('25.00')` (metoda `unitPrice(format)`).
**Po:**

```typescript
total(tickets: number, unitPrice: Money): Quote {
  let sum = unitPrice.times(tickets);
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

**W IDE:** na polu `seatsByRow` dopisz ręcznie argumenty typu `new Map<number, string[]>()` (VS Code nie ma szybkiej poprawki Add type arguments); `get(row) ?? []` zamiast sprawdzania `undefined`; usuń asercje `as` - kompilator zna już typy.
**Po:**

```typescript
private readonly seatsByRow = new Map<number, string[]>();
...
const seats = this.seatsByRow.get(row) ?? [];
this.seatsByRow.set(row, seats);
seats.push(seat);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s03` - zostają kategorie `deprecation`, `fallthrough`.
**Co powiedzieć:** jedna kategoria ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s03 0 1`

### Krok 2: nowe API zamiast przestarzałego

**W IDE:** VS Code przekreśla wywołanie przestarzałego przeciążenia (`basePrice` z tagiem `@deprecated`). W `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do przeciążenia `basePrice(format: string)`. Najechanie kursorem na metodę (albo F12) potwierdza, że to nowe przeciążenie i przekreślenie znika.
**Po:**

```typescript
return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
  + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.ts:13` (TypeScript wskazuje `case`, z którego następuje przelot).
**Co powiedzieć:** przestarzałe przeciążenie usuniemy dopiero, gdy nikt go nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s03 1 2`

### Krok 3: funkcja z return w każdym case zamiast przelotu

**W IDE:** TypeScript nie ma wyrażenia `switch`, więc zaznacz `switch` i ⌃⇧R → Extract to method `features(format)`, potem ręcznie zamień przypisania na `return` w każdym `case`; przelot zapisany wprost: `case 3: return 'duzy ekran, dzwiek Dolby'`. Dyrektywa `@ts-expect-error` znika razem z przelotem.
**Po:**

```typescript
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

**W IDE:** metoda `scanSources`: każda linia z `TODO`/`FIXME` albo `console.` to wynik z plikiem i linią.
**Po:**

```typescript
if (line.includes('TODO') || line.includes('FIXME')) {
  findings.push('TODO ' + where);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s04` - `TODO PriceTable.ts:10`, `console PriceTable.ts:20`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 0 1`

### Krok 2: diagnostyki kompilatora

**W IDE:** metoda `compileSources` (`ts.createProgram` z `typescript-api`, `strict`, `noUnusedLocals`, `noUnusedParameters`, `noImplicitReturns`), każda diagnostyka składniowa i semantyczna to wynik.
**Po:**

```typescript
const line = sourceFile.getLineAndCharacterOfPosition(d.start ?? 0).line + 1;
findings.push('kompilator ' + path.basename(sourceFile.fileName) + ':' + line + ' TS' + d.code);
```

**Uruchom:** test zielony; dochodzą dwa wyniki `kompilator PriceTable.ts:28 TS7006` (parametry `what` i `kind` bez typu - niejawne `any`).
**Co powiedzieć:** to ta sama idea co w s03, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**W IDE:** metoda `checkCoverage`: każda publiczna metoda `PriceTable` (bez `private`/`protected`, wyszukana wyrażeniem regularnym na poziomie wcięcia składowej) musi być wywołana w `PriceTableTest.ts`.
**Po:**

```typescript
if (!test.includes('.' + method[1]! + '(')) {
  findings.push('pokrycie ' + input.keyClass + '.' + method[1]! + ' bez testu');
}
```

**Uruchom:** test zielony; `vipSurcharge` i `lookupCount` bez testu.
**Co powiedzieć:** to nie zastępuje pomiaru pokrycia (`vitest run --coverage`), ale łapie metodę, której żaden test nawet nie woła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s04 2 3`

### Krok 4: testy zielone

**W IDE:** metoda `runTests`: bramka wywołuje każdy przypadek zestawu `input.testSuite` (bez zależności od vitest) i zgłasza porażki.
**Po:**

```typescript
try {
  suite.tests[name]!();
} catch (failure) {
  findings.push('test ' + suite.name + '.' + name + ' nie przechodzi: ' + (failure as Error).name);
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

**Różnica względem Javy:** javowy `StringBuilder` zamiast konkatenacji ma w TypeScripcie idiomatyczny odpowiednik: tablicę linii złączoną `join` (w kroku 2 literał tablicy z szablonami tekstowymi). Rename to F2 (Rename Symbol w VS Code). Antyprzykład w kroku 1 jest taki sam jak w Javie: sortowanie miejsc, `toLowerCase()` e-maila i pominięta linia "Tel".

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

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh --lang ts jump m2/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```typescript
const sortedSeats = [...ticket.seats].sort();
lines.push(`Miejsca: ${sortedSeats.join(', ')}`);
lines.push(`Klient: ${ticket.email.trim().toLowerCase()}`);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest` → `abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh --lang ts reset m2/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s05 0 1`

### Krok 2: poprawna, mała poprawa

**W IDE:** Rename Symbol (F2) `t` → `ticket`; pętlę sklejającą miejsca zamień ręcznie na `ticket.seats.join(', ')`; tablica linii złączona `join` zamiast konkatenacji; ⌃⇧R → Extract to method `phoneLine`.
**Po:**

```typescript
`Miejsca: ${ticket.seats.join(', ')}`,
`Klient: ${ticket.email.trim()}`,
TicketPrinter.phoneLine(ticket),
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

**W IDE:** ⌃⇧R → Extract to method na wyborze ceny bazowej → `basePrice(format: string)`; to samo na wyborze zniżki → `discountPercent(type: string)` zwracające procent; Money zamiast Decimal.
**Po:**

```typescript
const base = PriceList.basePrice(query.format);
let price = base.minus(base.percent(PriceList.discountPercent(query.type)));
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**W IDE:** VS Code nie ma Change Signature, więc ręcznie: parametr `query: TicketQuery` zamiast `type: string`, w ciele `switch (query.type)`, w wywołaniu `discountPercent(query)`; `npm run typecheck` pokaże pominięte wywołania.
**Po:**

```typescript
private static discountPercent(query: TicketQuery): number {
  switch (query.type) { ... }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** "make the change easy, then make the easy change". Ten commit nadal nie zmienia zachowania.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**W IDE:** w gałęzi `default` warunek `isCheapTuesday(query) ? 20 : 0`, metoda pomocnicza, stała `CHEAP_TUESDAY_PERCENT`.
**Po:**

```typescript
default: return PriceList.isCheapTuesday(query) ? PriceList.CHEAP_TUESDAY_PERCENT : 0;
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

**W IDE:** zaznacz blok pod `// księgowość`, ⌃⇧R > Extract to method in class, nazwa `accountingSection` (niech zwraca `string` i ma własną zmienną `out`); to samo dla `// marketing` jako `marketingSection`.
**Po:**

```typescript
render(sales: readonly Sale[]): string {
  return this.accountingSection(sales) + this.marketingSection(sales);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `revenue` nadal wiąże oba światy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**W IDE:** skopiuj `revenue` (⇧⌥↓ duplikuje zaznaczone linie), nazwij kopię `popularity`, w `marketingSection` użyj `popularity`. Dodaj komentarz dokumentacyjny (`/** ... */`) z właścicielem każdej metody.
**Po:**

```typescript
/** Księgowość: przychód brutto seansu. */
private revenue(sale: Sale): Decimal { ... }

/** Marketing: miara popularności filmu (dziś: bilety + bar). */
private popularity(sale: Sale): Decimal { ... }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z DRY (slajd 2.6: dwie identyczne dziś formuły to nie duplikacja wiedzy) w kontekście SRP.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**W IDE:** VS Code nie ma Extract Class (Extract Delegate), więc ręcznie: utwórz `AccountingSection.ts` i przenieś do niej `accountingSection` (jako `render`), `revenue` i `net`; utwórz `MarketingSection.ts` z `marketingSection` (jako `render`) i `popularity`. W `DailyReport` zostaw dwa pola i delegowanie. Zaznaczone metody możesz też przenieść przez Move to a new file, ale trafią do pliku jako funkcje - i tak trzeba je potem opakować w klasę.
**Po:**

```typescript
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

**W IDE:** VS Code nie ma Introduce Parameter, więc ręcznie: zamień pole `mail` na właściwość parametru konstruktora `constructor(private readonly mail: SmtpMailSender) {}`, a `new SmtpMailSender('smtp.kino.pl', 25)` przenieś do `Main.ts` (kompilator wskaże to wywołanie). Import w `ConfirmReservation` staje się `import type`.
**Po:**

```typescript
export class ConfirmReservation {
  constructor(private readonly mail: SmtpMailSender) {}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s08` - 18 testów zielonych.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `import type { SmtpMailSender } from '../infra/SmtpMailSender.js'` nadal stoi w polityce. Test `step1AndStep2PolicyDependsOnInfrastructure` to potwierdza. `import type` znika po kompilacji, ale zależność źródłowa zostaje: zmiana API `SmtpMailSender` psuje politykę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s08 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**W IDE:** zaznacz składanie MIME, `send` i sprawdzenie kodu, ⌃⇧R > Extract to method in class, nazwa `notifyCustomer`; parametry nazwij `email` i `message` (F2).
**Po:**

```typescript
this.notifyCustomer(reservation.email, message);
return `potwierdzono: ${reservation.email}`;
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie wydzielamy interfejsu z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s08 1 2`

### Krok 3: Port po stronie polityki, adapter w infra

**W IDE:** utwórz w `app` interfejs `CustomerNotifier` z metodą `notifyCustomer(email, message)`. Utwórz `infra/SmtpCustomerNotifier.ts` z klasą `SmtpCustomerNotifier implements CustomerNotifier` i przenieś do niej metodę `notifyCustomer` (wytnij i wklej; VS Code nie przenosi metod między klasami). W `ConfirmReservation` parametr konstruktora -> `notifier: CustomerNotifier`. W `Main.ts` złóż `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender(...)))`.
**Po:**

```typescript
export interface CustomerNotifier {           // app
  notifyCustomer(email: string, message: string): void;
}

export class SmtpCustomerNotifier implements CustomerNotifier {   // infra
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

**W IDE:** utwórz klasy danych `BookSeatsCommand(email, format, rows)` i `Booking(id, total)` (pola `readonly`, odpowiedniki rekordów). Zaznacz w kontrolerze wycenę, zapis i publikację, ⌃⇧R > Extract to method in class, nazwa `execute`, potem przenieś metodę ręcznie do nowej klasy `BookSeats` w pliku `BookSeats.ts` (wycenę wydziel w niej jako `price`). Kontroler mapuje błędy: `IllegalArgumentError` -> 400, `IllegalStateError` -> 503.
**Po:**

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s09` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste obiekty danych, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**W IDE:** utwórz `NewReservation` (klasa danych), porty `ReservationStore.save(reservation)` i `BookingNotifier.reservationCreated(id, reservation)`. Przenieś ręcznie mapowanie na `unknown[]` do `RowStoreReservationStore`, publikację do `OutboxBookingNotifier`. `BookSeats` przyjmuje porty w konstruktorze.
**Po:**

```typescript
const id = this.store.save(reservation);
this.notifier.reservationCreated(id, reservation);
return new Booking(id, reservation.total);
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 1 2`

### Krok 3: Move - granica widoczna w katalogach

**W IDE:** w eksploratorze VS Code przeciągnij `BookSeats.ts`, `BookSeatsCommand.ts`, `Booking.ts`, `NewReservation.ts`, `ReservationStore.ts`, `BookingNotifier.ts` do nowego katalogu `app`, a kontroler i oba adaptery do `adapter`. Na pytanie "Update imports?" odpowiedz "Yes" - VS Code poprawi ścieżki importów (także w `CinemaApplication.ts`).
**Po:**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy katalogów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s09 2 3`

### Krok 4: Composition root

**W IDE:** ręcznie (bez Introduce Parameter): w kontrolerze zamień tworzenie `new BookSeats(...)` w konstruktorze na parametr `private readonly bookSeats: BookSeats`, a składanie grafu przenieś do funkcji `reservationController` w `CinemaApplication.ts` (kompilator wskaże to wywołanie). Parametry `db` i `outbox` znikają z konstruktora kontrolera.
**Po:**

```typescript
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

**W IDE:** ręcznie (VS Code nie ma Extract Class): utwórz `domain/ScreeningRowMapper.ts` i przenieś do niego `toRow`, `fromRow` oraz parametr `table`; importy `Timestamp`, `ScreeningRow` i `IllegalArgumentError` idą razem z kodem (Quick Fix ⌘. > Add all missing imports). Popraw `CinemaApp.ts` (composition root), żeby tworzył obie klasy.
**Po:**

```typescript
export class ScreeningRowMapper {    // wciąż w domain
  constructor(private readonly table: string) {}

  toRow(screening: Screening): ScreeningRow { ... }

  fromRow(row: ScreeningRow): Screening { ... }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m2/s10` - 13 testów zielonych; LCOM4 obu klas = 1, ale `step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m2/s10 0 1`

### Krok 2: Move do adaptera

**W IDE:** w eksploratorze przeciągnij `ScreeningRowMapper.ts` z `domain` do `adapter` i zgódź się na "Update imports". VS Code poprawi ścieżki w maperze (`../domain/Screening.js`, `./ScreeningRow.js`) i w `CinemaApp.ts`.
**Po:**

```typescript
import { Timestamp } from '../../sql/Timestamp.js';
import { Screening } from '../domain/Screening.js';
import { ScreeningRow } from './ScreeningRow.js';
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
