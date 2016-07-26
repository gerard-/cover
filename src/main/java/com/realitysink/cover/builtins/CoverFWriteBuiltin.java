/*
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.realitysink.cover.builtins;

import java.io.IOException;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.realitysink.cover.nodes.SLExpressionNode;
import com.realitysink.cover.runtime.CoverRuntimeException;

/**
 * size_t fwrite ( const void * ptr, size_t size, size_t count, FILE * stream );
 */
@NodeInfo(shortName = "fwrite")
@NodeChildren({@NodeChild("ptr"), @NodeChild("size"), @NodeChild("count"), @NodeChild("stream")})
public abstract class CoverFWriteBuiltin extends SLExpressionNode {

    // FIXME: should take a byte[] directly!
    @Specialization
    public Object fwrite(Object[] ptr, long size, long count, long stream) {
        long totalSize = size * count;
        byte[] bytes = new byte[(int) totalSize];
        for (int i=0;i<totalSize;i++) {
            long value = (long) ptr[i];
            bytes[i] = (byte)value;
        }
        doWrite(bytes, size, count, stream);
        return null; // is actually a void function
    }
    
    @Specialization
    public Object fwrite(long[] ptr, long size, long count, long stream) {
        long totalSize = size * count;
        byte[] bytes = new byte[(int) totalSize];
        for (int i=0;i<totalSize;i++) {
            bytes[i] = (byte) ptr[i];
        }
        doWrite(bytes, size, count, stream);
        return null; // is actually a void function
    }

    @TruffleBoundary
    private void doWrite(byte[] bytes, long size, long count, long stream) {
        // stream is ignored, we always write to stdout
        try {
            System.out.write(bytes);
        } catch (IOException e) {
            throw new CoverRuntimeException(this, e);
        }
    }
}