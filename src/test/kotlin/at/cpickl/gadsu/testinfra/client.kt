package at.cpickl.gadsu.testinfra

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.image.defaultImage
import at.cpickl.gadsu.service.parseDate
import at.cpickl.gadsu.tcm.model.XProps
import org.joda.time.DateTime


@Suppress("UNUSED")
fun Client.Companion.unsavedValidInstance() = Client.INSERT_PROTOTYPE.copy(
        created = TEST_DATETIME1,
        firstName = "testFirstName",
        lastName = "testLastName",
        gender = Gender.MALE,
        birthday = TEST_DATE_1985,
        job = "lazy bastard",
        picture = MyImage.DEFAULT_PROFILE_MAN,
        countryOfOrigin = "\u00d6sterreich"
)

@Suppress("UNUSED")
fun Client.Companion.savedValidInstance() = unsavedValidInstance().copy(id = TEST_UUID1)

@Suppress("UNUSED")
fun Client.Companion.savedValidInstance2() = unsavedValidInstance().copy(id = TEST_UUID2)

fun Client.copyWithoutCprops() = this.copy(cprops = CProps.empty)
