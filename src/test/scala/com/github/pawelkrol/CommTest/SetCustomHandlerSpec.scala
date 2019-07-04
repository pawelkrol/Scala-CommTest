package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import TestUtils._

class SetCustomHandlerSpec extends FunSpec {

  outputPrg = testResourcePath("/load.prg")

  labelLog = testResourcePath("/load.log")

  describe("show") {
    context("shared examples") {
      before {
        setCustomHandler("load") {
          writeByteAt("data", get("data").asInstanceOf[Int])
        }
      }

      sharedExamples("setCustomHandler") {
        it("mocks selected subroutine call") {
          expect(call).toChange(readByteAt("data")).to((get("data").asInstanceOf[Int] + 1).toShort)
        }
      }

      let("data") { 0x02 }

      includeExamples("setCustomHandler")

      context("nested mock scope") {
        let("data") { 0x03 }

        includeExamples("setCustomHandler")
      }
    }

    context("custom handler redefined twice") {
      before {
        setCustomHandler("load") { writeByteAt("data", 0x04) }
        setCustomHandler("load") { writeByteAt("data", 0x05) }
      }

      it("calls most recently installed callback") {
        expect(call).toChange(readByteAt("data")).to(0x06)
      }
    }

    context("mocking subroutines called with JMP") {
      before {
        setCustomHandler("decr") { writeByteAt("data", 0x01) }
      }

      it("mocks selected subroutine call") {
        expect(call).toChange(readByteAt("data")).to(0x01)
      }
    }

    context("regular program flow for no subroutine mocks") {
      before {
        setCustomHandler("none") { writeByteAt("data", 0x10) }
      }

      it("executes original program source code") {
        expect(call).toChange(readByteAt("data")).to(0x02)
      }
    }

    context("calling 'setCustomHandler' outside of a 'before' block") {
      setCustomHandler("load") { writeByteAt("data", 0x11) }

      it("takes no effect on a subroutine mocking procedure") {
        setCustomHandler("decr") { writeByteAt("data", 0x12) }
        expect(call).toChange(readByteAt("data")).to(0x02)
      }
    }

    context("original subroutine call invoked within custom handler code") {
      before {
        setCustomHandler("load") {
          writeByteAt("data", 0x20)
          callOriginal
        }
      }

      it("mocks selected subroutine call and calls original from within custom handler code") {
        expect(call).toChange(readByteAt("data")).to(0x22)
      }
    }

    context("original subroutine call invoked without any special setup") {
      before {
        setCustomHandler("load") {
          callOriginal
        }
      }

      it("mocks selected subroutine call and calls original as if nothing has been changed") {
        expect(call).toChange(readByteAt("data")).to(0x02)
      }
    }

    context("original subroutine call invoked outside custom handler code") {
      before {
        setCustomHandler("load") {
          writeByteAt("data", 0x21)
        }
      }

      it("forbids calling original subroutine outside of a custom handler context") {
        val exception = intercept[UnsupportedOperationException] {
          callOriginal
        }
        assert(exception.getMessage === "Original subroutine call not allowed outside of a custom handler context")
      }
    }
  }
}
