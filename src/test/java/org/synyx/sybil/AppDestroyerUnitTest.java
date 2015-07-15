package org.synyx.sybil;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import org.slf4j.LoggerFactory;

import org.synyx.sybil.bricklet.output.ledstrip.LEDStripDTOService;
import org.synyx.sybil.bricklet.output.ledstrip.LEDStripService;
import org.synyx.sybil.bricklet.output.ledstrip.domain.LEDStripDTO;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.argThat;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class AppDestroyerUnitTest {

    @Mock
    LEDStripService ledStripServiceMock;

    @Mock
    LEDStripDTOService ledStripDTOServiceMock;

    @Mock
    LEDStripDTO ledStripDTOOneMock;

    @Mock
    LEDStripDTO ledStripDTOTwoMock;

    @Mock
    LEDStripDTO ledStripDTOThreeMock;

    @Test
    public void turnOffAllLEDStrips() throws Exception {

        // setup
        List<LEDStripDTO> ledStripDTOList = Arrays.asList(ledStripDTOOneMock, ledStripDTOTwoMock, ledStripDTOThreeMock);
        when(ledStripDTOServiceMock.getAllDTOs()).thenReturn(ledStripDTOList);

        AppDestroyer sut = new AppDestroyer(ledStripDTOServiceMock, ledStripServiceMock);

        // execution
        sut.turnOffAllLEDStrips();

        // verification
        verify(ledStripDTOServiceMock).getAllDTOs();

        verify(ledStripServiceMock, times(1)).turnOff(ledStripDTOOneMock);
        verify(ledStripServiceMock, times(1)).turnOff(ledStripDTOTwoMock);
        verify(ledStripServiceMock, times(1)).turnOff(ledStripDTOThreeMock);
    }


    @Test
    public void provokeErrorAndCheckItWasLogged() throws Exception {

        // setup
        doThrow(IOException.class).when(ledStripDTOServiceMock).getAllDTOs();

        @SuppressWarnings("unchecked")
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(
                ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        final Appender mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("MOCK");
        root.addAppender(mockAppender);

        AppDestroyer sut = new AppDestroyer(ledStripDTOServiceMock, ledStripServiceMock);

        // execution
        sut.turnOffAllLEDStrips();

        // verification
        verify(mockAppender).doAppend(argThat(new ArgumentMatcher() {

                    @Override
                    public boolean matches(final Object argument) {

                        return ((LoggingEvent) argument).getFormattedMessage()
                            .contains("Error turning off LED strips: ");
                    }
                }));
    }
}
