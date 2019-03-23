package com.ericc.the.game.systems.realtime;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ericc.the.game.Mappers;
import com.ericc.the.game.Media;
import com.ericc.the.game.TileTextureIndicator;
import com.ericc.the.game.components.FieldOfViewComponent;
import com.ericc.the.game.components.PositionComponent;
import com.ericc.the.game.components.ScreenBoundariesComponent;
import com.ericc.the.game.components.SpriteSheetComponent;
import com.ericc.the.game.entities.Player;
import com.ericc.the.game.entities.Screen;
import com.ericc.the.game.map.Map;
import com.ericc.the.game.shaders.GrayscaleShader;

import java.util.ArrayList;

/**
 * The system responsible for drawing the map and the entities on the screen.
 * It is desirable to concentrate and contain graphics-related code here.
 */
public class RenderSystem extends EntitySystem {
    private Map map;
    private Viewport viewport;
    private ScreenBoundariesComponent visibleMapArea;
    private final Affine2 transform = new Affine2();

    private SpriteBatch batch = new SpriteBatch();
    private SpriteBatch tilesSeen = new SpriteBatch();
    private ImmutableArray<Entity> entities; // Renderable entities
    private FieldOfViewComponent playersFieldOfView;

    public RenderSystem(Map map, Viewport viewport, FieldOfViewComponent playersFieldOfView, Screen screen) {
        super(9999); // Rendering should be the last system in effect
        this.map = map;
        this.viewport = viewport;
        this.playersFieldOfView = playersFieldOfView;
        this.visibleMapArea = Mappers.screenBoundaries.get(screen);
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, SpriteSheetComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Gdx.gl.glClearColor(.145f, .075f, .102f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        initBatch(tilesSeen);
        tilesSeen.setShader(GrayscaleShader.grayscaleShader);
        for (int y = visibleMapArea.top; y >= visibleMapArea.bottom; --y) {
            for (int x = visibleMapArea.left; x <= visibleMapArea.right; ++x) {
                if (map.hasBeenSeenByPlayer(x, y)) {
                    drawTile(tilesSeen, x, y, true);
                }
            }
        }
        tilesSeen.end();

        initBatch(batch);

        /*
        If we could access entities standing on a given position,
        there would be no need to perform the culling and sorting.
        If such caching is implemented at some point,
        rendering should be redone to take advantage of it
         */

        // Get a list of visible entities
        ArrayList<Entity> visibleEntities = new ArrayList<>();
        final int margin = 5; // Assume that no sprite is more than 5 tiles away from it's logical position
        for (Entity entity : entities) {
            PositionComponent pos = Mappers.position.get(entity);
            if (visibleMapArea.left - margin <= pos.x
                    && pos.x <= visibleMapArea.right + margin
                    && visibleMapArea.bottom - margin <= pos.y
                    && pos.y <= visibleMapArea.top + margin
                    && playersFieldOfView.visibility.get(pos.x, pos.y)) {
                visibleEntities.add(entity);
            }
        }

        /*
        Depth-order the entities.
        The ordering here is based on the logical position
        This might need to change in the future
        */
        visibleEntities.sort((a, b) -> Mappers.position.get(b).y - Mappers.position.get(a).y);

        /*
        Perform the drawing.
         */
        int entityIndex = 0;
        for (int y = visibleMapArea.top; y >= visibleMapArea.bottom; --y) {
            for (int x = visibleMapArea.left; x <= visibleMapArea.right; ++x) {
                if (playersFieldOfView.visibility.get(x, y)) {
                    drawTile(batch, x, y, false);
                }
            }
            while (entityIndex < visibleEntities.size() && Mappers.position.get(visibleEntities.get(entityIndex)).y >= y) {
                drawEntity(visibleEntities.get(entityIndex));
                ++entityIndex;
            }
        }
        while (entityIndex < visibleEntities.size()) {
            drawEntity(visibleEntities.get(entityIndex));
            ++entityIndex;
        }

        batch.end();
    }

    private void drawEntity(Entity entity) {
        PositionComponent pos = Mappers.position.get(entity);
        SpriteSheetComponent render = Mappers.spriteSheet.get(entity);

        transform.idt();
        transform.translate(-render.sprite.getOriginX(), -render.sprite.getOriginY()); // From bottom-left-corner space to origin space.
        transform.mul(render.transform); // Apply affine animations.
        transform.translate(pos.x, pos.y); // Move to logical position.

        batch.draw(render.sprite, render.sprite.getWidth(), render.sprite.getWidth(), transform);
    }

    private void drawTile(SpriteBatch batch, int x, int y, boolean isStatic) {

        /*
        The nine-digit tile code describes the neighbourhood of the tile
        1 - passable / floor
        0 - impassable / wall / void / out of map bounds

        100
        011 -> 001 011 100
        001
         */
        int code = encodeTile(x, y);

        /*
        This complicated algorithm attempts to paint convincing walls with a tileset representing (half-tile)-wide
        walls
        Assumptions:
        - every texture used is square
        - corner and side-wall textures are half-width and are padded with "void" to the side on which they
        should appear on the tile: left wall is aligned to right side, etc
        */

        if (code == 0) {
            // Void
            return;
        }

        if ((code & 0b000010000) != 0) {
            // Floor tile.
            batch.draw(Media.getRandomFloorTile(
                    x, y, map.getRandomNumber(x, y, TileTextureIndicator.FLOOR.getValue()), isStatic
                    ), x, y, 1, 1);

            // Drawing decorations on the floor.
            int clutterType = map.getRandomClutter(x, y, TileTextureIndicator.FLOOR.getValue());

            if (clutterType < Media.clutter.size) {
                batch.draw(Media.clutter.get(clutterType),
                        x, y, 1, 1);
            }
            return;
        }

        if ((code & 0b000000010) != 0) {
            // A down-facing wall.
            batch.draw(Media.wallDown.get(map.getRandomNumber(x, y, TileTextureIndicator.DOWN.getValue())),
                    x, y + 0.5f, 1, 1);
        } else {
            // Maybe down-facing corners?
            if ((code & 0b000100010) == 0 && (code & 0b000000100) != 0) {
                draw(batch, Media.wallRD, x, y + 0.5f, 0.5f, 1, 0, 1, 0.5f, 0);
            }
            if ((code & 0b000001010) == 0 && (code & 0b000000001) != 0) {
                draw(batch, Media.wallLD, x + 0.5f, y + 0.5f, 0.5f, 1, 0.5f, 1, 1, 0);
            }
        }

        // Symmetrical (down-up) to the code above.
        if ((code & 0b010000000) != 0) {
            batch.draw(Media.wallUp.get(map.getRandomNumber(x, y, TileTextureIndicator.UP.getValue())),
                    x, y, 1, 1);
        } else {
            if ((code & 0b010100000) == 0 && (code & 0b100000000) != 0) {
                draw(batch, Media.wallRU, x, y, 0.5f, 1, 0, 1, 0.5f, 0);
            }
            if ((code & 0b010001000) == 0 && (code & 0b001000000) != 0) {
                draw(batch, Media.wallLU, x + 0.5f, y, 0.5f, 1, 0.5f, 1, 1, 0);
            }
        }

        if ((code & 0b010100010) == 0b000100000 || (code & 0b010101010) == 0b010100010) {
            // (A proper right-facing wall) || (the ending of a horizontal double-wall - special case for aesthetics)
            draw(batch, Media.wallRight.get(map.getRandomNumber(x, y, TileTextureIndicator.RIGHT.getValue())),
                    x, y + 0.5f, 0.5f, 1, 0, 0, 0.5f, 1);
        } else if ((code & 0b010100010) == 0b000100010) {
            // A right-and-up-facing wall. The up-facing part is already drawn. It looks better if the up-facing
            // part is drawn on top, so let's "draw the left part underneath". This meaning, we will only draw
            // the lower half of the left-facing wall
            draw(batch, Media.wallRight.get(map.getRandomNumber(x, y, TileTextureIndicator.RIGHT.getValue())),
                    x, y + 0.5f, 0.5f, 0.5f, 0, 0.5f, 0.5f, 1);
        } else if ((code & 0b010100010) == 0b010100000) {
            // A right-and-down facing wall. See above.
            draw(batch, Media.wallRight.get(map.getRandomNumber(x, y, TileTextureIndicator.RIGHT.getValue())),
                    x, y + 0.5f + 0.5f, 0.5f, 0.5f, 0, 0, 0.5f, 0.5f);
        }

        // Symmetrical (right-left) to code above
        if ((code & 0b010001010) == 0b000001000 || (code & 0b010101010) == 0b010001010) {
            draw(batch, Media.wallLeft.get(map.getRandomNumber(x, y, TileTextureIndicator.LEFT.getValue())),
                    x + 0.5f, y + 0.5f, 0.5f, 1, 0.5f, 0, 1, 1);
        } else if ((code & 0b010001010) == 0b000001010) {
            draw(batch, Media.wallLeft.get(map.getRandomNumber(x, y, TileTextureIndicator.LEFT.getValue())),
                    x + 0.5f, y + 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 1, 1);
        } else if ((code & 0b010001010) == 0b010001000) {
            draw(batch, Media.wallLeft.get(map.getRandomNumber(x, y, TileTextureIndicator.LEFT.getValue())),
                    x + 0.5f, y + 0.5f + 0.5f, 0.5f, 0.5f, 0.5f, 0, 1, 0.5f);
        }

        // Drawing decorations on the upper walls
        if ((code & 0b010000000) != 0) {
            int clutterType = map.getRandomClutter(x, y, TileTextureIndicator.UP.getValue());

            if (clutterType < Media.wallClutter.size) {
                batch.draw(Media.wallClutter.get(clutterType),
                        x, y, 1, 1);
            }
        }
    }

    private int encodeTile(int x, int y) {
        int ans = 0;
        int cnt = 8;
        for (int j = y - 1; j <= y + 1; ++j) {
            for (int i = x - 1; i <= x + 1; ++i) {
                ans |= (map.isPassable(i, j) ? 1 : 0) << cnt;
                --cnt;
            }
        }
        return ans;
    }

    /**
     * Draw a part of a texture region.
     * Similar to SpriteBatch.draw(texture, x, y, w, h, u, v, u2, v2), except
     * u, v, u2, v2 describe a fraction of the given region,
     * not a fraction of the entire texture
     */
    private void draw(SpriteBatch batch, TextureRegion t,
                      float x, float y, float width, float height,
                      float u, float v, float u2, float v2) {
        u = t.getU2() * u + t.getU() * (1 - u);
        u2 = t.getU2() * u2 + t.getU() * (1 - u2);
        v = t.getV2() * v + t.getV() * (1 - v);
        v2 = t.getV2() * v2 + t.getV() * (1 - v2);
        batch.draw(t.getTexture(), x, y, width, height, u, v, u2, v2);
    }

    /**
     * Custom function that does the sprite batch initialisation.
     * @param batch a sprite batch to be initialised
     */
    private void initBatch(SpriteBatch batch) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        batch.begin();
    }
}
