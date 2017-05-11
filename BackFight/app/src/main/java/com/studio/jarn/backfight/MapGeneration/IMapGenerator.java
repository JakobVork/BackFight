package com.studio.jarn.backfight.MapGeneration;

import com.studio.jarn.backfight.Tile;

public interface IMapGenerator {
    Tile[][] generateMap(int sGridSize);
}
