package com.github.pawelkrol.CommTest
package Memory.Types

class Commodore64CSpec extends FunSpec {

  // memoryType = Memory.Commodore64C

  describe("with Commodore 64C RAM/ROM") {
    it("respects selection of a memory address space based on the '$01' I/O register value") {
      writeByteAt(0x0001, 0x37)
      assert(readByteAt(0xa000) === 0x94)
      writeByteAt(0xa000, 0x78)
      assert(readByteAt(0xa000) === 0x94)
      writeByteAt(0x0001, 0x36)
      assert(readByteAt(0xa000) === 0x78)
    }
  }
}
