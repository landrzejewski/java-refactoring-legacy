import type { ApprovalStep } from './ApprovalStep.js';
import type { ScriptStep } from './ScriptStep.js';

// Odpowiednik `sealed interface DeploymentStep permits ScriptStep, ApprovalStep`.
export type DeploymentStep = ScriptStep | ApprovalStep;
