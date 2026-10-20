# Moduł 2. Prewencja - przewodnik prowadzącego po przykładach

Materiał dla prowadzącego. Opisuje przykłady kodu z pakietu `pl.training.module2`, ich związek ze slajdami modułu (`src/main/resources/md/slides/02-prewencja-slajdy.md`) oraz sugerowany przebieg demonstracji.

Pakiet łączy trzy grupy przykładów, ułożone w przewodniku według tematów modułu:

- **szybka informacja zwrotna i decyzje** (sekcje 1-2 slajdów): bramka kompilatora w pamięci (`tooling`) i wykonywalny model ADR (`documentation`),
- **projekt jako kontrola degradacji** (sekcja 5): studium wyceny dostawy - zastany serwis `LegacyDeliveryQuoteService` (`legacy`) rozłożony na domenę (`domain`), przypadek użycia z portami (`application`), adaptery (`adapter`) i ręczny composition root (`Module2Examples`),
- **prewencja w codziennej pracy** (sekcja 6): lokalna poprawa Boy Scout z testem równoważności (`boyscout`).

Sekcje 3 (akceptacja) i 4 (degradacja) slajdów są koncepcyjne i nie mają osobnego kodu; ilustracją kwarantanny jest pakiet `adapter`, a ilustracją rejestru długu - stan bazowy bramki z Ćwiczenia 1.

## Uruchamianie przykładów i testów

Wszystkie polecenia uruchamiamy z katalogu głównego repozytorium.

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module2.Module2Examples
mvn test -Dtest='pl.training.module2.**'
```

Albo z IDE: uruchom `Module2Examples.main` oraz testy z katalogu `src/test/java/pl/training/module2`. Przed ćwiczeniami warto wykonać `mvn clean verify`.

Oczekiwany wynik `Module2Examples`:

```text
Quote ready for developer@example.com: STANDARD costs 17.28
Quote ready for developer@example.com: STANDARD costs 17.28
Legacy and refactored prices equal: true
Stored quotes: 1
Boy Scout: Successful: 1/2
Decision record: # ADR-0042: Use Branch by Abstraction
Compiler gate: compiled=true
```

Pierwsza linia pochodzi z wersji zastanej, druga z adaptera `ConsoleQuoteNotifier` nowej wersji. Przykład `tooling` wymaga uruchomienia na JDK (nie JRE), bo korzysta z `ToolProvider.getSystemJavaCompiler()`; opcja `--release 25` wymaga JDK w wersji co najmniej 25.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module2
dotnet test tests/Training.Module2.Tests
```

Kod: `csharp/src/Training.Module2` (katalogi `Legacy`, `Domain`, `Application`, `Adapter`, `BoyScout` z `Before` i `After`, `Documentation`, `Tooling`, plik `Module2Examples.cs`), testy: `csharp/tests/Training.Module2.Tests`. Odpowiednikiem kompilatora w pamięci jest `Tooling/InMemoryCSharpCompiler.cs` (Roslyn).

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module2
npx vitest run test/module2
```

Kod: `typescript/src/module2` (`legacy`, `domain`, `application`, `adapter`, `boyscout/before`, `boyscout/after`, `documentation`, `tooling`, `Module2Examples.ts`, `main.ts`), testy: `typescript/test/module2`. Odpowiednikiem kompilatora jest `tooling/InMemoryTypeScriptCompiler.ts`.

---

## Mapa przykładów

| Nr | Przykład (pakiet) | Sekcja teorii | Kluczowe pliki (`src/main/java/pl/training/module2/...`) | Testy (`src/test/java/pl/training/module2/...`) | Zadania |
| --- | --- | --- | --- | --- | --- |
| 1 | Bramka kompilatora (`tooling`) | 1.4 Różne narzędzia, 1.5 Kompilator jako najszybsza bramka, 1.6 Bramki w legacy | `tooling/InMemoryJavaCompiler.java`, `WarningPolicy.java`, `CompilationResult.java`, `CompilationDiagnostic.java` | `tooling/InMemoryJavaCompilerTest.java` | Ćwiczenie 1 |
| 2 | Wykonywalny model ADR (`documentation`) | 2.7 Zapis decyzji: ADR | `documentation/DecisionRecord.java`, `DecisionRecordMarkdownRenderer.java`, `DecisionId.java`, `DecisionStatus.java`, `DecisionOption.java`, `DecisionConsequence.java`, `ConsequenceKind.java` | `documentation/DecisionRecordTest.java`, `DecisionRecordMarkdownRendererTest.java` | Ćwiczenie 2 |
| 3 | Wersja zastana i test charakterystyki (`legacy`) | 5.7 Punkt wyjścia, 5.8 Co naprawdę jest problemem | `legacy/LegacyDeliveryQuoteService.java` | `legacy/LegacyDeliveryQuoteServiceCharacterizationTest.java` | Ćwiczenie 3 |
| 4 | Model domeny i DRY dla opłaty (`domain`) | 5.2 DRY dotyczy wiedzy | `domain/Parcel.java`, `ShippingMethod.java`, `DeliveryQuote.java`, `FuelSurcharge.java` | pośrednio `domain/DeliveryPriceCalculatorTest.java`, `domain/DeliveryPricePolicyContractTest.java` | Ćwiczenia 2 i 3 |
| 5 | Strategy dla wariantów ceny i test kontraktowy (`domain`) | 5.3 SOLID, 5.4 Błędne uproszczenia, 5.10 Bilans | `domain/DeliveryPricePolicy.java`, `StandardDeliveryPricePolicy.java`, `ExpressDeliveryPricePolicy.java`, `DeliveryPriceCalculator.java` | `domain/DeliveryPricePolicyContractTest.java`, `domain/DeliveryPriceCalculatorTest.java` | Ćwiczenie 3 |
| 6 | Porty i przypadek użycia (`application`) | 5.6 Reguła zależności, 5.9 Porty i przypadek użycia | `application/QuoteRepository.java`, `QuoteNotifier.java`, `CreateDeliveryQuote.java` | `application/CreateDeliveryQuoteTest.java` | Ćwiczenie 3 |
| 7 | Adaptery i composition root (`adapter`) | 3.3 Kwarantanna, 5.6, 5.10 | `adapter/InMemoryQuoteRepository.java`, `ConsoleQuoteNotifier.java`, `Module2Examples.java` | `adapter/InMemoryQuoteRepositoryTest.java` | Ćwiczenie 3 |
| 8 | Lokalna poprawa Boy Scout (`boyscout`) | 6.1-6.3 Boy Scout Rule, 6.4 Małe zestawy zmian | `boyscout/before/ReleaseSummaryFormatter.java`, `boyscout/after/ReleaseSummaryFormatter.java`, `DeploymentResult.java`, `DeploymentStatus.java` | `boyscout/BoyScoutEquivalenceTest.java` | Ćwiczenie 4 |
| 9 | Przegląd całego modułu | wszystkie sekcje | `Module2Examples.java` | `Module2ExamplesTest.java` | - |

---

## Przykład 1. Bramka kompilatora w pamięci (`pl.training.module2.tooling`)

**Sekcja teorii:** 1.4 Różne narzędzia, różne dowody; 1.5 Kompilator jako najszybsza bramka; 1.6 Bramki w legacy: stan bazowy i ratchet.

**Co ilustruje:** politykę ostrzeżeń jako jawny parametr (`WarningPolicy`): ten sam kod z surowym typem przechodzi przy `ALLOW_WARNINGS` (z ostrzeżeniem) i nie przechodzi przy `TREAT_WARNINGS_AS_ERRORS` (odpowiednik `-Werror`). Diagnostyka jest zapisywana strukturalnie (rodzaj, kod, pozycja), a nie jako lokalizowany komunikat. To najszybsza pętla informacji zwrotnej po IDE.

**Klasy:**

- `tooling/WarningPolicy.java` - `ALLOW_WARNINGS`, `TREAT_WARNINGS_AS_ERRORS`.
- `tooling/InMemoryJavaCompiler.java` - `javax.tools`, opcje `--release 25`, `-proc:none`, `-Xlint:rawtypes`, plus `-Werror` zależnie od polityki; źródło i kod bajtowy w pamięci (`MemoryFileManager`).
- `tooling/CompilationResult.java` - `successful`, lista diagnostyk, posortowane nazwy wygenerowanych klas, `hasDiagnostic(kind, code)`.
- `tooling/CompilationDiagnostic.java` - `Optional<String> code` (API dopuszcza brak kodu), pozycje `NOPOS` lub dodatnie.

```java
private static final List<String> BASE_OPTIONS = List.of(
        "--release", "25",
        "-proc:none",
        "-Xlint:rawtypes");
...
if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
    options.add("-Werror");
}
```

**Na co zwrócić uwagę:**

- `--release 25` ustala poziom języka i API, ale nie gwarantuje, że Maven działa na JDK 25. W CI kontroluje się faktyczny JDK (toolchains lub jawne środowisko). Warto pokazać `mvn -v` na maszynie prowadzącego.
- `-Werror` to silna bramka dla nowego kodu, ale włączona nagle w dużym legacy zablokuje wszystkich. Stąd stan bazowy i ratchet (slajd 1.6).
- Testy sprawdzają konkretne kody `javac` (`compiler.warn.raw.class.use`, `compiler.err.warnings.and.werror`), więc są świadomie związane z wersją kompilatora.
- Ten przykład nie zastępuje Mavena z pełną ścieżką klas, testów ani analizatorów (Checkstyle, PMD, SpotBugs pokrywają inne klasy problemów).
- Tabela 1.4: każde narzędzie daje inny dowód i ma inne granice. Zielony wynik jednego narzędzia to nie certyfikat.

**Testy:** `InMemoryJavaCompilerTest`:

- `compilesTypedSourceAndKeepsGeneratedBytecodeInMemory` - czysty kod przechodzi nawet z `-Werror`, brak diagnostyk,
- `reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource` - surowy typ: sukces i ostrzeżenie `compiler.warn.raw.class.use`,
- `rejectsTheSameRawTypeWhenWarningsAreErrors` - ten sam kod: porażka, ostrzeżenie oraz błąd `compiler.err.warnings.and.werror`,
- `validatesCompilationRequest` - walidacja nazwy binarnej, źródła, polityki,
- `diagnosticsCanRepresentAnAbsentImplementationSpecificCode` - brak kodu diagnostyki i walidacja pozycji.

**Demonstracja na żywo (10 min):**

1. `java -version`, `mvn -v`: jaki JDK faktycznie buduje projekt? Porównaj z `maven.compiler.release` w `pom.xml`.
2. Pokaż `InMemoryJavaCompilerTest` i uruchom trzy pierwsze testy; pokaż różnicę w `successful` dla tego samego `RAW_SOURCE`.
3. Na żywo: skompiluj wszystkie źródła z `-Xlint:all` bez zmiany `pom.xml`, np. `javac --release 25 -Xlint:all -d /tmp/lint $(find src/main/java -name '*.java')`, i pokaż, że nawet projekt szkoleniowy ma historyczne ostrzeżenia (kategoria `auxiliaryclass` w module 1) oraz celowe ostrzeżenia w materiale warsztatu (`m2/s03_compilergate/start`, `m2/s04_qualitygate/sample/dirty`). Most do Ćwiczenia 1: „co byście zrobili, gdyby jutro włączyć `-Werror`?”.
4. Zapytaj o czas tej pętli: ile sekund od zapisu pliku do sygnału? Porównaj z czasem przeglądu kodu (slajd 1.1).

---

## Przykład 2. Wykonywalny model ADR (`pl.training.module2.documentation`)

**Sekcja teorii:** 2.7 Zapis decyzji: ADR; pośrednio 2.2 Decyzje odwracalne i nieodwracalne.

**Co ilustruje:** model decyzji architektonicznej w lokalnym szablonie projektu (rdzeń ADR: tytuł, status, kontekst, decyzja, konsekwencje, rozszerzony o identyfikator, rozważone opcje i metodę weryfikacji) oraz deterministyczny renderer do Markdown.

**Klasy:**

- `documentation/DecisionId.java` - lokalna konwencja `ADR-[0-9]{4}` (nie wymóg formatu ADR).
- `documentation/DecisionStatus.java` - `PROPOSED`, `ACCEPTED`, `REJECTED`, `DEPRECATED`, `SUPERSEDED`.
- `documentation/DecisionOption.java`, `DecisionConsequence.java`, `ConsequenceKind.java`.
- `documentation/DecisionRecord.java` - wymaga co najmniej jednej opcji i jednej konsekwencji, niepustych tekstów; kopie defensywne list.
- `documentation/DecisionRecordMarkdownRenderer.java` - stała kolejność sekcji, polskie etykiety statusów (`Zaakceptowana`, `Zastąpiona` itd.) i rodzajów konsekwencji.

**Na co zwrócić uwagę:**

- ADR jest dla decyzji jednokierunkowych, o trwałym wpływie na architekturę (slajd 2.2), nie dla każdego Extract Method.
- Model wymaga przynajmniej jednej konsekwencji dowolnego rodzaju; w praktyce warto uczciwie opisać korzyści i koszty.
- Renderer gwarantuje formę, nie jakość decyzji. Wartość pochodzi z kontekstu, uczciwych alternatyw i weryfikacji.
- Przy zmianie kierunku stary ADR oznacza się jako zastąpiony (`SUPERSEDED`) i tworzy nowy; historia rozumowania zostaje.
- ADR to dokumentacja historyczna. Komentarz w kodzie opisuje „dlaczego” i stan obecny, nie historię edycji („przeniesiono tę metodę” należy do VCS).
- Wykonywalną regułę z ADR (test architektury pilnujący decyzji) pokazuje scena warsztatu `m2/s02_adr`.

**Testy:**

- `DecisionRecordTest.snapshotsCollectionsAndExposesImmutableViews` - migawki list i niemodyfikowalne widoki,
- `DecisionRecordTest.rejectsInvalidIdentifiersAndIncompleteRecords` - odrzucenie złego identyfikatora (`"42"`), pustego tytułu, pustej listy opcji, pustej listy konsekwencji, pustego uzasadnienia i opisu,
- `DecisionRecordMarkdownRendererTest.rendersEveryFieldInDeterministicOrder` - pełny oczekiwany dokument jako text block (kolejność sekcji, etykiety, końcowe nowe linie), determinizm,
- `DecisionRecordMarkdownRendererTest.rejectsANullRecord`.

**Demonstracja na żywo (8 min):**

1. Zapytaj, gdzie uczestnicy zapisują dziś decyzje i jak po roku odtwarzają, dlaczego coś zrobiono.
2. Pokaż `DecisionRecord` i test renderera; uruchom `Module2Examples` (linia `Decision record: # ADR-0042: Use Branch by Abstraction`).
3. Pokaż w teście kompletny wynik Markdown jako wzór struktury.
4. Eksperyment: spróbuj utworzyć `DecisionRecord` z pustą listą konsekwencji i pokaż wyjątek `consequences must not be empty`.
5. Przejdź do Aktywności 2.2 i zapytaj, które z sześciu decyzji zasługują na ADR.

---

## Przykład 3. Wersja zastana i test charakterystyki (`pl.training.module2.legacy`)

**Sekcja teorii:** 5.7 Studium przypadku: punkt wyjścia; 5.8 Co naprawdę jest problemem.

**Co ilustruje:** klasę, w której jeden przebieg łączy trzy niezależne decyzje: cennik (dział finansowy), zapis (właściciel danych) i komunikat (komunikacja z klientem). Sam `switch` nie jest głównym problemem. Każda kolejna zmiana dokłada się do tej samej metody - to mechanizm degradacji z sekcji 4 w małej skali.

**Klasy:** `src/main/java/pl/training/module2/legacy/LegacyDeliveryQuoteService.java`.

**Na co zwrócić uwagę:**

- stała `0.08` występuje w obu gałęziach `switch` (ta sama reguła, dwie reprezentacje),
- zapis to prywatna `List` serwisu, powiadomienie to bezpośredni `System.out.printf`,
- kolejność: obliczenie, zapis, powiadomienie, zwrot jest ukryta w implementacji; to protokół efektów ubocznych, który wymaga jawnej decyzji,
- brak punktu podmiany: nie da się wiarygodnie zasymulować awarii zapisu.

**Testy:** `LegacyDeliveryQuoteServiceCharacterizationTest`:

- `documentsStandardDeliveryPriceAndStorage` - 3 kg STANDARD daje `17.28`, a oferta trafia do `storedQuotes()`,
- `documentsExpressDeliveryPrice` - 3 kg EXPRESS daje `31.32`.
- Jawna luka: test nie przechwytuje treści konsoli. Nie jest to dowód, że komunikat nie jest częścią kontraktu. Technika testów charakteryzujących jest rozwijana w module 3.

**Demonstracja na żywo (8 min):**

1. Otwórz `LegacyDeliveryQuoteService` i poproś grupę o wypisanie „kto może zażądać zmiany w tej klasie”.
2. Pokaż tabelę ze slajdu 5.8 dopiero po odpowiedziach grupy.
3. Otwórz test charakterystyki, uruchom go. Przelicz na tablicy: (10 + 3 x 2) x 1.08 = 17.28, (20 + 3 x 3) x 1.08 = 31.32.
4. Pytania: „Czego ten test nie chroni?”, „Czy treść komunikatu jest kontraktem? Kto to rozstrzyga?”, „Jak wyglądałaby ta metoda po pięciu kolejnych metodach dostawy, gdyby nikt jej nie porządkował?” (prawa Lehmana, slajd 4.1).

---

## Przykład 4. Model domeny i DRY dla opłaty paliwowej (`pl.training.module2.domain`)

**Sekcja teorii:** 5.2 DRY dotyczy wiedzy, KISS - złożoności wprowadzonej.

**Co ilustruje:** rekordy utrzymujące podstawowe inwarianty w konstruktorze kanonicznym oraz centralizację jednej potwierdzonej reguły (stawka + sposób doliczenia + zaokrąglenie `HALF_UP`) przy świadomym **niewydzielaniu** wspólnej formuły ceny bazowej.

**Klasy:**

- `domain/Parcel.java` (masa niepusta i dodatnia), `domain/ShippingMethod.java` (enum `STANDARD`, `EXPRESS`), `domain/DeliveryQuote.java`:

  ```java
  if (price.signum() < 0) {
      throw new IllegalArgumentException("Price must not be negative");
  }
  price = price.setScale(2, RoundingMode.UNNECESSARY);
  ```

- `domain/FuelSurcharge.java`:

  ```java
  if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
      throw new IllegalArgumentException(
              "Fuel surcharge rate must be between zero and one");
  }
  ...
  return baseAmount.add(baseAmount.multiply(rate)).setScale(2, RoundingMode.HALF_UP);
  ```

**Na co zwrócić uwagę:**

- `RoundingMode.UNNECESSARY` nie zaokrągla, tylko rzuca `ArithmeticException`, gdy cena ma więcej niż dwa znaczące miejsca dziesiętne. Zaokrąglenie jest odpowiedzialnością polityki ceny, a nie wyniku.
- `BigDecimal` tworzony z tekstu, nie z `double`; `record` ogranicza kod techniczny, ale nie tworzy modelu - znaczenie wynika z nazw i reguł.
- brak metody `basePrice(base, rate, weight)`: formuły STANDARD i EXPRESS są podobne tekstowo, ale to odrębne cenniki, które mogą zmieniać się niezależnie,
- inwariant `0..1` jest decyzją modelową, nie wynika z typu; jeśli biznes dopuści więcej niż 100 procent lub korektę ujemną, trzeba go zmienić,
- brak interfejsu `Surcharge` (YAGNI, jedna reguła, brak granicy) - decyzja odłożona do chwili, gdy pojawi się drugi wariant,
- pułapka: w testach nie należy liczyć oczekiwanej ceny przez `FuelSurcharge` (niezależność wyroczni). Testy w projekcie mają wartości wpisane jawnie (`17.28`, `31.32`).
- Wersja zastana także korzysta z `Parcel` i `DeliveryQuote`, więc inwarianty obowiązują obie wersje.

**Testy:** brak dedykowanych testów rekordów i `FuelSurcharge`; zachowanie chronią pośrednio `DeliveryPriceCalculatorTest` (konkretne ceny) i `DeliveryPricePolicyContractTest` (skala 2, nieujemność). Walidacja zakresu stawki i ujemnej kwoty bazowej nie ma testu - warto to powiedzieć wprost jako lukę.

**Demonstracja na żywo (6 min):** pokaż obok siebie dwie gałęzie `switch` z Przykładu 3 i `FuelSurcharge`. Pytanie: „Dlaczego scalono `0.08`, a nie scalono `10.00 + weight * 2.00` z `20.00 + weight * 3.00`?”. Następnie: „Co się stanie dla `new BigDecimal("17.285")` w `DeliveryQuote`?” (wyjątek, nie zaokrąglenie).

---

## Przykład 5. Strategy dla wariantów ceny i test kontraktowy (`pl.training.module2.domain`)

**Sekcja teorii:** 5.3 SOLID jako narzędzie analizy zmian; 5.4 Błędne uproszczenia SOLID; 5.10 Bilans decyzji.

**Co ilustruje:** dwa istniejące algorytmy za wspólnym kontraktem; każda polityka ma własne stałe `BASE_PRICE`, `PRICE_PER_KG` i współdzieli `FuelSurcharge` przez konstruktor. Kalkulator indeksuje strategie raz w konstruktorze (`EnumMap`), odrzuca konfigurację pustą, `null` i zduplikowaną, a następnie tworzy niemodyfikowalną kopię (`Map.copyOf`). Jeden parametryzowany test sprawdza kontrakt dla wszystkich implementacji.

**Klasy:** `domain/DeliveryPricePolicy.java`, `domain/StandardDeliveryPricePolicy.java`, `domain/ExpressDeliveryPricePolicy.java`, `domain/DeliveryPriceCalculator.java`.

```java
DeliveryPricePolicy previous = indexedPolicies.putIfAbsent(method, policy);
if (previous != null) {
    throw new IllegalArgumentException("Duplicate policy for method: " + method);
}
...
this.policies = Map.copyOf(indexedPolicies);
```

**Na co zwrócić uwagę:**

- kontrakt jest zapisany w Javadoc (stabilna niepusta metoda; wynik deterministyczny, nieujemny, skala 2, bez efektów ubocznych); typy tego nie wyrażają,
- brak pluginów, refleksji, konfiguracji wyrażeń (YAGNI) - Strategy jest uzasadniona **istniejącymi** wariantami, a nie samym faktem istnienia `switch` („nie każdy `switch` łamie OCP”, slajd 5.4),
- `ShippingMethod` pozostaje enumem: dodanie metody wymaga zmiany enuma i composition root. Zamknięta na zmianę jest logika wyboru w kalkulatorze, nie cały system,
- brak polityki dla metody jest wykrywany dopiero przy żądaniu (`priceFor`), nie w konstruktorze; wybór zależy od tego, czy częściowa konfiguracja jest legalna,
- `CreateDeliveryQuote` zależy od konkretnej klasy kalkulatora, bez interfejsu: DIP nie wymaga interfejsu przed każdą klasą,
- test kontraktowy dostarcza dowodów, nie formalnego dowodu LSP: jedna masa (100 kg), brak sprawdzenia niezmienności wejścia; lista implementacji w `policies()` musi być ręcznie utrzymywana.

**Testy:**

- `DeliveryPricePolicyContractTest.everyPolicyObeysTheSubstitutionContract` (warianty „standard policy”, „express policy”): metoda stabilna, wynik nieujemny, skala 2, determinizm,
- `DeliveryPriceCalculatorTest.delegatesToPolicySelectedByShippingMethod` - parametryzowany: STANDARD `17.28`, EXPRESS `31.32` dla 3 kg,
- `DeliveryPriceCalculatorTest.rejectsDuplicatePolicyForOneShippingMethod` - dwie polityki STANDARD dają `IllegalArgumentException`,
- `DeliveryPriceCalculatorTest.reportsMissingPolicy` - kalkulator tylko ze STANDARD rzuca wyjątek dla EXPRESS.

**Demonstracja na żywo (8 min):** najpierw interfejs i jego Javadoc, potem obie polityki. Uruchom testy domeny. W kopii roboczej zmień w jednej polityce `setScale(2, ...)` na `setScale(3, ...)` i pokaż czerwony test kontraktowy; cofnij zmianę. Pytanie: „Co trzeba zmienić, żeby dodać SAME_DAY? Ile plików?” oraz „Dlaczego nie ma `IDeliveryPriceCalculator`?”.

---

## Przykład 6. Porty i przypadek użycia (`pl.training.module2.application`)

**Sekcja teorii:** 5.6 Reguła zależności i composition root; 5.9 Porty i przypadek użycia.

**Co ilustruje:** dwa wąskie porty wyjściowe należące do pakietu `application`, nazwane przez potrzebę wnętrza, oraz `CreateDeliveryQuote`, który koordynuje jeden cel: oblicza cenę, tworzy `DeliveryQuote`, zapisuje, powiadamia i zwraca. Nie zna wzoru ceny, mechanizmu zapisu ani formatu komunikatu.

**Klasy:** `application/QuoteRepository.java` (`void save(DeliveryQuote)`), `application/QuoteNotifier.java` (`void quoteCreated(DeliveryQuote)`), oba `@FunctionalInterface`; `application/CreateDeliveryQuote.java` z rekordem `Command`.

```java
repository.save(quote);
notifier.quoteCreated(quote);
return quote;
```

**Na co zwrócić uwagę:**

- po jednej metodzie, bo są dwie odrębne role, a nie dlatego, że „ISP wymaga jednej metody”; brak dziedziczenia `CrudRepository<DeliveryQuote>`,
- port należy do strony formułującej potrzebę; adapter importuje port, nie odwrotnie,
- kolejność `save`, potem `quoteCreated` to decyzja protokołu wynikająca z wymagania biznesowego, nie z SOLID; wyjątek przy zapisie przerywa przebieg,
- kolejność nie daje atomowości: jeśli zapis się uda, a powiadomienie zawiedzie, potrzebne mogą być ponowienia, idempotencja lub transactional outbox (świadomie nieimplementowane),
- zmiana semantyki względem wersji zastanej: w legacy nie było możliwości awarii zapisu, więc zachowanie przy błędzie jest **nową, jawnie uzgodnioną** decyzją, a nie przeniesionym zachowaniem.

**Testy:** `CreateDeliveryQuoteTest`:

- `calculatesStoresAndNotifiesAboutQuote` - cena `17.28`, dokładnie jeden zapis i jedno powiadomienie z tym samym obiektem,
- `doesNotNotifyWhenSavingFails` - repozytorium rzuca `IllegalStateException`, wyjątek propaguje się, lista powiadomień pozostaje pusta.
- Porty jako lambdy (`savedQuotes::add`) nie udają bazy ani brokera; realne adaptery wymagają testów integracyjnych (moduł 3).

**Demonstracja na żywo (8 min):** pokaż oba porty, potem przypadek użycia i test awarii. Pytania: „Kto jest właścicielem tego interfejsu?”, „Czy dodalibyśmy tu `findAll()`? Dla kogo?”, „Skąd wiemy, że powiadomienie po błędzie zapisu jest niedozwolone?”.

---

## Przykład 7. Adaptery i composition root (`pl.training.module2.adapter`)

**Sekcja teorii:** 3.3 Kwarantanna: izoluj problem za granicą; 5.6 Reguła zależności i composition root; 5.10 Kierunek zależności i bilans decyzji.

**Co ilustruje:** konkretne efekty wyniesione poza przypadek użycia; adaptery importują porty aplikacji, aplikacja nie importuje adapterów. `Module2Examples.main` ręcznie składa graf: `FuelSurcharge(0.08)`, dwie polityki, `DeliveryPriceCalculator`, `InMemoryQuoteRepository`, `ConsoleQuoteNotifier`, `CreateDeliveryQuote`; uruchamia też wersję zastaną i porównuje ceny.

**Klasy:** `adapter/InMemoryQuoteRepository.java` (lista w pamięci, `quotes()` zwraca `List.copyOf`), `adapter/ConsoleQuoteNotifier.java` (ten sam format komunikatu co w legacy: `"Quote ready for %s: %s costs %s%n"`), `Module2Examples.java` (composition root).

**Na co zwrócić uwagę:**

- adapter to naturalne miejsce kwarantanny (slajd 3.3): gdyby zapis trafiał do obcego systemu, tłumaczenie jego modelu i błędów zostałoby tutaj, a przypadek użycia by się nie zmienił,
- `InMemoryQuoteRepository` to działający adapter demonstracyjny, nie substytut testu trwałości: brak bezpieczeństwa wątkowego, transakcji, przetrwania restartu,
- format komunikatu w `ConsoleQuoteNotifier` został przeniesiony bez zmian, więc wyjście programu jest takie samo jak w legacy,
- composition root zna wszystkie konkrety i nie zawiera reguł biznesowych; ręczne konstruktory to pełnoprawny root - kontener DI nie tworzy poprawnego kierunku zależności samym faktem użycia (KISS, YAGNI),
- reguła zależności działa tu tylko na poziomie pakietów i importów: wszystko jest w jednym module Maven; silniejszą, automatyczną kontrolę pokazuje scena warsztatu `m2/s10_boundarycheck`,
- refaktoryzacja zwiększyła liczbę klas; wartością jest rozdzielenie niezależnych decyzji, nie mniejsza liczba linii.

**Testy:** `InMemoryQuoteRepositoryTest.storesQuote` - zapisana oferta jest zwracana przez `quotes()`. `ConsoleQuoteNotifier` nie ma testu (treść konsoli nie jest przechwytywana) - to motywacja do adaptera buforującego w rozwiązaniu Ćwiczenia 3.

**Demonstracja na żywo (8 min):** narysuj dwie strzałki: przepływ sterowania `CreateDeliveryQuote -> QuoteNotifier -> ConsoleQuoteNotifier` oraz zależność źródłową `ConsoleQuoteNotifier -> QuoteNotifier`. Wykonaj na żywo `grep -r "import pl.training.module2.adapter" src/main/java/pl/training/module2/application src/main/java/pl/training/module2/domain` (brak wyników). Pytanie: „Czy wywołanie adaptera z przypadku użycia łamie regułę zależności?”, „Gdzie dodalibyśmy trzecią politykę?”.

---

## Przykład 8. Lokalna poprawa Boy Scout z testem równoważności (`pl.training.module2.boyscout`)

**Sekcja teorii:** 6.1 Boy Scout Rule: heurystyka, nie mandat; 6.2 Przykład lokalnej poprawy; 6.3 Bezpieczna sekwencja i typowe nadużycia; 6.4 Małe zestawy zmian.

**Co ilustruje:** małą, lokalną refaktoryzację klasy formatera raportu wydania, wykonaną przy okazji bieżącej pracy, bez zmiany obserwowalnego zachowania, zweryfikowaną testem różnicowym (stara i nowa wersja obok siebie) oraz niezależnym oraklem.

**Klasy:**

- `boyscout/DeploymentStatus.java`, `boyscout/DeploymentResult.java` - wspólny model; normalizacja (`strip`) i walidacja raz, w konstruktorze rekordu.
- `boyscout/before/ReleaseSummaryFormatter.java` - nazwy `s`, `n`, `r`, konkatenacja w pętli, powielony format wiersza.
- `boyscout/after/ReleaseSummaryFormatter.java` - wydzielone `validate` i `formatResult`, nazwa `successfulDeployments`, `StringBuilder`.

**Na co zwrócić uwagę:**

- Poprawa **nie** dodaje nowej walidacji, nie zmienia sygnatury i zachowuje kolejność kontroli (`releaseId` null, `results` null, pusty `releaseId`, pusta lista, `null` element). Kusi, żeby „przy okazji” zaostrzyć kontrakt; to byłaby zmiana zachowania.
- Kombinacja `format(" ", null)` pokazuje, że kolejność walidacji jest częścią kontraktu: oba warianty muszą rzucić ten sam wyjątek (`NullPointerException` z komunikatem `results`, bo kontrole null są przed kontrolą pustego napisu).
- Test różnicowy sam w sobie nie wystarcza (dwie zgodne wersje mogą być jednocześnie błędne), dlatego jest też test z niezależnie zapisanym oczekiwanym tekstem.
- Boy Scout to heurystyka, nie mandat: zmiana publicznego API, aktualizacja zależności czy naprawa reguły biznesowej to nie jest „drobne sprzątanie”.
- Masowe formatowanie lub przeniesienia trzeba oddzielić od ręcznych poprawek (slajd 6.4).

**Testy:** `BoyScoutEquivalenceTest`:

- `localCleanupPreservesRepresentativeOutputs` - porównanie wyników `before` i `after` (pojedynczy sukces, pojedyncza porażka, mieszany wynik ze spacjami w `releaseId`),
- `refactoredCodeProducesTheIndependentlySpecifiedSummary` - niezależny orakl tekstu raportu,
- `localCleanupPreservesValidationFailuresAndTheirOrder` - ten sam typ i komunikat wyjątku dla sześciu błędnych wejść,
- `bothVersionsExposeTheSameCallableContract` - obie wersje pasują do tego samego interfejsu funkcyjnego (zgodność sygnatury),
- `sharedInputModelRejectsIncompleteResults` - walidacja `DeploymentResult`.

**Demonstracja na żywo (10 min):**

1. Otwórz `before` i poproś uczestników o listę zapachów (słownik z modułu 1).
2. Otwórz `after` obok (widok porównania w IDE). Pokaż, co się zmieniło, a co zostało celowo zachowane (walidacja, sygnatura).
3. Uruchom `BoyScoutEquivalenceTest`.
4. Eksperyment: w `after.validate` zamień kolejność kontroli (`isBlank` przed `requireNonNull(results)`). Test `localCleanupPreservesValidationFailuresAndTheirOrder` zawiedzie na przypadku `(" ", null)`. Przywróć.
5. Eksperyment 2: w `after` dodaj walidację „przy okazji” (np. odrzucanie `releaseId` dłuższego niż 20 znaków) i zapytaj, czy to nadal Boy Scout. Wycofaj.

Uwaga: w Ćwiczeniu 4 uczestnicy pracują na `before`. Przed ćwiczeniem nie pokazuj `after` jako wzorca do skopiowania albo zaznacz, że ich zadanie jest szersze (seria zmian, zmiana zachowania, opis).

---

## Przykład 9. `Module2Examples` - przegląd całego modułu (`pl.training.module2`)

**Sekcja teorii:** wszystkie sekcje.

**Co ilustruje:** jeden program uruchamia po kolei: wersję zastaną i zrefaktoryzowaną wyceny (porównanie cen), lokalną poprawę Boy Scout (ostatnia linia raportu), renderer ADR (pierwsza linia dokumentu) i bramkę kompilatora (czysty kod z `-Werror`).

**Klasy:** `src/main/java/pl/training/module2/Module2Examples.java`.

**Na co zwrócić uwagę:** ścieżka `legacy` istnieje tylko dla demonstracji porównania i współdzieli typy domenowe (`Parcel`, `DeliveryQuote`) z nową wersją; zmiana ich inwariantów wpływa na obie.

**Testy:** `Module2ExamplesTest.runsAllModuleExamples` - tylko `assertDoesNotThrow`; nie sprawdza treści wyjścia (warto to wskazać jako słabą wyrocznię, por. moduł 1).

**Demonstracja na żywo:** dobry punkt startowy na początku modułu (pokazanie „mapy”) i na końcu (podsumowanie): przy każdej linii wyjścia zapytaj, w której pętli informacji zwrotnej działa dany mechanizm.

---

## Proponowany plan modułu (~3 h)

| Blok | Czas | Slajdy | Przykłady | Zadania |
| --- | --- | --- | --- | --- |
| Fast feedback | ~30 min | 1.1-1.6 | Przykład 1 | Aktywność 2.1, Ćwiczenie 1 |
| Decyzje | ~40 min | 2.1-2.7 | Przykład 2 | Aktywność 2.2, Ćwiczenie 2 |
| Akceptacja i degradacja | ~20 min | 3.1-4.4 | (Przykład 7 jako kwarantanna) | Aktywność 2.3 |
| Projekt | ~55 min | 5.1-5.10 | Przykłady 3-7 | Ćwiczenie 3 |
| Prewencja codzienna | ~35 min | 6.1-6.6 | Przykład 8 | Ćwiczenie 4 |
| Zamknięcie | ~5 min | wnioski, sprawdzenie wiedzy, lista kontrolna | Przykład 9 | - |

Sceny warsztatu CineLegacy (`src/main/resources/md/warsztat/przewodnik/02-prewencja.md`) są pokazami prowadzącego do wyboru; każda trwa około 10-15 minut i może zastąpić demonstrację odpowiedniego przykładu.
