package gui.module;

import java.awt.Component;

import security.AccessPolicy;
import security.PermissionModule;

import static security.PermissionAction.*;

/** Enforces permissions even if an action is invoked without its toolbar button. */
final class AuthorizedEditor implements RecordEditor {
    private final RecordEditor delegate;
    private final AccessPolicy policy;
    private final PermissionModule module;

    AuthorizedEditor(RecordEditor delegate, AccessPolicy policy, PermissionModule module) {
        this.delegate = delegate;
        this.policy = policy;
        this.module = module;
    }

    @Override public Object show(Component parent, Object record) {
        policy.require(module, record == null ? CREATE : EDIT);
        return delegate.show(parent, record);
    }

    @Override public boolean save(Object value, boolean insert) {
        policy.require(module, insert ? CREATE : EDIT);
        return delegate.save(value, insert);
    }

    @Override public boolean delete(Object value) {
        policy.require(module, DELETE);
        return delegate.delete(value);
    }

    @Override public boolean cancel(Object value) {
        policy.require(module, CANCEL);
        return delegate.canCancel(value) && delegate.cancel(value);
    }

    @Override public boolean complete(Object value) {
        policy.require(module, COMPLETE);
        return delegate.canComplete(value) && delegate.complete(value);
    }

    @Override public Object[][] loadViewRows(Object record) {
        policy.require(module, VIEW);
        return delegate.loadViewRows(record);
    }

    @Override public void showView(Component parent, Object record, Object[][] rows) {
        policy.require(module, VIEW);
        delegate.showView(parent, record, rows);
    }

    @Override public boolean canCreate() { return policy.can(module, CREATE) && delegate.canCreate(); }
    @Override public boolean canEdit() { return policy.can(module, EDIT) && delegate.canEdit(); }
    @Override public boolean canDelete() { return policy.can(module, DELETE) && delegate.canDelete(); }
    @Override public boolean canCancel(Object value) { return policy.can(module, CANCEL) && delegate.canCancel(value); }
    @Override public boolean canComplete(Object value) { return policy.can(module, COMPLETE) && delegate.canComplete(value); }
    @Override public String createLabel() { return delegate.createLabel(); }
    @Override public String viewLabel() { return policy.can(module, VIEW) ? delegate.viewLabel() : null; }
    @Override public String cancelLabel() { return policy.can(module, CANCEL) ? delegate.cancelLabel() : null; }
    @Override public String completeLabel() { return policy.can(module, COMPLETE) ? delegate.completeLabel() : null; }
    @Override public String cancelConfirmation(Object value) { return delegate.cancelConfirmation(value); }
    @Override public String completeConfirmation() { return delegate.completeConfirmation(); }
}
