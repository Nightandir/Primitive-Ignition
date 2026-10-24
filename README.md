# Primitive Ignition (Minecraft 1.21.11 + Fabric)

Prototype for a vanilla-only manual ignition mechanic.

## Current intended behavior
- Applies only to vanilla furnace, blast furnace, and smoker block entities.
- Requires the vanilla flint and steel to manually start a furnace that has an input and fuel.
- Leaves normal flint-and-steel use on other blocks alone.
- Adds no items, blocks, recipes, or world generation.

## Build
Use Java 21 and Gradle 9.2.1 or a compatible Gradle version supported by Fabric Loom 1.14. Run `gradle build`; output should be under `build/libs/`.

## Status
This is an initial source prototype. It has not yet been compiled or tested in Minecraft. Do not treat it as a verified working JAR.
