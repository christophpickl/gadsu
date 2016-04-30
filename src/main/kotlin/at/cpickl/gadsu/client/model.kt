package at.cpickl.gadsu.client

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.Ordered
import at.cpickl.gadsu.SqlEnum
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.orderedValuesOf
import at.cpickl.gadsu.parseSqlCodeFor
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.Persistable
import at.cpickl.gadsu.view.components.Labeled
import com.google.common.base.MoreObjects
import com.google.common.base.Objects
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime


interface Prop {

}

data class StringProp(val value: String) : Prop
data class MultiEnumProp(val entries: List<String>) : Prop {
//    val activeEntries = entries.filterValues { it == true }.keys.toList()
}

data class ClientProps(val properties: Map<String, Prop>) {
    companion object {
        val empty: ClientProps get() = ClientProps(emptyMap())
    }
}

data class Client(
        override val id: String?, // it is null if not yet persisted
        val created: DateTime,
        val firstName: String,
        val lastName: String,
        val contact: Contact,
        val birthday: DateTime?,
        val gender: Gender,
        val countryOfOrigin: String,
        val relationship: Relationship,
        val job: String,
        val children: String,

        val note: String,
        val picture: MyImage,
        val props: ClientProps

) : Comparable<Client>, HasId, Persistable {

    companion object { // needed for static extension methods

        // created will be overridden anyway
        val INSERT_PROTOTYPE = Client(null, DUMMY_CREATED, "", "",
                Contact.INSERT_PROTOTYPE, null, Gender.UNKNOWN, "", Relationship.UNKNOWN, "", "", "",
                MyImage.DEFAULT_PROFILE_MAN, ClientProps.empty)
    }

    override val yetPersisted: Boolean
        get() = id != null

    val fullName: String
        get() {
            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                return firstName + " " + lastName
            }
            return firstName + lastName
        }

    val idComparator: (Client) -> Boolean
        get() = { that -> this.id.equals(that.id) }

    override fun compareTo(other: Client): Int {
        return ComparisonChain.start()
                .compare(this.lastName, other.lastName)
                .compare(this.firstName, other.firstName)
                .result()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Client) return false
        val that = other

        return  Objects.equal(this.id, that.id) &&
                Objects.equal(this.created, that.created) &&
                Objects.equal(this.firstName, that.firstName) &&
                Objects.equal(this.lastName, that.lastName) &&
                Objects.equal(this.contact, that.contact) &&
                Objects.equal(this.birthday, that.birthday) &&
                Objects.equal(this.gender, that.gender) &&
                Objects.equal(this.countryOfOrigin, that.countryOfOrigin) &&
                Objects.equal(this.relationship, that.relationship) &&
                Objects.equal(this.job, that.job) &&
                Objects.equal(this.children, that.children) &&
                Objects.equal(this.note, that.note) &&
                Objects.equal(this.picture.toSaveRepresentation(), that.picture.toSaveRepresentation()) &&
                Objects.equal(this.created, that.created)
    }

    override fun toString(): String {
        return MoreObjects.toStringHelper(javaClass)
                .add("id", id)
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("picture", picture)
                .add("props", props)
                .toString()
    }

}


data class Contact(
        val mail: String,
        val phone: String,
        val street: String,
        val zipCode: String,
        val city: String
) {
    companion object {
        val INSERT_PROTOTYPE = Contact("", "", "", "", "")
    }
}


enum class Gender(override val order: Int, override val sqlCode: String, override val label: String) :
        Ordered, SqlEnum, Labeled {
    MALE(1, "M", "M\u00e4nnlich"),
    FEMALE(2, "F", "Weiblich"),
    UNKNOWN(99, "?", "Unbekannt");

    companion object {
        fun orderedValues():List<Gender> = orderedValuesOf(Gender.values())
        fun parseSqlCode(search: String) = parseSqlCodeFor(Gender.values(), search)
    }
}

enum class Relationship(override val order: Int, override val sqlCode: String, override val label: String) :
        Ordered, SqlEnum, Labeled {
    SINGLE(1, "SINGLE", "ledig"),
    RELATION(2, "RELATION", "partnerschaft"),
    MARRIED(3, "MARRIED", "verheiratet"),
    DIVORCED(4, "DIVORCED", "geschieden"),
    WIDOW(5, "WIDOW", "verwitwet"),
    UNKNOWN(99, "UNKNOWN", "Unbekannt");


    companion object {
        fun orderedValues():List<Relationship> = orderedValuesOf(Relationship.values())
        fun parseSqlCode(search: String) = parseSqlCodeFor(Relationship.values(), search)
    }
}

