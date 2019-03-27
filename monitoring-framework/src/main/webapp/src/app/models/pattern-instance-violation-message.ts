import { PatternInstance } from './PatternInstance';

export class PatternInstanceViolationMessage {
  constructor(
    public patternInstance?: PatternInstance,
    public violation?: string
  ){}
}
