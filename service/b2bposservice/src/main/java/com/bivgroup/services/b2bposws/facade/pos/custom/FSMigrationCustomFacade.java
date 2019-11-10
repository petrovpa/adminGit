package com.bivgroup.services.b2bposws.facade.pos.custom;

import com.bivgroup.seaweedfs.client.AssignParams;
import com.bivgroup.seaweedfs.client.Assignation;
import com.bivgroup.seaweedfs.client.ReplicationStrategy;
import com.bivgroup.seaweedfs.client.WeedFSClient;
import com.bivgroup.seaweedfs.client.WeedFSClientBuilder;
import com.bivgroup.services.b2bposws.system.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ru.diasoft.services.config.Config;
import ru.diasoft.services.inscore.facade.BaseFacade;
import ru.diasoft.services.inscore.system.WsConstants;
import ru.diasoft.services.inscore.system.WsUtils;
import ru.diasoft.services.inscore.system.annotations.BOName;
import ru.diasoft.services.inscore.system.annotations.WsMethod;

/**
 * Фасад для загрузки бинарных файлов во внешнюю ФС
 *
 * @author ilich
 */
@BOName("FSMigrationCustom")
public class FSMigrationCustomFacade extends BaseFacade {

    public static final String SERVICE_NAME = Constants.B2BPOSWS;

    protected String getUseSeaweedFS() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("USESEAWEEDFS", "FALSE");
        return login;
    }

    protected String getSeaweedFSUrl() {
        String login;
        Config config = Config.getConfig(SERVICE_NAME);
        login = config.getParam("SEAWEEDFSURL", "");
        return login;
    }

    private String getUploadFilePath() {
        String result = Config.getConfig("webclient").getParam("uploadPath", System.getProperty("user.home") + "\\.diasoft\\webclient\\upload");
        // проверим, что пути есть и каталоги существуют
        File dirFile = new File(result);
        dirFile.mkdirs();
        return result;
    }

    private List<Map<String, Object>> readEntityBinaryFiles(String entityName, String login, String password) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("DISABLEIDCHECK", "TRUE");
        Map<String, Object> readRes = this.callService(Constants.B2BPOSWS, "ds" + entityName + "_BinaryFile_BinaryFileBrowseListByParam", params, login, password);
        return WsUtils.getListFromResultMap(readRes);
    }

    @WsMethod(requiredParams = {})
    public Map<String, Object> dsFSMigrationUploadFilesToFS(Map<String, Object> params) throws Exception {
        String login = params.get(WsConstants.LOGIN).toString();
        String password = params.get(WsConstants.PASSWORD).toString();
        String[] migrationEntities = {"B2BContract", "B2BContractDocument", "B2BBankStateDocument", "B2BExportData", "B2BLossCompReqDoc"};
        List<Map<String, Object>> resBinFileList = new ArrayList<Map<String, Object>>();
        for (String bean : migrationEntities) {
            List<Map<String, Object>> list = readEntityBinaryFiles(bean, login, password);
            if ((list != null) && (list.size() > 0)) {
                for (Map<String, Object> lstBean : list) {
                    lstBean.put("ENTITYNAME", bean);
                }
                resBinFileList.addAll(list);
            }
        }
        String uploadFilePath = getUploadFilePath();
        for (Map<String, Object> bean : resBinFileList) {
            if ((bean.get("FSID") == null) && (bean.get("FILEPATH") != null)) {
                File src = new File(bean.get("FILEPATH").toString());
                String name = src.getName();
                name = name.replace("\\", "").replace("/", "");
                File f = new File(uploadFilePath + name);
                if (f.exists() && f.getCanonicalPath().startsWith(uploadFilePath)) {
                    String masterUrlString = getSeaweedFSUrl();
                    URL masterURL = new URL(masterUrlString);
                    WeedFSClient client = WeedFSClientBuilder.createBuilder().setMasterUrl(masterURL).build();
                    Assignation a = client.assign(new AssignParams("b2bmigration", ReplicationStrategy.TwiceOnRack));
                    int size = client.write(a.weedFSFile, a.location, new FileInputStream(f), bean.get("FILENAME").toString());
                    if (size == 0) {
                        throw new Exception("Unable to write file to SeaweedFS");
                    }
                    Map<String, Object> updParams = new HashMap<String, Object>();
                    updParams.put("BINFILEID", bean.get("BINFILEID"));
                    updParams.put("FSID", a.weedFSFile.fid);
                    String entityName = bean.get("ENTITYNAME").toString();
                    this.callService(Constants.B2BPOSWS, "ds" + entityName + "_BinaryFile_updateBinaryFileInfo", updParams, login, password);
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        return result;
    }
}
