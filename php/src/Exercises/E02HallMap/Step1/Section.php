<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step1;

use InvalidArgumentException;
use Training\Workshop\Exercises\E02HallMap\SectionSpec;

/** Krok 1: sektor jest węzłem drzewa nad rzędami. */
final class Section extends HallGroup
{
    /** @param list<Row> $rows */
    private function __construct(private readonly string $name, private readonly array $rows)
    {
        parent::__construct($rows);
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
