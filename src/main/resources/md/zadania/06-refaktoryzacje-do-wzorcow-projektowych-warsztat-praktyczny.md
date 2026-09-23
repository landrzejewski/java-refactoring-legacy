# Moduł 6. Refaktoryzacje do wzorców projektowych - warsztat praktyczny

## Zadania dla uczestników

### Jak pracować

Każde ćwiczenie wykonujemy w krótkiej, powtarzalnej pętli:

1. uruchom testy bazowe,
2. nazwij zachowanie chronione w bieżącym kroku,
3. wykonaj jedną transformację,
4. uruchom test celowany,
5. uruchom test różnicowy (wersja przed i po na tych samych danych),
6. zapisz decyzję projektową (jedno, dwa zdania w notatce albo w opisie commita),
7. przejdź do następnego kroku dopiero po zielonym wyniku.

Nie zaczynaj od utworzenia wszystkich klas wzorca. Pierwszy commit powinien być mały i odwracalny. Refaktoryzacja zachowuje obserwowalne zachowanie: jeżeli w trakcie pracy chcesz dodać nowy wariant, zmienić obsługę błędów, wątek wykonania albo liczbę odbiorców, zapisz to jako osobną decyzję i wykonaj w osobnym kroku.

Przy każdej zmianie sprawdzaj nie tylko wartość zwracaną, ale również:

- typ wyjątku i jego komunikat,
- stan obiektu po sukcesie i po błędzie,
- liczbę i kolejność efektów ubocznych,
- zachowanie dla `null`, wartości nieznanej i gałęzi domyślnej.

### Środowisko

Pracuj w katalogu repozytorium. Kod wyjściowy modułu znajduje się w pakietach `before` (klasy z przedrostkiem `Legacy`).

```bash
# kompilacja i uruchomienie demonstracji modułu
mvn -q compile && java -cp target/classes pl.training.module6.Module6Examples

# testy modułu
mvn test -Dtest='pl.training.module6.**'
```

Nowe klasy twórz we własnym pakiecie (np. `pl.training.module6.workshop`) albo na osobnej gałęzi, tak aby zawsze móc porównać wynik z kodem wyjściowym. Testy pisz w `src/test/java/pl/training/module6/`.

---

## Ćwiczenie 1: wybór właściwego mechanizmu

**Cel:** rozpoznać, jaki rodzaj zmienności kryje się za podobnie wyglądającymi warunkami, i dla każdej osi wprowadzić właściwy kontrakt bez zmiany zachowania.

**Czas:** ok. 45-60 minut (orientacyjnie, dostosuje prowadzący).

### Kontekst

Punkt wyjścia to jeden serwis z czterema warunkami:

- wariant kalkulacji kosztu,
- rodzaj kroku wdrożenia,
- bieżący stan wydania,
- akcję operatora.

W projekcie każda z tych osi występuje w osobnej klasie wyjściowej. Możesz pracować bezpośrednio na tych klasach albo najpierw złożyć z nich jeden serwis (szkic poniżej), aby odczuć, jak osie mieszają się w jednym miejscu.

Pliki wyjściowe:

- `src/main/java/pl/training/module6/strategy/before/LegacyDeploymentCostCalculator.java` - koszt zależny od `DeploymentMode` (`STANDARD`, `EXPEDITED`),
- `src/main/java/pl/training/module6/polymorphism/before/LegacyDeploymentStep.java` - krok wdrożenia z polem `kind` (`SCRIPT`, `APPROVAL`) i polem `value`, którego znaczenie zależy od `kind`,
- `src/main/java/pl/training/module6/state/before/LegacyRelease.java` - wydanie z polem `status` i metodami `approve`, `deploy`, `cancel`,
- `src/main/java/pl/training/module6/command/before/LegacyDeploymentDispatcher.java` - dispatcher akcji operatora (`PAUSE`, `ROLLBACK`).

Szkic serwisu łączącego cztery osie (dane wejściowe, zbudowane z powyższych klas):

```java
public final class LegacyDeploymentService {
    private Status status = Status.DRAFT;

    public long cost(long baseCostInCents, DeploymentMode mode) {
        if (baseCostInCents < 0) {
            throw new IllegalArgumentException("base cost must not be negative");
        }
        return switch (Objects.requireNonNull(mode, "mode")) {
            case STANDARD -> baseCostInCents;
            case EXPEDITED -> Math.addExact(baseCostInCents, baseCostInCents / 4);
        };
    }

    public String executeStep(Kind kind, String value) {
        return switch (kind) {
            case SCRIPT -> "executed:" + value;
            case APPROVAL -> "approved-by:" + value;
        };
    }

    public void approve() { /* if (status == DRAFT) status = APPROVED; else throw ... */ }
    public void deploy()  { /* if (status == APPROVED) status = DEPLOYED; else throw ... */ }
    public void cancel()  { /* DRAFT lub APPROVED -> CANCELLED; else throw ... */ }

    public String dispatch(DeploymentAction action, String releaseId) {
        Objects.requireNonNull(action, "action");
        if (releaseId == null || releaseId.isBlank()) {
            throw new IllegalArgumentException("releaseId must not be blank");
        }
        return switch (action) {
            case PAUSE -> "paused:" + releaseId;
            case ROLLBACK -> "rolled-back:" + releaseId;
        };
    }
}
```

Dokładne komunikaty wyjątków i reguły walidacji odczytaj z plików wyjściowych.

### Polecenia

1. Przypisz każdą oś do jednego z mechanizmów: Strategy, polimorfizm, State albo Command.
2. Dla każdej osi zapisz uzasadnienie w jednym zdaniu.
3. Dodaj test charakterystyki dla gałęzi domyślnej i `null` (oraz dla pozostałych gałęzi i przypadków błędnych, jeżeli jeszcze nie są pokryte).
4. Wprowadź jeden kontrakt naraz. Po każdym kontrakcie uruchom testy.
5. Nie usuwaj warunku, dopóki wszystkie punkty tworzenia nie używają nowego modelu.

### Kryteria akceptacji

- nie powstała kombinatoryczna hierarchia podtypów,
- stan przejściowy nie jest strategią,
- komendy nie przechowują danych poprzedniego żądania,
- fabryka lub korzeń kompozycji jest jedynym miejscem mapowania konfiguracji na Strategy,
- testy porównują wyniki i wyjątki wersji przed i po.

### Oczekiwany produkt

- krótka tabela: oś - mechanizm - uzasadnienie,
- zestaw testów charakterystyki (w tym `null`, gałąź domyślna, wartości graniczne),
- seria małych commitów, z których każdy wprowadza jeden kontrakt,
- testy różnicowe przechodzące dla wersji przed i po.

---

## Ćwiczenie 2: State, Observer i Decorator

**Cel:** połączyć kilka wzorców współpracy wokół jednego obiektu domenowego i jawnie zapisać kontrakt kolejności, błędów i subskrypcji.

**Czas:** ok. 60 minut (orientacyjnie, dostosuje prowadzący).

### Kontekst

Rozbuduj przykład wydania o zapis audytu i powiadomienie odbiorców po udanym wdrożeniu.

Pliki wyjściowe:

- `src/main/java/pl/training/module6/state/before/LegacyRelease.java` - wydanie ze stanami `DRAFT`, `APPROVED`, `DEPLOYED`, `CANCELLED`; niedozwolona operacja rzuca `IllegalStateException` z komunikatem `cannot <akcja> release in state <STAN>`,
- `src/main/java/pl/training/module6/decorator/before/LegacyDeploymentRunner.java` - wdrożenie z wplecionym audytem (`start:`, `success:`, `failure:<id>:<TypWyjątku>`),
- `src/main/java/pl/training/module6/observer/before/LegacyReleasePublisher.java` - publikacja do dwóch na stałe wpisanych odbiorców (`auditLog`, potem `metrics`).

Tabela przejść wydania (do potwierdzenia testami przed zmianą):

| Stan | approve | deploy | cancel |
| --- | --- | --- | --- |
| DRAFT | APPROVED | wyjątek | CANCELLED |
| APPROVED | wyjątek | DEPLOYED | CANCELLED |
| DEPLOYED | wyjątek | wyjątek | wyjątek |
| CANCELLED | wyjątek | wyjątek | wyjątek |

### Polecenia

1. Ustal, czy audyt jest częścią przejścia State, dekoracją operacji, czy osobnym zdarzeniem.
2. Zapisz kolejność stanu, audytu i publikacji.
3. Dodaj listener, który usuwa inną subskrypcję podczas callbacku.
4. Dodaj listener rzucający wyjątek.
5. Udokumentuj politykę fail-fast albo kontynuacji.
6. Przetestuj stan po awarii audytu oraz listenera.

### Kryteria akceptacji

- kod nie wywołuje listenerów pod nieudokumentowaną blokadą,
- uchwyt subskrypcji jest zamykany idempotentnie,
- test ustala, czy usunięty listener jest jeszcze wywoływany podczas bieżącej publikacji,
- nie nastąpiła niejawna zmiana na wykonanie asynchroniczne,
- błąd dodatku nie zasłania pierwotnej przyczyny bez jawnej decyzji.

### Oczekiwany produkt

- zapisany kontrakt publikacji (synchroniczność, wątek, kolejność, duplikaty, polityka wyjątków, wyrejestrowanie),
- zapisana kolejność: zmiana stanu, audyt, publikacja (dla sukcesu i dla awarii),
- testy: pełna tabela przejść (w tym niezmieniony stan po niedozwolonej operacji), usunięcie subskrypcji w trakcie callbacku, wyjątek listenera, awaria audytu, podwójne zamknięcie uchwytu.

---

## Ćwiczenie 3: rodzina refaktoryzacji Composite

**Cel:** zastąpić ukryte drzewo jawnym modelem Composite i przećwiczyć powiązane techniki: jeden kontrakt dla jednego i wielu, Builder, Collecting Parameter i Visitor.

**Czas:** ok. 60-90 minut (orientacyjnie, dostosuje prowadzący).

### Kontekst

Punkt wyjścia to lista zadań ze ścieżkami tekstowymi oraz osobne metody dla jednego zadania i listy.

Pliki wyjściowe:

- `src/main/java/pl/training/module6/composite/before/LegacyPathPlan.java` - plan jako płaska lista `Entry(path, minutes)`; hierarchia istnieje tylko w konwencji ścieżki, np. `release/database/backup`; metody `totalMinutes()` i `taskNamesBelow(path)`,
- `src/main/java/pl/training/module6/composite/before/LegacyPlanExecutor.java` - para metod `execute(Task)` i `executeAll(List<Task>)`.

Przykładowe dane wejściowe:

```java
List<LegacyPathPlan.Entry> entries = List.of(
        new LegacyPathPlan.Entry("release/database/backup", 5),
        new LegacyPathPlan.Entry("release/database/migrate", 8),
        new LegacyPathPlan.Entry("release/deploy", 3));
```

Dodatkowe przypadki do rozważenia:

- `release/a/first`, `release/b/second`, `release/a/third` (powrót do wcześniej zamkniętej grupy),
- `release/database` oraz `release/database/migrate` (ta sama ścieżka jako zadanie i jako grupa),
- `other/deploy` przy korzeniu `release`,
- ścieżki z pustym lub białym segmentem, ukośnikiem na początku lub końcu,
- zadanie z `Long.MAX_VALUE` minut.

### Polecenia

1. Scharakteryzuj separator, kolejność i niepoprawne ścieżki.
2. Wprowadź `PlanComponent`, liść i grupę.
3. Zbuduj mapper starego formatu do Composite.
4. Zastąp rozróżnienie jeden lub wiele jednym kontraktem.
5. Enkapsuluj konstrukcję drzewa w Builderze.
6. Przenieś zbieranie liści do parametru zbierającego.
7. Dodaj osobny Visitor liczący czas.
8. Przetestuj puste drzewo, głęboką strukturę, przepełnienie i współdzielone poddrzewo.

### Kryteria akceptacji

- gotowy wynik Buildera jest niemutowalny,
- kolejność przechodzenia po strukturze jest jawna,
- przepełnienie nie zawija wartości,
- każdy Visitor obsługuje wszystkie dozwolone rodzaje węzłów,
- zmiana formatu trwałego jest oddzielona od refaktoryzacji modelu.

### Oczekiwany produkt

- model: wspólny kontrakt węzła, liść, grupa,
- mapper ze starego formatu z jawnie opisaną polityką dla danych niejednoznacznych,
- jeden wykonawca przyjmujący pojedyncze zadanie i grupę,
- Builder z określonym zachowaniem po `build()`,
- test różnicowy: suma minut, nazwy i kolejność zadań dla korzenia i podgrupy, a także niezależne oczekiwania liczbowe (nie tylko porównanie przed/po).

---

## Sprawdzenie wiedzy

1. Dlaczego usunięcie `switch` nie wystarcza do uzasadnienia Strategy?
2. Kiedy kod typu powinien stać się klasą wartości, a kiedy podtypem?
3. Dlaczego wybór strategii w konstruktorze może zmienić zachowanie?
4. Co odróżnia State od Strategy?
5. Dlaczego kolejność Decoratorów jest częścią kontraktu?
6. Jak zachowuje się iterator `CopyOnWriteArrayList` po usunięciu listenera podczas callbacku?
7. Kiedy przejście od jednego odbiorcy do kolekcji Observerów jest rozszerzeniem?
8. Co musi tłumaczyć Adapter oprócz nazw metod?
9. Kiedy mapy komend nie można uznać za równoważną łańcuchowi warunków?
10. Dlaczego rekord z komponentem `List` nie jest automatycznie głęboko niemutowalny?
11. Jaki wymiar zmian preferuje klasyczny Visitor?
12. Czym Extract Composite różni się od Replace Implicit Tree with Composite?
13. Dlaczego enum Singleton nie oznacza bezwzględnie jednej instancji w całym procesie?
14. Jakie zachowanie trzeba ustalić dla częściowego błędu podczas akumulacji?
15. Kiedy prosty `switch` jest lepszy od wzorca?
