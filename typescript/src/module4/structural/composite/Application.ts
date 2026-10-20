import { Department } from './Department.js';
import { Employee } from './Employee.js';

export function run(): void {
  const mainDepartment = new Department('Main Department');
  const marketingDepartment = new Department('Marketing Department');
  const itDepartment = new Department('IT Department');
  mainDepartment.addChild(marketingDepartment);
  mainDepartment.addChild(itDepartment);
  itDepartment.addChild(new Employee('Kowalski'));
  marketingDepartment.addChild(new Employee('Nowak'));
  itDepartment.addChild(new Employee('Karlsson'));
  //----------------------------------------------------------
  itDepartment.printInfo();
}
