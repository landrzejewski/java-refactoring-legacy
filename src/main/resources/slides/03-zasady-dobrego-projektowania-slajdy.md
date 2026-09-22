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
# Moduł 3. Zasady dobrego projektowania
DRY, KISS, YAGNI, SOLID, spójność i sprzężenie, Clean Architecture oraz wzorce jako narzędzia oceny kosztu zmiany

---

## Agenda

1. Jakość projektu w kontekście zmiany
2. DRY, KISS i YAGNI
3. Zasady SOLID w praktyce
4. Wysoka spójność i niskie sprzężenie
5. Clean Architecture i pokrewne podejścia inside/outside
6. Wzorce projektowe jako kierunek refaktoryzacji
7. Studium przypadku w Javie 25: wycena dostawy
8. Warsztat praktyczny, omówienie i listy kontrolne

---

## Dobry projekt nie jest cechą absolutną

- Projekt organizuje wiedzę, odpowiedzialności i zależności, a jego jakość ujawnia się przede wszystkim **podczas zmiany**.
- Dobry projekt pozwala znaleźć właściwe miejsce modyfikacji, ogranicza promień oddziaływania i szybko daje wiarygodną informację zwrotną.
- Ocena zależy od kontekstu: reguł domeny, rodzaju i częstotliwości zmian, krytyczności błędów, wymagań jakościowych, struktury zespołów i czasu życia rozwiązania.
- Kod może być lokalnie elegancki, a mimo to utrudniać najczęstsze zmiany; kod proceduralny może poprawnie modelować mały, zamknięty problem.
- Zasada projektowa ma pomóc **postawić pytanie**, a nie zakończyć analizę.

---

## Zasady są heurystykami

| Zasada | Pytanie diagnostyczne | Ryzyko nadinterpretacji |
| --- | --- | --- |
| DRY | Gdzie jest autorytatywna reprezentacja wiedzy? | łączenie reguł, bo wyglądają podobnie |
| KISS | Czy jest mniej złożone poprawne rozwiązanie? | pomijanie przypadków, walidacji, błędów |
| YAGNI | Jakie aktualne wymaganie to uzasadnia? | zaniedbanie zdrowia kodu |
| SOLID | Jak podział ogranicza koszt konkretnej zmiany? | mechaniczne klasy i interfejsy |
| Clean Arch. | Czy polityka zależy od szczegółu technicznego? | kopiowanie szablonu warstw |
| wzorzec | Jaki problem i jakie siły równoważy? | katalog wzorców bez potrzeby |

Jeśli decyzji nie da się powiązać z wymaganiem, ryzykiem lub kierunkiem zmiany, sama nazwa zasady nie jest uzasadnieniem.

---

## Projekt obejmuje więcej niż klasy

- Na koszt zmiany wpływają też schematy danych, formaty komunikatów, granice transakcji oraz modele błędów i ponowień.
- Liczą się również zależności wdrożeniowe, konfiguracja, proces budowania, publiczne API i zgodność danych historycznych.
- Znaczenie mają kolejność operacji, wymagania czasowe oraz właściciele modułów i sposób współpracy zespołów.
- Dodanie interfejsu nie naprawi niejasnej transakcji, a rozdzielenie klasy nie usunie sprzężenia przez wspólną tabelę.
- Zmiana nazwy pakietu nie tworzy granicy, jeśli moduły nadal odwołują się do szczegółów infrastruktury.

---

## Dowód wartości zmiany projektu

Przed refaktoryzacją opisz jeden lub dwa scenariusze zmiany:

1. Co ma zostać zmienione i które pliki, moduły oraz zespoły trzeba dziś zaangażować?
2. Jakie ryzyka i testy uruchamia ta zmiana?
3. Jaki podział zmniejszy ten koszt i jaki **nowy koszt** wprowadzi proponowana abstrakcja?
4. Jak sprawdzimy, że cel został osiągnięty?

Taki scenariusz jest lepszym uzasadnieniem niż stwierdzenie, że kod „nie jest wystarczająco zgodny z SOLID”.

---

## DRY dotyczy wiedzy

- DRY wymaga jednej, jednoznacznej i autorytatywnej reprezentacji danej porcji **wiedzy** - nie jest zakazem występowania podobnych linii.
- Wiedzą jest np. reguła cenowa lub podatkowa, format komunikatu, sposób wyliczenia statusu, ograniczenie walidacyjne czy mapowanie pojęcia domenowego.
- Wiedzą jest też konfiguracja wdrożenia oraz kontrakt, z którego generowane są inne artefakty.
- Kluczowe pytanie: **czy zmiana jednej decyzji wymaga znalezienia i zgodnej aktualizacji kilku miejsc?**
- Jeśli tak - istnieje kandydat na duplikację wiedzy.

---

## Podobieństwo nie wystarcza

| Sytuacja | Ocena |
| --- | --- |
| walidacja wieku i liczby produktów: `value > 0` | podobny kod, niezależna wiedza i właściciele |
| stawka opłaty paliwowej w dwóch wariantach usługi | jedna reguła - kandydat do centralizacji |
| dwa konteksty mają dziś rabat 10% | podobieństwo może być przypadkowe; ustal relację reguł |
| cache powiela dane źródłowe | kontrolowana duplikacja z właścicielem i synchronizacją |
| klient API i dokumentacja z jednego kontraktu | wiele artefaktów, jedno źródło wiedzy |

Wspólna metoda dla niezależnych reguł tworzy **fałszywą zależność** i wymusza nieuzasadnioną koordynację.

---

## Fałszywa abstrakcja i DRY w testach

- Istniejącą fałszywą abstrakcję najpierw zabezpiecz testami, potem przenieś kod z powrotem do kontekstów i usuń zbędne gałęzie.
- Dopiero wtedy wydziel wyłącznie potwierdzoną wspólną wiedzę - tymczasowe powtórzenie kodu bywa bezpiecznym etapem rozdzielania.
- Testy mogą współdzielić czytelne fabryki danych i stabilne przygotowanie środowiska.
- Test **nie powinien** liczyć oczekiwanej wartości tym samym algorytmem co produkcja, bo traci niezależną wyrocznię i powiela defekt.
- Kilka jawnych wartości bywa tańsze niż ogólny kreator scenariuszy z wieloma flagami - czytelność przypadku to wymaganie testu.

---

## KISS: prostota po poprawności

> Preferuj najmniej złożone rozwiązanie, które poprawnie realizuje aktualne wymagania i ograniczenia.

- Prostota nie oznacza najmniejszej liczby linii ani klas, ani pierwszego rozwiązania, które udało się uruchomić.
- Nie oznacza też technologii najlepiej znanej autorowi, braku abstrakcji ani pominięcia błędów, bezpieczeństwa, transakcji czy wydajności.
- Rozdzielenie splątanych odpowiedzialności może zwiększyć liczbę plików, a jednocześnie zmniejszyć złożoność poznawczą i promień zmian.

---

## Złożoność istotna i wprowadzona

- Nie każdą złożoność można usunąć: reguły prawne, współbieżność, nieodwracalne efekty czy rozliczenia finansowe są naprawdę trudne.
- KISS kieruje uwagę na złożoność **wprowadzoną przez rozwiązanie**, np. pośrednie warstwy bez odrębnej odpowiedzialności lub konfigurację zastępującą czytelny kod.
- Przykłady nadmiaru: mechanizm pluginów bez rozszerzeń, generyczny silnik reguł dla dwóch przypadków, hierarchia typów bez kontraktu substytucji.
- Ukryty przepływ sterowania oparty na refleksji lub globalnym rejestrze również jest złożonością wprowadzoną.
- Gdy problem jest złożony, celem jest **nazwanie i izolowanie trudności**, a nie jej pozorne usunięcie.

---

## YAGNI: aktualna potrzeba przed przewidywaną

- YAGNI zaleca, by nie budować dziś zdolności potrzebnej wyłącznie dla przewidywanego wymagania - dotyczy to funkcji i spekulacyjnej elastyczności.
- Typowe przykłady: parametry bez wariantu, fabryki tworzące jeden typ, interfejsy dla hipotetycznych implementacji, obsługa przyszłych formatów.
- Do tej grupy należą też systemy pluginów, dynamiczne ładowanie oraz pola i metody, których nikt nie używa.
- Przedwczesny element trzeba zaprojektować i przetestować, opóźnia aktualną wartość i podnosi bieżący koszt rozumienia.
- Może też wymagać przebudowy, gdy rzeczywista potrzeba okaże się inna niż przewidywana.

---

## Czego YAGNI nie zabrania

- Nie uzasadnia pomijania refaktoryzacji upraszczającej aktualny kod ani rezygnacji z testów i ciągłej integracji.
- Nie pozwala ignorować aktualnych wymagań bezpieczeństwa, zgodności, znanych limitów wydajności ani odkładać potwierdzonej migracji danych.
- Nie usprawiedliwia usuwania kodu używanego przez refleksję, konfigurację lub zewnętrznych klientów bez sprawdzenia.
- Ograniczony czasowo eksperyment techniczny może być aktualną potrzebą, jeśli rozwiązuje niewiadomą blokującą decyzję.
- YAGNI działa, gdy system jest podatny na zmianę: testy, małe kroki i refaktoryzacja pozwalają bezpiecznie odraczać decyzje.

---

## Napięcia między zasadami

| Decyzja | DRY | KISS | YAGNI |
| --- | --- | --- | --- |
| nazwanie potwierdzonej reguły opłaty | jedno źródło wiedzy | brak synchronizacji | aktualna reguła |
| ogólny silnik hipotetycznych taryf | brak korzyści | więcej pojęć i trybów | przewidywana elastyczność |
| podobne reguły niezależnych działów | brak wspólnej wiedzy | lokalność prostsza | brak spekulacji |
| wspólna biblioteka niezależnych usług | brak wspólnej wiedzy | droższa nawigacja | narusza, jeśli dla hipotetycznego współdzielenia |

**Reguła trzech** pomaga poczekać na kształt abstrakcji, ale nie definiuje DRY: dwie kopie tej samej reguły mogą wymagać natychmiastowej centralizacji.

---

## Filtr decyzyjny przed dodaniem abstrakcji

1. Jakie aktualne wymaganie lub ryzyko ją uzasadnia?
2. Jaką konkretną wiedzę reprezentują podobne miejsca?
3. Czy zmieniają się z tego samego powodu i na żądanie tego samego właściciela?
4. Czy abstrakcja ma nazwę w języku problemu?
5. Czy upraszcza aktualny przypadek, czy tylko przyszły scenariusz?
6. Ile nowych trybów, warunków i zależności wprowadza - i jak łatwo będzie ją zmienić lub usunąć?

---

## SOLID jako narzędzie analizy zmian

SOLID to zbiór heurystyk, a nie system punktowy ani nakaz maksymalizacji polimorfizmu.

| Zasada | Główna troska |
| --- | --- |
| SRP | grupowanie elementów zmieniających się dla tego samego aktora |
| OCP | ochrona stabilnej części przed wybraną osią rozszerzeń |
| LSP | zachowanie kontraktu przy podstawieniu implementacji |
| ISP | ograniczenie zależności klienta do potrzebnej roli |
| DIP | kierowanie zależności od szczegółów ku polityce i abstrakcjom |

---

## SRP: jeden aktor zmiany

- „Jeden powód do zmiany” oznacza odpowiedzialność wobec **aktora** lub spójnej grupy interesariuszy, a nie każdą możliwą edycję pliku.
- Serwis oferty może łączyć regułę ceny (finanse), zapis (zespół danych) i format wiadomości (komunikacja z klientem) - jeden przebieg, różne przyczyny zmian.
- Przypadek użycia może te operacje koordynować, ale nie powinien przejmować szczegółów każdej polityki.
- SRP nie oznacza jednej metody na klasę, limitu linii ani zakazu koordynacji; dotyczy metod, klas, pakietów i komponentów.
- Pytania: kto żąda zmiany, co zmienia się razem, czy zmiana domenowa wymaga edycji infrastruktury, czy moduł jest polem konfliktu zespołów?

---

## OCP: zamknięcie dla wybranej osi

- OCP to możliwość rozszerzenia **wybranego** zachowania bez modyfikowania stabilnej części - nie da się zamknąć systemu na wszystkie zmiany.
- Przykładowe osie: otwarty zbiór polityk wyceny lub adapterów, zamknięty zbiór stanów protokołu, stabilny algorytm z wymiennym źródłem danych.
- Dodanie implementacji może nadal wymagać zmiany composition root, konfiguracji lub rejestru - OCP nie jest zakazem edycji plików.
- Nie każdy `switch` narusza OCP: może jasno modelować mały, celowo zamknięty zbiór wariantów.
- Polimorfizm się opłaca, gdy zbiór rozszerza się niezależnie, a centralne rozgałęzienia wymuszają powtarzalne modyfikacje.

---

## LSP: substytucja behawioralna (1/2)

- LSP dotyczy **zachowania**, a nie tylko zgodności sygnatur: klient typu bazowego musi móc użyć każdego podtypu bez wiedzy o implementacji.
- Kontrakt obejmuje dopuszczalne wejście, gwarancje wyniku, inwarianty, obserwowalne wyjątki, efekty uboczne i protokół wywołań.
- Obejmuje też mutowalność, kolejność, idempotencję czy bezpieczeństwo wątkowe - jeśli zostały obiecane.
- Podtyp **nie wzmacnia warunków wstępnych** (przyjmuje co najmniej to, co typ bazowy) i **nie osłabia warunków końcowych** (może dać silniejszą gwarancję, nie słabszą).

---

## LSP: substytucja behawioralna (2/2)

- Kompilator Javy sprawdza część zgodności typów i wyjątków kontrolowanych, ale nie kontrakty domenowe, inwarianty ani znaczenie wyniku.
- Test kontraktowy uruchamiany dla wszystkich implementacji dostarcza **dowodów** substytucyjności, lecz jej formalnie nie dowodzi.
- Testuje wybrane przykłady, a nie całą przestrzeń stanów - jego zakres musi rosnąć razem z publicznymi gwarancjami.
- `UnsupportedOperationException` nie jest automatycznie naruszeniem LSP, bo niektóre interfejsy Javy jawnie opisują operacje opcjonalne.
- Ocena zawsze zaczyna się od rzeczywistego kontraktu typu bazowego.

---

## ISP: interfejs z perspektywy klienta

- Klient nie powinien zależeć od metod, których nie potrzebuje - interfejsy projektujemy według **ról klientów** i ich powodów zmian.
- ISP nie wymaga jednej metody w interfejsie: jednometodowy port jest dobry, gdy reprezentuje jedną spójną rolę.
- Szeroki interfejs też może być właściwy, jeśli jego operacje tworzą nierozdzielny kontrakt klienta.
- Typowe naruszenie: ogólny `CrudRepository<T>` przekazany przypadkowi użycia, który potrzebuje jednej operacji.
- Klient staje się wtedy źródłowo zależny od zapisu, usuwania i wyszukiwania, których wcale nie używa.

---

## DIP: kierunek zależności, nie sposób wstrzykiwania

- DIP kieruje zależności źródłowe od szczegółów technicznych ku stabilniejszej polityce i abstrakcjom.
- Moduł wysokiego poziomu nie powinien importować klienta bazy, biblioteki HTTP czy modelu frameworka, gdy potrzebuje jedynie informacji lub efektu.
- **DIP ≠ dependency injection**: wstrzyknięcie konkretnej klasy wciąż wiąże ze szczegółem, a DIP działa bez kontenera IoC.
- Ręczne składanie grafu w composition root jest poprawne; `new` dla obiektów wartości i kolekcji nie narusza DIP; interfejs dla każdej klasy nie jest wymagany.
- Port nazywa potrzeba klienta: przypadek użycia definiuje `QuoteRepository`, a adapter bazodanowy go implementuje.

---

## Jak zasady SOLID się uzupełniają

- **SRP** pomaga znaleźć niezależne osie zmian, a **OCP** chroni wybraną oś przez stabilny kontrakt.
- **LSP** zapewnia, że nowe implementacje nie psują tego kontraktu.
- **ISP** ogranicza kontrakt do roli potrzebnej klientowi, a **DIP** ustawia zależności implementacji w stronę kontraktu i polityki.
- Łatwość utworzenia mocka nie dowodzi zgodności z SOLID - wskazuje jedynie, że istnieje punkt podmiany.
- Jakość granicy zależy od znaczenia kontraktu i kosztu zmian, a nie od samej obecności interfejsu.

---

## Błędne uproszczenia SOLID

| Uproszczenie | Korekta |
| --- | --- |
| SRP: klasa robi jedną rzecz | odpowiedzialność wynika z aktora i powodu zmiany |
| każdy `switch` łamie OCP | zamknięty zbiór wariantów może być poprawnym modelem |
| dziedziczenie gwarantuje LSP | potrzebna jest zgodność behawioralna |
| ISP: jedna metoda na interfejs | interfejs odpowiada spójnej roli klienta |
| DIP: konstruktor lub `@Autowired` | chodzi o kierunek zależności źródłowej |
| interfejs dla każdej klasy | abstrakcja musi chronić znaczącą granicę lub zmienność |

---

## Spójność

- Spójność opisuje, jak silnie elementy modułu - metody, klasy, pakietu, komponentu, usługi - współpracują na rzecz wspólnego celu.
- Wysoko spójny moduł da się opisać jednym zdaniem domenowym, a jego elementy zmieniają się z tego samego powodu.
- Elementy potrzebują podobnego kontekstu i danych, a testy skupiają się na jednym obszarze zachowania.
- Nazwa wyjaśnia rolę bez ogólników typu `Manager` czy `Utils`.
- Mała klasa nie musi być spójna, a duża może reprezentować bogaty, nierozdzielny koncept - liczba metod to sygnał, nie definicja.

---

## Sprzężenie i dlaczego nie da się go usunąć

- Praktyczne pytanie: czy zmiana jednego modułu wymusza edycję, testy, wdrożenie lub koordynację drugiego?
- Sprzężenie dotyczy importów, formatu i znaczenia danych, kolejności wywołań, czasu i dostępności, wspólnego stanu, technologii, wdrożeń i wiedzy zespołów.
- Jedna zależność od niestabilnego, szerokiego kontraktu może kosztować więcej niż kilka zależności od małych, stabilnych pojęć.
- Celem jest silne sprzężenie wewnątrz spójnej granicy, jawne kontrakty, zależności ku stabilnym pojęciom i brak cykli.
- **Interfejs nie usuwa sprzężenia** - zastępuje zależność od implementacji zależnością od kontraktu i przenosi wybór implementacji gdzie indziej.

---

## Koszt wydzielenia i ukrywana decyzja

- Extract Class może zwiększyć spójność, ale dodaje współpracownika, wydłuża nawigację i tworzy nowy kontrakt.
- Może też wymagać koordynacji cyklu życia obiektów lub rozdzielić dane, które muszą zmieniać się atomowo - porównuj koszt zmiany przed i po.
- Dobry moduł ukrywa decyzję trudną lub prawdopodobnie zmienną, zamiast dzielić system tylko według etapów przetwarzania.
- Warto izolować politykę cenową, sposób zapisu, format komunikatu, wybór algorytmu oraz szczegóły biblioteki lub protokołu.
- Zmiana ukrytej decyzji powinna mieć lokalny zasięg: konsument zna operację i jej semantykę, a nie reprezentację szczegółu.

---

## Diagnostyka spójności i sprzężenia

| Pytanie | Sygnał problemu |
| --- | --- |
| Ile miejsc zmienić dla jednej reguły? | rozproszona wiedza, shotgun surgery |
| Czy moduł ma kilku niezależnych właścicieli? | niska spójność odpowiedzialności |
| Czy typ frameworka trafia do domeny? | sprzężenie technologiczne przez granicę |
| Czy wywołania wymagają ukrytej kolejności? | sprzężenie protokołu lub czasu |
| Czy zmiana adaptera zmienia przypadek użycia? | zły kierunek zależności |
| Czy moduły importują się wzajemnie? | cykl blokujący niezależną zmianę |

Historia wspólnych zmian w repozytorium bywa dowodem sprzężenia, ale wymaga interpretacji (migracje, formatowanie, proces).

---

## Clean Architecture: cel i reguła zależności

- Clean Architecture organizuje granice i zależności tak, aby kod polityki biznesowej nie zależał od szczegółów technologicznych.
- **Reguła zależności**: zależności źródłowe przekraczające granicę wskazują w stronę polityki wyższego poziomu, a nie zewnętrznego mechanizmu.
- Przypadek użycia nie importuje `ResultSet`, encji ORM, żądania HTTP ani modelu dostawcy wiadomości.
- Adapter tłumaczy te typy na model wygodny dla wnętrza.

---

## Kierunek zależności a przepływ sterowania

1. Aplikacja definiuje port `QuoteRepository` potrzebny przypadkowi użycia.
2. Przypadek użycia wywołuje ten port.
3. Adapter bazodanowy implementuje port i importuje pakiet aplikacyjny.
4. Composition root tworzy adapter i przekazuje go do przypadku użycia.

- **Sterowanie** płynie od przypadku użycia do adaptera, a **zależność źródłowa** adaptera prowadzi do portu wewnętrznego.
- To praktyczne zastosowanie DIP: wywołanie bazy w czasie wykonania nie wymaga źródłowej zależności od jej klas.

---

## Kręgi nie są obowiązkowym szablonem

- Diagram z encjami, przypadkami użycia, adapterami i frameworkami jest schematem - system może mieć mniej lub więcej granic.
- Istotne jest położenie polityki i kierunek zależności, a nie nazwy czterech folderów.
- Architektura heksagonalna, Onion i Clean Architecture to rodzina podejść inside/outside, ale z różnymi pojęciami - nie są dokładnymi synonimami.
- Clean Architecture nie wymaga mikroserwisów, DDD, rozbudowanego modelu obiektowego ani frameworka DI.
- Może z powodzeniem działać wewnątrz modularnego monolitu.

---

## Elementy praktyczne

| Element | Odpowiedzialność |
| --- | --- |
| domena | stabilne pojęcia, inwarianty i reguły problemu |
| przypadek użycia | orkiestracja jednego celu użytkownika lub systemu |
| port wejściowy | opcjonalna stabilna granica wywołania przypadku użycia |
| port wyjściowy | potrzeba wnętrza wobec świata zewnętrznego |
| adapter wejściowy | mapowanie żądania na dane przypadku użycia |
| adapter wyjściowy | realizacja portu przez bazę, plik, HTTP, broker |
| composition root | wybór konkretów i składanie grafu zależności |

Port należy do strony formułującej potrzebę - nie jest odbiciem wszystkich możliwości bazy czy dostawcy.

---

## Dane na granicy i composition root

- Wnętrze nie powinno przyjmować modelu wygodnego wyłącznie dla frameworka; granicę przekraczają proste rekordy, obiekty wartości, jawne argumenty i błędy kontraktu.
- Mapowanie kosztuje, ale chroni wnętrze przed zmianą zewnętrznego schematu - osobny model ma sens, gdy granica oddziela różne semantyki lub tempo zmian.
- Nie należy mechanicznie kopiować każdego obiektu tylko dla zasady.
- Composition root zna porty i konkretne adaptery, wybiera implementacje i tworzy graf obiektów, ale **nie zawiera reguł biznesowych**.
- Ręczne konstruktory to pełnoprawny composition root; kontener DI automatyzuje składanie, ale nie tworzy poprawnego kierunku zależności.

---

## Koszty i ograniczenia granic

- Granice wprowadzają dodatkowe porty i modele, mapowanie, więcej plików, koszt nawigacji oraz testy kontraktowe i integracyjne.
- Rośnie też złożoność konfiguracji composition root.
- Mały CRUD, krótko żyjący prototyp czy stabilny skrypt mogą nie uzasadniać pełnego podziału.
- Granica jest wartościowa, gdy chroni istotną politykę, rozdziela tempo zmian, umożliwia potrzebny test lub izoluje ryzykowny mechanizm.
- Niezależność od bazy nie znaczy, że bazę wymienimy bez kosztu - adapter ogranicza propagację szczegółów, ale nie usuwa właściwości technologii.

---

## Wzorzec opisuje decyzję w kontekście

- Wzorzec nazywa powtarzalny problem, kontekst, siły, strukturę rozwiązania i konsekwencje - nie jest kodem do skopiowania ani celem samym w sobie.
- Przed użyciem trzeba znać występujący problem oraz aktualne warianty i właścicieli zmian.
- Trzeba wiedzieć, jaki kontrakt wymaga stabilizacji i ile kosztuje dodatkowy poziom pośrednictwa.
- Należy znać prostszą alternatywę oraz warunek, w którym wzorzec przestanie być potrzebny.

---

## Refaktoryzacja w kierunku wzorca

1. Zabezpiecz obserwowalne zachowanie.
2. Nazwij konkretną oś zmienności.
3. Wykonaj małe refaktoryzacje odsłaniające role.
4. Wprowadź najwęższy potrzebny kontrakt.
5. Przenieś jeden wariant za granicę i uruchom testy zachowania oraz kontraktu.
6. Przenieś pozostałe warianty i **zatrzymaj się**, gdy aktualny problem jest rozwiązany.

Wzorzec może wyłonić się z kolejnych transformacji - nie trzeba projektować jego najbardziej ogólnej postaci na starcie.

---

## Strategy i Adapter

- **Strategy** pasuje do rodziny wymiennych algorytmów o wspólnym kontrakcie; to, czy zbiór jest otwarty, czy zamknięty, to osobna decyzja OCP.
- Czytelny `switch` bywa lepszy, gdy warianty są nieliczne, celowo zamknięte, zmieniają się razem, a logika jest lokalna.
- W warsztacie `DeliveryPricePolicy` uzasadniają dwie istniejące polityki wyceny; mechanizmu pluginów nie ma, bo nie ma takiego wymagania.
- **Adapter** tłumaczy niezgodny interfejs lub model zewnętrzny na kontrakt klienta i ma sens na realnej granicy technologii lub semantyki.
- Jego wartość to mapowanie formatów, tłumaczenie błędów, ukrycie protokołu, kontrola ponowień i czasu - nie puste przekazywanie wywołań 1:1.

---

## Wzorzec można usunąć

Refaktoryzacja **od** wzorca jest równie poprawna jak refaktoryzacja **do** wzorca, gdy warianty zniknęły lub zmienność okazała się inna.

Sygnały nadmiaru:

- jedna implementacja od długiego czasu lub interfejs powtarzający API jednej klasy,
- fabryka zawierająca jeden konstruktor albo konfiguracja trudniejsza od jawnego `new`,
- większość implementacji rzuca `UnsupportedOperationException`,
- dodanie wariantu nadal wymaga zmian w wielu centralnych miejscach.

---

## Studium przypadku: punkt wyjścia

```java
public DeliveryQuote createQuote(String customerEmail,
        ShippingMethod method, Parcel parcel) {
    BigDecimal price = switch (method) {
        case STANDARD -> { BigDecimal base = new BigDecimal("10.00")
                .add(parcel.weightKg().multiply(new BigDecimal("2.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08")))); }
        case EXPRESS -> { BigDecimal base = new BigDecimal("20.00")
                .add(parcel.weightKg().multiply(new BigDecimal("3.00")));
            yield money(base.add(base.multiply(new BigDecimal("0.08")))); }
    };
    DeliveryQuote quote = new DeliveryQuote(customerEmail, method, parcel, price);
    storedQuotes.add(quote);
    System.out.printf("Quote ready for %s: %s costs %s%n",
            customerEmail, method, price);
    return quote;
}
```

`LegacyDeliveryQuoteService` liczy cenę, zapisuje ofertę we własnej kolekcji, wypisuje powiadomienie i zwraca wynik.

---

## Co naprawdę jest problemem

Sam `switch` nie jest głównym problemem - kłopotem są niezależne decyzje zebrane w jednym miejscu.

| Obserwacja | Konsekwencja |
| --- | --- |
| stawka `0.08` w obu gałęziach | jedna reguła, dwie reprezentacje |
| polityki cenowe wewnątrz serwisu | zmiana wariantu modyfikuje koordynację |
| zapis w kolekcji serwisu | przypadek użycia zna mechanizm przechowywania |
| bezpośredni `System.out` | zmiana kanału wymaga edycji serwisu |
| różni właściciele zmian | niska spójność klasy |
| ukryta kolejność zapis → powiadomienie | protokół wymaga kontraktu i testu |

---

## Najpierw test charakterystyki

```java
@Test
void documentsStandardDeliveryPriceAndStorage() {
    LegacyDeliveryQuoteService service = new LegacyDeliveryQuoteService();
    DeliveryQuote quote = service.createQuote("developer@example.com",
            ShippingMethod.STANDARD, new Parcel(new BigDecimal("3.00")));

    assertEquals(new BigDecimal("17.28"), quote.price());
    assertEquals(List.of(quote), service.storedQuotes());
}
// EXPRESS dla 3 kg: 31.32
```

- Pierwszym krokiem nie jest wzorzec, lecz zapisanie znanego zachowania: ceny i zapisu.
- Test nie przechwytuje konsoli - to **jawna luka** w charakterystyce; trzeba ustalić, czy treść i kolejność powiadomień są częścią kontraktu.

---

## Model i inwarianty domeny

- Rekordy `Parcel` i `DeliveryQuote` pilnują inwariantów: dodatnia masa, niepusty e-mail, nieujemna cena o skali dokładnie 2.
- Cena w konstruktorze wyniku nie jest niejawnie zaokrąglana (`RoundingMode.UNNECESSARY`) - błędna skala zostaje wykryta, a nie ukryta.
- `record` ogranicza kod techniczny, ale znaczenie modelu nadal wynika z nazw i reguł konstruktora.
- `BigDecimal` tworzony z tekstu unika przybliżeń binarnych typu `double`; walidacja e-maila celowo ogranicza się do niepustości.

---

## DRY zastosowane do reguły opłaty

- Powtarzającą się decyzję - stawkę i sposób doliczenia opłaty paliwowej - nazwano `FuelSurcharge` z jednym właścicielem.
- Nie wydzielono wspólnej `basePrice(base, rate, weight)`: formuły są podobne, ale to **odrębne cenniki**, które mogą zmieniać się niezależnie.
- Inwariant stawki 0-1 (0-100%) jest jawną decyzją modelową, a nie cechą typu `BigDecimal`.

```java
public BigDecimal addTo(BigDecimal baseAmount) {
    return baseAmount.add(baseAmount.multiply(rate))
            .setScale(2, RoundingMode.HALF_UP);
}
```

---

## Strategy dla istniejących wariantów

```java
public interface DeliveryPricePolicy {
    /** Stable, non-null shipping method handled by this policy. */
    ShippingMethod method();

    /** Deterministic, non-negative amount with scale two for every
     *  valid parcel, without changing the parcel or side effects. */
    BigDecimal priceFor(Parcel parcel);
}
```

- Kontrakt dokumentuje gwarancje, których same typy nie wyrażają: stabilna metoda, determinizm, nieujemność, skala 2, brak efektów ubocznych.
- `StandardDeliveryPricePolicy` i `ExpressDeliveryPricePolicy` mają własne stałe cennika i współdzielą tylko `FuelSurcharge`.
- Strategy ma potwierdzone uzasadnienie: dwa istniejące algorytmy, wspólny kontrakt, wybór według metody dostawy - bez pluginów i dynamicznego odkrywania.

---

## Świadomy zakres OCP i wybór polityki

- `ShippingMethod` pozostaje enumem, więc nowa metoda dostawy wymaga zmiany enuma i composition root - zamknięta jest logika wyboru, nie cały system.
- `DeliveryPriceCalculator` indeksuje strategie raz w konstruktorze (`EnumMap`) i odrzuca pustą lub zduplikowaną konfigurację.
- `Map.copyOf` daje niemodyfikowalną kopię, więc późniejsza zmiana przekazanej kolekcji nie wpływa na kalkulator.
- Brak polityki wykrywany jest przy żądaniu; alternatywą jest wymaganie kompletu metod w konstruktorze - zależy, czy częściowa konfiguracja jest legalna.
- Przypadek użycia zależy od konkretnego kalkulatora bez interfejsu, bo brak drugiej sensownej implementacji - **DIP nie wymaga interfejsu przed każdą klasą**.

---

## Test kontraktowy i LSP

```java
@ParameterizedTest(name = "{0}")
@MethodSource("policies")
void everyPolicyObeysTheSubstitutionContract(String description,
        ShippingMethod expectedMethod, DeliveryPricePolicy policy) {
    Parcel parcel = new Parcel(new BigDecimal("100.00"));
    BigDecimal first = policy.priceFor(parcel);
    assertAll(
        () -> assertEquals(expectedMethod, policy.method()),
        () -> assertTrue(first.signum() >= 0),
        () -> assertEquals(2, first.scale()),
        () -> assertEquals(first, policy.priceFor(parcel)));
}
```

- Każda strategia przechodzi ten sam zestaw sprawdzeń jawnych gwarancji kontraktu.
- Test **nie dowodzi** LSP dla wszystkich stanów - nie sprawdza np. maksymalnej masy, każdego zaokrąglenia ani wydajności.

---

## Porty definiowane przez potrzeby przypadku użycia

```java
@FunctionalInterface
public interface QuoteRepository { void save(DeliveryQuote quote); }

@FunctionalInterface
public interface QuoteNotifier { void quoteCreated(DeliveryQuote quote); }
```

- Przypadek użycia potrzebuje dwóch odrębnych ról - stąd dwa wąskie porty, a nie mechaniczne zastosowanie ISP.
- Gdyby jedna spójna operacja wymagała odczytu i zapisu w jednym kontrakcie, dwie metody w porcie byłyby właściwe.
- `QuoteRepository` nie dziedziczy `CrudRepository`, więc wnętrze nie zależy od usuwania, stronicowania i wyszukiwania.
- Nazwa portu opisuje potrzebę wnętrza, a nie technologię adaptera.

---

## Przypadek użycia jako jawna orkiestracja

```java
public DeliveryQuote execute(Command command) {
    Objects.requireNonNull(command, "command");
    DeliveryQuote quote = new DeliveryQuote(
            command.customerEmail(), command.method(), command.parcel(),
            priceCalculator.priceFor(command.method(), command.parcel()));
    repository.save(quote);
    notifier.quoteCreated(quote);
    return quote;
}
```

- `CreateDeliveryQuote` ma wysoką spójność celu: koordynuje jedną wycenę, bez wzoru ceny, kodu zapisu i formatowania komunikatu.
- Zależności wstrzykiwane są ręcznie przez konstruktor.

---

## Protokół efektów i test przypadku użycia

- Kolejność `save` → `quoteCreated` to **decyzja protokołu**: przy wyjątku zapisu powiadomienie nie jest wysyłane - musi to wynikać z wymagania biznesowego.
- W systemie rozproszonym kolejność nie gwarantuje atomowości; może być potrzebna strategia ponowień, idempotencja lub transactional outbox (poza zakresem przykładu).
- Test dokumentuje przebieg udany i zachowanie przy błędzie: `doesNotNotifyWhenSavingFails`.
- Lambdy w teście (`savedQuotes::add`) to proste implementacje portów - nie udają bazy ani dostawcy wiadomości.
- Test weryfikuje politykę przypadku użycia; rzeczywiste adaptery potrzebują osobnych testów integracyjnych.

---

## Adaptery na zewnętrznej stronie granicy

```java
public final class ConsoleQuoteNotifier implements QuoteNotifier {
    @Override
    public void quoteCreated(DeliveryQuote quote) {
        System.out.printf("Quote ready for %s: %s costs %s%n",
                quote.customerEmail(), quote.method(), quote.price());
    }
}
```

- `InMemoryQuoteRepository` i `ConsoleQuoteNotifier` importują porty aplikacji; aplikacja nie importuje pakietu `adapter`.
- W czasie wykonania przypadek użycia wywołuje adaptery przez porty - kierunek sterowania i zależności źródłowej są różne.
- Repozytorium w pamięci to adapter demonstracyjny: bez bezpieczeństwa wątkowego, transakcji i trwałości - adapter JDBC/JPA wymaga testu z realną bazą.

---

## Composition root i kierunek zależności

- `Module3Examples` składa graf: `FuelSurcharge` → polityki → `DeliveryPriceCalculator` → adaptery → `CreateDeliveryQuote`.
- Nie należy do domeny ani aplikacji; w aplikacji frameworkowej tę rolę pełni kod startowy, ale składanie nadal warto trzymać poza przypadkami użycia.
- Program potwierdza, że cena legacy i po refaktoryzacji są równe (`17.28`) i zapisano jedną wycenę.

| Kod | Może zależeć od | Nie powinien zależeć od |
| --- | --- | --- |
| domena | Java SE, pojęcia domenowe | aplikacja, adaptery, frameworki |
| aplikacja | domena, własne porty | konkretne adaptery, konsola, baza |
| adapter | porty, domena, technologia | composition root, cykle z innymi adapterami |
| composition root | wszystko do złożenia | reguły biznesowe |

---

## Zasady widoczne w rozwiązaniu

| Decyzja | Zasada | Korzyść | Koszt |
| --- | --- | --- | --- |
| `FuelSurcharge` | DRY, spójność | jedna stawka i zaokrąglenie | dodatkowy typ |
| dwie polityki | SRP, OCP, Strategy | lokalne warianty | kontrakt, rejestracja, pliki |
| test kontraktowy | LSP | wspólne sprawdzenie gwarancji | lista implementacji |
| porty | ISP, DIP | tylko potrzebne role | składanie implementacji |
| adaptery | Clean Arch., Adapter | efekty na zewnątrz | mapowanie, testy integracyjne |
| ręczny root | KISS, YAGNI | widoczny graf bez kontenera | ręczna konfiguracja |

Liczba klas wzrosła - wartością jest rozdzielenie niezależnych decyzji, uzasadnione tym, że dwa warianty i dwa efekty zewnętrzne **już istnieją**.

---

## Warsztat praktyczny - zasady pracy

- Ćwiczenia korzystają z kodu w `pl.training.module3`; przed startem uruchom `mvn clean verify`.
- Po każdym małym kroku uruchom odpowiedni test, a przed zakończeniem pełne `mvn verify`.
- Nie zmieniaj jednocześnie zachowania biznesowego i struktury, chyba że zadanie wyraźnie tego wymaga.
- Po każdym kroku notuj, którą oś zmiany chroni konstrukcja i jaki koszt dodaje.

---

## Ćwiczenie 1: DRY, KISS i YAGNI (20 + 10 min)

Dla każdej sytuacji zdecyduj: **scalić teraz, zostawić lokalnie czy zebrać więcej informacji?**

1. STANDARD i EXPRESS mają tę samą stawkę paliwową 8% i identyczne zaokrąglenie.
2. `MaximumParcelWeight` i `MaximumWarehouseLoad` w niezależnych kontekstach wynoszą dziś 1000 kg.
3. Testy STANDARD i EXPRESS zawierają jawną oczekiwaną cenę dla 3 kg.
4. Zespół proponuje refleksyjny system pluginów, bo „za rok” mogą pojawić się taryfy partnerów.
5. Zatwierdzono trzecią metodę dostawy za trzy tygodnie; cache wycen powiela dane dla zmierzonego limitu czasu.

Zapisz wiedzę, właściciela, wymaganie, najprostsze rozwiązanie, koszt błędu i sygnał ponownej oceny. Oceń, czy potrzebny jest interfejs `Surcharge`.

---

## Ćwiczenie 2: SOLID jako seria bezpiecznych zmian (25 + 10 min)

- Refaktoryzuj `LegacyDeliveryQuoteService`: wskaż aktorów zmiany i ustal z właścicielem procesu, czy po nieudanym zapisie wolno powiadomić.
- Wydziel `FuelSurcharge` bez łączenia cenników, potem kontrakt polityki - przenieś jeden wariant i uruchom testy przed drugim.
- Dodaj test kontraktowy dla każdej polityki, wydziel porty zapisu i powiadamiania, dopisz test awarii zapisu.
- Przenieś efekty do adapterów, złóż rozwiązanie ręcznie i porównaj wynik przed i po.
- Uzupełnij macierz kontraktu LSP (metoda, wejście, wynik, zaokrąglenie, determinizm, efekty, wyjątki).
- Kryteria: ceny `17.28` / `31.32` dla 3 kg, brak importu adapterów w przypadku użycia, zielone `mvn verify`.

---

## Ćwiczenie 3: granica i reguła zależności (25 + 10 min)

- **A.** Oceń jako dozwoloną, podejrzaną lub niedozwoloną m.in.: `domain` → `BigDecimal`, `application` → `InMemoryQuoteRepository`, `domain` → encja ORM, port zwracający wyjątek JDBC.
- **B.** Dodaj `BufferingQuoteNotifier` implementujący istniejący `QuoteNotifier`, z niemodyfikowalnym widokiem komunikatów i własnym testem.
- Podmień adapter **wyłącznie w composition root** - bez frameworka DI i bez plików konfiguracyjnych.
- **C.** Zaproponuj kontrolę zakazującą importu `adapter` w `application`/`domain`: test architektury, moduły Maven, JPMS lub kontrola w CI.
- Porównaj siłę gwarancji, koszt utrzymania i jakość komunikatu błędu; narysuj osobno zależność źródłową i wywołanie w czasie wykonania.

---

## Ćwiczenie 4: wzorzec jako decyzja odwracalna (20 + 10 min)

- **Scenariusz 1 - SAME_DAY:** cena pochodzi z klienta przewoźnika (masa w gramach jako `long`, kwota w najmniejszych jednostkach bez waluty, własny typ błędu, operacja sieciowa).
- Odpowiedz: kolejna `DeliveryPricePolicy`, adapter czy współpraca obu ról? Gdzie konwersja jednostek, jaki błąd przekracza granicę, gdzie timeout i ponowienia?
- Nie dopasowuj klienta na siłę do kontraktu, którego obietnic nie spełnia.
- **Scenariusz 2 - uproszczenie:** EXPRESS zniknął, STANDARD jest jedyną regułą - czy `DeliveryPricePolicy` i rejestr wciąż chronią realną zmienność?
- Upraszczaj tylko, gdy koszt abstrakcji przewyższa koszt prawdopodobnej zmiany; zielone testy umożliwiają refaktoryzację od wzorca.

---

## Omówienie ćwiczenia 1

| Sytuacja | Decyzja | Uzasadnienie |
| --- | --- | --- |
| wspólna opłata paliwowa | scalić teraz | jedna reguła, zmiana stawki musi być spójna |
| dwa limity 1000 kg | zostawić lokalnie | inna wiedza i właściciele |
| ceny w testach | zostawić jawnie | niezależna wyrocznia testowa |
| system pluginów | nie budować | brak potrzeby, wysoki koszt |
| zatwierdzony 3. wariant | najwęższe rozszerzenie | aktualna potrzeba, ale bez pluginów |
| cache | kontrolowana duplikacja | źródło prawdy, ważność, unieważnianie |

Interfejs `Surcharge` nie jest dziś potrzebny - brak drugiego wariantu i granicy; interfejs tylko do mockowania dodaje pojęcia bez semantyki.

---

## Omówienie ćwiczenia 2

| Obszar | Aktor zmiany | Odpowiedzialność |
| --- | --- | --- |
| cennik i opłata paliwowa | właściciel polityki cenowej | domena |
| zapis wyceny | właściciel danych i operacji | adapter portu |
| treść i kanał komunikatu | komunikacja z klientem | adapter powiadomień |
| kolejność kroków | właściciel procesu | przypadek użycia |

- Przykładowe naruszenia LSP: EXPRESS rejestruje się jako STANDARD, odrzuca paczki > 10 kg, zwraca `-1.00`, `17.280`, zależy od czasu lub zapisuje do bazy w `priceFor`.
- Test kontraktowy uzupełnia dokumentację; konkretne ceny wymagają testów poszczególnych polityk, a zmiana semantyki awarii zapisu musi być świadoma.

---

## Omówienie ćwiczenia 3

| Zależność | Ocena |
| --- | --- |
| domena → `BigDecimal` | dozwolona (Java SE, modelowanie wartości) |
| aplikacja → `InMemoryQuoteRepository` | niedozwolona |
| adapter → `QuoteRepository` | dozwolona |
| domena → encja ORM | niedozwolona w przyjętym modelu |
| composition root → konkrety; notifier → `DeliveryQuote` | dozwolone |
| wywołanie adaptera przez port | dozwolone - sterowanie nie odwraca zależności |
| wyjątek JDBC w porcie | niedozwolony - adapter tłumaczy błąd |

Kontrola tekstowa jest tania, ale zawodna; test architektury daje czytelne reguły; moduły Maven/JPMS są najsilniejsze, lecz najdroższe.

---

## Omówienie ćwiczenia 4

- SAME_DAY nie powinien automatycznie implementować `DeliveryPricePolicy` - kontrakt obiecuje determinizm bez efektów, a klient sieciowy tego nie spełnia (naruszenie LSP).
- Przypadek użycia definiuje port wyceny zewnętrznej z jawną semantyką błędu; adapter konwertuje jednostki, np. `movePointRight(3).longValueExact()`.
- Kwota w najmniejszych jednostkach wymaga waluty i wykładnika; adapter odrzuca nieoczekiwaną walutę i tłumaczy błędy SDK - typy SDK nie trafiają do wnętrza.
- Timeout należy do adaptera, a ponowienia wymagają analizy idempotencji; nazwanie wszystkiego `Strategy` nie usuwa różnicy semantycznej.
- Po usunięciu EXPRESS można zwinąć strategię do jednego kalkulatora, zachowując testy ceny - ale **porty zapisu i powiadomień zostają**, bo chronią inne decyzje.

---

## Sprawdzenie wiedzy

- Co jest przedmiotem DRY - tekst czy wiedza? Kiedy identycznych funkcji nie łączyć?
- Dlaczego prostsze rozwiązanie nie zawsze ma mniej klas? Czy YAGNI zabrania testów?
- Jak SRP definiuje powód zmiany? Czy każdy `switch` narusza OCP? Jakie warunki są kluczowe dla LSP?
- Dlaczego `@Autowired` nie dowodzi DIP? Czy interfejs jednometodowy jest automatycznie zgodny z ISP?
- Co interfejs robi ze sprzężeniem? Gdzie należy port wyjściowy? Czy Clean Architecture wymaga czterech warstw?
- Kiedy Strategy jest gorsza od `switch`? Dlaczego test kontraktowy nie dowodzi LSP? Kiedy usunąć wzorzec?

---

## Listy kontrolne (1/2)

**Przed wydzieleniem abstrakcji:** potrafię nazwać reprezentowaną wiedzę, znam aktualne wymaganie, właściciela zmian, prostsze rozwiązanie lokalne i sygnał ponownej oceny.

**Przed zastosowaniem SOLID:** SRP odnoszę do aktorów, dla OCP nazwałem oś rozszerzeń, kontrakt opisuje zachowanie dla LSP i jest sprawdzany testami kontraktowymi.

- Interfejs odpowiada roli klienta i należy do strony formułującej potrzebę.
- Importy prowadzą ku polityce, a interfejs nie powstaje tylko dlatego, że istnieje klasa.

---

## Listy kontrolne (2/2)

**Przed granicą architektoniczną:** granica oddziela znaczenie, tempo zmian lub ryzyko; wnętrze nie importuje typów frameworka; błędy są tłumaczone; regułę zależności da się sprawdzić automatycznie.

**Przed zastosowaniem wzorca:** opiszę problem bez nazwy wzorca, wariant istnieje dziś, znam prostszą alternatywę i wiem, kiedy wzorzec uprościć.

**Przed zakończeniem refaktoryzacji:**
- zachowanie z testów niezmienione, nowe kontrakty mają opisane gwarancje,
- brak starej i nowej reprezentacji tej samej wiedzy oraz funkcji „na przyszłość”,
- brak nowych cykli, zachowana semantyka błędów, pełny build na Javie 25 przechodzi.

---

## Podsumowanie - najważniejsze wnioski

- **DRY, KISS i YAGNI** równoważą centralizację wiedzy, prostotę i odraczanie spekulacji - żadna nie działa przez mechaniczne liczenie linii, klas czy interfejsów.
- **SOLID** pomaga analizować aktorów zmian, osie rozszerzeń, kontrakty substytucji, role klientów i kierunek zależności; wartość ma tylko wtedy, gdy obniża koszt konkretnej zmiany.
- **Spójność i sprzężenie**: interfejs zastępuje jedno sprzężenie innym i ma sens, gdy nowy kontrakt jest stabilniejszy i należy do właściwej strony.
- **Clean Architecture** chroni politykę przez kierowanie zależności do wnętrza; porty, adaptery i composition root są użyteczne tylko przy realnych decyzjach i ryzykach.
- **Wzorzec** to możliwy kierunek serii refaktoryzacji: najpierw chroń zachowanie, potem wprowadź najwęższe rozwiązanie i bądź gotów je uprościć.
