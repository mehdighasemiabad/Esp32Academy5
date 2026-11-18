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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
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

class MainActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { AcademyApp() } } }

data class Lesson(val title: String, val objective: String, val wiring: String, val code: String, val explanation: String)
private val lessons = listOf(
    Lesson("چشمک‌زدن LED", "روشن و خاموش کردن یک LED با تأخیر زمانی.", "پایه مثبت LED را با مقاومت ۲۲۰ تا ۳۳۰ اهم به GPIO 2 و پایه منفی را به GND وصل کنید.", """const int LED_PIN = 2;

void setup() {
  pinMode(LED_PIN, OUTPUT);
}

void loop() {
  digitalWrite(LED_PIN, HIGH);
  delay(500);
  digitalWrite(LED_PIN, LOW);
  delay(500);
}""", "در setup پایه به‌عنوان خروجی تنظیم می‌شود. loop پیوسته وضعیت پایه را هر نیم‌ثانیه تغییر می‌دهد. در بعضی بردها LED داخلی روی GPIO 2 نیست؛ مشخصات برد را بررسی کنید."),
    Lesson("خواندن کلید", "خواندن وضعیت کلید با مقاومت بالاکش داخلی.", "یک سر کلید را به GPIO 4 و سر دیگر را به GND وصل کنید. INPUT_PULLUP در حالت آزاد HIGH و هنگام فشردن LOW می‌خواند.", """const int BUTTON_PIN = 4;

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
}""", "Serial Monitor را روی 115200 baud باز کنید. چون ورودی pull-up است، LOW به معنی فشرده‌شدن کلید است. برای حذف پرش مکانیکی می‌توان debounce اضافه کرد."),
    Lesson("خواندن سنسور آنالوگ", "نمونه‌برداری از ولتاژ خروجی سنسور با ADC.", "خروجی سنسور را به GPIO 34، زمین سنسور را به GND و تغذیه مناسب را وصل کنید. ولتاژ ورودی نباید از محدوده مجاز ADC تجاوز کند.", """const int SENSOR_PIN = 34;

void setup() {
  Serial.begin(115200);
}

void loop() {
  int value = analogRead(SENSOR_PIN);
  Serial.println(value);
  delay(500);
}""", "در ESP32 مقدار ADC معمولاً در بازه ۰ تا ۴۰۹۵ گزارش می‌شود، اما دقت و بازه ولتاژ به مدل و تنظیمات ADC وابسته است. GPIO 34 فقط ورودی است."),
    Lesson("اتصال به Wi-Fi", "اتصال ESP32 به شبکه بی‌سیم ۲٫۴ گیگاهرتز.", "سیم‌کشی لازم نیست. نام و گذرواژه شبکه خود را جایگزین مقادیر نمونه کنید.", """#include <WiFi.h>

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
}""", "برد به شبکه ۲٫۴ گیگاهرتز متصل می‌شود. پس از اتصال، IP در Serial Monitor چاپ می‌شود. اطلاعات واقعی شبکه را در کد عمومی منتشر نکنید."),
    Lesson("وب‌سرور ساده", "نمایش صفحه وب محلی و کنترل LED از طریق مرورگر.", "LED و مقاومت را میان GPIO 2 و GND ببندید. برد و گوشی/رایانه باید روی یک شبکه باشند.", """#include <WiFi.h>
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
}""", "پس از جایگزینی اطلاعات Wi-Fi، IP چاپ‌شده را در مرورگر دستگاهی در همان شبکه باز کنید. مسیرهای /on و /off وضعیت LED را عوض می‌کنند؛ handleClient درخواست‌ها را پردازش می‌کند."),
)

@Composable fun AcademyApp() {
    val nav = rememberNavController()
    val entry by nav.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: "home"
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = darkColorScheme(background=Bg, surface=Surface, primary=Mint, onBackground=TextMain, onSurface=TextMain)) {
            Scaffold(containerColor=Bg, bottomBar={ NavigationBar(containerColor=Surface) {
                val items=listOf(Triple("home","خانه",Icons.Filled.Home),Triple("examples","مثال‌ها",Icons.Filled.Code),Triple("about","درباره",Icons.Filled.Info))
                items.forEach { (r,label,icon) -> NavigationBarItem(selected=route==r, onClick={nav.navigate(r){launchSingleTop=true;popUpTo("home"){saveState=true};restoreState=true}}, icon={Icon(icon,null)}, label={Text(label)}, colors=NavigationBarItemDefaults.colors(selectedIconColor=Mint,selectedTextColor=Mint,indicatorColor=Bg)) }
            } }) { pad ->
                NavHost(navController=nav,startDestination="intro_splash",modifier=Modifier.padding(pad)) {
                    composable("intro_splash"){ SplashScreen { nav.navigate("home") { popUpTo("intro_splash") { inclusive=true } } } }
                    composable("home"){ HomeScreen { nav.navigate(it) } }
                    composable("intro"){ InfoScreen("معرفی ESP32", listOf("ESP32 یک خانواده از میکروکنترلرهای کم‌مصرف و قدرتمند است که برای پروژه‌های اینترنت اشیا و سامانه‌های تعبیه‌شده به‌کار می‌رود.","وجود Wi‑Fi و Bluetooth در بسیاری از مدل‌ها ارتباط بی‌سیم را بدون ماژول جداگانه ممکن می‌کند.","کاربردها: خانه هوشمند، خواندن حسگرها، کنترل از راه دور، رباتیک و پایش محیط.")) }
                    composable("specs"){ SpecsScreen() }
                    composable("pins"){ InfoScreen("پایه‌های مهم", listOf("GPIO: ورودی/خروجی دیجیتال و برخی قابلیت‌های ویژه؛ قابلیت هر پایه به مدل برد وابسته است.","3V3: خروجی/تغذیه ۳٫۳ ولت برای مدارهای سازگار.","GND: مرجع مشترک زمین مدار.","VIN: ورودی تغذیه برد؛ بازه مجاز بسته به مدل برد و طراحی آن متفاوت است.","هشدار: پایه‌های ESP32 منطق ۳٫۳ ولتی دارند. به GPIO ولتاژ ۵ ولت اعمال نکنید؛ ممکن است تراشه آسیب ببیند.")) }
                    composable("examples"){ ExamplesScreen { nav.navigate("lesson/$it") } }
                    composable("quiz"){ QuizScreen() }
                    composable("wireless"){ InfoScreen("Wi-Fi و Bluetooth", listOf("بسیاری از بردهای ESP32 از Wi-Fi باند ۲٫۴ گیگاهرتز پشتیبانی می‌کنند؛ مشخصات مدل دقیق خود را بررسی کنید.","کاربردها: اتصال به شبکه محلی، وب‌سرور داخلی، ارسال داده حسگر و کنترل از راه دور.","Bluetooth Classic و BLE در مدل‌های مختلف پشتیبانی متفاوتی دارند. برای مصرف کم انرژی و حسگرهای پوشیدنی معمولاً BLE مناسب است.","اطلاعات ورود شبکه را در کد عمومی یا مخزن منتشر نکنید؛ گذرواژه نمونه در این برنامه صرفاً جایگزین متنی است.")) }
                    composable("iot"){ InfoScreen("پروژه‌های اینترنت اشیاء", listOf("پایش دما و رطوبت: خواندن حسگر و نمایش داده در داشبورد محلی.","خانه هوشمند: کنترل بار کم‌ولتاژ از طریق وب‌سرور؛ برای برق شهر از رله ایزوله و نظارت متخصص استفاده کنید.","ایستگاه محیطی: اندازه‌گیری نور، دما یا کیفیت هوا و ثبت روندها.","چراغ وضعیت شبکه: نمایش اتصال و وضعیت سامانه با LED.","در هر پروژه هدف یادگیری، نمودار اتصال، فهرست قطعات، روش آزمون و ملاحظات ایمنی را ثبت کنید.")) }
                    composable("competition"){ InfoScreen("راهنمای مسابقه آفرینش هوشمند", listOf("این نرم‌افزار در قالب یک اپلیکیشن آموزشی تعاملی برای آموزش میکروکنترلر ESP32 و اینترنت اشیاء قرار می‌گیرد؛ این موضوع در محورهای پیشنهادی شیوه‌نامه، از جمله آموزش بردهای توسعه و طراحی ابزار آموزشی تعاملی، آمده است.","برای اثر آموزشی، هدف یادگیری روشن تعریف کنید؛ محتوا باید علمی، متناسب با سطح هنرستان و قابل استفاده در تدریس یا کارگاه باشد.","شیوه‌نامه استفاده از ابزارهای هوش مصنوعی در فرایند تولید را الزامی دانسته و شرح ابزارها و میزان استفاده را در مستندات لازم می‌داند. محتوای تولیدشده باید بازبینی و تأیید علمی شود و صرفاً خروجی خام هوش مصنوعی نباشد.","مستندات پیشنهادی تیم: نیاز آموزشی، مخاطب، اهداف، سناریوی تدریس، روش ارزیابی یادگیری، ابزارهای هوش مصنوعی و نقش آنها، اصلاحات انسانی، آزمون برنامه و منابع.","حقوق مؤلفان و منابع تصاویر، کدها و متن‌ها را رعایت کنید. در تیم دو نفره نقش هر عضو را شفاف بنویسید؛ هنرجو فقط در تیم مشترک با هنرآموز امکان شرکت دارد.","این راهنما جایگزین نسخه رسمی شیوه‌نامه نیست. ضوابط، مهلت‌ها و مدارک ارسال را در متن رسمی آخرین دوره بررسی کنید.")) }
                    composable("about"){ InfoScreen("درباره آکادمی", listOf("پدیدآورنده و نویسنده: دکتر مهدی قاسمی","هنرستان فنی طالقانی شازند - استان مرکزی","این برنامه یک راهنمای آفلاین فارسی برای آشنایی با ESP32 و تمرین مثال‌های پایه است.","نمونه‌ها برای Arduino Core for ESP32 نوشته شده‌اند. شماره پایه‌ها و امکانات واقعی را با مدل برد خود تطبیق دهید.")) }
                    composable("lesson/{id}",arguments=listOf(navArgument("id"){type=NavType.IntType})){ back -> val id=back.arguments?.getInt("id")?:0; LessonScreen(lessons.getOrElse(id){lessons[0]}) }
                }
            }
        }
    }
}

@Composable private fun SplashScreen(continueApp:()->Unit){
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),verticalArrangement=Arrangement.Center,horizontalAlignment=Alignment.CenterHorizontally){
        Text("ESP32",fontSize=46.sp,fontWeight=FontWeight.Bold,color=Mint)
        Text("آکادمی ESP32",fontSize=28.sp,fontWeight=FontWeight.Bold,color=TextMain,textAlign=TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Panel { Text("آموزش تعاملی میکروکنترلر و اینترنت اشیاء",color=Mint,fontWeight=FontWeight.Bold); Body("پدیدآورنده: دکتر مهدی قاسمی"); Body("هنرستان فنی طالقانی شازند - استان مرکزی") }
        Spacer(Modifier.height(22.dp))
        Button(onClick=continueApp,colors=ButtonDefaults.buttonColors(containerColor=Mint,contentColor=Bg)){ Text("ورود به آکادمی",fontWeight=FontWeight.Bold) }
    }
}
@Composable private fun Page(title:String, content:@Composable ColumnScope.()->Unit){ Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement=Arrangement.spacedBy(14.dp)){ Text(title,fontSize=26.sp,fontWeight=FontWeight.Bold,color=Mint); content() } }
@Composable private fun Panel(content:@Composable ColumnScope.()->Unit){ Column(Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(8.dp)).padding(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp), content=content) }
@Composable private fun Body(text:String){ Text(text,color=TextMain,fontSize=16.sp,lineHeight=26.sp,textAlign=TextAlign.Start) }
@Composable private fun HomeScreen(open:(String)->Unit){ Page("آکادمی ESP32") {
    Panel { Text("از آشنایی تا ساخت پروژه",color=Mint,fontSize=20.sp,fontWeight=FontWeight.Bold); Body("راهنمای فارسی و آفلاین برای شناخت برد ESP32 و شروع برنامه‌نویسی آن.") }
    listOf(Triple("معرفی ESP32","intro",Icons.Filled.Memory),Triple("مشخصات فنی و پین‌اوت","specs",Icons.Filled.Tune),Triple("Wi-Fi و Bluetooth","wireless",Icons.Filled.Wifi),Triple("پروژه‌های اینترنت اشیاء","iot",Icons.Filled.Devices),Triple("کدها و Arduino IDE","examples",Icons.Filled.Code),Triple("آزمونک تعاملی","quiz",Icons.Filled.Quiz),Triple("راهنمای مسابقه آفرینش هوشمند","competition",Icons.Filled.EmojiEvents),Triple("پایه‌های مهم","pins",Icons.Filled.DeveloperBoard)).forEach { (title,route,icon)-> Card(onClick={open(route)},shape=RoundedCornerShape(8.dp),colors=CardDefaults.cardColors(containerColor=Surface),modifier=Modifier.fillMaxWidth()){ Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(12.dp)){Icon(icon,null,tint=Mint);Text(title,color=TextMain,fontSize=17.sp,fontWeight=FontWeight.Medium);Spacer(Modifier.weight(1f));Icon(Icons.Filled.ChevronLeft,null,tint=Muted)} } }
} }
@Composable private fun InfoScreen(title:String, paragraphs:List<String>){ Page(title){ paragraphs.forEach { Panel { Body(it) } } } }
@Composable private fun SpecsScreen(){ Page("مشخصات فنی") { listOf("پردازنده" to "دو هسته‌ای، تا 240MHz (بسته به مدل برد)","Wi‑Fi" to "۲٫۴GHz (بسته به مدل برد)","Bluetooth" to "Bluetooth Classic و BLE (بسته به مدل برد)","GPIO" to "تعداد و قابلیت‌ها بسته به مدل برد", "ADC" to "مبدل آنالوگ به دیجیتال؛ کانال‌ها و ویژگی‌ها بسته به مدل برد", "PWM" to "خروجی PWM؛ قابلیت و تعداد کانال بسته به مدل برد", "SPI / I2C / UART" to "رابط‌های ارتباطی؛ تعداد و پایه‌های قابل استفاده بسته به مدل برد", "ولتاژ منطقی" to "۳٫۳V").forEach { (a,b)-> Panel { Text(a,color=Mint,fontWeight=FontWeight.Bold); Body(b) } } } }
@Composable private fun ExamplesScreen(open:(Int)->Unit){ Page("مثال‌های برنامه‌نویسی") { lessons.forEachIndexed { i,l -> Card(onClick={open(i)},shape=RoundedCornerShape(8.dp),colors=CardDefaults.cardColors(containerColor=Surface),modifier=Modifier.fillMaxWidth()){ Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically){ Column(Modifier.weight(1f)){Text(l.title,color=TextMain,fontWeight=FontWeight.Bold,fontSize=17.sp);Text(l.objective,color=Muted,fontSize=14.sp)};Icon(Icons.Filled.ChevronLeft,null,tint=Mint)} } } } }
@Composable private fun LessonScreen(lesson:Lesson){ Page(lesson.title){ Section("هدف",lesson.objective); Section("سیم‌کشی",lesson.wiring); Text("کد Arduino C++",color=Mint,fontWeight=FontWeight.Bold,fontSize=18.sp); SelectionContainer { Text(lesson.code,Modifier.fillMaxWidth().heightIn(max=360.dp).verticalScroll(rememberScrollState()).background(Color(0xFF0B1110),RoundedCornerShape(8.dp)).padding(14.dp),color=Color(0xFFC9F4E7),fontSize=13.sp,lineHeight=19.sp) }; Section("اجرای کد",lesson.explanation) } }
@Composable private fun Section(title:String,text:String){ Panel { Text(title,color=Mint,fontWeight=FontWeight.Bold,fontSize=18.sp);Body(text) } }


private data class QuizQuestion(val prompt: String, val options: List<String>, val answer: Int, val feedback: String)
private val quizQuestions = listOf(
    QuizQuestion("ولتاژ منطقی پایه‌های ESP32 معمولاً چند ولت است؟", listOf("۵ ولت", "۳٫۳ ولت", "۱۲ ولت"), 1, "GPIOهای ESP32 منطق ۳٫۳ ولتی دارند؛ ورودی ۵ ولت می‌تواند به تراشه آسیب بزند."),
    QuizQuestion("در ورودی INPUT_PULLUP، وضعیت کلید فشرده معمولاً چیست؟", listOf("HIGH", "LOW", "مقاومت بی‌نهایت"), 1, "با اتصال کلید به GND، هنگام فشردن مقدار LOW خوانده می‌شود."),
    QuizQuestion("برای دسترسی به وب‌سرور محلی ESP32 چه چیزی لازم است؟", listOf("برد و گوشی در یک شبکه محلی", "اتصال HDMI", "حذف کد setup"), 0, "آدرس IP برد را در مرورگر دستگاه متصل به همان شبکه باز کنید."),
    QuizQuestion("کدام نکته برای انتشار کد پروژه مهم است؟", listOf("قرار دادن گذرواژه واقعی Wi-Fi در کد عمومی", "حذف یا امن‌سازی اطلاعات محرمانه", "استفاده از ولتاژ ۵ ولت روی همه GPIOها"), 1, "اطلاعات شبکه را در مخزن عمومی قرار ندهید و از مقادیر نمونه استفاده کنید.")
)

@Composable private fun QuizScreen() {
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var finished by remember { mutableStateOf(false) }
    Page("آزمونک و خودارزیابی") {
        Panel { Body("به هر پرسش پاسخ دهید. پس از مشاهده بازخورد، به پرسش بعد بروید؛ در پایان امتیاز شما نمایش داده می‌شود.") }
        if (finished) {
            Panel {
                Text("نتیجه: $score از ${quizQuestions.size}", color = Mint, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Body(if (score == quizQuestions.size) "عالی! نکات ایمنی و مفاهیم پایه را خوب آموخته‌اید." else "درس‌های پایه و نکات ایمنی را مرور کنید و دوباره آزمون دهید.")
                Button(onClick = { index = 0; score = 0; selected = -1; finished = false }, colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Bg)) { Text("شروع دوباره") }
            }
        } else {
            val q = quizQuestions[index]
            Text("پرسش ${index + 1} از ${quizQuestions.size}", color = Mint, fontWeight = FontWeight.Bold)
            Panel {
                Text(q.prompt, color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                q.options.forEachIndexed { i, option ->
                    val answerColor = if (selected == i && i == q.answer) Mint else if (selected == i) Color(0xFFFF8A80) else TextMain
                    OutlinedButton(onClick = { if (selected == -1) { selected = i; if (i == q.answer) score++ } }, modifier = Modifier.fillMaxWidth(), enabled = selected == -1, colors = ButtonDefaults.outlinedButtonColors(contentColor = answerColor)) { Text(option) }
                }
                if (selected != -1) {
                    Body((if (selected == q.answer) "پاسخ درست. " else "پاسخ درست نیست. ") + q.feedback)
                    Button(onClick = { if (index == quizQuestions.lastIndex) finished = true else index++; selected = -1 }, colors = ButtonDefaults.buttonColors(containerColor = Mint, contentColor = Bg)) { Text(if (index == quizQuestions.lastIndex) "دیدن نتیجه" else "پرسش بعد") }
                }
            }
        }
    }
}
