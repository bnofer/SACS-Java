package com.sabre.api.sacs.workflow;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Class used to run a sequence of activities.
 */
public class Workflow {

    private static final Logger LOG = LogManager.getLogger(Workflow.class);
    private static final int RERUN_LIMIT = 3;
    private Activity startActivity;
    private SharedContext sharedContext;

    public Workflow(Activity startActivity) {
        this.startActivity = startActivity;
        this.sharedContext = new SharedContext();
        sharedContext.setRerunLimit(RERUN_LIMIT);
    }

    public SharedContext run() {
        sharedContext.setOwner(this);
        Activity next = startActivity;
        sharedContext.setConversationId(createConversationId());
        sharedContext.setRerun(sharedContext.getRerun() + 1);
        LOG.debug("Running workflow with the start activity: " + startActivity.toString());
        LOG.debug("for the " + sharedContext.getRerun() + " time.");
        LOG.debug("With the ConversationID: " + sharedContext.getConversationId());
        while (next != null) {
            if (!sharedContext.isFaulty()) {
                next = next.run(sharedContext);
            } else {
                break;
            }
        }
        return sharedContext;
    }

    private String createConversationId() {

        StringBuffer buffer = new StringBuffer(getTimestamp());
        buffer.append("-");
        buffer.append(longRandomHexString());
        return buffer.toString();

    }

    private String getTimestamp() {

        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddhhmmss");

        return sdf.format(new Date());

    }

    private String longRandomHexString() {

        return RandomStringUtils.randomAlphanumeric(8);
    }

}
