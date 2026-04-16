# Beanz Core

Beanz Core is the foundational framework mod for the RefriedBeanz Hytale server.

This mod provides the base systems that all other mods will depend on, including player data storage, skill progression, ability execution, and server-side movement systems.

---

## What's Built

### Jump Skill
- Full XP-based progression from level 1 to 100 — XP awarded on every jump
- Jump height scales with level (up to 1.5× at level 100)
- Fall damage reduction scales with level — full immunity at level 100
- Stamina cost per jump decreases as you level up
- Low stamina means a lower jump — stamina scales jump force
- Stamina exhaustion recovery — regen resumes automatically after full drain

### Sky Leap Ability
- Unlocks at jump level 60
- Mid-air boost activated via the Ability 3 key
- One use per airtime, resets on landing
- Uses the same stamina pool as ground jump
- Bound to held item or wearable (helmet slot)
- Input buffering — pressing the key a fraction early still triggers correctly

### Admin Commands
- `/beanzlevel <1-100>` — set your own jump level; syncs XP and ability unlocks
- `/beanzlevel <player> <skill> <level>` — set any online player's skill level
- `/beanz` — base debug command and skill inspector

### Infrastructure
- Per-player persistent skill data via entity component system
- Level-up HUD notifications
- Gradle build with one-step deploy to Hytale Mods folder

---

## Planned

Future work is tracked in [ROADMAP.md](ROADMAP.md), including:

- Bonus XP on landing from height
- Wall jump ability
- Sprint / Endurance skill
- Additional skill trees
- Separate modules: `beanz-factions`, `beanz-raids`, `beanz-taming`, `beanz-world`

---

## Development Notes

See `docs/` for detailed notes on the Hytale engine, system architecture, and build/deploy process.

- Build: `./gradlew jar`
- Deploy: `./gradlew deployToHytale`
- Requires Java 21 (`JAVA_HOME` pointing to a JDK 21+ install)

---

## Author

RefriedBeanz
