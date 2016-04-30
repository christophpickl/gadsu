package at.cpickl.gadsu.client.xprops

// TODO rename to something shorter



object XPropsRegistry {

    val Sleep = EnumClientXProp("Sleep")


//    enum class Strings(val key: String) {
//        MoodOfToday("MoodOfToday")
//    }
//
//    object Enums {
//        enum class SleepEnum(override val key: String) : HasKey {
//            ProblemsFallAsleep("ProblemsFallAsleepKeeey"),
//            ProblemsWakeUp("ProblemsWakeUp"),
//            TiredInTheMorning("TiredInTheMorning"),
//            TiredInTheEvening("TiredInTheEvening");
//
//            companion object : HasKey {
//                override val key = "Sleep"
//            }
//        }
//
//    }
}

class EnumClientXProp(override val key: String) : ClientXProp

interface ClientXProp {
    val key: String
}

data class ClientXProps(val properties: Map<String, ClientXProp>) {
    companion object {
        val empty: ClientXProps get() = ClientXProps(emptyMap())
    }
}
