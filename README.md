# 🚗 Car Management Service

A comprehensive microservice for managing electric vehicle fleets with real-time GPS tracking, battery monitoring, and event logging capabilities.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Configuration](#-configuration)
- [Virtual Car Simulator](#-virtual-car-simulator)
- [Database Schema](#-database-schema)
- [Project Structure](#-project-structure)
- [Contributing](#-contributing)

---

## ✨ Features

### Core Functionality
- ✅ **Complete Vehicle CRUD Operations** - Create, read, update, delete vehicles with validation
- ✅ **Real-time GPS Tracking** - Track vehicle location, speed, and movement
- ✅ **Battery Monitoring** - Monitor battery levels and charging status
- ✅ **Event Logging (Option B)** - Comprehensive audit trail for all vehicle operations
- ✅ **Fleet Hub Management** - Organize vehicles by hub locations
- ✅ **Vehicle State Management** - Real-time state updates with data source tracking

### Advanced Features
- 🔄 **Virtual Car Simulator** - Automated simulation of vehicle movement and status changes
- 📊 **Pagination & Filtering** - Efficient data retrieval with sorting and filtering
- 🔍 **Message Sequence Validation** - Prevent out-of-order updates from distributed sources
- 📝 **Comprehensive API Documentation** - Interactive Swagger UI
- ⚡ **RESTful API Design** - Clean, consistent API endpoints

### Integration Ready
- 🐰 **RabbitMQ Support** (architecture ready for future integration)
- 🔗 **Microservice Integration Points** - Booking Service, Driver Service
- 📡 **IoT Device Support** - Ready for real vehicle data ingestion

---

## 🏗 Architecture

### Layered Architecture (Option B - Event-Driven)

```
┌─────────────────────────────────────────────────────┐
│              REST API Layer                        │
│  VehicleController | VehicleStateController        │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│              Service Layer                         │
│  VehicleService | VehicleStateService              │
│  VehicleEventLogService (Audit Trail)              │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│              Repository Layer                      │
│  Spring Data JPA Repositories                     │
└─────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────┐
│              PostgreSQL Database                   │
│  5 Tables: vehicle, vehicle_state,                │
│  vehicle_event_log, vehicle_model, fleet_hub      │
└─────────────────────────────────────────────────────┘
```

### Key Design Decisions
- **Option B Event Logging**: All vehicle operations are logged with before/after values
- **Message Sequencing**: Ensures data consistency in distributed environments
- **Data Source Tracking**: Distinguishes between manual, simulated, and IoT data
- **Flexible State Management**: Support for future integration with real vehicles

---

## 🛠 Tech Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.4.2** - Framework
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM
- **Spring Validation** - Request validation
- **Lombok** - Reduce boilerplate code

### Database
- **PostgreSQL 16** - Primary database
- **HikariCP** - Connection pooling

### Documentation & Tools
- **SpringDoc OpenAPI 2.7.0** - API documentation
- **Swagger UI** - Interactive API explorer
- **Maven** - Build tool

### Future Integrations (Architecture Ready)
- **RabbitMQ** - Message broker for real-time updates
- **Spring Cloud** - Microservice communication

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **PostgreSQL 16** or higher
- **Maven 3.9+**
- **Git**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/laptruong-hub/Car-Management-System-CRS.git
   cd car-management
   ```

2. **Set up PostgreSQL database**
   ```sql
   CREATE DATABASE car_management_db;
   CREATE USER car_user WITH PASSWORD 'car_password';
   GRANT ALL PRIVILEGES ON DATABASE car_management_db TO car_user;
   ```

3. **Configure application**
   
   Edit `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/car_management_db
       username: car_user
       password: car_password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the application**
   - **API Base URL**: `http://localhost:8080/api/v1`
   - **Swagger UI**: `http://localhost:8080/swagger-ui.html`
   - **API Docs**: `http://localhost:8080/api-docs`

---

## 📚 API Documentation

### Vehicle Management

#### **Create Vehicle**
```http
POST /api/v1/vehicles
Content-Type: application/json

{
  "plateNumber": "29A-12345",
  "vin": "VIN1234567890ABCDE",
  "modelId": 1,
  "color": "White",
  "manufactureYear": 2024,
  "status": "AVAILABLE",
  "odometerKm": 0.0,
  "fleetHubId": 1,
  "isVirtual": true
}
```

#### **Get All Vehicles (with pagination)**
```http
GET /api/v1/vehicles?page=0&size=10&sort=createdAt,desc
```

#### **Get Vehicle by ID**
```http
GET /api/v1/vehicles/{id}
```

#### **Update Vehicle**
```http
PUT /api/v1/vehicles/{id}
Content-Type: application/json

{
  "color": "Black",
  "status": "IN_USE",
  "currentBookingId": "BK-12345"
}
```

#### **Delete Vehicle**
```http
DELETE /api/v1/vehicles/{id}
```

### GPS Tracking & State Management

#### **Get Vehicle State**
```http
GET /api/v1/vehicles/{id}/state
```

#### **Update Vehicle State**
```http
PUT /api/v1/vehicles/{id}/state
Content-Type: application/json

{
  "latitude": 10.762622,
  "longitude": 106.660172,
  "batteryLevel": 85,
  "speedKmh": 45.5,
  "isCharging": false,
  "odometerKm": 1255.8
}
```

#### **Quick GPS Update**
```http
PUT /api/v1/vehicles/{id}/state/location?latitude=10.780000&longitude=106.700000
```

#### **Quick Battery Update**
```http
PUT /api/v1/vehicles/{id}/state/battery?batteryLevel=50&isCharging=true
```

### Simulator Control

#### **Get Simulator Config**
```http
GET /api/v1/simulator/config
```

#### **Enable/Disable Simulator**
```http
POST /api/v1/simulator/enable
POST /api/v1/simulator/disable
```

#### **Set Vehicles to Simulate**
```http
PUT /api/v1/simulator/vehicles
Content-Type: application/json

[1, 2, 3]
```

---

## ⚙ Configuration

### Database Configuration

**application.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/car_management_db
    username: car_user
    password: car_password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update  # Use 'validate' in production
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### Simulator Configuration

```yaml
simulator:
  enabled: true                     # Enable/disable simulator
  update-interval-ms: 5000          # Update every 5 seconds
  status-change-interval-ms: 30000  # Status changes every 30 seconds
  vehicle-ids: [1, 2]              # Specific vehicles to simulate (empty = all)
```

### Server Configuration

```yaml
server:
  port: 8080
  servlet:
    context-path: /

springdoc:
  paths:
    api-docs: /api-docs
    swagger-ui: /swagger-ui.html
```

---

## 🤖 Virtual Car Simulator

The simulator automatically generates realistic vehicle behavior for testing purposes.

### Features
- **GPS Movement**: Random walk simulation around Ho Chi Minh City
- **Battery Drain**: Realistic battery consumption based on speed
- **Charging Simulation**: Battery increases when vehicle is charging
- **Status Changes**: Random status transitions (available → in use → charging)
- **Configurable**: Control via YAML or REST API

### Simulation Behaviors

| Vehicle Status | Behavior |
|---------------|----------|
| **IN_USE** | Moving (GPS changes), speed 20-80 km/h, battery drains |
| **CHARGING** | Stationary, speed 0, battery increases by 2%/update |
| **AVAILABLE** | Parked, speed 0, minimal battery drain (standby) |

### Control Simulator

**Via Configuration File** (permanent):
```yaml
simulator:
  enabled: true
  vehicle-ids: [1]  # Simulate only vehicle 1
```

**Via API** (runtime):
```bash
# Set specific vehicles
curl -X PUT http://localhost:8080/api/v1/simulator/vehicles \
  -H "Content-Type: application/json" \
  -d '[1, 2]'

# Disable
curl -X POST http://localhost:8080/api/v1/simulator/disable
```

---

## 🗄 Database Schema

### Core Tables

**vehicle**
- Primary vehicle information
- Links to model, hub, current booking/driver
- Option B fields: `is_virtual`, tracking timestamps

**vehicle_state**
- Real-time state: GPS, battery, speed
- Option B fields: `data_source`, `message_sequence`, `last_updated_at`

**vehicle_event_log**
- Audit trail for all operations
- Stores event type and detailed change data (JSON)

**vehicle_model**
- Vehicle specifications and capabilities

**fleet_hub**
- Hub locations and metadata

### Relationships
```
vehicle_model ─┐
               ├─< vehicle >─ vehicle_state
fleet_hub ────┘              │
                             └─ vehicle_event_log
```

---

## 📁 Project Structure

```
car-management/
├── src/main/java/com/crs/carmanagement/
│   ├── config/              # Configuration classes
│   │   ├── DataInitializer.java
│   │   └── SimulatorConfig.java
│   ├── controller/          # REST controllers
│   │   ├── VehicleController.java
│   │   ├── VehicleStateController.java
│   │   └── SimulatorController.java
│   ├── dto/                 # Data Transfer Objects
│   │   ├── request/
│   │   └── response/
│   ├── entity/              # JPA entities
│   │   ├── Vehicle.java
│   │   ├── VehicleState.java
│   │   ├── VehicleEventLog.java
│   │   ├── VehicleModel.java
│   │   └── FleetHub.java
│   ├── enums/               # Enumerations
│   │   ├── VehicleStatus.java
│   │   ├── EventType.java
│   │   └── DataSource.java
│   ├── exception/           # Exception handling
│   │   ├── GlobalExceptionHandler.java
│   │   ├── EntityNotFoundException.java
│   │   └── BusinessValidationException.java
│   ├── repository/          # Data repositories
│   ├── service/             # Business logic
│   │   └── impl/
│   └── simulator/           # Virtual car simulator
│       └── VirtualCarSimulator.java
├── src/main/resources/
│   └── application.yml      # Application configuration
├── pom.xml                  # Maven dependencies
└── README.md               # This file
```

---

## 🧪 Testing

### Run with Sample Data

The application automatically initializes with sample data:
- **5 Vehicles** (3 virtual, 2 real)
- **2 Vehicle Models** (VinFast VF8, VF9)
- **2 Fleet Hubs** (District 1, Thu Duc)

### Manual Testing

1. **View all vehicles**:
   ```bash
   curl http://localhost:8080/api/v1/vehicles
   ```

2. **Track vehicle movement**:
   ```bash
   watch -n 2 'curl -s http://localhost:8080/api/v1/vehicles/1/state | jq'
   ```

3. **View event logs**:
   ```bash
   curl http://localhost:8080/api/v1/vehicles/1 | jq '.data.recentEvents'
   ```

---

## 🔮 Future Enhancements

- [ ] RabbitMQ integration for real-time IoT data
- [ ] Integration with Booking Service
- [ ] Integration with Driver Service
- [ ] JWT authentication & authorization
- [ ] Advanced search and filtering
- [ ] Analytics and reporting endpoints
- [ ] WebSocket support for real-time notifications
- [ ] Docker containerization
- [ ] Kubernetes deployment
- [ ] Monitoring with Spring Actuator & Prometheus

---

## 📝 API Response Format

All API responses follow a consistent structure:

**Success Response**:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2026-01-30T15:30:00"
}
```

**Error Response**:
```json
{
  "success": false,
  "message": "Error description",
  "errors": ["Detailed error 1", "Detailed error 2"],
  "timestamp": "2026-01-30T15:30:00",
  "path": "/api/v1/vehicles/999"
}
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **LapTruong** - *Initial work* - [GitHub](https://github.com/laptruong-hub)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- VinFast for inspiration on electric vehicle management
- OpenAPI/Swagger for API documentation tools

---

## 📞 Support

For support, email your-email@example.com or create an issue in the repository.

---

**Built with ❤️ using Spring Boot and Java 21**
