# Devsu

> Dashboard par Excellence

> "Web application to manage clients, accounts, and bank transactions."


## 📌 Table of Contents

- [Description](#description)
- [Technologies](#technologies)
- [Installation](#installation)
- [Execution](#execution)
- [Project Structure](#project-structure)
- [Usage](#usage)


---

## 📝 Description

This project allows you to easily manage clients, accounts, and transactions. It includes:

- Registration, editing, and deletion of clients and accounts.

- Recording of debit and credit transactions.

- Generation and export of reports.

- Pagination and advanced search.

- Modal form for data registration and editing.

---

## 🛠️ Technologies

The project is built using:

- **Frontend:** Angular 21
- **Backend:** Spring Boot 3.5 & Java 21
- **Database:** MySQL (Docker)
- **Containers:** Docker / Docker Compose
- **State Management:** NgModel (Angular Forms)
- **Version Control:** Git

---

## ⚡ Installation

Clone the repository:

```
git clone https://github.com/Jeysson123/Devsu.git
```

Move to root folder (where docker-compose is located)

```
cd Devsu
```

## ▶️ Execution

Create images and run containers:

```
docker compose down && docker compose build --no-cache && docker compose up -d
```

App running on those ports

http://localhost:8082 (frontend)

http://localhost:8080 (backend)




## 🛠️ Project Structure

The project follows Clean Architecture, separating layers by responsibility for maintainability and scalability.

BACKEND

```text
com.devsu.backend
├── application           # Use cases: commands, queries, and bus
│   ├── bus
│   ├── command
│   └── query
├── domain                # Core business logic
│   ├── factory
│   └── service
├── infrastructure        # External systems, configs, persistence, security
│   ├── config
│   ├── export
│   ├── persistence
│   └── security
└── web                   # API layer: controllers, DTOs, advice, logging
    ├── advice
    ├── config
    ├── controller
    ├── dto
    └── logging
BackendApplication.java
```

FRONTEND

```
app
├── core               # Interceptors and services
│   ├── interceptors
│   └── service
└── presentation       # UI layer
    ├── components     # Reusable UI components
    ├── layouts        # Layouts for pages
    └── pages          # Views / pages

 ```   


 
## ⚙️ Usage

How to handle different operations.

### BACKEND

To test the API endpoints, please open **Postman** and import the collection and environment files located in:

`Devsu/documentation`

#### API Endpoints (Test Scenario)

Once the collection is imported, you will be able to manage the following resources:

| Resource | Method | Description |
| :--- | :--- | :--- |
| `/clientes` | GET/POST/UPDATE/DELETE | Client management |
| `/cuentas` | GET/POST/UPDATE/DELETE| Bank account management |
| `/movimientos` | GET/POST/UPDATE/DELETE | Transaction recording (Debit/Credit) |
| `/reportes` | GET/POST | Manage historic of transactions |

### FRONTEND

<img width="1366" height="768" alt="image" src="https://github.com/user-attachments/assets/3d9ce1d4-32e4-4154-b031-ce97673d0c75" />
