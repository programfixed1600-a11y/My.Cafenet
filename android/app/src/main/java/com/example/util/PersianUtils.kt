package com.example.util

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

/**
 * Utility functions for Persian localization, Jalali calendar dates,
 * Persian numerals, currency formatting (Toman), and Iranian validation rules.
 */
object PersianUtils {

    private val persianDigits = arrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /**
     * Converts English digits in a string to Persian digits.
     */
    fun toPersianDigits(input: String): String {
        val sb = StringBuilder()
        for (char in input) {
            if (char in '0'..'9') {
                sb.append(persianDigits[char - '0'])
            } else {
                sb.append(char)
            }
        }
        return sb.toString()
    }

    fun toPersianDigits(number: Long): String {
        return toPersianDigits(number.toString())
    }

    fun toPersianDigits(number: Int): String {
        return toPersianDigits(number.toString())
    }

    /**
     * Formats an amount to Persian Toman with commas.
     * Example: 50000 -> "۵۰,۰۰۰ تومان"
     */
    fun formatToman(amount: Long): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US) as DecimalFormat
        formatter.applyPattern("#,###")
        val formatted = formatter.format(amount)
        return "${toPersianDigits(formatted)} تومان"
    }

    /**
     * Iranian National Code (کد ملی) Validation Algorithm
     */
    fun isValidNationalCode(code: String): Boolean {
        val clean = code.trim().replace(" ", "").replace("-", "")
        if (clean.length != 10 || !clean.all { it.isDigit() }) return false
        if (clean.toSet().size == 1) return false // All identical digits like 1111111111

        val checkDigit = clean[9].digitToInt()
        var sum = 0
        for (i in 0 until 9) {
            sum += clean[i].digitToInt() * (10 - i)
        }
        val remainder = sum % 11
        return if (remainder < 2) {
            checkDigit == remainder
        } else {
            checkDigit == (11 - remainder)
        }
    }

    /**
     * Iranian Mobile Number validation (09xxxxxxxxx or 9xxxxxxxxx)
     */
    fun isValidIranianMobile(phone: String): Boolean {
        val clean = phone.trim().replace(" ", "").replace("-", "").replace("+98", "0")
        val regex = Regex("^09[0-9]{9}$")
        return regex.matches(clean)
    }

    /**
     * Converts current or provided timestamp to Jalali (Shamsi) Date string.
     * Example: "۱۴۰۴/۰۶/۱۲ - ۱۱:۳۰"
     */
    fun getPersianDateTime(timestamp: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val hour = String.format(Locale.US, "%02d", cal.get(Calendar.HOUR_OF_DAY))
        val minute = String.format(Locale.US, "%02d", cal.get(Calendar.MINUTE))

        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val datePart = "${jy}/${String.format(Locale.US, "%02d", jm)}/${String.format(Locale.US, "%02d", jd)}"
        val timePart = "$hour:$minute"
        return "${toPersianDigits(datePart)} - ${toPersianDigits(timePart)}"
    }

    fun getPersianDate(timestamp: Long = System.currentTimeMillis()): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)

        val (jy, jm, jd) = gregorianToJalali(gy, gm, gd)
        val datePart = "${jy}/${String.format(Locale.US, "%02d", jm)}/${String.format(Locale.US, "%02d", jd)}"
        return toPersianDigits(datePart)
    }

    /**
     * Gregorian to Jalali calendar algorithm
     */
    private fun gregorianToJalali(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gDaysInMonth = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = gy - 1600
        val gm2 = gm - 1
        val gd2 = gd - 1

        var gDayNo = 365 * gy2 + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400

        for (i in 0 until gm2) {
            gDayNo += gDaysInMonth[i]
        }
        if (gm2 > 1 && ((gy2 % 4 == 0 && gy2 % 100 != 0) || (gy2 % 400 == 0))) {
            gDayNo++
        }
        gDayNo += gd2

        var jDayNo = gDayNo - 79
        val jNp = jDayNo / 12053
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDayNo / 1461)
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365
            jDayNo = (jDayNo - 1) % 365
        }

        var jm = 0
        for (i in 0..11) {
            if (jDayNo < jDaysInMonth[i]) {
                jm = i
                break
            }
            jDayNo -= jDaysInMonth[i]
        }
        val jd = jDayNo + 1
        return Triple(jy, jm + 1, jd)
    }
}
