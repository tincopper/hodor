package org.dromara.hodor.client.action;

import cn.hutool.core.date.DateUtil;
import java.util.Date;
import org.draomara.hodor.model.executor.JobExecuteStatus;
import org.dromara.hodor.client.JobExecutionContext;
import org.dromara.hodor.client.JobParameter;
import org.dromara.hodor.client.JobRegistrar;
import org.dromara.hodor.client.ServiceProvider;
import org.dromara.hodor.client.core.RequestContext;
import org.dromara.hodor.client.core.ScheduledMethodRunnable;
import org.dromara.hodor.remoting.api.message.request.JobExecuteRequest;
import org.dromara.hodor.remoting.api.message.response.JobExecuteResponse;

/**
 * job execution
 *
 * @author tomgs
 * @since 2021/3/2
 */
public class JobExecuteAction extends AbstractExecuteAction {

    private final JobRegistrar jobRegistrar;

    public JobExecuteAction(RequestContext context) {
        super(context);
        this.jobRegistrar = ServiceProvider.getInstance().getBean(JobRegistrar.class);
    }

    @Override
    public JobExecuteResponse executeRequest0(JobExecuteRequest request) {
        ScheduledMethodRunnable jobRunnable = jobRegistrar.getJobRunnable(request.getGroupName(), request.getJobName());
        if (jobRunnable == null) {
            throw new IllegalArgumentException(String.format("not found job %s_%s.", request.getGroupName(), request.getJobName()));
        }
        if (jobRunnable.isHasArg()) {
            final JobParameter jobParameter = new JobParameter(request.getGroupName(), request.getJobName(), request.getRequestId(),
                request.getJobParameters(), request.getShardId(), request.getShardName());
            final JobExecutionContext context = new JobExecutionContext(getLogger(), jobParameter);
            jobRunnable.setArgs(context);
        }

        jobRunnable.run();

        // to set job return result to response
        JobExecuteResponse response = buildResponse(request);
        response.setStatus(JobExecuteStatus.SUCCESS);
        response.setCompleteTime(DateUtil.formatDateTime(new Date()));
        return response;
    }

}
