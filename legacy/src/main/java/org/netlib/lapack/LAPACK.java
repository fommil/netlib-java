package org.netlib.lapack;

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.NoArgsConstructor;

/**
 * @deprecated use {@link com.github.fommil.netlib.LAPACK}
 */
@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LAPACK extends com.github.fommil.netlib.LAPACK {

    private static final LAPACK INSTANCE = new LAPACK();

    /**
     * @return
     * @deprecated use {@link com.github.fommil.netlib.LAPACK#getInstance()}
     */
    @Deprecated
    public static LAPACK getInstance() {
        return INSTANCE;
    }

    @Delegate
    private final com.github.fommil.netlib.LAPACK DELEGATE = com.github.fommil.netlib.LAPACK.getInstance();

}
