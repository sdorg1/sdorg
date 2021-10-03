package model;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import common.FileUpload;
import common.PassCode;
import dao.AccusationDao;
import dao.DeviceDao;
import dao.DeviceImageDao;
import dao.MovementDao;
import dao.PersonDao;
import dao.StudentDao;
import dao.UserDao;
import dao.VisitorDao;
import domain.Accusation;
import domain.Device;
import domain.DeviceImage;
import domain.EGender;
import domain.EMovementStatus;
import domain.EStatus;
import domain.EUserType;
import domain.Movement;
import domain.Person;
import domain.Student;
import domain.User;
import domain.Visitor;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;

@ManagedBean
@SessionScoped
public class SecurityModel {

    private User loggedInUser = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
    private Visitor visitor = new Visitor();
    private List<String> chosenImage = new ArrayList<>();
    private List<DeviceImage> visitorDevices = new ArrayList<>();
    private String password = new String();
    private String gender = new String();
    private User user = new User();
    private Device device = new Device();
    private Person chosenPerson = new Person();
    private List<Person> persons = new PersonDao().findAll(Person.class);
    private DeviceImage chosenDeviceImage = new DeviceImage();
    private List<Movement> universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_IN);
    private List<Movement> alertedDevices = new ArrayList<>();
    private String alertMessage = new String();
    private String alertMessageCheckout = new String();
    private Accusation chosenAccusation = new Accusation();
    private boolean alerted = Boolean.FALSE;
    private String rfid = new String();
    private Device chosenDevice = new Device();
    private List<DeviceImage> chosenDeviceImages = new ArrayList<>();
    private String inComments = new String();
    private String outComments = new String();

    @PostConstruct
    public void init() {
        loggedInUser = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_IN);
        check24HAlert();
    }

    public void registerVisitor() throws Exception {

        if (gender.matches("Male")) {
            visitor.setGender(EGender.MALE);
        } else {
            visitor.setGender(EGender.FEMALE);
        }
        if (new UserDao().usernameExist(user.getUsername())) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Username already exists"));
        } else {
            visitor.setPersonType("Visitor");
            new VisitorDao().register(visitor);
            persons = new PersonDao().findAll(Person.class);

            user.setVisitor(visitor);
            user.setStatus(EStatus.ACTIVE);
            user.setUserType(EUserType.VISITOR);
            user.setPassword(new PassCode().encrypt(password));
            new UserDao().register(user);
            user = new User();
            visitor = new Visitor();

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Visitor Registered"));
        }
    }

    public void check24HAlert() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = null;
        long diff;
        long diffSeconds;
        try {
            String strDate = sdf.format(new Date());
            Calendar cal = new GregorianCalendar();
            cal.setTime(sdf.parse(strDate));
//            cal.add(Calendar.HOUR_OF_DAY, 24);
            String t1 = sdf.format(cal.getTime());
            Date d2 = sdf.parse(t1);
            alertedDevices.clear();
            for (Movement m : universityDevices) {
//                d1 = sdf.parse(m.getEntranceTime().toString());
                d1 = m.getEntranceTime();
                diff = d2.getTime() - d1.getTime();
                diffSeconds = diff / 1000;
                if (diffSeconds > 86400) {
                    alertedDevices.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchByRfid() {
        chosenDevice = new DeviceDao().findByRfid(rfid);
        if (chosenDevice == null || chosenDevice.equals(null) || chosenDevice.equals("")) {
            alertMessage = "No device with such RFID Found";
            alerted = Boolean.FALSE;
        } else if (chosenDevice.getMovementStatus().equals(EMovementStatus.CHECKED_IN)) {
            chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            alertMessageCheckout = "This device is checked-in";
        } else if (chosenAccusation == null || chosenAccusation.equals(null) || chosenAccusation.equals("")) {
            chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            alertMessage = "This device is free to enter";
            alerted = Boolean.FALSE;
        } else {
            chosenAccusation = new AccusationDao().findByDeviceAndStatus(chosenDevice);
            if (chosenAccusation == null) {
                alertMessage = "This device is free to enter";
                alerted = Boolean.FALSE;
                chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            } else {
                alertMessage = "This device is alerted in Complaints";
                alerted = Boolean.TRUE;
                chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            }
        }
    }

    public void searchByRfidCheckout() {
        chosenDevice = new DeviceDao().findByRfid(rfid);
        if (chosenDevice == null || chosenDevice.equals(null) || chosenDevice.equals("")) {
            alertMessageCheckout = "No device with such RFID Found";
            chosenDevice = new Device();
            chosenAccusation = new Accusation();
        } else if (chosenDevice.getMovementStatus().equals(EMovementStatus.CHECKED_OUT)) {
            chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            alertMessageCheckout = "This device is checked-out";
        } else {
            chosenAccusation = new AccusationDao().findByDeviceAndStatus(chosenDevice);
            if (chosenAccusation == null) {
                alertMessageCheckout = "This device is free to exit";
                chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            } else {
                alertMessage = "This device is alerted in Complaints";
                chosenDeviceImages = new DeviceImageDao().findByDevice(chosenDevice);
            }
        }
    }

    public void updateRfid() {
        new DeviceDao().update(chosenDevice);

        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage("Device RFID Updated"));
    }

    public String navigateToRegisterVisitorDevice(Person person) {
        chosenPerson = person;
        visitorDevices = new DeviceImageDao().findByPerson(person);
        return "devices.xhtml?faces-redirect=true";
    }

    public String navigateToDevice(DeviceImage device) {
        chosenDeviceImage = device;
        chosenDevice = device.getDevice();
        chosenAccusation = new AccusationDao().findByDeviceAndStatus(device.getDevice());
        if (chosenAccusation == null) {
            alertMessage = "This device is free to enter";
            alerted = Boolean.FALSE;
        } else {
            alertMessage = "This device is alerted in Complaints";
            alerted = Boolean.TRUE;
        }
        System.out.println(alertMessage);
        return "check-in.xhtml?faces-redirect=true";
    }

    public void registerVisitorDevice() {
        try {
            if (chosenImage.isEmpty()) {
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage("Upload Device Image"));
            } else {
                device.setMovementStatus(EMovementStatus.CHECKED_OUT);
                device.setPerson(chosenPerson);
                device.setCreatedBy(loggedInUser.getStaff());
                device.setDateCreated(new Date());
                device.setUpdatedBy(loggedInUser.getStaff());
                device.setDateUpdated(new Date());
                new DeviceDao().register(device);

                DeviceImage deviceImage = new DeviceImage();
                for (String x : chosenImage) {
                    deviceImage.setPath(x);
                    deviceImage.setDevice(device);
                    new DeviceImageDao().register(deviceImage);
                }
                chosenImage.clear();
                visitorDevices = new DeviceImageDao().findByPerson(chosenPerson);
                device = new Device();
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage("Device Registered"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkInDevice() {
        Movement movement = new Movement();
        movement.setDevice(chosenDeviceImage.getDevice());
        movement.setEntranceTime(new Date());
        movement.setMovementStatus(EMovementStatus.CHECKED_IN);
        movement.setUniversity(loggedInUser.getStaff().getUniversity());
        movement.setCreatedBy(loggedInUser.getStaff());
        movement.setDateCreated(new Date());
        //movement.setDateUpdated(new Date());
        //movement.setUpdatedBy(loggedInUser.getStaff());
        new MovementDao().register(movement);

        Device device = chosenDeviceImage.getDevice();
        device.setMovementStatus(EMovementStatus.CHECKED_IN);
        new DeviceDao().update(device);

        visitorDevices = new DeviceImageDao().findByPerson(chosenPerson);
        universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_IN);

        FacesContext fc = FacesContext.getCurrentInstance();
        fc.addMessage(null, new FacesMessage("Device Checked-In"));
    }

    public void checkInDeviceByRFID() {
        try {
            Movement movement = new Movement();
            movement.setDevice(chosenDevice);
            movement.setEntranceTime(new Date());
            movement.setMovementStatus(EMovementStatus.CHECKED_IN);
            movement.setUniversity(loggedInUser.getStaff().getUniversity());
            movement.setCreatedBy(loggedInUser.getStaff());
            movement.setDateCreated(new Date());
            //movement.setDateUpdated(new Date());
            //movement.setUpdatedBy(loggedInUser.getStaff());
            movement.setInComment(inComments);
            new MovementDao().register(movement);

            chosenDevice.setMovementStatus(EMovementStatus.CHECKED_IN);
            new DeviceDao().update(chosenDevice);
            universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_IN);
            movement = new Movement();
            inComments = new String();
            chosenDevice = new Device();
//        visitorDevices = new DeviceImageDao().findByPerson(chosenPerson);

            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Device Checked-In"));
        } catch (Exception e) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Device Checked-In"));
        }

    }

//    public void checkOutDevice(Movement movement) {
//        movement.setExitTime(new Date());
//        movement.setMovementStatus(EMovementStatus.CHECKED_OUT);
//        movement.setDateUpdated(new Date());
//        movement.setUpdatedBy(loggedInUser.getStaff());
//        new MovementDao().update(movement);
//
//        Device device = movement.getDevice();
//        device.setMovementStatus(EMovementStatus.CHECKED_OUT);
//        new DeviceDao().update(device);
//        universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_IN);
//
//        FacesContext fc = FacesContext.getCurrentInstance();
//        fc.addMessage(null, new FacesMessage("Device Checked-Out"));
//    }
    public void checkOutDevice() {

        Movement movement = new MovementDao().findByDeviceAndStatus(chosenDevice, EMovementStatus.CHECKED_IN);
        try {
            if (!movement.getCreatedBy().getPersonType().equals(loggedInUser.getStaff().getPersonType())) {
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage("You are note allow to CHECK OUT This Device"));
            } else {
                movement.setExitTime(new Date());
                movement.setMovementStatus(EMovementStatus.CHECKED_OUT);
                movement.setDateUpdated(new Date());
                movement.setUpdatedBy(loggedInUser.getStaff());
                movement.setOutComment(outComments);
                new MovementDao().update(movement);

                Device device = movement.getDevice();
                device.setMovementStatus(EMovementStatus.CHECKED_OUT);
                new DeviceDao().update(device);
                FacesContext fc = FacesContext.getCurrentInstance();
                fc.addMessage(null, new FacesMessage("Device Checked-Out"));
                universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_OUT);

                movement = new Movement();
            outComments = new String();
            chosenDevice = new Device();
            }
        } catch (Exception e) {
            FacesContext fc = FacesContext.getCurrentInstance();
            fc.addMessage(null, new FacesMessage("Device Checked-Out"));
            universityDevices = new MovementDao().findByUniversity(loggedInUser.getStaff().getUniversity(), EMovementStatus.CHECKED_OUT);

        }

    }
    
    

    public void generateaccusationreport() throws FileNotFoundException, DocumentException, BadElementException, IOException, Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  -  HH:mm");

        FacesContext context = FacesContext.getCurrentInstance();
        Document document = new Document();
        Rectangle rect = new Rectangle(0, 0, PageSize.A4.getRight(), PageSize.A4.getTop());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance((com.lowagie.text.Document) document, baos);
        writer.setBoxSize("art", rect);
        writer.setPageEvent(new MyPdfPageEventHelper());
        document.setPageSize(rect);
        if (!document.isOpen()) {
            document.open();
        }
        String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\reportDesign");
        
        Font font0 = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
        Font font1 = new Font(Font.TIMES_ROMAN, 14, Font.ITALIC, new Color(37, 46, 158));
        Font font2 = new Font(Font.TIMES_ROMAN, 9, Font.BOLD, new Color(19, 41, 79));
        Font font5 = new Font(Font.TIMES_ROMAN, 10, Font.ITALIC, new Color(0, 0, 0));
        Font colorFont = new Font(Font.TIMES_ROMAN, 14, Font.BOLD, new Color(0, 0, 0));
        Font font6 = new Font(Font.TIMES_ROMAN, 9, Font.NORMAL);
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n" + "\n"));
//        
        Paragraph p = new Paragraph("ALERTED DEVICES CHECKED IN FOR 24 REPORT ", colorFont);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("           " + sdf.format(new Date())));
        document.add(new Paragraph("\n" + "\n" + "\n" + "\n" + "\n"));
//        

        PdfPTable tables = new PdfPTable(6);
        tables.setWidthPercentage(100);

        PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
        cell1.setBorder(Rectangle.BOX);
        tables.addCell(cell1);

        PdfPCell c2 = new PdfPCell(new Phrase("Device Name", font2));
        c2.setBorder(Rectangle.BOX);
        tables.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase("Device Type", font2));
        c3.setBorder(Rectangle.BOX);
        tables.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase("Entrance Time", font2));
        c4.setBorder(Rectangle.BOX);
        tables.addCell(c4);

        PdfPCell c5 = new PdfPCell(new Phrase("Owner Names", font2));
        c5.setBorder(Rectangle.BOX);
        tables.addCell(c5);
        
        PdfPCell c6 = new PdfPCell(new Phrase("Owner Phone", font2));
        c6.setBorder(Rectangle.BOX);
        tables.addCell(c6);

        tables.setHeaderRows(1);
        PdfPCell pdfc5;
        PdfPCell pdfc1;
        PdfPCell pdfc3;
        PdfPCell pdfc2;
        PdfPCell pdfc4;
        PdfPCell pdfc6;
        PdfPCell pdfc7;
        PdfPCell pdfc8;
        int i = 1;
        DecimalFormat dcf = new DecimalFormat("###,###,###");
        for (Movement x : alertedDevices) {
            pdfc5 = new PdfPCell(new Phrase(i + ""));
            pdfc5.setBorder(Rectangle.BOX);
            tables.addCell(pdfc5);

            pdfc4 = new PdfPCell(new Phrase(x.getDevice().getDeviceName() + "", font6));
            pdfc4.setBorder(Rectangle.BOX);
            tables.addCell(pdfc4);

            pdfc3 = new PdfPCell(new Phrase(x.getDevice().getDeviceType() + "", font6));
            pdfc3.setBorder(Rectangle.BOX);
            tables.addCell(pdfc3);

            pdfc2 = new PdfPCell(new Phrase(x.getEntranceTime() + "", font6));
            pdfc2.setBorder(Rectangle.BOX);
            tables.addCell(pdfc2);

            pdfc1 = new PdfPCell(new Phrase(x.getDevice().getPerson().getFirstName() + " " + x.getDevice().getPerson().getLastName() + "", font6));
            pdfc1.setBorder(Rectangle.BOX);
            tables.addCell(pdfc1);

            pdfc6 = new PdfPCell(new Phrase(x.getDevice().getPerson().getPhone() + "", font6));
            pdfc6.setBorder(Rectangle.BOX);
            tables.addCell(pdfc6);
            
            i++;
        }
        document.add(tables);
//        
        document.close();
        String fileName = (loggedInUser.getUsername()).toUpperCase()+" ALERTED DEVICES CHECKED IN FOR 24h  Report_" + sdf.format(new Date());
        writePDFToResponse(context.getExternalContext(), baos, fileName);
        context.responseComplete();
    }

    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException {
        externalContext.responseReset();
        externalContext.setResponseContentType("application/pdf");
        externalContext.setResponseHeader("Expires", "0");
        externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        externalContext.setResponseHeader("Pragma", "public");
        externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
        externalContext.setResponseContentLength(baos.size());
        OutputStream out = externalContext.getResponseOutputStream();
        baos.writeTo(out);
        externalContext.responseFlushBuffer();
    }
    
    class MyPdfPageEventHelper extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter pdfWriter, Document document) {

//            System.out.println("Creating Waterwark Image in PDF");
            try {
                String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\reportDesign");
                path = path.substring(0, path.indexOf("\\build"));
                path = path + "\\web\\reportDesign\\SDORGReportPage.jpg";
                Image waterMarkImage = Image.getInstance(path);

                //Get width and height of whole page
                float pdfPageWidth = document.getPageSize().getWidth();
                float pdfPageHeight = document.getPageSize().getHeight();

                //Set waterMarkImage on whole page
                pdfWriter.getDirectContentUnder().addImage(waterMarkImage,
                        pdfPageWidth, 0, 0, pdfPageHeight, 0, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void upload(FileUploadEvent event) {
        chosenImage.add(new FileUpload().Upload(event, "C:\\Users\\Joshua-TB\\Documents\\NetBeansProjects\\SDORG_App-master\\web\\uploads\\device\\"));
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Visitor getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor visitor) {
        this.visitor = visitor;
    }

    public List<String> getChosenImage() {
        return chosenImage;
    }

    public void setChosenImage(List<String> chosenImage) {
        this.chosenImage = chosenImage;
    }

    public List<DeviceImage> getVisitorDevices() {
        return visitorDevices;
    }

    public void setVisitorDevices(List<DeviceImage> visitorDevices) {
        this.visitorDevices = visitorDevices;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Person getChosenPerson() {
        return chosenPerson;
    }

    public void setChosenPerson(Person chosenPerson) {
        this.chosenPerson = chosenPerson;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    public DeviceImage getChosenDeviceImage() {
        return chosenDeviceImage;
    }

    public void setChosenDeviceImage(DeviceImage chosenDeviceImage) {
        this.chosenDeviceImage = chosenDeviceImage;
    }

    public List<Movement> getUniversityDevices() {
        return universityDevices;
    }

    public void setUniversityDevices(List<Movement> universityDevices) {
        this.universityDevices = universityDevices;
    }

    public List<Movement> getAlertedDevices() {
        return alertedDevices;
    }

    public void setAlertedDevices(List<Movement> alertedDevices) {
        this.alertedDevices = alertedDevices;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public Accusation getChosenAccusation() {
        return chosenAccusation;
    }

    public void setChosenAccusation(Accusation chosenAccusation) {
        this.chosenAccusation = chosenAccusation;
    }

    public boolean isAlerted() {
        return alerted;
    }

    public void setAlerted(boolean alerted) {
        this.alerted = alerted;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public Device getChosenDevice() {
        return chosenDevice;
    }

    public void setChosenDevice(Device chosenDevice) {
        this.chosenDevice = chosenDevice;
    }

    public List<DeviceImage> getChosenDeviceImages() {
        return chosenDeviceImages;
    }

    public void setChosenDeviceImages(List<DeviceImage> chosenDeviceImages) {
        this.chosenDeviceImages = chosenDeviceImages;
    }

    public String getAlertMessageCheckout() {
        return alertMessageCheckout;
    }

    public void setAlertMessageCheckout(String alertMessageCheckout) {
        this.alertMessageCheckout = alertMessageCheckout;
    }

    public List<Movement> getInComm() {
        return new MovementDao().findByDeviceforComment(chosenDevice);
    }

    public String getInComments() {
        return inComments;
    }

    public void setInComments(String inComments) {
        this.inComments = inComments;
    }

    public String getOutComments() {
        return outComments;
    }

    public void setOutComments(String outComments) {
        this.outComments = outComments;
    }

    
}
