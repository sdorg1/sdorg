
package dao;

import domain.Device;
import domain.DeviceImage;
import domain.Lecturer;
import domain.Person;
import domain.Staff;
import domain.Student;
import domain.Visitor;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

public class DeviceImageDao extends GenericDao<DeviceImage>{
    public List<DeviceImage> findByPerson(Person x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device.person = :x GROUP BY a.device");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    
    public List<DeviceImage> findByStudent(Student x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device.person = :x");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    public List<DeviceImage> findByStaff(Staff x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device.person = :x");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    
    public List<DeviceImage> findByLecturer(Lecturer x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device.person = :x");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    public List<DeviceImage> findByVisitor(Visitor x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device.visitor = :x");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    
    public List<DeviceImage> findByDevice(Device x){
        Session s = HibernateUtil.getSessionFactory().openSession();
        Query q = s.createQuery("SELECT a FROM DeviceImage a WHERE a.device = :x");
        q.setParameter("x", x);
        List<DeviceImage> list = q.list();
        s.close();
        return list;
    }
    
}
