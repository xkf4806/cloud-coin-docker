package com.ourdax.coindocker.job;

import com.google.common.collect.Maps;
import com.ourdax.coindocker.job.collectJob.CollectTransQueryJob;
import com.ourdax.coindocker.job.collectJob.CollectionFeeRechargeJob;
import com.ourdax.coindocker.job.collectJob.CollectionJob;
import com.ourdax.coindocker.job.collectJob.UniformAccountMonitorJob;
import com.ourdax.coindocker.job.majorjob.NewTransFindingJob;
import com.ourdax.coindocker.job.majorjob.TransferInQueryJob;
import com.ourdax.coindocker.job.majorjob.TransferOutQueryJob;
import com.ourdax.coindocker.job.majorjob.TransferOutSendingJob;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author think on 3/2/2018
 */
public abstract class DefaultJobScheduler extends JobScheduler {

    private static final Random rand = new Random(System.currentTimeMillis());

    @Autowired
    NewTransFindingJob newTransFindingJob;

    @Autowired
    TransferInQueryJob transferInQueryJob;

    @Autowired
    TransferOutQueryJob transferOutQueryJob;

    @Autowired
    TransferOutSendingJob transferOutSendingJob;

    /**
     * 以下三个为币归集job
     */
    @Autowired
    CollectionFeeRechargeJob collectionFeeRechargeJob;

    @Autowired
    CollectionJob collectionJob;

    @Autowired
    UniformAccountMonitorJob uniformAccountMonitorJob;

    @Autowired
    CollectTransQueryJob collectTransQueryJob;

    private static final long DEFAULT_NEW_TRANS_FINDING_RATE = 10;

    private static final long DEFAULT_RATE = 30;

    protected long getNewTransFindingRate() {
        return DEFAULT_NEW_TRANS_FINDING_RATE;
    }

    protected long getTransferInRate() {
        return DEFAULT_RATE;
    }

    protected long getTransferOutQueryRate() {
        return DEFAULT_RATE;
    }

    protected long getTransferOutSendingRate() {
        return DEFAULT_RATE;
    }

    /**
     * 币归集
     */
    protected long getCollectionFeeRechargeRate() {
        return DEFAULT_RATE;
    }

    protected long getCollectionJobRate() {
        return DEFAULT_RATE;
    }

    protected long getUniformAccountMonitorRate() {
        return DEFAULT_RATE;
    }

    protected long getCollectionQueryJobRate(){
        return DEFAULT_RATE;
    }

    @Override
    protected Map<String, JobConfig> getJobConfigs() {
        Map<String, JobConfig> jobConfigs = Maps.newHashMap();
        jobConfigs.put(newTransFindingJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getNewTransFindingRate(),
                        TimeUnit.SECONDS));
        jobConfigs.put(transferInQueryJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getTransferInRate(), TimeUnit.SECONDS));
        jobConfigs.put(transferOutQueryJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getTransferOutQueryRate(),
                        TimeUnit.SECONDS));
        jobConfigs.put(transferOutSendingJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getTransferOutSendingRate(),
                        TimeUnit.SECONDS));
        /**币归集*/
        jobConfigs.put(collectionFeeRechargeJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getCollectionFeeRechargeRate(),
                        TimeUnit.SECONDS));
        jobConfigs.put(collectionJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getCollectionJobRate(), TimeUnit.SECONDS));
        jobConfigs.put(uniformAccountMonitorJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getUniformAccountMonitorRate(),
                        TimeUnit.SECONDS));
        jobConfigs.put(collectTransQueryJob.getName(),
                new JobConfig(getAssetCode(), getRandomDelay(), getCollectionQueryJobRate(),
                        TimeUnit.SECONDS));
        return jobConfigs;
    }

    private long getRandomDelay() {
        return rand.nextInt(10);
    }

}