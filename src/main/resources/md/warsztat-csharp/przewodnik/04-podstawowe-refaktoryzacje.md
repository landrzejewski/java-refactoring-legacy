# Moduł 4. Podstawowe refaktoryzacje - warsztat CineLegacy (C#): przewodnik prowadzącego

Trzynaście krótkich scen w domenie kina pokazuje na żywo każdą technikę z modułu 4: od testu charakterystyki, przez Rename, Extract i Inline, Move Method i Move Field, Extract Class, aż po hermetyzację pól, kolekcji i warunków. Każda scena ma kod wyjściowy (`Start`), gotowe snapshoty po każdym kroku (`StepN`) i test, który po każdym ruchu w IDE ma być zielony. Tam, gdzie tematem jest pułapka semantyki, test ją dokumentuje: pokazuje, co by się zepsuło, i dlaczego bezpieczny krok wygląda inaczej.

Kod portu C# leży w `csharp/src/Training.Workshop/M4/SNN.../{Start,Step1,...}`, testy (xUnit) w `csharp/tests/Training.Workshop.Tests/M4/SNN.../`. Tam, gdzie C# ma inny mechanizm niż Java (`decimal` zamiast `BigDecimal`, `TimeProvider` zamiast `Clock`, właściwości zamiast getterów, `const` zamiast `static final`), scena używa idiomu C#, a akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej. Wspólne zasady portu: kwoty to `decimal` (literały `40.00m`, zaokrąglenie `Math.Round(x, 2, MidpointRounding.AwayFromZero)`), wydruki kwot i czasu są jawnie formatowane z `CultureInfo.InvariantCulture` (w C# konkatenacja `decimal` albo `DateTime` zależy od bieżącej kultury, w Javie `toString` nie), a wyjątek stanu to `InvalidOperationException`.

Sceny nie powtarzają studium "generator oferty wynajmu" z zadań modułu 4 (w repozytorium w wersji Java, `pl.training.module4`). Tamto studium uczestnicy robią sami, a sceny poniżej służą do pokazu.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m4            # sceny modułu i ich kroki
scripts/warsztat.sh --lang cs test m4/s05        # testy jednej sceny
scripts/warsztat.sh --lang cs test m4            # wszystkie sceny modułu (230 testów)
scripts/warsztat.sh --lang cs diff m4/s05 2 3    # co zmienia krok 3 względem kroku 2 (0 = Start)
scripts/warsztat.sh --lang cs diff m4/s05 2 3 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m4/s05 2      # skopiuj Step2 do Start, gdy brakuje czasu
scripts/warsztat.sh --lang cs next m4/s05        # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                         # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                         # krok wstecz
scripts/warsztat.sh status                       # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m4/s05       # przywróć Start po pokazie
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza.** Ruch w Rider, uruchomienie testu sceny (ikona w gutterze przy klasie testu albo okno Unit Tests), zdanie z sekcji "Co powiedzieć".
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m4/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujesz zawsze w katalogu (namespace) `Start`. Snapshoty `StepN` są po to, żeby pokazać `diff` albo przeskoczyć (`next`, `jump`), gdy coś się rozjedzie.
- W scenach o pułapkach (s01, s02, s05, s06, s07) warto najpierw **zrobić naiwny ruch na żywo**, pokazać czerwony test, cofnąć (⌘Z) i dopiero wtedy wykonać krok z przewodnika.
- Sceny s10 i s11 zawierają kroki, które **świadomie zmieniają zachowanie**. Test ma dla nich osobne oczekiwania. Powiedz to na głos: to osobny commit, a nie refaktoryzacja.
- Skróty w Rider (keymap macOS w stylu IntelliJ): ⇧F6 Rename, ⌥⌘V Introduce Variable, ⌥⌘F Introduce Field, ⌥⌘P Introduce Parameter, ⌥⌘M Extract Method, ⌥⌘N Inline, F6 Move, ⌘F6 Change Signature, ⌘⌦ Safe Delete, ⌃T Refactor This (menu wszystkich refaktoryzacji dostępnych w miejscu kursora). Gdy nie pamiętasz nazwy ruchu, ⌃T i wybór z listy zawsze działa.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
|---|---|---|---|---|
| s00 | Test charakterystyki przed pierwszą zmianą; Test równoważności etapów | 2 | `M4.S00Characterization` | ~15 min |
| s01 | Rename - cel i mechanika (nazwy poza C#) | 3 | `M4.S01Rename` | ~12 min |
| s02 | Extract Variable i moment ewaluacji | 3 | `M4.S02ExtractVariable` | ~8 min |
| s03 | Extract Constant i `const`/`readonly`; Replace Magic Numbers | 3 | `M4.S03MagicNumbers` | ~10 min |
| s04 | Extract Method - przepływ danych, mechanika i trudne przypadki | 3 | `M4.S04ExtractMethod` | ~15 min |
| s05 | Inline Variable i typ docelowy (liczba i moment ewaluacji) | 3 | `M4.S05InlineVariable` | ~12 min |
| s06 | Inline Method i jego ryzyka | 2 | `M4.S06InlineMethod` | ~8 min |
| s07 | Move Method - wybór właściciela, krok po kroku i ryzyka | 2 | `M4.S07MoveMethod` | ~10 min |
| s08 | Move Field krok po kroku i ryzyka | 3 | `M4.S08MoveField` | ~10 min |
| s09 | Extract Class - cel, mechanika i zły wynik | 3 | `M4.S09ExtractClass` | ~15 min |
| s10 | Encapsulate Field | 3 | `M4.S10EncapsulateField` | ~10 min |
| s11 | Encapsulate Collection; Trzy różne kontrakty kolekcji | 3 | `M4.S11EncapsulateCollection` | ~12 min |
| s12 | Encapsulate Conditional | 3 | `M4.S12EncapsulateConditional` | ~8 min |

Pełne nazwy namespace zaczynają się od `Training.Workshop.`, katalogi to `csharp/src/Training.Workshop/M4/S00Characterization` itd.

## Scena s00. Test charakterystyki przed pierwszą zmianą

**Temat ze slajdów:** Test charakterystyki przed pierwszą zmianą; Test równoważności etapów; Co ma pozostać niezmienione
**Namespace:** `Training.Workshop.M4.S00Characterization` · **Test:** `scripts/warsztat.sh --lang cs test m4/s00`
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

W tej scenie najważniejszy jest test `S00CharacterizationTest`. Powstaje w trzech ruchach (A, B, C), a kod `Start/Step1/Step2` tylko za nim nadąża.

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

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s00` - 15 testów zielonych, w tym `FromStep1TheWholeDocumentIsDeterministic` (z zatrzymanym `FakeTimeProvider` porównujemy także linię z czasem, bez scrubbera).
**Co powiedzieć:** to minimalna zmiana, która robi szew dla testu. Stary konstruktor zostaje, więc produkcyjni klienci nie widzą różnicy. Scrubber był rusztowaniem i od tego kroku jest potrzebny tylko dla `Start`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s00 0 1`

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
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s00 1 2`

### Rozwiązanie i uzasadnienie

Test charakterystyki porównuje **pełny dokument**, a nie wybrane liczby, dla przypadków po obu stronach progów. Ten sam zestaw oczekiwań uruchamiamy na `Start`, `Step1` i `Step2`, co jest testem równoważności etapów. Oczekiwania zapisaliśmy raz, zatwierdzone przez człowieka, a nie liczone na nowo przez starą wersję kodu. Kod zmienia się minimalnie: szew na czas i jedna ekstrakcja.

### Pułapki

- Test, który wylicza oczekiwania tym samym algorytmem co kod ("drugi raz to samo"), niczego nie charakteryzuje.
- Test zależny od strefy czasowej, kultury albo bieżącej daty przejdzie na laptopie i padnie na CI. Tutaj `CultureInfo.CurrentCulture` (i `CurrentUICulture`) ustawiamy na czas wywołania i przywracamy w `finally`. Kultura płynie z kontekstem wykonania, więc równolegle uruchomione klasy testów xUnit sobie nie przeszkadzają, ale ustawienie jej "na cały proces" (`CultureInfo.DefaultThreadCurrentCulture`) już by przeszkadzało.
- "Poprawienie" znaleziska z charakterystyki w tym samym commicie co refaktoryzacja. Znalezisko zapisujemy (`ZASTANE: ...`), zgłaszamy i poprawiamy osobno.
- Brak sprawdzenia czułości testu. Zmień na chwilę `> 10` na `>= 10` w `Start` i zobacz, że test padnie. Potem cofnij.

### Pytanie do sali

Znaleźliście w charakterystyce ewidentny błąd (rabat od 11 biletów). Kto decyduje, czy go poprawić, i jak powinien wyglądać commit z poprawką?

## Scena s01. Rename - nazwy, które żyją poza C#

**Temat ze slajdów:** Rename - cel i mechanika; Co ma pozostać niezmienione (refleksja, konfiguracja)
**Namespace:** `Training.Workshop.M4.S01Rename` · **Test:** `scripts/warsztat.sh --lang cs test m4/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** W `SalesReport` nazwy `Calc2`, `s`, `m`, `t` nic nie mówią, ale nazwa metody żyje w konfiguracji, a nazwy właściwości rekordu trafiają refleksją do nagłówka CSV. Zmieniamy nazwy od najbezpieczniejszych lokalnych do tych, które są kontraktem zewnętrznym.

**Zasada:** Rename nadaje nazwę opisującą rolę w kontekście, a nie typ ani implementację. Dla zmiennych lokalnych IDE robi to bezpiecznie, ale nazwa użyta w konfiguracji, refleksji, JSON czy ORM jest częścią API i wymaga strategii migracji. IDE nie widzi takich użyć, więc nie mylić operacji ⇧F6 z bezpieczną zmianą kontraktu.

**Efekt:** Kod czyta się po nazwach ról, a oba kontrakty zostają nietknięte: stara nazwa `Calc2` jako przestarzały delegat, nagłówek CSV jako jawna stała. Koszt to delegat, którego nie wolno usunąć, dopóki ktoś nie zmieni konfiguracji na serwerach.

**Różnica względem Javy:** metoda z konfiguracji nazywa się w C# `Calc2` (PascalCase), więc wpis to `report.method=Calc2`. Rekord `Line` ma w `Start`, `Step1` i `Step2` celowo właściwości `t`, `n`, `d` z małej litery (część zapachu), dzięki czemu nagłówek z refleksji (`GetProperties()`) jest taki sam jak w Javie: `t;n;d`. `@Deprecated` to `[Obsolete]`, a `Properties` to ręczne parsowanie `klucz=wartość`. `GetMethod` zwraca w C# `null` zamiast rzucać, więc `ReportJob` sam rzuca `MissingMethodException` i opakowuje go w `InvalidOperationException`.

### Co widzimy

`SalesReport.Calc2(IReadOnlyList<Sale> s, bool flag)` składa CSV dla dystrybutora: `m`, `x`, `l`, `b`, a wiersz to `Line(string t, int n, decimal d)`. Dwie nazwy żyją poza C#:

```csharp
// ReportJob - w produkcji plik report.properties na serwerze
internal const string Config = """
    report.method=Calc2
    report.onlineOnly=true
    """;
...
var method = typeof(SalesReport).GetMethod(name, [typeof(IReadOnlyList<Sale>), typeof(bool)])
    ?? throw new MissingMethodException(nameof(SalesReport), name);

// SalesReport - nagłówek CSV liczony refleksją z nazw właściwości rekordu
return string.Join(";", typeof(Line).GetProperties().Select(p => p.Name));
```

### Krok 1: Rename zmiennych lokalnych i parametrów

**W IDE:** kursor na `s`, ⇧F6, `sales`. Tak samo `flag` -> `onlineOnly`, `m` -> `linesByTitle`, `x` -> `sale`, `l` -> `previous` / `line`, `b` -> `csv`, `v` -> `values`, `c` i `p` -> `property`.
**Po:**

```csharp
public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
{
    var linesByTitle = new SortedDictionary<string, Line>(StringComparer.Ordinal);
    foreach (var sale in sales)
    {
        if (onlineOnly && !sale.Online)
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s01` - 14 testów zielonych.
**Co powiedzieć:** zasięg lokalny, zero użyć poza metodą, więc IDE robi to w stu procentach bezpiecznie. Od tego zaczynamy każde porządkowanie, bo tanie nazwy ułatwiają następne kroki.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s01 0 1`

### Krok 2: Rename metody z delegatem dla starej nazwy

**W IDE:** najpierw na żywo naiwnie: ⇧F6 na `Calc2`, nazwa `RevenueCsv`, bez zmiany wystąpień w tekście (text occurrences). Uruchom test: `EveryStepRunsTheConfiguredJob` jest czerwony (`InvalidOperationException: Zadanie raportu nie działa: ...`, w środku `MissingMethodException` dla `Calc2`). IDE nie wie, że tekst w konfiguracji to nazwa metody. Cofnij (⌘Z). Teraz poprawnie: skopiuj metodę, zmień nazwę kopii na `RevenueCsv`, a ciało `Calc2` zamień na delegowanie i oznacz `[Obsolete]`.
**Po:**

```csharp
/// <summary>
/// Stara nazwa z konfiguracji report.properties (report.method=Calc2).
/// Usunąć dopiero, gdy żaden serwer nie ma jej w konfiguracji.
/// </summary>
[Obsolete("Użyj RevenueCsv - Calc2 zostaje tylko dla konfiguracji report.properties")]
public string Calc2(IReadOnlyList<Sale> sales, bool onlineOnly)
{
    return RevenueCsv(sales, onlineOnly);
}

public string RevenueCsv(IReadOnlyList<Sale> sales, bool onlineOnly)
{
```

**Uruchom:** test zielony, zadanie z konfiguracji działa. Test, który wciąż woła `Calc2` wprost, wyłącza ostrzeżenie kompilatora przez `#pragma warning disable CS0618` (odpowiednik `@SuppressWarnings("deprecation")`).
**Co powiedzieć:** konfiguracja leży na serwerach, poza naszym repozytorium, więc nie zmienimy jej w tym samym commicie. Stara nazwa zostaje jako przestarzały delegat, dopóki ktoś nie zmieni konfiguracji wszędzie. To jest strategia migracji ze slajdu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s01 1 2`

### Krok 3: Rename właściwości rekordu po odcięciu kontraktu CSV

**W IDE:** najpierw naiwnie: ⇧F6 na `t` w `Line`, `Title` (Rider zmienia parametr rekordu i wygenerowaną z niego właściwość). Test pokaże nagłówek `Title;n;d` zamiast `t;n;d`: plik dla dystrybutora właśnie się zmienił. Cofnij. Teraz poprawnie: najpierw zamień refleksyjne `Header()` i `Row()` na jawny `CsvHeader = "t;n;d"` i jawne składanie wiersza (test zielony), dopiero potem ⇧F6 na `t`, `n`, `d` -> `Title`, `Tickets`, `Revenue`.
**Po:**

```csharp
/// <summary>Nagłówek uzgodniony z dystrybutorem - kontrakt zewnętrzny, NIE nazwy właściwości w C#.</summary>
internal const string CsvHeader = "t;n;d";

public sealed record Line(string Title, int Tickets, decimal Revenue);

private static string Row(Line line)
{
    return line.Title + ";" + line.Tickets + ";" + line.Revenue.ToString(CultureInfo.InvariantCulture);
}
```

**Uruchom:** test zielony, także `S01RenameLimitsTest.ReflectionTurnsCSharpNamesIntoTheCsvHeader`, który pokazuje, że refleksja na nowym rekordzie dałaby `Title;Tickets;Revenue`.
**Co powiedzieć:** nazwa w C# i nazwa w kontrakcie zewnętrznym to dwie różne rzeczy. Dopóki są sklejone refleksją, każdy Rename jest zmianą API. Przy okazji: jawny wiersz formatuje kwotę z `InvariantCulture`, tak jak robiła to refleksja (`Convert.ToString(..., CultureInfo.InvariantCulture)`), inaczej na serwerze z `pl-PL` w pliku pojawiłby się przecinek.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s01 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: wszystkie nazwy mówią o roli (`RevenueCsv`, `onlineOnly`, `linesByTitle`, `Line.Revenue`), a dwa kontrakty zewnętrzne (nazwa metody w konfiguracji i nagłówek CSV) są zachowane jawnie: przez delegat i przez stałą. Oba testy równoważności (wywołanie z C# i zadanie z konfiguracji) są zielone na każdym etapie.

### Pułapki

- Poleganie na zmianie wystąpień w tekście (text occurrences) przy Rename w Rider. Czasem pomaga, ale nie widzi plików poza projektem (konfiguracja na serwerze, `appsettings.*.json` w innym repozytorium) ani nazw składanych dynamicznie (`"Calc" + version`).
- Inne miejsca, w których nazwa jest kontraktem: JSON (`System.Text.Json`, Newtonsoft), kolumny i tabele EF Core bez jawnego mapowania, wiązanie konfiguracji (`IConfiguration.Bind`, `IOptions<T>` wiążą po nazwach właściwości), widoki Razor, nazwy w DI rejestrowane po nazwie, logi parsowane przez monitoring. `nameof(...)` jest bezpieczne, bo Rename je zmienia, ale wtedy zmienia też tekst, który ktoś może czytać.
- Usunięcie delegatu `Calc2` "bo nikt go nie woła". Find Usages go nie znajdzie, bo wołają go tylko refleksja i konfiguracja.

### Pytanie do sali

Jak w waszym systemie sprawdzić, czy nazwa klasy albo metody nie jest użyta w konfiguracji, bazie danych lub innym repozytorium?

## Scena s02. Extract Variable dla złożonego wyrażenia ceny

**Temat ze slajdów:** Extract Variable i moment ewaluacji
**Namespace:** `Training.Workshop.M4.S02ExtractVariable` · **Test:** `scripts/warsztat.sh --lang cs test m4/s02`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Cena biletu w `TicketPrice` to jedno długie wyrażenie z pięcioma ternary, którego nie da się przeczytać bez liczenia w głowie. Rozbijamy je na zmienne z nazwami z cennika: najpierw kwoty bazowe, potem warunki, na końcu dopłaty.

**Zasada:** Extract Variable nazywa znaczenie fragmentu wyrażenia, a nie jego składnię. Może jednak zmienić moment ewaluacji, więc trzeba uważać na wyrażenia z efektem, odczytem czasu albo wyjątkiem. Krótkie spięcie `&&` jest zachowaniem - nie wolno wydzielić prawej strony przed osłoną `!= null`.

**Efekt:** Ostatnia linia czyta się jak paragon, a kolejność działań i miejsce zaokrąglenia są takie same jak w `Start`. Liczby nadal są literałami, co zostawiamy na następną scenę.

**Różnica względem Javy:** rząd to `int?` (odpowiednik `Integer`). W C# porównanie `r.Row >= 10` na `int?` jest "podniesione" (lifted operator) i dla `null` po prostu zwraca `false`, bez wyjątku. Dlatego kod sceny pisze osłonę jawnie, jak w Javie: `r.Row != null && r.Row.Value >= 10`. Pułapka w C# to wydzielenie `r.Row.Value >= 10` przed osłoną: `.Value` na `null` rzuca `InvalidOperationException` (w Javie `NullPointerException` z unboxingu). Warto powiedzieć na sali, że idiomatyczne `r.Row >= 10` jest w C# bezpieczne właśnie dzięki lifted operators.

### Co widzimy

Cała cena biletu to jedno wyrażenie z pięcioma ternary. Żeby odpowiedzieć na pytanie "skąd 32.00?", trzeba policzyć je w głowie. Uwaga na `Row`: to `int?`, a `null` oznacza wolną widownię.

```csharp
return Math.Round((r.Format == 3 ? 40.00m
            : r.Format == 2 ? 32.00m : 25.00m)
        * (100 - (r.Type == "S" ? 25
            : r.Type == "E" ? 30 : r.Type == "C" ? 40 : 0))
        / 100, 2, MidpointRounding.AwayFromZero)
    - (r.Start < new TimeOnly(12, 0)
        ? 5.00m : 0m)
    + (r.Row != null && r.Row.Value >= 10 ? 10.00m : 0m)
    + (r.Format == 2 && !r.OwnGlasses ? 3.00m : 0m);
```

### Krok 1: Extract Variable dla ceny bazowej i zniżki

**W IDE:** zaznacz pierwszy nawias z ternary formatu, ⌥⌘V, `basePrice`. Potem zaznacz ternary typu biletu, ⌥⌘V, `discountPercent`. Na koniec zaznacz całe `Math.Round(basePrice * (100 - discountPercent) / 100, ...)`, ⌥⌘V, `discountedPrice`. Jeśli Rider zaproponuje `var`, wybierz jawny typ - w tej scenie typy (`decimal`, `int`) pomagają czytać cennik.
**Po:**

```csharp
decimal basePrice = r.Format == 3 ? 40.00m
    : r.Format == 2 ? 32.00m : 25.00m;
int discountPercent = r.Type == "S" ? 25
    : r.Type == "E" ? 30 : r.Type == "C" ? 40 : 0;
decimal discountedPrice = Math.Round(basePrice * (100 - discountPercent) / 100,
    2, MidpointRounding.AwayFromZero);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s02` - 25 testów zielonych.
**Co powiedzieć:** zmienna nazywa pojęcie z cennika, a nie fragment składni. Wyrażenia są czyste, więc przesunięcie ich ewaluacji wcześniej niczego nie zmienia.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s02 0 1`

### Krok 2: Extract Variable dla warunków

**W IDE:** zaznacz `r.Start < new TimeOnly(12, 0)`, ⌥⌘V, `morning`. Dla VIP zaznacz **całe** `r.Row != null && r.Row.Value >= 10`, ⌥⌘V, `vipSeat`. Potem `r.Format == 2 && !r.OwnGlasses` -> `needsGlasses`.
**Po:**

```csharp
bool morning = r.Start < new TimeOnly(12, 0);
bool vipSeat = r.Row != null && r.Row.Value >= 10;
bool needsGlasses = r.Format == 2 && !r.OwnGlasses;
```

**Uruchom:** test zielony, także przypadki "wolna widownia (Row = null)".
**Co powiedzieć:** gdybyśmy wydzielili samo `r.Row.Value >= 10`, zmienna policzyłaby się **przed** osłoną `!= null` i przy wolnej widowni poleciałby `InvalidOperationException` z `Nullable<int>.Value`. Krótkie spięcie jest zachowaniem. Pokazuje to test `ExtractingTheComparisonWithoutTheNullGuardThrows`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s02 1 2`

### Krok 3: Extract Variable dla kwot

**W IDE:** zaznacz każde `warunek ? kwota : 0m`, ⌥⌘V: `morningReduction`, `vipSurcharge`, `glassesFee`.
**Po:**

```csharp
decimal morningReduction = morning ? 5.00m : 0m;
decimal vipSurcharge = vipSeat ? 10.00m : 0m;
decimal glassesFee = needsGlasses ? 3.00m : 0m;
return discountedPrice - morningReduction + vipSurcharge + glassesFee;
```

**Uruchom:** test zielony.
**Co powiedzieć:** ostatnia linia czyta się jak paragon. Następny naturalny krok to Replace Magic Numbers (scena s03) albo Extract Method dla `basePrice` (scena s04).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s02 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketPrice`: każda część ceny ma nazwę, kolejność operacji arytmetycznych i miejsce zaokrąglenia są takie same jak w `Start`. Test obejmuje obie strony progów (11:59 i 12:00, rząd 9 i 10) oraz `Row = null`.

### Pułapki

- Wydzielenie jednej strony `&&` / `||` przed osłoną (`null`, `is`, `Count > 0`, `.Value` na `Nullable`).
- Scalenie dwóch identycznych wyrażeń w jedną zmienną, gdy wyrażenie ma efekt uboczny albo czyta czas. Wtedy zmienia się **liczba** ewaluacji (patrz s05).
- Nazwa opisująca składnię (`ternary1`, `tmp`) zamiast znaczenia.

### Pytanie do sali

Kiedy jedna zmienna `now = _clock.GetUtcNow()` zamiast dwóch odczytów zegara jest poprawką, a kiedy zmianą kontraktu?

## Scena s03. Stałe z nazwą zamiast magicznych liczb

**Temat ze slajdów:** Extract Constant i `final` (w C#: `const` i `readonly`); Replace Magic Numbers with Named Constants
**Namespace:** `Training.Workshop.M4.S03MagicNumbers` · **Test:** `scripts/warsztat.sh --lang cs test m4/s03`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `OrderPricer.Summary` jest pełen liczb bez nazw, a cztery dziesiątki oznaczają cztery różne decyzje biznesowe. Zamieniamy każdą liczbę na prywatną stałą z nazwą roli, osobno dla każdej reguły.

**Zasada:** Replace Magic Numbers usuwa ukrytą decyzję, a Extract Constant to mechanika tej zmiany. Nazwa opisuje rolę (`GroupMinTickets`), nie wartość (`Ten`), a dwa identyczne literały nie zawsze są tą samą wiedzą. Stała nie jest miejscem na konfigurację zmienianą bez wdrożenia, a `readonly` blokuje tylko przypisanie, nie zawartość.

**Efekt:** W metodzie nie ma żadnej liczby bez nazwy, a zmiana progu grupy nie przesunie już rzędu VIP. Piętnaście stałych pokazuje przy okazji, że cennik to osobna odpowiedzialność, którą jeszcze trzeba będzie przenieść.

**Różnica względem Javy:** `private static final BigDecimal` to w C# `private const decimal` - `decimal` może być stałą czasu kompilacji. Stałe mają nazwy w PascalCase (`BasePrice2D`, `VipFromRow`), a nie `UPPER_SNAKE_CASE`. `BigDecimal.TEN` to po prostu `10m`, a `divide(..., RoundingMode.DOWN)` to `decimal.Truncate`. Odpowiednikiem `final` na polu z listą jest `readonly`, a niemodyfikowalnej listy - `Array.AsReadOnly(...)`.

### Co widzimy

`OrderPricer.Summary` liczy bilety, opłatę online i punkty lojalnościowe. Liczby: 25.00, 32.00, 40.00, 0.25, 0.30, 0.40, 5.00, 12, 10, 10.00, 10, 0.90, 2.00 i 10 w dzieleniu przez punkty. Cztery "dziesiątki" oznaczają cztery różne decyzje biznesowe.

```csharp
if (t.Row >= 10)                                       // od którego rzędu VIP
{
    p = p + 10.00m;                                    // dopłata VIP
}
...
if (o.Tickets.Count >= 10)                             // próg grupy
{
    tickets = tickets * 0.90m;
}
int points = (int)decimal.Truncate(tickets / 10);      // zł za punkt
```

### Krok 1: Extract Constant dla kwot z cennika

**W IDE:** zaznacz `25.00m`, Refactor This (⌃T) > Introduce Field i zaznacz, że ma to być stała (`const`), nazwa `BasePrice2D`. Rider zapyta o zastąpienie wszystkich wystąpień, więc się zgódź, bo to ta sama wiedza. Tak samo `BasePrice3D`, `BasePriceImax`, `MorningReduction`, `VipSurcharge`, `OnlineFeePerTicket`, `NoFee`.
**Po:**

```csharp
private const decimal BasePrice2D = 25.00m;
private const decimal MorningReduction = 5.00m;
private const decimal VipSurcharge = 10.00m;
private const decimal OnlineFeePerTicket = 2.00m;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s03` - 17 testów zielonych.
**Co powiedzieć:** `decimal` jest typem wartościowym i może być `const`, więc to prawdziwa stała czasu kompilacji. Stała jest prywatna, bo ma jednego właściciela.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s03 0 1`

### Krok 2: Replace Magic Numbers - progi reguł

**W IDE:** zaznacz `10` w `t.Row >= 10`, Introduce Field jako `const`, `VipFromRow`. **Odmów** zastąpienia wszystkich wystąpień, bo pozostałe dziesiątki to inna wiedza. Osobno: `GroupMinTickets`, `GroupPriceFactor`, `AmountPerLoyaltyPoint` (z `10` w `tickets / 10`), `MorningEndsAtHour`.
**Po:**

```csharp
private const int MorningEndsAtHour = 12;
private const int VipFromRow = 10;
private const int GroupMinTickets = 10;
private const decimal GroupPriceFactor = 0.90m;
private const decimal AmountPerLoyaltyPoint = 10m;
```

**Uruchom:** test zielony.
**Co powiedzieć:** jeśli dyrekcja zmieni próg grupy na 8, rząd VIP nie może się przesunąć razem z nim. Nazwa opisuje rolę (`GroupMinTickets`), a nie wartość (`Ten`). Extract Constant to mechanika, a Replace Magic Number to powód.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s03 1 2`

### Krok 3: Extract Constant dla stawek zniżek

**W IDE:** zaznacz `0.25m`, Introduce Field jako `const`, `StudentDiscount`. Tak samo `SeniorDiscount` i `ChildDiscount`.
**Po:**

```csharp
if (t.Type == "S")
{
    p = p * (1 - StudentDiscount);
}
else if (t.Type == "E")
{
    p = p * (1 - SeniorDiscount);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** w metodzie nie ma już żadnej liczby bez nazwy. Następny ruch (już nie w tej scenie) to przeniesienie cennika do własnej klasy, bo stałe pokazują, że to osobna odpowiedzialność.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s03 2 3`

### Rozwiązanie i uzasadnienie

`Step3/OrderPricer`: piętnaście prywatnych stałych, każda z nazwą roli. Trzy progi o wartości 10 mają trzy różne nazwy, bo zmieniają się z różnych powodów. Test obejmuje 9 i 10 biletów, 12:00 i rano, kasę i online.

### Pułapki

- `readonly` blokuje tylko przypisanie. `readonly List<string>` z `["S", "E", "C"]` każdy może zmodyfikować. Test `ReadonlyDoesNotMakeACollectionConstant` pokazuje to na liście typów zniżkowych, a `Array.AsReadOnly(...)` rzuca `NotSupportedException`.
- `public const` to stała czasu kompilacji. Kompilator wkleja wartość do assembly klienta, więc po zmianie `VipFromRow` klient bez rekompilacji widzi starą wartość. `public static readonly` nie jest wklejany - dlatego stałe widoczne poza assembly, które mogą się zmienić, lepiej robić jako `static readonly`.
- Stała dla czegoś, co jest konfiguracją (ceny zmieniane bez wdrożenia). Wtedy miejsce jest w konfiguracji (`IOptions<T>`) albo w bazie, a nie w kodzie.
- Jedna stała `Ten` dla czterech reguł.

### Pytanie do sali

Które z tych stałych w prawdziwym kinie powinny być konfiguracją, a nie kodem? Po czym to poznać?

## Scena s04. Extract Method - przepływ danych i wiele wyjść

**Temat ze slajdów:** Extract Method - cel i sygnały; Extract Method - analiza przepływu danych; Extract Method - mechanika i trudne przypadki
**Namespace:** `Training.Workshop.M4.S04ExtractMethod` · **Test:** `scripts/warsztat.sh --lang cs test m4/s04`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `TicketSummary.Describe` liczy cenę, sumę i liczbę miejsc VIP, a potem składa dokument, i to wszystko w jednej metodzie z komentarzami zamiast nazw. Wydzielamy bloki do metod, a pętlę z dwoma wyjściami najpierw rozdzielamy przez Split Loop.

**Zasada:** Extract Method przenosi spójny fragment do metody, gdy nazwa wyrazi intencję lepiej niż szczegóły, a sama długość nie jest powodem. Kluczowa jest analiza przepływu danych: co wchodzi jako parametr, co wychodzi jako wynik. Gdy fragment ma kilka wyjść, dzielimy go albo tworzymy obiekt wyniku, a nie zwracamy tablicy, parametrów `out` czy mutowalnego holdera.

**Efekt:** Metoda publiczna czyta się jak spis treści, a obliczenia są oddzielone od renderowania, bez zmiany sygnatury ani formatu. Kosztem są dwa przejścia po liście rzędów, pomijalne wobec czytelności.

**Różnica względem Javy:** `base` to w C# słowo kluczowe, więc zmienna ceny bazowej nazywa się `@base`. Suma startuje od `0.00m` (a nie od `0m`), żeby puste zamówienie drukowało `0.00` tak jak `BigDecimal.setScale(2)` w Javie - `decimal` pamięta skalę literału. Inaczej niż IntelliJ, Rider nie odmawia ekstrakcji fragmentu z dwoma wyjściami: proponuje parametr `out` (albo krotkę), co omawiamy w kroku 2.

### Co widzimy

`TicketSummary.Describe` liczy cenę bazową, sumę i liczbę miejsc VIP, a na końcu składa dokument. Komentarze `// cena bazowa formatu`, `// suma i liczba miejsc VIP`, `// dokument` to naturalne granice metod. Pętla ma jednak **dwa wyjścia**: `subtotal` i `vipSeats`.

```csharp
decimal subtotal = 0.00m;
int vipSeats = 0;
foreach (var row in order.Rows)
{
    decimal price = @base;
    if (row >= 10)
    {
        price = price + 10.00m;
        vipSeats++;
    }
    subtotal = subtotal + price;
}
subtotal = Math.Round(subtotal, 2, MidpointRounding.AwayFromZero);
```

### Krok 1: Extract Method dla bloku z jednym wyjściem

**W IDE:** zaznacz blok pod `// cena bazowa formatu` (od `decimal @base;` do końca `if` z porankiem), ⌥⌘M, nazwa `BasePrice`. Rider sam wykryje jedno wejście (`order`) i jedno wyjście (`@base`), które zostanie wartością zwracaną. Usuń zbędny komentarz.
**Po:**

```csharp
decimal @base = BasePrice(order);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s04` - 16 testów zielonych.
**Co powiedzieć:** blok z jednym wyjściem to najprostszy przypadek, IDE robi to bezpiecznie. Komentarz stał się nazwą metody.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s04 0 1`

### Krok 2: Split Loop, potem dwa razy Extract Method

**W IDE:** spróbuj ⌥⌘M na pętli (razem z `Math.Round`). Rider pokaże, że fragment ma dwie wartości wyjściowe, i zaproponuje jedną jako wynik, a drugą jako parametr `out` (albo krotkę). To jest moment dydaktyczny: zamknij dialog. Ręcznie skopiuj pętlę (Split Loop) i z pierwszej kopii usuń `vipSeats`, a z drugiej `subtotal` i `price`. Uruchom test. Następnie na każdej pętli ⌥⌘M, nazwy `Subtotal` i `VipSeats`.
**Po:**

```csharp
decimal @base = BasePrice(order);
decimal subtotal = Subtotal(order, @base);
int vipSeats = VipSeats(order);
```

**Uruchom:** test po Split Loop i po każdej ekstrakcji.
**Co powiedzieć:** zamiast zwracać parę (record z dwoma polami, krotkę albo `out`) rozdzielamy odpowiedzialności. Dwa przejścia po liście to koszt pomijalny wobec czytelności. Jeśli wydajność miałaby znaczenie, mierzymy (np. BenchmarkDotNet), a nie zgadujemy.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s04 1 2`

### Krok 3: Extract Method dla renderowania

**W IDE:** zaznacz blok `// dokument` i wykonaj ⌥⌘M, nazwa `Render`. Parametry: `order`, `subtotal`, `vipSeats`. Potem Inline Variable (⌥⌘N) dla `@base`.
**Po:**

```csharp
public string Describe(Order order)
{
    decimal subtotal = Subtotal(order, BasePrice(order));
    int vipSeats = VipSeats(order);
    return Render(order, subtotal, vipSeats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda publiczna czyta się jak spis treści. Obliczenia są oddzielone od prezentacji, więc przygotowaliśmy grunt pod Extract Class (scena s09).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s04 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketSummary`: cztery prywatne metody, każda z jednym wyjściem. Nie zmieniliśmy sygnatury publicznej ani formatu dokumentu, co potwierdza test równoważności na czterech przypadkach (w tym zamówienie puste i same miejsca VIP).

### Pułapki

- Ekstrakcja bloku, który modyfikuje zmienną lokalną używaną dalej, bez zwrócenia jej wartości. IDE tego pilnuje, ręczne kopiowanie nie.
- Zwracanie tablicy `object[]`, mutowalnego "holdera" albo dorzucanie parametrów `out`/`ref` tylko po to, żeby wyciągnąć dwa wyniki. Rider zaproponuje `out` sam z siebie - to nie znaczy, że to dobry projekt.
- Nazwa metody opisująca "jak" (`LoopRows`) zamiast "co" (`VipSeats`).

### Pytanie do sali

Kiedy zamiast Split Loop lepiej zwrócić record (albo nazwaną krotkę) z dwoma polami?

## Scena s05. Inline Variable - liczba i moment ewaluacji

**Temat ze slajdów:** Inline Variable i typ docelowy; Extract Variable i moment ewaluacji
**Namespace:** `Training.Workshop.M4.S05InlineVariable` · **Test:** `scripts/warsztat.sh --lang cs test m4/s05`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketIssuer.Issue` ma sześć zmiennych lokalnych, które wyglądają na zbędne, ale trzy z nich chronią pojedynczy efekt uboczny, pojedynczy odczyt zegara i wybór przeciążenia. Wklejamy tylko bezpieczne zmienne, a przy `price` najpierw usuwamy niejednoznaczność nazwą przeciążenia.

**Zasada:** Inline Variable usuwa zmienną, której nazwa nic nie wnosi, ale zmienia liczbę i moment ewaluacji inicjalizatora. Wyrażenie z efektem ubocznym albo odczytem czasu po wklejeniu wykona się tyle razy, ile jest użyć. Jawny typ zmiennej jest też typem docelowym, który wybiera przeciążenie - to nie jest ozdoba.

**Efekt:** Znikają `label`, `holdUntil` i `price`, a `number`, `issuedAt` i `code` zostają celowo i warto je opisać. Wynik jest identyczny, co potwierdza test z tykającym zegarem.

**Różnica względem Javy:** zegar to `TimeProvider`, odczyt to `_clock.GetUtcNow()` (zamiast `clock.instant()`), a chwila to `DateTimeOffset`. Tykający zegar w teście to ręczna podklasa `TimeProvider` z nadpisanym `GetUtcNow()` (`TickingClock`). Można by użyć `FakeTimeProvider.AutoAdvanceAmount`, ale jawny fake lepiej pokazuje, o co chodzi. Pułapka przeciążeń `Money(double)` / `Money(int)` działa w C# identycznie jak w Javie: argument `int` wybiera przeciążenie `int`.

### Co widzimy

`TicketIssuer.Issue` ma sześć zmiennych lokalnych. Wyglądają na zbędne, ale trzy z nich trzymają wynik, którego **nie wolno** obliczyć ponownie:

```csharp
int number = NextNumber();                      // efekt uboczny: każde wywołanie zużywa numer
DateTimeOffset issuedAt = _clock.GetUtcNow();   // odczyt czasu: każde wywołanie to inna chwila
string code = screeningCode + "-" + number;
double price = BasePrice(format);               // int -> double: typ zmiennej wybiera przeciążenie Money
string label = "Bilet " + code + ", cena " + Money(price)
    + ", oplata " + Money(OnlineFeeGrosze);
DateTimeOffset holdUntil = issuedAt + Hold;
return new Ticket(code, label, issuedAt, holdUntil);
```

`Money(double zloty)` i `Money(int grosze)` to dwa przeciążenia o tej samej nazwie i różnych jednostkach.

**Pokaz pułapki (przed krokiem 1):** ⌥⌘N na `number`, na `issuedAt` i na `price`. Kod się kompiluje, IDE nie protestuje, bo nie wie, że `NextNumber()` ma efekt uboczny, a `GetUtcNow()` zależy od czasu. Test równoważności jest czerwony: etykieta ma numer 2, a kod numer 1, rezerwacja trzyma o sekundę za długo (fake zegar tyka przy każdym odczycie), a cena 40 zł to "0.40". Cofnij (⌘Z). Te same trzy objawy dokumentuje stale `S05InlineTrapTest`.

### Krok 1: Inline Variable dla czystego wyrażenia użytego raz

**W IDE:** kursor na `label`, ⌥⌘N.
**Po:**

```csharp
return new Ticket(code,
    "Bilet " + code + ", cena " + Money(price)
        + ", oplata " + Money(OnlineFeeGrosze),
    issuedAt, holdUntil);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s05` - 11 testów zielonych.
**Co powiedzieć:** wyrażenie jest czyste i użyte raz, a nazwa parametru rekordu (`Label`) mówi to samo co zmienna. To podręcznikowy bezpieczny inline.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s05 0 1`

### Krok 2: Inline Variable, który czyta zmienną, a nie zegar

**W IDE:** kursor na `holdUntil`, ⌥⌘N.
**Po:**

```csharp
return new Ticket(code, ..., issuedAt, issuedAt + Hold);
```

**Uruchom:** test zielony.
**Co powiedzieć:** ten inline jest bezpieczny, bo inicjalizator czyta **zmienną** `issuedAt`, która zamroziła jeden odczyt zegara. Gdybyśmy wcześniej zrobili inline `issuedAt`, ten sam ruch dałby dwa różne odczyty czasu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s05 1 2`

### Krok 3: najpierw Rename przeciążenia, potem Inline `price`

**W IDE:** ⇧F6 na `Money(int grosze)`, nazwa `MoneyFromGrosze`. Teraz ⌥⌘N na `price`: jedyny kandydat to `Money(double)`, a `int` z `BasePrice(format)` zostanie niejawnie poszerzony do `double` tak jak wcześniej w zmiennej.
**Po:**

```csharp
"Bilet " + code + ", cena " + Money(BasePrice(format))
    + ", oplata " + MoneyFromGrosze(OnlineFeeGrosze)
```

**Uruchom:** test zielony.
**Co powiedzieć:** typ zmiennej lokalnej to typ docelowy, który wybiera przeciążenie. Najpierw usuwamy niejednoznaczność nazwą, a dopiero potem inline. `number` i `issuedAt` zostają celowo i warto dopisać im komentarz albo nic nie ruszać.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s05 2 3`

### Rozwiązanie i uzasadnienie

`Step3/TicketIssuer`: zostały trzy zmienne (`number`, `issuedAt`, `code`), bo każda chroni pojedynczą ewaluację albo nazywa pojęcie. Zniknęły te, które nic nie wnosiły. Test równoważności używa ręcznego `TickingClock` (każdy odczyt +1 s), więc wykryłby każde podwójne czytanie zegara.

### Pułapki

- Inline zmiennej z inicjalizatorem, który ma efekt uboczny (`NextSequence()`, `enumerator.MoveNext()`, `queue.Dequeue()`), zmienia **liczbę** wywołań.
- Inline odczytu czasu, losowości albo I/O zmienia **moment** i liczbę odczytów, a test z zatrzymanym `FakeTimeProvider` tego nie wykryje. Dlatego zegar w teście tyka.
- Inline zmiennej o jawnym typie (`double`, `long`, `int?`) może wybrać inne przeciążenie albo dodać/usunąć konwersję na `Nullable`. Tak samo `var` w miejsce jawnego typu: `var price = BasePrice(format)` to już `int`.
- Lambda i typ docelowy: od C# 10 `var f = () => 42;` się kompiluje (typ naturalny `Func<int>`), ale ta sama lambda przypisana do `Expression<Func<int>>` jest drzewem wyrażeń, a nie delegatem. Inline takiej zmiennej do wywołania `Where` może przełączyć zapytanie z `IQueryable` (SQL w bazie) na `IEnumerable` (filtrowanie w pamięci) albo odwrotnie.

### Pytanie do sali

Jak w teście wykryć, że kod czyta zegar dwa razy, skoro zatrzymany `FakeTimeProvider` zawsze zwraca tę samą chwilę?

## Scena s06. Inline Method i nadpisanie w podklasie

**Temat ze slajdów:** Inline Method i jego ryzyka
**Namespace:** `Training.Workshop.M4.S06InlineMethod` · **Test:** `scripts/warsztat.sh --lang cs test m4/s06`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPricing` ma trzy małe metody: dwa prywatne pośredniki i chroniony hak `BookingFee()`, nadpisany w wersji internetowej. Wklejamy pośredniki, ale hak zostaje, bo wklejenie jego ciała zabiłoby nadpisanie.

**Zasada:** Inline Method usuwa pośrednictwo, które nie dodaje znaczenia, a najbezpieczniejszy jest prywatny, niepolimorficzny delegat z jednym wywołaniem. Wklejenie ciała metody nadpisywanej usuwa dynamiczną dyspozycję, a przy `lock` albo proxy znika blokada lub transakcja. Liczba linii nie przesądza - metoda z nazwą z domeny zostaje.

**Efekt:** `Total` wprost pokazuje "cena + opłata", a kasa i internet liczą tak jak wcześniej. Hak `BookingFee()` zostaje, bo zachowanie podklasy jest częścią kontraktu klasy bazowej.

**Różnica względem Javy:** w C# metody domyślnie nie są wirtualne. Żeby podklasa mogła nadpisać hak, klasa bazowa deklaruje go jawnie jako `protected virtual decimal BookingFee()`, a `OnlineTicketPricing` jako `protected override`. Ryzyko z tej sceny dotyczy więc w C# metod `virtual`/`abstract`/`override` i implementacji interfejsów - zwykłą metodę niewirtualną można wkleić bez obaw o dyspozycję.

### Co widzimy

`TicketPricing` ma trzy małe metody, które "wyglądają na trywialne": dwa prywatne pośredniki (`Base`, `AddFee`) i chroniony hak `BookingFee()`, nadpisany w `OnlineTicketPricing` (+2.00).

```csharp
public decimal Total(Ticket ticket)
{
    return AddFee(Price(ticket));
}

/// <summary>Kasa nie pobiera opłaty rezerwacyjnej. Nadpisywane w OnlineTicketPricing.</summary>
protected virtual decimal BookingFee()      // kasa: 0.00, OnlineTicketPricing: 2.00
{
    return 0.00m;
}

private decimal Base(Ticket ticket)
{
    return BasePrice(ticket.Format);
}
```

### Krok 1: Inline Method trywialnego delegata

**W IDE:** kursor na `Base`, ⌥⌘N, wklej wszystkie wywołania i usuń metodę.
**Po:**

```csharp
decimal price = BasePrice(ticket.Format);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s06` - 19 testów zielonych (kasa i internet).
**Co powiedzieć:** prywatny, niepolimorficzny delegat z jednym wywołaniem to najbezpieczniejszy możliwy inline. Nazwa `Base` nic nie dodawała do `BasePrice`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s06 0 1`

### Krok 2: Inline Method wywołującego hak, a nie samego haka

**W IDE:** najpierw pokaż pułapkę: kursor na `BookingFee()` w klasie bazowej, ⌥⌘N. Rider ostrzeże, że metoda jest wirtualna i nadpisywana (albo nie zaproponuje inline). Ręczne wklejenie ciała dałoby `Price(ticket) + 0.00m` i internet sprzedawałby bez opłaty. Test `S06InlineOverriddenMethodTrapTest` pokazuje dokładnie ten wynik: `40.00` zamiast `42.00`. Zamknij dialog. Teraz poprawnie: kursor na `AddFee`, ⌥⌘N.
**Po:**

```csharp
public decimal Total(Ticket ticket)
{
    return Price(ticket) + BookingFee();
}
```

**Uruchom:** test zielony, `OnlineTotalsStayTheSame` nadal daje 42.00.
**Co powiedzieć:** wklejamy **wywołanie** metody polimorficznej, więc dynamiczna dyspozycja zostaje. Wklejenie **ciała** metody nadpisywanej zabija nadpisanie po cichu, a podklasa kompiluje się dalej, z poprawnym `override`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s06 1 2`

### Rozwiązanie i uzasadnienie

`Step2/TicketPricing`: bez pośredników, `Total` wprost pokazuje "cena + opłata", a `BookingFee()` zostaje hakiem dla podklasy. Test równoważności uruchamia **obie** klasy, bo zachowanie podklasy jest częścią kontraktu klasy bazowej.

### Pułapki

- Inline metody wirtualnej (albo `abstract`, `override`) lub implementującej interfejs usuwa dynamiczną dyspozycję.
- Inline metody z ciałem w `lock (...)` albo oznaczonej `[MethodImpl(MethodImplOptions.Synchronized)]` usuwa blokadę. Inline metody wywoływanej przez proxy albo dekorator (interceptory Castle/DynamicProxy, filtry ASP.NET Core, atrybuty w rodzaju `[Authorize]` czy `[Transaction]` w bibliotekach AOP) usuwa transakcję albo autoryzację.
- Parametr użyty w ciele dwa razy, a w wywołaniu argument z efektem ubocznym: po inline efekt wykona się dwa razy (Rider zwykle wprowadza wtedy zmienną, a ręczne wklejanie tego nie zrobi).
- Liczba linii nie przesądza. Metoda o nazwie z domeny (`QualifiesForGroupDiscount`) zostaje, nawet jeśli ma jedną linię.

### Pytanie do sali

Jak test klasy bazowej ma "wiedzieć" o podklasach, które jeszcze nie istnieją albo są w innym projekcie?

## Scena s07. Move Method - Feature Envy i pułapka przeciążenia

**Temat ze slajdów:** Move Method i Move Field - wybór właściciela; Move Method krok po kroku i ryzyka
**Namespace:** `Training.Workshop.M4.S07MoveMethod` · **Test:** `scripts/warsztat.sh --lang cs test m4/s07`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BookingPrinter` ma dwie metody, które używają wyłącznie danych `Screening`. Przenosimy je do seansu i zmieniamy im nazwy, ale typ parametru `int?` zostaje bez zmian.

**Zasada:** Move Method przenosi zachowanie do klasy, która ma jego dane i odpowiedzialność. Feature Envy to sygnał do analizy, a nie nakaz, bo metoda koordynująca kilka obiektów może zostać na miejscu. Przeniesienie zmienia kontekst typów, więc zmiana typu parametru przy okazji (`int?` na `int`) może po cichu wybrać inne przeciążenie, tu `Remove(int index)` zamiast `Remove(int? seat)`.

**Efekt:** `Screening` ma `Headline` i `FreeSeatsWithout`, a `BookingPrinter` tylko składa wydruk, który jest identyczny. Parametr `int?` wygląda na niekonsekwencję, więc jego powód zapisujemy w komentarzu dokumentacyjnym.

**Różnica względem Javy:** `List<int>` w C# nie ma pary przeciążeń `remove(int index)` / `remove(Object)` - jest `Remove(T item)` i osobne `RemoveAt(int index)`, więc javowa pułapka wprost nie istnieje. Scena używa stabilnego typu `SeatList` ("lista miejsc ze starego API, port biblioteki z Javy") z dwoma przeciążeniami: `Remove(int index)` i `Remove(int? seat)`. Numer miejsca przychodzi jako `int?` (odpowiednik `Integer`). "Posprzątanie" parametru na `int` po przeniesieniu sprawia, że kompilator wybiera lepiej dopasowane `Remove(int index)` - usuwa zły element albo rzuca `ArgumentOutOfRangeException` (w Javie `IndexOutOfBoundsException`). Takie API w prawdziwych bibliotekach też się zdarza, a kompilator C# nie ostrzega.

### Co widzimy

`BookingPrinter` ma dwie metody, które używają **wyłącznie** danych `Screening`. `Print` koordynuje `Booking` i `Screening`, więc zostaje.

```csharp
private string ScreeningLine(Screening s)
{
    string format = s.Format switch { ... };
    return s.Title + " (" + format + "), sala " + s.Hall + ", "
        + s.Start.ToString("yyyy-MM-dd HH:mm", CultureInfo.InvariantCulture);
}

private SeatList RemainingSeats(Screening s, int? seat)
{
    var free = new SeatList(s.FreeSeats);
    free.Remove(seat);          // int? -> Remove(int? seat): usuń MIEJSCE o tym numerze
    return free;
}
```

### Krok 1: Move Method - opis seansu

**W IDE:** kursor na `ScreeningLine`, F6 (Move Instance Method), wybierz cel: parametr `s` (typ `Screening`). Rider przeniesie metodę do rekordu i zamieni `s.Title` na `Title`. Potem ⇧F6 na przeniesionej metodzie: `Headline`, i zmień widoczność na `public`.
**Po:**

```csharp
// Screening
public string Headline()
{
    string formatName = Format switch { ... };
    return Title + " (" + formatName + "), sala " + Hall + ", " + ...;
}
// BookingPrinter
+ booking.Screening.Headline() + "\n"
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s07` - 11 testów zielonych.
**Co powiedzieć:** metoda poszła tam, gdzie są jej dane. Wybór właściciela jest decyzją, a nie automatem: `Print` też sięga do `Screening`, ale jej zadaniem jest złożenie wydruku z kilku obiektów.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s07 0 1`

### Krok 2: Move Method - wolne miejsca, bez zmiany typu parametru

**W IDE:** najpierw pokaż pułapkę: przenieś `RemainingSeats` (F6, cel `s`), a potem "posprzątaj" parametr `int? seat` na `int seat`, bo w `Screening` wszystkie numery są typu `int`. Kompiluje się (wywołanie w `BookingPrinter` dostaje `booking.Seat.Value` albo rzutowanie). Test jest czerwony: przy miejscu 1 zostało `[1, 3, 4, 5]`, a przy miejscu 8 poleciał `ArgumentOutOfRangeException`, bo teraz wołane jest `Remove(int index)`. Cofnij. Poprawnie: F6 bez zmiany typu, ⇧F6 na `FreeSeatsWithout`, a w komentarzu `<summary>` zapisz, dlaczego parametr to `int?`.
**Po:**

```csharp
/// <summary>
/// Wolne miejsca bez wskazanego. Parametr MUSI zostać <c>int?</c>: z <c>int</c>
/// wywołanie <c>Remove</c> wybrałoby <c>Remove(int index)</c> zamiast <c>Remove(int? seat)</c>.
/// </summary>
public SeatList FreeSeatsWithout(int? seat)
{
    var free = new SeatList(FreeSeats);
    free.Remove(seat);
    return free;
}
```

**Uruchom:** test zielony. `S07OverloadTrapTest` dokumentuje oba wyniki pułapki (`NullableParameterRemovesTheSeatNumber`, `IntParameterRemovesTheElementAtIndex`).
**Co powiedzieć:** przeniesienie metody zmienia kontekst typów: inne pola, inne typy, inne przeciążenia w zasięgu. Zwróć uwagę na trzeci przypadek testu: miejsce 2 na pozycji 2 **nie** odróżnia `Remove(int index)` od `Remove(int? seat)`. Jeden przypadek testowy to za mało.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s07 1 2`

### Rozwiązanie i uzasadnienie

`Step2`: `Screening` ma zachowanie o seansie (`Headline`, `FreeSeatsWithout`), a `BookingPrinter` tylko składa wydruk. Wydruk jest identyczny dla trzech rezerwacji, w tym takich, gdzie numer miejsca różni się od indeksu.

### Pułapki

- Zmiana typu parametru "przy okazji" przenosin (`int?` -> `int`, `IList<T>` -> `IEnumerable<T>`, `long` -> `int`) wybiera inne przeciążenie albo inną metodę rozszerzającą.
- Metoda przenoszona z klasy, która ma podklasy nadpisujące tę metodę (`override`). Nadpisania zostają w starym miejscu i przestają działać.
- `lock (this)` albo `[MethodImpl(MethodImplOptions.Synchronized)]` po przeniesieniu blokuje **inny obiekt**.
- Publiczna metoda przeniesiona bez delegatu w starym miejscu psuje zewnętrznych klientów. Delegat może zostać jako fasada (z `[Obsolete]`).

### Pytanie do sali

`Print` też używa głównie danych `Booking` i `Screening`. Dlaczego nie przenosimy jej do `Booking`?

## Scena s08. Move Field - próg VIP należy do sali

**Temat ze slajdów:** Move Field krok po kroku i ryzyka; Move Method i Move Field - wybór właściciela
**Namespace:** `Training.Workshop.M4.S08MoveField` · **Test:** `scripts/warsztat.sh --lang cs test m4/s08`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Próg VIP `VipFromRow` siedzi w seansie, choć jest cechą sali, więc dwa seanse w tej samej sali mogą się różnić. Przenosimy pole do `Hall` przez właściwość, a potem zamieniamy odczyty surowego progu na pytanie o regułę.

**Zasada:** Move Field przenosi stan do właściciela, a zaczyna się od Self-Encapsulate Field, żeby mieć jedno miejsce odczytu. Najpierw migrujemy odczyty, potem zapisy, bez okresu z dwiema zapisywalnymi kopiami (dual write). Find Usages nie pokaże użyć przez refleksję, ORM ani serializację.

**Efekt:** Próg VIP ma jednego właściciela i jedną regułę `Hall.IsVip`, a wycena miejsca się nie zmienia. Kosztem jest zmieniony konstruktor `Screening`, a w systemie z bazą danych także osobna migracja danych.

**Różnica względem Javy:** pole package-private to w C# `internal readonly int VipFromRow`. Self-Encapsulate Field w C# zamienia pole na właściwość o tej samej nazwie (`public int VipFromRow => _vipFromRow;`), więc w kroku 1 klient `SeatPricer` nie zmienia się w źródle (w Javie `screening.vipFromRow` -> `screening.vipFromRow()`). To zgodność źródłowa, ale nie binarna: skompilowany wcześniej klient z innego assembly szukałby pola i dostałby `MissingFieldException`. Gettery `hall()`, `format()`, `name()` to właściwości `Hall`, `Format`, `Name`.

### Co widzimy

Od którego rzędu zaczynają się miejsca VIP, to cecha **sali**, a pole `VipFromRow` siedzi w **seansie**. Każdy seans w tej samej sali niesie własną kopię i nic nie pilnuje zgodności. `SeatPricer` czyta pole bezpośrednio (`internal`).

```csharp
public sealed class Screening
{
    internal readonly int VipFromRow;
    ...
}
// SeatPricer
decimal price = row >= screening.VipFromRow ? @base + VipSurcharge : @base;
```

`S08SingleSourceOfTruthTest.StartLetsScreeningsInOneHallDisagree` pokazuje, że w `Start` ten sam fotel w tej samej sali bywa VIP albo nie, zależnie od seansu.

### Krok 1: Self-Encapsulate Field

**W IDE:** kursor na `VipFromRow`, ⇧F6 na `_vipFromRow`, a potem Refactor This (⌃T) > Encapsulate Field, właściwość tylko do odczytu o nazwie `VipFromRow`, z zamianą wszystkich użyć (także w `IsVip` i w `SeatPricer`) na właściwość. Zmień widoczność pola na `private`.
**Po:**

```csharp
private readonly int _vipFromRow;

public int VipFromRow => _vipFromRow;

public bool IsVip(int row)
{
    return row >= VipFromRow;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s08` - 14 testów zielonych.
**Co powiedzieć:** teraz jest dokładnie jedno miejsce, w którym zmienimy źródło wartości. Bez tego kroku przeniesienie oznaczałoby poprawianie każdego odczytu ręcznie. W C# klienci nawet nie zauważyli zmiany w źródle, bo składnia odczytu pola i właściwości jest ta sama.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s08 0 1`

### Krok 2: Move Field do Hall

**W IDE:** dodaj parametr konstruktora i właściwość w `Hall` (Change Signature ⌘F6 na konstruktorze `Hall`, nowy parametr `int vipFromRow`, potem właściwość `public int VipFromRow { get; }` przypisana w konstruktorze). W `Screening` ciało właściwości zamień na `Hall.VipFromRow`, usuń pole i parametr konstruktora (⌘F6 na konstruktorze `Screening`, usuń `vipFromRow`). Miejsca tworzące seanse (tu: adaptery w teście) przekazują próg do sali.
**Po:**

```csharp
// Hall
public Hall(string name, int vipFromRow) { ... }
public int VipFromRow { get; }
// Screening
public int VipFromRow => Hall.VipFromRow;
```

**Uruchom:** test zielony.
**Co powiedzieć:** przenosimy w jednym ruchu, bez okresu, w którym obie klasy mają zapisywalną kopię (dual write). Dwie kopie rozjadą się przy pierwszym wyjątku albo przy pierwszym zapomnianym zapisie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s08 1 2`

### Krok 3: aktualizacja odczytów i usunięcie akcesora przejściowego

**W IDE:** w `Hall` dodaj `IsVip(int row)`. W `Screening.IsVip` zamień ciało na `Hall.IsVip(row)`. W `SeatPricer` zamień `row >= screening.VipFromRow` na `screening.IsVip(row)` i wydziel zmienną `vip` (⌥⌘V, Rider zastąpi też drugie wywołanie w etykiecie). Na końcu Safe Delete (⌘⌦) na `Screening.VipFromRow`, a w `Hall` zamień właściwość z powrotem na prywatne pole `_vipFromRow`.
**Po:**

```csharp
// Hall
public bool IsVip(int row)
{
    return row >= _vipFromRow;
}
// SeatPricer
bool vip = screening.IsVip(row);
decimal price = vip ? @base + VipSurcharge : @base;
```

**Uruchom:** test zielony, także `AfterMoveFieldTheHallDecidesForEveryScreening`.
**Co powiedzieć:** najpierw migrujemy odczyty, a stary akcesor usuwamy dopiero, gdy nikt go nie używa. Przy okazji klienci przestali porównywać surowy próg i pytają o regułę.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s08 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: próg VIP ma jednego właściciela (`Hall`) i jedną regułę (`Hall.IsVip`). Wycena miejsca jest identyczna na każdym etapie. Zmienił się sposób tworzenia seansu, bo konstruktor stracił parametr. W systemie z bazą danych to osobna migracja danych.

### Pułapki

- Pole używane przez refleksję, ORM (kolumna w tabeli seansów w mapowaniu EF Core), serializację albo `Equals`/`GetHashCode` (w rekordzie generowane automatycznie ze wszystkich pól). Find Usages tego nie pokaże.
- Utrata modyfikatorów przy przenoszeniu: `readonly`, `volatile`, `[NonSerialized]`, `[JsonIgnore]`.
- Migracja zapisów przed odczytami albo dwie zapisywalne kopie "na chwilę".
- Seanse, które naprawdę miały różny próg w tej samej sali (np. inna konfiguracja foteli). Wtedy Move Field jest zmianą zachowania i trzeba najpierw sprawdzić dane.

### Pytanie do sali

Jak przeprowadzić ten Move Field, gdy `VipFromRow` jest kolumną w tabeli `screening` z milionem wierszy?

## Scena s09. Extract Class - klient, płatność i klasa-worek

**Temat ze slajdów:** Extract Class - cel, mechanika i zły wynik
**Namespace:** `Training.Workshop.M4.S09ExtractClass` · **Test:** `scripts/warsztat.sh --lang cs test m4/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Booking` ma siedem pól i trzy powody zmiany: dane klienta, płatność i samą rezerwację. Wydzielamy `Customer` i `Payment`, przy czym pierwszy krok celowo daje zły wynik, który potem naprawiamy przeniesieniem zachowania.

**Zasada:** Extract Class wydziela spójne pola i operacje z odrębnym powodem zmiany, a Move Field i Move Method to tylko mechanika tej decyzji. Zły wynik to klasa-worek: dane przeniesione, a logika dalej w źródle przez właściwości. Nowa klasa nie powinna trzymać referencji zwrotnej do źródła, bo powstaje cykl.

**Efekt:** `Booking` ma cztery pola i składa rezerwację z dwóch klas z własnym zachowaniem, a jego publiczne API działa jak wcześniej jako fasada. Delegaty `Pay`, `IsPaid` i `Contact` zostają w `Booking`, dopóki klienci nie zechcą wołać nowych klas wprost.

**Różnica względem Javy:** test struktury (`S09StructureTest`) porównuje nazwy pól bez prefiksu `_` (więc oczekiwane listy są jak w Javie: `amount`, `cardNumber`, ...) i pomija pola generowane przez kompilator. "Zachowanie" rekordu to publiczne metody poza akcesorami właściwości i metodami, które kompilator generuje dla rekordu (`Equals`, `GetHashCode`, `ToString`, `Deconstruct`, `<Clone>$`, operatory). Oczekiwana nazwa metody to `ContactLine`.

### Co widzimy

`Booking` ma siedem pól i trzy powody zmiany: dane klienta i ich formatowanie (e-mail, telefon), płatność (karta, status, maskowanie) oraz samą rezerwację (id, kwota).

```csharp
private readonly string _customerName;
private readonly string _customerEmail;
private readonly string _customerPhone;
private readonly decimal _amount;
private string? _cardNumber;
private string _paymentStatus = "NEW";

public string Contact()
{
    return _customerName + " <" + _customerEmail.Trim().ToLowerInvariant() + ">, tel. "
        + FormattedPhone();
}
```

### Krok 1: Extract Class - tylko dane (zły wynik pośredni)

**W IDE:** Refactor This (⌃T) > Extract Class na `Booking`, zaznacz trzy pola `_customer*`, nazwa klasy `Customer`. Zamień wygenerowaną klasę na `public sealed record Customer(string Name, string Email, string Phone);` (ręcznie albo akcją kontekstową ⌥⏎ na klasie, jeśli Rider zaproponuje konwersję do rekordu). Konstruktor `Booking` tworzy `new Customer(...)`.
**Po:**

```csharp
public sealed record Customer(string Name, string Email, string Phone);

public string Contact()
{
    return _customer.Name + " <" + _customer.Email.Trim().ToLowerInvariant() + ">, tel. "
        + FormattedPhone();
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s09` - 15 testów zielonych, także `Step1CustomerIsADataBagWithoutBehaviour`.
**Co powiedzieć:** to jest **zły wynik**, jeśli się tu zatrzymamy. Mamy klasę-worek z właściwościami, a cała wiedza o kliencie nadal siedzi w `Booking`. Dane się przeniosły, odpowiedzialność nie. Test struktury to pokazuje: `Customer` nie ma ani jednej metody poza akcesorami i tym, co kompilator generuje dla rekordu.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s09 0 1`

### Krok 2: Move Method - zachowanie idzie za danymi

**W IDE:** w `Booking.Contact()` zaznacz treść `return`, Extract Method (⌥⌘M) z parametrem `customer`, nazwa `ContactLine`. Potem F6 (Move Instance Method) na `ContactLine` i `FormattedPhone`, cel `Customer`. `Booking.Contact()` zostaje jako delegat.
**Po:**

```csharp
public sealed record Customer(string Name, string Email, string Phone)
{
    public string ContactLine()
    {
        return Name + " <" + Email.Trim().ToLowerInvariant() + ">, tel. " + FormattedPhone();
    }
}
// Booking
public string Contact()
{
    return _customer.ContactLine();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** teraz `Customer` ma własną odpowiedzialność. Extract Class to decyzja projektowa, a Move Field i Move Method to mechanika jej realizacji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s09 1 2`

### Krok 3: Extract Class - płatność od razu z zachowaniem

**W IDE:** utwórz klasę `Payment`, przenieś do niej pola `_cardNumber` i `_paymentStatus` (jako `_status`) oraz metody `Pay`, `IsPaid`, `MaskedCard` - Extract Class z zaznaczeniem pól **i** metod albo ręcznie. Metoda `Payment.PayWith(card, bookingId)` dostaje id jako **wartość**, bez referencji do `Booking`. W `Booking` zostają delegaty `Pay` i `IsPaid`, a `Summary` woła `_payment.Description()`.
**Po:**

```csharp
public sealed class Payment
{
    private string? _cardNumber;
    private string _status = "NEW";

    public void PayWith(string card, string bookingId) { ... }
    public bool IsPaid() { ... }
    public string Description()
    {
        return IsPaid() ? "oplacona karta " + MaskedCard() : "oczekuje";
    }
}
```

**Uruchom:** test zielony, także druga płatność odrzucona z tym samym komunikatem i `Step3BookingComposesCustomerAndPayment`.
**Co powiedzieć:** tym razem od razu przenosimy dane **i** zachowanie. `Payment` nie zna `Booking`, więc nie powstał cykl zależności. Publiczne API `Booking` się nie zmieniło, bo działa jako fasada.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s09 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `Booking` ma cztery pola (`_id`, `_customer`, `_amount`, `_payment`) i składa rezerwację z dwóch spójnych części. Każda z nich ma jeden powód zmiany: formaty kontaktu, płatność i PCI. Podsumowanie i komunikaty błędów są identyczne na każdym etapie.

### Pułapki

- **Klasa-worek:** przeniesione dane, logika w źródle przez właściwości (krok 1). Drugi rodzaj worka to jedna klasa `BookingDetails` na "wszystko, co nie pasuje", bez spójności.
- Referencja zwrotna (`new Payment(this)`) tworzy cykl. Lepiej przekazać wartość albo wąski interfejs.
- Dwa źródła prawdy: pole zostawione w `Booking` "na wszelki wypadek" obok kopii w nowej klasie.
- Zmiana publicznego API przy okazji ekstrakcji (np. `booking.Payment.PayWith(...)` u klientów). To osobny krok.

### Pytanie do sali

Po czym na przeglądzie kodu rozpoznać, że wydzielona klasa jest workiem, a nie pojęciem?

## Scena s10. Encapsulate Field - status, który każdy może nadpisać

**Temat ze slajdów:** Encapsulate Field
**Namespace:** `Training.Workshop.M4.S10EncapsulateField` · **Test:** `scripts/warsztat.sh --lang cs test m4/s10`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Publiczne pole `Reservation.Status` pozwala każdemu wpisać dowolny status, także gościa na anulowaną rezerwację. Najpierw ukrywamy pole i zastępujemy setter operacjami domenowymi, a na końcu osobnym krokiem dodajemy pilnowanie przejść.

**Zasada:** Encapsulate Field daje dostęp do stanu tylko przez operacje właściciela, więc można kontrolować zmiany i pilnować niezmiennika. Migracja idzie przez trywialne akcesory, migrację klientów i zwężenie widoczności. Walidacja to już zmiana zachowania, więc dodajemy ją osobno, nie razem z getterem i setterem.

**Efekt:** Po kroku 2 pole jest prywatne, nie ma settera, a zachowanie jest identyczne. Krok 3 świadomie zmienia zachowanie: niedozwolone przejścia rzucają `InvalidOperationException` zamiast być cicho ignorowane, więc to osobny commit z decyzją biznesu.

**Różnica względem Javy:** Encapsulate Field w C# daje właściwość `Status { get; set; }` na prywatnym polu `_status`, więc w kroku 1 `BoxOffice` nie zmienia się w źródle (w Javie przepisanie na `getStatus()`/`setStatus(...)`). Krok 2 to właściwość tylko do odczytu `public string Status => _status;` (odpowiednik `status()`) i operacje domenowe jak w Javie. Test `StartFieldIsPublicFromStep1ItIsNot` sprawdza refleksją `GetField("Status")`: w `Start` to pole, od `Step1` właściwość.

### Co widzimy

`Reservation.Status` to publiczne pole. Reguły przejść (NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED) pilnuje kasa, a `GuestEntry` po prostu przypisuje `"USED"`, także na rezerwację anulowaną.

```csharp
public sealed class Reservation
{
    public string Status = "NEW";
}
// BoxOffice
public void GuestEntry(Reservation r)
{
    r.Status = "USED";
}
```

Test równoważności zapisuje dziwne zachowanie jako `ZASTANE: ...`: płatność po wygaśnięciu, druga płatność i wejście bez płatności są cicho ignorowane, a gość wchodzi na anulowaną rezerwację.

### Krok 1: Encapsulate Field

**W IDE:** kursor na `Status`, ⇧F6 na `_status`, potem Refactor This (⌃T) > Encapsulate Field: właściwość `Status` z getterem i setterem, zamiana wszystkich użyć na właściwość, pole `private`.
**Po:**

```csharp
private string _status = "NEW";

public string Status
{
    get => _status;
    set => _status = value;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s10` - 29 testów zielonych, także `StartFieldIsPublicFromStep1ItIsNot`.
**Co powiedzieć:** trywialne akcesory na tym samym polu to czysta refaktoryzacja, bo setter przyjmuje wszystko jak wcześniej. Zysk jest taki, że każdy zapis przechodzi teraz przez jedno miejsce. W C# `BoxOffice` nawet się nie zmienił, bo `r.Status = "PAID"` wygląda tak samo dla pola i dla właściwości - dlatego od początku warto wystawiać właściwości, a nie pola.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s10 0 1`

### Krok 2: operacje domenowe zamiast settera

**W IDE:** w `BoxOffice.Pay` zaznacz ciało, Extract Method (⌥⌘M), potem F6 (Move Instance Method) do `Reservation` jako `Pay()`. Powtórz dla `CheckIn`, `Cancel`, `Expire`, a `GuestEntry` przenieś jako `AdmitGuestWithoutPayment()`. Na końcu ręcznie usuń setter (Remove Setting Method) - właściwość zostaje tylko do odczytu: `public string Status => _status;`.
**Po:**

```csharp
public string Status => _status;

public void Pay()
{
    if (_status == "NEW")
    {
        _status = "PAID";
    }
}

public void AdmitGuestWithoutPayment()
{
    _status = "USED";
}
```

**Uruchom:** test zielony, bo warunki przepisaliśmy 1:1.
**Co powiedzieć:** `Pay()` komunikuje dozwoloną zmianę lepiej niż `Status = "PAID"`. Obejście z `GuestEntry` ma teraz uczciwą nazwę i jest widoczne w API, a nie ukryte w przypisaniu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s10 1 2`

### Krok 3: niezmiennik w operacjach (świadoma zmiana zachowania)

**W IDE:** ręcznie: prywatna `MoveTo(string target, params string[] allowedFrom)` rzuca `InvalidOperationException`, gdy przejście jest niedozwolone. Każda operacja deleguje do `MoveTo`.
**Po:**

```csharp
public void Pay()
{
    MoveTo("PAID", "NEW");
}

public void AdmitGuestWithoutPayment()
{
    MoveTo("USED", "NEW", "PAID");
}
```

**Uruchom:** test zielony, ale dla `Step3` z **innymi oczekiwaniami** (`Step3RejectsIllegalTransitions`): dozwolone ścieżki bez zmian, a przypadki `ZASTANE` kończą się `BLAD`.
**Co powiedzieć:** to już nie jest refaktoryzacja. Zmieniają się akceptowane operacje i wyjątki, więc to osobny commit z wymaganiem od biznesu. Hermetyzacja z kroków 1-2 sprawiła, że ta zmiana dotyczy jednej klasy.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s10 2 3`

### Rozwiązanie i uzasadnienie

`Step2` to koniec refaktoryzacji: pole prywatne, operacje domenowe i identyczne zachowanie. `Step3` to zatwierdzona zmiana kontraktu, którą test opisuje osobno. Kolejność ma znaczenie: najpierw hermetyzacja bez zmiany zachowania, potem reguła.

### Pułapki

- Walidacja dodana razem z getterem i setterem w jednym commicie. Nie da się wtedy odróżnić refaktoryzacji od zmiany zachowania.
- Setter, który zostaje "na wszelki wypadek" (także `private set` używany z wielu metod albo `init`, który pozwala ustawić dowolny status przy tworzeniu). Wtedy niezmiennik da się obejść tak samo jak przez publiczne pole.
- Pole `volatile` albo chronione blokadą. Semantyka pamięci nie przechodzi sama na właściwość.
- Zamiana pola na właściwość zmienia kontrakt binarny i refleksyjny: klient skompilowany z innym assembly, `GetField(...)`, a także serializatory, które domyślnie widzą tylko właściwości albo tylko pola.
- Frameworki, które zapisują pole refleksją (EF Core z mapowaniem na pole, deserializacja) i omijają operacje domenowe.

### Pytanie do sali

Kto w waszej organizacji decyduje, że "gość na anulowaną rezerwację" to błąd, a nie funkcja? Jak test ma to zapisać, zanim decyzja zapadnie?

## Scena s11. Encapsulate Collection - trzy kontrakty listy miejsc

**Temat ze slajdów:** Encapsulate Collection; Trzy różne kontrakty kolekcji
**Namespace:** `Training.Workshop.M4.S11EncapsulateCollection` · **Test:** `scripts/warsztat.sh --lang cs test m4/s11`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Booking.Seats` to publiczna, mutowalna lista i każdy może zmienić miejsca z pominięciem właściciela. Przenosimy dodawanie i usuwanie do `Booking`, a potem zmieniamy właściwość na niemodyfikowalny widok i wreszcie na migawkę.

**Zasada:** Encapsulate Collection oddaje właścicielowi kontrolę nad członkostwem: odczyt przez kontrakt, zmiana przez `Add`/`Remove` lub polecenia. Widok (`AsReadOnly()`) i migawka (`ToImmutableList()`) to dwa różne kontrakty, bo tylko widok pokazuje późniejsze zmiany. Niemodyfikowalność nie chroni wnętrza elementów i nie daje bezpieczeństwa wątkowego.

**Efekt:** Kasa działa tak samo na każdym etapie, ale kroki 2 i 3 świadomie zmieniają kontrakt właściwości: klient nie może już modyfikować listy, a po kroku 3 nie widzi późniejszych zmian. Wybór między widokiem a migawką zależy od tego, czego potrzebują klienci.

**Różnica względem Javy:** `Collections.unmodifiableList` to w C# `_seats.AsReadOnly()` (żywy widok `ReadOnlyCollection<T>`), a `List.copyOf` to `_seats.ToImmutableList()` (migawka z `System.Collections.Immutable`). Od kroku 2 właściwość ma typ `IReadOnlyList<Seat>`, więc kompilator już blokuje `Add`. Test kontraktu sprawdza modyfikację przez rzutowanie na `ICollection<Seat>` (`NotSupportedException`), co pokazuje, że sam typ `IReadOnlyList` nad `List<T>` nie wystarczy: rzutowanie by przeszło i klient zmieniłby listę właściciela. Dlatego widok i migawka to opakowania. Świadomie nie użyto `.ToList()` jako migawki - to też kopia, ale po rzutowaniu klient mógłby ją zmieniać.

### Co widzimy

`Booking.Seats` to publiczna, mutowalna lista. `readonly` blokuje tylko przypisanie, więc każdy może dodać, usunąć albo wyczyścić miejsca z pominięciem właściciela.

```csharp
public readonly List<Seat> Seats = [];
// SeatDesk
booking.Seats.Add(seat);
booking.Seats.Remove(seat);
```

### Krok 1: operacje w właścicielu, właściwość bez zmiany kontraktu

**W IDE:** ⇧F6 na `Seats` -> `_seats`, potem Encapsulate Field (⌃T) na `_seats`, właściwość `Seats` tylko do odczytu. Dodaj `AddSeat` i `RemoveSeat` w `Booking` i przepisz `SeatDesk` na te operacje (w `SeatDesk` zaznacz `booking.Seats.Add(seat)`, ⌥⌘M, F6 do `Booking`). Właściwość zwraca **tę samą** listę.
**Po:**

```csharp
private readonly List<Seat> _seats = [];

/// <summary>Przejściowo: żywa, MODYFIKOWALNA lista - dokładnie to, co dawało publiczne pole.</summary>
public List<Seat> Seats => _seats;

public void AddSeat(Seat seat)
{
    _seats.Add(seat);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s11` - 16 testów zielonych, `Step1GetterStillReturnsTheSameAlias`: klient zmienia tak, widzi zmiany tak.
**Co powiedzieć:** to faza przejściowa, która nadal jest czystą refaktoryzacją. Klient, który jeszcze modyfikuje listę przez właściwość, działa jak wcześniej, a my migrujemy klientów po kolei.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s11 0 1`

### Krok 2: niemodyfikowalny widok

**W IDE:** zmień typ właściwości na `IReadOnlyList<Seat>` i zwracaj `_seats.AsReadOnly()`. Kompilator od razu pokaże każdego klienta, który jeszcze woła `Seats.Add(...)`.
**Po:**

```csharp
/// <summary>Żywy widok tylko do odczytu.</summary>
public IReadOnlyList<Seat> Seats => _seats.AsReadOnly();
```

**Uruchom:** test równoważności zielony (kasa używa już `AddSeat`/`RemoveSeat`). `S11CollectionContractTest` (`Step2AsReadOnlyIsAReadOnlyLiveView`): klient zmienia nie, widzi zmiany tak.
**Co powiedzieć:** to pierwsza **zmiana kontraktu** właściwości. Jest bezpieczna dopiero wtedy, gdy żaden klient nie modyfikuje listy przez właściwość. Widok jest żywy, więc wcześniej pobrana lista pokaże miejsca dodane później.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s11 1 2`

### Krok 3: migawka

**W IDE:** we właściwości `return _seats.ToImmutableList();` (plus `using System.Collections.Immutable;`).
**Po:**

```csharp
/// <summary>Niemodyfikowalna kopia z chwili wywołania.</summary>
public IReadOnlyList<Seat> Seats => _seats.ToImmutableList();
```

**Uruchom:** test równoważności zielony. `S11CollectionContractTest` (`Step3ToImmutableListIsASnapshot`): klient zmienia nie, widzi zmiany nie.
**Co powiedzieć:** widok czy migawka to decyzja o kontrakcie, a nie szczegół implementacji. Klient, który trzymał listę i liczył, że "sama się odświeży", po tym kroku widzi stare dane.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s11 2 3`

### Rozwiązanie i uzasadnienie

Tabela ze slajdu jako test:

| Wariant | Klient zmienia | Widzi późniejsze zmiany |
|---|:---:|:---:|
| `Start` (publiczne pole) | tak | tak |
| `Step1` (właściwość na tym samym polu) | tak | tak |
| `Step2` (`AsReadOnly()`) | nie | tak |
| `Step3` (`ToImmutableList()`) | nie | nie |

Wszystkie warianty dają ten sam wynik operacji kasy (`S11EquivalenceTest`), a różnią się kontraktem właściwości (`S11CollectionContractTest`). Który wariant jest rozwiązaniem, zależy od klientów: widok, gdy ktoś potrzebuje bieżącego stanu, a migawka, gdy wynik idzie dalej (raport, inny wątek).

### Pułapki

- Niemodyfikowalność blokuje członkostwo, a nie wnętrze elementów. Tu `Seat` jest niezmiennym rekordem, więc to wystarcza. Przy mutowalnych elementach nie wystarczy.
- Sam typ `IReadOnlyList<T>` to obietnica, a nie ochrona: zwrócenie `_seats` jako `IReadOnlyList<Seat>` pozwala klientowi zrzutować listę z powrotem na `List<Seat>` albo `ICollection<Seat>` i ją zmienić.
- `ToImmutableList()` na liście, która już jest `ImmutableList<T>`, zwraca ten sam obiekt, a `AsReadOnly()` przy każdym wywołaniu tworzy nowe opakowanie - nie opieraj kontraktu na tożsamości. W odróżnieniu od javowego `List.copyOf` migawka w C# przyjmuje elementy `null`.
- Konstruktor przyjmujący listę z zewnątrz bez kopii zachowuje alias do listy klienta.
- Migawka nie daje bezpieczeństwa wątkowego samego właściciela: `ToImmutableList()` w trakcie `Add` z innego wątku może rzucić albo dać niespójny stan.
- Zmiana na zbiór (`HashSet<T>`) "przy okazji" zmienia zachowanie duplikatów. Test ma przypadek z tym samym miejscem dwa razy.

### Pytanie do sali

Który kontrakt wybierzecie dla listy miejsc, którą czyta ekran sali w kasie, a który dla listy wysyłanej w e-mailu z potwierdzeniem?

## Scena s12. Encapsulate Conditional - czy przysługuje zwrot

**Temat ze slajdów:** Encapsulate Conditional
**Namespace:** `Training.Workshop.M4.S12EncapsulateConditional` · **Test:** `scripts/warsztat.sh --lang cs test m4/s12`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Warunek zwrotu w `RefundCalculator.Refund` pyta o status, czas i prefiks promocji zamiast o regułę z regulaminu. Zamykamy kolejne fragmenty w predykaty z nazwami domenowymi, od najmniejszego do warunku gałęzi.

**Zasada:** Encapsulate Conditional zamienia pytanie o implementację na pytanie o znaczenie, czyli wydziela predykat z nazwą z domeny. Krótkie spięcie jest zachowaniem, więc osłona `null` idzie razem z testem, a kolejność warunków się nie zmienia. Nazwa `Is`/`Has` nie gwarantuje, że predykat jest czysty.

**Efekt:** `Refund` czyta się jak regulamin (`IsRefundable`, `CancelledAtLeast24hBefore`), a granica "dokładnie 24 h" jest opisana testem. Wyrażeń celowo nie upraszczamy, bo zamiana `!(a > b)` na `a < b` zgubiłaby ten przypadek.

**Różnica względem Javy:** `!now.plusHours(24).isAfter(start)` to w C# `!(now.AddHours(24) > b.ScreeningStart)` - podwójne zaprzeczenie zostaje celowo, żeby scena miała co zamknąć w nazwie. `startsWith("FREE")` to `StartsWith("FREE", StringComparison.Ordinal)` (bez `Ordinal` porównanie zależałoby od kultury). Kwoty liczy wspólny typ `Money` (`Percent`, `Minus`, `Max`).

### Co widzimy

`RefundCalculator.Refund` liczy zwrot: 100% przy anulowaniu co najmniej 24 h przed seansem, 50% później, 0 po starcie, potrącenie 3.00 (nie poniżej zera). Warunek pyta o implementację, a nie o regułę:

```csharp
if (b.Status == "PAID" && now < b.ScreeningStart
    && !(b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal)))
{
    if (!(now.AddHours(24) > b.ScreeningStart))
    {
        amount = b.Tickets;
    }
    else
    {
        amount = b.Tickets.Percent(50);
    }
}
```

### Krok 1: Extract Method dla najmniejszego fragmentu

**W IDE:** zaznacz `b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal)` (bez negacji i bez nawiasów), ⌥⌘M, nazwa `HasFreeTicketPromo`.
**Po:**

```csharp
if (b.Status == "PAID" && now < b.ScreeningStart && !HasFreeTicketPromo(b))

private static bool HasFreeTicketPromo(Booking b)
{
    return b.Promo != null && b.Promo.StartsWith("FREE", StringComparison.Ordinal);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m4/s12` - 28 testów zielonych.
**Co powiedzieć:** osłona `!= null` idzie razem z testem prefiksu. Krótkie spięcie jest zachowaniem, co sprawdza przypadek z `Promo = null`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s12 0 1`

### Krok 2: Encapsulate Conditional - cały warunek

**W IDE:** zaznacz cały warunek pierwszego `if`, ⌥⌘M, nazwa `IsRefundable`.
**Po:**

```csharp
if (IsRefundable(b, now))

private static bool IsRefundable(Booking b, DateTime now)
{
    return b.Status == "PAID" && now < b.ScreeningStart && !HasFreeTicketPromo(b);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** `if` pyta teraz o regułę z regulaminu, a szczegóły (status, czas, promocja) mieszkają w jednym miejscu. Kolejność warunków w `&&` jest taka sama, więc wyjątki i krótkie spięcie też są te same.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s12 1 2`

### Krok 3: Encapsulate Conditional dla gałęzi

**W IDE:** zaznacz `!(now.AddHours(24) > b.ScreeningStart)`, ⌥⌘M, nazwa `CancelledAtLeast24hBefore`.
**Po:**

```csharp
if (IsRefundable(b, now))
{
    if (CancelledAtLeast24hBefore(b, now))
    {
        amount = b.Tickets;
    }
    else
    {
        amount = b.Tickets.Percent(50);
    }
}
```

**Uruchom:** test zielony, także przypadek "dokładnie 24 h przed" (100%) i "24 h minus minuta" (50%).
**Co powiedzieć:** podwójne zaprzeczenie zamknęliśmy w nazwie. Granicę (`>=` 24 h) opisuje test, więc nikt jej nie "poprawi" przy okazji uproszczenia wyrażenia.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m4/s12 2 3`

### Rozwiązanie i uzasadnienie

`Step3/RefundCalculator`: metoda publiczna czyta się jak regulamin (czy przysługuje zwrot, czy anulowano co najmniej 24 h przed), a predykaty mają nazwy z domeny. Test obejmuje granice czasu, statusy, promocje, `null` i dolną granicę kwoty.

### Pułapki

- Uproszczenie `!(now.AddHours(24) > start)` do `now.AddHours(24) < start` gubi przypadek "dokładnie 24 h". Poprawna negacja to `<=` - Rider ją zaproponuje (akcja kontekstowa ⌥⏎ na `!`), ale ręczne "uproszczenie" łatwo zrobić źle.
- Obliczenie prawej strony osłony wcześniej (np. `bool free = b.Promo.StartsWith("FREE", ...)` przed sprawdzeniem `null`). W C# z włączonymi nullable reference types kompilator ostrzeże (`CS8602`), ale ostrzeżenie to nie błąd, a `!` je ucisza.
- Nazwa `Is`/`Has` nie gwarantuje czystości. Predykat z efektem ubocznym (log, licznik, zapytanie do bazy) po ekstrakcji może zostać wywołany w innym miejscu albo inną liczbę razy.
- Przeniesienie predykatu do `Booking` jako następny krok ma sens, ale wymaga `TimeProvider` albo `now` jako parametru, bo sam rekord nie zna bieżącej chwili.

### Pytanie do sali

Czy `IsRefundable` powinno trafić do `Booking`? Co wtedy z parametrem `now`?

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
