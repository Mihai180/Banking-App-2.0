package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class UpgradePlanCommand implements Command {
    private String command;
    private String newPlanType;
    private String account;
    private int timestamp;

    public UpgradePlanCommand(String command, String newPlanType, String account, int timestamp) {
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

    public void accept(CommandVisitor visitor) {
        visitor.visit(this);
    }
}
