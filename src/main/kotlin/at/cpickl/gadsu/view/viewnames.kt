package at.cpickl.gadsu.view

import at.cpickl.gadsu.image.ImagePicker

object ViewNames {
    val Main = MainViewNames
    val MenuBar = MenuBarViewNames
    val Client = ClientViewNames
    val Appointment = AppointmentViewNames
    val Treatment = TreatmentViewNames
    val Acupunct = AcupunctViewNames
    val Preferences = PreferencesViewNames
    val MultiProtocol = MultiProtocolViewNames
    val Components = ComponentsViewNames
    val Assistent = AssistentViewNames
}

object MainViewNames {
    val ContainerPanel = "Main.ContainerPanel"
    val ContentPanel = "Main.ContentPanel"
}

object MenuBarViewNames {
    val ProtocolGenerate = "MenuBar.ProtocolGenerate"
}

object ClientViewNames {
    val MainPanel = "Client.MainPanel"
    val List = "Client.List"

    val CreateButton = "Client.CreateButton"
    val SaveButton = "Client.SaveButton"
    val CancelButton = "Client.CancelButton"

    val InputFirstName = "Client.InputFirstName"
    val InputLastName = "Client.InputLastName"
    val InputNickNameInt = "Client.InputNickNameInt"
    val InputNickNameExt = "Client.InputNickNameExt"
    val InputJob = "Client.InputJob"

    val ImageContainer = "Client.ImageContainer"
    val ImagePrefix = "Client.Image" // .Panel, .OpenButton
    val OpenImageButton = "$ImagePrefix.${ImagePicker.VIEWNAME_SUFFIX_OPENBUTTON}"
    val ImagePickerPanel = "$ImagePrefix.${ImagePicker.VIEWNAME_SUFFIX_PANEL}"
    val TabbedPane = "Client.TabbedPane"
    val InputChildren = "Client.InputChildren"
    val InputCountryOfOrigin = "Client.InputCountryOfOrigin"
    val InputOrigin = "Client.InputOrigin"
    val InputHobbies = "Client.InputHobbies"
    val InputKnownBy = "Client.InputKnownBy"
    val InputMail = "Client.InputMail"
    val InputPhone = "Client.InputPhone"
    val InputStreet = "Client.InputStreet"
    val InputZipCode = "Client.InputZipCode"
    val InputCity = "Client.InputCity"
    val InputDsgvoAccepted = "Client.InputDsgvoAccepted"
    val InputReceiveMails = "Client.InputReceiveMails"
    val InputGender = "Client.InputGender"
    val InputRelationship = "Client.InputRelationship"
    val InputCategory = "Client.InputCategory"
    val InputNote = "Client.InputNote"
    val InputTextYinYang = "Client.InputTextYinYang"
    val InputYyTendency = "Client.InputYyTendency"
    val InputElementTendency = "Client.InputElementTendency"
    val InputActive = "Client.InputActive"

    val InputTcmNote = "Client.InputTcmNote"
    val InputBirthdayPrefix = "Client.InputBirthday" // will be added something like ".XXX"

    val InputTextImpression = "Client.InputTextImpression"
    val InputTextMedical = "Client.InputTextMedical"
    val InputTextComplaints = "Client.InputTextComplaints"
    val InputTextPersonal = "Client.InputTextPersonal"
    val InputTextObjective = "Client.InputTextObjective"
    val InputTextMainObjective = "Client.InputTextMainObjective"
    val InputTextSymptoms = "Client.InputTextSymptoms"
    val InputTextFiveElements = "Client.InputTextFiveElements"
    val InputTextSyndrom = "Client.InputTextSyndrom"

    val InputTcmSearchField = "Client.InputTcmSearchField"

}

object AppointmentViewNames {
    val ListInClientView = "Appointment.ListInClientView"
    val InputStartDate = "Appointment.InputStartDate"
    val InputDuration = "Appointment.InputDuration"
    val InputNote = "Appointment.InputNote"
    val ButtonNewTreatment = "Appointment.ButtonNewTreatment"
    val ButtonSave = "Appointment.ButtonSave"
    val ButtonCancel = "Appointment.ButtonCancel"
}

object TreatmentViewNames {
    val MainPanel = "Treatment.MainPanel"
    val OpenNewButton = "Treatment.OpenNewButton"
    val BackButton = "Treatment.BackButton"
    val SaveButton = "Treatment.SaveButton"
    val ListInClientView = "Treatment.ListInClientView"
    val InputDatePrefix = "Treatment.InputDatePrefix" // will be added something like ".XXX"
    val InputDuration = "Treatment.Duration"
    val InputAboutDiscomfort = "Treatment.InputAboutDiscomfort"
    val InputAboutDiagnosis = "Treatment.InputAboutDiagnosis"
    val InputAboutContent = "Treatment.InputAboutContent"
    val InputAboutFeedback = "Treatment.InputAboutFeedback"
    val InputAboutHomework = "Treatment.InputAboutHomework"
    val InputAboutUpcoming = "Treatment.InputAboutUpcoming"
    val InputNote = "Treatment.InputNote"
    val ButtonPrevious = "Treatment.ButtonPrevious"
    val ButtonNext = "Treatment.ButtonNext"


}

object AcupunctViewNames {
    val List = "Acupunct.List"
}

object PreferencesViewNames {
    val Window = "Preferences.Window"
    val InputUsername = "Preferences.InputUsername"
    val InputThresholdAttention = "Preferences.InputThresholdAttention"
    val InputThresholdWarn = "Preferences.InputThresholdWarn"
    val InputThresholdFatal = "Preferences.InputThresholdFatal"
}

object MultiProtocolViewNames {
    val InputDescription = "MultiProtocol.InputDescription"
    val ButtonPrint = "MultiProtocol.ButtonPrint"
    val ButtonTestPrint = "MultiProtocol.ButtonTestPrint"
}

object ComponentsViewNames {
    val MultiProperties = MultiPropertiesViewNames
}

object AssistentViewNames {
    val ButtonRecalculate = "Assistent.ButtonRecalculate"
}

object MultiPropertiesViewNames {
    fun RenderText(id: String) = "MultiProperties.RenderText.$id"
    fun ButtonDone(id: String) = "MultiProperties.ButtonDone.$id"
    fun InputNote(id: String) = "MultiProperties.InputNote.$id"
    fun InputList(id: String) = "MultiProperties.InputList.$id"
}
