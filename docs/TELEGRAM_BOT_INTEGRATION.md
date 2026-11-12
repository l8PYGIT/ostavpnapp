# 🤖 راهنمای اتصال ربات تلگرام به عنوان تاییدکننده هویت (IdP) در OstaApp

این سند نحوه پیاده‌سازی ربات تلگرام را به عنوان **تاییدکننده هویت (Identity Provider - IdP)** و ارتباط آن با **سرور Backend API** و اپلیکیشن **OstaApp** توضیح می‌دهد.

> 💡 **نکته کلیدی معماری:** اپلیکیشن OstaApp مستقیماً با سرور API شما صحبت می‌کند و تمام اشتراک‌ها، حجم و کانفیگ‌ها را از دیتابیس API می‌خواند. ربات تلگرام به هیچ عنوان وظیفه ساخت کانفیگ یا ذخیره آن را ندارد و صرفاً هویت کاربر تلگرام را برای سشن باز شده در اپلیکیشن تایید (Authorize) می‌کند.

برای جزئیات دقیق تمامی اندپوینت‌ها و کدهای نمونه سرور، به [مستندات کامل API و احراز هویت سشن (docs/API_AND_SESSION_AUTH.md)](API_AND_SESSION_AUTH.md) مراجعه کنید.

---

## ۱. چرخه احراز هویت سشن (Session Authentication Lifecycle)

```
[ اپلیکیشن OstaApp ]
       │
       ▼ (1. ایجاد سشن و دریافت کد ۶ رقمی)
[ سرور Backend API ] ──► سشن در وضعیت PENDING
       │
       ▼ (2. هدایت کاربر با دیپ‌لینک tg://resolve?domain=bot&start=auth_sess_xxx)
[ ربات تلگرام ]
       │
       ▼ (3. نمایش دکمه «تایید ورود به OstaApp»)
[ کاربر تلگرام دکمه را لمس می‌کند ]
       │
       ▼ (4. ربات به API اطلاع می‌دهد: POST /api/auth/session/confirm)
[ سرور Backend API ] ──► سشن در وضعیت VERIFIED + تولید JWT Bearer Token
       │
       ▼ (5. اپلیکیشن با پولینگ توکن را دریافت کرده و اندپوینت /api/user/services را می‌خواند)
[ تمام اشتراک‌های کاربر در OstaApp بارگذاری می‌شوند ]
```

---

## ۲. روش‌های ورود کاربر در OstaApp

### ۱. ورود خودکار با دیپ‌لینک (Deep Link - ۱ کلیکه و پیشنهادی):
- اپلیکیشن سشن را در API ایجاد کرده و لینک زیر را اجرا می‌کند:
  ```
  tg://resolve?domain=YOUR_BOT_USERNAME&start=auth_sess_xxxxxxxxxxxx
  ```
- کاربر دکمه **Start** و سپس **«تایید ورود در اپلیکیشن»** را می‌زند.
- ربات با ارسال شناسه کاربر به سرور API سشن را تایید می‌کند.
- اپلیکیشن در کمتر از ۲ ثانیه متوجه تایید سشن شده و تمام اشتراک‌های کاربر را بارگذاری می‌کند.

### ۲. بازگشت مستقیم با دیپ‌لینک بومی (`osta://auth`):
- ربات پس از تایید هویت، دکمه شیشه‌ای حاوی لینک زیر را به کاربر نمایش می‌دهد:
  ```
  osta://auth?session_id=sess_xxx&tg_id=12345678&username=ali&name=Ali
  ```
- کلیک روی این لینک فوراً اپلیکیشن OstaApp را در گوشی باز کرده و فرآیند را تکمیل می‌کند (پشتیبانی سازگار با `panelvpn://auth` نیز محفوظ است).

### ۳. ورود با کد ۶ رقمی سشن (Verification Code):
- اپلیکیشن یک کد ۶ رقمی اختصاصی سشن (مثلاً `849201`) نمایش می‌دهد.
- کاربر می‌تواند با ارسال این کد به ربات، یا بالعکس وارد کردن کد دریافتی در اپلیکیشن، ورود را نهایی کند.

---

## ۳. نمونه پیاده‌سازی ربات تلگرام با پایتون (Python - Aiogram 3.x)

```python
import aiohttp
from aiogram import Bot, Dispatcher, types, F
from aiogram.filters import CommandStart
from aiogram.types import InlineKeyboardMarkup, InlineKeyboardButton

BOT_TOKEN = "YOUR_TELEGRAM_BOT_TOKEN"
API_SERVER_URL = "https://api.myvpnbot.com"  # آدرس سرور API شما

bot = Bot(token=BOT_TOKEN)
dp = Dispatcher()

@dp.message(CommandStart())
async def handle_start(message: types.Message):
    args = message.text.split()[1] if len(message.text.split()) > 1 else None
    user_id = str(message.from_user.id)
    username = message.from_user.username or ""
    first_name = message.from_user.first_name or "کاربر"

    # بررسی سشن ارسالی از OstaApp
    if args and args.startswith("auth_"):
        session_id = args.replace("auth_", "")
        
        keyboard = InlineKeyboardMarkup(inline_keyboard=[
            [InlineKeyboardButton(text="🛡️ تایید ورود به OstaApp", callback_data=f"confirm_{session_id}")],
            [InlineKeyboardButton(text="❌ لغو", callback_data="cancel_auth")]
        ])

        await message.answer(
            f"سلام {first_name} عزیز! 👋\n\n"
            f"درخواست ورود به اپلیکیشن <b>OstaApp</b> از دستگاه اندروید شما دریافت شد.\n\n"
            f"شناسه سشن: <code>{session_id[:10]}...</code>\n\n"
            f"آیا ورود به برنامه و دسترسی به اشتراک‌های خود را تایید می‌کنید؟",
            reply_markup=keyboard,
            parse_mode="HTML"
        )
    else:
        await message.answer(
            "به ربات احراز هویت OstaApp خوش آمدید!\n"
            "برای اتصال، لطفاً دکمه «ورود با تلگرام» را در داخل اپلیکیشن OstaApp لمس کنید."
        )

@dp.callback_query(F.data.startswith("confirm_"))
async def handle_confirm(callback: types.CallbackQuery):
    session_id = callback.data.replace("confirm_", "")
    user_id = str(callback.from_user.id)
    username = callback.from_user.username or ""
    first_name = callback.from_user.first_name or "کاربر"

    # ارسال تاییدیه هویت کاربر به سرور API مرکزی
    async with aiohttp.ClientSession() as session:
        payload = {
            "session_id": session_id,
            "telegram_id": user_id,
            "username": username,
            "first_name": first_name
        }
        try:
            async with session.post(f"{API_SERVER_URL}/api/auth/session/confirm", json=payload) as resp:
                if resp.status == 200:
                    deep_link = f"osta://auth?session_id={session_id}&tg_id={user_id}&username={username}"
                    keyboard = InlineKeyboardMarkup(inline_keyboard=[
                        [InlineKeyboardButton(text="🚀 بازگشت سریع به اپلیکیشن OstaApp", url=deep_link)]
                    ])
                    await callback.message.edit_text(
                        "✅ <b>هویت شما با موفقیت تایید شد!</b>\n\n"
                        "اپلیکیشن OstaApp اکنون در حال بارگذاری تمام اشتراک‌ها و کانفیگ‌های شما از سرور است.\n"
                        "می‌توانید به اپلیکیشن بازگردید.",
                        reply_markup=keyboard,
                        parse_mode="HTML"
                    )
                else:
                    await callback.message.edit_text("❌ خطا در برقراری ارتباط با سرور API یا منقضی شدن سشن.")
        except Exception as e:
            await callback.message.edit_text(f"❌ خطا در اتصال به API: {e}")
            
    await callback.answer()

@dp.callback_query(F.data == "cancel_auth")
async def handle_cancel(callback: types.CallbackQuery):
    await callback.message.edit_text("❌ درخواست ورود به اپلیکیشن لغو گردید.")
    await callback.answer()
```

---

## ۴. لینک مستندات تکمیلی

- [مستندات کامل API و احراز هویت سشن (docs/API_AND_SESSION_AUTH.md)](API_AND_SESSION_AUTH.md)
- [راهنمای جامع پروژه و معماری هسته OstaApp (README.md)](../README.md)
