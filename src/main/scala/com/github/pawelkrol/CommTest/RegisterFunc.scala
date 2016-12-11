package com.github.pawelkrol.CommTest

import com.github.pawelkrol.CPU6502.ByteVal
import com.github.pawelkrol.CPU6502.Status.{ BF => _BF, CF => _CF, DF => _DF, Flag, IF => _IF, OF => _OF, SF => _SF, ZF => _ZF }

trait RegisterFunc extends CPU6502Spec {

  protected def initRegisters {
    AC = 0x00
    XR = 0x00
    YR = 0x00
    SR = 0x20
    SP = 0xff
    PC = 0x0000
  }

  private def getFlag(flag: Flag) = register.getStatusFlag(flag)

  private def setFlag(flag: Flag, value: Boolean) { register.setStatusFlag(flag, value) }

  def AC = register.AC

  def AC_=(value: ByteVal) { register.AC = value }

  def BF = getFlag(_BF)

  def BF_=(value: Boolean) { setFlag(_BF, value) }

  def CF = getFlag(_CF)

  def CF_=(value: Boolean) { setFlag(_CF, value) }

  def DF = getFlag(_DF)

  def DF_=(value: Boolean) { setFlag(_DF, value) }

  def IF = getFlag(_IF)

  def IF_=(value: Boolean) { setFlag(_IF, value) }

  def OF = getFlag(_OF)

  def OF_=(value: Boolean) { setFlag(_OF, value) }

  def PC = register.PC

  def PC_=(value: Short) { register.PC = value }

  def SF = getFlag(_SF)

  def SF_=(value: Boolean) { setFlag(_SF, value) }

  def SP = register.SP

  def SP_=(value: ByteVal) { register.SP = value }

  def SR = register.status

  def SR_=(value: ByteVal) { register.status = value }

  def XR = register.XR

  def XR_=(value: ByteVal) { register.XR = value }

  def YR = register.YR

  def YR_=(value: ByteVal) { register.YR = value }

  def ZF = getFlag(_ZF)

  def ZF_=(value: Boolean) { setFlag(_ZF, value) }
}
