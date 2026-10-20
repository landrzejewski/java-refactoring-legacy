import { IllegalArgumentError } from '../../../shared/errors.js';
import { requireNonNull } from '../../../shared/requireNonNull.js';
import type { Entry } from '../before/LegacyPathPlan.js';
import { isBlank } from '../../support.js';
import { DeploymentGroup } from './DeploymentGroup.js';
import { DeploymentTask } from './DeploymentTask.js';

export class LegacyPathPlanMapper {
  map(rootName: string, entries: readonly Entry[]): DeploymentGroup {
    requireRootName(rootName);
    const source = Object.freeze([...requireNonNull(entries, 'entries')]);
    validateDepthFirstOrder(rootName, source);

    const root = new GroupNode(rootName);
    for (const entry of source) {
      const segments = segmentsBelow(rootName, entry);
      let parent = root;
      for (let index = 1; index < segments.length - 1; index++) {
        parent = parent.group(segments[index]!, entry.path);
      }
      parent.task(segments[segments.length - 1]!, entry.minutes, entry.path);
    }
    return root.freeze();
  }
}

function validateDepthFirstOrder(rootName: string, entries: readonly Entry[]): void {
  let previousParents: readonly string[] = [];
  const closedGroups = new Set<string>();
  for (const entry of entries) {
    const segments = segmentsBelow(rootName, entry);
    const currentParents = parentPaths(segments);
    const common = commonPrefixLength(previousParents, currentParents);
    for (const closed of previousParents.slice(common)) {
      closedGroups.add(closed);
    }
    for (const parent of currentParents) {
      if (closedGroups.has(parent)) {
        throw new IllegalArgumentError('entries are not in depth-first order: ' + entry.path);
      }
    }
    previousParents = currentParents;
  }
}

function segmentsBelow(rootName: string, entry: Entry): readonly string[] {
  requireNonNull(entry, 'entries must not contain null');
  const segments = entry.path.split('/');
  if (segments[0] !== rootName) {
    throw new IllegalArgumentError('entry is outside root ' + rootName + ': ' + entry.path);
  }
  for (const segment of segments) {
    if (isBlank(segment)) {
      throw new IllegalArgumentError('path segment must not be blank: ' + entry.path);
    }
  }
  return segments;
}

function parentPaths(segments: readonly string[]): readonly string[] {
  const parents: string[] = [];
  let path = segments[0] ?? '';
  for (let index = 1; index < segments.length - 1; index++) {
    path += '/' + segments[index];
    parents.push(path);
  }
  return Object.freeze(parents);
}

function commonPrefixLength(first: readonly string[], second: readonly string[]): number {
  const length = Math.min(first.length, second.length);
  let index = 0;
  while (index < length && first[index] === second[index]) {
    index++;
  }
  return index;
}

function requireRootName(rootName: string): void {
  if (isBlank(rootName) || rootName.includes('/')) {
    throw new IllegalArgumentError('rootName must be one non-blank path segment');
  }
}

// Prywatna, zamknięta hierarchia węzłów roboczych (sealed interface Node permits GroupNode, TaskNode).
type Node = GroupNode | TaskNode;

class GroupNode {
  private readonly children: Node[] = [];
  private readonly groups = new Map<string, GroupNode>();
  private readonly taskNames = new Set<string>();

  constructor(readonly name: string) {}

  group(childName: string, sourcePath: string): GroupNode {
    if (this.taskNames.has(childName)) {
      throw pathConflict(sourcePath, childName);
    }
    const existing = this.groups.get(childName);
    if (existing !== undefined) {
      return existing;
    }
    const created = new GroupNode(childName);
    this.groups.set(childName, created);
    this.children.push(created);
    return created;
  }

  task(taskName: string, minutes: number, sourcePath: string): void {
    if (this.groups.has(taskName)) {
      throw pathConflict(sourcePath, taskName);
    }
    this.taskNames.add(taskName);
    this.children.push(new TaskNode(taskName, minutes));
  }

  freeze(): DeploymentGroup {
    return new DeploymentGroup(this.name, this.children.map(child => child.freeze()));
  }
}

class TaskNode {
  constructor(
    readonly name: string,
    readonly minutes: number,
  ) {}

  freeze(): DeploymentTask {
    return new DeploymentTask(this.name, this.minutes);
  }
}

function pathConflict(sourcePath: string, childName: string): IllegalArgumentError {
  return new IllegalArgumentError(
    'path is both a task and a group at ' + childName + ': ' + sourcePath,
  );
}
