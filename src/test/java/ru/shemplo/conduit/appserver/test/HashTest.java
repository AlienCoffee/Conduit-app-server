package ru.shemplo.conduit.appserver.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import ru.shemplo.conduit.appserver.utils.Utils;
import ru.shemplo.snowball.stuctures.Pair;

public class HashTest {
    
    @Test
    public void testHash2Simple () {
        assertEquals ((Long) 0L, Utils.hash2 (0L, 0L));
    }
    
    @Test
    public void testHash2Simple2 () {
        assertEquals ((Long) 1L, Utils.hash2 (0L, 1L));
    }
    
    @Test
    public void testHash2Simple3 () {
        assertEquals ((Long) 0x01_00_00_00_01L, Utils.hash2 (1L, 1L));
    }
    
    @Test
    public void testHash2Simple4 () {
        assertEquals ((Long) 0x02_00_00_00_02L, Utils.hash2 (2L, 2L));
    }
    
    @Test
    public void testDehash2Simple () {
        Pair <Long, Long> result = Utils.dehash2 (0x02_00_00_00_02L);
        assertEquals ((Long) 2L, result.F);
        assertEquals ((Long) 2L, result.S);
    }
    
    @Test
    public void testDehash2Simple2 () {
        Pair <Long, Long> result = Utils.dehash2 (0x03_00_00_00_02L);
        assertEquals ((Long) 3L, result.F);
        assertEquals ((Long) 2L, result.S);
    }
    
    @Test
    public void testDehash2Simple3 () {
        Pair <Long, Long> result = Utils.dehash2 (0x03_00_00_00_12L);
        assertEquals ((Long) 3L, result.F);
        assertEquals ((Long) 18L, result.S);
    }
    
    @Test
    public void testDehash2Simple4 () {
        Pair <Long, Long> result = Utils.dehash2 (0x23_00_00_00_12L);
        assertEquals ((Long) 35L, result.F);
        assertEquals ((Long) 18L, result.S);
    }
    
}
