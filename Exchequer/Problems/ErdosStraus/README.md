# Erdős–Straus workspace

## Statement

For every integer `n ≥ 2`, find positive integers `x`, `y`, and `z` such that

```text
4/n = 1/x + 1/y + 1/z.
```

The Lean definition in `Basic.lean` clears denominators and records positivity explicitly.

## Current verified results

The checked source currently contains:

1. the instance `n = 2`;
2. every positive even `n`;
3. closure under positive scaling;
4. lifting a verified decomposition from a positive divisor to a positive multiple.

These are infrastructure lemmas, not a proof of the full conjecture.

## Research protocol

New work should be separated into:

- `Basic.lean` for stable definitions and foundational lemmas;
- additional `.lean` files for residue classes, divisor identities, or computational certificates;
- `research/erdos_straus.md` for informal derivations, failed attempts, and candidate statements.

A statement moves from the research log into the verified list only after Lean accepts it in CI.
