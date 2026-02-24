java
package org.apache.continuum.scm;

import org.apache.maven.scm.ScmException;
import org.apache.maven.scm.ScmFileSet;
import org.apache.maven.scm.ScmVersion;
import org.apache.maven.scm.command.changelog.ChangeLogScmResult;
import org.apache.maven.scm.manager.NoSuchScmProviderException;
import org.apache.maven.scm.manager.ScmManager;
import org.apache.maven.scm.repository.ScmRepository;
import org.apache.maven.scm.repository.ScmRepositoryException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Date;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class DefaultContinuumScmTest
{
    private DefaultContinuumScm continuumScm;

    private ScmManager scmManager;

    @Before
    public void setUp()
    {
        continuumScm = new DefaultContinuumScm();
        scmManager = Mockito.mock( ScmManager.class );
        continuumScm.setScmManager( scmManager );
    }

    @Test
    public void testChangeLogWithScmVersion()
        throws ScmException, ScmRepositoryException, NoSuchScmProviderException
    {
        ContinuumScmConfiguration configuration = Mockito.mock( ContinuumScmConfiguration.class );
        when( configuration.getWorkingDirectory() ).thenReturn( new File( "target/test/checkout" ) );
        when( configuration.getUrl() ).thenReturn( "scm:svn:http://svn.example.com/repo" );
        when( scmManager.makeScmRepository( "scm:svn:http://svn.example.com/repo" ) ).thenReturn(
            Mockito.mock( ScmRepository.class ) );
        when( scmManager.changeLog( any( ScmRepository.class ), any( ScmFileSet.class ), any( ScmVersion.class ),
                                    any( ScmVersion.class ) ) ).thenReturn(
            Mockito.mock( ChangeLogScmResult.class ) );

        ChangeLogScmResult result = continuumScm.changeLog( configuration );
        assertNotNull( result );
    }

    @Test
    public void testChangeLogWithStartDate()
        throws ScmException, ScmRepositoryException, NoSuchScmProviderException
    {
        ContinuumScmConfiguration configuration = Mockito.mock( ContinuumScmConfiguration.class );
        when( configuration.getWorkingDirectory() ).thenReturn( new File( "target/test/checkout" ) );
        when( configuration.getUrl() ).thenReturn( "scm:svn:http://svn.example.com/repo" );
        when( scmManager.makeScmRepository( "scm:svn:http://svn.example.com/repo" ) ).thenReturn(
            Mockito.mock( ScmRepository.class ) );
        when( configuration.getLatestUpdateDate() ).thenReturn( new Date() );
        when( scmManager.changeLog( any( ScmRepository.class ), any( ScmFileSet.class ), any( Date.class ),
                                    Mockito.isNull( Date.class ), Mockito.anyInt(), Mockito.isNull( String.class ),
                                    Mockito.isNull( String.class ) ) ).thenReturn(
            Mockito.mock( ChangeLogScmResult.class ) );

        ChangeLogScmResult result = continuumScm.changeLog( configuration );
        assertNotNull( result );
    }
}