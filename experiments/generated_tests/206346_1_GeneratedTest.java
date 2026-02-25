java
package org.apache.synapse.transport.passthru.util;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMText;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.AddressingConstants;
import org.apache.axis2.addressing.AddressingHelper;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.WSDL2Constants;
import org.apache.axis2.engine.AxisConfiguration;
import org.apache.axis2.engine.Handler;
import org.junit.Test;
import org.apache.synapse.transport.passthru.util.RelayUtils;
import org.apache.axis2.transport.base.BaseConstants;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RelayUtilsTest {

    @Test
    public void testBuildMessage() throws IOException, XMLStreamException {
        MessageContext msgCtx = mock(MessageContext.class);
        RelayUtils.buildMessage(msgCtx);
    }
}