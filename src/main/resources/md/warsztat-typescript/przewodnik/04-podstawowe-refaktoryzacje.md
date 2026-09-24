# Moduł 4. Podstawowe refaktoryzacje - warsztat CineLegacy: przewodnik prowadzącego

Trzynaście krótkich scen w domenie kina pokazuje na żywo każdą technikę z modułu 4: od testu charakterystyki, przez Rename, Extract i Inline, Move Method i Move Field, Extract Class, aż po hermetyzację pól, kolekcji i warunków. Każda scena ma kod wyjściowy (`start`), gotowe snapshoty po każdym kroku (`stepN`) i test (Vitest), który po każdym ruchu w IDE ma być zielony. Tam, gdzie tematem jest pułapka semantyki, test ją dokumentuje: pokazuje, co by się zepsuło, i dlaczego bezpieczny krok wygląda inaczej.

To wersja przewodnika dla portu TypeScript. Kod scen leży w `typescript/src/workshop/m4/`, testy w `typescript/test/workshop/m4/`, a pokaz prowadzimy w VS Code. Tam, gdzie TypeScript zmienia mechanikę albo objaw pułapki, scena ma akapit **Różnica względem Javy**.

Sceny nie powtarzają studium "generator oferty wynajmu" z zadań modułu 4. Tamto studium uczestnicy robią sami, a sceny poniżej służą do pokazu.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m4            # sceny modułu i ich kroki
scripts/warsztat.sh --lang ts test m4/s05        # testy jednej sceny
scripts/warsztat.sh --lang ts test m4            # wszystkie sceny modułu (230 testów)
scripts/warsztat.sh --lang ts diff m4/s05 2 3    # co zmienia krok 3 względem kroku 2 (0 = start)
scripts/warsztat.sh --lang ts diff m4/s05 2 3 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m4/s05 2      # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh --lang ts next m4/s05        # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                         # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                         # krok wstecz
scripts/warsztat.sh status                       # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m4/s05       # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w IDE, uruchomienie testu sceny (skrypt albo przycisk Run przy teście w rozszerzeniu Vitest), zdanie z sekcji "Co powiedzieć".
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta ostatni język i ostatnią scenę, więc po pierwszym `--lang ts next m4/sNN` wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu `start`. Snapshoty `stepN` są po to, żeby pokazać `diff` albo przeskoczyć (`next`, `jump`), gdy coś się rozjedzie. Kroki importują typy sceny ścieżką `../Typ.js`, a własne klasy ścieżką `./Klasa.js`, więc skopiowanie `stepN` do `start` działa bez zmiany importów.
- Skróty VS Code (macOS): Refactor... ⌃⇧R, Rename Symbol F2, Quick Fix ⌘., Go to Definition F12, Go to References ⇧F12. VS Code ma automatyczne Rename, Extract to constant/function/method, Inline variable i Move to file, ale nie ma Inline Method, Move Method między klasami, Change Signature ani Safe Delete. Te ruchy robimy ręcznie, a kompilator (`tsc`) i test pilnują, czy nic nie zgubiliśmy.
- W scenach o pułapkach (s01, s02, s05, s06, s07) warto najpierw **zrobić naiwny ruch na żywo**, pokazać czerwony test (albo błąd kompilacji), cofnąć (⌘Z) i dopiero wtedy wykonać krok z przewodnika.
- Sceny s10 i s11 zawierają kroki, które **świadomie zmieniają zachowanie**. Test ma dla nich osobne oczekiwania. Powiedz to na głos: to osobny commit, a nie refaktoryzacja.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
|---|---|---|---|---|
| s00 | Test charakterystyki przed pierwszą zmianą; Test równoważności etapów | 2 | `m4/s00_characterization` | ~15 min |
| s01 | Rename - cel i mechanika (nazwy poza kodem) | 3 | `m4/s01_rename` | ~12 min |
| s02 | Extract Variable i moment ewaluacji | 3 | `m4/s02_extractvariable` | ~8 min |
| s03 | Extract Constant i `const`; Replace Magic Numbers | 3 | `m4/s03_magicnumbers` | ~10 min |
| s04 | Extract Method - przepływ danych, mechanika i trudne przypadki | 3 | `m4/s04_extractmethod` | ~15 min |
| s05 | Inline Variable i typ docelowy (liczba i moment ewaluacji) | 3 | `m4/s05_inlinevariable` | ~12 min |
| s06 | Inline Method i jego ryzyka | 2 | `m4/s06_inlinemethod` | ~8 min |
| s07 | Move Method - wybór właściciela, krok po kroku i ryzyka | 2 | `m4/s07_movemethod` | ~10 min |
| s08 | Move Field krok po kroku i ryzyka | 3 | `m4/s08_movefield` | ~10 min |
| s09 | Extract Class - cel, mechanika i zły wynik | 3 | `m4/s09_extractclass` | ~15 min |
| s10 | Encapsulate Field | 3 | `m4/s10_encapsulatefield` | ~10 min |
| s11 | Encapsulate Collection; Trzy różne kontrakty kolekcji | 3 | `m4/s11_encapsulatecollection` | ~12 min |
| s12 | Encapsulate Conditional | 3 | `m4/s12_encapsulateconditional` | ~8 min |

## Scena s00. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** Test charakterystyki przed pierwszą zmianą; Test równoważności etapów; Co ma pozostać niezmienione
**Katalog:** `typescript/src/workshop/m4/s00_characterization` · **Test:** `scripts/warsztat.sh --lang ts test m4/s00` (`typescript/test/workshop/m4/s00_characterization/S00CharacterizationTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingConfirmation.confirm` drukuje potwierdzenie bez żadnego testu, a w wydruku jest bieżący czas i kwoty zależne od domyślnego locale. Najpierw zapisujemy w teście pełny dokument, potem robimy szew na zegar i dopiero wtedy pierwszą ekstrakcję.

**Zasada:** Test charakterystyki zapisuje, co kod faktycznie robi, a nie co powinien robić - razem z dziwnymi regułami. Stosujemy go przed pierwszą zmianą kodu bez testów, a ten sam zestaw oczekiwań uruchamiamy na każdym etapie jako test równoważności. Nie mylić z testem, który liczy oczekiwania tym samym algorytmem co kod.

**Efekt:** Mamy deterministyczny test całego dokumentu, wstrzykiwany `Clock` i wydzieloną metodę `ticketPrice`, a wywołanie `new BookingConfirmation()` bez argumentu dalej działa. Znaleziska (rabat od 11 biletów, kwoty zależne od locale) zostają zapisane, ale niepoprawione, bo to osobne decyzje.

**Różnica względem Javy:** JavaScript nie ma `Locale.setDefault`. Odpowiednikiem `String.format("%.2f", x)` bez `Locale` jest `x.toLocaleString(undefined, AMOUNT)`, którego wynik zależy od domyślnego locale środowiska. Pomocnik testu `inLocale` podmienia na czas wywołania domyślny argument `Number.prototype.toLocaleString` i przywraca oryginał w `finally`. Parameterize Constructor to jeden konstruktor z parametrem domyślnym (`clock: Clock = systemClock`) zamiast dwóch konstruktorów, bo TypeScript nie ma przeciążeń konstruktorów, a dla klientów efekt jest ten sam.

### Co widzimy

`BookingConfirmation.confirm` drukuje potwierdzenie rezerwacji. Nie ma żadnego testu, a dokument czytają klienci i infolinia. Zanim cokolwiek zmienimy, zapisujemy, co kod **robi**. Na drodze stoją dwie rzeczy: bieżący czas w ostatniej linii i `toLocaleString(undefined, ...)`, czyli formatowanie zależne od domyślnego locale.

```typescript
if (b.ticketTypes.length > 10) {
  sum = sum * 0.9;
}
...
  + 'Do zaplaty: ' + (sum + fee).toLocaleString(undefined, AMOUNT) + '\n'
  + 'Wygenerowano: ' + LocalDateTime.now().toString() + '\n';
```

W tej scenie najważniejszy jest test `S00CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `start/step1/step2` tylko za nim nadąża.

**Ruch A (na `start`, przed krokiem 1):** napisz `expect(new BookingConfirmation().confirm(booking)).toBe('')`, uruchom i skopiuj rzeczywisty wynik z diffu Vitest do oczekiwań. Test dalej jest czerwony, bo zmienia się sekunda. Dodaj "scrubber", który maskuje linię z czasem (`document.replace(/Wygenerowano: .*\n/g, 'Wygenerowano: <czas>\n')`), i ustaw locale jawnie na czas wywołania (`inLocale('pl-PL', () => ...)`). Teraz test jest zielony i deterministyczny. Dopisz przypadki na obu bokach progów: 10 i 11 biletów, rano i wieczorem, kasa i online.

```typescript
function inLocale(locale: string, action: () => string): string {
  const original = Number.prototype.toLocaleString;
  Number.prototype.toLocaleString = function (this: number, locales?: Intl.LocalesArgument,
    options?: Intl.NumberFormatOptions): string {
    return original.call(this, locales ?? locale, options);
  };
  try {
    return action();
  } finally {
    Number.prototype.toLocaleString = original;
  }
}
```

W trakcie wychodzą dwa znaleziska, które test zapisuje, a nie poprawia:

- rabat grupowy działa od **11** biletów (`> 10`), choć regulamin mówi "10+" - przypadek nazwany `ZASTANE: ...`,
- kwoty zależą od locale serwera: na `pl-PL` jest `74,00`, na `en-US` `74.00` (test `foundDuringCharacterizationAmountsDependOnServerLocale`).

### Krok 1: Parameterize Constructor - wstrzyknięty Clock

**W IDE:** VS Code nie ma Extract Parameter ani Introduce Field dla konstruktora, więc ruch robimy ręcznie w dwóch małych krokach. Najpierw dodaj konstruktor z parametrem domyślnym: `constructor(private readonly clock: Clock = systemClock) {}` (import `Clock` i `systemClock` z `shared/time.js` podpowie Quick Fix ⌘.). Uruchom test - nic się nie zmieniło, bo nikt jeszcze nie czyta pola. Potem w `confirm` zamień `LocalDateTime.now()` na `LocalDateTime.now(this.clock)`.
**Po:**

```typescript
export class BookingConfirmation {
  constructor(private readonly clock: Clock = systemClock) {}
  ...
      + 'Wygenerowano: ' + LocalDateTime.now(this.clock).toString() + '\n';
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s00` - 15 testów zielonych, w tym `fromStep1TheWholeDocumentIsDeterministic` (z `fixedClock(...)` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Wywołanie bez argumentu zostaje (parametr domyślny to zegar systemowy), więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `start`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s00 0 1`

### Krok 2: pierwsza refaktoryzacja pod ochroną testu

**W IDE:** zaznacz ciało pętli od `let p: number;` do końca `if` z porankiem (`p = p - 5.00; }`), ⌃⇧R, "Extract to method in class 'BookingConfirmation'", nazwa `ticketPrice`. VS Code wykryje dwa wejścia (`b`, `t`) i jedno wyjście (`p`), więc w pętli zostanie zmienna z wynikiem metody. Kursor na tej zmiennej, ⌃⇧R, "Inline variable". W pętli zostaje `sum = sum + this.ticketPrice(b, t);`.
**Po:**

```typescript
for (const t of b.ticketTypes) {
  sum = sum + this.ticketPrice(b, t);
}
```

**Uruchom:** test zielony - ten sam dokument, znak w znak.
**Co powiedzieć:** kusi, żeby przy okazji zamienić `number` na `Decimal`, podać jawne locale i poprawić próg grupy. Każda z tych rzeczy zmienia wydruk, więc to trzy osobne decyzje z wymaganiem, a nie refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s00 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `start`, `step1` i `step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, locale albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj locale ustawiamy na czas wywołania i przywracamy w `finally`. Podmiana `Number.prototype.toLocaleString` bez `finally` rozlałaby się na inne testy w tym samym procesie.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s01. Rename - nazwy, które żyją poza kodem

**Temat ze slajdów:** Rename - cel i mechanika; Co ma pozostać niezmienione (wywołanie po nazwie, konfiguracja)
**Katalog:** `typescript/src/workshop/m4/s01_rename` · **Test:** `scripts/warsztat.sh --lang ts test m4/s01` (`S01EquivalenceTest.test.ts`, `S01RenameLimitsTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `SalesReport` nazwy `calc2`, `s`, `m`, `x` nic nie mówią, ale nazwa metody żyje w konfiguracji, a nazwy pól klasy `Line` trafiają przez `Object.keys` do nagłówka CSV. Zmieniamy nazwy od najbezpieczniejszych lokalnych do tych, które są kontraktem zewnętrznym.

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, wywołaniu po nazwie (`obj[name]`), JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić operacji F2 z bezpieczną zmianą kontraktu.

**Efekt:** Kod czyta się po nazwach ról, a oba kontrakty zostają nietknięte: stara nazwa `calc2` jako przestarzały delegat, nagłówek CSV jako jawna stała. Koszt to delegat, którego nie wolno usunąć, dopóki ktoś nie zmieni konfiguracji na serwerach.

**Różnica względem Javy:** refleksja Javy ma w TypeScript dwa odpowiedniki. `ReportJob` wywołuje metodę po nazwie z tekstu konfiguracji (`report[name].call(...)`, brak metody kończy się `IllegalStateError`). Nagłówek CSV liczy `Object.keys(...)` wiersza, a sam wiersz `Object.values(...)` - to odpowiednik `getRecordComponents()`. `Line` jest eksportowaną klasą z polami `readonly` (zamiast rekordu), a kwoty to `Money`, który w wydruku zachowuje dwa miejsca po przecinku. Adnotację `@Deprecated` zastępuje tag JSDoc `@deprecated`.

### Co widzimy

`SalesReport.calc2(s: readonly Sale[], flag: boolean)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(t: string, n: number, d: Money)`. Dwie nazwy żyją poza kodem:

```typescript
// ReportJob - w produkcji plik report.properties na serwerze
static readonly CONFIG = `
report.method=calc2
report.onlineOnly=true
`;
...
// Wywołanie po nazwie z tekstu - odpowiednik refleksji (getMethod + invoke).
const method: unknown = (this.report as unknown as Record<string, unknown>)[methodName];

// SalesReport - nagłówek CSV liczony z nazw pól wiersza
function header(): string {
  return Object.keys(new Line('', 0, Money.ZERO)).join(';');
}
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**W IDE:** kursor na `s`, F2, `sales`. Tak samo `flag` -> `onlineOnly`, `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` / `line`, `b` -> `csv`, `v` -> `values`, `c` -> `component`.
**Po:**

```typescript
calc2(sales: readonly Sale[], onlineOnly: boolean): string {
  const linesByTitle = new Map<string, Line>();
  for (const sale of sales) {
    if (onlineOnly && !sale.online) {
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s01` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s01 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**W IDE:** najpierw na żywo naiwnie: F2 na `calc2`, nazwa `revenueCsv`. VS Code poprawi wszystkie wywołania w kodzie, ale nie tekst `report.method=calc2`. Uruchom test: `everyStepRunsTheConfiguredJob` jest czerwony (`IllegalStateError: Zadanie raportu nie działa: brak metody calc2`). IDE nie wie, że tekst w konfiguracji to nazwa metody. Cofnij (⌘Z). Teraz poprawnie: skopiuj metodę, zmień nazwę kopii na `revenueCsv`, a ciało `calc2` zamień na delegowanie i oznacz w JSDoc `@deprecated`.
**Po:**

```typescript
/**
 * Stara nazwa z konfiguracji report.properties (report.method=calc2).
 * Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
 *
 * @deprecated użyj {@link revenueCsv}
 */
calc2(sales: readonly Sale[], onlineOnly: boolean): string {
  return this.revenueCsv(sales, onlineOnly);
}

revenueCsv(sales: readonly Sale[], onlineOnly: boolean): string {
```

**Uruchom:** test zielony, zadanie z konfiguracji działa. `S01RenameLimitsTest.configurationStillNamesTheOldMethod` sprawdza, że na prototypie są obie metody.
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu. VS Code przekreśli wywołania `calc2` w kodzie, więc nowi klienci od razu widzą, której nazwy używać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s01 1 2`

### Krok 3: Rename pól wiersza po odcięciu kontraktu CSV

**W IDE:** najpierw naiwnie: F2 na `t` w `Line`, `title`. Test pokaże nagłówek `title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij. Teraz poprawnie: najpierw zamień `header()` i `row()` oparte na `Object.keys`/`Object.values` na jawną stałą `CSV_HEADER = 't;n;d'` i jawne składanie wiersza (test zielony), dopiero potem F2 na `t`, `n`, `d` -> `title`, `tickets`, `revenue`.
**Po:**

```typescript
/** Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy pól w kodzie. */
export const CSV_HEADER = 't;n;d';

export class Line {
  constructor(readonly title: string, readonly tickets: number, readonly revenue: Money) {}
}

function row(line: Line): string {
  return line.title + ';' + line.tickets + ';' + line.revenue.toString();
}
```

**Uruchom:** test zielony, także `S01RenameLimitsTest.reflectionTurnsJavaNamesIntoTheCsvHeader`, który pokazuje, że `Object.keys` na nowej klasie dałoby `title;tickets;revenue`.
**Co powiedzieć:** nazwa w kodzie i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone przez `Object.keys`, każdy Rename jest zmianą API. To samo dotyczy `JSON.stringify` - nazwy pól obiektu stają się kluczami w JSON.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: wszystkie nazwy mówią o roli (`revenueCsv`, `onlineOnly`, `linesByTitle`, `Line.revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z kodu i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na tym, że Rename w VS Code "znajdzie wszystko". Rename Symbol działa na typach i nie zmienia tekstu w stringach, a globalne szukaj-zamień (⇧⌘H) nie widzi plików poza projektem ani nazw składanych dynamicznie (`'calc' + version`).
- Inne miejsca, w których nazwa jest kontraktem: `JSON.stringify` / `JSON.parse` (klucze obiektu), kolumny ORM, szablony, nazwy pól w odpowiedziach REST, dostęp `obj[name]`, logi parsowane przez monitoring.
- Usunięcie delegatu `calc2` "bo nikt go nie woła". Go to References (⇧F12) go nie znajdzie, bo woła go tylko konfiguracja przez `report[name]`.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s02. Extract Variable dla złożonego wyrażenia ceny

**Temat ze slajdów:** Extract Variable i moment ewaluacji
**Katalog:** `typescript/src/workshop/m4/s02_extractvariable` · **Test:** `scripts/warsztat.sh --lang ts test m4/s02` (`S02EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** Cena biletu w `TicketPrice` to jedno długie wyrażenie z pięcioma ternary, którego nie da się przeczytać bez liczenia w głowie. Rozbijamy je na zmienne z nazwami z cennika: najpierw kwoty bazowe, potem warunki, na końcu dopłaty.

**Zasada:** Extract Variable nazywa znaczenie fragmentu wyrażenia, a nie jego składnię. Może jednak zmienić moment ewaluacji, więc trzeba uważać na wyrażenia z efektem, odczytem czasu albo wyjątkiem. Krótkie spięcie `&&` jest zachowaniem - nie wolno wydzielić prawej strony przed osłoną `!== null`.

**Efekt:** Ostatnia linia czyta się jak paragon, a kolejność działań i miejsce zaokrąglenia są takie same jak w `start`. Liczby nadal są literałami, co zostawiamy na następną scenę.

**Różnica względem Javy:** w Javie wydzielenie `r.row() >= 10` przed osłoną kończy się `NullPointerException` z unboxingu `Integer`. W TypeScript `row: number | null`, więc takie wydzielenie odrzuca już kompilator (TS18047: 'row' is possibly 'null'). Kto obejdzie kompilator operatorem `!`, nie dostanie wyjątku, tylko cichą zmianę zachowania: JavaScript zamienia `null` na 0 w porównaniu (`null >= 0` to `true`). Kwoty to `Decimal` (`decimal.js`), a zaokrąglenie to `.dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)`.

### Co widzimy

Cała cena biletu to jedno wyrażenie z pięcioma ternary. Żeby odpowiedzieć na pytanie "skąd 32.00?", trzeba policzyć je w głowie. Uwaga na `row`: to `number | null`, a `null` oznacza wolną widownię.

```typescript
return (r.format === 3 ? new Decimal('40.00')
  : r.format === 2 ? new Decimal('32.00') : new Decimal('25.00'))
  .times(100 - (r.type === 'S' ? 25
    : r.type === 'E' ? 30 : r.type === 'C' ? 40 : 0))
  .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP)
  .minus(r.start.isBefore(LocalTime.NOON)
    ? new Decimal('5.00') : new Decimal(0))
  .plus(r.row !== null && r.row >= 10 ? new Decimal('10.00') : new Decimal(0))
  .plus(r.format === 2 && !r.ownGlasses ? new Decimal('3.00') : new Decimal(0));
```

### Krok 1: Extract Variable dla ceny bazowej i zniżki

**W IDE:** zaznacz pierwszy nawias z ternary formatu (bez nawiasów), ⌃⇧R, "Extract to constant in enclosing scope", `basePrice`. Potem zaznacz ternary typu biletu, ⌃⇧R, `discountPercent`. Na koniec zaznacz `basePrice.times(...).dividedBy(...).toDecimalPlaces(...)`, ⌃⇧R, `discountedPrice`. VS Code nadaje nazwę `newLocal` i od razu uruchamia Rename, więc wystarczy wpisać nową nazwę.
**Po:**

```typescript
const basePrice = r.format === 3 ? new Decimal('40.00')
  : r.format === 2 ? new Decimal('32.00') : new Decimal('25.00');
const discountPercent = r.type === 'S' ? 25
  : r.type === 'E' ? 30 : r.type === 'C' ? 40 : 0;
const discountedPrice = basePrice
  .times(100 - discountPercent)
  .dividedBy(100).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s02` - 25 testów zielonych.
**Co powiedzieć:** zmienna nazywa pojęcie z cennika, a nie fragment składni. Wyrażenia są czyste, więc przesunięcie ich ewaluacji wcześniej niczego nie zmienia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s02 0 1`

### Krok 2: Extract Variable dla warunków

**W IDE:** zaznacz `r.start.isBefore(LocalTime.NOON)`, ⌃⇧R, `morning`. Dla VIP zaznacz **całe** `r.row !== null && r.row >= 10`, ⌃⇧R, `vipSeat`. Potem `r.format === 2 && !r.ownGlasses` -> `needsGlasses`.
**Po:**

```typescript
const morning = r.start.isBefore(LocalTime.NOON);
const vipSeat = r.row !== null && r.row >= 10;
const needsGlasses = r.format === 2 && !r.ownGlasses;
```

**Uruchom:** test zielony, także przypadki "wolna widownia (row = null)".
**Co powiedzieć:** gdybyśmy wydzielili samo `r.row >= 10`, zmienna policzyłaby się **przed** osłoną `!== null`. TypeScript od razu to zgłasza (TS18047), a "naprawienie" tego przez `r.row! >= 10` wyłącza sprawdzenie i daje kod, który dla wolnej widowni porównuje `null` jak 0. Krótkie spięcie jest zachowaniem. Pokazuje to test `extractingTheComparisonWithoutTheNullGuardThrows`: utrwala błąd kompilacji przez `// @ts-expect-error` i sprawdza, że `null >= 0` daje `true`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s02 1 2`

### Krok 3: Extract Variable dla kwot

**W IDE:** zaznacz każde `warunek ? kwota : new Decimal(0)`, ⌃⇧R: `morningReduction`, `vipSurcharge`, `glassesFee`.
**Po:**

```typescript
const morningReduction = morning ? new Decimal('5.00') : new Decimal(0);
const vipSurcharge = vipSeat ? new Decimal('10.00') : new Decimal(0);
const glassesFee = needsGlasses ? new Decimal('3.00') : new Decimal(0);
return discountedPrice.minus(morningReduction).plus(vipSurcharge).plus(glassesFee);
```

**Uruchom:** test zielony.
**Co powiedzieć:** ostatnia linia czyta się jak paragon. Następny naturalny krok to Replace Magic Numbers (scena s03) albo Extract Method dla `basePrice` (scena s04).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s02 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketPrice.ts`: każda część ceny ma nazwę, kolejność operacji arytmetycznych i miejsce zaokrąglenia są takie same jak w `start`. Test obejmuje obie strony progów (11:59 i 12:00, rząd 9 i 10) oraz `row = null`.

### Pułapki

- Wydzielenie jednej strony `&&` / `||` przed osłoną (`null`, `instanceof`, `typeof`, `length > 0`). W TypeScript zawężanie typów często to wyłapie, ale tylko przy `strictNullChecks` i bez `!`.
- Scalenie dwóch identycznych wyrażeń w jedną zmienną, gdy wyrażenie ma efekt uboczny albo czyta czas. Wtedy zmienia się **liczba** ewaluacji (patrz s05).
- Nazwa opisująca składnię (`ternary1`, `tmp`, domyślne `newLocal` z VS Code) zamiast znaczenia.

### Pytanie do sali

Kiedy jedna zmienna `now = clock.now()` zamiast dwóch wywołań zegara jest poprawką, a kiedy zmianą kontraktu?

## Scena s03. Stałe z nazwą zamiast magicznych liczb

**Temat ze slajdów:** Extract Constant i `const`; Replace Magic Numbers with Named Constants
**Katalog:** `typescript/src/workshop/m4/s03_magicnumbers` · **Test:** `scripts/warsztat.sh --lang ts test m4/s03` (`S03EquivalenceTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `OrderPricer.summary` jest pełen liczb bez nazw, a cztery dziesiątki oznaczają cztery różne decyzje biznesowe. Zamieniamy każdą liczbę na nieeksportowaną stałą modułu z nazwą roli, osobno dla każdej reguły.

**Zasada:** Replace Magic Numbers usuwa ukrytą decyzję, a Extract Constant to mechanika tej zmiany. Nazwa opisuje rolę (`GROUP_MIN_TICKETS`), nie wartość (`TEN`), a dwa identyczne literały nie zawsze są tą samą wiedzą. Stała nie jest miejscem na konfigurację zmienianą bez wdrożenia, a `const` blokuje tylko przypisanie, nie zawartość.

**Efekt:** W metodzie nie ma żadnej liczby bez nazwy, a zmiana progu grupy nie przesunie już rzędu VIP. Czternaście stałych pokazuje przy okazji, że cennik to osobna odpowiedzialność, którą jeszcze trzeba będzie przenieść.

**Różnica względem Javy:** `private static final BigDecimal` to w porcie nieeksportowane stałe modułu (`const` na poziomie pliku) z `Decimal`. Uwaga z Javy o wklejaniu publicznych `static final int` do klas klienta nie ma odpowiednika: moduły ES importują stałe jako wiązania, więc klient zawsze czyta aktualną wartość. Zamiast `List.of` niezmienną tablicę daje `Object.freeze([...])`, a zapis do niej rzuca `TypeError`. Kwoty w wydruku to `toFixed(2)`, bo `decimal.js` nie przechowuje skali.

### Co widzimy

`OrderPricer.summary` liczy bilety, opłatę online i punkty lojalnościowe. Liczby: 25.00, 32.00, 40.00, 0.25, 0.30, 0.40, 5.00, 12, 10, 10.00, 10, 0.90, 2.00 i dzielnik 10 przy punktach. Cztery "dziesiątki" oznaczają cztery różne decyzje biznesowe.

```typescript
if (t.row >= 10) {                                    // od którego rzędu VIP
  p = p.plus(new Decimal('10.00'));                   // dopłata VIP
}
...
if (o.tickets.length >= 10) {                         // próg grupy
  tickets = tickets.times(new Decimal('0.90'));
}
const points = tickets.dividedBy(10).toDecimalPlaces(0, Decimal.ROUND_DOWN).toNumber(); // zł za punkt
```

### Krok 1: Extract Constant dla kwot z cennika

**W IDE:** zaznacz `new Decimal('25.00')`, ⌃⇧R, "Extract to constant in module scope", `BASE_PRICE_2D`. VS Code zastępuje tylko zaznaczone wystąpienie i nie pyta o pozostałe, więc inne wystąpienia tej samej kwoty zamieniasz na stałą ręcznie - i tylko wtedy, gdy to ta sama wiedza. Tak samo `BASE_PRICE_3D`, `BASE_PRICE_IMAX`, `MORNING_REDUCTION`, `VIP_SURCHARGE`, `ONLINE_FEE_PER_TICKET`, `NO_FEE`.
**Po:**

```typescript
const BASE_PRICE_2D = new Decimal('25.00');
const BASE_PRICE_3D = new Decimal('32.00');
const BASE_PRICE_IMAX = new Decimal('40.00');
const MORNING_REDUCTION = new Decimal('5.00');
const VIP_SURCHARGE = new Decimal('10.00');
const ONLINE_FEE_PER_TICKET = new Decimal('2.00');
const NO_FEE = new Decimal('0.00');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s03` - 17 testów zielonych.
**Co powiedzieć:** `Decimal` jest niezmienny (każda operacja zwraca nowy obiekt), więc `const` rzeczywiście jest stałą. Stała nie jest eksportowana, bo ma jednego właściciela.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s03 0 1`

### Krok 2: Replace Magic Numbers - progi reguł

**W IDE:** zaznacz `10` w `t.row >= 10`, ⌃⇧R, "Extract to constant in module scope", `VIP_FROM_ROW`. **Nie zamieniaj** pozostałych dziesiątek na tę stałą, bo to inna wiedza. Osobno: `GROUP_MIN_TICKETS`, `GROUP_PRICE_FACTOR`, `AMOUNT_PER_LOYALTY_POINT` (z dzielnika 10), `MORNING_ENDS_AT_HOUR`.
**Po:**

```typescript
const MORNING_ENDS_AT_HOUR = 12;
const VIP_FROM_ROW = 10;
const GROUP_MIN_TICKETS = 10;
const GROUP_PRICE_FACTOR = new Decimal('0.90');
const AMOUNT_PER_LOYALTY_POINT = new Decimal(10);
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeśli dyrekcja zmieni próg grupy na 8, rząd VIP nie może się przesunąć razem z nim. Nazwa opisuje rolę (`GROUP_MIN_TICKETS`), a nie wartość (`TEN`). Extract Constant to mechanika, a Replace Magic Number to powód.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s03 1 2`

### Krok 3: Extract Constant dla stawek zniżek

**W IDE:** zaznacz `new Decimal('0.25')`, ⌃⇧R, "Extract to constant in module scope", `STUDENT_DISCOUNT`. Tak samo `SENIOR_DISCOUNT` i `CHILD_DISCOUNT`.
**Po:**

```typescript
if (t.type === 'S') {
  p = p.times(new Decimal(1).minus(STUDENT_DISCOUNT));
} else if (t.type === 'E') {
  p = p.times(new Decimal(1).minus(SENIOR_DISCOUNT));
```

**Uruchom:** test zielony.
**Co powiedzieć:** w metodzie nie ma już żadnej liczby bez nazwy. Stałe nie są eksportowane, bo to szczegół tego modułu, a nie API dla klientów. Następny ruch (już nie w tej scenie) to przeniesienie cennika do własnej klasy albo modułu, bo stałe pokazują, że to osobna odpowiedzialność.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/OrderPricer.ts`: czternaście nieeksportowanych stałych modułu, każda z nazwą roli. Trzy progi o wartości 10 mają trzy różne nazwy, bo zmieniają się z różnych powodów. Test obejmuje 9 i 10 biletów, 12:00 i rano, kasę i online.

### Pułapki

- `const` blokuje tylko przypisanie. `const discountedTypes = ['S', 'E', 'C']` każdy może zmodyfikować przez `push`. Test `finalDoesNotMakeACollectionConstant` pokazuje to na liście typów zniżkowych, a tablica z `Object.freeze([...])` przy `push` rzuca `TypeError`. Typ `readonly string[]` blokuje zmiany tylko w kompilacji, a `Object.freeze` jest płytkie.
- Eksportowana stała to kontrakt modułu. Klienci importują wiązanie, więc widzą nową wartość po przebudowie, ale każdy, kto polegał na starej wartości (np. w teście albo w innym serwisie z kopią liczby), dowie się o zmianie dopiero w produkcji.
- Stała dla czegoś, co jest konfiguracją (ceny zmieniane bez wdrożenia). Wtedy miejsce jest w konfiguracji albo w bazie, a nie w kodzie.
- Jedna stała `TEN` dla czterech reguł.

### Pytanie do sali

Które z tych stałych w prawdziwym kinie powinny być konfiguracją, a nie kodem? Po czym to poznać?

## Scena s04. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** Extract Method - cel i sygnały; Extract Method - analiza przepływu danych; Extract Method - mechanika i trudne przypadki
**Katalog:** `typescript/src/workshop/m4/s04_extractmethod` · **Test:** `scripts/warsztat.sh --lang ts test m4/s04` (`S04EquivalenceTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `TicketSummary.describe` liczy cenę, sumę i liczbę miejsc VIP, a potem składa dokument, i to wszystko w jednej metodzie z komentarzami zamiast nazw. Wydzielamy bloki do metod, a pętlę z dwoma wyjściami najpierw rozdzielamy przez Split Loop.

**Zasada:** Extract Method przenosi spójny fragment do metody, gdy nazwa wyrazi intencję lepiej niż szczegóły, a sama długość nie jest powodem. Kluczowa jest analiza przepływu danych: co wchodzi jako parametr, co wychodzi jako wynik. Gdy fragment ma kilka wyjść, dzielimy go albo tworzymy obiekt wyniku z nazwą, a nie zwracamy tablicy czy anonimowego "worka" na dwie wartości.

**Efekt:** Metoda publiczna czyta się jak spis treści, a obliczenia są oddzielone od renderowania, bez zmiany sygnatury ani formatu. Kosztem są dwa przejścia po liście rzędów, pomijalne wobec czytelności.

**Różnica względem Javy:** IntelliJ odmawia wydzielenia fragmentu z dwiema wartościami wyjściowymi. VS Code nie odmawia: wydzielona metoda zwraca wtedy obiekt `{ subtotal, vipSeats }`, a w miejscu wywołania pojawia się destrukturyzacja. Kod się kompiluje i test jest zielony, ale to dokładnie "holder" z pułapek - dwie odpowiedzialności w jednej metodzie. Kwoty to `Decimal`, w wydruku `toFixed(2)`.

### Co widzimy

`TicketSummary.describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa formatu`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```typescript
let subtotal = new Decimal(0);
let vipSeats = 0;
for (const row of order.rows) {
  let price = base;
  if (row >= 10) {
    price = price.plus(new Decimal('10.00'));
    vipSeats++;
  }
  subtotal = subtotal.plus(price);
}
subtotal = subtotal.toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**W IDE:** zaznacz blok pod `// cena bazowa formatu` (od `let base: Decimal;` do końca `if` z porankiem), ⌃⇧R, "Extract to method in class 'TicketSummary'", nazwa `basePrice`. VS Code sam wykryje jedno wejście (`order`) i jedno wyjście (`base`). Usuń zbędny komentarz.
**Po:**

```typescript
const base = this.basePrice(order);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s04` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s04 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**W IDE:** zaznacz pętlę razem z deklaracjami `subtotal` i `vipSeats` i otwórz ⌃⇧R. Pokaż podgląd "Extract to method": VS Code zaproponuje metodę zwracającą `{ subtotal, vipSeats }`. To jest moment dydaktyczny - IDE nie widzi, że to dwie odpowiedzialności. Zamknij bez zmian (Esc). Ręcznie skopiuj pętlę (Split Loop) i z pierwszej kopii usuń `vipSeats`, a z drugiej `subtotal`. Uruchom test. Następnie na każdej pętli ⌃⇧R, "Extract to method", nazwy `subtotal` i `vipSeats`.
**Po:**

```typescript
const subtotal = this.subtotal(order, base);
const vipSeats = this.vipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (obiekt z dwoma polami) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy, a nie zgadujemy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s04 1 2`

### Krok 3: Extract Method dla renderowania

**W IDE:** zaznacz blok `// dokument` i wykonaj ⌃⇧R, "Extract to method", nazwa `render`. Parametry: `order`, `subtotal`, `vipSeats`. Potem kursor na `base`, ⌃⇧R, "Inline variable".
**Po:**

```typescript
describe(order: Order): string {
  const subtotal = this.subtotal(order, this.basePrice(order));
  const vipSeats = this.vipSeats(order);
  return this.render(order, subtotal, vipSeats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class (scena s09).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s04 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketSummary.ts`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy (`[subtotal, vipSeats]`) albo anonimowego obiektu tylko po to, żeby wyciągnąć dwa wyniki. VS Code zrobi to sam, jeśli mu pozwolimy.
- Ekstrakcja fragmentu z `this` do funkcji w zasięgu modułu ("Extract to function in module scope") zamiast do metody klasy. VS Code zaproponuje oba warianty, a wybór zmienia miejsce i dostęp do pól.
- Nazwa metody opisująca "jak" (`loopRows`) zamiast "co" (`vipSeats`), albo domyślne `newMethod` z VS Code.

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić obiekt z dwoma polami (i jak go wtedy nazwać)?

## Scena s05. Inline Variable - liczba i moment ewaluacji

**Temat ze slajdów:** Inline Variable i typ docelowy; Extract Variable i moment ewaluacji
**Katalog:** `typescript/src/workshop/m4/s05_inlinevariable` · **Test:** `scripts/warsztat.sh --lang ts test m4/s05` (`S05EquivalenceTest.test.ts`, `S05InlineTrapTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketIssuer.issue` ma sześć zmiennych lokalnych, które wyglądają na zbędne, ale trzy z nich chronią pojedynczy efekt uboczny, pojedynczy odczyt zegara i jawną konwersję jednostki przed wywołaniem przeciążonej funkcji. Wklejamy tylko bezpieczne zmienne, a przy `price` najpierw usuwamy niejednoznaczność nazwą przeciążenia.

**Zasada:** Inline Variable usuwa zmienną, której nazwa nic nie wnosi, ale zmienia liczbę i moment ewaluacji inicjalizatora. Wyrażenie z efektem ubocznym albo odczytem czasu po wklejeniu wykona się tyle razy, ile jest użyć. Jawny typ zmiennej i konwersja w inicjalizatorze decydują, które przeciążenie zostanie wybrane - to nie jest ozdoba.

**Efekt:** Znikają `label`, `holdUntil` i `price`, a `number`, `issuedAt` i `code` zostają celowo i warto je opisać. Wynik jest identyczny, co potwierdza test z tykającym zegarem.

**Różnica względem Javy:** w Javie `double price = basePrice(format)` niejawnie poszerza `int` do `double`, a wklejenie `money(basePrice(format))` wybiera przeciążenie `money(int grosze)`. TypeScript nie ma niejawnych konwersji liczbowych, więc port odwzorowuje to jawnie: `basePrice` zwraca `bigint` (jak `int` ze starego cennika), `money` ma dwie sygnatury przeciążenia (`number` = złote, `bigint` = grosze) i jedną implementację z `typeof`, a zmienna `const price: number = Number(basePrice(format))` niesie konwersję. Zegar to `Clock` z `shared/time.ts`, a czas trzymania rezerwacji to `HOLD_MINUTES` i `plusMinutes`.

### Co widzimy

`TicketIssuer.issue` ma sześć zmiennych lokalnych. Wyglądają na zbędne, ale trzy z nich trzymają wynik, którego **nie wolno** obliczyć ponownie:

```typescript
const number = this.nextNumber();                  // efekt uboczny: każde wywołanie zużywa numer
const issuedAt: LocalDateTime = this.clock.now();  // odczyt czasu: każde wywołanie to inna chwila
const code = screeningCode + '-' + number;
const price: number = Number(basePrice(format));   // bigint -> number: wybiera przeciążenie money
const label = 'Bilet ' + code + ', cena ' + money(price)
  + ', oplata ' + money(ONLINE_FEE_GROSZE);
const holdUntil = issuedAt.plusMinutes(HOLD_MINUTES);
return new Ticket(code, label, issuedAt, holdUntil);
```

`money(zloty: number)` i `money(grosze: bigint)` to dwie sygnatury przeciążenia o tej samej nazwie i różnych jednostkach:

```typescript
/** Kwota w złotych. */
function money(zloty: number): string;
/** Kwota w groszach - ta sama nazwa, inna jednostka. */
function money(grosze: bigint): string;
function money(amount: number | bigint): string {
  if (typeof amount === 'bigint') {
    return `${amount / 100n}.${String(amount % 100n).padStart(2, '0')}`;
  }
  return amount.toFixed(2);
}
```

**Pokaz pułapki (przed krokiem 1):** ⌃⇧R "Inline variable" na `number` i na `issuedAt`, a przy `price` wklej `basePrice(format)` ręcznie, bez `Number(...)` ("po co konwersja, skoro `money` przyjmuje też `bigint`"). Kod się kompiluje, IDE nie protestuje, bo nie wie, że `nextNumber()` ma efekt uboczny, a `clock.now()` zależy od czasu. Test równoważności jest czerwony: etykieta ma numer 2, a kod numer 1, rezerwacja trzyma o sekundę za długo (fake zegar tyka przy każdym odczycie), a cena 40 zł to "0.40". Cofnij (⌘Z). Te same trzy objawy dokumentuje stale `S05InlineTrapTest`.

### Krok 1: Inline Variable dla czystego wyrażenia użytego raz

**W IDE:** kursor na `label`, ⌃⇧R, "Inline variable".
**Po:**

```typescript
return new Ticket(code,
  'Bilet ' + code + ', cena ' + money(price)
    + ', oplata ' + money(ONLINE_FEE_GROSZE),
  issuedAt, holdUntil);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s05` - 11 testów zielonych.
**Co powiedzieć:** wyrażenie jest czyste i użyte raz, a nazwa parametru konstruktora `Ticket` (`label`) mówi to samo co zmienna. To podręcznikowy bezpieczny inline.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s05 0 1`

### Krok 2: Inline Variable, który czyta zmienną, a nie zegar

**W IDE:** kursor na `holdUntil`, ⌃⇧R, "Inline variable".
**Po:**

```typescript
return new Ticket(code, ..., issuedAt, issuedAt.plusMinutes(HOLD_MINUTES));
```

**Uruchom:** test zielony.
**Co powiedzieć:** ten inline jest bezpieczny, bo inicjalizator czyta **zmienną** `issuedAt`, która zamroziła jeden odczyt zegara. Gdybyśmy wcześniej zrobili inline `issuedAt`, ten sam ruch dałby dwa różne odczyty czasu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s05 1 2`

### Krok 3: najpierw Rename przeciążenia, potem Inline `price`

**W IDE:** przeciążenia w TypeScript dzielą jedną implementację, więc Rename Symbol nie rozdzieli ich za nas. Ręcznie: wydziel wariant groszowy do osobnej funkcji `moneyFromGrosze(grosze: bigint)`, zostaw `money(zloty: number)` jako zwykłą funkcję bez przeciążeń i zmień wywołanie `money(ONLINE_FEE_GROSZE)` na `moneyFromGrosze(ONLINE_FEE_GROSZE)` (kompilator wskaże je sam, bo `money` przestała przyjmować `bigint`). Test zielony. Teraz kursor na `price`, ⌃⇧R, "Inline variable": VS Code wklei cały inicjalizator razem z `Number(...)`.
**Po:**

```typescript
'Bilet ' + code + ', cena ' + money(Number(basePrice(format)))
  + ', oplata ' + moneyFromGrosze(ONLINE_FEE_GROSZE),
...
/** Kwota w złotych. */
function money(zloty: number): string {
  return zloty.toFixed(2);
}

/** Kwota w groszach - osobna nazwa, osobna jednostka. */
function moneyFromGrosze(grosze: bigint): string {
  return `${grosze / 100n}.${String(grosze % 100n).padStart(2, '0')}`;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konwersja w zmiennej wybierała przeciążenie. Najpierw usuwamy niejednoznaczność nazwą, a dopiero potem inline. Po rozdzieleniu nazw zgubienie `Number(...)` przy wklejaniu jest błędem kompilacji, a nie cichą zmianą ceny. `number` i `issuedAt` zostają celowo i warto dopisać im komentarz albo nic nie ruszać.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s05 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketIssuer.ts`: zostały trzy zmienne (`number`, `issuedAt`, `code`), bo każda chroni pojedynczą ewaluację albo nazywa pojęcie. Zniknęły te, które nic nie wnosiły. Test równoważności używa ręcznego `TickingClock` (każdy odczyt +1 s, w pliku `test/workshop/m4/s05_inlinevariable/TickingClock.ts`), więc wykryłby każde podwójne czytanie zegara.

### Pułapki

- Inline zmiennej z inicjalizatorem, który ma efekt uboczny (`nextSequence()`, `iterator.next()`, `queue.shift()`), zmienia **liczbę** wywołań.
- Inline odczytu czasu, losowości albo I/O zmienia **moment** i liczbę odczytów, a test z `fixedClock(...)` tego nie wykryje. Dlatego zegar w teście tyka.
- Inline zmiennej z jawnym typem albo konwersją (`: number`, `Number(...)`, `String(...)`) może wybrać inną sygnaturę przeciążenia albo zmienić jednostkę. Przy `number | bigint` czy `string | number` kompilator zaakceptuje oba warianty.
- `await` w inicjalizatorze: `const user = await load()` wklejone w dwa miejsca to dwa wywołania i dwa oczekiwania, a wklejone do wyrażenia warunkowego może się nie wykonać wcale.

### Pytanie do sali

Jak w teście wykryć, że kod czyta zegar dwa razy, skoro `fixedClock(...)` zawsze zwraca tę samą chwilę?

## Scena s06. Inline Method i nadpisanie w podklasie

**Temat ze slajdów:** Inline Method i jego ryzyka
**Katalog:** `typescript/src/workshop/m4/s06_inlinemethod` · **Test:** `scripts/warsztat.sh --lang ts test m4/s06` (`S06EquivalenceTest.test.ts`, `S06InlineOverriddenMethodTrapTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPricing` ma trzy małe metody: dwa prywatne pośredniki i chroniony hak `bookingFee()`, nadpisany w wersji internetowej. Wklejamy pośredniki, ale hak zostaje, bo wklejenie jego ciała zabiłoby nadpisanie.

**Zasada:** Inline Method usuwa pośrednictwo, które nie dodaje znaczenia, a najbezpieczniejszy jest prywatny, niepolimorficzny delegat z jednym wywołaniem. Wklejenie ciała metody nadpisywanej usuwa dynamiczną dyspozycję, a przy dekoratorze albo proxy (transakcja, cache, autoryzacja) znika to, co owijało metodę. Liczba linii nie przesądza - metoda z nazwą z domeny zostaje.

**Efekt:** `total` wprost pokazuje "cena + opłata", a kasa i internet liczą tak jak wcześniej. Hak `bookingFee()` zostaje, bo zachowanie podklasy jest częścią kontraktu klasy bazowej.

**Różnica względem Javy:** VS Code nie ma refaktoryzacji Inline Method, więc oba kroki robimy ręcznie: kopiujemy ciało w miejsce wywołania, podstawiamy argumenty i usuwamy metodę, a kompilator pokazuje, czy coś jeszcze jej używa. `@Override` to w TypeScript modyfikator `protected override` (port ma włączone `noImplicitOverride`). Klasy nie da się zamknąć jak `final`, więc `OnlineTicketPricing` to zwykła podklasa.

### Co widzimy

`TicketPricing` ma trzy małe metody, które "wyglądają na trywialne": dwa prywatne pośredniki (`base`, `addFee`) i chroniony hak `bookingFee()`, nadpisany w `OnlineTicketPricing` (+2.00).

```typescript
total(ticket: Ticket): Decimal {
  return this.addFee(this.price(ticket));
}

/** Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing. */
protected bookingFee(): Decimal {
  return new Decimal('0.00');
}

private base(ticket: Ticket): Decimal {
  return basePrice(ticket.format);
}

// OnlineTicketPricing
protected override bookingFee(): Decimal {
  return new Decimal('2.00');
}
```

### Krok 1: Inline Method trywialnego delegata

**W IDE:** ⇧F12 na `base` pokazuje jedno wywołanie. Zastąp `this.base(ticket)` ciałem metody (`basePrice(ticket.format)`) i usuń metodę `base`. Brak błędów kompilacji potwierdza, że nie było innych użyć.
**Po:**

```typescript
const price = basePrice(ticket.format);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s06` - 19 testów zielonych (kasa i internet).
**Co powiedzieć:** prywatny, niepolimorficzny delegat z jednym wywołaniem to najbezpieczniejszy możliwy inline. Nazwa `base` nic nie dodawała do `basePrice`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s06 0 1`

### Krok 2: Inline Method wywołującego hak, a nie samego haka

**W IDE:** najpierw pokaż pułapkę: ⇧F12 na `bookingFee()` w klasie bazowej pokazuje też `override` w `OnlineTicketPricing`, ale nic nie blokuje ręcznego wklejenia ciała. Wklej je w `addFee`: `price.plus(new Decimal('0.00'))`. Kompiluje się, a internet sprzedaje bez opłaty. Test `S06InlineOverriddenMethodTrapTest.inliningAnOverriddenMethodSilentlyDropsTheOnlineFee` pokazuje dokładnie ten wynik: `40.00` zamiast `42.00`. Cofnij (⌘Z). Teraz poprawnie: w `total` zastąp `this.addFee(...)` ciałem `addFee` z podstawionym argumentem i usuń `addFee`.
**Po:**

```typescript
total(ticket: Ticket): Decimal {
  return this.price(ticket).plus(this.bookingFee());
}
```

**Uruchom:** test zielony, `onlineTotalsStayTheSame` nadal daje 42.00.
**Co powiedzieć:** wklejamy **wywołanie** metody polimorficznej, więc dynamiczna dyspozycja zostaje. Wklejenie **ciała** metody nadpisywanej zabija nadpisanie po cichu, a podklasa kompiluje się dalej, z poprawnym `override`, bo metoda w klasie bazowej nadal istnieje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s06 1 2`

### Rozwiązanie i uzasadnienie

`step2/TicketPricing.ts`: bez pośredników, `total` wprost pokazuje "cena + opłata", a `bookingFee()` zostaje hakiem dla podklasy. Test równoważności uruchamia **obie** klasy, bo zachowanie podklasy jest częścią kontraktu klasy bazowej (bilety wspólne dla obu plików testów są w `test/workshop/m4/s06_inlinemethod/tickets.ts`).

### Pułapki

- Inline metody nadpisywanej albo implementującej interfejs usuwa dynamiczną dyspozycję.
- Inline metody owiniętej dekoratorem albo wołanej przez proxy (np. `@Transactional` w NestJS/TypeORM, cache, autoryzacja) usuwa transakcję, cache albo sprawdzenie uprawnień. Inline metody `async` zmienia miejsce, w którym czekamy na `await`.
- Parametr użyty w ciele dwa razy, a w wywołaniu argument z efektem ubocznym: po ręcznym inline efekt wykona się dwa razy. Wtedy najpierw wprowadź zmienną na argument.
- Liczba linii nie przesądza. Metoda o nazwie z domeny (`qualifiesForGroupDiscount`) zostaje, nawet jeśli ma jedną linię.

### Pytanie do sali

Jak test klasy bazowej ma "wiedzieć" o podklasach, które jeszcze nie istnieją albo są w innym pakiecie npm?

## Scena s07. Move Method - Feature Envy i pułapka przeciążenia

**Temat ze slajdów:** Move Method i Move Field - wybór właściciela; Move Method krok po kroku i ryzyka
**Katalog:** `typescript/src/workshop/m4/s07_movemethod` · **Test:** `scripts/warsztat.sh --lang ts test m4/s07` (`S07EquivalenceTest.test.ts`, `S07OverloadTrapTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BookingPrinter` ma dwie metody, które używają wyłącznie danych `Screening`. Przenosimy je do seansu i zmieniamy im nazwy, ale sposób usuwania miejsca z listy (po wartości, nie po pozycji) zostaje bez zmian.

**Zasada:** Move Method przenosi zachowanie do klasy, która ma jego dane i odpowiedzialność. Feature Envy to sygnał do analizy, a nie nakaz, bo metoda koordynująca kilka obiektów może zostać na miejscu. Przeniesienie zmienia kontekst typów, więc "sprzątanie" przy okazji może po cichu zmienić znaczenie operacji: tutaj usuwanie miejsca o danym numerze zamienia się w usuwanie elementu o danym indeksie.

**Efekt:** `Screening` ma `headline` i `freeSeatsWithout`, a `BookingPrinter` tylko składa wydruk, który jest identyczny. Zapis `splice(indexOf(seat), 1)` wygląda na zbyt rozwlekły, więc jego powód zapisujemy w JSDoc.

**Różnica względem Javy:** w Javie pułapką jest zmiana typu parametru `Integer` na `int`, po której `free.remove(seat)` wybiera przeciążenie `remove(int index)` zamiast `remove(Object)`. TypeScript nie wybiera przeciążeń po typie statycznym, a `number` to jeden typ, więc pułapka w porcie to dwa idiomy JavaScript, które odpowiadają tym przeciążeniom: usuwanie po wartości `free.splice(free.indexOf(seat), 1)` kontra "uproszczone" usuwanie po pozycji `free.splice(seat, 1)`. Oba się kompilują. Zamiast `IndexOutOfBoundsException` dla miejsca spoza zakresu `splice` po cichu nic nie usuwa. Nazwa testu `S07OverloadTrapTest` i nazwy przypadków zostały jak w Javie. VS Code nie ma Move Method między klasami, więc przenosimy ręcznie.

### Co widzimy

`BookingPrinter` ma dwie metody, które używają **wyłącznie** danych `Screening`. `print` koordynuje `Booking` i `Screening`, więc zostaje.

```typescript
private screeningLine(s: Screening): string {
  let format: string;
  switch (s.format) { ... }
  return s.title + ' (' + format + '), sala ' + s.hall + ', '
    + s.start.toLocalDate().toString() + ' ' + s.start.toLocalTime().toString();
}

private remainingSeats(s: Screening, seat: number): number[] {
  const free = [...s.freeSeats];
  free.splice(free.indexOf(seat), 1);   // usuń MIEJSCE o tym numerze
  return free;
}
```

### Krok 1: Move Method - opis seansu

**W IDE:** ręcznie: skopiuj `screeningLine` do klasy `Screening` jako publiczną metodę bez parametru, zamień `s.` na `this.` (zaznacz `s.` i ⌘D, żeby zaznaczyć kolejne wystąpienia), a lokalną zmienną `format` przemianuj F2 na `formatName`, bo zasłaniałaby pole `this.format`. W `BookingPrinter` zamień wywołanie na `booking.screening.headline()` i usuń starą metodę. Na koniec F2 na nowej metodzie, jeśli nazwa jest jeszcze inna niż `headline`.
**Po:**

```typescript
// Screening
headline(): string {
  let formatName: string;
  switch (this.format) { ... }
  return this.title + ' (' + formatName + '), sala ' + this.hall + ', ' + ...;
}
// BookingPrinter
+ booking.screening.headline() + '\n'
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s07` - 11 testów zielonych.
**Co powiedzieć:** metoda poszła tam, gdzie są jej dane. Wybór właściciela jest decyzją, a nie automatem: `print` też sięga do `Screening`, ale jej zadaniem jest złożenie wydruku z kilku obiektów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s07 0 1`

### Krok 2: Move Method - wolne miejsca, bez "sprzątania" przy przenosinach

**W IDE:** najpierw pokaż pułapkę: przenieś `remainingSeats` do `Screening` i "posprzątaj" `free.splice(free.indexOf(seat), 1)` do `free.splice(seat, 1)`, bo w `Screening` wszystkie numery to po prostu `number`. Kompiluje się. Test jest czerwony: przy miejscu 1 zostało `[1, 3, 4, 5]`, a przy miejscu 8 w `[7, 8, 9]` zarezerwowane miejsce zostało na liście, bo teraz usuwamy po pozycji. Cofnij. Poprawnie: przenieś metodę bez zmiany ciała, F2 na `freeSeatsWithout`, a w JSDoc zapisz, dlaczego usuwamy po wartości.
**Po:**

```typescript
/**
 * Wolne miejsca bez wskazanego. Usuwamy po WARTOŚCI (`splice(indexOf(seat), 1)`), nie po pozycji:
 * "uproszczone" przy przenosinach `splice(seat, 1)` też się kompiluje (seat to number),
 * ale usuwa element o indeksie seat, a nie miejsce o tym numerze.
 */
freeSeatsWithout(seat: number): number[] {
  const free = [...this.freeSeats];
  free.splice(free.indexOf(seat), 1);
  return free;
}
```

**Uruchom:** test zielony. `S07OverloadTrapTest` dokumentuje oba wyniki pułapki (`integerParameterRemovesTheSeatNumber` i `intParameterRemovesTheElementAtIndex`).
**Co powiedzieć:** przeniesienie metody zmienia kontekst: inne pola, inne typy, inne pokusy "uproszczenia". Zwróć uwagę na trzeci przypadek testu: miejsce 2 na pozycji 2 **nie** odróżnia usuwania po indeksie od usuwania po wartości. Jeden przypadek testowy to za mało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s07 1 2`

### Rozwiązanie i uzasadnienie

`step2`: `Screening` ma zachowanie o seansie (`headline`, `freeSeatsWithout`), a `BookingPrinter` tylko składa wydruk. Wydruk jest identyczny dla trzech rezerwacji, w tym takich, gdzie numer miejsca różni się od indeksu.

### Pułapki

- Zmiana typu albo idiomu "przy okazji" przenosin (usuwanie po wartości na usuwanie po indeksie, `readonly number[]` na `number[]`, `number` na `string` z formularza) zmienia znaczenie operacji, a kompilator milczy.
- Metoda przenoszona z klasy, która ma podklasy nadpisujące tę metodę. Nadpisania zostają w starym miejscu i przestają działać. Port ma w `tsconfig.json` włączone `noImplicitOverride`, więc nadpisanie musi mieć `override`, a po usunięciu metody z klasy bazowej kompilator to zgłosi. Bez tej flagi podklasa kompiluje się dalej i po cichu definiuje nową, osieroconą metodę.
- Metoda przekazywana jako callback (`items.map(this.format)`) po przeniesieniu traci właściwe `this`. Wtedy potrzebna jest funkcja strzałkowa albo `bind`.
- Publiczna metoda przeniesiona bez delegatu w starym miejscu psuje zewnętrznych klientów. Delegat może zostać jako fasada.

### Pytanie do sali

`print` też używa głównie danych `Booking` i `Screening`. Dlaczego nie przenosimy jej do `Booking`?

## Scena s08. Move Field - próg VIP należy do sali

**Temat ze slajdów:** Move Field krok po kroku i ryzyka; Move Method i Move Field - wybór właściciela
**Katalog:** `typescript/src/workshop/m4/s08_movefield` · **Test:** `scripts/warsztat.sh --lang ts test m4/s08` (`S08EquivalenceTest.test.ts`, `S08SingleSourceOfTruthTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Próg VIP `vipFromRow` siedzi w seansie, choć jest cechą sali, więc dwa seanse w tej samej sali mogą się różnić. Przenosimy pole do `Hall` przez getter, a potem zamieniamy odczyty surowego progu na pytanie o regułę.

**Zasada:** Move Field przenosi stan do właściciela, a zaczyna się od Self-Encapsulate Field, żeby mieć jedno miejsce odczytu. Najpierw migrujemy odczyty, potem zapisy, bez okresu z dwiema zapisywalnymi kopiami (dual write). Go to References nie pokaże użyć przez dostęp po nazwie (`obj[key]`), ORM ani serializację.

**Efekt:** Próg VIP ma jednego właściciela i jedną regułę `Hall.isVip`, a wycena miejsca się nie zmienia. Kosztem jest zmieniony konstruktor `Screening`, a w systemie z bazą danych także osobna migracja danych.

**Różnica względem Javy:** TypeScript nie ma widoczności pakietowej, więc w `start` `vipFromRow` to publiczne pole `readonly`, które `SeatPricer` czyta bezpośrednio. Self-Encapsulate Field to prywatne pole `#vipFromRow` i getter `get vipFromRow()`. Getter jest przezroczysty dla klienta: odczyt `screening.vipFromRow` w `SeatPricer` i w `isVip` nie zmienia zapisu, więc klienci nie wymagają żadnej edycji (w Javie IDE przepisuje odczyty na `vipFromRow()`). To zaleta TypeScript warta powiedzenia na pokazie. Pozostałe akcesory rekordów z Javy (`hall()`, `format()`, `name()`) to pola `readonly`.

### Co widzimy

Od którego rzędu zaczynają się miejsca VIP, to cecha **sali**, a pole `vipFromRow` siedzi w **seansie**. Każdy seans w tej samej sali niesie własną kopię i nic nie pilnuje zgodności. `SeatPricer` czyta publiczne pole bezpośrednio.

```typescript
export class Screening {
  constructor(
    readonly hall: Hall,
    readonly format: number,
    readonly vipFromRow: number,
  ) {}
  ...
}
// SeatPricer
const price = row >= screening.vipFromRow ? base.plus(VIP_SURCHARGE) : base;
```

`S08SingleSourceOfTruthTest.startLetsScreeningsInOneHallDisagree` pokazuje, że w `start` ten sam fotel w tej samej sali bywa VIP albo nie, zależnie od seansu.

### Krok 1: Self-Encapsulate Field

**W IDE:** ręcznie: zamień parametr-pole `readonly vipFromRow: number` na zwykły parametr konstruktora, dodaj prywatne pole `readonly #vipFromRow: number` przypisane w konstruktorze i getter `get vipFromRow()`. (VS Code ma ⌃⇧R "Generate 'get' and 'set' accessors", ale tworzy pole z podkreśleniem `_vipFromRow`, a port używa pól prywatnych `#`.) Odczyty w `isVip` i w `SeatPricer` zostają bez zmian, bo idą już przez getter.
**Po:**

```typescript
readonly #vipFromRow: number;

constructor(
  readonly hall: Hall,
  readonly format: number,
  vipFromRow: number,
) {
  this.#vipFromRow = vipFromRow;
}

get vipFromRow(): number {
  return this.#vipFromRow;
}

isVip(row: number): boolean {
  return row >= this.vipFromRow;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s08` - 14 testów zielonych.
**Co powiedzieć:** teraz jest dokładnie jedno miejsce, w którym zmienimy źródło wartości. Bez tego kroku przeniesienie oznaczałoby poprawianie każdego odczytu ręcznie. W TypeScript getter nie zmienia zapisu po stronie klientów, więc ten krok nie dotyka `SeatPricer`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s08 0 1`

### Krok 2: Move Field do Hall

**W IDE:** VS Code nie ma Change Signature, więc ręcznie: w `Hall` dodaj parametr-pole `readonly vipFromRow: number` w konstruktorze. W `Screening` ciało gettera zamień na `return this.hall.vipFromRow;`, usuń pole `#vipFromRow` i parametr konstruktora. Kompilator pokaże wszystkie miejsca tworzące sale i seanse (tu: adaptery w teście) - każde przekazuje teraz próg do sali.
**Po:**

```typescript
// Hall
constructor(readonly name: string, readonly vipFromRow: number) {}
// Screening
get vipFromRow(): number {
  return this.hall.vipFromRow;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** przenosimy w jednym ruchu, bez okresu, w którym obie klasy mają zapisywalną kopię (dual write). Dwie kopie rozjadą się przy pierwszym wyjątku albo przy pierwszym zapomnianym zapisie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s08 1 2`

### Krok 3: aktualizacja odczytów i usunięcie akcesora przejściowego

**W IDE:** w `Hall` dodaj `isVip(row: number)`. W `Screening.isVip` zamień ciało na `this.hall.isVip(row)`. W `SeatPricer` zamień `row >= screening.vipFromRow` na `screening.isVip(row)` i wydziel stałą `vip` (⌃⇧R, "Extract to constant"), której użyj też w opisie `(VIP)`. Na koniec ⇧F12 na getterze `Screening.vipFromRow` - brak odwołań, więc go usuń - i zmień pole w `Hall` na `private readonly`. Kompilator potwierdzi, że nikt spoza `Hall` go nie czyta.
**Po:**

```typescript
// Hall
constructor(readonly name: string, private readonly vipFromRow: number) {}

isVip(row: number): boolean {
  return row >= this.vipFromRow;
}
// SeatPricer
const vip = screening.isVip(row);
const price = vip ? base.plus(VIP_SURCHARGE) : base;
```

**Uruchom:** test zielony, także `afterMoveFieldTheHallDecidesForEveryScreening`.
**Co powiedzieć:** najpierw migrujemy odczyty, a stary akcesor usuwamy dopiero, gdy nikt go nie używa. Przy okazji klienci przestali porównywać surowy próg i pytają o regułę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s08 2 3`

### Rozwiązanie i uzasadnienie

`step3`: próg VIP ma jednego właściciela (`Hall`) i jedną regułę (`Hall.isVip`). Wycena miejsca jest identyczna na każdym etapie. Zmienił się sposób tworzenia seansu, bo konstruktor stracił parametr. W systemie z bazą danych to osobna migracja danych.

### Pułapki

- Pole używane przez dostęp po nazwie (`obj['vipFromRow']`), ORM (kolumna w tabeli seansów), `JSON.stringify` / deserializację albo porównanie obiektów. Go to References tego nie pokaże.
- Utrata modyfikatorów przy przenoszeniu: `readonly`, `private`, `#`. Bez `readonly` w nowym właścicielu wraca możliwość zapisu z zewnątrz.
- Pole prywatne `#` nie trafia do `JSON.stringify` ani `Object.keys`, a pole `private` z TypeScript trafia. Zamiana jednego na drugie przy przenosinach zmienia serializację.
- Migracja zapisów przed odczytami albo dwie zapisywalne kopie "na chwilę".
- Seanse, które naprawdę miały różny próg w tej samej sali (np. inna konfiguracja foteli). Wtedy Move Field jest zmianą zachowania i trzeba najpierw sprawdzić dane.

### Pytanie do sali

Jak przeprowadzić ten Move Field, gdy `vipFromRow` jest kolumną w tabeli `screening` z milionem wierszy?

## Scena s09. Extract Class - klient, płatność i klasa-worek

**Temat ze slajdów:** Extract Class - cel, mechanika i zły wynik
**Katalog:** `typescript/src/workshop/m4/s09_extractclass` · **Test:** `scripts/warsztat.sh --lang ts test m4/s09` (`S09EquivalenceTest.test.ts`, `S09StructureTest.test.ts`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Booking` ma siedem pól i trzy powody zmiany: dane klienta, płatność i samą rezerwację. Wydzielamy `Customer` i `Payment`, przy czym pierwszy krok celowo daje zły wynik, który potem naprawiamy przeniesieniem zachowania.

**Zasada:** Extract Class wydziela spójne pola i operacje z odrębnym powodem zmiany, a Move Field i Move Method to tylko mechanika tej decyzji. Zły wynik to klasa-worek: dane przeniesione, a logika dalej w źródle przez odczyty pól. Nowa klasa nie powinna trzymać referencji zwrotnej do źródła, bo powstaje cykl.

**Efekt:** `Booking` ma cztery pola i składa rezerwację z dwóch klas z własnym zachowaniem, a jego publiczne API działa jak wcześniej jako fasada. Delegaty `pay`, `isPaid` i `contact` zostają w `Booking`, dopóki klienci nie zechcą wołać nowych klas wprost.

**Różnica względem Javy:** `record Customer` to klasa z polami `readonly` (worek z kroku 1 to klasa bez metod). `S09StructureTest` nie ma refleksji Javy: pola liczy `Object.keys(instancja)` (pola `private` z TypeScript są w JavaScript zwykłymi właściwościami, więc liczą się jak `getDeclaredFields`), a "zachowanie" to publiczne metody sprawdzane na poziomie typów (`expectTypeOf<Behaviour<Customer>>()`, bo `keyof` widzi tylko publiczne składowe) oraz, dla worka z kroku 1, pusta lista metod na prototypie. Karta na starcie to `''` zamiast `null`, `toLowerCase(Locale.ROOT)` to zwykłe `toLowerCase()`, a kwota to `Money`.

### Co widzimy

`Booking` ma siedem pól i trzy powody zmiany: dane klienta i ich formatowanie (e-mail, telefon), płatność (karta, status, maskowanie) oraz samą rezerwację (id, kwota).

```typescript
private cardNumber = '';
private paymentStatus = 'NEW';

constructor(
  private readonly id: string,
  private readonly customerName: string,
  private readonly customerEmail: string,
  private readonly customerPhone: string,
  private readonly amount: Money,
) {}

contact(): string {
  return this.customerName + ' <' + this.customerEmail.trim().toLowerCase() + '>, tel. '
    + this.formattedPhone();
}
```

### Krok 1: Extract Class - tylko dane (zły wynik pośredni)

**W IDE:** VS Code nie ma Extract Class, więc ręcznie: utwórz plik `Customer.ts` z klasą `Customer(readonly name, readonly email, readonly phone)`. W `Booking` trzy parametry `customer*` przestają być polami (usuń `private readonly`), a konstruktor tworzy `this.customer = new Customer(...)`. Kompilator wskaże każdy odczyt `this.customerEmail` i `this.customerPhone` - zamień je na `this.customer.email` i `this.customer.phone`.
**Po:**

```typescript
export class Customer {
  constructor(readonly name: string, readonly email: string, readonly phone: string) {}
}

// Booking
contact(): string {
  return this.customer.name + ' <' + this.customer.email.trim().toLowerCase() + '>, tel. '
    + this.formattedPhone();
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s09` - 15 testów zielonych, także `step1CustomerIsADataBagWithoutBehaviour`.
**Co powiedzieć:** to jest **zły wynik**, jeśli się tu zatrzymamy. Mamy klasę-worek z polami, a cała wiedza o kliencie nadal siedzi w `Booking`. Dane się przeniosły, odpowiedzialność nie. Test struktury to pokazuje: `Customer` nie ma na prototypie ani jednej metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s09 0 1`

### Krok 2: Move Method - zachowanie idzie za danymi

**W IDE:** w `Booking.contact()` zaznacz wyrażenie po `return` i ⌃⇧R, "Extract to method", nazwa `contactLine`. Potem ręcznie przenieś `contactLine` i `formattedPhone` do `Customer` (w ciałach `this.customer.x` staje się `this.x`, a `formattedPhone` zostaje `private`). `Booking.contact()` zostaje jako delegat.
**Po:**

```typescript
export class Customer {
  constructor(readonly name: string, readonly email: string, readonly phone: string) {}

  contactLine(): string {
    return this.name + ' <' + this.email.trim().toLowerCase() + '>, tel. ' + this.formattedPhone();
  }
  ...
}
// Booking
contact(): string {
  return this.customer.contactLine();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz `Customer` ma własną odpowiedzialność. Extract Class to decyzja projektowa, a Move Field i Move Method to mechanika jej realizacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s09 1 2`

### Krok 3: Extract Class - płatność od razu z zachowaniem

**W IDE:** utwórz plik `Payment.ts` z klasą `Payment`, przenieś do niej pola `cardNumber` i `paymentStatus` (jako `status`) oraz metody `pay`, `isPaid`, `maskedCard`. Metoda `Payment.payWith(card, bookingId)` dostaje id jako **wartość**, bez referencji do `Booking`. W `Booking` zostają delegaty `pay` i `isPaid`, a `summary` woła `this.payment.description()`.
**Po:**

```typescript
export class Payment {
  private cardNumber = '';
  private status = 'NEW';

  payWith(card: string, bookingId: string): void {
    if (this.status !== 'NEW') {
      throw new IllegalStateError('Rezerwacja ' + bookingId + ' jest juz oplacona');
    }
    ...
  }

  isPaid(): boolean { ... }

  description(): string {
    return this.isPaid() ? 'oplacona karta ' + this.maskedCard() : 'oczekuje';
  }
}
```

**Uruchom:** test zielony, także druga płatność odrzucona z tym samym komunikatem i `step3BookingComposesCustomerAndPayment`.
**Co powiedzieć:** tym razem od razu przenosimy dane **i** zachowanie. `Payment` nie zna `Booking`, więc nie powstał cykl importów. Publiczne API `Booking` się nie zmieniło, bo działa jako fasada.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s09 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `Booking` ma cztery pola (`amount`, `customer`, `id`, `payment`) i składa rezerwację z dwóch spójnych części. Każda z nich ma jeden powód zmiany: formaty kontaktu, płatność i PCI. Podsumowanie i komunikaty błędów są identyczne na każdym etapie.

### Pułapki

- **Klasa-worek:** przeniesione dane, logika w źródle przez odczyty pól (krok 1). Drugi rodzaj worka to jedna klasa `BookingDetails` na "wszystko, co nie pasuje", bez spójności.
- Referencja zwrotna (`new Payment(this)`) tworzy cykl. W TypeScript cykl klas w dwóch modułach potrafi dodatkowo dać `undefined` przy imporcie w czasie ładowania. Lepiej przekazać wartość albo wąski interfejs.
- Dwa źródła prawdy: pole zostawione w `Booking` "na wszelki wypadek" obok kopii w nowej klasie.
- Zmiana publicznego API przy okazji ekstrakcji (np. `booking.payment.payWith(...)` u klientów). To osobny krok.

### Pytanie do sali

Po czym na przeglądzie kodu rozpoznać, że wydzielona klasa jest workiem, a nie pojęciem?

## Scena s10. Encapsulate Field - status, który każdy może nadpisać

**Temat ze slajdów:** Encapsulate Field
**Katalog:** `typescript/src/workshop/m4/s10_encapsulatefield` · **Test:** `scripts/warsztat.sh --lang ts test m4/s10` (`S10EquivalenceTest.test.ts`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Publiczne pole `Reservation.status` pozwala każdemu wpisać dowolny status, także gościa na anulowaną rezerwację. Najpierw ukrywamy pole i zastępujemy setter operacjami domenowymi, a na końcu osobnym krokiem dodajemy pilnowanie przejść.

**Zasada:** Encapsulate Field daje dostęp do stanu tylko przez operacje właściciela, więc można kontrolować zmiany i pilnować niezmiennika. Migracja idzie przez trywialne akcesory, migrację klientów i zwężenie widoczności. Walidacja to już zmiana zachowania, więc dodajemy ją osobno, nie razem z getterem i setterem.

**Efekt:** Po kroku 2 pole jest prywatne, nie ma settera, a zachowanie jest identyczne. Krok 3 świadomie zmienia zachowanie: niedozwolone przejścia rzucają `IllegalStateError` zamiast być cicho ignorowane, więc to osobny commit z decyzją biznesu.

**Różnica względem Javy:** Encapsulate Field w TypeScript to prywatne pole `#status` i akcesory `get status()` / `set status(...)` zamiast `getStatus()` / `setStatus(...)`. Klienci nie zmieniają zapisu - `r.status = 'PAID'` woła teraz setter - więc diff `start -> step1` dotyczy tylko `Reservation` (i komentarza kasy). Krok 2 usuwa setter (Remove Setting Method) i zostawia sam getter `status`, co odpowiada Rename `getStatus` -> `status()` z Javy. `IllegalStateException` to `IllegalStateError`, a varargs `String...` to parametr resztowy `...allowedFrom: string[]`.

### Co widzimy

`Reservation.status` to publiczne pole. Reguły przejść (NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED) pilnuje kasa, a `guestEntry` po prostu przypisuje `'USED'`, także na rezerwację anulowaną.

```typescript
export class Reservation {
  status = 'NEW';
}
// BoxOffice
guestEntry(r: Reservation): void {
  r.status = 'USED';
}
```

Test równoważności zapisuje dziwne zachowanie jako `ZASTANE: ...`: płatność po wygaśnięciu, druga płatność i wejście bez płatności są cicho ignorowane, a gość wchodzi na anulowaną rezerwację.

### Krok 1: Encapsulate Field

**W IDE:** zmień pole na prywatne `#status = 'NEW'` i dopisz trywialne akcesory `get status()` i `set status(...)`. (⌃⇧R "Generate 'get' and 'set' accessors" zrobi to samo z polem `_status`, jeśli wolisz ruch automatyczny, ale port używa pól `#`.) `BoxOffice` zostaje bez zmian.
**Po:**

```typescript
#status = 'NEW';

get status(): string {
  return this.#status;
}

set status(status: string) {
  this.#status = status;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s10` - 29 testów zielonych, także `startFieldIsPublicFromStep1ItIsNot` (w `start` `status` to własna właściwość obiektu, od `step1` akcesor na prototypie - `Object.hasOwn(...)`).
**Co powiedzieć:** trywialne akcesory na tym samym polu to czysta refaktoryzacja, bo setter przyjmuje wszystko jak wcześniej. Zysk jest taki, że każdy zapis przechodzi teraz przez jedno miejsce. W TypeScript klienci nawet tego nie widzą - to zaleta i zarazem ryzyko, bo `r.status = ...` wygląda jak przypisanie, a jest wywołaniem.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s10 0 1`

### Krok 2: operacje domenowe zamiast settera

**W IDE:** w `BoxOffice.pay` przenieś ciało do `Reservation` jako metodę `pay()` (ręcznie: `r.status` staje się `this.#status`), a w kasie zostaw `r.pay()`. Powtórz dla `checkIn`, `cancel`, `expire`, a `guestEntry` przenieś jako `admitGuestWithoutPayment()`. Na końcu usuń setter `set status(...)` - kompilator potwierdzi, że nikt już nie przypisuje statusu z zewnątrz.
**Po:**

```typescript
pay(): void {
  if (this.#status === 'NEW') {
    this.#status = 'PAID';
  }
}

admitGuestWithoutPayment(): void {
  this.#status = 'USED';
}
```

**Uruchom:** test zielony, bo warunki przepisaliśmy 1:1. Test sprawdza też, że w `step2` zapis do `status` jest błędem kompilacji (`@ts-expect-error`) i rzuca `TypeError` w czasie wykonania.
**Co powiedzieć:** `pay()` komunikuje dozwoloną zmianę lepiej niż `status = 'PAID'`. Obejście z `guestEntry` ma teraz uczciwą nazwę i jest widoczne w API, a nie ukryte w przypisaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s10 1 2`

### Krok 3: niezmiennik w operacjach (świadoma zmiana zachowania)

**W IDE:** ręcznie: prywatna `moveTo(target, ...allowedFrom)` rzuca `IllegalStateError`, gdy przejście jest niedozwolone. Każda operacja deleguje do `moveTo`.
**Po:**

```typescript
pay(): void {
  this.moveTo('PAID', 'NEW');
}

admitGuestWithoutPayment(): void {
  this.moveTo('USED', 'NEW', 'PAID');
}

private moveTo(target: string, ...allowedFrom: string[]): void {
  for (const allowed of allowedFrom) {
    if (this.#status === allowed) {
      this.#status = target;
      return;
    }
  }
  throw new IllegalStateError('Niedozwolone przejscie ' + this.#status + ' -> ' + target);
}
```

**Uruchom:** test zielony, ale dla `step3` z **innymi oczekiwaniami** (`step3RejectsIllegalTransitions`): dozwolone ścieżki bez zmian, a przypadki `ZASTANE` kończą się `BLAD`.
**Co powiedzieć:** to już nie jest refaktoryzacja. Zmieniają się akceptowane operacje i wyjątki, więc to osobny commit z wymaganiem od biznesu. Hermetyzacja z kroków 1-2 sprawiła, że ta zmiana dotyczy jednej klasy.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s10 2 3`

### Rozwiązanie i uzasadnienie

`step2` to koniec refaktoryzacji: pole prywatne, operacje domenowe i identyczne zachowanie. `step3` to zatwierdzona zmiana kontraktu, którą test opisuje osobno. Kolejność ma znaczenie: najpierw hermetyzacja bez zmiany zachowania, potem reguła.

### Pułapki

- Walidacja dodana razem z getterem i setterem w jednym commicie. Nie da się wtedy odróżnić refaktoryzacji od zmiany zachowania.
- Setter, który zostaje "na wszelki wypadek". Wtedy niezmiennik da się obejść tak samo jak przez publiczne pole - a w TypeScript nawet nie widać, że to setter.
- `private` z TypeScript chroni tylko w kompilacji: `(r as any).status = 'X'` albo JavaScript bez typów zapisze pole. Pole `#` jest prywatne także w czasie wykonania.
- Kod, który zapisuje stan z pominięciem operacji: `Object.assign(r, dane)`, deserializacja, ORM ustawiający właściwości. Omija operacje domenowe.

### Pytanie do sali

Kto w waszej organizacji decyduje, że "gość na anulowaną rezerwację" to błąd, a nie funkcja? Jak test ma to zapisać, zanim decyzja zapadnie?

## Scena s11. Encapsulate Collection - trzy kontrakty listy miejsc

**Temat ze slajdów:** Encapsulate Collection; Trzy różne kontrakty kolekcji
**Katalog:** `typescript/src/workshop/m4/s11_encapsulatecollection` · **Test:** `scripts/warsztat.sh --lang ts test m4/s11` (`S11EquivalenceTest.test.ts`, `S11CollectionContractTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Booking.seats` to publiczna, mutowalna tablica i każdy może zmienić miejsca z pominięciem właściciela. Przenosimy dodawanie i usuwanie do `Booking`, a potem zmieniamy getter na niemodyfikowalny widok i wreszcie na migawkę.

**Zasada:** Encapsulate Collection oddaje właścicielowi kontrolę nad członkostwem: odczyt przez kontrakt, zmiana przez `addSeat`/`removeSeat` lub polecenia. Widok (odpowiednik `Collections.unmodifiableList`) i migawka (odpowiednik `List.copyOf`) to dwa różne kontrakty, bo tylko widok pokazuje późniejsze zmiany. Niemodyfikowalność nie chroni wnętrza elementów.

**Efekt:** Kasa działa tak samo na każdym etapie, ale kroki 2 i 3 świadomie zmieniają kontrakt gettera: klient nie może już modyfikować listy, a po kroku 3 nie widzi późniejszych zmian. Wybór między widokiem a migawką zależy od tego, czego potrzebują klienci.

**Różnica względem Javy:** JavaScript nie ma `Collections.unmodifiableList` ani `List.copyOf`. Widok w `step2` to `Proxy` na prywatnej tablicy, którego pułapki `set` i `deleteProperty` rzucają `UnsupportedOperationError`, a typ `readonly Seat[]` blokuje zmiany już w kompilacji. Migawka w `step3` to `Object.freeze([...this.#seats])` (zapis rzuca `TypeError`). Tablice porównują referencje, więc `Seat` ma `equals`, a usuwanie to `findIndex(s => s.equals(seat))` i `splice` z osłoną `index >= 0` (samo `splice(-1, 1)` usunęłoby ostatni element) - odpowiednik `List.remove(Object)`. Pole i getter mają tę samą nazwę dzięki `#seats` i `get seats()`. Test kontraktu rzutuje wynik gettera na `Seat[]`, żeby sprawdzić zachowanie w czasie wykonania.

### Co widzimy

`Booking.seats` to publiczna, mutowalna tablica. `readonly` blokuje tylko przypisanie, więc każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.

```typescript
readonly seats: Seat[] = [];
// SeatDesk
booking.seats.push(seat);
...
const index = booking.seats.findIndex((s) => s.equals(seat));
if (index >= 0) {
  booking.seats.splice(index, 1);
}
```

### Krok 1: operacje w właścicielu, getter bez zmiany kontraktu

**W IDE:** zmień pole na prywatne `readonly #seats: Seat[] = []` i dodaj getter `get seats(): Seat[]`, który zwraca **tę samą** tablicę. Dodaj `addSeat` i `removeSeat` w `Booking` (ciała przenieś ręcznie z `SeatDesk`) i przepisz `SeatDesk` na te operacje.
**Po:**

```typescript
readonly #seats: Seat[] = [];

/** Przejściowo: żywa, MODYFIKOWALNA tablica - dokładnie to, co dawało publiczne pole. */
get seats(): Seat[] {
  return this.#seats;
}

addSeat(seat: Seat): void {
  this.#seats.push(seat);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s11` - 16 testów zielonych, `step1GetterStillReturnsTheSameAlias`: klient zmienia tak, widzi zmiany tak.
**Co powiedzieć:** to faza przejściowa, która nadal jest czystą refaktoryzacją. Klient, który jeszcze modyfikuje listę przez getter, działa jak wcześniej, a my migrujemy klientów po kolei.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s11 0 1`

### Krok 2: niemodyfikowalny widok

**W IDE:** w getterze zwróć `new Proxy(this.#seats, READ_ONLY)` i zmień typ na `readonly Seat[]`. Kompilator od razu pokaże klientów, którzy jeszcze modyfikują listę przez getter.
**Po:**

```typescript
/** Żywy widok tylko do odczytu. */
get seats(): readonly Seat[] {
  return new Proxy(this.#seats, READ_ONLY);
}
...
const READ_ONLY: ProxyHandler<Seat[]> = {
  set: () => {
    throw new UnsupportedOperationError('lista miejsc tylko do odczytu');
  },
  deleteProperty: () => {
    throw new UnsupportedOperationError('lista miejsc tylko do odczytu');
  },
};
```

**Uruchom:** test równoważności zielony (kasa używa już `addSeat`/`removeSeat`). `S11CollectionContractTest`: klient zmienia nie, widzi zmiany tak.
**Co powiedzieć:** to pierwsza **zmiana kontraktu** gettera. Jest bezpieczna dopiero wtedy, gdy żaden klient nie modyfikuje listy przez getter. Widok jest żywy, więc wcześniej pobrana lista pokaże miejsca dodane później. Sam typ `readonly Seat[]` byłby tylko obietnicą kompilatora - rzutowanie albo JavaScript bez typów obszedłby ją bez `Proxy`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s11 1 2`

### Krok 3: migawka

**W IDE:** w getterze `return Object.freeze([...this.#seats]);` i usuń nieużywane `READ_ONLY`.
**Po:**

```typescript
/** Niemodyfikowalna kopia z chwili wywołania. */
get seats(): readonly Seat[] {
  return Object.freeze([...this.#seats]);
}
```

**Uruchom:** test równoważności zielony. `S11CollectionContractTest`: klient zmienia nie, widzi zmiany nie.
**Co powiedzieć:** widok czy migawka to decyzja o kontrakcie, a nie szczegół implementacji. Klient, który trzymał listę i liczył, że "sama się odświeży", po tym kroku widzi stare dane.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s11 2 3`

### Rozwiązanie i uzasadnienie

Tabela ze slajdu jako test:

| Wariant | Klient zmienia | Widzi późniejsze zmiany |
|---|:---:|:---:|
| `start` (publiczne pole) | tak | tak |
| `step1` (getter na tej samej tablicy) | tak | tak |
| `step2` (widok przez `Proxy`) | nie | tak |
| `step3` (`Object.freeze([...])`) | nie | nie |

Wszystkie warianty dają ten sam wynik operacji kasy (`S11EquivalenceTest`), a różnią się kontraktem gettera (`S11CollectionContractTest`). Który wariant jest rozwiązaniem, zależy od klientów: widok, gdy ktoś potrzebuje bieżącego stanu, a migawka, gdy wynik idzie dalej (raport, e-mail, inny proces).

### Pułapki

- Niemodyfikowalność blokuje członkostwo, a nie wnętrze elementów. Tu `Seat` ma pola `readonly`, więc to wystarcza. Przy mutowalnych elementach nie wystarczy, a `Object.freeze` jest płytkie.
- `readonly Seat[]` to tylko typ. Bez `Proxy` albo `Object.freeze` klient z rzutowaniem (`as Seat[]`) albo kod JavaScript zmieni tablicę właściciela.
- Getter, który przy każdym wywołaniu tworzy nowy `Proxy` albo nową kopię, zwraca za każdym razem inny obiekt. Nie opieraj kontraktu na tożsamości (`booking.seats === booking.seats` to `false`).
- Konstruktor przyjmujący tablicę z zewnątrz bez kopii zachowuje alias do tablicy klienta.
- Zmiana na `Set` "przy okazji" zmienia zachowanie duplikatów, a przy obiektach `Seat` porównuje referencje, nie `equals`. Test ma przypadek z tym samym miejscem dwa razy.

### Pytanie do sali

Który kontrakt wybierzecie dla listy miejsc, którą czyta ekran sali w kasie, a który dla listy wysyłanej w e-mailu z potwierdzeniem?

## Scena s12. Encapsulate Conditional - czy przysługuje zwrot

**Temat ze slajdów:** Encapsulate Conditional
**Katalog:** `typescript/src/workshop/m4/s12_encapsulateconditional` · **Test:** `scripts/warsztat.sh --lang ts test m4/s12` (`S12EquivalenceTest.test.ts`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** Warunek zwrotu w `RefundCalculator.refund` pyta o status, czas i prefiks promocji zamiast o regułę z regulaminu. Zamykamy kolejne fragmenty w predykaty z nazwami domenowymi, od najmniejszego do warunku gałęzi.

**Zasada:** Encapsulate Conditional zamienia pytanie o implementację na pytanie o znaczenie, czyli wydziela predykat z nazwą z domeny. Krótkie spięcie jest zachowaniem, więc osłona `null` idzie razem z testem, a kolejność warunków się nie zmienia. Nazwa `is`/`has` nie gwarantuje, że predykat jest czysty.

**Efekt:** `refund` czyta się jak regulamin (`isRefundable`, `cancelledAtLeast24hBefore`), a granica "dokładnie 24 h" jest opisana testem. Wyrażeń celowo nie upraszczamy, bo prosta zamiana `!isAfter` na `isBefore` zgubiłaby ten przypadek.

**Różnica względem Javy:** `private static boolean` predykaty to nieeksportowane funkcje modułu obok klasy (`hasFreeTicketPromo`, `isRefundable`, `cancelledAtLeast24hBefore`), bo nie potrzebują stanu obiektu. `promo` ma typ `string | null`, więc wydzielenie testu prefiksu bez osłony `b.promo !== null &&` kończy się błędem kompilacji, a nie `NullPointerException`.

### Co widzimy

`RefundCalculator.refund` liczy zwrot: 100% przy anulowaniu co najmniej 24 h przed seansem, 50% później, 0 po starcie, potrącenie 3.00 (nie poniżej zera). Warunek pyta o implementację, a nie o regułę:

```typescript
if (b.status === 'PAID' && now.isBefore(b.screeningStart)
  && !(b.promo !== null && b.promo.startsWith('FREE'))) {
  if (!now.plusHours(24).isAfter(b.screeningStart)) {
    amount = b.tickets;
  } else {
    amount = b.tickets.percent(50);
  }
}
```

### Krok 1: Extract Method dla najmniejszego fragmentu

**W IDE:** zaznacz `b.promo !== null && b.promo.startsWith('FREE')` (bez negacji), ⌃⇧R, "Extract to function in module scope", nazwa `hasFreeTicketPromo`.
**Po:**

```typescript
if (b.status === 'PAID' && now.isBefore(b.screeningStart) && !hasFreeTicketPromo(b)) {

function hasFreeTicketPromo(b: Booking): boolean {
  return b.promo !== null && b.promo.startsWith('FREE');
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m4/s12` - 28 testów zielonych.
**Co powiedzieć:** osłona `!== null` idzie razem z testem prefiksu. Krótkie spięcie jest zachowaniem, co sprawdza przypadek z `promo = null`. TypeScript pilnuje tego za nas: bez osłony w tym samym wyrażeniu `b.promo.startsWith` się nie skompiluje.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s12 0 1`

### Krok 2: Encapsulate Conditional - cały warunek

**W IDE:** zaznacz cały warunek pierwszego `if`, ⌃⇧R, "Extract to function in module scope", nazwa `isRefundable`.
**Po:**

```typescript
if (isRefundable(b, now)) {

function isRefundable(b: Booking, now: LocalDateTime): boolean {
  return b.status === 'PAID' && now.isBefore(b.screeningStart) && !hasFreeTicketPromo(b);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `if` pyta teraz o regułę z regulaminu, a szczegóły (status, czas, promocja) mieszkają w jednym miejscu. Kolejność warunków w `&&` jest taka sama, więc wyjątki i krótkie spięcie też są te same.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s12 1 2`

### Krok 3: Encapsulate Conditional dla gałęzi

**W IDE:** zaznacz `!now.plusHours(24).isAfter(b.screeningStart)`, ⌃⇧R, "Extract to function in module scope", nazwa `cancelledAtLeast24hBefore`.
**Po:**

```typescript
if (isRefundable(b, now)) {
  if (cancelledAtLeast24hBefore(b, now)) {
    amount = b.tickets;
  } else {
    amount = b.tickets.percent(50);
  }
}
```

**Uruchom:** test zielony, także przypadek "dokładnie 24 h przed" (100%) i "24 h minus minuta" (50%).
**Co powiedzieć:** podwójne zaprzeczenie zamknęliśmy w nazwie. Granicę (`>=` 24 h) opisuje test, więc nikt jej nie "poprawi" przy okazji uproszczenia wyrażenia.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m4/s12 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator.ts`: metoda publiczna czyta się jak regulamin (czy przysługuje zwrot, czy anulowano co najmniej 24 h przed), a predykaty mają nazwy z domeny. Test obejmuje granice czasu, statusy, promocje, `null` i dolną granicę kwoty.

### Pułapki

- Uproszczenie `!now.plusHours(24).isAfter(start)` do `now.plusHours(24).isBefore(start)` gubi przypadek "dokładnie 24 h".
- Obliczenie prawej strony osłony wcześniej (np. `const free = b.promo!.startsWith('FREE')` przed sprawdzeniem `null`). Kompilator zaprotestuje, ale `!` albo `?.` to uciszą: `b.promo?.startsWith('FREE')` daje `undefined` zamiast `false`, a negacja `!undefined` to `true`, więc łatwo o odwrócenie znaczenia.
- Nazwa `is`/`has` nie gwarantuje czystości. Predykat z efektem ubocznym (log, licznik, zapytanie do bazy) po ekstrakcji może zostać wywołany w innym miejscu albo inną liczbę razy.
- Przeniesienie predykatu do `Booking` jako następny krok ma sens, ale wymaga zegara albo `now` jako parametru, bo sama rezerwacja nie zna bieżącej chwili.

### Pytanie do sali

Czy `isRefundable` powinno trafić do `Booking`? Co wtedy z parametrem `now`?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min):** zasada "najpierw test, potem ruch" i pułapki semantyki.

1. s00 Test charakterystyki (ruch A i krok 1, ~12 min)
2. s01 Rename, tylko krok 2 i 3 z naiwnym ruchem na żywo (~8 min)
3. s02 Extract Variable, krok 2 z osłoną `null` (~5 min)
4. s04 Extract Method (~15 min)
5. s05 Inline Variable, pokaz pułapki i krok 3 (~8 min)
6. s06 Inline Method, krok 2 (~5 min)
7. s07 Move Method, krok 2 (~6 min)
8. s09 Extract Class, kroki 1 i 2 ze "złym wynikiem" (~10 min)
9. s11 Encapsulate Collection, tabela kontraktów (~6 min)

**Ścieżka pełna (~145 min):** wszystkie sceny w kolejności numerów, s00-s12. Po s04 i po s09 zrób przerwę na pytania. Sceny s03, s08, s10 i s12 dobrze nadają się do zadania dla uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 4).
