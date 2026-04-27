java
package net.dahanne.gallery3.client.utils;

import net.dahanne.gallery.commons.model.Album;
import net.dahanne.gallery3.client.model.Entity;
import net.dahanne.gallery3.client.model.Item;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class G3ConvertUtilsTest {

    @Test
    public void testItemToAlbum_nullItem() {
        assertNull(G3ConvertUtils.itemToAlbum(null));
    }

    @Test
    public void testItemToAlbum_nullEntity() {
        Item item = new Item();
        item.setEntity(null);
        assertNull(G3ConvertUtils.itemToAlbum(item));
    }

    @Test
    public void testItemToAlbum_validItem() {
        Item item = new Item();
        Entity entity = new Entity();
        entity.setId("123");
        entity.setTitle("Test Album");
        entity.setDescription("Album description");
        entity.setWebUrl("http://example.com/album");
        entity.setThumbUrl("http://example.com/album/thumb");
        item.setEntity(entity);

        Album album = G3ConvertUtils.itemToAlbum(item);

        assertEquals("123", album.getId());
        assertEquals("123", album.getName());
        assertEquals("Test Album", album.getTitle());
        assertEquals("Album description", album.getSummary());
        assertEquals("http://example.com/album", album.getAlbumUrl());
        assertEquals("http://example.com/album/thumb", album.getAlbumCoverUrl());
    }

    @Test
    public void testItemToAlbum_emptyItem() {
        Item item = new Item();
        Entity entity = new Entity();
        item.setEntity(entity);
        Album album = G3ConvertUtils.itemToAlbum(item);
        assertEquals(entity.getId(), album.getId());
    }
}