# 🍕 Pizza Mania

**Pizza Mania** is an Android application built in **Java with Android Studio**, providing a smooth and modern pizza ordering experience.

---

## ✨ Features

* 🔐 **User Authentication** – Signup, Login, and secure session management
* 🍕 **Pizza Catalog** – Browse pizzas with images, descriptions, and prices
* 📄 **Pizza Details** – Choose size, adjust quantity, view total price
* 🛒 **Shopping Cart** – Add/remove pizzas, update quantity, see live totals
* 💳 **Checkout Flow** – Calculate order total and proceed to checkout
* 🎨 **Modern UI/UX** – Responsive layouts, custom buttons, clean design

---

## 🛠 Tech Stack

* **Language:** Java
* **Framework:** Android (AppCompat, Material Components)
* **Backend:** Springboot, NodeJs (Express)
* **UI:** XML Layouts, RecyclerView, ConstraintLayout
* **Storage:** SQLite (user + cart)
* **Session:** SharedPreferences / EncryptedSharedPreferences
* **Networking:** Retrofit (optional if backend APIs added later)

---

## 📂 Project Structure

```
pizza-mania/
│
├── app/src/main/java/com/pizzamania/
│   ├── activities/        # Activities (Main, Login, Signup, PizzaDetails, Cart)
│   ├── adapters/          # RecyclerView Adapters
│   ├── models/            # Data models (User, Pizza, CartItem)
│   ├── db/                # SQLite helper classes
│   ├── network/           # API client (Retrofit, if backend integrated)
│   └── session/           # Session Manager
│
├── app/src/main/res/
│   ├── layout/            # XML UI layouts
│   ├── drawable/          # Custom buttons, icons, shapes
│   ├── values/            # Strings, colors, styles
│   └── mipmap/            # App icons
│
└── README.md
```

---

## 🚀 Getting Started

### 1️⃣ Clone Repository

```bash
git clone https://github.com/chandisarandeni/pizza-mania.git
cd pizza-mania
```

### 2️⃣ Open in Android Studio

* Launch **Android Studio**
* Select **Open Project** → choose the `pizza-mania` folder

### 3️⃣ Run the App

* Connect your Android device or start an emulator
* Click ▶️ **Run**

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repo
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m "Add feature"`)
4. Push to your fork (`git push origin feature/my-feature`)
5. Open a Pull Request 🎉

