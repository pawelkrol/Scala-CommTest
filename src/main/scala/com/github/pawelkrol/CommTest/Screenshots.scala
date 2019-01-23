package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.{ ByteVal, CommodoreMemory, SimpleMemory }

import java.io.File

import org.c64.attitude.Afterimage.Colour.Palette
import org.c64.attitude.Afterimage.File.Type.PNG
import org.c64.attitude.Afterimage.Sprite.{ Data, HiResProps, MultiProps }
import org.c64.attitude.Afterimage.View.Image
import org.c64.attitude.Afterimage.Mode.{ CBM, HiRes, MultiColour }

trait Screenshots extends MemoryReader {

  def captureScreenshot(
    targetFile: Option[String] = None,
    bitmapMode: Option[Boolean] = None,
    multiColourMode: Option[Boolean] = None,
    screenAddress: Option[Int] = None,
    bitmapAddress: Option[Int] = None,
    colorsAddress: Option[Int] = None,
    backgroundColour: Option[Byte] = None,
    includeSprites: Boolean = true
  ) = captureCommodoreScreenshot(targetFile, bitmapMode, multiColourMode, screenAddress, bitmapAddress, colorsAddress, backgroundColour, includeSprites)

  private def captureCommodoreScreenshot(
    targetFile: Option[String],
    bitmapMode: Option[Boolean],
    multiColourMode: Option[Boolean],
    screenAddress: Option[Int],
    bitmapAddress: Option[Int],
    colorsAddress: Option[Int],
    backgroundColour: Option[Byte],
    includeSprites: Boolean
  ) = memory match {
    case commodoreMemory: CommodoreMemory => {
      val io = commodoreMemory.io
      bitmapMode.getOrElse(getBitmapMode(io(0x0011))) match {
        case false =>
          throw new RuntimeException("Capturing screenshot feature in a text mode is currently not supported")
        case true =>
          captureBitmapScreenshot(targetFile, multiColourMode, screenAddress, bitmapAddress, colorsAddress, backgroundColour, includeSprites, io)
      }
    }
    case _ =>
      throw new RuntimeException("Capturing screenshot feature is not supported for this type of memory object")
  }

  private def captureBitmapScreenshot(
    targetFile: Option[String],
    multiColourMode: Option[Boolean],
    screenAddress: Option[Int],
    bitmapAddress: Option[Int],
    colorsAddress: Option[Int],
    backgroundColour: Option[Byte],
    includeSprites: Boolean,
    io: Array[ByteVal]
  ) = {
    val fullScreen = screen(screenAddress, io)
    val fullBitmap = bitmap(bitmapAddress, io)
    val fullColors = colors(colorsAddress, io)
    val background = backgroundColour.getOrElse((io(0x0021) & 0x0f).value)
    multiColourMode.getOrElse(getMultiColourMode(io(0x0016))) match {
      case true =>
        captureMultiColourScreenshot(targetFile, screenAddress, fullScreen, fullBitmap, fullColors, background, includeSprites, io)
      case false =>
        captureHiResScreenshot(targetFile, screenAddress, fullScreen, fullBitmap, includeSprites, io)
    }
  }

  private def captureHiResScreenshot(
    targetFile: Option[String],
    screenAddress: Option[Int],
    screen: Array[Byte],
    bitmap: Array[Byte],
    includeSprites: Boolean,
    io: Array[ByteVal]
  ) = savePicture(targetFile, HiRes(bitmap, screen), screenAddress, includeSprites, io)

  private def captureMultiColourScreenshot(
    targetFile: Option[String],
    screenAddress: Option[Int],
    screen: Array[Byte],
    bitmap: Array[Byte],
    colors: Array[Byte],
    background: Byte,
    includeSprites: Boolean,
    io: Array[ByteVal]
  ) = savePicture(targetFile, MultiColour(bitmap, screen, colors, background), screenAddress, includeSprites, io)

  private def bitmap(
    bitmapAddress: Option[Int],
    io: Array[ByteVal]
  ) = dataBytesAt(bitmapAddress.getOrElse(memoryBank(io(0x0d00)) + getBitmapAddress(io(0x0018))), 0x1f40)

  private def screen(
    screenAddress: Option[Int],
    io: Array[ByteVal]
  ) = dataBytesAt(screenAddress.getOrElse(fullScreenAddress(io)), 0x03e8)

  private def colors(
    colorsAddress: Option[Int],
    io: Array[ByteVal]
  ) = dataBytesAt(colorsAddress.getOrElse(0xd800), 0x03e8)

  private def savePicture(
    targetFile: Option[String],
    picture: CBM,
    screenAddress: Option[Int],
    includeSprites: Boolean,
    io: Array[ByteVal]
  ) = {
    val image = addSprites(Image(picture, Palette("default")), screenAddress, includeSprites, io)
    targetFile match {
      case Some(fileName) =>
        PNG.writeImage(fileName, image)
      case None =>
    }
    image
  }

  private def dataBytesAt(address: Int, count: Int): Array[Byte] = readBytesAt(address, count).map(_.value).toArray

  private def getBitmapMode(d011: ByteVal) = (d011 & 0x30)() == 0x30

  private def getMultiColourMode(d016: ByteVal) = (d016 & 0x10)() == 0x10

  private def getBitmapAddress(d018: ByteVal) = ((d018 & 0x0f)() >> 3) << 13

  private def getScreenAddress(d018: ByteVal) = ((d018 & 0xf0)() >> 4) << 10

  private def fullScreenAddress(io: Array[ByteVal]): Int = memoryBank(io(0x0d00)) + getScreenAddress(io(0x0018))

  private def memoryBank(dd00: ByteVal) = ((dd00 & 0x03)() ^ 0x03) << 14

  private def addSprites(
    image: Image,
    screenAddress: Option[Int],
    includeSprites: Boolean,
    io: Array[ByteVal]
  ) = {
    val scaleFactor = 1

    val sprites = collectSprites(screenAddress, io)

    if (includeSprites && sprites.nonEmpty) {
      val (spriteN, xN, yN) = sprites.last
      val imageWithSpriteN = spriteN.render(xN, yN, _ => image, scaleFactor, 0x18, 0x32)

      sprites.init.foldRight(imageWithSpriteN)({ case ((sprite, x, y), finalImage) =>
        sprite.renderNext(x, y, finalImage, scaleFactor, 0x18, 0x32)
      })((_, _, _, _, _) => {})
    }
    else
      image.create(scaleFactor, scaleOf)
  }

  private def collectSprites(
    screenAddress: Option[Int],
    io: Array[ByteVal]
  ) = {
    val multiColour0 = io(0x0025)()
    val multiColour1 = io(0x0026)()

    (0 to 7).map(n => {
      val mask = bitMask(n)
      (
        spriteVisible(io, mask),
        spriteX(io, n, mask),
        spriteY(io, n),
        spriteMultiColour(io, mask),
        spriteColour(io, n),
        spriteExpandX(io, mask),
        spriteExpandY(io, mask),
        spriteHasPriority(io, mask),
        spriteData(screenAddress, io, n)
      )
    }).flatMap({ case (visible, x, y, multiMode, colour, expandX, expandY, hasPriority, data) =>
      if (visible) {
        val props =
          if (multiMode)
            MultiProps(colour, multiColour0, multiColour1, expandX, expandY, hasPriority)
          else
            HiResProps(colour, expandX, expandY, hasPriority)
        Seq((Data(data.toSeq, props), x, y))
      }
      else {
        Seq()
      }
    })
  }

  private def spriteVisible(io: Array[ByteVal], mask: Int) =
    spriteFlag(io, 0x0015, mask)

  private def spriteX(io: Array[ByteVal], n: Int, mask: Int) =
    (spriteValue(io, 0x0000, n * 2)) + (if (spriteFlag(io, 0x0010, mask)) 0x0100 else 0x0000)

  private def spriteY(io: Array[ByteVal], n: Int) =
    spriteValue(io, 0x0001, n * 2)

  private def spriteMultiColour(io: Array[ByteVal], mask: Int) =
    spriteFlag(io, 0x001c, mask)

  private def spriteColour(io: Array[ByteVal], n: Int) =
    spriteValue(io, 0x0027, n)

  private def spriteExpandX(io: Array[ByteVal], mask: Int) =
    spriteFlag(io, 0x001d, mask)

  private def spriteExpandY(io: Array[ByteVal], mask: Int) =
    spriteFlag(io, 0x0017, mask)

  private def spriteHasPriority(io: Array[ByteVal], mask: Int) =
    !spriteFlag(io, 0x001b, mask)

  private def spriteData(
    screenAddress: Option[Int],
    io: Array[ByteVal],
    n: Int
  ) = screenAddress match {
    case Some(address) =>
      dataBytesAt((address & 0xc000) + readByteAt(address + 0x03f8 + n)() * 0x40, 0x3f)
    case None =>
      dataBytesAt(memoryBank(io(0x0d00)) + readByteAt(fullScreenAddress(io) + 0x03f8 + n)() * 0x40, 0x3f)
  }

  private def spriteValue(io: Array[ByteVal], offset: Int, n: Int) = io(offset + n)()

  private def spriteFlag(io: Array[ByteVal], offset: Int, mask: Int) = (io(offset)() & mask) == mask

  private def bitMask(n: Int) = 0x80 >> (7 - n)

  private def scaleOf(pic: CBM) = pic match {
    case multiColour: MultiColour => (2, 1)
    case hiRes: HiRes => (1, 1)
    case _ => throw new RuntimeException("Something went wrong...")
  }
}
