# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy (TypeScript): przewodnik prowadzącego

Szesnaście małych scen w domenie kina CineLegacy pokazuje każdy ruch z modułu 5: Pull Up / Push Down, Extract Superclass / Subclass / Interface, Collapse Hierarchy, kompozycję zamiast dziedziczenia, a także semantykę języka, która sprawia, że te ruchy bywają niebezpieczne (przeciążenia, ukrywanie pól, konstruktory, wymazywanie typów, hierarchie zamknięte, zgodność z już zbudowanym kodem, serializacja, proxy). Sceny nie powielają studium powiadomień z `typescript/src/module5` ani ćwiczeń z `md/zadania/05-*.md`. Każda scena ma katalog `start` (na nim pracujesz na żywo), kompletne snapshoty `stepN` i testy, które po każdym kroku mają być zielone.

To wersja przewodnika dla portu TypeScript. Kod scen leży w `typescript/src/workshop/m5/sNN_.../{start,step1,...}`, testy w `typescript/test/workshop/m5/sNN_.../`, a pokaz prowadzimy w VS Code. Sceny s01-s08 i s14 są wierne Javie niemal ruch w ruch. Sceny s09-s13, s15 i s16 dotyczą mechanizmów, których TypeScript nie ma albo ma w innej postaci (przeciążenia, ukrywanie pól, prolog konstruktora, metody bridge, `sealed`, zgodność binarna, serializacja Javy) - tam scena pokazuje najbliższy odpowiednik ze świata TS/JS, a akapit **Różnica względem Javy** mówi, co i dlaczego wygląda inaczej.

## Jak prowadzić pokaz

```bash
scripts/warsztat.sh --lang ts list m5              # sceny i kroki modułu 5
scripts/warsztat.sh --lang ts test m5/s01          # testy jednej sceny (albo całego modułu: m5, 238 testów)
scripts/warsztat.sh --lang ts diff m5/s01 0 1      # co zmienia krok 1 względem start
scripts/warsztat.sh --lang ts diff m5/s01 0 1 --word  # to samo, zmiany podświetlone w obrębie linii
scripts/warsztat.sh --lang ts jump m5/s01 2        # nie zdążyłeś? start = snapshot step2
scripts/warsztat.sh --lang ts next m5/s01          # następny krok do start + podsumowanie zmian
scripts/warsztat.sh next                           # kolejny krok tej samej sceny (język i scena zapamiętane)
scripts/warsztat.sh prev                           # krok wstecz
scripts/warsztat.sh status                         # który krok jest teraz w start
scripts/warsztat.sh --lang ts reset m5/s01         # przywraca start z repozytorium
```

- Zasada: **jeden krok - jeden test - jedno zdanie komentarza**. Po każdym ruchu w VS Code uruchamiasz test sceny (skrypt albo przycisk Run przy teście w rozszerzeniu Vitest), zanim cokolwiek powiesz.
- **Krok po kroku bez numerów:** `scripts/warsztat.sh --lang ts next m5/sNN` wstawia do `start` gotowy następny krok i wypisuje, co się zmieniło. `prev` cofa o krok, `status` mówi, który krok jest teraz w `start`. Skrypt pamięta język i ostatnią scenę, więc po pierwszym `next` ze sceną wystarczy samo `next`. Ręczne zmiany w `start` zostają nadpisane - o to chodzi, gdy krok na żywo się rozjechał.
- Test `SNNEquivalenceTest` sprawdza, że start i wszystkie kroki zachowują się tak samo. `SNNSolutionTest` pokazuje to, czego równoważność nie widzi: klasę deklarującą członka, klasę w czasie działania, pułapkę semantyki, zgodność z już zbudowanym kodem.
- Sceny o pułapkach (s08-s11, s13) mają w `SNNSolutionTest` testy o nazwie `start...`, które **dokumentują pułapkę w start**. Gdy naprawisz start na żywo, te testy zrobią się czerwone - to jest dowód, że pułapka zniknęła. Powiedz to sali przed pierwszym krokiem. `reset` przywraca stan wyjściowy.
- Pozostałe testy strukturalne sprawdzają snapshoty `stepN`, więc praca na `start` ich nie psuje.
- **Refleksja w TypeScripcie.** Javowe `getDeclaredMethods()`, `getSuperclass()` czy `Modifier` mają w teście dwa odpowiedniki (pomocnik `typescript/test/workshop/m5/reflection.ts`). To, co istnieje w czasie działania, sprawdzamy na prototypach (`Object.getOwnPropertyNames(Klasa.prototype)`, `Object.getPrototypeOf(Klasa)`). To, co kompilator wymazuje (`abstract`, `readonly`, `static`, pola `#`, typy pól), czytamy z drzewa składni pliku sceny przez API kompilatora TypeScript. Sceny s09, s13, s15 i s16 dodatkowo kompilują w pamięci mały fragment kodu i sprawdzają kod błędu (np. TS2416, TS2322) - to test tego, co zobaczyłby `tsc`.
- **Test to nie kompilacja.** Vitest uruchamia kod bez sprawdzania typów. Tam, gdzie scena opiera się na kontroli kompilatora (s09, s12, s13, s15, s16), uruchom też `npm run typecheck` w katalogu `typescript` albo pokaż podkreślenia w VS Code.
- Konwencje portu w całym module: pola prywatne to pola ES `#nazwa` (prywatne także w czasie działania, dzięki temu pole `#seat` i akcesor `seat()` mogą mieć tę samą nazwę jak w Javie), `final` pola to `readonly`, a `@Override` to słowo kluczowe `override` (wymuszane przez `noImplicitOverride`). TypeScript nie ma metod ani klas `final`: tam, gdzie Java dodaje `final`, snapshot ma komentarz "w Javie final", a test sprawdza sens (metoda jest na prototypie bazy i żadna podklasa jej nie deklaruje).
- Refaktoryzacje w VS Code: zaznaczenie i ⌃⇧R (Refactor...) daje Extract to method / Extract to function / Extract to constant, F2 to Rename Symbol, ⌘. (Quick Fix) m.in. Implement interface i Add missing member, a Move to a new file / Move to file przenosi deklaracje najwyższego poziomu. **Pull Members Up, Push Members Down, Extract Superclass, Extract Subclass, Extract Interface, Change Signature ani Safe Delete nie ma** - wszystkie ruchy na hierarchii robimy ręcznie (wytnij, wklej, popraw `super(...)` i `override`), a listę miejsc do poprawy daje `npm run typecheck` (albo Find All References ⇧⌥F12). Właśnie dlatego w tym module test po każdym ruchu jest ważniejszy niż zwykle.

## Mapa scen

| Scena | Temat ze slajdów | Kroki | Katalog | Czas |
| --- | --- | --- | --- | --- |
| s01 | Pull Up Method (3.1-3.3) | 3 | `m5/s01_pullupmethod` | ~12 min |
| s02 | Pull Up Field (3.4-3.6) | 3 | `m5/s02_pullupfield` | ~10 min |
| s03 | Push Down Method/Field, asymetria przesunięć (4.1-4.4) | 3 | `m5/s03_pushdown` | ~12 min |
| s04 | Extract Superclass, konstruktory i fabryki (5) | 3 | `m5/s04_extractsuperclass` | ~15 min |
| s05 | Extract Subclass (6) | 4 | `m5/s05_extractsubclass` | ~15 min |
| s06 | Extract Interface, metody domyślne (7.1-7.4) | 3 | `m5/s06_extractinterface` | ~12 min |
| s07 | Collapse Hierarchy (8) | 3 | `m5/s07_collapsehierarchy` | ~8 min |
| s08 | Replace Inheritance with Composition, self-use, pułapki delegowania (9.1-9.4) | 2 | `m5/s08_composition` | ~12 min |
| s09 | Overriding a overloading (2.1-2.2) - w TS: warianty po typie i biwariancja | 2 | `m5/s09_overloading` | ~10 min |
| s10 | Ukrywanie pól i metod static (2.3) - w TS: pola `#` | 2 | `m5/s10_fieldhiding` | ~8 min |
| s11 | Konstruktor wołający override (2.4) | 2 | `m5/s11_constructorcall` | ~8 min |
| s12 | Generyki i metody bridge (2.7, 10.2) - w TS: wymazywanie typów i biwariancja | 2 | `m5/s12_bridgemethods` | ~10 min |
| s13 | Hierarchie zamknięte (unia dyskryminowana), wyczerpujący `switch`, `assertNever` (2.8) | 3 | `m5/s13_sealed` | ~12 min |
| s14 | Współdzielenie implementacji a podtypowanie (1.1-1.2, 9.1) | 2 | `m5/s14_reuse` | ~10 min |
| s15 | Zgodność zbudowanego kodu, refleksja i metadane (1.3, 10.2-10.3) | 4 | `m5/s15_compatibility` | ~15 min |
| s16 | Serializacja (JSON), proxy, DI/ORM (10.4-10.6) | 3 | `m5/s16_serializationproxy` | ~12 min |

## Scena s01. Pull Up Method - najpierw ujednolicić ciała

**Temat ze slajdów:** 3.1-3.3. Pull Up Method
**Katalog:** `typescript/src/workshop/m5/s01_pullupmethod` · **Test:** `scripts/warsztat.sh --lang ts test m5/s01` (`typescript/test/workshop/m5/s01_pullupmethod/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Trzy podklasy `Ticket` mają własne `label()`, różne tekstem, a robiące to samo. Najpierw ujednolicamy ciała, potem wciągamy do bazy abstrakcyjne `price()` jako punkt rozszerzenia, a na końcu jedno wspólne `label()`.

**Zasada:** Pull Up Method przenosi metodę na najniższy poziom hierarchii, na którym jest prawdziwa dla wszystkich potomków. Porównujemy kontrakty, a nie tekst: identyczne ciało nie gwarantuje tego samego zachowania, a różny tekst może kodować ten sam kontrakt.

**Efekt:** Podklasy zawierają tylko regułę ceny, a `BoxOffice` pracuje na typie bazowym. W Javie kosztem jest `final` na `label()`; w TypeScripcie `final` nie istnieje, więc regułę "żadna podklasa nie deklaruje `label()`" pilnuje test, a nie kompilator.

**Różnica względem Javy:** trzy "różne teksty, to samo zachowanie" to w porcie operator `+`, `util.format('%s: %s', ...)` z `node:util` (odpowiednik `String.format`) i `[...].join('')` (odpowiednik `StringBuilder`). TypeScript nie ma wyrażenia `switch`, więc `BoxOffice` w kroku 3 przypisuje bilet do zmiennej `let ticket: Ticket` w zwykłym `switch`. Metod `final` nie ma - `step3/Ticket.ts` ma komentarz, a `S01SolutionTest` sprawdza, że `label` leży tylko na prototypie `Ticket`.

### Co widzimy

Trzy bilety (`StandardTicket`, `StudentTicket`, `VipTicket`) mają wspólną nadklasę `Ticket`, ale każdy deklaruje własne `label()`. Ciała różnią się tekstem, a robią to samo:

```typescript
return this.title() + ': ' + this.price();                 // StandardTicket
return format('%s: %s', this.title(), this.price());       // StudentTicket
return [this.title(), ': ', this.price()].join('');        // VipTicket
```

VS Code nie ma Pull Members Up, a nawet w IDE, które go ma, ruch nie przejdzie dla metod, które nie są identyczne. Do tego `label()` woła `price()`, którego baza nie zna. Klient `BoxOffice` musi znać konkretne klasy.

### Krok 1: Ujednolicenie ciał metod

**W IDE:** ręcznie zmień ciało `label()` w `StudentTicket` i `VipTicket` na wersję z `StandardTicket`. Możesz skopiować i wkleić, ale porównaj wynik z kontraktem, nie z tekstem: `%s` w `util.format`, konkatenacja i `join('')` dają to samo, bo wszystkie zamieniają `Money` na tekst przez `toString()`. Import `format` z `node:util` przestaje być potrzebny (Quick Fix ⌘. > Remove unused declaration albo Organize Imports ⇧⌥O).
**Po:**

```typescript
label(): string {
  return this.title() + ': ' + this.price();
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s01` - 15 testów zielonych.
**Co powiedzieć:** Pull Up wymaga identycznego ciała, więc najpierw robimy z trzech wersji jedną. To osobny, bezpieczny krok, a test pilnuje, że format się nie zmienił.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 0 1`

### Krok 2: Pull Up price() jako metoda abstrakcyjna

**W IDE:** ręcznie dopisz w `Ticket` deklarację `abstract price(): Money;` (klasa już jest `abstract`). W trzech podklasach dodaj `override` przed `price()` - bez niego `noImplicitOverride` zgłosi błąd, tak jak Java podpowiada `@Override`.
**Po:**

```typescript
export abstract class Ticket {
  ...
  abstract price(): Money;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** ciała `price()` są różne, więc do bazy idzie tylko deklaracja. To punkt rozszerzenia, dzięki któremu wspólne `label()` będzie mogło zamieszkać w bazie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 1 2`

### Krok 3: Pull Up label() i klient na typie bazowym

**W IDE:** wytnij `label()` z `StandardTicket` i wklej do `Ticket`, a identyczne kopie usuń z `StudentTicket` i `VipTicket` (kompilator nic nie zgłosi - dlatego uruchamiamy test). W `BoxOffice` zamień `switch` z trzema `return ...label()` na zmienną `let ticket: Ticket` przypisywaną w `case` i jedno wywołanie `ticket.label()` na końcu.
**Po:**

```typescript
// Ticket
label(): string {
  return this.title() + ': ' + this.price();
}
// BoxOffice
let ticket: Ticket;
switch (kind) {
  case 'STUDENT': ticket = new StudentTicket(title, basePrice); break;
  case 'VIP': ticket = new VipTicket(title, basePrice); break;
  default: ticket = new StandardTicket(title, basePrice);
}
return ticket.label();
```

**Uruchom:** test zielony. `S01SolutionTest` sprawdza, że `label` jest na prototypie `Ticket`, żadna podklasa go nie deklaruje (wszystkie dziedziczą tę samą funkcję), a `price` w źródle `Ticket` ma modyfikator `abstract`.
**Co powiedzieć:** metoda trafiła na najniższy poziom, na którym jest prawdziwa dla wszystkich potomków. W Javie `final` chroniłby przed przypadkowym nadpisaniem przez podklasę spoza repozytorium, która miała własne `label()` - jej kompilacja by się wywróciła. W TypeScripcie takiej ochrony nie ma: podklasa z własnym `label()` (z `override`) skompiluje się i po cichu zmieni zachowanie. Zostaje test, przegląd kodu albo reguła lintera.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s01 2 3`

### Rozwiązanie i uzasadnienie

`step3/Ticket.ts` ma abstrakcyjne `price()` i wspólne `label()` (w Javie `final`). Podklasy zawierają tylko to, co naprawdę różne: regułę ceny. Klient tworzy konkretny bilet w jednym miejscu, a dalej pracuje na typie bazowym.

### Pułapki

- Pull Up metody o tej samej sygnaturze, ale innym kontrakcie (np. `label()` jednej podklasy dodaje walutę). Tekst to nie kontrakt.
- Metoda wciągnięta do bazy, która korzysta z pola dostępnego tylko w jednej podklasie - kusi wtedy `protected` pole albo getter "na zapas".
- Zewnętrzna podklasa z metodą o tej samej nazwie staje się nieplanowanym override. W Javie `final` zamienia to w błąd kompilacji; w TS `noImplicitOverride` wymusi przynajmniej słowo `override` - autor podklasy zobaczy, że nadpisuje, ale nikt mu tego nie zabroni.
- Refleksja: `Object.getOwnPropertyNames(StudentTicket.prototype)` przestaje zawierać `label` po Pull Up (scena s15).

### Pytanie do sali

Czy `label()` powinno być "finalne" (w TS: chronione testem albo regułą), jeśli za rok pojawi się bilet z etykietą w innym formacie? Co wtedy zrobicie?

## Scena s02. Pull Up Field - to samo znaczenie, typ i cykl życia

**Temat ze slajdów:** 3.4-3.6. Pull Up Field i ryzyka Pull Up
**Katalog:** `typescript/src/workshop/m5/s02_pullupfield` · **Test:** `scripts/warsztat.sh --lang ts test m5/s02` (`typescript/test/workshop/m5/s02_pullupfield/`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Każdy bilet trzyma miejsce na sali po swojemu: inna nazwa pola, setter zamiast konstruktora, normalizacja w VIP. Wyrównujemy nazwę i cykl życia pól, a dopiero potem przenosimy jedno pole `#seat` do `Ticket`.

**Zasada:** Pull Up Field łączy pola tylko wtedy, gdy mają to samo znaczenie, typ, cykl życia, walidację i moment inicjalizacji. Pole w bazie jest prywatne i ustawiane przez `super(...)`, a nie surowe `protected`. Ten sam typ i podobna nazwa to za mało - dlatego `#studentId` zostaje.

**Efekt:** Stan miejsca ma jedno źródło, a normalizacja VIP została w podklasie, która przekazuje bazie gotową wartość. Znika setter, więc miejsce biletu normalnego jest teraz niemutowalne.

**Różnica względem Javy:** pola to pola prywatne ES (`#seat`, `#seatCode`, `#studentId`), a `final` to `readonly`. Pola `#` są niewidoczne z zewnątrz także w czasie działania, więc `S02SolutionTest` czyta ich deklaracje i modyfikatory ze źródła sceny. `#studentId` ma typ `string | null`, bo test (jak w Javie) podaje `null` dla biletów niestudenckich. `Locale.ROOT` w `toUpperCase` odpada - `toUpperCase()` w JS nie zależy od locale.

### Co widzimy

Każdy bilet przechowuje miejsce na sali, ale każdy po swojemu: `StandardTicket` ma mutowalne `#seat: string | undefined` ustawiane setterem, `StudentTicket` ma `#seatCode`, a `VipTicket` normalizuje wartość do wielkich liter. `#studentId` to zupełnie inne pojęcie.

```typescript
const ticket = new StandardTicket();   // klient w BoxOffice
ticket.setSeat(seat);
```

### Krok 1: Rename seatCode na seat

**W IDE:** w `StudentTicket` na polu `#seatCode` Rename Symbol F2 -> `#seat`, na parametrze konstruktora F2 -> `seat`, na akcesorze `seatCode()` F2 -> `seat()` (Rename Symbol zmienia każdy symbol osobno, więc to trzy ruchy).
**Po:**

```typescript
readonly #seat: string;
seat(): string {
  return this.#seat;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s02` - 15 testów zielonych.
**Co powiedzieć:** to samo znaczenie musi mieć tę samą nazwę, inaczej nie połączymy pól. Rename jest bezpieczny, bo nikt spoza klasy nie używał `seatCode()`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 0 1`

### Krok 2: Ujednolicenie cyklu życia (konstruktor zamiast settera)

**W IDE:** ręcznie: dodaj w `StandardTicket` konstruktor `constructor(seat: string)` z `super()`, oznacz pole `readonly` i zmień jego typ na `string`, usuń `setSeat` (najpierw Find All References ⇧⌥F12 - jedyne użycie jest w `BoxOffice`) i popraw klienta na `new StandardTicket(seat)`.
**Po:**

```typescript
readonly #seat: string;

constructor(seat: string) {
  super();
  this.#seat = seat;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** pole mutowalne i pole `readonly` to dwa różne cykle życia. Gdybyśmy wciągnęli je do bazy teraz, jeden z biletów zmieniłby semantykę. Dopiero po tym kroku trzy pola mają ten sam typ, znaczenie, walidację i moment inicjalizacji.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 1 2`

### Krok 3: Pull Up Field seat i akcesora seat()

**W IDE:** ręcznie przenieś pole `readonly #seat: string` i metodę `seat()` do `Ticket`, dodaj konstruktor `protected constructor(seat: string)` i usuń duplikaty z trzech podklas. W podklasach zamień `super()` + przypisanie na `super(seat)`, a odwołania `this.#seat` na `this.seat()` (pole `#` bazy jest niewidoczne w podklasie - kompilator wskaże każde miejsce). W `VipTicket`: `super(seat.toUpperCase())`.
**Po:**

```typescript
export abstract class Ticket {
  readonly #seat: string;

  protected constructor(seat: string) {
    this.#seat = seat;
  }

  seat(): string {
    return this.#seat;
  }

  abstract describe(): string;
}
```

**Uruchom:** test zielony. `S02SolutionTest` sprawdza w źródle, że `#seat` jest zadeklarowane raz, w `Ticket`, jako `readonly`, a `#studentId` zostało w `StudentTicket`.
**Co powiedzieć:** pole w bazie jest prywatne i ustawiane przez `super(...)`, nie `protected`. Pola `#` wymuszają to same: podklasa w ogóle nie widzi `#seat` bazy, tylko akcesor. Reguła normalizacji VIP została w podklasie - baza dostaje już gotową wartość.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s02 2 3`

### Rozwiązanie i uzasadnienie

Jedno źródło stanu miejsca, bez surowego `protected`. Pola o innym znaczeniu (`#studentId`) nie są przenoszone tylko dlatego, że mają ten sam typ.

### Pułapki

- Przy ręcznym przenoszeniu kusi `protected seat` - "bo podklasy go czytają". Zostaw pole prywatne (`#`) i daj akcesor.
- Pola `static` o tej samej nazwie po Pull Up scalają dwa niezależne stany (np. liczniki numerów biletów).
- Refleksja, mappery i serializacja (JSON) zobaczą pole w innym miejscu albo pod inną nazwą (scena s16).
- Pole wciągnięte w górę, choć w bazie i podklasie zostawiono deklaracje `#`, to dwa sloty (scena s10).

### Pytanie do sali

Gdyby `VipTicket` normalizował miejsce w getterze, a nie w konstruktorze, czy Pull Up Field nadal byłby bezpieczny?

## Scena s03. Push Down Method/Field i asymetria przesunięć

**Temat ze slajdów:** 4.1-4.4. Push Down Method, Push Down Field i asymetria przesunięć
**Katalog:** `typescript/src/workshop/m5/s03_pushdown` · **Test:** `scripts/warsztat.sh --lang ts test m5/s03` (`typescript/test/workshop/m5/s03_pushdown/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Ticket` obiecuje `upgradeToVip()` wszystkim biletom, a bilet studencki odmawia wyjątkiem. Przestawiamy klientów na podtyp, przenosimy zachowanie za hak `surcharge()`, a na końcu spychamy pole i operację VIP do `StandardTicket`.

**Zasada:** Push Down zawęża zbyt szeroki kontrakt bazy do gałęzi, która naprawdę potrzebuje członka. Sygnały to `UnsupportedOperationError`, flagi bez znaczenia i wywołania po `instanceof`. Kolejność: klienci, potem zachowanie korzystające z pola, na końcu pole.

**Efekt:** Baza obiecuje tylko to, co prawdziwe dla wszystkich biletów, a `StudentTicket` nie musi niczego odmawiać. W bibliotece to zmiana łamiąca: już zbudowany kod JS wołający `upgradeToVip` na obiekcie typu bazowego dostanie `TypeError`, bo Pull Up i Push Down nie są symetryczne.

**Różnica względem Javy:** odpowiednikiem `getMethod(...).getDeclaringClass()` jest w teście pomocnik `declaringClassOf`, który idzie łańcuchem prototypów w górę - to samo wyszukiwanie, które wykonuje wywołanie metody w JS. Asymetria jest więc identyczna: metoda przeniesiona w górę jest dalej znajdowana, przeniesiona w dół - nie. Zamiast `NoSuchMethodError` starego `.class` stary klient JS dostanie `TypeError: ticket.upgradeToVip is not a function`, a klient TS przy przebudowie - błąd kompilacji.

### Co widzimy

`Ticket` obiecuje `upgradeToVip()` wszystkim biletom, ale bilet studencki odrzuca dopłatę wyjątkiem. Klient sprawdza `instanceof`, zanim wywoła metodę. Klasyczne sygnały zbyt szerokiego kontraktu.

```typescript
override upgradeToVip(): void {
  throw new UnsupportedOperationError('bilet ulgowy nie ma dopłaty VIP');
}
```

### Krok 1: Klienci na podtyp

**W IDE:** w `BoxOffice` rozdziel gałęzie ręcznie: dla studenta zwróć cenę od razu, dla biletu normalnego zadeklaruj zmienną typu `StandardTicket`. Import typu `Ticket` przestaje być potrzebny.
**Po:**

```typescript
if (kind === 'STUDENT') {
  return new StudentTicket(basePrice).price();
}
const ticket: StandardTicket = new StandardTicket(basePrice);
if (vip) {
  ticket.upgradeToVip();
}
return ticket.price();
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s03` - 19 testów zielonych.
**Co powiedzieć:** zanim przesuniemy metodę w dół, nikt nie może jej wołać przez typ bazowy. Find All References ⇧⌥F12 na `Ticket.upgradeToVip` ma pokazać tylko wywołania na `StandardTicket` (i override w `StudentTicket`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 0 1`

### Krok 2: Najpierw zachowanie - hak surcharge()

**W IDE:** w `Ticket.price()` wydziel dopłatę do metody `protected surcharge(): Money`, która w bazie zwraca `Money.ZERO` (⌃⇧R > Extract to method in class 'Ticket' na wyrażeniu, potem ręcznie uprość ciało). W `StandardTicket` dopisz `protected override surcharge()` z logiką VIP (Quick Fix ⌘. nie generuje override - piszemy ręcznie).
**Po:**

```typescript
// Ticket
price(): Money {
  return this.#basePrice.minus(this.#basePrice.percent(this.discountPercent())).plus(this.surcharge());
}
// StandardTicket
protected override surcharge(): Money {
  return this.isVipUpgraded() ? Money.of('10.00') : Money.ZERO;
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** baza nie czyta już pola `#vipUpgraded` w `price()`. Najpierw przenosimy zachowanie, które korzysta z pola, potem samo pole - inaczej Push Down się nie skompiluje albo zostawi dwa sloty.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 1 2`

### Krok 3: Push Members Down

**W IDE:** ręcznie wytnij z `Ticket` pole `#vipUpgraded` i metody `upgradeToVip()`, `isVipUpgraded()` i wklej je do `StandardTicket`. Potem usuń override rzucający wyjątek w `StudentTicket` - bez metody w bazie `override` i tak dałby błąd kompilacji (TS4113: metoda nie istnieje w klasie bazowej), więc kompilator przypilnuje, że niczego nie zostawisz.
**Po:**

```typescript
export class StandardTicket extends Ticket {
  #vipUpgraded = false;
  ...
  upgradeToVip(): void {
    this.#vipUpgraded = true;
  }
  ...
}
```

**Uruchom:** test zielony. `S03SolutionTest` pokazuje, że `declaringClassOf(Ticket, 'upgradeToVip')` zwraca `undefined` (odpowiednik `NoSuchMethodException`), a pola `#vipUpgraded` nie ma już w źródle `Ticket`.
**Co powiedzieć:** baza obiecuje tylko to, co prawdziwe dla wszystkich biletów. W bibliotece to zmiana łamiąca: już zbudowany JavaScript klienta, który woła `ticket.upgradeToVip()` na bilecie studenckim albo na obiekcie typu bazowego, dostanie `TypeError ... is not a function`.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s03 2 3`

### Rozwiązanie i uzasadnienie

Stan i operacja VIP żyją w `StandardTicket`. `StudentTicket` nie musi niczego odmawiać. Kontrakt bazy jest węższy, ale uczciwy.

### Pułapki

- **Asymetria:** Pull Up jest zgodny dla wywołań (JS szuka metody w łańcuchu prototypów w górę, test `beforePushDownSubclassFindsMethodInSuperclass`), Push Down nie jest (nikt nie szuka w podklasach).
- Kilka podklas potrzebuje metody - przesuwamy do najbliższego wspólnego przodka, nie kopiujemy do liści.
- Zostawienie pola w bazie i skopiowanie go do podklasy daje dwa niezależne sloty (przy polach `#` - dosłownie dwa, scena s10).

### Pytanie do sali

Jak przeprowadzić ten Push Down w opublikowanej bibliotece (pakiecie npm), której klientów nie możecie przebudować?

## Scena s04. Extract Superclass - seans i wynajem sali

**Temat ze slajdów:** 5. Extract Superclass
**Katalog:** `typescript/src/workshop/m5/s04_extractsuperclass` · **Test:** `scripts/warsztat.sh --lang ts test m5/s04` (`typescript/test/workshop/m5/s04_extractsuperclass/`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** Seans i wynajem sali nie mają wspólnego typu, więc `HallPlanner` ma trzy kopie warunku kolizji. Wydzielamy abstrakcyjną nadklasę `HallBooking`, dołączamy do niej klasy pojedynczo i przenosimy tam jeden algorytm `overlaps`.

**Zasada:** Extract Superclass ma sens, gdy klasy są wariantami jednego pojęcia ze wspólnym kontraktem - samo podobieństwo linii kodu to za mało. Nazwa nadklasy opisuje pojęcie domenowe, a nie `Base` czy `Common`. Publiczne sygnatury konstruktorów i fabryki zachowujemy świadomie.

**Efekt:** `HallPlanner` ma jedną pętlę, a klienci nie zauważyli zmiany, bo `conflicts(...)`, konstruktory i `rental(...)` zostały. Nowy poziom zmienia jednak prototyp klasy (`Object.getPrototypeOf(PrivateEvent)`) i klasę deklarującą akcesorów, co może mieć znaczenie dla refleksji i mapperów.

**Różnica względem Javy:** w TypeScripcie konstruktory **są** dziedziczone: podklasa bez własnego konstruktora dostałaby konstruktor bazy, a przy `protected constructor` w bazie - także jego widoczność. Dlatego `PrivateEvent` zachowuje świadomie własny publiczny konstruktor `(client, hall, start, minutes)` - sens ten sam co w Javie, powód inny. `getSuperclass()` to w teście `Object.getPrototypeOf(Klasa)` (klasa bez `extends` daje `undefined` zamiast `Object.class`), a `abstract` klasy test czyta ze źródła.

### Co widzimy

`Screening` (seans) i `PrivateEvent` (wynajem sali na firmową imprezę) mają te same pojęcia: sala, początek, czas trwania, koniec. Nie mają wspólnego typu, więc `HallPlanner` szuka kolizji w trzech pętlach z trzema kopiami warunku.

```typescript
if (a.hall() === b.hall()
    && a.start().isBefore(b.end()) && b.start().isBefore(a.end())) {
```

Oba typy są wariantami jednego pojęcia - rezerwacji sali. To jest właściwy sygnał do Extract Superclass, a nie samo podobieństwo linii kodu.

### Krok 1: Extract Superclass z Screening

**W IDE:** VS Code nie ma Extract Superclass, więc ręcznie: nowy plik `HallBooking.ts` z `export abstract class HallBooking`, przenieś z `Screening` pola `#hall`, `#start`, `#minutes` i metody `hall()`, `start()`, `end()`, dodaj `protected constructor(hall, start, minutes)`. W `Screening` dopisz `extends HallBooking` i `super(hall, start, minutes)` na początku konstruktora. Pola w bazie zostają prywatne (`#`).
**Po:**

```typescript
export abstract class HallBooking {
  readonly #hall: string;
  readonly #start: LocalDateTime;
  readonly #minutes: number;

  protected constructor(hall: string, start: LocalDateTime, minutes: number) { ... }
  ...
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s04` - 19 testów zielonych.
**Co powiedzieć:** nazwa opisuje pojęcie domenowe. `BaseScreening` albo `AbstractCommon` zdradzałyby, że wydzielamy tylko z powodu duplikacji. Publiczny konstruktor `Screening` się nie zmienił.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 0 1`

### Krok 2: PrivateEvent dołącza do hierarchii

**W IDE:** w `PrivateEvent` dopisz `extends HallBooking`, zamień przypisania pól na `super(hall, start, minutes)`, usuń zduplikowane pola i akcesory (po dopisaniu `extends` kompilator nic nie zgłosi, bo akcesory podklasy po prostu przesłoniłyby bazowe - usuń je świadomie, z testem).
**Po:**

```typescript
export class PrivateEvent extends HallBooking {
  readonly #client: string;

  constructor(client: string, hall: string, start: LocalDateTime, minutes: number) {
    super(hall, start, minutes);
    this.#client = client;
  }

  static rental(client: string, hall: string, start: LocalDateTime): PrivateEvent {
    return new PrivateEvent(client, hall, start, 120);
  }
```

**Uruchom:** test zielony.
**Co powiedzieć:** dołączamy klasy pojedynczo, z testem po każdej. Bez własnego konstruktora podklasa odziedziczyłaby chroniony konstruktor bazy `(hall, start, minutes)`, więc publiczną sygnaturę i fabrykę `rental(...)` zachowujemy świadomie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 1 2`

### Krok 3: Wspólny algorytm w nadklasie

**W IDE:** w `HallPlanner` zaznacz warunek kolizji i ⌃⇧R > Extract to method, potem ręcznie przenieś metodę do `HallBooking` jako `overlaps(other: HallBooking)` (Move Method między klasami w VS Code nie ma). Dodaj w obu podklasach `name()`, a w bazie `abstract name(): string` (w podklasach `override`). Zastąp trzy pętle jedną po `HallBooking[]`.
**Po:**

```typescript
// HallBooking
overlaps(other: HallBooking): boolean {
  return this.#hall === other.#hall && this.#start.isBefore(other.end()) && other.#start.isBefore(this.end());
}
// HallPlanner
const bookings: HallBooking[] = [...screenings, ...events];
...
if (a.overlaps(b)) {
  result.push(a.name() + ' x ' + b.name());
}
```

**Uruchom:** test zielony, w tym przypadek graniczny "koniec 20:00 i start 20:00 to nie konflikt".
**Co powiedzieć:** dopiero wspólny typ pozwolił usunąć trzy kopie algorytmu. `overlaps` czyta `other.#hall` - pola `#` są dostępne na innych instancjach tej samej klasy, jak `other.hall` w Javie. Publiczna sygnatura `conflicts(screenings, events)` została - klienci niczego nie zauważyli.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s04 2 3`

### Rozwiązanie i uzasadnienie

`HallBooking` jest abstrakcyjna, ma prywatny stan i wspólne `overlaps` (w Javie `final`). Podklasy dostarczają tylko nazwę. `S04SolutionTest` sprawdza nowy nadtyp (`superclassOf`), klasę deklarującą `hall` i symetrię `overlaps` między seansem a wynajmem.

### Pułapki

- Nowy poziom zmienia prototyp klasy, klasę deklarującą akcesorów i założenia mapperów (ORM, JSON). Encja z nową nadklasą to migracja modelu, nie refaktoryzacja.
- Przy ręcznym przenoszeniu łatwo zrobić pola `protected` "dla wygody" - to zaproszenie do omijania walidacji.
- Przeniesienie całego `Screening` naraz zamiast dołączania klas pojedynczo utrudnia znalezienie błędu.

### Pytanie do sali

Czy `HallBooking` powinna być klasą abstrakcyjną, czy interfejsem i osobną funkcją `overlaps(a, b)`? Co przemawia za każdą opcją?

## Scena s05. Extract Subclass - premiera z gościem

**Temat ze slajdów:** 6. Extract Subclass
**Katalog:** `typescript/src/workshop/m5/s05_extractsubclass` · **Test:** `scripts/warsztat.sh --lang ts test m5/s05` (`typescript/test/workshop/m5/s05_extractsubclass/`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** `Screening` ma flagę `#premiere` i pole `#guest`, które dla zwykłego seansu jest zawsze `null`, a cena i opis powtarzają `if (premiere)`. Najpierw wprowadzamy fabryki, potem podklasę `PremiereScreening`, przenosimy do niej zachowanie i stan, a na końcu usuwamy flagę.

**Zasada:** Extract Subclass tworzy podklasę dla stabilnego podzbioru instancji, który ma dodatkowy stan lub zachowanie. Wariant musi być stały przez całe życie obiektu - rola zmienna w czasie to State lub Strategy, a wiele niezależnych osi to kompozycja.

**Efekt:** Nie ma już `if (premiere)` ani pola `null`, a `Programme` nie zmienił się od kroku 1, bo tworzy obiekty przez fabryki. Ruch celowo zmienia klasę obiektów premierowych w czasie działania, co widzą `obj.constructor`, `instanceof`, `equals`, mappery ORM i JSON z polem typu.

**Różnica względem Javy:** od kroku 2 `PremiereScreening` leży w tym samym pliku co `Screening` (`stepN/Screening.ts`), a nie w osobnym `PremiereScreening.ts`. Fabryka `Screening.premiere(...)` tworzy podklasę, a podklasa potrzebuje bazy już przy ładowaniu modułu (`extends`) - dwa pliki dałyby cykl importów ES i `ReferenceError` przy imporcie `Screening` jako pierwszego. TypeScript nie ma `sealed ... permits`: hierarchia jest "zamknięta w module", a test zamiast `getPermittedSubclasses()` sprawdza, że jedyną eksportowaną podklasą `Screening` jest `PremiereScreening`. Konstruktor pakietowy `PremiereScreening` jest publiczny (TS nie ma widoczności pakietowej), `getClass()` to `obj.constructor`, a stała `PREMIERE_SURCHARGE` to `static readonly #PREMIERE_SURCHARGE`.

### Co widzimy

`Screening` ma flagę `#premiere` i pole `#guest`, które ma sens tylko dla premier (dla zwykłego seansu jest `null`). Cena i opis powtarzają `if (premiere)`. Premiera to stały wariant w całym życiu obiektu - kandydat na podklasę, nie na State.

```typescript
constructor(title: string, format: string, premiere: boolean, guest: string | null)
...
return this.#premiere ? base.plus(Money.of('15.00')) : base;
```

Reguła sceny: premiera kosztuje 15.00 więcej niż zwykły seans w tym samym formacie.

### Krok 1: Replace Constructor with Factory Method

**W IDE:** VS Code nie ma Replace Constructor with Factory Method, więc ręcznie: dodaj statyczne fabryki `regular(title, format)` i `premiere(title, format, guest)`, konstruktor oznacz `private`, a w `Programme` wybierz fabrykę w zależności od `guest` (kompilator wskaże wywołanie `new Screening(...)`, które przestało być dostępne).
**Po:**

```typescript
static regular(title: string, format: string): Screening {
  return new Screening(title, format, false, null);
}

static premiere(title: string, format: string, guest: string): Screening {
  return new Screening(title, format, true, guest);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s05` - 18 testów zielonych.
**Co powiedzieć:** najpierw punkty tworzenia. Fabryka to jedyne miejsce, które za chwilę wybierze klasę obiektu, a klient już nie przekazuje flagi i `null`.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 0 1`

### Krok 2: Extract Subclass

**W IDE:** ręcznie. Pod klasą `Screening`, w tym samym pliku, dopisz `export class PremiereScreening extends Screening` z konstruktorem wołającym `super(title, format, true, guest)`. Konstruktor `Screening` zmień z `private` na `protected` (inaczej podklasa go nie wywoła). Fabryka `premiere(...)` zwraca `new PremiereScreening(...)`.
**Po:**

```typescript
export class Screening {
  ...
  protected constructor(title: string, format: string, premiere: boolean, guest: string | null) { ... }

  static premiere(title: string, format: string, guest: string): Screening {
    return new PremiereScreening(title, format, guest);
  }
  ...
}

export class PremiereScreening extends Screening {
  constructor(title: string, format: string, guest: string) {
    super(title, format, true, guest);
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** podklasa jest pusta, logika się nie zmieniła - zmieniła się tylko klasa obiektów premierowych. W Javie `sealed` mówi wprost, że innych wariantów nie ma. W TS tę rolę pełni moduł: obie klasy w jednym pliku, a gdyby ktoś chciał pisać własne podklasy, `protected constructor` i tak mu to umożliwi - zamknięcie jest konwencją, nie gwarancją.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 1 2`

### Krok 3: Push Down - opis i pole guest

**W IDE:** w `PremiereScreening` dopisz `override describe()` i przenieś tam gałąź premierową (`super.describe()` + gość). Następnie ręcznie przenieś pole `#guest` do podklasy (typ `string` - premiera zawsze ma gościa) i usuń parametr `guest` z konstruktora bazy (Change Signature w VS Code nie ma - wywołania wskaże kompilator).
**Po:**

```typescript
export class PremiereScreening extends Screening {
  readonly #guest: string;

  constructor(title: string, format: string, guest: string) {
    super(title, format, true);
    this.#guest = guest;
  }

  override describe(): string {
    return super.describe() + ' - premiera, gość: ' + this.#guest;
  }
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** najpierw zachowanie, potem stan - jak w Push Down ze sceny s03. Zwykły seans nie ma już pola, które zawsze było `null`, a typ `string` zamiast `string | null` mówi to kompilatorowi.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 2 3`

### Krok 4: Usunięcie flagi premiere

**W IDE:** w `PremiereScreening` dopisz `override price()` = `super.price().plus(...)` ze stałą `static readonly #PREMIERE_SURCHARGE = Money.of('15.00')`, w bazie usuń dopłatę z `price()` (a przy okazji zamień `let base` + `switch` na `return` w każdym `case`). Potem ręcznie usuń pole `#premiere` i parametr konstruktora bazy (Find All References ⇧⌥F12 pokaże, że pole czytały już tylko usunięte gałęzie).
**Po:**

```typescript
override price(): Money {
  return super.price().plus(PremiereScreening.#PREMIERE_SURCHARGE);
}
```

**Uruchom:** test zielony. `S05SolutionTest` sprawdza klasę obiektu zwracanego przez fabryki (`constructor`), brak pól `#guest`/`#premiere` w źródle bazy i to, że jedyną eksportowaną podklasą `Screening` jest `PremiereScreening`.
**Co powiedzieć:** żadnego `if (premiere)`. Klient `Programme` nie zmienił się od kroku 1 - dzięki fabrykom cała ekstrakcja była dla niego niewidoczna.
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s05 3 4`

### Rozwiązanie i uzasadnienie

`Screening` zna tylko zwykły seans, `PremiereScreening` dodaje gościa i dopłatę. W Javie hierarchia jest `sealed`, więc wyczerpujący `switch` po seansach jest możliwy; w TS wyczerpujący `switch` wymaga unii dyskryminowanej (scena s13), a hierarchia klas zamknięta w module pozostaje konwencją.

### Pułapki

- Extract Subclass **celowo zmienia klasę obiektu**: `obj.constructor`, `instanceof`, `equals` porównujące `constructor`, dyskryminator ORM, JSON z polem typu.
- Rola zmienna w czasie życia (seans, który "staje się" premierą po ogłoszeniu gościa) to State albo Strategy, nie podklasa.
- Kilka niezależnych osi (premiera, maraton, seans dla szkół) daje eksplozję podklas - wtedy kompozycja.
- Podklasa w osobnym pliku, której baza w fabryce tworzy podklasę, daje cykl importów ES - objaw to `ReferenceError: Cannot access 'Screening' before initialization`, zależny od kolejności importów.

### Pytanie do sali

Kino zaczyna ogłaszać gości tydzień po dodaniu seansu do repertuaru. Czy podklasa nadal jest dobrym modelem?

## Scena s06. Extract Interface - rola koszyka i metoda domyślna

**Temat ze slajdów:** 7.1-7.3. Extract Interface; 7.4. Metody domyślne
**Katalog:** `typescript/src/workshop/m5/s06_extractinterface` · **Test:** `scripts/warsztat.sh --lang ts test m5/s06` (`typescript/test/workshop/m5/s06_extractinterface/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `Cart` ma dwie listy, dwie metody `addTicket`/`addSnack` i dwie kopie liczenia VAT dla biletów i przekąsek. Wydzielamy interfejs roli `Priceable` tylko z tym, czego używa koszyk, przestawiamy na niego klienta i dodajemy wspólne liczenie VAT obok interfejsu - funkcję `vatAmount(item)`.

**Zasada:** Extract Interface wydziela rolę określonej grupy klientów, a nie kopię całego publicznego API klasy. `implements` wymusza sygnatury, nie zachowanie. Wspólne zachowanie dodane do roli (w Javie metoda `default`) musi być poprawne dla każdej implementacji i korzystać wyłącznie z operacji kontraktu.

**Efekt:** Nowy rodzaj pozycji to nowa implementacja bez zmian w koszyku, a VAT liczy się w jednym miejscu. Zmiana `addTicket`/`addSnack` na `add` jest w TS niezgodna już na poziomie źródła - w bibliotece zostawilibyśmy stare metody jako delegujące.

**Różnica względem Javy:** (1) TypeScript nie ma przeciążeń wybieranych po typie parametru (w czasie działania jest jedna metoda o danej nazwie), więc dwa przeciążenia `add(Ticket)`/`add(Snack)` z Javy to w porcie dwie nazwy `addTicket`/`addSnack`. Krok 2 zmienia przez to także wywołujących - w Javie ta zmiana była zgodna źródłowo, a niezgodna binarnie. (2) Interfejs TS nie ma metod `default`, więc `default Money vatAmount()` to funkcja `vatAmount(item: Priceable)` eksportowana obok interfejsu z `step3/Priceable.ts`. Intencja zostaje: kontrakt roli (`price`, `vatPercent`) się nie zmienia, implementacje nic nie robią, a nowa implementacja dostaje VAT za darmo.

### Co widzimy

`Cart` przyjmuje bilety i przekąski z baru, ma dla nich dwie listy, dwie metody dodawania i dwie kopie liczenia VAT (bilety 8%, bar 23%). Koszyk używa z obu klas tylko `price()` i `vatPercent()`.

```typescript
addTicket(ticket: Ticket): void {
  this.#tickets.push(ticket);
}

addSnack(snack: Snack): void {
  this.#snacks.push(snack);
}
```

### Krok 1: Extract Interface z perspektywy klienta

**W IDE:** VS Code nie ma Extract Interface, więc ręcznie: nowy plik `Priceable.ts` z interfejsem zawierającym **tylko** `price(): Money` i `vatPercent(): number`. W `Ticket` i `Snack` dopisz `implements Priceable` - kompilator od razu sprawdzi, że obie klasy spełniają rolę. Klientów jeszcze nie ruszamy.
**Po:**

```typescript
export interface Priceable {
  price(): Money;

  vatPercent(): number;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s06` - 19 testów zielonych.
**Co powiedzieć:** interfejs to rola jednej grupy klientów, a nie kopia publicznego API. `title()`, `seat()` i `name()` koszyka nie obchodzą. W TS `implements` nie jest nawet potrzebne, żeby obiekt pasował do roli (typowanie strukturalne) - dopisujemy je, bo zamienia "przypadkowo pasuje" w "deklaruje, że spełnia", a kompilator sprawdza to w miejscu deklaracji.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 0 1`

### Krok 2: Klient na rolę

**W IDE:** w `Cart` zastąp dwie listy jedną `readonly #items: Priceable[]`, dwie metody jedną `add(item: Priceable)`, dwie pętle jedną. Wywołania `addTicket`/`addSnack` poprawiasz ręcznie - listę daje Find All References albo `npm run typecheck`.
**Po:**

```typescript
readonly #items: Priceable[] = [];

add(item: Priceable): void {
  this.#items.push(item);
}
```

**Uruchom:** test zielony. Wariant `start` w `S06EquivalenceTest` woła koszyk przez pomocnika `addTo`, który używa `addTicket`/`addSnack`, a gdy ich nie ma - `add`, więc przeżywa ten krok na żywo. Zwykły klient nie ma takiego pomocnika: pokaż sali, że `npm run typecheck` wskazuje każde wywołanie `addTicket`/`addSnack` do poprawy.
**Co powiedzieć:** w Javie kod klienta `cart.add(ticket)` kompilował się dalej, a pękała tylko zgodność binarna (deskryptor `add(LTicket;)V`). W TS zmieniła się nazwa, więc pęka już źródło, a stary zbudowany JavaScript dostanie `TypeError`. W bibliotece zostawilibyśmy `addTicket`/`addSnack` jako przestarzałe metody delegujące do `add` (scena s15).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 1 2`

### Krok 3: Wspólne vatAmount() obok roli

**W IDE:** w `Cart` zaznacz wyrażenie liczące VAT pozycji i ⌃⇧R > Extract to function in module scope, nazwa `vatAmount`, parametr `item: Priceable`. Potem ⌃⇧R > Move to file i wskaż `Priceable.ts` (albo przenieś ręcznie), a w `Cart` zaimportuj `vatAmount`. Implementacji nie dotykasz.
**Po:**

```typescript
/** Kwota VAT zawarta w cenie brutto: brutto * stawka / (100 + stawka). */
export function vatAmount(item: Priceable): Money {
  const rate = new Decimal(item.vatPercent());
  return new Money(item.price().amount.times(rate)
    .dividedBy(rate.plus(100)).toDecimalPlaces(2, Decimal.ROUND_HALF_UP));
}
```

**Uruchom:** test zielony. `S06SolutionTest` sprawdza w źródle, że interfejs ma nadal tylko `price` i `vatPercent`, i dodaje nową implementację (okulary 3D, `Glasses3D`) spoza pierwotnej hierarchii, która dostaje `vatAmount` za darmo.
**Co powiedzieć:** funkcja używa wyłącznie operacji kontraktu, więc jest poprawna dla każdej implementacji. Gdybyśmy zamiast tego dopisali do interfejsu wymaganą metodę `vatAmount(): Money`, każda klasa z `implements Priceable` przestałaby się kompilować - to odpowiednik `AbstractMethodError` w starych binariach Javy, tylko wykryty wcześniej.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s06 2 3`

### Rozwiązanie i uzasadnienie

`Cart` zależy tylko od `Priceable`. Nowy rodzaj pozycji (okulary 3D, voucher) to nowa implementacja bez zmian w koszyku.

### Pułapki

- `implements` wymusza sygnatury, nie zachowanie. Wszystkie implementacje powinny przejść ten sam test kontraktowy.
- Typowanie strukturalne: do `add(item: Priceable)` pasuje każdy obiekt z `price()` i `vatPercent()`, także przypadkowy literał - rola nie jest listą zamkniętą.
- Funkcja obok interfejsu nie jest polimorficzna jak `default` w Javie: implementacja nie może jej "nadpisać". Jeśli któraś pozycja potrzebuje własnego liczenia VAT, to nowa (opcjonalna) składowa kontraktu, a nie wyjątek w funkcji.
- Zmiana nazwy albo kształtu metody w publicznym API łamie klientów - źródłowo i w już zbudowanym kodzie (scena s15).

### Pytanie do sali

Czy `vatPercent()` w ogóle powinno być częścią roli, skoro stawka zależy od kategorii towaru, a nie od obiektu?

## Scena s07. Collapse Hierarchy - sala IMAX

**Temat ze slajdów:** 8. Collapse Hierarchy
**Katalog:** `typescript/src/workshop/m5/s07_collapsehierarchy` · **Test:** `scripts/warsztat.sh --lang ts test m5/s07` (`typescript/test/workshop/m5/s07_collapsehierarchy/`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `ImaxHall` nie ma własnego stanu, jej override'y tylko wołają `super`, a jedyna różnica to reguła VIP w konstruktorze. Usuwamy puste override'y, przenosimy regułę do fabryki `Hall.imax(...)` i kasujemy podklasę.

**Zasada:** Collapse Hierarchy scala poziomy, gdy rozróżnienie nie jest już kontraktem, wariantem ani punktem rozszerzenia. Przed usunięciem typu trzeba sprawdzić, czy nie jest markerem, typem w konfiguracji albo kontraktem DI.

**Efekt:** Zostaje jedna klasa `Hall` z nazwaną fabryką, a wiedza o sali IMAX nie ginie. Usunięcie publicznej klasy łamie klientów, więc w bibliotece `ImaxHall` zostałaby na jedno wydanie jako przestarzały typ zgodności.

**Różnica względem Javy:** TS nie ma klas `final`, więc krok 3 nie oznacza `Hall` jako finalnej (komentarz "w Javie final"), a test pomija ten warunek. `Class.forName(...ImaxHall)` to w teście sprawdzenie, że plik `step3/ImaxHall.ts` nie istnieje, a "brak własnego stanu i metod" - brak deklaracji pól w źródle i tylko `constructor` na prototypie `ImaxHall`.

### Co widzimy

`ImaxHall extends Hall` nie ma własnego stanu. Jej override'y tylko wołają `super`, a jedyna różnica to reguła w konstruktorze: VIP w dwóch ostatnich rzędach. Nikt nie sprawdza `instanceof ImaxHall`.

```typescript
constructor(name: string, rows: number, seatsPerRow: number) {
  super(name, rows, seatsPerRow, rows - 1);
}

override describe(): string {
  return super.describe();
}
```

### Krok 1: Usunięcie override'ów wołających tylko super

**W IDE:** VS Code nie podkreśla metod identycznych z metodą nadklasy - usuń ręcznie `capacity()` i `describe()` z `ImaxHall` (test potwierdzi, że nic się nie zmieniło).
**Po:**

```typescript
export class ImaxHall extends Hall {
  constructor(name: string, rows: number, seatsPerRow: number) {
    super(name, rows, seatsPerRow, rows - 1);
  }
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s07` - 22 testy zielone.
**Co powiedzieć:** zostało jedno: reguła tworzenia. To nie jest kontrakt, wariant zachowania ani punkt rozszerzenia.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 0 1`

### Krok 2: Reguła tworzenia do fabryki w Hall

**W IDE:** w `Hall` dopisz ręcznie statyczną fabrykę `imax(name, rows, seatsPerRow)` z regułą z konstruktora `ImaxHall`. W `HallCatalog` zamień `new ImaxHall(...)` na `Hall.imax(...)` i usuń import.
**Po:**

```typescript
/** Sala IMAX: VIP zawsze w dwóch ostatnich rzędach (wiedza przeniesiona z konstruktora ImaxHall). */
static imax(name: string, rows: number, seatsPerRow: number): Hall {
  return new Hall(name, rows, seatsPerRow, rows - 1);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** wiedza z konstruktora podklasy ma teraz nazwę w klasie bazowej. `ImaxHall` nie ma już użyć (Find All References ⇧⌥F12 pokaże zero).
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 1 2`

### Krok 3: Usunięcie ImaxHall

**W IDE:** Safe Delete w VS Code nie ma: po Find All References bez wyników usuń plik `ImaxHall.ts` (w Eksploratorze ⌘⌫), a `npm run typecheck` potwierdzi, że nikt go nie importował. W Javie `Hall` dostaje teraz `final`; w TS zostaje zwykłą klasą. Kierunek scalania ma znaczenie: zostaje nazwa, której używają klienci (`Hall`), a nie nazwa podklasy.
**Po:**

```typescript
/** Krok 3 (rozwiązanie): jedna klasa (w Javie final). Rozróżnienie IMAX nie było kontraktem, tylko regułą tworzenia. */
export class Hall { ... }
```

**Uruchom:** test zielony. `S07SolutionTest` sprawdza, że pliku `ImaxHall.ts` w kroku 3 nie ma, a `Hall.imax(...)` tworzy zwykłą `Hall`.
**Co powiedzieć:** zostaje nazwa, której używają klienci. W bibliotece `ImaxHall` zostałaby na jedno wydanie jako przestarzały typ zgodności z komentarzem JSDoc `@deprecated` (odpowiednik `@Deprecated(forRemoval = true)`) - VS Code przekreśla wtedy każde użycie.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s07 2 3`

### Rozwiązanie i uzasadnienie

Jedna klasa `Hall` z nazwaną fabryką. Rozróżnienie IMAX nie było kontraktem, tylko regułą tworzenia.

### Pułapki

- Pusty typ bywa **markerem** (np. `instanceof ImaxHall` w konfiguracji projektora), punktem rozszerzenia albo tokenem w kontenerze DI. Przed Collapse szukaj też w konfiguracji, dekoratorach, rejestrach wtyczek i dynamicznych `import()`.
- Usunięcie publicznej klasy łamie klientów - przy przebudowie (błąd importu) i w już zbudowanym kodzie (`SyntaxError`/`undefined` przy imporcie brakującego eksportu).
- Przy ręcznym scalaniu sprawdź kierunek: zostaje nazwa używana przez klientów.

### Pytanie do sali

Za pół roku sale IMAX dostaną inną politykę zwrotów. Czy wtedy przywrócicie podklasę, czy dodacie pole?

## Scena s08. Replace Inheritance with Composition - licznik kliknięć

**Temat ze slajdów:** 9.1-9.4. Replace Inheritance with Composition, procedura i pułapki delegowania
**Katalog:** `typescript/src/workshop/m5/s08_composition` · **Test:** `scripts/warsztat.sh --lang ts test m5/s08` (`typescript/test/workshop/m5/s08_composition/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** `SeatSelection` dziedziczy po `Set<string>` tylko dla wygody i liczy kliknięcia, a hurtowe `addAll()` dodaje przez nasze `add()` na `this`, więc dwa miejsca dają cztery kliknięcia. Zastępujemy dziedziczenie delegatem i zamykamy go za wąską fasadą.

**Zasada:** Replace Inheritance with Composition stosujemy, gdy `extends` służy tylko do ponownego użycia kodu, a podklasa nie jest zastępowalnym wariantem. Pułapka self-use polega na tym, że metody nadklasy wołają inne metody na `this`, czego nikt nie gwarantuje w kontrakcie. Delegowanie ma własne pułapki: wyciek delegata, metody zwracające delegata i utrata tożsamości zbioru.

**Efekt:** Zachowanie świadomie się zmienia: hurtowy wybór liczy każde miejsce raz, a klient dostaje zamrożoną kopię listy. Kosztem jest utrata przypisywalności do `Set` (`instanceof Set`) oraz operacji `delete`, `clear`, iteracji i właściwości `size` (teraz metoda `size()`) - to nie jest zgodny zamiennik.

**Różnica względem Javy:** start dziedziczy po `Set<string>` (odpowiednik `LinkedHashSet` - `Set` w JS zachowuje kolejność wstawiania). `Set` w JS nie ma `addAll`, więc nie ma też odziedziczonej metody hurtowej z ukrytym self-use jak `AbstractCollection.addAll` w JDK. Start ma własne "wygodne" `addAll`, które dodaje przez `this.add` (`seats.forEach(this.add, this)`), a `add` jest nadpisane - efekt identyczny (2 miejsca -> 4 kliknięcia). Prawdziwy self-use w bibliotece standardowej JS też istnieje: konstruktor `Set` woła `this.add()` dla każdego elementu, i to zanim podklasa zainicjuje swoje pola (temat sceny s11). `Set.add` zwraca zbiór, a nie `boolean`, więc kroki mają prywatną metodę `#addToSeats`, która zwraca "czy dodano" jak w Javie. `List.copyOf` to `Object.freeze([...])`, a `UnsupportedOperationException` to `TypeError` przy `push`.

### Co widzimy

`SeatSelection extends Set<string>` tylko po to, by mieć `add`/`has` za darmo, i liczy kliknięcia klienta do analityki. Pułapka self-use: `addAll()` dodaje przez `add()` na `this`, więc miejsca dodane hurtem liczą się podwójnie.

```typescript
addAll(seats: readonly string[]): boolean {
  this.#clicks += seats.length;
  const before = this.size;
  seats.forEach(this.add, this);   // woła nasze add() -> #clicks++ drugi raz
  return this.size !== before;
}
```

`S08SolutionTest.startCountsBulkSelectionTwiceBecauseAddAllCallsAdd` dokumentuje: 2 miejsca, 4 kliknięcia. Drugi test `start...` pokazuje, że odziedziczone `clear()` omija licznik.

### Krok 1: Replace Inheritance with Delegation

**W IDE:** VS Code nie ma Replace Inheritance with Delegation - ręcznie. Usuń `extends Set<string>`, dodaj pole `readonly #seats = new Set<string>()`, a metody `add`, `addAll`, `contains`, `size()` przepisz na delegata (`add` zwraca teraz `boolean` przez prywatne `#addToSeats`). Dopisz też getter delegata `getSeats()` - tak, jak zrobiłby to generator w IDE, żeby pokazać pułapkę.
**Po:**

```typescript
readonly #seats = new Set<string>();

addAll(more: readonly string[]): boolean {
  this.#clicks += more.length;
  let changed = false;
  for (const seat of more) {
    changed = this.#addToSeats(seat) || changed;   // add() delegata, nie nasze
  }
  return changed;
}

getSeats(): Set<string> {
  return this.#seats;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s08` - na snapshotach 14 zielonych. Gdy robisz krok w `start`, oba testy `start...` robią się czerwone (pułapka usunięta). Warianty `start` w `S08EquivalenceTest` zostają zielone: pomocnik `sizeOf` czyta rozmiar i jako pole `size` zbioru, i jako metodę `size()` fasady. Zwykły klient takiego pomocnika nie ma - zmienił się kształt API (pole na metodę), więc `npm run typecheck` pokaże każde miejsce do poprawy.
**Co powiedzieć:** hook woła się teraz na delegacie, nie na `this`, więc podwójne liczenie znika. Ale getter wydaje delegata - `getSeats().add('Z1')` omija licznik (test `generatedGetterLeaksTheDelegate`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 0 1`

### Krok 2: Encapsulate Collection i wąska fasada

**W IDE:** usuń `getSeats()` (Find All References ⇧⌥F12 najpierw), dodaj `seats()` zwracające `Object.freeze([...this.#seats])`. `addAll` przepisz na pętlę po własnym `add` - self-use jest teraz bezpieczne, bo nikt nie dziedziczy po fasadzie i sami kontrolujemy oba końce (w Javie klasa dostaje `final`; w TS to decyzja projektowa zapisana w komentarzu).
**Po:**

```typescript
seats(): readonly string[] {
  return Object.freeze([...this.#seats]);
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** świadomie tracimy `instanceof Set`, `delete`/`clear`, iterację (`for...of`), właściwość `size` i porównania zbioru. Tę listę trzeba wypisać i uzgodnić z klientami - to nie jest zgodny zamiennik publicznego `Set`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s08 1 2`

### Rozwiązanie i uzasadnienie

Klasa z prywatnym zbiorem, jawnym API i zamrożoną kopią. Licznik nie zależy od szczegółów implementacji `Set` ani od tego, czy jakaś metoda hurtowa akurat woła `add`.

### Pułapki

- Self-use nie jest częścią kontraktu - wynik start zależy od tego, czy metoda hurtowa akurat woła `add`. W JS sam konstruktor `Set(iterable)` woła `this.add()` - podklasa `Set` z licznikiem, utworzona z elementami, liczyłaby je przed inicjalizacją własnych pól.
- Metoda delegata zwracająca delegata albo jego widok (np. `Set.add` zwraca zbiór, `values()` zwraca iterator po delegacie) - zmiany i odczyty omijają logikę wrappera.
- `readonly` na polu nie chroni zawartości: `readonly #seats` dalej można zmienić przez `getSeats().add(...)`. Chroni dopiero kopia albo `Object.freeze`.
- Znika tożsamość zbioru: `instanceof Set`, spread `[...selection]`, `new Set(selection)` i porównania w kodzie klientów przestają działać.

### Pytanie do sali

Które testy wykryłyby, że wrapper przypadkiem zwraca delegata? Spy, callback, a może test tożsamości `this`?

## Scena s09. Overriding a overloading - pułapka po Extract Superclass

**Temat ze slajdów:** 2.1. Overriding i dynamiczna dyspozycja; 2.2-2.3. Overloading - wybór statyczny
**Katalog:** `typescript/src/workshop/m5/s09_overloading` · **Test:** `scripts/warsztat.sh --lang ts test m5/s09` (`typescript/test/workshop/m5/s09_overloading/`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** Po Extract Superclass `PriceList` ma dwa warianty: `price(Ticket)` i `priceStudent(StudentTicket)`, a klient z `Ticket[]` może wywołać tylko wersję bazową, więc student płaci pełną cenę. Zamieniamy wybór wariantu po typie na nadpisywane `discountPercent()` i poprawiamy `equals(other: Ticket)` na prawdziwy kontrakt równości `Equatable`.

**Zasada:** Overriding działa dynamicznie: JS wybiera implementację według prototypu obiektu, a rzutowanie typu (`as Ticket`) tego nie wyłącza. Wariant wybierany według typu **deklarowanego** (w Javie przeciążenie wybrane przez kompilator, w TS osobna nazwa wybrana przez piszącego kod) to decyzja zapisana na stałe w kodzie klienta. Reguła bezpieczeństwa: zachowanie zależne od typu umieszczamy w override, a kontrakty, które mają łapać błędy nadpisania, zapisujemy tak, żeby kompilator je sprawdzał.

**Efekt:** Zachowanie świadomie się zmienia: student dostaje zniżkę niezależnie od typu referencji, a `alreadyInCart` znajduje równy bilet. Test równoważności tego nie wykrył, bo testował przez typ konkretny - dlatego testy przez typ bazowy są obowiązkowe.

**Różnica względem Javy:** TypeScript nie ma przeciążeń wybieranych przez kompilator (w czasie działania jest jedna metoda o danej nazwie), a kolekcje JS nie wołają `equals`. Obie pułapki mają więc najbliższe odpowiedniki. (1) Przeciążenia to osobne nazwy `price(Ticket)` i `priceStudent(StudentTicket)`: wariant wybiera piszący kod według typu deklarowanego, a po migracji klienta na `Ticket[]` jedynym poprawnym typowo wyborem jest `price(ticket)`. (2) Start ma `equals(other: Ticket)`, a `Checkout.alreadyInCart` używa `cart.includes(ticket)`, które porównuje tożsamość. Odpowiednikiem `Object.equals(Object)` + `List.contains` jest stabilny kontrakt sceny `s09_overloading/Equatable.ts`: właściwość `equals: (other: unknown) => boolean` i funkcja `containsEqual(items, candidate)`. Odpowiednikiem `@Override` jest tu **zapis kontraktu typem funkcyjnym** - o tym krok 2.

### Co widzimy

Stan po Extract Superclass: `StudentTicket extends Ticket`. `PriceList` ma dwa warianty ceny. Dopóki klient miał `StudentTicket[]`, wołał `priceStudent` - działało. Po migracji na `Ticket[]` naturalny (i jedyny, który się kompiluje) tekst `priceList.price(ticket)` wybiera wersję bazową. Student płaci pełną cenę.

```typescript
total(tickets: readonly Ticket[]): Money {
  let total = Money.ZERO;
  for (const ticket of tickets) {
    total = total.plus(this.#priceList.price(ticket));   // zawsze price(Ticket)
  }
  return total;
}
```

Druga pułapka: `equals(other: Ticket)` wygląda jak równość wartości, ale `cart.includes(ticket)` go nie widzi - porównuje tożsamość. A gdyby ktoś zapisał kontrakt równości jako zwykłą metodę interfejsu `equals(other: unknown): boolean`, zawężony parametr `Ticket` przeszedłby przez niego bez błędu, bo parametry **metod** TypeScript sprawdza biwariantnie. To TS-owy odpowiednik "`equals(Ticket)` to przeciążenie, nie override".

### Krok 1: Replace Overloading with Overriding

**W IDE:** w `Ticket` dodaj `discountPercent(): number { return 0; }`, w `StudentTicket` ręcznie `override discountPercent(): number { return 25; }`. `PriceList.price(Ticket)` liczy z `ticket.discountPercent()`. `priceStudent` usuń (Find All References ⇧⌥F12 najpierw - Safe Delete w VS Code nie ma).
**Po:**

```typescript
price(ticket: Ticket): Money {
  return ticket.basePrice().minus(ticket.basePrice().percent(ticket.discountPercent()));
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s09` - `startPicksOverloadByDeclaredType` czerwony (pułapka usunięta), `overridingDispatchesOnRuntimeClass` zielony: 58.75 zamiast 65.00. Warianty `start` w `S09EquivalenceTest` zostają zielone, bo pomocnik `priceStudentOf` woła `priceStudent`, a gdy go nie ma - `price`. Stary klient bez takiego pomocnika dostałby `priceStudent is not a function` (a `npm run typecheck` - TS2339). Na snapshotach: 11 zielonych.
**Co powiedzieć:** dyspozycja dynamiczna wybiera implementację według prototypu obiektu, rzutowanie na nadtyp tego nie wyłącza. Wariant wybrany po typie deklarowanym to decyzja zapisana na stałe w kodzie klienta - w Javie w `.class`, w TS w nazwie metody.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 0 1`

### Krok 2: equals(other: unknown) przez kontrakt Equatable

**W IDE:** w `Ticket` dopisz `implements Equatable` (import z `../Equatable.js`). Kompilator od razu zgłosi TS2416 na `equals(other: Ticket)`: parametr nie jest zgodny z `(other: unknown) => boolean`. Zmień sygnaturę na `equals(other: unknown)`, porównaj `constructor` (odpowiednik `getClass()`), dodaj `key()` jako odpowiednik `hashCode()`. W `Checkout.alreadyInCart` zamień `cart.includes(ticket)` na `containsEqual(cart, ticket)`.
**Po:**

```typescript
equals(other: unknown): boolean {
  return other instanceof Ticket && this.constructor === other.constructor
    && this.#title === other.#title && this.#basePrice.equals(other.#basePrice);
}

/** Odpowiednik hashCode(): klucz wartości do Map/Set - równe bilety mają ten sam klucz. */
key(): string {
  return `${this.constructor.name}|${this.#title}|${this.#basePrice}`;
}
```

**Uruchom:** test zielony - `alreadyInCart` znajduje równy bilet (`solutionOverridesEqualsAndHashCode`). Dodatkowy test portu `narrowedEqualsParameterIsCaughtOnlyByFunctionTypedContract` kompiluje w pamięci bilet z `equals(other: Ticket)`: przeciw kontraktowi zapisanemu jako metoda - zero błędów, przeciw `Equatable` sceny - `TS2416`. (Po kroku 2 na żywo w `start` czerwony robi się też `overloadedEqualsIsInvisibleToCollections`, bo dokumentuje pułapkę startu.)
**Co powiedzieć:** w Javie `@Override` zamienia cichy błąd przeciążenia w błąd kompilacji. W TS tę rolę gra kontrakt zapisany typem funkcyjnym (sprawdzany kontrawariantnie dzięki `strictFunctionTypes`) - a słowo `override` pilnuje tylko, że metoda o tej nazwie istnieje w bazie, nie jej parametrów. W Javie `Checkout` w tym kroku się nie zmienia (`contains` samo woła `equals(Object)`); w JS kolekcja nie zna `equals`, więc klient musi zapytać przez kontrakt.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s09 1 2`

### Rozwiązanie i uzasadnienie

Zniżka jest zachowaniem biletu (override), a nie decyzją piszącego klienta (wariant po typie). Jedno `price(Ticket)` działa poprawnie dla każdego typu deklarowanego. Równość jest kontraktem sprawdzanym przez kompilator, a kolekcja pyta o nią przez `containsEqual`.

### Pułapki

- Po Pull Up / Extract Superclass ten sam kod klienta zaczyna wybierać inny wariant - testy przez typ bazowy to wykrywają, testy przez typ konkretny nie (`S09EquivalenceTest` jest zielony nawet dla start!).
- Już zbudowany klient woła dawną nazwę wariantu, nawet gdy dodamy nową, "lepszą" metodę.
- Biwariancja parametrów metod: `equals(other: Ticket)`, `compare(a: Ticket, b: Ticket)` i podobne zawężenia przechodzą przez kontrakty zapisane jako metody. Kontrakt, który ma łapać takie błędy, zapisuj jako właściwość z typem funkcyjnym.
- `equals` porównujące `instanceof` zamiast `constructor` łamie symetrię między bazą a podklasą.
- `Array.includes`, `Set` i `Map` porównują tożsamość (SameValueZero) - równość wartości trzeba przekazać jawnie (`containsEqual`, klucz `key()` w `Map`).

### Pytanie do sali

Dlaczego test równoważności tej sceny przechodzi dla start, mimo że start liczy źle?

## Scena s10. Ukrywanie pól i metod static

**Temat ze slajdów:** 2.3. Pola nie są polimorficzne; 2.1. `static` jest ukrywana
**Katalog:** `typescript/src/workshop/m5/s10_fieldhiding` · **Test:** `scripts/warsztat.sh --lang ts test m5/s10` (`typescript/test/workshop/m5/s10_fieldhiding/`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** `StudentTicket` redeklaruje pole prywatne `#type` i statyczne `category()` z `Ticket`, więc `label()` dla studenta zwraca `'BILET: NORMAL'`. Zastępujemy dwa sloty jednym prywatnym polem ustawianym przez konstruktor bazy, a `category()` robimy metodą instancji.

**Zasada:** Pola prywatne i metody `static` nie są polimorficzne: są wiązane z klasą, w której stoi kod, a nie z klasą obiektu. Ukryte pole to dwa niezależne sloty w jednym obiekcie. Reguła: stan przekazujemy przez `super(...)`, a zachowanie zależne od obiektu musi być metodą instancji.

**Efekt:** Zachowanie świadomie się zmienia: `label()` studenta zwraca `'BILET ULGOWY: STUDENT'` bez względu na typ referencji. Znika też ryzyko, że Pull Up Field bez usunięcia deklaracji w podklasie po cichu stworzy ukrycie.

**Różnica względem Javy:** w JS pola **publiczne** są właściwościami obiektu - redeklaracja `type = 'STUDENT'` w podklasie nadpisałaby ten sam slot i żadnego ukrycia by nie było (`label()` zobaczyłoby `'STUDENT'`). Ukrywanie pól istnieje w TS tylko dla pól prywatnych ES: `#type` w `Ticket` i `#type` w `StudentTicket` to dwa niezależne sloty w jednym obiekcie, a kod `Ticket.label()` czyta slot `Ticket` (`private type` w obu klasach TS odrzuca błędem kompilacji). Pola `#` są niewidoczne z zewnątrz, więc start ma akcesor `type()` w bazie i nadpisany w podklasie - dzięki temu test pokazuje oba sloty bez refleksji. Metoda statyczna: `static category()` w obu klasach (w podklasie wymagane `static override`), a `label()` woła `Ticket.category()` - wiązanie z nazwą klasy w kodzie, jak w Javie. Krok 1 ma jeden konstruktor `constructor(type = 'NORMAL')` zamiast dwóch konstruktorów Javy, bo TS nie ma przeciążeń konstruktorów.

### Co widzimy

`StudentTicket` deklaruje pole `#type` i statyczne `category()` o tych samych nazwach co `Ticket`. Wygląda na nadpisanie, ale to ukrycie. `label()` jest zapisane w `Ticket`, więc dla studenta zwraca `'BILET: NORMAL'`.

```typescript
const student = new StudentTicket();
student.type()    // 'STUDENT' - slot StudentTicket (przez nadpisany akcesor)
student.label()   // 'BILET: NORMAL' - kod Ticket czyta drugi slot w tym samym obiekcie
```

Testy `start...` czytają deklaracje pól i modyfikator `static` ze źródła sceny, a sloty - przez akcesor `type()` i `label()`, więc moduł działa także po naprawie na żywo.

### Krok 1: Jedno pole zamiast dwóch slotów

**W IDE:** w `Ticket` zamień pole na `readonly #type: string` ustawiane w konstruktorze `constructor(type = 'NORMAL')`. W `StudentTicket` usuń pole `#type` i nadpisany akcesor `type()`, dodaj `constructor() { super('STUDENT'); }` (ręcznie - Encapsulate Field ani Introduce Parameter w VS Code nie ma).
**Po:**

```typescript
readonly #type: string;

constructor(type = 'NORMAL') {
  this.#type = type;
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s10` - testy `start...` czerwone po naprawie start, `step1FixesFieldButStaticIsStillHidden` zielony: `'BILET: STUDENT'`. Na snapshotach: 7 zielonych.
**Co powiedzieć:** pole ukryte to dwa niezależne sloty. Który zobaczysz, zależy od klasy, w której stoi kod. Stan przekazujemy przez konstruktor, a nie redeklarujemy.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 0 1`

### Krok 2: category() jako metoda instancji

**W IDE:** usuń `static` z obu `category()` (ręcznie; w podklasie zostaje samo `override`), a w `label()` zamień `Ticket.category()` na `this.category()`.
**Po:**

```typescript
// StudentTicket
override category(): string {
  return 'BILET ULGOWY';
}
// Ticket
label(): string {
  return this.category() + ': ' + this.#type;
}
```

**Uruchom:** test zielony: `'BILET ULGOWY: STUDENT'` bez względu na typ referencji.
**Co powiedzieć:** metoda statyczna wywołana przez nazwę klasy jest związana z tą klasą na stałe. Jeśli zachowanie ma zależeć od obiektu, musi być metodą instancji. (W JS dałoby się napisać `this.constructor.category()`, ale TS typuje `this.constructor` jako `Function` - piszący kod i tak sięga po nazwę klasy.)
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s10 1 2`

### Rozwiązanie i uzasadnienie

Jedno prywatne pole, jedna polimorficzna metoda. `label()` daje ten sam wynik dla `const t: Ticket = new StudentTicket()` i `const s = new StudentTicket()`.

### Pułapki

- Pull Up Field bez usunięcia deklaracji `#pole` w podklasie tworzy ukrycie zamiast przeniesienia. Z polem publicznym jest odwrotnie: redeklaracja w podklasie po cichu nadpisuje slot bazy (a z inicjalizatorem - nadpisuje wartość ustawioną przez konstruktor bazy).
- Pola statyczne i stan w `static` są związane z klasą: Pull Up takiego pola sprawia, że dwie podklasy zaczynają dzielić jeden stan (liczniki, cache).
- Wywołanie `Ticket.category()` wewnątrz metody instancji wygląda niewinnie, a wyłącza polimorfizm - to samo co wywołanie metody statycznej przez instancję w Javie.

### Pytanie do sali

Gdzie w waszym kodzie są stałe `static readonly` redeklarowane w podklasach? Czy ktoś czyta je przez nazwę klasy bazowej?

## Scena s11. Konstruktor wołający metodę nadpisywalną

**Temat ze slajdów:** 2.4. Konstruktory i inicjalizacja; 3.1-3.3 (nie wołaj override z konstruktora, żeby umożliwić Pull Up)
**Katalog:** `typescript/src/workshop/m5/s11_constructorcall` · **Test:** `scripts/warsztat.sh --lang ts test m5/s11` (`typescript/test/workshop/m5/s11_constructorcall/`)
**Czas:** ~8 min

### W skrócie

**Co robimy:** Konstruktor `Ticket` woła nadpisywalne `describe()`, a `VipTicket` czyta w nim pole, które jeszcze nie istnieje, więc etykieta na zawsze zawiera `undefined`. Najpierw naprawiamy lokalnie w podklasie (override `label()` liczący etykietę na żądanie), potem strukturalnie: etykieta liczona na żądanie w bazie.

**Zasada:** Konstruktor bazy wykonuje się, zanim powstaną pola podklasy (w JS pola podklasy są tworzone dopiero po powrocie z `super(...)`), więc wywołany z niego override widzi obiekt w połowie zbudowany. Naprawa w jednej podklasie jest lokalna - każda następna musi o niej pamiętać. Reguła: konstruktor tylko przypisuje pola i nie woła metod nadpisywalnych.

**Efekt:** Zachowanie świadomie się zmienia: etykieta VIP zawiera salonik, a poprawność nie zależy od kolejności inicjalizacji. Kosztem jest liczenie etykiety przy każdym wywołaniu zamiast raz w konstruktorze.

**Różnica względem Javy:** pułapka jest ta sama, ale objaw w JS to `undefined`, nie `null`: start daje `Miejsce K12 (VIP: undefined)`. Pole `lounge` jest w tej scenie celowo `private readonly lounge` (prywatne tylko dla kompilatora TS), bo z polem prywatnym ES `#lounge` odczyt przed inicjalizacją rzuca `TypeError` - pokazuje to dodatkowy test `startWithPrivateNameFieldFailsFast`. TS/JS nie ma prologu konstruktora z Javy 25 (JEP 513): użycie `this` przed `super()` to błąd kompilacji i czasu działania. Dlatego krok 1 to inna lokalna, krucha naprawa - override `label()` w podklasie (w Javie `label()` jest `final`, w TS nie ma `final`, więc taki override jest możliwy). Test `constructorPrologInitializesFieldBeforeSuper` ma nazwę z Javy, a sprawdza tę naprawę. Odpowiednika ostrzeżenia `-Xlint:this-escape` TypeScript nie ma.

### Co widzimy

Konstruktor `Ticket` zapamiętuje etykietę, wołając `describe()`. `VipTicket` nadpisuje `describe()` i używa pola `lounge`, którego w tym momencie jeszcze nie ma - konstruktor bazy kończy się, zanim podklasa zainicjuje swoje pola.

```typescript
constructor(seat: string) {
  this.#seat = seat;
  this.#label = this.describe();   // kompilator nie ostrzega
}
```

`new VipTicket('K12', 'Salonik A').label()` zwraca `'Miejsce K12 (VIP: undefined)'` - na zawsze, bo wynik jest w polu `readonly #label`.

### Krok 1: Szybka naprawa lokalna w podklasie

**W IDE:** najpierw pokaż, że prolog z Javy 25 nie przejdzie: przenieś `this.lounge = lounge;` przed `super(seat);` - kompilator zgłosi błąd (`'super' must be called before accessing 'this'`). Cofnij (⌘Z). Zamiast tego w `VipTicket` dopisz ręcznie `override label()`, które liczy etykietę na żądanie.
**Po:**

```typescript
override label(): string {
  return this.describe();
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s11` - `startSubclassSeesUninitializedField` czerwony (pułapka usunięta), pozostałe zielone. Na snapshotach: 7 zielonych.
**Co powiedzieć:** działa, ale naprawa jest lokalna i krucha - każda następna podklasa musi pamiętać o nadpisaniu `label()`, a baza dalej woła `describe()` z konstruktora (i dalej zapisuje w `#label` etykietę z `undefined`, tylko nikt jej nie czyta). Zwróć uwagę na test `startWithPrivateNameFieldFailsFast`: z polem `#lounge` ten sam błąd nie jest cichy - `new` rzuca `TypeError`. Pola `#` zamieniają cichy błąd w głośny.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s11 0 1`

### Krok 2: Konstruktor bez wywołań nadpisywalnych

**W IDE:** w `Ticket` usuń pole `#label` i jego przypisanie w konstruktorze (Replace Field with Query - ręcznie, Inline Field w VS Code nie ma): `label()` zwraca `this.describe()`. W `VipTicket` usuń override `label()` z kroku 1.
**Po:**

```typescript
label(): string {
  return this.describe();
}
```

**Uruchom:** test zielony.
**Co powiedzieć:** etykieta liczona na żądanie, gdy obiekt jest w pełni zbudowany. Poprawność nie zależy od kolejności inicjalizacji. Inne naprawy: przekazanie wyliczonej wartości przez parametr konstruktora albo fabryka, która woła metodę po `new`.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s11 1 2`

### Rozwiązanie i uzasadnienie

Konstruktor bazy tylko przypisuje pola. Wszystko, co może być nadpisane, wykonuje się po zakończeniu konstrukcji.

### Pułapki

- Pull Up metody, którą ktoś potem wywoła z konstruktora bazy "bo tak wygodnie".
- Pole z inicjalizatorem w podklasie (`private lounge = 'Salonik'`) też jest ustawiane dopiero po `super(...)` - override wywołany z konstruktora bazy zobaczy `undefined`. Co gorsza, przy `useDefineForClassFields` (domyślne dla nowych targetów) samo zadeklarowane pole podklasy bez inicjalizatora nadpisze po `super(...)` wartość, którą override zdążył przypisać w konstruktorze bazy.
- Wbudowane klasy też wołają metody z konstruktora: `new Set(iterable)` woła `this.add()` dla każdego elementu, zanim podklasa zainicjuje pola (scena s08).
- Podklasa nie może przypisać odziedziczonego pola `readonly` - trzeba je przekazać przez konstruktor bazy.

### Pytanie do sali

TypeScript nie ostrzega przed wywołaniem metody nadpisywalnej z konstruktora. Jak to wyłapiecie w legacy - reguła lintera, przegląd kodu, pola `#` zamiast `private`?

## Scena s12. Generyki, wymazywanie typów i brak metod bridge

**Temat ze slajdów:** 2.5-2.8. Sygnatury po erasure i metody bridge; 10.2-10.3. Refleksja
**Katalog:** `typescript/src/workshop/m5/s12_bridgemethods` · **Test:** `scripts/warsztat.sh --lang ts test m5/s12` (`typescript/test/workshop/m5/s12_bridgemethods/`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `RuleRegistry` rejestruje reguły cenowe "frameworkowo": typu parametru `apply(...)` nie da się przeczytać w czasie działania, więc zgaduje obsługiwany bilet z nazwy klasy reguły. Wydzielamy generyczne `PriceRule<T>` - i nic się nie psuje, co jest pułapką - a potem zastępujemy zgadywanie jawnym `ticketType()`.

**Zasada:** TypeScript wymazuje generyki i adnotacje typów całkowicie: w JavaScripcie nie zostaje ani `T`, ani typ parametru, ani (w przeciwieństwie do Javy) żadna metoda bridge z rzutowaniem. Kod, który w czasie działania "odgaduje" typ z sygnatury, nazwy albo konwencji, zgaduje. Reguła: typ potrzebny w czasie działania musi być jawną częścią kontraktu.

**Efekt:** Rejestr nie zgaduje, a brak reguły kończy się czytelnym `IllegalStateError`, zły bilet - `TypeError` w jednym miejscu. Każda reguła musi teraz dodatkowo deklarować swój typ biletu.

**Różnica względem Javy:** TS nie ma metod bridge ani typów parametrów w czasie działania, więc scena zachowuje intencję "refleksja zgaduje typ z sygnatury, generyczna hierarchia to psuje, jawny kontrakt naprawia", ale objawy są inne. Start zgaduje typ z konwencji nazw (`StudentRule` -> `StudentTicket`) i woła `apply` przez rzutowanie `as { apply(ticket: Ticket): Money }` (odpowiednik `getMethod(...).invoke`). W Javie krok 1 **psuje test** (bridge widoczny dla refleksji rejestruje się pod `Ticket`); w TS krok 1 **niczego nie psuje** - interfejs generyczny nie dodaje żadnej metody, a kompiluje się tylko dzięki biwariancji parametrów metod: `PriceRule<StudentTicket>` przypisuje się do `PriceRule<Ticket>`. To TS-owy odpowiednik mostu - wywołanie przez wymazany typ, tyle że **bez rzutowania**: zły bilet zostaje wyceniony po cichu zamiast `ClassCastException`. Testy mają nazwy dostosowane do TS: `genericInterfaceAddsNoRuntimeMethod`, `naiveReflectionWouldRegisterGuessedType`, `erasedCallWithWrongTicketSilentlyMisprices`. Typy sceny (`Ticket` - interfejs, `StandardTicket`, `StudentTicket`) to klasy zamiast rekordów.

### Co widzimy

Reguły cenowe `StandardRule` i `StudentRule` nie mają wspólnego typu. `RuleRegistry` przyjmuje je jako `object[]` i rejestruje pod nazwą biletu wyprowadzoną z nazwy klasy reguły.

```typescript
constructor(...rules: object[]) {
  for (const rule of rules) {
    this.#rules.set(rule.constructor.name.replace(/Rule$/, 'Ticket'), rule);
  }
}

price(ticket: Ticket): Money {
  const rule = this.#rules.get(ticket.constructor.name) as { apply(ticket: Ticket): Money };
  return rule.apply(ticket);
}
```

Działa, dopóki nazwa klasy reguły pasuje do nazwy klasy biletu (i dopóki minifikator nie zmieni nazw klas). Typy biletów (`Ticket`, `StandardTicket`, `StudentTicket`) są stabilnym kontraktem sceny.

### Krok 1: Extract Interface generycznej roli

**W IDE:** VS Code nie ma Extract Interface - ręcznie nowy plik `PriceRule.ts` z `export interface PriceRule<T extends Ticket> { apply(ticket: T): Money; }`. Reguły dostają `implements PriceRule<StudentTicket>` / `PriceRule<StandardTicket>`. W `RuleRegistry` zmień typ przechowywanych reguł z `object` na `PriceRule<Ticket>` i usuń rzutowanie przy `apply` (zostaje tylko `as PriceRule<Ticket>` po `get`). **Uruchom test - jest zielony.** Zatrzymaj się tu: w Javie w tym miejscu test był czerwony, bo kompilator dołożył syntetyczne `apply(Ticket)`.
**Po:**

```typescript
export interface PriceRule<T extends Ticket> {
  apply(ticket: T): Money;
}
// RuleRegistry
readonly #rules = new Map<string, PriceRule<Ticket>>();

constructor(...rules: PriceRule<Ticket>[]) { ... }
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s12` - 10 testów zielonych. Pokaż trzy testy `S12SolutionTest`: na prototypie `StudentRule` jest jedna `apply` (brak mostu), reguła o nazwie `DiscountRule` rejestruje się pod zmyślonym typem `'DiscountTicket'`, a `const erased: PriceRule<Ticket> = new StudentRule()` wycenia bilet **normalny** na 18.75 - bez błędu kompilacji i bez wyjątku.
**Co powiedzieć:** w Javie po erasure `apply(T)` to `apply(Ticket)`, więc JVM potrzebuje mostu z rzutowaniem - refleksja go widzi, a surowe wywołanie ze złym biletem kończy się `ClassCastException`. W TS nie ma czego zobaczyć: generyk zniknął, a przypisanie `PriceRule<StudentTicket>` do `PriceRule<Ticket>` przechodzi przez biwariancję metod. Niebezpieczeństwo nie zniknęło - przeszło w ciszę.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 0 1`

### Krok 2: Jawny kontrakt zamiast zgadywania

**W IDE:** w `PriceRule` dodaj `ticketType(): TicketType<T>` (typ `TicketType<T>` to konstruktor klasy biletu - odpowiednik `Class<T>`), zaimplementuj w regułach (Quick Fix ⌘. > Implement interface / Add missing member). `RuleRegistry` rejestruje po `ticketType()` w `Map` z konstruktorem jako kluczem, a wywołuje przez pomocniczą metodę generyczną, która sprawdza `instanceof` w jednym miejscu (odpowiednik `Class.cast`). Brak reguły -> `IllegalStateError`.
**Po:**

```typescript
static #apply<T extends Ticket>(rule: PriceRule<T>, ticket: Ticket): Money {
  const type = rule.ticketType();
  if (!(ticket instanceof type)) {
    throw new TypeError(`${ticket.constructor.name} nie jest ${type.name}`);
  }
  return rule.apply(ticket);
}
```

**Uruchom:** test zielony, `npm run typecheck` bez błędów i bez ani jednego `as` przy wywołaniu reguły.
**Co powiedzieć:** zgadywanie typu z nazwy zastąpiliśmy jawnym kontraktem, a typ istnieje teraz w czasie działania jako wartość (konstruktor klasy). Brak reguły to czytelny `IllegalStateError`, a nie `TypeError: Cannot read properties of undefined`; zły bilet to `TypeError` z nazwami obu klas, a nie cicha zła cena.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s12 1 2`

### Rozwiązanie i uzasadnienie

`PriceRule<T>` z `ticketType()`. Rejestr nie zgaduje, a sprawdzenie typu biletu jest w jednym miejscu, w czasie działania.

### Pułapki

- Biwariancja parametrów metod dotyczy każdej generycznej roli z metodą przyjmującą `T` (`Comparator<T>`, `Handler<T>`, `Rule<T>`): `Handler<Dog>` da się przypisać do `Handler<Animal>`. Kontrakt zapisany właściwością z typem funkcyjnym (`apply: (ticket: T) => Money`) byłby sprawdzany ściśle (scena s09).
- Zgadywanie typu z `constructor.name` psuje się po minifikacji, przy klasach anonimowych i przy dwóch klasach o tej samej nazwie w różnych modułach.
- Dekoratory z metadanymi typów (`emitDecoratorMetadata`, `reflect-metadata` w DI/ORM) dają tylko typ z adnotacji, i to bez generyków: `PriceRule<StudentTicket>` staje się `Object`. Frameworki na tym oparte widzą mniej, niż sugeruje źródło.

### Pytanie do sali

Gdzie w waszym systemie kod w czasie działania "odgaduje" typ - z nazwy klasy, pola `type`, kształtu obiektu - i co się stanie, gdy ktoś doda wariant o innej nazwie?

## Scena s13. Hierarchie zamknięte i wyczerpujący switch

**Temat ze slajdów:** 2.5-2.8. `sealed`, `permits`, `MatchException` - w TS: unia dyskryminowana, `never`, `assertNever`
**Katalog:** `typescript/src/workshop/m5/s13_sealed` · **Test:** `scripts/warsztat.sh --lang ts test m5/s13` (`typescript/test/workshop/m5/s13_sealed/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Otwarta hierarchia biletów i łańcuch `instanceof` z cichym `return 0` sprawiają, że nowy typ biletu nie dostaje zniżki bez żadnego ostrzeżenia. Zamykamy hierarchię unią dyskryminowaną, zamieniamy łańcuch na wyczerpujący `switch (ticket.kind)` z `assertNever` i dodajemy bilet dziecięcy.

**Zasada:** Unia dyskryminowana (`type Ticket = A | B | C` z polem `kind`) zamyka listę wariantów w jednym miejscu, a `switch` po `kind`, w którym `default` przekazuje resztę do `assertNever(ticket: never)`, pozwala kompilatorowi sprawdzić, czy obsłużono wszystkie warianty. Działa to tylko przy przebudowie: stary zbudowany JavaScript z nowym wariantem dojdzie do `assertNever` w czasie działania. `default` z wynikiem (np. `return 0`) wyłącza sprawdzanie wyczerpania.

**Efekt:** Zachowanie świadomie się zmienia: obce obiekty nie przejdą jako bilety, a nowy wariant bez obsługi to błąd kompilacji zamiast cichego zera. Kosztem jest zamknięcie hierarchii dla implementacji spoza repozytorium i ryzyko `Error: Unexpected value` w osobno budowanych pakietach.

**Różnica względem Javy:** TypeScript nie ma `sealed interface ... permits` ani pattern matchingu po typie. Odpowiednikiem jest unia dyskryminowana `type Ticket = StandardTicket | StudentTicket | SeniorTicket` z polem `kind`, a `switch` z wzorcami to `switch (ticket.kind)` z `default: return assertNever(ticket)`. Otwarta hierarchia startu to w TS typowanie strukturalne: `interface Ticket { kind: string; basePrice(): Money }` - **każdy** obiekt o tym kształcie (także literał) jest biletem. Warianty to klasy (zamiast rekordów) z `readonly kind = 'STUDENT'` i polem `#basePrice`, takie same w start i we wszystkich krokach, więc krok 1 zmienia tylko `Ticket.ts`. Pola `#` czynią klasy nominalnymi - po zamknięciu unii obcy literał o tym samym kształcie nie jest już biletem (TS2322). `MatchException` to `Error: Unexpected value` z `assertNever`. Dodatkowy test portu `newVariantBreaksCompilationOfExhaustiveSwitch` kompiluje w pamięci kalkulator ze step2 przeciw unii ze step3.

### Co widzimy

Otwarta hierarchia `Ticket` (interfejs strukturalny) z klasami `StandardTicket`, `StudentTicket`, `SeniorTicket` i kalkulator z łańcuchem `instanceof` zakończonym cichym `return 0`.

```typescript
if (ticket instanceof StudentTicket) {
  return 25;
} else if (ticket instanceof SeniorTicket) {
  return 30;
}
return 0;
```

`startSilentlyGivesUnknownTicketNoDiscount` podstawia jako `Ticket` zwykły literał `{ kind: 'CHILD', basePrice: () => Money.of('25.00') }` (bilet dziecięcy, 40%) - dostaje 0% zniżki bez żadnego ostrzeżenia. Przy typowaniu strukturalnym "dopisać typ z zewnątrz" jest jeszcze łatwiej niż w Javie: nie trzeba nawet klasy.

### Krok 1: Unia dyskryminowana zamiast otwartego interfejsu

**W IDE:** w `Ticket.ts` zamień interfejs na `export type Ticket = StandardTicket | StudentTicket | SeniorTicket;` (importy typów wariantów). Warianty już mają `readonly kind` z typem literału (`'STUDENT'` itd.), więc nic więcej nie trzeba.
**Po:**

```typescript
export type Ticket = StandardTicket | StudentTicket | SeniorTicket;
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s13` - 17 testów zielonych. Po zmianie w `start` test `startSilently...` jest nadal zielony w vitest (nie sprawdza typów), ale `npm run typecheck` zgłasza TS2322 na literale w teście: obcy obiekt nie przejdzie już jako bilet. `sealedHierarchyListsAllVariants` czyta warianty unii ze źródła i sprawdza ten sam TS2322 kompilacją w pamięci.
**Co powiedzieć:** zamknięta lista wariantów w jednym miejscu. Samo zamknięcie nie naprawia kalkulatora - łańcuch `if` nie jest wyczerpujący.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 0 1`

### Krok 2: Wyczerpujący switch z assertNever

**W IDE:** ręcznie zamień łańcuch `if` na `switch (ticket.kind)`: jawny `case 'STANDARD': return 0;`, `'STUDENT'`, `'SENIOR'`, a `default: return assertNever(ticket);` (import z `src/shared/assertNever.ts`). Importy klas wariantów przestają być potrzebne.
**Po:**

```typescript
switch (ticket.kind) {
  case 'STANDARD': return 0;
  case 'STUDENT': return 25;
  case 'SENIOR': return 30;
  default: return assertNever(ticket);
}
```

**Uruchom:** test zielony na snapshotach. W `start` czerwony robi się teraz `startSilently...`: literał `'CHILD'` dochodzi do `assertNever` i dostaje `Error: Unexpected value` zamiast cichego zera.
**Co powiedzieć:** w `default` zostaje typ `never` - jeśli jakiś wariant nie ma swojego `case`, reszta nie jest `never` i kompilacja się nie uda. Każdy wariant jest wymieniony jawnie, także ten z zerową zniżką.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 1 2`

### Krok 3: Nowy typ biletu wymusza obsługę

**W IDE:** utwórz `ChildTicket.ts` (skopiuj `SeniorTicket.ts`, `readonly kind = 'CHILD'`), dopisz `ChildTicket` do unii. Kompilacja `PriceCalculator` się wywraca: TS2345 `Argument of type 'ChildTicket' is not assignable to parameter of type 'never'`. Dopisz `case 'CHILD': return 40;` (VS Code podpowiada brakujące literały w `case` przez autouzupełnianie).
**Po:**

```typescript
case 'CHILD': return 40;
```

**Uruchom:** test zielony: bilet dziecięcy 25.00 -> 15.00.
**Co powiedzieć:** to jest cała wartość zamkniętej unii: nowy wariant zamienia ciche błędy w błędy kompilacji. Ale tylko przy przebudowie - test `oldExhaustiveSwitchThrowsMatchExceptionForNewVariant` woła kalkulator ze step2 (jak osobno zbudowany pakiet) z biletem ze step3 i dostaje `Error: Unexpected value`. Test `newVariantBreaksCompilationOfExhaustiveSwitch` pokazuje obie strony: ten sam kalkulator przeciw nowej unii to TS2345, a z `default: return 0` - zero błędów.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s13 2 3`

### Rozwiązanie i uzasadnienie

Unia dyskryminowana + wyczerpujący `switch` z `assertNever`. Wariant dodany w źródle jest obsłużony, bo inaczej `npm run typecheck` (i build) się nie uda.

### Pułapki

- `default` z wynikiem w `switch` po unii wyłącza sprawdzanie wyczerpania - wracamy do cichego błędu.
- Vitest i transpilacja bez sprawdzania typów (esbuild, `ts-node --transpile-only`) nie widzą braku `case` - zamknięcie działa tylko, jeśli `tsc --noEmit` jest częścią pipeline'u.
- Klasa wariantu bez pola `#` jest strukturalna: obcy obiekt z `kind: 'STUDENT'` i `basePrice()` przejdzie jako `StudentTicket`. Zamknięcie unii zależy od nominalności wariantów.
- Nowy wariant nie łamie starego zbudowanego kodu przy imporcie, ale stare `switch`e dojdą do `assertNever` w czasie działania - dotyczy wtyczek i pakietów budowanych osobno.

### Pytanie do sali

Kiedy otwarta hierarchia jest lepsza niż zamknięta unia? Kto w waszym systemie dopisuje implementacje spoza repozytorium?

## Scena s14. Współdzielenie implementacji a podtypowanie

**Temat ze slajdów:** 1.1-1.2. Hierarchia jest częścią zachowania; 9.1. Replace Inheritance with Composition - kiedy?
**Katalog:** `typescript/src/workshop/m5/s14_reuse` · **Test:** `scripts/warsztat.sh --lang ts test m5/s14` (`typescript/test/workshop/m5/s14_reuse/`)
**Czas:** ~10 min

### W skrócie

**Co robimy:** `CorporateAccount` dziedziczy po `LoyaltyAccount` tylko po to, by nie pisać drugi raz naliczania punktów, i blokuje wyjątkiem wymianę punktów na bilet. Wydzielamy logikę punktów do `PointsLedger`, zrywamy dziedziczenie, a raportowi dajemy wspólną rolę `PointsHolder`.

**Zasada:** Wspólny kod uzasadnia współpracownika, a dziedziczenie dopiero wspólny kontrakt. Podtyp musi móc wystąpić wszędzie tam, gdzie nadtyp, więc override rzucający `UnsupportedOperationError` to podręcznikowy sygnał złamanej substytucji. Zgodna sygnatura nie dowodzi zastępowalności.

**Efekt:** Implementację współdzieli kompozycja, a kontrakt - interfejs, więc operacji, której nie da się wykonać, po prostu nie ma. Klienci, którzy przyjmowali `LoyaltyAccount` i dostawali konto firmowe, przestaną się kompilować i każdego trzeba przejrzeć.

**Różnica względem Javy:** naliczanie punktów to `amount.dividedToIntegerBy(10).toNumber()` (odpowiednik `divideToIntegralValue(TEN).intValue()`). Test zamiast `isAssignableFrom` sprawdza `instanceof LoyaltyAccount` (false) i przypisanie `const holder: PointsHolder = corporate` (interfejs znika w czasie działania, więc to sprawdza kompilator), a brak metody - `'redeemFreeTicket' in corporate`. Typowanie w TS jest strukturalne, ale klienci `LoyaltyAccount` i tak przestaną przyjmować `CorporateAccount` po kroku 2: brakuje `redeemFreeTicket`, a pola `#` czynią klasę nominalną.

### Co widzimy

`CorporateAccount extends LoyaltyAccount` tylko po to, by nie pisać drugi raz naliczania punktów (1 pkt za pełne 10.00). Firma zbiera punkty do rocznego rabatu i nie wymienia ich na bilety, więc odziedziczoną operację blokuje wyjątkiem.

```typescript
override redeemFreeTicket(): boolean {
  throw new UnsupportedOperationError('konto firmowe nie wymienia punktów na bilety');
}
```

Zgodna sygnatura nie dowodzi zastępowalności: każdy klient `LoyaltyAccount` może dostać ten obiekt i wybuchnąć.

### Krok 1: Extract Delegate - współdzielona implementacja

**W IDE:** VS Code nie ma Extract Delegate - ręcznie. Nowa klasa `PointsLedger` w `PointsLedger.ts` z polem `#points` oraz metodami `earn`, `spend(amount)` i `points()`. `LoyaltyAccount` dostaje pole `readonly #ledger = new PointsLedger()` i deleguje do niego; publiczne API się nie zmienia.
**Po:**

```typescript
readonly #ledger = new PointsLedger();

redeemFreeTicket(): boolean {
  return this.#ledger.spend(100);
}
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s14` - 12 testów zielonych.
**Co powiedzieć:** kod, który chcieliśmy współdzielić, ma teraz własną klasę. Reużycie nie wymaga już dziedziczenia - można go użyć z dowolnego miejsca.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 0 1`

### Krok 2: Konto firmowe przez kompozycję + rola dla raportu

**W IDE:** w `CorporateAccount` usuń `extends LoyaltyAccount` i override `redeemFreeTicket`, dodaj własne pola `#company` i `#ledger` oraz metody `earn`, `owner`, `points`. Nowy plik `PointsHolder.ts` z interfejsem `owner()` i `points()`, obie klasy `implements PointsHolder`, `LoyaltyReport.line(account: PointsHolder)`. Wszystkie miejsca, które przekazywały konto firmowe jako `LoyaltyAccount`, wskaże `npm run typecheck`.
**Po:**

```typescript
export class CorporateAccount implements PointsHolder {
  readonly #company: string;
  readonly #ledger = new PointsLedger();
  ...
}
```

**Uruchom:** test zielony. `S14SolutionTest` sprawdza, że `CorporateAccount` nie jest `instanceof LoyaltyAccount`, przypisuje się do `PointsHolder` i nie ma `redeemFreeTicket`.
**Co powiedzieć:** wspólny kontrakt (właściciel i saldo) jest prawdziwy dla obu kont, więc tu podtypowanie jest uczciwe. Operacji, której nie da się wykonać, po prostu nie ma - nie trzeba jej blokować.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s14 1 2`

### Rozwiązanie i uzasadnienie

Implementację współdzieli `PointsLedger` (kompozycja), kontrakt - `PointsHolder` (interfejs). Dziedziczenie zostało tam, gdzie nie było potrzebne: nigdzie.

### Pułapki

- `UnsupportedOperationError` w override to podręcznikowy sygnał złamanej substytucji (tak samo `ReadOnlyAccount.withdraw`).
- Klienci, którzy przyjmowali `LoyaltyAccount` i dostawali konto firmowe, przestaną się kompilować - dobrze, każdy z nich trzeba przejrzeć. Uwaga na klientów w czystym JS albo z `any`: ci się nie wywrócą przy kompilacji, tylko przy wywołaniu `redeemFreeTicket` (`TypeError`).
- Wydzielony współpracownik nie powinien wiedzieć, kto go używa (żadnych `if (owner instanceof ...)`).

### Pytanie do sali

Pytania ze slajdów 1.1-1.2: czy te klasy mają ten sam kod, czy są wariantami jednego pojęcia? Jaka jest odpowiedź dla każdej pary w tej scenie?

## Scena s15. Zgodność zbudowanego kodu, refleksja i metadane

**Temat ze slajdów:** 1.3. Warstwy zgodności; 10.2-10.3. Zgodność binarna, refleksja i adnotacje
**Katalog:** `typescript/src/workshop/m5/s15_compatibility` · **Test:** `scripts/warsztat.sh --lang ts test m5/s15` (`typescript/test/workshop/m5/s15_compatibility/`)
**Czas:** ~15 min

### W skrócie

**Co robimy:** Biblioteka kasowa ma eksporter szukający metod oznaczonych kolumną `cena` tylko na prototypie klasy obiektu i API używane przez już zbudowane wtyczki partnerów. Zabezpieczamy "refleksję", wciągamy `price()` do bazy, uogólniamy `quoteStudent(StudentTicket)` do `quote(Ticket)` i przywracamy starą nazwę przestarzałą metodą delegującą.

**Zasada:** Zgodność ma kilka warstw: zachowanie, źródło, już zbudowany kod, refleksja, serializacja i integracje - i każda wymaga osobnego sprawdzenia. Pull Up jest zgodny dla wywołań (JS szuka metody w łańcuchu prototypów), ale zmienia klasę deklarującą widoczną dla przeglądu prototypów, a zmiana nazwy metody łamie każdy zbudowany kod, który jej używa. Pełny build sprawdza tylko kod, który budujemy sami.

**Efekt:** Eksport działa po przesunięciu metody, `quote` przyjmuje każdy bilet, a stare wtyczki działają dzięki przestarzałej metodzie delegującej. Przestarzała metoda zostaje na okres przejściowy, a jej usunięcie to osobna, zapowiedziana zmiana łamiąca.

**Różnica względem Javy:** warstwy zgodności przeniesione na świat JS/TS: "binaria" to już zbudowany JavaScript wtyczki partnera (plik `typescript/test/workshop/m5/s15_compatibility/plugin/Plugin.js` z `Plugin.d.ts`, jak w pakiecie npm), który dostaje klasy API z zewnątrz (odpowiednik podmiany JAR-a); "źródło" to przebudowa wtyczki kompilatorem TS w pamięci. Adnotacja `@Column("cena")`: dekoratory TC39 nie działają jeszcze w całym łańcuchu narzędzi (Node 22, vitest), więc stabilny kontrakt sceny `Column.ts` przypina metadane do obiektu funkcji, a klasa oznacza metodę w bloku statycznym `static { column(this.prototype.price, 'cena'); }` - metadane należą do klasy, która metodę deklaruje, jak adnotacja metody w Javie. `getDeclaredMethods()` to własne nazwy prototypu obiektu, `getMethods()` - cały łańcuch prototypów. W JS metoda jest identyfikowana wyłącznie **nazwą** (brak deskryptorów z typami), więc przeciążenie `quote(StudentTicket)` to w starcie `quoteStudent(ticket: StudentTicket)`, a Generalize Parameter Type w kroku 3 daje `quote(ticket: Ticket)` - stara nazwa znika. Inny wynik niż w Javie: krok 3 w Javie jest zgodny źródłowo, w TS stare źródło wtyczki NIE przebuduje się ze step3 (TS2339) - stąd test `generalizedParameterIsSourceCompatibleOnlyAfterRename`.

### Co widzimy

Biblioteka kasowa: `StandardTicket` i `StudentTicket` deklarują własne `price()` oznaczone kolumną `cena`. `TicketExporter` szuka kolumn tylko wśród własnych nazw prototypu obiektu. `BoxOfficeApi.quoteStudent(StudentTicket)` jest używane przez **zbudowane** wtyczki partnerów. Ta scena jest nietypowa: główną demonstracją jest `S15SolutionTest`, który uruchamia gotowy `Plugin.js` z klasami API z różnych kroków (jak podmiana pakietu biblioteki na serwerze) i przebudowuje źródło wtyczki kompilatorem TS przeciw wybranej wersji API.

```typescript
// Tylko metody zadeklarowane w klasie runtime (odpowiednik getDeclaredMethods()).
static #candidates(ticket: Ticket): Array<[string, unknown]> {
  const prototype: object = Object.getPrototypeOf(ticket);
  return Object.getOwnPropertyNames(prototype).map((name) => [name, Reflect.get(prototype, name)]);
}
```

### Krok 1: Przygotowanie refleksji przed ruchem w hierarchii

**W IDE:** w `TicketExporter.#candidates` zamień przegląd jednego prototypu na pętlę po całym łańcuchu prototypów (do `Object.prototype`); nazwa znaleziona najniżej zasłania tę samą nazwę wyżej, jak nadpisanie.
**Po:**

```typescript
const found = new Map<string, unknown>();
for (let prototype: object | null = Object.getPrototypeOf(ticket);
  prototype !== null && prototype !== Object.prototype;
  prototype = Object.getPrototypeOf(prototype)) {
  for (const name of Object.getOwnPropertyNames(prototype)) {
    if (!found.has(name)) {
      found.set(name, Reflect.get(prototype, name));
    }
  }
}
return [...found];
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s15` - 20 testów zielonych.
**Co powiedzieć:** kod szukający metod po nazwie albo metadanych to ukryty kontrakt na klasę deklarującą. Zabezpieczamy go przed ruchem, a nie po awarii.
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 0 1`

### Krok 2: Pull Up price() z metadanymi kolumny

**W IDE:** ciała `price()` są różne, więc najpierw ⌃⇧R > Extract to method na procencie zniżki (`discountPercent()`), potem ręcznie przenieś `price()` **razem z blokiem statycznym `column(...)`** do `Ticket`, a w bazie dopisz `protected abstract discountPercent(): number`. Podklasy zostają z `protected override discountPercent()`. (W Javie `price()` dostaje `final`; w TS nie ma takiej możliwości.)
**Po:**

```typescript
static {
  column(this.prototype.price, 'cena');
}

price(): Money {
  return this.#basePrice.minus(this.#basePrice.percent(this.discountPercent()));
}

protected abstract discountPercent(): number;
```

**Uruchom:** test zielony. `pullUpIsBinaryCompatibleForCallers`: wtyczka zbudowana przeciw step1 działa z API ze step2 (JS znajduje `studentTicket.price()` w prototypie `Ticket`). `pullUpHidesAnnotatedMethodFromDeclaredMethodsLookup`: prototyp `StudentTicket` nie ma już metody z kolumną - eksporter ze start by ją zgubił.
**Co powiedzieć:** zgodność dla wywołań nie oznacza zgodności "refleksyjnej". Metadane przypięte do metody idą razem z nią do klasy, która ją deklaruje - kto szuka ich na podklasie, nic nie znajdzie. Blok statyczny zapomniany w podklasie przy Pull Up to cicha utrata kolumny w eksporcie.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 1 2`

### Krok 3: Generalize Parameter Type - zmiana łamiąca zbudowany kod

**W IDE:** w `BoxOfficeApi` zmień `quoteStudent(ticket: StudentTicket)` na `quote(ticket: Ticket)` (F2 na nazwie, potem ręcznie typ parametru). W repozytorium wszystko się kompiluje, bo Rename Symbol poprawił wywołania.
**Po:**

```typescript
quote(ticket: Ticket): Money {
  return ticket.price();
}
```

**Uruchom:** test zielony. `generalizedParameterBreaksOldBinary`: zbudowana wtyczka dostaje `TypeError: ... quoteStudent is not a function` (odpowiednik `NoSuchMethodError`). `generalizedParameterIsSourceCompatibleOnlyAfterRename`: stare źródło wtyczki przeciw step3 daje TS2339, a przepisane na `quote(...)` - zero błędów. Wariant `start` w `S15EquivalenceTest` woła API przez pomocnika `quoteStudentOf` (`quoteStudent`, a gdy go nie ma - `quote`), więc krok na żywo go nie psuje.
**Co powiedzieć:** w Javie ten krok był zgodny źródłowo i łamał tylko binaria. W TS nazwa jest całym "deskryptorem", więc pęka też źródło - build w repozytorium to wykryje, ale nie przebuduje cudzych wtyczek. Dopowiedz: samo uogólnienie typu parametru **przy tej samej nazwie** byłoby w TS zgodne na wszystkich warstwach (typy są wymazywane) - niebezpieczne są zmiany nazw i kształtu.
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 2 3`

### Krok 4: Przestarzała metoda delegująca

**W IDE:** dodaj z powrotem `quoteStudent(ticket: StudentTicket)` z komentarzem JSDoc `@deprecated od 2.0 - użyj quote(ticket).`, delegujące do `this.quote(ticket)`.
**Po:**

```typescript
/**
 * Zgodność ze zbudowanymi wtyczkami 1.x.
 * @deprecated od 2.0 - użyj quote(ticket).
 */
quoteStudent(ticket: StudentTicket): Money {
  return this.quote(ticket);
}
```

**Uruchom:** test zielony. `delegatingOverloadRestoresBinaryCompatibility`: stara wtyczka działa z API ze step4, a jej źródło znów się przebudowuje.
**Co powiedzieć:** stara nazwa zostaje na okres przejściowy, a usunięcie to osobna, zapowiedziana zmiana łamiąca. VS Code przekreśla wywołania `quoteStudent`, więc nowi klienci widzą, że mają przejść na `quote` - ale nowy kod z typem `StudentTicket` może nadal odruchowo sięgać po wariant z nazwą "po typie" (scena s09).
**Snapshot:** `step4/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s15 3 4`

### Rozwiązanie i uzasadnienie

Eksporter odporny na przesunięcia metod, `price()` w bazie, API przyjmujące `Ticket` z przejściową metodą o starej nazwie. Każda warstwa zgodności jest sprawdzona osobnym testem.

### Pułapki

- Zgodność zbudowanego kodu nie dowodzi zgodności źródłowej ani zachowania - i odwrotnie.
- Push Down, zmiana nazwy metody, usunięcie klasy albo eksportu - łamią zbudowany kod. Pull Up, dodanie metody, zmiana samego typu parametru - zwykle nie (ale zawężenie typu wyniku czy parametru łamie klientów przy przebudowie).
- Klasa deklarująca metodę, własne nazwy prototypu i metadane przypięte do metod zmieniają się przy każdym ruchu członka.
- Narzędzia porównujące publiczne API pakietu (np. API Extractor, `are-the-types-wrong`, porównanie plików `.d.ts` w CI) sprawdzają zgodność automatycznie; zgodności już zbudowanego JS pilnuje dopiero test uruchamiający stare wtyczki.

### Pytanie do sali

Którą warstwę zgodności z tabeli 1.3 sprawdza wasz obecny pipeline, a której nie sprawdza żadna automatyzacja?

## Scena s16. Serializacja i proxy - hierarchia jako format danych

**Temat ze slajdów:** 10.4-10.6. Serializacja, ORM i DI
**Katalog:** `typescript/src/workshop/m5/s16_serializationproxy` · **Test:** `scripts/warsztat.sh --lang ts test m5/s16` (`typescript/test/workshop/m5/s16_serializationproxy/`)
**Czas:** ~12 min

### W skrócie

**Co robimy:** Wydzielenie nadklasy z polami tytułu i miejsca w zapisywanym do JSON `StudentTicket` zmienia nazwy pól, więc stare dane czytają się bez wyjątku, ale z `undefined` w tych polach. Wprowadzamy proxy serializacji (`toJSON`/`fromJSON`) z wersją formatu, a dla serwisu cenowego z polem `#` wydzielamy interfejs, żeby dało się go opakować proxy.

**Zasada:** Domyślna serializacja obiektu zapisuje jego pola pod ich nazwami, więc struktura klasy (nazwy i miejsce pól) jest formatem danych, a brak wersji formatu nie przenosi stanu między starą a nową postacią. Proxy serializacji oddziela format od struktury klas. Opakowanie serwisu (audyt, cache, transakcje) wymaga roli, a nie konkretnej klasy - klasa z polami `#` nie przyjmie ani obiektu delegującego, ani przezroczystego `Proxy`.

**Efekt:** Przyszłe Pull Up i Push Down nie zmienią postaci JSON, a proxy audytowe działa bez rezygnacji z pól `#`. Zachowanie świadomie się zmienia: stare dane są odrzucane głośno zamiast czytane z utratą pól, a ich migracja to osobne zadanie.

**Różnica względem Javy:** JS nie ma serializacji obiektów z segmentem na każdy poziom hierarchii ani `serialVersionUID`. Formatem danych jest JSON, a `JSON.stringify` zapisuje **własne** pola obiektu pod ich nazwami (pola `#` pomija): hierarchia jako taka do JSON nie trafia, ale nazwy i miejsce pól - tak. Stabilny kontrakt sceny `SessionStore.ts` gra rolę `ObjectOutputStream`/`ObjectInputStream`: `save(value)` zapisuje `{type, data}` (data przez `toJSON()`, jeśli klasa go ma - odpowiednik `writeReplace`), `load(json, ...types)` używa statycznego `fromJSON` klasy (odpowiednik `readResolve`) albo odtwarza obiekt **bez konstruktora** (`Object.create` + kopiowanie pól po nazwach, z pominięciem kluczy będących nazwami metod) - jak domyślna deserializacja Javy. `InvalidClassError` to odpowiednik `InvalidClassException`, a "plik `.ser` z v1" to stała JSON w teście. Pola startu są prywatne tylko dla TS (`private readonly title`), bo pola `#` nie trafiłyby do JSON. Krok 3: w JS `Proxy` opakuje każdy obiekt i nie ma `final`, więc pułapka startu to pole prywatne ES `#studentDiscountPercent` w `TicketPricing` (odpowiednik `final` dodanego "dla bezpieczeństwa"); test `jdkProxyCannotWrapFinalClassWithoutInterface` nazywa się w porcie `proxyCannotWrapClassWithPrivateFieldsWithoutInterface`.

### Co widzimy

`StudentTicket` z polami `title`, `seat`, `studentId` (prywatnymi tylko dla kompilatora TS) zapisywany przez `SessionStore` - sesja, cache, kolejka. Postać JSON to nazwy pól obiektu. `TicketPricing` to klasa z polem `#studentDiscountPercent` i bez interfejsu. Scena nietypowa: główną demonstracją jest `S16SolutionTest` z "danymi z v1" zamrożonymi jako stała (bilet zapisany przez klasę ze start):

```typescript
const SAVED_BY_V1 = '{"type":"StudentTicket","data":{"title":"Amator","seat":"F3","studentId":"S-123"}}';
```

```typescript
export class TicketPricing {
  readonly #studentDiscountPercent = 25;

  studentPrice(basePrice: Money): Money { ... }
}
```

### Krok 1: Extract Superclass + Pull Up Field - ciche zgubienie danych

**W IDE:** ręcznie (Extract Superclass w VS Code nie ma): nowa `abstract class Ticket` z polami tytułu i miejsca oraz akcesorami `title()`/`seat()`. TypeScript nie pozwala, by pole i metoda miały tę samą nazwę (TS2300), więc pola nazywają się `_title` i `_seat`. `StudentTicket extends Ticket` z `super(title, seat)`.
**Po:**

```typescript
export abstract class Ticket {
  private readonly _title: string;
  private readonly _seat: string;
  ...
  title(): string {
    return this._title;
  }
```

**Uruchom:** `scripts/warsztat.sh --lang ts test m5/s16` - 13 testów zielonych. `pulledUpFieldsAreSilentlyLostWhenReadingOldData`: stare dane odczytane przez step1 dają `'undefined undefined (legitymacja S-123)'`. Bez wyjątku.
**Co powiedzieć:** zmienił się format - klucze `title` i `seat` ze starych danych trafiają teraz na nazwy metod i są pomijane, a `_title` i `_seat` zostają `undefined`. Nic w danych nie mówi o wersji formatu, więc nikt nie rzuca wyjątku - to najgorsza możliwa kombinacja (w Javie ten sam efekt daje stały `serialVersionUID`: `null null ...`).
**Snapshot:** `step1/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 0 1`

### Krok 2: Serialization Proxy

**W IDE:** ręcznie: w `StudentTicket` interfejs `SerializedForm { v, title, seat, studentId }`, metoda `toJSON()` zwracająca płaską postać z `v: 2`, statyczne `fromJSON(data)` sprawdzające wersję i wołające publiczny konstruktor (inna wersja -> `InvalidClassError`). `Ticket` przestaje być częścią formatu, więc jego pola mogą przejść na `#title`/`#seat`, a `studentId` - na `#studentId`.
**Po:**

```typescript
/** Odpowiednik readResolve: odczyt tylko przez postać serializowaną i publiczny konstruktor. */
static fromJSON(data: unknown): StudentTicket {
  const form = data as Partial<SerializedForm>;
  if (form.v !== StudentTicket.#SERIAL_VERSION) {
    throw new InvalidClassError(`StudentTicket: nieobsługiwana wersja formatu ${String(form.v)}`);
  }
  return new StudentTicket(String(form.title), String(form.seat), String(form.studentId));
}

/** Odpowiednik writeReplace: do JSON trafia płaska postać, a nie pola klas. */
toJSON(): SerializedForm {
  return { v: StudentTicket.#SERIAL_VERSION, title: this.title(), seat: this.seat(), studentId: this.#studentId };
}
```

**Uruchom:** test zielony. `serializationProxyRejectsOldDataLoudly`: stare dane odrzucone `InvalidClassError`. `serializationProxyWritesFlatFormIndependentOfHierarchy`: w JSON są dokładnie klucze `v`, `title`, `seat`, `studentId`, bez `_title`.
**Co powiedzieć:** hierarchia klasy przestaje być formatem danych - przyszłe Pull Up/Push Down nie zmienią JSON. Wersja `v: 2` to świadoma deklaracja nowego formatu: błąd głośny zamiast cichego. Migracja starych danych to osobne zadanie (czytnik v1 w `fromJSON` albo konwersja offline). Odpowiednik `readObject` rzucającego `InvalidObjectException` jest zbędny: `SessionStore` zawsze używa `fromJSON`, gdy klasa go ma.
**Snapshot:** `step2/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 1 2`

### Krok 3: Extract Interface dla proxy

**W IDE:** ręcznie nowy plik `Pricing.ts` z interfejsem `studentPrice(basePrice: Money): Money`, `TicketPricing implements Pricing`. Pole `#studentDiscountPercent` może zostać. Klienci i kontener DI zależą od `Pricing`.
**Po:**

```typescript
export interface Pricing {
  studentPrice(basePrice: Money): Money;
}
```

**Uruchom:** test zielony. `proxyCannotWrapClassWithPrivateFieldsWithoutInterface`: obiekt delegujący nie typuje się jako `TicketPricing` (TS2741 - brakuje pola `#studentDiscountPercent`, sprawdzane kompilacją w pamięci), a przezroczysty `new Proxy(pricing, {})` rzuca `TypeError`, bo metoda wołana przez proxy ma `this = proxy`, które nie ma pola `#`. `extractedInterfaceAllowsDynamicProxy`: proxy audytowe typowane jako `Pricing` woła metody na prawdziwym obiekcie i liczy wywołania.
**Co powiedzieć:** pole `#` czyni klasę nominalną i zamyka ją przed opakowaniem - tak jak `final` w Javie zamyka ją przed proxy klasowym (CGLIB/ByteBuddy). Rola pozwala opakować serwis bez dotykania jego pól. W świecie TS to samo dotyczy kontenerów DI i bibliotek AOP/ORM opartych na `Proxy` (np. lazy loading w ORM, obserwowalny stan w bibliotekach UI).
**Snapshot:** `step3/` · **Różnica:** `scripts/warsztat.sh --lang ts diff m5/s16 2 3`

### Rozwiązanie i uzasadnienie

Zapisywana postać to płaski obiekt z wersją, niezależny od hierarchii. Serwis ma interfejs roli, który kontener DI może opakować bez dziedziczenia.

### Pułapki

- Pola `#` dodane "dla bezpieczeństwa" przy refaktoryzacji potrafią wyłączyć audyt, cache i lazy loading oparte na `Proxy` - a przy serializacji po cichu wypadają z JSON.
- Self-invocation: `this.innaMetoda()` wewnątrz serwisu omija proxy (także po Extract Interface).
- Hierarchia encji w ORM (TypeORM, MikroORM) to migracja modelu: strategia dziedziczenia, dyskryminator, tabele, zapytania polimorficzne. Test z prawdziwą bazą: zapis, wyczyszczenie kontekstu, ponowny odczyt.
- DI po Extract Interface: interfejs znika w czasie działania, więc kontener potrzebuje tokenu; dwie implementacje tej samej roli pod jednym tokenem to niejednoznaczne wstrzykiwanie - uruchom kontener w teście.

### Pytanie do sali

Gdzie wasz system trzyma obiekty zapisane jako JSON z pól klas (sesje, cache, kolejki, `localStorage`)? Czy macie choć jeden zapis z poprzedniej wersji w testach?

## Proponowana kolejność pokazu

**Ścieżka krótka (~80 min)** - rdzeń katalogu refaktoryzacji plus dwie najgroźniejsze pułapki:

1. s01 Pull Up Method (12 min)
2. s03 Push Down i asymetria (12 min)
3. s04 Extract Superclass (15 min)
4. s06 Extract Interface i wspólne zachowanie roli (12 min)
5. s08 Kompozycja i self-use (12 min)
6. s09 Wybór wariantu po typie deklarowanym po Extract Superclass (10 min)
7. s15 tylko kroki 3-4 z `scripts/warsztat.sh --lang ts jump m5/s15 2` (7 min)

**Ścieżka pełna (~3 h 10 min, z przerwą)** - według agendy slajdów:

1. Semantyka języka (TS/JS): s09, s10, s11, s12, s13 (~48 min)
2. Pull Up / Push Down: s01, s02, s03 (~34 min)
3. Ekstrakcje: s04, s05, s06 (~42 min)
4. Collapse i kompozycja: s07, s14, s08 (~30 min)
5. Zgodność i integracje: s15, s16 (~27 min)

Jeśli brakuje czasu, sceny s02, s07 i s10 dobrze działają jako praca własna uczestników (zadania modułu 5).
