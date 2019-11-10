package ru.diasoft.services.wsws;

import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.admin.AdmInfo;
import ru.diasoft.services.inscore.admin.RootAdmInfo;
import com.bivgroup.services.wsws.admin.WSAdmInfo;

public class ServiceRootAdmInfo extends RootAdmInfo {

    @Override
    public Set<Class<? extends AdmInfo>> getAdmInfoClasses() {
        Set<Class<? extends AdmInfo>> classes = new HashSet<Class<? extends AdmInfo>>();
        classes.add(WSAdmInfo.class);
        return classes;
    }
}
