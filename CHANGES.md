CHANGES
=======

0.04-SNAPSHOT (2018-02-28)
--------------------------

* TODO

0.03 (2018-02-28)
-----------------

* New feature: Enable screenshot capture of a presently displayed screen as well as a screen from an arbitrary memory location and save it as a _PNG_ file (currently limited to `HiRes` and `MultiColour` graphic modes, optionally including all visible sprites)
* `cpu-6502-simulator` dependency version updated to 0.04

0.02 (2018-02-10)
-----------------

* New feature: Provide an option to parameterise spec execution properties through a set of newly recognised attributes:
  * `memoryType` defines a RAM variant virtually connected to a simulated CPU (it replaces a default behaviour of RAM everywhere and respects selection of an appropriate memory address space based on the `$01` I/O register value)
  * `plus60kEnabled` enables a +60k RAM extension simulation
* New feature: Allow mocking of individual subroutine calls
* `Scala` library version updated to 2.12.4, `sbt-pgp` plugin version updated to 1.1.0, `scalatest` framework version updated to 3.0.5, `commons-lang3` utilities updated to version 3.7, `cpu-6502-simulator` dependency version updated to 0.03

0.01 (2016-12-25)
-----------------

* Initial version (provides a complete unit-testing framework designed to verify correctness of compiled assembly programs targetting MOS 6502 CPU)
