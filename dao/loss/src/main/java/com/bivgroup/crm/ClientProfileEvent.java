package com.bivgroup.crm;
// Generated 12.01.2018 13:20:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * ClientProfileEvent Generated 12.01.2018 13:20:35 core-dictionary-maven-plugin Maven Mojo 1.01.03-SNAPSHOT
 */
@Entity
@Table(name="SD_ClientProfile_Event"
)
public class ClientProfileEvent  implements java.io.Serializable {


    private Long id;
    private String ipRoute;
    private Long refObjectId;
    private Integer paramDate10;
    private Integer paramDate9;
    private Integer paramDate8;
    private Integer paramDate7;
    private Integer paramDate6;
    private Integer paramDate5;
    private Integer paramDate4;
    private Integer paramDate3;
    private Integer paramDate2;
    private Integer paramDate1;
    private Integer paramBoolean10;
    private Integer paramBoolean9;
    private Integer paramBoolean8;
    private Integer paramBoolean7;
    private Integer paramBoolean6;
    private Integer paramBoolean5;
    private Integer paramBoolean4;
    private Integer paramBoolean3;
    private Integer paramBoolean2;
    private Integer paramBoolean1;
    private Float paramFloat10;
    private Float paramFloat9;
    private Float paramFloat8;
    private Float paramFloat7;
    private Float paramFloat6;
    private Float paramFloat5;
    private Float paramFloat4;
    private Float paramFloat3;
    private Float paramFloat2;
    private Float paramFloat1;
    private Long paramInt10;
    private Long paramInt9;
    private Long paramInt8;
    private Long paramInt7;
    private Long paramInt6;
    private Long paramInt5;
    private Long paramInt4;
    private Long paramInt3;
    private Long paramInt2;
    private Long paramInt1;
    private String paramStr10;
    private String paramStr9;
    private String paramStr8;
    private String paramStr7;
    private String paramStr6;
    private String paramStr5;
    private String paramStr4;
    private String paramStr3;
    private String paramStr2;
    private String paramStr1;
    private String errorTest;
    private String errorCode;
    private String signature;
    private Long modeApp;
    private Date eventDate;
    private Long eId;
    private String eventSource;
    private KindEventClientProfile eventTypeId_EN;
    private Long eventTypeId;
    private ClientProfile clientProfileId_EN;
    private Long clientProfileId;
    private ClientProfileToken tokenId_EN;
    private Long tokenId;
    private Long rowStatus;

    public ClientProfileEvent() {
    }


    public ClientProfileEvent(Long id) {
        this.id = id;
    }
    public ClientProfileEvent(Long id, String ipRoute, Long refObjectId, Integer paramDate10, Integer paramDate9, Integer paramDate8, Integer paramDate7, Integer paramDate6, Integer paramDate5, Integer paramDate4, Integer paramDate3, Integer paramDate2, Integer paramDate1, Integer paramBoolean10, Integer paramBoolean9, Integer paramBoolean8, Integer paramBoolean7, Integer paramBoolean6, Integer paramBoolean5, Integer paramBoolean4, Integer paramBoolean3, Integer paramBoolean2, Integer paramBoolean1, Float paramFloat10, Float paramFloat9, Float paramFloat8, Float paramFloat7, Float paramFloat6, Float paramFloat5, Float paramFloat4, Float paramFloat3, Float paramFloat2, Float paramFloat1, Long paramInt10, Long paramInt9, Long paramInt8, Long paramInt7, Long paramInt6, Long paramInt5, Long paramInt4, Long paramInt3, Long paramInt2, Long paramInt1, String paramStr10, String paramStr9, String paramStr8, String paramStr7, String paramStr6, String paramStr5, String paramStr4, String paramStr3, String paramStr2, String paramStr1, String errorTest, String errorCode, String signature, Long modeApp, Date eventDate, Long eId, String eventSource, KindEventClientProfile eventTypeId_EN, Long eventTypeId, ClientProfile clientProfileId_EN, Long clientProfileId, ClientProfileToken tokenId_EN, Long tokenId, Long rowStatus) {
        this.id = id;
        this.ipRoute = ipRoute;
        this.refObjectId = refObjectId;
        this.paramDate10 = paramDate10;
        this.paramDate9 = paramDate9;
        this.paramDate8 = paramDate8;
        this.paramDate7 = paramDate7;
        this.paramDate6 = paramDate6;
        this.paramDate5 = paramDate5;
        this.paramDate4 = paramDate4;
        this.paramDate3 = paramDate3;
        this.paramDate2 = paramDate2;
        this.paramDate1 = paramDate1;
        this.paramBoolean10 = paramBoolean10;
        this.paramBoolean9 = paramBoolean9;
        this.paramBoolean8 = paramBoolean8;
        this.paramBoolean7 = paramBoolean7;
        this.paramBoolean6 = paramBoolean6;
        this.paramBoolean5 = paramBoolean5;
        this.paramBoolean4 = paramBoolean4;
        this.paramBoolean3 = paramBoolean3;
        this.paramBoolean2 = paramBoolean2;
        this.paramBoolean1 = paramBoolean1;
        this.paramFloat10 = paramFloat10;
        this.paramFloat9 = paramFloat9;
        this.paramFloat8 = paramFloat8;
        this.paramFloat7 = paramFloat7;
        this.paramFloat6 = paramFloat6;
        this.paramFloat5 = paramFloat5;
        this.paramFloat4 = paramFloat4;
        this.paramFloat3 = paramFloat3;
        this.paramFloat2 = paramFloat2;
        this.paramFloat1 = paramFloat1;
        this.paramInt10 = paramInt10;
        this.paramInt9 = paramInt9;
        this.paramInt8 = paramInt8;
        this.paramInt7 = paramInt7;
        this.paramInt6 = paramInt6;
        this.paramInt5 = paramInt5;
        this.paramInt4 = paramInt4;
        this.paramInt3 = paramInt3;
        this.paramInt2 = paramInt2;
        this.paramInt1 = paramInt1;
        this.paramStr10 = paramStr10;
        this.paramStr9 = paramStr9;
        this.paramStr8 = paramStr8;
        this.paramStr7 = paramStr7;
        this.paramStr6 = paramStr6;
        this.paramStr5 = paramStr5;
        this.paramStr4 = paramStr4;
        this.paramStr3 = paramStr3;
        this.paramStr2 = paramStr2;
        this.paramStr1 = paramStr1;
        this.errorTest = errorTest;
        this.errorCode = errorCode;
        this.signature = signature;
        this.modeApp = modeApp;
        this.eventDate = eventDate;
        this.eId = eId;
        this.eventSource = eventSource;
        this.eventTypeId_EN = eventTypeId_EN;
        this.eventTypeId = eventTypeId;
        this.clientProfileId_EN = clientProfileId_EN;
        this.clientProfileId = clientProfileId;
        this.tokenId_EN = tokenId_EN;
        this.tokenId = tokenId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "SD_CLIENTPROFILE_EVENT_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name="ID", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name="IPROUTE")
    public String getIpRoute() {
        return this.ipRoute;
    }

    public void setIpRoute(String ipRoute) {
        this.ipRoute = ipRoute;
    }


    @Column(name="RefObjectID")
    public Long getRefObjectId() {
        return this.refObjectId;
    }

    public void setRefObjectId(Long refObjectId) {
        this.refObjectId = refObjectId;
    }


    @Column(name="ParamDate10")
    public Integer getParamDate10() {
        return this.paramDate10;
    }

    public void setParamDate10(Integer paramDate10) {
        this.paramDate10 = paramDate10;
    }


    @Column(name="ParamDate9")
    public Integer getParamDate9() {
        return this.paramDate9;
    }

    public void setParamDate9(Integer paramDate9) {
        this.paramDate9 = paramDate9;
    }


    @Column(name="ParamDate8")
    public Integer getParamDate8() {
        return this.paramDate8;
    }

    public void setParamDate8(Integer paramDate8) {
        this.paramDate8 = paramDate8;
    }


    @Column(name="ParamDate7")
    public Integer getParamDate7() {
        return this.paramDate7;
    }

    public void setParamDate7(Integer paramDate7) {
        this.paramDate7 = paramDate7;
    }


    @Column(name="ParamDate6")
    public Integer getParamDate6() {
        return this.paramDate6;
    }

    public void setParamDate6(Integer paramDate6) {
        this.paramDate6 = paramDate6;
    }


    @Column(name="ParamDate5")
    public Integer getParamDate5() {
        return this.paramDate5;
    }

    public void setParamDate5(Integer paramDate5) {
        this.paramDate5 = paramDate5;
    }


    @Column(name="ParamDate4")
    public Integer getParamDate4() {
        return this.paramDate4;
    }

    public void setParamDate4(Integer paramDate4) {
        this.paramDate4 = paramDate4;
    }


    @Column(name="ParamDate3")
    public Integer getParamDate3() {
        return this.paramDate3;
    }

    public void setParamDate3(Integer paramDate3) {
        this.paramDate3 = paramDate3;
    }


    @Column(name="ParamDate2")
    public Integer getParamDate2() {
        return this.paramDate2;
    }

    public void setParamDate2(Integer paramDate2) {
        this.paramDate2 = paramDate2;
    }


    @Column(name="ParamDate1")
    public Integer getParamDate1() {
        return this.paramDate1;
    }

    public void setParamDate1(Integer paramDate1) {
        this.paramDate1 = paramDate1;
    }


    @Column(name="ParamBoolean10")
    public Integer getParamBoolean10() {
        return this.paramBoolean10;
    }

    public void setParamBoolean10(Integer paramBoolean10) {
        this.paramBoolean10 = paramBoolean10;
    }


    @Column(name="ParamBoolean9")
    public Integer getParamBoolean9() {
        return this.paramBoolean9;
    }

    public void setParamBoolean9(Integer paramBoolean9) {
        this.paramBoolean9 = paramBoolean9;
    }


    @Column(name="ParamBoolean8")
    public Integer getParamBoolean8() {
        return this.paramBoolean8;
    }

    public void setParamBoolean8(Integer paramBoolean8) {
        this.paramBoolean8 = paramBoolean8;
    }


    @Column(name="ParamBoolean7")
    public Integer getParamBoolean7() {
        return this.paramBoolean7;
    }

    public void setParamBoolean7(Integer paramBoolean7) {
        this.paramBoolean7 = paramBoolean7;
    }


    @Column(name="ParamBoolean6")
    public Integer getParamBoolean6() {
        return this.paramBoolean6;
    }

    public void setParamBoolean6(Integer paramBoolean6) {
        this.paramBoolean6 = paramBoolean6;
    }


    @Column(name="ParamBoolean5")
    public Integer getParamBoolean5() {
        return this.paramBoolean5;
    }

    public void setParamBoolean5(Integer paramBoolean5) {
        this.paramBoolean5 = paramBoolean5;
    }


    @Column(name="ParamBoolean4")
    public Integer getParamBoolean4() {
        return this.paramBoolean4;
    }

    public void setParamBoolean4(Integer paramBoolean4) {
        this.paramBoolean4 = paramBoolean4;
    }


    @Column(name="ParamBoolean3")
    public Integer getParamBoolean3() {
        return this.paramBoolean3;
    }

    public void setParamBoolean3(Integer paramBoolean3) {
        this.paramBoolean3 = paramBoolean3;
    }


    @Column(name="ParamBoolean2")
    public Integer getParamBoolean2() {
        return this.paramBoolean2;
    }

    public void setParamBoolean2(Integer paramBoolean2) {
        this.paramBoolean2 = paramBoolean2;
    }


    @Column(name="ParamBoolean1")
    public Integer getParamBoolean1() {
        return this.paramBoolean1;
    }

    public void setParamBoolean1(Integer paramBoolean1) {
        this.paramBoolean1 = paramBoolean1;
    }


    @Column(name="ParamFloat10")
    public Float getParamFloat10() {
        return this.paramFloat10;
    }

    public void setParamFloat10(Float paramFloat10) {
        this.paramFloat10 = paramFloat10;
    }


    @Column(name="ParamFloat9")
    public Float getParamFloat9() {
        return this.paramFloat9;
    }

    public void setParamFloat9(Float paramFloat9) {
        this.paramFloat9 = paramFloat9;
    }


    @Column(name="ParamFloat8")
    public Float getParamFloat8() {
        return this.paramFloat8;
    }

    public void setParamFloat8(Float paramFloat8) {
        this.paramFloat8 = paramFloat8;
    }


    @Column(name="ParamFloat7")
    public Float getParamFloat7() {
        return this.paramFloat7;
    }

    public void setParamFloat7(Float paramFloat7) {
        this.paramFloat7 = paramFloat7;
    }


    @Column(name="ParamFloat6")
    public Float getParamFloat6() {
        return this.paramFloat6;
    }

    public void setParamFloat6(Float paramFloat6) {
        this.paramFloat6 = paramFloat6;
    }


    @Column(name="ParamFloat5")
    public Float getParamFloat5() {
        return this.paramFloat5;
    }

    public void setParamFloat5(Float paramFloat5) {
        this.paramFloat5 = paramFloat5;
    }


    @Column(name="ParamFloat4")
    public Float getParamFloat4() {
        return this.paramFloat4;
    }

    public void setParamFloat4(Float paramFloat4) {
        this.paramFloat4 = paramFloat4;
    }


    @Column(name="ParamFloat3")
    public Float getParamFloat3() {
        return this.paramFloat3;
    }

    public void setParamFloat3(Float paramFloat3) {
        this.paramFloat3 = paramFloat3;
    }


    @Column(name="ParamFloat2")
    public Float getParamFloat2() {
        return this.paramFloat2;
    }

    public void setParamFloat2(Float paramFloat2) {
        this.paramFloat2 = paramFloat2;
    }


    @Column(name="ParamFloat1")
    public Float getParamFloat1() {
        return this.paramFloat1;
    }

    public void setParamFloat1(Float paramFloat1) {
        this.paramFloat1 = paramFloat1;
    }


    @Column(name="ParamInt10")
    public Long getParamInt10() {
        return this.paramInt10;
    }

    public void setParamInt10(Long paramInt10) {
        this.paramInt10 = paramInt10;
    }


    @Column(name="ParamInt9")
    public Long getParamInt9() {
        return this.paramInt9;
    }

    public void setParamInt9(Long paramInt9) {
        this.paramInt9 = paramInt9;
    }


    @Column(name="ParamInt8")
    public Long getParamInt8() {
        return this.paramInt8;
    }

    public void setParamInt8(Long paramInt8) {
        this.paramInt8 = paramInt8;
    }


    @Column(name="ParamInt7")
    public Long getParamInt7() {
        return this.paramInt7;
    }

    public void setParamInt7(Long paramInt7) {
        this.paramInt7 = paramInt7;
    }


    @Column(name="ParamInt6")
    public Long getParamInt6() {
        return this.paramInt6;
    }

    public void setParamInt6(Long paramInt6) {
        this.paramInt6 = paramInt6;
    }


    @Column(name="ParamInt5")
    public Long getParamInt5() {
        return this.paramInt5;
    }

    public void setParamInt5(Long paramInt5) {
        this.paramInt5 = paramInt5;
    }


    @Column(name="ParamInt4")
    public Long getParamInt4() {
        return this.paramInt4;
    }

    public void setParamInt4(Long paramInt4) {
        this.paramInt4 = paramInt4;
    }


    @Column(name="ParamInt3")
    public Long getParamInt3() {
        return this.paramInt3;
    }

    public void setParamInt3(Long paramInt3) {
        this.paramInt3 = paramInt3;
    }


    @Column(name="ParamInt2")
    public Long getParamInt2() {
        return this.paramInt2;
    }

    public void setParamInt2(Long paramInt2) {
        this.paramInt2 = paramInt2;
    }


    @Column(name="ParamInt1")
    public Long getParamInt1() {
        return this.paramInt1;
    }

    public void setParamInt1(Long paramInt1) {
        this.paramInt1 = paramInt1;
    }


    @Column(name="ParamStr10")
    public String getParamStr10() {
        return this.paramStr10;
    }

    public void setParamStr10(String paramStr10) {
        this.paramStr10 = paramStr10;
    }


    @Column(name="ParamStr9")
    public String getParamStr9() {
        return this.paramStr9;
    }

    public void setParamStr9(String paramStr9) {
        this.paramStr9 = paramStr9;
    }


    @Column(name="ParamStr8")
    public String getParamStr8() {
        return this.paramStr8;
    }

    public void setParamStr8(String paramStr8) {
        this.paramStr8 = paramStr8;
    }


    @Column(name="ParamStr7")
    public String getParamStr7() {
        return this.paramStr7;
    }

    public void setParamStr7(String paramStr7) {
        this.paramStr7 = paramStr7;
    }


    @Column(name="ParamStr6")
    public String getParamStr6() {
        return this.paramStr6;
    }

    public void setParamStr6(String paramStr6) {
        this.paramStr6 = paramStr6;
    }


    @Column(name="ParamStr5")
    public String getParamStr5() {
        return this.paramStr5;
    }

    public void setParamStr5(String paramStr5) {
        this.paramStr5 = paramStr5;
    }


    @Column(name="ParamStr4")
    public String getParamStr4() {
        return this.paramStr4;
    }

    public void setParamStr4(String paramStr4) {
        this.paramStr4 = paramStr4;
    }


    @Column(name="ParamStr3")
    public String getParamStr3() {
        return this.paramStr3;
    }

    public void setParamStr3(String paramStr3) {
        this.paramStr3 = paramStr3;
    }


    @Column(name="ParamStr2")
    public String getParamStr2() {
        return this.paramStr2;
    }

    public void setParamStr2(String paramStr2) {
        this.paramStr2 = paramStr2;
    }


    @Column(name="ParamStr1")
    public String getParamStr1() {
        return this.paramStr1;
    }

    public void setParamStr1(String paramStr1) {
        this.paramStr1 = paramStr1;
    }


    @Column(name="ErrorTest")
    public String getErrorTest() {
        return this.errorTest;
    }

    public void setErrorTest(String errorTest) {
        this.errorTest = errorTest;
    }


    @Column(name="ErrorCode")
    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    @Column(name="Signature")
    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }


    @Column(name="ModeApp")
    public Long getModeApp() {
        return this.modeApp;
    }

    public void setModeApp(Long modeApp) {
        this.modeApp = modeApp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EventDate")
    public Date getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }


    @Column(name="EID")
    public Long geteId() {
        return this.eId;
    }

    public void seteId(Long eId) {
        this.eId = eId;
    }


    @Column(name="EventSource")
    public String getEventSource() {
        return this.eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="EventTypeID")
    public KindEventClientProfile getEventTypeId_EN() {
        return this.eventTypeId_EN;
    }

    public void setEventTypeId_EN(KindEventClientProfile eventTypeId_EN) {
        this.eventTypeId_EN = eventTypeId_EN;
    }


    @Column(name="EventTypeID", insertable=false, updatable=false)
    public Long getEventTypeId() {
        return this.eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="ClientProfileID")
    public ClientProfile getClientProfileId_EN() {
        return this.clientProfileId_EN;
    }

    public void setClientProfileId_EN(ClientProfile clientProfileId_EN) {
        this.clientProfileId_EN = clientProfileId_EN;
    }


    @Column(name="ClientProfileID", insertable=false, updatable=false)
    public Long getClientProfileId() {
        return this.clientProfileId;
    }

    public void setClientProfileId(Long clientProfileId) {
        this.clientProfileId = clientProfileId;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="TokenID")
    public ClientProfileToken getTokenId_EN() {
        return this.tokenId_EN;
    }

    public void setTokenId_EN(ClientProfileToken tokenId_EN) {
        this.tokenId_EN = tokenId_EN;
    }


    @Column(name="TokenID", insertable=false, updatable=false)
    public Long getTokenId() {
        return this.tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


