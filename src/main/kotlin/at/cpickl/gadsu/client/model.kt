package at.cpickl.gadsu.client

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.EnumBase
import at.cpickl.gadsu.Labeled
import at.cpickl.gadsu.Ordered
import at.cpickl.gadsu.SqlEnum
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.firstNotEmpty
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.parseSqlCodeFor
import at.cpickl.gadsu.persistence.Persistable
import at.cpickl.gadsu.service.Current
import at.cpickl.gadsu.service.CurrentEvent
import at.cpickl.gadsu.service.HasId
import com.google.common.base.Objects
import com.google.common.collect.ComparisonChain
import com.google.common.eventbus.EventBus
import org.joda.time.DateTime
import javax.inject.Inject

class CurrentClient @Inject constructor(bus: EventBus) :
        Current<Client>(ID, bus, INITIAL_VALUE) { // MINOR @REFACTOR - use kotlin delegation (requires interface first): Client by data
    companion object {
        val ID: String = "client"
        val INITIAL_VALUE = Client.INSERT_PROTOTYPE
    }
}

fun CurrentEvent.forClient(function: (Client?) -> Unit) {
    if (this.id == CurrentClient.ID) function(this.newData as Client?)
}

interface IClient : HasId, Persistable {

    val created: DateTime
    val firstName: String
    val lastName: String
    val nickNameInt: String
    val nickNameExt: String
    val preferredName: String // either nickNameInt (if set) or firstName; used INTERNALLY only! (don't use for mails, etc)
    val fullName: String // "$firstName $lastName"
    val state: ClientState
    val contact: Contact
    val hasMail: Boolean // inferred
    val hasMailAndWantsMail: Boolean
    val wantReceiveMails: Boolean
    val birthday: DateTime?
    val gender: Gender
    /** birth location */
    val countryOfOrigin: String
    val origin: String
    val relationship: Relationship
    val job: String
    val children: String
    val hobbies: String
    val note: String

    val textImpression: String
    val textMedical: String
    val textComplaints: String
    val textPersonal: String
    val textObjective: String
    val textMainObjective: String
    val textSymptoms: String
    val textFiveElements: String
    val textSyndrom: String
    val category: ClientCategory
    val donation: ClientDonation

    val tcmNote: String
    val picture: MyImage
    val cprops: CProps
}

data class Client(
        override val id: String?, // it is null if not yet persisted
        override val created: DateTime,
        override val state: ClientState,

        override val firstName: String,
        override val lastName: String,
        override val nickNameInt: String,
        override val nickNameExt: String,
        override val contact: Contact,
        override val wantReceiveMails: Boolean,
        override val birthday: DateTime?,
        override val gender: Gender,
        /** Geburtsort */
        override val countryOfOrigin: String,
        /** Wohnort */
        override val origin: String,
        override val relationship: Relationship,
        override val job: String,
        override val children: String,
        override val hobbies: String,
        override val note: String,

        /** "Texte" tab, textarea: Allgemeiner Eindruck */
        override val textImpression: String,
        /** "Texte" tab, textarea: Medizinisches */
        override val textMedical: String,
        /** "Texte" tab, textarea: Beschwerden */
        override val textComplaints: String,
        /** "Texte" tab, textarea: Lebensprofil */
        override val textPersonal: String,
        /** "Texte" tab, textarea: Ziele */
        override val textObjective: String,

        /** "Allgemein" tab, textfield: Hauptanliegen */
        override val textMainObjective: String,
        /** "Allgemein" tab, textfield: symptome */
        override val textSymptoms: String,
        /** "Allgemein" tab, textfield: 5 Elemente */
        override val textFiveElements: String,
        /** "Allgemein" tab, textfield: Syndrom */
        override val textSyndrom: String,
        override val category: ClientCategory,
        override val donation: ClientDonation,

        override val tcmNote: String,
        override val picture: MyImage,
        override val cprops: CProps

) : IClient, Comparable<Client> {

    companion object { // needed for static extension methods

        // created date will be overridden anyway, so just set it to DUMMY_CREATED is ok :)
        val INSERT_PROTOTYPE = Client(
                id = null,
                created = DUMMY_CREATED,
                state = ClientState.ACTIVE,
                firstName = "",
                lastName = "",
                nickNameInt = "",
                nickNameExt = "",
                contact = Contact.EMPTY,
                wantReceiveMails = true,
                birthday = null,
                gender = Gender.UNKNOWN,
                countryOfOrigin = "",
                origin = "",
                relationship = Relationship.UNKNOWN,
                job = "",
                children = "",
                hobbies = "",
                note = "",
                textImpression = "",
                textMedical = "",
                textComplaints = "",
                textPersonal = "",
                textObjective = "",
                textMainObjective = "",
                textSymptoms = "",
                textFiveElements = "",
                textSyndrom = "",
                category = ClientCategory.B,
                donation = ClientDonation.UNKNOWN,
                tcmNote = "",
                picture = MyImage.DEFAULT_PROFILE_MAN,
                cprops = CProps.empty
        )
    }

    override val hasMail: Boolean = this.contact.mail.isNotEmpty()
    override val hasMailAndWantsMail: Boolean = hasMail && this.wantReceiveMails

    override val yetPersisted: Boolean get() = id != null

    override val fullName: String
        get() = if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            firstName + " " + lastName
        } else {
            firstName + lastName
        }

    override val preferredName = firstNotEmpty(nickNameInt, nickNameExt, firstName)

    val idComparator: (Client) -> Boolean
        get() = { that -> this.id.equals(that.id) }

    override fun compareTo(other: Client): Int {
        return ComparisonChain.start()
                .compare(this.preferredName, other.preferredName)
                .compare(this.lastName, other.lastName)
                .compare(this.created, other.created)
                .result()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Client) return false
        val that = other

        return Objects.equal(this.id, that.id) &&
                Objects.equal(this.created, that.created) &&
                Objects.equal(this.state, that.state) &&
                Objects.equal(this.firstName, that.firstName) &&
                Objects.equal(this.lastName, that.lastName) &&
                Objects.equal(this.nickNameInt, that.nickNameInt) &&
                Objects.equal(this.contact, that.contact) &&
                Objects.equal(this.birthday, that.birthday) &&
                Objects.equal(this.gender, that.gender) &&
                Objects.equal(this.countryOfOrigin, that.countryOfOrigin) &&
                Objects.equal(this.origin, that.origin) &&
                Objects.equal(this.relationship, that.relationship) &&
                Objects.equal(this.job, that.job) &&
                Objects.equal(this.children, that.children) &&
                Objects.equal(this.hobbies, that.hobbies) &&
                Objects.equal(this.note, that.note) &&
                Objects.equal(this.textImpression, that.textImpression) &&
                Objects.equal(this.textMedical, that.textMedical) &&
                Objects.equal(this.textComplaints, that.textComplaints) &&
                Objects.equal(this.textPersonal, that.textPersonal) &&
                Objects.equal(this.textObjective, that.textObjective) &&
                Objects.equal(this.textMainObjective, that.textMainObjective) &&
                Objects.equal(this.textSymptoms, that.textSymptoms) &&
                Objects.equal(this.textFiveElements, that.textFiveElements) &&
                Objects.equal(this.textSyndrom, that.textSyndrom) &&
                Objects.equal(this.category, that.category) &&
                Objects.equal(this.donation, that.donation) &&
                Objects.equal(this.tcmNote, that.tcmNote) &&
                Objects.equal(this.picture.toSaveRepresentation(), that.picture.toSaveRepresentation()) &&
                Objects.equal(this.cprops, that.cprops)
    }

    override fun hashCode() = Objects.hashCode(id, created, firstName, lastName)

//    override fun toString(): String {
//        return MoreObjects.toStringHelper(javaClass)
//                .add("id", id)
//                .add("firstName", firstName)
//                .add("nickNameInt", nickNameInt)
//                .add("lastName", lastName)
//                .toString()
//    }

}


data class Contact(
        val mail: String,
        val phone: String,
        val street: String,
        val zipCode: String,
        val city: String
) {
    companion object {
        val EMPTY = Contact("", "", "", "", "")
    }
}


enum class ClientState(override val sqlCode: String, override val label: String) :
        SqlEnum, Labeled {
    ACTIVE("ACTIVE", "Aktiv"),
    INACTIVE("INACTIVE", "Inaktiv");

    companion object {
        //        val orderedValues:List<ClientState> = orderedValuesOf(ClientState.values())
        fun parseSqlCode(search: String) = parseSqlCodeFor(ClientState.values(), search)
    }
}

enum class Gender(override val order: Int, override val sqlCode: String, override val label: String) :
        Ordered, SqlEnum, Labeled {
    MALE(1, "M", "M\u00e4nnlich"),
    FEMALE(2, "F", "Weiblich"),
    UNKNOWN(99, "?", "Unbekannt");

    object Enum : EnumBase<Gender>(Gender.values())
}

enum class Relationship(override val order: Int, override val sqlCode: String, override val label: String) :
        Ordered, SqlEnum, Labeled {
    SINGLE(1, "SINGLE", "single"),
    RELATION(2, "RELATION", "partnerschaft"),
    MARRIED(3, "MARRIED", "verheiratet"),
    DIVORCED(4, "DIVORCED", "geschieden"),
    WIDOW(5, "WIDOW", "verwitwet"),
    COMPLICATED(6, "COMPLICATED", "es ist kompliziert"),
    UNKNOWN(99, "UNKNOWN", "unbekannt");

    object Enum : EnumBase<Relationship>(Relationship.values())
}

enum class ClientCategory(override val order: Int, override val sqlCode: String, override val label: String) :
        Ordered, SqlEnum, Labeled {
    A(1, "A", "A"),
    B(2, "B", "B"),
    C(3, "C", "C");

    object Enum : EnumBase<ClientCategory>(ClientCategory.values())
}

enum class ClientDonation(
        override val order: Int,
        override val sqlCode: String,
        override val label: String
) : Ordered, SqlEnum, Labeled {
    UNKNOWN(1, "UNKNOWN", "Unbekannt"),
    NONE(2, "NONE", "Nichts"),
    PRESENT(3, "PRESENT", "Geschenk"),
    MONEY(4, "MONEY", "Geld");

    object Enum : EnumBase<ClientDonation>(ClientDonation.values())

}
