<p align="center">
  <img src="docs/images/hero_banner.jpg" alt="OstaApp Android VPN Client" width="100%" />
</p>

<h1 align="center">🛡️ OstaApp (اوستا اپ)</h1>

<p align="center">
  <strong>کلاینت پیشرفته، هوشمند و مدرن اندروید برای اتصال به پنل‌های پروکسی، احراز هویت با ربات تلگرام و اسکنر اندپوینت‌های تمیز کلودفلر</strong>
</p>

<p align="center">
  <a href="https://github.com/l8PYGIT/ostavpnapp"><img src="https://img.shields.io/badge/Repository-l8PYGIT%2Fostavpnapp-00E5FF?style=for-the-badge&logo=github&logoColor=white" alt="GitHub Repo" /></a>
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Architecture-sing--box%20Compatible-F38020?style=for-the-badge" alt="sing-box" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License" />
</p>

---

## 📱 تصاویر واقعی از محیط برنامه (OstaApp Real Screenshots)

<p align="center">
  <img src="docs/images/screens_mockup.jpg" alt="OstaApp Real Screenshots" width="100%" />
</p>

---

## ⚙️ هسته برنامه (Core Architecture)

یکی از سوالات متداول در مورد معماری برنامه: **هسته OstaApp چیست؟ آیا sing-box است؟**

> **پاسخ کوتاه:** معماری OstaApp در لایه سیستم‌عامل از **Native Android `VpnService` با اینترفیس TUN بومی** و در لایه پروتکل از **فرمت‌های یکپارچه sing-box / Xray** (شامل VLESS Reality، VMess CDN، Trojan TLS و Shadowsocks) استفاده می‌کند.

### جزئیات فنی هسته:
1. **اینترفیس TUN بومی اندروید (`PanelVpnService`):**
   - پیاده‌سازی سبک، امن و بهینه بدون باینری‌های سنگین خارجی.
   - مدیریت مستقیم پکت‌های IP، مسیریابی IPv4، تنظیم دقیق MTU (1500) و دیسپچ دی‌ان‌اس اختصاصی (Cloudflare, Google, Shecan, AdGuard).
2. **پشتیبانی کامل از ساختار Outbound سینگ‌باکس (sing-box):**
   - تمامی لینک‌های سابسکریپشن مستقیماً بر اساس تعاریف `outbound` استاندارد sing-box پارس و مدیریت می‌شوند.
   - سازگار با کانفیگ‌های تولیدی پنل‌های مرزبان (Marzban)، سنایی (X-UI)، هیدیفای (Hiddify) و ربات‌های تلگرامی.
3. **موتور اسکنر TCP و جایگزینی SNI کلودفلر:**
   - تست واقعی بدون واسطه روی پورت‌های ۴۴۳ و ۸۴۴۳ برای پیدا کردن آی‌پی‌های بدون فیلتر در همراه اول و ایرانسل و جایگزینی آدرس مقصد در کانفیگ‌های CDN.

---

## 🌟 ویژگی‌های کلیدی (Key Features)

### ۱. 🤖 لاگین مستقیم و همگام‌سازی با ربات تلگرام (Telegram Bot Integration)
- **ورود سریع با دیپ‌لینک (۱ کلیک):** هدایت کاربر به ربات تلگرام (`tg://resolve?domain=bot&start=login_sessionId`).
- **بازگشت خودکار به برنامه:** باز شدن برنامه از طریق Deep Link اختصاصی (`panelvpn://auth?token=...&tg_id=...`).
- **ورود با کد تلگرام یا توکن اختصاصی:** تایید با وب‌سرویس و بارگذاری خودکار سابسکریپشن‌های فعال.
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
  <img src="docs/images/architecture_workflow.jpg" alt="OstaApp Architecture" width="100%" />
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
[ Android OstaApp Client ] ─────────────────────────────────────────────┘
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

کلاینت به راحتی می‌تواند به هر وب‌سرور یا اسکریپت ربات تلگرام متصل شود:

### ۱. تایید کد تلگرام (`POST /api/auth/telegram-code`)
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

### ۲. ورود مستقیم با Deep Link (`panelvpn://auth`)
ربات می‌تواند در پیام تایید، دکمه شیشه‌ای (Inline Button) قرار دهد:
```
panelvpn://auth?token=XYZ&tg_id=12345678&username=ali&name=Ali&balance=50000&sub_url=https://sub.myvpn.site/sub/ali
```

---

## 🛠️ نحوه بیلد و راه‌اندازی (Build Instructions)

```bash
# کلون ریپازیتوری
git clone https://github.com/l8PYGIT/ostavpnapp.git
cd ostavpnapp

# بیلد نسخه دیباگ (Debug APK)
gradle assembleDebug

# اجرای تست‌های واحد و Robolectric
gradle :app:testDebugUnitTest

# بیلد نسخه نهایی (Release APK)
gradle assembleRelease
```

---

## 📄 مجوز (License)

این پروژه به صورت متن‌باز تحت مجوز **MIT License** در آدرس [https://github.com/l8PYGIT/ostavpnapp](https://github.com/l8PYGIT/ostavpnapp) منتشر شده است.
```
MIT License

Copyright (c) 2026 OstaApp Contributors
https://github.com/l8PYGIT/ostavpnapp
```
