package com.bivgroup.termination;
// Generated 02.11.2017 16:40:01 unknow unknow


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
import javax.persistence.Transient;

/**
 * ProdKindDeclaration Generated 02.11.2017 16:40:01 unknow unknow
 */
@Entity
@Table(name="HB_PRODKINDDECLARATION"
)
public class ProdKindDeclaration  implements java.io.Serializable {


    private Long id;
    private Long prodProgId;
    private Long prodVerId;
    private KindDeclaration kindDeclarationId_EN;
    private Long kindDeclarationId;
    private Long rowStatus;

    public ProdKindDeclaration() {
    }


    public ProdKindDeclaration(Long id) {
        this.id = id;
    }
    public ProdKindDeclaration(Long id, Long prodProgId, Long prodVerId, KindDeclaration kindDeclarationId_EN, Long kindDeclarationId, Long rowStatus) {
        this.id = id;
        this.prodProgId = prodProgId;
        this.prodVerId = prodVerId;
        this.kindDeclarationId_EN = kindDeclarationId_EN;
        this.kindDeclarationId = kindDeclarationId;
        this.rowStatus = rowStatus;
    }

    @SequenceGenerator(name = "generator", sequenceName = "HB_PRODKINDDECLARATION_SEQ", allocationSize = 10)
    @GeneratedValue(strategy = SEQUENCE, generator = "generator")
    @Id


    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Column(name="prodProgId")
    public Long getProdProgId() {
        return this.prodProgId;
    }

    public void setProdProgId(Long prodProgId) {
        this.prodProgId = prodProgId;
    }


    @Column(name="prodVerId")
    public Long getProdVerId() {
        return this.prodVerId;
    }

    public void setProdVerId(Long prodVerId) {
        this.prodVerId = prodVerId;
    }

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="kindDeclarationId")
    public KindDeclaration getKindDeclarationId_EN() {
        return this.kindDeclarationId_EN;
    }

    public void setKindDeclarationId_EN(KindDeclaration kindDeclarationId_EN) {
        this.kindDeclarationId_EN = kindDeclarationId_EN;
    }


    @Column(name="kindDeclarationId", insertable=false, updatable=false)
    public Long getKindDeclarationId() {
        return this.kindDeclarationId;
    }

    public void setKindDeclarationId(Long kindDeclarationId) {
        this.kindDeclarationId = kindDeclarationId;
    }

    @Transient

    public Long getRowStatus() {
        return this.rowStatus;
    }

    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


