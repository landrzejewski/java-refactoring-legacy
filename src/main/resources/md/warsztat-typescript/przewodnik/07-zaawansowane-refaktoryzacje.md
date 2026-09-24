# Moduł 7. Zaawansowane refaktoryzacje - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Piętnaście scen w domenie kina pokazuje na żywo wszystkie trzynaście refaktoryzacji z modułu 7 oraz dwa tematy przekrojowe: granicę między refaktoryzacją a zmianą kontraktu i wektor obserwowalnego zachowania. Każda scena ma kod wyjściowy (`start`), kompletne snapshoty po każdym kroku (`stepN`) i test (vitest), który po każdym ruchu w VS Code ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s04, s06, s08, s14, s15), test ma osobne oczekiwania dla wariantów i pokazuje różnicę wprost.

Kod portu TypeScript leży w `typescript/src/workshop/m7/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m7/sNN_.../`. Sceny nie powtarzają przykładów z domeny wdrożeń (`typescript/src/module7`) ani Warsztatów 1-3 z zadań modułu 7. Tamte ćwiczenia uczestnicy robią sami, a sceny poniżej służą do pokazu. Scena s13 pracuje na **kopii** starego `CinemaManager`, więc oryginał w `typescript/src/workshop/legacy` i jego golden master pozostają nietknięte.

Tam, gdzie TypeScript nie ma odpowiednika mechanizmu Javy (rekordy, `final`, przeciążenia konstruktorów, widoczność pakietowa, `BigDecimal` ze skalą, zgodność binarna), scena używa najbliższego idiomu TypeScriptu, a akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej. Wspólne dla całego modułu: rekordy Javy to klasy z polami `readonly` (akcesor `title()` staje się polem `title`), klasy pakietowe Javy są eksportowane (komentarz w klasie to odnotowuje), `BigDecimal` to `Decimal` z decimal.js, `Optional<String>` to `string | undefined`, a w oczekiwanych tekstach testów zamiast nazw wyjątków Javy stoją nazwy klas błędów portu: `IllegalArgumentError`, `NullPointerError`.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m7              # sceny modułu i ich kroki
scripts/warsztat.sh --lang ts test m7/s02          # testy jednej sceny
scripts/warsztat.sh --lang ts test m7              # wszystkie sceny modułu (287 testów)
scripts/warsztat.sh --lang ts diff m7/s13 2 3      # co zmienia krok 3 względem kroku 2 (0 = start)
scripts/warsztat.sh --lang ts diff m7/s13 2 3 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m7/s13 2        # skopiuj step2 do start, gdy brakuje czasu
scripts/warsztat.sh --lang ts next m7/s13          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m7/s13         # przywróć start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w VS Code, test sceny (skrypt albo rozszerzenie Vitest), zdanie z sekcji "Co powiedzieć".
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów, więc tam, gdzie scena opiera się na kompilatorze (lista błędów po zmianie typu mapy w s13, klienci starej sygnatury w s06 i s10), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m7/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu `start`. Snapshoty `stepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd "Wektor obserwowalnego zachowania" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?* Testy scen celowo obserwują więcej niż sam wynik (audyt w s07, licznik w s12, lista wejściowa w s05, skrzynka nadawcza w s03).
- W s01 start **nie ma prawdziwych testów** i to jest puenta: test dla `start` tylko dokumentuje, że klasy nie da się uruchomić, a testy zachowania pojawiają się dopiero wtedy, gdy kod ma seam.
- W s10 VS Code przekreśla wywołania starej metody w 4 miejscach (kroki 1 i 2, po dwóch klientów) - to odpowiednik ostrzeżeń `[deprecation]` z Javy. Sam `tsc` ich nie zgłasza; w CI robi to reguła lintera `@typescript-eslint/no-deprecated`. To celowe: lista przekreśleń jest listą klientów do migracji. Zniknie w kroku 3.
- Kroki, które zmieniają kontrakt (s04 krok 2, s06 krok 3, s08 kroki 1-2, s14 kroki 2-3, s15 krok 2), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".
- Refaktoryzacje w VS Code: zaznaczenie i ⌃⇧R (Refactor...) daje Extract to method in class / Extract to function / Extract to constant / Extract to readonly field i Inline variable, F2 to Rename Symbol, ⌘. (Quick Fix) m.in. Implement interface i Add missing member, a Move to a new file / Move to file przenosi deklaracje najwyższego poziomu. Extract Interface (z klasy), Change Signature, Invert 'if', Convert Local to Field, Introduce Parameter Object, Inline Method ani Safe Delete nie ma - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` (albo Find All References ⇧⌥F12).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
|---|---|---|---|---|
| s01 | 1. Break Dependencies - intencja, ryzyka, przed i po | 4 | `m7/s01_breakdependencies` | ~20 min |
| s02 | 2. Extract Method Object - przed, po, sekwencja i ryzyka | 3 | `m7/s02_methodobject` | ~15 min |
| s03 | 3. Break Responsibilities - intencja, ryzyka, po zmianie | 3 | `m7/s03_breakresponsibilities` | ~12 min |
| s04 | 4. Remove Duplication - duplikacja wiedzy, po zmianie | 3 | `m7/s04_removeduplication` | ~12 min |
| s05 | 5. Break Method - intencja i bezpieczna sekwencja | 3 | `m7/s05_breakmethod` | ~10 min |
| s06 | 6. Introduce Parameter Object - data clump, walidacja, migracja | 3 | `m7/s06_parameterobject` | ~12 min |
| s07 | 7. Remove Arrowhead Antipattern | 3 | `m7/s07_arrowhead` | ~10 min |
| s08 | 8. Introduce Design by Contract Checks | 2 | `m7/s08_designbycontract` | ~10 min |
| s09 | 9. Remove Double Negative | 3 | `m7/s09_doublenegative` | ~8 min |
| s10 | 11. Remove Boolean Method Parameters; Zgodność binarna | 4 | `m7/s10_booleanparameter` | ~12 min |
| s11 | 12. Remove Middle Man | 3 | `m7/s11_middleman` | ~8 min |
| s12 | 13. Return ASAP | 2 | `m7/s12_returnasap` | ~8 min |
| s13 | 10. Remove God Classes - kampania, wydzielony fragment i ryzyka | 4 | `m7/s13_godclass` | ~30 min |
| s14 | Refaktoryzacja a zmiana kontraktu; Pętla pracy (pkt 5) | 3 | `m7/s14_contractchange` | ~10 min |
| s15 | Wektor obserwowalnego zachowania | 3 | `m7/s15_behaviourvector` | ~12 min |

Katalogi są względne wobec `typescript/src/workshop/`.

## Scena s01. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 1. Break Dependencies - intencja i ryzyka; Break Dependencies - przed i po; Java 25 w tym module (interfejs funkcyjny jako seam)
**Katalog:** `typescript/src/workshop/m7/s01_breakdependencies` · **Test:** `scripts/warsztat.sh --lang ts test m7/s01`
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

Sekwencja ze slajdu: charakterystyka, najwęższy kontrakt, dotychczasowa implementacja przez kontrakt, test z fake/spy. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i małe.

### Krok 1: Extract Interface + Parameterize Constructor

**W IDE:** VS Code nie wydziela interfejsu z klasy, więc ręcznie: plik `BookingStore.ts` z interfejsem `BookingStore` z dwiema metodami (`paidBookings`, `markReminded`), a na `LegacyDatabase` dopisz `implements BookingStore`. Potem w `ShowtimeReminderJob`: pole `stores: () => BookingStore` i parametr konstruktora z wartością domyślną `() => new LegacyDatabase()`. W `run()` zamień `new LegacyDatabase()` na `this.stores()`.
**Po:**

```typescript
constructor(stores: () => BookingStore = () => new LegacyDatabase()) {
  this.stores = requireNonNull(stores, 'stores');
}

run(): number {
  const database = this.stores();
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s01` - testy `step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania.
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `run()`. Gdybyśmy dali domyślnie `stores: BookingStore = new LegacyDatabase()`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s01 0 1`

### Krok 2: Clock jako zależność

**W IDE:** ręcznie (VS Code nie ma Introduce Parameter; ⌃⇧R dodałby co najwyżej parametr do wydzielanej funkcji, a potrzebujemy zależności konstruktora): pole `clock: Clock`, drugi parametr konstruktora z wartością domyślną `systemClock`, w `run()` `LocalDateTime.now(this.clock)`.
**Po:**

```typescript
const now = LocalDateTime.now(this.clock);
```

**Uruchom:** test zielony. Test `step2ControlsTimeButStillHitsStaticMailer` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s01 1 2`

### Krok 3: Subclass and Override Method i pierwszy test

**W IDE:** zaznacz wywołanie `ReminderMailer.send(...)`, ⌃⇧R > Extract to method in class 'ShowtimeReminderJob', nazwa `sendReminder`. VS Code tworzy metodę `private` - zmień ją ręcznie na `protected`. W teście anonimowa podklasa (`new (class extends step3.ShowtimeReminderJob { ... })(...)`) nadpisuje `sendReminder` i zapisuje wiadomości do tablicy - to jest **pierwszy prawdziwy test** tej klasy.
**Po:**

```typescript
protected sendReminder(to: string, subject: string, body: string): void {
  ReminderMailer.send(to, subject, body);
}
```

**Uruchom:** test zielony: 3 przypadki z granicą 120/121 minut, już przypomniane i już rozpoczęte.
**Co powiedzieć:** to najtańszy seam, gdy nie możemy jeszcze zmienić konstruktora - ale ma cenę: klasa ma teraz punkt rozszerzenia przez dziedziczenie (metodę `protected`), a test zależy od szczegółu implementacji (nazwy metody chronionej).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s01 2 3`

### Krok 4: seam z dziedziczenia na typ funkcyjny

**W IDE:** utwórz `ReminderSender.ts` z `export type ReminderSender = (to: string, subject: string, body: string) => void;`, dodaj trzeci parametr konstruktora z wartością domyślną wołającą `ReminderMailer.send`. Inline Method nie ma w VS Code, więc ręcznie: w `run()` zamień `this.sendReminder(...)` na `this.sender(...)` i usuń metodę `protected`. W teście podklasę zastępuje lambda.
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
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s01 3 4`

### Rozwiązanie i uzasadnienie

`step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`BookingStore` z dwiema metodami, `Clock`, `ReminderSender` z jedną sygnaturą). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `stores: BookingStore = new LegacyDatabase()` jako parametr domyślny: zmiana czasu życia i momentu wyjątku (test `step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- Seam typowany klasą `LegacyDatabase` zamiast interfejsu - dopóki klasa ma tylko metody publiczne, fake pasuje strukturalnie, ale pierwsze pole `private` albo `#private` robi z typu klasy typ nominalny i test nie podstawi już własnej implementacji.
- Zostawienie Subclass and Override na stałe: metoda `protected` i klasa otwarta do dziedziczenia tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s02. Extract Method Object - wycena zamówienia grupowego

**Temat ze slajdów:** 2. Extract Method Object - przed; Extract Method Object - po, sekwencja i ryzyka
**Katalog:** `typescript/src/workshop/m7/s02_methodobject` · **Test:** `scripts/warsztat.sh --lang ts test m7/s02`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `GroupPricing.quote` ma osiem splątanych zmiennych lokalnych i pętlę z dwoma wynikami, więc zwykły Extract Method się nie udaje. Przenosimy całe ciało dosłownie do obiektu `GroupQuoteCalculation`, zmienne robocze robimy polami i dopiero wtedy tniemy algorytm na nazwane kroki.

**Zasada:** Extract Method Object zamienia jedno wywołanie metody w krótko żyjący obiekt, którego pola niosą stan lokalny, dzięki czemu każdy blok może stać się metodą bez parametrów. Stosujemy go dopiero wtedy, gdy przepływ lokalnego stanu blokuje zwykłe ekstrakcje, w kolejności: skopiuj bez upraszczania, deleguj, porównaj, potem porządkuj.

**Efekt:** `calculate()` czyta się jak sześć kroków wyceny, a publiczne API `GroupPricing` i `Quote` się nie zmieniły. Kolejność wywołań kroków staje się kontraktem, a obiekt musi powstawać na każde wywołanie, bo inaczej wyceny zaczną współdzielić stan.

**Różnica względem Javy:** pola robocze obiektu metody mają w krokach 2-3 jawne wartości początkowe (`morning = false`, `count = 0`) - odpowiednik domyślnych wartości pól w Javie - a `base` ma `!` (definite assignment), bo przypisuje go dopiero `readConditions()`. Obcięcie `intValue() / 10` to `Math.trunc(tickets.amount.trunc().toNumber() / 10)`. Test porównuje `Quote.toString()`, bo `Money` nie ma równości wartościowej rekordu.

### Co widzimy

`GroupPricing.quote` liczy cenę zamówienia (także grupowego): cena formatu, zniżki, poranek, okulary 3D, VIP, rabat 10% od 10 biletów, opłaty online i punkty. Osiem zmiennych lokalnych karmi się nawzajem. Pętla ma dwa wyjścia (`tickets`, `count`) i trzy wejścia robocze.

```typescript
let tickets = Money.ZERO;
let count = 0;
for (const type of order.ticketTypes) {
  ...
  let price = base.minus(base.percent(discount));
  if (morning) { price = price.minus(Money.of('5.00')); }
  if (glasses) { price = price.plus(Money.of('3.00')); }
  tickets = tickets.plus(price);
  count++;
}
```

Spróbuj ⌃⇧R > Extract to function na pętli: VS Code wprawdzie ją wydzieli, ale nowa funkcja dostaje kilka parametrów roboczych i zwraca literał obiektu z `tickets` i `count`, przypisywany z powrotem destrukturyzacją. Dwa wyjścia i trzy wejścia robocze nie zniknęły, tylko zmieniły miejsce. To jest moment na Method Object.

### Krok 1: Extract Method Object - kopia bez upraszczania

**W IDE:** utwórz plik `GroupQuoteCalculation.ts` z klasą `GroupQuoteCalculation` (pole `order` jako parametr konstruktora `private readonly`) i metodą `calculate()`. Skopiuj ciało `quote` **dosłownie**, zamieniając `order` na `this.order`. W `GroupPricing.quote` zostaw jedną linię delegacji. (VS Code nie ma Replace Method with Method Object - ten ruch zawsze robimy ręcznie.)
**Po:**

```typescript
quote(order: GroupOrder): Quote {
  return new GroupQuoteCalculation(order).calculate();
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s02` - 24 testy zielone.
**Co powiedzieć:** skopiuj, deleguj, porównaj - dopiero potem sprzątaj. Obiekt metody powstaje na każde wywołanie, więc nie ma współdzielonego stanu między wycenami.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s02 0 1`

### Krok 2: zmienne lokalne jako pola

**W IDE:** VS Code nie ma Convert Local to Field, więc ręcznie: dla każdej zmiennej roboczej (`base`, `morning`, `glasses`, `tickets`, `count`) dodaj pole `private`, usuń `let`/`const` z deklaracji w metodzie i dopisz `this.` przy użyciach (Rename Symbol F2 nie pomoże - to zmiana rodzaju symbolu, nie nazwy; kompilator podkreśli każde pominięte miejsce).
**Po:**

```typescript
private base!: Money;
private morning = false;
private glasses = false;
private tickets = Money.ZERO;
private count = 0;
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz każdy blok może zostać metodą bez parametrów i bez wielu wyjść - stan niosą pola. To działa tylko dlatego, że obiekt żyje jedno wywołanie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s02 1 2`

### Krok 3: Extract Method wewnątrz obiektu

**W IDE:** kolejno ⌃⇧R > Extract to method in class 'GroupQuoteCalculation': `readConditions`, `addTickets` (a w niej `ticketPrice` i statyczne `discountPercent`), `addVipSeats`, `applyGroupDiscount`, `bookingFees`, `loyaltyPoints`. Na `total` ⌃⇧R > Inline variable.
**Po:**

```typescript
calculate(): Quote {
  this.readConditions();
  this.addTickets();
  this.addVipSeats();
  this.applyGroupDiscount();
  const fees = this.bookingFees();
  return new Quote(this.tickets, fees, this.tickets.plus(fees), this.loyaltyPoints());
}
```

**Uruchom:** test zielony - w tym przypadek z rabatem 23.125 zaokrąglonym do 23.13.
**Co powiedzieć:** kolejność wywołań jest kontraktem: rabat grupowy liczony po dodaniu VIP, punkty po rabacie. Metody z efektem na polach czytają się jak kroki algorytmu.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s02 2 3`

### Rozwiązanie i uzasadnienie

`step3/GroupQuoteCalculation`: jedno wykonanie wyceny jako obiekt, sześć nazwanych kroków. Publiczne API `GroupPricing` i kontrakt `Quote` bez zmian. Porównaj ze sceną s05: tam dane łatwo przechodzą między etapami, więc Method Object byłby zbędny.

### Pułapki

- Obiekt metody jako pole usługi (`private readonly calc = new GroupQuoteCalculation(...)`) - pola przeżyją wywołanie i wyceny zaczną się mieszać.
- Upraszczanie w trakcie kopiowania (np. `count` zastąpione `order.ticketTypes.length` przed testem).
- Obiekt metody "po cichu" jako domknięcie: funkcje zagnieżdżone w `quote` na wspólnych `let` - stan jest ukryty, nienazwany i nie da się go obejrzeć ani przetestować osobno.
- Zmiana momentu odczytu danych (np. `order.start` czytane w konstruktorze zamiast w `calculate()`).

### Pytanie do sali

Po co w ogóle Method Object, skoro można zwrócić obiekt `{ tickets, count }` z wydzielonej pętli?

## Scena s03. Break Responsibilities - walidacja, wycena, powiadomienie

**Temat ze slajdów:** 3. Break Responsibilities - intencja i ryzyka; Break Responsibilities - po zmianie
**Katalog:** `typescript/src/workshop/m7/s03_breakresponsibilities` · **Test:** `scripts/warsztat.sh --lang ts test m7/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `BookingDesk.book` waliduje prośbę, wycenia miejsca i wysyła powiadomienie, czyli ma trzy powody zmiany od trzech różnych działów kina. Wydzielamy po jednej klasie na raz: `BookingValidator`, `TicketPricer` z klasą wyniku `Pricing` i `BookingNotifier`.

**Zasada:** Break Responsibilities dzieli klasę według powodów zmiany, a nie według liczby linii czy metod. Wydzielamy spójny klaster, stara klasa zostaje delegującą fasadą, a każdy stan ma dokładnie jednego właściciela, bez dwóch kopii.

**Efekt:** `BookingDesk` tylko składa kroki w tej samej kolejności co wcześniej, a publiczny konstruktor i `book` są bez zmian, więc klienci niczego nie zauważają. `Outbox` należy wyłącznie do notifiera, a w prawdziwym systemie trzeba jeszcze pilnować granic transakcji między nowymi klasami.

**Różnica względem Javy:** `Optional<String>` walidatora to `string | undefined`. W kroku 3 Java ma dodatkowy pakietowy konstruktor `BookingDesk(validator, pricer, notifier)`; TypeScript nie ma przeciążeń konstruktorów ani widoczności pakietowej, więc części są składane w jedynym konstruktorze `BookingDesk(outbox)` (odnotowane w komentarzu klasy).

### Co widzimy

`BookingDesk.book` waliduje prośbę, wycenia miejsca i wysyła powiadomienie do `Outbox`. Trzy powody zmiany: reguły walidacji (obsługa klienta), cennik (finanse), treść wiadomości (marketing). Komentarze `// walidacja`, `// wycena`, `// powiadomienie` wyznaczają klastry. Test obserwuje wynik i zawartość skrzynki nadawczej.

```typescript
let total = new Decimal(0);
let vipSeats = 0;
for (const seat of request.seats) { ... }

// powiadomienie
let text = `Rezerwacja ${request.seats.length} miejsc`;
if (vipSeats > 0) { ... }
```

### Krok 1: Extract Class - walidacja

**W IDE:** zaznacz blok walidacji, ⌃⇧R > Extract to method in class 'BookingDesk', nazwa `firstError` zwracająca `string | undefined` (ręcznie dopisz `return undefined;` na końcu i zmień wywołanie na sprawdzenie wyniku). Przeniesienia metody do innej klasy VS Code nie robi - utwórz `BookingValidator.ts`, wytnij i wklej metodę, w `BookingDesk` pole `validator = new BookingValidator()`.
**Po:**

```typescript
const error = this.validator.firstError(request);
if (error !== undefined) {
  return error;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s03` - 20 testów zielonych (w tym priorytet: zły e-mail przed brakiem miejsc).
**Co powiedzieć:** kolejność kontroli jest częścią kontraktu - pierwszy błąd wygrywa.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s03 0 1`

### Krok 2: Extract Class - wycena z wynikiem jako obiekt wartości

**W IDE:** utwórz klasę `Pricing` z polami `readonly total: Decimal` i `readonly vipSeats: number`, wydziel blok wyceny do `TicketPricer.price`. Dwa wyjścia pętli wracają jako jeden obiekt z nazwą z domeny.
**Po:**

```typescript
const pricing = this.pricer.price(request);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `vipSeats` potrzebuje powiadomienie, a liczy cennik - klasa wyniku jawnie opisuje ten przepływ zamiast ukrytej zmiennej lokalnej.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s03 1 2`

### Krok 3: Extract Class - powiadomienie, BookingDesk jako orkiestrator

**W IDE:** wydziel blok powiadomienia do `BookingNotifier.bookingConfirmed(request, pricing)`. `Outbox` przechodzi do notifiera. Publiczny konstruktor zostaje `BookingDesk(outbox)` i składa w sobie trzech współpracowników.
**Po:**

```typescript
book(request: BookingRequest): string {
  const error = this.validator.firstError(request);
  if (error !== undefined) {
    return error;
  }
  const pricing = this.pricer.price(request);
  this.notifier.bookingConfirmed(request, pricing);
  return `OK ${pricing.total.toFixed(2)}`;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klasa składa kroki, ale nie zna ich szczegółów. Stan (`Outbox`) ma jednego właściciela.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s03 2 3`

### Rozwiązanie i uzasadnienie

`step3/BookingDesk` z trzema współpracownikami. Publiczne API bez zmian, więc klienci niczego nie zauważają. Kierunek zależności jest czytelny: orkiestrator zna usługi, usługi nie znają orkiestratora.

### Pułapki

- Podział według liczby linii, a nie powodu zmiany.
- Dwie kopie stanu (np. `Outbox` w `BookingDesk` i w `BookingNotifier`).
- Przeniesienie powiadomienia przed wycenę "bo tak ładniej" - zmiana kolejności efektów.
- W prawdziwym systemie: rozdzielenie transakcji (zapis i wysyłka w innych granicach, np. osobne `await`).

### Pytanie do sali

Czy `TicketPricer` powinien dostać `BookingRequest`, czy tylko format i listę miejsc?

## Scena s04. Remove Duplication - rabat grupowy w kasie i w sklepie

**Temat ze slajdów:** 4. Remove Duplication - duplikacja wiedzy; Remove Duplication - po zmianie
**Katalog:** `typescript/src/workshop/m7/s04_removeduplication` · **Test:** `scripts/warsztat.sh --lang ts test m7/s04`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Reguła "10 i więcej biletów to 10% rabatu" żyje w `BoxOffice` i `WebShop`, zapisana zupełnie inaczej i z innym zaokrągleniem. Najpierw ujednolicamy zapis, żeby różnica stała się widoczna, potem świadomie decydujemy o zaokrągleniu i wydzielamy regułę do `GroupDiscount`.

**Zasada:** Remove Duplication łączy fragmenty reprezentujące tę samą wiedzę, czyli regułę, która zmienia się razem, a nie podobny tekst. Podobne reguły z niezależnych kontekstów mogą celowo żyć osobno, a wspólna metoda z flagą "żeby nic nie zmienić" zwykle utrwala przypadkową różnicę.

**Efekt:** Reguła rabatu ma jednego właściciela, a opłata online zostaje jawnie w `WebShop`. Zachowanie świadomie się zmienia: w przypadku brzegowym sklep zaokrągla teraz HALF_UP jak kasa i klient płaci grosz mniej, co trafia do osobnego commita jako zmiana kontraktu.

**Różnica względem Javy:** `setScale(2, RoundingMode.HALF_UP/HALF_EVEN)` to `toDecimalPlaces(2, Decimal.ROUND_HALF_UP/ROUND_HALF_EVEN)`, a stream z `reduce` w `WebShop` to `Array.reduce`. Koszyki wspólne dla dwóch plików testów (`TWO_3D`, `TEN_2D`, `EDGE`, `prices`) są w module `test/workshop/m7/s04_removeduplication/baskets.ts`.

### Co widzimy

Reguła "10+ biletów = -10%" żyje w dwóch miejscach, zapisana inaczej: `BoxOffice` (pętla, `> 9`, `times(0.10)`, HALF_UP) i `WebShop` (`reduce`, `>= 10`, `x * 10 / 100`, **HALF_EVEN**). Tekstowo niepodobne, ale to ta sama wiedza. Czy różnica w zaokrągleniu to decyzja, czy przypadek?

```typescript
// BoxOffice
const discount = sum.times(new Decimal('0.10')).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
// WebShop
tickets = tickets.minus(tickets.times(10)
  .dividedBy(new Decimal('100')).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN));
```

Test `S04RoundingDecisionTest` pokazuje przypadek brzegowy: 3 x student 2D (18.75) + 7 x normalny = 231.25, rabat 23.125. Kasa odejmuje 23.13, sklep 23.12.

### Krok 1: ujednolicenie zapisu (bez zmiany zachowania)

**W IDE:** w obu klasach zaznacz liczbę i ⌃⇧R > Extract to readonly field in class, potem ręcznie dopisz `static` i nadaj nazwy `GROUP_SIZE` i `GROUP_DISCOUNT`. F2 (Rename Symbol) `sum` na `tickets`, warunek `> 9` na `>= GROUP_SIZE`, w `WebShop` `reduce` na pętlę i `x * 10 / 100` na `times(GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN)`.
**Po:**

```typescript
if (ticketPrices.length >= WebShop.GROUP_SIZE) {
  const discount = tickets.times(WebShop.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_EVEN);
  tickets = tickets.minus(discount);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s04` - wszystko zielone (34 testy), sklep nadal daje 228.13 w przypadku brzegowym.
**Co powiedzieć:** po ujednoliceniu obie wersje różnią się jedną linią. Różnica przestała być ukryta w formie zapisu i można o niej rozmawiać z biznesem.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s04 0 1`

### Krok 2: decyzja - HALF_UP także w sklepie (zmiana kontraktu)

**W IDE:** ręcznie `ROUND_HALF_EVEN` na `ROUND_HALF_UP` w `WebShop`. Osobny commit z opisem decyzji.
**Po:**

```typescript
const discount = tickets.times(WebShop.GROUP_DISCOUNT).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zielony: `step2DeliberatelyAlignsWebWithBoxOffice` oczekuje 228.12, zwykłe koszyki bez zmian.
**Co powiedzieć:** to NIE jest refaktoryzacja - klient sklepu w przypadku brzegowym zapłaci grosz mniej. Robimy to świadomie, osobno i z testem, który mówi to wprost.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s04 1 2`

### Krok 3: wydzielenie reguły

**W IDE:** w `BoxOffice` zaznacz pętlę i rabat, ⌃⇧R > Extract to method in class, nazwa `ticketsTotal`, ręcznie `static`. Metody między klasami VS Code nie przenosi, więc utwórz `GroupDiscount.ts` i wytnij do niego metodę razem ze stałymi. W `WebShop` zastąp identyczny fragment wywołaniem - VS Code nie szuka duplikatów po ekstrakcji, więc robimy to ręcznie.
**Po:**

```typescript
// WebShop
const fees = WebShop.FEE.times(ticketPrices.length);
return GroupDiscount.ticketsTotal(ticketPrices).plus(fees).toDecimalPlaces(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zielony.
**Co powiedzieć:** wspólna reguła ma jednego właściciela, a różnica (opłata online) zostaje jawna w `WebShop`. Przełączaj po jednej ścieżce: najpierw kasa, test, potem sklep.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s04 2 3`

### Rozwiązanie i uzasadnienie

`step3/GroupDiscount`: reguła 10+/-10%/HALF_UP w jednym miejscu. Sekwencja ze slajdu zachowana: scharakteryzuj obie wersje osobno, porównaj brzegi, ujednolić, zdecyduj o różnicy, wydziel, przełącz po jednej ścieżce.

### Pułapki

- Wydzielenie wspólnej metody z parametrem `rounding: Decimal.Rounding` "żeby nic nie zmieniać" - flaga utrwala przypadkową różnicę.
- Sklejanie podobnego tekstu, który jest inną wiedzą (np. opłata online wygląda jak rabat, ale ma innego właściciela).
- Test tylko na "ładnych" kwotach (250.00) - różnica w zaokrągleniu przechodzi niezauważona.
- `toDecimalPlaces(2)` bez trybu zaokrąglenia - użyje globalnego `Decimal.rounding`, który ktoś inny może zmienić przez `Decimal.set(...)`.

### Pytanie do sali

Kto w Waszej firmie może zdecydować, że HALF_EVEN w sklepie był błędem, a nie wymaganiem?

## Scena s05. Break Method - repertuar dnia

**Temat ze slajdów:** 5. Break Method - intencja; Break Method - bezpieczna sekwencja
**Katalog:** `typescript/src/workshop/m7/s05_breakmethod` · **Test:** `scripts/warsztat.sh --lang ts test m7/s05`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RepertoireBuilder.build` miesza kontrolę wejścia, porządkowanie seansów i renderowanie tekstu w jednej metodzie. Serią zwykłych Extract Method dzielimy ją na `validateAndCopy`, `order` i `render`.

**Zasada:** Break Method to seria małych ekstrakcji, po której metoda opisuje algorytm na jednym poziomie abstrakcji - kryterium to nazwane, spójne kroki, a nie liczba wierszy. Zaczynamy od fragmentu z najmniejszą liczbą wejść i jednym wynikiem, przenosimy go dosłownie, a nazwę nadajemy po teście. Method Object jest potrzebny dopiero wtedy, gdy lokalny stan blokuje ekstrakcje.

**Efekt:** `build` ma trzy linie, a typ i komunikat wyjątku oraz nienaruszona lista klienta zostają bez zmian. Etapy nadal dzielą model `Screening`, więc gdyby potrzebne były osobne modele danych, następnym ruchem byłby Split Phase.

**Różnica względem Javy:** `Comparator.comparing(start).thenComparing(title)` to komparator `(a, b) => a.start.compareTo(b.start) || compareText(a.title, b.title)`, gdzie `compareText` porównuje jednostki kodu niezależnie od locale (jak `String.compareTo`, a nie `localeCompare`). `StringBuilder` to konkatenacja łańcucha. Wejście jest typowane jako `readonly (Screening | null)[]`, bo test podaje `null` w środku listy, a w oczekiwanym tekście stoi `IllegalArgumentError:`.

### Co widzimy

`RepertoireBuilder.build` miesza trzy poziomy abstrakcji: kontrolę wejścia (`null` w liście), porządkowanie (odwołane seanse, sortowanie po godzinie i tytule) i renderowanie tekstu. Test obserwuje tekst albo wyjątek (typ i komunikat) oraz to, czy lista klienta pozostała nietknięta.

```typescript
const active: Screening[] = [];
for (const screening of copy) { if (!screening.cancelled) { active.push(screening); } }
active.sort((a, b) => a.start.compareTo(b.start) || compareText(a.title, b.title));
let text = 'REPERTUAR\n';
```

### Krok 1: Extract Method - fragment z jednym wejściem i jednym wynikiem

**W IDE:** zaznacz pierwszą pętlę (kopia z kontrolą `null`) razem z deklaracją `copy`, ⌃⇧R > Extract to method in class 'RepertoireBuilder', nazwa `validateAndCopy`.
**Po:**

```typescript
const copy = this.validateAndCopy(screenings);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s05` - 12 testów zielonych, w tym wyjątek dla `null`.
**Co powiedzieć:** zaczynamy od fragmentu z najmniejszą liczbą zależności i przenosimy go dosłownie. Nazwa przychodzi po teście.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s05 0 1`

### Krok 2: Extract Method - porządkowanie

**W IDE:** zaznacz filtr i sortowanie, ⌃⇧R > Extract to method in class, nazwa `order`.
**Po:**

```typescript
const active = this.order(copy);
```

**Uruchom:** test zielony - "wejscie bez zmian: true".
**Co powiedzieć:** sortujemy kopię. Gdyby ktoś "uprościł" to do `screenings.sort(...)` (albo `filter` i `sort` na wejściu przekazanym przez referencję), lista klienta zostałaby przestawiona - test to wykryje. Nowsze `toSorted()` zwraca kopię, `sort()` sortuje w miejscu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s05 1 2`

### Krok 3: Extract Method - renderowanie i Inline Variable

**W IDE:** zaznacz budowę tekstu, ⌃⇧R > Extract to method in class, nazwa `render`. F2 (Rename Symbol) zmiennych na `validated` i `ordered`.
**Po:**

```typescript
build(screenings: readonly (Screening | null)[]): string {
  const validated = this.validateAndCopy(screenings);
  const ordered = this.order(validated);
  return this.render(ordered);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda opisuje algorytm na jednym poziomie abstrakcji. Dane łatwo przechodzą między krokami (lista, lista, tekst), więc Method Object ze sceny s02 byłby tu przerostem formy.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s05 2 3`

### Rozwiązanie i uzasadnienie

`step3/RepertoireBuilder`: trzy etapy, każdy z jednym wejściem i jednym wyjściem. Gdyby etapy miały osobne modele danych (np. wiersz raportu zamiast `Screening`), następnym ruchem byłby Split Phase.

### Pułapki

- Ekstrakcja pętli z `return`/`break`/`continue` w środku bez sprawdzenia, dokąd skacze sterowanie.
- Zastąpienie `validateAndCopy` przez `[...screenings]` albo `screenings.filter((s) => s !== null)` - kopia nie rzuca w ogóle, a filtr po cichu gubi `null` zamiast zgłosić `IllegalArgumentError` (inny wynik, brak wyjątku).
- Sortowanie tablicy wejściowej zamiast kopii (aliasowanie) - `readonly` w typie parametru chroni tylko przed kompilatorem, nie przed rzutowaniem ani kodem JS.

### Pytanie do sali

Kiedy `order` i `render` zasługują na osobne klasy (albo moduły), a kiedy wystarczą prywatne metody?

## Scena s06. Introduce Parameter Object - termin seansu

**Temat ze slajdów:** 6. Introduce Parameter Object - data clump jako pojęcie; Parameter Object - walidacja, migracja i ryzyka
**Katalog:** `typescript/src/workshop/m7/s06_parameterobject` · **Test:** `scripts/warsztat.sh --lang ts test m7/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Dwie metody `ScreeningPlanner` przyjmują tę samą czwórkę parametrów, która w domenie znaczy "termin seansu w sali". Nazywamy ją klasą `ScreeningSlot`, zostawiamy stare sygnatury na czas migracji, przenosimy do nowego typu opis, a na końcu walidację.

**Zasada:** Introduce Parameter Object zamienia grupę parametrów, które zawsze chodzą razem (data clump), w typ nazywający jedno pojęcie z domeny - typ ogranicza pomyłki kolejności i przyciąga zachowanie. To nie jest worek `Parameters` na długą listę argumentów. Walidacja w konstruktorze przesuwa moment błędu, więc jest osobnym krokiem.

**Efekt:** `ScreeningSlot` jest poprawny z definicji, a walidacja jest w jednym miejscu. Zachowanie świadomie się zmienia: zła sala rzuca wyjątek już przy tworzeniu obiektu, więc `describe` dla sali 12 przestaje zwracać tekst, a stare sygnatury trzeba jeszcze usunąć po migracji klientów.

**Różnica względem Javy:** rekord `ScreeningSlot` to klasa z polami `readonly`, a walidacja w kroku 3 siedzi w zwykłym konstruktorze (zamiast kompaktowego konstruktora rekordu). Przeciążenia `describe(String, LocalDate, int, String)` / `describe(ScreeningSlot)` (i tak samo `ticketPrice`) to w TypeScripcie sygnatury przeciążeń z jedną implementacją, która rozpoznaje wariant przez `typeof slotOrId === 'string'`. Stara sygnatura ma `/** @deprecated */` - VS Code przekreśla jej wywołania (odpowiednik `@Deprecated` i ostrzeżeń kompilatora), a kształt wywołań się nie zmienia. Pomocnicze funkcje testu są w module `S06Observations.ts`, bo używają ich oba pliki testów.

### Co widzimy

`ScreeningPlanner` ma dwie metody z tą samą czwórką parametrów: `screeningId, date, hall, format`. To pojęcie "termin seansu w sali", którego nikt nie nazwał. Walidacja sali i formatu siedzi tylko w `ticketPrice`, a `describe` przyjmuje wszystko.

```typescript
describe(screeningId: string, date: LocalDate, hall: number, format: string): string
ticketPrice(screeningId: string, _date: LocalDate, hall: number, format: string): Decimal
```

### Krok 1: Introduce Parameter Object (stan przejściowy, bez walidacji)

**W IDE:** VS Code ma tylko Convert parameters to destructured object (⌃⇧R), który tworzy anonimowy typ literału obiektu i przepisuje wywołania - nie nazywa pojęcia i nie zostawia starej sygnatury. Dlatego ręcznie: utwórz `ScreeningSlot.ts` z klasą o czterech polach `readonly` (parameter properties), dodaj do `describe` i `ticketPrice` sygnaturę przeciążenia z `ScreeningSlot`, starą oznacz `/** @deprecated */`, a implementacja w gałęzi `typeof slotOrId === 'string'` składa `ScreeningSlot` i deleguje.
**Po:**

```typescript
export class ScreeningSlot {
  constructor(
    readonly screeningId: string,
    readonly date: LocalDate,
    readonly hall: number,
    readonly format: string,
  ) {}
}

describe(slot: ScreeningSlot): string;
/** @deprecated użyj {@link ScreeningPlanner.describe} z ScreeningSlot */
describe(screeningId: string, date: LocalDate, hall: number, format: string): string;
describe(slotOrId: ScreeningSlot | string, date?: LocalDate, hall?: number, format?: string): string {
  if (typeof slotOrId === 'string') {
    return this.describe(new ScreeningSlot(slotOrId, date!, hall!, format!));
  }
  ...
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s06` - 24 testy zielone: nowe API i stare sygnatury dają te same wyniki.
**Co powiedzieć:** typ bez walidacji to celowy stan przejściowy - kontrole zostają tam, gdzie były. Stare sygnatury pozwalają migrować wywołania po jednym, a przekreślenia w edytorze pokazują, kto jeszcze ich używa.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s06 0 1`

### Krok 2: Move Method - typ przyciąga zachowanie

**W IDE:** VS Code nie przenosi metod między klasami, więc ręcznie: w `ScreeningSlot` dodaj `label()` z treścią opisu (`slot.` na `this.`), a `describe` deleguje do `slot.label()`.
**Po:**

```typescript
label(): string {
  return `${this.screeningId} ${this.date.toString()} sala ${this.hall} (${this.format})`;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po nazwaniu pojęcia pojawia się miejsce na zachowanie, które do niego należy.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s06 1 2`

### Krok 3: walidacja w konstruktorze (zmiana kontraktu)

**W IDE:** przenieś kontrolę sali i formatu z `ticketPrice` do konstruktora `ScreeningSlot` (zbiór `FORMATS` jako `static readonly`). W `ticketPrice` gałąź `default: throw` staje się zbędna.
**Po:**

```typescript
constructor(
  readonly screeningId: string,
  readonly date: LocalDate,
  readonly hall: number,
  readonly format: string,
) {
  if (hall < 1 || hall > 8) {
    throw new IllegalArgumentError(`nie ma sali ${hall} (${screeningId})`);
  }
  if (!ScreeningSlot.FORMATS.has(format)) { ... }
}
```

**Uruchom:** test zielony. `S06ValidationMomentTest` pokazuje różnicę: do kroku 2 `describe` dla sali 12 zwraca tekst, a `ticketPrice` rzuca. W kroku 3 wyjątek leci już przy `new ScreeningSlot(...)`.
**Co powiedzieć:** ten sam komunikat, inny moment. Klient, który logował opis przed wyceną, przestanie dostawać log. To zmiana kontraktu - osobny krok i osobny commit.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s06 2 3`

### Rozwiązanie i uzasadnienie

`step3/ScreeningSlot` jest poprawny z definicji, planner nie musi niczego sprawdzać. Stare sygnatury zostają do końca migracji klientów, potem usuwamy je ręcznie (Safe Delete w VS Code nie ma - Find All References ⇧⌥F12 na starej sygnaturze i `npm run typecheck` pokażą, czy ktoś jej jeszcze używa).

### Pułapki

- Walidacja w konstruktorze w tym samym kroku co wprowadzenie typu - nie wiadomo, który ruch zmienił zachowanie.
- Worek `Parameters` z flagami (albo anonimowy literał obiektu z Convert parameters to destructured object) zamiast pojęcia z domeny.
- `readonly` jest tylko płytkie i tylko w czasie kompilacji: pole tablicowe bez kopii defensywnej da się zmienić z zewnątrz. Kopia (`[...seats]`) albo `Object.freeze` zmienia semantykę aliasowania.
- Nazwy pól publicznego typu to API - zmiana nazwy łamie klientów i każdy `JSON.stringify`/`JSON.parse` na granicy.
- Obiekt zbudowany literałem `{ screeningId, date, hall, format }` pasuje strukturalnie do typu klasy bez pól prywatnych i omija walidację z konstruktora - dlatego po kroku 3 warto, żeby klasa miała coś nominalnego (np. pole `private`) albo fabrykę.

### Pytanie do sali

Czy `date` naprawdę należy do tego pojęcia, skoro `ticketPrice` jej nie używa?

## Scena s07. Remove Arrowhead Antipattern - bramka rezerwacji

**Temat ze slajdów:** 7. Remove Arrowhead Antipattern; Remove Arrowhead - po zmianie
**Katalog:** `typescript/src/workshop/m7/s07_arrowhead` · **Test:** `scripts/warsztat.sh --lang ts test m7/s07`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BookingGate.book` ma pięć poziomów zagnieżdżenia, a główna ścieżka rezerwacji siedzi na dnie, pod którym jeszcze zapisuje się audyt. Najpierw wydzielamy decyzję do `decide`, żeby audyt został w jednym miejscu, a potem spłaszczamy warunki poziom po poziomie.

**Zasada:** Remove Arrowhead zamienia zagnieżdżone `if`/`else` na guard clauses, czyli wczesne wyjścia dla przypadków kończących przetwarzanie. Jest bezpieczne tylko przy zachowanym priorytecie warunków i wtedy, gdy wczesny `return` nie omija efektu na końcu metody, takiego jak log, zmiana stanu czy zwolnienie zasobu.

**Efekt:** `decide` to czysta funkcja z pięcioma guard clauses bez zmiennej `result`, a audyt nadal zapisuje się dla każdej ścieżki. Trzeba pilnować poprawnego zaprzeczenia warunków: `<=` odwraca się na `>`, a nie na `>=`.

### Co widzimy

`BookingGate.book` ma pięć poziomów zagnieżdżenia, a główna ścieżka (`BOOKED`) siedzi na samym dnie. Wynik zbiera zmienna `result`. Na końcu metoda zapisuje wpis do audytu - **dla każdej ścieżki**.

```typescript
if (attempt.screeningFound) {
  if (attempt.salesOpen) {
    if (!attempt.customerBlocked) {
      if (attempt.requestedSeats > 0) {
        if (attempt.requestedSeats <= attempt.freeSeats) {
...
this.auditLog.push(`${attempt.email} -> ${result}`);
return result;
```

Test to macierz gałęzi z kombinacjami sprawdzającymi priorytet (np. brak seansu i jednocześnie zamknięta sprzedaż) plus zawartość audytu.

### Krok 1: Extract Method - oddziel decyzję od efektu

**W IDE:** zaznacz cały grot (od `let result: string;` do końca zewnętrznego `if`), ⌃⇧R > Extract to method in class 'BookingGate', nazwa `decide`.
**Po:**

```typescript
book(attempt: BookingAttempt): string {
  const result = this.decide(attempt);
  this.auditLog.push(`${attempt.email} -> ${result}`);
  return result;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s07` - 28 testów zielonych.
**Co powiedzieć:** najpierw chronimy efekt uboczny. Gdybyśmy wstawili guard clauses od razu w `book`, każde wczesne `return` ominęłoby audyt - test pokazałby puste listy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s07 0 1`

### Krok 2: odwróć najbardziej zewnętrzny warunek

**W IDE:** VS Code nie ma Invert 'if', więc ręcznie: warunek `if (attempt.screeningFound)` zamień na `if (!attempt.screeningFound)`, przenieś do niego ciało gałęzi `else` z `return 'NO_SCREENING'` zamiast przypisania i usuń `else`, a dotychczasowe ciało `if` wyciągnij poziom wyżej.
**Po:**

```typescript
if (!attempt.screeningFound) {
  return 'NO_SCREENING';
}
let result: string;
if (attempt.salesOpen) { ...
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden poziom naraz, test po każdym. Kolejność warunków się nie zmienia - to ona definiuje priorytet błędów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s07 1 2`

### Krok 3: spłaszcz pozostałe poziomy

**W IDE:** powtórz ruch z kroku 2 dla każdego kolejnego poziomu. Na końcu zmienna `result` znika (ostatnie przypisanie staje się `return 'BOOKED'`, deklarację usuń - kompilator podpowie, gdy zostanie nieużywana).
**Po:**

```typescript
if (!attempt.screeningFound) { return 'NO_SCREENING'; }
if (!attempt.salesOpen) { return 'SALES_CLOSED'; }
if (attempt.customerBlocked) { return 'CUSTOMER_BLOCKED'; }
if (attempt.requestedSeats <= 0) { return 'NO_SEATS_REQUESTED'; }
if (attempt.requestedSeats > attempt.freeSeats) { return 'SOLD_OUT'; }
return 'BOOKED';
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaprzeczenie `requestedSeats <= freeSeats` to `>`, nie `>=` - przypadek "dokładnie tyle wolnych" w teście tego pilnuje.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s07 2 3`

### Rozwiązanie i uzasadnienie

`step3/BookingGate`: czysta funkcja decyzji z guard clauses i jedno miejsce efektu ubocznego. Priorytet warunków identyczny jak w start.

### Pułapki

- Guard clause w metodzie z efektem na końcu (audyt, log, zwolnienie zasobu) - wczesny `return` go omija. Zasoby: `try/finally` albo deklaracja `using`.
- Przestawienie warunków "od najczęstszego" - zmienia priorytet błędów.
- Błędne zaprzeczenie przy odwracaniu (`<=` na `>=`). W JavaScripcie dochodzi `NaN`: `!(a <= b)` nie jest tym samym co `a > b`, gdy któraś liczba to `NaN` - obie wersje dają wtedy `false`.

### Pytanie do sali

Gdyby `customerBlocked` był getterem robiącym zapytanie do bazy (albo zwracał `Promise`), czy wolno przenieść go na początek?

## Scena s08. Introduce Design by Contract Checks - pula miejsc

**Temat ze slajdów:** 8. Introduce Design by Contract Checks; Design by Contract - jawne kontrole
**Katalog:** `typescript/src/workshop/m7/s08_designbycontract` · **Test:** `scripts/warsztat.sh --lang ts test m7/s08`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `SeatPool` przyjmuje każde wywołanie, więc `reserve(-2)` po cichu dodaje miejsca, a nadmiarowe `release` daje więcej wolnych miejsc, niż ma sala. Dodajemy jawne warunki wstępne przez `Contracts.require`, a potem warunki końcowe i niezmiennik.

**Zasada:** Design by Contract rozróżnia warunek wstępny (obowiązek klienta), warunek końcowy (gwarancję operacji) i niezmiennik (właściwość prawdziwą między operacjami). Kontrole piszemy jawnie, a nie przez `console.assert`, który tylko wypisuje komunikat, i stawiamy je przed pierwszą mutacją. Odpowiedź `false` przy braku miejsc to poprawny wynik, a nie naruszenie kontraktu.

**Efekt:** Zachowanie świadomie się zmienia dla niepoprawnych wejść: zamiast psuć stan, `SeatPool` rzuca wyjątek i zostaje nietknięty, więc krok 1 nie jest refaktoryzacją. Dla poprawnych wywołań nic się nie zmienia, a warunki końcowe i niezmiennik chronią przyszłe zmiany.

**Różnica względem Javy:** w Javie argumentem przeciw `assert` jest to, że działa tylko z `-ea`. W TypeScripcie odpowiednikiem jest `console.assert`, który przy fałszywym warunku tylko wypisuje komunikat i nie przerywa działania - komentarz w `Contracts` porównuje jawne kontrole właśnie z nim. `Contracts.require` rzuca `IllegalArgumentError`, `Contracts.ensure` - `IllegalStateError` (klasy z `src/shared/errors.ts`), więc w oczekiwanych tekstach jest `IllegalArgumentError:`. Pola `remaining`/`capacity` mają w klasie nazwy `remainingSeats`/`seatCapacity`, bo metody `remaining()` i `capacity()` zostają.

### Co widzimy

`SeatPool` pilnuje liczby wolnych miejsc seansu. Nie ma żadnych kontraktów: `reserve(-2)` po cichu dodaje dwa miejsca, a `release(15)` po rezerwacji 10 daje 105 wolnych w sali na 100. Test `startSilentlyCorruptsStateForInvalidInput` dokumentuje te niemożliwe stany.

```typescript
reserve(seats: number): boolean {
  if (seats > this.remainingSeats) {
    return false;
  }
  this.remainingSeats = this.remainingSeats - seats;
  return true;
}
```

Uwaga: `false` przy braku miejsc to **poprawna, udokumentowana odpowiedź**, nie naruszenie kontraktu.

### Krok 1: warunki wstępne (to nie jest refaktoryzacja)

**W IDE:** utwórz `Contracts.ts` z klasą `Contracts` i metodami statycznymi `require` (`IllegalArgumentError`) i `ensure` (`IllegalStateError`). Na początku `reserve`, `release` i konstruktora dodaj `Contracts.require(...)` - **przed pierwszą mutacją**.
**Po:**

```typescript
release(seats: number): void {
  Contracts.require(seats > 0, 'seats must be positive');
  Contracts.require(seats <= this.seatCapacity - this.remainingSeats, 'cannot release more seats than reserved');
  this.remainingSeats = this.remainingSeats + seats;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s08` - 11 testów zielonych: poprawne użycia bez zmian, niepoprawne rzucają wyjątek i nie zmieniają stanu ("zostalo 100", "zostalo 90").
**Co powiedzieć:** dla niepoprawnych wejść zachowanie się zmieniło - dlatego test ma dla nich osobne oczekiwania. Nie zamieniamy `false` na wyjątek: to zmieniłoby kontrakt także dla poprawnych klientów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s08 0 1`

### Krok 2: warunki końcowe i niezmiennik

**W IDE:** ręcznie: zapamiętaj `previous`, policz `next`, `this.checkInvariant(next)` przed przypisaniem, po przypisaniu `Contracts.ensure(...)`.
**Po:**

```typescript
const previous = this.remainingSeats;
const next = previous - seats;
this.checkInvariant(next);
this.remainingSeats = next;
Contracts.ensure(this.remainingSeats === previous - seats, 'reserve must reduce remaining by seats');
```

**Uruchom:** test zielony - dla poprawnych wejść nic się nie zmienia.
**Co powiedzieć:** przy obecnym kodzie te kontrole nie mogą się nie udać. Chronią przyszłe zmiany: ktoś dopisze rabat "2 za 1" i niezmiennik złapie błąd, zanim stan się zepsuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s08 1 2`

### Rozwiązanie i uzasadnienie

`step2/SeatPool`: `require` przed mutacją, niezmiennik sprawdzany dla kandydata przed przypisaniem, `ensure` po operacji. Jawne kontrole zamiast `console.assert`.

### Pułapki

- `console.assert(seats > 0)` - przy fałszywym warunku tylko wypisuje komunikat i metoda działa dalej. Test zielony, produkcja bez ochrony. (`assert` z `node:assert` rzuca zawsze, ale nie działa w przeglądarce i bywa wycinany przez bundlery jako kod deweloperski.)
- Kontrola po mutacji - wyjątek leci, ale stan już jest zepsuty.
- Zamiana historycznego `IllegalArgumentError` na własny błąd "przy okazji" - klienci mogą go rozpoznawać przez `instanceof`.
- `number` to nie `int`: `reserve(2.5)` i `reserve(NaN)` - `NaN > 0` jest `false`, więc warunek wstępny go odrzuci, ale `2.5` przejdzie. Jeśli kontrakt mówi o liczbie całkowitej, warunek musi to powiedzieć (`Number.isInteger(seats)`).
- Podklasa wzmacniająca warunek wstępny (np. "maks. 6 miejsc naraz") łamie LSP.

### Pytanie do sali

Czy "nie więcej miejsc, niż zostało" to warunek wstępny, czy zwykła odpowiedź `false`? Kto o tym decyduje?

## Scena s09. Remove Double Negative - wstęp do strefy VIP

**Temat ze slajdów:** 9. Remove Double Negative
**Katalog:** `typescript/src/workshop/m7/s09_doublenegative` · **Test:** `scripts/warsztat.sh --lang ts test m7/s09`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `LoungeAccess` sprawdza `!customer.notVip` i `!voucher.isNotExpired(today)`, więc każdy warunek trzeba odwracać w głowie. Dodajemy pozytywne predykaty delegujące, migrujemy użycia po jednym i na końcu odwracamy delegację, zmieniając pole `Customer` na `vip`.

**Zasada:** Remove Double Negative zastępuje negatywną nazwę pozytywną, która musi być dokładnym logicznym dopełnieniem starej, także na granicy i dla `null`/`undefined`. Bezpieczna sekwencja to predykat pozytywny delegujący, migracja po jednym użyciu i odwrócenie delegacji. Negatywna nazwa w bazie, JSON-ie czy konfiguracji wymaga osobnej migracji granicy.

**Efekt:** Warunki czytają się wprost: `customer.vip` i `voucher.isExpired(today)`. Kosztem jest zmiana pola konstruktora `Customer` - każdy, kto go tworzy, musi odwrócić wartość, co widać w adapterze testu.

**Różnica względem Javy:** komponent rekordu `notVip()` to pole `readonly notVip`. Pozytywny predykat `vip` w krokach 1-2 jest getterem (`get vip()`), żeby w kroku 3 pole `vip` mogło go zastąpić bez zmiany wywołań w `LoungeAccess` - w Javie akcesor rekordu też jest metodą `vip()`, więc `LoungeAccess` w krokach 2 i 3 jest identyczny w obu językach.

### Co widzimy

`LoungeAccess` decyduje, czy klient wejdzie do strefy VIP. `Customer` ma pole `notVip`, a `Voucher` predykat `isNotExpired`. Każdy warunek trzeba odwracać w głowie.

```typescript
if (!customer.notVip) {
  return true;
}
...
if (!voucher.isNotExpired(today)) {
  return false;
}
```

### Krok 1: pozytywne predykaty delegujące

**W IDE:** w `Customer` dodaj getter `get vip()` zwracający `!this.notVip`, w `Voucher` `isExpired(today)` zwracające `!this.isNotExpired(today)`. `LoungeAccess` bez zmian.
**Po:**

```typescript
isExpired(today: LocalDate): boolean {
  return !this.isNotExpired(today);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s09` - 20 testów zielonych.
**Co powiedzieć:** pozytywna nazwa musi być dokładnym dopełnieniem starej. Voucher ważny "do dziś włącznie" - test ma przypadek granicy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s09 0 1`

### Krok 2: migracja użyć po jednym

**W IDE:** w `LoungeAccess` zamień `!customer.notVip` na `customer.vip` (w obu metodach) i `!voucher.isNotExpired(today)` na `voucher.isExpired(today)`. Test po każdej zamianie.
**Po:**

```typescript
if (customer.vip) {
  return true;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** mechaniczna zamiana `!negatyw` na `pozytyw` - bez wymyślania logiki od nowa.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s09 1 2`

### Krok 3: odwrócenie delegacji

**W IDE:** w `Voucher` przenieś ciało `isNotExpired` do `isExpired` i uprość `!!` (Inline Method w VS Code nie ma - ręcznie), potem usuń `isNotExpired`. W `Customer` zmiana pola `notVip` na `vip` wymaga odwrócenia wartości u wszystkich, którzy tworzą obiekt - zrób to ręcznie i usuń getter `vip` (teraz to pole).
**Po:**

```typescript
export class Customer {
  constructor(readonly email: string, readonly vip: boolean) {}
}
```

**Uruchom:** test zielony. Zwróć uwagę na adapter w teście: dla `step3` tworzy `new Customer('anna@kino.pl', !v.notVip)`.
**Co powiedzieć:** to jest migracja granicy. Gdyby obiekt trafiał do JSON-a albo bazy, pole `notVip` w danych trzeba by migrować osobno.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s09 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `customer.vip` i `voucher.isExpired(today)`. Sekwencja ze slajdu: predykat pozytywny delegujący, migracja po jednym użyciu, odwrócenie delegacji.

### Pułapki

- Pole opcjonalne nie jest dwustanowe: dla `notVip?: boolean` wartość `undefined` daje `!undefined === true`, czyli klient bez danych staje się VIP-em, a `notVip !== true` daje to samo innym zapisem. Pozytywna wersja musi zachować, co znaczy brak wartości (`undefined`, `null` z JSON-a).
- F2 (Rename Symbol) na parametrze konstruktora `notVip` bez odwrócenia wartości - kompiluje się, a znaczenie jest odwrotne. To samo dotyczy argumentów pozycyjnych `boolean` w wywołaniach `new Customer(...)`.
- "Dopełnienie" z przesuniętą granicą (`isBefore` zamiast `!isAfter`).

### Pytanie do sali

Gdzie w Waszym kodzie negatywna nazwa żyje w kolumnie bazy, w polu JSON albo w pliku konfiguracyjnym?

## Scena s10. Remove Boolean Method Parameters - migracja API

**Temat ze slajdów:** 11. Remove Boolean Method Parameters; Java 25 w tym module (zgodność binarna)
**Katalog:** `typescript/src/workshop/m7/s10_booleanparameter` · **Test:** `scripts/warsztat.sh --lang ts test m7/s10`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketService.book` ma dwie flagi, więc wywołanie `book(..., true, false)` nic nie mówi bez zaglądania do sygnatury. Kanał zamieniamy na jawne metody `bookOnline` i `bookAtBoxOffice`, okulary na enum `Glasses`, migrujemy klientów po jednym i dopiero na końcu usuwamy starą metodę.

**Zasada:** Remove Boolean Method Parameters zastępuje literał `true`/`false`, który steruje zachowaniem, jawną metodą albo typem. Nie każdy `boolean` jest flagą: dana z formularza zostaje daną, a przy wielu flagach zamiast metody na każdą kombinację robimy przegląd odpowiedzialności i obiekt polityki. Stara metoda zostaje jako `@deprecated` i delegująca do końca migracji, bo jej usunięcie łamie klientów, których kompilator projektu nie widzi.

**Efekt:** Wywołania w `MobileApp` i `BoxOfficeTerminal` czytają się bez sygnatury, a przekreślenia `@deprecated` znikają. Usunięcie starej metody w bibliotece publicznej to zmiana łamiąca zgodność, więc wymaga jawnego kryterium końca migracji.

**Różnica względem Javy (zgodność binarna):** w TypeScripcie nie ma zgodności binarnej w sensie JVM (plik `.class` z zapisaną sygnaturą metody), ale jest jej odpowiednik: kod JS skompilowany wcześniej (inny pakiet npm, gotowy bundle) nie przechodzi już przez sprawdzanie typów i woła API po nazwie i pozycji argumentów. Usunięcie `book(..., boolean, boolean)` w kroku 4 daje klientom TS błąd kompilacji, a skompilowanemu JS dopiero `TypeError: ... book is not a function` w czasie działania. Pokazuje to dodatkowy test tylko w TS, `precompiledJsClientBreaksOnlyAfterSafeDelete`: "klient JS" wołający `book('Kraina Lodu', '3D', 2, true, false)` bez typów działa na start..step3 i rzuca `TypeError` na step4. Ostrzeżenia `[deprecation]` z Javy to w VS Code przekreślone wywołania metody z `/** @deprecated */` (sam `tsc` ich nie zgłasza, robi to edytor albo reguła `@typescript-eslint/no-deprecated`). Prywatne przeciążenie Javy `book(..., Channel, ...)` ma w TS nazwę `bookInChannel`, a `Channel` to nieeksportowany enum modułu.

### Co widzimy

`TicketService.book(title, format, seats, online, ownGlasses)` ma dwie flagi. Klienci: `MobileApp` woła `book(..., true, false)`, `BoxOfficeTerminal` woła `book(..., false, customerHasGlasses)`. Z miejsca wywołania nie widać, co znaczy `true`.

```typescript
return this.tickets.book(title, format, seats, true, false);
```

`customerHasGlasses` w terminalu to dana z formularza, a nie flaga sterująca. Nie każdy `boolean` jest zapachem.

### Krok 1: jawne metody dla kanału, stara metoda @deprecated

**W IDE:** dodaj `bookOnline(...)` i `bookAtBoxOffice(...)` oraz prywatne `bookInChannel(..., channel: Channel, ...)` z enumem `Channel` na poziomie modułu (bez `export`). Stara publiczna metoda dostaje `/** @deprecated ... */` i deleguje.
**Po:**

```typescript
/**
 * @deprecated użyj {@link TicketService.bookOnline} albo {@link TicketService.bookAtBoxOffice}
 */
book(title: string, format: string, seats: number, online: boolean, ownGlasses: boolean): string {
  return online
    ? this.bookOnline(title, format, seats, ownGlasses)
    : this.bookAtBoxOffice(title, format, seats, ownGlasses);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s10` - 32 testy zielone. VS Code przekreśla wywołania `book` w obu klientach.
**Co powiedzieć:** przekreślenia to lista klientów do migracji. Stara metoda zostaje, bo skompilowany wcześniej kod JS innego pakietu woła ją po nazwie - to nasz odpowiednik zgodności binarnej.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s10 0 1`

### Krok 2: druga flaga jako typ

**W IDE:** utwórz `Glasses.ts` z `export enum Glasses { OWN = 'OWN', RENTED = 'RENTED' }`. Change Signature w VS Code nie ma, więc w `bookOnline` i `bookAtBoxOffice` ręcznie zmień typ parametru z `boolean` na `Glasses` (i nazwę na `glasses`); `npm run typecheck` pokaże miejsca do poprawy. Stara metoda tłumaczy `boolean` na `Glasses`.
**Po:**

```typescript
bookOnline(title: string, format: string, seats: number, glasses: Glasses): string
```

**Uruchom:** test zielony.
**Co powiedzieć:** nie robimy czterech metod na każdą kombinację (`bookOnlineWithOwnGlasses`...). Kanał jest metodą, okulary są wartością. Przy trzech i więcej flagach - obiekt polityki i przegląd odpowiedzialności; idiomatycznie w TypeScripcie często obiekt opcji, np. `bookOnline(title, format, seats, { glasses: 'RENTED' })`. Tu zostajemy przy enumie, żeby diff pokazywał ten sam ruch co w Javie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s10 1 2`

### Krok 3: migracja klientów

**W IDE:** w `MobileApp` zamień wywołanie na `bookOnline(title, format, seats, Glasses.RENTED)`. Test. W `BoxOfficeTerminal` - `bookAtBoxOffice(...)` z tłumaczeniem `customerHasGlasses` na `Glasses`. Test.
**Po:**

```typescript
return this.tickets.bookOnline(title, format, seats, Glasses.RENTED);
```

**Uruchom:** test zielony, przekreśleń `@deprecated` w klientach już nie ma.
**Co powiedzieć:** jeden klient naraz, test po każdym. Wywołanie czyta się bez zaglądania do sygnatury.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s10 2 3`

### Krok 4: usunięcie starej metody

**W IDE:** Safe Delete w VS Code nie ma - na starym `book(...)` Find All References (⇧⌥F12) potwierdza brak użyć w kodzie produkcyjnym, potem usuń metodę i uruchom `npm run typecheck`.
**Po:**

```typescript
bookOnline(title: string, format: string, seats: number, glasses: Glasses): string { ... }
bookAtBoxOffice(title: string, format: string, seats: number, glasses: Glasses): string { ... }
```

**Uruchom:** test zielony (testy starego API obejmują tylko start..step3, a `precompiledJsClientBreaksOnlyAfterSafeDelete` pokazuje, że skompilowany klient JS dostaje teraz `TypeError`).
**Co powiedzieć:** koniec okresu przejściowego to decyzja z jawnym kryterium ("wszyscy klienci zmigrowani, wydanie X"). Find All References widzi tylko ten projekt - w bibliotece publicznej to zmiana łamiąca zgodność (nowa wersja major w semver).
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s10 3 4`

### Rozwiązanie i uzasadnienie

`step4/TicketService`: dwie jawne metody i enum. Migracja wg slajdu: jawne metody, klienci pojedynczo, stara metoda delegująca do końca migracji, usunięcie.

### Pułapki

- Usunięcie starej metody w tym samym commicie co dodanie nowych.
- Zamiana każdego `boolean` na enum, także danych z formularza.
- Metoda na każdą kombinację flag zamiast przeglądu odpowiedzialności.
- Poleganie na tym, że "kompilator pokaże wszystkich klientów" - kod JS spoza projektu, wywołania dynamiczne (`service[name](...)`) i testy pisane w JS są dla `tsc` niewidoczne.

### Pytanie do sali

Jak długo trzymać `@deprecated` w pakiecie używanym przez inne zespoły? Kto mierzy, czy ktoś go jeszcze woła?

## Scena s11. Remove Middle Man - fasada kina

**Temat ze slajdów:** 12. Remove Middle Man
**Katalog:** `typescript/src/workshop/m7/s11_middleman` · **Test:** `scripts/warsztat.sh --lang ts test m7/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CinemaFacade` prawie wszystko deleguje 1:1 do `ScreeningCatalog`, ale w `freeSeats` po cichu zamienia nieznany seans na 0 wolnych miejsc. Przenosimy to tłumaczenie do `SeatBadge`, migrujemy obu klientów do katalogu po jednym i usuwamy fasadę.

**Zasada:** Remove Middle Man usuwa pośrednika, który tylko przekazuje wywołania dalej, i pozwala klientom rozmawiać bezpośrednio z właściwym obiektem - ruch odwrotny to Hide Delegate. Najpierw sprawdzamy, czy pośrednik nie robi czegoś więcej (autoryzacja, transakcja, telemetria, retry, translacja błędów), bo fasada modułu czy seam testowy mogą być wartościowe.

**Efekt:** Klienci zależą bezpośrednio od `ScreeningCatalog`, a zachowanie "nieznany seans = WYPRZEDANE" zostało zachowane w jedynym miejscu, które go potrzebuje. To dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

**Różnica względem Javy:** `NoSuchElementException` nie ma odpowiednika we wspólnym `src/shared/errors.ts`, więc `NoSuchElementError` jest zdefiniowany i eksportowany w kontrakcie sceny `ScreeningCatalog.ts`. TypeScript nie ma `catch` po typie, więc `catch (NoSuchElementException e)` to `catch (error)` z `instanceof NoSuchElementError`, a inne błędy są rzucane dalej. `LinkedHashMap` to `Map` (też zachowuje kolejność wstawiania).

### Co widzimy

`CinemaFacade` deleguje 1:1 do `ScreeningCatalog` - prawie. `freeSeats` po cichu tłumaczy `NoSuchElementError` na 0, więc `SeatBadge` pokazuje "WYPRZEDANE" dla nieznanego seansu. Dwóch klientów: `SeatBadge` i `DailyBoard`.

```typescript
freeSeats(id: string): number {
  try {
    return this.catalog.freeSeats(id);
  } catch (error) {
    if (error instanceof NoSuchElementError) {
      return 0;
    }
    throw error;
  }
}
```

### Krok 1: pośrednik staje się czystym forwarderem

**W IDE:** przenieś `try/catch` do prywatnej metody `freeSeats` w `SeatBadge` (jedyny klient, który polega na tłumaczeniu). W fasadzie zostaje zwykłe delegowanie.
**Po:**

```typescript
// SeatBadge
private freeSeats(id: string): number {
  try {
    return this.cinema.freeSeats(id);
  } catch (error) {
    if (error instanceof NoSuchElementError) {
      return 0;
    }
    throw error;
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s11` - 13 testów zielonych, przypadek "nieznany seans wyglada jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s11 0 1`

### Krok 2: pierwszy klient rozmawia z katalogiem

**W IDE:** w `SeatBadge` ręcznie zmień typ parametru konstruktora z `CinemaFacade` na `ScreeningCatalog`, a pole F2 (Rename Symbol) na `catalog`. Metody `title`, `format` i `freeSeats` katalog ma o tych samych sygnaturach, więc reszta kodu się kompiluje.
**Po:**

```typescript
constructor(catalog: ScreeningCatalog) {
  this.catalog = requireNonNull(catalog, 'catalog');
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s11 1 2`

### Krok 3: ostatni klient i usunięcie fasady

**W IDE:** ten sam ruch w `DailyBoard` (`cinema.screenings()` na `catalog.all()`), potem Find All References (⇧⌥F12) na `CinemaFacade` - brak użyć - i usunięcie pliku.
**Po:**

```typescript
return this.catalog.all()
  .map((screening) => `${screening.id} ${screening.title} ${screening.format}`)
  .join('\n');
```

**Uruchom:** test zielony.
**Co powiedzieć:** odwrotny ruch to Hide Delegate. Fasada modułu albo seam testowy mogą być wartościowe - usuwamy pośrednika, który nic nie wnosi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s11 2 3`

### Rozwiązanie i uzasadnienie

`step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `catalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Inline wszystkich metod fasady naraz (ręcznie albo wyszukaj-i-zamień `cinema.` na `catalog.`) bez sprawdzenia, że któraś tłumaczy błędy - kompilator nic nie zgłosi, bo sygnatury są identyczne.
- Usunięcie fasady, która jest granicą modułu (klienci z innych katalogów zaczną zależeć od szczegółów - w TS nie ma widoczności pakietowej, która by ich zatrzymała, co najwyżej pole `exports` w `package.json`).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s12. Return ASAP - wyszukiwanie wolnego miejsca

**Temat ze slajdów:** 13. Return ASAP
**Katalog:** `typescript/src/workshop/m7/s12_returnasap` · **Test:** `scripts/warsztat.sh --lang ts test m7/s12`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `SeatFinder` trzyma się zasady jednego wyjścia: `seatClass` ma zagnieżdżone `if` ze zmienną `result`, a `firstFree` pętlę z flagą `found`. Zamieniamy je na guard clauses i `return` w miejscu, gdzie wynik jest już znany.

**Zasada:** Return ASAP każe zwracać wynik tam, gdzie jest ostateczny, zamiast nieść go w zmiennej i fladze do końca metody. Wczesny `return` nie może jednak ominąć wymaganej mutacji ani zmienić sposobu dostępu do danych, a `return` w `try` nadal uruchamia `finally`.

**Efekt:** Obie metody nie mają flag ani zmiennych wyniku, a licznik `inspected` jest taki sam, bo `return` stoi po inkrementacji. Celowo zostają `length` i `seats[index]`, bo `for...of` (iterator) albo `find` zmieniłyby sposób czytania listy.

**Różnica względem Javy:** `Optional<String>` to `string | undefined`; test formatuje wynik jak `Optional.toString()` w Javie (`Optional[B10]`, `Optional.empty`), więc oczekiwania są identyczne. Pole licznika nazywa się `inspectedCount`, bo metoda `inspected()` zostaje. Odpowiednikiem `size()`/`get(index)` kontra iterator z Javy jest `length`/`[index]` kontra `for...of`.

### Co widzimy

`SeatFinder` ma dwie metody w stylu "jeden punkt wyjścia": `seatClass` z zagnieżdżonymi `if` i zmienną `result`, oraz `firstFree` z pętlą `while (!found && index < seats.length)`. Licznik `inspected` (metryka dla działu IT) jest efektem ubocznym, który musi przetrwać zmianę.

```typescript
while (!found && index < seats.length) {
  const seat = seats[index]!;
  this.inspectedCount++;
  if (seat.row >= minRow && !seat.taken) {
    result = seat.label;
    found = true;
  }
  index++;
}
```

### Krok 1: guard clauses

**W IDE:** w `seatClass` ręcznie odwróć zewnętrzne `if` (VS Code nie ma Invert 'if'), zamień przypisania `result = ...` na `return ...`, na końcu usuń `result`. W `firstFree` guard `if (seats === null) return undefined;` zamiast otaczającego `if`.
**Po:**

```typescript
if (seat === null) { return 'BRAK'; }
if (seat.taken) { return 'ZAJETE'; }
if (seat.row >= vipFromRow) { return 'VIP'; }
return 'STANDARD';
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s12` - 12 testów zielonych.
**Co powiedzieć:** kolejność sprawdzeń zachowana: miejsce zajęte w rzędzie VIP to nadal "ZAJETE".
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s12 0 1`

### Krok 2: Return ASAP w pętli

**W IDE:** zamień `result = seat.label; found = true;` na `return seat.label;`, usuń `found` i `result`, ręcznie zamień `while` na `for` z licznikiem `index`. Ostatnia linia `return undefined;`.
**Po:**

```typescript
for (let index = 0; index < seats.length; index++) {
  const seat = seats[index]!;
  this.inspectedCount++;
  if (seat.row >= minRow && !seat.taken) {
    return seat.label;
  }
}
return undefined;
```

**Uruchom:** test zielony - "sprawdzono=3" dla pierwszego wolnego VIP.
**Co powiedzieć:** zwracamy tam, gdzie wynik jest ostateczny, ale **po** `inspectedCount++`. Gdyby `return` trafił przed inkrementację, licznik różniłby się o jeden i test by to złapał.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s12 1 2`

### Rozwiązanie i uzasadnienie

`step2/SeatFinder`: bez flag i zmiennych wyniku, `length`/`[index]` zachowane (`for...of` przeszedłby przez iterator, co zmienia sposób dostępu np. dla obiektu tablicopodobnego, `Proxy` z licznikiem odczytów albo tablicy z dziurami).

### Pułapki

- `return` w `try` uruchamia `finally`, a `return` w `finally` zastępuje wynik (i połyka wyjątek).
- Wczesny `return` przed wymaganą mutacją (licznik, log, zwolnienie blokady).
- "Przy okazji" zamiana na `seats.find(...)` - licznik trzeba by liczyć efektem ubocznym w predykacie, inny sposób iteracji i inne zachowanie dla dziur i `undefined` w tablicy.

### Pytanie do sali

Czy reguła "jeden return na metodę" ma jeszcze sens w TypeScripcie z `try/finally` i deklaracją `using`?

## Scena s13. Remove God Class - kampania na kopii CinemaManager

**Temat ze slajdów:** 10. Remove God Classes - kampania, nie pojedynczy ruch; Remove God Classes - wydzielony fragment i ryzyka; Warsztat 3 (kontekst)
**Katalog:** `typescript/src/workshop/m7/s13_godclass` · **Test:** `scripts/warsztat.sh --lang ts test m7/s13`
**Czas:** ~30 min

### W skrócie

**Co robimy:** Kopia `CinemaManager` to niemal 300 linii z cennikiem, powiadomieniami, rezerwacjami w `unknown[]` i raportami w jednej klasie. Prowadzimy kampanię czterech pionowych wycinków: `PricingService`, `NotificationService`, `Booking` z `BookingRepository` i `ReportService`, z golden masterem po każdym kroku.

**Zasada:** God Class to klasa o niskiej spójności, wielu powodach zmiany i roli centralnego węzła zależności, a nie po prostu długi plik. Usuwamy ją kampanią małych Extract Class i Move Method w obszarze bieżącej zmiany: stara klasa zostaje fasadą, a stan dostaje jednego właściciela. To nie jest przepisanie od nowa.

**Efekt:** Publiczne API `CinemaManager` i pełny wektor zachowania (maile, SMS-y, bramka, raporty) są identyczne, a dane rezerwacji mają nazwany typ. Świadomie zostają kwoty w `number`, globalny magazyn `LegacyDb` i brak atomowości - to osobne decyzje na kolejne kroki.

**Różnica względem Javy:** start to kopia TypeScriptowej wersji legacy (`src/workshop/legacy/*.ts`), więc wiersze rezerwacji to `unknown[]` z rzutowaniami `as number`, `as string` zamiast `Object[]` z rzutowaniami Javy, a kwoty to `number` zamiast `double`. Każdy krok ma własne kopie `LegacyDb`, `LegacyMailer` i `LegacyPaymentGateway` (osobny stan statyczny). `Booking` ma pola `readonly`, mutowalne pole `status` (w Javie `status()` / `status(int)`) i getter `card` ustawiany przez `markPaid(card)`. `BookingRepository.find` zwraca `Booking | undefined` (w Javie `null`), a `all()` zwraca `Iterable<Booking>`. Klasy wydzielone są eksportowane, bo w TS nie ma widoczności pakietowej.

### Co widzimy

`start/CinemaManager` to kopia legacy (razem z `LegacyDb`, `LegacyMailer`, `LegacyPaymentGateway`) - 289 linii, dane w `unknown[]` z magicznymi indeksami, cennik, płatności, zwroty, lojalność, powiadomienia, raporty i rozliczenia w jednej klasie. Zanim cokolwiek ruszysz, pokaż mapę: metody, pola `LegacyDb`, efekty (mail, SMS, bramka).

```typescript
LegacyDb.BOOKINGS.set(id, [screeningId, email, phone,
  seats, types, web, total, 0, CinemaManager.clock(), null, sum]);
...
const points = Math.trunc((b[10] as number) / 10);
```

Ochrona: `S13GoldenMasterTest` puszcza ten sam scenariusz "jednego dnia kina" co golden master legacy (`S13Script.ts`, ten sam scenariusz co `test/workshop/legacy/CinemaManagerScript.ts`) i porównuje **pełny wektor**: wyniki wywołań, maile, SMS-y, operacje bramki, raport dzienny i rozliczenia. Oczekiwany tekst to kopia `src/test/resources/workshop/cinema-manager.approved.txt` (ten sam plik zatwierdza wersję Javy). Każdy wariant ma adapter `Driver` w katalogu testowym (`test/workshop/m7/s13_godclass/{start..step4}/Driver.ts`, interfejs `CinemaUnderTest`) z dostępem do haka `CinemaManager.clock` i globalnego stanu.

### Krok 1: wycinek "cennik" - PricingService

**W IDE:** w `book` zaznacz pętlę cen i rabat grupowy razem z zaokrągleniem, ⌃⇧R > Extract to method in class 'CinemaManager', nazwa `ticketsSum`. Change Signature w VS Code nie ma, więc ręcznie zamień parametr `s: unknown[]` na trzy wartości (`format`, `start`, `vipFromRow`) i popraw wywołanie. Wydziel `bookingFee`. Obie metody wytnij do nowej klasy `PricingService` w pliku `PricingService.ts`, w `CinemaManager` pole `pricing`.
**Po:**

```typescript
const sum = this.pricing.ticketsSum(s[1] as number, s[2] as LocalDateTime, s[5] as number,
  seats, types, ownGlasses);
const total = sum + this.pricing.bookingFee(web, seats.length);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s13` - golden master zielony dla start i step1 (test ma 5 przypadków, po jednym na wariant start..step4).
**Co powiedzieć:** cennik ma jednego właściciela i nie zna układu `unknown[]`. Arytmetyka `number` przeniesiona dosłownie - naprawa typu pieniędzy to osobna decyzja (scena s14).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s13 0 1`

### Krok 2: wycinek "powiadomienia" - NotificationService

**W IDE:** każde wywołanie `LegacyMailer.send/sms` wydziel (⌃⇧R > Extract to method in class) do metody o nazwie zdarzenia: `bookingCreated`, `paymentDeclined`, `ticketsPaid` (mail i SMS razem), `bookingExpired`, `bookingCancelled`. Wytnij je do klasy `NotificationService`. Funkcję `fmt` przenieś do `Formats.amount` (klasa statyczna w `Formats.ts`), w `CinemaManager` zostaw delegację.
**Po:**

```typescript
this.notifications.ticketsPaid(email, b[2] as string | null, bookingId, b[6] as number, points);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `CinemaManager` decyduje KIEDY powiadomić, `NotificationService` - CO i JAK. Kolejność efektów (mail przed SMS, powiadomienie po zapisie rezerwacji) jest częścią wektora - golden master pilnuje jej co do linii.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s13 1 2`

### Krok 3: wycinek "rezerwacje" - Booking i BookingRepository zamiast unknown[]

**W IDE:** utwórz klasę `Booking` z nazwanymi polami (status zostaje kodem liczbowym), `BookingRepository` z `nextId`, `save`, `find`, `all`. Zmień typ mapy `LegacyDb.BOOKINGS` na `Map<string, Booking>`, uruchom `npm run typecheck` i napraw błędy jeden po drugim: `b[7]` na `b.status`, `b[10]` na `b.ticketsSum`...
**Po:**

```typescript
const b = this.bookings.find(bookingId);
...
b.markPaid(card);
const points = Math.trunc(b.ticketsSum / 10);
```

**Uruchom:** test zielony.
**Co powiedzieć:** kompilator prowadzi tę migrację - każdy magiczny indeks staje się błędem do naprawy (to działa tylko przy włączonym `strict`/`noImplicitAny`; bez nich `b[7]` na obiekcie cicho da `any`). Magazyn zostaje globalny (`LegacyDb`), więc współdzielenie stanu między instancjami się nie zmienia. Przeniesienie mapy do instancji repozytorium zmieniłoby semantykę - to osobny krok.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s13 2 3`

### Krok 4: wycinek "raporty" - ReportService

**W IDE:** metody `dailyReport` i `settlement` wytnij do nowej klasy `ReportService` z zależnością `BookingRepository` (VS Code nie przenosi metod między klasami). W `CinemaManager` zostają metody delegujące (stare API).
**Po:**

```typescript
dailyReport(day: LocalDate): string {
  return this.reports.dailyReport(day);
}

settlement(title: string, week: number): string {
  return this.reports.settlement(title, week);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** raporty czytają rezerwacje przez repozytorium, więc zmiana magazynu nie dotknie raportów. Kampanię kontynuujemy (zwroty, lojalność, repertuar), dopóki przynosi wartość dla planowanych zmian - nie przepisujemy wszystkiego.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s13 3 4`

### Rozwiązanie i uzasadnienie

`step4`: `CinemaManager` jest fasadą o niezmienionym publicznym API, cztery wydzielone klasy mają po jednym powodzie zmiany, a dane rezerwacji mają nazwany typ i jednego właściciela dostępu. Cztery kroki, cztery commity, po każdym identyczny wektor zachowania.

### Pułapki

- "Przepiszmy to od nowa" zamiast pionowych wycinków - brak punktu, w którym system jest zielony.
- Zmiana kolejności efektów przy wydzielaniu (np. SMS przed mailem, mail przed zapisem rezerwacji) - golden master pokazuje różnicę w jednej linii.
- Poprawianie błędów "przy okazji" (np. `number` na `Decimal`, `!=` na `!==`) w kroku strukturalnym - `b == null` łapie też `undefined`, `b === null` już nie.
- Przeniesienie stanu do instancji usługi: zmiana współdzielenia i czasu życia.
- Transakcje: przykład nie zapewnia atomowości - wyjątek w `NotificationService` po zapisie rezerwacji zostawia rezerwację bez maila. To było tak samo przed zmianą, ale po wydzieleniu łatwiej to przeoczyć. Atomowość (transakcja, outbox, kompensacja) to osobna decyzja.

### Pytanie do sali

Który wycinek zrobilibyście jako następny, jeśli za tydzień dochodzi nowa zasada zwrotów?

## Scena s14. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** Refaktoryzacja a zmiana kontraktu; Pętla pracy (zmiany kontraktu w osobnych krokach)
**Katalog:** `typescript/src/workshop/m7/s14_contractchange` · **Test:** `scripts/warsztat.sh --lang ts test m7/s14`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundCalculator.refund` liczy zwrot na `number` (binarny zmiennoprzecinkowy, jak `double`), a kontraktem jest też format z dwoma miejscami po przecinku. Robimy jedną czystą refaktoryzację, potem pokazujemy naiwne przejście na `Decimal`, które zmienia dwie rzeczy naraz, i na koniec zostawiamy tylko uzgodnioną zmianę.

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy, a kryterium brzmi: klient nie dostrzega żadnej nieuzgodnionej różnicy. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu, którą robimy i zatwierdzamy osobno od ruchów strukturalnych, a nie "przy okazji".

**Efekt:** Zachowanie świadomie się zmienia w jednym punkcie: 64.35 anulowane 2 godziny przed seansem daje 29.18 zamiast 29.17, zatwierdzone w osobnym commicie. Format "0.00" zostaje, a naiwny krok 2 nie powinien trafić do repozytorium.

**Różnica względem Javy:** tytuł sceny zostaje (to temat ze slajdów), ale w porcie `BigDecimal` to `Decimal` z decimal.js. Pułapka formatu w kroku 2 musiała dostać inną postać. W Javie `BigDecimal.ZERO` drukuje się jako `"0"`, a pozostałe kwoty zachowują skalę 2; decimal.js nie przechowuje skali, więc `toString()` psułby także zwykłe zwroty. Dlatego w TS naiwny krok 2 robi "przy okazji" dwie rzeczy: przejście `number` -> `Decimal` (zaokrąglenie HALF_UP na dokładnej wartości 29.175 -> 29.18) i krótsze obcięcie do zera guard clause `if (refund.lessThanOrEqualTo(0)) return '0';` - zero wychodzi jako `"0"` zamiast `"0.00"`. Krok 3 wraca do jednej ścieżki: `Decimal.max(..., 0).toFixed(2, Decimal.ROUND_HALF_UP)`. Oczekiwania testu są identyczne jak w Javie, zmienił się tylko komunikat asercji (`"format: '0' zamiast 0.00"`).

### Co widzimy

`RefundCalculator.refund` liczy zwrot na `number` - dokładnie jak w legacy: 100%, 50% albo 0%, minus 3.00, nie poniżej zera, zaokrąglenie `Math.round(x * 100) / 100.0`, format `toFixed(2)`. Kontraktem jest także **format**: zawsze dwa miejsca po przecinku.

```typescript
refund = refund - 3.00;
if (refund < 0) {
  refund = 0;
}
refund = Math.round(refund * 100) / 100.0;
return refund.toFixed(2);
```

### Krok 1: czysta refaktoryzacja

**W IDE:** zaznacz łańcuch `if/else` wyboru udziału, ⌃⇧R > Extract to method in class, nazwa `share`, zwraca `number` (0, 1.0, 0.5) - ręcznie zamień przypisania na `return` i oznacz metodę `private static`. W `refund` jedno wyrażenie `ticketsPaid * share(minutes) - 3.00`.
**Po:**

```typescript
let refund = ticketsPaid * RefundCalculator.share(minutesBeforeStart) - 3.00;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s14` - 19 testów zielonych, łącznie z przypadkami brzegowymi (29.17 i 0.00).
**Co powiedzieć:** `x * 1.0 === x` w arytmetyce zmiennoprzecinkowej - wynik jest identyczny co do bitu. To jest refaktoryzacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s14 0 1`

### Krok 2: "przy okazji" number na Decimal

**W IDE:** pokaż naiwny ruch: `share` zwraca `Decimal`, liczymy `new Decimal(ticketsPaid).times(...).minus(FEE)`, a "przy okazji" skracamy obcięcie do zera do guard clause.
**Po:**

```typescript
const refund = new Decimal(ticketsPaid)
  .times(RefundCalculator.share(minutesBeforeStart))
  .minus(RefundCalculator.FEE);
if (refund.lessThanOrEqualTo(0)) {
  return '0';
}
return refund.toFixed(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zwykłych zwrotów zielony, ale `byTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s14 1 2`

### Krok 3: świadoma decyzja

**W IDE:** zastąp guard clause obcięciem `Decimal.max(..., 0)` przed formatowaniem - jedna ścieżka wyniku i format "0.00" wraca. Różnicę groszową zatwierdzamy jako zmianę kontraktu: komentarz TSDoc z decyzją, nowe oczekiwanie w teście, osobny commit.
**Po:**

```typescript
const refund = Decimal.max(
  new Decimal(ticketsPaid)
    .times(RefundCalculator.share(minutesBeforeStart))
    .minus(RefundCalculator.FEE),
  0);
return refund.toFixed(2, Decimal.ROUND_HALF_UP);
```

**Uruchom:** test zielony: `deliberateStepChangesOnlyTheApprovedRounding`.
**Co powiedzieć:** kryterium refaktoryzacji to "klient nie dostrzega żadnej **nieuzgodnionej** różnicy". Różnicę w groszach uzgodniliśmy, różnicy w formacie nie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s14 2 3`

### Rozwiązanie i uzasadnienie

`step3/RefundCalculator`: `Decimal` i HALF_UP zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `number` i `Decimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- `Number.prototype.toFixed` i `Math.round(x * 100) / 100` działają na rozwinięciu binarnym: 29.175 to w pamięci 29.17499..., stąd 29.17. Ta sama pułapka co `new BigDecimal(double)` w Javie, tylko ukryta w "zwykłym" formatowaniu (`(1.005).toFixed(2)` daje `"1.00"`).
- decimal.js nie przechowuje skali: `new Decimal('0.00').toString()` to `"0"`, a `equals` porównuje tylko wartość. Format kwoty musi być jawny (`toFixed(2, ...)`), inaczej zmienia się przy pierwszym zerze albo okrągłej kwocie.
- `toFixed(2)` na `Decimal` bez trybu zaokrąglenia używa globalnego `Decimal.rounding` - ktoś może go zmienić przez `Decimal.set(...)`.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s15. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** Wektor obserwowalnego zachowania; Lista kontrolna przeglądu (zachowanie)
**Katalog:** `typescript/src/workshop/m7/s15_behaviourvector` · **Test:** `scripts/warsztat.sh --lang ts test m7/s15`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Po "porządkach" kolegi `TicketCheckout.pay` wysyła mail z potwierdzeniem także klientowi z odrzuconą kartą, a test sprawdzający tylko wynik tego nie widzi. Wprowadzamy seam dla maili, naprawiamy regresję i dodajemy seam dla płatności, żeby test widział pełny wektor.

**Zasada:** Wektor obserwowalnego zachowania to wszystko, co klient może zauważyć: wynik, wyjątki (typ, komunikat, moment), stan po sukcesie i po błędzie, wywołania współpracowników z ich kolejnością, czas i granice. Test sprawdzający jeden wymiar przepuści regresję w pozostałych, dlatego granica kontraktu ma być świadomą decyzją, a nie skutkiem słabego testu.

**Efekt:** Test widzi wynik, wyjątek, maile i obciążenia karty w jednym dzienniku oraz status rezerwacji. Zachowanie świadomie się zmienia w kroku 2: przy odrzuconej karcie znika błędny mail o opłaceniu - to naprawa błędu, a nie refaktoryzacja, choć wynik metody pozostaje ten sam.

**Różnica względem Javy:** `@FunctionalInterface Mailer` i `PaymentGateway` to aliasy typów funkcyjnych, więc w teście wystarczy funkcja strzałkowa (albo pole-strzałka `MailLog.add`). Konstruktory z domyślnymi zależnościami (`this(CinemaMailer::send)`) to parametry domyślne. `Objects.requireNonNull(card, "card")` to `requireNonNull(card, 'card')` rzucające `NullPointerError`, więc w pełnym wektorze jest `wyjatek=NullPointerError: card`. `Booking.status()` to getter `status` bez settera - stan zmienia tylko `markPaid()`.

### Co widzimy

`TicketCheckout.pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S15ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

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

Pokaż tabelę ze slajdu: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**W IDE:** utwórz `Mailer.ts` z `export type Mailer = (to: string, text: string) => void;`, dodaj parametr konstruktora z wartością domyślną `(to, text) => CinemaMailer.send(to, text)` i zamień statyczne wywołania na `this.mailer(...)`. Logika bez zmian.
**Po:**

```typescript
constructor(mailer: Mailer = (to, text) => CinemaMailer.send(to, text)) {
  this.mailer = requireNonNull(mailer, 'mailer');
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m7/s15` - 23 testy zielone. `step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona B1" i "Bilety B1 oplacone: 114.00".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s15 0 1`

### Krok 2: naprawa regresji pod ochroną testu

**W IDE:** przenieś wysyłkę potwierdzenia do gałęzi sukcesu, zamień `charged ? 'OK' : 'DECLINED'` na dwa `return`.
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
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s15 1 2`

### Krok 3: seam dla płatności i pełny wektor

**W IDE:** utwórz `PaymentGateway.ts` z `export type PaymentGateway = (card: string, amount: Money) => boolean;`, drugi parametr konstruktora z wartością domyślną `(card, amount) => CardTerminal.charge(card, amount)`. W teście oba fałszywe obiekty zapisują do **jednej** tablicy zdarzeń.
**Po:**

```typescript
constructor(
  mailer: Mailer = (to, text) => CinemaMailer.send(to, text),
  gateway: PaymentGateway = (card, amount) => CardTerminal.charge(card, amount),
) {
```

**Uruchom:** test zielony: `step3ObservesTheFullVector` sprawdza wynik albo wyjątek (nazwa klasy błędu i komunikat), obciążenia i maile w jednej kolejności oraz status rezerwacji po operacji.
**Co powiedzieć:** jeden dziennik dla wszystkich współpracowników pokazuje kolejność: najpierw obciążenie, potem mail. Stan po błędzie (`NEW` po odrzuceniu) i brak efektów przy wyjątku (`wyjatek=NullPointerError: card; zdarzenia=[]`) też są częścią kontraktu.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m7/s15 2 3`

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

## Proponowana kolejność pokazu

**Ścieżka krótka (~85 min):** model bezpieczeństwa i najczęstsze ruchy.

1. s15 Wektor obserwowalnego zachowania, kroki 1 i 3 (~10 min) - ustawia język na cały moduł
2. s01 Break Dependencies, kroki 1, 3 i 4 (~15 min)
3. s02 Extract Method Object (~12 min)
4. s07 Remove Arrowhead, krok 1 i 3 z naiwnym ruchem na żywo (~8 min)
5. s06 Introduce Parameter Object, kroki 1 i 3 (~8 min)
6. s14 Refaktoryzacja a zmiana kontraktu (~10 min)
7. s13 Remove God Class, kroki 1 i 3 (~20 min)

**Ścieżka pełna (~200 min):** kolejność slajdów modułu.

1. Model bezpieczeństwa: s15, s14 (~22 min)
2. Zależności i struktura: s01, s02, s03, s04, s05 (~69 min)
3. Sygnatury i warunki: s06, s07, s08, s09 (~40 min)
4. Klasy i API: s13, s10, s11, s12 (~58 min)

Po s05 i po s09 zrób przerwę na pytania. Sceny s03, s05, s09, s11 i s12 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 7).
