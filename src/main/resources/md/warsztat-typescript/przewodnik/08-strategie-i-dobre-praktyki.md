# Moduł 8. Strategie i dobre praktyki - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Trzynaście małych scen pokazuje strategie z modułu 8 na kodzie kina CineLegacy: przyrostową wymianę implementacji (Branch by Abstraction, Strangler Fig, shadow), Boy Scout Rule, serię zmian gotowych do przeglądu, ADR i dokumentację żywą, bramki kompilatora i jakości, codemod na AST, wdrożenie etapowe oraz migrację danych expand and contract. Każda scena to katalog `start` (stan wyjściowy) i kompletne snapshoty `stepN` po każdym ruchu; test sceny (vitest) uruchamiasz po każdym kroku.

Kod portu TypeScript leży w `typescript/src/workshop/m8/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m8/sNN_.../`. Rekordy zagnieżdżone z Javy (`VerificationReport.Agreement`, `BookingPlan.Charge`, `BookingTable.Row`, `Routing.Route`) są w porcie osobnymi klasami eksportowanymi z tego samego modułu, `sealed interface` to unia klas z polem `kind` i `assertNever` w `switch`, a `Optional<T>` to `T | undefined`. Największe adaptacje dotyczą scen narzędziowych: bramka kompilatora (s08) i codemod (s09) korzystają z API kompilatora TypeScript (pakiet `typescript-api`), a bramka jakości (s10) - z tego samego API i z zestawów przypadków testowych ładowanych jako moduły. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m8              # sceny i kroki modułu 8
scripts/warsztat.sh --lang ts test m8/s01          # testy jednej sceny
scripts/warsztat.sh --lang ts test m8              # wszystkie sceny modułu (237 testów)
scripts/warsztat.sh --lang ts diff m8/s03 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh --lang ts diff m8/s03 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m8/s03 2        # start = step2, gdy trzeba przeskoczyć krok
scripts/warsztat.sh --lang ts next m8/s03          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m8/s03         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m8/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- W tym module część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych, wynik bramki, lista naruszeń ADR. Testy, które **dokumentują stan startowy** (np. "start wysyła dwa maile", "start narusza ADR"), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh --lang ts reset m8/sNN`.
- Sceny s07, s08, s09, s10, s12 i s13 czytają pliki źródłowe sceny. Ścieżki są liczone względem modułu (`import.meta.url`, pomocnik testów `workshopDir(...)`), więc testy działają niezależnie od katalogu, z którego je uruchamiasz.
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów. Tam, gdzie krok opiera się na kontroli kompilatora (usunięty plik, wyczerpujący `switch`), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- Sceny s08 i s10 celowo zawierają kod, którego nie przepuszcza ostrzejsza bramka (`start` w s08 i `sample/dirty` w s10) - to materiał dla bramek, nie niedoróbka. W s08 przelot w `switch` jest w buildzie projektu wyciszony `// @ts-expect-error`, a próbki s10 są wyłączone z typechecku projektu (`exclude` w `typescript/tsconfig.json`).
- Refaktoryzacje w VS Code: zaznaczenie i ⌃⇧R (Refactor...) daje Extract to method / Extract to function / Extract to constant, F2 to Rename Symbol, ⌘. (Quick Fix) m.in. Implement interface, a Move to a new file / Move to file przenosi deklaracje najwyższego poziomu. Change Signature, Safe Delete, Extract Interface ani Type Migration nie ma - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` (albo Find All References ⇧⌥F12).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | 1.3 Branch by Abstraction | 4 | `m8/s01_branchbyabstraction` | ~15 min |
| s02 | 1.3-1.4 Strangler Fig, architektura przejściowa | 4 | `m8/s02_stranglerfig` | ~12 min |
| s03 | 1.5 Równoległa weryfikacja (shadow) | 4 | `m8/s03_parallelrun` | ~15 min |
| s04 | 1.6-1.7 Granice trybu shadow | 3 | `m8/s04_shadowlimits` | ~12 min |
| s05 | 2.1-2.4 Boy Scout Rule i nadużycia | 2 | `m8/s05_boyscout` | ~8 min |
| s06 | 3.2 Małe zestawy zmian - czego nie łączyć | 3 | `m8/s06_reviewableseries` | ~10 min |
| s07 | 4.3-4.4 ADR i wykonywalny model decyzji | 2 | `m8/s07_adr` | ~10 min |
| s08 | 5.2-5.3, 5.6 Bramka kompilatora | 3 | `m8/s08_compilergate` | ~10 min |
| s09 | 5.4-5.5 Automatyczna transformacja (codemod) | 3 | `m8/s09_codemod` | ~15 min |
| s10 | 5.6-5.7 Minimalna bramka jakości | 4 | `m8/s10_qualitygate` | ~12 min |
| s11 | 6.2-6.4 Jawna polityka wdrożenia etapowego | 3 | `m8/s11_stagedrollout` | ~10 min |
| s12 | 6.5-6.6 Dane, wycofanie, zamknięcie migracji | 4 | `m8/s12_expandcontract` | ~15 min |
| s13 | 4.1, 4.5 Dokumentacja żywa i historyczna | 2 | `m8/s13_livingdocs` | ~8 min |

Katalogi są względne wobec `typescript/src/workshop/`.

## Scena s01. Branch by Abstraction na cenniku z CinemaManager

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Katalog:** `typescript/src/workshop/m8/s01_branchbyabstraction` · **Test:** `scripts/warsztat.sh --lang ts test m8/s01`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingService.confirm` ma wklejony stary cennik z `CinemaManager` (`number` jako kwota, kody formatu, typy "S"/"E"/"C") i nie ma szwu, przez który dałoby się go wymienić. Izolujemy stary cennik w osobnej klasie, chowamy go za abstrakcją `TicketPricing` zwracającą `Money`, dokładamy obok `ModernTicketPricing` z przełącznikiem, a na końcu usuwamy starą ścieżkę.

**Zasada:** Branch by Abstraction to "gałąź" zrobiona w kodzie, a nie w Git: stabilny kontrakt wewnątrz aplikacji, za nim stara i nowa implementacja, a każdy krok trafia od razu do głównej gałęzi. Wydzielenie abstrakcji i adaptera to jeszcze refaktoryzacja, a nowa implementacja i przełączenie to już migracja. Nie mylić ze Strangler Fig, który działa na granicy systemu, a nie wewnątrz aplikacji.

**Efekt:** `BookingService` zależy tylko od `TicketPricing` i liczy w `Money`, a starego cennika i przełącznika `PricingMode` już nie ma - migracja jest zamknięta. Do pilnowania zostaje moment zaokrąglenia: nowy kod zaokrągla każdy procent osobno, a legacy dopiero sumę.

**Różnica względem Javy:** TypeScript nie ma przeciążonych konstruktorów, więc trzy konstruktory Javy z kroku 3 (`BookingService()`, `BookingService(PricingMode)`, `BookingService(TicketPricing)`) to jeden parametr `PricingMode | TicketPricing` z wartością domyślną `PricingMode.LEGACY`. `PricingMode` jest wyliczeniem tekstowym (`LEGACY = 'LEGACY'`), a wybór implementacji robi `switch` z `assertNever` w metodzie `pricingFor` (TypeScript nie ma wyrażenia `switch`). Kwoty legacy to `number`, a odpowiednikiem `new Money(BigDecimal.valueOf(total))` jest `new Money(new Decimal(total))`.

### Co widzimy

`start/BookingService.ts` - metoda `confirm` to kopia cennika z `CinemaManager.book()`: kwoty w `number`, kody formatu `1/2/3`, typy biletu `'S'/'E'/'C'` i formatowanie potwierdzenia w jednej metodzie. Chcemy wymienić cennik na nową implementację, ale nie ma szwu, a długa gałąź w Git na czas przepisywania to antywzorzec.

```typescript
let sum = 0;
for (let i = 0; i < request.seats.length; i++) {
  let p = 0;
  const f = s.format;
  if (f === 1) {
    p = 25.00;
  } ...
}
if (request.seats.length >= 10) {
  sum = sum - sum * 0.10;
}
```

### Krok 1: Extract Method + Move - stary cennik w osobnej klasie

**W IDE:** zaznacz blok od `const s = ...` do obliczenia `total`, ⌃⇧R → Extract to method in class 'BookingService', nazwa `total`, zwraca `number`. Metodę przenieś ręcznie do nowej klasy `LegacyTicketPricing` w pliku `LegacyTicketPricing.ts` (Move to a new file działa tylko na deklaracjach najwyższego poziomu, więc możesz najpierw wybrać Extract to function in module scope, potem Move to a new file i opakować funkcję w klasę). W `BookingService` pole `pricing`.
**Po:**

```typescript
const total = this.pricing.total(request);
return request.screening.title + ': ' + request.seats.join(',')
  + ' - do zaplaty ' + total.toFixed(2);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s01` - równoważność zielona dla wszystkich wariantów.
**Co powiedzieć:** starego kodu nie poprawiamy, tylko go izolujemy. Brzydki, ale ma jedno wejście i jedno wyjście.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s01 0 1`

### Krok 2: abstrakcja TicketPricing, stary cennik za nią

**W IDE:** VS Code nie ma Extract Interface dla klasy, więc interfejs `TicketPricing` piszemy ręcznie w nowym pliku, a `LegacyTicketPricing` dostaje `implements TicketPricing`. Typ zwracany kontraktu to od razu `Money`; w implementacji zamieniamy `number` na `Money` dopiero na granicy (`new Money(new Decimal(total))`). `BookingService` dostaje konstruktor z parametrem `TicketPricing` (domyślnie `new LegacyTicketPricing()`), a listę miejsc do poprawy pokazuje `npm run typecheck`.
**Po:**

```typescript
export interface TicketPricing {
  total(request: BookingRequest): Money;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** abstrakcję projektujemy pod przyszłość (Money), a stary kod dostaje adapter. Do tego momentu to czysta refaktoryzacja - można ją zintegrować z główną gałęzią od razu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s01 1 2`

### Krok 3: nowa implementacja obok i przełącznik

**W IDE:** nowa klasa `ModernTicketPricing implements TicketPricing` (⌘. → Implement interface; Money, `switch` na formacie i typie, nazwane stałe `private static readonly`). Wyliczenie `PricingMode { LEGACY, MODERN }` i konstruktor `BookingService` przyjmujący tryb albo gotową implementację; domyślnie `LEGACY`.
**Po:**

```typescript
constructor(modeOrPricing: PricingMode | TicketPricing = PricingMode.LEGACY) {
  this.pricing = typeof modeOrPricing === 'string' ? BookingService.pricingFor(modeOrPricing) : modeOrPricing;
}

private static pricingFor(mode: PricingMode): TicketPricing {
  switch (mode) {
    case PricingMode.LEGACY: return new LegacyTicketPricing();
    case PricingMode.MODERN: return new ModernTicketPricing();
    default: return assertNever(mode);
  }
}
```

**Uruchom:** test zielony; `S01SolutionTest` → `legacyAndModernPricingFulfilTheSameContract` porównuje obie implementacje na tych samych żądaniach.
**Co powiedzieć:** wdrożenie nowego kodu nie zmienia zachowania - zmienia je dopiero konfiguracja. To już migracja, a nie refaktoryzacja, więc przełącznik ma mieć właściciela i termin usunięcia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s01 2 3`

### Krok 4: usunięcie starej implementacji i przełącznika

**W IDE:** VS Code nie ma Safe Delete: Find All References (⇧⌥F12) na `LegacyTicketPricing` i `PricingMode` pokazuje jedyne użycie w `BookingService`, potem usuwamy oba pliki; domyślna wartość parametru konstruktora to `new ModernTicketPricing()`. `npm run typecheck` potwierdza, że nic już ich nie importuje.
**Po:**

```typescript
constructor(private readonly pricing: TicketPricing = new ModernTicketPricing()) {}
```

**Uruchom:** test zielony; `migrationIsClosedOnlyWhenTheOldPathIsGone` sprawdza, że dynamiczny `import()` plików `LegacyTicketPricing.ts` i `PricingMode.ts` z `step4` się nie udaje.
**Co powiedzieć:** migracja kończy się po usunięciu starej ścieżki. Abstrakcja może zostać jako szew dla testów.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s01 3 4`

### Rozwiązanie i uzasadnienie

Cztery kroki, każdy z zielonym buildem i możliwy do zintegrowania osobno. Gałąź istnieje w kodzie (abstrakcja + przełącznik), a nie w systemie kontroli wersji. Test równoważności obejmuje obie gałęzie przełącznika w kroku 3.

### Pułapki

- Moment zaokrąglenia: legacy sumuje `number` i zaokrągla na końcu, nowy kod zaokrągla każdy procent. Dla kwot z "połową grosza" (np. 10% z 245.25) wyniki mogą się różnić - dopisz taki przypadek do testu kontraktu, zanim przełączysz.
- Abstrakcja skopiowana 1:1 ze starej sygnatury (`total(...): number`) utrwala stary model.
- Przełącznik bez terminu usunięcia staje się nowym legacy.

### Pytanie do sali

Który z czterech kroków jest refaktoryzacją, a który migracją? Jakie dowody są potrzebne dla każdego z nich?

## Scena s02. Strangler Fig - fasada, która przejmuje ścieżki

**Temat ze slajdów:** 1.3-1.4 Branch by Abstraction, Strangler Fig, architektura przejściowa
**Katalog:** `typescript/src/workshop/m8/s02_stranglerfig` · **Test:** `scripts/warsztat.sh --lang ts test m8/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `LegacyCinema` jest wystawiony klientom wprost jako `CinemaApi`, więc nie da się przenieść rezerwacji bez raportu. Stawiamy przed nim `CinemaFacade`, która najpierw deleguje 1:1, potem kieruje rezerwację do `BookingModule`, raport do `ReportModule`, a na końcu legacy znika.

**Zasada:** Strangler Fig to przejmowanie systemu operacja po operacji: brama, proxy lub fasada na granicy systemu kieruje część ruchu do nowego komponentu, a reszta dalej trafia do starego. Każdy element przejściowy, tu fasada i jej routing, potrzebuje właściciela i kryterium usunięcia. Fasada tylko kieruje ruch - nie powinna mieć własnej logiki biznesowej.

**Efekt:** Klienci rozmawiają z fasadą, która opisuje routing w `routes()`, a `LegacyCinema` zostało usunięte po przejęciu obu ścieżek. Przez okres hybrydy obie strony musiały pisać do `BookingLedger` w tym samym formacie - zgodność danych to osobny kontrakt migracji.

**Różnica względem Javy:** `routes()` zwraca `ReadonlyMap<string, string>` (w Javie `Map.of(...)`), a test "klasa została usunięta" to dynamiczny `import()` pliku `step4/LegacyCinema.ts`, który musi się nie powieść.

### Co widzimy

`start/LegacyCinema.ts` implementuje publiczne API `CinemaApi` (rezerwacja i raport) i jest wystawiony klientom wprost. Obie operacje korzystają ze wspólnej bazy `BookingLedger`. Nie da się przenieść jednej operacji bez drugiej.

```typescript
export class LegacyCinema implements CinemaApi {
  book(email: string, title: string, format: number, tickets: number, web: boolean): string { ... }
  report(): string { ... }
}
```

### Krok 1: fasada 1:1

**W IDE:** nowa klasa `CinemaFacade implements CinemaApi`; ⌘. na nazwie klasy → Implement interface 'CinemaApi' tworzy szkielety metod, które uzupełniamy delegacją do pola `legacy` (VS Code nie ma Delegate Methods). Metoda `routes()` opisuje, kto obsługuje którą operację.
**Po:**

```typescript
book(email: string, title: string, format: number, tickets: number, web: boolean): string {
  return this.legacy.book(email, title, format, tickets, web);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s02` - scenariusz klienta (rezerwacje + raport) daje identyczny zapis.
**Co powiedzieć:** fasada nic nie zmienia, ale daje jedno miejsce przekierowania. Klienci od teraz rozmawiają z fasadą.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s02 0 1`

### Krok 2: przejęcie pierwszej ścieżki - rezerwacja

**W IDE:** nowa klasa `BookingModule` (Money, nazwane reguły), w fasadzie `book` deleguje do niej; `routes()` zwraca `book=new`.
**Po:**

```typescript
book(email: string, title: string, format: number, tickets: number, web: boolean): string {
  return this.bookings.book(email, title, format, tickets, web);
}

report(): string {
  return this.legacy.report();
}
```

**Uruchom:** test zielony; `legacyReportSeesBookingsTakenByTheNewModule` - raport legacy widzi rezerwacje nowego modułu, bo baza jest wspólna.
**Co powiedzieć:** przez chwilę system jest hybrydą. To działa tylko dlatego, że obie strony piszą w tym samym formacie danych - zgodność danych to osobny kontrakt.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s02 1 2`

### Krok 3: przejęcie kolejnej ścieżki - raport

**W IDE:** nowa klasa `ReportModule` z tym samym formatem wyjścia (sumy w Money); fasada kieruje `report` do niej. `LegacyCinema` zostaje w kodzie, ale bez ruchu.
**Po:**

```typescript
routes(): ReadonlyMap<string, string> {
  return new Map([['book', 'new'], ['report', 'new']]);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** stary kod zostaje na czas okna wycofania - powrót to jedna linia w fasadzie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s02 2 3`

### Krok 4: usunięcie legacy

**W IDE:** Find All References (⇧⌥F12) na `LegacyCinema` - zostało tylko pole i konstruktor fasady. Usuwamy je razem z plikiem `LegacyCinema.ts` (VS Code nie ma Safe Delete), `npm run typecheck` potwierdza brak użyć.
**Po:** fasada ma tylko `BookingModule` i `ReportModule`.
**Uruchom:** test zielony; `legacyCinemaIsDeletedInTheLastStep`.
**Co powiedzieć:** kryterium usunięcia było znane od kroku 1 (brak ruchu do legacy + zamknięte okno wycofania).
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s02 3 4`

### Rozwiązanie i uzasadnienie

Strangler Fig działa na granicy systemu (publiczne API), Branch by Abstraction (s01) wewnątrz aplikacji. Test równoważności uruchamia ten sam scenariusz klienta na każdym etapie hybrydy.

### Pułapki

- Nowy moduł z innym formatem danych niż legacy - raport legacy przestaje się zgadzać w kroku 2.
- Fasada, która zaczyna mieć własną logikę biznesową.
- Brak `routes()` albo innego jawnego opisu routingu - po miesiącu nikt nie wie, co gdzie działa (patrz s13).

### Pytanie do sali

Którą operację przenieślibyście jako pierwszą w prawdziwym CinemaManager i dlaczego: najprostszą, najczęściej zmienianą czy najbardziej ryzykowną?

## Scena s03. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 1.5 Przykład: równoległa weryfikacja kalkulatora
**Katalog:** `typescript/src/workshop/m8/s03_parallelrun` · **Test:** `scripts/warsztat.sh --lang ts test m8/s03`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PriceService` woła tylko stary kalkulator liczący w `number`, a gotowy `CandidatePriceCalculator` nigdy nie był porównany z produkcją. Uruchamiamy kandydata w cieniu, zbieramy typowany raport rozbieżności, poprawiamy znaleziony błąd i wprowadzamy jawny tryb `MigrationMode`.

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik obiema implementacjami na prawdziwym ruchu, ale klient dostaje wynik legacy, a awaria kandydata jest izolowana. Nadaje się dla czystych obliczeń bez efektów ubocznych. Zgodność obu implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.

**Efekt:** Raport wskazał złą kolejność rabatu porannego i zniżki procentowej, a po poprawce zostaje tylko awaria dla 4DX. W trybie `CANDIDATE` zachowanie dla 4DX świadomie się zmienia (odrzucenie zamiast wyceny 0.00), a `LEGACY` służy za natychmiastowe wycofanie, dopóki stary kod istnieje.

**Różnica względem Javy:** `sealed interface Verification` z rekordami to unia klas `Agreement | Divergence | CandidateFailure` z polem `kind`, a `problems()` rozróżnia je `switch (entry.kind)` z `assertNever`. Raport awarii kandydata zawiera nazwę klasy błędu TypeScriptu (`IllegalArgumentError: Nieznany format: 4DX`), a nie `IllegalArgumentException` - odpowiednikiem `getClass().getSimpleName()` jest `error.name`. W kroku 1 `catch` łapie każdy błąd (TypeScript nie ma typowanych klauzul `catch`).

### Co widzimy

`start/PriceService.ts` woła tylko `LegacyPriceCalculator` (liczy w `number`). Obok leży `CandidatePriceCalculator` (Money), napisany przez zespół, ale nigdy nieporównany z produkcją. Jedyne opcje: "włączyć i zobaczyć" albo wieczne testy ręczne.

```typescript
price(query: TicketQuery): Money {
  return this.legacy.price(query);
}
```

### Krok 1: dodanie cienia

**W IDE:** w `price` po wyliczeniu wyniku legacy wywołaj kandydata w `try/catch` (zaznaczenie i Surround With: snippet `try` z Quick Fix albo ręcznie) i licz niezgodności.
**Po:**

```typescript
const result = this.legacy.price(query);
try {
  if (!this.candidate.price(query).equals(result)) {
    this.mismatchCount++;
  }
} catch {
  this.mismatchCount++;
}
return result;
```

**Uruchom:** test zielony; `step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches` - błąd kandydata dla formatu 4DX nie przerywa sprzedaży.
**Co powiedzieć:** klient zawsze dostaje wynik legacy. Ale licznik mówi tylko, ŻE coś się różni - nie wiemy co.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s03 0 1`

### Krok 2: raport rozbieżności

**W IDE:** nowy moduł `VerificationReport.ts` z klasami `Agreement`, `Divergence`, `CandidateFailure` (pole `kind`) i unią `Verification`. ⌃⇧R → Extract to method `verify` w `PriceService`.
**Po:**

```typescript
return legacyPrice.equals(candidatePrice)
  ? new Agreement(query, legacyPrice)
  : new Divergence(query, legacyPrice, candidatePrice);
```

**Uruchom:** test zielony; `step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX (`IllegalArgumentError: Nieznany format: 4DX`).
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s03 1 2`

### Krok 3: poprawka znaleziona przez raport

**W IDE:** w `CandidatePriceCalculator` przenieś blok rabatu porannego (⌥↓, Move Line Down) za wyliczenie zniżki procentowej.
**Po:**

```typescript
let price = base.minus(base.percent(CandidatePriceCalculator.discountPercent(query.type)));
if (query.start.hour < 12) {
  price = price.minus(CandidatePriceCalculator.MORNING_DISCOUNT);
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s03 2 3`

### Krok 4: przełączenie

**W IDE:** wyliczenie `MigrationMode { LEGACY, SHADOW, CANDIDATE }`, konstruktor `PriceService(mode = MigrationMode.SHADOW, report = new VerificationReport())`, `switch` w `price`.
**Po:**

```typescript
switch (this.mode) {
  case MigrationMode.LEGACY: return this.legacy.price(query);
  case MigrationMode.SHADOW: return this.shadow(query);
  case MigrationMode.CANDIDATE: return this.candidate.price(query);
  default: return assertNever(this.mode);
}
```

**Uruchom:** test zielony; równoważność obejmuje wszystkie trzy tryby, `step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat` pokazuje świadomą zmianę dla 4DX.
**Co powiedzieć:** tryb zamiast flagi boolean, wybór w jednym miejscu. `LEGACY` to natychmiastowe wycofanie, dopóki nie usuniemy starego kodu.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s03 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - błąd w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na CANDIDATE? Kto o tym decyduje?

## Scena s04. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 1.6-1.7 Granice trybu shadow i antywzorce migracji
**Katalog:** `typescript/src/workshop/m8/s04_shadowlimits` · **Test:** `scripts/warsztat.sh --lang ts test m8/s04`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShadowBooking` stosuje shadow do rezerwacji z efektami ubocznymi, więc klient dostaje dwa maile i dwa obciążenia karty, a cień niczego nie zgłasza. Wprowadzamy port `Effects`, w cieniu nagrywamy efekty zamiast je wykonywać, a na końcu wydzielamy czysty `BookingPlanner`, który zwraca plan jako dane.

**Zasada:** Podwójne wykonanie w trybie shadow jest bezpieczne tylko dla czystych obliczeń - płatności, maili, zapisów ani zdarzeń nie wolno powielać. Efekty zamieniamy w dane (Separate Query from Modifier): opis "co zrobić" można porównać, a wykonuje się go tylko raz. Zgodność zwracanego wyniku nie dowodzi zgodności efektów.

**Efekt:** Po rezerwacji klient dostaje jeden mail i jedno obciążenie, a cień porównuje zaplanowane efekty z efektami legacy i wyłapuje błędną kwotę w mailu kandydata. Bezpieczeństwo gwarantuje typ, bo `BookingPlanner` nie ma dostępu do portu efektów; kosztem jest rozdzielenie planu od wykonania i dodatkowy typ `BookingPlan`.

**Różnica względem Javy:** efekty planu (w Javie `sealed interface Effect` z rekordami) to klasy `Charge`, `Save`, `SendMail` z polem `kind`, metodami `describe()` i `applyTo(port)`, połączone w unię `Effect` w module `BookingPlan.ts`. Lista efektów w `BookingPlan` jest zamrożona (`Object.freeze`), jak `List.copyOf` w rekordzie.

### Co widzimy

`start/ShadowBooking.ts` stosuje shadow "jak dla kalkulatora" do rezerwacji z efektami ubocznymi. `NewBookingFlow` sam obciąża kartę, zapisuje wiersz i wysyła mail. Test pokazuje: klient dostaje **dwa maile i dwa obciążenia**, a cień niczego nie zgłasza, bo porównuje tylko zwracany tekst.

```typescript
const result = this.legacy.book(request);
try {
  const shadow = this.candidate.book(request); // też obciąża kartę i wysyła mail
```

### Krok 1: port efektów (Parameterize Constructor)

**W IDE:** interfejs `Effects` z operacjami `Infrastructure` używanymi przez nowy przepływ piszemy ręcznie (VS Code nie ma Extract Interface), adapter `RealEffects implements Effects` (⌘. → Implement interface). Typ parametru konstruktora `NewBookingFlow` zmieniamy ręcznie z `Infrastructure` na `Effects`; miejsca wywołań wskaże `npm run typecheck`.
**Po:**

```typescript
this.candidate = new NewBookingFlow(new RealEffects(infra));
```

**Uruchom:** test zielony; `step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s04 0 1`

### Krok 2: przechwycenie efektów w cieniu

**W IDE:** nowa klasa `RecordingEffects implements Effects` zapisuje zamiary w formacie dziennika. `ShadowBooking` przekazuje kandydatowi nagrywarkę i porównuje nagrane efekty z efektami, które faktycznie wykonało legacy.
**Po:**

```typescript
const recorder = new RecordingEffects();
const shadow = new NewBookingFlow(recorder).book(request);
this.compare('wynik', [result], [shadow]);
this.compare('efekty', legacyEffects, recorder.recorded());
```

**Uruchom:** test zielony; `step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s04 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**W IDE:** z `NewBookingFlow.book` wydziel (⌃⇧R → Extract to method, potem ręcznie do nowego pliku) klasę `BookingPlanner` z metodą `plan`, która zwraca `new BookingPlan(result, effects)`. `NewBookingFlow` wykonuje plan na porcie; `ShadowBooking` woła tylko `BookingPlanner`. Plik `RecordingEffects.ts` usuń (Find All References potwierdzi brak użyć).
**Po:**

```typescript
const plan = this.candidate.plan(request);
this.compare('wynik', [result], [plan.result]);
const planned = plan.effects.map((effect) => effect.describe());
this.compare('efekty', legacyEffects, planned);
```

**Uruchom:** test zielony; `step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s04 2 3`

### Rozwiązanie i uzasadnienie

Podwójne wykonanie tylko dla czystych obliczeń. Efekty (płatność, mail, zapis, zdarzenia) w cieniu są danymi do porównania, a nie akcjami.

### Pułapki

- Nagrywarka podpięta tylko w części miejsc - jeden zapomniany efekt i klient dostaje drugi SMS.
- Efekty ukryte w obliczeniu: sekwencja numerów rezerwacji, cache, licznik - też są efektami.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

### Pytanie do sali

Jakie efekty uboczne ma wasz "czysty" kod, o których zwykle nie myślicie (logi audytowe, metryki, sekwencje)?

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie

**Temat ze slajdów:** 2.1-2.4 Heurystyka, nie mandat; bezpieczna sekwencja i typowe nadużycia
**Katalog:** `typescript/src/workshop/m8/s05_boyscout` · **Test:** `scripts/warsztat.sh --lang ts test m8/s05`
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

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh --lang ts jump m8/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```typescript
const sortedSeats = [...ticket.seats].sort();
lines.push(`Miejsca: ${sortedSeats.join(', ')}`);
lines.push(`Klient: ${ticket.email.trim().toLowerCase()}`);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest` → `abusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh --lang ts reset m8/s05`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s05 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 3.2 Małe zestawy zmian - czego nie łączyć; 3.3-3.4 Przegląd kodu
**Katalog:** `typescript/src/workshop/m8/s06_reviewableseries` · **Test:** `scripts/warsztat.sh --lang ts test m8/s06`
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s06 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**W IDE:** w gałęzi `default` warunek `isCheapTuesday(query) ? 20 : 0`, metoda pomocnicza, stała `CHEAP_TUESDAY_PERCENT`.
**Po:**

```typescript
default: return PriceList.isCheapTuesday(query) ? PriceList.CHEAP_TUESDAY_PERCENT : 0;
```

**Uruchom:** test zielony; `S06SolutionTest` → `cheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `behaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s06 2 3`

### Rozwiązanie i uzasadnienie

Trzy zestawy zmian, każdy z jedną intencją i zielonym buildem. Refaktoryzacje dowodzi test równoważności, zmianę zachowania - test nowej reguły i test różnicowy ograniczający jej zasięg.

### Pułapki

- Łączenie refaktoryzacji ze zmianą zachowania: recenzent nie odróżni ruchu mechanicznego od nowej reguły.
- Łączenie formatowania z logiką, diffu automatycznego z ręcznym.
- Commit przygotowawczy, który "przy okazji" poprawia regułę.

### Pytanie do sali

Jak opisalibyście commit 3 według pięciu pytań ze slajdu 3.3 (dlaczego, co i czego nie, kontrakt, dowody, wdrożenie i wycofanie)?

## Scena s07. ADR i wykonywalny model decyzji

**Temat ze slajdów:** 4.3-4.4 Architecture Decision Record
**Katalog:** `typescript/src/workshop/m8/s07_adr` · **Test:** `scripts/warsztat.sh --lang ts test m8/s07`
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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s07 0 1`

### Krok 2: spełnienie R2 - Money

**W IDE:** VS Code nie ma Type Migration: zmieniamy ręcznie typ `number` → `Money` w `Quote.total` i parametrze `unitPrice` w `TicketPricing`, a `npm run typecheck` prowadzi przez kolejne miejsca. Nazwane stałe dla progu i procentu grupy; `BookingService` podaje cenę jednostkową jako `Money.of('25.00')` (metoda `unitPrice(format)`).
**Po:**

```typescript
total(tickets: number, unitPrice: Money): Quote {
  let sum = unitPrice.times(tickets);
```

**Uruchom:** test zielony; `step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s07 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `adrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: dynamiczny `import()`, re-eksport z innego katalogu albo kwota w `number` pod inną nazwą ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s08. Bramka kompilatora - opcje strict i ostrzeżenia jako błędy

**Temat ze slajdów:** 5.2-5.3 Refaktoryzacje IDE i kompilator Javy 25; 5.6 Bramka kompilatora
**Katalog:** `typescript/src/workshop/m8/s08_compilergate` · **Test:** `scripts/warsztat.sh --lang ts test m8/s08`
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

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s08` - zostają kategorie `deprecation`, `fallthrough`.
**Co powiedzieć:** jedna kategoria ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s08 0 1`

### Krok 2: nowe API zamiast przestarzałego

**W IDE:** VS Code przekreśla wywołanie przestarzałego przeciążenia (`basePrice` z tagiem `@deprecated`). W `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do przeciążenia `basePrice(format: string)`. Najechanie kursorem na metodę (albo F12) potwierdza, że to nowe przeciążenie i przekreślenie znika.
**Po:**

```typescript
return name + ' [' + features + '], cena ' + PriceTable.basePrice(name)
  + ' zl, zajete: ' + OccupancyReport.show(map.takenPerRow());
```

**Uruchom:** test zielony; zostaje tylko `[fallthrough] OccupancyReport.ts:13` (TypeScript wskazuje `case`, z którego następuje przelot).
**Co powiedzieć:** przestarzałe przeciążenie usuniemy dopiero, gdy nikt go nie woła - także poza repozytorium.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s08 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s08 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: w Javie z `-Werror` javac przerywa po fazie z pierwszym ostrzeżeniem i pełną listę trzeba zbierać drugim przebiegiem. TypeScript zbiera diagnostyki składniowe i semantyczne w jednym przebiegu, ale część kategorii (surowe typy, niesprawdzone rzutowania, przestarzałe API) nie jest diagnostyką kompilatora - bramka dokłada je własnym przejściem po AST z `TypeChecker` (`getResolvedSignature(...).getJsDocTags()` dla `@deprecated`).

### Pułapki

- Nagłe włączenie ostrzejszych opcji jako błędów w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `// @ts-ignore` albo `// @ts-nocheck` na całym pliku zamiast wąskiego `// @ts-expect-error` z uzasadnieniem (i bramki, która wyciszeń nie honoruje).
- Wersja TypeScriptu bramki (`typescript-api`, 5.9) nie jest wersją, którą buduje projekt - różne wersje potrafią inaczej raportować te same diagnostyki, więc obie trzeba przypiąć w `package-lock.json`.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s09. Codemod na API kompilatora TypeScript

**Temat ze slajdów:** 5.4-5.5 Analiza statyczna, formatowanie i automatyzacja (receptury)
**Katalog:** `typescript/src/workshop/m8/s09_codemod` · **Test:** `scripts/warsztat.sh --lang ts test m8/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookCallCodemod` migruje wywołania przestarzałego przeciążenia `book(..., web: boolean, ownGlasses: boolean)` wyrażeniem regularnym, które trafia w komentarz i w `HotelService`, a gubi wywołanie rozbite na trzy linie. Zastępujemy regex wyszukiwaniem po AST, przepisujemy argumenty według pozycji z drzewa, a na końcu dopasowujemy wywołania po typach.

**Zasada:** Codemod (receptura automatyczna) to powtarzalna transformacja wielu miejsc naraz; oparty na składni i typach jest bezpieczniejszy od regexa, ale automatyzacja zwiększa też zasięg błędu receptury. Recepturę sprawdzają testy `before`/`after`, kompilacja wyniku i test idempotencji.

**Efekt:** Codemod migruje tylko wywołania rozwiązane do przestarzałej sygnatury `BookingService.book` z `cinema/BookingService.ts`, wynik kompiluje się bez ostrzeżeń, a drugie uruchomienie niczego nie zmienia. Kosztem jest sporo kodu narzędziowego, więc przy kilku miejscach szybsze bywa ręczne poprawienie wywołań z listy Find All References.

**Różnica względem Javy:** Compiler Tree API (`JavacTask.parse()`, `TreeScanner`, `Trees.getElement`) zastępuje API kompilatora TypeScript z pakietu `typescript-api`. Kroki 1 i 2 używają samego parsera (`ts.createSourceFile`, węzeł `CallExpression` z `PropertyAccessExpression` o nazwie `book` i sześcioma argumentami), a krok 3 buduje w pamięci `ts.createProgram` ze źródeł API i migrowanego pliku (jako `desk/Source.ts`) i pyta `TypeChecker.getResolvedSignature`. Przepisanie to edycje tekstu w pozycjach węzłów AST (bez `ts.transform` i printera), więc formatowanie reszty pliku zostaje. Próbka projektu jest w TypeScripcie i ma ścieżki plików (`ProjectFile { path, text }`), bo importy modułów wymagają ścieżek zamiast pakietów; przestarzała sygnatura to przeciążenie z tagiem `@deprecated`. Pierwsza linia próbki to komentarz `// desk/TicketDesk.ts` w miejscu `package desk;`, więc numery linii wywołań są jak w Javie. Test sprawdza kompilację wyniku usługą językową TypeScript: błędy to `ERROR TSxxxx`, a użycie przestarzałego API to `WARNING TS6385` (w Javie `compiler.warn.has.been.deprecated`).

### Co widzimy

`SampleProject` zawiera API kina z przestarzałym przeciążeniem `book(..., web: boolean, ownGlasses: boolean)` i nowym `book(..., channel: Channel, glasses: Glasses)`, łudząco podobne `HotelService.book(..., breakfast: boolean, parking: boolean)` oraz klienta `TicketDesk` z trzema wywołaniami: w jednej linii (11), rozbite na trzy linie (16-18) i hotelowe (22), plus komentarz ze starym przykładem (15). `start/BookCallCodemod.ts` to "grep i zamień":

```typescript
private static readonly OLD_CALL = /\bbook\(.*,\s*(true|false)\s*,\s*\w+\s*\)/;
```

Test pokazuje: regex znajduje linie 11, 15, 22 - trafia w komentarz i hotel, a gubi wywołanie wielolinijkowe.

### Krok 1: wyszukiwanie przez AST

**W IDE:** ręcznie - `ts.createSourceFile` i rekurencyjny `visit` z `ts.forEachChild`; dopasowanie: `CallExpression`, którego wyrażenie to `PropertyAccessExpression` o nazwie `book`, z sześcioma argumentami; linia z `getLineAndCharacterOfPosition`.
**Po:**

```typescript
if (ts.isCallExpression(node) && BookCallCodemod.looksLikeOldBook(node)) {
  lines.push(unit.getLineAndCharacterOfPosition(node.getStart(unit)).line + 1);
}
```

**Uruchom:** test zielony; `step1AstSearchFindsRealCallsIncludingMultilineOne` - linie 11, 16, 22.
**Co powiedzieć:** parser widzi wywołania, nie tekst. Ale 22 to nadal hotel - sama składnia nie zna typów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s09 0 1`

### Krok 2: przepisanie według pozycji z AST

**W IDE:** `rewrite` podmienia dokładnie dwa ostatnie argumenty (literał `true`/`false` → stała enum, wyrażenie → operator warunkowy) i dopisuje importy za ostatnim `import`; edycje stosowane od końca pliku.
**Po:**

```typescript
if (arg.kind === ts.SyntaxKind.TrueKeyword || arg.kind === ts.SyntaxKind.FalseKeyword) {
  return { start, end, text: arg.kind === ts.SyntaxKind.TrueKeyword ? ifTrue : ifFalse };
}
```

**Uruchom:** test zielony; `step2SyntacticRewriteBreaksTheHotelCallAndIsNotIdempotent` - wywołanie hotelowe zostało "zmigrowane" i kod się nie kompiluje, a drugie uruchomienie psuje już zmigrowane wywołania.
**Co powiedzieć:** automatyzacja zwiększa zasięg - także zasięg błędu receptury.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s09 1 2`

### Krok 3: dopasowanie po typach

**W IDE:** analiza razem ze źródłami projektu (`ts.createProgram` z hostem w pamięci), `program.getTypeChecker()` i `checker.getResolvedSignature(call)`; migrujemy tylko wywołania rozwiązane do przestarzałej sygnatury `book` klasy `BookingService` z `cinema/BookingService.ts`.
**Po:**

```typescript
const owner = declaration.parent;
return signature!.getJsDocTags().some((tag) => tag.name === 'deprecated')
  && ts.isClassDeclaration(owner)
  && owner.name?.text === BookCallCodemod.OLD_API_OWNER
  && owner.getSourceFile().fileName === BookCallCodemod.absolute(BookCallCodemod.OLD_API_FILE);
```

**Uruchom:** test zielony; wynik identyczny z oczekiwanym `MIGRATED`, kompiluje się bez błędów i bez ostrzeżeń o przestarzałym API (`strict`, brak `WARNING TS6385`), a drugie uruchomienie niczego nie zmienia.
**Co powiedzieć:** receptura oparta na typach, test `before/after` na próbce, test idempotencji. Tak działają narzędzia typu jscodeshift czy ts-morph (w Javie OpenRewrite).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s09 2 3`

### Rozwiązanie i uzasadnienie

Codemod ma trzy dowody: znajduje dokładnie właściwe miejsca, wynik się kompiluje bez użycia przestarzałego API, a ponowne uruchomienie jest bezpieczne. Diff z codemodu idzie do osobnego commita niż zmiany ręczne.

### Pułapki

- Regex na kodzie źródłowym: komentarze, stringi, wywołania wielolinijkowe, przeciążenia.
- Brak testu idempotencji - receptura uruchomiona drugi raz w CI psuje kod.
- Importy dopisane "gdzieś" - kolejność poprawi formatter (albo Organize Imports), ale w osobnym commicie.

### Pytanie do sali

Kiedy codemod się opłaca, a kiedy szybciej jest poprawić 12 miejsc ręcznie z listą z Find All References i podpowiedziami `npm run typecheck`?

## Scena s10. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 5.6-5.7 Bramka kompilatora i minimalna bramka jakości
**Katalog:** `typescript/src/workshop/m8/s10_qualitygate` · **Test:** `scripts/warsztat.sh --lang ts test m8/s10`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `QualityGate` w `start` to lista kontrolna w komentarzu TSDoc i metoda, która zawsze zwraca pustą listę, więc przepuszcza brudną próbkę. Zamieniamy listę na cztery wykonywalne sprawdzenia: skan TODO i `console.`, diagnostyki kompilatora, przybliżenie pokrycia i uruchomienie testów.

**Zasada:** Minimalna bramka jakości łączy kilka niezależnych sygnałów, bo każde narzędzie dowodzi czegoś innego. Wynik ma być deterministyczny i wskazywać miejsce, a bramka potrzebuje właściciela i nie może dawać fałszywych alarmów - inaczej zespół ją wyłączy.

**Efekt:** Bramka zgłasza problemy `sample/dirty`, zatrzymuje nieprzechodzący test z `sample/broken` i przepuszcza `sample/clean`. Sprawdzenie pokrycia to tylko przybliżenie ("czy jakiś test w ogóle woła metodę") i nie zastępuje pomiaru `vitest --coverage`.

**Różnica względem Javy:** próbki `sample/clean`, `sample/dirty` i ich testy (`test/workshop/m8/s10_qualitygate/sample/{clean,dirty,broken}`) są wyłączone z typechecku projektu (`exclude` w `typescript/tsconfig.json`), bo brudna próbka celowo nie przechodzi `strict`: parametry bez typu to niejawne `any`, odpowiednik surowego typu. `GateInput.testClass` (nazwa klasy dla `Class.forName`) to `testSuite: TestSuite` - załadowany zestaw przypadków `{ name, tests }`; testy próbek to pliki `PriceTableTest.ts` z asercjami `node:assert/strict` (nie `*.test.ts`, więc vitest ich nie uruchamia), a `S10Fixtures.ts` ładuje je dynamicznym `import()`. `System.out`/`System.err` to `console.`, kompilator to `typescript-api` z opcjami projektu i `noUnused*`, `noImplicitReturns`, a wynik ma postać `kompilator <plik>:<linia> TS<kod>` zamiast klucza javac. Numery linii są inne niż w Javie.

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s10` - `TODO PriceTable.ts:10`, `console PriceTable.ts:20`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s10 0 1`

### Krok 2: diagnostyki kompilatora

**W IDE:** metoda `compileSources` (`ts.createProgram` z `typescript-api`, `strict`, `noUnusedLocals`, `noUnusedParameters`, `noImplicitReturns`), każda diagnostyka składniowa i semantyczna to wynik.
**Po:**

```typescript
const line = sourceFile.getLineAndCharacterOfPosition(d.start ?? 0).line + 1;
findings.push('kompilator ' + path.basename(sourceFile.fileName) + ':' + line + ' TS' + d.code);
```

**Uruchom:** test zielony; dochodzą dwa wyniki `kompilator PriceTable.ts:28 TS7006` (parametry `what` i `kind` bez typu - niejawne `any`).
**Co powiedzieć:** to ta sama idea co w s08, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s10 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s10 2 3`

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
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s10 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S10EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s11. Jawna polityka wdrożenia etapowego

**Temat ze slajdów:** 6.2 Warstwy kontroli; 6.3-6.4 Kryteria i jawna polityka wdrożenia etapowego
**Katalog:** `typescript/src/workshop/m8/s11_stagedrollout` · **Test:** `scripts/warsztat.sh --lang ts test m8/s11`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `CheckoutRouter` wdraża nowy proces płatności przez stałą w kodzie i testerów wpisanych w `if`, bez etapów i bez wyłącznika. Zamieniamy to na niezmienną klasę `RolloutPolicy` z procentem ruchu, deterministycznym koszykiem klienta i wyłącznikiem awaryjnym.

**Zasada:** Wdrożenie etapowe to jawna, testowana polityka: populacja, deterministyczny podział klientów, wyjątki i wyłącznik, a o zwiększeniu ekspozycji decydują z góry ustalone kryteria. Podział ma być stabilny - ten sam klient zawsze na tej samej ścieżce - więc bez losowania i bez skrótu zależnego od procesu czy tożsamości obiektu.

**Efekt:** Z dotychczasowymi ustawieniami router kieruje klientów tak samo jak `start`, procent da się zwiększać bez wyrzucania nikogo, a kill switch wycofuje zmianę bez nowego wydania. Świadomie zmienia się jedno zachowanie: e-mail jest normalizowany, a start porównywał go dosłownie.

**Różnica względem Javy:** rekord `RolloutPolicy` to klasa z polami `readonly`, a `Set.copyOf` to kopia do `ReadonlySet` w konstruktorze. `java.util.zip.CRC32` zastępuje funkcja `crc32` z `node:zlib` (ten sam algorytm na bajtach UTF-8), więc koszyki klientów są takie same jak w Javie.

### Co widzimy

`start/CheckoutRouter.ts` wdraża nowy proces płatności "na flagę": stała w kodzie (zmiana = nowe wydanie), testerzy wpisani w `if`, brak etapów i wyłącznika awaryjnego.

```typescript
static readonly NEW_CHECKOUT: boolean = false;

useNewCheckout(email: string): boolean {
  if (CheckoutRouter.NEW_CHECKOUT) {
    return true;
  }
  return email === 'anna@kino.pl' || email === 'jan@kino.pl';
}
```

### Krok 1: polityka jako wartość

**W IDE:** Introduce Parameter Object ręcznie (VS Code go nie ma) dla flagi i listy testerów → klasa `RolloutPolicy` z `readonly enabled: boolean` i `readonly allowList: ReadonlySet<string>`, fabryka `current()` z dotychczasowymi wartościami; router dostaje politykę w konstruktorze (domyślnie `RolloutPolicy.current()`).
**Po:**

```typescript
useNewCheckout(email: string): boolean {
  return this.policy.allows(email);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s11` - równoważność dla dotychczasowych ustawień.
**Co powiedzieć:** konfigurację da się podać z zewnątrz, przetestować i przejrzeć.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s11 0 1`

### Krok 2: deterministyczny podział klientów

**W IDE:** ręcznie zmieniamy parametr konstruktora: `percent: number` zamiast `enabled: boolean` (0 = dawne `false`, walidacja 0-100); metoda `bucket` - `crc32` znormalizowanego e-maila modulo 100.
**Po:**

```typescript
allows(email: string): boolean {
  return this.allowList.has(RolloutPolicy.normalize(email)) || RolloutPolicy.bucket(email) < this.percent;
}
```

**Uruchom:** test zielony; `sameCustomerAlwaysGetsTheSamePath`, `percentOfCustomersIsRoughlyRespected`, `increasingPercentNeverRemovesAnyone`.
**Co powiedzieć:** stabilny skrót, nie losowanie ani nic, co zależy od procesu. Ten sam klient zawsze widzi tę samą ścieżkę, a zwiększenie procentu nikogo nie wyrzuca. Uwaga: normalizacja e-maila to świadoma zmiana - start porównywał dosłownie (`startComparesEmailLiterally`).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s11 1 2`

### Krok 3: wyłącznik awaryjny

**W IDE:** pole `readonly killSwitch: boolean` w polityce (nowy parametr konstruktora), sprawdzane jako pierwsze w `allows`.
**Po:**

```typescript
if (this.killSwitch) {
  return false;
}
```

**Uruchom:** test zielony; `killSwitchOverridesPercentAndAllowList`.
**Co powiedzieć:** kill switch ma pierwszeństwo nawet przed testerami - służy do natychmiastowego wycofania bez wydania.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s11 2 3`

### Rozwiązanie i uzasadnienie

Polityka wdrożenia to kod z testami: populacja, deterministyczny podział, wyjątki i wyłącznik. Decyzję o zwiększeniu procentu (ADVANCE / HOLD / ROLLBACK) podejmuje się na podstawie metryk - to temat przykładu `RolloutPolicy` z katalogu `typescript/src/module8/risk`.

### Pułapki

- Losowanie przy każdym żądaniu - klient przeskakuje między ścieżkami.
- Własny skrót tekstu w stylu `hash = hash * 31 + code` bez `>>> 0` albo `Math.abs` - ujemne wartości i `%` dają ujemne koszyki.
- Flaga bez właściciela i terminu usunięcia.

### Pytanie do sali

Kill switch wyłącza nowy proces płatności. Co z zamówieniami, które są w trakcie płatności w nowym procesie?

## Scena s12. Expand and contract - format danych rezerwacji

**Temat ze slajdów:** 6.5-6.6 Dane, wycofanie i zamknięcie migracji
**Katalog:** `typescript/src/workshop/m8/s12_expandcontract` · **Test:** `scripts/warsztat.sh --lang ts test m8/s12`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Repozytorium rezerwacji zna tylko format csv, a zapis nowego formatu w tej samej kolumnie uniemożliwiłby wycofanie wydania. Przechodzimy na kolumnę `payload` w czterech krokach: podwójny zapis, odczyt z fallbackiem, backfill starych wierszy i contract.

**Zasada:** Expand and contract migruje dane tak, żeby w oknie wycofania stara wersja kodu czytała to, co zapisała nowa: najpierw rozszerzamy (nowe pole, podwójny zapis), potem przełączamy odczyt i uzupełniamy stare dane, a stary format usuwamy na końcu. Flaga ani wycofanie paczki z kodem nie cofną danych zapisanych w niezgodnym formacie.

**Efekt:** Repozytorium pisze i czyta tylko wersjonowany `payload`, a w kodzie nie ma już odwołań do starego formatu - migracja jest zamknięta. Ceną jest zamknięcie okna wycofania: po contract stara wersja nie widzi nowych danych, więc ten krok robi się dopiero, gdy powrót nie będzie potrzebny.

**Różnica względem Javy:** `Optional<Booking>` to `Booking | undefined`, rekord `BookingTable.Row` to osobna klasa `Row` eksportowana z `BookingTable.ts` (kolumny typu `string | null`), a test "brak referencji do starego formatu" przegląda pliki `.ts` kroku 4 i sprawdza, że dynamiczny `import()` pliku `CsvBookingFormat.ts` się nie udaje.

### Co widzimy

`BookingTable` ma starą kolumnę `csv` i nową, pustą kolumnę `payload` (migracja schematu "expand" już zrobiona). `start/BookingRepository.ts` zna tylko csv: `B1;anna@kino.pl;A5,A10;84.00`. Zapisanie nowego formatu "w miejscu" do tej samej kolumny uniemożliwiłoby wycofanie wydania.

```typescript
save(booking: Booking): void {
  this.table.put(booking.id, new Row(CsvBookingFormat.write(booking), null));
}
```

### Krok 1: expand + dual write

**W IDE:** nowa klasa `BookingPayloadFormat` (wersjonowany format `v2|id=...|email=...`); `save` pisze obie kolumny, `find` nadal czyta csv.
**Po:**

```typescript
this.table.put(booking.id, new Row(CsvBookingFormat.write(booking),
  BookingPayloadFormat.write(booking)));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s12` - `step1DualWriteKeepsRollbackToTheOldVersionSafe`: repozytorium ze startu czyta dane zapisane przez krok 1.
**Co powiedzieć:** wycofanie kodu jest bezpieczne, bo stary format wciąż powstaje.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s12 0 1`

### Krok 2: odczyt nowego formatu z fallbackiem

**W IDE:** ⌃⇧R → Extract to method `read(row)`: payload, a gdy go brak - csv.
**Po:**

```typescript
return row.payload != null
  ? BookingPayloadFormat.read(row.payload)
  : CsvBookingFormat.read(row.csv!);
```

**Uruchom:** test zielony; `step2ReadsNewFormatAndFallsBackForOldRows`.
**Co powiedzieć:** stare wiersze (sprzed kroku 1) nadal są czytelne. Zapis dalej podwójny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s12 1 2`

### Krok 3: backfill

**W IDE:** metoda `migrateAll` uzupełnia payload w wierszach, które go nie mają; zwraca licznik.
**Po:**

```typescript
if (row.payload == null) {
  this.table.put(id, new Row(row.csv,
    BookingPayloadFormat.write(CsvBookingFormat.read(row.csv!))));
  migrated++;
}
```

**Uruchom:** test zielony; `step3BackfillIsIdempotentAndPreparesTheContract` - przed backfillem wersja po contract gubi stary wiersz, po backfillu go widzi; drugie uruchomienie migruje 0 wierszy.
**Co powiedzieć:** kolejność ma znaczenie: backfill przed contract. Licznik to dowód w logu wdrożenia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s12 2 3`

### Krok 4: contract - usunięcie starego formatu

**W IDE:** usuń plik `CsvBookingFormat.ts` (VS Code nie ma Safe Delete - `npm run typecheck` pokaże wszystkie miejsca, które go jeszcze importują); `save` pisze tylko payload (`Row.withPayload`), `find` czyta tylko payload; `migrateAll` znika razem z fallbackiem.
**Po:**

```typescript
find(id: string): Booking | undefined {
  const payload = this.table.get(id)?.payload;
  return payload == null ? undefined : BookingPayloadFormat.read(payload);
}
```

**Uruchom:** test zielony; `step4HasNoReferenceToTheOldFormat` (żaden plik kroku nie wspomina starego formatu) i `step4ClosesTheRollbackWindow` (stara wersja nie widzi nowych danych).
**Co powiedzieć:** migracja kończy się po usunięciu kosztu legacy. Od tego momentu wycofanie kodu już nie wystarczy - dlatego contract robimy dopiero po zamknięciu okna wycofania. Usunięcie kolumny to kolejna, osobna migracja schematu.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s12 3 4`

### Rozwiązanie i uzasadnienie

Każdy krok jest wdrażalny i odwracalny do poprzedniego, aż do contract. Test "brak referencji do starego kodu" zamienia "zamknięcie migracji" z deklaracji w sprawdzalny warunek.

### Pułapki

- Contract przed backfillem - utrata starych wierszy.
- Dual write bez strategii rekoncyliacji (co, jeśli zapis jednej kolumny się nie uda?).
- Flaga "czytaj nowy format" przełączona bez sprawdzenia, że stara wersja nadal czyta dane.

### Pytanie do sali

Jak długo trzymać okno wycofania dla danych i kto decyduje o jego zamknięciu?

## Scena s13. Dokumentacja żywa kontra historyczna

**Temat ze slajdów:** 4.1 Dokumentacja według trwałości; 4.5 Dokumentacja żywa i historyczna
**Katalog:** `typescript/src/workshop/m8/s13_livingdocs` · **Test:** `scripts/warsztat.sh --lang ts test m8/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Ręcznie pisany `ROUTING.md` rozjechał się z `Routing.routes()`: twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Generujemy dokument z kodu przez `RoutingDoc`, pilnujemy go testem i dopisujemy właściciela oraz kryterium usunięcia każdej trasy.

**Zasada:** Dokumentacja żywa opisuje stan obecny, więc powinna powstawać z systemu albo być z nim sprawdzana, a historyczna (ADR, zamknięty opis zmian) jest niezmienna. Mieszanie tych ról kończy się dokumentem, który opisuje nieistniejący system albo gubi historię.

**Efekt:** Test porównuje zapisany `ROUTING.md` z wygenerowanym przy każdym buildzie, a każda trasa pokazuje właściciela i warunek usunięcia. Uzasadnienia decyzji nie wpisujemy do tego pliku, bo zniknęłyby przy regeneracji - ich miejsce jest w ADR.

**Różnica względem Javy:** `RoutingDoc.location()` liczy ścieżkę dokumentu z adresu modułu (`new URL('./ROUTING.md', import.meta.url)`), a nie z nazwy pakietu, a `main(String[])` to statyczna metoda `RoutingDoc.main()`, która zapisuje `ROUTING.md` obok modułu. Rekord `Routing.Route` to osobna klasa `Route` z polami `readonly`. Pliki `ROUTING.md` są takie same jak w Javie.

### Co widzimy

`start/Routing.ts` - `Routing.routes()` to routing fasady Strangler Fig jako kod - źródło prawdy. Obok leży ręcznie pisany `start/ROUTING.md`, który twierdzi, że raport obsługuje legacy, i nie zna operacji `cancel`. Test `startDocumentationHasDriftedFromCode` wypisuje rozjazd: `report: dokument mowi legacy, kod mowi new` i `cancel: brak w dokumencie`.

```text
| Operacja | Obsługuje |
| --- | --- |
| book | new |
| report | legacy |
```

### Krok 1: dokument generowany z kodu

**W IDE:** nowa klasa `RoutingDoc` z `render(routes)`, `location()` i `main()`, który zapisuje `ROUTING.md` obok kodu (ścieżka liczona z `import.meta.url`). Port nie ma osobnego uruchamiacza plików `.ts` (Node nie rozwiązuje importów `./Routing.js` na pliki `.ts`), więc `RoutingDoc.main()` wywołujemy jednorazowo przez vitest, np. z tymczasowego testu albo z konsoli debugowania VS Code przy zatrzymanym teście sceny. Snapshot `step1/` ma już wygenerowany plik.
**Po:**

```typescript
for (const route of routes) {
  md += '| ' + route.operation + ' | ' + route.target + ' |' + '\n';
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m8/s13` - `step1DocumentIsGeneratedFromCode`: zapisany plik równa się wygenerowanemu (przy rozjeździe komunikat `ROUTING.md nieaktualny - uruchom RoutingDoc.main()`).
**Co powiedzieć:** dokument żywy nie może się rozjechać, bo test porównuje go z kodem przy każdym buildzie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s13 0 1`

### Krok 2: architektura przejściowa w dokumencie

**W IDE:** ręcznie dopisz do konstruktora klasy `Route` pola `owner` i `removeWhen` (`npm run typecheck` pokaże wywołania do uzupełnienia); nowe kolumny w `render`; ponownie wygeneruj dokument (`RoutingDoc.main()`).
**Po:**

```typescript
new Route('cancel', 'legacy', 'zespol Sprzedaz',
  'CancelModule w trybie CANDIDATE przez 14 dni'),
```

**Uruchom:** test zielony; `step2EveryTransitionalRouteHasOwnerAndRemovalCriterion`.
**Co powiedzieć:** żywa dokumentacja operacyjna mówi, co działa dziś i kiedy to zniknie. Dlaczego tak zdecydowaliśmy - to ADR (historia, scena s07).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m8/s13 1 2`

### Rozwiązanie i uzasadnienie

Dokument żywy powstaje z kodu i jest sprawdzany testem, historyczny (ADR, zamknięty opis zestawu zmian) jest niezmienny. Mieszanie ról kończy się dokumentem, który opisuje nieistniejący system albo gubi historię.

### Pułapki

- Ręczna edycja wygenerowanego pliku.
- Generator bez testu zgodności - plik i tak się zestarzeje.
- Uzasadnienia decyzji wpisywane do dokumentu żywego (znikną przy następnej regeneracji).

### Pytanie do sali

Które dokumenty w waszym projekcie powinny być generowane z kodu, a które świadomie zamrożone?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min):** s01 (Branch by Abstraction, 15) → s03 (shadow, 15) → s04 (granice shadow, 12) → s06 (seria zmian, 10) → s08 (bramka kompilatora, 10) → s12 (expand and contract, 15). Pokazuje pełny łuk: szew, weryfikacja, bezpieczeństwo efektów, przegląd, narzędzia i dane.

**Ścieżka pełna (~150 min):** s01 → s02 → s03 → s04 (strategie migracji) → s05 → s06 (Boy Scout i przegląd) → s07 → s13 (dokumentacja) → s08 → s09 → s10 (narzędzia i bramki) → s11 → s12 (ryzyko, wdrożenie i dane). Między blokami krótkie pytanie do sali ze scen.
