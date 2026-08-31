# Moduł 2. Fundamenty refaktoryzacji

## Cel modułu

Celem modułu jest zbudowanie bezpiecznego sposobu zmiany struktury istniejącego kodu. Uczestnik uczy się określać zachowanie, które ma pozostać niezmienione, dobierać adekwatną sieć testów, wykonywać małe transformacje oraz wprowadzać punkty podmiany w kodzie, którego nie można jeszcze testować w izolacji.

Moduł nie sprowadza bezpieczeństwa do wysokiego pokrycia ani do użycia biblioteki mockującej. Testy, kompilator, narzędzia IDE, przegląd różnicy i obserwacja systemu dostarczają uzupełniających się dowodów. Żaden pojedynczy mechanizm nie dowodzi pełnej równoważności zachowania.

## Efekty uczenia się

Po ukończeniu modułu uczestnik:

- odróżnia refaktoryzację od naprawy defektu, optymalizacji, modernizacji i zmiany funkcjonalnej,
- określa obserwowalne zachowanie wymagające zachowania podczas konkretnej zmiany,
- prowadzi refaktoryzację w małych krokach z krótką pętlą informacji zwrotnej,
- dobiera testy według ryzyka, zakresu, szybkości, realizmu i wartości diagnostycznej,
- interpretuje piramidę testów jako heurystykę, a nie obowiązkową proporcję,
- rozróżnia role stub, spy, fake i mock oraz stosuje je świadomie,
- wykorzystuje pokrycie kodu do wykrywania luk, bez traktowania procentu jako miary poprawności,
- tworzy testy charakteryzujące aktualne zachowanie kodu legacy,
- rozpoznaje seam i jego punkt aktywacji,
- wykonuje minimalne rozrywanie zależności potrzebne do uruchomienia i obserwowania kodu w teście.

## Zakres

1. Istota i cele refaktoryzacji
2. Zasady bezpiecznej refaktoryzacji
3. Znaczenie testów w procesie refaktoryzacji
4. Piramida testów i strategie testowania
5. Obiekty zastępcze: stub, spy, fake i mock
6. Pokrycie kodu: zalety, ograniczenia i nadużycia
7. Testy charakteryzujące
8. Seams i rozrywanie zależności
9. Warsztat praktyczny

## Konwencje przykładów

Przykłady używają Javy 25, JUnit Jupiter 6.1.3 i JaCoCo 0.8.15. Kod domenowy jest celowo uproszczony. Nie stanowi kompletnego modelu płatności, podatków, faktur ani komunikacji z klientem. Klasy z przedrostkiem `Legacy` oraz ich nowsze odpowiedniki mają różne nazwy wyłącznie po to, aby kolejne etapy mogły współistnieć i być uruchamiane w jednym projekcie. W rzeczywistej refaktoryzacji byłyby kolejnymi wersjami tego samego kodu.

Kod znajduje się w projekcie Maven `refactoring-legacy`. Bazowym pakietem jest `pl.training`, a przykłady tego modułu należą do pakietu `pl.training.module2`:

| Lokalizacja | Zawartość |
| --- | --- |
| `src/main/java/pl/training/module2` | kod produkcyjny przykładów i klasa `Module2Examples` |
| `src/test/java/pl/training/module2` | testy JUnit oraz ręcznie napisane obiekty zastępcze |
| `target/site/jacoco/index.html` | raport pokrycia tworzony podczas fazy `verify` |

Kompilacja, testy i uruchomienie przykładów:

```shell
cd refactoring-legacy
mvn clean verify
java -cp target/classes pl.training.module2.Module2Examples
```

Raport JaCoCo powstaje dla całego projektu. Podczas ćwiczeń należy analizować pakiet `pl.training.module2`, a nie wyłącznie globalny procent.

## Organizacja pracy

Sugerowany czas pracy synchronicznej wynosi 210 minut:

| Część | Czas |
| --- | ---: |
| teoria wraz z krótkimi aktywnościami | 65 minut |
| ćwiczenie 1 i omówienie | 20 minut |
| ćwiczenie 2 i omówienie | 45 minut |
| ćwiczenie 3 i omówienie | 35 minut |
| ćwiczenie 4 i omówienie | 35 minut |
| sprawdzenie wiedzy i podsumowanie | 10 minut |

Na zajęciach obowiązkowe są definicja refaktoryzacji, cykl bezpiecznej zmiany, ograniczenia testów i pokrycia, role obiektów zastępczych, testy charakteryzujące oraz pojęcia seam i punkt aktywacji. Szczegółowe antywzorce, rozbudowane odpowiedzi wzorcowe i listy kontrolne mogą służyć jako materiał do samodzielnej pracy.

## 1. Istota i cele refaktoryzacji

### 1.1. Precyzyjna definicja

Refaktoryzacja jest kontrolowaną zmianą wewnętrznej struktury oprogramowania, której celem jest ułatwienie rozumienia i dalszych modyfikacji bez zmiany jego obserwowalnego zachowania.

Termin ma dwa powiązane znaczenia:

- refaktoryzacja jako pojedyncza transformacja, na przykład zmiana nazwy, wydzielenie metody albo przeniesienie pola,
- refaktoryzowanie jako proces wykonywania serii małych transformacji zachowujących zachowanie.

Małe kroki nie oznaczają małego celu. Duża zmiana projektu może powstać z wielu lokalnych transformacji, po których kod nadal się buduje, testy pozostają zielone, a ostatnią różnicę można łatwo przeanalizować lub wycofać.

### 1.2. Co oznacza zachowanie obserwowalne

Granica obserwowalności zależy od systemu i konkretnej zmiany. Nie ogranicza się do wartości zwracanej przez metodę.

| Rodzaj zachowania | Przykład |
| --- | --- |
| wynik | zwrócona kwota, kod decyzji, wygenerowany dokument |
| błąd | typ wyjątku, warunek jego wystąpienia, kod odpowiedzi |
| efekt uboczny | zapis rekordu, wysłanie wiadomości, publikacja zdarzenia |
| protokół | liczba i kolejność wywołań, jeżeli mają znaczenie transakcyjne |
| dane | format pliku, schemat komunikatu, reguła zaokrąglenia |
| kontrakt publiczny | sygnatura API, widoczność typu, kompatybilność binarna |
| właściwość pozafunkcjonalna | czas odpowiedzi, użycie pamięci lub przepustowość, jeżeli są częścią wymagań |

Prywatna metoda, liczba klas i wewnętrzna kolejność czystych obliczeń zwykle nie są kontraktem. Mogą się zmieniać, jeżeli publiczne obserwacje pozostają zgodne. Inaczej jest, gdy refleksja, serializacja, konfiguracja albo zewnętrzny konsument zależy od elementu pozornie wewnętrznego.

Nie należy więc mówić wyłącznie: „zachowanie się nie zmieni”. Należy nazwać, jakie obserwacje mają pozostać niezmienione i skąd pochodzi o nich wiedza.

### 1.3. Refaktoryzacja a inne rodzaje zmian

| Zmiana | Czy sama jest refaktoryzacją? | Dlaczego |
| --- | --- | --- |
| zmiana nazwy prywatnej metody wraz z aktualizacją wywołań | zwykle tak | celem jest poprawa struktury i rozumienia, a kontrakt pozostaje bez zmian |
| korekta błędnej reguły zaokrąglania | nie | celowo zmienia wynik |
| dodanie nowego wariantu rabatu | nie | rozszerza zachowanie funkcjonalne |
| wydzielenie obliczenia do metody bez zmiany wyniku | tak | jest transformacją struktury |
| dodanie pamięci podręcznej | zwykle optymalizacja | zmienia charakterystyki czasu, pamięci i potencjalnie spójności |
| przejście na inną platformę | modernizacja | może zawierać refaktoryzacje, ale ma szerszy zakres i ryzyka |
| pełne przepisanie klasy na podstawie wymagań | nie | tworzy nową implementację i wymaga odtworzenia zachowania |
| usunięcie ujawnionego defektu podczas porządkowania | nie w tym samym kroku | poprawkę należy rozpoznać jako jawną zmianę zachowania |

Ta sama operacja składniowa może mieć inny charakter zależnie od celu i kontraktu. Zmiana publicznej nazwy w aplikacji, której wszystkich klientów kontroluje jeden zespół, może być częścią refaktoryzacji. Ta sama zmiana w opublikowanej bibliotece może naruszyć kompatybilność i stać się migracją API.

### 1.4. Cele ekonomiczne i projektowe

Refaktoryzacja nie jest konkursem estetycznym. Ma obniżyć koszt lub ryzyko dalszej pracy. Typowe cele to:

- przygotowanie miejsca pod zaplanowaną funkcję,
- utrwalenie wiedzy zdobytej podczas analizy,
- skrócenie drogi od zmiany do wiarygodnej informacji zwrotnej,
- rozdzielenie odpowiedzialności zmieniających się z różnych powodów,
- ograniczenie sprzężenia i promienia oddziaływania,
- udostępnienie punktu testowania lub obserwacji,
- usunięcie potwierdzonej duplikacji wiedzy,
- przywrócenie możliwości małych, częstych zmian.

Nie są samodzielnymi celami:

- maksymalizacja liczby klas lub interfejsów,
- zastosowanie wzorca projektowego dla samego wzorca,
- skrócenie każdej metody do arbitralnej liczby linii,
- osiągnięcie wybranego procentu pokrycia,
- usunięcie wszystkich symptomów z całego systemu,
- jednorazowe osiągnięcie idealnego projektu.

Najłatwiej uzasadnić inwestycję w obszar, który będzie wkrótce zmieniany. Poprawa stabilnego kodu o krótkim horyzoncie życia może nigdy nie zwrócić kosztu i ryzyka interwencji.

### 1.5. Kiedy refaktoryzować

Refaktoryzacja może być:

- przygotowawcza: tworzy prostszą drogę dla najbliższej funkcji,
- wspierająca zrozumienie: zapisuje w strukturze wiedzę zdobytą podczas czytania,
- oportunistyczna: niewielka poprawa wykonywana przy bezpośredniej pracy w danym miejscu,
- zaplanowana: ograniczony wysiłek dotyczący potwierdzonego problemu przekrojowego,
- długoterminowa: seria małych zmian przesuwających granicę architektoniczną bez zatrzymywania rozwoju produktu.

Osobny projekt refaktoryzacyjny bywa potrzebny, ale nie powinien być domyślną odpowiedzią. Duże zakresy zwiększają czas bez informacji zwrotnej i ryzyko połączenia wielu założeń w jednym wdrożeniu.

### 1.6. Krótka aktywność: nazwij kontrakt

**Czas:** 4 minuty.

Dla każdej zmiany wskaż zachowanie, które może być obserwowalne:

1. zmiana typu prywatnego pola z `List` na `Set`,
2. zamiana dwóch zapisów do bazy miejscami,
3. przeniesienie klasy używanej przez mechanizm refleksji,
4. zastąpienie algorytmu szybszą implementacją,
5. zmiana prywatnej nazwy bez użycia refleksji i konfiguracji tekstowej.

Nie klasyfikuj zmiany przed nazwaniem konsumenta, obserwacji i sposobu weryfikacji.

## 2. Zasady bezpiecznej refaktoryzacji

### 2.1. Zacznij od znanego stanu

Przed pierwszym krokiem należy ustalić bazę odniesienia:

- projekt buduje się w powtarzalnym środowisku,
- istniejące testy przechodzą albo ich znane awarie są jawnie odizolowane,
- zakres planowanej zmiany i ważne kontrakty są nazwane,
- dostępne są reprezentatywne dane lub scenariusze,
- wiadomo, które testy dają szybką informację lokalną, a które sprawdzają integrację większej części systemu,
- istnieje bezpieczny sposób powrotu do poprzedniego kroku.

Stale czerwony test nie może pełnić funkcji czujnika nowej regresji. Należy go naprawić, odizolować z jednoznacznym uzasadnieniem albo potraktować jako znaną awarię w osobnym procesie. Ignorowanie wyniku całego zestawu szybko prowadzi do utraty zaufania.

### 2.2. Oddziel dwa tryby pracy

W trybie strukturalnym celem jest zachowanie uzgodnionych obserwacji. W trybie funkcjonalnym celem jest ich jawna zmiana. Tryby można przełączać często, ale w danym kroku programista powinien wiedzieć, który z nich realizuje. Fowler opisuje to rozróżnienie metaforą dwóch kapeluszy: kapelusza dodawania funkcji i kapelusza refaktoryzacji, których nie należy nosić jednocześnie.

Przykładowy przebieg:

1. Zapisać nowe wymaganie jako kryterium planowanej zmiany, pozostawiając istniejący zestaw testów zielony.
2. W trybie strukturalnym uprościć drogę do implementacji i po każdym kroku utrzymywać wszystkie testy zielone.
3. Przełączyć się na tryb funkcjonalny, dodać test nowego wymagania i zobaczyć jego kontrolowaną porażkę.
4. Zaimplementować nowe zachowanie, aż test stanie się zielony.
5. Ponownie uporządkować kod bez rozszerzania zmiany funkcjonalnej.

Jeżeli test charakteryzujący ujawnia podejrzany wynik, nie należy go po cichu aktualizować. Najpierw trzeba rozstrzygnąć, czy wynik jest defektem, historycznym kontraktem czy nieistotnym szczegółem.

### 2.3. Pętla małych kroków

Bezpieczna pętla refaktoryzacji wygląda następująco:

1. Określ najmniejszą transformację.
2. Wykonaj automatyczną operację IDE albo małą zmianę ręczną.
3. Skompiluj kod.
4. Uruchom najszybsze testy obejmujące zmienione zachowanie.
5. Obejrzyj różnicę i usuń przypadkowe modyfikacje.
6. Zapisz punkt przywracania, gdy kod jest spójny.
7. Powtórz.
8. Regularnie uruchamiaj szerszy zestaw testów.
9. Przed zakończeniem wykonaj pełną weryfikację wymaganych kontraktów.

Mały krok powinien być na tyle ograniczony, aby w przypadku porażki ostatnia transformacja była głównym podejrzanym. Wielogodzinna seria zmian przed pierwszym uruchomieniem testów usuwa tę przewagę.

### 2.4. Dobierz weryfikację do ryzyka

Kompilacja wykryje błędne typy i część nieaktualnych wywołań, ale nie zweryfikuje danych, efektów ubocznych ani semantyki. Test jednostkowy szybko sprawdzi regułę, ale nie potwierdzi mapowania ORM, serializacji czy konfiguracji brokera. Test systemowy obejmie integrację, lecz może wolno wskazywać przyczynę.

Weryfikacja powinna odpowiadać rodzajowi zmiany:

| Zmieniany element | Przydatna weryfikacja |
| --- | --- |
| czyste obliczenie | szybkie testy przykładów i wartości granicznych |
| mapowanie danych | test adaptera z rzeczywistym mechanizmem mapowania |
| format komunikatu | test kontraktu lub serializacji |
| kolejność skutków | test interakcji i test zachowania transakcyjnego |
| zapytanie wydajnościowe | test poprawności oraz osobny pomiar wydajności |
| publiczne API | testy konsumenta, kompatybilność i integracja |

### 2.5. Ogranicz szum w różnicy

Zmiana strukturalna nie powinna jednocześnie zawierać masowego formatowania, aktualizacji zależności, zmian nazw niezwiązanych z celem i nowej funkcji. Duża różnica utrudnia przegląd oraz wskazanie źródła regresji.

Automatyczne formatowanie jest użyteczne, lecz najlepiej wykonywać je jako osobny, mechaniczny krok. Krótkie punkty przywracania nie muszą oznaczać publikowania każdego kroku w historii głównej gałęzi. Mają umożliwiać lokalny powrót i rozdzielenie intencji.

### 2.6. Narzędzie nie zna całego kontraktu

Automatyczna refaktoryzacja IDE zwykle lepiej niż ręczna edycja aktualizuje odwołania widoczne dla analizatora. Nadal może nie rozpoznać:

- nazw klas zapisanych w konfiguracji,
- refleksji i dynamicznego ładowania,
- szablonów, zapytań i skryptów,
- formatów serializacji,
- kodu generowanego,
- zewnętrznych konsumentów biblioteki,
- zależności wdrożeniowych i operacyjnych.

Narzędzie zmniejsza ryzyko transformacji. Nie stanowi dowodu zachowania całego systemu.

### 2.7. Kiedy przerwać

Należy zatrzymać krok i wrócić do ostatniego znanego stanu, gdy:

- nie wiadomo, czy porażka testu oznacza regresję czy zmianę wymagania,
- zakres różnicy rośnie szybciej niż zrozumienie,
- trzeba jednocześnie zmienić wiele niezależnych kontraktów,
- testy stały się niestabilne lub zbyt wolne, aby prowadzić pracę,
- w trakcie refaktoryzacji ujawniono defekt wymagający decyzji biznesowej,
- minimalne rozrywanie zależności zaczyna przypominać nieplanowaną przebudowę architektury.

Przerwanie kroku nie jest porażką. Jest mechanizmem kontroli ryzyka.

## 3. Znaczenie testów w refaktoryzacji

### 3.1. Test dostarcza dowodu, nie pewności absolutnej

Zielony zestaw testów oznacza, że żaden test nie wykrył różnicy w zachowaniach, które wywołał i sprawdził. Nie oznacza, że:

- wszystkie wymagania są poprawnie opisane,
- wykonano każdą możliwą ścieżkę,
- sprawdzono wszystkie dane graniczne,
- asercje są wystarczająco czułe,
- środowisko testowe odpowiada produkcji,
- nie zmieniła się istotna właściwość pozafunkcjonalna.

Bezpieczeństwo rośnie przez połączenie testów z małym zakresem zmiany, analizą różnicy, kompilatorem, statyczną analizą, testami integracyjnymi i obserwacją operacyjną.

### 3.2. Testuj stabilne zachowanie

Test przywiązany do prywatnych metod, liczby klas albo nieistotnej kolejności wewnętrznych wywołań może blokować poprawną refaktoryzację. Test powinien obserwować najwęższą stabilną granicę, która nadal wykrywa ważną zmianę.

Weryfikacja stanu lub wyniku jest zwykle odporniejsza na zmianę implementacji. Weryfikacja interakcji jest właściwa, gdy interakcja stanowi kontrakt, na przykład:

- płatność nie może zostać pobrana dwukrotnie,
- zdarzenie musi zostać opublikowane po zatwierdzeniu transakcji,
- poufne dane nie mogą trafić do zewnętrznej bramki,
- operacja ponowienia musi zachować idempotencję.

Sprawdzanie każdej rozmowy między obiektami tylko dlatego, że biblioteka to umożliwia, odtwarza implementację w teście.

### 3.3. Cechy użytecznej sieci bezpieczeństwa

| Cecha | Znaczenie podczas refaktoryzacji |
| --- | --- |
| szybkość | wynik pojawia się przed kolejnym krokiem |
| determinizm | porażka wskazuje zmianę, a nie przypadkowy czas, sieć lub kolejność |
| czułość | istotna zmiana zachowania powoduje porażkę |
| stabilność strukturalna | poprawna zmiana wnętrza nie wymaga masowej aktualizacji testów |
| diagnostyka | komunikat i zakres testu pomagają znaleźć źródło problemu |
| realizm | granice wysokiego ryzyka są sprawdzane z rzeczywistą technologią tam, gdzie jest to potrzebne |
| utrzymywalność | dane i oczekiwania dają się zrozumieć oraz zmienić |

Szybki test o słabej asercji nie daje ochrony. Realistyczny test uruchamiany przez jedenaście godzin nie prowadzi małych kroków. Potrzebny jest portfel uzupełniających się testów.

### 3.4. Punkt zmiany i punkt testowania

Miejsce modyfikacji nie musi być najlepszym miejscem obserwacji. Reguła może być prywatna, a stabilny wynik widoczny dopiero na granicy usługi. Na początku bezpieczniej bywa zabezpieczyć szerszy przebieg, a po poprawie struktury dodać precyzyjne testy wydzielonej reguły.

Przed pracą należy odpowiedzieć:

1. Gdzie kod zostanie zmieniony?
2. Z jakiego miejsca można uruchomić to zachowanie?
3. Gdzie widać jego wynik lub efekt?
4. Które zależności uniemożliwiają kontrolowane wykonanie?
5. Jaki najmniejszy test wykryje niezamierzoną zmianę?

## 4. Piramida testów i strategie testowania

### 4.1. Piramida jest heurystyką

Piramida testów przypomina o dwóch typowych zależnościach:

- testów o małym zakresie, szybkim wykonaniu i precyzyjnej diagnozie powinno być wiele,
- wraz ze wzrostem zakresu, kosztu i podatności na niestabilność liczba testów zwykle maleje.

Nie wynika z tego uniwersalna proporcja, na przykład 70/20/10. Nie wynika też obowiązek zastępowania wszystkich zależności mockami. Jeżeli szeroki test jest szybki, deterministyczny, tani w utrzymaniu i daje dobrą diagnozę, może być bardzo wartościowy.

### 4.2. Nazwy warstw nie są jednoznaczne

„Jednostka” może oznaczać funkcję, klasę albo niewielki współpracujący komponent. „Test integracyjny” bywa nazwą zarówno testu jednego adaptera bazy danych, jak i testu kilku usług. Dlatego zespół powinien opisywać test przez jego właściwości, nie samą etykietę.

| Właściwość | Pytanie |
| --- | --- |
| zakres | Ile kodu i procesów uruchamia test? |
| interfejs | Czy test wywołuje metodę, API, komunikat czy interfejs użytkownika? |
| realizm zależności | Które elementy są rzeczywiste, a które zastąpione? |
| szybkość | Kiedy programista otrzyma wynik? |
| determinizm | Czy wynik zależy od sieci, czasu lub współdzielonego stanu? |
| diagnostyka | Jak dokładnie porażka wskazuje przyczynę? |
| wykrywane ryzyko | Jaką istotną awarię test może ujawnić? |
| koszt | Ile kosztuje wykonanie i utrzymanie? |

### 4.3. Typowy portfel

| Rodzaj testu | Najważniejsza wartość | Typowe ograniczenie |
| --- | --- | --- |
| test logiki domenowej | szybka diagnoza reguł i granic | nie sprawdza integracji technologicznej |
| test komponentowy | weryfikuje współpracę kilku klas przez publiczną granicę | może korzystać z uproszczonych zależności |
| wąski test integracyjny | sprawdza adapter, mapowanie, protokół lub konfigurację | wymaga zarządzania rzeczywistą technologią |
| test kontraktowy | wykrywa niezgodność producenta i konsumenta | nie dowodzi działania całego przepływu |
| test systemowy | sprawdza współdziałanie aplikacji jako całości | wolniejsza diagnoza i większy koszt danych |
| test end-to-end | chroni najważniejszą ścieżkę przez cały system | wysoki koszt, niestabilność i szeroki zakres porażki |

Test akceptacyjny opisuje perspektywę i cel, a niekoniecznie poziom techniczny. Scenariusz biznesowy może zostać sprawdzony na granicy modułu bez interfejsu użytkownika. Test end-to-end także nie musi używać interfejsu graficznego, jeżeli przepływ systemu jest dostępny przez API lub komunikaty.

### 4.4. Rozmieszczenie w procesie weryfikacji

Testy warto uruchamiać według czasu i wartości informacji:

1. kompilacja i bardzo szybkie testy zmienionego obszaru,
2. szybkie testy całego modułu,
3. wąskie testy integracyjne i kontraktowe,
4. szersze testy systemowe,
5. ograniczony zestaw krytycznych scenariuszy end-to-end,
6. testy wydajnościowe, bezpieczeństwa i odporności odpowiednio do ryzyka.

Szybki test adaptera może działać w pierwszym etapie razem z testami logiki. Etykieta nie powinna opóźniać wartościowej informacji.

### 4.5. Strategia dla kodu legacy

W systemie bez szybkich testów nie trzeba od razu budować idealnej piramidy dla całości. Należy:

1. rozpocząć od planowanej zmiany i jej ryzyka,
2. wykorzystać istniejącą stabilną granicę, nawet jeżeli początkowy test jest szerszy,
3. po utworzeniu punktów podmiany dodawać szybsze testy niższego poziomu,
4. zachować szersze testy tylko tam, gdzie dostarczają dodatkowej informacji,
5. sprawdzać rzeczywiste adaptery osobnymi testami integracyjnymi,
6. skracać ścieżkę krytycznej informacji zwrotnej.

Jeżeli szeroki test wykrył defekt, a niższy poziom go nie wykrywa, warto odtworzyć przypadek na najniższym poziomie, który nadal zachowuje mechanizm awarii. Nie oznacza to automatycznego usunięcia testu szerokiego. Najpierw trzeba ustalić, czy nadal chroni integrację systemu.

### 4.6. Krótka aktywność: zaprojektuj portfel

**Czas:** 5 minut.

System nalicza opłatę, zapisuje wynik w PostgreSQL i publikuje komunikat. Ostatnie regresje dotyczyły wartości granicznej, mapowania `BigDecimal` oraz niezgodnego schematu komunikatu. Zaproponuj minimalny portfel testów. Dla każdego testu nazwij wykrywane ryzyko, rzeczywiste zależności, oczekiwany czas oraz miejsce w procesie weryfikacji.

## 5. Obiekty zastępcze

### 5.1. Pojęcie nadrzędne

Obiekt zastępczy, po angielsku *test double*, zastępuje obiekt produkcyjny na potrzeby testu. Nazwa konkretnej roli zależy od sposobu użycia w danym teście, a nie wyłącznie od metody biblioteki, która utworzyła obiekt.

| Rola | Zadanie | Typowa weryfikacja |
| --- | --- | --- |
| stub | dostarcza zaprogramowane odpowiedzi | wynik obiektu testowanego |
| spy | rejestruje sposób użycia | odczyt zapisanych wywołań po operacji |
| fake | realizuje uproszczoną, działającą implementację | stan i kontrakt uproszczonego komponentu |
| mock | ma zaprogramowane oczekiwania dotyczące interakcji | jawna weryfikacja oczekiwanych wywołań |
| dummy | wypełnia wymagany parametr, lecz w danym scenariuszu nie jest używany | zwykle brak |

Mock nie jest synonimem każdego obiektu zastępczego. Obiekt utworzony przez bibliotekę mockującą może pełnić rolę stuba, jeżeli tylko zwraca dane, albo mocka, jeżeli test weryfikuje oczekiwany protokół.

### 5.2. Przykładowa usługa

```java
package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class OrderPlacementService {
    private final ProductCatalog catalog;
    private final PaymentGateway paymentGateway;
    private final OrderRepository repository;
    private final EventPublisher eventPublisher;

    public OrderPlacementService(
            ProductCatalog catalog,
            PaymentGateway paymentGateway,
            OrderRepository repository,
            EventPublisher eventPublisher) {
        this.catalog = Objects.requireNonNull(catalog);
        this.paymentGateway = Objects.requireNonNull(paymentGateway);
        this.repository = Objects.requireNonNull(repository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public PlacedOrder place(
            String sku,
            int quantity,
            String paymentToken) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        BigDecimal total = catalog.priceFor(sku)
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
        String authorizationId = paymentGateway.charge(paymentToken, total);
        long orderId = repository.save(
                new OrderDraft(sku, quantity, total, authorizationId));

        eventPublisher.publish(new OrderPlaced(orderId, total));
        return new PlacedOrder(orderId, total, authorizationId);
    }

    public interface ProductCatalog {
        BigDecimal priceFor(String sku);
    }

    public interface PaymentGateway {
        String charge(String paymentToken, BigDecimal amount);
    }

    public interface OrderRepository {
        long save(OrderDraft order);
    }

    public interface EventPublisher {
        void publish(OrderPlaced event);
    }

    public record OrderDraft(
            String sku,
            int quantity,
            BigDecimal total,
            String authorizationId) {
    }

    public record OrderPlaced(long orderId, BigDecimal total) {
    }

    public record PlacedOrder(
            long orderId,
            BigDecimal total,
            String authorizationId) {
    }
}
```

Przykład upraszcza transakcję płatności i zapisu. Jego celem jest pokazanie ról współpracowników, a nie zaprojektowanie odpornego procesu zamówienia.

### 5.3. Stub, fake i spy w jednym teście

W pierwszym teście katalog i bramka płatnicza dostarczają stałe odpowiedzi, więc pełnią role stubów. Repozytorium zachowuje stan i implementuje użyteczny podzbiór kontraktu, dlatego jest fake. Publikator zapisuje odebrane zdarzenia do późniejszej weryfikacji, więc jest spy.

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import pl.training.module2.OrderPlacementService.EventPublisher;
import pl.training.module2.OrderPlacementService.OrderDraft;
import pl.training.module2.OrderPlacementService.OrderPlaced;
import pl.training.module2.OrderPlacementService.OrderRepository;
import pl.training.module2.OrderPlacementService.PaymentGateway;
import pl.training.module2.OrderPlacementService.PlacedOrder;
import pl.training.module2.OrderPlacementService.ProductCatalog;

final class OrderPlacementServiceTest {
    @Test
    void placesOrderUsingStubFakeAndSpy() {
        ProductCatalog catalogStub = sku -> new BigDecimal("12.50");
        PaymentGateway paymentStub = (token, amount) -> "AUTH-7";
        InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
        RecordingEventPublisher publisherSpy = new RecordingEventPublisher();
        OrderPlacementService service = new OrderPlacementService(
                catalogStub,
                paymentStub,
                repositoryFake,
                publisherSpy);

        PlacedOrder result = service.place("BOOK", 2, "TOKEN-1");

        assertEquals(new BigDecimal("25.00"), result.total());
        assertEquals("AUTH-7", result.authorizationId());
        assertEquals(
                new OrderDraft("BOOK", 2, new BigDecimal("25.00"), "AUTH-7"),
                repositoryFake.find(result.orderId()));
        assertEquals(
                List.of(new OrderPlaced(result.orderId(), new BigDecimal("25.00"))),
                publisherSpy.publishedEvents());
    }

    @Test
    void verifiesPaymentProtocolUsingMock() {
        ProductCatalog catalogStub = sku -> new BigDecimal("40.00");
        ExpectingPaymentGateway paymentMock = new ExpectingPaymentGateway(
                "TOKEN-2",
                new BigDecimal("120.00"),
                "AUTH-9");
        InMemoryOrderRepository repositoryFake = new InMemoryOrderRepository();
        EventPublisher publisherStub = event -> {
        };
        OrderPlacementService service = new OrderPlacementService(
                catalogStub,
                paymentMock,
                repositoryFake,
                publisherStub);

        service.place("COURSE", 3, "TOKEN-2");

        paymentMock.verify();
    }

    private static final class InMemoryOrderRepository implements OrderRepository {
        private final Map<Long, OrderDraft> orders = new LinkedHashMap<>();
        private long nextId = 1;

        @Override
        public long save(OrderDraft order) {
            long id = nextId++;
            orders.put(id, order);
            return id;
        }

        OrderDraft find(long orderId) {
            return orders.get(orderId);
        }
    }

    private static final class RecordingEventPublisher implements EventPublisher {
        private final List<OrderPlaced> events = new ArrayList<>();

        @Override
        public void publish(OrderPlaced event) {
            events.add(event);
        }

        List<OrderPlaced> publishedEvents() {
            return List.copyOf(events);
        }
    }

    private static final class ExpectingPaymentGateway implements PaymentGateway {
        private final String expectedToken;
        private final BigDecimal expectedAmount;
        private final String authorizationId;
        private int calls;

        private ExpectingPaymentGateway(
                String expectedToken,
                BigDecimal expectedAmount,
                String authorizationId) {
            this.expectedToken = expectedToken;
            this.expectedAmount = expectedAmount;
            this.authorizationId = authorizationId;
        }

        @Override
        public String charge(String paymentToken, BigDecimal amount) {
            assertEquals(expectedToken, paymentToken);
            assertEquals(expectedAmount, amount);
            calls++;
            return authorizationId;
        }

        void verify() {
            assertEquals(1, calls);
        }
    }
}
```

### 5.4. Weryfikacja stanu i interakcji

Pierwszy test weryfikuje głównie stan i wynik. Może zmienić się wewnętrzna kolejność obliczeń, a test nadal pozostanie użyteczny. Drugi test opisuje protokół płatności: token, kwotę i dokładnie jedno wywołanie. Taka weryfikacja jest uzasadniona, jeżeli liczba obciążeń jest częścią kontraktu.

Mock może również zwracać zaprogramowaną wartość, więc role techniczne mogą się nakładać. Klasyfikacja ma wyjaśniać intencję testu, a nie wymuszać czyste kategorie obiektów.

### 5.5. Ryzyka obiektów zastępczych

- Implementacja typu fake może zachowywać się inaczej niż produkcyjny adapter. Warto uruchamiać wspólny zestaw testów kontraktu, jeżeli jego skutki można obserwować przez publiczny interfejs albo jawny mechanizm testowy obu implementacji.
- Rozbudowana konfiguracja mocków może powielać implementację i utrudniać refaktoryzację.
- Mockowanie prostych obiektów wartości i kolekcji zwykle zwiększa koszt bez zwiększenia kontroli.
- Zastąpienie bazy implementacją działającą w pamięci nie sprawdza zapytań, ograniczeń, transakcji ani mapowania typów.
- Spy może prowadzić do sprawdzania nieistotnej kolejności wywołań.
- Stub zwracający nierealne dane może ominąć ważne zachowanie graniczne.

Nazwa `spy` używana przez konkretną bibliotekę może oznaczać częściowy mock wywołujący rzeczywiste metody. Nie jest to automatycznie ta sama rola co klasyczny spy rejestrujący wywołania.

## 6. Pokrycie kodu

### 6.1. Co mierzy JaCoCo

JaCoCo analizuje skompilowany kod bajtowy i raportuje między innymi:

- pokrycie instrukcji,
- pokrycie gałęzi instrukcji `if` i `switch`,
- pokrycie linii, gdy pliki klas zawierają informacje o numerach linii,
- wykonanie metod i klas,
- złożoność cyklomatyczną oraz jej część pokrytą i niepokrytą, wyznaczaną na podstawie pokrycia gałęzi.

Obsługa wyjątków nie jest liczona jako gałąź. Konstruktor i inicjalizator statyczny są metodami z perspektywy kodu bajtowego. Kod syntetyczny generowany przez kompilator może prowadzić do wyników nieoczywistych na poziomie źródła.

### 6.2. Pełne linie i brakująca gałąź

```java
package pl.training.module2;

public final class DeliveryFee {
    private DeliveryFee() {
    }

    public static int fee(boolean premium) {
        int fee = 100;

        if (premium) {
            fee = 0;
        }

        return fee;
    }
}
```

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

final class DeliveryFeeLineCoverageTest {
    @Test
    void premiumCustomerHasFreeDelivery() {
        assertEquals(0, DeliveryFee.fee(true));
    }
}
```

Test wykonuje wszystkie linie metody `fee`, lecz tylko prawdziwą gałąź warunku. Nie sprawdza opłaty dla klienta bez statusu premium. Wysokie pokrycie linii nie ujawnia tej luki równie wyraźnie jak pokrycie gałęzi.

### 6.3. Poprawne pytania do raportu

Pokrycie dobrze odpowiada na pytania:

- Których instrukcji i gałęzi nie wykonał zmierzony zestaw testów?
- Czy testy weszły w obszar planowanej zmiany?
- Czy ważny warunek ma niewykonaną stronę?
- Czy nowy kod pogłębia istniejącą lukę?

Nie odpowiada natomiast na pytania:

- Czy asercja sprawdziła właściwy wynik?
- Czy oczekiwanie odpowiada wymaganiu?
- Czy dane wejściowe są reprezentatywne?
- Czy wykonano wszystkie istotne kombinacje i ścieżki?
- Czy system jest wolny od defektów?

### 6.4. Progi i nadużycia

Próg może pełnić funkcję zabezpieczenia przed niekontrolowanym pogarszaniem wyniku. Nie istnieje jednak uniwersalna poprawna wartość dla każdego systemu. Krytyczna, często zmieniana reguła wymaga innej ochrony niż kod generowany lub wycofywany adapter.

Typowe nadużycia to:

- pisanie testów bez istotnych asercji tylko dla wyniku raportu,
- mierzenie całego systemu jedną średnią,
- wykluczanie trudnego kodu bez uzasadnienia,
- testowanie banalnych akcesorów kosztem ryzykownych reguł,
- traktowanie 100 procent jako dowodu poprawności,
- rozliczanie zespołów z liczby zamiast z ryzyka i wyniku.

Test mutacyjny wprowadza kontrolowane zmiany do kodu i sprawdza, czy testy je wykrywają. Może ujawnić wykonany kod chroniony słabymi asercjami. Wynik mutacji także wymaga interpretacji i nie jest samodzielną miarą jakości.

### 6.5. Krótka aktywność: raport bez celu

**Czas:** 4 minuty.

Otwórz raport po `mvn clean verify` i znajdź `DeliveryFee`. Odpowiedz:

1. Która gałąź nie została wykonana?
2. Jaki test ją uruchomi?
3. Jaka asercja wykryje zmianę `100` na `200`?
4. Czy podniesienie globalnego pokrycia przez testy innych klas zmniejszy to ryzyko?

## 7. Testy charakteryzujące

### 7.1. Cel i zakres

Test charakteryzujący rejestruje aktualne, zaobserwowane zachowanie istniejącego kodu. Jego pierwszym zadaniem jest wykrywanie niezamierzonych zmian podczas pracy, a nie potwierdzenie zgodności z idealnym wymaganiem.

Rozróżnienie jest istotne:

- test wymagania odpowiada na pytanie, jak system powinien się zachowywać,
- test charakteryzujący odpowiada na pytanie, jak system zachowuje się obecnie dla wybranej obserwacji,
- ten sam test może później pełnić obie role, jeżeli oczekiwanie zostanie potwierdzone jako poprawny kontrakt.

Zapisany wynik może zawierać historyczny defekt. Test nie nadaje mu automatycznie statusu wymagania. Chroni zespół przed zmianą dokonaną bez świadomości i wymusza osobną decyzję, gdy wynik ma zostać skorygowany.

### 7.2. Praktyczny przebieg

1. Wybierz wąską granicę uruchomienia związaną z planowaną zmianą.
2. Uczyń wykonanie deterministycznym: kontroluj czas, losowość, kolejność i dane zewnętrzne.
3. Uruchom kod na małym, reprezentatywnym zestawie danych.
4. Zapisz istotny wynik, wyjątek albo efekt uboczny.
5. Sprawdź oczekiwanie przez przegląd kodu, dokumentację, dane produkcyjne lub konsultację domenową.
6. Dodaj przypadki graniczne i przypadki z historii defektów.
7. Wywołaj kontrolowaną mutację, aby upewnić się, że test potrafi wykryć zmianę.
8. Dopiero wtedy rozpocznij serię małych transformacji.

Pierwsze oczekiwanie może zostać pozyskane przez obserwację działania programu. Nie należy jednak bezrefleksyjnie kopiować każdego znaku z dużego wyniku. Trzeba ustalić, które elementy są stabilnym kontraktem, a które szumem, na przykład znacznikiem czasu, identyfikatorem technicznym lub kolejnością niezależnych rekordów.

### 7.3. Kod podlegający charakterystyce

Element faktury jest prostym obiektem wartości:

```java
package pl.training.module2;

import java.math.BigDecimal;

public record InvoiceLine(String sku, int quantity, BigDecimal unitPrice) {
}
```

Klasa legacy łączy normalizację danych klienta, obliczenia, zaokrąglenia i budowę tekstu:

```java
package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public final class LegacyInvoiceFormatter {
    public String format(String customer, List<InvoiceLine> lines) {
        BigDecimal subtotal = BigDecimal.ZERO;
        StringBuilder result = new StringBuilder("INVOICE\n");

        String displayedCustomer = customer == null
                ? "UNKNOWN"
                : customer.trim().toUpperCase(Locale.ROOT);
        result.append("Customer: ").append(displayedCustomer).append('\n');

        for (InvoiceLine line : lines) {
            BigDecimal lineTotal = line.unitPrice()
                    .multiply(BigDecimal.valueOf(line.quantity()));
            subtotal = subtotal.add(lineTotal);

            result.append(line.sku())
                    .append(" x ")
                    .append(line.quantity())
                    .append(" = ")
                    .append(lineTotal.setScale(2, RoundingMode.HALF_UP))
                    .append('\n');
        }

        BigDecimal tax = subtotal
                .multiply(new BigDecimal("0.23"))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal
                .add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        result.append("Subtotal: ")
                .append(subtotal.setScale(2, RoundingMode.HALF_UP))
                .append('\n');
        result.append("Tax: ").append(tax).append('\n');
        result.append("Total: ").append(total).append('\n');

        return result.toString();
    }
}
```

Zanim kod zostanie rozdzielony, test zapisuje kompletny tekst oraz ważne reguły widoczne na granicy: usuwanie spacji, zmianę wielkości liter, stawkę podatku, zaokrąglenie i końcowy znak nowego wiersza.

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

final class LegacyInvoiceFormatterCharacterizationTest {
    private final LegacyInvoiceFormatter formatter = new LegacyInvoiceFormatter();

    @Test
    void documentsCurrentFormattingAndRounding() {
        List<InvoiceLine> lines = List.of(
                new InvoiceLine("BOOK", 2, new BigDecimal("19.99")),
                new InvoiceLine("PEN", 1, new BigDecimal("5.00")));

        String result = formatter.format("  Acme  ", lines);

        assertEquals(
                String.join("\n",
                        "INVOICE",
                        "Customer: ACME",
                        "BOOK x 2 = 39.98",
                        "PEN x 1 = 5.00",
                        "Subtotal: 44.98",
                        "Tax: 10.35",
                        "Total: 55.33",
                        ""),
                result);
    }

    @Test
    void documentsCurrentFallbackForMissingCustomer() {
        String result = formatter.format(null, List.of());

        assertEquals(
                String.join("\n",
                        "INVOICE",
                        "Customer: UNKNOWN",
                        "Subtotal: 0.00",
                        "Tax: 0.00",
                        "Total: 0.00",
                        ""),
                result);
    }
}
```

Nazwy testów używają słowa `documents`, ponieważ na tym etapie zespół świadomie dokumentuje zastany wynik. Po potwierdzeniu reguł domenowych można nazwać testy językiem wymagań.

### 7.4. Dobór przypadków

Nie trzeba odtwarzać całej przestrzeni wejść. Priorytet mają:

- granice warunków i przedziały tuż po obu ich stronach,
- wartości puste, brakujące, zerowe i ujemne, jeżeli są dopuszczalne,
- reguły zaokrąglania, strefy czasowe i przejścia dat,
- rozgałęzienia prowadzące do efektów zewnętrznych,
- dane spotykane w produkcji i przypadki z historii awarii,
- formaty konsumowane poza zmienianym komponentem,
- błędy, wyjątki i częściowo wykonane operacje.

Test charakteryzujący nie powinien utrwalać przypadkowej implementacji. Jeżeli kolejność elementów nie należy do kontraktu, test może porównać zbiory. Jeżeli identyfikator jest losowy, test może sprawdzić jego format albo wprowadzić kontrolowane źródło identyfikatorów.

### 7.5. Golden master, snapshot i approval testing

Przy dużym, złożonym wyniku można zapisać rezultat i traktować go jako historyczną wyrocznię dla kolejnych wykonań. Jest to typowe użycie techniki golden master. Snapshot jest zapisanym wynikiem porównywanym w kolejnych wykonaniach, a approval testing podkreśla proces świadomego przeglądu i zatwierdzania wyniku lub różnicy. Terminologia nie jest całkowicie ustandaryzowana i zakresy tych pojęć mogą się nakładać. Żadne z nich nie jest jednak dokładnym synonimem testu charakteryzującego.

Duży zapis oczekiwanego wyniku jest wartościowy, gdy:

- wynik ma stabilny format i da się sensownie przejrzeć,
- dane wejściowe reprezentują istotny scenariusz,
- różnica jest czytelna,
- elementy niedeterministyczne zostały kontrolowane lub znormalizowane.

Aktualizacja pliku oczekiwanego bez przeczytania różnicy usuwa zabezpieczenie. Snapshot zawierający setki nieistotnych pól może również ukryć ważną zmianę w szumie.

## 8. Refaktoryzacja pod ochroną testów

### 8.1. Rozdzielenie odpowiedzialności

Po uzyskaniu zielonej bazy można wydzielać kolejne elementy. Nowa wersja formatera oddziela wyliczenie sumy, wyliczenie wartości wiersza, regułę zaokrąglenia, prezentację klienta i zapis wierszy.

```java
package pl.training.module2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public final class InvoiceFormatter {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.23");

    public String format(String customer, List<InvoiceLine> lines) {
        BigDecimal subtotal = calculateSubtotal(lines);
        BigDecimal tax = money(subtotal.multiply(TAX_RATE));
        BigDecimal total = money(subtotal.add(tax));

        StringBuilder result = new StringBuilder("INVOICE\n")
                .append("Customer: ")
                .append(displayedCustomer(customer))
                .append('\n');

        appendLines(result, lines);

        return result
                .append("Subtotal: ").append(money(subtotal)).append('\n')
                .append("Tax: ").append(tax).append('\n')
                .append("Total: ").append(total).append('\n')
                .toString();
    }

    private static String displayedCustomer(String customer) {
        return customer == null
                ? "UNKNOWN"
                : customer.trim().toUpperCase(Locale.ROOT);
    }

    private static BigDecimal calculateSubtotal(List<InvoiceLine> lines) {
        return lines.stream()
                .map(InvoiceFormatter::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static void appendLines(
            StringBuilder result,
            List<InvoiceLine> lines) {
        for (InvoiceLine line : lines) {
            result.append(line.sku())
                    .append(" x ")
                    .append(line.quantity())
                    .append(" = ")
                    .append(money(lineTotal(line)))
                    .append('\n');
        }
    }

    private static BigDecimal lineTotal(InvoiceLine line) {
        return line.unitPrice().multiply(BigDecimal.valueOf(line.quantity()));
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
```

Kod nadal nie jest pełnym modelem faktury. Stała stawka, brak waluty i brak walidacji danych są świadomymi uproszczeniami przykładu. Refaktoryzacja nie upoważnia do samodzielnego dodawania tych reguł.

### 8.2. Porównanie implementacji

Gdy stara implementacja działa deterministycznie, można przez ograniczony czas uruchamiać obie wersje dla tych samych danych. Taki test różnicowy uzupełnia testy ze stałym oczekiwaniem.

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class FormatterEquivalenceTest {
    private final LegacyInvoiceFormatter legacy = new LegacyInvoiceFormatter();
    private final InvoiceFormatter refactored = new InvoiceFormatter();

    @ParameterizedTest
    @MethodSource("representativeInvoices")
    void refactoringPreservesObservedOutput(
            String customer,
            List<InvoiceLine> lines) {
        assertEquals(
                legacy.format(customer, lines),
                refactored.format(customer, lines));
    }

    private static Stream<Arguments> representativeInvoices() {
        return Stream.of(
                Arguments.of("Acme", List.of()),
                Arguments.of(null, List.of(
                        new InvoiceLine("BOOK", 1, new BigDecimal("10.00")))),
                Arguments.of("vip", List.of(
                        new InvoiceLine("A", 3, new BigDecimal("0.10")),
                        new InvoiceLine("B", 2, new BigDecimal("19.995")))));
    }
}
```

Test różnicowy jest szczególnie użyteczny dla wielu danych generowanych lub zanonimizowanych danych produkcyjnych. Ma też ograniczenia:

- potwierdza zgodność tylko dla wykonanych danych,
- nie wykryje historycznego defektu obecnego w obu wersjach,
- stara implementacja nie jest niezależną specyfikacją,
- efekty uboczne mogą uniemożliwiać bezpieczne podwójne wykonanie,
- różnica wydajności może wymagać osobnego pomiaru.

Dlatego należy zachować przynajmniej kilka testów ze stałymi, przejrzanymi oczekiwaniami. Po usunięciu implementacji legacy test różnicowy przestaje być dostępny, a potwierdzone testy zachowania nadal stanowią zabezpieczenie.

### 8.3. Sekwencja transformacji

Przykładowa seria kroków dla formatera:

1. Uruchomić testy charakteryzujące i zapisać zieloną bazę.
2. Wyodrębnić `displayedCustomer` i uruchomić testy.
3. Wyodrębnić `lineTotal` i uruchomić testy.
4. Wyodrębnić wspólną regułę `money` i uruchomić testy.
5. Wyodrębnić `calculateSubtotal` i uruchomić testy.
6. Wyodrębnić `appendLines` i uruchomić testy.
7. Nazwać stawkę jako `TAX_RATE` i uruchomić testy.
8. Uruchomić pełny zestaw testów i obejrzeć różnicę.

Każdy krok ma jedną intencję. Gdy po wydzieleniu `money` zmieni się wynik, zakres poszukiwania przyczyny jest niewielki.

### 8.4. Jawna zmiana zachowania

Jeżeli właściciel produktu potwierdzi, że brak klienta powinien powodować wyjątek zamiast tekstu `UNKNOWN`, praca przechodzi w tryb funkcjonalny:

1. Zapisać nowe wymaganie w osobnym teście.
2. Zobaczyć oczekiwaną porażkę tego testu.
3. Zaimplementować nową regułę.
4. Świadomie usunąć lub zmienić sprzeczne oczekiwanie charakteryzujące.
5. Uruchomić testy regresji i opisać zmianę kontraktu.

Nie należy przedstawiać tej korekty jako refaktoryzacji, ponieważ obserwowalny wynik został celowo zmieniony.

## 9. Seams i rozrywanie zależności

### 9.1. Seam i punkt aktywacji

Seam, dalej nazywany szwem, jest miejscem, w którym można zmienić zachowanie programu bez edytowania kodu wykonywanego w tym miejscu. Każdy szew ma punkt aktywacji, czyli miejsce, w którym wybiera się alternatywne zachowanie.

Przykładami są:

- wywołanie interfejsu, którego implementację wybiera konstruktor lub konfiguracja,
- wywołanie metody możliwej do nadpisania, dla którego wybór klasy następuje podczas tworzenia obiektu,
- fabryka wybierająca adapter,
- wybór implementacji przy budowaniu albo uruchamianiu aplikacji.

Sam interfejs nie jest jeszcze użytecznym szwem, jeżeli kod wewnątrz metody na stałe tworzy konkretną implementację. Musi istnieć dostępny punkt, w którym test lub konfiguracja produkcyjna wybierze współpracownika.

W Javie najczęściej stosuje się szwy obiektowe (object seams) oparte na polimorfizmie i przekazywaniu zależności. Możliwe są też szwy wynikające ze sposobu budowania lub ładowania klas (link seams), lecz zwykle są mniej lokalne i trudniejsze do zrozumienia. Trzeci rodzaj opisywany przez Feathersa, szew preprocesora (preprocessing seam), nie ma w Javie praktycznego zastosowania, ponieważ język nie używa preprocesora. Bezpośrednie wywołanie statyczne, takie jak `LocalDate.now(...)`, nie daje szwu obiektowego. Trzeba przekazać źródło czasu albo najpierw owinąć wywołanie metodą, którą można zastąpić.

### 9.2. Dwa powody rozrywania zależności

Rozrywanie zależności zwykle służy jednej z dwóch potrzeb:

- separacja: uruchomienie badanego kodu bez trudnej, wolnej lub niedostępnej zależności,
- obserwacja: odczyt wyniku lub efektu, którego test nie potrafi obecnie zobaczyć.

Klient bazy danych wymagający niedostępnej infrastruktury jest przykładem problemu separacji. Bezpośredni zapis do `System.out` utrudnia obserwację, ponieważ efekt nie jest reprezentowany przez port domenowy. Globalny zegar stanowi jeszcze inny praktyczny problem: kod można uruchomić, ale test nie kontroluje pośredniego wejścia, więc wykonanie nie jest deterministyczne. Wprowadzenie szwu może rozwiązać więcej niż jeden z tych problemów, lecz warto nazwać je osobno.

### 9.3. Punkt wyjścia

```java
package pl.training.module2;

import java.time.LocalDate;

public record Subscription(String email, LocalDate renewalDate) {
}
```

```java
package pl.training.module2;

import java.time.LocalDate;
import java.time.ZoneOffset;

public final class LegacyReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        System.out.printf(
                "Sent renewal reminder to %s for %s%n",
                subscription.email(),
                subscription.renewalDate());
        return true;
    }
}
```

Zastany warunek ma wyłącznie górną granicę `today + 7`. Wiadomość jest więc wysyłana dla daty dokładnie za siedem dni, dla daty dzisiejszej oraz dla dat przeszłych. Data za osiem dni nie powoduje wysyłki. Brak dolnej granicy może być historycznym defektem, ale jego ewentualna korekta nie należy do refaktoryzacji. Bieżąca data jest pobierana globalnie, a wysłanie komunikatu jest połączone z decyzją biznesową. Próba testowania tej klasy przez zmianę zegara systemowego albo przechwytywanie globalnego strumienia zwiększa sprzężenie testu z procesem uruchomieniowym.

### 9.4. Minimalny szew przejściowy

Jeżeli zmiana konstruktora ma zbyt duży promień oddziaływania, pierwszym krokiem może być owinięcie trudnych wywołań metodami `protected`. W projekcie warsztatowym etap otrzymuje osobną nazwę klasy, aby wszystkie wersje mogły współistnieć. W rzeczywistym przebiegu byłaby to mała modyfikacja istniejącej klasy:

```java
package pl.training.module2;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class SeamedReminderService {
    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = currentDate();

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        sendMessage(subscription.email(), subscription.renewalDate());
        return true;
    }

    protected LocalDate currentDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }

    protected void sendMessage(String email, LocalDate renewalDate) {
        System.out.printf(
                "Sent renewal reminder to %s for %s%n",
                email,
                renewalDate);
    }
}
```

Kod produkcyjny wykonuje nadal te same operacje. Punktem aktywacji w teście jest wyrażenie tworzące `new TestableReminderService(...)`. Dynamiczne wiązanie wywołań metod `currentDate` i `sendMessage` jest mechanizmem realizującym podmianę zachowania. W katalogu technik Feathersa to podejście nosi nazwę Subclass and Override Method.

Zmiana klasy `final` na klasę rozszerzalną i dodanie metod `protected` zmienia kontrakt rozszerzalności oraz powierzchnię API. Krok można traktować jako refaktoryzację, gdy typ jest wewnętrzny, wszyscy konsumenci pozostają pod kontrolą, a rozszerzalność nie stanowi kontraktu zewnętrznego. Dla publicznej biblioteki wymaga to osobnej analizy kompatybilności i planu migracji, nawet jeśli wynik podstawowej operacji pozostaje taki sam.

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

final class SeamedReminderServiceTest {
    @Test
    void sendsReminderForRenewalExactlySevenDaysAway() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 6));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-09-06"),
                service.sentMessages());
    }

    @Test
    void doesNotSendReminderMoreThanSevenDaysBeforeRenewal() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 7));

        boolean sent = service.sendRenewalReminder(subscription);

        assertFalse(sent);
        assertEquals(List.of(), service.sentMessages());
    }

    @Test
    void documentsCurrentBehaviorForPastRenewalDate() {
        TestableReminderService service = new TestableReminderService(
                LocalDate.of(2026, 8, 30));
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 8, 29));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-08-29"),
                service.sentMessages());
    }

    private static final class TestableReminderService extends SeamedReminderService {
        private final LocalDate today;
        private final List<String> messages = new ArrayList<>();

        private TestableReminderService(LocalDate today) {
            this.today = today;
        }

        @Override
        protected LocalDate currentDate() {
            return today;
        }

        @Override
        protected void sendMessage(String email, LocalDate renewalDate) {
            messages.add(email + "|" + renewalDate);
        }

        List<String> sentMessages() {
            return List.copyOf(messages);
        }
    }
}
```

To rozwiązanie jest świadomą techniką przejściową. Ma zalety:

- wymaga niewielkiej zmiany sygnatur i miejsc tworzenia obiektu,
- szybko udostępnia czas i efekt do kontroli,
- pozwala scharakteryzować regułę graniczną przed większą przebudową.

Ma również koszty:

- rozszerza powierzchnię dziedziczenia tylko na potrzeby zastępowania zależności,
- łączy test z chronionymi metodami klasy,
- może zachęcać do dalszego rozwoju hierarchii, która nie ma znaczenia domenowego,
- nie nazywa jeszcze jawnie portu komunikacyjnego.

Nie każda zależność musi natychmiast otrzymać interfejs. Minimalny szew ma odblokować test i następną bezpieczną zmianę. Powinien być oznaczony jako rozwiązanie przejściowe, jeżeli nie jest pożądanym projektem końcowym.

### 9.5. Jawne zależności jako rozwiązanie docelowe

Standardowy `Clock` kontroluje czas. Interfejs `ReminderGateway` nazywa efekt wyjściowy, a wyrażenie tworzące `ReminderService` jest punktem aktywacji obu szwów. Przekazanie zależności przez konstruktor odpowiada technice Parameterize Constructor, a nazwanie efektu własnym interfejsem — technice Extract Interface z katalogu Feathersa.

```java
package pl.training.module2;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Objects;

public final class ReminderService {
    private final Clock clock;
    private final ReminderGateway reminderGateway;

    public ReminderService(Clock clock, ReminderGateway reminderGateway) {
        this.clock = Objects.requireNonNull(clock);
        this.reminderGateway = Objects.requireNonNull(reminderGateway);
    }

    public boolean sendRenewalReminder(Subscription subscription) {
        LocalDate today = LocalDate.now(clock);

        if (subscription.renewalDate().isAfter(today.plusDays(7))) {
            return false;
        }

        reminderGateway.send(subscription.email(), subscription.renewalDate());
        return true;
    }

    public interface ReminderGateway {
        void send(String email, LocalDate renewalDate);
    }
}
```

```java
package pl.training.module2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

final class ReminderServiceTest {
    @Test
    void usesInjectedClockAndGateway() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 6));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-09-06"),
                gateway.messages());
    }

    @Test
    void doesNotSendReminderMoreThanSevenDaysBeforeRenewal() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 9, 7));

        boolean sent = service.sendRenewalReminder(subscription);

        assertFalse(sent);
        assertEquals(List.of(), gateway.messages());
    }

    @Test
    void preservesCurrentBehaviorForPastRenewalDate() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-30T10:00:00Z"),
                ZoneOffset.UTC);
        RecordingReminderGateway gateway = new RecordingReminderGateway();
        ReminderService service = new ReminderService(clock, gateway);
        Subscription subscription = new Subscription(
                "developer@example.com",
                LocalDate.of(2026, 8, 29));

        boolean sent = service.sendRenewalReminder(subscription);

        assertTrue(sent);
        assertEquals(
                List.of("developer@example.com|2026-08-29"),
                gateway.messages());
    }

    private static final class RecordingReminderGateway
            implements ReminderService.ReminderGateway {
        private final List<String> messages = new ArrayList<>();

        @Override
        public void send(String email, LocalDate renewalDate) {
            messages.add(email + "|" + renewalDate);
        }

        List<String> messages() {
            return List.copyOf(messages);
        }
    }
}
```

W produkcji konstruktor otrzyma zegar systemowy z ustaloną strefą i adapter wysyłający komunikaty. Test używa `Clock.fixed` oraz obiektu spy zapisującego wiadomości. Reguła biznesowa nie zna technologii wysyłki.

### 9.6. Bezpieczna kolejność rozrywania zależności

1. Nazwij zachowanie i efekt, które trzeba zachować.
2. Wybierz punkt uruchomienia oraz punkt obserwacji.
3. Wprowadź najmniejszy szew bez zmiany logiki.
4. Zabezpiecz zachowanie testami, w tym wartości graniczne.
5. Przenieś wybór zależności do jawnego punktu aktywacji.
6. Zastąp zależność w teście i pozostaw rzeczywisty adapter w produkcji.
7. Dodaj osobny test integracyjny rzeczywistego adaptera, jeżeli jego kontrakt niesie ryzyko.
8. Usuń konstrukcję przejściową, gdy jawny projekt jest już bezpieczny.

### 9.7. Czego unikać

- Zmieniania widoczności prywatnej metody tylko po to, aby testować jej implementację bezpośrednio.
- Dodawania interfejsu do każdej klasy niezależnie od potrzebnej zmienności.
- Ukrywania grafu zależności w globalnym rejestrze usług.
- Zastępowania produkcyjnego adaptera własną implementacją typu fake, gdy rzeczywisty kontrakt adaptera nigdy nie jest testowany.
- Wprowadzania wielu warstw abstrakcji przed uzyskaniem pierwszego działającego testu.
- Pozostawiania rozwiązania przejściowego bez decyzji, czy ma stać się projektem docelowym.

## 10. Warsztat praktyczny

### 10.1. Sposób pracy

Ćwiczenia wykonujemy w krótkich iteracjach. Po każdej zakończonej transformacji kod ma się kompilować, a adekwatne testy mają być zielone. Jeżeli krok zmienia zachowanie, należy go nazwać zmianą funkcjonalną i oddzielić od transformacji strukturalnych.

Przed rozpoczęciem:

1. Sprawdź `mvn -version` i potwierdź, że Maven korzysta z Javy 25.
2. Przejdź do katalogu `refactoring-legacy`.
3. Uruchom `mvn clean verify`.
4. Potwierdź, że pracujesz wyłącznie w pakiecie `pl.training.module2`.
5. Ustal sposób zapisywania małych punktów przywracania.

### 10.2. Ćwiczenie 1: granica refaktoryzacji

**Czas pracy:** 15 minut. **Omówienie:** 5 minut.

#### Kontekst

Zespół planuje uporządkowanie usługi rozliczeniowej. Dla każdej propozycji określ:

1. Czy może być wykonana jako refaktoryzacja?
2. Jakie zachowanie jest obserwowalne?
3. Kto lub co jest konsumentem tego zachowania?
4. Jakiego dowodu potrzebujesz przed i po zmianie?
5. Czy propozycję trzeba podzielić na zmianę strukturalną i funkcjonalną?

#### Propozycje

A. Zmiana nazwy prywatnej metody `calc` na `calculateTax`.

B. Zastąpienie `double` przez `BigDecimal`, ponieważ produkcja wykazuje błędy zaokrągleń.

C. Przeniesienie publicznej klasy DTO do innego pakietu.

D. Wydzielenie budowy komunikatu do osobnej metody bez zmiany tekstu.

E. Dodanie pamięci podręcznej do odczytu kursu walut.

#### Kryterium ukończenia

Odpowiedź nie opiera się wyłącznie na nazwie operacji. Dla każdej pozycji wskazuje konkretną obserwację oraz mechanizm weryfikacji.

### 10.3. Ćwiczenie 2: charakterystyka i refaktoryzacja formatera

**Czas pracy:** 35 minut. **Omówienie:** 10 minut.

#### Punkt wyjścia

Użyj `LegacyInvoiceFormatter`, `InvoiceLine` i testu `LegacyInvoiceFormatterCharacterizationTest`.

#### Zadania

1. Przeczytaj kod bez zmieniania go i wypisz wszystkie widoczne reguły.
2. Uruchom istniejące testy charakteryzujące.
3. Zbuduj macierz przypadków obejmującą co najmniej poniższe obserwacje. Zaznacz, które są już zabezpieczone, i dodaj testy tylko dla brakujących:
   - pustą listę pozycji,
   - klienta `null`,
   - cenę wymagającą zaokrąglenia,
   - wiele pozycji, dla których suma wartości niezaokrąglonych różni się od sumy wartości prezentowanych.
4. Ustal, które reguły są potwierdzonym kontraktem, a które tylko zastanym zachowaniem.
5. Refaktoryzuj w małych krokach, wydzielając normalizację klienta, wartość wiersza, sumę, zaokrąglenie oraz formatowanie pozycji.
6. Po każdym kroku uruchom adekwatny test.
7. Porównaj wynik `LegacyInvoiceFormatter` i `InvoiceFormatter` dla reprezentatywnych danych.
8. Obejrzyj końcową różnicę i wskaż każdą zmianę, której nie wymagał cel ćwiczenia.

#### Ograniczenia

- Nie zmieniaj stawki podatku ani sposobu zaokrąglania.
- Nie dodawaj walidacji, wyjątków ani obsługi walut.
- Nie zmieniaj tekstu, białych znaków ani końcowego znaku nowego wiersza.
- Nie łącz wszystkich wydzieleń w jedną dużą edycję.

#### Kryteria ukończenia

- Wszystkie testy są zielone.
- Każda transformacja ma jedną rozpoznawalną intencję.
- Wynik starej i nowej wersji jest zgodny dla przygotowanych danych.
- Uczestnik potrafi wskazać ograniczenia uzyskanego dowodu.

### 10.4. Ćwiczenie 3: strategia testów, obiekty zastępcze i pokrycie

**Czas pracy:** 25 minut. **Omówienie:** 10 minut.

#### Część A: role obiektów zastępczych

Przeanalizuj `OrderPlacementServiceTest` i dla każdego współpracownika odpowiedz:

1. Jaką rolę pełni w danym teście?
2. Czy test sprawdza stan, wynik czy interakcję?
3. Jaki defekt może wykryć?
4. Jakiego defektu nie może wykryć?
5. Która implementacja typu fake jest kandydatem do wspólnego testu kontraktu z produkcyjnym adapterem i jak można obserwować skutki obu implementacji?

Następnie oceń, czy sprawdzanie dokładnej kolejności zapisu zamówienia i publikacji zdarzenia byłoby uzasadnione. Odpowiedź musi odwoływać się do konkretnego kontraktu transakcyjnego, a nie do aktualnej kolejności linii kodu.

#### Część B: raport pokrycia

1. Uruchom `mvn clean verify`.
2. Otwórz raport pakietu `pl.training.module2`.
3. Znajdź `DeliveryFee` i wyjaśnij różnicę między pokryciem linii i gałęzi.
4. Zaproponuj test, który wykona brakującą gałąź i wykryje zmianę opłaty `100` na `200`.
5. Podaj przykład testu, który zwiększyłby pokrycie, ale nie chronił reguły opłaty.
6. Zaproponuj sposób użycia progu pokrycia, który nie zamieni go w cel sam w sobie.

#### Kryteria ukończenia

- Role są nazwane według użycia w teście.
- Wskazano granice każdego obiektu zastępczego.
- Brakująca gałąź ma asercję dotyczącą wartości biznesowej.
- Wniosek z raportu nie sprowadza jakości do jednego procentu.

### 10.5. Ćwiczenie 4: wprowadzenie szwu

**Czas pracy:** 25 minut. **Omówienie:** 10 minut.

#### Punkt wyjścia

Użyj `LegacyReminderService` i `Subscription`.

#### Zadania

1. Wskaż niedeterministyczne wejście oraz efekt trudny do obserwacji.
2. Nazwij górną granicę reguły oraz konsekwencję braku granicy dolnej.
3. Wprowadź minimalny szew przez owinięcie czasu i wysyłki metodami możliwymi do nadpisania.
4. Napisz deterministyczne testy dla odnowienia dokładnie za siedem dni, za osiem dni oraz dla daty przeszłej.
5. Zastąp szew przejściowy jawnymi zależnościami `Clock` i `ReminderGateway`.
6. Przenieś testy na docelowy projekt.
7. Wskaż punkt aktywacji każdego szwu.
8. Wyjaśnij, jaki test byłby potrzebny dla rzeczywistego adaptera komunikacyjnego.

#### Kryteria ukończenia

- Test nie zależy od bieżącej daty ani globalnego wyjścia.
- Górna granica siedmiu dni jest sprawdzona po obu stronach, a zachowanie dla daty przeszłej jest jawnie scharakteryzowane.
- Kod domenowy nie tworzy konkretnego adaptera.
- Rozwiązanie przejściowe jest odróżnione od projektu docelowego.
- Uczestnik potrafi wskazać potrzebę testu integracyjnego adaptera.

## 11. Odpowiedzi wzorcowe do ćwiczeń

### 11.1. Ćwiczenie 1

| Pozycja | Ocena | Obserwowalne zachowanie i weryfikacja |
| --- | --- | --- |
| A | zwykle refaktoryzacja | Należy sprawdzić odwołania tekstowe, refleksję i narzędzia. Przy braku takich konsumentów wystarczą automatyczna zmiana nazwy, kompilacja i testy zachowania. |
| B | cel obejmuje zmianę zachowania | Jeżeli wynik ma przestać zawierać błąd zaokrąglenia, nie jest to wyłącznie refaktoryzacja. Najpierw można strukturalnie wydzielić obliczenie, następnie osobno zmienić typ i oczekiwany wynik. |
| C | zależy od kontraktu | Dla publicznej biblioteki nazwa pakietu jest częścią nazwy typu i może naruszyć kompatybilność źródłową lub binarną. Potrzebna jest strategia migracji konsumentów. |
| D | refaktoryzacja | Kontraktem jest dokładna treść komunikatu, w tym format i białe znaki. Chronią go test serializacji albo test charakteryzujący tekst. |
| E | zwykle optymalizacja | Wynik może pozostać ten sam, lecz zmieniają się świeżość danych, opóźnienie, zużycie pamięci i zachowanie przy awarii. Wymaga testów poprawności, wygaszania danych i pomiaru wydajności. |

Najważniejszy wniosek: etykieta operacji nie rozstrzyga jej charakteru. Decydują cel oraz obserwacje, które mają pozostać niezmienione.

### 11.2. Ćwiczenie 2

Widoczne reguły formatera to:

- nagłówek `INVOICE`,
- klient zapisany wielkimi literami po usunięciu skrajnych spacji,
- tekst `UNKNOWN` dla klienta `null`,
- zachowanie kolejności pozycji wejściowych,
- mnożenie ceny jednostkowej przez liczbę sztuk,
- prezentacja wartości z dwoma miejscami i `HALF_UP`,
- suma częściowa liczona z wartości przed zaokrągleniem prezentacji,
- podatek 23 procent zaokrąglany do dwóch miejsc,
- suma całkowita z części niezaokrąglonej i podatku zaokrąglonego,
- końcowy znak nowego wiersza.

Nie wszystkie reguły muszą być poprawnym wymaganiem. Na przykład kolejność momentów zaokrągleń może być zastanym zachowaniem o istotnym wpływie finansowym. Wymaga potwierdzenia domenowego przed zmianą.

Test startowy z klientem `null` obejmuje równocześnie pustą listę pozycji. Pierwszy test obejmuje wiele pozycji i zaokrąglenie podatku, ale nie pokazuje różnicy między sumą wartości dokładnych a sumą wartości prezentowanych. Uzupełniający przypadek może zawierać dwie pozycje o cenie `0.005` i liczbie sztuk `1`. Każdy wiersz jest prezentowany jako `0.01`, natomiast suma częściowa liczona z wartości dokładnych wynosi `0.01`, a nie `0.02`. Taki przypadek precyzyjnie ujawnia kolejność zaokrągleń.

Odpowiednia sekwencja transformacji została pokazana w punkcie 8.3. `LegacyInvoiceFormatterCharacterizationTest` zachowuje stałe oczekiwania, a `FormatterEquivalenceTest` porównuje obie implementacje dla dodatkowych danych. Te dwa mechanizmy się uzupełniają:

- stałe oczekiwanie jest niezależne od dalszego działania starej klasy,
- porównanie implementacji łatwo rozszerzyć o wiele zestawów danych,
- żaden mechanizm nie dowodzi równoważności dla wszystkich możliwych wejść,
- oba mogą utrwalić historyczny defekt, jeżeli oczekiwania nie zostaną przejrzane.

Zmiana z `19.995` na prezentowane `20.00` w jednej pozycji może nie oznaczać, że do sumy częściowej należy dodać `20.00`. Zastany kod dodaje dokładną wartość pozycji przed jej prezentacją. Zmiana tej kolejności jest zmianą zachowania i wymaga osobnej decyzji.

### 11.3. Ćwiczenie 3

| Współpracownik | Rola i rodzaj weryfikacji | Może wykryć | Nie może wykryć |
| --- | --- | --- | --- |
| `catalogStub` | stub; wynik usługi i zapisany stan pośrednio potwierdzają użycie ceny | pominięcie ceny albo błędne mnożenie jej przez liczbę sztuk | błędne wyszukiwanie SKU w rzeczywistym katalogu |
| `paymentStub` | stub; wynik i stan repozytorium potwierdzają propagację identyfikatora autoryzacji | zgubienie lub zmianę identyfikatora zwróconego przez bramkę | błędny token, kwotę wywołania i integrację z operatorem płatności |
| `repositoryFake` | fake; test odczytuje stan przez pomocniczą metodę `find` | zapis niepoprawnego `OrderDraft` albo brak zapisu | błędny SQL, mapowanie, ograniczenia i semantykę transakcji |
| `publisherSpy` | spy; test weryfikuje zarejestrowaną interakcję i jej dane | brak zdarzenia, złą liczbę zdarzeń albo błędną treść | serializację, dostarczenie i konfigurację rzeczywistego brokera |
| `paymentMock` | mock; test jawnie weryfikuje token, kwotę i liczbę wywołań | niepoprawny protokół obciążenia, w tym wielokrotne wywołanie | zachowanie produkcyjnej bramki i poprawność całego procesu rozliczenia |
| `publisherStub` | stub bez odpowiedzi; test nie weryfikuje publikacji | umożliwia wykonanie ścieżki, ale sam nie wykrywa braku publikacji | wszystkie defekty protokołu i integracji publikatora |

Nazwa `stub` jest właściwsza dla `publisherStub` niż `dummy`, ponieważ metoda `publish` rzeczywiście zostaje wywołana.

Implementacja zastępcza repozytorium działająca w pamięci nie wykryje błędnego SQL, ograniczeń bazy, mapowania `BigDecimal`, izolacji transakcji ani zachowania generatora identyfikatorów. Produkcyjny adapter wymaga wąskiego testu integracyjnego.

W przykładzie `find` jest pomocniczą metodą wyłącznie klasy `InMemoryOrderRepository`, a nie częścią `OrderRepository`. Nie da się więc bezpośrednio uruchomić identycznego testu odczytu przez sam publiczny interfejs. Wspólny kontrakt może opisywać wyłącznie obserwowalne elementy wspólne, na przykład semantykę zwracanego identyfikatora. Sprawdzenie trwałego zapisu wymaga jawnego mechanizmu obserwacji właściwego dla środowiska testowego, na przykład zapytania kontrolnego do rzeczywistej bazy. Nie należy dodawać metody odczytu do interfejsu produkcyjnego wyłącznie po to, aby ujednolicić testy.

Dokładna kolejność zapisu i publikacji jest warta sprawdzania tylko wtedy, gdy wynika z mechanizmu niezawodności, na przykład zdarzenie może zostać opublikowane dopiero po trwałym zapisie albo oba działania są koordynowane przez outbox. Sama obecna kolejność wywołań nie uzasadnia kruchego testu interakcji.

Dla `DeliveryFee` brakującą gałąź wykonuje wywołanie `DeliveryFee.fee(false)`. Asercja powinna oczekiwać dokładnie `100`. Test bez asercji albo asercja sprawdzająca tylko brak wyjątku podniesie pokrycie, lecz nie wykryje zmiany `100` na `200`.

Rozsądny próg może blokować znaczący spadek pokrycia zmienianego pakietu i kierować uwagę na nowe, nieprzetestowane gałęzie. Nadal potrzebny jest przegląd ryzyka, asercji i wyłączeń. Globalny procent nie powinien pozwalać, aby dobrze pokryty kod banalny ukrywał brak ochrony reguły krytycznej.

### 11.4. Ćwiczenie 4

W `LegacyReminderService` globalne źródło czasu uniemożliwia kontrolowanie pośredniego wejścia, a bezpośrednie użycie `System.out` utrudnia obserwację efektu. Istotne przypadki to:

- data odnowienia dokładnie siedem dni po dzisiejszej dacie, wiadomość ma zostać wysłana,
- data odnowienia osiem dni po dzisiejszej dacie, wiadomość nie ma zostać wysłana,
- data odnowienia w przeszłości, wiadomość również zostaje wysłana, ponieważ zastana reguła nie ma dolnej granicy.

Jeżeli wymaganie domenowe definiuje przedział od dnia dzisiejszego do siedmiu dni włącznie, dodanie warunku odrzucającego daty przeszłe będzie osobną zmianą funkcjonalną. Nie należy wprowadzać go po cichu podczas rozrywania zależności.

`SeamedReminderService` tworzy minimalny szew przez `currentDate` i `sendMessage`. Punktem aktywacji jest utworzenie `TestableReminderService`, a dynamiczne wiązanie wybiera nadpisane metody. `SeamedReminderServiceTest` potwierdza regułę przed dalszą zmianą projektu.

`ReminderService` zastępuje dziedziczenie przekazanymi zależnościami. Punktem aktywacji jest wyrażenie `new ReminderService(...)`, a jego argumenty wybierają zachowanie. Test przekazuje `Clock.fixed` oraz `RecordingReminderGateway`. Produkcja przekaże zegar systemowy z jawną strefą i rzeczywisty adapter komunikacyjny.

Test jednostkowy usługi nie potwierdzi, że adapter poprawnie serializuje komunikat, uwierzytelnia się, obsługuje odpowiedzi ani ponowienia. Te ryzyka należy sprawdzić wąskim testem integracyjnym z rzeczywistą technologią lub środowiskiem o zgodnym kontrakcie.

Rozwiązanie przejściowe można usunąć po przeniesieniu wywołań produkcyjnych i testów na `ReminderService`. Usunięcie powinno być osobnym, małym krokiem, po którym ponownie uruchamiany jest pełny zestaw testów.

## 12. Sprawdzenie wiedzy

### Pytania

1. Co odróżnia refaktoryzację od zmiany funkcjonalnej?
2. Czy zachowanie obserwowalne ogranicza się do wartości zwracanej?
3. Dlaczego małe kroki ograniczają koszt diagnozy?
4. Co dokładnie oznacza zielony zestaw testów?
5. Dlaczego piramida testów nie narzuca uniwersalnych proporcji?
6. Czym stub różni się od mocka?
7. Jakie ryzyko pozostaje po zastąpieniu bazy implementacją działającą w pamięci?
8. Co pokazuje pokrycie gałęzi, czego może nie pokazać pokrycie linii?
9. Dlaczego test charakteryzujący nie dowodzi poprawności wymagania?
10. Czym różni się seam od punktu aktywacji?
11. Jakie dwie potrzeby realizuje rozrywanie zależności?
12. Kiedy szew przez dziedziczenie warto traktować jako przejściowy?

### Odpowiedzi

1. Refaktoryzacja zmienia strukturę przy zachowaniu uzgodnionych obserwacji. Zmiana funkcjonalna celowo modyfikuje co najmniej jedną z nich.
2. Nie. Obejmuje także wyjątki, efekty, protokoły, formaty, API i istotne właściwości pozafunkcjonalne.
3. Po porażce niewielka ostatnia transformacja staje się głównym podejrzanym i łatwo ją przeanalizować lub wycofać.
4. Żaden wykonany test nie wykrył różnicy w sprawdzanych obserwacjach. Nie jest to dowód braku wszystkich regresji.
5. Koszt i wartość testu zależą od architektury, technologii i ryzyka. Ważniejsze są właściwości testów niż arbitralna liczba.
6. Stub dostarcza odpowiedzi potrzebne scenariuszowi. Mock zawiera oczekiwania dotyczące interakcji i wymaga ich weryfikacji.
7. Test nie sprawdza między innymi zapytań, mapowania, ograniczeń, transakcji i zachowania rzeczywistego silnika.
8. Pokazuje, czy wykonano różne wyniki warunków. Wszystkie linie mogą zostać wykonane mimo pominięcia jednej strony decyzji.
9. Zapisuje obserwowany wynik, który może być historycznym defektem lub przypadkowym szczegółem.
10. Seam jest miejscem możliwej zmiany zachowania, a punkt aktywacji jest miejscem wyboru konkretnego wariantu.
11. Separację badanego kodu od trudnej zależności oraz obserwację niedostępnego efektu.
12. Gdy dziedziczenie istnieje tylko dla testowalności, rozszerza niepożądaną powierzchnię klasy i nie opisuje sensownej relacji domenowej.

## 13. Listy kontrolne

### 13.1. Przed refaktoryzacją

- [ ] Cel zmiany i jej granica są zapisane.
- [ ] Wskazano istotne zachowania obserwowalne i ich konsumentów.
- [ ] Projekt buduje się w powtarzalnym środowisku Java 25.
- [ ] Istniejące awarie testów są wyjaśnione.
- [ ] Wybrano szybkie testy prowadzące lokalne kroki.
- [ ] Ryzykowne zachowanie bez ochrony ma test charakteryzujący.
- [ ] Czas, losowość i dane zewnętrzne są kontrolowane tam, gdzie to potrzebne.
- [ ] Istnieje bezpieczny punkt przywracania.

### 13.2. Podczas refaktoryzacji

- [ ] Aktualny krok ma jedną intencję.
- [ ] Wiadomo, czy praca jest strukturalna, czy funkcjonalna.
- [ ] Kod jest regularnie kompilowany.
- [ ] Najszybsze adekwatne testy są uruchamiane po każdym kroku.
- [ ] Szerszy zestaw jest uruchamiany w rozsądnych odstępach.
- [ ] Różnica nie zawiera przypadkowego formatowania i zmian pobocznych.
- [ ] Nie aktualizuje się oczekiwań bez zrozumienia przyczyny różnicy.
- [ ] Nowa abstrakcja rozwiązuje konkretną przeszkodę.

### 13.3. Przed zakończeniem

- [ ] Pełny zestaw testów przechodzi.
- [ ] Sprawdzono ważne adaptery i kontrakty integracyjne.
- [ ] Raport pokrycia przejrzano pod kątem ryzykownych luk.
- [ ] Wykonano test kontrolnej zmiany dla kluczowej asercji.
- [ ] Przejrzano cały diff pod kątem zmiany zachowania.
- [ ] Zmiany funkcjonalne są oddzielone i opisane.
- [ ] Rozwiązania przejściowe zostały usunięte albo jawnie oznaczone.
- [ ] Instrukcja uruchomienia pozostaje aktualna.

## 14. Podsumowanie

Bezpieczna refaktoryzacja nie wynika z pojedynczego narzędzia ani wskaźnika. Opiera się na świadomie określonym kontrakcie, małych transformacjach i kilku uzupełniających się źródłach informacji zwrotnej.

Najważniejsze zasady modułu:

1. Refaktoryzacja zmienia strukturę, zachowując nazwane obserwacje.
2. Testy zwiększają poziom dowodu, lecz nie stanowią dowodu absolutnego.
3. Piramida testów jest heurystyką kosztu i informacji, nie receptą liczbową.
4. Stub, spy, fake i mock opisują role w konkretnym teście.
5. Pokrycie wskazuje niewykonany kod, ale nie ocenia poprawności wymagań ani siły asercji.
6. Test charakteryzujący zapisuje zastane zachowanie, które nadal wymaga interpretacji.
7. Szew udostępnia punkt zmiany zachowania, a punkt aktywacji pozwala wybrać jego wariant.
8. Rozrywanie zależności ma być minimalnym krokiem prowadzącym do kontrolowanego wykonania i obserwacji.
9. Rozwiązanie przejściowe nie musi być projektem docelowym.
10. Poprawkę defektu i nową funkcję należy oddzielić od zmiany strukturalnej.
