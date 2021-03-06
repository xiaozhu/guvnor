/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.security.Capabilities;

import java.util.Map;

/**
 * Storage for global prefs.
 * Preferences effect behaviour and display.
 */
public class Preferences {

    static final Preferences    INSTANCE = new Preferences();
    private Map<String, String> prefs;

    private Preferences() {
    }

    void loadPrefs(Capabilities caps) {
        this.prefs = caps.prefs;
    }

    public static boolean getBooleanPref(String name) {
        if ( INSTANCE.prefs.containsKey( name ) ) {
            return Boolean.parseBoolean( INSTANCE.prefs.get( name ) );
        } else {
            return false;
        }
    }

    public static String getStringPref(String name) {
        return INSTANCE.prefs.get( name );
    }

}
