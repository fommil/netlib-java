package org.netlib.blas;

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.NoArgsConstructor;

/**
 * @deprecated use {@link com.github.fommil.netlib.BLAS}
 */
@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BLAS extends com.github.fommil.netlib.BLAS {

    private static final BLAS INSTANCE = new BLAS();

    /**
     * @return
     * @deprecated use {@link com.github.fommil.netlib.BLAS#getInstance()}
     */
    @Deprecated
    public static BLAS getInstance() {
        return INSTANCE;
    }

    @Delegate
    private final com.github.fommil.netlib.BLAS DELEGATE = com.github.fommil.netlib.BLAS.getInstance();

}
