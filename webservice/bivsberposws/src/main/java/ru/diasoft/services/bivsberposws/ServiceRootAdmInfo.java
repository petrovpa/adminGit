package ru.diasoft.services.bivsberposws;

import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.admin.AdmInfo;
import ru.diasoft.services.inscore.admin.RootAdmInfo;
import com.bivgroup.services.bivsberposws.admin.BivSberPosAdmInfo;

public class ServiceRootAdmInfo extends RootAdmInfo {

    @Override
    public Set<Class<? extends AdmInfo>> getAdmInfoClasses() {
        Set<Class<? extends AdmInfo>> classes = new HashSet<Class<? extends AdmInfo>>();
        classes.add(BivSberPosAdmInfo.class);
        return classes;
    }
}
