java
package cbit.vcell.message.server.dispatcher;

import cbit.vcell.message.server.htc.HtcProxy.PartitionStatistics;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationJobStatus.SchedulerStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BatchSchedulerTest {

    @Test
    void schedule_emptyJobList() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        PartitionStatistics partitionStatistics = new PartitionStatistics();
        int userQuotaOde = 10;
        int userQuotaPde = 5;
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, userQuotaOde, userQuotaPde, systemID);

        assertNotNull(decisions);
    }

    @Test
    void schedule_singleJobRunnableThisSite() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.WAITING);
        when(job.serverId).thenReturn(new VCellServerID("testServer"));
        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        partitionStatistics.numCpusTotal = 2;
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 10, 5, systemID);

        assertTrue(decisions.runnableThisSite.contains(job));
    }

    @Test
    void schedule_jobHeldClusterResources() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.WAITING);
        when(job.serverId).thenReturn(new VCellServerID("testServer"));
        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        partitionStatistics.numCpusTotal = 0;
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 10, 5, systemID);

        assertTrue(decisions.heldClusterResources.contains(job));
    }

    @Test
    void schedule_inactiveJob() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.INACTIVE);
        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 10, 5, systemID);

        assertTrue(decisions.inactiveJobs.contains(job));
    }

    @Test
    void schedule_alreadyRunningOrQueuedJob() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.RUNNING);
        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 10, 5, systemID);

        assertTrue(decisions.alreadyRunningOrQueued.contains(job));
    }

    @Test
    void schedule_userQuotaPDEExceeded() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        User user = new User();
        when(job.simulationOwner).thenReturn(user);
        when(job.isPDE).thenReturn(true);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.WAITING);
        when(job.serverId).thenReturn(new VCellServerID("testServer"));

        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 0, 0, systemID);

        assertTrue(decisions.heldUserQuotaPDE.contains(job));
    }

    @Test
    void schedule_userQuotaODEExceeded() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        User user = new User();
        when(job.simulationOwner).thenReturn(user);
        when(job.isPDE).thenReturn(false);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.WAITING);
        when(job.serverId).thenReturn(new VCellServerID("testServer"));

        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 0, 0, systemID);

        assertTrue(decisions.heldUserQuotaODE.contains(job));
    }

    @Test
    void schedule_runnableOtherSite() {
        List<ActiveJob> activeJobsAllSites = new ArrayList<>();
        ActiveJob job = Mockito.mock(ActiveJob.class);
        User user = new User();
        when(job.simulationOwner).thenReturn(user);
        when(job.isPDE).thenReturn(false);
        when(job.schedulerStatus).thenReturn(SimulationJobStatus.SchedulerStatus.WAITING);
        when(job.serverId).thenReturn(new VCellServerID("otherServer"));

        activeJobsAllSites.add(job);

        PartitionStatistics partitionStatistics = new PartitionStatistics();
		partitionStatistics.numCpusTotal = 2;
        VCellServerID systemID = new VCellServerID("testServer");

        SchedulerDecisions decisions = BatchScheduler.schedule(activeJobsAllSites, partitionStatistics, 10, 10, systemID);

        assertTrue(decisions.runnableOtherSite.contains(job));
    }
}