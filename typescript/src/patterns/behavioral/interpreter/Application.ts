import { IllegalArgumentError } from '../../../shared/errors.js';
import { javaDouble } from '../../JavaText.js';
import { JulLogger } from '../../JulLogger.js';
import type { BiOperator } from './BiOperator.js';
import { Literal } from './Literal.js';
import { Minus } from './Minus.js';
import { Multiply } from './Multiply.js';
import { Operation } from './Operation.js';
import { Plus } from './Plus.js';
import { Variable } from './Variable.js';

const log = JulLogger.getLogger('pl.training.patterns.behavioral.interpreter.Application');
const OPERATORS = new Map<string, BiOperator>();

function op(name: string): BiOperator {
  let operator = OPERATORS.get(name);
  if (operator === undefined) {
    operator = operatorFactory(name);
    OPERATORS.set(name, operator);
  }
  return operator;
}

function operatorFactory(name: string): BiOperator {
  switch (name) {
    case '+':
      return new Plus();
    case '-':
      return new Minus();
    case '*':
      return new Multiply();
    default:
      throw new IllegalArgumentError(`Unknown operator: ${name}`);
  }
}

export function run(): void {
  // 2a * 3 + 1 gdzie a = 4
  const expression = new Operation(new Operation(new Operation(new Literal(2), new Variable('a'), op('*')), new Literal(3), op('*')), new Literal(1), op('+'));
  log.info(`Result: ${javaDouble(expression.evaluate(new Map([['a', 4.0]])))}`, 'main');
}
