package com.bivgroup.underwriting;

import com.bivgroup.core.aspect.AspectEntityManager;
import com.bivgroup.core.dictionary.dao.hierarchy.ExtendGenericDAO;
import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

public class Underwriting2 extends HierarchyDAO implements ExtendGenericDAO {

    @PersistenceContext(unitName = "Underwriting")
    private EntityManager l_em;

    @Resource
    private UserTransaction l_ut;

    @Inject
    private AspectEntityManager l_aem;

    /**
     * testing constructor
     */
    public Underwriting2(EntityManager l_em, UserTransaction l_ut/*, AspectEntityManager l_aem*/) {
        this.setEm(l_em);
        this.setUt(l_ut);
        this.setAem(l_aem);
        initEntityClasses();
    }

    public Underwriting2() {
    }

    @PostConstruct
    public void init() {
        this.setEm(l_em);
        this.setUt(l_ut);
        this.setAem(l_aem);
        initEntityClasses();
    }

    @PreDestroy
    public void cleanUp() {
    }

    @Override
    public void initEntityClasses() {
        for (Class<?> clazz : new Class<?>[]{
                Answer.class,
                AnswerEnumType.class,
                AnswerEnumValue.class,
                FilledQuestionnaire.class,
                Question.class,
                Questionnaire.class,
                QuestionnaireByProduct.class,
                QuestionnaireVersion.class,
                UwCommonDetail.class,
                UwResult.class,
                UwRiskDetail.class,
                AnswerComponent.class,
                QuestionComponent.class
        }) {
            mapClasses.put(clazz.getSimpleName(), clazz);
        }

    }

}
