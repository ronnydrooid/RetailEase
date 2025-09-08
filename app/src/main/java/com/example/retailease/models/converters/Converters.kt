package com.example.retailease.models.converters

import androidx.room.TypeConverter
import com.example.retailease.models.TransactionType
import java.math.BigDecimal
import java.util.Date


class Converters {
    @TypeConverter
    fun fromTimeStamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? = value?.toPlainString()

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? = value?.let { BigDecimal(it) }

    @TypeConverter
    fun fromTransactionType(transactionType: TransactionType): String {
        return transactionType.name
    }

    @TypeConverter
    fun toTransactionType(transactionType: String): TransactionType {
        return TransactionType.valueOf(transactionType)
    }
}
