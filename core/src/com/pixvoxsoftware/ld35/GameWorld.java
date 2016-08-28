package com.pixvoxsoftware.ld35;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibrary;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeLibraryManager;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.pixvoxsoftware.ld35.controllers.EntityController;
import com.pixvoxsoftware.ld35.controllers.PlayerController;
import com.pixvoxsoftware.ld35.entities.*;

import java.util.ArrayList;
import java.util.Iterator;

public class GameWorld {

    private Player player;
    private ArrayList<Entity> entities = new ArrayList<>();
    private TiledMap map;
    private Vector2 gravity = new Vector2(0, -10);
    private float accumulator;
    public World physicsWorld;
    public RayHandler rayHandler = null;

    public GameWorld(String mapName) {
        physicsWorld = new World(gravity, true);

        rayHandler = new RayHandler(new World(gravity, true));
        rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 1f);

        map = new TmxMapLoader().load(mapName);

        for (MapObject mapObject : map.getLayers().get("Physics").getObjects()) {
            RectangleMapObject platformObject = (RectangleMapObject) mapObject;
            BodyDef platformBodyDef = new BodyDef();
            Vector2 position = platformObject.getRectangle().getCenter(new Vector2(0, 0)).scl(1f / WorldConstants.PIXELS_PER_METER);
            platformBodyDef.position.set(position);
            Body platformBody = physicsWorld.createBody(platformBodyDef);
            PolygonShape platformBox = new PolygonShape();
            platformBox.setAsBox(
                    platformObject.getRectangle().getWidth() / 2f / WorldConstants.PIXELS_PER_METER,
                    platformObject.getRectangle().getHeight() / 2f / WorldConstants.PIXELS_PER_METER
            );
            Loggers.game.debug("physics {}", platformObject.getName());
            platformBody.createFixture(platformBox, 0.0f).setUserData(new Entity() {
                @Override
                public boolean isGround() {
                    return true;
                }

                @Override
                public short getCategory() {
                    return WorldConstants.OBSTACLE_CATEGORY;
                }

                @Override
                public short getCollisionMask() {
                    return WorldConstants.ANY_CATEGORY;
                }

                @Override
                public int renderPass() {
                    return 0;
                }

                @Override
                public String getName() {
                    return null;
                }
            });
            platformBox.dispose();
        }

        RectangleMapObject spawnEntity = (RectangleMapObject) map.getLayers().get("Entities").getObjects().get("Spawn");

        player = new Player(this, spawnEntity.getRectangle().getX() / WorldConstants.PIXELS_PER_METER, spawnEntity.getRectangle().getY() / WorldConstants.PIXELS_PER_METER);
        addEntity(player);

        physicsWorld.setContactListener(new GroundCheckContactListener());

        //AI

        BehaviorTreeLibraryManager libraryManager = BehaviorTreeLibraryManager.getInstance();
        libraryManager.setLibrary(new BehaviorTreeLibrary(BehaviorTreeParser.DEBUG_HIGH));

        // Load entities
        for (MapObject mapObject : map.getLayers().get("Entities").getObjects()) {
                if (mapObject instanceof TiledMapTileMapObject) {
                    TiledMapTileMapObject tileMapObject = (TiledMapTileMapObject) mapObject;
//                    if (tileMapObject.getName().equals("Lamp")) {
//                        addEntity(new Lamp(this, tileMapObject.getTextureRegion(), 16 / WorldConstants.PIXELS_PER_METER, 16 / WorldConstants.PIXELS_PER_METER, tileMapObject.getX() / WorldConstants.PIXELS_PER_METER, tileMapObject.getY() / WorldConstants.PIXELS_PER_METER));
//                    } else if (tileMapObject.getName().equals("Torch")) {
//                        AnimatedSprite spriteOn = new AnimatedSprite(Gdx.files.internal("torch.png"), 8, 0.06f);
//                        Sprite spriteOff = new Sprite(torchOffTexture);
//                        addEntity(new Lamp(this, spriteOn, spriteOff, spriteOn.getWidth() / 2, 5 * spriteOn.getHeight() / 12, tileMapObject.getX() / WorldConstants.PIXELS_PER_METER, tileMapObject.getY() / WorldConstants.PIXELS_PER_METER));
//                    } else {
//                        addEntity(new Box(this, tileMapObject.getTile().getTextureRegion(),
//                                tileMapObject.getX() / WorldConstants.PIXELS_PER_METER, tileMapObject.getY() / WorldConstants.PIXELS_PER_METER));
//                    }
                } else if (mapObject instanceof RectangleMapObject) {
//                    RectangleMapObject rectangleMapObject = (RectangleMapObject) mapObject;
//                    if(rectangleMapObject.getName().equals("GuardSpawn")) {
//
//                        Guard guard = new Guard(this, rectangleMapObject.getRectangle().getX() / WorldConstants.PIXELS_PER_METER,
//                                rectangleMapObject.getRectangle().getY() / WorldConstants.PIXELS_PER_METER,
//                                Integer.valueOf((String)rectangleMapObject.getProperties().get("stepsLeft")),
//                                Integer.valueOf((String)rectangleMapObject.getProperties().get("stepsRight")), 4f);
//
//                        BehaviorTree<Guard> tree = libraryManager.createBehaviorTree("tasks/guard.tree", guard);
//                        tree.setObject(guard);
//                        guard.setController(new GuardController(tree));
//                        addEntity(guard);
//                    } else if (rectangleMapObject.getName().equals("MerlinSpawn")) {
//                        Rectangle rectangle = rectangleMapObject.getRectangle();
//                        Loggers.game.debug("merlin found at {} {}", rectangle.getX() / WorldConstants.PIXELS_PER_METER, rectangle.getY() / WorldConstants.PIXELS_PER_METER);
//                        merlin = new Merlin(this, rectangle.getX() / WorldConstants.PIXELS_PER_METER, rectangle.getY() / WorldConstants.PIXELS_PER_METER);
//                        addEntity(merlin);
//                    }
                } else {
                    Loggers.game.debug("unknown tile: {}", mapObject.getName());
                }
        }

        Loggers.game.debug("game world initialized");
    }


    public void act() {
        float frameTime = Math.min(Gdx.graphics.getDeltaTime(), 0.25f);
        accumulator += frameTime;
        while (accumulator >= 1 / 60f) {
            physicsWorld.step(1 / 60f, 6, 2);
            accumulator -= 1 / 60f;
        }

        for (Iterator<Entity> iterator = entities.iterator(); iterator.hasNext();) {
            Entity entity = iterator.next();
            // if entity is killed, remove it
            if (entity.isKilled()) {
                iterator.remove();
            } else {
                EntityController controller = entity.getController();
                if (controller != null) {
                    controller.act(entity);
                }
            }
        }
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public TiledMap getMap() {
        return map;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean onKeyPressed(int keycode) {
        for (Entity entity : entities) {
            EntityController controller = entity.getController();
            if (controller != null && controller.onKeyPressed(entity, keycode)) {
                return true;
            }
        }
        return false;
    }

    public boolean onKeyReleased(int keycode) {
        for (Entity entity : entities) {
            EntityController controller = entity.getController();
            if (controller != null && controller.onKeyReleased(entity, keycode)) {
                return true;
            }
        }
        return false;
    }

    public boolean touchDown(float worldX, float worldY, int pointer, int button) {
        PlayerController playerController = (PlayerController) player.getController();
        return playerController.onTouchDown(player, worldX, worldY, pointer, button);
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public Vector2 getGravity() {
        return gravity;
    }

    public String[] getDebugStrings() {
        return new String[] {
                "consumed soul: " + Boolean.toString(player.getConsumedSoul() != null),
                "player x: " + Float.toString(player.getSprite().getX()),
                "player y: " + Float.toString(player.getSprite().getY()),
//                "player velocity x: " + Float.toString(player.physicsBody.getLinearVelocity().x),
//                "player velocity y: " + Float.toString(player.physicsBody.getLinearVelocity().y),
                "player grounded: " + Boolean.toString(player.isOnGround()),
                "player direction: " + player.getDirection(),
                "player state: " + player.getState().toString(),
                "player alive: " + !player.isKilled(),
        };
    }
    public float getHeight() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        return layer.getHeight()*layer.getTileHeight();
    }

    public float getWidth() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        return layer.getWidth()*layer.getTileWidth();
    }

    public ArrayList<Entity> getEntitiesInArea(float x, float y, float radius) {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity entity : this.entities) {
            if (Math.hypot(entity.getSprite().getX() - x, entity.getSprite().getY() - y) <= radius) {
                entities.add(entity);
            }
        }
        return entities;
    }
}
