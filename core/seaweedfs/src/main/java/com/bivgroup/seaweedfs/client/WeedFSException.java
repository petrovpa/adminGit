package com.bivgroup.seaweedfs.client;

import java.io.IOException;

public class WeedFSException extends IOException {

    private static final long serialVersionUID = 1L;

    public WeedFSException(String reason) {
        super(reason);
    }

    public WeedFSException(Exception cause) {
        super(cause);
    }

    public WeedFSException(String reason, Exception cause) {
        super(reason, cause);
    }

}
