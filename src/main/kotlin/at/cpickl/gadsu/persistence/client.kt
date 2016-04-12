package at.cpickl.gadsu.persistence

interface ClientRepository {
    fun save(client: at.cpickl.gadsu.Client)
}
