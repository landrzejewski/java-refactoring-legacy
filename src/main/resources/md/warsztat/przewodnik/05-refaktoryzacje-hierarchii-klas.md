# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy: przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje każdy ruch z modułu 5: Pull Up / Push Down, Extract Superclass / Subclass / Interface, Collapse Hierarchy, kompozycję zamiast dziedziczenia, a także semantykę Javy, która sprawia, że te ruchy bywają niebezpieczne (przeciążenia, ukrywanie pól, konstruktory, bridge methods, `sealed`, zgodność binarna, serializacja, proxy). Sceny nie powielają studium powiadomień z `pl.training.module5` ani ćwiczeń z `md/zadania/05-*.md`. Każda scena ma pakiet `start` (na nim pracujesz na żywo), kompletne snapshoty `stepN` i testy, które po każdym kroku mają być zielone.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh list m5              # sceny i kroki modułu 5
scripts/warsztat.sh test m5/s01          # testy jednej sceny (albo całego modułu: m5)
scripts/warsztat.sh diff m5/s01 0 1      # co zmienia krok 1 względem start
scripts/warsztat.sh jump m5/s01 2        # nie zdążyłeś? start = snapshot step2
scripts/warsztat.sh reset m5/s01         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w IDE uruchamiasz test sceny, zanim cokolwiek powiesz.
- Test `SNNEquivalenceTest` sprawdza, że start i wszystkie kroki zachowują się tak samo. `SNNSolutionTest` pokazuje to, czego równoważność nie widzi: typ deklarujący członka, klasę runtime, pułapkę semantyki, zgodność binarną.
- Sceny o pułapkach (s08-s11, s13) mają w `SNNSolutionTest` testy o nazwie `start...`, które **dokumentują pułapkę w start**. Gdy naprawisz start na żywo, te testy zrobią się czerwone - to jest dowód, że pułapka zniknęła. Powiedz to sali przed pierwszym krokiem. `reset` przywraca stan wyjściowy.
- Pozostałe testy strukturalne sprawdzają snapshoty `stepN`, więc praca na `start` ich nie psuje.
- Scena s11 celowo kompiluje się z ostrzeżeniem `[this-escape]` w `start` i `step1` - pokaż je w oknie Build.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Pakiet | Czas |
| --- | --- | --- | --- | --- |
| s01 | Pull Up Method (3.1-3.3) | 3 | `m5.s01_pullupmethod` | ~12 min |
| s02 | Pull Up Field (3.4-3.6) | 3 | `m5.s02_pullupfield` | ~10 min |
| s03 | Push Down Method/Field, asymetria przesunięć (4.1-4.4) | 3 | `m5.s03_pushdown` | ~12 min |
| s04 | Extract Superclass, konstruktory i fabryki (5) | 3 | `m5.s04_extractsuperclass` | ~15 min |
| s05 | Extract Subclass (6) | 4 | `m5.s05_extractsubclass` | ~15 min |
| s06 | Extract Interface, metody domyślne (7.1-7.4) | 3 | `m5.s06_extractinterface` | ~12 min |
| s07 | Collapse Hierarchy (8) | 3 | `m5.s07_collapsehierarchy` | ~8 min |
| s08 | Replace Inheritance with Composition, self-use, pułapki delegowania (9.1-9.4) | 2 | `m5.s08_composition` | ~12 min |
| s09 | Overriding a overloading (2.1-2.2) | 2 | `m5.s09_overloading` | ~10 min |
| s10 | Ukrywanie pól i metod static (2.3) | 2 | `m5.s10_fieldhiding` | ~8 min |
| s11 | Konstruktor wołający override, prolog Javy 25 (2.4) | 2 | `m5.s11_constructorcall` | ~8 min |
| s12 | Generyki i metody bridge (2.7, 10.2) | 2 | `m5.s12_bridgemethods` | ~10 min |
| s13 | Hierarchie `sealed`, wyczerpujący `switch`, `MatchException` (2.8) | 3 | `m5.s13_sealed` | ~12 min |
| s14 | Współdzielenie implementacji a podtypowanie (1.1-1.2, 9.1) | 2 | `m5.s14_reuse` | ~10 min |
| s15 | Zgodność binarna, refleksja i adnotacje (1.3, 10.2-10.3) | 4 | `m5.s15_compatibility` | ~15 min |
| s16 | Serializacja, proxy, DI/ORM (10.4-10.6) | 3 | `m5.s16_serializationproxy` | ~12 min |

## Scena s01. Pull Up Method - najpierw ujednolicić ciała

**Temat ze slajdów:** 3.1-3.3. Pull Up Method
**Pakiet:** `pl.training.workshop.m5.s01_pullupmethod` · **Test:** `scripts/warsztat.sh test m5/s01`
**Czas:** ~12 min

### Co widzimy

Trzy bilety (`StandardTicket`, `StudentTicket`, `VipTicket`) mają wspólną nadklasę `Ticket`, ale każdy deklaruje własne `label()`. Ciała różnią się tekstem, a robią to samo:

```java
return title() + ": " + price();                                  // StandardTicket
return String.format("%s: %s", title(), price());                 // StudentTicket
return new StringBuilder(title()).append(": ").append(price()).toString(); // VipTicket
```

IntelliJ nie zaproponuje Pull Up dla metod, które nie są identyczne. Do tego `label()` woła `price()`, którego baza nie zna. Klient `BoxOffice` musi znać konkretne klasy.

### Krok 1: Ujednolicenie ciał metod

**W IDE:** ręcznie zmień ciało `label()` w `StudentTicket` i `VipTicket` na wersję z `StandardTicket`. Możesz skopiować i wkleić, ale porównaj wynik z kontraktem, nie z tekstem: `%s` dla `Money` i konkatenacja dają to samo, bo oba wołają `toString()`.
**Po:**

```java
public String label() {
    return title() + ": " + price();
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s01` - 15 testów zielonych.
**Co powiedzieć:** Pull Up wymaga identycznego ciała, więc najpierw robimy z trzech wersji jedną. To osobny, bezpieczny krok, a test pilnuje, że format się nie zmienił.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 0 1`

### Krok 2: Pull Up price() jako metoda abstrakcyjna

**W IDE:** w `StandardTicket` wybierz Refactor > Pull Members Up (albo Refactor This ⌃T), zaznacz `price()` i opcję **Make abstract**. IntelliJ doda `@Override` w pozostałych podklasach (jeśli nie, dodaj ręcznie przez ⌥⏎).
**Po:**

```java
public abstract class Ticket {
    ...
    public abstract Money price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** ciała `price()` są różne, więc do bazy idzie tylko deklaracja. To punkt rozszerzenia, dzięki któremu wspólne `label()` będzie mogło zamieszkać w bazie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 1 2`

### Krok 3: Pull Up label() i klient na typie bazowym

**W IDE:** Refactor > Pull Members Up na `label()` w `StandardTicket`. IntelliJ wykryje identyczne metody w rodzeństwie i zaproponuje ich usunięcie - zgódź się. Dodaj `final`. W `BoxOffice` zmień `switch` tak, by zwracał `Ticket`, i wywołaj `label()` raz (Extract Variable ⌥⌘V).
**Po:**

```java
public final String label() {
    return title() + ": " + price();
}
```

**Uruchom:** test zielony. `S01SolutionTest` sprawdza, że `label()` jest zadeklarowane tylko w `Ticket` i jest `final`.
**Co powiedzieć:** metoda trafiła na najniższy poziom, na którym jest prawdziwa dla wszystkich potomków. `final` chroni przed przypadkowym nadpisaniem przez podklasę spoza repozytorium, która miała własne `label()` - jej kompilacja się wywróci, zamiast po cichu zmienić zachowanie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3/Ticket` ma abstrakcyjne `price()` i finalne `label()`. Podklasy zawierają tylko to, co naprawdę różne: regułę ceny. Klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym.

### Pułapki

- Pull Up metody o tej samej sygnaturze, ale innym kontrakcie (np. `label()` jednej podklasy dodaje walutę). Tekst to nie kontrakt.
- Metoda wciągnięta do bazy, która korzysta z pola dostępnego tylko w jednej podklasie - IDE zaproponuje wtedy `protected` pole albo getter "na zapas".
- Zewnętrzna podklasa z metodą o tej samej nazwie staje się nieplanowanym override. `final` zamienia to w błąd kompilacji.
- Refleksja: `StudentTicket.class.getDeclaredMethod("label")` przestaje działać po Pull Up (scena s15).

### Pytanie do sali

Czy `label()` powinno być `final`, jeśli za rok pojawi się bilet z etykietą w innym formacie? Co wtedy zrobicie?

## Scena s02. Pull Up Field - to samo znaczenie, typ i cykl życia

**Temat ze slajdów:** 3.4-3.6. Pull Up Field i ryzyka Pull Up
**Pakiet:** `pl.training.workshop.m5.s02_pullupfield` · **Test:** `scripts/warsztat.sh test m5/s02`
**Czas:** ~10 min

### Co widzimy

Każdy bilet przechowuje miejsce na sali, ale każdy po swojemu: `StandardTicket` ma mutowalne `seat` ustawiane setterem, `StudentTicket` ma `seatCode`, a `VipTicket` normalizuje wartość do wielkich liter. `studentId` to zupełnie inne pojęcie.

```java
StandardTicket ticket = new StandardTicket();   // klient w BoxOffice
ticket.setSeat(seat);
```

### Krok 1: Rename seatCode na seat

**W IDE:** w `StudentTicket` na polu `seatCode` Rename ⇧F6 -> `seat` (IntelliJ zapyta o parametr konstruktora i akcesor - zaznacz oba). Akcesor `seatCode()` również ⇧F6 -> `seat()`.
**Po:**

```java
private final String seat;
public String seat() { return seat; }
```

**Uruchom:** `scripts/warsztat.sh test m5/s02` - 15 testów zielonych.
**Co powiedzieć:** to samo znaczenie musi mieć tę samą nazwę, inaczej IDE nie połączy pól. Rename jest bezpieczny, bo nikt spoza klasy nie używał `seatCode()`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 0 1`

### Krok 2: Ujednolicenie cyklu życia (konstruktor zamiast settera)

**W IDE:** w `StandardTicket` Refactor > Introduce Parameter ⌥⌘P nie pomoże przy setterze, więc ręcznie: dodaj konstruktor `StandardTicket(String seat)`, oznacz pole `final`, usuń `setSeat` (Safe Delete ⌘⌦ pokaże jedyne użycie w `BoxOffice`) i popraw klienta na `new StandardTicket(seat)`.
**Po:**

```java
private final String seat;

public StandardTicket(String seat) {
    this.seat = seat;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** pole mutowalne i pole final to dwa różne cykle życia. Gdybyśmy wciągnęli je do bazy teraz, jeden z biletów zmieniłby semantykę. Dopiero po tym kroku trzy pola mają ten sam typ, znaczenie, walidację i moment inicjalizacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 1 2`

### Krok 3: Pull Up Field seat i akcesora seat()

**W IDE:** w `StandardTicket` Refactor > Pull Members Up, zaznacz pole `seat` i metodę `seat()`. IntelliJ usunie duplikaty z rodzeństwa. Następnie w `Ticket` dodaj konstruktor `protected Ticket(String seat)` (Generate ⌘N > Constructor) i w podklasach zamień przypisanie na `super(seat)`. W `VipTicket`: `super(seat.toUpperCase(Locale.ROOT))`.
**Po:**

```java
public abstract class Ticket {
    private final String seat;

    protected Ticket(String seat) {
        this.seat = seat;
    }
    ...
}
```

**Uruchom:** test zielony. `S02SolutionTest` sprawdza, że pole jest `private final` w `Ticket`, a `studentId` zostało w `StudentTicket`.
**Co powiedzieć:** pole w bazie jest prywatne i ustawiane przez `super(...)`, nie `protected`. Reguła normalizacji VIP została w podklasie - baza dostaje już gotową wartość.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Jedno źródło stanu miejsca, bez surowego `protected`. Pola o innym znaczeniu (`studentId`) nie są przenoszone tylko dlatego, że mają ten sam typ.

### Pułapki

- IntelliJ domyślnie proponuje `protected` dla pola przeniesionego w górę. Zmień na `private` + akcesor.
- Pola `static` o tej samej nazwie po Pull Up scalają dwa niezależne stany (np. liczniki numerów biletów).
- Refleksja i haki serializacji zobaczą inny typ deklarujący pola (scena s16).
- Pole wciągnięte w górę, choć w bazie i podklasie zostawiono deklaracje, to dwa sloty (scena s10).

### Pytanie do sali

Gdyby `VipTicket` normalizował miejsce w getterze, a nie w konstruktorze, czy Pull Up Field nadal byłby bezpieczny?

## Scena s03. Push Down Method/Field i asymetria przesunięć

**Temat ze slajdów:** 4.1-4.4. Push Down Method, Push Down Field i asymetria przesunięć
**Pakiet:** `pl.training.workshop.m5.s03_pushdown` · **Test:** `scripts/warsztat.sh test m5/s03`
**Czas:** ~12 min

### Co widzimy

`Ticket` obiecuje `upgradeToVip()` wszystkim biletom, ale bilet studencki odrzuca dopłatę wyjątkiem. Klient sprawdza `instanceof`, zanim wywoła metodę. Klasyczne sygnały zbyt szerokiego kontraktu.

```java
@Override
public void upgradeToVip() {
    throw new UnsupportedOperationException("bilet ulgowy nie ma dopłaty VIP");
}
```

### Krok 1: Klienci na podtyp

**W IDE:** w `BoxOffice` rozdziel gałęzie: dla studenta zwróć cenę od razu, dla biletu normalnego zadeklaruj zmienną typu `StandardTicket`. Ręcznie (albo ⌥⏎ na `instanceof` > "Replace with pattern variable", potem porządki).
**Po:**

```java
StandardTicket ticket = new StandardTicket(basePrice);
if (vip) {
    ticket.upgradeToVip();
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s03` - 19 testów zielonych.
**Co powiedzieć:** zanim przesuniemy metodę w dół, nikt nie może jej wołać przez typ bazowy. Find Usages ⌥F7 na `Ticket.upgradeToVip` ma pokazać tylko wywołania na `StandardTicket`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 0 1`

### Krok 2: Najpierw zachowanie - hak surcharge()

**W IDE:** w `Ticket.price()` Extract Method ⌥⌘M na wyrażeniu dopłaty, nazwa `surcharge()`, zwraca `Money.ZERO`. Następnie w `StandardTicket` Generate ⌘N > Override Methods > `surcharge()` z logiką VIP.
**Po:**

```java
// Ticket
public Money price() {
    return basePrice.minus(basePrice.percent(discountPercent())).plus(surcharge());
}
// StandardTicket
@Override
protected Money surcharge() {
    return isVipUpgraded() ? Money.of("10.00") : Money.ZERO;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie czyta już pola `vipUpgraded`. Najpierw przenosimy zachowanie, które korzysta z pola, potem samo pole - inaczej Push Down się nie skompiluje albo zostawi dwa sloty.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 1 2`

### Krok 3: Push Members Down

**W IDE:** w `Ticket` Refactor > Push Members Down, zaznacz `vipUpgraded`, `upgradeToVip()`, `isVipUpgraded()`, cel: tylko `StandardTicket`. Potem Safe Delete ⌘⌦ override rzucający wyjątek w `StudentTicket` (IDE może już go usunąć samo).
**Po:**

```java
public final class StandardTicket extends Ticket {
    private boolean vipUpgraded;
    public void upgradeToVip() { vipUpgraded = true; }
    ...
}
```

**Uruchom:** test zielony. `S03SolutionTest` pokazuje, że `Ticket.class.getMethod("upgradeToVip")` rzuca `NoSuchMethodException`.
**Co powiedzieć:** baza obiecuje tylko to, co prawdziwe dla wszystkich biletów. W bibliotece to zmiana łamiąca: stary `.class` klienta ma w sobie `invokevirtual Ticket.upgradeToVip` i dostanie `NoSuchMethodError`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Stan i operacja VIP żyją w `StandardTicket`. `StudentTicket` nie musi niczego odmawiać. Kontrakt bazy jest węższy, ale uczciwy.

### Pułapki

- **Asymetria:** Pull Up jest zgodny binarnie dla wywołań (JVM szuka metody w nadklasach, test `beforePushDownSubclassFindsMethodInSuperclass`), Push Down nie jest (JVM nie szuka w podklasach).
- Kilka podklas potrzebuje metody - przesuwamy do najbliższego wspólnego przodka, nie kopiujemy do liści.
- Zostawienie pola w bazie i skopiowanie go do podklasy daje dwa niezależne sloty.

### Pytanie do sali

Jak przeprowadzić ten Push Down w opublikowanej bibliotece, której klientów nie możecie przekompilować?

## Scena s04. Extract Superclass - seans i wynajem sali

**Temat ze slajdów:** 5. Extract Superclass
**Pakiet:** `pl.training.workshop.m5.s04_extractsuperclass` · **Test:** `scripts/warsztat.sh test m5/s04`
**Czas:** ~15 min

### Co widzimy

`Screening` (seans) i `PrivateEvent` (wynajem sali na firmową imprezę) mają te same pojęcia: sala, początek, czas trwania, koniec. Nie mają wspólnego typu, więc `HallPlanner` szuka kolizji w trzech pętlach z trzema kopiami warunku.

```java
if (a.hall().equals(b.hall())
        && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
```

Oba typy są wariantami jednego pojęcia - rezerwacji sali. To jest właściwy sygnał do Extract Superclass, a nie samo podobieństwo linii kodu.

### Krok 1: Extract Superclass z Screening

**W IDE:** na `Screening` Refactor > Extract Superclass, nazwa `HallBooking`, zaznacz pola `hall`, `start`, `minutes` i metody `hall()`, `start()`, `end()`. Zaznacz "Make abstract" dla klasy. IntelliJ wygeneruje konstruktor bazy i `super(...)`. Sprawdź, czy pola w bazie są `private` - jeśli IDE dało `protected`, zmień.
**Po:**

```java
public abstract class HallBooking {
    private final String hall;
    private final LocalDateTime start;
    private final int minutes;
    protected HallBooking(String hall, LocalDateTime start, int minutes) { ... }
    ...
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s04` - 19 testów zielonych.
**Co powiedzieć:** nazwa opisuje pojęcie domenowe. `BaseScreening` albo `AbstractCommon` zdradzałyby, że wydzielamy tylko z powodu duplikacji. Publiczny konstruktor `Screening` się nie zmienił.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 0 1`

### Krok 2: PrivateEvent dołącza do hierarchii

**W IDE:** w `PrivateEvent` dopisz `extends HallBooking`, zamień przypisania pól na `super(hall, start, minutes)`, usuń zduplikowane pola i akcesory (IntelliJ podświetli je jako "overrides method in HallBooking" - Safe Delete ⌘⌦).
**Po:**

```java
public final class PrivateEvent extends HallBooking {
    private final String client;
    public PrivateEvent(String client, String hall, LocalDateTime start, int minutes) {
        super(hall, start, minutes);
        this.client = client;
    }
    public static PrivateEvent rental(...) { return new PrivateEvent(..., 120); }
```

**Uruchom:** test zielony.
**Co powiedzieć:** dołączamy klasy pojedynczo, z testem po każdej. Konstruktory nie są dziedziczone, więc każdą publiczną sygnaturę i fabrykę `rental(...)` zachowujemy świadomie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 1 2`

### Krok 3: Wspólny algorytm w nadklasie

**W IDE:** w `HallPlanner` zaznacz warunek kolizji, Extract Method ⌥⌘M, potem Move F6 (albo Convert to Instance Method) do `HallBooking` jako `overlaps(HallBooking other)`. Dodaj abstrakcyjne `name()` (Pull Members Up z "Make abstract" po dodaniu `name()` w obu podklasach). Zastąp trzy pętle jedną po `List<HallBooking>`.
**Po:**

```java
List<HallBooking> bookings = new ArrayList<>(screenings);
bookings.addAll(events);
...
if (a.overlaps(b)) {
    result.add(a.name() + " x " + b.name());
}
```

**Uruchom:** test zielony, w tym przypadek graniczny "koniec 20:00 i start 20:00 to nie konflikt".
**Co powiedzieć:** dopiero wspólny typ pozwolił usunąć trzy kopie algorytmu. Publiczna sygnatura `conflicts(List<Screening>, List<PrivateEvent>)` została - klienci niczego nie zauważyli.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

`HallBooking` jest abstrakcyjna, ma prywatny stan i finalne `overlaps`. Podklasy dostarczają tylko nazwę. `S04SolutionTest` sprawdza nowy nadtyp i symetrię `overlaps` między seansem a wynajmem.

### Pułapki

- Nowy poziom zmienia `getSuperclass()`, typ deklarujący akcesorów i założenia mapperów (ORM, JSON). Encja z nową nadklasą to migracja modelu, nie refaktoryzacja.
- IntelliJ przy Extract Superclass potrafi zrobić pola `protected` - to zaproszenie do omijania walidacji.
- Przeniesienie całego `Screening` naraz zamiast dołączania klas pojedynczo utrudnia znalezienie błędu.

### Pytanie do sali

Czy `HallBooking` powinna być klasą abstrakcyjną, czy interfejsem z `default overlaps`? Co przemawia za każdą opcją?

## Scena s05. Extract Subclass - premiera z gościem

**Temat ze slajdów:** 6. Extract Subclass
**Pakiet:** `pl.training.workshop.m5.s05_extractsubclass` · **Test:** `scripts/warsztat.sh test m5/s05`
**Czas:** ~15 min

### Co widzimy

`Screening` ma flagę `premiere` i pole `guest`, które ma sens tylko dla premier (dla zwykłego seansu jest `null`). Cena i opis powtarzają `if (premiere)`. Premiera to stały wariant w całym życiu obiektu - kandydat na podklasę, nie na State.

```java
public Screening(String title, String format, boolean premiere, String guest)
...
return premiere ? base.plus(Money.of("15.00")) : base;
```

Reguła sceny: premiera kosztuje 15.00 więcej niż zwykły seans w tym samym formacie.

### Krok 1: Replace Constructor with Factory Method

**W IDE:** na konstruktorze Refactor > Replace Constructor with Factory Method, nazwa `regular`. Potem ręcznie dodaj drugą fabrykę `premiere(title, format, guest)` i w `Programme` wybierz fabrykę w zależności od `guest`. Konstruktor zrób `private`.
**Po:**

```java
public static Screening regular(String title, String format) { ... }
public static Screening premiere(String title, String format, String guest) { ... }
```

**Uruchom:** `scripts/warsztat.sh test m5/s05` - 18 testów zielonych.
**Co powiedzieć:** najpierw punkty tworzenia. Fabryka to jedyne miejsce, które za chwilę wybierze klasę runtime, a klient już nie przekazuje flagi i `null`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 0 1`

### Krok 2: Extract Subclass

**W IDE:** IntelliJ nie ma automatu Extract Subclass - ręcznie. Utwórz `PremiereScreening extends Screening` z konstruktorem pakietowym wołającym `super(title, format, true, guest)`. `Screening` zrób `sealed ... permits PremiereScreening`, konstruktor `protected`. Fabryka `premiere(...)` zwraca `new PremiereScreening(...)`.
**Po:**

```java
public sealed class Screening permits PremiereScreening { ... }
public final class PremiereScreening extends Screening {
    PremiereScreening(String title, String format, String guest) {
        super(title, format, true, guest);
    }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podklasa jest pusta, logika się nie zmieniła - zmieniła się tylko klasa runtime obiektów premierowych. `sealed` mówi wprost, że innych wariantów nie ma.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 1 2`

### Krok 3: Push Down - opis i pole guest

**W IDE:** w `PremiereScreening` Generate ⌘N > Override Methods > `describe()`, przenieś tam gałąź premierową. Następnie Refactor > Push Members Down na polu `guest` w `Screening` (albo ręcznie: pole do podklasy, parametr konstruktora bazy usunięty przez Change Signature ⌘F6).
**Po:**

```java
@Override
public String describe() {
    return super.describe() + " - premiera, gość: " + guest;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** najpierw zachowanie, potem stan - jak w Push Down z sceny s03. Zwykły seans nie ma już pola, które zawsze było `null`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 2 3`

### Krok 4: Usunięcie flagi premiere

**W IDE:** w `PremiereScreening` override `price()` = `super.price().plus(15.00)`, w bazie usuń dopłatę z `price()`. Potem Safe Delete ⌘⌦ na polu `premiere` i Change Signature ⌘F6 na konstruktorze bazy.
**Po:**

```java
@Override
public Money price() {
    return super.price().plus(PREMIERE_SURCHARGE);
}
```

**Uruchom:** test zielony. `S05SolutionTest` sprawdza klasę runtime zwracaną przez fabryki i brak pól `guest`/`premiere` w bazie.
**Co powiedzieć:** żadnego `if (premiere)`. Klient `Programme` nie zmienił się od kroku 1 - dzięki fabrykom cała ekstrakcja była dla niego niewidoczna.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m5/s05 3 4`

### Rozwiązanie i uzasadnienie

`Screening` zna tylko zwykły seans, `PremiereScreening` dodaje gościa i dopłatę. Hierarchia jest `sealed`, więc wyczerpujący `switch` po seansach jest możliwy.

### Pułapki

- Extract Subclass **celowo zmienia klasę runtime**: `getClass()`, `equals` oparte na `getClass()`, dyskryminator ORM, JSON z polem typu, serializacja.
- Rola zmienna w czasie życia (seans, który "staje się" premierą po ogłoszeniu gościa) to State albo Strategy, nie podklasa.
- Kilka niezależnych osi (premiera, maraton, seans dla szkół) daje eksplozję podklas - wtedy kompozycja.

### Pytanie do sali

Kino zaczyna ogłaszać gości tydzień po dodaniu seansu do repertuaru. Czy podklasa nadal jest dobrym modelem?

## Scena s06. Extract Interface - rola koszyka i metoda domyślna

**Temat ze slajdów:** 7.1-7.3. Extract Interface; 7.4. Metody domyślne
**Pakiet:** `pl.training.workshop.m5.s06_extractinterface` · **Test:** `scripts/warsztat.sh test m5/s06`
**Czas:** ~12 min

### Co widzimy

`Cart` przyjmuje bilety i przekąski z baru, ma dla nich dwie listy, dwa przeciążenia `add(...)` i dwie kopie liczenia VAT (bilety 8%, bar 23%). Koszyk używa z obu klas tylko `price()` i `vatPercent()`.

```java
public void add(Ticket ticket) { tickets.add(ticket); }
public void add(Snack snack) { snacks.add(snack); }
```

### Krok 1: Extract Interface z perspektywy klienta

**W IDE:** na `Ticket` Refactor > Extract Interface, nazwa `Priceable`, zaznacz **tylko** `price()` i `vatPercent()`. Nie zaznaczaj "Use interface where possible" - klientów przeniesiemy osobno. W `Snack` dopisz `implements Priceable` i `@Override` (⌥⏎).
**Po:**

```java
public interface Priceable {
    Money price();
    int vatPercent();
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s06` - 19 testów zielonych.
**Co powiedzieć:** interfejs to rola jednej grupy klientów, a nie kopia publicznego API. `title()`, `seat()` i `name()` koszyka nie obchodzą.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 0 1`

### Krok 2: Klient na rolę

**W IDE:** w `Cart` zastąp dwie listy jedną `List<Priceable>`, dwa `add` jednym `add(Priceable)` (Safe Delete drugiego przeciążenia), dwie pętle jedną. Refactor > Use Interface Where Possible pomoże przy typach zmiennych.
**Po:**

```java
private final List<Priceable> items = new ArrayList<>();

public void add(Priceable item) {
    items.add(item);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** kod klienta `cart.add(ticket)` kompiluje się dalej, więc to zmiana zgodna źródłowo. Binarnie nie: deskryptor `add(LTicket;)V` zniknął. W bibliotece zostawilibyśmy stare przeciążenie delegujące.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 1 2`

### Krok 3: Metoda domyślna vatAmount()

**W IDE:** w `Cart` zaznacz wyrażenie liczące VAT pozycji, Extract Method ⌥⌘M, potem Move F6 do `Priceable` - IntelliJ zrobi z niego metodę `default`. Implementacji nie dotykasz.
**Po:**

```java
default Money vatAmount() {
    BigDecimal rate = BigDecimal.valueOf(vatPercent());
    return new Money(price().amount().multiply(rate)
            .divide(rate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP));
}
```

**Uruchom:** test zielony. `S06SolutionTest` dodaje nową implementację (okulary 3D) spoza pierwotnej hierarchii, która dostaje `vatAmount()` za darmo.
**Co powiedzieć:** metoda domyślna używa wyłącznie operacji kontraktu, więc jest poprawna dla każdej implementacji. Nowa metoda abstrakcyjna w opublikowanym interfejsie złamałaby każdą istniejącą implementację (`AbstractMethodError` w starych binariach).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

`Cart` zależy tylko od `Priceable`. Nowy rodzaj pozycji (okulary 3D, voucher) to nowa implementacja bez zmian w koszyku.

### Pułapki

- `implements` wymusza sygnatury, nie zachowanie. Wszystkie implementacje powinny przejść ten sam test kontraktowy.
- Metoda klasy wygrywa z `default`. Konflikt dwóch niespokrewnionych interfejsów z tą samą metodą domyślną trzeba rozstrzygnąć jawnie.
- `default` nie zastąpi `equals`/`hashCode` i nie robi sekwencji wywołań atomowej.
- Zmiana parametru z klasy na interfejs zmienia deskryptor JVM (scena s15).

### Pytanie do sali

Czy `vatPercent()` w ogóle powinno być częścią roli, skoro stawka zależy od kategorii towaru, a nie od obiektu?

## Scena s07. Collapse Hierarchy - sala IMAX

**Temat ze slajdów:** 8. Collapse Hierarchy
**Pakiet:** `pl.training.workshop.m5.s07_collapsehierarchy` · **Test:** `scripts/warsztat.sh test m5/s07`
**Czas:** ~8 min

### Co widzimy

`ImaxHall extends Hall` nie ma własnego stanu. Jej override'y tylko wołają `super`, a jedyna różnica to reguła w konstruktorze: VIP w dwóch ostatnich rzędach. Nikt nie sprawdza `instanceof ImaxHall`.

```java
public ImaxHall(String name, int rows, int seatsPerRow) {
    super(name, rows, seatsPerRow, rows - 1);
}
@Override
public String describe() {
    return super.describe();
}
```

### Krok 1: Usunięcie override'ów wołających tylko super

**W IDE:** IntelliJ podkreśla je inspekcją "Method is identical to its super method". ⌥⏎ > Delete method, albo Safe Delete ⌘⌦.
**Po:**

```java
public class ImaxHall extends Hall {
    public ImaxHall(String name, int rows, int seatsPerRow) {
        super(name, rows, seatsPerRow, rows - 1);
    }
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s07` - 22 testy zielone.
**Co powiedzieć:** zostało jedno: reguła tworzenia. To nie jest kontrakt, wariant zachowania ani punkt rozszerzenia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 0 1`

### Krok 2: Reguła tworzenia do fabryki w Hall

**W IDE:** w `Hall` dodaj statyczną fabrykę `imax(name, rows, seatsPerRow)` (Refactor > Replace Constructor with Factory Method na konstruktorze `ImaxHall` generuje fabrykę w `ImaxHall` - przenieś ją F6 do `Hall` i zmień typ zwracany). W `HallCatalog` zamień `new ImaxHall(...)` na `Hall.imax(...)`.
**Po:**

```java
public static Hall imax(String name, int rows, int seatsPerRow) {
    return new Hall(name, rows, seatsPerRow, rows - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wiedza z konstruktora podklasy ma teraz nazwę w klasie bazowej. `ImaxHall` nie ma już użyć.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 1 2`

### Krok 3: Safe Delete ImaxHall

**W IDE:** Safe Delete ⌘⌦ na `ImaxHall` (przejdzie bez ostrzeżeń, bo nie ma użyć). `Hall` oznacz `final`. Uwaga: IntelliJ ma też Refactor > Inline Superclass (⌥⌘N na nadklasie), ale on scala w drugą stronę - zostawia nazwę podklasy. Tu klienci używają `Hall`, więc zostaje `Hall`.
**Po:**

```java
public final class Hall { ... }
```

**Uruchom:** test zielony. `S07SolutionTest` sprawdza, że klasy `ImaxHall` nie ma, a `Hall` jest `final`.
**Co powiedzieć:** zostaje nazwa, której używają klienci. W bibliotece `ImaxHall` zostałaby na jedno wydanie jako `@Deprecated(forRemoval = true)` typ zgodności.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Jedna finalna klasa `Hall` z nazwaną fabryką. Rozróżnienie IMAX nie było kontraktem, tylko regułą tworzenia.

### Pułapki

- Pusty typ bywa **markerem** (np. `instanceof ImaxHall` w konfiguracji projektora), punktem rozszerzenia albo kontraktem DI. Przed Collapse szukaj też w konfiguracji, adnotacjach i `ServiceLoader`.
- Usunięcie publicznej klasy łamie klientów - źródłowo i binarnie.
- Inline Superclass w IntelliJ zachowuje nazwę podklasy - sprawdź kierunek przed kliknięciem.

### Pytanie do sali

Za pół roku sale IMAX dostaną inną politykę zwrotów. Czy wtedy przywrócicie podklasę, czy dodacie pole?

## Scena s08. Replace Inheritance with Composition - licznik kliknięć

**Temat ze slajdów:** 9.1-9.4. Replace Inheritance with Composition, procedura i pułapki delegowania
**Pakiet:** `pl.training.workshop.m5.s08_composition` · **Test:** `scripts/warsztat.sh test m5/s08`
**Czas:** ~12 min

### Co widzimy

`SeatSelection extends LinkedHashSet<String>` tylko po to, by mieć `add`/`contains` za darmo, i liczy kliknięcia klienta do analityki. Pułapka self-use: odziedziczone `addAll()` woła `add()` na `this`, więc miejsca dodane hurtem liczą się podwójnie.

```java
@Override
public boolean addAll(Collection<? extends String> seats) {
    clicks += seats.size();
    return super.addAll(seats);   // wewnątrz woła nasze add() -> clicks++ drugi raz
}
```

`S08SolutionTest.startCountsBulkSelectionTwiceBecauseAddAllCallsAdd` dokumentuje: 2 miejsca, 4 kliknięcia. Drugi test `start...` pokazuje, że odziedziczone `clear()` omija licznik.

### Krok 1: Replace Inheritance with Delegation

**W IDE:** na `SeatSelection` Refactor > Replace Inheritance with Delegation. Pole delegata `seats`, zaznacz metody `add`, `addAll`, `contains`, `size`, zaznacz **Generate getter for delegated component** (żeby pokazać pułapkę). Po refaktoryzacji zamień typ pola na `Set<String>`.
**Po:**

```java
private final Set<String> seats = new LinkedHashSet<>();

public boolean addAll(Collection<? extends String> more) {
    clicks += more.size();
    return seats.addAll(more);    // add() delegata, nie nasze
}
public Set<String> getSeats() { return seats; }
```

**Uruchom:** `scripts/warsztat.sh test m5/s08` - testy `start...` robią się czerwone (pułapka usunięta), pozostałe zielone. Na snapshotach: 14 zielonych.
**Co powiedzieć:** hook woła się teraz na delegacie, nie na `this`, więc podwójne liczenie znika. Ale wygenerowany getter wydaje delegata - `getSeats().add("Z1")` omija licznik (test `generatedGetterLeaksTheDelegate`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s08 0 1`

### Krok 2: Encapsulate Collection i wąska fasada

**W IDE:** Safe Delete ⌘⌦ na `getSeats()`, dodaj `seats()` zwracające `List.copyOf(seats)`. Klasę oznacz `final`. `addAll` przepisz na pętlę po własnym `add` - self-use jest teraz bezpieczne, bo klasa jest finalna i sami kontrolujemy oba końce.
**Po:**

```java
public List<String> seats() {
    return List.copyOf(seats);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie tracimy przypisywalność do `Set`, `remove`/`clear`/`retainAll` i `equals`/`hashCode` zbioru. Tę listę trzeba wypisać i uzgodnić z klientami - to nie jest zgodny zamiennik publicznego `Set`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s08 1 2`

### Rozwiązanie i uzasadnienie

Finalna klasa z prywatnym zbiorem, jawnym API i defensywną kopią. Licznik nie zależy od szczegółów implementacji `LinkedHashSet`.

### Pułapki

- Self-use w JDK nie jest częścią kontraktu - wynik start zależy od tego, czy `HashSet.addAll` akurat woła `add`.
- Metoda fluent delegata (np. `stream()`, `subList()`) zwraca obiekt delegata, nie wrappera - zmiany omijają logikę.
- `synchronized` w wrapperze blokuje inny monitor niż delegat.
- Znikają odziedziczone `equals`/`hashCode` - zmienia się zachowanie w `HashSet<SeatSelection>`.

### Pytanie do sali

Które testy wykryłyby, że wrapper przypadkiem zwraca delegata? Spy, callback, a może test tożsamości `this`?

## Scena s09. Overriding a overloading - pułapka po Extract Superclass

**Temat ze slajdów:** 2.1. Overriding i dynamiczna dyspozycja; 2.2-2.3. Overloading - wybór statyczny
**Pakiet:** `pl.training.workshop.m5.s09_overloading` · **Test:** `scripts/warsztat.sh test m5/s09`
**Czas:** ~10 min

### Co widzimy

Stan po Extract Superclass: `StudentTicket extends Ticket`. `PriceList` ma dwa przeciążenia `price(Ticket)` i `price(StudentTicket)`. Dopóki klient miał `List<StudentTicket>`, działało. Po migracji na `List<Ticket>` ten sam tekst `priceList.price(ticket)` wybiera `price(Ticket)`, bo przeciążenie wybiera kompilator po typie deklarowanym. Student płaci pełną cenę.

```java
public Money total(List<Ticket> tickets) {
    ...
    total = total.plus(priceList.price(ticket));   // zawsze price(Ticket)
```

Druga pułapka: `public boolean equals(Ticket other)` to przeciążenie, nie override - `List.contains` go nie widzi.

### Krok 1: Replace Overloading with Overriding

**W IDE:** w `Ticket` dodaj `public int discountPercent() { return 0; }`, w `StudentTicket` Generate ⌘N > Override Methods > `discountPercent()` = 25. `PriceList.price(Ticket)` liczy z `ticket.discountPercent()`. Safe Delete ⌘⌦ na `price(StudentTicket)`.
**Po:**

```java
public Money price(Ticket ticket) {
    return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s09` - `startPicksOverloadByDeclaredType` czerwony (pułapka usunięta), `overridingDispatchesOnRuntimeClass` zielony: 58.75 zamiast 65.00.
**Co powiedzieć:** dyspozycja dynamiczna wybiera implementację według klasy obiektu, rzutowanie na nadtyp tego nie wyłącza. Przeciążenie to decyzja kompilatora, zapisana na stałe w `.class` klienta.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 0 1`

### Krok 2: equals(Object) z @Override

**W IDE:** usuń `equals(Ticket)`, Generate ⌘N > equals() and hashCode(), szablon z `getClass()`, pola `title` i `basePrice`.
**Po:**

```java
@Override
public boolean equals(Object other) {
    return other != null && getClass() == other.getClass()
            && title.equals(((Ticket) other).title) && basePrice.equals(((Ticket) other).basePrice);
}
```

**Uruchom:** test zielony - `alreadyInCart` znajduje równy bilet.
**Co powiedzieć:** `@Override` zamienia cichy błąd przeciążenia w błąd kompilacji. Warto je mieć na każdym nadpisaniu - także przy Pull Up, bo metoda w podklasie może nagle nadpisać albo przestać nadpisywać.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s09 1 2`

### Rozwiązanie i uzasadnienie

Zniżka jest zachowaniem biletu (override), a nie decyzją kalkulatora (overload). Jedno `price(Ticket)` działa poprawnie dla każdego typu deklarowanego.

### Pułapki

- Po Pull Up / Extract Superclass ten sam kod źródłowy może wybrać inne przeciążenie - testy przez typ bazowy to wykrywają, testy przez typ konkretny nie (`S09EquivalenceTest` jest zielony nawet dla start!).
- Stary `.class` klienta woła dawną sygnaturę przeciążenia, nawet gdy dodamy nowe, "lepsze".
- `equals` porównujące `instanceof` zamiast `getClass()` łamie symetrię między bazą a podklasą.

### Pytanie do sali

Dlaczego test równoważności tej sceny przechodzi dla start, mimo że start liczy źle?

## Scena s10. Ukrywanie pól i metod static

**Temat ze slajdów:** 2.3. Pola nie są polimorficzne; 2.1. `static` jest ukrywana
**Pakiet:** `pl.training.workshop.m5.s10_fieldhiding` · **Test:** `scripts/warsztat.sh test m5/s10`
**Czas:** ~8 min

### Co widzimy

`StudentTicket` deklaruje pole `type` i statyczne `category()` o tych samych nazwach co `Ticket`. Wygląda na nadpisanie, ale to ukrycie. `label()` jest skompilowane w `Ticket`, więc dla studenta zwraca `"BILET: NORMAL"`.

```java
StudentTicket student = new StudentTicket();
Ticket sameObject = student;
student.type      // "STUDENT"
sameObject.type   // "NORMAL" - drugi slot w tym samym obiekcie
```

Testy `start...` czytają oba sloty refleksją, żeby moduł kompilował się także po naprawie na żywo.

### Krok 1: Jedno pole zamiast dwóch slotów

**W IDE:** w `Ticket` Refactor > Encapsulate Fields na `type` (getter `type()`, pole `private`), dodaj konstruktor `protected Ticket(String type)` i `public Ticket() { this("NORMAL"); }`. W `StudentTicket` usuń pole (Safe Delete) i dodaj `super("STUDENT")`.
**Po:**

```java
private final String type;
protected Ticket(String type) {
    this.type = type;
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s10` - testy `start...` czerwone po naprawie start, `step1FixesFieldButStaticIsStillHidden` zielony: `"BILET: STUDENT"`.
**Co powiedzieć:** pole ukryte to dwa niezależne sloty. Który zobaczysz, zależy od typu referencji. Stan przekazujemy przez konstruktor, a nie redeklarujemy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s10 0 1`

### Krok 2: category() jako metoda instancji

**W IDE:** usuń `static` z obu `category()` (ręcznie; IntelliJ podpowie ⌥⏎ "Make not static" przy wywołaniu), dodaj `@Override` w `StudentTicket`.
**Po:**

```java
@Override
public String category() {
    return "BILET ULGOWY";
}
```

**Uruchom:** test zielony: `"BILET ULGOWY: STUDENT"` bez względu na typ referencji.
**Co powiedzieć:** metody statyczne są wiązane w czasie kompilacji według typu, w którym stoi wywołanie. Jeśli zachowanie ma zależeć od obiektu, musi być metodą instancji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s10 1 2`

### Rozwiązanie i uzasadnienie

Jedno prywatne pole, jedna polimorficzna metoda. `label()` daje ten sam wynik dla `Ticket t = new StudentTicket()` i `StudentTicket s`.

### Pułapki

- Pull Up Field bez usunięcia deklaracji w podklasie tworzy ukrycie zamiast przeniesienia.
- `static synchronized` blokuje obiekt `Class` typu deklarującego - Pull Up takiej metody zmienia monitor (dwie podklasy zaczynają dzielić jedną blokadę).
- Wywołanie statycznej metody przez instancję (`ticket.category()`) kompiluje się z ostrzeżeniem i myli czytelnika.

### Pytanie do sali

Gdzie w waszym kodzie są stałe `public static final` redeklarowane w podklasach? Czy ktoś czyta je przez typ bazowy?

## Scena s11. Konstruktor wołający metodę nadpisywalną

**Temat ze slajdów:** 2.4. Konstruktory i inicjalizacja; 3.1-3.3 (nie wołaj override z konstruktora, żeby umożliwić Pull Up)
**Pakiet:** `pl.training.workshop.m5.s11_constructorcall` · **Test:** `scripts/warsztat.sh test m5/s11`
**Czas:** ~8 min

### Co widzimy

Konstruktor `Ticket` zapamiętuje etykietę, wołając `describe()`. `VipTicket` nadpisuje `describe()` i używa pola `lounge`, które w tym momencie jest jeszcze `null` - konstruktor bazy kończy się, zanim podklasa przypisze swoje pola.

```java
public Ticket(String seat) {
    this.seat = seat;
    this.label = describe();       // javac -Xlint:this-escape ostrzega
}
```

`new VipTicket("K12", "Salonik A").label()` zwraca `"Miejsce K12 (VIP: null)"` - na zawsze, bo wynik jest w polu `final`.

### Krok 1: Szybka naprawa - prolog konstruktora (Java 25)

**W IDE:** w `VipTicket` przenieś `this.lounge = lounge;` **przed** `super(seat);` (ręcznie; Java 25, JEP 513 dopuszcza przypisanie pól w prologu).
**Po:**

```java
public VipTicket(String seat, String lounge) {
    this.lounge = lounge;
    super(seat);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s11` - `startSubclassSeesUninitializedField` czerwony (pułapka usunięta), pozostałe zielone.
**Co powiedzieć:** działa, ale naprawa jest lokalna i krucha - każda następna podklasa musi pamiętać o kolejności. Ostrzeżenie `this-escape` w `Ticket` zostaje.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s11 0 1`

### Krok 2: Konstruktor bez wywołań nadpisywalnych

**W IDE:** w `Ticket` Inline ⌥⌘N na polu `label` (Replace Field with Query): `label()` zwraca `describe()`. W `VipTicket` przywróć zwykłą kolejność `super(seat)`, potem pole.
**Po:**

```java
public final String label() {
    return describe();
}
```

**Uruchom:** test zielony. Ostrzeżenie `this-escape` znika z Build.
**Co powiedzieć:** etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany. Poprawność nie zależy od kolejności inicjalizacji. Inne naprawy: przekazanie wyliczonej wartości przez parametr konstruktora albo fabryka, która woła metodę po `new`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Konstruktor bazy tylko przypisuje pola. Wszystko, co może być nadpisane, wykonuje się po zakończeniu konstrukcji.

### Pułapki

- Pull Up metody, którą ktoś potem wywoła z konstruktora bazy "bo tak wygodnie".
- Pole z inicjalizatorem (`private final String lounge = ...`) też jest przypisywane po `super(...)` - chyba że to stała kompilacji, wtedy działa "przypadkiem".
- Podklasa nie może przypisać odziedziczonego pola `final` - trzeba je przekazać przez konstruktor bazy.

### Pytanie do sali

Czy włączylibyście `-Xlint:this-escape -Werror` w buildzie legacy? Ile ostrzeżeń byście dostali?

## Scena s12. Generyki i metody bridge

**Temat ze slajdów:** 2.5-2.8. Sygnatury po erasure i metody bridge; 10.2-10.3. Refleksja
**Pakiet:** `pl.training.workshop.m5.s12_bridgemethods` · **Test:** `scripts/warsztat.sh test m5/s12`
**Czas:** ~10 min

### Co widzimy

Reguły cenowe `StandardRule` i `StudentRule` nie mają wspólnego typu. `RuleRegistry` znajduje je refleksją: każda metoda o nazwie `apply` rejestruje regułę pod typem swojego parametru.

```java
for (Method method : rule.getClass().getDeclaredMethods()) {
    if (method.getName().equals("apply")) {
        this.rules.put(method.getParameterTypes()[0], rule);
    }
}
```

Działa, dopóki w klasie reguły jest dokładnie jedna metoda `apply`. Typy biletów (`Ticket`, `StandardTicket`, `StudentTicket`) są stabilnym kontraktem sceny.

### Krok 1: Extract Interface generycznej roli (i naprawa refleksji)

**W IDE:** na `StudentRule` Refactor > Extract Interface, nazwa `PriceRule`, metoda `apply`. Ręcznie zrób interfejs generycznym: `PriceRule<T extends Ticket>` z `Money apply(T ticket)`, reguły `implements PriceRule<StudentTicket>` / `PriceRule<StandardTicket>`. **Uruchom test - jest czerwony:** `supportedTypes()` zwraca też `"Ticket"`. Pokaż `javap -p` na klasie (albo test `genericInterfaceAddsSyntheticBridgeMethod`): kompilator dodał syntetyczne `apply(Ticket)`. Dopisz `&& !method.isBridge()` w rejestrze.
**Po:**

```java
if (method.getName().equals("apply") && !method.isBridge()) {
```

**Uruchom:** `scripts/warsztat.sh test m5/s12` - 10 testów zielonych.
**Co powiedzieć:** po erasure `apply(T)` to `apply(Ticket)`, więc JVM potrzebuje mostu z rzutowaniem. Źródło go nie pokazuje, refleksja i stack trace - tak. Surowe wywołanie mostu z niewłaściwym biletem kończy się `ClassCastException`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 0 1`

### Krok 2: Jawny kontrakt zamiast refleksji

**W IDE:** w `PriceRule` dodaj `Class<T> ticketType()`, zaimplementuj w regułach (⌥⏎ > Implement methods). `RuleRegistry` przyjmuje `PriceRule<?>...`, rejestruje po `ticketType()`, a wywołuje przez pomocniczą metodę generyczną z `ticketType().cast(ticket)`.
**Po:**

```java
private static <T extends Ticket> Money apply(PriceRule<T> rule, Ticket ticket) {
    return rule.apply(rule.ticketType().cast(ticket));
}
```

**Uruchom:** test zielony, brak ostrzeżeń `unchecked`.
**Co powiedzieć:** zgadywanie typu z sygnatury metody zastąpiliśmy jawnym kontraktem. Brak reguły to czytelny `IllegalStateException`, a nie `NullPointerException` z refleksji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s12 1 2`

### Rozwiązanie i uzasadnienie

`PriceRule<T>` z `ticketType()`. Rejestr nie używa refleksji, więc metody syntetyczne nie mają znaczenia.

### Pułapki

- Metody bridge pojawiają się też przy kowariantnym typie zwracanym i przy `Comparable<Ticket>` (`compareTo(Object)`).
- Przeniesienie metody w hierarchii generycznej (Pull Up do bazy z innym parametrem typu) zmienia, gdzie i czy powstaje bridge - kod szukający metod po nazwie widzi co innego.
- Frameworki (AOP, walidacja, mapowanie) zwykle filtrują `isBridge()`/`isSynthetic()` - własny kod legacy często nie.

### Pytanie do sali

Gdzie w waszym systemie ktoś iteruje po `getDeclaredMethods()` i czy filtruje metody syntetyczne?

## Scena s13. Hierarchie sealed i wyczerpujący switch

**Temat ze slajdów:** 2.5-2.8. `sealed`, `permits`, `MatchException`
**Pakiet:** `pl.training.workshop.m5.s13_sealed` · **Test:** `scripts/warsztat.sh test m5/s13`
**Czas:** ~12 min

### Co widzimy

Otwarta hierarchia `Ticket` z rekordami `StandardTicket`, `StudentTicket`, `SeniorTicket` i kalkulator z łańcuchem `instanceof` zakończonym cichym `return 0`.

```java
if (ticket instanceof StudentTicket) {
    return 25;
} else if (ticket instanceof SeniorTicket) {
    return 30;
}
return 0;
```

`startSilentlyGivesUnknownTicketNoDiscount` dopisuje w runtime nowy typ biletu (dziecięcy, 40%) - dostaje 0% zniżki bez żadnego ostrzeżenia.

### Krok 1: sealed permits

**W IDE:** na `Ticket` ⌥⏎ > "Seal class" (IntelliJ wypełni `permits` znalezionymi implementacjami) albo ręcznie `public sealed interface Ticket permits StandardTicket, StudentTicket, SeniorTicket`. Rekordy są `final`, więc nic więcej nie trzeba.
**Po:**

```java
public sealed interface Ticket permits StandardTicket, StudentTicket, SeniorTicket {
```

**Uruchom:** `scripts/warsztat.sh test m5/s13` - `startSilently...` czerwony po naprawie start (obcy typ nie przejdzie), reszta zielona.
**Co powiedzieć:** zamknięta lista wariantów w jednym miejscu. Samo `sealed` nie naprawia kalkulatora - łańcuch `if` nie jest wyczerpujący.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s13 0 1`

### Krok 2: Wyczerpujący switch z pattern matching

**W IDE:** na `if` ⌥⏎ > "Replace 'if' with 'switch'", potem usuń `default` i dopisz `case StandardTicket _ -> 0`.
**Po:**

```java
return switch (ticket) {
    case StandardTicket _ -> 0;
    case StudentTicket _ -> 25;
    case SeniorTicket _ -> 30;
};
```

**Uruchom:** test zielony.
**Co powiedzieć:** bez `default` kompilator sprawdza wyczerpanie. Każdy wariant jest wymieniony jawnie, także ten z zerową zniżką.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s13 1 2`

### Krok 3: Nowy typ biletu wymusza obsługę

**W IDE:** utwórz `record ChildTicket(Money basePrice) implements Ticket`, dopisz do `permits`. Kompilacja `PriceCalculator` się wywraca: "the switch expression does not cover all possible input values". ⌥⏎ > "Create missing branch", wartość 40.
**Po:**

```java
case ChildTicket _ -> 40;
```

**Uruchom:** test zielony: bilet dziecięcy 25.00 -> 15.00.
**Co powiedzieć:** to jest cała wartość `sealed`: nowy wariant zamienia ciche błędy w błędy kompilacji. Ale tylko przy rekompilacji - test `oldExhaustiveSwitchThrowsMatchExceptionForNewVariant` kompiluje `PriceCalculator` ze step2 i uruchamia z hierarchią ze step3: linkuje się, a `switch` rzuca `MatchException`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s13 2 3`

### Rozwiązanie i uzasadnienie

`sealed` + wyczerpujący `switch` bez `default`. Wariant dodany w źródle jest obsłużony, bo inaczej build się nie uda.

### Pułapki

- `default` w `switch` po typie `sealed` wyłącza sprawdzanie wyczerpania - wracamy do cichego błędu.
- `permits` obejmuje tylko bezpośrednie podtypy. Pośrednia nadklasa (Extract Superclass w środku hierarchii) zmienia oba poziomy.
- Nowy wariant jest zgodny binarnie, ale stare skompilowane `switch`e rzucą `MatchException` - dotyczy wtyczek i modułów budowanych osobno.

### Pytanie do sali

Kiedy otwarta hierarchia jest lepsza niż `sealed`? Kto w waszym systemie dopisuje implementacje spoza repozytorium?

## Scena s14. Współdzielenie implementacji a podtypowanie

**Temat ze slajdów:** 1.1-1.2. Hierarchia jest częścią zachowania; 9.1. Replace Inheritance with Composition - kiedy?
**Pakiet:** `pl.training.workshop.m5.s14_reuse` · **Test:** `scripts/warsztat.sh test m5/s14`
**Czas:** ~10 min

### Co widzimy

`CorporateAccount extends LoyaltyAccount` tylko po to, by nie pisać drugi raz naliczania punktów (1 pkt za pełne 10.00). Firma zbiera punkty do rocznego rabatu i nie wymienia ich na bilety, więc odziedziczoną operację blokuje wyjątkiem.

```java
@Override
public boolean redeemFreeTicket() {
    throw new UnsupportedOperationException("konto firmowe nie wymienia punktów na bilety");
}
```

Zgodna sygnatura nie dowodzi zastępowalności: każdy klient `LoyaltyAccount` może dostać ten obiekt i wybuchnąć.

### Krok 1: Extract Delegate - współdzielona implementacja

**W IDE:** na `LoyaltyAccount` Refactor > Extract > Delegate, nazwa `PointsLedger`, zaznacz pole `points` oraz logikę `earn` i odejmowania punktów (IntelliJ wygeneruje delegowanie). Nazwij metodę odejmowania `spend(int)`.
**Po:**

```java
private final PointsLedger ledger = new PointsLedger();

public boolean redeemFreeTicket() {
    return ledger.spend(100);
}
```

**Uruchom:** `scripts/warsztat.sh test m5/s14` - 12 testów zielonych.
**Co powiedzieć:** kod, który chcieliśmy współdzielić, ma teraz własną klasę. Reużycie nie wymaga już dziedziczenia - można go użyć z dowolnego miejsca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s14 0 1`

### Krok 2: Konto firmowe przez kompozycję + rola dla raportu

**W IDE:** w `CorporateAccount` usuń `extends LoyaltyAccount`, dodaj własne pole `PointsLedger`, metody `earn`, `owner`, `points` (albo Refactor > Replace Inheritance with Delegation i usuń `redeemFreeTicket`). Na `LoyaltyAccount` Refactor > Extract Interface `PointsHolder` z `owner()` i `points()`, `CorporateAccount implements PointsHolder`, `LoyaltyReport.line(PointsHolder)`.
**Po:**

```java
public final class CorporateAccount implements PointsHolder {
    private final PointsLedger ledger = new PointsLedger();
    ...
}
```

**Uruchom:** test zielony. `S14SolutionTest` sprawdza, że `CorporateAccount` nie jest `LoyaltyAccount` i nie ma `redeemFreeTicket`.
**Co powiedzieć:** wspólny kontrakt (właściciel i saldo) jest prawdziwy dla obu kont, więc tu podtypowanie jest uczciwe. Operacji, której nie da się wykonać, po prostu nie ma - nie trzeba jej blokować.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s14 1 2`

### Rozwiązanie i uzasadnienie

Implementację współdzieli `PointsLedger` (kompozycja), kontrakt - `PointsHolder` (interfejs). Dziedziczenie zostało tam, gdzie nie było potrzebne: nigdzie.

### Pułapki

- `UnsupportedOperationException` w override to podręcznikowy sygnał złamanej substytucji (tak samo `ReadOnlyAccount.withdraw`).
- Klienci, którzy przyjmowali `LoyaltyAccount` i dostawali konto firmowe, przestaną się kompilować - dobrze, każdy z nich trzeba przejrzeć.
- Wydzielony współpracownik nie powinien wiedzieć, kto go używa (żadnych `if (owner instanceof ...)`).

### Pytanie do sali

Pytania ze slajdów 1.1-1.2: czy te klasy mają ten sam kod, czy są wariantami jednego pojęcia? Jaka jest odpowiedź dla każdej pary w tej scenie?

## Scena s15. Zgodność binarna, refleksja i adnotacje

**Temat ze slajdów:** 1.3. Warstwy zgodności; 10.2-10.3. Zgodność binarna, refleksja i adnotacje
**Pakiet:** `pl.training.workshop.m5.s15_compatibility` · **Test:** `scripts/warsztat.sh test m5/s15`
**Czas:** ~15 min

### Co widzimy

Biblioteka kasowa: `StandardTicket` i `StudentTicket` deklarują własne `price()` z adnotacją `@Column("cena")`. `TicketExporter` szuka kolumn przez `getDeclaredMethods()` klasy runtime. `BoxOfficeApi.quote(StudentTicket)` jest używane przez **skompilowane** wtyczki partnerów. Ta scena jest nietypowa: główną demonstracją jest `S15SolutionTest`, który przez `javax.tools` kompiluje wtyczkę przeciw jednej wersji API (pliki wariantu z pakietem podmienionym na `api`) i uruchamia ją z inną - jak podmiana JAR-a na serwerze.

```java
for (Method m : ticket.getClass().getDeclaredMethods())   // tylko klasa runtime
```

### Krok 1: Przygotowanie refleksji przed ruchem w hierarchii

**W IDE:** w `TicketExporter` zmień `getDeclaredMethods()` na `getMethods()` (publiczne, także odziedziczone).
**Po:**

```java
Method[] candidates = ticket.getClass().getMethods();
```

**Uruchom:** `scripts/warsztat.sh test m5/s15` - 20 testów zielonych.
**Co powiedzieć:** kod szukający metod po nazwie albo adnotacji to ukryty kontrakt na typ deklarujący. Zabezpieczamy go przed ruchem, a nie po awarii.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 0 1`

### Krok 2: Pull Up price() z adnotacją

**W IDE:** Refactor > Pull Members Up na `price()` ze `StudentTicket` - ciała są różne, więc najpierw Extract Method ⌥⌘M na procencie zniżki (`discountPercent()`), potem Pull Up `price()` z adnotacją, a `discountPercent()` jako abstract. Dodaj `final`.
**Po:**

```java
@Column("cena")
public final Money price() {
    return basePrice.minus(basePrice.percent(discountPercent()));
}
```

**Uruchom:** test zielony. `pullUpIsBinaryCompatibleForCallers`: wtyczka skompilowana przeciw step1 działa z API ze step2 (JVM znajduje `StudentTicket.price()` w nadklasie). `pullUpHidesAnnotatedMethodFromDeclaredMethodsLookup`: `getDeclaredMethods()` na podklasie już nie widzi kolumny - eksporter ze start by ją zgubił.
**Co powiedzieć:** zgodność binarna dla wywołań nie oznacza zgodności refleksyjnej. `@Inherited` tu nie pomoże - działa tylko dla adnotacji klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 1 2`

### Krok 3: Generalize Parameter Type - zmiana łamiąca binarnie

**W IDE:** na `BoxOfficeApi.quote` Change Signature ⌘F6, typ parametru `StudentTicket` -> `Ticket`. Wszystko się kompiluje.
**Po:**

```java
public Money quote(Ticket ticket) {
    return ticket.price();
}
```

**Uruchom:** test zielony. `generalizedParameterBreaksOldBinary`: stara wtyczka dostaje `NoSuchMethodError` (deskryptor `quote(Lapi/StudentTicket;)` zniknął). `generalizedParameterIsSourceCompatible`: po rekompilacji ta sama wtyczka działa.
**Co powiedzieć:** pełny build sprawdza zgodność źródłową. Stara wtyczka, której nie przebudujemy, widzi zmianę deskryptora JVM - IDE tego nie zgłosi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 2 3`

### Krok 4: Przestarzałe przeciążenie delegujące

**W IDE:** dodaj z powrotem `quote(StudentTicket)` z `@Deprecated(since = "2.0")`, delegujące do `quote((Ticket) ticket)`.
**Po:**

```java
@Deprecated(since = "2.0")
public Money quote(StudentTicket ticket) {
    return quote((Ticket) ticket);
}
```

**Uruchom:** test zielony. `delegatingOverloadRestoresBinaryCompatibility`: stara wtyczka działa z API ze step4.
**Co powiedzieć:** stary deskryptor zostaje na okres przejściowy, a usunięcie to osobna, zapowiedziana zmiana łamiąca. Uwaga na przeciążenia: nowi klienci z typem `StudentTicket` wybiorą przestarzałą metodę (scena s09).
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh diff m5/s15 3 4`

### Rozwiązanie i uzasadnienie

Eksporter odporny na przesunięcia metod, `price()` w bazie, API przyjmujące `Ticket` z przejściowym przeciążeniem. Każda warstwa zgodności jest sprawdzona osobnym testem.

### Pułapki

- Zgodność binarna nie dowodzi zgodności źródłowej ani zachowania - i odwrotnie.
- Push Down, zmiana typu parametru lub wyniku, usunięcie klasy - łamią stare binaria. Pull Up, dodanie metody do klasy - zwykle nie.
- `getDeclaringClass()`, `getDeclaredMethods()`, adnotacje metod i metody bridge zmieniają się przy każdym ruchu członka.
- Narzędzia typu japicmp / revapi w CI sprawdzają zgodność binarną automatycznie.

### Pytanie do sali

Którą warstwę zgodności z tabeli 1.3 sprawdza wasz obecny pipeline, a której nie sprawdza żadna automatyzacja?

## Scena s16. Serializacja i proxy - hierarchia jako format danych

**Temat ze slajdów:** 10.4-10.6. Serializacja, ORM i DI
**Pakiet:** `pl.training.workshop.m5.s16_serializationproxy` · **Test:** `scripts/warsztat.sh test m5/s16`
**Czas:** ~12 min

### Co widzimy

`StudentTicket implements Serializable` (UID 1L) z polami `title`, `seat`, `studentId`. Postać strumienia zawiera nazwę klasy i pola każdego poziomu hierarchii osobno. `TicketPricing` to klasa `final` bez interfejsu. Scena nietypowa: główną demonstracją jest `S16SolutionTest` z "plikiem .ser z v1" zamrożonym jako stała Base64 (bilet zapisany przez klasę ze start).

```java
public final class TicketPricing {
    public Money studentPrice(Money basePrice) { ... }
}
```

### Krok 1: Extract Superclass + Pull Up Field - ciche zgubienie danych

**W IDE:** Refactor > Extract Superclass na `StudentTicket`, nazwa `Ticket`, pola `title`, `seat` + akcesory. `Ticket implements Serializable` (UID 1L), w `StudentTicket` zostaje UID 1L.
**Po:**

```java
public abstract class Ticket implements Serializable {
    private final String title;
    private final String seat;
```

**Uruchom:** `scripts/warsztat.sh test m5/s16` - 13 testów zielonych. `pulledUpFieldsAreSilentlyLostWhenReadingOldData`: stare dane odczytane przez step1 dają `"null null (legitymacja S-123)"`. Bez wyjątku.
**Co powiedzieć:** pola przeszły do segmentu poziomu `Ticket`, którego w starym strumieniu nie ma. Stały `serialVersionUID` zapobiega wyjątkowi, ale nie migruje stanu - to najgorsza możliwa kombinacja.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh diff m5/s16 0 1`

### Krok 2: Serialization Proxy

**W IDE:** ręcznie (brak automatu): w `StudentTicket` prywatny `record SerializedForm(title, seat, studentId) implements Serializable` z `readResolve()` wołającym publiczny konstruktor, `writeReplace()` zwracające `SerializedForm`, `readObject(...)` rzucające `InvalidObjectException`. `Ticket` przestaje być `Serializable`. UID podbity do 2L.
**Po:**

```java
@Serial
private Object writeReplace() {
    return new SerializedForm(title(), seat(), studentId);
}
```

**Uruchom:** test zielony. `serializationProxyRejectsOldDataLoudly`: stare dane odrzucone `InvalidClassException`. `serializationProxyWritesFlatFormIndependentOfHierarchy`: poziom `Ticket` nie występuje w strumieniu.
**Co powiedzieć:** hierarchia klasy przestaje być formatem danych - przyszłe Pull Up/Push Down nie zmienią strumienia. Podbity UID to świadoma deklaracja nowego formatu: błąd głośny zamiast cichego. Migracja starych danych to osobne zadanie (czytnik v1 albo konwersja offline).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh diff m5/s16 1 2`

### Krok 3: Extract Interface dla dynamicznego proxy

**W IDE:** na `TicketPricing` Refactor > Extract Interface, nazwa `Pricing`, metoda `studentPrice`. Klasa może zostać `final`.
**Po:**

```java
public interface Pricing {
    Money studentPrice(Money basePrice);
}
```

**Uruchom:** test zielony. `jdkProxyCannotWrapFinalClassWithoutInterface`: `Proxy.newProxyInstance` dla klasy rzuca `IllegalArgumentException`. `extractedInterfaceAllowsDynamicProxy`: proxy audytowe liczy wywołania.
**Co powiedzieć:** `java.lang.reflect.Proxy` opakowuje tylko interfejsy. Proxy klasowe (CGLIB/ByteBuddy w Spring AOP, lazy loading Hibernate) robi podklasę, więc nie ruszy klasy `final` ani metod `final` - a `@Transactional` na metodzie `final` po cichu nie działa.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh diff m5/s16 2 3`

### Rozwiązanie i uzasadnienie

Serializowana postać to płaski record niezależny od hierarchii. Serwis ma interfejs roli, który kontener DI może opakować bez dziedziczenia.

### Pułapki

- `final` dodane "dla bezpieczeństwa" przy refaktoryzacji potrafi wyłączyć transakcje, cache i lazy loading.
- Self-invocation: `this.innaMetoda()` wewnątrz serwisu omija proxy (także po Extract Interface).
- Hierarchia encji JPA to migracja modelu: strategia dziedziczenia, dyskryminator, tabele, zapytania polimorficzne. Test z prawdziwym dostawcą: `flush`, `clear`, ponowny odczyt.
- DI po Extract Interface: dwie implementacje tej samej roli to niejednoznaczne wstrzykiwanie - uruchom kontekst aplikacji w teście.

### Pytanie do sali

Gdzie wasz system trzyma obiekty zserializowane Javą (sesje, cache, kolejki)? Czy macie choć jeden plik `.ser` z poprzedniej wersji w testach?

## Proponowana kolejność pokazu

**Ścieżka krótka (~80 min)** - rdzeń katalogu refaktoryzacji plus dwie najgroźniejsze pułapki:

1. s01 Pull Up Method (12 min)
2. s03 Push Down i asymetria (12 min)
3. s04 Extract Superclass (15 min)
4. s06 Extract Interface i `default` (12 min)
5. s08 Kompozycja i self-use (12 min)
6. s09 Overloading po Extract Superclass (10 min)
7. s15 tylko kroki 3-4 z `jump m5/s15 2` (7 min)

**Ścieżka pełna (~3 h 10 min, z przerwą)** - według agendy slajdów:

1. Semantyka Javy: s09, s10, s11, s12, s13 (~48 min)
2. Pull Up / Push Down: s01, s02, s03 (~34 min)
3. Ekstrakcje: s04, s05, s06 (~42 min)
4. Collapse i kompozycja: s07, s14, s08 (~30 min)
5. Zgodność i integracje: s15, s16 (~27 min)

Jeśli brakuje czasu, sceny s02, s07 i s10 dobrze działają jako praca własna uczestników (zadania modułu 5).
