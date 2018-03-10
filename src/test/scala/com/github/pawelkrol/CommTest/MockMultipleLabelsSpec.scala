package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import TestUtils._

class MockMultipleLabelsSpec extends FunSpec {

  outputPrg = testResourcePath("/mock.prg")

  labelLog = testResourcePath("/mock.log")

  describe("test") {
    context("multiple labels pointing to the same target memory addresss") {
      context("applying mock to 'mock' label") {
        before {
          setCustomHandler("mock") { AC = 0x02 }
        }

        it("calls installed callback") {
          expect(call).toChange(AC).from(0x00).to(0x02)
        }
      }

      context("applying mock to 'mock1' label") {
        before {
          setCustomHandler("mock1") { AC = 0x03 }
        }

        it("calls installed callback") {
          expect(call).toChange(AC).from(0x00).to(0x03)
        }
      }

      context("applying mock to 'mock2' label") {
        before {
          setCustomHandler("mock2") { AC = 0x04 }
        }

        it("calls installed callback") {
          expect(call).toChange(AC).from(0x00).to(0x04)
        }
      }
    }
  }
}
