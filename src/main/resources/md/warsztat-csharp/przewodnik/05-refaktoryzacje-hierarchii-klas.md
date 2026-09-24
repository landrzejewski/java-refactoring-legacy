# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy (C#): przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje każdy ruch z modułu 5: Pull Up / Push Down, Extract Superclass / Subclass / Interface, Collapse Hierarchy, kompozycję zamiast dziedziczenia, a także semantykę C# i CLR, która sprawia, że te ruchy bywają niebezpieczne (przeciążenia, ukrywanie pól, konstruktory, generyki i refleksja, hierarchie zamknięte, zgodność binarna, serializacja, proxy). Sceny nie powielają studium powiadomień z modułu 5 (w repozytorium w wersji Java, `pl.training.module5`) ani ćwiczeń z `md/zadania/05-*.md`. Każda scena ma katalog (namespace) `Start` (na nim pracujesz na żywo), kompletne snapshoty `StepN` i testy, które po każdym kroku mają być zielone.

Kod portu C# leży w `csharp/src/Training.Workshop/M5/SNN.../{Start,Step1,...}`, testy (xUnit) w `csharp/tests/Training.Workshop.Tests/M5/SNN.../`. Wspólne konwencje portu: gettery Javy (`title()`, `seat()`, `basePrice()`) to właściwości (`Title`, `Seat`, `BasePrice`), a metody liczące i nadpisywane (`Price()`, `Label()`, `Describe()`) zostały metodami. W C# metody **domyślnie nie są wirtualne**: tam, gdzie Java po prostu nadpisuje metodę bazy, baza ma w C# jawne `virtual` albo `abstract`, podklasa obowiązkowe `override`, a javowe `final` na metodzie to zwykła metoda niewirtualna. Ukrycie członka bazy wymaga słowa `new` - bez niego kompilator daje ostrzeżenie CS0108/CS0114, a projekt ma `TreatWarningsAsErrors`, więc to błąd. Tam, gdzie scena w porcie działa inaczej niż w Javie, akapit **Różnica względem Javy** mówi, co i dlaczego.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang cs list m5              # sceny i kroki modułu 5
scripts/warsztat.sh --lang cs test m5/s01          # testy jednej sceny
scripts/warsztat.sh --lang cs test m5              # wszystkie sceny modułu (238 testów)
scripts/warsztat.sh --lang cs diff m5/s01 0 1      # co zmienia krok 1 względem Start
scripts/warsztat.sh --lang cs diff m5/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang cs jump m5/s01 2        # nie zdążyłeś? Start = snapshot Step2
scripts/warsztat.sh --lang cs next m5/s01          # następny krok do Start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w Start
scripts/warsztat.sh --lang cs reset m5/s01         # przywraca Start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w Rider uruchamiasz test sceny (ikona w gutterze przy klasie testu albo okno Unit Tests), zanim cokolwiek powiesz.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang cs next m5/sNN` wstawia do `Start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `Start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `Start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Test `SNNEquivalenceTest` sprawdza, że Start i wszystkie kroki zachowują się tak samo. `SNNSolutionTest` pokazuje to, czego równoważność nie widzi: typ deklarujący członka, klasę runtime, pułapkę semantyki, zgodność binarną.
- Sceny o pułapkach (s08-s11, s13) mają w `SNNSolutionTest` testy o nazwie `Start...`, które **dokumentują pułapkę w Start**. Gdy naprawisz Start na żywo, te testy zrobią się czerwone - to jest dowód, że pułapka zniknęła. Powiedz to sali przed pierwszym krokiem. `reset` przywraca stan wyjściowy.
- Pozostałe testy strukturalne sprawdzają snapshoty `StepN`, więc praca na `Start` ich nie psuje.
- Kilka scen (s13, s15, s16) kompiluje w teście kod w pamięci przez Roslyn i ładuje go w osobnym `AssemblyLoadContext` - to odpowiednik `javax.tools` i `URLClassLoader` z wersji Java. Pomocnicze klasy leżą w `tests/Training.Workshop.Tests/M5/Tooling` (`InMemoryAssembly`, `SceneSources`) i nie są sceną.
- Scena s11 w C# kompiluje się **bez** ostrzeżenia: kompilator C# nie ma odpowiednika javowego `-Xlint:this-escape`. Najbliższy jest analizator CA2214 ("nie wołaj metod nadpisywalnych w konstruktorach"), wspomniany w komentarzu kodu, ale niewłączony w buildzie.
- Skróty w Rider (keymap macOS w stylu IntelliJ): ⌃T Refactor This (menu wszystkich refaktoryzacji w miejscu kursora, tam są m.in. Pull Members Up, Push Members Down, Extract Superclass, Extract Interface, Extract Class, Safe Delete), ⇧F6 Rename, ⌥⌘M Extract Method, ⌥⌘V Introduce Variable, ⌥⌘N Inline, F6 Move, ⌘F6 Change Signature, ⌘⌦ Safe Delete, ⌘N Generate (konstruktory, Overriding Members, Delegating Members, Equality Members), ⌥⏎ akcje kontekstowe i quick fixy.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Namespace | Czas |
| --- | --- | --- | --- | --- |
| s01 | Pull Up Method (3.1-3.3) | 3 | `M5.S01PullUpMethod` | ~12 min |
| s02 | Pull Up Field (3.4-3.6) | 3 | `M5.S02PullUpField` | ~10 min |
| s03 | Push Down Method/Field, asymetria przesunięć (4.1-4.4) | 3 | `M5.S03PushDown` | ~12 min |
| s04 | Extract Superclass, konstruktory i fabryki (5) | 3 | `M5.S04ExtractSuperclass` | ~15 min |
| s05 | Extract Subclass (6) | 4 | `M5.S05ExtractSubclass` | ~15 min |
| s06 | Extract Interface, domyślne metody interfejsu (7.1-7.4) | 3 | `M5.S06ExtractInterface` | ~12 min |
| s07 | Collapse Hierarchy (8) | 3 | `M5.S07CollapseHierarchy` | ~8 min |
| s08 | Replace Inheritance with Composition, `new` zamiast `override`, pułapki delegowania (9.1-9.4) | 2 | `M5.S08Composition` | ~12 min |
| s09 | Overriding a overloading (2.1-2.2) | 2 | `M5.S09Overloading` | ~10 min |
| s10 | Ukrywanie pól i metod static (2.3) | 2 | `M5.S10FieldHiding` | ~8 min |
| s11 | Konstruktor wołający override, inicjalizatory pól przed konstruktorem bazy (2.4) | 2 | `M5.S11ConstructorCall` | ~8 min |
| s12 | Generyki i metody bridge (2.7, 10.2) | 2 | `M5.S12BridgeMethods` | ~10 min |
| s13 | Hierarchie zamknięte, `switch` po typach, `UnreachableException` (2.8) | 3 | `M5.S13Sealed` | ~12 min |
| s14 | Współdzielenie implementacji a podtypowanie (1.1-1.2, 9.1) | 2 | `M5.S14Reuse` | ~10 min |
| s15 | Zgodność binarna, refleksja i atrybuty (1.3, 10.2-10.3) | 4 | `M5.S15Compatibility` | ~15 min |
| s16 | Serializacja, proxy, DI/ORM (10.4-10.6) | 3 | `M5.S16SerializationProxy` | ~12 min |

Pełne nazwy namespace zaczynają się od `Training.Workshop.`, katalogi to `csharp/src/Training.Workshop/M5/S01PullUpMethod` itd.

## Scena s01. Pull Up Method - najpierw ujednolicić ciała

**Temat ze slajdów:** 3.1-3.3. Pull Up Method
**Namespace:** `Training.Workshop.M5.S01PullUpMethod` · **Test:** `scripts/warsztat.sh --lang cs test m5/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Trzy podklasy `Ticket` mają własne `Label()`, różne tekstem, a robiące to samo. Najpierw ujednolicamy ciała, potem wciągamy do bazy abstrakcyjne `Price()` jako punkt rozszerzenia, a na końcu jedno niewirtualne `Label()`.

**Zasada:** Pull Up Method przenosi metodę na najniższy poziom hierarchii, na którym jest prawdziwa dla wszystkich potomków. Porównujemy kontrakty, a nie tekst: identyczne ciało nie gwarantuje tego samego zachowania, a różny tekst może kodować ten sam kontrakt.

**Efekt:** Podklasy zawierają tylko regułę ceny, a `BoxOffice` pracuje na typie bazowym. Kosztem jest niewirtualne `Label()`: bilet z innym formatem etykiety będzie wymagał świadomej zmiany kontraktu bazy.

**Różnica względem Javy:** javowe `final label()` to w C# po prostu metoda bez `virtual` - podklasa nie może jej nadpisać (`override` na niewirtualnej metodzie to błąd CS0506). Test sprawdza to przez `Assert.False(label.IsVirtual)` zamiast `Modifier.isFinal`. `String.format` z wersji Java to `string.Format(CultureInfo.InvariantCulture, ...)`.

### Co widzimy

Trzy bilety (`StandardTicket`, `StudentTicket`, `VipTicket`) mają wspólną nadklasę `Ticket`, ale każdy deklaruje własne `Label()`. Ciała różnią się tekstem, a robią to samo:

```csharp
return Title + ": " + Price();                                                        // StandardTicket
return string.Format(CultureInfo.InvariantCulture, "{0}: {1}", Title, Price());       // StudentTicket
return new StringBuilder(Title).Append(": ").Append(Price()).ToString();              // VipTicket
```

Rider nie przeniesie w górę trzech różnych metod jako jednej. Do tego `Label()` woła `Price()`, którego baza nie zna. Klient `BoxOffice` musi znać konkretne klasy.

### Krok 1: Ujednolicenie ciał metod

**W IDE:** ręcznie zmień ciało `Label()` w `StudentTicket` i `VipTicket` na wersję z `StandardTicket`. Możesz skopiować i wkleić, ale porównaj wynik z kontraktem, nie z tekstem: `{0}` w `string.Format` i konkatenacja dają to samo, bo oba wołają `Money.ToString()`, a ten formatuje kwotę z `CultureInfo.InvariantCulture`.
**Po:**

```csharp
public string Label()
{
    return Title + ": " + Price();
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s01` - 15 testów zielonych.
**Co powiedzieć:** Pull Up wymaga identycznego ciała, więc najpierw robimy z trzech wersji jedną. To osobny, bezpieczny krok, a test pilnuje, że format się nie zmienił.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 0 1`

### Krok 2: Pull Up Price() jako metoda abstrakcyjna

**W IDE:** w `StandardTicket` na `Price()` ⌃T > Pull Members Up, zaznacz `Price()` i opcję **Make abstract**. W pozostałych podklasach dodaj `override` (⌥⏎ > "Add 'override' modifier" - bez niego kompilator zgłosi, że klasa nie implementuje abstrakcyjnego członka i że metoda ukrywa członka bazy).
**Po:**

```csharp
public abstract class Ticket
{
    ...
    public abstract Money Price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** ciała `Price()` są różne, więc do bazy idzie tylko deklaracja. To punkt rozszerzenia, dzięki któremu wspólne `Label()` będzie mogło zamieszkać w bazie. W C# `override` jest obowiązkowe - kompilator od razu pokazuje, które metody nadpisują bazę.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 1 2`

### Krok 3: Pull Up Label() i klient na typie bazowym

**W IDE:** ⌃T > Pull Members Up na `Label()` w `StandardTicket`. Kopie w `StudentTicket` i `VipTicket` usuń (Safe Delete ⌘⌦) - zostawione ukrywałyby metodę bazy (ostrzeżenie CS0108, u nas błąd). Nie dodawaj `virtual`. W `BoxOffice` zmień `switch` tak, by zwracał `Ticket`, i wywołaj `Label()` raz (Introduce Variable ⌥⌘V).
**Po:**

```csharp
public string Label()
{
    return Title + ": " + Price();
}
```

**Uruchom:** test zielony. `S01SolutionTest` sprawdza, że `Label()` jest zadeklarowane tylko w `Ticket` i nie jest wirtualne, a `Price()` jest abstrakcyjne.
**Co powiedzieć:** metoda trafiła na najniższy poziom, na którym jest prawdziwa dla wszystkich potomków. Brak `virtual` (odpowiednik `final`) chroni przed przypadkowym nadpisaniem przez podklasę spoza repozytorium, która miała własne `Label()`: jej `override` się nie skompiluje, a metoda bez `override` dostanie ostrzeżenie o ukryciu, zamiast po cichu zmienić zachowanie.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`Step3/Ticket` ma abstrakcyjne `Price()` i niewirtualne `Label()`. Podklasy zawierają tylko to, co naprawdę różne: regułę ceny. Klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym.

### Pułapki

- Pull Up metody o tej samej sygnaturze, ale innym kontrakcie (np. `Label()` jednej podklasy dodaje walutę). Tekst to nie kontrakt.
- Metoda wciągnięta do bazy, która korzysta z pola dostępnego tylko w jednej podklasie - IDE zaproponuje wtedy `protected` pole albo właściwość "na zapas".
- Zewnętrzna podklasa z metodą o tej samej nazwie: w C# nie staje się nieplanowanym override (bez `override` to ukrycie z ostrzeżeniem CS0108), ale gdy ktoś "naprawi" ostrzeżenie słowem `new`, klient wołający przez typ bazowy dostanie wersję z bazy, a przez typ podklasy - wersję z podklasy.
- Refleksja: `typeof(StudentTicket).GetMethod("Label", BindingFlags.DeclaredOnly | ...)` przestaje ją znajdować po Pull Up (scena s15).

### Pytanie do sali

Czy `Label()` powinno zostać niewirtualne, jeśli za rok pojawi się bilet z etykietą w innym formacie? Co wtedy zrobicie?

## Scena s02. Pull Up Field - to samo znaczenie, typ i cykl życia

**Temat ze slajdów:** 3.4-3.6. Pull Up Field i ryzyka Pull Up
**Namespace:** `Training.Workshop.M5.S02PullUpField` · **Test:** `scripts/warsztat.sh --lang cs test m5/s02`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Każdy bilet trzyma miejsce na sali po swojemu: inna nazwa pola, setter zamiast konstruktora, normalizacja w VIP. Wyrównujemy nazwę i cykl życia pól, a dopiero potem przenosimy jedno pole `_seat` do `Ticket`.

**Zasada:** Pull Up Field łączy pola tylko wtedy, gdy mają to samo znaczenie, typ, cykl życia, walidację i moment inicjalizacji. Pole w bazie jest `private` i ustawiane przez `base(...)`, a nie surowe `protected`. Ten sam typ i podobna nazwa to za mało - dlatego `_studentId` zostaje.

**Efekt:** Stan miejsca ma jedno źródło, a normalizacja VIP została w podklasie, która przekazuje bazie gotową wartość. Znika setter, więc miejsce biletu normalnego jest teraz niemutowalne.

**Różnica względem Javy:** setter `setSeat` to w C# właściwość z setterem (`Seat { get; set; }`), a klient używa inicjalizatora obiektu `new StandardTicket { Seat = seat }`. Różnicę cyklu życia widać też w typach: pole ustawiane setterem po konstrukcji jest `string?`, bo do chwili wywołania settera jest `null`. Javowe `final` na polu to `readonly` (test sprawdza `FieldInfo.IsInitOnly`), a pola mają prefiks `_`: `_seat`, `_seatCode`, `_studentId`.

### Co widzimy

Każdy bilet przechowuje miejsce na sali, ale każdy po swojemu: `StandardTicket` ma mutowalne `_seat` ustawiane setterem właściwości, `StudentTicket` ma `_seatCode` z właściwością `SeatCode`, a `VipTicket` normalizuje wartość do wielkich liter. `_studentId` to zupełnie inne pojęcie.

```csharp
_ => new StandardTicket { Seat = seat }.Describe(),   // klient w BoxOffice
```

### Krok 1: Rename _seatCode na _seat

**W IDE:** w `StudentTicket` na polu `_seatCode` Rename ⇧F6 -> `_seat` (Rider zaproponuje zmianę nazwy parametru konstruktora - zgódź się). Właściwość `SeatCode` również ⇧F6 -> `Seat`.
**Po:**

```csharp
private readonly string _seat;

public string Seat => _seat;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s02` - 15 testów zielonych.
**Co powiedzieć:** to samo znaczenie musi mieć tę samą nazwę, inaczej IDE nie połączy pól. Rename jest bezpieczny, bo nikt spoza klasy nie używał `SeatCode`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 0 1`

### Krok 2: Ujednolicenie cyklu życia (konstruktor zamiast settera)

**W IDE:** Introduce Parameter ⌥⌘P nie pomoże przy setterze, więc ręcznie: w `StandardTicket` dodaj konstruktor `StandardTicket(string seat)` (Generate ⌘N > Constructor), oznacz pole `readonly` i zmień jego typ na `string`, zamień właściwość na tylko do odczytu `Seat => _seat` i popraw klienta na `new StandardTicket(seat)`. Kompilator wskaże jedyne użycie settera w `BoxOffice`.
**Po:**

```csharp
private readonly string _seat;

public StandardTicket(string seat)
{
    _seat = seat;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** pole mutowalne i pole `readonly` to dwa różne cykle życia - w C# widać to nawet w typie (`string?` kontra `string`). Gdybyśmy wciągnęli je do bazy teraz, jeden z biletów zmieniłby semantykę. Dopiero po tym kroku trzy pola mają ten sam typ, znaczenie, walidację i moment inicjalizacji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 1 2`

### Krok 3: Pull Up Field _seat i właściwości Seat

**W IDE:** w `StandardTicket` ⌃T > Pull Members Up, zaznacz pole `_seat` i właściwość `Seat`. Duplikaty w `StudentTicket` i `VipTicket` usuń (Safe Delete ⌘⌦) - zostawione ukryłyby członków bazy (scena s10). Następnie w `Ticket` dodaj konstruktor `protected Ticket(string seat)` (Generate ⌘N > Constructor) i w podklasach zamień przypisanie na `: base(seat)`. W `VipTicket`: `: base(seat.ToUpperInvariant())`.
**Po:**

```csharp
public abstract class Ticket
{
    private readonly string _seat;

    protected Ticket(string seat)
    {
        _seat = seat;
    }

    public string Seat => _seat;
    ...
}
```

**Uruchom:** test zielony. `S02SolutionTest` sprawdza, że pole jest `private readonly` w `Ticket`, a `_studentId` zostało w `StudentTicket`.
**Co powiedzieć:** pole w bazie jest prywatne i ustawiane przez `base(...)`, nie `protected`. Reguła normalizacji VIP została w podklasie - baza dostaje już gotową wartość.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Jedno źródło stanu miejsca, bez surowego `protected`. Pola o innym znaczeniu (`_studentId`) nie są przenoszone tylko dlatego, że mają ten sam typ.

### Pułapki

- Pull Members Up potrafi zostawić pole z dostępem `protected`, żeby podklasy dalej się kompilowały. Zmień na `private` + właściwość.
- Pola `static` o tej samej nazwie po Pull Up scalają dwa niezależne stany (np. liczniki numerów biletów).
- Refleksja i serializatory zobaczą inny typ deklarujący pola (scena s16).
- Pole wciągnięte w górę, choć w bazie i podklasie zostawiono deklaracje, to dwa sloty (scena s10). W C# kompilator ostrzeże o ukryciu, ale słowo `new` to ostrzeżenie ucisza.

### Pytanie do sali

Gdyby `VipTicket` normalizował miejsce w getterze właściwości, a nie w konstruktorze, czy Pull Up Field nadal byłby bezpieczny?

## Scena s03. Push Down Method/Field i asymetria przesunięć

**Temat ze slajdów:** 4.1-4.4. Push Down Method, Push Down Field i asymetria przesunięć
**Namespace:** `Training.Workshop.M5.S03PushDown` · **Test:** `scripts/warsztat.sh --lang cs test m5/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` obiecuje `UpgradeToVip()` wszystkim biletom, a bilet studencki odmawia wyjątkiem. Przestawiamy klientów na podtyp, przenosimy zachowanie za hak `Surcharge()`, a na końcu spychamy pole i operację VIP do `StandardTicket`.

**Zasada:** Push Down zawęża zbyt szeroki kontrakt bazy do gałęzi, która naprawdę potrzebuje członka. Sygnały to `NotSupportedException`, flagi bez znaczenia i wywołania po sprawdzeniu `is`. Kolejność: klienci, potem zachowanie korzystające z pola, na końcu pole.

**Efekt:** Baza obiecuje tylko to, co prawdziwe dla wszystkich biletów, a `StudentTicket` nie musi niczego odmawiać. W bibliotece to zmiana łamiąca: stare binaria wołające `Ticket.UpgradeToVip` dostaną `MissingMethodException`, bo Pull Up i Push Down nie są symetryczne.

**Różnica względem Javy:** `UpgradeToVip()` musi być w bazie jawnie `virtual`, żeby `StudentTicket` mógł je nadpisać. Javowe `UnsupportedOperationException` to `NotSupportedException`, `instanceof` to `is`, a `getMethod(...)` rzucające `NoSuchMethodException` to `GetMethod(...)` zwracające `null`. Asymetria jest w CLR taka sama jak w JVM: wywołanie skompilowane jako odwołanie do metody na podklasie po Pull Up się wiąże (CLR szuka w klasach bazowych), po Push Down kończy się `MissingMethodException` (odpowiednik `NoSuchMethodError`).

### Co widzimy

`Ticket` obiecuje `UpgradeToVip()` wszystkim biletom, ale bilet studencki odrzuca dopłatę wyjątkiem. Klient sprawdza `is`, zanim wywoła metodę. Klasyczne sygnały zbyt szerokiego kontraktu.

```csharp
public override void UpgradeToVip()
{
    throw new NotSupportedException("bilet ulgowy nie ma dopłaty VIP");
}
```

### Krok 1: Klienci na podtyp

**W IDE:** w `BoxOffice` rozdziel gałęzie: dla studenta zwróć cenę od razu, dla biletu normalnego zadeklaruj zmienną typu `StandardTicket` (`var ticket = new StandardTicket(basePrice)`). Ręcznie - to kilka linii.
**Po:**

```csharp
if ("STUDENT" == kind)
{
    return new StudentTicket(basePrice).Price();
}
var ticket = new StandardTicket(basePrice);
if (vip)
{
    ticket.UpgradeToVip();
}
return ticket.Price();
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s03` - 19 testów zielonych.
**Co powiedzieć:** zanim przesuniemy metodę w dół, nikt nie może jej wołać przez typ bazowy. Find Usages (⌥F7) na `Ticket.UpgradeToVip` ma pokazać tylko wywołania na `StandardTicket`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 0 1`

### Krok 2: Najpierw zachowanie - hak Surcharge()

**W IDE:** w `Ticket.Price()` Extract Method ⌥⌘M na wyrażeniu dopłaty, nazwa `Surcharge()`, zwraca `Money.Zero`, i oznacz ją `protected virtual`. Następnie w `StandardTicket` Generate ⌘N > Overriding Members > `Surcharge()` z logiką VIP.
**Po:**

```csharp
// Ticket
public Money Price()
{
    return _basePrice.Minus(_basePrice.Percent(DiscountPercent())).Plus(Surcharge());
}

protected virtual Money Surcharge()
{
    return Money.Zero;
}

// StandardTicket
protected override Money Surcharge()
{
    return IsVipUpgraded ? Money.Of("10.00") : Money.Zero;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie czyta już pola `_vipUpgraded`. Najpierw przenosimy zachowanie, które korzysta z pola, potem samo pole - inaczej Push Down się nie skompiluje albo zostawi dwa sloty.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 1 2`

### Krok 3: Push Members Down

**W IDE:** w `Ticket` ⌃T > Push Members Down, zaznacz `_vipUpgraded`, `UpgradeToVip()`, `IsVipUpgraded`, cel: tylko `StandardTicket`. W `StandardTicket` metoda traci `virtual`. Potem Safe Delete ⌘⌦ na override rzucającym wyjątek w `StudentTicket` - po ruchu i tak się nie kompiluje (nie ma czego nadpisać).
**Po:**

```csharp
public sealed class StandardTicket : Ticket
{
    private bool _vipUpgraded;
    ...
    public void UpgradeToVip()
    {
        _vipUpgraded = true;
    }

    public bool IsVipUpgraded => _vipUpgraded;
```

**Uruchom:** test zielony. `S03SolutionTest` pokazuje, że `typeof(Ticket).GetMethod("UpgradeToVip")` zwraca `null`, a metoda jest zadeklarowana w `StandardTicket`.
**Co powiedzieć:** baza obiecuje tylko to, co prawdziwe dla wszystkich biletów. W bibliotece to zmiana łamiąca: stare assembly klienta ma w IL `callvirt Ticket::UpgradeToVip` i dostanie `MissingMethodException`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Stan i operacja VIP żyją w `StandardTicket`. `StudentTicket` nie musi niczego odmawiać. Kontrakt bazy jest węższy, ale uczciwy.

### Pułapki

- **Asymetria:** Pull Up jest zgodny binarnie dla wywołań (CLR szuka metody w klasach bazowych, test `BeforePushDownSubclassFindsMethodInSuperclass`), Push Down nie jest (CLR nie szuka w podklasach).
- Kilka podklas potrzebuje metody - przesuwamy do najbliższego wspólnego przodka, nie kopiujemy do liści.
- Zostawienie pola w bazie i skopiowanie go do podklasy daje dwa niezależne sloty.

### Pytanie do sali

Jak przeprowadzić ten Push Down w opublikowanym pakiecie NuGet, którego klientów nie możecie przekompilować?

## Scena s04. Extract Superclass - seans i wynajem sali

**Temat ze slajdów:** 5. Extract Superclass
**Namespace:** `Training.Workshop.M5.S04ExtractSuperclass` · **Test:** `scripts/warsztat.sh --lang cs test m5/s04`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Seans i wynajem sali nie mają wspólnego typu, więc `HallPlanner` ma trzy kopie warunku kolizji. Wydzielamy abstrakcyjną nadklasę `HallBooking`, dołączamy do niej klasy pojedynczo i przenosimy tam jeden algorytm `Overlaps`.

**Zasada:** Extract Superclass ma sens, gdy klasy są wariantami jednego pojęcia ze wspólnym kontraktem - samo podobieństwo linii kodu to za mało. Nazwa nadklasy opisuje pojęcie domenowe, a nie `Base` czy `Common`. Konstruktory nie są dziedziczone, więc publiczne sygnatury i fabryki zachowujemy świadomie.

**Efekt:** `HallPlanner` ma jedną pętlę, a klienci nie zauważyli zmiany, bo `Conflicts(...)`, konstruktory i `Rental(...)` zostały. Nowy poziom zmienia jednak `BaseType` i typ deklarujący właściwości, co może mieć znaczenie dla refleksji i mapperów.

**Różnica względem Javy:** `name()` to abstrakcyjna właściwość `Name`, a finalne `overlaps` to niewirtualne `Overlaps`. `getSuperclass()` to `Type.BaseType`. `LocalDateTime` to `DateTime`, `isBefore` to operator `<`, a wynik jest sortowany `string.CompareOrdinal` (jak `Collections.sort` dla `String`).

### Co widzimy

`Screening` (seans) i `PrivateEvent` (wynajem sali na firmową imprezę) mają te same pojęcia: sala, początek, czas trwania, koniec. Nie mają wspólnego typu, więc `HallPlanner` szuka kolizji w trzech pętlach z trzema kopiami warunku.

```csharp
if (a.Hall == b.Hall
    && a.Start < b.End && b.Start < a.End)
```

Oba typy są wariantami jednego pojęcia - rezerwacji sali. To jest właściwy sygnał do Extract Superclass, a nie samo podobieństwo linii kodu.

### Krok 1: Extract Superclass z Screening

**W IDE:** na `Screening` ⌃T > Extract Superclass, nazwa `HallBooking`, zaznacz pola `_hall`, `_start`, `_minutes` i właściwości `Hall`, `Start`, `End`. Rider wygeneruje klasę bazową i `: base(...)` w konstruktorze. Klasę bazową oznacz `abstract`, jej konstruktor `protected`. Sprawdź, czy pola w bazie są `private` - jeśli IDE dało `protected`, zmień.
**Po:**

```csharp
public abstract class HallBooking
{
    private readonly string _hall;
    private readonly DateTime _start;
    private readonly int _minutes;

    protected HallBooking(string hall, DateTime start, int minutes) { ... }
    ...
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s04` - 19 testów zielonych.
**Co powiedzieć:** nazwa opisuje pojęcie domenowe. `BaseScreening` albo `CommonBase` zdradzałyby, że wydzielamy tylko z powodu duplikacji. Publiczny konstruktor `Screening` się nie zmienił.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 0 1`

### Krok 2: PrivateEvent dołącza do hierarchii

**W IDE:** w `PrivateEvent` dopisz `: HallBooking`, dodaj do konstruktora `: base(hall, start, minutes)`, usuń zduplikowane pola i właściwości (Rider oznaczy je ostrzeżeniem "hides inherited member" - Safe Delete ⌘⌦).
**Po:**

```csharp
public sealed class PrivateEvent : HallBooking
{
    private readonly string _client;

    public PrivateEvent(string client, string hall, DateTime start, int minutes) : base(hall, start, minutes)
    {
        _client = client;
    }

    public static PrivateEvent Rental(string client, string hall, DateTime start)
    {
        return new PrivateEvent(client, hall, start, 120);
    }
```

**Uruchom:** test zielony.
**Co powiedzieć:** dołączamy klasy pojedynczo, z testem po każdej. Konstruktory nie są dziedziczone, więc każdą publiczną sygnaturę i fabrykę `Rental(...)` zachowujemy świadomie.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 1 2`

### Krok 3: Wspólny algorytm w nadklasie

**W IDE:** w `HallPlanner` zaznacz warunek kolizji, Extract Method ⌥⌘M, potem przenieś metodę do `HallBooking` jako metodę instancji `Overlaps(HallBooking other)` (F6 Move, albo ręcznie: wytnij i wklej do klasy, parametr `a` staje się `this`). Dodaj abstrakcyjne `Name` (najpierw `Name` w obu podklasach, potem Pull Members Up z "Make abstract"). Zastąp trzy pętle jedną po `List<HallBooking>`.
**Po:**

```csharp
var bookings = new List<HallBooking>(screenings);
bookings.AddRange(events);
...
if (a.Overlaps(b))
{
    result.Add(a.Name + " x " + b.Name);
}
```

**Uruchom:** test zielony, w tym przypadek graniczny "koniec 20:00 i start 20:00 to nie konflikt".
**Co powiedzieć:** dopiero wspólny typ pozwolił usunąć trzy kopie algorytmu. Publiczna sygnatura `Conflicts(IReadOnlyList<Screening>, IReadOnlyList<PrivateEvent>)` została - klienci niczego nie zauważyli.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

`HallBooking` jest abstrakcyjna, ma prywatny stan i niewirtualne `Overlaps`. Podklasy dostarczają tylko nazwę. `S04SolutionTest` sprawdza nowy nadtyp i symetrię `Overlaps` między seansem a wynajmem.

### Pułapki

- Nowy poziom zmienia `BaseType`, typ deklarujący właściwości i założenia mapperów (EF Core, System.Text.Json). Encja z nową klasą bazową to migracja modelu (TPH/TPT, dyskryminator), nie refaktoryzacja.
- Extract Superclass potrafi zrobić pola `protected` - to zaproszenie do omijania walidacji.
- Przeniesienie całego `Screening` naraz zamiast dołączania klas pojedynczo utrudnia znalezienie błędu.

### Pytanie do sali

Czy `HallBooking` powinna być klasą abstrakcyjną, czy interfejsem z domyślną metodą `Overlaps`? Co przemawia za każdą opcją?

## Scena s05. Extract Subclass - premiera z gościem

**Temat ze slajdów:** 6. Extract Subclass
**Namespace:** `Training.Workshop.M5.S05ExtractSubclass` · **Test:** `scripts/warsztat.sh --lang cs test m5/s05`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Screening` ma flagę `_premiere` i pole `_guest`, które dla zwykłego seansu jest zawsze `null`, a cena i opis powtarzają `if (_premiere)`. Najpierw wprowadzamy fabryki, potem podklasę `PremiereScreening`, przenosimy do niej zachowanie i stan, a na końcu usuwamy flagę.

**Zasada:** Extract Subclass tworzy podklasę dla stabilnego podzbioru instancji, który ma dodatkowy stan lub zachowanie. Wariant musi być stały przez całe życie obiektu - rola zmienna w czasie to State lub Strategy, a wiele niezależnych osi to kompozycja.

**Efekt:** Nie ma już `if (_premiere)` ani pola `null`, a `Programme` nie zmienił się od kroku 1, bo tworzy obiekty przez fabryki. Ruch celowo zmienia klasę runtime premier, co widzą `GetType()`, `Equals`, ORM i serializacja.

**Różnica względem Javy:** C# nie ma `sealed class ... permits`. Port zamyka hierarchię inaczej: konstruktor bazy jest `private protected` (dziedziczyć można tylko w tym samym assembly), a jedyna podklasa `PremiereScreening` jest `sealed` i ma konstruktor `internal` (odpowiednik javowego konstruktora pakietowego). `Screening` przestaje być `sealed`, a `Price()` i `Describe()` stają się `virtual` dopiero w krokach, w których podklasa je nadpisuje. Test zamiast `getPermittedSubclasses()` sprawdza `IsFamilyAndAssembly` konstruktorów i listę podtypów `Screening` w assembly.

### Co widzimy

`Screening` ma flagę `_premiere` i pole `_guest`, które ma sens tylko dla premier (dla zwykłego seansu jest `null`). Cena i opis powtarzają `if (_premiere)`. Premiera to stały wariant w całym życiu obiektu - kandydat na podklasę, nie na State.

```csharp
public Screening(string title, string format, bool premiere, string? guest)
...
return _premiere ? @base.Plus(Money.Of("15.00")) : @base;
```

Reguła sceny: premiera kosztuje 15.00 więcej niż zwykły seans w tym samym formacie.

### Krok 1: Replace Constructor with Factory Method

**W IDE:** na konstruktorze ⌃T > Replace Constructor with Factory Method, nazwa `Regular`. Potem ręcznie ustaw jej parametry (`title`, `format`) i dodaj drugą fabrykę `Premiere(title, format, guest)`. W `Programme` wybierz fabrykę w zależności od `guest`. Konstruktor zrób `private`.
**Po:**

```csharp
public static Screening Regular(string title, string format) { ... }
public static Screening Premiere(string title, string format, string guest) { ... }
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s05` - 18 testów zielonych.
**Co powiedzieć:** najpierw punkty tworzenia. Fabryka to jedyne miejsce, które za chwilę wybierze klasę runtime, a klient już nie przekazuje flagi i `null`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 0 1`

### Krok 2: Extract Subclass

**W IDE:** Rider nie ma automatu Extract Subclass - ręcznie. Usuń `sealed` z `Screening`, konstruktor zmień z `private` na `private protected`. Utwórz `sealed class PremiereScreening : Screening` z konstruktorem `internal` wołającym `base(title, format, true, guest)`. Fabryka `Premiere(...)` zwraca `new PremiereScreening(...)`.
**Po:**

```csharp
public class Screening
{
    ...
    private protected Screening(string title, string format, bool premiere, string? guest) { ... }
}

public sealed class PremiereScreening : Screening
{
    internal PremiereScreening(string title, string format, string guest) : base(title, format, true, guest)
    {
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podklasa jest pusta, logika się nie zmieniła - zmieniła się tylko klasa runtime obiektów premierowych. `private protected` na konstruktorze i `sealed` na podklasie mówią wprost, że innych wariantów nie ma: nikt spoza assembly nie odziedziczy po `Screening`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 1 2`

### Krok 3: Push Down - opis i pole _guest

**W IDE:** w bazie oznacz `Describe()` jako `virtual`, w `PremiereScreening` Generate ⌘N > Overriding Members > `Describe()` i przenieś tam gałąź premierową. Następnie ⌃T > Push Members Down na polu `_guest` w `Screening` (albo ręcznie: pole do podklasy, parametr `guest` konstruktora bazy usunięty przez Change Signature ⌘F6).
**Po:**

```csharp
public override string Describe()
{
    return base.Describe() + " - premiera, gość: " + _guest;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** najpierw zachowanie, potem stan - jak w Push Down ze sceny s03. Zwykły seans nie ma już pola, które zawsze było `null`, a w podklasie `_guest` jest `string`, nie `string?`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 2 3`

### Krok 4: Usunięcie flagi _premiere

**W IDE:** w bazie oznacz `Price()` jako `virtual`, w `PremiereScreening` override `Price()` = `base.Price().Plus(PremiereSurcharge)`, w bazie usuń dopłatę z `Price()`. Potem Safe Delete ⌘⌦ na polu `_premiere` i Change Signature ⌘F6 na konstruktorze bazy.
**Po:**

```csharp
public override Money Price()
{
    return base.Price().Plus(PremiereSurcharge);
}
```

**Uruchom:** test zielony. `S05SolutionTest` sprawdza klasę runtime zwracaną przez fabryki, brak pól `_guest`/`_premiere` w bazie, konstruktor `private protected` i to, że `PremiereScreening` jest jedynym, zamkniętym (`sealed`) podtypem.
**Co powiedzieć:** żadnego `if (_premiere)`. Klient `Programme` nie zmienił się od kroku 1 - dzięki fabrykom cała ekstrakcja była dla niego niewidoczna.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s05 3 4`

### Rozwiązanie i uzasadnienie

`Screening` zna tylko zwykły seans, `PremiereScreening` dodaje gościa i dopłatę. Hierarchia jest zamknięta w assembly (konstruktor `private protected`, podklasa `sealed`), więc wszystkie warianty seansu są znane w jednym miejscu. Kompilator C# nie sprawdzi jednak wyczerpania `switch` po klasach - wraca to w scenie s13.

### Pułapki

- Extract Subclass **celowo zmienia klasę runtime**: `GetType()`, `Equals` oparte na `GetType()`, dyskryminator EF Core, JSON z polem typu (`$type`), serializacja.
- Rola zmienna w czasie życia (seans, który "staje się" premierą po ogłoszeniu gościa) to State albo Strategy, nie podklasa.
- Kilka niezależnych osi (premiera, maraton, seans dla szkół) daje eksplozję podklas - wtedy kompozycja.

### Pytanie do sali

Kino zaczyna ogłaszać gości tydzień po dodaniu seansu do repertuaru. Czy podklasa nadal jest dobrym modelem?

## Scena s06. Extract Interface - rola koszyka i metoda domyślna

**Temat ze slajdów:** 7.1-7.3. Extract Interface; 7.4. Metody domyślne
**Namespace:** `Training.Workshop.M5.S06ExtractInterface` · **Test:** `scripts/warsztat.sh --lang cs test m5/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Cart` ma dwie listy, dwa `Add(...)` i dwie kopie liczenia VAT dla biletów i przekąsek. Wydzielamy interfejs roli `IPriceable` tylko z tym, czego używa koszyk, przestawiamy na niego klienta i dodajemy domyślną metodę interfejsu `VatAmount()`.

**Zasada:** Extract Interface wydziela rolę określonej grupy klientów, a nie kopię całego publicznego API klasy. Implementacja interfejsu wymusza sygnatury, nie zachowanie. Metoda domyślna musi być poprawna dla każdej implementacji i korzystać wyłącznie z operacji kontraktu.

**Efekt:** Nowy rodzaj pozycji to nowa implementacja bez zmian w koszyku, a VAT liczy się w jednym miejscu. Zmiana parametru z klasy na interfejs jest zgodna źródłowo, ale nie binarnie - w bibliotece zostawilibyśmy stare przeciążenie.

**Różnica względem Javy:** interfejs ma w C# nazwę z prefiksem `I` i właściwości `Price` oraz `VatPercent`. Javowa metoda `default` to domyślna metoda interfejsu (default interface method, C# 8). W odróżnieniu od Javy jest ona widoczna **tylko przez typ interfejsu**: `glasses.VatAmount()` na zmiennej typu klasy się nie skompiluje, trzeba `((IPriceable)glasses).VatAmount()`. Zamiast `isDefault()` test sprawdza, że metoda interfejsu nie jest abstrakcyjna.

### Co widzimy

`Cart` przyjmuje bilety i przekąski z baru, ma dla nich dwie listy, dwa przeciążenia `Add(...)` i dwie kopie liczenia VAT (bilety 8%, bar 23%). Koszyk używa z obu klas tylko `Price` i `VatPercent`.

```csharp
public void Add(Ticket ticket)
{
    _tickets.Add(ticket);
}

public void Add(Snack snack)
{
    _snacks.Add(snack);
}
```

### Krok 1: Extract Interface z perspektywy klienta

**W IDE:** na `Ticket` ⌃T > Extract Interface, nazwa `IPriceable`, zaznacz **tylko** `Price` i `VatPercent`. Nie zamieniaj jeszcze użyć na interfejs - klientów przeniesiemy osobno. W `Snack` dopisz `: IPriceable` (sygnatury już pasują).
**Po:**

```csharp
public interface IPriceable
{
    Money Price { get; }

    int VatPercent { get; }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s06` - 19 testów zielonych.
**Co powiedzieć:** interfejs to rola jednej grupy klientów, a nie kopia publicznego API. `Title`, `Seat` i `Name` koszyka nie obchodzą.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 0 1`

### Krok 2: Klient na rolę

**W IDE:** w `Cart` zastąp dwie listy jedną `List<IPriceable>`, dwa `Add` jednym `Add(IPriceable)` (Safe Delete drugiego przeciążenia), dwie pętle jedną. ⌃T > Use Base Type where Possible pomoże przy typach zmiennych.
**Po:**

```csharp
private readonly List<IPriceable> _items = [];

public void Add(IPriceable item)
{
    _items.Add(item);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** kod klienta `cart.Add(ticket)` kompiluje się dalej, więc to zmiana zgodna źródłowo. Binarnie nie: w metadanych stare assembly odwołuje się do `Add(Ticket)`, a tej sygnatury już nie ma. W bibliotece zostawilibyśmy stare przeciążenie delegujące.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 1 2`

### Krok 3: Metoda domyślna VatAmount()

**W IDE:** w `Cart` zaznacz wyrażenie liczące VAT pozycji, Extract Method ⌥⌘M, potem przenieś metodę do `IPriceable` jako metodę z ciałem (ręcznie: wytnij i wklej do interfejsu, `item.` znika, bo w interfejsie to `this`). Implementacji nie dotykasz.
**Po:**

```csharp
Money VatAmount()
{
    var rate = (decimal)VatPercent;
    return new Money(Math.Round(Price.Amount * rate / (rate + 100), 2, MidpointRounding.AwayFromZero));
}
```

**Uruchom:** test zielony. `S06SolutionTest` dodaje nową implementację (okulary 3D) spoza pierwotnej hierarchii, która dostaje `VatAmount()` za darmo.
**Co powiedzieć:** metoda domyślna używa wyłącznie operacji kontraktu, więc jest poprawna dla każdej implementacji. Nowy członek abstrakcyjny w opublikowanym interfejsie złamałby każdą istniejącą implementację - źródłowo i binarnie (stare assembly z implementacją dostałoby `TypeLoadException`).
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

`Cart` zależy tylko od `IPriceable`. Nowy rodzaj pozycji (okulary 3D, voucher) to nowa implementacja bez zmian w koszyku.

### Pułapki

- Implementacja interfejsu wymusza sygnatury, nie zachowanie. Wszystkie implementacje powinny przejść ten sam test kontraktowy.
- Metoda klasy o tej samej sygnaturze wygrywa z domyślną metodą interfejsu. Konflikt dwóch niespokrewnionych interfejsów z tą samą metodą domyślną trzeba rozstrzygnąć jawnie w klasie.
- Domyślna metoda jest widoczna tylko przez typ interfejsu - klient pracujący na `Ticket` jej nie zobaczy.
- Metoda domyślna nie zastąpi `Equals`/`GetHashCode` i nie robi sekwencji wywołań atomowej.
- Zmiana parametru z klasy na interfejs zmienia sygnaturę w metadanych (scena s15).

### Pytanie do sali

Czy `VatPercent` w ogóle powinno być częścią roli, skoro stawka zależy od kategorii towaru, a nie od obiektu?

## Scena s07. Collapse Hierarchy - sala IMAX

**Temat ze slajdów:** 8. Collapse Hierarchy
**Namespace:** `Training.Workshop.M5.S07CollapseHierarchy` · **Test:** `scripts/warsztat.sh --lang cs test m5/s07`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ImaxHall` nie ma własnego stanu, jej override'y tylko wołają `base`, a jedyna różnica to reguła VIP w konstruktorze. Usuwamy puste override'y, przenosimy regułę do fabryki `Hall.Imax(...)` i kasujemy podklasę.

**Zasada:** Collapse Hierarchy scala poziomy, gdy rozróżnienie nie jest już kontraktem, wariantem ani punktem rozszerzenia. Przed usunięciem typu trzeba sprawdzić, czy nie jest markerem, typem w konfiguracji albo kontraktem DI.

**Efekt:** Zostaje jedna klasa `sealed` `Hall` z nazwaną fabryką, a wiedza o sali IMAX nie ginie. Usunięcie publicznej klasy łamie klientów, więc w bibliotece `ImaxHall` zostałaby na jedno wydanie jako przestarzały typ zgodności.

**Różnica względem Javy:** `Capacity` i `Describe()` są w `Hall` jawnie `virtual`, bo `ImaxHall` je nadpisuje. W kroku 3 `sealed class Hall` wymaga usunięcia `virtual` - nowy członek wirtualny w klasie `sealed` to błąd CS0549. `@Deprecated(forRemoval = true)` to w C# atrybut `[Obsolete]`, a brak klasy test sprawdza przez `Assembly.GetType(...) == null`.

### Co widzimy

`ImaxHall : Hall` nie ma własnego stanu. Jej override'y tylko wołają `base`, a jedyna różnica to reguła w konstruktorze: VIP w dwóch ostatnich rzędach. Nikt nie sprawdza `is ImaxHall`.

```csharp
public ImaxHall(string name, int rows, int seatsPerRow) : base(name, rows, seatsPerRow, rows - 1)
{
}

public override int Capacity => base.Capacity;

public override string Describe()
{
    return base.Describe();
}
```

### Krok 1: Usunięcie override'ów wołających tylko base

**W IDE:** Rider podkreśla je inspekcją "Redundant overriding member". ⌥⏎ > Remove redundant member, albo Safe Delete ⌘⌦.
**Po:**

```csharp
public class ImaxHall : Hall
{
    public ImaxHall(string name, int rows, int seatsPerRow) : base(name, rows, seatsPerRow, rows - 1)
    {
    }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s07` - 22 testy zielone.
**Co powiedzieć:** zostało jedno: reguła tworzenia. To nie jest kontrakt, wariant zachowania ani punkt rozszerzenia.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 0 1`

### Krok 2: Reguła tworzenia do fabryki w Hall

**W IDE:** w `Hall` dodaj statyczną fabrykę `Imax(name, rows, seatsPerRow)` (⌃T > Replace Constructor with Factory Method na konstruktorze `ImaxHall` generuje fabrykę w `ImaxHall` - przenieś ją do `Hall`, zmień typ zwracany i `new ImaxHall` na `new Hall(..., rows - 1)`). W `HallCatalog` zamień `new ImaxHall(...)` na `Hall.Imax(...)`.
**Po:**

```csharp
public static Hall Imax(string name, int rows, int seatsPerRow)
{
    return new Hall(name, rows, seatsPerRow, rows - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wiedza z konstruktora podklasy ma teraz nazwę w klasie bazowej. `ImaxHall` nie ma już użyć.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 1 2`

### Krok 3: Safe Delete ImaxHall

**W IDE:** Safe Delete ⌘⌦ na `ImaxHall` (przejdzie bez ostrzeżeń, bo nie ma użyć). `Hall` oznacz `sealed` i usuń `virtual` z `Capacity` i `Describe()` - Rider sam podpowie to ⌥⏎, bo w klasie `sealed` członek wirtualny się nie skompiluje. Rider nie ma odpowiednika javowego Inline Superclass (który zresztą scala w drugą stronę i zostawia nazwę podklasy). Tu klienci używają `Hall`, więc zostaje `Hall`.
**Po:**

```csharp
public sealed class Hall
{
    ...
    public int Capacity => _rows * _seatsPerRow;
    ...
    public string Describe() { ... }
}
```

**Uruchom:** test zielony. `S07SolutionTest` sprawdza, że klasy `ImaxHall` nie ma, a `Hall` jest `sealed`.
**Co powiedzieć:** zostaje nazwa, której używają klienci. W bibliotece `ImaxHall` zostałaby na jedno wydanie jako typ zgodności oznaczony `[Obsolete]`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Jedna klasa `sealed` `Hall` z nazwaną fabryką. Rozróżnienie IMAX nie było kontraktem, tylko regułą tworzenia.

### Pułapki

- Pusty typ bywa **markerem** (np. `is ImaxHall` w konfiguracji projektora), punktem rozszerzenia albo kontraktem DI. Przed Collapse szukaj też w konfiguracji, atrybutach, rejestracjach kontenera i skanowaniu assembly.
- Usunięcie publicznej klasy łamie klientów - źródłowo i binarnie (`TypeLoadException` w starych assembly).
- Scalenie w złym kierunku (zostaje nazwa podklasy zamiast nazwy używanej przez klientów) - sprawdź kierunek przed ruchem.

### Pytanie do sali

Za pół roku sale IMAX dostaną inną politykę zwrotów. Czy wtedy przywrócicie podklasę, czy dodacie pole?

## Scena s08. Replace Inheritance with Composition - licznik kliknięć

**Temat ze slajdów:** 9.1-9.4. Replace Inheritance with Composition, procedura i pułapki delegowania
**Namespace:** `Training.Workshop.M5.S08Composition` · **Test:** `scripts/warsztat.sh --lang cs test m5/s08`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `SeatSelection` dziedziczy po `HashSet<string>` tylko dla wygody i liczy kliknięcia, ale metody `HashSet` nie są wirtualne, więc licznik da się dołożyć tylko przez ukrycie (`new`). Hurtowy wybór wywołany przez typ bazowy albo interfejs trafia prosto do `HashSet` i nie liczy się wcale. Zastępujemy dziedziczenie delegatem i zamykamy go za wąską fasadą `sealed`.

**Zasada:** Replace Inheritance with Composition stosujemy, gdy dziedziczenie służy tylko do ponownego użycia kodu, a podklasa nie jest zastępowalnym wariantem. Dziedzicząc po cudzej klasie, wiążemy się z jej szczegółami: które metody są wirtualne i które wołają inne metody na `this` (self-use) - kontrakt biblioteki tego nie gwarantuje. Delegowanie ma własne pułapki: wyciek delegata, inny obiekt blokady i utrata `Equals`/`GetHashCode`.

**Efekt:** Zachowanie świadomie się zmienia: hurtowy wybór liczy każde miejsce raz niezależnie od typu referencji, a klient dostaje niemodyfikowalną kopię listy w kolejności wyboru. Kosztem jest utrata przypisywalności do `ISet<string>` oraz operacji `Remove`, `Clear` i `IntersectWith` - to nie jest zgodny zamiennik.

**Różnica względem Javy:** to adaptacja. W .NET nie ma kolekcji, w której publiczna metoda hurtowa woła publiczną wirtualną metodę pojedynczą (`HashSet<T>` i `List<T>` nie mają metod wirtualnych, a `ArrayList.AddRange` nie woła `Add`), więc javowej pułapki self-use (`addAll` woła `add`, dwa miejsca - cztery kliknięcia) nie da się odtworzyć. Port pokazuje pułapkę specyficzną dla C#: "nadpisanie" przez `new` to ukrycie, wiązane statycznie według typu referencji, więc wywołanie przez `HashSet<string>` albo `ISet<string>` omija licznik - dwa miejsca, **zero** kliknięć. `addAll` to we wszystkich wariantach `UnionWith` (Start musi ukryć metodę `HashSet`). .NET nie ma `LinkedHashSet`, więc fasada w kroku 2 trzyma `HashSet` (unikalność) i `List` (kolejność wyboru).

### Co widzimy

`SeatSelection : HashSet<string>` tylko po to, by mieć `Add`/`Contains` za darmo, i liczy kliknięcia klienta do analityki. Metody `HashSet` nie są wirtualne, więc `override` się nie skompiluje - autor dopisał `new`, żeby uciszyć ostrzeżenie o ukryciu:

```csharp
public new void UnionWith(IEnumerable<string> seats)
{
    var more = seats.ToList();
    _clicks += more.Count;
    base.UnionWith(more);
}
```

Działa, dopóki ktoś woła `selection.UnionWith(...)` przez zmienną typu `SeatSelection`. Wystarczy przekazać obiekt do metody przyjmującej `ISet<string>` albo przypisać do `HashSet<string>`, a wywołanie trafi do metody `HashSet` i ominie licznik. `S08SolutionTest.StartLosesBulkSelectionBecauseNewHidesInsteadOfOverriding` dokumentuje: 2 miejsca, 0 kliknięć. Drugi test `Start...` pokazuje, że odziedziczone `Clear()` (przez `ISet<string>`) też omija licznik.

### Krok 1: Replace Inheritance with Delegation

**W IDE:** ręcznie z pomocą generatora: usuń `: HashSet<string>`, dodaj pole delegata `private readonly HashSet<string> _seats = [];`, w `Add` i `UnionWith` usuń `new` i zamień `base.` na `_seats.`. Metody `Contains` i `Count` dodaj przez Generate ⌘N > Delegating Members (delegat `_seats`). Dodaj też właściwość wydającą delegata, `SeatSet` - żeby pokazać pułapkę.
**Po:**

```csharp
private readonly HashSet<string> _seats = [];

public void UnionWith(IEnumerable<string> seats)
{
    var more = seats.ToList();
    _clicks += more.Count;
    _seats.UnionWith(more);    // metoda delegata, nie da się jej ominąć przez typ bazowy
}

public HashSet<string> SeatSet => _seats;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s08` - na snapshotach 14 testów zielonych. Testy `Start...` sięgają po API zbioru przez `object` (`Assert.IsAssignableFrom<HashSet<string>>((object)selection)`), więc projekt testów kompiluje się dla każdego kroku w Start, a po naprawie na żywo te dwa testy robią się czerwone - to dowód, że Start nie jest już zbiorem.
**Co powiedzieć:** klasa nie ma już typu bazowego ani interfejsu, przez który dałoby się ominąć licznik. Ale wygenerowana właściwość wydaje delegata - `SeatSet.Add("Z1")` omija licznik (test `GeneratedGetterLeaksTheDelegate`).
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s08 0 1`

### Krok 2: Encapsulate Collection i wąska fasada

**W IDE:** Safe Delete ⌘⌦ na `SeatSet`, dodaj pole `_order` (`List<string>`, kolejność wyboru) i właściwość `Seats` zwracającą `_order.ToList().AsReadOnly()`. Klasę oznacz `sealed`. `UnionWith` przepisz na pętlę po własnym `Add` - self-use jest teraz bezpieczne, bo klasa jest `sealed` i sami kontrolujemy oba końce.
**Po:**

```csharp
public void UnionWith(IEnumerable<string> seats)
{
    foreach (var seat in seats)
    {
        Add(seat);
    }
}

public IReadOnlyList<string> Seats => _order.ToList().AsReadOnly();
```

**Uruchom:** test zielony. `SolutionIsNarrowFacadeWithDefensiveCopy`: modyfikacja zwróconej listy rzuca `NotSupportedException`, a `SeatSelection` nie jest `ISet<string>`.
**Co powiedzieć:** świadomie tracimy przypisywalność do `ISet<string>`, `Remove`/`Clear`/`IntersectWith` i `SetEquals` zbioru. Tę listę trzeba wypisać i uzgodnić z klientami - to nie jest zgodny zamiennik publicznego zbioru.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s08 1 2`

### Rozwiązanie i uzasadnienie

Klasa `sealed` z prywatnym zbiorem, jawnym API i defensywną kopią. Licznik nie zależy od tego, przez jaki typ ktoś trzyma obiekt, ani od szczegółów implementacji `HashSet`.

### Pułapki

- `new` na metodzie to ukrycie, nie nadpisanie: wynik zależy od typu referencji, a kolekcja przekazana jako `ISet<T>`, `ICollection<T>` albo `IEnumerable<T>` zawsze używa metod bazy. Kompilator o tym nie ostrzega - `new` służy właśnie do uciszenia ostrzeżenia.
- Self-use w bibliotece nie jest częścią kontraktu - przy dziedziczeniu po klasie z metodami wirtualnymi (np. `Collection<T>` z `InsertItem`) wynik zależy od tego, która metoda woła którą.
- Metoda delegata zwracająca obiekt (np. `GetEnumerator()`, `AsReadOnly()`, widok kolekcji) zwraca obiekt delegata, nie wrappera - zmiany omijają logikę.
- `lock (this)` w wrapperze blokuje inny obiekt niż delegat.
- Znikają odziedziczone `Equals`/`GetHashCode` i `SetEquals` - zmienia się zachowanie w `HashSet<SeatSelection>` i w porównaniach.

### Pytanie do sali

Które testy wykryłyby, że wrapper przypadkiem zwraca delegata? Spy, callback, a może test tożsamości `this`?

## Scena s09. Overriding a overloading - pułapka po Extract Superclass

**Temat ze slajdów:** 2.1. Overriding i dynamiczna dyspozycja; 2.2-2.3. Overloading - wybór statyczny
**Namespace:** `Training.Workshop.M5.S09Overloading` · **Test:** `scripts/warsztat.sh --lang cs test m5/s09`
**Czas:** ~10 min

### W skrócie

**Co robimy:** Po Extract Superclass `PriceList` ma przeciążenia `Price(Ticket)` i `Price(StudentTicket)`, a klient z `IReadOnlyList<Ticket>` zawsze trafia w wersję bazową, więc student płaci pełną cenę. Zamieniamy przeciążenie na wirtualną właściwość `DiscountPercent` i poprawiamy `Equals(Ticket)` na prawdziwe `override Equals(object?)`.

**Zasada:** Overriding działa dynamicznie: CLR wybiera implementację według klasy obiektu i rzutowanie tego nie wyłącza. Overloading wybiera kompilator według typu deklarowanego, a decyzja zostaje zapisana w IL klienta. Reguła bezpieczeństwa: zachowanie zależne od typu umieszczamy w override - w C# słowo `override` jest obowiązkowe, więc kompilator pilnuje, że naprawdę coś nadpisujemy.

**Efekt:** Zachowanie świadomie się zmienia: student dostaje zniżkę niezależnie od typu referencji, a `Contains` znajduje równy bilet. Test równoważności tego nie wykrył, bo testował przez typ konkretny - dlatego testy przez typ bazowy są obowiązkowe.

**Różnica względem Javy:** pułapka przeciążeń jest identyczna. Przy `Equals`: `public bool Equals(Ticket other)` bez `override` to przeciążenie, a `List<T>.Contains` używa `EqualityComparer<T>.Default`, który dla typu bez `IEquatable<T>` woła `Equals(object)` - przeciążenia nie widzi. Rozwiązanie to `override Equals(object?)` z `GetType()` i `GetHashCode` z `HashCode.Combine`. `discountPercent()` to wirtualna właściwość `DiscountPercent`.

### Co widzimy

Stan po Extract Superclass: `StudentTicket : Ticket`. `PriceList` ma dwa przeciążenia `Price(Ticket)` i `Price(StudentTicket)`. Dopóki klient miał `List<StudentTicket>`, działało. Po migracji na `IReadOnlyList<Ticket>` ten sam tekst `_priceList.Price(ticket)` wybiera `Price(Ticket)`, bo przeciążenie wybiera kompilator po typie deklarowanym. Student płaci pełną cenę.

```csharp
public Money Total(IReadOnlyList<Ticket> tickets)
{
    ...
    total = total.Plus(_priceList.Price(ticket));   // zawsze Price(Ticket)
```

Druga pułapka: `public bool Equals(Ticket other)` to przeciążenie, nie override - `Contains` go nie widzi.

### Krok 1: Replace Overloading with Overriding

**W IDE:** w `Ticket` dodaj `public virtual int DiscountPercent => 0;`, w `StudentTicket` Generate ⌘N > Overriding Members > `DiscountPercent` = 25. `PriceList.Price(Ticket)` liczy z `ticket.DiscountPercent`. Safe Delete ⌘⌦ na `Price(StudentTicket)`.
**Po:**

```csharp
public Money Price(Ticket ticket)
{
    return ticket.BasePrice.Minus(ticket.BasePrice.Percent(ticket.DiscountPercent));
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s09` - `StartPicksOverloadByDeclaredType` czerwony po naprawie Start (pułapka usunięta), `OverridingDispatchesOnRuntimeClass` zielony: 58.75 zamiast 65.00.
**Co powiedzieć:** dyspozycja dynamiczna wybiera implementację według klasy obiektu, rzutowanie na nadtyp tego nie wyłącza. Przeciążenie to decyzja kompilatora, zapisana na stałe w IL klienta.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s09 0 1`

### Krok 2: override Equals(object?) i GetHashCode

**W IDE:** usuń `Equals(Ticket)`, Generate ⌘N > Equality Members, pola `_title` i `_basePrice`, porównanie typów przez `GetType()` (Rider wygeneruje też `GetHashCode`; bez niego kompilator ostrzega CS0659, u nas błąd).
**Po:**

```csharp
public override bool Equals(object? other)
{
    return other != null && GetType() == other.GetType()
        && _title == ((Ticket)other)._title && _basePrice.Equals(((Ticket)other)._basePrice);
}

public override int GetHashCode()
{
    return HashCode.Combine(_title, _basePrice);
}
```

**Uruchom:** test zielony - `AlreadyInCart` znajduje równy bilet.
**Co powiedzieć:** `override` zamienia cichy błąd przeciążenia w błąd kompilacji: `override bool Equals(Ticket other)` się nie skompiluje, bo nie ma czego nadpisać. W C# to słowo jest obowiązkowe przy każdym nadpisaniu - także po Pull Up, gdy metoda w podklasie może nagle nadpisać albo przestać nadpisywać.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s09 1 2`

### Rozwiązanie i uzasadnienie

Zniżka jest zachowaniem biletu (override), a nie decyzją kalkulatora (overload). Jedno `Price(Ticket)` działa poprawnie dla każdego typu deklarowanego.

### Pułapki

- Po Pull Up / Extract Superclass ten sam kod źródłowy może wybrać inne przeciążenie - testy przez typ bazowy to wykrywają, testy przez typ konkretny nie (`S09EquivalenceTest` jest zielony nawet dla Start!).
- Stare skompilowane assembly klienta woła dawną sygnaturę przeciążenia, nawet gdy dodamy nowe, "lepsze".
- `Equals` porównujące `is` zamiast `GetType()` łamie symetrię między bazą a podklasą.
- Implementacja `IEquatable<Ticket>` bez `override Equals(object)` daje dwa różne pojęcia równości: `List.Contains` użyje jednego, `ArrayList` albo `object.Equals` - drugiego.

### Pytanie do sali

Dlaczego test równoważności tej sceny przechodzi dla Start, mimo że Start liczy źle?

## Scena s10. Ukrywanie pól i metod static

**Temat ze slajdów:** 2.3. Pola nie są polimorficzne; 2.1. `static` jest ukrywana
**Namespace:** `Training.Workshop.M5.S10FieldHiding` · **Test:** `scripts/warsztat.sh --lang cs test m5/s10`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `StudentTicket` redeklaruje pole `Type` i statyczne `Category()` z `Ticket` (ze słowem `new`), więc `Label()` dla studenta zwraca `"BILET: NORMAL"`. Zastępujemy dwa sloty jednym prywatnym polem ustawianym przez konstruktor bazy, a `Category()` robimy wirtualną metodą instancji.

**Zasada:** Pola i metody `static` nie są polimorficzne: są wiązane w czasie kompilacji według typu, w którym stoi odwołanie. Ukryte pole to dwa niezależne sloty w jednym obiekcie. Reguła: stan przekazujemy przez `base(...)`, a zachowanie zależne od obiektu musi być wirtualną metodą instancji.

**Efekt:** Zachowanie świadomie się zmienia: `Label()` studenta zwraca `"BILET ULGOWY: STUDENT"` bez względu na typ referencji. Znika też ryzyko, że Pull Up Field bez usunięcia deklaracji w podklasie po cichu stworzy ukrycie.

**Różnica względem Javy:** C# wymaga słowa `new` przy ukrywaniu pola i metody statycznej (bez niego ostrzeżenie CS0108, u nas błąd), więc pułapka jest w kodzie "podpisana". Ale `new` tylko ucisza kompilator - zachowanie jest takie samo jak w Javie: dwa sloty, wiązanie statyczne. Pole to publiczne pole `Type` (jak javowe pole pakietowe), a `category()` to `Category()`.

### Co widzimy

`StudentTicket` deklaruje pole `Type` i statyczne `Category()` o tych samych nazwach co `Ticket`. Wygląda na nadpisanie, ale to ukrycie. `Label()` jest skompilowane w `Ticket`, więc dla studenta zwraca `"BILET: NORMAL"`.

```csharp
public class StudentTicket : Ticket
{
    public new string Type = "STUDENT";

    public static new string Category()
    {
        return "BILET ULGOWY";
    }
}

var student = new StudentTicket();
Ticket sameObject = student;
student.Type      // "STUDENT"
sameObject.Type   // "NORMAL" - drugi slot w tym samym obiekcie
```

Testy `Start...` czytają oba sloty refleksją, żeby projekt testów kompilował się także po naprawie na żywo.

### Krok 1: Jedno pole zamiast dwóch slotów

**W IDE:** w `Ticket` na polu `Type` ⌃T > Encapsulate Field (pole `private readonly string _type`, właściwość tylko do odczytu `Type`), dodaj konstruktor `protected Ticket(string type)` i `public Ticket() : this("NORMAL")`. W `StudentTicket` usuń pole (Safe Delete) i dodaj konstruktor `public StudentTicket() : base("STUDENT")`.
**Po:**

```csharp
private readonly string _type;

public Ticket() : this("NORMAL")
{
}

protected Ticket(string type)
{
    _type = type;
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s10` - testy `Start...` czerwone po naprawie Start, `Step1FixesFieldButStaticIsStillHidden` zielony: `"BILET: STUDENT"`.
**Co powiedzieć:** pole ukryte to dwa niezależne sloty. Który zobaczysz, zależy od typu referencji. Stan przekazujemy przez konstruktor, a nie redeklarujemy - `new` na polu to prawie zawsze błąd projektu.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s10 0 1`

### Krok 2: Category() jako metoda instancji

**W IDE:** w `Ticket` usuń `static` z `Category()` i dodaj `virtual` (ręcznie; Rider ma też ⌃T > Make Method Non-Static). W `StudentTicket` zamień `static new` na `override`.
**Po:**

```csharp
// Ticket
public virtual string Category()
{
    return "BILET";
}

// StudentTicket
public override string Category()
{
    return "BILET ULGOWY";
}
```

**Uruchom:** test zielony: `"BILET ULGOWY: STUDENT"` bez względu na typ referencji.
**Co powiedzieć:** metody statyczne są wiązane w czasie kompilacji według typu, w którym stoi wywołanie. Jeśli zachowanie ma zależeć od obiektu, musi być wirtualną metodą instancji, a `override` potwierdza, że coś naprawdę nadpisujemy.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s10 1 2`

### Rozwiązanie i uzasadnienie

Jedno prywatne pole, jedna polimorficzna metoda. `Label()` daje ten sam wynik dla `Ticket t = new StudentTicket()` i `StudentTicket s`.

### Pułapki

- Pull Up Field bez usunięcia deklaracji w podklasie tworzy ukrycie zamiast przeniesienia. Kompilator ostrzeże (CS0108), ale "naprawa" przez `new` utrwala dwa sloty.
- `lock (typeof(Ticket))` albo blokada na statycznym obiekcie w klasie deklarującej - Pull Up takiej metody zmienia obiekt blokady (dwie podklasy zaczynają dzielić jedną blokadę). To odpowiednik javowego `static synchronized`.
- Pola statyczne w klasach generycznych są osobne dla każdego `T` (`Ticket<A>.Count` i `Ticket<B>.Count` to dwa sloty) - Pull Up do generycznej bazy potrafi rozdzielić stan, który był wspólny.
- W C# wywołanie statycznej metody przez instancję (`ticket.Category()`) to błąd kompilacji CS0176, więc tej javowej pomyłki nie ma - ale wywołanie przez nazwę podklasy (`StudentTicket.Category()`) nadal wiąże się statycznie.

### Pytanie do sali

Gdzie w waszym kodzie są stałe `const` albo `static readonly` redeklarowane w podklasach słowem `new`? Czy ktoś czyta je przez typ bazowy?

## Scena s11. Konstruktor wołający metodę nadpisywalną

**Temat ze slajdów:** 2.4. Konstruktory i inicjalizacja; 3.1-3.3 (nie wołaj override z konstruktora, żeby umożliwić Pull Up)
**Namespace:** `Training.Workshop.M5.S11ConstructorCall` · **Test:** `scripts/warsztat.sh --lang cs test m5/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** Konstruktor `Ticket` woła wirtualne `Describe()`, a `VipTicket` czyta w nim pole, które nie jest jeszcze przypisane, więc etykieta na zawsze ma pusty salonik. Najpierw naprawiamy lokalnie inicjalizatorem pola w konstruktorze głównym (wykonuje się przed konstruktorem bazy), potem strukturalnie: etykieta liczona na żądanie.

**Zasada:** Konstruktor bazy wykonuje się przed ciałem konstruktora podklasy, więc wywołany z niego override widzi obiekt w połowie zbudowany. C# pozwala przypisać pole wcześniej - inicjalizatory pól wykonują się przed wywołaniem konstruktora bazy - ale to naprawa lokalna, o której każda podklasa musi pamiętać. Reguła: konstruktor tylko przypisuje pola i nie woła metod wirtualnych.

**Efekt:** Zachowanie świadomie się zmienia: etykieta VIP zawiera salonik, a poprawność nie zależy od kolejności inicjalizacji. Kosztem jest liczenie etykiety przy każdym wywołaniu zamiast raz w konstruktorze.

**Różnica względem Javy:** odpowiednikiem prologu konstruktora z Javy 25 (JEP 513, przypisanie pola przed `super(...)`) jest w C# konstruktor główny (primary constructor) z inicjalizatorem pola: inicjalizatory pól wykonują się **przed** konstruktorem bazy. Oczekiwany tekst pułapki to `"Miejsce K12 (VIP: )"` zamiast javowego `"... (VIP: null)"`, bo w C# `null` sklejony z napisem daje pusty napis. Analiza nullable niczego nie zgłasza: pole ma typ `string`, nie `string?` - adnotacje "kłamią" w trakcie konstrukcji. Kompilator C# nie ma odpowiednika `-Xlint:this-escape`; najbliższa jest reguła analizatora CA2214 (wspomniana w komentarzu, niewłączona w buildzie), więc scena kompiluje się bez ostrzeżeń.

### Co widzimy

Konstruktor `Ticket` zapamiętuje etykietę, wołając `Describe()`. `VipTicket` nadpisuje `Describe()` i używa pola `_lounge`, które w tym momencie jest jeszcze `null` - konstruktor bazy kończy się, zanim ciało konstruktora podklasy przypisze swoje pola.

```csharp
public Ticket(string seat)
{
    _seat = seat;
    _label = Describe();       // CA2214: Do not call overridable methods in constructors
}
```

`new VipTicket("K12", "Salonik A").Label()` zwraca `"Miejsce K12 (VIP: )"` - na zawsze, bo wynik jest w polu `readonly`.

### Krok 1: Szybka naprawa - inicjalizator pola w konstruktorze głównym

**W IDE:** na `VipTicket` ⌥⏎ > Convert to primary constructor, tak by pole `_lounge` było przypisane inicjalizatorem (`private readonly string _lounge = lounge;`), a nie w ciele konstruktora. Albo ręcznie: `class VipTicket(string seat, string lounge) : Ticket(seat)`.
**Po:**

```csharp
public class VipTicket(string seat, string lounge) : Ticket(seat)
{
    private readonly string _lounge = lounge;
    ...
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s11` - `StartSubclassSeesUninitializedField` czerwony po naprawie Start (pułapka usunięta), `FieldInitializerRunsBeforeBaseConstructor` i pozostałe zielone.
**Co powiedzieć:** działa, bo inicjalizatory pól wykonują się przed konstruktorem bazy. Ale naprawa jest lokalna i krucha - każda następna podklasa musi pamiętać, żeby nie przypisywać pól w ciele konstruktora. Wywołanie metody wirtualnej w konstruktorze `Ticket` zostaje.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s11 0 1`

### Krok 2: Konstruktor bez wywołań wirtualnych

**W IDE:** w `Ticket` Inline ⌥⌘N na polu `_label` (Replace Field with Query): `Label()` zwraca `Describe()`. W `VipTicket` przywróć zwykły konstruktor z `: base(seat)` i przypisaniem pola w ciele.
**Po:**

```csharp
public string Label()
{
    return Describe();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany. Poprawność nie zależy od kolejności inicjalizacji. Inne naprawy: przekazanie wyliczonej wartości przez parametr konstruktora albo fabryka, która woła metodę po `new`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Konstruktor bazy tylko przypisuje pola. Wszystko, co może być nadpisane, wykonuje się po zakończeniu konstrukcji.

### Pułapki

- Pull Up metody, którą ktoś potem wywoła z konstruktora bazy "bo tak wygodnie".
- Kolejność inicjalizacji w C# jest inna niż w Javie: inicjalizatory pól podklasy działają przed konstruktorem bazy, a ciało konstruktora podklasy po nim. Przeniesienie przypisania z inicjalizatora do ciała konstruktora (albo odwrotnie, np. przez akcję IDE) potrafi po cichu włączyć albo wyłączyć tę pułapkę.
- Pole z inicjalizatorem, który sam czyta stan bazy albo woła metodę wirtualną, widzi bazę jeszcze niezbudowaną.
- Podklasa nie może przypisać odziedziczonego pola `readonly` - trzeba je przekazać przez konstruktor bazy.

### Pytanie do sali

Czy włączylibyście CA2214 jako błąd (`TreatWarningsAsErrors`) w buildzie legacy? Ile ostrzeżeń byście dostali?

## Scena s12. Generyki i metody bridge

**Temat ze slajdów:** 2.5-2.8. Sygnatury po erasure i metody bridge; 10.2-10.3. Refleksja
**Namespace:** `Training.Workshop.M5.S12BridgeMethods` · **Test:** `scripts/warsztat.sh --lang cs test m5/s12`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RuleRegistry` rejestruje reguły cenowe refleksją, po typie parametru każdej metody `Apply`. Po wydzieleniu generycznej roli `IPriceRule<T>` każda reguła dostaje drugą metodę `Apply(ITicket)` - "most" z rzutowaniem do nieogólnego kontraktu - i rejestr się psuje, więc najpierw ją filtrujemy, a potem zastępujemy refleksję jawnym `TicketType`.

**Zasada:** Hierarchia generyczna potrzebuje wejścia nieogólnego: w Javie kompilator dokłada po erasure syntetyczną metodę bridge, w C# trzeba napisać nieogólny interfejs bazowy i jego implementację z rzutowaniem. W obu przypadkach źródło reguły "wygląda" na jedną metodę `Apply`, a refleksja widzi dwie. Reguła: kod iterujący po metodach klasy musi odróżniać metody kontraktu nieogólnego od właściwych, a najlepiej zastąpić go jawnym kontraktem.

**Efekt:** Rejestr nie używa refleksji, więc metody pomostowe przestają mieć znaczenie, a brak reguły kończy się czytelnym `InvalidOperationException`. Typ biletu i most są napisane raz, w kontrakcie `IPriceRule<T>`.

**Różnica względem Javy:** to adaptacja. .NET ma generyki reifikowane (bez erasure), więc kompilator C# nie generuje metod bridge. Ale C# nie ma też typów wieloznacznych (`PriceRule<?>`), więc żeby rejestr mógł trzymać reguły dla różnych biletów, generyczna rola `IPriceRule<in T>` dostaje nieogólny interfejs bazowy `IPriceRule` z `Apply(ITicket)`. Reguła implementuje go publiczną metodą z rzutowaniem (Rider: Implement missing members) - to ręczny "most", widoczny dla refleksji jak javowy bridge. Naprawa `!method.isBridge()` to w C# `!IsBridge(type, method)`, wyliczane z `GetInterfaceMap(typeof(IPriceRule))`. Wywołanie mostu z innym biletem rzuca `InvalidCastException` (Java: `ClassCastException`). W kroku 2 `TicketType` i most są jawnymi domyślnymi implementacjami nieogólnego kontraktu w `IPriceRule<T>` (z `typeof(T)`), więc reguły nie deklarują ich same - w Javie `ticketType()` jest w każdej regule. Dodatkowy test `SolutionRulesDeclareNoBridge` sprawdza, że reguły w rozwiązaniu mają jedną metodę `Apply`.

### Co widzimy

Reguły cenowe `StandardRule` i `StudentRule` nie mają wspólnego typu. `RuleRegistry` znajduje je refleksją: każda metoda o nazwie `Apply` rejestruje regułę pod typem swojego parametru.

```csharp
foreach (var method in type.GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.DeclaredOnly))
{
    if (method.Name == "Apply")
    {
        _rules[method.GetParameters()[0].ParameterType] = rule;
    }
}
```

Działa, dopóki w klasie reguły jest dokładnie jedna metoda `Apply`. Typy biletów (`ITicket`, `StandardTicket`, `StudentTicket`) leżą poza `Start` i `StepN` - są stabilnym kontraktem sceny.

### Krok 1: Extract Interface generycznej roli (i naprawa refleksji)

**W IDE:** na `StudentRule` ⌃T > Extract Interface, nazwa `IPriceRule`, metoda `Apply`. Ręcznie zrób z niego parę interfejsów: nieogólny `IPriceRule` z `Money Apply(ITicket ticket)` (tak rejestr może trzymać różne reguły) i generyczny `IPriceRule<in T> : IPriceRule where T : ITicket` z `Money Apply(T ticket)`. Reguły implementują `IPriceRule<StudentTicket>` / `IPriceRule<StandardTicket>`, a brakujące `Apply(ITicket)` dodaj przez ⌥⏎ > Implement missing members, z ciałem `return Apply((StudentTicket)ticket);`. **Uruchom test - jest czerwony:** `SupportedTypes()` zwraca też `"ITicket"`. Pokaż test `NaiveReflectionWouldRegisterBaseType`: naiwny skan widzi `["ITicket", "StudentTicket"]`. Dopisz w rejestrze `&& !IsBridge(type, method)`.
**Po:**

```csharp
// StudentRule - "most" do nieogólnego kontraktu
public Money Apply(ITicket ticket)
{
    return Apply((StudentTicket)ticket);
}

// RuleRegistry
if (method.Name == "Apply" && !IsBridge(type, method))
...
private static bool IsBridge(Type type, MethodInfo method)
{
    return typeof(IPriceRule).IsAssignableFrom(type)
        && type.GetInterfaceMap(typeof(IPriceRule)).TargetMethods.Contains(method);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s12` - 11 testów zielonych.
**Co powiedzieć:** rola generyczna potrzebuje wejścia nieogólnego, a to wejście to druga metoda `Apply` z rzutowaniem. W Javie dokłada ją kompilator i źródło jej nie pokazuje, w C# piszemy ją sami (albo generuje ją IDE) - ale refleksja w obu językach widzi dwie metody. Wywołanie mostu z niewłaściwym biletem kończy się `InvalidCastException`.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s12 0 1`

### Krok 2: Jawny kontrakt zamiast refleksji

**W IDE:** w `IPriceRule` dodaj właściwość `Type TicketType { get; }`. W `IPriceRule<T>` dopisz jawne domyślne implementacje nieogólnego kontraktu: `Type IPriceRule.TicketType => typeof(T);` i `Money IPriceRule.Apply(ITicket ticket) => Apply((T)ticket);`, a z reguł usuń ręczne mosty (Safe Delete ⌘⌦). `RuleRegistry` przyjmuje `params IPriceRule[]`, rejestruje po `TicketType` i woła `rule.Apply(ticket)` bez refleksji.
**Po:**

```csharp
public interface IPriceRule<in T> : IPriceRule
    where T : ITicket
{
    Type IPriceRule.TicketType => typeof(T);

    Money Apply(T ticket);

    Money IPriceRule.Apply(ITicket ticket) => Apply((T)ticket);
}
```

**Uruchom:** test zielony. `SolutionReportsMissingRuleExplicitly`: brak reguły to `InvalidOperationException`. `SolutionRulesDeclareNoBridge`: reguła ma jedną metodę `Apply`.
**Co powiedzieć:** zgadywanie typu z sygnatury metody zastąpiliśmy jawnym kontraktem, a rzutowanie jest w jednym miejscu. Brak reguły to czytelny `InvalidOperationException`, a nie `NullReferenceException` z refleksji.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s12 1 2`

### Rozwiązanie i uzasadnienie

`IPriceRule<T>` z nieogólnym kontraktem `IPriceRule` (`TicketType`, `Apply(ITicket)`) zaimplementowanym raz, w interfejsie. Rejestr nie używa refleksji, więc metody pomostowe nie mają znaczenia.

### Pułapki

- Metody pomostowe pojawiają się w C# wszędzie, gdzie generyczny typ implementuje też nieogólny kontrakt: `IEnumerable<T>` + `IEnumerable.GetEnumerator()`, `IComparable<T>` + `IComparable.CompareTo(object)`, `IEquatable<T>` + `Equals(object)`. Jawna implementacja interfejsu (`IEnumerable.GetEnumerator()`) jest prywatna i ma nazwę z prefiksem interfejsu - kod szukający metod po nazwie widzi co innego niż przy implementacji publicznej.
- Przeniesienie metody w hierarchii generycznej (Pull Up do bazy z innym parametrem typu) zmienia, gdzie i czy powstaje most - kod szukający metod po nazwie widzi co innego.
- Frameworki (serializatory, kontenery DI, mappery) zwykle rozumieją mapy interfejsów i jawne implementacje - własny kod legacy iterujący po `GetMethods()` często nie.

### Pytanie do sali

Gdzie w waszym systemie ktoś iteruje po `GetMethods()` i czy odróżnia metody implementujące kontrakt nieogólny od właściwych?

## Scena s13. Hierarchie sealed i wyczerpujący switch

**Temat ze slajdów:** 2.5-2.8. `sealed`, zamknięte hierarchie, wyjątek z nieobsłużonego wariantu
**Namespace:** `Training.Workshop.M5.S13Sealed` · **Test:** `scripts/warsztat.sh --lang cs test m5/s13`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Otwarta hierarchia biletów i łańcuch `is` z cichym `return 0` sprawiają, że nowy typ biletu nie dostaje zniżki bez żadnego ostrzeżenia. Zamykamy hierarchię konstruktorem `private protected` i wariantami `sealed record`, zamieniamy łańcuch na `switch` po typach z jawnym ramieniem `_ => throw new UnreachableException(...)` i dodajemy bilet dziecięcy.

**Zasada:** Zamknięta hierarchia trzyma listę wariantów w jednym miejscu, a `switch`, który wymienia każdy wariant jawnie, pozwala sprawdzić, czy obsłużono wszystkie. W C# tego sprawdzenia nie robi kompilator, tylko test (analizator) - a ramię `_` zamienia nieobsłużony wariant w głośny `UnreachableException` zamiast cichego zera. Ramię `_` zwracające wartość (odpowiednik `default`) wyłącza całą ochronę.

**Efekt:** Zachowanie świadomie się zmienia: obce typy biletów spoza assembly nie przejdą, a nowy wariant bez obsługi to czerwony test i wyjątek zamiast cichego zera. Kosztem jest zamknięcie hierarchii dla implementacji spoza assembly i ryzyko `UnreachableException` w kodzie, którego nikt nie sprawdził.

**Różnica względem Javy:** to adaptacja. C# nie ma `sealed interface ... permits` ani sprawdzania wyczerpania `switch` dla hierarchii klas. Start to otwarta hierarchia `abstract record Ticket(Money BasePrice)` (konstruktor `protected` - każdy, także w innym assembly, może dopisać wariant). Krok 1 zamyka ją konstruktorem `private protected` i wariantami `sealed record`. W kroku 2 `switch` po typach **musi** mieć ramię `_` - bez niego kompilator zgłasza CS8509 ("switch nie obsługuje wszystkich wartości") zawsze, nawet dla kompletu wariantów - więc `_ => throw new UnreachableException(...)` to jawny odpowiednik niejawnego `MatchException` z Javy. Wyczerpanie sprawdza dodatkowy test Roslyn `ExhaustivenessCheckFlagsUnhandledVariant`: czyta `switch` w `PriceCalculator.cs` i porównuje obsłużone typy z wariantami w assembly. Zamiast `java.lang.reflect.Proxy` obcy wariant `ChildTicket` jest kompilowany w pamięci w osobnym assembly "Outside".

### Co widzimy

Otwarta hierarchia `Ticket` z rekordami `StandardTicket`, `StudentTicket`, `SeniorTicket` i kalkulator z łańcuchem `is` zakończonym cichym `return 0`.

```csharp
public abstract record Ticket(Money BasePrice);

if (ticket is StudentTicket)
{
    return 25;
}
else if (ticket is SeniorTicket)
{
    return 30;
}
return 0;
```

`StartSilentlyGivesUnknownTicketNoDiscount` kompiluje w pamięci osobne assembly "Outside" z nowym typem biletu (dziecięcy, 40%) dziedziczącym po `Start.Ticket` - dostaje 0% zniżki bez żadnego ostrzeżenia.

### Krok 1: Zamknięcie hierarchii - konstruktor private protected i sealed record

**W IDE:** ręcznie: zamień rekord pozycyjny `Ticket` na `abstract record Ticket` z konstruktorem `private protected Ticket(Money basePrice)` i właściwością `BasePrice { get; }`. Warianty są już `sealed record`, więc nic więcej nie trzeba.
**Po:**

```csharp
public abstract record Ticket
{
    private protected Ticket(Money basePrice)
    {
        BasePrice = basePrice;
    }

    public Money BasePrice { get; }
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s13` - 17 testów zielonych; po naprawie Start na żywo `StartSilently...` robi się czerwony (obce assembly się nie skompiluje), reszta zielona.
**Co powiedzieć:** zamknięta lista wariantów w jednym assembly: `SealedHierarchyListsAllVariants` pokazuje, że kod spoza assembly dostaje błąd CS0122 (konstruktor niedostępny). Samo zamknięcie nie naprawia kalkulatora - łańcuch `if` nie jest wyczerpujący.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s13 0 1`

### Krok 2: switch po typach z UnreachableException

**W IDE:** na `if` ⌥⏎ > Convert to 'switch' expression (albo ręcznie), wymień jawnie każdy wariant, także `StandardTicket => 0`, a ostatnie ramię zamień na `_ => throw new UnreachableException(...)` zamiast `_ => 0`.
**Po:**

```csharp
return ticket switch
{
    StandardTicket => 0,
    StudentTicket => 25,
    SeniorTicket => 30,
    _ => throw new UnreachableException("nieobsłużony wariant biletu: " + ticket.GetType().Name),
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** każdy wariant jest wymieniony jawnie, także ten z zerową zniżką. Ramię `_` nie zwraca wartości "na wszelki wypadek", tylko rzuca - to deklaracja, że tu nie da się dojść. Kompilator C# nie sprawdzi, czy wymieniliśmy wszystkie warianty, więc robi to test Roslyn `ExhaustivenessCheckFlagsUnhandledVariant`.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s13 1 2`

### Krok 3: Nowy typ biletu i test wyczerpania

**W IDE:** utwórz `public sealed record ChildTicket(Money BasePrice) : Ticket(BasePrice);`. Kompilacja przechodzi - to jest różnica względem Javy, gdzie wywróciłby ją kompilator. W porcie tę rolę pełni test `ExhaustivenessCheckFlagsUnhandledVariant`, który sprawdza snapshoty: kalkulator ze Step2 zestawiony z hierarchią ze Step3 ma nieobsłużony `ChildTicket`. Pokaż ten test sali (na `Start` na żywo nic nie zrobi się czerwone), potem dopisz ramię z wartością 40.
**Po:**

```csharp
ChildTicket => 40,
```

**Uruchom:** test zielony: bilet dziecięcy 25.00 -> 15.00.
**Co powiedzieć:** zamknięta hierarchia plus kontrola wyczerpania zamieniają ciche błędy w czerwone testy. Ale tylko tam, gdzie ktoś ją uruchomi - test `OldSwitchThrowsUnreachableExceptionForNewVariant` buduje `PriceCalculator` ze Step2 razem z hierarchią ze Step3: w C# kompiluje się bez błędu (nawet rekompilacja nie pomaga, w odróżnieniu od Javy), a `switch` rzuca `UnreachableException`.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s13 2 3`

### Rozwiązanie i uzasadnienie

Hierarchia zamknięta w assembly (`private protected` + `sealed record`) i `switch` wymieniający każdy wariant jawnie, z ramieniem `_` rzucającym `UnreachableException`. Wariant dodany w źródle jest obsłużony, bo inaczej test wyczerpania jest czerwony, a w runtime - wyjątek zamiast cichego zera.

### Pułapki

- Ramię `_ => 0` (odpowiednik `default`) w `switch` po zamkniętej hierarchii wyłącza ochronę - wracamy do cichego błędu. Test wyczerpania go nie odróżni od `throw`, jeśli nie sprawdza treści ramienia.
- Zamknięcie przez `private protected` działa na poziomie assembly: każdy kod w tym samym projekcie (a przez `InternalsVisibleTo` także w projektach testów) może dopisać wariant. Pośrednia klasa bazowa (Extract Superclass w środku hierarchii) zmienia oba poziomy.
- Nowy wariant jest zgodny binarnie, a każdy skompilowany wcześniej `switch` - w tym w osobnych assembly i wtyczkach - rzuci `UnreachableException`. W C# nawet przebudowanie go nie zgłosi błędu.

### Pytanie do sali

Kiedy otwarta hierarchia jest lepsza niż zamknięta? Kto w waszym systemie dopisuje implementacje spoza assembly?

## Scena s14. Współdzielenie implementacji a podtypowanie

**Temat ze slajdów:** 1.1-1.2. Hierarchia jest częścią zachowania; 9.1. Replace Inheritance with Composition - kiedy?
**Namespace:** `Training.Workshop.M5.S14Reuse` · **Test:** `scripts/warsztat.sh --lang cs test m5/s14`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `CorporateAccount` dziedziczy po `LoyaltyAccount` tylko po to, by nie pisać drugi raz naliczania punktów, i blokuje wyjątkiem wymianę punktów na bilet. Wydzielamy logikę punktów do `PointsLedger`, zrywamy dziedziczenie, a raportowi dajemy wspólną rolę `IPointsHolder`.

**Zasada:** Wspólny kod uzasadnia współpracownika, a dziedziczenie dopiero wspólny kontrakt. Podtyp musi móc wystąpić wszędzie tam, gdzie nadtyp, więc override rzucający `NotSupportedException` to podręcznikowy sygnał złamanej substytucji. Zgodna sygnatura nie dowodzi zastępowalności.

**Efekt:** Implementację współdzieli kompozycja, a kontrakt - interfejs, więc operacji, której nie da się wykonać, po prostu nie ma. Klienci, którzy przyjmowali `LoyaltyAccount` i dostawali konto firmowe, przestaną się kompilować i każdego trzeba przejrzeć.

**Różnica względem Javy:** `RedeemFreeTicket()` jest w Start i Step1 jawnie `virtual`, żeby konto firmowe mogło je nadpisać - w C# już sama konieczność dopisania `virtual` do metody bazy "tylko po to, żeby podklasa mogła odmówić" jest sygnałem ostrzegawczym. Interfejs to `IPointsHolder` z właściwościami `Owner` i `Points`, a `divideToIntegralValue(10)` to `decimal.Truncate(amount / 10)`.

### Co widzimy

`CorporateAccount : LoyaltyAccount` tylko po to, by nie pisać drugi raz naliczania punktów (1 pkt za pełne 10.00). Firma zbiera punkty do rocznego rabatu i nie wymienia ich na bilety, więc odziedziczoną operację blokuje wyjątkiem.

```csharp
public override bool RedeemFreeTicket()
{
    throw new NotSupportedException("konto firmowe nie wymienia punktów na bilety");
}
```

Zgodna sygnatura nie dowodzi zastępowalności: każdy klient `LoyaltyAccount` może dostać ten obiekt i wybuchnąć.

### Krok 1: Extract Delegate - współdzielona implementacja

**W IDE:** na `LoyaltyAccount` ⌃T > Extract Class, nazwa `PointsLedger`, zaznacz pole `_points` oraz logikę `Earn` i odejmowania punktów (Rider zostawi w `LoyaltyAccount` delegowanie do nowego pola). Metodę odejmowania nazwij `Spend(int amount)`; jeśli Extract Class nie obejmie fragmentu z `RedeemFreeTicket`, najpierw Extract Method ⌥⌘M na tym fragmencie.
**Po:**

```csharp
private readonly PointsLedger _ledger = new();

public virtual bool RedeemFreeTicket()
{
    return _ledger.Spend(100);
}
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s14` - 12 testów zielonych.
**Co powiedzieć:** kod, który chcieliśmy współdzielić, ma teraz własną klasę. Reużycie nie wymaga już dziedziczenia - można go użyć z dowolnego miejsca.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s14 0 1`

### Krok 2: Konto firmowe przez kompozycję + rola dla raportu

**W IDE:** w `CorporateAccount` usuń `: LoyaltyAccount`, dodaj własne pole `PointsLedger`, pole `_company`, metodę `Earn` i właściwości `Owner`, `Points` (Generate ⌘N > Delegating Members dla `_ledger`), a `RedeemFreeTicket` usuń. Na `LoyaltyAccount` ⌃T > Extract Interface `IPointsHolder` z `Owner` i `Points`, `CorporateAccount : IPointsHolder`, `LoyaltyReport.Line(IPointsHolder)`. Obie klasy kont oznacz `sealed`, a z `RedeemFreeTicket` usuń `virtual`.
**Po:**

```csharp
public sealed class CorporateAccount : IPointsHolder
{
    private readonly string _company;
    private readonly PointsLedger _ledger = new();
    ...
}
```

**Uruchom:** test zielony. `S14SolutionTest` sprawdza, że `CorporateAccount` nie jest `LoyaltyAccount` i nie ma `RedeemFreeTicket`.
**Co powiedzieć:** wspólny kontrakt (właściciel i saldo) jest prawdziwy dla obu kont, więc tu podtypowanie jest uczciwe. Operacji, której nie da się wykonać, po prostu nie ma - nie trzeba jej blokować.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s14 1 2`

### Rozwiązanie i uzasadnienie

Implementację współdzieli `PointsLedger` (kompozycja), kontrakt - `IPointsHolder` (interfejs). Dziedziczenie zostało tam, gdzie nie było potrzebne: nigdzie.

### Pułapki

- `NotSupportedException` w override to podręcznikowy sygnał złamanej substytucji (tak samo `ReadOnlyAccount.Withdraw`). W BCL ten sam zapach ma `ReadOnlyCollection<T>` implementujące `IList<T>.Add`.
- Klienci, którzy przyjmowali `LoyaltyAccount` i dostawali konto firmowe, przestaną się kompilować - dobrze, każdy z nich trzeba przejrzeć.
- Wydzielony współpracownik nie powinien wiedzieć, kto go używa (żadnych `if (owner is ...)`).

### Pytanie do sali

Pytania ze slajdów 1.1-1.2: czy te klasy mają ten sam kod, czy są wariantami jednego pojęcia? Jaka jest odpowiedź dla każdej pary w tej scenie?

## Scena s15. Zgodność binarna, refleksja i adnotacje

**Temat ze slajdów:** 1.3. Warstwy zgodności; 10.2-10.3. Zgodność binarna, refleksja i adnotacje
**Namespace:** `Training.Workshop.M5.S15Compatibility` · **Test:** `scripts/warsztat.sh --lang cs test m5/s15`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Biblioteka kasowa ma eksporter szukający atrybutu `[Column]` przez `GetMethods(... | DeclaredOnly)` i API używane przez skompilowane wtyczki partnerów. Zabezpieczamy refleksję, wciągamy `Price()` do bazy, uogólniamy parametr `Quote` i przywracamy starą sygnaturę przestarzałym przeciążeniem.

**Zasada:** Zgodność ma kilka warstw: zachowanie, źródło, binaria, refleksja, serializacja i integracje - i każda wymaga osobnego sprawdzenia. Pull Up jest zwykle zgodny binarnie dla wywołań, ale zmienia typ deklarujący widoczny dla refleksji, a zmiana typu parametru zmienia sygnaturę metody w metadanych. Pełny build sprawdza tylko zgodność źródłową.

**Efekt:** Eksport działa po przesunięciu metody, `Quote` przyjmuje każdy bilet, a stare wtyczki działają dzięki przeciążeniu delegującemu. Przestarzała metoda zostaje na okres przejściowy, a jej usunięcie to osobna, zapowiedziana zmiana łamiąca.

**Różnica względem Javy:** adnotacja `@Column` to atrybut `ColumnAttribute` (stabilny kontrakt sceny, poza `Start`/`StepN`), `getDeclaredMethods()` to `GetMethods(... | BindingFlags.DeclaredOnly)`, a `getMethods()` to `GetMethods()`. Zamiast `javax.tools` i podmiany JAR-a test kompiluje przez Roslyn w pamięci API wariantu jako assembly "Api" (przestrzeń nazw podmieniona na `Api`), a wtyczkę "Plugin" kompiluje przeciw jednej wersji i ładuje z inną w osobnym `AssemblyLoadContext` - jak podmiana `Api.dll` na serwerze. Wyniki są takie jak w Javie, zmieniają się tylko nazwy wyjątków: `NoSuchMethodError` to `MissingMethodException`, `@Deprecated` to `[Obsolete]`, `@SuppressWarnings("deprecation")` w teście równoważności to `#pragma warning disable CS0618`. Pliki wariantów mają jawne `using Training.Workshop.M5.S15Compatibility;` - potrzebne po podmianie przestrzeni nazw.

### Co widzimy

Biblioteka kasowa: `StandardTicket` i `StudentTicket` deklarują własne `Price()` z atrybutem `[Column("cena")]`. `TicketExporter` szuka kolumn przez `GetMethods(... | DeclaredOnly)` klasy runtime. `BoxOfficeApi.Quote(StudentTicket)` jest używane przez **skompilowane** wtyczki partnerów. Ta scena jest nietypowa: główną demonstracją jest `S15SolutionTest`, który przez Roslyn kompiluje wtyczkę przeciw jednej wersji API (pliki wariantu z przestrzenią nazw podmienioną na `Api`) i uruchamia ją z inną - jak podmiana DLL na serwerze.

```csharp
var candidates = ticket.GetType().GetMethods(BindingFlags.Public | BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.Static | BindingFlags.DeclaredOnly);   // tylko klasa runtime
```

### Krok 1: Przygotowanie refleksji przed ruchem w hierarchii

**W IDE:** w `TicketExporter` zmień wywołanie na `GetMethods()` bez flag (publiczne metody instancji i statyczne, także odziedziczone).
**Po:**

```csharp
var candidates = ticket.GetType().GetMethods();
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s15` - 20 testów zielonych.
**Co powiedzieć:** kod szukający metod po nazwie albo atrybucie to ukryty kontrakt na typ deklarujący. Zabezpieczamy go przed ruchem, a nie po awarii.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 0 1`

### Krok 2: Pull Up Price() z atrybutem

**W IDE:** ciała `Price()` są różne, więc najpierw Extract Method ⌥⌘M na procencie zniżki (`DiscountPercent()`), potem ⌃T > Pull Members Up na `Price()` ze `StudentTicket` (atrybut idzie razem z metodą), a `DiscountPercent()` jako abstract (`protected abstract int DiscountPercent()`). Kopię `Price()` w `StandardTicket` usuń. Nie dodawaj `virtual`.
**Po:**

```csharp
[Column("cena")]
public Money Price()
{
    return _basePrice.Minus(_basePrice.Percent(DiscountPercent()));
}

protected abstract int DiscountPercent();
```

**Uruchom:** test zielony. `PullUpIsBinaryCompatibleForCallers`: wtyczka skompilowana przeciw Step1 działa z API ze Step2 (CLR znajduje `StudentTicket::Price` w klasie bazowej). `PullUpHidesAnnotatedMethodFromDeclaredMethodsLookup`: `GetMethods(... | DeclaredOnly)` na podklasie już nie widzi kolumny - eksporter ze Start by ją zgubił.
**Co powiedzieć:** zgodność binarna dla wywołań nie oznacza zgodności refleksyjnej. `AttributeUsage(Inherited = true)` tu nie pomoże - dotyczy nadpisań metod wirtualnych, a nie metody, której podklasa w ogóle nie deklaruje.
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 1 2`

### Krok 3: Generalize Parameter Type - zmiana łamiąca binarnie

**W IDE:** na `BoxOfficeApi.Quote` Change Signature ⌘F6, typ parametru `StudentTicket` -> `Ticket`. Wszystko się kompiluje.
**Po:**

```csharp
public Money Quote(Ticket ticket)
{
    return ticket.Price();
}
```

**Uruchom:** test zielony. `GeneralizedParameterBreaksOldBinary`: stara wtyczka dostaje `MissingMethodException` z nazwą `Quote` (sygnatury `Quote(Api.StudentTicket)` nie ma już w metadanych). `GeneralizedParameterIsSourceCompatible`: po rekompilacji ta sama wtyczka działa.
**Co powiedzieć:** pełny build sprawdza zgodność źródłową. Stara wtyczka, której nie przebudujemy, widzi zmianę sygnatury w metadanych - IDE tego nie zgłosi.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 2 3`

### Krok 4: Przestarzałe przeciążenie delegujące

**W IDE:** dodaj z powrotem `Quote(StudentTicket)` z `[Obsolete("od 2.0: użyj Quote(Ticket)")]`, delegujące do `Quote((Ticket)ticket)`.
**Po:**

```csharp
[Obsolete("od 2.0: użyj Quote(Ticket)")]
public Money Quote(StudentTicket ticket)
{
    return Quote((Ticket)ticket);
}
```

**Uruchom:** test zielony. `DelegatingOverloadRestoresBinaryCompatibility`: stara wtyczka działa z API ze Step4.
**Co powiedzieć:** stara sygnatura zostaje na okres przejściowy, a usunięcie to osobna, zapowiedziana zmiana łamiąca. Uwaga na przeciążenia: nowi klienci z typem `StudentTicket` wybiorą przestarzałą metodę (scena s09) i dostaną ostrzeżenie CS0618 - przy `TreatWarningsAsErrors` to błąd, więc test równoważności wyłącza je `#pragma`.
**Snapshot:** `Step4/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s15 3 4`

### Rozwiązanie i uzasadnienie

Eksporter odporny na przesunięcia metod, `Price()` w bazie, API przyjmujące `Ticket` z przejściowym przeciążeniem. Każda warstwa zgodności jest sprawdzona osobnym testem.

### Pułapki

- Zgodność binarna nie dowodzi zgodności źródłowej ani zachowania - i odwrotnie.
- Push Down, zmiana typu parametru lub wyniku, usunięcie klasy - łamią stare binaria. Pull Up, dodanie metody do klasy - zwykle nie. W C# łamią też rzeczy "niewinne" w źródle: zamiana pola na właściwość, dodanie parametru opcjonalnego (wartość domyślna jest wkompilowana u klienta), zmiana wartości `const` (klient ma starą wartość).
- `MethodInfo.DeclaringType`, `GetMethods(DeclaredOnly)`, atrybuty metod i metody pomostowe (scena s12) zmieniają się przy każdym ruchu członka.
- Narzędzia typu Microsoft.DotNet.ApiCompat (walidacja pakietu `EnablePackageValidation`) w CI sprawdzają zgodność binarną automatycznie.

### Pytanie do sali

Którą warstwę zgodności z tabeli 1.3 sprawdza wasz obecny pipeline, a której nie sprawdza żadna automatyzacja?

## Scena s16. Serializacja i proxy - hierarchia jako format danych

**Temat ze slajdów:** 10.4-10.6. Serializacja, ORM i DI
**Namespace:** `Training.Workshop.M5.S16SerializationProxy` · **Test:** `scripts/warsztat.sh --lang cs test m5/s16`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Wydzielenie nadklasy z polami `_title` i `_seat` w zapisywanym przez `DataContractSerializer` bilecie `StudentTicket` sprawia, że stare dane czytają się bez wyjątku, ale z `null` w tytule. Wprowadzamy Serialization Proxy w nowym formacie (JSON z płaskim DTO), a dla serwisu cenowego `sealed` wydzielamy interfejs, żeby dało się go opakować `DispatchProxy`.

**Zasada:** Serializator zapisujący pola obiektu (tu `DataContractSerializer`) zapisuje je poziomami hierarchii, więc przeniesienie pola zmienia format danych, a stała nazwa kontraktu nie przenosi stanu między poziomami. Serialization Proxy oddziela format od struktury klas. Dynamiczne proxy `DispatchProxy` opakowuje tylko interfejsy, a proxy klasowe nie ruszy klas `sealed` ani metod niewirtualnych.

**Efekt:** Przyszłe Pull Up i Push Down nie zmienią zapisu, a proxy audytowe działa bez usuwania `sealed`. Zachowanie świadomie się zmienia: stare dane są odrzucane głośno zamiast czytane z utratą pól, a ich migracja to osobne zadanie.

**Różnica względem Javy:** to adaptacja. Serializacja Javy nie ma odpowiednika w .NET (`BinaryFormatter` został usunięty w .NET 9), a System.Text.Json nie ma pułapki "poziomów hierarchii". Stary format to więc `DataContractSerializer` (stare serwisy WCF, cache, sesje): czyta prywatne pola bez wołania konstruktora, stała nazwa i przestrzeń kontraktu (`[DataContract(Name = "StudentTicket", Namespace = "urn:cinelegacy:tickets")]`) to odpowiednik `serialVersionUID = 1L`, a pola bazy stoją w XML przed polami podklasy. Po Pull Up stary XML (`seat, studentId, title`) czytany przez nową hierarchię (`seat, title` | `studentId`) po cichu gubi tylko `title` (w Javie ginęły `title` i `seat`): oczekiwane `" F3 (legitymacja S-123)"` i `Title == null`. "Plik z v1" to zamrożona stała XML, a nie Base64. Serialization Proxy w kroku 2 to System.Text.Json + DTO: `[JsonConverter]` na `StudentTicket` zapisuje płaski `record SerializedForm` (`writeReplace`) i czyta przez publiczny konstruktor (`readResolve`); nowy format (JSON) jest odpowiednikiem `serialVersionUID = 2L`, więc stary XML jest odrzucany głośno `JsonException` (Java: `InvalidClassException`). Testowy `TicketStore` (odpowiednik `ObjectOutputStream`/`ObjectInputStream`) wybiera format po typie: klasa z `[DataContract]` - XML, inna - JSON. `java.lang.reflect.Proxy` to `DispatchProxy` (dla klasy rzuca `ArgumentException`), a proxy klasowe CGLIB/ByteBuddy to Castle DynamicProxy (przechwytuje tylko metody wirtualne). Dodatkowy test `StartWritesTheFrozenV1Data` pilnuje, że stała XML to naprawdę zapis klasy ze Start.

### Co widzimy

`StudentTicket` z `[DataContract]` i trzema prywatnymi polami `[DataMember]`: `title`, `seat`, `studentId`. Postać XML zawiera pola każdego poziomu hierarchii po kolei (najpierw baza, potem podklasa, w obrębie poziomu alfabetycznie). `TicketPricing` to klasa `sealed` bez interfejsu. Scena nietypowa: główną demonstracją jest `S16SolutionTest` z "plikiem z v1" zamrożonym jako stała XML (bilet zapisany przez klasę ze Start).

```csharp
[DataContract(Name = "StudentTicket", Namespace = "urn:cinelegacy:tickets")]
public sealed class StudentTicket
{
    [DataMember(Name = "title")]
    private readonly string _title;
    ...
}

public sealed class TicketPricing
{
    public Money StudentPrice(Money basePrice) { ... }
}
```

### Krok 1: Extract Superclass + Pull Up Field - ciche zgubienie danych

**W IDE:** ⌃T > Extract Superclass na `StudentTicket`, nazwa `Ticket`, pola `_title`, `_seat` + właściwości `Title`, `Seat`. `Ticket` dostaje `[DataContract(Name = "Ticket", Namespace = "urn:cinelegacy:tickets")]`, pola w bazie zachowują `[DataMember]`, a `StudentTicket` zostaje z tą samą nazwą i przestrzenią kontraktu.
**Po:**

```csharp
[DataContract(Name = "Ticket", Namespace = "urn:cinelegacy:tickets")]
public abstract class Ticket
{
    [DataMember(Name = "title")]
    private readonly string _title;

    [DataMember(Name = "seat")]
    private readonly string _seat;
```

**Uruchom:** `scripts/warsztat.sh --lang cs test m5/s16` - 14 testów zielonych. `PulledUpFieldsAreSilentlyLostWhenReadingOldData`: stare dane odczytane przez Step1 dają `" F3 (legitymacja S-123)"`, a `Title` jest `null`. Bez wyjątku.
**Co powiedzieć:** pole `title` przeszło na poziom `Ticket`, który serializator czyta przed polami podklasy - w starym XML stoi ono po `studentId`, więc zostaje pominięte. Stała nazwa kontraktu zapobiega wyjątkowi, ale nie migruje stanu - to najgorsza możliwa kombinacja.
**Snapshot:** `Step1/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s16 0 1`

### Krok 2: Serialization Proxy (JSON i płaski DTO)

**W IDE:** ręcznie (brak automatu): w `StudentTicket` prywatny `sealed record SerializedForm(string Title, string Seat, string StudentId)` i prywatny `JsonConverter<StudentTicket>`: `Write` zapisuje `SerializedForm`, `Read` czyta `SerializedForm` i woła publiczny konstruktor. Klasę oznacz `[JsonConverter(typeof(SerializedFormConverter))]`. Z `Ticket` i `StudentTicket` usuń `[DataContract]` i `[DataMember]` - baza przestaje być kontraktem danych.
**Po:**

```csharp
public override void Write(Utf8JsonWriter writer, StudentTicket value, JsonSerializerOptions options)
{
    JsonSerializer.Serialize(writer, new SerializedForm(value.Title, value.Seat, value._studentId), options);
}
```

**Uruchom:** test zielony. `SerializationProxyRejectsOldDataLoudly`: stare dane odrzucone `JsonException`. `SerializationProxyWritesFlatFormIndependentOfHierarchy`: zapis to dokładnie `{"Title":"Amator","Seat":"F3","StudentId":"S-1"}`, bez słowa `Ticket`.
**Co powiedzieć:** hierarchia klasy przestaje być formatem danych - przyszłe Pull Up/Push Down nie zmienią zapisu. Nowy format to świadoma deklaracja nowej wersji: błąd głośny zamiast cichego. Migracja starych danych to osobne zadanie (czytnik v1 albo konwersja offline).
**Snapshot:** `Step2/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s16 1 2`

### Krok 3: Extract Interface dla dynamicznego proxy

**W IDE:** na `TicketPricing` ⌃T > Extract Interface, nazwa `IPricing`, metoda `StudentPrice`. Klasa może zostać `sealed`.
**Po:**

```csharp
public interface IPricing
{
    Money StudentPrice(Money basePrice);
}
```

**Uruchom:** test zielony. `DispatchProxyCannotWrapSealedClassWithoutInterface`: `DispatchProxy.Create` dla klasy rzuca `ArgumentException`. `ExtractedInterfaceAllowsDynamicProxy`: proxy audytowe liczy wywołania.
**Co powiedzieć:** `DispatchProxy` opakowuje tylko interfejsy. Proxy klasowe (Castle DynamicProxy w interceptorach kontenerów DI, lazy loading w EF Core Proxies i NHibernate) robi podklasę, więc nie ruszy klasy `sealed` ani metod niewirtualnych - a interceptor transakcji albo cache na metodzie niewirtualnej po cichu nie działa.
**Snapshot:** `Step3/` · **Różnica:** `scripts/warsztat.sh --lang cs diff m5/s16 2 3`

### Rozwiązanie i uzasadnienie

Serializowana postać to płaski record niezależny od hierarchii. Serwis ma interfejs roli, który kontener DI może opakować bez dziedziczenia.

### Pułapki

- `sealed` albo usunięcie `virtual` dodane "dla porządku" przy refaktoryzacji potrafi wyłączyć interceptory, lazy loading (EF Core Proxies wymaga `virtual` na nawigacjach) i mocki (Moq, NSubstitute).
- Self-invocation: `this.InnaMetoda()` wewnątrz serwisu omija proxy (także po Extract Interface).
- Hierarchia encji EF Core to migracja modelu: strategia TPH/TPT/TPC, dyskryminator, tabele, zapytania polimorficzne. Test z prawdziwym dostawcą: `SaveChanges`, `ChangeTracker.Clear()`, ponowny odczyt.
- DI po Extract Interface: dwie rejestracje tej samej roli to po cichu "wygrywa ostatnia" (`GetService<IPricing>`) albo lista w `IEnumerable<IPricing>` - uruchom kontener (`ValidateOnBuild`) w teście.

### Pytanie do sali

Gdzie wasz system trzyma obiekty zapisane serializatorem pól (sesje, cache rozproszony, kolejki, stary WCF)? Czy macie choć jeden plik z poprzedniej wersji formatu w testach?

## Proponowana kolejność pokazu

**Ścieżka krótka (~80 min)** - rdzeń katalogu refaktoryzacji plus dwie najgroźniejsze pułapki:

1. s01 Pull Up Method (12 min)
2. s03 Push Down i asymetria (12 min)
3. s04 Extract Superclass (15 min)
4. s06 Extract Interface i metoda domyślna (12 min)
5. s08 Kompozycja i `new` zamiast `override` (12 min)
6. s09 Overloading po Extract Superclass (10 min)
7. s15 tylko kroki 3-4 z `scripts/warsztat.sh --lang cs jump m5/s15 2` (7 min)

**Ścieżka pełna (~3 h 10 min, z przerwą)** - według agendy slajdów:

1. Semantyka C# i CLR: s09, s10, s11, s12, s13 (~48 min)
2. Pull Up / Push Down: s01, s02, s03 (~34 min)
3. Ekstrakcje: s04, s05, s06 (~42 min)
4. Collapse i kompozycja: s07, s14, s08 (~30 min)
5. Zgodność i integracje: s15, s16 (~27 min)

Jeśli brakuje czasu, sceny s02, s07 i s10 dobrze działają jako praca własna uczestników (zadania modułu 5).
