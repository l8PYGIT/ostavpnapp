# ⚙️ بررسی ساختار فنی و هسته ارتباطی OstaApp

این سند ساختار هسته ارتباطی (Core Engine)، نحوه تونلینگ، پارس پروتکل‌ها و تطابق آن با استانداردهای **sing-box** و **Xray** را تشریح می‌کند.

---

## ۱. هسته فعلی پروژه چیست؟

در اپلیکیشن **OstaApp**، هسته ارتباطی بر پایه یک معماری ترکیبی دو لایه پیاده‌سازی شده است:

```
┌──────────────────────────────────────────────────────────┐
│                   OstaApp Android App                    │
│   (Jetpack Compose UI + MVVM + Telegram Auth + Scanner)  │
└────────────────────────────┬─────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────┐
│             لایه سیستم‌عامل (OS Tunneling Layer)           │
│         Native Android VpnService + TUN Interface        │
│          - مسیردهی ترافیک (0.0.0.0/0 Route)              │
│          - مدیریت MTU (1500) و اینترفیس شبکه بومی        │
│          - تفکیک ترافیک ایران (Bypass Iran)             │
│          - دیسپچ DNS ایمن (Cloudflare, Shecan, Google)   │
└────────────────────────────┬─────────────────────────────┘
                             │
                             ▼
┌──────────────────────────────────────────────────────────┐
│             لایه پروتکل و کانفیگ (Protocol Layer)         │
│          sing-box & Xray Outbound Unified Engine         │
│          - VLESS (Reality, TLS, WebSocket, gRPC)         │
│          - VMess (WebSocket CDN, TLS)                    │
│          - Trojan (TLS, gRPC, WebSocket)                 │
│          - Shadowsocks (AEAD Ciphers)                    │
└──────────────────────────────────────────────────────────┘
```

---

## ۲. مقایسه با sing-box و Xray-core

| ویژگی | هسته بومی OstaApp (معماری فعلی) | ادغام با Go Core (sing-box / libbox) |
| :--- | :--- | :--- |
| **حجم خروجی APK** | بسیار سبک (زیر ۱۲ مگابایت) | سنگین‌تر (به دلیل فایل‌های `.so` معماری‌های مختلف NDK) |
| **مصرف باتری و حافظه** | حداقل به دلیل عدم نیاز به اجرای ران‌تایم Go | متوسط به بالا |
| **فرمت و پروتکل‌ها** | پشتیبانی از لینک‌های استاندارد VLESS/VMess/Trojan | همان فرمت‌های استاندارد JSON |
| **سازگاری با پنل‌ها** | ۱۰۰٪ سازگار با مرزبان، سنایی و هیدیفای | ۱۰۰٪ سازگار |
| **اسکنر Clean IP** | تعبیه‌شده در لایه سوکت کلاینت | نیازمند تنظیم دستی اندپوینت در JSON |

---

## ۳. نگاشت مدل‌های داده به ساختار JSON سینگ‌باکس (sing-box Outbound Mapping)

هر شیء `ServerNode` در این برنامه به طور مستقیم با یک بلاک `outbound` در sing-box مطابقت دارد.

### نمونه کانفیگ VLESS Reality:
```json
{
  "type": "vless",
  "tag": "proxy-out",
  "server": "104.16.132.229", // آدرس IP تمیز کلودفلر بعد از اسکن
  "server_port": 443,
  "uuid": "f648b29c-5182-4ebc-8d19-35a09ce4a001",
  "tls": {
    "enabled": true,
    "server_name": "speedtest.net", // مقدار SNI اصلی برای کلودفلر
    "reality": {
      "enabled": true,
      "public_key": "...",
      "short_id": "..."
    }
  }
}
```

### نمونه کانفیگ Trojan / VMess WebSocket CDN:
```json
{
  "type": "trojan",
  "tag": "proxy-out",
  "server": "172.67.74.152", // آی‌پی تمیز اسکن‌شده
  "server_port": 443,
  "password": "strongpassword123",
  "tls": {
    "enabled": true,
    "server_name": "sub.mysite.com"
  },
  "transport": {
    "type": "ws",
    "path": "/websocket-path",
    "headers": {
      "Host": "sub.mysite.com"
    }
  }
}
```

---

## ۴. نحوه ادغام با `libbox` (در صورت نیاز به هسته کامل باینری sing-box)

در صورتی که در آینده مایل باشید باینری کامل Golang سینگ‌باکس (`io.nekohasekai.libbox`) را به پروژه متصل کنید:
1. پکیج `libbox.aar` از ریپازیتوری رسمی sing-box برای اندروید دریافت و در مسیر `app/libs/libbox.aar` قرار می‌گیرد.
2. کلاس `PanelVpnService` متد `Libbox.newStandaloneCommand()` یا `BoxService` را درون سرویس فعال می‌سازد.
3. در حال حاضر کدهای مدل و پارسر `OstaApp` از قبل با استانداردهای خروجی این فرمت تنظیم شده‌اند.
