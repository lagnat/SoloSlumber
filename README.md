# SoloSlumber - A Minecraft pluging for single-player sleep.

### SoloSlumber allows any player to initiate the skip-night feature of Minecraft but gives the other players a chance to wakeup the sleeper if they prefer not to skip night.  SoloSlumber only notifies players that are in the same world as the sleeper.

## Config
```yml
# Number of ticks to sleep a player sleep before making it day
napTime: 100

# Does waking up a sleeper revert to vanilla sleep? Set this to
# `true` to allow anyone to veto single-player sleep for one
# night cycle.  This setting also enables the wantnight command.
# NOTE that feature depends on world time not going backwards.
# Commands like built-in command `/time set day` resets world
# time back to zero.  Consider Essentials `/day` instead.
wakerForcesNight: false

# The worlds that this plugin should listen for sleepers.
# Note that each world is handled separately.  Sleeping off
# night in a particular world will not affect other worlds
# listed here.
worlds:
  - world

messages:
  sleep_warning: '§b%player_name% is sleeping'
  woken_up: '§b%player_name% woke you up!'
  night_skipped: '§bNight was skipped!'
  click_here: '  §9Click here to wake up the sleeper(s)'
  wake_them_up: 'Wake them up'
  woke_them_up: '§bYou just woke up the sleeper(s)'
  skip_night_vetoed: '§b%player_name% has forced night.  Night forcers must sleep to skip night.'
  woke_up: '§b%player_name% woke up the sleeper(s)'
  wants_night: '§b%player_name% is requesting night'
  got_up: '§b%player_name% got out of bed'
```
## Commands
* `/soloslumber wake <player>` - Wake the sleepers
* `/soloslumber wantnight` - Prevent single player sleep for 1 night cycle
* `/soloslumber reload` - Reload the configuration.  Requires the `soloslumber.reload` permission.
