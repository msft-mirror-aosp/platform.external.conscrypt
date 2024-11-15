/* GENERATED SOURCE. DO NOT MODIFY. */
/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.org.conscrypt;

import libcore.junit.util.SwitchTargetSdkVersionRule;
import libcore.junit.util.SwitchTargetSdkVersionRule.TargetSdkVersion;

import java.security.Provider;
import javax.net.ssl.SSLSocket;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import com.android.org.conscrypt.javax.net.ssl.TestSSLContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;

/**
 * @hide This class is not part of the Android public SDK API
 */
@RunWith(JUnit4.class)
public class TlsDeprecationTest {
    @Rule
    public final TestRule switchTargetSdkVersionRule = SwitchTargetSdkVersionRule.getInstance();

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void test_SSLSocket_SSLv3Unsupported_35() throws Exception {
        assertFalse(TestUtils.isTlsV1Filtered());
        TestSSLContext context = TestSSLContext.create();
        final SSLSocket client =
                (SSLSocket) context.clientContext.getSocketFactory().createSocket();
        assertThrows(IllegalArgumentException.class, () -> client.setEnabledProtocols(new String[] {"SSLv3"}));
        assertThrows(IllegalArgumentException.class, () -> client.setEnabledProtocols(new String[] {"SSL"}));
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void test_SSLSocket_SSLv3Unsupported_34() throws Exception {
        TestSSLContext context = TestSSLContext.create();
        final SSLSocket client =
                (SSLSocket) context.clientContext.getSocketFactory().createSocket();
        // For app compatibility, SSLv3 is stripped out when setting only.
        client.setEnabledProtocols(new String[] {"SSLv3"});
        assertEquals(0, client.getEnabledProtocols().length);
        assertThrows(IllegalArgumentException.class, () -> client.setEnabledProtocols(new String[] {"SSL"}));
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void test_TLSv1Filtered_34() throws Exception {
        TestSSLContext context = TestSSLContext.create();
        final SSLSocket client =
                (SSLSocket) context.clientContext.getSocketFactory().createSocket();
        client.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"});
        assertEquals(1, client.getEnabledProtocols().length);
        assertEquals("TLSv1.2", client.getEnabledProtocols()[0]);
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void test_TLSv1Filtered_35() throws Exception {
        assertFalse(TestUtils.isTlsV1Filtered());
        TestSSLContext context = TestSSLContext.create();
        final SSLSocket client =
                (SSLSocket) context.clientContext.getSocketFactory().createSocket();
        assertThrows(IllegalArgumentException.class, () ->
            client.setEnabledProtocols(new String[] {"TLSv1", "TLSv1.1", "TLSv1.2"}));
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void testInitializeDeprecatedEnabled_34() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(true, true);
        assertTrue(TestUtils.isTlsV1Deprecated());
        assertFalse(TestUtils.isTlsV1Filtered());
        assertTrue(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void testInitializeDeprecatedEnabled_35() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(true, true);
        assertTrue(TestUtils.isTlsV1Deprecated());
        assertFalse(TestUtils.isTlsV1Filtered());
        assertTrue(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void testInitializeDeprecatedDisabled_34() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(true, false);
        assertTrue(TestUtils.isTlsV1Deprecated());
        assertTrue(TestUtils.isTlsV1Filtered());
        assertFalse(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void testInitializeDeprecatedDisabled_35() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(true, false);
        assertTrue(TestUtils.isTlsV1Deprecated());
        assertFalse(TestUtils.isTlsV1Filtered());
        assertFalse(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void testInitializeUndeprecatedEnabled_34() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(false, true);
        assertFalse(TestUtils.isTlsV1Deprecated());
        assertFalse(TestUtils.isTlsV1Filtered());
        assertTrue(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void testInitializeUndeprecatedEnabled_35() {
        Provider conscryptProvider = TestUtils.getConscryptProvider(false, true);
        assertFalse(TestUtils.isTlsV1Deprecated());
        assertFalse(TestUtils.isTlsV1Filtered());
        assertTrue(TestUtils.isTlsV1Supported());
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(34)
    public void testInitializeUndeprecatedDisabled_34() {
        assertThrows(RuntimeException.class, () -> TestUtils.getConscryptProvider(false, false));
    }

    @Test
    @SwitchTargetSdkVersionRule.TargetSdkVersion(35)
    public void testInitializeUndeprecatedDisabled_35() {
        assertThrows(RuntimeException.class, () -> TestUtils.getConscryptProvider(false, false));
    }
}