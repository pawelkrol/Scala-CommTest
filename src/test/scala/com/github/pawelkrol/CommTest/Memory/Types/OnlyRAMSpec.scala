package com.github.pawelkrol.CommTest
package Memory.Types

class OnlyRAMSpec extends FunSpec {

  memoryType = Memory.OnlyRAM

  describe("with RAM everywhere") {
    it("ignores selection of a memory address space based on the '$01' I/O register value") {
      writeByteAt(0x0001, 0x37)
      assert(readByteAt(0xa000) === 0xff)
      writeByteAt(0xa000, 0x78)
      assert(readByteAt(0xa000) === 0x78)
      writeByteAt(0x0001, 0x36)
      assert(readByteAt(0xa000) === 0x78)
    }
  }
}
