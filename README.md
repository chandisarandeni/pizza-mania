# Pizza Mania 🍕  

**Pizza Mania** is a full-stack pizza shop application that allows users to browse, order, and manage pizzas seamlessly. It features both mobile and backend services, using modern technologies and microservice architecture.  

---

## Features
- Browse pizza menu with images, prices, and descriptions
- Add pizzas to cart and place orders
- Track order status in real-time
- User authentication and profile management
- Admin panel for managing menu and orders
- Offline support with local SQLite database
- Push notifications for order updates (via Firebase)

---

## Technology Stack

**Frontend (Mobile App)**  
- Android Studio (Java/Kotlin)  
- SQLite for local offline storage  
- Firebase Authentication & Push Notifications  

**Backend & Microservices**  
- Spring Boot REST API for core services  
- NodeJS microservices for specific features  
- API Gateway built with Spring Boot for routing  
- Firebase for real-time updates and authentication  

**Database**  
- SQLite (mobile local storage)  
- Firebase Realtime Database / Firestore (cloud storage)  

---

## Architecture

![Architecture Diagram](https://github.com/user-attachments/assets/c2c03b29-b2c2-4b8e-909a-4ca11618f5b7)

---

## Repositories

| Service | GitHub Link |
|---------|------------|
| Pizza Mania Mobile App | https://github.com/chandisarandeni/pizza-mania |
| Pizza Mania Admin Panel | https://github.com/chandisarandeni/pizza-mania-admin |
| Pizza Mania Backend | https://github.com/chandisarandeni/pizza-mania-backend |
| Pizza Mania Notification Service | https://github.com/chandisarandeni/pizza-mania-notification-service |
| Pizza Mania API Gateway | https://github.com/chandisarandeni/pizza-mania-api-gateway |

---

## Setup & Installation

### Backend
1. Clone the backend repository:
   ```bash
   git clone https://github.com/chandisarandeni/pizza-mania-backend.git
   ```

2. Navigate to the project directory:

   ```bash
   cd pizza-mania-backend
   ```
3. Configure `application.properties` with your database and Firebase credentials.
4. Build and run the Spring Boot server:

   ```bash
   ./mvnw spring-boot:run
   ```

### Android App

1. Open the `pizza-mania` project in Android Studio.
2. Configure Firebase project by adding your `google-services.json`.
3. Run the app on an emulator or physical device.

---

## Usage

1. Launch the app on your Android device or emulator.
2. Sign up or log in with Firebase Authentication.
3. Browse the pizza menu, add items to your cart.
4. Place an order and track its status in real-time.
5. Admins can manage menu items and view all orders via backend API.
