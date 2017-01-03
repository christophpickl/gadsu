package at.cpickl.gadsu.acupuncture

/**
 * Right now only contains the "important" ones. Would need to mark some of them as "important", but define EACH AND EVERY one of them here!
 */
/*
object Acupuncts {


    val allPuncts = lazy {
        byMeridian.values.flatMap { it }
    }

    fun byMeridian(meridian: Meridian) = byMeridian[meridian]!!
}


private class AcupunctsPunctBuilder() {

    // needs to be linked to keep ordering
    private val map = LinkedHashMap<Meridian, LinkedList<Acupunct>>()
    private lateinit var currentMeridian: Meridian

    fun meridian(meridian: Meridian, func: AcupunctsPunctBuilder.() -> Unit): AcupunctsPunctBuilder {
        currentMeridian = meridian
        with(this, func)
        return this
    }

    fun punct(
            number: Int,
            germanName: String,
            chineseName: String,
            localisation: String,
            indications: String = "",
            note: String = "",
            flags: List<AcupunctFlag> = emptyList()
    ): AcupunctsPunctBuilder {
        if (!map.containsKey(currentMeridian)) {
            map.put(currentMeridian, LinkedList())
        }
        map[currentMeridian]!!.add(Acupunct.build(currentMeridian, number, germanName, chineseName, note, localisation, indications, flags))
        return this
    }

    fun build() = map
}
*/
