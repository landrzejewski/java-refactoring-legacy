# Moduł 3. Zasady dobrego projektowania - rozwiązania dla prowadzącego

Numeracja i nazwy odpowiadają plikowi zadań (`src/main/resources/prowadzenie/zadania/03-zasady-dobrego-projektowania.md`). Materiał teoretyczny nie zawiera osobnych „krótkich aktywności”, dlatego plik obejmuje cztery ćwiczenia i sprawdzenie wiedzy.

Kod referencyjny (stan „po”) znajduje się w `src/main/java/pl/training/module3/` w pakietach `domain`, `application`, `adapter` oraz w `Module3Examples.java`. Wersja „przed” to `legacy/LegacyDeliveryQuoteService.java`.

---

## Ćwiczenie 1: DRY, KISS i YAGNI

### Rozwiązanie wzorcowe (z teorii, sekcja 9.1)

| Sytuacja | Zalecana decyzja | Uzasadnienie |
| --- | --- | --- |
| 1. wspólna opłata paliwowa | scalić teraz | to jedna reguła stosowana do dwóch istniejących wariantów; zmiana stawki musi być spójna |
| 2. dwa limity po 1000 kg | pozostawić lokalnie | podobna wartość nie dowodzi wspólnej wiedzy; limity mają inne znaczenie i mogą mieć innych właścicieli |
| 3. oczekiwane ceny w testach | pozostawić jawnie | test nie powinien wyliczać oczekiwanego wyniku algorytmem produkcyjnym; niezależność wyroczni ma większą wartość |
| 4. hipotetyczny system pluginów | nie budować | brak aktualnej potrzeby, a koszt obejmuje odkrywanie, bezpieczeństwo, wersjonowanie i diagnostykę |
| 5. zatwierdzony trzeci wariant | przygotować najwęższe rozszerzenie | potrzeba jest aktualna i znana; nadal nie uzasadnia mechanizmu pluginów, jeśli wariant jest kompilowany razem z aplikacją |
| 6. cache dla zmierzonego limitu | dopuścić kontrolowaną duplikację | wydajność jest aktualnym wymaganiem; trzeba określić źródło prawdy, ważność, unieważnianie i zachowanie po błędzie |

**Interfejs `Surcharge`:** obecnie niepotrzebny. `FuelSurcharge` jest jedną stabilną regułą domenową, a żadna granica techniczna ani drugi wariant nie wymaga podmiany. Interfejs można dodać później, gdy pojawi się rzeczywisty kontrakt wielu zachowań. Utworzenie go wyłącznie dla mockowania zwiększyłoby liczbę pojęć bez nowej semantyki.

### Uzupełnienie pozostałych kolumn tabeli

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

| Nr | Wiedza | Właściciel i powód zmiany | Koszt błędnej decyzji | Sygnał ponownej oceny |
| --- | --- | --- | --- | --- |
| 1 | stawka opłaty paliwowej i sposób jej doliczenia z zaokrągleniem `HALF_UP` | właściciel polityki cenowej; zmiana cen paliwa | pozostawienie: przy zmianie stawki jedna gałąź zostanie pominięta i ceny się rozjadą | biznes wprowadza różne stawki dla różnych metod dostawy (wtedy wiedza przestaje być wspólna) |
| 2 | dwa różne limity: maksymalna masa przesyłki i maksymalne obciążenie magazynu | inni właściciele (oferta przewozowa vs. operacje magazynowe) | centralizacja: zmiana jednego limitu niepostrzeżenie zmienia drugi, fałszywa zależność i wymuszona koordynacja | oba limity okazują się wynikać z jednej reguły (np. przepisu), co potwierdzi właściciel |
| 3 | oczekiwany wynik scenariusza testowego (wyrocznia) | autor testu; zmiana wymagania cenowego | centralizacja (wyliczanie oczekiwań kodem produkcyjnym): ten sam defekt po obu stronach asercji | przygotowanie danych staje się nieczytelne; wtedy wspólne fabryki danych, ale nadal jawne wartości oczekiwane |
| 4 | przewidywana możliwość definiowania taryf przez partnerów | brak aktualnego właściciela wymagania | budowa teraz: koszt implementacji, testów, bezpieczeństwa, wersjonowania; prawdopodobna przebudowa, gdy prawdziwe wymaganie okaże się inne | zatwierdzone wymaganie od produktu z opisem, kto i jak definiuje taryfy |
| 5 | nowy algorytm ceny trzeciej metody dostawy | właściciel polityki cenowej, termin 3 tygodnie | pozostawienie bez przygotowania: kolejna gałąź w centralnym rozgałęzieniu; nadmiar: mechanizm pluginów bez potrzeby | nowy wariant nie spełnia kontraktu istniejących polityk (np. wycena sieciowa, patrz ćwiczenie 4) |
| 6 | wynik wyceny, którego źródłem prawdy jest serwis cenowy | właściciel wydajności i właściciel serwisu cenowego | brak cache: niespełniony zmierzony limit czasu; cache bez reguł: nieaktualne ceny | zmiana limitu, zmiana częstotliwości aktualizacji cen, incydent ze starą ceną; test inwariantu synchronizacji |

Najprostsze poprawne rozwiązanie dla sytuacji 1 w projekcie: `src/main/java/pl/training/module3/domain/FuelSurcharge.java` wstrzykiwany do obu polityk. Dla sytuacji 3: `src/test/java/pl/training/module3/domain/DeliveryPriceCalculatorTest.java` z jawnymi `17.28` i `31.32`.

### Typowe błędy uczestników

- Scalanie sytuacji 2 „bo to ta sama liczba”.
- Budowanie wspólnego helpera w testach, który liczy cenę tak samo jak produkcja.
- Traktowanie sytuacji 5 jako uzasadnienia pluginów albo rejestru ładowanego refleksją.
- Uznanie cache za „naruszenie DRY” bez rozróżnienia kontrolowanej duplikacji.
- Wydzielenie `basePrice(base, rate, weight)` wspólnego dla STANDARD i EXPRESS (fałszywa abstrakcja, 7.3).
- Uzasadnianie interfejsu `Surcharge` łatwością mockowania.

### Pytania do dyskusji

- Po czym poznać, że dwie reprezentacje to ta sama wiedza? Kogo o to zapytać?
- Czy reguła trzech pomogłaby w sytuacji 1? (Nie: dwie reprezentacje potwierdzonej reguły mogą wymagać natychmiastowej centralizacji.)
- Jak rozdzielić istniejącą fałszywą abstrakcję? (Testy, przeniesienie kodu z powrotem do kontekstów, usunięcie zbędnych gałęzi, dopiero potem wydzielenie potwierdzonej wspólnej wiedzy, sekcja 2.2.)

---

## Ćwiczenie 2: SOLID jako seria bezpiecznych zmian

### Aktorzy zmiany (z teorii, sekcja 9.2)

| Obszar | Aktor zmiany | Właściwa odpowiedzialność |
| --- | --- | --- |
| cennik i opłata paliwowa | właściciel polityki cenowej | domena |
| zapis wyceny | właściciel danych i operacji | adapter realizujący port aplikacji |
| treść i kanał komunikatu | właściciel komunikacji z klientem | adapter powiadomień |
| kolejność utworzenia, zapisu i powiadomienia | właściciel procesu aplikacyjnego | przypadek użycia |

### Macierz kontraktu LSP (z teorii, sekcja 9.2)

| Element kontraktu | Gwarancja typu bazowego | Jak sprawdzić | Przykład naruszenia |
| --- | --- | --- | --- |
| obsługiwana metoda | `method()` zwraca stabilną, niepustą wartość zgodną z daną polityką | wspólny test odczytujący metodę co najmniej dwa razy | polityka EXPRESS rejestruje się jako STANDARD albo zmienia wartość w czasie |
| dane wejściowe | każda prawidłowa `Parcel` jest akceptowana | przypadki graniczne oraz testy generatywne, jeśli ryzyko je uzasadnia | EXPRESS odrzuca paczki powyżej 10 kg bez takiego ograniczenia w kontrakcie |
| wynik | cena jest nieujemna | wspólny test kontraktowy | wartość `-1.00` sygnalizująca brak ceny |
| zaokrąglenie | wynik ma skalę dwa | asercja na `scale()` i wartościach granicznych | zwrot `17.280` albo niejawnie inna reguła zaokrąglenia |
| deterministyczność | te same dane dają ten sam wynik | dwa wywołania oraz właściwe testy własności | wynik zależny od bieżącego czasu bez jawnego wejścia |
| efekty uboczne | obliczenie nie zmienia wejścia i nie wysyła komunikatów | obserwacja stanu oraz test podwójnego wywołania | zapis do bazy wewnątrz `priceFor` |
| wyjątki | prawidłowa przesyłka nie powoduje błędu implementacyjnego | testy wszystkich implementacji i przypadków brzegowych | `UnsupportedOperationException` dla legalnej masy |

Test kontraktowy jest uzupełnieniem dokumentacji, nie kompletnym formalnym dowodem. Konkretne ceny nadal wymagają testów poszczególnych polityk. Test przypadku użycia powinien chronić decyzję, że nie powiadamiamy po nieudanym zapisie. Jeśli właściciel procesu wybierze inną semantykę, należy zmienić test oraz implementację świadomie.

**Decyzja do kroku 2 (prowadzący jako właściciel procesu):** kod referencyjny przyjmuje, że po nieudanym zapisie **nie** wolno wysłać powiadomienia. Tę odpowiedź należy podać uczestnikom, gdy o nią zapytają.

### Przebieg kroków odniesiony do kodu referencyjnego

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

| Krok | Wynik w projekcie | Chroniona oś zmiany | Dodany koszt |
| --- | --- | --- | --- |
| 1. aktorzy | tabela powyżej | rozpoznanie osi: cennik, zapis, komunikacja, proces | brak |
| 2. decyzja o awarii zapisu | wymaganie „brak powiadomienia po błędzie zapisu” | protokół efektów ubocznych | konieczność testu po utworzeniu punktu podmiany |
| 3. opłata paliwowa | `domain/FuelSurcharge.java` | zmiana stawki w jednym miejscu (DRY) | nowy typ domenowy, walidacja stawki `0..1` |
| 4. kontrakt i pierwszy wariant | `domain/DeliveryPricePolicy.java`, `domain/StandardDeliveryPricePolicy.java` | niezależne cenniki (SRP, OCP) | kontrakt do utrzymania |
| 5. testy po pierwszym wariancie | `legacy/LegacyDeliveryQuoteServiceCharacterizationTest` i test nowej polityki zielone | bezpieczeństwo małego kroku | czas uruchomienia |
| 6. drugi wariant i test kontraktowy | `domain/ExpressDeliveryPricePolicy.java`, `domain/DeliveryPriceCalculator.java`, test `domain/DeliveryPricePolicyContractTest.java` | substytucja polityk (LSP) | lista implementacji w teście do utrzymania, rejestracja w kalkulatorze |
| 7. porty | `application/QuoteRepository.java`, `application/QuoteNotifier.java` | przypadek użycia zna tylko potrzebne role (ISP, DIP) | dwa kontrakty |
| 8. test awarii | `CreateDeliveryQuoteTest.doesNotNotifyWhenSavingFails` | uzgodniony protokół | test do utrzymania |
| 9. adaptery | `adapter/InMemoryQuoteRepository.java`, `adapter/ConsoleQuoteNotifier.java` | wymiana mechanizmu zapisu i kanału bez edycji przypadku użycia | klasy adapterów, w realnym systemie testy integracyjne |
| 10. składanie | `Module3Examples.java` (ręczny composition root) | wybór konkretów w jednym miejscu (KISS, YAGNI: bez kontenera) | ręczna aktualizacja konfiguracji |
| 11. porównanie | wyjście programu: dwie identyczne linie `Quote ready ... 17.28`, `Legacy and refactored prices equal: true`, `Stored quotes: 1` | dowód zachowania cen i komunikatu | brak |

Rdzeń przypadku użycia po zmianie (`application/CreateDeliveryQuote.java`):

```java
DeliveryQuote quote = new DeliveryQuote(
        command.customerEmail(),
        command.method(),
        command.parcel(),
        priceCalculator.priceFor(command.method(), command.parcel()));

repository.save(quote);
notifier.quoteCreated(quote);
return quote;
```

Kryteria akceptacji są spełnione przez kod referencyjny: ceny `17.28` / `31.32` (`DeliveryPriceCalculatorTest`), brak importów `adapter` w `application`, jedna reprezentacja `0.08` (wstrzykiwana w composition root), wspólny test kontraktowy, `doesNotNotifyWhenSavingFails`.

### Typowe błędy uczestników

- Wydzielanie wszystkiego naraz, bez uruchamiania testów po pierwszym wariancie.
- Scalanie cenników STANDARD i EXPRESS w jedną sparametryzowaną metodę.
- Umieszczenie stawki `0.08` jako stałej w każdej polityce (dwie reprezentacje wiedzy nadal istnieją).
- Port nazwany technologią (`JdbcQuoteRepository` jako interfejs) albo ogólny `CrudRepository` w aplikacji.
- Tworzenie interfejsu dla `DeliveryPriceCalculator` „bo DIP” (w referencji świadomie go nie ma, 7.5).
- Utrata formatu komunikatu przy przenoszeniu do adaptera (test charakterystyki tego nie wykryje; wykryje porównanie wyjścia w kroku 11).
- Pominięcie decyzji z kroku 2 i traktowanie kolejności `save`/`quoteCreated` jako oczywistej.
- Przypisywanie klas do pojedynczych liter SOLID zamiast opisu osi zmiany i kosztu.
- `DeliveryQuote` z `RoundingMode.UNNECESSARY`: polityka zwracająca więcej niż dwa miejsca po przecinku kończy się `ArithmeticException`, a nie zaokrągleniem.

### Pytania do dyskusji

- Która zmiana była refaktoryzacją, a która nową decyzją zachowania (awaria zapisu)?
- Czy przy jednej metodzie dostawy Strategy byłaby uzasadniona?
- Co w teście kontraktowym nie jest sprawdzane (niezmienność wejścia, brak efektów ubocznych, wszystkie masy)?
- Czy brak polityki powinien być wykrywany w konstruktorze kalkulatora czy przy żądaniu?

---

## Ćwiczenie 3: granica i reguła zależności

### Zadanie A: klasyfikacja zależności (z teorii, sekcja 9.3)

| Zależność | Ocena | Uzasadnienie |
| --- | --- | --- |
| 1. domena do `BigDecimal` | dozwolona | jest to typ Java SE przydatny do jawnego modelowania wartości dziesiętnych; nie jest szczegółem adaptera |
| 2. aplikacja do `InMemoryQuoteRepository` | niedozwolona | wnętrze zależałoby od konkretnego mechanizmu zapisu |
| 3. adapter do `QuoteRepository` | dozwolona | zewnętrzny szczegół implementuje port należący do wnętrza |
| 4. domena do encji ORM | niedozwolona w przyjętym modelu | typ frameworka przenosi decyzję trwałości do polityki domenowej |
| 5. composition root do wszystkich konkretów | dozwolona | właśnie tam podejmowana jest zewnętrzna decyzja o składaniu |
| 6. notifier do `DeliveryQuote` | dozwolona | adapter realizuje port posługujący się modelem kontraktu; w większym systemie można mapować na osobny model granicy |
| 7. wywołanie adaptera przez port | dozwolone | przepływ sterowania na zewnątrz nie odwraca zależności źródłowej |
| 8. wyjątek JDBC w porcie | niedozwolony | szczegół dostawcy przecieka do kontraktu aplikacji; adapter powinien przetłumaczyć błąd na semantykę granicy |

Diagram oczekiwany od uczestników (z 7.11 i 5.2):

```text
Zależności źródłowe:              Przepływ sterowania (runtime):
Module3Examples -> adapter        Module3Examples
Module3Examples -> application      -> CreateDeliveryQuote.execute
adapter -> application (porty)          -> DeliveryPriceCalculator -> polityka
adapter -> domain                        -> QuoteRepository (InMemoryQuoteRepository)
application -> domain                    -> QuoteNotifier (ConsoleQuoteNotifier)
```

### Zadanie B: nowy adapter

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Kod referencyjny nie zawiera `BufferingQuoteNotifier`. Przykładowa implementacja zgodna ze wzorcem `InMemoryQuoteRepository` i formatem `ConsoleQuoteNotifier`:

```java
package pl.training.module3.adapter;

public final class BufferingQuoteNotifier implements QuoteNotifier {
    private final List<String> messages = new ArrayList<>();

    @Override
    public void quoteCreated(DeliveryQuote quote) {
        Objects.requireNonNull(quote);
        messages.add("Quote ready for %s: %s costs %s".formatted(
                quote.customerEmail(), quote.method(), quote.price()));
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}
```

Test adaptera (analogiczny do `InMemoryQuoteRepositoryTest`): utwórz `DeliveryQuote("developer@example.com", STANDARD, new Parcel(new BigDecimal("3.00")), new BigDecimal("17.28"))`, wywołaj `quoteCreated`, sprawdź `assertEquals(List.of("Quote ready for developer@example.com: STANDARD costs 17.28"), notifier.messages())`. Warto dodać sprawdzenie, że modyfikacja zwróconej listy rzuca `UnsupportedOperationException` albo nie wpływa na bufor.

Zmiana w composition root (`Module3Examples.java`), jedyne edytowane miejsce poza nowymi plikami:

```java
BufferingQuoteNotifier notifier = new BufferingQuoteNotifier();
CreateDeliveryQuote useCase = new CreateDeliveryQuote(calculator, repository, notifier);
```

Weryfikacja: `git diff --stat` nie pokazuje zmian w `application/` ani `domain/`; `mvn verify` zielony.

Punkty do omówienia:

- **Zmiana obserwowalnego wyjścia programu:** po podmianie druga linia `Quote ready ...` znika ze standardowego wyjścia. `Module3ExamplesTest` tego nie wykryje (tylko `assertDoesNotThrow`). Należy to nazwać świadomą zmianą zachowania programu demonstracyjnego, albo wypisać zawartość bufora, albo podmieniać adapter tylko w teście demonstracyjnym.
- **Format komunikatu w dwóch adapterach:** czy to ta sama wiedza (DRY)? Jeśli właściciel komunikacji wymaga identycznej treści w obu kanałach, można wydzielić formatter w pakiecie `adapter`; jeśli bufor służy tylko testom, lokalna kopia jest akceptowalna. Nie wolno przenosić formatowania do aplikacji ani domeny.
- Brak frameworka DI i pliku konfiguracyjnego: ręczna podmiana jest pełnoprawna (5.6).
- Bufor nie jest bezpieczny wątkowo, tak samo jak `InMemoryQuoteRepository`; w demonstracji to akceptowalne.

### Zadanie C: automatyczna ochrona granicy

Porównanie wariantów (z teorii, sekcja 9.3): najprostsza kontrola tekstowa importów jest tania, ale słabo rozumie język i może generować wyniki fałszywe. Test architektoniczny daje czytelne reguły na poziomie typów. Osobne moduły Maven lub JPMS dostarczają silniejszej granicy kompilacji i widoczności, lecz zwiększają koszt budowania oraz konfiguracji. Należy dobrać siłę mechanizmu do znaczenia granicy.

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

| Wariant | Siła gwarancji | Koszt utrzymania | Komunikat o błędzie | Uwagi dla tego projektu |
| --- | --- | --- | --- | --- |
| kontrola statyczna w CI (`grep` importów) | najsłabsza: nie widzi pełnych nazw w kodzie, importów z `*`, refleksji; możliwe fałszywe alarmy w komentarzach | bardzo niski | surowy wynik narzędzia, zwykle ścieżka i linia | można uruchomić od razu |
| test JUnit skanujący pliki źródłowe | jak wyżej, ale w `mvn verify` | niski, bez nowych zależności | własny, czytelny komunikat asercji | pasuje do obecnego `pom.xml` (tylko JUnit) |
| narzędzie do testów architektury (np. ArchUnit) | reguły na poziomie typów w bytecode | średni: nowa zależność testowa | bardzo czytelny, wskazuje klasę i zależność | w `pom.xml` brak takiej zależności; trzeba ją dodać |
| osobne moduły Maven | kompilator odrzuca import | wysoki: restrukturyzacja projektu, build wielomodułowy | błąd kompilacji „package does not exist” | wymaga rozbicia jednego modułu Maven (7.11) |
| JPMS | silna granica widoczności pakietów | średni do wysokiego: `module-info.java`, eksporty | błąd kompilacji o widoczności | w jednym module JPMS nie oddzieli pakietów tego samego modułu; potrzebne i tak osobne moduły |

Przykładowy test bez nowych zależności (szkic do pokazania):

```java
@Test
void innerPackagesDoNotImportAdapters() throws IOException {
    for (String pkg : List.of("application", "domain")) {
        Path dir = Path.of("src/main/java/pl/training/module3", pkg);
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(p -> p.toString().endsWith(".java")).forEach(p ->
                assertFalse(read(p).contains("import pl.training.module3.adapter"),
                        () -> p + " imports adapter"));
        }
    }
}
```

(`read` to pomocnicze `Files.readString` z obsługą `IOException`.) Test nie wykryje użycia w pełni kwalifikowanej nazwy bez importu; to przykład słabości kontroli tekstowej.

### Typowe błędy uczestników

- Mylenie przepływu sterowania z zależnością źródłową (ocena punktu 7 jako „niedozwolone”).
- Uznanie `BigDecimal` w domenie za zależność od szczegółu technicznego.
- Zmiana portu `QuoteNotifier` (np. dodanie `messages()`) zamiast umieszczenia odczytu w adapterze.
- Podmiana adaptera w `CreateDeliveryQuote` zamiast w composition root.
- Dodawanie kontenera DI lub pliku konfiguracyjnego.
- Zwracanie wewnętrznej, modyfikowalnej listy z adaptera.

### Pytania do dyskusji

- Czy `ConsoleQuoteNotifier` powinien mieć osobny model komunikatu zamiast `DeliveryQuote`? Kiedy to się opłaca (5.5)?
- Jaka siła kontroli granicy jest adekwatna dla tego projektu, a jaka dla systemu z wieloma zespołami?
- Czy wyjście programu demonstracyjnego jest kontraktem?

---

## Ćwiczenie 4: wzorzec jako decyzja odwracalna

### Scenariusz 1: dostawa tego samego dnia (z teorii, sekcja 9.4)

SAME_DAY nie powinien automatycznie implementować `DeliveryPricePolicy`. Aktualny kontrakt obiecuje deterministyczne obliczenie bez efektów ubocznych, podczas gdy klient sieciowy może być niedostępny, zmieniać odpowiedź i powodować opóźnienia. Udawanie zgodności naruszyłoby LSP oraz ukryło istotną właściwość operacyjną.

Odpowiedzi na pytania:

1. **Rola:** współpraca ról, ale nie jako trzecia `DeliveryPricePolicy`. Przypadek użycia definiuje port wyceny zewnętrznej o jawnej semantyce błędu i dostępności; adapter przewoźnika go implementuje (wzorzec Adapter). Decyzja, czy wykonać lokalne obliczenie, czy zdalne zapytanie, może należeć do przypadku użycia poziom wyżej. Nazwanie wszystkiego `Strategy` nie usuwa różnicy semantycznej.
2. **Konwersja kg na g:** w adapterze przewoźnika. Jeżeli kontrakt wymaga dokładności do jednego grama, `weightKg.movePointRight(3).longValueExact()` odrzuca zarówno ułamek grama, jak i przepełnienie. Zaokrąglenie wolno zastosować wyłącznie po uzgodnieniu kierunku i momentu zaokrąglania jako reguły biznesowej.
3. **Najmniejsze jednostki:** kwota nie jest kompletna bez waluty i liczby jej miejsc dziesiętnych. Po ustaleniu wykładnika można użyć `BigDecimal.valueOf(minorUnits, fractionDigits)`. Obecny `DeliveryQuote` nie przechowuje waluty, więc system musi wymagać jednej skonfigurowanej waluty albo rozszerzyć model o wartość pieniężną z kwotą i kodem waluty. Adapter musi odrzucić niespodziewaną walutę.
4. **Błąd na granicy:** adapter tłumaczy typ błędu dostawcy na błąd należący do kontraktu portu. Klasy SDK nie trafiają do wnętrza.
5. **Brak efektów ubocznych:** nie, dla wyceny sieciowej kontrakt `DeliveryPricePolicy` nie może tego obiecywać. Dlatego potrzebny jest osobny port, a nie trzeci wariant tej samej strategii.
6. **Timeout i ponowienia:** timeout w konfiguracji klienta w adapterze. Ponowienia wymagają analizy idempotencji, budżetu czasu i charakteru błędów.
7. **Testy:** test adaptera uruchamia się wobec kontrolowanego serwera zastępczego lub środowiska testowego dostawcy. Test przypadku użycia korzysta z implementacji portu zwracającej ustalone wyniki (jak lambdy w `CreateDeliveryQuoteTest`). Domena i przypadek użycia nie importują typów SDK przewoźnika.

Dodatkowo do omówienia: `ShippingMethod` jest enumem, więc SAME_DAY wymaga jego zmiany; `DeliveryPriceCalculator` zgłosi `IllegalArgumentException` dla metody bez lokalnej polityki, więc ścieżka zdalna musi zostać rozgałęziona przed kalkulatorem (w przypadku użycia), a nie ukryta w nim.

### Scenariusz 2: uproszczenie (z teorii, sekcja 9.4)

Po usunięciu EXPRESS można uprościć projekt do jednej klasy kalkulatora, jeśli rejestr i interfejs nie chronią już realnej granicy. Warto zachować testy ceny, zaokrąglenia i inwariantów. Koszt ponownego wydzielenia strategii jest zwykle mały przy istniejących testach. Nie należy jednak usuwać portów zapisu i powiadamiania tylko dlatego, że zniknęła zmienność cennika. Te granice chronią inne decyzje.

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

Odniesienie do plików:

- do usunięcia lub wchłonięcia: `domain/DeliveryPricePolicy.java`, `domain/ExpressDeliveryPricePolicy.java`, rejestr `EnumMap` w `domain/DeliveryPriceCalculator.java`; reguła STANDARD (`10.00 + masa x 2.00`, opłata przez `FuelSurcharge`) może trafić do kalkulatora,
- do zachowania: `FuelSurcharge` (nadal jedna reguła), porty `application/QuoteRepository.java`, `application/QuoteNotifier.java`, adaptery, composition root,
- testy: z `DeliveryPricePolicyContractTest` zachować jako testy zachowania sprawdzenia skali 2, nieujemności i deterministyczności kalkulatora; z `DeliveryPriceCalculatorTest` zachować przypadek STANDARD `17.28`; testy `rejectsDuplicatePolicyForOneShippingMethod` i `reportsMissingPolicy` tracą sens razem z rejestrem,
- `ShippingMethod.EXPRESS` znika z enuma; testy charakterystyki legacy (`documentsExpressDeliveryPrice`) i gałąź `legacy` trzeba potraktować osobno (gałąź demonstracyjna),
- ponowne dodanie wariantu dotknęłoby: enuma, kalkulatora (ponowne wydzielenie kontraktu), composition root i testów; przy zielonych testach to kilka małych kroków.

### Typowe błędy uczestników

- Implementacja SAME_DAY jako trzeciej `DeliveryPricePolicy` z wywołaniem sieciowym w `priceFor`.
- Przekazanie typów SDK przewoźnika lub jego wyjątków do domeny albo aplikacji.
- Ciche zaokrąglanie gramów lub założenie waluty bez uzgodnienia.
- Umieszczenie timeoutu i ponowień w przypadku użycia lub domenie.
- W scenariuszu 2: usunięcie także portów i adapterów „przy okazji” albo pozostawienie Strategy „na przyszłość” bez uzasadnienia kosztowego.
- Proponowanie mechanizmu pluginów dla przyszłych przewoźników.

### Pytania do dyskusji

- Jaki problem rozwiązuje Strategy, a jaki Adapter w tym scenariuszu?
- Po jakim sygnale uznamy, że wzorzec stał się nadmiarowy (6.5)?
- Czy refaktoryzacja od wzorca jest „cofaniem się”? (Nie, jest równie poprawna jak refaktoryzacja do wzorca.)

---

## Sprawdzenie wiedzy: odpowiedzi (z teorii, sekcja 10)

1. Wiedza i decyzje systemu. Podobny tekst może reprezentować różną wiedzę.
2. Gdy mają inne znaczenie, innych właścicieli lub niezależne powody zmian.
3. Kilka spójnych, nazwanych elementów może zmniejszyć złożoność poznawczą i promień zmian mimo większej liczby plików.
4. Nie. Testy aktualnego zachowania wspierają bezpieczną zmianę i pozwalają odkładać spekulacyjne decyzje.
5. Jako odpowiedzialność wobec aktora lub spójnej grupy aktorów żądających określonej zmiany.
6. Nie. Dla małego, celowo zamkniętego zbioru wariantów `switch` może być najprostszym poprawnym modelem.
7. Podtyp nie wzmacnia warunków wstępnych i nie osłabia warunków końcowych. Musi również zachować istotne inwarianty oraz efekty kontraktu.
8. Wstrzyknięcie zależności opisuje sposób dostarczenia obiektu. DIP dotyczy kierunku zależności źródłowej i własności abstrakcji.
9. Nie. Interfejs jest zgodny z ISP, gdy odpowiada spójnej roli klienta, niezależnie od liczby metod.
10. Nie usuwa go. Zmienia zależność od konkretu na zależność od kontraktu i przenosi wybór implementacji.
11. Nie. Adapter może być wywołany w czasie wykonania, podczas gdy źródłowo zależy od portu zdefiniowanego wewnątrz.
12. Po stronie klienta, który formułuje potrzebę, zwykle w aplikacji lub domenie zależnie od znaczenia operacji.
13. Nie. Liczy się właściwe rozdzielenie polityki i szczegółu oraz kierunek zależności, a nie nazwy katalogów.
14. Gdy warianty są nieliczne, zamknięte, lokalne i zmieniają się razem, a mechanizm wyboru dodaje więcej kosztu niż chronionej zmienności.
15. Wykonuje skończony zestaw przypadków i obserwacji, a kontrakt może obejmować większą przestrzeń stanów i właściwości.
16. Gdy problem lub warianty zniknęły, a koszt abstrakcji przewyższa korzyść z chronionej zmienności.

## Listy kontrolne do zamknięcia modułu

Na zakończenie warto przejść z grupą listy kontrolne z sekcji 11 teorii (przed wydzieleniem abstrakcji, przed zastosowaniem SOLID, przed ustanowieniem granicy, przed zastosowaniem wzorca, przed zakończeniem refaktoryzacji) i poprosić uczestników o wskazanie, które punkty ich rozwiązania z ćwiczeń 2 i 3 spełniają, a których nie.
