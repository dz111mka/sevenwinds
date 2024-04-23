package mobi.sevenwinds.app.author

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.joda.time.format.DateTimeFormat

object AuthorTable : IntIdTable("author") {
    val fullName = text("full_name")
    val creationDate = datetime("creation_date")
}

class AuthorEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AuthorEntity>(AuthorTable)

    var fullName by AuthorTable.fullName
    var creationDate by AuthorTable.creationDate

    fun toResponse(): AuthorRecordResponse {
        return AuthorRecordResponse(id.value, fullName, creationDate.toString(DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss")))
    }
}
