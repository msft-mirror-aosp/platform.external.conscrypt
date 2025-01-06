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

package android.net.ssl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import android.platform.test.annotations.RequiresFlagsEnabled;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(JUnit4.class)
public class PakeServerKeyManagerParametersTest {
    private static final byte[] CLIENT_ID_1 = new byte[] {1, 2, 3};
    private static final byte[] SERVER_ID_1 = new byte[] {4, 5, 6};
    private static final byte[] CLIENT_ID_2 = new byte[] {7, 8, 9};
    private static final byte[] SERVER_ID_2 = new byte[] {10, 11, 12};
    private static final byte[] PASSWORD_BYTES = new byte[] {1, 2, 3};

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_valid() {
        PakeOption option1 = createOption("SPAKE2PLUS_PRERELEASE", "password");
        PakeOption option2 = createOption("SPAKE2PLUS_PRERELEASE", "w0", "registration_record");

        PakeServerKeyManagerParameters params =
                new PakeServerKeyManagerParameters.Builder()
                        .setOptions(CLIENT_ID_1, SERVER_ID_1, List.of(option1))
                        .setOptions(CLIENT_ID_2, SERVER_ID_2, List.of(option2))
                        .build();

        assertEquals(option1, params.getOptions(CLIENT_ID_1, SERVER_ID_1).get(0));
        assertEquals(option2, params.getOptions(CLIENT_ID_2, SERVER_ID_2).get(0));
    }

    @Test(expected = InvalidParameterException.class)
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_noLinks() {
        new PakeServerKeyManagerParameters.Builder().build();
    }

    @Test(expected = NullPointerException.class)
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_nullOption() {
        new PakeServerKeyManagerParameters.Builder().setOptions(
                CLIENT_ID_1, SERVER_ID_1, List.of((PakeOption) null));
    }

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_duplicateOptionAlgorithm() {
        PakeOption option = createOption("SPAKE2PLUS_PRERELEASE", "password");
        PakeOption sameOption = createOption("SPAKE2PLUS_PRERELEASE", "password");
        assertThrows(InvalidParameterException.class,
                ()
                        -> new PakeServerKeyManagerParameters.Builder().setOptions(
                                CLIENT_ID_1, SERVER_ID_1, List.of(option, sameOption)));
    }

    @Test(expected = InvalidParameterException.class)
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_linkWithNoOptions() {
        new PakeServerKeyManagerParameters.Builder().setOptions(
                CLIENT_ID_1, SERVER_ID_1, new ArrayList());
    }

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testGetOptions_nonExistingLink() {
        PakeOption option1 = createOption("SPAKE2PLUS_PRERELEASE", "password");

        PakeServerKeyManagerParameters params =
                new PakeServerKeyManagerParameters.Builder()
                        .setOptions(CLIENT_ID_1, SERVER_ID_1, List.of(option1))
                        .build();

        PakeServerKeyManagerParameters.Link nonExistingLink =
                new PakeServerKeyManagerParameters.Link(CLIENT_ID_2, SERVER_ID_2);
        assertThrows(InvalidParameterException.class, () -> params.getOptions(nonExistingLink));
    }

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testGetLinks() {
        PakeOption option1 = createOption("SPAKE2PLUS_PRERELEASE", "password");
        PakeOption option2 = createOption("SPAKE2PLUS_PRERELEASE", "w0", "registration_record");

        PakeServerKeyManagerParameters params =
                new PakeServerKeyManagerParameters.Builder()
                        .setOptions(CLIENT_ID_1, SERVER_ID_1, List.of(option1))
                        .setOptions(CLIENT_ID_2, SERVER_ID_2, List.of(option2))
                        .build();

        PakeServerKeyManagerParameters.Link link1 =
                new PakeServerKeyManagerParameters.Link(CLIENT_ID_1, SERVER_ID_1);
        PakeServerKeyManagerParameters.Link link2 =
                new PakeServerKeyManagerParameters.Link(CLIENT_ID_2, SERVER_ID_2);
        Set<PakeServerKeyManagerParameters.Link> expectedLinks = new HashSet<>();
        expectedLinks.add(link1);
        expectedLinks.add(link2);
        assertEquals(expectedLinks, params.getLinks());
    }

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_spake2PlusPrerelease_w0WithoutRegistrationRecord() {
        PakeOption option = createOption("SPAKE2PLUS_PRERELEASE", "w0", "w1");
        assertThrows(InvalidParameterException.class,
                ()
                        -> new PakeServerKeyManagerParameters.Builder().setOptions(
                                CLIENT_ID_1, SERVER_ID_1, List.of(option)));
    }

    @Test
    @RequiresFlagsEnabled(com.android.org.conscrypt.flags.Flags.FLAG_SPAKE2PLUS_API)
    public void testBuilder_spake2PlusPrerelease_w0WithRegistrationRecord() {
        PakeOption option = createOption("SPAKE2PLUS_PRERELEASE", "w0", "registration_record");
        PakeServerKeyManagerParameters params =
                new PakeServerKeyManagerParameters.Builder()
                        .setOptions(CLIENT_ID_1, SERVER_ID_1, List.of(option))
                        .build();
        PakeServerKeyManagerParameters.Link link =
                new PakeServerKeyManagerParameters.Link(CLIENT_ID_1, SERVER_ID_1);
        assertEquals(option, params.getOptions(link).get(0));
    }

    private static PakeOption createOption(String algorithm, String... keys) {
        PakeOption.Builder builder = new PakeOption.Builder(algorithm);
        for (String key : keys) {
            builder.addMessageComponent(key, PASSWORD_BYTES);
        }
        return builder.build();
    }
}
