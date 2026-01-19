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
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/register -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"pass123\"}"
echo.
pause

echo Test 2: Register User 2
echo Expected: 201 Created (or 400 if exists)
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/register -H "Content-Type: application/json" -d "{\"username\":\"testuser2\",\"password\":\"pass456\"}"
echo.
pause

echo Test 3: Login User 1
echo Expected: 200 OK
echo COPY THE TOKEN FROM RESPONSE
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"pass123\"}"
echo.
set /p TOKEN1="Enter TOKEN1 (e.g., testuser1-mrpToken): "
pause

echo Test 4: Login User 2
echo Expected: 200 OK
echo COPY THE TOKEN FROM RESPONSE
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser2\",\"password\":\"pass456\"}"
echo.
set /p TOKEN2="Enter TOKEN2 (e.g., testuser2-mrpToken): "
pause

echo Test 5: Login with Wrong Password
echo Expected: 401 Unauthorized
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/users/login -H "Content-Type: application/json" -d "{\"username\":\"testuser1\",\"password\":\"wrongpass\"}"
echo.
pause

echo ===================================
echo 2. MEDIA MANAGEMENT TESTS
echo ===================================
echo.

echo Test 6: Create Media Entry
echo Expected: 201 Created
echo COPY THE MEDIA ID FROM RESPONSE
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/media -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"title\":\"Inception\",\"description\":\"A mind-bending thriller\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Sci-Fi\",\"Thriller\"],\"ageRestriction\":13}"
echo.
set /p MEDIA_ID="Enter MEDIA_ID (UUID): "
pause

echo Test 7: Get All Media
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/media -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 8: Get Media by ID
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/media/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 9: Search Media by Title
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media/search?title=Inception" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 10: Filter Media by Genre
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media?genre=Sci-Fi" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 11: Filter by Type (MOVIE)
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media?type=MOVIE" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 12: Sort by Rating
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET "%BASE_URL%/api/media?sort=rating" -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 13: Update Media (Owner)
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/media/%MEDIA_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"title\":\"Inception - Updated\",\"description\":\"Updated\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Sci-Fi\",\"Action\"],\"ageRestriction\":16}"
echo.
pause

echo Test 14: Update Media by Non-Owner (NEGATIVE TEST)
echo Expected: 400 Bad Request or 403 Forbidden
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/media/%MEDIA_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"title\":\"Hacked\",\"description\":\"Hacked\",\"mediaType\":\"MOVIE\",\"releaseYear\":2010,\"genres\":[\"Action\"],\"ageRestriction\":12}"
echo.
pause

echo ===================================
echo 3. RATING TESTS
echo ===================================
echo.

echo Test 15: Create Rating
echo Expected: 201 Created
echo COPY THE RATING ID FROM RESPONSE
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"mediaId\":\"%MEDIA_ID%\",\"stars\":5,\"comment\":\"Amazing movie!\"}"
echo.
set /p RATING_ID="Enter RATING_ID (UUID): "
pause

echo Test 16: Create Duplicate Rating (NEGATIVE TEST)
echo Expected: 400 Bad Request (already rated)
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"mediaId\":\"%MEDIA_ID%\",\"stars\":3,\"comment\":\"Duplicate\"}"
echo.
pause

echo Test 17: Create Rating with Invalid Stars (NEGATIVE TEST)
echo Expected: 400 Bad Request
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"mediaId\":\"%MEDIA_ID%\",\"stars\":6,\"comment\":\"Invalid\"}"
echo.
pause

echo Test 18: Get Ratings for Media
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/ratings/media/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 19: Update Rating
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X PUT %BASE_URL%/api/ratings/%RATING_ID% -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN2%" -d "{\"stars\":4,\"comment\":\"Updated comment\"}"
echo.
pause

echo Test 20: Confirm Rating (make comment public)
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings/%RATING_ID%/confirm -H "Authorization: Bearer %TOKEN2%"
echo.
pause

echo Test 21: Like Rating (User1 likes User2's rating)
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/ratings/%RATING_ID%/like -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 4. FAVORITES TESTS
echo ===================================
echo.

echo Test 22: Add to Favorites
echo Expected: 201 Created
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/favorites -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"mediaId\":\"%MEDIA_ID%\"}"
echo.
pause

echo Test 23: Add Duplicate Favorite (NEGATIVE TEST)
echo Expected: 400 Bad Request (already in favorites)
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X POST %BASE_URL%/api/favorites -H "Content-Type: application/json" -H "Authorization: Bearer %TOKEN1%" -d "{\"mediaId\":\"%MEDIA_ID%\"}"
echo.
pause

echo Test 24: Get User Favorites
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/favorites -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 25: Remove from Favorites
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X DELETE %BASE_URL%/api/favorites/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 5. USER PROFILE TESTS
echo ===================================
echo.

echo Test 26: Get User Profile
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/users/testuser1/profile -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 27: Get Leaderboard
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/users/leaderboard -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 6. RECOMMENDATIONS TEST
echo ===================================
echo.

echo Test 28: Get Recommendations
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/recommendations -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo Test 29: Get Recommendations with Limit
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X GET %BASE_URL%/api/recommendations?limit=5 -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo 7. CLEANUP
echo ===================================
echo.

echo Test 30: Delete Rating
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X DELETE %BASE_URL%/api/ratings/%RATING_ID% -H "Authorization: Bearer %TOKEN2%"
echo.
pause

echo Test 31: Delete Media
echo Expected: 200 OK
curl.exe -w "\n[HTTP Status: %%{http_code}]\n" -X DELETE %BASE_URL%/api/media/%MEDIA_ID% -H "Authorization: Bearer %TOKEN1%"
echo.
pause

echo ===================================
echo TESTS COMPLETE
echo ===================================
echo.
echo Review the HTTP status codes above.
echo.
echo Expected Results Summary:
echo - Most tests: 200 OK or 201 Created
echo - Negative tests: 400 Bad Request or 401/403
echo - Wrong password: 401 Unauthorized
echo - Duplicate operations: 400 Bad Request
echo - Non-owner edits: 403 Forbidden
echo.
echo If all tests show expected codes, the API works correctly
echo.
pause