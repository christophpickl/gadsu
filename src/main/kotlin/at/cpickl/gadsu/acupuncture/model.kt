package at.cpickl.gadsu.acupuncture

import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.tcm.model.Element
import at.cpickl.gadsu.tcm.model.Meridian
import at.cpickl.gadsu.tcm.model.UnpairedMeridian
import com.google.common.base.Splitter
import com.google.common.collect.ComparisonChain
import gadsu.generated.Acupuncts
import java.util.HashMap
import java.util.LinkedList
import java.util.regex.Pattern


// synonym
@Suppress("unused")
fun AcupunctCoordinate.Companion.byLabel(label: String) = Acupunct.coordinateByLabel(label)



data class Acupunct(
        val coordinate: AcupunctCoordinate,
        val germanName: String,
        val chineseName: String,
        val note: String,
        val localisation: String,
        val indications: List<String>,
        /**
         * Always in the same ordered as its natural sort order.
         * Always only contains 0 or 1 ElementPoint.
         */
        val flags: List<AcupunctFlag>
) :
        Comparable<Acupunct> {

    companion object {
        private val acupunctByCoordinate = HashMap<AcupunctCoordinate, Acupunct>()
        private val coordinatesByLabel = HashMap<String, AcupunctCoordinate>()
        private val byMeridian = HashMap<Meridian, MutableList<Acupunct>>()
        private val all = LinkedList<Acupunct>()

        fun build(
                meridian: Meridian,
                number: Int,
                germanName: String,
                chineseName: String,
                note: String,
                localisation: String,
                joinedIndications: String,
                flags: List<AcupunctFlag>
        ): Acupunct {
            val indications = Splitter.on(",").trimResults().split(joinedIndications).toList()

            val coordinate = AcupunctCoordinate(meridian, number)
            coordinatesByLabel.put(coordinate.label, coordinate)

            return Acupunct(coordinate, germanName, chineseName, note, localisation, indications, flags).apply {
                acupunctByCoordinate.put(coordinate, this)
                if (!byMeridian.contains(meridian)) byMeridian.put(meridian, LinkedList<Acupunct>())
                byMeridian[meridian]!!.add(this)
                all.add(this)
            }
        }

        fun coordinateByLabel(label: String) = coordinatesByLabel[label]
        fun byCoordinate(coordinate: AcupunctCoordinate) = acupunctByCoordinate[coordinate]
        fun byLabel(label: String): Acupunct? {
            val coordinate = coordinateByLabel(label) ?: return null
            return acupunctByCoordinate[coordinate]
        }

        fun allForMeridian(meridian: Meridian): List<Acupunct> = byMeridian[meridian]!!
        fun all(): List<Acupunct> {
            Acupuncts.Lu1 // enforce eager loading
            return all
        }
    }

    init {
        verifyFlags()
    }

    val isMarinaportant: Boolean by lazy { flags.contains(AcupunctFlag.Marinaportant) }

    // delegation is not working properly due to mismatching Comparable<T> interfaces
    val meridian: Meridian = coordinate.meridian
    val number: Int = coordinate.number

    // move to and delegate by AcupunctCoordinate??
    val titleLong: String get() = "${meridian.labelLong} $number"
    val titleShort: String get() = "${meridian.labelShort}$number"

    val elementFlag: AcupunctFlag.ElementPoint? = flags.filterIsInstance(AcupunctFlag.ElementPoint::class.java).firstOrNull()

    override fun equals(other: Any?): Boolean {
        if (other !is Acupunct) {
            return false
        }
        return this.coordinate == other.coordinate
    }

    override fun hashCode() = this.coordinate.hashCode()

    override fun compareTo(other: Acupunct) = this.coordinate.compareTo(other.coordinate)

    override fun toString() = coordinate.label

    private fun verifyFlags() {
        if (flags.sorted() != flags) {
            throw GadsuException("$this: Flags must be in precise order! Was: ${flags.joinToString()}, but should be: ${flags.sorted().joinToString()}")
        }
        if (flags.filter { it is AcupunctFlag.ElementPoint }.size > 1) {
            throw GadsuException("$this: Flags must only contain 0 or 1 element point, but was: " + flags.joinToString(", "))
        }
    }
}

data class AcupunctCoordinate(
        val meridian: Meridian,
        val number: Int
) : Comparable<AcupunctCoordinate> {

    companion object {
        private val regexp = Pattern.compile("((Lu)|(Di)|(Ma)|(MP)|(He)|(Due)|(Bl)|(Ni)|(Pk)|(3E)|(Gb)|(Le))[1-9][0-9]?")
        fun isPotentialLabel(potent: String) = regexp.matcher(potent).matches()
    }

    /** E.g. "Lu1", "3E21, "Due14" */
    val label = meridian.labelShort + number

    override fun compareTo(other: AcupunctCoordinate): Int {
        return ComparisonChain.start()
                .compare(this.meridian, other.meridian)
                .compare(this.number, other.number)
                .result()
    }
}


interface AcupunctFlagCallback<T> {

    companion object {
        val LABELIZE: AcupunctFlagCallback<String> = AcupunctFlagStringCallback.INSTANCE
        val LABELIZE_SHORT: AcupunctFlagCallback<String> = AcupunctFlagStringShortCallback.INSTANCE
    }

    fun onMarinaportant(flag: AcupunctFlag.Marinaportant): T
    fun onOrientation(flag: AcupunctFlag.Orientation): T
    fun onBoPoint(flag: AcupunctFlag.BoPoint): T
    fun onElementPoint(flag: AcupunctFlag.ElementPoint): T
    fun onYuPoint(flag: AcupunctFlag.YuPoint): T
    fun onOriginalPoint(flag: AcupunctFlag.OriginalPoint): T
    fun onNexusPoint(flag: AcupunctFlag.NexusPoint): T
    fun onMasterPoint(flag: AcupunctFlag.MasterPoint): T
    fun onKeyPoint(flag: AcupunctFlag.KeyPoint): T
    fun onTonePoint(flag: AcupunctFlag.TonePoint): T
    fun onSedatePoint(flag: AcupunctFlag.SedatePoint): T
    fun onJingPoint(flag: AcupunctFlag.JingPoint): T
    fun onEntryPoint(flag: AcupunctFlag.EntryPoint): T
}

open class AcupunctFlagStringCallback private constructor() : AcupunctFlagCallback<String> {
    companion object {
        val INSTANCE = AcupunctFlagStringCallback()
    }

    override fun onMarinaportant(flag: AcupunctFlag.Marinaportant) = flag.label
    override fun onOrientation(flag: AcupunctFlag.Orientation) = flag.label
    override fun onBoPoint(flag: AcupunctFlag.BoPoint) = "${flag.label} ${flag.meridian.labelShort}"
    override fun onYuPoint(flag: AcupunctFlag.YuPoint) = "${flag.label} ${flag.meridian.labelShort}"
    override fun onElementPoint(flag: AcupunctFlag.ElementPoint) = flag.element.label + "punkt"
    override fun onOriginalPoint(flag: AcupunctFlag.OriginalPoint) = flag.label
    override fun onNexusPoint(flag: AcupunctFlag.NexusPoint) = flag.label
    override fun onMasterPoint(flag: AcupunctFlag.MasterPoint) = flag.label
    override fun onKeyPoint(flag: AcupunctFlag.KeyPoint) = "${flag.label} ${flag.meridianx.label}"
    override fun onTonePoint(flag: AcupunctFlag.TonePoint) = flag.label
    override fun onSedatePoint(flag: AcupunctFlag.SedatePoint) = flag.label
    override fun onJingPoint(flag: AcupunctFlag.JingPoint) = flag.label
    override fun onEntryPoint(flag: AcupunctFlag.EntryPoint) = "${flag.label} ${flag.meridian.labelShort}"
}

open class AcupunctFlagStringShortCallback private constructor() : AcupunctFlagCallback<String> {
    companion object {
        val INSTANCE = AcupunctFlagStringShortCallback()
    }

    override fun onMarinaportant(flag: AcupunctFlag.Marinaportant) = flag.labelShort
    override fun onOrientation(flag: AcupunctFlag.Orientation) = flag.labelShort
    override fun onBoPoint(flag: AcupunctFlag.BoPoint) = "${flag.labelShort} ${flag.meridian.labelShort}"
    override fun onYuPoint(flag: AcupunctFlag.YuPoint) = "${flag.labelShort} ${flag.meridian.labelShort}"
    override fun onElementPoint(flag: AcupunctFlag.ElementPoint) = flag.element.label
    override fun onOriginalPoint(flag: AcupunctFlag.OriginalPoint) = flag.labelShort
    override fun onNexusPoint(flag: AcupunctFlag.NexusPoint) = flag.labelShort
    override fun onMasterPoint(flag: AcupunctFlag.MasterPoint) = flag.labelShort
    override fun onKeyPoint(flag: AcupunctFlag.KeyPoint) = "${flag.labelShort} ${flag.meridianx.label}"
    override fun onTonePoint(flag: AcupunctFlag.TonePoint) = flag.labelShort
    override fun onSedatePoint(flag: AcupunctFlag.SedatePoint) = flag.labelShort
    override fun onJingPoint(flag: AcupunctFlag.JingPoint) = flag.labelShort
    override fun onEntryPoint(flag: AcupunctFlag.EntryPoint) = "${flag.labelShort} ${flag.meridian.labelShort}"
}

@Suppress("unused")
sealed class AcupunctFlag(
        val label: String,
        val labelShort: String,
        private val compareWeight: Int,
        val renderShortLabel: Boolean = true
) : Comparable<AcupunctFlag> {

    abstract fun <T> onFlagType(callback: AcupunctFlagCallback<T>): T

    open protected fun additionalCompareTo(other: AcupunctFlag): Int {
        return 0
    }

    // ! kontrindiziert bei schwangerschaft

    // ? erfahrungsstelle (marina) =??= verbindungspunkt (DTV) fuer zb Lu? oder nase
    // ? RIM?
    // notfallpunkt
    // akutpunkt, zb Ma34

//    https://en.renmai.at/index.php?m=meridiane&style=0&page=28
//    https://www.renmai.at/index.php?m=meridiane&style=0&page=28

    override fun compareTo(other: AcupunctFlag): Int {
        if (this.compareWeight == other.compareWeight) {
            return additionalCompareTo(other)
        }
        return this.compareWeight - other.compareWeight
    }

    /** Important for marina. */
    // =================================================================================================================
    object Marinaportant : AcupunctFlag("Wichtig", "!!!", 0, renderShortLabel = false) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onMarinaportant(this)
        override fun toString() = "Marinaportant"
    }

    /** Helpful for orientation. */
    // =================================================================================================================
    object Orientation : AcupunctFlag("Orientation", "Ort", 1) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onOrientation(this)
        override fun toString() = "Orientation"
    }

    /** Alarmpunkt, japan. "bo", chin. "mu" */
    // con ab = sammlungspunkt lt dtv atlas
    // =================================================================================================================
    class BoPoint private constructor(val meridian: Meridian) : AcupunctFlag("Bopunkt", "BO", 2) {
        companion object {
            val Lung = BoPoint(Meridian.Lung)
            val LargeIntestine = BoPoint(Meridian.LargeIntestine)
            val Stomach = BoPoint(Meridian.Stomach)
            val Spleen = BoPoint(Meridian.Spleen)
            val Heart = BoPoint(Meridian.Heart)
            val SmallIntestine = BoPoint(Meridian.SmallIntestine)
            val UrinaryBladder = BoPoint(Meridian.UrinaryBladder)
            val Kidney = BoPoint(Meridian.Kidney)
            val Pericardium = BoPoint(Meridian.Pericardium)
            val TripleBurner = BoPoint(Meridian.TripleBurner)
            val GallBladder = BoPoint(Meridian.GallBladder)
        }

        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onBoPoint(this)
        override fun toString() = "BoPoint[meridian=$meridian]"
    }

    /** Zustimmungspunkt japan. "yu", chin. "shu" */
    // =================================================================================================================
    class YuPoint private constructor(val meridian: Meridian) : AcupunctFlag("Yupunkt", "YU", 3) {
        companion object {
            val Lung = YuPoint(Meridian.Lung)
            val LargeIntestine = YuPoint(Meridian.LargeIntestine)
            val Stomach = YuPoint(Meridian.Stomach)
            val Spleen = YuPoint(Meridian.Spleen)
            val Heart = YuPoint(Meridian.Heart)
            val SmallIntestine = YuPoint(Meridian.SmallIntestine)
            val UrinaryBladder = YuPoint(Meridian.UrinaryBladder)
            val Kidney = YuPoint(Meridian.Kidney)
            val Pericardium = YuPoint(Meridian.Pericardium)
            val TripleBurner = YuPoint(Meridian.TripleBurner)
            val GallBladder = YuPoint(Meridian.GallBladder)
        }

        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onYuPoint(this)
        override fun toString() = "YuPoint[meridian=$meridian]"
    }

    /** Wandlungsphasen Zuordnung. */
    // =================================================================================================================
    class ElementPoint private constructor(val element: Element) : AcupunctFlag("Element", "5E", 4, renderShortLabel = false) {
        companion object {
            val Wood = ElementPoint(Element.Wood)
            val Fire = ElementPoint(Element.Fire)
            val Earth = ElementPoint(Element.Earth)
            val Metal = ElementPoint(Element.Metal)
            val Water = ElementPoint(Element.Water)
        }

        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onElementPoint(this)
        override fun toString() = "ElementPoint[element=$element]"
    }

    /** Quellpunkt / yuan / ORIG, ursprungsqi */
    // =================================================================================================================
    object OriginalPoint : AcupunctFlag("Quellpunkt", "ORIG", 5) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onOriginalPoint(this)
        override fun toString() = "OriginalPoint"
    }

    /** Durchgangspunkt / luo / NEX(orien) */
    // NEX=verknuepfungspunkt; zb mit KG!!!
    // =================================================================================================================
    object NexusPoint : AcupunctFlag("Durchgangspunkt", "NEX", 6) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onNexusPoint(this)
        override fun toString() = "NexusPoint"
    }

    /** Meisterpunkt fuer meridian (gefaesssystem) / ba hui xue */
    // =================================================================================================================
    object MasterPoint : AcupunctFlag("Meisterpunkt", "MAST", 7) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onMasterPoint(this)
        override fun toString() = "MasterPoint"
    }

    /** Schluesselpunkt (oeffnungspunkt) / ba mai jiao hui xue */
    // =================================================================================================================
    class KeyPoint private constructor(val meridianx: UnpairedMeridian) : AcupunctFlag("Schlüsselpunkt", "KEY", 8) {
        companion object {
            val ChongMai = KeyPoint(UnpairedMeridian.ChongMai)
        }

        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onKeyPoint(this)
        override fun toString() = "KeyPoint[meridianx=$meridianx]"
    }

    /** Tonisierungspunkt / bu punkt */
    // =================================================================================================================
    object TonePoint : AcupunctFlag("Tonisierungspunkt", "TON", 9) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onTonePoint(this)
        override fun toString() = "TonePoint"
    }

    /** Sedierungspunkt / xie punkt */
    // =================================================================================================================
    object SedatePoint : AcupunctFlag("Sedierungspunkt", "SED", 10) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onSedatePoint(this)
        override fun toString() = "SedatePoint"
    }

    /** brunnenpunkt / jing*/
    // =================================================================================================================
    object JingPoint : AcupunctFlag("Brunnenpunkt", "JING", 11) {
        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onJingPoint(this)
        override fun toString() = "JingPoint"
    }

    /** Eintrittspunkt */
    // =================================================================================================================
    class EntryPoint private constructor(val meridian: Meridian) : AcupunctFlag("Eintrittspunkt", "EIN", 12) {
        companion object {
            val Heart = EntryPoint(Meridian.Heart)
        }

        override fun <T> onFlagType(callback: AcupunctFlagCallback<T>) = callback.onEntryPoint(this)
        override fun toString() = "EntryPoint[meridian=$meridian]"
    }

}
