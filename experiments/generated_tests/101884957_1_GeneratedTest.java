java
package com.teester.whatsnearby.data.source;

import org.junit.Test;

import static org.junit.Assert.*;

public class QueryOverpassTest {

    @Test
    public void getOverpassUri_accuracyBelow20() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 10.0f);
        assertTrue(uri.contains("around:20.0,1.0,1.0"));
    }

    @Test
    public void getOverpassUri_accuracyAbove100() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 110.0f);
        assertTrue(uri.contains("around:100.0,1.0,1.0"));
    }

    @Test
    public void getOverpassUri_accuracyBetween20And100() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 50.0f);
        assertTrue(uri.contains("around:50.0,1.0,1.0"));
    }

    @Test
    public void getOverpassUri_latitudeAndLongitude() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.5, 2.5, 50.0f);
        assertTrue(uri.contains("around:50.0,1.5,2.5"));
    }

    @Test
    public void getOverpassUri_fullUri() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 50.0f);
        String expectedStart = "https://www.overpass-api.de/api/interpreter?data=[out:json][timeout:25];(";
        String expectedEnd = ");out%20center%20meta%20qt;";
        assertTrue(uri.startsWith(expectedStart));
        assertTrue(uri.endsWith(expectedEnd));
    }

    @Test
    public void getOverpassUri_typesIncluded() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 50.0f);
        assertTrue(uri.contains("shop|amenity|leisure|tourism"));
    }

    @Test
    public void getOverpassUri_nodeWayRelation() {
        QueryOverpass queryOverpass = new QueryOverpass();
        String uri = queryOverpass.getOverpassUri(1.0, 1.0, 50.0f);
        assertTrue(uri.contains("node[~\"^(shop|amenity|leisure|tourism)$\"~\".\"](around:50.0,1.0,1.0);"));
        assertTrue(uri.contains("way[~\"^(shop|amenity|leisure|tourism)$\"~\".\"](around:50.0,1.0,1.0);"));
        assertTrue(uri.contains("relation[~\"^(shop|amenity|leisure|tourism)$\"~\".\"](around:50.0,1.0,1.0);"));
    }
}