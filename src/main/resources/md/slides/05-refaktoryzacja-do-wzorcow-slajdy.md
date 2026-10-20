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
# Moduł 5. Refaktoryzacja do wzorców

Kiedy prowadzić kod do wzorca, kiedy od niego odejść i jak zrobić to bezpiecznie, małymi krokami

---

## Agenda (1/2)

1. Czy zawsze warto?
2. Kiedy warto pozbyć się wzorców?
3. Niebezpieczeństwa refaktoryzacji
4. Wsparcie IDE
5. Refaktoryzacja a testowanie
6. Command, Strategy czy State?
7. Strategy czy Template Method?
8. Chain of Responsibility czy Decorator?
9. Adapter i Boundary Object

---

## Agenda (2/2)

10. Fabryka jako sposób kontroli spójności i enkapsulacji
11. Facade jako sposób kontroli spójności i enkapsulacji
12. Facade a Anti-Corruption Layer
13. Builder, 14. Observer, 15. Mediator, 16. Visitor
17. Pozostałe refaktoryzacje do wzorców (przegląd)
18. Ćwiczenia, mapa decyzji, lista kontrolna

Moduł 4 przedstawił katalog GoF, moduł 3 - sieć bezpieczeństwa. Tu nie powtarzamy definicji: interesuje nas **kierunek** (do wzorca, od wzorca) i **wybór** między podobnymi wzorcami.

---

## Organizacja modułu (ok. 6 h)

| Blok | Treść | Czas |
| --- | --- | --- |
| Wykład 1 | sekcje 1-5: decyzja, usuwanie wzorców, ryzyka, IDE, testy | 50 min |
| Ćwiczenie 1 | usunięcie zbędnego wzorca | 30 min |
| Wykład 2 | sekcje 6-8: Command, Strategy, State, Template Method, Chain, Decorator | 40 min |
| Ćwiczenie 2 | Command, Strategy czy State | 60 min |
| Ćwiczenie 3 | Template Method → Strategy, Chain czy Decorator | 60 min |
| Wykład 3 | sekcje 9-12: granice | 35 min |
| Ćwiczenie 4 | Adapter, Boundary Object, Facade, ACL | 60 min |
| Wykład 4 | sekcje 13-17 i mapa decyzji | 25 min |

---

## 1.1. Wzorzec jest rezultatem, nie celem

- Wzorzec opisuje problem, kontekst, siły i konsekwencje - nie jest kodem do skopiowania.
- Kierunek pracy: zabezpiecz zachowanie → nazwij oś zmienności → odsłoń role → wprowadź **najwęższy** kontrakt.
- Przenoś po jednym wariancie z testami i **zatrzymaj się**, gdy problem jest rozwiązany - pełna postać z katalogu nie jest celem.
- Przed decyzją: znany problem, warianty istniejące **dziś**, prostsza alternatywa, warunek, kiedy wzorzec przestanie być potrzebny.

---

## 1.2. Najpierw rodzaj zmienności

| Pytanie | Typowa technika |
| --- | --- |
| Czy zmienia się algorytm wybierany dla operacji? | Strategy |
| Czy zachowanie należy do trwałego rodzaju obiektu? | polimorfizm podtypów |
| Czy zachowanie zależy od bieżącego stanu i przejść? | State |
| Czy żądanie trzeba kolejkować, logować lub cofać? | Command |
| Czy reguły sprawdzamy po kolei, a obsługuje jedna? | Chain of Responsibility |
| Czy dodatek opakowuje rdzeń o tym samym kontrakcie? | Decorator |
| Czy kod jedynie tworzy właściwy obiekt? | Factory |

Ten sam `switch` może sygnalizować różne problemy - ustal, **co naprawdę jest zmienne** i czy zestaw wariantów jest otwarty, czy zamknięty.

---

## 1.3. Refaktoryzacja a zmiana projektu

- Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy - wprowadzenie wzorca tego nie zmienia.
- Kusi: nowe warianty, inna obsługa nieznanego kodu, asynchroniczność, kontynuacja po błędzie listenera.
- Podobnie: dowolna liczba odbiorców, cache i współdzielenie instancji, nowy format trwałych danych.
- Każdy taki krok to **rozszerzenie zachowania** - oddziel go od transformacji strukturalnej i zatwierdź osobno.

---

## 1.4. Kiedy wzorzec się nie opłaca

| Sygnał | Lepszy ruch |
| --- | --- |
| dwa stabilne warianty w jednym miejscu | czytelny `switch` lub `if` |
| wariant „na przyszłość”, bez wymagania | YAGNI - poczekaj na drugi przypadek |
| jeden klient, jedna implementacja | konkretna klasa, interfejs później |
| problem leży w nazwach lub długości metody | Rename, Extract Method |
| zespół nie zna wzorca, kod jest krytyczny | prostsza struktura i dobry test |

Koszt wzorca: więcej typów, pośredniość, konfiguracja w korzeniu kompozycji, trudniejsze śledzenie przepływu. Zysk musi być **widoczny w najbliższych zmianach**.

---

## 2.1. Refaktoryzacja od wzorca

Refaktoryzacja **od** wzorca jest równie poprawna jak **do** wzorca. Sygnały nadmiaru:

- jedna implementacja od dawna lub interfejs powtarzający API jednej klasy,
- fabryka z jednym konstruktorem, konfiguracja trudniejsza od `new`,
- większość implementacji rzuca `UnsupportedOperationException`,
- nowy wariant nadal wymaga zmian w wielu centralnych miejscach,
- pośrednik, który tylko przekazuje wywołania (Middle Man),
- globalna instancja, która ukrywa zależność i łączy testy (Singleton).

---

## 2.2. Kierunki odwrotne

| Nadmiar | Refaktoryzacja od wzorca |
| --- | --- |
| pośrednik przekazujący 1:1 | Remove Middle Man (odwrotność Hide Delegate) |
| globalny, mutowalny Singleton | Inline Singleton |
| podklasa bez stanu i zachowania | Collapse Hierarchy |
| Strategy z jedną, stałą implementacją | Inline Class, wchłonięcie reguły |
| fabryka z jednym konstruktorem | Inline Method, zwykłe `new` |
| Decorator, który niczego nie dodaje | usunięcie warstwy |
| Visitor przy częstych nowych węzłach | metody polimorficzne, `switch` po `sealed` |

Usunięcie wzorca to też refaktoryzacja: ten sam test, te same małe kroki. CineLegacy s21: Strategy z jedną implementacją po wygaśnięciu wariantu.

---

## 2.3. Remove Middle Man

```java
// przed: ReleaseDashboard → ReleaseService.statusOf → registry.statusOf
public DeploymentStatus statusOf(String deploymentId) {
    return registry.statusOf(deploymentId);          // ReleaseService tylko przekazuje
}
// po: dashboard rozmawia z rejestrem bezpośrednio
public ReleaseDashboard(DeploymentRegistry registry) {
    this.registry = Objects.requireNonNull(registry, "registry must not be null");
}
```

- Najpierw potwierdź, że pośrednik nie realizuje autoryzacji, transakcji, telemetrii, retry ani translacji błędów.
- Migruj jednego klienta naraz; stare metody jako forwardery w okresie zgodności.
- CineLegacy s22: `CinemaFacade.freeSeats` po cichu tłumaczy brak seansu na `0` - to nie jest czysty pośrednik.

---

## 2.4. Inline Singleton - przed i po

```java
// przed: ukryta zależność od globalnego, mutowalnego stanu
LegacyFeatureFlags flags = LegacyFeatureFlags.getInstance();
String stages = flags.isEnabled("canary") ? "10% -> 50% -> 100%" : "100%";

// po: niezmienna wartość przekazana jawnie
public record FeatureFlags(Set<String> enabled) {
    public FeatureFlags { enabled = Set.copyOf(enabled); }
}
public RolloutService(FeatureFlags flags) {
    this.flags = Objects.requireNonNull(flags, "flags");
}
// korzeń kompozycji: RolloutCompositionRoot.create("canary,dark-launch")
```

„Jedna instancja” przestaje być własnością klasy - staje się **decyzją korzenia kompozycji**.

---

## 2.5. Inline Singleton - procedura i ryzyka

- Procedura: parametr konstruktora (przejściowo z domyślnym `getInstance()`) → migracja klientów pojedynczo → tworzenie w korzeniu kompozycji → usunięcie `getInstance()` i `reset()`.
- Stary test wymaga `reset()` i `@ResourceLock` - dowód, że globalny stan łączy testy.
- Mutowalny singleton → niezmienna wartość to **zmiana kontraktu**: `enable()` w trakcie działania przestaje działać. Ustal, czy ktoś przełączał flagi w locie.
- Sprawdź wszystkie wywołania `getInstance()`, także z refleksji, skryptów i innych modułów.
- Potrzebna zmienność w locie → jawny port źródła flag, nie globalny obiekt.

---

## 3.1. Refaktoryzacja a zmiana kontraktu

- Refaktoryzacja zachowuje **obserwowalne zachowanie** w przyjętej granicy - nie strukturę klas ani prywatne metody.
- Kryterium: klient nie dostrzega żadnej nieuzgodnionej różnicy.
- Nowa walidacja, inny typ wyjątku, kopia defensywna, ukrycie publicznej klasy - to **zmiany kontraktu**.
- Takie kroki wykonuj i zatwierdzaj **osobno** od ruchów strukturalnych.
- Pełny wektor obserwowalnego zachowania - moduł 3, sekcja 4.6. CineLegacy s23: `double` → `BigDecimal` „przy okazji” zmienia zaokrąglenie i format zera.

---

## 3.2. Co trzeba zachować przy wprowadzaniu wzorca

- Nie tylko wynik: typ wyjątku (i komunikat, jeśli jest kontraktem), stan po sukcesie i po błędzie.
- Liczba i kolejność efektów ubocznych, moment wyboru wariantu i odczytu konfiguracji.
- W callbackach: wątek, kolejność odbiorców, polityka błędów, tożsamość przekazanego obiektu.
- `null`, wartość nieznana, gałąź domyślna; atomowość przejść i używany monitor.
- Zgodność danych, refleksji i publicznego API.

---

## 3.3. Ukryte przesunięcia w przykładach modułu

| Przykład | Co się przesuwa |
| --- | --- |
| Strategy (`DeploymentCostCalculator`) | błąd `null` z wywołania do konstrukcji; wybór zamrożony |
| Command (`DeploymentCommandDispatcher`) | brak komendy wykryty przy składaniu aplikacji |
| Boundary Object (`BuildReport`) | `NumberFormatException` w domenie → błąd na granicy |
| ACL (`CmdbTranslator`) | nieznany kod środowiska odrzucany zamiast pomijany |
| Inline Singleton (`FeatureFlags`) | flagi niezmienne po starcie |
| Facade (`ReleasePublishingFacade`) | komunikat dla `null` środowiska |

Każde takie przesunięcie: **nazwij, zatwierdź, opisz** - albo cofnij.

---

## 3.4. Skutki strukturalne wzorców

| Transformacja | Typowe ryzyko dla kontraktu |
| --- | --- |
| podtypy zamiast kodu typu | nowe typy runtime, `switch` po `sealed`, ORM, serializacja |
| delegacja zamiast dziedziczenia | utrata przypisywalności, hooków, monitora, `equals` |
| opakowanie (Decorator, Adapter) | `getClass`, tożsamość, `synchronized`, serializacja |
| ukrycie konstruktorów (Factory) | klienci zewnętrzni, refleksja, DI, deserializatory |
| zawężenie widoczności (Facade) | klienci spoza pakietu, testy, frameworki |
| nowy nadtyp lub interfejs | deskryptory metod, metody domyślne, zgodność binarna |

---

## 3.5. Zgodność, refleksja, serializacja, ORM i DI

- Biblioteka: klient skompilowany przeciw `v1` uruchomiony z `v2` - pełny build sprawdza głównie zgodność **źródłową**.
- Przeniesienie metody zmienia `getDeclaredMethods()` i `getDeclaringClass()`; `@Inherited` działa tylko dla adnotacji klas z nadklasy.
- Serializacja Javy: osobna postać dla każdego poziomu hierarchii - testuj historyczny plik `.ser` z nową wersją.
- ORM: hierarchia encji to migracja modelu (dyskryminator, tabele, proxy) - test z prawdziwym dostawcą i `flush`.
- DI: nazwy, konstruktory, adnotacje, proxy - uruchom kontekst aplikacji, nie tylko `new`.

---

## 4.1. Co daje IDE, a czego nie dowodzi

- Rename, Move, Extract, Inline, Change Signature i Safe Delete działają na **modelu symboli** - bezpieczniej niż szukaj-i-zamień.
- IDE nie widzi refleksji, konfiguracji, szablonów, nazw serializowanych (JSON, kolumny, nagłówki CSV) ani konsumentów spoza repozytorium.
- Automat nie zna semantyki: nie wie, czy dwa podobne fragmenty znaczą to samo.
- Jeden ruch automatu = kompilacja, testy, commit; ręczne poprawki w osobnym kroku.
- Receptury (np. OpenRewrite) zwiększają zasięg zmiany - także **zasięg błędu**.

---

## 4.2. Rename - mechanika i granice

| Słaba nazwa | Lepszy kierunek |
| --- | --- |
| `calc2` | `distributorReport` |
| `process()` | `createRentalQuote()` |
| `manager` | konkretna odpowiedzialność |
| `l` | `lines` |

- Nazwa opisuje **rolę w kontekście**, nie typ ani implementację.
- Nazwa żyje poza Javą: refleksja, konfiguracja, JSON/SQL/ORM, szablony, serializacja.
- Publiczne API: stara metoda delegująca, oznaczona jako przestarzała, nie tylko operacja IDE.
- CineLegacy s24: `calc2` w konfiguracji `ReportJob`, komponenty `t`, `n`, `d` w nagłówku CSV.

---

## 4.3. Extract Method - analiza przepływu danych

| Sytuacja | Typowe rozwiązanie |
| --- | --- |
| wartość tylko odczytywana | parametr lub spójny obiekt |
| wartość powstaje i jest używana później | wartość zwracana |
| modyfikowanych kilka wartości lokalnych | podziel fragment lub obiekt wyniku |
| używa wielu pól innego obiektu | możliwy sygnał Move Method |
| lista parametrów nadmiernie rośnie | zła granica lub brak spójnego pojęcia |

Extract Method to zwykle **pierwszy krok** do każdego wzorca: gałąź `switch` staje się metodą, potem klasą stanu, strategii lub komendy.

---

## 4.4. Extract Method - mechanika i trudne przypadki

- **Mechanika:** najmniejszy fragment → nazwa → parametry i wynik → kopia bez upraszczania → wywołanie → test.
- Uwaga na `return`/`break`/`continue`, `try-with-resources`, `catch`/`finally`, `synchronized`.
- Także `this`/`super`, metody przesłaniane, lambdy, przeciążenia, efekty w getterach.
- Nie wolno przesunąć efektu przed warunek, zmienić liczby wywołań ani wydłużyć trzymania blokady.
- CineLegacy s25: bloki opisane komentarzami; pętla liczy jednocześnie sumę i liczbę miejsc VIP (dwie wartości wyjściowe).

---

## 4.5. Ruchy IDE w drodze do wzorca

| Cel | Typowa sekwencja automatów |
| --- | --- |
| Strategy | Extract Method → Extract Interface → Introduce Parameter → Move Method |
| State | Extract Method dla gałęzi → Move Method do klas stanu |
| Template Method | Extract Method w podklasach → Change Signature → Pull Members Up |
| Template Method → Strategy | Replace Inheritance with Delegation → Extract Interface |
| Factory | Replace Constructor with Factory Method → Move → zawężenie widoczności |
| Facade | Extract Method w kliencie → Move Method do fasady → Safe Delete kopii |
| od wzorca | Inline Method, Remove Middle Man, Inline Class, Introduce Parameter |

Nazwy według IntelliJ IDEA; Rider i VS Code mają odpowiedniki o podobnym zakresie.

---

## 5.1. Bezpieczna pętla pracy

1. Nazwij problem i granicę obserwowalnego zachowania.
2. Dodaj test charakterystyki dla każdej gałęzi i każdej awarii.
3. Wprowadź minimalny kontrakt, początkowo delegujący do starego kodu.
4. Przenoś jedną gałąź lub efekt naraz, testy po każdym ruchu.
5. Stary warunek usuń po migracji wszystkich klientów; nadmiar wzorca usuń w osobnym kroku.

Pierwszy commit ma być **mały i odwracalny** - nie zaczynamy od tworzenia wszystkich klas wzorca.

---

## 5.2. Test równoważności przed i po

```java
@ParameterizedTest
@EnumSource(CustomerTier.class)
void strategyAxisPreservesEveryTierCost(CustomerTier tier) {
    var before = new LegacyReleaseConsole(tier);
    var after = ReleaseConsole.forTier(tier);
    for (int hours : new int[] {0, 1, 10, 33, 100}) {
        assertEquals(before.supportCost(hours), after.supportCost(hours));
    }
    assertEquals(
        assertThrows(IllegalArgumentException.class, () -> before.supportCost(-1)).getMessage(),
        assertThrows(IllegalArgumentException.class, () -> after.supportCost(-1)).getMessage());
}
```

- Obie wersje żyją obok siebie do końca migracji; test porównuje wynik **i** awarię.
- Dodaj niezależne oczekiwanie (np. dosłowny log) - test różnicowy nie wykryje błędu obecnego w obu wersjach.

---

## 5.3. Co porównuje test równoważności

| Przykład | Porównywany wektor |
| --- | --- |
| `variability` | koszt, log poleceń, cofanie, stan i komunikat po błędzie |
| `templatetostrategy` | cztery podklasy = cztery złożenia strategii |
| `chain` | 54 kombinacje rozmiaru, ryzyka i środowiska na granicach progów |
| `facade` | lista efektów i ich kolejność; brak efektu przy błędzie |
| `anticorruptionlayer` | decyzje planowania dla tych samych rekordów |
| `mediator` | migawka formularza po każdym z 12 zdarzeń |

Dobór danych: **granice** warunków (50/51, 500/501), zdarzenia ignorowane, awarie w środku sekwencji.

---

## 5.4. Testy struktury i ich koszt

- Część celu wzorca to **struktura**: `final` eksporter, pakietowo-prywatne podsystemy fasady, komponenty znające tylko mediator.
- Test refleksyjny utrwala taką decyzję (`subsystemsAreHiddenBehindTheFacade`, `componentsKnowOnlyTheMediator`).
- Jest kruchy - stosuj go dla reguł, które naprawdę są kontraktem architektury; resztę zostaw przeglądowi.
- Kontrolna mutacja: przestaw ogniwa łańcucha albo usuń `approverPicker.clear()` w mediatorze i sprawdź, który test to wykryje.
- `@ResourceLock("LegacyFeatureFlags")` w teście to dowód zapachu, który usuwa Inline Singleton.

---

## 6.1. Kto wybiera wariant i kiedy?

| | Strategy | Command | State |
| --- | --- | --- | --- |
| kto wybiera | klient, raz | nadawca, przy każdym żądaniu | nikt z zewnątrz |
| kiedy się zmienia | przy konfiguracji | każde wywołanie | po przejściu stanu |
| co jest obiektem | algorytm | żądanie (log, kolejka, `undo`) | stan i przejścia |
| typowy zapach | `switch` po trybie w każdym wywołaniu | `switch` po nazwie w kilku miejscach | `if (status == ...)` w każdej operacji |
| w module | `SupportCostPolicy` | `OperatorCommand` | `ReleaseLifecycle` |

Pytanie kontrolne zamiast nazwy wzorca: **kto zmienia wariant i kiedy?**

---

## 6.2. Punkt wyjścia: trzy osie w jednej klasie

```java
public long supportCost(int hours) {                 // (a) taryfa klienta
    return switch (tier) {
        case STANDARD -> hours * 100L;
        case PREMIUM -> 500 + hours * 80L;
        case ENTERPRISE -> Math.max(2_000, hours * 60L);
    };
}
public void undoLast() {                             // (b) polecenie operatora
    String action = history.pop();
    switch (action) { case "scale-up" -> instances--; /* ... */ }
}
public void advance() {                              // (c) cykl życia
    status = switch (status) { case PLANNED -> ReleaseStatus.DEPLOYING; /* ... */ };
}
```

`LegacyReleaseConsole`: to samo narzędzie (`switch`), trzy różne przyczyny zmiany.

---

## 6.3. Po: jeden mechanizm na każdą oś

```java
@FunctionalInterface
public interface SupportCostPolicy {                 // Strategy
    SupportCostPolicy PREMIUM = hours -> 500 + hours * 80L;
    long cost(int hours);
}
public sealed interface OperatorCommand {            // Command
    void execute(Cluster cluster);
    void undo(Cluster cluster);
}
public final class ReleaseLifecycle {                // State
    private State state = State.PLANNED;
    public void advance() { state = state.advance(); }
}
```

- `ReleaseConsole` składa trzy mechanizmy; walidacja `hours < 0` zostaje w kontekście.
- Wykonanie i cofnięcie są obok siebie w jednej klasie polecenia, a nie w dwóch `switch`.

---

## 6.4. Strategy - intencja, sekwencja, ryzyka

- Kandydat: kilka wariantów tego samego obliczenia, wybieranych **niezależnie od klasy obiektu**.
- Samo `if` nie uzasadnia Strategy - dla prostego, stabilnego warunku interfejs pogarsza czytelność.
- Sekwencja: tabela decyzji (`null`, nieznany, przepełnienie) → interfejs ze strategią przejściową → gałęzie do strategii → wybór w korzeniu kompozycji lub fabryce.
- Wybór w konstruktorze **zamraża** decyzję - nie jest równoważny odczytowi konfiguracji przy każdym wywołaniu.
- `Function`/`ToLongFunction` nie deklarują wyjątków kontrolowanych - wtedy własny interfejs z `throws`.

---

## 6.5. State - tabela przejść przed hierarchią

| Stan | advance | rollback | banner |
| --- | --- | --- | --- |
| PLANNED | DEPLOYING | wyjątek | release planned |
| DEPLOYING | LIVE | ROLLED_BACK | deployment in progress |
| LIVE | wyjątek | ROLLED_BACK | release is live |
| ROLLED_BACK | wyjątek | wyjątek | release rolled back |

- Tabela jest specyfikacją testu; zapisz ją **przed** hierarchią stanów.
- Domyślna metoda stanu rzuca wyjątek, nie jest pusta; stan po niedozwolonej operacji się nie zmienia.
- Stany to bezstanowe stałe enumu - dane konkretnego obiektu zostają w kontekście.

---

## 6.6. State - kolejność przejścia i efektów

- Kontrakt efektów: efekt, potem stan; stan, potem efekt; outbox; albo rozdzielenie stanu i próby efektu.
- Żaden wariant nie jest uniwersalny - refaktoryzacja zachowuje dotychczasowy moment zmiany stanu.
- `volatile` daje widoczność referencji, nie atomowość sekwencji sprawdzenie-efekt-zapis.
- Funkcja w `updateAndGet` może wykonać się wielokrotnie - bez nieidempotentnych efektów.
- Przy dwóch stanach `switch` bywa czytelniejszy; kilka osi stanu grozi eksplozją klas.

---

## 6.7. Command - rejestr i ograniczenia

```java
public DeploymentCommandDispatcher(Map<DeploymentAction, DeploymentCommand> commands) {
    Map<DeploymentAction, DeploymentCommand> copy = Map.copyOf(commands);
    EnumSet<DeploymentAction> missing = EnumSet.allOf(DeploymentAction.class);
    missing.removeAll(copy.keySet());
    if (!missing.isEmpty()) throw new IllegalArgumentException("missing commands: " + missing);
    this.commands = copy;
}
```

- Mapa jest równoważna `switch` tylko przy **rozłącznym kluczu** - nie przy nakładających się predykatach (to Chain, sekcja 8).
- Command **nie daje automatycznie** asynchroniczności, retry ani undo; `undo` wymaga modelu kompensacji (`OperatorCommand.undo`).
- Komendy współdzielone są bezstanowe; dane żądania to argument.

---

## 6.8. Typowe pomyłki przy wyborze

- Strategy dla stanu („bo to też `switch`”) - wariant zmienia sam obiekt, więc to State.
- State dla rodzaju obiektu, który się nie zmienia - to polimorfizm podtypów.
- Command bez potrzeby kolejki, logu ani cofania - wystarczy metoda lub mapa funkcji.
- Komenda pamiętająca dane poprzedniego żądania; pole konkretnego obiektu w enumie stanu.
- Kombinatoryczna hierarchia (`PremiumDeployingScaleUp`) zamiast złożenia osi.

CineLegacy s26: cena według typu biletu, operacje kasjera z cofaniem, cykl życia rezerwacji.

---

## 7.1. Apply Template Method

```java
public abstract class ReleaseImporter {
    public final ReleaseDraft importRelease(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("input must not be blank");
        Fields fields = parse(raw);
        // walidacja releaseId i service
        return new ReleaseDraft(fields.releaseId(), fields.service());
    }
    protected abstract Fields parse(String raw);
    protected final Fields fields(String releaseId, String service) { ... }
}
```

- Importery wykonują te same kroki w tej samej kolejności - zmienne jest tylko **parsowanie**.
- `final` chroni kolejność, ale dodany do opublikowanej, nadpisywanej metody łamie klientów.

---

## 7.2. Template Method - sekwencja i ryzyka

- Extract Method w każdej klasie, ujednolicaj sygnatury po jednym kroku, Pull Up Method dla szkieletu.
- Hook opcjonalny tylko przy poprawnym domyślnym zachowaniu dla wszystkich podklas.
- Nowa metoda abstrakcyjna psuje podklasy; hook z konstruktora bazy widzi domyślne pola podklasy.
- `synchronized` w szablonie trzyma monitor podczas hooków - ryzyko zakleszczenia.
- Wiele hooków lub zmienna kolejność → kompozycja lub Strategy.

---

## 7.3. Gdy haki niosą dwie osie

```java
public abstract class LegacyReleaseNotesExporter {
    public final String export(String version, List<ChangeEntry> entries) {
        var output = new StringBuilder(header(version));
        for (ChangeEntry entry : entries) {
            if (include(entry)) output.append(formatEntry(entry));
        }
        return output.append(footer()).toString();
    }
    protected abstract String header(String version);          // format i tytuł
    protected abstract boolean include(ChangeEntry entry);     // zakres treści
    protected abstract String formatEntry(ChangeEntry entry);  // format
    protected abstract String footer();                        // format
}
```

2 formaty × 2 zakresy = 4 podklasy; `escape` skopiowany między podklasami HTML; trzeci format dokłada dwie kolejne.

---

## 7.4. Po: szkielet w klasie finalnej, osie jako strategie

```java
public final class ReleaseNotesExporter {
    private final NotesFormat format;          // header, entry, footer
    private final ContentSelection selection;  // tytuł i Predicate<ChangeEntry>

    public String export(String version, List<ChangeEntry> entries) {
        var output = new StringBuilder(format.header(selection.title(), version));
        for (ChangeEntry entry : entries) {
            if (selection.includes(entry)) output.append(format.entry(entry));
        }
        return output.append(format.footer()).toString();
    }
}
```

- N formatów + M zakresów = N + M klas zamiast N × M podklas; nowa kombinacja to nowe złożenie.
- Kolejność kroków nadal chroni jedna metoda; `final` na klasie zamiast na metodzie szablonowej.

---

## 7.5. Replace Inheritance with Delegation

- Kiedy: podklasa nie jest zastępowalnym wariantem, `extends` służy ponownemu użyciu, haki niosą **niezależne** osie, wariant ma być wymienny.
- Procedura: zgrupuj haki według osi → Extract Interface dla każdej osi → delegat przejściowy wołający stary hak → przenieś implementacje → usuń podklasy po migracji klientów.
- Hook woła się na delegacie, nie na `this`; stan `protected` bazy nie jest dostępny dla strategii.
- Znika typ podklasy: `instanceof`, DI po typie, `synchronized` na innym monitorze.
- Tytuł przechodzi z kodu podklasy do danych strategii; `HtmlFormat` ucieka wersję, tytułu nie - tak jak legacy.

---

## 7.6. Strategy czy Template Method?

| Kryterium | Template Method | Strategy |
| --- | --- | --- |
| osie zmienności | jedna | jedna lub kilka niezależnych |
| wymiana wariantu | wybór podklasy | przy konstrukcji, także w czasie życia |
| dostęp do stanu bazy | tak (`protected`) | tylko przez parametry |
| typowe ryzyko | kruche haki, eksplozja podklas | więcej obiektów, konfiguracja |
| test wariantu | podklasa testowa | lambda lub fake |

Template Method jest dobry, gdy szkielet jest stabilny, a zmienny jest **jeden** krok. Dwie osie w hakach → Strategy. CineLegacy s27: okres × format raportu.

---

## 8.1. Move Embellishment to Decorator

```java
public String run(String releaseId) {
    audit.accept("start:" + releaseId);
    try {
        String result = delegate.run(releaseId);
        audit.accept("success:" + releaseId);
        return result;
    } catch (RuntimeException exception) {
        audit.accept("failure:" + releaseId + ":" + exception.getClass().getSimpleName());
        throw exception;
    }
}
```

- Opcjonalny dodatek wokół rdzenia (audyt, retry, cache) z **tym samym kontraktem** co rdzeń.
- **Kolejność jest zachowaniem:** `audit(retry(core))` to jeden wpis, `retry(audit(core))` - wpis na próbę.

---

## 8.2. Decorator - wyjątki, migracja, przezroczystość

- Zachowana jest instancja wyjątku delegata, ale błąd samego audytu może **zasłonić** błąd rdzenia - potrzebna jawna polityka.
- Migracja: wąski interfejs, zapis kolejności dla sukcesu i awarii, dekorator przekazujący, potem po jednym dodatku.
- Składanie łańcucha w kontrolowanej fabryce; dawną flagę usuń po migracji.
- Nieprzezroczysty dla `getClass`, tożsamości, serializacji i monitora `synchronized`.
- Ślepe delegowanie `equals` łamie symetrię.

---

## 8.3. Łańcuch `if/else` z ukrytą kolejnością

```java
if (request.linesChanged() > 5_000) {
    return Decision.of(Approver.REJECTED, "change too large");
} else if (request.risk() == Risk.LOW
        && (request.linesChanged() <= 50 || !request.production())) {
    return Decision.of(Approver.AUTOMATIC, "small low-risk change");
} else if (request.risk() != Risk.HIGH && request.linesChanged() <= 500) {
    return Decision.of(Approver.TEAM_LEAD, "moderate change");
} else if (request.risk() != Risk.HIGH || !request.production()) {
    return Decision.of(Approver.RELEASE_MANAGER, "large or non-production change");
} else if (request.linesChanged() <= 2_000) {
    return Decision.of(Approver.CHANGE_ADVISORY_BOARD, "high-risk production change");
}
return Decision.of(Approver.REJECTED, "no approver for " + request.id());
```

Warunek `RELEASE_MANAGER` jest poprawny tylko dlatego, że wcześniejsze gałęzie odfiltrowały mniejsze zmiany - **kolejność jest regułą**.

---

## 8.4. Chain of Responsibility - ogniwo i złożenie

```java
public abstract class ApprovalHandler {
    private ApprovalHandler next;
    public final ApprovalHandler then(ApprovalHandler next) {
        this.next = Objects.requireNonNull(next, "next");
        return next;
    }
    public abstract Decision handle(ChangeRequest request);
    protected final Decision passOn(ChangeRequest request) {
        if (next == null) return Decision.of(Approver.REJECTED, "no approver for " + request.id());
        return next.handle(request);
    }
}
// ApprovalRouter.standard(): SizeLimit → Automatic → TeamLead → ReleaseManager → ChangeAdvisoryBoard
```

Każde ogniwo samo decyduje: obsługuje i kończy albo `passOn`. Kolejność widać w **jednym** wyrażeniu.

---

## 8.5. Chain - sekwencja i ryzyka

- Sekwencja: test różnicowy na granicach progów → Extract Method dla gałęzi → klasa ogniwa z `passOn` → złożenie w jednym miejscu → usunięcie `if/else`.
- Ogniwa **nie są niezależne**: warunek ogniwa zakłada, że poprzednie odmówiły. Przestawienie zmienia wynik.
- Koniec łańcucha to decyzja (`REJECTED`), nie `null`; dokładnie jedno ogniwo obsługuje żądanie.
- Mutowalne `next`: złóż łańcuch raz, przy starcie; ogniw nie współdziel między łańcuchami.
- Pięć gałęzi w jednej stabilnej metodzie bywa czytelniejsze - łańcuch opłaca się przy kilku procesach zatwierdzania.

---

## 8.6. Chain of Responsibility czy Decorator?

| | Decorator | Chain of Responsibility |
| --- | --- | --- |
| ile elementów pracuje | wszystkie warstwy | zwykle jedno ogniwo |
| delegowanie | zawsze do opakowanego | tylko gdy ogniwo nie obsłuży |
| wynik | rdzeń i dodatek (audyt, retry, cache) | decyzja jednego ogniwa |
| kolejność oznacza | zasięg dodatków | priorytet reguł |
| przerwanie | wyjątek lub skrót (cache) | normalny przepływ |

Pytanie kontrolne: czy po obsłużeniu przez ten element **ktoś dalej** musi pracować? Tak → Decorator, nie → Chain. Audyt decyzji to Decorator wokół `ApprovalRouter`, nie kolejne ogniwo. CineLegacy s28.

---

## 9.1. Unify Interfaces with Adapter

```java
public final class LegacyGatewayAdapter implements ReleaseNotifier {
    private final LegacyMessageGateway gateway;

    @Override
    public String send(ReleaseMessage message) {
        Objects.requireNonNull(message, "message");
        return gateway.transmit(message.recipient(), "release:" + message.releaseId());
    }
}
```

- Klient (`NotificationService`) zależy tylko od **preferowanego kontraktu** `ReleaseNotifier`.
- Adapter tłumaczy nazwy, parametry, wyniki i błędy, ale nie udaje zgodności semantycznej.

---

## 9.2. Adapter - co naprawdę trzeba przetłumaczyć

- Podobna sygnatura ≠ podobny kontrakt: jednostki, strefa czasowa, precyzja, kodowanie.
- Znaczenie `null` i pustej wartości, indeksowanie, mutowalność wejścia i wyniku.
- Wyjątki mapowane z zachowaniem przyczyny; idempotencja, wątek wykonania.
- Adapter `AutoCloseable` musi wiedzieć, czy jest **właścicielem** zasobu.
- Jeśli wystarczy Rename Method lub Move Method, adapter jest zbędny.

---

## 9.3. Surowy ładunek w domenie

```java
public boolean canRelease(String buildId) {
    Map<String, String> payload = ciClient.fetchBuild(buildId);
    boolean succeeded = "SUCCESS".equals(payload.get("status").trim().toUpperCase());
    int failed = Integer.parseInt(payload.get("failed").trim());
    double coverage = Double.parseDouble(payload.get("coverage").trim());
    return succeeded && failed == 0 && coverage >= 80.0;
}
```

- `LegacyReleaseGate` powtarza nazwy kluczy, parsowanie i normalizację w każdej metodzie.
- Błędny ładunek wybucha **głęboko w domenie**: `NumberFormatException`, `NullPointerException`.
- Reguła wydania (sukces, zero błędów, pokrycie ≥ 80) ginie wśród szczegółów formatu.

---

## 9.4. Boundary Object: zwalidowany obraz na granicy

```java
public record BuildReport(String buildId, Status status, double coverage,
                          int failedTests, String commit) {
    public BuildReport { /* pokrycie 0-100, failedTests >= 0, commit = hex sha */ }
    public boolean succeeded() { return status == Status.SUCCESS; }
}
public final class CiBuildReportsAdapter implements BuildReports {     // port domenowy
    public BuildReport find(String buildId) {
        return BuildReportMapper.fromPayload(ciClient.fetchBuild(buildId));
    }
}
// ReleaseGate: report.succeeded() && report.failedTests() == 0 && report.coverage() >= 80.0
```

- Jeśli obiekt istnieje, jest poprawny - domena niczego już nie parsuje ani nie sprawdza.
- Pakietowo-prywatny `BuildReportMapper` jako jedyny zna klucze `id`, `status`, `coverage`, `failed`, `commit`.

---

## 9.5. Dane na granicy - decyzje

- Granicę przekraczają rekordy, obiekty wartości i błędy kontraktu, nie mapy ani modele frameworka.
- Walidacja na granicy zmienia **typ i moment** błędu: `NumberFormatException` w domenie → `IllegalArgumentException("invalid CI payload: ...")` w adapterze. To zmiana kontraktu dla danych błędnych - do zatwierdzenia.
- Dla danych poprawnych decyzje i podsumowania są identyczne (test różnicowy na pięciu ładunkach).
- Drobna różnica do nazwania: normalizacja z `Locale.ROOT` zamiast domyślnego locale.
- Osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian - nie kopiuj mechanicznie każdego DTO.

---

## 9.6. Adapter czy Boundary Object?

| | Adapter | Boundary Object |
| --- | --- | --- |
| tłumaczy | interfejs: wywołania, nazwy, protokół | dane: format, typy, walidację |
| odpowiada na pytanie | jak wywołać obcy system? | co wpuszczamy do domeny? |
| typowy produkt | implementacja portu | niezmienny rekord z niezmiennikami |
| ryzyko | udawana zgodność semantyczna | kopiowanie każdego DTO bez potrzeby |

Zwykle występują razem: adapter implementuje port i zwraca obiekt graniczny (`CiBuildReportsAdapter` → `BuildReport`). CineLegacy: s14 (Adapter), s29 (odpowiedź terminala `OK;AUTH-R1;4500`).

---

## 10.1. Factory - publiczna granica tworzenia

```java
public final class DeploymentProbeFactory {
    public DeploymentProbe create(ProbeKind kind, String target) {
        Objects.requireNonNull(kind, "kind");
        // walidacja target ...
        return switch (kind) {
            case HTTP -> new HttpProbe(target);
            case QUEUE -> new QueueProbe(target);
        };
    }
    private record HttpProbe(String endpoint) implements DeploymentProbe { ... }
    private record QueueProbe(String queueName) implements DeploymentProbe { ... }
}
// ProbeService: return factory.create(kind, target).check();
```

`switch` zostaje - to właściwe miejsce wiedzy o konstrukcji.

---

## 10.2. Factory - intencja, procedura, pułapki

- **Encapsulate Classes with Factory** ukrywa klasy konkretne; **Extract Factory Class** wydziela tworzenie z klasy o innej odpowiedzialności.
- Zinwentaryzuj konstruktory, refleksję, DI, deserializatory; przekierowuj tworzenie pojedynczo, widoczność ogranicz na końcu.
- Tworzenie wariantów z wyprzedzeniem lub cache może uruchomić kosztowne efekty wcześniej niż stara gałąź.
- Fabryka nie może stać się **globalnym rejestrem usług**.
- Ukrycie publicznego konstruktora w bibliotece wymaga etapu `@Deprecated`.

---

## 10.3. Fabryka jako strażnik spójności

- Jedno miejsce wiedzy o konstrukcji: walidacja, wybór klasy, kanonizacja, mapowanie konfiguracji.
- W module: `DeploymentZone.fromCode` (kanoniczne instancje), `SupportCostPolicy.forTier`, `OperatorCommand.byName` (nieznana akcja → wyjątek), `ApprovalRouter.standard()` (kolejność ogniw).
- Produkty `private` lub pakietowo-prywatne - klient nie utworzy niespójnego obiektu z pominięciem walidacji.
- Metoda fabrykująca vs klasa fabryki: wstrzykiwana klasa, gdy klient ma być testowany z innym produktem.
- Fabryka ≠ korzeń kompozycji: fabryka tworzy obiekty na żądanie, korzeń składa graf raz, przy starcie.

CineLegacy: s04 (Encapsulate Classes with Factory), s05 (Extract Factory Class).

---

## 11.1. Klient orkiestruje podsystemy

```java
public String publish(String releaseId, String environment) {
    if (releaseId == null || releaseId.isBlank()) throw new IllegalArgumentException(...);
    String target = environment.trim().toLowerCase(Locale.ROOT);
    if (!Set.of("staging", "prod").contains(target)) throw new IllegalArgumentException(...);
    String artifact = store.upload(releaseId);
    String config = renderer.render(releaseId, target);
    queue.enqueue(artifact, config);
    notifier.announce(releaseId, target);
    return "published " + releaseId + " to " + target;
}
```

- `LegacyReleaseCli` i `LegacyReleaseScheduler` znają cztery podsystemy, ich kolejność i walidację.
- Ta sama wiedza skopiowana i zapisana nieco inaczej - zmiana kolejności w jednym miejscu rozjedzie się z drugim.
- CineLegacy s30: sklep internetowy i kasa z tą samą orkiestracją; kasa nie pobiera opłaty rezerwacyjnej.

---

## 11.2. Po: fasada jako strażnik kolejności

```java
public final class ReleasePublishingFacade {
    private final ArtifactStore store;          // podsystemy: klasy pakietowo-prywatne
    private final ConfigRenderer renderer;
    private final DeploymentQueue queue;
    private final ReleaseNotifier notifier;

    public String publish(String releaseId, String environment) {
        // walidacja, potem: upload → render → enqueue → announce
    }
}
// klienci: new ReleaseCli(facade), new ReleaseScheduler(facade)
```

- Jedna operacja wymusza kolejność i walidację; klient spoza pakietu nie pominie kroku.
- Test: te same efekty w tej samej kolejności, a przy błędnym żądaniu **żadnego** efektu.

---

## 11.3. Facade - procedura i ryzyka

- Procedura: test efektów obu klientów → Extract Method orkiestracji → Move Method do fasady → przepięcie drugiego klienta → zawężenie widoczności podsystemów.
- Zawężenie do pakietowo-prywatnych to **zmiana API** dla klientów spoza pakietu - osobny krok, po migracji.
- Różnica do zapisania: `null` środowiska dawał `NullPointerException` bez komunikatu, teraz `requireNonNull(environment, "environment")`.
- Fasada to jedna spójna operacja, nie wszystkie operacje podsystemów (God Class); nie daje też transakcji.
- Fasada przekazująca 1:1 bez reguł to Middle Man (sekcja 2).

---

## 12.1. Obcy model w regułach domeny

```java
public List<String> canaryTargets() {
    return cmdb.fetchAll().stream()
            .filter(record -> record.statFlg() == 1)
            .filter(record -> "T".equals(record.envCd()) || "D".equals(record.envCd()))
            .map(LegacyCmdbRecord::nm)
            .sorted()
            .toList();
}
```

- `LegacyDeploymentPlanner` mówi językiem CMDB: kody `P/T/D`, flagi `1/0/9`, daty `yyyyMMdd`.
- Każda zmiana modelu CMDB przecieka do reguł planowania; test domeny wymaga rekordów CMDB.
- Reguła „canary tylko poza produkcją” jest zapisana jako „kod `T` albo `D`”.
- CineLegacy s31: rozliczenie w modelu dystrybutora - kody statusu, daty `yyyyMMdd`, grosze jako tekst, `45%`.

---

## 12.2. ACL: port domenowy i tłumacz

```java
public record Environment(String name, Tier tier, boolean active, LocalDate lastDeployment) { }

public final class CmdbEnvironmentDirectory implements EnvironmentDirectory {  // pakiet acl
    public List<Environment> all() {
        return cmdb.fetchAll().stream().map(CmdbTranslator::toEnvironment).toList();
    }
}
// CmdbTranslator: "P" → PRODUCTION, statFlg == 1 → active, "20261015" → LocalDate
// DeploymentPlanner: filter(Environment::active).filter(e -> e.tier() != Tier.PRODUCTION)
```

- Typy CMDB nie wychodzą poza pakiet `acl`; domenę testujemy bez żadnego typu obcego systemu.
- `CmdbTranslator` to jedyne miejsce, które zna kody i formaty CMDB.

---

## 12.3. Facade a Anti-Corruption Layer

| | Facade | Anti-Corruption Layer |
| --- | --- | --- |
| cel | prostszy interfejs podsystemu | ochrona języka domeny przed obcym modelem |
| model po obu stronach | ten sam, mniej wywołań | dwa różne - potrzebne tłumaczenie |
| zawartość | orkiestracja kroków | port, adapter, tłumacz, często fasada w środku |
| właściciel | zespół podsystemu lub klienta | zespół domeny |
| co testujemy | kolejność i efekty | tłumaczenie i reguły bez typów obcych |

Fasada **upraszcza**, ACL **tłumaczy**. ACL bez różnicy modeli to drogi Middle Man.

---

## 12.4. ACL - ryzyka i Strangler Fig

- Tłumaczenie **zachłanne** zmienia zachowanie dla danych błędnych: nieznany kod dawniej po cichu pomijany, teraz wyjątek; zła data w nieaktywnym rekordzie dawniej bez znaczenia, teraz zatrzymuje planowanie.
- Polityka (odrzuć, pomiń z logiem, kwarantanna) to decyzja biznesowa, nie szczegół implementacji.
- ACL to naturalny element **Strangler Fig** i **Branch by Abstraction**: port domenowy stoi, za nim stary i nowy dostawca.
- Każdy element przejściowy potrzebuje właściciela, testów i **kryterium usunięcia**.
- Koszt: drugi model, mapowanie, testy tłumacza - nie opłaca się, gdy model obcy jest zgodny z naszym.

---

## 13.1. Encapsulate Composite with Builder

```java
DeploymentGroup plan = PlanBuilder.group("release")
        .group("database", group -> group
                .task("backup", 5)
                .task("migrate", 8))
        .task("deploy", 3)
        .build();
```

- Uzasadniony, gdy budowa drzewa jest wieloetapowa i ma **własne niezmienniki**.
- `PlanBuilder` jest jednorazowy: każde wywołanie po `build()` rzuca `IllegalStateException`; wynik kopiuje listę.
- Lambda grupy ma kontrakt synchroniczny - odroczenie lub ponowne wywołanie zmienia zachowanie.
- Ustal: pusta grupa, unikalność nazw, kolejność dzieci; współdzielony podplan (DAG) liczy się dla każdej ścieżki.

---

## 13.2. Builder - kiedy i ryzyka

- Kandydat: konstruktor teleskopowy, wiele opcjonalnych parametrów, budowa drzewa lub grafu z niezmiennikami.
- Nie dla rekordu z trzema wymaganymi polami - kanoniczny konstruktor jest czytelniejszy.
- Walidacja w każdym kroku (szybki błąd) czy w `build()` (niezmienniki całości) - wybór zmienia moment wyjątku.
- Jednorazowość albo kopia przy `build()`; wynik niemutowalny; brak wycieku wewnętrznej listy.
- Test Data Builder w testach to inna rola - tu chodzi o produkcyjne API tworzenia.

CineLegacy s06: Builder dla drzewa repertuaru dzień - sala - seans.

---

## 14.1. Replace Hard-coded Notifications with Observer

```java
private final CopyOnWriteArrayList<Registration> listeners = new CopyOnWriteArrayList<>();

public Subscription subscribe(ReleaseListener listener) {
    Registration registration = new Registration(Objects.requireNonNull(listener, "listener"));
    listeners.add(registration);
    AtomicBoolean active = new AtomicBoolean(true);
    return () -> {
        if (active.compareAndSet(true, false)) listeners.remove(registration);
    };
}

public void publish(ReleasePublished event) {
    for (Registration registration : listeners) registration.notifyListener(event);
}
```

Osobny `Registration` na `subscribe` - uchwyt usuwa dokładnie własną rejestrację, idempotentnie.

---

## 14.2. Observer - kontrakt i migracja

- Subject zna tylko interfejs listenera; pierwszy krok zachowuje relację **jeden do jednego**.
- Wielu odbiorców to często **rozszerzenie** zachowania, nie refaktoryzacja.
- Kontrakt: synchroniczność i wątek, kolejność, duplikaty, wyjątki (tu fail-fast), wyrejestrowanie.
- Iterator na migawce: listener usunięty w trakcie publikacji może być jeszcze wywołany w bieżącej.
- Niezamknięty uchwyt wydłuża życie grafu obiektów; globalna szyna zdarzeń ukrywa zależności.

CineLegacy s09: mail, SMS i punkty lojalnościowe wołane na sztywno po opłaceniu.

---

## 15.1. Komponenty połączone każdy z każdym

```java
public void type(String value) {          // VersionField zna trzy inne komponenty
    if (!editable) return;
    text = value;
    boolean valid = isValid();
    picker.setEnabled(valid);
    if (!valid) picker.clear();
    button.setEnabled(valid && picker.hasSelection());
    if (!valid) label.setText("enter version");
    else if (!picker.hasSelection()) label.setText("choose approver");
    else label.setText("ready to publish");
}
```

- `ApproverPicker.choose` powtarza logikę etykiety; `PublishButton.click` blokuje pozostałe komponenty.
- Nowa reguła formularza dotyka kilku klas; zależności rosną jak n × (n - 1).

---

## 15.2. Po: reguły współpracy w jednym miejscu

```java
public final class ReleaseFormMediator implements FormMediator {
    private final VersionField versionField = new VersionField(this);
    private final ApproverPicker approverPicker = new ApproverPicker(this);
    @Override
    public void versionChanged() {
        boolean valid = versionValid();
        approverPicker.setEnabled(valid);
        if (!valid) approverPicker.clear();
        refresh();                                  // przycisk i etykieta
    }
    @Override public void approverChosen() { refresh(); }
}
// VersionField.type(): text = value; mediator.versionChanged();
```

Komponenty znają tylko `FormMediator`; pasywna `StatusLabel` nie zna nikogo. CineLegacy s32: ekran wyboru miejsc, 11 referencji między 4 komponentami.

---

## 15.3. Mediator - procedura i ryzyka

- Test: ta sama sekwencja zdarzeń daje tę samą migawkę `FormState` po każdym kroku, także dla zdarzeń ignorowanych.
- Procedura: interfejs mediatora → reguły jednego komponentu do mediatora → referencje do sąsiadów zastąpione wywołaniem mediatora → powtórz → usuń `connect`.
- Kolejność aktualizacji bywa zachowaniem - rozstrzyga test migawek, nie intuicja; uważaj na reentrancję.
- Ryzyko: **God Mediator** dla całego ekranu lub systemu - dziel według spójnych grup reguł.
- Dwa komponenty i jedna zależność - zwykłe wywołanie wystarczy.

---

## 15.4. Observer czy Mediator?

| | Observer | Mediator |
| --- | --- | --- |
| kierunek | subject → wielu niezależnych odbiorców | komponenty ↔ jeden koordynator |
| reguły współpracy | rozproszone w odbiorcach | skupione w mediatorze |
| kto kogo zna | odbiorcy nie znają się nawzajem | mediator zna wszystkich |
| typowe ryzyko | ukryte zależności, kolejność, wycieki | God Mediator |
| pasuje do | zdarzeń domenowych, powiadomień | formularzy, dialogów, koordynacji |

Mediator często używa Observera jako mechanizmu powiadomień - różnica dotyczy **miejsca reguł**.

---

## 16.1. Visitor i macierz zmian

```java
public final class TotalMinutesVisitor implements PlanVisitor<Long> {
    public Long visitTask(DeploymentTask task) { return task.minutes(); }
    public Long visitGroup(DeploymentGroup group) {
        long total = 0;
        for (PlanComponent c : group.components()) total = Math.addExact(total, c.accept(this));
        return total;
    }
}
```

| Częsta zmiana | Naturalniejszy kierunek |
| --- | --- |
| nowe operacje, stabilne rodzaje węzłów | Visitor |
| nowe rodzaje węzłów, stabilne operacje | metody polimorficzne |
| jedna prosta akumulacja | Collecting Parameter |

---

## 16.2. Do i od Visitora

- Do Visitora: łańcuchy `instanceof` lub `switch` po rodzaju węzła powtarzane w wielu operacjach, stabilny zestaw węzłów.
- Java 21+: `switch` z dopasowaniem wzorców po `sealed` daje wyczerpujące sprawdzenie bez podwójnej dyspozycji - często prostsza alternatywa.
- Od Visitora: nowe węzły częstsze niż nowe operacje albo jeden Visitor w całym kodzie.
- Ryzyka: publiczny dostęp do danych węzła, rekurencja (głębokość, cykle), wynik opakowany (`Long`).
- Dwie równoległe ścieżki akumulacji (metoda węzła i Visitor) - po decyzji zostaw jedną.

CineLegacy: s18 (Collecting Parameter), s19 (Visitor).

---

## 17.1. Replace Conditional with Polymorphism

```java
// przed: pole value ma znaczenie zależne od kind
public record LegacyDeploymentStep(Kind kind, String value) { ... switch (kind) ... }

// po: zamknięty zestaw rodzajów, każdy z własnymi danymi
public sealed interface DeploymentStep permits ScriptStep, ApprovalStep { String execute(); }
public record ScriptStep(String command) implements DeploymentStep {
    public String execute() { return "executed:" + command; }
}
```

- Selektor opisuje **stabilny rodzaj** obiektu; tryb przejściowy → State, algorytm klienta → Strategy.
- Najpierw znajdź tworzenie, deserializację, mapowanie ORM i podobne `switch` w innych usługach.
- Nowy podtyp `sealed` może ujawnić wyczerpujące `switch` także poza modułem. CineLegacy s02.

---

## 17.2. Replace Type Code with Class

```java
public final class DeploymentZone {
    public static final DeploymentZone PRODUCTION = new DeploymentZone("PROD", true);
    public static DeploymentZone fromCode(String code) {
        // pusty → wyjątek, normalizacja Locale.ROOT, nieznany → wyjątek
    }
    public boolean requiresApproval() { return approvalRequired; }
}
```

- Nowy typ przejmuje **walidację, normalizację i operacje** pojęcia; nie oznacza automatycznie podklas.
- Enum dla prostego, zamkniętego zestawu; klasa przy aliasach, kodach zewnętrznych i własnej polityce tworzenia.
- W bazie zostaje stabilny kod `PROD`, nie nazwa stałej ani `toString`. CineLegacy s03.

---

## 17.3. Replace Implicit Tree with Composite

```java
public sealed interface PlanComponent permits DeploymentTask, DeploymentGroup {
    String name();
    long totalMinutes();
    void collectTasks(Collection<? super DeploymentTask> target);
    <R> R accept(PlanVisitor<R> visitor);
}
```

- Zamiast ścieżek `release/database/backup` i wyszukiwania po prefiksie `path + "/"`.
- `LegacyPathPlanMapper` odwzorowuje stary format; dane, których kolejności nie da się zachować (`a/first`, `b/second`, `a/third`), odrzuca **błędem** zamiast cichej zmiany.
- Głębokie drzewa i cykle: limit głębokości dla danych niezaufanych; `Math.addExact` czyni przepełnienie jawnym. CineLegacy s10-s11.

---

## 17.4. Jeden lub wiele i Extract Composite

- **Replace One/Many Distinctions with Composite:** jeden kontrakt `execute(PlanComponent)` zamiast `execute` i `executeAll` z powielonymi regułami.
- Nie zmieniaj: kolejności (preorder), pustej grupy, przerwania po pierwszym błędzie, liczby transakcji.
- Safe Composite: zarządzanie dziećmi tylko w grupie; Transparent: we wspólnym kontrakcie, więc `add` na liściu wymaga wyjątku.
- **Extract Composite:** kilka klas hierarchii powiela obsługę dzieci (`ReleaseGroup`, `RollbackGroup`) → wspólna `CompositePlanNode`.
- Podobne pętle mogą znaczyć co innego (pomijanie, kontynuacja po błędzie) - porównaj kontrakty. CineLegacy s12-s13.

---

## 17.5. Collecting Parameter

- Metoda mutująca lokalny wynik → elementy same dopisują wynik do **przekazanego akumulatora**.
- `collectTasks(Collection<? super DeploymentTask>)`: liść dodaje siebie, grupa przekazuje akumulator dzieciom.
- Wywołujący zachowuje własność kolekcji; ustal, czy metoda czyści, czy dopisuje.
- Po wyjątku może zostać **wynik częściowy** - to część kontraktu.
- Dobry parametr zbierający ma wąski typ; ogólna mutowalna mapa ukrywa zbyt wiele.

---

## 17.6. Limit Instantiation with Singleton

```java
public enum DeploymentDefaults {
    INSTANCE;
    private final Duration healthCheckTimeout = Duration.ofSeconds(30);
    public Duration healthCheckTimeout() { return healthCheckTimeout; }
}
```

- To **decyzja o cyklu życia** - najpierw zmierz koszt i udowodnij równoważność instancji.
- Enum: bezpieczna publikacja i serializacja, ale **jedna instancja na loader klas**.
- Globalny mutowalny stan łączy testy i ukrywa zależności - zwykle lepsze jawne wstrzyknięcie lub zakres DI.
- Odwrotna transformacja: **Inline Singleton** (sekcja 2.4). CineLegacy s17.

---

## Ćwiczenie 1: usunięcie zbędnego wzorca (30 min)

- Cel: ocenić, czy pośrednik i singleton chronią wartościową granicę; jeśli nie, usunąć je małymi krokami.
- Kod: `middleman/before` (Remove Middle Man) i `inlinesingleton/before` (Inline Singleton).
- Pisemna ocena pośrednika (autoryzacja, transakcje, telemetria, retry, translacja błędów) przed pierwszym ruchem.
- Zmiana kontraktu (np. niezmienne flagi) nazwana i zatwierdzona osobno.

Szczegóły: zadania modułu 5, Ćwiczenie 1.

---

## Ćwiczenie 2: Command, Strategy czy State (60 min)

- Cel: w `LegacyReleaseConsole` przypisać każdą oś do mechanizmu i wprowadzić jeden kontrakt naraz.
- Test równoważności dla kosztu, logu poleceń, cofania, cyklu życia i komunikatów błędów - przed pierwszą zmianą.
- Stan przejściowy nie jest strategią; polecenie nie pamięta danych poprzedniego żądania.
- Wariant CineLegacy: scena s26 (pulpit kasjera).

Szczegóły: zadania modułu 5, Ćwiczenie 2.

---

## Ćwiczenie 3: Template Method → Strategy, Chain czy Decorator (60 min)

- **A:** `LegacyReleaseNotesExporter` z czterema podklasami → jeden eksporter i dwie strategie; nowa kombinacja bez nowej klasy.
- **B:** `LegacyApprovalRouter` → łańcuch ogniw; test na granicach progów przed zmianą.
- **C:** decyzja: audyt decyzji zatwierdzania jako Decorator czy ogniwo łańcucha - uzasadnij.
- Wariant CineLegacy: sceny s27 i s28.

Szczegóły: zadania modułu 5, Ćwiczenie 3.

---

## Ćwiczenie 4: granice - Adapter, Boundary Object, Facade, ACL (60 min)

- **A:** surowy ładunek CI → port, adapter i `BuildReport` (`boundaryobject/before`).
- **B:** orkiestracja skopiowana w dwóch klientach → fasada z ukrytymi podsystemami (`facade/before`).
- **C:** kody CMDB w regułach planowania → port domenowy i tłumacz (`anticorruptionlayer/before`).
- Każda zmiana typu lub momentu błędu na granicy zapisana jako decyzja.
- Wariant CineLegacy: sceny s29 (terminal), s30 (sklep i kasa), s31 (dystrybutor).

Szczegóły: zadania modułu 5, Ćwiczenie 4.

---

## Kluczowe wnioski z omówienia

- Pośrednik i singleton bywają wartościowe - usuwamy je dopiero po pisemnej ocenie, co naprawdę robią.
- Ten sam `switch` w `LegacyReleaseConsole` to trzy osie: o mechanizmie decyduje, **kto wybiera wariant i kiedy**.
- Dwie osie w hakach Template Method to sygnał do Strategy: N + M zamiast N × M.
- Ogniwa łańcucha zależą od kolejności; audyt decyzji to Decorator, nie ogniwo.
- Walidacja na granicy (Boundary Object, ACL) zmienia zachowanie dla danych błędnych - to decyzja, nie skutek uboczny.

Pełne omówienie: rozwiązania modułu 5.

---

## Mapa decyzji (1/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| wymienny algorytm wybierany raz | Strategy | moment wyboru, stan strategii |
| żądanie do kolejki, logu, cofnięcia | Command | kompletność, stan komendy, kompensacja |
| zachowanie zależne od stanu | State | tabela przejść, atomowość, efekty |
| trwały rodzaj obiektu | polimorfizm | fabryki, serializacja |
| stała sekwencja, jeden zmienny krok | Template Method | kruche haki |
| kilka osi w hakach | Strategy przez delegację | utrata typu podklasy |

---

## Mapa decyzji (2/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| uporządkowane reguły, obsługuje jedna | Chain of Responsibility | zależność od kolejności, koniec łańcucha |
| opcjonalny dodatek wokół rdzenia | Decorator | kolejność, wyjątki, tożsamość |
| niezgodne API | Adapter | semantyka, własność zasobów |
| surowe dane z zewnątrz w domenie | Boundary Object | typ i moment błędu |
| klienci orkiestrują podsystemy | Facade | God Class, brak transakcji |
| obcy model w języku domeny | Anti-Corruption Layer | zachłanne tłumaczenie, koszt modelu |
| rozproszona wiedza o tworzeniu | Factory | globalny rejestr |

---

## Mapa decyzji (3/3)

| Sytuacja | Rozważ | Uważaj na |
| --- | --- | --- |
| skomplikowane tworzenie drzewa | Builder | moment walidacji, ponowne użycie |
| zmienni odbiorcy zdarzenia | Observer | kolejność, błędy, wątki |
| komponenty znające się nawzajem | Mediator | God Mediator, reentrancja |
| wiele operacji na stabilnych typach | Visitor | koszt nowego rodzaju węzła |
| pośrednik przekazujący 1:1 | Remove Middle Man | utracona autoryzacja lub telemetria |
| globalny mutowalny stan | Inline Singleton | przełączanie w locie, inicjalizacja |

---

## Lista kontrolna

- **Decyzja:** problem opisany bez nazwy wzorca; wariant istnieje dziś; rozważona prostsza alternatywa i kierunek „od wzorca”.
- **Kontrakt:** testy obejmują każdą gałąź, `null`, wartość nieznaną i granice progów; zachowane wyjątki, stan po błędzie, kolejność efektów.
- **Kroki:** jeden kontrakt naraz, automat IDE tam, gdzie się da; użycia dynamiczne (refleksja, konfiguracja) sprawdzone ręcznie.
- **Granice:** obcy model i surowe dane nie przechodzą do domeny; zmiana typu lub momentu błędu zatwierdzona osobno.
- **Na koniec:** stary warunek i elementy przejściowe usunięte; nowy podtyp `sealed` sprawdzony we wszystkich `switch`; klienci binarni i refleksyjni uwzględnieni.

---

## Sprawdzenie wiedzy

1. Jakie sygnały wskazują, że wzorzec należy usunąć?
2. Co trzeba sprawdzić przed Remove Middle Man?
3. Dlaczego Inline Singleton może być zmianą kontraktu?
4. Kto wybiera wariant w Strategy, w Command i w State?
5. Kiedy Template Method warto zastąpić Strategy?
6. Czym różni się Chain of Responsibility od Decoratora?
7. Co odróżnia Boundary Object od Adaptera, a Facade od ACL?
8. Czego nie widzi automatyczny Rename w IDE?
9. Kiedy prosty `switch` jest lepszy od wzorca?

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja do wzorca zaczyna się od **kontraktu i osi zmienności**, nie od diagramu klas.
- Kierunek „od wzorca” jest równie ważny: Remove Middle Man, Inline Singleton, Collapse Hierarchy.
- Podobne wzorce rozstrzygają proste pytania: kto wybiera wariant, ile elementów pracuje, który model chronimy.
- Granice (Adapter, Boundary Object, Facade, ACL) przesuwają błędy bliżej źródła - to świadoma zmiana kontraktu.
- IDE i testy równoważności dają małe, odwracalne kroki; rozszerzenia zachowania zatwierdzamy osobno.
- Wzorzec zostaje tylko, gdy upraszcza **realny** problem.
