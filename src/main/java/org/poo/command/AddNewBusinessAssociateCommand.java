package org.poo.command;

import org.poo.visitor.command.CommandVisitor;

public class AddNewBusinessAssociateCommand implements Command {
    private final String command;
    private final String account;
    private final String role;
    private final String email;
    private final int timestamp;

    public AddNewBusinessAssociateCommand(String command, String account, String role, String email, int timestamp) {
        this.command = command;
        this.account = account;
        this.role = role;
        this.email = email;
        this.timestamp = timestamp;
    }

    public String getCommand() {
        return command;
    }

    public String getAccount() {
        return account;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    public void accept(final CommandVisitor visitor) {
        visitor.visit(this);
    }
}
