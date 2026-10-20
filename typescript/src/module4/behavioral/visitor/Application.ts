import { Department } from './Department.js';
import { Employee } from './Employee.js';
import { NameValidator } from './NameValidator.js';

export function run(): void {
  const mainDepartment = new Department('Ma');
  const marketingDepartment = new Department('Marketing Department');
  const itDepartment = new Department('IT Department');
  mainDepartment.addChild(marketingDepartment);
  mainDepartment.addChild(itDepartment);
  itDepartment.addChild(new Employee('Kowalski'));
  marketingDepartment.addChild(new Employee('Nowak'));
  itDepartment.addChild(new Employee('Karlsson'));
  //----------------------------------------------------------
  mainDepartment.accept(new NameValidator());
}
