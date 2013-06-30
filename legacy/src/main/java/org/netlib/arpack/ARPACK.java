package org.netlib.arpack;

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.NoArgsConstructor;

/**
 * @deprecated use {@link com.github.fommil.netlib.ARPACK}
 */
@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ARPACK extends com.github.fommil.netlib.ARPACK {

    private static final ARPACK INSTANCE = new ARPACK();

    /**
     * @return
     * @deprecated use {@link com.github.fommil.netlib.BLAS#getInstance()}
     */
    @Deprecated
    public static ARPACK getInstance() {
        return INSTANCE;
    }

    @Delegate
    private final com.github.fommil.netlib.ARPACK DELEGATE = com.github.fommil.netlib.ARPACK.getInstance();

}
