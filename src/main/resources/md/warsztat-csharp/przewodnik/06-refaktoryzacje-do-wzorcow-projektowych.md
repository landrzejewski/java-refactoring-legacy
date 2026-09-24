# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat CineLegacy (C#): przewodnik prowadzącego

Dwadzieścia małych scen w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo od wzorca) ze slajdów modułu 6: od Strategy i State, przez Factory, Builder, Decorator i Observer, po rodzinę Composite, Visitor i mapę decyzji. Każda scena ma kod wyjściowy `Start` z zapachem lub pułapką i gotowe snapshoty `StepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera), osobny test to dokumentuje.

Port C# żyje w `csharp/src/Training.Workshop/M6/SNN...` (testy w `csharp/tests/Training.Workshop.Tests/M6/...`), pracujemy w JetBrains Rider (keymap macOS zgodny z IntelliJ). Projekt ma `TreatWarningsAsErrors`, więc każde ostrzeżenie kompilatora (np. niewyczerpujący `switch`, wywołanie metody `[Obsolete]`) zatrzymuje build - kilka scen korzysta z tego świadomie.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m6              # sceny i kroki modułu 6
scripts/warsztat.sh --lang cs test m6/s08          # testy jednej sceny
scripts/warsztat.sh --lang cs test m6              # wszystkie sceny modułu (473 testy)
scripts/warsztat.sh --lang cs diff m6/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = Start)
scripts/warsztat.sh --lang cs diff m6/s08 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m6/s08 2        # kopiuje Step2 do Start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh --lang cs next m6/s08          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m6/s08         # przywraca Start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w IDE uruchamiamy test sceny (z gutter przy klasie testu, z okna Unit Tests albo skryptem); zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m6/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujemy na namespace `Start` sceny (np. `Training.Workshop.M6.S08State.Start`). Gdy coś pójdzie nie tak, `next` (albo `jump` do właściwego kroku), a po scenie `reset`.
- Test równoważności przechodzi przez `Start` i wszystkie kroki (xUnit `[Theory]` z wariantami `start`, `step1`...). Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku kompiluje się i przechodzi. Testy dokumentujące pułapkę samego `Start` (s11, s17) po `jump` kończą się bez sprawdzeń i są zielone - xUnit 2 nie ma odpowiednika `assumeTrue` z JUnit, więc test sam wykrywa refleksją, że `Start` nie ma już pułapki, i wychodzi wcześniej. To zamierzone.
- Motyw przewodni modułu: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | Strategy - przed i po; intencja, sekwencja, ryzyka | 3 | `M6.S01Strategy` | ~12 min |
| s02 | Polimorfizm - przed i po; kiedy i pułapki | 3 | `M6.S02Polymorphism` | ~12 min |
| s03 | Replace Type Code with Class; granica trwałości | 3 | `M6.S03TypeCode` | ~12 min |
| s04 | Factory - Encapsulate Classes with Factory | 3 | `M6.S04EncapsulateFactory` | ~10 min |
| s05 | Factory - Extract Factory Class | 3 | `M6.S05ExtractFactory` | ~10 min |
| s06 | Encapsulate Composite with Builder | 3 | `M6.S06Builder` | ~12 min |
| s07 | Move Embellishment to Decorator; przezroczystość | 3 | `M6.S07Decorator` | ~12 min |
| s08 | Replace State-Altering Conditionals with State | 3 | `M6.S08State` | ~15 min |
| s09 | Replace Hard-coded Notifications with Observer | 3 | `M6.S09Observer` | ~15 min |
| s10 | Replace Implicit Tree with Composite; mapper | 3 | `M6.S10ImplicitTree` | ~15 min |
| s11 | Safe vs Transparent Composite | 2 | `M6.S11SafeComposite` | ~8 min |
| s12 | Replace One/Many Distinctions with Composite | 3 | `M6.S12OneMany` | ~10 min |
| s13 | Extract Composite | 2 | `M6.S13ExtractComposite` | ~8 min |
| s14 | Unify Interfaces with Adapter | 3 | `M6.S14Adapter` | ~12 min |
| s15 | Replace Conditional Dispatcher with Command | 3 | `M6.S15Command` | ~12 min |
| s16 | Form Template Method | 2 | `M6.S16TemplateMethod` | ~8 min |
| s17 | Limit Instantiation with Singleton | 3 | `M6.S17Singleton` | ~10 min |
| s18 | Move Accumulation to Collecting Parameter | 3 | `M6.S18CollectingParameter` | ~8 min |
| s19 | Visitor i macierz zmian | 3 | `M6.S19Visitor` | ~12 min |
| s20 | Mapa decyzji, "najpierw rodzaj zmienności" | 3 | `M6.S20DecisionMap` | ~12 min |

Pełne namespace mają prefiks `Training.Workshop.` (np. `Training.Workshop.M6.S01Strategy`). Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów) są przykładowe i opisane w komentarzach XML (`/// <summary>`) sceny.

Konwencje portu, które wracają w wielu scenach: interfejsy mają prefiks `I` (`IDiscountPolicy`, `IPricedTicket`, `IPaymentListener`...), gettery z Javy są właściwościami (`Name`, `Price`, `Minutes`), wyjątki to `ArgumentException` (Java `IllegalArgumentException`), `InvalidOperationException` (`IllegalStateException`) i `NotSupportedException` (`UnsupportedOperationException`), a parametr `base` nazywa się `basePrice`, bo `base` to słowo kluczowe C#. Tam, gdzie Java przekazuje lambdę jako implementację interfejsu, C# potrzebuje klasy (lambda pasuje tylko do typu delegata) - w porcie są to małe prywatne klasy.

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** Strategy - przed i po; Strategy - intencja, sekwencja, ryzyka; Istotne mechanizmy Javy 25
**Namespace:** `Training.Workshop.M6.S01Strategy` · **Test:** `scripts/warsztat.sh --lang cs test m6/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `IDiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (mała klasa) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

**Różnica względem Javy:** w Javie strategia przejściowa z kroku 1 to lambda `(b, type) -> legacyDiscount(b, type, program)`. Interfejs C# nie przyjmuje lambdy, więc strategią przejściową jest prywatna klasa zagnieżdżona `LegacyPolicy(program)` (primary constructor). Z tego samego powodu nowy program w `S01SolutionTest` to prywatna klasa `BlackFridayDiscount`, a nie lambda. Ruch i wniosek ("kontekst bez zmian") są te same.

### Co widzimy

`TicketPricer.Price(basePrice, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```csharp
if (program == "PREMIERE")
{
    discount = Money.Zero;
}
else if (program == "STUDENT_WEEK" && ticketType == "S")
{
    discount = basePrice.Percent(50);
}
else if (program == "STANDARD" || program == "STUDENT_WEEK")
{
    var percent = ticketType switch { "N" => 0, "S" => 25, ... };
    discount = basePrice.Percent(percent);
}
else
{
    throw new ArgumentException("unknown program: " + program);
}
```

Klientem jest `PriceBoard.PriceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - Start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**W IDE:** utwórz interfejs `IDiscountPolicy` z metodą `Money Discount(Money basePrice, string ticketType)`. Zaznacz łańcuch `if`, ⌥⌘M, nazwa `LegacyDiscount` (parametry `basePrice`, `ticketType`, `program`, zwracane `Money`). Dopisz prywatną klasę zagnieżdżoną `LegacyPolicy(string program) : IDiscountPolicy`, która deleguje do `LegacyDiscount`, i wywołaj ją w `Price`.
**Po:**

```csharp
IDiscountPolicy policy = new LegacyPolicy(program);
return basePrice.Minus(policy.Discount(basePrice, ticketType));
...
private sealed class LegacyPolicy(string program) : IDiscountPolicy
{
    public Money Discount(Money basePrice, string ticketType) => LegacyDiscount(basePrice, ticketType, program);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**W IDE:** dla każdej gałęzi osobno: nowa klasa implementująca `IDiscountPolicy` (⌥⏎ na nazwie interfejsu w deklaracji klasy - Implement missing members), ciało skopiowane z gałęzi, test. Kolejno `StandardDiscount`, `PremiereDiscount`, `StudentWeekDiscount` (ta ostatnia deleguje nie-studentów do `StandardDiscount`). Na koniec `LegacyDiscount` zastąp metodą `PolicyFor(program)` ze switch expression.
**Po:**

```csharp
return program switch
{
    "STANDARD" => new StandardDiscount(),
    "STUDENT_WEEK" => new StudentWeekDiscount(new StandardDiscount()),
    "PREMIERE" => new PremiereDiscount(),
    _ => throw new ArgumentException("unknown program: " + program),
};
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**W IDE:** przenieś `PolicyFor` (Move F6) do nowej statycznej klasy `DiscountPrograms` jako `ForName`, instancje strategii jako pola `static readonly` (współdzielone). Change Signature (⌘F6) na `TicketPricer`: strategia w konstruktorze, `Price(basePrice, ticketType)` bez programu. Popraw klienta `PriceBoard`.
**Po:**

```csharp
return new TicketPricer(DiscountPrograms.ForName(request.Program))
    .Price(request.Base, request.TicketType);
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek (`BlackFridayDiscount`) bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w Start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `ChoosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s01 2 3`

### Rozwiązanie i uzasadnienie

`Step3`: `TicketPricer` zna tylko `IDiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- `Func<Money, string, Money>` zamiast własnego interfejsu - działa, ale gubi nazwę pojęcia i nazwy parametrów w kontrakcie.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona między wątkami.
- Porównywanie strategii-delegatów przez `==` - lambda nie ma stabilnej tożsamości, każde wyrażenie może dać nowy obiekt delegata.
- Zamknięcie punktu rozszerzeń (np. klasa bazowa z konstruktorem `private protected` zamiast interfejsu) - punkt rozszerzeń ma być otwarty.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** Polimorfizm - przed i po; Polimorfizm - kiedy i pułapki
**Namespace:** `Training.Workshop.M6.S02Polymorphism` · **Test:** `scripts/warsztat.sh --lang cs test m6/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `_kind` i ten sam `switch` w trzech metodach, a pole `_value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta hierarchia rekordów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `FromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Hierarchia jest zamknięta w assembly, ale kompilator C# nie sprawdza wyczerpania `switch` po typach - listę rodzajów pilnuje test.

**Różnica względem Javy:** C# nie ma `sealed ... permits` ani `sealed interface`. W krokach 1-2 hierarchię zamyka konstruktor bazy `private protected` (podklasy tylko w tym assembly), a podklasy są `sealed` z konstruktorami `internal`. Krok 3 to `abstract record Screening` z konstruktorem `private protected` i trzy `sealed record`. Switch po typach bez ramienia `_` daje ostrzeżenie CS8509 (przy `TreatWarningsAsErrors` - błąd), więc w `S02SolutionTest.ClientSwitchIsExhaustiveWithoutDefault` klient ma ramię `_ => throw new UnreachableException(...)`, a sygnał "nowy rodzaj psuje kompilację" zastępuje asercja refleksyjna: nieabstrakcyjne podtypy `Screening` to dokładnie Marathon, Premiere, Regular. Z tego samego powodu (CS8524) switche po enumie `Kind` w Start i kroku 1 mają ramię `_ => throw new ArgumentOutOfRangeException(...)`.

### Co widzimy

`Screening` ma pole `_kind` (Regular, Premiere, Marathon) i ten sam `switch` w trzech metodach: `Label()`, `DurationMinutes()`, `Price()`. Pole `_value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```csharp
public int DurationMinutes()
{
    return _kind switch
    {
        Kind.Regular => 20 + _value,
        Kind.Premiere => 30 + _value,
        Kind.Marathon => _value * 120 + (_value - 1) * 15,
        _ => throw new ArgumentOutOfRangeException(nameof(_kind)),
    };
}
```

Tworzenie jest w jednym miejscu: `Screening.FromRow(ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji (Find Usages ⌥F7 na konstruktorze).

### Krok 1: Extract Subclass dla jednej gałęzi

**W IDE:** usuń `sealed` z `Screening`, konstruktor zmień na `private protected`, trzy metody oznacz `virtual`. Utwórz `MarathonScreening : Screening` z polem `_films`, nadpisz trzy metody (⌘N Generate > Overriding Members) i skopiuj do nich gałęzie `Kind.Marathon`. W `FromRow` twórz podklasę dla "MARATHON". W bazie gałęzie `Kind.Marathon` rzucają `InvalidOperationException`.
**Po:**

```csharp
"MARATHON" => new MarathonScreening(row.Title, row.Value),
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**W IDE:** powtórz ruch dla Regular i Premiere. Potem metody bazy jako `abstract`, usuń pole `_kind` i enum `Kind` (Safe Delete ⌘⌦). Jedynym `switch` zostaje `FromRow`.
**Po:**

```csharp
public abstract class Screening
{
    private protected Screening(string title) { ... }
    public abstract int DurationMinutes();
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s02 1 2`

### Krok 3: forma C# - abstrakcyjny rekord i zapieczętowane rekordy

**W IDE:** zamień klasę bazową na `abstract record Screening` z konstruktorem `private protected`, statycznym `FromRow` i abstrakcyjną właściwością `Title`, a podklasy na `sealed record` (ręcznie - rekord pozycyjny w miejscu klasy z polami). `_value` znika - każdy rekord ma nazwane dane (`Runtime`, `Films`).
**Po:**

```csharp
public sealed record MarathonScreening(string Title, int Films) : Screening
{
    public override string Title { get; } = Title;
    public override int DurationMinutes() { return Films * 120 + (Films - 1) * 15; }
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch` klienta z ramieniem `_ => throw new UnreachableException(...)` i asercję listy podtypów.
**Co powiedzieć:** zamknięty zestaw rodzajów daje w Javie kontrolę kompilatora, a w C# tylko częściową: konstruktor `private protected` nie wpuści podtypów spoza assembly, ale nowy rodzaj w tym assembly nie zepsuje kompilacji żadnego `switch` - wyjdzie jako `UnreachableException` w runtime albo jako czerwony test listy rodzajów. Koszt jest więc inny niż w Javie: mniej pilnowania przez kompilator, za to nowy rodzaj nie psuje kodu poza modułem.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy rekordy z własnymi danymi i zachowaniem. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, deserializator JSON, fabryka w innym projekcie) - obiekt powstanie jako zły podtyp albo wcale.
- Wywołanie metody wirtualnej z konstruktora bazy - podklasa widzi pola, których jej konstruktor jeszcze nie ustawił.
- Składowe `static` nie są polimorficzne - "nadpisanie" stałej w podklasie (`new`) nic nie daje przy wywołaniu przez typ bazowy.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** Replace Type Code with Class; Type Code - decyzje i granica trwałości
**Namespace:** `Training.Workshop.M6.S03TypeCode` · **Test:** `scripts/warsztat.sh --lang cs test m6/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `int` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na enum `Format` tuż po odczycie, przenosimy do jego rozszerzeń etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Enum wystarcza dla małego, zamkniętego zestawu, klasa jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy `(int)format` ani `ToString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

**Różnica względem Javy:** enum C# nie ma pól ani metod. `Format` to zwykły `enum Format { TwoD, ThreeD, Imax }`, a operacje typu są w bloku członków rozszerzeń C# 14 (`extension(Format format)` w statycznej klasie `FormatExtensions`): w kroku 1 `format.Code` i statyczne `Format.FromCode(int)`, w kroku 2 właściwości `Label`, `BasePrice`, `RequiresGlasses` (jeden `switch` na operację zamiast danych w konstruktorach stałych), w kroku 3 mapowanie w `FormatCodes` na `Dictionary<Format, int>` zamiast `EnumMap`. Odpowiednikiem pułapki `ordinal()` jest `(int)format`. Przypadek "kod nie jest liczbą": `int.Parse` rzuca `FormatException`, które nie dziedziczy po `ArgumentException`; oczekiwany tekst to komunikat .NET `ERROR: The input string 'x' was not in a correct format.`.

### Co widzimy

Format seansu jest surowym `int` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `NeedsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać `int` - to jest **granica trwałości**.

```csharp
private static Money BasePrice(int formatCode)
{
    return formatCode switch
    {
        Format2D => Money.Of("25.00"),
        ...
        _ => throw new ArgumentException("unknown format code: " + formatCode),
    };
}
```

### Krok 1: nowy typ i konwersja na granicy

**W IDE:** utwórz `enum Format { TwoD, ThreeD, Imax }` i obok statyczną klasę `FormatExtensions` z blokiem `extension(Format format)`: właściwość `Code` (jawne przypisanie 1, 2, 3) i statyczne `FromCode(int)` rzucające ten sam wyjątek co Start. W `Describe` zamień `int` na `Format` zaraz po odczycie; Change Signature (⌘F6) metod prywatnych na `Format`. Do CSV zapisuj `format.Code`.
**Po:**

```csharp
var format = Format.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** enum wystarcza, bo zestaw jest mały i zamknięty. Trwały kod to jawne przypisanie, nigdy `(int)format` ani `ToString()`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s03 0 1`

### Krok 2: Move Method do typu

**W IDE:** przenieś `Label`, `BasePrice`, `NeedsGlasses` do bloku rozszerzeń jako właściwości `Label`, `BasePrice`, `RequiresGlasses` (ręcznie - wytnij `switch` z `ScreeningCsv` i wklej jako ciało właściwości). Klient pyta obiekt zamiast wykonywać `switch`.
**Po:**

```csharp
extension(Format format)
{
    public string Label => format switch
    {
        Format.TwoD => "2D",
        Format.ThreeD => "3D",
        Format.Imax => "IMAX",
        _ => throw new ArgumentOutOfRangeException(nameof(format)),
    };
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas. W C# "typ z operacjami" to enum plus członkowie rozszerzeń - wywołanie `format.Label` wygląda jak właściwość typu.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**W IDE:** utwórz statyczną klasę `FormatCodes` z `FromCode`/`ToCode` (słownik `Dictionary<Format, int>` jako `IReadOnlyDictionary`), usuń `Code` i `FromCode` z rozszerzeń. `ScreeningCsv` rozmawia z mapperem, reszta kodu wyłącznie z `Format`.
**Po:**

```csharp
var format = FormatCodes.FromCode(int.Parse(parts[1].Trim(), CultureInfo.InvariantCulture));
... + "|csv=" + title + ";" + FormatCodes.ToCode(format);
```

**Uruchom:** test zielony; `S03SolutionTest.PersistentCodeIsNotTheOrdinal` sprawdza, że `(int)Format.Imax` to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Enum, bo zestaw jest zamknięty. Klasę z prywatnym konstruktorem i instancjami statycznymi (jak `DeploymentZone` ze slajdów, a w tym porcie `Format` ze sceny s20) wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis `(int)format` do bazy - dodanie wartości w środku enuma przestawia dane.
- Zapis `ToString()` (nazwy stałej) - ktoś ją "upiększy" przy Rename i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście klasę zamiast enuma dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** Factory - publiczna granica tworzenia; Factory - intencja, procedura, pułapki
**Namespace:** `Training.Workshop.M6.S04EncapsulateFactory` · **Test:** `scripts/warsztat.sh --lang cs test m6/s04`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu chowamy klasy konkretne w fabryce jako typy prywatne.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `ITicket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (refleksja, DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `[Obsolete]`.

**Różnica względem Javy:** podpakiet `tickets` to namespace `Ticketing` (namespace `...Tickets` kolidowałby z klasą fabryki `Tickets`). W kroku 3 Java odbiera klasom `public` i zostają package-private. W C# `internal` nie ukryłby ich przed `BoxOffice` (to samo assembly), więc klasy konkretne stają się prywatnymi rekordami zagnieżdżonymi w `public static partial class Tickets` - każdy nadal we własnym pliku (`StandardTicket.cs`, `VipTicket.cs`). `S04SolutionTest` sprawdza refleksją `IsNestedPrivate` dla biletów i `IsPublic` dla `Tickets` i `ITicket`.

### Co widzimy

`BoxOffice` (inny namespace niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są publiczne, choć klient potrzebuje tylko interfejsu `ITicket`.

```csharp
tickets.Add(row >= 10
    ? new VipTicket(title, basePrice, row)
    : new StandardTicket(title, basePrice, row));
```

### Krok 1: metody tworzące

**W IDE:** w namespace `Ticketing` utwórz statyczną klasę `Tickets` ze statycznymi `Standard(...)` i `Vip(...)` zwracającymi `ITicket`. Zastąp każde `new` w `BoxOffice` wywołaniem metody tworzącej - Find Usages (⌥F7) na konstruktorach pokaże wszystkie miejsca, a zamianę robimy ręcznie (to dwa miejsca).
**Po:**

```csharp
return Tickets.Vip(title, basePrice, row);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie używa już klas konkretnych.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s04 0 1`

### Krok 2: decyzja do fabryki

**W IDE:** przenieś warunek `row >= 10` do `Tickets.ForSeat(title, basePrice, row)`, stałą nazwij `VipFromRow` (Introduce Field jako `const` albo ręcznie). `SellAll` woła `Sell`.
**Po:**

```csharp
public ITicket Sell(string title, Money basePrice, int row)
{
    return Tickets.ForSeat(title, basePrice, row);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s04 1 2`

### Krok 3: ograniczenie widoczności

**W IDE:** oznacz `Tickets` jako `public static partial class`, a w plikach `StandardTicket.cs` i `VipTicket.cs` owiń bilety tą samą klasą częściową i zamień je na `private sealed record`. Rider podkreśli każde użycie spoza fabryki - to inwentaryzacja, którą robi za nas kompilator.
**Po:**

```csharp
public static partial class Tickets
{
    private sealed record VipTicket(string Title, Money Base, int Row) : ITicket { ... }
}
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza widoczność refleksją.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `[Obsolete]`. W C# `internal` to granica assembly, nie namespace - dlatego ukrywamy typy w samej fabryce.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `ITicket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: refleksja, kontener DI, deserializacja JSON wymagająca publicznego konstruktora.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez ORM (np. Entity Framework)?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** Factory - intencja, procedura, pułapki (Extract Factory Class)
**Namespace:** `Training.Workshop.M6.S05ExtractFactory` · **Test:** `scripts/warsztat.sh --lang cs test m6/s05`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

**Różnica względem Javy:** `Clock` to w porcie `TimeProvider`, czas lokalny `_clock.GetLocalNow().DateTime` (w testach `FakeTimeProvider` w strefie UTC). W oczekiwanym wyniku scenariusza nazwy wyjątków są z .NET: `InvalidOperationException: seat taken: A1` i `ArgumentException: unknown channel: ...` zamiast `IllegalStateException`/`IllegalArgumentException`.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `Reserve` i `ReserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```csharp
_sequence++;
var id = "R" + _sequence;
if (channel != "ONLINE" && channel != "BOX_OFFICE")
{
    throw new ArgumentException("unknown channel: " + channel);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**W IDE:** w `Reserve` zaznacz blok od `_sequence++` do `new Reservation(...)`, ⌥⌘M, nazwa `NewReservation`. W `ReserveGroup` zastąp duplikat wywołaniem `NewReservation("ONLINE", email, seats)`. Pętlę zajętości wyciągnij do `RequireFree`.
**Po:**

```csharp
RequireFree(seats);
var reservation = NewReservation(channel, email, seats);
_takenSeats.UnionWith(seats);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**W IDE:** Refactor This (⌃T) > Extract Class dla `NewReservation` razem z polami `_clock` i `_sequence` (albo Move F6 do nowej klasy); w fabryce metoda nazywa się `Create`. Serwis tworzy fabrykę w swoim konstruktorze - sygnatura konstruktora bez zmian.
**Po:**

```csharp
public ReservationService(TimeProvider clock)
{
    _factory = new ReservationFactory(clock);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s05 1 2`

### Krok 3: fabryka jako zależność

**W IDE:** dodaj konstruktor `ReservationService(ReservationFactory factory)`; dotychczasowy `ReservationService(TimeProvider)` zamień na delegację `: this(new ReservationFactory(clock))`, żeby nie ruszać klientów.
**Po:**

```csharp
public ReservationService(TimeProvider clock)
    : this(new ReservationFactory(clock))
{
}

public ReservationService(ReservationFactory factory) { ... }
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Zegar pobierany przez `DateTime.Now` bez `TimeProvider` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** Encapsulate Composite with Builder
**Namespace:** `Training.Workshop.M6.S06Builder` · **Test:** `scripts/warsztat.sh --lang cs test m6/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `Add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi rekordami i jednorazowym `Build()`, na końcu z zagnieżdżoną lambdą dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a lambda gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - Start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

**Różnica względem Javy:** `LocalDate`/`LocalTime` to `DateOnly`/`TimeOnly`, a dzień tygodnia w wydruku to nazwa enuma .NET (`2026-10-03 Saturday` zamiast `SATURDAY`). W kroku 2 `LinkedHashMap` to `OrderedDictionary<string, List<Screening>>` (.NET 9+), a niemutowalność rekordów daje kopia `ToList().AsReadOnly()` w inicjatorze właściwości (`S06SolutionTest.BuiltTreeIsImmutable` rzutuje listy na `ICollection<T>` i oczekuje `NotSupportedException` przy `Clear()`). W kroku 3 `Consumer<HallBuilder>` to `Action<HallBuilder>`, a konstruktor `HallBuilder` jest `internal`, bo klasa zewnętrzna C# nie widzi prywatnych składowych klasy zagnieżdżonej.

### Co widzimy

`WeekendPlanner.Plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `Add`, a o `day.Add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```csharp
var hall2 = new Hall("Sala 2");
if (weekend)
{
    hall2.Add(new Screening("Kraina Lodu", new TimeOnly(10, 0)));
}
hall2.Add(new Screening("Amator", new TimeOnly(17, 30)));
day.Add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**W IDE:** utwórz `ScheduleBuilder` z metodami `Hall(name)`, `Screening(title, hour, minute)` i `Build()`; builder pamięta bieżącą salę. Przepisz planera na wywołania buildera. Drzewo bez zmian.
**Po:**

```csharp
var builder = new ScheduleBuilder(date)
    .Hall("Sala 1")
    .Screening("Diuna", 18, 0)
    .Screening("Diuna", 21, 0)
    .Hall("Sala 2");
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**W IDE:** węzły zamień na rekordy z kopią listy w inicjatorze właściwości (usuń `Add`). Builder zbiera dane w `OrderedDictionary` i tworzy rekordy w `Build()`; drugie `Build()` rzuca `InvalidOperationException`.
**Po:**

```csharp
public sealed record Hall(string Name, IReadOnlyList<Screening> Screenings)
{
    public IReadOnlyList<Screening> Screenings { get; } = Screenings.ToList().AsReadOnly();
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. Unikalność nazw sal to **nowy niezmiennik** - Start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s06 1 2`

### Krok 3: zagnieżdżony builder z lambdą

**W IDE:** `Hall(name, Action<HallBuilder>)` zamiast "bieżącej sali"; zagnieżdżona klasa `HallBuilder` z `Screening(...)`, statyczne wejście `ScheduleBuilder.Day(date)`. Przepisz planera.
**Po:**

```csharp
return ScheduleBuilder.Day(date)
    .Hall("Sala 1", hall => hall
        .Screening("Diuna", 18, 0)
        .Screening("Diuna", 21, 0))
    .Hall("Sala 2", hall => { ... })
    .Build();
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt lambdy: synchroniczna, wywołana dokładnie raz.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `Build()` zwraca wewnętrzną listę - kolejne wywołania zmieniają już zwrócone drzewo.
- Lambda zapamiętana i wywołana później (odroczenie) - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** Move Embellishment to Decorator; Decorator - wyjątki, migracja, przezroczystość
**Namespace:** `Training.Workshop.M6.S07Decorator` · **Test:** `scripts/warsztat.sh --lang cs test m6/s07`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `_vip`, `_glasses`, `_insurance` i te same `if` w `Price()` i `Description()`. Wydzielamy interfejs `IPricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `is`, `GetType()`, tożsamości i `Equals`.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `is VipSeat`.

**Różnica względem Javy:** merytorycznie brak. Interfejs to `IPricedTicket`, dekoratory i rdzeń z kroku 3 to `sealed record`, `instanceof` to `is` (nazwa testu `InstanceofSeesOnlyTheOutermostDecorator` zachowana z Javy).

### Co widzimy

`Ticket` ma flagi `_vip`, `_glasses`, `_insurance` i te same `if` w `Price()` i `Description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```csharp
return _title + " " + _format
    + (_vip ? " +VIP" : "")
    + (_glasses ? " +okulary 3D" : "")
    + (_insurance ? " +ubezpieczenie" : "");
```

### Krok 1: Extract Interface

**W IDE:** Refactor This (⌃T) > Extract Interface na `Ticket` - `IPricedTicket` z `Price()` i `Description()`. `TicketAssembler.Assemble` zwraca interfejs.
**Po:**

```csharp
public IPricedTicket Assemble(TicketOrder order)
{
    return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**W IDE:** rekord `Insurance(IPricedTicket Inner) : IPricedTicket` dodający 4.00 i " +ubezpieczenie". Usuń flagę z `Ticket` (Safe Delete ⌘⌦), w assemblerze owiń, gdy `order.Insurance`.
**Po:**

```csharp
IPricedTicket ticket = new Ticket(order);
if (order.Insurance)
{
    ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**W IDE:** `VipSeat` i `Glasses3D` jako dekoratory, rdzeń jako rekord `Ticket(Title, Format, Base)`. Decyzja "czy okulary potrzebne" (3D i brak własnych) zostaje w assemblerze.
**Po:**

```csharp
if (order.Vip) { ticket = new VipSeat(ticket); }
if (order.Format == "3D" && !order.OwnGlasses) { ticket = new Glasses3D(ticket); }
if (order.Insurance) { ticket = new Insurance(ticket); }
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket is VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. `Equals` rdzenia i udekorowanego biletu też się różni (rekordy porównują typ i pola). Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `Features`).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Ślepe delegowanie `Equals` do `Inner` - łamie symetrię (`core.Equals(vip)` różne od `vip.Equals(core)`).
- Kod sprawdzający `GetType()`, `is`, robiący `lock` na obiekcie albo serializujący go - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `is VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** Replace State-Altering Conditionals with State; State - kontekst delegujący
**Namespace:** `Training.Workshop.M6.S08State` · **Test:** `scripts/warsztat.sh --lang cs test m6/s08`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole `_status`, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `Pay`, `Use`, `Expire`, `Cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w Start, więc awaria bramki nadal zostawia rezerwację w stanie New.

**Różnica względem Javy:** Java używa prywatnego `sealed interface ReservationState` i stanów jako enumów (`NewState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). C# nie ma `sealed interface`, a enum nie ma metod, więc stan to prywatny interfejs zagnieżdżony `IReservationState`, a stany to prywatne klasy `sealed` z instancjami `static readonly` (`NewState.Instance`, `PaidState.Instance`, jedna klasa `ClosedState(Status)` z trzema instancjami `Used`, `Expired`, `Cancelled`). Domyślne metody Javy to domyślne implementacje metod interfejsu (C# 8+) - są widoczne tylko przez typ interfejsu, dlatego pole `_state` ma typ `IReservationState`. Statusy są w PascalCase, więc komunikaty brzmią np. `cannot pay in New`.

### Co widzimy

`Reservation` ma pole `_status` i w każdej operacji (`Pay`, `Use`, `Expire`, `Cancel`) warunki sprawdzające i zmieniające status. Reguły przejść New -> Paid -> Used, New -> Expired, New/Paid -> Cancelled są rozsiane po metodach. `Pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje New.

```csharp
public void Cancel()
{
    if (_status == Status.New)
    {
        _status = Status.Cancelled;
        _effects.Add("seats released");
    }
    else if (_status == Status.Paid)
    {
        _status = Status.Cancelled;
        _effects.Add("refund");
        _effects.Add("seats released");
    }
    else
    {
        throw new InvalidOperationException("cannot cancel in " + _status);
    }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**W IDE:** utwórz prywatny interfejs zagnieżdżony `IReservationState` z właściwością `Status` i stany jako prywatne klasy z instancjami `static readonly` (`NewState.Instance`, `PaidState.Instance`, `ClosedState.Used/Expired/Cancelled`). Pole `_status` zastąp polem `_state`; warunki porównują `_state.Status`. Efekty i zmianę stanu wyciągnij do `Charge()`, `MoveTo(...)`, `Record(...)` (⌥⌘M).
**Po:**

```csharp
private IReservationState _state = NewState.Instance;
...
Charge();
MoveTo(PaidState.Instance);
Record("charged");
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe to jedna klasa `ClosedState` z trzema instancjami - wszystkie zachowują się tak samo (każde przejście jest błędem).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s08 0 1`

### Krok 2: Pay i Use do stanów

**W IDE:** w interfejsie stanu dodaj domyślne implementacje `void Pay(Reservation reservation) => throw Invalid("pay");` (i `Use`) oraz prywatną metodę interfejsu `Invalid(action)`. W `NewState` zaimplementuj `Pay`, w `PaidState` - `Use`, przenosząc ciała gałęzi. Kontekst deleguje: `_state.Pay(this)`.
**Po:**

```csharp
public void Pay(Reservation reservation)
{
    reservation.Charge();
    reservation.MoveTo(PaidState.Instance);
    reservation.Record("charged");
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślna metoda **rzuca wyjątek**, nie jest pusta - pusta metoda po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w Start; test awarii bramki to pilnuje. Stany są zagnieżdżone w `Reservation`, więc widzą jej prywatne `Charge`, `MoveTo`, `Record` - interfejs publiczny kontekstu się nie zmienia.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s08 1 2`

### Krok 3: Expire i Cancel do stanów

**W IDE:** ten sam ruch dla `Expire` i `Cancel`. Warunek `if/else if` w `Cancel` rozkłada się na dwa stany: New (zwolnienie miejsc) i Paid (zwrot i zwolnienie).
**Po:**

```csharp
public void Cancel()
{
    _state.Cancel(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów. Stany są bezstanowymi instancjami współdzielonymi, dane zostają w `Reservation`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan implementuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co Start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- `volatile` na polu stanu nie daje atomowości przejścia przy współbieżnych `Pay` i `Cancel`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż hierarchia.
- Kilka niezależnych osi stanu (płatność, obecność) w jednej hierarchii - eksplozja klas.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** Replace Hard-coded Notifications with Observer; Observer - kontrakt i migracja
**Namespace:** `Training.Workshop.M6.S09Observer` · **Test:** `scripts/warsztat.sh --lang cs test m6/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.Confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `IPaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie zna `IMailer`, `ISmsGateway` ani `ILoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w Start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

**Różnica względem Javy:** w kroku 3 `Subscription extends AutoCloseable` to `ISubscription : IDisposable` (idempotentne `Dispose()` przez `Interlocked.CompareExchange`), a `CopyOnWriteArrayList` to pole `ImmutableList<Registration>` aktualizowane `ImmutableInterlocked.Update` - iteracja idzie po migawce, jak w Javie. Porty mają prefiks `I` (`IMailer`, `ISmsGateway`, `ILoyaltyProgram`). W testach lambdy Javy zastąpiły małe klasy: jedna klasa `Ports` implementuje trzy porty, a odbiorcy w `S09SolutionTest` to klasa `Listener(Action<ReservationPaid>)`. Parametr `event` nazywa się `paidEvent` (`event` to słowo kluczowe C#).

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.Standard(mailer, sms, loyalty)`. `PaymentService.Confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```csharp
_paid.Add(payment.ReservationId);
_mailer.Send(payment.Email, "Potwierdzenie platnosci " + ...);
_sms.Send(payment.Phone, "Oplacono " + payment.ReservationId);
_loyalty.AddPoints(payment.Email, (int)payment.Amount.Amount / 10);
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**W IDE:** utwórz rekord `ReservationPaid`. Zaznacz trzy powiadomienia, ⌥⌘M `NotifyPaid(paidEvent)`.
**Po:**

```csharp
_paid.Add(payment.ReservationId);
NotifyPaid(new ReservationPaid(
    payment.ReservationId, payment.Email, payment.Phone, payment.Amount));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**W IDE:** interfejs `IPaymentListener.OnPaid(paidEvent)` i trzy rekordy: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` (każdy z ciałem jednej linii z `NotifyPaid`). Serwis tworzy listę (wyrażenie kolekcji) w konstruktorze w starej kolejności; `NotifyPaid` to pętla **bez** `try/catch`.
**Po:**

```csharp
foreach (var listener in _listeners)
{
    listener.OnPaid(paidEvent);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s09 1 2`

### Krok 3: Subscribe i korzeń kompozycji

**W IDE:** `Subscribe(listener)` zwracające `ISubscription` (idempotentne `Dispose()`), pole `ImmutableList<Registration>` rejestracji. Konstruktor serwisu staje się bezargumentowy, a subskrypcje przenosimy do istniejącego `PaymentServices.Standard(...)`.
**Po:**

```csharp
var service = new PaymentService();
service.Subscribe(new MailConfirmation(mailer));
service.Subscribe(new SmsConfirmation(sms));
service.Subscribe(new LoyaltyPoints(loyalty));
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** `Registration` jest klasą, nie rekordem - rekord z tym samym odbiorcą byłby równy (`Equals`) i `Remove` usunąłby cudzą rejestrację. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `IPaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (`Task.Run`, kolejka) - inny wątek, inna transakcja, inna kolejność.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Subskrypcja bez `Dispose()` trzyma przy życiu graf obiektów (klasyczny wyciek przez zdarzenia w .NET).

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** Replace Implicit Tree with Composite; Implicit Tree - mapper, procedura, ryzyka
**Namespace:** `Training.Workshop.M6.S10ImplicitTree` · **Test:** `scripts/warsztat.sh --lang cs test m6/s10`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona lista, w której pierwszy element jest nazwą, a `BarMenu` powtarza testy `is` i rzutowania. Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `Price` i `Render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie ma testów `is` ani rzutowań, a komunikaty błędów zostają jak w Start. Format trwały (zagnieżdżone listy) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

**Różnica względem Javy:** stary format `List<?>` to `IReadOnlyList<object>` (w testach tablice `object[]` i wyrażenia kolekcji), `instanceof` i `switch` po typie to `is` i switch expression. `MenuItem` to interfejs `IMenuItem` z właściwościami `Name` i `Price` - rekord `Product(string Name, Money Price)` spełnia go wprost, a `Combo.Price` to właściwość licząca sumę. Interfejs nie jest zamknięty (C# nie ma `sealed interface`). `S10DifferentialTest` używa `new Random(42)` z .NET, które daje inne drzewa niż `java.util.Random(42)` - test jest różnicowy (Start kontra nowy kod), więc to bez znaczenia.

### Co widzimy

Zestaw combo to zagnieżdżona lista: pierwszy element to nazwa, kolejne to `"nazwa=cena"` albo podlisty. `BarMenu.Price` i `BarMenu.Render` powtarzają `is` i rzutowania, a `Render` dla każdego węzła liczy cenę od nowa.

```csharp
["Zestaw Duo", "Popcorn L=18.00",
    new object[] { "Napoje", "Cola 0.5=9.00", "Cola 0.5=9.00" }, "Nachos=14.00"]
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**W IDE:** utwórz interfejs `IMenuItem` z rekordami `Product` i `Combo` oraz statyczną klasę `MenuMapper.FromNested(IReadOnlyList<object>)`. `BarMenu` bez zmian. Uruchom test różnicowy - porównuje stary kod z drzewem z mappera.
**Po:**

```csharp
items.Add(element switch
{
    string product => Product(product),
    IReadOnlyList<object> nested => FromNested(nested),
    _ => throw new ArgumentException("unsupported element: " + element),
});
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s10` - zielone, w tym `CompositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s10 0 1`

### Krok 2: Price na Composite

**W IDE:** ciało `BarMenu.Price` zastąp `MenuMapper.FromNested(combo).Price`. Usuń nieużywane `RequireName` (Safe Delete ⌘⌦).
**Po:**

```csharp
public Money Price(IReadOnlyList<object> combo)
{
    return MenuMapper.FromNested(combo).Price;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `Render` wciąż jest stary i działa, bo woła nowe `Price`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s10 1 2`

### Krok 3: Render na Composite

**W IDE:** `Render` deleguje do `Combo.Render(0, text)`; stara rekurencja z `is` do usunięcia (Safe Delete).
**Po:**

```csharp
var text = new StringBuilder();
MenuMapper.FromNested(combo).Render(0, text);
return text.ToString();
```

**Uruchom:** test zielony, test różnicowy `FinalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone listy) zostaje. Jego zmiana na JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `StackOverflowException`, którego w .NET nie da się złapać (proces kończy działanie).
- Cykl w danych (lista zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** Replace One/Many Distinctions with Composite (Safe i Transparent Composite)
**Namespace:** `Training.Workshop.M6.S11SafeComposite` · **Test:** `scripts/warsztat.sh --lang cs test m6/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `Add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `Add` i `Children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z rekordów bez `Add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym typie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `Add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `Add` ani `Children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - rzutowanie `(Combo)` w kliencie oznaczałoby powrót do problemu.

**Różnica względem Javy:** krok 2 w Javie to `sealed interface MenuComponent` + rekordy. W C# `MenuComponent` jest `abstract record` z konstruktorem `private protected` (podtypy tylko w tym projekcie), a `Product` i `Combo` to `sealed record`; nazwa typu zostaje `MenuComponent` jak w Start. `S11SolutionTest` sprawdza refleksją brak `Add(MenuComponent)` na `Product` i brak właściwości `Children` na `MenuComponent`. Test pułapki Start (`TransparentCompositeFailsAtRuntime`) po `jump` kończy się bez sprawdzeń (zamiast `assumeTrue`).

### Co widzimy

Transparent Composite: `Add()` i `Children` są we wspólnej klasie abstrakcyjnej `MenuComponent`, liść `Product` dziedziczy `Add()`, które rzuca `NotSupportedException`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.Add(sauce)`.

```csharp
public virtual void Add(MenuComponent child)
{
    throw new NotSupportedException("cannot add to " + Name);
}
```

### Krok 1: Safe Composite - Push Members Down

**W IDE:** Refactor This (⌃T) > Push Members Down na `Add` i `Children` z `MenuComponent` do `Combo`. Rider pokaże konflikty w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci. `Describe()` staje się `virtual`, a `Combo` nadpisuje go i rozszerza opis o dzieci.
**Po:**

```csharp
var combo = new Combo("Zestaw Rodzinny");
combo.Add(new Product("Popcorn XL", "24.00"));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `Add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji. Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s11 0 1`

### Krok 2: niemutowalny Composite w C# - abstrakcyjny rekord

**W IDE:** `abstract record MenuComponent` z konstruktorem `private protected`, rekordy `Product` i `Combo(name, children)` z `Combo.Of(name, params MenuComponent[])`. Katalog zapisany deklaratywnie.
**Po:**

```csharp
"duo" => Combo.Of("Zestaw Duo",
    new Product("Popcorn L", "18.00"),
    new Product("Cola", "9.00"),
    new Product("Cola", "9.00")),
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `Add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika. Lista dzieci to kopia `ToList().AsReadOnly()`, więc nawet rzutowanie na `ICollection<T>` nie pozwoli jej zmienić.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `Add()` (bez wyjątku) - dziecko po cichu ginie.
- Rzutowanie `(Combo)component` w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** Replace One/Many Distinctions with Composite
**Namespace:** `Training.Workshop.M6.S12OneMany` · **Test:** `scripts/warsztat.sh --lang cs test m6/s12`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `Refund` i `RefundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `IRefundable` z liściem i grupą, a stare metody usuwamy po etapie `[Obsolete]`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `Refund(IRefundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

**Różnica względem Javy:** `sealed interface Refundable` to zwykły interfejs `IRefundable` (bez zamknięcia hierarchii). `@Deprecated` to `[Obsolete]` - przy `TreatWarningsAsErrors` każde wywołanie przestarzałej metody (ostrzeżenie CS0618) zatrzymuje build, dlatego w kroku 2 klient przechodzi na nowy kontrakt od razu, a test delegacji (`DeprecatedWrappersInStep2DelegateToTheNewContract`) wyłącza CS0618 przez `#pragma`. `Duration.toHours() >= 24` to `TimeSpan.TotalHours >= 24`; w `RefundAll` w Start jest rzutowanie na `long`, żeby zachować "trochę inny" zapis reguły.

### Co widzimy

`RefundService` ma `Refund(ticket, now)` i `RefundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.Count == 1)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```csharp
foreach (var ticket in tickets)
{
    if (now < ticket.ShowStart)
    {
        var hours = (long)(ticket.ShowStart - now).TotalHours;
        total = total.Plus(hours >= 24 ? ticket.Price : ticket.Price.Percent(50));
    }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**W IDE:** w `Refund` zaznacz wyliczenie kwoty, ⌥⌘M `TicketShare(ticket, now)`. W `RefundAll` zastąp ciało pętli wywołaniem `TicketShare`.
**Po:**

```csharp
total = total.Plus(TicketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**W IDE:** interfejs `IRefundable` z `RefundableAmount(now)`, liść `SingleTicket` (reguła z `TicketShare`), węzeł `TicketGroup` (suma, `TicketGroup.Of(tickets)`). Nowa metoda `Refund(IRefundable, now)`; stare metody jako delegacje z `[Obsolete]`. `CancellationDesk` przechodzi na nowy kontrakt (na razie wciąż wybiera `SingleTicket` albo `TicketGroup`).
**Po:**

```csharp
public Money Refund(IRefundable refundable, DateTime now)
{
    return refundable.RefundableAmount(now).Minus(Fee).Max(Money.Zero);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach. `[Obsolete]` przy ostrzeżeniach traktowanych jak błędy działa jak twarda bramka: nikt nowy nie wywoła starego API.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s12 1 2`

### Krok 3: usunięcie starych metod

**W IDE:** po migracji klientów Safe Delete (⌘⌦) na `Refund(TicketData, ...)` i `RefundAll`. W `CancellationDesk` usuń `if` - klient zawsze buduje `TicketGroup.Of(tickets)`, także z jednego biletu.
**Po:**

```csharp
return _service.Refund(TicketGroup.Of(tickets), now);
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `Refund(IRefundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `[Obsolete]`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w Start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** Extract Composite
**Namespace:** `Training.Workshop.M6.S13ExtractComposite` · **Test:** `scripts/warsztat.sh --lang cs test m6/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `Add`, kopię `Children`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `Add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `Minutes` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

**Różnica względem Javy:** metody `final` z Javy to w C# zwykłe metody niewirtualne (w C# metody są domyślnie niewirtualne, więc podklasa i tak nie może ich nadpisać). `Objects.requireNonNull` to `ArgumentNullException.ThrowIfNull` (test oczekuje `ArgumentNullException`). `Minutes` jest abstrakcyjną właściwością bazy, a interfejs to `IProgramItem`.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `Add` z kontrolą `null`, `Children` jako kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```csharp
return _children.Count == 0 ? 0 : total + 15 * (_children.Count - 1);
```

### Krok 1: Extract Superclass

**W IDE:** na `Marathon` Refactor This (⌃T) > Extract Superclass: `CompositeProgramItem`, zaznacz pole `_children`, `Add`, `Children`. Potem `ShortsBlock : CompositeProgramItem` i usuń jego kopie (Pull Members Up już nie trzeba - są w bazie).
**Po:**

```csharp
public abstract class CompositeProgramItem : IProgramItem
{
    private readonly List<IProgramItem> _children = [];
    public void Add(IProgramItem child) { ... }
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**W IDE:** Pull Members Up dla `ChildrenMinutes()` (`protected`, niewirtualna) i `Describe()` jako szablon z hookiem `protected abstract string Label()`. Nazwa kontenera trafia do konstruktora bazy. Podklasy zostają z regułą czasu i etykietą.
**Po:**

```csharp
public override int Minutes
{
    get
    {
        var count = Children.Count;
        return count == 0 ? 0 : ChildrenMinutes() + 15 * (count - 1);
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `Minutes` zostaje w podklasach.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `Minutes` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** Unify Interfaces with Adapter; Adapter - co naprawdę trzeba przetłumaczyć
**Namespace:** `Training.Workshop.M6.S14Adapter` · **Test:** `scripts/warsztat.sh --lang cs test m6/s14`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.Pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `IPaymentGateway`, a serwis wybiera bramkę ze słownika.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `IPaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w słowniku. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

**Różnica względem Javy:** komponent rekordu `PaymentResult.accepted()` kolidowałby w C# z fabryką `Accepted(...)`, więc właściwość nazywa się `IsAccepted` (fabryki `Accepted`/`Declined` bez zmian). Metody "bibliotek" `XmlPayGateway.Submit` i `RestPayClient.Charge` są `virtual`, bo `S14SolutionTest` nadpisuje `Submit` w podklasie nagrywającej (w Javie metody są domyślnie wirtualne). `Map.copyOf` to kopia `Dictionary` wystawiona jako `IReadOnlyDictionary`, a grosze liczy `decimal.ToInt64(amount.Amount * 100)`.

### Co widzimy

`CheckoutService.Pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w namespace sceny i ich nie zmieniamy.

```csharp
var grosze = decimal.ToInt64(amount.Amount * 100);
var request = "<charge ref='" + reservationId + "' amount='" + grosze + "'/>";
```

### Krok 1: Extract Method z jednolitą sygnaturą

**W IDE:** każdą gałąź ⌥⌘M: `PayWithXml(reservationId, amount)` i `PayWithRest(reservationId, amount)`, obie zwracają `PaymentResult`.
**Po:**

```csharp
if (provider == "XML")
{
    return PayWithXml(reservationId, amount);
}
else if (provider == "REST")
{
    return PayWithRest(reservationId, amount);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**W IDE:** interfejs `IPaymentGateway.Pay(reservationId, amount)`. Move (F6) `PayWithXml` do `XmlPayAdapter` i `PayWithRest` do `RestPayAdapter` (razem z pomocniczym `Attribute`). Konstruktor serwisu bez zmian - tworzy adaptery.
**Po:**

```csharp
public CheckoutService(XmlPayGateway xml, RestPayClient rest)
{
    _xml = new XmlPayAdapter(xml);
    _rest = new RestPayAdapter(rest);
}
```

**Uruchom:** test zielony; `S14SolutionTest.XmlAdapterSendsAmountInGrosze` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**W IDE:** nowy konstruktor `CheckoutService(IReadOnlyDictionary<string, IPaymentGateway> gateways)` (kopia do nowego `Dictionary`); stary konstruktor deleguje do niego (`: this(...)`) ze standardowym zestawem adapterów. `if` zastąp wyszukaniem w słowniku; nieznany dostawca - ten sam wyjątek.
**Po:**

```csharp
if (!_gateways.TryGetValue(provider, out var gateway))
{
    throw new ArgumentException("unknown provider: " + provider);
}
return gateway.Pay(reservationId, amount);
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `IPaymentGateway` i da się ją testować fake'iem (`CheckoutWorksWithAnyGateway`). Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w słowniku.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`IPaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia, strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`InnerException`) - trudna diagnoza w produkcji.
- Adapter wołający `Dispose()` na zasobie, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** Replace Conditional Dispatcher with Command; Command - sekwencja i ograniczenia
**Namespace:** `Training.Workshop.M6.S15Command` · **Test:** `scripts/warsztat.sh --lang cs test m6/s15`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.Handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy słownikiem komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Słownik jest równoważny warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w słowniku, bez zmiany `Handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

**Różnica względem Javy:** `@FunctionalInterface ConsoleCommand` to interfejs `IConsoleCommand` (nowa komenda HELLO w `S15SolutionTest` to prywatna klasa `HelloCommand` zamiast lambdy). `toUpperCase(Locale.ROOT)` to `ToUpperInvariant()`, `strip()` to `Trim()`, a `matches("\\d+")` to `Regex.IsMatch(..., "^[0-9]+$")` (tylko cyfry ASCII, jak w Javie). Metody `Till` package-private są `internal` (`Cash` i `Tickets` jako właściwości).

### Co widzimy

`CashierConsole.Handle(line)` to łańcuch `if (command == "SELL")` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `ToUpperInvariant()` - `sell` i `SELL` działają tak samo.

```csharp
if (command == "SELL")
{
    var sell = args.Split(' ', 2);
    ...
}
else if (command == "REFUND")
{
    ...
}
else if (command == "REPORT")
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**W IDE:** ciało każdej gałęzi ⌥⌘M: `Sell(args)`, `Refund(args)`, `Report(args)` - ta sama sygnatura, także gdy `args` nie jest używane.
**Po:**

```csharp
if (command == "SELL")
{
    return Sell(args);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**W IDE:** Extract Class `Till` (pola `_cash`, `_tickets` i cennik). Interfejs `IConsoleCommand.Execute(args, till)`, klasy `SellCommand`, `RefundCommand`, `ReportCommand` z ciałami metod. Dyspozytor `if` jeszcze zostaje.
**Po:**

```csharp
if (command == "SELL")
{
    return _sell.Execute(args, _till);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugi wątek.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s15 1 2`

### Krok 3: rejestr zamiast warunku

**W IDE:** `IReadOnlyDictionary<string, IConsoleCommand>` w konstruktorze (domyślnym, delegującym przez `: this(...)`, i wstrzykiwanym), `Handle` wyszukuje komendę po znormalizowanym kluczu.
**Po:**

```csharp
if (!_commands.TryGetValue(parts[0].ToUpperInvariant(), out var command))
{
    return "Nieznana komenda: " + parts[0];
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** słownik jest równoważny warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`StartsWith`) kolejność `if` byłaby częścią kontraktu i słownik by go zgubił.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s15 2 3`

### Rozwiązanie i uzasadnienie

Dyspozytor bez wiedzy o komendach, komendy bezstanowe, stan w `Till`. Nowa komenda to nowa klasa i wpis w słowniku.

### Pułapki

- Zgubiona normalizacja klucza - `sell 1 Diuna` nagle "nieznana komenda" (albo `ToUpper()` zależne od kultury, np. tureckie "i").
- Command traktowany jako darmowa asynchroniczność, retry albo undo - to osobne decyzje (idempotencja, kompensacja).
- Brak sprawdzenia kompletności rejestru przy starcie aplikacji.

### Pytanie do sali

Jak dodalibyście `UNDO` dla ostatniej sprzedaży? Czego wymaga to od komend?

## Scena s16. Form Template Method - raporty CSV i HTML

**Temat ze slajdów:** Apply Template Method; Template Method - sekwencja i ryzyka
**Namespace:** `Training.Workshop.M6.S16TemplateMethod` · **Test:** `scripts/warsztat.sh --lang cs test m6/s16`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `Header`, `Row`, `Footer`, doprowadzamy `Render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako metodę niewirtualną.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. Niewirtualny `Render` chroni kolejność kroków, ale w opublikowanej bibliotece odebranie `virtual` nadpisywanej metodzie złamałoby klientów.

**Różnica względem Javy:** Java dodaje `final` do `render`. W C# metody są domyślnie niewirtualne, więc `Render` w bazie po prostu nie jest `virtual` - to odpowiednik `final`, a `S16SolutionTest.TemplateMethodIsFinal` sprawdza `MethodInfo.IsVirtual == false`. Sortowanie idzie przez `OrderBy` (stabilne, jak `List.sort` w Javie; `List<T>.Sort` w .NET nie jest stabilne), godzina to `TimeOnly.ToString("HH:mm", CultureInfo.InvariantCulture)`. W kroku 1 `Header`, `Row`, `Footer` są prywatne statyczne, a nowy format Markdown w teście to prywatna podklasa zamiast klasy anonimowej.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (`OrderBy(...).ToList()` kontra `foreach` wprost po `OrderBy`, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**W IDE:** w obu klasach ⌥⌘M dla nagłówka, wiersza i stopki: `Header()`, `Row(Sale)`, `Footer(int tickets, Money total)`. Przepisz `Render` HTML na ten sam kształt co CSV (nazwy zmiennych, sortowanie).
**Po:**

```csharp
var text = new StringBuilder(Header());
foreach (var sale in sorted)
{
    text.Append(Row(sale));
    ...
}
return text.Append(Footer(tickets, total)).ToString();
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `Render` obu klas będzie identyczny znak w znak.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s16 0 1`

### Krok 2: Extract Superclass + Pull Up Render

**W IDE:** Refactor This (⌃T) > Extract Superclass `SalesReport` z CSV, zaznacz `Render` i trzy metody jako abstrakcyjne (`protected abstract`). W HTML `: SalesReport`, usuń kopię `Render`, a `Header/Row/Footer` oznacz `protected override`. `Render` zostaw bez `virtual`.
**Po:**

```csharp
public string Render(IReadOnlyList<Sale> sales) { ... }
protected abstract string Header();
protected abstract string Row(Sale sale);
protected abstract string Footer(int tickets, Money total);
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza, że `Render` nie jest wirtualny, i dodaje format Markdown trzema metodami.
**Co powiedzieć:** niewirtualny szablon chroni kolejność kroków. W opublikowanej bibliotece odebranie `virtual` (albo `sealed override`) nadpisywanej metodzie łamie klientów.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny (`virtual`) z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi pola, których jej konstruktor jeszcze nie ustawił.
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** Limit Instantiation with Singleton
**Namespace:** `Training.Workshop.M6.S17Singleton` · **Test:** `scripts/warsztat.sh --lang cs test m6/s17`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `GetInstance()`, potem statyczna właściwość `Instance`), a na końcu ukrywamy za interfejsem `ITariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Statyczna właściwość tylko do odczytu daje bezpieczną wątkowo, jednokrotną inicjalizację (gwarantuje ją CLR), ale oznacza jedną instancję na `AssemblyLoadContext`, nie na proces. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w domyślnym konstruktorze, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

**Różnica względem Javy:** krok 2 w Javie to singleton jako `enum` (język gwarantuje jedną instancję na loader klas). W C# odpowiednikiem jest idiomatyczna statyczna właściwość `public static PriceList Instance { get; } = new();` z prywatnym konstruktorem - CLR inicjalizuje typ raz i bezpiecznie wątkowo (jedna instancja na `AssemblyLoadContext`, analogicznie do "na loader klas"). Niemodyfikowalna mapa to `FrozenDictionary`, licznik w Start to `Interlocked.Increment` zamiast `AtomicInteger`. Oba testy sceny są w kolekcji xUnit `Legacy`, bo xUnit uruchamia klasy równolegle, a Start zmienia statyczny licznik mierzony w `StartCreatesAPriceListForEveryQuote`. Cennik promocyjny w teście to prywatna klasa `PromoTariff` zamiast lambdy, a test pomiaru po `jump` kończy się bez sprawdzeń.

### Co widzimy

`TicketDesk.Quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```csharp
var price = new PriceList().BasePrice(format);
```

### Krok 1: klasyczny Singleton

**W IDE:** prywatny konstruktor, `private static readonly PriceList Instance = new();`, `GetInstance()`. Licznik z pomiaru usuń. Klient woła `PriceList.GetInstance()`.
**Po:**

```csharp
var price = PriceList.GetInstance().BasePrice(format);
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s17 0 1`

### Krok 2: Singleton jako statyczna właściwość tylko do odczytu

**W IDE:** zamień pole i `GetInstance()` na `public static PriceList Instance { get; } = new();`, słownik cen zbuduj raz i zamroź (`ToFrozenDictionary()`).
**Po:**

```csharp
var price = PriceList.Instance.BasePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** CLR daje bezpieczną publikację i jednokrotną inicjalizację bez `lock`. Ale to **jedna instancja na `AssemblyLoadContext`**, nie "jedna na proces", a prywatny konstruktor nie chroni przed refleksją (`Activator.CreateInstance(type, nonPublic: true)`) - inaczej niż enum w Javie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**W IDE:** Refactor This (⌃T) > Extract Interface `ITariff` z `PriceList`; `TicketDesk(ITariff)` plus konstruktor bezargumentowy `: this(PriceList.Instance)`.
**Po:**

```csharp
public TicketDesk()
    : this(PriceList.Instance)
{
}
```

**Uruchom:** test zielony; `InjectedTariffNeedsNoGlobalState` używa cennika promocyjnego `PromoTariff`.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `SetPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (statyczna właściwość `Instance`), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności. W aplikacji z kontenerem DI ten sam efekt daje rejestracja `AddSingleton<ITariff, PriceList>()`.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności (a w xUnit także od równoległości klas testowych).
- Ręcznie pisany leniwy singleton z podwójnym sprawdzaniem zamiast `Lazy<T>` albo inicjalizacji statycznej.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** Collecting Parameter
**Namespace:** `Training.Workshop.M6.S18CollectingParameter` · **Test:** `scripts/warsztat.sh --lang cs test m6/s18`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w `string`, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na listę, potem przekazujemy akumulator do metod `Check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w Start.

**Różnica względem Javy:** brak istotnych. E-mail jest `string?`, `isBlank()` to `string.IsNullOrWhiteSpace`, `addAll` w kroku 1 to `AddRange`, a obcięcie separatora w Start to zakres `warnings[..^2]`.

### Co widzimy

`ReservationValidator.Validate` skleja ostrzeżenia w `string`: każda metoda pomocnicza zwraca fragment zakończony `"; "`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```csharp
warnings += CheckEmail(draft.Email);
warnings += CheckSeats(draft.Seats);
...
return warnings.Length == 0 ? "OK" : warnings[..^2];
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: lista zamiast sklejania

**W IDE:** metody pomocnicze zwracają `IReadOnlyList<string>` bez separatora, `Validate` robi `AddRange` i `string.Join("; ", ...)`.
**Po:**

```csharp
warnings.AddRange(CheckEmail(draft.Email));
warnings.AddRange(CheckSeats(draft.Seats));
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne listy.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s18 0 1`

### Krok 2: Collecting Parameter

**W IDE:** Change Signature (⌘F6) - dodaj parametr `List<string> warnings`, zmień zwracany typ na `void`, zamień `return [x]` na `warnings.Add(x)`. Warunek czasu seansu wyciągnij do `CheckShowTime(draft, warnings)` (⌥⌘M).
**Po:**

```csharp
CheckEmail(draft.Email, warnings);
CheckSeats(draft.Seats, warnings);
CheckShowTime(draft, warnings);
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `Validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s18 1 2`

### Krok 3: wąski typ parametru

**W IDE:** klasa `Warnings` z `Add` i `Summary()`; zmień typ parametru `List<string>` na `Warnings` w każdej metodzie `Check...` (Change Signature ⌘F6 albo ręcznie - Rider nie ma odpowiednika Type Migration z IntelliJ, ale po zmianie typu w `Validate` kompilator wskaże resztę).
**Po:**

```csharp
var warnings = new Warnings();
...
return warnings.Summary();
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `Check...(..., Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólny mutowalny słownik jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator, czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** Visitor i macierz zmian
**Namespace:** `Training.Workshop.M6.S19Visitor` · **Test:** `scripts/warsztat.sh --lang cs test m6/s19`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem testów `is` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę C#: switch expression po typach z wzorcami pozycyjnymi - i jej granice.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W Javie 25 tę samą kontrolę kompilatora daje wyczerpujący `switch` po `sealed`; C# nie ma zamkniętych hierarchii, więc switch po typach wymaga ramienia `_`, a nowy rodzaj pozycji wychodzi dopiero w runtime - w C# argument za Visitorem jest mocniejszy.

**Efekt:** W kodzie nie ma łańcuchów `is`. W wersji z Visitorem pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie, w wersji ze switch expression - `UnreachableException` z nazwą typu. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji, i od tego, ile kontroli kompilatora potrzebujemy.

**Różnica względem Javy:** krok 3 w Javie to `sealed interface` + wyczerpujący `switch` bez `default`, w którym kompilator pilnuje nowych typów. C# 14 nie ma zamkniętych hierarchii ani sprawdzania wyczerpania dla wzorców typów - switch expression bez `_` daje ostrzeżenie CS8509 (przy `TreatWarningsAsErrors` błąd). Krok 3 w porcie to więc switch expression po typach (w linii paragonu wzorce pozycyjne, odpowiednik record patterns) z ramieniem `_ => throw new UnreachableException(...)`, a `IOrderItem` jest zwykłym interfejsem znacznikowym. Test Javy `newOperationIsANewSwitchInJava25` nazywa się `NewOperationIsANewSwitchExpression`. Generyczne `accept` to `TResult Accept<TResult>(IOrderItemVisitor<TResult> visitor)`, a interfejs Visitora jest kowariantny (`out TResult`).

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `is` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```csharp
if (item is TicketItem ticket)
{
    return VatOf(ticket.Price, 8);
}
else if (item is SnackItem snack)
{
    return VatOf(snack.Price, 23);
}
else if (item is VoucherItem)
{
    return Money.Zero;
}
throw new ArgumentException("unknown item: " + item);
```

### Krok 1: Accept + pierwszy Visitor

**W IDE:** interfejs `IOrderItemVisitor<out TResult>` z `VisitTicket/VisitSnack/VisitVoucher`, metoda `Accept<TResult>` w `IOrderItem` i rekordach. Operację "linia paragonu" przenieś do `ReceiptLineVisitor`.
**Po:**

```csharp
text.Append(item.Accept(_lines)).Append('\n');
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**W IDE:** `AmountVisitor` i `VatVisitor`, usuń łańcuchy `is`.
**Po:**

```csharp
total = total.Plus(item.Accept(_amounts));
vat = vat.Plus(item.Accept(_vats));
```

**Uruchom:** test zielony; `S19SolutionTest.NewOperationIsANewVisitor` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów. W C# to jedyna z form tej sceny, w której kompilator pilnuje kompletności.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s19 1 2`

### Krok 3: alternatywa C# - switch expression po typach

**W IDE:** usuń `Accept` i Visitory, `IOrderItem` zostaje pustym interfejsem, operacje jako switch expression po typach z ramieniem `_ => throw new UnreachableException(...)` (z wzorcami pozycyjnymi w linii paragonu).
**Po:**

```csharp
internal static Money Vat(IOrderItem item) => item switch
{
    TicketItem ticket => VatOf(ticket.Price, 8),
    SnackItem snack => VatOf(snack.Price, 23),
    VoucherItem => Money.Zero,
    _ => throw new UnreachableException("unknown item: " + item),
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** każda operacja w jednym miejscu, bez ceremonii `Accept/Visit`, ale **bez** kontroli kompilatora: nowy rodzaj pozycji skompiluje się i wyjdzie jako `UnreachableException` na kasie. W Javie 25 ta forma daje tę samą kontrolę co Visitor, w C# - nie. Visitor ma w C# sens zawsze, gdy nowe rodzaje mają psuć kompilację, a także gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`Step2`) i switch expression po typach (`Step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor (w C# jedyna forma z kontrolą kompilatora) albo switch expression po typach |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- Ramię `_`, które po cichu zwraca wartość domyślną zamiast rzucać `UnreachableException` - nowy typ pozycji przejdzie niezauważony.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze Start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** Najpierw rodzaj zmienności; Mapa decyzji (1/2 i 2/2); Lista kontrolna
**Namespace:** `Training.Workshop.M6.S20DecisionMap` · **Test:** `scripts/warsztat.sh --lang cs test m6/s20`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

**Różnica względem Javy:** `@FunctionalInterface DayPolicy` to delegat `DayPolicy(Money basePrice)` - Strategy jako delegat; stałe `DayPolicies.CheapTuesday`, `Weekend`, `Regular` to lambdy jak w Javie, a kalendarz to `Func<DayOfWeek, DayPolicy>`. `enum Format` z polami i zachowaniem to `sealed class Format` z instancjami statycznymi `TwoD`, `ThreeD`, `Imax` i prywatnym konstruktorem (enum C# nie ma pól ani metod). `DayOfWeek` pochodzi z `System` (wartości PascalCase), a test parsuje wejście "MONDAY 2D" przez `Enum.Parse<DayOfWeek>(..., ignoreCase: true)`, więc oczekiwane teksty są identyczne jak w Javie.

### Co widzimy

`ShowPricing.Price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```csharp
if (format == "2D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "17.50" : weekend ? "27.00" : "25.00");
}
else if (format == "3D")
{
    return Money.Of(day == DayOfWeek.Tuesday ? "22.40" : weekend ? "34.00" : "32.00");
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**W IDE:** zauważ, że tabela to dwie niezależne reguły. ⌥⌘M `BasePrice(format)` ze switch expression po formacie i `AdjustForDay(day, basePrice)` ze switch expression po dniu. `Price` = złożenie.
**Po:**

```csharp
public Money Price(DayOfWeek day, string format)
{
    return AdjustForDay(day, BasePrice(format));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m6/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**W IDE:** delegat `DayPolicy(Money basePrice)`, statyczna klasa `DayPolicies` ze stałymi `CheapTuesday`, `Weekend`, `Regular` i kalendarzem `DayPolicies.Standard(day)`. `ShowPricing` dostaje kalendarz `Func<DayOfWeek, DayPolicy>` w konstruktorze (domyślnie standardowy). Format zostaje prostym `switch`.
**Po:**

```csharp
return _calendar(day)(BasePrice(format));
```

**Uruchom:** test zielony; `PathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany. Tu delegat jest naturalniejszy niż interfejs z s01: polityka to jedna funkcja bez nazwanych wariantów w konfiguracji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**W IDE:** pokaż alternatywę od kroku 1: `sealed class Format` z instancjami statycznymi, ceną bazową i metodą `PriceOn(day)`, `Format.Of(code)` z tym samym wyjątkiem. `ShowPricing` deleguje. Reguła dnia zostaje zwykłym `switch` w typie.
**Po:**

```csharp
public Money Price(DayOfWeek day, string format)
{
    return Format.Of(format).PriceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh --lang cs diff m6/s20 1 3` z `diff m6/s20 1 2`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m6/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

### Rozwiązanie i uzasadnienie

Kryterium wyboru: **rodzaj zmienności, nie kształt kodu**.

| Co zmienia się często | Struktura | Co jest tanie |
| --- | --- | --- |
| reguły dnia, kampanie, kalendarz | Strategy (ścieżka A) | nowa polityka, wymiana kalendarza w teście |
| formaty i ich wyjątki | typ z zachowaniem (ścieżka B) | nowy format w jednym miejscu |
| nic, tabela stabilna | krok 1 wystarczy | czytelność bez nowych typów |

Obie ścieżki są równoważne behawioralnie - test to potwierdza. Różnią się kosztem **przyszłej** zmiany. Gdy nie wiemy, która oś będzie się zmieniać, zostajemy na kroku 1.

### Pułapki

- Wybór wzorca "bo ładniej" bez historii zmian (git log, backlog) - zgadujemy przyszłość.
- Obie ścieżki naraz (Strategy dnia w typie formatu) - podwójna ceremonia dla dwóch stabilnych osi.
- Reguła zależna od obu osi naraz ("IMAX we wtorek bez zniżki") - łamie założenie niezależności z kroku 1; wtedy tabela może być lepsza niż wzorzec.

### Pytanie do sali

Biznes zapowiada "IMAX we wtorek bez zniżki". Która ścieżka przyjmie to łatwiej i dlaczego?

## Proponowana kolejność pokazu

**Ścieżka krótka (~75 min)** - po jednej scenie z każdej grupy slajdów, z naciskiem na kontrakt:

1. s20 mapa decyzji (12 min) - ustawia pytanie "jaki rodzaj zmienności?" na cały moduł.
2. s01 Strategy (12 min) - pułapka momentu wyboru.
3. s08 State (15 min) - tabela przejść przed hierarchią, kolejność efektów.
4. s07 Decorator (12 min) - kolejność jest kontraktem, granice przezroczystości.
5. s09 Observer (12 min, kroki 1-2, krok 3 omówić z `diff`) - fail-fast i rozszerzenie kontra refaktoryzacja.
6. s10 Implicit Tree (12 min) - test różnicowy i mapper.

**Ścieżka pełna (~4 h z przerwami)** - kolejność zgodna z agendą slajdów:

1. Strategy, polimorfizm, typ: s20 (wprowadzenie), s01, s02, s03.
2. Builder, Factory, Decorator: s04, s05, s06, s07.
3. State, Observer, Composite: s08, s09, s10.
4. Adapter, Command, Template Method: s14, s15, s16.
5. Jeden/wiele, Singleton: s12, s11, s17.
6. Collecting Parameter, Visitor, Extract Composite: s18, s19, s13.
7. Podsumowanie: wróć do s20 i poproś salę o przypisanie każdej sceny do wiersza mapy decyzji.

Sceny s11 i s13 (po 2 kroki) dobrze sprawdzają się jako samodzielna praca uczestników w parach, a s04 i s05 jako jeden blok "Factory" z porównaniem obu refaktoryzacji. W grupie C# warto dodatkowo zestawić s02, s11 i s19: wszystkie trzy pokazują, czego C# nie daje w miejscu `sealed interface` z Javy i czym to zastępujemy (konstruktor `private protected`, test refleksyjny, Visitor).
