# Moduł 5. Refaktoryzacje hierarchii klas - warsztat CineLegacy (TypeScript): zadania

Każda scena to mały fragment systemu kina CineLegacy w katalogu `typescript/src/workshop/m5/sNN_.../start`, a testy (Vitest) leżą w `typescript/test/workshop/m5/sNN_.../`. Pracuj wyłącznie w `start`, małymi krokami, i po każdym ruchu uruchamiaj test sceny (`scripts/warsztat.sh --lang ts test m5/sNN` albo przycisk Run przy teście w rozszerzeniu Vitest w VS Code). Test równoważności (`SNNEquivalenceTest`) ma być zielony przez cały czas. W scenach o pułapkach (s08-s11, s13) testy o nazwie `start...` dokumentują błąd w kodzie wyjściowym - gdy go usuniesz, te testy zrobią się czerwone i to jest oczekiwany efekt. Vitest nie sprawdza typów, więc tam, gdzie zadanie opiera się na kompilatorze, uruchamiaj też `npm run typecheck` w katalogu `typescript`. Nie zaglądaj do katalogów `stepN` przed zakończeniem zadania; potem porównaj swoje rozwiązanie z ostatnim krokiem (`scripts/warsztat.sh --lang ts diff m5/sNN 0 N`). Chcesz zacząć od nowa: `scripts/warsztat.sh --lang ts reset m5/sNN`.

VS Code nie ma automatycznych refaktoryzacji hierarchii (Pull Members Up, Push Members Down, Extract Superclass, Extract Interface, Replace Inheritance with Delegation, Safe Delete). Masz do dyspozycji Rename Symbol (F2), Refactor... (⌃⇧R: Extract to method / function / constant, Move to file), Quick Fix (⌘.: m.in. Implement interface) i Find All References (⇧⌥F12). Ruchy na hierarchii rób ręcznie tak, by po każdym dało się uruchomić test, a listę miejsc do poprawy bierz z `npm run typecheck`. TypeScript nie ma metod ani klas `final` - tam, gdzie zadanie w Javie wymaga `final`, zapisz decyzję w komentarzu.

## Scena s01. Pull Up Method
**Katalog:** `typescript/src/workshop/m5/s01_pullupmethod/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s01`

**Zasada:** Pull Up Method przenosi metodę na najniższy poziom, na którym jest prawdziwa dla wszystkich potomków. Porównuje się kontrakty metod, a nie ich tekst, a przenieść w górę da się tylko metody o identycznych ciałach.

**Zadanie:**
1. Usuń trzy kopie metody `label()` z podklas `Ticket`, zostawiając jedną implementację.
2. Klient `BoxOffice` ma wywoływać `label()` przez typ bazowy.
3. Zdecyduj, czy wspólna metoda może być nadpisywana, i zapisz decyzję (TS nie ma `final`).

**Podpowiedź:** w górę przeniesiesz tylko metody o identycznych ciałach (operator `+`, `util.format` i `join('')` dają ten sam tekst), a wspólna metoda potrzebuje czegoś, czego baza jeszcze nie zna.
**Gotowe, gdy:** test zielony, `label()` jest zadeklarowane tylko w `Ticket`, a podklasy zawierają wyłącznie regułę ceny.

## Scena s02. Pull Up Field
**Katalog:** `typescript/src/workshop/m5/s02_pullupfield/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s02`

**Zasada:** Pull Up Field łączy pola tylko przy tym samym znaczeniu, typie, cyklu życia i momencie inicjalizacji. Pole w bazie powinno być prywatne i ustawiane przez konstruktor bazy, a nie `protected`.

**Zadanie:**
1. Przenieś informację o miejscu na sali do `Ticket` jako jedno pole.
2. Pole w bazie ma być prywatne (`#seat`) i `readonly`, ustawiane przez konstruktor bazy.
3. Nie przenoś pól, które mają inne znaczenie.

**Podpowiedź:** zanim połączysz pola, porównaj ich nazwę, typ, znaczenie, cykl życia i sposób inicjalizacji. Każda różnica to osobny krok przygotowawczy.
**Gotowe, gdy:** test zielony, żadna podklasa nie deklaruje pola miejsca, nie ma settera, a normalizacja miejsca VIP działa jak wcześniej.

## Scena s03. Push Down Method/Field
**Katalog:** `typescript/src/workshop/m5/s03_pushdown/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s03`

**Zasada:** Push Down zawęża zbyt szeroki kontrakt bazy do gałęzi, która naprawdę potrzebuje członka. Sygnałem jest `UnsupportedOperationError` lub wywołanie tylko po `instanceof`, a w bibliotece taki ruch łamie już zbudowany kod klientów.

**Zadanie:**
1. Usuń z `Ticket` operację dopłaty VIP i związany z nią stan - ma zostać tylko tam, gdzie ma sens.
2. Usuń override rzucający `UnsupportedOperationError`.
3. Zapisz w komentarzu, co ta zmiana oznaczałaby dla zbudowanych klientów biblioteki (opublikowany pakiet npm).

**Podpowiedź:** kolejność ma znaczenie: klienci, potem zachowanie korzystające z pola, na końcu pole.
**Gotowe, gdy:** test zielony, `Ticket` nie ma `upgradeToVip()` ani pola VIP, `BoxOffice` nie używa `instanceof`.

## Scena s04. Extract Superclass
**Katalog:** `typescript/src/workshop/m5/s04_extractsuperclass/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s04`

**Zasada:** Extract Superclass ma sens, gdy klasy są wariantami jednego pojęcia ze wspólnym kontraktem, a nie tylko mają podobny kod. W TS podklasa bez własnego konstruktora dziedziczy konstruktor bazy (razem z `protected`), więc publiczne sygnatury trzeba zachować świadomie.

**Zadanie:**
1. Wydziel wspólną nadklasę dla seansu i wynajmu sali z nazwą opisującą pojęcie domenowe.
2. Dołączaj klasy do hierarchii pojedynczo, z testem po każdej.
3. Usuń trzy kopie warunku kolizji w `HallPlanner`.
4. Zachowaj publiczne konstruktory, fabrykę `rental(...)` i sygnaturę `conflicts(...)`.

**Podpowiedź:** zacznij od jednej klasy: nowy plik z klasą abstrakcyjną, przeniesione pola `#` i akcesory, `super(...)`. Algorytm kolizji potrzebuje od podklas tylko nazwy do komunikatu.
**Gotowe, gdy:** test zielony (w tym przypadek "styk 20:00 to nie konflikt"), nadklasa jest abstrakcyjna i ma prywatne pola, `HallPlanner` ma jedną pętlę.

## Scena s05. Extract Subclass
**Katalog:** `typescript/src/workshop/m5/s05_extractsubclass/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s05`

**Zasada:** Extract Subclass tworzy podklasę dla stabilnego podzbioru instancji z dodatkowym stanem lub zachowaniem. Wariant zmienny w czasie życia obiektu to State lub Strategy, a nie podklasa.

**Zadanie:**
1. Wydziel podklasę dla premier i przenieś do niej stan i zachowanie, które dotyczą tylko premier.
2. Usuń flagę `#premiere` i wszystkie `if (premiere)`.
3. Klient `Programme` nie może przekazywać `null` ani flagi.

**Podpowiedź:** zacznij od punktów tworzenia obiektów - to one mają wybierać klasę obiektu. Jeśli fabryka w bazie tworzy podklasę, trzymaj obie klasy w jednym pliku - osobny plik da cykl importów ES.
**Gotowe, gdy:** test zielony, zwykły seans nie ma pola gościa, a fabryka premiery zwraca obiekt podklasy.

## Scena s06. Extract Interface i metody domyślne
**Katalog:** `typescript/src/workshop/m5/s06_extractinterface/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s06`

**Zasada:** Extract Interface wydziela rolę potrzebną konkretnym klientom, a nie kopię całego API klasy. Wspólne zachowanie roli (w Javie metoda `default`) musi być poprawne dla każdej implementacji i korzystać tylko z operacji kontraktu.

**Zadanie:**
1. Wydziel interfejs roli, której potrzebuje koszyk - tylko z operacjami, których `Cart` używa.
2. `Cart` ma mieć jedną listę pozycji i jedno `add(...)` zamiast `addTicket`/`addSnack`.
3. Przenieś liczenie kwoty VAT pozycji obok interfejsu, nie zmieniając `Ticket` ani `Snack`.

**Podpowiedź:** interfejs TS nie ma metod `default`. Nowa wymagana składowa interfejsu złamie każdą klasę z `implements` - jak inaczej dać roli wspólne zachowanie?
**Gotowe, gdy:** test zielony, `Cart` nie importuje `Ticket` ani `Snack`, interfejs ma tylko `price` i `vatPercent`, a nowa implementacja roli (np. okulary 3D za 3.00 z VAT 23%) działa bez zmian w koszyku.

## Scena s07. Collapse Hierarchy
**Katalog:** `typescript/src/workshop/m5/s07_collapsehierarchy/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s07`

**Zasada:** Collapse Hierarchy scala poziomy, gdy rozróżnienie nie jest już kontraktem, wariantem ani punktem rozszerzenia. Pusty typ może jednak być markerem lub kontraktem konfiguracji, więc trzeba to sprawdzić przed usunięciem.

**Zadanie:**
1. Usuń zbędny poziom hierarchii sal, zachowując nazwę klasy używaną przez klientów.
2. Wiedza o regule VIP w sali IMAX nie może zniknąć.
3. Opisz w komentarzu, co trzeba by sprawdzić, zanim zrobi się to w bibliotece.

**Podpowiedź:** najpierw pozbądź się metod, które tylko wołają `super`. Jedyna różnica między klasami to sposób tworzenia.
**Gotowe, gdy:** test zielony, istnieje jedna klasa `Hall` (plik `ImaxHall.ts` usunięty), sala IMAX ma VIP od przedostatniego rzędu.

## Scena s08. Replace Inheritance with Composition
**Katalog:** `typescript/src/workshop/m5/s08_composition/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s08`

**Zasada:** Kompozycja zastępuje dziedziczenie, gdy `extends` służy tylko do ponownego użycia kodu. Dziedziczenie wiąże nas z self-use, czyli z tym, które metody wołają inne metody na `this`.

**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego `addAll` z dwoma miejscami daje 4 kliknięcia.
2. Zastąp dziedziczenie po `Set<string>` delegatem.
3. Zadbaj, by żadna metoda nie wydawała delegata na zewnątrz.
4. Wypisz operacje, które klienci świadomie tracą.

**Podpowiedź:** VS Code nie ma Replace Inheritance with Delegation - zrób to ręcznie i sprawdź, co zwróciłby getter "wygenerowany" dla delegata. Pamiętaj, że `Set.add` zwraca zbiór, a nie `boolean`, i że `size` zbioru to właściwość, a nie metoda.
**Gotowe, gdy:** testy nie-`start` zielone, `addAll` liczy każde miejsce raz, `SeatSelection` nie jest `instanceof Set`, a lista miejsc zwracana klientowi jest zamrożona (`push` rzuca `TypeError`).

## Scena s09. Overriding a overloading
**Katalog:** `typescript/src/workshop/m5/s09_overloading/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s09`

**Zasada:** Override wybiera JS w czasie działania według prototypu obiektu, a wariant wybrany po typie deklarowanym (w TS osobna nazwa, np. `priceStudent`) to decyzja zapisana na stałe w kodzie klienta. Kontrakt zapisany typem funkcyjnym zamienia pomyłkę w parametrze nadpisania w błąd kompilacji.

**Zadanie:**
1. Uruchom test i wyjaśnij, dlaczego student w `Checkout.total(tickets: readonly Ticket[])` płaci pełną cenę, choć `PriceList` ma wariant dla `StudentTicket`.
2. Popraw model tak, by zniżka zależała od klasy obiektu biletu.
3. Popraw porównywanie biletów tak, by `alreadyInCart` znajdowało równy bilet.

**Podpowiedź:** kto wybiera `price` albo `priceStudent`, a kto override? `Array.includes` nie woła `equals`. Zajrzyj do `s09_overloading/Equatable.ts` - dlaczego `equals` jest tam właściwością z typem funkcyjnym, a nie metodą?
**Gotowe, gdy:** testy nie-`start` zielone, `PriceList` ma jedno `price(...)`, `Ticket implements Equatable` z `equals(other: unknown)` i `key()`, a `npm run typecheck` przechodzi.

## Scena s10. Ukrywanie pól i metod static
**Katalog:** `typescript/src/workshop/m5/s10_fieldhiding/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s10`

**Zasada:** Pola prywatne ES i metody `static` wywoływane przez nazwę klasy nie są polimorficzne - są związane z klasą, w której stoi kod. Pole `#` redeklarowane w podklasie to drugi, niezależny slot w tym samym obiekcie.

**Zadanie:**
1. Wyjaśnij, dlaczego `new StudentTicket().label()` zwraca `'BILET: NORMAL'`.
2. Usuń ukrywanie pola.
3. Usuń ukrywanie metody statycznej.

**Podpowiedź:** `label()` stoi w `Ticket` - który slot `#type` czyta i którą `category()` woła? Czym różniłoby się pole publiczne?
**Gotowe, gdy:** testy nie-`start` zielone, `label()` studenta zwraca `'BILET ULGOWY: STUDENT'` niezależnie od typu referencji.

## Scena s11. Konstruktor wołający metodę nadpisywalną
**Katalog:** `typescript/src/workshop/m5/s11_constructorcall/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s11`

**Zasada:** Konstruktor nadklasy wykonuje się, zanim powstaną pola podklasy, więc wywołany z niego override widzi obiekt w połowie zbudowany. Bezpieczny konstruktor nie woła metod nadpisywalnych.

**Zadanie:**
1. Znajdź przyczynę `undefined` w etykiecie biletu VIP. Sprawdź, co by się stało, gdyby pole `lounge` było polem `#lounge` (test `startWithPrivateNameFieldFailsFast`).
2. Napraw błąd najpierw lokalnie w `VipTicket`, potem strukturalnie w `Ticket`.

**Podpowiedź:** czy da się przypisać pole przed `super(...)`, jak w Javie 25? Jeśli nie - co jeszcze może zrobić sama podklasa? Czy to wystarczy dla następnej podklasy?
**Gotowe, gdy:** testy nie-`start` zielone, konstruktor `Ticket` nie woła metod nadpisywalnych, a `VipTicket` nie nadpisuje `label()`.

## Scena s12. Generyki, wymazywanie typów i brak metod bridge
**Katalog:** `typescript/src/workshop/m5/s12_bridgemethods/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s12`

**Zasada:** TypeScript wymazuje generyki i typy parametrów całkowicie - w czasie działania nie ma ani `T`, ani (jak w Javie) metod bridge. Parametry metod są sprawdzane biwariantnie, więc `PriceRule<StudentTicket>` da się przypisać do `PriceRule<Ticket>` i wywołać ze złym biletem bez żadnego rzutowania.

**Zadanie:**
1. Wydziel generyczny interfejs reguły cenowej `PriceRule<T extends Ticket>`, a rejestr niech przechowuje `PriceRule<Ticket>`.
2. Test zostanie zielony - przeczytaj `S12SolutionTest` i wyjaśnij, dlaczego to jest pułapka (co się stanie z regułą o nazwie `DiscountRule` i z biletem normalnym podanym `StudentRule`).
3. Usuń zgadywanie typu biletu z nazwy klasy reguły.

**Podpowiedź:** jaki typ biletu istnieje jeszcze w czasie działania, skoro `T` zniknęło? Konstruktor klasy jest wartością.
**Gotowe, gdy:** test zielony, `RuleRegistry` nie używa `constructor.name` do rejestracji ani rzutowania `as` przy `apply`, zły bilet kończy się `TypeError` w jednym miejscu, a brak reguły - czytelnym `IllegalStateError`.

## Scena s13. Hierarchie zamknięte
**Katalog:** `typescript/src/workshop/m5/s13_sealed/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s13`

**Zasada:** Unia dyskryminowana zamyka listę wariantów, a `switch` po polu `kind` z `default: return assertNever(ticket)` pozwala kompilatorowi sprawdzić, czy obsłużono wszystkie warianty. Ochrona działa tylko przy przebudowie kodu, który używa `switch`, i tylko wtedy, gdy ktoś uruchamia `tsc`.

**Zadanie:**
1. Zamknij hierarchię biletów.
2. Zastąp łańcuch `instanceof` wyczerpującym `switch` bez gałęzi z wynikiem domyślnym.
3. Dodaj bilet dziecięcy (40%) i pozwól kompilatorowi wskazać, co trzeba obsłużyć.

**Podpowiedź:** po kroku 3 przeczytaj test `oldExhaustiveSwitchThrowsMatchExceptionForNewVariant` - co się dzieje ze zbudowanym wcześniej `switch`? A `newVariantBreaksCompilationOfExhaustiveSwitch` - co zmienia `default: return 0`?
**Gotowe, gdy:** testy nie-`start` zielone, `npm run typecheck` przechodzi, bilet dziecięcy za 25.00 kosztuje 15.00, w `PriceCalculator` nie ma `instanceof` ani `default` z wynikiem.

## Scena s14. Współdzielenie implementacji a podtypowanie
**Katalog:** `typescript/src/workshop/m5/s14_reuse/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s14`

**Zasada:** Wspólny kod uzasadnia współpracownika, a dziedziczenie dopiero wspólny kontrakt. Podtyp, który blokuje odziedziczoną operację wyjątkiem, łamie zasadę podstawienia.

**Zadanie:**
1. Wskaż, który kontrakt `LoyaltyAccount` łamie `CorporateAccount`.
2. Wydziel współdzieloną logikę punktów do osobnej klasy.
3. Zerwij dziedziczenie między kontami, a raportowi daj wspólną rolę.

**Podpowiedź:** osobno odpowiedz na pytania "czy mają ten sam kod?" i "czy są wariantami jednego pojęcia?".
**Gotowe, gdy:** test zielony, `CorporateAccount` nie jest `instanceof LoyaltyAccount` i nie ma metody wymiany punktów, `LoyaltyReport` przyjmuje oba konta.

## Scena s15. Zgodność zbudowanego kodu, refleksja i metadane
**Katalog:** `typescript/src/workshop/m5/s15_compatibility/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s15`

**Zasada:** Zgodność ma kilka warstw: źródłową, już zbudowanego kodu, "refleksyjną" (przegląd prototypów, metadane) i inne - pełny build sprawdza tylko kod, który budujesz sam. W JS metodę identyfikuje wyłącznie nazwa, więc przeniesienie członka albo zmiana nazwy może zmienić to, co widzą zbudowane wtyczki i przegląd prototypów.

**Zadanie:**
1. Przenieś `price()` (oznaczone kolumną `cena` w bloku `static { column(...) }`) do `Ticket`, nie psując eksportu CSV.
2. Zmień `BoxOfficeApi` tak, by wycena przyjmowała każdy bilet (`quote(ticket: Ticket)`).
3. Zachowaj działanie wtyczek partnerów zbudowanych przeciw starej wersji API.

**Podpowiedź:** przeczytaj `S15SolutionTest` - uruchamia gotowy `plugin/Plugin.js` z różnymi wersjami API i przebudowuje jego źródło kompilatorem TS. Zastanów się, która zmiana łamie źródło, a która już zbudowany kod.
**Gotowe, gdy:** test zielony, eksport daje `Amator;cena=18.75`, stara metoda `quoteStudent(StudentTicket)` jest dostępna jako przestarzała (`@deprecated`) i deleguje do `quote`.

## Scena s16. Serializacja i proxy
**Katalog:** `typescript/src/workshop/m5/s16_serializationproxy/start` · **Test:** `scripts/warsztat.sh --lang ts test m5/s16`

**Zasada:** Domyślny zapis obiektu do JSON (`SessionStore.save`) bierze własne pola obiektu pod ich nazwami, więc ruch albo zmiana nazwy pola zmienia format danych, a brak wersji formatu tego nie ujawnia. Klasy z polami `#` nie da się opakować ani obiektem delegującym (typ nominalny), ani przezroczystym `Proxy`.

**Zadanie:**
1. Wydziel nadklasę `Ticket` z tytułem i miejscem (akcesory `title()`, `seat()`) i sprawdź w teście, co się dzieje z danymi zapisanymi przez starą wersję.
2. Uniezależnij postać zapisywaną od hierarchii klas.
3. Umożliw opakowanie `TicketPricing` proxy (np. audyt wywołań), nie usuwając pola `#studentDiscountPercent`.

**Podpowiedź:** Effective Java, "serialization proxy pattern" - w JS jego odpowiednikami są `toJSON()` i statyczne `fromJSON()` (zobacz, jak używa ich `SessionStore.ts`). Dlaczego metoda wołana przez `new Proxy(pricing, {})` nie widzi pola `#`?
**Gotowe, gdy:** test zielony, stare dane nie są czytane po cichu z utratą pól (`InvalidClassError`), JSON nie zawiera pól klasy bazowej, a proxy audytowe typowane jako rola zlicza wywołania `studentPrice`.
