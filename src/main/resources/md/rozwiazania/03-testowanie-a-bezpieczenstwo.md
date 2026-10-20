# Moduł 3. Testowanie a bezpieczeństwo - rozwiązania dla prowadzącego

Numeracja i nazwy odpowiadają plikowi zadań `src/main/resources/md/zadania/03-testowanie-a-bezpieczenstwo.md`. Kod referencyjny znajduje się w `src/main/java/pl/training/module3` i `src/test/java/pl/training/module3`; szczegółowy przewodnik po nim: `src/main/resources/md/przyklady/03-testowanie-a-bezpieczenstwo.md`. Numery sekcji w nawiasach odnoszą się do slajdów modułu.

Myśl przewodnia do powtarzania przy każdym omówieniu: test jest dowodem tylko dla tego, co obserwuje. Zanim zaufasz sieci bezpieczeństwa, sprawdź jej czułość kontrolną mutacją i nazwij, czego nie widzi.

| Zadanie | Kod referencyjny (wersja docelowa) | Testy chroniące zachowanie |
| --- | --- | --- |
| Ćwiczenie 1 | `src/main/java/pl/training/module3/InvoiceFormatter.java` | `LegacyInvoiceFormatterCharacterizationTest`, `FormatterEquivalenceTest` |
| Ćwiczenie 2A | `src/main/java/pl/training/module3/OrderPlacementService.java` | `OrderPlacementServiceTest` |
| Ćwiczenie 2B | `src/main/java/pl/training/module3/DeliveryFee.java` | `DeliveryFeeLineCoverageTest` (celowo niepełny) |
| Ćwiczenie 3 | `src/main/java/pl/training/module3/SeamedReminderService.java`, `src/main/java/pl/training/module3/ReminderService.java` | `SeamedReminderServiceTest`, `ReminderServiceTest` |
| Ćwiczenie 4 | `src/main/java/pl/training/module3/incremental/MigratingPricingEngine.java` | `incremental/PricingEngineContractTest`, `incremental/MigratingPricingEngineTest` |

(Testy leżą w `src/test/java/pl/training/module3/`.)

---

## Aktywność 1.8: Zaprojektuj portfel

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost ze slajdów.

Przykładowy minimalny portfel:

| Test | Wykrywane ryzyko | Rzeczywiste zależności | Oczekiwany czas | Miejsce w procesie |
| --- | --- | --- | --- | --- |
| testy reguły naliczania opłaty (przykłady i wartości tuż po obu stronach granicy) | regresja wartości granicznej | tylko logika domenowa, bez bazy i brokera | milisekundy | etap 1: po każdym kroku lokalnie |
| wąski test integracyjny repozytorium z rzeczywistym PostgreSQL (np. kontener testowy) | mapowanie `BigDecimal` (precyzja, skala, zaokrąglenie kolumny `numeric`) | rzeczywista baza i rzeczywisty mechanizm mapowania | sekundy | etap 1-3: przy zmianie adaptera lokalnie, zawsze w CI |
| test kontraktu lub serializacji komunikatu (zgodność ze schematem uzgodnionym z konsumentem) | niezgodny schemat komunikatu | rzeczywisty serializer i schemat; broker niepotrzebny | milisekundy do sekund | etap 1-3: razem z testami modułu i w CI |
| opcjonalnie jeden test komponentowy lub systemowy ścieżki "nalicz, zapisz, opublikuj" | poprawne połączenie elementów, konfiguracja, kolejność zapisu i publikacji (jeśli jest kontraktem) | aplikacja, baza, broker lub jego wiarygodny odpowiednik | kilka do kilkudziesięciu sekund | etap 4: przed scaleniem lub w nocnym przebiegu |

**Kluczowe tezy do omówienia:**

- Każda z trzech historycznych regresji ma test na najniższym poziomie, który nadal zachowuje mechanizm awarii (1.7).
- Mapowania `BigDecimal` nie sprawdzi mock ani repozytorium w pamięci (2.5). Potrzebna jest rzeczywista technologia.
- Schematu komunikatu nie sprawdzi test logiki; potrzebny jest test kontraktu.
- Szybki test adaptera może działać w pierwszym etapie razem z testami logiki - etykieta "integracyjny" nie powinna opóźniać informacji (1.7).
- Brak uniwersalnej proporcji 70/20/10 (1.5). Liczy się to, jakie ryzyko test wykrywa.

**Typowe błędy:** opis portfela samymi etykietami ("unit, integracyjne, e2e"); mockowanie bazy w teście, który ma wykryć błąd mapowania; test end-to-end jako jedyna ochrona schematu komunikatu.

**Pytania do dyskusji:** Który z tych testów jest najdroższy w utrzymaniu? Czy test systemowy jest tu w ogóle potrzebny, skoro trzy ryzyka są pokryte niżej?

---

## Aktywność 2.7: Nazwij rolę

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost ze slajdów.

1. **`publisherStub = event -> {}`** to stub, a nie dummy. Metoda `publish` jest rzeczywiście wywoływana w trakcie `place`, a pusta odpowiedź pozwala scenariuszowi przejść dalej. Dummy (2.1) nigdy nie byłby użyty; bezpieczny dummy rzuciłby wyjątek przy pierwszym wywołaniu, żeby test nie kłamał. Nazwa zmiennej nie rozstrzyga roli - rozstrzyga użycie.
2. **Podwójne obciążenie karty:** żaden obiekt w `placesOrderUsingStubFakeAndSpy` go nie wykryje. `paymentStub` zwraca `AUTH-7` przy każdym wywołaniu i nie liczy wywołań; wynik i stan repozytorium wyglądałyby tak samo. Wykrywa to dopiero `paymentMock` w `verifiesPaymentProtocolUsingMock` (`verify()` sprawdza dokładnie jedno wywołanie).
3. **Łamanie reguły „nie mockuj tego, czego nie posiadasz”:** gdyby test zamiast portu `PaymentGateway` (naszego interfejsu w języku domeny) zastępował bezpośrednio klienta SDK operatora płatności albo klienta HTTP i programował jego odpowiedzi (kody statusu, nagłówki, format JSON). Wtedy test utrwala nasze wyobrażenie o cudzym API, a zmiana biblioteki psuje testy logiki. Poprawnie: port mockujemy w testach logiki, a adapter do operatora sprawdzamy wąskim testem integracyjnym z sandboxem lub testem kontraktu (2.6).

**Typowe błędy:** klasyfikacja `publisherStub` jako dummy, bo "nic nie robi"; przekonanie, że `paymentStub` wykryje podwójne obciążenie, bo "jest bramką płatności"; mylenie reguły z zakazem wszelkich obiektów zastępczych dla płatności.

---

## Aktywność 3.7: Raport bez celu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost ze slajdów.

1. **Niewykonana gałąź:** fałszywa strona warunku `if (premium)` w `DeliveryFee.fee`, czyli klient bez statusu premium. JaCoCo pokazuje wszystkie linie metody jako wykonane, ale na linii `if` sygnalizuje "1 of 2 branches missed". Dodatkowo niepokryty jest prywatny konstruktor (w kodzie bajtowym to metoda), co nie stanowi ryzyka.
2. **Test uruchamiający gałąź:** wywołanie `DeliveryFee.fee(false)`.
3. **Asercja wykrywająca zmianę `100` na `200`:**

   ```java
   @Test
   void standardCustomerPaysDeliveryFee() {
       assertEquals(100, DeliveryFee.fee(false));
   }
   ```

   Test bez asercji albo sprawdzający tylko brak wyjątku podniesie pokrycie gałęzi, ale nie wykryje zmiany wartości.
4. **Czy globalne pokrycie z innych klas pomoże?** Nie. Żaden inny test nie wywołuje `DeliveryFee`, więc wzrost średniej dla całego projektu nie zmienia ochrony tej reguły. Ryzyko zmniejsza wyłącznie test wykonujący brakującą gałąź z asercją na wartość biznesową. Mierzenie całego systemu jedną średnią to jedno z nadużyć z 3.4-3.5.

**Kod referencyjny:** `src/main/java/pl/training/module3/DeliveryFee.java`, `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java` (celowo bez testu dla `false`).

**Typowe błędy:** uznanie 100% pokrycia linii za pełną ochronę; propozycja testu `fee(false)` bez asercji; szukanie raportu przed uruchomieniem `verify` (raport powstaje w fazie `verify`, w `target/site/jacoco`).

---

## Aktywność 4.20: Pierwsza sieć bez testów

> Rozwiązanie opracowane na podstawie kodu warsztatu (`pl.training.workshop.m3.s02_breakdependencies`), nie pochodzi wprost ze slajdów.

1. **Obserwacja bez zmiany kodu:** praktycznie żadna w teście - konstruktor `LegacyDatabase` rzuca wyjątek poza serwerownią, więc `run()` nie da się uruchomić. Można czytać kod, logi produkcyjne i tabelę "przypomniane" w bazie (obserwacja produkcji jako dowód, 1.1), ale nie da się napisać testu charakteryzującego. To jest sytuacja z drugiego wiersza tabeli 4.1: najpierw szew, potem test.
2. **Pierwszy ruch IDE:** Extract Interface na `LegacyDatabase` (np. `BookingStore` z dwiema używanymi metodami) i Parameterize Constructor z zachowanym konstruktorem bezargumentowym. Nie wolno zmienić **czasu życia i momentu tworzenia** połączenia: stary kod otwiera bazę przy każdym `run()`, więc konstruktor domyślny powinien przekazać fabrykę (`this(LegacyDatabase::new)`), a nie gotowy obiekt (`this(new LegacyDatabase())`). Ten drugi wariant przenosi wyjątek do konstruktora i zmienia zachowanie.
3. **Notatki po scratch refactoringu:** ukryte wejścia (bieżący czas, stan bazy), efekty i ich kolejność (mail, potem oznaczenie rezerwacji), granica 2 godzin i jej strona (`<=` czy `<`), przypadki "już przypomniane" i "seans już się zaczął", trzy kandydatów na szwy (baza, zegar, mailer), lista przypadków do pierwszego testu (90, 120, 121 minut). Kod ze scratch refactoringu wyrzucamy (4.17).
4. **Dlaczego nie shadow:** zadanie ma wyłącznie efekty uboczne (mail, zapis "przypomniane"). Podwójne wykonanie wysłałoby klientowi dwa maile i dwukrotnie zmieniło stan bazy (4.19). Shadow nadaje się dla czystych obliczeń, a tu nie ma czego porównać bez zamiany efektów w dane.

**Kolejność pierwszych ruchów:** szew na bazę (fabryka) → szew na zegar (`Clock` jako zależność konstruktora) → szew na mailer (najpierw Subclass and Override, potem interfejs funkcyjny) → pierwszy test → reguła SMS w osobnym kroku. Tę sekwencję pokazuje scena s02 warsztatu.

**Typowe błędy:** "najpierw napiszę test E2E na produkcyjnej bazie"; dodanie reguły SMS razem z wprowadzaniem szwów; interfejs `BookingStore` ze wszystkimi metodami `LegacyDatabase`.

---

## Ćwiczenie 1: Charakterystyka i refaktoryzacja formatera

### Krok 1: widoczne reguły

- nagłówek `INVOICE`,
- klient zapisany wielkimi literami (`Locale.ROOT`) po usunięciu skrajnych spacji,
- tekst `UNKNOWN` dla klienta `null`,
- zachowanie kolejności pozycji wejściowych,
- wiersz pozycji w formacie `SKU x ilość = wartość`,
- mnożenie ceny jednostkowej przez liczbę sztuk,
- prezentacja wartości z dwoma miejscami i `HALF_UP`,
- suma częściowa liczona z wartości przed zaokrągleniem prezentacji,
- podatek 23 procent zaokrąglany do dwóch miejsc,
- suma całkowita z części niezaokrąglonej i podatku zaokrąglonego,
- końcowy znak nowego wiersza.

### Krok 3: macierz przypadków

| Obserwacja | Stan w `LegacyInvoiceFormatterCharacterizationTest` |
| --- | --- |
| pusta lista pozycji | zabezpieczona w `documentsCurrentFallbackForMissingCustomer` |
| klient `null` | zabezpieczona w tym samym teście (jeden test obejmuje oba przypadki) |
| usuwanie spacji i wielkie litery | zabezpieczona w `documentsCurrentFormattingAndRounding` |
| wiele pozycji, zaokrąglenie podatku | zabezpieczona w `documentsCurrentFormattingAndRounding` (Tax 10.35 z 10.3454) |
| cena wymagająca zaokrąglenia przy prezentacji wiersza | brak, do dodania |
| suma dokładnych wartości różna od sumy wartości prezentowanych | brak, do dodania |

Uzupełniający przypadek może zawierać dwie pozycje o cenie `0.005` i liczbie sztuk `1`. Każdy wiersz jest prezentowany jako `0.01`, natomiast suma częściowa liczona z wartości dokładnych wynosi `0.01`, a nie `0.02`. Taki przypadek precyzyjnie ujawnia kolejność zaokrągleń.

Przykładowy test (oczekiwane wartości policzone dla zastanego kodu: subtotal 0.010, podatek 0.0023 zaokrąglony do 0.00, suma 0.01):

```java
@Test
void documentsSubtotalCalculatedFromUnroundedLineValues() {
    List<InvoiceLine> lines = List.of(
            new InvoiceLine("A", 1, new BigDecimal("0.005")),
            new InvoiceLine("B", 1, new BigDecimal("0.005")));

    String result = formatter.format("x", lines);

    assertEquals(String.join("\n",
            "INVOICE", "Customer: X",
            "A x 1 = 0.01", "B x 1 = 0.01",
            "Subtotal: 0.01", "Tax: 0.00", "Total: 0.01", ""), result);
}
```

> Kod powyższego testu opracowano na podstawie kodu referencyjnego.

Przypadek "cena wymagająca zaokrąglenia" można pokryć np. pozycją `19.995 x 1` (prezentacja `20.00`). Zmiana z `19.995` na prezentowane `20.00` w jednej pozycji nie oznacza, że do sumy częściowej należy dodać `20.00`. Zastany kod dodaje dokładną wartość pozycji przed jej prezentacją. Zmiana tej kolejności jest zmianą zachowania i wymaga osobnej decyzji.

### Krok 4: kontrolna mutacja

Mutacja "sumuj wartości zaokrąglone" (w pętli `subtotal = subtotal.add(lineTotal.setScale(2, RoundingMode.HALF_UP))`) musi spowodować porażkę testu `documentsSubtotalCalculatedFromUnroundedLineValues` (Subtotal `0.02` zamiast `0.01`). Istniejące dwa testy charakteryzujące pozostają przy tej mutacji zielone - to dowód, że brakowało przypadku. Druga przydatna mutacja: usunięcie `trim()` (pada `documentsCurrentFormattingAndRounding`).

### Krok 5: kontrakt a zastane zachowanie

Nie wszystkie reguły muszą być poprawnym wymaganiem. Kolejność momentów zaokrągleń może być zastanym zachowaniem o istotnym wpływie finansowym i wymaga potwierdzenia domenowego przed zmianą. Podobnie `UNKNOWN` dla braku klienta: test go utrwala, ale nie nadaje mu statusu wymagania (4.9 opisuje, jak postąpić, gdy właściciel produktu zażąda wyjątku).

### Kroki 6-7: sekwencja transformacji (4.7)

1. Uruchomić testy charakteryzujące i zapisać zieloną bazę.
2. Wyodrębnić `displayedCustomer` (Extract Method) i uruchomić testy.
3. Wyodrębnić `lineTotal` i uruchomić testy.
4. Wyodrębnić wspólną regułę `money` i uruchomić testy.
5. Wyodrębnić `calculateSubtotal` i uruchomić testy.
6. Wyodrębnić `appendLines` i uruchomić testy.
7. Nazwać stawkę jako `TAX_RATE` (Extract Constant) i uruchomić testy.
8. Uruchomić pełny zestaw testów i obejrzeć różnicę.

Każdy krok ma jedną intencję. Gdy po wydzieleniu `money` zmieni się wynik, zakres poszukiwania przyczyny jest niewielki.

### Kod referencyjny

`src/main/java/pl/training/module3/InvoiceFormatter.java` (stan końcowy). Zwróć uwagę uczestników, że `calculateSubtotal` sumuje `lineTotal(line)` (wartości dokładne), a `money` stosowane jest dopiero do prezentacji, podatku i sumy.

`src/test/java/pl/training/module3/FormatterEquivalenceTest.java` - test parametryzowany porównujący `LegacyInvoiceFormatter` i `InvoiceFormatter` dla trzech zestawów danych (pusta lista, klient `null` z jedną pozycją, klient `vip` z pozycjami `0.10` i `19.995`).

### Krok 8: porównanie implementacji

Stałe oczekiwania i test różnicowy się uzupełniają:

- stałe oczekiwanie jest niezależne od dalszego działania starej klasy,
- porównanie implementacji łatwo rozszerzyć o wiele zestawów danych,
- żaden mechanizm nie dowodzi równoważności dla wszystkich możliwych wejść,
- oba mogą utrwalić historyczny defekt, jeżeli oczekiwania nie zostaną przejrzane.

Ograniczenia testu różnicowego (4.8): potwierdza zgodność tylko dla wykonanych danych; nie wykryje defektu obecnego w obu wersjach; stara implementacja nie jest niezależną specyfikacją; efekty uboczne mogą uniemożliwiać podwójne wykonanie; różnica wydajności wymaga osobnego pomiaru. Po usunięciu klasy legacy test różnicowy znika, dlatego stałe, przejrzane oczekiwania trzeba przenieść na nową klasę.

Warto pokazać: dane z `FormatterEquivalenceTest` nie wykrywają mutacji "sumuj wartości zaokrąglone" (dla `0.30` i `39.990` obie sumy dają `40.29`). Wykrywa ją dopiero przypadek z dwiema pozycjami `0.005`.

### Krok 9: przegląd różnicy

Przykłady zmian, których cel ćwiczenia nie wymagał i które należy wskazać lub wycofać: masowe przeformatowanie pliku, zmiana nazw niezwiązanych z celem, dodanie walidacji `null` dla `lines`, zmiana `HALF_UP`, "poprawienie" kolejności zaokrągleń, dodanie waluty, zmiana komunikatu `UNKNOWN`.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| wszystkie testy zielone | `mvn test -Dtest='pl.training.module3.**'` |
| nowe testy czułe na mutację | mutacja "sumuj zaokrąglone" czerwieni test z dwiema pozycjami `0.005` |
| jedna intencja na transformację | historia commitów lub lista kroków odpowiada sekwencji z 4.7 |
| zgodność starej i nowej wersji | test parametryzowany uczestnika zielony, z co najmniej jednym przypadkiem `0.005` |
| znane ograniczenia dowodu | uczestnik wymienia: tylko wykonane dane, wspólny defekt, test znika z klasą legacy |

### Typowe błędy uczestników

- Wszystkie wydzielenia w jednej edycji, testy uruchomione dopiero na końcu.
- Zamiana sumowania na sumę wartości zaokrąglonych "bo tak prościej" - zmiana zachowania finansowego.
- Aktualizacja oczekiwania w teście, gdy ten padł, bez zrozumienia przyczyny.
- Nowy test charakteryzujący bez kontrolnej mutacji - zielony, ale nikt nie wie, co chroni.
- Użycie `toUpperCase()` bez `Locale.ROOT` przy przepisywaniu normalizacji klienta.
- Zgubienie końcowego `\n` przy przebudowie `StringBuilder`.
- Traktowanie zielonego testu różnicowego jako dowodu równoważności dla wszystkich danych.

### Pytania do dyskusji

- Który z testów zostawicie po usunięciu `LegacyInvoiceFormatter`?
- Co zrobicie, jeśli księgowość stwierdzi, że subtotal powinien być sumą wartości prezentowanych? (tryb funkcjonalny, 4.9)
- Które reguły potwierdzilibyście z domeną przed przemianowaniem testów z `documents...` na język wymagań?
- Kiedy zamiast kilku testów charakteryzujących lepszy byłby jeden golden master na wielu fakturach? (4.5)

---

## Ćwiczenie 2: Obiekty zastępcze i pokrycie

### Część A: role obiektów zastępczych

| Współpracownik | Rola i rodzaj weryfikacji | Może wykryć | Nie może wykryć |
| --- | --- | --- | --- |
| `catalogStub` | stub (pośrednie wejście); wynik usługi i zapisany stan pośrednio potwierdzają użycie ceny | pominięcie ceny albo błędne mnożenie jej przez liczbę sztuk | błędne wyszukiwanie SKU w rzeczywistym katalogu |
| `paymentStub` | stub; wynik i stan repozytorium potwierdzają propagację identyfikatora autoryzacji | zgubienie lub zmianę identyfikatora zwróconego przez bramkę | błędny token, kwotę wywołania, liczbę wywołań i integrację z operatorem płatności |
| `repositoryFake` | fake; weryfikacja stanu przez pomocniczą metodę `find` | zapis niepoprawnego `OrderDraft` albo brak zapisu | błędny SQL, mapowanie, ograniczenia i semantykę transakcji |
| `publisherSpy` | spy (pośrednie wyjście); test weryfikuje zarejestrowaną interakcję i jej dane | brak zdarzenia, złą liczbę zdarzeń albo błędną treść | serializację, dostarczenie i konfigurację rzeczywistego brokera |
| `paymentMock` | mock; weryfikacja zachowania: token, kwota i liczba wywołań | niepoprawny protokół obciążenia, w tym wielokrotne wywołanie | zachowanie produkcyjnej bramki i poprawność całego procesu rozliczenia |
| `publisherStub` | stub bez odpowiedzi; test nie weryfikuje publikacji | umożliwia wykonanie ścieżki, ale sam nie wykrywa braku publikacji | wszystkie defekty protokołu i integracji publikatora |

Nazwa `stub` jest właściwsza dla `publisherStub` niż `dummy`, ponieważ metoda `publish` rzeczywiście zostaje wywołana (Aktywność 2.7). W drugim teście `catalogStub` i `repositoryFake` pełnią te same role co w pierwszym, ale test nie sprawdza ich skutków.

**Pytanie 2 (stan, wynik, interakcja):** pierwszy test weryfikuje głównie wynik (`total`, `authorizationId`) i stan (`repositoryFake.find`), a interakcję tylko przez listę zdarzeń spy - to przeważnie weryfikacja stanu. Drugi test weryfikuje zachowanie (protokół płatności). Jest to uzasadnione, bo liczba obciążeń jest częścią kontraktu (płatność nie może zostać pobrana dwukrotnie, 1.2, 2.2).

**Pytanie 5 (kandydat do wspólnego testu kontraktu):** `InMemoryOrderRepository`. Nie wykryje błędnego SQL, ograniczeń bazy, mapowania `BigDecimal`, izolacji transakcji ani zachowania generatora identyfikatorów. Produkcyjny adapter wymaga wąskiego testu integracyjnego. `find` jest metodą pomocniczą wyłącznie klasy `InMemoryOrderRepository`, a nie częścią `OrderRepository`, więc nie da się uruchomić identycznego testu odczytu przez sam publiczny interfejs. Wspólny kontrakt może opisywać wyłącznie obserwowalne elementy wspólne, np. semantykę zwracanego identyfikatora (unikalność kolejnych wartości). Sprawdzenie trwałego zapisu wymaga jawnego mechanizmu obserwacji właściwego dla środowiska testowego, np. zapytania kontrolnego do rzeczywistej bazy. Nie należy dodawać metody odczytu do interfejsu produkcyjnego wyłącznie po to, aby ujednolicić testy.

**Kolejność zapisu i publikacji:** jest warta sprawdzania tylko wtedy, gdy wynika z mechanizmu niezawodności, np. zdarzenie może zostać opublikowane dopiero po trwałym zapisie albo oba działania są koordynowane przez outbox. Sama obecna kolejność wywołań w kodzie nie uzasadnia kruchego testu interakcji.

### Część B: raport pokrycia

- **Linie a gałęzie:** `DeliveryFeeLineCoverageTest` wywołuje tylko `fee(true)`. Wszystkie linie `fee` są wykonane (przypisanie `100`, warunek, przypisanie `0`, `return`), ale warunek `if (premium)` miał tylko wynik `true`. Pokrycie gałęzi pokazuje 1 z 2, pokrycie linii 100%.
- **Test brakującej gałęzi:** wywołanie `DeliveryFee.fee(false)` z asercją oczekującą dokładnie `100` (kod testu jak w Aktywności 3.7). Kontrolna mutacja `100` na `200` musi spowodować porażkę.
- **Test zwiększający pokrycie bez ochrony:** test bez asercji albo z asercją sprawdzającą tylko brak wyjątku (np. `assertDoesNotThrow(() -> DeliveryFee.fee(false))`) podniesie pokrycie, lecz nie wykryje zmiany `100` na `200`. W pakiecie podobny charakter ma `Module3ExamplesTest` (`assertDoesNotThrow` dla całego `main`): podnosi pokrycie wielu klas, nie chroniąc żadnej reguły.
- **Rozsądne użycie progu:** próg może blokować znaczący spadek pokrycia zmienianego pakietu i kierować uwagę na nowe, nieprzetestowane gałęzie. Nadal potrzebny jest przegląd ryzyka, asercji i wyłączeń. Globalny procent nie powinien pozwalać, aby dobrze pokryty kod banalny ukrywał brak ochrony reguły krytycznej. Uzupełnieniem może być test mutacyjny, którego wynik też wymaga interpretacji (3.4-3.5).

### Kod referencyjny

- `src/test/java/pl/training/module3/OrderPlacementServiceTest.java` - klasy pomocnicze `InMemoryOrderRepository`, `RecordingEventPublisher`, `ExpectingPaymentGateway`.
- `src/test/java/pl/training/module3/DeliveryFeeLineCoverageTest.java` - celowo bez testu dla `false`; test uczestnika dopisuje się obok.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| role według użycia | `publisherStub` sklasyfikowany jako stub, `paymentStub` jako stub (nie mock) |
| granice obiektów zastępczych | kolumna "nie może wykryć" wypełniona dla każdego wiersza, fake bez SQL i transakcji |
| interakcja uzasadniona kontraktem | uzasadnienie mocka odwołuje się do jednego obciążenia, a nie do "tak jest w kodzie" |
| asercja na wartość biznesową | mutacja `100` na `200` czerwieni nowy test |
| pokrycie nie jako jeden procent | wniosek mówi o obszarze zmiany i sile asercji |

### Typowe błędy uczestników

- Nazywanie każdego obiektu zastępczego "mockiem".
- Klasyfikacja `publisherStub` jako dummy.
- Twierdzenie, że `repositoryFake` "testuje repozytorium".
- Proponowanie dodania `find` do `OrderRepository` dla wygody testów.
- Uzasadnianie testu kolejności save/publish kolejnością linii w kodzie.
- Test `fee(false)` bez asercji albo z asercją `assertNotNull`.

### Pytania do dyskusji

- Który test przeżyje zmianę wewnętrznej kolejności obliczeń w `place`, a który nie?
- Czy w pierwszym teście wykrylibyśmy podwójne obciążenie karty? (nie, `paymentStub` nie liczy wywołań)
- Czego nie mierzy raport pokrycia? (siły asercji, zgodności z wymaganiem, reprezentatywności danych, kombinacji ścieżek, 3.4-3.5)
- Ile testów w Waszym projekcie weryfikuje interakcje z wewnętrznymi klasami, a nie z portami?

---

## Ćwiczenie 3: Wprowadzenie szwu

### Kroki 1-2: analiza

- **Niedeterministyczne wejście:** globalne źródło czasu `LocalDate.now(ZoneOffset.UTC)` uniemożliwia kontrolowanie pośredniego wejścia. Potrzebny stub albo fake zegara (`Clock.fixed`).
- **Efekt trudny do obserwacji:** bezpośrednie użycie `System.out.printf` - efekt nie jest reprezentowany przez port. Potrzebny spy, który zarejestruje wysłane wiadomości.
- **Górna granica:** `today + 7` włącznie (`isAfter` zwraca `false` dla dokładnie +7).
- **Brak dolnej granicy:** wiadomość jest wysyłana również dla daty dzisiejszej i dat przeszłych.

Istotne przypadki:

- data odnowienia dokładnie siedem dni po dzisiejszej dacie: wiadomość ma zostać wysłana,
- data odnowienia osiem dni po dzisiejszej dacie: wiadomość nie ma zostać wysłana,
- data odnowienia w przeszłości: wiadomość również zostaje wysłana, ponieważ zastana reguła nie ma dolnej granicy.

Jeżeli wymaganie domenowe definiuje przedział od dnia dzisiejszego do siedmiu dni włącznie, dodanie warunku odrzucającego daty przeszłe będzie osobną zmianą funkcjonalną. Nie należy wprowadzać go po cichu podczas rozrywania zależności.

### Kroki 3-4: minimalny szew

Technika: Subclass and Override Method (Feathers). W IDE: zaznacz `LocalDate.now(ZoneOffset.UTC)`, Extract Method z widocznością `protected` (`currentDate`), to samo dla `System.out.printf(...)` (`sendMessage`), usuń `final` z klasy. Koszty: rozszerzenie powierzchni dziedziczenia tylko dla testów, sprzężenie testu z metodami chronionymi, zachęta do hierarchii bez znaczenia domenowego, brak jawnego portu. Zdjęcie `final` jest refaktoryzacją tylko dla typu wewnętrznego z konsumentami pod kontrolą.

### Kroki 5-6: jawne zależności

Techniki: Parameterize Constructor i Extract Interface. `ReminderGateway` jest portem w języku domeny (2.6), więc zastąpienie go w teście nie łamie reguły „nie mockuj tego, czego nie posiadasz”. Testy przenoszą się z podklasy na `Clock.fixed` i spy `RecordingReminderGateway`; nazwy zmieniają się z `documents...` na `preserves...`, bo zachowanie jest teraz świadomie utrzymane.

### Kod referencyjny

- `src/main/java/pl/training/module3/SeamedReminderService.java` (klasa bez `final`, metody `protected currentDate()` i `protected sendMessage(email, renewalDate)`) oraz `src/test/java/pl/training/module3/SeamedReminderServiceTest.java` z podklasą `TestableReminderService`. Testy dla dziś = 2026-08-30: `sendsReminderForRenewalExactlySevenDaysAway` (2026-09-06, wysyłka), `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` (2026-09-07, brak), `documentsCurrentBehaviorForPastRenewalDate` (2026-08-29, wysyłka).
- `src/main/java/pl/training/module3/ReminderService.java` (konstruktor `ReminderService(Clock clock, ReminderGateway reminderGateway)`, interfejs `ReminderGateway`) oraz `src/test/java/pl/training/module3/ReminderServiceTest.java` (`Clock.fixed(Instant.parse("2026-08-30T10:00:00Z"), ZoneOffset.UTC)` i spy `RecordingReminderGateway`).

### Krok 7: punkty aktywacji

- Szew przejściowy: `SeamedReminderService` tworzy minimalny szew przez `currentDate` i `sendMessage`. Punktem aktywacji jest utworzenie `TestableReminderService`, a dynamiczne wiązanie wybiera nadpisane metody.
- Projekt docelowy: `ReminderService` zastępuje dziedziczenie przekazanymi zależnościami. Punktem aktywacji jest wyrażenie `new ReminderService(...)`, a jego argumenty wybierają zachowanie. Test przekazuje `Clock.fixed` oraz `RecordingReminderGateway`. Produkcja przekaże zegar systemowy z jawną strefą (tu UTC, aby zachować zachowanie wersji legacy) i rzeczywisty adapter komunikacyjny.

### Krok 8: test rzeczywistego adaptera

Test jednostkowy usługi nie potwierdzi, że adapter poprawnie serializuje komunikat, uwierzytelnia się, obsługuje odpowiedzi ani ponowienia. Te ryzyka należy sprawdzić wąskim testem integracyjnym z rzeczywistą technologią lub środowiskiem o zgodnym kontrakcie.

### Usunięcie rozwiązania przejściowego

Rozwiązanie przejściowe można usunąć po przeniesieniu wywołań produkcyjnych i testów na `ReminderService`. Usunięcie powinno być osobnym, małym krokiem (Safe Delete), po którym ponownie uruchamiany jest pełny zestaw testów. Pełna kolejność: 4.15 (nazwij zachowanie i punkty uruchomienia oraz obserwacji, wprowadź najmniejszy szew i zabezpiecz testami, przenieś wybór do jawnego punktu aktywacji, dodaj test adaptera, usuń konstrukcję przejściową).

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| test niezależny od daty i wyjścia | brak `LocalDate.now()` i przechwytywania `System.out` w testach; test zielony po zmianie daty systemowej |
| granica 7 dni po obu stronach, data przeszła scharakteryzowana | trzy testy: +7 (wysyłka), +8 (brak), przeszłość (wysyłka, nazwa `documents...` lub `preserves...`) |
| kod domenowy nie tworzy adaptera | brak `new` konkretnego adaptera w klasie docelowej; zależności w konstruktorze |
| przejściowe odróżnione od docelowego | szew przejściowy oznaczony (komentarz, nazwa, zadanie do usunięcia) |
| test adaptera nazwany | uczestnik wymienia serializację, uwierzytelnienie, ponowienia |

### Typowe błędy uczestników

- "Poprawienie" reguły przez dodanie dolnej granicy w trakcie wprowadzania szwu.
- Testy liczone względem `LocalDate.now()` (niedeterministyczne w okolicach północy i niezgodne ze strefą UTC).
- Przechwytywanie `System.out` zamiast wprowadzenia portu.
- Przekazanie w produkcji `Clock.systemDefaultZone()` zamiast zegara UTC - cicha zmiana zachowania.
- Interfejs `ReminderGateway` dodany, ale kod domenowy dalej tworzy konkretny adapter przez `new` (interfejs bez punktu aktywacji nie jest użytecznym szwem, 4.10).
- Pozostawienie `SeamedReminderService` bez decyzji o jego losie.

### Pytania do dyskusji

- Kiedy Subclass and Override Method może zostać na stałe?
- Jaki test wykryłby pomyłkę strefy czasowej w zegarze produkcyjnym?
- Które z problemów tej klasy to separacja, a które obserwacja? (4.10)

---

## Ćwiczenie 4: Równoległa weryfikacja i jej granice

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost ze slajdów.

### Krok 1: wynik `Module3Examples`

`Parallel verification: 53.97/Agreement`. Pierwsza część to cena, którą dostał klient (`19.99 x 3` z rabatem `0.10`: brutto `59.97`, rabat `6.00`, netto `53.97`), czyli wynik legacy - w trybie `VERIFY` zawsze autorytatywny. Druga część to typ zdarzenia raportu: obie implementacje dały ten sam wynik.

### Krok 2: kontrolna mutacja w kandydacie

Po usunięciu `.setScale(MONEY_SCALE, ROUNDING)` przy rabacie kandydat zaokrągla dopiero wynik końcowy (w `PriceQuote`). Zawodzi `PricingEngineContractTest.everyProductionImplementationCalculatesCanonicalExamples` w wariancie kandydata: dla `0.01 x 1` z rabatem `0.5` oczekiwane `0.01`, kandydat zwraca `0.00` (rabat `0.005` nie jest już zaokrąglany do `0.00` przed odejmowaniem). `MigratingPricingEngineTest` pozostaje zielony, bo testuje router na stubach i spy (liczniki wywołań, sztuczne wyniki), a nie prawdziwą implementację kandydata. `Module3ExamplesTest` też jest zielony - sprawdza tylko brak wyjątku.

Wniosek: test kontraktowy chroni **obliczenie** (niezależne oczekiwania dla obu implementacji), test routera chroni **protokół migracji** (kto jest wołany, co jest zwracane, co raportowane). To dwa różne elementy portfela.

### Krok 3: zdarzenie w trybie `VERIFY`

Router zgłosiłby `Divergence(request, legacy = 0.01, candidate = 0.00)`, a klient dostałby `0.01` - wynik legacy. Rozbieżność jest widoczna w raporcie, ale nie dotyka klienta.

### Krok 4: klasyfikacja operacji

| Operacja | Shadow (podwójne wykonanie) | Co porównać zamiast wykonania |
| --- | --- | --- |
| wycena biletu | tak - czyste obliczenie | wynik obu implementacji (`Agreement` / `Divergence`) |
| obciążenie karty | nie - drugie obciążenie klienta | kwotę i token, które kandydat **zamierza** obciążyć (plan jako dane) |
| mail z potwierdzeniem | nie - klient dostaje dwa maile | adresata i treść zaplanowanego maila |
| nadanie kolejnego numeru rezerwacji | nie - sekwencja to stan współdzielony; kandydat "zużyje" numer | regułę formatu numeru albo numer wyliczony bez inkrementacji licznika |
| zapis audytu | nie - podwójny wpis zafałszuje audyt | treść wpisu, który kandydat by zapisał |

Wspólna technika: efekty zamieniamy w dane (opis "co zrobić"), które da się porównać i wykonać tylko raz. Pokazuje to scena s05 warsztatu (`RecordingEffects`, potem `BookingPlanner` zwracający `BookingPlan`).

### Krok 5: warunki produkcyjne

Dowolne dwa z: izolacja wykonania kandydata od ścieżki odpowiedzi (asynchronicznie lub poza wątkiem żądania), timeout i bulkhead dla kandydata, limit kopiowanego ruchu (próbkowanie), monitoring kanału raportowania (awaria reportera jest połykana, więc brak zdarzeń nie oznacza zgodności), jawny właściciel i kryterium zakończenia trybu `VERIFY`.

### Kod referencyjny

- `src/main/java/pl/training/module3/incremental/MigratingPricingEngine.java` - metoda `verify`, `tryToReport`.
- `src/test/java/pl/training/module3/incremental/PricingEngineContractTest.java`, `src/test/java/pl/training/module3/incremental/MigratingPricingEngineTest.java`.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| odróżnienie testu kontraktowego od testu routera | uczestnik wskazuje, który test padł po mutacji i dlaczego drugi nie |
| klient zawsze dostaje wynik legacy | odpowiedź na krok 3 (`0.01`), wzmianka o `CandidateFailure` |
| sposób porównania efektów bez wykonania | tabela z kroku 4 ma kolumnę "co porównać" dla każdej operacji z efektem |
| zgodność to nie poprawność | uczestnik podaje przykład wspólnego błędu (np. obie wersje źle zaokrąglają) |

### Typowe błędy uczestników

- Oczekiwanie, że `MigratingPricingEngineTest` wykryje błąd obliczenia kandydata.
- Przekonanie, że w trybie `VERIFY` klient dostaje wynik kandydata, "jeśli jest zgodny".
- Uznanie numeru rezerwacji za czyste obliczenie.
- Traktowanie braku zdarzeń `Divergence` jako dowodu zgodności bez sprawdzenia, czy reporter działa.

### Pytania do dyskusji

- Ile zgodnych porównań i z jakiego okresu wystarczy, żeby przełączyć na `CANDIDATE`? Kto o tym decyduje?
- Jak w Waszym systemie zamienić efekt uboczny w dane, żeby go porównać?

---

## Sprawdzenie wiedzy: odpowiedzi

1. **Co dokładnie oznacza zielony zestaw testów?** Żaden wykonany test nie wykrył różnicy w sprawdzanych obserwacjach. Nie jest to dowód braku wszystkich regresji.
2. **Dlaczego piramida testów nie narzuca uniwersalnych proporcji?** Koszt i wartość testu zależą od architektury, technologii i ryzyka. Ważniejsze są właściwości testów (zakres, realizm, szybkość, determinizm, wykrywane ryzyko) niż arbitralna liczba.
3. **Czym punkt zmiany różni się od punktu testowania?** Punkt zmiany to miejsce modyfikacji kodu, punkt testowania - miejsce, z którego da się uruchomić zachowanie i zobaczyć jego efekt. W legacy często testujemy szerzej niż zmieniamy.
4. **Czym stub różni się od mocka, a spy od fake?** Stub dostarcza odpowiedzi (pośrednie wejście). Mock zawiera oczekiwania dotyczące interakcji i sam je weryfikuje. Spy rejestruje wywołania do późniejszej asercji w teście. Fake to uproszczona, ale działająca implementacja (np. repozytorium w pamięci).
5. **Kiedy weryfikacja zachowania (interakcji) jest uzasadniona?** Gdy interakcja jest kontraktem (jedno obciążenie, publikacja po zatwierdzeniu transakcji, idempotencja) albo gdy efekt na granicy systemu nie zostawia stanu do odczytu. Domyślnie weryfikujemy stan i wynik.
6. **Co oznacza reguła „nie mockuj tego, czego nie posiadasz” i co robić zamiast tego?** Nie zastępujemy w testach cudzego API, bo utrwalamy własne wyobrażenie o nim. Zamiast tego definiujemy własny wąski port w języku domeny, zastępujemy go w testach logiki, a adapter do cudzego API sprawdzamy testem integracyjnym lub kontraktowym.
7. **Jakie ryzyko pozostaje po zastąpieniu bazy implementacją działającą w pamięci?** Test nie sprawdza między innymi zapytań, mapowania, ograniczeń, transakcji i zachowania rzeczywistego silnika.
8. **Co pokazuje pokrycie gałęzi, czego może nie pokazać pokrycie linii?** Pokazuje, czy wykonano różne wyniki warunków. Wszystkie linie mogą zostać wykonane mimo pominięcia jednej strony decyzji (przykład: `DeliveryFee`, `DiscountPolicy`).
9. **Dlaczego test charakteryzujący nie dowodzi poprawności wymagania?** Zapisuje obserwowany wynik, który może być historycznym defektem lub przypadkowym szczegółem.
10. **Kiedy golden master przestaje chronić?** Gdy plik oczekiwany jest aktualizowany bez czytania różnicy, gdy niedeterminizm (czas, losowość, kolejność) nie został znormalizowany i test "miga", albo gdy scenariusz nie zawiera przypadku, który zmieniamy.
11. **Czym różni się seam od punktu aktywacji?** Seam jest miejscem możliwej zmiany zachowania, a punkt aktywacji jest miejscem wyboru konkretnego wariantu.
12. **Które ruchy automatów IDE są bezpieczne bez testów i kiedy przestają takie być?** Rename lokalnych i prywatnych elementów, Extract Variable i Extract Method, Inline Variable czystego wyrażenia, Extract Interface i Introduce Parameter z delegacją, Safe Delete. Przestają być bezpieczne przy refleksji, serializacji, konfiguracji tekstowej, zewnętrznych konsumentach, efektach ubocznych w wyrażeniu i zmianie momentu tworzenia zależności.
13. **Co jest produktem scratch refactoringu?** Zrozumienie zapisane poza kodem: reguły, ukryte wejścia, efekty, punkty zmiany, kandydaci na szwy i przypadki do testu charakteryzującego. Sam kod jest wyrzucany.
14. **Dlaczego tryb shadow jest bezpieczny tylko dla czystych obliczeń?** Bo obie implementacje wykonują się naprawdę: efekty uboczne (płatność, mail, zapis, zdarzenie, sekwencja) zostałyby wykonane dwukrotnie. Efekty trzeba zamienić w dane i porównywać zamiast wykonywać.
