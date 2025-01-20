package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public final class BusinessReportCommand implements Command {
    private final String command;
    private final String type;
    private final int startTimestamp;
    private final int endTimestamp;
    private final String account;
    private final int timestamp;

    public BusinessReportCommand(final String command, final String type,
                                 final int startTimestamp, final int endTimestamp,
                                 final String account, final int timestamp) {
        this.command = command;
        this.type = type;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getType() {
        return type;
    }

    public int getStartTimestamp() {
        return startTimestamp;
    }

    public int getEndTimestamp() {
        return endTimestamp;
    }

    public String getAccount() {
        return account;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
