java
package org.apache.continuum.scm;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultContinuumScmTest {

    private DefaultContinuumScm continuumScm;
    private ScmManager scmManager;
    private ContinuumScmConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        continuumScm = new DefaultContinuumScm();
        scmManager = Mockito.mock(ScmManager.class);
        continuumScm.setScmManager(scmManager);
        configuration = Mockito.mock(ContinuumScmConfiguration.class);
    }

    @Test
    public void testChangeLogWithScmVersion() throws ScmException {
        ScmVersion scmVersion = Mockito.mock(ScmVersion.class);
        File workingDirectory = new File("test");
        ScmRepository repository = Mockito.mock(ScmRepository.class);
        ChangeLogScmResult expectedResult = Mockito.mock(ChangeLogScmResult.class);

        when(configuration.getWorkingDirectory()).thenReturn(workingDirectory);
        when(continuumScm.getScmVersion(configuration)).thenReturn(scmVersion);
        when(continuumScm.getScmRepository(configuration)).thenReturn(repository);
        when(scmVersion.getName()).thenReturn("1.0");

        when(scmManager.changeLog(eq(repository), any(ScmFileSet.class), eq(scmVersion), eq(scmVersion))).thenReturn(expectedResult);

        ChangeLogScmResult actualResult = continuumScm.changeLog(configuration);

        assertEquals(expectedResult, actualResult);
        verify(scmManager, times(1)).changeLog(eq(repository), any(ScmFileSet.class), eq(scmVersion), eq(scmVersion));
    }

    @Test
    public void testChangeLogWithStartDate() throws ScmException {
        Date startDate = new Date();
        File workingDirectory = new File("test");
        ScmRepository repository = Mockito.mock(ScmRepository.class);
        ChangeLogScmResult expectedResult = Mockito.mock(ChangeLogScmResult.class);

        when(configuration.getWorkingDirectory()).thenReturn(workingDirectory);
        when(continuumScm.getScmVersion(configuration)).thenReturn(null);
        when(continuumScm.getScmRepository(configuration)).thenReturn(repository);
        when(continuumScm.getScmStartDate(configuration)).thenReturn(startDate);

        when(scmManager.changeLog(eq(repository), any(ScmFileSet.class), eq(startDate), eq(null), eq(0), eq(null), eq(null))).thenReturn(expectedResult);

        ChangeLogScmResult actualResult = continuumScm.changeLog(configuration);

        assertEquals(expectedResult, actualResult);
        verify(scmManager, times(1)).changeLog(eq(repository), any(ScmFileSet.class), eq(startDate), eq(null), eq(0), eq(null), eq(null));
    }
}