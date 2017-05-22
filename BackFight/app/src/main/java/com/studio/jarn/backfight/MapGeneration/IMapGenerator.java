package com.studio.jarn.backfight.MapGeneration;

import com.studio.jarn.backfight.Gameboard.Tile;

public interface IMapGenerator {
    Tile[][] generateMap(int sGridSize);
}
