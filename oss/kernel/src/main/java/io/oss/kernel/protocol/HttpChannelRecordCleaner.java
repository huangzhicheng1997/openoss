package io.oss.kernel.protocol;

import io.oss.kernel.spi.plugins.WheelTask;
import io.oss.protocol.http.HttpChannelRecord;

/**
 * @Author zhicheng
 * @Date 2021/6/6 9:31 下午
 * @Version 1.0
 */
public class HttpChannelRecordCleaner implements WheelTask {
    @Override
    public void execute() {
        HttpChannelRecord.remove();
    }

    @Override
    public Long delayMillSeconds() {
        return 200L;
    }
}
