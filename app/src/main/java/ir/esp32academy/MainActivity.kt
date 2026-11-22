package ir.esp32academy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

private val Bg = Color(0xFF101817)
private val Surface = Color(0xFF1B2725)
private val Mint = Color(0xFF52E0B5)
private val TextMain = Color(0xFFE8F1EF)
private val Muted = Color(0xFFB4C5C1)

class MainActivity : ComponentActivity() { 
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        setContent { 
            // اعمال سراسری راست‌چین بودن برنامه (RTL)
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AcademyApp() 
            }
        } 
    } 
}

data class Lesson(val title: String, val objective: String, val wiring: String, val code: String, val explanation: String)

private val lessons = listOf(
    Lesson(
        "چشمک‌زدن LED", 
        "روشن و خاموش کردن یک LED با تأخیر زمانی.", 
        "پایه مثبت LED (آند) را با مقاومت ۲۲۰ تا ۳۳۰ اهم به GPIO 2 و پایه منفی (کاتد) را به GND وصل کنید.", 
        """const int LED_PIN = 2;

void setup() {
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  digitalWrite(LED_PIN, HIGH);
  delay(500);
  digitalWrite(LED_PIN, LOW);
  delay(500);
}""", 
        "در تابع setup پایه به‌عنوان خروجی (OUTPUT) تنظیم می‌شود. تابع loop پیوسته وضعیت پایه را هر نیم‌ثانیه تغییر می‌دهد. در بعضی بردها LED داخلی روی GPIO 2 نیست؛ مشخصات برد را بررسی کنید."
    ),
    Lesson(
        "خواندن کلید (Button)", 
        "خواندن وضعیت کلید با مقاومت بالاکش داخلی (Internal Pull-up).", 
        "یک سر کلید را به GPIO 4 و سر دیگر را به GND وصل کنید. حالت INPUT_PULLUP باعث می‌شود در حالت آزاد مقدار HIGH و هنگام فشردن مقدار LOW خوانده شود.", 
        """const int BUTTON_PIN = 4;

void setup() {
  Serial.begin(115200);
  pinMode(BUTTON_PIN, INPUT_PULLUP);
}

void loop() {
  if (digitalRead(BUTTON_PIN) == LOW) {
    Serial.println("کلید فشرده شد");
  } else {
    Serial.println("کلید آزاد است");
  }
  delay(200);
}""", 
        "Serial Monitor را روی 115200 baud باز کنید. چون ورودی pull-up است، LOW به معنی فشرده‌شدن کلید است. برای حذف پرش مکانیکی (Bounce) می‌توان debounce اضافه کرد."
    ),
    Lesson(
        "خواندن سنسور آنالوگ (ADC)", 
        "نمونه‌برداری از ولتاژ خروجی سنسور با مبدل آنالوگ به دیجیتال.", 
        "خروجی سنسور را به GPIO 34، زمین سنسور را به GND و تغذیه مناسب را وصل کنید. ولتاژ ورودی نباید از ۳.۳ ولت تجاوز کند.", 
        """const int SENSOR_PIN = 34;

void setup() {
  Serial.begin(115200);
}

void loop() {
  int value = analogRead(SENSOR_PIN);
  Serial.print("Sensor Value: ");
  Serial.println(value);
  delay(500);
}""", 
        "در ESP32 مقدار ADC معمولاً در بازه ۰ تا ۴۰۹۵ گزارش می‌شود، اما دقت و بازه ولتاژ به مدل و تنظیمات ADC وابسته است. GPIO 34 فقط ورودی است و مقاومت داخلی pull-up ندارد."
    ),
    Lesson(
        "اتصال به Wi-Fi", 
        "اتصال ESP32 به شبکه بی‌سیم ۲.۴ گیگاهرتز.", 
        "سیم‌کشی لازم نیست. نام و گذرواژه شبکه خود را جایگزین مقادیر نمونه کنید.", 
        """#include <WiFi.h>

const char* ssid = "YOUR_SSID";
const char* password = "YOUR_PASSWORD";

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  Serial.print("در حال اتصال");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("IP: " );
  Serial.println(WiFi.localIP());
}

void loop() {
  // اتصال حفظ می‌شود
}""", 
        "برد به شبکه ۲.۴ گیگاهرتز متصل می‌شود. پس از اتصال، IP در Serial Monitor چاپ می‌شود. اطلاعات واقعی شبکه را در کد عمومی منتشر نکنید."
    ),
    Lesson(
        "وب‌سرور ساده", 
        "نمایش صفحه وب محلی و کنترل LED از طریق مرورگر.", 
        "LED و مقاومت را میان GPIO 2 و GND ببندید. برد و گوشی/رایانه باید روی یک شبکه باشند.", 
        """#include <WiFi.h>
#include <WebServer.h>

const char* ssid = "YOUR_SSID";
const char* password = "YOUR_PASSWORD";
const int LED_PIN = 2;
WebServer server(80);

void handleRoot() {
  String page = "<h1>کنترل ESP32</h1>";
  page += "<p><a href='/on'>روشن</a></p>";
  page += "<p><a href='/off'>خاموش</a></p>";
  server.send(200, "text/html; charset=utf-8", page);
}

void setup() {
  Serial.begin(115200);
  pinMode(LED_PIN, OUTPUT);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) delay(500);
  server.on("/", handleRoot);
  server.on("/on", []() { digitalWrite(LED_PIN, HIGH); handleRoot(); });
  server.on("/off", []() { digitalWrite(LED_PIN, LOW); handleRoot(); });
  server.begin();
  Serial.println(WiFi.localIP());
}

void loop() {
  server.handleClient();
}""", 
        "پس از جایگزینی اطلاعات Wi-Fi، IP چاپ‌شده را در مرورگر دستگاهی در همان شبکه باز کنید. مسیرهای /on و /off وضعیت LED را عوض می‌کنند؛ handleClient درخواست‌ها را پردازش می‌کند."
    ),
)

@Composable 
fun AcademyApp() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    
    // رنگ‌بندی و تنظیمات Material Design
    MaterialTheme(colorScheme = darkColorScheme(background=Bg, surface=Surface, primary=Mint, onBackground=TextMain, onSurface=TextMain)) {
        Scaffold(containerColor=Bg, bottomBar={ 
            NavigationBar(containerColor=Surface) {
                val items = listOf(
                    Triple("home", "خانه", Icons.Filled.Home),
                    Triple("examples", "مثال‌ها", Icons.Filled.Code),
                    Triple("about", "درباره", Icons.Filled.Info)
                )
                items.forEach { (r, label, icon) -> 
                    NavigationBarItem(
                        selected = route == r || (r == "examples" && route?.startsWith("lesson") == true), 
                        onClick = {
                            nav.navigate(r) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }, 
                        icon = { Icon(icon, null) }, 
                        label = { Text(label) }, 
                        colors = NavigationBarItemDefaults.colors(selectedIconColor=Mint, selectedTextColor=Mint, indicatorColor=Bg)
                    ) 
                } 
            } 
        }) { pad ->
            NavHost(navController=nav, startDestination="splash", modifier=Modifier.padding(pad)) {
                composable("splash") { 
                    SplashScreen { 
                        nav.navigate("home") { 
                            popUpTo("splash") { inclusive = true } 
                        } 
                    } 
                }
                composable("home") { HomeScreen { nav.navigate(it) } }
                composable("intro") { 
                    InfoScreen("معرفی ESP32", listOf(
                        "ESP32 یک خانواده از میکروکنترلرهای کم‌مصرف و قدرتمند است که توسط شرکت Espressif Systems طراحی شده و برای پروژه‌های اینترنت اشیا (IoT) و سامانه‌های تعبیه‌شده به‌کار می‌رود.",
                        "ویژگی بارز آن وجود Wi‑Fi و Bluetooth در بسیاری از مدل‌ها است که ارتباط بی‌سیم را بدون نیاز به ماژول جداگانه ممکن می‌کند.",
                        "پردازنده آن معمولاً دو هسته‌ای (Dual-Core) است و فرکانس آن تا 240 مگاهرتز می‌رسد که برای پردازش‌های نسبتاً سنگین مناسب است.",
                        "کاربردها: خانه هوشمند (Smart Home)، خواندن حسگرها (Sensors)، کنترل از راه دور، رباتیک و پایش محیط."
                    )) 
                }
                composable("specs") { SpecsScreen() }
                composable("pins") { PinsScreen() }
                composable("examples") { ExamplesScreen { nav.navigate("lesson/$it") } }
                composable("quiz") { QuizScreen() }
                composable("wireless") { 
                    InfoScreen("Wi-Fi و Bluetooth", listOf(
                        "بسیاری از بردهای ESP32 از Wi-Fi باند ۲.۴ گیگاهرتز پشتیبانی می‌کنند؛ مشخصات مدل دقیق خود را بررسی کنید.",
                        "کاربردها: اتصال به شبکه محلی، وب‌سرور داخلی (مانند کنترل LED)، ارسال داده حسگر به سرور و کنترل از راه دور.",
                        "Bluetooth Classic و BLE در مدل‌های مختلف پشتیبانی متفاوتی دارند. برای مصرف کم انرژی و حسگرهای پوشیدنی معمولاً BLE مناسب است.",
                        "اطلاعات ورود شبکه را در کد عمومی یا مخزن منتشر نکنید؛ گذرواژه نمونه در این برنامه صرفاً جایگزین متنی است."
                    )) 
                }
                composable("iot") { 
                    InfoScreen("پروژه‌های اینترنت اشیاء", listOf(
                        "پایش دما و رطوبت: خواندن حسگر DHT11/DHT22 و نمایش داده در داشبورد محلی یا ابری.",
                        "خانه هوشمند: کنترل لامپ یا بار کم‌ولتاژ از طریق وب‌سرور یا اپلیکیشن (برای برق شهر حتماً از رله ایزوله و نظارت متخصص استفاده کنید).",
                        "ایستگاه محیطی: اندازه‌گیری نور، دما یا کیفیت هوا و ثبت روندها در حافظه.",
                        "رباتیک: کنترل موتورها و سرووها از طریق Wi-Fi یا Bluetooth.",
                        "در هر پروژه هدف یادگیری، نمودار اتصال، فهرست قطعات، روش آزمون و ملاحظات ایمنی را ثبت کنید."
                    )) 
                }
                composable("competition") { 
                    InfoScreen("راهنمای مسابقه آفرینش هوشمند", listOf(
                        "این نرم‌افزار در قالب یک اپلیکیشن آموزشی تعاملی برای آموزش میکروکنترلر ESP32 و اینترنت اشیاء قرار می‌گیرد؛ این موضوع در محورهای پیشنهادی شیوه‌نامه، از جمله آموزش بردهای توسعه و طراحی ابزار آموزشی تعاملی، آمده است.",
                        "برای اثر آموزشی، هدف یادگیری روشن تعریف کنید؛ محتوا باید علمی، متناسب با سطح هنرستان و قابل استفاده در تدریس یا کارگاه باشد.",
                        "شیوه‌نامه استفاده از ابزارهای هوش مصنوعی در فرایند تولید را الزامی دانسته و شرح ابزارها و میزان استفاده را در مستندات لازم می‌داند. محتوای تولیدشده باید بازبینی و تأیید علمی شود.",
                        "این راهنما جایگزین نسخه رسمی شیوه‌نامه نیست. ضوابط، مهلت‌ها و مدارک ارسال را در متن رسمی آخرین دوره بررسی کنید."
                    )) 
                }
                composable("about") { 
                    InfoScreen("درباره آکادمی", listOf(
                        "پدیدآورنده و نویسنده: دکتر مهدی قاسمی",
                        "هنرستان فنی طالقانی شازند - استان مرکزی",
                        "این برنامه یک راهنمای آفلاین فارسی برای آشنایی با ESP32 و تمرین مثال‌های پایه است.",
                        "نمونه‌ها برای Arduino Core for ESP32 نوشته شده‌اند. شماره پایه‌ها و امکانات واقعی را با مدل برد خود تطبیق دهید."
                    )) 
                }
                composable("lesson/{id}", arguments=listOf(navArgument("id"){type=NavType.IntType})) { back -> 
                    val id = back.arguments?.getInt("id") ?: 0
                    LessonScreen(lessons.getOrElse(id){lessons[0]}) 
                }
            }
        }
    }
}

@Composable 
private fun SplashScreen(continueApp:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally){
        Icon(Icons.Filled.Memory, contentDescription = "ESP32", tint = Mint, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("ESP32", fontSize=46.sp, fontWeight=FontWeight.Bold, color=Mint)
        Text("آکادمی ESP32", fontSize=28.sp, fontWeight=FontWeight.Bold, color=TextMain, textAlign=TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Panel { 
            Text("آموزش تعاملی میکروکنترلر و اینترنت اشیاء", color=Mint, fontWeight=FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Body("پدیدآورنده: دکتر مهدی قاسمی")
            Body("هنرستان فنی طالقانی شازند - استان مرکزی") 
        }
        Spacer(Modifier.height(22.dp))
        Button(onClick=continueApp, colors=ButtonDefaults.buttonColors(containerColor=Mint, contentColor=Bg)){
            Text("ورود به آکادمی", fontWeight=FontWeight.Bold) 
        }
    }
}

@Composable 
private fun Page(title:String, content:@Composable ColumnScope.()->Unit){ 
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)){ 
        Row(
            verticalAlignment = Alignment.CenterVertically, 
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(title, fontSize=26.sp, fontWeight=FontWeight.Bold, color=Mint, textAlign=TextAlign.Right, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = Mint)
        }
        content() 
    } 
}

@Composable 
private fun Panel(content:@Composable ColumnScope.()->Unit){ 
    Column(
        Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(8.dp))
            .padding(16.dp), 
        verticalArrangement=Arrangement.spacedBy(10.dp), 
        content=content
    ) 
}

// این تابع تضمین می‌کند که همه متن‌ها از سمت راست شروع شوند
@Composable 
private fun Body(text:String){ 
    Text(
        text = text, 
        color = TextMain, 
        fontSize = 16.sp, 
        lineHeight = 26.sp, 
        textAlign = TextAlign.Right, // راست‌چین کردن متن
        modifier = Modifier.fillMaxWidth()
    ) 
}

@Composable 
private fun HomeScreen(open:(String)->Unit){ 
    Page("آکادمی ESP32") {
        Panel { 
            Text("از آشنایی تا ساخت پروژه", color=Mint, fontSize=20.sp, fontWeight=FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Body("راهنمای جامع، فارسی و آفلاین برای شناخت برد ESP32 و شروع برنامه‌نویسی آن با Arduino IDE.") 
        }
        val menuItems = listOf(
            Triple("معرفی ESP32", "intro", Icons.Filled.Memory),
            Triple("مشخصات فنی", "specs", Icons.Filled.Tune),
            Triple("پین‌اوت و پایه‌ها", "pins", Icons.Filled.DeveloperBoard),
            Triple("Wi-Fi و Bluetooth", "wireless", Icons.Filled.Wifi),
            Triple("پروژه‌های اینترنت اشیاء", "iot", Icons.Filled.Devices),
            Triple("کدها و Arduino IDE", "examples", Icons.Filled.Code),
            Triple("آزمونک تعاملی", "quiz", Icons.Filled.Quiz),
            Triple("راهنمای مسابقه", "competition", Icons.Filled.EmojiEvents)
        )
        menuItems.forEach { (title, route, icon) -> 
            Card(
                onClick={open(route)},
                shape=RoundedCornerShape(8.dp),
                colors=CardDefaults.cardColors(containerColor=Surface),
                modifier=Modifier.fillMaxWidth()
            ){ 
                Row(Modifier.padding(16.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(12.dp)){
                    Icon(icon, null, tint=Mint)
                    Text(title, color=TextMain, fontSize=17.sp, fontWeight=FontWeight.Medium, textAlign=TextAlign.Right, modifier=Modifier.weight(1f))
                    Icon(Icons.Filled.ChevronLeft, null, tint=Muted) // در حالت RTL این آیکون به درستی به سمت چپ اشاره می‌کند
                } 
            } 
        }
    } 
}

@Composable 
private fun InfoScreen(title:String, paragraphs:List<String>){ 
    Page(title){ 
        paragraphs.forEach { 
            Panel { 
                Body(it) 
            } 
        } 
    } 
}

@Composable 
private fun SpecsScreen(){ 
    Page("مشخصات فنی") { 
        val specs = listOf(
            "پردازنده" to "دو هسته‌ای (Dual-Core)، معمولاً تا فرکانس 240 مگاهرتز (بسته به مدل برد).",
            "حافظه" to "معمولاً دارای حافظه Flash بین 4 تا 16 مگابایت و حافظه RAM (SRAM) حدود 520 کیلوبایت.",
            "Wi‑Fi" to "پشتیبانی از استانداردهای 802.11 b/g/n در باند ۲.۴ گیگاهرتز.",
            "Bluetooth" to "پشتیبانی از Bluetooth Classic و BLE (نسخه 4.2) - بسته به مدل برد.",
            "GPIO" to "تعداد و قابلیت‌های پایه‌ها (مثل PWM، I2C، SPI) بسته به مدل برد متفاوت است.",
            "ADC" to "مبدل آنالوگ به دیجیتال (۱۲ بیت)؛ کانال‌ها و ویژگی‌ها بسته به مدل برد متفاوت است.",
            "ولتاژ منطقی" to "۳.۳ ولت (اعمال ولتاژ ۵ ولت به پایه‌ها باعث آسیب جدی می‌شود)."
        )
        specs.forEach { (a, b) -> 
            Panel { 
                Text(a, color=Mint, fontWeight=FontWeight.Bold, fontSize = 18.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                Body(b) 
            } 
        } 
    } 
}

@Composable 
private fun PinsScreen() { 
    Page("پین‌اوت و پایه‌های مهم") {
        Panel {
            Text("نمودار شماتیک پایه‌ها", color=Mint, fontWeight=FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            // کدهای متنی و نمودارها همیشه باید چپ‌چین (LTR) بمانند تا ساختارشان حفظ شود
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = """
                    +-------------------+
                    |      ESP32        |
                    |                   |
                    |  [3V3]    [GND]   |
                    |  [EN]     [GPIO23]|
                    |  [VP]     [GPIO22]|
                    |  [VN]     [TXD0]  |
                    |  [GPIO34] [RXD0]  |
                    |  [GPIO35] [GPIO21]|
                    |  [GPIO32] [GPIO19]|
                    |  [GPIO33] [GPIO18]|
                    |  [GPIO25] [GPIO5] |
                    |  [GPIO26] [GPIO17]|
                    |  [GPIO27] [GPIO16]|
                    |  [GPIO14] [GPIO4] |
                    |  [GPIO12] [GPIO0] |
                    |  [GND]    [GPIO2] |
                    |  [GPIO13] [GPIO15]|
                    |  [SD2]    [SD3]   |
                    |  [SD0]    [CMD]   |
                    |  [CLK]    [SD1]   |
                    +-------------------+
                    """,
                    color = Mint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                )
            }
        }
        val pinDetails = listOf(
            "GPIO 2" to "معمولاً به LED داخلی برد متصل است. برای تست‌های ساده عالی است.",
            "GPIO 34, 35, 36, 39" to "فقط ورودی (Input Only) هستند و مقاومت Pull-up داخلی ندارند.",
            "GPIO 6 تا 11" to "به حافظه Flash داخلی متصل هستند و نباید استفاده شوند.",
            "3V3" to "خروجی/تغذیه ۳.۳ ولت برای مدارهای سازگار. جریان خروجی آن محدود است.",
            "GND" to "مرجع مشترک زمین مدار. تمام مدارات باید زمین مشترک داشته باشند.",
            "VIN" to "ورودی تغذیه برد (معمولاً ۵ ولت)؛ بازه مجاز بسته به مدل برد متفاوت است.",
            "هشدار ایمنی" to "پایه‌های ESP32 منطق ۳.۳ ولتی دارند. هرگز به GPIO ولتاژ ۵ ولت اعمال نکنید؛ ممکن است تراشه بسوزد."
        )
        pinDetails.forEach { (pin, desc) ->
            Panel {
                Text(pin, color = Mint, fontWeight = FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                Body(desc)
            }
        }
    } 
}

@Composable 
private fun ExamplesScreen(open:(Int)->Unit){ 
    Page("مثال‌های برنامه‌نویسی") { 
        lessons.forEachIndexed { i, l -> 
            Card(
                onClick={open(i)},
                shape=RoundedCornerShape(8.dp),
                colors=CardDefaults.cardColors(containerColor=Surface),
                modifier=Modifier.fillMaxWidth()
            ){ 
                Row(Modifier.padding(16.dp), verticalAlignment=Alignment.CenterVertically){ 
                    Icon(Icons.Filled.Code, contentDescription = null, tint = Mint)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)){
                        Text(l.title, color=TextMain, fontWeight=FontWeight.Bold, fontSize=17.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                        Text(l.objective, color=Muted, fontSize=14.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                    }
                    Icon(Icons.Filled.ChevronLeft, null, tint=Mint)
                } 
            } 
        } 
    } 
}

@Composable 
private fun LessonScreen(lesson:Lesson){ 
    Page(lesson.title){ 
        Panel {
            Text("هدف", color=Mint, fontWeight=FontWeight.Bold, fontSize=18.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Body(lesson.objective)
        }
        
        Panel {
            Text("سیم‌کشی (مدار)", color=Mint, fontWeight=FontWeight.Bold, fontSize=18.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = """
          ESP32                  قطعه
        +-------+              +-----+
        |       |              |     |
        | GPIO  |--------------| +   |
        |       |              |     |
        | GND   |--------------| -   |
        +-------+              +-----+
                    """,
                    color = Mint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Body(lesson.wiring)
        }

        Panel {
            Text("کد Arduino C++", color=Mint, fontWeight=FontWeight.Bold, fontSize=18.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            // کدها ذاتاً انگلیسی هستند و باید چپ‌چین نمایش داده شوند
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                SelectionContainer { 
                    Text(
                        lesson.code,
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max=360.dp)
                            .verticalScroll(rememberScrollState())
                            .background(Color(0xFF0B1110), RoundedCornerShape(8.dp))
                            .padding(14.dp),
                        color=Color(0xFFC9F4E7),
                        fontSize=13.sp,
                        lineHeight=19.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Left // کدها باید چپ‌چین باشند
                    ) 
                }
            }
        }
        
        Panel {
            Text("توضیح اجرا", color=Mint, fontWeight=FontWeight.Bold, fontSize=18.sp, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Body(lesson.explanation) 
        }
    } 
}

private data class QuizQuestion(val prompt: String, val options: List<String>, val answer: Int, val feedback: String)
private val quizQuestions = listOf(
    QuizQuestion("ولتاژ منطقی پایه‌های ESP32 معمولاً چند ولت است؟", listOf("۵ ولت", "۳.۳ ولت", "۱۲ ولت"), 1, "GPIOهای ESP32 منطق ۳.۳ ولتی دارند؛ ورودی ۵ ولت می‌تواند به تراشه آسیب بزند."),
    QuizQuestion("در ورودی INPUT_PULLUP، وضعیت کلید فشرده معمولاً چیست؟", listOf("HIGH", "LOW", "مقاومت بی‌نهایت"), 1, "با اتصال کلید به GND، هنگام فشردن مقدار LOW خوانده می‌شود."),
    QuizQuestion("برای دسترسی به وب‌سرور محلی ESP32 چه چیزی لازم است؟", listOf("برد و گوشی در یک شبکه محلی", "اتصال کابل HDMI", "حذف کد setup"), 0, "آدرس IP برد را در مرورگر دستگاه متصل به همان شبکه باز کنید."),
    QuizQuestion("کدام پایه‌ها فقط به عنوان ورودی قابل استفاده هستند؟", listOf("GPIO 2", "GPIO 34", "GPIO 5"), 1, "پایه‌های 34، 35، 36 و 39 فقط ورودی هستند و مقاومت داخلی ندارند.")
)

@Composable 
private fun QuizScreen() {
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var finished by remember { mutableStateOf(false) }
    
    Page("آزمونک و خودارزیابی") {
        Panel { 
            Body("به هر پرسش پاسخ دهید. پس از مشاهده بازخورد، به پرسش بعد بروید؛ در پایان امتیاز شما نمایش داده می‌شود.") 
        }
        if (finished) {
            Panel {
                Text("نتیجه: $score از ${quizQuestions.size}", color = Mint, fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                Body(if (score == quizQuestions.size) "عالی! نکات ایمنی و مفاهیم پایه را خوب آموخته‌اید." else "درس‌های پایه و نکات ایمنی را مرور کنید و دوباره آزمون دهید.")
                Button(
                    onClick = { index = 0; score = 0; selected = -1; finished = false }, 
                    colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Bg),
                    modifier = Modifier.fillMaxWidth()
                ) { 
                    Text("شروع دوباره", textAlign=TextAlign.Center) 
                }
            }
        } else {
            val q = quizQuestions[index]
            Text("پرسش ${index + 1} از ${quizQuestions.size}", color = Mint, fontWeight = FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
            Panel {
                Text(q.prompt, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign=TextAlign.Right, modifier=Modifier.fillMaxWidth())
                q.options.forEachIndexed { i, option ->
                    val answerColor = if (selected == i && i == q.answer) Mint else if (selected == i) Color(0xFFFF8A80) else TextMain
                    OutlinedButton(
                        onClick = { 
                            if (selected == -1) { 
                                selected = i
                                if (i == q.answer) score++ 
                            } 
                        }, 
                        modifier = Modifier.fillMaxWidth(), 
                        enabled = selected == -1, 
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = answerColor)
                    ) { 
                        Text(option, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) 
                    }
                }
                if (selected != -1) {
                    Body((if (selected == q.answer) "پاسخ درست. " else "پاسخ درست نیست. ") + q.feedback)
                    Button(
                        onClick = { 
                            if (index == quizQuestions.lastIndex) finished = true else index++
                            selected = -1 
                        }, 
                        colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Bg),
                        modifier = Modifier.fillMaxWidth()
                    ) { 
                        Text(if (index == quizQuestions.lastIndex) "دیدن نتیجه" else "پرسش بعد", textAlign = TextAlign.Center) 
                    }
                }
            }
        }
    }
}
