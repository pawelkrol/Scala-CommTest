Scala-CommTest
==============

`CommTest` is the first project of its kind, a complete unit-testing framework designed to verify correctness of compiled assembly programs targetting MOS 6502 CPU, entirely written in [Scala](http://www.scala-lang.org/), and built on top of a very popular unit-testing framework [ScalaTest 3.0.1](http://www.scalatest.org/).

VERSION
-------

Version 0.04-SNAPSHOT (2018-02-28)

INSTALLATION
------------

You can automatically download and install this library by adding the following dependency information to your `build.sbt` configuration file:

    resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

    libraryDependencies += "com.github.pawelkrol" % "commtest" % "0.04-SNAPSHOT" % "test"

PREREQUISITES
-------------

In order to execute your test suite, you must first provide a compiled program file with a corresponding label log. A reasoning behind this decision (requiring users to compile their source code themselves rather than letting test framework execute it automatically) is to enable anyone to use an arbitrary assembler, so that anyone can stick to whatever their preferences are. For instance, in order to generate an output program with a complementary label log using [dreamass](https://github.com/docbacardi/dreamass) from an example source code file named `math.src`, you would type:

    $ dreamass --max-errors 10 --max-warnings 10 --verbose -Wall --label-log math.log --output math.prg math.src

If you prefer to use a different compiler, please consult a documentation of your tool of choice in order to find out how to generate a label log while assembling a source code.

INITIAL SETUP
-------------

Your test suite is expected to extend `FunSpec` class from `com.github.pawelkrol.CommTest` package:

    import com.github.pawelkrol.CommTest.FunSpec

    class MySpec extends FunSpec {

      outputPrg = "math.prg"

      labelLog = "math.log"

      // ...test examples...
    }

There are just two mandatory properties that each test class has to define:

* `labelLog` is an output file containing a label log corresponding to your compiled program
* `outputPrg` is an output program file in a binary format created when compiling your source code

It is also possible (although not a very convenient solution) to provide label log as a plain text instead of reading it from an external file:

    labelLog = """
      init = $1000
      play = $1003
    """

OPTIONAL PARAMETERS
-------------------

`CommTest` recognises the following optional properties that may be configured on a spec definition level:

    memoryType = Memory.Commodore64C

    plus60kEnabled = false

* `memoryType` sets a RAM variant virtually connected to a simulated CPU
  * default value: `Memory.Commodore64C`
  * possible values: `Memory.Commodore64C`, `Memory.OnlyRAM`
* `plus60kEnabled` enables a +60k RAM extension (it is however ignored unless `memoryType` equals to `Memory.Commodore64C`)
  * default value: `false`
  * possible values: `false`, `true`

DESCRIBING TESTS
----------------

Under the hood `CommTest`'s `FunSpec` extends `ScalaTest`'s `FunSpec`. Hence you wrap your tests with `describe` clauses and place your test examples inside `it` clauses. If you prefer you may use `context` keyword alternately with `describe`, for `context` is just an alias of `describe`.

    describe("divide") {

      context("when the divisor is zero") {

        // ...test examples...
      }
    }

`context` and `describe` sections may be nested and span an arbitrary number of levels.

ACCESSING MEMORY
----------------

Free access to a computer's memory before and after execution of your program is a premier method of ascertaining your code to behave correctly. `CommTest` exposes a collection of methods to read from and write into memory. All of them may be accessed either directly (via hexadecimal address of a target location) or indirectly (via a subroutine name listed in a label log file).

`readByteAt` and `writeByteAt` methods read/write a single byte memory value:

    val result = readByteAt("result")
    val result = readByteAt(0x1038)

    writeByteAt("result", 0x01)
    writeByteAt(0x1038, 0x01)

`readWordAt` and `writeWordAt` methods read/write two byte (a single word) memory values:

    val divisor = readWordAt("divisor")
    val divisor = readWordAt(0x0020)

    writeWordAt("divisor", 0x0003)
    writeWordAt(0x0020, 0x0003)

`readBytesAt` and `writeBytesAt` methods read/write an arbitrary number of byte memory values:

    val offset = readBytesAt("offset", 0x03)
    val offset = readBytesAt(0x1052, 0x03)

    writeBytesAt("offset", 0x00, 0x28, 0x50)
    writeBytesAt(0x1052, 0x00, 0x28, 0x50)

BEFORE FILTERS
--------------

`before` filters allow you to specify a common behaviour that is shared across all test examples in the same scope where `before` definition originally appears. For instance, if your division computation's divisor is accessible via `divisor` label and you want to share its assignment across several test examples, you may specify a separate context and execute common assignment once inside a `before` filter:

    context("when the divisor is zero") {

      before {
        writeWordAt("divisor", 0x0000)
      }

      // ...test examples...
    }

FLAGS AND REGISTERS
-------------------

`CommTest` provides a direct read/write access to all processor registers and status flags. They can be referred via the following symbolic names:

| Symbol | Description          |
| ------ | -------------------- |
| `AC`   | Accumulator register |
| `XR`   | Index register X     |
| `YR`   | Index register Y     |
| `PC`   | Program counter      |
| `SR`   | Status register      |
| `SP`   | Stack pointer        |
| `BF`   | Break flag           |
| `CF`   | Carry flag           |
| `DF`   | Decimal flag         |
| `IF`   | Interrupt flag       |
| `OF`   | Overflow flag        |
| `SF`   | Sign flag            |
| `ZF`   | Zero flag            |

Each symbol may be used both in an assignment instruction as well as a return value:

    AC = 0x80
    CF = false

    assert(SF === false)
    println(XR)

CALLING SUBROUTINES
-------------------

Every subroutine name defined in a label log may be called and executed directly from your test examples. It is as simple as calling a method named `call` and providing a subroutine name as an argument. It will execute code residing at a specified memory location and return control to your test program as soon as a subroutine called executes `RTS`.

    call("divide")

You may also avoid typing subroutine name each time you want to call it and leave out an argument if you specify label as an argument to an outside `describe` block. From that point on each `call` will execute described subroutine.

    describe("divide") {

      it("computes division") {

        call
        // ...test assertions...
      }
    }

MOCKING SUBROUTINE CALLS
------------------------

It may sometime happen that one of the subroutine calls executed in the course of a running program triggers some code that a CPU simulator is unable to complete. This may for example be some asynchronous function when a main program loop awaits an IRQ interrupt to accomplish a timeboxed task, or a communication with a peripheral device that cannot ever be finished because there is no implementation of simulated devices communicating with external interfaces of a physical computer. In these situations installing mock handlers of subroutine calls comes in handy.

Imagine having a subroutine named `show_image` that is a subject to your tests which performs multiple operations, and one of them is loading a file from disk accomplished by calling `jsr loader`. A 6502 CPU simulator will never fetch the data from disk because it is currently outside of its implementation scope, so your test examples would end up running in an infinite loop. What you may do to mitigate these risks is providing your own custom implementation of selected subroutine calls to ensure their successful execution:

    before {

      set_custom_handler("loader") {

        // ...alternative implementation...
      }
    }

They will always be executed in place of each mocked subroutine. Note that invoking `set_custom_handler` is only allowed within a scope of a `before` block (calling it anywhere else will have no effect at all on a test execution!). See an example spec file [SetCustomHandlerSpec.scala](src/test/scala/com/github/pawelkrol/CommTest/SetCustomHandlerSpec.scala) illustrating a simple usage pattern.

ASSERTIONS AND EXPECTATIONS
---------------------------

This is pure [Scala](http://www.scala-lang.org/), so you can write any [Scala](http://www.scala-lang.org/) code you want to edit your tests. That means anything available in a [Predef](http://www.scala-lang.org/api/current/scala/Predef$.html) package, including (but not limited to) `assert` function, will work. You may bring them into play when writing your test examples:

    assert(readByteAt("init") === 0x4c)
    assert(AC === 0x00)

But there is a better way provided by `CommTest`: expectations! They are designed to read better and resemble natural language more than ordinary assertions. And they give you even more flexibility. They will let you automatically verify if a target value changes in an exact way you want it:

    expect(call).toChange(AC).from(0x00).to(0x80)
    expect(call).toChange(AC).to(0x80)
    expect(call).notToChange(AC)

SHARED EXAMPLES
---------------

Shared examples give you another opportunity to implement a DRY _(Don't Repeat Yourself)_ principle when writing your tests. They let you define your test examples just once and execute them multiple times from different contexts by simply including those test examples any number of times:

    describe("shared examples") {

      sharedExamples("argument validation") {

        // ...shared test examples...
      }

      sharedExamples("result computation") {

        // ...shared test examples...
      }

      context("one set of arguments") {

        before {
          // ...context specific initializations...
        }

        includeExamples("argument validation")
        includeExamples("result computation")
      }

      context("another set of arguments") {

        before {
          // ...context specific initializations...
        }

        includeExamples("argument validation")
        includeExamples("result computation")
      }
    }

LOCAL VARIABLES
---------------

With `let` and `get` keywords you are able to define and use local variables that may be reassigned in different scopes and contexts. Although [Scala](http://www.scala-lang.org/) allows you to define variables, you either need to scope them to a class level or to initialize them with an arbitrary value, otherwise your code will not compile at all _(local variables must be initialized)._

With `CommTest` you may define a new variable that is accessible from anywhere within your test class, and at the same time have its value lexically scoped to a block in which definition statement appears. This allows you to write fancy shared examples that have different value assigned to the same variable in different contexts as demonstranted in an example below. In order to assign a new value to your variable in the current scope use `let` keyword, and in order to fetch its value anywhere in your tests use `get` keyword.

    describe("divide") {
      sharedExamples("validate quotient") {
        it("finishes computations with a valid fraction") {
          val quotient = get("dividend").asInstanceOf[Int] / get("divisor").asInstanceOf[Int]
          call
          assert(readWordAt("result") == quotient)
        }
      }

      before {
        writeWordAt("dividend", get("dividend").asInstanceOf[Int])
        writeWordAt("divisor", get("divisor").asInstanceOf[Int])
      }

      context("$01 / $02") {
        let("dividend") { 0x0100 }
        let("divisor") { 0x0002 }

        includeExamples("validate quotient")
      }

      context("$02 / $03") {
        let("dividend") { 0x0200 }
        let("divisor") { 0x0003 }

        includeExamples("validate quotient")
      }
    }

One caveat is that you need to explicitly state what is the class of a fetched variable by using `.asInstanceOf[ClassName]`. What you get in return is an extremely flexible system of locally scoped variables that may be reassigned and referred to in different contexts of your tests.

RELATIVE ADDRESSING
-------------------

When referring to a specific memory address via label, it is possible to apply an arbitrary offset to the target address before fetching/storing an actual value from/into memory. This is as simple as including a chosen offset next to a label:

    // Given that init = $c000...
    writeByteAt("init", 0x00)     // ...will store $00 in $c000
    writeByteAt("init + 1", 0x01) // ...will store $01 in $c001

DISABLING TESTS
---------------

You may temporarily ignore individual test examples as well as groups of tests by using `xit` and `xdescribe` keywords in place of `it` and `describe` respectively:

    xdescribe("divide") {

      // ...no tests in this context will be executed...
    }

    describe("divide") {

      xit("computes division") {

        // ...only this test example will be skipped...
      }
    }

CAPTURING SCREENSHOTS
---------------------

In order to help with debugging of a source code it is possible to save a _PNG_ screenshot of a currently displayed screen using the following method:

    val targetFile = "screenshot.png"

    captureScreenshot(
      targetFile = targetFile
    )

Alternatively creating an in-memory representation of a captured screen image is also possible. The following code will create an `ij.ImagePlus` object and display it in a separate preview window:

    val screenshot = captureScreenshot()

    screenshot.show()

Sometimes you may want to capture a screenshot from an arbitrary memory location rather than using a currently displayed screen. It is possible to specify some optional parameters to each `captureScreenshot` call:

    val screenshot = captureScreenshot(
      bitmapMode = Some(true),
      multiColourMode = Some(true),
      screenAddress = Some(0x6000),
      bitmapAddress = Some(0x4000),
      colorsAddress = Some(0x6400),
      backgroundColour = Some(0x00)
    )

In order to exclude rendering sprites from a captured image you only need to provide an additional argument to `captureScreenshot` method call (which normally defaults to `true`):

    val screenshot = captureScreenshot(
      includeSprites = false
    )

COPYRIGHT AND LICENCE
---------------------

Copyright (C) 2016, 2018 by Pawel Krol.

This library is free open source software; you can redistribute it and/or modify it under [the same terms](https://github.com/pawelkrol/Scala-CommTest/blob/master/LICENSE.md) as Scala itself, either Scala version 2.12.4 or, at your option, any later version of Scala you may have available.
