<?php

declare(strict_types=1);

namespace Training\Workshop\Exercises\E04SeatPicker\Step2;

/** Krok 2: VOUCHER - ustawienie albo zdjęcie kodu; cofnięcie przywraca poprzedni kod. */
final class SetVoucher implements Command
{
    private ?string $before = null;

    public function __construct(private readonly Cart $cart, private readonly ?string $code)
    {
    }

    public function execute(): void
    {
        $this->before = $this->cart->voucher();
        $this->cart->setVoucher($this->code);
    }

    public function undo(): void
    {
        $this->cart->setVoucher($this->before);
    }
}
