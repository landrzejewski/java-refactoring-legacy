<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E07Documents;

use Closure;
use InvalidArgumentException;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use Training\Workshop\Exercises\E07Documents\Channel;
use Training\Workshop\Exercises\E07Documents\PrintOptions;
use Training\Workshop\Exercises\E07Documents\PrintRequest;
use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Exercises\E07Documents\Start;
use Training\Workshop\Exercises\E07Documents\Step1;
use Training\Workshop\Exercises\E07Documents\Step2;
use Training\Workshop\Shared\Money;
use Training\Workshop\Tests\Support\Scene;

/**
 * Dokument sprzedaży znak po znaku: każdy kanał, każda sekcja osobno i razem (kolejność sekcji
 * jest kontraktem), paragon bez baru, próg 100 punktów oraz błędy walidacji.
 */
final class E07EquivalenceTest extends TestCase
{
    /** @return array<string, array{Closure, PrintRequest, string}> */
    public static function everyStep(): array
    {
        $diuna = new Sale('S-1001', 'Diuna', 'R10-5', Money::of('40.00'), Money::of('12.00'), 96);
        $amator = new Sale('S-1002', 'Amator', 'R3-7', Money::of('25.00'), Money::zero(), 10);
        return Scene::variants()
            ->variant('start', fn (PrintRequest $r) => self::document(fn () => (new Start\SalesDocuments())->print($r->sale, $r->channel, $r->options)))
            ->variant('step1', fn (PrintRequest $r) => self::document(fn () => (new Step1\SalesDocuments())->print($r->sale, $r->channel, $r->options)))
            ->variant('step2', fn (PrintRequest $r) => self::document(fn () => (new Step2\SalesDocuments())->print($r->sale, $r->channel, $r->options)))
            ->expect('KIOSK bez opcji', new PrintRequest($diuna, Channel::KIOSK, PrintOptions::none()), <<<'TXT'
                ********************************
                BILET S-1001
                Diuna
                Miejsce R10-5
                || ||| | S-1001 | ||| ||
                PARAGON
                Bilet                      40.00
                Bar                        12.00
                RAZEM                      52.00
                ********************************
                TXT)
            ->expect('MOBILE bez opcji', new PrintRequest($diuna, Channel::MOBILE, PrintOptions::none()), <<<'TXT'
                <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                <qr>S-1001</qr>
                <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>
                TXT)
            ->expect('BOX_OFFICE bez opcji', new PrintRequest($diuna, Channel::BOX_OFFICE, PrintOptions::none()), <<<'TXT'
                CINELEGACY - BILET WSTEPU
                Numer:   S-1001
                Film:    Diuna
                Miejsce: R10-5
                Kod kontrolny: #S-1001#
                PARAGON FISKALNY
                Bilet ............. 40.00
                Bar ............... 12.00
                RAZEM ............. 52.00
                TXT)
            ->expect('KIOSK wszystkie sekcje, bez baru', new PrintRequest($amator, Channel::KIOSK, PrintOptions::all()), <<<'TXT'
                ********************************
                BILET S-1002
                Amator
                Miejsce R3-7
                || ||| | S-1002 | ||| ||
                PARAGON
                Bilet                      25.00
                RAZEM                      25.00
                ********************************
                VAT 8% bilet: 1.85
                --------------------------------
                Kod KINO10: -10% na nastepny bilet online
                --------------------------------
                Punkty: 10 + 2 = 12
                --------------------------------
                TXT)
            ->expect('MOBILE tylko VAT, z barem', new PrintRequest($diuna, Channel::MOBILE, new PrintOptions(true, false, false)), <<<'TXT'
                <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                <qr>S-1001</qr>
                <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>
                <section><p>VAT 8% bilet: 2.96</p><p>VAT 23% bar: 2.24</p></section>
                TXT)
            ->expect(
                'BOX_OFFICE promocja i stopka, prog 100 punktow',
                new PrintRequest($diuna, Channel::BOX_OFFICE, new PrintOptions(false, true, true)),
                <<<'TXT'
                CINELEGACY - BILET WSTEPU
                Numer:   S-1001
                Film:    Diuna
                Miejsce: R10-5
                Kod kontrolny: #S-1001#
                PARAGON FISKALNY
                Bilet ............. 40.00
                Bar ............... 12.00
                RAZEM ............. 52.00
                  Kod KINO10: -10% na nastepny bilet online
                  Punkty: 96 + 4 = 100
                  Odbierz darmowy bilet 2D
                TXT,
            )
            ->expect(
                'KIOSK tylko stopka, ponizej progu',
                new PrintRequest($amator, Channel::KIOSK, new PrintOptions(false, false, true)),
                <<<'TXT'
                ********************************
                BILET S-1002
                Amator
                Miejsce R3-7
                || ||| | S-1002 | ||| ||
                PARAGON
                Bilet                      25.00
                RAZEM                      25.00
                ********************************
                Punkty: 10 + 2 = 12
                --------------------------------
                TXT,
            )
            ->expect('MOBILE wszystkie sekcje', new PrintRequest($diuna, Channel::MOBILE, PrintOptions::all()), <<<'TXT'
                <ticket><h1>Diuna</h1><p>Bilet S-1001</p><p>Miejsce R10-5</p></ticket>
                <qr>S-1001</qr>
                <receipt><li>Bilet 40.00</li><li>Bar 12.00</li><li><b>Razem 52.00</b></li></receipt>
                <section><p>VAT 8% bilet: 2.96</p><p>VAT 23% bar: 2.24</p></section>
                <section><p>Kod KINO10: -10% na nastepny bilet online</p></section>
                <section><p>Punkty: 96 + 4 = 100</p><p>Odbierz darmowy bilet 2D</p></section>
                TXT)
            ->expect('BOX_OFFICE wszystkie sekcje, bez baru', new PrintRequest($amator, Channel::BOX_OFFICE, PrintOptions::all()), <<<'TXT'
                CINELEGACY - BILET WSTEPU
                Numer:   S-1002
                Film:    Amator
                Miejsce: R3-7
                Kod kontrolny: #S-1002#
                PARAGON FISKALNY
                Bilet ............. 25.00
                RAZEM ............. 25.00
                  VAT 8% bilet: 1.85
                  Kod KINO10: -10% na nastepny bilet online
                  Punkty: 10 + 2 = 12
                TXT)
            ->expect('sprzedaz null', new PrintRequest(null, Channel::KIOSK, PrintOptions::none()), 'ERROR: sale must not be null')
            ->expect('kanal null', new PrintRequest($diuna, null, PrintOptions::none()), 'ERROR: channel must not be null')
            ->expect('opcje null', new PrintRequest($diuna, Channel::MOBILE, null), 'ERROR: options must not be null')
            ->expect(
                'ujemna cena biletu',
                new PrintRequest(new Sale('S-1', 'Diuna', 'R1-1', Money::of('-1.00'), Money::zero(), 0), Channel::BOX_OFFICE, PrintOptions::all()),
                'ERROR: ticket price must not be negative',
            )
            ->expect(
                'ujemna kwota baru',
                new PrintRequest(new Sale('S-1', 'Diuna', 'R1-1', Money::of('25.00'), Money::of('-0.01'), 0), Channel::KIOSK, PrintOptions::none()),
                'ERROR: bar price must not be negative',
            )
            ->cases();
    }

    #[Test]
    #[DataProvider('everyStep')]
    public function everyStepPrintsTheSameDocument(Closure $variant, PrintRequest $request, string $expected): void
    {
        self::assertSame($expected, $variant($request));
    }

    /** @param Closure(): string $document */
    public static function document(Closure $document): string
    {
        try {
            return $document();
        } catch (InvalidArgumentException $exception) {
            return 'ERROR: ' . $exception->getMessage();
        }
    }
}
