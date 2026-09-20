package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val managerName: String,
    val activeEmployeesCount: Int = 3,
    val activeOrdersCount: Int = 12
)
