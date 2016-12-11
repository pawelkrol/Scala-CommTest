package com.github.pawelkrol.CommTest

class MemoryReaderSpec extends FunSpec {

  labelLog = """
    init = $1000
  """

  before {
    writeBytesAt(0x1000, 0x4c, 0x93, 0x10)
  }

  describe("readByteAt") {
    describe("by address") {
      it("reads a byte value at a given address") {
        assert(readByteAt(0x1000) === 0x4c)
        assert(readByteAt(0x1000.toShort) === 0x4c)
      }
    }

    describe("by name") {
      it("reads a byte value at a given label") {
        assert(readByteAt("init") === 0x4c)
      }
    }
  }

  describe("readBytesAt") {
    describe("by address") {
      it("reads byte values at a given address") {
        assert(readBytesAt(0x1000, 0x03) === List(0x4c, 0x93, 0x10))
        assert(readBytesAt(0x1000.toShort, 0x03) === List(0x4c, 0x93, 0x10))
      }
    }

    describe("by name") {
      it("reads byte values at a given label") {
        assert(readBytesAt("init", 0x03) === List(0x4c, 0x93, 0x10))
      }
    }

    describe("by names") {
      it("reads byte values at given labels") {
        assert(readBytesAt("init") === List(0x4c))
        assert(readBytesAt("init", "init") === List(0x4c, 0x4c))
      }
    }
  }

  describe("readWordAt") {
    describe("by address") {
      it("reads a word value at a given address") {
        assert(readWordAt(0x1000) === 0x934c.toShort)
        assert(readWordAt(0x1000.toShort) === 0x934c.toShort)
      }
    }

    describe("by name") {
      it("reads a word value at a given label") {
        assert(readWordAt("init") === 0x934c.toShort)
      }
    }
  }
}
