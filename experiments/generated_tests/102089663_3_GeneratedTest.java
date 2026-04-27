java
package org.dromara.raincat.admin.service.recover;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.ZooKeeper;
import org.dromara.raincat.admin.helper.PageHelper;
import org.dromara.raincat.admin.page.CommonPager;
import org.dromara.raincat.admin.query.RecoverTransactionQuery;
import org.dromara.raincat.admin.vo.TransactionRecoverVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ZookeeperRecoverTransactionServiceImplTest {

    @InjectMocks
    private ZookeeperRecoverTransactionServiceImpl zookeeperRecoverTransactionService;

    @Mock
    private ZooKeeper zooKeeper;

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListByPage_noCondition() throws Exception {
        RecoverTransactionQuery query = new RecoverTransactionQuery();
        query.setApplicationName("testApp");
        query.getPageParameter().setCurrentPage(1);
        query.getPageParameter().setPageSize(10);
        String rootPath = "/raincat/recover/testApp";
        List<String> children = Lists.newArrayList("tx1", "tx2");

        when(zooKeeper.getChildren(rootPath, false)).thenReturn(children);

        CommonPager<TransactionRecoverVO> result = zookeeperRecoverTransactionService.listByPage(query);

        assertEquals(2, result.getPage().getTotalCount());
    }

    @Test
    public void testListByPage_retryCondition() throws Exception {
        RecoverTransactionQuery query = new RecoverTransactionQuery();
        query.setApplicationName("testApp");
        query.setRetry(3);
        query.getPageParameter().setCurrentPage(1);
        query.getPageParameter().setPageSize(10);
        String rootPath = "/raincat/recover/testApp";
        List<String> children = Lists.newArrayList("tx1", "tx2");
        when(zooKeeper.getChildren(rootPath, false)).thenReturn(children);

        CommonPager<TransactionRecoverVO> result = zookeeperRecoverTransactionService.listByPage(query);

        assertEquals(0, result.getPage().getTotalCount());
    }

    @Test
    public void testListByPage_txGroupIdCondition() throws Exception {
        RecoverTransactionQuery query = new RecoverTransactionQuery();
        query.setApplicationName("testApp");
        query.setTxGroupId("tx1");
        query.getPageParameter().setCurrentPage(1);
        query.getPageParameter().setPageSize(10);

        CommonPager<TransactionRecoverVO> result = zookeeperRecoverTransactionService.listByPage(query);

        assertEquals(1, result.getPage().getTotalCount());
    }

    @Test
    public void testListByPage_txGroupIdAndRetryCondition() throws Exception {
        RecoverTransactionQuery query = new RecoverTransactionQuery();
        query.setApplicationName("testApp");
        query.setTxGroupId("tx1");
        query.setRetry(3);
        query.getPageParameter().setCurrentPage(1);
        query.getPageParameter().setPageSize(10);

        CommonPager<TransactionRecoverVO> result = zookeeperRecoverTransactionService.listByPage(query);

        assertEquals(1, result.getPage().getTotalCount());
    }
}