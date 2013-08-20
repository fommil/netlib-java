package org.netlib.lapack;

import lombok.AccessLevel;
import lombok.Delegate;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

/**
 * @deprecated use {@link com.github.fommil.netlib.LAPACK}
 */
@Deprecated
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log
public class LAPACK extends com.github.fommil.netlib.LAPACK {

  private static final LAPACK INSTANCE = new LAPACK();

  /**
   * @return
   * @deprecated use {@link com.github.fommil.netlib.LAPACK#getInstance()}
   */
  @Deprecated
  public static LAPACK getInstance() {
    log.warning("this API is deprecated and will be removed. Instead, use com.github.fommil.netlib.LAPACK");
    return INSTANCE;
  }

  @Delegate
  private final com.github.fommil.netlib.LAPACK DELEGATE = com.github.fommil.netlib.LAPACK.getInstance();

}
