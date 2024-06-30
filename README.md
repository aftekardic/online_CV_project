# CV Project

The CV Project includes many features e.g. registration, secure login, chat screen, cv upload, edit information, view all user information of admin users.

### Key Features

---

1. **User Registration and Login:**
   - **Registration:** New users must register by using Keycloak. In this way, the membership system has been made more secure.
   - **Login:** Registered customers can log in by entering their credentials. Keycloak is activated on login, generating a temporary token unique to the user. Until expiration, the token is periodically renewed by refresh-token.

2. **Account Management**
   - **Upload CV:** Users can upload their CV. They can update it later.
   - **Update Informations:** Users can update their informations.
   - **Show Users Informations:** Admins can display all users informations.
   - **Chat:** All members can chat instantly on the chat screen.

3. **Validation and Error Handling:**
   - The system ensures that withdrawals are completed only if the client has sufficient funds. Insufficient funds will result in the transaction being rejected and an error message. Completed transactions are also returned to the user as an information message.
  
### Prerequisites

---

- Java SDK 17
- Maven
- Docker
- Node minimum version → v21.6.1
- MySQL Database

### Installation

---

#### Cloning the Repository

Once Git is installed, clone the repository to your local machine:

1. Open a terminal or command prompt.

2. Navigate to the directory where you want to clone the repository.

3. Run the following command:
```
git clone https://github.com/aftekardic/online_CV_project.git
```

4. Navigate into the cloned repository:

```
cd online_CV_project
```

#### Running the Application

1. Open the project in your IDE.

2.  Run the application:

    - **Backend**
      
      - Additional Setup

         1. Configure your database settings in backend/src/main/resources/application.properties.

         2. Ensure you have the necessary environment variables set up for database access and other configurations.

      - Run backend application
        ```
        cd backend
        mvn clean install
        mvn spring-boot:run
        ```
        or you can run from BackendApplication.java run button.

    - **Frontend**

      - Additional Setup

         1. Firstly you can set the .env file for frontend requests.

         2. Open the .env file in the frontend home directory and configure it according to your needs. After you can run the frontend application.

      - Run frontend application
        ```
        cd frontend
        npm run start
        ```

### API Usage

---

If you are using backend only, you should first get a token from the /authenticate endpoint, then you can use other endpoints by adding `Bearer <your_token>` to the Authorization of the Header.

| HTTP Method | Endpoint                        | Description                          |
|-------------|---------------------------------|--------------------------------------|
| POST        | /auth/sign-in                   | Authenticate user and obtain token   |
| POST        | /auth/sign-up                   | Register a new user                  |
| POST        | /auth/refresh-token             | Refresh authentication token         |
| POST        | /auth/logout                    | Terminate user session               |
| ----------- | ------------------------------- | ------------------------------------ |
| GET         | /api/v1/chat/all                | Retrieve all chat messages           |
| POST        | /api/v1/chat/send               | Send a new chat message              |
| ----------- | ------------------------------- | ------------------------------------ |
| POST        | /api/v1/cv/upload               | Upload a CV                          |
| GET         | /api/v1/cv/info/{userEmail}     | Get CV information by user email     |
| GET         | /api/v1/cv/list                 | List all CVs                         |
| ----------- | ------------------------------- | ------------------------------------ |
| GET         | /api/v1/user/info               | Get user information by email        |
| GET         | /api/v1/user/all                | Get all users                        |
| PUT         | /api/v1/user/update             | Update user information              |

### Security Configurations

---

This project includes security configurations to ensure proper authentication and authorization for accessing various endpoints. Here's an overview of the security components and their functionalities:

1. **CustomAccessDenied Class (CustomAccessDenied.java):**
   - Implements Spring Security's AccessDeniedHandler interface.
   - Handles access denied situations by returning HTTP 403 Forbidden responses.
   - Uses JSON format with AuthResponseDto to indicate access denial.

2. **CustomAuthenticationEntryPoint Class (CustomAuthenticationEntryPoint.java):**
   - Implements Spring Security's AuthenticationEntryPoint interface.
   - Returns HTTP 401 Unauthorized responses for authentication failures or missing authentication credentials.
   - Uses JSON format with AuthResponseDto to indicate authentication errors.

3. **KeycloakJwtRolesConverter Class (KeycloakJwtRolesConverter.java):**
   - Implements Converter<Jwt, Collection<GrantedAuthority>> for Spring Security.
   - Extracts roles from JWT provided by Keycloak and converts them into a collection of GrantedAuthority.
   - Validates roles within the JWT using realm_access and resource_access fields.
   - Constructs appropriate GrantedAuthority objects based on the keycloak.client-id property.

4. **Web Security Configuration:**
   - The WebSecurityConfiguration class is a Spring configuration that manages security within the application. It enables web security with annotations and defines access rules using HttpSecurity. The configuration disables CSRF protection for stateless APIs and specifies roles (hasAnyRole) required to access endpoints like /api/v1/cv/**, /api/v1/user/**, and /api/v1/chat/**.

   - Endpoints under /auth/** are exempt from authentication requirements (permitAll). Error handling for authentication issues (authenticationEntryPoint) and access denial (accessDeniedHandler) is also configured.

   - JWT tokens issued by Keycloak are validated using a JwtDecoder configured with the token issuer URL (tokenIssuerUrl). The configuration converts JWT claims into Spring Security GrantedAuthority objects using JwtAuthenticationConverter and DelegatingJwtGrantedAuthoritiesConverter.

   - Additionally, CORS (Cross-Origin Resource Sharing) is enabled to allow requests from http://localhost:3000, supporting credentials, headers, and methods as specified.

   - This setup ensures that the application's endpoints are protected based on defined roles, integrates seamlessly with Keycloak for token validation, and manages cross-origin requests securely.
   
**_Feel free to customize these configurations according to your project's specific requirements and security policies. If you have any questions or need further assistance, please let me know!_**

## Conclusion

In essence, the CV Project exemplifies a secure and user-centric application environment where advanced functionalities are supported by robust security measures. By leveraging Keycloak for authentication, implementing precise access controls, and integrating error handling mechanisms, the project ensures both user data protection and operational efficiency. This comprehensive approach not only enhances user trust but also underscores the project's commitment to delivering a secure and reliable service.

If you need further details or adjustments to the conclusion, feel free to let me know!
