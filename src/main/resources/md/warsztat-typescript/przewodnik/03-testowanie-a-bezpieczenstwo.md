# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Pięć scen w domenie kina pokazuje na żywo, jak zbudować sieć bezpieczeństwa wokół kodu, który jej nie ma: test charakteryzujący przed pierwszą zmianą, szew (ang. seam) w klasie, której nie da się uruchomić w teście, wektor obserwowalnego zachowania, weryfikację w cieniu i jej granice przy efektach ubocznych. Każda scena ma kod wyjściowy (`start`), kompletne snapshoty po każdym kroku (`stepN`) i test (vitest), który po każdym ruchu w VS Code ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s03 krok 2, s04 krok 4), test ma osobne oczekiwania i pokazuje różnicę wprost.

Punktem odniesienia dla całego modułu jest wynik wzorcowy (ang. golden master) starego systemu: `CinemaManagerGoldenMasterTest` (`typescript/test/workshop/legacy/CinemaManagerGoldenMasterTest.test.ts`) odtwarza "jeden dzień kina" na nietkniętym `typescript/src/workshop/legacy/CinemaManager.ts` i porównuje pełny wektor zachowania (wyniki wywołań, maile, SMS-y, obciążenia i zwroty, raport dzienny, rozliczenia) z zatwierdzonym plikiem `src/test/resources/workshop/cinema-manager.approved.txt`, wspólnym dla Javy, C# i TypeScriptu. To odpowiedź na pytanie "jak poradzić sobie bez testów?" w skali całego systemu: jeden szeroki, deterministyczny test, zanim powstanie jakikolwiek test jednostkowy. Sceny poniżej pokazują te same techniki w mniejszej skali. Pokaż golden master na początku modułu (3 min) i wracaj do niego przy s01 (charakterystyka) i s03 (wektor zachowania).

Sceny ilustrują sekcję 3 slajdów ("Siatka bezpieczeństwa") i służą do pokazu; uczestnicy powtarzają je samodzielnie według zakładki "Zadania".

Port TypeScript: kod scen leży w `typescript/src/workshop/m3/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m3/sNN_.../`, a pokaz prowadzimy w VS Code. Wspólne dla całego modułu: rekordy Javy to klasy z polami `readonly`, `@FunctionalInterface` to alias typu funkcyjnego (w teście wystarczy funkcja strzałkowa), drugi konstruktor Javy (`this(...)`) zastępują parametry domyślne jedynego konstruktora, `Clock` pochodzi z `typescript/src/workshop/shared/time.ts`, `sealed interface` z rekordami to unia klas z polem `kind` i `assertNever` w `switch`, a w oczekiwanych tekstach testów zamiast nazw wyjątków Javy stoją nazwy klas błędów portu (`IllegalArgumentError`, `IllegalStateError`, `NullPointerError` z `typescript/src/shared/errors.ts`). Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
(cd typescript && npx vitest run test/workshop/legacy)   # golden master starego systemu
scripts/warsztat.sh --lang ts list m3              # sceny modułu i ich kroki
scripts/warsztat.sh --lang ts test m3/s02          # testy jednej sceny
scripts/warsztat.sh --lang ts test m3              # wszystkie sceny modułu (107 testów)
scripts/warsztat.sh --lang ts diff m3/s04 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh --lang ts diff m3/s04 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m3/s04 2        # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh --lang ts next m3/s04          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m3/s04         # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w VS Code, test sceny (skrypt albo rozszerzenie Vitest), zdanie z sekcji "Co powiedzieć". Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m3/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu `start`. Snapshoty `stepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd 1.2 "Zachowanie obserwowalne to więcej niż wynik metody" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?*
- W s02 start **nie ma prawdziwych testów** i to jest puenta: test dla `start` (`startCannotEvenRunInATest`) tylko dokumentuje, że klasy nie da się uruchomić, a testy zachowania pojawiają się dopiero wtedy, gdy kod ma szew. Pierwsze ruchy to mechaniczne ruchy o niskim ryzyku (slajd 3.6).
- W s01 i s03 warto najpierw **zrobić naiwny ruch na żywo** (np. "poprawić" znalezisko z charakterystyki, sprawdzić tylko wynik), pokazać, czego test nie widzi, cofnąć zmianę i dopiero wtedy wykonać krok z przewodnika.
- W s04 i s05 część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych. Testy, które **dokumentują stan startowy** (np. "start obciąża kartę dwa razy"), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh --lang ts reset m3/sNN`.
- Kroki, które zmieniają zachowanie (s03 krok 2, s04 krok 4), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów, więc tam, gdzie krok zmienia sygnaturę konstruktora albo usuwa plik (s02, s03, s05), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- Refaktoryzacje w VS Code: edytor sam wydzieli zaznaczony fragment do metody w klasie, funkcji albo stałej, wpisze zmienną z powrotem w miejsca użycia, zmieni nazwę symbolu w całym projekcie, podpowie brakujące importy i szkielet implementacji interfejsu oraz przeniesie deklaracje najwyższego poziomu do innego pliku. Nie wydzieli interfejsu z klasy, nie zmieni sygnatury metody, nie doda parametru do istniejącego konstruktora, nie wpisze metody w miejsca wywołań ani nie usunie pliku ze sprawdzeniem użyć - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` albo lista wszystkich odwołań do symbolu.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
|---|---|---|---|---|
| s01 | 3.1. Test charakteryzujący: zapisz, co kod robi dziś; 3.2. Wynik wzorcowy i dobór przypadków; 3.3. Test równoważności przed zmianą i po niej | 2 | `m3/s01_characterization` | ~15 min |
| s02 | 3.4. Szew: miejsce, w którym podstawiamy zależność; 3.5. Rozrywanie zależności: po zmianie; 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą | 4 | `m3/s02_breakdependencies` | ~20 min |
| s03 | 1.2. Zachowanie obserwowalne to więcej niż wynik metody; 3.2. Wynik wzorcowy i dobór przypadków | 3 | `m3/s03_behaviourvector` | ~12 min |
| s04 | 3.8. Weryfikacja w cieniu | 4 | `m3/s04_parallelrun` | ~15 min |
| s05 | 3.9. Granice weryfikacji w cieniu: efekty uboczne | 3 | `m3/s05_shadowlimits` | ~12 min |

Katalogi są względne wobec `typescript/src/workshop/`, testy leżą w odpowiednim katalogu pod `typescript/test/workshop/`.

## Scena s01. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** 3.1. Test charakteryzujący: zapisz, co kod robi dziś; 3.2. Wynik wzorcowy i dobór przypadków; 3.3. Test równoważności przed zmianą i po niej
**Katalog:** `typescript/src/workshop/m3/s01_characterization` · **Test:** `scripts/warsztat.sh --lang ts test m3/s01` (`S01CharacterizationTest.test.ts`)
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

W tej scenie najważniejszy jest test `S01CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `start/step1/step2` tylko za nim nadąża.

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

**Zmiany:**

1. W `BookingConfirmation` dodaj konstruktor z parametrem domyślnym: `constructor(private readonly clock: Clock = systemClock) {}` (Parameterize Constructor). Dopisz do importu z `../../../shared/time.js` typ `Clock` i `systemClock`.
2. Uruchom test - nic się nie zmieniło, bo nikt jeszcze nie czyta nowego pola.
3. W `confirm` zamień `LocalDateTime.now()` na `LocalDateTime.now(this.clock)`.

**Przed:**

```typescript
import { LocalDateTime } from '../../../shared/time.js';
...
export class BookingConfirmation {
  confirm(b: Booking): string {
    ...
      + 'Wygenerowano: ' + LocalDateTime.now().toString() + '\n';
  }
}
```

**Po:**

```typescript
import { type Clock, LocalDateTime, systemClock } from '../../../shared/time.js';
...
export class BookingConfirmation {
  constructor(private readonly clock: Clock = systemClock) {}
  ...
      + 'Wygenerowano: ' + LocalDateTime.now(this.clock).toString() + '\n';
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s01` - 15 testów zielonych, w tym `fromStep1TheWholeDocumentIsDeterministic` (z `fixedClock(...)` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Wywołanie bez argumentu zostaje (parametr domyślny to zegar systemowy), więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `start`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 0 1`

### Krok 2: pierwsza refaktoryzacja pod ochroną testu

**Zmiany:**

1. Zaznacz ciało pętli od `let p: number;` do końca `if` z porankiem (`p = p - 5.00; }`) i wydziel je do nowej metody prywatnej `ticketPrice` w klasie `BookingConfirmation` (Extract Method). Metoda ma dwa wejścia (`b`, `t`) i jedno wyjście (`p`), więc w pętli zostaje zmienna z wynikiem metody.
2. Wpisz tę zmienną z powrotem w miejsce użycia i usuń jej deklarację (Inline Variable). W pętli zostaje `sum = sum + this.ticketPrice(b, t);`.

**Przed:**

```typescript
for (const t of b.ticketTypes) {
  let p: number;
  if (b.format === 3) {
    p = 40.00;
  } else if (b.format === 2) {
    p = 32.00;
  } else {
    p = 25.00;
  }
  if (t === 'S') {
    p = p * 0.75;
  } else if (t === 'E') {
    p = p * 0.70;
  } else if (t === 'C') {
    p = p * 0.60;
  }
  if (b.start.hour < 12) {
    p = p - 5.00;
  }
  sum = sum + p;
}
```

**Po:**

```typescript
for (const t of b.ticketTypes) {
  sum = sum + this.ticketPrice(b, t);
}
```

**Uruchom:** test zielony - ten sam dokument, znak w znak.
**Co powiedzieć:** kusi, żeby przy okazji zamienić `number` na `Decimal`, podać jawne locale i poprawić próg grupy. Każda z tych rzeczy zmienia wydruk, więc to trzy osobne decyzje z wymaganiem, a nie refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s01 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `start`, `step1` i `step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, locale albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj locale ustawiamy na czas wywołania i przywracamy w `finally`. Podmiana `Number.prototype.toLocaleString` bez `finally` rozlałaby się na inne testy w tym samym procesie.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s02. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 3.4. Szew: miejsce, w którym podstawiamy zależność; 3.5. Rozrywanie zależności: po zmianie; 3.6. Refaktoryzacje automatyczne w IDE: co dają, a czego nie widzą
**Katalog:** `typescript/src/workshop/m3/s02_breakdependencies` · **Test:** `scripts/warsztat.sh --lang ts test m3/s02` (`S02SeamTest.test.ts`)
**Czas:** ~20 min

### W skrócie

**Co robimy:** `ShowtimeReminderJob` sam tworzy bazę, czyta zegar systemowy i woła statyczny mailer, więc nie da się go uruchomić w teście. Kolejnymi małymi ruchami wyciągamy trzy zależności do konstruktora, aż test może podstawić fałszywą bazę, stały zegar i lambdę zamiast maila.

**Zasada:** Break Dependencies tworzy seam - miejsce, w którym można podstawić inną implementację bez edycji algorytmu, żeby uruchomić kod bez kosztownego otoczenia i obserwować jego komunikację. Wybieramy najwęższy skuteczny seam: parametr, zależność konstruktora, `Clock` albo typ funkcyjny, a nie interfejs przed każdą klasą. To nie jest zmiana reguły biznesowej, tylko zmiana miejsca tworzenia zależności.

**Efekt:** Klasa ma pierwszy prawdziwy test z granicą 120/121 minut, a produkcyjny konstruktor (parametry domyślne) składa te same implementacje co wcześniej, łącznie z bazą otwieraną przy każdym `run()`. Kosztem są trzy nowe małe typy i konstruktor z trzema parametrami.

**Różnica względem Javy:** `Supplier<BookingStore>` to typ funkcyjny `() => BookingStore`, `Clock` pochodzi z `workshop/shared/time.ts` (w teście `fixedClock(NOW)`), a `@FunctionalInterface ReminderSender` to alias typu funkcyjnego. Drugi konstruktor Javy (`this(...)`) zastępują parametry domyślne jedynego konstruktora. Klasy TS nie są `final`, więc ceną seamu z kroku 3 jest metoda `protected` (punkt rozszerzenia przez dziedziczenie), która w kroku 4 znika. Test ma jeden `FakeStore` dla wszystkich kroków - interfejsy `BookingStore` z kroków mają ten sam kształt, a TypeScript typuje strukturalnie.

### Co widzimy

`ShowtimeReminderJob.run` wysyła przypomnienia na 2 godziny przed seansem. Wszystkie zależności są zaszyte w środku: produkcyjna baza tworzona przez `new`, bieżący czas i statyczny mailer. Konstruktor `LegacyDatabase` rzuca `IllegalStateError` poza serwerownią, a `ReminderMailer.send` - poza produkcją. **Tej klasy nie da się uruchomić w teście** - test sceny dla `start` (`startCannotEvenRunInATest`) tylko to dokumentuje.

```typescript
run(): number {
  const database = new LegacyDatabase();
  const now = LocalDateTime.now();
  ...
      ReminderMailer.send(booking.email,
        `Przypomnienie: ${booking.title}`, ...);
      database.markReminded(booking.id);
```

Sekwencja rozrywania zależności: nazwij zachowanie i punkt obserwacji, wprowadź najmniejszy szew bez zmiany logiki, zabezpiecz go testem z fake/spy, przenieś wybór do jawnego punktu aktywacji, usuń konstrukcję przejściową. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i małe (slajd 3.6). VS Code nie ma tu wszystkich automatów, więc część ruchów robimy ręcznie, a kompilator wskazuje miejsca do poprawy.

### Krok 1: Extract Interface + Parameterize Constructor

**Zmiany:**

1. Utwórz nowy plik `BookingStore.ts` z interfejsem `BookingStore` o dwóch metodach: `paidBookings(): readonly PaidBooking[]` i `markReminded(bookingId: string): void` (Extract Interface - ręcznie, bo VS Code nie wydziela interfejsu z klasy).
2. W deklaracji klasy `LegacyDatabase` dopisz `implements BookingStore`.
3. W `ShowtimeReminderJob` dodaj pole `private readonly stores: () => BookingStore;` i konstruktor z parametrem o wartości domyślnej `() => new LegacyDatabase()`, przypisywanym przez `requireNonNull(stores, 'stores')` (Parameterize Constructor).
4. W `run()` zamień `new LegacyDatabase()` na `this.stores()`.

**Przed:**

```typescript
export class ShowtimeReminderJob {
  run(): number {
    const database = new LegacyDatabase();
    const now = LocalDateTime.now();
```

**Po:**

```typescript
constructor(stores: () => BookingStore = () => new LegacyDatabase()) {
  this.stores = requireNonNull(stores, 'stores');
}

run(): number {
  const database = this.stores();
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s02` - testy `step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania.
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `run()`. Gdybyśmy dali domyślnie `stores: BookingStore = new LegacyDatabase()`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 0 1`

### Krok 2: Clock jako zależność

**Zmiany:**

1. W `ShowtimeReminderJob` dodaj pole `private readonly clock: Clock;` i drugi parametr konstruktora `clock: Clock = systemClock`, przypisywany przez `requireNonNull(clock, 'clock')` (Parameterize Constructor - ręcznie, bo VS Code nie dodaje parametru do istniejącego konstruktora).
2. W `run()` zamień `LocalDateTime.now()` na `LocalDateTime.now(this.clock)`.

**Przed:**

```typescript
constructor(stores: () => BookingStore = () => new LegacyDatabase()) {
  this.stores = requireNonNull(stores, 'stores');
}

run(): number {
  const database = this.stores();
  const now = LocalDateTime.now();
```

**Po:**

```typescript
constructor(stores: () => BookingStore = () => new LegacyDatabase(), clock: Clock = systemClock) {
  this.stores = requireNonNull(stores, 'stores');
  this.clock = requireNonNull(clock, 'clock');
}

run(): number {
  const database = this.stores();
  const now = LocalDateTime.now(this.clock);
```

**Uruchom:** test zielony. Test `step2ControlsTimeButStillHitsStaticMailer` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 1 2`

### Krok 3: Subclass and Override Method i pierwszy test

**Zmiany:**

1. Zaznacz w `run()` wywołanie `ReminderMailer.send(...)` i wydziel je do nowej metody `sendReminder` w klasie `ShowtimeReminderJob` (Extract Method). Wydzielona metoda powstaje jako `private`.
2. Zmień ręcznie widoczność metody `sendReminder` z `private` na `protected`.
3. W teście anonimowa podklasa (`new (class extends step3.ShowtimeReminderJob { ... })(...)`) nadpisuje `sendReminder` i zapisuje wiadomości do tablicy - to jest **pierwszy prawdziwy test** tej klasy (Subclass and Override Method).

**Przed:**

```typescript
if (!booking.reminded && minutes > 0 && minutes <= 120) {
  ReminderMailer.send(booking.email,
    `Przypomnienie: ${booking.title}`,
    `Seans zaczyna sie o ${booking.start.toLocalTime().toString()}`);
  database.markReminded(booking.id);
```

**Po:**

```typescript
protected sendReminder(to: string, subject: string, body: string): void {
  ReminderMailer.send(to, subject, body);
}
```

**Uruchom:** test zielony: 3 przypadki z granicą 120/121 minut, już przypomniane i już rozpoczęte.
**Co powiedzieć:** to najtańszy seam, gdy nie możemy jeszcze zmienić konstruktora - ale ma cenę: klasa ma teraz punkt rozszerzenia przez dziedziczenie (metodę `protected`), a test zależy od szczegółu implementacji (nazwy metody chronionej).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 2 3`

### Krok 4: seam z dziedziczenia na typ funkcyjny

**Zmiany:**

1. Utwórz nowy plik `ReminderSender.ts` z aliasem typu funkcyjnego: `export type ReminderSender = (to: string, subject: string, body: string) => void;`.
2. W `ShowtimeReminderJob` dodaj pole `private readonly sender: ReminderSender;` i trzeci parametr konstruktora z wartością domyślną `(to, subject, body) => ReminderMailer.send(to, subject, body)`, przypisywany przez `requireNonNull(sender, 'sender')`.
3. W `run()` zamień `this.sendReminder(...)` na `this.sender(...)` i usuń metodę `protected sendReminder` (Inline Method - ręcznie, bo VS Code tego nie automatyzuje).
4. W teście podklasę z kroku 3 zastępuje funkcja strzałkowa przekazana jako `sender`.

**Przed:**

```typescript
this.sendReminder(booking.email,
  `Przypomnienie: ${booking.title}`,
  `Seans zaczyna sie o ${booking.start.toLocalTime().toString()}`);
...
protected sendReminder(to: string, subject: string, body: string): void {
  ReminderMailer.send(to, subject, body);
}
```

**Po:**

```typescript
constructor(
  stores: () => BookingStore = () => new LegacyDatabase(),
  clock: Clock = systemClock,
  sender: ReminderSender = (to, subject, body) => ReminderMailer.send(to, subject, body),
) {
```

**Uruchom:** test zielony - te same 3 przypadki dla `step3` (podklasa) i `step4` (lambda).
**Co powiedzieć:** pod ochroną testu z kroku 3 wymieniliśmy seam na najwęższy możliwy. Produkcja (parametry domyślne) składa dokładnie te same implementacje co wcześniej - zmieniło się **miejsce tworzenia zależności**, nie reguła.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s02 3 4`

### Rozwiązanie i uzasadnienie

`step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`BookingStore` z dwiema metodami, `Clock`, `ReminderSender` z jedną sygnaturą). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `stores: BookingStore = new LegacyDatabase()` jako parametr domyślny: zmiana czasu życia i momentu wyjątku (test `step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- Seam typowany klasą `LegacyDatabase` zamiast interfejsu - dopóki klasa ma tylko metody publiczne, fake pasuje strukturalnie, ale pierwsze pole `private` albo `#private` robi z typu klasy typ nominalny i test nie podstawi już własnej implementacji.
- Zostawienie Subclass and Override na stałe: metoda `protected` i klasa otwarta do dziedziczenia tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s03. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** 1.2. Zachowanie obserwowalne to więcej niż wynik metody; 3.2. Wynik wzorcowy i dobór przypadków
**Katalog:** `typescript/src/workshop/m3/s03_behaviourvector` · **Test:** `scripts/warsztat.sh --lang ts test m3/s03` (`S03ResultOnlyTest.test.ts`, `S03BehaviourVectorTest.test.ts`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Po "porządkach" kolegi `TicketCheckout.pay` wysyła mail z potwierdzeniem także klientowi z odrzuconą kartą, a test sprawdzający tylko wynik tego nie widzi. Wprowadzamy seam dla maili, naprawiamy regresję i dodajemy seam dla płatności, żeby test widział pełny wektor.

**Zasada:** Wektor obserwowalnego zachowania to wszystko, co klient może zauważyć: wynik, wyjątki (typ, komunikat, moment), stan po sukcesie i po błędzie, wywołania współpracowników z ich kolejnością, czas i granice. Test sprawdzający jeden wymiar przepuści regresję w pozostałych, dlatego granica kontraktu ma być świadomą decyzją, a nie skutkiem słabego testu.

**Efekt:** Test widzi wynik, wyjątek, maile i obciążenia karty w jednym dzienniku oraz status rezerwacji. Zachowanie świadomie się zmienia w kroku 2: przy odrzuconej karcie znika błędny mail o opłaceniu - to naprawa błędu, a nie refaktoryzacja, choć wynik metody pozostaje ten sam.

**Różnica względem Javy:** `@FunctionalInterface Mailer` i `PaymentGateway` to aliasy typów funkcyjnych, więc w teście wystarczy funkcja strzałkowa (albo pole-strzałka `MailLog.add`). Konstruktory z domyślnymi zależnościami (`this(CinemaMailer::send)`) to parametry domyślne. `Objects.requireNonNull(card, "card")` to `requireNonNull(card, 'card')` rzucające `NullPointerError`, więc w pełnym wektorze jest `wyjatek=NullPointerError: card`. `Booking.status()` to getter `status` bez settera - stan zmienia tylko `markPaid()`.

### Co widzimy

`TicketCheckout.pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S03ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

```typescript
const charged = CardTerminal.charge(cardNumber, booking.amount);
if (charged) {
  booking.markPaid();
} else {
  CinemaMailer.send(booking.email, `Platnosc odrzucona ${booking.id}`);
}
CinemaMailer.send(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
return charged ? 'OK' : 'DECLINED';
```

Pokaż tabelę ze slajdu 1.2: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**Zmiany:**

1. Utwórz nowy plik `Mailer.ts` z aliasem typu funkcyjnego: `export type Mailer = (to: string, text: string) => void;`.
2. W `TicketCheckout` dodaj pole `private readonly mailer: Mailer;` i konstruktor z parametrem o wartości domyślnej `(to, text) => CinemaMailer.send(to, text)`, przypisywanym przez `requireNonNull(mailer, 'mailer')` (Parameterize Constructor).
3. W `pay` zamień oba statyczne wywołania `CinemaMailer.send(...)` na `this.mailer(...)`. Logika bez zmian.

**Przed:**

```typescript
export class TicketCheckout {
  pay(booking: Booking, card: string | null): string {
    ...
    } else {
      CinemaMailer.send(booking.email, `Platnosc odrzucona ${booking.id}`);
    }
    CinemaMailer.send(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
```

**Po:**

```typescript
constructor(mailer: Mailer = (to, text) => CinemaMailer.send(to, text)) {
  this.mailer = requireNonNull(mailer, 'mailer');
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m3/s03` - 23 testy zielone. `step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona B1" i "Bilety B1 oplacone: 114.00".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s03 0 1`

### Krok 2: naprawa regresji pod ochroną testu

**Zmiany:**

1. Przenieś wysyłkę maila potwierdzającego do gałęzi sukcesu, zaraz za `booking.markPaid()`.
2. Zamień końcowe `return charged ? 'OK' : 'DECLINED';` na dwa osobne `return`: `'OK'` w gałęzi sukcesu, `'DECLINED'` po mailu o odrzuceniu. Zmienna `charged` znika, a wynik `CardTerminal.charge(...)` trafia wprost do `if`.

**Przed:**

```typescript
const charged = CardTerminal.charge(cardNumber, booking.amount);
if (charged) {
  booking.markPaid();
} else {
  this.mailer(booking.email, `Platnosc odrzucona ${booking.id}`);
}
this.mailer(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
return charged ? 'OK' : 'DECLINED';
```

**Po:**

```typescript
if (CardTerminal.charge(cardNumber, booking.amount)) {
  booking.markPaid();
  this.mailer(booking.email, `Bilety ${booking.id} oplacone: ${booking.amount.toString()}`);
  return 'OK';
}
this.mailer(booking.email, `Platnosc odrzucona ${booking.id}`);
return 'DECLINED';
```

**Uruchom:** test zielony: `fromStep2MailsAreCorrect`.
**Co powiedzieć:** to naprawa błędu, nie refaktoryzacja - przywracamy kontrakt sprzed "porządków". Wynik metody się nie zmienia, zmienia się efekt uboczny.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s03 1 2`

### Krok 3: seam dla płatności i pełny wektor

**Zmiany:**

1. Utwórz nowy plik `PaymentGateway.ts` z aliasem typu funkcyjnego: `export type PaymentGateway = (card: string, amount: Money) => boolean;`.
2. W `TicketCheckout` dodaj pole `private readonly gateway: PaymentGateway;` i drugi parametr konstruktora z wartością domyślną `(card, amount) => CardTerminal.charge(card, amount)`, przypisywany przez `requireNonNull(gateway, 'gateway')`.
3. W `pay` zamień `CardTerminal.charge(cardNumber, booking.amount)` na `this.gateway(cardNumber, booking.amount)`.
4. W teście oba fałszywe obiekty zapisują do **jednej** tablicy zdarzeń.

**Przed:**

```typescript
constructor(mailer: Mailer = (to, text) => CinemaMailer.send(to, text)) {
  this.mailer = requireNonNull(mailer, 'mailer');
}
...
if (CardTerminal.charge(cardNumber, booking.amount)) {
```

**Po:**

```typescript
constructor(
  mailer: Mailer = (to, text) => CinemaMailer.send(to, text),
  gateway: PaymentGateway = (card, amount) => CardTerminal.charge(card, amount),
) {
```

**Uruchom:** test zielony: `step3ObservesTheFullVector` sprawdza wynik albo wyjątek (nazwa klasy błędu i komunikat), obciążenia i maile w jednej kolejności oraz status rezerwacji po operacji.
**Co powiedzieć:** jeden dziennik dla wszystkich współpracowników pokazuje kolejność: najpierw obciążenie, potem mail. Stan po błędzie (`NEW` po odrzuceniu) i brak efektów przy wyjątku (`wyjatek=NullPointerError: card; zdarzenia=[]`) też są częścią kontraktu.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/TicketCheckout` z dwoma seamami i test, który obserwuje pięć wymiarów wektora: wynik, wyjątek, efekty, kolejność efektów i stan. Granica kontraktu jest świadomą decyzją, a nie skutkiem zbyt słabego testu.

### Pułapki

- Test "zielony, bo nic nie sprawdza" - pokrycie linii 100%, a efekty uboczne niewidoczne.
- Osobne tablice dla maili i obciążeń - kolejność między współpracownikami ginie.
- Sprawdzanie komunikatu wyjątku bez sprawdzenia, że nie było efektów przed wyjątkiem.
- Metoda przekazana jako seam bez wiązania `this` (`new TicketCheckout(log.add)`, gdy `add` jest zwykłą metodą) - `this` jest wtedy `undefined` i test pada z innego powodu. Dlatego `MailLog.add` w teście to pole-strzałka.
- Logi: jeśli ktoś je parsuje (alerty, audyt), też należą do wektora - wtedy potrzebny kolejny seam.

### Pytanie do sali

Które elementy wektora sprawdzają testy w Waszym najważniejszym module? Czego nie widzą?

## Scena s04. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 3.8. Weryfikacja w cieniu
**Katalog:** `typescript/src/workshop/m3/s04_parallelrun` · **Test:** `scripts/warsztat.sh --lang ts test m3/s04` (`S04EquivalenceTest.test.ts`, `S04SolutionTest.test.ts`)
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

**Zmiany:**

1. W `PriceService` dodaj pole `private readonly candidate = new CandidatePriceCalculator();` i licznik `private mismatchCount = 0;`.
2. W `price` zapisz wynik legacy do zmiennej `result`, a po nim wywołaj kandydata w bloku `try/catch`: gdy wynik kandydata różni się od `result` albo kandydat rzuci błąd, zwiększ `mismatchCount`. Zwracaj zawsze `result`.
3. Dodaj metodę `mismatches()` zwracającą licznik.

**Przed:**

```typescript
export class PriceService {
  private readonly legacy = new LegacyPriceCalculator();

  price(query: TicketQuery): Money {
    return this.legacy.price(query);
  }
}
```

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
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 0 1`

### Krok 2: raport rozbieżności

**Zmiany:**

1. Utwórz nowy plik `VerificationReport.ts` z klasami `Agreement`, `Divergence`, `CandidateFailure` (każda z polem `kind`), unią `export type Verification = Agreement | Divergence | CandidateFailure;` i klasą `VerificationReport` z metodami `record`, `entries` i `problems()` (w `problems` rozróżnienie wpisów po `kind` w `switch` z `assertNever`).
2. W `PriceService` usuń licznik `mismatchCount` i metodę `mismatches()`; dodaj konstruktor `constructor(private readonly report: VerificationReport = new VerificationReport()) {}`.
3. Wydziel porównanie z `price` do prywatnej metody `verify(query, legacyPrice)` (Extract Method), która zwraca `Agreement` albo `Divergence`, a w `catch` buduje `CandidateFailure` z opisem `error.name + ': ' + error.message`.
4. W `price` po wyliczeniu wyniku legacy wywołaj `this.report.record(this.verify(query, result));` i zwróć wynik legacy.

**Przed:**

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

**Po:**

```typescript
return legacyPrice.equals(candidatePrice)
  ? new Agreement(query, legacyPrice)
  : new Divergence(query, legacyPrice, candidatePrice);
```

**Uruchom:** test zielony; `step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX (`IllegalArgumentError: Nieznany format: 4DX`).
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 1 2`

### Krok 3: poprawka znaleziona przez raport

**Zmiany:**

1. W `CandidatePriceCalculator.price` przenieś blok rabatu porannego (`if (query.start.hour < 12) { ... }`) za wyliczenie zniżki procentowej.
2. Rabat poranny odejmuj teraz od `price`, a nie od `base`. Zmienna `base` nie jest już modyfikowana, więc zamień `let base` na `const base`.

**Przed:**

```typescript
let base = CandidatePriceCalculator.basePrice(query.format);
if (query.start.hour < 12) {
  base = base.minus(CandidatePriceCalculator.MORNING_DISCOUNT);
}
let price = base.minus(base.percent(CandidatePriceCalculator.discountPercent(query.type)));
```

**Po:**

```typescript
const base = CandidatePriceCalculator.basePrice(query.format);
let price = base.minus(base.percent(CandidatePriceCalculator.discountPercent(query.type)));
if (query.start.hour < 12) {
  price = price.minus(CandidatePriceCalculator.MORNING_DISCOUNT);
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 2 3`

### Krok 4: przełączenie

**Zmiany:**

1. Utwórz nowy plik `MigrationMode.ts` z wyliczeniem `MigrationMode` o wartościach `LEGACY`, `SHADOW`, `CANDIDATE`.
2. W `PriceService` dodaj pierwszy parametr konstruktora `private readonly mode: MigrationMode = MigrationMode.SHADOW` (przed `report`).
3. Przenieś dotychczasową treść `price` i `verify` do prywatnej metody `shadow(query)`: liczy wynik legacy, w `try/catch` porównuje go z kandydatem, zapisuje do raportu `Agreement`, `Divergence` albo `CandidateFailure` i zwraca wynik legacy.
4. W `price` zostaje `switch (this.mode)` z trzema gałęziami (`LEGACY`, `SHADOW`, `CANDIDATE`) i `default: return assertNever(this.mode);`.

**Przed:**

```typescript
price(query: TicketQuery): Money {
  const result = this.legacy.price(query);
  this.report.record(this.verify(query, result));
  return result;
}
```

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
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s04 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - błąd w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na CANDIDATE? Kto o tym decyduje?

## Scena s05. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 3.9. Granice weryfikacji w cieniu: efekty uboczne
**Katalog:** `typescript/src/workshop/m3/s05_shadowlimits` · **Test:** `scripts/warsztat.sh --lang ts test m3/s05` (`S05EquivalenceTest.test.ts`, `S05SolutionTest.test.ts`)
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

**Zmiany:**

1. Utwórz nowy plik `Effects.ts` z interfejsem `Effects` zawierającym trzy operacje `Infrastructure` używane przez nowy przepływ: `sendMail`, `charge`, `save` (Extract Interface - ręcznie, bo VS Code nie wydziela interfejsu z klasy).
2. Utwórz nowy plik `RealEffects.ts` z adapterem `RealEffects implements Effects`, który każdą operację deleguje do przekazanej w konstruktorze `Infrastructure`.
3. W `NewBookingFlow` zmień ręcznie typ parametru konstruktora z `Infrastructure` na `Effects` (pole `effects`) i zamień wywołania `this.infra.*` na `this.effects.*`; miejsca wywołań do poprawy wskaże `npm run typecheck`.
4. W konstruktorze `ShadowBooking` przekazuj kandydatowi adapter: `new NewBookingFlow(new RealEffects(infra))` (Parameterize Constructor).

**Przed:**

```typescript
// NewBookingFlow.ts
constructor(private readonly infra: Infrastructure) {}

book(request: BookingRequest): string {
  ...
  this.infra.charge(request.card, total);

// ShadowBooking.ts
this.candidate = new NewBookingFlow(infra);
```

**Po:**

```typescript
// NewBookingFlow.ts
constructor(private readonly effects: Effects) {}

book(request: BookingRequest): string {
  ...
  this.effects.charge(request.card, total);

// ShadowBooking.ts
this.candidate = new NewBookingFlow(new RealEffects(infra));
```

**Uruchom:** test zielony; `step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s05 0 1`

### Krok 2: przechwycenie efektów w cieniu

**Zmiany:**

1. Utwórz nowy plik `RecordingEffects.ts` z klasą `RecordingEffects implements Effects`: zapisuje zamiary w formacie dziennika infrastruktury (`MAIL ...`, `CHARGE ...`, `SAVE ...`), niczego nie wykonuje i udostępnia wpisy metodą `recorded()`.
2. W `ShadowBooking` usuń pole `candidate` i jego tworzenie w konstruktorze; zapamiętaj `infra` jako pole. W `book` przed wywołaniem legacy zapisz długość dziennika (`before`), a po nim wytnij efekty legacy: `this.infra.log().slice(before)`.
3. W bloku `try` utwórz nagrywarkę, przekaż ją kandydatowi (`new NewBookingFlow(recorder).book(request)`) i porównaj osobno wynik i efekty prywatną metodą `compare`, która dopisuje różnice do listy rozbieżności.

**Przed:**

```typescript
constructor(infra: Infrastructure) {
  this.legacy = new LegacyBookingFlow(infra);
  this.candidate = new NewBookingFlow(new RealEffects(infra));
}

book(request: BookingRequest): string {
  const result = this.legacy.book(request);
  try {
    const shadow = this.candidate.book(request);
    if (shadow !== result) {
      this.divergenceList.push('legacy: ' + result + ' | kandydat: ' + shadow);
    }
```

**Po:**

```typescript
const recorder = new RecordingEffects();
const shadow = new NewBookingFlow(recorder).book(request);
this.compare('wynik', [result], [shadow]);
this.compare('efekty', legacyEffects, recorder.recorded());
```

**Uruchom:** test zielony; `step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s05 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**Zmiany:**

1. Utwórz nowy plik `BookingPlan.ts` z klasami `Charge`, `Save`, `SendMail` (pole `kind`, metody `describe()` i `applyTo(port)`), unią `export type Effect = Charge | Save | SendMail;` i klasą `BookingPlan` (pole `result` i zamrożona lista `effects`).
2. Utwórz nowy plik `BookingPlanner.ts` i przenieś do niego z `NewBookingFlow.book` czyste obliczenie jako metodę `plan(request)`, która zwraca `new BookingPlan('OK ' + total.toString(), [new Charge(...), new Save(...), new SendMail(...)])` (Separate Query from Modifier). Klasa nie dostaje żadnego portu efektów.
3. W `NewBookingFlow.book` zostaje: policz plan (`this.planner.plan(request)`), wykonaj efekty na porcie (`plan.effects.forEach((effect) => effect.applyTo(this.effects))`) i zwróć `plan.result`.
4. W `ShadowBooking` zamień kandydata na pole `private readonly candidate = new BookingPlanner();`; w `book` porównuj `plan.result` z wynikiem legacy, a opisy efektów (`plan.effects.map((effect) => effect.describe())`) z efektami legacy.
5. Usuń plik `RecordingEffects.ts` - po tym kroku nikt go nie używa, co potwierdza `npm run typecheck` i brak odwołań do klasy.

**Przed:**

```typescript
// ShadowBooking.ts (step2)
const recorder = new RecordingEffects();
const shadow = new NewBookingFlow(recorder).book(request);
this.compare('wynik', [result], [shadow]);
this.compare('efekty', legacyEffects, recorder.recorded());

// RecordingEffects.ts (plik usuwany w tym kroku)
export class RecordingEffects implements Effects {
  private readonly recordedList: string[] = [];
  ...
}
```

**Po:**

```typescript
const plan = this.candidate.plan(request);
this.compare('wynik', [result], [plan.result]);
const planned = plan.effects.map((effect) => effect.describe());
this.compare('efekty', legacyEffects, planned);
```

**Uruchom:** test zielony; `step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m3/s05 2 3`

### Rozwiązanie i uzasadnienie

Podwójne wykonanie tylko dla czystych obliczeń. Efekty (płatność, mail, zapis, zdarzenia) w cieniu są danymi do porównania, a nie akcjami.

### Pułapki

- Nagrywarka podpięta tylko w części miejsc - jeden zapomniany efekt i klient dostaje drugi SMS.
- Efekty ukryte w obliczeniu: sekwencja numerów rezerwacji, cache, licznik - też są efektami.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

### Pytanie do sali

Jakie efekty uboczne ma wasz "czysty" kod, o których zwykle nie myślicie (logi audytowe, metryki, sekwencje)?

## Proponowana kolejność pokazu

**Ścieżka krótka (~50 min):** jak zdobyć pierwszy dowód, gdy testów nie ma.

1. Golden master `CinemaManager` (~3 min) - jeden szeroki test na cały stary system
2. s01 Test charakterystyki (ruch A i krok 1, ~12 min)
3. s02 Break Dependencies, kroki 1, 3 i 4 (~15 min)
4. s03 Wektor obserwowalnego zachowania, kroki 1 i 3 (~10 min)
5. s05 Granice trybu shadow, kroki 1 i 2 (~10 min)

**Ścieżka pełna (~80 min):** golden master → s01 → s02 → s03 → s04 → s05, czyli kolejność sekcji 3 slajdów. Jeśli chcesz wcześniej ustawić język wektora zachowania, pokaż s03 przed s02 (scena sama wprowadza swoje seamy). Po s03 zrób przerwę na pytania. Sceny s03 i s05 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 3).
