package io.oss.util;

/**
 * @Author zhicheng
 * @Date 2021/4/10 3:57 下午
 * @Version 1.0
 */
public class CommandFactoryHolder {

    private static CommandFactory commandFactory;

    public static void addCommandFactory(CommandFactory commandFactory) {
        if (CommandFactoryHolder.commandFactory != null) {
            return;
        }
        CommandFactoryHolder.commandFactory = commandFactory;
    }

    public static CommandFactory getCommandFactory() {
        return commandFactory;
    }

}
