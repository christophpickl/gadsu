package at.cpickl.gadsu.view

object ViewNames {
    val Main = MainViewNames
    val Client = ClientViewNames
    val Treatment = TreatmentViewNames
    val Preferences = PreferencesViewNames
}

object MainViewNames {
    val ContainerPanel = "Main.ContainerPanel"
    val ContentPanel = "Main.ContentPanel"
}

object ClientViewNames {
    val MainPanel = "Client.MainPanel"
    val List = "Client.List"

    val CreateButton = "Client.CreateButton"
    val SaveButton = "Client.SaveButton"
    val CancelButton = "Client.CancelButton"

    val InputFirstName = "Client.InputFirstName"
    val InputLastName = "Client.InputLastName"
}

object TreatmentViewNames {
    val MainPanel = "Treatment.MainPanel"
    val OpenNewButton = "Treatment.OpenNewButton"
    val BackButton = "Treatment.BackButton"
}

object PreferencesViewNames {
    val Window = "Preferences.Window"
}
