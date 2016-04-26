package at.cpickl.gadsu.client.props

import kotlin.reflect.KClass


val allStrings: List<String> = Props.Strings.values().map { it.key }
val allMultiEnums: Map<String, KClass<Enum<*>>> = mapOf(
        Pair(Props.Enums.SleepEnum.key, Props.Enums.SleepEnum::class as KClass<Enum<*>>)
        // mooooore ....
)

interface HasKey {
    val key: String
}


object Props {
    enum class Strings(val key: String) {
        MoodOfToday("MoodOfToday")
    }
    object Enums {
        enum class SleepEnum(override val key: String) : HasKey {
            ProblemsFallAsleep("ProblemsFallAsleepKeeey"),
            ProblemsWakeUp("ProblemsWakeUp"),
            TiredInTheMorning("TiredInTheMorning"),
            TiredInTheEvening("TiredInTheEvening");
            companion object : HasKey {
                override val key = "Sleep"
            }
        }

    }
}
