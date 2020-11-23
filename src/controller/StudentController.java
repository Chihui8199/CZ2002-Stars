package controller;

import errormessage.ErrorMessage;
import exception.MaxAuExceededException;
import filemanager.ILoginable;
import filemanager.IStorageManager;
import model.Course;
import model.IndexNumber;
import model.LoginInfo;
import model.Student;
import view.StudentUi;
import exception.CourseRegisteredException;
import exception.ClashingRegisteredIndexNumberException;
import exception.NoVacancyException;
import exception.NoVacancySwapException;
import exception.CourseInWaitListException;
import exception.SameIndexNumberSwapException;
import exception.WrongLoginInfoException;
import exception.WrongAccessPeriodException;
import exception.ClashingWaitListedIndexNumberException;
import exception.PeerClashingRegisteredIndexNumberException;
import exception.PeerClashingWaitListedIndexNumberException;

import java.util.ArrayList;

public class StudentController {
    private Student student;
    private StudentUi studentUi;
    private IStorageManager storageManager;
    private ILoginable loginManager;

    public StudentController(String userId, IStorageManager storageManager, ILoginable loginManager) {
        studentUi = new StudentUi();
        this.storageManager = storageManager;
        student = storageManager.getStudent(userId);
        this.loginManager = loginManager;
    }

    public void run() {
        studentUi.printWelcomeMessage(student.getName());
        int choice;
        do {
            choice = studentUi.getMenuInputChoice();
            switch (choice) {
                case 1:
                    addCourse();
                    break;
                case 2:
                    dropRegisteredCourse();
                    break;
                case 3:
                    dropWaitListCourse();
                    break;
                case 4:
                    printRegisteredAndWaitListCourses();
                    break;
                case 5:
                    printVacancies();
                    break;
                case 6:
                    changeIndex();
                    break;
                case 7:
                    swapIndex();
                    break;
                case 8:
                    studentUi.printGoodBye();
                    break;
                default:
                    studentUi.printErrorMessage(ErrorMessage.ERROR_INPUT_CHOICE);
            }
        } while (choice != 8);
    }

    private void addCourse() {
        ArrayList<Course> courses = storageManager.getAllCourses();
        int index;
        index = studentUi.getIndexOfCourseToRegister(courses);
        Course courseToBeAdded = courses.get(index);
        index = studentUi.getIndexOfIndexNumberToRegister(courseToBeAdded.getIndexNumbers());
        IndexNumber indexNumberToBeAdded = courseToBeAdded.getIndexNumbers().get(index);
        try {
            storageManager.registerForCourse(student.getUserId(), courseToBeAdded.getCourseCode(),
                    indexNumberToBeAdded);
            String messageSuccess = "You have been successfully registered for the course:\n\n"
                    + courseToBeAdded.toString() + "\n\n" + indexNumberToBeAdded.getFullDescription();
            studentUi.printMessageWithDivider(messageSuccess, "An email will be sent to you.");
        } catch (CourseRegisteredException | ClashingRegisteredIndexNumberException | CourseInWaitListException
                | ClashingWaitListedIndexNumberException | MaxAuExceededException e) {
            studentUi.printErrorMessage(e.getMessage());
        } catch (NoVacancyException e) {
            studentUi.printMessageWithDivider(e.getMessage());
            storageManager.addCourseToWaitList(student.getUserId(), courseToBeAdded.getCourseCode(),
                    indexNumberToBeAdded);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dropRegisteredCourse() {
        ArrayList<Course> registeredCourses = storageManager.getCoursesTakenByStudent(student);
        if (registeredCourses.isEmpty()) {
            studentUi.printMessageWithDivider(ErrorMessage.NO_REGISTERED_COURSES);
            return;
        }
        int index = studentUi.getIndexOfCourseToDrop(registeredCourses, true);
        Course courseToBeDropped = registeredCourses.get(index);
        IndexNumber indexNumber = student.getRegisteredIndexNumbers().get(courseToBeDropped.getCourseCode());
        try {
            storageManager.dropCourseAndRegisterNextStudentInWaitList(student.getUserId(), courseToBeDropped.getCourseCode(), indexNumber);
            String messageSuccess = "You have successfully dropped the course:\n\n"
                    + courseToBeDropped.toString() + "\n\n" + indexNumber.getFullDescription();
            studentUi.printMessageWithDivider(messageSuccess, "An email will be sent to you");
        } catch (Exception e) {
            assert false : "These exceptions should have already been accounted for when you add the course into wait list...";
        }
    }

    private void dropWaitListCourse() {
        ArrayList<Course> waitListCourses = storageManager.getCoursesInWaitListByStudent(student);
        if (waitListCourses.isEmpty()) {
            studentUi.printMessageWithDivider(ErrorMessage.NO_WAITLIST_COURSES);
            return;
        }
        int index = studentUi.getIndexOfCourseToDrop(waitListCourses, false);
        Course course = waitListCourses.get(index);
        IndexNumber indexNumber = student.getWaitListIndexNumbers().get(course.getCourseCode());
        storageManager.dropCourseFromWaitList(student.getUserId(), course.getCourseCode(), indexNumber);

        String messageSuccess = "You have dropped from wait list for the course:\n\n"
                + course.toString() + "\n\n" + indexNumber.getFullDescription();
        studentUi.printMessageWithDivider(messageSuccess, "An email will be sent to you.");
    }

    private void printRegisteredAndWaitListCourses() {
        String registeredAu = "You are registered for " + student.getRegisteredAu() + " AU.";
        String registeredCourses = "\n";
        int index = 1;
        for (String courseCode: student.getRegisteredCourseCodes()) {
            Course course = storageManager.getCourse(courseCode);
            registeredCourses += (index) + ". " + course.toString();
            registeredCourses += "\n\t" + student.getRegisteredIndexNumbers().get(courseCode).getFullDescription();
            if (index != student.getRegisteredCourseCodes().size()) {
                registeredCourses += "\n";
            }
            index++;
        }
        studentUi.printMessageWithDivider(registeredAu, "Here are the courses you are registered for:",
                registeredCourses);

        String waitListAu = "You have " + student.getWaitListAu() + " AU in the wait list.";
        String waitListCourses = "\n";
        for (String courseCode: student.getWaitListCourseCodes()) {
            Course course = storageManager.getCourse(courseCode);
            waitListCourses += (index) + ". " + course.toString();
            waitListCourses += "\n\t" + student.getWaitListIndexNumbers().get(courseCode).getFullDescription();
            if (index != student.getWaitListIndexNumbers().size()) {
                registeredCourses += "\n";
            }
            index++;
        }
        studentUi.printMessageWithDivider(waitListAu, "Here are the courses on your wait list:", waitListCourses);

        studentUi.print("Your total AU (registered + wait list) is " + student.getTotalAuInRegisteredAndWaitList() + ".");
    }

    private void printVacancies() {
        studentUi.checkVacancyOfIndexNumber(storageManager.getAllCourses());
    }

    private void changeIndex() {
        // Input the course you want to change //
        ArrayList<Course> courses = storageManager.getCoursesTakenByStudent(student);
        if (courses.isEmpty()) {
            studentUi.printMessageWithDivider(ErrorMessage.NO_REGISTERED_COURSES);
            return;
        }
        int indexStudent = studentUi.getIndexOfCourseToChange(courses);
        Course courseToBeChanged = courses.get(indexStudent);
        IndexNumber indexNumberToBeChanged = student.getRegisteredIndexNumbers().get(courseToBeChanged.getCourseCode());
        studentUi.printMessageWithDivider("Swapping index for course: " + courseToBeChanged.toString()
                , "You are currently registered for Index Number:\n" + indexNumberToBeChanged.getId());

        // Input the index you want to change to: //
        int index;
        index = studentUi.getIndexOfIndexNumberToChange(courseToBeChanged.getIndexNumbers());
        IndexNumber newIndexNumber = courseToBeChanged.getIndexNumbers().get(index);

        try {
            storageManager.swapIndexNumber(student.getUserId(), courseToBeChanged.getCourseCode(), newIndexNumber);
            studentUi.printMessageWithDivider("Index Number: " + indexNumberToBeChanged.getId() + " for "
                    + courseToBeChanged.toString() + " has been successfully changed to " + newIndexNumber.getId(),
                    "An email will be sent to you.");
        } catch (NoVacancySwapException | ClashingRegisteredIndexNumberException | SameIndexNumberSwapException e) {
            studentUi.printErrorMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void swapIndex() {
        ArrayList<Course> courses = storageManager.getCoursesTakenByStudent(student);
        if (courses.isEmpty()) {
            studentUi.printMessageWithDivider(ErrorMessage.NO_REGISTERED_COURSES);
            return;
        }
        int index = studentUi.getIndexOfCourseToChange(courses);
        Course courseToBeSwapped = storageManager.getCourse(student.getRegisteredCourseCodes().get(index));

        LoginInfo loginInfoOfPeer = studentUi.getLoginInfoOfPeer();
        try {
            loginManager.verifyLoginInfo(loginInfoOfPeer);
        } catch (WrongLoginInfoException e) {
            studentUi.printErrorMessage(e.getMessage());
            return;
        } catch (WrongAccessPeriodException e) {
            // ignore, doesn't matter since we are just swapping index with peer
        }

        Student peer = storageManager.getStudent(loginInfoOfPeer.getUserId());

        if (!peer.getRegisteredCourseCodes().contains(courseToBeSwapped.getCourseCode())) {
            studentUi.printErrorMessage(ErrorMessage.PEER_DOES_NOT_TAKE_COURSE);
            return;
        }

        if (!studentUi.confirmSwapWithPeer(student, peer, courseToBeSwapped)) {
            return;
        }

        try {
            storageManager.swapIndexWithPeer(student.getUserId(), peer.getUserId(), courseToBeSwapped.getCourseCode());

            studentUi.printMessageWithDivider("Your Index Number: "
                            + peer.getRegisteredIndexNumbers().get(courseToBeSwapped.getCourseCode()).getId() + " for "
                            + courseToBeSwapped.toString() + " has been successfully changed to: "
                            + student.getRegisteredIndexNumbers().get(courseToBeSwapped.getCourseCode()).getId(),
                            "An email will be sent to you and your peer.");
        } catch (PeerClashingRegisteredIndexNumberException | SameIndexNumberSwapException
                | ClashingRegisteredIndexNumberException | PeerClashingWaitListedIndexNumberException
                | ClashingWaitListedIndexNumberException e) {
            studentUi.printErrorMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
