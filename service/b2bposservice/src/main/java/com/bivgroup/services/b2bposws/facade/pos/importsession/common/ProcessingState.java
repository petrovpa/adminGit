package com.bivgroup.services.b2bposws.facade.pos.importsession.common;

public enum ProcessingState {

    NOT_PROCESSED(false, false),
    PROCESSED_WITH_ERROR(true, true),
    PROCESSED_SUCCESSFULLY(true, false);

    public final boolean isProcessed;
    public final boolean isNotProcessed;
    public final boolean isError;
    public final boolean isNoError;

    ProcessingState(boolean isProcessed, boolean isError) {
        this.isProcessed = isProcessed;
        this.isNotProcessed = !isProcessed;
        this.isError = isError;
        this.isNoError = !isError;
    }

}
