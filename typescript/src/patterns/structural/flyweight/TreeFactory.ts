import { TreeType } from './TreeType.js';

export class TreeFactory {
  private static readonly TYPES = new Map<string, TreeType>();

  private constructor() {}

  static getTreeType(name: string, color: string): TreeType {
    const key = `${name}:${color}`;
    let type = TreeFactory.TYPES.get(key);
    if (type === undefined) {
      type = new TreeType(name, color);
      TreeFactory.TYPES.set(key, type);
    }
    return type;
  }
}
