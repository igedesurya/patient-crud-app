# Patient CRUD App

This is a full-stack web application to manage patient data, built as Task #2 for the Xtramile Solutions technical assessment.

## Tech Stack
- **Backend:** Java, Spring Boot, Spring JPA, Spring REST API
- **Frontend:** Angular
- **Build Tool:** Maven
- **Database:** PostgreSQL
- **Unit Tests:** JUnit and Spring Boot Test (for REST Controller & Service layer)

## Features
- ✅ List all patients in a paginated and sortable grid
- ✅ Create a new patient entry
- ✅ Update existing patient data
- ✅ Delete a patient record
- ✅ Search by:
  - PID
  - First Name
  - Last Name
- ✅ Server-side pagination

## Patient Fields
- PID (Patient Identity)
- First Name
- Last Name
- Date of Birth
- Gender
- Australian Address:
  - Street Address
  - Suburb
  - State
  - Postcode
- Phone Number

## How to Run

### 1. Configure PostgreSQL
Ensure PostgreSQL is running and a database is created (e.g. `patient`).  
Update your application configuration in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/patient
    username: your_db_username
    password: your_db_password

Build and run the backend:

./mvnw

or

mvn spring-boot:run

🌐 Frontend (Angular)

Go to the frontend project folder:
cd src/main/webapp

Install dependencies:
npm install

Start the Angular development server:
npm start

🔑 How to Use (Demo)

Open the application in your browser:

Frontend: http://localhost:4200

Login:

Click Account > Sign in

Use default credentials:

Username: admin

Password: admin

Navigate to Patient Menu:

After login, go to the Entities dropdown in the top navbar

Select Patient to manage patient records

🧪 Testing

Unit tests are included for:

REST API controllers

Service layer logic

To run tests:
./mvnw test

patient-crud-app/
│
├── src/
│   ├── main/
│   │   ├── java/                # Backend (Spring Boot)
│   │   ├── resources/           # Application configs
│   │   └── webapp/              # Frontend (Angular)
│   └── test/                    # Unit tests
│
├── pom.xml                      # Maven configuration
└── README.md
