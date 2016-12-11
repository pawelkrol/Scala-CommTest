package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import MiscUtils._

trait MemoryReader extends CPU6502Spec {

  protected def readByteAt(address: Int): ByteVal =
    readByteAt(address.toShort)

  protected def readByteAt(address: Short) =
    memory.read(address)

  protected def readWordAt(address: Int): Short =
    readWordAt(address.toShort)

  protected def readWordAt(address: Short) =
    readBytesAt(address, 0x02).foldRight[Short](0x0000)((byte, result) => ((result << 8) | byte).toShort)

  protected def readBytesAt(address: Int, count: Int): Seq[ByteVal] =
    readBytesAt(address.toShort, count)

  protected def readBytesAt(address: Short, count: Int) =
    (0 until count).map(index => readByteAt(address + index))

  protected def readByteAt(name: String): ByteVal =
    readByteAt(label2address(name))

  protected def readWordAt(name: String): Short =
    readWordAt(label2address(name))

  protected def readBytesAt(name: String, count: Int): Seq[ByteVal] =
    readBytesAt(label2address(name), count)

  protected def readBytesAt(names: String*): Seq[ByteVal] =
    names.map(readByteAt(_))
}
