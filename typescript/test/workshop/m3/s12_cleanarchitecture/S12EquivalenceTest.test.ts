import { describe } from 'vitest';

import { Outbox } from '../../../../src/workshop/m3/s12_cleanarchitecture/Outbox.js';
import { RowStore } from '../../../../src/workshop/m3/s12_cleanarchitecture/RowStore.js';
import * as start from '../../../../src/workshop/m3/s12_cleanarchitecture/start/CinemaApplication.js';
import * as step1 from '../../../../src/workshop/m3/s12_cleanarchitecture/step1/CinemaApplication.js';
import * as step2 from '../../../../src/workshop/m3/s12_cleanarchitecture/step2/CinemaApplication.js';
import * as step3 from '../../../../src/workshop/m3/s12_cleanarchitecture/step3/CinemaApplication.js';
import * as step4 from '../../../../src/workshop/m3/s12_cleanarchitecture/step4/CinemaApplication.js';
import { Scene } from '../../support/scene.js';

interface Request {
  readonly params: ReadonlyMap<string, string>;
  readonly dbAvailable: boolean;
}

type Controller = { handle(params: ReadonlyMap<string, string>): string };

function run(app: (db: RowStore, outbox: Outbox) => Controller): (request: Request) => string {
  return (request) => {
    const db = new RowStore(request.dbAvailable);
    const outbox = new Outbox();
    const response = app(db, outbox).handle(request.params);
    return `${response} | db=[${db.dump().join(', ')}] | outbox=[${outbox.messages().join(', ')}]`;
  };
}

function request(params: Record<string, string>, dbAvailable: boolean): Request {
  return { params: new Map(Object.entries(params)), dbAvailable };
}

/** Z zewnątrz (odpowiedź, zapisane wiersze, komunikaty) każdy krok zachowuje się identycznie. */
describe('S12EquivalenceTest', () => {
  describe('everyStepHandlesRequestsTheSame', () => {
    Scene.variants<Request, string>()
      .variant('start', run(start.reservationController))
      .variant('step1', run(step1.reservationController))
      .variant('step2', run(step2.reservationController))
      .variant('step3', run(step3.reservationController))
      .variant('step4', run(step4.reservationController))
      .expect('IMAX, jedno miejsce VIP',
        request({ email: 'anna@kino.pl', format: 'IMAX', rows: '5,10' }, true),
        '201 R-1 90.00 | db=[anna@kino.pl;IMAX;2;90.00]'
          + ' | outbox=[reservation-created:R-1;anna@kino.pl;90.00]')
      .expect('brak adresu e-mail', request({ format: '2D', rows: '3' }, true),
        '400 brak email | db=[] | outbox=[]')
      .expect('brak miejsc', request({ email: 'jan@kino.pl', rows: '' }, true),
        '400 brak miejsc | db=[] | outbox=[]')
      .expect('baza niedostepna - brak powiadomienia',
        request({ email: 'jan@kino.pl', format: '2D', rows: '3' }, false),
        '503 baza niedostepna | db=[] | outbox=[]')
      .tests();
  });
});
