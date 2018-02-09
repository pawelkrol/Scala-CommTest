package com.github.pawelkrol.CommTest
package Memory.Types

class Plus60kEnabledSpec extends FunSpec {

  // memoryType = Memory.Commodore64C

  plus60kEnabled = true

  describe("with +60k RAM extension enabled") {
    it("simulates an additional RAM bank") {
      writeByteAt(0xd100, 0x00)
      writeByteAt(0x1000, 0x78)
      writeByteAt(0xd100, 0x80)
      writeByteAt(0x1000, 0x58)

      writeByteAt(0xd100, 0x00)
      assert(readByteAt(0x1000) === 0x78)
      writeByteAt(0xd100, 0x80)
      assert(readByteAt(0x1000) === 0x58)
    }
  }
}
