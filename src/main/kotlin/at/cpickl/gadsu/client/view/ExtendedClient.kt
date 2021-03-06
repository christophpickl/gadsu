package at.cpickl.gadsu.client.view

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.ClientCategory
import at.cpickl.gadsu.client.ClientDonation
import at.cpickl.gadsu.client.ClientState
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.ElementMaybe
import at.cpickl.gadsu.client.Gender
import at.cpickl.gadsu.client.IClient
import at.cpickl.gadsu.client.Relationship
import at.cpickl.gadsu.client.YinYangMaybe
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.image.MyImage
import com.google.common.base.MoreObjects
import org.joda.time.DateTime

data class ExtendedClient(
        var client: Client,
        var countTreatments: Int,
        var upcomingAppointment: DateTime?,
        var differenceDaysToRecentTreatment: Int?
) : IClient, Comparable<ExtendedClient> {

    companion object {}

    override fun compareTo(other: ExtendedClient) =
            this.client.compareTo(other.client)

    // by client delegation does not work for mutable var fields :-/
    override val id: String? get() = client.id
    override val yetPersisted: Boolean get() = client.yetPersisted
    override val created: DateTime get() = client.created
    override val firstName: String get() = client.firstName
    override val nickNameExt: String get() = client.nickNameExt
    override val nickNameInt: String get() = client.nickNameInt
    override val lastName: String get() = client.lastName
    override val preferredName: String get() = client.preferredName
    override val fullName: String get() = client.fullName
    override val state: ClientState get() = client.state
    override val contact: Contact get() = client.contact
    override val knownBy: String get() = client.knownBy
    override val dsgvoAccepted: Boolean get() = client.dsgvoAccepted
    override val hasMail: Boolean get() = client.hasMail
    override val hasMailAndWantsMail: Boolean get() = client.hasMailAndWantsMail
    override val wantReceiveMails = client.wantReceiveMails
    override val birthday: DateTime? get() = client.birthday
    override val gender: Gender get() = client.gender
    override val countryOfOrigin: String get() = client.countryOfOrigin
    override val origin: String get() = client.origin
    override val relationship: Relationship get() = client.relationship
    override val job: String get() = client.job
    override val children: String get() = client.children
    override val hobbies: String get() = client.hobbies
    override val note: String get() = client.note

    override val yyTendency: YinYangMaybe get() = client.yyTendency
    override val textYinYang: String get() = client.textYinYang
    override val elementTendency: ElementMaybe get() = client.elementTendency
    override val textFiveElements: String get() = client.textFiveElements
    override val textWood: String get() = client.textWood
    override val textFire: String get() = client.textFire
    override val textEarth: String get() = client.textEarth
    override val textMetal: String get() = client.textMetal
    override val textWater: String get() = client.textWater

    override val textImpression: String get() = client.textImpression
    override val textMedical: String get() = client.textMedical
    override val textComplaints: String get() = client.textComplaints
    override val textPersonal: String get() = client.textPersonal
    override val textObjective: String get() = client.textObjective

    override val textMainObjective: String get() = client.textMainObjective
    override val textSymptoms: String get() = client.textSymptoms
    override val textSyndrom: String get() = client.textSyndrom
    override val category: ClientCategory get() = client.category
    override val donation: ClientDonation get() = client.donation

    override val tcmNote: String get() = client.tcmNote
    override val picture: MyImage get() = client.picture
    override val cprops: CProps get() = client.cprops

    override fun toString() =
            MoreObjects.toStringHelper(javaClass)
                    .add("id", id)
                    .add("firstName", firstName)
                    .add("nickNameInt", nickNameInt)
                    .add("lastName", lastName)
                    .add("countTreatments", countTreatments)
                    .add("upcomingAppointment", upcomingAppointment)
                    .add("differenceDaysToRecentTreatment", differenceDaysToRecentTreatment)
                    .toString()

}
