package at.cpickl.gadsu.preferences

import at.cpickl.gadsu.service.GapiCredentials
import at.cpickl.gadsu.service.testInstance


fun PreferencesData.Companion.emptyInstance() = PreferencesData("", false, null, null, null, null, null, null, null)
fun PreferencesData.Companion.testInstance() = PreferencesData(
        username = "testUsername",
        checkUpdates = true,
        proxy = "testProxy",
        gcalName = "testGcalName",
        gmailAddress = "testGmailAddress",
        gapiCredentials = GapiCredentials.testInstance(),
        treatmentGoal = 42,
        templateConfirmSubject = "testTemplateConfirmSubject",
        templateConfirmBody = "testTemplateConfirmBody"
)
