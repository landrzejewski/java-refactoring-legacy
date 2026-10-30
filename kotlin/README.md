# Refaktoryzacja do wzorców - ćwiczenia utrwalające CineLegacy w Kotlinie

Odpowiednik ćwiczeń Java z `src/main/java/pl/training/workshop/exercises/**` (oraz testów z `src/test/java/pl/training/workshop/exercises/**`) napisany w Kotlinie (JVM).

## Wymagania

- JDK 21 lub nowszy i Maven (Kotlin 2.4 pobiera się jako plugin Mavena)

## Budowanie i testy

```bash
cd kotlin
mvn test
```

## Struktura

Pakiety są takie same jak w Javie, więc kroki przenosi się tym samym mechanizmem (zamiana nazwy pakietu):

| Java | Kotlin | Testy |
|---|---|---|
| `pl.training.workshop.shared.Money` | `src/main/kotlin/pl/training/workshop/shared/Money.kt` | |
| `pl.training.workshop.exercises.e03_notifications.start` | `src/main/kotlin/pl/training/workshop/exercises/e03_notifications/start` | `src/test/kotlin/pl/training/workshop/exercises/e03_notifications/E03EquivalenceTest.kt` |
| `support/Scene` (JUnit `@TestFactory`) | `src/test/kotlin/pl/training/workshop/support/Scene.kt` (zwraca `List<DynamicTest>`) | |
| `src/main/resources/exercises` | `src/main/resources/exercises` | |

Każde ćwiczenie ma katalog `start` (kod wyjściowy) i `step1..N` (snapshoty po kolejnych krokach, różni się tylko pakiet). Klasy wspólne dla wszystkich wariantów (stabilny kontrakt ćwiczenia, np. `ImportRequest`) leżą w pakiecie ćwiczenia, poza `start`/`stepN`.

## Konwencje tłumaczenia Java → Kotlin

- Nazwy klas, metod, pól i testów bez zmian; jeden plik `.kt` na klasę Javy, komentarze KDoc z tą samą treścią.
- `record` → `data class` (z własnym `toString()` tam, gdzie Java go nadpisuje); `final class` → zwykła klasa (w Kotlinie domyślnie final); klasa i metoda otwarta w Javie → `open`/`abstract`; metoda `final` w klasie abstrakcyjnej → metoda bez `open`.
- Pola i metody `static` → `companion object` albo `object` (klasy narzędziowe z prywatnym konstruktorem); `Optional<T>` → `T?`.
- Interfejsy funkcyjne implementowane lambdą → `fun interface` albo typ funkcyjny `(A) -> B`.
- Wyjątki JVM bez zmian (`IllegalArgumentException`, `IllegalStateException`, `UnsupportedOperationException`, ...), te same komunikaty.
- `BigDecimal` i `java.time` bez zmian; `Money` ma operatory `+`, `-`, `*` obok metod `plus`/`minus`/`times`.
- Niemodyfikowalne kolekcje: `List<T>` z Kotlina jest tylko do odczytu w typie; tam, gdzie test Javy sprawdza `UnsupportedOperationException`, kod zwraca kolekcję niemodyfikowalną także w czasie działania (`java.util.List.copyOf`, `Collections.unmodifiableList`).
- Testy refleksyjne używają `java.lang.reflect` jak w Javie i pomijają składowe generowane przez kompilator Kotlina (`component1`, `copy`, gettery właściwości, `Companion`) - takie miejsca opisuje komentarz "Różnica względem Javy".
- Widoczność pakietowa Javy → `internal`. W bajtkodzie `internal` jest publiczne, więc testy sprawdzające ukrycie klas (e07, e10) czytają widoczność przez `kotlin-reflect` (`KClass.visibility`, zależność tylko testowa).
- Klasa zagnieżdżona z prywatnym konstruktorem używanym przez klasę zewnętrzną (Java widzi go, Kotlin nie) → konstruktor `internal` (budowniczy w e08) albo prywatna implementacja pustego interfejsu (memento w e04).
- Singletony bezstanowe (`INSTANCE` w Javie) → `object`.

## Testy (JUnit 6)

- `@TestFactory` zwraca `List<DynamicTest>` z `Scene`, `assertThrows<T> { }` z `org.junit.jupiter.api`.
- Testy równoważności używają tylko publicznego API wspólnego dla wszystkich wariantów, więc kompilują się także po skopiowaniu dowolnego kroku do `start`. Testy sprawdzające "pułapkę" w `start` są pomijane (`assumeTrue`), gdy w `start` jest już rozwiązanie; w testach, które obok pułapki sprawdzają też rozwiązanie, sama asercja na `start` jest warunkowa (`assumingThat`).

## Pokaz

Ten sam skrypt co w Javie, z opcją języka:

```bash
scripts/warsztat.sh --lang kt list exercises
scripts/warsztat.sh --lang kt test exercises/e03
scripts/warsztat.sh --lang kt next exercises/e03
scripts/warsztat.sh --lang kt diff exercises/e03 0 1
scripts/warsztat.sh --lang kt reset exercises/e03
```
