Bankly

Bankly is an Android mobile banking application that allows users to register, authenticate, and access account information through a REST API.
The project focuses on implementing real-world API integration and user authentication flows in a mobile environment.

Key Features
Account creation and activation
User login and authentication
Fetching account information from an external API
Network communication using Retrofit
Logging and debugging with OkHttp
Tech Stack
Java
Android Studio
Retrofit
OkHttp
REST API (JSON)
API

Base URL:
https://coral-app-zkmoe.ondigitalocean.app/

Endpoints used:
POST /v1/accounts/create
POST /v1/accounts/auth
POST /v1/accounts/activate
GET /v1/accounts/info
GET /v1/accounts/info/{email}
How It Works

The application follows a simple authentication flow:
Users create an account
The account is activated via the API
Users log in with their credentials
Account data is retrieved and displayed

Project Structure

activities/
models/
api/
services/
utils/
Getting Started
Clone the repository and open it in Android Studio:
git clone https://github.com/YOUR_USERNAME/Bankly.git
Run the application on an emulator or Android device.
