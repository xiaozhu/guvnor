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

package org.drools.guvnor.server;

import org.drools.guvnor.client.rpc.PushResponse;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ArrayList;

public class BackchannelTest {

    @Test
    public void testPushAll() throws Exception {

        final Backchannel bc = new Backchannel();

        final PushResponse[] resp = new PushResponse[2];
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    List<PushResponse> r = bc.await("mic");
                    assertEquals(1, r.size());
                    resp[0] = r.get(0);
                } catch (InterruptedException e) {
                    fail("should not interrupt");
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    List<PushResponse> r = bc.await("jazz");
                    assertEquals(1, r.size());
                    resp[1] = r.get(0);

                } catch (InterruptedException e) {
                    fail("should not interrupt");
                }
            }
        });
        t.start();
        t2.start();


        Thread.sleep(200);

        bc.publish(new PushResponse("hey", "ho"));

        //t.join();
        //t2.join();

        Thread.sleep(500);
        assertNotNull(resp[0]);
        assertNotNull(resp[1]);
        assertEquals("hey", resp[0].messageType);
        assertEquals("hey", resp[1].messageType);


    }

    @Test
    public void testSimple() throws Exception {

            final Backchannel bc = new Backchannel();

        bc.push("mic", new PushResponse("m", "b"));
        bc.push("dave", new PushResponse("d", "b"));
        List<PushResponse> r = bc.await("mic");
        assertEquals(1, r.size());
        assertEquals("m", r.get(0).messageType);
        r = bc.await("dave");
        assertEquals(1, r.size());
        assertEquals("d", r.get(0).messageType);


        final boolean[] check = new boolean[1];

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    @SuppressWarnings("unused")
                    List<PushResponse> list = bc.await("mic");
                    /*
                    for (PushResponse resp: list) {
                                       System.err.println(resp.messageType + "," + resp.message);
                    }
                    */
                    check[0] = true;
                } catch (InterruptedException e) {
                    fail("Should not interrupt");
                }
            }
        });

        t.setDaemon(true);
        t.start();


        Thread.sleep(400);

        assertFalse(check[0]);
        bc.push("dave", new PushResponse("x", "y"));
        //bc.push("mic", new PushResponse("Q", "W"));
        t.join();



        

        check[0] = false;
        bc.push("mic", new PushResponse("R", "T"));
        bc.push("mic", new PushResponse("Q", "A"));


        final List<List<PushResponse>> container = new ArrayList<List<PushResponse>>();

        t = new Thread(new Runnable() {
            public void run() {
                try {
                    container.add(bc.await("mic"));
                    check[0] = true;
                } catch (InterruptedException e) {
                    fail("Should not interrupt");
                }
            }
        });
        t.setDaemon(true);
        t.start();
        t.join();

        assertTrue(check[0]);
        assertEquals(1, container.size());
        List<PushResponse> list = container.get(0);
        if (list.size() > 2) {
            for (PushResponse resp: list) {
                System.err.println(resp.messageType + "," + resp.message);
            }
        }
        assertEquals(2, list.size());

    }

    @Test
    public void testManyConcurrent() throws Exception {
        final Backchannel bc = new Backchannel();
        for (int i =0; i < 1000; i++) {
            spinup(bc, i);
        }
        bc.push("mic", new PushResponse("yo", "yo"));
        bc.push("mic", new PushResponse("yo", "yo"));

        //for (int i=0; i< 1000; i++) {
        List<PushResponse> res = bc.await("mic");
        assertEquals(2, res.size());

        res = bc.await("mic");
        assertEquals(0, res.size());
        //assertNull(res); //as other concurrent things will be unlatching...

        Thread.sleep(20);

        for (int i = 0; i < 20000; i++) {
            bc.push("mic", new PushResponse("yo", "yo"));
            bc.push("mic", new PushResponse("yo", "yo"));

            res = bc.await("mic");
            assertEquals(2, res.size());
        }
    }

    private void spinup(final Backchannel bc, final int i) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(i / 2);
                } catch (InterruptedException e) {
                    fail("should not interrupt");
                }
                bc.push(i + "user", new PushResponse(i + "type", "message"));
            }
        });

        t.setDaemon(true);

        t.start();
    }

}
