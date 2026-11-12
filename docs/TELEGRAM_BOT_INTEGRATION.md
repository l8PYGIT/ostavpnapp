# 🤖 راهنمای اتصال ربات تلگرام به اپلیکیشن Panel VPN

این سند نحوه پیاده‌سازی بک‌اند و ربات تلگرام را برای احراز هویت کاربران و ارائه خودکار کانفیگ‌ها به کلاینت اندروید توضیح می‌دهد.

---

## ۱. چرخه لاگین (Authentication Flow)

اپلیکیشن Panel VPN از سه روش برای ورود کاربر و دریافت سرویس‌ها پشتیبانی می‌کند:

1. **روش دیپ‌لینک (Deep Link - پیشنهادی و ۱ کلیکه):**
   - اپلیکیشن یک `sessionId` تصادفی تولید کرده و تلگرام را با لینک زیر باز می‌کند:
     ```
     tg://resolve?domain=YOUR_BOT_USERNAME&start=login_auth_xxxxxxxx
     ```
   - کاربر روی دکمه **Start** در تلگرام کلیک می‌کند.
   - ربات هویت کاربر را تایید کرده و دکمه شیشه‌ای (Inline Button) برای بازگشت به اپلیکیشن نمایش می‌دهد یا بک‌اند به پولینگ اپلیکیشن پاسخ می‌دهد.

2. **روش بازگشت مستقیم (Direct App Callback via Deep Link):**
   - ربات تلگرام دکمه بازگشت به اپلیکیشن را با لینک زیر برای کاربر می‌فرستد:
     ```
     panelvpn://auth?token=USER_TOKEN&tg_id=USER_ID&username=USER_NAME&name=FIRST_NAME&balance=WALLET_BALANCE&sub_url=SUBSCRIPTION_URL
     ```
   - لمس این لینک توسط کاربر، فوراً اپلیکیشن Panel VPN را باز کرده، لاگین را تکمیل و کانفیگ‌ها را بارگذاری می‌کند.

3. **روش کد عددی (Verification Code):**
   - کاربر در ربات دستور `/login` یا «ورود به اپلیکیشن» را می‌زند.
   - ربات یک کد یکبار مصرف (مثلاً `839102`) به کاربر نمایش می‌دهد.
   - کاربر کد را در برنامه وارد کرده و برنامه با فراخوانی متد `POST /api/auth/telegram-code` احراز هویت را نهایی می‌کند.

---

## ۲. نمونه پیاده‌سازی ربات با پایتون (Python - Aiogram 3.x)

```python
import os
import aiohttp
from aiogram import Bot, Dispatcher, types
from aiogram.filters import CommandStart, Command
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton

BOT_TOKEN = "YOUR_TELEGRAM_BOT_TOKEN"
API_BASE_URL = "https://api.myvpnbot.com"

bot = Bot(token=BOT_TOKEN)
dp = Dispatcher()

# ذخیره موقت سشن‌ها در دیتابیس یا حافظه
sessions_db = {}

@dp.message(CommandStart())
async def handle_start(message: types.Message):
    args = message.text.split()[1] if len(message.text.split()) > 1 else None
    user_id = str(message.from_user.id)
    username = message.from_user.username or f"user_{user_id}"
    first_name = message.from_user.first_name or "کاربر"

    # بررسی دیپ‌لینک لاگین اپلیکیشن
    if args and args.startswith("login_"):
        session_id = args.replace("login_", "")
        
        # دریافت لینک سابسکریپشن اختصاصی کاربر از پنل (مثلاً مرزبان یا سنایی)
        user_sub_url = f"https://sub.myvpnbot.com/sub/{user_id}"
        wallet_balance = 50000

        # ذخیره وضعیت سشن برای پاسخ به Polling اپلیکیشن
        sessions_db[session_id] = {
            "status": "success",
            "token": f"token_{session_id}",
            "user": {
                "telegram_id": user_id,
                "username": username,
                "first_name": first_name,
                "wallet_balance": wallet_balance
            },
            "services": [
                {
                    "id": f"svc-{user_id}",
                    "name": "اشتراک اختصاصی ربات تلگرام",
                    "subscription_url": user_sub_url,
                    "total_bytes": 50 * 1024 * 1024 * 1024,
                    "used_bytes": 12 * 1024 * 1024 * 1024,
                    "expire_timestamp": 1740000000000,
                    "protocol": "VLESS Reality / Trojan",
                    "is_default": True
                }
            ]
        }

        # دکمه شیشه‌ای بازگشت مستقیم به اپلیکیشن
        deep_link = (
            f"panelvpn://auth?token=token_{session_id}"
            f"&tg_id={user_id}&username={username}&name={first_name}"
            f"&balance={wallet_balance}&sub_url={user_sub_url}"
        )
        keyboard = InlineKeyboardMarkup(inline_keyboard=[
            [InlineKeyboardButton(text="🚀 بازگشت و اتصال در اپلیکیشن", url=deep_link)]
        ])

        await message.answer(
            f"سلام {first_name} عزیز! ورود شما به اپلیکیشن Panel VPN تایید شد.\n"
            f"روی دکمه زیر کلیک کنید تا اشتراک شما مستقیماً در اپلیکیشن فعال شود:",
            reply_markup=keyboard
        )
    else:
        await message.answer(
            "به ربات فروش و مدیریت اشتراک‌های وی‌پی‌ان خوش آمدید!\n"
            "برای ورود در برنامه، گزینه لاگین با تلگرام را در برنامه بزنید."
        )

if __name__ == "__main__":
    import asyncio
    asyncio.run(dp.start_polling(bot))
```

---

## ۳. فرمت هدر مصرف حجم در لینک سابسکریپشن

اپلیکیشن Panel VPN هدر استاندارد `subscription-userinfo` را از سرور سابسکریپشن شما می‌خواند:
```http
HTTP/1.1 200 OK
Content-Type: text/plain; charset=utf-8
subscription-userinfo: upload=1073741824; download=15032385536; total=53687091200; expire=1740000000
```
- **upload:** حجم آپلود شده به بایت
- **download:** حجم دانلود شده به بایت
- **total:** کل حجم مجاز به بایت
- **expire:** زمان انقضا بر حسب ثانیه یونیکس (Unix Timestamp)

اپلیکیشن این هدر را مستقیماً پارس کرده و نمودار، درصد مصرف، و روزهای باقی‌مانده را بدون نیاز به لاگین مجدد به‌روزرسانی می‌کند.
