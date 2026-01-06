@echo off
REM MRP API Integration Tests - WITH STATUS CODES
REM Shows HTTP status codes for each request

echo ==================================
echo MRP API Integration Tests
echo WITH HTTP STATUS CODES
echo ==================================
echo.

set BASE_URL=http://localhost:8080
set TOKEN1=
set TOKEN2=
set MEDIA_ID=
set RATING_ID=
set USER1_ID=
set USER2_ID=

echo ===================================
echo 1. AUTHENTICATION TESTS
echo ===================================
echo.

echo Test 1: Register User 1
echo Expected: 201 Created (or 400 if exists)
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/register -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"pass123\"}"
echo.
pause

echo Test 2: Register User 2
echo Expected: 201 Created (or 400 if exists)
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/register -H "Content-Type: application/json" -d "{\"username\":\"testuser2\",\"password\":\"pass456\"}"
echo.
pause

echo Test 3: Login User 1
echo Expected: 200 OK
echo COPY THE TOKEN
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"pass123\"}"
echo.
set /p TOKEN1="Enter TOKEN1: "
pause

echo Test 4: Login User 2
echo Expected: 200 OK
echo COPY THE TOKEN
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser2\",\"password\":\"pass456\"}"
echo.
set /p TOKEN2="Enter TOKEN2: "
pause

echo Test 5: Login with Wrong Password
echo Expected: 401 Unauthorized
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"wrongpass\"}"
echo.
pause

echo ===================================
echo 2. MEDIA MANAGEMENT TESTS
echo ===================================
echo.

echo Test 6: Create Media Entry
echo Expected: 201 Created
echo COPY THE MEDIA ID
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/media -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"title\":\"Inception\",\"description\":\"A mind-bending thriller\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Sci-Fi\",\"Thriller\"],\"ageRestriction\":13}"
echo.
set /p MEDIA_ID="Enter MEDIA_ID: "
pause

echo Test 7: Get All Media
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/media -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 8: Get Media by ID
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/media/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 9: Search Media by Title
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media/search?title=Inception" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 10: Filter Media by Genre
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media?genre=Sci-Fi" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 11: Update Media (Owner)
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/media/%MEDIA_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"title\":\"Inception - Updated\",\"description\":\"Updated\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Sci-Fi\"],\"ageRestriction\":16}"
echo.
pause

echo Test 12: Update Media by Non-Owner (NEGATIVE TEST)
echo Expected: 400 Bad Request or 403 Forbidden
curl -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/media/%MEDIA_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"title\":\"Hacked\",\"description\":\"Hacked\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Action\"],\"ageRestriction\":12}"
echo.
pause

echo ===================================
echo 3. RATING TESTS
echo ===================================
echo.

echo Test 13: Create Rating
echo Expected: 201 Created
echo COPY THE RATING ID
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"mediaId\":\"%MEDIA_ID%\",\"stars\":5,\"comment\":\"Amazing movie!\"}"
echo.
set /p RATING_ID="Enter RATING_ID: "
pause

echo Test 14: Create Rating with Invalid Stars (NEGATIVE TEST)
echo Expected: 400 Bad Request
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"mediaId\":\"%MEDIA_ID%\",\"stars\":6,\"comment\":\"Invalid\"}"
echo.
pause

echo Test 15: Get Ratings for Media
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/ratings/media/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 16: Update Rating
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/ratings/%RATING_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"stars\":4,\"comment\":\"Updated comment\"}"
echo.
pause

echo Test 17: Confirm Rating
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings/%RATING_ID%/confirm -H "Authorization: Bearer %TOKEN2%"
echo.
pause

echo Test 18: Like Rating
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings/%RATING_ID%/like -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 4. FAVORITES TESTS
echo ===================================
echo.

echo Test 19: Add to Favorites
echo Expected: 201 Created
curl -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/favorites -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"mediaId\":\"%MEDIA_ID%\"}"
echo.
pause

echo Test 20: Get Favorites
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/favorites -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 21: Remove from Favorites
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X DELETE %BASE_URL%/api/favorites/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 5. USER PROFILE TESTS
echo ===================================
echo.

echo Test 22: Get User Profile
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/users/testuser1/profile -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 23: Update User Profile
echo Expected: 200 OK
echo Note: Enter the User ID from registration response
set /p USER1_ID="Enter USER1_ID: "
curl -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/users/%USER1_ID%/profile -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"favoriteGenre\":\"Sci-Fi\"}"
echo.
pause

echo Test 24: Get Leaderboard
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/users/leaderboard -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 6. RECOMMENDATIONS TEST
echo ===================================
echo.

echo Test 25: Get Recommendations
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/recommendations -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 7. CLEANUP
echo ===================================
echo.

echo Test 26: Delete Rating
echo Expected: 200 OK
curl -w "\n[HTTP Status: %%{http_code}]\n" -X DELETE %BASE_URL%/api/ratings/%RATING_ID% -H "Authorization: Bearer %TOKEN2%"
echo.
pause

echo ===================================
echo TESTS COMPLETE
echo ===================================
echo.
echo Review the HTTP status codes above.
echo Expected: Most tests 200/201, negative tests 400/401
echo.
pause