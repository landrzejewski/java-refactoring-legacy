# Moduł 7. Zaawansowane refaktoryzacje - warsztat CineLegacy: przewodnik prowadzącego

Piętnaście scen w domenie kina pokazuje na żywo wszystkie trzynaście refaktoryzacji z modułu 7 oraz dwa tematy przekrojowe: granicę między refaktoryzacją a zmianą kontraktu i wektor obserwowalnego zachowania. Każda scena ma kod wyjściowy (`Start`), kompletne snapshoty po każdym kroku (`StepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s04, s06, s08, s14, s15), test ma osobne oczekiwania dla wariantów i pokazuje różnicę wprost.

Sceny nie powtarzają przykładów z domeny wdrożeń (`Training.Module7`) ani Warsztatów 1-3 z zadań modułu 7. Tamte ćwiczenia uczestnicy robią sami, a sceny poniżej służą do pokazu. Scena s13 pracuje na **kopii** starego `CinemaManager`, więc oryginał w `Training.Workshop.Legacy` i jego golden master (`CinemaManagerGoldenMasterTest`) pozostają nietknięte.

Port C# trzyma się przebiegu scen z Javy. Mechanizmy języka są tłumaczone na odpowiedniki .NET: interfejsy funkcyjne (`ReminderSender`, `Mailer`, `PaymentGateway`) to delegaty, `Supplier<X>` to `Func<X>`, `Clock` to `TimeProvider` (w testach `FakeTimeProvider`), `BigDecimal` to `decimal`, `@Deprecated` to `[Obsolete]`, a gettery stanu (`Audit`, `Remaining`, `Inspected`) są właściwościami. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi co i dlaczego.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m7            # sceny modułu i ich kroki
scripts/warsztat.sh --lang cs test m7/s02        # testy jednej sceny
scripts/warsztat.sh --lang cs test m7            # wszystkie sceny modułu (290 testów)
scripts/warsztat.sh --lang cs diff m7/s13 2 3    # co zmienia krok 3 względem kroku 2 (0 = start)
scripts/warsztat.sh --lang cs diff m7/s13 2 3 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m7/s13 2      # skopiuj Step2 do Start, gdy brakuje czasu
scripts/warsztat.sh --lang cs next m7/s13        # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                         # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                         # krok wstecz
scripts/warsztat.sh status                       # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m7/s13       # przywróć Start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w IDE, test sceny w Rider (ikona w gutter przy klasie testu albo okno Unit Tests), zdanie z sekcji "Co powiedzieć".
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m7/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `next` ze sceną wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu `Start` (namespace `...Start`). Snapshoty `StepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd "Wektor obserwowalnego zachowania" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?* Testy scen celowo obserwują więcej niż sam wynik (audyt w s07, licznik w s12, lista wejściowa w s05, skrzynka nadawcza w s03).
- W s01 start **nie ma prawdziwych testów** i to jest puenta: test `StartCannotEvenRunInATest` tylko dokumentuje, że kodu nie da się uruchomić. Testy pojawiają się dopiero wtedy, gdy kod ma seam.
- W s10 ostrzeżenie CS0618 (użycie API oznaczonego `[Obsolete]`) jest w tym projekcie błędem, bo `Directory.Build.props` ma `TreatWarningsAsErrors`. Dlatego niezmigrowani klienci w krokach 1 i 2 mają lokalne `#pragma warning disable CS0618` - 4 miejsca (`MobileApp` i `BoxOfficeTerminal` w `Step1` i `Step2`). To celowe: lista pragm jest listą klientów do migracji. Znikają w kroku 3.
- Kroki, które zmieniają kontrakt (s04 krok 2, s06 krok 3, s08 kroki 1-2, s14 kroki 2-3, s15 krok 2), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
|---|---|---|---|---|
| s01 | 1. Break Dependencies - intencja, ryzyka, przed i po | 4 | `M7.S01BreakDependencies` | ~20 min |
| s02 | 2. Extract Method Object - przed, po, sekwencja i ryzyka | 3 | `M7.S02MethodObject` | ~15 min |
| s03 | 3. Break Responsibilities - intencja, ryzyka, po zmianie | 3 | `M7.S03BreakResponsibilities` | ~12 min |
| s04 | 4. Remove Duplication - duplikacja wiedzy, po zmianie | 3 | `M7.S04RemoveDuplication` | ~12 min |
| s05 | 5. Break Method - intencja i bezpieczna sekwencja | 3 | `M7.S05BreakMethod` | ~10 min |
| s06 | 6. Introduce Parameter Object - data clump, walidacja, migracja | 3 | `M7.S06ParameterObject` | ~12 min |
| s07 | 7. Remove Arrowhead Antipattern | 3 | `M7.S07Arrowhead` | ~10 min |
| s08 | 8. Introduce Design by Contract Checks | 2 | `M7.S08DesignByContract` | ~10 min |
| s09 | 9. Remove Double Negative | 3 | `M7.S09DoubleNegative` | ~8 min |
| s10 | 11. Remove Boolean Method Parameters; Zgodność binarna | 4 | `M7.S10BooleanParameter` | ~12 min |
| s11 | 12. Remove Middle Man | 3 | `M7.S11MiddleMan` | ~8 min |
| s12 | 13. Return ASAP | 2 | `M7.S12ReturnAsap` | ~8 min |
| s13 | 10. Remove God Classes - kampania, wydzielony fragment i ryzyka | 4 | `M7.S13GodClass` | ~30 min |
| s14 | Refaktoryzacja a zmiana kontraktu; Pętla pracy (pkt 5) | 3 | `M7.S14ContractChange` | ~10 min |
| s15 | Wektor obserwowalnego zachowania | 3 | `M7.S15BehaviourVector` | ~12 min |

Pełne przestrzenie nazw mają prefiks `Training.Workshop.`, katalogi leżą w `csharp/src/Training.Workshop/M7/`, a testy w `csharp/tests/Training.Workshop.Tests/M7/`.

## Scena s01. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 1. Break Dependencies - intencja i ryzyka; Break Dependencies - przed i po; Java 25 w tym module (interfejs funkcyjny jako seam - w C# delegat)
**Namespace:** `Training.Workshop.M7.S01BreakDependencies` (katalog `csharp/src/Training.Workshop/M7/S01BreakDependencies`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s01` (`S01SeamTest`)
**Czas:** ~20 min

### W skrócie

**Co robimy:** `ShowtimeReminderJob` sam tworzy bazę, czyta zegar systemowy i woła statyczny mailer, więc nie da się go uruchomić w teście. Kolejnymi małymi ruchami wyciągamy trzy zależności do konstruktora, aż test może podstawić fałszywą bazę, stały zegar (`FakeTimeProvider`) i lambdę zamiast maila.

**Zasada:** Break Dependencies tworzy seam - miejsce, w którym można podstawić inną implementację bez edycji algorytmu, żeby uruchomić kod bez kosztownego otoczenia i obserwować jego komunikację. Wybieramy najwęższy skuteczny seam: parametr, zależność konstruktora, `TimeProvider` albo delegat, a nie interfejs przed każdą klasą. To nie jest zmiana reguły biznesowej, tylko zmiana miejsca tworzenia zależności.

**Efekt:** Klasa ma pierwszy prawdziwy test z granicą 120/121 minut, a produkcyjny konstruktor składa te same implementacje co wcześniej, łącznie z bazą otwieraną przy każdym `Run()`. Kosztem są trzy nowe małe typy i konstruktor z trzema parametrami.

**Różnica względem Javy:** `Supplier<BookingStore>` to `Func<IBookingStore>`, `Clock` to `TimeProvider` (w teście `FakeTimeProvider`), a `@FunctionalInterface ReminderSender` to delegat. W kroku 3 klasa przestaje być `sealed`, a `SendReminder` jest `protected virtual` (w Javie "przestaje być final"). Podklasa testowa to prywatna klasa zagnieżdżona `RecordingStep3Job` zamiast klasy anonimowej, a `FakeStore` w teście implementuje `IBookingStore` wszystkich kroków naraz.

### Co widzimy

`ShowtimeReminderJob.Run` wysyła przypomnienia na 2 godziny przed seansem. Wszystkie zależności są zaszyte w środku: produkcyjna baza tworzona przez `new`, bieżący czas (`DateTime.Now`) i statyczny mailer. Konstruktor `LegacyDatabase` rzuca wyjątek poza serwerownią, a `ReminderMailer.Send` - poza produkcją. **Tej klasy nie da się uruchomić w teście** - test `StartCannotEvenRunInATest` tylko to dokumentuje.

```csharp
public int Run()
{
    var database = new LegacyDatabase();
    var now = DateTime.Now;
    ...
            ReminderMailer.Send(booking.Email, "Przypomnienie: " + booking.Title, ...);
            database.MarkReminded(booking.Id);
```

Sekwencja ze slajdu: charakterystyka, najwęższy kontrakt, dotychczasowa implementacja przez kontrakt, test z fake/spy. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i wspierane przez IDE.

### Krok 1: Extract Interface + Parameterize Constructor

**W IDE:** na `LegacyDatabase` Refactor This (⌃T) → Extract Interface, nazwa `IBookingStore`, zaznacz `PaidBookings` i `MarkReminded`. Potem w `ShowtimeReminderJob` ręcznie: pole `Func<IBookingStore> _stores`, konstruktor z parametrem i konstruktor bezargumentowy `: this(() => new LegacyDatabase())`. W `Run()` zamień `new LegacyDatabase()` na `_stores()`.
**Po:**

```csharp
public ShowtimeReminderJob()
    : this(() => new LegacyDatabase())
{
}

public int Run()
{
    var database = _stores();
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s01` - testy `Step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania (`Step1RunsWithFakeStoreWhenNothingIsDue`).
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `Run()`. Gdybyśmy zrobili `: this(new LegacyDatabase())`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s01 0 1`

### Krok 2: TimeProvider jako zależność

**W IDE:** ręcznie (Introduce Parameter ⌥⌘P dodałby parametr do `Run()`, a potrzebujemy zależności konstruktora): pole `TimeProvider _clock`, drugi parametr konstruktora, w domyślnym konstruktorze `TimeProvider.System`, w `Run()` `_clock.GetLocalNow().DateTime`.
**Po:**

```csharp
var now = _clock.GetLocalNow().DateTime;
```

**Uruchom:** test zielony. Test `Step2ControlsTimeButStillHitsStaticMailer` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s01 1 2`

### Krok 3: Subclass and Override Method i pierwszy test

**W IDE:** zaznacz wywołanie `ReminderMailer.Send(...)`, ⌥⌘M (Extract Method), nazwa `SendReminder`, widoczność `protected`, dopisz `virtual`. Usuń `sealed` z klasy. W teście prywatna klasa zagnieżdżona `RecordingStep3Job` nadpisuje `SendReminder` i zapisuje wiadomości do listy - to jest **pierwszy prawdziwy test** tej klasy.
**Po:**

```csharp
protected virtual void SendReminder(string to, string subject, string body)
{
    ReminderMailer.Send(to, subject, body);
}
```

**Uruchom:** test zielony: `FirstRealTestForSteps3And4` - 3 przypadki z granicą 120/121 minut, już przypomniane i już rozpoczęte.
**Co powiedzieć:** to najtańszy seam, gdy nie możemy jeszcze zmienić konstruktora - ale ma cenę: klasa przestała być `sealed`, metoda stała się `virtual`, a test zależy od szczegółu implementacji (nazwy metody chronionej).
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s01 2 3`

### Krok 4: seam z dziedziczenia na delegat

**W IDE:** utwórz `public delegate void ReminderSender(string to, string subject, string body)`, dodaj trzeci parametr konstruktora, w domyślnym grupa metod `ReminderMailer.Send`. Inline Method (⌥⌘N) na `SendReminder`, przywróć `sealed`. W teście podklasę zastępuje lambda.
**Po:**

```csharp
public ShowtimeReminderJob()
    : this(() => new LegacyDatabase(), TimeProvider.System, ReminderMailer.Send)
{
}
```

**Uruchom:** test zielony - te same 3 przypadki dla `Step3` (podklasa) i `Step4` (lambda).
**Co powiedzieć:** pod ochroną testu z kroku 3 wymieniliśmy seam na najwęższy możliwy. Produkcja w domyślnym konstruktorze składa dokładnie te same implementacje co wcześniej - zmieniło się **miejsce tworzenia zależności**, nie reguła.
**Snapshot:** `Step4/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s01 3 4`

### Rozwiązanie i uzasadnienie

`Step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`IBookingStore` z dwiema metodami, `TimeProvider`, `ReminderSender` z jedną sygnaturą). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `: this(new LegacyDatabase())` w domyślnym konstruktorze: zmiana czasu życia i momentu wyjątku (test `Step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- Seam, którego test nie może podstawić: interfejs `internal` w innym assembly bez `InternalsVisibleTo` albo klasa `sealed` w miejscu interfejsu czy delegatu.
- Zostawienie Subclass and Override na stałe: klasa otwarta do dziedziczenia (bez `sealed`, z metodą `virtual`) tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s02. Extract Method Object - wycena zamówienia grupowego

**Temat ze slajdów:** 2. Extract Method Object - przed; Extract Method Object - po, sekwencja i ryzyka
**Namespace:** `Training.Workshop.M7.S02MethodObject` (katalog `csharp/src/Training.Workshop/M7/S02MethodObject`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s02` (`S02EquivalenceTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `GroupPricing.Quote` ma osiem splątanych zmiennych lokalnych i pętlę z dwoma wynikami, więc zwykły Extract Method się nie udaje. Przenosimy całe ciało dosłownie do obiektu `GroupQuoteCalculation`, zmienne robocze robimy polami i dopiero wtedy tniemy algorytm na nazwane kroki.

**Zasada:** Extract Method Object zamienia jedno wywołanie metody w krótko żyjący obiekt, którego pola niosą stan lokalny, dzięki czemu każdy blok może stać się metodą bez parametrów. Stosujemy go dopiero wtedy, gdy przepływ lokalnego stanu blokuje zwykłe ekstrakcje, w kolejności: skopiuj bez upraszczania, deleguj, porównaj, potem porządkuj.

**Efekt:** `Calculate()` czyta się jak sześć kroków wyceny, a publiczne API `GroupPricing` i `Quote` się nie zmieniły. Kolejność wywołań kroków staje się kontraktem, a obiekt musi powstawać na każde wywołanie, bo inaczej wyceny zaczną współdzielić stan.

**Różnica względem Javy:** zmienna `base` nazywa się `basePrice` (`base` to słowo kluczowe C#). Pola `GroupQuoteCalculation` celowo nie mają prefiksu `_`: krok 2 ma pokazać, że kod metody się nie zmienia, zmieniają się tylko deklaracje. Pole `basePrice` ma inicjalizator `Money.Zero` (typ nie jest nullowalny), w Javie było `null` do pierwszego przypisania.

### Co widzimy

`GroupPricing.Quote` liczy cenę zamówienia (także grupowego): cena formatu, zniżki, poranek, okulary 3D, VIP, rabat 10% od 10 biletów, opłaty online i punkty. Osiem zmiennych lokalnych karmi się nawzajem. Pętla ma dwa wyjścia (`tickets`, `count`) i trzy wejścia robocze.

```csharp
var tickets = Money.Zero;
var count = 0;
foreach (var type in order.TicketTypes)
{
    ...
    var price = basePrice.Minus(basePrice.Percent(discount));
    if (morning) { price = price.Minus(Money.Of("5.00")); }
    if (glasses) { price = price.Plus(Money.Of("3.00")); }
    tickets = tickets.Plus(price);
    count++;
}
```

Spróbuj ⌥⌘M na pętli: pętla ma dwie wartości wyjściowe, więc Rider nie da metody z jednym wynikiem - najwyżej zaproponuje parametry `out`, co tylko przenosi splątanie do sygnatury. To jest moment na Method Object.

### Krok 1: Extract Method Object - kopia bez upraszczania

**W IDE:** Rider nie ma automatycznego Replace Method with Method Object, więc ręcznie: utwórz klasę `internal sealed class GroupQuoteCalculation` z polem `order` i metodą `Calculate()`. Skopiuj ciało `Quote` **dosłownie**. W `GroupPricing.Quote` zostaw jedną linię delegacji.
**Po:**

```csharp
public Quote Quote(GroupOrder order)
{
    return new GroupQuoteCalculation(order).Calculate();
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s02` - 24 testy zielone.
**Co powiedzieć:** skopiuj, deleguj, porównaj - dopiero potem sprzątaj. Obiekt metody powstaje na każde wywołanie, więc nie ma współdzielonego stanu między wycenami.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s02 0 1`

### Krok 2: zmienne lokalne jako pola

**W IDE:** na każdej zmiennej roboczej (`basePrice`, `morning`, `glasses`, `tickets`, `count`) Refactor This (⌃T) → Introduce Field (albo ręcznie: deklaracja pola, a w metodzie zostaje samo przypisanie).
**Po:**

```csharp
private readonly GroupOrder order;
private Money basePrice = Money.Zero;
private bool morning;
private bool glasses;
private Money tickets = Money.Zero;
private int count;
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz każdy blok może zostać metodą bez parametrów i bez wielu wyjść - stan niosą pola. To działa tylko dlatego, że obiekt żyje jedno wywołanie.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s02 1 2`

### Krok 3: Extract Method wewnątrz obiektu

**W IDE:** kolejno ⌥⌘M: `ReadConditions`, `AddTickets` (a w niej `TicketPrice` i statyczne `DiscountPercent`), `AddVipSeats`, `ApplyGroupDiscount`, `BookingFees`, `LoyaltyPoints`. Inline Variable (⌥⌘N) dla `total`. W `ReadConditions` Rider proponuje zamianę `switch` na wyrażenie `switch` - można ją przyjąć, bo przypisanie jest jedno.
**Po:**

```csharp
internal Quote Calculate()
{
    ReadConditions();
    AddTickets();
    AddVipSeats();
    ApplyGroupDiscount();
    var fees = BookingFees();
    return new Quote(tickets, fees, tickets.Plus(fees), LoyaltyPoints());
}
```

**Uruchom:** test zielony - w tym przypadek z rabatem 23.125 zaokrąglonym do 23.13.
**Co powiedzieć:** kolejność wywołań jest kontraktem: rabat grupowy liczony po dodaniu VIP, punkty po rabacie. Metody z efektem na polach czytają się jak kroki algorytmu.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s02 2 3`

### Rozwiązanie i uzasadnienie

`Step3/GroupQuoteCalculation`: jedno wykonanie wyceny jako obiekt, sześć nazwanych kroków. Publiczne API `GroupPricing` i kontrakt `Quote` bez zmian. Porównaj ze sceną s05: tam dane łatwo przechodzą między etapami, więc Method Object byłby zbędny.

### Pułapki

- Obiekt metody jako pole usługi (`private readonly GroupQuoteCalculation _calc`) - pola przeżyją wywołanie i wyceny zaczną się mieszać.
- Upraszczanie w trakcie kopiowania (np. `count` zastąpione `order.TicketTypes.Count` przed testem).
- Funkcje lokalne w `Quote` domykające zmienne zamiast obiektu metody - kompilują się, ale stan dalej jest splątany, tylko ukryty w domknięciu. (Odpowiednik javowej pułapki z niestatyczną klasą wewnętrzną, która ukrywa referencję do obiektu zewnętrznego; klasa zagnieżdżona w C# takiej referencji nie ma.)
- Zmiana momentu odczytu danych (np. `order.Start` czytane w konstruktorze zamiast w `Calculate()`).

### Pytanie do sali

Po co w ogóle Method Object, skoro można zwrócić krotkę albo rekord `(tickets, count)` z wydzielonej pętli?

## Scena s03. Break Responsibilities - walidacja, wycena, powiadomienie

**Temat ze slajdów:** 3. Break Responsibilities - intencja i ryzyka; Break Responsibilities - po zmianie
**Namespace:** `Training.Workshop.M7.S03BreakResponsibilities` (katalog `csharp/src/Training.Workshop/M7/S03BreakResponsibilities`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s03` (`S03EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `BookingDesk.Book` waliduje prośbę, wycenia miejsca i wysyła powiadomienie, czyli ma trzy powody zmiany od trzech różnych działów kina. Wydzielamy po jednej klasie na raz: `BookingValidator`, `TicketPricer` z rekordem `Pricing` i `BookingNotifier`.

**Zasada:** Break Responsibilities dzieli klasę według powodów zmiany, a nie według liczby linii czy metod. Wydzielamy spójny klaster, stara klasa zostaje delegującą fasadą, a każdy stan ma dokładnie jednego właściciela, bez dwóch kopii.

**Efekt:** `BookingDesk` tylko składa kroki w tej samej kolejności co wcześniej, a publiczny konstruktor i `Book` są bez zmian, więc klienci niczego nie zauważają. `Outbox` należy wyłącznie do notifiera, a w prawdziwym systemie trzeba jeszcze pilnować granic transakcji między nowymi klasami.

**Różnica względem Javy:** `FirstError` zwraca `string?` (`null` = brak błędu) zamiast `Optional<String>`, a kwoty są typu `decimal`. Konstruktor z trzema współpracownikami jest `internal` (w Javie pakietowy), a nowe klasy są `internal sealed`.

### Co widzimy

`BookingDesk.Book` waliduje prośbę, wycenia miejsca i wysyła powiadomienie do `Outbox`. Trzy powody zmiany: reguły walidacji (obsługa klienta), cennik (finanse), treść wiadomości (marketing). Komentarze `// walidacja`, `// wycena`, `// powiadomienie` wyznaczają klastry. Test obserwuje wynik i zawartość skrzynki nadawczej.

```csharp
var total = 0m;
var vipSeats = 0;
foreach (var seat in request.Seats) { ... }

// powiadomienie
var text = "Rezerwacja " + request.Seats.Count + " miejsc";
if (vipSeats > 0) { ... }
```

### Krok 1: Extract Class - walidacja

**W IDE:** zaznacz blok walidacji, ⌥⌘M `FirstError` zwracająca `string?` (ręcznie: każde `return "ERROR: ..."` zostaje, na końcu `return null`), potem F6 (Move) do nowej klasy `BookingValidator`.
**Po:**

```csharp
var error = _validator.FirstError(request);
if (error != null)
{
    return error;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s03` - 20 testów zielonych (w tym priorytet: zły e-mail przed brakiem miejsc).
**Co powiedzieć:** kolejność kontroli jest częścią kontraktu - pierwszy błąd wygrywa.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s03 0 1`

### Krok 2: Extract Class - wycena z wynikiem jako rekord

**W IDE:** utwórz rekord `internal sealed record Pricing(decimal Total, int VipSeats)`, wydziel blok wyceny do `TicketPricer.Price`. Dwa wyjścia pętli wracają jako jeden obiekt z nazwą z domeny.
**Po:**

```csharp
var pricing = _pricer.Price(request);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `VipSeats` potrzebuje powiadomienie, a liczy cennik - rekord jawnie opisuje ten przepływ zamiast ukrytej zmiennej lokalnej.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s03 1 2`

### Krok 3: Extract Class - powiadomienie, BookingDesk jako orkiestrator

**W IDE:** wydziel blok powiadomienia do `BookingNotifier.BookingConfirmed(request, pricing)`. `Outbox` przechodzi do notifiera. Dodaj konstruktor `internal` przyjmujący trzech współpracowników, publiczny zostaje `BookingDesk(Outbox)` i deleguje przez `: this(...)`.
**Po:**

```csharp
public string Book(BookingRequest request)
{
    var error = _validator.FirstError(request);
    if (error != null)
    {
        return error;
    }
    var pricing = _pricer.Price(request);
    _notifier.BookingConfirmed(request, pricing);
    return "OK " + pricing.Total.ToString(CultureInfo.InvariantCulture);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klasa składa kroki, ale nie zna ich szczegółów. Stan (`Outbox`) ma jednego właściciela.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s03 2 3`

### Rozwiązanie i uzasadnienie

`Step3/BookingDesk` z trzema współpracownikami. Publiczne API bez zmian, więc klienci niczego nie zauważają. Kierunek zależności jest czytelny: orkiestrator zna usługi, usługi nie znają orkiestratora.

### Pułapki

- Podział według liczby linii, a nie powodu zmiany.
- Dwie kopie stanu (np. `Outbox` w `BookingDesk` i w `BookingNotifier`).
- Przeniesienie powiadomienia przed wycenę "bo tak ładniej" - zmiana kolejności efektów.
- W prawdziwym systemie: rozdzielenie transakcji (zapis i wysyłka w innych granicach).

### Pytanie do sali

Czy `TicketPricer` powinien dostać `BookingRequest`, czy tylko format i listę miejsc?

## Scena s04. Remove Duplication - rabat grupowy w kasie i w sklepie

**Temat ze slajdów:** 4. Remove Duplication - duplikacja wiedzy; Remove Duplication - po zmianie
**Namespace:** `Training.Workshop.M7.S04RemoveDuplication` (katalog `csharp/src/Training.Workshop/M7/S04RemoveDuplication`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s04` (`S04EquivalenceTest`, `S04RoundingDecisionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Reguła "10 i więcej biletów to 10% rabatu" żyje w `BoxOffice` i `WebShop`, zapisana zupełnie inaczej i z innym zaokrągleniem. Najpierw ujednolicamy zapis, żeby różnica stała się widoczna, potem świadomie decydujemy o zaokrągleniu i wydzielamy regułę do `GroupDiscount`.

**Zasada:** Remove Duplication łączy fragmenty reprezentujące tę samą wiedzę, czyli regułę, która zmienia się razem, a nie podobny tekst. Podobne reguły z niezależnych kontekstów mogą celowo żyć osobno, a wspólna metoda z flagą "żeby nic nie zmienić" zwykle utrwala przypadkową różnicę.

**Efekt:** Reguła rabatu ma jednego właściciela, a opłata online zostaje jawnie w `WebShop`. Zachowanie świadomie się zmienia: w przypadku brzegowym sklep zaokrągla teraz `MidpointRounding.AwayFromZero` (HALF_UP) jak kasa i klient płaci grosz mniej, co trafia do osobnego commita jako zmiana kontraktu.

**Różnica względem Javy:** `BigDecimal` to `decimal`, `setScale(2, HALF_UP)` to `Math.Round(x, 2, MidpointRounding.AwayFromZero)`, `setScale(2, HALF_EVEN)` to `Math.Round(x, 2, MidpointRounding.ToEven)`, a stream w sklepie to LINQ `prices.Sum()`. Test formatuje wynik przez `ToString("0.00")` (odpowiednik `toPlainString()`), bo `decimal` pustego koszyka to `0` bez skali.

### Co widzimy

Reguła "10+ biletów = -10%" żyje w dwóch miejscach, zapisana inaczej: `BoxOffice` (pętla, `> 9`, `* 0.10m`, `AwayFromZero`) i `WebShop` (LINQ `Sum()`, `>= 10`, `x * 10 / 100`, **`ToEven`**, czyli zaokrąglenie bankierskie HALF_EVEN). Tekstowo niepodobne, ale to ta sama wiedza. Czy różnica w zaokrągleniu to decyzja, czy przypadek?

```csharp
// BoxOffice
var discount = Math.Round(sum * 0.10m, 2, MidpointRounding.AwayFromZero);
// WebShop
tickets -= Math.Round(tickets * 10 / 100, 2, MidpointRounding.ToEven);
```

Test `S04RoundingDecisionTest` pokazuje przypadek brzegowy: 3 x student 2D (18.75) + 7 x normalny = 231.25, rabat 23.125. Kasa odejmuje 23.13, sklep 23.12.

### Krok 1: ujednolicenie zapisu (bez zmiany zachowania)

**W IDE:** w obu klasach Introduce Field (⌃T → Introduce Field, jako `const`) `GroupSize` i `GroupDiscount`, Rename (⇧F6) `sum` na `tickets`, warunek `> 9` na `>= GroupSize`, w `WebShop` `Sum()` na pętlę i `x * 10 / 100` na `Math.Round(tickets * GroupDiscount, 2, MidpointRounding.ToEven)`.
**Po:**

```csharp
if (ticketPrices.Count >= GroupSize)
{
    var discount = Math.Round(tickets * GroupDiscount, 2, MidpointRounding.ToEven);
    tickets -= discount;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s04` - wszystko zielone, sklep nadal daje 228.13 w przypadku brzegowym (`StartAndStep1KeepTheHistoricalWebRounding`).
**Co powiedzieć:** po ujednoliceniu obie wersje różnią się jedną linią. Różnica przestała być ukryta w formie zapisu i można o niej rozmawiać z biznesem.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s04 0 1`

### Krok 2: decyzja - AwayFromZero także w sklepie (zmiana kontraktu)

**W IDE:** ręcznie `MidpointRounding.ToEven` na `MidpointRounding.AwayFromZero` w `WebShop`. Osobny commit z opisem decyzji.
**Po:**

```csharp
var discount = Math.Round(tickets * GroupDiscount, 2, MidpointRounding.AwayFromZero);
```

**Uruchom:** test zielony: `Step2DeliberatelyAlignsWebWithBoxOffice` oczekuje 228.12, zwykłe koszyki bez zmian (`WebShopAgreesOnOrdinaryBaskets`).
**Co powiedzieć:** to NIE jest refaktoryzacja - klient sklepu w przypadku brzegowym zapłaci grosz mniej. Robimy to świadomie, osobno i z testem, który mówi to wprost.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s04 1 2`

### Krok 3: wydzielenie reguły

**W IDE:** w `BoxOffice` ⌥⌘M na pętli i rabacie, nazwa `TicketsTotal`, statyczna. F6 (Move) do nowej klasy `internal static class GroupDiscount`. W `WebShop` zastąp identyczny fragment wywołaniem (Rider po ekstrakcji potrafi sam znaleźć duplikaty w tej samej klasie - tu kopia jest w innej klasie, więc robimy to ręcznie).
**Po:**

```csharp
// WebShop
var fees = Fee * ticketPrices.Count;
return Math.Round(GroupDiscount.TicketsTotal(ticketPrices) + fees, 2, MidpointRounding.AwayFromZero);
```

**Uruchom:** test zielony.
**Co powiedzieć:** wspólna reguła ma jednego właściciela, a różnica (opłata online) zostaje jawna w `WebShop`. Przełączaj po jednej ścieżce: najpierw kasa, test, potem sklep.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s04 2 3`

### Rozwiązanie i uzasadnienie

`Step3/GroupDiscount`: reguła 10+/-10%/`AwayFromZero` w jednym miejscu. Sekwencja ze slajdu zachowana: scharakteryzuj obie wersje osobno, porównaj brzegi, ujednolić, zdecyduj o różnicy, wydziel, przełącz po jednej ścieżce.

### Pułapki

- Wydzielenie wspólnej metody z parametrem `MidpointRounding mode` "żeby nic nie zmieniać" - flaga utrwala przypadkową różnicę.
- Sklejanie podobnego tekstu, który jest inną wiedzą (np. opłata online wygląda jak rabat, ale ma innego właściciela).
- Test tylko na "ładnych" kwotach (250.00) - różnica w zaokrągleniu przechodzi niezauważona.
- Domyślny `Math.Round(x, 2)` bez trybu to w .NET `ToEven`, a nie "zwykłe" zaokrąglenie - kto "uprości" wywołanie, usuwając argument, po cichu zmieni tryb.

### Pytanie do sali

Kto w Waszej firmie może zdecydować, że `ToEven` w sklepie był błędem, a nie wymaganiem?

## Scena s05. Break Method - repertuar dnia

**Temat ze slajdów:** 5. Break Method - intencja; Break Method - bezpieczna sekwencja
**Namespace:** `Training.Workshop.M7.S05BreakMethod` (katalog `csharp/src/Training.Workshop/M7/S05BreakMethod`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s05` (`S05EquivalenceTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RepertoireBuilder.Build` miesza kontrolę wejścia, porządkowanie seansów i renderowanie tekstu w jednej metodzie. Serią zwykłych Extract Method dzielimy ją na `ValidateAndCopy`, `Order` i `Render`.

**Zasada:** Break Method to seria małych ekstrakcji, po której metoda opisuje algorytm na jednym poziomie abstrakcji - kryterium to nazwane, spójne kroki, a nie liczba wierszy. Zaczynamy od fragmentu z najmniejszą liczbą wejść i jednym wynikiem, przenosimy go dosłownie, a nazwę nadajemy po teście. Method Object jest potrzebny dopiero wtedy, gdy lokalny stan blokuje ekstrakcje.

**Efekt:** `Build` ma trzy linie, a typ i komunikat wyjątku oraz nienaruszona lista klienta zostają bez zmian. Etapy nadal dzielą model `Screening`, więc gdyby potrzebne były osobne modele danych, następnym ruchem byłby Split Phase.

**Różnica względem Javy:** stabilne `List.sort` z Javy to `OrderBy(...).ThenBy(..., StringComparer.Ordinal)`, bo `List<T>.Sort` w .NET jest niestabilne, więc świadomie go nie użyto. Wejście to `IReadOnlyList<Screening?>` (null w liście jest częścią scenariusza), a oczekiwany wyjątek to `ArgumentException: screening must not be null`.

### Co widzimy

`RepertoireBuilder.Build` miesza trzy poziomy abstrakcji: kontrolę wejścia (null w liście), porządkowanie (odwołane seanse, sortowanie po godzinie i tytule) i renderowanie tekstu. Test obserwuje tekst albo wyjątek (typ i komunikat) oraz to, czy lista klienta pozostała nietknięta.

```csharp
var active = new List<Screening>();
foreach (var screening in copy) { if (!screening.Cancelled) { active.Add(screening); } }
active = [.. active.OrderBy(s => s.Start).ThenBy(s => s.Title, StringComparer.Ordinal)];
var text = new StringBuilder("REPERTUAR\n");
```

### Krok 1: Extract Method - fragment z jednym wejściem i jednym wynikiem

**W IDE:** zaznacz pierwszą pętlę (kopia z kontrolą null), ⌥⌘M, nazwa `ValidateAndCopy`.
**Po:**

```csharp
var copy = ValidateAndCopy(screenings);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s05` - 12 testów zielonych, w tym wyjątek dla `null`.
**Co powiedzieć:** zaczynamy od fragmentu z najmniejszą liczbą zależności i przenosimy go dosłownie. Nazwa przychodzi po teście.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s05 0 1`

### Krok 2: Extract Method - porządkowanie

**W IDE:** zaznacz filtr i sortowanie, ⌥⌘M, nazwa `Order`.
**Po:**

```csharp
var active = Order(copy);
```

**Uruchom:** test zielony - "wejscie bez zmian: true".
**Co powiedzieć:** sortujemy kopię. Gdyby ktoś "uprościł" to do sortowania w miejscu listy klienta (np. `List<T>.Sort` na tym, co przyszło z zewnątrz), lista klienta zostałaby przestawiona - test to wykryje. A `List<T>.Sort` jest do tego niestabilne.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s05 1 2`

### Krok 3: Extract Method - renderowanie i Rename

**W IDE:** zaznacz budowę tekstu, ⌥⌘M `Render`. Rename (⇧F6) zmiennych na `validated` i `ordered`.
**Po:**

```csharp
public string Build(IReadOnlyList<Screening?> screenings)
{
    var validated = ValidateAndCopy(screenings);
    var ordered = Order(validated);
    return Render(ordered);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda opisuje algorytm na jednym poziomie abstrakcji. Dane łatwo przechodzą między krokami (lista, lista, tekst), więc Method Object ze sceny s02 byłby tu przerostem formy.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s05 2 3`

### Rozwiązanie i uzasadnienie

`Step3/RepertoireBuilder`: trzy etapy, każdy z jednym wejściem i jednym wyjściem. Gdyby etapy miały osobne modele danych (np. wiersz raportu zamiast `Screening`), następnym ruchem byłby Split Phase.

### Pułapki

- Ekstrakcja pętli z `return`/`break`/`continue` w środku bez sprawdzenia, dokąd skacze sterowanie.
- Zastąpienie `ValidateAndCopy` przez `screenings.ToList()` - `null` przechodzi dalej i wybucha później jako `NullReferenceException` zamiast `ArgumentException` (inny typ, komunikat i moment wyjątku).
- Sortowanie listy wejściowej zamiast kopii (aliasowanie), a przy okazji zamiana stabilnego `OrderBy` na niestabilne `List<T>.Sort`.

### Pytanie do sali

Kiedy `Order` i `Render` zasługują na osobne klasy, a kiedy wystarczą prywatne metody?

## Scena s06. Introduce Parameter Object - termin seansu

**Temat ze slajdów:** 6. Introduce Parameter Object - data clump jako pojęcie; Parameter Object - walidacja, migracja i ryzyka
**Namespace:** `Training.Workshop.M7.S06ParameterObject` (katalog `csharp/src/Training.Workshop/M7/S06ParameterObject`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s06` (`S06EquivalenceTest`, `S06ValidationMomentTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Dwie metody `ScreeningPlanner` przyjmują tę samą czwórkę parametrów, która w domenie znaczy "termin seansu w sali". Nazywamy ją rekordem `ScreeningSlot`, zostawiamy stare sygnatury na czas migracji, przenosimy do rekordu opis, a na końcu walidację.

**Zasada:** Introduce Parameter Object zamienia grupę parametrów, które zawsze chodzą razem (data clump), w typ nazywający jedno pojęcie z domeny - typ ogranicza pomyłki kolejności i przyciąga zachowanie. To nie jest worek `Parameters` na długą listę argumentów. Walidacja w konstruktorze rekordu przesuwa moment błędu, więc jest osobnym krokiem.

**Efekt:** `ScreeningSlot` jest poprawny z definicji, a walidacja jest w jednym miejscu. Zachowanie świadomie się zmienia: zła sala rzuca wyjątek już przy tworzeniu rekordu, więc `Describe` dla sali 12 przestaje zwracać tekst, a stare sygnatury trzeba jeszcze usunąć po migracji klientów.

**Różnica względem Javy:** Java waliduje w kompaktowym konstruktorze kanonicznym rekordu. W C# rekord pozycyjny nie ma takiego konstruktora, więc w kroku 3 `ScreeningSlot` staje się `sealed record` z jawnym konstruktorem i właściwościami tylko do odczytu (bez `init`) - dzięki temu wyrażenie `with` nie ominie walidacji. Stare sygnatury mają `[Obsolete]` zamiast `@Deprecated`, a test, który je woła, ma `#pragma warning disable CS0618`.

### Co widzimy

`ScreeningPlanner` ma dwie metody z tą samą czwórką parametrów: `screeningId, date, hall, format`. To pojęcie "termin seansu w sali", którego nikt nie nazwał. Walidacja sali i formatu siedzi tylko w `TicketPrice`, a `Describe` przyjmuje wszystko.

```csharp
public string Describe(string screeningId, DateOnly date, int hall, string format)
public decimal TicketPrice(string screeningId, DateOnly date, int hall, string format)
```

### Krok 1: Introduce Parameter Object (stan przejściowy, bez walidacji)

**W IDE:** Rider ma pokrewny ruch (Refactor This ⌃T → Extract Class from Parameters), ale tworzy klasę, a nie rekord, i nie zostawia starej sygnatury jako delegata. Dlatego ręcznie: rekord pozycyjny `ScreeningSlot`, nowe przeciążenia `Describe(ScreeningSlot)` i `TicketPrice(ScreeningSlot)` z przeniesionym ciałem, a stare sygnatury dostają `[Obsolete]` i delegują.
**Po:**

```csharp
public sealed record ScreeningSlot(string ScreeningId, DateOnly Date, int Hall, string Format);

[Obsolete("użyj Describe(ScreeningSlot)")]
public string Describe(string screeningId, DateOnly date, int hall, string format)
{
    return Describe(new ScreeningSlot(screeningId, date, hall, format));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s06` - 24 testy zielone: nowe API i stare sygnatury dają te same wyniki (`NewApiBehavesLikeTheOldOne`, `DeprecatedSignaturesStillWork`).
**Co powiedzieć:** rekord bez walidacji to celowy stan przejściowy - kontrole zostają tam, gdzie były. Stare sygnatury pozwalają migrować wywołania po jednym.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s06 0 1`

### Krok 2: Move Method - typ przyciąga zachowanie

**W IDE:** ciało `Describe(ScreeningSlot)` przenieś do `ScreeningSlot` jako metodę `Label()` (Refactor This → Move Instance Method albo ręcznie, bo rekord jest parametrem). `Describe` deleguje.
**Po:**

```csharp
public string Label()
{
    return ScreeningId + " " + Date.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture)
        + " sala " + Hall + " (" + Format + ")";
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** po nazwaniu pojęcia pojawia się miejsce na zachowanie, które do niego należy.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s06 1 2`

### Krok 3: walidacja w konstruktorze rekordu (zmiana kontraktu)

**W IDE:** zamień rekord pozycyjny na rekord z jawnym konstruktorem i właściwościami `{ get; }`, przenieś do konstruktora kontrolę sali i formatu z `TicketPrice`. W `TicketPrice` gałąź `_ => throw` staje się zbędna (zostaje `_ => 40.00m` dla IMAX).
**Po:**

```csharp
public ScreeningSlot(string screeningId, DateOnly date, int hall, string format)
{
    if (hall < 1 || hall > 8)
    {
        throw new ArgumentException("nie ma sali " + hall + " (" + screeningId + ")");
    }
    if (!Formats.Contains(format)) { ... }
    ScreeningId = screeningId;
    ...
}
```

**Uruchom:** test zielony. `S06ValidationMomentTest` pokazuje różnicę: do kroku 2 `Describe` dla sali 12 zwraca tekst, a `TicketPrice` rzuca (`UntilStep2OnlyTicketPriceRejectsWrongHall`). W kroku 3 wyjątek leci już przy `new ScreeningSlot(...)` (`Step3RejectsWrongHallWhenTheSlotIsCreated`), a nieznany format przesuwa się tak samo (`UnknownFormatMovesTheSameWay`).
**Co powiedzieć:** ten sam komunikat, inny moment. Klient, który logował opis przed wyceną, przestanie dostawać log. To zmiana kontraktu - osobny krok i osobny commit.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s06 2 3`

### Rozwiązanie i uzasadnienie

`Step3/ScreeningSlot` jest poprawny z definicji, planner nie musi niczego sprawdzać. Stare sygnatury zostają do końca migracji klientów, potem Safe Delete (⌘⌦).

### Pułapki

- Walidacja w rekordzie w tym samym kroku co wprowadzenie rekordu - nie wiadomo, który ruch zmienił zachowanie.
- Worek `Parameters` z flagami zamiast pojęcia z domeny.
- Walidacja w rekordzie z właściwościami `init` (także pozycyjnym): `slot with { Hall = 12 }` kopiuje obiekt bez wołania konstruktora i omija kontrolę. Dlatego w kroku 3 właściwości są tylko do odczytu.
- Rekord jest tylko płytko niemutowalny: właściwość `IReadOnlyList<T>` może wskazywać na `List<T>` klienta. Kopia defensywna (`[.. items]`) zmienia semantykę aliasowania.
- Właściwości publicznego rekordu to API - zmiana nazwy łamie klientów (także argumenty nazwane i dekonstrukcję) i serializację.

### Pytanie do sali

Czy `Date` naprawdę należy do tego pojęcia, skoro `TicketPrice` jej nie używa?

## Scena s07. Remove Arrowhead Antipattern - bramka rezerwacji

**Temat ze slajdów:** 7. Remove Arrowhead Antipattern; Remove Arrowhead - po zmianie
**Namespace:** `Training.Workshop.M7.S07Arrowhead` (katalog `csharp/src/Training.Workshop/M7/S07Arrowhead`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s07` (`S07EquivalenceTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BookingGate.Book` ma pięć poziomów zagnieżdżenia, a główna ścieżka rezerwacji siedzi na dnie, pod którym jeszcze zapisuje się audyt. Najpierw wydzielamy decyzję do `Decide`, żeby audyt został w jednym miejscu, a potem spłaszczamy warunki poziom po poziomie.

**Zasada:** Remove Arrowhead zamienia zagnieżdżone `if`/`else` na guard clauses, czyli wczesne wyjścia dla przypadków kończących przetwarzanie. Jest bezpieczne tylko przy zachowanym priorytecie warunków i wtedy, gdy wczesny `return` nie omija efektu na końcu metody, takiego jak log, zmiana stanu czy zwolnienie zasobu.

**Efekt:** `Decide` to czysta funkcja z pięcioma guard clauses bez zmiennej `result`, a audyt nadal zapisuje się dla każdej ścieżki. Trzeba pilnować poprawnego zaprzeczenia warunków: `<=` odwraca się na `>`, a nie na `>=`.

### Co widzimy

`BookingGate.Book` ma pięć poziomów zagnieżdżenia, a główna ścieżka (`BOOKED`) siedzi na samym dnie. Wynik zbiera zmienna `result`. Na końcu metoda zapisuje wpis do audytu - **dla każdej ścieżki**.

```csharp
if (attempt.ScreeningFound)
{
    if (attempt.SalesOpen)
    {
        if (!attempt.CustomerBlocked)
        {
            if (attempt.RequestedSeats > 0)
            {
                if (attempt.RequestedSeats <= attempt.FreeSeats)
...
_audit.Add(attempt.Email + " -> " + result);
return result;
```

Test to macierz gałęzi z kombinacjami sprawdzającymi priorytet (np. brak seansu i jednocześnie zamknięta sprzedaż) plus zawartość audytu (właściwość `Audit`).

### Krok 1: Extract Method - oddziel decyzję od efektu

**W IDE:** zaznacz cały grot (od `string result;` do końca `if`), ⌥⌘M, nazwa `Decide` (Rider zrobi ją `static`, bo nie używa pól).
**Po:**

```csharp
public string Book(BookingAttempt attempt)
{
    var result = Decide(attempt);
    _audit.Add(attempt.Email + " -> " + result);
    return result;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s07` - 28 testów zielonych.
**Co powiedzieć:** najpierw chronimy efekt uboczny. Gdybyśmy wstawili guard clauses od razu w `Book`, każde wczesne `return` ominęłoby audyt - test pokazałby puste listy.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s07 0 1`

### Krok 2: odwróć najbardziej zewnętrzny warunek

**W IDE:** kursor na `if (attempt.ScreeningFound)`, ⌥⏎ → Invert 'if', potem usuń `else` (⌥⏎ → Remove redundant 'else' albo ręcznie) i zamień przypisanie na `return "NO_SCREENING"`.
**Po:**

```csharp
if (!attempt.ScreeningFound)
{
    return "NO_SCREENING";
}
string result;
if (attempt.SalesOpen) { ...
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden poziom naraz, test po każdym. Kolejność warunków się nie zmienia - to ona definiuje priorytet błędów.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s07 1 2`

### Krok 3: spłaszcz pozostałe poziomy

**W IDE:** powtórz ruch z kroku 2 dla każdego kolejnego poziomu. Na końcu zmienna `result` znika (Inline ⌥⌘N).
**Po:**

```csharp
if (!attempt.ScreeningFound) { return "NO_SCREENING"; }
if (!attempt.SalesOpen) { return "SALES_CLOSED"; }
if (attempt.CustomerBlocked) { return "CUSTOMER_BLOCKED"; }
if (attempt.RequestedSeats <= 0) { return "NO_SEATS_REQUESTED"; }
if (attempt.RequestedSeats > attempt.FreeSeats) { return "SOLD_OUT"; }
return "BOOKED";
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaprzeczenie `RequestedSeats <= FreeSeats` to `>`, nie `>=` - przypadek "dokładnie tyle wolnych" w teście tego pilnuje.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s07 2 3`

### Rozwiązanie i uzasadnienie

`Step3/BookingGate`: czysta funkcja decyzji z guard clauses i jedno miejsce efektu ubocznego. Priorytet warunków identyczny jak w `Start`.

### Pułapki

- Guard clause w metodzie z efektem na końcu (audyt, log, zwolnienie zasobu) - wczesny `return` go omija. Zasoby: `using` albo `try`/`finally`.
- Przestawienie warunków "od najczęstszego" - zmienia priorytet błędów.
- Błędne zaprzeczenie przy odwracaniu (`<=` na `>=`).

### Pytanie do sali

Gdyby `CustomerBlocked` był właściwością robiącą zapytanie do bazy, czy wolno przenieść go na początek?

## Scena s08. Introduce Design by Contract Checks - pula miejsc

**Temat ze slajdów:** 8. Introduce Design by Contract Checks; Design by Contract - jawne kontrole
**Namespace:** `Training.Workshop.M7.S08DesignByContract` (katalog `csharp/src/Training.Workshop/M7/S08DesignByContract`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s08` (`S08ContractTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `SeatPool` przyjmuje każde wywołanie, więc `Reserve(-2)` po cichu dodaje miejsca, a nadmiarowe `Release` daje więcej wolnych miejsc, niż ma sala. Dodajemy jawne warunki wstępne przez `Contracts.Require`, a potem warunki końcowe i niezmiennik.

**Zasada:** Design by Contract rozróżnia warunek wstępny (obowiązek klienta), warunek końcowy (gwarancję operacji) i niezmiennik (właściwość prawdziwą między operacjami). Kontrole piszemy jawnie, a nie przez `Debug.Assert`, który działa tylko w buildzie Debug, i stawiamy je przed pierwszą mutacją. Odpowiedź `false` przy braku miejsc to poprawny wynik, a nie naruszenie kontraktu.

**Efekt:** Zachowanie świadomie się zmienia dla niepoprawnych wejść: zamiast psuć stan, `SeatPool` rzuca wyjątek i zostaje nietknięty, więc krok 1 nie jest refaktoryzacją. Dla poprawnych wywołań nic się nie zmienia, a warunki końcowe i niezmiennik chronią przyszłe zmiany.

**Różnica względem Javy:** javowy `assert` (wymaga `-ea`) ma w .NET odpowiednik `Debug.Assert` (kompilowany tylko z symbolem `DEBUG`) - komentarz klasy `Contracts` porównuje oba. `Require` rzuca `ArgumentException` (w Javie `IllegalArgumentException`), `Ensure` - `InvalidOperationException` (w Javie `IllegalStateException`).

### Co widzimy

`SeatPool` pilnuje liczby wolnych miejsc seansu. Nie ma żadnych kontraktów: `Reserve(-2)` po cichu dodaje dwa miejsca, a `Release(15)` po rezerwacji 10 daje 105 wolnych w sali na 100. Test `StartSilentlyCorruptsStateForInvalidInput` dokumentuje te niemożliwe stany.

```csharp
public bool Reserve(int seats)
{
    if (seats > _remaining)
    {
        return false;
    }
    _remaining = _remaining - seats;
    return true;
}
```

Uwaga: `false` przy braku miejsc to **poprawna, udokumentowana odpowiedź**, nie naruszenie kontraktu.

### Krok 1: warunki wstępne (to nie jest refaktoryzacja)

**W IDE:** utwórz klasę `internal static class Contracts` z `Require` (`ArgumentException`) i `Ensure` (`InvalidOperationException`). Na początku `Reserve`, `Release` i konstruktora dodaj `Contracts.Require(...)` - **przed pierwszą mutacją**.
**Po:**

```csharp
public void Release(int seats)
{
    Contracts.Require(seats > 0, "seats must be positive");
    Contracts.Require(seats <= _capacity - _remaining, "cannot release more seats than reserved");
    _remaining = _remaining + seats;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s08` - poprawne użycia bez zmian, niepoprawne rzucają wyjątek i nie zmieniają stanu (`ArgumentException: seats must be positive, zostalo 100`, `... cannot release more seats than reserved, zostalo 90`).
**Co powiedzieć:** dla niepoprawnych wejść zachowanie się zmieniło - dlatego test ma dla nich osobne oczekiwania (`PreconditionsRejectInvalidInputAndLeaveStateUntouched`). Nie zamieniamy `false` na wyjątek: to zmieniłoby kontrakt także dla poprawnych klientów.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s08 0 1`

### Krok 2: warunki końcowe i niezmiennik

**W IDE:** ręcznie: zapamiętaj `previous`, policz `next`, `CheckInvariant(next)` przed przypisaniem, po przypisaniu `Contracts.Ensure(...)`.
**Po:**

```csharp
var previous = _remaining;
var next = previous - seats;
CheckInvariant(next);
_remaining = next;
Contracts.Ensure(_remaining == previous - seats, "reserve must reduce remaining by seats");
```

**Uruchom:** test zielony - dla poprawnych wejść nic się nie zmienia (`ValidUsageBehavesTheSame`).
**Co powiedzieć:** przy obecnym kodzie te kontrole nie mogą się nie udać. Chronią przyszłe zmiany: ktoś dopisze rabat "2 za 1" i niezmiennik złapie błąd, zanim stan się zepsuje.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s08 1 2`

### Rozwiązanie i uzasadnienie

`Step2/SeatPool`: `Require` przed mutacją, niezmiennik sprawdzany dla kandydata przed przypisaniem, `Ensure` po operacji. Jawne kontrole zamiast `Debug.Assert`.

### Pułapki

- `Debug.Assert(seats > 0)` - wywołanie jest usuwane z buildu Release (`[Conditional("DEBUG")]`), a testy zwykle biegną w Debug. Test zielony, produkcja bez ochrony.
- Kontrola po mutacji - wyjątek leci, ale stan już jest zepsuty.
- Zamiana historycznego `ArgumentException` na własny wyjątek "przy okazji" - klienci mogą go łapać.
- Podklasa wzmacniająca warunek wstępny (np. "maks. 6 miejsc naraz") łamie LSP.

### Pytanie do sali

Czy "nie więcej miejsc, niż zostało" to warunek wstępny, czy zwykła odpowiedź `false`? Kto o tym decyduje?

## Scena s09. Remove Double Negative - wstęp do strefy VIP

**Temat ze slajdów:** 9. Remove Double Negative
**Namespace:** `Training.Workshop.M7.S09DoubleNegative` (katalog `csharp/src/Training.Workshop/M7/S09DoubleNegative`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s09` (`S09EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `LoungeAccess` sprawdza `!customer.NotVip` i `!voucher.IsNotExpired(today)`, więc każdy warunek trzeba odwracać w głowie. Dodajemy pozytywne predykaty delegujące, migrujemy użycia po jednym i na końcu odwracamy delegację, zmieniając właściwość pozycyjną rekordu na `Vip`.

**Zasada:** Remove Double Negative zastępuje negatywną nazwę pozytywną, która musi być dokładnym logicznym dopełnieniem starej, także na granicy i dla `null`. Bezpieczna sekwencja to predykat pozytywny delegujący, migracja po jednym użyciu i odwrócenie delegacji. Negatywna nazwa w bazie, JSON-ie czy konfiguracji wymaga osobnej migracji granicy.

**Efekt:** Warunki czytają się wprost: `customer.Vip` i `voucher.IsExpired(today)`. Kosztem jest zmiana właściwości pozycyjnej rekordu `Customer` - każdy, kto go tworzy, musi odwrócić wartość, co widać w adapterze testu.

**Różnica względem Javy:** `notVip()` i `vip()` to w C# właściwości (`NotVip`, `Vip`). W kroku 1 `Vip` jest właściwością wyliczaną, a w kroku 3 staje się właściwością pozycyjną rekordu, więc użycia w `LoungeAccess` się nie zmieniają. Dochodzi C#-owa pułapka: wywołania pozycyjne konstruktora kompilują się dalej z odwróconym znaczeniem, łamią się tylko wywołania z nazwanym argumentem `NotVip:`.

### Co widzimy

`LoungeAccess` decyduje, czy klient wejdzie do strefy VIP. `Customer` ma właściwość pozycyjną `NotVip`, a `Voucher` predykat `IsNotExpired`. Każdy warunek trzeba odwracać w głowie.

```csharp
if (!customer.NotVip)
{
    return true;
}
...
if (!voucher.IsNotExpired(today))
{
    return false;
}
```

### Krok 1: pozytywne predykaty delegujące

**W IDE:** w `Customer` dodaj właściwość `Vip => !NotVip`, w `Voucher` metodę `IsExpired(today)` zwracającą `!IsNotExpired(today)`. `LoungeAccess` bez zmian.
**Po:**

```csharp
public bool IsExpired(DateOnly today)
{
    return !IsNotExpired(today);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s09` - 20 testów zielonych.
**Co powiedzieć:** pozytywna nazwa musi być dokładnym dopełnieniem starej. Voucher ważny "do dziś włącznie" - test ma przypadek granicy.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s09 0 1`

### Krok 2: migracja użyć po jednym

**W IDE:** w `LoungeAccess` zamień `!customer.NotVip` na `customer.Vip` (w obu metodach) i `!voucher.IsNotExpired(today)` na `voucher.IsExpired(today)`. Test po każdej zamianie.
**Po:**

```csharp
if (customer.Vip)
{
    return true;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** mechaniczna zamiana `!Negatyw` na `Pozytyw` - bez wymyślania logiki od nowa.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s09 1 2`

### Krok 3: odwrócenie delegacji

**W IDE:** w `Voucher` Inline (⌥⌘N) `IsNotExpired` do `IsExpired` i uprość `!!` do `today > ValidUntil`. W `Customer` usuń wyliczaną właściwość `Vip`, a parametr pozycyjny `NotVip` przemianuj (⇧F6) na `Vip` - to wymaga odwrócenia wartości u wszystkich, którzy tworzą rekord. Zrób to ręcznie, bo Rename sam znaczenia nie odwróci.
**Po:**

```csharp
public sealed record Customer(string Email, bool Vip);
```

**Uruchom:** test zielony. Zwróć uwagę na adapter w teście: dla `Step3` tworzy `new Customer("anna@kino.pl", !v.NotVip)`.
**Co powiedzieć:** to jest migracja granicy. Gdyby rekord trafiał do JSON-a albo bazy, pole `NotVip` w danych trzeba by migrować osobno.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s09 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `customer.Vip` i `voucher.IsExpired(today)`. Sekwencja ze slajdu: predykat pozytywny delegujący, migracja po jednym użyciu, odwrócenie delegacji.

### Pułapki

- `bool?` nie jest dwustanowy: `!NotVip` dla `null` daje `null` (i nie przejdzie jako warunek `if` bez rzutowania), `NotVip != true` daje `true`. Pozytywna wersja musi zachować, co znaczy `null`.
- Rename parametru pozycyjnego rekordu przez ⇧F6 bez odwrócenia wartości - wywołania pozycyjne kompilują się dalej, a znaczenie jest odwrotne.
- "Dopełnienie" z przesuniętą granicą (`today >= ValidUntil` zamiast `today > ValidUntil`).

### Pytanie do sali

Gdzie w Waszym kodzie negatywna nazwa żyje w kolumnie bazy albo w pliku konfiguracyjnym?

## Scena s10. Remove Boolean Method Parameters - migracja API

**Temat ze slajdów:** 11. Remove Boolean Method Parameters; Java 25 w tym module (zgodność binarna - w C# pokazana na prawdziwych assembly .NET)
**Namespace:** `Training.Workshop.M7.S10BooleanParameter` (katalog `csharp/src/Training.Workshop/M7/S10BooleanParameter`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s10` (`S10EquivalenceTest`, `S10BinaryCompatibilityTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketService.Book` ma dwie flagi, więc wywołanie `Book(..., true, false)` nic nie mówi bez zaglądania do sygnatury. Kanał zamieniamy na jawne metody `BookOnline` i `BookAtBoxOffice`, okulary na enum `Glasses`, migrujemy klientów po jednym i dopiero na końcu usuwamy starą metodę.

**Zasada:** Remove Boolean Method Parameters zastępuje literał `true`/`false`, który steruje zachowaniem, jawną metodą albo typem. Nie każdy `bool` jest flagą: dana z formularza zostaje daną, a przy wielu flagach zamiast metody na każdą kombinację robimy przegląd odpowiedzialności i obiekt polityki. Stara metoda zostaje jako `[Obsolete]` i delegująca do końca migracji, bo jej usunięcie łamie zgodność binarną.

**Efekt:** Wywołania w `MobileApp` i `BoxOfficeTerminal` czytają się bez sygnatury, a ostrzeżenia CS0618 (i tłumiące je pragmy) znikają. Usunięcie starej metody w bibliotece publicznej to zmiana łamiąca zgodność, więc wymaga jawnego kryterium końca migracji.

**Różnica względem Javy:** kroki są 1:1 z Javą, ale projekt ma `TreatWarningsAsErrors`, więc ostrzeżenie CS0618 w niezmigrowanych klientach byłoby błędem kompilacji. Dlatego `MobileApp` i `BoxOfficeTerminal` w krokach 1-2 mają lokalne `#pragma warning disable CS0618` z komentarzem "to lista klientów do migracji", a krok 3 usuwa pragmy razem z migracją. W Javie ostrzeżenia `[deprecation]` są tylko ostrzeżeniami. Dochodzi też test `S10BinaryCompatibilityTest` (4 przypadki, tylko w C#), który pokazuje zgodność binarną na prawdziwych assembly - patrz sekcja po kroku 4.

### Co widzimy

`TicketService.Book(title, format, seats, online, ownGlasses)` ma dwie flagi. Klienci: `MobileApp` woła `Book(..., true, false)`, `BoxOfficeTerminal` woła `Book(..., false, customerHasGlasses)`. Z miejsca wywołania nie widać, co znaczy `true`.

```csharp
return _tickets.Book(title, format, seats, true, false);
```

`customerHasGlasses` w terminalu to dana z formularza, a nie flaga sterująca. Nie każdy `bool` jest zapachem.

### Krok 1: jawne metody dla kanału, stara metoda [Obsolete]

**W IDE:** dodaj `BookOnline(...)` i `BookAtBoxOffice(...)`, prywatne przeciążenie `Book(..., Channel, ...)` z prywatnym enumem `Channel { Online, BoxOffice }`. Stara publiczna metoda dostaje `[Obsolete("użyj BookOnline albo BookAtBoxOffice")]` i deleguje. Build od razu pokaże błędy CS0618 w obu klientach - dopisz tam `#pragma warning disable CS0618` / `restore` wokół wywołania.
**Po:**

```csharp
[Obsolete("użyj BookOnline albo BookAtBoxOffice")]
public string Book(string title, string format, int seats, bool online, bool ownGlasses)
{
    return online
        ? BookOnline(title, format, seats, ownGlasses)
        : BookAtBoxOffice(title, format, seats, ownGlasses);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s10` - 35 testów zielonych (31 testów równoważności i 4 testy zgodności binarnej). Kompilator zgłasza CS0618 w obu klientach - dopóki nie stoją tam pragmy, `TreatWarningsAsErrors` nie pozwoli zbudować projektu.
**Co powiedzieć:** ostrzeżenia kompilatora to lista klientów do migracji - u nas dodatkowo jawna, bo każda pragma to jeden klient. Stara metoda zostaje, bo skompilowany wcześniej klient z innego assembly odwołuje się do tej sygnatury w IL (zgodność binarna).
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s10 0 1`

### Krok 2: druga flaga jako typ

**W IDE:** utwórz `public enum Glasses { Own, Rented }`. Na `BookOnline` Change Signature (⌘F6): typ parametru `bool ownGlasses` na `Glasses glasses`. To samo dla `BookAtBoxOffice`. Stara metoda tłumaczy `bool` na `Glasses`.
**Po:**

```csharp
public string BookOnline(string title, string format, int seats, Glasses glasses)
```

**Uruchom:** test zielony.
**Co powiedzieć:** nie robimy czterech metod na każdą kombinację (`BookOnlineWithOwnGlasses`...). Kanał jest metodą, okulary są wartością. Przy trzech i więcej flagach - obiekt polityki i przegląd odpowiedzialności. I nie idziemy na skrót `Glasses glasses = Glasses.Rented`: wartość domyślna jest wkompilowana w miejsce wywołania, a dodanie parametru opcjonalnego zmienia sygnaturę metody w IL (komentarz w `TicketService` i test zgodności binarnej niżej).
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s10 1 2`

### Krok 3: migracja klientów

**W IDE:** w `MobileApp` zamień wywołanie na `BookOnline(title, format, seats, Glasses.Rented)` i usuń pragmę. Test. W `BoxOfficeTerminal` - `BookAtBoxOffice(...)` z tłumaczeniem `customerHasGlasses` na `Glasses`, pragma też znika. Test.
**Po:**

```csharp
return _tickets.BookOnline(title, format, seats, Glasses.Rented);
```

**Uruchom:** test zielony, w kodzie produkcyjnym nie ma już ani CS0618, ani pragm.
**Co powiedzieć:** jeden klient naraz, test po każdym. Wywołanie czyta się bez zaglądania do sygnatury.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s10 2 3`

### Krok 4: Safe Delete starej metody

**W IDE:** na starym `Book(...)` Safe Delete (⌘⌦) - Rider potwierdza brak użyć w kodzie produkcyjnym (został tylko prywatny `Book(..., Channel, ...)`).
**Po:**

```csharp
public string BookOnline(string title, string format, int seats, Glasses glasses) { ... }
public string BookAtBoxOffice(string title, string format, int seats, Glasses glasses) { ... }
```

**Uruchom:** test zielony (testy starego API w `S10EquivalenceTest` obejmują tylko `Start`..`Step3`).
**Co powiedzieć:** koniec okresu przejściowego to decyzja z jawnym kryterium ("wszyscy klienci zmigrowani, wydanie X"). W bibliotece publicznej to zmiana łamiąca zgodność binarną - i to możemy teraz pokazać, a nie tylko powiedzieć.
**Snapshot:** `Step4/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s10 3 4`

### Dodatkowo w C#: S10BinaryCompatibilityTest - zgodność binarna na prawdziwych assembly

W Javie zgodność binarna zostaje w sferze słów ("stary `.class` innego modułu oczekuje tej sygnatury"). W C# scena ma dodatkowy test, który to uruchamia. Roslyn kompiluje w pamięci "bibliotekę `CineLib`" w dwóch wersjach i "starego klienta" skompilowanego tylko przeciw wersji 1. Klient jest potem uruchamiany z wersją 2 podmienioną w kolekcjonowalnym `AssemblyLoadContext` - tak jak po wdrożeniu nowej biblioteki bez przebudowy modułów, które jej używają. Stary klient to jedna linia:

```csharp
public static string Run() => new CineLib.TicketService().Book("Diuna", "IMAX", 2, true, false);
```

Cztery przypadki:

1. `ObsoleteOverloadKeepsOldBinariesWorkingUntilSafeDelete` - biblioteką są **prawdziwe pliki sceny** (`TicketService.cs` i `Glasses.cs` z `Step3` i `Step4`, czytane z dysku, z przestrzenią nazw podmienioną na wspólne `CineLib`). Stary klient działa ze `Step3` (`Diuna IMAX x2 online: 84.00`, bo przeciążenie `[Obsolete]` wciąż istnieje), a ze `Step4` dostaje `MissingMethodException`.
2. `SafeDeleteIsAlsoASourceBreakForUnmigratedClients` - przebudowa starego klienta przeciw `Step4` kończy się błędem CS1061. Safe Delete łamie więc zgodność i binarną, i źródłową.
3. `AddingAnOptionalParameterCompilesButBreaksOldBinaries` - kuszący skrót "dodajmy `Glasses glasses = Glasses.Rented` do istniejącej `BookOnline(format, seats)`" jest zgodny źródłowo (rekompilacja klienta działa, `37.00`), ale stary binarny klient dostaje `MissingMethodException`, bo metoda z dwoma parametrami przestała istnieć w IL.
4. `DefaultValueIsBakedIntoTheCallerUntilItIsRecompiled` - zmiana wartości domyślnej (`Rented` na `Own`) nie dociera do starego klienta: nadal płaci `37.00` z wypożyczonymi okularami, a dopiero po rekompilacji `34.00`. Wartość domyślna jest stałą wkompilowaną w miejsce wywołania.

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s10` - 4 przypadki są częścią 35 testów sceny. Pokaż je w Rider z okna Unit Tests po kroku 4 albo przy Pytaniu do sali.
**Co powiedzieć:** w bibliotece publicznej migrację robimy przeciążeniem z `[Obsolete]` (ewentualnie `[Obsolete("...", error: true)]` jako etap pośredni przed usunięciem: nowy kod się nie skompiluje, stare binaria dalej działają), a nie parametrami opcjonalnymi. Usunięcie przeciążenia to zmiana łamiąca zgodność binarną i źródłową - robimy ją świadomie, w wydaniu z nowym numerem głównym.

### Rozwiązanie i uzasadnienie

`Step4/TicketService`: dwie jawne metody i enum. Migracja wg slajdu: jawne metody, klienci pojedynczo, stara metoda delegująca do końca migracji, usunięcie. `S10BinaryCompatibilityTest` pokazuje, dlaczego ta kolejność jest bezpieczna dla starych binariów, a skrót z parametrem opcjonalnym - nie.

### Pułapki

- Usunięcie starej metody w tym samym commicie co dodanie nowych.
- Zamiana każdego `bool` na enum, także danych z formularza.
- Metoda na każdą kombinację flag zamiast przeglądu odpowiedzialności.
- Parametr opcjonalny zamiast przeciążenia: zgodny źródłowo, ale łamie stare binaria, a wartość domyślna zostaje wkompilowana u klienta (przypadki 3 i 4 testu zgodności binarnej).
- `#pragma warning disable CS0618` na cały plik albo `<NoWarn>CS0618</NoWarn>` w projekcie - lista klientów do migracji znika, a stare API zostaje na zawsze.

### Pytanie do sali

Jak długo trzymać `[Obsolete]` w bibliotece używanej przez inne zespoły? Kto mierzy, czy ktoś go jeszcze woła?

## Scena s11. Remove Middle Man - fasada kina

**Temat ze slajdów:** 12. Remove Middle Man
**Namespace:** `Training.Workshop.M7.S11MiddleMan` (katalog `csharp/src/Training.Workshop/M7/S11MiddleMan`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s11` (`S11EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CinemaFacade` prawie wszystko deleguje 1:1 do `ScreeningCatalog`, ale w `FreeSeats` po cichu zamienia nieznany seans na 0 wolnych miejsc. Przenosimy to tłumaczenie do `SeatBadge`, migrujemy obu klientów do katalogu po jednym i usuwamy fasadę.

**Zasada:** Remove Middle Man usuwa pośrednika, który tylko przekazuje wywołania dalej, i pozwala klientom rozmawiać bezpośrednio z właściwym obiektem - ruch odwrotny to Hide Delegate. Najpierw sprawdzamy, czy pośrednik nie robi czegoś więcej (autoryzacja, transakcja, telemetria, retry, translacja błędów), bo fasada modułu czy seam testowy mogą być wartościowe.

**Efekt:** Klienci zależą bezpośrednio od `ScreeningCatalog`, a zachowanie "nieznany seans = WYPRZEDANE" zostało zachowane w jedynym miejscu, które go potrzebuje. To dziwne zachowanie jest teraz widoczne i czeka na osobną decyzję.

**Różnica względem Javy:** `NoSuchElementException` to `KeyNotFoundException`, a `LinkedHashMap` w katalogu to `OrderedDictionary<string, Screening>` (.NET 9+), żeby kolejność tablicy seansów była gwarantowana.

### Co widzimy

`CinemaFacade` deleguje 1:1 do `ScreeningCatalog` - prawie. `FreeSeats` po cichu tłumaczy `KeyNotFoundException` na 0, więc `SeatBadge` pokazuje "WYPRZEDANE" dla nieznanego seansu. Dwóch klientów: `SeatBadge` i `DailyBoard`.

```csharp
public int FreeSeats(string id)
{
    try
    {
        return _catalog.FreeSeats(id);
    }
    catch (KeyNotFoundException)
    {
        return 0;
    }
}
```

### Krok 1: pośrednik staje się czystym forwarderem

**W IDE:** przenieś `try/catch` do prywatnej metody `FreeSeats` w `SeatBadge` (jedyny klient, który polega na tłumaczeniu). W fasadzie zostaje zwykłe delegowanie.
**Po:**

```csharp
// SeatBadge
private int FreeSeats(string id)
{
    try
    {
        return _cinema.FreeSeats(id);
    }
    catch (KeyNotFoundException)
    {
        return 0;
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s11` - przypadek "nieznany seans wygląda jak wyprzedany" nadal zielony.
**Co powiedzieć:** najpierw sprawdź, co pośrednik naprawdę robi: autoryzacja, transakcja, telemetria, retry, translacja błędów. Tu była translacja - przenosimy ją do klienta, zanim usuniemy pośrednika.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s11 0 1`

### Krok 2: pierwszy klient rozmawia z katalogiem

**W IDE:** w `SeatBadge` Change Signature (⌘F6) konstruktora: `CinemaFacade` na `ScreeningCatalog`, pole przez ⇧F6 na `_catalog`.
**Po:**

```csharp
public SeatBadge(ScreeningCatalog catalog)
{
    ArgumentNullException.ThrowIfNull(catalog);
    _catalog = catalog;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeden klient naraz. `DailyBoard` wciąż używa fasady i działa.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s11 1 2`

### Krok 3: ostatni klient i Safe Delete fasady

**W IDE:** ten sam ruch w `DailyBoard` (`_cinema.Screenings()` na `_catalog.All()`), potem Safe Delete (⌘⌦) na `CinemaFacade`.
**Po:**

```csharp
return string.Join("\n", _catalog.All()
    .Select(screening => screening.Id + " " + screening.Title + " " + screening.Format));
```

**Uruchom:** test zielony.
**Co powiedzieć:** odwrotny ruch to Hide Delegate. Fasada modułu albo seam testowy mogą być wartościowe - usuwamy pośrednika, który nic nie wnosi.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s11 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: dwaj klienci zależą od `ScreeningCatalog`, zachowanie dla nieznanego seansu zachowane w jedynym miejscu, które go potrzebuje. Test `CatalogItselfRejectsUnknownScreening` pokazuje, dlaczego krok 1 był konieczny.

### Pułapki

- Inline wszystkich metod fasady naraz (⌥⌘N na każdej metodzie, bez automatycznego Remove Middleman w Rider) bez sprawdzenia, że któraś tłumaczy błędy.
- Usunięcie fasady, która jest granicą modułu (klienci z innych przestrzeni nazw albo assembly zaczną zależeć od szczegółów).
- Utrwalenie dziwnego zachowania ("nieznany = wyprzedany") bez notatki - to kandydat na osobną decyzję.

### Pytanie do sali

Czy "nieznany seans = WYPRZEDANE" to błąd? Kiedy wolno go poprawić?

## Scena s12. Return ASAP - wyszukiwanie wolnego miejsca

**Temat ze slajdów:** 13. Return ASAP
**Namespace:** `Training.Workshop.M7.S12ReturnAsap` (katalog `csharp/src/Training.Workshop/M7/S12ReturnAsap`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s12` (`S12EquivalenceTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `SeatFinder` trzyma się zasady jednego wyjścia: `SeatClass` ma zagnieżdżone `if` ze zmienną `result`, a `FirstFree` pętlę z flagą `found`. Zamieniamy je na guard clauses i `return` w miejscu, gdzie wynik jest już znany.

**Zasada:** Return ASAP każe zwracać wynik tam, gdzie jest ostateczny, zamiast nieść go w zmiennej i fladze do końca metody. Wczesny `return` nie może jednak ominąć wymaganej mutacji ani zmienić sposobu dostępu do danych, a `return` w `try` nadal uruchamia `finally`.

**Efekt:** Obie metody nie mają flag ani zmiennych wyniku, a licznik `Inspected` jest taki sam, bo `return` stoi po inkrementacji. Celowo zostają `Count` i `seats[index]`, bo `foreach` (enumerator) albo LINQ zmieniłby sposób czytania listy.

**Różnica względem Javy:** `Optional<String>` to `string?`, więc oczekiwany tekst ma `B10` albo `null` zamiast `Optional[B10]` albo `Optional.empty`. Licznik jest właściwością `Inspected`, a lista indeksowana przez `Count` i `seats[index]` (w Javie `size()` i `get(index)`).

### Co widzimy

`SeatFinder` ma dwie metody w stylu "jeden punkt wyjścia": `SeatClass` z zagnieżdżonymi `if` i zmienną `result`, oraz `FirstFree` z pętlą `while (!found && index < seats.Count)`. Licznik `Inspected` (metryka dla działu IT) jest efektem ubocznym, który musi przetrwać zmianę.

```csharp
while (!found && index < seats.Count)
{
    var seat = seats[index];
    _inspected++;
    if (seat.Row >= minRow && !seat.Taken)
    {
        result = seat.Label;
        found = true;
    }
    index++;
}
```

### Krok 1: guard clauses

**W IDE:** w `SeatClass` odwróć zewnętrzne `if` (⌥⏎ → Invert 'if'), zamień przypisania `result = ...` na `return ...`, na końcu Inline (⌥⌘N) `result`. W `FirstFree` guard `if (seats == null) { return null; }` zamiast otaczającego `if`.
**Po:**

```csharp
if (seat == null) { return "BRAK"; }
if (seat.Taken) { return "ZAJETE"; }
if (seat.Row >= vipFromRow) { return "VIP"; }
return "STANDARD";
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s12` - 12 testów zielonych.
**Co powiedzieć:** kolejność sprawdzeń zachowana: miejsce zajęte w rzędzie VIP to nadal "ZAJETE".
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s12 0 1`

### Krok 2: Return ASAP w pętli

**W IDE:** zamień `result = seat.Label; found = true;` na `return seat.Label;`, usuń `found` i `result`, ręcznie zamień `while` na `for` z licznikiem `index`. Ostatnia linia `return null;`.
**Po:**

```csharp
for (var index = 0; index < seats.Count; index++)
{
    var seat = seats[index];
    _inspected++;
    if (seat.Row >= minRow && !seat.Taken)
    {
        return seat.Label;
    }
}
return null;
```

**Uruchom:** test zielony - "B10 sprawdzono=3" dla pierwszego wolnego VIP.
**Co powiedzieć:** zwracamy tam, gdzie wynik jest ostateczny, ale **po** `_inspected++`. Gdyby `return` trafił przed inkrementację, licznik różniłby się o jeden i test by to złapał.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s12 1 2`

### Rozwiązanie i uzasadnienie

`Step2/SeatFinder`: bez flag i zmiennych wyniku, `Count`/`seats[index]` zachowane (enumerator w `foreach` zmieniłby sposób dostępu do listy, np. dla listy leniwej albo z licznikiem wywołań).

### Pułapki

- `return` w `try` uruchamia `finally`. C# nie pozwala na `return` w `finally` (CS0157), ale wyjątek rzucony w `finally` zastąpi wynik.
- Wczesny `return` przed wymaganą mutacją (licznik, log, zwolnienie blokady).
- "Przy okazji" zamiana na LINQ `FirstOrDefault` - inny sposób iteracji (enumerator), inne zachowanie dla `null` w liście, a licznik trzeba by zwiększać w lambdzie.

### Pytanie do sali

Czy reguła "jeden return na metodę" ma jeszcze sens w C# z `using` i `try`/`finally`?

## Scena s13. Remove God Class - kampania na kopii CinemaManager

**Temat ze slajdów:** 10. Remove God Classes - kampania, nie pojedynczy ruch; Remove God Classes - wydzielony fragment i ryzyka; Warsztat 3 (kontekst)
**Namespace:** `Training.Workshop.M7.S13GodClass` (katalog `csharp/src/Training.Workshop/M7/S13GodClass`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s13` (`S13GoldenMasterTest`)
**Czas:** ~30 min

### W skrócie

**Co robimy:** Kopia `CinemaManager` to ponad 370 linii z cennikiem, powiadomieniami, rezerwacjami w `object?[]` i raportami w jednej klasie. Prowadzimy kampanię czterech pionowych wycinków: `PricingService`, `NotificationService`, `Booking` z `BookingRepository` i `ReportService`, z golden masterem po każdym kroku.

**Zasada:** God Class to klasa o niskiej spójności, wielu powodach zmiany i roli centralnego węzła zależności, a nie po prostu długi plik. Usuwamy ją kampanią małych Extract Class i Move Method w obszarze bieżącej zmiany: stara klasa zostaje fasadą, a stan dostaje jednego właściciela. To nie jest przepisanie od nowa.

**Efekt:** Publiczne API `CinemaManager` i pełny wektor zachowania (maile, SMS-y, bramka, raporty) są identyczne, a dane rezerwacji mają nazwany typ. Świadomie zostają `double` w cenach, globalny magazyn `LegacyDb` i brak atomowości - to osobne decyzje na kolejne kroki.

**Różnica względem Javy:** `Start` to kopia C#-owego `Training.Workshop.Legacy.CinemaManager` (razem z `LegacyDb`, `LegacyMailer`, `LegacyPaymentGateway`) z podmienioną przestrzenią nazw. Hak zegara to statyczne pole `internal static Func<DateTime> Clock`, a adaptery `Driver` (po jednym na wariant, w przestrzeniach `Training.Workshop.Tests.M7.S13GodClass.Start`/`Step1..4`) sięgają do niego przez `InternalsVisibleTo` (w Javie: ten sam pakiet). W kroku 3 `LegacyDb.Bookings` staje się `internal`, bo C# nie pozwala wystawić wewnętrznego typu `Booking` publicznym polem, a `Booking` ma właściwości (`Status { get; set; }`, `Card { get; private set; }`) zamiast par getter/setter. W kroku 4 `ReportService` powstaje w konstruktorze `CinemaManager`, bo inicjalizator pola nie może czytać innego pola instancji. Test jest w kolekcji `[Collection("Legacy")]` (statyczny stan).

### Co widzimy

`Start/CinemaManager` to kopia legacy - ponad 370 linii, dane w `object?[]` z magicznymi indeksami, cennik, płatności, zwroty, lojalność, powiadomienia, raporty i rozliczenia w jednej klasie. Zanim cokolwiek ruszysz, pokaż mapę: metody, pola `LegacyDb`, efekty (mail, SMS, bramka).

```csharp
LegacyDb.Bookings[id] = [screeningId, email, phone,
    seats, types, web, total, 0, Clock(), null, sum];
...
int points = (int)((double)b[10]! / 10);
```

Ochrona: `S13GoldenMasterTest` puszcza ten sam scenariusz "jednego dnia kina" co golden master legacy (`S13Script`) i porównuje **pełny wektor**: wyniki wywołań, maile, SMS-y, operacje bramki, raport dzienny i rozliczenia. Oczekiwany tekst to kopia `cinema-manager.approved.txt` wpisana w teście jako raw string `Approved`. Każdy wariant ma adapter `Driver` implementujący `ICinemaUnderTest` (dostęp do haka `Clock`), więc test to jedna teoria z 5 przypadkami (`Start` i `Step1..4`).

### Krok 1: wycinek "cennik" - PricingService

**W IDE:** w `Book` zaznacz pętlę cen i rabat grupowy razem z zaokrągleniem, ⌥⌘M `TicketsSum`. Zamień parametr `object?[] s` na trzy wartości (Change Signature ⌘F6: `format`, `start`, `vipFromRow`). Wydziel `BookingFee`. Przenieś obie metody (Move, F6, albo ręcznie) do nowej klasy `internal sealed class PricingService`, pole `_pricing` w `CinemaManager`.
**Po:**

```csharp
double sum = _pricing.TicketsSum((int)s[1]!, (DateTime)s[2]!, (int)s[5]!,
    seats, types, ownGlasses);
double total = sum + _pricing.BookingFee(web, seats.Length);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s13` - golden master zielony dla `Start` i `Step1`.
**Co powiedzieć:** cennik ma jednego właściciela i nie zna układu `object?[]`. Arytmetyka `double` przeniesiona dosłownie - naprawa typu pieniędzy to osobna decyzja (scena s14).
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s13 0 1`

### Krok 2: wycinek "powiadomienia" - NotificationService

**W IDE:** każde wywołanie `LegacyMailer.Send/Sms` wydziel (⌥⌘M) do metody o nazwie zdarzenia: `BookingCreated`, `PaymentDeclined`, `TicketsPaid` (mail i SMS razem), `BookingExpired`, `BookingCancelled`. Przenieś je do `NotificationService`. `Fmt` przenieś do `Formats.Amount`, w `CinemaManager` zostaw delegację.
**Po:**

```csharp
_notifications.TicketsPaid(email, (string?)b[2], bookingId, (double)b[6]!, points);
```

**Uruchom:** test zielony.
**Co powiedzieć:** `CinemaManager` decyduje KIEDY powiadomić, `NotificationService` - CO i JAK. Kolejność efektów (mail przed SMS, powiadomienie po zapisie rezerwacji) jest częścią wektora - golden master pilnuje jej co do linii.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s13 1 2`

### Krok 3: wycinek "rezerwacje" - Booking i BookingRepository zamiast object?[]

**W IDE:** utwórz klasę `internal sealed class Booking` z nazwanymi właściwościami (status zostaje kodem `int`), `BookingRepository` z `NextId`, `Save`, `Find`, `All`. Zmień typ słownika `LegacyDb.Bookings` na `Dictionary<string, Booking>` (i widoczność na `internal`) i napraw błędy kompilacji jeden po drugim: `b[7]` na `b.Status`, `b[10]` na `b.TicketsSum`...
**Po:**

```csharp
var b = _bookings.Find(bookingId);
...
b.MarkPaid(card);
int points = (int)(b.TicketsSum / 10);
```

**Uruchom:** test zielony.
**Co powiedzieć:** kompilator prowadzi tę migrację - każdy magiczny indeks staje się błędem do naprawy. Magazyn zostaje globalny (`LegacyDb`), więc współdzielenie stanu między instancjami się nie zmienia. Przeniesienie słownika do instancji repozytorium zmieniłoby semantykę - to osobny krok. Kolejność iteracji rezerwacji nadal opiera się, jak w legacy, na kolejności wstawiania do `Dictionary` bez usuwania.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s13 2 3`

### Krok 4: wycinek "raporty" - ReportService

**W IDE:** `DailyReport` i `Settlement` przenieś (Move, F6) do nowej klasy `ReportService` z zależnością `BookingRepository`, utworzonej w konstruktorze `CinemaManager`. W `CinemaManager` zostają metody delegujące (stare API).
**Po:**

```csharp
public string DailyReport(DateOnly day)
{
    return _reports.DailyReport(day);
}

public string Settlement(string title, int week)
{
    return _reports.Settlement(title, week);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** raporty czytają rezerwacje przez repozytorium, więc zmiana magazynu nie dotknie raportów. Kampanię kontynuujemy (zwroty, lojalność, repertuar), dopóki przynosi wartość dla planowanych zmian - nie przepisujemy wszystkiego.
**Snapshot:** `Step4/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s13 3 4`

### Rozwiązanie i uzasadnienie

`Step4`: `CinemaManager` jest fasadą o niezmienionym publicznym API, cztery wydzielone klasy mają po jednym powodzie zmiany, a dane rezerwacji mają nazwany typ i jednego właściciela dostępu. Cztery kroki, cztery commity, po każdym identyczny wektor zachowania.

### Pułapki

- "Przepiszmy to od nowa" zamiast pionowych wycinków - brak punktu, w którym system jest zielony.
- Zmiana kolejności efektów przy wydzielaniu (np. SMS przed mailem, mail przed zapisem rezerwacji) - golden master pokazuje różnicę w jednej linii.
- Poprawianie błędów "przy okazji" (np. `double` na `decimal`) w kroku strukturalnym.
- Przeniesienie stanu do instancji usługi: zmiana współdzielenia i czasu życia.
- Transakcje: przykład nie zapewnia atomowości - wyjątek w `NotificationService` po zapisie rezerwacji zostawia rezerwację bez maila. To było tak samo przed zmianą, ale po wydzieleniu łatwiej to przeoczyć. Atomowość (transakcja, outbox, kompensacja) to osobna decyzja.

### Pytanie do sali

Który wycinek zrobilibyście jako następny, jeśli za tydzień dochodzi nowa zasada zwrotów?

## Scena s14. Refaktoryzacja a zmiana kontraktu - zwrot na BigDecimal

**Temat ze slajdów:** Refaktoryzacja a zmiana kontraktu; Pętla pracy (zmiany kontraktu w osobnych krokach)
**Namespace:** `Training.Workshop.M7.S14ContractChange` (katalog `csharp/src/Training.Workshop/M7/S14ContractChange`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s14` (`S14ContractTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundCalculator.Refund` liczy zwrot na `double`, a kontraktem jest też format z dwoma miejscami po przecinku. Robimy jedną czystą refaktoryzację, potem pokazujemy naiwne przejście na `decimal` (w C# odpowiednik `BigDecimal` z tytułu sceny), które zmienia dwie rzeczy naraz, i na koniec zostawiamy tylko uzgodnioną zmianę.

**Zasada:** Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy, a kryterium brzmi: klient nie dostrzega żadnej nieuzgodnionej różnicy. Zmiana typu, zaokrąglenia, formatu czy wyjątku to zmiana kontraktu, którą robimy i zatwierdzamy osobno od ruchów strukturalnych, a nie "przy okazji".

**Efekt:** Zachowanie świadomie się zmienia w jednym punkcie: 64.35 anulowane 2 godziny przed seansem daje 29.18 zamiast 29.17, zatwierdzone w osobnym commicie. Format "0.00" zostaje, a naiwny krok 2 nie powinien trafić do repozytorium.

**Różnica względem Javy:** `BigDecimal` to `decimal`. Pułapka formatu jest ta sama, ale mechanizm jest C#-owy: `decimal` niesie własną skalę, a `Math.Round` **nie dopisuje zer**, więc `Math.Max(..., 0m)` drukuje się jako `0`. Dlatego w kroku 3 poza przestawieniem zaokrąglenia za `Max()` format "0.00" przywraca jawne `ToString("F2")`. `(decimal)double` zastępuje `BigDecimal.valueOf(double)` (dla danych testu wynik identyczny), a komunikaty z `assertEquals(..., message)` są komentarzami nad `Assert.Equal`.

### Co widzimy

`RefundCalculator.Refund` liczy zwrot na `double` - dokładnie jak w legacy: 100%, 50% albo 0%, minus 3.00, nie poniżej zera, zaokrąglenie `Math.Floor(x * 100 + 0.5) / 100.0` (odpowiednik javowego `Math.round`, bo domyślne `Math.Round` w .NET zaokrągla bankiersko), format `"F2"`. Kontraktem jest także **format**: zawsze dwa miejsca po przecinku.

```csharp
refund = refund - 3.00;
if (refund < 0)
{
    refund = 0;
}
refund = Math.Floor(refund * 100 + 0.5) / 100.0;
return refund.ToString("F2", CultureInfo.InvariantCulture);
```

### Krok 1: czysta refaktoryzacja

**W IDE:** zaznacz łańcuch `if/else` wyboru udziału, ⌥⌘M, nazwa `Share`, zwraca `double` (0, 1.0, 0.5). W `Refund` jedno wyrażenie `ticketsPaid * Share(minutes) - 3.00`.
**Po:**

```csharp
double refund = ticketsPaid * Share(minutesBeforeStart) - 3.00;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s14` - wszystko zielone, łącznie z przypadkami brzegowymi (29.17 i 0.00 w `RefactoringStepKeepsHistoricalRoundingAndFormat`).
**Co powiedzieć:** `x * 1.0 == x` w `double` - arytmetyka jest identyczna co do bitu. To jest refaktoryzacja.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s14 0 1`

### Krok 2: "przy okazji" double na decimal

**W IDE:** pokaż naiwny ruch: `Share` zwraca `decimal`, liczymy `Math.Max(Math.Round((decimal)ticketsPaid * Share(...) - Fee, 2, MidpointRounding.AwayFromZero), 0m)`, zwracamy `ToString(CultureInfo.InvariantCulture)` bez formatu.
**Po:**

```csharp
var refund = Math.Max(
    Math.Round((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 2, MidpointRounding.AwayFromZero),
    0m);
return refund.ToString(CultureInfo.InvariantCulture);
```

**Uruchom:** test zwykłych zwrotów zielony (`OrdinaryRefundsNeverChange`), ale `ByTheWayStepChangedTwoThingsAtOnce` dokumentuje dwie zmiany: 64.35 zapłacone, anulowanie 2 h przed seansem daje 29.18 zamiast 29.17, a zwrot po starcie to "0" zamiast "0.00".
**Co powiedzieć:** krok wyglądał na porządki, a zmienił dwa elementy kontraktu. Pierwszy (grosz) można uzasadnić regułą domeny, drugi (format) to czysta regresja - paragon i mail pokażą "Zwrot: 0".
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s14 1 2`

### Krok 3: świadoma decyzja

**W IDE:** przestaw `Math.Max` do środka, przed `Math.Round(..., 2, AwayFromZero)`, i wróć do formatu `"F2"` - "0.00" wraca. Różnicę groszową zatwierdzamy jako zmianę kontraktu: komentarz XML z decyzją, nowe oczekiwanie w teście, osobny commit.
**Po:**

```csharp
var refund = Math.Round(
    Math.Max((decimal)ticketsPaid * Share(minutesBeforeStart) - Fee, 0m),
    2, MidpointRounding.AwayFromZero);
return refund.ToString("F2", CultureInfo.InvariantCulture);
```

**Uruchom:** test zielony: `DeliberateStepChangesOnlyTheApprovedRounding`.
**Co powiedzieć:** kryterium refaktoryzacji to "klient nie dostrzega żadnej **nieuzgodnionej** różnicy". Różnicę w groszach uzgodniliśmy, różnicy w formacie nie. W C# samo przestawienie `Max` nie wystarczy: `Math.Round(0m, 2)` to dalej `0`, bo zaokrąglenie nie podnosi skali - format trzeba napisać jawnie.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s14 2 3`

### Rozwiązanie i uzasadnienie

`Step3/RefundCalculator`: `decimal` i `AwayFromZero` (HALF_UP) zgodnie z regułą domeny, format bez zmian. Historia commitów: refaktoryzacja (krok 1), zmiana typu i reguły zaokrąglenia jako jawna zmiana kontraktu (krok 3). Krok 2 nie powinien trafić do repozytorium.

### Pułapki

- Test tylko na "okrągłych" kwotach - `double` i `decimal` dają ten sam wynik i zmiana przechodzi niezauważona.
- Liczenie dalej na `double` i konwersja na `decimal` dopiero na końcu (albo `Math.Round` na `double`) - binarne rozwinięcie (64.349999...) daje inny wynik zaokrąglenia. Na `decimal` przechodzimy na wejściu.
- `decimal` porównuje wartości (`0m == 0.00m` to `true`), ale `ToString()` drukuje skalę ("0" i "0.00"). Test porównujący liczby nie zobaczy zmiany formatu - test porównujący tekst tak.
- `Math.Round(x, 2)` bez trybu zaokrągla bankiersko (`ToEven`) - inny wynik niż HALF_UP np. dla 0.125 (0.12 zamiast 0.13), więc tryb podajemy zawsze jawnie.
- Zmiana typu wyjątku, kolejności efektów albo formatu "bo i tak ruszamy ten kod".

### Pytanie do sali

Kto zatwierdza zmianę kontraktu o 1 grosz: programista, księgowość czy klient API?

## Scena s15. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** Wektor obserwowalnego zachowania; Lista kontrolna przeglądu (zachowanie)
**Namespace:** `Training.Workshop.M7.S15BehaviourVector` (katalog `csharp/src/Training.Workshop/M7/S15BehaviourVector`) · **Test:** `scripts/warsztat.sh --lang cs test m7/s15` (`S15ResultOnlyTest`, `S15BehaviourVectorTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Po "porządkach" kolegi `TicketCheckout.Pay` wysyła mail z potwierdzeniem także klientowi z odrzuconą kartą, a test sprawdzający tylko wynik tego nie widzi. Wprowadzamy seam dla maili, naprawiamy regresję i dodajemy seam dla płatności, żeby test widział pełny wektor.

**Zasada:** Wektor obserwowalnego zachowania to wszystko, co klient może zauważyć: wynik, wyjątki (typ, komunikat, moment), stan po sukcesie i po błędzie, wywołania współpracowników z ich kolejnością, czas i granice. Test sprawdzający jeden wymiar przepuści regresję w pozostałych, dlatego granica kontraktu ma być świadomą decyzją, a nie skutkiem słabego testu.

**Efekt:** Test widzi wynik, wyjątek, maile i obciążenia karty w jednym dzienniku oraz status rezerwacji. Zachowanie świadomie się zmienia w kroku 2: przy odrzuconej karcie znika błędny mail o opłaceniu - to naprawa błędu, a nie refaktoryzacja, choć wynik metody pozostaje ten sam.

**Różnica względem Javy:** `Mailer` i `PaymentGateway` to delegaty, więc domyślny konstruktor przekazuje grupy metod `CinemaMailer.Send` i `CardTerminal.Charge`, a test - lambdy albo `list.Add`. `Objects.requireNonNull(card, "card")` to `ArgumentNullException.ThrowIfNull(card)`, więc wektor pokazuje `wyjatek=ArgumentNullException: card` (nazwa parametru z `ParamName`). Statusy są w PascalCase: `status=New`, `status=Paid`.

### Co widzimy

`TicketCheckout.Pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S15ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

```csharp
var charged = CardTerminal.Charge(card, booking.Amount);
if (charged)
{
    booking.MarkPaid();
}
else
{
    CinemaMailer.Send(booking.Email, "Platnosc odrzucona " + booking.Id);
}
CinemaMailer.Send(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
return charged ? "OK" : "DECLINED";
```

Pokaż tabelę ze slajdu: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**W IDE:** utwórz `public delegate void Mailer(string to, string text)`, Parameterize Constructor (pole `_mailer`, konstruktor z parametrem), domyślny konstruktor `: this(CinemaMailer.Send)`, zamień statyczne wywołania na `_mailer(...)`. Logika bez zmian.
**Po:**

```csharp
public TicketCheckout()
    : this(CinemaMailer.Send)
{
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m7/s15` - zielony. `Step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona B1" i "Bilety B1 oplacone: 114.00".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s15 0 1`

### Krok 2: naprawa regresji pod ochroną testu

**W IDE:** przenieś wysyłkę potwierdzenia do gałęzi sukcesu, zamień `charged ? "OK" : "DECLINED"` na dwa `return`.
**Po:**

```csharp
if (CardTerminal.Charge(card, booking.Amount))
{
    booking.MarkPaid();
    _mailer(booking.Email, "Bilety " + booking.Id + " oplacone: " + booking.Amount);
    return "OK";
}
_mailer(booking.Email, "Platnosc odrzucona " + booking.Id);
return "DECLINED";
```

**Uruchom:** test zielony: `FromStep2MailsAreCorrect`.
**Co powiedzieć:** to naprawa błędu, nie refaktoryzacja - przywracamy kontrakt sprzed "porządków". Wynik metody się nie zmienia, zmienia się efekt uboczny.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s15 1 2`

### Krok 3: seam dla płatności i pełny wektor

**W IDE:** utwórz `public delegate bool PaymentGateway(string card, Money amount)`, drugi parametr konstruktora, domyślnie `CardTerminal.Charge`. W teście oba fałszywe obiekty zapisują do **jednego** dziennika zdarzeń.
**Po:**

```csharp
public TicketCheckout()
    : this(CinemaMailer.Send, CardTerminal.Charge)
{
}
```

**Uruchom:** test zielony: `Step3ObservesTheFullVector` sprawdza wynik albo wyjątek (typ i komunikat), obciążenia i maile w jednej kolejności oraz status rezerwacji po operacji.
**Co powiedzieć:** jeden dziennik dla wszystkich współpracowników pokazuje kolejność: najpierw obciążenie, potem mail. Stan po błędzie (`New` po odrzuceniu) i brak efektów przy wyjątku (`wyjatek=ArgumentNullException: card; zdarzenia=[]; status=New`) też są częścią kontraktu.
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m7/s15 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketCheckout` z dwoma seamami i test, który obserwuje pięć wymiarów wektora: wynik, wyjątek, efekty, kolejność efektów i stan. Granica kontraktu jest świadomą decyzją, a nie skutkiem zbyt słabego testu.

### Pułapki

- Test "zielony, bo nic nie sprawdza" - pokrycie linii 100%, a efekty uboczne niewidoczne.
- Osobne listy dla maili i obciążeń - kolejność między współpracownikami ginie.
- Sprawdzanie komunikatu wyjątku bez sprawdzenia, że nie było efektów przed wyjątkiem.
- Logi: jeśli ktoś je parsuje (alerty, audyt), też należą do wektora - wtedy potrzebny kolejny seam (np. `ILogger` podstawiany w teście).

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

Po s05 i po s09 zrób przerwę na pytania. Sceny s03, s05, s09, s11 i s12 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 7). W s10 zostaw 2-3 minuty na `S10BinaryCompatibilityTest` - to jedyne miejsce w module, gdzie zgodność binarną widać w działaniu, a nie tylko w opowieści.
