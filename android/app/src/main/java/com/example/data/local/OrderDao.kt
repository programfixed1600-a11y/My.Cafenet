package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUser(userId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE employeeId = :employeeId OR employeeId = '' ORDER BY createdAt DESC")
    fun getOrdersForEmployee(employeeId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: String): Flow<Order?>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderByIdOnce(id: String): Order?

    @Query("SELECT * FROM orders WHERE trackingCode = :code LIMIT 1")
    fun getOrderByTrackingCode(code: String): Flow<Order?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET status = :status, revisionNotes = :notes, updatedAt = :time WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus, notes: String = "", time: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :paymentStatus, updatedAt = :time WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: String, paymentStatus: PaymentStatus, time: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET rating = :rating, reviewComment = :comment WHERE id = :orderId")
    suspend fun rateOrder(orderId: String, rating: Int, comment: String)

    @Query("SELECT SUM(finalPrice) FROM orders WHERE paymentStatus = 'PAID'")
    fun getTotalRevenue(): Flow<Long?>

    @Query("SELECT COUNT(*) FROM orders")
    fun getTotalOrdersCount(): Flow<Int>
}
