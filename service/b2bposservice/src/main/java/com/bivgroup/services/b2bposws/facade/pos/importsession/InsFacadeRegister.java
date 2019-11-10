package com.bivgroup.services.b2bposws.facade.pos.importsession;

import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionTaskStarterCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.common.B2BImportSessionTestCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.content.B2BImportSessionContentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.content.B2BImportSessionContentProcessLogEntryCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.department.B2BImportSessionDepartmentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.department.tasks.B2BImportSessionDepartmentTaskProcessContentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.department.tasks.B2BImportSessionDepartmentTaskProcessFileCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract.B2BImportSessionManagerContractCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract.tasks.B2BImportSessionManagerContractTaskProcessContentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managercontract.tasks.B2BImportSessionManagerContractTaskProcessFileCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment.B2BImportSessionManagerDepartmentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment.tasks.B2BImportSessionManagerDepartmentTaskProcessContentCustomFacade;
import com.bivgroup.services.b2bposws.facade.pos.importsession.managerdepartment.tasks.B2BImportSessionManagerDepartmentTaskProcessFileCustomFacade;
import ru.diasoft.services.inscore.facade.FacadeRegister;

import java.util.HashSet;
import java.util.Set;

public class InsFacadeRegister extends FacadeRegister {

    @Override
    public Set<Class<? extends Object>> getFacadeClasses() {

        // Фасады для импорта данных по оргструктуре, КМ, ВСП и пр. (#18463)
        Set<Class<? extends Object>> classes = new HashSet<>();

        // Сессия импорта
        // classes.add(B2BImportSessionCustomFacade.class);
        // Содержимое сессии импорта
        classes.add(B2BImportSessionContentCustomFacade.class);
        // Запись протокола обработки содержимого сессии импорта
        classes.add(B2BImportSessionContentProcessLogEntryCustomFacade.class);

        // Сессия импорта оргструктуры
        classes.add(B2BImportSessionDepartmentCustomFacade.class);
        // Сессия импорта оргструктуры - регламентные задания
        classes.add(B2BImportSessionDepartmentTaskProcessFileCustomFacade.class);
        classes.add(B2BImportSessionDepartmentTaskProcessContentCustomFacade.class);

        // Сессия импорта КМ-ВСП
        classes.add(B2BImportSessionManagerDepartmentCustomFacade.class);
        // Сессия импорта КМ-ВСП - регламентные задания
        classes.add(B2BImportSessionManagerDepartmentTaskProcessFileCustomFacade.class);
        classes.add(B2BImportSessionManagerDepartmentTaskProcessContentCustomFacade.class);

        // Сессия импорта КМ-Договор
        classes.add(B2BImportSessionManagerContractCustomFacade.class);
        // Сессия импорта КМ-Договор - регламентные задания
        classes.add(B2BImportSessionManagerContractTaskProcessFileCustomFacade.class);
        classes.add(B2BImportSessionManagerContractTaskProcessContentCustomFacade.class);

        // Запуск регламентных заданий
        classes.add(B2BImportSessionTaskStarterCustomFacade.class);

        // тестовый фасад
        // todo: отключить по завершению разработки
        classes.add(B2BImportSessionTestCustomFacade.class);

        return classes;

    }

}
