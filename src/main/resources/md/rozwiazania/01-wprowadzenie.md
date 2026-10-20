# Moduł 1. Wprowadzenie - rozwiązania dla prowadzącego

Materiał tylko dla prowadzącego. Numeracja i nazwy zadań odpowiadają plikowi zadań (`src/main/resources/md/zadania/01-wprowadzenie.md`), a odwołania do slajdów - plikowi `src/main/resources/md/slides/01-wprowadzenie-slajdy.md`.

**Uwaga ogólna:** moduł 1 nie zawiera kodu „after”. Kod w `src/main/java/pl/training/module1/` jest materiałem diagnostycznym, a testy w `src/test/java/pl/training/module1/` są testami charakteryzującymi obecne zachowanie. Odniesienia do kodu referencyjnego w tym pliku wskazują więc miejsca, które ilustrują odpowiedź, a nie gotową refaktoryzację.

**Zasada oceniania we wszystkich zadaniach:** nagradzamy trafne rozróżnienie faktu od hipotezy i jawne nazwanie niewiadomych. Nie nagradzamy szybkiego proponowania wzorców ani liczb, które nie wynikają z materiału.

---

## Aktywność 1.1: Nazwij kontrakt

### Rozwiązanie

| Zmiana | Konsument | Możliwa obserwacja | Weryfikacja | Ocena |
| --- | --- | --- | --- | --- |
| 1. prywatne pole `List` na `Set` | metody publiczne zwracające lub przetwarzające elementy, serializacja pól (np. Jackson, serializacja Javy), `equals`/`hashCode` | usunięcie duplikatów, utrata kolejności (np. `HashSet`), inny format po serializacji, inna złożoność operacji | testy z duplikatami i z kolejnością elementów na publicznej granicy, test serializacji | refaktoryzacja tylko wtedy, gdy duplikaty są niemożliwe lub nieistotne, kolejność nie jest kontraktem, a pole nie jest serializowane; w przeciwnym razie zmiana zachowania |
| 2. zamiana dwóch zapisów do bazy | baza (klucze obce, wyzwalacze, ograniczenia), mechanizmy CDC i audytu, inne transakcje | kolejność efektów ubocznych (protokół), stan po częściowej awarii, naruszenie ograniczeń, zakleszczenia | test integracyjny z rzeczywistą bazą, test zachowania transakcyjnego, test interakcji, jeśli kolejność jest kontraktem | zwykle nie jest czystą refaktoryzacją, dopóki nie wykaże się, że oba zapisy są w jednej transakcji i nic nie zależy od ich kolejności |
| 3. przeniesienie klasy używanej przez refleksję | konfiguracja tekstowa, `Class.forName`, serializacja z nazwą typu, kontener DI, zewnętrzne pliki | pełna nazwa klasy (pakiet + nazwa) jest częścią kontraktu; błąd pojawia się dopiero w runtime | wyszukanie nazwy w konfiguracji i zasobach, test uruchamiający rzeczywisty mechanizm (np. start kontekstu, deserializacja zapisanych danych) | IDE nie zobaczy wszystkich odwołań; refaktoryzacja tylko po aktualizacji wszystkich konsumentów i zgodności zapisanych danych |
| 4. szybszy algorytm | wywołujący kod, użytkownicy, SLA | wynik (stabilność sortowania, remisy, zaokrąglenia zmiennoprzecinkowe, przypadki brzegowe), czas i pamięć | test różnicowy stary kontra nowy na wielu danych, testy wartości granicznych, osobny pomiar wydajności | zwykle optymalizacja (slajd 1.3); wynik funkcjonalny musi być identyczny, a zmiana właściwości pozafunkcjonalnej jest celem, więc trzeba ją nazwać |
| 5. prywatna nazwa bez refleksji i konfiguracji | praktycznie brak (ewentualnie logi ze stosem wywołań) | brak obserwacji na granicy publicznej | automatyczna zmiana nazwy w IDE, kompilacja, istniejące testy | refaktoryzacja |

### Kod referencyjny

Aktywność jest koncepcyjna. Ilustracją zachowania obserwowalnego innego niż wynik jest `src/main/java/pl/training/module1/LegacyOrderService.java`: zapis przez `OrderRepository` następuje przed wysłaniem wiadomości przez `MailGateway`, więc zamiana tych dwóch wywołań miejscami zmieniłaby stan po awarii bramki pocztowej (przypadek 2 w małej skali).

### Weryfikacja kryteriów akceptacji

- Każdy wiersz ma konsumenta i obserwację przed oceną - jeśli uczestnicy zaczynają od kolumny „Ocena”, poproś o uzupełnienie pierwszych kolumn.
- Ocena „zależy od kontraktu” jest poprawna dla pozycji 1-3, o ile nazwany jest warunek.

### Typowe błędy uczestników

- uznanie zmiany kolejności zapisów za „nieistotną wewnętrzną kolejność”,
- pominięcie serializacji przy polu prywatnym,
- uznanie szybszego algorytmu za refaktoryzację bez nazwania zmiany wydajności,
- klasyfikowanie od razu („to refaktoryzacja”) bez kolumn konsument, obserwacja, weryfikacja.

### Pytania do dyskusji

- Pozycje 1 i 5 wyglądają na „wewnętrzne” - dlaczego tylko 5 jest bezpieczna bez dodatkowej analizy?
- Kiedy kolejność dwóch zapisów staje się kontraktem?
- Co w Waszym systemie korzysta z nazw klas zapisanych tekstowo?

---

## Aktywność 1.2: Brakujące pola rekordu

### Rozwiązanie

**Co zawiera zapis zespołu:** częściowo lokalizację („import”, bez wskazania komponentu ani procesu), częściowo konstrukcję („brak testów”, bez zakresu) i coś, co wygląda na kapitał („trzy dni”), ale bez podstawy oszacowania i bez definicji, co oznacza „naprawa”.

| Pole | Źródło informacji | Komentarz |
| --- | --- | --- |
| Przyczyna | dane historyczne, wiedza domenowa | historia repozytorium, rozmowa z autorami; nie zakładamy zaniedbania |
| Konsekwencja | dane historyczne | np. czas ręcznej regresji, liczba zmian odrzuconych lub opóźnionych |
| Dowód | dane historyczne | incydenty, regresje, wycofane wdrożenia związane z importem |
| Zaobserwowane odsetki | dane historyczne | dodatkowy nakład poniesiony przy ostatnich zmianach importu |
| Ekspozycja | decyzja właściciela produktu (plan rozwoju) | czy i jak często import będzie zmieniany |
| Kapitał bieżący (weryfikacja „trzech dni”) | eksperyment techniczny | np. ograniczona czasowo próba napisania pierwszych testów charakteryzujących |
| Ryzyko spłaty | eksperyment techniczny, wiedza domenowa | czy import da się uruchomić na kontrolowanych danych; nieznane reguły formatów |
| Decyzja | decyzja właściciela produktu | spłacić, ograniczyć, tolerować, obserwować |
| Poziom pewności | wynika z jakości zebranych dowodów | jawnie „niski”, dopóki brak danych |
| Właściciel i termin przeglądu | decyzja właściciela produktu / zespołu | kto i kiedy wraca do tematu |

Wystarczająca odpowiedź: co najmniej cztery pola z tej listy z poprawnie przypisanym źródłem.

### Kod referencyjny

Brak - aktywność dotyczy rejestru długu. Pełny przykład wypełnionego rekordu znajduje się niżej w rozwiązaniu Ćwiczenia 3 (`PricingService`).

### Weryfikacja kryteriów akceptacji

- cztery lub więcej pól z typem źródła z listy czterech,
- brak „dopisanych” wartości - wpis „brak danych” jest poprawny.

### Typowe błędy uczestników

- „dopisywanie” wartości (np. „konsekwencja: dużo regresji”) bez danych,
- przyjęcie „trzech dni” jako kapitału bez pytania, czy obejmuje testy, wdrożenie i odzyskanie wiedzy (slajd 2.6: oszacowanie jest przedziałem, nie obietnicą),
- pomijanie ekspozycji: bez planu zmian nie wiadomo, czy odsetki w ogóle będą naliczane.

### Pytania do dyskusji

- Czy „brak testów” jest zawsze długiem? (Tylko gdy zwiększa przyszły koszt lub ryzyko zmian, np. wymusza powtarzalną ręczną regresję.)
- Jaki najtańszy eksperyment zweryfikuje oszacowanie „trzech dni”?

---

## Aktywność 1.3: Wybór najmniejszego kroku

### Rozwiązanie

| Krok | Jaki problem rozwiązuje | Czy problem jest potwierdzony w opisie |
| --- | --- | --- |
| Pełne przepisanie systemu | całą konstrukcję systemu | nie: problem dotyczy jednego modułu; brak danych o całości; długi okres bez wartości, ryzyko utraty zachowań (slajd 2.10) |
| Wymiana platformy | wsparcie i możliwości platformy | nie: platforma jest wspierana przez dwa lata; regresje wynikają z braku testów, nie z platformy |
| Testy charakteryzujące moduł | brak szybkiej informacji zwrotnej w module, który zmienia się co tydzień i powoduje większość regresji | tak |

**Rekomendowany krok:** testy charakteryzujące krytyczny moduł.

- **Założenie do weryfikacji:** regresje wynikają głównie z braku szybkiej weryfikacji zachowania modułu, a jego zachowanie da się uchwycić testami na kontrolowanych danych przez istniejącą granicę.
- **Miernik wyniku:** liczba poprawek po wydaniu przypadająca na zmianę modułu; czas uzyskania wiarygodnego wyniku testów modułu. (Nie: „liczba napisanych testów”, „procent pokrycia”, bo to mierniki aktywności.)
- **Warunek przerwania:** np. jeżeli po ustalonym czasie (np. dwóch tygodniach) nie udaje się uruchomić modułu na kontrolowanych danych albo testy są niestabilne, przerywamy i oceniamy inną granicę testów (np. wyższy poziom). Jeżeli po kilku cyklach wydań poprawki się nie zmniejszają, założenie o przyczynie regresji jest błędne i trzeba wrócić do diagnozy.

### Kod referencyjny

`src/test/java/pl/training/module1/LegacyOrderServiceTest.java` to przykład testu charakteryzującego: utrwala obecny wynik (236.39) i interakcje z repozytorium oraz bramką pocztową, bez oceniania, czy wynik jest „poprawny”. Technika testów charakteryzujących jest rozwijana w module 3.

### Weryfikacja kryteriów akceptacji

- wybór odwołuje się do faktów scenariusza (moduł zmienia się co tydzień, brak szybkich testów, platforma wspierana),
- miernik opisuje efekt, warunek przerwania ma termin lub sprawdzalny sygnał.

### Typowe błędy uczestników

- wybór pełnego przepisania z uzasadnieniem „i tak kiedyś trzeba” (sygnał ostrzegawczy ze slajdu 2.10),
- miernik aktywności zamiast wyniku,
- warunek przerwania niesprawdzalny („jeśli nie wyjdzie”).

### Pytania do dyskusji

Jaka nowa informacja po pierwszym kroku mogłaby uzasadnić wymianę platformy lub modułu? (Np. potwierdzone ograniczenie platformy, niemożność testowania w izolacji, planowane zmiany wykraczające poza model modułu.)

---

## Aktywność 1.4: Fakt czy ocena

### Rozwiązanie

| Nr | Stwierdzenie | Kategoria | Uzasadnienie |
| --- | --- | --- | --- |
| 1 | Metoda ma 142 linie i złożoność cyklomatyczną równą 24. | fakt (pomiar) + symptom | Wynik pomiaru, sprawdzalny, ale zależny od narzędzia i konfiguracji. Duży rozmiar i złożoność to sygnał możliwego problemu (zapach Long Method), nie dowód problemu. |
| 2 | Metoda jest niemożliwa do utrzymania. | ocena / hipoteza | Brak faktu, kontekstu, konsekwencji i dowodu. W dosłownym brzmieniu zwykle nieprawdziwe (metoda jest utrzymywana, skoro działa w produkcji). |
| 3 | Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty. | fakt + defekt | Zaobserwowana niezgodność zachowania z zatwierdzonym wymaganiem. Nie jest to automatycznie dług techniczny. |
| 4 | Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy. | fakt + ograniczenie (i źródło ryzyka) | Decyzja strony trzeciej, której zespół nie kontroluje. Może stać się ryzykiem bezpieczeństwa i zgodności, jeżeli brak ścieżki aktualizacji. |

Przykład przepisania stwierdzenia 2 jako diagnozy:

- **Fakt:** metoda `X` ma 142 linie i złożoność cyklomatyczną 24 (narzędzie Y, konfiguracja Z); łączy wycenę, zapis i wysyłkę powiadomień.
- **Kontekst:** w następnym kwartale planujemy w niej trzy zmiany reguł cenowych.
- **Konsekwencja:** każda zmiana wymaga analizy wielu niezależnych decyzji, a regresja może dotknąć zapisu i powiadomień; wydłuża to czas zmiany i zwiększa ryzyko poprawek po wydaniu.
- **Potrzebny dowód:** historia zmian tego pliku, czas realizacji ostatnich zmian, liczba regresji i poprawek po wydaniu powiązanych z metodą, pokrycie gałęzi zmienianych decyzji. (Wartości nieznane zostawiamy jako do zebrania.)

### Kod referencyjny

`LegacyOrderService.placeOrder` to miniatura stwierdzenia 1: metoda łączy kilka odpowiedzialności. Diagnoza w czterech częściach dla niej to materiał Ćwiczenia 1.

### Weryfikacja kryteriów akceptacji

- każde stwierdzenie ma kategorię i jedno zdanie uzasadnienia,
- diagnoza nie używa słów „brzydki”, „zły”, „nieczytelny” jako argumentu,
- dowód jest listą danych do zebrania.

### Typowe błędy uczestników

- klasyfikowanie stwierdzenia 1 jako „problem” lub „dług” (to fakt i symptom, konsekwencja nie jest wykazana),
- traktowanie stwierdzenia 3 jako długu technicznego (defekt wymaga osobnego zarządzania, slajd 2.5),
- w diagnozie wpisanie wymyślonych liczb jako dowodu,
- pomijanie kontekstu: diagnoza bez planowanej zmiany nie wskazuje, dlaczego problem ma znaczenie teraz.

### Pytania do dyskusji

- Czy stwierdzenie 1 może być prawdziwe, a mimo to metoda nie wymaga żadnej interwencji? (Tak: np. stabilny kod bez planowanych zmian, slajd 2.1.)
- Czy stwierdzenie 4 jest długiem technicznym? (Nie z samego faktu; staje się kandydatem, gdy wykażemy konsekwencję i ekspozycję. Por. `OldPaymentAdapter` w Ćwiczeniu 3.)

---

## Aktywność 1.5: Dobór metryk

### Rozwiązanie

Wybrane wskaźniki:

| Wskaźnik | Pytanie | Ograniczenie interpretacji |
| --- | --- | --- |
| liczba poprawek po wydaniu przypadająca na zmianę reguł | czy zmiany reguł cenowych rzeczywiście powodują regresje i jak często | normalizacja na zmianę jest konieczna, bo bezwzględna liczba regresji rośnie wraz z liczbą wdrożeń; nie wskazuje przyczyny |
| współzmienność plików zawierających reguły cenowe | czy jedna reguła jest rozproszona i czy kopie zmieniają się razem | wskazuje ryzyko pominięcia kopii, ale nie dowodzi wspólnej semantyki (por. `SalesCalculations`) |
| pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami | czy testy wykonują obie strony zmienianych decyzji | ujawnia niewykonane warianty, ale nie ocenia jakości danych i oczekiwanych wyników (por. `DiscountPolicy`: 100 procent linii, 50 procent gałęzi) |

Odrzucone wskaźniki:

- globalne pokrycie linii całej aplikacji: może rosnąć poza obszarem wyceny, uśrednia ryzyko (antywzorzec ze slajdu 4.8),
- maksymalna złożoność dowolnej metody w systemie: nie odpowiada na pytanie o przyczyny regresji w aktywnie zmienianych regułach.

Uwaga prowadzącego: „liczba klas w repozytorium” jest oczywiście nieprzydatna dla tego celu. Jeżeli uczestnicy odrzucą ją zamiast jednego z dwóch powyższych, uznaj odpowiedź, o ile uzasadnią, dlaczego pozostały wskaźnik globalny jest słaby. Oba odrzucone wskaźniki mogą służyć innym analizom, ale nie temu celowi.

### Kod referencyjny

- `src/main/java/pl/training/module1/DiscountPolicy.java` + `src/test/java/pl/training/module1/DiscountPolicyTest.java` (pokrycie linii a gałęzi),
- `src/main/java/pl/training/module1/SalesCalculations.java` (współzmienność kopii reguły VIP),
- `src/test/java/pl/training/module1/Module1ExamplesTest.java` (pokrycie bez asercji).

### Weryfikacja kryteriów akceptacji

- wybrane wskaźniki dotyczą reguł cenowych,
- każdy wybrany wskaźnik ma nazwane ograniczenie.

### Typowe błędy uczestników

- wybór globalnego pokrycia „bo to standard” bez pytania, czy mierzy obszar reguł cenowych,
- pominięcie słów „z istotnymi asercjami”: pokrycie bez asercji nie mówi nic o wykrywaniu regresji (`Module1ExamplesTest` jest tu dobrym kontrprzykładem),
- brak ograniczeń przy wybranych wskaźnikach.

### Pytania do dyskusji

Jaki cel (inny niż regresje w wycenie) uzasadniałby użycie maksymalnej złożoności lub globalnego pokrycia?

---

## Ćwiczenie 1: Obserwacja bez pochopnej naprawy

### Krok 1: fakty, hipotezy, dowody

Tabela jest jedną z możliwych odpowiedzi.

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
| warunki dotyczące `customerType` | typ klienta jest reprezentowany przez `String`, a metoda sprawdza wyłącznie wartość `VIP` | dodanie `PARTNER` może rozbudować zestaw warunków i ujawnić nieznane zasady łączenia rabatów | zatwierdzone przykłady cen dla każdego typu klienta, dane o aktualnie używanych wartościach, testy istniejących rabatów | nowy typ `PARTNER` |
| warunki dotyczące `destinationCountry` | jawne stawki istnieją tylko dla `PL` i `DE`, a każda inna wartość daje podatek równy zero | zero może oznaczać zarówno poprawną stawkę, jak i nieobsługiwany kraj ukryty jako poprawny wynik | wymagania podatkowe, kontrakt metody, lista krajów z danych produkcyjnych, zachowanie oczekiwane dla nieznanej wartości | obsługa Czech |
| obliczanie `shipping` | metoda rozróżnia tylko przesyłkę ekspresową i standardową; koszt zależy także od sumy po rabatach | parametr `boolean` nie pozwoli jednoznacznie reprezentować odbioru osobistego, a próg bezpłatnej wysyłki może nie dotyczyć tego wariantu | reguły dostawy, przykłady graniczne dla kwoty 200,00, decyzja o kolejności rabatu i naliczania wysyłki | odbiór osobisty |
| pętla po pozycjach | rabat VIP i rabat ilościowy są składane przez mnożenie, bez pośredniego zaokrąglania | rabaty mogą zgodnie z wymaganiem składać się multiplikatywnie albo sumować jako punkty procentowe; etap zaokrąglania także może być istotny | przykłady zatwierdzone przez właściciela produktu, istniejące faktury i testy charakteryzujące przypadki łączenia rabatów | typ `PARTNER` może mieć odmienną politykę rabatową |
| końcowa część `placeOrder` | jedna metoda oblicza kwotę, zapisuje wynik i wysyła wiadomość; zapis następuje przed wysłaniem | awaria bramki pocztowej może pozostawić zapisane zamówienie, mimo że metoda zakończy się wyjątkiem | kontrakt transakcyjny, sposób obsługi wyjątków przez wywołującego, logi awarii i zasady ponawiania | każda z planowanych zmian wymaga bezpiecznej regresji całego przebiegu |
| walidacja wejścia | sprawdzana jest obecność zamówienia i co najmniej jednej pozycji, ale nie są jawnie sprawdzane liczba sztuk, cena, adres ani kod kraju | niedozwolone wartości mogą docierać z wcześniejszej warstwy albo nie być kontrolowane wcale | kontrakty wejścia, walidacja w kontrolerze lub modelu, próbki błędnych żądań | nowe wartości zwiększą liczbę wariantów wejścia |

### Krok 2: pytania domenowe

- Czy brak stawki oznacza nieobsługiwany kraj, stawkę zerową czy sprzedaż zwolnioną z podatku?
- Czy awaria wiadomości powinna cofnąć zapis zamówienia, czy wysyłka ma być ponawiana niezależnie?
- Czy rabaty mają być składane multiplikatywnie, czy sumowane jako punkty procentowe, i na którym etapie należy zaokrąglać kwoty?
- Czy próg bezpłatnej wysyłki jest liczony przed rabatem czy po nim?
- Czy odbiór osobisty zawsze ma koszt zerowy?

Dodatkowe fakty widoczne w kodzie (przydatne, gdy grupa ma mało obserwacji):

- podatek liczony jest od `subtotal`, bez kosztu wysyłki,
- przesyłka ekspresowa kosztuje zawsze 39.99, także gdy suma przekracza próg darmowej wysyłki,
- rabat ilościowy dotyczy pojedynczej pozycji (`line.quantity() >= 10`), nie całego zamówienia,
- zaokrąglenie `HALF_UP` do dwóch miejsc następuje tylko raz, dla sumy końcowej,
- wartości reguł (`0.90`, `0.95`, `10`, `39.99`, `200.00`, `14.99`, `0.23`, `0.19`) są literałami bez nazw,
- zależności `OrderRepository` i `MailGateway` są przekazywane przez konstruktor jako interfejsy.

Każdy z tych faktów rodzi pytanie domenowe, a nie wniosek „to błąd”.

**Kluczowy komunikat:** stwierdzenie „podatek jest liczony błędnie” nie jest faktem widocznym w kodzie. Bez reguły biznesowej pozostaje hipotezą. Najlepszy pierwszy krok zwiększa wiedzę lub kontrolę nad zachowaniem, nie musi tworzyć docelowej architektury.

### Kod referencyjny

- `src/main/java/pl/training/module1/LegacyOrderService.java` - materiał ćwiczenia.
- `src/test/java/pl/training/module1/LegacyOrderServiceTest.java` realizuje rozsądny pierwszy krok: test charakteryzujący przez publiczną metodę z implementacjami zastępczymi (`OrderRepository`, `MailGateway` jako lambdy). Po ćwiczeniu pokaż go i policz z grupą 236.39 (200.00 x 0.90 = 180.00; wysyłka 14.99; podatek 41.40).
- Zapytaj, których wierszy tabeli ten test nie chroni (ekspres, próg 200.00, DE i inne kraje, rabat ilościowy, klient bez VIP, walidacja).
- Rozsądna sekwencja kolejnych kroków: testy charakteryzujące, uzgodnienie przykładów z właścicielem produktu, odseparowanie obliczeń od zapisu i wysyłki, precyzyjne testy wyodrębnionych reguł, dopiero potem decyzja o strukturze.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| co najmniej pięć faktów | każdy fakt wskazuje linię lub fragment kodu |
| trzy hipotezy oddzielone od faktów | hipoteza zawiera słowo „może” albo opisuje ryzyko, nie stan |
| lista pytań domenowych | pytania, na które kod nie odpowiada (np. kraj nieobsługiwany kontra stawka zero) |
| dowód adekwatny do hipotezy | dla reguły podatkowej dowodem jest wymaganie, a nie „ponowne czytanie kodu” |
| brak docelowej architektury | w arkuszu nie ma słów Strategy, enum, Extract Class |

### Typowe błędy uczestników

- zapisywanie ocen jako faktów („kod jest nieczytelny”, „podatek jest źle liczony”),
- proponowanie wzorca Strategia, enumów lub podziału klas mimo zakazu,
- stwierdzenie, że klasa „nie jest testowalna” (jest: interfejsy w konstruktorze, wynik zwracany z metody),
- dowody nieadekwatne do hipotezy (np. „przeczytać kod jeszcze raz” jako dowód reguły podatkowej),
- brak powiązania symptomu z konkretnym elementem planowanej zmiany (Czechy, `PARTNER`, odbiór osobisty).

### Pytania do dyskusji

- Który z symptomów stanie się istotny dopiero przy planowanej zmianie, a który jest ryzykiem już dziś (zapis przed wysłaniem wiadomości)?
- Czy dla Czech wystarczy „dodać kolejny `if`”? Co musimy wiedzieć przedtem?
- Jaki najmniejszy krok dostarczy najwięcej wiedzy?

---

## Ćwiczenie 2: Od zapachu do kierunku zmiany

> Rozwiązanie opracowane na podstawie kodu referencyjnego. Tabela jest jedną z możliwych odpowiedzi; oceniaj powiązanie zapachu z utrudnioną zmianą, a nie zgodność nazw co do słowa.

### Krok 1: zapachy, dowody, utrudnione zmiany

| Klasa / fragment | Zapach (grupa) | Dowód w kodzie | Utrudniona zmiana | Możliwy kierunek | Decyzja i uzasadnienie |
| --- | --- | --- | --- | --- | --- |
| `LegacyOrderService.placeOrder` | Long Method (Bloaters) | walidacja, wycena, rabaty, wysyłka, podatek, zaokrąglenie, zapis i wiadomość w jednej metodzie | każda z trzech zmian wymaga regresji całego przebiegu | Extract Method, Compose Method | **przy planowanej zmianie**, po testach charakteryzujących: refaktoryzacja przygotowawcza („najpierw ułatw zmianę”) |
| parametr `customerType` | Primitive Obsession (Bloaters) | `"VIP".equals(customerType)`; dowolny napis jest legalny | `PARTNER`: literówka przejdzie bez błędu, brak listy dozwolonych typów | Replace Type Code with Class | **przy planowanej zmianie** (przed dodaniem `PARTNER`); bez tej zmiany typ jako napis nikomu dziś nie szkodzi |
| `destinationCountry` i łańcuch `if` krajów | Primitive Obsession + Conditional Complexity | `"PL"`, `"DE"`, a każdy inny kraj daje podatek zero | Czechy: kolejny `if` i niejawne zachowanie dla nieznanego kraju | tabela 3.6 podpowiada Strategy; prostszy kierunek: jawna tabela stawek (Replace Data Value with Object) | **przy planowanej zmianie**; stawki to dane, nie różne algorytmy - wzorzec byłby nadmiarem, decyzja o nieznanym kraju należy do domeny |
| parametr `boolean express` | Switch Statements w postaci argumentu-flagi (Object-Orientation Abusers) | `if (express) ... else if (subtotal >= 200.00) ...` | odbiór osobisty: trzeci wariant nie mieści się w `boolean` | Replace Type Code with Class; Strategy dopiero, gdy koszty dostawy staną się różnymi algorytmami | **teraz lub przy zmianie** - planowana zmiana wymusza trzeci wariant, więc sygnatura i tak się zmieni |
| literały `0.90`, `0.95`, `39.99`, `200.00`, `14.99`, `0.23`, `0.19` | liczby bez nazw (wspierają Primitive Obsession) | literały w regułach bez nazwy domenowej | każda zmiana stawki wymaga odnalezienia znaczenia liczby | Extract Constant (Replace Magic Number with Symbolic Constant) | **przy planowanej zmianie** jako mała poprawa w dotykanym kodzie |
| `LegacyOrderService` jako całość | Divergent Change (Change Preventers) | ta sama klasa zmienia się z powodu cennika, podatków, logistyki i komunikacji z klientem | trzy zmiany różnych aktorów w jednym pliku | Extract Class | **przy planowanej zmianie**, gdy testy charakteryzujące chronią przebieg; nie przed nimi |
| `SalesCalculations.invoiceLineTotal` / `quoteLineTotal` i `0.90` w `LegacyOrderService` | Duplicated Code (Dispensables) | identyczne metody, ta sama liczba w trzech miejscach | zmiana rabatu VIP (nieplanowana); `PARTNER` może mieć inny rabat | Extract Method, ewentualnie Form Template Method | **nie teraz - zebrać informacje**: najpierw ustalić z właścicielem, czy faktura i oferta korzystają z jednej reguły |
| `RiskClassifier.riskLevel` | brak zapachu wartego interwencji | `V(G) = 5`, metoda krótka i liniowa | żadna planowana zmiana jej nie dotyka | - | **nie**: wysoka liczba nie jest werdyktem (slajd 4.2) |

Couplers: w kodzie modułu 1 nie ma wyraźnego przykładu (brak łańcuchów wywołań i zawiści o dane). Jeśli grupa dopisze zapach z tej grupy „na siłę”, zapytaj o dowód w kodzie.

### Krok 2: zapach, którego świadomie nie usuwamy

Najczęstsza dobra odpowiedź: duplikacja w `SalesCalculations` albo złożoność `RiskClassifier`. W pierwszym przypadku decyzja zależy od wiedzy domenowej, w drugim metryka sugeruje problem, którego nie ma.

### Kod referencyjny

- `src/main/java/pl/training/module1/LegacyOrderService.java`,
- `src/main/java/pl/training/module1/SalesCalculations.java` i `src/test/java/pl/training/module1/SalesCalculationsTest.java` (nazwa testu z „currently” dokumentuje obecną zbieżność, nie wymaganie),
- `src/main/java/pl/training/module1/RiskClassifier.java`.

Refaktoryzacje i wzorce z kolumny „Możliwy kierunek” są wykonywane w modułach 4 i 5; tu tylko je nazywamy.

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| nazwa z katalogu, grupa, dowód | każdy wiersz wskazuje fragment kodu, a nazwa pochodzi ze slajdów 3.3-3.4 |
| kierunek powiązany z utrudnioną zmianą | kolumna „Utrudniona zmiana” jest wypełniona przed kolumną „Możliwy kierunek” |
| co najmniej jedna decyzja „nie” lub „przy planowanej zmianie” | najczęściej `SalesCalculations` lub `RiskClassifier` |
| brak kodu docelowego | arkusz zawiera nazwy refaktoryzacji, nie implementację |

### Typowe błędy uczestników

- traktowanie tabeli 3.6 jak algorytmu („jest `if`, więc Strategy”), bez pytania o utrudnioną zmianę,
- Strategy dla stawek podatkowych, które są danymi, a nie wariantami algorytmu,
- decyzja „teraz” dla wszystkiego, bez kosztu i bez sieci bezpieczeństwa,
- mylenie grup (Duplicated Code jako Change Preventer) - drugorzędne, jeśli dowód i decyzja są trafne,
- pominięcie zapachów na poziomie klasy (Divergent Change) i skupienie wyłącznie na liniach.

### Pytania do dyskusji

- Który zapach utrudnia najwięcej planowanych zmian naraz? (Long Method i Divergent Change - dlatego przygotowawcza refaktoryzacja po testach jest dobrym pierwszym ruchem.)
- Kiedy kierunek „od wzorca” byłby właściwy w tym kodzie? (Np. gdyby ktoś wcześniej wprowadził Strategy dla dwóch stawek podatkowych.)
- Czy nazwa zapachu pomogła w rozmowie, czy tylko zastąpiła słowo „zły”?

---

## Ćwiczenie 3: Klasyfikacja i priorytetyzacja długu

### Krok 1: klasyfikacja

| Kandydat | Fakt | Symptom | Potencjalny dług | Defekt | Ograniczenie | Pewność |
| --- | --- | --- | --- | --- | --- | --- |
| `PricingService` | istnieją trzy kopie reguły; dwukrotnie pominięto jedną z nich | duplikacja i wysoka złożoność | rozproszona reguła zwiększa koszt oraz ryzyko kolejnych zmian | brak dowodu na aktualnie błędny wynik | brak | wysoka dla konstrukcji i odsetek |
| `AnnualReportFormatter` | maksymalna złożoność wynosi 54; brak zmian od 22 miesięcy; wycofanie planowane za cztery miesiące | wysoka złożoność | brak wystarczającego dowodu na istotny przyszły koszt | brak danych | krótki pozostały okres życia | wysoka dla faktów, niska dla długu wymagającego spłaty |
| `OldPaymentAdapter` | wsparcie zależności kończy się za sześć miesięcy; nowy interfejs jest niezgodny | nie wynika z podanych metryk kodu | zależność może ograniczać przyszłą zmianę, ale konstrukcja i koszt pozostawienia wymagają rozpoznania | brak danych | decyzja dostawcy i niezgodny interfejs | wysoka dla ograniczenia, średnia dla długu |
| `CustomerExportController` | komentarz opisuje niezatwierdzoną funkcję | możliwy zbędny komentarz | brak wykazanego przyszłego kosztu | brak | brak | wysoka |
| `TaxRoundingRule` | implementacja używa `DOWN` zamiast wymaganego `HALF_UP`; błąd odtworzono | brak opisanego symptomu strukturalnego | brak danych o konstrukcji powodującej przyszły koszt | tak | brak | wysoka |

### Krok 2: pełny rekord `PricingService`

| Pole | Wartość |
| --- | --- |
| Lokalizacja | `PricingService`, trzy metody zawierające regułę VIP |
| Konstrukcja | trzy niezależne kopie tej samej reguły |
| Przyczyna | brak danych; do sprawdzenia w historii zmian i rozmowie z właścicielem |
| Konsekwencja | każda zmiana reguły wymaga zgodnej modyfikacji trzech miejsc i zwiększa ryzyko pominięcia |
| Dowód | dwa wdrożenia z pominiętą kopią, trzy regresje w ciągu pół roku |
| Zaobserwowane odsetki | co najmniej analiza i naprawa dwóch niespójnych wdrożeń; brak danych o liczbie osobodni |
| Ekspozycja | pięć podobnych zmian w planie rozwoju produktu |
| Kapitał bieżący | od 3 do 5 osobodni według podanego oszacowania |
| Ryzyko spłaty | kopie mogą zawierać zamierzone różnice; wspólna abstrakcja może połączyć reguły rozwijane niezależnie |
| Decyzja | zabezpieczyć przykłady zachowania i usunąć potwierdzoną duplikację przed kolejną zmianą VIP |
| Poziom pewności | wysoki dla potrzeby interwencji, średni dla docelowej konstrukcji |
| Właściciel i termin przeglądu | właściciel modułu wyceny; przegląd po analizie trzech kopii, nie później niż przed następną zmianą rabatu |

Rekord z jawną niepewnością dla `OldPaymentAdapter` (rejestrujemy jako ograniczenie i potencjalny dług, bez zgadywania):

| Pole | Wartość |
| --- | --- |
| Lokalizacja | granica integracji płatniczej i używana zależność |
| Konstrukcja | zależność od interfejsu, który nie obsługuje nowej metody płatności |
| Przyczyna | ewolucja interfejsu dostawcy; wcześniejsza decyzja mogła być poprawna |
| Konsekwencja | po utracie wsparcia mogą zniknąć poprawki i możliwość dodania nowej metody; dokładny wpływ nieznany |
| Dowód | ogłoszony termin końca wsparcia i potwierdzona niezgodność interfejsu |
| Zaobserwowane odsetki | brak danych |
| Ekspozycja | planowana nowa metoda płatności i termin sześciu miesięcy |
| Kapitał bieżący | od 4 do 7 osobodni, z nieznanym zakresem testów kontraktowych i wdrożenia |
| Ryzyko spłaty | zmiana zachowania integracji, niepełna zgodność danych, wymagania certyfikacyjne dostawcy |
| Decyzja | jednodniowe rozpoznanie ograniczeń i kontraktu, następnie ponowna priorytetyzacja |
| Poziom pewności | średni |
| Właściciel i termin przeglądu | właściciel integracji płatniczej; następny przegląd po rozpoznaniu, w tym samym tygodniu |

### Krok 3: budżet sześciu osobodni

| Nakład | Działanie | Oczekiwany rezultat |
| ---: | --- | --- |
| 1 osobodzień | poprawić `TaxRoundingRule` i dodać test regresyjny odtwarzający zatwierdzony przypadek | usunięty potwierdzony defekt |
| 4 osobodni | scharakteryzować trzy warianty reguły VIP, a po potwierdzeniu równoważności skonsolidować je i uruchomić testy | ograniczone odsetki w obszarze pięciu planowanych zmian |
| 1 osobodzień | wykonać ograniczone czasowo rozpoznanie `OldPaymentAdapter` | znany zakres wpływu końca wsparcia, kontraktu, certyfikacji i migracji |

Jeżeli interwencja w `PricingService` nie mieści się w czterech dniach, zespół kończy bezpieczny etap charakteryzacji i aktualizuje oszacowanie, zamiast przyspieszać zmianę kosztem weryfikacji. **Świadomie nie** refaktoryzuje `AnnualReportFormatter` (najgorsza metryka, ale brak zmian i wycofanie za cztery miesiące) i **nie** implementuje niezatwierdzonego eksportu z `CustomerExportController`.

### Krok 4: brakujące informacje

Koszt i termin biznesowy nowej metody płatności, dokładny zakres wsparcia dostawcy, aktywni konsumenci adaptera, semantyczne różnice między kopiami reguły VIP, czas obsługi regresji, krytyczność raportu rocznego oraz potwierdzenie terminu jego wycofania.

### Krok 5: nowe założenie dla `AnnualReportFormatter`

Przy aktywności przez dwa lata i zmianach co miesiąc wzrasta ekspozycja: około 24 zmian. Nie oznacza to automatycznej potrzeby pełnej refaktoryzacji, ale uzasadnia analizę struktury metody, historii podobnych zmian i ochrony testami. Przy potwierdzonym koszcie każdej zmiany formatter powinien zostać ponownie porównany z adapterem i modułem cenowym.

### Kod referencyjny

`src/main/java/pl/training/module1/SalesCalculations.java` i `src/test/java/pl/training/module1/SalesCalculationsTest.java` to miniatura problemu `PricingService`: ta sama reguła VIP (`0.90`) w dwóch metodach, a trzecia kopia w `LegacyOrderService`. Test o nazwie `duplicatedCalculationsCurrentlyProduceTheSameResult` jest przykładem „zabezpieczenia przykładów zachowania” przed konsolidacją. Pokaż go przy omawianiu decyzji dla `PricingService` i ryzyka spłaty („kopie mogą zawierać zamierzone różnice”).

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| suma nakładów nie przekracza sześciu osobodni | zsumuj kolumnę „Nakład” |
| plan wskazuje świadome rezygnacje | `AnnualReportFormatter` i `CustomerExportController` z uzasadnieniem |
| rekord bez wymyślonych wartości | pola bez danych mają wpis „brak danych” |
| ponowna ocena odnosi się do ekspozycji | uzasadnienie mówi o liczbie przyszłych zmian, a nie o samej złożoności |

### Typowe błędy uczestników

- priorytet dla `AnnualReportFormatter`, bo ma najwyższą złożoność (to dokładnie teza, którą ćwiczenie obala),
- zaliczenie `TaxRoundingRule` do długu (to defekt; brak danych o konstrukcji),
- zaliczenie `CustomerExportController` do długu lub zaplanowanie implementacji eksportu (brak zakresu produktu to nie dług, slajd 2.5),
- zignorowanie `OldPaymentAdapter`, bo „metryki są dobre” (ograniczenie środowiskowe nie wynika z metryk kodu),
- plan przekraczający sześć osobodni albo bez listy świadomych rezygnacji,
- wymyślanie wartości w rekordach długu zamiast „brak danych”.

### Pytania do dyskusji

- Dlaczego defekt `TaxRoundingRule` dostaje budżet, choć nie jest długiem?
- Co musiałoby się stać, żeby `OldPaymentAdapter` wyprzedził `PricingService`?
- Czy konsolidacja trzech kopii reguły VIP zawsze jest właściwa? Jak sprawdzić, że kopie są równoważne?
- Jak zespół powinien zapisać decyzję o niespłacaniu długu `AnnualReportFormatter`? (Zapowiedź świadomego rejestru długu z modułu 2.)

---

## Ćwiczenie 4: Wybór strategii modernizacji

**Teza wyjściowa:** sam przedstawiony materiał nie uzasadnia pełnego przepisania `ClaimsCore`. Oszacowanie nie obejmuje znacznej części kosztu, zachowanie jest częściowo niejawne, system jest krytyczny i musi równolegle ewoluować.

### Krok 1: rozważone strategie

1. **Pozostawić obecną architekturę**, skrócić zestaw testów dla modułu reguł i dodać testy charakteryzujące. Mały koszt i szybka poprawa informacji zwrotnej, ale nie musi zapewnić niezależnego wdrażania ani ograniczyć sprzężenia z resztą systemu.
2. **Przyrostowo wydzielać moduł reguł za jawnym kontraktem**, początkowo obliczając wyniki równolegle. Wykorzystuje istniejącą granicę, pozwala porównywać zachowanie i dostarczać korzyści kategoriami spraw.
3. **Przepisać cały `ClaimsCore`** i przełączyć system po osiągnięciu zgodności. Może usunąć część ograniczeń, ale ma niepełne oszacowanie, późno dostarcza wartość i wymaga równoległego odtwarzania zmian regulacyjnych, danych oraz 18 integracji.

### Krok 2: modelowy rekord decyzji (wersja skrócona)

**Problem:** najczęściej zmieniany moduł reguł ma wolną pętlę weryfikacji, niskie pokrycie gałęzi i odpowiada za większość poprawek po wydaniu. Pełny system musi zachować dostępność i nadal przyjmować zmiany regulacyjne.

**Decyzja i uzasadnienie:** przyrostowo wydzielać moduł reguł za jawnym kontraktem. Najpierw zbudować szybką weryfikację i uruchamianie równoległe, następnie przełączać pojedyncze kategorie spraw. Nie rozpoczynać pełnego przepisania systemu. Strategia bezpośrednio skraca pętlę weryfikacji reguły (czas wdrożenia) i ogranicza promień zmiany (poprawki po wydaniu).

**Pierwszy odwracalny krok (4 tygodnie):**

| Tydzień | Rezultat |
| --- | --- |
| 1 | jawny opis wejścia, wyjścia i efektów ubocznych; zestaw reprezentatywnych spraw uzgodniony z ekspertami; bazowe mierniki czasu wdrożenia i poprawek |
| 2 | szybki zestaw testów charakteryzujących kontrakt oraz automatyczne porównanie wyników dla danych historycznych |
| 3 | nowa implementacja jednej ograniczonej kategorii reguł, bez prawa do podejmowania decyzji produkcyjnych |
| 4 | uruchomienie równoległe dla kontrolowanej próbki, raport rozbieżności, wpływ na wydajność i decyzja o następnym kroku |

**Warunek przerwania albo zmiany kierunku:** po czterech tygodniach zatrzymać wydzielanie i ponownie ocenić strategię, jeżeli nie da się utworzyć powtarzalnego kontraktu bez wykonywania efektów ubocznych albo nie można wiarygodnie sklasyfikować rozbieżności. Dokładne progi zatwierdzają właściciele biznesowi i operacyjni.

**Mierniki wyniku:** mediana i 85. percentyl czasu od zatwierdzenia reguły do wdrożenia, odsetek zmian reguł wymagających poprawki po wydaniu, czas uzyskania wiarygodnego wyniku testów modułu, liczba niewyjaśnionych rozbieżności na tysiąc porównań, dostępność i czas odpowiedzi systemu. Liczba przepisanych klas, wykonanych zadań lub procent migracji to mierniki aktywności i nie dowodzą osiągnięcia celu.

Jeśli grupa ma czas, dopytaj o elementy pominięte w wersji skróconej: sposób weryfikacji zgodności (klasyfikacja rozbieżności: defekt nowej ścieżki, ujawniony defekt starej, zamierzona zmiana reguły, niewystarczające dane), plan wycofania (tryb obserwacyjny wyłączany flagą, brak nieodwracalnych zapisów w pierwszym kroku) i kryterium usunięcia architektury przejściowej (stara implementacja nie obsługuje żadnej kategorii przez dwa pełne cykle wydawnicze, potem usunięcie podwójnego obliczania, flag i adapterów). Techniki przyrostowej wymiany (Strangler Fig, Branch by Abstraction, równoległa weryfikacja) są omawiane w module 5.

### Kod referencyjny

Ćwiczenie jest scenariuszowe, bez kodu w projekcie. Przy omówieniu można nawiązać do `LegacyOrderService`: ten sam tok rozumowania w małej skali (testy charakteryzujące przez istniejącą granicę, potem separacja, potem decyzja o strukturze).

### Weryfikacja kryteriów akceptacji

| Kryterium | Jak sprawdzić |
| --- | --- |
| pięć elementów rekomendacji | szablon wypełniony we wszystkich polach |
| opcja inna niż refaktoryzacja i przepisanie | np. pozostawienie z ograniczeniem ryzyka albo przyrostowe wydzielanie |
| pierwszy krok w czterech tygodniach, odwracalny | brak migracji wspólnej bazy i nieodwracalnych zapisów |
| mierniki powiązane z celem | czas wdrożenia reguły, poprawki po wydaniu, dostępność |

### Typowe błędy uczestników

- tylko dwie opcje (refaktoryzacja albo przepisanie) zamiast co najmniej trzech,
- przyjęcie 18 miesięcy jako pełnego kosztu przepisania (brak migracji danych, integracji, równoległego utrzymania, wyłączenia; slajd 2.10),
- pierwszy krok dłuższy niż cztery tygodnie albo nieodwracalny (np. migracja wspólnej bazy),
- mierniki aktywności (liczba przepisanych klas, procent migracji) zamiast wyniku,
- ignorowanie wspólnej bazy z procesami raportowymi i zmian regulacyjnych w trakcie prac.

### Pytania do dyskusji

- Która informacja ze scenariusza najmocniej przemawia przeciw pełnemu przepisaniu?
- Jakie sygnały musiałyby wystąpić, żeby przepisanie modułu (a nie systemu) stało się zasadne?
- Kto w organizacji powinien zatwierdzić progi przerwania i dlaczego nie zespół deweloperski sam?

---

## Sprawdzenie wiedzy - odpowiedzi

1. **Czym refaktoryzacja różni się od zmiany funkcjonalnej i od przepisania?** Refaktoryzacja zmienia strukturę, zachowując nazwane obserwacje. Zmiana funkcjonalna celowo zmienia zachowanie. Przepisanie tworzy nową implementację, która nie dziedziczy automatycznie zachowań starej.
2. **Czy zachowanie obserwowalne to tylko wartość zwracana?** Nie. Obejmuje też wyjątki, efekty uboczne i ich kolejność, format danych, kontrakt publiczny oraz właściwości pozafunkcjonalne, jeśli są wymaganiem.
3. **Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?** Istotne są koszt i ryzyko zmiany, znaczenie systemu, testowalność oraz kontekst planowanych prac. Nowy kod także może być trudny do bezpiecznej zmiany.
4. **W jakich momentach pracy refaktoryzacja jest najłatwiejsza do uzasadnienia?** Przygotowawczo przed zmianą w trudnym miejscu, dla zrozumienia kodu, oportunistycznie w dotykanym kodzie i planowo małymi seriami w często zmienianym obszarze.
5. **Co oznaczają kapitał i odsetki w metaforze długu?** Kapitał to przybliżony koszt usunięcia lub ograniczenia konstrukcji, a odsetki to dodatkowy koszt ponoszony podczas kolejnych prac, które ją napotykają.
6. **Kiedy brak testów można opisać jako element długu technicznego?** Gdy brak wiarygodnej weryfikacji zwiększa przyszły koszt albo ryzyko zmian, na przykład wymusza powtarzalną ręczną regresję.
7. **Czym różni się symptom jakości od defektu?** Symptom wskazuje możliwy problem projektowy. Defekt oznacza zaobserwowaną niezgodność zachowania z wymaganiem.
8. **Do której grupy katalogu należy Shotgun Surgery i jaki kierunek refaktoryzacji podpowiada?** Do Change Preventers. Kierunek: zebrać rozproszoną wiedzę w jednym miejscu (Move Method, Move Field, Extract Class, czasem Inline Class), ewentualnie jeden punkt wejścia w postaci fasady.
9. **Dlaczego tabela „zapach -> wzorzec” nie rozstrzyga, czy refaktoryzować?** Bo zapach jest hipotezą, a decyzja zależy od planowanej zmiany, kosztu interwencji i sieci bezpieczeństwa. Tabela mówi, dokąd można iść, nie czy i kiedy.
10. **Co mierzy złożoność cyklomatyczna, a czego nie mierzy?** Opisuje liczbę liniowo niezależnych ścieżek wynikających ze struktury przepływu sterowania. Nie mierzy pełnej trudności domeny, czytelności ani wszystkich możliwych zachowań. (Ilustracja: `RiskClassifier.riskLevel`, V(G) = 5.)
11. **Dlaczego 100 procent pokrycia linii nie dowodzi poprawności testów?** Pokrycie potwierdza wykonanie kodu, ale nie jakość danych, asercji, modelu przypadków ani zdolność wykrywania ważnych defektów. (Ilustracja: `DiscountPolicyTest` daje 100 procent linii i 50 procent gałęzi; `Module1ExamplesTest` wykonuje cały kod bez asercji wyniku.)
12. **Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?** Podobne fragmenty mogą reprezentować różne reguły, które mają rozwijać się niezależnie. Wspólna abstrakcja stworzyłaby wtedy nieprawidłowe sprzężenie. (Ilustracja: `SalesCalculations`.)
13. **Co daje połączenie częstotliwości zmian ze złożonością?** Pomaga znaleźć aktywne obszary (*hotspoty*), w których trudna struktura faktycznie wpływa na bieżącą pracę. Każdy z tych sygnałów osobno daje słabszą podstawę decyzji.
14. **Jakie koszty pełnego przepisania są często pomijane?** Między innymi odzyskanie wymagań, utrzymanie dwóch systemów, migracja danych, integracje, testy niefunkcjonalne, operacje, szkolenia, przełączenie, wycofanie wdrożenia i wyłączenie starego rozwiązania.
