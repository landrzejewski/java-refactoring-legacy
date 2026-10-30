<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E04SeatPicker;

use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionMethod;
use Training\Workshop\Exercises\E04SeatPicker\HallPlan;
use Training\Workshop\Exercises\E04SeatPicker\PickerRun;
use Training\Workshop\Exercises\E04SeatPicker\Seat;
use Training\Workshop\Exercises\E04SeatPicker\Start;
use Training\Workshop\Exercises\E04SeatPicker\Step1;
use Training\Workshop\Exercises\E04SeatPicker\Step2;
use Training\Workshop\Exercises\E04SeatPicker\Step2\AutoArrange;
use Training\Workshop\Exercises\E04SeatPicker\Step2\Cart;
use Training\Workshop\Exercises\E04SeatPicker\Step2\ClearSeats;
use Training\Workshop\Exercises\E04SeatPicker\Step2\Command;
use Training\Workshop\Exercises\E04SeatPicker\Step2\DeselectSeat;
use Training\Workshop\Exercises\E04SeatPicker\Step2\Memento;
use Training\Workshop\Exercises\E04SeatPicker\Step2\SelectSeat;
use Training\Workshop\Exercises\E04SeatPicker\Step2\SetGlasses;
use Training\Workshop\Exercises\E04SeatPicker\Step2\SetVoucher;
use Training\Workshop\Exercises\E04SeatPicker\Step2\SwapSeats;

/**
 * Kontrakt rozwiązania: każde polecenie cofa dokładnie siebie, migawka koszyka jest nieprzezroczysta,
 * a pełna migawka naprawia zgubiony voucher (świadoma zmiana zachowania względem start).
 */
final class E04SolutionTest extends TestCase
{
    #[Test]
    public function everyCommandUndoesExactlyItself(): void
    {
        $cart = new Cart(HallPlan::standard());
        (new SelectSeat($cart, new Seat(6, 4)))->execute();
        (new SetVoucher($cart, 'KINO10'))->execute();
        $before = $cart->state();

        /** @var list<Command> $commands */
        $commands = [
            new SelectSeat($cart, new Seat(6, 7)),
            new SwapSeats($cart, new Seat(6, 4), new Seat(6, 8)),
            new DeselectSeat($cart, new Seat(6, 4)),
            new SetGlasses($cart, true),
            new ClearSeats($cart),
            new AutoArrange($cart, 2),
        ];
        foreach ($commands as $command) {
            $command->execute();
            $command->undo();
            self::assertSame($before, $cart->state(), (new ReflectionClass($command))->getShortName());
        }
    }

    #[Test]
    public function mementoIsOpaqueOutsideTheCart(): void
    {
        // Różnica względem Javy: Cart.Memento to osobna klasa Memento (PHP nie ma klas zagnieżdżonych),
        // a konstruktor jest w PHP metodą, więc liczymy metody poza konstruktorem.
        $memento = new ReflectionClass(Memento::class);
        $methods = array_filter($memento->getMethods(), fn (ReflectionMethod $method) => !$method->isConstructor());
        self::assertCount(0, $methods, 'migawka nie ma metod');
        foreach ($memento->getProperties() as $property) {
            self::assertTrue($property->isPrivate(), $property->getName() . ' jest prywatne');
        }
        self::assertTrue($memento->getConstructor()?->isPrivate() ?? false, 'tylko koszyk tworzy migawkę');
    }

    #[Test]
    public function commandsCarryTheirOwnUndoDataAndShareNothing(): void
    {
        // Różnica względem Javy: odpowiednikiem pola static final jest stała klasy (const), a właściwość
        // statyczna nie może być readonly - każda statyczna właściwość to współdzielony stan.
        $commands = [
            SelectSeat::class, DeselectSeat::class, SwapSeats::class,
            SetGlasses::class, SetVoucher::class, ClearSeats::class, AutoArrange::class,
        ];
        foreach ($commands as $command) {
            foreach ((new ReflectionClass($command))->getProperties() as $field) {
                self::assertFalse(
                    $field->isStatic(),
                    (new ReflectionClass($command))->getShortName() . '.' . $field->getName() . ' to współdzielony stan',
                );
            }
        }
    }

    #[Test]
    public function failedAutoUsedToLoseTheVoucherAndNowRestoresIt(): void
    {
        $run = PickerRun::almostFull('VOUCHER KINO10', 'SELECT 1-1', 'AUTO 2');
        $lost = 'ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: - | suma: 35.00';
        $kept = 'ERROR: brak 2 miejsc obok siebie @ miejsca: 1-1 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50';
        if (self::startHasNoMemento()) {
            self::assertSame($lost, (new Start\SeatPickerSession($run->plan))->run($run->script));
        }
        self::assertSame($lost, (new Step1\SeatPickerSession($run->plan))->run($run->script));
        self::assertSame($kept, (new Step2\SeatPickerSession($run->plan))->run($run->script));
    }

    #[Test]
    public function undoAfterAutoUsedToLoseTheVoucherAndNowRestoresIt(): void
    {
        $run = PickerRun::standard('VOUCHER KINO10', 'SELECT 6-4', 'AUTO 2', 'UNDO');
        $lost = 'miejsca: 6-4 | okulary: wypozyczone | voucher: - | suma: 35.00';
        $kept = 'miejsca: 6-4 | okulary: wypozyczone | voucher: KINO10 | suma: 31.50';
        if (self::startHasNoMemento()) {
            self::assertSame($lost, (new Start\SeatPickerSession($run->plan))->run($run->script));
        }
        self::assertSame($kept, (new Step2\SeatPickerSession($run->plan))->run($run->script));
    }

    /** Pułapka start (zgubiony voucher) jest sprawdzana tylko, dopóki w Start nie ma pełnej migawki (po "warsztat.sh jump 2"). */
    private static function startHasNoMemento(): bool
    {
        return !class_exists(Start\Memento::class);
    }
}
