<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E01Import;

use Closure;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E01Import\ImportRequest;
use Training\Workshop\Exercises\E01Import\Start;
use Training\Workshop\Exercises\E01Import\Step1;
use Training\Workshop\Exercises\E01Import\Step2;
use Training\Workshop\Tests\Support\Scene;

/**
 * Import repertuaru: oba formaty, komentarze właściwe dla formatu, sortowanie, numeracja wierszy
 * w komunikatach i każdy błąd walidacji - te same przypadki na start i każdym kroku.
 */
final class E01EquivalenceTest extends TestCase
{
    private const string THREE_SCREENINGS = "pozycji: 3\n"
        . "10:00 Kraina Lodu (3D, sala 2) 27.00\n"
        . "16:15 Amator (2D, sala 3) 25.00\n"
        . '20:30 Diuna (IMAX, sala 1) 40.00';

    /** @return array<string, array{Closure, ImportRequest, string}> */
    public static function everyStep(): array
    {
        return Scene::variants()
            ->variant('start', fn (ImportRequest $r) => self::report(fn () => (new Start\RepertoireImport())->load($r)))
            ->variant('step1', fn (ImportRequest $r) => self::report(fn () => (new Step1\RepertoireImport())->load($r)))
            ->variant('step2', fn (ImportRequest $r) => self::report(fn () => (new Step2\RepertoireImport())->load($r)))
            ->expect(
                'CSV: komentarz, pusta linia, sortowanie po godzinie, zniżka poranna',
                self::csv('# repertuar na piatek', 'Diuna;IMAX;1;20:30', '', 'Kraina Lodu;3D;2;10:00', 'Amator;2D;3;16:15'),
                '[CSV] ' . self::THREE_SCREENINGS,
            )
            ->expect(
                'JSONL: te same seanse, komentarz od //',
                self::jsonl(
                    '// repertuar na piatek',
                    '{"title": "Diuna", "format": "IMAX", "hall": "1", "start": "20:30"}',
                    '',
                    '{"title":"Kraina Lodu","format":"3D","hall":"2","start":"10:00"}',
                    '{"title": "Amator", "format": "2D", "hall": "3", "start": "16:15"}',
                ),
                '[JSONL] ' . self::THREE_SCREENINGS,
            )
            ->expect(
                'CSV: ta sama godzina sortuje po tytule',
                self::csv('Diuna;IMAX;1;18:00', 'Amator;2D;3;18:00'),
                "[CSV] pozycji: 2\n18:00 Amator (2D, sala 3) 25.00\n18:00 Diuna (IMAX, sala 1) 40.00",
            )
            ->expect(
                'CSV: spacje wokół pól są przycinane',
                self::csv('  Diuna ; IMAX ; 1 ; 20:30  '),
                "[CSV] pozycji: 1\n20:30 Diuna (IMAX, sala 1) 40.00",
            )
            ->expect('CSV: pusty plik', self::csv(), '[CSV] pozycji: 0')
            ->expect(
                'JSONL: kolejność pól w obiekcie nie ma znaczenia',
                self::jsonl('{"start": "11:59", "hall": "4", "format": "2D", "title": "Amator"}'),
                "[JSONL] pozycji: 1\n11:59 Amator (2D, sala 4) 20.00",
            )
            ->expect('CSV: za mało pól', self::csv('# naglowek', 'Diuna;IMAX;20:30'), 'ERROR: line 2: expected 4 fields, got 3')
            ->expect('CSV: komentarz w stylu JSONL to błąd', self::csv('// to nie jest komentarz CSV'), 'ERROR: line 1: expected 4 fields, got 1')
            ->expect('JSONL: komentarz w stylu CSV to błąd', self::jsonl('# to nie jest komentarz JSONL'), 'ERROR: line 1: not a JSON object')
            ->expect(
                'JSONL: brak pola',
                self::jsonl('{"title": "Diuna", "format": "IMAX", "hall": "1"}'),
                'ERROR: line 1: missing field: start',
            )
            ->expect('numer wiersza liczy też komentarze i puste linie', self::csv('# a', '', 'Diuna;4DX;1;20:30'), 'ERROR: line 3: unknown format: 4DX')
            ->expect(
                'zła godzina',
                self::jsonl('{"title": "Diuna", "format": "IMAX", "hall": "1", "start": "25:00"}'),
                'ERROR: line 1: bad start time: 25:00',
            )
            ->expect('pusty tytuł', self::csv(';2D;1;10:00'), 'ERROR: line 1: empty title')
            ->expect(
                'pusta sala',
                self::jsonl('{"title": "Amator", "format": "2D", "hall": "", "start": "10:00"}'),
                'ERROR: line 1: empty hall',
            )
            ->expect(
                'walidacja zatrzymuje się na pierwszym złym wierszu',
                self::csv('Diuna;IMAX;1;20:30', 'Amator;2D;;10:00', 'Kraina Lodu;5D;2;10:00'),
                'ERROR: line 2: empty hall',
            )
            ->expect('nieobsługiwane rozszerzenie', new ImportRequest('repertuar.xml', ['<r/>']), 'ERROR: unsupported file: repertuar.xml')
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepImportsTheSame(Closure $variant, ImportRequest $request, string $expected): void
    {
        self::assertSame($expected, $variant($request));
    }

    private static function csv(string ...$lines): ImportRequest
    {
        return new ImportRequest('repertuar.csv', $lines);
    }

    private static function jsonl(string ...$lines): ImportRequest
    {
        return new ImportRequest('repertuar.jsonl', $lines);
    }

    /** @param Closure(): string $load */
    public static function report(Closure $load): string
    {
        try {
            return $load();
        } catch (InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}
