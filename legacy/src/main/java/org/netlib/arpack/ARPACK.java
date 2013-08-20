package org.netlib.arpack;

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * @deprecated use {@link com.github.fommil.netlib.ARPACK}
 */
@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log
public class ARPACK extends com.github.fommil.netlib.ARPACK {

  private static final ARPACK INSTANCE = new ARPACK();

  /**
   * @return
   * @deprecated use {@link com.github.fommil.netlib.BLAS#getInstance()}
   */
  @Deprecated
  public static ARPACK getInstance() {
    log.warning("this API is deprecated and will be removed. Instead, use com.github.fommil.netlib.ARPACK");
    return INSTANCE;
  }

  @Delegate
  private final com.github.fommil.netlib.ARPACK DELEGATE = com.github.fommil.netlib.ARPACK.getInstance();

}
