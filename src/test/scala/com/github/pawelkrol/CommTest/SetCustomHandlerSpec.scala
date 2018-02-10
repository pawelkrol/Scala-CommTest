package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import TestUtils._

class SetCustomHandlerSpec extends FunSpec {

  outputPrg = testResourcePath("/load.prg")

  labelLog = testResourcePath("/load.log")

  describe("show") {
    context("shared examples") {
      before {
        set_custom_handler("load") {
          writeByteAt("data", get("data").asInstanceOf[Int])
        }
      }

      sharedExamples("set_custom_handler") {
        it("mocks selected subroutine call") {
          expect(call).toChange(readByteAt("data")).to((get("data").asInstanceOf[Int] + 1).toShort)
        }
      }

      let("data") { 0x02 }

      includeExamples("set_custom_handler")

      context("nested mock scope") {
        let("data") { 0x03 }

        includeExamples("set_custom_handler")
      }
    }

    context("custom handler redefined twice") {
      before {
        set_custom_handler("load") { writeByteAt("data", 0x04) }
        set_custom_handler("load") { writeByteAt("data", 0x05) }
      }

      it("calls most recently installed callback") {
        expect(call).toChange(readByteAt("data")).to(0x06)
      }
    }

    context("mocking subroutines called with JMP") {
      before {
        set_custom_handler("decr") { writeByteAt("data", 0x01) }
      }

      it("mocks selected subroutine call") {
        expect(call).toChange(readByteAt("data")).to(0x01)
      }
    }

    context("regular program flow for no subroutine mocks") {
      before {
        set_custom_handler("none") { writeByteAt("data", 0x10) }
      }

      it("executes original program source code") {
        expect(call).toChange(readByteAt("data")).to(0x02)
      }
    }

    context("calling 'set_custom_handler' outside of a 'before' block") {
      set_custom_handler("load") { writeByteAt("data", 0x11) }

      it("takes no effect on a subroutine mocking procedure") {
        set_custom_handler("decr") { writeByteAt("data", 0x12) }
        expect(call).toChange(readByteAt("data")).to(0x02)
      }
    }
  }
}
