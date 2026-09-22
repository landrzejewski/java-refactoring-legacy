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

## Obserwowalne zachowanie

- Zgodność nie kończy się na wartości zwracanej - obejmuje też typ, komunikat i moment wyjątku oraz skutki uboczne i ich kolejność.
- Liczą się stan trwały, właściwości transakcyjne, idempotencja i kolejność komunikatów, a także zgodność źródłowa, binarna i protokołowa.
- Opóźnienie, przepustowość i zużycie zasobów są częścią kontraktu, jeśli klienci na nich polegają; podobnie telemetria, audyt i bezpieczeństwo.
- Nie każdy przypadkowy szczegół starego systemu trzeba utrwalać - ale zespół musi świadomie zdecydować, co zachowuje, co naprawia, a co usuwa.

---

## Krótka pętla informacji zwrotnej

1. Określ cel i granicę zmiany; zapisz istotny kontrakt i stan bazowy.
2. Dodaj brakujące testy charakterystyki lub kontraktowe.
3. Wykonaj jeden mały, odwracalny krok; skompiluj, uruchom zawężone testy i obejrzyj diff.
4. Włącz szerszą weryfikację i analizę statyczną, potem zintegruj z główną linią.
5. Obserwuj zachowanie po wdrożeniu i usuń elementy przejściowe po spełnieniu jawnych kryteriów.

Mały krok to nie arbitralny limit linii, lecz **jedna spójna intencja** z testami, pozostawiająca system w działającym stanie.

---

<!-- _class: lead -->
# 1. Stopniowa refaktoryzacja kontra przepisanie systemu

---

## 1.1. Domyślny kierunek

- Dla dużego systemu z rzeczywistym ruchem bezpieczniejszym punktem wyjścia jest zwykle praca przyrostowa - wcześnie ujawnia nieznane reguły, integracje i charakterystykę operacyjną.
- Podejście przyrostowe pozwala przerwać lub zmienić kierunek, zanim zostanie wydany cały budżet.
- Pełne przepisanie nie usuwa złożoności domeny, tylko przenosi ją do nowego kodu - często bez wiedzy ukrytej w wyjątkach, danych i integracjach.
- W czasie budowy stary system nadal się zmienia, więc cel migracji jest ruchomy.
- Ograniczone przepisanie komponentu bywa racjonalne: mały zakres, znany kontrakt, nieliczne integracje, brak stanu (lub zaprojektowana migracja) i niezależna weryfikacja obu wersji.

---

## 1.2. Kryteria decyzji

| Pytanie | Za zmianą przyrostową | Za ograniczonym przepisaniem |
| --- | --- | --- |
| Zachowanie poznane? | liczne reguły niejawne, słabe testy | kompletny, mierzalny kontrakt |
| Zakres izolowany? | wiele integracji i wspólnych danych | wąska granica, niewielu klientów |
| Komponent ma stan? | skomplikowana migracja, silna spójność | brak stanu / sprawdzony plan migracji |
| Musi działać stale? | ruch krytyczny, brak okna serwisowego | proste, odwracalne przełączenie |
| Cel się zmienia? | aktywny rozwój starego systemu | stabilny zakres i kryteria akceptacji |
| Współistnienie? | dostępny router, adapter lub seam | zbędne przy małym zakresie |

Nowa technologia sama w sobie nie jest wynikiem biznesowym - decyzję uzasadniają mierzalne ograniczenia (wsparcie, bezpieczeństwo, koszt, niezawodność, czas zmian).

---

## 1.3. Branch by Abstraction

- Branch by Abstraction wprowadza wewnątrz aplikacji stabilny kontrakt między klientem a wymienianym dostawcą - nazwa nie oznacza gałęzi w VCS.
- Sekwencja: opisz potrzeby klienta → mała abstrakcja → stara implementacja za adapterem → wywołania przez abstrakcję → wspólne testy kontraktowe.
- Dalej: implementacja kandydująca → porównanie wyników → stopniowe przełączanie klientów → usunięcie starej ścieżki i rusztowania.
- Wprowadzenie abstrakcji i adaptera może być refaktoryzacją (zachowuje zachowanie); budowa nowego dostawcy i przełączenie to już **migracja**.

---

## 1.3. Strangler Fig i architektura przejściowa (1.4)

- Strangler Fig działa na granicy systemu lub funkcji biznesowej: brama, fasada, proxy lub router kieruje część operacji do legacy, a część do nowego komponentu.
- Branch by Abstraction wymienia dostawcę za kontraktem wewnątrz aplikacji; Strangler Fig przenosi funkcje między większymi granicami wykonawczymi.
- Adapter, router, translator, flaga i telemetria to uzasadniony koszt redukcji ryzyka - ale każdy element potrzebuje właściciela, celu, testów, monitoringu, kryterium zakończenia i terminu ponownej oceny.
- Bez tego rusztowanie migracyjne staje się kolejną trwałą warstwą legacy; procent przeniesionego ruchu nie kończy migracji - trzeba usunąć stare zależności, dane i przełączniki.

---

## 1.5. Przykład: równoległa weryfikacja kalkulatora (1/3)

- `PriceRequest` normalizuje pieniądze i stopę rabatu według jawnej polityki `HALF_EVEN`; walidacja poprzedza zaokrąglenie, więc mała wartość ujemna nie zamieni się w zero.
- Abstrakcja `PricingEngine` wyraża jedną potrzebę klienta; jej implementacje są wolne od efektów zewnętrznych - to warunek bezpiecznego trybu shadow.
- `LegacyPriceCalculator` oczekuje rabatu w procentach 0-100, a adapter tłumaczy kontrakt klienta na stare API, więc kod domenowy nie zna jednostki legacy.

```java
@FunctionalInterface
public interface PricingEngine {
    PriceQuote quote(PriceRequest request);  // czysta granica, bez I/O
}

public PriceQuote quote(PriceRequest request) {          // adapter legacy
    return new PriceQuote(calculator.calculate(
            request.unitPrice(), request.quantity(),
            request.discountRate().movePointRight(2)));
}
```

---

## 1.5. Przykład: równoległa weryfikacja kalkulatora (2/3)

- `CandidatePricingEngine` zachowuje moment zaokrąglenia rabatu - przy `HALF_EVEN` zaokrąglenie przed odejmowaniem nie zawsze równa się zaokrągleniu kwoty końcowej.
- Trzy stany migracji nazywa enum `MigrationMode { LEGACY, VERIFY, CANDIDATE }` - flaga boolean nie opisałaby osobno kontroli, weryfikacji i użycia kandydata.
- Wynik porównania to zamknięta hierarchia `sealed interface VerificationEvent`: `Agreement`, `Divergence`, `CandidateFailure` (typ i komunikat wyjątku, bez przekazania go klientowi).
- `MigratingPricingEngine` centralizuje wybór ścieżki - logika przełączania nie jest rozrzucona po kodzie domenowym.

---

## 1.5. Przykład: równoległa weryfikacja kalkulatora (3/3)

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

- Wspólny `PricingEngineContractTest` uruchamia te same przykłady dla adaptera legacy i kandydata; przypadek „połowy centa” chroni moment zaokrąglenia.

---

## 1.6. Granice trybu shadow

- `VERIFY` zachowuje wynik legacy przy przewidzianych awariach kandydata, ale nie maskuje `Error` z JVM i nie gwarantuje dostarczenia zdarzenia po awarii reportera.
- Synchroniczny kandydat może zwiększyć latencję lub wyczerpać zasoby - produkcyjny shadow wymaga izolacji, timeoutu z polityką anulowania, bulkheadu i limitu kopiowanego ruchu.
- Podwójne wykonanie jest bezpieczne głównie dla czystych obliczeń i izolowanych odczytów - nie powielaj płatności, wysyłek, zapisów do wspólnej bazy ani publikacji zdarzeń.
- Dla operacji ze skutkami ubocznymi porównuj decyzję przed wykonaniem efektu albo kieruj kandydata do odizolowanego środowiska.
- Zgodność dwóch implementacji nie dowodzi poprawności - obie mogą mieć ten sam błąd; potrzebne są niezależne przykłady domenowe.

---

## 1.7. Antywzorce migracji

- Wieloletnia gałąź bez częstej integracji oraz jednorazowy big bang bez wcześniejszych sygnałów produkcyjnych.
- Odtwarzanie każdej starej funkcji bez sprawdzenia, czy nadal jest potrzebna.
- Abstrakcja kopiująca całe API wymienianego frameworka oraz logika przełączania rozrzucona po kodzie domenowym.
- Flaga bez właściciela i terminu usunięcia; utrzymywanie starej ścieżki bez planu jej wyłączenia.
- Uznanie zgodności odpowiedzi API za dowód zgodności danych i transakcji.

---

<!-- _class: lead -->
# 2. Zasada Boy Scout

---

## 2.1. Heurystyka, nie mandat do dowolnych zmian

- Boy Scout Rule zachęca, by dotykany kod zostawiać w nieco lepszym stanie - to heurystyka ograniczania lokalnej entropii, nie miernik jakości ani licencja na przebudowę.
- Poprawa jest właściwa, gdy dotyczy kodu potrzebnego do bieżącej zmiany, jest mała, lokalna i łatwa do weryfikacji.
- Musi zachowywać obserwowalne zachowanie, nie zwiększać istotnie ryzyka konfliktów i nie zaciemniać głównej intencji zestawu zmian.
- Przykłady: lepsza nazwa, małe Extract Method, usunięcie lokalnej duplikacji, uproszczenie warunku.
- Zmiana publicznego API, aktualizacja zależności, migracja danych czy naprawa reguły biznesowej **nie są** drobnym sprzątaniem.

---

## 2.2. Przykład lokalnej poprawy - przed

- Wersja początkowa `ReleaseSummaryFormatter` ma nieczytelne nazwy, wielokrotną konkatenację i powielony format wiersza.
- Jej publiczny kontrakt (walidacja, komunikaty, kolejność kontroli) jest jednak jednoznaczny i nie powinien zostać przypadkowo zaostrzony.

```java
String s = "Release " + releaseId.strip() + "\n";
int n = 0;
for (DeploymentResult r : results) {
    Objects.requireNonNull(r, "result");
    if (r.status() == DeploymentStatus.SUCCESS) {
        s = s + "[OK] " + r.environment() + ": " + r.description() + "\n";
        n++;
    } else {
        s = s + "[ERROR] " + r.environment() + ": " + r.description() + "\n";
    }
}
return s + "Successful: " + n + "/" + results.size();
```

---

## 2.2. Przykład lokalnej poprawy - po

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

- Wydzielono walidację i format wiersza, nadano nazwy, użyto `StringBuilder` - bez nowej walidacji, bez zmiany sygnatury i kolejności kontroli.
- Test różnicowy porównuje obie wersje (także typy i komunikaty wyjątków), a niezależny orakl chroni przed sytuacją, w której obie są błędne.

---

## 2.3. Bezpieczna sekwencja i 2.4. typowe nadużycia

- Sekwencja: zielone testy → nazwij lokalną przeszkodę → jeden mały ruch strukturalny → kompilacja i zawężone testy → przegląd diffu → zmiana zachowania dopiero w osobnym kroku.
- Gdy zakres rośnie, odłóż resztę porządków do osobnego zadania; mała zmiana nazwy może zostać przy pracy, ale przeniesienia i formatowanie warto oddzielić.
- Nadużycia: nieograniczone poprawianie przy małym zadaniu i ukrywanie zmiany zachowania pod etykietą refaktoryzacji.
- Nadużycia: masowe formatowanie razem z naprawą, poprawianie cudzego modułu bez komunikacji, abstrakcje „na przyszłość”.
- Liczba zmienionych linii nie jest miarą pozostawienia kodu w lepszym stanie.

---

<!-- _class: lead -->
# 3. Praca zespołowa w refaktoryzacji

---

## 3.1. Wspólny obraz zmiany

- Refaktoryzacja współdzielonego kodu wymaga krótkiego uzgodnienia: jaki problem usuwa, gdzie jest granica zachowania i co jest poza zakresem.
- Zespół powinien znać serię planowanych kroków, właścicieli obszarów, osoby przeglądające oraz sposób weryfikacji.
- Potrzebny jest plan wdrożenia, obserwacji i wycofania oraz kryterium usunięcia elementów przejściowych.
- Mała zmiana lokalna - wystarczy opis zestawu zmian; migracja między zespołami - propozycja techniczna, zapis decyzji i uzgodniona odpowiedzialność. Dokument ma zmniejszać niepewność, nie spełniać rytuał.
- Programowanie w parze i praca grupowa pomagają w odkrywaniu zachowania i ryzykownych transformacjach, ale nie zastępują testów.

---

## 3.2. Małe zestawy zmian

- Dobry zestaw zmian ma jedną samodzielną intencję, zawiera powiązane testy i po integracji zostawia repozytorium w działającym stanie.
- Mała zmiana jest szybsza do zrozumienia, dokładniej przeglądana, mniej konfliktowa, prostsza do cofnięcia i lepiej wskazuje przyczynę regresji.
- Liczba linii to tylko przybliżenie: automatyczny rename w setkach miejsc ma jedną intencję, a kilkanaście linii łączących schemat, zachowanie i uprawnienia bywa trudniejsze.
- Przykładowa seria: testy charakteryzujące → refaktoryzacja tworząca seam → nowe zachowanie za seamem → konfiguracja wdrożenia etapowego → usunięcie starej ścieżki.
- Testy z pierwszego kroku też wymagają przeglądu - mogą utrwalić przypadkowe zachowanie.

---

## 3.3. Przygotowanie do przeglądu kodu

- Opis zmiany powinien odpowiadać na pięć pytań: dlaczego, co zmienia (a czego nie), jaki kontrakt zostaje, jakie są dowody i jak wdrożyć, obserwować oraz wycofać.
- Warto wskazać kolejność czytania: testy kontraktowe → nowa granica → implementacje → miejsce składania zależności → konfiguracja wdrożenia etapowego.
- Przegląd ocenia poprawność zachowania, projekt, testy i jakość orakli, nazwy i dokumentację oraz skutki dla danych, współbieżności, bezpieczeństwa i wydajności.
- Sprawdza też kompatybilność klientów, obserwowalność, możliwość wycofania i plan usunięcia kodu przejściowego.
- Celem nie jest abstrakcyjna perfekcja, lecz poprawa ogólnego stanu - ale nie akceptuje się nowej złożoności tylko dlatego, że podobna już istnieje w legacy.

---

## 3.4. Informacja zwrotna

- Komentarz dotyczy kodu i ryzyka, nie osoby.
- Najbardziej użyteczna forma zawiera obserwację, możliwy skutek, uzasadnienie oraz konkretną propozycję albo pytanie.
- Zespół może stosować etykiety `BLOCKER`, `SUGGESTION` i `NIT`, jeśli znaczenie poziomów jest wspólnie ustalone - etykieta nie zastępuje argumentu.
- Przy sporze o kierunek szybko przejdź do rozmowy synchronicznej, a wniosek zapisz w zestawie zmian lub zapisie decyzji.

---

## 3.5. Przykład polityki gotowości do przeglądu (1/2)

- Polityka oddziela intencję zmiany (`ChangeIntent`) od dowodu weryfikacji (`VerificationEvidence` z `EvidenceKind`); `ChangeSet` robi niemutowalne migawki kolekcji.
- Problemy gotowości są typowane (`ReadinessProblem`), więc klient nie musi parsować komunikatów dla człowieka.

---

## 3.5. Przykład polityki gotowości do przeglądu (2/2)

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

- To lokalna umowa zespołu, nie uniwersalny algorytm - jej wartością jest jawność powodów zatrzymania zmiany.

---

## 3.6. Czego nie należy łączyć

- Duża refaktoryzacja i zmiana funkcjonalna trafiają do osobnych zestawów zmian, by niezależnie ocenić równoważność strukturalną i poprawność nowej reguły.
- Wyjątkiem może być bardzo małe lokalne uporządkowanie, którego wydzielenie utrudniłoby zrozumienie.
- Oddzielaj masowe formatowanie od zmian logicznych oraz rename i przeniesienia od naprawy błędu.
- Oddzielaj automatycznie wygenerowaną transformację od ręcznych poprawek oraz zmianę schematu danych od przełączenia odczytów.
- Oddzielaj wprowadzenie flagi od zwiększania ekspozycji, a usunięcie starego kodu od wcześniejszego przełączenia ruchu.

---

<!-- _class: lead -->
# 4. Dokumentowanie zmian

---

## 4.1. Dokumentacja według trwałości

| Artefakt | Co powinien zawierać |
| --- | --- |
| nazwy i struktura kodu | bieżący model i intencję implementacji |
| komentarz | nieoczywiste uzasadnienie, ograniczenie, kontrakt zewnętrzny |
| test | wykonywalny przykład zachowania i granic |
| opis zestawu zmian | cel, zakres, dowody, ryzyko, wdrożenie i wycofanie |
| commit | jeden logiczny krok i jego powód |
| ADR | trwałą decyzję architektoniczną, kontekst i konsekwencje |
| instrukcja operacyjna | procedurę wdrożenia, diagnozy i odzyskania |

Kod opisuje stan obecny - komentarz „przeniesiono tę metodę” szybko traci wartość, bo historię ruchu przechowuje VCS; komentarz o wymaganiu regulatora zostaje.

---

## 4.2. Opis zestawu zmian

- Profesjonalny opis nie powtarza diffu - zawiera problem i oczekiwany rezultat, zakres oraz elementy świadomie pominięte.
- Wskazuje kontrakt zachowywany przez refaktoryzację, a ewentualną zmianę zachowania opisuje oddzielnie.
- Podaje dowody z testów i analizy oraz wpływ na dane, integracje i operacje.
- Opisuje sposób wdrożenia i obserwacji, warunki zatrzymania i wycofania oraz plan usunięcia flag, adapterów i starego kodu.
- „Testy przeszły” to za mało dla ryzykownej migracji - trzeba wskazać, jakie testy, jakie scenariusze chronią i jakie ryzyka zostają poza zakresem.

---

## 4.3. Architecture Decision Record

- ADR służy decyzjom o trwałym wpływie na architekturę - np. granicy migracji, własności danych czy strategii współistnienia; nie jest potrzebny dla każdego Extract Method czy Rename.
- Klasyczny lekki rdzeń ADR: tytuł, status, kontekst, decyzja, konsekwencje.
- Lokalny szablon modułu dodaje identyfikator, rozważone opcje i metodę weryfikacji - to wartościowe elementy procesu, ale nie uniwersalny wymóg formatu.
- Warto uczciwie opisać zarówno korzyści, jak i koszty decyzji.
- Po zmianie kierunku stary zapis oznacza się jako zastąpiony i tworzy nowy - historia rozumowania zostaje przy kodzie.

---

## 4.4. Przykład wykonywalnego modelu decyzji

- `DecisionId` wymusza lokalną konwencję `ADR-NNNN`; `DecisionStatus` obejmuje `PROPOSED`, `ACCEPTED`, `REJECTED`, `DEPRECATED`, `SUPERSEDED`.
- `DecisionRecord` wymaga co najmniej jednej rozważonej opcji i jednej konsekwencji (`POSITIVE`/`NEGATIVE`/`NEUTRAL`) oraz robi kopie defensywne list.
- `DecisionRecordMarkdownRenderer` deterministycznie generuje Markdown - wynik sprawdza test z text blockiem i można go trzymać w VCS.
- Generator nie gwarantuje jakości decyzji: wartość wynika z konkretnego kontekstu, uczciwych alternatyw i konsekwencji oraz późniejszej weryfikacji.

```java
consideredOptions = nonEmptyCopy(consideredOptions, "consideredOptions");
consequences = nonEmptyCopy(consequences, "consequences");
```

---

## 4.5. Dokumentacja żywa i historyczna

- Instrukcja operacyjna, diagram stanu obecnego i opis wdrożenia muszą być aktualizowane wraz z systemem.
- ADR i zamknięty opis zestawu zmian to zapis historyczny - mieszanie ról prowadzi do utraty historii albo instrukcji opisującej nieistniejący system.
- Właściciel architektury przejściowej regularnie sprawdza, czy opis routingu odpowiada konfiguracji produkcyjnej i czy kryteria usunięcia starej ścieżki zostały spełnione.
- Weryfikuje też, czy procedura wycofania nadal działa, czy dashboardy i alerty pokrywają obie implementacje i czy termin usunięcia flagi jest realny.

---

<!-- _class: lead -->
# 5. Narzędzia wspierające refaktoryzację

---

## 5.1. Różne narzędzia, różne dowody (1/2)

| Narzędzie | Główna rola | Czego samo nie dowodzi |
| --- | --- | --- |
| refaktoryzacja IDE | transformacja oparta na symbolach | zgodności klientów zewn. i użyć dynamicznych |
| kompilator | typy, składnia, ostrzeżenia | poprawności reguł biznesowych |
| formatter / linter | jednolity zapis, reguły źródła | dobrego projektu i semantyki |
| analiza statyczna | wzorce ryzyka | braku fałszywych wyników |

---

## 5.1. Różne narzędzia, różne dowody (2/2)

| Narzędzie | Główna rola | Czego samo nie dowodzi |
| --- | --- | --- |
| testy | zgodność dla wykonanych scenariuszy | zachowania poza zakresem testu |
| receptura automatyczna | powtarzalna transformacja wielu miejsc | każdej konsekwencji domenowej |
| przegląd kodu | intencja, projekt, ryzyko | matematycznego braku regresji |

Bezpieczna bramka łączy kilka niezależnych sygnałów - zielony wynik jednego narzędzia to nie certyfikat poprawności.

---

## 5.2. Refaktoryzacje IDE

- Rename, Move, Change Signature, Extract, Inline i Safe Delete korzystają z modelu symboli - są bezpieczniejsze niż szukaj-i-zamień, bo odróżniają deklaracje od użyć i sygnalizują konflikty.
- IDE może nie znać konsumentów skompilowanych poza repozytorium ani nazw zapisanych w konfiguracji.
- Nie widzi też użyć przez refleksję i pluginy, szablonów, skryptów i zapytań zależnych od nazw, nazw serializowanych do danych ani kontraktów innych języków.
- Przed zatwierdzeniem: obejrzyj podgląd transformacji, przeszukaj użycia tekstowe, skompiluj pełny projekt i uruchom testy integracyjne dla danej granicy.

---

## 5.3. Kompilator Javy 25

- `--release 25` ustala poziom języka, format klas i dostępne API - ale nie gwarantuje, że Maven działa na JDK 25; w CI kontroluj faktyczny JDK (np. toolchains).
- `-Xlint` włącza zalecane ostrzeżenia, a `-Werror` zamienia je w błędy - to silna bramka dla nowego kodu, lecz nagłe włączenie w dużym legacy zablokuje wszystkie zmiany.
- Rozsądne wdrożenie: zapisz stan bazowy, oceń kategorie ostrzeżeń, napraw problemy wysokiego ryzyka i dopuszczaj tylko ograniczone, uzasadnione wyłączenia.
- Następnie nie dopuszczaj nowych naruszeń i stopniowo zmniejszaj stan bazowy.
- Tłumienie wskazuje konkretną regułę w najmniejszym zakresie; rozróżnij false positive, regułę niedopasowaną do projektu i świadomie zaakceptowane ryzyko.

---

## 5.4. Analiza statyczna i formatowanie

- Checkstyle pilnuje konwencji źródła i nazw, PMD analizuje źródło i AST, a SpotBugs - kod bajtowy pod kątem prawdopodobnych błędów; pokrywają różne klasy problemów i nie są zamienne.
- Upewnij się, że wersja analizatora obsługuje składnię i kod bajtowy używanej Javy; niepełny `classpath` pogarsza dokładność.
- Konfigurację wyników wersjonuj, a wyłączenia dokumentuj blisko kodu lub w precyzyjnym filtrze.
- Formatter usuwa dyskusje o zapisie, ale formatowanie całego repozytorium razem z naprawą logiczną utrudnia przegląd i historię.
- Lepsza jest osobna zmiana mechaniczna albo zasada ratchet - blokowanie nowych naruszeń w nowych i dotykanych plikach.

---

## 5.5. Automatyzacja transformacji

- IDE dobrze obsługuje interaktywne zmiany w jednym repozytorium; do powtarzalnej migracji wielu miejsc lub repozytoriów warto użyć strukturalnej receptury, np. OpenRewrite.
- Transformacja oparta na składni i typach jest bezpieczniejsza od regexa, ale wymaga jawnych warunków wejściowych, przykładów `before`/`after` i przypadków, których nie wolno zmienić.
- Potrzebna jest kompletna ścieżka klas, przebieg próbny, przegląd wygenerowanej łatki oraz kompilacja i testy po zastosowaniu.
- Wygenerowany diff trzeba oddzielić od ręcznych poprawek.
- Automatyzacja zwiększa spójność i zasięg - ale też zasięg błędu receptury, dlatego jej testy są częścią produktu.

---

## 5.6. Przykład bramki kompilatora

- `InMemoryJavaCompiler` używa publicznego API `javax.tools`: `--release 25`, `-proc:none`, `-Xlint:rawtypes`, a przy polityce `TREAT_WARNINGS_AS_ERRORS` dokłada `-Werror`.
- Wynik przechowuje strukturalną diagnostykę (rodzaj, `Optional<String>` kod, pozycja) - lokalizowany komunikat nie staje się częścią kontraktu testów.
- Ten sam surowy typ `List` daje ostrzeżenie `compiler.warn.raw.class.use` albo - przy `-Werror` - błąd `compiler.err.warnings.and.werror`.

```java
private static List<String> compilerOptions(WarningPolicy warningPolicy) {
    var options = new ArrayList<>(BASE_OPTIONS); // --release 25, -proc:none, -Xlint:rawtypes
    if (warningPolicy == WarningPolicy.TREAT_WARNINGS_AS_ERRORS) {
        options.add("-Werror");
    }
    return List.copyOf(options);
}
```

Bramka demonstruje jedną własność - nie zastępuje Mavena, pełnej ścieżki klas, testów ani analizatorów.

---

## 5.7. Minimalna bramka jakości

1. Podgląd transformacji w IDE albo przebieg próbny receptury oraz przegląd diffu.
2. Czysta kompilacja na docelowym JDK z ostrzeżeniami kompilatora.
3. Testy zawężone do zmienionej granicy, potem pełny zestaw testów.
4. Skonfigurowane analizatory statyczne i przegląd wykonany przez człowieka.
5. Obserwacja kontrolowanego wdrożenia.

Kolejność daje najpierw szybki, a potem kosztowniejszy feedback. W CI wszystkie wymagane bramki pozostają obowiązkowe niezależnie od wyniku lokalnego.

---

<!-- _class: lead -->
# 6. Zarządzanie ryzykiem

---

## 6.1. Ryzyko jest właściwością zmiany i kontekstu

- Ta sama transformacja bywa niskim ryzykiem w narzędziu wewnętrznym i wysokim w ścieżce autoryzacji płatności.
- Oceniaj krytyczność i zasięg skutków, znajomość zachowania i jakość orakli oraz zgodność i odtwarzalność danych.
- Uwzględnij transakcje, kolejność, współbieżność, idempotencję, bezpieczeństwo, regulacje i kompatybilność integracji.
- Liczą się też wydajność, obserwowalność i czas detekcji, odwracalność kodu, konfiguracji i danych oraz kompetencje i dostępność zespołu.
- Macierz prawdopodobieństwa i wpływu porządkuje rozmowę, ale nie daje obiektywnej precyzji - najważniejsze są założenia, dowody, właściciel i mechanizmy kontroli.

---

## 6.2. Warstwy kontroli (1/2)

| Ryzyko | Przykładowe zabezpieczenia |
| --- | --- |
| nieznane zachowanie | testy charakterystyki, kontraktowe i różnicowe |
| szeroki zasięg skutków | mała kohorta, deterministyczny routing, wdrożenie etapowe |
| niezgodność wyników | shadow dla czystej operacji, normalizacja, rekoncyliacja |
| awaria kandydata | izolacja, timeout, wynik kontrolny z legacy |
| regresja operacyjna | stan bazowy, SLI, SLO, panel metryk, alert |

---

## 6.2. Warstwy kontroli (2/2)

| Ryzyko | Przykładowe zabezpieczenia |
| --- | --- |
| trudne wycofanie | zgodność wsteczna, przetestowany przełącznik |
| migracja danych | expand and contract, kopia, walidacja, źródło prawdy |
| kod przejściowy | właściciel, termin i kryterium usunięcia |

Zabezpieczenie musi odpowiadać mechanizmowi awarii - flaga nie cofnie danych zapisanych w formacie niezrozumiałym dla starej wersji.

---

## 6.3. Kryteria przed wdrożeniem etapowym

- Przed ekspozycją ruchu zdefiniuj populację kontrolną i kandydującą oraz minimalną reprezentatywną próbkę.
- Ustal metryki zgodności funkcjonalnej, bezwzględne SLO i porównanie ze stanem bazowym oraz czas obserwacji.
- Określ kryteria przejścia do kolejnego etapu, warunki zatrzymania oraz automatyczne i ręczne warunki wycofania.
- Wskaż właściciela decyzji i kanał eskalacji.
- Nie ma uniwersalnych progów: dla obliczeń finansowych dopuszczalna różnica może wynosić zero; progu nie dopasowuje się po zobaczeniu niekorzystnych wyników.

---

## 6.4. Przykład jawnej polityki wdrożenia etapowego (1/2)

- `RolloutThresholds` (min. próbka, maks. wskaźnik błędów, maks. p95) to dane konfiguracyjne; `RolloutSnapshot` nie pozwala na niemożliwe liczniki ani `NaN`.
- Kontrole bezpieczeństwa idą przed oceną rozmiaru próbki - mała próbka z potwierdzonym naruszeniem nie „czeka na więcej danych”.

---

## 6.4. Przykład jawnej polityki wdrożenia etapowego (2/2)

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

- `HOLD` to brak wystarczających danych, nie sukces ani porażka; `ROLLBACK` ma pierwszeństwo przy naruszeniu zgodności lub bezwzględnego SLO.

---

## 6.5. Dane i wycofanie

- Wycofanie `.jar` jest proste tylko wtedy, gdy nowa wersja nie zostawiła niezgodnego stanu - migracja danych powinna stosować expand and contract.
- Expand and contract: dodaj strukturę zgodną z obiema wersjami → wdróż kod działający w okresie przejściowym → przenieś dane z kontrolą postępu.
- Dalej: przełącz odczyty i zweryfikuj zgodność → zatrzymaj stare zapisy → usuń stare pola lub tabele dopiero po zamknięciu okna wycofania.
- Dual write wymaga jawnej strategii dla częściowych awarii, ponowień, kolejności i rekoncyliacji - sam zapis do dwóch systemów nie daje spójności.
- Próba wycofania sprawdza czas powrotu, zgodność danych, żądania w toku, brak powtórzenia nieidempotentnych efektów i dostępność dashboardów oraz eskalacji.

---

## 6.6. Zamknięcie migracji

- Migracja kończy się po usunięciu kosztu legacy, a nie po pierwszym sukcesie nowej ścieżki.
- Kryteria: pełny ruch na nowej implementacji przez uzgodniony okres, brak niewyjaśnionych rozbieżności, dotrzymane SLO i budżet kosztowy.
- Dodatkowo: zakończona migracja i rekoncyliacja danych oraz potwierdzona gotowość operacyjna.
- Trzeba usunąć stary kod, flagi, adaptery i dashboardy przejściowe, zaktualizować dokumentację i zapisać wnioski z incydentów.
- Procent przepisanych linii nie mierzy wartości - lepsze sygnały to przeniesione funkcje biznesowe, usunięte zależności, wyłączone koszty i krótszy czas bezpiecznej zmiany.

---

## Ćwiczenie: Warsztat 1 - plan przyrostowej wymiany implementacji

**Cel:** zaplanować migrację kalkulatora na krytycznej ścieżce bez jednorazowego przełączenia.

- Zapisz funkcjonalny i operacyjny kontrakt starej implementacji oraz wskaż najmniejszą abstrakcję potrzebną klientowi.
- Zaprojektuj adapter i testy kontraktowe; oceń, czy tryb shadow jest bezpieczny.
- Zdefiniuj kohorty i etapy przełączenia, kryteria `ADVANCE` / `HOLD` / `ROLLBACK` i warunki usunięcia starej ścieżki.
- **Akceptacja:** plan odróżnia refaktoryzację od migracji i zmiany zachowania, kandydat nie powiela nieidempotentnych efektów, obie implementacje przechodzą wspólny test kontraktowy.
- **Akceptacja:** routing deterministyczny i obserwowalny, wycofanie obejmuje kod, konfigurację i dane, każdy element przejściowy ma właściciela i kryterium usunięcia.

---

## Ćwiczenie: Warsztat 2 - seria zmian gotowych do przeglądu

**Cel:** podzielić szeroką zmianę legacy na samodzielne jednostki o czytelnej intencji.

- Oddziel odkrycie zachowania od przebudowy struktury oraz zmianę zachowania od refaktoryzacji.
- Umieść testy w odpowiednich zestawach zmian i zapewnij zielony build po każdym kroku.
- Przygotuj opis zakresu, dowodów i ryzyka oraz kolejność czytania dla osoby przeglądającej.
- **Akceptacja:** każdy zestaw ma jedną główną intencję, system działa po każdym kroku, testy nie utrwalają bezkrytycznie przypadkowego zachowania.
- **Akceptacja:** zmiany mechaniczne i ręczne rozdzielone, opis pozwala odtworzyć weryfikację, komentarze odróżniają blokadę od sugestii.

---

## Ćwiczenie: Warsztat 3 - bramki w projekcie legacy

**Cel:** poprawić automatyczną informację zwrotną bez blokowania zespołu historycznymi naruszeniami.

- Uruchom ostrzeżenia kompilatora i analizę statyczną bez modyfikowania kodu; sklasyfikuj wyniki według ryzyka i pewności.
- Zapisz stan bazowy naruszeń i włącz bramkę dla nowego oraz dotykanego kodu.
- Ustal format precyzyjnego tłumienia, termin przeglądu wyjątków i plan zmniejszania stanu bazowego.
- **Akceptacja:** narzędzia obsługują Javę 25, analiza ma kompletną ścieżkę klas, build nie generuje niezwiązanych automatycznych poprawek.
- **Akceptacja:** false positive odróżniony od zaakceptowanego ryzyka, nowe naruszenia nie powiększają stanu bazowego, raport w CI ma właściciela.

---

## Lista kontrolna (1/2)

- **Strategia:** czy cel modernizacji jest mierzalny, rodzaj zmiany poprawnie nazwany, a granicę pierwszego przyrostu da się zmniejszyć? Czy obie ścieżki mogą bezpiecznie współistnieć, a architektura przejściowa ma kryterium usunięcia?
- **Zespół i przegląd:** czy zestaw zmian ma jedną intencję i zachowuje działający build? Czy testy są wiarygodnym dowodem, a nie tylko pokryciem linii?
- **Zespół i przegląd:** czy opis zawiera zakres, elementy poza zakresem, wdrożenie etapowe i wycofanie, a właściciele zostali włączeni wcześnie?
- **Dokumentacja:** czy informacja trafiła do artefaktu o właściwej trwałości, a komentarze wyjaśniają przyczynę, nie historię edycji?
- **Dokumentacja:** czy decyzja zawiera alternatywy i konsekwencje, dokumentacja operacyjna opisuje stan obecny, a decyzja zastąpiona pozostaje dostępna?

---

## Lista kontrolna (2/2)

- **Narzędzia:** czy IDE widzi wszystkie statyczne użycia i sprawdzono użycia dynamiczne? Czy kompilacja faktycznie używa JDK 25?
- **Narzędzia:** czy analizator obsługuje używaną składnię i kod bajtowy, wyłączenia są wąskie i uzasadnione, a automatyczny diff oddzielony od ręcznego?
- **Ryzyko:** czy ustalono stan bazowy, SLI i bezwzględne SLO, a próbka jest reprezentatywna?
- **Ryzyko:** czy kryteria zatrzymania powstały przed wdrożeniem etapowym, a wycofanie przećwiczono z uwzględnieniem danych?
- **Ryzyko:** czy zespół wie, kto podejmuje decyzję podczas incydentu?

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja legacy to zdobywanie informacji w kontrolowanych krokach; kod, testy, narzędzia, przegląd, dokumentacja i wdrożenie tworzą jeden system bezpieczeństwa - żaden element sam nie gwarantuje poprawności.
- Refaktoryzacja, migracja i zmiana zachowania wymagają innych dowodów; małe przepisanie komponentu bywa rozsądne, ale big bang odbiera możliwość wczesnej nauki.
- Boy Scout Rule to małe, lokalne i weryfikowalne ulepszenia; jedna główna intencja ułatwia przegląd, wycofanie i diagnozę.
- Dokument dobieraj do odbiorcy i trwałości informacji; narzędzia semantyczne zmniejszają ryzyko, ale mają granice widoczności.
- Progi wdrożenia etapowego wynikają z kontraktu i SLO, a migracja kończy się dopiero po usunięciu starego kosztu i architektury przejściowej.
