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
# Moduł 5. Refaktoryzacje hierarchii klas - warsztat praktyczny
Jak bezpiecznie przesuwać stan, zachowanie i relacje typów, nie łamiąc kontraktów, starych binariów ani integracji

---

## Agenda

1. Kontrakt hierarchii i model ryzyka
2. Semantyka dziedziczenia w Javie 25
3. Pull Up / Push Down (Method, Field)
4. Extract Superclass, Extract Subclass, Extract Interface
5. Collapse Hierarchy, Replace Inheritance with Composition
6. Zgodność, serializacja i integracje frameworkowe
7. Studium przypadku: hierarchia powiadomień + dodatkowe przypadki przed/po
8. Warsztat praktyczny, rozwiązania, sprawdzenie wiedzy, listy kontrolne

---

## 1.1. Hierarchia jest częścią zachowania

- Dziedziczenie nie służy tylko redukcji duplikacji - deklaruje, że podklasa może wystąpić **wszędzie tam, gdzie klient oczekuje nadklasy**.
- Klient polega nie tylko na metodach, ale też na warunkach wstępnych, wynikach, wyjątkach, efektach ubocznych i niezmiennikach.
- Poprawny podtyp przyjmuje wszystkie dane dopuszczone przez bazę, nie wzmacnia warunków wstępnych i nie osłabia gwarancji wyniku.
- Nie dodaje nieoczekiwanych wyjątków ani efektów oraz zachowuje obserwowalny kontrakt tożsamości, kolejności i współbieżności.
- **Zgodna sygnatura nie dowodzi zastępowalności**: `ReadOnlyAccount` z `withdraw` rzucającym zawsze wyjątek kompiluje się, ale nie jest poprawnym podtypem konta.

---

## 1.2. Współdzielenie implementacji a podtypowanie

- Przed utworzeniem nadklasy zadaj dwa osobne pytania: czy klasy mają **ten sam kod lub stan** oraz czy są **wariantami tego samego pojęcia** o wspólnym kontrakcie.
- Wspólny kod uzasadnia co najwyżej wydzielenie współpracownika, funkcji lub obiektu wartości - dopiero wspólny kontrakt uzasadnia dziedziczenie.
- Jeśli klasy łączy wyłącznie algorytm techniczny, kompozycja jest zwykle kierunkiem mniej zobowiązującym.
- Dobra nadklasa nie jest magazynem wszystkiego, co się powtarza - to stabilna abstrakcja, której operacje znaczą to samo dla każdej podklasy.

---

## 1.3. Warstwy zgodności

| Warstwa | Pytanie kontrolne |
| --- | --- |
| zachowanie wykonania | Czy wyniki, wyjątki, stan i efekty pozostają zgodne? |
| zgodność źródłowa | Czy kod klienta nadal się kompiluje? |
| zgodność binarna | Czy stary skompilowany klient nadal się linkuje? |
| kontrakt refleksyjny | Czy nazwy, deklarujący typ, członkowie i adnotacje są znajdowane? |
| zgodność serializacyjna | Czy nowy kod czyta dane zapisane przez starą wersję? |
| integracje | Czy ORM, DI, proxy, konfiguracja i generatory rozumieją model? |
| właściwości operacyjne | Czy zachowano blokady, transakcje, kolejność, wydajność? |

Zgodność binarna oznacza tylko, że stare `.class` się połączą - nie dowodzi zgodności źródłowej ani zachowania.

---

## 1.4. Mapa hierarchii przed zmianą

- Zinwentaryzuj wszystkie nadklasy, interfejsy i podklasy - także spoza bieżącego modułu i wcześniej skompilowane rozszerzenia.
- Zbierz członków `public`/`protected`, dostęp pakietowy, konstruktory, fabryki, kontenery DI i deserializatory tworzące obiekty.
- Znajdź wywołania przez typ bazowy, rzutowania, `instanceof`, `switch`, `super` oraz metody `static`, `private`, `final`, `synchronized`.
- Uwzględnij pola ukrywające inne pola, adnotacje, refleksję, mapowania ORM, format serializacji i granice modułów/pakietów.
- **Samo wyszukanie `extends` nie wystarczy** - hierarchia bywa zakodowana w konfiguracji, danych, `ServiceLoader`, ORM czy proxy.

---

## 1.5. Pętla bezpiecznej transformacji

1. Nazwij obserwowalny kontrakt, który ma pozostać niezmieniony, i dodaj najmniejszy wiarygodny test na jego poziomie.
2. Wykonaj **jeden** ruch metody, pola albo relacji typu, po czym skompiluj całą dotkniętą hierarchię.
3. Uruchom testy wszystkich podtypów przez typ bazowy.
4. Sprawdź klientów źródłowych, binarnych i integracyjnych proporcjonalnie do ryzyka.
5. Stary członek lub poziom hierarchii usuń dopiero po migracji.

Refaktoryzacja IDE zmienia deklaracje widoczne w modelu programu, ale **nie potwierdza** zgodności starych binariów, danych historycznych, nazw tekstowych ani zewnętrznych podklas.

---

## 2.1. Overriding i dynamiczna dyspozycja

- Kompilator wybiera sygnaturę, a JVM w runtime wybiera najbardziej szczegółową implementację według klasy odbiorcy - rzutowanie na nadtyp tego nie wyłącza.
- Metoda `static` jest ukrywana, a nie nadpisywana; `private` nie jest dziedziczona; `final` nie może zostać nadpisana.
- `super.method()` omija zwykłą dyspozycję i zaczyna wyszukiwanie od bezpośredniego nadtypu.
- Override nie może ograniczyć dostępności ani poszerzyć wyjątków kontrolowanych, ale może kowariantnie zawęzić typ wyniku.
- **Wniosek:** Pull Up Method może utworzyć nowy punkt dyspozycji - metoda w zewnętrznej podklasie nagle staje się nieplanowanym override.

---

## 2.2-2.3. Overloading i pola - wybór statyczny

- **Przeciążenie** wybierane jest w czasie kompilacji według typów kompilacyjnych; klasa runtime argumentu nie zmienia wyboru.
- Po Pull Up Method identyczny tekst kodu może wywołać inną metodę pomocniczą, bo zmieniają się widoczne typy i zestaw przeciążeń.
- Nowe przeciążenie może zmienić wybór w ponownie skompilowanym kliencie, a stary `.class` nadal wywoła dawną sygnaturę.
- **Pola nie są polimorficzne**: dostęp zależy od typu kompilacyjnego, a ukryte pole instancyjne to dwa niezależne sloty w jednym obiekcie.
- Rzutowanie lub `super.field` może czytać inny slot; scalenie pól usuwa niezależny stan - przenoś pola ze względu na znaczenie, nie zgodność nazwy i typu.

---

## 2.4. Konstruktory i inicjalizacja

- Konstruktory nie są dziedziczone; dodanie pierwszego jawnego konstruktora usuwa konstruktor domyślny.
- Java 25 dopuszcza ograniczony **prolog** przed `super(...)`/`this(...)`; potem konstruktor nadklasy, inicjalizatory pól i bloki bieżącej klasy, na końcu epilog.
- Inicjalizatory pól nadklasy zawsze kończą się przed inicjalizatorami podklasy - nowa składnia tego nie zmienia.
- Dyspozycja dynamiczna działa podczas konstrukcji: konstruktor bazy może wywołać override zanim pola podklasy zostaną zainicjalizowane.
- Pole `final` bez inicjalizatora musi być przypisane dokładnie raz w klasie deklarującej - **podklasa nie może przypisać odziedziczonego pola `final`**.

---

## 2.5-2.6. Monitory, `static` i granice pakietów

- `synchronized` instancyjne blokuje `this`; zamiana na delegowanie może przenieść blokadę z wrappera na delegata.
- `static synchronized` blokuje obiekt `Class` typu deklarującego - Pull Up takiej metody **zmienia monitor**.
- Przeniesienie pola `static` może zmienić moment inicjalizacji klasy (wyjątek: zmienne stałe wstawiane do kodu klienta przy kompilacji).
- Członek pakietowy nie jest dziedziczony przez podklasę z innego pakietu, więc metoda o tej samej sygnaturze nie jest override.
- `protected` poza pakietem działa tylko przez odbiorcę typu podklasy; przeniesienie klasy między pakietami może zmienić dziedziczenie mimo tej samej sygnatury.

---

## 2.7-2.8. Generyki, bridge methods i hierarchie `sealed`

- Sygnaturę przenoszonej metody analizuj **po erasure** - dwie różne deklaracje mogą mieć ten sam deskryptor i utworzyć name clash.
- Kowariantny override generuje syntetyczne metody bridge, widoczne dla refleksji i frameworków; porównuj wynik `javap -p -s -v` (deskryptory, flagi `bridge`, `synthetic`).
- `permits` obejmuje tylko bezpośrednie podtypy, a każdy z nich musi być `final`, `sealed` lub `non-sealed` - wstawienie pośredniej nadklasy zmienia oba poziomy.
- Nowy dozwolony wariant może być zgodny binarnie, ale stary wyczerpujący `switch` rzuci w runtime `MatchException`.
- Klasa `final`, rekord i enum nie dają się zwykle rozszerzyć; rekord nie może mieć nowej nadklasy, bo zawsze rozszerza `Record`.

---

## 3. Pull Up Method i Pull Up Field - cel i warunki

- Pull Up Method przenosi wspólne zachowanie do nadklasy, Pull Up Field zastępuje pola podklas jednym polem bazy.
- Celem nie jest opróżnienie podklas, lecz umieszczenie kontraktu na **najniższym poziomie, na którym jest prawdziwy dla wszystkich potomków**.
- Metodę przenieś, gdy implementacje realizują ten sam kontrakt, a różnice da się wyrazić istniejącym punktem polimorficznym.
- Metoda musi używać tylko stanu dostępnego w bazie, nie kolidować po erasure i nie tworzyć przypadkowego override; zachowane zostają `super`, przeciążenia i monitor.
- Identyczne ciało nie oznacza tego samego zachowania - np. `calculateLimit()` w kredycie i limicie technicznym może się rozwijać inaczej.

---

## 3.3. Procedura Pull Up Method

1. Uruchom test kontraktowy dla każdego podtypu i porównaj **kontrakty**, nie tylko tekst implementacji.
2. Ujednolić nazwy i sygnatury małymi krokami.
3. Przenieś potrzebne operacje pomocnicze albo wprowadź abstrakcyjny punkt rozszerzenia.
4. Umieść jedną implementację w nadklasie i usuń identyczne implementacje z podklas.
5. Uruchom testy przez referencję typu bazowego i konkretnego; sprawdź zewnętrzne podklasy, refleksję i zgodność binarną.

Nie wywołuj overridable metody z konstruktora tylko po to, by umożliwić Pull Up - powstaje hak działający na częściowo zainicjalizowanym obiekcie.

---

## 3.4-3.5. Pull Up Field - warunki i procedura

- Pola łączymy tylko przy tym samym znaczeniu domenowym, typie, cyklu życia, regułach walidacji i zgodnym momencie inicjalizacji.
- Pole w nadklasie powinno zwykle pozostać **`private`** - konstruktor bazowy i nazwane operacje lepiej chronią niezmienniki niż surowe pole `protected`.
- Procedura: znajdź wszystkie deklaracje tej nazwy w hierarchii, sprawdź odczyty, zapisy, rzutowania i `super.field`, ustal jednego właściciela stanu.
- Dodaj prywatne pole i parametr konstruktora bazy, przekaż wartości przez `super(...)`, przekieruj metody do wspólnego stanu.
- Pola podklas usuń po zniknięciu odwołań; na końcu sprawdź kolejność inicjalizacji, serializację i stare binaria.

---

## 3.6. Ryzyka Pull Up

- Pola `static` - dwa stany podklas mogą zostać scalone; inicjalizatory z efektami ubocznymi wykonają się wcześniej.
- Pola `final` - przypisanie musi zostać w dozwolonym kontekście klasy deklarującej; metody z `super` - zmienia się bezpośredni nadtyp.
- `static synchronized` zmienia monitor, a metody generyczne mogą zmienić deskryptor i bridge.
- Prywatne haki serializacji dotyczą konkretnego segmentu klasy, a frameworki z `getDeclaredField`/`getDeclaredMethod` zobaczą inny typ deklarujący.
- Pull Up Method może być zgodny binarnie przy tej samej nazwie, deskryptorze i dostępności - ale **to nie gwarantuje zgodności zachowania**.

---

## 4.1. Push Down Method i Push Down Field - cel

- Push Down przenosi operację lub stan z typu bazowego do gałęzi, która **rzeczywiście ich potrzebuje**, zawężając zbyt szeroki kontrakt.
- Sygnał: członek bazy ma sens tylko dla jednego rodzaju obiektu albo w części podklas pozostaje nieużywany.
- Sygnał: wymusza wartości `null`, puste kolekcje lub flagi bez znaczenia, albo w podklasach rzuca `UnsupportedOperationException`.
- Sygnał: jest używany tylko po sprawdzeniu `instanceof` i sprawia, że klient typu bazowego zna szczegóły jednego wariantu.

---

## 4.2. Push Down Method - warunki i sekwencja

- Metoda nie może być prawdziwą częścią kontraktu bazy - znajdź wywołania przez referencję bazową i wewnątrz nadklasy.
- Jeśli potrzebuje jej kilka podklas jednej gałęzi, trafia do **najbliższego wspólnego przodka**, a nie jest kopiowana do liści.
- Sekwencja: przestaw klientów na właściwy podtyp lub węższy interfejs → dodaj metodę w podklasie → opcjonalnie przekieruj bazę → testy → usuń metodę bazową.
- W opublikowanej bibliotece usunięcie metody bazowej łamie API: stary `.class` szuka jej w nadklasie, nie w podklasach.
- Samo pozostawienie deklaracji abstrakcyjnej nie chroni zgodności - stara podklasa bez implementacji zawiedzie w runtime.

---

## 4.3-4.4. Push Down Field i asymetria przesunięć

- Stan przesuwamy w dół, gdy ma znaczenie tylko dla jednej gałęzi, a nadklasa i pozostali potomkowie go nie używają (także przez refleksję).
- Procedura: najpierw przenieś **zachowanie** korzystające z pola, potem dodaj pole w gałęzi, przekieruj odczyty/zapisy i usuń pole z bazy.
- Równoczesne deklaracje w bazie i podklasie tworzą dwa niezależne sloty - publicznego pola nie da się zachować przez delegowanie.
- **Pull Up i Push Down nie są symetryczne**: stary odnośnik do metody podklasy może znaleźć implementację w nadklasie, ale nie odwrotnie.
- Dlatego ruch w dół publicznego członka częściej wymaga jawnej migracji API i planu dla danych historycznych.

---

## 5.1. Extract Superclass - cel i właściwy sygnał

- Tworzy wspólną nadklasę dla klas, które są **wariantami jednego pojęcia** i dzielą część stanu lub zachowania.
- Właściwym sygnałem jest wspólny kontrakt - podobne linie kodu to za mało.
- Pytaj: jak klient nazwie wspólną rolę, które operacje są prawdziwe dla każdego wariantu i czy każdy wariant zadziała przez referencję nadklasy.
- Sprawdź, czy wspólne pola mają identyczne znaczenie i cykl życia oraz czy klasy nie potrzebują już innej istotnej nadklasy.
- Jeśli nazwa wymaga słów `Base`, `Common` albo `Abstract`, to często znak ekstrakcji motywowanej tylko duplikacją.

---

## 5.2. Extract Superclass - procedura

1. Zabezpiecz osobno kontrakt każdej klasy i utwórz pustą abstrakcyjną nadklasę.
2. Dołączaj klasy **pojedynczo**, uruchamiając testy po każdej.
3. Przenieś wspólne pola (Pull Up Field), potem wspólne metody (Pull Up Method).
4. Różnice zostaw za punktami rozszerzeń tylko wtedy, gdy są częścią stabilnego algorytmu.
5. Dodaj test kontraktowy dla wszystkich konkretnych podtypów i zmigruj klientów, którzy rzeczywiście potrzebują wspólnego typu.

W przykładzie `Notification` ma prywatny wspólny stan, normalizację i podsumowanie; podklasy określają kanał i wysyłkę.

---

## 5.3-5.4. Konstruktory, fabryki i ograniczenia

- Nadklasa nie przenosi konstruktorów - każdą publiczną sygnaturę trzeba świadomie zachować lub zmigrować; wspólny stan idzie przez `super(...)`.
- Sprawdź, czy istniał niejawny konstruktor bezargumentowy i jaką miał dostępność oraz czy framework nie wymaga konstruktora `public`/`protected`.
- Zweryfikuj, że fabryki zachowują typ wyniku, kolejność walidacji i wyjątków, a konstruktor bazy nie wywołuje metod overridable.
- Java pozwala rozszerzyć tylko jedną klasę - gdy klasy mają już istotne nadklasy, lepszy bywa interfejs i kompozycja.
- Pusty nowy poziom może być zgodny binarnie, ale zmienia `getSuperclass`, typ deklarujący członków, miejsce adnotacji i założenia mapperów.

---

## 6. Extract Subclass

- Tworzy podklasę dla **stabilnego podzbioru instancji** z dodatkowym stanem lub odmiennym zachowaniem; reszta zachowuje prostszy typ bazowy.
- Sygnały: pole opcjonalne sensowne tylko dla części obiektów, wiele metod zaczyna się od tego samego sprawdzenia, konstrukcja kontrolowana przez fabryki.
- Nie nadaje się dla roli dynamicznej - gdy obiekt zmienia tryb w czasie życia, lepsze są State lub Strategy; wiele osi zmienności wskazuje na kompozycję.
- Procedura: zabezpiecz warianty → fabryka lub inwentaryzacja `new` → podklasa → zmiana punktów tworzenia → Push Down Method/Field → usuń warunek z bazy.
- Technika celowo **zmienia klasę runtime**: wpływa na `getClass()`, `equals`, ORM, JSON, serializację, DI, `instanceof` i `switch`.

---

## 7.1-7.2. Extract Interface - rola klienta i kontrakt

- Interfejs wyodrębnia spójny podzbiór operacji potrzebnych **określonej grupie klientów** - nie kopiujemy całego publicznego API klasy.
- Dobry interfejs ma nazwę rozpoznawalnej roli, minimalny zestaw operacji, opisane błędy i efekty, i nie istnieje tylko dla frameworka mockującego.
- `implements` wymusza zgodność sygnatur, ale **nie behawioralną** - wszystkie implementacje przechodzą ten sam test kontraktowy.
- Test obejmuje dane graniczne i `null`, typy i kolejność wyjątków, efekty uboczne, idempotencję, atomowość oraz `equals`/`hashCode`, jeśli klient ich używa.
- Pola interfejsu są niejawnie `public static final` - interfejs nie jest miejscem na przenoszony stan instancji.

---

## 7.3. Extract Interface - bezpieczna procedura

1. Zidentyfikuj konkretnych klientów i używany przez nich podzbiór API; zapisz kontrakt tego podzbioru.
2. Utwórz interfejs z minimalnymi metodami i dodaj `implements` **bez zmiany** publicznych sygnatur klasy.
3. Uruchom test kontraktowy i zmieniaj zależności klientów z klasy na interfejs pojedynczo.
4. Dopiero po migracji rozważ ograniczenie niepotrzebnego API klasy.

- Zmiana typu parametru z klasy na interfejs **zmienia deskryptor JVM** - w bibliotece zostaw stare przeciążenie delegujące.
- Zmiany typu wyniku nie obsłuży przeciążenie - potrzebna jest stara metoda albo nowa nazwa.

---

## 7.4. Metody domyślne

- Metoda `default` musi być poprawna dla każdej implementacji i używać wyłącznie operacji kontraktu - nie jest uniwersalnym sposobem dodawania zachowania.
- Metoda klasy wygrywa z `default`, a bardziej szczegółowy interfejs z mniej szczegółowym; konflikt dwóch niespokrewnionych interfejsów wymaga jawnego rozstrzygnięcia.
- `default` nie zastąpi `equals`, `hashCode` ani `toString`, a sekwencja wywołań w niej nie staje się atomowa.
- Dla interfejsu używanego z lambdą rozważ `@FunctionalInterface` - druga metoda abstrakcyjna odbierze mu funkcyjność.
- Nowa metoda abstrakcyjna w opublikowanym interfejsie może zakończyć się `AbstractMethodError` na starych implementacjach.

---

## 8. Collapse Hierarchy

- Scala nadklasę i podklasę, gdy rozróżnienie nie reprezentuje już odrębnego kontraktu, wariantu domenowego ani używanego punktu rozszerzenia.
- Sygnały: podklasa nie dodaje stanu ani zachowania, override tylko woła `super`, nadklasa ma jednego potomka, a historyczny powód podziału zniknął.
- Mała liczba linii nie wystarcza - pusty typ może być markerem, punktem rozszerzenia, granicą modułu, typem w protokole lub kontraktem DI.
- Procedura: wybierz typ-właściciela kontraktu, znajdź konstrukcje, rzutowania, `instanceof` i refleksję, przenieś członków, przekieruj DI i mapowania.
- Usunięcie publicznej klasy łamie klientów - w bibliotece przejściowo zostaw cienki, przestarzały typ zgodności i zaplanuj wydanie łamiące.

---

## 9.1. Replace Inheritance with Composition - kiedy?

- Gdy podklasa **nie jest zastępowalnym wariantem** nadklasy, a `extends` służy głównie do ponownego użycia niewielkiej części implementacji.
- Gdy odziedziczone API jest znacznie szersze niż potrzeby klientów albo podklasa musi blokować odziedziczone operacje.
- Gdy zachowanie ma być wymienne w czasie życia obiektu lub trzeba połączyć kilka niezależnych zachowań.
- Gdy zmiany w nadklasie nie powinny automatycznie poszerzać API potomka.
- Dziedziczenie pozostaje właściwe przy prawdziwym, stabilnym kontrakcie podtypowania - celem nie jest likwidacja wszystkich hierarchii.

---

## 9.2-9.3. Procedura i granice zgodności

- Zinwentaryzuj **całe** odziedziczone API, zabezpiecz zachowanie testami charakterystyki i określ wąski kontrakt, który ma zostać.
- Dodaj pole delegata o jednoznacznej własności i cyklu życia, wprowadzaj jawne metody przekazujące tylko potrzebne operacje.
- Zmigruj klientów zależnych od nadklasy, usuń `extends` i dodaj testy specyficzne dla delegowania.
- Po usunięciu `extends ArrayList<String>` obiekt przestaje być `List` - przypisania, rzutowania i parametry `List` przestają działać.
- To poprawna migracja w kontrolowanej aplikacji, ale **nie zgodny zamiennik** publicznego `List`; tam trzeba wiernie delegować pełny interfejs.

---

## 9.4. Pułapki delegowania

- Odziedziczona metoda wywoływała hook na tym samym `this`, a delegat wywoła własny hook; `super.method()` nie ma wiernego odpowiednika.
- Metoda fluent lub callback mogą zwrócić/przekazać **delegata zamiast wrappera**.
- `synchronized` blokuje monitor delegata; odziedziczone `equals`/`hashCode` znikają; członkowie `protected` nie są dostępni przez kompozycję.
- Współdzielony delegat może nieplanowanie połączyć stan kilku wrapperów, a kolejność konstrukcji się zmienia.
- Test kontraktowy nie wykryje liczby wywołań, kolejności efektów, tożsamości `this` ani monitora - potrzebne recording fake, spy, callback i test współbieżności.

---

## 10.1. Macierz typowych skutków

| Transformacja | Typowe ryzyko dla publicznego kontraktu |
| --- | --- |
| Pull Up Method | przypadkowy override, inne `super`, przeciążenia, monitor `static` |
| Pull Up Field | scalenie slotów, wcześniejsza inicjalizacja, segment serializacji |
| Push Down Method/Field | klient bazowy traci członka; kopia pola = dwa stany |
| Extract Superclass | nowy nadtyp, konstruktory, refleksja, ORM, serializacja |
| Extract Subclass | inny typ runtime, fabryki, mapowanie, postać danych |
| Extract Interface | deskryptory parametrów/wyników, metody domyślne |
| Collapse Hierarchy | usunięta nazwa klasy, utrata nadtypu i konfiguracji |
| Replace Inh. with Composition | utrata przypisywalności, API, hooków, monitora |

---

## 10.2-10.3. Test zgodności binarnej, refleksja i adnotacje

- Pełny test biblioteki: skompiluj klienta przeciw `v1`, zachowaj jego `.class`, uruchom z `v2`, osobno skompiluj ponownie przeciw `v2` i porównaj oba uruchomienia.
- Sam pełny build sprawdza głównie zgodność źródłową - nie odtwarza sytuacji wtyczki skompilowanej przeciw starej wersji.
- Przeniesienie członka zmienia wyniki `getDeclaredFields/Methods()`, `Method.getDeclaringClass()`, `getSuperclass()` oraz liczbę metod bridge.
- `@Inherited` działa tylko dla adnotacji klas z nadklasy - nie z interfejsów i nie dla metod; oceniaj według sposobu skanowania frameworka.
- W systemie modułowym publiczny interfejs musi być w eksportowanym pakiecie, a głęboka refleksja może wymagać otwarcia pakietu.

---

## 10.4. Serializacja Javy

- Domyślna postać serializowana jest opisana **osobno dla każdego poziomu hierarchii** - przeniesienie pola zmienia segment strumienia.
- Bez jawnej migracji historyczna wartość może zostać odrzucona, a nowe pole dostanie wartość domyślną.
- Stały `serialVersionUID` nie przenosi stanu między segmentami i nie naprawia zmienionego modelu obiektu.
- Testuj na zachowanym pliku `.ser` z `v1` odczytywanym przez `v2`; stabilny format zapewnij przez `serialPersistentFields`, `writeObject`/`readObject` lub serialization proxy.
- Prywatne haki serializacji nie podlegają overridingowi; przy deserializacji uruchamia się konstruktor pierwszej **nieserializowalnej** nadklasy.

---

## 10.5-10.6. ORM, DI, proxy i testy wysokiego ryzyka

- Zmiana hierarchii encji to migracja modelu trwałego: strategia dziedziczenia, dyskryminator, tabele, zapytania polimorficzne, lazy loading, proxy.
- Test ORM z rzeczywistym dostawcą: zapis, `flush`, czyszczenie kontekstu, ponowny odczyt, proxy i zapytanie polimorficzne.
- Kontener DI zależy od nazw, konstruktorów, adnotacji i typów proxy - uruchom kontekst aplikacji, a nie tylko test z ręcznym `new`.
- Minimalny zestaw: test kontraktowy dla każdego podtypu, test różnicowy z niezależnym oczekiwaniem, test wyboru pól i kolejności konstrukcji.
- Do tego `javap`, stary `.class` z nową biblioteką, historyczny plik serializacji, skanowanie adnotacji i wszystkie wyczerpujące `switch` dla `sealed`.

---

## 11.1. Studium przypadku: kontrakt szkoleniowy

- System obsługuje powiadomienia **e-mail i SMS**: oba wymagają niepustego identyfikatora, nadawcy i treści oraz usuwają białe znaki.
- Nadawca normalizowany jest do wielkich liter niezależnie od ustawień regionalnych, a wynik wysyłki to stabilne podsumowanie z `SENT` lub `FAILED`.
- Tylko udana wysyłka SMS może zawierać `RECEIPT` - mimo to kod początkowy kopiuje pole i metodę potwierdzenia także do e-maila.
- Kontrakt obejmuje publiczne konstruktory, wyniki, typy walidacji i kolejność elementów tekstu - nie obejmuje refleksji ani serializacji.
- Abstrakcyjna `Notification` ma dostęp pakietowy, a klasy konkretne są finalne - ta jawna granica pozwala zrobić Push Down Field jako refaktoryzację.

---

## 11.2. Etap 0: niezależne klasy i duplikacja

```java
public final class SmsNotification {        // EmailNotification - prawie identyczna
    private final String messageId, senderId, body;
    private final boolean deliveryReceipt;   // w e-mailu też jest, choć bez znaczenia

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
}
```

Duplikacja wskazuje kierunek, ale nie dowodzi poprawnej nadklasy - najpierw test charakterystyki.

---

## 11.2. Test charakterystyki jako punkt odniesienia

- `NotificationCharacterizationTest` zapisuje niezależne oczekiwania dla obu kanałów, sukcesu, błędu, flagi potwierdzenia i walidacji.
- Przykład: `" msg-1 ", " ops ", " Deployment ready "` → `msg-1|OPS|Deployment ready|EMAIL|SENT`.
- Dla SMS z potwierdzeniem wynik to `...|SMS|SENT|RECEIPT`, a przy błędzie `...|SMS|FAILED` - bez znacznika.
- Walidacja: pusta wartość rzuca `IllegalArgumentException`, `null` rzuca `NullPointerException`.
- Test nie ocenia, czy zachowanie jest idealne - oddziela **ruch struktury** od ewentualnych późniejszych zmian funkcjonalnych.

---

## 11.3. Etap 1: Extract Superclass oraz Pull Up

```java
abstract class Notification {
    private final String messageId, senderId, body;
    private final boolean deliveryReceipt;   // celowo niedoskonały stan przejściowy

    public final String summary() {
        return messageId + "|" + senderId + "|" + body + "|" + channel();
    }
    protected final String dispatchResult(boolean successful) {
        return summary() + (successful ? "|SENT" : "|FAILED");
    }
    protected final String appendReceipt(String result) { ... }
    protected abstract String channel();
    public abstract String dispatch(boolean successful);
}
```

`channel()` to świadomy punkt polimorficzny; `final` na `summary()` dokumentuje wspólny algorytm (w bibliotece - tylko po sprawdzeniu zewnętrznych override).

---

## 11.4. Etap 2: Push Down cechy SMS

```java
public final class SmsNotification extends Notification {
    private final boolean deliveryReceipt;           // stan tylko tam, gdzie ma sens

    public SmsNotification(String id, String sender, String body, boolean receipt) {
        super(id, sender, body);
        this.deliveryReceipt = receipt;
    }
    @Override public String dispatch(boolean successful) {
        String result = dispatchResult(successful);
        return successful ? appendReceipt(result) : result;
    }
    private String appendReceipt(String r) { return deliveryReceipt ? r + "|RECEIPT" : r; }
}
```

- Testy pokazały, że flaga nie wpływa na e-mail - baza odzyskuje kontrakt prawdziwy dla wszystkich podtypów.
- E-mail zachowuje przejściowy konstruktor 4-argumentowy (ignoruje flagę) i dostaje docelowy 3-argumentowy.

---

## 11.5. Etap 3: Extract Interface

```java
@FunctionalInterface
public interface OutboundNotification {
    String dispatch(boolean successful);
}

public List<String> dispatchAll(List<? extends OutboundNotification> notifications,
                                boolean successful) {
    Objects.requireNonNull(notifications, "notifications must not be null");
    var validated = notifications.stream()
            .map(n -> Objects.requireNonNull(n, "notification must not be null"))
            .toList();                                   // walidacja przed wysyłką
    return validated.stream().map(n -> n.dispatch(successful)).toList();
}
```

- `NotificationBatch` potrzebuje tylko `dispatch` - bez identyfikatora, podsumowania, konstruktorów i `channel()`.
- Walidacja całej listy przed pierwszą wysyłką zapobiega częściowemu efektowi.

---

## 11.6. Test równoważności wszystkich etapów

- `NotificationHierarchyEquivalenceTest` porównuje każdy etap (`stage0`-`stage3`) z **niezależnym oczekiwaniem**, a nie wersję „przed” z „po”.
- Macierz obejmuje obie wartości dawnej flagi e-mail, obie wartości potwierdzenia SMS, sukces, błąd oraz typy wyjątków dla pustej wartości i `null`.
- Samo `before.equals(after)` byłoby niewystarczające, bo obie wersje mogłyby zawierać ten sam błąd.
- `OutboundNotificationContractTest` uruchamia te same reguły przez interfejs i sprawdza niemodyfikowalność listy wynikowej.
- Test odrzucenia `null` potwierdza, że błąd jest wykrywany **przed** pierwszym wywołaniem `dispatch`.

---

## 12.1. Extract Subclass: zadanie zaplanowane

```java
public class DeliveryJob {
    DeliveryJob() { }                                   // konstruktor pakietowy
    public static DeliveryJob immediate() { return new DeliveryJob(); }
    public static DeliveryJob scheduled(Instant at) { return new ScheduledDeliveryJob(at); }
    public String dispatchAt(Instant now) { Objects.requireNonNull(now); return "SENT"; }
}

public final class ScheduledDeliveryJob extends DeliveryJob {
    private final Instant scheduledAt;
    @Override public String dispatchAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        if (now.isBefore(scheduledAt)) return "WAITING_UNTIL " + scheduledAt;
        return super.dispatchAt(now);
    }
}
```

Przed: jedna klasa z `Optional<Instant>`. Po: fabryka nadal zwraca `DeliveryJob`, a `now == scheduledAt` oznacza `SENT`.

---

## 12.1. Extract Subclass - decyzje projektowe

- `scheduledAt` jest niezmienne, więc wariant **nie zmienia się w czasie życia** obiektu - to warunek sensowności podklasy.
- Fabryka zachowuje typ wyniku `DeliveryJob`, a specjalna podklasa nie jest częścią publicznego API konstrukcji.
- `super.dispatchAt(now)` świadomie ponownie używa zachowania bazowego po osiągnięciu terminu.
- `DeliveryJobEquivalenceTest` sprawdza zadanie natychmiastowe i zaplanowane przed, dokładnie w i po terminie - bez wymagania tej samej klasy runtime.
- Gdyby zadanie można było wielokrotnie planować, wstrzymywać i wznawiać, lepszy byłby obiekt polityki albo State.

---

## 12.2. Collapse Hierarchy: zbędny formatter

```java
// przed
public class LegacyNotificationFormatter {
    public String format(String recipient, String message) { ... }
}
public final class NotificationFormatter extends LegacyNotificationFormatter { }

// po
public final class NotificationFormatter {
    public String format(String recipient, String message) {
        Objects.requireNonNull(recipient, "recipient must not be null");
        Objects.requireNonNull(message, "message must not be null");
        return "To: " + recipient + "\nMessage: " + message;
    }
}
```

- Zachowanie trafia do nazwy używanej przez klientów; test sprawdza dokładny tekst, również z polskimi znakami.
- Usunięcie publicznej `LegacyNotificationFormatter` i dodanie `final` byłyby w bibliotece zmianami łamiącymi.

---

## 12.3. Replace Inheritance with Composition: lista odbiorców

```java
// przed: public final class RecipientList extends ArrayList<String> { snapshot() }
public final class RecipientList implements Iterable<String> {
    private final List<String> recipients = new ArrayList<>();

    public boolean add(String r)         { return recipients.add(r); }
    public boolean remove(Object r)      { return recipients.remove(r); }
    public boolean contains(Object r)    { return recipients.contains(r); }
    public int size()                    { return recipients.size(); }
    @Override public Iterator<String> iterator() {
        return Collections.unmodifiableList(recipients).iterator();
    }
    public List<String> snapshot() {
        return Collections.unmodifiableList(new ArrayList<>(recipients));
    }
}
```

Jawnie zadeklarowany wąski podzbiór operacji; `Iterable` dodaje jeszcze `forEach` i `spliterator`.

---

## 12.3. Co zmiana świadomie pomija

- `RecipientListEquivalenceTest` sprawdza duplikaty, kolejność iteracji, `remove(Object)`, izolację migawek i niezależność stanu dwóch wrapperów.
- Test potwierdza też, że iterator wersji po zmianie **nie pozwala ominąć** jawnego API mutacji.
- Poza kontraktem pozostają: przypisywalność do `List<String>`, `add(int, E)`, `set`, `clear`, `sort`, `remove(int)` i inne odziedziczone metody.
- Tracone są też `Iterator.remove()`, semantyka `equals`/`hashCode` kolekcji, charakterystyki `Spliterator`, serializowalność i klasa runtime.
- Wycofanie tych właściwości jest dopuszczalne tylko po potwierdzeniu, że nie należą do kontraktu, albo po zaplanowanej migracji klientów.

---

## 12.4. Program demonstracyjny

- `Module5Examples` uruchamia wszystkie przykłady modułu i porównuje wersje przed i po dla każdej techniki.
- Oczekiwany wynik potwierdza równoważność etapów hierarchii, Extract Subclass, Collapse Hierarchy i kompozycji.
- Partia z e-mailem i SMS przez `NotificationBatch` pokazuje wspólną rolę `OutboundNotification`.

```text
Hierarchy stages equivalent: true
Extract subclass equivalent: true
Collapse hierarchy equivalent: true
Composition client behavior equivalent: true
Batch results: [mail-1|OPS|Ready|EMAIL|SENT, sms-1|OPS|Ready|SMS|SENT|RECEIPT]
```

---

## 13. Warsztat praktyczny - zasady

- Każde ćwiczenie zaczyna się od **działających testów** i kończy krótką retrospektywą.
- Wykonuj małe kroki i uruchamiaj testy po każdym z nich.
- Nie łącz w jednym kroku ruchu członka, zmiany zachowania i usunięcia publicznego API.
- Kod ćwiczeń: pakiety `pl.training.module5.stage0…stage3`, `extractsubclass`, `collapse`, `composition` (warianty `before`/`after`).

---

## Ćwiczenie 1: Extract Superclass i Pull Up

**Start:** `stage0`. **Cel:** wspólna `Notification` z zachowaniem wyników i walidacji obu kanałów.

- Uruchom `NotificationCharacterizationTest`, wypisz wspólne pola i metody i oceń ich znaczenie, cykl życia i kontrakt.
- Utwórz pustą abstrakcyjną `Notification`, dołącz jedną klasę, przenieś stan przez konstruktor bazowy, normalizację, `messageId` i podsumowanie.
- Wprowadź minimalny punkt polimorficzny kanału, dołącz drugą klasę i uruchom test różnicowy.
- Pytania: dlaczego pola bazowe są prywatne? czy `deliveryReceipt` należy do kontraktu? co zmieniłoby wywołanie `channel()` z konstruktora?
- Akceptacja: te same sygnatury konstruktorów, jedno źródło każdej reguły, brak surowych pól `protected`.

---

## Ćwiczenie 2: Push Down i Extract Interface

**Start:** `stage1`. **Cel:** usunąć potwierdzenie z kontraktu bazowego i utworzyć rolę dla klienta partii.

- Znajdź odczyty `deliveryReceipt` i wywołania `appendReceipt`; potwierdź testem, że e-mail ignoruje flagę.
- Przenieś pole i metodę do `SmsNotification`, usuń parametr z konstruktora bazy, zachowaj przejściowy konstruktor e-mail.
- Zdefiniuj minimalny interfejs dla `NotificationBatch`, dodaj `implements` bez zmiany sygnatur i przestaw klienta na interfejs.
- Zaprojektuj ścieżkę wycofania starego konstruktora dla publicznej biblioteki.
- Akceptacja: brak stanu SMS w bazie, `NotificationBatch` nie zależy od klasy abstrakcyjnej, test kontraktowy przez `OutboundNotification`.

---

## Ćwiczenie 3: Extract Subclass i Collapse Hierarchy

**Część A** (`extractsubclass.before`):
- Zapisz tabelę wyników zadania natychmiastowego i zaplanowanego przed, w i po terminie; utwórz `ScheduledDeliveryJob`.
- Zmień tylko fabrykę wariantu zaplanowanego, przenieś `scheduledAt` i warunek, usuń `Optional` z bazy; wypisz kontrakty celowo niezachowane.

**Część B** (`collapse.before`):
- Ustal nazwę używaną przez klientów, przenieś zachowanie, usuń zbędny poziom i uruchom test formatu oraz walidacji.
- Zaproponuj kompatybilny etap przejściowy, gdyby oba typy były publiczne.

**Akceptacja:** jawny test terminu granicznego, stan specjalny tylko w podtypie, rozpoznana zmiana klasy runtime.

---

## Ćwiczenie 4: Replace Inheritance with Composition

**Start:** `composition.before`. **Cel:** zastąpić dziedziczenie po `ArrayList` wąską fasadą bez przypadkowej obietnicy pełnego `List`.

- Wyszukaj metody wywoływane przez klientów i zapisz skrypt zachowania: duplikaty, kolejność, usuwanie, migawka.
- Dodaj prywatny delegate, wprowadzaj po jednej metodzie przekazującej, zaimplementuj `Iterable<String>` i usuń `extends`.
- Sprawdź, co przestało się kompilować; oceń `equals`, `hashCode`, serializację, iterator i własność delegata.
- Przygotuj wariant planu dla publicznej klasy, której klienci wymagają pełnego `List`.
- Akceptacja: każdy wrapper ma własny delegate, migawki są izolowane, znasz różnicę `remove(Object)` vs `remove(int)`.

---

## Ćwiczenie: retrospektywa po warsztacie

Zespół odpowiada na pięć pytań:

1. Jaki kontrakt był chroniony?
2. Który krok był mechaniczny, a który wymagał decyzji projektowej?
3. Jaki test wykryłby najgroźniejszą regresję?
4. Której warstwy zgodności nie sprawdziły testy jednostkowe?
5. Czy końcowa hierarchia opisuje domenę, czy tylko ponownie używa kodu?

---

## 14. Rozwiązania - ćwiczenia 1 i 2

- **Ćw. 1** (wynik = `stage1`): pusta `Notification` → prywatne pola w konstruktorze bazy → `normalized` → `messageId()` → `channel()` + `summary()` → `dispatchResult`.
- `channel()` to jedyna różnica potrzebna wspólnemu algorytmowi - nie trzeba getterów dla wszystkich pól ani pól `protected`.
- `deliveryReceipt` w bazie zachowuje wyniki, ale jest sygnałem złego modelu: e-mail przyjmuje wartość bez znaczenia.
- **Ćw. 2** (wynik = `stage2`/`stage3`): najpierw przenieś zachowanie `appendReceipt`, potem stan - baza przestaje zależeć od pola przed jego usunięciem.
- `OutboundNotification` zawiera tylko `dispatch`; w publicznej bibliotece stary deskryptor klienta zostaje jako przeciążenie delegujące.

---

## 14. Rozwiązania - ćwiczenia 3 i 4

- **Ćw. 3:** krytyczna jest zmiana punktu tworzenia **przed** Push Down - obiekt zaplanowany musi już być `ScheduledDeliveryJob`.
- `now.isBefore(scheduledAt)` oznacza, że równość z terminem daje `SENT`; bez testu granicznego zmiana operatora przeszłaby niezauważona.
- Collapse: pozostaje `NotificationFormatter`, bo tej nazwy używają klienci; w publicznym API - cienka klasa zgodności lub wydanie łamiące.
- **Ćw. 4:** własny delegate zapobiega dzieleniu stanu; ujawnienie `remove(int)` sprawiłoby, że literał liczbowy usuwałby po indeksie.
- Dla publicznego `List`: zachowaj dziedziczenie, zaimplementuj pełny `List` z precyzyjnymi testami albo wprowadź nowy wąski typ i wydanie łamiące.

---

## 15. Sprawdzenie wiedzy (1/2)

1. Dlaczego podobny kod nie wystarcza do Extract Superclass? - *nadklasa deklaruje wspólne pojęcie i kontrakt, podobieństwo może być przypadkowe.*
2. Czym overriding różni się od overloading? - *override wybierany w runtime wg klasy odbiorcy, overload w kompilacji wg typów statycznych.*
3. Czy rzutowanie na nadtyp wyłącza dyspozycję dynamiczną? - *nie; omija ją m.in. jawne `super`.*
4. Jaki monitor blokuje `static synchronized`? - *obiekt `Class` klasy deklarującej, więc przeniesienie zmienia monitor.*
5. Dlaczego Push Down publicznej metody łamie stare binaria? - *stary odnośnik wskazuje nadklasę, a JVM nie szuka w podklasach.*

---

## 15. Sprawdzenie wiedzy (2/2)

6. Kiedy Extract Subclass przegrywa ze State/Strategy? - *gdy rola zmienia się w czasie życia lub jest kilka niezależnych osi zmienności.*
7. Dlaczego zmiana parametru z klasy na interfejs łamie stare `.class`? - *typ parametru jest częścią deskryptora metody.*
8. Kiedy pusta podklasa ma znaczenie? - *jako marker, punkt rozszerzenia, typ w protokole, cel DI lub część formatu danych.*
9. Dlaczego `serialVersionUID` nie wystarcza po Pull Up Field? - *stan zapisywany jest per poziom hierarchii; UID nie migruje wartości.*
10. Dlaczego `RecipientList` po kompozycji nie zastąpi `List`? - *traci przypisywalność, odziedziczone operacje, `equals`/`hashCode` i serializację.*

---

## 16. Listy kontrolne - przed zmianą i ruchy członków

- **Przed zmianą:** umiem nazwać kontrakt i klientów, znam wszystkie podtypy (także zewnętrzne), punkty tworzenia, `super`, `instanceof`, rzutowania.
- Sprawdziłem pola o tych samych nazwach, metody `static`/`final`/`synchronized`, pakiety i `protected`, erasure i bridge, refleksję, ORM, DI, serializację.
- **Pull Up:** ten sam kontrakt i znaczenie pól, te same przeciążenia i `super`, brak przypadkowego override, pole bazowe prywatne, brak scalania pól `static`.
- **Push Down:** członek nie należy do kontraktu bazy, klienci przestawieni na podtyp, członek trafia do najbliższej wspólnej gałęzi, jedno źródło stanu.
- Publiczny członek ma plan wycofania, a dane historyczne - plan migracji.

---

## 16. Listy kontrolne - ekstrakcje, scalanie, kompozycja

- **Extract Superclass/Subclass:** nazwa opisuje pojęcie, podtypy spełniają kontrakt, konstruktory i fabryki świadomie zachowane, cecha podklasy stabilna.
- **Extract Interface:** rola konkretnych klientów, minimalny podzbiór, wspólny test kontraktowy, poprawne `default`, plan dla zmian deskryptorów.
- **Collapse Hierarchy:** usuwany poziom nie jest markerem ani punktem rozszerzenia, wybrana właściwa nazwa, klienci starej nazwy mają warstwę zgodności.
- **Composition:** podtypowanie fałszywe, znane całe odziedziczone API, delegate ma jasną własność, sprawdzone hooki, fluent `this`, monitor, `equals`, serializacja.
- **Na koniec modułu:** testy charakterystyki, kontraktowe i różnicowe przechodzą, brak dwóch źródeł stanu, zmiana łamiąca jest nazwana i zaplanowana osobno.

---

## Podsumowanie - najważniejsze wnioski

- Refaktoryzacja hierarchii jest bezpieczna dopiero, gdy **struktura typów odpowiada kontraktom**, a testy obserwują właściwy poziom systemu.
- Pull Up centralizuje prawdziwie wspólny stan i zachowanie; Push Down usuwa z bazy cechy właściwe tylko części obiektów.
- Extract Superclass/Subclass zmieniają model typów i wymagają kontroli konstrukcji oraz zastępowalności; Extract Interface wydziela rolę klienta.
- Collapse Hierarchy usuwa rozróżnienie bez znaczenia; Replace Inheritance with Composition ogranicza fałszywe podtypowanie i nadmiarowe API.
- W Javie metody, przeciążenia i pola mają różne reguły wyboru - poprawna składnia nie wystarcza, gdy kontrakt obejmuje binaria, refleksję, serializację czy ORM.
- Rozdzielaj trzy decyzje: **gdzie należy stan i zachowanie, jaki typ widzi klient i które formy zgodności muszą zostać zachowane**.
