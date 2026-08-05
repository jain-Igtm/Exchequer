import Mathlib

set_option autoImplicit false

namespace Exchequer.ErdosStraus

/--
`HasDecomposition n` is the denominator-cleared form of

`4 / n = 1 / x + 1 / y + 1 / z`

for positive natural-number denominators `x`, `y`, and `z`.

The polynomial identity avoids division while retaining exactly the intended
Diophantine content for positive denominators.
-/
def HasDecomposition (n : ℕ) : Prop :=
  ∃ x y z : ℕ,
    0 < x ∧ 0 < y ∧ 0 < z ∧
      4 * (x * y * z) = n * (y * z + x * z + x * y)

/-- The Erdős–Straus conjecture as a proposition. This definition is not a proof. -/
def Conjecture : Prop :=
  ∀ n : ℕ, 2 ≤ n → HasDecomposition n

/-- The first nontrivial instance: `4 / 2 = 1 + 1 / 2 + 1 / 2`. -/
theorem two_has_decomposition : HasDecomposition 2 := by
  refine ⟨1, 2, 2, by norm_num, by norm_num, by norm_num, ?_⟩
  norm_num

/-- Every positive even integer satisfies the conjectured decomposition. -/
theorem even_family (k : ℕ) (hk : 0 < k) : HasDecomposition (2 * k) := by
  refine ⟨k, 2 * k, 2 * k, hk, by omega, by omega, ?_⟩
  ring

/--
A decomposition scales to every positive multiple: multiplying `n` by `a`
and all three denominators by `a` preserves the unit-fraction identity.
-/
theorem scale_decomposition {n : ℕ} (a : ℕ) (ha : 0 < a)
    (h : HasDecomposition n) : HasDecomposition (a * n) := by
  rcases h with ⟨x, y, z, hx, hy, hz, hEq⟩
  refine ⟨a * x, a * y, a * z,
    Nat.mul_pos ha hx, Nat.mul_pos ha hy, Nat.mul_pos ha hz, ?_⟩
  calc
    4 * ((a * x) * (a * y) * (a * z)) = a ^ 3 * (4 * (x * y * z)) := by ring
    _ = a ^ 3 * (n * (y * z + x * z + x * y)) := by rw [hEq]
    _ = (a * n) * ((a * y) * (a * z) + (a * x) * (a * z) + (a * x) * (a * y)) := by ring

/-- A verified decomposition for a positive divisor lifts to a positive multiple. -/
theorem lift_along_multiple {m n : ℕ} (hn : 0 < n) (hdiv : m ∣ n)
    (h : HasDecomposition m) : HasDecomposition n := by
  rcases hdiv with ⟨a, rfl⟩
  have ha : 0 < a := by
    by_contra hnot
    have ha0 : a = 0 := Nat.eq_zero_of_not_pos hnot
    subst a
    simp at hn
  simpa [Nat.mul_comm] using scale_decomposition a ha h

end Exchequer.ErdosStraus
