package com.example.data.repository

import com.example.data.local.ServiceDao
import com.example.data.model.CafeService
import com.example.data.model.ServiceCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ServiceRepository(private val serviceDao: ServiceDao) {

    fun getAllActiveServices(): Flow<List<CafeService>> = serviceDao.getAllActiveServices()

    fun getAllServicesAdmin(): Flow<List<CafeService>> = serviceDao.getAllServicesAdmin()

    fun getServiceById(id: String): Flow<CafeService?> = serviceDao.getServiceById(id)

    fun getServicesByCategory(category: ServiceCategory): Flow<List<CafeService>> =
        serviceDao.getServicesByCategory(category)

    fun searchServices(query: String): Flow<List<CafeService>> = serviceDao.searchServices(query)

    suspend fun updatePrice(serviceId: String, newPrice: Long) {
        serviceDao.updateServicePrice(serviceId, newPrice)
    }

    suspend fun saveService(service: CafeService) {
        serviceDao.insertService(service)
    }

    suspend fun addNewService(
        title: String,
        category: ServiceCategory,
        description: String,
        price: Long,
        requiredDocs: List<String>,
        timeEstimate: String
    ) {
        val newService = CafeService(
            id = "srv_" + UUID.randomUUID().toString().take(8),
            title = title,
            category = category,
            description = description,
            price = price,
            requiredDocuments = requiredDocs,
            timeEstimate = timeEstimate,
            active = true
        )
        serviceDao.insertService(newService)
    }
}
