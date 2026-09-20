package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppNotification
import com.example.data.model.Branch
import com.example.data.model.CafeService
import com.example.data.model.ChatMessage
import com.example.data.model.NotificationType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.ServiceCategory
import com.example.data.model.ServiceFaq
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import com.example.data.model.User
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        CafeService::class,
        Order::class,
        Transaction::class,
        AppNotification::class,
        ChatMessage::class,
        Branch::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun orderDao(): OrderDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao
    abstract fun chatDao(): ChatDao
    abstract fun branchDao(): BranchDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cafenet_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            // Pre-seed demo users (Customer, Operator, Admin)
            val demoUsers = listOf(
                User(
                    id = "cust_01",
                    name = "علی محمدی",
                    phone = "09121234567",
                    nationalCode = "0012345678",
                    role = UserRole.CUSTOMER,
                    walletBalance = 250000L
                ),
                User(
                    id = "emp_01",
                    name = "سارا حسینی (اپراتور ارشد)",
                    phone = "09129876543",
                    nationalCode = "0087654321",
                    role = UserRole.EMPLOYEE,
                    walletBalance = 0L
                ),
                User(
                    id = "admin_01",
                    name = "مهندس رضایی (مدیر کافی‌نت)",
                    phone = "09121112233",
                    nationalCode = "0076543210",
                    role = UserRole.ADMIN,
                    walletBalance = 1500000L
                )
            )
            demoUsers.forEach { database.userDao().insertUser(it) }

            // Pre-seed comprehensive Iranian CafeNet Services
            val services = listOf(
                // خدمات دولتی
                CafeService(
                    id = "gov_sana",
                    title = "ثبت‌نام سامانه ثنا و احراز هویت",
                    category = ServiceCategory.GOVERNMENT,
                    description = "ثبت نام اولیه و احراز هویت الکترونیکی غیرحضوری سامانه ثنا قوه قضاییه همراه با دریافت کد ثنا و رمز عبور.",
                    price = 45000L,
                    requiredDocuments = listOf("تصویر کارت ملی هوشمند", "تصویر شناسنامه", "عکس پرسنلی واضح ۴*۳", "شماره شبا بانکی به نام متقاضی"),
                    timeEstimate = "۱۵ الی ۳۰ دقیقه",
                    badge = "پرتقاضا",
                    popular = true,
                    faqs = listOf(
                        ServiceFaq("آیا حضور فیزیکی لازم است؟", "خیر، کل فرآیند آنلاین و از طریق اپراتور انجام می‌شود."),
                        ServiceFaq("کد ثنا چگونه ارسال می‌شود؟", "رمز شخصی ثنا به شماره موبایل شما پیامک خواهد شد.")
                    )
                ),
                CafeService(
                    id = "gov_adliran",
                    title = "عدل ایران و پیگیری پرونده قضایی",
                    category = ServiceCategory.GOVERNMENT,
                    description = "مشاهده آخرین وضعیت پرونده قضایی، رول پرونده، دادنامه‌ها و تصمیمات دادگاه در درگاه عدل ایران.",
                    price = 35000L,
                    requiredDocuments = listOf("کد ملی", "رمز شخصی سامانه ثنا", "شماره ۱۶ رقمی پرونده (اختیاری)"),
                    timeEstimate = "۱۰ دقیقه",
                    popular = true,
                    faqs = listOf(
                        ServiceFaq("آیا برای ورود به عدل ایران ثبت‌نام ثنا الزامی است؟", "بله، داشتن رمز شخصی ثنا ضروری است.")
                    )
                ),
                CafeService(
                    id = "gov_ncard",
                    title = "نوبت‌دهی و ثبت‌نام کارت ملی هوشمند",
                    category = ServiceCategory.GOVERNMENT,
                    description = "پیش‌ثبت‌نام آنلاین کارت هوشمند ملی، انتخاب نزدیک‌ترین دفتر پیشخوان و رزرو نوبت عکاسی و انگشت‌نگاری.",
                    price = 40000L,
                    requiredDocuments = listOf("تصویر صفحه اول شناسنامه", "کد پستی دقیق ۱۰ رقمی محل سکونت", "شماره همراه به نام متقاضی"),
                    timeEstimate = "۱۵ دقیقه",
                    popular = false
                ),
                CafeService(
                    id = "gov_subsidy",
                    title = "ثبت‌نام و استعلام دهک‌بندی یارانه",
                    category = ServiceCategory.GOVERNMENT,
                    description = "استعلام دهک خانوار در سامانه حمایت وزارت رفاه، ثبت اعتراض به دهک‌بندی و تغییر سرپرست خانوار.",
                    price = 30000L,
                    requiredDocuments = listOf("کد ملی سرپرست خانوار", "شماره همراه سرپرست"),
                    timeEstimate = "۱۰ دقیقه"
                ),
                CafeService(
                    id = "gov_services",
                    title = "پنجره ملی خدمات دولت هوشمند (my.gov.ir)",
                    category = ServiceCategory.GOVERNMENT,
                    description = "ورود و ثبت‌نام در سامانه یکپارچه دولت، دریافت تاییدیه مدارک تحصیلی و سوابق بیمه تامین اجتماعی.",
                    price = 35000L,
                    requiredDocuments = listOf("کد ملی", "کد پستی", "شماره موبایل به نام"),
                    timeEstimate = "۲۰ دقیقه"
                ),

                // خدمات خودرو
                CafeService(
                    id = "car_reg",
                    title = "ثبت‌نام خودرو (ایران‌خودرو و سایپا)",
                    category = ServiceCategory.VEHICLE,
                    description = "ثبت نام طرح‌های فروش فوق‌العاده، پیش‌فروش و مادران در سامانه یکپارچه فروش خودرو و قرعه‌کشی.",
                    price = 75000L,
                    requiredDocuments = listOf("کد ملی", "اطلاعات گواهینامه معتبر", "شماره حساب وکالتی تایید شده", "کد پستی و آدرس"),
                    timeEstimate = "۳۰ دقیقه",
                    badge = "تخصصی",
                    popular = true,
                    faqs = listOf(
                        ServiceFaq("آیا قبل از ثبت‌نام باید حساب وکالتی افتتاح شود؟", "بله، موجودی حساب وکالتی باید حداقل ۲۴ ساعت قبل تکمیل و تایید شده باشد.")
                    )
                ),
                CafeService(
                    id = "car_plate",
                    title = "نوبت‌دهی اینترنتی تعویض پلاک",
                    category = ServiceCategory.VEHICLE,
                    description = "رزرو نوبت تعویض پلاک در تمامی مراکز استان‌ها همراه با تعیین ساعت دقیق و صدور برگه نوبت بارکددار.",
                    price = 35000L,
                    requiredDocuments = listOf("شماره پلاک خودرو", "شماره شاسی خودرو", "کد ملی خریدار و فروشنده"),
                    timeEstimate = "۱۰ دقیقه",
                    popular = true
                ),
                CafeService(
                    id = "car_fines",
                    title = "استعلام و پرداخت خلافی خودرو و عوارض",
                    category = ServiceCategory.VEHICLE,
                    description = "استعلام فوری جزئیات جرایم رانندگی همراه با تصاویر دوربین‌ها و امکان پرداخت تجمیعی یا تکی آنی.",
                    price = 25000L,
                    requiredDocuments = listOf("شماره پلاک", "کد ملی مالک خودرو", "شماره موبایل مالک"),
                    timeEstimate = "۵ دقیقه",
                    popular = true
                ),
                CafeService(
                    id = "car_insurance",
                    title = "استعلام و صدور بیمه شخص ثالث و بدنه",
                    category = ServiceCategory.VEHICLE,
                    description = "مقایسه قیمت تمامی شرکت‌های بیمه (ایران، آسیا، دانا و...) و صدور آنلاین بیمه‌نامه با تخفیف عدم خسارت.",
                    price = 45000L,
                    requiredDocuments = listOf("تصویر کارت خودرو پشت و رو", "تصویر بیمه‌نامه قبلی", "تصویر گواهینامه"),
                    timeEstimate = "۴۵ دقیقه"
                ),
                CafeService(
                    id = "car_fuel",
                    title = "پیگیری و ثبت درخواست کارت سوخت المثنی",
                    category = ServiceCategory.VEHICLE,
                    description = "ثبت نام کارت سوخت جدید خودرو و موتورسیکلت در سامانه سخا پلیس و رهگیری پستی مرسوله.",
                    price = 40000L,
                    requiredDocuments = listOf("تصویر شناسنامه خودرو (برگ سبز)", "کارت ملی مالک", "بیمه‌نامه شخص ثالث"),
                    timeEstimate = "۲۰ دقیقه"
                ),

                // خدمات آموزشی
                CafeService(
                    id = "edu_konkur",
                    title = "ثبت‌نام کنکور سراسری و کارشناسی ارشد",
                    category = ServiceCategory.EDUCATION,
                    description = "خرید سریال اعتباری سازمان سنجش، تکمیل فرم تقاضانامه، تایید سوابق تحصیلی آموزش و پرورش و دریافت کد پیگیری.",
                    price = 65000L,
                    requiredDocuments = listOf("عکس پرسنلی اسکن شده استاندارد سنجش", "کد سوابق تحصیلی دیپلم و پیش‌دانشگاهی", "کد منطقه و کد پستی"),
                    timeEstimate = "۳۰ دقیقه",
                    badge = "تضمینی",
                    popular = true
                ),
                CafeService(
                    id = "edu_univ",
                    title = "ثبت‌نام دانشگاه (آزاد، پیام‌نور، علمی‌کاربردی)",
                    category = ServiceCategory.EDUCATION,
                    description = "انتخاب رشته، ثبت‌نام بدون کنکور بر اساس سوابق تحصیلی و واریز شهریه ثابت در سامانه‌های آموزشیار و گلستان.",
                    price = 60000L,
                    requiredDocuments = listOf("مدرک پایه دیپلم یا کارشناسی", "ریز نمرات", "تصاویر مدارک هویتی"),
                    timeEstimate = "۴۰ دقیقه"
                ),
                CafeService(
                    id = "edu_exam_card",
                    title = "دریافت کارت ورود به جلسه آزمون",
                    category = ServiceCategory.EDUCATION,
                    description = "چاپ کارت ورود به جلسه کنکور، آزمون‌های استخدامی، نهایی مدارس همراه با برگه راهنما به صورت PDF باکیفیت.",
                    price = 20000L,
                    requiredDocuments = listOf("شماره پرونده", "کد پیگیری یا شماره داوطلبی", "کد ملی"),
                    timeEstimate = "۵ دقیقه",
                    popular = true
                ),
                CafeService(
                    id = "edu_hire",
                    title = "ثبت‌نام آزمون‌های استخدامی دستگاه‌های اجرایی",
                    category = ServiceCategory.EDUCATION,
                    description = "ثبت‌نام آزمون‌های استخدامی سنجش، جهاد دانشگاهی، آموزش و پرورش و بانک‌ها با بررسی دقیق شرایط سنی و مدرک.",
                    price = 55000L,
                    requiredDocuments = listOf("مدرک تحصیلی مرتبط", "عکس پرسنلی", "کارت پایان خدمت (آقایان)", "اطلاعات بومی بودن"),
                    timeEstimate = "۳۰ دقیقه"
                ),

                // خدمات مالی و مالیاتی
                CafeService(
                    id = "fin_tax",
                    title = "اظهارنامه مالیاتی و ثبت‌نام کد اقتصادی",
                    category = ServiceCategory.FINANCIAL,
                    description = "تنظیم و ارسال اظهارنامه مالیات بر مشاغل (تبصره ماده ۱۰۰)، تشکیل پرونده مالیاتی و اتصال پوز بانکی.",
                    price = 90000L,
                    requiredDocuments = listOf("گردش حساب دستگاه کارتخوان", "اجاره‌نامه یا سند تجاری", "شناسنامه و کارت ملی"),
                    timeEstimate = "۱ الی ۲ ساعت",
                    badge = "تخصصی مالیاتی",
                    popular = true
                ),
                CafeService(
                    id = "fin_sejam",
                    title = "ثبت‌نام سجام و احراز هویت بورس",
                    category = ServiceCategory.FINANCIAL,
                    description = "ثبت نام کامل در سامانه سجام شرکت سپرده‌گذاری مرکزی، احراز هویت تصویری و دریافت کد ۱۰ رقمی بورسی.",
                    price = 45000L,
                    requiredDocuments = listOf("کارت ملی هوشمند", "شماره شبا بانکی معتبر", "آدرس و کد پستی دقیق"),
                    timeEstimate = "۲۰ دقیقه"
                ),
                CafeService(
                    id = "fin_equity",
                    title = "استعلام و مدیریت سهام عدالت",
                    category = ServiceCategory.FINANCIAL,
                    description = "مشاهده سبد دارایی سهام عدالت، تغییر شماره شبای بانکی واریز سود، استعلام سودهای واریزی گذشته.",
                    price = 30000L,
                    requiredDocuments = listOf("کد ملی دارنده سهام", "شماره همراه به نام"),
                    timeEstimate = "۱۰ دقیقه"
                ),

                // خدمات قضایی
                CafeService(
                    id = "jud_clearance",
                    title = "دریافت گواهی عدم سوء پیشینه الکترونیکی",
                    category = ServiceCategory.JUDICIAL,
                    description = "درخواست فوری صدور گواهی عدم سوء پیشینه از طریق درگاه خدمات الکترونیک قضایی و تحویل فایل PDF امضاشده.",
                    price = 50000L,
                    requiredDocuments = listOf("ثبت‌نام ثنا فعال", "عکس پرسنلی", "علت درخواست گواهی"),
                    timeEstimate = "۲۴ ساعت کاری",
                    badge = "رسمی",
                    popular = true
                ),
                CafeService(
                    id = "jud_notif",
                    title = "مشاهده و چاپ ابلاغیه الکترونیکی دادگاه",
                    category = ServiceCategory.JUDICIAL,
                    description = "ورود به سامانه ابلاغ عدل ایران، دریافت متن کامل ابلاغیه‌ها و احضاریه‌ها و چاپ با وضوح بالا.",
                    price = 25000L,
                    requiredDocuments = listOf("کد ملی", "رمز ثنا", "کد پیامک موقت"),
                    timeEstimate = "۵ دقیقه",
                    popular = true
                ),

                // خدمات اداری و تایپ
                CafeService(
                    id = "off_type",
                    title = "تایپ متون فارسی، انگلیسی و فرمول‌نویسی",
                    category = ServiceCategory.OFFICE,
                    description = "تایپ سریع و دقیق مقالات، نامه‌های اداری، پایان‌نامه، جزوه دست‌نویس با فونت‌های استاندارد B Nazanin و صفحه آرایی.",
                    price = 30000L,
                    requiredDocuments = listOf("تصویر صفحات دست‌نویس یا فایل صوتی"),
                    timeEstimate = "بر اساس حجم متن (۱ الی ۳ ساعت)",
                    popular = true
                ),
                CafeService(
                    id = "off_cv",
                    title = "ساخت رزومه کاری و تحصیلی حرفه‌ای (CV)",
                    category = ServiceCategory.OFFICE,
                    description = "طراحی رزومه کاری مدرن و جذاب با قالب‌های بین‌المللی و اداری، مناسب برای مصاحبه‌های شغلی و مهاجرت.",
                    price = 70000L,
                    requiredDocuments = listOf("سوابق شغلی و تحصیلی", "مهارت‌ها و دوره‌ها", "عکس پرسنلی مناسب"),
                    timeEstimate = "۲ ساعت",
                    badge = "طراحی VIP"
                ),
                CafeService(
                    id = "off_trans",
                    title = "ترجمه و ویرایش متون تخصصی",
                    category = ServiceCategory.OFFICE,
                    description = "ترجمه روان متون انگلیسی به فارسی و فارسی به انگلیسی در رشته‌های مختلف دانشگاهی و اداری.",
                    price = 60000L,
                    requiredDocuments = listOf("فایل متن منبع (PDF یا Word یا عکس)"),
                    timeEstimate = "۱ روز کاری"
                ),
                CafeService(
                    id = "off_print",
                    title = "پرینت و اسکن مدارک با ارسال آنلاین",
                    category = ServiceCategory.OFFICE,
                    description = "پرینت سیاه سفید و رنگی انواع اسناد و جزوات با ارسال فایل و دریافت حضوری یا پیک موتوری.",
                    price = 15000L,
                    requiredDocuments = listOf("فایل‌های PDF یا تصاویر مدارک"),
                    timeEstimate = "فوری"
                )
            )
            database.serviceDao().insertServices(services)

            // Pre-seed sample orders
            val sampleOrders = listOf(
                Order(
                    id = "ord_101",
                    trackingCode = "CN-94812",
                    userId = "cust_01",
                    userName = "علی محمدی",
                    userPhone = "09121234567",
                    userNationalCode = "0012345678",
                    serviceId = "gov_sana",
                    serviceTitle = "ثبت‌نام سامانه ثنا و احراز هویت",
                    category = ServiceCategory.GOVERNMENT,
                    employeeId = "emp_01",
                    employeeName = "سارا حسینی",
                    status = OrderStatus.IN_PROGRESS,
                    price = 45000L,
                    finalPrice = 45000L,
                    paymentStatus = PaymentStatus.PAID,
                    customerNotes = "لطفا در سریع‌ترین زمان انجام شود ممنون.",
                    createdAt = System.currentTimeMillis() - 3600000L * 3
                ),
                Order(
                    id = "ord_102",
                    trackingCode = "CN-83721",
                    userId = "cust_01",
                    userName = "علی محمدی",
                    userPhone = "09121234567",
                    userNationalCode = "0012345678",
                    serviceId = "jud_clearance",
                    serviceTitle = "دریافت گواهی عدم سوء پیشینه الکترونیکی",
                    category = ServiceCategory.JUDICIAL,
                    employeeId = "emp_01",
                    employeeName = "سارا حسینی",
                    status = OrderStatus.COMPLETED,
                    price = 50000L,
                    finalPrice = 50000L,
                    paymentStatus = PaymentStatus.PAID,
                    rating = 5,
                    reviewComment = "بسیار سریع و عالی فایل نهایی تحویل داده شد.",
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                )
            )
            sampleOrders.forEach { database.orderDao().insertOrder(it) }

            // Pre-seed sample notifications
            val sampleNotifications = listOf(
                AppNotification(
                    id = "notif_01",
                    userId = "cust_01",
                    title = "سفارش شما در حال انجام است",
                    message = "سفارش ثبت‌نام سامانه ثنا با کد رهگیری CN-94812 توسط اپراتور سارا حسینی در حال پیگیری است.",
                    type = NotificationType.STATUS_CHANGED,
                    orderId = "ord_101",
                    isRead = false,
                    date = System.currentTimeMillis() - 3600000L
                ),
                AppNotification(
                    id = "notif_02",
                    userId = "cust_01",
                    title = "تکمیل گواهی عدم سوء پیشینه",
                    message = "گواهی رسمی شما صادر شد و فایل PDF نهایی در بخش سفارش‌ها آماده دانلود است.",
                    type = NotificationType.ORDER_COMPLETED,
                    orderId = "ord_102",
                    isRead = true,
                    date = System.currentTimeMillis() - 86400000L
                )
            )
            database.notificationDao().insertNotifications(sampleNotifications)

            // Pre-seed sample transactions
            val sampleTransactions = listOf(
                Transaction(
                    id = "tx_01",
                    userId = "cust_01",
                    amount = 300000L,
                    type = TransactionType.DEPOSIT,
                    transactionId = "SHP-78219401",
                    description = "افزایش اعتبار از درگاه شاپرک بانک ملت",
                    isSuccess = true,
                    date = System.currentTimeMillis() - 86400000L * 3
                ),
                Transaction(
                    id = "tx_02",
                    userId = "cust_01",
                    amount = 50000L,
                    type = TransactionType.ORDER_PAYMENT,
                    transactionId = "ORD-83721",
                    description = "پرداخت سفارش گواهی عدم سوء پیشینه",
                    isSuccess = true,
                    date = System.currentTimeMillis() - 86400000L * 2
                )
            )
            sampleTransactions.forEach { database.transactionDao().insertTransaction(it) }

            // Pre-seed branches
            val branches = listOf(
                Branch(
                    id = "central",
                    name = "شعبه مرکزی کافینت هوشمند (میدان انقلاب)",
                    address = "تهران، میدان انقلاب، ابتدای کارگر شمالی، پلاک ۱۲",
                    phone = "02166954321",
                    managerName = "مهندس رضایی",
                    activeEmployeesCount = 4,
                    activeOrdersCount = 18
                ),
                Branch(
                    id = "branch_tajrish",
                    name = "شعبه شماره ۲ (تجریش)",
                    address = "تهران، میدان تجریش، مجتمع تجاری ارگ، طبقه اول",
                    phone = "02122718899",
                    managerName = "خانم طاهری",
                    activeEmployeesCount = 3,
                    activeOrdersCount = 9
                )
            )
            database.branchDao().insertBranches(branches)
        }
    }
}
