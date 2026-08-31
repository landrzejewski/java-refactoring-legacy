# Moduł 5. Refaktoryzacje hierarchii klas - warsztat praktyczny

## Cel modułu

Celem modułu jest opanowanie bezpiecznych zmian struktury dziedziczenia w istniejącym kodzie. Uczestnik uczy się odróżniać współdzielenie implementacji od rzeczywistego podtypowania, przenosić stan i zachowanie między poziomami hierarchii oraz zastępować niewłaściwe dziedziczenie węższym kontraktem albo kompozycją.

Refaktoryzacje hierarchii mają większy promień oddziaływania niż lokalne zmiany wewnątrz metody. Mogą wpływać na dynamiczną dyspozycję, kolejność inicjalizacji, dostępność członków, zgodność plików `.class`, refleksję, proxy, mapowanie ORM i serializację. Dlatego każda technika jest omawiana wraz z warunkami wstępnymi, procedurą, testami i granicami bezpieczeństwa.

## Efekty uczenia się

Po ukończeniu modułu uczestnik:

- rozpoznaje wspólny kontrakt uzasadniający Pull Up Method, Pull Up Field i Extract Superclass,
- odróżnia podobieństwo tekstu od wspólnego znaczenia domenowego,
- stosuje Push Down Method i Push Down Field do zawężania zbyt szerokiego typu bazowego,
- wydziela podklasę dla stabilnego podzbioru instancji ze specyficznym stanem lub zachowaniem,
- wydziela interfejs opisujący rolę klienta, a nie sumę publicznych metod klasy,
- usuwa poziom hierarchii, który nie reprezentuje już odrębnego pojęcia,
- zastępuje dziedziczenie kompozycją, gdy relacja `is-a` jest fałszywa albo zbyt szeroka,
- wyjaśnia różnicę między nadpisywaniem metod a ukrywaniem pól,
- analizuje konstruktory, inicjalizatory, `super`, metody `static`, `private`, `final` i `synchronized`,
- uwzględnia erasure, metody bridge, dostęp `protected` i hierarchie `sealed`,
- rozdziela zgodność zachowania, źródłową, binarną, refleksyjną i serializacyjną,
- zabezpiecza transformacje testami charakterystyki, kontraktowymi, różnicowymi i integracyjnymi,
- planuje migrację publicznego API zamiast nazywać zmianę łamiącą zwykłą refaktoryzacją.

## Zakres

1. Kontrakt hierarchii i model ryzyka
2. Semantyka dziedziczenia w Javie 25
3. Pull Up Method i Pull Up Field
4. Push Down Method i Push Down Field
5. Extract Superclass
6. Extract Subclass
7. Extract Interface
8. Collapse Hierarchy
9. Replace Inheritance with Composition
10. Zgodność, serializacja i integracje frameworkowe
11. Studium przypadku: hierarchia powiadomień
12. Dodatkowe przypadki przed i po
13. Warsztat praktyczny

## Nazwy technik

Materiał zachowuje nazwy z agendy. W literaturze i narzędziach można spotkać także następujące warianty:

| Nazwa w module | Spotykany odpowiednik lub doprecyzowanie |
| --- | --- |
| Pull Up Method | przeniesienie wspólnej metody do nadklasy |
| Pull Up Field | przeniesienie wspólnego stanu do nadklasy |
| Push Down Method | przeniesienie operacji do właściwej gałęzi hierarchii |
| Push Down Field | przeniesienie stanu do właściwej gałęzi hierarchii |
| Extract Superclass | wyodrębnienie wspólnej nadklasy |
| Extract Subclass | klasyczne wydzielenie podklasy; bywa łączone z Replace Type Code with Subclasses |
| Extract Interface | wydzielenie interfejsu roli klienta |
| Collapse Hierarchy | scalenie poziomów bez istotnego rozróżnienia |
| Replace Inheritance with Composition | Replace Superclass with Delegate, Replace Inheritance with Delegation |

Kompozycja i delegowanie opisują powiązane, ale różne aspekty rozwiązania. Kompozycja oznacza, że obiekt posiada współpracownika. Delegowanie oznacza, że przekazuje mu wykonanie operacji. Wrapper może używać kompozycji bez delegowania całego API, a nie każde delegowanie oznacza silną własność cyklu życia.

## Konwencje przykładów

Przykłady używają Javy 25, JUnit Jupiter 6.1.3 i projektu Maven `refactoring-legacy`. Kod modułu znajduje się w pakiecie `pl.training.module5` oraz jego podpakietach.

| Pakiet | Znaczenie |
| --- | --- |
| `pl.training.module5.stage0` | niezależne klasy powiadomień przed zmianą |
| `pl.training.module5.stage1` | Extract Superclass oraz Pull Up Method i Field |
| `pl.training.module5.stage2` | Push Down Method i Field dla potwierdzenia SMS |
| `pl.training.module5.stage3` | Extract Interface i klient zależny od roli |
| `pl.training.module5.extractsubclass` | osobny przykład Extract Subclass |
| `pl.training.module5.collapse` | osobny przykład Collapse Hierarchy |
| `pl.training.module5.composition` | osobny przykład zastąpienia dziedziczenia kompozycją |

Pakiety `before`, `after` i `stage0` do `stage3` celowo zawierają kolejne wersje kodu. Dzięki temu wszystkie stany kompilują się w ramach jednego przebiegu budowania i mogą być porównywane przez testy różnicowe. W repozytorium produkcyjnym kolejne stany zwykle zastępują te same pliki i pozostają dostępne w historii wersji.

Kompilacja, testy i uruchomienie:

```shell
cd refactoring-legacy
mvn clean verify
java -cp target/classes pl.training.module5.Module5Examples
```

## Organizacja pracy

Sugerowany czas pracy synchronicznej wynosi 240 minut:

| Część | Czas |
| --- | ---: |
| kontrakt hierarchii i semantyka Javy | 45 minut |
| demonstracja Pull Up, Push Down i Extract Superclass | 40 minut |
| ćwiczenie 1 i omówienie | 35 minut |
| Extract Subclass, Extract Interface i Collapse Hierarchy | 35 minut |
| ćwiczenie 2 i omówienie | 30 minut |
| kompozycja, zgodność i ćwiczenie 3 | 40 minut |
| ćwiczenie 4, sprawdzenie wiedzy i podsumowanie | 15 minut |

## 1. Kontrakt hierarchii i model ryzyka

### 1.1. Hierarchia jest częścią zachowania

Dziedziczenie nie służy wyłącznie redukcji duplikacji. Deklaruje, że obiekt podklasy może występować wszędzie tam, gdzie klient oczekuje nadklasy. Taki klient może polegać nie tylko na dostępności metod, lecz także na ich warunkach wstępnych, rezultatach, wyjątkach, efektach ubocznych, niezmiennikach i własnościach operacyjnych.

Podtyp spełniający kontrakt:

- przyjmuje wszystkie poprawne dane dopuszczone przez typ bazowy,
- nie wzmacnia warunków wstępnych,
- zapewnia co najmniej gwarancje wyniku typu bazowego,
- nie osłabia niezmienników ani warunków końcowych,
- nie dodaje nieoczekiwanych wyjątków lub efektów,
- zachowuje kontrakt tożsamości, kolejności i współbieżności, jeśli klient może go obserwować.

Zgodna sygnatura nie dowodzi zastępowalności. Klasa `ReadOnlyAccount` dziedzicząca publiczne `withdraw` i rzucająca wyjątek dla każdej kwoty może kompilować się poprawnie, ale prawdopodobnie nie jest poprawnym podtypem kontraktu pozwalającego wypłacać środki.

### 1.2. Współdzielenie implementacji a podtypowanie

Przed utworzeniem nadklasy należy odpowiedzieć na dwa osobne pytania:

1. Czy klasy zawierają ten sam kod lub stan?
2. Czy reprezentują warianty tego samego pojęcia i spełniają wspólny kontrakt?

Pierwsza odpowiedź może uzasadniać wydzielenie współpracownika, funkcji albo obiektu wartości. Dopiero druga uzasadnia relację dziedziczenia. Jeżeli klasy łączy wyłącznie algorytm techniczny, kompozycja jest zwykle mniej zobowiązującym kierunkiem.

Dobra nadklasa nie jest magazynem wszystkiego, co powtarza się w potomkach. Reprezentuje stabilną abstrakcję, której operacje mają to samo znaczenie dla każdej poprawnej podklasy.

### 1.3. Warstwy zgodności

Wymagany zakres zgodności zależy od sposobu wdrażania systemu:

| Warstwa | Pytanie kontrolne |
| --- | --- |
| zachowanie wykonania | Czy wyniki, wyjątki, stan i efekty uboczne pozostają zgodne? |
| zgodność źródłowa | Czy dotychczasowy kod klienta nadal się kompiluje? |
| zgodność binarna | Czy wcześniej skompilowany klient nadal linkuje się z nową wersją? |
| kontrakt refleksyjny | Czy nazwy klas, deklarujący typ, pola, metody i adnotacje są nadal znajdowane? |
| zgodność serializacyjna | Czy nowy kod poprawnie odczytuje dane zapisane przez starą wersję? |
| integracje | Czy ORM, DI, proxy, konfiguracja i generatory nadal rozumieją model? |
| właściwości operacyjne | Czy zachowano blokady, transakcje, kolejność i wymagane granice wydajności? |

Zgodność binarna ma wąskie znaczenie: stare pliki `.class` nadal mogą się połączyć. Nie dowodzi zgodności źródłowej ani zachowania. Ponowna kompilacja wszystkich kontrolowanych komponentów może być wystarczająca w aplikacji wdrażanej atomowo, ale nie rozwiązuje wymagań biblioteki publicznej, wtyczek ani klientów aktualizowanych niezależnie.

### 1.4. Mapa hierarchii przed zmianą

Przed pierwszą transformacją warto przygotować mapę obejmującą:

- wszystkie nadklasy, interfejsy i podklasy, także spoza bieżącego modułu,
- publicznych i `protected` członków oraz użycia dostępu pakietowego,
- konstruktory, fabryki, kontenery DI i deserializatory tworzące obiekty,
- wywołania przez typ bazowy oraz rzutowania do typów konkretnych,
- `instanceof`, wzorce i instrukcje `switch`,
- wywołania `super`, metody `static`, `private`, `final` i `synchronized`,
- pola ukrywające inne pola o tej samej nazwie,
- adnotacje i miejsca użycia refleksji,
- mapowania ORM, format serializacji oraz zapisane historyczne dane,
- zewnętrzne podklasy i wcześniej skompilowane rozszerzenia,
- granice modułów i pakietów.

Samo wyszukanie słowa `extends` jest niewystarczające. Hierarchia może być zakodowana w konfiguracji, nazwie klasy w danych, mechanizmie `ServiceLoader`, mapowaniu ORM, konstrukcji proxy albo regule serializacji.

### 1.5. Pętla bezpiecznej transformacji

1. Nazwij obserwowalny kontrakt, który ma pozostać niezmieniony.
2. Dodaj najmniejszy wiarygodny test na poziomie tego kontraktu.
3. Wykonaj jeden ruch metody, pola albo relacji typu.
4. Skompiluj całą dotkniętą hierarchię.
5. Uruchom testy wszystkich podtypów przez typ bazowy.
6. Sprawdź klientów źródłowych, binarnych i integracyjnych odpowiednio do ryzyka.
7. Dopiero po migracji usuń stary członek lub poziom hierarchii.

Automatyczna refaktoryzacja IDE potrafi zmienić deklaracje i użycia widoczne w modelu programu. Nie potwierdza zgodności starych binariów, historycznych danych, zapytań, nazw tekstowych ani zachowania zewnętrznych podklas.

## 2. Semantyka dziedziczenia w Javie 25

### 2.1. Overriding i dynamiczna dyspozycja

Dla zwykłej metody instancyjnej kompilator wybiera sygnaturę, a podczas wykonania JVM wybiera najbardziej szczegółową implementację na podstawie klasy runtime odbiorcy. Rzutowanie odbiorcy do nadtypu nie wyłącza dynamicznej dyspozycji.

Ta reguła nie działa jednakowo dla wszystkich deklaracji:

- metoda `static` jest ukrywana, a nie nadpisywana,
- metoda `private` nie jest dziedziczona ani nadpisywana,
- wywołanie `super.method()` omija zwykłą dynamiczną dyspozycję dla bieżącej klasy i rozpoczyna wyszukiwanie od bezpośredniego nadtypu,
- metoda `final` nie może zostać nadpisana,
- implementacja nadpisująca nie może ograniczyć dostępności,
- implementacja może zawęzić typ wyniku kowariantnie,
- implementacja nie może poszerzyć deklarowanych wyjątków kontrolowanych.

Pull Up Method może zatem utworzyć nowy punkt dynamicznej dyspozycji. Metoda istniejąca wcześniej w zewnętrznej podklasie może po dodaniu deklaracji do nadklasy stać się override, choć autor nadklasy nigdy jej nie planował.

### 2.2. Overloading jest wybierany statycznie

Przeciążenie metody jest wybierane w czasie kompilacji na podstawie typów kompilacyjnych odbiorcy i argumentów. Klasa runtime argumentu nie powoduje ponownego wyboru przeciążenia.

Przeniesienie ciała metody do nadklasy może zmienić typy widoczne w miejscu kompilacji i dostępny zestaw przeciążeń. Kod o identycznym tekście może po Pull Up Method wywołać inną metodę pomocniczą. Dodanie nowego przeciążenia może także sprawić, że ponownie skompilowany klient wybierze inną sygnaturę, podczas gdy stary plik `.class` nadal wywoła sygnaturę zapisaną podczas wcześniejszej kompilacji.

### 2.3. Pola nie są polimorficzne

Dostęp do pola jest wybierany na podstawie typu kompilacyjnego wyrażenia kwalifikującego. Klasa runtime obiektu nie uczestniczy w tym wyborze. Podklasa może ukryć pole nadklasy o tej samej nazwie. Jeżeli obie deklaracje są polami instancyjnymi, pojedynczy obiekt zawiera dwa niezależne miejsca na stan. Pola `static` także mogą się ukrywać, lecz są odrębnymi zmiennymi należącymi do klas, a nie slotami pojedynczego obiektu.

Konsekwencje dla Pull Up Field i Push Down Field:

- należy wyszukać pola o tej samej nazwie w całej hierarchii,
- rzutowanie może zmienić odczytywane pole,
- `super.field` może odwoływać się do innego slotu niż `this.field`,
- połączenie dwóch pól instancyjnych może usunąć jeden niezależny stan,
- połączenie dwóch pól `static` scala dwa stany klasowe w jeden,
- bezpośredniego pola nie da się delegować tak jak metody.

Pola powinny być przenoszone ze względu na wspólne znaczenie, cykl życia i niezmienniki. Równa nazwa i typ nie wystarczają.

### 2.4. Konstruktory i inicjalizacja

Konstruktory nie są dziedziczone ani nadpisywane. Dodanie pierwszego jawnego konstruktora usuwa konstruktor domyślny, jeżeli programista nie odtworzy go jawnie.

Po przydzieleniu obiektu wszystkie jego pola otrzymują wartości domyślne. Java 25 pozwala umieścić ograniczony prolog przed jawnym `super(...)` albo `this(...)`. Dalszy porządek dla wykonywanego konstruktora jest następujący:

1. Wykonuje się jego prolog, o ile istnieje.
2. `this(...)` deleguje do innego konstruktora tej samej klasy, a `super(...)` uruchamia konstruktor nadklasy. Brak jawnego wywołania oznacza niejawne `super()`.
3. Po zakończeniu konstrukcji nadklasy inicjalizatory pól i bloki instancyjne bieżącej klasy wykonują się raz, w kolejności tekstowej. W łańcuchu `this(...)` wykonuje je konstruktor, który ostatecznie deleguje do nadklasy, a nie każdy konstruktor pośredni.
4. Wykonuje się epilog konstruktora, czyli kod po jawnym wywołaniu konstruktora. Po delegacji przez `this(...)` sterowanie wraca do epilogu konstruktora delegującego.

Prolog działa we wczesnym kontekście konstrukcji i nie może dowolnie używać konstruowanej instancji. Nowa składnia nie zmienia faktu, że inicjalizatory pól nadklasy kończą się przed inicjalizatorami pól podklasy.

Dynamiczna dyspozycja działa także podczas konstrukcji. Konstruktor nadklasy może wywołać metodę nadpisaną w podklasie, zanim pola tej podklasy zostaną zainicjalizowane. Przeniesienie wywołania lub inicjalizatora w górę hierarchii może więc ujawnić wartość domyślną pola albo zmienić kolejność efektów ubocznych.

Pole instancyjne `final`, które nie ma inicjalizatora w deklaracji, musi zostać przypisane dokładnie raz w dozwolonym kontekście klasy deklarującej: w jej bloku inicjalizacyjnym albo konstruktorze, z zachowaniem reguł definite assignment. Po zakończeniu każdego konstruktora musi być zdecydowanie przypisane. Analogiczne pole statyczne przypisuje się w deklaracji albo inicjalizatorze statycznym. Podklasa nie może przypisać odziedziczonego pola `final`.

### 2.5. `synchronized`, `static` i monitory

Metoda instancyjna `synchronized` blokuje monitor odbiorcy, czyli `this`. Przeniesienie jej między klasami nie zmienia odbiorcy zwykłego wywołania, ale zmiana na delegowanie może przenieść blokadę z wrappera na delegata.

Metoda `static synchronized` blokuje obiekt `Class` typu, który ją deklaruje. Pull Up Method takiej metody zmienia obiekt monitora. Nie jest to mechanicznie neutralny ruch dla kodu współbieżnego.

Przeniesienie pola `static` może zmienić także moment inicjalizacji klasy. Zwykły dostęp do odziedziczonego pola statycznego inicjalizuje klasę, która faktycznie deklaruje pole, a niekoniecznie klasę używaną jako kwalifikator. Wyjątkiem jest odczyt zmiennej stałej, czyli pola `final` typu prostego albo `String` z inicjalizatorem będącym wyrażeniem stałym. Jej wartość może zostać wstawiona do kodu klienta podczas kompilacji bez inicjalizacji klasy deklarującej.

### 2.6. Dostęp `protected` i granice pakietów

Członek o dostępie pakietowym nie jest dziedziczony przez podklasę z innego pakietu. Metoda o tej samej sygnaturze zadeklarowana w takiej podklasie nie jest override niedostępnej metody nadklasy.

`protected` obejmuje pełny dostęp z pakietu deklaracji oraz dodatkowo dostęp z podklas w innych pakietach. Poza pakietem nie oznacza to jednak dostępu przez dowolną referencję typu bazowego. Kod podklasy może użyć chronionego członka przez odbiorcę, którego typ jest tą podklasą albo jej podtypem. Przeniesienie klasy lub metody między pakietami może więc zmienić dziedziczenie i dostępność mimo niezmienionej sygnatury.

`protected` konstruktor jest dostępny podklasie przez `super(...)`, ale poza pakietem nie staje się przez to zwykłym publicznym punktem tworzenia `new Base(...)`.

### 2.7. Generyki, erasure i metody bridge

Sygnaturę przenoszonej metody należy analizować jako członka konkretnego typu sparametryzowanego oraz po erasure. Dwie różne deklaracje źródłowe mogą po usunięciu argumentów typu mieć ten sam deskryptor i utworzyć name clash.

Kowariantny albo wyspecjalizowany override może spowodować wygenerowanie syntetycznej metody bridge. Bridge podtrzymuje polimorfizm po erasure, ale jest widoczny dla refleksji i może wpływać na framework skanujący metody. Zmiana pierwszego ograniczenia parametru typu może zmienić erasure, a tym samym deskryptor binarny.

Przy ryzykownej zmianie warto porównać wynik:

```shell
javap -classpath target/classes -p -s -v pl.training.module5.stage3.EmailNotification
```

Należy zwrócić uwagę na deskryptory metod i pól oraz flagi `bridge` i `synthetic`.

### 2.8. Hierarchie `sealed`, rekordy i enumy

Lista `permits` obejmuje wyłącznie bezpośrednie podtypy. Każdy bezpośredni podtyp typu `sealed` musi być jawnie albo niejawnie `final`, `sealed` lub `non-sealed`. Rekord jest niejawnie `final`, a enum może być niejawnie `final` albo `sealed` zależnie od swoich stałych. Wstawienie pośredniej nadklasy wymaga zmiany relacji na obu poziomach.

W module nazwanym dozwolone bezpośrednie podtypy muszą należeć do tego samego modułu. W module nienazwanym muszą znajdować się w tym samym pakiecie.

Usunięcie starej podklasy z `permits` może spowodować błąd zgodności przy ładowaniu jej starego pliku `.class`. Dodanie nowego dozwolonego wariantu może być zgodne binarnie, ale stary wyczerpujący `switch` może podczas wykonania otrzymać nieznany wariant i rzucić `MatchException`.

Klasa `final`, rekord i enum nie mogą zostać rozszerzone zwykłą podklasą. Rekord nie może także rozszerzyć nowej nadklasy klasowej, ponieważ jego bezpośrednią nadklasą jest zawsze `Record`.

## 3. Pull Up Method i Pull Up Field

### 3.1. Cel

Pull Up Method przenosi wspólne zachowanie z podklas do ich nadklasy. Pull Up Field zastępuje odpowiadające sobie pola podklas jednym polem nadklasy. Celem nie jest maksymalne opróżnienie podklas, lecz umieszczenie wspólnego kontraktu i stanu na najniższym poziomie, na którym są prawdziwe dla wszystkich potomków.

### 3.2. Warunki Pull Up Method

Metodę można przenieść w górę, gdy:

- implementacje realizują ten sam kontrakt,
- różnice można wyrazić przez istniejący, prawidłowy punkt polimorficzny,
- metoda używa wyłącznie stanu i operacji dostępnych w nadklasie,
- jej sygnatura po erasure nie koliduje z innymi członkami,
- zmiana nie tworzy przypadkowego override w niekontrolowanym potomku,
- dostępność, wyjątki i typ wyniku pozostają zgodne,
- zachowane zostają znaczenie `super`, wybór przeciążeń i monitor.

Dwie metody o takim samym ciele nie muszą reprezentować tego samego zachowania. Przykładowo `calculateLimit()` w kredycie i limicie technicznym mogą dzisiaj wykonywać to samo mnożenie, ale mieć inne reguły rozwoju. Ich połączenie stworzyłoby fałszywą wspólną wiedzę.

### 3.3. Procedura Pull Up Method

1. Uruchom test kontraktowy dla każdego podtypu.
2. Porównaj kontrakty, nie tylko tekst implementacji.
3. Ujednolić nazwy i sygnatury małymi krokami.
4. Przenieś potrzebne operacje pomocnicze albo wprowadź abstrakcyjny punkt rozszerzenia.
5. Umieść jedną implementację w nadklasie.
6. Usuń identyczne implementacje z podklas.
7. Ponownie uruchom testy przez referencję typu bazowego i konkretnego.
8. Sprawdź zewnętrzne podklasy, refleksję i zgodność binarną, jeżeli należą do kontraktu.

Nie należy wywoływać overridable metody z konstruktora tylko po to, aby umożliwić Pull Up Method. Tworzy to punkt rozszerzenia działający na częściowo zainicjalizowanym obiekcie.

### 3.4. Warunki Pull Up Field

Pola można połączyć, gdy mają:

- to samo znaczenie domenowe,
- ten sam typ i sposób reprezentacji,
- ten sam cykl życia,
- te same reguły walidacji i własności,
- zgodny moment inicjalizacji,
- brak niezależnych obserwacji przez refleksję lub serializację.

Docelowe pole nadklasy powinno zwykle pozostać `private`. Konstruktor bazowy oraz nazwane operacje zapewniają lepszą kontrolę niezmienników niż surowe pole `protected`. Przykłady katalogowe z polem `protected` upraszczają mechanikę, ale nie są uniwersalną rekomendacją projektową.

### 3.5. Procedura Pull Up Field

1. Znajdź wszystkie deklaracje o tej samej nazwie w nadtypach i podtypach.
2. Sprawdź odczyty, zapisy, rzutowania i użycia `super.field`.
3. Ustal jedno znaczenie, walidację i właściciela stanu.
4. Dodaj prywatne pole oraz parametr konstruktora nadklasy.
5. Przekaż wartości przez `super(...)` z każdej podklasy.
6. Przekieruj metody do wspólnego stanu.
7. Usuń pola podklas dopiero po zniknięciu wszystkich odwołań.
8. Sprawdź kolejność inicjalizacji, serializację i stare binaria.

### 3.6. Ryzyka

Szczególnej uwagi wymagają:

- pola `static`, ponieważ dwa stany podklas mogą zostać scalone,
- inicjalizatory z efektami ubocznymi, ponieważ wykonają się wcześniej,
- pola `final`, ponieważ przypisanie musi pozostać w dozwolonym kontekście klasy deklarującej i spełniać definite assignment,
- metody zawierające `super`, ponieważ po przeniesieniu zmienia się bezpośredni nadtyp,
- metody `static synchronized`, ponieważ zmienia się monitor,
- metody generyczne i bridge, ponieważ może zmienić się deskryptor,
- prywatne haki serializacji, które dotyczą konkretnego segmentu klasy i nie są zwykłymi metodami wirtualnymi,
- frameworki używające `getDeclaredField` albo `getDeclaredMethod`, ponieważ deklarujący typ ulegnie zmianie.

Pull Up Method może być zgodny binarnie, jeżeli zachowuje nazwę, erased descriptor, rodzaj `static` lub instancyjny i wystarczającą dostępność. Nie oznacza to automatycznie zgodności zachowania. Pull Up Field również wymaga osobnej analizy. Stary odnośnik do pola podklasy może zostać rozwiązany w nadklasie tylko przy zgodnej nazwie, deskryptorze, dostępności i rodzaju pola. Dla zmiennej stałej stary klient może nie zawierać odnośnika do pola, ponieważ zachował wartość wstawioną podczas kompilacji.

## 4. Push Down Method i Push Down Field

### 4.1. Cel

Push Down Method przenosi operację z typu bazowego do gałęzi, która rzeczywiście jej potrzebuje. Push Down Field robi to samo ze stanem. Techniki zmniejszają zbyt szeroki kontrakt oraz usuwają stan nieistotny dla pozostałych podtypów.

Typowym sygnałem jest członek nadklasy, który:

- ma sens tylko dla jednego rodzaju obiektu,
- w części podklas pozostaje nieużywany,
- wymusza wartości `null`, puste kolekcje albo flagi bez znaczenia,
- w podklasach rzuca `UnsupportedOperationException`,
- jest używany wyłącznie po sprawdzeniu `instanceof`,
- powoduje, że klient typu bazowego zna szczegóły jednego wariantu.

### 4.2. Warunki Push Down Method

Metoda nie może być prawdziwą częścią kontraktu nadklasy. Należy znaleźć wszystkie wywołania przez referencję bazową oraz wywołania wewnątrz nadklasy. Jeżeli wiele podklas z jednej gałęzi potrzebuje operacji, metoda powinna trafić do ich najbliższego wspólnego przodka, a nie zostać skopiowana do liści.

Bezpieczna sekwencja w kontrolowanej aplikacji:

1. Zmień klientów tak, aby zależeli od właściwego podtypu albo węższego interfejsu.
2. Dodaj metodę w docelowej podklasie.
3. Przekieruj implementację bazową do nowego miejsca, jeżeli potrzebny jest etap przejściowy.
4. Uruchom testy wszystkich pozostałych podtypów.
5. Usuń metodę bazową po zniknięciu wszystkich klientów jej kontraktu.

W opublikowanej bibliotece pełne usunięcie metody bazowej jest zwykle zmianą łamiącą API. Stary plik `.class` odwołuje się do metody nadklasy i nie szuka jej w podklasach. Przejściowo można pozostawić oznaczoną jako przestarzała metodę zgodności, ale wtedy technika nie jest jeszcze zakończona na poziomie publicznego API.

Pozostawienie w bazie wyłącznie deklaracji abstrakcyjnej nie zawsze chroni zgodność. Stara podklasa bez implementacji może zakończyć wywołanie błędem podczas działania, a zmiana wcześniej konkretnej klasy na abstrakcyjną łamie stare miejsca tworzenia.

### 4.3. Warunki Push Down Field

Stan można przesunąć w dół, gdy:

- ma znaczenie tylko dla jednej gałęzi,
- kod nadklasy go nie odczytuje ani nie modyfikuje,
- pozostali potomkowie nie polegają na nim bezpośrednio ani przez refleksję,
- konstrukcja właściwego podtypu może go poprawnie zainicjalizować,
- migracja danych historycznych została zaplanowana, jeśli obiekty są utrwalane.

Procedura:

1. Przenieś zachowanie korzystające z pola do właściwej gałęzi.
2. Dodaj pole i inicjalizację w tej gałęzi.
3. Przekieruj wszystkie odczyty i zapisy.
4. Potwierdź, że nadklasa i pozostałe podtypy nie używają stanu.
5. Usuń pole z nadklasy.
6. Sprawdź serializację, ORM, refleksję i stare binaria.

Bezpośredniego publicznego pola nie można zachować przez zwykłe delegowanie. Dla pól instancyjnych jednoczesna deklaracja w bazie i podklasie tworzy dwa niezależne sloty. Dwie deklaracje `static` tworzą natomiast dwie zmienne klasowe. Jeżeli pole było częścią publicznego API, bezpieczniejsza migracja zaczyna się od metod dostępu i jednego magazynu stanu, a pełne usunięcie pola odbywa się w wydaniu łamiącym kontrakt.

### 4.4. Przesuwanie nie jest symetryczne

Pull Up i Push Down wyglądają jak operacje odwrotne, lecz mają inne konsekwencje dla klientów. Stary odnośnik do metody podklasy może w określonych warunkach znaleźć implementację przeniesioną do nadklasy. Stary odnośnik do metody nadklasy nie może szukać implementacji w nieznanej podklasie.

Analogicznie, stary odnośnik do pola nadklasy nie zostanie rozwiązany przez pole dodane w podklasie. Dlatego ruch w dół publicznego członka częściej wymaga jawnej migracji API.

## 5. Extract Superclass

### 5.1. Cel i właściwy sygnał

Extract Superclass tworzy wspólną nadklasę dla klas, które reprezentują warianty jednego pojęcia oraz dzielą część stanu lub zachowania. Właściwym sygnałem jest wspólny kontrakt, nie wyłącznie podobne linie kodu.

Przykładowe pytania:

- Jak klient nazwie wspólną rolę tych obiektów?
- Które operacje są prawdziwe dla każdego wariantu?
- Czy dowolny wariant może zostać użyty przez referencję nowej nadklasy?
- Czy wspólne pola mają identyczne znaczenie i cykl życia?
- Czy nowa nadklasa ma własny, stabilny niezmiennik?
- Czy klasy nie potrzebują już innej istotnej nadklasy?

Jeżeli nie da się nadać nadklasie nazwy domenowej bez słów `Base`, `Common` albo `Abstract`, warto ponownie sprawdzić, czy powstaje prawdziwa abstrakcja. Nazwy techniczne nie są zawsze błędem, ale często ujawniają ekstrakcję motywowaną tylko duplikacją.

### 5.2. Procedura

1. Zabezpiecz osobno kontrakt każdej klasy.
2. Utwórz pustą abstrakcyjną nadklasę.
3. Dołącz jedną klasę i uruchom testy.
4. Dołącz kolejne klasy pojedynczo.
5. Przenieś wspólne pola przez Pull Up Field.
6. Przenieś wspólne metody przez Pull Up Method.
7. Zostaw różnice za abstrakcyjnymi lub chronionymi punktami rozszerzeń tylko wtedy, gdy są częścią stabilnego algorytmu.
8. Dodaj test kontraktowy wykonywany dla wszystkich konkretnych podtypów.
9. Zmigruj klientów, którzy rzeczywiście potrzebują wspólnego typu.

W przykładzie powiadomień nowa klasa `Notification` posiada prywatny wspólny stan, normalizację i budowę podsumowania. Podklasy określają kanał oraz zachowanie wysyłki specyficzne dla kanału.

### 5.3. Konstruktory i fabryki

Wprowadzenie nadklasy nie przenosi konstruktorów. Każda dotychczasowa publiczna sygnatura musi zostać świadomie zachowana albo zmigrowana. Parametry wspólnego stanu są przekazywane do `super(...)`, a parametry specyficzne pozostają w podklasie.

Należy sprawdzić:

- czy istniał niejawny konstruktor bezargumentowy,
- jaką miał dostępność,
- czy framework wymaga konstruktora publicznego lub `protected`,
- czy fabryki zachowują typ i semantykę wyniku,
- czy kolejność walidacji i wyjątków pozostaje obserwowalnie taka sama,
- czy konstruktor nadklasy nie wywołuje metod podlegających overridingowi.

### 5.4. Ograniczenia

Java pozwala rozszerzyć tylko jedną klasę. Jeżeli podobne klasy mają już różne, istotne nadklasy, wspólny interfejs oraz kompozycja mogą być lepszym rozwiązaniem.

Wstawienie pustego poziomu hierarchii może zachować zgodność binarną, jeżeli żaden typ nie traci dotychczasowych nadtypów. Każde przeniesienie metody i pola trzeba jednak ocenić osobno. Zmieniają się także `getSuperclass`, deklarujący typ członków, miejsce adnotacji i założenia narzędzi mapujących.

## 6. Extract Subclass

### 6.1. Cel

Extract Subclass tworzy podklasę dla stabilnego podzbioru instancji, który ma dodatkowy stan albo odmienne zachowanie. Pozostałe instancje zachowują prostszy typ bazowy.

Typowe sygnały:

- pole opcjonalne ma sens tylko dla części obiektów,
- wiele metod zaczyna się od sprawdzenia tej samej cechy,
- część instancji wymaga dodatkowego niezmiennika,
- konstrukcja obiektu jest kontrolowana przez fabryki,
- specjalna rola nie zmienia się swobodnie podczas życia obiektu.

Technika nie jest dobrym wyborem dla roli dynamicznej. Jeżeli obiekt może w czasie życia przechodzić między trybami, lepszym kierunkiem bywa State lub Strategy. Jeśli istnieje kilka niezależnych osi zmienności, mnożenie podklas prowadzi do kombinatorycznej liczby typów i również wskazuje na kompozycję.

### 6.2. Procedura

1. Zabezpiecz zachowanie każdego wariantu kodu warunkowego.
2. Wprowadź fabrykę albo zinwentaryzuj wszystkie miejsca użycia `new`.
3. Utwórz podklasę dla specjalnego wariantu.
4. Zmień właściwe punkty tworzenia, aby zwracały nowy typ runtime.
5. Przenieś specyficzne metody przez Push Down Method.
6. Przenieś specyficzny stan przez Push Down Field.
7. Usuń warunek z typu bazowego.
8. Uruchom test kontraktowy przez referencję bazową oraz testy podklasy.

W przykładzie `DeliveryJob` wariant zaplanowany otrzymuje osobną klasę `ScheduledDeliveryJob` i pole `scheduledAt`. Fabryka nadal zwraca `DeliveryJob`, dzięki czemu klienci opierający się na zachowaniu bazowym nie muszą znać konkretnej podklasy.

### 6.3. Zmiana klasy runtime

Extract Subclass nie zachowuje dokładnej klasy runtime specjalnych obiektów. Może to wpływać na:

- `getClass()` i refleksję,
- `equals` używające porównania klas przez `getClass()`,
- reguły ORM i dyskryminatory hierarchii,
- metadane typu w JSON lub innym formacie,
- serializację Javy,
- kontenery DI i generatory proxy,
- wzorce `instanceof` oraz `switch`.

Istniejącego obiektu klasy bazowej nie można zmienić w instancję nowej podklasy. Migracja utrwalonego modelu wymaga odtworzenia obiektu właściwego typu albo osobnej transformacji danych.

## 7. Extract Interface

### 7.1. Interfejs opisuje rolę klienta

Extract Interface wyodrębnia spójny podzbiór operacji potrzebnych określonej grupie klientów. Nie należy kopiować do interfejsu całego publicznego API klasy tylko dlatego, że jest dostępne.

Dobry interfejs:

- ma nazwę roli biznesowej lub technicznej rozpoznawalnej przez klientów,
- zawiera minimalny spójny zestaw operacji,
- ma opisane warunki wstępne, wyniki, błędy i efekty,
- może mieć więcej niż jedną sensowną implementację, choć nie musi istnieć od pierwszego dnia,
- nie ujawnia operacji technicznych konkretnej implementacji,
- nie istnieje wyłącznie na potrzeby frameworka mockującego.

W studium przypadku `OutboundNotification` zawiera tylko operacje wspólne dla klienta wysyłającego partię powiadomień. `NotificationBatch` nie potrzebuje dostępu do konstruktorów, pól ani chronionych punktów rozszerzeń klasy bazowej.

### 7.2. Kontrakt behawioralny

Dodanie `implements` ustanawia nominalną relację podtypowania i wymusza zgodność sygnatur, ale nie dowodzi zgodności behawioralnej. Wszystkie implementacje powinny przejść ten sam zestaw testów kontraktowych.

Należy sprawdzić:

- poprawne dane graniczne i `null`,
- typy oraz kolejność wyjątków,
- efekty uboczne dla sukcesu i błędu,
- idempotencję, jeśli jest obiecana,
- atomowość i bezpieczeństwo wątkowe, jeśli należą do umowy,
- reguły tożsamości oraz `equals` i `hashCode`, jeśli klient ich używa.

Metoda implementująca interfejs musi być `public`. Może zwracać typ kowariantny i nie może poszerzać wyjątków kontrolowanych. Węższy typ parametru nie implementuje metody interfejsu. Pola interfejsu są niejawnie `public static final`, więc interfejs nie jest miejscem dla przenoszonego stanu instancji. Nie każde takie pole jest jednak zmienną stałą w znaczeniu kompilatora. Wymaga to typu prostego albo `String` oraz inicjalizatora będącego wyrażeniem stałym.

### 7.3. Bezpieczna procedura

1. Zidentyfikuj konkretnych klientów i używany przez nich podzbiór API.
2. Zapisz kontrakt tego podzbioru.
3. Utwórz interfejs z minimalnymi metodami.
4. Dodaj `implements` bez zmiany istniejących publicznych sygnatur klasy.
5. Uruchom test kontraktowy dla dotychczasowej implementacji.
6. Zmieniaj zależności klientów pojedynczo z klasy na interfejs.
7. Dopiero po migracji rozważ ograniczenie niepotrzebnego API klasy.

Zmiana typu parametru publicznej metody z klasy konkretnej na interfejs zmienia deskryptor JVM. Dla kodu źródłowego może wyglądać jak bezpieczne poszerzenie, ale stary klient binarny może nie znaleźć dawnej sygnatury. W bibliotece należy pozostawić stare przeciążenie delegujące do nowego kontraktu.

Zmiana typu wyniku z klasy na interfejs również zmienia deskryptor i nie może zostać obsłużona przeciążeniem o tej samej nazwie i parametrach. Potrzebna jest stara metoda albo nowa nazwa.

Samo dodanie istniejącego interfejsu do listy nadtypów klasy jest zwykle zgodne binarnie, jeżeli klasa nie traci żadnego dotychczasowego nadtypu. Nadal może zmienić wybór przeciążenia po ponownej kompilacji, wyniki refleksji, obliczany domyślnie `serialVersionUID` oraz rozstrzyganie metod domyślnych.

### 7.4. Metody domyślne

Metoda `default` powinna być poprawna dla każdej implementacji i używać wyłącznie operacji kontraktu. Nie jest automatycznie bezpiecznym sposobem dodawania dowolnego zachowania.

Najważniejsze reguły:

- konkretna metoda klasy ma pierwszeństwo przed metodą domyślną,
- bardziej szczegółowy interfejs ma pierwszeństwo przed mniej szczegółowym,
- dwa niespokrewnione interfejsy z równoważnymi metodami domyślnymi wymagają jawnego rozstrzygnięcia,
- metoda domyślna nie może zastąpić publicznej metody `Object`, takiej jak `equals`, `hashCode` lub `toString`,
- dodanie metody domyślnej może być binarnie zgodne, ale konflikt w starym binarium może ujawnić się podczas wywołania,
- sekwencja kilku wywołań w metodzie domyślnej nie staje się przez to atomowa,
- dynamiczne proxy kieruje wywołanie metody domyślnej do swojego handlera.

Jeżeli interfejs ma pozostać celem lambdy, należy rozważyć `@FunctionalInterface`. Dodanie drugiej niezależnej metody abstrakcyjnej, która nie jest override-equivalent i nie odpowiada publicznej metodzie `Object`, może sprawić, że interfejs przestanie być funkcyjny. Metody `default`, `static` i `private` nie zwiększają liczby abstrakcyjnych metod jego kontraktu funkcyjnego.

Dodanie metody abstrakcyjnej do opublikowanego interfejsu nie musi uniemożliwić załadowania starej implementacji, ale ponownie kompilowana implementacja nie spełni nowego kontraktu, a wywołanie brakującej metody na starym pliku `.class` może zakończyć się `AbstractMethodError`. Formalna zgodność binarna samego dodania nie jest więc wystarczającą gwarancją migracji.

## 8. Collapse Hierarchy

### 8.1. Cel

Collapse Hierarchy scala nadklasę i podklasę, gdy ich rozróżnienie nie reprezentuje już odrębnego kontraktu, wariantu domenowego ani używanego punktu rozszerzenia. Celem jest usunięcie pustego lub bezwartościowego poziomu pośrednictwa.

Sygnały:

- podklasa nie dodaje stanu ani zachowania,
- override jedynie wywołuje `super` bez zmiany kontraktu,
- nadklasa ma tylko jednego potomka i nie pełni samodzielnej roli,
- klienci nie korzystają z rozróżnienia typów,
- historyczny powód podziału przestał istnieć.

Sama mała liczba linii nie uzasadnia scalenia. Pusty typ może być znaczącym markerem, punktem rozszerzenia, granicą modułu, osobnym typem w protokole albo kontraktem używanym przez DI.

### 8.2. Procedura

1. Wybierz typ, który ma pozostać publicznym właścicielem kontraktu.
2. Znajdź wszystkie konstrukcje, rzutowania, `instanceof`, `switch` i użycia refleksji.
3. Przenieś pola i metody z usuwanego typu.
4. Rozwiąż kolizje pól, sygnatur i wywołań `super`.
5. Przekieruj fabryki, DI, konfigurację i mapowania.
6. Uruchom testy przez oba dawne typy.
7. Usuń zbędny poziom dopiero po migracji klientów.

W przykładzie `LegacyNotificationFormatter` jest nadklasą bez osobnego kontraktu, a `NotificationFormatter` nie dodaje zachowania. Stan końcowy pozostawia jedną klasę o nazwie używanej przez klientów.

### 8.3. Publiczne typy i zgodność

Usunięcie klasy łamie klienta odwołującego się do jej nazwy. Może zakończyć się błędem ładowania klasy, nieudanym rzutowaniem, awarią konfiguracji albo brakiem mapowania danych.

W publicznej bibliotece etap przejściowy może pozostawić cienki typ zgodności oznaczony jako przestarzały. Zachowa część miejsc konstrukcji i przypisań, ale nie wszystkie kontrakty refleksyjne, serializacyjne i frameworkowe. Pełne usunięcie należy zaplanować jako zmianę łamiącą.

## 9. Replace Inheritance with Composition

### 9.1. Kiedy dziedziczenie jest niewłaściwe

Dziedziczenie warto zastąpić kompozycją, gdy:

- podklasa nie jest zastępowalnym wariantem nadklasy,
- `extends` służy głównie do ponownego użycia niewielkiej części implementacji,
- odziedziczone API jest znacznie szersze niż potrzeby klientów,
- podklasa musi blokować lub unieważniać odziedziczone operacje,
- zachowanie powinno być wymienne podczas życia obiektu,
- trzeba połączyć kilka niezależnych zachowań,
- zmiany w nadklasie nie powinny automatycznie poszerzać API potomka.

Dziedziczenie pozostaje właściwe, gdy relacja podtypowania jest prawdziwym, stabilnym kontraktem. Celem nie jest usunięcie wszystkich hierarchii.

### 9.2. Procedura

1. Zinwentaryzuj całe odziedziczone API, nie tylko obecnie wywoływane metody.
2. Zabezpiecz obserwowalne zachowanie klientów testami charakterystyki.
3. Określ wąski kontrakt, który ma pozostać dostępny.
4. Dodaj pole delegata z jednoznaczną własnością i cyklem życia.
5. Wprowadzaj jawne metody przekazujące tylko potrzebne operacje.
6. Przenieś zachowanie podklasy do wrappera albo współpracownika.
7. Zmigruj klientów zależnych od nadklasy.
8. Usuń `extends` po zniknięciu zależności od podtypowania.
9. Dodaj testy specyficzne dla delegowania, nie tylko wspólny test kontraktowy.

W przykładzie `RecipientList extends ArrayList<String>` dziedziczy bardzo szerokie API, w tym operacje, których domena nie zatwierdziła. Wersja z kompozycją jawnie deklaruje `add`, `remove(Object)`, `contains`, `size`, iterator i migawkę. Implementacja `Iterable` udostępnia dodatkowo odziedziczone `forEach` i `spliterator`, dlatego również te operacje trzeba uwzględnić przy ocenie publicznego kontraktu.

### 9.3. To nie jest automatycznie zgodność API

Po usunięciu `extends ArrayList<String>` obiekt przestaje być `ArrayList` i `List`. Klienci zależni od tej relacji typu nie będą się kompilować, a ich stare binaria mogą przestać działać. Pojedyncze wywołanie może nadal się połączyć, jeśli wrapper jawnie deklaruje metodę o identycznej nazwie i deskryptorze JVM, na przykład `size()`. Nie zachowuje to jednak przypisań, rzutowań ani parametrów oczekujących `List`. Zmiana może być prawidłową migracją wewnątrz kontrolowanej aplikacji, ale nie jest zgodnym zamiennikiem dowolnego publicznego `List`.

Jeżeli publiczny kontrakt `List` musi pozostać, należy zaimplementować i wiernie delegować pełny interfejs albo pozostawić warstwę zgodności. Nawet wtedy trzeba określić zachowanie `equals`, `hashCode`, iteratorów, spliteratora, mutacji podczas iteracji i metod domyślnych kolekcji.

### 9.4. Pułapki delegowania

Mechaniczne przekazanie metod może zmienić zachowanie:

- odziedziczona metoda wywoływała overridable hook na tym samym `this`, a delegat wywoła własny hook,
- `super.method()` nie ma wiernego odpowiednika w zwykłym `delegate.method()`,
- metoda fluent zwracająca `this` może zwrócić delegata zamiast wrappera,
- callback może otrzymać delegata zamiast wrappera,
- metoda `synchronized` blokuje monitor delegata zamiast wrappera,
- odziedziczone `equals` i `hashCode` mogą zniknąć,
- współdzielony delegat może nieplanowanie połączyć stan kilku wrapperów,
- członek `protected` nie jest dostępny przez kompozycję,
- publiczne pola i metody statyczne nie mają transparentnego odpowiednika w delegowaniu,
- zmienia się kolejność konstrukcji i inicjalizacji.

Test kontraktowy potwierdza wyniki wymagane przez wspólny interfejs. Nie wykryje liczby wywołań delegata, kolejności efektów, tożsamości `this` ani używanego monitora, jeżeli te obserwacje nie zostały jawnie zapisane. Dla wrappera potrzebne są dodatkowe testy z recording fake, spy, callbackiem i kontrolowaną współbieżnością.

## 10. Zgodność, serializacja i integracje frameworkowe

### 10.1. Macierz typowych skutków

| Transformacja | Typowe ryzyko dla publicznego kontraktu |
| --- | --- |
| Pull Up Method | przypadkowy override, inne `super`, przeciążenie, refleksja i monitor metody statycznej |
| Pull Up Field | scalenie slotów, wcześniejsza inicjalizacja, zmiana deklarującej klasy i segmentu serializacji |
| Push Down Method | klient typu bazowego traci sygnaturę; stare binarium może nie znaleźć metody |
| Push Down Field | klient bazowy traci pole; pozostawienie kopii tworzy dwa stany |
| Extract Superclass | zmiana bezpośredniego nadtypu, konstruktorów, refleksji, ORM i serializacji |
| Extract Subclass | zmiana dokładnego typu runtime, fabryk, mapowania i postaci danych |
| Extract Interface | zmiana nominalnych typów, deskryptorów parametrów lub wyników i metod domyślnych |
| Collapse Hierarchy | usunięcie nazwy klasy, utrata nadtypu, konfiguracji i segmentu danych |
| Replace Inheritance with Composition | utrata przypisywalności, odziedziczonego API, hooków, monitora i serializowalności |

Tabela nie zastępuje analizy konkretnej hierarchii. Członek prywatny wewnątrz atomowo wdrażanej aplikacji ma inny kontrakt niż publiczny typ rozszerzany przez niezależnych klientów.

### 10.2. Test zgodności binarnej

Pełny test biblioteki wymaga dwóch wersji artefaktu:

1. Skompiluj klienta przeciw wersji `v1`.
2. Zachowaj jego niezmienione pliki `.class`.
3. Uruchom te pliki z biblioteką `v2`.
4. Osobno ponownie skompiluj klienta przeciw `v2`.
5. Porównaj linkowanie i zachowanie obu uruchomień.

Sam pełny build po ponownej kompilacji sprawdza głównie zgodność źródłową kontrolowanego kodu. Nie odtwarza sytuacji wtyczki, która nadal ma pliki skompilowane przeciw poprzedniej wersji.

Typowe błędy wykonania przy niezgodnej zmianie to brak klasy, brak metody, brak pola, niedozwolony dostęp, niezgodna zmiana `static` i metody abstrakcyjnej. Konkretna postać zależy od deklaracji oraz miejsca zapisanego w stałej puli klienta.

### 10.3. Refleksja i adnotacje

Przeniesienie członka zmienia wyniki operacji deklaracyjnych:

- `getDeclaredFields()` i `getDeclaredMethods()` pokazują tylko bieżący typ,
- `getFields()` i `getMethods()` uwzględniają dostępne publiczne elementy dziedziczone,
- `Method.getDeclaringClass()` wskazuje nowe miejsce deklaracji,
- może zmienić się liczba metod bridge i synthetic,
- `Class.getSuperclass()` oraz lista bezpośrednich interfejsów ulegają zmianie.

Adnotacja klasy oznaczona `@Inherited` może przechodzić z nadklasy na podklasę. Mechanizm ten nie dziedziczy adnotacji z interfejsu i nie działa automatycznie dla adnotacji metod. Przeniesienie adnotowanej deklaracji trzeba ocenić według sposobu skanowania konkretnego frameworka.

W systemie modułowym publiczny interfejs używany przez inne moduły musi znajdować się w eksportowanym pakiecie. Framework wykonujący głęboką refleksję może wymagać dodatkowego otwarcia pakietu.

### 10.4. Serializacja Javy

Domyślna postać serializowana jest opisana osobno dla każdego serializowalnego poziomu hierarchii. Przeniesienie pola z podklasy do nadklasy nie jest dla strumienia prostym zachowaniem tej samej nazwy. Jeżeli oba poziomy są serializowalne, stary segment traci pole, a nowy segment je zyskuje. Bez jawnej migracji historyczna wartość może zostać odrzucona, a nowe pole otrzyma wartość domyślną. Jeżeli nowa nadklasa nie jest serializowalna, jej pola nie należą do domyślnej postaci strumienia i wymagają osobnego odtworzenia.

Stały `serialVersionUID` zapobiega tylko części odrzuceń wersji klasy. Nie przenosi stanu między segmentami hierarchii i nie naprawia zmienionego modelu obiektu.

Dla zgodności danych należy:

- zachować plik binarny lub fixture `.ser` utworzony przez wersję `v1`,
- odczytać go rzeczywistą wersją `v2`,
- sprawdzić wszystkie wartości i niezmienniki po odczycie,
- w razie wymaganej zgodności dwukierunkowej wykonać test odwrotny,
- zastosować `serialPersistentFields`, prywatne `writeObject` i `readObject` albo serialization proxy, jeśli format musi być stabilny.

Prywatne `writeObject`, `readObject` i `readObjectNoData` są hakami stanu konkretnej klasy. Nie podlegają zwykłemu overridingowi i nie powinny być mechanicznie przenoszone przez Pull Up Method.

Przy deserializacji zwykłego `Serializable` konstruktory i inicjalizatory klas serializowalnych nie wykonują się. Uruchamiany jest konstruktor bezargumentowy pierwszej nieserializowalnej nadklasy. Nowa nadklasa może więc zmienić wymagania konstrukcyjne oraz stan początkowy odczytywanego obiektu.

### 10.5. ORM, DI i proxy

Zmiana hierarchii encji jest zwykle migracją modelu trwałego, a nie wyłącznie lokalną refaktoryzacją. Może wpływać na strategię dziedziczenia, dyskryminator, tabele, klucze, zapytania polimorficzne, lazy loading i proxy.

Wyodrębniony interfejs może opisywać rolę domenową, ale sam nie staje się encją. Delegate jako obiekt osadzony, relacja do osobnej encji i pole nietrwałe mają różną tożsamość, cykl życia oraz postać schematu.

Test integracyjny powinien używać rzeczywistego dostawcy i obejmować co najmniej zapis, `flush`, wyczyszczenie kontekstu, ponowny odczyt, proxy, lazy loading, zapytanie polimorficzne i relacje cascade odpowiednie dla modelu.

Kontener DI może polegać na nazwie klasy, konstruktorze bezargumentowym, widoczności, adnotacji albo konkretnym typie proxy. Po zmianie należy uruchomić kontekst aplikacyjny, a nie ograniczać się do testu z ręcznym `new`.

### 10.6. Minimalny zestaw testów wysokiego ryzyka

W zależności od kontraktu wykorzystaj:

- test kontraktowy uruchamiany dla każdego podtypu,
- test różnicowy wersji przed i po z niezależnym oczekiwanym wynikiem,
- test wyboru pola przez referencję bazową, podklasę, rzutowanie i `super`,
- test kolejności konstruktorów oraz inicjalizatorów,
- test wywołania metody wirtualnej podczas konstrukcji,
- test metod `static`, `private`, `final` i `synchronized`,
- porównanie deskryptorów przez `javap`,
- uruchomienie starego pliku `.class` z nową biblioteką,
- odczyt historycznego pliku serializacji,
- test refleksji i skanowania adnotacji,
- test rzeczywistego ORM, DI lub proxy,
- test wszystkich wyczerpujących `switch` dla hierarchii `sealed`.

## 11. Studium przypadku: hierarchia powiadomień

### 11.1. Kontrakt szkoleniowy

System obsługuje powiadomienia e-mail i SMS. Oba kanały:

- wymagają niepustego identyfikatora wiadomości, nadawcy i treści,
- usuwają białe znaki z początku i końca wartości,
- normalizują identyfikator nadawcy do wielkich liter niezależnie od domyślnych ustawień regionalnych,
- budują stabilne podsumowanie tekstowe,
- zwracają status `SENT` lub `FAILED`.

Tylko udana wysyłka SMS może zawierać znacznik `RECEIPT`. Flaga potwierdzenia nie ma znaczenia dla e-maila. Kod początkowy zawiera jednak skopiowane pole i prywatną metodę także w klasie e-mail.

Kontrakt przykładu obejmuje publiczne konstruktory klas konkretnych, wyniki metod, typy walidacji i kolejność elementów tekstu. Nie obejmuje dokładnego miejsca deklaracji prywatnych pól, refleksji ani domyślnej serializacji. Nie dopuszcza także zewnętrznych podklas. Klasy abstrakcyjne `Notification` mają dostęp pakietowy, a publiczną granicę tworzą finalne klasy konkretne oraz, w etapie 3, interfejs. To jawne ograniczenie pozwala przeprowadzić Push Down Field jako refaktoryzację w ramach przyjętej granicy. Gdy układ pól albo rozszerzalne `protected` API są obserwowalne, potrzebna jest migracja kontraktu.

### 11.2. Etap 0: niezależne klasy i duplikacja

Plik `pl/training/module5/stage0/EmailNotification.java`:

```java
package pl.training.module5.stage0;

import java.util.Locale;
import java.util.Objects;

public final class EmailNotification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;

    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
        this.deliveryReceipt = deliveryReceipt;
    }

    public String messageId() {
        return messageId;
    }

    public String summary() {
        return messageId + "|" + senderId + "|" + body + "|EMAIL";
    }

    public String dispatch(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }

    @SuppressWarnings("unused")
    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
```

Plik `pl/training/module5/stage0/SmsNotification.java`:

```java
package pl.training.module5.stage0;

import java.util.Locale;
import java.util.Objects;

public final class SmsNotification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;

    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
        this.deliveryReceipt = deliveryReceipt;
    }

    public String messageId() {
        return messageId;
    }

    public String summary() {
        return messageId + "|" + senderId + "|" + body + "|SMS";
    }

    public String dispatch(boolean successful) {
        String result = summary() + (successful ? "|SENT" : "|FAILED");
        return successful ? appendReceipt(result) : result;
    }

    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
```

Duplikacja wskazuje możliwy kierunek, ale nie jest jeszcze dowodem poprawnej nadklasy. Najpierw test charakterystyki zapisuje niezależne oczekiwania dla obu kanałów, sukcesu, błędu, flagi potwierdzenia i walidacji.

Plik `pl/training/module5/stage0/NotificationCharacterizationTest.java`:

```java
package pl.training.module5.stage0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

final class NotificationCharacterizationTest {
    @Test
    void capturesEmailBehavior() {
        var notification = new EmailNotification(
                " msg-1 ",
                " ops ",
                " Deployment ready ",
                true);

        assertEquals("msg-1", notification.messageId());
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL",
                notification.summary());
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL|SENT",
                notification.dispatch(true));
        assertEquals(
                "msg-1|OPS|Deployment ready|EMAIL|FAILED",
                notification.dispatch(false));
    }

    @Test
    void capturesSmsReceiptBehavior() {
        var withReceipt = new SmsNotification(
                "msg-2",
                "ops",
                "Deploy now",
                true);
        var withoutReceipt = new SmsNotification(
                "msg-2",
                "ops",
                "Deploy now",
                false);

        assertEquals(
                "msg-2|OPS|Deploy now|SMS|SENT|RECEIPT",
                withReceipt.dispatch(true));
        assertEquals(
                "msg-2|OPS|Deploy now|SMS|FAILED",
                withReceipt.dispatch(false));
        assertEquals(
                "msg-2|OPS|Deploy now|SMS|SENT",
                withoutReceipt.dispatch(true));
    }

    @Test
    void capturesValidation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new EmailNotification(" ", "ops", "body", false));
        assertThrows(
                NullPointerException.class,
                () -> new SmsNotification("msg", null, "body", false));
    }
}
```

Test nie interpretuje, czy obecne zachowanie jest idealne. Tworzy punkt odniesienia potrzebny do bezpiecznego rozdzielenia ruchu struktury od ewentualnych późniejszych zmian funkcjonalnych.

### 11.3. Etap 1: Extract Superclass oraz Pull Up

Nowa `Notification` reprezentuje prawdziwe wspólne pojęcie. Prywatne pola `messageId`, `senderId` i `body`, normalizacja, `messageId()`, `summary()` oraz budowa wyniku wysyłki przechodzą do nadklasy.

Na tym etapie również `deliveryReceipt` i `appendReceipt` są przeniesione w górę. Jest to celowo niedoskonały stan przejściowy: ujawnia, że mechaniczne usunięcie duplikacji pozostawiło w bazie cechę właściwą tylko SMS.

Plik `pl/training/module5/stage1/Notification.java`:

```java
package pl.training.module5.stage1;

import java.util.Locale;
import java.util.Objects;

abstract class Notification {
    private final String messageId;
    private final String senderId;
    private final String body;
    private final boolean deliveryReceipt;

    protected Notification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
        this.deliveryReceipt = deliveryReceipt;
    }

    public final String messageId() {
        return messageId;
    }

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }

    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }

    protected final String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }

    protected abstract String channel();

    public abstract String dispatch(boolean successful);

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
```

Plik `pl/training/module5/stage1/EmailNotification.java`:

```java
package pl.training.module5.stage1;

public final class EmailNotification extends Notification {
    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body, deliveryReceipt);
    }

    @Override
    protected String channel() {
        return "EMAIL";
    }

    @Override
    public String dispatch(boolean successful) {
        return dispatchResult(successful);
    }
}
```

Plik `pl/training/module5/stage1/SmsNotification.java`:

```java
package pl.training.module5.stage1;

public final class SmsNotification extends Notification {
    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body, deliveryReceipt);
    }

    @Override
    protected String channel() {
        return "SMS";
    }

    @Override
    public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }
}
```

`channel()` jest świadomym punktem polimorficznym używanym przez finalną metodę `summary()`. Finalność dokumentuje, że algorytm podsumowania ma być wspólny, a podtyp dostarcza wyłącznie wartość kanału. Nie należy jednak dodawać `final` do publicznej metody istniejącej biblioteki bez sprawdzenia zewnętrznych override i zgodności binarnej.

### 11.4. Etap 2: Push Down cechy SMS

Testy pokazują, że flaga potwierdzenia nie wpływa na e-mail. Pole `deliveryReceipt` i metoda `appendReceipt` zostają przeniesione do `SmsNotification`. Klasa bazowa odzyskuje kontrakt prawdziwy dla wszystkich podtypów.

Plik `pl/training/module5/stage2/Notification.java`:

```java
package pl.training.module5.stage2;

import java.util.Locale;
import java.util.Objects;

abstract class Notification {
    private final String messageId;
    private final String senderId;
    private final String body;

    protected Notification(
            String messageId,
            String senderId,
            String body) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
    }

    public final String messageId() {
        return messageId;
    }

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }

    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }

    protected abstract String channel();

    public abstract String dispatch(boolean successful);

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
```

Plik `pl/training/module5/stage2/EmailNotification.java`:

```java
package pl.training.module5.stage2;

public final class EmailNotification extends Notification {
    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean ignoredDeliveryReceipt) {
        this(messageId, senderId, body);
    }

    public EmailNotification(
            String messageId,
            String senderId,
            String body) {
        super(messageId, senderId, body);
    }

    @Override
    protected String channel() {
        return "EMAIL";
    }

    @Override
    public String dispatch(boolean successful) {
        return dispatchResult(successful);
    }
}
```

Plik `pl/training/module5/stage2/SmsNotification.java`:

```java
package pl.training.module5.stage2;

public final class SmsNotification extends Notification {
    private final boolean deliveryReceipt;

    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body);
        this.deliveryReceipt = deliveryReceipt;
    }

    @Override
    protected String channel() {
        return "SMS";
    }

    @Override
    public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }

    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }
}
```

Czteroargumentowy konstruktor e-mail pozostaje jako przejściowy punkt zgodności źródłowej kontrolowanych klientów i ignoruje dawną flagę zgodnie z zapisanym zachowaniem. Nowy konstruktor trzyargumentowy komunikuje docelowy model. W publicznej bibliotece decyzja o usunięciu starej sygnatury wymaga osobnego cyklu wycofania.

### 11.5. Etap 3: Extract Interface

Klient partii powiadomień potrzebuje wyłącznie operacji wysyłki. Nie potrzebuje identyfikatora, podsumowania, konstruktorów ani chronionej metody `channel()`. Interfejs opisuje dokładnie tę wąską rolę.

Plik `pl/training/module5/stage3/OutboundNotification.java`:

```java
package pl.training.module5.stage3;

@FunctionalInterface
public interface OutboundNotification {
    String dispatch(boolean successful);
}
```

Plik `pl/training/module5/stage3/Notification.java`:

```java
package pl.training.module5.stage3;

import java.util.Locale;
import java.util.Objects;

abstract class Notification implements OutboundNotification {
    private final String messageId;
    private final String senderId;
    private final String body;

    protected Notification(
            String messageId,
            String senderId,
            String body) {
        this.messageId = normalized(messageId, "messageId");
        this.senderId = normalized(senderId, "senderId")
                .toUpperCase(Locale.ROOT);
        this.body = normalized(body, "body");
    }

    public final String messageId() {
        return messageId;
    }

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }

    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }

    protected abstract String channel();

    private static String normalized(String value, String fieldName) {
        String normalized = Objects.requireNonNull(
                value,
                fieldName + " must not be null").strip();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank");
        }
        return normalized;
    }
}
```

Plik `pl/training/module5/stage3/EmailNotification.java`:

```java
package pl.training.module5.stage3;

public final class EmailNotification extends Notification {
    public EmailNotification(
            String messageId,
            String senderId,
            String body,
            boolean ignoredDeliveryReceipt) {
        this(messageId, senderId, body);
    }

    public EmailNotification(
            String messageId,
            String senderId,
            String body) {
        super(messageId, senderId, body);
    }

    @Override
    protected String channel() {
        return "EMAIL";
    }

    @Override
    public String dispatch(boolean successful) {
        return dispatchResult(successful);
    }
}
```

Plik `pl/training/module5/stage3/SmsNotification.java`:

```java
package pl.training.module5.stage3;

public final class SmsNotification extends Notification {
    private final boolean deliveryReceipt;

    public SmsNotification(
            String messageId,
            String senderId,
            String body,
            boolean deliveryReceipt) {
        super(messageId, senderId, body);
        this.deliveryReceipt = deliveryReceipt;
    }

    @Override
    protected String channel() {
        return "SMS";
    }

    @Override
    public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }

    private String appendReceipt(String result) {
        return deliveryReceipt ? result + "|RECEIPT" : result;
    }
}
```

Plik `pl/training/module5/stage3/NotificationBatch.java`:

```java
package pl.training.module5.stage3;

import java.util.List;
import java.util.Objects;

public final class NotificationBatch {
    public List<String> dispatchAll(
            List<? extends OutboundNotification> notifications,
            boolean successful) {
        Objects.requireNonNull(notifications, "notifications must not be null");
        var validatedNotifications = notifications.stream()
                .map(notification -> Objects.requireNonNull(
                        notification,
                        "notification must not be null"))
                .toList();
        return validatedNotifications.stream()
                .map(notification -> notification.dispatch(successful))
                .toList();
    }
}
```

`List<? extends OutboundNotification>` pozwala przekazać listę konkretnego podtypu, ponieważ metoda jedynie odczytuje elementy. Nie umożliwia bezpiecznego dodania dowolnego `OutboundNotification` do listy klienta. Pierwsze przejście waliduje całą kolekcję, zanim rozpocznie wysyłkę. Dzięki temu niepoprawny późniejszy element nie powoduje częściowego efektu. Zwracane przez `Stream.toList()` wyniki są niemodyfikowalne, co jest częścią kontraktu tej nowej klasy, a nie skutkiem samego Extract Interface.

### 11.6. Test równoważności wszystkich etapów

Test porównuje każdy etap z niezależnym oczekiwaniem. Macierz obejmuje obie wartości dawnej flagi e-mail, obie wartości potwierdzenia SMS, sukces, błąd oraz typy wyjątków dla wartości pustej i `null`. Samo `before.equals(after)` byłoby niewystarczające, ponieważ obie wersje mogłyby zawierać ten sam błąd.

Plik `pl/training/module5/NotificationHierarchyEquivalenceTest.java`:

```java
package pl.training.module5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

final class NotificationHierarchyEquivalenceTest {
    @Test
    void allStagesPreserveEmailContractForEveryLegacyFlagAndStatus() {
        for (boolean deliveryReceipt : List.of(false, true)) {
            for (boolean successful : List.of(false, true)) {
                List<String> results = List.of(
                        new pl.training.module5.stage0.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage1.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage2.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage3.EmailNotification(
                                " msg-1 ",
                                " ops ",
                                " Deployment ready ",
                                deliveryReceipt)
                                .dispatch(successful));
                String expected = "msg-1|OPS|Deployment ready|EMAIL|"
                        + (successful ? "SENT" : "FAILED");

                assertAllEqual(expected, results);
            }
        }
    }

    @Test
    void allStagesPreserveSmsContractForEveryReceiptAndStatus() {
        for (boolean deliveryReceipt : List.of(false, true)) {
            for (boolean successful : List.of(false, true)) {
                List<String> results = List.of(
                        new pl.training.module5.stage0.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage1.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage2.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful),
                        new pl.training.module5.stage3.SmsNotification(
                                "msg-2", "ops", "Deploy now", deliveryReceipt)
                                .dispatch(successful));
                String expected = "msg-2|OPS|Deploy now|SMS|"
                        + (successful ? "SENT" : "FAILED")
                        + (successful && deliveryReceipt ? "|RECEIPT" : "");

                assertAllEqual(expected, results);
            }
        }
    }

    @Test
    void allStagesPreserveValidationTypes() {
        assertExceptionType(
                IllegalArgumentException.class,
                List.of(
                        () -> new pl.training.module5.stage0.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage1.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage2.EmailNotification(
                                " ", "ops", "body", false),
                        () -> new pl.training.module5.stage3.EmailNotification(
                                " ", "ops", "body", false)));
        assertExceptionType(
                NullPointerException.class,
                List.of(
                        () -> new pl.training.module5.stage0.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage1.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage2.SmsNotification(
                                "msg", null, "body", false),
                        () -> new pl.training.module5.stage3.SmsNotification(
                                "msg", null, "body", false)));
    }

    private static void assertAllEqual(
            String expected,
            List<String> results) {
        results.forEach(result -> assertEquals(expected, result));
    }

    private static <T extends Throwable> void assertExceptionType(
            Class<T> expectedType,
            List<Executable> scenarios) {
        scenarios.forEach(scenario -> assertEquals(
                expectedType,
                assertThrows(expectedType, scenario).getClass()));
    }
}
```

Test kontraktowy `OutboundNotificationContractTest` uruchamia te same reguły przez nowy interfejs i potwierdza, że `NotificationBatch` nie zależy od konkretnej klasy. Sprawdza również niemodyfikowalność listy wynikowej. Test odrzucenia `null` dla kolekcji oraz jej elementów dodatkowo potwierdza, że błąd jest wykrywany przed pierwszym wywołaniem `dispatch`.

## 12. Dodatkowe przypadki przed i po

### 12.1. Extract Subclass: zadanie zaplanowane

Kod początkowy reprezentuje dwa warianty przez `Optional<Instant>`. Nie każde pole opcjonalne jest błędem, ale tutaj stan zaplanowania jest stabilny i zmienia zachowanie wysyłki. Fabryki kontrolują wybór wariantu.

Plik `pl/training/module5/extractsubclass/before/DeliveryJob.java`:

```java
package pl.training.module5.extractsubclass.before;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public final class DeliveryJob {
    private static final String SENT = "SENT";
    private static final String WAITING_UNTIL = "WAITING_UNTIL ";

    private final Optional<Instant> scheduledAt;

    private DeliveryJob(Optional<Instant> scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public static DeliveryJob immediate() {
        return new DeliveryJob(Optional.empty());
    }

    public static DeliveryJob scheduled(Instant scheduledAt) {
        return new DeliveryJob(Optional.of(Objects.requireNonNull(
                scheduledAt,
                "scheduledAt must not be null")));
    }

    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");

        return scheduledAt
                .filter(now::isBefore)
                .map(instant -> WAITING_UNTIL + instant)
                .orElse(SENT);
    }
}
```

Po ekstrakcji zwykłe zadanie zawiera tylko zachowanie natychmiastowe. Konstruktor ma dostęp pakietowy, więc klienci korzystają z fabryk i nie mogą przypadkowo utworzyć nieznanego wariantu.

Plik `pl/training/module5/extractsubclass/after/DeliveryJob.java`:

```java
package pl.training.module5.extractsubclass.after;

import java.time.Instant;
import java.util.Objects;

public class DeliveryJob {
    DeliveryJob() {
    }

    public static DeliveryJob immediate() {
        return new DeliveryJob();
    }

    public static DeliveryJob scheduled(Instant scheduledAt) {
        return new ScheduledDeliveryJob(scheduledAt);
    }

    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        return "SENT";
    }
}
```

Plik `pl/training/module5/extractsubclass/after/ScheduledDeliveryJob.java`:

```java
package pl.training.module5.extractsubclass.after;

import java.time.Instant;
import java.util.Objects;

public final class ScheduledDeliveryJob extends DeliveryJob {
    private final Instant scheduledAt;

    ScheduledDeliveryJob(Instant scheduledAt) {
        this.scheduledAt = Objects.requireNonNull(
                scheduledAt,
                "scheduledAt must not be null");
    }

    @Override
    public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");

        if (now.isBefore(scheduledAt)) {
            return "WAITING_UNTIL " + scheduledAt;
        }
        return super.dispatchAt(now);
    }
}
```

Test `DeliveryJobEquivalenceTest` sprawdza zadanie natychmiastowe oraz zadanie zaplanowane przed, dokładnie w i po terminie. Zachowanie `now == scheduledAt` jest częścią kontraktu: zadanie jest wtedy wysłane. Test celowo nie wymaga zachowania dokładnej klasy runtime, ponieważ ta właściwość musi się zmienić, aby technika miała sens.

Decyzje projektowe:

- `scheduledAt` jest niezmienne, więc wariant nie zmienia się w czasie życia,
- fabryka zachowuje wynik typu `DeliveryJob`,
- specjalna podklasa nie jest częścią publicznego API konstrukcji,
- baza zachowuje pełny kontrakt wspólny,
- `super.dispatchAt(now)` świadomie ponownie używa zachowania bazowego po osiągnięciu terminu.

Jeśli zadanie mogłoby być wielokrotnie planowane, wstrzymywane i wznawiane, podklasa nie byłaby stabilnym modelem stanu. Wtedy należałoby rozważyć osobny obiekt polityki albo State.

### 12.2. Collapse Hierarchy: zbędny formatter

Stan początkowy ma nadklasę zawierającą całe zachowanie i pustą podklasę używaną przez klientów.

Plik `pl/training/module5/collapse/before/LegacyNotificationFormatter.java`:

```java
package pl.training.module5.collapse.before;

import java.util.Objects;

public class LegacyNotificationFormatter {
    public String format(String recipient, String message) {
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(message, "message must not be null");

        return "To: " + recipient + "\nMessage: " + message;
    }
}
```

Plik `pl/training/module5/collapse/before/NotificationFormatter.java`:

```java
package pl.training.module5.collapse.before;

public final class NotificationFormatter
        extends LegacyNotificationFormatter {
}
```

Po scaleniu zachowanie trafia do nazwy używanej przez docelowych klientów.

Plik `pl/training/module5/collapse/after/NotificationFormatter.java`:

```java
package pl.training.module5.collapse.after;

import java.util.Objects;

public final class NotificationFormatter {
    public String format(String recipient, String message) {
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(message, "message must not be null");

        return "To: " + recipient + "\nMessage: " + message;
    }
}
```

Test równoważności sprawdza dokładny tekst dla dwóch zestawów danych, w tym polskie znaki. Scalenie jest zachowujące dla kontrolowanych klientów `NotificationFormatter`, ale usunięcie publicznej nazwy `LegacyNotificationFormatter` nie byłoby zgodne dla niezależnego klienta tej klasy.

Nadanie wynikowej klasie `final` również powinno odpowiadać granicy kontraktu. Jest bezpieczne w tym zamkniętym przykładzie. W bibliotece pozwalającej klientom tworzyć podklasy byłoby osobną zmianą łamiącą.

### 12.3. Replace Inheritance with Composition: lista odbiorców

Kod początkowy dziedziczy po `ArrayList<String>` wyłącznie dla ponownego użycia kolekcji:

Plik `pl/training/module5/composition/before/RecipientList.java`:

```java
package pl.training.module5.composition.before;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RecipientList extends ArrayList<String> {
    @Serial
    private static final long serialVersionUID = 1L;

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(this));
    }
}
```

Wersja po zmianie posiada prywatną listę i jawnie deklaruje scharakteryzowany podzbiór operacji. Ponieważ implementuje `Iterable`, dziedziczy również `forEach` i `spliterator`:

Plik `pl/training/module5/composition/after/RecipientList.java`:

```java
package pl.training.module5.composition.after;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class RecipientList implements Iterable<String> {
    private final List<String> recipients = new ArrayList<>();

    public boolean add(String recipient) {
        return recipients.add(recipient);
    }

    public boolean remove(Object recipient) {
        return recipients.remove(recipient);
    }

    public boolean contains(Object recipient) {
        return recipients.contains(recipient);
    }

    public int size() {
        return recipients.size();
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(recipients).iterator();
    }

    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recipients));
    }
}
```

Test `RecipientListEquivalenceTest` wykonuje tę samą sekwencję na wersji dziedziczącej i delegującej:

1. dodaje elementy, w tym duplikat,
2. sprawdza rozmiar, zawartość i kolejność iteracji,
3. usuwa pierwsze równe wystąpienie przez `remove(Object)`,
4. porównuje niemodyfikowalne migawki,
5. modyfikuje właścicieli i potwierdza izolację wcześniejszych migawek,
6. potwierdza, że iterator wersji po zmianie nie pozwala ominąć jawnego API mutacji,
7. sprawdza wynik usuwania nieistniejącego elementu,
8. potwierdza niezależność stanu dwóch instancji wrappera.

To świadomie wąski kontrakt. Zmiana nie zachowuje dowolnych klientów `ArrayList` lub `List`. W szczególności poza kontraktem pozostają:

- przypisywalność do `List<String>`,
- `add(int, E)`, `set`, `clear`, `sort`, `replaceAll` i pozostałe odziedziczone metody,
- przeciążenie `remove(int)`,
- mutacja przez `Iterator.remove()`, którą wersja po zmianie celowo blokuje,
- semantyka `equals` i `hashCode` kolekcji,
- dokładne charakterystyki `Spliterator`, mimo że sama metoda pozostaje dostępna przez `Iterable`,
- serializowalność odziedziczona po `ArrayList`,
- dokładna klasa runtime i wyniki refleksji.

`forEach` i `spliterator` są odziedziczonymi operacjami odczytu `Iterable`. Dokładny zestaw charakterystyk spliteratora nie jest w tym ćwiczeniu zachowywany. Klient korzystający wcześniej z `Iterator.remove()` wymaga migracji do jawnej metody `remove(Object)`.

Wycofanie tych właściwości jest dopuszczalne tylko po potwierdzeniu, że nie należą do kontraktu kontrolowanej aplikacji, albo po zaplanowanej migracji klientów.

### 12.4. Program demonstracyjny

Plik `pl/training/module5/Module5Examples.java` uruchamia wszystkie przykłady modułu:

```java
package pl.training.module5;

import java.time.Instant;
import java.util.List;

import pl.training.module5.stage3.EmailNotification;
import pl.training.module5.stage3.NotificationBatch;
import pl.training.module5.stage3.OutboundNotification;
import pl.training.module5.stage3.SmsNotification;

public final class Module5Examples {
    private Module5Examples() {
    }

    public static void main(String[] args) {
        String legacyNotification =
                new pl.training.module5.stage0.SmsNotification(
                        "msg-1", "ops", "Deployment ready", true)
                        .dispatch(true);
        String refactoredNotification = new SmsNotification(
                "msg-1", "ops", "Deployment ready", true)
                .dispatch(true);

        Instant scheduledAt = Instant.parse("2030-06-15T10:15:30Z");
        Instant now = scheduledAt.minusSeconds(1);
        String jobBefore =
                pl.training.module5.extractsubclass.before.DeliveryJob
                        .scheduled(scheduledAt)
                        .dispatchAt(now);
        String jobAfter =
                pl.training.module5.extractsubclass.after.DeliveryJob
                        .scheduled(scheduledAt)
                        .dispatchAt(now);

        String formattedBefore =
                new pl.training.module5.collapse.before.NotificationFormatter()
                        .format("ops@example.com", "Deployment ready");
        String formattedAfter =
                new pl.training.module5.collapse.after.NotificationFormatter()
                        .format("ops@example.com", "Deployment ready");

        var inheritedRecipients =
                new pl.training.module5.composition.before.RecipientList();
        var composedRecipients =
                new pl.training.module5.composition.after.RecipientList();
        inheritedRecipients.add("ops@example.com");
        composedRecipients.add("ops@example.com");

        List<OutboundNotification> batch = List.of(
                new EmailNotification("mail-1", "ops", "Ready"),
                new SmsNotification("sms-1", "ops", "Ready", true));

        System.out.println("Hierarchy stages equivalent: "
                + legacyNotification.equals(refactoredNotification));
        System.out.println("Extract subclass equivalent: "
                + jobBefore.equals(jobAfter));
        System.out.println("Collapse hierarchy equivalent: "
                + formattedBefore.equals(formattedAfter));
        System.out.println("Composition client behavior equivalent: "
                + inheritedRecipients.snapshot()
                        .equals(composedRecipients.snapshot()));
        System.out.println("Batch results: "
                + new NotificationBatch().dispatchAll(batch, true));
    }
}
```

Oczekiwany wynik:

```text
Hierarchy stages equivalent: true
Extract subclass equivalent: true
Collapse hierarchy equivalent: true
Composition client behavior equivalent: true
Batch results: [mail-1|OPS|Ready|EMAIL|SENT, sms-1|OPS|Ready|SMS|SENT|RECEIPT]
```

## 13. Warsztat praktyczny

Każde ćwiczenie rozpoczyna się od działających testów i kończy krótką retrospektywą. Uczestnik powinien wykonywać małe kroki. Nie należy łączyć ruchu członka, zmiany zachowania i usunięcia publicznego API w jednym kroku.

### 13.1. Ćwiczenie 1: Extract Superclass i Pull Up

Punkt startowy: `pl.training.module5.stage0`.

Cel: utworzyć wspólną klasę `Notification`, zachowując dokładne wyniki i walidację obu kanałów.

Zadania:

1. Uruchom `NotificationCharacterizationTest`.
2. Wypisz pola i metody o wspólnej nazwie.
3. Dla każdego elementu odpowiedz, czy ma wspólne znaczenie, cykl życia i kontrakt.
4. Utwórz pustą abstrakcyjną `Notification`.
5. Dołącz najpierw jedną klasę i ponownie uruchom testy.
6. Przenieś wspólny stan przez konstruktor bazowy.
7. Przenieś normalizację, `messageId`, budowę podsumowania i wspólną część wyniku wysyłki.
8. Wprowadź minimalny punkt polimorficzny dla nazwy kanału.
9. Dołącz drugą klasę.
10. Uruchom test różnicowy wszystkich etapów.

Pytania kontrolne:

- Dlaczego pola bazowe pozostają prywatne?
- Czy `deliveryReceipt` naprawdę należy do wspólnego kontraktu?
- Co zmieniłoby wywołanie `channel()` z konstruktora?
- Czy dodanie `final` do `summary()` byłoby bezpieczne dla zewnętrznych podklas?
- Jak zmieniłoby się zachowanie, gdyby normalizacja zależała od domyślnych ustawień regionalnych?

Kryteria akceptacji:

- publiczne konstruktory klas konkretnych zachowują sygnatury,
- testy charakterystyki nadal przechodzą,
- w każdej klasie istnieje jedno źródło wspólnej reguły,
- nadklasa nie ujawnia surowych pól `protected`,
- uczestnik umie wskazać celowo niedoskonały stan `deliveryReceipt` w bazie.

### 13.2. Ćwiczenie 2: Push Down i Extract Interface

Punkt startowy: `pl.training.module5.stage1`.

Cel: usunąć cechę potwierdzenia z kontraktu bazowego i utworzyć rolę potrzebną klientowi partii.

Zadania:

1. Znajdź wszystkie odczyty pola `deliveryReceipt` i wywołania `appendReceipt`.
2. Potwierdź testem, że e-mail ignoruje flagę dla sukcesu i błędu.
3. Przenieś pole oraz metodę do `SmsNotification`.
4. Usuń parametr z konstruktora nadklasy.
5. Zachowaj przejściowy czteroargumentowy konstruktor e-mail i dodaj konstruktor docelowy.
6. Zdefiniuj minimalny interfejs dla `NotificationBatch`.
7. Dodaj `implements` do nadklasy bez zmiany dotychczasowych sygnatur metod konkretnych.
8. Zmień klienta partii, aby zależał od interfejsu.
9. Uruchom testy kontraktowe dla e-maila i SMS.
10. Zaprojektuj ścieżkę wycofania starego konstruktora dla publicznej biblioteki.

Kryteria akceptacji:

- `Notification` nie zawiera stanu specyficznego dla SMS,
- SMS zachowuje potwierdzenie tylko po sukcesie,
- e-mail zachowuje dotychczasowy wynik dla obu wartości dawnej flagi,
- `NotificationBatch` nie zależy od klasy abstrakcyjnej,
- interfejs nie zawiera metod potrzebnych wyłącznie implementacji,
- test kontraktowy jest wykonywany przez typ `OutboundNotification`.

### 13.3. Ćwiczenie 3: Extract Subclass i Collapse Hierarchy

Część A, punkt startowy: `pl.training.module5.extractsubclass.before`.

1. Zapisz tabelę wyników zadania natychmiastowego oraz zaplanowanego przed, w i po terminie.
2. Wprowadź fabryki, jeśli eksperyment zaczyna się od publicznych konstruktorów.
3. Utwórz `ScheduledDeliveryJob`.
4. Zmień wyłącznie fabrykę wariantu zaplanowanego.
5. Przenieś `scheduledAt` oraz warunek do podklasy.
6. Usuń `Optional` i warunek z klasy bazowej.
7. Uruchom test różnicowy.
8. Wypisz kontrakty, których technika celowo nie zachowuje.

Część B, punkt startowy: `pl.training.module5.collapse.before`.

1. Ustal, którą publiczną nazwę zachowują kontrolowani klienci.
2. Znajdź użycia obu klas, konstruktorów i relacji `extends`.
3. Przenieś zachowanie do typu docelowego.
4. Usuń zbędny poziom.
5. Uruchom test dokładnego formatu i walidacji.
6. Zaproponuj kompatybilny etap przejściowy, gdyby oba typy były publiczne.

Kryteria akceptacji:

- fabryki `DeliveryJob` zachowują publiczne typy wyników,
- termin graniczny ma jawny test,
- specjalny stan istnieje wyłącznie w specjalnym typie,
- uczestnik rozpoznaje zmianę dokładnej klasy runtime,
- formatter po scaleniu ma jedno źródło zachowania,
- usunięcie publicznej nazwy nie jest błędnie nazwane zgodną refaktoryzacją biblioteki.

### 13.4. Ćwiczenie 4: Replace Inheritance with Composition

Punkt startowy: `pl.training.module5.composition.before`.

Cel: zastąpić dziedziczenie po `ArrayList` wąską fasadą kolekcji bez przypadkowej obietnicy zgodności całego API `List`.

Zadania:

1. Wyszukaj wszystkie metody wywoływane przez kontrolowanych klientów.
2. Zapisz skrypt zachowania obejmujący duplikaty, kolejność, usuwanie i migawkę.
3. Dodaj prywatny `List<String>` jako delegate.
4. Wprowadź po jednej metodzie przekazującej scharakteryzowane operacje.
5. Zaimplementuj `Iterable<String>`, jeśli iteracja należy do kontraktu.
6. Usuń `extends ArrayList<String>`.
7. Uruchom skrypt różnicowy.
8. Sprawdź, które odziedziczone operacje przestały się kompilować.
9. Oceń `equals`, `hashCode`, serializację, iterator i własność delegata.
10. Przygotuj wariant planu dla publicznej klasy, której klienci wymagają pełnego `List`.

Kryteria akceptacji:

- wersje przed i po zachowują zatwierdzoną sekwencję klienta,
- wrapper nie dziedziczy niezatwierdzonego API,
- każdy wrapper posiada własny delegate,
- wcześniejsza migawka nie zmienia się po późniejszej mutacji właściciela,
- uczestnik potrafi wskazać różnicę między `remove(Object)` i `remove(int)`,
- utrata przypisywalności do `List` jest nazwana zmianą kontraktu, jeżeli była obserwowalna.

### 13.5. Retrospektywa po ćwiczeniu

Zespół odpowiada na pięć pytań:

1. Jaki kontrakt był chroniony?
2. Który krok był mechaniczny, a który wymagał decyzji projektowej?
3. Jaki test wykryłby najgroźniejszą regresję?
4. Której warstwy zgodności nie sprawdziły testy jednostkowe?
5. Czy końcowa hierarchia opisuje domenę, czy tylko ponownie używa kodu?

## 14. Rozwiązania i omówienie ćwiczeń

### 14.1. Ćwiczenie 1

Oczekiwany wynik odpowiada `stage1`. Zalecana kolejność:

1. Utworzyć pustą `Notification` i dołączyć jedną klasę.
2. Przenieść wspólne prywatne pola i ustawiać je w konstruktorze nadklasy.
3. Przenieść `normalized`, ponieważ jej kontrakt jest identyczny.
4. Przenieść `messageId()`.
5. Wprowadzić `channel()` i przenieść `summary()`.
6. Wydzielić wspólny `dispatchResult`.
7. Dołączyć drugi podtyp i usunąć duplikację.
8. Uruchomić test po każdym kroku.

`channel()` jest jedyną różnicą potrzebną wspólnemu algorytmowi podsumowania. Nie ma potrzeby tworzenia getterów dla wszystkich pól ani oznaczania pól jako `protected`.

Przeniesienie `deliveryReceipt` jest na tym etapie poprawne w przyjętym kontrakcie zachowania, ponieważ obie klasy miały pole, a wyniki pozostają niezmienione. Jednocześnie jest to sygnał złego modelu: konstruktor e-mail przyjmuje wartość bez znaczenia, a nadklasa opisuje cechę nieprawdziwą dla wszystkich wariantów. Następne ćwiczenie naprawia ten problem.

### 14.2. Ćwiczenie 2

Oczekiwany wynik odpowiada `stage2` i `stage3`.

Najpierw należy przenieść zachowanie `appendReceipt` do SMS, a dopiero potem stan. Dzięki temu nadklasa przestaje zależeć od pola przed jego usunięciem. Czteroargumentowy konstruktor e-mail deleguje do trzyargumentowego i jawnie komunikuje, że parametr jest ignorowany wyłącznie dla zgodności okresu przejściowego.

Interfejs `OutboundNotification` wynika z jedynej operacji klienta partii: `dispatch`. Nie zawiera `messageId()`, `summary()` ani `channel()`, ponieważ klient ich nie potrzebuje. Nie zawiera także metod związanych z potwierdzeniem SMS. Adnotacja `@FunctionalInterface` zabezpiecza rolę jako interfejs z jedną metodą abstrakcyjną.

Zmiana pola parametru klienta z `Notification` na `OutboundNotification` jest bezpieczna źródłowo dla kontrolowanych wywołań przy ponownej kompilacji. Jeżeli metoda klienta była częścią publicznej biblioteki, stary deskryptor powinien pozostać jako przeciążenie delegujące przez okres migracji.

### 14.3. Ćwiczenie 3

Dla Extract Subclass krytyczna jest zmiana punktu tworzenia przed Push Down. Obiekt zaplanowany musi rzeczywiście być `ScheduledDeliveryJob`, zanim stan i zachowanie znikną z `DeliveryJob`.

Warunek `now.isBefore(scheduledAt)` oznacza, że równość z terminem przechodzi do `SENT`. Pominięcie testu granicznego mogłoby pozwolić na nieświadomą zmianę operatora.

Dokładna klasa runtime specjalnego zadania zmienia się celowo. Jeśli `getClass`, mapowanie typu albo serializacja są częścią kontraktu, potrzebna jest dodatkowa migracja. Nie można ukryć tego faktu testem sprawdzającym wyłącznie tekst.

Dla Collapse Hierarchy pozostaje `NotificationFormatter`, ponieważ to tej nazwy używają kontrolowani klienci. Zachowanie `LegacyNotificationFormatter` zostaje przeniesione, a pusta klasa znika. W publicznym API, w którym istnieją klienci obu nazw, należałoby utrzymać cienką klasę zgodności lub zaplanować wydanie łamiące.

### 14.4. Ćwiczenie 4

Oczekiwany wynik odpowiada `composition.after.RecipientList`. Własny delegate uniemożliwia przypadkowe dzielenie stanu między wrapperami. Każda jawnie zadeklarowana metoda domenowa odpowiada operacji potwierdzonej przez klienta.

`remove(Object)` jest deklarowane jawnie. Gdyby klasa ujawniła także `remove(int)`, wywołanie z literałem liczbowym wybierałoby usunięcie po indeksie, a nie usunięcie elementu o wartości. Dla listy innych typów lub po zmianie przeciążeń łatwo o subtelną regresję.

`snapshot()` tworzy nową `ArrayList`, a następnie niemodyfikowalny widok tej kopii. Późniejsze zmiany delegata nie są widoczne w wyniku. Jest to płytka migawka, ale `String` jest niemutowalny, więc w tym przykładzie nie występuje problem mutowalnych elementów.

W publicznej klasie obiecującej `List` istnieją trzy główne możliwości:

- zachować dziedziczenie, jeżeli podtypowanie jest prawdziwe,
- zaimplementować pełny `List` i delegować z precyzyjnymi testami kontraktu,
- wprowadzić nowy wąski typ, migrować klientów i usunąć stary typ w wydaniu łamiącym.

Nie należy wybierać automatycznej delegacji setek operacji bez rozumienia ich kontraktów.

## 15. Sprawdzenie wiedzy

### Pytania

1. Dlaczego podobny kod w dwóch klasach nie wystarcza do Extract Superclass?
2. Co oznacza zastępowalność podtypu w praktyce?
3. Czym overriding różni się od overloading?
4. Czy rzutowanie odbiorcy na nadtyp wyłącza dynamiczną dyspozycję metody?
5. Dlaczego dwa pola instancyjne o tej samej nazwie mogą przechowywać różne wartości w jednym obiekcie?
6. Co może zmienić przeniesienie inicjalizatora pola do nadklasy?
7. Dlaczego konstruktor podklasy nie może przypisać odziedziczonego pola instancyjnego `final`, które nie ma inicjalizatora w deklaracji?
8. Jaki monitor blokuje metoda `static synchronized`?
9. Co trzeba sprawdzić przed Pull Up Method zawierającej `super.method()`?
10. Dlaczego Push Down Method publicznej metody bazowej zwykle łamie stare binaria?
11. Dlaczego nie można emulować Push Down Field przez dwa pola o tej samej nazwie?
12. Kiedy Extract Subclass jest gorsze od State lub Strategy?
13. Jaką obserwowalną właściwość celowo zmienia Extract Subclass?
14. Dlaczego interfejs powinien wynikać z potrzeb klienta?
15. Czy dodanie metody `default` gwarantuje zgodność behawioralną?
16. Dlaczego zmiana parametru publicznej metody z klasy na interfejs może złamać stare `.class`?
17. Kiedy pusta podklasa nadal może mieć znaczenie?
18. Jakie zachowanie może zmienić delegowanie metody `synchronized`?
19. Dlaczego jawny `serialVersionUID` nie wystarcza po Pull Up Field?
20. Jak wiarygodnie przetestować zgodność binarną?
21. Co należy sprawdzić dla hierarchii `sealed` po dodaniu podtypu?
22. Dlaczego `RecipientList` po kompozycji nie jest zgodnym zamiennikiem dowolnego `List`?

### Odpowiedzi

1. Nadklasa deklaruje wspólne pojęcie i kontrakt. Podobieństwo implementacji może być przypadkowe albo wynikać wyłącznie ze współdzielenia algorytmu technicznego.
2. Obiekt podtypu przyjmuje dane dopuszczone przez bazę i zachowuje jej gwarancje wyników, wyjątków, stanu, efektów oraz innych obserwowalnych właściwości.
3. Overriding wybiera implementację instancyjnej sygnatury według klasy runtime odbiorcy. Overloading wybiera sygnaturę w czasie kompilacji według typów kompilacyjnych.
4. Nie. Zwykłe wywołanie nadal wybierze override klasy runtime. Dynamiczną dyspozycję omija między innymi jawne `super`.
5. Pole podklasy ukrywa pole nadklasy, ale go nie zastępuje. Jeśli obie deklaracje są instancyjne, obiekt zawiera osobny slot dla każdej z nich, a wybór zależy od typu kompilacyjnego odwołania. Pola statyczne należą osobno do klas.
6. Moment wykonania, kolejność efektów ubocznych, widoczne wartości podczas konstrukcji, a dla pola statycznego także moment inicjalizacji klasy.
7. Niezainicjalizowane w deklaracji pole instancyjne `final` musi zostać przypisane w konstruktorze albo bloku inicjalizacyjnym klasy deklarującej i spełniać definite assignment dla każdego konstruktora. Podklasa nie może zapisać odziedziczonego pola finalnego.
8. Obiekt `Class` klasy deklarującej metodę. Przeniesienie metody statycznej do innej klasy zmienia monitor.
9. Czy wyszukiwanie rozpoczęte od bezpośredniego nadtypu nadal wybiera tę samą implementację i czy nie zmienia się semantyka wywołania.
10. Stary odnośnik wskazuje metodę nadklasy i mechanizm rozwiązywania nie szuka implementacji w podklasach.
11. Dla pól instancyjnych powstałyby dwa niezależne sloty wybierane statycznie. Dla pól `static` powstałyby dwie zmienne klasowe. W obu przypadkach zapis do jednej deklaracji nie aktualizuje drugiej.
12. Gdy specjalna rola zmienia się podczas życia obiektu albo istnieje kilka niezależnych osi zachowania powodujących mnożenie klas.
13. Dokładny typ runtime specjalnych obiektów, a wraz z nim potencjalnie refleksję, `equals`, ORM i serializację.
14. Dzięki temu opisuje minimalną spójną rolę i nie wiąże klientów z operacjami konkretnej implementacji.
15. Nie. Konflikty metod domyślnych, metoda klasy o innej semantyce, atomowość i stare implementacje nadal mogą zmienić zachowanie.
16. Typ parametru jest częścią deskryptora metody zapisanego w pliku `.class`. Stary klient szuka dokładnie dawnej sygnatury.
17. Może być markerem domenowym, punktem rozszerzenia, typem w protokole, elementem konfiguracji, celem DI albo częścią formatu danych.
18. Metoda odziedziczona blokowała monitor wrappera, a metoda delegata może blokować monitor innego obiektu. Zmienia to wzajemne wykluczanie i widoczność pamięci.
19. Format zapisuje stan osobno dla każdego poziomu hierarchii. Przeniesienie pola zmienia segment, a UID nie migruje wartości między segmentami.
20. Uruchomić niezmieniony klient skompilowany przeciw wersji starej z biblioteką nową, a osobno ponownie skompilować klienta i porównać oba wyniki.
21. Listę bezpośrednich `permits`, modyfikatory podtypów oraz wszystkie wyczerpujące `switch`, które mogą otrzymać nowy wariant.
22. Traci przypisywalność, wiele odziedziczonych operacji oraz kontrakty `equals`, `hashCode`, serializacji i implementacji iteracji, chyba że zostaną jawnie odtworzone.

## 16. Listy kontrolne

### 16.1. Przed zmianą hierarchii

- [ ] Potrafię nazwać wspólny kontrakt i jego klientów.
- [ ] Znam wszystkie kontrolowane oraz zewnętrzne podtypy.
- [ ] Sprawdziłem konstruktory, fabryki i punkty tworzenia.
- [ ] Znalazłem `super`, `instanceof`, rzutowania i `switch`.
- [ ] Znalazłem pola o tych samych nazwach w całej hierarchii.
- [ ] Sprawdziłem metody `static`, `private`, `final` i `synchronized`.
- [ ] Oceniłem granice pakietów i dostęp `protected`.
- [ ] Oceniłem generyki, erasure i bridge methods.
- [ ] Ustaliłem wymagany poziom zgodności źródłowej i binarnej.
- [ ] Sprawdziłem refleksję, adnotacje, ORM, DI, proxy i serializację.
- [ ] Mam test obejmujący najgroźniejszą obserwowalną regresję.

### 16.2. Pull Up Method i Field

- [ ] Metoda ma ten sam kontrakt we wszystkich podtypach.
- [ ] Pole ma to samo znaczenie, cykl życia i niezmienniki.
- [ ] Przenoszony kod używa tylko kontraktu dostępnego w nadklasie.
- [ ] Wybór przeciążeń pozostaje taki sam.
- [ ] `super` nadal wskazuje zamierzoną implementację.
- [ ] Nie tworzę przypadkowego override w zewnętrznej podklasie.
- [ ] Pole bazowe pozostaje prywatne, jeśli nie ma mocnego powodu do szerszego dostępu.
- [ ] Inicjalizacja nie wykonuje się w innym obserwowalnym momencie.
- [ ] Nie scalam niezależnych pól statycznych.
- [ ] Refleksja i serializacja zostały sprawdzone na właściwym poziomie.

### 16.3. Push Down Method i Field

- [ ] Członek nie należy do prawdziwego kontraktu bazowego.
- [ ] Kod nadklasy i pozostali potomkowie go nie używają.
- [ ] Klienci zostali skierowani do właściwego podtypu lub interfejsu.
- [ ] Członek trafia do najbliższej wspólnej gałęzi, nie jest kopiowany do liści.
- [ ] Przez cały czas istnieje jedno źródło stanu.
- [ ] Publiczny członek ma plan wycofania lub zmianę wersji łamiącej.
- [ ] Historyczne dane mają plan migracji.

### 16.4. Extract Superclass i Extract Subclass

- [ ] Nowy typ ma nazwę opisującą rzeczywiste pojęcie.
- [ ] Wszystkie podtypy spełniają kontrakt bazowy.
- [ ] Nie poświęcam innej istotnej nadklasy wyłącznie dla ponownego użycia kodu.
- [ ] Publiczne konstruktory i fabryki są świadomie zachowane lub migrowane.
- [ ] Konstruktor nadklasy nie wywołuje overridable zachowania na częściowo zainicjalizowanym obiekcie.
- [ ] Specjalna cecha podklasy jest stabilna w cyklu życia.
- [ ] Punkty tworzenia wybierają prawidłowy typ runtime przed Push Down.
- [ ] Uwzględniłem `final`, rekordy, enumy i `sealed`.

### 16.5. Extract Interface

- [ ] Interfejs opisuje rolę konkretnej grupy klientów.
- [ ] Zawiera minimalny spójny podzbiór operacji.
- [ ] Kontrakt obejmuje błędy, efekty, stan i właściwości operacyjne.
- [ ] Wszystkie implementacje przechodzą wspólny test kontraktowy.
- [ ] Metody implementujące są publiczne i mają zgodne sygnatury po erasure.
- [ ] Metoda `default` jest poprawna dla każdej implementacji.
- [ ] Zmiany parametrów i wyników publicznych metod mają plan zgodności binarnej.
- [ ] Interfejs nie przenosi stanu instancji ani technicznego API jednej klasy.

### 16.6. Collapse Hierarchy

- [ ] Usuwany poziom nie reprezentuje osobnego pojęcia ani kontraktu.
- [ ] Nie jest markerem, punktem rozszerzenia ani typem konfiguracji.
- [ ] Wybrałem właściwą publiczną nazwę typu docelowego.
- [ ] Rozwiązałem kolizje pól, metod i `super`.
- [ ] Zmigrowałem konstrukcję, DI, refleksję i mapowania.
- [ ] Klienci usuwanej nazwy mają warstwę zgodności albo zaplanowane wydanie łamiące.

### 16.7. Replace Inheritance with Composition

- [ ] Relacja podtypowania jest fałszywa albo nie należy do wymaganego kontraktu.
- [ ] Znam całe odziedziczone API używane przez klientów.
- [ ] Delegate ma jednoznaczną własność i cykl życia.
- [ ] Przekazuję tylko świadomie zatwierdzone operacje.
- [ ] Sprawdziłem hooki, `super`, fluent `this` i callbacki.
- [ ] Sprawdziłem monitor, transakcje i proxy.
- [ ] Zdefiniowałem `equals`, `hashCode`, iterację i serializację.
- [ ] Testuję liczbę oraz kolejność wywołań delegata, jeśli są obserwowalne.
- [ ] Utrata przypisywalności do nadklasy ma plan migracji.

### 16.8. Przed zakończeniem modułu

- [ ] Każdy przykład kompiluje się na Javie 25.
- [ ] Testy charakterystyki przechodzą.
- [ ] Testy kontraktowe obejmują wszystkie konkretne podtypy.
- [ ] Testy różnicowe mają niezależne oczekiwania.
- [ ] Program demonstracyjny uruchamia się bez dodatkowej konfiguracji.
- [ ] Nie pozostały dwa niezależne źródła tego samego stanu.
- [ ] Każdy typ hierarchii reprezentuje rozpoznawalny kontrakt.
- [ ] Zmiana łamiąca API, dane lub integrację jest nazwana i zaplanowana osobno.

## 17. Podsumowanie

Refaktoryzacje hierarchii są bezpieczne dopiero wtedy, gdy struktura typów odpowiada kontraktom, a testy obserwują właściwy poziom systemu. Pull Up centralizuje prawdziwie wspólny stan i zachowanie. Push Down usuwa z bazy cechy właściwe tylko części obiektów. Extract Superclass i Extract Subclass zmieniają model typów, więc wymagają kontroli konstrukcji oraz zastępowalności. Extract Interface wydziela rolę klienta. Collapse Hierarchy usuwa rozróżnienie bez znaczenia. Replace Inheritance with Composition ogranicza fałszywe podtypowanie i odziedziczone API.

W Javie metody instancyjne, przeciążenia i pola mają różne reguły wyboru. Do tego dochodzą konstruktory, `super`, monitory, erasure, bridge methods, pakiety i typy `sealed`. Poprawna zmiana składni nie wystarcza, jeżeli kontrakt obejmuje stare binaria, refleksję, serializację, ORM albo proxy.

Najważniejsza praktyka modułu to rozdzielenie trzech decyzji: gdzie należy stan i zachowanie, jaki typ powinien widzieć klient oraz które formy zgodności muszą zostać zachowane. Dzięki temu zmiana hierarchii nie jest jednorazowym przestawieniem `extends`, lecz kontrolowaną sekwencją z testem po każdym kroku.
