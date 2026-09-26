# LMS Postman Collection

The `collections/LMS` folder contains one request and one representative saved exchange for each controller operation.

## Variables

- `HOST` defaults to `http://localhost:8080`.
- `TOKEN` is the JWT for the current role. Login returns the token; paste it into this variable before running protected requests.
- `STUDENT_USERNAME` and `STUDENT_PASSWORD` are sample login values. Register the student first or replace them with an existing account.
- `DEPARTMENT_ID`, `INSTRUCTOR_ID`, `COURSE_ID`, `STUDENT_ID`, `ENROLLMENT_ID`, and `SEMESTER_ID` are sample IDs. Replace them with IDs returned by your running app.

## Suggested Sequence

1. Register a student and log in. Use the resulting `STUDENT` token for `/students/me/enrollments` requests.
2. Use an existing `ADMIN` token for department, instructor, course, semester, student-list, and enrollment-result operations. The admin registration endpoint cannot create the first admin.
3. Create a department, then an instructor in that department, then a course. The course example assigns `INSTRUCTOR_ID` and has no prerequisite.
4. Create a semester whose date range includes today before enrolling students. Semester ranges cannot overlap.
5. Enroll using a student token and an existing course ID. Enrollment requires an active semester and is limited by that semester's maximum hours and failed-attempt settings. Prerequisites must be completed first.
6. Use an admin token to record `COMPLETED` or `FAILED`; students can drop only their own active enrollments.

Saved responses are illustrative examples. Actual IDs, timestamps, nested entities, and errors depend on the database state. The application returns structured `code` and `message` bodies for its handled 400, 403, 404, and 409 errors.