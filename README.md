# Git Project Netflix Clone
This project is a Spring Boot–based backend for a Netflix clone application. It provides secure authentication and user management functionalities, allowing users to sign up, log in, and access the platform.
User Registration (Sign Up)\
User Authentication (Login)\
Password Reset Functionality\
Email Verification\
Role-Based Access Control (Admin & User)\
The following APIs are implemented in this project:\
Auth API\
Login\
Sign Up\
Reset Password\
Email Verification\

### Other authentication-related operations
Admin&nbsp;&nbsp;
User
Method Endpoint  Description </br>
POST &nbsp;&nbsp; 	/api/auth/signup	&nbsp;&nbsp;&nbsp;&nbsp; Register a new user\
POST &nbsp;&nbsp;	/api/auth/login	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Authenticate user\
POST &nbsp;&nbsp;	/api/auth/forgot-password &nbsp;&nbsp; Send password reset link\
POST &nbsp;&nbsp;	/api/auth/reset-password &nbsp;&nbsp; Reset user password\
GET	 &nbsp;&nbsp;&nbsp;&nbsp;   /api/auth/verify-email	&nbsp;&nbsp;&nbsp;&nbsp; Verify user email