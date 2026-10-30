<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Start;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\SectionSpec;

/** Start: sektor to nazwa i lista rzędów - kolejny poziom, przez który klient musi przejść pętlą. */
final class Section
{
    /** @param list<Row> $rows */
    private function __construct(private readonly string $name, private readonly array $rows)
    {
    }

    public static function of(SectionSpec $spec): self
    {
        if ($spec->rows === []) {
            throw new InvalidArgumentException('section ' . $spec->name . ' has no rows');
        }
        $rows = [];
        foreach ($spec->rows as $rowSpec) {
            $rows[] = Row::of($rowSpec);
        }
        return new self($spec->name, $rows);
    }

    public function name(): string
    {
        return $this->name;
    }

    /** @return list<Row> */
    public function rows(): array
    {
        return $this->rows;
    }
}
