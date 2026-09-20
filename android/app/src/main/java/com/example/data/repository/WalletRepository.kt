package com.example.data.repository

import com.example.data.local.TransactionDao
import com.example.data.local.UserDao
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Random
import java.util.UUID

class WalletRepository(
    private val transactionDao: TransactionDao,
    private val userDao: UserDao
) {

    fun getTransactionsByUser(userId: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByUser(userId)

    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions()

    suspend fun deposit(userId: String, amount: Long): Transaction {
        val txId = "SHP-" + (10000000 + Random().nextInt(90000000))
        val transaction = Transaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            amount = amount,
            type = TransactionType.DEPOSIT,
            transactionId = txId,
            description = "افزایش اعتبار کیف پول از طریق درگاه پرداخت شاپرک",
            isSuccess = true
        )
        transactionDao.insertTransaction(transaction)
        userDao.updateWalletBalance(userId, amount)
        return transaction
    }

    suspend fun payForOrder(userId: String, amount: Long, orderTrackingCode: String): Boolean {
        val user = userDao.getUserByIdOnce(userId) ?: return false
        if (user.walletBalance < amount) return false

        val txId = "ORD-" + orderTrackingCode
        val transaction = Transaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            amount = amount,
            type = TransactionType.ORDER_PAYMENT,
            transactionId = txId,
            description = "پرداخت هزینه سفارش $orderTrackingCode با کیف پول",
            isSuccess = true
        )
        transactionDao.insertTransaction(transaction)
        userDao.updateWalletBalance(userId, -amount)
        return true
    }

    suspend fun refund(userId: String, amount: Long, orderTrackingCode: String) {
        val txId = "REF-" + (10000000 + Random().nextInt(90000000))
        val transaction = Transaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            amount = amount,
            type = TransactionType.REFUND,
            transactionId = txId,
            description = "استرداد وجه سفارش $orderTrackingCode به کیف پول",
            isSuccess = true
        )
        transactionDao.insertTransaction(transaction)
        userDao.updateWalletBalance(userId, amount)
    }
}
