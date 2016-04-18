package at.cpickl.gadsu.client

import at.cpickl.gadsu.DUMMY_CREATED
import at.cpickl.gadsu.GadsuException
import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.service.HasId
import at.cpickl.gadsu.service.Persistable
import com.google.common.collect.ComparisonChain
import org.joda.time.DateTime


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
        val picture: MyImage

) : Comparable<Client>, HasId, Persistable {

    companion object { // needed for static extension methods

        // created will be overridden anyway
        val INSERT_PROTOTYPE = Client(null, DUMMY_CREATED, "", "",
                Contact.INSERT_PROTOTYPE, null, Gender.UNKNOWN, "", Relationship.UNKNOWN, "", "", "", Images.DEFAULT_PROFILE_MAN)
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

    override fun compareTo(other: Client): Int {
        return ComparisonChain.start()
                .compare(this.lastName, other.lastName)
                .compare(this.firstName, other.firstName)
                .result()
    }

    val idComparator: (Client) -> Boolean
        get() = { that -> this.id.equals(that.id) }

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


enum class Gender(override val sqlCode: String) : SqlEnum {
    MALE("M"),
    FEMALE("F"),
    UNKNOWN("?");

    companion object {
        fun parseSqlCode(search: String) = _parseSqlCode(Gender.values(), search)
    }
}

enum class Relationship(override val sqlCode: String, val label: String) : SqlEnum {
    SINGLE("SINGLE", "ledig"),
    RELATION("RELATION", "partnerschaft"),
    MARRIED("MARRIED", "verheiratet"),
    DIVORCED("DIVORCED", "geschieden"),
    WIDOW("WIDOW", "verwitwet"),
    UNKNOWN("UNKNOWN", "Unbekannt");


    companion object {
        fun parseSqlCode(search: String) = _parseSqlCode(Relationship.values(), search)
    }
}

interface SqlEnum {
    val sqlCode: String
}


private fun <E : SqlEnum> _parseSqlCode(values: Array<E>, search: String): E {
    return values.firstOrNull { it.sqlCode.equals(search) } ?:
            throw GadsuException("Unhandled SQL code: '${search}'!")
}
