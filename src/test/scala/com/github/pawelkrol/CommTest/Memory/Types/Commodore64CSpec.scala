package com.github.pawelkrol.CommTest
package Memory.Types

class Commodore64CSpec extends FunSpec {

  // memoryType = Memory.Commodore64C

  // plus60kEnabled = false

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

  describe("with +60k RAM extension disabled") {
    it("simulates only an onboard RAM bank") {
      writeByteAt(0xd100, 0x00)
      writeByteAt(0x1000, 0x78)
      writeByteAt(0xd100, 0x80)
      writeByteAt(0x1000, 0x58)

      writeByteAt(0xd100, 0x00)
      assert(readByteAt(0x1000) === 0x58)
      writeByteAt(0xd100, 0x80)
      assert(readByteAt(0x1000) === 0x58)
    }
  }
}
