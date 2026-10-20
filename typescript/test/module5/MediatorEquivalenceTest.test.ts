import { describe, expect, it } from 'vitest';
import { FormState } from '../../src/module5/mediator/FormState.js';
import { ApproverPicker } from '../../src/module5/mediator/after/ApproverPicker.js';
import { PublishButton } from '../../src/module5/mediator/after/PublishButton.js';
import { ReleaseFormMediator } from '../../src/module5/mediator/after/ReleaseFormMediator.js';
import { StatusLabel } from '../../src/module5/mediator/after/StatusLabel.js';
import { VersionField } from '../../src/module5/mediator/after/VersionField.js';
import { LegacyReleaseForm } from '../../src/module5/mediator/before/LegacyReleaseForm.js';

const EVENTS: readonly string[] = [
  'choose:early',
  'type:1.2',
  'type:1.2.0',
  'choose:anna',
  'type:oops',
  'click',
  'type:1.3.0',
  'choose:bob',
  'click',
  'type:2.0.0',
  'choose:carol',
  'click',
];

function apply(form: LegacyReleaseForm | ReleaseFormMediator, event: string): void {
  if (event.startsWith('type:')) {
    form.versionField().type(event.substring(5));
  } else if (event.startsWith('choose:')) {
    form.approverPicker().choose(event.substring(7));
  } else {
    form.publishButton().click();
  }
}

describe('MediatorEquivalenceTest', () => {
  it('sameEventsLeadToTheSameFormStates', () => {
    const before = new LegacyReleaseForm();
    const after = new ReleaseFormMediator();
    const beforeStates: FormState[] = [before.snapshot()];
    const afterStates: FormState[] = [after.snapshot()];

    for (const event of EVENTS) {
      apply(before, event);
      apply(after, event);
      beforeStates.push(before.snapshot());
      afterStates.push(after.snapshot());
    }

    expect(afterStates).toEqual(beforeStates);
    expect(afterStates[0]).toEqual(new FormState('', true, false, '', false, 'enter version'));
    expect(afterStates.at(-1)).toEqual(new FormState('1.3.0', false, false, 'bob', false, 'published 1.3.0'));
  });

  it('componentsKnowOnlyTheMediator', () => {
    // Java sprawdza refleksją zadeklarowane typy pól. W TS typy znikają w runtime,
    // więc sprawdzamy wartości pól komponentów utworzonych przez mediator:
    // każde pole to sam mediator, tekst albo wartość logiczna - nigdy inny komponent.
    const mediator = new ReleaseFormMediator();
    const components = Object.values(mediator) as unknown[];
    const componentTypes = [VersionField, ApproverPicker, PublishButton, StatusLabel];

    expect(components.map(component => (component as object).constructor)).toEqual(componentTypes);
    for (const component of components) {
      for (const [name, value] of Object.entries(component as object)) {
        const label = `${(component as object).constructor.name}.${name}`;
        expect(value === mediator || typeof value === 'string' || typeof value === 'boolean', label).toBe(true);
      }
    }
  });
});
