package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.DocumentFile
import com.example.data.model.FileType
import com.example.data.model.NotificationType
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceFaq
import com.example.data.model.TransactionType
import com.example.data.model.UserRole
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.CUSTOMER)

    @TypeConverter
    fun fromServiceCategory(value: ServiceCategory): String = value.name

    @TypeConverter
    fun toServiceCategory(value: String): ServiceCategory = runCatching { ServiceCategory.valueOf(value) }.getOrDefault(ServiceCategory.GOVERNMENT)

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = runCatching { OrderStatus.valueOf(value) }.getOrDefault(OrderStatus.SUBMITTED)

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = runCatching { PaymentStatus.valueOf(value) }.getOrDefault(PaymentStatus.UNPAID)

    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = runCatching { TransactionType.valueOf(value) }.getOrDefault(TransactionType.DEPOSIT)

    @TypeConverter
    fun fromNotificationType(value: NotificationType): String = value.name

    @TypeConverter
    fun toNotificationType(value: String): NotificationType = runCatching { NotificationType.valueOf(value) }.getOrDefault(NotificationType.SYSTEM_ANNOUNCEMENT)

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<String>()
        runCatching {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        }
        return list
    }

    @TypeConverter
    fun fromFaqList(list: List<ServiceFaq>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { faq ->
            val obj = JSONObject()
            obj.put("q", faq.question)
            obj.put("a", faq.answer)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toFaqList(data: String?): List<ServiceFaq> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<ServiceFaq>()
        runCatching {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(ServiceFaq(obj.getString("q"), obj.getString("a")))
            }
        }
        return list
    }

    @TypeConverter
    fun fromDocumentFileList(list: List<DocumentFile>?): String {
        if (list == null) return "[]"
        val array = JSONArray()
        list.forEach { file ->
            val obj = JSONObject()
            obj.put("id", file.id)
            obj.put("name", file.name)
            obj.put("uriOrPath", file.uriOrPath)
            obj.put("fileType", file.fileType.name)
            obj.put("uploadTime", file.uploadTime)
            obj.put("sizeFormatted", file.sizeFormatted)
            obj.put("isResultFile", file.isResultFile)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toDocumentFileList(data: String?): List<DocumentFile> {
        if (data.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<DocumentFile>()
        runCatching {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val type = runCatching { FileType.valueOf(obj.getString("fileType")) }.getOrDefault(FileType.IMAGE)
                list.add(
                    DocumentFile(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        uriOrPath = obj.getString("uriOrPath"),
                        fileType = type,
                        uploadTime = obj.optLong("uploadTime", System.currentTimeMillis()),
                        sizeFormatted = obj.optString("sizeFormatted", "1 MB"),
                        isResultFile = obj.optBoolean("isResultFile", false)
                    )
                )
            }
        }
        return list
    }
}
