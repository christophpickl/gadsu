package at.cpickl.gadsu.service

import at.cpickl.gadsu.version.Version
import at.cpickl.gadsu.version.VersionTag
import org.joda.time.DateTime

val MetaInf.Companion.DUMMY: MetaInf get() = MetaInf(Version(1, 42, 0, VersionTag.Release), DateTime.now())
