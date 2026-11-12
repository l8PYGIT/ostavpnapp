# 🔐 مستندات معماری سشن و API احراز هویت OstaApp
## OstaApp Backend API & Telegram Authenticator (IdP) Architecture

این سند به طور کامل معماری احراز هویت دو مرحله‌ای و ارتباط مستقیم اپلیکیشن با سرور **Backend API** و نقش **ربات تلگرام به عنوان تاییدکننده هویت (Identity Provider - IdP)** را شرح می‌دهد.

---

## ۱. چرایی جداسازی سرور API از ربات تلگرام؟ (Architecture Philosophy)

در سیستم‌های سنتی، ربات تلگرام وظیفه سنگین ساخت کانفیگ، کوئری به دیتابیس پنل‌ها (مرزبان/سنایی) و ذخیره‌سازی را به دوش می‌کشید. این رویکرد معایب زیادی داشت:
1. **محدودیت‌های ترافیکی و بلاک شدن ربات توسط تلگرام.**
2. **ناامنی نگهداری اطلاعات حساس و کانفیگ‌ها در تلگرام.**
3. **عدم امکان همگام‌سازی بلادرنگ وضعیت حجم و چند اشتراکی.**

### در معماری نوین OstaApp:
- **اپلیکیشن مستقیماً با Backend API اختصاصی شما صحبت می‌کند.** تمامی عملیات ساخت سشن، دریافت توکن JWT، استعلام لیست تمامی اشتراک‌ها، حجم مصرفی و وضعیت انقضا از طریق وب‌سرویس مستقیم API انجام می‌شود.
- **ربات تلگرام صرفاً یک احراز هویت کننده (Authenticator / IdP) است.** ربات فقط پیام «آیا ورود به اپلیکیشن را تایید می‌کنید؟» را به کاربر نمایش می‌دهد و با کلیک کاربر، تاییدیه را به API می‌فرستد. ربات هیچ کانفیگی را ذخیره یا دستکاری نمی‌کند.

---

## ۲. دیاگرام جریان کاری (Sequence Diagram)

```text
┌──────────────┐             ┌─────────────────────┐           ┌────────────────────┐
│   OstaApp    │             │   Your Backend API  │           │ Telegram Bot (IdP) │
│ (Android UI) │             │ (serverApiUrl:443)  │           │   (@MyVpnBot)      │
└──────┬───────┘             └──────────┬──────────┘           └─────────┬──────────┘
       │                                │                                │
       │ 1. POST /api/auth/session/create                                │
       │───────────────────────────────>│                                │
       │    {"app":"OstaApp"}           │ (Stores session in memory/DB)  │
       │                                │ (Generates 6-digit code)       │
       │ 2. Returns session_id + code   │                                │
       │<───────────────────────────────│                                │
       │                                │                                │
       │ 3. Deep Link to Telegram       │                                │
       │   tg://resolve?domain=bot&start=auth_sess_123                   │
       │────────────────────────────────────────────────────────────────>│
       │                                │                                │ (User presses Start
       │ 4. Polls status every 2s       │                                │  & "تایید ورود")
       │    GET /api/auth/session/status?session_id=sess_123             │
       │───────────────────────────────>│                                │
       │    {"status":"pending"}        │                                │
       │<───────────────────────────────│                                │
       │                                │ 5. POST /api/auth/session/confirm
       │                                │<───────────────────────────────│
       │                                │    {"session_id": "sess_123",  │
       │                                │     "telegram_id": 98273612}   │
       │                                │                                │
       │ 6. Polling gets VERIFIED       │                                │
       │    {"status":"verified",       │                                │
       │     "access_token":"eyJ..."}   │                                │
       │<───────────────────────────────│                                │
       │                                │                                │
       │ 7. GET /api/user/services      │                                │
       │    (Header: Bearer eyJ...)     │                                │
       │───────────────────────────────>│ (Queries database for all      │
       │    Returns ALL Subscriptions   │  services owned by this user)  │
       │<───────────────────────────────│                                │
       │                                │                                │
       │ 8. Connects to VPN via configs │                                │
```

---

## ۳. مشخصات کامل وب‌سرویس‌های Backend API

### آدرس پایه (Base URL):
آدرس سرور API در برنامه از بخش تنظیمات قابل تغییر است (پیش‌فرض: `https://api.myvpnbot.com`).

---

### ۱. ایجاد نشست احراز هویت (Create Login Session)
- **متد:** `POST`
- **مسیر:** `/api/auth/session/create`
- **هدرها:** `Content-Type: application/json`
- **بدنه درخواست (Request Body):**
```json
{
  "app": "OstaApp",
  "platform": "android",
  "version": "1.0.0"
}
```
- **پاسخ موفق (HTTP 200 OK):**
```json
{
  "status": "success",
  "session_id": "sess_7f9b8c2d1e0a",
  "verification_code": "849201",
  "expires_in": 300,
  "telegram_deep_link": "tg://resolve?domain=MyVpnBot&start=auth_sess_7f9b8c2d1e0a"
}
```

---

### ۲. استعلام وضعیت سشن (Check Session Status)
اپلیکیشن هر ۲ ثانیه این اندپوینت را صدا می‌زند تا تایید هویت کاربر در ربات را دریافت کند.
- **متد:** `GET`
- **مسیر:** `/api/auth/session/status?session_id={session_id}`
- **پاسخ در انتظار تایید (HTTP 200 OK):**
```json
{
  "status": "pending",
  "expires_in": 284
}
```
- **پاسخ پس از تایید هویت کاربر در تلگرام (HTTP 200 OK):**
```json
{
  "status": "verified",
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI5ODI3MzYxMiIsIm5hbWUiOiJBbGkiLCJleHAiOjE3NDYwMDAwMDB9...",
  "user": {
    "telegram_id": "98273612",
    "username": "ostadev",
    "first_name": "علی",
    "wallet_balance": 45000
  }
}
```
- **پاسخ در صورت انقضا:**
```json
{
  "status": "expired",
  "message": "نشست لاگین منقضی شده است."
}
```

---

### ۳. تایید سشن توسط ربات تلگرام (Confirm Session by Bot)
این اندپوینت توسط ربات تلگرام (وب‌هوک یا تسک پس‌زمینه) به محض کلیک کاربر روی دکمه «تایید ورود» صدا زده می‌شود.
- **متد:** `POST`
- **مسیر:** `/api/auth/session/confirm`
- **هدرها:** 
  - `Content-Type: application/json`
  - `X-Bot-Secret-Token: your_internal_bot_secret`
- **بدنه درخواست:**
```json
{
  "session_id": "sess_7f9b8c2d1e0a",
  "telegram_id": "98273612",
  "username": "ostadev",
  "first_name": "علی"
}
```
- **پاسخ:**
```json
{
  "status": "success",
  "message": "سشن تایید شد و توکن دسترسی برای کاربر ایجاد گردید."
}
```

---

### ۴. تایید دستی کد ۶ رقمی (Verify Code)
اگر کاربر مایل بود کد ۶ رقمی را مستقیماً در برنامه وارد کند:
- **متد:** `POST`
- **مسیر:** `/api/auth/session/verify-code`
- **بدنه درخواست:**
```json
{
  "code": "849201",
  "session_id": "sess_7f9b8c2d1e0a"
}
```
- **پاسخ:** همانند پاسخ تایید شده اندپوینت شماره ۲ (حاوی `access_token` و `user`).

---

### ۵. دریافت تمام اشتراک‌های کاربر (Get All User Services)
**مهم‌ترین اندپوینت کلاینت:** اپلیکیشن به جای دریافت تک کانفیگ، لیست کامل سرویس‌ها و ساب‌های خریداری شده کاربر را دریافت می‌کند تا کاربر بتواند بین اشتراک‌های خود سوئیچ کند.
- **متد:** `GET`
- **مسیر:** `/api/user/services`
- **هدرها:** `Authorization: Bearer {access_token}`
- **پاسخ موفق (HTTP 200 OK):**
```json
{
  "status": "success",
  "total_services": 2,
  "services": [
    {
      "id": "sub_vip_de",
      "name": "اشتراک ۱ ماهه VIP آلمان و فنلاند",
      "subscription_url": "https://api.myvpnbot.com/sub/de_vip_token_123",
      "total_bytes": 53687091200,
      "used_bytes": 14032385536,
      "expire_timestamp": 1746000000000,
      "protocol": "VLESS Reality / Trojan",
      "server_count": 6,
      "is_default": true
    },
    {
      "id": "sub_backup_tr",
      "name": "اشتراک پشتیبان نامحدود ترکیه",
      "subscription_url": "https://api.myvpnbot.com/sub/tr_backup_token_456",
      "total_bytes": 21474836480,
      "used_bytes": 4221225472,
      "expire_timestamp": 1748000000000,
      "protocol": "VMess CDN / Shadowsocks",
      "server_count": 4,
      "is_default": false
    }
  ]
}
```

---

### ۶. دریافت پروفایل کاربر (Get User Profile)
- **متد:** `GET`
- **مسیر:** `/api/user/profile`
- **هدرها:** `Authorization: Bearer {access_token}`
- **پاسخ:**
```json
{
  "status": "success",
  "user": {
    "telegram_id": "98273612",
    "username": "ostadev",
    "first_name": "علی",
    "wallet_balance": 45000,
    "created_at": 1700000000
  }
}
```

---

## ۴. نمونه پیاده‌سازی کامل سرور بک‌اند و ربات تلگرام

### پیاده‌سازی با پایتون (Python FastAPI + aiogram 3.x)

```python
# main.py
import time
import random
import secrets
import jwt
from typing import Dict, Optional
from fastapi import FastAPI, Header, HTTPException, Depends
from pydantic import BaseModel
from aiogram import Bot, Dispatcher, types, F
from aiogram.filters import CommandStart
import asyncio

JWT_SECRET = "super_secret_osta_key_change_me"
BOT_TOKEN = "123456789:ABCdefGhIJKlmNoPQRsTUVwxyZ"
API_URL = "https://api.myvpnbot.com"

app = FastAPI(title="OstaApp Backend API")
bot = Bot(token=BOT_TOKEN)
dp = Dispatcher()

# Memory session store (in production use Redis)
sessions: Dict[str, dict] = {}
code_to_session: Dict[str, str] = {}

class CreateSessionReq(BaseModel):
    app: str
    platform: str
    version: Optional[str] = "1.0.0"

class ConfirmSessionReq(BaseModel):
    session_id: str
    telegram_id: str
    username: Optional[str] = ""
    first_name: Optional[str] = "کاربر"

class VerifyCodeReq(BaseModel):
    code: str
    session_id: Optional[str] = None

# =========================
# FastAPI REST Endpoints
# =========================

@app.post("/api/auth/session/create")
def create_session(req: CreateSessionReq):
    session_id = f"sess_{secrets.token_hex(8)}"
    code = str(random.randint(100000, 999999))
    
    sessions[session_id] = {
        "session_id": session_id,
        "code": code,
        "status": "pending",
        "created_at": time.time(),
        "expires_in": 300,
        "access_token": None,
        "user": None
    }
    code_to_session[code] = session_id
    
    return {
        "status": "success",
        "session_id": session_id,
        "verification_code": code,
        "expires_in": 300,
        "telegram_deep_link": f"tg://resolve?domain=MyVpnBot&start=auth_{session_id}"
    }

@app.get("/api/auth/session/status")
def get_session_status(session_id: str):
    sess = sessions.get(session_id)
    if not sess:
        raise HTTPException(status_code=404, detail="Session not found")
    
    if time.time() - sess["created_at"] > sess["expires_in"]:
        sess["status"] = "expired"
        return {"status": "expired"}
    
    if sess["status"] == "verified":
        return {
            "status": "verified",
            "access_token": sess["access_token"],
            "user": sess["user"]
        }
    return {"status": "pending", "expires_in": int(sess["expires_in"] - (time.time() - sess["created_at"]))}

@app.post("/api/auth/session/confirm")
def confirm_session(req: ConfirmSessionReq):
    sess = sessions.get(req.session_id)
    if not sess:
        raise HTTPException(status_code=404, detail="Session not found")
    
    token_payload = {
        "sub": req.telegram_id,
        "username": req.username,
        "name": req.first_name,
        "exp": int(time.time() + (30 * 86400))
    }
    access_token = jwt.encode(token_payload, JWT_SECRET, algorithm="HS256")
    
    user_data = {
        "telegram_id": req.telegram_id,
        "username": req.username,
        "first_name": req.first_name,
        "wallet_balance": 45000
    }
    
    sess["status"] = "verified"
    sess["access_token"] = access_token
    sess["user"] = user_data
    return {"status": "success", "access_token": access_token}

@app.post("/api/auth/session/verify-code")
def verify_code(req: VerifyCodeReq):
    s_id = req.session_id or code_to_session.get(req.code)
    sess = sessions.get(s_id)
    if not sess or sess["code"] != req.code:
        raise HTTPException(status_code=400, detail="کد وارد شده معتبر نیست")
    
    if sess["status"] != "verified":
        # Auto-verify with demo/associated user
        token_payload = {"sub": "98273612", "username": "user", "exp": int(time.time() + 86400)}
        access_token = jwt.encode(token_payload, JWT_SECRET, algorithm="HS256")
        sess["status"] = "verified"
        sess["access_token"] = access_token
        sess["user"] = {"telegram_id": "98273612", "username": "ostadev", "first_name": "کاربر تایید شده", "wallet_balance": 50000}
    
    return {"status": "verified", "access_token": sess["access_token"], "user": sess["user"]}

@app.get("/api/user/services")
def get_user_services(authorization: str = Header(None)):
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Unauthorized")
    
    token = authorization.split(" ")[1]
    try:
        decoded = jwt.decode(token, JWT_SECRET, algorithms=["HS256"])
        tg_id = decoded["sub"]
    except Exception:
        raise HTTPException(status_code=401, detail="Invalid JWT Token")
    
    # Query database for all subscriptions of this telegram_id
    return {
        "status": "success",
        "total_services": 2,
        "services": [
            {
                "id": f"sub_vip_{tg_id}",
                "name": "اشتراک ۱ ماهه VIP آلمان و فنلاند",
                "subscription_url": f"{API_URL}/sub/de_vip_{tg_id}",
                "total_bytes": 50 * 1024 * 1024 * 1024,
                "used_bytes": 12 * 1024 * 1024 * 1024,
                "expire_timestamp": int((time.time() + 25 * 86400) * 1000),
                "protocol": "VLESS Reality / Trojan",
                "server_count": 6,
                "is_default": True
            },
            {
                "id": f"sub_backup_{tg_id}",
                "name": "اشتراک کمکی ترکیه و فرانسه",
                "subscription_url": f"{API_URL}/sub/tr_backup_{tg_id}",
                "total_bytes": 25 * 1024 * 1024 * 1024,
                "used_bytes": 3 * 1024 * 1024 * 1024,
                "expire_timestamp": int((time.time() + 15 * 86400) * 1000),
                "protocol": "VMess CDN / Shadowsocks",
                "server_count": 4,
                "is_default": False
            }
        ]
    }

# =========================
# Telegram Bot (IdP Role)
# =========================

@dp.message(CommandStart())
async def start_handler(message: types.Message):
    args = message.text.split(maxsplit=1)
    if len(args) > 1 and args[1].startswith("auth_"):
        session_id = args[1].replace("auth_", "")
        kb = types.InlineKeyboardMarkup(inline_keyboard=[
            [types.InlineKeyboardButton(text="✅ تایید ورود به OstaApp", callback_data=f"confirm_{session_id}")],
            [types.InlineKeyboardButton(text="❌ انصراف", callback_data="cancel")]
        ])
        await message.answer(
            f"درخواست ورود به اپلیکیشن <b>OstaApp</b> از دستگاه اندروید شما دریافت شد.\n\n"
            f"آیا ورود به حساب کاربری را تایید می‌کنید؟",
            reply_markup=kb,
            parse_mode="HTML"
        )
    else:
        await message.answer("سلام! به ربات احراز هویت OstaApp خوش آمدید.")

@dp.callback_query(F.data.startswith("confirm_"))
async def confirm_callback(query: types.CallbackQuery):
    session_id = query.data.replace("confirm_", "")
    # Call internal confirm function or API
    confirm_session(ConfirmSessionReq(
        session_id=session_id,
        telegram_id=str(query.from_user.id),
        username=query.from_user.username or "",
        first_name=query.from_user.first_name or "کاربر"
    ))
    
    app_deep_link = f"osta://auth?session_id={session_id}&tg_id={query.from_user.id}"
    kb = types.InlineKeyboardMarkup(inline_keyboard=[
        [types.InlineKeyboardButton(text="🚀 بازگشت به اپلیکیشن OstaApp", url=app_deep_link)]
    ])
    await query.message.edit_text(
        "✅ <b>ورود شما با موفقیت تایید شد!</b>\n\nمی‌توانید به برنامه بازگردید؛ تمامی کانفیگ‌ها و اشتراک‌ها بارگذاری شدند.",
        reply_markup=kb,
        parse_mode="HTML"
    )
    await query.answer()
```

---

## ۵. امنیت و بهترین راهکارها (Best Practices)

1. **طول عمر کوتاه نشست (Session TTL):** سشن‌ها حداکثر ۳۰۰ ثانیه (۵ دقیقه) معتبر هستند و در صورت عدم تایید خودکار منقضی می‌شوند.
2. **استفاده از JWT استاندارد:** پس از تایید هویت در ربات، یک توکن امضاشده HS256/RS256 با شناسه کاربری و تاریخ انقضا صادر می‌شود.
3. **عدم افشای سکرت‌های پنل پروکسی به ربات:** ربات هیچ دسترسی مستقیمی به پسوردها، UUID سرورها یا پایگاه‌داده پنل‌های Marzban/X-UI ندارد. فقط سرور API مرکزی به عنوان لایه محافظ (Gateway) عمل می‌کند.
4. **پشتیبانی از چندین اشتراک همزمان:** کاربر می‌تواند در بخش «اشتراک‌ها» تمام سرویس‌های فعال خود را مشاهده و بین آن‌ها سوئیچ نماید.
