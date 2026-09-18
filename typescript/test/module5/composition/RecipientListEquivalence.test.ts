import { describe, expect, it } from 'vitest';
import { RecipientList } from '../../../src/module5/composition/after/RecipientList.js';
import { RecipientList as BeforeRecipientList } from '../../../src/module5/composition/before/RecipientList.js';

// Klient wersji "before" używa odziedziczonego API tablicy (push/length/includes/splice),
// wersja "after" udostępnia tylko intencjonalny kontrakt (add/size/contains/remove).
function removeFromArray(list: string[], recipient: string): boolean {
  const index = list.indexOf(recipient);
  if (index < 0) {
    return false;
  }
  list.splice(index, 1);
  return true;
}

function addToArray(list: string[], recipient: string): boolean {
  list.push(recipient);
  return true;
}

describe('RecipientListEquivalenceTest', () => {
  it('compositionPreservesTheIntendedRecipientListContract', () => {
    const before = new BeforeRecipientList();
    const after = new RecipientList();

    expect(after.add('alice@example.com')).toBe(addToArray(before, 'alice@example.com'));
    expect(after.add('bob@example.com')).toBe(addToArray(before, 'bob@example.com'));
    expect(after.add('alice@example.com')).toBe(addToArray(before, 'alice@example.com'));

    expect(before.length).toBe(3);
    expect(after.size()).toBe(before.length);
    expect(before.includes('bob@example.com')).toBe(true);
    expect(after.contains('bob@example.com')).toBe(before.includes('bob@example.com'));
    expect([...after]).toEqual([...before]);

    expect(after.remove('alice@example.com')).toBe(removeFromArray(before, 'alice@example.com'));
    expect(before.snapshot()).toEqual(['bob@example.com', 'alice@example.com']);
    expect(after.snapshot()).toEqual(before.snapshot());

    const beforeSnapshot = before.snapshot();
    const afterSnapshot = after.snapshot();
    expect(() => (beforeSnapshot as string[]).push('forbidden@example.com')).toThrow(TypeError);
    expect(() => (afterSnapshot as string[]).push('forbidden@example.com')).toThrow(TypeError);
    before.push('carol@example.com');
    after.add('carol@example.com');

    expect(beforeSnapshot).toEqual(['bob@example.com', 'alice@example.com']);
    expect(afterSnapshot).toEqual(beforeSnapshot);
    expect([...after]).toEqual([...before]);

    // Java: after.iterator().remove() rzuca. W TS iterator nie ma operacji remove i nie
    // odsłania tablicy; wyciek zostaje tylko w wersji dziedziczącej po Array.
    const afterIterator = after[Symbol.iterator]();
    afterIterator.next();
    expect('remove' in afterIterator).toBe(false);
    expect(before).toBeInstanceOf(Array);
    expect(Array.isArray(after)).toBe(false);

    const removedBefore = removeFromArray(before, 'missing@example.com');
    const removedAfter = after.remove('missing@example.com');
    expect(removedBefore).toBe(false);
    expect(removedAfter).toBe(removedBefore);

    const anotherBefore = new BeforeRecipientList();
    const anotherAfter = new RecipientList();
    expect(anotherBefore.length).toBe(0);
    expect(anotherAfter.size()).toBe(0);
  });
});
