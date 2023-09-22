package com.kalimero2.team.bedrockredirector;

import io.netty.buffer.Unpooled;
import org.cloudburstmc.math.vector.Vector2f;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.AttributeData;
import org.cloudburstmc.protocol.bedrock.data.AuthoritativeMovementMode;
import org.cloudburstmc.protocol.bedrock.data.ChatRestrictionLevel;
import org.cloudburstmc.protocol.bedrock.data.GamePublishSetting;
import org.cloudburstmc.protocol.bedrock.data.GameRuleData;
import org.cloudburstmc.protocol.bedrock.data.GameType;
import org.cloudburstmc.protocol.bedrock.data.PlayerPermission;
import org.cloudburstmc.protocol.bedrock.data.SpawnBiomeType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.packet.AvailableEntityIdentifiersPacket;
import org.cloudburstmc.protocol.bedrock.packet.BiomeDefinitionListPacket;
import org.cloudburstmc.protocol.bedrock.packet.CreativeContentPacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelChunkPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayStatusPacket;
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateAttributesPacket;
import org.cloudburstmc.protocol.common.util.OptionalBoolean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Based on https://github.com/Pugmatt/BedrockConnect/blob/master/serverlist-server/src/main/com/pyratron/pugmatt/bedrockconnect/BCPlayer.java
// Author: Pugmatt

public class Player {

    private final BedrockServerSession session;

    private static final NbtMap EMPTY_TAG = NbtMap.EMPTY;
    private static final byte[] EMPTY_LEVEL_CHUNK_DATA;

    static {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            outputStream.write(new byte[258]); // Biomes + Border Size + Extra Data Size

            try (NBTOutputStream stream = NbtUtils.createNetworkWriter(outputStream)) {
                stream.writeTag(EMPTY_TAG);
            }

            EMPTY_LEVEL_CHUNK_DATA = outputStream.toByteArray();
        }catch (IOException e) {
            throw new AssertionError("Unable to generate empty level chunk data");
        }
    }


    public Player(BedrockServerSession session) {
        this.session = session;

        joinGame();
    }

    public void joinGame() {
        StartGamePacket startGamePacket = new StartGamePacket();
        startGamePacket.setUniqueEntityId(1);
        startGamePacket.setRuntimeEntityId(1);
        startGamePacket.setPlayerGameType(GameType.SURVIVAL);
        startGamePacket.setPlayerPosition(Vector3f.from(0, 0, 0));
        startGamePacket.setRotation(Vector2f.from(1, 1));

        startGamePacket.setSeed(-1);
        startGamePacket.setDimensionId(0);
        startGamePacket.setGeneratorId(1);
        startGamePacket.setLevelGameType(GameType.SURVIVAL);
        startGamePacket.setDifficulty(1);
        startGamePacket.setDefaultSpawn(Vector3i.ZERO);
        startGamePacket.setAchievementsDisabled(false);
        startGamePacket.setCurrentTick(-1);
        //startGamePacket.setUnknownInt0(-1);
        //startGamePacket.setUnknownInt1(-1);
        startGamePacket.setEduEditionOffers(0);
        startGamePacket.setEduFeaturesEnabled(false);
        startGamePacket.setRainLevel(0);
        startGamePacket.setLightningLevel(0);
        //startGamePacket.setPlatformLockedContentConfirmed(false);
        startGamePacket.setMultiplayerGame(true);
        startGamePacket.setBroadcastingToLan(true);
        startGamePacket.getGamerules().add((new GameRuleData<>("showcoordinates", false)));
        startGamePacket.setPlatformBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setXblBroadcastMode(GamePublishSetting.PUBLIC);
        startGamePacket.setCommandsEnabled(true);
        startGamePacket.setTexturePacksRequired(false);
        startGamePacket.setBonusChestEnabled(false);
        startGamePacket.setStartingWithMap(false);
        startGamePacket.setTrustingPlayers(true);
        startGamePacket.setDefaultPlayerPermission(PlayerPermission.MEMBER);
        startGamePacket.setServerChunkTickRange(4);
        startGamePacket.setBehaviorPackLocked(false);
        startGamePacket.setResourcePackLocked(false);
        startGamePacket.setFromLockedWorldTemplate(false);
        startGamePacket.setUsingMsaGamertagsOnly(false);
        startGamePacket.setFromWorldTemplate(false);
        startGamePacket.setWorldTemplateOptionLocked(false);
        startGamePacket.setSpawnBiomeType(SpawnBiomeType.DEFAULT);
        startGamePacket.setCustomBiomeName("");
        startGamePacket.setEducationProductionId("");
        startGamePacket.setForceExperimentalGameplay(OptionalBoolean.empty());

        startGamePacket.setAuthoritativeMovementMode(AuthoritativeMovementMode.CLIENT);
        startGamePacket.setRewindHistorySize(0);
        startGamePacket.setServerAuthoritativeBlockBreaking(false);
        //startGamePacket.setTrial(false);
        startGamePacket.setVanillaVersion("*");
        startGamePacket.setInventoriesServerAuthoritative(true);
        startGamePacket.setServerEngine("");

        startGamePacket.setLevelId("world");
        startGamePacket.setLevelName("world");
        //startGamePacket.setWorldName("world");
        startGamePacket.setPremiumWorldTemplateId("00000000-0000-0000-0000-000000000000");
        startGamePacket.setCurrentTick(0);
        startGamePacket.setEnchantmentSeed(0);
        startGamePacket.setMultiplayerCorrelationId("");
        startGamePacket.setServerEngine("");
        startGamePacket.setPlayerPropertyData(NbtMap.EMPTY);
        startGamePacket.setWorldTemplateId(UUID.randomUUID());

        startGamePacket.setChatRestrictionLevel(ChatRestrictionLevel.NONE);

        startGamePacket.setBlockPalette(Server.paletteManager.CACHED_PALLETE);

        session.sendPacket(startGamePacket);

        // Required to be sent for 1.16.100
        CreativeContentPacket creativeContentPacket = new CreativeContentPacket();
        creativeContentPacket.setContents(new ItemData[0]);
        session.sendPacket(creativeContentPacket);

        spawn();
    }

    public void spawn() {

        Vector3f pos = Vector3f.ZERO;
        int chunkX = pos.getFloorX() >> 4;
        int chunkZ = pos.getFloorX() >> 4;

        for (int x = -3; x < 3; x++) {
            for (int z = -3; z < 3; z++) {
                LevelChunkPacket data2 = new LevelChunkPacket();
                data2.setChunkX(chunkX + x);
                data2.setChunkZ(chunkZ + z);
                data2.setSubChunksLength(0);
                data2.setData(Unpooled.wrappedBuffer(EMPTY_LEVEL_CHUNK_DATA));
                session.sendPacket(data2);
            }
        }

        BiomeDefinitionListPacket biomePacket = new BiomeDefinitionListPacket();
        biomePacket.setDefinitions(PaletteManager.BIOMES);
        session.sendPacket(biomePacket);

        AvailableEntityIdentifiersPacket entityPacket = new AvailableEntityIdentifiersPacket();
        entityPacket.setIdentifiers(PaletteManager.ENTITY_IDENTIFIERS);
        session.sendPacket(entityPacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);

        UpdateAttributesPacket attributesPacket = new UpdateAttributesPacket();
        attributesPacket.setRuntimeEntityId(0);
        List<AttributeData> attributes = new ArrayList<>();
        // Default move speed
        // Bedrock clients move very fast by default until they get an attribute packet correcting the speed
        attributes.add(new AttributeData("minecraft:movement", 0.0f, 1024f, 0.1f, 0.1f));
        attributesPacket.setAttributes(attributes);
        session.sendPacket(attributesPacket);
    }
}
