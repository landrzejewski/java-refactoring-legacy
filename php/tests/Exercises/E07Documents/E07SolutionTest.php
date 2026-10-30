<?php

declare(strict_types=1);

namespace Training\Workshop\Tests\Exercises\E07Documents;

use PHPUnit\Framework\Attributes\Test;
use PHPUnit\Framework\TestCase;
use ReflectionClass;
use ReflectionNamedType;
use ReflectionParameter;
use ReflectionProperty;
use Training\Workshop\Exercises\E07Documents\Channel;
use Training\Workshop\Exercises\E07Documents\PrintOptions;
use Training\Workshop\Exercises\E07Documents\Sale;
use Training\Workshop\Exercises\E07Documents\Step2\ChannelDocument;
use Training\Workshop\Exercises\E07Documents\Step2\CodeRenderer;
use Training\Workshop\Exercises\E07Documents\Step2\Document;
use Training\Workshop\Exercises\E07Documents\Step2\DocumentComposer;
use Training\Workshop\Exercises\E07Documents\Step2\DocumentFactories;
use Training\Workshop\Exercises\E07Documents\Step2\DocumentFactory;
use Training\Workshop\Exercises\E07Documents\Step2\LoyaltyFooter;
use Training\Workshop\Exercises\E07Documents\Step2\PromoBanner;
use Training\Workshop\Exercises\E07Documents\Step2\ReceiptLayout;
use Training\Workshop\Exercises\E07Documents\Step2\TicketLayout;
use Training\Workshop\Exercises\E07Documents\Step2\VatSection;
use Training\Workshop\Shared\Money;

/**
 * Pułapka start (zestaw da się pomieszać) i kontrakt rozwiązania: spójna rodzina na kanał,
 * nowy kanał jedną klasą, sekcje owijane w kolejności zapisanej w jednym miejscu.
 */
final class E07SolutionTest extends TestCase
{
    private const string START = 'Training\\Workshop\\Exercises\\E07Documents\\Start\\';
    private const string STEP2 = 'Training\\Workshop\\Exercises\\E07Documents\\Step2\\';

    /** Pomiar w start: trzy luźne części można pomieszać; po "warsztat.sh jump" test jest pominięty. */
    #[Test]
    public function startLetsYouMixTicketCodeAndReceiptOfDifferentChannels(): void
    {
        $parameters = (new ReflectionClass(self::START . 'DocumentComposer'))->getConstructor()?->getParameters() ?? [];
        $loose = array_map(fn (ReflectionParameter $parameter) => (string) $parameter->getType(), $parameters)
            === [self::START . 'TicketLayout', self::START . 'CodeRenderer', self::START . 'ReceiptLayout'];
        if (!$loose) {
            self::markTestSkipped('start nie przyjmuje już trzech luźnych części');
        }
        $composer = self::START . 'DocumentComposer';
        $ticket = self::START . 'KioskTicketLayout';
        $code = self::START . 'MobileCodeRenderer';
        $receipt = self::START . 'BoxOfficeReceiptLayout';
        $mixed = new $composer(new $ticket(), new $code(), new $receipt());
        $document = $mixed->compose(self::sale());
        self::assertTrue(
            str_contains($document, '********') && str_contains($document, '<qr>') && str_contains($document, 'PARAGON FISKALNY'),
            "kiosk + QR aplikacji + paragon A4 w jednym dokumencie:\n" . $document,
        );
    }

    /**
     * Różnica względem Javy: PHP nie ma klas pakietowych, więc "niepubliczny" produkt to klasa
     * oznaczona {@code @internal} w PHPDoc; rodziny i interfejs fabryki nie mają tej adnotacji.
     */
    #[Test]
    public function solutionHidesConcreteDocumentsBehindFamilies(): void
    {
        foreach (['Kiosk', 'Mobile', 'BoxOffice'] as $prefix) {
            foreach (['TicketLayout', 'CodeRenderer', 'ReceiptLayout'] as $product) {
                $concrete = new ReflectionClass(self::STEP2 . $prefix . $product);
                self::assertFalse(self::isPublic($concrete), $concrete->getShortName() . ' jest publiczna');
            }
            self::assertTrue(self::isPublic(new ReflectionClass(self::STEP2 . $prefix . 'Documents')));
        }
        self::assertTrue(self::isPublic(new ReflectionClass(DocumentFactory::class)));
    }

    #[Test]
    public function everyFamilyDeliversPartsOfOneChannelOnly(): void
    {
        foreach (Channel::cases() as $channel) {
            $family = DocumentFactories::forChannel($channel);
            $prefix = str_replace('TicketLayout', '', (new ReflectionClass($family->ticketLayout()))->getShortName());
            self::assertSame($prefix . 'CodeRenderer', (new ReflectionClass($family->codeRenderer()))->getShortName());
            self::assertSame($prefix . 'ReceiptLayout', (new ReflectionClass($family->receiptLayout()))->getShortName());
        }
    }

    #[Test]
    public function newChannelIsOneFamilyWithoutTouchingTheComposer(): void
    {
        $email = new class implements DocumentFactory {
            public function ticketLayout(): TicketLayout
            {
                return new class implements TicketLayout {
                    public function ticket(Sale $sale): string
                    {
                        return 'Bilet ' . $sale->number . ' na ' . $sale->movie;
                    }
                };
            }

            public function codeRenderer(): CodeRenderer
            {
                return new class implements CodeRenderer {
                    public function code(Sale $sale): string
                    {
                        return '[' . $sale->number . ']';
                    }
                };
            }

            public function receiptLayout(): ReceiptLayout
            {
                return new class implements ReceiptLayout {
                    public function receipt(Sale $sale): string
                    {
                        return 'Razem ' . $sale->ticketPrice->plus($sale->barPrice);
                    }

                    public function section(array $lines): string
                    {
                        return '> ' . implode(' | ', $lines);
                    }
                };
            }
        };
        $document = (new DocumentComposer($email))->compose(self::sale(), PrintOptions::all())->render();
        self::assertSame(<<<'TXT'
            Bilet S-1001 na Diuna
            [S-1001]
            Razem 40.00
            > VAT 8% bilet: 2.96
            > Kod KINO10: -10% na nastepny bilet online
            > Punkty: 96 + 4 = 100 | Odbierz darmowy bilet 2D
            TXT, $document);
    }

    #[Test]
    public function wrappingOrderIsBehaviourAndIsFixedInTheComposer(): void
    {
        $sale = self::sale();
        $kiosk = DocumentFactories::forChannel(Channel::KIOSK);
        $layout = $kiosk->receiptLayout();
        $core = new ChannelDocument($sale, $kiosk);
        $vatThenLoyalty = new LoyaltyFooter(new VatSection($core, $layout, $sale), $layout, $sale);
        $loyaltyThenVat = new VatSection(new LoyaltyFooter($core, $layout, $sale), $layout, $sale);
        self::assertNotSame($vatThenLoyalty->render(), $loyaltyThenVat->render(), 'kolejność owijania zmienia dokument');

        $composed = (new DocumentComposer($kiosk))->compose($sale, new PrintOptions(true, false, true))->render();
        self::assertSame($vatThenLoyalty->render(), $composed, 'composer owija w kolejności VAT, promocja, stopka');
    }

    #[Test]
    public function everySectionWrapsExactlyOneInnerDocument(): void
    {
        foreach ([VatSection::class, PromoBanner::class, LoyaltyFooter::class] as $section) {
            $shortName = (new ReflectionClass($section))->getShortName();
            self::assertTrue(is_subclass_of($section, Document::class), $shortName . ' ma kontrakt dokumentu');
            self::assertSame(1, self::fieldsOfType($section, Document::class), $shortName);
        }
        self::assertSame(0, self::fieldsOfType(ChannelDocument::class, Document::class), 'rdzeń niczego nie owija');
    }

    private static function sale(): Sale
    {
        return new Sale('S-1001', 'Diuna', 'R10-5', Money::of('40.00'), Money::zero(), 96);
    }

    /** Odpowiednik {@code Modifier.isPublic}: klasa nie jest oznaczona jako wewnętrzna przestrzeni nazw. */
    private static function isPublic(ReflectionClass $class): bool
    {
        return !str_contains((string) $class->getDocComment(), '@internal');
    }

    /** @param class-string $owner */
    private static function fieldsOfType(string $owner, string $type): int
    {
        return count(array_filter(
            (new ReflectionClass($owner))->getProperties(),
            fn (ReflectionProperty $field) => $field->getType() instanceof ReflectionNamedType && $field->getType()->getName() === $type,
        ));
    }
}
