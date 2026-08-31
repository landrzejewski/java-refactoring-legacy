# Moduł 3. Zasady dobrego projektowania

## Cel modułu

Celem modułu jest zbudowanie praktycznego sposobu oceny i poprawiania projektu istniejącego kodu. Uczestnik uczy się łączyć zasady DRY, KISS, YAGNI i SOLID z konkretnymi kosztami zmian, rozpoznawać granice o wysokiej spójności, świadomie kierować zależności oraz traktować architekturę i wzorce jako narzędzia rozwiązujące potwierdzone problemy.

Moduł nie przedstawia zasad jako praw ani jako listy kontrolnej gwarantującej dobry projekt. Ta sama konstrukcja może być właściwa w jednym kontekście i kosztowna w innym. Ocenie podlegają aktualne wymagania, przewidywalne osie zmian, ryzyko, kontrakty, koszt poznawczy i możliwość bezpiecznej ewolucji.

## Efekty uczenia się

Po ukończeniu modułu uczestnik:

- odróżnia duplikację wiedzy od przypadkowego podobieństwa kodu,
- stosuje KISS bez sprowadzania prostoty do liczby linii lub klas,
- stosuje YAGNI bez zaniedbywania testów, refaktoryzacji i aktualnych wymagań jakościowych,
- rozpoznaje napięcia między DRY, KISS i YAGNI,
- interpretuje każdą zasadę SOLID przez koszt i kierunek konkretnej zmiany,
- definiuje kontrakt behawioralny potrzebny do bezpiecznej substytucji,
- odróżnia Dependency Inversion Principle od dependency injection,
- ocenia spójność i sprzężenie na poziomie metod, klas, pakietów i komponentów,
- odróżnia kierunek zależności źródłowych od przepływu sterowania w czasie wykonania,
- wyznacza granice domeny, przypadku użycia i adapterów,
- dobiera zakres Clean Architecture proporcjonalnie do ryzyka systemu,
- refaktoryzuje w kierunku wzorca dopiero po rozpoznaniu problemu, kontekstu i kosztów rozwiązania.

## Zakres

1. Jakość projektu w kontekście zmiany
2. DRY, KISS i YAGNI
3. Zasady SOLID w praktyce
4. Wysoka spójność i niskie sprzężenie
5. Clean Architecture i pokrewne podejścia inside/outside
6. Wzorce projektowe jako kierunek refaktoryzacji
7. Studium przypadku w Javie 25
8. Warsztat praktyczny

## Konwencje przykładów

Przykłady używają Javy 25, JUnit Jupiter 6.1.3 i JaCoCo 0.8.15. Kod domenowy jest celowo uproszczony. Nie stanowi kompletnego modelu logistyki, rozliczeń, trwałości danych ani dostarczania wiadomości.

Kod znajduje się w projekcie Maven `refactoring-legacy`. Bazowym pakietem jest `pl.training`, a przykłady tego modułu należą do pakietu głównego `pl.training.module3` i jego podpakietów:

| Pakiet | Odpowiedzialność |
| --- | --- |
| `pl.training.module3.legacy` | zastana wersja łącząca kilka powodów zmian |
| `pl.training.module3.domain` | pojęcia i reguły wyceny dostawy |
| `pl.training.module3.application` | przypadek użycia oraz potrzebne mu porty |
| `pl.training.module3.adapter` | implementacje zależne od mechanizmu zapisu i wyjścia |
| `pl.training.module3` | zewnętrzne miejsce składania grafu obiektów i uruchamiania demonstracji |

Podział pakietów ma uwidocznić kierunek zależności. Sam układ katalogów nie egzekwuje architektury. W większym systemie granice można dodatkowo chronić widocznością typów, osobnymi modułami Maven, modułami JPMS lub automatycznymi testami reguł architektonicznych.

Klasa z przedrostkiem `Legacy` i wersja rozdzielona mają różne nazwy wyłącznie po to, aby oba etapy mogły współistnieć w jednym, kompilowalnym projekcie. W rzeczywistej refaktoryzacji byłyby kolejnymi stanami tego samego obszaru kodu.

Na potrzeby studium zakładamy, że zastana klasa jest wewnętrzna, a wszystkie jej wywołania pozostają pod kontrolą zespołu. Dzięki temu migracja klientów z `createQuote(...)` do `execute(Command)` może być osobnym, bezpiecznym krokiem. Jeżeli stare API jest publiczne albo ma niezależnych konsumentów, sama zmiana sygnatur nie jest refaktoryzacją zachowującą kontrakt. Należy wtedy utrzymać fasadę ze starą sygnaturą, delegującą do nowego przypadku użycia, i migrować konsumentów oddzielnie przed usunięciem fasady.

Kompilacja, testy i uruchomienie:

```shell
cd refactoring-legacy
mvn clean verify
java -cp target/classes pl.training.module3.Module3Examples
```

## Organizacja pracy

Sugerowany czas pracy synchronicznej wynosi 210 minut:

| Część | Czas |
| --- | ---: |
| teoria wraz z krótkimi aktywnościami | 70 minut |
| ćwiczenie 1 i omówienie | 30 minut |
| ćwiczenie 2 i omówienie | 35 minut |
| ćwiczenie 3 i omówienie | 35 minut |
| ćwiczenie 4 i omówienie | 30 minut |
| sprawdzenie wiedzy i podsumowanie | 10 minut |

Na zajęciach obowiązkowe są właściwe znaczenie DRY, KISS i YAGNI, pięć zasad SOLID, relacja między spójnością a sprzężeniem, reguła zależności Clean Architecture oraz kryteria zastosowania wzorca. Rozbudowane odpowiedzi, antywzorce i listy kontrolne mogą służyć jako materiał do samodzielnej pracy.

## 1. Jakość projektu w kontekście zmiany

### 1.1. Dobry projekt nie jest cechą absolutną

Projekt oprogramowania organizuje wiedzę, odpowiedzialności i zależności. Jego jakość ujawnia się przede wszystkim podczas zmiany. Dobry projekt pozwala znaleźć właściwe miejsce modyfikacji, ogranicza jej promień oddziaływania i szybko dostarcza wiarygodnej informacji zwrotnej.

Ocena zależy od kontekstu:

- aktualnych reguł i ograniczeń domenowych,
- rodzaju oraz częstotliwości zmian,
- krytyczności błędów,
- wymaganej wydajności, bezpieczeństwa i niezawodności,
- struktury zespołów i odpowiedzialności organizacyjnej,
- oczekiwanego czasu życia rozwiązania,
- kosztu wdrażania i wycofywania zmian.

Kod może być lokalnie elegancki, a jednocześnie utrudniać najczęstsze zmiany systemu. Inny fragment może wyglądać proceduralnie, ale poprawnie modelować mały i zamknięty problem. Zasada projektowa ma pomóc postawić pytanie, a nie zakończyć analizę.

### 1.2. Zasady są heurystykami

| Zasada | Pytanie diagnostyczne | Typowe ryzyko nadinterpretacji |
| --- | --- | --- |
| DRY | Gdzie znajduje się autorytatywna reprezentacja tej wiedzy? | łączenie niezależnych reguł tylko dlatego, że wyglądają podobnie |
| KISS | Czy istnieje mniej złożone poprawne rozwiązanie aktualnego problemu? | pomijanie koniecznych przypadków, walidacji i błędów |
| YAGNI | Jakie aktualne wymaganie uzasadnia ten element? | zaniedbanie zdrowia kodu i potwierdzonych wymagań jakościowych |
| SOLID | Jak ten podział ogranicza koszt konkretnej zmiany? | mechaniczne tworzenie klas i interfejsów |
| Clean Architecture | Czy polityka biznesowa zależy źródłowo od szczegółu technicznego? | kopiowanie pełnego szablonu warstw do każdego systemu |
| wzorzec projektowy | Jaki powtarzalny problem i jakie siły równoważy wzorzec? | implementowanie katalogu wzorców bez rzeczywistej potrzeby |

Jeżeli decyzji nie da się powiązać z wymaganiem, ryzykiem albo kierunkiem zmiany, sama nazwa zasady nie jest wystarczającym uzasadnieniem.

### 1.3. Projekt obejmuje więcej niż klasy

Na koszt zmiany wpływają także:

- schematy danych i formaty komunikatów,
- granice transakcji,
- modele błędów i ponowień,
- zależności czasu wykonania i wdrożenia,
- konfiguracja, generowanie kodu i proces budowania,
- publiczne API i zgodność danych historycznych,
- kolejność operacji oraz wymagania czasowe,
- właściciele modułów i sposób współpracy zespołów.

Dodanie interfejsu nie naprawi niejasnej transakcji. Rozdzielenie klasy nie usunie sprzężenia przez wspólną tabelę. Zmiana nazwy pakietu nie utworzy granicy, jeżeli wszystkie moduły nadal odwołują się do szczegółów infrastruktury.

### 1.4. Dowód wartości zmiany projektu

Przed refaktoryzacją warto opisać jeden lub dwa scenariusze:

1. Co ma zostać zmienione?
2. Które pliki, moduły i zespoły trzeba dziś zaangażować?
3. Jakie ryzyka i testy uruchamia zmiana?
4. Jaki podział zmniejszy ten koszt?
5. Jaki nowy koszt wprowadzi proponowana abstrakcja?
6. Jak sprawdzimy, że cel został osiągnięty?

Taki scenariusz jest lepszym uzasadnieniem niż stwierdzenie, że kod nie jest wystarczająco zgodny z SOLID.

## 2. DRY, KISS i YAGNI

### 2.1. DRY dotyczy wiedzy

DRY wymaga jednej, jednoznacznej i autorytatywnej reprezentacji danej porcji wiedzy w systemie. Nie jest zakazem występowania podobnych linii.

Wiedzą może być:

- reguła podatkowa lub cenowa,
- definicja formatu komunikatu,
- sposób wyliczenia statusu,
- ograniczenie walidacyjne,
- mapowanie pojęcia domenowego,
- konfiguracja procesu wdrożenia,
- opis kontraktu, z którego generowane są inne artefakty.

Podstawowe pytanie brzmi:

> Czy zmiana jednej decyzji wymaga znalezienia i zgodnej aktualizacji kilku miejsc?

Jeżeli odpowiedź jest twierdząca, istnieje kandydat na duplikację wiedzy.

### 2.2. Podobieństwo nie wystarcza

| Sytuacja | Ocena |
| --- | --- |
| Walidacja wieku i liczby produktów używa dziś warunku `value > 0` | podobny kod, ale niezależna wiedza i inni właściciele zmian |
| Stawka opłaty paliwowej jest powtórzona w dwóch wariantach tej samej usługi | jedna potwierdzona reguła, kandydat do centralizacji |
| Dwa konteksty biznesowe mają dziś rabat 10 procent | podobieństwo może być przypadkowe; najpierw trzeba ustalić relację reguł |
| Cache powiela dane źródłowe | kontrolowana duplikacja wymagająca właściciela i reguły synchronizacji |
| Klient API oraz dokumentacja są generowane z jednego kontraktu | kilka artefaktów, lecz jedno źródło wiedzy |

Usunięcie duplikacji tekstowej przez wspólną metodę może utworzyć fałszywą zależność. Gdy dwie reguły mają innych właścicieli i zmieniają się niezależnie, wspólna abstrakcja zmusza je do nieuzasadnionej koordynacji.

Jeżeli fałszywa abstrakcja już istnieje, najpierw trzeba zabezpieczyć jej obserwowalne zachowanie testami. Następnie można przenieść kod z abstrakcji z powrotem do poszczególnych kontekstów, usunąć niepotrzebne gałęzie w każdym z nich i dopiero wtedy wydzielić wyłącznie potwierdzoną wspólną wiedzę. Tymczasowe powtórzenie kodu może być bezpiecznym etapem rozdzielania błędnie połączonych reguł.

### 2.3. DRY w testach

Test może współdzielić czytelne fabryki danych i stabilne przygotowanie środowiska. Nie powinien jednak obliczać oczekiwanej wartości tym samym algorytmem co kod produkcyjny. Taka centralizacja usuwa niezależność wyroczni testowej i może sprawić, że ten sam defekt wystąpi po obu stronach asercji.

Powtórzenie kilku jawnych wartości w testach bywa tańsze niż ogólny kreator scenariuszy z wieloma flagami. Czytelność pojedynczego przypadku jest aktualnym wymaganiem testu.

### 2.4. KISS: prostota po poprawności

Operacyjna interpretacja KISS brzmi:

> Preferuj najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania i ograniczenia.

Prostota nie oznacza automatycznie:

- najmniejszej liczby linii,
- najmniejszej liczby klas,
- pierwszego rozwiązania, które udało się uruchomić,
- technologii najlepiej znanej autorowi,
- braku abstrakcji,
- pominięcia obsługi błędów, bezpieczeństwa, transakcji lub wydajności.

Rozdzielenie splątanych odpowiedzialności na kilka nazwanych elementów może zwiększyć liczbę plików, a jednocześnie zmniejszyć złożoność poznawczą i promień zmian.

### 2.5. Złożoność istotna i wprowadzona

Nie każdą złożoność można usunąć. Reguły prawne, współbieżność, nieodwracalne efekty, rozliczenia finansowe i wymagania odporności mogą być rzeczywiście trudne. KISS kieruje uwagę przede wszystkim na złożoność wprowadzoną przez rozwiązanie:

- pośrednie warstwy bez odrębnej odpowiedzialności,
- konfigurację zastępującą czytelny kod,
- mechanizm pluginów bez istniejących rozszerzeń,
- generyczny silnik reguł dla dwóch prostych przypadków,
- hierarchię typów bez kontraktu substytucji,
- ukryty przepływ sterowania oparty na refleksji lub globalnym rejestrze.

Skomplikowany problem może wymagać złożonego rozwiązania. Wtedy celem jest nazwanie i izolowanie trudności, a nie jej pozorne usunięcie.

### 2.6. YAGNI: aktualna potrzeba przed przewidywaną

YAGNI zaleca, aby nie budować dziś zdolności potrzebnej wyłącznie dla przewidywanego przyszłego wymagania. Dotyczy to zarówno funkcji widocznych dla użytkownika, jak i spekulacyjnej elastyczności:

- parametrów bez aktualnego wariantu,
- fabryk tworzących jeden typ,
- interfejsów utworzonych wyłącznie dla hipotetycznych implementacji, które nie wyrażają aktualnej granicy ani roli klienta,
- obsługi przyszłych formatów,
- systemów pluginów i dynamicznego ładowania,
- pól oraz metod, których nikt jeszcze nie używa.

Przedwczesny element powoduje kilka kosztów:

1. Trzeba go zaprojektować, zaimplementować i przetestować.
2. Opóźnia aktualną wartość.
3. Zwiększa bieżący koszt rozumienia i zmian.
4. Może wymagać przebudowy, gdy rzeczywista potrzeba okaże się inna.

### 2.7. Czego YAGNI nie zabrania

YAGNI nie jest uzasadnieniem dla:

- pomijania refaktoryzacji upraszczającej aktualny kod,
- rezygnacji z automatycznych testów i ciągłej integracji,
- ignorowania aktualnych wymagań bezpieczeństwa i zgodności,
- odkładania potwierdzonej migracji danych,
- ignorowania znanego limitu wydajnościowego,
- usuwania kodu używanego przez refleksję, konfigurację lub zewnętrznych klientów bez sprawdzenia.

Eksperyment techniczny ograniczony czasowo może być aktualną potrzebą, jeżeli rozwiązuje niewiadomą blokującą decyzję. Nie musi być częścią kodu produkcyjnego.

YAGNI działa dobrze, gdy system jest podatny na zmianę. Testy, małe kroki i refaktoryzacja umożliwiają odroczenie decyzji bez tworzenia niebezpiecznej bariery na przyszłość.

### 2.8. Napięcia między zasadami

| Decyzja | DRY | KISS | YAGNI |
| --- | --- | --- | --- |
| nazwanie jednej potwierdzonej reguły opłaty | jedno źródło wiedzy | usuwa konieczność synchronizacji | realizuje aktualną regułę |
| ogólny silnik dla hipotetycznych taryf | brak korzyści DRY bez potwierdzonej wspólnej wiedzy | zwiększa liczbę pojęć i trybów | buduje przewidywaną elastyczność |
| dwie lokalne, podobne reguły niezależnych działów | brak wspólnej wiedzy | lokalność może być prostsza | brak spekulacyjnej abstrakcji |
| wspólna biblioteka dla niezależnych usług | brak wspólnej wiedzy, jeśli usługi zmieniają reguły niezależnie | może zwiększyć koszt nawigacji | narusza YAGNI tylko wtedy, gdy bibliotekę utworzono dla hipotetycznego współdzielenia |

Reguła trzech może pomóc poczekać na lepsze zrozumienie kształtu abstrakcji. Nie jest definicją DRY. Dwie reprezentacje tej samej potwierdzonej reguły mogą wymagać natychmiastowej centralizacji, a wiele podobnych fragmentów niezależnej wiedzy może pozostać osobno.

### 2.9. Filtr decyzyjny

Przed dodaniem abstrakcji odpowiedz:

1. Jakie aktualne wymaganie lub ryzyko ją uzasadnia?
2. Jaką konkretną wiedzę reprezentują podobne miejsca?
3. Czy powinny zmieniać się z tego samego powodu i na żądanie tego samego właściciela?
4. Czy abstrakcja ma nazwę w języku problemu?
5. Czy upraszcza aktualny przypadek, czy tylko przyszły scenariusz?
6. Ile nowych trybów, warunków i zależności wprowadza?
7. Jak łatwo można ją później zmienić lub usunąć?

## 3. Zasady SOLID w praktyce

### 3.1. SOLID jako narzędzie analizy zmian

SOLID jest zbiorem heurystyk dotyczących odpowiedzialności, rozszerzalności, kontraktów podtypów, interfejsów klientów i kierunku zależności. Nie jest systemem punktowym ani nakazem maksymalizacji polimorfizmu.

| Zasada | Główna troska |
| --- | --- |
| SRP | grupowanie elementów zmieniających się dla tego samego aktora |
| OCP | ochrona stabilnej części przed wybraną osią rozszerzeń |
| LSP | zachowanie kontraktu przy podstawieniu implementacji |
| ISP | ograniczenie zależności klienta do potrzebnej roli |
| DIP | kierowanie zależności źródłowych od szczegółów ku polityce i abstrakcjom |

### 3.2. SRP: jeden aktor zmiany

Popularne zdanie o jednym powodzie do zmiany wymaga doprecyzowania. Powód oznacza odpowiedzialność wobec aktora lub spójnej grupy interesariuszy, a nie każdą możliwą edycję pliku.

Serwis tworzący ofertę może zawierać:

- regułę ceny należącą do działu finansowego,
- zapis należący do zespołu odpowiedzialnego za dane,
- format wiadomości należący do komunikacji z klientem.

Wszystkie operacje uczestniczą w jednym przebiegu, ale zmieniają się z innych przyczyn. Przypadek użycia może je koordynować, natomiast nie powinien przejmować szczegółów każdej polityki.

SRP nie oznacza jednej metody na klasę, limitu linii ani zakazu koordynacji. Dotyczy metod, klas, pakietów i większych komponentów.

Pytania:

- Kto żąda tej zmiany?
- Które elementy zwykle zmieniają się razem?
- Czy jedna zmiana domenowa wymaga edycji kodu infrastruktury?
- Czy jeden moduł jest polem konfliktu niezależnych zespołów?

### 3.3. OCP: zamknięcie dla wybranej osi

Open-Closed Principle oznacza możliwość rozszerzenia wybranego zachowania bez modyfikowania stabilnej części. Nie można zamknąć systemu na wszystkie przyszłe zmiany.

Przykładowe osie:

- otwarty zbiór polityk wyceny,
- otwarty zbiór adapterów komunikacyjnych,
- zamknięty zbiór stanów protokołu,
- stabilny algorytm z wymiennym źródłem danych.

Dodanie implementacji może nadal wymagać zmiany composition root, konfiguracji lub rejestru. OCP nie jest zakazem edytowania plików. Chroni te miejsca, których stabilność ma wartość.

Nie każdy `switch` narusza OCP. Wyrażenie `switch` może jasno modelować niewielki, celowo zamknięty zbiór wariantów. Polimorfizm jest wartościowy, gdy zbiór rozszerza się niezależnie, a centralne rozgałęzienia powodują powtarzalne modyfikacje.

### 3.4. LSP: substytucja behawioralna

Liskov Substitution Principle dotyczy zachowania, nie tylko zgodności sygnatur. Kod pracujący z typem bazowym powinien móc użyć każdego prawidłowego podtypu bez specjalnej wiedzy o jego implementacji.

Praktyczny kontrakt obejmuje:

- dopuszczalne dane wejściowe,
- gwarancje wyniku,
- inwarianty,
- obserwowalne wyjątki,
- efekty uboczne i protokół wywołań,
- mutowalność, kolejność, idempotencję lub bezpieczeństwo wątkowe, jeżeli są obiecane.

Podtyp nie powinien wzmacniać warunków wstępnych. Powinien przyjąć co najmniej dane dopuszczane przez typ bazowy. Nie powinien osłabiać warunków końcowych. Może dostarczyć silniejszą gwarancję, ale nie mniejszą.

Kompilator Javy weryfikuje część zgodności typów i wyjątków kontrolowanych. Nie sprawdza kontraktów domenowych, inwariantów ani znaczenia wyniku.

Test kontraktowy uruchamiany dla wszystkich implementacji dostarcza dowodów substytucyjności, ale jej formalnie nie dowodzi. Testuje wybrane przykłady, nie całą przestrzeń stanów.

Wyjątek `UnsupportedOperationException` nie jest automatycznie naruszeniem LSP. Niektóre interfejsy Javy jawnie opisują operacje opcjonalne. Ocena zawsze zaczyna się od rzeczywistego kontraktu.

### 3.5. ISP: interfejs z perspektywy klienta

Interface Segregation Principle mówi, że klient nie powinien zależeć od metod, których nie potrzebuje. Interfejsy należy projektować według ról klientów i ich powodów zmian.

ISP nie wymaga jednej metody w każdym interfejsie. Jednometodowy port może być właściwy, gdy reprezentuje jedną spójną rolę. Szeroki interfejs może być właściwy, gdy jego operacje tworzą nierozdzielny kontrakt klienta.

Typowe naruszenie to ogólny `CrudRepository<T>` przekazany przypadkowi użycia, który potrzebuje wyłącznie jednej operacji. Klient staje się źródłowo zależny od metod zapisu, usuwania i wyszukiwania, mimo że ich nie używa.

### 3.6. DIP: kierunek zależności, nie sposób wstrzykiwania

Dependency Inversion Principle kieruje zależności źródłowe od szczegółów technicznych ku stabilniejszej polityce i abstrakcjom. Moduł wysokiego poziomu nie powinien importować konkretnego klienta bazy, biblioteki HTTP ani modelu frameworka, gdy potrzebuje jedynie określonej informacji lub efektu.

DIP nie jest synonimem dependency injection:

- wstrzyknięcie konkretnej klasy może nadal pozostawić zależność od szczegółu,
- DIP można zastosować bez kontenera IoC,
- ręczne utworzenie grafu w composition root jest poprawne,
- tworzenie obiektów wartości i kolekcji przez `new` nie jest naruszeniem,
- interfejs dla każdej klasy nie jest wymagany.

Port powinien być nazwany przez potrzebę klienta. Przypadek użycia potrzebujący zapisu oferty definiuje `QuoteRepository`, a adapter bazodanowy implementuje ten kontrakt. Ogólny port należący do infrastruktury często odwraca odpowiedzialność tylko pozornie.

### 3.7. Zależności między zasadami

- SRP pomaga znaleźć niezależne osie zmian.
- OCP chroni wybraną oś przez stabilny kontrakt.
- LSP zapewnia, że nowe implementacje nie psują tego kontraktu.
- ISP ogranicza kontrakt do roli potrzebnej klientowi.
- DIP ustawia zależności implementacji w stronę kontraktu i polityki.

Łatwość utworzenia mocka nie dowodzi zgodności z SOLID. Może jedynie wskazywać, że zależność ma punkt podmiany. Jakość granicy zależy od znaczenia kontraktu i kosztu zmian.

### 3.8. Błędne uproszczenia

| Uproszczenie | Korekta |
| --- | --- |
| SRP oznacza, że klasa robi jedną rzecz | odpowiedzialność wynika z aktora i powodu zmiany |
| każdy `switch` łamie OCP | zamknięty zbiór wariantów może być poprawnym modelem |
| dziedziczenie typów gwarantuje LSP | potrzebna jest zgodność behawioralna |
| ISP oznacza jedną metodę na interfejs | interfejs ma odpowiadać spójnej roli klienta |
| DIP oznacza użycie konstruktora lub `@Autowired` | chodzi o kierunek zależności źródłowej |
| SOLID wymaga interfejsu dla każdej klasy | abstrakcja musi chronić znaczącą granicę lub zmienność |

## 4. Wysoka spójność i niskie sprzężenie

### 4.1. Spójność

Spójność opisuje, jak silnie elementy modułu współpracują na rzecz wspólnego celu. Modułem może być metoda, klasa, pakiet, komponent lub usługa.

Wysoka spójność zwykle oznacza, że:

- moduł da się opisać jednym zdaniem domenowym,
- elementy zmieniają się z tego samego powodu,
- potrzebują podobnego kontekstu i danych,
- testy modułu skupiają się na jednym obszarze zachowania,
- nazwa modułu wyjaśnia jego rolę bez ogólnych słów typu `Manager` lub `Utils`.

Mała klasa nie musi być spójna. Duża klasa może reprezentować bogaty, nierozdzielny koncept. Liczba metod jest sygnałem, nie definicją.

### 4.2. Sprzężenie

Sprzężenie opisuje zależność między elementami. Najbardziej praktyczna perspektywa pyta, czy zmiana jednego modułu wymusza edycję, ponowne testowanie, wdrożenie lub koordynację drugiego.

Sprzężenie może dotyczyć:

- typów i importów w kodzie źródłowym,
- formatu oraz znaczenia danych,
- kolejności wywołań,
- czasu i dostępności,
- wspólnego stanu lub transakcji,
- konkretnej technologii,
- wspólnego procesu wdrażania,
- wiedzy rozproszonej pomiędzy zespołami.

Liczba zależności nie wystarcza. Jedna zależność od niestabilnego, szerokiego kontraktu może kosztować więcej niż kilka zależności od małych i stabilnych pojęć domenowych.

### 4.3. Sprzężenia nie da się usunąć

Współpracujące elementy muszą być w pewien sposób powiązane. Celem jest:

- umieszczenie silnego sprzężenia wewnątrz spójnej granicy,
- ograniczenie sprzężenia przekraczającego granice,
- uczynienie kontraktów jawnymi,
- skierowanie zależności ku stabilniejszym pojęciom,
- unikanie cykli zależności,
- kontrolowanie protokołów czasu wykonania.

Interfejs nie usuwa sprzężenia. Zastępuje zależność od implementacji zależnością od kontraktu i przenosi decyzję o wyborze implementacji do innego miejsca.

### 4.4. Koszt wydzielenia

Extract Class może zwiększyć spójność, ale również:

- dodać współpracownika,
- wydłużyć nawigację,
- utworzyć nowy kontrakt,
- wymagać koordynacji cyklu życia obiektów,
- rozdzielić dane, które muszą być zmieniane atomowo.

Ocena powinna porównać koszt przyszłej zmiany przed i po wydzieleniu. Maksymalna liczba małych klas nie jest celem.

### 4.5. Granica oparta na ukrywanej decyzji

Dobry moduł ukrywa decyzję projektową, która jest trudna albo prawdopodobnie się zmieni. Zamiast dzielić system wyłącznie według kolejnych etapów przetwarzania, warto izolować:

- politykę cenową,
- sposób trwałego zapisu,
- format zewnętrznego komunikatu,
- wybór algorytmu,
- szczegóły biblioteki lub protokołu.

Zmiana ukrytej decyzji powinna mieć lokalny zasięg. Konsument zna potrzebną operację i jej semantykę, a nie reprezentację szczegółu.

### 4.6. Diagnostyka

| Pytanie | Sygnał problemu |
| --- | --- |
| Ile miejsc trzeba zmienić dla jednej reguły? | rozproszona wiedza lub shotgun surgery |
| Czy moduł ma kilku niezależnych właścicieli? | niska spójność odpowiedzialności |
| Czy import ujawnia typ frameworka w domenie? | sprzężenie technologiczne przez granicę |
| Czy wywołania muszą nastąpić w ukrytej kolejności? | sprzężenie protokołu lub czasu |
| Czy zmiana adaptera wymaga zmiany przypadku użycia? | nieprawidłowy kierunek zależności |
| Czy dwa moduły importują się wzajemnie? | cykl ograniczający niezależną zmianę |
| Czy dane przekraczające granicę zawierają zbędne pola? | sprzężenie klienta z szerokim modelem |

Historia wspólnych zmian w repozytorium może dostarczyć dowodu sprzężenia, ale wymaga interpretacji. Pliki mogą zmieniać się razem z powodów procesowych, migracyjnych lub przypadkowego formatowania.

## 5. Clean Architecture

### 5.1. Cel

Clean Architecture organizuje granice i zależności źródłowe tak, aby kod opisujący politykę biznesową nie zależał od szczegółów technologicznych.

Najważniejsza jest reguła zależności:

> Zależności w kodzie źródłowym przekraczające granicę powinny wskazywać w stronę polityki wyższego poziomu, a nie w stronę zewnętrznego mechanizmu.

Kod przypadku użycia nie powinien importować typu `ResultSet`, encji ORM, żądania HTTP ani modelu konkretnego dostawcy wiadomości. Adapter tłumaczy te typy na model wygodny dla wnętrza.

### 5.2. Kierunek zależności a przepływ sterowania

Przypadek użycia może w czasie wykonania wywołać bazę danych lub bramkę komunikacyjną. Nie oznacza to, że musi zależeć źródłowo od ich klas.

Przykładowy układ:

1. Aplikacja definiuje port `QuoteRepository` potrzebny przypadkowi użycia.
2. Przypadek użycia wywołuje ten port.
3. Adapter bazodanowy implementuje port i importuje pakiet aplikacyjny.
4. Composition root tworzy adapter i przekazuje go do przypadku użycia.

Przepływ sterowania prowadzi od przypadku użycia ku adapterowi. Zależność źródłowa adaptera prowadzi do portu wewnętrznego. To praktyczne zastosowanie DIP.

### 5.3. Kręgi nie są obowiązkowym szablonem

Popularny diagram z encjami, przypadkami użycia, adapterami i frameworkami jest schematem. System może mieć mniej albo więcej granic. Istotne jest położenie polityki i kierunek zależności, a nie nazwy czterech folderów.

Architektura heksagonalna, Onion Architecture i Clean Architecture należą do rodziny podejść oddzielających wnętrze od zewnętrznych mechanizmów. Używają różnych pojęć i akcentów, więc nie są dokładnymi synonimami.

Clean Architecture nie wymaga mikroserwisów, DDD, rozbudowanego modelu obiektowego ani frameworka DI. Może działać wewnątrz modularnego monolitu.

### 5.4. Elementy praktyczne

| Element | Odpowiedzialność |
| --- | --- |
| domena | stabilne pojęcia, inwarianty i reguły problemu |
| przypadek użycia | orkiestracja jednego celu użytkownika lub systemu |
| port wejściowy | opcjonalna stabilna granica wywołania przypadku użycia |
| port wyjściowy | potrzeba wnętrza wobec świata zewnętrznego |
| adapter wejściowy | mapowanie żądania zewnętrznego na dane przypadku użycia |
| adapter wyjściowy | realizacja portu za pomocą bazy, pliku, HTTP lub brokera |
| composition root | wybór konkretów i składanie grafu zależności |

Interfejs portu powinien należeć do strony, która formułuje potrzebę. Nie musi być ogólnym odbiciem wszystkich możliwości bazy lub dostawcy.

### 5.5. Dane przekraczające granicę

Wnętrze nie powinno przyjmować modelu wygodnego wyłącznie dla zewnętrznego frameworka. Granicę mogą przekraczać:

- proste rekordy wejściowe i wynikowe,
- obiekty wartości domeny,
- jawne argumenty metod,
- typy błędów należące do kontraktu przypadku użycia.

Mapowanie ma koszt, ale chroni wnętrze przed zmianą schematu zewnętrznego. Nie należy kopiować każdego obiektu mechanicznie. Osobny model jest uzasadniony wtedy, gdy granica naprawdę oddziela różne semantyki lub tempo zmian.

### 5.6. Composition root

Zewnętrzne miejsce uruchomienia może znać zarówno porty, jak i konkretne adaptery. Jego zadaniem jest wybór implementacji, konfiguracja i utworzenie grafu obiektów. Nie zawiera reguł biznesowych.

Ręczne użycie konstruktorów jest pełnoprawnym composition root. Kontener zależności może automatyzować składanie, ale nie tworzy poprawnego kierunku zależności samym faktem użycia.

### 5.7. Koszty i ograniczenia

Granice mogą wprowadzać:

- dodatkowe porty i modele danych,
- mapowanie,
- większą liczbę plików,
- koszt nawigacji,
- potrzebę testów kontraktowych i integracyjnych,
- złożoność konfiguracji composition root.

Mały CRUD, krótko żyjący prototyp lub stabilny skrypt może nie uzasadniać pełnego podziału. Granica jest wartościowa, gdy chroni istotną politykę, rozdziela różne tempo zmian, umożliwia potrzebny test albo izoluje ryzykowny mechanizm.

Niezależność od bazy nie oznacza, że każdą bazę można wymienić bez kosztu. Systemy różnią się transakcjami, spójnością, modelem zapytań i wydajnością. Adapter ogranicza propagację szczegółów, ale nie usuwa realnych właściwości technologii.

## 6. Wzorce projektowe jako kierunek refaktoryzacji

### 6.1. Wzorzec opisuje decyzję w kontekście

Wzorzec projektowy nazywa powtarzalny problem, kontekst, siły, strukturę rozwiązania oraz konsekwencje. Nie jest fragmentem kodu do skopiowania ani celem samym w sobie.

Przed użyciem wzorca trzeba znać:

- występujący problem,
- aktualne warianty i właścicieli zmian,
- kontrakt wymagający stabilizacji,
- koszt dodatkowego poziomu pośrednictwa,
- alternatywę prostszą,
- warunek, w którym wzorzec przestanie być potrzebny.

### 6.2. Refaktoryzacja w kierunku wzorca

Bezpieczny przebieg:

1. Zabezpiecz obserwowalne zachowanie.
2. Nazwij konkretną oś zmienności.
3. Wykonaj małe refaktoryzacje odsłaniające role.
4. Wprowadź najwęższy potrzebny kontrakt.
5. Przenieś jeden wariant za granicę.
6. Uruchom testy zachowania i kontraktu.
7. Przenieś pozostałe warianty.
8. Zatrzymaj się, gdy aktualny problem został rozwiązany.

Wzorzec może wyłonić się z kolejnych transformacji. Nie trzeba projektować jego pełnej, najbardziej ogólnej postaci na początku.

### 6.3. Strategy

Strategy jest przydatna, gdy istnieje rodzina wymiennych algorytmów o wspólnym kontrakcie, wybieranych niezależnie od klienta. Zbiór algorytmów może być otwarty albo celowo zamknięty. Jest to osobna decyzja dotycząca OCP. Kosztem są dodatkowy kontrakt, implementacje i mechanizm wyboru.

Czytelny `switch` może być lepszy, gdy:

- zbiór wariantów jest mały i celowo zamknięty,
- wszystkie warianty zmieniają się razem,
- logika jest lokalna,
- nie ma potrzeby niezależnego wdrażania lub konfigurowania algorytmów.

W projekcie warsztatowym `DeliveryPricePolicy` jest uzasadniona dwoma istniejącymi politykami wyceny. Nie powstaje mechanizm dynamicznych pluginów, ponieważ nie ma takiego wymagania.

### 6.4. Adapter

Adapter tłumaczy niezgodny interfejs lub model zewnętrzny na kontrakt potrzebny klientowi. Jest uzasadniony, gdy oddziela rzeczywistą granicę technologii lub semantyki.

Adapter nie powinien być pustą klasą przekazującą każde wywołanie jeden do jednego bez ochrony jakiejkolwiek decyzji. Wartość może polegać na:

- mapowaniu formatów,
- tłumaczeniu błędów,
- ukryciu protokołu,
- kontroli ponowień i czasu,
- ochronie modelu domenowego przed typami dostawcy.

### 6.5. Wzorzec można usunąć

Jeżeli warianty zniknęły albo zmienność okazała się inna, wcześniejsza abstrakcja może stać się zbędna. Refaktoryzacja od wzorca jest równie poprawna jak refaktoryzacja do wzorca.

Sygnały nadmiaru:

- jedna implementacja od długiego czasu,
- interfejs powtarzający dokładnie publiczne API jednej klasy,
- fabryka zawierająca jeden konstruktor,
- konfiguracja trudniejsza od jawnego utworzenia obiektu,
- większość implementacji rzuca `UnsupportedOperationException`,
- dodanie wariantu nadal wymaga zmian w wielu centralnych miejscach.

## 7. Studium przypadku: wycena dostawy

### 7.1. Punkt wyjścia i zachowanie do ochrony

Przykład dotyczy utworzenia wyceny dostawy. Zastany serwis:

- oblicza cenę dla dwóch metod dostawy,
- zapisuje utworzoną ofertę we własnej kolekcji,
- wypisuje powiadomienie na standardowe wyjście,
- zwraca wynik klientowi.

Kod wersji zastanej znajduje się w pliku `LegacyDeliveryQuoteService.java`:

```java
package pl.training.module3.legacy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

public final class LegacyDeliveryQuoteService {
    private final List<DeliveryQuote> storedQuotes = new ArrayList<>();

    public DeliveryQuote createQuote(
            String customerEmail,
            ShippingMethod method,
            Parcel parcel) {
        BigDecimal price = switch (method) {
            case STANDARD -> {
                BigDecimal base = new BigDecimal("10.00")
                        .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
                yield money(base.add(
                        base.multiply(new BigDecimal("0.08"))));
            }
            case EXPRESS -> {
                BigDecimal base = new BigDecimal("20.00")
                        .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
                yield money(base.add(
                        base.multiply(new BigDecimal("0.08"))));
            }
        };

        DeliveryQuote quote = new DeliveryQuote(
                customerEmail,
                method,
                parcel,
                price);
        storedQuotes.add(quote);

        System.out.printf(
                "Quote ready for %s: %s costs %s%n",
                customerEmail,
                method,
                price);
        return quote;
    }

    public List<DeliveryQuote> storedQuotes() {
        return List.copyOf(storedQuotes);
    }

    private static BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }
}
```

Sam `switch` nie jest głównym problemem. Istotne są zebrane w jednym miejscu niezależne decyzje:

| Obserwacja | Konsekwencja projektowa |
| --- | --- |
| stawka opłaty paliwowej `0.08` występuje w obu gałęziach | ta sama reguła ma dwie reprezentacje i może się rozjechać |
| szczegóły dwóch polityk cenowych są częścią serwisu | dodanie lub zmiana wariantu modyfikuje koordynację przypadku użycia |
| zapis jest kolekcją należącą do serwisu | przypadek użycia zna konkretny mechanizm przechowywania |
| powiadomienie wywołuje bezpośrednio `System.out` | nie można zmienić kanału bez edycji serwisu |
| obliczanie, zapis i komunikacja mają innych właścicieli zmian | klasa ma niską spójność względem zmian |
| kolejność zapis, powiadomienie, zwrot jest ukryta w implementacji | protokół efektów ubocznych wymaga jawnego kontraktu i testu |

Pierwszym krokiem nie jest dodanie wzorca, lecz zapisanie znanego zachowania:

```java
package pl.training.module3.legacy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

final class LegacyDeliveryQuoteServiceCharacterizationTest {
    @Test
    void documentsStandardDeliveryPriceAndStorage() {
        LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();
        Parcel parcel = new Parcel(new BigDecimal("3.00"));

        DeliveryQuote quote = service.createQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel);

        assertEquals(new BigDecimal("17.28"), quote.price());
        assertEquals(List.of(quote), service.storedQuotes());
    }

    @Test
    void documentsExpressDeliveryPrice() {
        LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();

        DeliveryQuote quote = service.createQuote(
                "developer@example.com",
                ShippingMethod.EXPRESS,
                new Parcel(new BigDecimal("3.00")));

        assertEquals(new BigDecimal("31.32"), quote.price());
    }
}
```

Test chroni ceny i zapis, ale nie przechwytuje treści wypisywanej na konsolę. Jest to jawna luka w charakterystyce, nie dowód braku takiego zachowania. W prawdziwym systemie przed zmianą trzeba ustalić, czy treść, liczba powiadomień i ich kolejność są częścią kontraktu.

### 7.2. Model i inwarianty domeny

Typy wejściowe i wynikowe utrzymują podstawowe inwarianty:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Parcel(BigDecimal weightKg) {
    public Parcel {
        Objects.requireNonNull(weightKg, "weightKg");

        if (weightKg.signum() <= 0) {
            throw new IllegalArgumentException("Weight must be positive");
        }
    }
}
```

```java
package pl.training.module3.domain;

public enum ShippingMethod {
    STANDARD,
    EXPRESS
}
```

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record DeliveryQuote(
        String customerEmail,
        ShippingMethod method,
        Parcel parcel,
        BigDecimal price) {
    public DeliveryQuote {
        Objects.requireNonNull(customerEmail, "customerEmail");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(parcel, "parcel");
        Objects.requireNonNull(price, "price");

        if (customerEmail.isBlank()) {
            throw new IllegalArgumentException("Customer email must not be blank");
        }
        if (price.signum() < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }

        price = price.setScale(2, RoundingMode.UNNECESSARY);
    }
}
```

`record` ogranicza kod techniczny reprezentacji danych, ale sam nie tworzy dobrego modelu. Znaczenie nadal wynika z nazw i reguł konstruktora. W tym przykładzie:

- masa musi być dodatnia,
- adres klienta nie może być pusty,
- cena nie może być ujemna,
- cena musi mieć dokładnie dwa miejsca dziesiętne bez niejawnego zaokrąglenia w konstruktorze wyniku.

Walidacja adresu sprawdza jedynie niepustość. Pełna walidacja i normalizacja adresu e-mail nie należy do zakresu przykładu. `BigDecimal` został utworzony z tekstu, dzięki czemu wartości dziesiętne nie dziedziczą przybliżenia binarnego typu `double`.

### 7.3. DRY zastosowane do reguły opłaty

W obu wariantach rzeczywiście powtarza się jedna decyzja: stawka opłaty paliwowej i sposób jej doliczenia. Otrzymuje ona nazwę oraz jednego właściciela:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class FuelSurcharge {
    private final BigDecimal rate;

    public FuelSurcharge(BigDecimal rate) {
        this.rate = Objects.requireNonNull(rate, "rate");

        if (rate.signum() < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Fuel surcharge rate must be between zero and one");
        }
    }

    public BigDecimal addTo(BigDecimal baseAmount) {
        Objects.requireNonNull(baseAmount, "baseAmount");

        if (baseAmount.signum() < 0) {
            throw new IllegalArgumentException("Base amount must not be negative");
        }

        return baseAmount
                .add(baseAmount.multiply(rate))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
```

Nie wydzielono natomiast wspólnej metody `basePrice(base, rate, weight)`. Formuły wariantów są obecnie podobne, lecz reprezentują odrębne cenniki. Mogą zmieniać się niezależnie. Wspólna funkcja wymuszałaby sztuczne sprzężenie parametrów i utrudniała nazwanie przyszłych reguł właściwym językiem domenowym.

Ważna decyzja modelowa jest jawna: stawka od `0` do `1` oznacza od 0 do 100 procent. Jeżeli biznes dopuszczałby opłatę większą niż 100 procent albo ujemną korektę, inwariant wymaga zmiany. Nie wynika on z typu `BigDecimal`.

### 7.4. Strategy dla istniejących wariantów

Wspólny kontrakt opisuje metodę dostawy oraz obliczenie ceny:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;

public interface DeliveryPricePolicy {
    /**
     * Returns the stable, non-null shipping method handled by this policy.
     */
    ShippingMethod method();

    /**
     * Returns a deterministic, non-negative amount with scale two for every
     * valid parcel, without changing the parcel or producing side effects.
     */
    BigDecimal priceFor(Parcel parcel);
}
```

Kontrakt dokumentuje zachowanie wspólne dla implementacji: identyfikator obsługiwanej metody jest stabilny i niepusty, wynik ceny jest deterministyczny, nieujemny, ma skalę dwa, a obliczenie nie zmienia przesyłki i nie wywołuje efektów ubocznych. Same typy zwracane tego nie wyrażają.

Pierwsza polityka:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class StandardDeliveryPricePolicy implements DeliveryPricePolicy {
    private static final BigDecimal BASE_PRICE = new BigDecimal("10.00");
    private static final BigDecimal PRICE_PER_KG = new BigDecimal("2.00");

    private final FuelSurcharge fuelSurcharge;

    public StandardDeliveryPricePolicy(FuelSurcharge fuelSurcharge) {
        this.fuelSurcharge = Objects.requireNonNull(fuelSurcharge);
    }

    @Override
    public ShippingMethod method() {
        return ShippingMethod.STANDARD;
    }

    @Override
    public BigDecimal priceFor(Parcel parcel) {
        BigDecimal baseAmount = BASE_PRICE.add(
                parcel.weightKg().multiply(PRICE_PER_KG));
        return fuelSurcharge.addTo(baseAmount);
    }
}
```

Druga polityka:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExpressDeliveryPricePolicy implements DeliveryPricePolicy {
    private static final BigDecimal BASE_PRICE = new BigDecimal("20.00");
    private static final BigDecimal PRICE_PER_KG = new BigDecimal("3.00");

    private final FuelSurcharge fuelSurcharge;

    public ExpressDeliveryPricePolicy(FuelSurcharge fuelSurcharge) {
        this.fuelSurcharge = Objects.requireNonNull(fuelSurcharge);
    }

    @Override
    public ShippingMethod method() {
        return ShippingMethod.EXPRESS;
    }

    @Override
    public BigDecimal priceFor(Parcel parcel) {
        BigDecimal baseAmount = BASE_PRICE.add(
                parcel.weightKg().multiply(PRICE_PER_KG));
        return fuelSurcharge.addTo(baseAmount);
    }
}
```

`Strategy` ma tu potwierdzone uzasadnienie: istnieją dwa algorytmy, oba realizują ten sam kontrakt i są wybierane według metody dostawy. Rozwiązanie nie obsługuje dynamicznego odkrywania klas, zewnętrznych pluginów ani konfiguracji wyrażeń. Te możliwości nie wynikają z aktualnych wymagań.

`ShippingMethod` pozostaje typem wyliczeniowym. Dodanie metody dostawy wymaga więc zmiany tego typu oraz composition root. Zamknięta na zmianę jest logika wyboru w kalkulatorze, a nie cały system. Jest to świadomy i ograniczony zakres OCP.

### 7.5. Wybór polityki i kontrola konfiguracji

Kalkulator indeksuje strategie raz podczas konstrukcji i odrzuca konfigurację niejednoznaczną:

```java
package pl.training.module3.domain;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class DeliveryPriceCalculator {
    private final Map<ShippingMethod, DeliveryPricePolicy> policies;

    public DeliveryPriceCalculator(
            Collection<? extends DeliveryPricePolicy> policies) {
        Objects.requireNonNull(policies, "policies");

        if (policies.isEmpty()) {
            throw new IllegalArgumentException("At least one policy is required");
        }

        EnumMap<ShippingMethod, DeliveryPricePolicy> indexedPolicies =
                new EnumMap<>(ShippingMethod.class);
        for (DeliveryPricePolicy policy : policies) {
            Objects.requireNonNull(policy, "policy");
            ShippingMethod method = Objects.requireNonNull(
                    policy.method(),
                    "policy.method()");
            DeliveryPricePolicy previous = indexedPolicies.putIfAbsent(
                    method,
                    policy);

            if (previous != null) {
                throw new IllegalArgumentException(
                        "Duplicate policy for method: " + method);
            }
        }
        this.policies = Map.copyOf(indexedPolicies);
    }

    public BigDecimal priceFor(ShippingMethod method, Parcel parcel) {
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(parcel, "parcel");

        DeliveryPricePolicy policy = policies.get(method);
        if (policy == null) {
            throw new IllegalArgumentException(
                    "No pricing policy for method: " + method);
        }
        return policy.priceFor(parcel);
    }
}
```

`Map.copyOf` tworzy niemodyfikowalną kopię mapy, dlatego późniejsza zmiana kolekcji przekazanej do konstruktora nie zmienia konfiguracji kalkulatora. Brak polityki jest wykrywany przy żądaniu danego wariantu. Alternatywą byłoby wymaganie kompletu wszystkich wartości `ShippingMethod` już w konstruktorze. Właściwy wybór zależy od tego, czy częściowa konfiguracja jest legalna w systemie.

Przypadek użycia zależy bezpośrednio od konkretnej klasy `DeliveryPriceCalculator`. Nie utworzono dla niej interfejsu, ponieważ jest częścią tej samej wewnętrznej polityki, a przykład nie ma drugiej semantycznie poprawnej implementacji kalkulatora. DIP nie wymaga interfejsu przed każdą klasą.

### 7.6. Test kontraktowy i LSP

Każda strategia przechodzi wspólny zestaw sprawdzeń:

```java
package pl.training.module3.domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

final class DeliveryPricePolicyContractTest {
    @ParameterizedTest(name = "{0}")
    @MethodSource("policies")
    void everyPolicyObeysTheSubstitutionContract(
            String description,
            ShippingMethod expectedMethod,
            DeliveryPricePolicy policy) {
        Parcel parcel = new Parcel(new BigDecimal("100.00"));

        ShippingMethod firstMethod = policy.method();
        ShippingMethod secondMethod = policy.method();
        BigDecimal firstResult = policy.priceFor(parcel);
        BigDecimal secondResult = policy.priceFor(parcel);

        assertAll(
                () -> assertEquals(expectedMethod, firstMethod),
                () -> assertEquals(firstMethod, secondMethod),
                () -> assertTrue(firstResult.signum() >= 0),
                () -> assertEquals(2, firstResult.scale()),
                () -> assertEquals(firstResult, secondResult));
    }

    private static Stream<Arguments> policies() {
        FuelSurcharge surcharge = new FuelSurcharge(new BigDecimal("0.08"));
        return Stream.of(
                Arguments.of(
                        "standard policy",
                        ShippingMethod.STANDARD,
                        new StandardDeliveryPricePolicy(surcharge)),
                Arguments.of(
                        "express policy",
                        ShippingMethod.EXPRESS,
                        new ExpressDeliveryPricePolicy(surcharge)));
    }
}
```

Test daje wspólne, wykonywalne dowody dla wybranych elementów kontraktu. Nie dowodzi LSP dla wszystkich wartości i stanów. Przykładowo nie sprawdza maksymalnej masy, polityki zaokrąglania przy każdym ułamku ani wydajności. Zakres testu musi ewoluować wraz z publicznymi gwarancjami.

### 7.7. Porty definiowane przez potrzeby przypadku użycia

Przypadek użycia potrzebuje zapisać ofertę i poinformować o jej utworzeniu. Definiuje dwa wąskie porty wyjściowe:

```java
package pl.training.module3.application;

import pl.training.module3.domain.DeliveryQuote;

@FunctionalInterface
public interface QuoteRepository {
    void save(DeliveryQuote quote);
}
```

```java
package pl.training.module3.application;

import pl.training.module3.domain.DeliveryQuote;

@FunctionalInterface
public interface QuoteNotifier {
    void quoteCreated(DeliveryQuote quote);
}
```

Interfejsy mają po jednej metodzie, ponieważ przypadek użycia potrzebuje dwóch odrębnych ról. Nie jest to mechaniczne zastosowanie ISP. Gdyby jedna spójna operacja wymagała od repozytorium zarówno odczytu, jak i zapisu w ramach jednego kontraktu, dwie metody mogłyby być właściwe.

Port `QuoteRepository` nie dziedziczy ogólnego `CrudRepository<DeliveryQuote>`. Przypadek użycia nie zależy dzięki temu od usuwania, stronicowania ani wyszukiwania, których nie używa. Nazwa portu opisuje potrzebę wnętrza, nie technologię adaptera.

### 7.8. Przypadek użycia jako jawna orkiestracja

`CreateDeliveryQuote` oblicza wynik, zapisuje go, powiadamia i zwraca:

```java
package pl.training.module3.application;

import java.util.Objects;

import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;

public final class CreateDeliveryQuote {
    private final DeliveryPriceCalculator priceCalculator;
    private final QuoteRepository repository;
    private final QuoteNotifier notifier;

    public CreateDeliveryQuote(
            DeliveryPriceCalculator priceCalculator,
            QuoteRepository repository,
            QuoteNotifier notifier) {
        this.priceCalculator = Objects.requireNonNull(priceCalculator);
        this.repository = Objects.requireNonNull(repository);
        this.notifier = Objects.requireNonNull(notifier);
    }

    public DeliveryQuote execute(Command command) {
        Objects.requireNonNull(command, "command");

        DeliveryQuote quote = new DeliveryQuote(
                command.customerEmail(),
                command.method(),
                command.parcel(),
                priceCalculator.priceFor(command.method(), command.parcel()));

        repository.save(quote);
        notifier.quoteCreated(quote);
        return quote;
    }

    public record Command(
            String customerEmail,
            ShippingMethod method,
            Parcel parcel) {
        public Command {
            Objects.requireNonNull(customerEmail, "customerEmail");
            Objects.requireNonNull(method, "method");
            Objects.requireNonNull(parcel, "parcel");
        }
    }
}
```

Klasa ma wysoką spójność na poziomie celu aplikacyjnego: koordynuje utworzenie jednej wyceny. Nie zawiera wzoru ceny, kodu zapisu ani formatowania komunikatu. Wstrzyknięcie zależności odbywa się ręcznie przez konstruktor.

Kolejność `save`, a następnie `quoteCreated`, jest decyzją protokołu. Gdy zapis kończy się wyjątkiem, powiadomienie nie jest wywoływane. Nie wynika to z SOLID ani Clean Architecture. Musi odpowiadać wymaganiu biznesowemu. W systemie rozproszonym samo ustawienie kolejności nie gwarantuje atomowości. Jeżeli zapis powiedzie się, a wysłanie komunikatu zawiedzie, potrzebna może być jawna strategia ponowień, idempotencja albo wzorzec transactional outbox. Przykład celowo nie implementuje tych mechanizmów.

Test przypadku użycia dokumentuje zarówno przebieg udany, jak i wybrane zachowanie przy błędzie:

```java
package pl.training.module3.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import pl.training.module3.application.CreateDeliveryQuote.Command;
import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.FuelSurcharge;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;
import pl.training.module3.domain.StandardDeliveryPricePolicy;

final class CreateDeliveryQuoteTest {
    @Test
    void calculatesStoresAndNotifiesAboutQuote() {
        List<DeliveryQuote> savedQuotes = new ArrayList<>();
        List<DeliveryQuote> notifications = new ArrayList<>();
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator(),
                savedQuotes::add,
                notifications::add);

        DeliveryQuote result = useCase.execute(new Command(
                "developer@example.com",
                ShippingMethod.STANDARD,
                new Parcel(new BigDecimal("3.00"))));

        assertEquals(new BigDecimal("17.28"), result.price());
        assertEquals(List.of(result), savedQuotes);
        assertEquals(List.of(result), notifications);
    }

    @Test
    void doesNotNotifyWhenSavingFails() {
        List<DeliveryQuote> notifications = new ArrayList<>();
        QuoteRepository failingRepository = quote -> {
            throw new IllegalStateException("Storage unavailable");
        };
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator(),
                failingRepository,
                notifications::add);

        assertThrows(
                IllegalStateException.class,
                () -> useCase.execute(new Command(
                        "developer@example.com",
                        ShippingMethod.STANDARD,
                        new Parcel(new BigDecimal("3.00")))));
        assertTrue(notifications.isEmpty());
    }

    private static DeliveryPriceCalculator calculator() {
        FuelSurcharge surcharge = new FuelSurcharge(new BigDecimal("0.08"));
        return new DeliveryPriceCalculator(List.of(
                new StandardDeliveryPricePolicy(surcharge)));
    }
}
```

Lambdy w teście są prostymi implementacjami portów i zapisują wywołania do list. Nie udają zachowania bazy ani dostawcy wiadomości. Test weryfikuje politykę przypadku użycia, natomiast rzeczywiste adaptery potrzebują osobnych testów integracyjnych.

### 7.9. Adaptery na zewnętrznej stronie granicy

Demonstracyjne repozytorium zapisuje dane w pamięci:

```java
package pl.training.module3.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pl.training.module3.application.QuoteRepository;
import pl.training.module3.domain.DeliveryQuote;

public final class InMemoryQuoteRepository implements QuoteRepository {
    private final List<DeliveryQuote> quotes = new ArrayList<>();

    @Override
    public void save(DeliveryQuote quote) {
        quotes.add(Objects.requireNonNull(quote));
    }

    public List<DeliveryQuote> quotes() {
        return List.copyOf(quotes);
    }
}
```

Adapter powiadomienia wypisuje komunikat:

```java
package pl.training.module3.adapter;

import java.util.Objects;

import pl.training.module3.application.QuoteNotifier;
import pl.training.module3.domain.DeliveryQuote;

public final class ConsoleQuoteNotifier implements QuoteNotifier {
    @Override
    public void quoteCreated(DeliveryQuote quote) {
        Objects.requireNonNull(quote);

        System.out.printf(
                "Quote ready for %s: %s costs %s%n",
                quote.customerEmail(),
                quote.method(),
                quote.price());
    }
}
```

Oba adaptery importują porty aplikacji. Aplikacja nie importuje klas z pakietu `adapter`. W czasie wykonania przypadek użycia wywołuje jednak obiekty adapterów przez porty. Kierunek sterowania i kierunek zależności źródłowej są różne.

`InMemoryQuoteRepository` jest działającym adapterem demonstracyjnym, a nie substytutem testu prawdziwej trwałości. Nie zapewnia bezpieczeństwa wątkowego, transakcji ani przetrwania restartu procesu. Adapter JDBC lub JPA powinien mapować model, błędy i transakcje oraz zostać sprawdzony z rzeczywistą bazą zgodną ze środowiskiem produkcyjnym.

### 7.10. Composition root i uruchomienie

Zewnętrzna klasa zna konkretne adaptery oraz składa graf obiektów:

```java
package pl.training.module3;

import java.math.BigDecimal;
import java.util.List;

import pl.training.module3.adapter.ConsoleQuoteNotifier;
import pl.training.module3.adapter.InMemoryQuoteRepository;
import pl.training.module3.application.CreateDeliveryQuote;
import pl.training.module3.application.CreateDeliveryQuote.Command;
import pl.training.module3.domain.DeliveryPriceCalculator;
import pl.training.module3.domain.DeliveryQuote;
import pl.training.module3.domain.ExpressDeliveryPricePolicy;
import pl.training.module3.domain.FuelSurcharge;
import pl.training.module3.domain.Parcel;
import pl.training.module3.domain.ShippingMethod;
import pl.training.module3.domain.StandardDeliveryPricePolicy;
import pl.training.module3.legacy.LegacyDeliveryQuoteService;

public final class Module3Examples {
    private Module3Examples() {
    }

    public static void main(String[] args) {
        Parcel parcel = new Parcel(new BigDecimal("3.00"));
        LegacyDeliveryQuoteService legacy = new LegacyDeliveryQuoteService();
        DeliveryQuote legacyQuote = legacy.createQuote(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel);

        FuelSurcharge fuelSurcharge = new FuelSurcharge(
                new BigDecimal("0.08"));
        DeliveryPriceCalculator calculator = new DeliveryPriceCalculator(
                List.of(
                        new StandardDeliveryPricePolicy(fuelSurcharge),
                        new ExpressDeliveryPricePolicy(fuelSurcharge)));
        InMemoryQuoteRepository repository = new InMemoryQuoteRepository();
        CreateDeliveryQuote useCase = new CreateDeliveryQuote(
                calculator,
                repository,
                new ConsoleQuoteNotifier());

        DeliveryQuote refactoredQuote = useCase.execute(new Command(
                "developer@example.com",
                ShippingMethod.STANDARD,
                parcel));

        System.out.println(
                "Legacy and refactored prices equal: "
                        + legacyQuote.price().equals(refactoredQuote.price()));
        System.out.println("Stored quotes: " + repository.quotes().size());
    }
}
```

Klasa `Module3Examples` nie należy do domeny ani aplikacji. Jej zadaniem jest wyłącznie konfiguracja i uruchomienie. W aplikacji opartej na frameworku podobną rolę może pełnić kod startowy i konfiguracja kontenera, ale nadal warto utrzymywać decyzje składania poza przypadkami użycia.

Program powinien wypisać:

```text
Quote ready for developer@example.com: STANDARD costs 17.28
Quote ready for developer@example.com: STANDARD costs 17.28
Legacy and refactored prices equal: true
Stored quotes: 1
```

Pierwsze powiadomienie pochodzi z wersji zastanej, a drugie z adaptera nowej wersji.

### 7.11. Kierunek zależności po zmianie

```text
Module3Examples
  -> legacy
       -> domain model
  -> adapter
       -> application ports
       -> domain model
  -> application
       -> domain
  -> domain
```

Dozwolone zależności w przykładzie:

| Kod źródłowy | Może zależeć od | Nie powinien zależeć od |
| --- | --- | --- |
| domena | Java SE i własne pojęcia domenowe | aplikacja, adaptery, frameworki wejścia i trwałości |
| aplikacja | domena i porty należące do aplikacji | konkretne adaptery, konsola, baza danych |
| adapter | porty aplikacji, domena, użyta technologia | composition root; inne adaptery, jeśli powoduje to cykl albo ominięcie przypadku użycia i jego portów |
| composition root | wszystkie elementy potrzebne do złożenia | reguły biznesowe umieszczone w kodzie konfiguracji |

Gałąź `legacy` istnieje wyłącznie po to, aby uruchomić oba etapy demonstracji. Nie jest częścią architektury docelowej. Jest to reguła zależności na poziomie pakietów i importów, nie pełna weryfikacja architektury. Wszystkie klasy nadal są w jednym module Maven i procesie. Niezależność kompilacji lub wdrożenia wymagałaby silniejszej granicy.

### 7.12. Zasady widoczne w rozwiązaniu

| Decyzja | Zasada | Konkretna korzyść | Wprowadzony koszt |
| --- | --- | --- | --- |
| `FuelSurcharge` | DRY, wysoka spójność | jedna reprezentacja stawki i zaokrąglenia | dodatkowy typ domenowy |
| dwie polityki ceny | SRP, OCP, Strategy | lokalne warianty i brak centralnego rozgałęzienia przy obliczeniu | kontrakt, rejestracja i więcej plików |
| test kontraktowy | LSP | wspólne sprawdzenie jawnych gwarancji | lista implementacji musi być utrzymywana |
| `QuoteRepository` i `QuoteNotifier` | ISP, DIP | przypadek użycia zna tylko potrzebne role | porty i składanie implementacji |
| `CreateDeliveryQuote` | wysoka spójność | jawna orkiestracja jednego celu | nadal trzeba zdecydować o błędach i atomowości |
| adaptery | Clean Architecture, Adapter | szczegóły efektów pozostają na zewnątrz | mapowanie i testy integracyjne w realnym systemie |
| ręczny composition root | KISS, YAGNI | widoczny graf bez kontenera | ręczna aktualizacja konfiguracji |

Refaktoryzacja zwiększyła liczbę klas. Jej wartością nie jest mniejsza liczba linii, lecz rozdzielenie niezależnych decyzji i skierowanie zależności ku polityce. Dla programu, który nigdy nie dostanie drugiej metody dostawy ani prawdziwego adaptera, taki podział mógłby być nadmierny. W przykładzie dwa warianty i dwa efekty zewnętrzne są już obecne.

## 8. Warsztat praktyczny

Ćwiczenia korzystają z kodu w `pl.training.module3`. Przed rozpoczęciem uruchom:

```shell
mvn clean verify
```

Po każdym małym kroku ponownie uruchom odpowiedni test, a przed zakończeniem całe `mvn verify`. Nie zmieniaj jednocześnie zachowania biznesowego i struktury, chyba że zadanie wyraźnie tego wymaga.

### 8.1. Ćwiczenie 1: DRY, KISS i YAGNI

**Czas:** 20 minut pracy i 10 minut omówienia.

#### Cel

Odróżnienie wspólnej wiedzy od podobnego tekstu oraz ocena kosztu abstrakcji.

#### Sytuacje

Dla każdej sytuacji zdecyduj: scalić teraz, pozostawić lokalnie albo zebrać więcej informacji.

1. W politykach STANDARD i EXPRESS występuje stawka opłaty paliwowej 8 procent oraz identyczne zaokrąglenie.
2. W dwóch niezależnych kontekstach `MaximumParcelWeight` i `MaximumWarehouseLoad` wynosi dziś 1000 kg.
3. Test polityki STANDARD i test polityki EXPRESS zawierają jawnie wartość oczekiwaną dla przesyłki 3 kg.
4. Zespół proponuje refleksyjny system pluginów, ponieważ za rok mogą pojawić się taryfy definiowane przez partnerów.
5. Produkt ma zatwierdzone wymaganie: za trzy tygodnie zostanie uruchomiona trzecia metoda dostawy z innym algorytmem.
6. Cache wycen powiela dane obliczone przez serwis cenowy, aby spełnić zmierzony limit czasu odpowiedzi.

#### Zadanie

Dla każdej sytuacji zapisz:

- reprezentowaną wiedzę,
- właściciela i powód zmiany,
- aktualne wymaganie lub ryzyko,
- najprostsze poprawne rozwiązanie,
- koszt błędnej centralizacji albo błędnego pozostawienia duplikacji,
- sygnał, po którym decyzję trzeba ponownie ocenić.

Następnie przeanalizuj `FuelSurcharge` i odpowiedz, czy warto wydzielić dodatkowy interfejs `Surcharge`. Uzasadnienie musi odwoływać się do aktualnego wariantu lub granicy, nie do samej możliwości utworzenia interfejsu.

#### Kryteria ukończenia

- decyzja odwołuje się do wiedzy, a nie liczby podobnych linii,
- KISS jest oceniane po spełnieniu aktualnych wymagań,
- przyszłe wymaganie jest odróżnione od zatwierdzonego wymagania,
- kontrolowana duplikacja ma właściciela, strategię synchronizacji i test inwariantu,
- uczestnik potrafi wskazać koszt wybranej opcji.

### 8.2. Ćwiczenie 2: SOLID jako seria bezpiecznych zmian

**Czas:** 25 minut pracy i 10 minut omówienia.

#### Cel

Refaktoryzacja `LegacyDeliveryQuoteService` z zachowaniem cen oraz jawnego protokołu efektów.

#### Przygotowanie

Pracuj na kopii klasy lub cofnij się do wersji `legacy`. Istniejące testy charakterystyki są punktem wyjścia, nie kompletną specyfikacją.

#### Zadanie

1. Wskaż aktorów zmiany dla wyceny, zapisu i powiadomień.
2. Ustal z właścicielem procesu, czy po nieudanym zapisie wolno wysłać powiadomienie, i zapisz decyzję jako wymaganie. Zastana klasa nie ma punktu podmiany pozwalającego wiarygodnie zasymulować taką awarię.
3. Wydziel regułę opłaty paliwowej bez łączenia niezależnych cenników.
4. Wydziel kontrakt polityki cenowej i przenieś jeden wariant.
5. Uruchom testy przed przeniesieniem drugiego wariantu.
6. Dodaj test kontraktowy uruchamiany dla każdej polityki.
7. Wydziel porty zapisu i powiadamiania z perspektywy przypadku użycia.
8. Po utworzeniu punktu podmiany dopisz test chroniący uzgodnione zachowanie przy awarii zapisu.
9. Przenieś konkretne efekty do adapterów.
10. Złóż rozwiązanie ręcznie poza przypadkiem użycia.
11. Porównaj wynik programu przed i po zmianie.

Po każdym kroku zanotuj, którą oś zmiany chroni konstrukcja oraz jaki koszt dodaje. Nie przypisuj jednej klasy do jednej zasady. Jedna decyzja może wspierać kilka właściwości projektu.

#### Macierz kontraktu LSP

Uzupełnij tabelę dla `DeliveryPricePolicy`:

| Element kontraktu | Gwarancja typu bazowego | Jak sprawdzić | Przykład naruszenia |
| --- | --- | --- | --- |
| obsługiwana metoda |  |  |  |
| dane wejściowe |  |  |  |
| wynik |  |  |  |
| zaokrąglenie |  |  |  |
| deterministyczność |  |  |  |
| efekty uboczne |  |  |  |
| wyjątki |  |  |  |

#### Kryteria ukończenia

- ceny STANDARD i EXPRESS pozostają odpowiednio `17.28` oraz `31.32` dla 3 kg,
- przypadek użycia nie importuje adapterów,
- stawka opłaty paliwowej ma jedną reprezentację,
- każdy wariant przechodzi wspólny test kontraktowy,
- awaria zapisu nie prowadzi do powiadomienia,
- `mvn verify` kończy się powodzeniem.

### 8.3. Ćwiczenie 3: granica i reguła zależności

**Czas:** 25 minut pracy i 10 minut omówienia.

#### Cel

Odróżnienie przepływu sterowania od zależności źródłowych i zaprojektowanie minimalnej granicy inside/outside.

#### Zadanie A: klasyfikacja zależności

Oceń każdą zależność jako dozwoloną, podejrzaną albo niedozwoloną w przyjętym modelu. Uzasadnij odpowiedź:

1. `domain` importuje `java.math.BigDecimal`.
2. `application` importuje `adapter.InMemoryQuoteRepository`.
3. `adapter` implementuje `application.QuoteRepository`.
4. `domain` przyjmuje encję oznaczoną adnotacjami ORM.
5. `Module3Examples` tworzy adaptery i przypadek użycia.
6. `ConsoleQuoteNotifier` importuje `DeliveryQuote`.
7. `CreateDeliveryQuote` w czasie wykonania wywołuje obiekt adaptera przez `QuoteNotifier`.
8. Port aplikacji zwraca wyjątek konkretnego sterownika JDBC.

#### Zadanie B: nowy adapter

Załóż aktualne wymaganie: powiadomienie ma być zapisane w buforze w pamięci, aby test demonstracyjny mógł sprawdzić jego treść bez przechwytywania `System.out`.

1. Dodaj `BufferingQuoteNotifier` w pakiecie `adapter`.
2. Zaimplementuj istniejący `QuoteNotifier`, bez zmiany portu.
3. Udostępnij niemodyfikowalny widok lub kopię zgromadzonych komunikatów.
4. Dodaj test adaptera.
5. Podmień adapter wyłącznie w composition root i potwierdź, że aplikacja oraz domena nie wymagają zmian.

Nie dodawaj frameworka DI ani pliku konfiguracyjnego. Aktualne wymaganie tego nie potrzebuje.

#### Zadanie C: automatyczna ochrona granicy

Zaproponuj prostą kontrolę, która wykryje import z `pl.training.module3.adapter` w pakiecie `application` albo `domain`. Możesz użyć:

- reguły narzędzia do testów architektury,
- osobnych modułów Maven,
- JPMS,
- prostej kontroli statycznej w potoku CI.

Porównaj siłę gwarancji, koszt utrzymania i jakość komunikatu o błędzie. Nie implementuj wszystkich wariantów.

#### Kryteria ukończenia

- nowy adapter można wybrać bez edycji przypadku użycia,
- adapter zależy od portu, a nie odwrotnie,
- kontrola obejmuje co najmniej zakaz importu adapterów do wnętrza,
- uczestnik potrafi narysować osobno zależność źródłową i wywołanie w czasie wykonania,
- pełny build pozostaje zielony.

### 8.4. Ćwiczenie 4: wzorzec jako decyzja odwracalna

**Czas:** 20 minut pracy i 10 minut omówienia.

#### Cel

Dobór Strategy i Adapter do potwierdzonego problemu oraz rozpoznanie momentu, w którym wzorzec należy uprościć.

#### Scenariusz 1: dostawa tego samego dnia

Pojawia się zatwierdzona metoda SAME_DAY. Cena pochodzi z zewnętrznego klienta przewoźnika, który:

- przyjmuje masę w gramach jako `long`,
- zwraca kwotę w najmniejszych jednostkach, ale wstępny opis nie podaje waluty ani wykładnika tych jednostek,
- sygnalizuje brak oferty własnym typem błędu,
- może wykonać operację sieciową.

Zaprojektuj zmianę, odpowiadając:

1. Czy SAME_DAY jest kolejną lokalną `DeliveryPricePolicy`, adapterem zewnętrznego klienta, czy współpracą obu ról?
2. Gdzie następuje konwersja kilogramów na gramy i co zrobić z ułamkiem grama oraz przepełnieniem typu `long`?
3. Jakiej informacji potrzeba do konwersji najmniejszych jednostek na `BigDecimal` i czy model wyniku musi zawierać walutę?
4. Jaki błąd powinien przekroczyć granicę do aplikacji?
5. Czy kontrakt `DeliveryPricePolicy` nadal może obiecywać brak efektów ubocznych?
6. Gdzie ustawić timeout i politykę ponowień?
7. Jak sprawdzić mapowanie bez wywoływania prawdziwej usługi w testach domenowych?

Nie dopasowuj zewnętrznego klienta na siłę do kontraktu, którego obietnic nie potrafi spełnić. Jeżeli wycena sieciowa zmienia semantykę, potrzebna może być inna granica przypadku użycia, a nie trzeci wariant tej samej strategii.

#### Scenariusz 2: uproszczenie

Po roku produkt usuwa EXPRESS, a STANDARD zostaje jedyną stałą regułą. Nie ma planu dodawania innych wariantów.

Oceń:

- czy `DeliveryPricePolicy` nadal chroni rzeczywistą zmienność,
- czy rejestr strategii ma wartość,
- czy prostszy `DeliveryPriceCalculator` może wchłonąć jedyną regułę,
- które testy kontraktowe należy zachować jako testy zachowania,
- ile miejsc zmieniłoby się przy ponownym dodaniu wariantu.

Wykonaj uproszczenie tylko wtedy, gdy koszt bieżącej abstrakcji przewyższa koszt prawdopodobnej zmiany. Zielone testy powinny umożliwić refaktoryzację od wzorca.

#### Kryteria ukończenia

- uczestnik nazywa problem rozwiązany przez każdy wzorzec,
- kontrakt nie ukrywa operacji sieciowej jako czystego obliczenia,
- typy zewnętrznego klienta nie przechodzą do domeny,
- decyzja o zachowaniu lub usunięciu Strategy ma uzasadnienie kosztowe,
- rozwiązanie nie zawiera mechanizmu pluginów bez wymagania.

## 9. Omówienie ćwiczeń

### 9.1. Odpowiedzi do ćwiczenia 1

| Sytuacja | Zalecana decyzja | Uzasadnienie |
| --- | --- | --- |
| wspólna opłata paliwowa | scalić teraz | to jedna reguła stosowana do dwóch istniejących wariantów; zmiana stawki musi być spójna |
| dwa limity po 1000 kg | pozostawić lokalnie | podobna wartość nie dowodzi wspólnej wiedzy; limity mają inne znaczenie i mogą mieć innych właścicieli |
| oczekiwane ceny w testach | pozostawić jawnie | test nie powinien wyliczać oczekiwanego wyniku algorytmem produkcyjnym; niezależność wyroczni ma większą wartość |
| hipotetyczny system pluginów | nie budować | brak aktualnej potrzeby, a koszt obejmuje odkrywanie, bezpieczeństwo, wersjonowanie i diagnostykę |
| zatwierdzony trzeci wariant | przygotować najwęższe rozszerzenie | potrzeba jest aktualna i znana; nadal nie uzasadnia mechanizmu pluginów, jeśli wariant jest kompilowany razem z aplikacją |
| cache dla zmierzonego limitu | dopuścić kontrolowaną duplikację | wydajność jest aktualnym wymaganiem; trzeba określić źródło prawdy, ważność, unieważnianie i zachowanie po błędzie |

Interfejs `Surcharge` nie jest obecnie konieczny. `FuelSurcharge` jest jedną stabilną regułą domenową, a żadna granica techniczna ani drugi wariant nie wymaga podmiany. Interfejs można dodać później, gdy pojawi się rzeczywisty kontrakt wielu zachowań. Utworzenie go wyłącznie dla mockowania zwiększyłoby liczbę pojęć bez nowej semantyki.

### 9.2. Odpowiedzi do ćwiczenia 2

Przykładowe przypisanie aktorów:

| Obszar | Aktor zmiany | Właściwa odpowiedzialność |
| --- | --- | --- |
| cennik i opłata paliwowa | właściciel polityki cenowej | domena |
| zapis wyceny | właściciel danych i operacji | adapter realizujący port aplikacji |
| treść i kanał komunikatu | właściciel komunikacji z klientem | adapter powiadomień |
| kolejność utworzenia, zapisu i powiadomienia | właściciel procesu aplikacyjnego | przypadek użycia |

Przykładowe uzupełnienie kontraktu LSP:

| Element kontraktu | Gwarancja typu bazowego | Jak sprawdzić | Przykład naruszenia |
| --- | --- | --- | --- |
| obsługiwana metoda | `method()` zwraca stabilną, niepustą wartość zgodną z daną polityką | wspólny test odczytujący metodę co najmniej dwa razy | polityka EXPRESS rejestruje się jako STANDARD albo zmienia wartość w czasie |
| dane wejściowe | każda prawidłowa `Parcel` jest akceptowana | przypadki graniczne oraz testy generatywne, jeśli ryzyko je uzasadnia | EXPRESS odrzuca paczki powyżej 10 kg bez takiego ograniczenia w kontrakcie |
| wynik | cena jest nieujemna | wspólny test kontraktowy | wartość `-1.00` sygnalizująca brak ceny |
| zaokrąglenie | wynik ma skalę dwa | asercja na `scale()` i wartościach granicznych | zwrot `17.280` albo niejawnie inna reguła zaokrąglenia |
| deterministyczność | te same dane dają ten sam wynik | dwa wywołania oraz właściwe testy własności | wynik zależny od bieżącego czasu bez jawnego wejścia |
| efekty uboczne | obliczenie nie zmienia wejścia i nie wysyła komunikatów | obserwacja stanu oraz test podwójnego wywołania | zapis do bazy wewnątrz `priceFor` |
| wyjątki | prawidłowa przesyłka nie powoduje błędu implementacyjnego | testy wszystkich implementacji i przypadków brzegowych | `UnsupportedOperationException` dla legalnej masy |

Test kontraktowy jest uzupełnieniem dokumentacji, nie kompletnym formalnym dowodem. Konkretne ceny nadal wymagają testów poszczególnych polityk. Test przypadku użycia powinien chronić decyzję, że nie powiadamiamy po nieudanym zapisie. Jeśli właściciel procesu wybierze inną semantykę, należy zmienić test oraz implementację świadomie.

### 9.3. Odpowiedzi do ćwiczenia 3

| Zależność | Ocena | Uzasadnienie |
| --- | --- | --- |
| domena do `BigDecimal` | dozwolona | jest to typ Java SE przydatny do jawnego modelowania wartości dziesiętnych; nie jest szczegółem adaptera |
| aplikacja do `InMemoryQuoteRepository` | niedozwolona | wnętrze zależałoby od konkretnego mechanizmu zapisu |
| adapter do `QuoteRepository` | dozwolona | zewnętrzny szczegół implementuje port należący do wnętrza |
| domena do encji ORM | niedozwolona w przyjętym modelu | typ frameworka przenosi decyzję trwałości do polityki domenowej |
| composition root do wszystkich konkretów | dozwolona | właśnie tam podejmowana jest zewnętrzna decyzja o składaniu |
| notifier do `DeliveryQuote` | dozwolona | adapter realizuje port posługujący się modelem kontraktu; w większym systemie można mapować na osobny model granicy |
| wywołanie adaptera przez port | dozwolone | przepływ sterowania na zewnątrz nie odwraca zależności źródłowej |
| wyjątek JDBC w porcie | niedozwolony | szczegół dostawcy przecieka do kontraktu aplikacji; adapter powinien przetłumaczyć błąd na semantykę granicy |

Najprostsza kontrola tekstowa importów jest tania, ale słabo rozumie język i może generować wyniki fałszywe. Test architektoniczny daje czytelne reguły na poziomie typów. Osobne moduły Maven lub JPMS dostarczają silniejszej granicy kompilacji i widoczności, lecz zwiększają koszt budowania oraz konfiguracji. Należy dobrać siłę mechanizmu do znaczenia granicy.

### 9.4. Odpowiedzi do ćwiczenia 4

SAME_DAY nie powinien automatycznie implementować `DeliveryPricePolicy`. Aktualny kontrakt obiecuje deterministyczne obliczenie bez efektów ubocznych, podczas gdy klient sieciowy może być niedostępny, zmieniać odpowiedź i powodować opóźnienia. Udawanie zgodności naruszyłoby LSP oraz ukryło istotną właściwość operacyjną.

Rozsądny kierunek:

1. Przypadek użycia definiuje port wyceny zewnętrznej o jawnej semantyce błędu i dostępności.
2. Adapter przewoźnika wykonuje konwersję jednostek. Jeżeli kontrakt wymaga dokładności do jednego grama, `weightKg.movePointRight(3).longValueExact()` odrzuca zarówno ułamek grama, jak i przepełnienie. Zaokrąglenie można zastosować wyłącznie po uzgodnieniu kierunku i momentu zaokrąglania jako reguły biznesowej.
3. Kwota w najmniejszych jednostkach nie jest kompletna bez waluty i liczby jej miejsc dziesiętnych. Po ustaleniu wykładnika można użyć `BigDecimal.valueOf(minorUnits, fractionDigits)`. Obecny `DeliveryQuote` nie przechowuje waluty, więc system musi wymagać jednej skonfigurowanej waluty albo rozszerzyć model o wartość pieniężną zawierającą kwotę i kod waluty. Adapter musi odrzucić niespodziewaną walutę.
4. Adapter tłumaczy typ błędu dostawcy na błąd należący do kontraktu portu. Nie powinien ujawniać klas SDK we wnętrzu.
5. Timeout należy do konfiguracji klienta w adapterze. Ponowienia wymagają analizy idempotencji, budżetu czasu i charakteru błędów.
6. Domena oraz przypadek użycia nie importują typów SDK przewoźnika.
7. Test adaptera uruchamia się wobec kontrolowanego serwera zastępczego lub środowiska testowego dostawcy. Test przypadku użycia korzysta z implementacji portu zwracającej ustalone wyniki.

Jeśli zewnętrzna wycena nie spełnia kontraktu lokalnej strategii, decyzja, czy wykonać lokalne obliczenie, czy zdalne zapytanie, może należeć do przypadku użycia znajdującego się poziom wyżej. Nazwanie wszystkiego `Strategy` nie usuwa różnicy semantycznej.

Po usunięciu EXPRESS można uprościć projekt do jednej klasy kalkulatora, jeśli rejestr i interfejs nie chronią już realnej granicy. Warto zachować testy ceny, zaokrąglenia i inwariantów. Koszt ponownego wydzielenia strategii jest zwykle mały przy istniejących testach. Nie należy jednak usuwać portów zapisu i powiadamiania tylko dlatego, że zniknęła zmienność cennika. Te granice chronią inne decyzje.

## 10. Sprawdzenie wiedzy

### Pytania

1. Co jest przedmiotem DRY: tekst kodu czy wiedza?
2. Kiedy dwie identyczne funkcje nie powinny zostać połączone?
3. Dlaczego prostsze rozwiązanie nie zawsze ma mniej klas?
4. Czy YAGNI zabrania pisania testów dla aktualnego zachowania?
5. Jak SRP definiuje powód zmiany?
6. Czy każdy `switch` narusza OCP?
7. Jakie dwa rodzaje warunków kontraktu są kluczowe dla LSP?
8. Dlaczego `@Autowired` nie dowodzi zastosowania DIP?
9. Czy jednometodowy interfejs jest automatycznie zgodny z ISP?
10. Co interfejs robi ze sprzężeniem?
11. Czy przepływ sterowania z przypadku użycia do adaptera łamie regułę zależności?
12. Gdzie powinien należeć port wyjściowy?
13. Czy Clean Architecture wymaga dokładnie czterech warstw?
14. Kiedy Strategy może być gorsza od `switch`?
15. Dlaczego test kontraktowy nie dowodzi formalnie LSP?
16. Kiedy należy rozważyć usunięcie wcześniej zastosowanego wzorca?

### Odpowiedzi

1. Wiedza i decyzje systemu. Podobny tekst może reprezentować różną wiedzę.
2. Gdy mają inne znaczenie, innych właścicieli lub niezależne powody zmian.
3. Kilka spójnych, nazwanych elementów może zmniejszyć złożoność poznawczą i promień zmian mimo większej liczby plików.
4. Nie. Testy aktualnego zachowania wspierają bezpieczną zmianę i pozwalają odkładać spekulacyjne decyzje.
5. Jako odpowiedzialność wobec aktora lub spójnej grupy aktorów żądających określonej zmiany.
6. Nie. Dla małego, celowo zamkniętego zbioru wariantów `switch` może być najprostszym poprawnym modelem.
7. Podtyp nie wzmacnia warunków wstępnych i nie osłabia warunków końcowych. Musi również zachować istotne inwarianty oraz efekty kontraktu.
8. Wstrzyknięcie zależności opisuje sposób dostarczenia obiektu. DIP dotyczy kierunku zależności źródłowej i własności abstrakcji.
9. Nie. Interfejs jest zgodny z ISP, gdy odpowiada spójnej roli klienta, niezależnie od liczby metod.
10. Nie usuwa go. Zmienia zależność od konkretu na zależność od kontraktu i przenosi wybór implementacji.
11. Nie. Adapter może być wywołany w czasie wykonania, podczas gdy źródłowo zależy od portu zdefiniowanego wewnątrz.
12. Po stronie klienta, który formułuje potrzebę, zwykle w aplikacji lub domenie zależnie od znaczenia operacji.
13. Nie. Liczy się właściwe rozdzielenie polityki i szczegółu oraz kierunek zależności, a nie nazwy katalogów.
14. Gdy warianty są nieliczne, zamknięte, lokalne i zmieniają się razem, a mechanizm wyboru dodaje więcej kosztu niż chronionej zmienności.
15. Wykonuje skończony zestaw przypadków i obserwacji, a kontrakt może obejmować większą przestrzeń stanów i właściwości.
16. Gdy problem lub warianty zniknęły, a koszt abstrakcji przewyższa korzyść z chronionej zmienności.

## 11. Listy kontrolne

### 11.1. Przed wydzieleniem abstrakcji

- [ ] Potrafię nazwać reprezentowaną wiedzę.
- [ ] Znam aktualne wymaganie lub ryzyko uzasadniające zmianę.
- [ ] Ustaliłem właściciela i powód zmiany każdego podobnego fragmentu.
- [ ] Sprawdziłem prostsze rozwiązanie lokalne.
- [ ] Znam koszt nowego kontraktu, mapowania i nawigacji.
- [ ] Abstrakcja ma nazwę w języku problemu.
- [ ] Wiem, po jakim sygnale ponownie ocenię decyzję.

### 11.2. Przed zastosowaniem SOLID

- [ ] SRP odnoszę do aktorów zmiany, nie liczby metod.
- [ ] Dla OCP nazwałem konkretną oś rozszerzeń.
- [ ] Kontrakt bazowy opisuje zachowanie potrzebne do LSP.
- [ ] Wszystkie implementacje przechodzą wspólne testy kontraktowe.
- [ ] Interfejs odpowiada roli konkretnego klienta.
- [ ] Abstrakcja należy do strony formułującej potrzebę.
- [ ] Kierunek importów prowadzi ku polityce.
- [ ] Nie tworzę interfejsu wyłącznie dlatego, że istnieje klasa.

### 11.3. Przed ustanowieniem granicy architektonicznej

- [ ] Granica oddziela inne znaczenie, tempo zmian albo ryzyko technologiczne.
- [ ] Wnętrze nie importuje typów frameworka ani dostawcy.
- [ ] Dane przekraczające granicę mają jawny kontrakt.
- [ ] Błędy zewnętrzne są tłumaczone na semantykę wnętrza.
- [ ] Composition root pozostaje na zewnątrz.
- [ ] Uwzględniłem transakcje, timeouty, ponowienia i idempotencję tam, gdzie są potrzebne.
- [ ] Adapter ma test proporcjonalny do ryzyka integracji.
- [ ] Koszt mapowania i dodatkowych typów jest uzasadniony.
- [ ] Regułę zależności można sprawdzić automatycznie.

### 11.4. Przed zastosowaniem wzorca

- [ ] Potrafię opisać problem bez użycia nazwy wzorca.
- [ ] Wariant lub niezgodność istnieje obecnie.
- [ ] Znam prostszą alternatywę.
- [ ] Kontrakt nie ukrywa istotnych efektów ani błędów.
- [ ] Wzorzec ogranicza promień realnej zmiany.
- [ ] Testy chronią zachowanie przed transformacją.
- [ ] Wiem, kiedy wzorzec można uprościć lub usunąć.

### 11.5. Przed zakończeniem refaktoryzacji

- [ ] Zachowanie objęte testami pozostało niezmienione.
- [ ] Nowe kontrakty mają jasno opisane gwarancje.
- [ ] Nie pozostała stara i nowa reprezentacja tej samej wiedzy.
- [ ] Nie wprowadzono funkcji przeznaczonych wyłącznie dla hipotetycznej przyszłości.
- [ ] Zależności nie tworzą nowych cykli.
- [ ] Obsługa błędów zachowuje ustaloną semantykę.
- [ ] Pełny build przechodzi na Javie 25.
- [ ] Program demonstracyjny uruchamia się bez dodatkowej konfiguracji.

## 12. Podsumowanie

DRY, KISS i YAGNI równoważą centralizację wiedzy, prostotę aktualnego rozwiązania i odraczanie spekulacyjnych decyzji. Żadna z tych zasad nie działa przez mechaniczne liczenie powtórzeń, klas lub interfejsów.

SOLID pomaga analizować aktorów zmian, wybrane osie rozszerzeń, kontrakty substytucji, role klientów oraz kierunek zależności. Zasady nie określają jednej poprawnej struktury. Ich wartość ujawnia się wtedy, gdy ograniczają koszt konkretnej zmiany bez ukrywania semantyki.

Wysoka spójność utrzymuje razem elementy realizujące wspólny cel. Niskie sprzężenie ogranicza koszt zmian między granicami, lecz nie oznacza braku współpracy. Interfejs zastępuje jedno sprzężenie innym i ma sens wtedy, gdy nowy kontrakt jest stabilniejszy oraz należy do właściwej strony.

Clean Architecture chroni politykę przez kierowanie zależności źródłowych do wnętrza. Nie wymaga sztywnego diagramu folderów. Porty, adaptery i composition root są użyteczne tylko w zakresie, w którym oddzielają realne decyzje i ryzyka.

Wzorzec projektowy jest możliwym kierunkiem serii refaktoryzacji, nie punktem startowym ani oznaką dojrzałości kodu. Najpierw należy ochronić zachowanie i rozpoznać problem. Później trzeba wprowadzić najwęższe rozwiązanie, zmierzyć jego koszt i zachować gotowość do uproszczenia, gdy kontekst się zmieni.
