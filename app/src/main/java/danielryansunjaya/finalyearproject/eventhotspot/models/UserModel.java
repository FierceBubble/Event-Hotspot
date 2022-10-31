package danielryansunjaya.finalyearproject.eventhotspot.models;

public class UserModel {
    String name;
    String studentID;
    String email;
    String programme;

    public UserModel(String name, String studentID, String email, String programme) {
        this.name = name;
        this.studentID = studentID;
        this.email = email;
        this.programme = programme;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
