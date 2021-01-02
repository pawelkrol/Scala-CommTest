package com.github.pawelkrol.CommTest

case class MemoisedMock(
  callback: () => Unit,
  nestedLevel: Int
)
