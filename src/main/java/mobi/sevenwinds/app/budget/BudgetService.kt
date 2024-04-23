package mobi.sevenwinds.app.budget

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mobi.sevenwinds.app.author.AuthorEntity
import mobi.sevenwinds.app.author.AuthorTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object BudgetService {
    suspend fun addRecord(body: BudgetRecordRequest): BudgetRecordResponse = withContext(Dispatchers.IO) {
        transaction {
            val author = AuthorEntity.find { AuthorTable.id eq body.authorId }.firstOrNull()
            val entity = BudgetEntity.new {
                this.year = body.year
                this.month = body.month
                this.amount = body.amount
                this.type = body.type
                this.authorEntity = author
            }

            return@transaction entity.toResponse()
        }
    }

    suspend fun getYearStats(param: BudgetYearParam): BudgetYearStatsResponse = withContext(Dispatchers.IO) {
        transaction {
            val query = BudgetTable
                .select { BudgetTable.year eq param.year }
                .orderBy(BudgetTable.month, SortOrder.ASC)
                .orderBy(BudgetTable.amount, SortOrder.DESC)
                .limit(param.limit, param.offset)

            val total = BudgetTable
                .select { BudgetTable.year eq param.year }
                .count()

            val all = BudgetTable
                .select { BudgetTable.year eq param.year }

            val listAll = BudgetEntity.wrapRows(all).map { it.toResponse() }

            val data = BudgetEntity.wrapRows(query).map { it.toResponse() }

            val sumByType = listAll.groupBy { it.type.name }.mapValues { it.value.sumOf { v -> v.amount } }




            return@transaction BudgetYearStatsResponse(
                total = total,
                totalByType = sumByType,
                items = data
            )
        }
    }
}