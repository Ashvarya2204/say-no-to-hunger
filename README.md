# 🍱 Say No To Hunger

> A smart food donation and distribution platform built to reduce food wastage and help feed people in need by connecting donors, NGOs, and volunteers through technology.

---

# 🌍 Problem Statement

Millions of tons of food are wasted every day from:
- Restaurants
- Hotels
- Cafeterias
- Events & Functions
- Households

At the same time, many people struggle to get even one proper meal.

The major problem is not food availability — it is the lack of an efficient system to connect surplus food with people who need it.

---

# 💡 Solution

**Say No To Hunger** is a web-based platform that creates a bridge between:
- Food Donors
- NGOs
- Volunteers
- Needy People

The system allows donors to upload extra food details, while NGOs and volunteers can quickly accept and distribute the food before it gets wasted.

---

# 🚀 Project Overview

This project is designed with the vision:

> “No food should be wasted while people are suffering from hunger.”

The platform focuses on:
- Reducing food wastage
- Helping needy communities
- Improving food distribution efficiency
- Enabling social contribution through technology

---

# ✨ Features

## 👨‍🍳 Donor Features
- Register & Login
- Donate surplus food
- Add food quantity and type
- Add pickup location
- Track donation requests

## 🏢 NGO Features
- View nearby food donations
- Accept donation requests
- Manage food distribution
- Update donation status

## 🚚 Volunteer Features
- Pickup and delivery coordination
- Real-time delivery updates
- Donation tracking

## 🔐 Security Features
- Secure authentication system
- Role-based access
- Input validation
- Protected APIs

## 📊 Dashboard & Analytics
- Total food donations
- Food saved from wastage
- Active donors & NGOs
- Number of people helped

---

# 🛠️ Tech Stack

## 💻 Frontend
- HTML5
- CSS3
- JavaScript
- Bootstrap

## ⚙️ Backend
- Spring Boot
- REST APIs
- Java

## 🗄️ Database
- PostgreSQL

## 🧰 Tools & Platforms
- VS Code
- Git & GitHub
- Postman

---

# 🏗️ System Architecture

```text id="gprhjz"
+------------------+
|      Donor       |
+------------------+
          |
          v
+---------------------------+
|  Spring Boot Application  |
+---------------------------+
          |
          v
+---------------------------+
|      PostgreSQL DB        |
+---------------------------+
          |
   -------------------
   |                 |
   v                 v
+------+       +------------+
| NGO  |       | Volunteers |
+------+       +------------+
```

---

# 📌 Modules

| Module | Description |
|--------|-------------|
| Authentication Module | Secure Login & Registration |
| Food Donation Module | Upload and manage donations |
| NGO Management Module | Accept and distribute food |
| Volunteer Module | Delivery coordination |
| Admin Dashboard | System monitoring |
| Analytics Module | Donation statistics |

---

# 🧠 Core Functionality

## Step 1
Food donors upload extra food details.

## Step 2
Nearby NGOs or volunteers receive donation information.

## Step 3
Volunteers coordinate pickup and delivery.

## Step 4
Food reaches needy people instead of being wasted.

---

# ⚙️ Installation & Setup

## 1️⃣ Clone Repository

```bash id="7gfdn5"
git clone https://github.com/your-username/say-no-to-hunger.git
```

---

## 2️⃣ Open Project

Open the project in **VS Code**

---

## 3️⃣ Configure PostgreSQL Database

Create a PostgreSQL database and update:

```properties id="n3yq7v"
application.properties
```

Example:

```properties id="z1h2k7"
spring.datasource.url=jdbc:postgresql://localhost:5432/say_no_to_hunger
spring.datasource.username=postgres
spring.datasource.password=your_password
```

---

## 4️⃣ Run Spring Boot Application

```bash id="8e1t0m"
mvn spring-boot:run
```

---

# 🔥 Future Enhancements

- 📍 Live GPS Tracking
- 📱 Mobile Application
- 🤖 AI-based food demand prediction
- 🔔 Real-time notifications
- ☁️ Cloud deployment
- 🌐 Multi-language support

---

# 🎯 Project Impact

This project helps:
- Reduce food wastage
- Support poor and homeless communities
- Improve NGO coordination
- Encourage social responsibility

---

# 📚 Learning Outcomes

Through this project, I learned:
- Spring Boot Development
- REST API Development
- PostgreSQL Integration
- Backend Architecture
- Authentication & Authorization
- Real-world Problem Solving

---

# 👨‍💻 Developer

## Aishwarya Patil
B.Tech CSE Student  
Java & Full Stack Developer

---

# 🌟 Why This Project Matters

> “Technology becomes meaningful when it serves humanity.”

**Say No To Hunger** is not just a software project —
it is an initiative to create social impact through technology.

---

# ⭐ Support The Project

If you like this project:
- Give it a ⭐ on GitHub
- Fork the repository
- Contribute ideas & improvements

---

# ❤️ Together We Can Fight Hunger
