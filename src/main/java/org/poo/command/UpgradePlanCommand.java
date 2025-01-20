package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public final class UpgradePlanCommand implements Command {
    private String command;
    private String newPlanType;
    private String account;
    private int timestamp;

    public UpgradePlanCommand(final String command, final String newPlanType,
                              final String account, final int timestamp) {
        this.command = command;
        this.newPlanType = newPlanType;
        this.account = account;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getNewPlanType() {
        return newPlanType;
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
