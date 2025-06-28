<h1 align="center" id="title">OrganizeU G4 (WebDev Wizards) </h1>

<p id="description">OrganizeU is a smart personal productivity website designed to simplify life and keep you organized. It helps you manage tasks set goals and track progress effortlessly.It is a one stop solution to create your schedule manage your time and stay productive.</p>
<br><br>
<h2>Project Demo</h2>

https://github.com/user-attachments/assets/4a94c1a0-55b9-4f7f-9ee8-c50d0e86e7bf

<br><br>
<h2>Project Screenshots</h2>

<img width="894" alt="image" src="https://github.com/user-attachments/assets/eddc6ad6-3393-4de3-9e67-23c3057b21fb"><br>
<img width="503" alt="image" src="https://github.com/user-attachments/assets/d88dff23-ab0f-440f-9b96-f6039a049b93"><br>
<img width="909" alt="image" src="https://github.com/user-attachments/assets/937801cf-adf4-430b-8e4c-328d1172a3cd"><br>

<img width="942" alt="image" src="https://github.com/user-attachments/assets/4c41780c-1582-43ab-b48f-c14b780bd5f8"><br>
<img width="914" alt="image" src="https://github.com/user-attachments/assets/414b9672-3e29-4c20-8e6f-656bda1a826d"><br>
<img width="396" alt="image" src="https://github.com/user-attachments/assets/a010aba1-004b-4651-939c-ea9c8fe78154"><br>
<img width="944" alt="image" src="https://github.com/user-attachments/assets/3b7feab8-aa65-4848-a68d-efa5a2541fcb"><br><br>



<h2>🧐 Features</h2>

Here're some of the project's best features:

*   Week and Month Planner
*   Day Schedular
*   Resource Hub
*   Student-centric Scheduling
*   Goal Progress Tracker

<br><br>
<h2>💻 Built with</h2>

Technologies used in the project:

*   HTML
*   CSS
*   Javascript
*   Spring Boot (Java)
*   SQL

<br><br>

## 👥 Team Members & Contributions

| Name       | Contributions |
|------------|---------------|
| **Mrunal** | Schedule Page, About Us Page, Frontend Integration, Spring Security |
| **Sambhav** | Spring Boot Backend Logic, Overall Integration, Deployment |
| **Khushi** | Resource Page, Page Routing, Content Enhancements |
| **Suhani** | Spring Boot Models, Resource Page Backend, Overall Site Design |
| **Surendra** | DB Integration, DB Error Fixes, Home Page, Profile Settings |
| **Parteek** | Forms Backend, Navbar, Footer, UI Refactor for Forms |
| **Janmesh** | Schedule Page Backend, Welcome Page, Login and Register Page |
| **Harshita** | JavaScript Functionality, User Dashboard, Final UI Enhancements, Design |
| **Srushti** | Contact Us Module, Site Layout, Testing and Debugging |





# OrganizeU - Student Productivity Suite

## Deployment Configuration

### Database Configuration
The application uses environment variables for database configuration. Set these environment variables in your deployment environment:

```bash
DB_URL=jdbc:mysql://your-database-host:3306/organizeu
DB_USERNAME=your-database-username
DB_PASSWORD=your-database-password
```

### Production Deployment
To run the application in production mode:

1. Set the environment variables mentioned above
2. Run the application with the production profile:
```bash
java -jar organizeu.jar --spring.profiles.active=prod
```

### Security Considerations
- Never commit database credentials to version control
- Use strong passwords for database users
- Consider using a secrets management service in production
- Use SSL/TLS for database connections in production

### Database Setup
1. Create a MySQL database:
```sql
CREATE DATABASE organizeu;
```

2. Create a database user with appropriate permissions:
```sql
CREATE USER 'your-username'@'%' IDENTIFIED BY 'your-password';
GRANT ALL PRIVILEGES ON organizeu.* TO 'your-username'@'%';
FLUSH PRIVILEGES;
```

### Environment-Specific Configuration
- Development: Uses default configuration in application.properties
- Production: Uses application-prod.properties with environment variables
- Test: Can be configured using application-test.properties



