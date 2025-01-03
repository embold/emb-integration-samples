/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.commons.lang3;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import org.apache.commons.lang3.ThreadUtils.ThreadGroupPredicate;
import org.apache.commons.lang3.ThreadUtils.ThreadPredicate;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link ThreadUtils}.
 */
public class ThreadUtilsTest extends AbstractLangTest {

    private static final class TestThread extends Thread {
        private final CountDownLatch latch = new CountDownLatch(1);

        TestThread(final String name) {
            super(name);
        }

        TestThread(final ThreadGroup group, final String name) {
            super(group, name);
        }

        @Override
        public void run() {
            latch.countDown();
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public synchronized void start() {
            super.start();
            try {
                latch.await();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Test
    public void testAtLeastOneThreadExists() {
        assertFalse(ThreadUtils.getAllThreads().isEmpty());
    }

    @Test
    public void testAtLeastOneThreadGroupsExists() {
        assertFalse(ThreadUtils.getAllThreadGroups().isEmpty());
    }

    @Test
    public void testComplexThreadGroups() throws Exception {
        final ThreadGroup threadGroup1 = new ThreadGroup("thread_group_1__");
        final ThreadGroup threadGroup2 = new ThreadGroup("thread_group_2__");
        final ThreadGroup threadGroup3 = new ThreadGroup(threadGroup2, "thread_group_3__");
        final ThreadGroup threadGroup4 = new ThreadGroup(threadGroup2, "thread_group_4__");
        final ThreadGroup threadGroup5 = new ThreadGroup(threadGroup1, "thread_group_5__");
        final ThreadGroup threadGroup6 = new ThreadGroup(threadGroup4, "thread_group_6__");
        final ThreadGroup threadGroup7 = new ThreadGroup(threadGroup4, "thread_group_7__");
        final ThreadGroup threadGroup7Doubled = new ThreadGroup(threadGroup4, "thread_group_7__");
        final List<ThreadGroup> threadGroups = Arrays.asList(threadGroup1, threadGroup2, threadGroup3, threadGroup4, threadGroup5, threadGroup6, threadGroup7,
            threadGroup7Doubled);

        final Thread t1 = new TestThread("thread1_X__");
        final Thread t2 = new TestThread(threadGroup1, "thread2_X__");
        final Thread t3 = new TestThread(threadGroup2, "thread3_X__");
        final Thread t4 = new TestThread(threadGroup3, "thread4_X__");
        final Thread t5 = new TestThread(threadGroup4, "thread5_X__");
        final Thread t6 = new TestThread(threadGroup5, "thread6_X__");
        final Thread t7 = new TestThread(threadGroup6, "thread7_X__");
        final Thread t8 = new TestThread(threadGroup4, "thread8_X__");
        final Thread t9 = new TestThread(threadGroup6, "thread9_X__");
        final Thread t10 = new TestThread(threadGroup3, "thread10_X__");
        final Thread t11 = new TestThread(threadGroup7, "thread11_X__");
        final Thread t11Doubled = new TestThread(threadGroup7Doubled, "thread11_X__");
        final List<Thread> threads = Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t11Doubled);

        try {
            for (final Thread thread : threads) {
                thread.start();
            }
            assertThat("getAllThreadGroups", ThreadUtils.getAllThreadGroups().size(), greaterThanOrEqualTo(7));
            assertThat("getAllThreads", ThreadUtils.getAllThreads().size(), greaterThanOrEqualTo(11));
            assertThat("findThreads(ThreadUtils.ALWAYS_TRUE_PREDICATE)", ThreadUtils.findThreads(ThreadUtils.ALWAYS_TRUE_PREDICATE).size(), greaterThanOrEqualTo(11));
            assertEquals(1, ThreadUtils.findThreadsByName(t4.getName(), threadGroup3.getName()).size());
            assertEquals(0, ThreadUtils.findThreadsByName(t4.getName(), threadGroup2.getName()).size());
            assertEquals(2, ThreadUtils.findThreadsByName(t11.getName(), threadGroup7.getName()).size());
        } finally {
            for (final Thread thread : threads) {
                thread.interrupt();
                thread.join();
            }
            for (final ThreadGroup threadGroup : threadGroups) {
                if (!threadGroup.isDestroyed()) {
                    threadGroup.destroy();
                }
            }
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ThreadUtils());
        final Constructor<?>[] cons = ThreadUtils.class.getDeclaredConstructors();
        assertEquals(1, cons.length);
        assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        assertTrue(Modifier.isPublic(ThreadUtils.class.getModifiers()));
        assertFalse(Modifier.isFinal(ThreadUtils.class.getModifiers()));
    }

    @Test
    public void testGetAllThreadGroupsDoesNotReturnNull() {
        // LANG-1706 getAllThreadGroups and findThreadGroups should not return null items
        final Collection<ThreadGroup> threads = ThreadUtils.getAllThreadGroups();
        assertEquals(0, threads.stream().filter(Objects::isNull).count());
    }

    @Test
    public void testGetAllThreadsDoesNotReturnNull() {
        // LANG-1706 getAllThreads and findThreads should not return null items
        final Collection<Thread> threads = ThreadUtils.getAllThreads();
        assertEquals(0, threads.stream().filter(Objects::isNull).count());
    }

    @Test
    public void testInvalidThreadId() {
        assertThrows(IllegalArgumentException.class, () -> ThreadUtils.findThreadById(-5L));
    }

    @Test
    public void testJoinDuration() throws InterruptedException {
        ThreadUtils.join(new Thread(), Duration.ZERO);
        ThreadUtils.join(new Thread(), Duration.ofMillis(1));
    }

    @Test
    public void testNoThread() {
        assertEquals(0, ThreadUtils.findThreadsByName("some_thread_which_does_not_exist_18762ZucTT").size());
    }

    @Test
    public void testNoThreadGroup() {
        assertEquals(0, ThreadUtils.findThreadGroupsByName("some_thread_group_which_does_not_exist_18762ZucTTII").size());
    }

    @Test
    public void testNullThreadGroupName() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroupsByName(null));
    }

    @Test
    public void testNullThreadName() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName(null));
    }

    @Test
    public void testNullThreadThreadGroup1() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName("tname", (ThreadGroup) null));
    }

    @Test
    public void testNullThreadThreadGroup2() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadById(1L, (ThreadGroup) null));
    }

    @Test
    public void testNullThreadThreadGroup3() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName(null, (ThreadGroup) null));
    }

    @Test
    public void testNullThreadThreadGroupName1() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName(null, "tgname"));
    }

    @Test
    public void testNullThreadThreadGroupName2() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName("tname", (String) null));
    }

    @Test
    public void testNullThreadThreadGroupName3() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadsByName(null, (String) null));
    }

    @Test
    public void testSleepDuration() throws InterruptedException {
        ThreadUtils.sleep(Duration.ZERO);
        ThreadUtils.sleep(Duration.ofMillis(1));
    }

    @Test
    public void testSystemThreadGroupExists() {
        final ThreadGroup systemThreadGroup = ThreadUtils.getSystemThreadGroup();
        assertNotNull(systemThreadGroup);
        assertNull(systemThreadGroup.getParent());
        assertEquals("system", systemThreadGroup.getName());
    }

    @Test
    public void testThreadGroups() throws InterruptedException {
        final String threadGroupName = "thread_group_DDZZ99__for_testThreadGroups";
        final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
        final Thread t1 = new TestThread(threadGroup, "thread1_XXOOPP__");
        final Thread t2 = new TestThread(threadGroup, "thread2_XXOOPP__");

        try {
            t1.start();
            t2.start();
            assertEquals(1, ThreadUtils.findThreadsByName("thread1_XXOOPP__").size());
            assertEquals(1, ThreadUtils.findThreadsByName("thread1_XXOOPP__", threadGroupName).size());
            assertEquals(1, ThreadUtils.findThreadsByName("thread2_XXOOPP__", threadGroupName).size());
            assertEquals(0, ThreadUtils.findThreadsByName("thread1_XXOOPP__", "non_existent_thread_group_JJHHZZ__").size());
            assertEquals(0, ThreadUtils.findThreadsByName("non_existent_thread_BBDDWW__", threadGroupName).size());
            assertEquals(1, ThreadUtils.findThreadGroupsByName(threadGroupName).size());
            assertEquals(0, ThreadUtils.findThreadGroupsByName("non_existent_thread_group_JJHHZZ__").size());
            assertNotNull(ThreadUtils.findThreadById(t1.getId(), threadGroup));
        } finally {
            t1.interrupt();
            t2.interrupt();
            t1.join();
            t2.join();
            threadGroup.destroy();
        }
    }

    @Test
    public void testThreadGroupsById() throws InterruptedException {
        final String threadGroupName = "thread_group_DDZZ99__for_testThreadGroupsById";
        final ThreadGroup threadGroup = new ThreadGroup(threadGroupName);
        final Thread t1 = new TestThread(threadGroup, "thread1_XXOOPP__");
        final Thread t2 = new TestThread(threadGroup, "thread2_XXOOPP__");
        final long nonExistingId = t1.getId() + t2.getId();

        try {
            t1.start();
            t2.start();
            assertSame(t1, ThreadUtils.findThreadById(t1.getId(), threadGroupName));
            assertSame(t2, ThreadUtils.findThreadById(t2.getId(), threadGroupName));
            assertNull(ThreadUtils.findThreadById(nonExistingId, "non_existent_thread_group_JJHHZZ__"));
            assertNull(ThreadUtils.findThreadById(nonExistingId, threadGroupName));
        } finally {
            t1.interrupt();
            t2.interrupt();
            t1.join();
            t2.join();
            threadGroup.destroy();
        }
    }

    @Test
    public void testThreadGroupsByIdFail() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadById(Thread.currentThread().getId(), (String) null));
    }

    @Test
    public void testThreadGroupsNullParent() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroups(null, true, ThreadUtils.ALWAYS_TRUE_PREDICATE));
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroups(null, false, ThreadUtils.ALWAYS_TRUE_PREDICATE));
    }

    @Test
    public void testThreadGroupsNullPredicate() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroups((ThreadGroupPredicate) null));
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroups((Predicate<ThreadGroup>) null));
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreadGroups((Predicate) null));
    }

    @Test
    public void testThreadGroupsRef() throws InterruptedException {
        final ThreadGroup threadGroup = new ThreadGroup("thread_group_DDZZ99__");
        final ThreadGroup deadThreadGroup = new ThreadGroup("dead_thread_group_MMQQSS__");
        deadThreadGroup.destroy();
        final Thread t1 = new TestThread(threadGroup, "thread1_XXOOPP__");
        final Thread t2 = new TestThread(threadGroup, "thread2_XXOOPP__");

        try {
            t1.start();
            t2.start();
            assertEquals(1, ThreadUtils.findThreadsByName("thread1_XXOOPP__").size());
            assertEquals(1, ThreadUtils.findThreadsByName("thread1_XXOOPP__", threadGroup).size());
            assertEquals(1, ThreadUtils.findThreadsByName("thread2_XXOOPP__", threadGroup).size());
            assertEquals(0, ThreadUtils.findThreadsByName("thread1_XXOOPP__", deadThreadGroup).size());
        } finally {
            t1.interrupt();
            t2.interrupt();
            t1.join();
            t2.join();
            threadGroup.destroy();
            assertEquals(0, ThreadUtils.findThreadsByName("thread2_XXOOPP__", threadGroup).size());
        }
    }

    @Test
    public void testThreads() throws InterruptedException {
        final Thread t1 = new TestThread("thread1_XXOOLL__");
        final Thread t2 = new TestThread("thread2_XXOOLL__");

        try {
            t1.start();
            t2.start();
            assertEquals(1, ThreadUtils.findThreadsByName("thread2_XXOOLL__").size());
        } finally {
            t1.interrupt();
            t2.interrupt();
            t1.join();
            t2.join();
        }
    }

    @Test
    public void testThreadsById() throws InterruptedException {
        final Thread t1 = new TestThread("thread1_XXOOLL__");
        final Thread t2 = new TestThread("thread2_XXOOLL__");

        try {
            t1.start();
            t2.start();
            assertSame(t1, ThreadUtils.findThreadById(t1.getId()));
            assertSame(t2, ThreadUtils.findThreadById(t2.getId()));
        } finally {
            t1.interrupt();
            t2.interrupt();
            t1.join();
            t2.join();
        }
    }

    @Test
    public void testThreadsByIdWrongGroup() throws InterruptedException {
        final Thread t1 = new TestThread("thread1_XXOOLL__");
        final ThreadGroup tg = new ThreadGroup("tg__HHEE22");

        try {
            t1.start();
            assertNull(ThreadUtils.findThreadById(t1.getId(), tg));
        } finally {
            t1.interrupt();
            t1.join();
            tg.destroy();
        }
    }

    @Test
    public void testThreadsNullPredicate() {
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreads((ThreadPredicate) null));
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreads((Predicate<Thread>) null));
        assertThrows(NullPointerException.class, () -> ThreadUtils.findThreads((Predicate) null));
    }

    @Test
    public void testThreadsSameName() throws InterruptedException {
        final Thread t1 = new TestThread("thread1_XXOOLL__");
        final Thread alsot1 = new TestThread("thread1_XXOOLL__");

        try {
            t1.start();
            alsot1.start();
            assertEquals(2, ThreadUtils.findThreadsByName("thread1_XXOOLL__").size());
        } finally {
            t1.interrupt();
            alsot1.interrupt();
            t1.join();
            alsot1.join();
        }
    }

}
