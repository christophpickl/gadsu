package at.cpickl.gadsu

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.Contact
import at.cpickl.gadsu.client.unsavedValidInstance
import at.cpickl.gadsu.export.ExportData
import at.cpickl.gadsu.export.ExportXstreamService
import at.cpickl.gadsu.image.MyImage
import at.cpickl.gadsu.testinfra.TEST_CLIENT_PIC1
import at.cpickl.gadsu.testinfra.TEST_DATE
import at.cpickl.gadsu.testinfra.TEST_DATE_1985
import at.cpickl.gadsu.treatment.Treatment


fun main(args: Array<String>) {
    val service = ExportXstreamService()
    val xml = service.export(
            ExportData(
                    created = TEST_DATE.minusYears(5),
                    _clients = listOf(
                            Client.INSERT_PROTOTYPE,
                            Client.unsavedValidInstance().copy(
                                    created = TEST_DATE.minusDays(1),
                                    picture = MyImage.TEST_CLIENT_PIC1,
                                    contact = Contact.INSERT_PROTOTYPE.copy(mail = "mail@home.at")
                            )),
                    _treatments = listOf(Treatment.insertPrototype(
                            "fooId", 1, TEST_DATE.minusMinutes(4), TEST_DATE_1985.plusDays(1), "my treat note")))
    )

    println("\n\nXML result:\n$xml\n\n")

    println("----------------------------\n")

    val obj = service.import(xml)
    println("imported: $obj")

//    Files.write(obj.clients[1].picture.toSaveRepresentation(), File("readFromXstream.jpg"))
}

