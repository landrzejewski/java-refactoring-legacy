import { Tree } from './Tree.js';
import { TreeFactory } from './TreeFactory.js';

export function run(): void {
  const forest: readonly Tree[] = Object.freeze([
    new Tree(1, 2, TreeFactory.getTreeType('Oak', 'green')),
    new Tree(5, 3, TreeFactory.getTreeType('Pine', 'dark green')),
    new Tree(9, 8, TreeFactory.getTreeType('Oak', 'green')),
  ]);
  forest.forEach((tree) => tree.draw());
  console.log(String(TreeFactory.getTreeType('Oak', 'green') === TreeFactory.getTreeType('Oak', 'green')));
}
