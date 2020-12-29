package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import org.scalatest.funspec.AnyFunSpec

import MiscUtils._

class MiscUtilsSpec extends AnyFunSpec {

  describe("isHexDigit") {
    it("recognises hexadecimal digit") {
      def areHexDigits(digits: List[Char], expected: Boolean) =
        digits.map(isHexDigit _).filterNot(_ == expected).isEmpty

      assert(areHexDigits(List('0', '9', 'A', 'F', 'a', 'f'), true))
      assert(areHexDigits(List('/', ':', '@', 'G', '`', 'g'), false))
    }
  }

  describe("hex2int") {
    it("converts hexadecimal number to integer") {
      assert(hex2int("C000") == 0xc000)
      assert(hex2int("c000") == 0xc000)
    }
  }

  describe("seq2addr") {
    it("converts sequence of 2 byte values to address") {
      assert(seq2addr(Seq[ByteVal](0x00, 0x10)) == 0x1000)
    }
  }
}
