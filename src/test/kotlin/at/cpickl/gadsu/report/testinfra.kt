package at.cpickl.gadsu.report

import java.io.InputStream

fun ClientReportData.Companion.testInstance(
        fullName: String = "Test Fullname",
        children: String? = null,
        job: String? = null,
        picture: InputStream? = null,
        cprops: String? = null
) =
        ClientReportData(
                fullName = fullName,
                children = children,
                job = job,
                picture = picture,
                cprops = cprops

        )
