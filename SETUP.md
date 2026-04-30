# SkillSync Backend Setup Guide

This guide will help you set up and run the complete SkillSync backend with REST API.

## Prerequisites

- Java 8 or higher
- MySQL 8.0 or higher
- Maven (optional, for dependency management)
- Git (to clone if needed)

## Setup Steps

### 1. Database Setup

```bash
# Connect to MySQL
mysql -u root -p

# Run the schema file
source /path/to/skillsync/schema.sql
```

### 2. Build the Project

```bash
# Navigate to backend directory
cd /path/to/skillsync/backend

# Make build script executable
chmod +x build.sh

# Run the build script
./build.sh
```

The build script will:
- Download required dependencies (Gson, Tomcat embedded)
- Compile all Java files
- Set up the directory structure
- Copy necessary files

### 3. Run the Server

```bash
# Start the REST API server
java -cp "build/classes:lib/*" Server
```

The server will start on `http://localhost:8080`

### 4. Run the Original Demo (Optional)

```bash
# Run the original demo (without REST API)
java -cp ".:mysql-connector-j-8.0.33.jar" Main
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration  
- `POST /api/auth/logout` - User logout
- `GET /api/auth/me` - Get current user info

### Requests
- `POST /api/requests` - Send new request
- `GET /api/requests/received/{userId}` - Get received requests
- `GET /api/requests/sent/{userId}` - Get sent requests
- `PUT /api/requests/{requestId}/accept` - Accept request
- `PUT /api/requests/{requestId}/reject` - Reject request
- `PUT /api/requests/{requestId}/complete` - Complete request (triggers credit transfer)

### Credits
- `GET /api/credits/{userId}` - Get user credit balance
- `GET /api/credits/{userId}/history` - Get credit transaction history

## Complete Flow Example

### 1. User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Johnson",
    "email": "alice@example.com",
    "password": "password123",
    "linkedinProfile": "linkedin.com/in/alice"
  }'
```

### 2. Send Request
```bash
curl -X POST http://localhost:8080/api/requests \
  -H "Content-Type: application/json" \
  -d '{
    "senderId": 1,
    "receiverId": 2
  }'
```

### 3. Accept Request
```bash
curl -X PUT http://localhost:8080/api/requests/1/accept
```

### 4. Complete Request (Credit Transfer)
```bash
curl -X PUT http://localhost:8080/api/requests/1/complete
```

This will:
- Mark request as "COMPLETED"
- Deduct 1 credit from sender (learner)
- Add 1 credit to receiver (teacher)
- Record transactions in credit_transactions table

### 5. Check Credits
```bash
curl -X GET http://localhost:8080/api/credits/1
```

## Credit System Logic

### Initial Setup
- New users get 10 credits automatically
- Credit transactions are logged for audit trail

### Credit Transfer Rules
- **Learner loses 1 credit** when session is completed
- **Teacher gains 1 credit** when session is completed
- Transfer happens **only when request status = "COMPLETED"**
- All operations are **atomic** (database transaction)

### Transaction Types
- `INITIAL_BONUS` - New user signup (10 credits)
- `SESSION_COMPLETED` - Learner spent credit (-1)
- `SESSION_TAUGHT` - Teacher earned credit (+1)
- `ADMIN_ADJUSTMENT` - Manual adjustments by admin

## Request Flow Statuses

1. **PENDING** - Request sent, waiting for response
2. **ACCEPTED** - Request accepted, session can be scheduled
3. **REJECTED** - Request rejected, no further action
4. **COMPLETED** - Session finished, credits transferred

## Frontend Integration

### API Base URL
```
http://localhost:8080/api
```

### Enable Real API in Frontend
```javascript
// In frontend/src/services/api.js
localStorage.setItem('use_real_api', 'true');
```

### Frontend API Calls
The frontend is already configured to use these endpoints:
- Login: `POST /api/auth/login`
- Register: `POST /api/auth/register`
- Get Requests: `GET /api/requests/received/{userId}`
- Send Request: `POST /api/requests`
- Accept Request: `PUT /api/requests/{requestId}/accept`
- Complete Request: `PUT /api/requests/{requestId}/complete`
- Get Credits: `GET /api/credits/{userId}`

## Testing the Complete Flow

1. **Start both servers**:
   ```bash
   # Backend (Terminal 1)
   cd backend && java -cp "build/classes:lib/*" Server
   
   # Frontend (Terminal 2)  
   cd frontend && npm run dev
   ```

2. **Register two users** in the frontend

3. **User A sends request** to User B

4. **User B accepts request**

5. **User B completes request** (triggers credit transfer)

6. **Check credit balances** for both users

## Troubleshooting

### Database Connection Issues
- Check MySQL is running
- Verify database name: `skillsync`
- Check username/password in `database/DatabaseUtil.java`

### Build Issues
- Ensure Java 8+ is installed
- Check internet connection for downloading dependencies
- Verify file permissions

### API Not Working
- Check server is running on port 8080
- Verify CORS settings in `web.xml`
- Check browser console for errors

### Credit Transfer Not Working
- Ensure request status is "ACCEPTED" before completing
- Check users have sufficient credits
- Verify database transaction logs

## Production Considerations

- Use HTTPS instead of HTTP
- Implement proper JWT authentication
- Add rate limiting to API endpoints
- Set up proper logging and monitoring
- Use connection pooling for database
- Add input validation and sanitization
- Implement proper error handling
