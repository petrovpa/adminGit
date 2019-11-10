package com.bivgroup.loss;
// Generated Aug 7, 2017 10:42:23 AM unknow unknow 


import com.bivgroup.core.annotation.IsArray;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.SEQUENCE;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * CategoryOfDamageOnInsProduct Generated Aug 7, 2017 10:42:23 AM unknow unknow 
 */
@Entity
@Table(name="PF_CATEGORYOFDAMAGE"
)
public class CategoryOfDamageOnInsProduct  implements java.io.Serializable {


     private Long id;
     private Integer isNotRequired;
     private String question;
     private String questionHint;
     private String answer;
     private String answerHint;
     private Integer isDataDefined;
     private Integer attrBoolean1;
     private Integer attrBoolean2;
     private Integer attrBoolean3;
     private Integer attrBoolean4;
     private Integer attrBoolean5;
     private Integer attrBoolean6;
     private Integer attrBoolean7;
     private Integer attrBoolean8;
     private Integer attrBoolean9;
     private Long groupInsObjectId;
     private Long insObjectId;
     private Long insRiskId;
     private SettingCategoryOfDamageOnInsProduct settingId_EN;
     private Long settingId;
     private CategoryOfDamageOnInsProduct parentId_EN;
     private Long parentId;
     private Set<CategoryOfDamageOnInsProduct> subQuestions = new HashSet<CategoryOfDamageOnInsProduct>(0);
     private Long rowStatus;

    public CategoryOfDamageOnInsProduct() {
    }

	
    public CategoryOfDamageOnInsProduct(Long id) {
        this.id = id;
    }
    public CategoryOfDamageOnInsProduct(Long id, Integer isNotRequired, String question, String questionHint, String answer, String answerHint, Integer isDataDefined, Integer attrBoolean1, Integer attrBoolean2, Integer attrBoolean3, Integer attrBoolean4, Integer attrBoolean5, Integer attrBoolean6, Integer attrBoolean7, Integer attrBoolean8, Integer attrBoolean9, Long groupInsObjectId, Long insObjectId, Long insRiskId, SettingCategoryOfDamageOnInsProduct settingId_EN, Long settingId, CategoryOfDamageOnInsProduct parentId_EN, Long parentId, Set<CategoryOfDamageOnInsProduct> subQuestions, Long rowStatus) {
       this.id = id;
       this.isNotRequired = isNotRequired;
       this.question = question;
       this.questionHint = questionHint;
       this.answer = answer;
       this.answerHint = answerHint;
       this.isDataDefined = isDataDefined;
       this.attrBoolean1 = attrBoolean1;
       this.attrBoolean2 = attrBoolean2;
       this.attrBoolean3 = attrBoolean3;
       this.attrBoolean4 = attrBoolean4;
       this.attrBoolean5 = attrBoolean5;
       this.attrBoolean6 = attrBoolean6;
       this.attrBoolean7 = attrBoolean7;
       this.attrBoolean8 = attrBoolean8;
       this.attrBoolean9 = attrBoolean9;
       this.groupInsObjectId = groupInsObjectId;
       this.insObjectId = insObjectId;
       this.insRiskId = insRiskId;
       this.settingId_EN = settingId_EN;
       this.settingId = settingId;
       this.parentId_EN = parentId_EN;
       this.parentId = parentId;
       this.subQuestions = subQuestions;
       this.rowStatus = rowStatus;
    }
   
  @SequenceGenerator(name = "generator", sequenceName = "PF_CATEGORYOFDAMAGE_SEQ", allocationSize = 10)
  @GeneratedValue(strategy = SEQUENCE, generator = "generator")
  @Id

    
    @Column(name="id", nullable=false, insertable=false, updatable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    
    @Column(name="isNotRequired")
    public Integer getIsNotRequired() {
        return this.isNotRequired;
    }
    
    public void setIsNotRequired(Integer isNotRequired) {
        this.isNotRequired = isNotRequired;
    }

    
    @Column(name="question")
    public String getQuestion() {
        return this.question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }

    
    @Column(name="questionHint")
    public String getQuestionHint() {
        return this.questionHint;
    }
    
    public void setQuestionHint(String questionHint) {
        this.questionHint = questionHint;
    }

    
    @Column(name="answer")
    public String getAnswer() {
        return this.answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    
    @Column(name="answerHint")
    public String getAnswerHint() {
        return this.answerHint;
    }
    
    public void setAnswerHint(String answerHint) {
        this.answerHint = answerHint;
    }

    
    @Column(name="isDataDefined")
    public Integer getIsDataDefined() {
        return this.isDataDefined;
    }
    
    public void setIsDataDefined(Integer isDataDefined) {
        this.isDataDefined = isDataDefined;
    }

    
    @Column(name="attrBoolean1")
    public Integer getAttrBoolean1() {
        return this.attrBoolean1;
    }
    
    public void setAttrBoolean1(Integer attrBoolean1) {
        this.attrBoolean1 = attrBoolean1;
    }

    
    @Column(name="attrBoolean2")
    public Integer getAttrBoolean2() {
        return this.attrBoolean2;
    }
    
    public void setAttrBoolean2(Integer attrBoolean2) {
        this.attrBoolean2 = attrBoolean2;
    }

    
    @Column(name="attrBoolean3")
    public Integer getAttrBoolean3() {
        return this.attrBoolean3;
    }
    
    public void setAttrBoolean3(Integer attrBoolean3) {
        this.attrBoolean3 = attrBoolean3;
    }

    
    @Column(name="attrBoolean4")
    public Integer getAttrBoolean4() {
        return this.attrBoolean4;
    }
    
    public void setAttrBoolean4(Integer attrBoolean4) {
        this.attrBoolean4 = attrBoolean4;
    }

    
    @Column(name="attrBoolean5")
    public Integer getAttrBoolean5() {
        return this.attrBoolean5;
    }
    
    public void setAttrBoolean5(Integer attrBoolean5) {
        this.attrBoolean5 = attrBoolean5;
    }

    
    @Column(name="attrBoolean6")
    public Integer getAttrBoolean6() {
        return this.attrBoolean6;
    }
    
    public void setAttrBoolean6(Integer attrBoolean6) {
        this.attrBoolean6 = attrBoolean6;
    }

    
    @Column(name="attrBoolean7")
    public Integer getAttrBoolean7() {
        return this.attrBoolean7;
    }
    
    public void setAttrBoolean7(Integer attrBoolean7) {
        this.attrBoolean7 = attrBoolean7;
    }

    
    @Column(name="attrBoolean8")
    public Integer getAttrBoolean8() {
        return this.attrBoolean8;
    }
    
    public void setAttrBoolean8(Integer attrBoolean8) {
        this.attrBoolean8 = attrBoolean8;
    }

    
    @Column(name="attrBoolean9")
    public Integer getAttrBoolean9() {
        return this.attrBoolean9;
    }
    
    public void setAttrBoolean9(Integer attrBoolean9) {
        this.attrBoolean9 = attrBoolean9;
    }

    
    @Column(name="groupInsObjectId")
    public Long getGroupInsObjectId() {
        return this.groupInsObjectId;
    }
    
    public void setGroupInsObjectId(Long groupInsObjectId) {
        this.groupInsObjectId = groupInsObjectId;
    }

    
    @Column(name="insObjectId")
    public Long getInsObjectId() {
        return this.insObjectId;
    }
    
    public void setInsObjectId(Long insObjectId) {
        this.insObjectId = insObjectId;
    }

    
    @Column(name="insRiskId")
    public Long getInsRiskId() {
        return this.insRiskId;
    }
    
    public void setInsRiskId(Long insRiskId) {
        this.insRiskId = insRiskId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="settingId")
@IsArray
    public SettingCategoryOfDamageOnInsProduct getSettingId_EN() {
        return this.settingId_EN;
    }
    
    public void setSettingId_EN(SettingCategoryOfDamageOnInsProduct settingId_EN) {
        this.settingId_EN = settingId_EN;
    }

    
    @Column(name="settingId", insertable=false, updatable=false)
    public Long getSettingId() {
        return this.settingId;
    }
    
    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

@ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="parentId")
   @IsArray
    public CategoryOfDamageOnInsProduct getParentId_EN() {
        return this.parentId_EN;
    }
    
    public void setParentId_EN(CategoryOfDamageOnInsProduct parentId_EN) {
        this.parentId_EN = parentId_EN;
    }

    
    @Column(name="parentId", insertable=false, updatable=false)
    public Long getParentId() {
        return this.parentId;
    }
    
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="parentId_EN")
    public Set<CategoryOfDamageOnInsProduct> getSubQuestions() {
        return this.subQuestions;
    }
    
    public void setSubQuestions(Set<CategoryOfDamageOnInsProduct> subQuestions) {
        this.subQuestions = subQuestions;
    }

@Transient
    
    public Long getRowStatus() {
        return this.rowStatus;
    }
    
    public void setRowStatus(Long rowStatus) {
        this.rowStatus = rowStatus;
    }




}


