<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

/** Krok 2: GLASSES - własne albo wypożyczone okulary; cofnięcie przywraca poprzedni wybór. */
final class SetGlasses implements Command
{
    private bool $before = false;

    public function __construct(private readonly Cart $cart, private readonly bool $own)
    {
    }

    public function execute(): void
    {
        $this->before = $this->cart->ownGlasses();
        $this->cart->setOwnGlasses($this->own);
    }

    public function undo(): void
    {
        $this->cart->setOwnGlasses($this->before);
    }
}
