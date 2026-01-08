# 🏦 Banking System - Help Guide

## Overview
This is a multi-operator banking system project implementing 10 design patterns for the INF461 course at Université de Yaoundé I.

## 📁 Project Structure

```
backend/
├── src/main/java/com/bankingsystem/
│   ├── BankingSystemApplication.java    # Main application class
│   ├── model/                          # Entity classes (User, Account, Transaction)
│   ├── repository/                     # JPA repositories
│   ├── service/                        # Business logic services
│   ├── controller/                     # REST API controllers
│   ├── security/                       # JWT authentication and security
│   ├── adapters/                       # SMS provider adapters (Pattern Adapter)
│   ├── interfaces/                     # Core interfaces for patterns
│   └── dto/                           # Data Transfer Objects
├── src/main/resources/
│   ├── application.properties          # Production configuration
│   └── application-dev.properties      # Development configuration
└── src/test/                           # Unit and integration tests

frontend/
├── src/
│   ├── components/                      # React components
│   ├── services/                       # API service calls
│   └── contexts/                       # React contexts (Auth)
└── public/                             # Static assets

patterns/
├── objectif-[1-10]/                    # Documentation for each design pattern
└── README.md                           # Pattern implementations

docs/
├── cahier-des-charges.md              # Project requirements
├── conception.md                       # Design documentation
└── rapport-final.md                    # Final project report

diagrams/
└── *.puml                              # PlantUML diagrams
```

## 🚀 Quick Start

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- PostgreSQL database
- Node.js 14+ (for frontend)
- Git

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd projet
   ```

2. **Database Setup**
   ```sql
   CREATE DATABASE tpinf461;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE tpinf461 TO postgres;
   ```

3. **Run the application**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

   The API will be available at: `http://localhost:8080/api`

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the development server**
   ```bash
   npm start
   ```

   The frontend will be available at: `http://localhost:3000`

## 🔧 Available Commands

### Backend (Maven)
```bash
# Build the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package

# Run the application
mvn spring-boot:run

# Generate dependency tree
mvn dependency:tree

# Validate the project
mvn validate

# Check for updates
mvn versions:display-dependency-updates
```

### Frontend (npm)
```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Check for vulnerabilities
npm audit
```

## 📚 Design Patterns Implemented

1. **Strategy Pattern** - Multiple authentication methods
2. **Abstract Factory** - Operator families and object creation
3. **Builder Pattern** - Transaction construction
4. **Singleton** - Global services
5. **Adapter** - SMS service unification
6. **Template Method** - Common operator behaviors
7. **Composite** - Notification targets
8. **Decorator** - Optional account behaviors
9. **Template Method** - Workflow skeletons
10. **Visitor** - Analytical operations

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Refresh JWT token

### Accounts
- `GET /api/accounts` - Get user accounts
- `POST /api/accounts` - Create new account
- `GET /api/accounts/{id}` - Get account details
- `PUT /api/accounts/{id}` - Update account

### Transactions
- `GET /api/transactions` - Get transactions
- `POST /api/transactions` - Create transaction
- `GET /api/transactions/{id}` - Get transaction details

### System
- `GET /api/health` - Health check
- `GET /api/info` - System information

## 🗄️ Database Configuration

### PostgreSQL (Production)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tpinf461
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### H2 (Testing)
- Automatically configured for test scope
- In-memory database for unit tests

## 🔐 Security Configuration

### JWT Configuration
```properties
jwt.secret=mySecretKey
jwt.expiration=86400000  # 24 hours
```

### CORS Configuration
```properties
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

## 📱 SMS Providers

The system supports multiple SMS providers through the Adapter pattern:
- **Orange SMS** - Primary provider
- **MTN SMS** - Secondary provider

Providers can be easily added by implementing the `ISMSAdapter` interface.

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn test -Dtest=**/*IntegrationTest
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📊 Monitoring

### Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

### API Documentation
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🔧 Development Profiles

### Development Profile
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Production Profile
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure PostgreSQL is running
   - Check database credentials in `application.properties`
   - Verify database exists

2. **Port Conflicts**
   - Backend: Change `server.port` in properties
   - Frontend: Change `PORT` environment variable

3. **JWT Token Issues**
   - Check `jwt.secret` configuration
   - Verify token expiration settings

4. **Build Failures**
   - Run `mvn clean` to remove corrupted artifacts
   - Check Java version compatibility
   - Verify Maven dependencies

### Logs
Application logs are configured to output to console with the following format:
```
yyyy-MM-dd HH:mm:ss - [LEVEL] Message
```

## 📞 Support

For issues and questions:
1. Check the documentation in `/docs/`
2. Review pattern implementations in `/patterns/`
3. Examine test cases for usage examples
4. Check application logs for error details

## 🔄 Continuous Integration

The project includes configuration for:
- Maven build validation
- Unit test execution
- Integration testing with TestContainers
- Code coverage reporting

## 📝 Contributing

1. Follow the existing code style
2. Add unit tests for new features
3. Update documentation
4. Ensure all tests pass before committing
5. Follow the established design patterns

---

**Project:** Système Bancaire Multi-opérateurs  
**Course:** INF461 - Design Patterns  
**University:** Université de Yaoundé I  
**Year:** 2025/2026