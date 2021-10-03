
package domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@EntityListeners(AuditableListener.class)
public class Device implements Serializable, Auditable{
    @Id
    private String deviceId = UUID.randomUUID().toString();
    @Column(unique = true)
    private String serialNumber;
    private String specification;
    private String deviceName;
    private String deviceType;
    @Temporal(TemporalType.DATE)
    private final Date registrationDate = new Date();
    @Enumerated(EnumType.STRING)
    private EMovementStatus movementStatus;
    @Column(unique = true)
    private String rfid;
    private Date dateCreated;
    private Date dateupdated;
    @ManyToOne
    private Person createdBy;
    @ManyToOne
    private Person updatedBy;
    
    @ManyToOne
    private Student student;
   
    
    @OneToMany(mappedBy = "device", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<Movement> movement;
    
    @OneToMany(mappedBy = "device", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<DeviceImage> deviceImage;
    
    @ManyToOne
    private Person person;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
   
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public List<Movement> getMovement() {
        return movement;
    }

    public void setMovement(List<Movement> movement) {
        this.movement = movement;
    }

    public List<DeviceImage> getDeviceImage() {
        return deviceImage;
    }

    public void setDeviceImage(List<DeviceImage> deviceImage) {
        this.deviceImage = deviceImage;
    }


    public EMovementStatus getMovementStatus() {
        return movementStatus;
    }

    public void setMovementStatus(EMovementStatus movementStatus) {
        this.movementStatus = movementStatus;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    @Override
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public Date getDateUpdated() {
        return dateupdated;
    }

    @Override
    public void setDateUpdated(Date lastUpdated) {
        this.dateupdated = lastUpdated;
    }

    public Person getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Person createdBy) {
        this.createdBy = createdBy;
    }

    public Person getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Person updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    
}
