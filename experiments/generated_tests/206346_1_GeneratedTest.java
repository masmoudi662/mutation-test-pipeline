java
package org.apache.synapse.transport.passthru.util;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

public class RelayUtilsTest {

    @Test
    public void testBuildMessage_pipeNull() throws IOException, XMLStreamException {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        when(msgCtx.getProperty(Mockito.anyString())).thenReturn(null);
        RelayUtils.buildMessage(msgCtx);
        verify(msgCtx, never()).setProperty(Mockito.anyString(), Mockito.any());
    }

    @Test
    public void testBuildMessage_forcePTBuildFalse() throws IOException, XMLStreamException {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.FORCE_PASS_THROUGH_BUILDER))).thenReturn(false);
		when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.MESSAGE_BUILDER_INVOKED))).thenReturn(true);
        RelayUtils.buildMessage(msgCtx);
        verify(pipe, never()).getInputStream();
    }

    @Test
    public void testBuildMessage_inputStreamNull() throws IOException, XMLStreamException {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.MESSAGE_BUILDER_INVOKED))).thenReturn(null);
        when(pipe.getInputStream()).thenReturn(null);
        RelayUtils.buildMessage(msgCtx);
        verify(msgCtx, never()).setProperty(Mockito.anyString(), Mockito.any());
    }

    @Test(expected = AxisFault.class)
    public void testBuildMessage_exception() throws IOException, XMLStreamException {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.MESSAGE_BUILDER_INVOKED))).thenReturn(null);
        InputStream inputStream = Mockito.mock(InputStream.class);
        when(pipe.getInputStream()).thenReturn(inputStream);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.BUFFERED_INPUT_STREAM))).thenReturn(null);
        try {
            when(inputStream.read()).thenThrow(new IOException("Test Exception"));
            RelayUtils.buildMessage(msgCtx);
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void testConsumeAndDiscardMessage() throws Exception {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        InputStream in = new ByteArrayInputStream("test data".getBytes());
        when(pipe.getInputStream()).thenReturn(in);
        when(pipe.isConsumeRequired()).thenReturn(true);
        //RelayUtils.consumeAndDiscardMessage(msgCtx);

    }

    @Test(expected = AxisFault.class)
    public void testConsumeAndDiscardMessage_exception() throws Exception {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        InputStream in = Mockito.mock(InputStream.class);
        when(pipe.getInputStream()).thenReturn(in);
        when(pipe.isConsumeRequired()).thenReturn(true);
        when(in.read()).thenThrow(new IOException("Test Exception"));
        //RelayUtils.consumeAndDiscardMessage(msgCtx);
    }
    
    @Test
    public void testBuildMessage_EarlyBuildTrue() throws IOException, XMLStreamException {
        MessageContext msgCtx = Mockito.mock(MessageContext.class);
        Pipe pipe = Mockito.mock(Pipe.class);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.MESSAGE_BUILDER_INVOKED))).thenReturn(null);
        InputStream inputStream = new ByteArrayInputStream("<root>test</root>".getBytes());
        when(pipe.getInputStream()).thenReturn(inputStream);
        when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.BUFFERED_INPUT_STREAM))).thenReturn(null);
        RelayUtils.buildMessage(msgCtx, true);
        verify(msgCtx, atLeastOnce()).setProperty(Mockito.anyString(), Mockito.any());
    }

	@Test
	public void testBuildMessage_EarlyBuildFalse() throws IOException, XMLStreamException {
		MessageContext msgCtx = Mockito.mock(MessageContext.class);
		Pipe pipe = Mockito.mock(Pipe.class);
		when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.PASS_THROUGH_PIPE))).thenReturn(pipe);
		when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.MESSAGE_BUILDER_INVOKED))).thenReturn(null);
		InputStream inputStream = new ByteArrayInputStream("<root>test</root>".getBytes());
		when(pipe.getInputStream()).thenReturn(inputStream);
		when(msgCtx.getProperty(Mockito.eq(PassThroughConstants.BUFFERED_INPUT_STREAM))).thenReturn(null);
		RelayUtils.buildMessage(msgCtx, false);
		verify(msgCtx, atLeastOnce()).setProperty(Mockito.anyString(), Mockito.any());
	}

	@Test
	public void testBuildMessage_BinaryContent() throws IOException, XMLStreamException {
		MessageContext msgCtx = Mockito.mock(MessageContext.class);
		OMElement element = Mockito.mock(OMElement.class);
		org.apache.axiom.soap.SOAPEnvelope envelope = Mockito.mock(org.apache.axiom.soap.SOAPEnvelope.class);
		org.apache.axiom.soap.SOAPBody body = Mockito.mock(org.apache.axiom.soap.SOAPBody.class);

		when(msgCtx.getEnvelope()).thenReturn(envelope);
		when(envelope.getBody()).thenReturn(body);
		when(body.getFirstElement()).thenReturn(element);
		when(envelope.getSOAPBodyFirstElementLocalName()).thenReturn(RelayConstants.BINARY_CONTENT_QNAME.getLocalPart());
		when(envelope.getSOAPBodyFirstElementNS()).thenReturn(new org.apache.axiom.om.impl.llom.OMNamespaceImpl(RelayConstants.BINARY_CONTENT_QNAME.getNamespaceURI(), RelayConstants.BINARY_CONTENT_QNAME.getPrefix()));

		RelayUtils.buildMessage(msgCtx);

		verify(msgCtx, times(1)).setProperty(Mockito.anyString(), Mockito.any());
	}
}