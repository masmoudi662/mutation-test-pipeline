java
package org.n52.wps.server.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.xmlbeans.XmlObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.n52.wps.server.request.InputHandler;

import net.opengis.wps.x10.DataInputsType;
import net.opengis.wps.x10.InputDescriptionType;
import net.opengis.wps.x10.ProcessDescriptionType;
import net.opengis.wps.x10.ows.CodeType;

public class InputHandlerTest {

    private InputHandler inputHandler;
    private ProcessDescriptionType processDesc;
    private DataInputsType dataInputs;

    @BeforeEach
    public void setup() {
        inputHandler = new InputHandler();
        processDesc = mock(ProcessDescriptionType.class);
        dataInputs = mock(DataInputsType.class);

        inputHandler.processDesc = processDesc;
        when(processDesc.getDataInputs()).thenReturn(dataInputs);
    }

    @Test
    public void testGetInputReferenceDescriptionType_found() {
        InputDescriptionType inputDesc1 = mock(InputDescriptionType.class);
        CodeType identifier1 = mock(CodeType.class);
        when(identifier1.getStringValue()).thenReturn("input1");
        when(inputDesc1.getIdentifier()).thenReturn(identifier1);

        InputDescriptionType[] inputArray = new InputDescriptionType[]{inputDesc1};
        when(dataInputs.getInputArray()).thenReturn(inputArray);

        InputDescriptionType result = inputHandler.getInputReferenceDescriptionType("input1");

        assertEquals(inputDesc1, result);
    }

    @Test
    public void testGetInputReferenceDescriptionType_notFound() {
        InputDescriptionType[] inputArray = new InputDescriptionType[]{};
        when(dataInputs.getInputArray()).thenReturn(inputArray);

        InputDescriptionType result = inputHandler.getInputReferenceDescriptionType("input1");

        assertNull(result);
    }

    @Test
    public void testGetInputReferenceDescriptionType_multipleInputs_found() {
        InputDescriptionType inputDesc1 = mock(InputDescriptionType.class);
        CodeType identifier1 = mock(CodeType.class);
        when(identifier1.getStringValue()).thenReturn("input1");
        when(inputDesc1.getIdentifier()).thenReturn(identifier1);

        InputDescriptionType inputDesc2 = mock(InputDescriptionType.class);
        CodeType identifier2 = mock(CodeType.class);
        when(identifier2.getStringValue()).thenReturn("input2");
        when(inputDesc2.getIdentifier()).thenReturn(identifier2);

        InputDescriptionType[] inputArray = new InputDescriptionType[]{inputDesc1, inputDesc2};
        when(dataInputs.getInputArray()).thenReturn(inputArray);

        InputDescriptionType result = inputHandler.getInputReferenceDescriptionType("input2");

        assertEquals(inputDesc2, result);
    }

    @Test
    public void testGetInputReferenceDescriptionType_multipleInputs_notFound() {
        InputDescriptionType inputDesc1 = mock(InputDescriptionType.class);
        CodeType identifier1 = mock(CodeType.class);
        when(identifier1.getStringValue()).thenReturn("input1");
        when(inputDesc1.getIdentifier()).thenReturn(identifier1);

        InputDescriptionType inputDesc2 = mock(InputDescriptionType.class);
        CodeType identifier2 = mock(CodeType.class);
        when(identifier2.getStringValue()).thenReturn("input2");
        when(inputDesc2.getIdentifier()).thenReturn(identifier2);

        InputDescriptionType[] inputArray = new InputDescriptionType[]{inputDesc1, inputDesc2};
        when(dataInputs.getInputArray()).thenReturn(inputArray);

        InputDescriptionType result = inputHandler.getInputReferenceDescriptionType("input3");

        assertNull(result);
    }

    @Test
    public void testGetInputReferenceDescriptionType_emptyArray() {
        InputDescriptionType[] inputArray = new InputDescriptionType[0];
        when(dataInputs.getInputArray()).thenReturn(inputArray);

        InputDescriptionType result = inputHandler.getInputReferenceDescriptionType("input1");

        assertNull(result);
    }
}