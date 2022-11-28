package danielryansunjaya.finalyearproject.eventhotspot.models;

public class EventModel {
    String title, date, email, location, organizer, pic, time;
    int elePoint;
    Boolean attended, completed;

    public EventModel(){
    }

    public EventModel(String title, String date, String email, String location, String organizer, String pic, String time, int elePoint) {
        this.title = title;
        this.date = date;
        this.email = email;
        this.location = location;
        this.organizer = organizer;
        this.pic = pic;
        this.time = time;
        this.elePoint = elePoint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getElePoint() {
        return elePoint;
    }

    public void setElePoint(int elePoint) {
        this.elePoint = elePoint;
    }

    public Boolean getAttended() {
        return attended;
    }

    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
