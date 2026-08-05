# Contributing

Exchequer distinguishes exploration from certification.

## Formal contributions

- Put mathematical declarations under `Exchequer/`.
- Every theorem must build without `sorry` or `admit`.
- State positivity, nonzero, domain, and finiteness assumptions explicitly.
- Prefer small lemmas whose exact mathematical meaning is easy to inspect.
- Do not describe a theorem as proving more than its Lean statement says.

## Experimental contributions

Scripts and finite searches must report:

- the exact range tested;
- the algorithm and stopping conditions;
- explicit witnesses or counterexamples;
- whether results were rechecked independently.

Finite verification must never be labeled a universal proof.

## Informal research notes

Record candidate arguments, dead ends, and ambiguous translations under `research/`. Mark their status plainly. A note becomes verified only when its formal counterpart compiles in CI.
