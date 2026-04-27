java
package com.wf.gts.nameserver.route;

import com.wf.gts.remoting.protocol.GtsManageLiveAddr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RouteInfoManagerTest {

    private RouteInfoManager routeInfoManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        routeInfoManager = new RouteInfoManager();
        routeInfoManager.liveTable.clear();
    }

    @Test
    void getGtsManagerInfo_emptyLiveTable() {
        byte[] result = routeInfoManager.getGtsManagerInfo();
        assertNotNull(result);
    }

    @Test
    void getGtsManagerInfo_oneEntryInLiveTable() {
        LiveInfo liveInfo = new LiveInfo();
        liveInfo.setGtsManageId(1);
        liveInfo.setLastUpdateTimestamp(System.currentTimeMillis());
        liveInfo.setGtsManageName("testName");
        liveInfo.setGtsManageAddr("testAddress");
        routeInfoManager.liveTable.put(1, liveInfo);

        byte[] result = routeInfoManager.getGtsManagerInfo();
        assertNotNull(result);
    }

    @Test
    void getGtsManagerInfo_multipleEntriesInLiveTable() {
        LiveInfo liveInfo1 = new LiveInfo();
        liveInfo1.setGtsManageId(1);
        liveInfo1.setLastUpdateTimestamp(System.currentTimeMillis());
        liveInfo1.setGtsManageName("testName1");
        liveInfo1.setGtsManageAddr("testAddress1");
        routeInfoManager.liveTable.put(1, liveInfo1);

        LiveInfo liveInfo2 = new LiveInfo();
        liveInfo2.setGtsManageId(2);
        liveInfo2.setLastUpdateTimestamp(System.currentTimeMillis());
        liveInfo2.setGtsManageName("testName2");
        liveInfo2.setGtsManageAddr("testAddress2");
        routeInfoManager.liveTable.put(2, liveInfo2);

        byte[] result = routeInfoManager.getGtsManagerInfo();
        assertNotNull(result);
    }

    @Test
    void getGtsManagerInfo_nullEntryInLiveTable() {
        routeInfoManager.liveTable.put(1, null);

        byte[] result = routeInfoManager.getGtsManagerInfo();
        assertNotNull(result);
    }

    @Test
    void getGtsManagerInfo_emptyNameAndAddress() {
        LiveInfo liveInfo = new LiveInfo();
        liveInfo.setGtsManageId(1);
        liveInfo.setLastUpdateTimestamp(System.currentTimeMillis());
        liveInfo.setGtsManageName("");
        liveInfo.setGtsManageAddr("");
        routeInfoManager.liveTable.put(1, liveInfo);

        byte[] result = routeInfoManager.getGtsManagerInfo();
        assertNotNull(result);
    }
}