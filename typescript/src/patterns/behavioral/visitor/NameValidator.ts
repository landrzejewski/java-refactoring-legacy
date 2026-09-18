import type { Department } from './Department.js';
import type { Visitor } from './Visitor.js';

export class NameValidator implements Visitor {
  visitDepartment(department: Department): void {
    if (department.name.length < 3) {
      console.log(`Department name too short: ${department.name}`);
    }
  }
}
