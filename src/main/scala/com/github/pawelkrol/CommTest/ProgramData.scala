package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal

import java.io.File

import scala.io.Codec.ISO8859
import scala.io.Source.fromFile

import MiscUtils._

case class ProgramData(address: Short, program: List[ByteVal] = List())

object ProgramData {

  private def address(data: List[ByteVal]) = seq2addr(data.take(2))

  private def program(data: List[ByteVal]) = data.drop(2)

  def apply(address: Short) = new ProgramData(address)

  def apply(data: List[ByteVal]) = new ProgramData(address(data), program(data))

  def apply(file: File): ProgramData = ProgramData(fromFile(file)(ISO8859).toList.map(ByteVal(_)))

  def apply(fileName: String): ProgramData = ProgramData(new File(fileName))
}
