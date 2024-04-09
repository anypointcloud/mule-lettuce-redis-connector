package cloud.anypoint.redis.api.operation.args;

import org.mule.runtime.extension.api.annotation.param.Parameter;

public class ZAddArguments {
    @Parameter private boolean xx;
    @Parameter private boolean nx;
    @Parameter private boolean lt;
    @Parameter private boolean gt;
    @Parameter private boolean ch;

    public boolean isXx() {
        return xx;
    }

    public boolean isNx() {
        return nx;
    }

    public boolean isLt() {
        return lt;
    }

    public boolean isGt() {
        return gt;
    }

    public boolean isCh() {
        return ch;
    }
}
