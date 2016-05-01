package at.cpickl.gadsu.client.xprops

import at.cpickl.gadsu.client.Client
import at.cpickl.gadsu.client.savedValidInstance
import at.cpickl.gadsu.client.xprops.model.CPropEnum
import at.cpickl.gadsu.client.xprops.model.CProps
import at.cpickl.gadsu.client.xprops.model.XProps
import at.cpickl.gadsu.client.xprops.SProp
import at.cpickl.gadsu.client.xprops.XPropsSqlRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class XPropsServiceImplTest {

    private val clientPrototype = Client.savedValidInstance()

    private val testEnum = XProps.Sleep
    private val testOps = listOf(XProps.Sleep_TiredInMorning, XProps.Sleep_TiredInEvening)
    private val testProps = CProps(mapOf(
            testEnum to CPropEnum(XProps.Sleep, testOps)
    ))

    private lateinit var repo: XPropsSqlRepository
    private lateinit var testee: XPropsService

    @BeforeMethod
    fun setup() {
        repo = Mockito.mock(XPropsSqlRepository::class.java)
        testee = XPropsServiceImpl(repo)
    }

    @AfterMethod
    fun verifyMocks() {
        verifyNoMoreInteractions(repo)
    }

    fun `update sunshine`() {
        val client = Client.savedValidInstance().copy(cprops = testProps)
        testee.update(client)

        verify(repo).delete(client)
        verify(repo).insert(client, listOf(SProp(testEnum.key, testOps.map { it.key }.joinToString(","))))
    }

    fun `read empty sunshine`() {
        val client = clientPrototype.copy(cprops = testProps)
        `when`(repo.select(client)).thenReturn(emptyList())

        assertThat(testee.read(client), equalTo(CProps.empty))
        verifyRepoSelectAll()
    }

    fun `read single sunshine`() {
        val client = Client.savedValidInstance().copy(cprops = testProps)
        `when`(repo.select(client)).thenReturn(listOf(SProp(testEnum.key, testOps.map { it.key }.joinToString(","))))

        assertThat(testee.read(client), equalTo(testProps))
        verifyRepoSelectAll()
    }

    private fun verifyRepoSelectAll() {
        verify(repo).select(clientPrototype)
    }

}
