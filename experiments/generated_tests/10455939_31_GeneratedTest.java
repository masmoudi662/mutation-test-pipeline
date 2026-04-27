java
package org.wikibrain.sr.dataset;

import org.junit.Test;
import org.wikibrain.core.dao.DaoException;

import java.util.Collection;

import static org.junit.Assert.*;

public class DatasetDaoTest {

    @Test
    public void testReadInfos() throws DaoException {
        Collection<Info> infos = DatasetDao.readInfos();
        assertNotNull(infos);
        assertFalse(infos.isEmpty());
    }
}