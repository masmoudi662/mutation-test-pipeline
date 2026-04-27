java
package org.apache.iotdb.db.metadata;

import org.apache.iotdb.db.exception.PathErrorException;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MTreeTest {

  private MTree mTree;

  @Before
  public void setUp() {
    mTree = new MTree();
  }

  @Test
  public void testAddTimeseriesPath() throws PathErrorException {
    String timeseriesPath = "root.ln.device.sensor";
    String dataType = "INT32";
    String encoding = "RLE";
    String[] args = {};

    mTree.addTimeseriesPath(timeseriesPath, dataType, encoding, args);

    try {
      mTree.addTimeseriesPath(timeseriesPath, dataType, encoding, args);
    } catch (PathErrorException e) {
      Assert.assertEquals(
          "The Node [sensor] is left node, the timeseries root.ln.device.sensor can't be created",
          e.getMessage());
    }

    try {
      mTree.addTimeseriesPath("root.ln", dataType, encoding, args);
    } catch (PathErrorException e) {
      Assert.assertEquals(
          "The Node [ln] is left node, the timeseries root.ln can't be created",
          e.getMessage());
    }
  }

  @Test
  public void testAddTimeseriesPathWithArgs() throws PathErrorException {
    String timeseriesPath = "root.ln.device.sensor";
    String dataType = "INT32";
    String encoding = "RLE";
    String[] args = {"key1=value1", "key2=value2"};

    mTree.addTimeseriesPath(timeseriesPath, dataType, encoding, args);
  }

  @Test(expected = PathErrorException.class)
  public void testAddTimeseriesPathInvalidPath() throws PathErrorException {
    String timeseriesPath = "invalid.ln.device.sensor";
    String dataType = "INT32";
    String encoding = "RLE";
    String[] args = {};

    mTree.addTimeseriesPath(timeseriesPath, dataType, encoding, args);
  }

  @Test
  public void testAddTimeseriesPathWithStorageLevel() throws PathErrorException {
    mTree.setStorageLevel("root.group");
    String timeseriesPath = "root.group.device.sensor";
    String dataType = "INT32";
    String encoding = "RLE";
    String[] args = {};
    mTree.addTimeseriesPath(timeseriesPath, dataType, encoding, args);
  }
}