# ADR-0007. Cennik jako czysty moduł domenowy

- **Status:** Zaakceptowana
- **Data:** 2026-03-02
- **Właściciel:** zespół Sprzedaż biletów
- **Zastępuje:** brak

## Kontekst

Cennik wycięty z `CinemaManager` ma trafić za abstrakcję i być weryfikowany w trybie shadow
(scena s03). Obecnie `TicketPricing` sam wysyła mail o rabacie grupowym i liczy kwoty w `number`
(liczba zmiennoprzecinkowa). Tryb shadow wymaga czystego obliczenia bez efektów ubocznych,
a rozliczenia z dystrybutorem nie tolerują błędów zaokrągleń.

## Decyzja

- **R1.** Katalog `pricing` nie zależy od katalogu `notification`. Cennik zwraca wynik
  (np. informację o przyznanym rabacie), a o powiadomieniu decyduje warstwa aplikacji.
- **R2.** Katalog `pricing` nie używa `number` do kwot (`total`, `sum`, `price`, `amount`,
  `unitPrice`) - wyłącznie `Money` (skala 2, HALF_UP). Liczby sztuk mogą zostać `number`.

## Rozważane opcje

1. Zostawić jak jest i opisać zasady w wiki - tanio, ale nikt tego nie pilnuje.
2. Wydzielić osobny pakiet npm (workspace) dla cennika - twarda granica, ale duży koszt przy obecnym buildzie.
3. Zasady R1 i R2 jako wykonywalny test architektury w tym samym pakiecie - wybrana.

## Konsekwencje

- Pozytywne: cennik można bezpiecznie liczyć w cieniu; naruszenie reguły zatrzymuje build.
- Negatywne: test czyta pliki źródłowe (reguły tekstowe, nie pełna analiza zależności);
  złamanie reguły przez dynamiczny `import()`, re-eksport z innego katalogu albo kwotę
  w `number` pod inną nazwą trzeba wychwycić w przeglądzie.
- Do zrobienia: po wydzieleniu pakietu npm zastąpić R1 granicą pakietu i oznaczyć ten ADR
  jako zastąpiony.

## Weryfikacja

Reguły R1 i R2 są zaimplementowane w `ArchitectureRules` (ta sama scena) i sprawdzane
testem `S07SolutionTest`. Zmiana reguły wymaga nowego ADR, a nie edycji testu.
