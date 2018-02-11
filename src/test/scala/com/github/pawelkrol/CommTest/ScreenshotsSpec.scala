package com.github.pawelkrol.CommTest

import ij.ImagePlus

import java.io.File
import java.nio.file.Files.delete
import java.nio.file.Paths.{ get => getPath }

import org.c64.attitude.Afterimage.Colour.Palette

import scala.util.Random

import TestUtils._

class ScreenshotsSpec extends FunSpec {

  outputPrg = testResourcePath("/snap.prg")

  labelLog = testResourcePath("/snap.log")

  private val tempDir = new File(System.getProperty("java.io.tmpdir"))

  private def tempName(extension: String): String = {
    val file = new File(tempDir.getCanonicalPath + "/" + Random.alphanumeric.take(16).mkString("") + extension)
    if (file.exists)
      tempName(extension)
    else {
      file.deleteOnExit
      file.getCanonicalPath
    }
  }

  private val palette = Palette("default")

  private def testPngPixelColours(fileName: String, tests: List[Tuple3[Int, Int, Int]]) {
    val img = new ImagePlus(fileName)
    val imp = img.getProcessor
    tests.foreach({ case (x, y, expectedColour) =>
      assert((imp.getPixel(x, y) & 0x00ffffff) == palette(expectedColour).pixel)
    })
  }

  describe("hires_pic") {
    it("captures a screenshot in a hires mode of a currently displayed bitmap") {
      val targetFile = tempName(".png")
      call
      captureScreenshot(targetFile)
      testPngPixelColours(targetFile, List((8, 0, 0x09), (9, 0, 0x08), (313, 199, 0x04), (314, 199, 0x00)))
      delete(getPath(targetFile))
    }
  }

  describe("multi_pic") {
    it("captures a screenshot in a multicolour mode of a currently displayed bitmap") {
      val targetFile = tempName(".png")
      call
      captureScreenshot(targetFile)
      testPngPixelColours(targetFile, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04)))
      delete(getPath(targetFile))
    }
  }
}
