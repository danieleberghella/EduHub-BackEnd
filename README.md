# EduHub (Backend)

EduHub is an experimental project designed to develop a web platform for a private school offering professional courses in graphic design, software development, and design. This platform allows students to access course materials, participate in online quizzes, receive official notifications, and monitor their progress. Teachers can upload course programs and track student attendance. Administrators can use dashboards to monitor students' average performance and course activities.

## Features

### For Students
- Access and download course materials (PDFs, videos, useful links).
- Participate in online quizzes with automatic correction and immediate feedback.
- View personal statistics, including average quiz scores.
- Receive official notifications from the school.

### For Teachers
- Upload and manage course materials.
- Track student attendance.
- Manage weekly syllabi and schedules.

### For Administrators
- Monitor average quiz scores per course.
- Track attendance for each course.
- View course syllabi and weekly schedules.
- Send formal notifications to students.

### General Features
- **Authentication and Authorization**: Role-based access for Students, Teachers, and Administrators.
- **Responsive Design**: Fully functional on both desktop and mobile devices.

## Technologies Used
- **Backend**: Java
- **Database**: PostgreSQL
- **Version Control**: Git and GitHub

## Getting Started

### Prerequisites
- PostgreSQL installed on your system.
- Git installed on your system.

### Setup
1. **Clone the Repository**
   ```bash
   git clone https://github.com/danieleberghella/EduHub-BackEnd.git
   cd EduHub-BackEnd
   ```

2. **Initialize the Database**
    - Navigate to the `db` folder and locate the `ddl.sql` and `dml.sql` files.
    - Execute the SQL commands in `ddl.sql` to set up the database structure.
    - Execute the SQL commands in `dml.sql` to populate the database with initial data.

3. **Configure Environment Variables**
    - Locate the `.env.local` file in the repository.
    - Rename it to `.env`.
    - Open the `.env` file and set the following variables:
      ```env
      DB_PASSWORD=<your-database-password>
      ADMIN_ID=<default-admin-id>
      ```
      Replace `<your-database-password>` with your PostgreSQL database password.
      Replace `<default-admin-id>` with the ID of the new default admin if you want to change the default admin.

4. **Run the Application**
    - Start the backend service by following the instructions provided in the project code.

## Environmental Variables
The following environment variables need to be set in the `.env` file:
- `DB_PASSWORD`: The password for connecting to your PostgreSQL database.
- `ADMIN_ID`: The ID of the default admin for receiving notifications for new portal registrations.

## Contribution
Feel free to fork this repository, submit issues, and make pull requests to contribute to the development of EduHub.

## License
This project is licensed under the MIT License. See the `LICENSE` file for more details.
