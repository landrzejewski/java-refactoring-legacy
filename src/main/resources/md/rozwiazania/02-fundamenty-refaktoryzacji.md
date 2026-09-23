# Moduł 2. Fundamenty refaktoryzacji - rozwiązania dla prowadzącego

Numeracja i nazwy odpowiadają plikowi zadań `src/main/resources/prowadzenie/zadania/02-fundamenty-refaktoryzacji.md`. Kod referencyjny znajduje się w `src/main/java/pl/training/module2` i `src/test/java/pl/training/module2`; szczegółowy przewodnik po nim: `src/main/resources/prowadzenie/przyklady/02-fundamenty-refaktoryzacji.md`.

Myśl przewodnia do powtarzania przy każdym omówieniu: etykieta operacji nie rozstrzyga jej charakteru. Decydują cel oraz obserwacje, które mają pozostać niezmienione, i sposób, w jaki to zweryfikujemy.

---

## Aktywność 1.6: Nazwij kontrakt

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.
> (Teoria nie zawiera odpowiedzi do tej aktywności; poniższe opracowano na podstawie sekcji 1.2, 1.3, 2.6 i 3.2.)

| Zmiana | Konsument | Możliwa obserwacja | Weryfikacja | Ocena |
| --- | --- | --- | --- | --- |
| 1. prywatne pole `List` na `Set` | metody publiczne zwracające lub przetwarzające elementy, serializacja pól (np. Jackson, serializacja Javy), `equals`/`hashCode` | usunięcie duplikatów, utrata kolejności (np. `HashSet`), inny format po serializacji, inna złożoność operacji | testy z duplikatami i z kolejnością elementów na publicznej granicy, test serializacji | refaktoryzacja tylko wtedy, gdy duplikaty są niemożliwe lub nieistotne, kolejność nie jest kontraktem, a pole nie jest serializowane; w przeciwnym razie zmiana zachowania |
| 2. zamiana dwóch zapisów do bazy | baza (klucze obce, wyzwalacze, ograniczenia), mechanizmy CDC i audytu, inne transakcje | kolejność efektów ubocznych (protokół), stan po częściowej awarii, naruszenie ograniczeń, zakleszczenia | test integracyjny z rzeczywistą bazą, test zachowania transakcyjnego, test interakcji jeśli kolejność jest kontraktem | zwykle nie jest czystą refaktoryzacją, dopóki nie wykaże się, że oba zapisy są w jednej transakcji i nic nie zależy od ich kolejności |
| 3. przeniesienie klasy używanej przez refleksję | konfiguracja tekstowa, `Class.forName`, serializacja z nazwą typu, kontener DI, zewnętrzne pliki | pełna nazwa klasy (pakiet + nazwa) jest częścią kontraktu; błąd pojawia się dopiero w runtime | wyszukanie nazwy w konfiguracji i zasobach, test uruchamiający rzeczywisty mechanizm (np. start kontekstu, deserializacja zapisanych danych) | IDE nie zobaczy wszystkich odwołań (2.6); refaktoryzacja tylko po aktualizacji wszystkich konsumentów i zgodności zapisanych danych |
| 4. szybszy algorytm | wywołujący kod, użytkownicy, SLA | wynik (stabilność sortowania, remisy, zaokrąglenia zmiennoprzecinkowe, przypadki brzegowe), czas i pamięć | test różnicowy stary vs nowy na wielu danych, testy wartości granicznych, osobny pomiar wydajności | zwykle optymalizacja (1.3); wynik funkcjonalny musi być identyczny, a zmiana właściwości pozafunkcjonalnej jest celem, więc trzeba ją nazwać |
| 5. prywatna nazwa bez refleksji i konfiguracji | praktycznie brak (ewentualnie logi ze stosem wywołań) | brak obserwacji na granicy publicznej | automatyczna zmiana nazwy w IDE, kompilacja, istniejące testy | refaktoryzacja |

**Na co zwrócić uwagę w omówieniu:**

- Pozycje 1 i 5 wyglądają na "wewnętrzne", ale tylko 5 jest bezpieczna bez dodatkowej analizy.
- Pozycja 3 to klasyczny przykład z 2.6: narzędzie nie zna całego kontraktu.
- Uczestnicy często klasyfikują od razu ("to refaktoryzacja"). Wymagaj najpierw kolumn konsument, obserwacja, weryfikacja.

**Typowe błędy:** uznanie zmiany kolejności zapisów za "nieistotną wewnętrzną kolejność"; pominięcie serializacji przy polu prywatnym; uznanie szybszego algorytmu za refaktoryzację bez nazwania zmiany wydajności.

**Pytania do dyskusji:** Kiedy kolejność dwóch zapisów staje się kontraktem? Co w Waszym systemie korzysta z nazw klas zapisanych tekstowo?

---

## Aktywność 4.6: Zaprojektuj portfel

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.
> (Teoria nie zawiera odpowiedzi do tej aktywności; poniższe opracowano na podstawie sekcji 4.1-4.5 i 5.5.)

Przykładowy minimalny portfel:

| Test | Wykrywane ryzyko | Rzeczywiste zależności | Oczekiwany czas | Miejsce w procesie |
| --- | --- | --- | --- | --- |
| testy reguły naliczania opłaty (przykłady i wartości tuż po obu stronach granicy) | regresja wartości granicznej | tylko logika domenowa, bez bazy i brokera | milisekundy | etap 1: po każdym kroku lokalnie |
| wąski test integracyjny repozytorium z rzeczywistym PostgreSQL (np. kontener testowy) | mapowanie `BigDecimal` (precyzja, skala, zaokrąglenie kolumny `numeric`) | rzeczywista baza i rzeczywisty mechanizm mapowania | sekundy | etap 1-3: przy zmianie adaptera lokalnie, zawsze w CI |
| test kontraktu lub serializacji komunikatu (zgodność ze schematem uzgodnionym z konsumentem) | niezgodny schemat komunikatu | rzeczywisty serializer i schemat; broker niepotrzebny | milisekundy do sekund | etap 1-3: razem z testami modułu i w CI |
| opcjonalnie jeden test komponentowy lub systemowy ścieżki "nalicz, zapisz, opublikuj" | poprawne połączenie elementów, konfiguracja, kolejność zapisu i publikacji (jeśli jest kontraktem) | aplikacja, baza, broker lub jego wiarygodny odpowiednik | kilka do kilkudziesięciu sekund | etap 4: przed scaleniem lub w nocnym przebiegu |

**Kluczowe tezy do omówienia:**

- Każda z trzech historycznych regresji ma test na najniższym poziomie, który nadal zachowuje mechanizm awarii (4.5).
- Mapowania `BigDecimal` nie sprawdzi mock ani repozytorium w pamięci (5.5). Potrzebna jest rzeczywista technologia.
- Schematu komunikatu nie sprawdzi test logiki; potrzebny jest test kontraktu.
- Szybki test adaptera może działać w pierwszym etapie razem z testami logiki - etykieta "integracyjny" nie powinna opóźniać informacji (4.4).
- Brak uniwersalnej proporcji 70/20/10 (4.1). Liczy się to, jakie ryzyko test wykrywa.

**Typowe błędy:** opis portfela samymi etykietami ("unit, integracyjne, e2e"); mockowanie bazy w teście, który ma wykryć błąd mapowania; test end-to-end jako jedyna ochrona schematu komunikatu.

**Pytania do dyskusji:** Który z tych testów jest najdroższy w utrzymaniu? Czy test systemowy jest tu w ogóle potrzebny, skoro trzy ryzyka są pokryte niżej?

---

## Aktywność 6.5: Raport bez celu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.
> (Odpowiedzi na pytania 1-3 pokrywają się z częścią omówienia Ćwiczenia 3 w teorii, sekcja 11.3; odpowiedź na pytanie 4 opracowano na podstawie sekcji 6.3-6.4.)

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
4. **Czy globalne pokrycie z innych klas pomoże?** Nie. Żaden inny test nie wywołuje `DeliveryFee`, więc wzrost średniej dla całego projektu nie zmienia ochrony tej reguły. Ryzyko zmniejsza wyłącznie test wykonujący brakującą gałąź z asercją na wartość biznesową. Mierzenie całego systemu jedną średnią to jedno z nadużyć z 6.4.

**Kod referencyjny:** `src/main/java/pl/training/module2/DeliveryFee.java`, `src/test/java/pl/training/module2/DeliveryFeeLineCoverageTest.java` (celowo bez testu dla `false`).

**Typowe błędy:** uznanie 100% pokrycia linii za pełną ochronę; propozycja testu `fee(false)` bez asercji; szukanie raportu przed uruchomieniem `verify` (raport powstaje w fazie `verify`, w `target/site/jacoco`).

---

## Ćwiczenie 1: Granica refaktoryzacji

Odpowiedź wzorcowa (teoria 11.1):

| Pozycja | Ocena | Obserwowalne zachowanie i weryfikacja |
| --- | --- | --- |
| A. `calc` na `calculateTax` (prywatna) | zwykle refaktoryzacja | Należy sprawdzić odwołania tekstowe, refleksję i narzędzia. Przy braku takich konsumentów wystarczą automatyczna zmiana nazwy, kompilacja i testy zachowania. |
| B. `double` na `BigDecimal` z powodu błędów zaokrągleń | cel obejmuje zmianę zachowania | Jeżeli wynik ma przestać zawierać błąd zaokrąglenia, nie jest to wyłącznie refaktoryzacja. Najpierw można strukturalnie wydzielić obliczenie, następnie osobno zmienić typ i oczekiwany wynik. |
| C. publiczne DTO do innego pakietu | zależy od kontraktu | Dla publicznej biblioteki nazwa pakietu jest częścią nazwy typu i może naruszyć kompatybilność źródłową lub binarną. Potrzebna jest strategia migracji konsumentów. |
| D. wydzielenie budowy komunikatu bez zmiany tekstu | refaktoryzacja | Kontraktem jest dokładna treść komunikatu, w tym format i białe znaki. Chronią go test serializacji albo test charakteryzujący tekst. |
| E. pamięć podręczna kursu walut | zwykle optymalizacja | Wynik może pozostać ten sam, lecz zmieniają się świeżość danych, opóźnienie, zużycie pamięci i zachowanie przy awarii. Wymaga testów poprawności, wygaszania danych i pomiaru wydajności. |

**Uzupełnienie do pytań 3-5 (konsument, dowód, podział):**

- A: konsument to kod w tej samej klasie; dowód: kompilacja i testy; podział niepotrzebny.
- B: konsumentem są raporty, księgowość, integracje odbierające kwoty; dowód przed: test charakteryzujący obecne (błędne) wyniki; po: nowy test wymagania z poprawnymi kwotami. Podział obowiązkowy: krok strukturalny (wydzielenie obliczenia pod ochroną testów), potem krok funkcjonalny (zmiana typu i oczekiwań, opisana jako zmiana zachowania).
- C: konsumenci to moduły i zewnętrzni klienci biblioteki, serializacja z nazwą typu; dowód: kompilacja wszystkich konsumentów, testy kompatybilności. Jeśli wszyscy konsumenci są w jednym repozytorium zespołu, może to być refaktoryzacja (1.3); dla opublikowanej biblioteki to migracja API (np. stary typ jako przestarzały, okres przejściowy).
- D: konsument to odbiorca komunikatu; dowód: test porównujący dokładny tekst przed i po.
- E: konsumenci to użytkownicy kursów oraz operacje (pamięć, monitoring); dowód: testy poprawności, testy wygaszania i unieważniania, pomiar wydajności, zachowanie przy awarii źródła.

**Najważniejszy wniosek:** etykieta operacji nie rozstrzyga jej charakteru. Decydują cel oraz obserwacje, które mają pozostać niezmienione.

**Typowe błędy uczestników:**

- Ocena B jako refaktoryzacji ("to tylko zmiana typu").
- Ocena C jako zawsze bezpiecznej, bo IDE przeniesie klasę.
- Ocena E jako refaktoryzacji, bo "wynik ten sam" - bez nazwania świeżości danych i zachowania przy awarii.
- Brak konkretnego mechanizmu weryfikacji (samo "przetestujemy").

**Pytania do dyskusji:** Jak w praktyce rozdzielić B na dwa commity? Kiedy zmiana nazwy publicznej klasy jest refaktoryzacją, a kiedy migracją API?

---

## Ćwiczenie 2: Charakterystyka i refaktoryzacja formatera

### Krok 1: widoczne reguły (teoria 11.2)

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

Według teorii: uzupełniający przypadek może zawierać dwie pozycje o cenie `0.005` i liczbie sztuk `1`. Każdy wiersz jest prezentowany jako `0.01`, natomiast suma częściowa liczona z wartości dokładnych wynosi `0.01`, a nie `0.02`. Taki przypadek precyzyjnie ujawnia kolejność zaokrągleń.

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

> Kod powyższego testu opracowano na podstawie kodu referencyjnego; teoria podaje jedynie dane przypadku.

Przypadek "cena wymagająca zaokrąglenia" można pokryć np. pozycją `19.995 x 1` (prezentacja `20.00`). Uwaga z teorii: zmiana z `19.995` na prezentowane `20.00` w jednej pozycji nie oznacza, że do sumy częściowej należy dodać `20.00`. Zastany kod dodaje dokładną wartość pozycji przed jej prezentacją. Zmiana tej kolejności jest zmianą zachowania i wymaga osobnej decyzji.

### Krok 4: kontrakt a zastane zachowanie

Nie wszystkie reguły muszą być poprawnym wymaganiem. Kolejność momentów zaokrągleń może być zastanym zachowaniem o istotnym wpływie finansowym i wymaga potwierdzenia domenowego przed zmianą. Podobnie `UNKNOWN` dla braku klienta: test go utrwala, ale nie nadaje mu statusu wymagania (8.4 opisuje, jak postąpić, gdy właściciel produktu zażąda wyjątku).

### Kroki 5-6: sekwencja transformacji (teoria 8.3)

1. Uruchomić testy charakteryzujące i zapisać zieloną bazę.
2. Wyodrębnić `displayedCustomer` i uruchomić testy.
3. Wyodrębnić `lineTotal` i uruchomić testy.
4. Wyodrębnić wspólną regułę `money` i uruchomić testy.
5. Wyodrębnić `calculateSubtotal` i uruchomić testy.
6. Wyodrębnić `appendLines` i uruchomić testy.
7. Nazwać stawkę jako `TAX_RATE` i uruchomić testy.
8. Uruchomić pełny zestaw testów i obejrzeć różnicę.

Każdy krok ma jedną intencję. Gdy po wydzieleniu `money` zmieni się wynik, zakres poszukiwania przyczyny jest niewielki.

**Kod referencyjny (stan końcowy):** `src/main/java/pl/training/module2/InvoiceFormatter.java`. Zwróć uwagę uczestników, że `calculateSubtotal` sumuje `lineTotal(line)` (wartości dokładne), a `money` stosowane jest dopiero do prezentacji, podatku i sumy.

### Krok 7: porównanie implementacji

**Kod referencyjny:** `src/test/java/pl/training/module2/FormatterEquivalenceTest.java` - test parametryzowany porównujący `LegacyInvoiceFormatter` i `InvoiceFormatter` dla trzech zestawów danych (pusta lista, klient `null` z jedną pozycją, klient `vip` z pozycjami `0.10` i `19.995`).

Stałe oczekiwania i test różnicowy się uzupełniają (teoria 11.2):

- stałe oczekiwanie jest niezależne od dalszego działania starej klasy,
- porównanie implementacji łatwo rozszerzyć o wiele zestawów danych,
- żaden mechanizm nie dowodzi równoważności dla wszystkich możliwych wejść,
- oba mogą utrwalić historyczny defekt, jeżeli oczekiwania nie zostaną przejrzane.

Ograniczenia testu różnicowego (8.2): potwierdza zgodność tylko dla wykonanych danych; nie wykryje defektu obecnego w obu wersjach; stara implementacja nie jest niezależną specyfikacją; efekty uboczne mogą uniemożliwiać podwójne wykonanie; różnica wydajności wymaga osobnego pomiaru. Po usunięciu klasy legacy test różnicowy znika, dlatego stałe, przejrzane oczekiwania trzeba przenieść na nową klasę.

Warto pokazać: dane z `FormatterEquivalenceTest` nie wykrywają mutacji "sumuj wartości zaokrąglone" (dla `0.30` i `39.990` obie sumy dają `40.29`). Wykrywa ją dopiero przypadek z dwiema pozycjami `0.005`.

### Krok 8: przegląd różnicy

Przykłady zmian, których cel ćwiczenia nie wymagał i które należy wskazać lub wycofać: masowe przeformatowanie pliku, zmiana nazw niezwiązanych z celem, dodanie walidacji `null` dla `lines`, zmiana `HALF_UP`, "poprawienie" kolejności zaokrągleń, dodanie waluty, zmiana komunikatu `UNKNOWN`.

### Typowe błędy uczestników

- Wszystkie wydzielenia w jednej edycji, testy uruchomione dopiero na końcu.
- Zamiana sumowania na sumę wartości zaokrąglonych "bo tak prościej" - zmiana zachowania finansowego.
- Aktualizacja oczekiwania w teście, gdy ten padł, bez zrozumienia przyczyny.
- Użycie `toUpperCase()` bez `Locale.ROOT` przy przepisywaniu normalizacji klienta.
- Zgubienie końcowego `\n` przy przebudowie `StringBuilder`.
- Traktowanie zielonego testu różnicowego jako dowodu równoważności dla wszystkich danych.

### Pytania do dyskusji

- Który z testów zostawicie po usunięciu `LegacyInvoiceFormatter`?
- Co zrobicie, jeśli księgowość stwierdzi, że subtotal powinien być sumą wartości prezentowanych? (tryb funkcjonalny, 8.4)
- Które reguły potwierdzilibyście z domeną przed przemianowaniem testów z `documents...` na język wymagań?

---

## Ćwiczenie 3: Strategia testów, obiekty zastępcze i pokrycie

### Część A: role obiektów zastępczych (teoria 11.3)

| Współpracownik | Rola i rodzaj weryfikacji | Może wykryć | Nie może wykryć |
| --- | --- | --- | --- |
| `catalogStub` | stub; wynik usługi i zapisany stan pośrednio potwierdzają użycie ceny | pominięcie ceny albo błędne mnożenie jej przez liczbę sztuk | błędne wyszukiwanie SKU w rzeczywistym katalogu |
| `paymentStub` | stub; wynik i stan repozytorium potwierdzają propagację identyfikatora autoryzacji | zgubienie lub zmianę identyfikatora zwróconego przez bramkę | błędny token, kwotę wywołania i integrację z operatorem płatności |
| `repositoryFake` | fake; test odczytuje stan przez pomocniczą metodę `find` | zapis niepoprawnego `OrderDraft` albo brak zapisu | błędny SQL, mapowanie, ograniczenia i semantykę transakcji |
| `publisherSpy` | spy; test weryfikuje zarejestrowaną interakcję i jej dane | brak zdarzenia, złą liczbę zdarzeń albo błędną treść | serializację, dostarczenie i konfigurację rzeczywistego brokera |
| `paymentMock` | mock; test jawnie weryfikuje token, kwotę i liczbę wywołań | niepoprawny protokół obciążenia, w tym wielokrotne wywołanie | zachowanie produkcyjnej bramki i poprawność całego procesu rozliczenia |
| `publisherStub` | stub bez odpowiedzi; test nie weryfikuje publikacji | umożliwia wykonanie ścieżki, ale sam nie wykrywa braku publikacji | wszystkie defekty protokołu i integracji publikatora |

Nazwa `stub` jest właściwsza dla `publisherStub` niż `dummy`, ponieważ metoda `publish` rzeczywiście zostaje wywołana. W drugim teście `catalogStub` i `repositoryFake` pełnią te same role co w pierwszym, ale test nie sprawdza ich skutków.

**Pytanie 2 (stan, wynik, interakcja):** pierwszy test weryfikuje głównie wynik (`total`, `authorizationId`) i stan (`repositoryFake.find`), a interakcję tylko przez listę zdarzeń spy. Drugi test weryfikuje interakcję (protokół płatności). Weryfikacja interakcji jest tu uzasadniona, bo liczba obciążeń jest częścią kontraktu (płatność nie może zostać pobrana dwukrotnie, 3.2).

**Pytanie 5 (kandydat do wspólnego testu kontraktu):** `InMemoryOrderRepository`. Nie wykryje błędnego SQL, ograniczeń bazy, mapowania `BigDecimal`, izolacji transakcji ani zachowania generatora identyfikatorów. Produkcyjny adapter wymaga wąskiego testu integracyjnego. `find` jest metodą pomocniczą wyłącznie klasy `InMemoryOrderRepository`, a nie częścią `OrderRepository`, więc nie da się uruchomić identycznego testu odczytu przez sam publiczny interfejs. Wspólny kontrakt może opisywać wyłącznie obserwowalne elementy wspólne, np. semantykę zwracanego identyfikatora (unikalność kolejnych wartości). Sprawdzenie trwałego zapisu wymaga jawnego mechanizmu obserwacji właściwego dla środowiska testowego, np. zapytania kontrolnego do rzeczywistej bazy. Nie należy dodawać metody odczytu do interfejsu produkcyjnego wyłącznie po to, aby ujednolicić testy.

**Kolejność zapisu i publikacji:** jest warta sprawdzania tylko wtedy, gdy wynika z mechanizmu niezawodności, np. zdarzenie może zostać opublikowane dopiero po trwałym zapisie albo oba działania są koordynowane przez outbox. Sama obecna kolejność wywołań w kodzie nie uzasadnia kruchego testu interakcji.

### Część B: raport pokrycia

- **Linie a gałęzie:** `DeliveryFeeLineCoverageTest` wywołuje tylko `fee(true)`. Wszystkie linie `fee` są wykonane (przypisanie `100`, warunek, przypisanie `0`, `return`), ale warunek `if (premium)` miał tylko wynik `true`. Pokrycie gałęzi pokazuje 1 z 2, pokrycie linii 100%.
- **Test brakującej gałęzi:** wywołanie `DeliveryFee.fee(false)` z asercją oczekującą dokładnie `100` (kod testu jak w Aktywności 6.5). Kontrolna mutacja `100` na `200` musi spowodować porażkę.
- **Test zwiększający pokrycie bez ochrony:** test bez asercji albo z asercją sprawdzającą tylko brak wyjątku (np. `assertDoesNotThrow(() -> DeliveryFee.fee(false))`) podniesie pokrycie, lecz nie wykryje zmiany `100` na `200`. W projekcie podobny charakter ma `Module2ExamplesTest` (`assertDoesNotThrow` dla całego `main`): podnosi pokrycie wielu klas, nie chroniąc żadnej reguły.
- **Rozsądne użycie progu:** próg może blokować znaczący spadek pokrycia zmienianego pakietu i kierować uwagę na nowe, nieprzetestowane gałęzie. Nadal potrzebny jest przegląd ryzyka, asercji i wyłączeń. Globalny procent nie powinien pozwalać, aby dobrze pokryty kod banalny ukrywał brak ochrony reguły krytycznej. Uzupełnieniem może być test mutacyjny, którego wynik też wymaga interpretacji (6.4).

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
- Czego nie mierzy raport pokrycia? (siły asercji, zgodności z wymaganiem, reprezentatywności danych, kombinacji ścieżek, 6.3)

---

## Ćwiczenie 4: Wprowadzenie szwu

### Kroki 1-2: analiza (teoria 11.4)

- **Niedeterministyczne wejście:** globalne źródło czasu `LocalDate.now(ZoneOffset.UTC)` uniemożliwia kontrolowanie pośredniego wejścia.
- **Efekt trudny do obserwacji:** bezpośrednie użycie `System.out.printf` - efekt nie jest reprezentowany przez port.
- **Górna granica:** `today + 7` włącznie (`isAfter` zwraca `false` dla dokładnie +7).
- **Brak dolnej granicy:** wiadomość jest wysyłana również dla daty dzisiejszej i dat przeszłych.

Istotne przypadki:

- data odnowienia dokładnie siedem dni po dzisiejszej dacie: wiadomość ma zostać wysłana,
- data odnowienia osiem dni po dzisiejszej dacie: wiadomość nie ma zostać wysłana,
- data odnowienia w przeszłości: wiadomość również zostaje wysłana, ponieważ zastana reguła nie ma dolnej granicy.

Jeżeli wymaganie domenowe definiuje przedział od dnia dzisiejszego do siedmiu dni włącznie, dodanie warunku odrzucającego daty przeszłe będzie osobną zmianą funkcjonalną. Nie należy wprowadzać go po cichu podczas rozrywania zależności.

### Kroki 3-4: minimalny szew

**Kod referencyjny:** `src/main/java/pl/training/module2/SeamedReminderService.java` (klasa bez `final`, metody `protected currentDate()` i `protected sendMessage(email, renewalDate)`) oraz `src/test/java/pl/training/module2/SeamedReminderServiceTest.java` z podklasą `TestableReminderService`. Testy dla dziś = 2026-08-30: `sendsReminderForRenewalExactlySevenDaysAway` (2026-09-06, wysyłka), `doesNotSendReminderMoreThanSevenDaysBeforeRenewal` (2026-09-07, brak), `documentsCurrentBehaviorForPastRenewalDate` (2026-08-29, wysyłka).

Technika: Subclass and Override Method (Feathers). Koszty: rozszerzenie powierzchni dziedziczenia tylko dla testów, sprzężenie testu z metodami chronionymi, zachęta do hierarchii bez znaczenia domenowego, brak jawnego portu. Zdjęcie `final` jest refaktoryzacją tylko dla typu wewnętrznego z konsumentami pod kontrolą.

### Kroki 5-6: jawne zależności

**Kod referencyjny:** `src/main/java/pl/training/module2/ReminderService.java` (konstruktor `ReminderService(Clock clock, ReminderGateway reminderGateway)`, interfejs `ReminderGateway`) oraz `src/test/java/pl/training/module2/ReminderServiceTest.java` (`Clock.fixed(Instant.parse("2026-08-30T10:00:00Z"), ZoneOffset.UTC)` i spy `RecordingReminderGateway`). Techniki: Parameterize Constructor i Extract Interface.

### Krok 7: punkty aktywacji

- Szew przejściowy: `SeamedReminderService` tworzy minimalny szew przez `currentDate` i `sendMessage`. Punktem aktywacji jest utworzenie `TestableReminderService`, a dynamiczne wiązanie wybiera nadpisane metody.
- Projekt docelowy: `ReminderService` zastępuje dziedziczenie przekazanymi zależnościami. Punktem aktywacji jest wyrażenie `new ReminderService(...)`, a jego argumenty wybierają zachowanie. Test przekazuje `Clock.fixed` oraz `RecordingReminderGateway`. Produkcja przekaże zegar systemowy z jawną strefą (tu UTC, aby zachować zachowanie wersji legacy) i rzeczywisty adapter komunikacyjny.

### Krok 8: test rzeczywistego adaptera

Test jednostkowy usługi nie potwierdzi, że adapter poprawnie serializuje komunikat, uwierzytelnia się, obsługuje odpowiedzi ani ponowienia. Te ryzyka należy sprawdzić wąskim testem integracyjnym z rzeczywistą technologią lub środowiskiem o zgodnym kontrakcie.

### Usunięcie rozwiązania przejściowego

Rozwiązanie przejściowe można usunąć po przeniesieniu wywołań produkcyjnych i testów na `ReminderService`. Usunięcie powinno być osobnym, małym krokiem, po którym ponownie uruchamiany jest pełny zestaw testów. Pełna kolejność: teoria 9.6 (nazwij zachowanie, wybierz punkt uruchomienia i obserwacji, wprowadź najmniejszy szew, zabezpiecz testami, przenieś wybór do jawnego punktu aktywacji, zastąp w teście, dodaj test adaptera, usuń konstrukcję przejściową).

### Typowe błędy uczestników

- "Poprawienie" reguły przez dodanie dolnej granicy w trakcie wprowadzania szwu.
- Testy liczone względem `LocalDate.now()` (niedeterministyczne w okolicach północy i niezgodne ze strefą UTC).
- Przechwytywanie `System.out` zamiast wprowadzenia portu.
- Przekazanie w produkcji `Clock.systemDefaultZone()` zamiast zegara UTC - cicha zmiana zachowania.
- Interfejs `ReminderGateway` dodany, ale kod domenowy dalej tworzy konkretny adapter przez `new` (interfejs bez punktu aktywacji nie jest użytecznym szwem, 9.1).
- Pozostawienie `SeamedReminderService` bez decyzji o jego losie.

### Pytania do dyskusji

- Kiedy Subclass and Override Method może zostać na stałe?
- Jaki test wykryłby pomyłkę strefy czasowej w zegarze produkcyjnym?
- Które z problemów tej klasy to separacja, a które obserwacja? (9.2)

---

## Sprawdzenie wiedzy: odpowiedzi

1. **Co odróżnia refaktoryzację od zmiany funkcjonalnej?** Refaktoryzacja zmienia strukturę przy zachowaniu uzgodnionych obserwacji. Zmiana funkcjonalna celowo modyfikuje co najmniej jedną z nich.
2. **Czy zachowanie obserwowalne ogranicza się do wartości zwracanej?** Nie. Obejmuje także wyjątki, efekty, protokoły, formaty, API i istotne właściwości pozafunkcjonalne.
3. **Dlaczego małe kroki ograniczają koszt diagnozy?** Po porażce niewielka ostatnia transformacja staje się głównym podejrzanym i łatwo ją przeanalizować lub wycofać.
4. **Co dokładnie oznacza zielony zestaw testów?** Żaden wykonany test nie wykrył różnicy w sprawdzanych obserwacjach. Nie jest to dowód braku wszystkich regresji.
5. **Dlaczego piramida testów nie narzuca uniwersalnych proporcji?** Koszt i wartość testu zależą od architektury, technologii i ryzyka. Ważniejsze są właściwości testów niż arbitralna liczba.
6. **Czym stub różni się od mocka?** Stub dostarcza odpowiedzi potrzebne scenariuszowi. Mock zawiera oczekiwania dotyczące interakcji i wymaga ich weryfikacji.
7. **Jakie ryzyko pozostaje po zastąpieniu bazy implementacją działającą w pamięci?** Test nie sprawdza między innymi zapytań, mapowania, ograniczeń, transakcji i zachowania rzeczywistego silnika.
8. **Co pokazuje pokrycie gałęzi, czego może nie pokazać pokrycie linii?** Pokazuje, czy wykonano różne wyniki warunków. Wszystkie linie mogą zostać wykonane mimo pominięcia jednej strony decyzji (przykład: `DeliveryFee`).
9. **Dlaczego test charakteryzujący nie dowodzi poprawności wymagania?** Zapisuje obserwowany wynik, który może być historycznym defektem lub przypadkowym szczegółem.
10. **Czym różni się seam od punktu aktywacji?** Seam jest miejscem możliwej zmiany zachowania, a punkt aktywacji jest miejscem wyboru konkretnego wariantu.
11. **Jakie dwie potrzeby realizuje rozrywanie zależności?** Separację badanego kodu od trudnej zależności oraz obserwację niedostępnego efektu.
12. **Kiedy szew przez dziedziczenie warto traktować jako przejściowy?** Gdy dziedziczenie istnieje tylko dla testowalności, rozszerza niepożądaną powierzchnię klasy i nie opisuje sensownej relacji domenowej.
