package at.cpickl.gadsu.client

import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.global.DUMMY_CREATED
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.tcm.model.XProps
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

val Contact.Companion.testInstance1 get() = Contact("1", "1", "1", "1", "1")
val Contact.Companion.testInstance2 get() = Contact("2", "2", "2", "2", "2")

val CProps.Companion.testInstance1 get() = CProps.builder().build()
val CProps.Companion.testInstance2 get() = CProps.builder().add(XProps.Hungry, XProps.HungryOpts.BigHunger).build()

val Client.Companion.testInstance1
    get() = Client(
            "1", DUMMY_CREATED, ClientState.ACTIVE, "1", "1", "1", "1", Contact.testInstance1, "1",
            true, true, DUMMY_CREATED, Gender.UNKNOWN, "1", "1", Relationship.UNKNOWN, "1", "1", "1", "1",
            "1", "1", "1", "1", "1", "1", "1", "1",
            YinYangMaybe.UNKNOWN, "yy1", ElementMaybe.UNKNOWN, "5e1", "w1", "f1", "e1", "m1", "w1",
            ClientCategory.A, ClientDonation.PRESENT, "1", MyImage.DEFAULT_PROFILE_ALIEN, CProps.testInstance1
    )

val Client.Companion.testInstance2
    get() = Client(
            "2", DUMMY_CREATED, ClientState.INACTIVE, "2", "2", "2", "2", Contact.testInstance2, "2",
            false, false, DUMMY_CREATED, Gender.MALE, "2", "2", Relationship.COMPLICATED, "2", "2", "2", "2",
            "2", "2", "2", "2", "2", "2", "2", "2",
            YinYangMaybe.YIN, "yy2", ElementMaybe.WOOD, "5e2", "w2", "f2", "e2", "m2", "w2",
            ClientCategory.B, ClientDonation.MONEY, "2", MyImage.DEFAULT_PROFILE_MAN, CProps.testInstance2
    )


@Test
class ClientTest {

    private val contact1 = Contact.testInstance1
    private val client1 = Client.testInstance1

    @DataProvider
    fun changingClientProvider(): Array<Array<Any>> = arrayOf(
            arrayOf<Any>("id", { it: Client -> it.copy(id = "2") }),
            arrayOf<Any>("state", { it: Client -> it.copy(state = ClientState.INACTIVE) }),
            arrayOf<Any>("firstName", { it: Client -> it.copy(firstName = "2") }),
            arrayOf<Any>("lastName", { it: Client -> it.copy(lastName = "2") }),
            arrayOf<Any>("nickNameExt", { it: Client -> it.copy(nickNameExt = "2") }),
            arrayOf<Any>("nickNameInt", { it: Client -> it.copy(nickNameInt = "2") }),
            arrayOf<Any>("contact", { it: Client -> it.copy(contact = contact1.copy(mail = "2")) }),
            arrayOf<Any>("knownBy", { it: Client -> it.copy(knownBy = "2") }),
            arrayOf<Any>("dsgvoAccepted", { it: Client -> it.copy(dsgvoAccepted = false) }),
            arrayOf<Any>("wantReceiveMails", { it: Client -> it.copy(wantReceiveMails = false) }),
            arrayOf<Any>("birthday", { it: Client -> it.copy(birthday = null) }),
            arrayOf<Any>("gender", { it: Client -> it.copy(gender = Gender.MALE) }),
            arrayOf<Any>("countryOfOrigin", { it: Client -> it.copy(countryOfOrigin = "2") }),
            arrayOf<Any>("origin", { it: Client -> it.copy(origin = "2") }),
            arrayOf<Any>("relationship", { it: Client -> it.copy(relationship = Relationship.DIVORCED) }),
            arrayOf<Any>("job", { it: Client -> it.copy(job = "2") }),
            arrayOf<Any>("children", { it: Client -> it.copy(children = "2") }),
            arrayOf<Any>("hobbies", { it: Client -> it.copy(hobbies = "2") }),
            arrayOf<Any>("note", { it: Client -> it.copy(note = "2") }),

            arrayOf<Any>("yyTendency", { it: Client -> it.copy(yyTendency = YinYangMaybe.YANG) }),
            arrayOf<Any>("textYinYang", { it: Client -> it.copy(textYinYang = "2") }),
            arrayOf<Any>("elementTendency", { it: Client -> it.copy(elementTendency = ElementMaybe.EARTH) }),
            arrayOf<Any>("textFiveElements", { it: Client -> it.copy(textFiveElements = "2") }),
            arrayOf<Any>("textWood", { it: Client -> it.copy(textWood = "2") }),
            arrayOf<Any>("textFire", { it: Client -> it.copy(textFire = "2") }),
            arrayOf<Any>("textEarth", { it: Client -> it.copy(textEarth = "2") }),
            arrayOf<Any>("textMetal", { it: Client -> it.copy(textMetal = "2") }),
            arrayOf<Any>("textWater", { it: Client -> it.copy(textWater = "2") }),

            arrayOf<Any>("textImpression", { it: Client -> it.copy(textImpression = "2") }),
            arrayOf<Any>("textMedical", { it: Client -> it.copy(textMedical = "2") }),
            arrayOf<Any>("textComplaints", { it: Client -> it.copy(textComplaints = "2") }),
            arrayOf<Any>("textPersonal", { it: Client -> it.copy(textPersonal = "2") }),
            arrayOf<Any>("textObjective", { it: Client -> it.copy(textObjective = "2") }),
            arrayOf<Any>("category", { it: Client -> it.copy(category = ClientCategory.C) }),
            arrayOf<Any>("donation", { it: Client -> it.copy(donation = ClientDonation.NONE) }),
            arrayOf<Any>("tcmNote", { it: Client -> it.copy(tcmNote = "2") }),
            arrayOf<Any>("cprops", { it: Client -> it.copy(cprops = CProps.builder().add(XProps.Hungry, XProps.HungryOpts.BigHunger).build()) })
    )

    @DataProvider
    fun changingClientProviderIgnored(): Array<Array<Any>> = arrayOf(
            arrayOf<Any>("picture", { it: Client -> it.copy(picture = MyImage.DEFAULT_PROFILE_MAN) })
    )

    @Test(dataProvider = "changingClientProvider")
    fun `inequal should be inequal`(field: String, changeClientProperty: (Client) -> Client) {
        assertThat("Changing field '$field' should have led to inequality!", client1, not(equalTo(changeClientProperty(client1))))
    }

    @Test(dataProvider = "changingClientProviderIgnored")
    fun `things to be ignored in equals`(field: String, changeClientProperty: (Client) -> Client) {
        assertThat("Changing field '$field' should NOT have led to inequality!", client1, equalTo(changeClientProperty(client1)))
    }

}
