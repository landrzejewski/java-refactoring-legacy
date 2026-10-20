import type { Department } from './Department.js';
import type { Employee } from './Employee.js';
import type { Visitor } from './Visitor.js';

export class Reporter implements Visitor {
  visitDepartment(department: Department): void {
    console.log(`Department: ${department.name}`);
  }

  visitEmployee(employee: Employee): void {
    console.log(` - Employee ${employee.name}`);
  }
}
