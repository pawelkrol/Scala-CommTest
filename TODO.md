TODO
====

* Allow to invoke "callOriginal" method within mock setup via "setCustomHandler".
* Currently you cannot "call" a subroutine inside of a "before" block.
  - "hasSubroutineMock" doesn't work when called within "before" block:
    [info]   java.lang.NullPointerException:
    [info]   at com.github.pawelkrol.CommTest.FunSpec.hasSubroutineMock(FunSpec.scala:106)
