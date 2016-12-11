package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import MiscUtils._

class MemoryWriterSpec extends FunSpec {

  labelLog = """
    init = $c000
  """

  private val bytes: List[ByteVal] = List(0x18, 0xd4)

  sharedExamples("verify data") {
    it("writes bytes to a specified memory location") {
      assert(readBytesAt("init", 0x02) === bytes)
    }
  }

  describe("writeByteAt") {
    describe("by address") {
      before {
        writeByteAt(0xc000, bytes(0))
        writeByteAt(0xc001, bytes(1))
      }

      includeExamples("verify data")
    }

    describe("by name") {
      before {
        writeByteAt("init", bytes(0))
        writeByteAt("init + $01", bytes(1))
      }

      includeExamples("verify data")
    }
  }

  describe("writeBytesAt") {
    describe("by address") {
      before {
        writeBytesAt(0xc000, bytes: _*)
      }

      includeExamples("verify data")
    }

    describe("by name") {
      before {
        writeBytesAt("init", bytes: _*)
      }

      includeExamples("verify data")
    }
  }

  describe("writeWordAt") {
    describe("by address") {
      before {
        writeWordAt(0xc000, seq2addr(bytes))
      }

      includeExamples("verify data")
    }

    describe("by name") {
      before {
        writeWordAt("init", seq2addr(bytes))
      }

      includeExamples("verify data")
    }
  }
}
