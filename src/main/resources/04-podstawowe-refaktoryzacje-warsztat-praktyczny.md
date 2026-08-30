# Moduł 4. Podstawowe refaktoryzacje - warsztat praktyczny

## Cel modułu

Celem modułu jest opanowanie niewielkich, odwracalnych transformacji kodu i łączenie ich w bezpieczną sekwencję prowadzącą od nieczytelnej implementacji do lepiej rozdzielonych odpowiedzialności. Uczestnik ćwiczy nie tylko obsługę narzędzia refaktoryzacyjnego, lecz przede wszystkim rozpoznawanie kontraktu, warunków wstępnych i ryzyka każdej zmiany.

Techniki są przedstawione jako operacje zachowujące ustalone zachowanie. Jeżeli krok ogranicza mutowalność, dodaje walidację albo zmienia publiczne API, zostaje nazwany zmianą kontraktu i oddzielony od refaktoryzacji strukturalnej.

## Efekty uczenia się

Po ukończeniu modułu uczestnik:

- rozpoznaje fragment nadający się do Extract Method i analizuje przepływ danych przed wydzieleniem,
- stosuje Extract Variable bez przypadkowej zmiany liczby ani momentu ewaluacji,
- odróżnia wydzielenie stałej od zastąpienia magicznego literału nazwanym pojęciem,
- używa Inline Method i Inline Variable do usuwania pośrednictwa bez znaczenia,
- przeprowadza Rename z uwzględnieniem publicznego API, refleksji, konfiguracji i serializacji,
- wybiera właściciela metody lub pola na podstawie odpowiedzialności, danych i cyklu życia,
- wykonuje Move Method oraz Move Field przez delegowanie i jedno źródło prawdy,
- wydziela klasę reprezentującą spójną odpowiedzialność, a nie przypadkową grupę pól,
- hermetyzuje pole bez utożsamiania hermetyzacji z automatycznym dodaniem settera,
- rozróżnia modyfikowalną kopię, niemodyfikowalny widok i niemodyfikowalną migawkę kolekcji,
- upraszcza warunek przez nazwanie predykatu oraz gałęzi z zachowaniem short-circuit evaluation,
- zabezpiecza kolejne stany refaktoryzacji testami charakterystyki i testami równoważności,
- odróżnia mechaniczny krok refaktoryzacji od decyzji projektowej i zmiany zachowania.

## Zakres

1. Kontrakt i rytm małych transformacji
2. Rename
3. Extract Method
4. Extract Variable i Extract Constant
5. Inline Method i Inline Variable
6. Move Method i Move Field
7. Extract Class
8. Encapsulate Field
9. Encapsulate Collection
10. Encapsulate Conditional
11. Replace Magic Numbers with Named Constants
12. Studium przypadku w Javie 25
13. Warsztat praktyczny

## Nazwy technik

Nazewnictwo katalogów i narzędzi różni się między wydaniami oraz językami. W materiale używamy nazw z agendy, a równolegle wskazujemy ich popularne odpowiedniki:

| Nazwa w agendzie | Spotykany odpowiednik |
| --- | --- |
| Extract Method | Extract Function |
| Inline Method | Inline Function |
| Extract Variable | Introduce Explaining Variable |
| Inline Variable | Inline Temp |
| Move Method | Move Function |
| Encapsulate Field | Encapsulate Variable, częściowo Self-Encapsulate Field |
| Encapsulate Conditional | najbliższa technika katalogowa: Decompose Conditional |
| Replace Magic Numbers with Named Constants | Replace Magic Literal, Replace Magic Number with Symbolic Constant |

Encapsulate Conditional nie ma jednej powszechnie obowiązującej definicji katalogowej. W tym module oznacza nazwanie złożonego predykatu i, gdy pomaga to czytelności, wydzielenie obliczeń jego gałęzi. Nie jest to automatycznie zastąpienie warunku polimorfizmem.

## Konwencje przykładów

Przykłady używają Javy 25, JUnit Jupiter 6.1.3 i JaCoCo 0.8.15. Kod znajduje się w projekcie Maven `refactoring-legacy/refactoring-legacy`, w pakiecie głównym `pl.training.module4` i jego podpakietach.

Studium przypadku przedstawia cztery pełne, kompilowalne stany generatora oferty wynajmu:

| Pakiet | Znaczenie |
| --- | --- |
| `pl.training.module4.stage0` | kod początkowy zabezpieczony testami charakterystyki |
| `pl.training.module4.stage1` | lokalne zmiany Rename, Extract, Inline i hermetyzacja warunku |
| `pl.training.module4.pricing` | wynik Extract Class oraz Move Field i Move Method |
| `pl.training.module4.stage2` | stan przejściowy z delegatem przygotowanym do Inline Method |
| `pl.training.module4.stage3` | stan końcowy po usunięciu zbędnego delegata |
| `pl.training.module4.encapsulation` | osobny przykład Encapsulate Field i Encapsulate Collection |

Pakiety etapów celowo powtarzają kod. Dzięki temu materiał może pokazać wszystkie punkty kontrolne w jednym buildzie. Nie jest to zalecany układ kodu produkcyjnego. W rzeczywistym repozytorium kolejne etapy zwykle zastępują ten sam kod i pozostają widoczne w historii wersji.

Reguły wyceny są uproszczonym kontraktem szkoleniowym. Przykład nie stanowi kompletnego modelu rozliczeń podatkowych ani prawnego modelu wynajmu.

Kompilacja, testy i uruchomienie:

```shell
cd refactoring-legacy/refactoring-legacy
mvn clean verify
java -cp target/classes pl.training.module4.Module4Examples
```

## Organizacja pracy

Sugerowany czas pracy synchronicznej wynosi 240 minut:

| Część | Czas |
| --- | ---: |
| teoria i demonstracja mechaniki | 50 minut |
| ćwiczenie 1 i omówienie | 40 minut |
| ćwiczenie 2 i omówienie | 35 minut |
| ćwiczenie 3 i omówienie | 50 minut |
| ćwiczenie 4 i omówienie | 45 minut |
| sprawdzenie wiedzy i podsumowanie | 20 minut |

## 1. Kontrakt i rytm małych transformacji

### 1.1. Co ma pozostać niezmienione

Refaktoryzacja zmienia strukturę kodu bez zmiany ustalonego, obserwowalnego zachowania. Zakres obserwacji zależy od systemu.

W aplikacji wdrażanej atomowo można zmienić wewnętrzną sygnaturę i jednocześnie zaktualizować wszystkich kontrolowanych klientów. Zachowanie całego systemu może pozostać takie samo. Dla publicznej biblioteki kontrakt może obejmować dodatkowo:

- kompatybilność źródłową i binarną,
- nazwy metod oraz pól używane przez refleksję,
- format serializacji i nazwy właściwości,
- konfigurację, skrypty, zapytania i szablony zawierające nazwy jako tekst,
- kolejność wyjątków i efektów ubocznych,
- blokady, transakcje i wywołania przechwytywane przez proxy,
- wydajność, jeśli jej granica jest częścią umowy.

Zielony test jednostkowy nie dowodzi zachowania kompatybilności binarnej ani integracji z frameworkiem. Test powinien działać na poziomie, na którym istnieje ryzyko.

### 1.2. Refaktoryzacja a zmiana kontraktu

Następujące operacje mogą być wartościowe, lecz nie są automatycznie zachowaniem czysto strukturalnym:

- dodanie walidacji do wcześniej dowolnej wartości,
- zmiana publicznego pola na prywatne bez migracji klientów,
- zastąpienie żywej kolekcji niemodyfikowalną migawką,
- wykonanie kopii defensywnej tam, gdzie wcześniej współdzielono tę samą kolekcję,
- zmiana kolejności lub miejsca zaokrąglenia,
- zmiana typu wyjątku,
- usunięcie publicznej metody po jej przeniesieniu,
- zmiana nazwy używanej w formacie danych lub konfiguracji.

Najbezpieczniej najpierw przenieść strukturę z zachowaniem starego kontraktu, a dopiero później wykonać świadomą zmianę zachowania z osobnym wymaganiem i testem.

### 1.3. Pętla robocza

1. Wybierz jedną obserwowalną właściwość do ochrony.
2. Uruchom test i potwierdź, że wykrywa kontrolowaną zmianę.
3. Wykonaj jedną transformację.
4. Skompiluj kod.
5. Uruchom najwęższy wiarygodny zestaw testów.
6. Zapisz mały, spójny krok w historii.
7. Okresowo uruchamiaj pełny build i testy integracyjne.

Automatyczna funkcja IDE może poprawnie przepisać składnię i odwołania rozumiane przez jego model programu. Nie zna jednak wszystkich kontraktów zewnętrznych, danych historycznych, ciągów tekstowych ani wymagań biznesowych. Podgląd zmiany i testy nadal są potrzebne.

### 1.4. Punkt zatrzymania

Seria refaktoryzacji powinna mieć konkretny cel, na przykład:

- nazwanie nieczytelnej reguły,
- umożliwienie dodania testu,
- przeniesienie odpowiedzialności do jej właściciela,
- ograniczenie niekontrolowanej mutacji,
- przygotowanie miejsca na zatwierdzoną zmianę funkcjonalną.

Nie trzeba zastosować wszystkich dostępnych technik. Extract i Inline zmieniają poziom pośrednictwa w przeciwnych kierunkach, lecz nie zawsze są bezpiecznie odwracalne. Właściwy poziom pośrednictwa zależy od kontekstu.

## 2. Rename

### 2.1. Cel

Rename zmienia nazwę elementu tak, aby precyzyjniej komunikował znaczenie. Dotyczy zmiennych, parametrów, metod, pól, klas, pakietów i innych symboli. Dobra nazwa opisuje rolę w danym kontekście, a nie typ techniczny ani bieżący sposób implementacji.

Przykłady intencji:

| Słaba nazwa | Lepszy kierunek | Uzasadnienie |
| --- | --- | --- |
| `d` | `discount` albo `deliveryDate` | znaczenie wynika z domeny, nie litery |
| `data` | `approvedPriceBreakdown` | nazwa określa zawartość i stan |
| `process()` | `createRentalQuote()` | operacja komunikuje rezultat |
| `manager` | nazwa konkretnej odpowiedzialności | ogólna rola ukrywa powód zmiany |
| `list` | `dailyRates` | kolekcja jest nazwana według zawartości |

### 2.2. Bezpieczna mechanika

1. Ustal znaczenie elementu i zasięg kontraktu jego nazwy.
2. Wyszukaj użycia typowane oraz tekstowe.
3. Dla lokalnego symbolu wykonaj automatyczny Rename i przejrzyj podgląd.
4. Skompiluj cały moduł, nie tylko edytowany plik.
5. Uruchom testy zachowania i integracji zależne od nazwy.
6. Dla publicznego API rozważ okres przejściowy ze starą metodą delegującą i oznaczoną jako przestarzała.

### 2.3. Granice automatyzacji

Nazwa może wystąpić poza typowanym kodem Javy:

- w `Class.getDeclaredMethod` i `Class.getDeclaredField`,
- w pliku konfiguracyjnym kontenera lub frameworka,
- w JSON, XML, CSV albo schemacie zdarzenia,
- w zapytaniu SQL i mapowaniu ORM,
- w szablonie, skrypcie i wyrażeniu języka reguł,
- w nazwie metryki, uprawnienia lub dashboardu,
- w danych serializowanych przez starszą wersję,
- u niezależnie kompilowanego klienta biblioteki.

Zmiana nazwy publicznej metody i usunięcie starej sygnatury może zakończyć się u istniejącego klienta błędem linkowania. Zmiana nazwy pola może zmienić refleksję oraz domyślny format serializacji. W takich przypadkach potrzebna jest strategia migracji, nie tylko operacja IDE.

### 2.4. Rename jako narzędzie projektowe

Trudność z nazwaniem metody często wskazuje, że fragment łączy kilka odpowiedzialności. Wtedy Rename może ujawnić potrzebę Extract Method lub Extract Class. Nie należy jednak umieszczać całego opisu algorytmu w nazwie. Nazwa ma wskazać pojęcie, a implementacja oraz testy mają opisać szczegóły.

## 3. Extract Method

### 3.1. Cel i sygnały

Extract Method przenosi spójny fragment do nazwanej metody i zastępuje go wywołaniem. Wydzielenie jest uzasadnione, gdy nazwa może wyrazić intencję lepiej niż szczegóły albo gdy fragment reprezentuje samodzielną odpowiedzialność.

Sygnały:

- komentarz opisuje cel następnego bloku,
- metoda łączy różne poziomy abstrakcji,
- ten sam fragment reprezentuje tę samą wiedzę w kilku miejscach,
- predykat lub obliczenie ma nazwę w języku domenowym,
- osobny test fragmentu istotnie zmniejszy ryzyko,
- przeniesienie odpowiedzialności wymaga najpierw jej odseparowania.

Długość nie jest wystarczającym kryterium. Krótki predykat może zasługiwać na nazwę, a długa, liniowa transformacja może być czytelna jako całość.

### 3.2. Analiza przepływu danych

Przed wydzieleniem sklasyfikuj zmienne użyte w zaznaczonym fragmencie:

| Sytuacja | Typowe rozwiązanie |
| --- | --- |
| wartość jest tylko odczytywana | parametr albo dostęp przez spójny obiekt |
| wartość powstaje w fragmencie i jest używana później | wartość zwracana |
| modyfikowany jest jeden obiekt | jawna operacja na obiekcie, jeśli mutacja należy do kontraktu |
| modyfikowanych jest kilka lokalnych wartości | podziel fragment albo rozważ obiekt wyniku |
| fragment używa wielu pól innego obiektu | możliwy sygnał Move Method |
| lista parametrów rośnie nadmiernie | granica wydzielenia może być niewłaściwa albo brakuje spójnego pojęcia |

Przekazanie całego obiektu tylko po to, aby metoda mogła swobodnie sięgać do każdego pola, ukrywa zależności. Z drugiej strony rozbicie spójnego obiektu na kilkanaście prymitywów również zwiększa sprzężenie. Należy zachować semantyczną granicę.

### 3.3. Bezpieczna mechanika

1. Wybierz najmniejszy spójny fragment.
2. Nadaj metodzie nazwę opisującą rezultat lub intencję.
3. Określ parametry i wartość zwracaną.
4. Skopiuj fragment do nowej metody bez dodatkowego upraszczania.
5. Zastąp pierwotny fragment wywołaniem.
6. Skompiluj i uruchom test.
7. Dopiero w kolejnym kroku popraw strukturę nowej metody.

### 3.4. Trudne przypadki

Szczególnej analizy wymagają:

- `return`, `break`, `continue` i etykiety przekraczające granicę fragmentu,
- modyfikacja kilku zmiennych lokalnych,
- zasoby `try-with-resources` oraz zasięg `catch` i `finally`,
- wyjątki kontrolowane i zmiana sygnatury `throws`,
- sekcje `synchronized` i monitor, na którym działa blokada,
- użycie `this`, `super` oraz metod przesłanianych,
- zmienne przechwytywane przez lambdę lub klasę lokalną,
- typy generyczne i przeciążenia zależne od kontekstu,
- efekty uboczne ukryte w getterach lub operatorach inkrementacji.

Wydzielenie nie powinno niejawnie przesuwać efektu przed warunek, zmieniać liczby wywołań ani poszerzać czasu utrzymywania zasobu lub blokady.

## 4. Extract Variable i Extract Constant

### 4.1. Extract Variable

Extract Variable zapisuje wynik wyrażenia w nazwanej zmiennej lokalnej. Pomaga rozdzielić etapy obliczenia, nazwać pojęcie i ułatwić debugowanie.

Dobra zmienna wyjaśniająca:

- nazywa znaczenie, nie składnię,
- ma najmniejszy potrzebny zasięg,
- nie jest wielokrotnie nadpisywana,
- nie ukrywa istotnej mutacji,
- nie powtarza wyłącznie nazwy dobrze nazwanej metody.

### 4.2. Liczba i moment ewaluacji

W Javie argumenty metod oraz operandy są ewaluowane w określonej kolejności, a argumenty od lewej do prawej. Przeniesienie wyrażenia do zmiennej może zmienić moment ewaluacji. Zastąpienie kilku wystąpień jedną zmienną zmniejsza liczbę ewaluacji.

To ma znaczenie, gdy wyrażenie:

- zmienia stan,
- odczytuje czas lub losowość,
- pobiera dane z sieci lub bazy,
- inkrementuje licznik,
- może rzucić wyjątek,
- zależy od mutowalnego stanu zmienianego między użyciami.

Zmiana dwóch wywołań `clock.instant()` na jeden lokalny `now` może być właściwą decyzją funkcjonalną, ale nie jest neutralna semantycznie. Najpierw trzeba ustalić, czy kontrakt wymaga jednego snapshotu czasu, czy dwóch niezależnych odczytów.

### 4.3. Extract Constant

Extract Constant przenosi stabilną wartość lub wyrażenie do nazwanego pola, zwykle `static final`. Jest właściwe, gdy wartość:

- reprezentuje wspólne pojęcie,
- nie zależy od instancji,
- ma ten sam cykl życia i właściciela co kod użycia,
- powinna zmieniać się spójnie we wszystkich zastosowaniach.

Nie należy przenosić do stałej wartości, która w rzeczywistości jest konfiguracją środowiska, daną regulacyjną aktualizowaną bez wdrożenia albo regułą należącą do innego kontekstu.

### 4.4. `final` nie oznacza głębokiej niemutowalności

`final` uniemożliwia ponowne przypisanie zmiennej. Jeżeli zmienna przechowuje referencję do mutowalnego obiektu, stan tego obiektu nadal może się zmieniać. Pole `static final List` może więc wskazywać na listę, którą da się modyfikować.

Stałą czasu kompilacji w rozumieniu języka Javy jest zmienna `final` typu prostego albo `String`, zainicjalizowana wyrażeniem stałym. Publiczne `static final int` spełniające te warunki może zostać wklejone do kodu skompilowanego klienta. Po zmianie wartości stary klient może nadal widzieć poprzednią wartość do czasu rekompilacji.

`static final BigDecimal` nie jest stałą czasu kompilacji w tym znaczeniu. Referencja jest nieprzypisywalna, a sam `BigDecimal` jest niemutowalny, ale wartość nie jest wklejana jako stała języka do kodu klienta.

## 5. Replace Magic Numbers with Named Constants

### 5.1. Magiczny literał

Magiczny literał to wartość, której znaczenia nie można wiarygodnie odczytać z miejsca użycia. Problemem nie jest sam fakt wystąpienia liczby, lecz ukryta decyzja.

W studium przypadku:

- `7` oznacza pierwszy dzień długiego wynajmu,
- `0.10` oznacza stawkę rabatu,
- `8.00` oznacza dzienną cenę ubezpieczenia,
- `25.00` oznacza jednorazową opłatę za dostawę,
- `0.23` oznacza uproszczoną stawkę VAT przykładu.

Nazwy powinny opisywać rolę, na przykład `LONG_RENTAL_DAYS`, a nie wartość, na przykład `SEVEN`.

### 5.2. Nie każda liczba wymaga stałej

`0` i `1` w operacji indeksowania, porównaniu z zerem albo zwiększeniu licznika często są oczywiste. Wydzielenie `ZERO` lub `ONE` może usunąć lokalny kontekst zamiast dodać znaczenie.

Dwa identyczne literały nie muszą oznaczać tej samej wiedzy. Limit siedmiu dni na zwrot i próg siedmiu dni dla rabatu powinny pozostać osobnymi pojęciami, nawet jeśli dziś mają tę samą wartość.

### 5.3. Różnica względem Extract Constant

Extract Constant opisuje mechanikę przeniesienia wartości do nazwanego pola. Replace Magic Literal opisuje powód: ujawnienie ukrytej decyzji. Można wydzielić stałą, która nadal ma złą nazwę, znajduje się u niewłaściwego właściciela albo centralizuje niezależne reguły. Sama transformacja nie gwarantuje dobrego modelu.

## 6. Inline Variable i Inline Method

### 6.1. Inline Variable

Inline Variable zastępuje odczyt zmiennej jej inicjalizatorem, a następnie usuwa deklarację. Jest właściwe, gdy zmienna nie niesie użytecznej nazwy, tylko powtarza dobrze nazwaną metodę albo blokuje dalszą refaktoryzację.

Warunki bezpieczeństwa:

- zmienna nie jest ponownie przypisywana,
- inicjalizator da ten sam rezultat w każdym miejscu podstawienia,
- liczba i moment ewaluacji pozostaną zgodne z kontraktem,
- typ zmiennej nie steruje wyborem przeciążenia ani wnioskowaniem typów,
- usunięta nazwa nie reprezentuje ważnego pojęcia domenowego.

Bezpieczna mechanika:

1. Zastąp jedno użycie inicjalizatorem.
2. Skompiluj i uruchom test.
3. Zastępuj następne użycia pojedynczo.
4. Usuń deklarację dopiero po usunięciu wszystkich odwołań.

Nie należy powielać wywołania z efektem ubocznym. Jeżeli inicjalizator był wykonany raz, a zmienna była czytana trzy razy, naiwne Inline Variable wykona wyrażenie trzy razy. Podobny problem występuje, gdy podstawienie przenosi operację do pętli albo do warunkowej gałęzi.

### 6.2. Typ docelowy i przeciążenia

Wyrażenie może korzystać z typu wynikającego z kontekstu. Wydzielenie lub włączenie zmiennej może zmienić ten kontekst:

- jawny typ lokalny może wybierać inne przeciążenie niż `var`,
- pusta kolekcja może otrzymać typ z parametru metody, a po wydzieleniu jako `var` stać się kolekcją `Object`,
- lambda i referencja do metody wymagają typu docelowego i nie mogą być samodzielnym inicjalizatorem `var`,
- typ prymitywny i opakowanie mogą uruchomić inne przeciążenie albo unboxing.

Kompilator wykryje część tych różnic, lecz poprawna kompilacja z innym przeciążeniem nadal może zmienić zachowanie. Podgląd typu i test są częścią transformacji.

### 6.3. Inline Method

Inline Method zastępuje wywołania ciałem metody i usuwa metodę, jeżeli nie ma już klientów. Jest uzasadnione, gdy metoda:

- nie wnosi użytecznej nazwy,
- tylko deleguje bez ochrony granicy,
- zaciemnia prostszy przepływ,
- jest pozostałością po wcześniejszych transformacjach,
- utrudnia przeprowadzenie innej refaktoryzacji.

Metoda domenowa o prostej implementacji może nadal mieć wysoką wartość. `qualifiesForLongRentalDiscount()` wyjaśnia decyzję lepiej niż samo porównanie z liczbą. Liczba linii nie przesądza o Inline Method.

### 6.4. Ryzyka Inline Method

- Parametr użyty dwa razy nie może zostać zastąpiony dwoma wywołaniami argumentu mającego efekt uboczny.
- Wywołanie polimorficzne może trafić do przesłoniętej implementacji. Wklejenie ciała znanego z jednej klasy usuwa dynamiczną dyspozycję.
- Instancyjna metoda `synchronized` blokuje monitor odbiorcy, a statyczna monitor obiektu `Class` klasy deklarującej. Usunięcie wywołania może usunąć lub zmienić blokadę.
- Adnotacje transakcyjne, autoryzacyjne i przechwytujące mogą działać na granicy metody.
- `return`, lokalne nazwy i obsługa wyjątków trzeba dopasować bez zmiany przepływu.
- Referencje do metod i wywołania refleksyjne nie muszą wyglądać jak zwykłe wywołania.
- Usunięcie publicznej metody może złamać klienta źródłowo i binarnie.

Najbezpieczniejszym kandydatem jest prywatny, niepolimorficzny delegat używany w jednym miejscu. Taki przypadek występuje między `stage2` i `stage3` studium.

## 7. Move Method i Move Field

### 7.1. Wybór właściciela

Move Method przenosi metodę do typu, który powinien odpowiadać za jej zachowanie. Move Field przenosi stan do właściciela o właściwej odpowiedzialności i cyklu życia.

Sygnały:

- metoda częściej używa danych innego obiektu niż własnego,
- zmiana reguły wymaga edycji klasy, która nie jest właścicielem pojęcia,
- pole jest odczytywane i modyfikowane głównie przez inny komponent,
- dane i zachowanie tworzą spójny klaster,
- obecne położenie wymusza długie listy parametrów lub ujawnianie szczegółów.

Feature Envy jest sygnałem do analizy, nie automatycznym nakazem przeniesienia. Metoda może celowo koordynować kilka obiektów na poziomie przypadku użycia.

### 7.2. Move Method krok po kroku

1. Zabezpiecz wynik, wyjątki, efekty i kolejność testem.
2. Sprawdź użycia `this`, `super`, prywatnych pól, metod wirtualnych i zasobów.
3. Utwórz metodę w klasie docelowej z najmniejszą widocznością.
4. Przenieś ciało bez dodatkowej zmiany algorytmu.
5. Zmień starą metodę w delegat do nowej.
6. Migruj wywołania pojedynczo.
7. Usuń delegat tylko wtedy, gdy nie jest częścią wymaganego kontraktu.

Delegat pozwala odseparować przeniesienie zachowania od migracji klientów. Może pozostać jako fasada kompatybilności, jeśli stare API musi działać.

### 7.3. Ryzyka Move Method

- Niekwalifikowane nazwy po zmianie odbiorcy mogą związać się z innymi polami lub metodami.
- Przeniesienie może ominąć przesłonięcie w podklasie.
- Zmiana pakietu wpływa na dostęp pakietowy i `protected`.
- Metoda instancyjna `synchronized` po przeniesieniu blokuje inny obiekt.
- Adnotacja transakcyjna lub bezpieczeństwa może pozostać na starym delegacie albo przestać być przechwytywana przez proxy.
- Samowywołanie i wywołanie przez zewnętrzny obiekt mogą mieć inną semantykę frameworka.
- Rekurencja może nadal przechodzić przez stary delegat i zmienić dynamiczne wiązanie.

### 7.4. Move Field krok po kroku

1. Znajdź wszystkie odczyty, zapisy i użycia refleksyjne.
2. Ustal liczność, własność i cykl życia wartości.
3. Utwórz jedno pole w obiekcie docelowym.
4. Przenieś tę samą wartość albo referencję, zachowując aliasing.
5. Pozostaw delegujące operacje dostępu w źródle, jeśli wymaga tego migracja.
6. Migruj odczyty, a potem zapisy.
7. Usuń stare pole dopiero po potwierdzeniu jednego źródła prawdy.

Nie należy utrzymywać dwóch niezależnie zapisywalnych kopii pola. Dual write może się rozjechać po wyjątku, błędzie współbieżności albo pominiętym zapisie.

### 7.5. Ryzyka Move Field

Przeniesienie musi zachować znaczenie `final`, `volatile`, `transient` i `static`. Skopiowanie mutowalnego obiektu zamiast przeniesienia referencji zmienia tożsamość i aliasing. Przeniesienie pola statycznego może zmienić moment inicjalizacji klasy oraz efekty inicjalizatora.

Publicznego pola nie można zachować za pomocą delegowania tak łatwo jak metody. Bezpośredniego zapisu do pola nie da się przechwycić. Jego usunięcie albo zwężenie widoczności wymaga migracji klientów lub wydania łamiącego API.

Należy sprawdzić także:

- domyślną serializację Javy,
- mapowanie ORM i położenie adnotacji,
- `equals`, `hashCode`, `toString` i klonowanie,
- schemat danych oraz migrację historycznych rekordów,
- narzędzia refleksyjne i generowanie kodu,
- zachowanie komponentów rekordu.

Przeniesienie komponentu rekordu zmienia konstruktor kanoniczny, akcesor oraz generowane `equals`, `hashCode` i `toString`. Jest szerszą zmianą API niż przeniesienie prywatnego pola implementacyjnego.

## 8. Extract Class

### 8.1. Cel

Extract Class tworzy nową klasę dla spójnego zestawu pól i operacji mającego własną odpowiedzialność. Liczba linii, pól lub metod jest tylko sygnałem. Uzasadnieniem powinny być pojęcie, odrębny powód zmiany i sensowna granica własności.

W studium przypadku:

- format dokumentu zmienia się z powodu wymagań prezentacyjnych,
- reguły ceny zmieniają się z powodu polityki handlowej,
- stawki, progi, zaokrąglenie i kolejność kalkulacji tworzą spójną odpowiedzialność `RentalPricing`.

### 8.2. Bezpieczna mechanika

1. Określ zachowanie klasy źródłowej.
2. Utwórz początkowo prywatną lub pakietową klasę docelową.
3. Dodaj jedną relację własności.
4. Przenoś pola pojedynczo i zachowuj jedno źródło prawdy.
5. Przenoś metody pracujące na tych polach.
6. Zachowaj publiczne operacje źródła jako delegującą fasadę.
7. Dopiero po ustabilizowaniu zdecyduj, czy nowy typ powinien wejść do publicznego API.

Extract Class jest decyzją projektową. Move Field i Move Method są mechaniką, za pomocą której można ją zrealizować.

### 8.3. Pytania o własność

- Czy nowy obiekt jest wartością, encją czy wewnętrznym komponentem?
- Czy ma taki sam cykl życia jak obiekt źródłowy?
- Czy może być współdzielony?
- Czy potrzebuje referencji zwrotnej?
- Kto odpowiada za jego utworzenie i walidację?
- Czy jego tożsamość ma znaczenie dla `==`, monitorów lub cache?
- Jak wpłynie na serializację, ORM i transakcje?

Referencja zwrotna może utworzyć cykl, utrudnić serializację i zwiększyć sprzężenie. Często lepiej przekazać do nowej klasy wymagane wartości lub wąski kontrakt niż cały obiekt źródłowy.

### 8.4. Zły wynik ekstrakcji

Jeżeli wszystkie dane zostają przeniesione, ale źródłowa klasa nadal wykonuje całą logikę przez serię getterów, powstaje dodatkowa nawigacja bez przeniesienia odpowiedzialności. Jeżeli nowa klasa wymaga większości pól starej klasy i wzajemnej referencji, granica prawdopodobnie została wybrana niewłaściwie.

## 9. Encapsulate Field

### 9.1. Cel

Encapsulate Field kieruje dostęp do pola przez operacje właściciela. Pozwala kontrolować odczyt i zmianę, utrzymywać inwariant oraz później zmienić reprezentację bez modyfikowania każdego klienta.

Getter i setter nie są automatycznie dobrym modelem. Publiczny setter może tylko przenieść dowolną mutację z pola do metody. Docelowa operacja `renameTo` lepiej komunikuje dozwoloną zmianę niż ogólne `setName`.

### 9.2. Migracja zachowująca strukturę

1. Dodaj trywialny odczyt i zapis działający na tym samym polu.
2. Nie dodawaj jeszcze walidacji, logowania ani synchronizacji.
3. Migruj kontrolowanych klientów po jednym miejscu.
4. Skompiluj i testuj po każdym kroku.
5. Zwęź widoczność dopiero po migracji.
6. W osobnym kroku zastąp ogólny zapis operacją domenową, jeśli zmiana kontraktu jest zatwierdzona.

Dodanie walidacji zmienia zbiór akceptowanych danych i typy wyjątków. W przykładzie `EquipmentCatalog` wykonuje już celowy krok projektowy po migracji, więc nie jest zachowaniowo równoważny z `LegacyEquipmentCatalog` dla niepoprawnych danych.

### 9.3. Pułapki

- Bezpośredni zapis do publicznego pola nie wywoła settera.
- Pola są wybierane statycznie, a akcesory, które można przesłonić, podlegają dynamicznej dyspozycji.
- Naiwne przepisanie złożonego przypisania może wykonać odbiorcę więcej razy.
- Trzeba zachować niejawną konwersję występującą w przypisaniu złożonym do typu prostego.
- `volatile` i blokady nie są zachowywane przez sam fakt dodania akcesora.
- Adnotacje pola nie przechodzą automatycznie na metodę.
- Framework może korzystać z dostępu do pól, właściwości albo z własnej strategii refleksji.
- Zmiana wyniku introspekcji może być obserwowalna.

## 10. Encapsulate Collection

### 10.1. Cel

Encapsulate Collection przekazuje właścicielowi kontrolę nad członkostwem kolekcji. Klient odczytuje zawartość przez określony kontrakt, a modyfikacje wykonuje przez operacje `add`, `remove`, `replace` albo bardziej domenowe polecenia.

Przed zmianą trzeba scharakteryzować:

- kolejność i duplikaty,
- dopuszczalność `null`,
- mutowalność elementów,
- tożsamość zwracanej kolekcji,
- widoczność późniejszych zmian właściciela,
- zachowanie iteratorów,
- alias do kolekcji przekazanej w konstruktorze,
- zasady współbieżności.

### 10.2. Trzy różne kontrakty

| Implementacja | Czy klient zmienia członkostwo | Czy późniejsze zmiany właściciela są widoczne | Czy elementy są kopiowane głęboko |
| --- | ---: | ---: | ---: |
| `Collections.unmodifiableList(internal)` | nie | tak, jest to żywy widok | nie |
| `List.copyOf(internal)` | nie | nie, jest to migawka | nie |
| `new ArrayList<>(internal)` | tak, we własnej kopii | nie | nie |

Analogiczne rozróżnienie dotyczy map. `Map.copyOf` użyte w przykładzie zwraca niemodyfikowalną migawkę struktury. Wartości typu `BigDecimal` są niemutowalne, więc w tym konkretnym modelu płytka kopia jest wystarczająca.

### 10.3. `copyOf` nie oznacza kopii głębokiej

Jeżeli element jest mutowalny, klient nadal może zmienić jego stan. Niemodyfikowalna kolekcja blokuje zmianę członkostwa, nie wnętrza elementów.

`List.copyOf` i `Map.copyOf` odrzucają `null`. Jeżeli stara kolekcja dopuszczała takie wartości, wykonanie kopii jest zmianą zachowania. Fabryka może też zwrócić istniejący obiekt, jeżeli wejście jest już odpowiednią niemodyfikowalną kolekcją. Nie należy opierać kontraktu na tożsamości wyniku ani używać go jako monitora.

### 10.4. Bezpieczna mechanika

1. Wykonaj kopię wejścia w konstruktorze albo zachowaj stary alias świadomie.
2. Dodaj operacje kontrolujące członkostwo.
3. Zmigruj wszystkie zapisy klientów.
4. Zastąp wynik odczytu wybranym widokiem albo migawką.
5. Dodaj test rozróżniający te semantyki.
6. Oddziel wprowadzenie walidacji, zakazu `null` i nowych reguł duplikatów.

Zwracanie migawki nie zapewnia bezpieczeństwa wątkowego całemu obiektowi. Współbieżne odczyty i zapisy nadal wymagają osobnego kontraktu.

## 11. Encapsulate Conditional

### 11.1. Nazwanie decyzji

Złożony warunek często łączy szczegóły techniczne i wiedzę domenową. Wydzielenie predykatu pozwala zastąpić pytanie o implementację pytaniem o znaczenie, na przykład `request.days() >= 7` przez `qualifiesForLongRentalDiscount(request)`.

Szerszy wariant, często nazywany Decompose Conditional, wydziela osobno:

- predykat,
- obliczenie gałęzi prawdziwej,
- obliczenie gałęzi fałszywej.

Każdy krok należy skompilować i przetestować oddzielnie.

### 11.2. Krótkie spięcie jest zachowaniem

Operatory `&&` i `||` nie zawsze ewaluują prawy operand. Operator warunkowy wybiera tylko jedną z dwóch gałęzi. Refaktoryzacja musi zachować tę własność.

Nie wolno obliczyć wcześniej wartości, która pierwotnie znajdowała się po prawej stronie osłony przed `null`, w niewybranej gałęzi operatora warunkowego albo w nieuruchomionej gałęzi `switch`. Mogłoby to wprowadzić nowy wyjątek lub efekt uboczny.

### 11.3. Zakres zmiennych wzorca

Przy dopasowaniu `instanceof` zakres zmiennej zależy od przepływu sterowania. Wydzielenie całego warunku może sprawić, że zmienna nie będzie dostępna w ciele `if`. Czasem należy pozostawić dopasowanie w miejscu i wydzielić tylko część warunku operującą na już dostępnej wartości.

### 11.4. Nazwana metoda nie gwarantuje czystości

Predykat może nadal wykonywać I/O, zmieniać stan albo rzucać wyjątek. Nazwa zaczynająca się od `is`, `has` lub `qualifies` nie zmienia zachowania. Jeżeli istnieją efekty, test powinien chronić ich liczbę i kolejność, a projekt powinien rozważyć oddzielenie zapytania od modyfikacji.

## 12. Studium przypadku: generator oferty wynajmu

### 12.1. Kontrakt szkoleniowy

Generator przygotowuje tekstową ofertę wynajmu sprzętu. Przyjmujemy następujące reguły:

- wiertnica kosztuje 39,99 za dzień, a generator 120,00 za dzień,
- wynajem trwający co najmniej 7 dni otrzymuje 10 procent rabatu od podstawowego kosztu wynajmu,
- ubezpieczenie kosztuje 8,00 za każdy dzień,
- dostawa kosztuje jednorazowo 25,00,
- VAT wynosi 23 procent wartości netto,
- każda pośrednia kwota pieniężna jest zaokrąglana do dwóch miejsc metodą HALF_UP,
- nazwa klienta w dokumencie jest pozbawiana skrajnych białych znaków i zamieniana na wielkie litery,
- zapis liczb i tekstu nie zależy od domyślnego locale procesu,
- dokument kończy się znakiem nowej linii.

Te reguły są kontraktem przykładu. Inny moment zaokrąglenia, rabat naliczony od dodatków albo brak końcowego znaku nowej linii oznaczałyby zmianę zachowania.

Model wejścia znajduje się w pakiecie <code>pl.training.module4.model</code>.

<code>EquipmentType.java</code>:

~~~java
package pl.training.module4.model;

public enum EquipmentType {
    DRILL,
    GENERATOR
}
~~~

<code>RentalRequest.java</code>:

~~~java
package pl.training.module4.model;

import java.util.Objects;

public record RentalRequest(
        String customerName,
        EquipmentType equipmentType,
        int days,
        boolean insurance,
        boolean delivery) {
    public RentalRequest {
        Objects.requireNonNull(customerName, "customerName");
        Objects.requireNonNull(equipmentType, "equipmentType");

        if (customerName.isBlank()) {
            throw new IllegalArgumentException("Customer name must not be blank");
        }
        if (days <= 0) {
            throw new IllegalArgumentException("Rental days must be positive");
        }
    }
}
~~~

Walidacja rekordu nie jest przedmiotem refaktoryzacji generatora. Jest stabilnym warunkiem wstępnym wszystkich czterech etapów.

### 12.2. Etap 0: kod początkowy

<code>pl.training.module4.stage0.RentalQuoteService</code> oblicza poprawny wynik, ale miesza reguły wyceny, formatowanie dokumentu, nieczytelne nazwy i literały domenowe.

~~~java
package pl.training.module4.stage0;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalQuoteService {
    private final Map<EquipmentType, BigDecimal> rates = Map.of(
            EquipmentType.DRILL, new BigDecimal("39.99"),
            EquipmentType.GENERATOR, new BigDecimal("120.00"));
    private final BigDecimal disc = new BigDecimal("0.10");

    public String createQuote(RentalRequest r) {
        BigDecimal a = money(rates.get(r.equipmentType())
                .multiply(BigDecimal.valueOf(r.days())));
        BigDecimal d = r.days() >= 7
                ? money(a.multiply(disc))
                : money(BigDecimal.ZERO);
        BigDecimal i = r.insurance()
                ? money(new BigDecimal("8.00")
                        .multiply(BigDecimal.valueOf(r.days())))
                : money(BigDecimal.ZERO);
        BigDecimal f = r.delivery()
                ? new BigDecimal("25.00")
                : money(BigDecimal.ZERO);
        BigDecimal n = money(a.subtract(d).add(i).add(f));
        BigDecimal v = money(n.multiply(new BigDecimal("0.23")));
        BigDecimal t = money(n.add(v));

        String q = "RENTAL QUOTE\n"
                + "Customer: "
                + r.customerName().strip().toUpperCase(Locale.ROOT) + "\n"
                + "Equipment: " + r.equipmentType() + "\n"
                + "Days: " + r.days() + "\n"
                + "Base: " + a.toPlainString() + "\n"
                + "Discount: " + d.toPlainString() + "\n"
                + "Insurance: " + i.toPlainString() + "\n"
                + "Delivery: " + f.toPlainString() + "\n"
                + "Net: " + n.toPlainString() + "\n"
                + "VAT: " + v.toPlainString() + "\n"
                + "Total: " + t.toPlainString() + "\n";
        return q;
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
~~~

Przed pierwszą zmianą powstaje test charakterystyki. Obejmuje pełny dokument, oba boki granicy rabatu, niezależne warianty dodatków oraz przypadki ujawniające sposób zaokrąglania VAT i rabatu.

~~~java
package pl.training.module4.stage0;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class RentalQuoteServiceCharacterizationTest {
    private final RentalQuoteService service = new RentalQuoteService();

    @Test
    void documentsCompleteGeneratorQuote() {
        RentalRequest request = new RentalRequest(
                " Acme ",
                EquipmentType.GENERATOR,
                8,
                true,
                true);

        String quote = service.createQuote(request);

        assertEquals("""
                RENTAL QUOTE
                Customer: ACME
                Equipment: GENERATOR
                Days: 8
                Base: 960.00
                Discount: 96.00
                Insurance: 64.00
                Delivery: 25.00
                Net: 953.00
                VAT: 219.19
                Total: 1172.19
                """, quote);
    }

    @Test
    void documentsDiscountBoundary() {
        String sixDays = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.GENERATOR,
                6,
                false,
                false));
        String sevenDays = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.GENERATOR,
                7,
                false,
                false));

        assertTrue(sixDays.contains("Discount: 0.00\n"));
        assertTrue(sixDays.contains("Total: 885.60\n"));
        assertTrue(sevenDays.contains("Discount: 84.00\n"));
        assertTrue(sevenDays.contains("Total: 929.88\n"));
    }

    @Test
    void documentsVatRounding() {
        String quote = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                1,
                false,
                false));

        assertTrue(quote.contains("VAT: 9.20\n"));
        assertTrue(quote.contains("Total: 49.19\n"));
    }

    @Test
    void distinguishesInsuranceFromDelivery() {
        String insuranceOnly = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                2,
                true,
                false));
        String deliveryOnly = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                2,
                false,
                true));

        assertAll(
                () -> assertTrue(
                        insuranceOnly.contains("Insurance: 16.00\n")),
                () -> assertTrue(
                        insuranceOnly.contains("Delivery: 0.00\n")),
                () -> assertTrue(
                        insuranceOnly.contains("Total: 118.06\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Insurance: 0.00\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Delivery: 25.00\n")),
                () -> assertTrue(
                        deliveryOnly.contains("Total: 129.13\n")));
    }

    @Test
    void documentsDiscountRounding() {
        String quote = service.createQuote(new RentalRequest(
                "Acme",
                EquipmentType.DRILL,
                15,
                false,
                false));

        assertTrue(quote.contains("Discount: 59.99\n"));
        assertTrue(quote.contains("Net: 539.86\n"));
        assertTrue(quote.contains("Total: 664.03\n"));
    }
}
~~~

Test charakterystyki nie stwierdza, czy zastane reguły są biznesowo pożądane. Rejestruje istotne obserwacje, aby zmiana struktury nie poprawiła ich przypadkiem.

### 12.3. Etap 1: lokalne transformacje

Pierwsza seria kroków pozostaje w jednej klasie:

1. Rename nadaje znaczenie parametrowi i zmiennym lokalnym.
2. Extract Constant nazywa próg rabatu, stawki dodatków i VAT.
3. Replace Magic Numbers with Named Constants zastępuje literały reprezentujące decyzje domenowe.
4. Extract Variable nazywa stawkę dzienną i liczbę dni używaną w obliczeniu.
5. Extract Method oddziela rabat, ubezpieczenie, dostawę i dokument.
6. Encapsulate Conditional nazywa kwalifikację do rabatu.
7. Inline Variable usuwa zmienną przechowującą dokument tuż przed jego zwróceniem.

Po każdym punkcie kod powinien się kompilować, a test charakterystyki powinien pozostać zielony.

~~~java
package pl.training.module4.stage1;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalQuoteService {
    private static final int LONG_RENTAL_DAYS = 7;
    private static final BigDecimal INSURANCE_DAILY_RATE =
            new BigDecimal("8.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("25.00");
    private static final BigDecimal VAT_RATE = new BigDecimal("0.23");
    private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");

    private final Map<EquipmentType, BigDecimal> dailyRates = Map.of(
            EquipmentType.DRILL, new BigDecimal("39.99"),
            EquipmentType.GENERATOR, new BigDecimal("120.00"));
    private final BigDecimal longRentalDiscountRate =
            new BigDecimal("0.10");

    public String createQuote(RentalRequest request) {
        BigDecimal dailyRate = dailyRates.get(request.equipmentType());
        BigDecimal rentalDays = BigDecimal.valueOf(request.days());
        BigDecimal baseRentalCost = money(dailyRate.multiply(rentalDays));
        BigDecimal discount = calculateDiscount(request, baseRentalCost);
        BigDecimal insuranceCost = calculateInsuranceCost(request);
        BigDecimal deliveryCost = calculateDeliveryCost(request);
        BigDecimal netAmount = money(baseRentalCost
                .subtract(discount)
                .add(insuranceCost)
                .add(deliveryCost));
        BigDecimal vat = money(netAmount.multiply(VAT_RATE));
        BigDecimal total = money(netAmount.add(vat));

        return buildDocument(
                request,
                baseRentalCost,
                discount,
                insuranceCost,
                deliveryCost,
                netAmount,
                vat,
                total);
    }

    private BigDecimal calculateDiscount(
            RentalRequest request,
            BigDecimal baseRentalCost) {
        if (!qualifiesForLongRentalDiscount(request)) {
            return ZERO_MONEY;
        }
        return money(baseRentalCost.multiply(longRentalDiscountRate));
    }

    private static boolean qualifiesForLongRentalDiscount(
            RentalRequest request) {
        return request.days() >= LONG_RENTAL_DAYS;
    }

    private static BigDecimal calculateInsuranceCost(RentalRequest request) {
        if (!request.insurance()) {
            return ZERO_MONEY;
        }
        return money(INSURANCE_DAILY_RATE.multiply(
                BigDecimal.valueOf(request.days())));
    }

    private static BigDecimal calculateDeliveryCost(RentalRequest request) {
        return request.delivery() ? DELIVERY_FEE : ZERO_MONEY;
    }

    private static String buildDocument(
            RentalRequest request,
            BigDecimal baseRentalCost,
            BigDecimal discount,
            BigDecimal insuranceCost,
            BigDecimal deliveryCost,
            BigDecimal netAmount,
            BigDecimal vat,
            BigDecimal total) {
        return String.format(
                Locale.ROOT,
                """
                RENTAL QUOTE
                Customer: %s
                Equipment: %s
                Days: %d
                Base: %s
                Discount: %s
                Insurance: %s
                Delivery: %s
                Net: %s
                VAT: %s
                Total: %s
                """,
                request.customerName().strip().toUpperCase(Locale.ROOT),
                request.equipmentType(),
                request.days(),
                baseRentalCost.toPlainString(),
                discount.toPlainString(),
                insuranceCost.toPlainString(),
                deliveryCost.toPlainString(),
                netAmount.toPlainString(),
                vat.toPlainString(),
                total.toPlainString());
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
~~~

Wydzielenie metod zwiększyło liczbę nazw, ale nie zmieniło kolejności obliczeń. Rabat nadal jest liczony wyłącznie od podstawowego kosztu, zaokrąglenie nadal zachodzi w tych samych miejscach, a dokument ma identyczny format.

### 12.4. Extract Class oraz Move Field i Move Method

Klasa etapu 1 ma dwie wyraźne odpowiedzialności: wycenę i prezentację. Reguły wyceny, stawki oraz zaokrąglenie zostają przeniesione do <code>RentalPricing</code>. Wynik obliczenia otrzymuje jawny typ <code>PriceBreakdown</code>.

<code>PriceBreakdown.java</code>:

~~~java
package pl.training.module4.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record PriceBreakdown(
        BigDecimal baseRentalCost,
        BigDecimal discount,
        BigDecimal insuranceCost,
        BigDecimal deliveryCost,
        BigDecimal netAmount,
        BigDecimal vat,
        BigDecimal total) {
    public PriceBreakdown {
        baseRentalCost = money(baseRentalCost, "baseRentalCost");
        discount = money(discount, "discount");
        insuranceCost = money(insuranceCost, "insuranceCost");
        deliveryCost = money(deliveryCost, "deliveryCost");
        netAmount = money(netAmount, "netAmount");
        vat = money(vat, "vat");
        total = money(total, "total");
    }

    private static BigDecimal money(BigDecimal amount, String name) {
        Objects.requireNonNull(amount, name);
        if (amount.signum() < 0) {
            throw new IllegalArgumentException(name + " must not be negative");
        }
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }
}
~~~

<code>RentalPricing.java</code>:

~~~java
package pl.training.module4.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

public final class RentalPricing {
    private static final int LONG_RENTAL_DAYS = 7;
    private static final BigDecimal INSURANCE_DAILY_RATE =
            new BigDecimal("8.00");
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("25.00");
    private static final BigDecimal VAT_RATE = new BigDecimal("0.23");
    private static final BigDecimal ZERO_MONEY = new BigDecimal("0.00");

    private final Map<EquipmentType, BigDecimal> dailyRates;
    private final BigDecimal longRentalDiscountRate;

    public RentalPricing(
            Map<EquipmentType, BigDecimal> dailyRates,
            BigDecimal longRentalDiscountRate) {
        Objects.requireNonNull(dailyRates, "dailyRates");
        Objects.requireNonNull(
                longRentalDiscountRate,
                "longRentalDiscountRate");

        if (longRentalDiscountRate.signum() < 0
                || longRentalDiscountRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Discount rate must be between zero and one");
        }

        EnumMap<EquipmentType, BigDecimal> rates =
                new EnumMap<>(EquipmentType.class);
        for (EquipmentType type : EquipmentType.values()) {
            BigDecimal rate = dailyRates.get(type);
            if (rate == null) {
                throw new IllegalArgumentException(
                        "Missing daily rate for " + type);
            }
            BigDecimal normalizedRate = money(rate);
            if (normalizedRate.signum() <= 0) {
                throw new IllegalArgumentException(
                        "Daily rate must be positive for " + type);
            }
            rates.put(type, normalizedRate);
        }

        this.dailyRates = Map.copyOf(rates);
        this.longRentalDiscountRate = longRentalDiscountRate;
    }

    public static RentalPricing standard() {
        return new RentalPricing(
                Map.of(
                        EquipmentType.DRILL, new BigDecimal("39.99"),
                        EquipmentType.GENERATOR, new BigDecimal("120.00")),
                new BigDecimal("0.10"));
    }

    public PriceBreakdown calculate(RentalRequest request) {
        Objects.requireNonNull(request, "request");

        BigDecimal baseRentalCost = calculateBaseRentalCost(request);
        BigDecimal discount = calculateDiscount(request, baseRentalCost);
        BigDecimal insuranceCost = calculateInsuranceCost(request);
        BigDecimal deliveryCost = calculateDeliveryCost(request);
        BigDecimal netAmount = money(baseRentalCost
                .subtract(discount)
                .add(insuranceCost)
                .add(deliveryCost));
        BigDecimal vat = money(netAmount.multiply(VAT_RATE));
        BigDecimal total = money(netAmount.add(vat));

        return new PriceBreakdown(
                baseRentalCost,
                discount,
                insuranceCost,
                deliveryCost,
                netAmount,
                vat,
                total);
    }

    private BigDecimal calculateBaseRentalCost(RentalRequest request) {
        BigDecimal dailyRate = dailyRates.get(request.equipmentType());
        return money(dailyRate.multiply(BigDecimal.valueOf(request.days())));
    }

    private BigDecimal calculateDiscount(
            RentalRequest request,
            BigDecimal baseRentalCost) {
        if (!qualifiesForLongRentalDiscount(request)) {
            return ZERO_MONEY;
        }
        return money(baseRentalCost.multiply(longRentalDiscountRate));
    }

    private static boolean qualifiesForLongRentalDiscount(
            RentalRequest request) {
        return request.days() >= LONG_RENTAL_DAYS;
    }

    private static BigDecimal calculateInsuranceCost(RentalRequest request) {
        if (!request.insurance()) {
            return ZERO_MONEY;
        }
        return money(INSURANCE_DAILY_RATE.multiply(
                BigDecimal.valueOf(request.days())));
    }

    private static BigDecimal calculateDeliveryCost(RentalRequest request) {
        return request.delivery() ? DELIVERY_FEE : ZERO_MONEY;
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
~~~

Sekwencja bezpiecznego przeniesienia wygląda następująco:

1. W klasie źródłowej wprowadź prywatny punkt <code>calculatePrice</code>, który nadal korzysta z istniejącej logiki.
2. Utwórz nowy typ i przenieś do niego implementację razem z potrzebnymi danymi.
3. Zastąp ciało metody źródłowej delegowaniem do nowego właściciela.
4. Po każdym kroku usuń ze źródła tylko te dane, które nie mają już odczytów.
5. Utrzymuj jedno źródło prawdy dla każdej stawki i reguły.
6. Migruj kontrolowanych klientów, a przejściowy delegat usuń dopiero wtedy, gdy nie chroni kontraktu.

Pakiety pokazują ważne punkty kontrolne, a nie każdy mikrocommit tej sekwencji. Konstruktor <code>RentalPricing</code> jawnie definiuje poprawność własnego, nowego API. Kopiuje mapę, normalizuje stawki do dwóch miejsc metodą HALF_UP, dopiero potem sprawdza ich dodatniość, a także kontroluje kompletność stawek i zakres rabatu. Dla poprawnych danych używanych przez wcześniejszą usługę wynik pozostaje ten sam. Zachowanie niepoprawnych konfiguracji nie było częścią publicznego kontraktu etapu 0. W systemie produkcyjnym dodanie takiej walidacji powinno być osobną, świadomą decyzją.

### 12.5. Etap 2: delegat po przeniesieniu

Po wydzieleniu wyceny usługa dokumentu korzysta z nowego obiektu. Prywatna metoda <code>calculatePrice</code> jest celowo zachowanym punktem przejściowym bezpiecznej migracji:

~~~java
package pl.training.module4.stage2;

import java.util.Locale;
import java.util.Objects;

import pl.training.module4.model.RentalRequest;
import pl.training.module4.pricing.PriceBreakdown;
import pl.training.module4.pricing.RentalPricing;

public final class RentalQuoteService {
    private final RentalPricing pricing;

    public RentalQuoteService() {
        this(RentalPricing.standard());
    }

    public RentalQuoteService(RentalPricing pricing) {
        this.pricing = Objects.requireNonNull(pricing);
    }

    public String createQuote(RentalRequest request) {
        Objects.requireNonNull(request, "request");

        PriceBreakdown price = calculatePrice(request);
        return buildDocument(request, price);
    }

    private PriceBreakdown calculatePrice(RentalRequest request) {
        return pricing.calculate(request);
    }

    private static String buildDocument(
            RentalRequest request,
            PriceBreakdown price) {
        return String.format(
                Locale.ROOT,
                """
                RENTAL QUOTE
                Customer: %s
                Equipment: %s
                Days: %d
                Base: %s
                Discount: %s
                Insurance: %s
                Delivery: %s
                Net: %s
                VAT: %s
                Total: %s
                """,
                request.customerName().strip().toUpperCase(Locale.ROOT),
                request.equipmentType(),
                request.days(),
                price.baseRentalCost().toPlainString(),
                price.discount().toPlainString(),
                price.insuranceCost().toPlainString(),
                price.deliveryCost().toPlainString(),
                price.netAmount().toPlainString(),
                price.vat().toPlainString(),
                price.total().toPlainString());
    }
}
~~~

Bezargumentowy konstruktor odwzorowuje publiczną sygnaturę dostępną w etapach 0 i 1. Gdy w produkcyjnym repozytorium kolejne etapy zastępują ten sam typ o tej samej nazwie kwalifikowanej, zachowuje także deskryptor konstruktora używany przez wcześniej skompilowanego klienta. Przeciążony konstruktor umożliwia jawne przekazanie konfiguracji. Usunięcie pierwszego konstruktora byłoby odrębną zmianą publicznego API, a nie skutkiem koniecznym Extract Class.

Delegat nie dodaje nazwy domenowej, nie ukrywa niestabilnej implementacji i nie tworzy potrzebnej granicy. Ma jedno wywołanie, więc jest kandydatem do Inline Method. Przed wykonaniem operacji należy jeszcze sprawdzić, czy metoda nie jest punktem rozszerzenia, celem refleksji, miejscem przechwytywania przez proxy ani granicą synchronizacji. Tutaj jest prywatna, niesynchronizowana i pozbawiona adnotacji.

### 12.6. Etap 3: Inline Method

Wywołanie zostaje zastąpione treścią delegata, a zbędna metoda usunięta.

~~~java
package pl.training.module4.stage3;

import java.util.Locale;
import java.util.Objects;

import pl.training.module4.model.RentalRequest;
import pl.training.module4.pricing.PriceBreakdown;
import pl.training.module4.pricing.RentalPricing;

public final class RentalQuoteService {
    private final RentalPricing pricing;

    public RentalQuoteService() {
        this(RentalPricing.standard());
    }

    public RentalQuoteService(RentalPricing pricing) {
        this.pricing = Objects.requireNonNull(pricing);
    }

    public String createQuote(RentalRequest request) {
        Objects.requireNonNull(request, "request");

        PriceBreakdown price = pricing.calculate(request);
        return buildDocument(request, price);
    }

    private static String buildDocument(
            RentalRequest request,
            PriceBreakdown price) {
        return String.format(
                Locale.ROOT,
                """
                RENTAL QUOTE
                Customer: %s
                Equipment: %s
                Days: %d
                Base: %s
                Discount: %s
                Insurance: %s
                Delivery: %s
                Net: %s
                VAT: %s
                Total: %s
                """,
                request.customerName().strip().toUpperCase(Locale.ROOT),
                request.equipmentType(),
                request.days(),
                price.baseRentalCost().toPlainString(),
                price.discount().toPlainString(),
                price.insuranceCost().toPlainString(),
                price.deliveryCost().toPlainString(),
                price.netAmount().toPlainString(),
                price.vat().toPlainString(),
                price.total().toPlainString());
    }
}
~~~

Inline nie oznacza, że każdą jednoliniową metodę należy usunąć. <code>buildDocument</code> pozostaje, ponieważ nazywa osobną odpowiedzialność i utrzymuje metodę publiczną na jednym poziomie abstrakcji. <code>qualifiesForLongRentalDiscount</code> pozostaje w klasie wyceny, ponieważ nazwa wyraża regułę domenową lepiej niż porównanie liczb.

### 12.7. Test równoważności etapów

Test regresji nie powinien jedynie porównywać nowej implementacji ze starą. Dwie implementacje mogą dzielić ten sam błąd. Poniższy test porównuje każdy etap z niezależnie zapisanym, zatwierdzonym wynikiem:

~~~java
package pl.training.module4;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;

final class RentalQuoteStagesEquivalenceTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("approvedQuotes")
    void everyStageProducesApprovedQuote(
            String scenario,
            RentalRequest request,
            String expectedQuote) {
        var stage0 = new pl.training.module4.stage0.RentalQuoteService();
        var stage1 = new pl.training.module4.stage1.RentalQuoteService();
        var stage2 = new pl.training.module4.stage2.RentalQuoteService();
        var stage3 = new pl.training.module4.stage3.RentalQuoteService();

        assertAll(
                () -> assertEquals(expectedQuote, stage0.createQuote(request)),
                () -> assertEquals(expectedQuote, stage1.createQuote(request)),
                () -> assertEquals(expectedQuote, stage2.createQuote(request)),
                () -> assertEquals(expectedQuote, stage3.createQuote(request)));
    }

    private static Stream<Arguments> approvedQuotes() {
        return Stream.of(
                Arguments.of(
                        "complete generator quote",
                        new RentalRequest(
                                " Acme ",
                                EquipmentType.GENERATOR,
                                8,
                                true,
                                true),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 8
                                Base: 960.00
                                Discount: 96.00
                                Insurance: 64.00
                                Delivery: 25.00
                                Net: 953.00
                                VAT: 219.19
                                Total: 1172.19
                                """),
                Arguments.of(
                        "insurance without delivery",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                2,
                                true,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 2
                                Base: 79.98
                                Discount: 0.00
                                Insurance: 16.00
                                Delivery: 0.00
                                Net: 95.98
                                VAT: 22.08
                                Total: 118.06
                                """),
                Arguments.of(
                        "delivery without insurance",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                2,
                                false,
                                true),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 2
                                Base: 79.98
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 25.00
                                Net: 104.98
                                VAT: 24.15
                                Total: 129.13
                                """),
                Arguments.of(
                        "day before discount threshold",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.GENERATOR,
                                6,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 6
                                Base: 720.00
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 720.00
                                VAT: 165.60
                                Total: 885.60
                                """),
                Arguments.of(
                        "discount threshold",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.GENERATOR,
                                7,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: GENERATOR
                                Days: 7
                                Base: 840.00
                                Discount: 84.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 756.00
                                VAT: 173.88
                                Total: 929.88
                                """),
                Arguments.of(
                        "discount rounding",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                15,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 15
                                Base: 599.85
                                Discount: 59.99
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 539.86
                                VAT: 124.17
                                Total: 664.03
                                """),
                Arguments.of(
                        "vat rounding",
                        new RentalRequest(
                                "Acme",
                                EquipmentType.DRILL,
                                1,
                                false,
                                false),
                        """
                                RENTAL QUOTE
                                Customer: ACME
                                Equipment: DRILL
                                Days: 1
                                Base: 39.99
                                Discount: 0.00
                                Insurance: 0.00
                                Delivery: 0.00
                                Net: 39.99
                                VAT: 9.20
                                Total: 49.19
                                """));
    }
}
~~~

Zestaw obejmuje oba typy sprzętu, oba boki granicy rabatu, wszystkie dodatki i wynik zaokrąglenia. Nie jest dowodem dla wszystkich możliwych danych, ale chroni ryzyka wybrane dla tej sekwencji.

Dodatkowy <code>LocaleIndependentFormattingTest</code> ustawia domyślne locale formatowania na <code>ar-EG</code> i potwierdza identyczność etapów. Dlatego nowe implementacje wywołują <code>String.format(Locale.ROOT, ...)</code>. Samo <code>String.formatted(...)</code> użyłoby domyślnego locale i dla specyfikatora <code>%d</code> mogłoby zmienić cyfry w polu liczby dni.

### 12.8. Encapsulate Field i Encapsulate Collection

Osobny przykład pokazuje obiekt, który nie ma kontroli nad własnym stanem:

~~~java
package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.module4.model.EquipmentType;

public final class LegacyEquipmentCatalog {
    public String name;
    public final Map<EquipmentType, BigDecimal> dailyRates;

    public LegacyEquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = name;
        this.dailyRates = dailyRates;
    }
}
~~~

Modyfikator <code>final</code> blokuje zmianę referencji pola <code>dailyRates</code>, ale nie blokuje mutacji mapy. Klient może zmienić nazwę, stawkę, usunąć wpis albo modyfikować mapę wejściową po skonstruowaniu katalogu.

Pierwszy stan pośredni ukrywa pola dopiero po migracji kontrolowanych klientów. Akcesory zachowują wszystkie obserwacje dotyczące aliasowania i dopuszczalnych wartości:

~~~java
package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.util.Map;

import pl.training.module4.model.EquipmentType;

public final class AccessorBasedEquipmentCatalog {
    private String name;
    private final Map<EquipmentType, BigDecimal> dailyRates;

    public AccessorBasedEquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = name;
        this.dailyRates = dailyRates;
    }

    public String name() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public Map<EquipmentType, BigDecimal> dailyRates() {
        return dailyRates;
    }
}
~~~

Mapa zwracana przez <code>dailyRates()</code> jest nadal tym samym modyfikowalnym obiektem, a <code>setName</code> nadal przyjmuje każdą wartość. Ten stan nie jest projektem docelowym. Daje jednak osobny punkt kontrolny przed zmianą własności oraz walidacji.

W zamkniętej aplikacji, w której wszystkie użycia są migrowane i wdrażane atomowo, obserwowalne zachowanie całości może pozostać takie samo. Dla opublikowanej biblioteki usunięcie publicznego pola łamie zgodność źródłową i binarną oraz zmienia wynik refleksji. Akcesor nie zachowuje więc automatycznie publicznego API pola.

Kolejny krok jest już świadomą zmianą kontraktu: katalog kopiuje wejście, normalizuje stawki do dwóch miejsc metodą HALF_UP, sprawdza inwarianty i zwraca niemodyfikowalne migawki.

~~~java
package pl.training.module4.encapsulation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

import pl.training.module4.model.EquipmentType;

public final class EquipmentCatalog {
    private String name;
    private final EnumMap<EquipmentType, BigDecimal> dailyRates;

    public EquipmentCatalog(
            String name,
            Map<EquipmentType, BigDecimal> dailyRates) {
        this.name = validName(name);
        Objects.requireNonNull(dailyRates, "dailyRates");

        this.dailyRates = new EnumMap<>(EquipmentType.class);
        dailyRates.forEach(this::changeDailyRate);
    }

    public String name() {
        return name;
    }

    public void renameTo(String newName) {
        name = validName(newName);
    }

    public BigDecimal dailyRateFor(EquipmentType type) {
        Objects.requireNonNull(type, "type");
        BigDecimal rate = dailyRates.get(type);
        if (rate == null) {
            throw new IllegalArgumentException("Missing daily rate for " + type);
        }
        return rate;
    }

    public void changeDailyRate(EquipmentType type, BigDecimal newRate) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(newRate, "newRate");
        BigDecimal normalizedRate =
                newRate.setScale(2, RoundingMode.HALF_UP);
        if (normalizedRate.signum() <= 0) {
            throw new IllegalArgumentException("Daily rate must be positive");
        }
        dailyRates.put(type, normalizedRate);
    }

    public Map<EquipmentType, BigDecimal> dailyRates() {
        return Map.copyOf(dailyRates);
    }

    private static String validName(String value) {
        Objects.requireNonNull(value, "name");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Catalog name must not be blank");
        }
        return value;
    }
}
~~~

Test nazywa każdą wybraną właściwość kontraktu:

~~~java
package pl.training.module4.encapsulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module4.model.EquipmentType;

final class EquipmentCatalogTest {
    @Test
    void legacyCatalogSharesItsMutableMapWithTheCaller() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        LegacyEquipmentCatalog catalog = new LegacyEquipmentCatalog(
                "Summer rental",
                source);

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));

        assertEquals(
                new BigDecimal("1.00"),
                catalog.dailyRates.get(EquipmentType.DRILL));
    }

    @Test
    void accessorBasedCatalogPreservesAliasesDuringControlledMigration() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        AccessorBasedEquipmentCatalog catalog =
                new AccessorBasedEquipmentCatalog("Summer rental", source);

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));
        catalog.setName(" ");

        assertSame(source, catalog.dailyRates());
        assertEquals(
                new BigDecimal("1.00"),
                catalog.dailyRates().get(EquipmentType.DRILL));
        assertEquals(" ", catalog.name());
    }

    @Test
    void encapsulatedCatalogOwnsRatesAndReturnsUnmodifiableSnapshots() {
        EnumMap<EquipmentType, BigDecimal> source = rates();
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                source);
        Map<EquipmentType, BigDecimal> snapshot = catalog.dailyRates();

        source.put(EquipmentType.DRILL, new BigDecimal("1.00"));
        assertEquals(
                new BigDecimal("39.99"),
                catalog.dailyRateFor(EquipmentType.DRILL));

        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.00"));

        assertEquals(
                new BigDecimal("39.99"),
                snapshot.get(EquipmentType.DRILL));
        assertEquals(
                new BigDecimal("42.00"),
                catalog.dailyRateFor(EquipmentType.DRILL));
        assertThrows(
                UnsupportedOperationException.class,
                () -> snapshot.put(
                        EquipmentType.GENERATOR,
                        new BigDecimal("120.00")));
    }

    @Test
    void changesNameOnlyThroughValidatedOperation() {
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                rates());

        catalog.renameTo("Winter rental");

        assertEquals("Winter rental", catalog.name());
        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.renameTo(" "));
    }

    @Test
    void normalizesRateBeforeCheckingItsInvariant() {
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                rates());

        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.005"));

        assertEquals(
                new BigDecimal("42.01"),
                catalog.dailyRateFor(EquipmentType.DRILL));
        assertThrows(
                IllegalArgumentException.class,
                () -> catalog.changeDailyRate(
                        EquipmentType.DRILL,
                        new BigDecimal("0.004")));
    }

    private static EnumMap<EquipmentType, BigDecimal> rates() {
        EnumMap<EquipmentType, BigDecimal> rates =
                new EnumMap<>(EquipmentType.class);
        rates.put(EquipmentType.DRILL, new BigDecimal("39.99"));
        return rates;
    }
}
~~~

Test pokazuje trzy stany. <code>LegacyEquipmentCatalog</code> i <code>AccessorBasedEquipmentCatalog</code> zachowują alias do mapy podczas kontrolowanej migracji klientów. <code>EquipmentCatalog</code> ma już nowy kontrakt własności, normalizacji i walidacji, dlatego nie jest z nimi równoważny. Ta zmiana powinna mieć oddzielne wymaganie, testy i komunikację do klientów.

### 12.9. Uruchamialny przykład

Klasa <code>pl.training.module4.Module4Examples</code> sprawdza równoważność pierwszego i ostatniego etapu, a następnie demonstruje semantykę migawki katalogu.

~~~java
package pl.training.module4;

import java.math.BigDecimal;
import java.util.EnumMap;

import pl.training.module4.encapsulation.EquipmentCatalog;
import pl.training.module4.model.EquipmentType;
import pl.training.module4.model.RentalRequest;
import pl.training.module4.pricing.RentalPricing;

public final class Module4Examples {
    private Module4Examples() {
    }

    public static void main(String[] args) {
        RentalRequest request = new RentalRequest(
                " Acme ",
                EquipmentType.GENERATOR,
                8,
                true,
                true);

        String legacyQuote = new pl.training.module4.stage0.RentalQuoteService()
                .createQuote(request);
        String refactoredQuote = new pl.training.module4.stage3.RentalQuoteService(
                RentalPricing.standard())
                .createQuote(request);

        if (!legacyQuote.equals(refactoredQuote)) {
            throw new IllegalStateException("Refactoring changed the quote");
        }

        EnumMap<EquipmentType, BigDecimal> sourceRates =
                new EnumMap<>(EquipmentType.class);
        sourceRates.put(EquipmentType.DRILL, new BigDecimal("39.99"));
        EquipmentCatalog catalog = new EquipmentCatalog(
                "Summer rental",
                sourceRates);
        var snapshot = catalog.dailyRates();
        catalog.changeDailyRate(
                EquipmentType.DRILL,
                new BigDecimal("42.00"));

        System.out.print(refactoredQuote);
        System.out.println("Quote stages equivalent: true");
        System.out.println(
                "Catalog snapshot isolated: "
                        + snapshot.get(EquipmentType.DRILL)
                                .equals(new BigDecimal("39.99")));
    }
}
~~~

Oczekiwany wynik:

~~~text
RENTAL QUOTE
Customer: ACME
Equipment: GENERATOR
Days: 8
Base: 960.00
Discount: 96.00
Insurance: 64.00
Delivery: 25.00
Net: 953.00
VAT: 219.19
Total: 1172.19
Quote stages equivalent: true
Catalog snapshot isolated: true
~~~

## 13. Warsztat praktyczny

Każde ćwiczenie rozpoczyna się od zielonego testu. Uczestnik zapisuje kolejne kroki osobno i po każdym uruchamia co najmniej test najbliższy zmienianemu kodowi. Jeżeli narzędzie IDE odmawia wykonania operacji, najpierw należy ustalić jego warunek wstępny. Ręczne przepisanie kodu bez tej analizy nie jest rozwiązaniem problemu.

### 13.1. Ćwiczenie 1: lokalne porządkowanie metody

Czas pracy: 30 minut. Omówienie: 10 minut.

Punkt startowy:

- <code>pl.training.module4.stage0.RentalQuoteService</code>,
- <code>RentalQuoteServiceCharacterizationTest</code>.

Zadanie:

1. Uruchom test charakterystyki i celowo zmień próg rabatu, aby sprawdzić, czy test jest czuły na błąd.
2. Cofnij kontrolowaną zmianę.
3. Zmień nazwy parametru oraz lokalnych wartości tak, aby opisywały role w wycenie.
4. Nazwij próg rabatu, stawkę ubezpieczenia, opłatę dostawy i VAT.
5. Wydziel obliczenia rabatu, ubezpieczenia i dostawy.
6. Nazwij predykat kwalifikacji do rabatu.
7. Wydziel budowę dokumentu.
8. Usuń zmienne, które po wydzieleniach jedynie przekazują wynik dalej i nie wnoszą znaczenia.

Ograniczenia:

- nie zmieniaj publicznej sygnatury <code>createQuote</code>,
- zachowaj kolejność i miejsca zaokrąglania,
- nie przenoś jeszcze kodu do nowej klasy,
- po każdym kroku uruchom test.

Kryteria akceptacji:

- pełny dokument jest identyczny bajt po bajcie,
- 6 dni nie daje rabatu, a 7 dni go daje,
- VAT dla jednodniowego wynajmu wiertnicy wynosi 9,20,
- w metodzie publicznej nie pozostały jednoliterowe nazwy,
- każdy nazwany literał ma jednoznaczne znaczenie domenowe,
- wydzielone metody mają spójny poziom abstrakcji.

### 13.2. Ćwiczenie 2: Extract i Inline pod presją semantyki

Czas pracy: 25 minut. Omówienie: 10 minut.

Dla każdego przypadku ustal, czy transformacja zachowuje zachowanie. Zapisz obserwację, która może się zmienić, oraz test potrzebny przed operacją.

1. Zmienna <code>now</code> przechowuje pojedynczy wynik <code>clock.instant()</code> używany dwa razy. Rozważ Inline Variable.
2. Warunek ma postać <code>account != null && account.isActive()</code>. Rozważ wcześniejsze obliczenie prawego operandu przez Extract Variable.
3. Argument metody wykonuje zapis audytowy, a po ręcznym Extract Method pojawia się w dwóch wywołaniach.
4. Wyrażenie lambda jest przekazywane do przeciążonej metody. Rozważ Extract Variable z <code>var</code> oraz z jawnym typem docelowym.
5. Jednoliniowa metoda instancyjna może być przesłonięta w podklasie. Rozważ Inline Method w klasie bazowej.
6. Jednoliniowa metoda jest oznaczona <code>synchronized</code>. Rozważ osobno wariant instancyjny i statyczny przed zastąpieniem wywołania jej treścią.
7. Prywatny delegat <code>calculatePrice</code> z etapu 2 tylko wywołuje <code>pricing.calculate(request)</code>. Rozważ Inline Method.
8. Lokalna zmienna przechowuje <code>new BigDecimal("0.23")</code> w dwóch miejscach. Rozważ Extract Constant i oceń, czy liczba reprezentuje jedno pojęcie.

Następnie wykonaj bezpieczny Inline Method pomiędzy etapami 2 i 3 oraz uruchom <code>RentalQuoteStagesEquivalenceTest</code>.

Kryteria akceptacji:

- ocena uwzględnia liczbę i kolejność ewaluacji,
- analiza rozróżnia statyczne wiązanie od dynamicznej dyspozycji,
- analiza rozróżnia monitor odbiorcy metody instancyjnej od monitora obiektu <code>Class</code> metody statycznej,
- dla lambdy zostaje zachowany typ docelowy,
- delegat zostaje usunięty tylko w przypadku bez ukrytego kontraktu.

### 13.3. Ćwiczenie 3: Extract Class oraz przeniesienie odpowiedzialności

Czas pracy: 35 minut. Omówienie: 15 minut.

Punkt startowy: <code>pl.training.module4.stage1.RentalQuoteService</code>.

Zadanie:

1. Nazwij odpowiedzialność, która ma zostać wydzielona, oraz jej klientów.
2. Wprowadź typ wyniku obliczeń <code>PriceBreakdown</code>.
3. Wprowadź w klasie źródłowej prywatny punkt <code>calculatePrice</code>, który nadal wykonuje istniejący algorytm.
4. Utwórz <code>RentalPricing</code>, przenieś do niego implementację oraz zastąp ciało źródłowej metody delegowaniem.
5. Przenieś metody wyceny wraz z używanymi stawkami i stałymi.
6. Pozostaw formatowanie dokumentu w <code>RentalQuoteService</code>.
7. Dodaj konstruktor wstrzykujący obiekt wyceny, zachowując bezargumentowy punkt wejścia.
8. Usuń duplikaty stawek i reguł z klasy źródłowej.
9. Dodaj test jednostkowy nowego obiektu oraz uruchom test równoważności wszystkich etapów.

Przed rozpoczęciem odpowiedz:

- kto jest właścicielem stawek,
- czy konfiguracja jest wspólna dla wszystkich ofert,
- jaki jest cykl życia obiektu wyceny,
- czy przenoszona metoda korzysta z monitora, przesłonięcia, dostępu pakietowego lub adnotacji frameworka,
- czy stary punkt wejścia musi pozostać ze względu na zewnętrznych klientów.

Kryteria akceptacji:

- istnieje jedno źródło prawdy dla stawek i progu rabatu,
- klasa dokumentu nie zna algorytmu wyceny,
- klasa wyceny nie zna tekstowego formatu dokumentu,
- zależności są przekazywane jawnie,
- wynik wyceny ma nazwane składowe,
- wszystkie zatwierdzone oferty są identyczne na każdym etapie.

### 13.4. Ćwiczenie 4: kontrola mutowalnego stanu

Czas pracy: 30 minut. Omówienie: 15 minut.

Punkt startowy: <code>LegacyEquipmentCatalog</code>.

Zadanie wykonaj w dwóch oddzielnych fazach.

Faza A, zachowanie istniejącego kontraktu:

1. Scharakteryzuj zapis do publicznego pola <code>name</code>.
2. Scharakteryzuj alias do mapy wejściowej i możliwość mutacji mapy zwracanej klientowi.
3. Ukryj pola.
4. Dodaj najwęższe akcesory i operacje przejściowe, które zachowują dotychczasowe obserwacje.
5. Zmigruj kontrolowanych klientów.

Faza B, zatwierdzona zmiana kontraktu:

1. Zdecyduj, czy konstruktor ma przejąć własność mapy przez kopię.
2. Zdefiniuj reguły nazwy i stawki.
3. Zastąp ogólny setter nazwanymi operacjami domenowymi.
4. Wybierz żywy niemodyfikowalny widok albo niemodyfikowalną migawkę.
5. Dodaj testy dla modyfikacji źródła, wyniku i właściciela po pobraniu kolekcji.
6. Oceń zachowanie <code>null</code>, mutowalnych elementów oraz współbieżności.

Kryteria akceptacji:

- faza A nie ukrywa zmiany zachowania pod nazwą refaktoryzacji,
- faza B ma jawnie opisany nowy kontrakt,
- żaden klient nie otrzymuje modyfikowalnego aliasu do struktury wewnętrznej,
- test rozróżnia migawkę od żywego widoku,
- walidacja jest wykonywana w jednym miejscu,
- uczestnik potrafi wyjaśnić, dlaczego niemodyfikowalna kolekcja nie oznacza kopii głębokiej ani bezpieczeństwa wątkowego.

### 13.5. Wspólna retrospektywa

Po każdym ćwiczeniu zespół odpowiada na cztery pytania:

1. Jaka obserwowalna właściwość była chroniona?
2. Który krok był czysto mechaniczny, a który wymagał decyzji projektowej?
3. Jaki najmniejszy test dawał wiarygodny sygnał?
4. W którym momencie należało zakończyć serię transformacji?

## 14. Rozwiązania i omówienie ćwiczeń

### 14.1. Ćwiczenie 1

Oczekiwany kierunek odpowiada etapowi 1. Zalecana kolejność:

1. Rename lokalnych symboli, ponieważ zmienia najmniejszy obszar i ułatwia czytanie następnych kroków.
2. Extract Constant oraz Replace Magic Numbers, ponieważ ujawniają reguły bez zmiany przepływu.
3. Extract Variable dla powtarzających się, czystych wartości używanych w jednym obliczeniu.
4. Extract Method dla kolejnych spójnych fragmentów.
5. Encapsulate Conditional dla progu rabatu.
6. Extract Method dla budowy dokumentu.
7. Inline Variable dla zmiennej <code>q</code>, która jedynie poprzedzała <code>return</code>.

Nie ma potrzeby wydzielania każdej operacji arytmetycznej. Nazwy <code>netAmount</code>, <code>vat</code> i <code>total</code> wystarczająco opisują kolejne kroki, a nadmiar jednoliniowych metod utrudniłby czytanie algorytmu.

Stała <code>ZERO_MONEY</code> nie jest wyłącznie optymalizacją tworzenia obiektu. Informuje o przyjętej skali wartości zerowej w dokumencie. Stawki sprzętu pozostają na etapie 1 w mapie instancyjnej, ponieważ będą przenoszone wraz z odpowiedzialnością za wycenę.

### 14.2. Ćwiczenie 2

| Przypadek | Ocena | Uzasadnienie |
| --- | --- | --- |
| Inline zmiennej z <code>clock.instant()</code> używanej dwa razy | niebezpieczne | jedno odczytanie czasu zmieniłoby się w dwa, które mogą zwrócić różne wartości |
| Wcześniejsze obliczenie <code>account.isActive()</code> | niebezpieczne | znika ochrona krótkiego spięcia i dla <code>null</code> pojawia się wyjątek |
| Argument z efektem ubocznym skopiowany do dwóch wywołań | niebezpieczne | zmienia liczbę i prawdopodobnie kolejność efektów |
| Lambda wydzielona do <code>var</code> | niepoprawne lub pozbawione wymaganego kontekstu | lambda potrzebuje typu docelowego; jawny odpowiedni interfejs funkcyjny zachowuje ten kontekst |
| Inline metody, którą może przesłonić podklasa | zwykle niebezpieczne | wywołanie podlega dynamicznej dyspozycji, a wklejona implementacja klasy bazowej nie |
| Inline metody <code>synchronized</code> | niebezpieczne bez odtworzenia blokady | dla metody instancyjnej nie zostanie automatycznie zachowany monitor odbiorcy, a dla statycznej monitor obiektu <code>Class</code> klasy deklarującej |
| Inline prywatnego delegata etapu 2 | bezpieczne przy ustalonych założeniach | delegat ma jedno wywołanie, brak adnotacji, proxy, przesłonięcia, blokady i odwołań tekstowych |
| Wydzielenie VAT do stałej | bezpieczne, jeśli oba literały oznaczają tę samą regułę | należy centralizować wspólną wiedzę, nie jedynie równą reprezentację tekstową |

Narzędzie IDE może zapobiec części błędów typowania. Nie potwierdzi jednak, że dwa wywołania zegara, repozytorium albo loggera są semantycznie równoważne jednemu.

### 14.3. Ćwiczenie 3

Odpowiedzialność <code>RentalPricing</code> brzmi: obliczyć nazwany rozkład ceny dla poprawnego żądania i skonfigurowanych reguł. Odpowiedzialność <code>RentalQuoteService</code> brzmi: zbudować dokument oferty na podstawie żądania i wyniku wyceny.

Pola <code>dailyRates</code> oraz <code>longRentalDiscountRate</code> poruszają się razem z metodami, które je interpretują. Stałe dodatków, VAT, progu rabatu i funkcja <code>money</code> również należą do algorytmu wyceny. Formatowanie, normalizacja nazwy klienta i etykiety dokumentu pozostają po stronie prezentacji.

<code>PriceBreakdown</code> zastępuje długą listę równoległych wartości jednym typem wyniku. Nie jest przypadkowym pojemnikiem. Jego pola reprezentują spójny rezultat wyceny, a konstruktor kompaktowy chroni format kwot.

Przejściowy delegat w etapie 2 jest celowo wprowadzony podczas Move Method. Umożliwia osobne sprawdzenie przeniesienia i późniejszego Inline Method. Bezargumentowy konstruktor deleguje do standardowej konfiguracji i zachowuje dotychczasowy punkt konstrukcji, a przeciążenie umożliwia jawne wstrzyknięcie <code>RentalPricing</code>. Stan końcowy odpowiada etapowi 3.

### 14.4. Ćwiczenie 4

Faza A i faza B nie powinny zostać połączone w jeden nieprzejrzysty commit.

W fazie A prywatne pole może nadal wskazywać mapę klienta, a akcesor może nadal zwracać ten sam modyfikowalny obiekt. Takie rozwiązanie nie daje docelowej hermetyzacji, ale pozwala najpierw migrować składniową formę dostępu bez zmiany aliasowania.

W fazie B przykład wybiera następujący kontrakt:

- konstruktor kopiuje wpisy do własnego <code>EnumMap</code>,
- wszystkie zmiany stawki przechodzą przez <code>changeDailyRate</code>,
- stawka jest normalizowana do dwóch miejsc metodą HALF_UP, a inwariant dodatniości jest sprawdzany po normalizacji,
- nazwa zmienia się tylko przez <code>renameTo</code>,
- <code>dailyRates()</code> zwraca niemodyfikowalną migawkę,
- późniejsze zmiany katalogu nie są widoczne w pobranej wcześniej migawce,
- <code>BigDecimal</code> jest niemutowalny, więc płytka kopia wartości jest wystarczająca.

Gdyby klient potrzebował obserwować późniejsze zmiany właściciela, właściwy byłby żywy niemodyfikowalny widok, na przykład wynik oparty na <code>Collections.unmodifiableMap</code>. To inny kontrakt. Nie należy wybierać go przypadkowo.

## 15. Sprawdzenie wiedzy

### Pytania

1. Co musi pozostać niezmienione podczas refaktoryzacji?
2. Dlaczego zielony test jednostkowy nie dowodzi kompatybilności publicznej biblioteki?
3. Jakie trzy grupy zmiennych trzeba rozpoznać przed Extract Method?
4. Kiedy Extract Variable może zmienić zachowanie?
5. Czym Replace Magic Numbers różni się od mechanicznego Extract Constant?
6. Dlaczego publiczna stała typu prostego lub <code>String</code> wymaga ostrożności przy zmianie wartości?
7. Kiedy Inline Variable może zwiększyć liczbę efektów ubocznych?
8. Dlaczego Inline Method metody, którą może przesłonić podklasa, może zmienić wynik?
9. Co może zostać utracone przy Inline Method metody <code>synchronized</code>?
10. Jakie kryterium jest ważniejsze przy Move Method niż liczba wywołań?
11. Jak bezpiecznie wykonać Move Field?
12. Jaki sygnał uzasadnia Extract Class?
13. Dlaczego prywatne pole z publicznym setterem nie gwarantuje hermetyzacji?
14. Czym różni się niemodyfikowalny widok od niemodyfikowalnej migawki?
15. Czy <code>Map.copyOf</code> wykonuje kopię głęboką?
16. Czy zwracanie niemodyfikowalnej mapy zapewnia bezpieczeństwo wątkowe obiektu?
17. Jak Extract Method może naruszyć krótkie spięcie?
18. Dlaczego test nowej implementacji wyłącznie przez porównanie ze starą jest niewystarczający?
19. Kiedy Rename wymaga strategii migracji zamiast jednorazowej operacji IDE?
20. Dlaczego Extract i Inline nie są sprzecznymi zaleceniami?

### Odpowiedzi

1. Ustalony zakres obserwowalnego zachowania, obejmujący odpowiednie wyniki, wyjątki, efekty, kolejność, a czasem także zgodność API i właściwości operacyjne.
2. Nie obejmuje automatycznie wcześniej skompilowanych klientów, refleksji, serializacji, konfiguracji, frameworków ani integracji.
3. Wartości wejściowe fragmentu, wartości lokalne używane tylko wewnątrz oraz wartości zmieniane wewnątrz i potrzebne później.
4. Gdy zmienia liczbę, kolejność lub moment ewaluacji, przez co wpływa na wyjątek, stan, czas, I/O albo wybór przeciążenia.
5. Extract Constant tworzy nazwę dla wystąpienia. Replace Magic Numbers wymaga ustalenia, które wystąpienia reprezentują tę samą decyzję i powinny mieć wspólnego właściciela.
6. Taka publiczna wartość, jeśli spełnia warunki stałego wyrażenia, może zostać wkompilowana do kodu klienta. Zmiana biblioteki bez rekompilacji klienta może pozostawić starą wartość.
7. Gdy inicjalizator nie jest czysty, a podstawienie wykonuje go w wielu miejscach lub w innych gałęziach.
8. Wywołanie metody wybiera implementację dynamicznie, natomiast wklejenie ciała klasy bazowej może ominąć implementację podtypu.
9. Automatyczne wejście w monitor odbiorcy dla metody instancyjnej albo w monitor obiektu <code>Class</code> klasy deklarującej dla metody statycznej, a wraz z nim synchronizacja i relacja widoczności pamięci.
10. Własność odpowiedzialności, danych i inwariantów, uwzględniająca cykl życia oraz granice kontraktu.
11. Wprowadzić nowego właściciela, skierować odczyty i zapisy do jednego źródła prawdy, migrować klientów, a stare pole usunąć dopiero po zniknięciu wszystkich użyć.
12. Spójna grupa danych i zachowań ma odrębną odpowiedzialność, język oraz powód zmiany.
13. Setter nadal może zezwalać na dowolną zmianę i pozostawiać inwarianty klientom. Hermetyzacja polega na kontroli kontraktu, nie samym modyfikatorze dostępu.
14. Widok blokuje mutację przez klienta, ale pokazuje późniejsze zmiany właściciela. Migawka zachowuje stan z chwili utworzenia.
15. Nie. Kopiuje strukturę kolekcji, a nie wnętrze obiektów będących kluczami lub wartościami.
16. Nie. Nie definiuje synchronizacji pozostałych operacji ani bezpiecznej publikacji całego stanu.
17. Przez wcześniejsze obliczenie prawego operandu lub niewybranej gałęzi, które wcześniej mogły nie zostać wykonane.
18. Stara implementacja może zawierać ten sam błąd. Potrzebne są niezależne oczekiwania wynikające z zatwierdzonego kontraktu.
19. Gdy nazwa należy do publicznego API albo występuje w refleksji, danych, konfiguracji, serializacji lub u niezależnie wdrażanych klientów.
20. Obie zmieniają poziom pośrednictwa. Extract nadaje nazwę i granicę, a Inline usuwa pośrednictwo, które nie przekazuje znaczenia. Wybór zależy od kontekstu.

## 16. Listy kontrolne

### 16.1. Przed każdym krokiem

- [ ] Potrafię wskazać zachowanie, które ma pozostać niezmienione.
- [ ] Test obejmuje ryzyko właściwe dla tej transformacji.
- [ ] Sprawdziłem liczbę i kolejność ewaluacji.
- [ ] Znam wyjątki i efekty uboczne zmienianego fragmentu.
- [ ] Ustaliłem, czy symbol należy do publicznego albo tekstowego kontraktu.
- [ ] Zmiana jest na tyle mała, że potrafię wskazać jej przyczynę po awarii testu.

### 16.2. Extract Method, Variable i Constant

- [ ] Fragment ma nazwę wyrażającą intencję.
- [ ] Dane wejściowe, lokalne i wyjściowe są jawne.
- [ ] Wydzielenie nie zmienia short-circuit ani zasięgu zmiennej wzorca.
- [ ] Wyrażenie nie jest wykonywane wcześniej, później ani częściej.
- [ ] Typ jawny zachowuje kontekst przeciążenia, lambdy i inferencji.
- [ ] Nazwana stała reprezentuje jedno pojęcie oraz ma właściwego właściciela.
- [ ] Publiczna stała nie wprowadza nieprzemyślanego kontraktu binarnego.

### 16.3. Inline Method i Variable

- [ ] Pośrednictwo nie przekazuje istotnego znaczenia.
- [ ] Metoda nie jest przesłaniana ani używana przez refleksję lub framework.
- [ ] Nie usuwam granicy proxy, transakcji, blokady ani monitoringu.
- [ ] Inicjalizator zmiennej jest bezpieczny do wykonania w każdym miejscu podstawienia.
- [ ] Typowanie i wybór przeciążenia pozostają takie same.
- [ ] Po operacji kod jest prostszy w swoim kontekście.

### 16.4. Rename

- [ ] Nowa nazwa opisuje rolę, a nie implementację.
- [ ] Przejrzałem użycia typowane i tekstowe.
- [ ] Sprawdziłem konfigurację, dane, serializację, ORM i refleksję.
- [ ] Dla publicznego API określiłem kompatybilność źródłową i binarną.
- [ ] Jeśli potrzeba, stara nazwa deleguje przez okres migracji.
- [ ] Pełny moduł oraz klienci kompilują się ponownie.

### 16.5. Move Method, Move Field i Extract Class

- [ ] Nowy właściciel ma odpowiedzialność i dane potrzebne do operacji.
- [ ] Cykl życia przenoszonego stanu jest zgodny z cyklem życia właściciela.
- [ ] Przez cały czas istnieje jedno źródło prawdy.
- [ ] Delegat chroni stary kontrakt tak długo, jak jest potrzebny.
- [ ] Przeniesienie nie omija dynamicznej dyspozycji, monitora ani proxy.
- [ ] Dostęp pakietowy, adnotacje, ORM i serializacja zostały sprawdzone.
- [ ] Wydzielona klasa ma spójny powód zmiany i sensowną nazwę.
- [ ] Nowa zależność nie tworzy cyklu.

### 16.6. Encapsulate Field i Collection

- [ ] Znam dotychczasowe aliasy i wszystkie drogi zapisu.
- [ ] Nie utożsamiam settera z hermetyzacją.
- [ ] Operacje modyfikujące wyrażają reguły właściciela.
- [ ] Świadomie wybrałem widok, migawkę albo kopię modyfikowalną.
- [ ] Testuję modyfikację źródła, wyniku i właściciela po odczycie.
- [ ] Zdefiniowałem obsługę <code>null</code>, kolejności i duplikatów.
- [ ] Uwzględniłem mutowalność elementów.
- [ ] Nie przypisuję kolekcji niemodyfikowalnej gwarancji bezpieczeństwa wątkowego.
- [ ] Zmianę własności lub walidacji nazywam zmianą kontraktu.

### 16.7. Przed zakończeniem warsztatu

- [ ] Każdy etap kompiluje się na Javie 25.
- [ ] Test charakterystyki przechodzi.
- [ ] Testy nowej odpowiedzialności przechodzą.
- [ ] Test równoważności korzysta z niezależnych oczekiwań.
- [ ] Nie pozostały dwie kopie tej samej reguły.
- [ ] Publiczny punkt wejścia zachowuje ustalony kontrakt.
- [ ] Program demonstracyjny uruchamia się bez dodatkowej konfiguracji.
- [ ] Potrafię wskazać, które kroki były refaktoryzacją, a które zmianą zachowania.

## 17. Podsumowanie

Podstawowe refaktoryzacje są małymi transformacjami, ale ich bezpieczeństwo zależy od precyzyjnego rozumienia semantyki. Rename może dotknąć publicznego lub tekstowego kontraktu. Extract i Inline mogą zmienić liczbę ewaluacji, wybór przeciążenia, dynamiczną dyspozycję albo blokadę. Move wymaga jednego źródła prawdy i właściwego właściciela. Hermetyzacja wymaga decyzji o dozwolonych operacjach, aliasowaniu i własności stanu.

Najważniejszą umiejętnością nie jest rozpoznanie nazwy techniki. Jest nią ułożenie sekwencji, w której każdy krok ma jawny cel, odpowiedni test i mały promień zmiany. Dzięki temu lokalne porządki mogą bezpiecznie doprowadzić do Extract Class oraz przesunięcia odpowiedzialności, bez jednoczesnego przepisywania algorytmu.

Studium przypadku zachowuje wynik etapów od 0 do 3 i osobno pokazuje świadomą zmianę kontraktu katalogu. To rozdzielenie jest kluczowe: refaktoryzację można oceniać przez równoważność obserwacji, natomiast nowe reguły własności i walidacji wymagają osobnej decyzji produktowej lub projektowej.
