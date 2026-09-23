# Moduł 6. Refaktoryzacje do wzorców projektowych - rozwiązania dla prowadzącego

Numeracja i nazwy zadań są zgodne z plikiem zadań. Ścieżki Javy są względne względem katalogu repozytorium.

Materiał teoretyczny zawiera odpowiedzi skrócone wyłącznie do pytań sprawdzających. Rozwiązania ćwiczeń zostały opracowane na podstawie kodu referencyjnego (pakiety `after`) oraz sekcji teorii opisujących procedury i ryzyka.

---

## Ćwiczenie 1: wybór właściwego mechanizmu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Uwaga organizacyjna

W projekcie nie ma pliku z jednym serwisem zawierającym cztery warunki. Zadanie odwołuje się do czterech klas legacy (strategy, polymorphism, state, command). Uczestnicy mogą najpierw złożyć je w szkic `LegacyDeploymentService` (podany w zadaniu), albo od razu pracować na czterech klasach. Najważniejsza jest część analityczna (krok 1 i 2) oraz dyscyplina małych kroków.

### Krok 1-2: przypisanie osi i uzasadnienie

| Oś | Mechanizm | Uzasadnienie (jedno zdanie) | Kod referencyjny |
| --- | --- | --- | --- |
| wariant kalkulacji kosztu | Strategy | Wymienny algorytm wybierany niezależnie od rodzaju obiektu, rozwijany jako niezależna polityka, z walidacją wspólną w kontekście. | `src/main/java/pl/training/module6/strategy/after/` |
| rodzaj kroku wdrożenia | polimorfizm podtypów | Rodzaj kroku jest trwały przez całe życie obiektu, a zachowanie i dane (`command`, `approver`) naturalnie do niego należą. | `src/main/java/pl/training/module6/polymorphism/after/` |
| bieżący stan wydania | State | Zachowanie zależy od bieżącego stanu, który zmienia się w czasie życia obiektu, a operacje wyznaczają przejścia. | `src/main/java/pl/training/module6/state/after/Release.java` |
| akcja operatora | Command | Selektor jest rozłącznym kluczem żądania kierowanego do wykonawcy, więc można go zastąpić rejestrem komend. | `src/main/java/pl/training/module6/command/after/` |

Pomocnicza tabela z sekcji 1.1 teorii: algorytm - Strategy, trwały rodzaj - polimorfizm, stan i przejścia - State, żądanie do wykonawcy - Command, samo tworzenie obiektu - Factory.

### Krok 3: testy charakterystyki

Minimalny zestaw (wzorzec: `StrategyEquivalenceTest`, `PolymorphismEquivalenceTest`, `StateEquivalenceTest`, `CommandEquivalenceTest` w `src/test/java/pl/training/module6/`):

- koszt: `STANDARD` i `EXPEDITED` dla 0, 1, 4, 10 001; wartość ujemna (`base cost must not be negative`); `mode == null` (`NullPointerException` z komunikatem `mode`); `Long.MAX_VALUE` dla `EXPEDITED` (`ArithmeticException`),
- krok: wyniki `executed:<cmd>` i `approved-by:<kto>`; pusta wartość (`command must not be blank` / `approver must not be blank`); `kind == null`,
- stan: wszystkie komórki tabeli przejść, w tym komunikat `cannot <akcja> release in state <STAN>` i niezmieniony status po wyjątku,
- akcja: PAUSE, ROLLBACK, `action == null`, pusty `releaseId` (`releaseId must not be blank`).

"Gałąź domyślna": we wszystkich przykładach `switch` jest wyczerpujący po enumie, więc nie ma `default`. Warto to zauważyć na głos: nowa stała enumu spowoduje błąd kompilacji w `switch`, ale nie w mapie komend (tam chroni sprawdzenie kompletności w konstruktorze).

### Krok 4-5: kolejność wprowadzania kontraktów

Przykładowa sekwencja commitów:

1. Testy charakterystyki (bez zmian w kodzie produkcyjnym).
2. Strategy: interfejs `DeploymentCostPolicy`, polityka przejściowa zawierająca jeszcze stary `switch`, potem `StandardCostPolicy` i `ExpeditedCostPolicy`; walidacja zostaje w `DeploymentCostCalculator`; mapowanie konfiguracji na politykę w jednym miejscu (fabryka lub korzeń kompozycji).
3. Polimorfizm: zamknięcie tworzenia za fabrykami `script`/`approval`, wprowadzenie `DeploymentStep`, przeniesienie po jednej gałęzi do `ScriptStep` i `ApprovalStep`, usunięcie `Kind` dopiero gdy nie steruje już niczym innym.
4. State: tabela przejść, `ReleaseState` z domyślnymi metodami rzucającymi wyjątek (nie pustymi), stany jako bezstanowe enumy, kontekst przypisuje `state = state.approve()`.
5. Command: wydzielenie ciał gałęzi do metod, potem do klas `PauseDeployment`, `RollbackDeployment`, rejestr `Map.copyOf` ze sprawdzeniem `EnumSet.allOf`, zastąpienie `switch` wyszukaniem.
6. Usunięcie starych warunków dopiero po migracji wszystkich punktów tworzenia.

### Weryfikacja kryteriów akceptacji

- Brak kombinatorycznej hierarchii: nie powstają klasy typu `ExpeditedScriptApprovedStep`. Każda oś ma własny kontrakt i obiekty są składane, nie dziedziczone.
- Stan przejściowy nie jest strategią: stan wydania nie jest wstrzykiwany z zewnątrz, zmienia go sam obiekt w wyniku operacji.
- Komendy są bezstanowe: `releaseId` jest argumentem `execute`, nie polem komendy.
- Jedno miejsce mapowania konfiguracji na Strategy: brak `new ExpeditedCostPolicy()` rozsianych po kodzie.
- Testy różnicowe porównują wyniki i wyjątki (klasa i komunikat) wersji przed i po.

### Typowe błędy uczestników

- Wybór Strategy dla stanu wydania ("bo to też `switch`"). Pytanie kontrolne: kto zmienia wariant i kiedy?
- Wybór State dla rodzaju kroku. Rodzaj kroku się nie zmienia, więc to polimorfizm.
- Przeniesienie walidacji wartości ujemnej do każdej strategii (duplikacja, możliwość rozjazdu).
- Pusta domyślna implementacja w interfejsie stanu zamiast wyjątku.
- Przechowywanie `releaseId` w polu komendy (dane poprzedniego żądania).
- Usunięcie `switch` przed migracją wszystkich miejsc tworzenia.
- Przeoczenie, że wybór strategii w konstruktorze zamraża go na czas życia obiektu.
- Tylko porównanie wyniku, bez porównania wyjątków i stanu po błędzie.

### Pytania do dyskusji

- Czy dla dwóch wariantów kosztu Strategy się opłaca? Co musiałoby się zmienić, żeby prosty `switch` był lepszy?
- Co się stanie, gdy dodamy nową stałą do `DeploymentAction`? Gdzie zobaczymy błąd: w kompilacji czy przy starcie?
- Gdyby tryb kosztu był odczytywany z konfiguracji przy każdym wywołaniu, jak zachować moment wyboru?

---

## Ćwiczenie 2: State, Observer i Decorator

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod referencyjny

- `src/main/java/pl/training/module6/state/after/Release.java` - State z tabelą przejść,
- `src/main/java/pl/training/module6/decorator/after/AuditedDeploymentRunner.java`, `BasicDeploymentRunner.java`, `DeploymentRunner.java` - audyt jako dekorator,
- `src/main/java/pl/training/module6/observer/after/ReleasePublisher.java`, `Subscription.java`, `ReleaseListener.java`, `ReleasePublished.java` - publikacja,
- testy: `StateEquivalenceTest`, `DecoratorEquivalenceTest`, `ObserverContractTest`.

Kod referencyjny pokazuje trzy wzorce osobno. Połączenie ich (w tym wybór kolejności) jest właściwym produktem ćwiczenia.

### Krok 1: gdzie umieścić audyt

Rekomendacja zgodna z kodem referencyjnym: audyt jako dekoracja operacji (`AuditedDeploymentRunner` opakowujący rdzeń wdrożenia), a nie część obiektu stanu.

- Obiekty State są bezstanowymi stałymi enumu i nie powinny wykonywać efektów zewnętrznych ani przechowywać danych wydania.
- Audyt jest opcjonalnym dodatkiem wokół odpowiedzialności podstawowej, co jest definicją embellishment (sekcja 7.1).
- Powiadomienie odbiorców po udanym wdrożeniu to osobne zdarzenie (`ReleasePublished`) publikowane przez Observer.

Dopuszczalny wariant: audyt jako listener zdarzenia. Wtedy jednak traci wpisy `start` i `failure` (listener dowiaduje się tylko o sukcesie), więc nie jest równoważny z `LegacyDeploymentRunner`.

### Krok 2: kolejność

Wybrany kontrakt musi być zapisany jawnie. Przykładowa kolejność dla `deploy` (zgodna z zasadą "efekt, potem zmiana stanu po sukcesie", wariant 1 z sekcji 8.3):

Sukces:

1. `audit: start:<id>` (dekorator),
2. wykonanie rdzenia wdrożenia,
3. zmiana stanu `APPROVED -> DEPLOYED`,
4. `audit: success:<id>` (dekorator),
5. publikacja `ReleasePublished(<id>)` do listenerów w kolejności rejestracji.

Awaria rdzenia:

1. `audit: start:<id>`,
2. wyjątek rdzenia,
3. stan pozostaje `APPROVED`,
4. `audit: failure:<id>:<TypWyjątku>`,
5. ten sam wyjątek propagowany dalej, brak publikacji.

Niedozwolone przejście (np. `deploy` w `DRAFT`): `IllegalStateException("cannot deploy release in state DRAFT")`, stan bez zmian, brak publikacji. Czy audyt ma zapisać taką próbę, to decyzja do zapisania.

Inne kolejności (stan przed efektem, outbox) są dopuszczalne, jeśli zostaną jawnie zatwierdzone. W refaktoryzacji należy zachować dotychczasowy moment zmiany stanu.

### Krok 3: listener usuwający inną subskrypcję

Wzorzec testu: `ObserverContractTest.unsubscriptionDuringPublicationAffectsTheNextSnapshot`.

```java
Subscription[] later = new Subscription[1];
publisher.subscribe(event -> {
    calls.add("first:" + event.releaseId());
    later[0].close();
});
later[0] = publisher.subscribe(event -> calls.add("later:" + event.releaseId()));
publisher.publish(new ReleasePublished("one"));
publisher.publish(new ReleasePublished("two"));
assertEquals(List.of("first:one", "later:one", "first:two"), calls);
```

Przy `CopyOnWriteArrayList` usunięty listener jest jeszcze wywołany w bieżącej publikacji (migawka), ale nie w następnej.

### Krok 4-5: listener rzucający wyjątek i polityka

Kod referencyjny stosuje fail-fast: pierwszy wyjątek przerywa pętlę, wyjątek jest propagowany bez opakowania (`usesFailFastExceptionPolicy`: `assertSame(failure, propagated)`, drugi listener niewywołany).

Należy udokumentować, że w momencie wyjątku listenera stan wydania jest już `DEPLOYED` (publikacja następuje po zatwierdzeniu zmiany). Wywołujący dostaje wyjątek mimo udanego wdrożenia. Jeżeli to niepożądane, alternatywą jest kontynuacja z agregacją błędów, ale to jest zmiana kontraktu, którą wprowadza się w osobnym kroku.

### Krok 6: testy po awarii

- awaria audytu `start`: rdzeń nie został wykonany, stan bez zmian,
- awaria audytu `failure`: zasłania pierwotny wyjątek rdzenia. Kod referencyjny tego nie rozwiązuje; decyzja (przerwanie, `addSuppressed`, kanał awaryjny) musi być jawna (sekcja 7.2),
- awaria audytu `success`: w kodzie referencyjnym `success` jest w bloku `try`, więc zostanie zapisany dodatkowo `failure` i wyjątek poleci do wywołującego; stan zależy od przyjętej kolejności (w kolejności z kroku 2 już `DEPLOYED`),
- awaria listenera: stan `DEPLOYED`, wyjątek u wywołującego, kolejne listenery niewywołane,
- podwójne `close()` uchwytu: brak błędu i brak usunięcia cudzej rejestracji (`AtomicBoolean.compareAndSet`), wzorzec `duplicateRegistrationsRemainIndependentSubscriptions`.

### Weryfikacja kryteriów akceptacji

- Brak blokady wokół wywołania listenerów: `ReleasePublisher` nie używa `synchronized`, iteruje po migawce.
- Idempotentne zamykanie: `AtomicBoolean` w lambdzie `Subscription`.
- Test ustala zachowanie usuniętego listenera w bieżącej publikacji: test z kroku 3.
- Brak asynchroniczności: publikacja w tym samym wątku, żadnego `Executor`.
- Błąd dodatku nie zasłania przyczyny bez decyzji: jawnie opisana polityka dla awarii audytu `failure`.

### Typowe błędy uczestników

- Umieszczenie wywołania audytu lub publikacji w obiekcie stanu (enum), a nawet danych wydania w polu enumu (współdzielone przez wszystkie wydania).
- Publikacja pod `synchronized` na obiekcie wydania (ryzyko zakleszczenia, reentrancja).
- Przejście z dwóch odbiorców na kolekcję i jednoczesna zmiana polityki błędów w jednym kroku.
- Wyrejestrowanie przez `remove(listener)` zamiast przez uchwyt; przy duplikatach usuwa nie tę rejestrację.
- Opakowanie wyjątku listenera w nowy wyjątek (zmiana tożsamości wyjątku).
- `ExecutorService` "dla wydajności" (zmiana wątku, transakcji, propagacji błędów).
- Brak testu stanu po wyjątku.

### Pytania do dyskusji

- Czy listener powinien widzieć stan przed, czy po zatwierdzeniu zmiany? Co z transakcją bazodanową?
- Kiedy potrzebny byłby outbox zamiast synchronicznej publikacji?
- Czy audyt ma dotyczyć próby wdrożenia, czy przejścia stanu? Jak to wpływa na kolejność dekoratorów przy retry?

---

## Ćwiczenie 3: rodzina refaktoryzacji Composite

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

### Kod referencyjny

- `src/main/java/pl/training/module6/composite/after/PlanComponent.java`, `DeploymentTask.java`, `DeploymentGroup.java` - model,
- `src/main/java/pl/training/module6/composite/after/LegacyPathPlanMapper.java` - mapper,
- `src/main/java/pl/training/module6/composite/after/PlanExecutor.java` - jeden kontrakt,
- `src/main/java/pl/training/module6/composite/after/PlanBuilder.java` - Builder,
- `src/main/java/pl/training/module6/composite/after/PlanVisitor.java`, `TotalMinutesVisitor.java` - Visitor,
- test: `src/test/java/pl/training/module6/CompositeRefactoringsTest.java`.

### Krok 1: charakterystyka starego formatu

Na podstawie `LegacyPathPlan`:

- separator `/`, brak mechanizmu maskowania,
- ścieżka niepoprawna: pusta, biała, zaczynająca się lub kończąca `/`, zawierająca `//`, z białym segmentem (`invalid path: ...`),
- ścieżka zadania musi mieć rodzica (`task path must contain a parent`),
- minuty nieujemne (`minutes must not be negative`),
- suma z `Math.addExact` (przepełnienie rzuca `ArithmeticException`),
- `taskNamesBelow(prefix)` zwraca nazwy w kolejności listy wejściowej, dopasowanie przez `startsWith(prefix + "/")`,
- lista wejściowa jest kopiowana (`List.copyOf`), `null` jest odrzucany.

### Krok 2: model

`PlanComponent` jako `sealed interface ... permits DeploymentTask, DeploymentGroup` z operacjami `name()`, `totalMinutes()`, `collectTasks(...)`, `accept(PlanVisitor<R>)`. Liść `DeploymentTask(name, minutes)` z walidacją; grupa `DeploymentGroup(name, components)` z `List.copyOf` i sumowaniem przez `Math.addExact`.

### Krok 3: mapper

`LegacyPathPlanMapper.map(rootName, entries)`:

- jawne `rootName` (pozwala odwzorować pusty plan; musi być jednym segmentem bez `/`),
- wpis spoza korzenia: błąd `entry is outside root ...`,
- ta sama nazwa jako zadanie i grupa: błąd `path is both a task and a group at ...`,
- powrót do zamkniętej grupy (`release/a/first`, `release/b/second`, `release/a/third`): błąd `entries are not in depth-first order: ...`, ponieważ zgrupowanie zmieniłoby kolejność zadań,
- powtórzone zadania są zachowane,
- budowa na mutowalnych węzłach pomocniczych, a na końcu `freeze()` do niemutowalnych rekordów.

Decyzja o błędzie dla danych niezgodnych z przejściem w głąb jest świadoma. Alternatywy (sortowanie, pole kolejności, zmiana kontraktu) to zmiany zachowania.

### Krok 4: jeden lub wiele

`PlanExecutor.execute(PlanComponent)` zbiera liście przez `collectTasks` i mapuje na `executed:<name>`. Zastępuje `execute(Task)` i `executeAll(List<Task>)`. Kolejność: preorder zgodny z kolejnością dzieci. Pusta grupa: pusta lista.

### Krok 5: Builder

`PlanBuilder.group(name).task(...).group(name, g -> ...).build()`:

- jednorazowy (`builder has already been used` po `build()`, sprawdzane przed walidacją argumentów),
- dziecko budowane synchronicznie wewnątrz `group(name, definition)`,
- wynik niemutowalny dzięki `List.copyOf` w konstruktorze `DeploymentGroup`.

### Krok 6-7: Collecting Parameter i Visitor

- `collectTasks(Collection<? super DeploymentTask> target)`: liść dodaje siebie, grupa przekazuje ten sam akumulator dzieciom; metoda tylko dopisuje, nie czyści; po wyjątku może zostać wynik częściowy.
- `TotalMinutesVisitor implements PlanVisitor<Long>`: `visitTask` zwraca minuty, `visitGroup` sumuje `component.accept(this)` przez `Math.addExact`. Każdy węzeł implementuje `accept`, więc brak łańcucha `instanceof`.

### Krok 8: przypadki brzegowe

- puste drzewo: `emptyCompositeHasNeutralBehavior` (suma 0, Visitor 0, pusta lista wykonania, pusty Builder równy pustemu mapperowi),
- przepełnienie: `bothAccumulationImplementationsDetectOverflow` (`Long.MAX_VALUE` + 1 daje `ArithmeticException` w obu akumulacjach),
- głęboka struktura: rekurencja może skończyć się `StackOverflowError`; dla danych niezaufanych limit głębokości lub przejście iteracyjne (w kodzie referencyjnym brak testu; uczestnicy powinni zapisać decyzję),
- współdzielone poddrzewo: przez publiczny konstruktor `DeploymentGroup` ten sam podplan może wystąpić dwa razy; `totalMinutes()` policzy go dwukrotnie (DAG). Test powinien to utrwalić albo domena musi to zabronić jawnie.

### Test różnicowy (wzorzec z projektu)

`CompositeRefactoringsTest.explicitCompositePreservesTheImplicitPathTree` porównuje `LegacyPathPlan` i wynik mappera: 16 minut, nazwy `backup`, `migrate`, `deploy`, topologię (`database`, `deploy`), zapytania dla korzenia i dla `release/database`. Zawiera też niezależne oczekiwania, bo samo porównanie przed/po może utrwalić błąd obu implementacji.

### Weryfikacja kryteriów akceptacji

- niemutowalny wynik Buildera: `builderIsSingleUseAndBuiltCompositeIsImmutable`,
- jawna kolejność: preorder według kolejności dzieci, mapper odrzuca dane, dla których kolejność by się zmieniła,
- brak zawijania wartości: `Math.addExact` w obu akumulacjach,
- Visitor obsługuje wszystkie rodzaje węzłów: `sealed PlanComponent` i dwie metody `visit`,
- format trwały bez zmian: `LegacyPathPlan.Entry` nadal istnieje, mapper jest warstwą migracyjną.

### Typowe błędy uczestników

- Zbudowanie drzewa ręcznie w teście zamiast przez mapper ze starego formatu (test nie sprawdza transformacji).
- Ciche sortowanie lub grupowanie zmieniające kolejność zadań.
- Rekord `DeploymentGroup` bez `List.copyOf` (płytka niemutowalność).
- Transparent Composite z `add` na liściu rzucającym wyjątek.
- Builder zwracający wewnętrzną listę albo dający się użyć ponownie.
- Zwykłe `+` zamiast `Math.addExact`.
- Collecting parameter czyszczący przekazaną kolekcję.
- Visitor z `instanceof` zamiast podwójnej dyspozycji.
- Zmiana formatu trwałego (np. zapis drzewa JSON) w tym samym kroku co zmiana modelu.

### Pytania do dyskusji

- Błąd czy sortowanie dla danych `a/b/a`? Kto powinien podjąć tę decyzję?
- Czy współdzielone poddrzewo to cecha, czy błąd domeny?
- Visitor czy metoda w węźle, jeśli nowe rodzaje węzłów pojawiają się częściej niż nowe operacje?
- Kiedy zastąpić rekurencję przejściem iteracyjnym?

---

## Odpowiedzi do pytań sprawdzających

Odpowiedzi pochodzą z sekcji 22 teorii ("Odpowiedzi skrócone"), z krótkim rozwinięciem na podstawie treści modułu.

1. **Dlaczego usunięcie `switch` nie wystarcza do uzasadnienia Strategy?**
   Strategy dotyczy wymiennego algorytmu, a warunek może reprezentować stan, typ, komendę albo zwykłą lokalną regułę. Najpierw trzeba ustalić, co jest zmienne i czy zestaw wariantów jest otwarty.

2. **Kiedy kod typu powinien stać się klasą wartości, a kiedy podtypem?**
   Klasa wartości pasuje do samego pojęcia i jego reguł (np. `DeploymentZone` z regułą zatwierdzenia). Podtyp pasuje do stabilnego rodzaju posiadającego własne zachowanie (np. `ScriptStep`, `ApprovalStep`). Gdy kod steruje przejściowym trybem, kandydatem jest State.

3. **Dlaczego wybór strategii w konstruktorze może zmienić zachowanie?**
   Może zamrozić decyzję wcześniej niż dawny warunek odczytywany przy każdym wywołaniu (np. zmienna konfiguracja).

4. **Co odróżnia State od Strategy?**
   Strategy wykonuje wybrany algorytm, a State reprezentuje bieżący stan i wyznacza dozwolone przejścia. Strategię zwykle wybiera klient, stan zmienia sam obiekt.

5. **Dlaczego kolejność Decoratorów jest częścią kontraktu?**
   Warstwy zmieniają zasięg retry, audytu, transakcji i obsługi wyjątków. `audit(retry(core))` zapisze jedną operację, `retry(audit(core))` każdą próbę.

6. **Jak zachowuje się iterator `CopyOnWriteArrayList` po usunięciu listenera podczas callbacku?**
   Iterator używa migawki, więc usunięcie wpływa na kolejną iterację, nie na już utworzoną. Usunięty listener może zostać wywołany jeszcze w bieżącej publikacji.

7. **Kiedy przejście od jednego odbiorcy do kolekcji Observerów jest rozszerzeniem?**
   Gdy stary kontrakt gwarantował dokładnie jeden efekt, a nowy dopuszcza dodatkowe efekty i nowe błędy.

8. **Co musi tłumaczyć Adapter oprócz nazw metod?**
   Semantykę danych, jednostki, `null`, błędy, własność zasobów, idempotencję i model wykonania (a także strefę czasu, kodowanie, kolejność i indeksowanie, mutowalność).

9. **Kiedy mapy komend nie można uznać za równoważną łańcuchowi warunków?**
   Gdy predykaty się nakładają, liczy się ich kolejność albo wybór nie jest pojedynczym kluczem.

10. **Dlaczego rekord z komponentem `List` nie jest automatycznie głęboko niemutowalny?**
    Referencja rekordu jest finalna, ale wskazany obiekt może być mutowalny. Potrzebna jest kopia lub niemutowalny typ (stąd `List.copyOf` w `DeploymentGroup`).

11. **Jaki wymiar zmian preferuje klasyczny Visitor?**
    Częste dodawanie nowych operacji przy stabilnym zestawie rodzajów elementów.

12. **Czym Extract Composite różni się od Replace Implicit Tree with Composite?**
    Pierwsza wydziela powieloną obsługę dzieci z istniejącej hierarchii (`ReleaseGroup`, `RollbackGroup` do `CompositePlanNode`). Druga zastępuje prymitywną, ukrytą reprezentację (ścieżki tekstowe) jawnym drzewem obiektów.

13. **Dlaczego enum Singleton nie oznacza bezwzględnie jednej instancji w całym procesie?**
    Każdy niezależny loader klas może mieć własną definicję i własną stałą enumu.

14. **Jakie zachowanie trzeba ustalić dla częściowego błędu podczas akumulacji?**
    Czy wynik częściowy zostaje, jest wycofywany, czy błąd jest agregowany i przetwarzanie trwa.

15. **Kiedy prosty `switch` jest lepszy od wzorca?**
    Gdy warunek jest mały, lokalny, stabilny i czytelniejszy niż dodatkowe typy.
