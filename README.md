# Beanz Core

Beanz Core is the foundational framework mod for the RefriedBeanz Hytale server.

This mod provides the base systems that all other mods will depend on, including player data storage, skill progression, ability execution, and server-side movement systems.

---

## What's Built

### Jump Skill System
- Full XP-based progression from level 1 to 100
- Passive ground jump boost that scales with level
- Stamina cost per jump that decreases as you level up
- Stamina scaling on jump force — low stamina means lower jumps
- Stamina exhaustion recovery

### SKY_LEAP Ability
- Unlocks at jump level 60
- Airborne-only mid-air boost activated via Ability3 input
- Bound to held item or wearable (helmet slot)
- Uses shared stamina pool with ground jump
- One use per airtime, resets on landing

### Admin Commands
- `/beanzlevel <1-100>` — set your own jump level for testing
- `/beanz` — base debug command

### Infrastructure
- Per-player persistent skill data via entity component system
- Level-up HUD notifications
- Fall damage system
- Gradle build with one-step deploy to Hytale Mods folder

---

## Planned

Future work is tracked in [ROADMAP.md](ROADMAP.md), including:

- `/beanzsetlevel <player> <skill> <level>` admin command
- XP gain on jumps and landings
- Double jump and wall jump abilities
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
