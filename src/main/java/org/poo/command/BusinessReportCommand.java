package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class BusinessReportCommand implements Command {
    private final String Command;
    private final String type;
    private final int startTimestamp;
    private final int endTimestamp;
    private final String account;
    private final int timestamp;

    public BusinessReportCommand(String command, String type, int startTimestamp, int endTimestamp, String account, int timestamp) {
        this.Command = command;
        this.type = type;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return Command;
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
