package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.{ ByteVal, CommodoreMemory, SimpleMemory }

import java.io.File

import org.c64.attitude.Afterimage.Colour.Palette
import org.c64.attitude.Afterimage.File.Type.PNG
import org.c64.attitude.Afterimage.View.Image
import org.c64.attitude.Afterimage.Mode.{ CBM, HiRes, MultiColour }

trait Screenshots extends MemoryReader {

  def captureScreenshot(targetFile: String) {
    memory match {
      case _: SimpleMemory =>
        throw new RuntimeException("Capturing screenshot feature is not supported for this type of memory object")
      case commodoreMemory: CommodoreMemory =>
        captureScreenshot(targetFile, commodoreMemory.io)
    }
  }

  private def captureScreenshot(targetFile: String, io: Array[ByteVal]) {
    bitmapMode(io(0x0011)) match {
      case false =>
        throw new RuntimeException("Capturing screenshot feature in a text mode is currently not supported")
      case true =>
        captureBitmapScreenshot(targetFile, io)
    }
  }

  private def captureBitmapScreenshot(targetFile: String, io: Array[ByteVal]) {
    multiColourMode(io(0x0016)) match {
      case true =>
        captureMultiColourScreenshot(targetFile, io)
      case false =>
        captureHiResScreenshot(targetFile, io)
    }
  }

  private def captureHiResScreenshot(targetFile: String, io: Array[ByteVal]) {
    savePicture(targetFile, HiRes(bitmap(io), screen(io)))
  }

  private def captureMultiColourScreenshot(targetFile: String, io: Array[ByteVal]) {
    savePicture(targetFile, MultiColour(bitmap(io), screen(io), colors, (io(0x0021) & 0x0f).value))
  }

  private def bitmap(io: Array[ByteVal]) = dataBytesAt(memoryBank(io(0x0d00)) + bitmapAddress(io(0x0018)), 0x1f40)

  private def screen(io: Array[ByteVal]) = dataBytesAt(memoryBank(io(0x0d00)) + screenAddress(io(0x0018)), 0x03e8)

  private def colors = dataBytesAt(0xd800, 0x03e8)

  private def savePicture(targetFile: String, picture: CBM) {
    PNG(Image(picture, Palette("default"))).save(targetFile)
  }

  private def dataBytesAt(address: Int, count: Int): Array[Byte] = readBytesAt(address, count).map(_.value).toArray

  private def bitmapMode(d011: ByteVal) = (d011 & 0x30)() == 0x30

  private def multiColourMode(d016: ByteVal) = (d016 & 0x10)() == 0x10

  private def bitmapAddress(d018: ByteVal) = ((d018 & 0x0f)() >> 3) << 13

  private def screenAddress(d018: ByteVal) = ((d018 & 0xf0)() >> 4) << 10

  private def memoryBank(dd00: ByteVal) = ((dd00 & 0x03)() ^ 0x03) << 14
}
