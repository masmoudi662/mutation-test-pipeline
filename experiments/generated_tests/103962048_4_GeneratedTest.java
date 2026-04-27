java
package ru.itpark.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.itpark.models.Car;
import ru.itpark.models.Human;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HumansJdbcTemplateDaoImplTest {

    private HumansJdbcTemplateDaoImpl humansDao;
    private EmbeddedDatabase dataSource;

    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("schema.sql")
                .addScript("data.sql")
                .build();
        humansDao = new HumansJdbcTemplateDaoImpl(new JdbcTemplate(dataSource));
    }

    @Test
    void find() {
        Human human = humansDao.find(1);
        assertEquals(1, human.getId());
        assertEquals("Marsel", human.getName());
    }

    @Test
    void find_not_found() {
        assertThrows(IllegalArgumentException.class, () -> humansDao.find(100));
    }

    @Test
    void findAll() {
        List<Human> humans = humansDao.findAll();
        assertEquals(3, humans.size());
    }

    @Test
    void save() {
        Human newHuman = new Human(4, "New Human", 30);
        humansDao.save(newHuman);
        Human retrievedHuman = humansDao.find(4);
        assertEquals("New Human", retrievedHuman.getName());
    }

    @Test
    void update() {
        Human humanToUpdate = new Human(1, "Updated Name", 25);
        humansDao.update(humanToUpdate);
        Human updatedHuman = humansDao.find(1);
        assertEquals("Updated Name", updatedHuman.getName());
        assertEquals(25, updatedHuman.getAge());
    }

    @Test
    void delete() {
        humansDao.delete(1);
        assertThrows(IllegalArgumentException.class, () -> humansDao.find(1));
    }

    @Test
    void findByName() {
        List<Human> humans = humansDao.findByName("Marsel");
        assertEquals(1, humans.size());
        assertEquals(1, humans.get(0).getId());
    }
}