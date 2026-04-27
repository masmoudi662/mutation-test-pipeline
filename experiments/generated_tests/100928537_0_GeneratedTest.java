java
package de.fzi.zielke.statemachine;

import de.fzi.zielke.statemachine.enums.CameraState;
import de.fzi.zielke.statemachine.enums.MicrophoneState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

class StateMachineTest {

    private StateMachine stateMachine;

    @Mock
    private JSONParser jsonParser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stateMachine = new StateMachine(jsonParser);
    }

    @Test
    void nextCameraState_fromBlocked() {
        stateMachine.setCameraState(CameraState.BLOCKED);
        stateMachine.nextCameraState();
        assertEquals(CameraState.BLACK_PICTURE, stateMachine.getCameraState());
        verify(jsonParser).setCameraState("softBlackPicture");
    }

    @Test
    void nextCameraState_fromBlackPicture() {
        stateMachine.setCameraState(CameraState.BLACK_PICTURE);
        stateMachine.nextCameraState();
        assertEquals(CameraState.NEUTRAL_PICTURE, stateMachine.getCameraState());
        verify(jsonParser).setCameraState("softNeutralPicture");
    }

    @Test
    void nextCameraState_fromNeutralPicture() {
        stateMachine.setCameraState(CameraState.NEUTRAL_PICTURE);
        stateMachine.nextCameraState();
        assertEquals(CameraState.NEUTRAL_PICTURE, stateMachine.getCameraState());
    }
}