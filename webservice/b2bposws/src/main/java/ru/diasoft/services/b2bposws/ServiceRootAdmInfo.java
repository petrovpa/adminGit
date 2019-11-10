package ru.diasoft.services.b2bposws;

import java.util.HashSet;
import java.util.Set;
import ru.diasoft.services.inscore.admin.AdmInfo;
import ru.diasoft.services.inscore.admin.RootAdmInfo;
import com.bivgroup.services.b2bposws.admin.B2BPosAdmInfo;

public class ServiceRootAdmInfo extends RootAdmInfo {

    @Override
    public Set<Class<? extends AdmInfo>> getAdmInfoClasses() {
        Set<Class<? extends AdmInfo>> classes = new HashSet<Class<? extends AdmInfo>>();
        classes.add(B2BPosAdmInfo.class);
        return classes;
    }
}
