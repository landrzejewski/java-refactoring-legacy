---
marp: true
theme: default
paginate: true
size: 16:9
style: |
  section { font-size: 24px; }
  section.lead h1 { font-size: 48px; }
  pre, code { font-size: 18px; }
---

<!-- _class: lead -->
# Moduł 8. Strategie i dobre praktyki
Jak planować, dzielić, dokumentować, weryfikować i bezpiecznie wdrażać refaktoryzację systemów legacy

---

## Agenda

1. Stopniowa refaktoryzacja kontra przepisanie systemu
2. Zasada Boy Scout
3. Praca zespołowa w refaktoryzacji
4. Dokumentowanie zmian
5. Narzędzia wspierające refaktoryzację
6. Zarządzanie ryzykiem
7. Warsztaty, lista kontrolna i podsumowanie

---

## Punkt wyjścia: kontrakt i krótka pętla

- **Obserwowalne zachowanie** to nie tylko wynik: także wyjątki, skutki uboczne i ich kolejność, stan trwały, zgodność API.
- Wydajność, telemetria i audyt są kontraktem, jeśli klienci na nich polegają.
- Zespół świadomie decyduje, co zachowuje, co naprawia, a co usuwa.
- Pętla: cel i kontrakt → brakujące testy → mały, odwracalny krok → szersza weryfikacja → integracja → obserwacja.
- Mały krok to **jedna spójna intencja** z testami, nie limit linii.

---

## 1.1. Domyślny kierunek

- Dla dużego systemu z ruchem zwykle bezpieczniejsza jest **praca przyrostowa** - wcześnie ujawnia nieznane reguły.
- Pozwala zmienić kierunek, zanim wydany zostanie cały budżet.
- Pełne przepisanie **przenosi złożoność domeny** do nowego kodu, często bez ukrytej wiedzy; cel migracji się przesuwa.
- Ograniczone przepisanie bywa racjonalne: mały zakres, znany kontrakt, mało integracji, brak stanu, niezależna weryfikacja.

---

## 1.2. Kryteria decyzji

| Pytanie | Za zmianą przyrostową | Za ograniczonym przepisaniem |
| --- | --- | --- |
| Zachowanie poznane? | liczne reguły niejawne, słabe testy | kompletny, mierzalny kontrakt |
| Zakres izolowany? | wiele integracji i wspólnych danych | wąska granica, niewielu klientów |
| Komponent ma stan? | skomplikowana migracja | brak stanu / sprawdzony plan |
| Musi działać stale? | ruch krytyczny, brak okna | proste, odwracalne przełączenie |
| Cel się zmienia? | aktywny rozwój starego systemu | stabilny zakres i kryteria |

Nowa technologia to nie wynik biznesowy - decyzję uzasadniają **mierzalne ograniczenia**.

---

## 1.3-1.4. Branch by Abstraction, Strangler Fig, architektura przejściowa

- **Branch by Abstraction**: stabilny kontrakt wewnątrz aplikacji, za nim stara i nowa implementacja (nie gałąź VCS).
- Abstrakcja i adapter mogą być refaktoryzacją; nowy dostawca i przełączenie to już **migracja**.
- **Strangler Fig**: brama, proxy lub router na granicy systemu kieruje część operacji do nowego komponentu.
- Każdy element przejściowy potrzebuje właściciela, testów, monitoringu i **kryterium usunięcia** - inaczej staje się nowym legacy.

---

## 1.5. Przykład: równoległa weryfikacja kalkulatora (1/2)

- `PricingEngine` - czysta abstrakcja bez I/O (warunek bezpiecznego shadow); adapter tłumaczy rabat na procenty legacy.
- `MigrationMode { LEGACY, VERIFY, CANDIDATE }` zamiast flagi boolean; wybór ścieżki scentralizowany w `MigratingPricingEngine`.
- Wynik porównania: `sealed interface VerificationEvent` - `Agreement`, `Divergence`, `CandidateFailure`.
- Kandydat musi zachować **moment zaokrąglenia** rabatu (`HALF_EVEN`).

```java
public PriceQuote quote(PriceRequest request) {          // adapter legacy
    return new PriceQuote(calculator.calculate(request.unitPrice(),
            request.quantity(), request.discountRate().movePointRight(2)));
}
```

---

## 1.5. Przykład: równoległa weryfikacja kalkulatora (2/2)

```java
private PriceQuote verify(PriceRequest request) {
    PriceQuote legacyQuote = requireQuote(legacy.quote(request), "legacy quote");
    try {
        PriceQuote candidateQuote = requireQuote(candidate.quote(request), "candidate quote");
        tryToReport(legacyQuote.equals(candidateQuote)
                ? new VerificationEvent.Agreement(request, legacyQuote)
                : new VerificationEvent.Divergence(request, legacyQuote, candidateQuote));
    } catch (RuntimeException failure) {
        tryToReport(new VerificationEvent.CandidateFailure(request, legacyQuote,
                failure.getClass().getName(), Objects.toString(failure.getMessage(), "")));
    }
    return legacyQuote;   // wynik legacy pozostaje autorytatywny
}
```

- Wspólny `PricingEngineContractTest` dla obu implementacji; przypadek „połowy centa” chroni moment zaokrąglenia.

---

## 1.6-1.7. Granice trybu shadow i antywzorce migracji

- Synchroniczny kandydat może zwiększyć latencję - produkcyjny shadow wymaga izolacji, timeoutu, bulkheadu i limitu ruchu.
- Podwójne wykonanie tylko dla **czystych obliczeń**; nie powielaj płatności, wysyłek, zapisów ani zdarzeń.
- Zgodność dwóch implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd.
- Antywzorce: wieloletnia gałąź, big bang, odtwarzanie każdej starej funkcji, flaga bez właściciela.
- Zgodność odpowiedzi API to nie dowód zgodności danych i transakcji.

---

## 2.1. Heurystyka, nie mandat do dowolnych zmian

- **Boy Scout Rule**: dotykany kod zostaw w nieco lepszym stanie - heurystyka, nie licencja na przebudowę.
- Poprawa dotyczy kodu bieżącej zmiany, jest mała, lokalna i łatwa do weryfikacji.
- Przykłady: lepsza nazwa, małe Extract Method, usunięcie lokalnej duplikacji.
- Zmiana publicznego API, zależności, migracja danych czy reguły biznesowej **nie są** drobnym sprzątaniem.

---

## 2.2. Przykład lokalnej poprawy: `ReleaseSummaryFormatter`

```java
validate(releaseId, results);
StringBuilder summary = new StringBuilder()
        .append("Release ").append(releaseId.strip()).append('\n');
int successfulDeployments = 0;
for (DeploymentResult result : results) {
    Objects.requireNonNull(result, "result");
    summary.append(formatResult(result)).append('\n');
    if (result.status() == DeploymentStatus.SUCCESS) {
        successfulDeployments++;
    }
}
return summary.append("Successful: ").append(successfulDeployments)
        .append('/').append(results.size()).toString();
```

- Przed: nazwy `s`, `n`, `r`, konkatenacja, powielony format wiersza. Po: nazwy, `validate`, `formatResult` - bez zmiany kontraktu.
- **Test różnicowy** porównuje obie wersje (także wyjątki); niezależny orakl chroni przed wspólnym błędem.

---

## 2.3-2.4. Bezpieczna sekwencja i typowe nadużycia

- Sekwencja: zielone testy → jeden mały ruch strukturalny → zawężone testy → przegląd diffu.
- Zmiana zachowania dopiero w **osobnym kroku**; gdy zakres rośnie, resztę odłóż do osobnego zadania.
- Nadużycia: nieograniczone porządki przy małym zadaniu, zmiana zachowania pod etykietą refaktoryzacji.
- Także: masowe formatowanie razem z naprawą, cudzy moduł bez komunikacji, abstrakcje „na przyszłość”.

---

## 3.1. Wspólny obraz zmiany

- Uzgodnij: jaki problem usuwasz, gdzie jest granica zachowania, co jest poza zakresem.
- Zespół zna serię kroków, właścicieli obszarów, osoby przeglądające i sposób weryfikacji.
- Plan wdrożenia, obserwacji i wycofania oraz kryterium usunięcia elementów przejściowych.
- Forma do skali: mała zmiana - opis zestawu zmian; migracja między zespołami - propozycja i zapis decyzji.
- Programowanie w parze pomaga odkrywać zachowanie, ale nie zastępuje testów.

---

## 3.2. Małe zestawy zmian - czego nie łączyć

- Dobry zestaw zmian: **jedna intencja**, powiązane testy, działające repozytorium po integracji.
- Liczba linii to przybliżenie: automatyczny rename w setkach miejsc to wciąż jedna intencja.
- Seria: testy charakteryzujące → seam → nowe zachowanie → wdrożenie etapowe → usunięcie starej ścieżki.
- Rozdzielaj: refaktoryzację od zmiany funkcjonalnej, formatowanie od logiki, diff automatyczny od ręcznego.
- Rozdzielaj też: zmianę schematu od przełączenia odczytów, flagę od zwiększania ekspozycji.

---

## 3.3-3.4. Przegląd kodu i informacja zwrotna

- Opis odpowiada na pięć pytań: **dlaczego, co (i czego nie), jaki kontrakt, jakie dowody, jak wdrożyć i wycofać**.
- Wskaż kolejność czytania: testy kontraktowe → nowa granica → implementacje → konfiguracja.
- Przegląd ocenia też kompatybilność, obserwowalność, wycofanie i plan usunięcia kodu przejściowego.
- Komentarz dotyczy kodu i ryzyka: obserwacja, skutek, uzasadnienie, propozycja; etykiety `BLOCKER` / `SUGGESTION` / `NIT`.
- Spór o kierunek - rozmowa synchroniczna, wniosek zapisany.

---

## 3.5. Przykład polityki gotowości do przeglądu

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

- Intencja (`ChangeIntent`) oddzielona od dowodu (`VerificationEvidence`); problemy typowane, bez parsowania tekstu.
- To **lokalna umowa zespołu** - wartością jest jawność powodów zatrzymania zmiany.

---

## 4.1. Dokumentacja według trwałości

| Artefakt | Co powinien zawierać |
| --- | --- |
| komentarz | nieoczywiste uzasadnienie, ograniczenie, kontrakt zewnętrzny |
| test | wykonywalny przykład zachowania i granic |
| opis zestawu zmian | cel, zakres, dowody, ryzyko, wdrożenie i wycofanie |
| commit | jeden logiczny krok i jego powód |
| ADR | trwała decyzja architektoniczna, kontekst i konsekwencje |
| instrukcja operacyjna | procedura wdrożenia, diagnozy i odzyskania |

Kod opisuje stan obecny - „przeniesiono tę metodę” należy do VCS, wymaganie regulatora do komentarza.

---

## 4.2. Opis zestawu zmian

- Nie powtarza diffu: problem, oczekiwany rezultat, zakres i elementy **świadomie pominięte**.
- Wskazuje zachowywany kontrakt; zmianę zachowania opisuje oddzielnie.
- Dowody z testów i analizy, wpływ na dane, integracje i operacje.
- Wdrożenie, warunki zatrzymania i wycofania, plan usunięcia flag i adapterów.
- „Testy przeszły” to za mało - jakie testy, jakie scenariusze, jakie ryzyka poza zakresem.

---

## 4.3-4.4. Architecture Decision Record

- **ADR** dla decyzji o trwałym wpływie (granica migracji, własność danych) - nie dla każdego Rename.
- Lekki rdzeń: tytuł, status, kontekst, decyzja, konsekwencje; lokalnie także opcje i weryfikacja.
- Zmiana kierunku: stary zapis oznacz jako zastąpiony, utwórz nowy - historia zostaje.
- Przykład: `DecisionRecord` wymaga opcji i konsekwencji, renderer deterministycznie generuje Markdown.
- Generator nie gwarantuje jakości decyzji - liczą się kontekst, uczciwe alternatywy i koszty.

---

## 4.5. Dokumentacja żywa i historyczna

- **Żywa**: instrukcja operacyjna, diagram stanu obecnego, opis wdrożenia - aktualizowane z systemem.
- **Historyczna**: ADR i zamknięty opis zestawu zmian - mieszanie ról gubi historię lub opisuje nieistniejący system.
- Właściciel architektury przejściowej sprawdza zgodność opisu routingu z produkcją i kryteria usunięcia.
- Weryfikuje też procedurę wycofania, alerty dla obu implementacji i termin usunięcia flagi.

---

## 5.1. Różne narzędzia, różne dowody

| Narzędzie | Główna rola | Czego samo nie dowodzi |
| --- | --- | --- |
| refaktoryzacja IDE | transformacja oparta na symbolach | zgodności klientów zewn. i użyć dynamicznych |
| kompilator | typy, składnia, ostrzeżenia | poprawności reguł biznesowych |
| analiza statyczna | wzorce ryzyka | braku fałszywych wyników |
| testy | zgodność dla wykonanych scenariuszy | zachowania poza testem |
| receptura automatyczna | powtarzalna transformacja wielu miejsc | każdej konsekwencji domenowej |
| przegląd kodu | intencja, projekt, ryzyko | braku regresji |

Bezpieczna bramka łączy **kilka niezależnych sygnałów**.

---

## 5.2-5.3. Refaktoryzacje IDE i kompilator Javy 25

- Rename, Move, Extract, Safe Delete korzystają z **modelu symboli** - bezpieczniejsze niż szukaj-i-zamień.
- IDE nie widzi refleksji, konfiguracji, szablonów, nazw serializowanych ani konsumentów spoza repozytorium.
- `--release 25` ustala poziom języka, ale nie JDK uruchamiający Mavena - kontroluj go w CI.
- `-Xlint` + `-Werror` to silna bramka, lecz nagle włączona w legacy zablokuje zespół.
- Stan bazowy → brak nowych naruszeń → stopniowa redukcja; tłumienie wąskie i uzasadnione.

---

## 5.4-5.5. Analiza statyczna, formatowanie i automatyzacja

- Checkstyle (konwencje), PMD (AST), SpotBugs (kod bajtowy) - **różne klasy problemów**, nie są zamienne.
- Analizator musi obsługiwać używaną Javę i mieć kompletny `classpath`.
- Formatowanie całego repozytorium oddziel od logiki albo stosuj **ratchet** dla dotykanych plików.
- Receptury (np. OpenRewrite) oparte na składni i typach są bezpieczniejsze od regexa - wymagają testów `before`/`after`.
- Automatyzacja zwiększa zasięg - także **zasięg błędu receptury**.

---

## 5.6-5.7. Bramka kompilatora i minimalna bramka jakości

```java
private static List<String> compilerOptions(WarningPolicy warningPolicy) {
    var options = new ArrayList<>(BASE_OPTIONS); // --release 25, -proc:none, -Xlint:rawtypes
    if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
        options.add("-Werror");
    }
    return List.copyOf(options);
}
```

- `InMemoryJavaCompiler` zwraca strukturalną diagnostykę (kod, pozycja) - nie zastępuje Mavena ani analizatorów.
- Minimalna bramka: podgląd diffu → kompilacja na docelowym JDK → testy zawężone i pełne → analiza statyczna i przegląd → obserwacja wdrożenia.
- W CI wszystkie wymagane bramki pozostają obowiązkowe.

---

## 6.1. Ryzyko jest właściwością zmiany i kontekstu

- Ta sama transformacja: niskie ryzyko w narzędziu wewnętrznym, wysokie w autoryzacji płatności.
- Oceniaj krytyczność, zasięg skutków, znajomość zachowania i jakość orakli.
- Uwzględnij dane, transakcje, współbieżność, bezpieczeństwo, odwracalność i czas detekcji.
- Macierz prawdopodobieństwa i wpływu porządkuje rozmowę, ale liczą się **założenia, dowody i właściciel**.

---

## 6.2. Warstwy kontroli

| Ryzyko | Przykładowe zabezpieczenia |
| --- | --- |
| nieznane zachowanie | testy charakterystyki, kontraktowe i różnicowe |
| szeroki zasięg skutków | mała kohorta, deterministyczny routing, wdrożenie etapowe |
| awaria kandydata | izolacja, timeout, wynik kontrolny z legacy |
| regresja operacyjna | stan bazowy, SLI, SLO, alert |
| migracja danych | expand and contract, walidacja, źródło prawdy |
| kod przejściowy | właściciel, termin i kryterium usunięcia |

Zabezpieczenie musi odpowiadać mechanizmowi awarii - flaga nie cofnie danych w nowym formacie.

---

## 6.3-6.4. Kryteria i jawna polityka wdrożenia etapowego

```java
public RolloutDecision decide(RolloutSnapshot snapshot) {
    if (snapshot.mismatchedResponses() > 0
            || snapshot.errorRate() > thresholds.maximumErrorRate()
            || snapshot.p95LatencyMillis() > thresholds.maximumP95LatencyMillis()) {
        return RolloutDecision.ROLLBACK;
    }
    if (snapshot.sampleSize() < thresholds.minimumSampleSize()) {
        return RolloutDecision.HOLD;
    }
    return RolloutDecision.ADVANCE;
}
```

- Przed ekspozycją: populacje, próbka, metryki, SLO, kryteria przejścia i wycofania, **właściciel decyzji**.
- Progi ustal z góry i nie dopasowuj ich do wyników; dla obliczeń finansowych dopuszczalna różnica może wynosić zero.
- Kontrole bezpieczeństwa **przed** rozmiarem próbki; `HOLD` to brak danych, nie sukces.

---

## 6.5-6.6. Dane, wycofanie i zamknięcie migracji

- Wycofanie `.jar` jest proste tylko bez niezgodnego stanu - dane migruj przez **expand and contract**.
- Stare pola usuwaj dopiero po zamknięciu okna wycofania; dual write wymaga strategii rekoncyliacji.
- Próba wycofania sprawdza czas powrotu, dane, żądania w toku i brak powtórzonych efektów.
- Migracja kończy się **po usunięciu kosztu legacy**: stary kod, flagi, adaptery, dokumentacja.
- Miarą są przeniesione funkcje i usunięte zależności, nie procent przepisanych linii.

---

## Warsztat 1: plan przyrostowej wymiany implementacji (60-75 min)

- Cel: zaplanować migrację kalkulatora na krytycznej ścieżce bez jednorazowego przełączenia.
- Kontrakt, abstrakcja, adapter, wspólny test kontraktowy, ocena shadow, kryteria `ADVANCE` / `HOLD` / `ROLLBACK`.
- Kandydat nie powiela nieidempotentnych efektów; wycofanie obejmuje kod, konfigurację i dane.
- Każdy krok oznaczony jako refaktoryzacja, migracja albo zmiana zachowania.

Szczegóły: zadania modułu 8, Warsztat 1.

---

## Warsztat 2: seria zmian gotowych do przeglądu (60-75 min)

- Cel: podzielić szeroką zmianę `ReleaseSummaryFormatter` na samodzielne zestawy zmian.
- Testy charakteryzujące → refaktoryzacja → nowa reguła raportu, każdy krok z zielonym buildem.
- Zmiany mechaniczne oddzielone od ręcznych; opis każdego zestawu na pięć pytań.
- Przegląd krzyżowy z etykietami `BLOCKER` / `SUGGESTION` / `NIT`.

Szczegóły: zadania modułu 8, Warsztat 2.

---

## Warsztat 3: wprowadzenie bramek do projektu legacy (60 min)

- Cel: lepsza automatyczna informacja zwrotna bez blokowania zespołu historycznymi naruszeniami.
- Pomiar bez zmian w kodzie, klasyfikacja wyników, stan bazowy, bramka dla nowego i dotykanego kodu.
- Narzędzia obsługują Javę 25; false positive odróżniony od zaakceptowanego ryzyka.
- Raport w CI ma właściciela; nowe naruszenia nie powiększają stanu bazowego.

Szczegóły: zadania modułu 8, Warsztat 3.

---

## Lista kontrolna

- **Strategia:** mierzalny cel, poprawnie nazwany rodzaj zmiany, architektura przejściowa z kryterium usunięcia.
- **Zespół i przegląd:** jedna intencja na zestaw, działający build, opis z zakresem, wdrożeniem i wycofaniem.
- **Dokumentacja:** właściwa trwałość artefaktu, komentarz wyjaśnia przyczynę, ADR z alternatywami.
- **Narzędzia:** sprawdzone użycia dynamiczne, faktyczny JDK 25, wąskie wyłączenia, diff automatyczny osobno.
- **Ryzyko:** stan bazowy i SLO, kryteria zatrzymania przed wdrożeniem, przećwiczone wycofanie z danymi, znany decydent.

---

## Podsumowanie - najważniejsze wnioski

- Kod, testy, narzędzia, przegląd i wdrożenie tworzą **jeden system bezpieczeństwa** - żaden element sam nie wystarcza.
- Refaktoryzacja, migracja i zmiana zachowania wymagają **innych dowodów**; big bang odbiera wczesną naukę.
- Boy Scout Rule to małe, lokalne, weryfikowalne ulepszenia.
- Jedna główna intencja ułatwia przegląd, wycofanie i diagnozę.
- Dokument dobieraj do odbiorcy i trwałości; narzędzia mają granice widoczności.
- Migracja kończy się dopiero po **usunięciu starej ścieżki** i architektury przejściowej.
