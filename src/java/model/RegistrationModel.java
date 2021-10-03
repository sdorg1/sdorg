
package model;

import common.FileUpload;
import common.PassCode;
import dao.AdminDao;
import dao.UniversityDao;
import dao.UserDao;
import domain.Admin;
import domain.EStatus;
import domain.EUserType;
import domain.University;
import domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class RegistrationModel {
    
    private University university = new University();
    private User user = new User();
    private List<String> chosenImage = new ArrayList<>();
    private String password = new String();
    
    public void registerUniversity() throws Exception {

        if (chosenImage.isEmpty()) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Upload Profile Image"));
        } else {
            chosenImage.forEach((x) -> {
                university.setProfilePicture(x);
            });
            chosenImage.clear();
            new UniversityDao().register(university);

            Admin admin = new Admin();
            //Attribute To be Removed Afterwards
            admin.setEmail(university.getEmail());
            admin.setFirstName(university.getName());
            admin.setLastName(university.getName());
            admin.setPhone(university.getPhoneNumber());
            admin.setProfilePicture(university.getProfilePicture());
            admin.setUniversity(university);
            new AdminDao().register(admin);

            user.setAdmin(admin);
            user.setStatus(EStatus.ACTIVE);
            user.setUserType(EUserType.ADMIN);
            user.setPassword(new PassCode().encrypt(password));
            new UserDao().register(user);
            user = new User();
            university = new University();
            
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("University Registered"));
        }

    }

    public void Upload(FileUploadEvent event) {
        chosenImage.add(new FileUpload().Upload(event, "C:\\Users\\Joshua-TB\\Documents\\NetBeansProjects\\SDORG_App-master\\web\\uploads\\profile\\"));
    }

    
    public University getUniversity() {
        return university;
    }

    public void setUniversity(University university) {
        this.university = university;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getChosenImage() {
        return chosenImage;
    }

    public void setChosenImage(List<String> chosenImage) {
        this.chosenImage = chosenImage;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
