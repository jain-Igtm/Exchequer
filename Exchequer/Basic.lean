import Mathlib

set_option autoImplicit false

namespace Exchequer

/-- A tiny kernel-checked sanity theorem for the project. -/
theorem add_zero_verified (n : ℕ) : n + 0 = n := by
  simp

end Exchequer
