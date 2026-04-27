java
package de.slackspace.openkeepass.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AttachmentValueTest {

    @Test
    public void testGetRef() {
        AttachmentValue attachmentValue = new AttachmentValue(123);
        assertEquals(123, attachmentValue.getRef());
    }

    private static class AttachmentValue extends de.slackspace.openkeepass.domain.AttachmentValue {
        private int ref;

        public AttachmentValue(int ref) {
            this.ref = ref;
        }

        @Override
        public int getRef() {
            return ref;
        }
    }
}