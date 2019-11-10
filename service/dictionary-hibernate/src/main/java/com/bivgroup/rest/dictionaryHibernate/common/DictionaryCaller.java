package com.bivgroup.rest.dictionaryHibernate.common;

import com.bivgroup.core.dictionary.dao.jpa.HierarchyDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.transaction.UserTransaction;
import org.hibernate.collection.internal.PersistentBag;

/**
 *
 * @author ilich
 */
public class DictionaryCaller {

    private UserTransaction transaction = null;
    private HierarchyDAO dao = null;

    public DictionaryCaller(HierarchyDAO dao) {
        this.dao = dao;
        //this.dao.setIsInsertRootInO2M(Boolean.TRUE);
    }

    public HierarchyDAO getDAO() {
        return dao;
    }

    public void beginTransaction() throws Exception {
        if (transaction == null) {
            transaction = this.dao.getUt();
            try {
                transaction.begin();
            } catch (Exception ex) {
                throw new Exception("DictionaryCaller: Cannot begin transaction", ex);
            }
        }
    }

    public void commit() throws Exception {
        if (transaction != null) {
            try {
                transaction.commit();
            } catch (Exception ex) {
                throw new Exception("DictionaryCaller: Cannot commit transaction", ex);
            }
            transaction = null;
        }
    }

    public void rollback() throws Exception {
        if (transaction != null) {
            try {
                transaction.rollback();
            } catch (Exception ex) {
                throw new Exception("DictionaryCaller: Cannot commit transaction", ex);
            }
            transaction = null;
        }
    }

    private boolean isInParentList(List<Map> list, Map map) {
        if (list != null) {
            for (Map bean : list) {
                if (bean == map) {
                    return true;
                }
            }
        }
        return false;
    }

    private void processReturnResult(Map map, List<Map> parentMapList) {
        if (map != null) {
            List<String> entryToRemove = new ArrayList();
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
                String keyName = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    if (value instanceof java.sql.Date) {
                        entry.setValue(new Date(((java.sql.Date) value).getTime()));
                    } else if (value instanceof Map) {
                        if ((value == map) || isInParentList(parentMapList, (Map) value)) {
                            entryToRemove.add(keyName);
                        } else {
                            parentMapList.add((Map) value);
                            processReturnResult((Map) value, parentMapList);
                            parentMapList.remove((Map) value);
                        }
                    } else if (value instanceof PersistentBag) {
                        PersistentBag pbCollection = (PersistentBag) value;
                        List<Object> list = new ArrayList<Object>(pbCollection);
                        entry.setValue(list);
                        List<Object> removeFromList = new ArrayList();
                        for (Object listBean : list) {
                            if (listBean instanceof Map) {
                                if ((listBean == map) || isInParentList(parentMapList, (Map) listBean)) {
                                    removeFromList.add(listBean);
                                } else {
                                    parentMapList.add((Map) listBean);
                                    processReturnResult((Map) listBean, parentMapList);
                                    parentMapList.remove((Map) listBean);
                                }
                            }
                        }
                        for (Object removeBean : removeFromList) {
                            list.remove(removeBean);
                        }
                    } else if (value instanceof List) {
                        List<Object> list = (List<Object>) value;
                        List<Object> removeFromList = new ArrayList();
                        for (Object listBean : list) {
                            if (listBean instanceof Map) {
                                if ((listBean == map) || isInParentList(parentMapList, (Map) listBean)) {
                                    removeFromList.add(listBean);
                                } else {
                                    parentMapList.add((Map) listBean);
                                    processReturnResult((Map) listBean, parentMapList);
                                    parentMapList.remove((Map) listBean);
                                }
                            }
                        }
                        for (Object removeBean : removeFromList) {
                            list.remove(removeBean);
                        }
                    }
                }
            }
            if (entryToRemove.size() > 0) {
                for (String bean : entryToRemove) {
                    map.remove(bean);
                }
            }
        }
    }

    public Map<String, Object> processReturnResult(Map map) {
        List<Map> l = new ArrayList<Map>();
        l.add(map);
        processReturnResult(map, l);
        return map;
    }

    public List<Map<String, Object>> processReturnResult(List<Map> list) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (list != null) {
            for (Map bean : list) {
                List<Map> l = new ArrayList<Map>();
                l.add(bean);
                processReturnResult(bean, l);
                result.add(bean);
            }
        }
        return result;
    }

}
