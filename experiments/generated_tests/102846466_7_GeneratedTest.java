java
package com.github.zzt93.syncer.producer.input.mysql.connect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinlogInfoTest {

    @Test
    void compareToSame() {
        BinlogInfo binlogInfo = new BinlogInfo("mysql-bin.000001", 100L);
        assertEquals(0, binlogInfo.compareTo(binlogInfo));
    }

    @Test
    void compareToLatest() {
        BinlogInfo binlogInfo = new BinlogInfo("mysql-bin.000001", 100L);
        binlogInfo.latest = true;
        BinlogInfo other = new BinlogInfo("mysql-bin.000002", 200L);
        assertEquals(1, binlogInfo.compareTo(other));
    }

    @Test
    void compareToEarliest() {
        BinlogInfo binlogInfo = new BinlogInfo("mysql-bin.000001", 100L);
        binlogInfo.earliest = true;
        BinlogInfo other = new BinlogInfo("mysql-bin.000002", 200L);
        assertEquals(-1, binlogInfo.compareTo(other));
    }

    @Test
    void compareToFilename() {
        BinlogInfo binlogInfo1 = new BinlogInfo("mysql-bin.000001", 100L);
        BinlogInfo binlogInfo2 = new BinlogInfo("mysql-bin.000002", 100L);
        assertTrue(binlogInfo1.compareTo(binlogInfo2) < 0);
    }

    @Test
    void compareToPosition() {
        BinlogInfo binlogInfo1 = new BinlogInfo("mysql-bin.000001", 100L);
        BinlogInfo binlogInfo2 = new BinlogInfo("mysql-bin.000001", 200L);
        assertTrue(binlogInfo1.compareTo(binlogInfo2) < 0);
    }

    @Test
    void compareToEqual() {
        BinlogInfo binlogInfo1 = new BinlogInfo("mysql-bin.000001", 100L);
        BinlogInfo binlogInfo2 = new BinlogInfo("mysql-bin.000001", 100L);
        assertEquals(0, binlogInfo1.compareTo(binlogInfo2));
    }
}