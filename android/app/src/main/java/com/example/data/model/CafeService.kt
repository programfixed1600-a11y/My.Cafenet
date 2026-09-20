package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ServiceCategory(val persianTitle: String, val iconName: String) {
    GOVERNMENT("خدمات دولتی", "account_balance"),
    VEHICLE("خدمات خودرو", "directions_car"),
    EDUCATION("خدمات آموزشی", "school"),
    FINANCIAL("خدمات مالی و مالیاتی", "payments"),
    JUDICIAL("خدمات قضایی", "gavel"),
    OFFICE("خدمات اداری و تایپ", "print")
}

data class ServiceFaq(
    val question: String,
    val answer: String
)

@Entity(tableName = "services")
data class CafeService(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: ServiceCategory,
    val description: String,
    val price: Long,
    val requiredDocuments: List<String>,
    val timeEstimate: String,
    val faqs: List<ServiceFaq> = emptyList(),
    val active: Boolean = true,
    val badge: String = "",
    val popular: Boolean = false
)
