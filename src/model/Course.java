package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Course implements Serializable {
    private String courseName;
    private String courseCode;
    private ArrayList<IndexNumber> indexNumbers;
    private School school;
    private int au;

    public Course(String courseName, String courseCode, School school, int au) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.school = school;
        this.au = au;
    }

    public ArrayList<IndexNumber> getIndexNumbers() {
        return indexNumbers;
    }

    public void setIndexNumbers(ArrayList<IndexNumber> indexNumbers) {
        this.indexNumbers = indexNumbers;
    }

    public void addIndexNumber(IndexNumber indexNumber) {
        indexNumbers.add(indexNumber);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public School getSchool() {
        return school;
    }

    public int getAu() {
        return au;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public ArrayList<Student> getRegisteredStudents() {
        ArrayList<Student> registeredStudents = new ArrayList<>();
        for (IndexNumber indexNumber: getIndexNumbers()) {
            for (Student student: indexNumber.getRegisteredStudents()) {
                registeredStudents.add(student);
            }
        }
        return registeredStudents;
    }

    public ArrayList<Student> getWaitListStudents() {
        ArrayList<Student> waitListStudents = new ArrayList<>();
        for (IndexNumber indexNumber: getIndexNumbers()) {
            for (Student student: indexNumber.getWaitListStudents()) {
                waitListStudents.add(student);
            }
        }
        return waitListStudents;
    }

    @Override
    public String toString() {
        return courseCode + " " + courseName;
    }
}
