package non_test._main_

import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.view.ExtendedClient
import at.cpickl.gadsu.client.view.ThresholdCalculator
import at.cpickl.gadsu.preferences.PreferencesData
import at.cpickl.gadsu.preferences.Prefs
import at.cpickl.gadsu.preferences.ThresholdPrefData
import at.cpickl.gadsu.testinfra.savedValidInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever

fun main(args: Array<String>) {
    val prefs = mock<Prefs>()
    val threshold = ThresholdPrefData.DEFAULT
    println(threshold)
    whenever(prefs.preferencesData).thenReturn(PreferencesData.DEFAULT.copy(
            threshold = threshold
    ))

    val clientPrototype = ExtendedClient(
            client = at.cpickl.gadsu.client.Client.savedValidInstance().copy(
                    category = ClientCategory.A,
                    donation = ClientDonation.MONEY
            ),
            countTreatments = 42,
            upcomingAppointment = null,
            differenceDaysToRecentTreatment = null)

    val calcer = ThresholdCalculator(prefs)

    fun ClientDonation.shortLabel() = this.sqlCode.substring(0, 3)
    val donationsReordered = listOf(ClientDonation.NONE, ClientDonation.UNKNOWN, ClientDonation.PRESENT, ClientDonation.MONEY)
    donationsReordered.forEach { donation ->
        val dLabel = donation.shortLabel()
        print(String.format("days | %10s | %10s | %10s /// ", "$dLabel A", "$dLabel B", "$dLabel C"))
    }
    println()
    ClientDonation.values().forEach { print("=========================================== /// ") }
    println()

    for (days in 0.rangeTo(100)) {
        donationsReordered.forEach { donation ->
            val stateA = calcer.calc(clientPrototype.copyThreshold(days, donation, ClientCategory.A))
            val stateB = calcer.calc(clientPrototype.copyThreshold(days, donation, ClientCategory.B))
            val stateC = calcer.calc(clientPrototype.copyThreshold(days, donation, ClientCategory.C))
            print(String.format("%4d | %10s | %10s | %10s /// ", days, stateA.name, stateB.name, stateC.name))
        }
        println()
    }
}

private fun ExtendedClient.copyThreshold(days: Int, donation: ClientDonation, category: ClientCategory) =
        this.copy(differenceDaysToRecentTreatment = days, client =
        client.copy(category = category, donation = donation))

/*
ThresholdPrefData(
    daysAttention = 14,
    daysWarn      = 30,
    daysFatal     = 60
)

days |      NON A |      NON B |      NON C /// days |      UNK A |      UNK B |      UNK C /// days |      PRE A |      PRE B |      PRE C /// days |      MON A |      MON B |      MON C ///
=========================================== /// =========================================== /// =========================================== /// =========================================== ///
   0 |         Ok |         Ok |         Ok ///    0 |         Ok |         Ok |         Ok ///    0 |         Ok |         Ok |         Ok ///    0 |         Ok |         Ok |         Ok ///
   1 |         Ok |         Ok |         Ok ///    1 |         Ok |         Ok |         Ok ///    1 |         Ok |         Ok |         Ok ///    1 |         Ok |         Ok |         Ok ///
   2 |         Ok |         Ok |         Ok ///    2 |         Ok |         Ok |         Ok ///    2 |         Ok |         Ok |         Ok ///    2 |         Ok |         Ok |         Ok ///
   3 |         Ok |         Ok |         Ok ///    3 |         Ok |         Ok |         Ok ///    3 |         Ok |         Ok |         Ok ///    3 |         Ok |         Ok |         Ok ///
   4 |         Ok |         Ok |         Ok ///    4 |         Ok |         Ok |         Ok ///    4 |         Ok |         Ok |         Ok ///    4 |         Ok |         Ok |         Ok ///
   5 |         Ok |         Ok |         Ok ///    5 |         Ok |         Ok |         Ok ///    5 |         Ok |         Ok |         Ok ///    5 |         Ok |         Ok |         Ok ///
   6 |         Ok |         Ok |         Ok ///    6 |         Ok |         Ok |         Ok ///    6 |         Ok |         Ok |         Ok ///    6 |  Attention |         Ok |         Ok ///
   7 |         Ok |         Ok |         Ok ///    7 |         Ok |         Ok |         Ok ///    7 |         Ok |         Ok |         Ok ///    7 |  Attention |         Ok |         Ok ///
   8 |         Ok |         Ok |         Ok ///    8 |         Ok |         Ok |         Ok ///    8 |  Attention |         Ok |         Ok ///    8 |  Attention |         Ok |         Ok ///
   9 |         Ok |         Ok |         Ok ///    9 |  Attention |         Ok |         Ok ///    9 |  Attention |         Ok |         Ok ///    9 |  Attention |  Attention |         Ok ///
  10 |         Ok |         Ok |         Ok ///   10 |  Attention |         Ok |         Ok ///   10 |  Attention |         Ok |         Ok ///   10 |  Attention |  Attention |         Ok ///
  11 |         Ok |         Ok |         Ok ///   11 |  Attention |         Ok |         Ok ///   11 |  Attention |         Ok |         Ok ///   11 |  Attention |  Attention |         Ok ///
  12 |         Ok |         Ok |         Ok ///   12 |  Attention |         Ok |         Ok ///   12 |  Attention |  Attention |         Ok ///   12 |  Attention |  Attention |         Ok ///
  13 |         Ok |         Ok |         Ok ///   13 |  Attention |         Ok |         Ok ///   13 |  Attention |  Attention |         Ok ///   13 |  Attention |  Attention |         Ok ///
  14 |         Ok |         Ok |         Ok ///   14 |  Attention |  Attention |         Ok ///   14 |  Attention |  Attention |         Ok ///   14 |       Warn |  Attention |  Attention ///
  15 |  Attention |         Ok |         Ok ///   15 |  Attention |  Attention |         Ok ///   15 |  Attention |  Attention |         Ok ///   15 |       Warn |  Attention |  Attention ///
  16 |  Attention |         Ok |         Ok ///   16 |  Attention |  Attention |         Ok ///   16 |  Attention |  Attention |         Ok ///   16 |       Warn |  Attention |  Attention ///
  17 |  Attention |         Ok |         Ok ///   17 |  Attention |  Attention |         Ok ///   17 |  Attention |  Attention |         Ok ///   17 |       Warn |  Attention |  Attention ///
  18 |  Attention |         Ok |         Ok ///   18 |  Attention |  Attention |         Ok ///   18 |       Warn |  Attention |  Attention ///   18 |       Warn |  Attention |  Attention ///
  19 |  Attention |         Ok |         Ok ///   19 |  Attention |  Attention |         Ok ///   19 |       Warn |  Attention |  Attention ///   19 |       Warn |  Attention |  Attention ///
  20 |  Attention |         Ok |         Ok ///   20 |  Attention |  Attention |         Ok ///   20 |       Warn |  Attention |  Attention ///   20 |       Warn |  Attention |  Attention ///
  21 |  Attention |         Ok |         Ok ///   21 |       Warn |  Attention |  Attention ///   21 |       Warn |  Attention |  Attention ///   21 |       Warn |       Warn |  Attention ///
  22 |  Attention |  Attention |         Ok ///   22 |       Warn |  Attention |  Attention ///   22 |       Warn |  Attention |  Attention ///   22 |       Warn |       Warn |  Attention ///
  23 |  Attention |  Attention |         Ok ///   23 |       Warn |  Attention |  Attention ///   23 |       Warn |  Attention |  Attention ///   23 |       Warn |       Warn |  Attention ///
  24 |  Attention |  Attention |         Ok ///   24 |       Warn |  Attention |  Attention ///   24 |       Warn |  Attention |  Attention ///   24 |       Warn |       Warn |  Attention ///
  25 |  Attention |  Attention |         Ok ///   25 |       Warn |  Attention |  Attention ///   25 |       Warn |  Attention |  Attention ///   25 |       Warn |       Warn |  Attention ///
  26 |  Attention |  Attention |         Ok ///   26 |       Warn |  Attention |  Attention ///   26 |       Warn |  Attention |  Attention ///   26 |       Warn |       Warn |  Attention ///
  27 |  Attention |  Attention |         Ok ///   27 |       Warn |  Attention |  Attention ///   27 |       Warn |       Warn |  Attention ///   27 |       Warn |       Warn |  Attention ///
  28 |  Attention |  Attention |         Ok ///   28 |       Warn |  Attention |  Attention ///   28 |       Warn |       Warn |  Attention ///   28 |       Warn |       Warn |  Attention ///
  29 |  Attention |  Attention |         Ok ///   29 |       Warn |  Attention |  Attention ///   29 |       Warn |       Warn |  Attention ///   29 |      Fatal |       Warn |  Attention ///
  30 |  Attention |  Attention |         Ok ///   30 |       Warn |       Warn |  Attention ///   30 |       Warn |       Warn |  Attention ///   30 |      Fatal |       Warn |  Attention ///
  31 |  Attention |  Attention |         Ok ///   31 |       Warn |       Warn |  Attention ///   31 |       Warn |       Warn |  Attention ///   31 |      Fatal |       Warn |       Warn ///
  32 |  Attention |  Attention |         Ok ///   32 |       Warn |       Warn |  Attention ///   32 |       Warn |       Warn |  Attention ///   32 |      Fatal |       Warn |       Warn ///
  33 |       Warn |  Attention |  Attention ///   33 |       Warn |       Warn |  Attention ///   33 |       Warn |       Warn |  Attention ///   33 |      Fatal |       Warn |       Warn ///
  34 |       Warn |  Attention |  Attention ///   34 |       Warn |       Warn |  Attention ///   34 |       Warn |       Warn |  Attention ///   34 |      Fatal |       Warn |       Warn ///
  35 |       Warn |  Attention |  Attention ///   35 |       Warn |       Warn |  Attention ///   35 |       Warn |       Warn |  Attention ///   35 |      Fatal |       Warn |       Warn ///
  36 |       Warn |  Attention |  Attention ///   36 |       Warn |       Warn |  Attention ///   36 |       Warn |       Warn |  Attention ///   36 |      Fatal |       Warn |       Warn ///
  37 |       Warn |  Attention |  Attention ///   37 |       Warn |       Warn |  Attention ///   37 |      Fatal |       Warn |  Attention ///   37 |      Fatal |       Warn |       Warn ///
  38 |       Warn |  Attention |  Attention ///   38 |       Warn |       Warn |  Attention ///   38 |      Fatal |       Warn |  Attention ///   38 |      Fatal |       Warn |       Warn ///
  39 |       Warn |  Attention |  Attention ///   39 |       Warn |       Warn |  Attention ///   39 |      Fatal |       Warn |  Attention ///   39 |      Fatal |       Warn |       Warn ///
  40 |       Warn |  Attention |  Attention ///   40 |       Warn |       Warn |  Attention ///   40 |      Fatal |       Warn |       Warn ///   40 |      Fatal |       Warn |       Warn ///
  41 |       Warn |  Attention |  Attention ///   41 |       Warn |       Warn |  Attention ///   41 |      Fatal |       Warn |       Warn ///   41 |      Fatal |       Warn |       Warn ///
  42 |       Warn |  Attention |  Attention ///   42 |      Fatal |       Warn |  Attention ///   42 |      Fatal |       Warn |       Warn ///   42 |      Fatal |      Fatal |       Warn ///
  43 |       Warn |  Attention |  Attention ///   43 |      Fatal |       Warn |  Attention ///   43 |      Fatal |       Warn |       Warn ///   43 |      Fatal |      Fatal |       Warn ///
  44 |       Warn |  Attention |  Attention ///   44 |      Fatal |       Warn |  Attention ///   44 |      Fatal |       Warn |       Warn ///   44 |      Fatal |      Fatal |       Warn ///
  45 |       Warn |  Attention |  Attention ///   45 |      Fatal |       Warn |       Warn ///   45 |      Fatal |       Warn |       Warn ///   45 |      Fatal |      Fatal |       Warn ///
  46 |       Warn |  Attention |  Attention ///   46 |      Fatal |       Warn |       Warn ///   46 |      Fatal |       Warn |       Warn ///   46 |      Fatal |      Fatal |       Warn ///
  47 |       Warn |  Attention |  Attention ///   47 |      Fatal |       Warn |       Warn ///   47 |      Fatal |       Warn |       Warn ///   47 |      Fatal |      Fatal |       Warn ///
  48 |       Warn |       Warn |  Attention ///   48 |      Fatal |       Warn |       Warn ///   48 |      Fatal |       Warn |       Warn ///   48 |      Fatal |      Fatal |       Warn ///
  49 |       Warn |       Warn |  Attention ///   49 |      Fatal |       Warn |       Warn ///   49 |      Fatal |       Warn |       Warn ///   49 |      Fatal |      Fatal |       Warn ///
  50 |       Warn |       Warn |  Attention ///   50 |      Fatal |       Warn |       Warn ///   50 |      Fatal |       Warn |       Warn ///   50 |      Fatal |      Fatal |       Warn ///
  51 |       Warn |       Warn |  Attention ///   51 |      Fatal |       Warn |       Warn ///   51 |      Fatal |       Warn |       Warn ///   51 |      Fatal |      Fatal |       Warn ///
  52 |       Warn |       Warn |  Attention ///   52 |      Fatal |       Warn |       Warn ///   52 |      Fatal |       Warn |       Warn ///   52 |      Fatal |      Fatal |       Warn ///
  53 |       Warn |       Warn |  Attention ///   53 |      Fatal |       Warn |       Warn ///   53 |      Fatal |       Warn |       Warn ///   53 |      Fatal |      Fatal |       Warn ///
  54 |       Warn |       Warn |  Attention ///   54 |      Fatal |       Warn |       Warn ///   54 |      Fatal |      Fatal |       Warn ///   54 |      Fatal |      Fatal |       Warn ///
  55 |       Warn |       Warn |  Attention ///   55 |      Fatal |       Warn |       Warn ///   55 |      Fatal |      Fatal |       Warn ///   55 |      Fatal |      Fatal |       Warn ///
  56 |       Warn |       Warn |  Attention ///   56 |      Fatal |       Warn |       Warn ///   56 |      Fatal |      Fatal |       Warn ///   56 |      Fatal |      Fatal |       Warn ///
  57 |       Warn |       Warn |  Attention ///   57 |      Fatal |       Warn |       Warn ///   57 |      Fatal |      Fatal |       Warn ///   57 |      Fatal |      Fatal |       Warn ///
  58 |       Warn |       Warn |  Attention ///   58 |      Fatal |       Warn |       Warn ///   58 |      Fatal |      Fatal |       Warn ///   58 |      Fatal |      Fatal |       Warn ///
  59 |       Warn |       Warn |  Attention ///   59 |      Fatal |       Warn |       Warn ///   59 |      Fatal |      Fatal |       Warn ///   59 |      Fatal |      Fatal |       Warn ///
  60 |       Warn |       Warn |  Attention ///   60 |      Fatal |      Fatal |       Warn ///   60 |      Fatal |      Fatal |       Warn ///   60 |      Fatal |      Fatal |       Warn ///
  61 |       Warn |       Warn |  Attention ///   61 |      Fatal |      Fatal |       Warn ///   61 |      Fatal |      Fatal |       Warn ///   61 |      Fatal |      Fatal |       Warn ///
  62 |       Warn |       Warn |  Attention ///   62 |      Fatal |      Fatal |       Warn ///   62 |      Fatal |      Fatal |       Warn ///   62 |      Fatal |      Fatal |      Fatal ///
  63 |       Warn |       Warn |  Attention ///   63 |      Fatal |      Fatal |       Warn ///   63 |      Fatal |      Fatal |       Warn ///   63 |      Fatal |      Fatal |      Fatal ///
  64 |       Warn |       Warn |  Attention ///   64 |      Fatal |      Fatal |       Warn ///   64 |      Fatal |      Fatal |       Warn ///   64 |      Fatal |      Fatal |      Fatal ///
  65 |       Warn |       Warn |  Attention ///   65 |      Fatal |      Fatal |       Warn ///   65 |      Fatal |      Fatal |       Warn ///   65 |      Fatal |      Fatal |      Fatal ///
  66 |       Warn |       Warn |  Attention ///   66 |      Fatal |      Fatal |       Warn ///   66 |      Fatal |      Fatal |       Warn ///   66 |      Fatal |      Fatal |      Fatal ///
  67 |      Fatal |       Warn |  Attention ///   67 |      Fatal |      Fatal |       Warn ///   67 |      Fatal |      Fatal |       Warn ///   67 |      Fatal |      Fatal |      Fatal ///
  68 |      Fatal |       Warn |  Attention ///   68 |      Fatal |      Fatal |       Warn ///   68 |      Fatal |      Fatal |       Warn ///   68 |      Fatal |      Fatal |      Fatal ///
  69 |      Fatal |       Warn |  Attention ///   69 |      Fatal |      Fatal |       Warn ///   69 |      Fatal |      Fatal |       Warn ///   69 |      Fatal |      Fatal |      Fatal ///
  70 |      Fatal |       Warn |  Attention ///   70 |      Fatal |      Fatal |       Warn ///   70 |      Fatal |      Fatal |       Warn ///   70 |      Fatal |      Fatal |      Fatal ///
  71 |      Fatal |       Warn |  Attention ///   71 |      Fatal |      Fatal |       Warn ///   71 |      Fatal |      Fatal |       Warn ///   71 |      Fatal |      Fatal |      Fatal ///
  72 |      Fatal |       Warn |       Warn ///   72 |      Fatal |      Fatal |       Warn ///   72 |      Fatal |      Fatal |       Warn ///   72 |      Fatal |      Fatal |      Fatal ///
  73 |      Fatal |       Warn |       Warn ///   73 |      Fatal |      Fatal |       Warn ///   73 |      Fatal |      Fatal |       Warn ///   73 |      Fatal |      Fatal |      Fatal ///
  74 |      Fatal |       Warn |       Warn ///   74 |      Fatal |      Fatal |       Warn ///   74 |      Fatal |      Fatal |       Warn ///   74 |      Fatal |      Fatal |      Fatal ///
  75 |      Fatal |       Warn |       Warn ///   75 |      Fatal |      Fatal |       Warn ///   75 |      Fatal |      Fatal |       Warn ///   75 |      Fatal |      Fatal |      Fatal ///
  76 |      Fatal |       Warn |       Warn ///   76 |      Fatal |      Fatal |       Warn ///   76 |      Fatal |      Fatal |       Warn ///   76 |      Fatal |      Fatal |      Fatal ///
  77 |      Fatal |       Warn |       Warn ///   77 |      Fatal |      Fatal |       Warn ///   77 |      Fatal |      Fatal |       Warn ///   77 |      Fatal |      Fatal |      Fatal ///
  78 |      Fatal |       Warn |       Warn ///   78 |      Fatal |      Fatal |       Warn ///   78 |      Fatal |      Fatal |       Warn ///   78 |      Fatal |      Fatal |      Fatal ///
  79 |      Fatal |       Warn |       Warn ///   79 |      Fatal |      Fatal |       Warn ///   79 |      Fatal |      Fatal |       Warn ///   79 |      Fatal |      Fatal |      Fatal ///
  80 |      Fatal |       Warn |       Warn ///   80 |      Fatal |      Fatal |       Warn ///   80 |      Fatal |      Fatal |       Warn ///   80 |      Fatal |      Fatal |      Fatal ///
  81 |      Fatal |       Warn |       Warn ///   81 |      Fatal |      Fatal |       Warn ///   81 |      Fatal |      Fatal |      Fatal ///   81 |      Fatal |      Fatal |      Fatal ///
  82 |      Fatal |       Warn |       Warn ///   82 |      Fatal |      Fatal |       Warn ///   82 |      Fatal |      Fatal |      Fatal ///   82 |      Fatal |      Fatal |      Fatal ///
  83 |      Fatal |       Warn |       Warn ///   83 |      Fatal |      Fatal |       Warn ///   83 |      Fatal |      Fatal |      Fatal ///   83 |      Fatal |      Fatal |      Fatal ///
  84 |      Fatal |       Warn |       Warn ///   84 |      Fatal |      Fatal |       Warn ///   84 |      Fatal |      Fatal |      Fatal ///   84 |      Fatal |      Fatal |      Fatal ///
  85 |      Fatal |       Warn |       Warn ///   85 |      Fatal |      Fatal |       Warn ///   85 |      Fatal |      Fatal |      Fatal ///   85 |      Fatal |      Fatal |      Fatal ///
  86 |      Fatal |       Warn |       Warn ///   86 |      Fatal |      Fatal |       Warn ///   86 |      Fatal |      Fatal |      Fatal ///   86 |      Fatal |      Fatal |      Fatal ///
  87 |      Fatal |       Warn |       Warn ///   87 |      Fatal |      Fatal |       Warn ///   87 |      Fatal |      Fatal |      Fatal ///   87 |      Fatal |      Fatal |      Fatal ///
  88 |      Fatal |       Warn |       Warn ///   88 |      Fatal |      Fatal |       Warn ///   88 |      Fatal |      Fatal |      Fatal ///   88 |      Fatal |      Fatal |      Fatal ///
  89 |      Fatal |       Warn |       Warn ///   89 |      Fatal |      Fatal |       Warn ///   89 |      Fatal |      Fatal |      Fatal ///   89 |      Fatal |      Fatal |      Fatal ///
  90 |      Fatal |       Warn |       Warn ///   90 |      Fatal |      Fatal |      Fatal ///   90 |      Fatal |      Fatal |      Fatal ///   90 |      Fatal |      Fatal |      Fatal ///
  91 |      Fatal |       Warn |       Warn ///   91 |      Fatal |      Fatal |      Fatal ///   91 |      Fatal |      Fatal |      Fatal ///   91 |      Fatal |      Fatal |      Fatal ///
  92 |      Fatal |       Warn |       Warn ///   92 |      Fatal |      Fatal |      Fatal ///   92 |      Fatal |      Fatal |      Fatal ///   92 |      Fatal |      Fatal |      Fatal ///
  93 |      Fatal |       Warn |       Warn ///   93 |      Fatal |      Fatal |      Fatal ///   93 |      Fatal |      Fatal |      Fatal ///   93 |      Fatal |      Fatal |      Fatal ///
  94 |      Fatal |       Warn |       Warn ///   94 |      Fatal |      Fatal |      Fatal ///   94 |      Fatal |      Fatal |      Fatal ///   94 |      Fatal |      Fatal |      Fatal ///
  95 |      Fatal |       Warn |       Warn ///   95 |      Fatal |      Fatal |      Fatal ///   95 |      Fatal |      Fatal |      Fatal ///   95 |      Fatal |      Fatal |      Fatal ///
  96 |      Fatal |      Fatal |       Warn ///   96 |      Fatal |      Fatal |      Fatal ///   96 |      Fatal |      Fatal |      Fatal ///   96 |      Fatal |      Fatal |      Fatal ///
  97 |      Fatal |      Fatal |       Warn ///   97 |      Fatal |      Fatal |      Fatal ///   97 |      Fatal |      Fatal |      Fatal ///   97 |      Fatal |      Fatal |      Fatal ///
  98 |      Fatal |      Fatal |       Warn ///   98 |      Fatal |      Fatal |      Fatal ///   98 |      Fatal |      Fatal |      Fatal ///   98 |      Fatal |      Fatal |      Fatal ///
  99 |      Fatal |      Fatal |       Warn ///   99 |      Fatal |      Fatal |      Fatal ///   99 |      Fatal |      Fatal |      Fatal ///   99 |      Fatal |      Fatal |      Fatal ///
 100 |      Fatal |      Fatal |       Warn ///  100 |      Fatal |      Fatal |      Fatal ///  100 |      Fatal |      Fatal |      Fatal ///  100 |      Fatal |      Fatal |      Fatal ///

 */
