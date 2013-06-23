package org.netlib;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;

/**
 * Gathers the functions from an F2J style jar file and provides convenient access.
 * <p>
 * Note that due to limitations in the binary format, parameter names are not
 * available for methods.
 *
 * @author Sam Halliday
 */
@RequiredArgsConstructor
public class F2JFunctionGatherer {

    @NonNull
    private final File jar;



}
