<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

/** Krok 2: operacja kasjera jako obiekt - wykonanie i jego odwrotność w jednym miejscu. */
interface Command
{
    public function execute(): void;

    public function undo(): void;
}
