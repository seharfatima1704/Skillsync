#!/bin/bash

# Test script for SkillSync API
# Tests the complete flow: registration → request → accept → complete → credit transfer

API_BASE="http://localhost:8080/api"

echo "=== SkillSync API Test Suite ==="
echo "Testing complete flow: User registration → Request → Accept → Complete → Credit Transfer"
echo ""

# Function to test API endpoint
test_api() {
    local method=$1
    local url=$2
    local data=$3
    local description=$4
    
    echo "Testing: $description"
    echo "Request: $method $url"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$url")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    echo "Response Code: $http_code"
    echo "Response Body: $body"
    echo ""
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo "✓ SUCCESS"
    else
        echo "✗ FAILED"
    fi
    echo "----------------------------------------"
}

# Check if server is running
echo "1. Checking if API server is running..."
if curl -s "$API_BASE/auth/login" > /dev/null; then
    echo "✓ API server is running"
else
    echo "✗ API server is not running. Please start it first:"
    echo "  java -cp \"build/classes:mysql-connector-j-8.0.33.jar:gson-2.8.9.jar\" APIServer"
    exit 1
fi
echo ""

# Test 1: Register User 1 (Alice)
echo "2. Registering User 1 (Alice)..."
test_api "POST" "$API_BASE/auth/register" '{
    "name": "Alice Johnson",
    "email": "alice@test.com",
    "password": "password123",
    "linkedinProfile": "linkedin.com/in/alice"
}' "Register Alice"

# Extract user ID from response (simplified - in real test would parse JSON)
alice_id=1

# Test 2: Register User 2 (Bob)
echo "3. Registering User 2 (Bob)..."
test_api "POST" "$API_BASE/auth/register" '{
    "name": "Bob Smith", 
    "email": "bob@test.com",
    "password": "password456",
    "linkedinProfile": "linkedin.com/in/bob"
}' "Register Bob"

bob_id=2

# Test 3: Login as Alice
echo "4. Login as Alice..."
test_api "POST" "$API_BASE/auth/login" '{
    "email": "alice@test.com",
    "password": "password123"
}' "Login Alice"

# Test 4: Check Alice's credits (should be 10)
echo "5. Check Alice's credits..."
test_api "GET" "$API_BASE/credits/$alice_id" "" "Get Alice's credits"

# Test 5: Alice sends request to Bob
echo "6. Alice sends request to Bob..."
test_api "POST" "$API_BASE/requests" '{
    "senderId": '$alice_id',
    "receiverId": '$bob_id'
}' "Alice sends request to Bob"

# Test 6: Check Bob's received requests
echo "7. Check Bob's received requests..."
test_api "GET" "$API_BASE/requests/received/$bob_id" "" "Get Bob's received requests"

# Test 7: Bob accepts the request (assuming request ID is 1)
echo "8. Bob accepts the request..."
test_api "PUT" "$API_BASE/requests/accept/1" "" "Accept request"

# Test 8: Complete the request (triggers credit transfer)
echo "9. Complete the request (credit transfer)..."
test_api "PUT" "$API_BASE/requests/complete/1" "" "Complete request"

# Test 10: Check Alice's credits after completion (should be 9)
echo "10. Check Alice's credits after completion..."
test_api "GET" "$API_BASE/credits/$alice_id" "" "Get Alice's final credits"

# Test 11: Check Bob's credits after completion (should be 11)
echo "11. Check Bob's credits after completion..."
test_api "GET" "$API_BASE/credits/$bob_id" "" "Get Bob's final credits"

# Test 12: Check Alice's sent requests
echo "12. Check Alice's sent requests..."
test_api "GET" "$API_BASE/requests/sent/$alice_id" "" "Get Alice's sent requests"

echo ""
echo "=== Test Summary ==="
echo "Expected Results:"
echo "- Alice should have 9 credits (10 - 1 for session)"
echo "- Bob should have 11 credits (10 + 1 for teaching)"
echo "- Request status should be 'COMPLETED'"
echo "- Credit transactions should be recorded"
echo ""
echo "If all tests passed, the complete flow is working correctly!"
echo ""
echo "To test with frontend:"
echo "1. Start API server: java -cp \"build/classes:mysql-connector-j-8.0.33.jar:gson-2.8.9.jar\" APIServer"
echo "2. Start frontend: cd ../frontend && npm run dev"
echo "3. Open browser to http://localhost:5173"
echo "4. Enable real API: localStorage.setItem('use_real_api', 'true')"
