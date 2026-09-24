# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Dwadzieścia małych scen w domenie kina CineLegacy. Każda pokazuje jedną refaktoryzację do wzorca (albo od wzorca) ze slajdów modułu 6: od Strategy i State, przez Factory, Builder, Decorator i Observer, po rodzinę Composite, Visitor i mapę decyzji. Każda scena ma kod wyjściowy `start` z zapachem lub pułapką i gotowe snapshoty `stepN` po każdym ruchu, więc w dowolnym momencie można przeskoczyć do kolejnego kroku. Test równoważności (vitest) pilnuje, że wzorzec wprowadzamy bez zmiany obserwowalnego zachowania, a tam, gdzie zachowanie celowo się zmienia (moment wyboru strategii, nowy niezmiennik buildera), osobny test to dokumentuje.

Kod portu TypeScript leży w `typescript/src/workshop/m6/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m6/sNN_.../`. Tam, gdzie TypeScript nie ma odpowiednika mechanizmu Javy (rekordy, `sealed`, `final`, enum z polami, przeciążenia konstruktorów), scena używa najbliższego idiomu TypeScriptu, a akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m6              # sceny i kroki modułu 6
scripts/warsztat.sh --lang ts test m6/s08          # testy jednej sceny
scripts/warsztat.sh --lang ts test m6              # wszystkie sceny modułu (473 testy)
scripts/warsztat.sh --lang ts diff m6/s08 1 2      # co zmienia krok 2 względem kroku 1 (0 = start)
scripts/warsztat.sh --lang ts diff m6/s08 1 2 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m6/s08 2        # kopiuje step2 do start (przeskok, gdy brakuje czasu)
scripts/warsztat.sh --lang ts next m6/s08          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m6/s08         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w VS Code uruchamiamy test sceny (skrypt albo rozszerzenie Vitest); zielony pasek jest dowodem, że wzorzec nie zmienił kontraktu.
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów, więc tam, gdzie scena opiera się na kontroli kompilatora (wyczerpujący `switch`, klasa bez `export`, brak `add` na liściu), uruchom też `npm run typecheck` w katalogu `typescript` albo patrz na podkreślenia w VS Code.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m6/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc potem wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Pracujemy w katalogu `start`. Gdy coś pójdzie nie tak, `next` (albo `jump` do właściwego kroku), a po scenie `reset`.
- Test równoważności przechodzi przez `start` i wszystkie kroki. Jeśli uczestnicy pracują równolegle, to ten sam test jest ich kryterium ukończenia.
- Testy wołają sceny przez stabilne punkty wejścia (klient w rodzaju `PriceBoard`, `CancellationDesk`, `PaymentServices` albo zachowany konstruktor), dzięki czemu `jump` do dowolnego kroku przechodzi. Testy dokumentujące pułapkę samego `start` (s11, s17) po `jump` są pomijane (`context.skip(...)`, vitest pokazuje je jako "skipped") - to zamierzone.
- Refaktoryzacje w VS Code: zaznaczenie i ⌃⇧R (Refactor...) daje Extract to method / Extract to function / Extract to constant, F2 to Rename Symbol, ⌘. (Quick Fix) m.in. Implement interface i Add missing member, a Move to a new file / Move to file przenosi deklaracje najwyższego poziomu. Change Signature, Safe Delete, Push Members Down ani Extract Superclass nie ma - te ruchy robimy ręcznie, a listę miejsc do poprawy daje `npm run typecheck` (albo Find All References ⇧⌥F12).
- Motyw przewodni modułu: **najpierw kontrakt, potem diagram klas**. W każdej scenie zaczynamy od pytania "co jest obserwowalne?" (wyjątek, kolejność efektów, moment wyboru, format trwały).

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | Strategy - przed i po; intencja, sekwencja, ryzyka | 3 | `m6/s01_strategy` | ~12 min |
| s02 | Polimorfizm - przed i po; kiedy i pułapki | 3 | `m6/s02_polymorphism` | ~12 min |
| s03 | Replace Type Code with Class; granica trwałości | 3 | `m6/s03_typecode` | ~12 min |
| s04 | Factory - Encapsulate Classes with Factory | 3 | `m6/s04_encapsulatefactory` | ~10 min |
| s05 | Factory - Extract Factory Class | 3 | `m6/s05_extractfactory` | ~10 min |
| s06 | Encapsulate Composite with Builder | 3 | `m6/s06_builder` | ~12 min |
| s07 | Move Embellishment to Decorator; przezroczystość | 3 | `m6/s07_decorator` | ~12 min |
| s08 | Replace State-Altering Conditionals with State | 3 | `m6/s08_state` | ~15 min |
| s09 | Replace Hard-coded Notifications with Observer | 3 | `m6/s09_observer` | ~15 min |
| s10 | Replace Implicit Tree with Composite; mapper | 3 | `m6/s10_implicittree` | ~15 min |
| s11 | Safe vs Transparent Composite | 2 | `m6/s11_safecomposite` | ~8 min |
| s12 | Replace One/Many Distinctions with Composite | 3 | `m6/s12_onemany` | ~10 min |
| s13 | Extract Composite | 2 | `m6/s13_extractcomposite` | ~8 min |
| s14 | Unify Interfaces with Adapter | 3 | `m6/s14_adapter` | ~12 min |
| s15 | Replace Conditional Dispatcher with Command | 3 | `m6/s15_command` | ~12 min |
| s16 | Form Template Method | 2 | `m6/s16_templatemethod` | ~8 min |
| s17 | Limit Instantiation with Singleton | 3 | `m6/s17_singleton` | ~10 min |
| s18 | Move Accumulation to Collecting Parameter | 3 | `m6/s18_collectingparameter` | ~8 min |
| s19 | Visitor i macierz zmian | 3 | `m6/s19_visitor` | ~12 min |
| s20 | Mapa decyzji, "najpierw rodzaj zmienności" | 3 | `m6/s20_decisionmap` | ~12 min |

Katalogi są względne wobec `typescript/src/workshop/`. Liczby domeny (ceny formatów, zniżki, VIP, okulary, opłata online, punkty lojalnościowe, zwroty, VAT) są wspólne dla całego warsztatu. Wartości spoza wspólnej listy (program "tydzień studenta" 50%, ubezpieczenie biletu 4.00, "tani wtorek" -30%, weekend +2.00, ceny premier i maratonów) są przykładowe i opisane w komentarzach TSDoc sceny.

## Scena s01. Replace Conditional Logic with Strategy - polityka zniżek

**Temat ze slajdów:** Strategy - przed i po; Strategy - intencja, sekwencja, ryzyka; Istotne mechanizmy Javy 25
**Katalog:** `typescript/src/workshop/m6/s01_strategy` · **Test:** `scripts/warsztat.sh --lang ts test m6/s01`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `TicketPricer` wybiera zniżkę łańcuchem `if` po nazwie programu kina, wymieszanym z walidacją ceny. Wprowadzamy interfejs `DiscountPolicy` najpierw jako strategię przejściową, przenosimy każdy program do osobnej klasy, a wybór programu trafia do `DiscountPrograms` i konstruktora.

**Zasada:** Strategy zamyka wymienne warianty tego samego obliczenia za wspólnym interfejsem, gdy wariant wybiera się niezależnie od klasy obiektu. Wspólna walidacja zostaje w kontekście, a strategie są bezstanowe. Samo `if` nie uzasadnia wzorca - przy prostym, stabilnym warunku interfejs pogarsza czytelność.

**Efekt:** Nowy program zniżek to nowa strategia (nawet literał obiektu) i jeden wpis w `switch`, bez zmiany `TicketPricer`. Zachowanie świadomie się zmienia: wybór w konstruktorze zamraża decyzję, więc nieznany program zgłasza się teraz przed ujemną ceną.

**Różnica względem Javy:** `@FunctionalInterface DiscountPolicy` to w TypeScripcie zwykły interfejs z jedną metodą `discount`, więc strategię "w locie" zapisujemy literałem obiektu `{ discount: (b, type) => ... }`, a nie gołą lambdą. Program w `PriceRequest` jest typu `string | null` (w Javie `null` w rekordzie).

### Co widzimy

`TicketPricer.price(base, ticketType, program)` wybiera algorytm zniżki łańcuchem `if` po nazwie programu skonfigurowanego w kinie (STANDARD, STUDENT_WEEK, PREMIERE). W tej samej metodzie jest walidacja ceny i programu. Algorytm jest wybierany niezależnie od klasy obiektu - to klasyczny kandydat na Strategy.

```typescript
if (program === 'PREMIERE') {
  discount = Money.ZERO;
} else if (program === 'STUDENT_WEEK' && ticketType === 'S') {
  discount = base.percent(50);
} else if (program === 'STANDARD' || program === 'STUDENT_WEEK') {
  let percent: number;
  switch (ticketType) {
    case 'N': percent = 0; break;
    case 'S': percent = 25; break;
    ...
  }
  discount = base.percent(percent);
} else {
  throw new IllegalArgumentError(`unknown program: ${program}`);
}
```

Klientem jest `PriceBoard.priceFor(request)` - przez niego woła test. Test równoważności to **tabela decyzji**: każda gałąź, typ nieznany, program nieznany i `null`. Zwróć uwagę na przypadek "PREMIERE nie sprawdza typu" - start przyjmuje typ `X` bez błędu i refaktoryzacja musi to zachować.

### Krok 1: Extract Interface + strategia przejściowa

**W IDE:** utwórz plik `DiscountPolicy.ts` z interfejsem `DiscountPolicy` i metodą `discount(base: Money, ticketType: string): Money`. Zaznacz łańcuch `if`, ⌃⇧R > Extract to method in class 'TicketPricer', nazwa `legacyDiscount` (parametry `base`, `ticketType`, `program`, zwracane `Money`; metoda nie używa `this`, więc w porcie jest `private static`). W `price` utwórz strategię jako literał obiektu i wywołaj ją.
**Po:**

```typescript
const policy: DiscountPolicy = { discount: (b, type) => TicketPricer.legacyDiscount(b, type, program) };
return base.minus(policy.discount(base, ticketType));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s01` - zielone.
**Co powiedzieć:** pierwszy commit jest mały i odwracalny: kontrakt strategii istnieje, ale deleguje do starego kodu. Nie zaczynamy od tworzenia wszystkich klas wzorca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s01 0 1`

### Krok 2: przeniesienie gałęzi do strategii

**W IDE:** dla każdej gałęzi osobno: nowa klasa `implements DiscountPolicy` (⌘. na podkreślonej nazwie klasy - Implement interface 'DiscountPolicy'), ciało skopiowane z gałęzi, test. Kolejno `StandardDiscount`, `PremiereDiscount`, `StudentWeekDiscount` (ta ostatnia deleguje nie-studentów do `StandardDiscount` przekazanego w konstruktorze). Na koniec `legacyDiscount` zastąp metodą `policyFor(program)` ze `switch`.
**Po:**

```typescript
private static policyFor(program: string): DiscountPolicy {
  switch (program) {
    case 'STANDARD': return new StandardDiscount();
    case 'STUDENT_WEEK': return new StudentWeekDiscount(new StandardDiscount());
    case 'PREMIERE': return new PremiereDiscount();
    default: throw new IllegalArgumentError(`unknown program: ${program}`);
  }
}
```

**Uruchom:** test po każdej przeniesionej gałęzi.
**Co powiedzieć:** walidacja zostaje w kontekście, strategie są bezstanowe. `switch` nie zniknął - przeniósł się w jedno miejsce wyboru, a to jest w porządku.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s01 1 2`

### Krok 3: wybór strategii w korzeniu kompozycji

**W IDE:** utwórz klasę `DiscountPrograms` z prywatnym konstruktorem i przenieś do niej `policyFor` jako `static forName(program)` (VS Code nie przenosi metod między klasami - wytnij i wklej), instancje strategii jako statyczne stałe (współdzielone); sprawdzenie `null` programu przechodzi razem z wyborem. Change Signature robimy ręcznie: `TicketPricer` dostaje strategię w konstruktorze, `price(base, ticketType)` traci parametr programu, a `npm run typecheck` wskaże klienta `PriceBoard` do poprawy.
**Po:**

```typescript
return new TicketPricer(DiscountPrograms.forName(request.program))
  .price(request.base, request.ticketType);
```

**Uruchom:** test zielony; `S01SolutionTest` pokazuje nowy program zniżek jako literał obiektu (`{ discount: (base) => base.percent(50) }`) bez zmiany kontekstu.
**Co powiedzieć:** wybór w konstruktorze **zamraża decyzję** i przesuwa moment błędu: w start ujemna cena zgłaszała się przed nieznanym programem, teraz jest odwrotnie. To dokumentuje test `choosingInConstructorMovesTheUnknownProgramErrorEarlier` - taka zmiana wymaga świadomej zgody, nie jest "za darmo".
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3`: `TicketPricer` zna tylko `DiscountPolicy`, a nazwy programów zna `DiscountPrograms`. Nowy program zniżek to nowa strategia i wpis w jednym `switch`. Kontekst trzyma wspólną walidację.

### Pułapki

- Goły typ funkcyjny `(base: Money, ticketType: string) => Money` zamiast nazwanego interfejsu - działa, ale gubi nazwę pojęcia w sygnaturach i komunikatach kompilatora.
- Strategia z polami zmienianymi podczas liczenia, a potem współdzielona - w JavaScripcie nie przez wątki, ale przez wywołania przeplatane `await` albo wielokrotne użycie tej samej instancji.
- Porównywanie strategii przez `===` - każdy literał obiektu i każda funkcja strzałkowa to nowa tożsamość.
- Strategia jako zamknięta unia typów zamiast interfejsu - punkt rozszerzeń ma być otwarty, a interfejs przyjmuje także literał obiektu z testu.

### Pytanie do sali

Czy przy dwóch programach i jednym `if` Strategy nadal byłaby uzasadniona? Co musiałoby się zmieniać, żeby tak?

## Scena s02. Replace Conditional with Polymorphism - rodzaj seansu

**Temat ze slajdów:** Polimorfizm - przed i po; Polimorfizm - kiedy i pułapki
**Katalog:** `typescript/src/workshop/m6/s02_polymorphism` · **Test:** `scripts/warsztat.sh --lang ts test m6/s02`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Screening` ma pole `kind` i ten sam `switch` w trzech metodach, a pole `value` znaczy raz minuty filmu, raz liczbę filmów. Wydzielamy podtyp dla każdego rodzaju seansu po kolei, aż zostaje zamknięta unia typów z nazwanymi danymi.

**Zasada:** Replace Conditional with Polymorphism przenosi zachowanie zależne od rodzaju do podtypów, gdy selektor opisuje trwały rodzaj obiektu. Jeśli rodzaj zmienia się w czasie życia obiektu, to State, a jeśli algorytm wybiera klient - Strategy. Zaczynamy od znalezienia wszystkich miejsc tworzenia i deserializacji.

**Efekt:** Switche w zachowaniu znikają, zostaje jeden w `fromRow`, gdzie należy wiedza o konstrukcji, a dane mają jednoznaczne nazwy. Kosztem zamkniętej unii jest to, że nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.

**Różnica względem Javy:** TypeScript nie ma zamkniętych hierarchii klas (`sealed ... permits`), więc w krokach 1-2 baza jest zwykłą (potem abstrakcyjną) klasą, a zamknięcie daje dopiero krok 3: unia dyskryminowana `type Screening = RegularScreening | PremiereScreening | MarathonScreening` z polem `kind`. W krokach 1-2 podklasy są zdefiniowane w `Screening.ts`, a pliki `MarathonScreening.ts` itd. tylko je re-eksportują - w ESM cykl importów `Screening -> podklasa -> Screening` (przez `extends`) kończy się `ReferenceError`. Chronione akcesory `title()` z Javy to pole `protected readonly title`.

### Co widzimy

`Screening` ma pole `kind` (enum `Kind`: REGULAR, PREMIERE, MARATHON) i ten sam `switch` w trzech metodach: `label()`, `durationMinutes()`, `price()`. Pole `value` znaczy raz "minuty filmu", raz "liczba filmów". Rodzaj seansu jest **trwałą cechą obiektu** - nie zmienia się w czasie życia, więc to nie State, a klient go nie wybiera, więc to nie Strategy.

```typescript
durationMinutes(): number {
  switch (this.kind) {
    case Kind.REGULAR: return 20 + this.value;
    case Kind.PREMIERE: return 30 + this.value;
    case Kind.MARATHON: return this.value * 120 + (this.value - 1) * 15;
  }
}
```

Tworzenie jest w jednym miejscu: `Screening.fromRow(row: ScreeningRow)` (mapowanie wiersza z bazy). Zawsze najpierw szukamy miejsc tworzenia i deserializacji (w VS Code: Find All References ⇧⌥F12 na konstruktorze).

### Krok 1: Extract Subclass dla jednej gałęzi

**W IDE:** konstruktor bazy z `private` na `protected`, `title` jako `protected readonly`. Pod klasą `Screening` (w tym samym pliku) utwórz `MarathonScreening extends Screening` z polem `films`, nadpisz trzy metody z modyfikatorem `override` (⌘. na klasie - Add missing members pomaga tylko przy metodach abstrakcyjnych, tu wpisujemy je ręcznie) i skopiuj do nich gałęzie MARATHON. Plik `MarathonScreening.ts` tylko re-eksportuje klasę. W `fromRow` twórz podklasę dla `'MARATHON'`. W bazie gałęzie MARATHON rzucają `IllegalStateError`.
**Po:**

```typescript
case 'MARATHON': return new MarathonScreening(row.title, row.value);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s02` - zielone.
**Co powiedzieć:** jedna gałąź naraz, jeden test kontraktowy dla wszystkich rodzajów. Martwa gałąź rzuca wyjątek zamiast po cichu liczyć bzdury.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s02 0 1`

### Krok 2: pozostałe podklasy, baza abstrakcyjna

**W IDE:** powtórz ruch dla REGULAR i PREMIERE. Potem klasa bazowa i jej metody jako `abstract`, usuń pole `kind` i enum `Kind` (ręcznie - `npm run typecheck` pokaże pozostałe użycia). Jedynym `switch` zostaje `fromRow`.
**Po:**

```typescript
export abstract class Screening {
  protected constructor(protected readonly title: string) {}
  ...
  abstract durationMinutes(): number;
```

**Uruchom:** test zielony.
**Co powiedzieć:** `switch` w miejscu tworzenia jest właściwy - to jest wiedza o konstrukcji. Znikają za to switche w zachowaniu.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s02 1 2`

### Krok 3: forma TypeScript - unia dyskryminowana

**W IDE:** zamień klasę bazową na unię `type Screening = RegularScreening | PremiereScreening | MarathonScreening` i obiekt `Screening` o tej samej nazwie z funkcją `fromRow` (typ i wartość mogą dzielić nazwę, więc klienci nie zmieniają importów). Każdy rodzaj dostaje literał `readonly kind = 'MARATHON'` i nazwane dane (`runtime`, `films`), a wspólne metody opisuje interfejs `ScreeningMembers`. `value` znika. Podtypy nie dziedziczą już po bazie, więc mogą wrócić do własnych plików.
**Po:**

```typescript
export class MarathonScreening implements ScreeningMembers {
  readonly kind = 'MARATHON';

  constructor(readonly title: string, readonly films: number) {}

  durationMinutes(): number {
    return this.films * 120 + (this.films - 1) * 15;
  }
```

**Uruchom:** test zielony; `S02SolutionTest` zawiera `switch (screening.kind)` klienta zakończony `default: return assertNever(screening)` - dodanie czwartego rodzaju do unii psuje kompilację (`npm run typecheck`).
**Co powiedzieć:** zamknięty zestaw rodzajów daje kontrolę kompilatora, ale też koszt: nowy rodzaj psuje kompilację każdego wyczerpującego `switch`, także poza modułem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s02 2 3`

### Rozwiązanie i uzasadnienie

Trzy niezmienne klasy z własnymi danymi i zachowaniem, połączone w unię dyskryminowaną. Znaczenie pól jest jednoznaczne, a test kontraktowy przechodzi przez wszystkie rodzaje, łącznie z nieznanym rodzajem w danych.

### Pułapki

- Pominięcie miejsc tworzenia (ORM, `JSON.parse` z ręcznym mapowaniem, fabryka w innym module) - obiekt powstanie jako zły podtyp albo zwykły obiekt bez metod.
- Wywołanie metody nadpisywanej z konstruktora bazy - podklasa widzi niezainicjalizowane pola (w TypeScripcie pola z parametrów konstruktora podklasy są przypisywane dopiero po `super(...)`).
- Pola i metody `static` nie są polimorficzne względem instancji - "nadpisanie" stałej w podklasie nie zmienia tego, co widzi kod bazy wołający `Screening.STALA`.

### Pytanie do sali

Seans może zmienić się z "premiery" w "zwykły" po tygodniu. Czy to nadal polimorfizm, czy już State?

## Scena s03. Replace Type Code with Class - format jako typ

**Temat ze slajdów:** Replace Type Code with Class; Type Code - decyzje i granica trwałości
**Katalog:** `typescript/src/workshop/m6/s03_typecode` · **Test:** `scripts/warsztat.sh --lang ts test m6/s03`
**Czas:** ~12 min

### W skrócie

**Co robimy:** Format seansu jest surowym `number` z CSV, a wiedza o kodach jest rozsiana po trzech metodach. Zamieniamy liczbę na typ `Format` tuż po odczycie, przenosimy do niego etykietę, cenę i okulary, a tłumaczenie kodu trwałego zamykamy w mapperze `FormatCodes`.

**Zasada:** Replace Type Code with Class zastępuje prymitywny kod typem, który przejmuje walidację, normalizację i operacje pojęcia - co nie oznacza automatycznie hierarchii podklas. Zamknięty zestaw stałych instancji wystarcza dla małego, zamkniętego zestawu, pełniejsza klasa z fabryką jest lepsza przy aliasach i kodach zewnętrznych. Na granicy trwałości zostaje stabilny kod, nigdy pozycja na liście wartości ani `toString()`.

**Efekt:** Poza mapperem żaden kod nie operuje na liczbach 1, 2, 3, a zapis CSV jest bez zmian. Zmiana formatu trwałego (np. na kody tekstowe) dotknie tylko mappera, ale to osobna decyzja, nie część tej refaktoryzacji.

**Różnica względem Javy:** `enum` w TypeScripcie nie ma pól ani metod, więc javowy enum z polami (`Format`) to klasa z prywatnym konstruktorem, statycznymi instancjami `TWO_D`, `THREE_D`, `IMAX`, metodą `values()` i polem `name` typu unii literałów. W kroku 1 `switch (format.name)` jest wyczerpujący bez `default` (odpowiednik switcha po enumie), a akcesory `label()`, `basePrice()`, `requiresGlasses()` to pola `readonly`. `Integer.parseInt` zastępuje pomocnik sceny `parseInteger` (`Integers.ts`), który rzuca `IllegalArgumentError` z komunikatem Javy (`For input string: "x"`) - `Number.parseInt` zwróciłby `NaN` albo po cichu uciął `"3x"` do 3. Odpowiednikiem `ordinal()` jest `Format.values().indexOf(format)`, a `EnumMap` - `ReadonlyMap<Format, number>`.

### Co widzimy

Format seansu jest surowym `number` (1=2D, 2=3D, 3=IMAX) czytanym z CSV `Diuna;3`. Wiedza o kodzie jest w trzech metodach, a `needsGlasses` w ogóle nie waliduje kodu. Plik CSV musi nadal przechowywać liczbę - to jest **granica trwałości**.

```typescript
private basePrice(formatCode: number): Money {
  switch (formatCode) {
    case ScreeningCsv.FORMAT_2D: return Money.of('25.00');
    ...
    default: throw new IllegalArgumentError(`unknown format code: ${formatCode}`);
  }
}
```

### Krok 1: nowy typ i konwersja na granicy

**W IDE:** utwórz klasę `Format` ze stałymi instancjami `TWO_D` (1), `THREE_D` (2), `IMAX` (3), polem `code`, `values()` i `fromCode(code)` rzucającym ten sam wyjątek co start. W `describe` zamień `number` na `Format` zaraz po odczycie; typy parametrów metod prywatnych zmień ręcznie na `Format` (VS Code nie ma Change Signature - kompilator wskaże resztę). Switche przejdź na `format.name`. Do CSV zapisuj `format.code`.
**Po:**

```typescript
const format = Format.fromCode(parseInteger(parts[1]!.trim()));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s03` - zielone, łącznie z "nieznany kod" i "kod nie jest liczbą".
**Co powiedzieć:** zamknięty zestaw instancji wystarcza, bo formatów jest mało. Trwały kod to jawne pole, nigdy pozycja w `values()` ani `name`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s03 0 1`

### Krok 2: Move Method do typu

**W IDE:** przenieś `label`, `basePrice`, `needsGlasses` do typu jako pola stałych instancji (ręcznie - wartości z gałęzi `switch` trafiają do wywołań konstruktora). Klient pyta obiekt zamiast wykonywać `switch`.
**Po:**

```typescript
static readonly TWO_D = new Format('TWO_D', 1, '2D', '25.00', false);
static readonly THREE_D = new Format('THREE_D', 2, '3D', '32.00', true);
static readonly IMAX = new Format('IMAX', 3, 'IMAX', '40.00', false);
```

**Uruchom:** test zielony.
**Co powiedzieć:** nowy typ przejmuje walidację, normalizację i operacje pojęcia. Nie oznacza to automatycznie hierarchii klas.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s03 1 2`

### Krok 3: mapper migracyjny na granicy trwałości

**W IDE:** nowa klasa `FormatCodes` z `fromCode`/`toCode` (mapa `ReadonlyMap<Format, number>`), usuń `code` i `fromCode` z `Format`. `ScreeningCsv` rozmawia z mapperem, reszta kodu wyłącznie z `Format`.
**Po:**

```typescript
const format = FormatCodes.fromCode(parseInteger(parts[1]!.trim()));
... + `|csv=${title};${FormatCodes.toCode(format)}`;
```

**Uruchom:** test zielony; `S03SolutionTest` sprawdza, że pozycja IMAX w `Format.values()` to 2, a kod trwały to 3.
**Co powiedzieć:** gdy kiedyś przejdziemy na kody tekstowe ("IMAX" w bazie), zmieni się tylko mapper - może przez pewien czas czytać oba formaty. Zmiana formatu trwałego to osobna decyzja, nie część tej refaktoryzacji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s03 2 3`

### Rozwiązanie i uzasadnienie

Typ domenowy bez wiedzy o bazie plus mapper na granicy. Zamknięty zestaw stałych instancji, bo formatów jest mało. Pełną klasę z fabryką i walidacją wartości (jak `DeploymentZone` ze slajdów) wybralibyśmy przy aliasach, kodach zewnętrznych albo gdy lista wartości pochodzi z konfiguracji.

### Pułapki

- Zapis pozycji w `values()` (albo wartości numerycznego `enum` TypeScriptu) do bazy - dodanie stałej w środku przestawia dane.
- Zapis `toString()` - ktoś go "upiększy" i CSV przestanie się wczytywać.
- Zmiana komunikatu wyjątku przy nieznanym kodzie - to też obserwowalne zachowanie (logi, monitoring).

### Pytanie do sali

Kiedy wybralibyście pełną klasę z fabryką zamiast zamkniętego zestawu stałych dla formatu? Co w domenie kina mogłoby to wymusić?

## Scena s04. Encapsulate Classes with Factory - bilety

**Temat ze slajdów:** Factory - publiczna granica tworzenia; Factory - intencja, procedura, pułapki
**Katalog:** `typescript/src/workshop/m6/s04_encapsulatefactory` · **Test:** `scripts/warsztat.sh --lang ts test m6/s04`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `BoxOffice` tworzy `StandardTicket` i `VipTicket` przez `new` i sam zna regułę VIP (rząd 10+). Przekierowujemy tworzenie do metod fabryki `Tickets`, przenosimy tam regułę wyboru biletu, a na końcu odbieramy klasom konkretnym `export`.

**Zasada:** Encapsulate Classes with Factory ukrywa klasy konkretne za publiczną granicą tworzenia, gdy klient potrzebuje tylko wspólnego interfejsu. Tworzenie przekierowujemy pojedynczo, a widoczność ograniczamy dopiero na końcu. Nie mylić z Extract Factory Class, która wydziela tworzenie z klasy o innej odpowiedzialności.

**Efekt:** Publiczne zostają tylko `Ticket` i `Tickets`, więc nowy rodzaj biletu nie zmienia żadnego klienta. Ryzykiem zostają miejsca tworzenia, których kompilator nie widzi (dynamiczne `import()`, kontener DI, deserializacja), a w bibliotece publicznej potrzebny byłby etap `@deprecated`.

**Różnica względem Javy:** TypeScript nie ma widoczności pakietowej. Jedyną granicą jest moduł (plik), więc w kroku 3 `StandardTicket` i `VipTicket` przenosimy do `tickets/Tickets.ts` jako klasy bez `export` - prywatne dla modułu - a ich osobne pliki znikają. `S04SolutionTest` zamiast refleksji (`Modifier.isPublic`) sprawdza, że plików `StandardTicket.ts`/`VipTicket.ts` w `step3` nie ma, moduł fabryki eksportuje tylko `Tickets`, klasy są zadeklarowane bez `export`, a `Ticket.ts` eksportuje interfejs.

### Co widzimy

`BoxOffice` (inny katalog niż bilety) tworzy `StandardTicket` i `VipTicket` przez `new` w dwóch miejscach i zna regułę VIP (rząd 10+). Klasy biletów są eksportowane, choć klient potrzebuje tylko interfejsu `Ticket`.

```typescript
tickets.push(row >= 10
  ? new VipTicket(title, base, row)
  : new StandardTicket(title, base, row));
```

### Krok 1: metody tworzące

**W IDE:** w katalogu `tickets` utwórz klasę `Tickets` z prywatnym konstruktorem i statycznymi `standard(...)` i `vip(...)` zwracającymi `Ticket`. Zastąp każde `new` w `BoxOffice` wywołaniem metody tworzącej (VS Code nie ma Replace Constructor with Factory Method - robimy to ręcznie, Find All References ⇧⌥F12 na konstruktorze daje listę miejsc).
**Po:**

```typescript
if (row >= 10) {
  return Tickets.vip(title, base, row);
}
return Tickets.standard(title, base, row);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s04` - zielone.
**Co powiedzieć:** przekierowujemy tworzenie pojedynczo; klient nie importuje już klas konkretnych.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s04 0 1`

### Krok 2: decyzja do fabryki

**W IDE:** warunek `row >= 10` przenieś do `Tickets.forSeat(title, base, row)` (Move Method ręcznie), liczbę 10 zaznacz i ⌃⇧R > Extract to readonly field, nazwa `VIP_FROM_ROW` (w porcie `private static readonly`). `sellAll` woła `sell`.
**Po:**

```typescript
sell(title: string, base: Money, row: number): Ticket {
  return Tickets.forSeat(title, base, row);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** reguła "który bilet" jest teraz w jednym miejscu - to właściwy dom dla `if` o konstrukcji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s04 1 2`

### Krok 3: ograniczenie widoczności

**W IDE:** przenieś `StandardTicket` i `VipTicket` do `tickets/Tickets.ts` (⌃⇧R > Move to file na deklaracji klasy), usuń słowo `export` i skasuj puste pliki. `npm run typecheck` podkreśli każde użycie spoza modułu - to inwentaryzacja, którą robi za nas kompilator.
**Po:**

```typescript
/** Krok 3: klasa prywatna modułu, miejsce VIP: +10.00 (w Javie: rekord pakietowy). */
class VipTicket implements Ticket {
  constructor(
    readonly title: string,
    readonly base: Money,
    readonly row: number,
  ) {}
```

**Uruchom:** test zielony; `S04SolutionTest` sprawdza, że poza modułem fabryki klas konkretnych nie widać.
**Co powiedzieć:** widoczność ograniczamy na końcu, gdy wszyscy klienci już przeszli. W bibliotece publicznej potrzebny byłby etap `@deprecated` (TSDoc), zanim eksport zniknie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s04 2 3`

### Rozwiązanie i uzasadnienie

Publiczne są tylko `Ticket` i `Tickets`. Nowy rodzaj biletu (np. miejsce dla osoby na wózku) nie zmienia żadnego klienta.

### Pułapki

- Zapomniane miejsca tworzenia: dynamiczne `import()`, kontener DI, deserializacja JSON wymagająca dostępnej klasy.
- Fabryka rosnąca w globalny rejestr wszystkiego ("ServiceLocator").
- Fabryka tworząca warianty z wyprzedzeniem - kosztowne efekty uruchamiane wcześniej niż w starej gałęzi.

### Pytanie do sali

Co się zmienia, jeśli klasy biletów są używane przez framework ORM?

## Scena s05. Extract Factory Class - tworzenie rezerwacji

**Temat ze slajdów:** Factory - intencja, procedura, pułapki (Extract Factory Class)
**Katalog:** `typescript/src/workshop/m6/s05_extractfactory` · **Test:** `scripts/warsztat.sh --lang ts test m6/s05`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwacje (numer, opłata online, termin ważności), i to w dwóch kopiach. Wyciągamy tworzenie do metody, potem do klasy `ReservationFactory`, a na końcu przekazujemy fabrykę do serwisu przez konstruktor.

**Zasada:** Extract Factory Class wydziela wiedzę o tworzeniu obiektów z klasy, która ma inną główną odpowiedzialność. Fabryka to zwykła zależność wstrzykiwana konstruktorem, a nie globalny rejestr ani zestaw metod statycznych. Kolejność efektów przy tworzeniu (tu: numer pobierany przed walidacją kanału) jest częścią kontraktu.

**Efekt:** Fabryka wie, jak powstaje rezerwacja, serwis wie, kiedy wolno ją utworzyć, i każdą da się testować osobno. "Spalanie" numeru przy błędnym kanale zostaje celowo - jego naprawa byłaby zmianą zachowania.

**Różnica względem Javy:** TypeScript nie ma przeciążonych konstruktorów, więc dwa konstruktory Javy `ReservationService(Clock)` i `ReservationService(ReservationFactory)` to w kroku 3 jeden konstruktor przyjmujący `ReservationFactory | Clock` (rozróżnienie przez `instanceof ReservationFactory`). W oczekiwaniach testu równoważności nazwy wyjątków to nazwy klas TypeScriptu (`IllegalStateError: seat taken: A1`, `IllegalArgumentError: ...`); treści komunikatów i kolejność są jak w Javie.

### Co widzimy

`ReservationService` pilnuje zajętości miejsc, ale też buduje rezerwację: numer z licznika, opłata online 2.00 za bilet, termin ważności 15 minut. Ta wiedza jest skopiowana w `reserve` i `reserveGroup`. Subtelność: numer jest pobierany **przed** walidacją kanału - błędny kanał "spala" numer.

```typescript
this.sequence++;
const id = `R${this.sequence}`;
if (channel !== 'ONLINE' && channel !== 'BOX_OFFICE') {
  throw new IllegalArgumentError(`unknown channel: ${channel}`);
}
```

Test to scenariusz kilku rezerwacji na jednym serwisie: zajęte miejsce nie zużywa numeru, nieznany kanał zużywa.

### Krok 1: Extract Method dla tworzenia

**W IDE:** w `reserve` zaznacz blok od `this.sequence++` do `new Reservation(...)`, ⌃⇧R > Extract to method in class 'ReservationService', nazwa `newReservation`. W `reserveGroup` zastąp duplikat wywołaniem `this.newReservation('ONLINE', email, seats)`. Pętlę zajętości wyciągnij tak samo do `requireFree`.
**Po:**

```typescript
this.requireFree(seats);
const reservation = this.newReservation(channel, email, seats);
seats.forEach((seat) => this.takenSeats.add(seat));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s05` - zielone.
**Co powiedzieć:** kolejność efektów zostaje: najpierw sprawdzenie miejsc, potem numer, potem walidacja kanału. Gdybyśmy przy okazji "poprawili" kolejność, test to wychwyci.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s05 0 1`

### Krok 2: Extract Class - ReservationFactory

**W IDE:** nowa klasa `ReservationFactory` (VS Code nie ma Extract Delegate - przenosimy ręcznie): metoda `newReservation` jako `create(channel, email, seats)` razem z polami `clock` i `sequence`. Serwis tworzy fabrykę w swoim konstruktorze - sygnatura konstruktora bez zmian.
**Po:**

```typescript
constructor(clock: Clock) {
  this.factory = new ReservationFactory(clock);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** klienci serwisu niczego nie zauważyli. Serwis ma jedną odpowiedzialność: dostępność miejsc.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s05 1 2`

### Krok 3: fabryka jako zależność

**W IDE:** konstruktor przyjmuje teraz `ReservationFactory | Clock`: gdy dostaje fabrykę, używa jej, gdy zegar - tworzy fabrykę jak dotąd, żeby nie ruszać klientów.
**Po:**

```typescript
constructor(factoryOrClock: ReservationFactory | Clock) {
  requireNonNull(factoryOrClock, 'factory');
  this.factory = factoryOrClock instanceof ReservationFactory
    ? factoryOrClock
    : new ReservationFactory(factoryOrClock);
}
```

**Uruchom:** test zielony; `S05SolutionTest` testuje fabrykę osobno i pokazuje współdzieloną numerację.
**Co powiedzieć:** fabryka to zwykła zależność wstrzykiwana konstruktorem, nie globalny rejestr. Dwie usługi z jedną fabryką dzielą numerację - to decyzja korzenia kompozycji.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s05 2 3`

### Rozwiązanie i uzasadnienie

`ReservationFactory` wie, jak powstaje rezerwacja (numer, opłata, termin), `ReservationService` wie, kiedy wolno ją utworzyć. Każdą da się testować osobno.

### Pułapki

- "Naprawienie" spalania numerów w ramach refaktoryzacji - to zmiana zachowania (luki w numeracji mogą być wymagane przez księgowość albo przez nią zabronione).
- Czas pobierany przez `new Date()` albo `Date.now()` zamiast wstrzykniętego `Clock` - fabryka staje się nietestowalna.
- Fabryka ze statycznymi metodami i statycznym licznikiem (albo licznikiem na poziomie modułu) - wraca globalny stan.

### Pytanie do sali

Czy numeracja rezerwacji powinna należeć do fabryki, czy do repozytorium? Co przemawia za każdą opcją?

## Scena s06. Encapsulate Composite with Builder - repertuar dnia

**Temat ze slajdów:** Encapsulate Composite with Builder
**Katalog:** `typescript/src/workshop/m6/s06_builder` · **Test:** `scripts/warsztat.sh --lang ts test m6/s06`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `WeekendPlanner` składa drzewo dzień - sala - seans ręcznie z `new` i `add`, a węzły są mutowalne. Wprowadzamy `ScheduleBuilder`: najpierw z bieżącą salą, potem z niemutowalnymi węzłami i jednorazowym `build()`, na końcu z zagnieżdżoną funkcją strzałkową dla każdej sali.

**Zasada:** Encapsulate Composite with Builder ukrywa budowę drzewa za API, które mówi "co" zbudować, a nie "jak". Jest uzasadniony, gdy budowa jest wieloetapowa i ma własne niezmienniki (kolejność, pusta grupa, unikalność nazw). Builder jest jednorazowy, a funkcja gałęzi wykonuje się synchronicznie dokładnie raz.

**Efekt:** Wcięcia kodu planera odpowiadają poziomom repertuaru, a gotowego drzewa nie da się zmodyfikować. Unikalność nazw sal to świadomie dodany nowy niezmiennik - start pozwalał na duplikaty, więc dla błędnych danych zachowanie się zmienia.

**Różnica względem Javy:** rekordy z `List.copyOf` to klasy z polami `readonly` i tablicą zamrożoną `Object.freeze([...])`, więc test niemutowalności oczekuje `TypeError` przy `push` (w Javie `UnsupportedOperationException`). Zagnieżdżona klasa `ScheduleBuilder.HallBuilder` to osobna eksportowana klasa `HallBuilder` w tym samym pliku, której konstruktor dostaje listę seansów należącą do `ScheduleBuilder`, a `Consumer<HallBuilder>` to typ `(hall: HallBuilder) => void`. `LinkedHashMap` zastępuje `Map`, który też zachowuje kolejność wstawiania.

### Co widzimy

`WeekendPlanner.plan(date)` buduje drzewo dzień - sala - seans ręcznie: dużo `new` i `add`, a o `day.add(hall2)` łatwo zapomnieć. Kod nie przypomina kształtu repertuaru, a węzły są mutowalne.

```typescript
const hall2 = new Hall('Sala 2');
if (weekend) {
  hall2.add(new Screening('Kraina Lodu', LocalTime.of(10, 0)));
}
hall2.add(new Screening('Amator', LocalTime.of(17, 30)));
day.add(hall2);
```

### Krok 1: klasyczny Builder z bieżącym węzłem

**W IDE:** utwórz `ScheduleBuilder` z metodami `hall(name)`, `screening(title, hour, minute)` i `build()` zwracającymi `this` (poza `build`); builder pamięta bieżącą salę. Przepisz planera na wywołania buildera. Drzewo bez zmian.
**Po:**

```typescript
const builder = new ScheduleBuilder(date)
  .hall('Sala 1')
  .screening('Diuna', 18, 0)
  .screening('Diuna', 21, 0)
  .hall('Sala 2');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s06` - zielone (sobota i piątek z pustą salą VIP).
**Co powiedzieć:** klient mówi "co" zbudować. Seans przed salą to teraz jawny błąd (`screening without hall`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s06 0 1`

### Krok 2: niemutowalne drzewo, builder jednorazowy

**W IDE:** węzły zamień na klasy z polami `readonly` i tablicą kopiowaną w konstruktorze przez `Object.freeze([...])`, usuń `add`. Builder zbiera dane (`Map<string, Screening[]>`) i tworzy węzły w `build()`; drugi `build()` rzuca `IllegalStateError`.
**Po:**

```typescript
export class Hall {
  readonly screenings: readonly Screening[];

  constructor(readonly name: string, screenings: readonly Screening[]) {
    this.screenings = Object.freeze([...screenings]);
  }
```

**Uruchom:** test zielony; `S06SolutionTest` sprawdza jednorazowość i niemutowalność.
**Co powiedzieć:** gdy builder jest jedyną drogą budowy, drzewo może stać się niemutowalne. `readonly` w typie chroni tylko w czasie kompilacji, dopiero `Object.freeze` zatrzymuje `push` w runtime. Unikalność nazw sal to **nowy niezmiennik** - start pozwalał na duplikaty. Taką regułę zatwierdzamy świadomie, bo zmienia zachowanie dla błędnych danych.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s06 1 2`

### Krok 3: zagnieżdżony builder z funkcją strzałkową

**W IDE:** `hall(name, content: (hall: HallBuilder) => void)` zamiast "bieżącej sali"; `HallBuilder` z `screening(...)`, a statyczne `ScheduleBuilder.day(date)` zamiast publicznego konstruktora. Przepisz planera.
**Po:**

```typescript
return ScheduleBuilder.day(date)
  .hall('Sala 1', (hall) => hall
    .screening('Diuna', 18, 0)
    .screening('Diuna', 21, 0))
  .hall('Sala 2', (hall) => { ... })
  .build();
```

**Uruchom:** test zielony.
**Co powiedzieć:** wcięcia kodu odpowiadają poziomom drzewa, a ukryty stan "bieżąca sala" zniknął. Kontrakt funkcji: synchroniczna (nie `async`), wywołana dokładnie raz.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s06 2 3`

### Rozwiązanie i uzasadnienie

Builder jest uzasadniony, bo budowa drzewa jest wieloetapowa i ma niezmienniki (kolejność sal, pusta sala dozwolona, unikalne nazwy). Wynik jest niemutowalny.

### Pułapki

- Builder wielokrotnego użytku, którego `build()` zwraca wewnętrzną tablicę - kolejne wywołania zmieniają już zwrócone drzewo.
- Funkcja zapamiętana i wywołana później (odroczenie) albo `async` - zmienia kolejność i wynik.
- Współdzielone poddrzewo (DAG) - liczy się tyle razy, ile ścieżek do niego prowadzi.

### Pytanie do sali

Czy pusta sala w repertuarze to poprawny stan, czy błąd? Kto powinien o tym zdecydować - builder czy model?

## Scena s07. Move Embellishment to Decorator - dodatki do biletu

**Temat ze slajdów:** Move Embellishment to Decorator; Decorator - wyjątki, migracja, przezroczystość
**Katalog:** `typescript/src/workshop/m6/s07_decorator` · **Test:** `scripts/warsztat.sh --lang ts test m6/s07`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Wydzielamy interfejs `PricedTicket` i przenosimy każdy dodatek do dekoratora, zaczynając od ubezpieczenia, a łańcuch składamy w `TicketAssembler`.

**Zasada:** Move Embellishment to Decorator przenosi opcjonalny dodatek wokół rdzenia do obiektu, który ma ten sam kontrakt i deleguje do środka. Kolejność owijania jest zachowaniem, więc składa się ją w jednym miejscu. Dekorator nie jest przezroczysty dla `instanceof`, `constructor`, tożsamości i równości strukturalnej.

**Efekt:** Rdzeń biletu nie ma flag, a nowy dodatek to nowa klasa i linia w assemblerze. Kosztem jest utrata pytania o typ: kto chce wiedzieć, czy bilet jest VIP, potrzebuje osobnego API zamiast `instanceof VipSeat`.

**Różnica względem Javy:** dekoratory i rdzeń (w Javie rekordy) to klasy z polami `readonly`, bez `equals`. Asercje `assertNotEquals` z Javy (rekord a jego dekorator, dwa łańcuchy w innej kolejności) to w `S07SolutionTest` `not.toStrictEqual` - równość strukturalna z typem, najbliższa `equals` rekordów; ceny porównujemy przez `Money.equals`.

### Co widzimy

`Ticket` ma flagi `vip`, `glasses`, `insurance` i te same `if` w `price()` i `description()`. Większość biletów nie ma żadnego dodatku, a każdy nowy dodatek to kolejne pole. Opis ma ustaloną kolejność: VIP, okulary, ubezpieczenie.

```typescript
return `${this.title} ${this.format}`
  + (this.vip ? ' +VIP' : '')
  + (this.glasses ? ' +okulary 3D' : '')
  + (this.insurance ? ' +ubezpieczenie' : '');
```

### Krok 1: Extract Interface

**W IDE:** VS Code nie ma Extract Interface dla klasy - utwórz `PricedTicket.ts` z interfejsem `PricedTicket` (`price()` i `description()`) ręcznie, dopisz `implements PricedTicket` do `Ticket`. `TicketAssembler.assemble` zwraca interfejs.
**Po:**

```typescript
assemble(order: TicketOrder): PricedTicket {
  return new Ticket(order);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s07` - zielone.
**Co powiedzieć:** wąski kontrakt, który spełnią i rdzeń, i dekoratory. Składanie w jednym miejscu (assembler).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s07 0 1`

### Krok 2: pierwszy dodatek jako dekorator

**W IDE:** klasa `Insurance implements PricedTicket` z polem `readonly inner: PricedTicket`, dodająca 4.00 i " +ubezpieczenie". Usuń flagę z `Ticket` (ręcznie, `npm run typecheck` pokaże resztę), w assemblerze owiń, gdy `order.insurance`.
**Po:**

```typescript
let ticket: PricedTicket = new Ticket(order);
if (order.insurance) {
  ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** zaczynamy od ubezpieczenia, bo w opisie jest **ostatnie** - dekorator dopisuje się na końcu. Gdybyśmy zaczęli od VIP, opis zmieniłby kolejność i test "wszystkie dodatki" by to złapał. Kolejność dekoratorów jest kontraktem.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s07 1 2`

### Krok 3: pozostałe dodatki, czysty rdzeń

**W IDE:** `VipSeat` i `Glasses3D` jako dekoratory, rdzeń jako niezmienna klasa `Ticket(title, format, base)`. Decyzja "czy okulary potrzebne" (3D i brak własnych) zostaje w assemblerze.
**Po:**

```typescript
let ticket: PricedTicket = new Ticket(order.title, order.format, order.base);
if (order.vip) {
  ticket = new VipSeat(ticket);
}
if (order.format === '3D' && !order.ownGlasses) {
  ticket = new Glasses3D(ticket);
}
if (order.insurance) {
  ticket = new Insurance(ticket);
}
```

**Uruchom:** test zielony; `S07SolutionTest` dokumentuje granice przezroczystości.
**Co powiedzieć:** `ticket instanceof VipSeat` zwraca `false`, gdy VIP jest owinięty ubezpieczeniem. Udekorowany bilet nie jest też strukturalnie równy rdzeniowi (`not.toStrictEqual`). Kto pyta o typ albo tożsamość, potrzebuje innego API (np. `features()`).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s07 2 3`

### Rozwiązanie i uzasadnienie

Rdzeń bez flag, trzy dekoratory z tym samym kontraktem, łańcuch składany w jednej fabryce w ustalonej kolejności.

### Pułapki

- Własna metoda `equals` w dekoratorze ślepo delegująca do `inner` - łamie symetrię (`core.equals(vip)` różne od `vip.equals(core)`).
- Kod sprawdzający `instanceof`, `constructor.name`, używający obiektu jako klucza `Map`/`WeakMap` albo serializujący go `JSON.stringify` - dekorator nie jest przezroczysty.
- Dekorator z efektem ubocznym (audyt), którego błąd zasłania błąd rdzenia.

### Pytanie do sali

Kasa chce wydrukować "Miejsce VIP" na bilecie. Jak to zrobić, nie używając `instanceof VipSeat`?

## Scena s08. Replace State-Altering Conditionals with State - status rezerwacji

**Temat ze slajdów:** Replace State-Altering Conditionals with State; State - kontekst delegujący
**Katalog:** `typescript/src/workshop/m6/s08_state` · **Test:** `scripts/warsztat.sh --lang ts test m6/s08`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Reservation` w każdej operacji sprawdza i zmienia pole statusu, więc reguły przejść są rozsiane po metodach. Zaczynamy od tabeli przejść w teście, zastępujemy pole obiektem stanu i przenosimy `pay`, `use`, `expire`, `cancel` do stanów po jednej akcji.

**Zasada:** State pasuje, gdy zachowanie operacji zależy od bieżącego stanu obiektu i zmienia się razem z przejściami. Kontekst deleguje do obiektu stanu, a niedozwolone przejście domyślnie rzuca wyjątek - pusta metoda po cichu zmieniłaby zachowanie. Od Strategy różni się tym, że stan zmienia się w czasie życia obiektu, zwykle na skutek jego własnych operacji.

**Efekt:** Tabelę przejść da się przeczytać z kodu stanów, a `Reservation` tylko deleguje i przechowuje dane. Kolejność "obciążenie - zmiana stanu - efekt" zostaje jak w start, więc awaria bramki nadal zostawia rezerwację w stanie NEW.

**Różnica względem Javy:** Java buduje stany z prywatnego `sealed interface ReservationState` z metodami `default` i enumów-singletonów (`NewState.INSTANCE`, `PaidState.INSTANCE`, `ClosedState.USED/EXPIRED/CANCELLED`). TypeScript nie ma metod domyślnych w interfejsie ani enumów z zachowaniem, więc stan to niezmienny obiekt z polem `status` i przejściami jako funkcjami `(reservation: Reservation) => void`. Tworzy go funkcja `state(status, allowed)`, która wypełnia wszystkie przejścia domyślnie wyjątkiem i nadpisuje tylko dozwolone. Stany są polami `private static readonly` klasy `Reservation` - dzięki temu ich funkcje mogą wołać prywatne `charge`, `moveTo` i `record` (TypeScript nie ma widoczności pakietowej). Komunikaty są jak w Javie: `cannot pay in PAID`.

### Co widzimy

`Reservation` ma pole `currentStatus` i w każdej operacji (`pay`, `use`, `expire`, `cancel`) warunki sprawdzające i zmieniające status. Reguły przejść NEW -> PAID -> USED, NEW -> EXPIRED, NEW/PAID -> CANCELLED są rozsiane po metodach. `pay` najpierw obciąża bramkę, potem zmienia stan: gdy bramka rzuci wyjątek, rezerwacja zostaje NEW.

```typescript
cancel(): void {
  if (this.currentStatus === Status.NEW) {
    this.currentStatus = Status.CANCELLED;
    this.effectList.push('seats released');
  } else if (this.currentStatus === Status.PAID) {
    this.currentStatus = Status.CANCELLED;
    this.effectList.push('refund');
    this.effectList.push('seats released');
  } else {
    throw new IllegalStateError(`cannot cancel in ${this.currentStatus}`);
  }
}
```

**Najpierw tabela przejść.** `S08EquivalenceTest` zawiera tabelę 5 stanów x 4 akcje (20 wierszy) plus przypadek awarii bramki. Każdy wiersz sprawdza status końcowy, efekty w kolejności i komunikat wyjątku. Pokaż ją na rzutniku przed pierwszym ruchem.

### Krok 1: obiekt stanu zamiast pola status

**W IDE:** utwórz prywatny dla modułu interfejs `ReservationState` z polem `readonly status: Status` i stany jako stałe `private static readonly NEW`, `PAID`, `USED`, `EXPIRED`, `CANCELLED` (na razie literały `{ status: Status.NEW }`). Pole `currentStatus` (w Javie `status` - tu nazwa jest zajęta przez metodę `status()` z kontraktu `ReservationActions`) zastąp polem `state`; warunki porównują `this.state.status`. Efekty i zmianę stanu wyciągnij do `charge()`, `moveTo(...)`, `record(...)` (⌃⇧R > Extract to method in class 'Reservation').
**Po:**

```typescript
private state = Reservation.NEW;
...
this.charge();
this.moveTo(Reservation.PAID);
this.record('charged');
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s08` - 84 testy zielone.
**Co powiedzieć:** stany końcowe USED, EXPIRED i CANCELLED zachowują się tak samo (każde przejście jest błędem) - różnią się tylko statusem.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s08 0 1`

### Krok 2: pay i use do stanów

**W IDE:** w interfejsie stanu dodaj przejścia `pay` i `use` typu `Transition`. Napisz funkcję `state(status, allowed)`, która dla każdego przejścia wstawia domyślnie funkcję rzucającą `cannot <akcja> in <status>`, a potem nakłada dozwolone (`...allowed`). W `NEW` podaj `pay`, w `PAID` - `use`, przenosząc ciała gałęzi. Kontekst deleguje: `this.state.pay(this)`.
**Po:**

```typescript
private static readonly NEW: ReservationState = state(Status.NEW, {
  pay: (reservation) => {
    reservation.charge();
    reservation.moveTo(Reservation.PAID);
    reservation.record('charged');
  },
});
```

**Uruchom:** test zielony.
**Co powiedzieć:** domyślne przejście **rzuca wyjątek**, nie jest puste - pusta funkcja po cichu zmieniłaby zachowanie niedozwolonych przejść. Kolejność "obciążenie - zmiana stanu - efekt" zostaje dokładnie jak w start; test awarii bramki to pilnuje.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s08 1 2`

### Krok 3: expire i cancel do stanów

**W IDE:** ten sam ruch dla `expire` i `cancel`. Warunek `if/else if` w `cancel` rozkłada się na dwa stany: NEW (zwolnienie miejsc) i PAID (zwrot i zwolnienie).
**Po:**

```typescript
cancel(): void {
  this.state.cancel(this);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** tabelę przejść da się teraz przeczytać z kodu stanów - każdy stan wymienia tylko swoje dozwolone przejścia. Stany są bezstanowymi stałymi, dane zostają w `Reservation`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s08 2 3`

### Rozwiązanie i uzasadnienie

Kontekst tylko deleguje. Każdy stan nadpisuje wyłącznie dozwolone przejścia, reszta rzuca wyjątek z tym samym komunikatem co start, a stan po błędzie się nie zmienia.

### Pułapki

- Zmiana stanu przed efektem zewnętrznym (albo odwrotnie) "przy okazji" - to inna semantyka przy awarii.
- Asynchroniczna bramka (`await payments.charge(...)`) - między sprawdzeniem stanu a przejściem może wejść `cancel`; jednowątkowość JavaScriptu nie daje atomowości przejścia przez `await`.
- Przy dwóch stanach i jednej akcji `switch` bywa czytelniejszy niż obiekty stanów.
- Kilka niezależnych osi stanu (płatność, obecność) w jednym zestawie stanów - eksplozja kombinacji.

### Pytanie do sali

Czym różni się State od Strategy, skoro oba to "obiekt, któremu delegujemy"?

## Scena s09. Replace Hard-coded Notifications with Observer - po opłaceniu

**Temat ze slajdów:** Replace Hard-coded Notifications with Observer; Observer - kontrakt i migracja
**Katalog:** `typescript/src/workshop/m6/s09_observer` · **Test:** `scripts/warsztat.sh --lang ts test m6/s09`
**Czas:** ~15 min

### W skrócie

**Co robimy:** `PaymentService.confirm` po zapisaniu opłaty na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe. Wprowadzamy zdarzenie `ReservationPaid`, zamieniamy trzech odbiorców na implementacje `PaymentListener`, a subskrypcje przenosimy do korzenia kompozycji `PaymentServices`.

**Zasada:** Observer odwraca zależność: publikujący zna tylko interfejs odbiorcy, a odbiorcy są do niego rejestrowani z zewnątrz. Kontrakt musi być jawny - synchroniczność, kolejność, polityka błędów (tu fail-fast) i wyrejestrowanie. Dodanie nowych odbiorców to rozszerzenie zachowania, a nie część refaktoryzacji.

**Efekt:** Serwis nie importuje `Mailer`, `SmsGateway` ani `LoyaltyProgram`, a odbiorcy są wołani w kolejności subskrypcji jak w start. Zostaje ta sama semantyka błędów: awaria SMS przerywa naliczanie punktów, a jej zmiana byłaby osobną decyzją.

**Różnica względem Javy:** `Subscription extends AutoCloseable` to interfejs z `close()` (bez `Symbol.dispose`/`using` - lib ES2023 ich nie ma). `CopyOnWriteArrayList` zastępuje pole z niezmienną tablicą podmienianą przy `subscribe`/`close` (kopia przy zapisie, pętla iteruje po migawce), a `AtomicBoolean` - zwykła zmienna w domknięciu, bo JavaScript jest jednowątkowy. Prywatna zagnieżdżona klasa `Registration` to klasa prywatna modułu. Odbiorcy z testów to literały `{ onPaid: ... }`, a punkty liczy `Math.trunc(amount.trunc().toNumber() / 10)`.

### Co widzimy

Serwis powstaje w korzeniu kompozycji `PaymentServices.standard(mailer, sms, loyalty)`. `PaymentService.confirm` zapisuje opłatę, a potem na sztywno wysyła mail, SMS i nalicza punkty lojalnościowe (1 pkt za pełne 10.00). Semantyka do zachowania: **kolejność** mail - SMS - punkty i **fail-fast**: wyjątek w SMS przerywa, punkty nie są naliczone, ale opłata jest już zapisana.

```typescript
this.paidList.push(payment.reservationId);
this.mailer.send(payment.email,
  `Potwierdzenie platnosci ${payment.reservationId}: ${payment.amount.toString()}`);
this.sms.send(payment.phone, `Oplacono ${payment.reservationId}`);
this.loyalty.addPoints(payment.email, Math.trunc(payment.amount.amount.trunc().toNumber() / 10));
```

Test używa fake'ów zapisujących do wspólnego logu i sprawdza kolejność oraz awarie w mailu i SMS.

### Krok 1: zdarzenie i Extract Method

**W IDE:** utwórz niezmienną klasę `ReservationPaid` (pola `readonly`). Zaznacz trzy powiadomienia, ⌃⇧R > Extract to method in class 'PaymentService', nazwa `notifyPaid`, i zmień jej parametr na `event: ReservationPaid`.
**Po:**

```typescript
this.paidList.push(payment.reservationId);
this.notifyPaid(new ReservationPaid(
  payment.reservationId, payment.email, payment.phone, payment.amount));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s09` - zielone.
**Co powiedzieć:** zdarzenie to fakt z danymi potrzebnymi odbiorcom. Jeszcze nic nie jest dynamiczne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s09 0 1`

### Krok 2: odbiorcy jako obserwatorzy

**W IDE:** interfejs `PaymentListener` z `onPaid(event)` i trzy klasy: `MailConfirmation`, `SmsConfirmation`, `LoyaltyPoints` (każda z ciałem jednej linii z `notifyPaid`). Serwis tworzy zamrożoną tablicę odbiorców w konstruktorze w starej kolejności; `notifyPaid` to pętla **bez** `try/catch`.
**Po:**

```typescript
for (const listener of this.listeners) {
  listener.onPaid(event);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** konstruktor bez zmian, relacja wciąż "ci sami trzej odbiorcy" - to nadal czysta refaktoryzacja.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s09 1 2`

### Krok 3: subscribe i korzeń kompozycji

**W IDE:** `subscribe(listener)` zwracające `Subscription` (idempotentne `close()`), pole z niezmienną tablicą rejestracji podmienianą przy każdym zapisie. Konstruktor serwisu staje się bezargumentowy, pętla powiadomień trafia do `confirm`, a subskrypcje przenosimy do istniejącego `PaymentServices.standard(...)`.
**Po:**

```typescript
const service = new PaymentService();
service.subscribe(new MailConfirmation(mailer));
service.subscribe(new SmsConfirmation(sms));
service.subscribe(new LoyaltyPoints(loyalty));
```

**Uruchom:** test zielony; `S09SolutionTest` pokazuje podwójną subskrypcję, idempotentne wyrejestrowanie i nowego odbiorcę.
**Co powiedzieć:** każde `subscribe` tworzy osobny obiekt `Registration`, a `close` usuwa go po tożsamości (`r !== registration`). Gdybyśmy trzymali samego słuchacza i filtrowali po nim, zamknięcie jednej z dwóch subskrypcji tego samego słuchacza usunęłoby obie. Dodanie czwartego odbiorcy (push) to już **rozszerzenie** zachowania, zatwierdzane osobno.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s09 2 3`

### Rozwiązanie i uzasadnienie

Serwis zna tylko `PaymentListener`. Kontrakt jest jawny: synchronicznie, w kolejności subskrypcji, fail-fast, iteracja po migawce.

### Pułapki

- "Przy okazji" dodany `try/catch` wokół każdego odbiorcy - zmienia politykę błędów (punkty naliczone mimo awarii SMS).
- Asynchroniczne powiadamianie (`queueMicrotask`, `setTimeout`, odbiorca `async` bez `await`) - inna kolejność, a błąd odbiorcy ląduje jako nieobsłużone odrzucenie zamiast przerwać `confirm`.
- Odbiorca usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej (migawka).
- Niezamknięta subskrypcja trzyma przy życiu graf obiektów.

### Pytanie do sali

Czy błąd wysyłki SMS powinien przerywać naliczanie punktów? Kto o tym decyduje i gdzie to zapisać?

## Scena s10. Replace Implicit Tree with Composite - zestawy baru

**Temat ze slajdów:** Replace Implicit Tree with Composite; Implicit Tree - mapper, procedura, ryzyka
**Katalog:** `typescript/src/workshop/m6/s10_implicittree` · **Test:** `scripts/warsztat.sh --lang ts test m6/s10`
**Czas:** ~15 min

### W skrócie

**Co robimy:** Zestaw combo to zagnieżdżona tablica, w której pierwszy element jest nazwą, a `BarMenu` powtarza sprawdzanie typu elementu (`typeof`, `Array.isArray`). Budujemy obok jawny Composite (`Product`, `Combo`) z mapperem ze starego formatu, a potem przenosimy na niego `price` i `render` po jednej operacji.

**Zasada:** Replace Implicit Tree with Composite zamienia drzewo ukryte w konwencji danych na jawne typy liścia i węzła. Mapper tłumaczy stary format i zgłasza błąd dla danych, których nie da się wiernie odwzorować. Stara i nowa reprezentacja żyją obok siebie, a test różnicowy potwierdza zgodność, ale nie zastępuje niezależnych oczekiwań.

**Efekt:** `BarMenu` nie sprawdza już typów elementów, a komunikaty błędów zostają jak w start. Format trwały (zagnieżdżone tablice) się nie zmienia - jego zmiana to osobna decyzja z osobnym testem.

**Różnica względem Javy:** `List<?>` to `readonly unknown[]`, a `instanceof String`/`instanceof List` - `typeof element === 'string'` i `Array.isArray(element)`. `sealed interface MenuItem permits Product, Combo` to unia `type MenuItem = Product | Combo`. `StringBuilder` w `render(depth, text)` zastępuje bufor `string[]` łączony `join('')`, a prywatne przeciążenie `render` w `BarMenu` nazywa się `renderTo`. `Product` przechowuje cenę w polu `amount` i udostępnia metodę `price()` (pole i metoda nie mogą mieć tej samej nazwy). Test różnicowy używa klasy `JavaRandom` - wiernej kopii generatora `java.util.Random` - więc losuje dokładnie te same 500 drzew co w Javie.

### Co widzimy

Zestaw combo to zagnieżdżona tablica: pierwszy element to nazwa, kolejne to `'nazwa=cena'` albo podtablice. `BarMenu.price` i `BarMenu.render` powtarzają sprawdzanie typu elementu, a `render` dla każdego węzła liczy cenę od nowa.

```typescript
['Zestaw Duo', 'Popcorn L=18.00',
  ['Napoje', 'Cola 0.5=9.00', 'Cola 0.5=9.00'], 'Nachos=14.00']
```

Testy: `S10EquivalenceTest` (niezależne oczekiwania policzone ręcznie, także błędy) i `S10DifferentialTest` (500 losowych drzew ze stałym ziarnem - stara reprezentacja kontra nowa).

### Krok 1: Composite i mapper obok starego kodu

**W IDE:** utwórz unię `MenuItem = Product | Combo`, klasy `Product` i `Combo` oraz `MenuMapper.fromNested(combo: readonly unknown[])`. `BarMenu` bez zmian. Uruchom test różnicowy - porównuje stary kod z drzewem z mappera.
**Po:**

```typescript
for (const element of combo.slice(1)) {
  if (typeof element === 'string') {
    items.push(MenuMapper.product(element));
  } else if (Array.isArray(element)) {
    items.push(MenuMapper.fromNested(element));
  } else {
    throw new IllegalArgumentError(`unsupported element: ${String(element)}`);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s10` - zielone, w tym `compositeFromMapperMatchesLegacyAlready`.
**Co powiedzieć:** obie reprezentacje żyją obok siebie. Mapper zachowuje komunikaty błędów starego kodu - to też obserwowalne zachowanie.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s10 0 1`

### Krok 2: price na Composite

**W IDE:** ciało `BarMenu.price` zastąp `MenuMapper.fromNested(combo).price()`. Usuń nieużywane `requireName` (VS Code wyszarza nieużywaną prywatną metodę).
**Po:**

```typescript
price(combo: readonly unknown[]): Money {
  return MenuMapper.fromNested(combo).price();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** jedna operacja naraz; `render` wciąż jest stary i działa, bo woła nowe `price`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s10 1 2`

### Krok 3: render na Composite

**W IDE:** `render` deleguje do `Combo.render(0, text)`; starą rekurencję `renderTo` i `productPrice` usuń (ręcznie - po zmianie stają się nieużywane).
**Po:**

```typescript
const text: string[] = [];
MenuMapper.fromNested(combo).render(0, text);
return text.join('');
```

**Uruchom:** test zielony, test różnicowy `finalBarMenuMatchesLegacy` zielony.
**Co powiedzieć:** format trwały (zagnieżdżone tablice) zostaje. Jego zmiana na obiekty JSON czy tabelę w bazie to osobna decyzja z osobnym testem.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s10 2 3`

### Rozwiązanie i uzasadnienie

Jawne drzewo z typami zamiast konwencji "pierwszy element to nazwa". Test różnicowy daje szerokie pokrycie, a niezależne oczekiwania chronią przed sytuacją, w której oba warianty mylą się tak samo.

### Pułapki

- Test różnicowy jako jedyny test - jeśli stary kod ma błąd, nowy go wiernie powtórzy.
- Brak limitu głębokości dla danych niezaufanych - `RangeError: Maximum call stack size exceeded`.
- Cykl w danych (tablica zawierająca samą siebie) - nieskończona rekurencja w obu wersjach.

### Pytanie do sali

Co zrobić, gdy mapper natrafi na dane, których nie da się wiernie odwzorować w drzewie?

## Scena s11. Transparent vs Safe Composite - add() na liściu

**Temat ze slajdów:** Replace One/Many Distinctions with Composite (Safe i Transparent Composite)
**Katalog:** `typescript/src/workshop/m6/s11_safecomposite` · **Test:** `scripts/warsztat.sh --lang ts test m6/s11`
**Czas:** ~8 min

### W skrócie

**Co robimy:** W Transparent Composite `add()` jest we wspólnym `MenuComponent`, więc wywołanie go na produkcie kompiluje się i wybucha dopiero w runtime. Przesuwamy `add` i `children` w dół do `Combo` (Safe Composite), a potem pokazujemy niemutowalne drzewo z obiektów wartości bez `add`.

**Zasada:** Transparent Composite trzyma zarządzanie dziećmi we wspólnym typie, więc liść musi rzucić wyjątek albo nic nie robić. Safe Composite trzyma je tylko w węźle, dzięki czemu błąd przenosi się do kompilacji, ale klient musi wiedzieć, czy ma w ręku węzeł. Gdy drzewo powstaje raz, dylemat znika, bo `add` nie jest potrzebne nigdzie.

**Efekt:** Dodanie dziecka do produktu przestaje się kompilować, a `MenuComponent` nie ma już `add` ani `children`. Kosztem jest dokładniejsze typowanie zmiennych w katalogu - asercja `as Combo` w kliencie oznaczałaby powrót do problemu.

**Różnica względem Javy:** krok 2 (w Javie `sealed interface` i rekordy) to unia `type MenuComponent = Product | Combo` z polem `kind`, a `name` i `price` są w nim polami tylko do odczytu (w `Combo` `price` to getter liczący sumę), a nie metodami jak w start i kroku 1. `Product` ma jeden konstruktor przyjmujący cenę jako `Money | string` (w Javie dwa konstruktory). `S11SolutionTest` zamiast refleksji szuka metody przez `Reflect.get(Klasa.prototype, 'add')`, a niemutowalność sprawdza `TypeError` przy `push` na zamrożonej tablicy dzieci.

### Co widzimy

Transparent Composite: `add()` i `children()` są we wspólnej klasie abstrakcyjnej `MenuComponent`, liść `Product` dziedziczy `add()`, które rzuca `UnsupportedOperationError`. Klient typuje wszystko jako `MenuComponent` i nic go nie chroni przed `nachos.add(sos)`.

```typescript
add(_child: MenuComponent): void {
  throw new UnsupportedOperationError(`cannot add to ${this.name()}`);
}
```

### Krok 1: Safe Composite - Push Members Down

**W IDE:** VS Code nie ma Push Members Down - wytnij `add` i `children` z `MenuComponent` i wklej do `Combo` (z listą dzieci jako prywatnym polem). `npm run typecheck` pokaże błędy w `ComboCatalog` - zmień typ zmiennych z `MenuComponent` na `Combo` tam, gdzie dodajemy dzieci. `describe()` w `Combo` rozszerza opis o dzieci.
**Po:**

```typescript
const combo: Combo = new Combo('Zestaw Rodzinny');
combo.add(new Product('Popcorn XL', '24.00'));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s11` - zielone; `S11SolutionTest` pokazuje, że `Product` nie ma już `add`.
**Co powiedzieć:** błąd przeniósł się z runtime do kompilacji (vitest go nie zobaczy - widać go w `npm run typecheck` i w edytorze). Ceną jest to, że klient musi wiedzieć, czy ma w ręku węzeł.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s11 0 1`

### Krok 2: niemutowalny Composite jako unia

**W IDE:** unia `type MenuComponent = Product | Combo`, niezmienne klasy `Product` i `Combo(name, children)` z `Combo.of(...)` i polem `kind`. Katalog zapisany deklaratywnie.
**Po:**

```typescript
case 'duo': return Combo.of('Zestaw Duo',
  new Product('Popcorn L', '18.00'),
  new Product('Cola', '9.00'),
  new Product('Cola', '9.00'));
```

**Uruchom:** test zielony.
**Co powiedzieć:** gdy drzewo powstaje raz, `add()` nie jest potrzebne nigdzie - dylemat Safe/Transparent znika.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s11 1 2`

### Rozwiązanie i uzasadnienie

Safe Composite, gdy drzewo jest modyfikowane; niemutowalny Composite, gdy jest budowane raz (wtedy budowę przejmuje Builder albo fabryka - scena s06).

### Pułapki

- Transparent Composite z domyślnym pustym `add()` (bez wyjątku) - dziecko po cichu ginie.
- Asercja `component as Combo` (albo `as any`) w kliencie zamiast poprawienia typów - Safe Composite w przebraniu Transparent, a w TypeScripcie asercja nic nie sprawdza w runtime.

### Pytanie do sali

Kiedy Transparent Composite jest lepszym wyborem mimo ryzyka błędu w runtime?

## Scena s12. Replace One/Many Distinctions with Composite - zwroty

**Temat ze slajdów:** Replace One/Many Distinctions with Composite
**Katalog:** `typescript/src/workshop/m6/s12_onemany` · **Test:** `scripts/warsztat.sh --lang ts test m6/s12`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RefundService` ma osobne `refund` i `refundAll` z regułą zwrotu zapisaną dwa razy, a `CancellationDesk` sam wybiera między nimi. Najpierw usuwamy duplikację reguły, potem wprowadzamy wspólny kontrakt `Refundable` z liściem i grupą, a stare metody usuwamy po etapie `@deprecated`.

**Zasada:** Replace One/Many Distinctions with Composite zastępuje dwie ścieżki API - dla jednego elementu i dla wielu - jednym kontraktem, w którym grupa też jest elementem. Nie wolno przy tym zmienić kolejności przetwarzania, wyniku dla pustej grupy ani liczby efektów naliczanych raz na wywołanie.

**Efekt:** Jest jedna metoda `refund(refundable)`, reguła biletu żyje w liściu, suma w węźle, a klient nie ma już `if`. Potrącenie 3.00 zostaje w serwisie i nadal jest naliczane raz na zwrot, a nie raz na bilet.

**Różnica względem Javy:** TypeScript nie ma przeciążeń metod, więc w kroku 2 stara metoda `refund(TicketData, now)` nie może współistnieć z nową `refund(Refundable, now)` - dostaje nazwę `refundTicket` i znacznik `@deprecated` w TSDoc (`refundAll` bez zmian nazwy). `Refundable` to unia `SingleTicket | TicketGroup` z polem `kind` (odpowiednik `sealed interface`).

### Co widzimy

`RefundService` ma `refund(ticket, now)` i `refundAll(tickets, now)`, a klient `CancellationDesk` sam wybiera jedną z nich (`if (tickets.length === 1 ...)`). Reguła zwrotu (>= 24h 100%, < 24h 50%, po starcie 0) jest napisana dwa razy, trochę inaczej. Potrącenie 3.00 jest naliczane raz na zwrot i nie schodzi poniżej 0.

```typescript
for (const ticket of tickets) {
  if (now.isBefore(ticket.showStart)) {
    const hours = Duration.between(now, ticket.showStart).toHours();
    total = total.plus(hours >= 24 ? ticket.price : ticket.price.percent(50));
  }
}
```

Test obejmuje granicę dokładnie 24h, 23h59m, bilet po starcie, listę i pustą listę.

### Krok 1: Extract Method na wspólnej regule

**W IDE:** w `refund` zaznacz wyliczenie kwoty, ⌃⇧R > Extract to method in class 'RefundService', nazwa `ticketShare(ticket, now)` (w porcie `private static`). W `refundAll` zastąp ciało pętli wywołaniem `ticketShare`.
**Po:**

```typescript
total = total.plus(RefundService.ticketShare(ticket, now));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s12` - zielone.
**Co powiedzieć:** zanim wprowadzimy wzorzec, usuwamy duplikację reguły. Test granicy 24h potwierdza, że obie wersje były równoważne.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s12 0 1`

### Krok 2: Composite i jeden kontrakt

**W IDE:** unia `Refundable = SingleTicket | TicketGroup`, obie klasy z `refundableAmount(now)`: liść `SingleTicket` (reguła z `ticketShare`), węzeł `TicketGroup` (suma). Nowa metoda `refund(refundable, now)`; stare metody jako delegacje z `@deprecated` (stara `refund` zmienia nazwę na `refundTicket` - F2 Rename Symbol poprawi wywołania). `CancellationDesk` przechodzi na nowy kontrakt (na razie wciąż wybiera `SingleTicket` albo `TicketGroup`).
**Po:**

```typescript
refund(refundable: Refundable, now: LocalDateTime): Money {
  return refundable.refundableAmount(now).minus(RefundService.FEE).max(Money.ZERO);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** potrącenie jest w jednym miejscu i naliczane raz na wywołanie - tak jak w obu starych metodach. VS Code przekreśla wywołania metod oznaczonych `@deprecated`, więc widać, kto jeszcze nie przeszedł.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s12 1 2`

### Krok 3: usunięcie starych metod

**W IDE:** po migracji klientów usuń `refundTicket` i `refundAll` (Find All References ⇧⌥F12 potwierdza, że nikt ich nie woła). W `CancellationDesk` usuń `if` - klient zawsze buduje `TicketGroup.of(tickets)`, także z jednego biletu.
**Po:**

```typescript
return this.service.refund(TicketGroup.of(tickets), now);
```

**Uruchom:** test zielony; `S12SolutionTest` pokazuje zagnieżdżone grupy.
**Co powiedzieć:** rozróżnienie "jeden/wiele" zniknęło z API. Grupa jednego biletu daje ten sam wynik co bilet.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s12 2 3`

### Rozwiązanie i uzasadnienie

Jeden kontrakt `refund(refundable)`, reguła biletu w liściu, suma w węźle. Stare API usunięte dopiero po etapie `@deprecated`.

### Pułapki

- Potrącenie przeniesione do liścia - wtedy lista biletów płaci 3.00 za każdy bilet (zmiana zachowania).
- Zmiana kolejności przetwarzania albo liczby transakcji przy przejściu na Composite.
- Pusta grupa - trzeba jawnie ustalić wynik (tu 0.00, jak w start).

### Pytanie do sali

Co, jeśli biznes chce potrącenia za każdą rezerwację, a jedna grupa obejmuje kilka rezerwacji?

## Scena s13. Extract Composite - kontenery programu

**Temat ze slajdów:** Extract Composite
**Katalog:** `typescript/src/workshop/m6/s13_extractcomposite` · **Test:** `scripts/warsztat.sh --lang ts test m6/s13`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `Marathon` i `ShortsBlock` powielają obsługę dzieci - listę, `add`, kopię `children()`, sumę minut i opis. Wydzielamy nadklasę `CompositeProgramItem` i podciągamy do niej to, co naprawdę wspólne, a regułę czasu zostawiamy w podklasach.

**Zasada:** Extract Composite wydziela wspólną nadklasę dla kilku kontenerów, które powielają zarządzanie dziećmi. Podobne pętle mogą znaczyć co innego, więc przed podciągnięciem porównujemy kontrakty, a nie tekst. Nowe reguły, np. wykrywanie cykli, to osobna zmiana zachowania.

**Efekt:** Lista dzieci, `add` i szkielet opisu są w jednym miejscu, a podklasy mają po kilkanaście linii. `minutes()` zostaje w podklasach, bo tylko maraton dolicza przerwy - podciągnięcie go do bazy zmieniłoby wynik.

**Różnica względem Javy:** TypeScript nie ma `final`, więc `add`, `children` i `childrenMinutes` w `CompositeProgramItem` są zwykłymi metodami (w Javie `final`) - przed nadpisaniem chroni tylko konwencja i przegląd kodu. `Film` przechowuje czas w prywatnym polu `length`, a `minutes()` zostaje metodą kontraktu `ProgramItem`.

### Co widzimy

`Marathon` i `ShortsBlock` powielają obsługę dzieci: lista, `add` z kontrolą `null`/`undefined` (`requireNonNull`), `children()` jako zamrożona kopia, suma minut, opis. Różnią się tylko regułą czasu: maraton dodaje 15 minut przerwy między pozycjami.

```typescript
return this.childList.length === 0 ? 0 : total + 15 * (this.childList.length - 1);
```

### Krok 1: Extract Superclass

**W IDE:** VS Code nie ma Extract Superclass - utwórz ręcznie `abstract class CompositeProgramItem implements ProgramItem` i przenieś do niej z `Marathon` pole `childList`, `add` i `children()`, a `minutes()` i `describe()` zadeklaruj jako `abstract`. Potem `ShortsBlock extends CompositeProgramItem` i usuń jego kopie; podklasy sięgają do dzieci przez `this.children()`.
**Po:**

```typescript
export abstract class CompositeProgramItem implements ProgramItem {
  private readonly childList: ProgramItem[] = [];

  add(child: ProgramItem): void {
    this.childList.push(requireNonNull(child, 'child'));
  }
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s13` - zielone.
**Co powiedzieć:** kontrakt dzieci (brak `null`, kopia listy) jest teraz w jednym miejscu dla wszystkich kontenerów.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s13 0 1`

### Krok 2: Pull Up sumy i opisu

**W IDE:** przenieś do bazy (ręcznie) `childrenMinutes()` jako `protected` i `describe()` jako szablon z hookiem `protected abstract label()`; nazwa kontenera trafia do chronionego konstruktora bazy. Podklasy zostają z regułą czasu i etykietą.
**Po:**

```typescript
override minutes(): number {
  const count = this.children().length;
  return count === 0 ? 0 : this.childrenMinutes() + 15 * (count - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podobne pętle mogą znaczyć co innego. Tu suma jest wspólna, ale przerwy nie - dlatego `minutes()` zostaje w podklasach.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s13 1 2`

### Rozwiązanie i uzasadnienie

`CompositeProgramItem` z pełną obsługą dzieci i szkieletem opisu; `Marathon` i `ShortsBlock` po kilkanaście linii.

### Pułapki

- Podciągnięcie całego `minutes()` do bazy - maraton traci przerwy.
- Wykrywanie cykli (maraton zawierający sam siebie) to osobna zmiana zachowania, nie część Extract Composite.

### Pytanie do sali

Trzeci kontener "blok z przerwą na reklamy co drugi film" - czy nadal pasuje do tej nadklasy?

## Scena s14. Unify Interfaces with Adapter - dwie bramki płatności

**Temat ze slajdów:** Unify Interfaces with Adapter; Adapter - co naprawdę trzeba przetłumaczyć
**Katalog:** `typescript/src/workshop/m6/s14_adapter` · **Test:** `scripts/warsztat.sh --lang ts test m6/s14`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CheckoutService.pay` mówi dwoma językami: dla starej bramki składa XML w groszach, dla nowej woła REST w złotych i łapie wyjątek odmowy. Wydzielamy obie gałęzie do metod o jednej sygnaturze, przenosimy je do adapterów interfejsu `PaymentGateway`, a serwis wybiera bramkę z mapy.

**Zasada:** Unify Interfaces with Adapter tłumaczy obcy interfejs na preferowany kontrakt klienta, tak żeby klient zależał tylko od niego. Adapter tłumaczy nazwy, jednostki, format i sposób zgłaszania błędów, ale nie udaje, że semantyka jest identyczna. Jeśli wystarczy Rename Method, adapter jest zbędny.

**Efekt:** Logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem, a trzeci dostawca to nowy adapter i wpis w mapie. Stary konstruktor zostaje jako jedyne miejsce znające biblioteki bramek - docelowo do przeniesienia do korzenia kompozycji.

**Różnica względem Javy:** zagnieżdżone typy `RestPayClient.ChargeRequest/ChargeResponse/RestPayException` to osobne eksporty modułu `RestPayClient.ts`. Grosze liczymy jako `amount.amount.times(100).toNumber()` (w Javie `movePointRight(2).longValueExact()`, które dodatkowo rzuca wyjątek przy ułamku grosza). Dwa konstruktory Javy w kroku 3 to sygnatury przeciążenia konstruktora - `(xml, rest)` i `(gateways: ReadonlyMap<string, PaymentGateway>)` - z jedną implementacją rozróżniającą argument przez `instanceof XmlPayGateway`, żeby stary sposób tworzenia działał bez zmian w klientach.

### Co widzimy

`CheckoutService.pay(provider, reservationId, amount)` mówi dwoma językami: dla starej bramki składa XML z kwotą w **groszach** i parsuje atrybuty odpowiedzi, dla nowej woła API REST w **złotych** i łapie wyjątek odmowy. "Biblioteki" bramek (`XmlPayGateway`, `RestPayClient`) są w katalogu sceny i ich nie zmieniamy.

```typescript
const grosze = amount.amount.times(100).toNumber();
const request = `<charge ref='${reservationId}' amount='${grosze}'/>`;
```

### Krok 1: Extract Method z jednolitą sygnaturą

**W IDE:** każdą gałąź ⌃⇧R > Extract to method in class 'CheckoutService': `payWithXml(reservationId, amount)` i `payWithRest(reservationId, amount)`, obie zwracają `PaymentResult`.
**Po:**

```typescript
if (provider === 'XML') {
  return this.payWithXml(reservationId, amount);
} else if (provider === 'REST') {
  return this.payWithRest(reservationId, amount);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s14` - zielone.
**Co powiedzieć:** identyczna sygnatura dwóch metod to gotowy kontrakt adaptera.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s14 0 1`

### Krok 2: interfejs i dwa adaptery

**W IDE:** interfejs `PaymentGateway` z `pay(reservationId, amount)`. Metody `payWithXml` i `payWithRest` przenieś ręcznie (VS Code nie przenosi metod między klasami) jako `pay` do `XmlPayAdapter` (razem z pomocniczym `attribute`) i `RestPayAdapter`. Konstruktor serwisu bez zmian - tworzy adaptery.
**Po:**

```typescript
constructor(xml: XmlPayGateway, rest: RestPayClient) {
  this.xml = new XmlPayAdapter(xml);
  this.rest = new RestPayAdapter(rest);
}
```

**Uruchom:** test zielony; `S14SolutionTest` sprawdza, że do starej bramki idzie `amount='4000'`.
**Co powiedzieć:** adapter tłumaczy jednostki (złote - grosze), format i sposób zgłaszania odmowy (atrybut kontra wyjątek). Nie udaje, że semantyka jest taka sama.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s14 1 2`

### Krok 3: serwis zależy tylko od interfejsu

**W IDE:** nowa sygnatura konstruktora `(gateways: ReadonlyMap<string, PaymentGateway>)` (kopia `new Map(gateways)`); stara sygnatura `(xml, rest)` zostaje jako przeciążenie i buduje mapę ze standardowym zestawem adapterów. `if` zastąp wyszukaniem w mapie; nieznany dostawca - ten sam wyjątek.
**Po:**

```typescript
const gateway = this.gateways.get(provider);
if (gateway === undefined) {
  throw new IllegalArgumentError(`unknown provider: ${provider}`);
}
return gateway.pay(reservationId, amount);
```

**Uruchom:** test zielony.
**Co powiedzieć:** logika serwisu zna tylko `PaymentGateway` i da się ją testować fake'iem. Stary konstruktor to jedyne miejsce, które jeszcze zna biblioteki bramek - docelowo przeniesiemy go do korzenia kompozycji. Trzeci dostawca to nowy adapter i wpis w mapie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s14 2 3`

### Rozwiązanie i uzasadnienie

Preferowany kontrakt kina (`PaymentGateway`, `PaymentResult`) i dwa adaptery. Granica 500.00 włącznie działa tak samo w obu bramkach - test to sprawdza.

### Pułapki

- Podobna sygnatura, inny kontrakt: jednostki, zaokrąglenia (`toNumber()` po cichu przepuści ułamek grosza), strefa czasowa, kodowanie znaków.
- Mapowanie wyjątku bez zachowania przyczyny (`new Error(msg, { cause })`) - trudna diagnoza w produkcji.
- Adapter zamykający zasób, którego nie jest właścicielem.
- Adapter tam, gdzie wystarczyłby Rename Method.

### Pytanie do sali

Stara bramka zwraca `status='ERROR'` przy błędnym XML. Jak to zamapować - na odmowę czy wyjątek? Kto decyduje?

## Scena s15. Replace Conditional Dispatcher with Command - konsola kasjera

**Temat ze slajdów:** Replace Conditional Dispatcher with Command; Command - sekwencja i ograniczenia
**Katalog:** `typescript/src/workshop/m6/s15_command` · **Test:** `scripts/warsztat.sh --lang ts test m6/s15`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `CashierConsole.handle` to łańcuch `if` po nazwie komendy z pełną logiką i zmianą stanu kasy w każdej gałęzi. Wydzielamy gałęzie do metod o wspólnej sygnaturze, zamieniamy je na bezstanowe komendy działające na `Till`, a łańcuch `if` zastępujemy mapą komend.

**Zasada:** Replace Conditional Dispatcher with Command zamienia każdą gałąź dyspozytora w obiekt komendy, a wybór gałęzi w wyszukanie w rejestrze. Mapa jest równoważna warunkom tylko wtedy, gdy klucze są rozłączne i zachowana jest normalizacja. Command nie daje automatycznie asynchroniczności, retry ani undo.

**Efekt:** Dodanie komendy to nowa klasa i wpis w mapie, bez zmiany `handle`. Komendy nie trzymają danych żądania, więc można je bezpiecznie współdzielić, a `sell` i `SELL` nadal działają tak samo.

**Różnica względem Javy:** `@FunctionalInterface ConsoleCommand` to interfejs z `execute(args, till)` - test dodaje komendę jako literał obiektu. Domyślny rejestr komend w kroku 3 to parametr domyślny konstruktora zamiast drugiego konstruktora. `toUpperCase(Locale.ROOT)` to zwykłe `toUpperCase()`, a `split(" ", 2)` - `split(' ')` z destrukturyzacją `[name = '', ...rest]` i `rest.join(' ')` (ten sam wynik).

### Co widzimy

`CashierConsole.handle(line)` to łańcuch `if (command === 'SELL')` z pełną logiką i modyfikacją stanu kasy w każdej gałęzi. Nazwa komendy jest normalizowana `toUpperCase()` - `sell` i `SELL` działają tak samo.

```typescript
if (command === 'SELL') {
  const [count = '', ...titleParts] = args.split(' ');
  ...
} else if (command === 'REFUND') {
  ...
} else if (command === 'REPORT') {
```

Test to sesja poleceń: sprzedaż, zwrot, raport oraz błędne polecenia, które nie zmieniają stanu.

### Krok 1: Extract Method dla gałęzi

**W IDE:** ciało każdej gałęzi ⌃⇧R > Extract to method in class 'CashierConsole': `sell(args)`, `refund(args)`, `report(args)` - ta sama sygnatura, także gdy `args` nie jest używane (`_args`).
**Po:**

```typescript
if (command === 'SELL') {
  return this.sell(args);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s15` - zielone.
**Co powiedzieć:** jednolita sygnatura przygotowuje interfejs komendy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s15 0 1`

### Krok 2: komendy jako obiekty, stan w Till

**W IDE:** nowa klasa `Till` (pola `cash`, `tickets` i cennik - ręczne Extract Class). Interfejs `ConsoleCommand` z `execute(args, till)`, klasy `SellCommand`, `RefundCommand`, `ReportCommand` z ciałami metod. Dyspozytor `if` jeszcze zostaje.
**Po:**

```typescript
if (command === 'SELL') {
  return this.sell.execute(args, this.till);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** komendy są bezstanowe - dane żądania to argument, stan kasy to argument. Współdzielona komenda ze stanem poprzedniego żądania to błąd czekający na drugie, przeplatane wywołanie (np. przez `await`).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s15 1 2`

### Krok 3: rejestr zamiast warunku

**W IDE:** `ReadonlyMap<string, ConsoleCommand>` jako parametr konstruktora z wartością domyślną (standardowy zestaw), `handle` wyszukuje komendę po znormalizowanym kluczu.
**Po:**

```typescript
const command = this.commands.get(name.toUpperCase());
if (command === undefined) {
  return `Nieznana komenda: ${name}`;
}
```

**Uruchom:** test zielony; `S15SolutionTest` dodaje komendę bez zmiany dyspozytora.
**Co powiedzieć:** mapa jest równoważna warunkom, bo klucze są **rozłączne** i normalizacja została zachowana. Przy nakładających się predykatach (`startsWith`) kolejność `if` byłaby częścią kontraktu i mapa by go zgubiła.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s15 2 3`

### Rozwiązanie i uzasadnienie

Dyspozytor bez wiedzy o komendach, komendy bezstanowe, stan w `Till`. Nowa komenda to nowa klasa i wpis w mapie.

### Pułapki

- Zgubiona normalizacja klucza - `sell 1 Diuna` nagle "nieznana komenda".
- Command traktowany jako darmowa asynchroniczność, retry albo undo - to osobne decyzje (idempotencja, kompensacja).
- Brak sprawdzenia kompletności rejestru przy starcie aplikacji.

### Pytanie do sali

Jak dodalibyście `UNDO` dla ostatniej sprzedaży? Czego wymaga to od komend?

## Scena s16. Form Template Method - raporty CSV i HTML

**Temat ze slajdów:** Apply Template Method; Template Method - sekwencja i ryzyka
**Katalog:** `typescript/src/workshop/m6/s16_templatemethod` · **Test:** `scripts/warsztat.sh --lang ts test m6/s16`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet (sortowanie, nagłówek, wiersze z sumą, stopka), ale napisany trochę inaczej. Wydzielamy różnice do metod `header`, `row`, `footer`, doprowadzamy `render` do identycznej postaci i podciągamy go do nadklasy `SalesReport` jako metodę chronioną przed nadpisaniem.

**Zasada:** Form Template Method umieszcza wspólną sekwencję kroków w metodzie bazowej, a zmienne kroki w metodach nadpisywanych przez podklasy. Stosuje się ją, gdy kolejność kroków jest stała i różnią się tylko szczegóły. Przy wielu hookach albo zmiennej kolejności lepsza jest kompozycja lub Strategy.

**Efekt:** Sortowanie i sumowanie są w jednym miejscu, a nowy format (np. Markdown) to trzy metody. Ochrona `render` pilnuje kolejności kroków, ale w opublikowanej bibliotece zablokowanie nadpisywania dotąd otwartej metody złamałoby klientów.

**Różnica względem Javy:** TypeScript nie ma `final`. `SalesReport` chroni szablon w runtime: konstruktor bazy rzuca `IllegalStateError('render() is final')`, jeśli podklasa nadpisała `render`. Test `templateMethodIsFinal` sprawdza, że `CsvSalesReport` i `HtmlSalesReport` nie mają własnego `render` (`Object.hasOwn(Klasa.prototype, 'render')`) oraz że podklasa nadpisująca `render` jest odrzucana (w Javie: `Modifier.isFinal`). `StringBuilder` to sklejanie łańcucha `text += ...`.

### Co widzimy

`CsvSalesReport` i `HtmlSalesReport` mają ten sam szkielet: kopia tablicy, sortowanie po godzinie, nagłówek, wiersze z sumowaniem, stopka. Napisane trochę inaczej (`toSorted` i tablica fragmentów kontra kopia z `sort` i sklejanie łańcucha, inne nazwy zmiennych), więc duplikacja nie rzuca się w oczy. Różnice to formatowanie i escapowanie (`;` w CSV, `&` w HTML).

### Krok 1: Extract Method na różnicach

**W IDE:** w obu klasach ⌃⇧R > Extract to method in class dla nagłówka, wiersza i stopki: `header()`, `row(sale)`, `footer(tickets, total)`. Przepisz `render` HTML na ten sam kształt co CSV (nazwy zmiennych, sortowanie).
**Po:**

```typescript
let text = this.header();
let tickets = 0;
let total = Money.ZERO;
for (const sale of sorted) {
  text += this.row(sale);
  ...
}
return text + this.footer(tickets, total);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s16` - zielone.
**Co powiedzieć:** ujednolicamy sygnatury po kroku, aż `render` obu klas będzie identyczny znak w znak.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s16 0 1`

### Krok 2: Extract Superclass + Pull Up render

**W IDE:** VS Code nie ma Extract Superclass - utwórz ręcznie `abstract class SalesReport`, przenieś do niej `render` z CSV, a `header`, `row`, `footer` zadeklaruj jako `protected abstract`. Obie klasy `extends SalesReport`, w podklasach metody formatujące z `protected override`, kopie `render` usuń. Na koniec dodaj w konstruktorze bazy strażnika zamiast `final`.
**Po:**

```typescript
constructor() {
  if (this.render !== SalesReport.prototype.render) {
    throw new IllegalStateError('render() is final');
  }
}
...
protected abstract header(): string;
protected abstract row(sale: Sale): string;
protected abstract footer(tickets: number, total: Money): string;
```

**Uruchom:** test zielony; `S16SolutionTest` sprawdza ochronę `render` i dodaje format Markdown trzema metodami.
**Co powiedzieć:** strażnik chroni kolejność kroków - ale dopiero w runtime, przy tworzeniu obiektu, a nie w kompilacji jak `final` w Javie. W opublikowanej bibliotece zablokowanie nadpisywania metody łamie klientów, którzy już ją nadpisali.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s16 1 2`

### Rozwiązanie i uzasadnienie

Szkielet raz, w bazie; podklasy to czyste formatowanie. Pusta sprzedaż daje nagłówek i sumę 0.00 w obu formatach.

### Pułapki

- Hook opcjonalny z domyślną implementacją, która nie pasuje do wszystkich podklas.
- Wywołanie hooka z konstruktora bazy - podklasa widzi niezainicjalizowane pola (pola podklasy są przypisywane dopiero po `super()`).
- Wiele hooków i zmienna kolejność - wtedy lepsza kompozycja lub Strategy.

### Pytanie do sali

Raport PDF potrzebuje stronicowania co 30 wierszy. Czy to jeszcze Template Method?

## Scena s17. Limit Instantiation with Singleton - cennik

**Temat ze slajdów:** Limit Instantiation with Singleton
**Katalog:** `typescript/src/workshop/m6/s17_singleton` · **Test:** `scripts/warsztat.sh --lang ts test m6/s17`
**Czas:** ~10 min

### W skrócie

**Co robimy:** `TicketDesk` przy każdej wycenie tworzy nowy `PriceList` i parsuje taryfę, choć cennik jest niemutowalny. Po pomiarze ograniczamy go do jednej instancji (najpierw `getInstance()`, potem zamrożona stała modułu), a na końcu ukrywamy za interfejsem `Tariff` wstrzykiwanym do `TicketDesk`.

**Zasada:** Limit Instantiation with Singleton to decyzja o cyklu życia: jedna instancja jest bezpieczna tylko wtedy, gdy obiekt jest niemutowalny, a instancje równoważne. Stała modułu ES daje jedną instancję, ale na graf modułów, nie na proces. Odwrotny ruch, Inline Singleton, jest równie ważny, gdy globalny dostęp szkodzi testom.

**Efekt:** Cennik powstaje raz, a `TicketDesk` zależy od kontraktu i da się go przetestować z innym cennikiem bez globalnego stanu. Globalna instancja zostaje tylko w wartości domyślnej konstruktora, a dodanie do niej mutowalnego stanu byłoby najgorszym wariantem.

**Różnica względem Javy:** javowy "singleton jako enum" (`PriceList.INSTANCE`) nie ma odpowiednika w `enum` TypeScriptu, więc krok 2 to zamrożona stała modułu `export const PriceList = Object.freeze({ basePrice })`. Moduł ES jest ewaluowany raz, więc to jedna instancja - ale na graf modułów (dwie kopie pakietu w `node_modules` albo worker to osobne instancje), tak jak enum w Javie to jedna instancja na loader klas. Klient woła `PriceList.basePrice(...)`. W kroku 3 `PriceList: Tariff`, a `TicketDesk(tariff: Tariff = PriceList)` ma parametr domyślny zamiast drugiego konstruktora. Licznik `created()` to zwykłe pole statyczne (bez `AtomicInteger`), a test pomiaru szuka go przez `Reflect.get` i po `jump` jest pomijany.

### Co widzimy

`TicketDesk.quote` przy każdym wywołaniu robi `new PriceList()`, a konstruktor parsuje taryfę. Cennik jest niemutowalny, więc instancje są równoważne. `S17SolutionTest` najpierw **mierzy**: trzy wyceny to trzy utworzone cenniki.

```typescript
const price = new PriceList().basePrice(format);
```

### Krok 1: klasyczny Singleton

**W IDE:** prywatny konstruktor, `private static readonly INSTANCE = new PriceList()`, `static getInstance()`. Licznik z pomiaru usuń. Klient woła `PriceList.getInstance()`.
**Po:**

```typescript
const price = PriceList.getInstance().basePrice(format);
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s17` - zielone.
**Co powiedzieć:** Singleton to decyzja o cyklu życia - bezpieczna tylko dlatego, że obiekt jest niemutowalny i udowodniliśmy równoważność instancji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s17 0 1`

### Krok 2: Singleton jako zamrożona stała modułu

**W IDE:** zamień klasę na stałą modułu `export const PriceList = Object.freeze({ basePrice(format) { ... } })`; taryfa parsowana raz, w funkcji `parse()` wywołanej przy ewaluacji modułu, do `ReadonlyMap`.
**Po:**

```typescript
const price = PriceList.basePrice(format);
```

**Uruchom:** test zielony.
**Co powiedzieć:** moduł ES daje jednokrotną inicjalizację bez leniwych sztuczek, a `Object.freeze` blokuje podmianę metody. Ale to **jedna instancja na graf modułów**, nie "jedna na proces" - dwie kopie pakietu albo worker mają własne.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s17 1 2`

### Krok 3: wstrzyknięcie zamiast globalnego dostępu

**W IDE:** wydziel interfejs `Tariff` z `basePrice(format)` (ręcznie - VS Code nie ma Extract Interface), oznacz stałą typem `PriceList: Tariff`; `TicketDesk` dostaje konstruktor `(tariff: Tariff = PriceList)`.
**Po:**

```typescript
constructor(tariff: Tariff = PriceList) {
  this.tariff = requireNonNull(tariff, 'tariff');
}
```

**Uruchom:** test zielony; `injectedTariffNeedsNoGlobalState` używa cennika promocyjnego jako literału obiektu `{ basePrice: () => Money.of('19.00') }`.
**Co powiedzieć:** o jednej instancji decyduje korzeń kompozycji, a klient zależy od kontraktu. Gdyby ktoś dodał do singletona `setPromo(...)`, testy zaczęłyby wpływać na siebie nawzajem (vitest uruchamia testy jednego pliku w tym samym grafie modułów) - mutowalny globalny stan to najgorszy wariant.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s17 2 3`

### Rozwiązanie i uzasadnienie

Jedna instancja cennika (stała modułu), ale ukryta za interfejsem i wstrzykiwana. Odwrotna transformacja (Inline Singleton) jest równie ważna - gdy globalny dostęp szkodzi testom, wracamy do jawnej zależności.

### Pułapki

- Singleton z mutowalnym stanem (promocje, cache) - testy zależne od kolejności.
- Leniwy singleton inicjowany asynchronicznie (`if (!instance) instance = await load()`) - dwa równoległe wywołania przed pierwszym `await` tworzą dwie instancje.
- Singleton "bo tak wygodniej" bez pomiaru kosztu tworzenia.

### Pytanie do sali

Cennik ma się zmieniać o północy bez restartu. Co wtedy z singletonem?

## Scena s18. Move Accumulation to Collecting Parameter - ostrzeżenia walidacji

**Temat ze slajdów:** Collecting Parameter
**Katalog:** `typescript/src/workshop/m6/s18_collectingparameter` · **Test:** `scripts/warsztat.sh --lang ts test m6/s18`
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ReservationValidator` skleja ostrzeżenia w łańcuchu, a każda metoda pomocnicza dopisuje własny separator. Zamieniamy sklejanie na tablicę, potem przekazujemy akumulator do metod `check...`, a na końcu zawężamy go do klasy `Warnings`.

**Zasada:** Collecting Parameter polega na tym, że metody dopisują wyniki do przekazanego akumulatora, zamiast zwracać fragmenty do sklejenia. Właścicielem kolekcji jest wywołujący, trzeba ustalić, czy metoda czyści, czy dopisuje, a wynik częściowy po wyjątku jest częścią kontraktu. Dobry akumulator ma wąski typ, a nie ogólną mutowalną kolekcję.

**Efekt:** Separator i format wyniku są w jednym miejscu, a metody pomocnicze mogą tylko dopisać ostrzeżenie. Treść, kolejność i separator ostrzeżeń są dokładnie takie jak w start.

**Różnica względem Javy:** `addAll` z kroku 1 to `push(...lista)`, `isBlank()` to `trim() === ''`, a `String.join` to `join('; ')`. `Set.add` w Javie zwraca `boolean`; w TypeScripcie warunek "duplikat zgłaszany raz" jest zapisany przez `has` i `add` (ta sama semantyka).

### Co widzimy

`ReservationValidator.validate` skleja ostrzeżenia w łańcuchu: każda metoda pomocnicza zwraca fragment zakończony `'; '`, a na końcu obcinamy dwa znaki. Łatwo zgubić separator.

```typescript
let warnings = '';
warnings += this.checkEmail(draft.email);
warnings += this.checkSeats(draft.seats);
...
return warnings === '' ? 'OK' : warnings.substring(0, warnings.length - 2);
```

Test sprawdza treść, kolejność i separator, w tym duplikaty miejsc zgłaszane jeden raz.

### Krok 1: tablica zamiast sklejania

**W IDE:** metody pomocnicze zwracają `readonly string[]` bez separatora, `validate` robi `push(...)` i `join('; ')`.
**Po:**

```typescript
warnings.push(...this.checkEmail(draft.email));
warnings.push(...this.checkSeats(draft.seats));
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s18` - zielone.
**Co powiedzieć:** separator dokłada teraz jedno miejsce. Metody wciąż tworzą własne tablice.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s18 0 1`

### Krok 2: Collecting Parameter

**W IDE:** zmiana sygnatury ręcznie (VS Code nie ma Change Signature): dodaj parametr `warnings: string[]`, zwracany typ `void`, zamień `return [x]` na `warnings.push(x)`. Warunek czasu seansu wyciągnij do `checkShowTime(draft, warnings)` (⌃⇧R > Extract to method in class).
**Po:**

```typescript
this.checkEmail(draft.email, warnings);
this.checkSeats(draft.seats, warnings);
this.checkShowTime(draft, warnings);
```

**Uruchom:** test zielony.
**Co powiedzieć:** metody dopisują do przekazanego akumulatora. Właścicielem kolekcji jest `validate` - tworzy ją i decyduje o formacie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s18 1 2`

### Krok 3: wąski typ parametru

**W IDE:** klasa `Warnings` z `add` i `summary()`; zmień typ parametrów z `string[]` na `Warnings` i `push` na `add` (VS Code nie ma Type Migration - `npm run typecheck` wskaże każde miejsce).
**Po:**

```typescript
const warnings = new Warnings();
...
return warnings.summary();
```

**Uruchom:** test zielony.
**Co powiedzieć:** metoda pomocnicza może tylko dopisać - nie wyczyści ani nie przestawi cudzych ostrzeżeń.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s18 2 3`

### Rozwiązanie i uzasadnienie

Każda reguła to metoda `check...(..., warnings: Warnings)`, format wyniku w jednym miejscu, akumulator o wąskim API.

### Pułapki

- Ogólny mutowalny obiekt (`Record<string, unknown>`, `Map`) jako parametr zbierający - ukrywa, kto co zapisuje.
- Niejasne, czy metoda czyści akumulator (`warnings.length = 0`), czy dopisuje.
- Wyjątek w połowie zbierania zostawia wynik częściowy - to część kontraktu, trzeba go ustalić.

### Pytanie do sali

Czy `Warnings` powinno pozwalać na poziomy (błąd kontra ostrzeżenie)? Jak to zmieni kontrakt?

## Scena s19. Visitor i macierz zmian - pozycje zamówienia

**Temat ze slajdów:** Visitor i macierz zmian
**Katalog:** `typescript/src/workshop/m6/s19_visitor` · **Test:** `scripts/warsztat.sh --lang ts test m6/s19`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ReceiptPrinter` ma trzy operacje na pozycjach zamówienia, każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Zamieniamy je na klasyczne Visitory, a potem pokazujemy alternatywę TypeScriptu: zamkniętą unię dyskryminowaną i wyczerpujący `switch` po polu `kind` z `assertNever`.

**Zasada:** Visitor przenosi operacje na strukturze do osobnych klas, a element wybiera właściwą metodę przez double dispatch. Macierz zmian mówi, kiedy go stosować: nowa operacja jest tania, nowy rodzaj elementu wymaga zmiany wszystkich Visitorów. W TypeScripcie tę samą kontrolę kompilatora daje wyczerpujący `switch` po unii dyskryminowanej, a Visitor ma sens, gdy zestawu typów nie da się zamknąć.

**Efekt:** W kodzie nie ma `instanceof`, a pozycja bez obsługi to błąd kompilacji zamiast awarii na kasie. Obie formy są poprawne - wybór zależy od tego, czy częściej dochodzą operacje, czy rodzaje pozycji.

**Różnica względem Javy:** nazwy `visitTicket/visitSnack/visitVoucher` są jak w Javie. Krok 3 (w Javie `sealed interface` i `switch` z record patterns) to unia `TicketItem | SnackItem | VoucherItem` z polem `kind` i `switch (item.kind)`; zamiast "switcha bez `default`" jest `default: return assertNever(item)`, który **nie** wyłącza kontroli kompilatora - nowy rodzaj pozycji to błąd kompilacji, bo `item` nie zawęża się do `never`. Record patterns zastępuje destrukturyzacja w gałęzi. Pusty interfejs znacznikowy `OrderItem` w start zostaje - w TypeScripcie strukturalnie akceptuje każdy obiekt, więc problem `instanceof` widać tym wyraźniej.

### Co widzimy

Pozycje zamówienia: bilet (VAT 8%), produkt baru (VAT 23%), voucher (pomniejsza kwotę, bez VAT). `ReceiptPrinter` ma trzy operacje - linia paragonu, kwota, VAT - każda z łańcuchem `instanceof` zakończonym wyjątkiem w runtime. Nowy rodzaj pozycji kompiluje się bez błędu i wybucha na kasie.

```typescript
if (item instanceof TicketItem) {
  return ReceiptPrinter.vatOf(item.price, 8);
} else if (item instanceof SnackItem) {
  return ReceiptPrinter.vatOf(item.price, 23);
} else if (item instanceof VoucherItem) {
  return Money.ZERO;
}
throw new IllegalArgumentError(`unknown item: ${String(item)}`);
```

### Krok 1: accept + pierwszy Visitor

**W IDE:** interfejs `OrderItemVisitor<R>` z `visitTicket/visitSnack/visitVoucher`, metoda `accept<R>(visitor)` w `OrderItem` i klasach pozycji (⌘. na klasie - Implement interface 'OrderItem'). Operację "linia paragonu" przenieś do `ReceiptLineVisitor`.
**Po:**

```typescript
text += `${item.accept(this.lines)}\n`;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s19` - zielone.
**Co powiedzieć:** double dispatch - element wybiera metodę odwiedzającego. Brak metody dla typu to błąd kompilacji (`npm run typecheck`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s19 0 1`

### Krok 2: pozostałe operacje jako Visitory

**W IDE:** `AmountVisitor` i `VatVisitor`, usuń łańcuchy `instanceof`.
**Po:**

```typescript
total = total.plus(item.accept(this.amounts));
vat = vat.plus(item.accept(this.vats));
```

**Uruchom:** test zielony; `S19SolutionTest` dodaje operację "grupa VAT" jako nowy Visitor.
**Co powiedzieć:** macierz zmian: nowa operacja - nowa klasa, tanio; nowy rodzaj pozycji - zmiana interfejsu i **wszystkich** Visitorów.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s19 1 2`

### Krok 3: alternatywa TypeScript - unia dyskryminowana + switch

**W IDE:** `type OrderItem = TicketItem | SnackItem | VoucherItem`, każda klasa z literałem `readonly kind = 'ticket'` itd.; usuń `accept` i Visitory, operacje jako `switch (item.kind)` zakończone `default: return assertNever(item)` (z destrukturyzacją w linii paragonu).
**Po:**

```typescript
switch (item.kind) {
  case 'ticket': return ReceiptPrinter.vatOf(item.price, 8);
  case 'snack': return ReceiptPrinter.vatOf(item.price, 23);
  case 'voucher': return Money.ZERO;
  default: return assertNever(item);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** ta sama kontrola kompilatora co w Visitorze, bez ceremonii `accept/visit`. Visitor ma sens, gdy zestawu typów nie da się zamknąć w unii (inny moduł, biblioteka, rozszerzenia) albo gdy odwiedzający ma stan i logikę przejścia struktury.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s19 2 3`

### Rozwiązanie i uzasadnienie

Dwie poprawne formy docelowe: klasyczny Visitor (`step2`) i wyczerpujący `switch` po zamkniętej unii (`step3`). Macierz zmian:

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje pozycji | Visitor albo switch po unii dyskryminowanej |
| nowe rodzaje pozycji, stabilne operacje | metody polimorficzne w pozycjach |
| jedna prosta akumulacja | Collecting Parameter (scena s18) |

### Pułapki

- `default` bez `assertNever` (np. `default: return Money.ZERO`) w `switch` po unii - wyłącza kontrolę kompilatora dla nowych typów.
- VAT zaokrąglany per pozycja kontra od sumy - różne wyniki, trzeba zachować sposób ze start.
- Visitor dla hierarchii, do której ciągle dochodzą typy - każdy nowy typ dotyka wszystkich Visitorów.

### Pytanie do sali

Dochodzi pozycja "karta podarunkowa" i operacja "eksport do księgowości". Która forma przyjmie te zmiany taniej?

## Scena s20. Mapa decyzji - jedna tabela, dwie struktury docelowe

**Temat ze slajdów:** Najpierw rodzaj zmienności; Mapa decyzji (1/2 i 2/2); Lista kontrolna
**Katalog:** `typescript/src/workshop/m6/s20_decisionmap` · **Test:** `scripts/warsztat.sh --lang ts test m6/s20`
**Czas:** ~12 min

### W skrócie

**Co robimy:** `ShowPricing` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki, w których splecione są format i reguła dnia. Najpierw rozplatamy dwie osie, a potem z tego samego punktu budujemy dwie alternatywy: Strategy dla reguły dnia albo typ formatu z zachowaniem.

**Zasada:** Wzorzec wybieramy według rodzaju zmienności, a nie kształtu kodu - ten sam `switch` może sygnalizować różne problemy. Pytamy, która oś zmienia się częściej, i tam budujemy punkt rozszerzenia. Gdy tego nie wiemy, zostajemy przy prostej strukturze bez nowych typów.

**Efekt:** Obie ścieżki są behawioralnie równoważne, a różnią się kosztem przyszłej zmiany: A tanio przyjmuje nowe akcje dniowe, B nowe formaty. Reguła zależna od obu osi naraz łamie założenie niezależności i wtedy tabela może być lepsza niż wzorzec.

**Różnica względem Javy:** `DayOfWeek` to unia literałów (`'MONDAY' | ... | 'SUNDAY'`) z `shared/time.ts`. `@FunctionalInterface DayPolicy` to typ funkcyjny `(base: Money) => Money`, a kalendarz - funkcja `(day) => DayPolicy`, więc wywołanie to `this.calendar(day)(base)` zamiast `calendar.apply(day).apply(base)`. `enum Format` z zachowaniem to klasa z instancjami statycznymi (`TWO_D`, `THREE_D`, `IMAX`), prywatnym konstruktorem i `values()`. Test równoważności parsuje dzień przez `DAYS_OF_WEEK` (odpowiednik `DayOfWeek.valueOf`).

### Co widzimy

`ShowPricing.price(day, format)` ma tabelę cen 3x3 wpisaną w zagnieżdżone warunki. Dwie osie zmienności - format i reguła dnia ("tani wtorek" -30%, weekend +2.00) - są splecione.

```typescript
if (format === '2D') {
  return Money.of(day === 'TUESDAY' ? '17.50' : weekend ? '27.00' : '25.00');
} else if (format === '3D') {
  return Money.of(day === 'TUESDAY' ? '22.40' : weekend ? '34.00' : '32.00');
} ...
```

Ta scena ma **dwie ścieżki**: krok 1 jest wspólny, krok 2 to ścieżka A (Strategy dla reguły dnia), krok 3 to ścieżka B (typ formatu z zachowaniem) - budowana od kroku 1, nie od kroku 2. Test równoważności sprawdza pełną tabelę dla obu ścieżek.

### Krok 1: rozplecenie osi (wspólny)

**W IDE:** zauważ, że tabela to dwie niezależne reguły. ⌃⇧R > Extract to method in class: `basePrice(format)` ze `switch` po formacie i `adjustForDay(day, base)` ze `switch` po dniu (w porcie obie `private static`). `price` = złożenie.
**Po:**

```typescript
price(day: DayOfWeek, format: string): Money {
  return ShowPricing.adjustForDay(day, ShowPricing.basePrice(format));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m6/s20` - zielone (9 cen i nieznany format).
**Co powiedzieć:** to jest moment decyzji. Ten sam `switch` może sygnalizować różne problemy - pytamy, **która oś zmienia się częściej**.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s20 0 1`

### Krok 2: ścieżka A - zmienia się reguła dnia (Strategy)

**W IDE:** typ `DayPolicy = (base: Money) => Money`, stałe `CHEAP_TUESDAY`, `WEEKEND`, `REGULAR` i kalendarz `DayPolicies.standard(day)`. `ShowPricing` dostaje kalendarz w konstruktorze (parametr domyślny - kalendarz standardowy). Format zostaje prostym `switch`.
**Po:**

```typescript
return this.calendar(day)(ShowPricing.basePrice(format));
```

**Uruchom:** test zielony; `pathAMakesANewDayCampaignCheap` dodaje "środę seniora" bez dotykania formatów.
**Co powiedzieć:** wybierz tę ścieżkę, gdy marketing co miesiąc zmienia akcje dniowe, a formaty są stabilne. Zestaw polityk jest otwarty i wstrzykiwany.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s20 1 2`

### Krok 3: ścieżka B - zmienia się zestaw formatów (typ z zachowaniem)

**W IDE:** pokaż alternatywę od kroku 1: klasa `Format` ze stałymi instancjami, ceną bazową i metodą `priceOn(day)`, `Format.of(code)` z tym samym wyjątkiem. `ShowPricing` deleguje. Reguła dnia zostaje zwykłym `switch` w typie.
**Po:**

```typescript
price(day: DayOfWeek, format: string): Money {
  return Format.of(format).priceOn(day);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wybierz tę ścieżkę, gdy dochodzą formaty (4DX, ScreenX) z własnymi wyjątkami od reguł, a kalendarz jest stabilny. Porównaj `scripts/warsztat.sh --lang ts diff m6/s20 1 3` z `diff m6/s20 1 2`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m6/s20 1 3` (alternatywa dla kroku 2; `diff 2 3` pokazuje zamianę ścieżki)

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

Sceny s11 i s13 (po 2 kroki) dobrze sprawdzają się jako samodzielna praca uczestników w parach, a s04 i s05 jako jeden blok "Factory" z porównaniem obu refaktoryzacji.
