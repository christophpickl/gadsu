package at.cpickl.gadsu.client

import at.cpickl.gadsu.image.Images
import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_UUID1
import at.cpickl.gadsu.testinfra.TEST_UUID2


@Suppress("UNUSED")
fun Client.Companion.unsavedValidInstance() = Client.INSERT_PROTOTYPE.copy(
        created = TEST_DATE,
        firstName = "testFirstName",
        lastName = "testLastName",
        gender = Gender.MALE,
        picture = Images.DEFAULT_PROFILE_MAN
)

@Suppress("UNUSED")
fun Client.Companion.savedValidInstance() = unsavedValidInstance().copy(id = TEST_UUID1)

@Suppress("UNUSED")
fun Client.Companion.savedValidInstance2() = unsavedValidInstance().copy(id = TEST_UUID2)
