<p align="center">
  <img src="docs/images/hero_banner.jpg" alt="Panel VPN Android Client" width="100%" />
</p>

<h1 align="center">🛡️ Panel VPN — Android Client with Telegram Bot Auth & Cloudflare Scanner</h1>

<p align="center">
  <strong>کلاینت پیشرفته، هوشمند و مدرن وی‌پی‌ان اندروید با اتصال به ربات تلگرام، اسکنر اندپوینت‌های تمیز کلودفلر و مدیریت اشتراک‌های پنل پروکسی</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-FF4081?style=for-the-badge" alt="Material 3" />
  <img src="https://img.shields.io/badge/Telegram-Bot%20API-24A1DE?style=for-the-badge&logo=telegram&logoColor=white" alt="Telegram" />
  <img src="https://img.shields.io/badge/Cloudflare-Clean%20IP-F38020?style=for-the-badge&logo=cloudflare&logoColor=white" alt="Cloudflare" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📱 نمایی از محیط برنامه (App Screenshots)

<p align="center">
  <img src="docs/images/screens_mockup.jpg" alt="App Preview Mockup" width="100%" />
</p>

---

## 🌟 ویژگی‌های کلیدی (Key Features)

### ۱. 🤖 لاگین مستقیم و همگام‌سازی با ربات تلگرام (Telegram Bot Integration)
- **ورود سریع با دیپ‌لینک (۱ کلیک):** هدایت کاربر به ربات اختصاصی تلگرام (`tg://resolve?domain=bot&start=login_sessionId`) و تایید اتوماتیک هویت.
- **بازگشت خودکار به برنامه:** پشتیبانی از Deep Link استاندارد اندروید (`panelvpn://auth?token=...&tg_id=...`).
- **ورود با کد تلگرام یا توکن اختصاصی:** تایید با وب‌سرویس بک‌اند و بارگذاری خودکار سابسکریپشن‌های خریداری شده.
- **نمایش اطلاعات حساب کاربر:** نمایش موجودی کیف‌پول، روزهای باقی‌مانده و حجم مصرف‌شده به صورت لحظه‌ای.

### ۲. ⚡ اسکنر اندپوینت‌های تمیز کلودفلر (Cloudflare Clean IP Scanner)
- **مخصوص دور زدن اختلالات شدید همراه اول، ایرانسل و مخابرات.**
- **اسکن ۱۰۰٪ واقعی سوکت TCP:** اجرای تست لایه انتقال بدون داده‌های شبیه‌سازی‌شده جهت اندازه‌گیری دقیق تاخیر RTT و پکت‌لاس.
- **رنج‌های گلچین‌شده اپراتورهای ایران:**
  - **همراه اول (MCI):** ساب‌نت‌های `104.16.x.x`، `172.64.x.x`، `162.158.x.x` و `172.67.x.x`
  - **ایرانسل (MTN):** ساب‌نت‌های کم‌تاخیر `188.114.96.x`، `104.22.x.x`، `104.24.x.x`
  - **مخابرات و ADSL:** ساب‌نت‌های `172.66.40.x`، `104.18.x.x`، `104.27.x.x`
  - **دامنه‌های تمیز:** `speed.cloudflare.com`، `cdnjs.com`، `pages.dev`
- **اعمال هوشمند روی کانفیگ‌های CDN:** جایگزینی آدرس اتصال (`host`) با حفظ دقیق هدرهای `Host` و `SNI` بدون ایجاد خطای SSL Handshake.
- **امکان وارد کردن دستی دامنه/IP تمیز دلخواه و تست زنده.**

### ۳. 🚀 اتصال هوشمند به سریع‌ترین سرور (Fastest Server Auto-Connect)
- تست پینگ سوکت TCP روی تمام نودها به صورت همزمان (Concurrent Coroutines).
- انتخاب و اتصال خودکار به سروری که کمترین تاخیر (Latency) را در شبکه فعلی دارد.

### ۴. 🛡️ سرویس بومی وی‌پی‌ان اندروید (Native Android VpnService)
- ساخت اینترفیس TUN استاندارد اندروید با MTU بهینه و مسیردهی هوشمند.
- اعلان سراسری (Foreground Notification) پایدار با دکمه قطع اتصال سریع.
- قابلیت **Bypass Iran** جهت عبور ترافیک سایت‌ها و سرویس‌های داخلی از خارج تونل.
- انتخاب ارائه‌دهنده DNS امن (Cloudflare 1.1.1.1، Google 8.8.8.8، Shecan، AdGuard و سفارشی).

### ۵. 🎨 طراحی مدرن Material 3 با پشتیبانی از Dark & Light Mode
- تم تاریک سایبرپانک شیک و تم روشن تمیز با تعویض آنی از نوار ابزار بالا.
- سازگاری کامل با جهت‌چین راست‌به‌چپ (RTL Persian First).
- نمایش نمودار زنده ترافیک آپلود/دانلود و لاگ‌های لحظه‌ای هسته شبکه.

---

## 🏗️ معماری و جریان کاری سیستم (Architecture & Workflow)

<p align="center">
  <img src="docs/images/architecture_workflow.jpg" alt="Technical Architecture and Workflow" width="100%" />
</p>

```
[ Telegram User ]
       │
       ▼ (1. Start Bot /login)
[ Telegram Bot (Your Server) ]
       │
       ▼ (2. Verify & Issue Auth Session)
[ Backend Panel API ] ◄────── (3. Fetch Subscriptions & Configs) ──────┐
       │                                                                │
       ▼ (4. Deep Link / Session Poll)                                  │
[ Android Panel VPN Client ] ──────────────────────────────────────────┘
       │
       ├─► [ Cloudflare Clean IP Scanner ] ── (Replaces CDN host IP with clean endpoint)
       │
       ▼ (5. Build TUN Interface & Start VPN)
[ Encrypted Tunnel (VLESS Reality / Trojan / VMess / Shadowsocks) ]
       │
       ▼
[ Global Secure Internet ]
```

---

## 📡 مشخصات API و تعامل با ربات تلگرام (Backend API Contract)

کلاینت به راحتی می‌تواند به هر وب‌سرور یا اسکریپت ربات تلگرام (نوشته‌شده با Python ،Node.js ،PHP یا Go) متصل شود.

### ۱. تایید کد تلگرام (`POST /api/auth/telegram-code`)
**درخواست:**
```json
{
  "code": "849201"
}
```

**پاسخ موفق:**
```json
{
  "status": "success",
  "token": "sess_8f92b7c109",
  "user": {
    "telegram_id": "98273612",
    "username": "user_parham",
    "first_name": "پرهام",
    "wallet_balance": 50000
  },
  "services": [
    {
      "id": "svc-01",
      "name": "اشتراک VIP دوماهه آلمان",
      "subscription_url": "https://sub.myvpn.site/sub/user_parham_token",
      "total_bytes": 53687091200,
      "used_bytes": 15032385536,
      "expire_timestamp": 1740000000000,
      "protocol": "VLESS Reality / Trojan",
      "is_default": true
    }
  ]
}
```

### ۲. بررسی وضعیت نشست دیپ‌لینک (`GET /api/auth/check-session?session_id=auth_xxxx`)
هنگامی که کاربر دکمه **«تایید در تلگرام»** را لمس می‌کند، اپلیکیشن وضعیت نشست را از سرور بررسی کرده و پس از فشردن دکمه استارت توسط کاربر در تلگرام، سابسکریپشن را دریافت می‌کند.

### ۳. ورود مستقیم با Deep Link (`panelvpn://auth`)
ربات می‌تواند در پیام تایید، دکمه شیشه‌ای (Inline Button) قرار دهد:
```
panelvpn://auth?token=XYZ&tg_id=12345678&username=ali&name=Ali&balance=50000&sub_url=https://sub.myvpn.site/sub/ali
```

---

## 📂 ساختار ماژول‌ها و کدهای پروژه (Project Structure)

```
com.example
├── MainActivity.kt                      # اکتیویتی اصلی با پشتیبانی از Deep Link
├── data
│   ├── model
│   │   └── Models.kt                    # مدل‌های داده (کاربر تلگرام، سرویس، سرورها، Clean IP)
│   ├── parser
│   │   └── ConfigParser.kt              # پارسر پروتکل‌های VLESS, VMess, Trojan, Shadowsocks
│   ├── repository
│   │   └── PanelRepository.kt           # مخزن داده، هماهنگی با ربات تلگرام و اسکنر کلودفلر
│   └── scanner
│       └── CloudflareScanner.kt         # موتور تست سوکت TCP واقعی برای ساب‌نت‌های کلودفلر
├── ui
│   ├── MainApp.kt                       # چارچوب اصلی ناوبری، تب‌بار و مدیریت BottomSheetها
│   ├── components
│   │   ├── CloudflareScannerBottomSheet.kt  # پنجره اسکن اندپوینت‌های کلودفلر با انیمیشن رادار
│   │   ├── ConnectPowerButton.kt            # دکمه مرکزی نئونی اتصال وی‌پی‌ان
│   │   ├── LiveSpeedMeter.kt                # گیج سرعت دانلود و آپلود لحظه‌ای
│   │   ├── PanelQuotaCard.kt                # کارت وضعیت حجم و روزهای اشتراک
│   │   ├── ServerSelectionBottomSheet.kt    # لیست سرورها همراه با پینگ و فیلترها
│   │   ├── TelegramLoginDialog.kt           # دیالوگ ورود هوشمند با ربات تلگرام
│   │   └── TrafficChart.kt                  # نمودار تاریخچه ترافیک مصرفی
│   ├── screens
│   │   ├── HomeScreen.kt                # داشبورد اصلی برنامه
│   │   ├── ServersScreen.kt             # صفحه سرورها، فیلتر CDN و بنر اسکنر
│   │   ├── PanelManagementScreen.kt     # صفحه مدیریت سرویس‌های تلگرام
│   │   ├── SettingsScreen.kt            # تنظیمات DNS، بایپس ایران و پروتکل
│   │   └── LogsScreen.kt                # کنسول لاگ‌های زنده شبکه
│   └── theme
│       ├── Color.kt                     # پالت‌های رنگی تم تاریک و تم روشن
│       ├── Theme.kt                     # تم مرکزی اپلیکیشن با پشتیبانی LocalAppColors
│       └── Type.kt                      # استایل‌های تایپوگرافی
└── vpn
    ├── PanelVpnService.kt               # سرویس اندرویدی ساخت تونل VPN (TUN)
    └── VpnController.kt                 # کنترل‌کننده وضعیت، تله‌متری ترافیک و اعلان سیستم
```

---

## 🛠️ پیش‌نیازها و نحوه بیلد (Build & Installation)

### پیش‌نیازها:
- **Android SDK:** حداقل نسخه ۲۴ (`minSdk = 24`، اندروید 7.0 به بالا)، نسخه هدف ۳۶ (`targetSdk = 36`).
- **JDK:** نسخه ۱۷ یا بالاتر.
- **Gradle:** بیلد خودکار با Kotlin DSL (`build.gradle.kts`).

### کامپایل و بیلد APK:
```bash
# بیلد نسخه دیباگ (Debug APK)
gradle assembleDebug

# اجرای تست‌های واحد و Robolectric
gradle :app:testDebugUnitTest

# بیلد نسخه نهایی (Release APK)
gradle assembleRelease
```
فایل نصبی تولید شده در مسیر زیر قرار می‌گیرد:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 مجوز (License)

این پروژه تحت مجوز **MIT License** منتشر شده است. استفاده، ویرایش و توسعه آزاد می‌باشد.
```
MIT License

Copyright (c) 2026 Panel VPN Contributors

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```
