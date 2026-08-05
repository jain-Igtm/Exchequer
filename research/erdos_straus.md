# Erdős–Straus research log

This file records informal mathematics. Nothing here is certified unless it is also represented by a compiling Lean theorem.

## 2026-08-05 — Initial reduction map

The project starts with the denominator-cleared relation

```text
4xyz = n(yz + xz + xy)
```

for positive natural numbers `x`, `y`, and `z`.

### Verified foundation

- positive even integers admit the choice `(x, y, z) = (k, 2k, 2k)` when `n = 2k`;
- a decomposition scales from `n` to `a n` by scaling all denominators by `a`;
- consequently, a decomposition for a positive divisor lifts to a positive multiple.

### Candidate next targets

1. Formalize the standard identity covering `n ≡ 2 (mod 3)`.
2. Formalize the standard identity covering `n ≡ 3 (mod 4)`.
3. Prove a prime-reduction theorem from the scaling lemma.
4. Add an executable finite search that emits explicit witnesses and independently rechecks the polynomial identity.
5. Represent residue-class coverage without confusing finite verification with a universal proof.

### Guardrail

Computational success over any finite range is evidence, not resolution of the conjecture. The final theorem remains open until `Conjecture` itself has a placeholder-free proof accepted by Lean.
