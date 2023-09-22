package com.kalimero2.team.bedrockredirector;

import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.*;
import org.cloudburstmc.protocol.common.PacketSignal;

@SuppressWarnings("deprecation")
public class PacketHandler implements BedrockPacketHandler {

    private final BedrockServerSession session;
    private final String transferAddress;
    private final int transferPort;

    public PacketHandler(BedrockServerSession session, String transferAddress, int transferPort) {
        this.session = session;
        this.transferAddress = transferAddress;
        this.transferPort = transferPort;
    }

    @Override
    public PacketSignal handle(RequestNetworkSettingsPacket packet) {
        NetworkSettingsPacket networkSettingsPacket = new NetworkSettingsPacket();
        networkSettingsPacket.setCompressionThreshold(0);
        networkSettingsPacket.setCompressionAlgorithm(PacketCompressionAlgorithm.ZLIB);

        session.sendPacketImmediately(networkSettingsPacket);
        session.setCompression(PacketCompressionAlgorithm.ZLIB);
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LoginPacket packet) {

        SetEntityMotionPacket motion = new SetEntityMotionPacket();
        motion.setRuntimeEntityId(1);
        motion.setMotion(Vector3f.ZERO);
        session.sendPacket(motion);


        PlayStatusPacket status = new PlayStatusPacket();
        status.setStatus(PlayStatusPacket.Status.LOGIN_SUCCESS);
        session.sendPacket(status);


        ResourcePacksInfoPacket resourcePacksInfo = new ResourcePacksInfoPacket();
        resourcePacksInfo.setForcedToAccept(false);
        resourcePacksInfo.setScriptingEnabled(false);
        session.sendPacket(resourcePacksInfo);

        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(SetLocalPlayerAsInitializedPacket packet) {
        movePlayer();

        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(RequestChunkRadiusPacket packet) {
        ChunkRadiusUpdatedPacket chunkRadiusUpdatePacket = new ChunkRadiusUpdatedPacket();
        chunkRadiusUpdatePacket.setRadius(packet.getRadius());
        session.sendPacketImmediately(chunkRadiusUpdatePacket);

        PlayStatusPacket playStatus = new PlayStatusPacket();
        playStatus.setStatus(PlayStatusPacket.Status.PLAYER_SPAWN);
        session.sendPacket(playStatus);
        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(ResourcePackClientResponsePacket packet) {
        switch (packet.getStatus()) {
            case COMPLETED:
                new Player(session);
                break;
            case HAVE_ALL_PACKS:
                ResourcePackStackPacket rs = new ResourcePackStackPacket();
                //rs.setExperimental(false);
                rs.setForcedToAccept(false);
                rs.setGameVersion("*");
                session.sendPacket(rs);
                break;
            default:
                session.disconnect("disconnectionScreen.internalError.cantConnect");
                break;
        }

        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerActionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnimatePacket packet) {
        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(ModalFormResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MovePlayerPacket packet) {
        movePlayer();
        return PacketSignal.HANDLED;
    }

    private void movePlayer() {
        TransferPacket transferPacket = new TransferPacket();
        transferPacket.setAddress(transferAddress);
        transferPacket.setPort(transferPort);
        session.sendPacket(transferPacket);
    }

    // Handle rest of packets to avoid log warnings

    @Override
    public PacketSignal handle(AdventureSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }


    @Override
    public PacketSignal handle(BlockEntityDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BlockPickRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BookEditPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheBlobStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheMissResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientCacheStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientToServerHandshakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandBlockUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerClosePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CraftingEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EntityEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EntityPickRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InteractPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryContentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventorySlotPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(InventoryTransactionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemFrameDropItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LabTablePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LecternUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelEventGenericPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MapInfoRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobArmorEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobEquipmentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityAbsolutePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PhotoTransferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerHotbarPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerInputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerSkinPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PurchaseReceiptPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RiderJumpPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerSettingsRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDefaultGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetPlayerGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SubClientLoginPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TextPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddBehaviorTreePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddHangingEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddItemEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPaintingPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AddPlayerPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AvailableCommandsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BlockEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BossEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CameraPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ChangeDimensionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ChunkRadiusUpdatedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ClientboundMapItemDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CommandOutputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerOpenPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ContainerSetDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CraftingDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ExplodePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelChunkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(GameRulesChangedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(GuiDataPickItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(HurtArmorPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AutomationClientConnectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MapCreateLockedCopyPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MobEffectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ModalFormRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MoveEntityDeltaPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NpcRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(OnScreenTextureAnimationPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlaySoundPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayStatusPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RemoveObjectivePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackChunkDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackDataInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePacksInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ResourcePackStackPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RespawnPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ScriptCustomEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerSettingsResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ServerToClientHandshakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetCommandsEnabledPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDifficultyPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetDisplayObjectivePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityDataPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityLinkPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetEntityMotionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetHealthPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetLastHurtByPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetScoreboardIdentityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetScorePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetSpawnPositionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetTimePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SetTitlePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowCreditsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowProfilePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ShowStoreOfferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SimpleEventPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SpawnExperienceOrbPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StartGamePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StopSoundPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureBlockUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureTemplateDataRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(StructureTemplateDataResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TakeItemEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TransferPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateAttributesPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockPropertiesPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateBlockSyncedPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateEquipPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateSoftEnumPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdateTradePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AvailableEntityIdentifiersPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(BiomeDefinitionListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEvent2Packet packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NetworkChunkPublisherUpdatePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SpawnParticleEffectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(VideoStreamConnectPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EmotePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(TickSyncPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnvilDamagePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(NetworkSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerAuthInputPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(SettingsCommandPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EducationSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CompletedUsingItemPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MultiplayerSettingsPacket packet) {
        return PacketSignal.HANDLED;
    }

    // 1.16 new packets

    @Override
    public PacketSignal handle(DebugInfoPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(EmoteListPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CodeBuilderPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CreativeContentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(LevelSoundEvent1Packet packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemStackResponsePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerArmorDamagePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerEnchantOptionsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(UpdatePlayerGameTypePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PacketViolationWarningPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PositionTrackingDBClientRequestPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PositionTrackingDBServerBroadcastPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(MotionPredictionHintsPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(AnimateEntityPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CameraShakePacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(PlayerFogPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(CorrectPlayerMovePredictionPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(ItemComponentPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(FilterTextPacket packet) {
        return PacketSignal.HANDLED;
    }

    @Override
    public PacketSignal handle(RequestAbilityPacket packet) {
        return PacketSignal.HANDLED;
    }
}

