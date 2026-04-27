java
package org.jclouds.compute.options;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.testng.annotations.Test;

/**
 * Tests {@link TemplateOptions}
 */
@Test(groups = "unit", testName = "TemplateOptionsTest")
public class TemplateOptionsTest {

   public void testAsTemplateOptions() {
      TemplateOptions options = new TemplateOptions();
      assertNull(options.getPrivateKey());
   }

   public void testInstallPrivateKey() {
      TemplateOptions options = new TemplateOptions();
      String privateKey = "-----BEGIN RSA PRIVATE KEY-----\nMIIG4QIBAAKC...";
      options.installPrivateKey(privateKey);
      assertEquals(options.getPrivateKey(), privateKey);
   }

   @Test(expectedExceptions = NullPointerException.class, expectedExceptionsMessageRegExp = "privateKey")
   public void testInstallNullPrivateKey() {
      TemplateOptions options = new TemplateOptions();
      options.installPrivateKey(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "key should start with -----BEGIN RSA PRIVATE KEY-----")
   public void testInstallInvalidPrivateKey() {
      TemplateOptions options = new TemplateOptions();
      options.installPrivateKey("ssh-rsa AAAAB3Nz...");
   }

   public void testInstallPrivateKeyChaining() {
      TemplateOptions options = new TemplateOptions();
      String privateKey = "-----BEGIN RSA PRIVATE KEY-----\nMIIG4QIBAAKC...";
      TemplateOptions chained = options.installPrivateKey(privateKey);
      assertEquals(chained, options);
   }

   public void testToString() {
      TemplateOptions options = new TemplateOptions();
      options.installPrivateKey("-----BEGIN RSA PRIVATE KEY-----\nMIIG4QIBAAKC...");
      String expected = "TemplateOptions{privateKey=-----BEGIN RSA PRIVATE KEY-----\nMIIG4QIBAAKC..., runScript=RunScriptOptions{runAsRoot=false, overrideLoginCredentials=null, environment={}, bootstrapper=null, initScript=null, installOnly=false, runScript=false, bindHost=null}}";
      assertEquals(options.toString(), expected);
   }

   public void testClone() {
      TemplateOptions options = new TemplateOptions();
      String privateKey = "-----BEGIN RSA PRIVATE KEY-----\nMIIG4QIBAAKC...";
      options.installPrivateKey(privateKey);
      TemplateOptions clone = options.clone();
      assertEquals(clone.getPrivateKey(), options.getPrivateKey());
   }
}