package com.github.pawelkrol.CommTest

class CPU6502SpecSpec extends FunSpec {

  labelLog = """
    init = $c000
  """

  describe("label2address") {
    it("recognises an existing label") {
      assert(label2address("init") === 0xc000.toShort)
    }

    it("throws exception for a non-existing label") {
      intercept[UnsupportedOperationException] {
        label2address("foobar")
      }
    }

    it("recognises whitespaces wrapped around a label") {
      assert(label2address("init ") === 0xc000.toShort)
      assert(label2address(" init") === 0xc000.toShort)
      assert(label2address(" init ") === 0xc000.toShort)
    }

    it("recognises positive addition to a label") {
      assert(label2address("init + $01") === 0xc001.toShort)
      assert(label2address("init + $0001") === 0xc001.toShort)
      assert(label2address("init + $1000") === 0xd000.toShort)
      assert(label2address("init + $1001") === 0xd001.toShort)
    }

    it("recognises negative addition to a label") {
      assert(label2address("init - $01") === 0xbfff.toShort)
      assert(label2address("init - $0001") === 0xbfff.toShort)
      assert(label2address("init - $1000") === 0xb000.toShort)
      assert(label2address("init - $1001") === 0xafff.toShort)
    }

    it("recognises zero addition to a label") {
      assert(label2address("init + $00") === 0xc000.toShort)
      assert(label2address("init + $0000") === 0xc000.toShort)
      assert(label2address("init - $00") === 0xc000.toShort)
      assert(label2address("init - $0000") === 0xc000.toShort)
    }

    it("handles an arbitrary number of spaces between words") {
      assert(label2address("init+$01") === 0xc001.toShort)
      assert(label2address("init +$01") === 0xc001.toShort)
      assert(label2address("init+ $01") === 0xc001.toShort)
      assert(label2address("init  +$01") === 0xc001.toShort)
      assert(label2address("init+  $01") === 0xc001.toShort)
      assert(label2address("init  +  $01") === 0xc001.toShort)
    }

    it("throws exception for an invalid label") {
      intercept[NumberFormatException] {
        label2address("init i+ $01")
      }
      intercept[NumberFormatException] {
        label2address("init + $01 + $02")
      }
    }

    it("recognises decimal values") {
      assert(label2address("init + 0") === 0xc000.toShort)
      assert(label2address("init + 1") === 0xc001.toShort)
      assert(label2address("init + 10") === 0xc00a.toShort)
      assert(label2address("init - 0") === 0xc000.toShort)
      assert(label2address("init - 1") === 0xbfff.toShort)
      assert(label2address("init - 10") === 0xbff6.toShort)
    }
  }
}
