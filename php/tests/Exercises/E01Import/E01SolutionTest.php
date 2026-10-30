<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E01Import;

use InvalidArgumentException;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionMethod;
use Training\Workshop\Exercises\E01Import\Step2\CsvRepertoireImporter;
use Training\Workshop\Exercises\E01Import\Step2\Importers;
use Training\Workshop\Exercises\E01Import\Step2\JsonLineParser;
use Training\Workshop\Exercises\E01Import\Step2\JsonLinesRepertoireImporter;
use Training\Workshop\Exercises\E01Import\Step2\LineParser;
use Training\Workshop\Exercises\E01Import\Step2\RawScreening;
use Training\Workshop\Exercises\E01Import\Step2\RepertoireImporter;

/**
 * Kontrakt rozwiązania: szkielet w jednym miejscu i chroniony, krok zależny od formatu jako osobny
 * produkt, importer konkretnego formatu tylko tworzy i podpisuje, a rozszerzenia zna jedno miejsce.
 */
final class E01SolutionTest extends TestCase
{
    #[Test]
    public function skeletonIsFinalSoNoFormatCanReorderTheSteps(): void
    {
        self::assertTrue((new ReflectionMethod(RepertoireImporter::class, 'importLines'))->isFinal());
    }

    #[Test]
    public function newFormatIsOneParserAndOneCreatorAndReusesSortingAndPricing(): void
    {
        $tsv = new class extends RepertoireImporter {
            public function label(): string
            {
                return 'TSV';
            }

            protected function createLineParser(): LineParser
            {
                return new class implements LineParser {
                    public function isComment(string $line): bool
                    {
                        return str_starts_with($line, ';');
                    }

                    public function parse(string $line, int $number): RawScreening
                    {
                        $fields = explode("\t", $line);
                        return new RawScreening($fields[0], $fields[1], $fields[2], $fields[3]);
                    }
                };
            }
        };
        $entries = $tsv->importLines(['; komentarz', "Diuna\tIMAX\t1\t20:30", "Amator\t2D\t3\t09:00"]);
        self::assertSame(
            '[09:00 Amator (2D, sala 3) 20.00, 20:30 Diuna (IMAX, sala 1) 40.00]',
            '[' . implode(', ', array_map(strval(...), $entries)) . ']',
        );
    }

    #[Test]
    public function lineParserIsTestableWithoutAnyImporter(): void
    {
        $parser = new JsonLineParser();
        self::assertEquals(
            new RawScreening('Diuna', 'IMAX', '1', '20:30'),
            $parser->parse('{"title": "Diuna", "format": "IMAX", "hall": "1", "start": "20:30"}', 7),
        );
        $this->expectException(InvalidArgumentException::class);
        $this->expectExceptionMessage('line 7: missing field: format');
        $parser->parse('{"title": "Diuna"}', 7);
    }

    #[Test]
    public function concreteImportersOnlyCreateAndLabel(): void
    {
        foreach ([CsvRepertoireImporter::class, JsonLinesRepertoireImporter::class] as $creator) {
            $declared = array_map(
                fn (ReflectionMethod $method) => $method->getName(),
                array_filter(
                    (new ReflectionClass($creator))->getMethods(),
                    fn (ReflectionMethod $method) => $method->getDeclaringClass()->getName() === $creator,
                ),
            );
            sort($declared);
            self::assertSame(['createLineParser', 'label'], $declared, $creator);
        }
    }

    #[Test]
    public function fileExtensionsAreKnownInOnePlace(): void
    {
        self::assertSame('CSV', Importers::forFile('piatek.csv')->label());
        self::assertSame('JSONL', Importers::forFile('piatek.jsonl')->label());
        $this->expectException(InvalidArgumentException::class);
        $this->expectExceptionMessage('unsupported file: piatek.xml');
        Importers::forFile('piatek.xml');
    }
}
