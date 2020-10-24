package Controller;

import ErrorMessage.ErrorMessage;
import FileManager.StorageManager;
import Model.Course;
import Model.LoginInfo;
import Model.Student;
import View.AdminUi;

import java.util.ArrayList;

public class AdminController {
    private AdminUi adminUi;
    private StorageManager storageManager;

    public AdminController() {
        adminUi = new AdminUi();
        storageManager = new StorageManager();
    }

    public void run() {
        int choice;
        do {
            choice = adminUi.getMenuInputChoice();

            switch (choice) {
            case 1:
                editStudentAccessPeriod();
                break;
            case 2:
                addStudent();
                break;
            case 3:
                addCourse();
                break;
            case 4:
                editCourse();
                break;
            case 5:
                checkAvailableSlot();
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            default:
                adminUi.printErrorMessage(ErrorMessage.ERROR_INPUT_CHOICE);
            }
        } while (choice != 8);
    }

    private void addCourse() {
        Course course = adminUi.getCourseToAdd();
        storageManager.addCourse(course);
        adminUi.printCourses(storageManager.getAllCourses(), "Added " + course.toString() + "!");
    }

    private void editCourse() {
        String courseCode = adminUi.getCourseToEdit(storageManager.getAllCourses());
        Course course = storageManager.getCourse(courseCode);
        int choice = adminUi.getEditCourseChoice(course);

        switch (choice) {
        case 1:
            storageManager.setNewCourseCode(adminUi.getNewCourseCode(course), course.getCourseCode());
            break;
        case 2:
            storageManager.setNewCourseName(adminUi.getNewCourseName(course), course.getCourseCode());
            break;
        case 3:
            storageManager.setNewSchool(adminUi.getNewSchool(course), course.getCourseCode());
            break;
        case 4:
            storageManager.addIndexNumber(adminUi.getIndexNumber(true, course), course.getCourseCode());
            break;
        case 5:
            int index = adminUi.getIndexOfIndexNumberToEdit(course);
            int newMaxVacancy = adminUi.getNewMaxVacancy(course, index);
            try {
                storageManager.setNewMaxVacancy(course.getCourseCode(), index, newMaxVacancy);
            } catch (Exception e) {
                adminUi.printMessageWithDivider(e.getMessage());
            }
            break;
        }
    }

    private void checkAvailableSlot() {
        adminUi.checkVacancyOfIndexNumber(storageManager.getAllCourses());
    }

    private void editStudentAccessPeriod() {
        ArrayList<Student> students = storageManager.getAllStudents();
        int index = adminUi.getStudentChoiceEditAccessTime(students);
        Student student = students.get(index);
        storageManager.setNewAccessTime(student.getUserId(),
                adminUi.getNewAccessTime(student));
        adminUi.printMessageWithDivider("Updated access period for " + student.getName() + "!");
    }

    private void addStudent() {
        LoginInfo loginInfoForStudent = adminUi.getLoginInfo();
        Student student = adminUi.getNewStudent(loginInfoForStudent.getUserId());
        storageManager.addStudent(student, loginInfoForStudent);
        adminUi.printStudents(storageManager.getAllStudents(), "Added " + student.getName() + "!");
    }
}
