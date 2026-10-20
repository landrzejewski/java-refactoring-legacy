# Moduł 3. Testowanie a bezpieczeństwo - warsztat CineLegacy (C#): przewodnik prowadzącego

Pięć scen w domenie kina pokazuje na żywo, jak zbudować sieć bezpieczeństwa wokół kodu, który jej nie ma: test charakterystyki przed pierwszą zmianą, seam w klasie, której nie da się uruchomić w teście, wektor obserwowalnego zachowania, równoległą weryfikację (shadow) i jej granice przy efektach ubocznych. Każda scena ma kod wyjściowy (`Start`), kompletne snapshoty po każdym kroku (`StepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie krok świadomie zmienia zachowanie (s03 krok 2, s04 krok 4), test ma osobne oczekiwania i pokazuje różnicę wprost.

Punktem odniesienia dla całego modułu jest golden master starego systemu: `CinemaManagerGoldenMasterTest` (`csharp/tests/Training.Workshop.Tests/Legacy`) odtwarza "jeden dzień kina" na nietkniętym `Training.Workshop.Legacy.CinemaManager` i porównuje pełny wektor zachowania (wyniki wywołań, maile, SMS-y, obciążenia i zwroty, raport dzienny, rozliczenia) z zatwierdzonym plikiem `src/test/resources/workshop/cinema-manager.approved.txt`. Plik jest wspólny dla Javy, C# i TypeScript (projekt testów C# dołącza go jako link i kopiuje do katalogu wyjściowego), bo wszystkie trzy wersje starego systemu dają identyczny wektor. To odpowiedź na pytanie "jak poradzić sobie bez testów?" w skali całego systemu: jeden szeroki, deterministyczny test, zanim powstanie jakikolwiek test jednostkowy. Sceny poniżej pokazują te same techniki w mniejszej skali. Pokaż golden master na początku modułu (3 min) i wracaj do niego przy s01 (charakterystyka) i s03 (wektor zachowania).

Sceny nie powtarzają przykładów z `Training.Module3` (`csharp/src/Training.Module3`) ani ćwiczeń z zadań modułu 3. Tamte ćwiczenia uczestnicy robią sami, a sceny poniżej służą do pokazu.

Kod portu C# leży w `csharp/src/Training.Workshop/M3/SNN.../{Start,Step1,...}`, testy (xUnit) w `csharp/tests/Training.Workshop.Tests/M3/SNN.../`. Port trzyma się przebiegu scen z Javy, a mechanizmy języka są tłumaczone na odpowiedniki .NET: interfejsy funkcyjne (`ReminderSender`, `Mailer`, `PaymentGateway`) to delegaty, `Supplier<X>` to `Func<X>`, `Clock` to `TimeProvider` (w testach `FakeTimeProvider` z `Microsoft.Extensions.Time.Testing`), domyślne `Locale` to `CultureInfo.CurrentCulture`, `sealed interface` z rekordami to interfejs z rekordami, a `final` to `sealed`. Kwoty to `decimal` albo wspólny typ `Money` (`decimal`, skala 2, zaokrąglenie `AwayFromZero`). Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
dotnet test csharp/tests/Training.Workshop.Tests --filter "FullyQualifiedName~CinemaManagerGoldenMasterTest"   # golden master starego systemu
scripts/warsztat.sh --lang cs list m3              # sceny modułu i ich kroki
scripts/warsztat.sh --lang cs test m3/s02          # testy jednej sceny
scripts/warsztat.sh --lang cs test m3              # wszystkie sceny modułu (107 testów)
scripts/warsztat.sh --lang cs diff m3/s04 1 2      # co zmienia krok 2 względem kroku 1 (0 = Start)
scripts/warsztat.sh --lang cs diff m3/s04 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m3/s04 2        # skopiuj Step2 do Start, gdy brakuje czasu
scripts/warsztat.sh --lang cs next m3/s04          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m3/s04         # przywróć Start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w Rider, uruchomienie testu sceny (ikona w gutterze przy klasie testu albo okno Unit Tests), zdanie z sekcji "Co powiedzieć". Zanim ruszysz kod, powiedz, jaki dowód da test po kroku.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m3/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu (namespace) `Start`. Snapshoty `StepN` służą do `diff` albo do przeskoku (`next`, `jump`), gdy coś się rozjedzie.
- Zanim zaczniesz, pokaż slajd 4.6 "Wektor obserwowalnego zachowania" i wracaj do niego przy każdej scenie: *co tu obserwujemy - wynik, wyjątek, stan, efekty, kolejność?*
- W s02 start **nie ma prawdziwych testów** i to jest puenta: test `StartCannotEvenRunInATest` tylko dokumentuje, że kodu nie da się uruchomić. Testy pojawiają się dopiero wtedy, gdy kod ma seam. Pierwsze ruchy to automaty IDE o niskim ryzyku (slajd 4.16).
- W s01 i s03 warto najpierw **zrobić naiwny ruch na żywo** (np. "poprawić" znalezisko z charakterystyki, sprawdzić tylko wynik), pokazać, czego test nie widzi, cofnąć (⌘Z) i dopiero wtedy wykonać krok z przewodnika.
- W s04 i s05 część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`): raport rozbieżności, dziennik efektów ubocznych. Testy, które **dokumentują stan startowy** (np. "start obciąża kartę dwa razy"), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `Start` - to znak, że start jest już naprawiony. Po pokazie: `scripts/warsztat.sh --lang cs reset m3/sNN`.
- Kroki, które zmieniają zachowanie (s03 krok 2, s04 krok 4), zapowiadaj na głos: "to jest osobny commit, nie refaktoryzacja".
- Skróty w Rider (keymap macOS w stylu IntelliJ): ⇧F6 Rename, ⌥⌘F Introduce Field, ⌥⌘P Introduce Parameter, ⌥⌘M Extract Method, ⌥⌘N Inline, F6 Move, ⌘F6 Change Signature, ⌘⌦ Safe Delete, ⌥⌘T Surround With, ⇧⌘↓ Move Statement Down, ⌃T Refactor This (menu wszystkich refaktoryzacji dostępnych w miejscu kursora, m.in. Extract Interface). Projekt ma `TreatWarningsAsErrors` (`csharp/Directory.Build.props`), więc każde nowe ostrzeżenie kompilatora zatrzyma build.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
|---|---|---|---|---|
| s01 | 4.2 Testy charakteryzujące; 4.4 Dobór przypadków; 4.8 Porównanie implementacji | 2 | `M3.S01Characterization` | ~15 min |
| s02 | 4.10 Seam; 4.12 Szew przejściowy; 4.14 Break Dependencies; 4.16 Ruchy IDE bez testów | 4 | `M3.S02BreakDependencies` | ~20 min |
| s03 | 4.6 Wektor obserwowalnego zachowania; 2.2 Weryfikacja stanu a zachowania | 3 | `M3.S03BehaviourVector` | ~12 min |
| s04 | 4.18 Równoległa weryfikacja (parallel run) | 4 | `M3.S04ParallelRun` | ~15 min |
| s05 | 4.19 Granice trybu shadow | 3 | `M3.S05ShadowLimits` | ~12 min |

Pełne nazwy namespace zaczynają się od `Training.Workshop.`, katalogi to `csharp/src/Training.Workshop/M3/S01Characterization` itd.

## Scena s01. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** 4.2 Testy charakteryzujące; 4.4 Dobór przypadków; 4.8 Porównanie implementacji (równoważność etapów); 4.6 Wektor obserwowalnego zachowania
**Namespace:** `Training.Workshop.M3.S01Characterization` · **Test:** `scripts/warsztat.sh --lang cs test m3/s01` (`S01CharacterizationTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `BookingConfirmation.Confirm` drukuje potwierdzenie bez żadnego testu, a w wydruku jest bieżący czas i kwoty zależne od bieżącej kultury (`CultureInfo.CurrentCulture`). Najpierw zapisujemy w teście pełny dokument, potem robimy szew na zegar i dopiero wtedy pierwszą ekstrakcję.

**Zasada:** Test charakterystyki zapisuje, co kod faktycznie robi, a nie co powinien robić - razem z dziwnymi regułami. Stosujemy go przed pierwszą zmianą kodu bez testów, a ten sam zestaw oczekiwań uruchamiamy na każdym etapie jako test równoważności. Nie mylić z testem, który liczy oczekiwania tym samym algorytmem co kod.

**Efekt:** Mamy deterministyczny test całego dokumentu, wstrzykiwany `TimeProvider` i wydzieloną metodę `TicketPrice`, a stary konstruktor dalej działa. Znaleziska (rabat od 11 biletów, kwoty zależne od kultury) zostają zapisane, ale niepoprawione, bo to osobne decyzje.

**Różnica względem Javy:** domyślne `Locale` to w C# `CultureInfo.CurrentCulture`, a zapach "formatowanie bez Locale" to `double.ToString("F2")` bez podanej kultury. Zamiast `Clock` jest `TimeProvider` (`TimeProvider.System` w produkcji, `FakeTimeProvider` z `Microsoft.Extensions.Time.Testing` w teście). Znacznik czasu port drukuje zawsze jako `yyyy-MM-ddTHH:mm:ss` (Java pomija sekundy, gdy są zerowe), a godzinę seansu jawnie jako `HH:mm`.

### Co widzimy

`BookingConfirmation.Confirm` drukuje potwierdzenie rezerwacji. Nie ma żadnego testu, a dokument czytają klienci i infolinia. Zanim cokolwiek zmienimy, zapisujemy, co kod **robi**. Na drodze stoją dwie rzeczy: bieżący czas w ostatniej linii i `ToString("F2")` bez kultury.

```csharp
if (b.TicketTypes.Count > 10)
{
    sum = sum * 0.9;
}
...
+ "Do zaplaty: " + (sum + fee).ToString("F2") + "\n"
+ "Wygenerowano: " + DateTime.Now.ToString("yyyy-MM-dd'T'HH:mm:ss", CultureInfo.InvariantCulture) + "\n";
```

W tej scenie najważniejszy jest test `S01CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `Start/Step1/Step2` tylko za nim nadąża.

**Ruch A (na `Start`, przed krokiem 1):** napisz `Assert.Equal("", new BookingConfirmation().Confirm(booking))`, uruchom i skopiuj rzeczywisty wynik z komunikatu błędu do oczekiwań. Test dalej jest czerwony, bo zmienia się sekunda. Dodaj "scrubber", który maskuje linię z czasem (`Regex.Replace(document, "Wygenerowano: .*\n", "Wygenerowano: <czas>\n")`), i ustaw kulturę jawnie na czas wywołania (`InCulture(pl-PL, ...)`). Teraz test jest zielony i deterministyczny. Dopisz przypadki na obu bokach progów: 10 i 11 biletów, rano i wieczorem, kasa i online.

W trakcie wychodzą dwa znaleziska, które test zapisuje, a nie poprawia:

- rabat grupowy działa od **11** biletów (`> 10`), choć regulamin mówi "10+" - przypadek nazwany `ZASTANE: ...`,
- kwoty zależą od kultury serwera: na `pl-PL` jest `74,00`, na `en-US` `74.00` (test `FoundDuringCharacterizationAmountsDependOnServerLocale`).

### Krok 1: Parameterize Constructor - wstrzyknięty TimeProvider

**W IDE:** w `Confirm` zamień `DateTime.Now` na `TimeProvider.System.GetLocalNow().DateTime`, zaznacz `TimeProvider.System` i wykonaj Introduce Field (⌥⌘F, inicjalizacja w konstruktorze, nazwa `_clock`). Potem w konstruktorze na tym samym wyrażeniu Introduce Parameter (⌥⌘P), nazwa `clock`. Rider nie tworzy przy tym przeciążenia delegującego (w IntelliJ opcja "Delegate via overloading method"), więc konstruktor bezargumentowy `: this(TimeProvider.System)` dopisz ręcznie - to dwie linie.
**Po:**

```csharp
private readonly TimeProvider _clock;

public BookingConfirmation()
    : this(TimeProvider.System)
{
}

public BookingConfirmation(TimeProvider clock)
{
    _clock = clock;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s01` - 15 testów zielonych, w tym `FromStep1TheWholeDocumentIsDeterministic` (z zatrzymanym `FakeTimeProvider` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Stary konstruktor zostaje, więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `Start`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 0 1`

### Krok 2: pierwsza refaktoryzacja pod ochroną testu

**W IDE:** zaznacz ciało pętli od `double p;` do `p = p - 5.00; }`, Extract Method (⌥⌘M), nazwa `TicketPrice`. Rider wykryje dwa wejścia (`b`, `t`) i jedno wyjście. W pętli zostaje `sum = sum + TicketPrice(b, t);`.
**Po:**

```csharp
foreach (var t in b.TicketTypes)
{
    sum = sum + TicketPrice(b, t);
}
```

**Uruchom:** test zielony - ten sam dokument, znak w znak.
**Co powiedzieć:** kusi, żeby przy okazji zamienić `double` na `decimal`, dodać `CultureInfo.InvariantCulture` i poprawić próg grupy. Każda z tych rzeczy zmienia wydruk, więc to trzy osobne decyzje z wymaganiem, a nie refaktoryzacja.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s01 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `Start`, `Step1` i `Step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, kultury albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj `CultureInfo.CurrentCulture` (i `CurrentUICulture`) ustawiamy na czas wywołania i przywracamy w `finally`. Kultura płynie z kontekstem wykonania, więc równolegle uruchomione klasy testów xUnit sobie nie przeszkadzają, ale ustawienie jej "na cały proces" (`CultureInfo.DefaultThreadCurrentCulture`) już by przeszkadzało.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `Start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s02. Break Dependencies - seam dla zadania przypomnień

**Temat ze slajdów:** 4.10 Seam, punkt aktywacji i cel rozrywania; 4.12 Minimalny szew przejściowy; 4.14 Break Dependencies - przed i po; 4.16 Bezpieczne ruchy automatami IDE, gdy brak testów
**Namespace:** `Training.Workshop.M3.S02BreakDependencies` · **Test:** `scripts/warsztat.sh --lang cs test m3/s02` (`S02SeamTest`)
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

Sekwencja ze slajdu 4.15: nazwij zachowanie i punkt obserwacji, wprowadź najmniejszy szew bez zmiany logiki, zabezpiecz go testem z fake/spy, przenieś wybór do jawnego punktu aktywacji, usuń konstrukcję przejściową. Charakterystyki nie da się napisać przed pierwszym ruchem, więc pierwsze ruchy muszą być mechaniczne i wspierane przez IDE (slajd 4.16).

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

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s02` - testy `Step1...` zielone: z fałszywą bazą zadanie działa, gdy nic nie jest do wysłania (`Step1RunsWithFakeStoreWhenNothingIsDue`).
**Co powiedzieć:** przekazujemy fabrykę, a nie gotową bazę, bo stary kod otwierał połączenie przy każdym `Run()`. Gdybyśmy zrobili `: this(new LegacyDatabase())`, wyjątek poleciałby już w konstruktorze - zmienilibyśmy czas życia zależności i moment błędu.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 0 1`

### Krok 2: TimeProvider jako zależność

**W IDE:** ręcznie (Introduce Parameter ⌥⌘P dodałby parametr do `Run()`, a potrzebujemy zależności konstruktora): pole `TimeProvider _clock`, drugi parametr konstruktora, w domyślnym konstruktorze `TimeProvider.System`, w `Run()` `_clock.GetLocalNow().DateTime`.
**Po:**

```csharp
var now = _clock.GetLocalNow().DateTime;
```

**Uruchom:** test zielony. Test `Step2ControlsTimeButStillHitsStaticMailer` pokazuje dwie rzeczy: przypadki bez wysyłki działają przy stałym zegarze, a przypadek "do wysłania" wciąż kończy się wyjątkiem SMTP.
**Co powiedzieć:** czas był ukrytym wejściem. Zegar czytamy w tym samym miejscu co wcześniej (po utworzeniu bazy), więc kolejność się nie zmienia. Następna przeszkoda jest widoczna w teście - statyczny mailer.
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 1 2`

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
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 2 3`

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
**Snapshot:** `Step4/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s02 3 4`

### Rozwiązanie i uzasadnienie

`Step4/ShowtimeReminderJob`: trzy zależności w konstruktorze, każda o najwęższym kontrakcie potrzebnym klientowi (`IBookingStore` z dwiema metodami, `TimeProvider`, `ReminderSender` z jedną sygnaturą). Reguła przypomnień nie zmieniła się ani o znak. Dopiero teraz można bezpiecznie dodać nową regułę biznesową (np. przypomnienie SMS), bo test widzi i czas, i wysyłki.

### Pułapki

- `: this(new LegacyDatabase())` w domyślnym konstruktorze: zmiana czasu życia i momentu wyjątku (test `Step1KeepsConnectionLifetimeOfProductionConstructor`).
- Interfejs "na zapas" z wszystkimi metodami `LegacyDatabase` zamiast dwóch potrzebnych.
- Seam, którego test nie może podstawić: interfejs `internal` w innym assembly bez `InternalsVisibleTo` albo klasa `sealed` w miejscu interfejsu czy delegatu.
- Zostawienie Subclass and Override na stałe: klasa otwarta do dziedziczenia (bez `sealed`, z metodą `virtual`) tylko dla testu.

### Pytanie do sali

Kiedy Subclass and Override jest lepszym pierwszym ruchem niż Parameterize Constructor?

## Scena s03. Wektor obserwowalnego zachowania - płatność za bilety

**Temat ze slajdów:** 4.6 Wektor obserwowalnego zachowania; 2.2 Weryfikacja stanu a weryfikacja zachowania; 3.4-3.5 Pokrycie: zalety, ograniczenia, progi, nadużycia
**Namespace:** `Training.Workshop.M3.S03BehaviourVector` · **Test:** `scripts/warsztat.sh --lang cs test m3/s03` (`S03BehaviourVectorTest`, `S03ResultOnlyTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Po "porządkach" kolegi `TicketCheckout.Pay` wysyła mail z potwierdzeniem także klientowi z odrzuconą kartą, a test sprawdzający tylko wynik tego nie widzi. Wprowadzamy seam dla maili, naprawiamy regresję i dodajemy seam dla płatności, żeby test widział pełny wektor.

**Zasada:** Wektor obserwowalnego zachowania to wszystko, co klient może zauważyć: wynik, wyjątki (typ, komunikat, moment), stan po sukcesie i po błędzie, wywołania współpracowników z ich kolejnością, czas i granice. Test sprawdzający jeden wymiar przepuści regresję w pozostałych, dlatego granica kontraktu ma być świadomą decyzją, a nie skutkiem słabego testu.

**Efekt:** Test widzi wynik, wyjątek, maile i obciążenia karty w jednym dzienniku oraz status rezerwacji. Zachowanie świadomie się zmienia w kroku 2: przy odrzuconej karcie znika błędny mail o opłaceniu - to naprawa błędu, a nie refaktoryzacja, choć wynik metody pozostaje ten sam.

**Różnica względem Javy:** `Mailer` i `PaymentGateway` to delegaty, więc domyślny konstruktor przekazuje grupy metod `CinemaMailer.Send` i `CardTerminal.Charge`, a test - lambdy albo `list.Add`. `Objects.requireNonNull(card, "card")` to `ArgumentNullException.ThrowIfNull(card)`, więc wektor pokazuje `wyjatek=ArgumentNullException: card` (nazwa parametru z `ParamName`). Statusy są w PascalCase: `status=New`, `status=Paid`.

### Co widzimy

`TicketCheckout.Pay` to kod **po** "porządkach" kolegi (Consolidate Duplicate Conditional Fragments): mail potwierdzający wysunięto za `if`, więc dostaje go także klient z odrzuconą kartą. Mailer i terminal są statyczne - z testu widać tylko wynik. `S03ResultOnlyTest` jest zielony dla każdego wariantu. Regresja przeszła.

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

Pokaż tabelę ze slajdu 4.6: wynik, wyjątki, stan, współpracownicy, czas, granice. Które z nich ten test widzi?

### Krok 1: seam dla maili - regresja staje się widoczna

**W IDE:** utwórz `public delegate void Mailer(string to, string text)`, Parameterize Constructor (pole `_mailer`, konstruktor z parametrem), domyślny konstruktor `: this(CinemaMailer.Send)`, zamień statyczne wywołania na `_mailer(...)`. Logika bez zmian.
**Po:**

```csharp
public TicketCheckout()
    : this(CinemaMailer.Send)
{
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m3/s03` - zielony. `Step1SeesTheRegressionThatResultOnlyTestMissed` dokumentuje, że po odrzuceniu karty idą dwa maile: "Platnosc odrzucona B1" i "Bilety B1 oplacone: 114.00".
**Co powiedzieć:** kod się nie zmienił, zmieniło się to, co widzimy. Test z samym wynikiem nie mógł tego wykryć.
**Snapshot:** `Step1/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s03 0 1`

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
**Snapshot:** `Step2/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s03 1 2`

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
**Snapshot:** `Step3/`  ·  **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s03 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketCheckout` z dwoma seamami i test, który obserwuje pięć wymiarów wektora: wynik, wyjątek, efekty, kolejność efektów i stan. Granica kontraktu jest świadomą decyzją, a nie skutkiem zbyt słabego testu.

### Pułapki

- Test "zielony, bo nic nie sprawdza" - pokrycie linii 100%, a efekty uboczne niewidoczne.
- Osobne listy dla maili i obciążeń - kolejność między współpracownikami ginie.
- Sprawdzanie komunikatu wyjątku bez sprawdzenia, że nie było efektów przed wyjątkiem.
- Logi: jeśli ktoś je parsuje (alerty, audyt), też należą do wektora - wtedy potrzebny kolejny seam (np. `ILogger` podstawiany w teście).

### Pytanie do sali

Które elementy wektora sprawdzają testy w Waszym najważniejszym module? Czego nie widzą?

## Scena s04. Równoległa weryfikacja (shadow) kalkulatora cen

**Temat ze slajdów:** 4.18 Równoległa weryfikacja (parallel run)
**Namespace:** `Training.Workshop.M3.S04ParallelRun` · **Test:** `scripts/warsztat.sh --lang cs test m3/s04` (`S04EquivalenceTest`, `S04SolutionTest`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PriceService` woła tylko stary kalkulator w `double`, a gotowy `CandidatePriceCalculator` nigdy nie był porównany z produkcją. Uruchamiamy kandydata w cieniu, zbieramy typowany raport rozbieżności, poprawiamy znaleziony błąd i wprowadzamy jawny tryb `MigrationMode`.

**Zasada:** Równoległa weryfikacja (shadow) liczy wynik obiema implementacjami na prawdziwym ruchu, ale klient dostaje wynik legacy, a awaria kandydata jest izolowana. Nadaje się dla czystych obliczeń bez efektów ubocznych. Zgodność obu implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.

**Efekt:** Raport wskazał złą kolejność rabatu porannego i zniżki procentowej, a po poprawce zostaje tylko awaria dla 4DX. W trybie `Candidate` zachowanie dla 4DX świadomie się zmienia (odrzucenie zamiast wyceny 0.00), a `Legacy` służy za natychmiastowe wycofanie, dopóki stary kod istnieje.

**Różnica względem Javy:** `sealed interface Verification` to w porcie interfejs `VerificationReport.IVerification` z trzema rekordami, a `switch` w `Problems()` ma gałąź `default` (C# nie sprawdza wyczerpania dla hierarchii interfejsów). Godzina seansu to `TimeOnly`, wypisywana jako `HH:mm`, a w raporcie awarii kandydata jest nazwa wyjątku .NET: `ArgumentException` zamiast `IllegalArgumentException`.

### Co widzimy

`Start.PriceService` woła tylko `LegacyPriceCalculator` (double). Obok leży `CandidatePriceCalculator` (Money), napisany przez zespół, ale nigdy nieporównany z produkcją. Jedyne opcje: "włączyć i zobaczyć" albo wieczne testy ręczne.

```csharp
public Money Price(TicketQuery query)
{
    return _legacy.Price(query);
}
```

### Krok 1: dodanie cienia

**W IDE:** w `Price` po wyliczeniu wyniku legacy wywołaj kandydata w `try/catch` (Surround With ⌥⌘T) i licz niezgodności.
**Po:**

```csharp
Money result = _legacy.Price(query);
try
{
    if (!_candidate.Price(query).Equals(result))
    {
        _mismatches++;
    }
}
catch (Exception)
{
    _mismatches++;
}
return result;
```

**Uruchom:** test zielony; `Step1ShadowSurvivesCandidateFailureButOnlyCountsMismatches` - wyjątek kandydata dla formatu 4DX nie przerywa sprzedaży.
**Co powiedzieć:** klient zawsze dostaje wynik legacy. Ale licznik mówi tylko, ŻE coś się różni - nie wiemy co.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 0 1`

### Krok 2: raport rozbieżności

**W IDE:** nowa klasa `VerificationReport` z interfejsem `IVerification` i rekordami `Agreement`, `Divergence`, `CandidateFailure`. Extract Method (⌥⌘M) `Verify` w `PriceService`.
**Po:**

```csharp
return legacyPrice.Equals(candidatePrice)
    ? new VerificationReport.Agreement(query, legacyPrice)
    : new VerificationReport.Divergence(query, legacyPrice, candidatePrice);
```

**Uruchom:** test zielony; `Step2ReportShowsWhatDivergedAndWhy` - raport pokazuje dwie rozbieżności (3D dziecko rano: 17.20 vs 19.20, 2D student rano: 13.75 vs 15.00) i awarię dla 4DX (`BLAD KANDYDATA 4DX NORMAL 20:00 rzad 1: ArgumentException: Nieznany format: 4DX`).
**Co powiedzieć:** z raportu widać wzorzec: rozbieżności tylko rano i tylko przy zniżce procentowej. Kandydat odejmuje rabat poranny przed procentem.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 1 2`

### Krok 3: poprawka znaleziona przez raport

**W IDE:** w `CandidatePriceCalculator` przenieś blok rabatu porannego (⇧⌘↓, Move Statement Down) za wyliczenie zniżki procentowej.
**Po:**

```csharp
Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
if (query.Start.Hour < 12)
{
    price = price.Minus(MorningDiscount);
}
```

**Uruchom:** test zielony; w raporcie zostaje tylko awaria 4DX.
**Co powiedzieć:** odrzucenie nieznanego formatu to świadoma decyzja (legacy wyceniało 4DX na 0.00!), a nie błąd. Zapisujemy ją jako zaakceptowaną różnicę.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 2 3`

### Krok 4: przełączenie

**W IDE:** enum `MigrationMode { Legacy, Shadow, Candidate }`, konstruktor `PriceService(MigrationMode, VerificationReport)`, wyrażenie `switch` w `Price`.
**Po:**

```csharp
return _mode switch
{
    MigrationMode.Legacy => _legacy.Price(query),
    MigrationMode.Shadow => Shadow(query),
    MigrationMode.Candidate => _candidate.Price(query),
    _ => throw new InvalidOperationException("nieznany tryb: " + _mode),
};
```

**Uruchom:** test zielony; równoważność obejmuje wszystkie trzy tryby, `Step4CandidateModeIsAuthoritativeAndRejectsUnknownFormat` pokazuje świadomą zmianę dla 4DX.
**Co powiedzieć:** tryb zamiast flagi `bool`, wybór w jednym miejscu. `Legacy` to natychmiastowe wycofanie, dopóki nie usuniemy starego kodu.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s04 3 4`

### Rozwiązanie i uzasadnienie

Shadow daje dowód na prawdziwym ruchu, zanim nowa implementacja stanie się autorytatywna. Typowane zdarzenia raportu pozwalają podjąć decyzję bez parsowania logów.

### Pułapki

- Brak izolacji awarii kandydata - wyjątek w cieniu zatrzymuje sprzedaż.
- Zgodność obu implementacji nie dowodzi poprawności: obie mogą mieć ten sam błąd (np. wycena 4DX na 0.00 w legacy).
- Produkcyjny cień kosztuje: latencja, timeout, limit ruchu.

### Pytanie do sali

Ile dni ruchu i ile zgodnych porównań wystarczy, żeby przełączyć tryb na `Candidate`? Kto o tym decyduje?

## Scena s05. Granice trybu shadow - efekty uboczne

**Temat ze slajdów:** 4.19 Granice trybu shadow
**Namespace:** `Training.Workshop.M3.S05ShadowLimits` · **Test:** `scripts/warsztat.sh --lang cs test m3/s05` (`S05EquivalenceTest`, `S05SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShadowBooking` stosuje shadow do rezerwacji z efektami ubocznymi, więc klient dostaje dwa maile i dwa obciążenia karty, a cień niczego nie zgłasza. Wprowadzamy port `IEffects`, w cieniu nagrywamy efekty zamiast je wykonywać, a na końcu wydzielamy czysty `BookingPlanner`, który zwraca plan jako dane.

**Zasada:** Podwójne wykonanie w trybie shadow jest bezpieczne tylko dla czystych obliczeń - płatności, maili, zapisów ani zdarzeń nie wolno powielać. Efekty zamieniamy w dane (Separate Query from Modifier): opis "co zrobić" można porównać, a wykonuje się go tylko raz. Zgodność zwracanego wyniku nie dowodzi zgodności efektów.

**Efekt:** Po rezerwacji klient dostaje jeden mail i jedno obciążenie, a cień porównuje zaplanowane efekty z efektami legacy i wyłapuje błędną kwotę w mailu kandydata. Bezpieczeństwo gwarantuje typ, bo `BookingPlanner` nie ma dostępu do portu efektów; kosztem jest rozdzielenie planu od wykonania i dodatkowy typ `BookingPlan`.

### Co widzimy

`Start.ShadowBooking` stosuje shadow "jak dla kalkulatora" do rezerwacji z efektami ubocznymi. `NewBookingFlow` sam obciąża kartę, zapisuje wiersz i wysyła mail. Test pokazuje: klient dostaje **dwa maile i dwa obciążenia**, a cień niczego nie zgłasza, bo porównuje tylko zwracany tekst.

```csharp
string result = _legacy.Book(request);
try
{
    string shadow = _candidate.Book(request); // też obciąża kartę i wysyła mail
```

### Krok 1: port efektów (Parameterize Constructor)

**W IDE:** ⌃T → Extract Interface na operacjach `Infrastructure` używanych przez nowy przepływ → `IEffects`; adapter `RealEffects`. ⌘F6 na konstruktorze `NewBookingFlow`: parametr `IEffects` zamiast `Infrastructure`.
**Po:**

```csharp
_candidate = new NewBookingFlow(new RealEffects(infra));
```

**Uruchom:** test zielony; `Step1PreparatoryRefactoringKeepsTheBugOnPurpose` - nadal dwa maile.
**Co powiedzieć:** refaktoryzacja przygotowawcza zachowuje zachowanie, także błędne. Mamy szew, który w następnym kroku zmieni wszystko.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s05 0 1`

### Krok 2: przechwycenie efektów w cieniu

**W IDE:** nowa klasa `RecordingEffects : IEffects` zapisuje zamiary w formacie dziennika. `ShadowBooking` przekazuje kandydatowi nagrywarkę i porównuje nagrane efekty z efektami, które faktycznie wykonało legacy.
**Po:**

```csharp
var recorder = new RecordingEffects();
string shadow = new NewBookingFlow(recorder).Book(request);
Compare("wynik", [result], [shadow]);
Compare("efekty", legacyEffects, recorder.Recorded());
```

**Uruchom:** test zielony; `Step2RecordsCandidateEffectsInsteadOfExecutingThem` - jeden mail, jedno obciążenie, a raport pokazuje rozbieżność w treści maila (kandydat pisze 50.00 zamiast 54.00).
**Co powiedzieć:** porównanie efektów znalazło błąd, którego porównanie wyniku nie widziało.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s05 1 2`

### Krok 3: Separate Query from Modifier - czysty plan

**W IDE:** z `NewBookingFlow.Book` wydziel (⌥⌘M, potem Move F6) klasę `BookingPlanner` z metodą `Plan`, która zwraca `BookingPlan(Result, Effects)`. `NewBookingFlow` wykonuje plan na porcie; `ShadowBooking` woła tylko `BookingPlanner`. `RecordingEffects` usuń (Safe Delete).
**Po:**

```csharp
BookingPlan plan = _candidate.Plan(request);
Compare("wynik", [result], [plan.Result]);
```

**Uruchom:** test zielony; `Step3ComparesAPurePlanAndNeverTouchesInfrastructure`.
**Co powiedzieć:** typ gwarantuje bezpieczeństwo: `BookingPlanner` nie ma dostępu do portu efektów, więc w cieniu nie da się go użyć "za mocno".
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m3/s05 2 3`

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

**Ścieżka pełna (~80 min):** golden master → s01 → s02 → s03 → s04 → s05, czyli kolejność sekcji 4 slajdów. Jeśli chcesz wcześniej ustawić język wektora zachowania, pokaż s03 przed s02 (scena sama wprowadza swoje seamy). Po s03 zrób przerwę na pytania. Sceny s03 i s05 dobrze nadają się na samodzielną pracę uczestników, jeśli brakuje czasu na pokaz (patrz plik zadań modułu 3).
