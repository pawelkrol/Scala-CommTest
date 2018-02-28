package com.github.pawelkrol.CommTest

import java.io.File

import scala.io.Codec.ISO8859
import scala.io.Source.fromFile

import MiscUtils._

class LabelLog(_labels: Map[String, Short] = Map()) {

  def apply(key: String) = _labels(key)

  def contains(key: String) = _labels.contains(key)

  def labels(address: Short) = _labels.filter(_._2 == address).map(_._1)
}

object LabelLog {

  private def split(log: List[Char]): List[List[Char]] =
    log match {
      case Nil => List(List())
      case x :: xs => {
        val result = split(xs)
        x.toInt match {
          case 0x0a => Nil +: result
          case _ => x +: result.head :: result.tail
        }
      }
    }

  private def extract(line: List[Char]) = {
    val (label, after, dollar, value) =
      line.foldLeft[Tuple4[String, Boolean, Boolean, String]](("", false, false, ""))((result, char) => {
        val (label, after, dollar, value) = result
        char.toInt match {
          case 0x3d => // "="
            (label, true, dollar, value)
          case _ => {
            after match {
              case false =>
                (label + char, after, dollar, value)
              case true =>
                char.toInt match {
                  case 0x24 => // "$"
                    (label, after, true, value)
                  case _ =>
                    dollar match {
                      case false =>
                        (label, after, dollar, value)
                      case true =>
                        if (isHexDigit(char))
                          (label, after, dollar, value + char)
                        else
                          (label, after, false, value)
                    }
                }
            }
          }
        }
      })

    if (label.nonEmpty && value.nonEmpty)
      Some(label.trim, hex2int(value).toShort)
    else
      None
  }

  private def parse(log: List[Char]) = split(log).filter(_.nonEmpty).map(extract(_)).filter(
    _ match {
      case Some(_) => true
      case None => false
    }
  ).map(_.get).toMap

  def apply() = new LabelLog()

  def apply(log: List[Char]) = new LabelLog(parse(log))

  def apply(file: File): LabelLog = LabelLog(fromFile(file)(ISO8859).toList)

  def apply(fileName: String): LabelLog = LabelLog(new File(fileName))
}
