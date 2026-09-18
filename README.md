# RandomRace

A Paper plugin that gives each player randomly assigned **ValhallaRaces** races and classes
via slot-machine chest GUIs. Race and class pools are pulled **live** from ValhallaRaces at
runtime — there is no duplicate config. Players claim one race (`/claimrace`) and one class
per group via `/claimclass` (10 groups), with optional weighted randomization and admin
re-roll/reset.

## Requirements

- **Paper** 1.21+ (`api-version: 1.21`)
- **ValhallaRaces** (required at runtime)
- **ValhallaMMO** (required by ValhallaRaces, also a hard dependency)

Both are declared as hard dependencies in `plugin.yml`. This plugin does **not** ship or
duplicate any race or class definitions; whatever races/classes exist on the server are used
automatically.


## Tested Ver
Paper 26.1.2 
## Install

Build with Maven, then drop the jar into the server's `plugins/` folder:

```bash
mvn package
# Result: target/randomrace-1.3.0.jar
```

## Commands & Permissions

| Command | Permission | Action |
|---|---|---|
| `/claimrace` | `randomrace.claim` | Run the spin and assign a random race |
| `/claimclass` | `randomrace.class` | Roll one random class per group (3-slot strip) |
| `/rerollrace` | `randomrace.claim` | Spend 1 race reroll to spin a new race |
| `/rerollclass` | `randomrace.class` | Spend 1 class reroll to re-roll all classes up to your slot cap |
| `/raceslot` | `randomrace.claim` | Open your 5 saved-race slots (save, replace, load) |
| `/randomrace reset <player>` | `randomrace.admin` | Remove the player's race, allow re-claim |
| `/randomrace reroll <player>` | `randomrace.admin` | Force a fresh race spin for the player |
| `/randomrace setrace <player> <race>` | `randomrace.admin` | Directly assign a race (no animation) |
| `/randomrace resetclass <player>` | `randomrace.admin` | Clear all of a player's classes |
| `/randomrace rerollclass <player>` | `randomrace.admin` | Clear classes and re-roll all groups |
| `/randomrace setclass <player> <group> <class>` | `randomrace.admin` | Set one class for a group |
| `/randomrace listclass` | `randomrace.admin` | List classes loaded from ValhallaRaces |
| `/randomrace reload` | `randomrace.admin` | Reload config + refresh race/class pools |
| `/randomrace listrace` | `randomrace.admin` | List races loaded from ValhallaRaces |

`randomrace.claim` and `randomrace.class` default to all players; `randomrace.admin`
defaults to ops.

## How it works

- On startup and before each `/claimrace`, the plugin refreshes its pool from
  `RaceManager.getRegisteredRaces()` (ValhallaRaces' live registry).
- Races the player lacks permission for (ValhallaRaces `permission:` key) are hidden from
  their pool.
- A weighted winner is pre-selected, then a 9x1 chest slot-machine animates across 3 phases
  (fast → medium → slow) and lands the winner in the center slot.
- Assignment is done **in-process** via `RaceManager.setRace(player, race)` — this applies
  the race's stat buffs, perk rewards, and configured commands. No command dispatch.
- The "already claimed" gate is `RaceManager.getRace(player) != null`, so it stays in sync
  with ValhallaRaces (e.g. if an admin runs `/races reset race`).

## Classes (`/claimclass`)

- Players get **one class per group** (10 groups). `/claimclass` rolls every group the
  player doesn't already have, one group per 3-slot strip animation, advancing group-by-group.
- Classes are filtered by permission (`permission:` key) and by ValhallaRaces'
  `race_filter` — a class limited to certain races is only offered if your race is listed.
- A weighted winner per group is chosen from `class-weights`, then all classes are assigned
  in-process via `ClassManager.setClasses(player, classes)` (applies stats/perks/commands).
- The "already claimed" gate is `ClassManager.getClasses(player)` — blocked once all 10
  groups are filled.

## Configuration (`config.yml`)

```yaml
race-weights:
  human: 40        # Optional weight per race id. Unlisted races default to equal weight.

one-time-only: true

race-materials:
  human: PLAYER_HEAD   # Optional icon material override per race id.

broadcast-enabled: true

# --- Classes ---
class-weights: {}      # Optional weight per class id. Unlisted = equal weight.
class-materials: {}    # Optional icon material override per class id.

groups:                # Display names for the class groups (1-10)
  1: "Warrior"
  2: "Specialist"
  # ... 10: "Weaver"

class-one-time-only: true
broadcast-class: true

messages:
  already-claimed: "&cYou have already claimed your race!"
  spin-start: "&eThe fates are deciding your race..."
  race-assigned: "&aYou have been chosen as a &e{race}&a!"
  no-permission: "&cYou don't have permission to use this."
  broadcast: "&e{player} &ahas been destined to be a &e{race}&a!"
  class-no-permission: "&cYou don't have permission to use this."
  class-already-claimed: "&cYou already have all your classes!"
  class-spin-start: "&eThe fates are choosing your classes..."
  class-assigned: "&eYou are now a &b{group} {class}&e!"
  class-summary: "&aYour classes are now: &e{classes}&a!"
  class-broadcast: "&e{player} &ahas been destined with their classes!"
```

`{race}`, `{player}`, `{group}`, `{class}`, and `{classes}` are replaced in message strings.
Race/class ids in `race-weights` / `race-materials` / `class-weights` / `class-materials`
must match the ValhallaRaces `races.yml` / `classes.yml` keys.

## Build note

`me.athlaeos:valhallaraces` is not published to any Maven repository, so this project
compiles against a compile-time-only stub of the `Race` / `RaceManager` and
`Class` / `ClassManager` APIs (`src/main/stub/`, excluded from the packaged jar). The real
ValhallaRaces classes are used at runtime. If a future ValhallaRaces release changes one of
these signatures, update the stub and recompile.
