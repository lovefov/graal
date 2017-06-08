/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.truffle.api.debug.test;

import com.oracle.truffle.api.InstrumentInfo;
import com.oracle.truffle.api.debug.Debugger;
import com.oracle.truffle.api.instrumentation.TruffleInstrument;
import com.oracle.truffle.api.vm.PolyglotRuntime;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test that debugger is accessible to instruments.
 */
public class InstrumentLookupTest {

    @Test
    public void testCanAccessDebugger() {
        PolyglotRuntime runtime = PolyglotRuntime.newBuilder().build();
        PolyglotRuntime.Instrument debuggerInstrument = runtime.getInstruments().get("debugger");
        Assert.assertNotNull(debuggerInstrument);
        PolyglotRuntime.Instrument accessDebuggerInstrument = runtime.getInstruments().get("testAccessInstrument");
        Debugger debugger = accessDebuggerInstrument.lookup(DebuggerProvider.class).getDebugger();
        Assert.assertNotNull(debugger);
        Assert.assertEquals(debugger, debuggerInstrument.lookup(Debugger.class));
    }

    @TruffleInstrument.Registration(id = "testAccessInstrument", services = DebuggerProvider.class)
    public static class TestAccessInstrument extends TruffleInstrument implements DebuggerProvider {

        private Debugger debugger;

        @Override
        protected void onCreate(final TruffleInstrument.Env env) {
            InstrumentInfo debuggerInfo = env.getInstruments().get("debugger");
            debugger = env.lookup(debuggerInfo, Debugger.class);
            env.registerService(this);
        }

        @Override
        protected void onDispose(TruffleInstrument.Env env) {
        }

        @Override
        public Debugger getDebugger() {
            return debugger;
        }

    }

    interface DebuggerProvider {
        Debugger getDebugger();
    }

}
