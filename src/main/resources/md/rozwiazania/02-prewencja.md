# Moduł 2. Prewencja - rozwiązania dla prowadzącego

Materiał wyłącznie dla prowadzącego. Numeracja i nazwy odpowiadają plikowi zadań (`src/main/resources/md/zadania/02-prewencja.md`), a odwołania do slajdów - plikowi `src/main/resources/md/slides/02-prewencja-slajdy.md`.

Kod referencyjny znajduje się w `src/main/java/pl/training/module2/`: stan „po” wyceny dostawy w pakietach `domain`, `application`, `adapter` oraz w `Module2Examples.java`, wersja „przed” w `legacy/LegacyDeliveryQuoteService.java`; lokalna poprawa w `boyscout/before` i `boyscout/after`; model ADR w `documentation`; kompilator w pamięci w `tooling`. Szczegółowy przewodnik po kodzie: `src/main/resources/md/przyklady/02-prewencja.md`.

Część zadań ma charakter projektowy i nie istnieje jedno poprawne rozwiązanie. Poniższe odpowiedzi pokazują produkt spełniający kryteria akceptacji. Prace uczestników oceniaj według kryteriów i listy kontrolnej prewencji (ostatni slajd przed podsumowaniem), a nie według zgodności z tym wzorcem.

---

## Aktywność 2.1: Zmapuj swoje pętle

### Rozwiązanie

Aktywność nie ma jednej odpowiedzi. Dobra odpowiedź zawiera liczby i przykład przesunięcia, np.:

| Pętla | Czas | Przykład |
| --- | --- | --- |
| kompilacja w IDE | 1-3 s | - |
| szybkie testy modułu | 20 s | - |
| pełny build lokalny | 6 min | uruchamiany rzadko, więc testy modułu też rzadko |
| CI | 25 min | - |
| przegląd kodu | 1-2 dni | wykrył błędną nazwę pola w JSON |
| produkcja | tydzień | - |

Przesunięcie: błędną nazwę pola w JSON złapałby test serializacji kontraktu w pętli szybkich testów (sekundy zamiast dni).

### Typowe błędy uczestników

- odpowiedzi jakościowe („CI jest wolne”) zamiast czasu,
- pominięcie przeglądu kodu jako pętli informacji zwrotnej,
- przesunięcie „wszystkiego do testów jednostkowych”, także problemów, które wymagają realnej technologii.

### Pytania do dyskusji

- Która pętla jest u Was najczęściej pomijana, bo „trwa za długo”?
- Ile zmian zwykle ląduje między przyczyną błędu a sygnałem w najwolniejszej pętli?

---

## Aktywność 2.2: Drzwi jedno- czy dwukierunkowe?

### Rozwiązanie

| Decyzja | Drzwi | Uzasadnienie | Mechanizm odwracalności | ADR |
| --- | --- | --- | --- | --- |
| 1. nazwa pakietu `pricing` na `tariffs` | dwukierunkowe | wszyscy konsumenci w repozytorium; IDE i kompilator przeprowadzą zmianę | - | nie |
| 2. format nocnego pliku dla partnera | jednokierunkowe | zewnętrzny konsument wdroży parser; zmiana wymaga koordynacji | wersja formatu w nagłówku pliku, okres równoległego generowania dwóch wersji, test kontraktowy formatu | tak |
| 3. biblioteka PDF w jednym adapterze | dwukierunkowe | biblioteka zamknięta za adapterem; wymiana dotyka jednej klasy | adapter i test wyniku (np. tekst wyciągnięty z PDF) | nie |
| 4. usunięcie kolumny z historią zamówień | jednokierunkowe | utrata danych historycznych; raporty i audyt mogą z nich korzystać | expand and contract: najpierw przestać pisać, archiwum lub kopia, usunięcie po zamknięciu okna wycofania | tak |
| 5. `PricingRule` „na pluginy” | dwukierunkowe, ale z kosztem | da się usunąć, dopóki nikt spoza repozytorium nie implementuje interfejsu; po publikacji staje się jednokierunkowe | nie publikować interfejsu, nie budować bez wymagania (YAGNI) | nie (zamiast ADR: nie budować) |
| 6. `double` na `BigDecimal` w publicznym DTO | jednokierunkowe | kompatybilność źródłowa i binarna klientów biblioteki | nowe pole lub nowy typ obok starego, oznaczenie starego jako przestarzałe, okres przejściowy | tak |

### Typowe błędy uczestników

- uznanie zmiany nazwy pakietu za ryzykowną „bo dużo plików” (liczba plików nie przesądza o odwracalności),
- pominięcie danych przy decyzji 4 („kolumna jest nieużywana” - przez kogo?),
- uznanie decyzji 5 za neutralną; koszt utrzymania istnieje od pierwszego dnia, a odwracalność znika po publikacji.

### Pytania do dyskusji

- Która z Waszych ostatnich decyzji była jednokierunkowa tylko dlatego, że brakowało testów?
- Czy decyzja 2 może stać się dwukierunkowa bez udziału partnera?

---

## Aktywność 2.3: Napraw, izoluj czy zaakceptuj?

### Rozwiązanie

| Problem | Reakcja | Uzasadnienie | Właściciel | Sygnał ponownej oceny |
| --- | --- | --- | --- | --- |
| 1. SDK przewoźnika: grosze jako `long`, własne wyjątki | izolować | nie kontrolujemy SDK; adapter tłumaczy jednostki i błędy na pojęcia domeny, a reszta kodu widzi tylko port | właściciel integracji z przewoźnikiem | nowa wersja SDK, druga integracja z innym przewoźnikiem |
| 2. raporty roczne do wycofania za 4 miesiące | zaakceptować (tolerować) | krótki horyzont, brak planowanych zmian | właściciel produktu raportów | przesunięcie terminu wycofania albo zaplanowana zmiana raportu |
| 3. reguła VIP w trzech miejscach, pięć zmian w planie | naprawić | potwierdzone odsetki i wysoka ekspozycja | właściciel modułu wyceny | - (przed następną zmianą rabatu) |
| 4. 10 ostrzeżeń `auxiliaryclass` w niezmienianym module | zaakceptować (stan bazowy) | ryzyko niskie; wpis do stanu bazowego, który może tylko maleć | właściciel jakości builda | pierwsza zmiana w module albo termin przeglądu stanu bazowego |

### Typowe błędy uczestników

- „zaakceptować” bez właściciela i sygnału (to ignorowanie),
- „naprawić” SDK dostawcy (nie da się) albo opakowanie, które przepuszcza typy SDK do domeny,
- refaktoryzacja modułu raportów „bo ma najgorszą metrykę” (por. moduł 1, Ćwiczenie 3).

### Pytania do dyskusji

Co odróżnia stan bazowy ostrzeżeń od „wiecznego baseline”? (Stan bazowy ma właściciela, może tylko maleć i ma termin przeglądu.)

---

## Ćwiczenie 1: Pętle informacji zwrotnej i bramka w legacy

> Rozwiązanie opracowane na podstawie kodu referencyjnego. Wyniki pomiaru zależą od JDK i stanu repozytorium - uczestnicy muszą zmierzyć sami.

### Krok 1: problemy z części A

| Problem | Wykryto w | Najszybsza możliwa pętla | Mechanizm |
| --- | --- | --- | --- |
| 1. literówka w nazwie reguły w konfiguracji | produkcja | kompilator | typ (enum, stała, rejestr w kodzie) zamiast napisu; ewentualnie szybki test ładujący konfigurację przy starcie |
| 2. `ClassCastException` z surowego typu | ręczna regresja | kompilator | `-Xlint:rawtypes,unchecked` z bramką „brak nowych naruszeń” |
| 3. błędne zaokrąglenie ceny grupowej | klient (produkcja) | szybkie testy obszaru | test reguły z wartościami granicznymi i jawną wyrocznią, uruchamiany lokalnie w sekundach |
| 4. spór o zależność `pricing` -> mail | przegląd kodu (3 dni) | build (test architektury) albo rozmowa przed pracą | ADR z regułą i test reguły zależności w każdym buildzie |
| 5. regresja formatu po „sprzątaniu” w PR z funkcją | przegląd / produkcja | szybkie testy + struktura zestawów zmian | test charakteryzujący formatu i osobny zestaw zmian dla refaktoryzacji (Boy Scout bez zmiany zachowania) |

### Krok 2: trzy ruchy z miernikami

| Ruch | Uzasadnienie | Miernik wyniku |
| --- | --- | --- |
| wydzielić szybki zestaw testów logiki (bez kontekstu aplikacji i bazy), uruchamiany lokalnie i w CI przed scaleniem | dziś testy biegną tylko w CI po scaleniu, 25 minut | czas od zapisu pliku do wyniku testów obszaru (cel: sekundy); liczba regresji wykrytych dopiero po scaleniu |
| bramka kompilatora i reguł architektury w każdym buildzie, ze stanem bazowym | problemy 1, 2 i 4 są wykrywalne statycznie | liczba nowych ostrzeżeń w gałęzi głównej (cel: zero); liczba sporów architektonicznych w przeglądzie |
| małe zestawy zmian (jedna intencja, limit orientacyjny, refaktoryzacja osobno) | PR po 900 linii czekają 2 dni i ukrywają zmiany zachowania | mediana czasu oczekiwania na przegląd; odsetek PR wracających z `BLOCKER` |

Odrzucone jako miernik: „liczba napisanych testów”, „procent pokrycia” - to mierniki aktywności.

### Krok 3: pomiar w części B

Polecenia:

```bash
java -version
mvn -v
javac --release 25 -Xlint:all -d <tmp> $(find src/main/java -name '*.java')
```

Wynik zmierzony przy przygotowaniu materiału (JDK 26 z `--release 25`; na innym JDK liczby mogą się różnić):

| Kategoria | Liczba | Gdzie | Klasyfikacja |
| --- | --- | --- | --- |
| `auxiliaryclass` | 10 | `pl/training/module1` (klasy pomocnicze z `LegacyOrderService.java` i `RiskClassifier.java` używane spoza pliku) | prawdziwy problem o niskim ryzyku, świadomie zaakceptowany w materiale szkoleniowym |
| `rawtypes`, `unchecked`, `deprecation`, `fallthrough` | 13 | `pl/training/workshop/m2/s03_compilergate` (`start`, `step1`, `step2`) i `pl/training/workshop/m2/s04_qualitygate/sample/dirty` | celowe naruszenia - materiał scen warsztatu o bramkach |
| `this-escape` | 1 | jeden przykład w innym module szkolenia | do oceny przez właściciela modułu |

Wniosek: przy obecnym `pom.xml` włączenie `-Xlint:all -Werror` zablokowałoby build z powodu 24 historycznych ostrzeżeń, choć żadne nie jest związane z bieżącą pracą. Dodatkowo `pom.xml` ustala `--release 25`, ale nie gwarantuje JDK 25 - CI powinno jawnie kontrolować JDK (Maven Toolchains, obraz CI z przypiętym JDK, `maven-enforcer-plugin`).

### Krok 4: eksperyment z polityką

Dla źródła z `List names` (surowy typ):

| Polityka | `successful()` | Diagnostyki |
| --- | --- | --- |
| `ALLOW_WARNINGS` | `true` | `WARNING compiler.warn.raw.class.use` |
| `TREAT_WARNINGS_AS_ERRORS` | `false` | `WARNING compiler.warn.raw.class.use` oraz `ERROR compiler.err.warnings.and.werror` |

Dokładnie to sprawdzają `reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource` i `rejectsTheSameRawTypeWhenWarningsAreErrors`. Ostrzeżenie jest tym samym sygnałem, polityka decyduje, czy blokuje.

### Krok 5: reguła bramki

- **Blokuje od razu:** każde nowe ostrzeżenie kategorii `rawtypes`, `unchecked`, `deprecation`, `fallthrough` i pozostałych poza stanem bazowym.
- **Stan bazowy:** plik w repozytorium (np. `quality/baseline/javac-warnings.txt`, lista `plik:kategoria:liczba`), zmieniany tylko przez przegląd właściciela jakości (CODEOWNERS); może tylko maleć.
- **Celowe naruszenia materiału warsztatu** są wpisane do stanu bazowego z uzasadnieniem („materiał sceny o bramkach”) - to zaakceptowane ryzyko, nie wynik fałszywie dodatni.
- Gdy stan bazowy obejmuje tylko jedną kategorię w jednym miejscu, prostsza bramka to `-Xlint:all,-auxiliaryclass -Werror` z komentarzem (właściciel, termin przeglądu); przy celowych naruszeniach w wielu pakietach potrzebne jest porównanie ze stanem bazowym (ratchet).

```xml
<compilerArgs>
    <!-- auxiliaryclass: stan bazowy module1, wlasciciel: zespol szkolenia, przeglad: <data> -->
    <arg>-Xlint:all,-auxiliaryclass</arg>
    <arg>-Werror</arg>
</compilerArgs>
```

### Kod referencyjny

- `src/main/java/pl/training/module2/tooling/InMemoryJavaCompiler.java`, `WarningPolicy.java`, `CompilationResult.java`, `CompilationDiagnostic.java`,
- `src/test/java/pl/training/module2/tooling/InMemoryJavaCompilerTest.java`,
- sceny warsztatu `m2/s03_compilergate` (usuwanie ostrzeżeń po jednej kategorii) i `m2/s04_qualitygate` (bramka jako kod) pokazują ten sam temat na żywo.

### Weryfikacja kryteriów akceptacji

| Kryterium | Dowód w pracy |
| --- | --- |
| szybsza pętla i mechanizm dla każdego problemu | tabela z kroku 1, mechanizm inny niż „więcej testów” |
| mierniki wyniku | czas od commita do sygnału, problemy wykryte po scaleniu |
| pomiar bez zmian w kodzie | polecenia `javac` z katalogiem tymczasowym, `git status` czysty |
| bramka nie blokuje historycznymi naruszeniami | stan bazowy albo wąskie wyłączenie kategorii z właścicielem |
| stan bazowy nie rośnie | zasada „tylko maleje” i właściciel zmian pliku |

### Typowe błędy uczestników

- „włączamy `-Werror` od jutra” bez pomiaru, co blokuje cały zespół,
- `@SuppressWarnings("all")` albo wyłączenie reguły dla całego pakietu,
- uznanie `maven.compiler.release=25` za dowód, że build działa na JDK 25,
- stan bazowy, który może rosnąć („dopiszemy nowe naruszenie do baseline”),
- brak rozróżnienia „narzędzie się myli” od „wiemy, ale akceptujemy”,
- przesunięcie sporu architektonicznego (problem 4) do „lepszego przeglądu” zamiast do reguły sprawdzanej automatycznie.

### Pytania do dyskusji

- Co jest lepsze: wyłączyć kategorię `auxiliaryclass` czy od razu przenieść klasy pomocnicze do osobnych plików? Kto o tym decyduje?
- Czy testy sprawdzające kody `javac` (`compiler.warn.raw.class.use`) to dobra praktyka? Co się stanie przy zmianie JDK?
- Które sprawdzenia uruchomilibyście lokalnie przed commitem, a które tylko w CI?

---

## Ćwiczenie 2: Decyzje teraz, później czy nigdy

### Krok 1: tabela decyzji

| Nr | Decyzja | Drzwi | Wiedza, właściciel, powód zmiany | Najprostsze rozwiązanie na dziś | Sygnał ponownej oceny |
| --- | --- | --- | --- | --- | --- |
| 1 | **teraz** - scalić | dwukierunkowe | stawka opłaty paliwowej i sposób jej doliczenia z zaokrągleniem `HALF_UP`; właściciel polityki cenowej, zmiana cen paliwa | jeden typ `FuelSurcharge` wstrzykiwany do obu polityk | biznes wprowadza różne stawki dla różnych metod (wiedza przestaje być wspólna) |
| 2 | **nie scalać** - pozostawić lokalnie | dwukierunkowe | dwa różne limity: maksymalna masa przesyłki i obciążenie magazynu; inni właściciele | dwie nazwane stałe w swoich kontekstach | właściciel potwierdzi, że oba limity wynikają z jednej reguły (np. przepisu) |
| 3 | **nie budować** | po publikacji jednokierunkowe | przewidywana możliwość taryf partnerów; brak właściciela wymagania | brak mechanizmu; polityki kompilowane razem z aplikacją | zatwierdzone wymaganie z opisem, kto i jak definiuje taryfy |
| 4 | **teraz, najwęższe rozszerzenie** | dwukierunkowe | nowy algorytm trzeciej metody; właściciel polityki cenowej, termin 3 tygodnie | kolejna implementacja `DeliveryPricePolicy`, wartość enuma i rejestracja w composition root; bez pluginów | nowy wariant nie spełnia kontraktu polityk (np. wycena sieciowa - wtedy osobny port) |
| 5 | **teraz** (ostatni odpowiedzialny moment wypada w tym sprincie) | jednokierunkowe | kontrakt danych z partnerem; właściciel integracji i partner | jawny, wersjonowany format, opis i test kontraktowy; ADR | partner zgłasza potrzebę zmiany albo pojawia się drugi odbiorca pliku |
| 6 | **odłożyć wybór na koniec, ale za adapterem** | dwukierunkowe | sposób generowania PDF; właściciel komunikacji z klientem | najprostsza biblioteka spełniająca wymagania, zamknięta w jednym adapterze, z testem wyniku | wymagania dostępności, podpisu lub wydajności, których biblioteka nie spełnia |

Komentarz do sytuacji 5: format jest jednokierunkowy, bo partner wdroży parser. Odkładanie decyzji „do kwartału” jest błędem - ostatni odpowiedzialny moment wypada, zanim partner zacznie implementację. Prewencja polega na wersji formatu w nagłówku i teście kontraktowym, które zmniejszają koszt przyszłej zmiany.

**Interfejs `Surcharge`** (często pojawia się przy sytuacji 1): obecnie niepotrzebny. `FuelSurcharge` jest jedną stabilną regułą, a żadna granica ani drugi wariant nie wymaga podmiany. Utworzenie interfejsu wyłącznie dla mockowania zwiększyłoby liczbę pojęć bez nowej semantyki.

### Krok 2: przykładowy ADR dla sytuacji 5

```markdown
# ADR-0012: Wersjonowany plik CSV z ofertami dla partnera logistycznego

## Status

Zaakceptowana

## Kontekst

Partner logistyczny będzie co noc pobierał oferty dostaw. Parser po jego stronie
powstanie w ciągu kwartału i nie będzie często zmieniany, więc format pliku jest
kontraktem trudnym do zmiany. Kwoty muszą zachować skalę 2 bez błędów zaokrągleń.

## Decyzja

Generujemy plik CSV (UTF-8, separator średnik) z nagłówkiem `format-version=1`.
Kwoty zapisujemy jako tekst dziesiętny ze skalą 2, masy w kilogramach ze skalą 2.
Adapter eksportu jest jedynym miejscem znającym format.

## Rozważone opcje

1. **CSV z wersją w nagłówku**: prosty dla partnera, wersja pozwala wprowadzić v2 równolegle.
2. **JSON Lines ze schematem**: bogatszy typowo, ale partner nie ma dziś parsera JSON.
3. **Bezpośredni dostęp partnera do naszej bazy**: najszybszy start, ale wiąże partnera z naszym schematem na stałe.

## Konsekwencje

- **Pozytywna**: zmiana formatu jest możliwa przez okres równoległego generowania v1 i v2.
- **Pozytywna**: nasz model domeny może się zmieniać bez wpływu na partnera.
- **Negatywna**: utrzymujemy test kontraktowy i opis formatu jako część dokumentacji żywej.

## Metoda weryfikacji

Test kontraktowy porównuje wygenerowany plik z przykładem uzgodnionym z partnerem;
partner uruchamia swój parser na tym samym przykładzie przed wdrożeniem.
```

Odpowiedź na pytanie 4: przy zmianie kierunku ten ADR dostaje status „Zastąpiona” z odwołaniem do nowego ADR; nie edytuje się go wstecz.

### Kod referencyjny

- `src/main/java/pl/training/module2/domain/FuelSurcharge.java` - sytuacja 1,
- `src/main/java/pl/training/module2/domain/DeliveryPricePolicy.java` i `DeliveryPriceCalculator.java` - sytuacja 4 (najwęższe rozszerzenie bez pluginów),
- `src/main/java/pl/training/module2/documentation/DecisionRecord.java` i `src/test/java/pl/training/module2/documentation/DecisionRecordMarkdownRendererTest.java` - wzór struktury ADR (pełny oczekiwany dokument w teście).

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| decyzja odwołuje się do wiedzy i właściciela | kolumna „Wiedza, właściciel” wypełniona przed kolumną „Decyzja” |
| przyszłe wymaganie oddzielone od zatwierdzonego | sytuacje 3 i 4 mają różne decyzje |
| moment wygaśnięcia opcji | sytuacja 5: przed rozpoczęciem implementacji parsera przez partnera |
| ADR z dwiema opcjami i kosztem | sekcje „Rozważone opcje” i „Konsekwencje” zawierają negatywną konsekwencję |

### Typowe błędy uczestników

- scalanie sytuacji 2 „bo to ta sama liczba”,
- traktowanie sytuacji 4 jako uzasadnienia pluginów albo rejestru ładowanego refleksją,
- odkładanie sytuacji 5 („ustalimy, gdy partner zacznie”) - opcja wygasa wcześniej,
- ADR dla sytuacji 6 albo dla zmiany nazwy - dokumentuje się decyzje o trwałym wpływie,
- ADR bez negatywnych konsekwencji albo z jedną opcją („nie było alternatywy”).

### Pytania do dyskusji

- Po czym poznać, że dwie reprezentacje to ta sama wiedza? Kogo o to zapytać?
- Czy reguła trzech pomogłaby w sytuacji 1? (Nie: dwie reprezentacje potwierdzonej reguły mogą wymagać natychmiastowej centralizacji.)
- Co zrobić, gdy zespół chce złamać regułę z ADR dla jednego przypadku: wyjątek czy nowy ADR?

---

## Ćwiczenie 3: Projekt jako hamulec degradacji

### Krok 1: aktorzy zmiany

| Obszar | Aktor zmiany | Właściwa odpowiedzialność |
| --- | --- | --- |
| cennik i opłata paliwowa | właściciel polityki cenowej | domena |
| zapis wyceny | właściciel danych i operacji | adapter realizujący port aplikacji |
| treść i kanał komunikatu | właściciel komunikacji z klientem | adapter powiadomień |
| kolejność utworzenia, zapisu i powiadomienia | właściciel procesu aplikacyjnego | przypadek użycia |

### Krok 2: decyzja o awarii zapisu

Kod referencyjny przyjmuje, że po nieudanym zapisie **nie** wolno wysłać powiadomienia. Tę odpowiedź podaj uczestnikom, gdy o nią zapytają. Jeśli właściciel procesu wybierze inną semantykę, test i implementację zmienia się świadomie - to nowa decyzja zachowania, a nie część refaktoryzacji (w legacy zapis nie mógł zawieść).

### Kroki 3-7: przebieg odniesiony do kodu referencyjnego

| Krok | Wynik w projekcie | Chroniona oś zmiany | Dodany koszt |
| --- | --- | --- | --- |
| 3. opłata paliwowa | `domain/FuelSurcharge.java` | zmiana stawki w jednym miejscu (DRY) | nowy typ domenowy, walidacja stawki `0..1` |
| 4. kontrakt i pierwszy wariant | `domain/DeliveryPricePolicy.java`, `domain/StandardDeliveryPricePolicy.java`; testy charakterystyki zielone | niezależne cenniki (SRP, OCP) | kontrakt do utrzymania |
| 4. drugi wariant i wybór | `domain/ExpressDeliveryPricePolicy.java`, `domain/DeliveryPriceCalculator.java` | wybór wariantu w jednym miejscu | rejestracja w kalkulatorze |
| 5. test kontraktowy | `domain/DeliveryPricePolicyContractTest.java` | substytucja polityk (LSP) | lista implementacji w teście do utrzymania |
| 6. porty i test awarii | `application/QuoteRepository.java`, `application/QuoteNotifier.java`, `CreateDeliveryQuoteTest.doesNotNotifyWhenSavingFails` | przypadek użycia zna tylko potrzebne role (ISP, DIP); uzgodniony protokół | dwa kontrakty, test do utrzymania |
| 7. adaptery i składanie | `adapter/InMemoryQuoteRepository.java`, `adapter/ConsoleQuoteNotifier.java`, `Module2Examples.java` | wymiana mechanizmu zapisu i kanału bez edycji przypadku użycia; wybór konkretów w jednym miejscu (KISS, YAGNI: bez kontenera) | klasy adapterów, w realnym systemie testy integracyjne |

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

Porównanie wyniku programu przed i po: dwie identyczne linie `Quote ready ... 17.28`, `Legacy and refactored prices equal: true`, `Stored quotes: 1`.

### Krok 8: automatyczna ochrona granicy

| Wariant | Siła gwarancji | Koszt utrzymania | Komunikat o błędzie |
| --- | --- | --- | --- |
| skan importów w CI (`grep`) | najsłabsza: nie widzi pełnych nazw w kodzie, importów z `*`, refleksji; fałszywe alarmy w komentarzach | bardzo niski | surowy wynik narzędzia, ścieżka i linia |
| test JUnit skanujący pliki źródłowe | jak wyżej, ale w `mvn verify` | niski, bez nowych zależności | własny, czytelny komunikat asercji |
| narzędzie do testów architektury (np. ArchUnit) | reguły na poziomie typów w kodzie bajtowym | średni: nowa zależność testowa | bardzo czytelny, wskazuje klasę i zależność |
| osobne moduły Maven | kompilator odrzuca import | wysoki: restrukturyzacja projektu | błąd kompilacji „package does not exist” |
| JPMS | silna granica widoczności | średni do wysokiego: `module-info.java`, eksporty | błąd kompilacji o widoczności |

Przykładowy test bez nowych zależności (szkic):

```java
@Test
void innerPackagesDoNotImportAdapters() throws IOException {
    for (String pkg : List.of("application", "domain")) {
        Path dir = Path.of("src/main/java/pl/training/module2", pkg);
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(p -> p.toString().endsWith(".java")).forEach(p ->
                assertFalse(read(p).contains("import pl.training.module2.adapter"),
                        () -> p + " imports adapter"));
        }
    }
}
```

(`read` to pomocnicze `Files.readString` z obsługą `IOException`.) Test nie wykryje użycia w pełni kwalifikowanej nazwy bez importu - to przykład słabości kontroli tekstowej. Należy dobrać siłę mechanizmu do znaczenia granicy.

Uzupełnienie dla szybszych par: adapter `BufferingQuoteNotifier` w pakiecie `adapter`, który zapisuje komunikaty w buforze (kopia przez `List.copyOf`) i jest podmieniany **wyłącznie** w composition root. `git diff --stat` nie pokazuje wtedy zmian w `application/` ani `domain/`. Uwaga: po podmianie druga linia `Quote ready ...` znika ze standardowego wyjścia, a `Module2ExamplesTest` tego nie wykryje (tylko `assertDoesNotThrow`) - to świadoma zmiana zachowania programu demonstracyjnego.

### Kod referencyjny

- stan „przed”: `src/main/java/pl/training/module2/legacy/LegacyDeliveryQuoteService.java`,
- stan „po”: `src/main/java/pl/training/module2/domain/`, `src/main/java/pl/training/module2/application/`, `src/main/java/pl/training/module2/adapter/`, `src/main/java/pl/training/module2/Module2Examples.java`,
- testy: `src/test/java/pl/training/module2/domain/DeliveryPriceCalculatorTest.java`, `src/test/java/pl/training/module2/domain/DeliveryPricePolicyContractTest.java`, `src/test/java/pl/training/module2/application/CreateDeliveryQuoteTest.java`, `src/test/java/pl/training/module2/adapter/InMemoryQuoteRepositoryTest.java`.

### Weryfikacja kryteriów akceptacji

| Kryterium | Dowód w kodzie referencyjnym |
| --- | --- |
| `17.28` / `31.32` dla 3 kg | `DeliveryPriceCalculatorTest.delegatesToPolicySelectedByShippingMethod` |
| przypadek użycia nie importuje adapterów | `grep -r "import pl.training.module2.adapter" src/main/java/pl/training/module2/application` - brak wyników |
| jedna reprezentacja `0.08` | stawka tworzona raz w `Module2Examples` i wstrzykiwana do obu polityk |
| wspólny test kontraktowy | `DeliveryPricePolicyContractTest.everyPolicyObeysTheSubstitutionContract` |
| brak powiadomienia po awarii zapisu | `CreateDeliveryQuoteTest.doesNotNotifyWhenSavingFails` |
| `mvn verify` zielony | wynik builda |

### Typowe błędy uczestników

- wydzielanie wszystkiego naraz, bez uruchamiania testów po pierwszym wariancie,
- scalanie cenników STANDARD i EXPRESS w jedną sparametryzowaną metodę `basePrice(base, rate, weight)` (fałszywa abstrakcja),
- stała `0.08` w każdej polityce (dwie reprezentacje wiedzy nadal istnieją),
- port nazwany technologią (`JdbcQuoteRepository` jako interfejs) albo ogólny `CrudRepository` w aplikacji,
- interfejs dla `DeliveryPriceCalculator` „bo DIP” (w referencji świadomie go nie ma),
- utrata formatu komunikatu przy przenoszeniu do adaptera (test charakterystyki tego nie wykryje; wykryje porównanie wyjścia programu),
- pominięcie decyzji z kroku 2 i traktowanie kolejności `save`/`quoteCreated` jako oczywistej,
- mylenie przepływu sterowania z zależnością źródłową („przypadek użycia woła adapter, więc łamie regułę”).

### Pytania do dyskusji

- Która zmiana była refaktoryzacją, a która nową decyzją zachowania (awaria zapisu)?
- Czy przy jednej metodzie dostawy Strategy byłaby uzasadniona? (Nie - to byłaby spekulacja; por. YAGNI i ostatni odpowiedzialny moment.)
- Jak ten podział zmienia koszt piątej kolejnej zmiany cennika w porównaniu z wersją zastaną?
- Jaka siła kontroli granicy jest adekwatna dla tego projektu, a jaka dla systemu z wieloma zespołami?

---

## Ćwiczenie 4: Boy Scout i seria zmian gotowych do przeglądu

> Rozwiązanie opracowane na podstawie kodu referencyjnego. Nowa reguła raportu (`Failed: n/m`) jest wymaganiem wymyślonym na potrzeby ćwiczenia i nie ma odpowiednika w kodzie referencyjnym; kod referencyjny obejmuje kroki 1-3.

### Krok 1: zestaw 1 - testy charakteryzujące

Commit: `Characterize ReleaseSummaryFormatter output and validation order`. Zawartość: wyłącznie testy, bez zmian w kodzie produkcyjnym.

- wynik dla sukcesu, porażki i przypadku mieszanego, w tym `releaseId` ze spacjami (`" release-3 "` -> `Release release-3`),
- niezależny orakl tekstu, np.:

```text
Release release-42
[OK] test: deployed
[ERROR] production: timeout
Successful: 1/2
```

- brak końcowego znaku nowej linii po ostatnim wierszu,
- walidacja i jej kolejność (typ i komunikat):

| Wejście | Wyjątek |
| --- | --- |
| `releaseId = null` | `NullPointerException("releaseId")` |
| `results = null` | `NullPointerException("results")` |
| `releaseId = "   "` | `IllegalArgumentException("releaseId must not be blank")` |
| `results = List.of()` | `IllegalArgumentException("results must not be empty")` |
| lista z elementem `null` | `NullPointerException("result")` |
| `(" ", null)` | `NullPointerException("results")` - kontrole null przed kontrolą pustego napisu |

Decyzje o testach (do zapisania w opisie):

- zachowanie zamierzone: format wierszy, licznik sukcesów, `strip` identyfikatora, komunikaty walidacji (klienci mogą na nich polegać),
- zachowanie potencjalnie przypadkowe: dokładna kolejność kontroli dla kombinacji błędów. Utrwalamy ją, bo refaktoryzacja ma być czysta, ale zaznaczamy w opisie, że test `(" ", null)` chroni kolejność, a nie wymaganie biznesowe.

### Krok 2: zestaw 2 - Boy Scout bez zmiany zachowania

Seria małych commitów w jednym zestawie (lub kilka zestawów):

1. `Rename local variables in ReleaseSummaryFormatter` (`s` -> `summary`, `n` -> `successfulDeployments`, `r` -> `result`) - zmiana IDE,
2. `Extract validation into validate()` - bez zmiany kolejności kontroli,
3. `Extract formatResult() to remove duplicated line format`,
4. `Build summary with StringBuilder`.

Wynik odpowiada `boyscout/after/ReleaseSummaryFormatter.java`. Po każdym commicie: kompilacja, testy z zestawu 1, przegląd diffu. Opcjonalny zestaw mechaniczny (formatowanie pliku) jako osobny commit bez ręcznych zmian, najlepiej przed zestawem 2.

### Krok 3: zestaw 3 - zmiana zachowania

Commit: `Show failed deployment count in release summary`.

- zmiana ostatniego wiersza na `Successful: 1/2, Failed: 1/2`,
- aktualizacja orakla w teście z zestawu 1 (świadoma zmiana oczekiwania, widoczna w diffie jako jedyna zmiana testu),
- nowy test dla przypadków brzegowych (same sukcesy: `Failed: 0/n`, same porażki),
- testy walidacji bez zmian (dowód, że kontrakt wejścia nie został naruszony).

### Krok 4: przykładowy opis zestawu 2

```markdown
## Dlaczego
Klasa ReleaseSummaryFormatter jest trudna do zmiany: nazwy s/n/r, powielony
format wiersza, konkatenacja w pętli. Przygotowuje grunt pod zmianę formatu
podsumowania (osobny PR).

## Co zmienia / czego nie zmienia
Zmienia: nazwy lokalne, wydzielone validate() i formatResult(), StringBuilder.
Nie zmienia: sygnatury format(...), tekstu wyniku, walidacji, kolejności kontroli.

## Zachowywany kontrakt
Tekst raportu i typy/komunikaty wyjątków, w tym kolejność kontroli
(test dla wejścia (" ", null)).

## Dowody
- mvn test -Dtest='pl.training.module2.exercise4.**': wszystkie testy zielone
  (testy charakteryzujące z PR #1 bez zmian)
- diff testów: brak zmian

## Wdrożenie i wycofanie
Brak zmiany zachowania; zwykłe wdrożenie. Wycofanie: revert PR.

## Kolejność czytania
1. testy (niezmienione) 2. validate() 3. formatResult() 4. format()
```

Kolejność czytania dla recenzenta całej serii: zestaw 1 (testy, czyli kontrakt), zestaw 2 (struktura, testy bez zmian), zestaw 3 (zmiana orakla i nowa reguła).

Przykładowe komentarze z przeglądu (do omówienia, jeśli zostanie czas):

- `BLOCKER`: „W `validate()` sprawdzenie `releaseId.isBlank()` jest przed `requireNonNull(results)`. Dla wejścia `(" ", null)` klient dostanie teraz `IllegalArgumentException` zamiast `NullPointerException`. To zmiana kontraktu w PR oznaczonym jako refaktoryzacja. Proszę przywrócić kolejność albo przenieść zmianę do osobnego PR z uzasadnieniem.”
- `SUGGESTION`: „`formatResult` mogłoby przyjmować etykietę jako parametr, ale przy dwóch statusach obecna wersja jest czytelniejsza. Zostawiam do decyzji autora.”
- `NIT`: „Nazwa testu `test1` nie mówi, co chroni. Może `preservesValidationOrderForBlankIdAndNullResults`?”

### Kod referencyjny

- wejście: `src/main/java/pl/training/module2/boyscout/before/ReleaseSummaryFormatter.java`,
- wzór refaktoryzacji bez zmiany zachowania: `src/main/java/pl/training/module2/boyscout/after/ReleaseSummaryFormatter.java`,
- wzór testów równoważności i orakla: `src/test/java/pl/training/module2/boyscout/BoyScoutEquivalenceTest.java`.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| jedna główna intencja na zestaw | każdy zestaw da się nazwać jednym słowem: testy charakteryzujące, refaktoryzacja, zmiana zachowania |
| system działa po każdym kroku | `git checkout` dowolnego commita kompiluje się i przechodzi testy |
| testy nie utrwalają bezkrytycznie przypadkowego zachowania | decyzja zamierzone kontra przypadkowe w opisie zestawu 1 |
| zmiany mechaniczne i ręczne rozdzielone | formatowanie i rename z IDE w osobnych commitach |
| opis pozwala odtworzyć weryfikację | konkretne polecenia, a nie „testy przeszły” |

### Typowe błędy uczestników

- zestaw 1 zawiera już pierwsze zmiany w kodzie produkcyjnym („tylko nazwy”),
- refaktoryzacja „przy okazji” dodaje walidację (np. odrzucanie pustego `environment` w formaterze, choć robi to już `DeploymentResult`) albo zmienia kolejność kontroli,
- zmiana orakla testu i zmiana formatu w zestawie oznaczonym jako refaktoryzacja,
- testy porównujące tylko „nie rzuca wyjątku” albo tylko `contains("Successful")`, bez pełnego tekstu,
- test różnicowy starej i nowej wersji bez niezależnego orakla,
- przeformatowanie całego pliku razem z logiką.

### Pytania do dyskusji

1. Kiedy mała zmiana nazwy może zostać w zestawie ze zmianą zachowania? (Wyjątek dla bardzo małego lokalnego uporządkowania, nazwanego w opisie.)
2. Czy kolejność kontroli walidacji dla kombinacji błędów jest kontraktem? Kto o tym decyduje?
3. Pięćset linii automatycznego rename kontra trzydzieści linii zmieniających schemat, regułę i uprawnienia: co jest „mniejszą” zmianą?

---

## Sprawdzenie wiedzy - odpowiedzi

1. **Dlaczego problem złapany w pętli kompilatora jest tańszy niż w przeglądzie kodu?** Sygnał przychodzi po sekundach, gdy autor ma pełny kontekst, a między przyczyną i sygnałem jest jedna mała zmiana. W przeglądzie sygnał przychodzi po godzinach lub dniach, z wieloma zmianami naraz, często do innej osoby.
2. **Dlaczego nagłe włączenie `-Werror` w dużym legacy jest złym pomysłem?** Historyczne ostrzeżenia zablokują wszystkich, niezależnie od bieżącej pracy. Zamiast tego: pomiar, stan bazowy, bramka „brak nowych naruszeń” i stopniowa redukcja.
3. **Czym jest ostatni odpowiedzialny moment?** To chwila, po której brak decyzji eliminuje ważną opcję. Odkładanie w nieskończoność ignoruje moment wygaśnięcia opcji i pozwala, żeby decyzja zapadła sama przez zaniechanie.
4. **Jak testy i granice zamieniają decyzje jednokierunkowe w dwukierunkowe?** Testy pozwalają szybko wykryć skutki cofnięcia, a granica (interfejs, adapter, wersjonowany format) ogranicza promień zmiany do jednego miejsca. Dzięki temu cofnięcie decyzji jest tanie.
5. **Czy YAGNI zabrania pisania testów dla aktualnego zachowania?** Nie. Testy aktualnego zachowania wspierają bezpieczną zmianę i właśnie dzięki nim można odkładać spekulacyjne decyzje o abstrakcji.
6. **Kiedy decyzja zasługuje na ADR?** Gdy ma trwały wpływ na architekturę i jest trudna do cofnięcia (granica modułu, format kontraktu, własność danych). Przy zmianie kierunku stary ADR oznacza się jako zastąpiony i tworzy nowy.
7. **Czym różni się akceptacja problemu od jego ignorowania?** Akceptacja jest jawną decyzją z właścicielem, uzasadnieniem i sygnałem ponownej oceny, zapisaną w rejestrze. Ignorowanie nie ma żadnego z tych elementów.
8. **Na czym polega kwarantanna problemu za granicą?** Obcy model, jednostki i błędy są tłumaczone w jednym miejscu (adapterze), a reszta kodu widzi tylko własny port i własne typy. Kierunkiem refaktoryzacji jest Anti-Corruption Layer (moduł 5).
9. **Co mówią prawa Lehmana?** Że używany system musi się zmieniać, a jego złożoność i postrzegana jakość pogarszają się, jeśli nie wykonuje się jawnej pracy nad ich utrzymaniem. Nie mówią, że degradacja jest nieunikniona w każdym tempie ani że jest winą konkretnych osób; to obserwacje empiryczne, nie prawa fizyki.
10. **Jak teoria rozbitych okien przekłada się na zasady pracy?** Widoczne zaniedbania (ignorowane ostrzeżenia, czerwone testy, zakomentowany kod) obniżają standard kolejnych zmian. Stąd zero tolerancji dla nowych naruszeń, jawne oznaczanie starych i szybkie naprawianie czerwonych testów.
11. **Co jest przedmiotem DRY?** Wiedza i decyzje systemu. Podobny tekst może reprezentować różną wiedzę.
12. **Dlaczego `@Autowired` nie dowodzi DIP?** Wstrzyknięcie zależności opisuje sposób dostarczenia obiektu. DIP dotyczy kierunku zależności źródłowej i własności abstrakcji; wstrzyknięcie konkretnej klasy nadal wiąże politykę ze szczegółem.
13. **Czy przepływ sterowania z przypadku użycia do adaptera łamie regułę zależności?** Nie. Adapter jest wywoływany w czasie wykonania, ale źródłowo zależy od portu zdefiniowanego wewnątrz.
14. **Czym różni się Boy Scout Rule od „sprzątania przy okazji” zmieniającego zachowanie?** Boy Scout to mała, lokalna, weryfikowalna poprawa struktury dotykanego kodu bez zmiany zachowania. Zmiana reguły, walidacji lub wyniku to decyzja biznesowa i osobny zestaw zmian.
15. **Dlaczego refaktoryzacji nie łączy się ze zmianą zachowania w jednym zestawie zmian?** Bo wymagają innych dowodów: refaktoryzację dowodzą niezmienione testy, zmianę zachowania - nowy test i świadomie zmieniony orakl. Po połączeniu recenzent nie odróżni ruchu mechanicznego od nowej reguły, a wycofanie jednego oznacza wycofanie obu.
