# Moduł 8. Strategie i dobre praktyki - przewodnik prowadzącego po przykładach

Materiał dla prowadzącego. Opisuje przykłady kodu z pakietu `pl.training.module8`, ich związek z teorią (`src/main/resources/08-strategie-i-dobre-praktyki.md`) oraz sugerowany przebieg demonstracji.

## Uruchamianie przykładów i testów

### Java

```bash
mvn -q compile && java -cp target/classes pl.training.module8.Module8Examples
mvn test -Dtest='pl.training.module8.**'
```

Albo z IDE: uruchom `Module8Examples.main` oraz testy z katalogu `src/test/java/pl/training/module8`.

Oczekiwany wynik `Module8Examples` (chroniony przez `Module8ExamplesTest`):

```text
Stopniowa migracja: 53.97/Agreement
Boy Scout: Successful: 1/2
Code review: ready=true
Dokumentowanie: # ADR-0042: Use Branch by Abstraction
Narzędzia: compiled=true
Zarządzanie ryzykiem: ADVANCE
```

Uwaga: przykład `tooling` wymaga uruchomienia na JDK (nie JRE), bo korzysta z `ToolProvider.getSystemJavaCompiler()`. Opcja `--release 25` wymaga JDK w wersji co najmniej 25.

### C#

```bash
cd csharp && dotnet run --project src/Training.Module8
dotnet test
```

Kod: `csharp/src/Training.Module8` (katalogi `Incremental`, `BoyScout`, `Collaboration`, `Documentation`, `Tooling`, `Risk`), testy: `csharp/tests/Training.Module8.Tests`. Odpowiednikiem kompilatora w pamięci jest `Tooling/InMemoryCSharpCompiler.cs` (Roslyn). Wersje Boy Scout są w `BoyScout/Before` i `BoyScout/After`.

### TypeScript

```bash
cd typescript && npm ci && npm run build && npm run module8
npm test
```

Kod: `typescript/src/module8` (te same pakiety, `boyscout/before` i `boyscout/after`), testy: `typescript/test/module8`. Odpowiednikiem kompilatora jest `tooling/InMemoryTypeScriptCompiler.ts`.

---

## Mapa przykładów

| Przykład (pakiet) | Sekcja teorii | Kluczowe pliki (`src/main/java/pl/training/module8/...`) | Testy (`src/test/java/pl/training/module8/...`) | Warsztat |
| --- | --- | --- | --- | --- |
| `incremental` | 1. Stopniowa refaktoryzacja kontra przepisanie (1.3-1.6) | `incremental/PricingEngine.java`, `LegacyPriceCalculator.java`, `LegacyPricingEngineAdapter.java`, `CandidatePricingEngine.java`, `MigratingPricingEngine.java`, `MigrationMode.java`, `VerificationEvent.java`, `VerificationReporter.java`, `PriceRequest.java`, `PriceQuote.java` | `incremental/PricingEngineContractTest`, `MigratingPricingEngineTest`, `PricingValueObjectsTest` | W1 |
| `boyscout` | 2. Zasada Boy Scout (2.2-2.3) | `boyscout/before/ReleaseSummaryFormatter.java`, `boyscout/after/ReleaseSummaryFormatter.java`, `DeploymentResult.java`, `DeploymentStatus.java` | `boyscout/BoyScoutEquivalenceTest` | W2 |
| `collaboration` | 3. Praca zespołowa (3.2-3.6) | `collaboration/ExampleTeamReviewPolicy.java`, `ChangeSet.java`, `ChangeIntent.java`, `VerificationEvidence.java`, `EvidenceKind.java`, `ReviewReadiness.java`, `ReadinessProblem.java` | `collaboration/ExampleTeamReviewPolicyTest` | W2 |
| `documentation` | 4. Dokumentowanie zmian (4.3-4.4) | `documentation/DecisionRecord.java`, `DecisionRecordMarkdownRenderer.java`, `DecisionId.java`, `DecisionStatus.java`, `DecisionOption.java`, `DecisionConsequence.java`, `ConsequenceKind.java` | `documentation/DecisionRecordTest`, `DecisionRecordMarkdownRendererTest` | aktywność A |
| `tooling` | 5. Narzędzia (5.3, 5.6-5.7) | `tooling/InMemoryJavaCompiler.java`, `WarningPolicy.java`, `CompilationResult.java`, `CompilationDiagnostic.java` | `tooling/InMemoryJavaCompilerTest` | W3 |
| `risk` | 6. Zarządzanie ryzykiem (6.3-6.4) | `risk/RolloutPolicy.java`, `RolloutThresholds.java`, `RolloutSnapshot.java`, `RolloutDecision.java` | `risk/RolloutPolicyTest`, `RolloutSnapshotTest`, `RolloutThresholdsTest` | W1, aktywność B |
| całość | wszystkie sekcje | `Module8Examples.java` | `Module8ExamplesTest` | - |

---

## 1. `incremental` - Branch by Abstraction i równoległa weryfikacja

### Sekcja teorii

1.3 Branch by Abstraction i Strangler Fig, 1.4 Architektura przejściowa, 1.5 Przykład: równoległa weryfikacja kalkulatora, 1.6 Granice trybu shadow.

### Co ilustruje

Pełną sekwencję Branch by Abstraction na czystym obliczeniu ceny: stara implementacja (`LegacyPriceCalculator`, rabat w procentach 0-100) zostaje schowana za małą abstrakcją `PricingEngine` przez adapter, obok powstaje implementacja kandydująca, a `MigratingPricingEngine` centralizuje wybór ścieżki w trzech jawnie nazwanych trybach: `LEGACY`, `VERIFY`, `CANDIDATE`.

### Kluczowe klasy

- `incremental/PricingEngine.java` - jedna metoda `quote(PriceRequest)`. Javadoc zapisuje warunek bezpieczeństwa trybu shadow: implementacje nie wykonują I/O i nie modyfikują zewnętrznego stanu.
- `incremental/PriceRequest.java`, `PriceQuote.java` - obiekty wartości. Walidacja **przed** zaokrągleniem (wartość `-0.001` nie staje się `0.00`), `HALF_EVEN`, skala 2 dla pieniędzy i 4 dla stopy rabatu.
- `incremental/LegacyPriceCalculator.java` - stary kod, rabat jako procent.
- `incremental/LegacyPricingEngineAdapter.java` - tłumaczy stopę (`0.10`) na procent (`10.00`) przez `movePointRight(2)`; kod domenowy nie zna jednostki legacy.
- `incremental/CandidatePricingEngine.java` - nowa implementacja; świadomie zaokrągla rabat przed odejmowaniem, tak jak legacy.
- `incremental/MigrationMode.java` - enum zamiast flagi boolean (trzy stany).
- `incremental/VerificationEvent.java` - zamknięta hierarchia (`sealed`): `Agreement`, `Divergence`, `CandidateFailure`.
- `incremental/VerificationReporter.java` - port raportowania, `ignoring()` jako wartość domyślna.
- `incremental/MigratingPricingEngine.java` - router migracji.

Serce trybu `VERIFY` (`MigratingPricingEngine.verify`):

```java
PriceQuote legacyQuote = requireQuote(legacy.quote(request), "legacy quote");
try {
    PriceQuote candidateQuote = requireQuote(
            candidate.quote(request), "candidate quote");
    VerificationEvent event = legacyQuote.equals(candidateQuote)
            ? new VerificationEvent.Agreement(request, legacyQuote)
            : new VerificationEvent.Divergence(request, legacyQuote, candidateQuote);
    tryToReport(event);
} catch (RuntimeException failure) {
    tryToReport(new VerificationEvent.CandidateFailure(request, legacyQuote,
            failure.getClass().getName(),
            Objects.toString(failure.getMessage(), "")));
}
return legacyQuote;
```

### Na co zwrócić uwagę uczestników

- Kroki "abstrakcja + adapter + przekierowanie wywołań" są refaktoryzacją (zachowują zachowanie). Budowa kandydata i przełączenie na niego to migracja.
- Legacy jest autorytatywne w `VERIFY`: najpierw liczone jest legacy; jeśli ono rzuci wyjątek, kandydat w ogóle nie jest wywoływany.
- Awaria kandydata (`RuntimeException`, także zwrócenie `null`) zamienia się w zdarzenie `CandidateFailure` i nie wpływa na odpowiedź.
- Awaria reportera jest połykana (`tryToReport`) - telemetria nie może zmienić wyniku, ale też nie ma gwarancji dostarczenia zdarzenia.
- `Error` (błąd JVM, np. `AssertionError`) **nie** jest maskowany.
- Przykład jest synchroniczny: kandydat i reporter zwiększają latencję. Teoria (1.6) mówi wprost, że produkcyjny shadow wymaga izolacji, timeoutu, bulkheadu, limitu kopiowanego ruchu.
- Shadow jest bezpieczny dla czystego obliczenia. Nie wolno w ten sposób dublować płatności, wysyłki wiadomości, zapisów czy publikacji zdarzeń.
- Zgodność dwóch implementacji nie dowodzi poprawności, bo mogą dzielić ten sam błąd. Dlatego test kontraktowy ma **niezależne** oczekiwane wartości.
- Moment zaokrąglenia: przypadek `0.01 x 1` z rabatem `0.5` daje `0.01` (rabat `0.005` zaokrąglony do `0.00` przed odejmowaniem). Zaokrąglenie dopiero wyniku końcowego dałoby `0.00`.
- Antywzorce z 1.7: logika przełączania rozrzucona po kodzie domenowym, flaga bez właściciela i terminu, abstrakcja kopiująca całe API.

### Testy chroniące zachowanie

- `PricingEngineContractTest` - sparametryzowany test kontraktowy uruchamiany dla `LegacyPricingEngineAdapter` i `CandidatePricingEngine`: przykłady kanoniczne (w tym połowa centa i `19.995 x 2` przy `0.12555`), determinizm i skala 2, odrzucenie `null` z komunikatem `request`.
- `MigratingPricingEngineTest` - liczba wywołań w `LEGACY` i `CANDIDATE` (nieaktywna implementacja nie jest wołana), `Agreement` i `Divergence` w `VERIFY` z wynikiem legacy, wyjątek kandydata, `null` od kandydata jako `CandidateFailure` z typem `NullPointerException` i komunikatem `candidate quote`, awaria reportera, autorytatywna awaria legacy (kandydat nie wywołany, brak zdarzeń), niemaskowanie `AssertionError`, `null` request nie dociera do żadnej implementacji, walidacja konstruktora.
- `PricingValueObjectsTest` - normalizacja `PriceRequest` (`12.345` -> `12.34`, `0.12345` -> `0.1234`), walidacja przed zaokrągleniem (`-0.001`, `-0.00001`, `1.00001`), normalizacja i walidacja `PriceQuote`.

### Sugerowany przebieg demonstracji (15-20 minut)

1. Pokaż `LegacyPriceCalculator` i zapytaj: "jaki jest kontrakt tej metody?". Zbierz odpowiedzi (jednostka rabatu, skale, moment zaokrąglenia, komunikaty wyjątków).
2. Pokaż `PricingEngine` i adapter. Podkreśl, że to jeszcze refaktoryzacja.
3. Pokaż `PricingEngineContractTest`. Uruchom go i zwróć uwagę, że IDE pokazuje dwa warianty każdego testu.
4. Eksperyment na żywo: w `CandidatePricingEngine` usuń `.setScale(MONEY_SCALE, ROUNDING)` przy rabacie (zaokrąglenie tylko na końcu, w `PriceQuote`). Uruchom test kontraktowy: przypadek `0.01, 1, 0.5` powinien zawieść (oczekiwane `0.01`, kandydat zwraca `0.00`). Przywróć kod.
5. Pokaż `MigratingPricingEngine` i trzy tryby. Uruchom `Module8Examples`: `53.97/Agreement`.
6. Zmień w `Module8Examples` tryb na `VERIFY` z kandydatem zwracającym inną cenę (np. lambda `request -> new PriceQuote(BigDecimal.ONE)`) i pokaż `Divergence` przy niezmienionej odpowiedzi. Przywróć kod.
7. Omów 1.6: co trzeba dodać, żeby to był produkcyjny shadow. Przejdź do Warsztatu 1.

---

## 2. `boyscout` - lokalna poprawa z testem równoważności

### Sekcja teorii

2.1 Heurystyka, nie mandat do dowolnych zmian, 2.2 Przykład lokalnej poprawy, 2.3 Bezpieczna sekwencja, 2.4 Typowe nadużycia.

### Co ilustruje

Małą, lokalną refaktoryzację klasy formatera raportu wydania, wykonaną przy okazji bieżącej pracy, bez zmiany obserwowalnego zachowania, zweryfikowaną testem różnicowym (stara i nowa wersja obok siebie) oraz niezależnym oraklem.

### Kluczowe klasy

- `boyscout/DeploymentStatus.java`, `boyscout/DeploymentResult.java` - wspólny model; normalizacja (`strip`) i walidacja raz, w konstruktorze rekordu.
- `boyscout/before/ReleaseSummaryFormatter.java` - nazwy `s`, `n`, `r`, konkatenacja w pętli, powielony format wiersza.
- `boyscout/after/ReleaseSummaryFormatter.java` - wydzielone `validate` i `formatResult`, nazwa `successfulDeployments`, `StringBuilder`.

### Na co zwrócić uwagę uczestników

- Poprawa **nie** dodaje nowej walidacji, nie zmienia sygnatury i zachowuje kolejność kontroli (`releaseId` null, `results` null, pusty `releaseId`, pusta lista, `null` element). Kusi, żeby "przy okazji" zaostrzyć kontrakt; to byłaby zmiana zachowania.
- Kombinacja `format(" ", null)` pokazuje, że kolejność walidacji jest częścią kontraktu: oba warianty muszą rzucić ten sam wyjątek (`NullPointerException` z komunikatem `results`, bo kontrole null są przed kontrolą pustego napisu).
- Test różnicowy sam w sobie nie wystarcza (dwie zgodne wersje mogą być jednocześnie błędne), dlatego jest też test z niezależnie zapisanym oczekiwanym tekstem.
- Boy Scout to heurystyka, nie mandat: zmiana publicznego API, aktualizacja zależności czy naprawa reguły biznesowej to nie jest "drobne sprzątanie".
- Nie każda nazwa wymaga osobnego commitu, ale masowe formatowanie lub przeniesienia trzeba oddzielić.

### Testy chroniące zachowanie

- `BoyScoutEquivalenceTest`:
  - `localCleanupPreservesRepresentativeOutputs` - porównanie wyników `before` i `after` (pojedynczy sukces, pojedyncza porażka, mieszany wynik ze spacjami w `releaseId`),
  - `refactoredCodeProducesTheIndependentlySpecifiedSummary` - niezależny orakl tekstu raportu,
  - `localCleanupPreservesValidationFailuresAndTheirOrder` - ten sam typ i komunikat wyjątku dla sześciu błędnych wejść,
  - `bothVersionsExposeTheSameCallableContract` - obie wersje pasują do tego samego interfejsu funkcyjnego (zgodność sygnatury),
  - `sharedInputModelRejectsIncompleteResults` - walidacja `DeploymentResult`.

### Sugerowany przebieg demonstracji (10-15 minut)

1. Otwórz `before` i poproś uczestników o listę "zapachów".
2. Otwórz `after` obok (widok porównania w IDE). Pokaż, co się zmieniło, a co zostało celowo zachowane (walidacja, sygnatura).
3. Uruchom `BoyScoutEquivalenceTest`.
4. Eksperyment: w `after.validate` zamień kolejność kontroli (`isBlank` przed `requireNonNull(results)`). Test `localCleanupPreservesValidationFailuresAndTheirOrder` zawiedzie na przypadku `(" ", null)`. Przywróć.
5. Eksperyment 2: w `after` dodaj walidację "przy okazji" (np. odrzucanie `releaseId` dłuższego niż 20 znaków) i zapytaj, czy to nadal Boy Scout. Wycofaj.
6. Przejdź do sekcji 2.3 (bezpieczna sekwencja) i 2.4 (nadużycia), potem do Warsztatu 2 (bez pokazywania, jak dzielić serię).

Uwaga: w Warsztacie 2 uczestnicy pracują na `before`. Przed warsztatem nie pokazuj `after` jako wzorca do skopiowania albo zaznacz, że ich zadanie jest szersze (seria zmian, zmiana zachowania, opis, przegląd).

---

## 3. `collaboration` - jawna polityka gotowości do przeglądu

### Sekcja teorii

3.2 Małe zestawy zmian, 3.3 Przygotowanie do przeglądu kodu, 3.5 Przykład polityki gotowości, 3.6 Czego nie należy łączyć.

### Co ilustruje

Przykładową politykę zespołu zapisaną w kodzie: zestaw zmian jest gotowy do przeglądu, gdy ma dokładnie jedną główną intencję, co najmniej jeden dowód weryfikacji i niezależnie zielony build. Problemy są typowane (enum), a nie ukryte w komunikacie tekstowym.

### Kluczowe klasy

- `collaboration/ChangeIntent.java` - `CHARACTERIZATION_TESTS`, `REFACTORING`, `BEHAVIOR_CHANGE`, `ROLLOUT` (odpowiada przykładowej serii z 3.2).
- `collaboration/EvidenceKind.java`, `VerificationEvidence.java` - rodzaj dowodu i niepusta obserwacja.
- `collaboration/ChangeSet.java` - rekord z migawkami kolekcji (`Set.copyOf`, `List.copyOf`).
- `collaboration/ReadinessProblem.java`, `ReviewReadiness.java` - typowane problemy, `ready()` gdy lista pusta.
- `collaboration/ExampleTeamReviewPolicy.java` - sama reguła:

```java
if (changeSet.intents().isEmpty()) {
    problems.add(ReadinessProblem.MISSING_INTENT);
} else if (changeSet.intents().size() > 1) {
    problems.add(ReadinessProblem.MIXED_PRIMARY_INTENTS);
}
if (changeSet.verificationEvidence().isEmpty()) {
    problems.add(ReadinessProblem.MISSING_VERIFICATION_EVIDENCE);
}
if (!changeSet.independentlyGreenBuild()) {
    problems.add(ReadinessProblem.BUILD_NOT_INDEPENDENTLY_GREEN);
}
```

### Na co zwrócić uwagę uczestników

- Nazwa `ExampleTeamReviewPolicy` i Javadoc mówią wprost: to przykład lokalnej umowy, a nie uniwersalny algorytm oceny przeglądu.
- Wartością jest jawność: autor i osoba przeglądająca widzą te same powody zatrzymania zmiany.
- Rozróżnienie intencji (co zmiana robi) i dowodu (jak to sprawdzono).
- Migawki kolekcji: późniejsza zmiana danych autora nie zmienia ocenionego obiektu.
- Polityka nie ocenia jakości testów ani projektu; to wciąż zadanie człowieka (lista z 3.3).
- Etykiety `BLOCKER`, `SUGGESTION`, `NIT` (3.4) wymagają wspólnie ustalonego znaczenia i nie zastępują argumentu.

### Testy chroniące zachowanie

- `ExampleTeamReviewPolicyTest`:
  - `acceptsAFocusedChangeWithEvidenceAndAGreenBuild` - jedna intencja, dowód, zielony build,
  - `reportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder` - trzy problemy w stabilnej kolejności,
  - `reportsAnUnspecifiedIntentAsATypedProblem` - `MISSING_INTENT`,
  - `snapshotsMutableInputCollections` - brak aliasów i niemodyfikowalne widoki.

### Sugerowany przebieg demonstracji (10 minut)

1. Zacznij od pytania: "po czym poznajecie, że PR jest gotowy do przeglądu?". Zapisz odpowiedzi.
2. Pokaż `ChangeIntent` i `ExampleTeamReviewPolicy`, porównaj z listą uczestników.
3. Uruchom test `reportsMixedIntentMissingEvidenceAndNonGreenBuildInStableOrder` i omów tytuł zestawu zmian "Move validator and change its rules" jako antyprzykład z 3.6.
4. Dyskusja: czego ta polityka nie sprawdza? (jakość orakli, zakres, plan wycofania, właściciele).
5. Omów pięć pytań z 3.3 i kolejność czytania dla recenzenta. To wprowadzenie do Warsztatu 2.

---

## 4. `documentation` - wykonywalny model ADR

### Sekcja teorii

4.1 Dokumentacja według trwałości, 4.3 Architecture Decision Record, 4.4 Przykład wykonywalnego modelu decyzji, 4.5 Dokumentacja żywa i historyczna.

### Co ilustruje

Model decyzji architektonicznej w lokalnym szablonie projektu (rdzeń ADR: tytuł, status, kontekst, decyzja, konsekwencje, rozszerzony o identyfikator, rozważone opcje i metodę weryfikacji) oraz deterministyczny renderer do Markdown.

### Kluczowe klasy

- `documentation/DecisionId.java` - lokalna konwencja `ADR-[0-9]{4}` (nie wymóg formatu ADR).
- `documentation/DecisionStatus.java` - `PROPOSED`, `ACCEPTED`, `REJECTED`, `DEPRECATED`, `SUPERSEDED`.
- `documentation/DecisionOption.java`, `DecisionConsequence.java`, `ConsequenceKind.java`.
- `documentation/DecisionRecord.java` - wymaga co najmniej jednej opcji i jednej konsekwencji, niepustych tekstów; kopie defensywne list.
- `documentation/DecisionRecordMarkdownRenderer.java` - stała kolejność sekcji, polskie etykiety statusów (`Zaakceptowana`, `Zastąpiona` itd.) i rodzajów konsekwencji.

### Na co zwrócić uwagę uczestników

- ADR jest dla decyzji o trwałym wpływie na architekturę, nie dla każdego Extract Method.
- Model wymaga przynajmniej jednej konsekwencji dowolnego rodzaju; w praktyce warto uczciwie opisać korzyści i koszty.
- Renderer gwarantuje formę, nie jakość decyzji. Wartość pochodzi z kontekstu, uczciwych alternatyw i weryfikacji.
- Przy zmianie kierunku stary ADR oznacza się jako zastąpiony (`SUPERSEDED`) i tworzy nowy; historia rozumowania zostaje.
- Tabela z 4.1: komentarz w kodzie opisuje "dlaczego" i stan obecny, nie historię edycji ("przeniesiono tę metodę").

### Testy chroniące zachowanie

- `DecisionRecordTest` - migawki list i niemodyfikowalne widoki; odrzucenie złego identyfikatora (`"42"`), pustego tytułu, pustej listy opcji, pustej listy konsekwencji, pustego uzasadnienia i opisu.
- `DecisionRecordMarkdownRendererTest` - pełny oczekiwany dokument jako text block (kolejność sekcji, etykiety, końcowe nowe linie), determinizm (dwukrotne renderowanie), odrzucenie `null`.

### Sugerowany przebieg demonstracji (10 minut)

1. Pokaż tabelę 4.1 i zapytaj, gdzie uczestnicy zapisują dziś decyzje.
2. Pokaż `DecisionRecord` i test renderera; uruchom `Module8Examples` (linia `Dokumentowanie: # ADR-0042: Use Branch by Abstraction`).
3. Pokaż w teście kompletny wynik Markdown jako wzór struktury.
4. Eksperyment: spróbuj utworzyć `DecisionRecord` z pustą listą konsekwencji i pokaż wyjątek `consequences must not be empty`.
5. Przejdź do aktywności A (ADR z decyzji z Warsztatu 1).

---

## 5. `tooling` - bramka kompilatora w pamięci

### Sekcja teorii

5.1 Różne narzędzia, różne dowody, 5.3 Kompilator Javy 25, 5.6 Przykład bramki kompilatora, 5.7 Minimalna bramka jakości.

### Co ilustruje

Politykę ostrzeżeń jako jawny parametr (`WarningPolicy`): ten sam kod z surowym typem przechodzi przy `ALLOW_WARNINGS` (z ostrzeżeniem) i nie przechodzi przy `TREAT_WARNINGS_AS_ERRORS` (odpowiednik `-Werror`). Diagnostyka jest zapisywana strukturalnie (rodzaj, kod, pozycja), a nie jako lokalizowany komunikat.

### Kluczowe klasy

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

### Na co zwrócić uwagę uczestników

- `--release 25` ustala poziom języka i API, ale nie gwarantuje, że Maven działa na JDK 25. W CI kontroluje się faktyczny JDK (toolchains lub jawne środowisko). Warto pokazać `mvn -v` na maszynie prowadzącego.
- `-Werror` to silna bramka dla nowego kodu, ale włączona nagle w dużym legacy zablokuje wszystkich. Stąd stan bazowy i ratchet (5.3, 5.4).
- Testy sprawdzają konkretne kody `javac` (`compiler.warn.raw.class.use`, `compiler.err.warnings.and.werror`), więc są świadomie związane z wersją kompilatora.
- Ten przykład nie zastępuje Mavena z pełną ścieżką klas, testów ani analizatorów (Checkstyle, PMD, SpotBugs pokrywają inne klasy problemów).
- Tabela 5.1: każde narzędzie daje inny dowód i ma inne granice. Zielony wynik jednego narzędzia to nie certyfikat.
- Refaktoryzacje IDE (5.2) nie widzą refleksji, konfiguracji, szablonów, konsumentów spoza repozytorium.

### Testy chroniące zachowanie

- `InMemoryJavaCompilerTest`:
  - `compilesTypedSourceAndKeepsGeneratedBytecodeInMemory` - czysty kod przechodzi nawet z `-Werror`, brak diagnostyk,
  - `reportsRawTypeByKindAndCompilerCodeWithoutRejectingSource` - surowy typ: sukces i ostrzeżenie `compiler.warn.raw.class.use`,
  - `rejectsTheSameRawTypeWhenWarningsAreErrors` - ten sam kod: porażka, ostrzeżenie oraz błąd `compiler.err.warnings.and.werror`,
  - `validatesCompilationRequest` - walidacja nazwy binarnej, źródła, polityki,
  - `diagnosticsCanRepresentAnAbsentImplementationSpecificCode` - brak kodu diagnostyki i walidacja pozycji.

### Sugerowany przebieg demonstracji (10-15 minut)

1. `java -version`, `mvn -v`: jaki JDK faktycznie buduje projekt? Porównaj z `maven.compiler.release` w `pom.xml`.
2. Pokaż `InMemoryJavaCompilerTest` i uruchom trzy pierwsze testy; pokaż różnicę w `successful` dla tego samego `RAW_SOURCE`.
3. Na żywo: skompiluj wszystkie źródła z `-Xlint:all` bez zmiany `pom.xml`, np.
   `javac --release 25 -Xlint:all -d /tmp/lint $(find src/main/java -name '*.java')`
   i pokaż, że nawet "czysty" projekt szkoleniowy ma historyczne ostrzeżenia (kategoria `auxiliaryclass` w module 1). To dobry most do Warsztatu 3: "co byście zrobili, gdyby jutro włączyć `-Werror`?".
4. Omów 5.7 (kolejność bramek) i przejdź do Warsztatu 3.

---

## 6. `risk` - jawna polityka wdrożenia etapowego

### Sekcja teorii

6.1 Ryzyko jest właściwością zmiany i kontekstu, 6.2 Warstwy kontroli, 6.3 Kryteria przed wdrożeniem etapowym, 6.4 Przykład jawnej polityki wdrożenia etapowego.

### Co ilustruje

Decyzję `ADVANCE` / `HOLD` / `ROLLBACK` na podstawie migawki metryk i progów ustalonych przed wdrożeniem. Dla dokładnego kontraktu odpowiedzi dopuszczalna liczba rozbieżności wynosi zero.

### Kluczowe klasy

- `risk/RolloutDecision.java` - trzy decyzje.
- `risk/RolloutThresholds.java` - minimalna próbka (>= 1), maksymalny wskaźnik błędów (0..1), maksymalne p95; odrzuca `NaN` i nieskończoność.
- `risk/RolloutSnapshot.java` - liczniki w granicach próbki, p95 skończone i nieujemne, pusta próbka ma p95 równe 0 i `errorRate()` równe 0.
- `risk/RolloutPolicy.java`:

```java
if (snapshot.mismatchedResponses() > 0
        || snapshot.errorRate() > thresholds.maximumErrorRate()
        || snapshot.p95LatencyMillis() > thresholds.maximumP95LatencyMillis()) {
    return RolloutDecision.ROLLBACK;
}
if (snapshot.sampleSize() < thresholds.minimumSampleSize()) {
    return RolloutDecision.HOLD;
}
return RolloutDecision.ADVANCE;
```

### Na co zwrócić uwagę uczestników

- Kontrole bezpieczeństwa są **przed** oceną wielkości próbki: mała próbka z potwierdzonym naruszeniem to nie "zdrowa próbka czekająca na dane".
- Progi są włącznie (`>` a nie `>=`): 5% przy progu 5% i p95 równe 250 ms przy progu 250 ms dają `ADVANCE`.
- `HOLD` to ani sukces, ani porażka, tylko brak danych do zwiększenia ekspozycji.
- Nie ma uniwersalnych progów. Zero rozbieżności ma sens dla obliczeń finansowych po ustalonej normalizacji; dla opóźnienia czy systemów probabilistycznych potrzebny jest uzgodniony przedział.
- Progów nie dopasowuje się po zobaczeniu wyników.
- Flaga nie cofnie danych zapisanych w nowym formacie: expand and contract (6.5), próba wycofania z danymi.
- Migracja kończy się po usunięciu kosztu legacy (6.6), nie po pierwszym sukcesie.

### Testy chroniące zachowanie

- `RolloutPolicyTest` - `ADVANCE` przy spełnionych progach i na granicach włącznych, `HOLD` dla zbyt małej zdrowej próbki (w tym pustej), `ROLLBACK` po rozbieżności, po naruszeniu błędów lub latencji, pierwszeństwo `ROLLBACK` nad zbyt małą próbką, odrzucenie `null`.
- `RolloutSnapshotTest` - wyliczenie `errorRate`, pusta próbka, liczniki na granicach, odrzucenie liczników ujemnych i większych niż próbka, `NaN`/nieskończoności/ujemnej latencji, niezerowego p95 dla pustej próbki.
- `RolloutThresholdsTest` - granice włączne, odrzucenie niedodatniej próbki, złych wskaźników błędów i progów latencji.

### Sugerowany przebieg demonstracji (10 minut)

1. Pokaż `RolloutPolicy` i zapytaj: "dlaczego rollback jest sprawdzany przed próbką?".
2. Uruchom `RolloutPolicyTest`; omów `safetyViolationsTakePriorityOverAnInsufficientSample`.
3. Eksperyment: zamień kolejność dwóch `if` w `decide`. Test `safetyViolationsTakePriorityOverAnInsufficientSample` zawiedzie (dla próbki 10 wynik to `HOLD` zamiast `ROLLBACK`). Przywróć.
4. Przeprowadź aktywność B (tabela etapów) bez pokazywania wyników testów.
5. Omów 6.5 (dane i wycofanie) oraz 6.6 (zamknięcie migracji) jako uzupełnienie Warsztatu 1.

---

## `Module8Examples` - przegląd całego modułu

Plik `src/main/java/pl/training/module8/Module8Examples.java` uruchamia po jednym deterministycznym przykładzie na temat (migracja w trybie `VERIFY`, Boy Scout, polityka przeglądu, ADR, kompilator, polityka wdrożenia). `Module8ExamplesTest` sprawdza dokładną listę wyników, niemodyfikowalność zwróconej listy oraz to, że `main` wypisuje każdy wynik w osobnej linii. Dobry punkt startowy na początku modułu (pokazanie "mapy") i na końcu (podsumowanie).

## Sugerowany plan modułu

| Blok | Czas (orientacyjnie) | Materiał |
| --- | --- | --- |
| Wprowadzenie: obserwowalne zachowanie, krótka pętla informacji zwrotnej | 10 min | teoria, wstęp; `Module8Examples` |
| Sekcja 1 + demo `incremental` | 30 min | przykład 1 |
| Warsztat 1 | 60-75 min | zadania, W1 |
| Sekcja 6 + demo `risk` + aktywność B | 25 min | przykład 6 |
| Sekcje 2-3 + demo `boyscout`, `collaboration` | 25 min | przykłady 2, 3 |
| Warsztat 2 | 60-75 min | zadania, W2 |
| Sekcja 4 + demo `documentation` + aktywność A | 30 min | przykład 4 |
| Sekcja 5 + demo `tooling` | 20 min | przykład 5 |
| Warsztat 3 | 60 min | zadania, W3 |
| Podsumowanie, lista kontrolna | 10 min | teoria, podsumowanie |
