# Exchequer

Exchequer is a laboratory for AI-assisted mathematical research with machine-checked proofs.

The working arrangement is simple:

- the AI proposes definitions, lemmas, proof strategies, counterexample searches, and formal proofs;
- Lean 4 and Mathlib check the formal mathematics;
- GitHub preserves the research trail, including failed approaches and unresolved obligations;
- CI refuses proof files containing `sorry` or `admit`.

A green build means the checked Lean declarations are accepted by the kernel. It does **not** mean an open conjecture has been solved unless the repository contains a complete theorem formalizing that conjecture and CI accepts it.

## Current target

The first research workspace is the Erdős–Straus conjecture. The initial Lean file defines the conjecture in an integer, denominator-cleared form and proves a few foundational closure results and elementary cases.

## Repository layout

```text
Exchequer.lean                         library root
Exchequer/Basic.lean                  project sanity checks
Exchequer/Problems/ErdosStraus/       first formal research target
research/                              informal notes and experiment logs
.github/workflows/lean.yml            kernel-checking CI
```

## Local use

Install Lean through `elan`, then run:

```bash
lake update
lake exe cache get
lake build
```

The project is pinned to the same stable Lean/Mathlib release in `lean-toolchain` and `lakefile.toml`.

## Proof-status vocabulary

- **Verified:** represented by Lean declarations that compile in CI.
- **Experimentally supported:** checked over a finite range by code, but not proved.
- **Candidate:** a proposed statement or proof step awaiting formal verification.
- **Open:** no complete proof is present.

Informal research notes are never proof certificates. Lean files and CI are the source of truth.
