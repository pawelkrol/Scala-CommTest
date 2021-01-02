CHANGES
=======

0.06-SNAPSHOT (2021-01-02)
--------------------------

* New feature: Add `toEqual` keyword enabling expectation syntax with any expressions
* `Scala` library version updated to 2.13.4, `sbt` build tool version updated to 1.4.5, `sbt-pgp` plugin version updated to 2.0.2, `scalatest` framework version updated to 3.2.3, `commons-lang3` utilities updated to version 3.11, `cpu-6502-simulator` dependency version updated to 0.06-SNAPSHOT, `afterimage` library version updated to 0.08-SNAPSHOT

0.05 (2019-07-06)
-----------------

* Bug fixed: Prevent execution of a `call` method within `setCustomHandler` code block in order to ensure that no infinite loop is accidentally created in user tests
* New feature: Add support for `callOriginal` method to be called within `setCustomHandler` code block, which executes an original subroutine implementation from within its mocked version
* `Scala` library version updated to 2.13.0, `sbt-pgp` plugin version updated to 1.1.1, `scalatest` framework version updated to 3.0.8, `commons-lang3` utilities updated to version 3.9, `cpu-6502-simulator` dependency version updated to 0.05, `afterimage` library version updated to 0.07

0.04 (2019-01-23)
-----------------

* Bug fixed: Mock subroutine calls properly when more than just a single label points to the same target memory address (previously only the first entry from a label log was picked when browsing the list of installed mocks for a currrently tested target memory address, completely ignoring all other labels pointing to the same memory location)
* `Scala` library version updated to 2.12.8, `sbt` build tool version updated to 1.2.8, `commons-lang3` utilities updated to version 3.8.1

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
