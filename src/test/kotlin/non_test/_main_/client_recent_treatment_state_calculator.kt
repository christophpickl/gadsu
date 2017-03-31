package non_test._main_

import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.view.RecentStateCalculator

fun main(args: Array<String>) {
    println(String.format("days | %10s | %10s | %10s", "A", "B", "C"))
    println("===========================================")
    for (days in 0.rangeTo(100)) {
        val stateA = RecentStateCalculator.calc(days, ClientCategory.A, null)
        val stateB = RecentStateCalculator.calc(days, ClientCategory.B, null)
        val stateC = RecentStateCalculator.calc(days, ClientCategory.C, null)
        println(String.format("%4d | %10s | %10s | %10s", days, stateA.name, stateB.name, stateC.name))
    }
}

/*
days |          A |          B |          C
===========================================
   0 |         Ok |         Ok |         Ok
   1 |         Ok |         Ok |         Ok
   2 |         Ok |         Ok |         Ok
   3 |         Ok |         Ok |         Ok
   4 |         Ok |         Ok |         Ok
   5 |         Ok |         Ok |         Ok
   6 |         Ok |         Ok |         Ok
   7 |         Ok |         Ok |         Ok
   8 |  Attention |         Ok |         Ok
   9 |  Attention |         Ok |         Ok
  10 |  Attention |         Ok |         Ok
  11 |  Attention |         Ok |         Ok
  12 |  Attention |         Ok |         Ok
  13 |  Attention |         Ok |         Ok
  14 |  Attention |  Attention |         Ok
  15 |  Attention |  Attention |         Ok
  16 |       Warn |  Attention |         Ok
  17 |       Warn |  Attention |         Ok
  18 |       Warn |  Attention |         Ok
  19 |       Warn |  Attention |  Attention
  20 |       Warn |  Attention |  Attention
  21 |       Warn |  Attention |  Attention
  22 |       Warn |  Attention |  Attention
  23 |       Warn |  Attention |  Attention
  24 |       Warn |  Attention |  Attention
  25 |   Critical |  Attention |  Attention
  26 |   Critical |  Attention |  Attention
  27 |   Critical |  Attention |  Attention
  28 |   Critical |       Warn |  Attention
  29 |   Critical |       Warn |  Attention
  30 |   Critical |       Warn |  Attention
  31 |   Critical |       Warn |  Attention
  32 |   Critical |       Warn |  Attention
  33 |   Critical |       Warn |  Attention
  34 |   Critical |       Warn |  Attention
  35 |   Critical |       Warn |  Attention
  36 |      Fatal |       Warn |  Attention
  37 |      Fatal |       Warn |  Attention
  38 |      Fatal |       Warn |  Attention
  39 |      Fatal |       Warn |       Warn
  40 |      Fatal |       Warn |       Warn
  41 |      Fatal |       Warn |       Warn
  42 |      Fatal |   Critical |       Warn
  43 |      Fatal |   Critical |       Warn
  44 |      Fatal |   Critical |       Warn
  45 |      Fatal |   Critical |       Warn
  46 |      Fatal |   Critical |       Warn
  47 |      Fatal |   Critical |       Warn
  48 |      Fatal |   Critical |       Warn
  49 |      Fatal |   Critical |       Warn
  50 |      Fatal |   Critical |       Warn
  51 |      Fatal |   Critical |       Warn
  52 |      Fatal |   Critical |       Warn
  53 |      Fatal |   Critical |       Warn
  54 |      Fatal |   Critical |       Warn
  55 |      Fatal |   Critical |       Warn
  56 |      Fatal |   Critical |       Warn
  57 |      Fatal |   Critical |       Warn
  58 |      Fatal |   Critical |   Critical
  59 |      Fatal |   Critical |   Critical
  60 |      Fatal |      Fatal |   Critical
  61 |      Fatal |      Fatal |   Critical
  62 |      Fatal |      Fatal |   Critical
  63 |      Fatal |      Fatal |   Critical
  64 |      Fatal |      Fatal |   Critical
  65 |      Fatal |      Fatal |   Critical
  66 |      Fatal |      Fatal |   Critical
  67 |      Fatal |      Fatal |   Critical
  68 |      Fatal |      Fatal |   Critical
  69 |      Fatal |      Fatal |   Critical
  70 |      Fatal |      Fatal |   Critical
  71 |      Fatal |      Fatal |   Critical
  72 |      Fatal |      Fatal |   Critical
  73 |      Fatal |      Fatal |   Critical
  74 |      Fatal |      Fatal |   Critical
  75 |      Fatal |      Fatal |   Critical
  76 |      Fatal |      Fatal |   Critical
  77 |      Fatal |      Fatal |   Critical
  78 |      Fatal |      Fatal |   Critical
  79 |      Fatal |      Fatal |   Critical
  80 |      Fatal |      Fatal |   Critical
  81 |      Fatal |      Fatal |   Critical
  82 |      Fatal |      Fatal |   Critical
  83 |      Fatal |      Fatal |   Critical
  84 |      Fatal |      Fatal |      Fatal
  85 |      Fatal |      Fatal |      Fatal
  86 |      Fatal |      Fatal |      Fatal
  87 |      Fatal |      Fatal |      Fatal
  88 |      Fatal |      Fatal |      Fatal
  89 |      Fatal |      Fatal |      Fatal
  90 |      Fatal |      Fatal |      Fatal
  91 |      Fatal |      Fatal |      Fatal
  92 |      Fatal |      Fatal |      Fatal
  93 |      Fatal |      Fatal |      Fatal
  94 |      Fatal |      Fatal |      Fatal
  95 |      Fatal |      Fatal |      Fatal
  96 |      Fatal |      Fatal |      Fatal
  97 |      Fatal |      Fatal |      Fatal
  98 |      Fatal |      Fatal |      Fatal
  99 |      Fatal |      Fatal |      Fatal
 100 |      Fatal |      Fatal |      Fatal
 */
