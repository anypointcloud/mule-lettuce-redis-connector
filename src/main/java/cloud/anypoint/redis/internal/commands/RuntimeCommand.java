package cloud.anypoint.redis.internal.commands;

import io.lettuce.core.protocol.ProtocolKeyword;

import java.nio.charset.StandardCharsets;

public class RuntimeCommand implements ProtocolKeyword {
    private final String command;
    public RuntimeCommand(String commandText) {
        this.command = commandText;
    }

    @Override
    public byte[] getBytes() {
        return command.getBytes(StandardCharsets.US_ASCII);
    }

    @Override
    public String name() {
        return command;
    }
}
