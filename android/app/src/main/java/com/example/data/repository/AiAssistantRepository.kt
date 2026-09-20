package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.ServiceDao
import com.example.data.model.CafeService
import com.example.util.PersianUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiAssistantRepository(private val serviceDao: ServiceDao) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getAiResponse(userPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val geminiAnswer = callGeminiApi(userPrompt, apiKey)
                if (geminiAnswer.isNotBlank()) {
                    return@withContext geminiAnswer
                }
            } catch (e: Exception) {
                // Fallback to local Persian CafeNet intelligence engine
            }
        }

        // Local Iranian CafeNet Expert Engine (پاسخگوی هوشمند محلی امور کافی‌نت)
        return@withContext generateLocalPersianResponse(userPrompt)
    }

    private fun callGeminiApi(prompt: String, apiKey: String): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val systemInstructionText = """
            شما دستیار هوشمند و مجرب کافینت هوشمند (پیشخوان الکترونیک خدمات دولت و کافی‌نت در ایران) هستید.
            وظیفه شما راهنمایی دقیق، مودبانه و حرفه‌ای به زبان فارسی برای کاربران ایرانی است.
            شما اطلاعات کاملی در خصوص مدارک لازم، زمان انجام، هزینه‌ها، شرایط و نحوه ثبت‌نام در سامانه‌های ایرانی نظیر:
            ثنا، عدل ایران، ثبت‌نام خودرو (ایران‌خودرو و سایپا)، کنکور سراسری، آزمون‌های استخدامی، سجام و بورس، اظهارنامه مالیاتی (تبصره ۱۰۰)، کارت سوخت، تعویض پلاک، خلافی خودرو، عدم سوء پیشینه، وام ازدواج و خدمات تایپ و پرینت دارید.
            پاسخ‌ها باید ساختاریافته، با بولت‌پوینت‌های خوانا و کاملاً فارسی و روان باشند.
        """.trimIndent()

        val json = JSONObject()
        val contents = JSONArray()
        val contentObj = JSONObject()
        val parts = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", prompt)
        parts.put(partObj)
        contentObj.put("parts", parts)
        contents.put(contentObj)

        val sysInstructionObj = JSONObject()
        val sysParts = JSONArray()
        val sysPart = JSONObject()
        sysPart.put("text", systemInstructionText)
        sysParts.put(sysPart)
        sysInstructionObj.put("parts", sysParts)

        json.put("contents", contents)
        json.put("systemInstruction", sysInstructionObj)

        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: return ""
            val resObj = JSONObject(responseBody)
            val candidates = resObj.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.getJSONObject("content")
                val resParts = content.getJSONArray("parts")
                if (resParts.length() > 0) {
                    return resParts.getJSONObject(0).getString("text")
                }
            }
        }
        return ""
    }

    private fun generateLocalPersianResponse(prompt: String): String {
        val lower = prompt.trim()

        return when {
            lower.contains("ثنا") || lower.contains("قضایی") -> {
                """
                ⚖️ **راهنمای ثبت‌نام سامانه ثنا و عدل ایران:**
                
                📋 **مدارک مورد نیاز:**
                • تصویر کارت هوشمند ملی (پشت و رو)
                • تصویر صفحه اول شناسنامه
                • شماره شبا بانکی به نام شخص متقاضی
                • شماره موبایل فعال به نام متقاضی
                • عکس پرسنلی واضح با پس‌زمینه سفید
                
                ⏱ **مدت زمان انجام:** حدود ۱۵ الی ۳۰ دقیقه
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(45000L)}
                
                💡 برای ثبت این درخواست، می‌توانید از بخش «خدمات دولتی» گزینه **ثبت‌نام سامانه ثنا** را انتخاب نمایید.
                """.trimIndent()
            }
            lower.contains("خودرو") || lower.contains("ایران خودرو") || lower.contains("سایپا") -> {
                """
                🚗 **راهنمای ثبت‌نام خودرو (سامانه یکپارچه):**
                
                📋 **مدارک و الزامات:**
                • حساب وکالتی تایید شده با موجودی کافی (حداقل ۲۴ ساعت قبل)
                • گواهینامه رانندگی دارای اعتبار (پشت و رو)
                • کد ملی و مشخصات شناسنامه‌ای
                • شماره همراه به نام شخص متقاضی
                • کد پستی ۱۰ رقمی تایید شده در سامانه سخا
                
                ⏱ **مدت زمان انجام:** ۲۰ الی ۴۰ دقیقه
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(75000L)}
                
                💡 کلیه مراحل ثبت‌نام و انتخاب اولویت‌ها توسط اپراتور با تضمین عدم خطا انجام می‌گردد.
                """.trimIndent()
            }
            lower.contains("کنکور") || lower.contains("سنجش") || lower.contains("دانشگاه") || lower.contains("استخدام") -> {
                """
                🎓 **راهنمای ثبت‌نام کنکور و آزمون‌های سراسری:**
                
                📋 **مدارک لازم:**
                • عکس پرسنلی استاندارد با فرمت و ابعاد تعیین‌شده سنجش
                • کد ۱۹ رقمی تاییدیه سوابق تحصیلی آموزش و پرورش
                • اطلاعات شناسنامه و کارت ملی
                • کارت پایان خدمت یا معافیت (برای آقایان)
                
                ⏱ **مدت زمان انجام:** حدود ۲۰ الی ۳۰ دقیقه
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(65000L)}
                
                💡 در کافینت هوشمند، سریال اعتباری سازمان سنجش مستقیماً تهیه و فرم تقاضانامه تکمیل می‌گردد.
                """.trimIndent()
            }
            lower.contains("مالیات") || lower.contains("کد اقتصادی") || lower.contains("پوز") -> {
                """
                📊 **راهنمای اظهارنامه مالیاتی و تبصره ماده ۱۰۰:**
                
                📋 **مدارک لازم:**
                • نام کاربری و کلمه عبور درگاه ملی خدمات مالیاتی (my.tax.gov.ir)
                • مجموع گردش مالی دستگاه پوز / درگاه پرداخت در سال گذشته
                • اجاره‌نامه یا سند تجاری محل کسب
                
                ⏱ **مدت زمان انجام:** ۱ الی ۲ ساعت
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(90000L)}
                """.trimIndent()
            }
            lower.contains("سوء پیشینه") || lower.contains("سو پیشینه") -> {
                """
                📜 **راهنمای گواهی عدم سوء پیشینه الکترونیکی:**
                
                📋 **شرایط و مدارک:**
                • دارا بودن حساب ثنا فعال
                • عکس پرسنلی واضح
                • کد پستی محل سکونت
                
                ⏱ **زمان صدور:** از چند ساعت تا حداکثر ۲۴ ساعت کاری
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(50000L)}
                
                💡 نتیجه به صورت فایل PDF رسمی با کد پیگیری قوه قضاییه در بخش «سفارش‌های من» تحویل داده می‌شود.
                """.trimIndent()
            }
            lower.contains("سجام") || lower.contains("بورس") || lower.contains("سهام") -> {
                """
                📈 **راهنمای ثبت‌نام و احراز هویت سجام:**
                
                📋 **مدارک لازم:**
                • کارت ملی و شناسنامه
                • شماره شبا بانکی بدون خط‌تیره
                • شماره موبایل فعال
                
                ⏱ **زمان انجام:** ۱۵ دقیقه
                💰 **هزینه خدمت:** ${PersianUtils.formatToman(45000L)}
                """.trimIndent()
            }
            lower.contains("هزینه") || lower.contains("قیمت") || lower.contains("چقدر") -> {
                """
                💳 **لیست نرخ مصوب خدمات کافینت هوشمند:**
                
                • استعلام خلافی خودرو: ${PersianUtils.formatToman(25000L)}
                • ثبت‌نام سامانه ثنا: ${PersianUtils.formatToman(45000L)}
                • ثبت‌نام خودرو: ${PersianUtils.formatToman(75000L)}
                • ثبت‌نام کنکور و ارشد: ${PersianUtils.formatToman(65000L)}
                • گواهی عدم سوء پیشینه: ${PersianUtils.formatToman(50000L)}
                • اظهارنامه مالیاتی: ${PersianUtils.formatToman(90000L)}
                • ساخت رزومه کاری حرفه‌ای: ${PersianUtils.formatToman(70000L)}
                • نوبت تعویض پلاک: ${PersianUtils.formatToman(35000L)}
                
                🎁 با هر بار شارژ کیف پول از ۵٪ تخفیف ویژه بهره‌مند شوید.
                """.trimIndent()
            }
            lower.contains("زمان") || lower.contains("طول میکشد") || lower.contains("ساعت") -> {
                """
                ⏱ **زمان‌بندی انجام خدمات در کافینت هوشمند:**
                
                • خدمات استعلامی (خلافی، ابلاغیه، کارت آزمون): **۵ الی ۱۰ دقیقه**
                • خدمات ثبت‌نامی (ثنا، سجام، تعویض پلاک، ثبت‌نام خودرو): **۱۵ الی ۳۰ دقیقه**
                • خدمات تایپ و رزومه‌سازی: **۱ الی ۳ ساعت**
                • گواهی عدم سوء پیشینه: **حداکثر ۲۴ ساعت**
                
                📌 به محض تغییر وضعیت یا آماده شدن فایل، پیامک و اعلان در اپلیکیشن برای شما ارسال خواهد شد.
                """.trimIndent()
            }
            else -> {
                """
                سلام و درود بر شما کاربر گرامی! 🌸
                من دستیار هوشمند کافینت هوشمند هستم. شما می‌توانید در خصوص موارد زیر از من سوال بپرسید:
                
                🔹 **مدارک لازم** برای سامانه‌ها (ثنا، خودرو، کنکور، مالیات و...)
                🔹 **هزینه و تعرفه** خدمات مختلف
                🔹 **مدت زمان** لازم برای انجام هر کار
                🔹 **راهنمایی** در انتخاب خدمت متناسب با نیاز شما
                
                چه خدمتی مد نظر شماست تا راهنمایی‌تان کنم؟
                """.trimIndent()
            }
        }
    }
}
