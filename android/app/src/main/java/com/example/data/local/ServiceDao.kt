package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CafeService
import com.example.data.model.ServiceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE active = 1 ORDER BY popular DESC, title ASC")
    fun getAllActiveServices(): Flow<List<CafeService>>

    @Query("SELECT * FROM services ORDER BY title ASC")
    fun getAllServicesAdmin(): Flow<List<CafeService>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    fun getServiceById(id: String): Flow<CafeService?>

    @Query("SELECT * FROM services WHERE category = :category AND active = 1")
    fun getServicesByCategory(category: ServiceCategory): Flow<List<CafeService>>

    @Query("SELECT * FROM services WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchServices(query: String): Flow<List<CafeService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<CafeService>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: CafeService)

    @Update
    suspend fun updateService(service: CafeService)

    @Query("UPDATE services SET price = :newPrice WHERE id = :serviceId")
    suspend fun updateServicePrice(serviceId: String, newPrice: Long)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getCount(): Int
}
