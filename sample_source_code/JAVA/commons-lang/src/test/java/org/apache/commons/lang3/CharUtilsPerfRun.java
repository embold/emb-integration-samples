/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang3;

import java.text.NumberFormat;
import java.util.Calendar;

/**
 * Tests the difference in performance between CharUtils and CharSet.
 *
 * Sample runs:

Now: Thu Mar 18 14:29:48 PST 2004
Sun Microsystems Inc. Java(TM) 2 Runtime Environment, Standard Edition 1.3.1_10-b03
Sun Microsystems Inc. Java HotSpot(TM) Client VM 1.3.1_10-b03
Windows XP 5.1 x86 pentium i486 i386
Do nothing: 0 milliseconds.
run_CharUtils_isAsciiNumeric: 4,545 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 3,417 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 85,679 milliseconds.

Now: Thu Mar 18 14:24:51 PST 2004
Sun Microsystems Inc. Java(TM) 2 Runtime Environment, Standard Edition 1.4.2_04-b05
Sun Microsystems Inc. Java HotSpot(TM) Client VM 1.4.2_04-b05
Windows XP 5.1 x86 pentium i486 i386
Do nothing: 0 milliseconds.
run_CharUtils_isAsciiNumeric: 2,578 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 2,477 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 114,429 milliseconds.

Now: Thu Mar 18 14:27:55 PST 2004
Sun Microsystems Inc. Java(TM) 2 Runtime Environment, Standard Edition 1.4.2_04-b05
Sun Microsystems Inc. Java HotSpot(TM) Server VM 1.4.2_04-b05
Windows XP 5.1 x86 pentium i486 i386
Do nothing: 0 milliseconds.
run_CharUtils_isAsciiNumeric: 630 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 709 milliseconds.
run_inlined_CharUtils_isAsciiNumeric: 84,420 milliseconds.

 */
public class CharUtilsPerfRun {

    private static final int WARM_UP = 100;

    private static final int COUNT = 5000;

    private static final char[] ALL_CHARS;

    static {
        ALL_CHARS = new char[Character.MAX_VALUE];
        for (char i = Character.MIN_VALUE; i < Character.MAX_VALUE; i++) {
            ALL_CHARS[i] = i;
        }
    }

    public static void main(final String[] args) {
        new CharUtilsPerfRun().run();
    }

    private void printlnTotal(final String prefix, final long startMillis) {
        final long totalMillis = System.currentTimeMillis() - startMillis;
        System.out.println(prefix + ": " + NumberFormat.getInstance().format(totalMillis) + " milliseconds.");
    }

    private void printSysInfo() {
        System.out.println("Now: " + Calendar.getInstance().getTime());
        System.out.println(SystemProperties.getJavaVendor()
                + " "
                + SystemProperties.getJavaRuntimeName()
                + " "
                + SystemProperties.getJavaRuntimeVersion());
        System.out.println(SystemProperties.getJavaVmVendor()
                + " "
                + SystemProperties.getJavaVmName()
                + " "
                + SystemProperties.getJavaVmVersion());
        System.out.println(SystemProperties.getOsName()
            + " "
            + SystemProperties.getOsVersion()
            + " "
            + SystemProperties.getOsArch()
            + " "
            + System.getProperty("sun.cpu.isalist"));
    }

    private void run() {
        printSysInfo();
        long startMillis;
        startMillis = System.currentTimeMillis();
        printlnTotal("Do nothing", startMillis);
        run_CharUtils_isAsciiNumeric(WARM_UP);
        startMillis = System.currentTimeMillis();
        run_CharUtils_isAsciiNumeric(COUNT);
        printlnTotal("run_CharUtils_isAsciiNumeric", startMillis);
        run_inlined_CharUtils_isAsciiNumeric(WARM_UP);
        startMillis = System.currentTimeMillis();
        run_inlined_CharUtils_isAsciiNumeric(COUNT);
        printlnTotal("run_inlined_CharUtils_isAsciiNumeric", startMillis);
        run_CharSet(WARM_UP);
        startMillis = System.currentTimeMillis();
        run_CharSet(COUNT);
        printlnTotal("run_CharSet", startMillis);
    }

    private int run_CharSet(final int loopCount) {
        int t = 0;
        for (int i = 0; i < loopCount; i++) {
            for (final char ch : ALL_CHARS) {
                final boolean b = CharSet.ASCII_NUMERIC.contains(ch);
                t += b ? 1 : 0;
            }
        }
        return t;
    }

    private int run_CharUtils_isAsciiNumeric(final int loopCount) {
        int t = 0;
        for (int i = 0; i < loopCount; i++) {
            for (final char ch : ALL_CHARS) {
                final boolean b = CharUtils.isAsciiNumeric(ch);
                t += b ? 1 : 0;
            }
        }
        return t;
    }

    private int run_inlined_CharUtils_isAsciiNumeric(final int loopCount) {
        int t = 0;
        for (int i = 0; i < loopCount; i++) {
            for (final char ch : ALL_CHARS) {
                final boolean b = ch >= '0' && ch <= '9';
                t += b ? 1 : 0;
            }
        }
        return t;
    }
}
