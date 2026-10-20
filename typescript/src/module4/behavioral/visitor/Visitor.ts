import type { Department } from './Department.js';
import type { Employee } from './Employee.js';
import type { SuperDepartment } from './SuperDepartment.js';

/**
 * Java: interface with overloaded default (no-op) visit(...) methods.
 * TypeScript has no overloading on the runtime type, so each overload gets its own optional method name.
 */
export interface Visitor {
  visitDepartment?(department: Department): void;
  visitSuperDepartment?(department: SuperDepartment): void;
  visitEmployee?(employee: Employee): void;
}
