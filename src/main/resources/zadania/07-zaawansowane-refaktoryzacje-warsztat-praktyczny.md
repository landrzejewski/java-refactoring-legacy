# Moduł 7. Zaawansowane refaktoryzacje - warsztat praktyczny

## Zadania dla uczestników

Ten zestaw zawiera trzy warsztaty główne oraz opcjonalne ćwiczenia uzupełniające do bloków tematycznych. Każde zadanie wskazuje kod wejściowy z repozytorium szkoleniowego. Pracujesz wyłącznie na wersjach wyjściowych (pakiety `before`, klasy `Legacy*`).

## Jak pracować

### Zasady ogólne

- Refaktoryzacja zachowuje obserwowalne zachowanie w przyjętej granicy. Zanim zmienisz strukturę, ustal, co jest częścią kontraktu: wartość zwracana i tożsamość obiektu, typ, komunikat i moment wyjątku, stan po sukcesie i po błędzie, liczba i kolejność wywołań współpracowników, kolejność sprawdzania warunków, aliasowanie mutowalnych obiektów, zgodność publicznego API.
- Pracuj w pętli: nazwij przeszkodę, zapisz przykłady (ścieżka poprawna, granice, awarie), wykonaj najmniejszy ruch, skompiluj, uruchom zawężone testy, powtórz.
- Każdą zmianę kontraktu (nowa walidacja, inny typ wyjątku, kopia defensywna) wykonuj jako osobny, nazwany krok i opisz ją w notatce do rozwiązania.
- Nie przeglądaj pakietów `after` ani plików testów równoważności przed zakończeniem pracy. Porównanie z wersją referencyjną nastąpi podczas przeglądu.

### Przygotowanie środowiska

1. Utwórz własną gałąź: `git switch -c warsztat-07-<twoje-inicjały>`.
2. Nie modyfikuj oryginalnych klas `Legacy*`. Skopiuj klasę wejściową do nowego pakietu, np. `pl.training.module7.workshop.w1`, i refaktoryzuj kopię. Oryginał pozostaje wyrocznią w testach różnicowych (porównanie "stara wersja vs nowa wersja").
3. Własne testy umieszczaj w `src/test/java/pl/training/module7/workshop/`.
4. Uruchamianie testów modułu (Java):

   ```bash
   mvn test -Dtest='pl.training.module7.**'
   ```

5. Pracując w C# lub TypeScript, korzystaj z odpowiedników w `csharp/src/Training.Module7` (testy: `cd csharp && dotnet test`) lub `typescript/src/module7` (testy: `cd typescript && npm test`). Nazwy klas i pakietów są analogiczne.

### Harmonogram modułu

Sugerowany czas pracy synchronicznej: 360 minut.

| Część | Czas |
| --- | ---: |
| model zachowania i Break Dependencies | 40 minut |
| Method Object, odpowiedzialności i duplikacja | 60 minut |
| Break Method i Parameter Object | 45 minut |
| Arrowhead, kontrakty i podwójne zaprzeczenia | 60 minut |
| God Class, flagi i Middle Man | 55 minut |
| Return ASAP i migracja API | 25 minut |
| ćwiczenia warsztatowe (Warsztaty 1-3) | 60 minut |
| przegląd rozwiązań i podsumowanie | 15 minut |

Na trzy warsztaty główne przypada łącznie 60 minut (orientacyjnie po 20 minut na warsztat, jeśli prowadzący nie ustali inaczej). Ćwiczenia uzupełniające wykonujesz w ramach bloków tematycznych, jeśli prowadzący je zleci.

---

## Warsztat 1. Utworzenie seam przed zmianą integracji

### Cel

Przygotować usługę, która sama tworzy klienta zewnętrznego, do testowanej zmiany reguły biznesowej.

### Czas

Orientacyjnie 20 minut (część bloku "ćwiczenia warsztatowe").

### Kontekst

Serwis planowania wdrożeń decyduje, czy usługa może zostać wdrożona o danej godzinie UTC. Decyzję deleguje do kalendarza okien serwisowych. Kalendarz jest tworzony wewnątrz serwisu. W tym ćwiczeniu traktuj `StandardMaintenanceWindows` jak klienta zewnętrznego systemu (w rzeczywistości mógłby łączyć się z usługą sieciową), którego nie chcesz uruchamiać w testach.

### Kod wejściowy

- `src/main/java/pl/training/module7/breakdependencies/before/LegacyDeploymentWindowService.java`
- `src/main/java/pl/training/module7/breakdependencies/before/StandardMaintenanceWindows.java`
- `src/main/java/pl/training/module7/breakdependencies/DeploymentDecision.java`

### Nowa reguła biznesowa do wprowadzenia (krok 5)

Usługi znajdujące się na liście zamrożonych (na potrzeby ćwiczenia: `billing`) nie mogą być wdrażane o żadnej godzinie. Serwis zwraca dla nich `DeploymentDecision.OUTSIDE_MAINTENANCE_WINDOW`. Prowadzący może podać inną regułę.

### Polecenia

1. Dodaj testy charakterystyki wyniku oraz kolejności wywołań istniejącej wersji: decyzje dla wszystkich godzin 0-23, granice okna, wszystkie ścieżki walidacji (typ i komunikat wyjątku, także dla kombinacji kilku błędnych argumentów).
2. Zidentyfikuj najmniejszą operację, której serwis faktycznie potrzebuje od kalendarza.
3. Wprowadź punkt podstawienia bez zmiany produkcyjnego składania (produkcja nadal ma używać dotychczasowej implementacji kalendarza).
4. Dodaj obiekt typu spy rejestrujący argumenty i liczbę wywołań. Sprawdź nim, co serwis przekazuje do kalendarza i kiedy go nie wywołuje.
5. Dopiero potem zaimplementuj nową regułę biznesową i pokryj ją testami.
6. Zapisz w notatce, które kroki były refaktoryzacją, a który zmianą zachowania.

### Kryteria akceptacji

- produkcyjna implementacja jest nadal wybierana w głównym miejscu składania zależności, czyli composition root,
- test nie uruchamia infrastruktury,
- nowy kontrakt nie zawiera metod nieużywanych przez usługę,
- liczba i kolejność wywołań są jawnie sprawdzone,
- testy charakterystyki z kroku 1 przechodzą dla wersji po refaktoryzacji bez zmian w ich asercjach,
- nowa reguła biznesowa została dodana w osobnym kroku (osobny commit).

---

## Warsztat 2. Dekompozycja długiej metody

### Cel

Wybrać między Extract Method, Split Phase i Extract Method Object na podstawie przepływu danych.

### Czas

Orientacyjnie 20 minut. Część A jest obowiązkowa, część B wykonujesz, jeśli wystarczy czasu (albo zespoły dzielą się częściami).

### Kontekst

Część A: budowniczy manifestu wydania waliduje wpisy, porządkuje je według kolejności wdrożenia i nazwy artefaktu, a następnie renderuje tekst manifestu. Wszystko dzieje się w jednej metodzie publicznej.

Część B: kalkulator ryzyka wdrożenia sumuje punkty ryzyka, uwzględnia redukcję za przetestowany rollback, ogranicza wynik do zakresu i klasyfikuje poziom ryzyka. Obliczenie operuje na jednej mutowanej zmiennej roboczej.

### Kod wejściowy

Część A:

- `src/main/java/pl/training/module7/breakmethod/before/LegacyReleaseManifestBuilder.java`
- `src/main/java/pl/training/module7/breakmethod/ManifestEntry.java`

Część B:

- `src/main/java/pl/training/module7/methodobject/before/LegacyDeploymentRiskCalculator.java`
- `src/main/java/pl/training/module7/methodobject/DeploymentRiskInput.java`
- `src/main/java/pl/training/module7/methodobject/RiskAssessment.java`
- `src/main/java/pl/training/module7/methodobject/RiskLevel.java`

### Polecenia

1. Narysuj (na kartce lub w komentarzu) wejścia, wyjścia i mutowane lokalne wartości kolejnych fragmentów metody.
2. Dodaj testy różnicowe porównujące wersję wyjściową z Twoją kopią: wynik, typ i komunikat wyjątków, kolejność zgłaszania błędów przy wielu niepoprawnych polach, przypadki brzegowe (pusta lista, równe klucze sortowania, granice klasyfikacji, wartości skrajne typu `int`).
3. Dodaj test sprawdzający, że lista przekazana przez wywołującego nie zostaje zmodyfikowana (część A).
4. Wydziel najpierw krok z najmniejszą liczbą zależności.
5. Utrzymaj metodę nadrzędną na jednym poziomie abstrakcji.
6. Jeżeli listy argumentów zaczynają rosnąć, oceń Method Object. Uzasadnij na piśmie wybór techniki dla części A i dla części B.
7. Porównaj wynik, wyjątki i brak modyfikacji wejściowych kolekcji.

### Kryteria akceptacji

- nazwy metod opisują intencję, nie mechanikę,
- kolejność kroków pozostaje jawna,
- obiekt metody, jeśli powstał, nie jest współdzielony między wywołaniami,
- refaktoryzacja nie dodaje nowej walidacji,
- testy różnicowe przechodzą dla wszystkich przygotowanych przypadków, w tym dla wartości skrajnych,
- lista wejściowa wywołującego ma po wywołaniu tę samą zawartość i kolejność,
- wybór techniki (Extract Method, Split Phase, Method Object) jest uzasadniony przepływem danych.

---

## Warsztat 3. Stopniowe rozbijanie God Class

### Cel

Wydzielić jedną odpowiedzialność bez przepisywania centralnej klasy i bez zmiany kolejności efektów.

### Czas

Orientacyjnie 20 minut.

### Kontekst

Menedżer wydań waliduje dane, przechowuje opublikowane wydania, prowadzi dziennik audytu, wysyła powiadomienia i rejestruje ślad zdarzeń. Każda zmiana w dowolnym z tych obszarów przechodzi przez jedną klasę. Klienci korzystają z metod `publish`, `releases`, `auditEntries`, `notifications` i `events`, dlatego ich sygnatury muszą pozostać dostępne.

### Kod wejściowy

- `src/main/java/pl/training/module7/godclass/before/LegacyReleaseManager.java`
- `src/main/java/pl/training/module7/godclass/PublishedRelease.java`

### Polecenia

1. Zbuduj mapę metod do pól i integracji (które metody czytają lub modyfikują które pola, jakie efekty zewnętrzne powstają i w jakiej kolejności).
2. Wskaż jeden klaster o samodzielnym powodzie zmiany. Zapisz, dlaczego wybrałeś właśnie ten.
3. Dodaj test śladu operacji dla sukcesu i awarii: kolejność efektów przy poprawnej publikacji, brak efektów przy błędzie walidacji, brak nowych efektów przy próbie ponownej publikacji tego samego identyfikatora.
4. Wydziel najmniejszy pionowy fragment do nowej klasy.
5. Pozostaw stare API jako delegujące.
6. Oceń granicę transakcji i własność stanu. Opisz, co dzieje się ze stanem, jeśli wydzielony krok zgłosi wyjątek, i dodaj test tej sytuacji.
7. Zaproponuj (bez implementacji) kolejne dwa kroki kampanii.

### Kryteria akceptacji

- stan ma jednego właściciela,
- nie powstał cykl zależności,
- walidacja zachowuje kolejność oraz komunikaty,
- po błędzie nie wykonują się późniejsze kroki, a wcześniejsze efekty odpowiadają scharakteryzowanemu kontraktowi,
- kolejne kroki kampanii można wdrażać niezależnie,
- publiczne metody klasy wyjściowej zwracają te same wartości co przed zmianą (w tym niemodyfikowalne kopie kolekcji).

---

## Ćwiczenia uzupełniające (opcjonalne)

Ćwiczenia są krótkie (5-15 minut) i przeznaczone do wykonania w ramach bloków tematycznych harmonogramu. Dla każdego obowiązuje ta sama zasada: najpierw test różnicowy wersji wyjściowej, potem zmiana struktury na kopii, na końcu porównanie.

### Ćwiczenie A. Break Responsibilities

- Kod wejściowy: `src/main/java/pl/training/module7/breakresponsibilities/before/LegacyDeploymentReport.java` (rekordy pomocnicze w `src/main/java/pl/training/module7/breakresponsibilities/`).
- Polecenie: rozdziel obliczanie metryk wdrożeń od formatowania raportu tekstowego. Zachowaj metodę `generate` jako punkt wejścia.
- Kryteria: identyczny tekst raportu (także dla pustej listy i średniej całkowitoliczbowej), identyczne wyjątki dla `null` listy, `null` elementu i przepełnienia sumy, brak modyfikacji listy wejściowej, każda nowa klasa ma jeden powód zmiany.

### Ćwiczenie B. Remove Duplication

- Kod wejściowy: `src/main/java/pl/training/module7/duplication/before/LegacyArtifactPublisher.java`.
- Polecenie: usuń duplikację wiedzy między publikacją snapshotu i wydania, zachowując dwie publiczne metody.
- Kryteria: identyczne wyniki niezależnie od domyślnego `Locale` (sprawdź co najmniej `tr-TR`), identyczny typ, komunikat i kolejność wyjątków dla obu wariantów, różnica między wariantami pozostaje jawna w kodzie.

### Ćwiczenie C. Introduce Parameter Object

- Kod wejściowy: `src/main/java/pl/training/module7/parameterobject/before/LegacyRolloutPlanner.java`.
- Polecenie: wprowadź obiekt parametru dla powtarzającej się grupy argumentów. Zaplanuj migrację publicznego API tak, aby stare i nowe sygnatury mogły czasowo współistnieć.
- Kryteria: identyczne wyniki `estimateSeconds` i `describe` (także dla `Integer.MAX_VALUE` instancji), jawnie opisany moment zgłaszania wyjątków walidacji przed i po zmianie, przeniesienie walidacji (jeśli nastąpiło) wykonane jako osobny krok.

### Ćwiczenie D. Remove Arrowhead Antipattern

- Kod wejściowy: `src/main/java/pl/training/module7/arrowhead/before/LegacyDeploymentEligibility.java`.
- Polecenie: spłaszcz zagnieżdżone warunki.
- Kryteria: test obejmuje pełną macierz kombinacji flag oraz przypadki kilku jednocześnie niespełnionych warunków, priorytet wyników nie zmienia się, każdy krok zmienia jeden poziom zagnieżdżenia.

### Ćwiczenie E. Introduce Design by Contract Checks

- Kod wejściowy: `src/main/java/pl/training/module7/contract/before/LegacyDeploymentCapacity.java`.
- Polecenie: sformułuj warunki wstępne, warunki końcowe i niezmiennik puli slotów wdrożeniowych, a następnie wprowadź jawne kontrole.
- Kryteria: poprawne sekwencje operacji dają te same wyniki co wersja wyjściowa, naruszenie warunku wstępnego nie zmienia stanu obiektu, kontrole nie opierają się na instrukcji `assert`, testy rozdzielają równoważność dla poprawnych danych od nowego kontraktu dla niepoprawnych danych.

### Ćwiczenie F. Remove Double Negative

- Kod wejściowy: `src/main/java/pl/training/module7/doublenegative/before/LegacyReleaseReadiness.java`, `src/main/java/pl/training/module7/doublenegative/before/LegacyReleaseGate.java`.
- Polecenie: zastąp negatywne nazwy właściwości pozytywnymi. Zaplanuj migrację klientów.
- Kryteria: wszystkie 8 wierszy tabeli prawdy daje ten sam wynik, zachowany wyjątek dla `null`, opisany wpływ zmiany na ewentualne granice systemu (JSON, baza danych, konfiguracja).

### Ćwiczenie G. Remove Boolean Method Parameters

- Kod wejściowy: `src/main/java/pl/training/module7/booleanparameter/before/LegacyDeploymentExecutor.java`.
- Polecenie: usuń parametr sterujący z publicznego API.
- Kryteria: obie gałęzie dają te same wyniki co wcześniej, walidacja identyczna dla obu wariantów, publiczne API nie zawiera parametru `boolean`, opisany plan okresu zgodności dla starej sygnatury.

### Ćwiczenie H. Remove Middle Man

- Kod wejściowy: `src/main/java/pl/training/module7/middleman/before/ReleaseService.java`, `src/main/java/pl/training/module7/middleman/before/ReleaseDashboard.java`, `src/main/java/pl/training/module7/middleman/before/DeploymentRegistry.java`.
- Polecenie: oceń, czy pośrednik chroni wartościową granicę. Jeśli nie, migruj klienta do bezpośredniej współpracy.
- Kryteria: pisemna ocena (autoryzacja, transakcje, telemetria, retry, translacja błędów), identyczne renderowanie i walidacja, migracja jednego klienta naraz.

### Ćwiczenie I. Return ASAP

- Kod wejściowy: `src/main/java/pl/training/module7/returnasap/before/LegacyArtifactFinder.java`.
- Polecenie: usuń zmienną wyniku i zmienną sterującą pętli, zwracając wynik w miejscu, w którym staje się ostateczny.
- Kryteria: zwracana jest ta sama instancja (pierwsze dopasowanie), zachowane zachowanie dla elementu `null` przed i po dopasowaniu, zachowany ślad dostępu do listy (liczba i kolejność wywołań `size()` i `get(index)`).

---

## Oddanie pracy

Na przegląd przygotuj:

1. link do gałęzi lub listę commitów (jeden commit = jeden krok),
2. listę testów charakterystyki i różnicowych,
3. krótką notatkę: co było refaktoryzacją, co zmianą kontraktu, jakie ryzyka pozostały i jakie są kolejne kroki.
