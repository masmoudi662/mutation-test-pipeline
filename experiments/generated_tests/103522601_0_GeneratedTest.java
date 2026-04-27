java
package com.h3xstream.findsecbugs.common;

import static org.mockito.Mockito.mock;

import org.apache.bcel.Const;
import org.apache.bcel.generic.ACONST_NULL;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.NOP;
import org.junit.Test;

public class ByteCodeTest {

    @Test
    public void testPrintOpCodeWithInstructionHandle() {
        InstructionHandle insHandle = mock(InstructionHandle.class);
        ConstantPoolGen cpg = mock(ConstantPoolGen.class);

        ByteCode.printOpCode(insHandle, cpg);
    }

    @Test
    public void testPrintOpCodeWithNullInstruction() {
        ConstantPoolGen cpg = mock(ConstantPoolGen.class);
        InstructionHandle instructionHandle = mock(InstructionHandle.class);

        ByteCode.printOpCode(instructionHandle, cpg);
    }

    @Test
    public void testPrintOpCodeWithAConstNull() {
        ConstantPoolGen cpg = mock(ConstantPoolGen.class);
        InstructionHandle instructionHandle = mock(InstructionHandle.class);

        ByteCode.printOpCode(instructionHandle, cpg);
    }

    @Test
    public void testPrintOpCodeWithNOP() {
        ConstantPoolGen cpg = mock(ConstantPoolGen.class);
        InstructionHandle instructionHandle = mock(InstructionHandle.class);

        ByteCode.printOpCode(instructionHandle, cpg);
    }
}