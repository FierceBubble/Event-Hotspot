package danielryansunjaya.finalyearproject.eventhotspot.models;

public class UserModel {
    String name;
    String studentID;
    String email;
    String programme;
    int elePoints;

    public UserModel(String name, String studentID, String email, String programme, int elePoints) {
        this.name = name;
        this.studentID = studentID;
        this.email = email;
        this.programme = programme;
        this.elePoints = elePoints;
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

    public int getElePoints() {
        return elePoints;
    }

    public void setElePoints(int elePoints) {
        this.elePoints = elePoints;
    }
}
