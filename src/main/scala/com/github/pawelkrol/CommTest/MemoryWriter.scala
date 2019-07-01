package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import MiscUtils._

trait MemoryWriter extends CPU6502Spec {

  protected def writeByteAt(address: Int, value: ByteVal): Unit = {
    writeByteAt(address.toShort, value)
  }

  protected def writeByteAt(address: Short, value: ByteVal): Unit = {
    memory.write(address, value)
  }

  protected def writeByteAt(name: String, value: ByteVal): Unit = {
    writeByteAt(label2address(name), value)
  }

  protected def writeBytesAt(address: Int, values: ByteVal*): Unit = {
    writeBytesAt(address.toShort, values: _*)
  }

  protected def writeBytesAt(address: Short, values: ByteVal*): Unit = {
    values.zipWithIndex.foreach({ case (value, index) => writeByteAt(address + index, value) })
  }

  protected def writeBytesAt(name: String, values: ByteVal*): Unit = {
    writeBytesAt(label2address(name), values: _*)
  }

  protected def writeWordAt(address: Int, value: Int): Unit = {
    writeWordAt(address, value.toShort)
  }

  protected def writeWordAt(address: Int, value: Short): Unit = {
    writeWordAt(address.toShort, value)
  }

  protected def writeWordAt(address: Short, value: Int): Unit = {
    writeWordAt(address, value.toShort)
  }

  protected def writeWordAt(address: Short, value: Short): Unit = {
    val (lo, hi) = word2nibbles(value)
    writeBytesAt(address, lo, hi)
  }

  protected def writeWordAt(name: String, value: Int): Unit = {
    writeWordAt(name, value.toShort)
  }

  protected def writeWordAt(name: String, value: Short): Unit = {
    writeWordAt(label2address(name), value)
  }
}
