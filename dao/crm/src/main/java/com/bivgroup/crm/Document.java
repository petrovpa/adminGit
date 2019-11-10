package com.bivgroup.crm;
// Generated 31.01.2018 18:17:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Document Generated 31.01.2018 18:17:44 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT 
 */
@Entity
@Inheritance(strategy=InheritanceType.JOINED)       
@DiscriminatorColumn(
    name="DISCRIMINATOR"
     )
@Table(name="SD_Document"
)
public class Document  implements java.io.Serializable {


     private Long id;
     private Date dateStayUntil;
     private Date dateStayFrom;
     private Date dateOfExpiry;
     private String issuerCode;
     private String authority;
     private Date dateOfIssue;
     private String no;
     private String series;
     private Long eId;
     private KindDocument typeId_EN;
     private Long typeId;
     private Long rowStatus;

    public Document() {
    }

	
    public Document(Long id) {
        this.id = id;
    }
    public Document(Long id, Date dateStayUntil, Date dateStayFrom, Date dateOfExpiry, String issuerCode, String authority, Date dateOfIssue, String no, String series, Long eId, KindDocument typeId_EN, Long typeId, Long rowStatus) {
       this.id = id;
       this.dateStayUntil = dateStayUntil;
       this.dateStayFrom = dateStayFrom;
       this.dateOfExpiry = dateOfExpiry;
       this.issuerCode = issuerCode;
       this.authority = authority;
       this.dateOfIssue = dateOfIssue;
       this.no = no;
       this.series = series;
       this.eId = eId;
       this.typeId_EN = typeId_EN;
       this.typeId = typeId;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "SD_DOCUMENT_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="dateStayUntil")
    public Date getDateStayUntil() {
        return this.dateStayUntil;
    }
    
    public void setDateStayUntil(Date dateStayUntil) {
        this.dateStayUntil = dateStayUntil;
    }

    
    @Column(name="dateStayFrom")
    public Date getDateStayFrom() {
        return this.dateStayFrom;
    }
    
    public void setDateStayFrom(Date dateStayFrom) {
        this.dateStayFrom = dateStayFrom;
    }

    
    @Column(name="DateOfExpiry")
    public Date getDateOfExpiry() {
        return this.dateOfExpiry;
    }
    
    public void setDateOfExpiry(Date dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    
    @Column(name="IssuerCode")
    public String getIssuerCode() {
        return this.issuerCode;
    }
    
    public void setIssuerCode(String issuerCode) {
        this.issuerCode = issuerCode;
    }

    
    @Column(name="Authority")
    public String getAuthority() {
        return this.authority;
    }
    
    public void setAuthority(String authority) {
        this.authority = authority;
    }

    
    @Column(name="DateOfIssue")
    public Date getDateOfIssue() {
        return this.dateOfIssue;
    }
    
    public void setDateOfIssue(Date dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    
    @Column(name="No")
    public String getNo() {
        return this.no;
    }
    
    public void setNo(String no) {
        this.no = no;
    }

    
    @Column(name="Series")
    public String getSeries() {
        return this.series;
    }
    
    public void setSeries(String series) {
        this.series = series;
    }

    
    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }
    
    public void seteId(Long eId) {
        this.eId = eId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TypeID")
    public KindDocument getTypeId_EN() {
        return this.typeId_EN;
    }
    
    public void setTypeId_EN(KindDocument typeId_EN) {
        this.typeId_EN = typeId_EN;
    }

    
    @Column(name="TypeID", insertable=false, updatable=false)
    public Long getTypeId() {
        return this.typeId;
    }
    
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


