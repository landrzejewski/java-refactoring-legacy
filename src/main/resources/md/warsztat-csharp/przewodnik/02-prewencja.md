# Moduł 2. Prewencja - warsztat CineLegacy (C#): przewodnik prowadzącego

Dziesięć małych scen w domenie kina CineLegacy pokazuje tematy z części II prezentacji („Zanim wprowadzisz wzorzec”) jako serie bezpiecznych zmian: bramki kompilatora i jakości jako najszybsze pętle informacji zwrotnej, odraczanie decyzji (YAGNI) i ich zapis (ADR z regułą sprawdzaną przez test), projekt, który hamuje degradację kodu (zasada pojedynczej odpowiedzialności, zasada odwrócenia zależności, granice czystej architektury, automatyczna kontrola granicy) oraz codzienną dbałość o kod (zasada harcerza, seria zmian gotowych do przeglądu). Każda scena ma kod wyjściowy (`Start`) z zapachem lub pułapką i kompletne snapshoty po każdym kroku (`Step1`, `Step2`, ...). Prowadzący pracuje na żywo w `Start`, po każdym kroku uruchamia test sceny i porównuje wynik ze snapshotem.

Sceny ilustrują sekcję 2 slajdów ("Zanim wprowadzisz wzorzec"): każdy temat ma własną, kilkuminutową scenę. Sceny są pokazami prowadzącego do wyboru - nie trzeba pokazywać wszystkich. Numery scen odpowiadają przestrzeniom nazw w kodzie i w tej kolejności sceny są opisane poniżej. Kolejność pokazu, zgodną z kolejnością slajdów, podaje sekcja „Proponowana kolejność pokazu” na końcu.

Kod portu C# leży w `csharp/src/Training.Workshop/M2/SNN.../{Start,Step1,...}`, testy (xUnit) w `csharp/tests/Training.Workshop.Tests/M2/SNN.../`. Kwoty to `decimal` (odpowiednik `BigDecimal`) albo wspólny typ `Money` (`decimal`, skala 2, zaokrąglenie `AwayFromZero`), a wydruki kwot są formatowane jawnie z `CultureInfo.InvariantCulture`, więc oczekiwane teksty są takie same jak w wersji Java. Wyjątki: `IllegalArgumentException` -> `ArgumentException`, `IllegalStateException` -> `InvalidOperationException`. Interfejsy mają prefiks `I` (`IPricingRule`, `ICustomerNotifier`, `IReservationStore`, `IBookingNotifier`), a podpakiety `app`/`infra`/`adapter`/`domain`/`pricing`/`notification` to przestrzenie nazw (i katalogi) `App`/`Infra`/`Adapter`/`Domain`/`Pricing`/`Notification`. Największe adaptacje dotyczą scen narzędziowych: bramka kompilatora (s03) kompiluje kod sceny w pamięci przez Roslyn, a bramka jakości (s04) kompiluje próbki w pamięci i sama uruchamia ich testy. Tam, gdzie scena odbiega od Javy, akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m2              # sceny i kroki modułu 2
scripts/warsztat.sh --lang cs test m2/s01          # testy jednej sceny (krótka nazwa wystarczy)
scripts/warsztat.sh --lang cs test m2              # wszystkie sceny modułu (150 testów)
scripts/warsztat.sh --lang cs diff m2/s01 0 1      # co zmienia krok 1 względem Start (0 = Start)
scripts/warsztat.sh --lang cs diff m2/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m2/s09 3        # przeskok: Start = snapshot kroku 3
scripts/warsztat.sh --lang cs next m2/s09          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m2/s09         # przywrócenie Start z repozytorium
```

- Zasada pokazu: **jeden krok - jeden test - jedno zdanie komentarza**. Nie łącz kroków, nawet jeśli IDE pozwala. Zanim ruszysz kod, powiedz, jaki dowód da test po kroku - to w praktyce pętla informacji zwrotnej ze slajdu 2.1.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh next` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `--lang cs next m2/sNN` wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Jeśli krok na żywo się nie uda albo brakuje czasu: `next` (albo `jump` do konkretnego snapshotu) i kontynuuj od następnego kroku. Po pokazie zawsze `reset`.
- Testy dotykają `Start` wyłącznie przez API wspólne dla wszystkich kroków, więc kompilują się w każdym stanie pośrednim. Tam, gdzie refaktoryzacja zmienia konstruktory, scena ma punkt wejścia pełniący rolę composition root: statyczna klasa `Main` (s08), `CinemaApplication` (s09), `CinemaApp` (s10). Refaktoryzacje Rider (Introduce Parameter, Move to Folder/Namespace) same aktualizują te miejsca.
- Część testów to nie tylko równoważność (`SNNEquivalenceTest`), ale też dowody strategii (`SNNSolutionTest`, `S08DependencyDirectionTest`, `S09UseCaseTest`, `S10ArchitectureTest`): wynik bramki, lista naruszeń ADR, kierunek zależności. Testy, które **dokumentują stan startowy** (`StartViolatesBothRules` w s02, `StartFailsTheGateWithFourKindsOfWarnings` w s03, `StartPassesEverythingBecauseItChecksNothing` w s04, `StartDomainHasNoViolationsBeyondTheFrozenOnes` w s10), zmienią kolor na czerwony, gdy wykonasz kroki na katalogu `Start` - to znak, że start jest już naprawiony. Pułapki, które znikają po naprawie, testy dokumentują na kopii w kroku, który jeszcze je ma (np. s05 krok 1 - antyprzykład nadużycia Boy Scout).
- Sceny s02, s03, s04 i s10 czytają pliki źródłowe sceny. Ścieżki wyznacza pomocnik testów `SourceFiles` (szuka `RefactoringLegacy.slnx` w górę od katalogu wyjściowego testów), więc testy działają niezależnie od katalogu, z którego je uruchamiasz - z `warsztat.sh`, z `dotnet test` i z Rider.
- Sceny s03 i s04 celowo zawierają kod z ostrzeżeniami kompilatora (`Start`/`Step1..2` w s03 i `Sample/Dirty` w s04) - to materiał dla bramek, nie niedoróbka. Build projektu ma `TreatWarningsAsErrors` (`csharp/Directory.Build.props`), więc w s03 plik `M2/S03CompilerGate/.editorconfig` wyłącza te cztery identyfikatory ostrzeżeń tylko dla wariantów sceny, a próbki s04 są wyłączone z kompilacji projektu (`<Compile Remove>` w obu plikach `.csproj`) - bramki kompilują je same, w pamięci.
- IDE: JetBrains Rider z keymapą macOS (skróty jak w IntelliJ): ⇧F6 Rename, ⌥⌘M Extract Method, ⌥⌘V Introduce Variable, ⌥⌘C Introduce Constant, ⌥⌘P Introduce Parameter, ⌥⌘N Inline, ⌘F6 Change Signature, F6 Move, ⌘⌦ Safe Delete, ⌥⌘T Surround With, ⌃T Refactor This (menu wszystkich refaktoryzacji dostępnych w miejscu kursora), ⌥⏎ akcje kontekstowe. Test sceny uruchamiasz skryptem albo z ikony w marginesie (gutter) klasy testowej lub z okna Unit Tests. Refaktoryzacji, których Rider nie ma (Inline Class, Type Migration), dokonujemy ręcznie - przy każdej takiej scenie jest opis, a listę miejsc do poprawy daje build.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji | 3 | `M2.S01Yagni` | ~12 min |
| s02 | 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj | 2 | `M2.S02Adr` | ~10 min |
| s03 | 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 3 | `M2.S03CompilerGate` | ~10 min |
| s04 | 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka | 4 | `M2.S04QualityGate` | ~12 min |
| s05 | 2.9. Zasada harcerza i małe porcje zmian | 2 | `M2.S05BoyScout` | ~8 min |
| s06 | 2.9. Zasada harcerza i małe porcje zmian | 3 | `M2.S06ReviewableSeries` | ~10 min |
| s07 | 2.6. Zasada pojedynczej odpowiedzialności: jeden aktor, jeden powód zmiany | 3 | `M2.S07Srp` | ~12 min |
| s08 | 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji | 3 | `M2.S08Dip` | ~12 min |
| s09 | 2.8. Reguła zależności, porty i korzeń kompozycji | 4 | `M2.S09CleanArchitecture` | ~18 min |
| s10 | 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie) | 2 | `M2.S10BoundaryCheck` | ~12 min |

Pełne nazwy namespace zaczynają się od `Training.Workshop.`, katalogi to `csharp/src/Training.Workshop/M2/S01Yagni` itd.

## Scena s01. YAGNI - silnik reguł dla dwóch reguł

**Temat ze slajdów:** 2.3. YAGNI: buduj dla dzisiejszej potrzeby; 2.4. Pięć pytań przed dodaniem abstrakcji
**Namespace:** `Training.Workshop.M2.S01Yagni` · **Test:** `scripts/warsztat.sh --lang cs test m2/s01` (`S01EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` to silnik z rejestrem pluginów, konfiguracją napisem i kontekstem `IDictionary` - dla dokładnie dwóch reguł. W trzech krokach usuwamy rejestr, zastępujemy słownik typowanymi danymi i wklejamy reguły do jednej klasy.

**Zasada:** YAGNI mówi, żeby nie budować zdolności potrzebnej tylko dla przewidywanego wymagania. Nie zabrania testów, refaktoryzacji ani nazwanych metod - to one pozwalają bezpiecznie odłożyć abstrakcję do chwili, gdy pojawi się realna potrzeba. To praktyczna wersja ostatniego odpowiedzialnego momentu: decyzję o mechanizmie rozszerzeń odkładamy, dopóki nie znamy jego kształtu, a usunięcie spekulacji jest drzwiami dwukierunkowymi.

**Efekt:** Z pięciu typów zostaje jedna klasa z dwiema nazwanymi regułami, a literówka w nazwie reguły nie skompiluje się. Gdy przyjdzie trzecia reguła z innym właścicielem, abstrakcję trzeba będzie wprowadzić ponownie.

**Różnica względem Javy:** kontekst to `IDictionary<string, object>` z rzutowaniami `(TimeOnly)` i `(int)`, fabryki pluginów to `Func<IPricingRule>`, a priorytet reguły to właściwość `Priority`.

### Co widzimy

`TicketPricer` to spekulatywny silnik: rejestr pluginów, konfiguracja napisem `"morning,vip"`, priorytety i kontekst `IDictionary<string, object>`. Obsługuje dokładnie dwie reguły (poranek -5.00, VIP +10.00), które nie zależą od kolejności.

```csharp
var context = new Dictionary<string, object>
{
    ["format"] = quote.Format,
    ["start"] = quote.Start,
    ["row"] = quote.Row,
    ["vipFromRow"] = quote.VipFromRow,
};
var price = BasePrice(quote.Format);
foreach (var rule in _registry.Resolve(_activeRules))
{
    if (rule.AppliesTo(context))
    {
        price = rule.Apply(context, price);
    }
}
```

Na żywo: zmień w konstruktorze `"morning,vip"` na `"morning,vlp"` i uruchom test - błąd konfiguracji wychodzi dopiero w runtime (`ArgumentException: brak reguly: vlp`). Cofnij zmianę.

### Krok 1: Inline Class - rejestr pluginów znika

**W IDE:** w `TicketPricer` zastąp pola `_registry` i `_activeRules` listą `[new MorningRule(), new VipRule()]` (ręcznie - Rider nie ma Inline Class; oba konstruktory usuń). Safe Delete (⌘⌦) na `RuleRegistry` i na właściwości `Priority` w interfejsie (była potrzebna tylko do sortowania).
**Po:**

```csharp
private readonly IReadOnlyList<IPricingRule> _rules = [new MorningRule(), new VipRule()];
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s01` - 16 testów zielonych.
**Co powiedzieć:** konfiguracja napisem nie miała żadnego klienta. Teraz literówka w nazwie reguły nie skompiluje się.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s01 0 1`

### Krok 2: Change Signature - typowane dane zamiast słownika

**W IDE:** ⌘F6 na `IPricingRule.AppliesTo`: typ parametru `IDictionary<string, object>` -> `TicketQuote`; na `Apply` usuń parametr kontekstu. Popraw implementacje (rzutowania znikają), usuń budowanie słownika w `TicketPricer`.
**Po:**

```csharp
public bool AppliesTo(TicketQuote quote)
{
    return quote.Row >= quote.VipFromRow;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** generyczny kontekst to typowa spekulacja "reguła może potrzebować czegokolwiek". Kompilator znów pilnuje nazw pól.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s01 1 2`

### Krok 3: Inline Class dla reguł, Safe Delete interfejsu

**W IDE:** ręcznie (Rider nie ma Inline Class): dwa `if` w `Price` z metodami `IsMorning` i `IsVip` - ciała wklej z `AppliesTo` i `Apply` obu reguł. Safe Delete na `MorningRule`, `VipRule` i `IPricingRule`. Stałe przez ⌥⌘C.
**Po:**

```csharp
public decimal Price(TicketQuote quote)
{
    var price = BasePrice(quote.Format);
    if (IsMorning(quote))
    {
        price -= MorningDiscount;
    }
    if (IsVip(quote))
    {
        price += VipSurcharge;
    }
    return price;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** YAGNI nie zabrania testów, nazwanych metod ani szwu testowego - cena liczona jest z danych wejściowych, bez zegara i stanu statycznego, więc każdą regułę łatwo sprawdzić. Gdy pojawi się trzecia reguła z innym właścicielem, wydzielimy abstrakcję wtedy.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s01 2 3`

### Rozwiązanie i uzasadnienie

Przejdź z salą pięć pytań ze slajdu 2.4: aktualne wymaganie (2 reguły), nazwa w języku problemu (brak: "plugin", "registry"), czy upraszcza aktualny przypadek (nie), koszt usunięcia (niski - pokazaliśmy). Z pięciu typów została jedna klasa.

### Pułapki

- "YAGNI, więc bez testów" - odwrotnie: testy i małe kroki pozwalają bezpiecznie odraczać decyzje.
- Usunięcie abstrakcji, która chroni aktualną, realną granicę (np. port do bazy) - to nie spekulacja.

### Pytanie do sali

Który mechanizm rozszerzeń w waszym systemie ma dziś jedną albo dwie implementacje? Jaki sygnał powiedziałby, że czas go wprowadzić ponownie?

## Scena s02. ADR i wykonywalny model decyzji

**Temat ze slajdów:** 2.5. ADR: zapisz decyzję, a najlepiej ją sprawdzaj
**Namespace:** `Training.Workshop.M2.S02Adr` · **Test:** `scripts/warsztat.sh --lang cs test m2/s02` (`S02EquivalenceTest`, `S02SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** ADR-0007 mówi, że cennik nie zależy od `Notification` i nie używa `double`, a cennik w `Start` sam wysyła mail o rabacie grupowym i liczy w `double`. Cennik zaczyna zwracać `Quote` z informacją o rabacie, mail wysyła `BookingService`, a kwoty przechodzą na `Money`.

**Zasada:** ADR zapisuje decyzję o trwałym wpływie: kontekst, opcje, decyzję i konsekwencje - to dokument historyczny, którego się nie edytuje, tylko zastępuje nowym. Regułę z ADR warto uczynić wykonywalną, żeby build pilnował jej na co dzień. Nie pisze się ADR dla każdego Rename.

**Efekt:** `ArchitectureRules` zwraca pustą listę naruszeń, a maile i odpowiedzi są takie same jak przed zmianą. Reguła tekstowa ma swoje granice (pełna nazwa typu bez dyrektywy `using` albo refleksja ją ominą) i ADR opisuje to w konsekwencjach.

**Różnica względem Javy:** pakiety `pricing`/`notification` to w porcie podprzestrzenie nazw i katalogi `Pricing`/`Notification`. Reguła R1 szuka dyrektywy `using` (regex `^using .*\.Notification\b` zamiast `import ...notification.`), a ponieważ w C# dyrektywa `using` stoi w pierwszej linii pliku, naruszenie startu to `ADR-0007/R1 TicketPricing.cs:1`. Enum `Rule` z polami to klasa `ArchitectureRules.Rule` z instancjami statycznymi i listą `Values`. ADR mówi o przestrzeni nazw zamiast pakietu i o osobnym projekcie (assembly) zamiast modułu Maven.

### Co widzimy

Obok kodu sceny leży `ADR-0007-cennik-jako-czysty-modul.md` (status: Zaakceptowana) z dwiema regułami: **R1** przestrzeń nazw `Pricing` nie zależy od `Notification`, **R2** `Pricing` nie używa `double`. `ArchitectureRules` to wykonywalny model tych reguł. W `Start` cennik sam wysyła mail o rabacie grupowym i liczy w `double`:

```csharp
using Training.Workshop.M2.S02Adr.Start.Notification;
...
public double Total(string organizer, int tickets, double unitPrice)
{
    ...
    _mailer.GroupDiscountGranted(organizer, tickets);
```

### Krok 1: spełnienie R1 - wynik zamiast efektu

**W IDE:** nowy rekord `Quote(double Total, bool GroupDiscount)` w `Pricing`; ⌘F6 na `Total` - bez `organizer`, zwraca `Quote`. Wywołanie `_mailer` przenieś do `BookingService` (warstwa aplikacji). Usuń dyrektywę `using ...Notification` z cennika.
**Po:**

```csharp
Quote quote = _pricing.Total(tickets, unitPrice);
if (quote.GroupDiscount)
{
    _mailer.GroupDiscountGranted(organizer, tickets);
}
```

**Uruchom:** test zielony; `Step1RemovesDependencyOnNotification` - zostało tylko naruszenie `ADR-0007/R2`.
**Co powiedzieć:** naruszenie w teście prowadzi wprost do identyfikatora decyzji i jej uzasadnienia.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s02 0 1`

### Krok 2: spełnienie R2 - Money

**W IDE:** ręcznie zmień typ `double` → `Money` w `Quote` i `TicketPricing` (Rider nie ma Type Migration - build wskaże resztę miejsc); nazwane stałe dla progu i procentu grupy; `BookingService` wybiera cenę jednostkową jako `Money` (`Money.Of("25.00")` itd.).
**Po:**

```csharp
public Quote Total(int tickets, Money unitPrice)
{
    Money sum = unitPrice.Times(tickets);
```

**Uruchom:** test zielony; `Step2CompliesWithTheDecision` - pusta lista naruszeń.
**Co powiedzieć:** moduł jest zgodny z decyzją, a test pilnuje, żeby tak zostało.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s02 1 2`

### Rozwiązanie i uzasadnienie

ADR opisuje kontekst, opcje i konsekwencje (historia), a `ArchitectureRules` sprawdza regułę w każdym buildzie. `AdrDocumentsEveryExecutableRule` pilnuje, żeby każda reguła w kodzie miała opis w ADR.

### Pułapki

- ADR dla każdego Rename - dokumentuje się decyzje o trwałym wpływie.
- Edycja zaakceptowanego ADR zamiast nowego zapisu, który go zastępuje.
- Reguła tekstowa ma granice: pełna nazwa typu bez dyrektywy `using` (`Training.Workshop...Notification.GroupMailer`) albo refleksja ją ominą (opisane w "Konsekwencjach").

### Pytanie do sali

Co zrobić, gdy zespół chce złamać R2 dla jednego przypadku (np. statystyk)? Wyjątek w teście czy nowy ADR?

## Scena s03. Bramka kompilatora - nullable i ostrzeżenia jako błędy

**Temat ze slajdów:** 2.1. Małe kroki i szybka informacja zwrotna; 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Namespace:** `Training.Workshop.M2.S03CompilerGate` · **Test:** `scripts/warsztat.sh --lang cs test m2/s03` (`S03EquivalenceTest`, `S03SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Kod `Start` nie przechodzi bramki Roslyn z włączonym nullable, wszystkimi falami ostrzeżeń i ostrzeżeniami jako błędami: kolekcje niegeneryczne z rzutowaniami w `SeatMap`, przestarzałe `PriceTable.BasePrice(int)` i celowy przelot w `switch` zapisany przez `goto case`. Usuwamy ostrzeżenia po jednej przyczynie na krok: typy generyczne, nowe przeciążenie, switch expression.

**Zasada:** Bramka kompilatora traktuje ostrzeżenia jak błędy i zwraca strukturalną diagnostykę, więc test może wymagać zera ostrzeżeń albo braku nowych. W dużym legacy nie włącza się jej nagle: najpierw stan bazowy, potem "brak nowych naruszeń" i stopniowa redukcja, a tłumienie tylko wąskie i uzasadnione.

**Efekt:** Bramka przechodzi z zerem ostrzeżeń, a raport działa tak samo, z intencją "IMAX ma też Dolby" zapisaną wprost w `switch`. Przestarzała metoda zostaje w `PriceTable`, dopóki ktoś może ją wołać spoza repozytorium.

**Różnica względem Javy:** zamiast `javax.tools` z `--release 25 -Xlint:all -Werror` bramka kompiluje pliki wariantu w pamięci przez Roslyn (`CSharpCompilation`): `NullableContextOptions.Enable`, `warningLevel: 9999` (wszystkie fale ostrzeżeń, odpowiednik `-Xlint:all`) i `ReportDiagnostic.Error` (ostrzeżenia jako błędy, odpowiednik `-Werror`), z tymi samymi globalnymi `using` co `ImplicitUsings` w projekcie. Kategorią ostrzeżenia jest identyfikator Roslyn (`CS8602`), a nie nazwa kategorii `-Xlint`. Zapachy są przełożone na C#: surowe typy i `unchecked` to niegeneryczne `Hashtable`/`ArrayList` z rzutowaniami (CS8600, CS8602), `deprecation` to `[Obsolete]` (CS0618), a ponieważ C# zabrania niejawnego przelotu między `case`, zamierzony przelot jest zapisany jako `goto case 2;`, za którym został martwy `break;` (CS0162, kod nieosiągalny).

### Co widzimy

`CompilerGate.Check` kompiluje wszystkie pliki wariantu i zwraca `Result(Passed, Warnings)` ze strukturalną diagnostyką (identyfikator, plik, linia). Kod `Start` nie przechodzi: `SeatMap` na `Hashtable` i `ArrayList` z rzutowaniami (CS8600, CS8602), `OccupancyReport` woła przestarzałe `PriceTable.BasePrice(int)` (CS0618) i celowo przelatuje z IMAX do 3D przez `goto case`, zostawiając nieosiągalny `break` (CS0162). Test startu: kategorie `["CS0162", "CS0618", "CS8600", "CS8602"]`.

```csharp
private readonly Hashtable _seatsByRow = new();
...
ArrayList seats = (ArrayList)_seatsByRow[row];
...
case 3:
    features = features + "duzy ekran, ";
    goto case 2;
    break;
case 2:
    features = features + "dzwiek Dolby";
    break;
```

Build całego projektu ma `TreatWarningsAsErrors`, więc plik `M2/S03CompilerGate/.editorconfig` wyłącza te cztery identyfikatory tylko dla `Start/` i `Step1..3/` (odpowiednik tego, że Maven nie kompiluje tej sceny z `-Werror`). Plik leży na poziomie sceny, bo `jump`/`reset` kasują i odtwarzają katalog `Start`. `CompilerGate` go nie czyta - bramką jest test sceny.

### Krok 1: typy generyczne

**W IDE:** ręcznie zmień pole `_seatsByRow` na `SortedDictionary<int, List<string>>`, `TryGetValue` zamiast indeksu z rzutowaniem, usuń rzutowania i `using System.Collections`.
**Po:**

```csharp
private readonly SortedDictionary<int, List<string>> _seatsByRow = new();
...
if (!_seatsByRow.TryGetValue(row, out List<string>? seats))
{
    seats = [];
    _seatsByRow[row] = seats;
}
seats.Add(seat);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s03` - zostają kategorie `CS0162`, `CS0618`.
**Co powiedzieć:** jedna przyczyna ostrzeżeń na krok - łatwy przegląd, łatwe wycofanie. Znane typy elementów to także koniec rzutowań, na których analiza nullable nie mogła nic udowodnić.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s03 0 1`

### Krok 2: nowe API zamiast przestarzałego

**W IDE:** w `return` zamień argument `format` na istniejącą już zmienną `name` - wywołanie trafia do `PriceTable.BasePrice(string)`. ⌘klik (Go to Declaration) na metodzie potwierdza, że to nowe przeciążenie.
**Po:**

```csharp
return name + " [" + features + "], cena " + PriceTable.BasePrice(name)
    + " zl, zajete: " + Show(map.TakenPerRow());
```

**Uruchom:** test zielony; zostaje tylko `[CS0162] OccupancyReport.cs:17` (`WarningsPointToFileAndLine`).
**Co powiedzieć:** przestarzałą metodę usuniemy (Safe Delete) dopiero, gdy nikt jej nie woła - także poza repozytorium.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s03 1 2`

### Krok 3: switch expression

**W IDE:** zamień instrukcję `switch` na wyrażenie `switch` (⌥⏎ na `switch` może zaproponować konwersję; `goto case` zwykle ją blokuje, wtedy ręcznie); przelot zapisany wprost: `3 => "duzy ekran, dzwiek Dolby"`.
**Po:**

```csharp
string features = format switch
{
    3 => "duzy ekran, dzwiek Dolby",
    2 => "dzwiek Dolby",
    _ => "standard",
};
```

**Uruchom:** test zielony; `Step3PassesTheGate` - zero ostrzeżeń, bramka z ostrzeżeniami jako błędami przechodzi.
**Co powiedzieć:** intencja "IMAX ma też Dolby" jest teraz w kodzie, a nie w skoku `goto case` i kolejności `case`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s03 2 3`

### Rozwiązanie i uzasadnienie

Bramka zwraca strukturalne wyniki, więc test może wymagać "zero ostrzeżeń" albo "nie więcej niż stan bazowy". Ciekawostka z implementacji: javac z `-Werror` przerywa po fazie z pierwszym ostrzeżeniem i wersja Java potrzebuje drugiego przebiegu, żeby zebrać pełną listę. Roslyn z ostrzeżeniami jako błędami raportuje wszystko w jednym przebiegu (`Emit` daje i werdykt, i pełną diagnostykę), więc `CompilerGate` kompiluje raz.

### Pułapki

- Nagłe włączenie `TreatWarningsAsErrors` w dużym legacy blokuje zespół - najpierw stan bazowy i "brak nowych naruszeń".
- `<NoWarn>` w `.csproj` albo `#pragma warning disable` bez numeru na całym pliku zamiast wąskiego wyłączenia z uzasadnieniem (tu: `.editorconfig` tylko dla wariantów sceny, z komentarzem dlaczego).
- `TargetFramework` i `LangVersion` w `.csproj` nie kontrolują, którym SDK buduje CI - wersję SDK przypina `global.json`.

### Pytanie do sali

Jak wprowadzić tę bramkę w projekcie z 3000 ostrzeżeń, nie blokując nikogo od jutra?

## Scena s04. Minimalna bramka jakości jako kod

**Temat ze slajdów:** 2.2. Bramki jakości w starym kodzie: stan bazowy i zapadka
**Namespace:** `Training.Workshop.M2.S04QualityGate` · **Test:** `scripts/warsztat.sh --lang cs test m2/s04` (`S04EquivalenceTest`, `S04SolutionTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `QualityGate` w `Start` to lista kontrolna w komentarzu dokumentacyjnym i metoda, która zawsze zwraca pustą listę, więc przepuszcza brudną próbkę. Zamieniamy listę na cztery wykonywalne sprawdzenia: skan TODO i wydruków na konsolę, ostrzeżenia kompilatora, przybliżenie pokrycia i uruchomienie testów.

**Zasada:** Minimalna bramka jakości łączy kilka niezależnych sygnałów, bo każde narzędzie dowodzi czegoś innego. Wynik ma być deterministyczny i wskazywać miejsce, a bramka potrzebuje właściciela i nie może dawać fałszywych alarmów - inaczej zespół ją wyłączy.

**Efekt:** Bramka zgłasza problemy `Sample/Dirty`, zatrzymuje nieprzechodzący test z `Sample/Broken` i przepuszcza `Sample/Clean`. Sprawdzenie pokrycia to tylko przybliżenie ("czy jakiś test w ogóle woła metodę") i nie zastępuje pomiaru coverlet.

**Różnica względem Javy:** próbki `Sample/Clean`, `Sample/Dirty` i ich testy (`Sample/{Clean,Dirty,Broken}/PriceTableTest.cs` w projekcie testów) nie wchodzą do kompilacji projektu (`<Compile Remove>` w obu plikach `.csproj`) - bramka czyta je przez `SourceFiles` i kompiluje sama, Roslynem w pamięci. Skutek: testy próbek nie są uruchamiane przez xUnit (w Javie to zwykłe testy JUnit), uruchamia je tylko bramka z kroku 4. Skan szuka `Console.Write`/`Console.Error` zamiast `System.out`/`System.err`, a "surowy typ" z Javy to niegeneryczna `ArrayList` w polu, którego nikt nie przypisuje - kompilator zgłasza dla niego CS0649 i CS8618 zamiast dwóch `compiler.warn.raw.class.use`. Fikstura broken ma własny atrybut `Test` i wyjątek `AssertionException` (w Javie `AssertionError`). Z tego powodu `scripts/warsztat.sh --lang cs test m2` liczy 150 testów, a wersja Java 153: trzy testy JUnit próbek `sample/clean` i `sample/dirty` w C# uruchamia tylko bramka.

### Co widzimy

`Start.QualityGate` to lista kontrolna w komentarzu `<summary>` i metoda `Evaluate`, która zawsze zwraca pustą listę - przepuszcza próbkę `Sample/Dirty` (TODO, `Console.WriteLine`, niegeneryczna kolekcja, metody bez testu). Bramka działa na `GateInput`: katalog źródeł domeny, plik testu, kluczowa klasa i pełna nazwa klasy testowej.

```csharp
public IReadOnlyList<string> Evaluate(GateInput input)
{
    return [];
}
```

### Krok 1: skan źródeł - TODO i Console

**W IDE:** metoda `ScanSources`: każda linia z `TODO`/`FIXME` albo `Console.Write`/`Console.Error` to wynik z plikiem i linią.
**Po:**

```csharp
if (line.Contains("TODO") || line.Contains("FIXME"))
{
    findings.Add("TODO " + where);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s04` - `TODO PriceTable.cs:15`, `Console PriceTable.cs:26`.
**Co powiedzieć:** tanie, deterministyczne, z pozycją - od tego zaczynamy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s04 0 1`

### Krok 2: ostrzeżenia kompilatora

**W IDE:** metoda `CompileSources` (Roslyn w pamięci: nullable włączone, `warningLevel: 9999`, te same globalne `using` co w projekcie), każde ostrzeżenie i błąd w źródłach to wynik.
**Po:**

```csharp
findings.Add("kompilator " + Path.GetFileName(d.Location.SourceTree!.FilePath) + ":"
    + (d.Location.GetLineSpan().StartLinePosition.Line + 1) + " " + d.Id);
```

**Uruchom:** test zielony; dochodzą dwa wyniki: `kompilator PriceTable.cs:11 CS0649` (pole nigdy nie jest przypisane) i `kompilator PriceTable.cs:11 CS8618` (pole nie dopuszcza null, a konstruktor go nie ustawia).
**Co powiedzieć:** to ta sama idea co w s03, tu jako jeden z kilku sygnałów bramki.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s04 1 2`

### Krok 3: pokrycie kluczowej klasy (przybliżenie)

**W IDE:** metoda `CheckCoverage`: każda publiczna metoda `PriceTable` musi być wywołana w `PriceTableTest`.
**Po:**

```csharp
if (!test.Contains("." + method.Groups[1].Value + "("))
{
    findings.Add("pokrycie " + input.KeyClass + "." + method.Groups[1].Value + " bez testu");
}
```

**Uruchom:** test zielony; `VipSurcharge` i `LookupCount` bez testu.
**Co powiedzieć:** to nie zastępuje pomiaru pokrycia (`dotnet test --collect:"XPlat Code Coverage"` - pakiet `coverlet.collector` jest w projekcie testów), ale łapie metodę, której żaden test nawet nie woła.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s04 2 3`

### Krok 4: testy zielone

**W IDE:** metoda `RunTests`: Roslyn kompiluje domenę razem z plikiem testu, zestaw trafia do zwalnianego `AssemblyLoadContext`, a refleksja uruchamia metody z atrybutem o nazwie `Fact` albo `Test` i zgłasza porażki.
**Po:**

```csharp
try
{
    test.Invoke(Activator.CreateInstance(type, nonPublic: true), null);
}
catch (TargetInvocationException failure)
{
    findings.Add("test " + type.Name + "." + test.Name + " nie przechodzi: "
        + failure.InnerException?.GetType().Name);
}
```

**Uruchom:** test zielony; `Step4AddsGreenTestsAndCatchesAFailingOne` - fikstura `Sample/Broken` (celowo nie xUnit, żeby nie psuć buildu) daje wynik `test PriceTableTest.VipSurchargeStartsAtRowNine nie przechodzi: AssertionException`.
**Co powiedzieć:** lista kontrolna jest w całości wykonywalna i ma ten sam wynik u każdego.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s04 3 4`

### Rozwiązanie i uzasadnienie

Bramka łączy kilka niezależnych sygnałów. `S04EquivalenceTest` pilnuje drugiej strony: czysta próbka przechodzi każdą wersję bramki (wynik `[]`, brak fałszywych alarmów).

### Pułapki

- Bramka, która nic nie sprawdza, a daje zielony znaczek.
- Przybliżenie pokrycia potraktowane jak pomiar.
- Bramka bez właściciela - każdy fałszywy alarm kończy się jej wyłączeniem.

### Pytanie do sali

Które z tych czterech sprawdzeń uruchomilibyście lokalnie przed commitem, a które tylko w CI?

## Scena s05. Boy Scout Rule - mała poprawa i jej nadużycie

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Namespace:** `Training.Workshop.M2.S05BoyScout` · **Test:** `scripts/warsztat.sh --lang cs test m2/s05` (`S05EquivalenceTest`, `S05SolutionTest`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `TicketPrinter.Print` i tak otwieramy dla nowej linii "Sala", a kod ma nazwy `s`, `d`, `x` i ręczne sklejanie listy miejsc. Najpierw pokazujemy antyprzykład "skoro już tu jestem", który po cichu zmienia wydruk, a potem robimy małą poprawę tylko w tej metodzie.

**Zasada:** Boy Scout Rule mówi: dotykany kod zostaw w nieco lepszym stanie - to heurystyka, nie licencja na przebudowę. Poprawa jest mała, lokalna, dotyczy kodu bieżącej zmiany i nie zmienia zachowania; zmiana reguły biznesowej pod etykietą sprzątania to nadużycie.

**Efekt:** Metoda ma czytelne nazwy, `string.Join`, `StringBuilder` i `PhoneLine`, a test równoważności potwierdza identyczny wydruk. Sortowanie miejsc czy małe litery w e-mailu mogą być dobrymi pomysłami, ale trafiają do osobnego commita jako świadoma zmiana zachowania.

**Różnica względem Javy:** data i godzina seansu są w wydruku formatowane jawnie (`yyyy-MM-dd` i `HH:mm` z `CultureInfo.InvariantCulture`) - to odpowiednik `LocalDate.toString()`/`LocalTime.toString()`, który w C# nie istnieje bez podania formatu. Antyprzykład sortuje miejsca porządkiem ordinalnym (`Order(StringComparer.Ordinal)`), tak jak `sorted()` w Javie, więc `A9, A10` staje się `A10, A9`.

### Co widzimy

`Start.TicketPrinter.Print` trzeba i tak otworzyć, bo w sprincie dochodzi linia "Sala". Kod ma nazwy `s`, `d`, `x`, konkatenację w pętli i ręczne sklejanie listy miejsc. Kusi, żeby "posprzątać wszystko".

```csharp
string x = "";
for (int i = 0; i < t.Seats.Count; i++)
{
    if (i > 0)
    {
        x = x + ", ";
    }
    x = x + t.Seats[i];
}
```

### Krok 1: nadużycie - "skoro już tu jestem"

**W IDE:** pokaż antyprzykład: `scripts/warsztat.sh --lang cs jump m2/s05 1`. Obok dobrych ruchów przemycono sortowanie miejsc, e-mail małymi literami i pominięcie linii "Tel" bez telefonu.
**Po:**

```csharp
IReadOnlyList<string> sortedSeats = ticket.Seats.Order(StringComparer.Ordinal).ToList();
text.Append("Klient: ").Append(ticket.Email.Trim().ToLowerInvariant()).Append('\n');
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s05` - równoważność czerwona w trzech przypadkach (m.in. `A9, A10` staje się `A10, A9`). `S05SolutionTest.AbusiveCleanupChangesBehaviourInThreeCases` dokumentuje, które.
**Co powiedzieć:** każda z tych zmian może być dobrym pomysłem, ale to decyzje biznesowe, nie sprzątanie. Cofamy: `scripts/warsztat.sh --lang cs reset m2/s05`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s05 0 1`

### Krok 2: poprawna, mała poprawa

**W IDE:** Rename (⇧F6) `t` → `ticket`; pętlę zamień ręcznie na `string.Join`; `StringBuilder` zamiast konkatenacji; Extract Method (⌥⌘M) `PhoneLine`.
**Po:**

```csharp
.Append("Miejsca: ").Append(string.Join(", ", ticket.Seats)).Append('\n')
.Append("Klient: ").Append(ticket.Email.Trim()).Append('\n')
.Append(PhoneLine(ticket)).Append('\n')
```

**Uruchom:** test zielony.
**Co powiedzieć:** tylko dotykana metoda, bez zmiany kontraktu, diff do przejrzenia w minutę. Nowa linia "Sala" to osobny commit (patrz s06).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s05 0 2`

### Rozwiązanie i uzasadnienie

Boy Scout Rule to heurystyka: mała, lokalna, weryfikowalna poprawa w kodzie bieżącej zmiany. Test różnicowy (start kontra krok) jest bezstronnym arbitrem, czy "sprzątanie" nie zmieniło zachowania.

### Pułapki

- Zmiana zachowania pod etykietą refaktoryzacji.
- Masowe formatowanie całego pliku (np. Reformat and Cleanup Code na całym projekcie) razem z poprawką - recenzent nie znajdzie zmiany merytorycznej.
- "Sprzątanie" cudzego modułu bez uzgodnienia.

### Pytanie do sali

Posortowane miejsca są czytelniejsze. Jak wprowadzić tę zmianę uczciwie?

## Scena s06. Seria małych zmian gotowych do przeglądu

**Temat ze slajdów:** 2.9. Zasada harcerza i małe porcje zmian
**Namespace:** `Training.Workshop.M2.S06ReviewableSeries` · **Test:** `scripts/warsztat.sh --lang cs test m2/s06` (`S06EquivalenceTest`, `S06SolutionTest`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `PriceList.Price` ma dostać "Tani wtorek" (NORMAL -20% we wtorek), a zniżki są wplecione w jedną metodę i nie widzą daty. Zamiast jednego commita "porządki + tani wtorek" robimy serię: Extract Method, Change Signature, a dopiero potem nowa reguła.

**Zasada:** Dobry zestaw zmian ma jedną intencję, własny dowód i zielony build po integracji. Refaktoryzację oddziela się od zmiany funkcjonalnej, formatowanie od logiki, a diff automatyczny od ręcznego - najpierw ułatw zmianę, potem zrób łatwą zmianę.

**Efekt:** Dwa pierwsze commity dowodzi test równoważności, a trzeci świadomie zmienia zachowanie: test różnicowy pokazuje, że zmieniło się dokładnie 12 przypadków, wszystkie NORMAL we wtorek. Kosztem jest więcej commitów, ale recenzent sprawdza w każdym tylko jedną rzecz.

### Co widzimy

`Start.PriceList.Price` ma dostać promocję "Tani wtorek": bilet NORMAL -20% ceny bazowej we wtorek. Zniżki są wplecione w jedną metodę i zależą tylko od typu biletu, a nowa reguła potrzebuje daty. Pokusa: jeden commit "porządki + tani wtorek".

```csharp
decimal d;
if (q.Type.Equals("STUDENT"))
{
    d = p * 0.25m;
}
else if ...
```

### Krok 1: commit 1 - refaktoryzacja (Extract Method)

**W IDE:** ⌥⌘M na wyborze ceny bazowej → `BasePrice(string format)`; ⌥⌘M na wyborze zniżki → `DiscountPercent(string type)` zwracające procent; Money zamiast `decimal`.
**Po:**

```csharp
Money @base = BasePrice(query.Format);
Money price = @base.Minus(@base.Percent(DiscountPercent(query.Type)));
```

**Uruchom:** test zielony - równoważność na przypadkach, w tym wtorkowych.
**Co powiedzieć:** opis commita: "refaktoryzacja, bez zmiany zachowania, dowód: S06EquivalenceTest". Recenzent sprawdza tylko mechanikę.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s06 0 1`

### Krok 2: commit 2 - refaktoryzacja przygotowawcza (Change Signature)

**W IDE:** ⌘F6 na `DiscountPercent`: parametr `TicketQuery query` zamiast `string type`, w ciele `query.Type switch`.
**Po:**

```csharp
private static int DiscountPercent(TicketQuery query) => query.Type switch
{
    ...
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** "make the change easy, then make the easy change". Ten commit nadal nie zmienia zachowania.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s06 1 2`

### Krok 3: commit 3 - zmiana zachowania

**W IDE:** w ramieniu `_` warunek `IsCheapTuesday(query) ? CheapTuesdayPercent : 0`, metoda pomocnicza, stała `CheapTuesdayPercent`.
**Po:**

```csharp
_ => IsCheapTuesday(query) ? CheapTuesdayPercent : 0,
```

**Uruchom:** test zielony; `S06SolutionTest.CheapTuesdayGivesNormalTicketTwentyPercentOff` (2D wtorek 20.00) i `BehaviourChangeIsLimitedToNormalTicketsOnTuesday` - na siatce 336 przypadków zmieniło się dokładnie 12, wszystkie NORMAL we wtorek.
**Co powiedzieć:** diff to kilka linii. Test różnicowy commitu 2 kontra 3 mówi recenzentowi dokładnie, gdzie zmieniło się zachowanie.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s06 2 3`

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
**Namespace:** `Training.Workshop.M2.S07Srp` · **Test:** `scripts/warsztat.sh --lang cs test m2/s07` (`S07EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `DailyReport` składa raport dla księgowości i marketingu, a wspólny helper `Revenue` sprawia, że zmiana dla jednego aktora cicho zmienia kwoty drugiego. Wydzielamy sekcje, rozdzielamy helper i tworzymy klasę na każdego aktora.

**Zasada:** SRP mówi, że moduł powinien odpowiadać przed jednym aktorem, czyli mieć jeden powód zmiany. To nie jest "klasa robi jedną rzecz" ani jedna metoda na klasę - klasa może mieć kilka metod, jeśli wszystkie zmieniają się dla tego samego aktora.

**Efekt:** Zmiana definicji hitu dotyka tylko `MarketingSection`, a zmiana stawki VAT tylko `AccountingSection`. Koszt: dwie identyczne dziś formuły, powtórzone świadomie, bo to kod, a nie wspólna wiedza.

**Różnica względem Javy:** `TreeMap` to `SortedDictionary<string, decimal>(StringComparer.Ordinal)`, a `merge` to `GetValueOrDefault(...) + ...`. Klasy sekcji w kroku 3 są `internal` (odpowiednik widoczności pakietowej).

### Co widzimy

`DailyReport.Render` składa raport dla księgowości (przychód brutto i netto wg VAT 8% i 23%) i dla marketingu (hit dnia, liczba biletów). Obie części korzystają ze wspólnego helpera `Revenue`. Gdy marketing poprosi "hit dnia licz bez baru", poprawka helpera po cichu zmieni też "Razem brutto" dla księgowości.

```csharp
private static decimal Revenue(Sale sale)
{
    return sale.TicketRevenue + sale.BarRevenue;
}
...
total += Revenue(sale);                                                     // księgowość
byTitle[sale.Title] = byTitle.GetValueOrDefault(sale.Title) + Revenue(sale); // marketing
```

### Krok 1: Extract Method według aktora

**W IDE:** zaznacz blok pod `// księgowość`, ⌥⌘M `AccountingSection` (zwraca `string`, własny `StringBuilder`); to samo dla `// marketing` jako `MarketingSection`.
**Po:**

```csharp
public string Render(IReadOnlyList<Sale> sales)
{
    return AccountingSection(sales) + MarketingSection(sales);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s07` - 12 testów zielonych (w tym remis tytułów i dzień bez sprzedaży).
**Co powiedzieć:** komentarze nazywały aktorów - teraz nazywają je metody. Helper `Revenue` nadal wiąże oba światy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s07 0 1`

### Krok 2: Rozdziel wspólny helper według aktora

**W IDE:** zaznacz metodę `Revenue` i zduplikuj ją (⌘D), nazwij kopię `Popularity` (⇧F6), w `MarketingSection` użyj `Popularity`. Dodaj komentarz XML (`/// <summary>`) z właścicielem każdej metody.
**Po:**

```csharp
/// <summary>Księgowość: przychód brutto seansu.</summary>
private static decimal Revenue(Sale sale) { ... }

/// <summary>Marketing: miara popularności filmu (dziś: bilety + bar).</summary>
private static decimal Popularity(Sale sale) { ... }
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie powtarzamy kod, nie wiedzę - dziś formuły są równe przypadkiem. To lekcja z DRY (slajd 2.6: dwie identyczne dziś formuły to nie duplikacja wiedzy) w kontekście SRP.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s07 1 2`

### Krok 3: Extract Class - klasa na aktora

**W IDE:** ⌃T > Extract Class dla `AccountingSection`, `Revenue`, `Net` -> klasa `AccountingSection`; to samo dla `MarketingSection`, `Popularity` -> `MarketingSection`. Metody nazwij `Render`, klasy oznacz `internal`.
**Po:**

```csharp
public sealed class DailyReport
{
    private readonly AccountingSection _accounting = new();
    private readonly MarketingSection _marketing = new();

    public string Render(IReadOnlyList<Sale> sales)
    {
        return _accounting.Render(sales) + _marketing.Render(sales);
    }
}
```

**Uruchom:** test zielony - dokument identyczny co do znaku.
**Co powiedzieć:** `DailyReport` koordynuje, ale nie zna polityk. Zmiana definicji hitu dotyka tylko `MarketingSection`, zmiana stawki VAT tylko `AccountingSection`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s07 2 3`

### Rozwiązanie i uzasadnienie

Odpowiedzialność = aktor i jego powód zmiany, nie "jedna rzecz". `AccountingSection` ma trzy metody i to jest w porządku: wszystkie zmieniają się dla księgowości.

### Pułapki

- Dzielenie na klasy "po jednej metodzie" zamiast według aktora.
- Zostawienie wspólnego helpera między nowymi klasami (np. w statycznej klasie `ReportUtils` albo metodzie rozszerzającej) - powrót do problemu.

### Pytanie do sali

Kto w waszej organizacji zgłasza zmiany do największej klasy w systemie? Ilu to różnych aktorów?

## Scena s08. DIP - kierunek zależności kontra przepływ sterowania

**Temat ze slajdów:** 2.7. Zasada odwrócenia zależności: liczy się kierunek importów; 2.8. Reguła zależności, porty i korzeń kompozycji
**Namespace:** `Training.Workshop.M2.S08Dip` · **Test:** `scripts/warsztat.sh --lang cs test m2/s08` (`S08DependencyDirectionTest`, `S08EquivalenceTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Przypadek użycia `ConfirmReservation` sam tworzy klienta SMTP i zna protokół, więc `using` i wywołania biegną `App -> Infra`. Wstrzykujemy zależność, nazywamy potrzebę polityki i wprowadzamy port `ICustomerNotifier` z adapterem w `Infra`.

**Zasada:** DIP mówi, że zależności źródłowe mają wskazywać od szczegółów ku polityce i abstrakcjom, a port nazywa potrzebę klienta, nie kształt technologii. DIP to nie dependency injection: wstrzyknięcie konkretnej klasy przez konstruktor wciąż wiąże politykę ze szczegółem.

**Efekt:** Sterowanie nadal płynie do `Infra`, ale zależność źródłowa odwróciła się na `Infra -> App`, a politykę da się przetestować ręcznym fake'iem. Wystarczyła ręczna statyczna klasa `Main` jako composition root, bez kontenera DI.

**Różnica względem Javy:** test kierunku zależności sprawdza refleksją przestrzeń nazw typów pól (`Namespace.EndsWith(".Infra")`) zamiast nazwy pakietu, a fake portu w `Step3PolicyIsTestableWithAHandWrittenFake` to prywatna klasa `FakeNotifier(Action<string, string>)` zamiast lambdy.

### Co widzimy

Przypadek użycia `App.ConfirmReservation` sam tworzy klienta `Infra.SmtpMailSender`, składa nagłówki MIME i interpretuje kody SMTP. `using` i przepływ sterowania biegną w tę samą stronę: `App -> Infra`. `Main` to composition root wariantu - test woła tylko `Main.ConfirmReservation()`.

```csharp
private readonly SmtpMailSender _mail = new("smtp.kino.pl", 25);
...
var mime = "To: " + reservation.Email + "\r\nSubject: Rezerwacja\r\n\r\n" + message;
var reply = _mail.Send(reservation.Email, mime);
if (!reply.StartsWith("250", StringComparison.Ordinal))
{
    throw new InvalidOperationException("SMTP odrzucil: " + reply);
}
```

### Krok 1: Introduce Parameter - dependency injection

**W IDE:** inicjalizator pola przenieś do konstruktora (⌥⏎ na polu, akcja przeniesienia inicjalizacji do konstruktora, albo ręcznie dopisz konstruktor), zaznacz w nim `new SmtpMailSender("smtp.kino.pl", 25)` i ⌥⌘P (Introduce Parameter). Rider przeniesie tworzenie obiektu do wywołania w `Main`.
**Po:**

```csharp
public ConfirmReservation(SmtpMailSender mail)
{
    _mail = mail;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s08` - 18 testów zielonych.
**Co powiedzieć:** to jest DI, ale jeszcze nie DIP - `using ...Infra;` nadal stoi w polityce. Test `Step1AndStep2PolicyDependsOnInfrastructure` to potwierdza.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s08 0 1`

### Krok 2: Extract Method - nazwij potrzebę polityki

**W IDE:** zaznacz składanie MIME, `Send` i sprawdzenie kodu, ⌥⌘M, nazwa `NotifyCustomer(string email, string message)`.
**Po:**

```csharp
NotifyCustomer(reservation.Email, message);
return "potwierdzono: " + reservation.Email;
```

**Uruchom:** test zielony.
**Co powiedzieć:** tak wygląda port, zanim stanie się interfejsem: potrzeba nazwana w języku problemu ("powiadom klienta"), a cała technologia w jednym miejscu. Nie robimy Extract Interface z `SmtpMailSender` - dostalibyśmy port `Send(to, mime)`, czyli kształt technologii, nie potrzeby.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s08 1 2`

### Krok 3: Port po stronie polityki, adapter w Infra

**W IDE:** ⌃T > Extract Class na `NotifyCustomer` i polu `_mail` -> klasa `SmtpCustomerNotifier`, potem F6 (Move to Folder) do `Infra`. Na nowej klasie ⌃T > Extract Interface `ICustomerNotifier` z metodą `NotifyCustomer` i przenieś interfejs (F6) do `App`. W `ConfirmReservation` typ pola i parametru -> `ICustomerNotifier`. W `Main` złóż `new ConfirmReservation(new SmtpCustomerNotifier(new SmtpMailSender(...)))`.
**Po:**

```csharp
public interface ICustomerNotifier                         // App
{
    void NotifyCustomer(string email, string message);
}

public sealed class SmtpCustomerNotifier : ICustomerNotifier   // Infra
{
```

**Uruchom:** test zielony; `StepsSendTheSameMimeMessage` potwierdza identyczny MIME, `Step3PolicyDependsOnlyOnItsOwnPort` - że polityka nie ma już pól z `Infra`, a `Step3PolicyIsTestableWithAHandWrittenFake` testuje politykę ręcznym fake'iem.
**Co powiedzieć:** sterowanie nadal płynie `App -> Infra` (`Confirm` woła notifier), ale zależność źródłowa odwróciła się: `Infra -> App`. To jest DIP.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s08 2 3`

### Rozwiązanie i uzasadnienie

Port należy do strony formułującej potrzebę. Adapter tłumaczy kody SMTP na błąd kontraktu (`InvalidOperationException`), więc polityka nie zna protokołu. Kontener DI (`Microsoft.Extensions.DependencyInjection`) nie był potrzebny - wystarczyła ręczna klasa `Main`.

### Pułapki

- "Rejestrujemy wszystko w `IServiceCollection`, więc mamy DIP" - krok 1 pokazuje, że nie.
- Port odbijający API technologii (`Send(host, port, mime)`) - wymiana kanału na SMS i tak zmieni politykę.
- Wyjątek technologii (np. `SmtpException` czy `DbException`) w kontrakcie portu.

### Pytanie do sali

Narysujcie strzałki `using` (zależności) i strzałki wywołań dla jednego waszego przypadku użycia. W którym miejscu biegną w tę samą stronę przez granicę?

## Scena s09. Clean Architecture - use case, dane na granicy, composition root

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji
**Namespace:** `Training.Workshop.M2.S09CleanArchitecture` · **Test:** `scripts/warsztat.sh --lang cs test m2/s09` (`S09EquivalenceTest`, `S09UseCaseTest`)
**Czas:** ~18 min

### W skrócie

**Co robimy:** `ReservationController` parsuje żądanie, liczy cenę, zapisuje wiersz i publikuje komunikat w jednej metodzie. W czterech krokach wydzielamy przypadek użycia, porty z adapterami, przestrzenie nazw `App` i `Adapter` oraz composition root.

**Zasada:** Clean Architecture to zasada, że polityka (przypadki użycia) nie zależy od mechanizmów: porty należą do strony formułującej potrzebę, granicę przekraczają proste rekordy, a composition root składa graf bez reguł biznesowych. Liczy się kierunek zależności (`using`), a nie nazwy czterech folderów.

**Efekt:** Przypadek użycia testujemy bez HTTP i bazy, a kolejność zapis-powiadomienie jest jawną decyzją protokołu. Koszt to siedem typów więcej, uzasadniony dwoma realnymi efektami zewnętrznymi.

**Różnica względem Javy:** parametry żądania to `IReadOnlyDictionary<string, string> parameters` (`params` to w C# słowo kluczowe), `getOrDefault` to `GetValueOrDefault`, a wiersz bazy to `object[]` budowany wyrażeniem kolekcji `[...]`. Porty w `S09UseCaseTest` to prywatne klasy `FakeStore`/`FakeNotifier` na delegatach zamiast lambd.

### Co widzimy

`ReservationController` obsługuje żądanie "HTTP" (`IReadOnlyDictionary<string, string>`) i robi wszystko: parsuje parametry, liczy cenę (format + VIP), zapisuje wiersz `object[]` w `RowStore`, publikuje komunikat w `Outbox`, buduje odpowiedź. `RowStore` i `Outbox` to stabilny "świat zewnętrzny" sceny. Test wchodzi przez `CinemaApplication.ReservationController(db, outbox)` i porównuje odpowiedź, zapisane wiersze i komunikaty.

```csharp
string id;
try
{
    id = _db.Insert([email, format, rows.Count, total]);
}
catch (InvalidOperationException e)
{
    return "503 " + e.Message;
}
_outbox.Publish("reservation-created",
    id + ";" + email + ";" + total.ToString(CultureInfo.InvariantCulture));
return "201 " + id + " " + total.ToString(CultureInfo.InvariantCulture);
```

### Krok 1: Extract Class - przypadek użycia jako jawna orkiestracja

**W IDE:** utwórz rekordy `BookSeatsCommand(Email, Format, Rows)` i `Booking(Id, Total)`. Zaznacz w kontrolerze wycenę, zapis i publikację, ⌥⌘M `Execute`, potem ⌃T > Extract Class (albo Move Instance Method, F6) do nowej klasy `BookSeats`. Kontroler mapuje wyjątki: `ArgumentException` -> 400, `InvalidOperationException` -> 503.
**Po:**

```csharp
public Booking Execute(BookSeatsCommand command)
{
    if (command.Rows.Count == 0)
    {
        throw new ArgumentException("brak miejsc");
    }
    var total = Price(command);
    var id = _db.Insert(
        [command.Email, command.Format, command.Rows.Count, total]);
    _outbox.Publish("reservation-created",
        id + ";" + command.Email + ";" + total.ToString(CultureInfo.InvariantCulture));
    return new Booking(id, total);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s09` - 22 testy zielone.
**Co powiedzieć:** przypadek użycia czyta się jak scenariusz: wyceń, zapisz, powiadom. Granicę przekraczają proste rekordy, nie słownik z HTTP. Wciąż zna jednak kolumny tabeli i temat komunikatu.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s09 0 1`

### Krok 2: Porty zdefiniowane przez potrzebę, adaptery

**W IDE:** utwórz `NewReservation` (rekord), porty `IReservationStore.Save(NewReservation)` i `IBookingNotifier.ReservationCreated(id, reservation)`. Przenieś (ręcznie lub ⌃T > Extract Class) mapowanie na `object[]` do `RowStoreReservationStore`, publikację do `OutboxBookingNotifier`. `BookSeats` przyjmuje porty.
**Po:**

```csharp
var id = _store.Save(reservation);
_notifier.ReservationCreated(id, reservation);
return new Booking(id, reservation.Total);
```

**Uruchom:** test zielony, także przypadek "baza niedostępna - brak powiadomienia".
**Co powiedzieć:** kolejność zapis -> powiadomienie to decyzja protokołu: błąd zapisu oznacza brak powiadomienia. Kolejność nie daje atomowości - w systemie rozproszonym potrzebny byłby outbox transakcyjny.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s09 1 2`

### Krok 3: Move to Namespace - granica widoczna w przestrzeniach nazw

**W IDE:** F6 (Move to Folder, z aktualizacją przestrzeni nazw) na `BookSeats`, `BookSeatsCommand`, `Booking`, `NewReservation`, `IReservationStore`, `IBookingNotifier` -> folder `App`; F6 na kontrolerze i obu adapterach -> `Adapter`. Rider poprawi dyrektywy `using` (także w `CinemaApplication`).
**Po:**

```text
Step3/App/       BookSeats, BookSeatsCommand, Booking, NewReservation, IReservationStore, IBookingNotifier
Step3/Adapter/   ReservationController, RowStoreReservationStore, OutboxBookingNotifier
```

**Uruchom:** test zielony.
**Co powiedzieć:** `App` nie ma żadnego `using ...Adapter`; adaptery używają `App`. Nazwy przestrzeni nazw są drugorzędne - liczy się kierunek zależności. Zauważ, że w jednym projekcie (csproj) kompilator tego kierunku nie pilnuje - wrócimy do tego w s10.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s09 2 3`

### Krok 4: Composition root

**W IDE:** w konstruktorze kontrolera zaznacz `new BookSeats(...)` i ⌥⌘P (Introduce Parameter) - tworzenie grafu przeniesie się do `CinemaApplication`. Usuń nieużywane parametry `db`, `outbox` z kontrolera (⌘F6).
**Po:**

```csharp
public static ReservationController ReservationController(RowStore db, Outbox outbox)
{
    var bookSeats = new BookSeats(
        new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
    return new ReservationController(bookSeats);
}
```

**Uruchom:** test zielony; pokaż `S09UseCaseTest`: przypadek użycia z ręcznymi fake'ami portów zamiast adapterów, w tym `DoesNotNotifyWhenSavingFails`.
**Co powiedzieć:** composition root zna wszystkie konkrety i nie zawiera reguł biznesowych. Podmiana bazy to zmiana tylko tutaj. Kontener DI nie jest potrzebny (a jeśli jest, to rejestracje w `Program.cs` pełnią tę samą rolę).
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s09 3 4`

### Rozwiązanie i uzasadnienie

Przypadek użycia testowany bez HTTP i bazy, adaptery wymienialne w jednym miejscu, dane na granicy jako rekordy. Koszt: siedem typów więcej. Uzasadnia go to, że dwa efekty zewnętrzne i protokół ich kolejności już istnieją (slajd 2.4, pytanie o koszt abstrakcji).

### Pułapki

- Kopiowanie szablonu czterech folderów (albo czterech projektów) bez sprawdzenia kierunku zależności.
- Osobny model na każdej granicy "bo tak trzeba" - `NewReservation` ma sens, bo oddziela semantykę od `object[]`.
- Reguły biznesowe w composition root albo w kontrolerze (np. walidacja miejsc w kontrolerze).

### Pytanie do sali

Czy wasz przypadek użycia da się uruchomić w teście bez ASP.NET Core i bazy? Co trzeba by przenieść, żeby się dało?

## Scena s10. Automatyczna ochrona granicy i diagnostyka spójności

**Temat ze slajdów:** 2.8. Reguła zależności, porty i korzeń kompozycji (sprawdzana automatycznie)
**Namespace:** `Training.Workshop.M2.S10BoundaryCheck` · **Test:** `scripts/warsztat.sh --lang cs test m2/s10` (`S10ArchitectureTest`, `S10EquivalenceTest`, `S10ToolsTest`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy i używa typów adaptera oraz `System.Data.SqlTypes`. Wydzielamy maper, przenosimy go do adaptera, a narzędzia `BoundaryRule` i `CohesionProbe` mierzą efekt po każdym kroku.

**Zasada:** Spójny moduł zmienia się z jednego powodu, a reguła zależności mówi, że domena nie zależy od technologii - to dwa niezależne wymiary. Regułę architektury warto sprawdzać automatycznie przy każdym buildzie, a metryki takie jak LCOM4 traktować jako sygnał do rozmowy, nie wyrocznię.

**Efekt:** Domena jest wolna od `System.Data` i typów adaptera, a każda klasa ma LCOM4 równe 1. Skan dyrektyw `using` jest słabą bramką - pełna nazwa typu w kodzie przejdzie, więc silniejsze są NetArchTest / ArchUnitNET, osobne projekty z kontrolowanymi referencjami albo analizatory Roslyn.

**Różnica względem Javy:** technologią bazy jest `System.Data.SqlTypes.SqlDateTime` (w BCL, bez pakietów) zamiast `java.sql.Timestamp`. `BoundaryRule` skanuje pliki `.cs` i dyrektywy `using` (także `using static`, `global using` i aliasy; instrukcje `using var`/`using (...)` pomija), a zakazane fragmenty to `"System.Data."` i `".Adapter"`. `CohesionProbe` rozpoznaje styl C# tego repozytorium (pola `private [readonly] Typ _pole;`, metody wcięte o 4 spacje), więc grupy metod są w PascalCase. Wynik porównywany w `S10EquivalenceTest` ma format rekordu C#: `ScreeningRow { Table = screenings, Title = Amator, Start = 2026-10-02 20:00:00 } | True`.

### Co widzimy

Dwa narzędzia bez bibliotek w przestrzeni nazw sceny: `BoundaryRule` (skanuje pliki `.cs` w katalogu i zgłasza zakazane dyrektywy `using`) oraz `CohesionProbe` (LCOM4: liczba grup metod połączonych wspólnym polem lub wywołaniem). Klasa domeny `ScreeningService` miesza politykę cenową z mapowaniem na wiersz bazy - używa adaptera i `System.Data.SqlTypes`.

```csharp
using System.Data.SqlTypes;
using Training.Workshop.M2.S10BoundaryCheck.Start.Adapter;

namespace Training.Workshop.M2.S10BoundaryCheck.Start.Domain;

public sealed class ScreeningService
{
    private readonly decimal _basePrice;
    private readonly decimal _morningDiscount;
    private readonly string _table;
```

Uruchom `scripts/warsztat.sh --lang cs test m2/s10` (skrypt wypisuje wyjście testów, które xUnit zbiera przez `ITestOutputHelper`) albo test z Rider (okno Unit Tests) i pokaż wyjście `S10ArchitectureTest`: `s10 start - naruszenia granicy: [ScreeningService.cs: System.Data.SqlTypes, ScreeningService.cs: Training.Workshop.M2.S10BoundaryCheck.Start.Adapter]` i `s10 start - LCOM4 ScreeningService: Result { Lcom4 = 2, Groups = [FromRow, ToRow; IsMorning, Price] }`. Test startu działa jak "zamrożone naruszenia" (FreezingArchRule w ArchUnit, podobny mechanizm ma ArchUnitNET): znane są tolerowane, każde nowe jest czerwone.

### Krok 1: Extract Class - spójność

**W IDE:** ⌃T > Extract Class na `ToRow`, `FromRow` i polu `_table`, klasa `ScreeningRowMapper` w tym samym folderze `Domain`. Popraw `CinemaApp` (composition root), żeby tworzył obie klasy.
**Po:**

```csharp
public sealed class ScreeningRowMapper    // wciąż w Domain
{
    private readonly string _table;
    public ScreeningRow ToRow(Screening screening) { ... }
    public Screening FromRow(ScreeningRow row) { ... }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m2/s10` - 13 testów zielonych; LCOM4 obu klas = 1 (`Step1ClassesAreCohesive`), ale `Step1StillViolatesTheBoundaryButOnlyInTheMapper` pokazuje, że naruszenie granicy tylko się przeniosło.
**Co powiedzieć:** spójność i kierunek zależności to dwa różne wymiary. Wydzielenie klasy nie naprawia granicy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s10 0 1`

### Krok 2: Move to Namespace - maper do adaptera

**W IDE:** F6 na `ScreeningRowMapper`, folder `Adapter` (z aktualizacją przestrzeni nazw). Rider doda `using ...Domain;` w maperze i poprawi `CinemaApp`.
**Po:**

```csharp
using System.Data.SqlTypes;
using Training.Workshop.M2.S10BoundaryCheck.Step2.Domain;

namespace Training.Workshop.M2.S10BoundaryCheck.Step2.Adapter;
```

**Uruchom:** test zielony; `Step2DomainIsFreeOfTechnology` - lista naruszeń pusta.
**Co powiedzieć:** zależność biegnie teraz `Adapter -> Domain`. Reguła jest sprawdzana automatycznie przy każdym buildzie, a nie tylko na code review.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m2/s10 1 2`

### Rozwiązanie i uzasadnienie

Domena bez `System.Data` i bez typów adaptera, każda klasa opisuje jedno pojęcie. `S10ToolsTest` sprawdza same narzędzia na stałych próbkach kodu (w tym `using static` do adaptera).

### Pułapki

- `BoundaryRule` widzi tylko dyrektywy `using` - pełna nazwa typu w kodzie (albo `global using` w innym pliku) przejdzie. Silniejsze bramki: NetArchTest / ArchUnitNET, osobne projekty (csproj) z kontrolowanymi referencjami (domena bez referencji do projektu adaptera - wtedy pilnuje kompilator), analizatory Roslyn, reguły w CI. Porównajcie ich siłę i koszt.
- LCOM4 to heurystyka na źródle (pola `private`, typy bez spacji, wcięcie 4 spacje) - sygnał do rozmowy, nie wyrocznia. Mała klasa nie musi być spójna, duża może być nierozdzielnym pojęciem.
- Wydzielenie klasy dodaje współpracownika i kontrakt - porównuj koszt zmiany przed i po.

### Pytanie do sali

Która reguła architektury w waszym projekcie istnieje tylko w głowach lub na wiki? Jak najtaniej zamienić ją w test?

## Proponowana kolejność pokazu

**Ścieżka krótka (~55 min):** s03 (bramka kompilatora, 10) → s01 (YAGNI, 12) → s08 (odwrócenie zależności, 12) → s05 (zasada harcerza, 8) → s06 (seria zmian, 10). Pokazuje pełny łuk prewencji: najszybsza pętla, odłożona decyzja, kierunek zależności, mała poprawa i zestaw zmian gotowy do przeglądu. Jeśli zostanie czas, z s09 pokaż tylko `scripts/warsztat.sh --lang cs diff m2/s09 3 4` i `S09UseCaseTest` (5 min).

**Ścieżka pełna (~115 min, z przerwą):**

1. Szybka informacja zwrotna: s03, s04 (~22 min) - na koniec pytanie, które sprawdzenia uruchamiać lokalnie, a które w CI.
2. Decyzje: s01, s02 (~22 min) - pięć pytań ze slajdu 2.4 na przykładzie s01, potem decyzja trwała zapisana w ADR i pilnowana testem.
3. Projekt: s07, s08, s09, s10 (~54 min) - od jednego aktora zmiany, przez kierunek zależności, do granicy sprawdzanej automatycznie.
4. Codzienna dbałość o kod: s05, s06 (~18 min) jako podsumowanie - mała poprawa w dotykanym kodzie i seria zmian z osobnymi dowodami.

Sceny s09 i s10 dobrze łączyć: po s09 zapytaj, jak upewnić się, że `App` nigdy nie użyje `Adapter` (w jednym projekcie kompilator tego nie pilnuje), i przejdź do s10. Podobnie s05 i s06: po s05 zapytaj, jak uczciwie wprowadzić posortowane miejsca, i pokaż, jak s06 oddziela refaktoryzację od zmiany zachowania.
