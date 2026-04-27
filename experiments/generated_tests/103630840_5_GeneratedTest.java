java
package com.xiaomi.shepher.service;

import com.xiaomi.shepher.biz.NodeBiz;
import com.xiaomi.shepher.biz.SnapshotBiz;
import com.xiaomi.shepher.dao.NodeDAO;
import com.xiaomi.shepher.exception.ShepherException;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NodeServiceTest {

    @InjectMocks
    private NodeService nodeService;

    @Mock
    private NodeBiz nodeBiz;

    @Mock
    private SnapshotBiz snapshotBiz;

    @Mock
    private NodeDAO nodeDAO;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdate() throws ShepherException {
        String cluster = "testCluster";
        String path = "/testPath";
        String data = "testData";
        String creator = "testCreator";

        Stat stat = new Stat();
        stat.setCtime(System.currentTimeMillis());
        stat.setVersion(1);

        when(nodeDAO.getStat(cluster, path, true)).thenReturn(stat);

        nodeService.update(cluster, path, data, creator);

        verify(nodeBiz).update(cluster, path, data);
        verify(nodeDAO).getStat(cluster, path, true);
    }
}