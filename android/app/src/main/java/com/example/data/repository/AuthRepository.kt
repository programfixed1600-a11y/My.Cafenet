package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class AuthRepository(private val userDao: UserDao) {

    // Currently logged-in active user
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "cust_01",
            name = "علی محمدی",
            phone = "09121234567",
            nationalCode = "0012345678",
            role = UserRole.CUSTOMER,
            walletBalance = 250000L
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()

    suspend fun loginWithPhone(phone: String): User {
        val existing = userDao.getUserByPhone(phone)
        val user = if (existing != null) {
            existing
        } else {
            val newUser = User(
                id = "user_" + UUID.randomUUID().toString().take(8),
                name = "کاربر محترم",
                phone = phone,
                nationalCode = "0010000000",
                role = UserRole.CUSTOMER,
                walletBalance = 50000L // 50,000 Toman welcome gift
            )
            userDao.insertUser(newUser)
            newUser
        }
        _currentUser.value = user
        return user
    }

    suspend fun registerUser(name: String, phone: String, nationalCode: String, role: UserRole): User {
        val existing = userDao.getUserByPhone(phone)
        val user = User(
            id = existing?.id ?: ("user_" + UUID.randomUUID().toString().take(8)),
            name = name.ifBlank { "کاربر کافینت" },
            phone = phone,
            nationalCode = nationalCode,
            role = role,
            walletBalance = existing?.walletBalance ?: 50000L
        )
        userDao.insertUser(user)
        _currentUser.value = user
        return user
    }

    suspend fun switchActiveUserRole(role: UserRole) {
        val current = _currentUser.value
        val switched = when (role) {
            UserRole.CUSTOMER -> {
                userDao.getUserByIdOnce("cust_01") ?: User(
                    id = "cust_01",
                    name = "علی محمدی",
                    phone = "09121234567",
                    nationalCode = "0012345678",
                    role = UserRole.CUSTOMER,
                    walletBalance = 250000L
                )
            }
            UserRole.EMPLOYEE -> {
                userDao.getUserByIdOnce("emp_01") ?: User(
                    id = "emp_01",
                    name = "سارا حسینی (اپراتور)",
                    phone = "09129876543",
                    nationalCode = "0087654321",
                    role = UserRole.EMPLOYEE,
                    walletBalance = 0L
                )
            }
            UserRole.ADMIN -> {
                userDao.getUserByIdOnce("admin_01") ?: User(
                    id = "admin_01",
                    name = "مهندس رضایی (مدیریت)",
                    phone = "09121112233",
                    nationalCode = "0076543210",
                    role = UserRole.ADMIN,
                    walletBalance = 1500000L
                )
            }
        }
        _currentUser.value = switched
    }

    suspend fun updateProfile(name: String, nationalCode: String) {
        val current = _currentUser.value ?: return
        val updated = current.copy(name = name, nationalCode = nationalCode)
        userDao.updateUser(updated)
        _currentUser.value = updated
    }

    suspend fun updateWalletBalanceLocally(newBalance: Long) {
        val current = _currentUser.value ?: return
        val updated = current.copy(walletBalance = newBalance)
        _currentUser.value = updated
    }
}
