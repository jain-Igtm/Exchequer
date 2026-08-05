# Exchequer Android

A fully native Android client written in Kotlin and Jetpack Compose.

The app does not execute Lean on the phone. It reads public GitHub state:

1. the latest `Lean verification` workflow run on `main`;
2. the latest successful run and its exact commit SHA;
3. `feed/index.json` from that exact verified commit;
4. recent commits from the GitHub API.

This prevents unverified repository data from being presented as Lean-verified. New proofs, build results, obstructions, mathematics, and commit activity appear without reinstalling the APK.

## Interface contract

Only repository-derived fields are displayed:

- Lean build result, commit, and completion time;
- theorem declarations listed in the feed at the verified commit;
- explicitly recorded obstructions;
- recent commit activity;
- mathematics and Lean source in a native collapsible panel.

There is no WebView and no remotely downloaded UI code.

## Build

GitHub Actions builds the debug APK with JDK 17, Gradle 9.6.1, Android Gradle Plugin 9.4.0, Kotlin 2.4.10, and the Compose 2026.06.00 BOM.
