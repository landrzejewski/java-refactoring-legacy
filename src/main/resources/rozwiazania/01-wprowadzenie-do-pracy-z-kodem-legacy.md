# Moduł 1. Wprowadzenie do pracy z kodem legacy - rozwiązania dla prowadzącego

Materiał tylko dla prowadzącego. Numeracja i nazwy zadań odpowiadają plikowi zadań (`src/main/resources/prowadzenie/zadania/01-wprowadzenie-do-pracy-z-kodem-legacy.md`).

**Uwaga ogólna:** moduł 1 nie zawiera kodu „after”. Kod w `src/main/java/pl/training/module1/` jest materiałem diagnostycznym, a testy w `src/test/java/pl/training/module1/` są testami charakteryzującymi obecne zachowanie. Odniesienia do kodu referencyjnego w tym pliku wskazują więc miejsca, które ilustrują odpowiedź, a nie gotową refaktoryzację.

**Zasada oceniania we wszystkich zadaniach:** nagradzamy trafne rozróżnienie faktu od hipotezy i jawne nazwanie niewiadomych. Nie nagradzamy szybkiego proponowania wzorców ani liczb, które nie wynikają z materiału.

---

## Aktywność 1.1. Fakt czy ocena

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

(Opracowane na podstawie sekcji 2 i 2.1 teorii, która definiuje kategorie, ale nie podaje odpowiedzi do tej aktywności.)

| Nr | Stwierdzenie | Kategoria | Uzasadnienie |
| --- | --- | --- | --- |
| 1 | Metoda ma 142 linie i złożoność cyklomatyczną równą 24. | fakt (pomiar) + symptom | Wynik pomiaru, sprawdzalny, ale zależny od narzędzia i konfiguracji. Duży rozmiar i złożoność to sygnał możliwego problemu, nie dowód problemu. |
| 2 | Metoda jest niemożliwa do utrzymania. | ocena / hipoteza | Brak faktu, kontekstu, konsekwencji i dowodu. W dosłownym brzmieniu zwykle nieprawdziwe (metoda jest utrzymywana, skoro działa w produkcji). |
| 3 | Wynik dla zatwierdzonego przykładu różni się od wymaganej kwoty. | fakt + defekt | Zaobserwowana niezgodność zachowania z zatwierdzonym wymaganiem. Nie jest to automatycznie dług techniczny. |
| 4 | Producent środowiska wykonawczego zakończy dostarczanie poprawek za sześć miesięcy. | fakt + ograniczenie (i źródło ryzyka) | Decyzja strony trzeciej, której zespół nie kontroluje. Może stać się ryzykiem bezpieczeństwa i zgodności, jeżeli brak ścieżki aktualizacji. |

**Przykład przepisania stwierdzenia 2 jako diagnozy:**

- **Fakt:** metoda `X` ma 142 linie i złożoność cyklomatyczną 24 (narzędzie Y, konfiguracja Z); łączy wycenę, zapis i wysyłkę powiadomień.
- **Kontekst:** w następnym kwartale planujemy w niej trzy zmiany reguł cenowych.
- **Konsekwencja:** każda zmiana wymaga analizy wielu niezależnych decyzji, a regresja może dotknąć zapisu i powiadomień; wydłuża to czas zmiany i zwiększa ryzyko poprawek po wydaniu.
- **Potrzebny dowód:** historia zmian tego pliku, czas realizacji ostatnich zmian, liczba regresji i poprawek po wydaniu powiązanych z metodą, pokrycie gałęzi zmienianych decyzji. (Wartości nieznane zostawiamy jako do zebrania.)

**Typowe błędy uczestników:**

- klasyfikowanie stwierdzenia 1 jako „problem” lub „dług” (to fakt i symptom, konsekwencja nie jest wykazana),
- traktowanie stwierdzenia 3 jako długu technicznego (defekt wymaga osobnego zarządzania, sekcja 4.2),
- w diagnozie wpisanie wymyślonych liczb jako dowodu,
- pomijanie kontekstu: diagnoza bez planowanej zmiany nie wskazuje, dlaczego problem ma znaczenie teraz.

**Pytania do dyskusji:**

- Czy stwierdzenie 1 może być prawdziwe, a mimo to metoda nie wymaga żadnej interwencji? (Tak: np. stabilny kod bez planowanych zmian, sekcja 3.3.)
- Czy stwierdzenie 4 jest długiem technicznym? (Nie z samego faktu; staje się kandydatem, gdy wykażemy konsekwencję i ekspozycję. Por. `OldPaymentAdapter` w Ćwiczeniu 2.)

---

## Aktywność 1.2. Brakujące pola rekordu

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

(Opracowane na podstawie szablonu z sekcji 4.5 i przykładów rekordów z sekcji 10.2.)

**Co zawiera zapis zespołu:** częściowo lokalizację („import”, bez wskazania komponentu ani procesu), częściowo konstrukcję („brak testów”, bez zakresu) i coś, co wygląda na kapitał („trzy dni”), ale bez podstawy oszacowania i bez definicji, co oznacza „naprawa”.

**Brakujące pola i źródła informacji:**

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
| Poziom pewności | wynika z jakości zebranych dowodów | jawnie „niski” dopóki brak danych |
| Właściciel i termin przeglądu | decyzja właściciela produktu / zespołu | kto i kiedy wraca do tematu |

Wystarczająca odpowiedź: co najmniej cztery pola z tej listy z poprawnie przypisanym źródłem.

**Typowe błędy uczestników:**

- „dopisywanie” wartości (np. „konsekwencja: dużo regresji”) bez danych,
- przyjęcie „trzech dni” jako kapitału bez pytania, czy obejmuje testy, wdrożenie i odzyskanie wiedzy (sekcja 4.5: oszacowanie jest przedziałem, nie obietnicą),
- pomijanie ekspozycji: bez planu zmian nie wiadomo, czy odsetki w ogóle będą naliczane.

**Pytania do dyskusji:**

- Czy „brak testów” jest zawsze długiem? (Tylko gdy zwiększa przyszły koszt lub ryzyko zmian, np. wymusza powtarzalną ręczną regresję; Sprawdzenie wiedzy, pytanie 3.)
- Jaki najtańszy eksperyment zweryfikuje oszacowanie „trzech dni”?

---

## Aktywność 1.3. Dobór metryk

Odpowiedź opiera się na sekcji 10.2 teorii (punkt 5 Ćwiczenia 2 dotyczy tego samego celu i tej samej listy wskaźników) oraz sekcjach 6.4, 6.6, 6.8.

**Wybrane wskaźniki:**

| Wskaźnik | Pytanie | Ograniczenie interpretacji |
| --- | --- | --- |
| liczba poprawek po wydaniu przypadająca na zmianę reguł | czy zmiany reguł cenowych rzeczywiście powodują regresje i jak często | normalizacja na zmianę jest konieczna, bo bezwzględna liczba regresji rośnie wraz z liczbą wdrożeń; nie wskazuje przyczyny |
| współzmienność plików zawierających reguły cenowe | czy jedna reguła jest rozproszona i czy kopie zmieniają się razem | wskazuje ryzyko pominięcia kopii, ale nie dowodzi wspólnej semantyki (por. `SalesCalculations`) |
| pokrycie gałęzi zmienianych reguł przez testy z istotnymi asercjami | czy testy wykonują obie strony zmienianych decyzji | ujawnia niewykonane warianty, ale nie ocenia jakości danych i oczekiwanych wyników (por. `DiscountPolicy`: 100 procent linii, 50 procent gałęzi) |

**Odrzucone wskaźniki (wg teorii):**

- globalne pokrycie linii całej aplikacji: może rosnąć poza obszarem wyceny, uśrednia ryzyko (antywzorzec 6.8),
- maksymalna złożoność dowolnej metody w systemie: nie odpowiada na pytanie o przyczyny regresji w aktywnie zmienianych regułach.

Uwaga prowadzącego (uzupełnienie, nie pochodzi wprost z teorii): „liczba klas w repozytorium” jest oczywiście nieprzydatna dla tego celu. Jeżeli uczestnicy odrzucą ją zamiast jednego z dwóch powyższych, uznaj odpowiedź, o ile uzasadnią, dlaczego pozostały wskaźnik globalny jest słaby. Teoria podkreśla, że oba odrzucone wskaźniki „mogą służyć innym analizom”, ale nie temu celowi.

**Kod referencyjny do pokazania:**

- `src/main/java/pl/training/module1/DiscountPolicy.java` + `src/test/java/pl/training/module1/DiscountPolicyTest.java` (pokrycie linii a gałęzi),
- `src/main/java/pl/training/module1/SalesCalculations.java` (współzmienność kopii reguły VIP).

**Typowe błędy uczestników:**

- wybór globalnego pokrycia „bo to standard” bez pytania, czy mierzy obszar reguł cenowych,
- pominięcie słów „z istotnymi asercjami”: pokrycie bez asercji nie mówi nic o wykrywaniu regresji (`Module1ExamplesTest` jest tu dobrym kontrprzykładem),
- brak ograniczeń przy wybranych wskaźnikach.

**Pytanie do dyskusji:** jaki cel (inny niż regresje w wycenie) uzasadniałby użycie maksymalnej złożoności lub globalnego pokrycia?

---

## Aktywność 1.4. Wybór najmniejszego kroku

> Rozwiązanie opracowane na podstawie kodu referencyjnego, nie pochodzi wprost z materiału teoretycznego.

(Opracowane na podstawie sekcji 8.6, 8.7, 8.9 i 8.10 teorii.)

**Porównanie kroków:**

| Krok | Jaki problem rozwiązuje | Czy problem jest potwierdzony w opisie |
| --- | --- | --- |
| Pełne przepisanie systemu | całą konstrukcję systemu | nie: problem dotyczy jednego modułu; brak danych o całości; długi okres bez wartości, ryzyko utraty zachowań (8.7) |
| Wymiana platformy | wsparcie i możliwości platformy | nie: platforma jest wspierana przez dwa lata; regresje wynikają z braku testów, nie z platformy |
| Testy charakteryzujące moduł | brak szybkiej informacji zwrotnej w module, który zmienia się co tydzień i powoduje większość regresji | tak |

**Rekomendowany krok:** testy charakteryzujące krytyczny moduł.

- **Założenie do weryfikacji:** regresje wynikają głównie z braku szybkiej weryfikacji zachowania modułu, a jego zachowanie da się uchwycić testami na kontrolowanych danych przez istniejącą granicę (tak jak `LegacyOrderService` daje się testować przez publiczną metodę i zastępcze implementacje interfejsów, sekcja 8.10).
- **Miernik wyniku:** liczba poprawek po wydaniu przypadająca na zmianę modułu; czas uzyskania wiarygodnego wyniku testów modułu. (Nie: „liczba napisanych testów”, „procent pokrycia”, bo to mierniki aktywności.)
- **Warunek przerwania:** np. jeżeli po ustalonym czasie (np. dwóch tygodniach) nie udaje się uruchomić modułu na kontrolowanych danych albo testy są niestabilne, przerywamy i oceniamy inną granicę testów (np. wyższy poziom). Jeżeli po kilku cyklach wydań poprawki się nie zmniejszają, założenie o przyczynie regresji jest błędne i trzeba wrócić do diagnozy.

**Odniesienie do kodu:** `src/test/java/pl/training/module1/LegacyOrderServiceTest.java` to przykład testu charakteryzującego: utrwala obecny wynik (236.39) i interakcje z repozytorium oraz bramką pocztową, bez oceniania, czy wynik jest „poprawny”.

**Typowe błędy uczestników:**

- wybór pełnego przepisania z uzasadnieniem „i tak kiedyś trzeba” (brak danych, sygnał ostrzegawczy z 8.7),
- miernik aktywności zamiast wyniku,
- warunek przerwania niesprawdzalny („jeśli nie wyjdzie”).

**Pytanie do dyskusji:** jaka nowa informacja po pierwszym kroku mogłaby uzasadnić wymianę platformy lub modułu? (Np. potwierdzone ograniczenie platformy, niemożność testowania w izolacji, planowane zmiany wykraczające poza model modułu.)

---

## Ćwiczenie 1. Obserwacja bez pochopnej naprawy

Rozwiązanie pochodzi z sekcji 10.1 teorii. Tabela jest jedną z możliwych odpowiedzi.

**Materiał:** `src/main/java/pl/training/module1/LegacyOrderService.java`.

| Fragment | Fakt | Hipoteza lub ryzyko | Potrzebny dowód | Planowana zmiana |
| --- | --- | --- | --- | --- |
| warunki dotyczące `customerType` | typ klienta jest reprezentowany przez `String`, a metoda sprawdza wyłącznie wartość `VIP` | dodanie `PARTNER` może rozbudować zestaw warunków i ujawnić nieznane zasady łączenia rabatów | zatwierdzone przykłady cen dla każdego typu klienta, dane o aktualnie używanych wartościach, testy istniejących rabatów | nowy typ `PARTNER` |
| warunki dotyczące `destinationCountry` | jawne stawki istnieją tylko dla `PL` i `DE`, a każda inna wartość daje podatek równy zero | zero może oznaczać zarówno poprawną stawkę, jak i nieobsługiwany kraj ukryty jako poprawny wynik | wymagania podatkowe, kontrakt metody, lista krajów z danych produkcyjnych, zachowanie oczekiwane dla nieznanej wartości | obsługa Czech |
| obliczanie `shipping` | metoda rozróżnia tylko przesyłkę ekspresową i standardową; koszt zależy także od sumy po rabatach | parametr `boolean` nie pozwoli jednoznacznie reprezentować odbioru osobistego, a próg bezpłatnej wysyłki może nie dotyczyć tego wariantu | reguły dostawy, przykłady graniczne dla kwoty 200,00, decyzja o kolejności rabatu i naliczania wysyłki | odbiór osobisty |
| pętla po pozycjach | rabat VIP i rabat ilościowy są składane przez mnożenie, bez pośredniego zaokrąglania | rabaty mogą zgodnie z wymaganiem składać się multiplikatywnie albo sumować jako punkty procentowe; etap zaokrąglania także może być istotny | przykłady zatwierdzone przez właściciela produktu, istniejące faktury i testy charakteryzujące przypadki łączenia rabatów | typ `PARTNER` może mieć odmienną politykę rabatową |
| końcowa część `placeOrder` | jedna metoda oblicza kwotę, zapisuje wynik i wysyła wiadomość; zapis następuje przed wysłaniem | awaria bramki pocztowej może pozostawić zapisane zamówienie, mimo że metoda zakończy się wyjątkiem | kontrakt transakcyjny, sposób obsługi wyjątków przez wywołującego, logi awarii i zasady ponawiania | każda z planowanych zmian wymaga bezpiecznej regresji całego przebiegu |
| walidacja wejścia | sprawdzana jest obecność zamówienia i co najmniej jednej pozycji, ale nie są jawnie sprawdzane liczba sztuk, cena, adres ani kod kraju | niedozwolone wartości mogą docierać z wcześniejszej warstwy albo nie być kontrolowane wcale | kontrakty wejścia, walidacja w kontrolerze lub modelu, próbki błędnych żądań | nowe wartości zwiększą liczbę wariantów wejścia |

**Przykładowe pytania domenowe (z teorii):**

- Czy brak stawki oznacza nieobsługiwany kraj, stawkę zerową czy sprzedaż zwolnioną z podatku?
- Czy awaria wiadomości powinna cofnąć zapis zamówienia, czy wysyłka ma być ponawiana niezależnie?
- Czy rabaty mają być składane multiplikatywnie, czy sumowane jako punkty procentowe, i na którym etapie należy zaokrąglać kwoty?
- Czy próg bezpłatnej wysyłki jest liczony przed rabatem czy po nim?
- Czy odbiór osobisty zawsze ma koszt zerowy?

**Dodatkowe fakty widoczne w kodzie** (uzupełnienie prowadzącego na podstawie kodu, nie pochodzi wprost z teorii; przydatne, gdy grupa ma mało obserwacji):

- podatek liczony jest od `subtotal`, bez kosztu wysyłki,
- przesyłka ekspresowa kosztuje zawsze 39.99, także gdy suma przekracza próg darmowej wysyłki,
- rabat ilościowy dotyczy pojedynczej pozycji (`line.quantity() >= 10`), nie całego zamówienia,
- zaokrąglenie `HALF_UP` do dwóch miejsc następuje tylko raz, dla sumy końcowej,
- wartości reguł (`0.90`, `0.95`, `10`, `39.99`, `200.00`, `14.99`, `0.23`, `0.19`) są literałami bez nazw,
- zależności `OrderRepository` i `MailGateway` są przekazywane przez konstruktor jako interfejsy.

Każdy z tych faktów rodzi pytanie domenowe, a nie wniosek „to błąd”.

**Kluczowy komunikat z teorii:** stwierdzenie „podatek jest liczony błędnie” nie jest faktem widocznym w kodzie. Bez reguły biznesowej pozostaje hipotezą. Wystarczająca odpowiedź zawiera co najmniej pięć podobnych rozróżnień i wskazuje dowód adekwatny do każdej hipotezy. Najlepszy pierwszy krok zwiększa wiedzę lub kontrolę nad zachowaniem, nie musi tworzyć docelowej architektury.

**Odniesienie do kodu referencyjnego:**

- `src/test/java/pl/training/module1/LegacyOrderServiceTest.java` realizuje pierwszy krok z sekcji 8.10: test charakteryzujący przez publiczną metodę z implementacjami zastępczymi (`OrderRepository`, `MailGateway` jako lambdy). Po ćwiczeniu pokaż go i policz z grupą 236.39 (200.00 x 0.90 = 180.00; wysyłka 14.99; podatek 41.40).
- Zapytaj, których wierszy tabeli ten test nie chroni (ekspres, próg 200.00, DE i inne kraje, rabat ilościowy, klient bez VIP, walidacja).
- Rozsądna sekwencja kolejnych kroków (8.10): testy charakteryzujące, uzgodnienie przykładów z właścicielem produktu, odseparowanie obliczeń od zapisu i wysyłki, precyzyjne testy wyodrębnionych reguł, dopiero potem decyzja o strukturze.

**Typowe błędy uczestników:**

- zapisywanie ocen jako faktów („kod jest nieczytelny”, „podatek jest źle liczony”),
- proponowanie wzorca Strategia, enumów lub podziału klas mimo zakazu,
- stwierdzenie, że klasa „nie jest testowalna” (jest: interfejsy w konstruktorze, wynik zwracany z metody),
- dowody nieadekwatne do hipotezy (np. „przeczytać kod jeszcze raz” jako dowód reguły podatkowej),
- brak powiązania symptomu z konkretnym elementem planowanej zmiany (Czechy, `PARTNER`, odbiór osobisty).

**Pytania do dyskusji:**

- Który z symptomów stanie się istotny dopiero przy planowanej zmianie, a który jest ryzykiem już dziś (zapis przed wysłaniem wiadomości)?
- Czy dla Czech wystarczy „dodać kolejny `if`”? Co musimy wiedzieć przedtem?
- Jaki najmniejszy krok dostarczy najwięcej wiedzy?

---

## Ćwiczenie 2. Klasyfikacja i priorytetyzacja długu

Rozwiązanie pochodzi z sekcji 10.2 teorii.

### Klasyfikacja

| Kandydat | Fakt | Symptom | Potencjalny dług | Defekt | Ograniczenie | Pewność |
| --- | --- | --- | --- | --- | --- | --- |
| `PricingService` | istnieją trzy kopie reguły; dwukrotnie pominięto jedną z nich | duplikacja i wysoka złożoność | rozproszona reguła zwiększa koszt oraz ryzyko kolejnych zmian | brak dowodu na aktualnie błędny wynik | brak | wysoka dla konstrukcji i odsetek |
| `AnnualReportFormatter` | maksymalna złożoność wynosi 54; brak zmian od 22 miesięcy; wycofanie planowane za cztery miesiące | wysoka złożoność | brak wystarczającego dowodu na istotny przyszły koszt | brak danych | krótki pozostały okres życia | wysoka dla faktów, niska dla długu wymagającego spłaty |
| `OldPaymentAdapter` | wsparcie zależności kończy się za sześć miesięcy; nowy interfejs jest niezgodny | nie wynika z podanych metryk kodu | zależność może ograniczać przyszłą zmianę, ale konstrukcja i koszt pozostawienia wymagają rozpoznania | brak danych | decyzja dostawcy i niezgodny interfejs | wysoka dla ograniczenia, średnia dla długu |
| `CustomerExportController` | komentarz opisuje niezatwierdzoną funkcję | możliwy zbędny komentarz | brak wykazanego przyszłego kosztu | brak | brak | wysoka |
| `TaxRoundingRule` | implementacja używa `DOWN` zamiast wymaganego `HALF_UP`; błąd odtworzono | brak opisanego symptomu strukturalnego | brak danych o konstrukcji powodującej przyszły koszt | tak | brak | wysoka |

### Pełny rekord: `PricingService`

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

### Rekord z jawną niepewnością: `OldPaymentAdapter`

Na tym etapie należy go zarejestrować jako ograniczenie i potencjalny element długu, bez zgadywania.

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

### Budżet sześciu osobodni

| Nakład | Działanie | Oczekiwany rezultat |
| ---: | --- | --- |
| 1 osobodzień | poprawić `TaxRoundingRule` i dodać test regresyjny odtwarzający zatwierdzony przypadek | usunięty potwierdzony defekt |
| 4 osobodni | scharakteryzować trzy warianty reguły VIP, a po potwierdzeniu równoważności skonsolidować je i uruchomić testy | ograniczone odsetki w obszarze pięciu planowanych zmian |
| 1 osobodzień | wykonać ograniczone czasowo rozpoznanie `OldPaymentAdapter` | znany zakres wpływu końca wsparcia, kontraktu, certyfikacji i migracji |

Jeżeli interwencja w `PricingService` nie mieści się w czterech dniach, zespół kończy bezpieczny etap charakteryzacji i aktualizuje oszacowanie, zamiast przyspieszać zmianę kosztem weryfikacji. **Świadomie nie** refaktoryzuje `AnnualReportFormatter` (najgorsza metryka, ale brak zmian i wycofanie za cztery miesiące) i **nie** implementuje niezatwierdzonego eksportu z `CustomerExportController`.

### Brakujące informacje

Koszt i termin biznesowy nowej metody płatności, dokładny zakres wsparcia dostawcy, aktywni konsumenci adaptera, semantyczne różnice między kopiami reguły VIP, czas obsługi regresji, krytyczność raportu rocznego oraz potwierdzenie terminu jego wycofania.

### Metryki dla celu „zmniejszyć liczbę regresji w wycenie”

Przydatne:

1. liczba poprawek po wydaniu przypadająca na zmianę reguł wyceny, ponieważ bezwzględna liczba regresji rośnie także wraz z liczbą wdrożeń,
2. liczba miejsc wymagających wspólnej zmiany oraz ich współzmienność w historii, ponieważ wskazuje ryzyko pominięcia kopii, ale nie dowodzi wspólnej semantyki,
3. pokrycie gałęzi zmienionych reguł przez testy z istotnymi asercjami, ponieważ ujawnia niewykonane warianty, ale nie ocenia jakości danych i oczekiwanych wyników.

Odrzucone: globalne pokrycie linii (może rosnąć poza obszarem wyceny) i maksymalna złożoność całego systemu (nie odpowiada na pytanie o przyczyny regresji w aktywnie zmienianych regułach). Oba mogą służyć innym analizom.

### Nowe założenie dla `AnnualReportFormatter`

Przy aktywności przez dwa lata i zmianach co miesiąc wzrasta ekspozycja: około 24 zmian. Nie oznacza to automatycznej potrzeby pełnej refaktoryzacji, ale uzasadnia analizę struktury metody, historii podobnych zmian i ochrony testami. Przy potwierdzonym koszcie każdej zmiany formatter powinien zostać ponownie porównany z adapterem i modułem cenowym.

### Odniesienie do kodu referencyjnego

- `src/main/java/pl/training/module1/SalesCalculations.java` i `src/test/java/pl/training/module1/SalesCalculationsTest.java` to miniatura problemu `PricingService`: ta sama reguła VIP (`0.90`) w dwóch metodach, a trzecia kopia w `LegacyOrderService`. Test o nazwie `duplicatedCalculationsCurrentlyProduceTheSameResult` jest przykładem „zabezpieczenia przykładów zachowania” przed konsolidacją. Pokaż go przy omawianiu decyzji dla `PricingService` i ryzyka spłaty („kopie mogą zawierać zamierzone różnice”).

### Typowe błędy uczestników

- priorytet dla `AnnualReportFormatter`, bo ma najwyższą złożoność (to dokładnie teza, którą ćwiczenie obala),
- zaliczenie `TaxRoundingRule` do długu (to defekt; brak danych o konstrukcji),
- zaliczenie `CustomerExportController` do długu lub zaplanowanie implementacji eksportu (brak zakresu produktu to nie dług, sekcja 4.2),
- zignorowanie `OldPaymentAdapter`, bo „metryki są dobre” (ograniczenie środowiskowe nie wynika z metryk kodu),
- plan przekraczający sześć osobodni albo bez listy świadomych rezygnacji,
- wymyślanie wartości w rekordach długu zamiast „brak danych”.

### Pytania do dyskusji

- Dlaczego defekt `TaxRoundingRule` dostaje budżet, choć nie jest długiem?
- Co musiałoby się stać, żeby `OldPaymentAdapter` wyprzedził `PricingService`?
- Czy konsolidacja trzech kopii reguły VIP zawsze jest właściwa? Jak sprawdzić, że kopie są równoważne?

---

## Ćwiczenie 3. Wybór strategii modernizacji

Rozwiązanie pochodzi z sekcji 10.3 teorii.

**Teza wyjściowa:** sam przedstawiony materiał nie uzasadnia pełnego przepisania `ClaimsCore`. Oszacowanie nie obejmuje znacznej części kosztu, zachowanie jest częściowo niejawne, system jest krytyczny i musi równolegle ewoluować.

### Rozważone strategie

1. **Pozostawić obecną architekturę**, skrócić zestaw testów dla modułu reguł i dodać testy charakteryzujące. Mały koszt i szybka poprawa informacji zwrotnej, ale nie musi zapewnić niezależnego wdrażania ani ograniczyć sprzężenia z resztą systemu.
2. **Przyrostowo wydzielać moduł reguł za jawnym kontraktem**, początkowo obliczając wyniki równolegle. Wykorzystuje istniejącą granicę, pozwala porównywać zachowanie i dostarczać korzyści kategoriami spraw.
3. **Przepisać cały `ClaimsCore`** i przełączyć system po osiągnięciu zgodności. Może usunąć część ograniczeń, ale ma niepełne oszacowanie, późno dostarcza wartość i wymaga równoległego odtwarzania zmian regulacyjnych, danych oraz 18 integracji.

### Modelowy rekord decyzji

**Problem:** najczęściej zmieniany moduł reguł ma wolną pętlę weryfikacji, niskie pokrycie gałęzi i odpowiada za większość poprawek po wydaniu. Pełny system musi zachować dostępność i nadal przyjmować zmiany regulacyjne.

**Oczekiwany wynik:** skrócenie czasu od zaakceptowania reguły do bezpiecznego wdrożenia z 15 do 3 dni oraz zmniejszenie udziału zmian wymagających poprawki po wydaniu, bez pogorszenia dostępności.

**Decyzja:** przyrostowo wydzielać moduł reguł za jawnym kontraktem. Najpierw zbudować szybką weryfikację i uruchamianie równoległe, następnie przełączać pojedyncze kategorie spraw. Nie rozpoczynać pełnego przepisania systemu.

**Założenia:** kontrakt można odseparować od zapisu do wspólnej bazy, te same wejścia można bezpiecznie przekazać obu implementacjom, a eksperci domenowi mogą rozstrzygać istotne różnice.

**Niewiadome:** kompletność danych wejściowych, deterministyczność reguł, dopuszczalny narzut uruchamiania równoległego i liczba wyjątków zależnych od stanu wspólnej bazy.

**Pierwszy krok w ciągu czterech tygodni:**

| Tydzień | Rezultat |
| --- | --- |
| 1 | jawny opis wejścia, wyjścia i efektów ubocznych; zestaw reprezentatywnych spraw uzgodniony z ekspertami; bazowe mierniki czasu wdrożenia i poprawek |
| 2 | szybki zestaw testów charakteryzujących kontrakt oraz automatyczne porównanie wyników dla danych historycznych |
| 3 | nowa implementacja jednej ograniczonej kategorii reguł, bez prawa do podejmowania decyzji produkcyjnych |
| 4 | uruchomienie równoległe dla kontrolowanej próbki, raport rozbieżności, wpływ na wydajność i decyzja o następnym kroku |

**Sposób weryfikacji:** obie implementacje otrzymują znormalizowane, identyfikowalne wejście. System porównuje wynik decyzji, kod uzasadnienia i istotne wartości pośrednie. Każda różnica jest przypisywana do kategorii: defekt nowej ścieżki, ujawniony defekt starej ścieżki, zamierzona zmiana reguły albo niewystarczające dane. Sama zgodność procentowa nie wystarcza bez analizy znaczenia rozbieżności.

**Wdrożenie i wycofanie:** początkowo nowa implementacja działa w trybie obserwacyjnym i nie wpływa na decyzję; można ją wyłączyć flagą konfiguracyjną. Po spełnieniu kryteriów wybrana kategoria spraw przechodzi na nową ścieżkę z możliwością natychmiastowego skierowania jej z powrotem do starej implementacji. Pierwszy krok nie obejmuje migracji źródła danych ani nieodwracalnych zapisów. Wycofanie jest przećwiczone na środowisku przedprodukcyjnym.

**Ryzyka i reakcje:** niejawne zależności od bazy ogranicza się przez rejestrowanie kompletnego wejścia; narzut wydajności kontroluje się na próbce i budżetem czasu; wzrost złożoności przejściowej ogranicza właściciel rozwiązania, termin przeglądu i kryteria usunięcia. Rozbieżności o skutku finansowym lub regulacyjnym blokują przełączenie danej kategorii.

**Warunek przerwania albo zmiany kierunku:** po czterech tygodniach zatrzymać wydzielanie i ponownie ocenić strategię, jeżeli nie da się utworzyć powtarzalnego kontraktu bez wykonywania efektów ubocznych albo nie można wiarygodnie sklasyfikować rozbieżności. Po rozpoczęciu przełączeń każda niewyjaśniona różnica o skutku finansowym, spadek dostępności lub przekroczenie uzgodnionego budżetu opóźnienia powoduje powrót danej kategorii do starej ścieżki. Dokładne progi zatwierdzają właściciele biznesowi i operacyjni.

**Kryterium usunięcia rozwiązania przejściowego:** stara implementacja nie obsługuje żadnej kategorii przez dwa pełne cykle wydawnicze, wszystkie zmiany reguł są wprowadzane wyłącznie w nowym module, nie ma niewyjaśnionych rozbieżności, przećwiczono odtworzenie usługi, a właściciele danych i integracji zatwierdzili wyłączenie. Następnie usuwa się podwójne obliczanie, flagi i adaptery.

**Mierniki wyniku:** mediana i 85. percentyl czasu od zatwierdzenia reguły do wdrożenia, odsetek zmian reguł wymagających poprawki po wydaniu, czas uzyskania wiarygodnego wyniku testów modułu, liczba niewyjaśnionych rozbieżności na tysiąc porównań, dostępność i czas odpowiedzi systemu. Liczba przepisanych klas, wykonanych zadań lub procent migracji to mierniki aktywności i nie dowodzą osiągnięcia celu.

### Odniesienie do kodu referencyjnego

Ćwiczenie jest scenariuszowe, bez kodu w projekcie. Przy omówieniu można nawiązać do `LegacyOrderService` (sekcja 8.10): ten sam tok rozumowania w małej skali (testy charakteryzujące przez istniejącą granicę, potem separacja, potem decyzja o strukturze).

### Typowe błędy uczestników

- tylko dwie opcje (refaktoryzacja albo przepisanie) zamiast co najmniej trzech,
- przyjęcie 18 miesięcy jako pełnego kosztu przepisania (brak migracji danych, integracji, równoległego utrzymania, wyłączenia; sekcja 8.5),
- pierwszy krok dłuższy niż cztery tygodnie albo nieodwracalny (np. migracja wspólnej bazy),
- weryfikacja jako „procent zgodności” bez klasyfikacji rozbieżności,
- mierniki aktywności (liczba przepisanych klas, procent migracji) zamiast wyniku,
- brak kryterium usunięcia architektury przejściowej (ryzyko, że „tymczasowe” zostanie na stałe),
- ignorowanie wspólnej bazy z procesami raportowymi i zmian regulacyjnych w trakcie prac.

### Pytania do dyskusji

- Która informacja ze scenariusza najmocniej przemawia przeciw pełnemu przepisaniu?
- Jakie sygnały z sekcji 8.8 musiałyby wystąpić, żeby przepisanie modułu (a nie systemu) stało się zasadne?
- Kto w organizacji powinien zatwierdzić progi przerwania i dlaczego nie zespół deweloperski sam?

---

## Sprawdzenie wiedzy - odpowiedzi

Odpowiedzi pochodzą z sekcji 11 teorii.

1. **Dlaczego wiek kodu nie wystarcza do określenia go jako legacy?** Istotne są koszt i ryzyko zmiany, znaczenie systemu, testowalność oraz kontekst planowanych prac. Nowy kod także może być trudny do bezpiecznej zmiany.
2. **Czym różni się symptom jakości od defektu?** Symptom wskazuje możliwy problem projektowy. Defekt oznacza zaobserwowaną niezgodność zachowania z wymaganiem.
3. **Kiedy brak testów można opisać jako element długu technicznego?** Gdy brak wiarygodnej weryfikacji zwiększa przyszły koszt albo ryzyko zmian, na przykład wymusza powtarzalną ręczną regresję.
4. **Co oznaczają kapitał i odsetki w metaforze długu?** Kapitał to przybliżony koszt usunięcia lub ograniczenia konstrukcji, a odsetki to dodatkowy koszt ponoszony podczas kolejnych prac, które ją napotykają.
5. **Dlaczego 100 procent pokrycia linii nie dowodzi poprawności testów?** Pokrycie potwierdza wykonanie kodu, ale nie jakość danych, asercji, modelu przypadków ani zdolność wykrywania ważnych defektów. (Ilustracja w projekcie: `DiscountPolicyTest` daje 100 procent linii i 50 procent gałęzi; `Module1ExamplesTest` wykonuje cały kod bez asercji wyniku.)
6. **Co mierzy złożoność cyklomatyczna, a czego nie mierzy?** Opisuje liczbę liniowo niezależnych ścieżek wynikających ze struktury przepływu sterowania. Nie mierzy pełnej trudności domeny, czytelności ani wszystkich możliwych zachowań. (Ilustracja: `RiskClassifier.riskLevel`, V(G) = 5.)
7. **Dlaczego identyczny fragment kodu nie zawsze powinien zostać uogólniony?** Podobne fragmenty mogą reprezentować różne reguły, które mają rozwijać się niezależnie. Wspólna abstrakcja stworzyłaby wtedy nieprawidłowe sprzężenie. (Ilustracja: `SalesCalculations`.)
8. **Co daje połączenie częstotliwości zmian ze złożonością?** Pomaga znaleźć aktywne obszary, w których trudna struktura faktycznie wpływa na bieżącą pracę. Każdy z tych sygnałów osobno daje słabszą podstawę decyzji.
9. **Jakie koszty pełnego przepisania są często pomijane?** Między innymi odzyskanie wymagań, utrzymanie dwóch systemów, migracja danych, integracje, testy niefunkcjonalne, operacje, szkolenia, przełączenie, wycofanie wdrożenia i wyłączenie starego rozwiązania.
10. **Dlaczego decyzja o modernizacji powinna zawierać warunek ponownej oceny?** Decyzja opiera się na założeniach i niepełnych danych. Eksperymenty oraz zmiany kontekstu mogą uzasadnić korektę kierunku, zanim powstanie duży koszt nieodwracalny.
