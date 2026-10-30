<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E02HallMap\Step3;

use ArrayIterator;
use Iterator;
use OutOfBoundsException;
use SplStack;

/**
 * Krok 3: przejście po miejscach w głąb, od lewej - bez zmian.
 *
 * Różnica względem Javy: PHP-owy {@see Iterator} ma {@code rewind/valid/current/key/next}
 * zamiast {@code hasNext/next}; {@code valid()} odpowiada {@code hasNext()}, a {@code current()}
 * rzuca {@see OutOfBoundsException} (odpowiednik {@code NoSuchElementException}), gdy miejsc już nie ma.
 *
 * @implements Iterator<int, Seat>
 */
final class SeatIterator implements Iterator
{
    /** @var SplStack<ArrayIterator<int, HallElement>> */
    private SplStack $path;
    private ?Seat $next = null;
    private int $position = 0;

    public function __construct(private readonly HallGroup $root)
    {
        $this->rewind();
    }

    public function rewind(): void
    {
        $this->path = new SplStack();
        $this->path->push(new ArrayIterator($this->root->children()));
        $this->position = 0;
        $this->advance();
    }

    public function valid(): bool
    {
        return $this->next !== null;
    }

    public function current(): Seat
    {
        if ($this->next === null) {
            throw new OutOfBoundsException();
        }
        return $this->next;
    }

    public function key(): int
    {
        return $this->position;
    }

    public function next(): void
    {
        $this->advance();
        $this->position++;
    }

    private function advance(): void
    {
        $this->next = null;
        while (!$this->path->isEmpty()) {
            $level = $this->path->top();
            if (!$level->valid()) {
                $this->path->pop();
                continue;
            }
            $element = $level->current();
            $level->next();
            if ($element instanceof HallGroup) {
                $this->path->push(new ArrayIterator($element->children()));
            } else {
                $this->next = $element;
                return;
            }
        }
    }
}
