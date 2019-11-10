package com.bivgroup.messages;
// Generated Jan 31, 2018 3:51:03 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Employee Generated Jan 31, 2018 3:51:03 PM core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Table(name="DEP_EMPLOYEE"
)
public class Employee  implements java.io.Serializable {


     private Long id;
     private String firstName;
     private String middleName;
     private String lastName;
     private String email;
     private String phone2;
     private String phone1;
     private Long manager;
     private String status;
     private Date endWorkDate;
     private Date startWorkDate;
     private Long departmentId;
     private String position;
     private String code;
     private Long participantId;
     private Long rowStatus;

    public Employee() {
    }

	
    public Employee(Long id) {
        this.id = id;
    }
    public Employee(Long id, String firstName, String middleName, String lastName, String email, String phone2, String phone1, Long manager, String status, Date endWorkDate, Date startWorkDate, Long departmentId, String position, String code, Long participantId, Long rowStatus) {
       this.id = id;
       this.firstName = firstName;
       this.middleName = middleName;
       this.lastName = lastName;
       this.email = email;
       this.phone2 = phone2;
       this.phone1 = phone1;
       this.manager = manager;
       this.status = status;
       this.endWorkDate = endWorkDate;
       this.startWorkDate = startWorkDate;
       this.departmentId = departmentId;
       this.position = position;
       this.code = code;
       this.participantId = participantId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "DEP_EMPLOYEE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="EMPLOYEEID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="FIRSTNAME")
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    
    @Column(name="MIDDLENAME")
    public String getMiddleName() {
        return this.middleName;
    }
    
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    
    @Column(name="LASTNAME")
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    
    @Column(name="EMAIL")
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    
    @Column(name="PHONE2")
    public String getPhone2() {
        return this.phone2;
    }
    
    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    
    @Column(name="PHONE1")
    public String getPhone1() {
        return this.phone1;
    }
    
    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    
    @Column(name="MANAGER")
    public Long getManager() {
        return this.manager;
    }
    
    public void setManager(Long manager) {
        this.manager = manager;
    }

    
    @Column(name="STATUS")
    public String getStatus() {
        return this.status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }

    
    @Column(name="ENDWORKDATE")
    public Date getEndWorkDate() {
        return this.endWorkDate;
    }
    
    public void setEndWorkDate(Date endWorkDate) {
        this.endWorkDate = endWorkDate;
    }

    
    @Column(name="STARTWORKDATE")
    public Date getStartWorkDate() {
        return this.startWorkDate;
    }
    
    public void setStartWorkDate(Date startWorkDate) {
        this.startWorkDate = startWorkDate;
    }

    
    @Column(name="DEPARTMENTID")
    public Long getDepartmentId() {
        return this.departmentId;
    }
    
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    
    @Column(name="POSITION")
    public String getPosition() {
        return this.position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }

    
    @Column(name="CODE")
    public String getCode() {
        return this.code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

    
    @Column(name="PARTICIPANTID")
    public Long getParticipantId() {
        return this.participantId;
    }
    
    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


