package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

object MiscUtils {

  def hex2int(hex: String) = Integer.parseInt(hex, 16)

  def isHexDigit(digit: Char) =
    digit match {
      case hex if 0x30 to 0x39 contains hex => true // "0" .. "9"
      case hex if 0x41 to 0x46 contains hex => true // "A" .. "F"
      case hex if 0x61 to 0x66 contains hex => true // "a" .. "f"
      case _ => false
    }

  def seq2addr(seq: Seq[ByteVal]) = (seq(0)() + (seq(1)() << 8)).toShort

  def string2int(string: String) = Integer.parseInt(string, 10)

  def word2nibbles(word: Short) = (word & 0xff, (word >> 8) & 0xff)
}
