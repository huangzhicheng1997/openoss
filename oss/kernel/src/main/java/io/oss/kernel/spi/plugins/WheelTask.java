package io.oss.kernel.spi.plugins;

import io.oss.kernel.support.WheelTimer;

/**
 * 由{@link WheelTimer}进行集中资源回收，回收方法由组件自己定义
 *
 * @author zhicheng
 * @date 2021-05-07 16:52
 */
public interface WheelTask extends Component {

    /**
     * 资源清理
     */
    void execute();

    /**
     * 该任务的清理频率
     *
     * @return 毫秒
     */
    default Long delayMillSeconds() {
        return 1000L;
    }
}
