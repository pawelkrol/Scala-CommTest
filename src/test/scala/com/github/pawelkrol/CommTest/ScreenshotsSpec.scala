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
    testViewImageColours(new ImagePlus(fileName), tests)
  }

  private def testViewImageColours(img: ImagePlus, tests: List[Tuple3[Int, Int, Int]]) {
    val imp = img.getProcessor
    tests.foreach({ case (x, y, expectedColour) =>
      assert((imp.getPixel(x, y) & 0x00ffffff) == palette(expectedColour).pixel)
    })
  }

  describe("hires_pic") {
    it("captures a screenshot in a hires mode of a currently displayed bitmap") {
      val targetFile = tempName(".png")
      call
      captureScreenshot(targetFile = Some(targetFile))
      testPngPixelColours(targetFile, List((8, 0, 0x09), (9, 0, 0x08), (313, 199, 0x04), (314, 199, 0x00)))
      delete(getPath(targetFile))
    }

    it("captures a screenshot in a hires mode to memory without saving a file") {
      call
      val screenshot = captureScreenshot()
      testViewImageColours(screenshot, List((8, 0, 0x09), (9, 0, 0x08), (313, 199, 0x04), (314, 199, 0x00)))
    }
  }

  describe("multi_pic") {
    it("captures a screenshot in a multicolour mode of a currently displayed bitmap") {
      val targetFile = tempName(".png")
      call
      captureScreenshot(targetFile = Some(targetFile))
      testPngPixelColours(targetFile, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04)))
      delete(getPath(targetFile))
    }

    it("captures a screenshot in a multicolour mode to memory without saving a file") {
      call
      val screenshot = captureScreenshot()
      testViewImageColours(screenshot, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04)))
    }
  }

  describe("sprites") {
    it("captures a screenshot in a multicolour mode including all visible sprites") {
      call
      val screenshot = captureScreenshot()
      testViewImageColours(screenshot, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04))) // image
      testViewImageColours(screenshot, List((3, 10, 0x02), (4, 10, 0x00), (5, 10, 0x01), (94, 10, 0x00))) // sprites
    }
  }

  describe("no_pic") {
    it("captures a default screenshot in a text mode") {
      call
      val exception = intercept[RuntimeException] { captureScreenshot() }
      assert(exception.getMessage == "Capturing screenshot feature in a text mode is currently not supported")
    }

    it("captures an arbitrary screenshot in a hires mode") {
      call
      val screenshot = captureScreenshot(
        bitmapMode = Some(true),
        multiColourMode = Some(false),
        screenAddress = Some(0x0c00),
        bitmapAddress = Some(0x2000)
      )
      testViewImageColours(screenshot, List((8, 0, 0x09), (9, 0, 0x08), (313, 199, 0x04), (314, 199, 0x00)))
    }

    it("captures an arbitrary screenshot in a multicolour mode") {
      call
      val screenshot = captureScreenshot(
        bitmapMode = Some(true),
        multiColourMode = Some(true),
        screenAddress = Some(0x6000),
        bitmapAddress = Some(0x4000),
        colorsAddress = Some(0x6400),
        backgroundColour = Some(0x00)
      )
      testViewImageColours(screenshot, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04)))
    }

    it("includes all visible sprites in a captured screenshot") {
      call
      val screenshot = captureScreenshot(
        bitmapMode = Some(true),
        multiColourMode = Some(true),
        screenAddress = Some(0x6000),
        bitmapAddress = Some(0x4000),
        colorsAddress = Some(0x6400),
        backgroundColour = Some(0x00)
      )
      testViewImageColours(screenshot, List((9, 0, 0x00), (10, 0, 0x06), (319, 198, 0x0a), (319, 199, 0x04))) // image
      testViewImageColours(screenshot, List((3, 10, 0x02), (4, 10, 0x00), (5, 10, 0x01), (94, 10, 0x00))) // sprites
      // screenshot.show()
    }
  }
}
