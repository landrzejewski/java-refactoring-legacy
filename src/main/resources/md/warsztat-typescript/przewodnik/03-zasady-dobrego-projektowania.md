# Moduł 3. Zasady dobrego projektowania - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje zasady z modułu 3 jako serie bezpiecznych zmian: DRY, KISS, YAGNI, SOLID, spójność i sprzężenie, granice Clean Architecture, wzorzec jako decyzja odwracalna oraz inwarianty modelu. Każda scena ma kod wyjściowy (`start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`step1`, `step2`, ...). Prowadzący pracuje na żywo w `start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny nie powielają ćwiczeń z `md/zadania/03-zasady-dobrego-projektowania.md` (studium wyceny dostawy w `typescript/src/module3`): tamte ćwiczą jeden duży przykład, tu każda zasada ma własną, kilkuminutową scenę.

Port TypeScript: kod scen leży w `typescript/src/workshop/m3/sNN_.../{start,step1,...}`, testy vitest w `typescript/test/workshop/m3/sNN_.../`. Kwoty to `Decimal` z biblioteki decimal.js (odpowiednik `BigDecimal`). `Decimal` nie pamięta skali, więc tam, gdzie Java drukuje kwotę przez `toString()`, port drukuje `toFixed(2)`. Rekordy Javy to klasy z polami `readonly` w konstruktorze, a statyczne metody klas-punktów wejścia to funkcje modułu.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m3              # lista scen i kroków modułu 3
scripts/warsztat.sh --lang ts test m3/s01          # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh --lang ts test m3              # wszystkie sceny modułu (238 testów)
scripts/warsztat.sh --lang ts diff m3/s01 0 1      # co zmienia krok 1 względem start (0 = start)
scripts/warsztat.sh --lang ts diff m3/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m3/s12 3        # przeskok: start = snapshot kroku 3
scripts/warsztat.sh --lang ts next m3/s12          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m3/s12         # przywrócenie start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `--lang ts next m3/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `next` (albo `jump` do konkretnego snapshotu) i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: funkcja `confirmReservation()` w `Main.ts` (s11), `reservationController()` w `CinemaApplication.ts` (s12), `describe()` w `CinemaApp.ts` (s13) oraz klasa `TicketDesk` (s16). VS Code nie ma refaktoryzacji Introduce Parameter ani Change Signature - te miejsca poprawiasz ręcznie, a kompilator (czerwone podkreślenia, `npx tsc --noEmit`) wskaże każde wywołanie do zmiany.
- Refaktoryzacje VS Code: menu Refactor... (⌃⇧R) daje m.in. Extract to method in class, Extract to function, Extract to constant, Inline variable, Move to a new file / Move to file. Rename Symbol to F2, Quick Fix ⌘., Go to Definition F12, Find All References ⇧⌥F12. Odpowiednika Safe Delete nie ma: przed usunięciem symbolu sprawdź Find All References, a po usunięciu - kompilator.
- Testy uruchamiasz skryptem (`scripts/warsztat.sh --lang ts test m3/sNN`) albo z rozszerzenia Vitest w VS Code (ikona przy `describe`/`it`).
- Pułapki, których nie da się sprawdzić na edytowanym `start` (bo znikają po naprawie), testy dokumentują na kopii w kroku, który jeszcze ją ma (np. s04 krok 1, s09 krok 1, s15 krok 1).
- Test s13 czyta pliki źródłowe względem katalogu `typescript` - uruchamiaj go skryptem albo z katalogu `typescript` (tak działa vitest z konfiguracją projektu).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.1 DRY dotyczy wiedzy | 4 | `m3/s01_dryknowledge` | ~12 min |
| s02 | 2.2 Podobieństwo nie wystarcza | 3 | `m3/s02_similarity` | ~10 min |
| s03 | 2.3 Fałszywa abstrakcja | 2 | `m3/s03_falseabstraction` | ~8 min |
| s04 | 2.3 DRY w testach, niezależna wyrocznia | 2 | `m3/s04_drytests` | ~10 min |
| s05 | 2.4-2.5 KISS: złożoność wprowadzona i istotna | 2 | `m3/s05_kiss` | ~8 min |
| s06 | 2.6-2.7 YAGNI, 2.8 napięcia, 2.9 filtr decyzyjny | 3 | `m3/s06_yagni` | ~12 min |
| s07 | 3.2 SRP: jeden aktor zmiany | 3 | `m3/s07_srp` | ~12 min |
| s08 | 3.3 OCP: zamknięcie dla wybranej osi | 3 | `m3/s08_ocp` | ~10 min |
| s09 | 3.4 LSP i test kontraktowy | 2 | `m3/s09_lsp` | ~12 min |
| s10 | 3.5 ISP z perspektywy klienta | 2 | `m3/s10_isp` | ~8 min |
| s11 | 3.6 DIP, 5.1-5.2 reguła zależności a przepływ sterowania | 3 | `m3/s11_dip` | ~12 min |
| s12 | 5.3-5.6 przypadek użycia, dane na granicy, composition root | 4 | `m3/s12_cleanarchitecture` | ~18 min |
| s13 | 4.4 diagnostyka spójności, lista kontrolna "granica sprawdzana automatycznie" | 2 | `m3/s13_boundarycheck` | ~12 min |
| s14 | 6.1-6.4 Strategy i Adapter, wzorzec można usunąć | 3 | `m3/s14_reversiblepattern` | ~12 min |
| s15 | 7.4 model domeny i inwarianty | 2 | `m3/s15_invariants` | ~8 min |
| s16 | 4.4 sprzężenie protokołu i czasu | 2 | `m3/s16_temporalcoupling` | ~6 min |

Katalogi są względne wobec `typescript/src/workshop/`, testy leżą w odpowiednim katalogu pod `typescript/test/workshop/`.

## Scena s01. DRY - jedna wiedza, dwie reprezentacje

**Temat ze slajdów:** 2.1 DRY dotyczy wiedzy; 2.8 Napięcia między zasadami (nazwanie potwierdzonej reguły)
**Katalog:** `typescript/src/workshop/m3/s01_dryknowledge` · **Test:** `scripts/warsztat.sh --lang ts test m3/s01` (`S01EquivalenceTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `BoxOffice` reguła ceny biletu jest zapisana dwa razy - raz przy sprzedaży, raz przy zwrocie - różnym kodem. Nazywamy obie kopie, sprawdzamy testem, że liczą to samo, zostawiamy jedną i przenosimy ją do klasy `TicketPrice`.

**Zasada:** DRY mówi, że każda wiedza w systemie powinna mieć jedną autorytatywną reprezentację. Chodzi o wiedzę (regułę biznesową), a nie o podobny tekst: dwa różne fragmenty mogą kodować tę samą regułę, a dwa identyczne mogą być przypadkowo podobne.

**Efekt:** Zmiana zniżki studenckiej to teraz edycja jednego miejsca, a zwrot automatycznie używa aktualnego cennika. Reguły samego zwrotu zostają w `BoxOffice`, bo mają innego właściciela.

### Co widzimy

`BoxOffice` sprzedaje bilety i przyjmuje zwroty. Reguła ceny biletu (cena formatu, zniżka typu, seans poranny) jest zapisana dwa razy: w `sell` jako dwa `switch` i procent, w `refund` jako łańcuch `if` i mnożniki. Tekst jest inny, więc żaden detektor duplikatów nic nie znajdzie, ale wiedza jest ta sama. Zmiana zniżki studenckiej wymaga zgodnej edycji dwóch miejsc.

```typescript
// ile kosztował bilet
let paid = new Decimal('25.00');
if (ticket.format === '3D') {
  paid = new Decimal('32.00');
} else if (ticket.format === 'IMAX') {
  paid = new Decimal('40.00');
}
if (ticket.type === 'STUDENT') {
  paid = paid.times(new Decimal('0.75'));
} // ...SENIOR 0.70, CHILD 0.60, poranek -5.00
```

### Krok 1: Extract Method - nazwij drugą kopię wiedzy

**W IDE:** w `refund` zaznacz blok od `// ile kosztował bilet` do końca `if` z porankiem, ⌃⇧R > Extract to method in class 'BoxOffice', nazwa `paidFor`. VS Code utworzy metodę prywatną zwracającą `paid`. Usuń komentarz.
**Po:**

```typescript
refund(ticket: Ticket, hoursBeforeStart: number): Decimal {
  const paid = this.paidFor(ticket);
  // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s01` - 25 testów zielonych (5 przypadków x 5 wariantów).
**Co powiedzieć:** zanim scalimy wiedzę, musimy ją zobaczyć. Nazwa `paidFor` odpowiada na to samo pytanie co `sell`: ile kosztuje ten bilet?
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 0 1`

### Krok 2: Extract Method po stronie sprzedaży

**W IDE:** w `sell` zaznacz całe ciało metody, ⌃⇧R > Extract to method in class 'BoxOffice', nazwa `ticketPrice`.
**Po:**

```typescript
sell(ticket: Ticket): Decimal {
  return this.ticketPrice(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz obie kopie reguły to dwie prywatne metody o identycznej sygnaturze. Widać, że jedna jest zbędna.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 1 2`

### Krok 3: Substitute Algorithm - zwrot korzysta z jednej reguły

**W IDE:** w `refund` ręcznie zamień `this.paidFor(ticket)` na `this.ticketPrice(ticket)`, uruchom test, potem usuń `paidFor` (Find All References ⇧⌥F12 pokazuje zero użyć, a VS Code wyszarza nieużywaną prywatną metodę). Import `LocalTime` przestaje być potrzebny - usuń go (Quick Fix ⌘. > Remove unused declaration albo Organize Imports ⇧⌥O).
**Po:**

```typescript
const paid = this.ticketPrice(ticket);
```

**Uruchom:** test zielony - to dowód, że obie kopie liczyły to samo dla wszystkich przypadków (w tym zniżki, poranek i zwrot po starcie).
**Co powiedzieć:** Substitute Algorithm jest bezpieczny tylko pod testem równoważności. Gdyby kopie się rozjechały, test pokazałby, która reguła jest "prawdziwa" - i to byłaby rozmowa z biznesem, nie decyzja programisty.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 2 3`

### Krok 4: Extract Class - autorytatywne źródło wiedzy

**W IDE:** VS Code nie ma Extract Class, więc ruch robimy ręcznie w małych krokach: utwórz plik `TicketPrice.ts` z klasą `TicketPrice` i metodą `of`, przenieś do niej ciało `ticketPrice` (wytnij i wklej), a w `BoxOffice` dodaj pole `private readonly ticketPrice = new TicketPrice()` i zamień wywołania na `this.ticketPrice.of(ticket)`. W nowej klasie ⌃⇧R > Extract to method in class dla `basePrice` i `discountPercent`; dla `MORNING_DISCOUNT` Extract to constant (VS Code proponuje stałą modułu albo pole klasy - wybierz pole i dopisz `static readonly`). To samo w `BoxOffice` dla `REFUND_DEDUCTION`.
**Po:**

```typescript
export class BoxOffice {
  private static readonly REFUND_DEDUCTION = new Decimal('3.00');

  private readonly ticketPrice = new TicketPrice();

  sell(ticket: Ticket): Decimal {
    return this.ticketPrice.of(ticket);
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła ceny ma nazwę w języku problemu i jednego właściciela (cennik). `BoxOffice` zna już tylko własną wiedzę: regulamin zwrotów.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 3 4`

### Rozwiązanie i uzasadnienie

`step4/TicketPrice.ts` to jedyna reprezentacja wiedzy "ile kosztuje bilet". Scalenie było uzasadnione od razu, bez czekania na trzecią kopię: dwie kopie tej samej reguły to już ryzyko niezgodnej zmiany (slajd 2.8: reguła trzech pomaga czekać na kształt abstrakcji, ale nie definiuje DRY).

### Pułapki

- Szukanie duplikacji po tekście: żadne narzędzie nie znajdzie tej kopii, bo była inaczej napisana.
- Scalanie bez testu równoważności - jeśli kopie już się różniły, "cicho" zmieniamy zachowanie.
- Przeniesienie do `TicketPrice` także reguł zwrotu - to inna wiedza z innym właścicielem.

### Pytanie do sali

Gdzie w waszym systemie ta sama reguła żyje w SQL, w kodzie i w dokumentacji jednocześnie? Co jest jej autorytatywnym źródłem?

## Scena s02. Podobieństwo to nie duplikacja

**Temat ze slajdów:** 2.2 Podobieństwo nie wystarcza; 2.3 Tymczasowe powtórzenie kodu bywa bezpiecznym etapem
**Katalog:** `typescript/src/workshop/m3/s02_similarity` · **Test:** `scripts/warsztat.sh --lang ts test m3/s02` (`S02EquivalenceTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Klasa `ServiceFee` scala opłatę rezerwacyjną online i potrącenie przy zwrocie tylko dlatego, że obie wyglądają jak "kwota razy liczba sztuk". Wklejamy wspólny kod z powrotem do obu wywołujących, usuwamy martwe gałęzie i dajemy każdej kwocie stałą u jej właściciela.

**Zasada:** Podobny kod to jeszcze nie ta sama wiedza - rozstrzyga pytanie, czy dwie reguły zmieniają się razem i z tego samego powodu. Wspólna metoda dla niezależnych reguł tworzy fałszywą zależność, a chwilowe powtórzenie kodu jest bezpiecznym etapem ich rozdzielania.

**Efekt:** Marketing może zmienić opłatę rezerwacyjną bez ryzyka dla regulaminu zwrotów, a parametr `units = 1` znika. Płacimy za to dwiema podobnie wyglądającymi linijkami, co jest w porządku, bo kodują różną wiedzę.

**Różnica względem Javy:** zagnieżdżony `ServiceFee.Kind` to w porcie osobny eksportowany enum `Kind` w pliku `ServiceFee.ts`. Po kroku 1 plik zawiera już tylko enum. We wstawionych argumentach kroku 1 stoi rzutowanie `(Kind.REFUND as Kind)` - bez niego kompilator TS sam zgłasza porównanie stałych, które nie mają części wspólnej (TS2367), i krok by się nie kompilował. Krok 2 usuwa rzutowanie razem z warunkiem.

### Co widzimy

Ktoś "zDRYował" dwie reguły, bo wyglądały tak samo ("stała kwota razy liczba sztuk"): opłatę rezerwacyjną online (2.00 za bilet, właściciel: sprzedaż online i marketing) i potrącenie przy zwrocie (3.00 za zwrot, właściciel: regulamin zwrotów). Powstała wspólna metoda z przełącznikiem, a `RefundDesk` musi podać `units = 1`, choć ten parametr pasuje tylko drugiej regule.

```typescript
static of(kind: Kind, units: number): Decimal {
  const perUnit = kind === Kind.ONLINE_BOOKING ? new Decimal('2.00') : new Decimal('3.00');
  return perUnit.times(units).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
}
```

### Krok 1: Inline Method - wspólny kod wraca do wywołujących

**W IDE:** VS Code nie ma Inline Method (jest tylko Inline variable), więc wklejamy ręcznie: skopiuj ciało `ServiceFee.of` w miejsce każdego wywołania, podstaw argumenty (`kind` -> `Kind.ONLINE_BOOKING as Kind` / `Kind.REFUND as Kind`, `units` -> `ticketPrices.length` / `1`), potem usuń klasę `ServiceFee`. Zostaje samo `enum Kind`.
**Po:**

```typescript
// "as Kind" - wstawiony argument; bez rzutowania kompilator sam zauważy stały warunek
const perUnit = (Kind.REFUND as Kind) === Kind.ONLINE_BOOKING
  ? new Decimal('2.00') : new Decimal('3.00');
const fee = perUnit.times(1).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s02` - 16 testów zielonych.
**Co powiedzieć:** chwilowo mamy więcej powtórzonego kodu - i to jest w porządku. Powtórzenie kodu to bezpieczny etap rozdzielania fałszywie scalonej wiedzy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 0 1`

### Krok 2: Simplify - usuń martwe gałęzie

**W IDE:** ręcznie: warunek porównujący stałe enuma jest zawsze prawdziwy (`OnlineCheckout`) albo zawsze fałszywy (`RefundDesk`) - zostaw właściwą gałąź, usuń rzutowanie, mnożenie przez 1 i zbędne `toDecimalPlaces`. Gdy nikt nie importuje już `Kind` (⇧⌥F12), usuń plik `ServiceFee.ts`.
**Po:**

```typescript
return Decimal.max(share.minus(new Decimal('3.00')), 0);
```

**Uruchom:** test zielony.
**Co powiedzieć:** po uproszczeniu widać, że obie reguły nie miały ze sobą nic wspólnego poza kształtem wyrażenia.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 1 2`

### Krok 3: Extract Constant u właściciela reguły

**W IDE:** ⌃⇧R > Extract to constant na `new Decimal('2.00')` w `OnlineCheckout` (nazwa `BOOKING_FEE_PER_TICKET`) i na `new Decimal('3.00')` w `RefundDesk` (nazwa `REFUND_DEDUCTION`); w obu przypadkach jako pole `private static readonly` klasy.
**Po:**

```typescript
private static readonly BOOKING_FEE_PER_TICKET = new Decimal('2.00');  // OnlineCheckout
private static readonly REFUND_DEDUCTION = new Decimal('3.00');        // RefundDesk
```

**Uruchom:** test zielony.
**Co powiedzieć:** każda stała ma nazwę w języku domeny i mieszka u swojego właściciela. Marketing może znieść opłatę rezerwacyjną w promocji, nie dotykając regulaminu zwrotów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 2 3`

### Rozwiązanie i uzasadnienie

Dwie reguły, dwie stałe, dwóch właścicieli. Kod jest podobny, wiedza niezależna - zgodnie z tabelą ze slajdu 2.2 ("dwa konteksty mają dziś podobną regułę: może być przypadkowe; ustal relację reguł").

### Pułapki

- Wspólna metoda dla niezależnych reguł tworzy fałszywą zależność: zmiana jednej grozi zmianą drugiej.
- Parametr, który ma sens tylko dla jednego wywołującego (`units = 1`), to sygnał fałszywej abstrakcji.
- Odwrotny błąd: rozdzielenie reguły, która naprawdę jest jedna (s01).

### Pytanie do sali

Jakie pytanie zadać biznesowi, żeby rozstrzygnąć, czy dwie podobne reguły to jedna wiedza? (Podpowiedź: "czy zmieniają się razem i z tego samego powodu?")

## Scena s03. Fałszywa abstrakcja z flagami

**Temat ze slajdów:** 2.3 Fałszywa abstrakcja: zabezpiecz testami, przenieś kod z powrotem, wydziel tylko potwierdzoną wiedzę
**Katalog:** `typescript/src/workshop/m3/s03_falseabstraction` · **Test:** `scripts/warsztat.sh --lang ts test m3/s03` (`S03EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Pricing.price` obsługuje bilety i karnety przez pięć parametrów, w tym flagi `boolean`, z których każdy wywołujący potrzebuje tylko części. Cofamy abstrakcję do obu kas, upraszczamy martwe gałęzie i sprawdzamy, czy zostało coś wspólnego.

**Zasada:** Fałszywa abstrakcja to wspólny kod, który łączy konteksty bez wspólnej wiedzy i rośnie przez kolejne flagi. Kolejność naprawy: zabezpiecz testami, przenieś kod z powrotem do kontekstów, a wydziel tylko tę wiedzę, która się potwierdzi.

**Efekt:** Zostają dwie proste klasy, a `PassCounter` nie wie nic o formatach i okularach. Wspólnej wiedzy nie było wcale, więc świadomie niczego nowego nie wydzielamy.

### Co widzimy

`Pricing.price` wycenia bilety i karnety (reguła sceny: karnet to 20.00 za wejście na seans 2D), sterowana flagami `boolean`. Kasa biletowa podaje `quantity = 1` i `pass = false`, kasa karnetów podaje format, poranek i okulary "na wszelki wypadek".

```typescript
price(format: string, quantity: number, pass: boolean,
  morning: boolean, ownGlasses: boolean): Decimal {
  let unit: Decimal;
  if (pass) {
    unit = new Decimal('20.00');
  } else { /* format, poranek */ }
  if (format === '3D' && !ownGlasses && !pass) { /* okulary */ }
```

```typescript
return this.pricing.price('2D', entries, true, false, true);   // PassCounter
```

### Krok 1: Inline Method do obu wywołujących

**W IDE:** ręcznie (VS Code nie ma Inline Method): skopiuj ciało `Pricing.price` do `TicketCounter.ticket` i `PassCounter.pass`, a stałe argumenty zapisz jako lokalne stałe na początku metody (`const pass: boolean = false;`) - tak samo wygląda wynik Inline Method w IntelliJ. Usuń klasę `Pricing` i pole `pricing`.
**Po:**

```typescript
ticket(format: string, morning: boolean, ownGlasses: boolean): Decimal {
  const pass: boolean = false;
  let unit: Decimal;
  if (pass) {
```

**Różnica względem Javy:** stałe flagi mają jawny typ (`const format: string = '2D'`, `const pass: boolean = true`). Bez niego TS zawęziłby je do typów literałowych i odrzucił "martwe" gałęzie `switch`/`if` jako błędy porównania, więc krok by się nie kompilował. Adnotacje znikają w kroku 2 razem z flagami.

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s03` - 18 testów zielonych.
**Co powiedzieć:** najpierw cofamy abstrakcję, dopiero potem myślimy, czy jest w tym kodzie coś naprawdę wspólnego.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s03 0 1`

### Krok 2: Inline Variable i Simplify - martwe gałęzie znikają

**W IDE:** ⌃⇧R > Inline variable na `pass`, `morning`, `ownGlasses`, `format` w `PassCounter` (i na `pass` w `TicketCounter`), potem ręcznie uprość warunki ze stałymi (`if (true)`, `!true`) i usuń martwe gałęzie. Na koniec ⌃⇧R > Extract to constant dla stałych kwot (pola `private static readonly`).
**Po:**

```typescript
pass(entries: number): Decimal {
  return PassCounter.PRICE_PER_ENTRY.times(entries);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po rozdzieleniu okazuje się, że wspólnej wiedzy nie ma wcale - karnet nie wie nic o formatach i okularach. Nie wydzielamy nic nowego.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s03 1 2`

### Rozwiązanie i uzasadnienie

Dwie proste klasy zamiast jednej metody z pięcioma parametrami. Kolejność z slajdu 2.3: testy (tu równoważności) - kod z powrotem do kontekstów - wydzielenie tylko potwierdzonej wiedzy (tu: żadnej).

### Pułapki

- Dokładanie kolejnej flagi do fałszywej abstrakcji "bo już jest wspólna metoda".
- Rozdzielanie bez testów: przy flagach łatwo pomylić kolejność argumentów `boolean`.

### Pytanie do sali

Ile flag `boolean` ma najdłuższa metoda w waszym projekcie? Ilu wywołujących używa każdej kombinacji?

## Scena s04. DRY w testach i niezależna wyrocznia

**Temat ze slajdów:** 2.3 DRY w testach: wspólne fabryki danych tak, liczenie oczekiwanej wartości algorytmem produkcyjnym nie
**Katalog:** `typescript/src/workshop/m3/s04_drytests` · **Test:** `scripts/warsztat.sh --lang ts test m3/s04` (`S04SpecsTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Specyfikacja cen `TicketPriceSpecs` ma przypadki zaszyfrowane w napisach i liczy oczekiwaną cenę tym samym wzorem co produkcja. Najpierw nadajemy przypadkom czytelne nazwy, potem zastępujemy wyliczanie oczekiwania kwotami policzonymi ręcznie z regulaminu.

**Zasada:** DRY w testach obejmuje fabryki danych i wspólne helpery, ale nie oczekiwaną wartość: test liczący ją algorytmem produkcyjnym traci niezależną wyrocznię. DAMP oznacza, że każdy przypadek czyta się jak zdanie z regulaminu, nawet kosztem pewnego powtórzenia.

**Efekt:** Specyfikacja łapie błędną zniżkę studencką i mówi, który przypadek, jaka kwota oczekiwana i jaka faktyczna. Koszt: przy zmianie cennika kwoty w przykładach trzeba przeliczyć ręcznie - i to jest zamierzone.

**Różnica względem Javy:** mapy taryfy to `ReadonlyMap`, a brak klucza (w Javie NPE) port zapisuje jako `get(...)!`. Poza tym scena jest wierną kopią.

### Co widzimy

Kod produkcyjny sceny (`Tariff`, `TicketPrice`) jest stabilny. Refaktoryzujemy specyfikację cen `TicketPriceSpecs` - w projekcie byłby to plik testowy vitest, tu leży w `src`, żeby działał mechanizm start/stepN. Metoda `run` zwraca listę niespełnionych przypadków. Start jest "maksymalnie DRY": przypadki zaszyfrowane w stringach, a oczekiwana cena liczona z tej samej taryfy i tym samym wzorem co produkcja.

```typescript
private static readonly SPECS = Object.freeze(['IMAX N 20', '3D S 11', '2D E 18', '2D C 10']);
...
const base = price.tariff().basePrices.get(format)!;
const expected = base.minus(discount)
  .minus(start.hour < 12 ? new Decimal('5.00') : new Decimal(0));
```

### Krok 1: DAMP - nazwane przykłady zamiast szyfru

**W IDE:** ręcznie: zamień pętlę po `SPECS` na cztery wywołania `this.check(price, 'student na porannym 3D', '3D', 'STUDENT', LocalTime.of(11, 0), failures)`. Usuń parsowanie stringa. Wyliczenie oczekiwania wydziel (⌃⇧R > Extract to method in class) jako `expectedFromTariff`. Komunikat błędu: nazwa przykładu, oczekiwana i faktyczna cena (`toFixed(2)`).
**Po:**

```typescript
this.check(price, 'student na porannym 3D', '3D', 'STUDENT', LocalTime.of(11, 0), failures);
...
const expected = this.expectedFromTariff(price.tariff(), format, type, start);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s04` - 5 testów zielonych; test `step1IsReadableButStillBlindToTheBug` pokazuje, że z błędną taryfą (zniżka studencka 20% zamiast 25%) krok 1 nadal nic nie zgłasza.
**Co powiedzieć:** czytelność poprawiona, ale test wciąż jest kopią algorytmu. Jeśli algorytm jest zły, test jest zły tak samo.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 0 1`

### Krok 2: Jawna oczekiwana wartość - niezależna wyrocznia

**W IDE:** VS Code nie ma Change Signature, więc ręcznie: dodaj do `check` parametr `expected: string` (przed `failures`), kompilator wskaże cztery wywołania - wpisz w nich ręcznie policzone kwoty (40.00, 19.00, 17.50, 10.00). Usuń `expectedFromTariff` (po zmianie nie ma już odwołań) i import `Tariff`.
**Po:**

```typescript
this.check(price, 'student na porannym 3D', '3D', 'STUDENT', LocalTime.of(11, 0), '19.00', failures);
```

**Uruchom:** test zielony; `step2CatchesBrokenStudentDiscount` pokazuje komunikat `student na porannym 3D: oczekiwano 19.00, jest 20.60`.
**Co powiedzieć:** wspólny helper `check` zostaje - współdzielenie fabryki przypadku i formatu komunikatu to dobre DRY w testach. Wyrocznia musi być niezależna od kodu, który sprawdza.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 1 2`

### Rozwiązanie i uzasadnienie

DAMP (Descriptive And Meaningful Phrases): każdy przypadek czyta się jak zdanie z regulaminu, oczekiwanie pochodzi z regulaminu, nie z taryfy. Kwoty policz na sali: 32.00 - 25% = 24.00, rano -5.00 = 19.00.

### Pułapki

- "Test sprawdza, czy kod robi to, co robi" - oczekiwanie liczone kodem produkcyjnym lub jego kopią.
- Przesadne DRY w testach: jeden parametryzowany helper z szyfrem zamiast czytelnych przykładów (w vitest: `it.each` z tablicą nieopisanych krotek).
- Odwrotna skrajność: kopiowanie całego przygotowania danych w każdym teście zamiast fabryki.

### Pytanie do sali

W którym waszym teście oczekiwana wartość jest wyliczana, a nie wpisana? Co by się stało, gdyby wzór był zły?

## Scena s05. KISS - złożoność wprowadzona kontra istotna

**Temat ze slajdów:** 2.4-2.5 KISS: prostota po poprawności; ukryty przepływ przez refleksję
**Katalog:** `typescript/src/workshop/m3/s05_kiss` · **Test:** `scripts/warsztat.sh --lang ts test m3/s05` (`S05EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `SeatCounter` wybiera rodzaj miejsc napisem i dynamicznym wywołaniem metody po nazwie, a wolne miejsca liczy wyrażeniem regularnym. Zastępujemy to dwiema jawnymi metodami i zwykłą pętlą po znakach.

**Zasada:** KISS to najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania. Celuje w złożoność wprowadzoną (refleksja, sprytne mechanizmy), a złożoność istotną zostawia, tylko ją nazywa - nie myl go z najmniejszą liczbą linii.

**Efekt:** Literówka w rodzaju miejsc nie przejdzie kompilacji, a Find All References znów pokazuje przepływ. Reguły istotne, jak miejsce zablokowane czy początek strefy VIP, zostają i pilnuje ich test.

**Różnica względem Javy:** refleksję (`getDeclaredMethod(kind + "Rows")`) zastępuje dynamiczne wywołanie po nazwie: ``Reflect.get(this, `${kind}Rows`)``. Zapach jest ten sam - nazwa metody w napisie, metody wierszy wyglądają na nieużywane, literówka wybucha w runtime (`IllegalStateError`). Strumienie z Javy to operacje na tablicach (`Array.from`, `filter`, `map`), a regex z nazwaną grupą zostaje (`matchAll`).

### Co widzimy

`SeatCounter` liczy wolne miejsca w sali (`'.'` wolne, `'X'` zajęte, `'B'` zablokowane, `' '` przejście) oraz wolne miejsca VIP. Rodzaj miejsc wybierany jest stringiem i wywołaniem dynamicznym, a liczenie idzie przez wyrażenie regularne z nazwaną grupą. VS Code wyszarza `allRows` i `vipRows` jako nieużywane (declared but never read).

```typescript
const rows: unknown = Reflect.get(this, `${kind}Rows`);
if (typeof rows !== 'function') {
  throw new IllegalStateError(`nieznany rodzaj miejsc: ${kind}`);
}
const selected = (rows as (hall: Hall) => Iterable<string>).call(this, hall);
return [...selected]
  .flatMap((row) => [...row.matchAll(SeatCounter.FREE)].map((m) => m.groups?.['seat']))
  .length;
```

### Krok 1: Replace Parameter with Explicit Methods

**W IDE:** ręcznie: zamiast `this.free(hall, 'all')` i `this.free(hall, 'vip')` utwórz `freeSeats(hall)` i `freeVipSeats(hall)`, które wołają wspólne `countFree(rows: Iterable<string>)`. Usuń `free` z `Reflect.get`, rzutowaniem i `IllegalStateError`.
**Po:**

```typescript
summary(hall: Hall): string {
  return `wolne: ${this.freeSeats(hall)}, wolne VIP: ${this.freeVipSeats(hall)}`;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s05` - 12 testów zielonych.
**Co powiedzieć:** literówka w `'vip'` wybuchała dopiero w runtime; teraz nie przejdzie kompilacji. Przepływ widać w IDE (⇧⌥F12 Find All References znów działa, F12 prowadzi do definicji).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s05 0 1`

### Krok 2: Substitute Algorithm - zwykłe pętle

**W IDE:** ręcznie zastąp tablicę numerów rzędów przez `slice` od pierwszego rzędu VIP (indeks obcięty do zakresu przez `Math.min(Math.max(...))`), a regex przez pętlę po znakach porównującą ze stałą `FREE = '.'`.
**Po:**

```typescript
private freeIn(rows: readonly string[]): number {
  let free = 0;
  for (const row of rows) {
    for (const seat of row) {
      if (seat === SeatCounter.FREE) {
        free++;
      }
    }
  }
  return free;
}
```

**Uruchom:** test zielony, także dla rzędu z `'B'` i przejściem.
**Co powiedzieć:** złożoność istotna zostaje (co to jest wolne miejsce, od którego rzędu VIP, obcięcie `vipFromRow` do zakresu), ale jest nazwana. Zniknęła tylko złożoność wprowadzona.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s05 1 2`

### Rozwiązanie i uzasadnienie

KISS to najmniej złożone rozwiązanie, które **poprawnie** realizuje wymagania. Test z zablokowanym miejscem i przejściem pilnuje, że uproszczenie nie zgubiło przypadku.

### Pułapki

- Uproszczenie, które pomija przypadek brzegowy (np. `'B'` liczone jako wolne) - "prostsze, ale błędne".
- Mylenie KISS z najmniejszą liczbą linii: jednolinijkowy regex jest krótszy, ale trudniejszy.

### Pytanie do sali

Jaki "sprytny" mechanizm (dynamiczne wywołania po nazwie, dekoratory, konwencje nazw plików) w waszym kodzie ukrywa przepływ sterowania? Czy daje coś, czego nie da zwykłe wywołanie?

## Scena s06. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.6-2.7 YAGNI i czego nie zabrania; 2.8 Napięcia (ogólny silnik hipotetycznych taryf); 2.9 Filtr decyzyjny
**Katalog:** `typescript/src/workshop/m3/s06_yagni` · **Test:** `scripts/warsztat.sh --lang ts test m3/s06` (`S06EquivalenceTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `Map` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy mapę typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba.

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s06` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s06 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s06 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s06 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą filtr ze slajdu 2.9: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu typów została jedna klasa.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s07. SRP - raport dla dwóch aktorów

**Temat ze slajdów:** 3.2 SRP: jeden aktor zmiany; 3.7 Błędne uproszczenia SOLID ("klasa robi jedną rzecz")
**Katalog:** `typescript/src/workshop/m3/s07_srp` · **Test:** `scripts/warsztat.sh --lang ts test m3/s07` (`S07EquivalenceTest.test.ts`)
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

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `revenue` nadal wiąże oba światy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s07 0 1`

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
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z s02 w kontekście SRP.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s07 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości. Obie sekcje są eksportowane (w Javie byłyby package-private) - TypeScript nie ma widoczności pakietowej, więc granicę "tylko dla `DailyReport`" wyznacza tu konwencja, a w większym projekcie reguła importów (jak w s13).

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w module `reportUtils.ts`) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. OCP na wybranej osi - formaty seansu

**Temat ze slajdów:** 3.3 OCP: zamknięcie dla wybranej osi; nie każdy `switch` narusza OCP
**Katalog:** `typescript/src/workshop/m3/s08_ocp` · **Test:** `scripts/warsztat.sh --lang ts test m3/s08` (`S08EquivalenceTest.test.ts`, `S08NewFormatTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Wiedza o formatach seansu jest rozsiana po trzech `switch` na napisach, więc nowy format wymaga edycji wielu miejsc. Zamieniamy napisy na enum, przenosimy do typu `Format` dane formatów i dodajemy 4DX jedną linią.

**Zasada:** OCP to możliwość rozszerzenia wybranego zachowania bez modyfikowania stabilnej części. Zamyka się kod na jedną, realnie rosnącą oś zmian, nie na wszystkie naraz, a nie każdy `switch` łamie OCP - wyczerpujący `switch` na enumie (z `assertNever` zamiast `default`) bywa dobrym modelem zamkniętego zbioru.

**Efekt:** Dodanie formatu to zmiana tylko w `Format`, a `ScreeningOffer` nie zmienia się ani o znak. Na inne osie, na przykład nową dopłatę, klasa nadal nie jest zamknięta - i nie musi.

**Różnica względem Javy:** w kroku 1 enum TS ma wartości-kody (`TWO_D = '2D'`) i osobną funkcję `parseFormat`, a `switch` bez `default` z Javy to `default: return assertNever(format)` - efekt ten sam: nowa wartość enuma to błąd kompilacji w każdym `switch`. Enum TS nie może mieć pól, więc w krokach 2-3 `Format` jest klasą z instancjami statycznymi i prywatnym konstruktorem, który sam dopisuje instancję do listy `ALL` (odpowiednik `values()`) - nowy format to nadal jedna linia.

### Co widzimy

Wiedza o formatach (2D, 3D, IMAX) jest rozsiana po trzech `switch` na stringu: cena bazowa, okulary 3D, etykieta. Kino kupuje salę 4DX - trzeba edytować każdy `switch`, a zapomniany trafi do `default`.

```typescript
let glasses: Decimal;
switch (format) {
  case '3D': glasses = ownGlasses ? new Decimal(0) : new Decimal('3.00'); break;
  default: glasses = new Decimal(0);
}
```

### Krok 1: Replace Type Code with Enum

**W IDE:** utwórz `Format.ts` z `enum Format { TWO_D = '2D', THREE_D = '3D', IMAX = 'IMAX' }` i funkcją `parseFormat(code)`; w `ScreeningOffer` przełącz `switch` na enum, a `default` zastąp `return assertNever(format)` (z `shared/assertNever.ts`).
**Po:**

```typescript
switch (format) {
  case Format.THREE_D: glasses = ownGlasses ? new Decimal(0) : new Decimal('3.00'); break;
  case Format.TWO_D:
  case Format.IMAX: glasses = new Decimal(0); break;
  default: return assertNever(format);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s08` - 18 testów zielonych.
**Co powiedzieć:** to jeszcze nie OCP, ale już bezpieczny, zamknięty zbiór: nowa wartość enuma to błąd kompilacji w każdym `switch`, który jej nie obsłuży (`assertNever` przyjmuje tylko typ `never`). Dla małego, stabilnego zbioru to często wystarcza.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s08 0 1`

### Krok 2: Dane zamiast gałęzi - wiedza przeniesiona do typu `Format`

**W IDE:** ręcznie zamień enum na klasę `Format` z prywatnym konstruktorem i polami `basePrice`, `needsGlasses`, `label`; wartości to `static readonly TWO_D = new Format('2D', new Decimal('25.00'), false, '2D')` itd., a `parseFormat` staje się `Format.parse`. W `ScreeningOffer` zastąp każdy `switch` odczytem pola (Replace Conditional with Polymorphism w wersji "dane").
**Po:**

```typescript
price(code: string, ownGlasses: boolean): Decimal {
  const format = Format.parse(code);
  const glasses = format.needsGlasses && !ownGlasses ? ScreeningOffer.GLASSES : new Decimal(0);
  return format.basePrice.plus(glasses);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `ScreeningOffer` jest zamknięta na oś "format seansu". Nie jest zamknięta na inne osie (nowa dopłata za fotel premium ją zmieni) - i nie musi.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s08 1 2`

### Krok 3: Nowy format 4DX - jedna linia

**W IDE:** w `Format` dopisz `static readonly FOUR_DX = new Format('4DX', new Decimal('45.00'), true, '4DX - ruchome fotele');`.
**Po:**

```typescript
static readonly IMAX = new Format('IMAX', new Decimal('40.00'), false, 'IMAX - ekran laserowy');
static readonly FOUR_DX = new Format('4DX', new Decimal('45.00'), true, '4DX - ruchome fotele');
```

**Uruchom:** test zielony; `S08NewFormatTest` sprawdza 4DX: 48.00 bez własnych okularów, 45.00 z własnymi.
**Co powiedzieć:** `scripts/warsztat.sh --lang ts diff m3/s08 2 3` pokazuje zmianę tylko w `Format` - `ScreeningOffer` nie zmieniła się ani o znak. To jest OCP: rozszerzenie wybranego zachowania bez modyfikacji stabilnej części.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s08 2 3`

### Rozwiązanie i uzasadnienie

Formaty to dane, więc typ z polami (w Javie enum, w TS klasa z instancjami statycznymi) jest prostszy niż hierarchia klas. Polimorfizm klas opłaciłby się, gdyby formaty miały różne algorytmy (np. dynamiczną cenę 4DX), a nie różne liczby.

### Pułapki

- "Każdy `switch` łamie OCP" - wyczerpujący `switch` na enumie (albo unii literałów) z `assertNever` to często dobry model zamkniętego zbioru.
- Próba zamknięcia klasy na wszystkie osie naraz: interfejsy dla dopłat, formatów, typów biletów "na zapas".

### Pytanie do sali

Która oś zmian w waszym systemie rośnie najszybciej? Czy kod jest zamknięty właśnie na nią?

## Scena s09. LSP i test kontraktowy

**Temat ze slajdów:** 3.4 LSP: substytucja behawioralna; `UnsupportedOperationException` nie zawsze łamie LSP - ocena zaczyna się od kontraktu
**Katalog:** `typescript/src/workshop/m3/s09_lsp` · **Test:** `scripts/warsztat.sh --lang ts test m3/s09` (`S09ContractTest.test.ts`, `S09EquivalenceTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReadOnlyHall` dziedziczy po `Hall`, ale na `reserve` rzuca wyjątek, więc łamie kontrakt sali bazowej. Wydzielamy interfejs odczytu `SeatMap` i zastępujemy dziedziczenie delegacją.

**Zasada:** LSP wymaga, by podtyp dało się podstawić za typ bazowy bez zmiany zachowania: nie może wzmacniać warunków wstępnych ani osłabiać końcowych. Kompilator tego nie sprawdzi, dlatego ten sam test kontraktowy uruchamia się dla każdej implementacji, a `UnsupportedOperationError` (odpowiednik `UnsupportedOperationException`) ocenia się względem kontraktu typu bazowego.

**Efekt:** Sala archiwalna spełnia tylko kontrakt odczytu, który naprawdę obiecuje, a przekazanie jej do kasy kończy się błędem kompilacji zamiast wyjątkiem w runtime. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód.

**Różnica względem Javy:** TypeScript nie ma `final` ani chronionego przeciążenia konstruktora. W `start` i kroku 1 `Hall` ma jeden konstruktor `Hall(capacity, alreadyTaken = [])` z drugim parametrem "dla podklas"; w kroku 2 parametr znika, a "final" jest zapisane w dokumentacji klasy (`@sealed`). Typowanie strukturalne działa na korzyść lekcji: w kroku 2 `ReadOnlyHall` nie ma `reserve`, więc kompilator nie pozwala przekazać jej do kasy ani dodać do listy sal w teście kontraktowym. W `S09ContractTest` sale z każdego kroku pasują strukturalnie do widoku testu `HallUnderTest` bez adapterów - adapter `readOnly(...)` potrzebny jest tylko sali archiwalnej z kroku 2.

### Co widzimy

`Hall` ma udokumentowany kontrakt `reserve`: wolne miejsce zostaje zajęte, `freeSeats()` maleje o 1, zajęte miejsce daje `IllegalStateError`. Kontrakt nie przewiduje odmowy. `ReadOnlyHall extends Hall` (plan zamkniętego seansu dla raportów) nadpisuje `reserve` rzucając `UnsupportedOperationError` - wzmacnia warunek wstępny do "nigdy".

```typescript
export class ReadOnlyHall extends Hall {
  constructor(capacity: number, taken: Iterable<number>) {
    super(capacity, taken);
  }

  override reserve(_seat: number): void {
    throw new UnsupportedOperationError('sala archiwalna - tylko do odczytu');
  }
}
```

Pokaż `S09ContractTest`: ten sam zestaw sprawdzeń (`obeysReserveContract`) uruchamiany dla każdej implementacji. `readOnlyHallAsSubclassBreaksTheReserveContract` dokumentuje, że sala archiwalna go łamie.

### Krok 1: Extract Interface - rola odczytu

**W IDE:** VS Code nie wydziela interfejsu z klasy, więc ręcznie: utwórz `SeatMap.ts` z metodami `isFree`, `freeSeats`, `capacity` (skopiuj sygnatury z `Hall`), dopisz `implements SeatMap` w `Hall`, a w `OccupancyReport.describe` zmień typ parametru na `SeatMap` (to, co w IntelliJ robi opcja "Use interface where possible").
**Po:**

```typescript
export interface SeatMap {
  isFree(seat: number): boolean;

  freeSeats(): number;

  capacity(): number;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s09` - 13 testów zielonych; kontrakt odczytu (`everySeatMapObeysTheReadContract`) spełniają obie sale.
**Co powiedzieć:** sala archiwalna jest doskonałym `SeatMap` - problemem nie jest ona, tylko dziedziczenie po typie, którego kontraktu nie spełnia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s09 0 1`

### Krok 2: Replace Inheritance with Delegation

**W IDE:** ręcznie: w `ReadOnlyHall` zmień `extends Hall` na `implements SeatMap`, dodaj pole `snapshot: Hall` i trzy metody delegujące (Quick Fix ⌘. > Implement interface 'SeatMap' wygeneruje ich szkielety). Konstruktor rezerwuje zajęte miejsca w prywatnej kopii. Usuń nadpisane `reserve`. W `Hall` usuń drugi parametr konstruktora i dopisz w dokumentacji klasy, że jest zamknięta na dziedziczenie (`@sealed`).
**Po:**

```typescript
export class ReadOnlyHall implements SeatMap {
  private readonly snapshot: Hall;

  constructor(capacity: number, taken: Iterable<number>) {
    this.snapshot = new Hall(capacity);
    for (const seat of taken) {
      this.snapshot.reserve(seat);
    }
  }
```

**Uruchom:** test zielony. Spróbuj na żywo przekazać `ReadOnlyHall` do `BoxOffice.sell` - kompilator odmówi (brak `reserve`, a prywatne pola `Hall` i tak nie pozwalają podstawić innej klasy).
**Co powiedzieć:** zamiast wyjątku w runtime mamy błąd kompilacji. Test kontraktowy `Hall` nie ma już komu się nie udać, bo sala archiwalna nie obiecuje rezerwacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s09 1 2`

### Rozwiązanie i uzasadnienie

Dwie role, dwa kontrakty, dwa testy kontraktowe. Test daje dowody zgodności dla sprawdzonych stanów, nie formalny dowód LSP. Alternatywa równie poprawna: osłabić kontrakt bazowy (np. `canReserve(): boolean` w kontrakcie) - wtedy `UnsupportedOperationError` przestaje łamać LSP, ale każdy klient musi obsłużyć odmowę.

### Pułapki

- "Kompiluje się, więc jest podstawialne" - kompilator nie sprawdza kontraktów domenowych (a przy typowaniu strukturalnym przepuści każdy obiekt o pasującym kształcie).
- Test kontraktowy uruchamiany tylko dla jednej implementacji.
- `instanceof ReadOnlyHall` w kasie jako "naprawa".

### Pytanie do sali

Które wasze klasy nadpisują metodę bazową tylko po to, żeby rzucić wyjątek? Jaki jest kontrakt typu bazowego i gdzie jest zapisany?

## Scena s10. ISP z perspektywy klienta

**Temat ze slajdów:** 3.5 ISP: interfejs według ról klientów; 4.2 Interfejs nie usuwa sprzężenia
**Katalog:** `typescript/src/workshop/m3/s10_isp` · **Test:** `scripts/warsztat.sh --lang ts test m3/s10` (`S10EquivalenceTest.test.ts`, `S10ClientFakeTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** Gruby `CinemaAdminService` ma osiem metod, a każdy klient używa dwóch lub trzech - fake kasy musi implementować wszystkie. Wydzielamy interfejsy ról według klientów i usuwamy gruby interfejs.

**Zasada:** ISP mówi, że klient nie powinien zależeć od metod, których nie używa, więc interfejsy projektuje się według ról klientów. To nie znaczy "jedna metoda na interfejs" - rola może mieć kilka metod, jeśli jest spójna.

**Efekt:** Kasa zależy tylko od `TicketSales`, a jej fake ma dwie metody zamiast ośmiu. Sprzężenie z zapleczem nie znika - staje się zależnością od węższego, stabilniejszego kontraktu.

**Różnica względem Javy:** "zmiana sygnatury wymusza rekompilację kasy" to w TS "zmiana sygnatury dotyka kasy" - ponowne sprawdzenie typów i przebieg jej testów. Harmonogram (`TreeMap<LocalTime, String>`) to `Map<string, string>` z kluczem "HH:mm" i sortowaniem kluczy. Fake w `S10ClientFakeTest` to literał obiektu typu `CinemaAdminService` - kompilator wymaga wszystkich ośmiu metod, tak jak klasa anonimowa w Javie.

### Co widzimy

Gruby `CinemaAdminService` ma osiem metod. Kasa używa dwóch (`sellTicket`, `refundTicket`), raport dwóch, tablica seansów trzech, zmianę ceny woła tylko konfiguracja. Fake do testu kasy musi implementować wszystkie osiem.

```typescript
export interface CinemaAdminService {
  sellTicket(title: string, seat: number): string;
  refundTicket(ticketId: string): string;
  dailyRevenue(): Decimal;
  ticketsSold(title: string): number;
  scheduleScreening(title: string, start: LocalTime): void;
  cancelScreening(title: string): void;
  screenings(): string[];
  updateTicketPrice(price: Decimal): void;
}
```

### Krok 1: Extract Interface - po jednej roli na klienta

**W IDE:** ręcznie utwórz trzy interfejsy: `TicketSales`, `SalesFigures`, `ScreeningSchedule` - przenieś do nich sygnatury z `CinemaAdminService` (wytnij i wklej), a gruby interfejs niech je rozszerza (`extends TicketSales, SalesFigures, ScreeningSchedule`). W każdym kliencie zmień typ parametru konstruktora (`private readonly backOffice`) na jego rolę.
**Po:**

```typescript
export interface CinemaAdminService extends TicketSales, SalesFigures, ScreeningSchedule {
  updateTicketPrice(price: Decimal): void;
}
```

```typescript
export class CashDesk {
  constructor(private readonly backOffice: TicketSales) {}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s10` - 8 testów zielonych; `S10ClientFakeTest` porównuje fake grubego interfejsu (8 metod) z fake roli (2 metody).
**Co powiedzieć:** role nazwaliśmy z perspektywy klienta, nie implementacji. Zmiana harmonogramu nie dotyka już kasy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s10 0 1`

### Krok 2: Usunięcie grubego interfejsu

**W IDE:** w `InMemoryBackOffice` zmień `implements CinemaAdminService` na `implements TicketSales, SalesFigures, ScreeningSchedule` (`updateTicketPrice` zostaje zwykłą metodą klasy). ⇧⌥F12 na `CinemaAdminService` - gdy nie ma już użyć, usuń plik.
**Po:**

```typescript
export class InMemoryBackOffice implements TicketSales, SalesFigures, ScreeningSchedule {
```

**Uruchom:** test zielony.
**Co powiedzieć:** zmiana ceny nie dostała interfejsu - jej jedynym klientem jest konfiguracja, więc interfejs niczego by nie chronił.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s10 1 2`

### Rozwiązanie i uzasadnienie

Trzy spójne role, jedna implementacja. Interfejs nie usunął sprzężenia kasy z zapleczem - zastąpił je sprzężeniem z węższym, stabilniejszym kontraktem. W TS widać to jeszcze wyraźniej: przy typowaniu strukturalnym kasa przyjmie dowolny obiekt z dwiema metodami roli, niezależnie od tego, czy ktoś napisał `implements`.

### Pułapki

- ISP jako "jedna metoda na interfejs" - `TicketSales` ma dwie metody, bo to jedna spójna rola.
- Interfejs dla każdej klasy "bo mock" - łatwość mockowania nie dowodzi dobrego projektu.

### Pytanie do sali

Który wasz przypadek użycia dostaje gruby typ (generyczne repozytorium, cały klient ORM, cały serwis), a używa jednej metody?

## Scena s11. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 3.6 DIP i DIP to nie dependency injection; 5.1-5.2 Reguła zależności a przepływ sterowania
**Katalog:** `typescript/src/workshop/m3/s11_dip` · **Test:** `scripts/warsztat.sh --lang ts test m3/s11` (`S11EquivalenceTest.test.ts`, `S11DependencyDirectionTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Przypadek użycia `ConfirmReservation` sam tworzy klienta SMTP i zna protokół, więc import i wywołania biegną `app -> infra`. Wstrzykujemy zależność, nazywamy potrzebę polityki i wprowadzamy port `CustomerNotifier` z adapterem w `infra`.

**Zasada:** DIP mówi, że zależności źródłowe mają wskazywać od szczegółów ku polityce i abstrakcjom, a port nazywa potrzebę klienta, nie kształt technologii. DIP to nie dependency injection: wstrzyknięcie konkretnej klasy przez konstruktor wciąż wiąże politykę ze szczegółem.

**Efekt:** Sterowanie nadal płynie do `infra`, ale import odwrócił się na `infra -> app`, a politykę da się przetestować literałem obiektu. Wystarczyła ręczna funkcja `confirmReservation()` w `Main.ts` jako composition root, bez kontenera DI.

**Różnica względem Javy:** `S11DependencyDirectionTest` zamiast refleksji po typach pól czyta importy pliku `app/ConfirmReservation.ts` i sprawdza, czy któryś zaczyna się od `../infra/` (liczy się też `import type`). `isAssignableFrom` zastępuje przypisanie adaptera do typu portu (sprawdza kompilator) oraz sprawdzenie, że plik adaptera importuje `../app/CustomerNotifier.js` - to kierunek zależności źródłowej `infra -> app`. Fake portu to literał obiektu zamiast lambdy.

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s11` - 18 testów zielonych.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `import type { SmtpMailSender } from '../infra/SmtpMailSender.js'` nadal stoi w polityce. Test `step1AndStep2PolicyDependsOnInfrastructure` to potwierdza. `import type` znika po kompilacji, ale zależność źródłowa zostaje: zmiana API `SmtpMailSender` psuje politykę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s11 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**W IDE:** zaznacz składanie MIME, `send` i sprawdzenie kodu, ⌃⇧R > Extract to method in class, nazwa `notifyCustomer`; parametry nazwij `email` i `message` (F2).
**Po:**

```typescript
this.notifyCustomer(reservation.email, message);
return `potwierdzono: ${reservation.email}`;
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie wydzielamy interfejsu z `SmtpMailSender` - dostalibyśmy port `send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s11 1 2`

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
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s11 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`IllegalStateError`), więc polityka nie zna protokołu. Kontener DI nie był potrzebny - wystarczyła ręczna funkcja w `Main.ts`.

### Pułapki

- "Mamy kontener DI (InversifyJS, NestJS), więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Błąd lub typ technologii (np. błąd sterownika bazy) w sygnaturze portu.

### Pytanie do sali

Narysujcie strzałki importów i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s12. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 5.3 Elementy praktyczne; 5.4 Dane na granicy i composition root; 5.5-5.6 Kręgi to nie szablon; 7.8 Protokół efektów
**Katalog:** `typescript/src/workshop/m3/s12_cleanarchitecture` · **Test:** `scripts/warsztat.sh --lang ts test m3/s12` (`S12EquivalenceTest.test.ts`, `S12UseCaseTest.test.ts`)
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, katalogi `app` i `adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste obiekty danych, a composition root składa graf bez reguł biznesowych. Liczy się kierunek importów, a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to siedem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

**Różnica względem Javy:** kwoty są typu `Money` (a nie `Decimal`), bo trafiają do wiersza bazy i komunikatu przez `toString()` - `Money` drukuje zawsze dwa miejsca po przecinku, jak `BigDecimal` w Javie. Wiersz `Object[]` to `readonly unknown[]`, rekordy to klasy z polami `readonly`, a `CinemaApplication` to moduł z funkcją `reservationController(db, outbox)`. Łapanie `IllegalArgumentException`/`IllegalStateException` to `instanceof IllegalArgumentError`/`IllegalStateError` z ponownym rzuceniem pozostałych błędów. Porty w `S12UseCaseTest` implementują literały obiektów zamiast lambd.

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s12` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste obiekty danych, nie `Map` z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s12 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s12 1 2`

### Krok 3: Move - granica widoczna w katalogach

**W IDE:** w eksploratorze VS Code przeciągnij `BookSeats.ts`, `BookSeatsCommand.ts`, `Booking.ts`, `NewReservation.ts`, `ReservationStore.ts`, `BookingNotifier.ts` do nowego katalogu `app`, a kontroler i oba adaptery do `adapter`. Na pytanie "Update imports?" odpowiedz "Yes" - VS Code poprawi ścieżki importów (także w `CinemaApplication.ts`).
**Po:**

```text
step3/app/       BookSeats, BookSeatsCommand, Booking, NewReservation, ReservationStore, BookingNotifier
step3/adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `app` nie importuje niczego z `adapter`; adaptery importują `app`. Nazwy katalogów są drugorzędne - liczy się kierunek importów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s12 2 3`

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

**Uruchom:** test zielony; pokaż `S12UseCaseTest`: przypadek użycia z literałami obiektów zamiast adapterów, w tym `doesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s12 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako proste klasy danych. Koszt: siedem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 7.10).

### Pułapki

- Kopiowanie szablonu czterech folderów bez sprawdzenia kierunku importów.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `unknown[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez frameworka webowego (Express, NestJS) i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s13. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 4.4 Diagnostyka spójności i sprzężenia; 4.1-4.3 Spójność, sprzężenie, koszt wydzielenia; lista kontrolna "reguła sprawdzana automatycznie"
**Katalog:** `typescript/src/workshop/m3/s13_boundarycheck` · **Test:** `scripts/warsztat.sh --lang ts test m3/s13` (`S13ArchitectureTest.test.ts`, `S13EquivalenceTest.test.ts`, `S13ToolsTest.test.ts`)
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

Uruchom test z logami (`scripts/warsztat.sh --lang ts test m3/s13` - skrypt uruchamia vitest z `--reporter=verbose`, więc widać `console.log` także z zielonych testów) i pokaż wyjście: `s13 start - naruszenia granicy: [ScreeningService.ts: ../../sql/Timestamp.js, ScreeningService.ts: ../adapter/ScreeningRow.js]` i `s13 start - LCOM4 ScreeningService: Result[lcom4=2, groups=[fromRow, toRow, isMorning, price]]`. Test startu działa jak "zamrożone naruszenia" (baseline znanych naruszeń w dependency-cruiser, FreezingArchRule w ArchUnit): znane są tolerowane, każde nowe jest czerwone.

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

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s13` - 13 testów zielonych; LCOM4 obu klas = 1, ale `step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s13 0 1`

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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s13 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `sql` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S13ToolsTest` sprawdza same narzędzia na stałych próbkach kodu.

### Pułapki

- `BoundaryRule` widzi tylko instrukcje `import`/`export ... from` - dynamiczne `import()` czy `require` przejdą. Silniejsze bramki: dependency-cruiser, eslint (`no-restricted-imports`, eslint-plugin-boundaries), osobne pakiety workspace z jawnymi zależnościami w `package.json`, project references w `tsconfig`, reguły w CI. Porównajcie ich siłę.
- LCOM4 to heurystyka na źródle (pola klasy i właściwości parametrów konstruktora, składowe wcięte o 2 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po (slajd 4.3).

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Scena s14. Wzorzec jako decyzja odwracalna

**Temat ze slajdów:** 6.1-6.2 Wzorzec i refaktoryzacja w jego kierunku; 6.3 Strategy i Adapter; 6.4 Wzorzec można usunąć
**Katalog:** `typescript/src/workshop/m3/s14_reversiblepattern` · **Test:** `scripts/warsztat.sh --lang ts test m3/s14` (`S14EquivalenceTest.test.ts`, `S14FestivalVariantTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Rozliczenie z dystrybutorem ma dwa modele w jednym `switch`, w tym festiwalowy z obcego systemu w groszach. Wprowadzamy Strategy z Adapterem, a gdy umowy festiwalowe wygasają, usuwamy wariant i cały wzorzec.

**Zasada:** Wzorzec to odpowiedź na konkretne siły (tu: dwa istniejące warianty i obcy interfejs), a nie kod do skopiowania. Strategy opłaca się dla rodziny wymiennych algorytmów, Adapter dla realnego tłumaczenia obcego modelu, a refaktoryzacja od wzorca jest równie poprawna jak do niego.

**Efekt:** Zachowanie świadomie się zmienia: umowy FESTIVAL są teraz odrzucane, a model procentowy liczy tak samo na każdym etapie. Zostaje prosta metoda bez interfejsu i mapy, którą w razie potrzeby rozbudujemy tymi samymi krokami w przód.

**Różnica względem Javy:** grosze to `number` (w Javie `long`), a konwersja `BigDecimal.valueOf(cents, 2)` to `new Decimal(cents).dividedBy(100)`.

### Co widzimy

Rozliczenie z dystrybutorem ma dwa istniejące modele: procentowy (tydzień 1: 50%, 2: 40%, dalej 35%, minimalna gwarancja 500.00) i festiwalowy (stawka z obcego systemu `FestivalTariffClient`, w groszach jako liczba całkowita). Oba siedzą w jednym `switch`, konwersja jednostek jest wpleciona w logikę rozliczeń.

```typescript
case 'FESTIVAL': {
  const cents = this.festival.weeklyFeeInCents(deal.title, week);
  return new Decimal(cents).dividedBy(100);
}
```

### Krok 1: Replace Conditional with Strategy (i Adapter dla obcego klienta)

**W IDE:** utwórz interfejs `SettlementModel` z metodą `payout(deal, week, ticketRevenue)`. Każdą gałąź `switch` wydziel (⌃⇧R > Extract to method in class) i przenieś ręcznie do klasy: `PercentageModel` i `FestivalFeeAdapter` (opakowuje `FestivalTariffClient`, konwertuje grosze). `DistributorSettlement` wybiera model z mapy.
**Po:**

```typescript
private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
  ['PERCENT', new PercentageModel()],
  ['FESTIVAL', new FestivalFeeAdapter(new FestivalTariffClient())],
]);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s14` - 18 testów zielonych; `step1AdapterPaysTheFestivalFeeConvertedFromCents` sprawdza 300.00 i 150.00.
**Co powiedzieć:** uzasadnienie to dwa **istniejące** warianty z różnymi właścicielami i obcy interfejs. Adapter ma realną pracę (konwersja jednostek), nie jest pustym przekazaniem 1:1.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s14 0 1`

### Krok 2: Wariant znika - usunięcie adaptera

**W IDE:** umowy festiwalowe wygasły. Usuń wpis `'FESTIVAL'` z mapy, potem plik `FestivalFeeAdapter.ts` (⇧⌥F12 potwierdza brak użyć) i niepotrzebny import `FestivalTariffClient`.
**Po:**

```typescript
private readonly models: ReadonlyMap<string, SettlementModel> = new Map<string, SettlementModel>([
  ['PERCENT', new PercentageModel()],
]);
```

**Uruchom:** test zielony; `afterTheVariantIsGoneFestivalDealsAreRejected` - to świadoma zmiana zachowania, dlatego sprawdzana osobno, a nie testem równoważności.
**Co powiedzieć:** zostaje Strategy z jedną implementacją i mapą z jednym wpisem - klasyczny sygnał nadmiaru wzorca ze slajdu 6.4.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s14 1 2`

### Krok 3: Refaktoryzacja od wzorca

**W IDE:** ręcznie (VS Code nie ma Inline Method): wklej ciało `PercentageModel.payout` w miejsce wywołania w `DistributorSettlement.payout`, wybór z mapy zastąp warunkiem `deal.model !== 'PERCENT'`, usuń mapę oraz pliki `PercentageModel.ts` i `SettlementModel.ts`. Stałą `MINIMUM_GUARANTEE` przenieś do `DistributorSettlement` jako `private static readonly`.
**Po:**

```typescript
payout(deal: Deal, week: number, ticketRevenue: Decimal): Decimal {
  if (deal.model !== 'PERCENT') {
    throw new IllegalArgumentError(`nieznany model: ${deal.model}`);
  }
  const percent = week === 1 ? 50 : week === 2 ? 40 : 35;
```

**Uruchom:** test zielony - model procentowy rozlicza się tak samo przed wzorcem, ze wzorcem i po jego usunięciu.
**Co powiedzieć:** refaktoryzacja od wzorca jest równie poprawna jak do wzorca. Gdy wróci drugi model, przywrócimy Strategy tymi samymi krokami w przód.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s14 2 3`

### Rozwiązanie i uzasadnienie

Wzorzec był odpowiedzią na konkretne siły (dwa warianty, obcy interfejs). Gdy siły zniknęły, zniknął też wzorzec. Test równoważności chroni oba kierunki.

### Pułapki

- Zostawienie interfejsu "bo może wróci" - to YAGNI w drugą stronę (s06).
- Adapter, który tylko przekazuje wywołania 1:1 bez tłumaczenia modelu, błędów czy jednostek.
- Dopasowanie obcego klienta na siłę do kontraktu, którego obietnic nie spełnia (np. determinizm albo synchroniczność przy wywołaniu sieciowym - w TS klient sieciowy zwróciłby `Promise`).

### Pytanie do sali

Który wzorzec w waszym kodzie przeżył powód, dla którego go wprowadzono? Co by kosztowało jego usunięcie?

## Scena s15. Inwarianty w modelu domeny

**Temat ze slajdów:** 7.4 Model domeny i DRY (obiekty pilnują inwariantów); 4.3 Dobry moduł ukrywa decyzję
**Katalog:** `typescript/src/workshop/m3/s15_invariants` · **Test:** `scripts/warsztat.sh --lang ts test m3/s15` (`S15InvariantsTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Reservation` to anemiczna klasa z publicznymi, zmiennymi polami, walidację robi tylko `BookingService`, więc import pliku partnera tworzy niepoprawne rezerwacje. Zamieniamy klasę na niezmienny obiekt z polami `readonly` i przenosimy strażników do jego konstruktora.

**Zasada:** Inwariant to warunek, który obiekt spełnia przez całe życie, a jego naturalnym właścicielem jest sam model domeny. Pilnują go konstruktor i niezmienność, a nie każdy serwis z osobna; reguły wymagające danych spoza obiektu należą do przypadku użycia.

**Efekt:** Złej rezerwacji nie da się utworzyć żadną ścieżką - import stał się chroniony, choć go nie zmienialiśmy. Zachowanie importu świadomie się zmienia: błędny wiersz kończy się teraz wyjątkiem.

**Różnica względem Javy:** JavaBean z getterami i setterami to w porcie klasa z publicznymi, zmiennymi polami (`email!: string`, `seats = 0`, `total!: Decimal`) - idiomatyczny w TS model anemiczny. Krok 1 ("Remove Setting Method + record") to pola `readonly` jako właściwości parametrów konstruktora, a kompaktowy konstruktor rekordu to zwykłe ciało konstruktora. Sprawdzenie `email == null` odpada, bo typ `string` w trybie `strict` wyklucza `null`; `signum() < 0` to `lessThan(0)`.

### Co widzimy

`Reservation` to anemiczna klasa z publicznymi polami. Walidację (e-mail z `@`, co najmniej jedno miejsce, kwota nieujemna) robi tylko `BookingService`. `ReservationImport` (plik partnera `email;miejsca;kwota`) tworzy model z pominięciem walidacji - i przepuszcza `jan-kino.pl;0;-5.00`.

```typescript
const reservation = new Reservation();
reservation.email = email;
reservation.seats = Number.parseInt(seats, 10);
reservation.total = new Decimal(total);
```

### Krok 1: Remove Setting Method - niezmienny obiekt z konstruktorem

**W IDE:** ręcznie (VS Code nie ma "Convert to record"): w `Reservation` zastąp trzy pola konstruktorem `constructor(readonly email: string, readonly seats: number, readonly total: Decimal) {}`. Kompilator wskaże oba serwisy - w każdym zastąp przypisania pól jednym wywołaniem `new Reservation(email, seats, total)`.
**Po:**

```typescript
export class Reservation {
  constructor(readonly email: string, readonly seats: number, readonly total: Decimal) {}
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s15` - 17 testów zielonych; `importWithoutInvariantsCreatesInvalidReservation` pokazuje, że import wciąż tworzy złą rezerwację.
**Co powiedzieć:** obiekt jest niezmienny i powstaje w całości w jednym wywołaniu - to warunek, żeby w ogóle dało się pilnować inwariantów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s15 0 1`

### Krok 2: Move - strażnicy do konstruktora

**W IDE:** wytnij trzy `if` z `BookingService.book` i wklej do ciała konstruktora `Reservation` (dodaj pusty blok `{}` konstruktora i import `IllegalArgumentError`).
**Po:**

```typescript
export class Reservation {
  constructor(readonly email: string, readonly seats: number, readonly total: Decimal) {
    if (!email.includes('@')) {
      throw new IllegalArgumentError(`niepoprawny email: ${email}`);
    }
    // ... miejsca, kwota
  }
}
```

**Uruchom:** test zielony; ścieżka przez serwis daje te same komunikaty, a `step2ImportCannotCreateInvalidReservation` pokazuje, że import jest chroniony, choć go nie zmienialiśmy.
**Co powiedzieć:** inwariant ma jednego właściciela - model. To też DRY: reguła poprawności rezerwacji istnieje w jednym miejscu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s15 1 2`

### Rozwiązanie i uzasadnienie

Nie da się utworzyć rezerwacji w złym stanie, niezależnie od ścieżki (kasa, online, import). Serwis tylko koordynuje.

### Pułapki

- Walidacja w schemacie DTO frameworka (np. dekoratory class-validator, schemat zod na wejściu HTTP) zamiast w modelu - omija ją każda ścieżka bez frameworka.
- Inwarianty, które wymagają danych spoza obiektu (np. dostępność miejsca) - to reguła przypadku użycia, nie konstruktora.

### Pytanie do sali

Ile ścieżek tworzy wasz najważniejszy obiekt domeny? Czy wszystkie przechodzą przez tę samą walidację?

## Scena s16. Sprzężenie protokołu - ukryta kolejność wywołań

**Temat ze slajdów:** 4.4 "Czy wywołania wymagają ukrytej kolejności?" - sprzężenie protokołu lub czasu
**Katalog:** `typescript/src/workshop/m3/s16_temporalcoupling` · **Test:** `scripts/warsztat.sh --lang ts test m3/s16` (`S16EquivalenceTest.test.ts`)
**Czas:** ~6 min

### W skrócie

**Co robimy:** `TicketPrinter` wymaga wywołania trzech setterów przed `print`, czego nie widać w typach - pominięcie kończy się `TypeError`. Przenosimy dane do parametrów `print`, a potem do obiektu `TicketRequest` z walidacją.

**Zasada:** Sprzężenie czasowe (protokołu) to ukryte wymaganie kolejności wywołań, o którym klient musi wiedzieć poza typami. Naprawia się je, zamieniając stan i kolejność na jawne dane wejściowe, które kompilator i konstruktor mogą sprawdzić.

**Efekt:** Drukarka jest bezstanowa i bezpieczna do współdzielenia, a niekompletne dane są odrzucane w chwili tworzenia żądania. Klient zamiast trzech wywołań buduje jeden obiekt.

**Różnica względem Javy:** pola drukarki w `start` mają typ `T | undefined`, a `print` "odpakowuje" je operatorem `!` - zapomniane wywołanie daje `TypeError` (odpowiednik NPE). `Objects.requireNonNull` to funkcja `requireNonNull` z `shared/requireNonNull.ts` (rzuca `NullPointerError`); test przekazuje `null as unknown as string`, czyli `null` spoza systemu typów (np. z JSON-a). "Bezpieczna współbieżnie" w jednowątkowym JS znaczy: bezpieczna przy współdzieleniu jednej drukarki przez dwie kasy i przy przeplataniu wywołań asynchronicznych.

### Co widzimy

`TicketPrinter` wymaga wywołania `selectScreening`, `selectSeat` i `forBuyer` przed `print`. Nic w typach tego nie mówi: zapomniane wywołanie to `TypeError` w `print`, a drukarka współdzielona przez dwie kasy miesza dane klientów. `TicketDesk` (klient) zna ten protokół.

```typescript
this.printer.selectScreening(screening);
this.printer.selectSeat(seat);
this.printer.forBuyer(buyer);
return this.printer.print();
```

Na żywo: w `TicketDesk` zakomentuj `selectSeat` i uruchom test - `TypeError: Cannot read properties of undefined (reading 'toFixed')`. Cofnij.

### Krok 1: Change Signature - wszystko, czego potrzeba, w parametrach

**W IDE:** ręcznie (VS Code nie ma Change Signature): dodaj do `print` parametry `screening: Screening`, `seat: number`, `buyer: string` i użyj ich w ciele zamiast pól (bez `!`). Usuń pola i trzy settery; w `TicketDesk` usuń trzy wywołania i przekaż dane do `print` - kompilator wskaże każde miejsce.
**Po:**

```typescript
print(screening: Screening, seat: number, buyer: string): string {
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s16` - 7 testów zielonych.
**Co powiedzieć:** protokół "najpierw ustaw, potem drukuj" stał się kontraktem sprawdzanym przez kompilator. Drukarka jest bezstanowa, więc bezpieczna do współdzielenia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s16 0 1`

### Krok 2: Introduce Parameter Object

**W IDE:** ręcznie (VS Code nie ma Introduce Parameter Object): utwórz klasę `TicketRequest(screening, seat, buyer)` z polami `readonly`, w konstruktorze `requireNonNull` dla seansu i kupującego. `print` przyjmuje `request: TicketRequest`, `TicketDesk` buduje żądanie.
**Po:**

```typescript
export class TicketRequest {
  readonly screening: Screening;
  readonly buyer: string;

  constructor(screening: Screening, readonly seat: number, buyer: string) {
    this.screening = requireNonNull(screening, 'screening');
    this.buyer = requireNonNull(buyer, 'buyer');
  }
}
```

**Uruchom:** test zielony; `step2RejectsIncompleteRequestAtCreation` - niekompletne żądanie jest odrzucane przy tworzeniu, a nie w `print`.
**Co powiedzieć:** błąd wychodzi tam, gdzie powstał, a nie trzy wywołania dalej. W TS kompilator już pilnuje kompletu argumentów; `requireNonNull` chroni przed danymi spoza systemu typów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s16 1 2`

### Rozwiązanie i uzasadnienie

Sprzężenie czasowe zamienione na zależność od danych. To samo dotyczy protokołów efektów (s12): kolejność, która ma znaczenie, powinna być jawna i przetestowana.

### Pułapki

- Metoda `init()` wymagana przed użyciem obiektu - ten sam zapach w innej formie (w TS często z polami oznaczonymi `!`).
- Builder bez walidacji w `build()` - przenosi problem, nie usuwa go.

### Pytanie do sali

Które API w waszym systemie wymaga "najpierw X, potem Y"? Jak klient ma się o tym dowiedzieć?

## Proponowana kolejność pokazu

**Ścieżka krótka (~85 min):** s01 (DRY) -> s02 (podobieństwo) -> s06 (YAGNI) -> s07 (SRP) -> s09 (LSP) -> s11 (DIP) -> s14 (wzorzec odwracalny). Z s12 pokaż tylko `scripts/warsztat.sh --lang ts diff m3/s12 3 4` i `S12UseCaseTest` (5 min), jeśli zostanie czas.

**Ścieżka pełna (~170 min, z przerwą):**

1. DRY, KISS, YAGNI: s01, s02, s03, s04, s05, s06 (~60 min) - na koniec filtr decyzyjny ze slajdu 2.9 na przykładzie s06.
2. SOLID: s07, s08, s09, s10, s11 (~55 min).
3. Spójność, sprzężenie, granice: s15, s16, s13, s12 (~45 min).
4. Wzorce: s14 (~12 min) jako podsumowanie - wzorzec to kierunek serii refaktoryzacji, który można odwrócić.

Sceny s12 i s13 dobrze łączyć: po s12 zapytaj, jak upewnić się, że `app` nigdy nie zaimportuje `adapter`, i przejdź do s13.
